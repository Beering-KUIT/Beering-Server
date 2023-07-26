package kuit.project.beering.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import kuit.project.beering.dto.request.auth.KakaoLoginRequest;
import kuit.project.beering.dto.response.SignupNotCompletedResponse;
import kuit.project.beering.dto.response.member.MemberLoginResponse;
import kuit.project.beering.service.OAuthService;
import kuit.project.beering.util.BaseResponse;
import kuit.project.beering.util.BaseResponseStatus;
import kuit.project.beering.util.exception.SignupNotCompletedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/oauth")
public class OAuthController {

    private final OAuthService oAuthService;

    @GetMapping("/kakao/callback")
    public BaseResponse<MemberLoginResponse> kakaoOauth(@ModelAttribute KakaoLoginRequest kakaoLoginRequest) throws JsonProcessingException {

        if (!kakaoLoginRequest.getError().isBlank()) return new BaseResponse<>(BaseResponseStatus.OAUTH_LOGIN_FAILED);

        MemberLoginResponse memberLoginResponse = oAuthService.kakaoOAuth(kakaoLoginRequest.getCode());

        return new BaseResponse<>(memberLoginResponse);

    }

    @ExceptionHandler(SignupNotCompletedException.class)
    public BaseResponse<SignupNotCompletedResponse> loginNotCompleted(SignupNotCompletedException ex) {

        return new BaseResponse<>(SignupNotCompletedResponse.builder()
                .isLoginCompleted(false)
                .sub(ex.getSub()).build());
    }
}
