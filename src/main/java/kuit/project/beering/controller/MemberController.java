package kuit.project.beering.controller;

import kuit.project.beering.dto.request.MemberLoginRequest;
import kuit.project.beering.dto.request.MemberSignupRequest;
import kuit.project.beering.dto.response.MemberLoginResponse;
import kuit.project.beering.service.MemberService;
import kuit.project.beering.util.BaseResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/signup")
    public BaseResponse<Object> signup(@RequestBody MemberSignupRequest request) {

        memberService.signup(request);

        return new BaseResponse<>(new Object());
    }

    @PostMapping("/login")
    public BaseResponse<MemberLoginResponse> login(@RequestBody MemberLoginRequest request) {

        MemberLoginResponse response = memberService.login(request);

        return new BaseResponse<>(response);
    }
}
