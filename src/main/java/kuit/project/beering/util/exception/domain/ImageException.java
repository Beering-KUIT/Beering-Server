package kuit.project.beering.util.exception.domain;

import kuit.project.beering.util.BaseResponseStatus;
import lombok.Getter;

@Getter
public class ImageException extends RuntimeException {
    private final BaseResponseStatus status;

    public ImageException(BaseResponseStatus status) {
        this.status = status;
    }
}
