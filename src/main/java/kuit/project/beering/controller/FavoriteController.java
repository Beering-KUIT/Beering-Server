package kuit.project.beering.controller;

import kuit.project.beering.dto.response.SliceResponse;
import kuit.project.beering.dto.response.favorite.GetFavoriteDrinkResponse;
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

import java.util.Objects;

import static kuit.project.beering.util.BaseResponseStatus.TOKEN_PATH_MISMATCH;

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

        validateMember(member.getId(), memberId);
        BaseResponseStatus status = favoriteService.saveFavorite(memberId, drinkId);

        return new BaseResponse<>(status);
    }

    @GetMapping("/members/{memberId}/favorites")
    public BaseResponse<SliceResponse<GetDrinkPreviewResponse>> getFavoriteDrinks(
            @PathVariable Long memberId,
            @RequestParam(required = false, defaultValue = "0") int page,
            @AuthenticationPrincipal AuthMember member) {

        validateMember(member.getId(), memberId);
        Slice<GetDrinkPreviewResponse> result = favoriteService.getFavoriteDrinks(memberId, PageRequest.of(page, SIZE));

        return new BaseResponse<>(new SliceResponse<>(result));
    }

    private void validateMember(Long authId, Long memberId) {
        if (!Objects.equals(authId, memberId))
            throw new FavoriteException(TOKEN_PATH_MISMATCH);
    }

}
