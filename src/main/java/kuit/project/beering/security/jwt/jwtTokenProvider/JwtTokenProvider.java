package kuit.project.beering.security.jwt.jwtTokenProvider;

import kuit.project.beering.security.jwt.JwtInfo;
import org.springframework.security.core.Authentication;

public interface JwtTokenProvider {

    JwtInfo createToken(Authentication authentication);

    boolean validateToken(String token);

    Authentication getAuthentication(String token);

    Long parseMemberId(String token);

    String parseSub(String token);

}
