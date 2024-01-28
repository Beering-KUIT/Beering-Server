package kuit.project.beering.security.jwt;

import kuit.project.beering.domain.OAuthType;
import kuit.project.beering.repository.OAuthRepository;
import kuit.project.beering.security.jwt.jwtTokenProvider.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class JwtTokenProviderResolver {

    private final JwtProviderMapper jwtProviderMapper;
    private final JwtParser jwtParser;
    private final OAuthRepository oauthRepository;

    /**
     * @Brief 토큰에 맞는 tokenProvider 반환
     */
    public JwtTokenProvider getProvider(String token) {
        if (isJwt(token)) return jwtProviderMapper.getProvider(parseIssuer(token));

        return jwtProviderMapper.getProvider(findOAuthType(token));
    }

    private String parseIssuer(String token) {
        return jwtParser.parseClaimsField(token, "iss", String.class);
    }

    private boolean isJwt(String token) {
        return jwtParser.isJwt(token);
    }

    private OAuthType findOAuthType(String refreshToken) {

        return oauthRepository.findByRefreshToken(refreshToken)
                .orElseThrow(IllegalArgumentException::new).getOauthType();
    }

}