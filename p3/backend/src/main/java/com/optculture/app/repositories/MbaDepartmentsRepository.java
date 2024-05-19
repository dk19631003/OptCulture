package com.optculture.app.repositories;

import com.optculture.shared.entities.communication.ereceipt.MbaDepartmentCodes;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.util.List;
import java.util.Objects;

@Repository
public interface MbaDepartmentsRepository extends JpaRepository<MbaDepartmentCodes,Long> {

    @Query("SELECT md.department2, MAX(md.score) FROM MbaDepartmentCodes md " +
            "WHERE md.department1 IN :departments AND md.userId = :userId " +
            "AND md.department2 NOT IN :departments GROUP BY md.department2 ORDER BY md.score DESC")
    List<Object[]> findTopScores(@Param("departments") List<String> departments, @Param("userId") Long userId, PageRequest request);
}
