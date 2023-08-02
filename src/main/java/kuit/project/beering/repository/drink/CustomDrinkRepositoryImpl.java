package kuit.project.beering.repository.drink;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import kuit.project.beering.dto.request.drink.DrinkSearchCondition;
import kuit.project.beering.dto.response.drink.DrinkSearchResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;

import static kuit.project.beering.domain.QDrink.*;
import static kuit.project.beering.domain.QFavorite.favorite;
import static kuit.project.beering.domain.image.QDrinkImage.drinkImage;

@Slf4j
public class CustomDrinkRepositoryImpl implements CustomDrinkRepository {
    private final JPAQueryFactory jpaQueryFactory;

    public CustomDrinkRepositoryImpl(EntityManager em) {
        this.jpaQueryFactory = new JPAQueryFactory(em);
    }

    public Page<DrinkSearchResponse> search(DrinkSearchCondition condition, Pageable pageable){
        List<DrinkSearchResponse> content = jpaQueryFactory
                                .select(Projections.constructor(DrinkSearchResponse.class,
                                        drink.id, drink.nameKr, drink.nameEn, drink.manufacturer,
                                        Expressions.constant(Collections.emptyList()), // 우선 빈 리스트
                                        Expressions.as(
                                                Expressions.cases()
                                                        .when(favorite.id.isNotNull()).then(true)
                                                        .otherwise(false),
                                                "isLiked"
                                        )
                                ))
                                .from(drink)
                                .where(eqName(condition.getNameKr(), condition.getNameEn()),
                                        drink.price.between(condition.getMinPrice(), condition.getMaxPrice()),
                                        eqCategory(condition.getCategoryName()))
                                .orderBy(drinkSort(pageable))
                                .offset(pageable.getOffset())
                                .limit(pageable.getPageSize())
                                .leftJoin(favorite)
                                .on(drink.id.eq(favorite.drink.id).and(favorite.member.id.eq(condition.getMemberId())))
                                .fetchJoin()
                                .fetch();

        for (DrinkSearchResponse response : content) {
            Long drinkId = response.getDrinkId();
            List<String> imageUrlList = getImageUrlsByDrinkId(drinkId);
            response.setImageUrlList(imageUrlList);
        }

        JPAQuery<Long> countQuery = jpaQueryFactory
                                    .select(drink.count())
                                    .from(drink)
                                    .where(eqName(condition.getNameKr(), condition.getNameEn()),
                                            drink.price.between(condition.getMinPrice(), condition.getMaxPrice()),
                                            eqCategory(condition.getCategoryName()));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);

    }

    private List<String> getImageUrlsByDrinkId(Long drinkId) {
        List<String> imageUrls = jpaQueryFactory
                .select(drinkImage.imageUrl)
                .from(drinkImage)
                .where(drinkImage.drink.id.eq(drinkId))
                .fetch();
        return imageUrls != null ? imageUrls : Collections.emptyList();
    }

    private OrderSpecifier<?> drinkSort(Pageable pageable) {
        if (!pageable.getSort().isEmpty()) {
            for (Sort.Order order : pageable.getSort()) {
                Order direction = order.getDirection().isAscending() ? Order.ASC : Order.DESC;
                return switch (order.getProperty()) {
                    case "avgRating" -> new OrderSpecifier<>(direction, drink.avgRating);
                    case "countOfReview" -> new OrderSpecifier<>(direction, drink.countOfReview);
                    case "nameKr" -> new OrderSpecifier<>(direction, drink.nameKr);
                    case "price" -> new OrderSpecifier<>(direction, drink.price);
                    default -> new OrderSpecifier<>(direction, drink.id);
                };
            }
        }
        return null;
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