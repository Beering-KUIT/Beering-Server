package kuit.project.beering.security.auth.oauth.helper;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import kuit.project.beering.domain.OAuthType;
import kuit.project.beering.dto.OAuthMemberInfo;
import kuit.project.beering.redis.OIDCPublicKey;
import kuit.project.beering.redis.OIDCPublicKeysResponse;
import kuit.project.beering.repository.MemberRepository;
import kuit.project.beering.repository.OAuthRepository;
import kuit.project.beering.security.auth.oauth.client.OAuthClient;
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
import java.util.Date;

@Slf4j
public abstract class AbstractOAuthHelper implements OAuthHelper {

    protected final MemberRepository memberRepository;
    protected final OAuthRepository oauthRepository;
    protected final OAuthProperties oauthProperties;
    protected final OAuthClient oauthClient;

    public AbstractOAuthHelper(MemberRepository memberRepository, OAuthRepository oauthRepository, OAuthProperties oauthProperties, OAuthClient oauthClient) {
        this.memberRepository = memberRepository;
        this.oauthRepository = oauthRepository;
        this.oauthProperties = oauthProperties;
        this.oauthClient = oauthClient;
    }

    @Override
    public boolean validateToken(String token) {

        return isAvailable(
                token,
                oauthProperties.getBaseUrl(),
                oauthProperties.getRestapiKey(),
                oauthClient.getOIDCOpenKeys());
    }

    @Override
    public OAuthTokenInfo reissueToken(String refreshToken) {

        return oauthClient.reissueToken(oauthProperties.getRestapiKey(), refreshToken, oauthProperties.getClientSecret());
    }

    @Override
    public OAuthTokenInfo createToken(String code) {
        return oauthClient.getToken(
                oauthProperties.getRestapiKey(),
                oauthProperties.getRedirectUrl(),
                code,
                oauthProperties.getClientSecret());
    }

    @Override
    public OAuthType getOauthType() {
        return oauthProperties.getOAuthType();
    }

    @Override
    public OAuthMemberInfo getAccount(String accessToken) {
        return oauthClient.getOAuthAccount("Bearer " + accessToken);
    }

    private boolean isAvailable(
            String token, String iss, String aud, OIDCPublicKeysResponse oidcPublicKeysResponse) {

        // 여기서 기타 필드 검증
        String kid = getKidFromUnsignedIdToken(token, iss, aud);

        OIDCPublicKey oidcPublicKey =
                oidcPublicKeysResponse.getKeys().stream()
                        .filter(o -> o.getKid().equals(kid))
                        .findFirst()
                        .orElseThrow();

        // 여기서 서명 검증
        return signKey(token, oidcPublicKey.getN(), oidcPublicKey.getE());
    }

    private boolean signKey(String token, String modulus, String exponent) {

        try {
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(getRSAPublicKey(modulus, exponent)).build()
                    .parseClaimsJws(token);
            return claims.getBody().getExpiration().after(new Date());
        } catch (ExpiredJwtException e) {
            throw new CustomJwtException(BaseResponseStatus.EXPIRED_ACCESS_TOKEN);
        } catch (UnsupportedJwtException e) {
            throw new CustomJwtException(BaseResponseStatus.UNSUPPORTED_TOKEN_TYPE);
        } catch (SignatureException e) {
            throw new CustomJwtException(BaseResponseStatus.INVALID_SIGNATURE_JWT);
        } catch (MalformedJwtException e) {
            log.error("AbstractOAuthHelper.signKey");
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
        return (String) getUnsignedTokenClaims(token, iss, aud).getHeader().get("kid");
    }

    private Jwt<Header, Claims> getUnsignedTokenClaims(String token, String iss, String aud) {

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
        }catch (Exception e) {
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
