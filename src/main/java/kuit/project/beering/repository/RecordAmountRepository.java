package kuit.project.beering.repository;

import kuit.project.beering.domain.RecordAmount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecordAmountRepository extends JpaRepository<RecordAmount, Long> {
}