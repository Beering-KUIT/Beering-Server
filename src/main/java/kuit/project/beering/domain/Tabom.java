package kuit.project.beering.domain;

import jakarta.persistence.*;
import lombok.Getter;


@Entity
@Getter
public class Tabom extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tabom_id", nullable = false)
    private Long id;

    @Column(nullable = false)
    private Boolean isUp;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "review_id")
    private Review review;

    public Tabom(Member member, Review review, boolean isUp) {
        super();
        this.member = member;
        if(member != null)
            member.addTabom(this);

        this.review = review;
        if(review != null)
            review.addTabom(this);

        this.isUp = isUp;
        this.status = Status.ACTIVE;
    }
}
