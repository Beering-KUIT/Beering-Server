package kuit.project.beering.dto.response.favorite;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class GetFavoriteDrinkResponse {
    Long drinkId;
    String nameKr;
    String nameEn;
    String manufacturer;
    String primaryImageUrl;
}
