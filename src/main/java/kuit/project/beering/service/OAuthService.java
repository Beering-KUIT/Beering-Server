package kuit.project.beering.service;

import kuit.project.beering.domain.Member;
import kuit.project.beering.domain.OAuth;
import kuit.project.beering.domain.OAuthType;
import kuit.project.beering.dto.common.OAuthSdkLoginDto;
import kuit.project.beering.dto.common.SignupContinueDto;
import kuit.project.beering.dto.request.member.AgreementRequest;
import kuit.project.beering.dto.request.member.MemberSignupRequest;
import kuit.project.beering.dto.response.member.MemberLoginResponse;
import kuit.project.beering.repository.MemberRepository;
import kuit.project.beering.repository.OAuthRepository;
import kuit.project.beering.security.jwt.JwtInfo;
import kuit.project.beering.security.jwt.OAuthTokenInfo;
import kuit.project.beering.security.jwt.jwtTokenProvider.JwtTokenProvider;
import kuit.project.beering.util.exception.SignupNotCompletedException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OAuthService {

    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final OAuthRepository oauthRepository;
    private final JwtTokenProvider jwtTokenProvider;

//     rest-api 임시 주석처리
//    @Transactional(noRollbackFor = SignupNotCompletedException.class)
//    public MemberLoginResponse restapiLogin(String code, OAuthClientService oAuthClientService) {
//        // 1. "인가 코드"로 "액세스 토큰" 요청
//        OAuthTokenInfo oauthTokenInfo = oAuthClientService.createToken(code);
//
//        // 2. token의 sub로 OAuth 조회, 없으면 첫 로그인이므로 회원가입 마저 진행
//        return checkAlreadySignup(oauthTokenInfo, oAuthClientService.getOauthType());
//    }

    @Transactional(noRollbackFor = SignupNotCompletedException.class)
    public MemberLoginResponse sdkLogin(OAuthSdkLoginDto oAuthSdkLoginDto) {

        OAuthTokenInfo oauthTokenInfo = oAuthSdkLoginDto.getOauthTokenInfo();
        OAuthType oAuthType = oAuthSdkLoginDto.getOAuthType();
        String sub = oAuthSdkLoginDto.getSub();
        String email = oAuthSdkLoginDto.getEmail();

        // token의 sub로 OAuth 조회, 없으면 첫 로그인이므로 회원가입 진행 응답 생성
        Long memberId = checkAlreadySignup(oauthTokenInfo, oAuthType, sub);

        JwtInfo jwtInfo = createToken(email);

        return MemberLoginResponse.builder()
                .jwtInfo(jwtInfo)
                .memberId(memberId)
                .build();
    }

    //sdk 전용?
    @Transactional
    public MemberLoginResponse signup(SignupContinueDto signupContinueDto) {

        String email = signupContinueDto.getEmail();
        String nickname = signupContinueDto.getNickname();
        List<AgreementRequest> agreements = signupContinueDto.getAgreements();

        // 회원가입 처리
        Member savedMember = memberService.signupForOAuth(new MemberSignupRequest(email, UUID.randomUUID().toString(), nickname, agreements));

        OAuth savedOAuth = oauthRepository.save(OAuth.createOauth(signupContinueDto.getSub(), signupContinueDto.getOAuthType(), signupContinueDto.getAccessToken(), signupContinueDto.getRefreshToken(), savedMember));

        savedMember.createOauthAssociation(savedOAuth);

        JwtInfo jwtInfo = createToken(email);

//         토큰 발행 rest-api에서 필요. 임시 주석처리
//        OAuthTokenInfo oauthTokenInfo = oauthClientService.reissueToken(oauth.getRefreshToken());
//
//        String idToken = oauthTokenInfo.getIdToken();
//        String accessToken = oauthTokenInfo.getAccessToken();
//        String refreshToken = oauthTokenInfo.getRefreshToken();
//
//        // 값 변경
//        oauth.tokenReissue(accessToken, refreshToken);

        return MemberLoginResponse.builder()
                .memberId(savedMember.getId())
                .jwtInfo(jwtInfo)
                .build();
    }

    private Long checkAlreadySignup(OAuthTokenInfo oauthTokenInfo, OAuthType oauthType, String sub) {

        Member member = memberRepository.findByOAuthSubAndOAuthType(sub, oauthType)
                .orElseThrow(SignupNotCompletedException::new);

        OAuth oauth = member.getOauth();

        oauth.updateToken(oauthTokenInfo.getAccessToken(), oauthTokenInfo.getRefreshToken());

        return member.getId();
    }

    private JwtInfo createToken(String email) {
        return jwtTokenProvider.createToken(new UsernamePasswordAuthenticationToken(email, ""));
    }
}
