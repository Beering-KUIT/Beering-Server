package kuit.project.beering.dto.response.drink;

import kuit.project.beering.domain.Drink;
import kuit.project.beering.domain.image.Image;
import kuit.project.beering.dto.response.ResponseBuilder;

import java.util.stream.Collectors;

public class DrinkPreviewBuilder implements ResponseBuilder<DrinkPreview> {
    private Drink drink;

    @Override
    public DrinkPreview buildResponse() {
        return DrinkPreview.builder()
                .drinkId(drink.getId())
                .nameKr(drink.getNameKr())
                .nameEn(drink.getNameEn())
                .country(drink.getCountry())
                .manufacturer(drink.getManufacturer())
                .alcohol(drink.getAlcohol())
                .AvgRating(drink.getAvgRating())
                .reviewCount(drink.getCountOfReview())
                .drinkImageUrlList(drink.getImages().stream()
                        .map(Image::getImageUrl)
                        .collect(Collectors.toList()))
                .build();
    }

    public DrinkPreviewBuilder setDrink(Drink drink) {
        this.drink = drink;
        return this;
    }

}
