package kuit.project.beering.repository;

import kuit.project.beering.domain.Tabom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TabomRepository extends JpaRepository<Tabom, Long> {
    boolean existsByReviewIdAndMemberId(Long memberId, Long reviewId);

    Long countByReviewIdAndIsUp(Long reviewId, boolean isUp);

    Tabom findByReviewIdAndMemberId(Long memberId, Long reviewId);
}
