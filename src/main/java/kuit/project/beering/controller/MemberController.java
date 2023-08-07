package kuit.project.beering.controller;

import kuit.project.beering.dto.request.member.MemberEmailRequest;
import kuit.project.beering.dto.request.member.MemberNicknameRequest;
import kuit.project.beering.dto.response.member.MemberEmailResponse;
import kuit.project.beering.dto.response.member.MemberNicknameResponse;
import kuit.project.beering.service.MemberService;
import kuit.project.beering.util.BaseResponse;
import kuit.project.beering.util.exception.FieldValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
