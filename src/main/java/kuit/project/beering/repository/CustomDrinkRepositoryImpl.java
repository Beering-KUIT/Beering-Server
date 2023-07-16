package kuit.project.beering.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import kuit.project.beering.domain.Drink;
import kuit.project.beering.dto.request.drink.DrinkSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.StringUtils;
import java.util.List;

import static kuit.project.beering.domain.QDrink.*;

public class CustomDrinkRepositoryImpl implements CustomDrinkRepository {
    private final JPAQueryFactory jpaQueryFactory;

    public CustomDrinkRepositoryImpl(EntityManager em) {
        this.jpaQueryFactory = new JPAQueryFactory(em);
    }

    public Page<Drink> search(DrinkSearchCondition condition, Pageable pageable){
        List<Drink> content = jpaQueryFactory
                                .selectFrom(drink)
                                .where(eqName(condition.getNameKr(), condition.getNameEn()),
                                        drink.price.between(condition.getMinPrice(), condition.getMaxPrice()),
                                        eqCategory(condition.getCategoryName()))
                                .offset(pageable.getOffset())
                                .limit(pageable.getPageSize())
                                .fetch();

        JPAQuery<Long> countQuery = jpaQueryFactory
                                    .select(drink.count())
                                    .from(drink)
                                    .where(eqName(condition.getNameKr(), condition.getNameEn()),
                                            drink.price.between(condition.getMinPrice(), condition.getMaxPrice()),
                                            eqCategory(condition.getCategoryName()));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);

    }

    private BooleanExpression eqCategory(String categoryName) {
        if(!StringUtils.hasText(categoryName))
            return null;
        return drink.category.name.eq(categoryName);
    }

    public BooleanExpression eqName(String nameKr, String nameEn){
        if (!StringUtils.hasText(nameKr))
            return null;
        return drink.nameKr.eq(nameKr).or(drink.nameEn.eq(nameEn));
    }

}