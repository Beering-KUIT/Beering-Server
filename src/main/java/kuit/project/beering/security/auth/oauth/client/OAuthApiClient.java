package kuit.project.beering.security.auth.oauth.client;

import kuit.project.beering.dto.common.OAuthMemberInfo;

public interface OAuthApiClient {

    OAuthMemberInfo getOAuthAccount(String accessToken);
}
