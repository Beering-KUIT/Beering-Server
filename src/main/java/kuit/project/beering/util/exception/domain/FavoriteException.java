package kuit.project.beering.util.exception.domain;

import kuit.project.beering.util.BaseResponseStatus;
import lombok.Getter;

@Getter
public class FavoriteException extends DomainException {

    public FavoriteException(BaseResponseStatus status) {
        super(status);
    }
}