package kuit.project.beering.util.exception;

import kuit.project.beering.util.BaseResponseStatus;
import lombok.Getter;

@Getter
public class MemberException extends RuntimeException {
    private final BaseResponseStatus status;

    public MemberException(BaseResponseStatus status) {
        this.status = status;
    }
}
