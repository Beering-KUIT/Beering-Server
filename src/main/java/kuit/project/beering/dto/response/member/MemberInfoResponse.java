package kuit.project.beering.dto.response.member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class MemberInfoResponse {

    private String username;
    private String nickname;
    private String url;
}
