package kuit.project.beering.controller;

import kuit.project.beering.dto.request.MemberSignupRequest;
import kuit.project.beering.service.MemberService;
import kuit.project.beering.util.BaseResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/signup")
    public BaseResponse<Object> signup(@RequestBody MemberSignupRequest request) {

        memberService.signup(request);

        return new BaseResponse<>(new Object());
    }
}