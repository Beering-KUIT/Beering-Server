package kuit.project.beering.repository;

import kuit.project.beering.domain.RecordAmount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecordAmountRepository extends JpaRepository<RecordAmount, Long> {
    RecordAmount findByRecordIdAndVolume(Long recordId, Integer volume);

    List<RecordAmount> findAllByRecordId(Long recordId);

    @Modifying
    @Query("delete from RecordAmount ra where ra.id in :ids")
    void deleteAllByRecordAmountIds(@Param("ids") List<Long> recordAmountIds);
}