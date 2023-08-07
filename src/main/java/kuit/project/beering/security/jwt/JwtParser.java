package kuit.project.beering.security.jwt;

import io.jsonwebtoken.*;
import kuit.project.beering.util.BaseResponseStatus;
import kuit.project.beering.util.exception.CustomJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtParser {

    public Claims parseUnsignedClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .build()
                    .parseClaimsJwt(getUnsignedToken(token)).getBody();
        } catch (ExpiredJwtException e) {
            throw new CustomJwtException(BaseResponseStatus.EXPIRED_ACCESS_TOKEN);
        }
    }

    public <T> T parseClaimsField(String token, String field, Class<T> tClass) {
        return parseUnsignedClaims(token).get(field, tClass);
    }

    public boolean isJwt(String token) {
        try {
            Jwts.parserBuilder().build().parse(getUnsignedToken(token));
            log.info("isjwt");
            return true;
        } catch (Exception e) {
            log.info("isnotjwt");
            return false;
        }
    }

    private String getUnsignedToken(String token) {
        String[] splitToken = token.split("\\.");
        if (splitToken.length != 3) throw new CustomJwtException(BaseResponseStatus.INVALID_TOKEN_TYPE);
        return splitToken[0] + "." + splitToken[1] + ".";
    }
}
