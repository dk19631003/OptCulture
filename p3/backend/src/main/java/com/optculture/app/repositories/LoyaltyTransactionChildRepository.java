package com.optculture.app.repositories;

import com.optculture.shared.entities.loyalty.LoyaltyTransactionChild;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface LoyaltyTransactionChildRepository extends JpaRepository<LoyaltyTransactionChild, Long> {
    List<LoyaltyTransactionChild> findByUserIdAndMembershipNumberAndReceiptNumberAndDocSID(Long userId,String membershipNumber,String receiptNumber,String docsId);
    List<LoyaltyTransactionChild> findByUserId(Long userId);
    @Transactional
    void deleteByUserIdAndMembershipNumberAndReceiptNumberAndDocSID(Long userId,String membershipNumber,String receiptNumber,String docsId);
    @Query("SELECT ltc FROM LoyaltyTransactionChild ltc where ltc.loyaltyId = :loyaltyId and ltc.userId = :userId  and ((:fromDate is null or :toDate is null) or ltc.createdDate BETWEEN :fromDate AND :toDate ) ORDER BY ltc.transChildId DESC" )
    Page<LoyaltyTransactionChild> findByLoyaltyIdAndTimePeriod(Long loyaltyId, Long userId, LocalDateTime fromDate, LocalDateTime toDate , PageRequest of);

    Optional<LoyaltyTransactionChild> findFirstByContactIdAndUserIdOrderByCreatedDateDesc(Long contactId, Long userId);

    @Query(value = "SELECT SUM(CASE WHEN ltc.enteredAmountType='issuancereversal' THEN -ltc.description WHEN ltc.enteredAmountType='Reward' THEN 0 ELSE ltc.earnedAmount END) purchasedAmount " +
            "FROM LoyaltyTransactionChild ltc " +
            "WHERE ltc.userId = :userId AND (ltc.loyaltyId = :loyaltyId OR ltc.transferedTo = :loyaltyId) " +
            "AND ltc.transactionType IN ('Issuance', 'Return') " +
            "AND ltc.enteredAmountType != 'StoreCredit' " +
            "AND ltc.createdDate >= :createdDate")
    Double findByUserIdAndloyaltyIdAndcreatedDate(Long userId, Long loyaltyId, LocalDateTime createdDate);
    @Query("SELECT SUM(CASE WHEN ltc.enteredAmountType = 'issuancereversal' THEN ltc.pointsDifference " +
            "                WHEN ltc.enteredAmountType = 'Reward' THEN 0 " +
            "                ELSE ltc.earnedPoints END) " +
            "FROM LoyaltyTransactionChild ltc WHERE ltc.userId = :userId AND ltc.loyaltyId = :loyaltyId AND ltc.transactionType IN ('Issuance', 'Return') AND ltc.enteredAmountType != 'StoreCredit' AND ltc.createdDate > :startDate AND ltc.createdDate <= :endDate  ")
    Long getCumulativePoints(Long userId, Long loyaltyId, LocalDateTime startDate, LocalDateTime endDate);
}
