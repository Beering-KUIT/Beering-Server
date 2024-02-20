package kuit.project.beering.dto.response.tag;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class TagCount {

    Long tagId;
    String tagName;
    Integer count;
}
