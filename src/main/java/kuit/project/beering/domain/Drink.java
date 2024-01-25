package kuit.project.beering.domain;

import jakarta.persistence.*;
import kuit.project.beering.domain.image.DrinkImage;
import kuit.project.beering.domain.image.Image;
import lombok.AccessLevel;
import org.hibernate.annotations.Formula;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Where(clause = "status = 'ACTIVE'")
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

    // TODO : country table 논의
    @Column(nullable = true)
    private String country;

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

    @OneToMany(mappedBy = "drink")
    private List<DrinkTag> drinkTags = new ArrayList<>();

    @OneToMany(mappedBy = "drink")
    private List<Record> records = new ArrayList<>();

    public void addReview(Review review) {
        this.reviews.add(review);
    }
    public void addFavorite(Favorite favorite) {
        this.favorites.add(favorite);
    }

    public void addRecord(Record record) {
        this.records.add(record);
    }
    public void addDrinkTag(DrinkTag drinkTag) {
        this.drinkTags.add(drinkTag);
    }

    // 가상 칼럼
    @Basic(fetch = FetchType.LAZY)
    @Formula("(ifnull((select COUNT(1) from review where review.drink_id = d1_0.drink_id), 0))")
    private int countOfReview;

    @Basic(fetch = FetchType.LAZY)
    @Formula("(ifnull((select AVG(r.total_rating) from review as r where r.drink_id = d1_0.drink_id), 0.0))")
    private float avgRating;
}
