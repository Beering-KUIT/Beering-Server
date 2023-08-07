package kuit.project.beering.security.auth.oauth.properties;

import kuit.project.beering.domain.OAuthType;

public interface OAuthProperties {

    OAuthType getOAuthType();

    String getBaseUrl();

    String getRedirectUrl();

    String getRestapiKey();

    String getAdminKey();

    String getClientSecret();
}
