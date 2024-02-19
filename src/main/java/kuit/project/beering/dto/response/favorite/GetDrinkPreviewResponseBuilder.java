package kuit.project.beering.dto.response.favorite;

import kuit.project.beering.domain.Drink;
import kuit.project.beering.dto.response.ResponseBuilder;

public class GetDrinkPreviewResponseBuilder{

    private Drink drink;

    public static GetDrinkPreviewResponse build(Drink drink){
        return GetDrinkPreviewResponse.builder()
                .drinkId(drink.getId())
                .nameKr(drink.getNameKr())
                .nameEn(drink.getNameEn())
                .alcohol(drink.getAlcohol())
                .manufacturer(drink.getManufacturer())
                .country(drink.getCountry())
                .avgRating(drink.getAvgRating())
                .countOfReview(drink.getCountOfReview())
                .primaryImageUrl(getPrimaryImage(drink))
                .build();
    }

    private static String getPrimaryImage(Drink drink) {
        if(drink.getImages().size() == 0)
            return null;
        return drink.getImages().get(0).getImageUrl();
    }
}
