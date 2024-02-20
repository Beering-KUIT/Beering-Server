package kuit.project.beering.service;

import kuit.project.beering.domain.DrinkType;
import kuit.project.beering.domain.Record;
import kuit.project.beering.dto.response.record.DailyAmount;
import kuit.project.beering.dto.response.record.MonthlyAmount;
import kuit.project.beering.dto.response.record.RecordByDateResponse;
import kuit.project.beering.dto.response.record.TypelyAmount;
import kuit.project.beering.repository.RecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RecordService {

    private final RecordRepository recordRepository;

    public RecordByDateResponse getMemberRecordStatisticByDate(int year, int month, Long memberId) {
        log.info("RecordService getMemberRecordStatisticByDate 진입");

        // #0 year 년 month 월 기록. 일별 주량(#1) & 주종별 주량(#3) 에서 사용
        List<Record> records = recordRepository.findByDateAndMemberId(year, month, memberId);
        log.info("Member 의 {}년/{}월 에 해당하는 Record 기록 조회 완료 - 기록 개수 : {}", year, month, records.size());

        // #1 Record 의 '일자' 로 일자별 주량 "List<DailyAmount> dailyAmounts" Setting
        List<DailyAmount> dailyAmounts = getDailyAmounts(records);

        // #2 month 로 최근 6개월 월별 주량 "List<MonthlyAmount> monthlyAmounts" Setting
        List<MonthlyAmount> monthlyAmounts = getRecent6MonthlyAmounts(year, month, memberId);

        // #3 : month 로 주종별 주량 "List<TypelyAmount> typeAmount" Setting
        List<TypelyAmount> typelyAmounts = getTypelyAmounts(records);

        return new RecordByDateResponse(dailyAmounts, monthlyAmounts, typelyAmounts);
    }

    /**
     * 일별 주량 리스트를 받아오는 메서드이다. 주량이 존재하는 일자 정보만 포함한다.
     * @param records : 요청한 Member 가 작성한, 특정 연/월 에 해당하는 Record list
     * @return : 일자, 총량 을 담는 DailyAmount 리스트
     */
    private List<DailyAmount> getDailyAmounts(List<Record> records) {
        log.info("RecordService getDailyAmounts 진입");

        Map<Integer, Integer> dayAndAmount = new HashMap<>();

        for(Record record: records) {
            int dayOfMonth = record.getDate().toLocalDateTime().getDayOfMonth();
            int additionalValue = record.getAmounts().stream()
                .mapToInt(amount -> amount.getQuantity() * amount.getVolume())
                .sum();

            // value 를 key 에 해당하는 value 에 sum 하되, 없으면 key 로 value 저장
            dayAndAmount.merge(dayOfMonth, additionalValue, Integer::sum);
        }

        // DailyAmount 객체 리스트 생성
        List<DailyAmount> dailyAmounts = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : dayAndAmount.entrySet()) {
            dailyAmounts.add(new DailyAmount(entry.getKey(), entry.getValue()));
        }

        return dailyAmounts;
    }

    /**
     * 입력받은 year month 를 포함하여 최근 6개월의 월별 주량을 받아오는 메서드이다.
     * @param year 기록 조회를 위한 연도
     * @param month 기록 조회를 위한 월
     * @param memberId 기록 조회를 위한 유저 id
     * @return : 월, 월별 총량 을 담는 MonthlyAmount 리스트 (최근 6개월의 데이터를 포함하기 때문에 size 6)
     */
    private List<MonthlyAmount> getRecent6MonthlyAmounts(int year, int month, Long memberId) {
        log.info("RecordService getRecent6MonthlyAmounts 진입");

        List<MonthlyAmount> monthlyAmounts = new ArrayList<>();

        for (int i = 0; i < 6; i++) {
            // 전년도 이동
            if (month - i < 1) {
                month += 12;
                year -= 1;
            }

            List<Record> records = recordRepository.findByDateAndMemberId(year, month - i, memberId);
            monthlyAmounts.add(getMonthlyTotalAmount(month - i, records));
        }
        return monthlyAmounts;
    }

    /**
     * 특정 월에 해당하는 모든 기록의 주량 총합을 받아오는 메서드이다.
     * @param month MonthlyAmount 에 포함하기 위한 월
     * @param records month 에 해당하는 모든 Record 리스트
     * @return : 해당 월에 대한 MontlyAmount 객체
     */
    private MonthlyAmount getMonthlyTotalAmount(int month, List<Record> records) {
        log.info("RecordService getMonthlyTotalAmount 진입");

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
     * 조회한 연-월 에 해당하는 Member 의 Records 를 기반으로,
     * DrinkType 별 주량을 담는, TyelyAmount 정보들을 구해 리턴한다
     * @param records 조회한 연-월 에 해당하는 Member 의 Records
     * @return List<TypelyAmount>
     */
    private List<TypelyAmount> getTypelyAmounts(List<Record> records) {
        log.info("RecordService getTypelyAmounts 진입");

        Map<DrinkType, Integer> typelyAmounts = new HashMap<>();

        for (DrinkType drinkType : DrinkType.getAllTypes()) {
            typelyAmounts.putIfAbsent(drinkType, 0);
        }

        for (Record record : records) {
            DrinkType recordDrinkType = record.getDrink()
                    .getCategory()
                    .getName();

            typelyAmounts.merge(recordDrinkType, record.calculateTotalAmount(), Integer::sum);
        }

        return typelyAmounts.entrySet().stream()
                .map(entry -> TypelyAmount.builder()
                        .drinkType(entry.getKey().getDrinkTypeKr())
                        .totalConsumption(entry.getValue())
                        .build()
                )
                .collect(Collectors.toList());
    }
}
