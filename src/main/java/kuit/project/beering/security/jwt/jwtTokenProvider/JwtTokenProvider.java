package kuit.project.beering.security.jwt.jwtTokenProvider;

import kuit.project.beering.security.jwt.JwtInfo;
import org.springframework.security.core.Authentication;

public interface JwtTokenProvider {

    boolean validateToken(String token);

    Authentication getAuthentication(String token);

    JwtInfo reissueJwtToken(String refreshToken);

    String parseSub(String token);

    String parseIssuer(String token);

    String parseEmail(String token);

    String validateRefreshToken(String refreshToken);
}
