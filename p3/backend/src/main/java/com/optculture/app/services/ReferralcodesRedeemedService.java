package com.optculture.app.services;

import com.optculture.app.repositories.ReferralcodesRedeemedRepository;
import com.optculture.shared.entities.promotion.ReferralcodesRedeemed;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

@Service
public class ReferralcodesRedeemedService {
    ReferralcodesRedeemedRepository referralcodesRedeemedRepository;
    public ReferralcodesRedeemedService(ReferralcodesRedeemedRepository referralcodesRedeemedRepository){
        this.referralcodesRedeemedRepository=referralcodesRedeemedRepository;
    }
    public Long getRedeemedCountByReferredCid(Long referredCid, Long userId) {
        ReferralcodesRedeemed referralcodesRedeemed= new ReferralcodesRedeemed();
        referralcodesRedeemed.setReferredCid(referredCid);
        referralcodesRedeemed.setUserId(userId);
        Example<ReferralcodesRedeemed> redeemedExample=Example.of(referralcodesRedeemed);
        return referralcodesRedeemedRepository.count(redeemedExample);
    }
}
