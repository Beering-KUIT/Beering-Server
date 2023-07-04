package kuit.project.beering.service;

import kuit.project.beering.domain.Member;
import kuit.project.beering.dto.request.member.MemberLoginRequest;
import kuit.project.beering.dto.request.member.MemberSignupRequest;
import kuit.project.beering.dto.response.member.MemberLoginResponse;
import kuit.project.beering.repository.MemberRepository;
import kuit.project.beering.security.auth.AuthMember;
import kuit.project.beering.security.jwt.JwtInfo;
import kuit.project.beering.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;


    public void signup(MemberSignupRequest request) {

        memberRepository.save(
                Member.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .nickname(request.getNickname())
                .build());
    }

    public MemberLoginResponse login(MemberLoginRequest request) {
        /**
         * @Brief username, password 기반으로 인증 객체 생성
         *        Authentication 의 authenticated 는 false 상태임. 즉 아직 미인증 상태
         */
        Authentication authenticationToken =
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(), request.getPassword());

        /**
         * @Brief username, password 체크/검증 이 이뤄지는 단계.
         *        이 시점에서 AuthenticationProviderImpl, UserDetailsServiceImpl 실행
         */
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        /**
         * @Brief 인증 정보 기반으로 토큰 생성.
         */
        JwtInfo jwtInfo = jwtTokenProvider.createToken(authentication);
        AuthMember principal = (AuthMember) authentication.getPrincipal();

        return MemberLoginResponse.builder()
                .memberId(principal.getId())
                .jwtInfo(jwtInfo)
                .build();
    }
}
