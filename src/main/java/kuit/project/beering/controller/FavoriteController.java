package kuit.project.beering.controller;

import kuit.project.beering.security.auth.AuthMember;
import kuit.project.beering.service.FavoriteService;
import kuit.project.beering.util.BaseResponse;
import kuit.project.beering.util.exception.FavoriteException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

import static kuit.project.beering.util.BaseResponseStatus.TOKEN_PATH_MISMATCH;

@RestController
@RequiredArgsConstructor
@Slf4j
public class FavoriteController {
    private final FavoriteService favoriteService;

    @PostMapping("/members/{memberId}/drinks/{drinkId}/favorites")
    public BaseResponse<Object> postFavorite(
            @PathVariable Long memberId,
            @PathVariable Long drinkId,
            @AuthenticationPrincipal AuthMember member) {

        validateMember(member.getId(), memberId);
        favoriteService.addToFavorite(memberId, drinkId);

        return new BaseResponse<>(new Object());
    }

    private void validateMember(Long authId, Long memberId) {
        if (!Objects.equals(authId, memberId))
            throw new FavoriteException(TOKEN_PATH_MISMATCH);
    }

}
