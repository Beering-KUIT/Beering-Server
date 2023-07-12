package kuit.project.beering.repository;

import kuit.project.beering.domain.Drink;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DrinkRepository extends JpaRepository<Drink, Long> {
    /* 주류 가져오기 - 이름 */
    Page<Drink> findByNameKrContainingOrNameEnContainingIgnoreCase(String nameKr, String nameEn, Pageable pageable);
}
