package kuit.project.beering.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Agreement extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "agreement_member_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AgreementName name;

    @Column(nullable = false)
    private Boolean isAgreed;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    //연관관계 mapping
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    public static Agreement createAgreementMember(Boolean isAgreed, String name, Member member) {
        Agreement agreement = new Agreement();
        agreement.isAgreed = isAgreed;
        agreement.name = AgreementName.valueOf(name);
        agreement.member = member;

        member.getAgreements().add(agreement);
        return agreement;
    }
}
