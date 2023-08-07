package kuit.project.beering.util.exception;

import kuit.project.beering.domain.OAuthType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SignupNotCompletedException extends RuntimeException {

    private final String sub;
    private final OAuthType oauthType;
}
