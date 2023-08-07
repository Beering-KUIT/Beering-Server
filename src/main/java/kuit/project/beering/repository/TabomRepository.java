package kuit.project.beering.repository;

import kuit.project.beering.domain.Tabom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TabomRepository extends JpaRepository<Tabom, Long> {
    @Query("select count(t) " +
            "from Tabom t " +
            "where t.isUp = true " +
            "and t.review.id = :reviewId ")
    Optional<Long> findCountByIsUPAndReviewId(@Param("reviewId") Long reviewId);

    @Query("select count(t) " +
            "from Tabom t " +
            "where t.isUp = false " +
            "and t.review.id = :reviewId ")
    Optional<Long> findCountByIsDownAndReviewId(@Param("reviewId") Long reviewId);
    boolean existsByReviewIdAndMemberId(Long memberId, Long reviewId);

    Long countByReviewIdAndIsUp(Long reviewId, boolean isUp);

    Tabom findByReviewIdAndMemberId(Long memberId, Long reviewId);
}
