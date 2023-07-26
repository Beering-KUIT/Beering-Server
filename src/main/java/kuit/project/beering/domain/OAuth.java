package kuit.project.beering.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OAuth extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "oauth_id")
    private Long id;

    private String sub;

    @Column(name = "type") @Enumerated(EnumType.STRING)
    private OAuthType oAuthType;
    private String accessToken;
    private String refreshToken;

    @Enumerated(EnumType.STRING)
    private Status status;

    @OneToOne(mappedBy = "oAuth")
    private Member member;

    public static OAuth createOAuth(String sub, OAuthType oAuthType, String accessToken, String refreshToken) {
        OAuth oAuth = new OAuth();
        oAuth.sub = sub;
        oAuth.oAuthType = oAuthType;
        oAuth.accessToken = accessToken;
        oAuth.refreshToken = refreshToken;
        oAuth.status = Status.DORMANT;
        return oAuth;
    }
}
