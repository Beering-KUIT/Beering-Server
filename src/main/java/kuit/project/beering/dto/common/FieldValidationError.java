package kuit.project.beering.dto.common;

import lombok.*;

@AllArgsConstructor
@Getter
public class FieldValidationError {

    private String fieldName;
    private String rejectValue;
    private String message;
}
