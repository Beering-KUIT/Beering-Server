package kuit.project.beering.security.jwt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class OAuthTokenInfo {

    private String accessToken;
    private String refreshToken;
    private String idToken;
}
