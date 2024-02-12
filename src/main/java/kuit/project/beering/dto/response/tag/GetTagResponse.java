package kuit.project.beering.dto.response.tag;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetTagResponse {
    Long tagId;
    String tagName;
}
