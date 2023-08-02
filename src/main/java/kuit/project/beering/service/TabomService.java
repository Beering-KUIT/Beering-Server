package kuit.project.beering.service;


import kuit.project.beering.domain.Member;
import kuit.project.beering.domain.Review;
import kuit.project.beering.domain.Tabom;
import kuit.project.beering.domain.image.Image;
import kuit.project.beering.domain.image.ReviewImage;
import kuit.project.beering.dto.response.tabom.GetTabomResponse;
import kuit.project.beering.repository.MemberRepository;
import kuit.project.beering.repository.ReviewRepository;
import kuit.project.beering.repository.TabomRepository;
import kuit.project.beering.util.exception.MemberException;
import kuit.project.beering.util.exception.ReviewException;
import kuit.project.beering.util.exception.TabomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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

    public Slice<GetTabomResponse> getTabomReviews(Long memberId, Pageable pageable) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(NONE_MEMBER));

        Slice<Review> reviews = reviewRepository.findByMemberIdAndUpTabom(member.getId(), pageable);

        List<GetTabomResponse> dto = reviews.stream()
                .map(this::getTabomResponseBuild)
                .collect(Collectors.toList());

        return new SliceImpl<>(dto, pageable, reviews.hasNext());
    }

    private GetTabomResponse getTabomResponseBuild(Review review) {
        return GetTabomResponse.builder()
                .memberId(review.getMember().getId())
                .profileImage(getProfileImage(review.getMember()))
                .reviewId(review.getId())
                .reviewImages(getReviewImages(review.getImages()))
                .content(review.getContent())
                .createdAt(review.getCreatedAt())
                .like(countOfTabom(review.getId(), true))
                .dislike(countOfTabom(review.getId(), false))
                .build();
    }

    private Long countOfTabom(Long reviewId, boolean isUp) {
        return tabomRepository.countByReviewIdAndIsUp(reviewId, isUp);
    }

    private List<String> getReviewImages(List<ReviewImage> images) {
        return images.stream()
                .map(Image::getImageUrl)
                .collect(Collectors.toList());
    }

    private String getProfileImage(Member member) {
        if(member.getImages().size() == 0)
            return null;
        return member.getImages().get(0).getImageUrl();
    }

}
