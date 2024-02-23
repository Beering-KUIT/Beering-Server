package kuit.project.beering.util.exception.domain;

import kuit.project.beering.util.BaseResponseStatus;
import lombok.Getter;

@Getter
public class ReviewException extends DomainException {

    public ReviewException(BaseResponseStatus status) {
        super(status);
    }
}