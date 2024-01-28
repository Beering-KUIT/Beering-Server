package kuit.project.beering.security.jwt;

import kuit.project.beering.domain.OAuthType;
import kuit.project.beering.security.jwt.jwtTokenProvider.BeeringJwtTokenProvider;
import kuit.project.beering.security.jwt.jwtTokenProvider.JwtTokenProvider;
import kuit.project.beering.security.jwt.jwtTokenProvider.oidc.KakaoJwtTokenProvider;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class JwtProviderMapper {

    private final Map<String, JwtTokenProvider> providerMap = new HashMap<>();

    public JwtProviderMapper(BeeringJwtTokenProvider beeringJwtTokenProvider,
                             KakaoJwtTokenProvider kakaoJwtTokenProvider) {
        providerMap.put("https://beering.com", beeringJwtTokenProvider);
        providerMap.put("https://kauth.kakao.com", kakaoJwtTokenProvider);
    }

    public JwtTokenProvider getProvider(String issuer) {
        return providerMap.get(issuer);
    }

    public JwtTokenProvider getProvider(OAuthType oAuthType) {
        return providerMap.get(oAuthType.getValue());
    }
}
