package kuit.project.beering.controller;

import kuit.project.beering.dto.request.drink.SearchDrinkRequest;
import kuit.project.beering.dto.response.SliceResponse;
import kuit.project.beering.dto.response.drink.DrinkSearchResponse;
import kuit.project.beering.dto.response.drink.GetDrinkResponse;
import kuit.project.beering.security.auth.AuthMember;
import kuit.project.beering.service.DrinkService;
import kuit.project.beering.util.BaseResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Slice;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/drinks")
public class DrinkController {

    private final DrinkService drinkService;

    @GetMapping("/search")
    public BaseResponse<SliceResponse<DrinkSearchResponse>> searchDrinksByName(
            @ModelAttribute SearchDrinkRequest searchDrinkRequest,
            @AuthenticationPrincipal AuthMember member
    ) {
        Slice<DrinkSearchResponse> result = drinkService.searchDrinksByName(searchDrinkRequest, isLoginMember(member));
        return new BaseResponse<>(new SliceResponse<>(result));
    }

    @GetMapping("/{drinkId}")
    public BaseResponse<GetDrinkResponse> getDrink(
            @PathVariable Long drinkId,
            @AuthenticationPrincipal AuthMember member){

        GetDrinkResponse getDrinkResponse = drinkService.getDrinkById(drinkId, isLoginMember(member));
        return new BaseResponse<>(getDrinkResponse);
    }

    private Long isLoginMember(AuthMember member) {
        if(member == null) return 0L;
        else return member.getId();
    }

}
