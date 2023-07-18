package kuit.project.beering.repository;

import kuit.project.beering.domain.ReviewOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReviewOptionRepository extends JpaRepository<ReviewOption, Long> {

    @Query("select ro from ReviewOption ro " +
            "join fetch ro.category " +
            "where ro.category.id = :categoryId")
    List<ReviewOption> findAllFetchByCategoryId(Long categoryId);

    List<ReviewOption> findAllByCategoryId(Long categoryId);
}
