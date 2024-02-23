package kuit.project.beering.util.exception.domain;

import kuit.project.beering.util.BaseResponseStatus;
import lombok.Getter;

@Getter
public class MemberException extends DomainException {

    public MemberException(BaseResponseStatus status) {
        super(status);
    }
}