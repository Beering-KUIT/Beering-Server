package kuit.project.beering.dto.request.member;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MemberSignupRequest {

    @Email(regexp = "[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}", message = "이메일 형식 확인")
    @Length(min = 5 ,max = 320, message = "5 ~ 320자리 이내")
    private String username;

    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).*$", message = "영문, 숫자, 특수문자 혼합")
    @Length(min = 5, max = 20, message = "8 ~ 20자리 이내")
    private String password;

    @Pattern(regexp = "[a-zA-Zㄱ-ㅎㅏ-ㅣ가-힣]+", message = "영문 또는 한글")
    @Length(min = 1, max = 10, message = "1 ~ 10자리 이내")
    private String nickname;

    private List<AgreementRequest> agreements = new ArrayList<>();
}
