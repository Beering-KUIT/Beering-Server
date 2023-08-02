package kuit.project.beering.service;

import kuit.project.beering.domain.Drink;
import kuit.project.beering.domain.ReviewOption;
import kuit.project.beering.dto.response.reviewOption.ReviewOptionReadResponseDto;
import kuit.project.beering.repository.ReviewOptionRepository;
import kuit.project.beering.repository.drink.DrinkRepository;
import kuit.project.beering.util.BaseResponseStatus;
import kuit.project.beering.util.exception.DrinkException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static kuit.project.beering.util.BaseResponseStatus.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewOptionService {

    private final ReviewOptionRepository reviewOptionRepository;
    private final DrinkRepository drinkRepository;

    @Transactional(readOnly = true)
    public List<ReviewOptionReadResponseDto> findAllReviewOptionByDrinkId(Long drinkId) {
        //엔티티 조회
        Drink drink = drinkRepository.findById(drinkId)
                .orElseThrow(() -> new DrinkException(NONE_DRINK));
        Long categoryId = drink.getCategory().getId();
        List<ReviewOption> reviewOptions = reviewOptionRepository.findAllByCategoryId(categoryId);

        return reviewOptions.stream()
                .map(ReviewOptionReadResponseDto::new)
                .collect(Collectors.toList());
    }
}
