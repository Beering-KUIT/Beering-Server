package kuit.project.beering.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AgreementRequest {

    private Long id;
    private Boolean isAgreed;
}
