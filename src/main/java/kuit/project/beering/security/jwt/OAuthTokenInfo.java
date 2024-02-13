package kuit.project.beering.security.jwt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
@Builder
@Setter
public class OAuthTokenInfo {

    private String accessToken;
    private String refreshToken;
    private String idToken;
}
