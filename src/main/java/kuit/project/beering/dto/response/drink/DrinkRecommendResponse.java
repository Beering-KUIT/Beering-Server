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
public class DrinkRecommendResponse {

    Long drinkId;
    String nameKr;
    String nameEn;
    String description;
    String manufacturer;
    float alcohol;
    float AvgRating;
    int reviewCount;
    List<String> drinkImageUrlList;
}
