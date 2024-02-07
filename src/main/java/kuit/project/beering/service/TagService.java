package kuit.project.beering.service;

import kuit.project.beering.domain.Tag;
import kuit.project.beering.dto.response.tag.GetTagDetailResponse;
import kuit.project.beering.dto.response.tag.GetTagResponse;
import kuit.project.beering.repository.TagRepository;
import kuit.project.beering.util.exception.domain.TagException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static kuit.project.beering.util.BaseResponseStatus.NONE_TAG;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class TagService {

    private final TagRepository tagRepository;

    public List<GetTagResponse> getAllTags() {
        return tagRepository.findAll().stream()
                .map(tag -> GetTagResponse.builder()
                        .tagId(tag.getId())
                        .tagName(tag.getValue())
                        .build())
                .collect(Collectors.toList());
    }

    public GetTagDetailResponse getTagDetail(Long tagId) {
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new TagException(NONE_TAG));

        return GetTagDetailResponse.builder()
                .tagId(tag.getId())
                .tagName(tag.getValue())
                .description(tag.getDescription())
                .build();
    }

}
