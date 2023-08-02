package kuit.project.beering.service;

import kuit.project.beering.domain.Drink;
import kuit.project.beering.domain.Favorite;
import kuit.project.beering.domain.Member;
import kuit.project.beering.repository.drink.DrinkRepository;
import kuit.project.beering.repository.FavoriteRepository;
import kuit.project.beering.repository.MemberRepository;
import kuit.project.beering.util.exception.DrinkException;
import kuit.project.beering.util.exception.FavoriteException;
import kuit.project.beering.util.exception.MemberException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static kuit.project.beering.util.BaseResponseStatus.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final DrinkRepository drinkRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void addToFavorite(Long memberId, Long drinkId) {
        Drink drink = drinkRepository.findById(drinkId)
                .orElseThrow(() -> new DrinkException(NONE_DRINK));

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(NONE_MEMBER));

        if (favoriteRepository.existsByDrinkIdAndMemberId(drinkId, memberId))
            throw new FavoriteException(POST_FAVORITE_ALREADY_CREATED);

        favoriteRepository.save(new Favorite(member, drink));
    }

}
