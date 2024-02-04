package kuit.project.beering.security.auth;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class AuthMember implements UserDetails {

    private final Long id;
    private final String username;
    private final String password;
    private final List<GrantedAuthority> authorities;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public static AuthMember GUEST(List<GrantedAuthority> authorities) {
        return AuthMember.builder()
                .id(0L)
                .username("")
                .password("")
                .authorities(authorities)
                .build();
    }

    public static AuthMember MEMBER(Long id, String username, List<GrantedAuthority> authorities) {
        return AuthMember.builder()
                .id(id)
                .username(username)
                .password("")
                .authorities(authorities)
                .build();
    }

    public boolean isGuest() {
        return authorities.contains(new SimpleGrantedAuthority("ROLE_GUEST"));
    }

    public boolean isMember() {
        return authorities.contains(new SimpleGrantedAuthority("ROLE_MEMBER"));
    }


}
