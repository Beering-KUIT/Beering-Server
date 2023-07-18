package kuit.project.beering.controller;

import kuit.project.beering.dto.request.review.ReviewCreateRequestDto;
import kuit.project.beering.dto.response.review.ReviewResponseDto;
import kuit.project.beering.dto.response.reviewOption.ReviewOptionReadResponseDto;
import kuit.project.beering.service.ReviewService;
import kuit.project.beering.util.BaseResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/members/{memberId}/drinks/{drinkId}/reviews")
    public BaseResponse<ReviewResponseDto> createReview(
            @PathVariable Long memberId,
            @PathVariable Long drinkId,
            @RequestBody ReviewCreateRequestDto requestDto) {

        ReviewResponseDto responseDto = reviewService.save(memberId, drinkId, requestDto);
        return new BaseResponse<>(responseDto);
    }
}
