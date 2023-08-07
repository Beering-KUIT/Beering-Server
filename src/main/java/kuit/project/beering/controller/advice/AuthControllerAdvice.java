package kuit.project.beering.controller.advice;

import jakarta.persistence.EntityNotFoundException;
import kuit.project.beering.util.BaseResponse;
import kuit.project.beering.util.BaseResponseStatus;
import kuit.project.beering.util.ObjectValidationError;
import kuit.project.beering.util.exception.AgreementValidationException;
import kuit.project.beering.util.exception.CustomJwtException;
import kuit.project.beering.util.exception.DuplicateUsernameException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
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
     * @Brief 이메일 중복 예외 처리
     */
    @ExceptionHandler(DuplicateUsernameException.class)
    public BaseResponse<Object> duplicateException(DuplicateUsernameException ex) {
        return new BaseResponse<>(BaseResponseStatus.DUPLICATED_EMAIL);
    }

    /**
     * @Brief 이메일 존재하지 않음
     */
    @ExceptionHandler(UsernameNotFoundException.class)
    public BaseResponse<Object> usernameNotFound(UsernameNotFoundException ex) {
        return new BaseResponse<>(BaseResponseStatus.NONE_MEMBER);
    }

    /**
     * @Brief 비밀번호 오류
     */
    @ExceptionHandler(BadCredentialsException.class)
    public BaseResponse<Object> badCredential(BadCredentialsException ex) {
        return new BaseResponse<>(BaseResponseStatus.INVALID_CHECKED_PASSWORD);
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
