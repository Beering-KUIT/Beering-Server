package kuit.project.beering.security.auth.oauth.client;

import kuit.project.beering.dto.common.OAuthMemberInfo;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@Component
@Qualifier("kakaoInfoClient")
@FeignClient(
        name = "kakaoInfoClient",
        url = "${kakao.api-url}"
)
public interface KakaoInfoClient extends OAuthInfoClient{

    /**
     * @Brief oauth 계정 요청
     */
    @Override
    @GetMapping(
            value = "/v2/user/me",
            headers = "Content-Type=application/x-www-form-urlencoded;charset=utf-8",
            params = "property_keys=[\"kakao_account.email\"]"
    )
    OAuthMemberInfo getOAuthAccount(@RequestHeader("Authorization") String accessToken);

}
