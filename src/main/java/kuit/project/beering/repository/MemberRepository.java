package kuit.project.beering.repository;

import kuit.project.beering.domain.Member;
import kuit.project.beering.domain.OAuthType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByNickname(String nickname);

    @Query("SELECT m FROM Member m JOIN FETCH m.oauth o WHERE o.sub = :sub AND o.oauthType = :type")
    Optional<Member> findByOAuthSubAndOAuthType(@Param("sub") String sub, @Param("type") OAuthType type);

    @Query("SELECT m FROM Member m JOIN FETCH m.oauth o WHERE o.refreshToken = :refreshToken AND o.oauthType = :type")
    Optional<Member> findByOauthRefreshTokenAndOauthType(@Param("refreshToken") String splitRefreshToken, @Param("type") OAuthType oauthType);
}
