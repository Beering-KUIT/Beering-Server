package kuit.project.beering.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class KakaoMemberInfo {

    private Long id;
    private String email;
    private String nickname;
}
