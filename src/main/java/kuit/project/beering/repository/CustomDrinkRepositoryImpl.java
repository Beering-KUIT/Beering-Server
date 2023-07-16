package kuit.project.beering.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import kuit.project.beering.domain.Drink;
import kuit.project.beering.dto.request.drink.DrinkSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public class CustomDrinkRepositoryImpl implements CustomDrinkRepository {
    private final JPAQueryFactory jpaQueryFactory;

    public CustomDrinkRepositoryImpl(EntityManager em) {
        this.jpaQueryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<Drink> search(DrinkSearchCondition drinkSearchCondition, Pageable pageable) {
        return null;
    }
}