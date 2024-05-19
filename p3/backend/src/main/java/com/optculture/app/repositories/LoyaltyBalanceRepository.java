package com.optculture.app.repositories;

import com.optculture.shared.entities.loyalty.LoyaltyBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface LoyaltyBalanceRepository extends JpaRepository<LoyaltyBalance,Long> {
    LoyaltyBalance findByUserIdAndMemberShipNumberAndValueCode(Long userId,String membershipNumber,String valueCode);

    List<LoyaltyBalance> findByMemberShipNumberAndUserId(String cardNumber, Long userId);
}
