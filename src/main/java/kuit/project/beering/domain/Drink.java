package kuit.project.beering.domain;

import jakarta.persistence.*;
import kuit.project.beering.domain.image.DrinkImage;
import kuit.project.beering.domain.image.Image;
import lombok.AccessLevel;
import org.hibernate.annotations.Formula;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Drink extends BaseTimeEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "drink_id")
    private Long id;

    @Column(nullable = false)
    private String nameKr;

    @Column(nullable = false)
    private String nameEn;

    @Column(nullable = false)
    private int price;

    @Column(nullable = false)
    private float alcohol;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(nullable = false)
    private String manufacturer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    //연관관계 mapping
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "drink")
    private List<Favorite> favorites = new ArrayList<>();

    @OneToMany(mappedBy = "drink")
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "drink")
    private List<DrinkImage> images = new ArrayList<>();

    // 가상 칼럼
    @Basic(fetch = FetchType.LAZY)
    @Formula("(ifnull((select COUNT(1) from review where review.drink_id = d1_0.drink_id), 0))")
    private int countOfReview;

    @Basic(fetch = FetchType.LAZY)
    @Formula("(ifnull((select AVG(r.total_rating) from review as r where r.drink_id = d1_0.drink_id), 0.0))")
    private float avgRating;
}