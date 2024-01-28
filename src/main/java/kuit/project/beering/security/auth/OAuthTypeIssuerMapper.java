package kuit.project.beering.security.auth;

import kuit.project.beering.domain.OAuthType;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class OAuthTypeIssuerMapper {

    private final Map<String, OAuthType> issuerOAuthTypeMap = new HashMap<>();

    public OAuthTypeIssuerMapper() {
        issuerOAuthTypeMap.put("https://kauth.kakao.com", OAuthType.KAKAO);
        issuerOAuthTypeMap.put("https://beering.com", OAuthType.BEERING);
    }

    public OAuthType get(String issuer) {
        return issuerOAuthTypeMap.get(issuer);
    }

}
