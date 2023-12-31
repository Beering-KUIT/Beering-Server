package kuit.project.beering.domain.image;

import jakarta.persistence.*;
import kuit.project.beering.domain.Review;
import lombok.AllArgsConstructor;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("review")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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
