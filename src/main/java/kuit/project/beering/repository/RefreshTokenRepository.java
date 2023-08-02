package kuit.project.beering.repository;

import kuit.project.beering.redis.RefreshToken;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepository {

    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${jwt-expired-in}")
    private long JWT_EXPIRED_IN;

    /**
     * @Brief 저장 및 수정(덮어쓰기). 키, 밸류 및 유효시간 설정
     * @param refreshToken
     */
    public void save(RefreshToken refreshToken) {
        redisTemplate.opsForValue()
                .set(refreshToken.getMemberId(), refreshToken.getRefreshToken(),
                        JWT_EXPIRED_IN * 7, TimeUnit.MILLISECONDS);
    }

    public Optional<RefreshToken> findById(String memberId) {
        String refreshToken = String.valueOf(redisTemplate.opsForValue().get(memberId));

        if (Objects.isNull(refreshToken)) {
            return Optional.empty();
        }

        return Optional.of(new RefreshToken(memberId, refreshToken));
    }

    public void delete(String memberId) {
        redisTemplate.delete(memberId);
    }
}
