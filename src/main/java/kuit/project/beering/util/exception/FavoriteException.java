package kuit.project.beering.util.exception;

import kuit.project.beering.util.BaseResponseStatus;
import lombok.Getter;

@Getter
public class FavoriteException extends RuntimeException {
    private final BaseResponseStatus status;

    public FavoriteException(BaseResponseStatus status) {
        super(status.getResponseMessage());
        this.status = status;
    }

    public FavoriteException(BaseResponseStatus status, String message) {
        super(message);
        this.status = status;
    }
}