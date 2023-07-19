package kuit.project.beering.controller.advice;

import kuit.project.beering.controller.MemberController;
import kuit.project.beering.util.*;
import kuit.project.beering.util.exception.AgreementValidationException;
import kuit.project.beering.util.exception.DuplicateUsernameException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice(assignableTypes = MemberController.class)
public class MemberControllerAdvice {

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
     * @Brief 이메일 중복 예외 처리
     */
    @ExceptionHandler(DuplicateUsernameException.class)
    public BaseResponse<Object> duplicateException(DuplicateUsernameException ex) {
        return new BaseResponse<>(BaseResponseStatus.DUPLICATED_EMAIL);
    }
}
