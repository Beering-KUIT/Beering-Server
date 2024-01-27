package kuit.project.beering.dto.response.record;

import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.List;
import java.util.Map;

@Builder
@AllArgsConstructor
public class RecordByDateResponse {

    // Calendar 표시될 <일자 : 일별 주량> 맵 리스트
    List<Map<Integer, Integer>> dailyAmount;
    // 그래프 표시될 <월 : 월별 총 주량> 맵 리스트 (최근 6개월)
    List<Map<Integer, Integer>> monthlyAmount;
    // 그래프 표시될 <주종 : 주종별 총 주량> 맵 리스트
    List<Map<Integer, Integer>> typeAmount;
}
