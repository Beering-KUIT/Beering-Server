package kuit.project.beering.redis;

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

    public void save(RefreshToken refreshToken) {
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(refreshToken.getMemberId(), refreshToken.getRefreshToken());
        redisTemplate.expire(refreshToken.getRefreshToken(), JWT_EXPIRED_IN * 7, TimeUnit.SECONDS);
    }

    public Optional<RefreshToken> findById(String accessToken) {
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        String refreshToken = String.valueOf(valueOperations.get(accessToken));

        if (Objects.isNull(refreshToken)) {
            return Optional.empty();
        }

        return Optional.of(new RefreshToken(accessToken, refreshToken));
    }

    public void delete(String memberId) {
        redisTemplate.delete(memberId);
    }
}
