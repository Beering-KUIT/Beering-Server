package kuit.project.beering.dto.request.record;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class AddRecordRequest {
    @NotNull(message = "volume(용량)은 null일 수 없습니다.")
    @Min(value = 1, message = "최소 용량은 1입니다.")
    private Integer volume;

    @NotNull(message = "volume(용량)의 quantity(개수)는 null일 수 없습니다.")
    @Min(value = 1, message = "최소 개수는 1입니다.")
    private Integer quantity;
}
