package com.optculture.app.repositories;

import com.clickhouse.client.internal.apache.hc.core5.http.HttpEntity;
import com.optculture.shared.entities.loyalty.LoyaltyTransactionExpiry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoyaltyTransactionExpiryRepository extends JpaRepository<LoyaltyTransactionExpiry,Long> {
    @Query("SELECT lte FROM LoyaltyTransactionExpiry lte WHERE lte.userId = :userId AND (lte.loyaltyId = :loyaltyId OR lte.transferedTo = :loyaltyId) AND lte.expiryPoints > 0 AND lte.rewardFlag = 'L' ORDER BY lte.createdDate ASC ")
    Page<LoyaltyTransactionExpiry> fetchExpLoyaltyPtsTrans(Long userId, Long loyaltyId, PageRequest of);
    @Query("SELECT lte FROM LoyaltyTransactionExpiry lte WHERE lte.userId = :userId AND lte.loyaltyId = :loyaltyId AND lte.expiryAmount != NULL AND lte.expiryAmount > 0 AND lte.rewardFlag = 'L' ORDER BY lte.createdDate ASC ")
    Page<LoyaltyTransactionExpiry> fetchExpLoyaltyAmtTrans(Long userId, Long loyaltyId, PageRequest of);
}
