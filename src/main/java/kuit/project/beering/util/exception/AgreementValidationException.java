package kuit.project.beering.util.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.validation.BindingResult;

@AllArgsConstructor
@Getter
public class AgreementValidationException extends RuntimeException {
    private BindingResult bindingResult;
}
