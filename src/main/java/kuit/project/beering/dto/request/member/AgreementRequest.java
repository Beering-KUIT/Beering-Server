package kuit.project.beering.dto.request.member;

import jakarta.validation.constraints.NotNull;
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

    @NotNull(message = "NULL 일 수 없음")
    private Boolean isAgreed;
}
