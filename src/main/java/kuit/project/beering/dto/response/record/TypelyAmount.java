package kuit.project.beering.dto.response.record;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@AllArgsConstructor
@Getter
@Setter
// 주종별 용량을 의미한다. 어법에 맞지 않지만, 편의상 사용한다.
public class TypelyAmount {
    // JSON Response 값에 한국어로 표시하기 위해, DrinkType 의 drinkTypeKr(String) 필드 사용
    private String drinkType;
    private Integer totalConsumption;
}
