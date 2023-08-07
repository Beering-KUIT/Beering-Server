package kuit.project.beering.security.jwt;

import kuit.project.beering.repository.OAuthRepository;
import kuit.project.beering.security.jwt.jwtTokenProvider.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class JwtTokenProviderResolver {

    private final Map<String, JwtTokenProvider> providerMap = new HashMap<>();
    private final JwtParser jwtParser;
    private final OAuthRepository oauthRepository;
    private final String KAKAO_URL = "https://kauth.kakao.com";
    private final String BASIC_URL = "https://beering.com";

    public JwtTokenProviderResolver(
            @Qualifier("beeringJwtTokenProvider") JwtTokenProvider beeringJwtTokenProvider,
            @Qualifier("kakaoJwtTokenProvider") JwtTokenProvider kakaoJwtTokenProvider,
            JwtParser jwtParser,
            OAuthRepository oauthRepository
    ) {
        providerMap.put(BASIC_URL, beeringJwtTokenProvider);
        providerMap.put(KAKAO_URL, kakaoJwtTokenProvider);
        this.jwtParser = jwtParser;
        this.oauthRepository = oauthRepository;
    }

    /**
     * @Brief 토큰에 맞는 tokenProvider 반환
     */
    public JwtTokenProvider getProvider(String token) {
        if (isJwt(token)) return providerMap.get(parseIssuer(token));

        return providerMap.get(findOauthType(token));
    }

    private String parseIssuer(String token) {
        return jwtParser.parseClaimsField(token, "iss", String.class);
    }

    private boolean isJwt(String token) {
        return jwtParser.isJwt(token);
    }

    private String findOauthType(String refreshToken) {

        return switch (oauthRepository.findByRefreshToken(refreshToken)
                .orElseThrow(IllegalArgumentException::new).getOauthType()) {
            case KAKAO ->  KAKAO_URL;
        };
    }

}