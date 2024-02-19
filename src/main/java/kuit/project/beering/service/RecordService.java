package kuit.project.beering.service;

import kuit.project.beering.domain.Drink;
import kuit.project.beering.domain.Member;
import kuit.project.beering.domain.Record;
import kuit.project.beering.domain.RecordAmount;
import kuit.project.beering.dto.response.record.GetRecordAmountResponse;
import kuit.project.beering.dto.response.record.GetRecordsResponse;
import kuit.project.beering.repository.MemberRepository;
import kuit.project.beering.repository.RecordAmountRepository;
import kuit.project.beering.repository.RecordRepository;
import kuit.project.beering.repository.drink.DrinkRepository;
import kuit.project.beering.util.exception.domain.DrinkException;
import kuit.project.beering.util.exception.domain.MemberException;
import kuit.project.beering.util.exception.domain.RecordException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static kuit.project.beering.util.BaseResponseStatus.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecordService {
    private final MemberRepository memberRepository;
    private final DrinkRepository drinkRepository;
    private final RecordRepository recordRepository;
    private final RecordAmountRepository recordAmountRepository;

    // 특정 날짜, 특정 주류의 용량기록 가져오기
    public List<GetRecordAmountResponse> getRecordAmounts(Long memberId, Long drinkId, Timestamp date) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(NONE_MEMBER));

        Drink drink = drinkRepository.findById(drinkId)
                .orElseThrow(() -> new DrinkException(NONE_DRINK));

        date = preprocessDate(date);

        Record record = recordRepository.findByDateAndMemberIdAndDrinkId(date, drinkId, memberId);

        if(record == null) return new ArrayList<>();

        List<RecordAmount> recordAmounts = recordAmountRepository.findAllByRecordId(record.getId());

        return recordAmounts.stream().map(
                recordAmount -> GetRecordAmountResponse.builder()
                        .recordAmountId(recordAmount.getId())
                        .quantity(recordAmount.getQuantity())
                        .volume(recordAmount.getVolume())
                        .build()
                ).collect(Collectors.toList());
    }

    public List<GetRecordsResponse> getRecords(Long memberId, Timestamp date) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(NONE_MEMBER));

        date = preprocessDate(date);

        List<Record> records = recordRepository.findAllByDateAndMemberId(date, memberId);

        return records.stream().map(
                record -> GetRecordsResponse.builder()
                        .recordId(record.getId())
                        .nameKr(record.getDrink().getNameKr())
                        .totalVolume(sumVolume(record)).build())
                .toList();

    }
    /**
     * t의 시간부분을 0으로 초기화하여 반환하는 메소드
     * @param t Timestamp
     * @return 시간이 0으로 초기화된 Timestamp
     */
    private Timestamp preprocessDate(Timestamp t) {
        Date preDate = new Date(t.getYear(), t.getMonth(), t.getDate());
        return new Timestamp(preDate.getTime());
    }
}
