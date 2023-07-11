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

}
