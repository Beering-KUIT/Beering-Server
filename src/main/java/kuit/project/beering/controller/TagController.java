package kuit.project.beering.controller;

import kuit.project.beering.dto.response.tag.GetFrequentTagResponse;
import kuit.project.beering.dto.response.tag.GetTagDetailResponse;
import kuit.project.beering.dto.response.tag.GetTagResponse;
import kuit.project.beering.security.auth.AuthMember;
import kuit.project.beering.service.TagService;
import kuit.project.beering.util.BaseResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping
public class TagController {

    private static final Logger logger = LoggerFactory.getLogger(TagController.class);
    private final TagService tagService;

    /**
     * 모든 태그를 받아오는 API
     * @return BaseResponse<List<GetTagResponse>>
     */
    @GetMapping("/tags")
    public BaseResponse<List<GetTagResponse>> getAllTags() {

        List<GetTagResponse> getTagResponses = tagService.getAllTags();

        return new BaseResponse<>(getTagResponses);
    }

    /**
     * tagId 를 받아, 해당 tag 의 description return
     */
    @GetMapping("/tags/{tagId}")
    public BaseResponse<GetTagDetailResponse> getTagDetail(@PathVariable Long tagId){

        logger.info("[tagId] : " + tagId);

        GetTagDetailResponse getTagDetailResponse = tagService.getTagDetail(tagId);

        return new BaseResponse<>(getTagDetailResponse);
    }

    @GetMapping("/tags/frequent-tags")
    public BaseResponse<GetFrequentTagResponse> getFrequentTags(@AuthenticationPrincipal AuthMember authMember) {

        // TODO : authMember isMember 처리
        GetFrequentTagResponse getFrequentTagResponse = tagService.getFrequentTags(authMember.getId());

        return new BaseResponse<>(getFrequentTagResponse);
    }
}
