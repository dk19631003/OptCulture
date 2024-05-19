package com.optculture.app.repositories;

import com.optculture.shared.entities.loyalty.ValueCodes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ValueCodesRepository extends JpaRepository<ValueCodes, Long> {

    List<ValueCodes> findByOrgId(Long orgId);
}
