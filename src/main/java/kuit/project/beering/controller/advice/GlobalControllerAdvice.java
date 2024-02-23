package kuit.project.beering.controller.advice;

import kuit.project.beering.util.BaseResponse;
import kuit.project.beering.util.exception.*;
import kuit.project.beering.util.exception.domain.DomainException;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import static kuit.project.beering.util.BaseResponseStatus.METHOD_ARGUMENT_TYPE_MISMATCH;

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

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public BaseResponse<?> handleTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        // TODO : String으로 반환할지 JSON으로 반환할지 고민
        // String result = ex.getName() + " : " + ex.getMessage();
        JSONObject result = new JSONObject();
        result.put(ex.getName(), ex.getMessage());
        return new BaseResponse<>(METHOD_ARGUMENT_TYPE_MISMATCH, result);
    }
}
