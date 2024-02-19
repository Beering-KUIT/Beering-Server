package kuit.project.beering.security.auth;

import kuit.project.beering.util.BaseResponseStatus;
import kuit.project.beering.util.exception.domain.MemberException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
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
    public Authentication authenticate(Authentication authentication) {
        log.info("AuthenticationProviderImpl 진입");

        String username = authentication.getName();
        String password = (String) authentication.getCredentials();

        UserDetails loginMember = service.loadUserByUsername(username);

        if (!bCryptPasswordEncoder.matches(password, loginMember.getPassword())) {
            throw new MemberException(BaseResponseStatus.INVALID_CHECKED_PASSWORD);
        }

        return new UsernamePasswordAuthenticationToken(loginMember, password, loginMember.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
