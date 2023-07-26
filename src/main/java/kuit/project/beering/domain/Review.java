package kuit.project.beering.domain;

import jakarta.persistence.*;
import lombok.*;
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

    //리뷰 엔티티에 빌더 추가
    @Builder
    public Review(Member member, Drink drink, String content, float totalRating, Status status) {

        this.member = member;
        if (member != null) {
            member.addReview(this);
        }

        this.drink = drink;
        if (drink != null) {
            drink.addReview(this);
        }

        this.category = drink.getCategory();
        if (drink.getCategory() != null) {
            category.addReview(this);
        }

        this.content = content;
        this.totalRating = totalRating;
        this.status = status;
    }

    public void addSelectedOption(SelectedOption selectedOption) {
        this.selectedOptions.add(selectedOption);
    }

    public void clearImages() {
        this.images.clear();
    }
}
