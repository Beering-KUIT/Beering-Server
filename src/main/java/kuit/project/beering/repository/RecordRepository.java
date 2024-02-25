package kuit.project.beering.repository;

import kuit.project.beering.domain.Record;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;
import java.util.List;

public interface RecordRepository extends JpaRepository<Record, Long> {
    Record findByDateAndMemberIdAndDrinkId(Timestamp date, Long memberId, Long drinkId);

    List<Record> findAllByDateAndMemberId(Timestamp date, Long memberId);
    @Query("SELECT r FROM Record r WHERE r.member.id = :memberId AND YEAR(r.date) = :year AND MONTH(r.date) = :month")
    List<Record> findByDateAndMemberId(@Param("year") int year, @Param("month") int month, @Param("memberId") Long memberId);

    List<Record> findAllRecordsByMemberId(@Param("memberId") Long memberId);

    @Modifying
    @Query("delete from Record r where r.id in :id")
    void deleteAllByRecordId(@Param("id") Long recordId);
}