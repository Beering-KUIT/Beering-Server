package kuit.project.beering.dto.response.drink;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class DrinkSearchResponse {
    Long beerId;
    String imgUrl;
    String nameKr;
    String nameEn;
    String manufacturer;
    Boolean isLiked;
}
