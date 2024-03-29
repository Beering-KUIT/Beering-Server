package kuit.project.beering.controller;

import kuit.project.beering.dto.request.member.MemberEmailRequest;
import kuit.project.beering.dto.request.member.MemberNicknameRequest;
import kuit.project.beering.dto.response.member.MemberEmailResponse;
import kuit.project.beering.dto.response.member.MemberInfoResponse;
import kuit.project.beering.dto.response.member.MemberNicknameResponse;
import kuit.project.beering.security.auth.AuthMember;
import kuit.project.beering.service.MemberService;
import kuit.project.beering.util.BaseResponse;
import kuit.project.beering.util.exception.validation.FieldValidationException;
import kuit.project.beering.util.exception.domain.MemberException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static kuit.project.beering.util.CheckMember.validateMember;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/validate/username")
    public BaseResponse<MemberEmailResponse> checkEmail(@Validated @ModelAttribute MemberEmailRequest memberEmailRequest, BindingResult bindingResult) {

        if (bindingResult.hasFieldErrors()) throw new FieldValidationException(bindingResult);

        MemberEmailResponse memberEmailResponse = memberService.checkEmail(memberEmailRequest.getUsername());

        return new BaseResponse<>(memberEmailResponse);
    }

    @GetMapping("/validate/nickname")
    public BaseResponse<MemberNicknameResponse> checkNickname(@Validated @ModelAttribute MemberNicknameRequest memberNicknameRequest, BindingResult bindingResult) {
        if (bindingResult.hasFieldErrors()) throw new FieldValidationException(bindingResult);

        MemberNicknameResponse memberEmailResponse = memberService.checkNickname(memberNicknameRequest.getNickname());

        return new BaseResponse<>(memberEmailResponse);
    }

    @GetMapping("/me")
    public BaseResponse<Object> getMyInfo(@AuthenticationPrincipal AuthMember authMember) {

        MemberInfoResponse memberInfoResponse = memberService.getMemberInfo(authMember.getId());

        return new BaseResponse<>(memberInfoResponse);
    }

    @PostMapping("/{memberId}/images")
    public BaseResponse<Object> uploadProfileImage(@RequestParam("profile_image") MultipartFile multipartFile,
                                                   @PathVariable Long memberId, @AuthenticationPrincipal AuthMember authMember) {
        validateMember(authMember, memberId, MemberException::new);
        memberService.uploadImage(multipartFile, memberId);

        return new BaseResponse<>(new Object());
    }

}
