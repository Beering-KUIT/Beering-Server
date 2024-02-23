package kuit.project.beering.util.exception.validation;

import org.springframework.validation.BindingResult;

public class AgreementValidationException extends ValidationException {

    public AgreementValidationException(BindingResult bindingResult) {
        super(bindingResult);
    }

}
