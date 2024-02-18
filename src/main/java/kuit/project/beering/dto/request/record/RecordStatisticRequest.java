package kuit.project.beering.dto.request.record;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RecordStatisticRequest {

    // TODO : 안드로이드 캘린더 라이브러리에서 관리되는 연도 범위에 따라 재설정하기
    @NotNull(message = "연도는 필수 입력 항목입니다.")
    @Min(value = 1950, message = "유효하지 않은 연도 입력입니다.")
    @Max(value = 2099, message = "유효하지 않은 연도 입력입니다.")
    private Integer year;

    @NotNull(message = "월은 필수 입력 항목입니다.")
    @Min(value = 1, message = "월은 1 이상이어야 합니다.")
    @Max(value = 12, message = "월은 12 이하여야 합니다.")
    private Integer month;
}
