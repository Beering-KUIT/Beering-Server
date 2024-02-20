package kuit.project.beering.repository;

import kuit.project.beering.domain.Record;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RecordRepository extends JpaRepository<Record, Long> {
    @Query("SELECT r FROM Record r WHERE r.member.id = :memberId AND YEAR(r.date) = :year AND MONTH(r.date) = :month")
    List<Record> findByDateAndMemberId(@Param("year") int year, @Param("month") int month, @Param("memberId") Long memberId);

    @Query("select r from Record r " +
            "where r.member.id = :memberId")
    List<Record> findAllRecordsByMemberId(@Param("memberId") Long memberId);
}
