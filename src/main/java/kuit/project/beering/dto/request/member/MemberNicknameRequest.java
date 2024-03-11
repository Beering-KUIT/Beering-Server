package kuit.project.beering.dto.request.member;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@NoArgsConstructor
@Getter
@Setter
public class MemberNicknameRequest {

    @Pattern(regexp = "[a-zA-Zㄱ-ㅎㅏ-ㅣ가-힣0-9]+", message = "영문, 한글, 숫자")
    @Length(min = 1, max = 10, message = "2 ~ 10자리 이내")
    private String nickname;
}
