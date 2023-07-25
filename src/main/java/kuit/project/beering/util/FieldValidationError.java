package kuit.project.beering.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class FieldValidationError {

    private String fieldName;
    private String rejectValue;
    private String message;
}
