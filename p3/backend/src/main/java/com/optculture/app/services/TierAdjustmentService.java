package com.optculture.app.services;

import com.optculture.app.config.GetLoggedInUser;
import com.optculture.app.dto.loyalty.TierAdjustmentsRequest;
//import com.optculture.app.dto.loyalty.TierAdjustmentsResponse;
import com.optculture.app.repositories.ContactLoyaltyRepository;
import com.optculture.app.repositories.LoyaltyTransactionChildRepository;
import com.optculture.shared.entities.contact.ContactLoyalty;
import com.optculture.shared.entities.loyalty.LoyaltyTransactionChild;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class TierAdjustmentService {

    Logger logger = LoggerFactory.getLogger(TierAdjustmentService.class);

    @Autowired
    ContactLoyaltyRepository contactLoyaltyRepository;

    @Autowired
    LoyaltyTransactionChildRepository loyaltyTransactionChildRepository;

    @Autowired
    GetLoggedInUser getLoggedInUser;
    public ResponseEntity doTierAdjustments(TierAdjustmentsRequest tierAdjustReq) {
        Long userId = getLoggedInUser.getLoggedInUser().getUserId();
        Long cardNumber = tierAdjustReq.getCardNumber();
        ContactLoyalty contactLoyalty = contactLoyaltyRepository.findFirstByUserIdAndCardNumber(userId,cardNumber.toString());
        logger.info("contactLoyalty object "+contactLoyalty.getCardNumber());
        if(tierAdjustReq.getTierId()!=null) {
            if(!Objects.equals(contactLoyalty.getProgramTierId(), tierAdjustReq.getTierId())) {
                contactLoyalty.setProgramTierId(tierAdjustReq.getTierId());
            }
            contactLoyaltyRepository.save(contactLoyalty);
            createMoveTierTransaction(contactLoyalty);
            logger.info("tier id "+ tierAdjustReq.getTierId());
            return  new ResponseEntity<>("Tier Adjustment Successful", HttpStatus.ACCEPTED);
        }
        return new ResponseEntity<>("Tier Adjustment Failed",HttpStatus.OK);
    }

    public void createMoveTierTransaction(ContactLoyalty cl) {
        LoyaltyTransactionChild loyaltyTransactionChild = new LoyaltyTransactionChild();
        loyaltyTransactionChild.setMembershipNumber(cl.getCardNumber());
        loyaltyTransactionChild.setMembershipType(cl.getMembershipType());
        loyaltyTransactionChild.setTransactionType("Tier adjustment");
        loyaltyTransactionChild.setProgramId(cl.getProgramId());
        loyaltyTransactionChild.setCardSetId(cl.getCardSetId());
        loyaltyTransactionChild.setTierId(cl.getProgramTierId());
        loyaltyTransactionChild.setUserId(cl.getUserId());
        loyaltyTransactionChild.setOrgId(cl.getOrgId());
        loyaltyTransactionChild.setAmountBalance(cl.getLoyaltyCurrencyBalance());
        loyaltyTransactionChild.setPointsBalance(cl.getLoyaltyPointBalance());
        loyaltyTransactionChild.setGiftBalance(cl.getGiftBalance());
        loyaltyTransactionChild.setHoldAmount(cl.getHoldamountBalance());
        loyaltyTransactionChild.setHoldPoints(cl.getHoldpointsBalance());
        loyaltyTransactionChild.setCreatedDate(LocalDateTime.now());
        loyaltyTransactionChild.setSourceType("Manual");
        loyaltyTransactionChild.setContactId(cl.getContact() == null ? null : cl.getContact().getContactId());
        loyaltyTransactionChild.setLoyaltyId(cl.getLoyaltyId());

        loyaltyTransactionChildRepository.save(loyaltyTransactionChild);
    }

}
