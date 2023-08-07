package kuit.project.beering.dto.response;

import lombok.Getter;
import org.springframework.data.domain.Slice;

import java.util.List;

@Getter
public class SliceResponse<T> {
    List<T> content;
    private int page;
    private boolean isLast;

    public SliceResponse(Slice<T> content){
        this.content = content.getContent();
        this.page = content.getNumber();
        this.isLast = content.isLast();
    }

    public SliceResponse(List<T> content, int page, boolean isLast){
        this.content = content;
        this.page = page;
        this.isLast = isLast;
    }

}
