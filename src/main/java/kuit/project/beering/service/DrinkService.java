package kuit.project.beering.service;

import kuit.project.beering.domain.Drink;
import kuit.project.beering.domain.Image;
import kuit.project.beering.dto.response.drink.DrinkSearchResponse;
import kuit.project.beering.repository.DrinkRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DrinkService {
    private final DrinkRepository drinkRepository;
    private final int SIZE = 10;

    public Page<DrinkSearchResponse> searchDrinksByName(String name, String orderBy, int page) {
        /* 페이징 & 정렬 설정 */
        Sort.Direction sortDirection = Sort.Direction.ASC;
        Sort.Order order = new Sort.Order(sortDirection, "createdAt");

        switch (orderBy) {
            case "name" -> {
                order = new Sort.Order(sortDirection, "nameKr");
            }
            case "rating" -> {
                sortDirection = Sort.Direction.DESC;
                order = new Sort.Order(sortDirection, "totalRating");
            }
            case "review" -> {
                sortDirection = Sort.Direction.DESC;
                order = new Sort.Order(sortDirection, "countOfReview");
            }
            case "price" -> {
                order = new Sort.Order(sortDirection, "price");
            }
        }

        Pageable pageable = PageRequest.of(page, SIZE, Sort.by(order));

        Page<Drink> drinkPage = drinkRepository.findByNameKrContainingOrNameEnContainingIgnoreCase(name, name, pageable);
        List<Drink> drinkList = drinkPage.getContent();

        List<DrinkSearchResponse> responseList;

        responseList = drinkList.stream()
                        .map(drink -> new DrinkSearchResponse(
                            drink.getId(),
                            getTop1DrinkImgUrl(drink),
                            drink.getNameKr(),
                            drink.getNameEn(),
                            drink.getManufacturer()))
                        .collect(Collectors.toList());

        return new PageImpl<>(responseList, pageable, drinkPage.getTotalElements());
    }

    private String getTop1DrinkImgUrl(Drink drink){
        String imgUrl = null;
        List<Image> images;
        if((images = drink.getImages()).size()!=0){
            imgUrl = images.get(0).getImageUrl();
        }
        return imgUrl;
    }
}
