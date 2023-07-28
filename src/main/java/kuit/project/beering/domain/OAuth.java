package kuit.project.beering.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "oauth")
public class OAuth extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "oauth_id")
    private Long id;

    private String sub;

    @Column @Enumerated(EnumType.STRING)
    private OAuthType oauthType;
    private String accessToken;
    private String refreshToken;

    @Enumerated(EnumType.STRING)
    private Status status;

    @OneToOne(mappedBy = "oauth")
    private Member member;

    public static OAuth createOauth(String sub, OAuthType oauthType, String accessToken, String refreshToken) {
        OAuth oauth = new OAuth();
        oauth.sub = sub;
        oauth.oauthType = oauthType;
        oauth.accessToken = accessToken;
        oauth.refreshToken = refreshToken;
        oauth.status = Status.DORMANT;
        return oauth;
    }

    public void tokenReissue(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        if (StringUtils.hasText(refreshToken)) this.refreshToken = refreshToken;
    }
}
