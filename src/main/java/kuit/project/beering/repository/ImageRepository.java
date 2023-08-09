package kuit.project.beering.repository;

import kuit.project.beering.domain.image.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {
}
