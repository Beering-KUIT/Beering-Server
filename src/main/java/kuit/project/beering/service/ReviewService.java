package kuit.project.beering.service;

import kuit.project.beering.domain.*;
import kuit.project.beering.dto.request.review.ReviewCreateRequestDto;
import kuit.project.beering.dto.request.selectedOption.SelectedOptionCreateRequestDto;
import kuit.project.beering.dto.response.review.ReviewResponseDto;
import kuit.project.beering.repository.*;
import kuit.project.beering.util.exception.DrinkException;
import kuit.project.beering.util.exception.MemberException;
import kuit.project.beering.util.exception.ReviewException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static kuit.project.beering.util.BaseResponseStatus.*;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final SelectedOptionRepository selectedOptionRepository;
    private final ReviewOptionRepository reviewOptionRepository;
    private final DrinkRepository drinkRepository;
    private final MemberRepository memberRepository;

    public ReviewResponseDto save(Long memberId, Long drinkId, ReviewCreateRequestDto requestDto) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(NONE_MEMBER));

        Drink drink = drinkRepository.findById(drinkId)
                .orElseThrow(() -> new DrinkException(NONE_DRINK));
        List<ReviewOption> reviewOptions = reviewOptionRepository.findAllByCategoryId(drink.getCategory().getId());

        Review review = requestDto.toEntity(member, drink);
        reviewRepository.save(review);

        List<SelectedOptionCreateRequestDto> selectedOptionCreateRequestDtos = requestDto.getSelectedOptions().stream()
                .collect(Collectors.toList());

        List<SelectedOption> selectedOptions = new ArrayList<>();

        for (SelectedOptionCreateRequestDto dto : selectedOptionCreateRequestDtos) {
            for(ReviewOption reviewOption : reviewOptions) {
                if (dto.getReviewOptionId() == reviewOption.getId()) {
                    selectedOptions.add(dto.toEntity(review, reviewOption));
                }
            }
        }

        log.info("ReviewOptionList size = {}", reviewOptions.size());
        log.info("SelectedOptionCreateRequestDtoList size= {}", selectedOptionCreateRequestDtos.size());
        log.info("SelectedOptionList size = {}", selectedOptions.size());

        if(selectedOptionCreateRequestDtos.size() != selectedOptions.size())
            throw new ReviewException(UNMATCHED_OPTION_SIZE);
        selectedOptionRepository.saveAll(selectedOptions);

        return ReviewResponseDto.of(review);
    }
}
