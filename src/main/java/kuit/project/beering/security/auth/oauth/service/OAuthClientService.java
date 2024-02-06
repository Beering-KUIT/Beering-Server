package kuit.project.beering.security.auth.oauth.service;

import kuit.project.beering.domain.OAuthType;
import kuit.project.beering.dto.common.OAuthMemberInfo;
import kuit.project.beering.security.jwt.OAuthTokenInfo;

public interface OAuthClientService {

    void validateToken(String token);

    OAuthTokenInfo reissueToken(String refreshToken);

    OAuthTokenInfo createToken(String code);

    OAuthType getOauthType();

    OAuthMemberInfo getAccount(String accessToken);
}
