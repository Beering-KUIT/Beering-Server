package kuit.project.beering.dto.request.member;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MemberSignupRequest {

    private String username;
    private String password;
    private String nickname;

    private List<AgreementRequest> agreements = new ArrayList<>();
}
