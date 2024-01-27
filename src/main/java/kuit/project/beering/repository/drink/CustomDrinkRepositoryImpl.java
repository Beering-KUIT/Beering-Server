package kuit.project.beering.repository.drink;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import kuit.project.beering.domain.Beer;
import kuit.project.beering.domain.Wine;
import kuit.project.beering.dto.request.drink.DrinkSearchCondition;
import kuit.project.beering.dto.response.drink.DrinkSearchResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;

import static kuit.project.beering.domain.QBeer.beer;
import static kuit.project.beering.domain.QDrink.drink;
import static kuit.project.beering.domain.QDrinkTag.drinkTag;
import static kuit.project.beering.domain.QFavorite.favorite;
import static kuit.project.beering.domain.QTag.tag;
import static kuit.project.beering.domain.QWine.wine;
import static kuit.project.beering.domain.image.QDrinkImage.drinkImage;

@Slf4j
public class CustomDrinkRepositoryImpl implements CustomDrinkRepository {
    private final JPAQueryFactory jpaQueryFactory;

    public CustomDrinkRepositoryImpl(EntityManager em) {
        this.jpaQueryFactory = new JPAQueryFactory(em);
    }

    public Slice<DrinkSearchResponse> search(DrinkSearchCondition condition, Pageable pageable){
        List<DrinkSearchResponse> drinks = jpaQueryFactory
                                .select(Projections.constructor(DrinkSearchResponse.class,
                                        drink.id, drink.nameKr, drink.nameEn, drink.manufacturer, drink.avgRating, drink.countOfReview,
                                        Expressions.constant(Collections.emptyList()), // 우선 빈 리스트
                                        Expressions.as(
                                                Expressions.cases()
                                                        .when(favorite.id.isNotNull()).then(true)
                                                        .otherwise(false),
                                                "isLiked"
                                        )
                                ))
                                .from(drink)
                                .where(containName(condition.getNameKr(), condition.getNameEn()),
                                        drink.price.between(condition.getMinPrice(), condition.getMaxPrice()),
                                        eqCategory(condition.getCategories()),
                                        eqTags(condition.getTags()),
                                        eqSweetness(condition.getSweetness()),
                                        eqCountry(condition.getCountry())
                                )
                                .orderBy(drinkSort(pageable))
                                .offset(pageable.getOffset())
                                .limit(pageable.getPageSize() + 1)
                                .leftJoin(favorite)
                                .on(drink.id.eq(favorite.drink.id).and(favorite.member.id.eq(condition.getMemberId())))
                                .leftJoin(wine).on(drink.id.eq(wine.id).and(drink.instanceOf(Wine.class)))
                                .leftJoin(beer).on(drink.id.eq(beer.id).and(drink.instanceOf(Beer.class)))
                                .fetchJoin()
                                .fetch();

        for (DrinkSearchResponse response : drinks) {
            Long drinkId = response.getDrinkId();
            List<String> imageUrlList = getImageUrlsByDrinkId(drinkId);
            response.setImageUrlList(imageUrlList);
        }

        boolean hasNext = false;
        if(pageable.getPageSize() < drinks.size()){
            drinks.remove(pageable.getPageSize());
            hasNext = true;
        }

        return new SliceImpl<>(drinks, pageable, hasNext);

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

    private BooleanExpression eqSweetness(Integer sweetness) {
        if(sweetness==null)
            return null;

        return wine.sweetness.eq(sweetness);
    }

    private BooleanExpression eqCountry(String country) {
        if(!StringUtils.hasText(country))
            return null;

        return wine.country.eq(country).or(beer.country.eq(country));
    }

    private BooleanExpression eqTags(List<String> tags) {
        if(tags==null)
            return null;

        return drink.id.in(
                jpaQueryFactory
                        .select(drinkTag.drink.id)
                        .from(drinkTag)
                        .innerJoin(drinkTag.tag, tag)
                        .where(tag.value.in(tags))
        );
    }


    private BooleanExpression eqCategory(List<String> categories) {
        if(categories == null)
            return null;

        BooleanExpression categoryConditions = null;

        for (String category : categories) {
            BooleanExpression condition = drink.category.name.eq(category);
            if (categoryConditions == null) {
                categoryConditions = condition;
            } else {
                categoryConditions = categoryConditions.or(condition);
            }
        }
        return categoryConditions;
    }

    public BooleanExpression containName(String nameKr, String nameEn){
        if (!StringUtils.hasText(nameKr) && !StringUtils.hasText(nameEn))
            return null;
        return drink.nameKr.contains(nameKr).or(drink.nameEn.contains(nameEn));
    }

}