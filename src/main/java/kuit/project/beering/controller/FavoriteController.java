package kuit.project.beering.controller;

import kuit.project.beering.dto.response.SliceResponse;
import kuit.project.beering.dto.response.drink.GetDrinkPreviewResponse;
import kuit.project.beering.security.auth.AuthMember;
import kuit.project.beering.service.FavoriteService;
import kuit.project.beering.util.BaseResponse;
import kuit.project.beering.util.BaseResponseStatus;
import kuit.project.beering.util.exception.domain.FavoriteException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static kuit.project.beering.util.CheckMember.validateMember;

@RestController
@RequiredArgsConstructor
@Slf4j
public class FavoriteController {
    private final FavoriteService favoriteService;
    private final int SIZE = 5;

    @PostMapping("/members/{memberId}/drinks/{drinkId}/favorites")
    public BaseResponse<BaseResponseStatus> postFavorite(
            @PathVariable Long memberId,
            @PathVariable Long drinkId,
            @AuthenticationPrincipal AuthMember member) {

        validateMember(member, memberId, FavoriteException::new);
        BaseResponseStatus status = favoriteService.saveFavorite(memberId, drinkId);

        return new BaseResponse<>(status);
    }

    @GetMapping("/members/{memberId}/favorites")
    public BaseResponse<SliceResponse<GetDrinkPreviewResponse>> getFavoriteDrinks(
            @PathVariable Long memberId,
            @RequestParam(required = false, defaultValue = "0") int page,
            @AuthenticationPrincipal AuthMember member) {

        validateMember(member, memberId, FavoriteException::new);
        Slice<GetDrinkPreviewResponse> result = favoriteService.getFavoriteDrinks(memberId, PageRequest.of(page, SIZE));

        return new BaseResponse<>(new SliceResponse<>(result));
    }


}
