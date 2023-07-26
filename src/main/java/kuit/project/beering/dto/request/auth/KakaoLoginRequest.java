package kuit.project.beering.dto.request.auth;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class KakaoLoginRequest {

    private String code;
    private String error;
    private String errorDescription;
    private String state;
}
