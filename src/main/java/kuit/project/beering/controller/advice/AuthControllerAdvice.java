package kuit.project.beering.controller.advice;

import jakarta.persistence.EntityNotFoundException;
import kuit.project.beering.util.BaseResponse;
import kuit.project.beering.util.BaseResponseStatus;
import kuit.project.beering.util.exception.CustomJwtException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AuthControllerAdvice {

    @ExceptionHandler(CustomJwtException.class)
    public BaseResponse<Object> handleJwtException(CustomJwtException ex) {
        return new BaseResponse<>(ex.getStatus());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public BaseResponse<Object> handleEntityNotFound(EntityNotFoundException ex) {
        return new BaseResponse<>(BaseResponseStatus.NONE_USER);
    }
}
