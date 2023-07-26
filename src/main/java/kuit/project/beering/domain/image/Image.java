package kuit.project.beering.domain.image;

import jakarta.persistence.*;
import kuit.project.beering.domain.BaseTimeEntity;
import kuit.project.beering.domain.Status;
import lombok.*;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Image extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String imageUrl;

    private String uploadName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;


    public Image (String imageUrl, String uploadName) {
        this.imageUrl = imageUrl;
        this.uploadName = uploadName;
        this.status = Status.ACTIVE;
    }
}
