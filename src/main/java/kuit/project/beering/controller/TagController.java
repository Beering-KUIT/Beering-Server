package kuit.project.beering.controller;

import kuit.project.beering.dto.response.drink.GetDrinkResponse;
import kuit.project.beering.dto.response.tag.GetTagDetailResponse;
import kuit.project.beering.service.TagService;
import kuit.project.beering.util.BaseResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static kuit.project.beering.util.BaseResponseStatus.NONE_TAG;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping
public class TagController {

    private static final Logger logger = LoggerFactory.getLogger(TagController.class);
    private final TagService tagService;

    /**
     * tagId 를 받아, 해당 tag 의 description return
     */
    @GetMapping("/tags/{tagId}")
    public BaseResponse<GetTagDetailResponse> getTagDetail(@PathVariable Long tagId){

        logger.info("[tagId] : " + tagId);

        GetTagDetailResponse getTagDetailResponse = tagService.getTagDetail(tagId);

        if (getTagDetailResponse == null) {
            return new BaseResponse<>(NONE_TAG);
        }
        return new BaseResponse<>(getTagDetailResponse);
    }

}
