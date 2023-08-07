package kuit.project.beering.security.auth.oauth.client;

import kuit.project.beering.dto.OAuthMemberInfo;
import kuit.project.beering.redis.OIDCPublicKeysResponse;
import kuit.project.beering.security.jwt.OAuthTokenInfo;

public interface OAuthClient {

    OIDCPublicKeysResponse getOIDCOpenKeys();

    OAuthTokenInfo getToken(String clientId, String redirectUri, String code, String client_secret);

    OAuthTokenInfo reissueToken(String clientId, String refreshToken, String secretKey);

    OAuthMemberInfo getOAuthAccount(String accessToken);
}
