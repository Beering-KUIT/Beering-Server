package kuit.project.beering.repository;

import kuit.project.beering.domain.OAuth;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OAuthRepository extends JpaRepository<OAuth, Long> {
}
