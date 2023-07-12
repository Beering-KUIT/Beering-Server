package kuit.project.beering.service;

import kuit.project.beering.repository.FavoriteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;

    // TODO : 유저의 주류 찜 여부 확인
    public boolean is_liked(Long drink_id) {
        // TODO : 토큰에서 userId 파싱하여 사용
        Long user_id = 1L;

        return favoriteRepository.existsByDrinkIdAndMemberId(drink_id, user_id);
    }

}
