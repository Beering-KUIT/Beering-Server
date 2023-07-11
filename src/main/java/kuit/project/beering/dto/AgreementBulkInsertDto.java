package kuit.project.beering.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class AgreementBulkInsertDto {

    private String name;
    private Boolean isAgreed;
    private String status;
    private Long memberId;
}
