package kuit.project.beering.controller;

import kuit.project.beering.dto.request.record.AddRecordRequest;
import kuit.project.beering.dto.response.record.GetRecordsResponse;
import kuit.project.beering.security.auth.AuthMember;
import kuit.project.beering.service.RecordService;
import kuit.project.beering.util.BaseResponse;
import kuit.project.beering.util.BaseResponseStatus;
import kuit.project.beering.util.exception.domain.RecordException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import static kuit.project.beering.util.BaseResponseStatus.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class RecordController {

    private final RecordService recordService;
    /** 특정 날짜, 특정 주류의 용량기록 가져오기
     * @return (recordAmount Id, 용량, 개수) 리스트
     */
    @GetMapping("/members/{memberId}/drinks/{drinkId}/records")
    public BaseResponse<List<GetRecordAmountResponse>> getRecordAmounts(
            @PathVariable Long memberId,
            @PathVariable Long drinkId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date date,
            @AuthenticationPrincipal AuthMember member){

        List<GetRecordAmountResponse> result = recordService.getRecordAmounts(memberId, drinkId, new Timestamp(date.getTime()));
        if(result.isEmpty())
            return new BaseResponse<>(EMPTY_RECORD_AMOUNTS);
        return new BaseResponse<>(result);
    }

    /** 특정 날짜의 기록 가져오기
     * @return (기록 id, 주류 이름, 총용량) 리스트 반환
     */
    @GetMapping("/members/{memberId}/records")
    public BaseResponse<List<GetRecordsResponse>> getRecords(
            @PathVariable Long memberId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date date,
            @AuthenticationPrincipal AuthMember member
    ){
        List<GetRecordsResponse> result = recordService.getRecords(memberId, new Timestamp(date.getTime()));
        if(result.isEmpty())
            return new BaseResponse<>(EMPTY_RECORDS);
        return new BaseResponse<>(result);
    }
}
