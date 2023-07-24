package kuit.project.beering.service;

import jakarta.persistence.EntityNotFoundException;
import kuit.project.beering.domain.Member;
import kuit.project.beering.dto.request.auth.RefreshTokenRequest;
import kuit.project.beering.redis.RefreshToken;
import kuit.project.beering.repository.MemberRepository;
import kuit.project.beering.repository.RefreshTokenRepository;
import kuit.project.beering.security.jwt.JwtInfo;
import kuit.project.beering.security.jwt.JwtTokenProvider;
import kuit.project.beering.util.BaseResponseStatus;
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
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional(noRollbackFor = CustomJwtException.class)
    public JwtInfo reissueToken(RefreshTokenRequest request) {

        String refreshToken = request.getRefreshToken();

        String memberId = validateRefreshToken(refreshToken);

        // 3. 액세스 토큰, 리프레시 토큰 재발급
        JwtInfo jwtInfo = jwtTokenProvider.createToken(jwtTokenProvider.getAuthentication(refreshToken));
        refreshTokenRepository.save(new RefreshToken(memberId, jwtInfo.getRefreshToken()));

        return jwtInfo;
    }

    /**
     * @Brief 토큰 검증 - 자체 검증(서명, 만료일 등), redis 비교하여 보안 검증 - 탈취 토큰 사용 감지 시 Member 휴먼으로
     */
    private String validateRefreshToken(String refreshToken) {
        // 리프레시 토큰 자체 검증

        jwtTokenProvider.validateToken(refreshToken);

        // 리프레시 토큰 2차 검증 - redis 와 비교
        Long memberId = jwtTokenProvider.parseMemberId(refreshToken);

        RefreshToken redisRefreshToken = refreshTokenRepository
                .findById(String.valueOf(memberId))
                .orElseThrow(() -> new CustomJwtException(BaseResponseStatus.EXPIRED_REFRESH_TOKEN));

        if (!refreshToken.equals(redisRefreshToken.getRefreshToken())) {
            // 보안 이슈 발생!! -> 사용자 status 변경
            Member findMember = memberRepository.findById(memberId).orElseThrow(EntityNotFoundException::new);
            findMember.UpdateStatusToDormant();
            memberRepository.save(findMember);
            System.out.println("findMember.getStatus() = " + findMember.getStatus());

            log.error("보안 이슈 발생 memberId = {}", memberId);
            refreshTokenRepository.delete(refreshToken);
            throw new CustomJwtException(BaseResponseStatus.USING_STOLEN_TOKEN);
        }

        return String.valueOf(memberId);
    }

}
