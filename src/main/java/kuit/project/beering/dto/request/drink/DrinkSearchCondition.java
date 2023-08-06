package kuit.project.beering.dto.request.drink;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class DrinkSearchCondition {
    String nameKr;
    String nameEn;
    List<String> categories;
    Integer minPrice;
    Integer maxPrice;
    Long memberId;
}
