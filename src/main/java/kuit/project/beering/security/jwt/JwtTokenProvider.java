package kuit.project.beering.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import kuit.project.beering.security.auth.AuthMember;
import kuit.project.beering.util.BaseResponseStatus;
import kuit.project.beering.util.CustomJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class JwtTokenProvider {

    @Value("${jwt-expired-in}")
    private long JWT_EXPIRED_IN;

    private final Key key;

    public JwtTokenProvider(@Value("${jwt-secret-key}") String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * @Brief 토큰 발급
     * @param authentication
     * @return JwtInfo - accessToken, refreshToken
     */
    public JwtInfo createToken(Authentication authentication) {

        String authorities = parseAuthorities(authentication);

        String accessToken = Jwts.builder()
                .setSubject(authentication.getName())
                .claim("auth", authorities)
                .claim("userId", ((AuthMember) authentication.getPrincipal()).getId())
                .setExpiration(new Date(System.currentTimeMillis() + JWT_EXPIRED_IN))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        String refreshToken = Jwts.builder()
                .claim("userId", ((AuthMember) authentication.getPrincipal()).getId())
                .setExpiration(new Date(System.currentTimeMillis() + JWT_EXPIRED_IN))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        return JwtInfo.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
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
     * @Brief accessToken 에서 인증(Authentication) 파싱
     * @param accessToken
     * @return Authentication
     */
    public Authentication getAuthentication(String accessToken) {
        Claims claims = parseClaims(accessToken);

        if (claims.get("auth") == null) throw new RuntimeException("권한없음");

        List<SimpleGrantedAuthority> authorities =
                Arrays.stream(claims.get("auth").toString().split(","))
                .map(SimpleGrantedAuthority::new).toList();

        UserDetails authMember =
                AuthMember.builder()
                .id(claims.get("memberId", Long.class))
                .username(claims.getSubject())
                .password("")
                .build();

        return new UsernamePasswordAuthenticationToken(authMember, "", authorities);
    }

    /**
     * @Brief Authentication에서 유저 인증 권한 파싱
     * @param authentication
     * @return String
     */
    private String parseAuthorities(Authentication authentication) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        return authorities;
    }

    /**
     * @Brief accessToke에서 유저 claims(payload) 파싱
     * @param accessToken
     * @return Claims
     */
    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder().
                    setSigningKey(key)
                    .build().parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

}
