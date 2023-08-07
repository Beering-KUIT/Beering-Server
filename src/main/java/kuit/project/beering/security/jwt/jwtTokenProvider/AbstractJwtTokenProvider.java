package kuit.project.beering.security.jwt.jwtTokenProvider;

import io.jsonwebtoken.Claims;
import kuit.project.beering.security.jwt.JwtParser;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public abstract class AbstractJwtTokenProvider implements JwtTokenProvider{

    protected final JwtParser jwtParser;

    protected Claims parseUnsignedClaims(String token) {
        return jwtParser.parseUnsignedClaims(token);
    }
}
