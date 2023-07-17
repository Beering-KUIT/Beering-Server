package kuit.project.beering.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class ErrorResponse {

    ObjectValidationError objectError;
    List<FieldValidationError> fieldErrors;
}
