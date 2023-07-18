package kuit.project.beering.controller.advice;

import kuit.project.beering.util.BaseResponse;
import kuit.project.beering.util.BaseResponseStatus;
import kuit.project.beering.util.FieldValidationError;
import kuit.project.beering.util.FieldValidationException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice(annotations = RestController.class)
public class FieldValidationControllerAdvice {

    /**
     * @Brief 필드 검증 오류 시 형식에 맞도록 응답 객체 반환
     */
    @ExceptionHandler(FieldValidationException.class)
    public BaseResponse<Object> handleValidationException(FieldValidationException ex) {

        BindingResult bindingResult = ex.getBindingResult();

        List<FieldValidationError> fieldValidationErrors =
                bindingResult.getFieldErrors().stream().map(fieldError ->
                        FieldValidationError.builder()
                                .fieldName(fieldError.getField())
                                .rejectValue(String.valueOf(fieldError.getRejectedValue()))
                                .message(fieldError.getDefaultMessage())
                                .build()).toList();

        return new BaseResponse<>(BaseResponseStatus.INVALID_FIELD, fieldValidationErrors);
    }
}
