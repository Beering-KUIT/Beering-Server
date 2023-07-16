package kuit.project.beering.util.exception;

import kuit.project.beering.util.BaseResponseStatus;
import lombok.Getter;

@Getter
public class DrinkException extends RuntimeException {
    private final BaseResponseStatus status;

    public DrinkException(BaseResponseStatus status) {
        this.status = status;
    }
}