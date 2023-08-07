package kuit.project.beering.dto.response;

import kuit.project.beering.domain.OAuthType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class SignupNotCompletedResponse {

    private boolean isLoginCompleted;
    private String sub;
    private OAuthType oauthType;
}
