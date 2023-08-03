package kuit.project.beering.dto.response.tabom;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class GetTabomResponsePage {
    List<GetTabomResponse> reviews;
    int page;
    boolean isLast;
}
