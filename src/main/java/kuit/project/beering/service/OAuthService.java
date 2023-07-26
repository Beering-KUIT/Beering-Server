package kuit.project.beering.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import kuit.project.beering.domain.Member;
import kuit.project.beering.domain.OAuth;
import kuit.project.beering.domain.OAuthType;
import kuit.project.beering.dto.KakaoMemberInfo;
import kuit.project.beering.dto.response.member.MemberLoginResponse;
import kuit.project.beering.redis.RefreshToken;
import kuit.project.beering.repository.MemberRepository;
import kuit.project.beering.repository.OAuthRepository;
import kuit.project.beering.repository.RefreshTokenRepository;
import kuit.project.beering.security.auth.AuthMember;
import kuit.project.beering.security.jwt.JwtInfo;
import kuit.project.beering.security.jwt.JwtTokenProvider;
import kuit.project.beering.security.jwt.OAuthTokenInfo;
import kuit.project.beering.util.exception.LoginNotCompletedException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OAuthService {

    @Value(value = "${oauth2-kakao-restapi-key}")
    private String CLIENT_ID;
    private final MemberRepository memberRepository;
    private final OAuthRepository oAuthRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public MemberLoginResponse kakaoOAuth(String code) throws JsonProcessingException {
        // 1. "인가 코드"로 "액세스 토큰" 요청
        OAuthTokenInfo oAuthTokenInfo = getAccessToken(code);

        // 2. 토큰으로 카카오 API 호출
        KakaoMemberInfo kakaoMemberInfo = getKakaoUserInfo(oAuthTokenInfo.getAccessToken());

        // 3. 카카오ID로 회원가입 처리
        Member kakaoMember = registerKakaoUserIfNeed(kakaoMemberInfo, oAuthTokenInfo);

        return MemberLoginResponse.builder()
                .memberId(kakaoMember.getId())
                .jwtInfo(JwtInfo.builder()
                        .accessToken(oAuthTokenInfo.getIdToken())
                        .refreshToken(oAuthTokenInfo.getRefreshToken())
                        .build())
                .build();
    }

    private OAuthTokenInfo getAccessToken(String code) throws JsonProcessingException {
        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP Body 생성
        // TODO client_secret 고려해보기
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", CLIENT_ID);
        body.add("redirect_uri", "http://localhost:9000/oauth/kakao/callback");
        body.add("code", code);

        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(body, headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );

        // HTTP 응답 (JSON) -> 액세스 토큰 파싱
        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        return OAuthTokenInfo.builder()
                .accessToken(jsonNode.get("access_token").asText())
                .refreshToken(jsonNode.get("refresh_token").asText()).build();

    }

    private KakaoMemberInfo getKakaoUserInfo(String accessToken) throws JsonProcessingException {
        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> kakaoUserInfoRequest = new HttpEntity<>(headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                kakaoUserInfoRequest,
                String.class
        );

        // responseBody에 있는 정보를 꺼냄
        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);

        Long id = jsonNode.get("id").asLong();
        String email = jsonNode.get("kakao_account").get("email").asText();
        String nickname = jsonNode.get("properties")
                .get("nickname").asText();

        return new KakaoMemberInfo(id, email, nickname);
    }

    // 3. 카카오ID로 회원가입 처리
    private Member registerKakaoUserIfNeed(KakaoMemberInfo kakaoMemberInfo, OAuthTokenInfo oAuthTokenInfo) {
        // DB 에 중복된 email이 있는지 확인
        // 없으면 닉네임 및 약관 요청 있으면 강제 로그인..
        // password: random UUID

        Member member = memberRepository.findByUsername(kakaoMemberInfo.getEmail())
                .orElseThrow(() -> {
                    OAuth oAuth = oAuthRepository.save(OAuth.createOAuth(jwtTokenProvider.parseSub(oAuthTokenInfo.getIdToken()),
                            OAuthType.KAKAO, oAuthTokenInfo.getAccessToken(), oAuthTokenInfo.getRefreshToken()));
                    throw new LoginNotCompletedException(oAuth.getSub());
                });

        forceLogin(member, oAuthTokenInfo.getRefreshToken());

        return member;
    }

    // 4. 강제 로그인 처리
    private void forceLogin(Member member, String refreshToken) {

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("MEMBER"));

        UserDetails userDetails = AuthMember.builder()
                .id(member.getId())
                .username(member.getUsername())
                .password(member.getPassword())
                .authorities(authorities)
                .build();

        Authentication authentication =
                new UsernamePasswordAuthenticationToken
                        (userDetails, "", userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        refreshTokenRepository.save(new RefreshToken(String.valueOf(member.getId()), refreshToken));
    }

}
