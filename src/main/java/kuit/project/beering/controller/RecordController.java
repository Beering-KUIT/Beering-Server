package kuit.project.beering.controller;

import static kuit.project.beering.util.BaseResponseStatus.INVALID_MONTH;
import static kuit.project.beering.util.BaseResponseStatus.INVALID_YEAR;

import kuit.project.beering.dto.response.record.RecordByDateResponse;
import kuit.project.beering.security.auth.AuthMember;
import kuit.project.beering.service.RecordService;
import kuit.project.beering.util.BaseResponse;
import kuit.project.beering.util.BaseResponseStatus;
import kuit.project.beering.util.exception.domain.RecordException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/record")
public class RecordController {

    private final RecordService recordService;

    @GetMapping
    public BaseResponse<RecordByDateResponse> getUserRecordStatisticByDate(
            @RequestParam("year") int year,
            @RequestParam("month") int month,
            @AuthenticationPrincipal AuthMember member) {
        log.info("RecordController getUserRecordStatisticByDate 진입 = {} 년 {} 월 조회", year, month);

        validateDate(year, month);
        RecordByDateResponse response = recordService.getUserRecordStatisticByDate(year, month, member.getId());

        return new BaseResponse<>(response);
    }

    private void validateDate(int year, int month) {
        if (!(2000 <= year && year <= 2999))
            throw new RecordException(INVALID_YEAR);

        if (!(1 <= month && month <= 12))
            throw new RecordException(INVALID_MONTH);
    }
}

