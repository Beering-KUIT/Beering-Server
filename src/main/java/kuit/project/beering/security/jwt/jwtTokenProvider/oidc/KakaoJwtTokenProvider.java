package kuit.project.beering.security.jwt.jwtTokenProvider.oidc;

import kuit.project.beering.repository.MemberRepository;
import kuit.project.beering.repository.OAuthRepository;
import kuit.project.beering.security.auth.oauth.helper.OAuthHelper;
import kuit.project.beering.security.jwt.JwtParser;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier("kakaoJwtTokenProvider")
public class KakaoJwtTokenProvider extends AbstractOIDCJwtTokenProvider {

    public KakaoJwtTokenProvider(
            @Qualifier("kakaoOauthHelper") OAuthHelper oAuthHelper,
            MemberRepository memberRepository,
            OAuthRepository oauthRepository,
            JwtParser jwtParser) {
        super(oAuthHelper, jwtParser, memberRepository, oauthRepository);
    }

}