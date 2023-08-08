package kuit.project.beering.controller;

import kuit.project.beering.dto.request.member.MemberEmailRequest;
import kuit.project.beering.dto.request.member.MemberNicknameRequest;
import kuit.project.beering.dto.response.member.MemberEmailResponse;
import kuit.project.beering.dto.response.member.MemberInfoResponse;
import kuit.project.beering.dto.response.member.MemberNicknameResponse;
import kuit.project.beering.security.auth.AuthMember;
import kuit.project.beering.service.MemberService;
import kuit.project.beering.util.BaseResponse;
import kuit.project.beering.util.exception.FieldValidationException;
import kuit.project.beering.util.exception.MemberException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

import static kuit.project.beering.util.BaseResponseStatus.TOKEN_PATH_MISMATCH;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/validate/email")
    public BaseResponse<MemberEmailResponse> checkEmail(@Validated @RequestBody MemberEmailRequest memberEmailRequest, BindingResult bindingResult) {

        if (bindingResult.hasFieldErrors()) throw new FieldValidationException(bindingResult);

        MemberEmailResponse memberEmailResponse = memberService.checkEmail(memberEmailRequest.getUsername());

        return new BaseResponse<>(memberEmailResponse);
    }

    @GetMapping("/validate/nickname")
    public BaseResponse<MemberNicknameResponse> checkNickname(@Validated @RequestBody MemberNicknameRequest memberEmailRequest, BindingResult bindingResult) {
        if (bindingResult.hasFieldErrors()) throw new FieldValidationException(bindingResult);

        MemberNicknameResponse memberEmailResponse = memberService.checkNickname(memberEmailRequest.getNickname());

        return new BaseResponse<>(memberEmailResponse);
    }

    @GetMapping("/me")
    public BaseResponse<Object> getMyInfo(@AuthenticationPrincipal AuthMember authMember) {

        MemberInfoResponse memberInfoResponse = memberService.getMemberInfo(authMember.getId());

        return new BaseResponse<>(memberInfoResponse);
    }

    @PostMapping("/{memberId}/images")
    public BaseResponse<Object> uploadProfileImage(@RequestParam("file") MultipartFile multipartFile,
                                                   @PathVariable Long memberId, @AuthenticationPrincipal AuthMember authMember) {
        validateMember(authMember.getId(), memberId);

        memberService.uploadImage(multipartFile, memberId);

        return new BaseResponse<>(new Object());
    }

    private void validateMember(Long authId, Long memberId) {
        if (!Objects.equals(authId, memberId))
            throw new MemberException(TOKEN_PATH_MISMATCH);
    }

}
