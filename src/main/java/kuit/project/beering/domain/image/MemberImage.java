package kuit.project.beering.domain.image;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import kuit.project.beering.domain.Member;
import kuit.project.beering.domain.image.Image;

@Entity
@DiscriminatorValue("member")
public class MemberImage extends Image {

    @OneToOne(mappedBy = "image")
    private Member member;
}
