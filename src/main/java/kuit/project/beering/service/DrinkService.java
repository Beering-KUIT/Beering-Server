package kuit.project.beering.service;

import kuit.project.beering.domain.Drink;
import kuit.project.beering.domain.Member;
import kuit.project.beering.domain.Review;
import kuit.project.beering.dto.request.drink.DrinkSearchCondition;
import kuit.project.beering.dto.request.drink.SortType;
import kuit.project.beering.dto.response.drink.DrinkSearchResponse;
import kuit.project.beering.dto.response.drink.GetDrinkResponse;
import kuit.project.beering.dto.response.drink.ReviewPreview;
import kuit.project.beering.repository.drink.DrinkRepository;
import kuit.project.beering.repository.FavoriteRepository;
import kuit.project.beering.repository.ReviewRepository;
import kuit.project.beering.util.exception.DrinkException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static kuit.project.beering.util.BaseResponseStatus.NONE_DRINK;

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

        Pageable pageable = PageRequest.of(page, SIZE, SortType.getMatchedSort(orderBy));

        Page<DrinkSearchResponse> drinkPage = drinkRepository.search(drinkSearchCondition, pageable);

        return new PageImpl<>(drinkPage.getContent(), pageable, drinkPage.getTotalElements());
    }

    public GetDrinkResponse getDrinkById(Long drinkId, Long memberId) {
        log.info("DrinkService.getDrinkById");
        Drink drink = drinkRepository.findById(drinkId)
                .orElseThrow(() -> new DrinkException(NONE_DRINK));

        // 유저의 주류 찜 여부
        boolean is_liked = favoriteRepository.existsByDrinkIdAndMemberId(drinkId, memberId);

        // 리뷰 프리뷰 배열
        List<ReviewPreview> reviewPreviews = getReviewPreviews(drinkId);

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
                .isLiked(is_liked)
                .reviewPreviews(reviewPreviews)
                .build();
    }

    private List<ReviewPreview> getReviewPreviews(Long drinkId) {
        // TODO : 생성순 -> 좋아요순으로 정렬하면 더 좋을 듯.
        List<Review> reviews = reviewRepository.findTop5ByDrinkIdOrderByCreatedAtDesc(drinkId);
        return reviews.stream()
                .map(review -> new ReviewPreview(
                        getProfileImageUrl(review),
                        review.getMember().getNickname(),
                        review.getContent(),
                        review.getCreatedAt(),
                        review.getTotalRating())
                )
                .collect(Collectors.toList());
    }

    private String getProfileImageUrl(Review review){
        Member member = review.getMember();
        return memberService.getProfileImageUrl(member);
    }

}
