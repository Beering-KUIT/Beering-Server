package kuit.project.beering.domain.image;

import jakarta.persistence.*;
import kuit.project.beering.domain.Review;
import kuit.project.beering.domain.image.Image;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("review")
@NoArgsConstructor
@AllArgsConstructor
public class ReviewImage extends Image {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    private Review review;

    @Builder
    public ReviewImage (Review review, String imageUrl, String uploadName) {
        super(imageUrl, uploadName);
        this.review = review;
        if(review != null)
            review.getImages().add(this);
    }
}
