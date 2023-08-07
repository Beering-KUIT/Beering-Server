package kuit.project.beering.controller.advice;

import kuit.project.beering.controller.MemberController;
import kuit.project.beering.util.BaseResponse;
import kuit.project.beering.util.BaseResponseStatus;
import kuit.project.beering.util.exception.DuplicateNicknameException;
import kuit.project.beering.util.exception.DuplicateUsernameException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice(assignableTypes = MemberController.class)
public class MemberControllerAdvice {

    /**
     * @Brief 이메일 중복 예외 처리
     */
    @ExceptionHandler(DuplicateUsernameException.class)
    public BaseResponse<Object> duplicateEmailException(DuplicateUsernameException ex) {
        return new BaseResponse<>(BaseResponseStatus.DUPLICATED_EMAIL);
    }

    /**
     * @Brief 닉네임 중복 예외 처리
     */
    @ExceptionHandler(DuplicateNicknameException.class)
    public BaseResponse<Object> duplicateNicknameException(DuplicateNicknameException ex) {
        return new BaseResponse<>(BaseResponseStatus.DUPLICATED_NICKNAME);
    }
}
