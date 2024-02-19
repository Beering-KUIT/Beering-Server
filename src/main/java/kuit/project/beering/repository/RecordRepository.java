package kuit.project.beering.repository;

import kuit.project.beering.domain.Record;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface RecordRepository extends JpaRepository<Record, Long> {
    Record findByDateAndMemberIdAndDrinkId(Timestamp date, Long memberId, Long drinkId);

    List<Record> findAllByDateAndMemberId(Timestamp date, Long memberId);
}