package kuit.project.beering.controller;


import kuit.project.beering.security.auth.AuthMember;
import kuit.project.beering.service.TabomService;
import kuit.project.beering.util.BaseResponse;
import kuit.project.beering.util.exception.TabomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

import static kuit.project.beering.util.BaseResponseStatus.TOKEN_PATH_MISMATCH;

@RestController
@RequiredArgsConstructor
@Slf4j
public class TabomController {
    private final TabomService tabomService;

    @PostMapping("/members/{memberId}/reviews/{reviewId}")
    public BaseResponse<Object> postTabom(
            @PathVariable Long memberId,
            @PathVariable Long reviewId,
            @RequestParam boolean isUp,
            @AuthenticationPrincipal AuthMember member
    ) {
        validateMember(member.getId(), memberId);
        tabomService.addToTabom(memberId, reviewId, isUp);
        return new BaseResponse<>(new Object());
    }

    private void validateMember(Long authId, Long memberId) {
        if (!Objects.equals(authId, memberId))
            throw new TabomException(TOKEN_PATH_MISMATCH);
    }

}
