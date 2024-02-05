package kuit.project.beering.domain;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Record extends BaseTimeEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "record_id")
    private Long id;

    @Column(nullable = false)
    private Timestamp date;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "drink_id")
    private Drink drink;

    @OneToMany(mappedBy = "record")
    private List<RecordAmount> amounts = new ArrayList<>();

    public Record(Member member, Drink drink, Timestamp date){
        super();
        this.member = member;
        if(member != null)
            member.addRecord(this);

        this.drink = drink;
        if(drink != null)
            drink.addRecord(this);

        this.date = date;
        this.status = Status.ACTIVE;
    }

    public void addRecordAmount(RecordAmount recordAmount){
        this.amounts.add(recordAmount);
    }

    public Integer calculateTotalAmount() {
        return amounts.stream()
                .mapToInt(amount -> amount.getQuantity() * amount.getVolume())
                .sum();
    }
}
