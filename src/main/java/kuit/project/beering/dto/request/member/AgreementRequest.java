package kuit.project.beering.dto.request.member;

import kuit.project.beering.domain.AgreementName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AgreementRequest {

    private AgreementName name;
    private Boolean isAgreed;
}
