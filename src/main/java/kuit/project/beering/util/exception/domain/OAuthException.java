package kuit.project.beering.util.exception.domain;

import kuit.project.beering.util.BaseResponseStatus;
import lombok.Getter;

@Getter
public class OAuthException extends DomainException {

    public OAuthException(BaseResponseStatus status) {
        super(status);
    }
}
