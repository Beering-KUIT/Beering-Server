package kuit.project.beering.domain;

import jakarta.persistence.*;

@Entity
@DiscriminatorValue("drink")
public class DrinkImage extends Image{

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "drink_id")
    private Drink drink;
}
