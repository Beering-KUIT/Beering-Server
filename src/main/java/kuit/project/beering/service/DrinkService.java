package kuit.project.beering.service;

import kuit.project.beering.domain.Drink;
import kuit.project.beering.domain.Member;
import kuit.project.beering.domain.Review;
import kuit.project.beering.domain.image.Image;
import kuit.project.beering.dto.request.drink.DrinkSearchCondition;
import kuit.project.beering.dto.request.drink.SearchDrinkRequest;
import kuit.project.beering.dto.request.drink.SortType;
import kuit.project.beering.dto.response.drink.*;
import kuit.project.beering.repository.FavoriteRepository;
import kuit.project.beering.repository.ReviewRepository;
import kuit.project.beering.repository.drink.DrinkRepository;
import kuit.project.beering.util.ConvertCreatedDate;
import kuit.project.beering.util.exception.domain.DrinkException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static kuit.project.beering.util.BaseResponseStatus.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class DrinkService {
    private final DrinkRepository drinkRepository;
    private final FavoriteRepository favoriteRepository;
    private final MemberService memberService;
    private final ReviewRepository reviewRepository;

    private final int SIZE = 10;

    /**
     * 주류 검색 메소드 <br>
     * <br>
     * page 페이지 번호<br>
     * orderBy 정렬 : 이름순, 리뷰많은순, 최저가순, 평점순<br>
     * drinkSearchCondition 필터 : 이름, 카테고리이름, 하한선, 상한선<br>
     * @return 검색 결과 10개 // 페이징
     * @exception DrinkException 유효하지 않은 정렬 방식 입력시 예외 발생
     */
    public Slice<DrinkSearchResponse> searchDrinksByName(SearchDrinkRequest request, Long memberId) {
        Pageable pageable = PageRequest.of(request.getPage(), SIZE, SortType.getMatchedSort(request.getOrderBy()));

        validSubOption(request);

        DrinkSearchCondition drinkSearchCondition = new DrinkSearchCondition(
                request.getName(), request.getName(), request.getCategory(), request.getMinPrice(), request.getMaxPrice(),
                request.getTag(), request.getCountry(), request.getSweetness(), memberId);

        return drinkRepository.search(drinkSearchCondition, pageable);
    }

    private void validSubOption(SearchDrinkRequest request) {
        if(request.getCategory() == null)
            return;

        boolean invalid = false;

        // 카테고리 다중 선택임에도 하위 옵션이 선택된 경우
        if(request.getCategory().size() > 1){
            if(request.getCountry() != null || request.getSweetness() != null)
                throw new DrinkException(UNSUPPORTED_SUB_OPTION);
            else return;
        }

        String category = request.getCategory().get(0);

        // 카테고리에 따른 하위옵션 검증구간
        if(category.equals("beer") && request.getSweetness() != null) invalid = true;

        if(invalid) throw new DrinkException(INVALID_SUB_OPTION);
    }

    @Transactional
    public GetDrinkResponse getDrinkById(Long drinkId, Long memberId) {
        log.info("DrinkService.getDrinkById");
        Drink drink = drinkRepository.findById(drinkId)
                .orElseThrow(() -> new DrinkException(NONE_DRINK));

        // 유저의 주류 찜 여부
        boolean is_liked = favoriteRepository.existsByDrinkIdAndMemberId(drinkId, memberId);

        // 리뷰 프리뷰 배열
        List<ReviewPreview> reviewPreviews = getReviewPreviews(drinkId);

        return new GetDrinkResponseBuilder()
                .setDrink(drink)
                .setIsLiked(is_liked)
                .setReviewPreviews(reviewPreviews)
                .buildResponse();
    }

    private List<ReviewPreview> getReviewPreviews(Long drinkId) {
        List<Review> reviews = reviewRepository.findTop5ByDrinkIdOrderByTabomsDesc(drinkId);
        return reviews.stream()
                .map(review -> new ReviewPreview(
                        getProfileImageUrl(review),
                        review.getMember().getNickname(),
                        review.getContent(),
                        ConvertCreatedDate.setCreatedDate(review.getCreatedAt()),
                        review.getTotalRating())
                )
                .collect(Collectors.toList());
    }

    private String getProfileImageUrl(Review review){
        Member member = review.getMember();
        return memberService.getProfileImageUrl(member);
    }

    public DrinkPreview recommendDrink() {
        Random random = new Random();
        long totalDrinks = drinkRepository.count();
        long drinkId = random.nextLong(totalDrinks) + 1;

        Drink drink = drinkRepository.findById(drinkId)
                .orElseThrow(() -> new DrinkException(NONE_DRINK));

        return DrinkPreview.builder()
                .drinkId(drinkId)
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
}
