package kuit.project.beering.dto.response.review;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ReviewSliceResponseDto {

    List<ReviewReadResponseDto> reviews;
    private long page;
    private Boolean isLast;

}
