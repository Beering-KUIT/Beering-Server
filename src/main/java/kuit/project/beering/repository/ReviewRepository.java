package kuit.project.beering.repository;

import kuit.project.beering.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findTop5ByDrinkIdOrderByCreatedAtDesc(Long beerId);
}
