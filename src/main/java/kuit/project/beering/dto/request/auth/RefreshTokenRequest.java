package kuit.project.beering.dto.request.auth;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RefreshTokenRequest {

    @Pattern(regexp = "Bearer .*", message = "Refresh token should start with 'Bearer '")
    private String refreshToken;
}
