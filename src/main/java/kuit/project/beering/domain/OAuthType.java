package kuit.project.beering.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum OAuthType {
    BEERING("https://beering.com"),
    KAKAO("https://kauth.kakao.com");

    private final String value;
}
