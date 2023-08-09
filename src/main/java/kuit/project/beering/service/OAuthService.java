package kuit.project.beering.service;

import jakarta.persistence.EntityNotFoundException;
import kuit.project.beering.domain.Member;
import kuit.project.beering.domain.OAuth;
import kuit.project.beering.domain.OAuthType;
import kuit.project.beering.dto.common.OAuthMemberInfo;
import kuit.project.beering.dto.request.auth.OAuthSignupRequest;
import kuit.project.beering.dto.request.member.MemberSignupRequest;
import kuit.project.beering.dto.response.member.MemberLoginResponse;
import kuit.project.beering.redis.RefreshToken;
import kuit.project.beering.repository.MemberRepository;
import kuit.project.beering.repository.OAuthRepository;
import kuit.project.beering.repository.RefreshTokenRepository;
import kuit.project.beering.security.auth.oauth.service.OAuthClientService;
import kuit.project.beering.security.jwt.JwtInfo;
import kuit.project.beering.security.jwt.JwtTokenProviderResolver;
import kuit.project.beering.security.jwt.OAuthTokenInfo;
import kuit.project.beering.security.jwt.jwtTokenProvider.JwtTokenProvider;
import kuit.project.beering.util.exception.SignupNotCompletedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OAuthService {

    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final OAuthRepository oauthRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProviderResolver jwtTokenProviderResolver;

    @Transactional
    public MemberLoginResponse restapiLogin(String code, OAuthClientService oAuthClientService) {
        // 1. "인가 코드"로 "액세스 토큰" 요청
        OAuthTokenInfo oauthTokenInfo = oAuthClientService.createToken(code);

        // 2. token의 sub로 OAuth 조회, 없으면 첫 로그인이므로 회원가입 마저 진행
        return checkAlreadySignup(oauthTokenInfo, oAuthClientService.getOauthType());
    }

    @Transactional
    public MemberLoginResponse sdkLogin(OAuthTokenInfo oauthTokenInfo, OAuthClientService oauthClientService) {
        // token의 sub로 OAuth 조회, 없으면 첫 로그인이므로 회원가입 마저 진행
        return checkAlreadySignup(oauthTokenInfo, oauthClientService.getOauthType());
    }

    @Transactional
    public MemberLoginResponse signup(OAuthSignupRequest request, OAuthClientService oauthClientService) {

        OAuth oauth = oauthRepository.findBySubAndOauthType(request.getSub(), request.getOAuthType())
                .orElseThrow(EntityNotFoundException::new);

        OAuthMemberInfo oauthAccountInfo = oauthClientService.getAccount(oauth.getAccessToken());

        String email = oauthAccountInfo.getEmail();
        String nickname = request.getNickname();

        // 회원가입 처리
        memberService.signup(new MemberSignupRequest(email, UUID.randomUUID().toString(), nickname, request.getAgreements()));
        Member member = memberRepository.findByUsername(email).orElseThrow(EntityNotFoundException::new);
        member.createOauthAssociation(oauth);

        // 토큰 발행
        OAuthTokenInfo oauthTokenInfo = oauthClientService.reissueToken(oauth.getRefreshToken());

        String idToken = oauthTokenInfo.getIdToken();
        String accessToken = oauthTokenInfo.getAccessToken();
        String refreshToken = oauthTokenInfo.getRefreshToken();

        // 값 변경
        oauth.tokenReissue(accessToken, refreshToken);

        refreshTokenRepository.save(new RefreshToken(String.valueOf(member.getId()), refreshToken));

        return MemberLoginResponse.builder()
                .memberId(member.getId())
                .jwtInfo(JwtInfo.builder().accessToken(idToken).refreshToken(refreshToken).build())
                .build();
    }

    private MemberLoginResponse checkAlreadySignup(OAuthTokenInfo oauthTokenInfo, OAuthType oauthType) {
        JwtTokenProvider tokenProvider = jwtTokenProviderResolver.getProvider(oauthTokenInfo.getIdToken());
        String sub = tokenProvider.parseSub(oauthTokenInfo.getIdToken());

        OAuth oauth = oauthRepository.findBySubAndOauthType(sub, oauthType)
                .orElseThrow(() -> {
                    oauthRepository.save(OAuth.createOauth(sub, oauthType, oauthTokenInfo.getAccessToken(), oauthTokenInfo.getRefreshToken()));
                    throw new SignupNotCompletedException(sub, oauthType);
                });

        Member member = memberRepository.findByOauthId(oauth.getId()).orElseThrow(
                () -> new SignupNotCompletedException(sub, oauth.getOauthType()));

        refreshTokenRepository.save(new RefreshToken(String.valueOf(member.getId()), oauthTokenInfo.getRefreshToken()));

        return MemberLoginResponse.builder()
                .memberId(member.getId())
                .jwtInfo(JwtInfo.builder()
                        .accessToken(oauthTokenInfo.getIdToken())
                        .refreshToken(oauthTokenInfo.getRefreshToken())
                        .build())
                .build();
    }

}
