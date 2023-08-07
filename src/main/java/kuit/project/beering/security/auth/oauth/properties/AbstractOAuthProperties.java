package kuit.project.beering.security.auth.oauth.properties;

import kuit.project.beering.domain.OAuthType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public abstract class AbstractOAuthProperties implements OAuthProperties{

    private final OAuthType oAuthType;
    private final String baseUrl;
    private final String redirectUrl;
    private final String restapiKey;
    private final String adminKey;
    private final String clientSecret;

}
