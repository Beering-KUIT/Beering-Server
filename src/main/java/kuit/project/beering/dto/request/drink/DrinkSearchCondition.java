package kuit.project.beering.dto.request.drink;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DrinkSearchCondition {
    String nameKr;
    String nameEn;
    String categoryName;
    Integer minPrice;
    Integer maxPrice;
}
