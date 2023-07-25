package kuit.project.beering.util.exception;

import kuit.project.beering.util.BaseResponseStatus;
import lombok.Getter;

@Getter
public class AwsException extends RuntimeException {
    private final BaseResponseStatus status;

    public AwsException(BaseResponseStatus status) {
        this.status = status;
    }
}
