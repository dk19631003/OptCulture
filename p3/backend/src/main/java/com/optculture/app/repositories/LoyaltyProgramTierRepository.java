package com.optculture.app.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.optculture.shared.entities.loyalty.LoyaltyProgramTier;

@Repository
public interface LoyaltyProgramTierRepository extends JpaRepository<LoyaltyProgramTier, Long> {

//	List<LoyaltyProgramTier> findByTierIdGreaterThanEqualAndProgramIdOrderByTierIdAsc(Long tierId, Long programId);
	List<LoyaltyProgramTier> findAllByProgramIdOrderByTierIdAsc(Long programId);

	List<LoyaltyProgramTier> findAllByProgramIdIn(List<Long> programId);

}
