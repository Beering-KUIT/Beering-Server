package kuit.project.beering.util;

import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.List;

@AllArgsConstructor
@Builder
public class FieldErrorsDto {

    List<FieldValidationError> errors;
}
