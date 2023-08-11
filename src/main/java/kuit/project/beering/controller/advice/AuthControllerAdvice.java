package kuit.project.beering.controller.advice;

import jakarta.persistence.EntityNotFoundException;
import kuit.project.beering.controller.AuthController;
import kuit.project.beering.dto.common.ObjectValidationError;
import kuit.project.beering.util.BaseResponse;
import kuit.project.beering.util.BaseResponseStatus;
import kuit.project.beering.util.exception.CustomJwtException;
import kuit.project.beering.util.exception.validation.AgreementValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = AuthController.class)
@Slf4j
public class AuthControllerAdvice {

    /**
     * @Breif 약관 정보 누락 시 거절 - 앱 개발 자체에서 실수
     */
    @ExceptionHandler(AgreementValidationException.class)
    public BaseResponse<Object> handleValidationException(AgreementValidationException ex) {

        BindingResult bindingResult = ex.getBindingResult();

        log.error("global error");
        ObjectError objectError = bindingResult.getGlobalError();
        ObjectValidationError objectValidationError = ObjectValidationError.builder()
                .ObjectName(objectError.getObjectName())
                .message(objectError.getDefaultMessage())
                .build();

        return new BaseResponse<>(BaseResponseStatus.INVALID_FIELD, objectValidationError);
    }

    /**
     * @Brief jwt 예외
     */
    @ExceptionHandler(CustomJwtException.class)
    public BaseResponse<Object> handleJwtException(CustomJwtException ex) {
        return new BaseResponse<>(ex.getStatus());
    }

    /**
     * @Brief 존재하지 않는 유저
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public BaseResponse<Object> handleEntityNotFound(EntityNotFoundException ex) {
        return new BaseResponse<>(BaseResponseStatus.NONE_MEMBER);
    }
}
