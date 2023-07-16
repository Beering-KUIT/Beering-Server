package kuit.project.beering.controller;

import kuit.project.beering.dto.request.drink.DrinkSearchCondition;
import kuit.project.beering.dto.response.drink.DrinkSearchResponse;
import kuit.project.beering.dto.response.drink.GetDrinkResponse;
import kuit.project.beering.util.BaseResponse;
import kuit.project.beering.util.exception.DrinkException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import kuit.project.beering.service.DrinkService;


@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/drinks")
public class DrinkController {

    private final DrinkService drinkService;

    @GetMapping("/search")
    public BaseResponse<Page<DrinkSearchResponse>> searchDrinksByName(
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "0", required = false) Integer page,
            @RequestParam(defaultValue = "name", required = false) String orderBy,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "0", required = false) Integer minPrice,
            @RequestParam(required = false) Integer maxPrice
    ) {
        if (maxPrice == null) {
            maxPrice = Integer.MAX_VALUE;
        }

        Page<DrinkSearchResponse> result = drinkService.searchDrinksByName(page, orderBy, new DrinkSearchCondition(name, name, category, minPrice, maxPrice));
        return new BaseResponse<>(result);
    }


    @GetMapping("/{beerId}")
    public BaseResponse<GetDrinkResponse> getDrink(@PathVariable Long beerId){

        GetDrinkResponse getDrinkResponse = drinkService.getDrinkById(beerId);
        return new BaseResponse<>(getDrinkResponse);
    }

}
