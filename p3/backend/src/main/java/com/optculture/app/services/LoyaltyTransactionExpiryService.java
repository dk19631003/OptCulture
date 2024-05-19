package com.optculture.app.services;

import com.optculture.app.repositories.LoyaltyTransactionExpiryRepository;
import com.optculture.shared.entities.loyalty.LoyaltyTransactionExpiry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LoyaltyTransactionExpiryService {
    @Autowired
    LoyaltyTransactionExpiryRepository loyaltyTransactionExpiryRepository;

    public List<LoyaltyTransactionExpiry> fetchExpLoyaltyPtsTrans(Long loyaltyId, int size, Long userId){

        List<LoyaltyTransactionExpiry> expireList = null;

        expireList=loyaltyTransactionExpiryRepository.fetchExpLoyaltyPtsTrans(userId,loyaltyId, PageRequest.of(0,size)).getContent();

        if(expireList != null && expireList.size() > 0){
            return expireList;
        }
        else return null;
    }

    public List<LoyaltyTransactionExpiry> fetchExpLoyaltyAmtTrans(Long loyaltyId, int size, Long userId) {
        List<LoyaltyTransactionExpiry> expireList = null;

        expireList=loyaltyTransactionExpiryRepository.fetchExpLoyaltyAmtTrans(userId,loyaltyId, PageRequest.of(0,size)).getContent();

        if(expireList != null && expireList.size() > 0){
            return expireList;
        }
        else return null;
    }
}
