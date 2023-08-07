package kuit.project.beering.security.auth.oauth.properties;

import kuit.project.beering.domain.OAuthType;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Qualifier("KakaoOauthProperties")
public class KakaoOAuthProperties extends AbstractOAuthProperties{

    public KakaoOAuthProperties(
            @Value("${secret.oauth.kakao.base-url}") String baseUrl,
            @Value("${secret.oauth.kakao.redirect-url}") String redirectUrl,
            @Value("${secret.oauth.kakao.restapi-key}") String restapiKey,
            @Value("${secret.oauth.kakao.admin-key}") String adminKey,
            @Value("${secret.oauth.kakao.client-secret}") String clientSecret) {
        super(OAuthType.KAKAO, baseUrl, redirectUrl, restapiKey, adminKey, clientSecret);
    }
}
