package kuit.project.beering.dto.request.selectedOption;

import kuit.project.beering.domain.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SelectedOptionCreateRequestDto {
    private long reviewOptionId;
    private float rating;

    public SelectedOption toEntity(Review review, ReviewOption reviewOption) {
        return SelectedOption.builder()
                .reviewOption(reviewOption)
                .review(review)
                .rating(rating)
                .status(Status.ACTIVE)
                .build();
    }
}
