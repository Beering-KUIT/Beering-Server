package kuit.project.beering.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kuit.project.beering.domain.Role;
import kuit.project.beering.security.auth.AuthMember;
import kuit.project.beering.security.jwt.jwtTokenProvider.JwtTokenProvider;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@AllArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = resolveToken(request);

        /**
         * @Breif 토큰 검증 후 Spring Security Context에 인증 정보 담음
         * @Condition 토큰 값이 존재하고, 검증 되었으면 실행
         */
        tokenValidateAndAuthorization(token);

        filterChain.doFilter(request,response);
    }

    /**
     * @Brief 토큰 파싱하여 Bearer 타입인지 확인하고 그 부분 잘라내서 반환
     * @return 토큰 값(Bearer 제외)
     */
    private String resolveToken(HttpServletRequest request) {
        String authorization = request.getHeader(AUTHORIZATION);
        if (StringUtils.hasText(authorization) && authorization.startsWith(jwtTokenProvider.BEARER)) {
            return authorization.substring(jwtTokenProvider.BEARER.length());
        }

        return null;
    }

    /**
     * @Brief 토큰 검증하고 인가 처리
     * @param token
     */
    private void tokenValidateAndAuthorization(String token) {

        if (token == null) {
            setAuthentication(createGuestAuthentication());
            return;
        }

        if (jwtTokenProvider.validateToken(token)) {
            setAuthentication(jwtTokenProvider.getAuthentication(token));
        }

    }

    private void setAuthentication(Authentication authentication) {
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private static Authentication createGuestAuthentication() {
        List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList(Role.GUEST.getRole());

        return new UsernamePasswordAuthenticationToken(
                AuthMember.GUEST(authorities),
                "", authorities);
    }
}
