package kuit.project.beering.dto.response.reviewOption;

import kuit.project.beering.domain.ReviewOption;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class ReviewOptionReadResponseDto {

    private Long reviewOptionId;
    private String name;

    public ReviewOptionReadResponseDto(ReviewOption reviewOption) {
        this.reviewOptionId = reviewOption.getId();
        this.name = reviewOption.getName();
    }
}
