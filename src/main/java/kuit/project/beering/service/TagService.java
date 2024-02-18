package kuit.project.beering.service;

import kuit.project.beering.domain.Drink;
import kuit.project.beering.domain.DrinkTag;
import kuit.project.beering.domain.Record;
import kuit.project.beering.domain.Review;
import kuit.project.beering.domain.Tag;
import kuit.project.beering.dto.response.tag.GetFrequentTagResponse;
import kuit.project.beering.dto.response.tag.TagCount;
import kuit.project.beering.dto.response.tag.GetTagDetailResponse;
import kuit.project.beering.dto.response.tag.GetTagResponse;
import kuit.project.beering.repository.RecordRepository;
import kuit.project.beering.repository.ReviewRepository;
import kuit.project.beering.repository.TagRepository;
import kuit.project.beering.util.exception.domain.TagException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static kuit.project.beering.util.BaseResponseStatus.NONE_TAG;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class TagService {

    private final TagRepository tagRepository;
    private final ReviewRepository reviewRepository;
    private final RecordRepository recordRepository;

    public List<GetTagResponse> getAllTags() {
        log.info("TagService getAllTags 진입");

        return tagRepository.findAll().stream()
                .map(tag -> GetTagResponse.builder()
                        .tagId(tag.getId())
                        .tagName(tag.getValue())
                        .build())
                .collect(Collectors.toList());
    }

    public GetTagDetailResponse getTagDetail(Long tagId) {
        log.info("TagService getTagDetail 진입");

        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new TagException(NONE_TAG));

        return GetTagDetailResponse.builder()
                .tagId(tag.getId())
                .tagName(tag.getValue())
                .description(tag.getDescription())
                .build();
    }

    public GetFrequentTagResponse getFrequentTags(Long memberId) {
        log.info("TagService getFrequentTags 진입");

        Map<Tag, Integer> tagAndCounts = new HashMap<>();

        // 리뷰에 등록된 주류의 태그 집계
        reviewRepository.findAllReviewsByMemberId(memberId).stream()
                .map(Review::getDrink)
                .map(Drink::getDrinkTags)
                .forEach(drinkTags -> {
                    drinkTags.stream()
                            .map(DrinkTag::getTag)
                            .forEach(tag -> tagAndCounts.merge(tag, 1, Integer::sum));
                });

        // 기록에 등록된 주류의 태그 집계
        recordRepository.findAllRecordsByMemberId(memberId).stream()
                .map(Record::getDrink)
                .map(Drink::getDrinkTags)
                .forEach(drinkTags -> {
                    drinkTags.stream()
                            .map(DrinkTag::getTag)
                            .forEach(tag -> tagAndCounts.merge(tag, 1, Integer::sum));
                });

        // 가장 많이 등장한 태그 순으로 정렬하여 5개 태그 반환
        List<TagCount> tagCounts = new ArrayList<>();
        tagAndCounts.entrySet().stream()
                .sorted(Map.Entry.<Tag, Integer>comparingByValue().reversed())
                .limit(5)
                .forEach(tagIntegerEntry -> {
                    TagCount tagCount = TagCount.builder()
                            .tagId(tagIntegerEntry.getKey().getId())
                            .tagName(tagIntegerEntry.getKey().getValue())
                            .count(tagIntegerEntry.getValue())
                            .build();
                    tagCounts.add(tagCount);
                });

        return new GetFrequentTagResponse(tagCounts);
    }
}
