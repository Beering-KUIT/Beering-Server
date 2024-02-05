package kuit.project.beering.dto.response.drink;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class DrinkSearchResponse {
    Long drinkId;
    String nameKr;
    String nameEn;
    String manufacturer;
    Float avgRating;
    Integer countOfReview;
    List<String> imageUrlList;
    Boolean isLiked;
}
