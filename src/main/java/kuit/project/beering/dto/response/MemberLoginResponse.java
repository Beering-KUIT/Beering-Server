package kuit.project.beering.dto.response;

import kuit.project.beering.security.jwt.JwtInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class MemberLoginResponse {

    private Long memberId;
    private JwtInfo jwtInfo;

}
