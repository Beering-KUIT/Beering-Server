package kuit.project.beering.security.jwt.jwtTokenProvider;

import io.jsonwebtoken.Claims;
import kuit.project.beering.security.jwt.JwtParser;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public abstract class AbstractJwtTokenProvider implements JwtTokenProvider{

    private final JwtParser jwtParser;

    protected Claims parseUnsignedClaims(String token) {
        return jwtParser.parseUnsignedClaims(token);
    }

    protected <T> T parseClaimsField(String token, String fieldName, Class<T> tClass) {
        return jwtParser.parseClaimsField(token, fieldName, tClass);
    }

    @Override
    public String parseIssuer(String token) {
        return jwtParser.parseClaimsField(token, "iss", String.class);
    }
}
