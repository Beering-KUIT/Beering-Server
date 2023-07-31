package kuit.project.beering.security.jwt.jwtTokenProvider;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import kuit.project.beering.security.auth.AuthMember;
import kuit.project.beering.security.jwt.JwtInfo;
import kuit.project.beering.util.BaseResponseStatus;
import kuit.project.beering.util.exception.CustomJwtException;
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
public class BasicJwtTokenProvider implements JwtTokenProvider{

    @Value("${jwt-expired-in}")
    private long JWT_EXPIRED_IN;

    private final Key key;

    public BasicJwtTokenProvider(@Value("${jwt-secret-key}") String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * @Brief 토큰 발급
     * @param authentication
     * @return JwtInfo - accessToken, refreshToken
     */
    @Override
    public JwtInfo createToken(Authentication authentication) {

        String authorities = parseAuthorities(authentication);

        String accessToken = Jwts.builder()
                .setSubject(authentication.getName())
                .claim("auth", authorities)
                .claim("iss", "https://beering.com")
                .claim("memberId", ((AuthMember) authentication.getPrincipal()).getId())
                .setExpiration(new Date(System.currentTimeMillis() + JWT_EXPIRED_IN))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        String refreshToken = Jwts.builder()
                .claim("auth", authorities)
                .claim("iss", "https://beering.com")
                .claim("memberId", ((AuthMember) authentication.getPrincipal()).getId())
                .setExpiration(new Date(System.currentTimeMillis() + JWT_EXPIRED_IN * 7))
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
     * @Brief token 에서 인증(Authentication) 파싱
     * @param token
     * @return Authentication
     */
    @Override
    public Authentication getAuthentication(String token) {
        Claims claims = parseClaims(token);

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

    @Override
    public Long parseMemberId(String token) {
        return parseClaims(token).get("memberId", Long.class);
    }

    @Override
    public String parseSub(String token) {
         return parseClaims(token).get("sub", String.class);
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
     * @param token
     * @return Claims
     */
    private Claims parseClaims(String token) {
        try {
            return Jwts.parserBuilder().
                    setSigningKey(key)
                    .build().parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    private Claims getUnsignedTokenClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .build()
                    .parseClaimsJwt(token).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }
}
