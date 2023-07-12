package kuit.project.beering.controller.advice;

import kuit.project.beering.controller.MemberController;
import kuit.project.beering.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RestControllerAdvice(assignableTypes = MemberController.class)
public class MemberControllerAdvice {

    private static final Map<String, Integer> errorStatus = new ConcurrentHashMap<>();

    //TODO 시간 남으면 메시지소스로 분리
    static {
        errorStatus.put("이메일 형식 확인", 123);
        errorStatus.put("최대 320자리 이내", 234);
        errorStatus.put("영문, 숫자, 특수문자 혼합하여 8 ~ 20자리 이내", 345);
        errorStatus.put("영문 또는 한글 1 ~ 10자리 이내", 456);
        errorStatus.put("SERVICE, PERSONAL, MARKETING 중 하나만 허용", 567);
        errorStatus.put("SERVICE, PERSONAL, MARKETING 을 모두 포함", 678);
        errorStatus.put("SERVICE, PERSONAL 의 isAgreed 값은 반드시 TRUE", 789);
        errorStatus.put("NULL 일 수 없음", 891);
    }

    /**
     * @Brief 검증 오류 시 형식에 맞도록 응답 객체 반환
     * @param ex
     * @return
     */
    @ExceptionHandler(UserValidationException.class)
    public BaseResponse<Object> handleValidationException(UserValidationException ex) {
        log.error("MemberControllerAdvice.handleValidationException = {}", ex.getClass().getName());

        BindingResult bindingResult = ex.getBindingResult();
        List<FieldValidationError> fieldValidationErrors = new ArrayList<>();

        /**
         * @Breif 약관 정보 누락 시 거절 - 앱 개발 자체에서 실수
         */
        if (bindingResult.hasGlobalErrors()) {
            log.error("global error");
            ObjectError objectError = bindingResult.getGlobalError();
            ObjectValidationError objectValidationError = ObjectValidationError.builder()
                    .code(errorStatus.get(objectError.getDefaultMessage()))
                    .ObjectName(objectError.getObjectName())
                    .message(objectError.getDefaultMessage())
                    .build();

            return new BaseResponse<>(BaseResponseStatus.INVALID_FIELD, objectValidationError);
        }

        /**
         * @Brief 회원 입력 정보 검증
         */
        if (bindingResult.hasFieldErrors()) {
            log.error("field error");
            fieldValidationErrors = bindingResult.getFieldErrors().stream().map(fieldError ->
                    FieldValidationError.builder()
                            .code(errorStatus.get(fieldError.getDefaultMessage()))
                            .fieldName(fieldError.getField())
                            .rejectValue(String.valueOf(fieldError.getRejectedValue()))
                            .message(fieldError.getDefaultMessage())
                            .build()).toList();
        }

        return new BaseResponse<>(BaseResponseStatus.INVALID_FIELD, fieldValidationErrors);
    }

    /**
     * @Brief 이메일 중복 예외 처리
     * @param ex
     * @return
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public BaseResponse<Object> duplicateException(SQLIntegrityConstraintViolationException ex) {
        return new BaseResponse<>(BaseResponseStatus.DUPLICATED_EMAIL);
    }
}
