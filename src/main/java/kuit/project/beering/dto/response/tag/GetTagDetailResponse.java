package kuit.project.beering.dto.response.tag;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetTagDetailResponse {
//    Long tagId;
    String tagName;
    String description;
}
