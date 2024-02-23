package kuit.project.beering.util.exception.domain;

import kuit.project.beering.util.BaseResponseStatus;
import lombok.Getter;

@Getter
public class RecordException extends DomainException {

    public RecordException(BaseResponseStatus status) {
        super(status);
    }
}