package kuit.project.beering.domain;

import jakarta.persistence.*;
import kuit.project.beering.domain.image.Image;
import kuit.project.beering.domain.image.MemberImage;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
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
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "image_id")
    private MemberImage image;

    @OneToMany(mappedBy = "member")
    private List<Agreement> agreements = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<Favorite> favorites = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<Tabom> taboms = new ArrayList<>();

    public static Member createMember(String username, String password, String nickname) {
        Member member = new Member();
        member.username = username;
        member.password = password;
        member.nickname = nickname;

        member.status = Status.ACTIVE;
        member.role = Role.MEMBER;
        return member;
    }
}
