package kuit.project.beering.util.exception.domain;

import kuit.project.beering.util.BaseResponseStatus;
import lombok.Getter;

@Getter
public class DomainException extends RuntimeException{
    private final BaseResponseStatus status;

    public DomainException(BaseResponseStatus status) {
        super(status.getResponseMessage());
        this.status = status;
    }

}
