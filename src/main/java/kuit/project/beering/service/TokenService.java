package kuit.project.beering.service;

import kuit.project.beering.dto.request.auth.RefreshTokenRequest;
import kuit.project.beering.repository.MemberRepository;
import kuit.project.beering.security.jwt.JwtInfo;
import kuit.project.beering.security.jwt.JwtTokenProviderResolver;
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

    private final MemberRepository memberRepository;
    private final JwtTokenProviderResolver jwtTokenProviderResolver;

    @Transactional(noRollbackFor = CustomJwtException.class)
    public JwtInfo reissueToken(RefreshTokenRequest request) {

        String refreshToken = request.getRefreshToken();

        JwtTokenProvider jwtTokenProvider = jwtTokenProviderResolver.getProvider(refreshToken);

        //todo 리턴값으로 멤버아이디가 필요할까?
        String memberId = jwtTokenProvider.validateRefreshToken(refreshToken);

        // 3. 액세스 토큰, 리프레시 토큰 재발급
        JwtInfo jwtInfo = jwtTokenProvider.reissueJwtToken(refreshToken);

        return jwtInfo;
    }

}
