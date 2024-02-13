package kuit.project.beering.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecordAmount extends BaseTimeEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "record_amount_id")
    private Long id;

    @Column(nullable = false, columnDefinition = "smallint")
    private Integer volume;

    @Column(nullable = false, columnDefinition = "tinyint")
    private Integer quantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "record_id")
    private Record record;

    public RecordAmount(Record record, Integer volume, Integer quantity){
        this.record = record;
        if(record!=null)
            this.record.addRecordAmount(this);

        this.volume = volume;
        this.quantity = quantity;
        this.status = Status.ACTIVE;
    }

}
