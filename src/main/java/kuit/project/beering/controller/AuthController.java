package kuit.project.beering.controller;

import kuit.project.beering.dto.request.auth.RefreshTokenRequest;
import kuit.project.beering.redis.TokenService;
import kuit.project.beering.security.jwt.JwtInfo;
import kuit.project.beering.util.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final TokenService tokenService;

    @PostMapping("/auth/token")
    public BaseResponse<JwtInfo> reissueToken(@RequestBody RefreshTokenRequest refreshToken) {
        JwtInfo jwtInfo = tokenService.reissueToken(refreshToken);

        return new BaseResponse<>(jwtInfo);
    }
}
