package kuit.project.beering.dto.response.review;

import kuit.project.beering.domain.Review;
import kuit.project.beering.domain.image.Image;
import kuit.project.beering.dto.response.drink.DrinkPreview;
import kuit.project.beering.util.ConvertCreatedDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReviewReadResponseDto {

    // 멤버 관련
    private long memberId;
    private String nickName;
    private String profileImage;

    // 리뷰 관련
    private long reviewId;
    private List<String> reviewImageUrls;
    private String content;
    private String diffFromCurrentTime;

    // 따봉 관련
    private long like;
    private long dislike;
    private String isTabomed;

    // 주류 관련
    private DrinkPreview drinkPreview;

    public ReviewReadResponseDto (Review review, String profileImageUrl, long isUpCount, long isDownCount, String isTabomed, DrinkPreview drinkPreview) {
        this.memberId = review.getMember().getId();
        this.nickName = review.getMember().getNickname();
        this.profileImage = profileImageUrl;

        this.reviewId = review.getId();
        this.reviewImageUrls = review.getImages().stream()
                .map(Image::getImageUrl)
                .collect(Collectors.toList());
        this.content = review.getContent();
        this.diffFromCurrentTime = ConvertCreatedDate.setCreatedDate(review.getCreatedAt());
        this.like = isUpCount;
        this.dislike = isDownCount;
        this.isTabomed = isTabomed;

        this.drinkPreview = drinkPreview;
    }
}
