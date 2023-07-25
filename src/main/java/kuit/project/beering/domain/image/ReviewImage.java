package kuit.project.beering.domain;

import jakarta.persistence.*;

@Entity
@DiscriminatorValue("review")
public class ReviewImage extends Image{

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    private Review review;
}
