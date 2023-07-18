package kuit.project.beering.dto.request.review;

import kuit.project.beering.domain.Drink;
import kuit.project.beering.domain.Member;
import kuit.project.beering.domain.Review;
import kuit.project.beering.domain.Status;
import kuit.project.beering.dto.request.selectedOption.SelectedOptionCreateRequestDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReviewCreateRequestDto {

    private String content;
    private float totalRating;
    private List<SelectedOptionCreateRequestDto> selectedOptions;

    public Review toEntity(Member member, Drink drink) {
        return Review.builder()
                .member(member)
                .drink(drink)
                .content(content)
                .totalRating(totalRating)
                .status(Status.ACTIVE)
                .build();
    }
}
