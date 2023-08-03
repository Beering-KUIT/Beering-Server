package kuit.project.beering.dto.response.review;

import kuit.project.beering.domain.Review;
import kuit.project.beering.domain.image.Image;
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

    public ReviewReadResponseDto (Review review, String profileImageUrl, long isUpCount, long isDownCount) {
        this.memberId = review.getMember().getId();
        this.nickName = review.getMember().getNickname();
        this.profileImage = profileImageUrl;

        this.reviewId = review.getId();
        this.reviewImageUrls = review.getImages().stream()
                .map(Image::getImageUrl)
                .collect(Collectors.toList());
        this.content = review.getContent();

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime  createdAt = review.getCreatedAt();
        if(ChronoUnit.YEARS.between(createdAt, now) != 0)
            this.diffFromCurrentTime = ChronoUnit.YEARS.between(createdAt, now) + "년 전";
        else if(ChronoUnit.MONTHS.between(createdAt, now) != 0)
            this.diffFromCurrentTime = ChronoUnit.MONTHS.between(createdAt, now) + "달 전";
        else if(ChronoUnit.WEEKS.between(createdAt, now) != 0)
            this.diffFromCurrentTime = ChronoUnit.WEEKS.between(createdAt, now) + "주 전";
        else if(ChronoUnit.DAYS.between(createdAt, now) != 0)
            this.diffFromCurrentTime = ChronoUnit.DAYS.between(createdAt, now) + "일 전";
        else
            this.diffFromCurrentTime = "오늘";

        this.like = isUpCount;
        this.dislike = isDownCount;
    }
}
