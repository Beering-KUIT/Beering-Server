package kuit.project.beering.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class SignupNotCompletedResponse {

    private boolean isLoginCompleted;
    private String sub;
}