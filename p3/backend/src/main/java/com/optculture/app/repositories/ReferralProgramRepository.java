package com.optculture.app.repositories;

import com.optculture.shared.entities.promotion.ReferralProgram;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReferralProgramRepository extends JpaRepository<ReferralProgram,Long> {
   Long countByUserId(Long userId);
}
