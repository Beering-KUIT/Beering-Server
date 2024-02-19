package kuit.project.beering.dto.response.drink;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class GetDrinkPreviewResponse {
    Long drinkId;
    String nameKr;
    String nameEn;
    String manufacturer;
    String primaryImageUrl;
    String country;
    Float alcohol;
    Float avgRating;
    Integer countOfReview;
}
