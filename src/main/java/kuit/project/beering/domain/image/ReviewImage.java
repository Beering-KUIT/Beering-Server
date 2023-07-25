package kuit.project.beering.domain.image;

import jakarta.persistence.*;
import kuit.project.beering.domain.Review;
import kuit.project.beering.domain.image.Image;

@Entity
@DiscriminatorValue("review")
public class ReviewImage extends Image {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    private Review review;
}
