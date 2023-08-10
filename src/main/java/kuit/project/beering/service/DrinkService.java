package kuit.project.beering.service;

import kuit.project.beering.domain.Drink;
import kuit.project.beering.domain.Member;
import kuit.project.beering.domain.Review;
import kuit.project.beering.dto.request.drink.DrinkSearchCondition;
import kuit.project.beering.dto.request.drink.SearchDrinkRequest;
import kuit.project.beering.dto.request.drink.SortType;
import kuit.project.beering.dto.response.drink.DrinkSearchResponse;
import kuit.project.beering.dto.response.drink.GetDrinkResponse;
import kuit.project.beering.dto.response.drink.GetDrinkResponseBuilder;
import kuit.project.beering.dto.response.drink.ReviewPreview;
import kuit.project.beering.repository.FavoriteRepository;
import kuit.project.beering.repository.ReviewRepository;
import kuit.project.beering.repository.drink.DrinkRepository;
import kuit.project.beering.util.ConvertCreatedDate;
import kuit.project.beering.util.exception.DrinkException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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

//    /**
//     * 주류 검색 메소드 <br>
//     * <br>
//     * @param page 페이지 번호
//     * @param orderBy 정렬 : 이름순, 리뷰많은순, 최저가순, 평점순
//     * @param drinkSearchCondition 필터 : 이름, 카테고리이름, 하한선, 상한선
//     * @return 검색 결과 10개 // 페이징
//     * @exception DrinkException 유효하지 않은 정렬 방식 입력시 예외 발생
//     */
//    public Slice<DrinkSearchResponse> searchDrinksByName(Integer page, String orderBy, DrinkSearchCondition drinkSearchCondition) {
//        Pageable pageable = PageRequest.of(page, SIZE, SortType.getMatchedSort(orderBy));
//
//        return drinkRepository.search(drinkSearchCondition, pageable);
//    }

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
        // TODO : 생성순 -> 좋아요순으로 정렬하면 더 좋을 듯.
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


    public Slice<DrinkSearchResponse> searchDrinksByName(SearchDrinkRequest request, Long memberId) {
        Pageable pageable = PageRequest.of(request.getPage(), SIZE, SortType.getMatchedSort(request.getOrderBy()));

        DrinkSearchCondition drinkSearchCondition = new DrinkSearchCondition(
                request.getName(), request.getName(), request.getCategory(), request.getMinPrice(), request.getMaxPrice(), memberId);

        return drinkRepository.search(drinkSearchCondition, pageable);
    }
}
