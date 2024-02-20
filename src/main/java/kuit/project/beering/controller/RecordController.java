package kuit.project.beering.controller;

import static kuit.project.beering.util.BaseResponseStatus.TOKEN_PATH_MISMATCH;

import java.util.Objects;
import kuit.project.beering.dto.request.record.RecordStatisticRequest;
import kuit.project.beering.dto.response.record.RecordByDateResponse;
import kuit.project.beering.security.auth.AuthMember;
import kuit.project.beering.service.RecordService;
import kuit.project.beering.util.BaseResponse;
import kuit.project.beering.util.exception.domain.RecordException;
import kuit.project.beering.util.exception.domain.TagException;
import kuit.project.beering.util.exception.validation.FieldValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
public class RecordController {

    private final RecordService recordService;

    @GetMapping("/members/{memberId}/records/statistics")
    public BaseResponse<RecordByDateResponse> getMemberRecordStatisticByDate(
            @PathVariable Long memberId,
            @Validated RecordStatisticRequest request,
            BindingResult bindingResult,
            @AuthenticationPrincipal AuthMember authMember) {
        log.info("RecordController getMemberRecordStatisticByDate 진입 = {} 년 {} 월 조회", request.getYear(), request.getMonth());

        if (bindingResult.hasErrors())
            throw new FieldValidationException(bindingResult);

        validateMember(memberId, authMember.getId());

        RecordByDateResponse response = recordService.getMemberRecordStatisticByDate(request.getYear(), request.getMonth(), authMember.getId());

        return new BaseResponse<>(response);
    }

    private void validateMember(Long authId, Long memberId) {
        if (!Objects.equals(authId, memberId))
            throw new RecordException(TOKEN_PATH_MISMATCH);
    }
}

