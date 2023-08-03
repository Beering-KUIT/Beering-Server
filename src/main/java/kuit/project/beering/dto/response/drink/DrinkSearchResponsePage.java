package kuit.project.beering.dto.response.drink;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class DrinkSearchResponsePage {
    List<DrinkSearchResponse> drinks;
    int page;
    boolean isLast;
}
