package kuit.project.beering.dto.request.drink;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SearchDrinkRequest {
    private String name;
    private Integer page = 0;
    private String orderBy = "name";
    private List<String> category;
    private Integer minPrice = 0;
    private Integer maxPrice = Integer.MAX_VALUE;
    private String country;
    private Integer sweetness;
    private List<String> tag;
}
