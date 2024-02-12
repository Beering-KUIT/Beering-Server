package kuit.project.beering.dto.response.record;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@AllArgsConstructor
@Getter
@Setter
public class DailyAmount {
    private Integer date;
    private Integer totalConsumption;
}
