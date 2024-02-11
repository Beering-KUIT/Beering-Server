package kuit.project.beering.controller;

import kuit.project.beering.domain.AgreementName;
import kuit.project.beering.dto.request.auth.RefreshTokenRequest;
import kuit.project.beering.dto.request.member.AgreementRequest;
import kuit.project.beering.dto.request.member.MemberLoginRequest;
import kuit.project.beering.dto.request.member.MemberSignupRequest;
import kuit.project.beering.dto.response.member.MemberLoginResponse;
import kuit.project.beering.service.MemberService;
import kuit.project.beering.service.TokenService;
import kuit.project.beering.security.jwt.JwtInfo;
import kuit.project.beering.util.BaseResponse;
import kuit.project.beering.util.exception.validation.AgreementValidationException;
import kuit.project.beering.util.exception.validation.FieldValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final MemberService memberService;
    private final TokenService tokenService;

    @PostMapping("/signup")
    public BaseResponse<Object> signup(@RequestBody @Validated MemberSignupRequest request,
                                       BindingResult bindingResult) {

        validateAgreement(request, bindingResult);

        if (bindingResult.hasFieldErrors()) throw new FieldValidationException(bindingResult);
        if (bindingResult.hasGlobalErrors()) throw new AgreementValidationException(bindingResult);

        memberService.signup(request);

        return new BaseResponse<>(new Object());
    }

    @PostMapping("/login")
    public BaseResponse<MemberLoginResponse> login(@RequestBody @Validated MemberLoginRequest request,
                                                   BindingResult bindingResult) {

        if (bindingResult.hasErrors()) throw new FieldValidationException(bindingResult);

        MemberLoginResponse response = memberService.login(request);

        return new BaseResponse<>(response);
    }

    /**
     * @Brief 토큰 재발행 api
     * @param refreshToken
     * @return
     */
    @PostMapping("/token")
    public BaseResponse<JwtInfo> reissueToken(@RequestBody @Validated RefreshTokenRequest refreshToken, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) throw new FieldValidationException(bindingResult);

        JwtInfo jwtInfo = tokenService.reissueToken(refreshToken);

        return new BaseResponse<>(jwtInfo);
    }

    /**
     * @param memberSignupRequest
     * @param bindingResult
     * @Brief Agreement 값 제대로 들어왔는지 확인. 개수, 약관 포함 여부
     */
    private static void validateAgreement(MemberSignupRequest memberSignupRequest, BindingResult bindingResult) {
        List<AgreementRequest> agreements = memberSignupRequest.getAgreements();
        List<AgreementName> agreementNames = agreements.stream()
                .map(AgreementRequest::getName).toList();

        /**
         * @Condition 약관 개수와 필요한 약관이 모두 들어왔는지
         */
        if (agreementNames.size() != AgreementName.values().length &&
                !agreementNames.containsAll(Arrays.stream(AgreementName.values()).toList())) {
            bindingResult.addError(new ObjectError
                    ("Agreement", "SERVICE, PERSONAL, MARKETING 을 모두 포함"));
        }

        List<AgreementRequest> agreementRequests = agreements.stream().filter(agreementRequest ->
                AgreementName.SERVICE.equals(agreementRequest.getName()) ||
                        AgreementName.PERSONAL.equals(agreementRequest.getName())).toList();

        agreementRequests.forEach(agreementRequest -> {
            if (!agreementRequest.getIsAgreed())
                bindingResult.addError(new FieldError(
                        "Agreement", "name", agreementRequest.getName(),
                        true, null, null,
                        "SERVICE, PERSONAL 의 isAgreed 값은 반드시 TRUE"));
        });
    }
}
