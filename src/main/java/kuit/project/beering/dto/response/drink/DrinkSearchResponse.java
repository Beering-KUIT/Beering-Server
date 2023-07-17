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
    Long beer_id;
    String beer_image_url;
    String name_kr;
    String name_en;
    String manufacturer;
}
