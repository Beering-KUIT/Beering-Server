package kuit.project.beering.security.auth.oauth.service;

import kuit.project.beering.repository.MemberRepository;
import kuit.project.beering.repository.OAuthRepository;
import kuit.project.beering.security.auth.oauth.client.OAuthClient;
import kuit.project.beering.security.auth.oauth.properties.OAuthProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@Qualifier("kakaoClientService")
public class KakaoClientService extends AbstractOAuthClientService {

    public KakaoClientService(
            MemberRepository memberRepository,
            OAuthRepository oauthRepository,
            @Qualifier("KakaoOauthProperties") OAuthProperties oauthProperties,
            @Qualifier("kakaoOauthClient") OAuthClient oauthClient) {

        super(memberRepository, oauthRepository, oauthProperties, oauthClient);
    }

}