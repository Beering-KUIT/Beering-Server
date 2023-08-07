package kuit.project.beering.security.auth.oauth.helper;

import kuit.project.beering.domain.OAuthType;
import kuit.project.beering.dto.OAuthMemberInfo;
import kuit.project.beering.security.jwt.OAuthTokenInfo;

public interface OAuthHelper {

    boolean validateToken(String token);

    OAuthTokenInfo reissueToken(String refreshToken);

    OAuthTokenInfo createToken(String code);

    OAuthType getOauthType();

    OAuthMemberInfo getAccount(String accessToken);
}
