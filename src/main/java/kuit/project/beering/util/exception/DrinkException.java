package kuit.project.beering.util.exception;

import kuit.project.beering.util.BaseResponseStatus;
import lombok.Getter;

@Getter
public class DrinkException extends RuntimeException {
    private final BaseResponseStatus status;

    public DrinkException(BaseResponseStatus status) {
        super(status.getResponseMessage());
        this.status = status;
    }

    public DrinkException(BaseResponseStatus status, String message) {
        super(message);
        this.status = status;
    }
}