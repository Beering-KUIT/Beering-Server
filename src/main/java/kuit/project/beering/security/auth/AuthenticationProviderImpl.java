package kuit.project.beering.security.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * @brief Spring Security 에서 로그인 기능
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AuthenticationProviderImpl implements AuthenticationProvider {

    private final UserDetailsService service;
    private final PasswordEncoder bCryptPasswordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        log.info("AuthenticationProviderImpl 진입");

        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) authentication;
        String username = token.getName();
        String password = (String) token.getCredentials();

        AuthMember loginMember = (AuthMember) service.loadUserByUsername(username);

        if (!bCryptPasswordEncoder.matches(password, loginMember.getPassword())) {
            throw new BadCredentialsException(loginMember.getUsername() + " 비밀번호를 확인해주세요.");
        }

        return new UsernamePasswordAuthenticationToken(loginMember, password, loginMember.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
