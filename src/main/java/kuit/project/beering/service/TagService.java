package kuit.project.beering.service;

import kuit.project.beering.domain.Tag;
import kuit.project.beering.dto.response.tag.GetTagDetailResponse;
import kuit.project.beering.dto.response.tag.GetTagResponse;
import kuit.project.beering.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        Optional<Tag> result = tagRepository.findById(tagId);

        if (result.isPresent()) {
            Tag tag = result.get();

            return GetTagDetailResponse.builder()
                    .tagId(tag.getId())
                    .tagName(tag.getValue())
                    .description(tag.getDescription())
                    .build();
        }
        return null;
    }

}
