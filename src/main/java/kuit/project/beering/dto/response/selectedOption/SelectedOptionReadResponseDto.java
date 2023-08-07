package kuit.project.beering.dto.response.selectedOption;

import kuit.project.beering.domain.SelectedOption;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SelectedOptionReadResponseDto {

    private String name;
    private float rating;

    public SelectedOptionReadResponseDto (SelectedOption selectedOption) {
        this.name = selectedOption.getReviewOption().getName();
        this.rating = selectedOption.getRating();
    }
}
