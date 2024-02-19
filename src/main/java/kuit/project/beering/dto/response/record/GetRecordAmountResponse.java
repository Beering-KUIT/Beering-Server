package kuit.project.beering.dto.response.record;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class GetRecordAmountResponse {
    private Long recordAmountId;
    private Integer volume;
    private Integer quantity;
}
