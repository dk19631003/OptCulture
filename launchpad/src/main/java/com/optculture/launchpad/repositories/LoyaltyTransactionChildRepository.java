package com.optculture.launchpad.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.optculture.shared.entities.loyalty.LoyaltyTransactionChild;

@Repository
public interface LoyaltyTransactionChildRepository extends JpaRepository<LoyaltyTransactionChild, Long> {
	
	@Query( "select u from LoyaltyTransactionChild u where userId = :userId and (loyaltyId = :loyaltyId  OR transferedTo= :loyaltyId ) AND transactionType = :transactionType ORDER BY transChildId DESC " )
	List<LoyaltyTransactionChild> findByTransactionType(Long userId, Long loyaltyId, String transactionType);
	
	LoyaltyTransactionChild findByTransChildId(Long childId);

}
