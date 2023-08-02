package kuit.project.beering.controller;

import kuit.project.beering.dto.request.review.ReviewCreateRequestDto;
import kuit.project.beering.dto.response.review.ReviewDetailReadResponseDto;
import kuit.project.beering.dto.response.review.ReviewReadResponseDto;
import kuit.project.beering.dto.response.review.ReviewResponseDto;
import kuit.project.beering.dto.response.review.ReviewSliceResponseDto;
import kuit.project.beering.dto.response.reviewOption.ReviewOptionReadResponseDto;
import kuit.project.beering.security.auth.AuthMember;
import kuit.project.beering.service.ReviewService;
import kuit.project.beering.util.BaseResponse;
import kuit.project.beering.util.exception.FavoriteException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
        ReviewResponseDto responseDto = reviewService.save(memberId, drinkId, requestDto, reviewImages);
        return new BaseResponse<>(responseDto);
    }

    @GetMapping(value = "/reviews/{reviewId}")
    public BaseResponse<ReviewDetailReadResponseDto> readReviewDetail(@PathVariable Long reviewId) {

        ReviewDetailReadResponseDto responseDto = reviewService.readReviewDetail(reviewId);
        return new BaseResponse<>(responseDto);
    }

    @GetMapping(value = "/members/{memberId}/reviews")
    public BaseResponse<ReviewSliceResponseDto> readReviewByMemberId(@PathVariable Long memberId,
                                                                     @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                                                     @RequestParam(value = "size", required = false, defaultValue = "5") int size,
                                                                     @AuthenticationPrincipal AuthMember member) {

        validateMember(member.getId(), memberId);
        PageRequest pageRequest = PageRequest.of(page, size);
        ReviewSliceResponseDto responseDtos = reviewService.findAllReviewByMemberIdByPage(memberId, pageRequest);
        return new BaseResponse<>(responseDtos);
    }

    private void validateMember(Long authId, Long memberId) {
        if (!Objects.equals(authId, memberId))
            throw new FavoriteException(TOKEN_PATH_MISMATCH);
    }
}
