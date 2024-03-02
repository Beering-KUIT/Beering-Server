package kuit.project.beering.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * @Brief 약관 삽입 시 사용되는 Dto
 */
@Getter
@AllArgsConstructor
@Builder
public class AgreementBulkInsertDto {

    private String name;
    private Boolean isAgreed;
    private String status;
    private Long memberId;
}
