package kuit.project.beering.controller;

import kuit.project.beering.dto.request.record.AddRecordRequest;
import kuit.project.beering.dto.request.record.RecordStatisticRequest;
import kuit.project.beering.dto.response.record.GetRecordAmountResponse;
import kuit.project.beering.dto.response.record.GetRecordsResponse;
import kuit.project.beering.dto.response.record.RecordByDateResponse;
import kuit.project.beering.security.auth.AuthMember;
import kuit.project.beering.service.RecordService;
import kuit.project.beering.util.BaseResponse;
import kuit.project.beering.util.BaseResponseStatus;
import kuit.project.beering.util.exception.domain.RecordException;
import kuit.project.beering.util.exception.validation.FieldValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import static kuit.project.beering.util.BaseResponseStatus.*;
import static kuit.project.beering.util.CheckMember.validateMember;

@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
public class RecordController {

    private final RecordService recordService;

    /** 기록 추가하기
     * @param date
     * @param drinkId
     * @param request (용량, 개수) 리스트
     */
    @PostMapping("/members/{memberId}/drinks/{drinkId}/records")
    public BaseResponse<BaseResponseStatus> addRecord(
            @PathVariable Long memberId,
            @PathVariable Long drinkId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date date,
            @RequestBody @Validated List<AddRecordRequest> request,
            @AuthenticationPrincipal AuthMember member){

        validateMember(member, memberId, RecordException::new);
        BaseResponseStatus status = recordService.addRecord(memberId, drinkId, request, new Timestamp(date.getTime()));
        return new BaseResponse<>(status);
    }

    /** 특정 날짜, 특정 주류의 용량기록 가져오기
     * @return (recordAmount Id, 용량, 개수) 리스트
     */
    @GetMapping("/members/{memberId}/drinks/{drinkId}/records")
    public BaseResponse<List<GetRecordAmountResponse>> getRecordAmounts(
            @PathVariable Long memberId,
            @PathVariable Long drinkId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date date,
            @AuthenticationPrincipal AuthMember authMember){

        validateMember(authMember, memberId, RecordException::new);
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
            @AuthenticationPrincipal AuthMember authMember
    ){
        validateMember(authMember, memberId, RecordException::new);
        List<GetRecordsResponse> result = recordService.getRecords(memberId, new Timestamp(date.getTime()));
        if(result.isEmpty())
            return new BaseResponse<>(EMPTY_RECORDS);
        return new BaseResponse<>(result);
    }
    @GetMapping("/members/{memberId}/records/statistics")
    public BaseResponse<RecordByDateResponse> getMemberRecordStatisticByDate(
            @PathVariable Long memberId,
            @Validated RecordStatisticRequest request,
            BindingResult bindingResult,
            @AuthenticationPrincipal AuthMember authMember) {
        log.info("RecordController getMemberRecordStatisticByDate 진입 = {} 년 {} 월 조회", request.getYear(), request.getMonth());

        if (bindingResult.hasErrors())
            throw new FieldValidationException(bindingResult);

        validateMember(authMember, memberId, RecordException::new);

        RecordByDateResponse response = recordService.getMemberRecordStatisticByDate(request.getYear(), request.getMonth(), authMember.getId());

        return new BaseResponse<>(response);
    }
}

