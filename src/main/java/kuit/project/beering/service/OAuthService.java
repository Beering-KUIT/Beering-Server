package kuit.project.beering.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import kuit.project.beering.domain.Member;
import kuit.project.beering.domain.OAuth;
import kuit.project.beering.domain.OAuthType;
import kuit.project.beering.dto.KakaoMemberInfo;
import kuit.project.beering.dto.request.auth.OAuthSignupRequest;
import kuit.project.beering.dto.request.member.MemberSignupRequest;
import kuit.project.beering.dto.response.member.MemberLoginResponse;
import kuit.project.beering.redis.RefreshToken;
import kuit.project.beering.repository.MemberRepository;
import kuit.project.beering.repository.OAuthRepository;
import kuit.project.beering.repository.RefreshTokenRepository;
import kuit.project.beering.security.jwt.JwtInfo;
import kuit.project.beering.security.jwt.JwtTokenProvider;
import kuit.project.beering.security.jwt.OAuthTokenInfo;
import kuit.project.beering.util.exception.SignupNotCompletedException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OAuthService {

    @Value(value = "${oauth2-kakao-restapi-key}")
    private String CLIENT_ID;
    private final MemberService memberService;
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

    @Transactional
    public MemberLoginResponse signup(OAuthSignupRequest request) throws JsonProcessingException {

        OAuth oAuth = oAuthRepository.findBySub(request.getSub())
                .orElseThrow(EntityNotFoundException::new);

        JsonNode kakaoAccountResponseBody = getKakaoAccount(oAuth.getAccessToken());

        String email = kakaoAccountResponseBody.get("kakao_account").get("email").asText();
        String nickname = kakaoAccountResponseBody.get("properties")
                .get("nickname").asText();

        memberService.signup(new MemberSignupRequest(email, "!oauthpassword321", nickname, request.getAgreements()));
        Member member = memberRepository.findByUsername(email).orElseThrow(EntityNotFoundException::new);
        member.createOAuthAssociation(oAuth);

        JsonNode tokenResponseBody = getTokenReissue(oAuth);

        String idToken = tokenResponseBody.get("id_token").asText();
        String accessToken = tokenResponseBody.get("access_token").asText();
        String refreshToken = tokenResponseBody.get("refresh_token").asText();

        oAuth.tokenReissue(accessToken, refreshToken);

        refreshTokenRepository.save(new RefreshToken(String.valueOf(member.getId()), refreshToken));

        return MemberLoginResponse.builder()
                .memberId(member.getId())
                .jwtInfo(JwtInfo.builder().accessToken(idToken).refreshToken(refreshToken).build())
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
        JsonNode jsonNode = getResponseBody(response);
        return OAuthTokenInfo.builder()
                .accessToken(jsonNode.get("access_token").asText())
                .refreshToken(jsonNode.get("refresh_token").asText()).build();

    }

    private KakaoMemberInfo getKakaoUserInfo(String accessToken) throws JsonProcessingException {
        // HTTP Header 생성
        JsonNode kakaoAccountResponseBody = getKakaoAccount(accessToken);

        Long id = kakaoAccountResponseBody.get("id").asLong();
        String email = kakaoAccountResponseBody.get("kakao_account").get("email").asText();
        String nickname = kakaoAccountResponseBody.get("properties")
                .get("nickname").asText();

        return new KakaoMemberInfo(id, email, nickname);
    }

    // 3. 카카오ID로 회원가입 처리
    private Member registerKakaoUserIfNeed(KakaoMemberInfo kakaoMemberInfo, OAuthTokenInfo oAuthTokenInfo) {
        // DB 에 중복된 email이 있는지 확인
        // 없으면 닉네임 및 약관 요청 있으면 강제 로그인..
        Member member = memberRepository.findByUsername(kakaoMemberInfo.getEmail())
                .orElseThrow(() -> {
                    OAuth oAuth = oAuthRepository.save(OAuth.createOAuth(jwtTokenProvider.parseSub(oAuthTokenInfo.getIdToken()),
                            OAuthType.KAKAO, oAuthTokenInfo.getAccessToken(), oAuthTokenInfo.getRefreshToken()));
                    throw new SignupNotCompletedException(oAuth.getSub());
                });

        refreshTokenRepository.save(new RefreshToken(String.valueOf(member.getId()), oAuthTokenInfo.getRefreshToken()));

        return member;
    }

    private JsonNode getKakaoAccount(String accessToken) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();

        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        headers.add(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> kakaoUserInfoRequest = new HttpEntity<>(headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                kakaoUserInfoRequest,
                String.class
        );

        return getResponseBody(response);
    }

    private JsonNode getTokenReissue(OAuth oAuth) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP Body 생성
        // TODO client_secret 고려해보기
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "refresh_token");
        body.add("client_id", CLIENT_ID);
        body.add("refresh_token", oAuth.getRefreshToken());

        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(body, headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> tokenResponse = rt.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );

        return getResponseBody(tokenResponse);
    }

    private JsonNode getResponseBody(ResponseEntity<String> response) throws JsonProcessingException {
        return new ObjectMapper().readTree(response.getBody());
    }
}
