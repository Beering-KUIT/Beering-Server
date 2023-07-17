package kuit.project.beering.controller;

import kuit.project.beering.dto.response.reviewOption.ReviewOptionReadResponseDto;
import kuit.project.beering.service.ReviewOptionService;
import kuit.project.beering.util.BaseResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ReviewOptionController {

    private final ReviewOptionService reviewOptionService;

    @GetMapping("/reviewOptions")
    public BaseResponse<List<ReviewOptionReadResponseDto>> readAllReviewOptionByDrinkId(
            @RequestParam(name = "drinkId")Long drinkId) {

        List<ReviewOptionReadResponseDto> responseDtos = reviewOptionService.findAllReviewOptionByDrinkId(drinkId);
        return new BaseResponse<>(responseDtos);
    }
}
