package kuit.project.beering.security.auth;

import kuit.project.beering.domain.Member;
import kuit.project.beering.repository.MemberRepository;
import kuit.project.beering.util.BaseResponseStatus;
import kuit.project.beering.util.exception.domain.MemberException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {
        log.info("UserDetailsServiceImpl 진입");
        Member member = memberRepository.findByUsername(username).orElseThrow(() ->
                new MemberException(BaseResponseStatus.NONE_MEMBER));

        List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList("ROLE_MEMBER");

        return AuthMember.MEMBER(
                member.getId(),
                member.getUsername(),
                member.getPassword(),
                authorities
        );

    }
}
