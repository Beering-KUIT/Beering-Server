package kuit.project.beering.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SelectedOption extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "selected_option_id")
    private Long id;

    @Column(nullable = false)
    private float rating;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    //연관관계 mapping
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_option_id")
    private ReviewOption reviewOption;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    private Review review;
}
