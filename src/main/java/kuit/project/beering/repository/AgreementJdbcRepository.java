package kuit.project.beering.repository;

import kuit.project.beering.dto.common.AgreementBulkInsertDto;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class AgreementJdbcRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    /**
     * @Brief bulk insert query 생성을 위한 메서드
     * @param dtos
     */
    public void bulkInsertAgreement(List<AgreementBulkInsertDto> dtos) {
        String sql = "INSERT INTO agreement (name, is_agreed, member_id, status, created_at, updated_at) " +
                "VALUES (:name, :isAgreed, :memberId, :status, :createdAt, :updatedAt)";

        SqlParameterSource[] sqlParameterSource =
                dtos.stream().map(
                                BeanPropertySqlParameterSource::new).
                        toArray(SqlParameterSource[]::new);

        jdbcTemplate.batchUpdate(sql, sqlParameterSource);
    }

}
