package kuit.project.beering.util.exception;

import kuit.project.beering.util.BaseResponseStatus;
import lombok.Getter;

@Getter
public class TabomException extends RuntimeException{
    private final BaseResponseStatus status;

    public TabomException(BaseResponseStatus status) {
        super(status.getResponseMessage());
        this.status = status;
    }

    public TabomException(BaseResponseStatus status, String message) {
        super(message);
        this.status = status;
    }
}
