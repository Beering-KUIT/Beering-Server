package kuit.project.beering.dto.response.review;

import kuit.project.beering.domain.Review;
import kuit.project.beering.domain.SelectedOption;
import kuit.project.beering.domain.image.Image;
import kuit.project.beering.domain.image.ReviewImage;
import kuit.project.beering.dto.response.selectedOption.SelectedOptionReadResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReviewDetailReadResponseDto {

    // 멤버 관련
    private long memberId;
    private String nickName;
    private String profileImage;

    // 리뷰 관련
    private long reviewId;
    private List<String> reviewImageUrls;
    private String content;
    private String createdAt;
    private float totalRating;

    // 셀렉티드 옵션 관련
    private List<SelectedOptionReadResponseDto> selectedOptions;

    // 따봉 관련
    private long like;
    private long dislike;
    private String isTabomed;

    public ReviewDetailReadResponseDto (Review review, String profileImageUrl, long isUpCount, long isDownCount, String isTabomed) {
        this.memberId = review.getMember().getId();
        this.nickName = review.getMember().getNickname();
        this.profileImage = profileImageUrl;

        this.reviewId = review.getId();
        this.reviewImageUrls = review.getImages().stream()
                .map(Image::getImageUrl)
                .collect(Collectors.toList());
        this.content = review.getContent();
        this.createdAt = review.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일"));
        this.totalRating = review.getTotalRating();

        this.selectedOptions = review.getSelectedOptions().stream()
                .map(SelectedOptionReadResponseDto::new)
                .collect(Collectors.toList());

        this.like = isUpCount;
        this.dislike = isDownCount;
        this.isTabomed = isTabomed;
    }
}
