package kuit.project.beering.controller;


import kuit.project.beering.dto.response.SliceResponse;
import kuit.project.beering.dto.response.tabom.GetTabomResponse;
import kuit.project.beering.security.auth.AuthMember;
import kuit.project.beering.service.TabomService;
import kuit.project.beering.util.BaseResponse;
import kuit.project.beering.util.BaseResponseStatus;
import kuit.project.beering.util.exception.domain.TabomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

import static kuit.project.beering.util.BaseResponseStatus.TOKEN_PATH_MISMATCH;

@RestController
@RequiredArgsConstructor
@Slf4j
public class TabomController {
    private final TabomService tabomService;

    private final int SIZE = 5;

    @PostMapping("/members/{memberId}/reviews/{reviewId}/tabom")
    public BaseResponse<BaseResponseStatus> postTabom(
            @PathVariable Long memberId,
            @PathVariable Long reviewId,
            @RequestParam boolean isUp,
            @AuthenticationPrincipal AuthMember member
    ) {
        validateMember(member.getId(), memberId);
        BaseResponseStatus status = tabomService.postTabom(memberId, reviewId, isUp);
        return new BaseResponse<>(status);
    }

    @GetMapping("/members/{memberId}/reviews/tabom")
    public BaseResponse<SliceResponse<GetTabomResponse>> getTabomReview(
            @PathVariable Long memberId,
            @RequestParam(required = false, defaultValue = "0") int page,
            @AuthenticationPrincipal AuthMember member
    ){
        validateMember(member.getId(), memberId);
        Slice<GetTabomResponse> result = tabomService.getTabomReviews(memberId, PageRequest.of(page, SIZE));

        return new BaseResponse<>(new SliceResponse<>(result));
    }


    private void validateMember(Long authId, Long memberId) {
        if (!Objects.equals(authId, memberId))
            throw new TabomException(TOKEN_PATH_MISMATCH);
    }

}
