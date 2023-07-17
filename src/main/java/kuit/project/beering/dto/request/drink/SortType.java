package kuit.project.beering.dto.request.drink;

import kuit.project.beering.util.exception.DrinkException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import java.util.Arrays;
import java.util.Locale;

import static kuit.project.beering.util.BaseResponseStatus.INVALID_ORDER;

@Getter
@RequiredArgsConstructor
public enum SortType {
    REVIEW(Sort.by(Direction.DESC, "countOfReview")),
    RATING(Sort.by(Direction.DESC, "avgRating")),
    PRICE(Sort.by(Direction.ASC, "price")),
    NAME(Sort.by(Direction.ASC, "nameKr")),
    OTHER(Sort.by(Direction.ASC, "id"));

    private final Sort sort;

    public static Sort getMatchedSort(String sorting) {
        return Arrays.stream(values())
                .filter(sortType -> sortType.name().equals(sorting.toUpperCase(Locale.ROOT)))
                .findAny()
                .orElseThrow(() -> new DrinkException(INVALID_ORDER))
                .sort;
    }
}
