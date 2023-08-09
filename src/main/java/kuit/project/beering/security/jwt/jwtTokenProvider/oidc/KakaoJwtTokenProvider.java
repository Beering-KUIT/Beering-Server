package kuit.project.beering.security.jwt.jwtTokenProvider.oidc;

import kuit.project.beering.repository.MemberRepository;
import kuit.project.beering.repository.OAuthRepository;
import kuit.project.beering.security.auth.oauth.service.OAuthClientService;
import kuit.project.beering.security.jwt.JwtParser;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier("kakaoJwtTokenProvider")
public class KakaoJwtTokenProvider extends AbstractOIDCJwtTokenProvider {

    public KakaoJwtTokenProvider(
            @Qualifier("kakaoClientService") OAuthClientService oAuthClientService,
            MemberRepository memberRepository,
            OAuthRepository oauthRepository,
            JwtParser jwtParser) {
        super(oAuthClientService, jwtParser, memberRepository, oauthRepository);
    }

}