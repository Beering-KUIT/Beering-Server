package kuit.project.beering.service;

import kuit.project.beering.domain.Drink;
import kuit.project.beering.domain.Image;
import kuit.project.beering.domain.Member;
import kuit.project.beering.domain.Review;
import kuit.project.beering.dto.request.drink.DrinkSearchCondition;
import kuit.project.beering.dto.response.drink.DrinkSearchResponse;
import kuit.project.beering.dto.response.drink.ReviewPreview;
import kuit.project.beering.dto.response.drink.GetDrinkResponse;
import kuit.project.beering.repository.CustomDrinkRepository;
import kuit.project.beering.repository.DrinkRepository;
import kuit.project.beering.repository.ReviewRepository;
import kuit.project.beering.util.exception.DrinkException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static kuit.project.beering.util.BaseResponseStatus.NONE_DRINK;

@Service
@RequiredArgsConstructor
@Slf4j
public class DrinkService {
    private final DrinkRepository drinkRepository;
    private final FavoriteService favoriteService;
    private final MemberService memberService;
    private final ReviewRepository reviewRepository;

    private final int SIZE = 10;

    /**
     * 주류 검색 메소드 <br>
     * <br>
     * @param page 페이지 번호
     * @param orderBy 정렬 : 이름순, 리뷰많은순, 최저가순, 평점순
     * @param drinkSearchCondition 필터 : 이름, 카테고리이름, 하한선, 상한선
     * @return 검색 결과 10개 // 페이징
     * @exception DrinkException 유효하지 않은 정렬 방식 입력시 예외 발생
     */
    public Page<DrinkSearchResponse> searchDrinksByName(Integer page, String orderBy, DrinkSearchCondition drinkSearchCondition) {
        /**
         * 정렬 적용
         */
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

//        Page<Drink> drinkPage = drinkRepository.findByNameKrContainingOrNameEnContainingIgnoreCase(name, name, pageable);
        Page<Drink> drinkPage = drinkRepository.search(drinkSearchCondition, pageable);
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
        List<Image> images = drink.getImages();
        if(!images.isEmpty()){
            return images.get(0).getImageUrl();
        }
        return null;
    }


    public GetDrinkResponse getDrinkById(Long beerId) {
        log.info("DrinkService.getDrinkById");
        Drink drink = drinkRepository.findById(beerId)
                .orElseThrow(() -> new DrinkException(NONE_DRINK));

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
                        review.getCreatedAt(),
                        review.getTotalRating())
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
        Member member = review.getMember();
        return memberService.getProfileImageUrl(member);
    }

}
