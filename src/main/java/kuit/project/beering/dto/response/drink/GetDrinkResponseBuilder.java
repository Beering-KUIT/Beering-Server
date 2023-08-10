package kuit.project.beering.dto.response.drink;

import kuit.project.beering.domain.Drink;
import kuit.project.beering.domain.image.DrinkImage;
import kuit.project.beering.domain.image.Image;
import kuit.project.beering.dto.response.ResponseBuilder;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

public class GetDrinkResponseBuilder implements ResponseBuilder<GetDrinkResponse> {
    private Drink drink;
    private boolean isLiked;
    private List<ReviewPreview> reviewPreviews;

    @Override
    public GetDrinkResponse buildResponse() {
        return GetDrinkResponse.builder()
                .drinkId(drink.getId())
                .nameKr(drink.getNameKr())
                .nameEn(drink.getNameEn())
                .price(drink.getPrice())
                .alcohol(drink.getAlcohol())
                .description(drink.getDescription())
                .manufacturer(drink.getManufacturer())
                .totalRating(drink.getAvgRating())
                .reviewCount(drink.getCountOfReview())
                .isLiked(isLiked)
                .reviewPreviews(reviewPreviews)
                .drinkImageUrlList(getDrinkImages(drink.getImages()))
                .build();
    }

    private static List<String> getDrinkImages(List<DrinkImage> images) {
        return images.stream()
                .map(Image::getImageUrl)
                .collect(Collectors.toList());
    }

    public GetDrinkResponseBuilder setDrink(Drink drink) {
        this.drink = drink;
        return this;
    }
    public GetDrinkResponseBuilder setIsLiked(boolean isLiked) {
        this.isLiked = isLiked;
        return this;
    }
    public GetDrinkResponseBuilder setReviewPreviews(List<ReviewPreview> reviewPreviews) {
        this.reviewPreviews = reviewPreviews;
        return this;
    }
}
