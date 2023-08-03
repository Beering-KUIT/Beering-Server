package kuit.project.beering.repository;

import kuit.project.beering.domain.Review;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findTop5ByDrinkIdOrderByCreatedAtDesc(Long beerId);

    @Query("select r from Review r " +
            "where r.member.id = :memberId " +
            "order by r.createdAt desc ")
    Slice<Review> findAllReviewsSliceByMemberIdByCreatedAtDesc(@Param("memberId")Long memberId, Pageable pageable);

    @Query(value = "select r from Review r " +
            "right join fetch Tabom t on t.review.id = r.id " +
            "where t.member.id = ?1 and t.isUp = true")
    Slice<Review> findByMemberIdAndUpTabom(Long memberId, Pageable pageable);

    @Query("select r from Review r " +
            "order by r.createdAt desc ")
    Slice<Review> findAllReviewsSliceByCreatedAtDesc(Pageable pageable);

}
