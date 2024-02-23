package kuit.project.beering.util.exception.domain;

import kuit.project.beering.util.BaseResponseStatus;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public class DomainException extends RuntimeException{
    private final BaseResponseStatus status;

    public DomainException(BaseResponseStatus status) {
        super(status.getResponseMessage());
        this.status = status;
        log.info("{} - message : {}", this.getClass().getSimpleName(), status.getResponseMessage());
    }

}
