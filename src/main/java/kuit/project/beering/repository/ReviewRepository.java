package kuit.project.beering.repository;

import kuit.project.beering.domain.Review;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
public interface ReviewRepository extends JpaRepository<Review, Long> {
    @Query("select r from Review r " +
            "left join fetch r.member m " +
            "where r.drink.id = :drinkId " +
            "order by (select count(t) from r.taboms t where t.isUp = true) desc " +
            "limit 5")
    List<Review> findTop5ByDrinkIdOrderByTabomsDesc(@Param("drinkId") Long drinkId);

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

    @Query("select r from Review r " +
            "where r.drink.id = :drinkId " +
            "order by r.createdAt desc ")
    Slice<Review> findAllReviewsSliceByDrinkIdByCreatedAtDesc(@Param("drinkId")Long drinkId, Pageable pageable);

    @Query("select r from Review r " +
            "where r.member.id = :memberId")
    List<Review> findAllReviewsByMemberId(@Param("memberId") Long memberId);
}
