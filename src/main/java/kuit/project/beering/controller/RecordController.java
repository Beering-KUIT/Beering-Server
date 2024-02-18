package kuit.project.beering.controller;

import kuit.project.beering.dto.request.record.RecordStatisticRequest;
import kuit.project.beering.dto.response.record.RecordByDateResponse;
import kuit.project.beering.security.auth.AuthMember;
import kuit.project.beering.service.RecordService;
import kuit.project.beering.util.BaseResponse;
import kuit.project.beering.util.exception.validation.FieldValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/record")
@Validated
public class RecordController {

    private final RecordService recordService;

    @GetMapping
    public BaseResponse<RecordByDateResponse> getUserRecordStatisticByDate(
            @Validated RecordStatisticRequest request,
            BindingResult bindingResult,
            @AuthenticationPrincipal AuthMember member) {
        log.info("RecordController getUserRecordStatisticByDate 진입 = {} 년 {} 월 조회", request.getYear(), request.getMonth());

        if (bindingResult.hasErrors())
            throw new FieldValidationException(bindingResult);

        RecordByDateResponse response = recordService.getUserRecordStatisticByDate(request.getYear(), request.getMonth(), member.getId());

        return new BaseResponse<>(response);
    }
}

