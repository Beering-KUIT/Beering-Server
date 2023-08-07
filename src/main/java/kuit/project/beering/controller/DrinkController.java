package kuit.project.beering.controller;

import kuit.project.beering.dto.request.drink.DrinkSearchCondition;
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

import java.util.List;


@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/drinks")
public class DrinkController {

    private final DrinkService drinkService;

    @GetMapping("/search")
    public BaseResponse<SliceResponse<DrinkSearchResponse>> searchDrinksByName(
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "0", required = false) Integer page,
            @RequestParam(defaultValue = "name", required = false) String orderBy,
            @RequestParam(required = false) List<String> category,
            @RequestParam(defaultValue = "0", required = false) Integer minPrice,
            @RequestParam(defaultValue = "2147483647", required = false) Integer maxPrice,
            @AuthenticationPrincipal AuthMember member
    ) {
        Slice<DrinkSearchResponse> result = drinkService.searchDrinksByName(page, orderBy, new DrinkSearchCondition(name, name, category, minPrice, maxPrice, member.getId()));
        return new BaseResponse<>(new SliceResponse<>(result));
    }


    @GetMapping("/{drinkId}")
    public BaseResponse<GetDrinkResponse> getDrink(
            @PathVariable Long drinkId,
            @AuthenticationPrincipal AuthMember member){

        GetDrinkResponse getDrinkResponse = drinkService.getDrinkById(drinkId, member.getId());
        return new BaseResponse<>(getDrinkResponse);
    }

}
