package kuit.project.beering.dto.response.favorite;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class GetFavoriteDrinkResponsePage {
    List<GetFavoriteDrinkResponse> drinks;
    int page;
    boolean isLast;
}
