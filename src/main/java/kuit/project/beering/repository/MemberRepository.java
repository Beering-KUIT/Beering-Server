package kuit.project.beering.repository;

import kuit.project.beering.domain.Member;
import kuit.project.beering.domain.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByUsernameAndStatus(String username, Status status);

    boolean existsByUsernameAndStatus(String username, Status status);
}
