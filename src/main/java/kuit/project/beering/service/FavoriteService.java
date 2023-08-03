package kuit.project.beering.service;

import kuit.project.beering.domain.Drink;
import kuit.project.beering.domain.Favorite;
import kuit.project.beering.domain.Member;
import kuit.project.beering.dto.response.favorite.GetFavoriteDrinkResponse;
import kuit.project.beering.repository.drink.DrinkRepository;
import kuit.project.beering.repository.FavoriteRepository;
import kuit.project.beering.repository.MemberRepository;
import kuit.project.beering.util.BaseResponseStatus;
import kuit.project.beering.util.exception.DrinkException;
import kuit.project.beering.util.exception.FavoriteException;
import kuit.project.beering.util.exception.MemberException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static kuit.project.beering.util.BaseResponseStatus.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final DrinkRepository drinkRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public BaseResponseStatus saveFavorite(Long memberId, Long drinkId) {
        Drink drink = drinkRepository.findById(drinkId)
                .orElseThrow(() -> new DrinkException(NONE_DRINK));

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(NONE_MEMBER));

        Favorite favorite = favoriteRepository.findByDrinkIdAndMemberId(drinkId, memberId);

        if (favorite == null){
            favoriteRepository.save(new Favorite(member, drink));
            return SUCCESS_ADD_FAVORITE;
        }
        else{
            favoriteRepository.deleteById(favorite.getId());
            return SUCCESS_DELETE_FAVORITE;
        }
    }

    public Slice<GetFavoriteDrinkResponse> getFavoriteReviews(Long memberId, Pageable pageable) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(NONE_MEMBER));

        Slice<Drink> drinks = drinkRepository.findByMemberIdAndFavorite(member.getId(), pageable);

        List<GetFavoriteDrinkResponse> dto = drinks.stream()
                .map(this::getFavoriteDrinkResponseBuild)
                .collect(Collectors.toList());

        return new SliceImpl<>(dto, pageable, drinks.hasNext());
    }

    private GetFavoriteDrinkResponse getFavoriteDrinkResponseBuild(Drink drink) {
        return GetFavoriteDrinkResponse.builder()
                .drinkId(drink.getId())
                .nameEn(drink.getNameEn())
                .nameKr(drink.getNameKr())
                .manufacturer(drink.getManufacturer())
                .primaryImageUrl(getPrimaryImage(drink))
                .build();
    }

    private String getPrimaryImage(Drink drink) {
        if(drink.getImages().size() == 0)
            return null;
        return drink.getImages().get(0).getImageUrl();
    }
}