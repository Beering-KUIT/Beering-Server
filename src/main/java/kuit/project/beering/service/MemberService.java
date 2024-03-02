package kuit.project.beering.service;

import kuit.project.beering.domain.Member;
import kuit.project.beering.domain.Status;
import kuit.project.beering.domain.image.MemberImage;
import kuit.project.beering.dto.common.AgreementBulkInsertDto;
import kuit.project.beering.dto.request.member.MemberLoginRequest;
import kuit.project.beering.dto.request.member.MemberSignupRequest;
import kuit.project.beering.dto.response.member.MemberEmailResponse;
import kuit.project.beering.dto.response.member.MemberInfoResponse;
import kuit.project.beering.dto.response.member.MemberLoginResponse;
import kuit.project.beering.dto.response.member.MemberNicknameResponse;
import kuit.project.beering.repository.AgreementJdbcRepository;
import kuit.project.beering.repository.ImageRepository;
import kuit.project.beering.repository.MemberRepository;
import kuit.project.beering.security.auth.AuthMember;
import kuit.project.beering.security.jwt.JwtInfo;
import kuit.project.beering.security.jwt.jwtTokenProvider.JwtTokenProvider;
import kuit.project.beering.util.BaseResponseStatus;
import kuit.project.beering.util.exception.domain.MemberException;
import kuit.project.beering.util.s3.AwsS3Uploader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final AgreementJdbcRepository agreementJdbcRepository;
    private final ImageRepository imageRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AwsS3Uploader awsS3Uploader;

    @Transactional
    public void signupForBeering(MemberSignupRequest request) {
        /**
         * @Brief 회원부터 저장, username이 중복일 경우에는 예외 발생하고 더이상 진행되지 않고 종료
         */
        signup(request);
    }

    @Transactional
    public Member signupForOAuth(MemberSignupRequest request) {

        return signup(request);
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
        JwtInfo jwtInfo = jwtTokenProvider.createToken(authentication);
        AuthMember principal = (AuthMember) authentication.getPrincipal();

        return MemberLoginResponse.builder()
                .memberId(principal.getId())
                .jwtInfo(jwtInfo)
                .build();
    }

    public String getProfileImageUrl(Member member) {
        return member.getImages().size() != 0 ? member.getImages().get(0).getImageUrl() : null;
    }

    public MemberEmailResponse checkEmail(String username) {
        return new MemberEmailResponse(!memberRepository.existsByUsername(username));
    }

    public MemberNicknameResponse checkNickname(String nickname) {
        return new MemberNicknameResponse(!memberRepository.existsByNickname(nickname));
    }

    @Transactional
    public void uploadImage(MultipartFile multipartFile, Long memberId) {

        Member member = memberRepository.findById(memberId).orElseThrow(() -> {
            throw new MemberException(BaseResponseStatus.NONE_MEMBER);
        });


        if (!multipartFile.isEmpty())
            uploadMemberImage(multipartFile, member);
    }

    public MemberInfoResponse getMemberInfo(Long id) {
        return memberRepository.findById(id).map(member ->
                        MemberInfoResponse.builder()
                                .username(member.getUsername())
                                .nickname(member.getNickname())
                                .url(getProfileImageUrl(member)).build())
                .orElseThrow(() -> new MemberException(BaseResponseStatus.NONE_MEMBER));
    }

    private Member signup(MemberSignupRequest request) {
        /**
         * @Brief 회원부터 저장, username이 중복일 경우에는 예외 발생하고 더이상 진행되지 않고 종료
         */
        if (memberRepository.existsByUsername(request.getUsername())) throw new MemberException(BaseResponseStatus.DUPLICATED_EMAIL);
        if (memberRepository.existsByNickname(request.getNickname())) throw new MemberException(BaseResponseStatus.DUPLICATED_NICKNAME);

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
                        .memberId(member.getId())
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build()
        ).toList();

        agreementJdbcRepository.bulkInsertAgreement(agreements);
        return member;
    }

    private void uploadMemberImage(MultipartFile multipartFile, Member member) {

        String uploadName = multipartFile.getOriginalFilename();

        String url = awsS3Uploader.upload(multipartFile, "member");

        if (StringUtils.hasText(getProfileImageUrl(member))) {
            awsS3Uploader.deleteImage(getProfileImageUrl(member));
            member.getImages().get(0).updateUrlAndUploadName(url, uploadName);
            return;
        }

        imageRepository.save(new MemberImage(member, url, uploadName));
    }
}
