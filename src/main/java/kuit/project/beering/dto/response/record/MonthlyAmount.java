package kuit.project.beering.dto.response.record;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class MonthlyAmount {
    private Integer month;
    private Integer totalConsumption;
}
