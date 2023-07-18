package kuit.project.beering.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.validation.BindingResult;

/**
 * @Brief 회원 가입 검증 예외
 */
@Getter
@AllArgsConstructor
public class MemberValidationException extends RuntimeException {

    private BindingResult bindingResult;

}
