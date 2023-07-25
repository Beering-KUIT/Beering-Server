package kuit.project.beering.domain;

import jakarta.persistence.*;
import kuit.project.beering.domain.image.Image;
import kuit.project.beering.domain.image.ReviewImage;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long id;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(nullable = false)
    private float totalRating;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    //연관관계 mapping
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "drink_id")
    private Drink drink;

    @OneToMany(mappedBy = "review")
    private List<SelectedOption> selectedOptions = new ArrayList<>();

    @OneToMany(mappedBy = "review")
    private List<ReviewImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "review")
    private List<Tabom> taboms = new ArrayList<>();

}
