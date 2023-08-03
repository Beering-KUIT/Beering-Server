package kuit.project.beering.repository;

import kuit.project.beering.domain.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    boolean existsByDrinkIdAndMemberId(Long drinkId, Long userId);
    Favorite findByDrinkIdAndMemberId(Long drinkId, Long memberId);
}
