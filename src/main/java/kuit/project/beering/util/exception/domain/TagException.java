package kuit.project.beering.util.exception.domain;

import kuit.project.beering.util.BaseResponseStatus;
import lombok.Getter;

@Getter
public class TagException extends DomainException {

    public TagException(BaseResponseStatus status) {
        super(status);
    }
}