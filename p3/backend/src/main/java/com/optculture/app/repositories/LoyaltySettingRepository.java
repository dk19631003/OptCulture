package com.optculture.app.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.optculture.shared.entities.loyalty.LoyaltySetting;

@Repository
public interface LoyaltySettingRepository extends JpaRepository<LoyaltySetting, Long> {

	LoyaltySetting findByUserId(Long userId);

	@Modifying
	@Transactional
	@Query("UPDATE LoyaltySetting ls SET ls.smartEreceiptJsonConfig = :smartEreceiptJsonConfig WHERE ls.userId = :userId")
	int saveOrUpdateEReceiptConfigured(@Param(value = "userId") Long userId, @Param(value = "smartEreceiptJsonConfig") String smartEreceiptJsonConfig);

}
