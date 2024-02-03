package kuit.project.beering.security.jwt.jwtTokenProvider;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import kuit.project.beering.security.auth.AuthMember;
import kuit.project.beering.security.jwt.JwtInfo;
import kuit.project.beering.security.jwt.JwtParser;
import kuit.project.beering.util.BaseResponseStatus;
import kuit.project.beering.util.exception.CustomJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.ArrayList;
import java.util.Date;

@Slf4j
@Component
public class JwtTokenProvider {

    @Value("${jwt-expired-in}")
    private long JWT_EXPIRED_IN;

    @Value("${jwt-refresh-expired-in}")
    private long JWT_REFRESH_EXPIRED_IN;

    private final Key key;

    private final String BEARER = "Bearer";
    private final JwtParser jwtParser;

    public JwtTokenProvider(@Value("${jwt-secret-key}") String secretKey, JwtParser jwtParser) {
        this.jwtParser = jwtParser;
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
    }

    /**
     * @Brief 토큰 검증
     * @param token
     * @return
     */
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
     * @Brief 토큰 발급
     * @param authentication
     * @return JwtInfo - accessToken, refreshToken
     */
    public JwtInfo createToken(Authentication authentication) {

        AuthMember authMember = (AuthMember) authentication.getPrincipal();

        String accessToken = Jwts.builder()
                .setSubject(authentication.getName())
                .claim("iss", "https://beering.com")
                .claim("sub", authMember.getId())
                .claim("email", authMember.getUsername())
                .setExpiration(new Date(System.currentTimeMillis() + JWT_EXPIRED_IN))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        String refreshToken = Jwts.builder()
                .claim("iss", "https://beering.com")
                .claim("sub", authMember.getId())
                .claim("email", authMember.getUsername())
                .setExpiration(new Date(System.currentTimeMillis() + JWT_REFRESH_EXPIRED_IN))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        return JwtInfo.builder()
                .accessToken(BEARER + accessToken)
                .refreshToken(BEARER + refreshToken)
                .build();
    }

    /**
     * @Brief token 에서 인증(Authentication) 파싱
     * @param token
     * @return Authentication
     */
    public Authentication getAuthentication(String token) {
        Claims claims = jwtParser.parseUnsignedClaims(token);

        UserDetails authMember =
                AuthMember.builder()
                .id(claims.get("sub", Long.class))
                .username(claims.getSubject())
                .password("")
                .build();

        return new UsernamePasswordAuthenticationToken(authMember, "", new ArrayList<>());
    }

    public JwtInfo reissueJwtToken(String refreshToken) {
        validateRefreshToken(refreshToken);
        return createToken(getAuthentication(refreshToken));
    }

    /**
     * @Brief 리프레시 토큰 검증
     * @return memberId
     */
    private void validateRefreshToken(String refreshToken) {
        // 리프레시 토큰 자체 검증
        try {
            validateToken(refreshToken);
        } catch (CustomJwtException ex) {
            if (ex.getStatus().equals(BaseResponseStatus.EXPIRED_ACCESS_TOKEN)) {
                throw new CustomJwtException(BaseResponseStatus.EXPIRED_REFRESH_TOKEN);
            }
            throw ex;
        }
    }

}