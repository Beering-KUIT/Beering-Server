package kuit.project.beering.util.exception.domain;

import kuit.project.beering.util.BaseResponseStatus;
import lombok.Getter;

@Getter
public class TabomException extends DomainException {

    public TabomException(BaseResponseStatus status) {
        super(status);
    }
}
