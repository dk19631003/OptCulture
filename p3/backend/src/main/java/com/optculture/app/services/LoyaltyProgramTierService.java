package com.optculture.app.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.optculture.app.repositories.LoyaltyProgramTierRepository;
import com.optculture.shared.entities.loyalty.LoyaltyProgramTier;

@Service
public class LoyaltyProgramTierService {
	
	@Autowired
	LoyaltyProgramTierRepository loyaltyProgramTierRepository;
	
	public List<LoyaltyProgramTier> getListOfLoyaltyProgramTiersByProgramId(Long programId) {
		return loyaltyProgramTierRepository.findAllByProgramIdOrderByTierIdAsc(programId);
	}
	public  LoyaltyProgramTier getLoyaltyTierByTierId(Long tierId){
		Optional<LoyaltyProgramTier> loyaltyProgramTier= loyaltyProgramTierRepository.findById(tierId);
		return loyaltyProgramTier.isEmpty()?null:loyaltyProgramTier.get();
	}

}
