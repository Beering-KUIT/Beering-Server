package kuit.project.beering.dto.request.member;

import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@NoArgsConstructor
@Getter
@Setter
public class MemberEmailRequest {

    @Email(regexp = "[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}", message = "이메일 형식 확인")
    @Length(min = 5 ,max = 320, message = "최대 320자리 이내")
    private String username;
}
