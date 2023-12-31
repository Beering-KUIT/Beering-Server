package kuit.project.beering.util.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.validation.BindingResult;

/**
 * @Brief 회원 가입 검증 예외
 */
@Getter
@AllArgsConstructor
public class FieldValidationException extends RuntimeException {

    private BindingResult bindingResult;

}
