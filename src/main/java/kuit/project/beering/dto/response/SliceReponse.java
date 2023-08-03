package kuit.project.beering.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Slice;

import java.util.List;

@Getter
public class SliceReponse<T> {
    List<T> content;
    private int page;
    private boolean isLast;

    public SliceReponse(Slice<T> content){
        this.content = content.getContent();
        this.page = content.getNumber();
        this.isLast = content.isLast();
    }

    public SliceReponse(List<T> content, int page, boolean isLast){
        this.content = content;
        this.page = page;
        this.isLast = isLast;
    }

}
