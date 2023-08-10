package kuit.project.beering.security.auth.oauth.client;

import kuit.project.beering.redis.OIDCPublicKeysResponse;
import kuit.project.beering.security.jwt.OAuthTokenInfo;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Component
@Qualifier("kakaoTokenClient")
@FeignClient(
        name = "KakaoTokenClient",
        url = "${kakao.auth-url}")
public interface KakaoTokenClient extends OAuthTokenClient {

    /**
     * @Brief 공개 키 요청
     */
    @Override
    @Cacheable(cacheNames = "KakaoOICD", cacheManager = "redisCacheManager")
    @GetMapping("/.well-known/jwks.json")
    OIDCPublicKeysResponse getOIDCOpenKeys();

    /**
     * @Brief 토큰 발행 (로그인 시)
     */
    @Override
    @PostMapping(
            value = "/oauth/token?grant_type=authorization_code&client_id={CLIENT_ID}&redirect_uri={REDIRECT_URI}&code={CODE}&client_secret={CLIENT_SECRET}",
            headers = "Content-Type=application/x-www-form-urlencoded;charset=utf-8")
    OAuthTokenInfo getToken(
            @PathVariable("CLIENT_ID") String clientId,
            @PathVariable("REDIRECT_URI") String redirectUri,
            @PathVariable("CODE") String code,
            @PathVariable("CLIENT_SECRET") String client_secret);

    /**
     * @Brief 토큰 재발행
     */
    @Override
    @PostMapping(
            value = "/oauth/token?grant_type=refresh_token&client_id={CLIENT_ID}&refresh_token={REFRESH_TOKEN}&client_secret={SECRET_KEY}",
            headers = "Content-Type=application/x-www-form-urlencoded;charset=utf-8")
    OAuthTokenInfo reissueToken(
            @PathVariable("CLIENT_ID") String clientId,
            @PathVariable("REFRESH_TOKEN") String refreshToken,
            @PathVariable("SECRET_KEY") String secretKey);

}