package kuit.project.beering.service;

import jakarta.persistence.EntityNotFoundException;
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
import kuit.project.beering.security.jwt.jwtTokenProvider.BeeringJwtTokenProvider;
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
    private final BeeringJwtTokenProvider jwtTokenProvider;

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
        // token의 sub로 OAuth 조회, 없으면 첫 로그인이므로 회원가입 마저 진행
        OAuthTokenInfo oauthTokenInfo = oAuthSdkLoginDto.getOauthTokenInfo();
        OAuthType oAuthType = oAuthSdkLoginDto.getOAuthType();
        String sub = oAuthSdkLoginDto.getSub();
        String email = oAuthSdkLoginDto.getEmail();

        Long memberId = checkAlreadySignup(oauthTokenInfo, oAuthType, sub);

        JwtInfo jwtInfo = createToken(email);

        return MemberLoginResponse.builder()
                .jwtInfo(jwtInfo)
                .memberId(memberId)
                .build();
    }

    //sdk 전용?
    @Transactional
    public MemberLoginResponse signupContinue(SignupContinueDto signupContinueDto) {

        OAuth oauth = oauthRepository.findBySubAndOauthType(signupContinueDto.getSub(), signupContinueDto.getOAuthType())
                .orElseThrow(EntityNotFoundException::new);

        String email = signupContinueDto.getEmail();
        String nickname = signupContinueDto.getNickname();
        List<AgreementRequest> agreements = signupContinueDto.getAgreements();

        // 회원가입 처리
        memberService.signup(new MemberSignupRequest(email, UUID.randomUUID().toString(), nickname, agreements));
        Member member = memberRepository.findByUsername(email).orElseThrow(EntityNotFoundException::new);
        member.createOauthAssociation(oauth);

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
                .memberId(member.getId())
                .jwtInfo(jwtInfo)
                .build();
    }
    private Long checkAlreadySignup(OAuthTokenInfo oauthTokenInfo, OAuthType oauthType, String sub) {

        OAuth oauth = oauthRepository.findBySubAndOauthType(sub, oauthType)
                .orElseThrow(() -> {
                    oauthRepository.save(OAuth.createOauth(sub, oauthType, oauthTokenInfo.getAccessToken(), oauthTokenInfo.getRefreshToken()));
                    throw new SignupNotCompletedException();
                });

        Member member = memberRepository.findByOauthId(oauth.getId())
                .orElseThrow(SignupNotCompletedException::new);

        return member.getId();
    }

    private JwtInfo createToken(String email) {
        return jwtTokenProvider.createToken(new UsernamePasswordAuthenticationToken(email, ""));
    }
}
