package kuit.project.beering.controller.advice;

import kuit.project.beering.util.BaseResponse;
import kuit.project.beering.util.exception.*;
import kuit.project.beering.util.exception.domain.DomainException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalControllerAdvice {

    @ExceptionHandler(DomainException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseResponse<Object> handleDomainException(DomainException ex) {
        return new BaseResponse<>(ex.getStatus());
    }

    @ExceptionHandler(AwsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseResponse<Object> handleAwsException(AwsException ex) {
        return new BaseResponse<>(ex.getStatus());
    }

}
