package kuit.project.beering.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum DrinkType {
    BEER("맥주", "Beer"),
    WINE("와인", "Wine"),
    TRADITIONAL_LIQUOR("전통주", "Traditional Liquor"),
    WHISKEY("위스키", "Whiskey");

    private final String drinkTypeKr;
    private final String drinkTypeEn;

    public static DrinkType fromName(String name) {
        for (DrinkType drinkType : values()) {
            if (drinkType.drinkTypeKr.equals(name) || drinkType.drinkTypeEn.equals(name)) {
                return drinkType;
            }
        }
        return null;
    }
}
