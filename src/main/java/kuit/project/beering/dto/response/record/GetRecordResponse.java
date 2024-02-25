package kuit.project.beering.dto.response.record;

import kuit.project.beering.dto.response.drink.GetDrinkPreviewResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class GetRecordResponse {
    private GetDrinkPreviewResponse drink;
    List<GetRecordAmountResponse> amounts;
}
