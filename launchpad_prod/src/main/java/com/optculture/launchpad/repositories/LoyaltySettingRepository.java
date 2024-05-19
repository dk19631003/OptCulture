package com.optculture.launchpad.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.optculture.shared.entities.loyalty.LoyaltySetting;

public interface LoyaltySettingRepository  extends JpaRepository<LoyaltySetting, Long>{
	
	LoyaltySetting findByuserOrgId(Long id);

}
