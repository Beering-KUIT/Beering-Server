package kuit.project.beering.dto.common;

import kuit.project.beering.domain.OAuthType;
import kuit.project.beering.dto.request.auth.OAuthSignupRequest;
import kuit.project.beering.dto.request.member.AgreementRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@AllArgsConstructor
@Getter
public class SignupContinueDto {

    private OAuthSignupRequest request;
    private OAuthType oAuthType;
    private String sub;
    private String email;

    public String getNickname() {
        return request.getNickname();
    }

    public String getAccessToken() {
        return request.getAccessToken();
    }

    public String getRefreshToken() {
        return request.getRefreshToken();
    }

    public List<AgreementRequest> getAgreements() {
        return request.getAgreements();
    }
}
