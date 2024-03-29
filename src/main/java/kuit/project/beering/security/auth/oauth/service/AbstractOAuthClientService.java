package kuit.project.beering.security.auth.oauth.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import kuit.project.beering.domain.OAuthType;
import kuit.project.beering.dto.common.OAuthMemberInfo;
import kuit.project.beering.redis.OIDCPublicKey;
import kuit.project.beering.redis.OIDCPublicKeysResponse;
import kuit.project.beering.security.auth.oauth.client.OAuthApiClient;
import kuit.project.beering.security.auth.oauth.client.OAuthTokenClient;
import kuit.project.beering.security.auth.oauth.properties.OAuthProperties;
import kuit.project.beering.security.jwt.OAuthTokenInfo;
import kuit.project.beering.util.BaseResponseStatus;
import kuit.project.beering.util.exception.CustomJwtException;
import lombok.extern.slf4j.Slf4j;

import java.math.BigInteger;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;

@Slf4j
public abstract class AbstractOAuthClientService implements OAuthClientService {

    private final OAuthProperties oauthProperties;
    private final OAuthTokenClient oauthTokenClient;
    private final OAuthApiClient oauthInfoClient;

    public AbstractOAuthClientService(
            OAuthProperties oauthProperties,
            OAuthTokenClient oauthTokenClient,
            OAuthApiClient oauthInfoClient) {
        this.oauthProperties = oauthProperties;
        this.oauthTokenClient = oauthTokenClient;
        this.oauthInfoClient = oauthInfoClient;
    }

    /**
     * @Brief 토큰 검증+
     */
    @Override
    public void validateToken(String token) {
        String iss = oauthProperties.getAuthUrl();
        String aud = oauthProperties.getRestapiKey();
        OIDCPublicKeysResponse oidcPublicKeysResponse = oauthTokenClient.getOIDCOpenKeys();

        // 기타 필드 검증
        String kid = getKidFromUnsignedIdToken(token, iss, aud);

        OIDCPublicKey oidcPublicKey =
                oidcPublicKeysResponse.getKeys().stream()
                        .filter(o -> o.getKid().equals(kid))
                        .findFirst()
                        .orElseThrow();

        // 서명 검증
        signKey(token, oidcPublicKey.getN(), oidcPublicKey.getE());
    }

    /**
     * @Brief 토큰 재발행
     */
    @Override
    public OAuthTokenInfo reissueToken(String refreshToken) {

        return oauthTokenClient.reissueToken(oauthProperties.getRestapiKey(), refreshToken, oauthProperties.getClientSecret());
    }

    /**
     * @Brief 토큰 발행 (로그인 시)
     */
    @Override
    public OAuthTokenInfo createToken(String code) {
        return oauthTokenClient.getToken(
                oauthProperties.getRestapiKey(),
                oauthProperties.getRedirectUrl(),
                code,
                oauthProperties.getClientSecret());
    }

    @Override
    public OAuthType getOauthType() {
        return oauthProperties.getOAuthType();
    }

    /**
     * @Brief oauth 계정 정보 요청
     */
    @Override
    public OAuthMemberInfo getAccount(String accessToken) {
        return oauthInfoClient.getOAuthAccount("Bearer " + accessToken);
    }

    /**
     * @Brief 공개 키 서명
     */
    private void signKey(String token, String modulus, String exponent) {

        try {
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(getRSAPublicKey(modulus, exponent)).build()
                    .parseClaimsJws(token);
        } catch (ExpiredJwtException e) {
            throw new CustomJwtException(BaseResponseStatus.EXPIRED_ID_TOKEN);
        } catch (UnsupportedJwtException e) {
            throw new CustomJwtException(BaseResponseStatus.UNSUPPORTED_TOKEN_TYPE);
        } catch (SignatureException e) {
            throw new CustomJwtException(BaseResponseStatus.INVALID_SIGNATURE_JWT);
        } catch (MalformedJwtException e) {
            throw new CustomJwtException(BaseResponseStatus.MALFORMED_TOKEN_TYPE);
        } catch (JwtException e) {
            log.error("[JwtTokenProvider.validateAccessToken]", e);
            throw e;
        } catch (Exception e) {
            log.error(e.toString());
            throw new CustomJwtException(BaseResponseStatus.INVALID_TOKEN_TYPE);
        }
    }

    private String getKidFromUnsignedIdToken(String token, String iss, String aud) {
        return (String) getValidatedFieldToken(token, iss, aud).getHeader().get("kid");
    }

    /**
     * @Brief 토큰 필드 검증
     */
    private Jwt<Header, Claims> getValidatedFieldToken(String token, String iss, String aud) {

        try {
            return Jwts.parserBuilder()
                    .requireAudience(aud)
                    .requireIssuer(iss)
                    .build()
                    .parseClaimsJwt(getUnsignedToken(token));
        } catch (ExpiredJwtException e) {
            throw new CustomJwtException(BaseResponseStatus.EXPIRED_ACCESS_TOKEN);
        } catch (UnsupportedJwtException e) {
            throw e;
        } catch (Exception e) {
            log.error(e.toString());
            throw new CustomJwtException(BaseResponseStatus.INVALID_TOKEN_TYPE);
        }
    }

    private String getUnsignedToken(String token) {
        String[] splitToken = token.split("\\.");
        if (splitToken.length != 3) throw new CustomJwtException(BaseResponseStatus.INVALID_TOKEN_TYPE);
        return splitToken[0] + "." + splitToken[1] + ".";
    }

    private Key getRSAPublicKey(String modulus, String exponent)
            throws NoSuchAlgorithmException, InvalidKeySpecException {

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        byte[] decodeN = Base64.getUrlDecoder().decode(modulus);
        byte[] decodeE = Base64.getUrlDecoder().decode(exponent);

        RSAPublicKeySpec keySpec = new RSAPublicKeySpec(
                new BigInteger(1, decodeN),
                new BigInteger(1, decodeE));

        return keyFactory.generatePublic(keySpec);
    }

}
