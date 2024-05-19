package com.optculture.launchpad.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.optculture.shared.entities.loyalty.LoyaltyProgramTier;

public interface LoyaltyProgramTierRepository extends JpaRepository<LoyaltyProgramTier , Long>{
	
	LoyaltyProgramTier findBytierId(Long Id);

}
