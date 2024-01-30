package kuit.project.beering.controller;

import kuit.project.beering.domain.AgreementName;
import kuit.project.beering.domain.OAuthType;
import kuit.project.beering.dto.common.SignupContinueDto;
import kuit.project.beering.dto.request.auth.OAuthSignupRequest;
import kuit.project.beering.dto.request.member.AgreementRequest;
import kuit.project.beering.dto.response.member.MemberSdkLoginResponse;
import kuit.project.beering.security.auth.OAuthTypeMapper;
import kuit.project.beering.security.auth.oauth.service.OAuthClientServiceResolver;
import kuit.project.beering.security.jwt.JwtTokenProviderResolver;
import kuit.project.beering.security.jwt.OAuthTokenInfo;
import kuit.project.beering.security.jwt.jwtTokenProvider.JwtTokenProvider;
import kuit.project.beering.service.OAuthService;
import kuit.project.beering.util.BaseResponse;
import kuit.project.beering.util.BaseResponseStatus;
import kuit.project.beering.util.exception.SignupNotCompletedException;
import kuit.project.beering.util.exception.validation.AgreementValidationException;
import kuit.project.beering.util.exception.validation.FieldValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/oauth")
public class OAuthController {

    private final OAuthService oauthService;
    private final OAuthClientServiceResolver oauthClientServiceResolver;
    private final JwtTokenProviderResolver jwtTokenProviderResolver;
    private final OAuthTypeMapper oAuthTypeMapper;

    // rest-api
//    @GetMapping("/kakao/callback")
//    public BaseResponse<MemberLoginResponse> restapiLogin(@ModelAttribute OAuthCodeRequest OAuthCodeRequest) {
//
//        if (OAuthCodeRequest.getError() != null) return new BaseResponse<>(BaseResponseStatus.OAUTH_LOGIN_FAILED);
//
//        MemberLoginResponse memberLoginResponse = oauthService.restapiLogin(OAuthCodeRequest.getCode(), oauthClientServiceResolver.getOauthClientService(OAuthType.KAKAO));
//
//        return new BaseResponse<>(memberLoginResponse);
//    }

    @PostMapping("/sdk")
    public BaseResponse<MemberSdkLoginResponse> sdkLogin(@RequestBody OAuthTokenInfo oauthTokenInfo) {
        String idToken = oauthTokenInfo.getIdToken();

        String issuer = jwtTokenProviderResolver.getProvider(idToken).parseIssuer(idToken);
        OAuthType oAuthType = oAuthTypeMapper.get(issuer);

        MemberSdkLoginResponse memberSdkLoginResponse = oauthService.sdkLogin(oauthTokenInfo, oAuthType);

        return new BaseResponse<>(memberSdkLoginResponse);
    }

    @PostMapping("/signup")
    public BaseResponse<MemberSdkLoginResponse> signup(@RequestBody @Validated OAuthSignupRequest request,
                                       BindingResult bindingResult) {

        validateAgreement(request, bindingResult);

        if (bindingResult.hasFieldErrors()) throw new FieldValidationException(bindingResult);
        if (bindingResult.hasGlobalErrors()) throw new AgreementValidationException(bindingResult);

        MemberSdkLoginResponse response = oauthService.signupContinue(createSignupRequestDto(request));

        return new BaseResponse<>(response);
    }

    @ExceptionHandler(SignupNotCompletedException.class)
    public BaseResponse<Object> loginNotCompleted(SignupNotCompletedException ex) {
        return new BaseResponse<>(BaseResponseStatus.SUCCESS_CONTINUE_SIGNUP, null);
    }

    /**
     * @param request
     * @param bindingResult
     * @Brief Agreement 값 제대로 들어왔는지 확인. 개수, 약관 포함 여부
     */
    private static void validateAgreement(OAuthSignupRequest request, BindingResult bindingResult) {
        List<AgreementRequest> agreements = request.getAgreements();
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

    private SignupContinueDto createSignupRequestDto(OAuthSignupRequest request) {
        String idToken = request.getIdToken();
        JwtTokenProvider provider = jwtTokenProviderResolver.getProvider(idToken);
        String issuer = provider.parseIssuer(idToken);

        OAuthType oAuthType = oAuthTypeMapper.get(issuer);
        String sub = provider.parseSub(idToken);
        String email = provider.parseEmail(idToken);
        return SignupContinueDto.builder()
                .request(request)
                .oAuthType(oAuthType)
                .sub(sub)
                .email(email).build();
    }
}
