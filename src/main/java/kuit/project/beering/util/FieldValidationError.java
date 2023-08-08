package kuit.project.beering.util;

import lombok.*;

@AllArgsConstructor
@Getter
public class FieldValidationError {

    private String fieldName;
    private String rejectValue;
    private String message;
}
