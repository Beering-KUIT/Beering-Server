package kuit.project.beering.controller;

import kuit.project.beering.dto.request.review.ReviewCreateRequestDto;
import kuit.project.beering.dto.response.review.ReviewDetailReadResponseDto;
import kuit.project.beering.dto.response.review.ReviewResponseDto;
import kuit.project.beering.dto.response.reviewOption.ReviewOptionReadResponseDto;
import kuit.project.beering.service.ReviewService;
import kuit.project.beering.util.BaseResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping(value = "/members/{memberId}/drinks/{drinkId}/reviews", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public BaseResponse<ReviewResponseDto> createReview(
            @PathVariable Long memberId,
            @PathVariable Long drinkId,
            @RequestPart ReviewCreateRequestDto requestDto,
            @RequestPart List<MultipartFile> reviewImages) {

        ReviewResponseDto responseDto = reviewService.save(memberId, drinkId, requestDto, reviewImages);
        return new BaseResponse<>(responseDto);
    }

    @GetMapping(value = "/reviews/{reviewId}")
    public BaseResponse<ReviewDetailReadResponseDto> readReviewDetail(@PathVariable Long reviewId) {

        ReviewDetailReadResponseDto responseDto = reviewService.readReviewDetail(reviewId);
        return new BaseResponse<>(responseDto);
    }
}
