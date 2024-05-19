package com.optculture.app.services;

import com.optculture.app.repositories.LoyaltyBalanceRepository;
import com.optculture.shared.entities.loyalty.LoyaltyBalance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LoyaltyBalanceService {
    @Autowired
    LoyaltyBalanceRepository loyaltyBalanceRepository;
    public List<LoyaltyBalance> findbyCardNo(String cardNumber, Long userId) {
        return  loyaltyBalanceRepository.findByMemberShipNumberAndUserId(cardNumber,userId);

    }
}
