package kuit.project.beering.controller;

import kuit.project.beering.domain.AgreementName;
import kuit.project.beering.domain.OAuthType;
import kuit.project.beering.dto.common.OAuthSdkLoginDto;
import kuit.project.beering.dto.common.SignupContinueDto;
import kuit.project.beering.dto.request.auth.OAuthSignupRequest;
import kuit.project.beering.dto.request.member.AgreementRequest;
import kuit.project.beering.dto.response.member.MemberLoginResponse;
import kuit.project.beering.security.auth.OAuthTypeMapper;
import kuit.project.beering.security.auth.oauth.service.OAuthClientService;
import kuit.project.beering.security.auth.oauth.service.OAuthClientServiceResolver;
import kuit.project.beering.security.jwt.JwtParser;
import kuit.project.beering.security.jwt.OAuthTokenInfo;
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
@RequestMapping("/oauth/sdk")
public class OAuthSdkController {

    private final OAuthService oauthService;
    private final OAuthClientServiceResolver oAuthClientServiceResolver;
    private final JwtParser jwtParser;
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

    @PostMapping("/login")
    public BaseResponse<MemberLoginResponse> sdkLogin(@RequestBody OAuthTokenInfo oauthTokenInfo) {

        OAuthSdkLoginDto oAuthSdkLoginDto = createOAuthSdkLoginDto(oauthTokenInfo);

        MemberLoginResponse memberLoginResponse = oauthService.sdkLogin(oAuthSdkLoginDto);

        return new BaseResponse<>(memberLoginResponse);
    }

    @PostMapping("/signup")
    public BaseResponse<MemberLoginResponse> signup(@RequestBody @Validated OAuthSignupRequest request,
                                       BindingResult bindingResult) {

        validateAgreement(request, bindingResult);

        if (bindingResult.hasFieldErrors()) throw new FieldValidationException(bindingResult);
        if (bindingResult.hasGlobalErrors()) throw new AgreementValidationException(bindingResult);

        MemberLoginResponse response = oauthService.signupContinue(createSignupRequestDto(request));

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
        String issuer = validateIdToken(idToken);

        OAuthType oAuthType = oAuthTypeMapper.get(issuer);
        String sub = jwtParser.parseSub(idToken);
        String email = jwtParser.parseEmail(idToken);

        return SignupContinueDto.builder()
                .request(request)
                .oAuthType(oAuthType)
                .sub(sub)
                .email(email).build();
    }

    private OAuthSdkLoginDto createOAuthSdkLoginDto(OAuthTokenInfo oauthTokenInfo) {

        String idToken = oauthTokenInfo.getIdToken();
        String issuer = validateIdToken(idToken);

        String sub = jwtParser.parseSub(idToken);
        String email = jwtParser.parseEmail(idToken);
        OAuthType oAuthType = oAuthTypeMapper.get(issuer);

        return OAuthSdkLoginDto.builder()
                .oauthTokenInfo(oauthTokenInfo)
                .oAuthType(oAuthType)
                .sub(sub)
                .email(email)
                .build();
    }

    private String validateIdToken(String idToken) {
        String issuer = jwtParser.parseIssuer(idToken);
        OAuthClientService oauthClientService = oAuthClientServiceResolver.getOauthClientService(issuer);
        oauthClientService.validateToken(idToken);

        return issuer;
    }
}
