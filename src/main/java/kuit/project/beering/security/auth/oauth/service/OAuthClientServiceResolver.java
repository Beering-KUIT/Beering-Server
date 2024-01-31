package kuit.project.beering.security.auth.oauth.service;

import kuit.project.beering.domain.OAuthType;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class OAuthClientServiceResolver {

    private final Map<String, OAuthClientService> oauthClientServiceMap = new HashMap<>();

    public OAuthClientServiceResolver(@Qualifier("kakaoClientService") OAuthClientService oauthClientService) {
        oauthClientServiceMap.put(OAuthType.KAKAO.getValue(), oauthClientService);
    }

    /**
     * @Brief OAuthType 에 맞는 service 반환
     */
    public OAuthClientService getOauthClientService(OAuthType oauthType) {
        return oauthClientServiceMap.get(oauthType.getValue());
    }

    public OAuthClientService getOauthClientService(String issuer) {
        return oauthClientServiceMap.get(issuer);
    }

}
