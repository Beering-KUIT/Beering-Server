package kuit.project.beering.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.List;

@AllArgsConstructor
@Builder
public class FieldErrorsDto {

    List<FieldValidationError> errors;
}
