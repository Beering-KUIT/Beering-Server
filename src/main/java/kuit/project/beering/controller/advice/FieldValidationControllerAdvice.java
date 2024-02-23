package kuit.project.beering.controller.advice;

import kuit.project.beering.util.BaseResponse;
import kuit.project.beering.util.BaseResponseStatus;
import kuit.project.beering.dto.common.FieldErrorsDto;
import kuit.project.beering.dto.common.FieldValidationError;
import kuit.project.beering.util.exception.validation.FieldValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice(annotations = RestController.class)
public class FieldValidationControllerAdvice {

    /**
     * @Brief 필드 검증 오류 시 형식에 맞도록 응답 객체 반환
     */
    @ExceptionHandler(FieldValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseResponse<Object> handleValidationException(FieldValidationException ex) {

        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();

        FieldErrorsDto build = FieldErrorsDto.builder().errors(
                        fieldErrors.stream().map(fieldError ->
                                new FieldValidationError(
                                        fieldError.getField(),
                                        String.valueOf(fieldError.getRejectedValue()),
                                        fieldError.getDefaultMessage())
                        ).toList())
                .build();

        return new BaseResponse<>(BaseResponseStatus.INVALID_FIELD, build);
    }
}
