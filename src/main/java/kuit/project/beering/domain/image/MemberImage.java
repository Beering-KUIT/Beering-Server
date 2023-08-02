package kuit.project.beering.domain.image;

import jakarta.persistence.*;
import kuit.project.beering.domain.Member;
import kuit.project.beering.domain.Review;
import kuit.project.beering.domain.image.Image;

@Entity
@DiscriminatorValue("member")
public class MemberImage extends Image {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;
}
