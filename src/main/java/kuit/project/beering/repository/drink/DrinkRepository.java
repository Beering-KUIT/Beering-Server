package kuit.project.beering.repository.drink;

import kuit.project.beering.domain.Drink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DrinkRepository extends JpaRepository<Drink, Long>, CustomDrinkRepository {
}
