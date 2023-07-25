package kuit.project.beering.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;

@Entity
@DiscriminatorValue("member")
public class MemberImage extends Image{

    @OneToOne(mappedBy = "image")
    private Member member;
}
