package kuit.project.beering.service;

import kuit.project.beering.domain.Member;
import kuit.project.beering.dto.request.MemberSignupRequest;
import kuit.project.beering.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;

    public void signup(MemberSignupRequest request) {

        memberRepository.save(
                Member.builder()
                .username(request.getUsername())
                .password(request.getPassword())
                .nickname(request.getNickname())
                .build());
    }
}
