package kuit.project.beering.security.auth.oauth.helper;

import kuit.project.beering.domain.OAuthType;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class OAuthHelperResolver {

    private final Map<OAuthType, OAuthHelper> oauthHelperMap = new HashMap<>();

    public OAuthHelperResolver(@Qualifier("kakaoOauthHelper") OAuthHelper oauthHelper) {
        oauthHelperMap.put(OAuthType.KAKAO, oauthHelper);
    }

    /**
     * @Brief OAuthType 에 맞는 helper 반환
     */
    public OAuthHelper getOauthHelper(OAuthType oauthType) {
        return oauthHelperMap.get(oauthType);
    }

}
