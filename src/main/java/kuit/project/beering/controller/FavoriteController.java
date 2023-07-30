package kuit.project.beering.controller;

import kuit.project.beering.security.auth.AuthMember;
import kuit.project.beering.service.FavoriteService;
import kuit.project.beering.util.BaseResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

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

        favoriteService.addToFavorite(memberId, drinkId);

        return new BaseResponse<>(new Object());
    }

}
