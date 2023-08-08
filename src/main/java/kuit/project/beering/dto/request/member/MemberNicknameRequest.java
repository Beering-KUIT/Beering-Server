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

    @Pattern(regexp = "[a-zA-Zㄱ-ㅎㅏ-ㅣ가-힣]+", message = "영문 또는 한글")
    @Length(min = 1, max = 10, message = "1 ~ 10자리 이내")
    private String nickname;
}
