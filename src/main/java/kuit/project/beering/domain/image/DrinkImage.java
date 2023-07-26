package kuit.project.beering.domain.image;

import jakarta.persistence.*;
import kuit.project.beering.domain.Drink;
import kuit.project.beering.domain.image.Image;

@Entity
@DiscriminatorValue("drink")
public class DrinkImage extends Image {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "drink_id")
    private Drink drink;
}
