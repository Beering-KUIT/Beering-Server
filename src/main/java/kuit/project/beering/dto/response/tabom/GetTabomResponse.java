package kuit.project.beering.dto.response.tabom;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class GetTabomResponse {
    Long memberId;
    String profileImage;
    Long reviewId;
    List<String> reviewImages;
    String content;
    LocalDateTime createdAt;
    Long like;
    Long dislike;
}
