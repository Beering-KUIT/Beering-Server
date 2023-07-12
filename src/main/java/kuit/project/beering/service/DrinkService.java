package kuit.project.beering.service;

import kuit.project.beering.domain.Drink;
import kuit.project.beering.domain.Image;
import kuit.project.beering.domain.Member;
import kuit.project.beering.domain.Review;
import kuit.project.beering.dto.response.drink.DrinkSearchResponse;
import kuit.project.beering.dto.response.drink.ReviewPreview;
import kuit.project.beering.dto.response.drink.GetDrinkResponse;
import kuit.project.beering.repository.DrinkRepository;
import kuit.project.beering.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DrinkService {
    private final DrinkRepository drinkRepository;
    private final FavoriteService favoriteService;
    private final ReviewRepository reviewRepository;

    private final int SIZE = 10;

    public Page<DrinkSearchResponse> searchDrinksByName(String name, String orderBy, int page) {
        /* 페이징 & 정렬 설정 */
        Sort.Direction sortDirection = Sort.Direction.ASC;
        Sort.Order order = new Sort.Order(sortDirection, "createdAt");

        switch (orderBy) {
            case "name" -> {
                order = new Sort.Order(sortDirection, "nameKr");
            }
            case "rating" -> {
                sortDirection = Sort.Direction.DESC;
                order = new Sort.Order(sortDirection, "totalRating");
            }
            case "review" -> {
                sortDirection = Sort.Direction.DESC;
                order = new Sort.Order(sortDirection, "countOfReview");
            }
            case "price" -> {
                order = new Sort.Order(sortDirection, "price");
            }
        }

        Pageable pageable = PageRequest.of(page, SIZE, Sort.by(order));

        Page<Drink> drinkPage = drinkRepository.findByNameKrContainingOrNameEnContainingIgnoreCase(name, name, pageable);
        List<Drink> drinkList = drinkPage.getContent();

        List<DrinkSearchResponse> responseList;

        responseList = drinkList.stream()
                        .map(drink -> new DrinkSearchResponse(
                            drink.getId(),
                            getTop1DrinkImgUrl(drink),
                            drink.getNameKr(),
                            drink.getNameEn(),
                            drink.getManufacturer()))
                        .collect(Collectors.toList());

        return new PageImpl<>(responseList, pageable, drinkPage.getTotalElements());
    }

    private String getTop1DrinkImgUrl(Drink drink){
        String imgUrl = null;
        List<Image> images;
        if((images = drink.getImages()).size()!=0){
            imgUrl = images.get(0).getImageUrl();
        }
        return imgUrl;
    }


    public GetDrinkResponse getDrinkById(Long beerId) {
        log.info("DrinkService.getDrinkById");
        Drink drink = drinkRepository.findById(beerId).get();

        // 유저의 주류 찜 여부
        boolean is_liked = favoriteService.is_liked(drink.getId());

        // 리뷰 프리뷰 배열
        List<Review> reviews = reviewRepository.findTop5ByDrinkIdOrderByCreatedAtDesc(beerId);
        List<ReviewPreview> reviewPreviews;
        reviewPreviews = reviews.stream()
                .map(review -> new ReviewPreview(
                        getProfileImageUrl(review),
                        review.getMember().getNickname(),
                        review.getContent(),
                        review.getCreatedAt())
                )
                .collect(Collectors.toList());

        return GetDrinkResponse.builder()
                .beerId(drink.getId())
                .nameKr(drink.getNameKr())
                .nameEn(drink.getNameEn())
                .price(drink.getPrice())
                .alcohol(drink.getAlcohol())
                .description(drink.getDescription())
                .manufacturer(drink.getManufacturer())
                .totalRating(drink.getAvgRating())
                .reviewCount(drink.getCountOfReview())
                .isLiked(is_liked)
                .reviewPreviews(reviewPreviews)
                .build();
    }

    private String getProfileImageUrl(Review review){
        String imgUrl = null;
        Member member;
        Image img;
        if((member = review.getMember())!=null){
            if((img = member.getImage())!=null){
                imgUrl = img.getImageUrl();
            }
        }
        return imgUrl;
    }

}
