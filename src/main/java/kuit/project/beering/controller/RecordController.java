package kuit.project.beering.controller;

import kuit.project.beering.dto.response.record.RecordByDateResponse;
import kuit.project.beering.security.auth.AuthMember;
import kuit.project.beering.service.RecordService;
import kuit.project.beering.util.BaseResponse;
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
    public BaseResponse<RecordByDateResponse> getRecord(
            @RequestParam("year") int year,
            @RequestParam("month") int month,
            @AuthenticationPrincipal AuthMember member) {

        System.out.println("RecordController getRecord 진입 " + year + " 년 " + month + " 월");
        RecordByDateResponse response = recordService.getUserRecordStatisticByDate(year, month, member.getId());

        return new BaseResponse<>(response);
    }
}

