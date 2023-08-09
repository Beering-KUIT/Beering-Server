package kuit.project.beering.repository;

import kuit.project.beering.domain.OAuth;
import kuit.project.beering.domain.OAuthType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OAuthRepository extends JpaRepository<OAuth, Long> {

    Optional<OAuth> findBySubAndOauthType(String sub, OAuthType type);

    Optional<OAuth> findByRefreshToken(String refreshToken);
}
