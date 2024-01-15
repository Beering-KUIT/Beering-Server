package kuit.project.beering.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DrinkTag extends BaseTimeEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "drink_tag_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "drink_id")
    private Drink drink;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id")
    private Tag tag;

    public DrinkTag(Drink drink, Tag tag){
        this.drink = drink;
        if(drink != null)
            this.drink.addDrinkTag(this);

        this.tag = tag;
        if(tag != null)
            this.tag.addDrinkTag(this);

        this.status = Status.ACTIVE;
    }

}