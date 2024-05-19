package com.optculture.app.services;

import com.optculture.app.repositories.ReferralCodesIssuedRepository;
import com.optculture.shared.entities.promotion.ReferralcodesIssued;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ReferralCodesIssuedService {
    private ReferralCodesIssuedRepository referralCodesIssuedRepository;
    public ReferralCodesIssuedService(ReferralCodesIssuedRepository referralCodesIssuedRepository){
        this.referralCodesIssuedRepository=referralCodesIssuedRepository;
    }
    public ReferralcodesIssued getReferralCodeByContactId(Long contactId,Long userId) {
        Optional<ReferralcodesIssued> optionalReferralcodesIssued= referralCodesIssuedRepository.findFirstByReferredCidAndUserId(contactId,userId);
        return optionalReferralcodesIssued.orElse(null);
    }
}
