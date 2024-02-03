package kuit.project.beering.service;

import kuit.project.beering.dto.request.auth.RefreshTokenRequest;
import kuit.project.beering.security.jwt.JwtInfo;
import kuit.project.beering.security.jwt.jwtTokenProvider.JwtTokenProvider;
import kuit.project.beering.util.exception.CustomJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class TokenService {

    private final JwtTokenProvider jwtTokenProvider;

    @Transactional(noRollbackFor = CustomJwtException.class)
    public JwtInfo reissueToken(RefreshTokenRequest request) {

        return jwtTokenProvider.reissueJwtToken(request.getRefreshToken());
    }

}
