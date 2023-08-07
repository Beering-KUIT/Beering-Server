package kuit.project.beering.dto.request.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import kuit.project.beering.domain.OAuthType;
import kuit.project.beering.dto.request.member.AgreementRequest;
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
public class OAuthSignupRequest {

    @Pattern(regexp = "[a-zA-Z가-힣]+", message = "영문 또는 한글 1 ~ 10자리 이내")
    @Length(min = 1, max = 10, message = "영문 또는 한글 1 ~ 10자리 이내")
    private String nickname;

    @NotBlank
    private String sub;

    @NotNull
    private OAuthType oAuthType;

    private List<AgreementRequest> agreements = new ArrayList<>();
}
