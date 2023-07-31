package kuit.project.beering.repository;

import kuit.project.beering.domain.Review;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findTop5ByDrinkIdOrderByCreatedAtDesc(Long beerId);

    @Query("select r from Review r " +
            "where r.member.id = :memberId " +
            "order by r.createdAt desc ")
    List<Review> findAllReviewsSliceByMemberIdByCreatedAtDesc(@Param("memberId")Long memberId, Pageable pageable);
}
