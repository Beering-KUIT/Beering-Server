package kuit.project.beering.dto.request.record;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.util.List;

@Getter
public class AddRecordRequestList {
    @Valid
    List<AddRecordRequest> amounts;
}
