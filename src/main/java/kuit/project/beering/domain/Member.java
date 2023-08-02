package kuit.project.beering.domain;

import jakarta.persistence.*;
import kuit.project.beering.domain.image.Image;
import kuit.project.beering.domain.image.MemberImage;
import kuit.project.beering.domain.image.ReviewImage;
import lombok.*;
import org.hibernate.annotations.Where;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Where(clause = "status = 'ACTIVE'")
public class Member extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "member_id", nullable = false)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    //연관관계 mapping
    @OneToMany(mappedBy = "member")
    private List<MemberImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<Agreement> agreements = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<Favorite> favorites = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<Tabom> taboms = new ArrayList<>();

    public void addReview(Review review) {
        this.reviews.add(review);
    }
    public void addFavorite(Favorite favorite) {
        this.favorites.add(favorite);
    }
    public void addTabom(Tabom tabom) {
        this.taboms.add(tabom);
    }
    public static Member createMember(String username, String password, String nickname) {
        Member member = new Member();
        member.username = username;
        member.password = password;
        member.nickname = nickname;

        member.status = Status.ACTIVE;
        member.role = Role.MEMBER;
        return member;
    }

    public void UpdateStatusToDormant() {
        this.status = Status.DORMANT;
    }
}
