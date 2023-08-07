package kuit.project.beering.service;

import kuit.project.beering.domain.image.Image;
import kuit.project.beering.domain.Member;
import kuit.project.beering.domain.Status;
import kuit.project.beering.domain.image.MemberImage;
import kuit.project.beering.dto.AgreementBulkInsertDto;
import kuit.project.beering.dto.request.member.MemberLoginRequest;
import kuit.project.beering.dto.request.member.MemberSignupRequest;
import kuit.project.beering.dto.response.member.MemberEmailResponse;
import kuit.project.beering.dto.response.member.MemberLoginResponse;
import kuit.project.beering.dto.response.member.MemberNicknameResponse;
import kuit.project.beering.redis.RefreshToken;
import kuit.project.beering.repository.RefreshTokenRepository;
import kuit.project.beering.repository.AgreementJdbcRepository;
import kuit.project.beering.repository.MemberRepository;
import kuit.project.beering.security.auth.AuthMember;
import kuit.project.beering.security.jwt.JwtInfo;
import kuit.project.beering.security.jwt.jwtTokenProvider.BeeringJwtTokenProvider;
import kuit.project.beering.util.exception.DuplicateNicknameException;
import kuit.project.beering.util.exception.DuplicateUsernameException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final AgreementJdbcRepository agreementJdbcRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final PasswordEncoder passwordEncoder;
    private final BeeringJwtTokenProvider beeringJwtTokenProvider;

    @Transactional
    public void signup(MemberSignupRequest request) {
        /**
         * @Brief 회원부터 저장, username이 중복일 경우에는 예외 발생하고 더이상 진행되지 않고 종료
         */
        checkEmail(request.getUsername());
        checkNickname(request.getNickname());

        Member member = memberRepository.saveAndFlush(
                Member.createMember(
                        request.getUsername(),
                        passwordEncoder.encode(request.getPassword()),
                        request.getNickname()));

        /**
         * @Brief RequestDto 사용해서 AgreementBulkInsertDto 생성
         */
        List<AgreementBulkInsertDto> agreements = request.getAgreements().stream().map(
                agreementRequest -> AgreementBulkInsertDto.builder()
                        .name(agreementRequest.getName().name())
                        .isAgreed(agreementRequest.getIsAgreed())
                        .status(Status.ACTIVE.name())
                        .memberId(member.getId()).build()
        ).toList();

        agreementJdbcRepository.bulkInsertAgreement(agreements);
    }

    @Transactional
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
        JwtInfo jwtInfo = beeringJwtTokenProvider.createToken(authentication);
        AuthMember principal = (AuthMember) authentication.getPrincipal();

        refreshTokenRepository.save(new RefreshToken(String.valueOf(principal.getId()), jwtInfo.getRefreshToken()));

        return MemberLoginResponse.builder()
                .memberId(principal.getId())
                .jwtInfo(jwtInfo)
                .build();
    }

    public String getProfileImageUrl(Member member) {
        return member.getImages().size() != 0 ? member.getImages().get(0).getImageUrl() : null;
    }

    public MemberEmailResponse checkEmail(String username) {
        if (memberRepository.existsByUsername(username)) throw new DuplicateUsernameException();
        return new MemberEmailResponse(true);
    }

    public MemberNicknameResponse checkNickname(String nickname) {
        if (memberRepository.existsByNickname(nickname)) throw new DuplicateNicknameException();
        return new MemberNicknameResponse(true);
    }
}
