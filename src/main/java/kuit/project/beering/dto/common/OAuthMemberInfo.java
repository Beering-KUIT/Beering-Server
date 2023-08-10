package kuit.project.beering.dto.common;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class OAuthMemberInfo {

    private String id;

    private Properties properties;
    private KakaoAccount kakaoAccount;

    @Getter
    @NoArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class Properties {
        private String nickname;
    }

    @Getter
    @NoArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class KakaoAccount {

        private Profile profile;
        private String email;
        private String phoneNumber;
        private String name;

        @Getter
        @NoArgsConstructor
        @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
        public static class Profile {
            private String profileImageUrl;
        }

        public String getProfileImageUrl() {
            return profile.getProfileImageUrl();
        }
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return kakaoAccount.getEmail();
    }

    public String getPhoneNumber() {
        return kakaoAccount.getPhoneNumber();
    }

    public String getName() {
        return kakaoAccount.getName() != null ? kakaoAccount.getName() : properties.getNickname();
    }

    public String getProfileUrl() {
        return kakaoAccount.getProfileImageUrl();
    }
}
