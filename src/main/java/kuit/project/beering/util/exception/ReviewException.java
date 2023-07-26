package kuit.project.beering.util.exception;

import kuit.project.beering.util.BaseResponseStatus;
import lombok.Getter;

@Getter
public class ReviewException extends RuntimeException {
    private final BaseResponseStatus status;

    public ReviewException(BaseResponseStatus status) {
        this.status = status;
    }
}
