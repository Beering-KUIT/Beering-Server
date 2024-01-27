package kuit.project.beering.service;

import kuit.project.beering.domain.Record;
import kuit.project.beering.dto.response.record.RecordByDateResponse;
import kuit.project.beering.repository.RecordRepository;
import kuit.project.beering.repository.drink.DrinkRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RecordService {

    private final DrinkRepository drinkRepository;
    private final RecordRepository recordRepository;

    public RecordByDateResponse getUserRecordStatisticByDate(int year, int month, Long userId) {

        System.out.println("getRecordsByDate 진입");

        // TODO#1 : Record 의 '일자' 로 일자별 주량 "List<Map<Integer, Integer>> dailyAmount" Setting
        List<Record> records = recordRepository.findByDateAndUserId(year, month, userId);
        System.out.println("User 의 연/월 에 해당하는 Record 기록 조회 완료 - 기록 개수 : " + records.size());

        // TODO#2 : month 로 최근 6개월 월별 주량 "List<Map<Integer, Integer>> monthlyAmount" Setting
        /* e.g.
            for (int i = 0; i < 6; i++) {
                recordRepository.findByDateAndUserId(year, month - i, userId)
                                .stream()
                                .맵핑
                                .합계
            }
        */

        // TODO#3 : month 로 주종별 주량 "List<Map<Integer, Integer>> typeAmount" Setting

        // TODO#4 : build 하여 return
        return new RecordByDateResponse(null, null, null);
    }
}
