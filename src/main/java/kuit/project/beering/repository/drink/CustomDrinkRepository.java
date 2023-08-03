package kuit.project.beering.repository.drink;

import kuit.project.beering.dto.request.drink.DrinkSearchCondition;
import kuit.project.beering.dto.response.drink.DrinkSearchResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomDrinkRepository {
    Slice<DrinkSearchResponse> search(DrinkSearchCondition drinkSearchCondition, Pageable pageable);
}
