package kuit.project.beering.util.exception.domain;

import kuit.project.beering.util.BaseResponseStatus;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public class TabomException extends DomainException {

    public TabomException(BaseResponseStatus status) {
        super(status);
        log.info("{} - message : {}", this.getClass().getSimpleName(), status.getResponseMessage());
    }
}
