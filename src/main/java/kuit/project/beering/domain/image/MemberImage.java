package kuit.project.beering.domain.image;

import jakarta.persistence.*;
import kuit.project.beering.domain.Member;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@DiscriminatorValue("member")
public class MemberImage extends Image {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    public MemberImage (Member member, String imageUrl, String uploadName) {
        super(imageUrl, uploadName);
        this.member = member;
        if(member != null)
            member.getImages().add(this);
    }

}
