package kuit.project.beering.security.jwt.jwtTokenProvider;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import kuit.project.beering.security.auth.AuthMember;
import kuit.project.beering.security.jwt.JwtInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class BasicJwtTokenProvider extends AbstractJwtTokenProvider {

    public BasicJwtTokenProvider(@Value("${jwt-secret-key}") String secretKey) {
        super(Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey)));
    }

    /**
     * @Brief 토큰 발급
     * @param authentication
     * @return JwtInfo - accessToken, refreshToken
     */
    @Override
    public JwtInfo createToken(Authentication authentication) {

        String accessToken = Jwts.builder()
                .setSubject(authentication.getName())
                .claim("iss", "https://beering.com")
                .claim("memberId", ((AuthMember) authentication.getPrincipal()).getId())
                .setExpiration(new Date(System.currentTimeMillis() + JWT_EXPIRED_IN))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        String refreshToken = Jwts.builder()
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
     * @Brief token 에서 인증(Authentication) 파싱
     * @param token
     * @return Authentication
     */
    @Override
    public Authentication getAuthentication(String token) {
        Claims claims = parseClaims(token);

        UserDetails authMember =
                AuthMember.builder()
                .id(claims.get("memberId", Long.class))
                .username(claims.getSubject())
                .password("")
                .build();

        return new UsernamePasswordAuthenticationToken(authMember, "", new ArrayList<>());
    }

    @Override
    public Long parseMemberId(String token) {
        return parseClaims(token).get("memberId", Long.class);
    }

    @Override
    public String parseSub(String token) {
         return parseClaims(token).get("sub", String.class);
    }


}
