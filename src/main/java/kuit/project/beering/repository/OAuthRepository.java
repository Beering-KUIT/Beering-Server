package kuit.project.beering.repository;

import kuit.project.beering.domain.OAuth;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OAuthRepository extends JpaRepository<OAuth, Long> {

    Optional<OAuth> findBySub(String sub);
}
