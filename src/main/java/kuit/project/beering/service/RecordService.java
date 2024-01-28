package kuit.project.beering.service;

import kuit.project.beering.domain.Record;
import kuit.project.beering.dto.response.record.DailyAmount;
import kuit.project.beering.dto.response.record.MonthlyAmount;
import kuit.project.beering.dto.response.record.RecordByDateResponse;
import kuit.project.beering.repository.RecordRepository;
import kuit.project.beering.repository.drink.DrinkRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RecordService {

    private final DrinkRepository drinkRepository;
    private final RecordRepository recordRepository;

    public RecordByDateResponse getUserRecordStatisticByDate(int year, int month, Long userId) {

        System.out.println("getRecordsByDate 진입");

        // #1 Record 의 '일자' 로 일자별 주량 "List<DailyAmount> dailyAmounts" Setting
        List<Record> records = recordRepository.findByDateAndUserId(year, month, userId);
        System.out.println("User 의 연/월 에 해당하는 Record 기록 조회 완료 - 기록 개수 : " + records.size());

        List<DailyAmount> dailyAmounts = getDailyAmounts(records);

        // #2 month 로 최근 6개월 월별 주량 "List<MonthlyAmount> monthlyAmounts" Setting
        List<MonthlyAmount> monthlyAmounts = getRecent6MonthlyAmounts(year, month, userId);

        // TODO#3 : month 로 주종별 주량 "List<Map<Integer, Integer>> typeAmount" Setting

        // TODO#4 : build 하여 return
        return new RecordByDateResponse(dailyAmounts, monthlyAmounts, null);
    }

    /**
     *
     * @param month MonthlyAmount 에 포함하기 위한 월
     * @param records month 에 해당하는 모든 Record 리스트
     * @return : 해당 월에 대한 MontlyAmount 객체
     */
    private MonthlyAmount getMonthlyTotalAmount(int month, List<Record> records) {
        // 해당 월의 모든 Record 과 연관된 RecordAmount 에서 수량 * 용량 을 합하여 total 집계
        int total = records.stream()
                .map(Record::getAmounts)
                .mapToInt(recordAmounts -> {
                    return recordAmounts.stream()
                            .mapToInt(amount -> amount.getVolume() * amount.getQuantity())
                            .sum();
                }).sum();

        return new MonthlyAmount(month, total);
    }

    /**
     *
     * @param year 기록 조회를 위한 연도
     * @param month 기록 조회를 위한 월
     * @param userId 기록 조회를 위한 유저 id
     * @return : 월, 월별 총량 을 담는 MonthlyAmount 리스트 (최근 6개월의 데이터를 포함하기 때문에 size 6)
     */
    private List<MonthlyAmount> getRecent6MonthlyAmounts(int year, int month, Long userId) {

        List<MonthlyAmount> monthlyAmounts = new ArrayList<>();

        for (int i = 0; i < 6; i++) {
            // 전년도 이동
            if (month - i < 1) {
                month += 12;
                year -= 1;
            }

            List<Record> records = recordRepository.findByDateAndUserId(year, month - i, userId);

            monthlyAmounts.add(getMonthlyTotalAmount(month - i, records));
        }

        return monthlyAmounts;
    }

    /**
     *
     * @param records : 요청한 User 가 작성한, 특정 연/월 에 해당하는 Record list
     * @return : 일자, 총량 을 담는 DailyAmount 리스트
     */
    private List<DailyAmount> getDailyAmounts(List<Record> records) {

        Map<Integer, Integer> dailyAmounts = new HashMap<>();

        for(Record record: records) {
            int dayOfMonth = record.getDate().toLocalDateTime().getDayOfMonth();
            int additionalValue = record.getAmounts().stream()
                    .mapToInt(amount -> amount.getQuantity() * amount.getVolume())
                    .sum();

            dailyAmounts.computeIfPresent(dayOfMonth, (k, v) -> v + additionalValue);
            // 처음 한번만 실행
            dailyAmounts.putIfAbsent(dayOfMonth, additionalValue);
        }

        // DailyAmount 객체 리스트 생성
        List<DailyAmount> dailyAmountList = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : dailyAmounts.entrySet()) {
            dailyAmountList.add(new DailyAmount(entry.getKey(), entry.getValue()));
        }

        return dailyAmountList;
    }
}
