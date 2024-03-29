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
public class DrinkPreview {

    Long drinkId;
    String nameKr;
    String nameEn;
    String country;
    String manufacturer;
    float alcohol;
    float AvgRating;
    int reviewCount;
    List<String> drinkImageUrlList;
}
