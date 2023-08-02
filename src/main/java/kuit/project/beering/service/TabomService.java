package kuit.project.beering.service;


import kuit.project.beering.domain.Member;
import kuit.project.beering.domain.Review;
import kuit.project.beering.domain.Tabom;
import kuit.project.beering.repository.MemberRepository;
import kuit.project.beering.repository.ReviewRepository;
import kuit.project.beering.repository.TabomRepository;
import kuit.project.beering.util.exception.MemberException;
import kuit.project.beering.util.exception.ReviewException;
import kuit.project.beering.util.exception.TabomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static kuit.project.beering.util.BaseResponseStatus.*;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class TabomService {
    private final TabomRepository tabomRepository;
    private final MemberRepository memberRepository;
    private final ReviewRepository reviewRepository;

    @Transactional
    public void addToTabom(Long memberId, Long reviewId, boolean isUp) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(NONE_MEMBER));

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewException(NONE_REVIEW));

        if(tabomRepository.existsByReviewIdAndMemberId(memberId, reviewId))
            throw new TabomException(POST_TABOM_ALREADY_CREATED);

        tabomRepository.save(new Tabom(member, review, isUp));
    }
}
