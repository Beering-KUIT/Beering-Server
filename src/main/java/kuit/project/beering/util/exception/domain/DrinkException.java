package kuit.project.beering.util.exception.domain;

import kuit.project.beering.util.BaseResponseStatus;
import lombok.Getter;

@Getter
public class DrinkException extends DomainException {

    public DrinkException(BaseResponseStatus status) {
        super(status);
    }

}