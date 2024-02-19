package kuit.project.beering.controller;

import kuit.project.beering.dto.request.drink.SearchDrinkRequest;
import kuit.project.beering.dto.response.SliceResponse;
import kuit.project.beering.dto.response.drink.DrinkRecommendResponse;
import kuit.project.beering.dto.response.drink.DrinkSearchResponse;
import kuit.project.beering.dto.response.drink.GetDrinkResponse;
import kuit.project.beering.dto.response.drink.GetDrinkPreviewResponse;
import kuit.project.beering.security.auth.AuthMember;
import kuit.project.beering.service.DrinkService;
import kuit.project.beering.util.BaseResponse;
import kuit.project.beering.util.exception.domain.DrinkException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import static kuit.project.beering.util.CheckMember.validateMember;
import static kuit.project.beering.util.CheckMember.getMemberId;


@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/drinks")
public class DrinkController {

    private final DrinkService drinkService;

    private final int SIZE = 5;

    @GetMapping("/search")
    public BaseResponse<SliceResponse<DrinkSearchResponse>> searchDrinks(
            @ModelAttribute SearchDrinkRequest searchDrinkRequest,
            @AuthenticationPrincipal AuthMember member
    ) {
        Slice<DrinkSearchResponse> result = drinkService.searchDrinks(searchDrinkRequest, getMemberId(member));
        return new BaseResponse<>(new SliceResponse<>(result));
    }

    @GetMapping("/{drinkId}")
    public BaseResponse<GetDrinkResponse> getDrink(
            @PathVariable Long drinkId,
            @AuthenticationPrincipal AuthMember member){

        GetDrinkResponse getDrinkResponse = drinkService.getDrinkById(drinkId, getMemberId(member));
        return new BaseResponse<>(getDrinkResponse);
    }

    /** 사용자 리뷰 남긴 주류 모아보기
     * @return 주류 요약 리스트
     */
    @GetMapping("/members/{memberId}/reviews")
    public BaseResponse<SliceResponse<GetDrinkPreviewResponse>> getReviewedDrinksByMember(
            @PathVariable Long memberId,
            @RequestParam(required = false, defaultValue = "0") int page,
            @AuthenticationPrincipal AuthMember member) {

        validateMember(member, memberId, DrinkException::new);
        Slice<GetDrinkPreviewResponse> result = drinkService.getReviewedDrinksByMember(memberId, PageRequest.of(page, SIZE));

        return new BaseResponse<>(new SliceResponse<>(result));
    }

    @GetMapping("/recommendation")
    public BaseResponse<DrinkRecommendResponse> recommendDrink() {

        DrinkRecommendResponse drinkRecommendResponse = drinkService.recommendDrink();
        return new BaseResponse<>(drinkRecommendResponse);
    }

}
