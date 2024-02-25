package kuit.project.beering.dto.request.drink;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SearchDrinkRequest {
    private String name;
    private Integer page = 0;

    @Pattern(regexp = "[^0-9]*", message = "숫자는 입력할 수 없습니다.")
    private String orderBy = "name";
    private List<String> category;
    private Integer minPrice = 0;
    private Integer maxPrice = Integer.MAX_VALUE;

    @Pattern(regexp = "[^0-9]*", message = "숫자는 입력할 수 없습니다.")
    private String country;
    @Range(min = 1, max = 5, message = "1~5 사이의 숫자만 입력할 수 있습니다.")
    private Integer sweetness;
    private List<String> tag;
}
