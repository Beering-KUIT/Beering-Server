package kuit.project.beering.dto.common;

import kuit.project.beering.domain.OAuthType;
import kuit.project.beering.security.jwt.OAuthTokenInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Builder
@Getter
@Setter
public class OAuthSdkLoginDto {

    private final OAuthTokenInfo oauthTokenInfo;
    private final OAuthType oAuthType;
    private final String sub;
    private final String email;

    public String getIdToken() {
        return oauthTokenInfo.getIdToken();
    }
    public String getAccessToken() {
        return oauthTokenInfo.getAccessToken();
    }
    public String getRefreshToken() {
        return oauthTokenInfo.getRefreshToken();
    }
}
