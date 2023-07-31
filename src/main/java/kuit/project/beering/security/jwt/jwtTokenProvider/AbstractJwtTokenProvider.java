package kuit.project.beering.security.jwt.jwtTokenProvider;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import kuit.project.beering.util.BaseResponseStatus;
import kuit.project.beering.util.exception.CustomJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.security.Key;
import java.util.Date;

@Slf4j
public abstract class AbstractJwtTokenProvider implements JwtTokenProvider{

    @Value("${jwt-expired-in}")
    protected long JWT_EXPIRED_IN;

    protected final Key key;

    protected AbstractJwtTokenProvider(Key key) {
        this.key = key;
    }

    /**
     * @Brief 토큰 검증
     * @param token
     * @return
     */
    @Override
    public boolean validateToken(String token) {

        try {
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(key).build()
                    .parseClaimsJws(token);
            return claims.getBody().getExpiration().after(new Date());
        } catch (ExpiredJwtException e) {
            throw new CustomJwtException(BaseResponseStatus.EXPIRED_ACCESS_TOKEN);
        } catch (UnsupportedJwtException e) {
            throw new CustomJwtException(BaseResponseStatus.UNSUPPORTED_TOKEN_TYPE);
        } catch (SignatureException e) {
            throw new CustomJwtException(BaseResponseStatus.INVALID_SIGNATURE_JWT);
        } catch (MalformedJwtException e) {
            throw new CustomJwtException(BaseResponseStatus.MALFORMED_TOKEN_TYPE);
        } catch (IllegalArgumentException e) {
            throw new CustomJwtException(BaseResponseStatus.INVALID_TOKEN_TYPE);
        } catch (JwtException e) {
            log.error("[JwtTokenProvider.validateAccessToken]", e);
            throw e;
        }
    }

    /**
     * @Brief accessToke에서 유저 claims(payload) 파싱
     * @param token
     * @return Claims
     */
    protected Claims parseClaims(String token) {
        try {
            return Jwts.parserBuilder().
                    setSigningKey(key)
                    .build().parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    protected Claims parseUnsignedClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .build()
                    .parseClaimsJwt(token).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

}
