package kuit.project.beering.dto.response.tag;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class GetFrequentTagResponse {

    List<TagCount> tagCounts;
}
