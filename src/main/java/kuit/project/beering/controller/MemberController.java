package kuit.project.beering.controller;

import jakarta.validation.Valid;
import kuit.project.beering.domain.AgreementName;
import kuit.project.beering.dto.request.member.AgreementRequest;
import kuit.project.beering.dto.request.member.MemberLoginRequest;
import kuit.project.beering.dto.request.member.MemberSignupRequest;
import kuit.project.beering.dto.response.member.MemberLoginResponse;
import kuit.project.beering.service.MemberService;
import kuit.project.beering.util.BaseResponse;
import kuit.project.beering.util.UserValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/signup")
    public BaseResponse<Object> signup(@RequestBody @Valid MemberSignupRequest request,
                                       BindingResult bindingResult) {

        validateAgreement(request, bindingResult);

        if (bindingResult.hasErrors()) throw new UserValidationException(bindingResult);

        memberService.signup(request);

        return new BaseResponse<>(new Object());
    }

    @PostMapping("/login")
    public BaseResponse<MemberLoginResponse> login(@RequestBody MemberLoginRequest request) {

        MemberLoginResponse response = memberService.login(request);

        return new BaseResponse<>(response);
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
