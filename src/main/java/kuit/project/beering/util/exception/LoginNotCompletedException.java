package kuit.project.beering.util.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginNotCompletedException extends RuntimeException {

    private final String sub;

}
