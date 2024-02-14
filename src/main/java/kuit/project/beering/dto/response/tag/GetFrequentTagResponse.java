package kuit.project.beering.dto.response.tag;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class GetFrequentTagResponse {
    List<TagCount> tagCounts;
}
