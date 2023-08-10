package kuit.project.beering.util.exception;

import kuit.project.beering.util.exception.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.BindingResult;

@Slf4j
public class FieldValidationException extends ValidationException {

    public FieldValidationException(BindingResult bindingResult) {
        super(bindingResult);
        log.info("{} - message : {}", this.getClass().getSimpleName(),
                bindingResult.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage));
    }
}
