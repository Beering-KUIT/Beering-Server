package kuit.project.beering.util.exception;

import io.jsonwebtoken.JwtException;
import kuit.project.beering.util.BaseResponseStatus;
import lombok.Getter;

@Getter
public class CustomJwtException extends JwtException {

    private final BaseResponseStatus status;

    public CustomJwtException(BaseResponseStatus status) {
        super(status.getResponseMessage());
        this.status = status;
    }
}
