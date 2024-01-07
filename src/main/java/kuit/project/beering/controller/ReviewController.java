package kuit.project.beering.controller;

import kuit.project.beering.dto.request.review.ReviewCreateRequestDto;
import kuit.project.beering.dto.response.review.ReviewDeleteResponseDto;
import kuit.project.beering.dto.response.SliceResponse;
import kuit.project.beering.dto.response.review.ReviewDetailReadResponseDto;
import kuit.project.beering.dto.response.review.ReviewReadResponseDto;
import kuit.project.beering.dto.response.review.ReviewResponseDto;
import kuit.project.beering.security.auth.AuthMember;
import kuit.project.beering.service.ReviewService;
import kuit.project.beering.util.BaseResponse;
import kuit.project.beering.util.exception.domain.FavoriteException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static kuit.project.beering.util.BaseResponseStatus.TOKEN_PATH_MISMATCH;

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
            @RequestPart List<MultipartFile> reviewImages,
            @AuthenticationPrincipal AuthMember member) {

        validateMember(member.getId(), memberId);
        log.info("reviewImages = {}", reviewImages.isEmpty());
        log.info("reviewImages.size() = {}", reviewImages.size());
        ReviewResponseDto responseDto = reviewService.save(memberId, drinkId, requestDto, reviewImages);
        return new BaseResponse<>(responseDto);
    }

    @GetMapping(value = "/reviews/{reviewId}")
    public BaseResponse<ReviewDetailReadResponseDto> readReviewDetail(@PathVariable Long reviewId,
                                                                      @AuthenticationPrincipal AuthMember member) {

        long memberId;
        if(member != null)
            memberId = member.getId();
        else
            memberId = 0;
        ReviewDetailReadResponseDto responseDto = reviewService.readReviewDetail(memberId, reviewId);
        return new BaseResponse<>(responseDto);
    }

    @GetMapping(value = "/members/{memberId}/reviews")
    public BaseResponse<SliceResponse<ReviewReadResponseDto>> readReviewByMemberId(@PathVariable Long memberId,
                                                                                   @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                                                                   @RequestParam(value = "size", required = false, defaultValue = "5") int size,
                                                                                   @AuthenticationPrincipal AuthMember member) {

        validateMember(member.getId(), memberId);
        PageRequest pageRequest = PageRequest.of(page, size);
        SliceResponse<ReviewReadResponseDto> responseDtos = reviewService.findAllReviewByMemberIdByPage(memberId, pageRequest);
        return new BaseResponse<>(responseDtos);
    }

    @GetMapping(value = "/drinks/{drinkId}/reviews")
    public BaseResponse<SliceResponse<ReviewReadResponseDto>> readReviewByDrinkId(@PathVariable Long drinkId,
                                                                                  @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                                                                  @RequestParam(value = "size", required = false, defaultValue = "5") int size,
                                                                                  @AuthenticationPrincipal AuthMember member) {
        long memberId;
        if(member != null)
            memberId = member.getId();
        else
            memberId = 0;

        PageRequest pageRequest = PageRequest.of(page, size);
        SliceResponse<ReviewReadResponseDto> responseDtos = reviewService.findAllReviewByDrinkIdByPage(drinkId, memberId, pageRequest);
        return new BaseResponse<>(responseDtos);
    }

    @GetMapping(value = "/reviews")
    public BaseResponse<SliceResponse<ReviewReadResponseDto>> readReviewByCreatedAtDesc(@RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                                                                        @RequestParam(value = "size", required = false, defaultValue = "5") int size,
                                                                                        @AuthenticationPrincipal AuthMember member) {

        long memberId;
        if(member != null)
            memberId = member.getId();
        else
            memberId = 0;

        log.info("now = {}", LocalDateTime.now());
        PageRequest pageRequest = PageRequest.of(page, size);
        SliceResponse<ReviewReadResponseDto> responseDtos = reviewService.findReviewByPage(memberId, pageRequest);
        return new BaseResponse<>(responseDtos);
    }

    @DeleteMapping(value = "/members/{memberId}/reviews/{reviewId}")
    public BaseResponse<ReviewDeleteResponseDto> deleteReview(@PathVariable Long memberId,
                                                              @PathVariable Long reviewId,
                                                              @AuthenticationPrincipal AuthMember member) {
        validateMember(member.getId(), memberId);
        ReviewDeleteResponseDto responseDto = reviewService.deleteReview(reviewId, memberId);
        return new BaseResponse<>(responseDto);
    }

    private void validateMember(Long authId, Long memberId) {
        if (!Objects.equals(authId, memberId))
            throw new FavoriteException(TOKEN_PATH_MISMATCH);
    }
}
