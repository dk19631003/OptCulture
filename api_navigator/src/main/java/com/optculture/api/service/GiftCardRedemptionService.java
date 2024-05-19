package com.optculture.api.service;

import com.optculture.api.dto.GiftCard.GiftCardRedemptionRequestDTO;
import com.optculture.api.dto.GiftCard.GiftCardRedemptionResponseDTO;
import com.optculture.api.dto.Status;
import com.optculture.api.repositories.UserRepository;
import com.optculture.api.repositories.GiftCard.GiftCardsExpiryRepository;
import com.optculture.api.repositories.GiftCard.GiftCardsHistoryRepository;
import com.optculture.api.repositories.GiftCard.GiftCardsRepository;
import com.optculture.api.repositories.GiftCard.GiftProgramsRepository;
import com.optculture.shared.entities.GiftCard.GiftCards;
import com.optculture.shared.entities.GiftCard.GiftCardsExpiry;
import com.optculture.shared.entities.GiftCard.GiftCardsHistory;
import com.optculture.shared.entities.GiftCard.GiftPrograms;
import com.optculture.shared.entities.org.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class GiftCardRedemptionService {

    Logger logger = LoggerFactory.getLogger(GiftCardRedemptionService.class);

    @Autowired
    UserRepository userRepository;

    @Autowired
    GiftCardsRepository giftCardsRepository;
    @Autowired
    GiftCardsExpiryRepository giftCardsExpiryRepository;

    @Autowired
    GiftProgramsRepository giftProgramsRepository;

    @Autowired
    GiftCardsHistoryRepository giftCardsHistoryRepository;

    public GiftCardRedemptionResponseDTO doRedemption(GiftCardRedemptionRequestDTO giftCardRedemptionRequestDTO) {
        logger.info("inside gift redemption service ");

        GiftCardRedemptionResponseDTO giftCardRedemptionResponseDTO = new GiftCardRedemptionResponseDTO();
        if(Objects.isNull(giftCardRedemptionRequestDTO)) {
        	Status status = new Status("1001","Request is Empty","failure");
            giftCardRedemptionResponseDTO.setStatus(status);
            return giftCardRedemptionResponseDTO;
        }

        // Request Validations
        if(Objects.isNull(giftCardRedemptionRequestDTO.getUserDetails()) ||
                Objects.isNull(giftCardRedemptionRequestDTO.getHeader()) ||
                Objects.isNull(giftCardRedemptionRequestDTO.getMembership()) ||
                Objects.isNull(giftCardRedemptionRequestDTO.getAmount())) {
        	Status status = new Status("1002","Mandatory details missing","failure");
            giftCardRedemptionResponseDTO.setStatus(status);
            return giftCardRedemptionResponseDTO;
        }
        if(Objects.isNull(giftCardRedemptionRequestDTO.getUserDetails().getUserName()) || giftCardRedemptionRequestDTO.getUserDetails().getUserName().isEmpty() ||
                Objects.isNull(giftCardRedemptionRequestDTO.getUserDetails().getToken()) || giftCardRedemptionRequestDTO.getUserDetails().getToken().isEmpty() ||
                Objects.isNull(giftCardRedemptionRequestDTO.getUserDetails().getOrganizationId()) || giftCardRedemptionRequestDTO.getUserDetails().getOrganizationId().isEmpty()) {
        	Status status = new Status("1003","Missing user mandatory details","failure");
            giftCardRedemptionResponseDTO.setStatus(status);
            return giftCardRedemptionResponseDTO;
        }
        if(Objects.isNull(giftCardRedemptionRequestDTO.getHeader().getRequestDate()) || giftCardRedemptionRequestDTO.getHeader().getRequestDate().isEmpty() ||
                Objects.isNull(giftCardRedemptionRequestDTO.getHeader().getStoreNumber()) || giftCardRedemptionRequestDTO.getHeader().getStoreNumber().isEmpty() ||
                Objects.isNull(giftCardRedemptionRequestDTO.getHeader().getReceiptNumber()) || giftCardRedemptionRequestDTO.getHeader().getReceiptNumber().isEmpty()) {
        	Status status = new Status("1004","Missing header mandatory details","failure");
            giftCardRedemptionResponseDTO.setStatus(status);
            return giftCardRedemptionResponseDTO;
        }
        if(Objects.isNull(giftCardRedemptionRequestDTO.getAmount().getType()) || giftCardRedemptionRequestDTO.getAmount().getType().isEmpty() ||
                Objects.isNull(giftCardRedemptionRequestDTO.getAmount().getEnteredValue()) || giftCardRedemptionRequestDTO.getAmount().getEnteredValue().isEmpty()) {
        	Status status = new Status("1005","Missing amount mandatory details","failure");
            giftCardRedemptionResponseDTO.setStatus(status);
            return giftCardRedemptionResponseDTO;
        }
        if(giftCardRedemptionRequestDTO.getAmount().getType().equals("Gift")) {
            if(Objects.isNull(giftCardRedemptionRequestDTO.getMembership().getCardNumber()) || giftCardRedemptionRequestDTO.getMembership().getCardNumber().isEmpty() ||
                    Objects.isNull(giftCardRedemptionRequestDTO.getMembership().getPin()) || giftCardRedemptionRequestDTO.getMembership().getPin().isEmpty()) {
            	Status status = new Status("1006","Missing membership mandatory details","failure");
                giftCardRedemptionResponseDTO.setStatus(status);
                return giftCardRedemptionResponseDTO;
            }
        } else if (giftCardRedemptionRequestDTO.getAmount().getType().equals("Void")) {
            if(Objects.isNull(giftCardRedemptionRequestDTO.getMembership().getCardNumber()) || giftCardRedemptionRequestDTO.getMembership().getCardNumber().isEmpty()) {
            	Status status = new Status("1007","Missing card number","failure");
                giftCardRedemptionResponseDTO.setStatus(status);
                return giftCardRedemptionResponseDTO;
            }
        }


        //User validation
        String userName = giftCardRedemptionRequestDTO.getUserDetails().getUserName()+"__org__"+giftCardRedemptionRequestDTO.getUserDetails().getOrganizationId();
        String token = giftCardRedemptionRequestDTO.getUserDetails().getToken();
        User user = userRepository.findByUserNameAndToken(userName,token);
        if(Objects.isNull(user)) {
            logger.info("searching with optsync key");
            user = userRepository.findByUserNameOptSyncKey(userName,token);
        }
        if(Objects.isNull(user)) {
        	Status status = new Status("1008","No User details found","failure");
            giftCardRedemptionResponseDTO.setStatus(status);
            return giftCardRedemptionResponseDTO;
        }
        logger.info("user name "+user.getUserName());


        //Entered value validation
        double enteredValue = !giftCardRedemptionRequestDTO.getAmount().getEnteredValue().isEmpty() ? Double.parseDouble(giftCardRedemptionRequestDTO.getAmount().getEnteredValue()) : 0.0;
        if(enteredValue <= 0) {
        	Status status = new Status("1009","Entered value is 0 or less","failure");
            giftCardRedemptionResponseDTO.setStatus(status);
            return giftCardRedemptionResponseDTO;
        }


        //Gift card validation
        String giftCardNumber = giftCardRedemptionRequestDTO.getMembership().getCardNumber();
        GiftCards giftCards = giftCardsRepository.findFirstByUserIdAndGiftCardNumber(user.getUserId(), giftCardNumber);
        if(Objects.isNull(giftCards)) {
        	Status status = new Status("1010","No gift card details found","failure");
            giftCardRedemptionResponseDTO.setStatus(status);
            return giftCardRedemptionResponseDTO;
        }
        if(!Objects.isNull(giftCards.getExpiryDate()) && giftCards.getExpiryDate().isBefore(LocalDateTime.now())) {
        	Status status = new Status("1011","Gift card expired","failure");
            giftCardRedemptionResponseDTO.setStatus(status);
            return giftCardRedemptionResponseDTO;
        }

        if(!Objects.isNull(giftCards.getGiftCardStatus()) && !giftCards.getGiftCardStatus().equals("Active")) {
        	Status status = new Status("1012","Gift card is not active","failure");
            giftCardRedemptionResponseDTO.setStatus(status);
            return giftCardRedemptionResponseDTO;
        }


        //Gift program validation
        GiftPrograms giftPrograms =giftProgramsRepository.findByGiftProgramId(giftCards.getGiftProgramId());
        if(!Objects.isNull(giftPrograms) && !giftPrograms.getProgramStatus().equals("Active")) {
        	Status status = new Status("1013","Gift program is not active","failure");
            giftCardRedemptionResponseDTO.setStatus(status);
            return giftCardRedemptionResponseDTO;
        }


        //Processing request bases on redemption type Gift/Void
        String amountType = giftCardRedemptionRequestDTO.getAmount().getType();
        if(amountType.equals("Gift")) {

            double receiptAmount = !Objects.isNull(giftCardRedemptionRequestDTO.getAmount().getReceiptAmount()) &&
                    !giftCardRedemptionRequestDTO.getAmount().getReceiptAmount().isEmpty() ? Double.parseDouble(giftCardRedemptionRequestDTO.getAmount().getReceiptAmount()) : 0.0;
            if(receiptAmount<=0) {
            	Status status = new Status("1014","Receipt amount can't be 0 or less","failure");
                giftCardRedemptionResponseDTO.setStatus(status);
                return giftCardRedemptionResponseDTO;
            }
            if(receiptAmount < enteredValue) {
            	Status status = new Status("1015","Receipt amount is less than entered value","failure");
                giftCardRedemptionResponseDTO.setStatus(status);
                return giftCardRedemptionResponseDTO;
            }
            if (!Objects.isNull(giftCards.getGiftCardPin()) ) {
                if(!(giftCards.getGiftCardPin().equals(giftCardRedemptionRequestDTO.getMembership().getPin()))) {
                	Status status = new Status("1016","Incorrect pin","failure");
                    giftCardRedemptionResponseDTO.setStatus(status);
                    return giftCardRedemptionResponseDTO;
                }
            }
            if(giftCards.getGiftBalance() <= 0) {
            	Status status = new Status("1017","Insufficient gift balance","failure");
                giftCardRedemptionResponseDTO.setStatus(status);
                return giftCardRedemptionResponseDTO;
            }
            if(giftCards.getGiftBalance() < (Double.parseDouble(giftCardRedemptionRequestDTO.getAmount().getEnteredValue()))) {
            	Status status = new Status("1018","Entered value can't be greater than available balance","failure");
                giftCardRedemptionResponseDTO.setStatus(status);
                return giftCardRedemptionResponseDTO;
            }
            if(!Objects.isNull(giftPrograms) && giftPrograms.getRedemptionType().equals("Full")) {
                enteredValue = giftCards.getGiftBalance();
            }

        } else if (amountType.equals("Void")) {

            List<GiftCardsHistory> giftHistory = giftCardsHistoryRepository.findByUserIdAndGiftCardNumberAndReceiptNumberAndStoreNumber(giftCards.getUserId(),giftCards.getGiftCardNumber(),
                    giftCardRedemptionRequestDTO.getHeader().getReceiptNumber(),giftCardRedemptionRequestDTO.getHeader().getStoreNumber());
            boolean giftRedemption = false;
            if(!Objects.isNull(giftHistory) && !giftHistory.isEmpty()) {
                for(GiftCardsHistory giftCardsHistory : giftHistory) {
                    if(giftCardsHistory.getTransactionType().equals("VoidRedemption")) {
                    	Status status = new Status("1019","Can't process void multiple times for same receipt number "+giftCardRedemptionRequestDTO.getHeader().getReceiptNumber()+" ","failure");
                    	giftCardRedemptionResponseDTO.setStatus(status);
                    	return giftCardRedemptionResponseDTO;
                    }

                }
                for(GiftCardsHistory giftCardsHistory : giftHistory) {
                	if(giftCardsHistory.getTransactionType().equals("GiftRedemption")) {
                        giftRedemption = true;
                        enteredValue = giftCardsHistory.getEnteredAmount();
                        break;
                    }
                }

            }
            if(Objects.isNull(giftHistory) || !giftRedemption){
            	Status status = new Status("1020","No redemption with receipt number "+giftCardRedemptionRequestDTO.getHeader().getReceiptNumber()+" ","failure");
            	giftCardRedemptionResponseDTO.setStatus(status);
                return giftCardRedemptionResponseDTO;
            }
        }


        //Balances calculation
        updateGiftCardBalances(giftCards,enteredValue,amountType);
        insertRedemptionTransaction(giftCards,enteredValue,giftCardRedemptionRequestDTO);
        updateGiftCardExpiryTransaction(giftCards,enteredValue,amountType);
        
        String responseType = amountType.equals("Void") ? "Reverted ": "Redeemed ";
        Status status = new Status("0",responseType+""+enteredValue+" Successfully","Success");
        giftCardRedemptionResponseDTO.setCardNumber(giftCardNumber);
        giftCardRedemptionResponseDTO.setRedeemedAmount(enteredValue+"");
        giftCardRedemptionResponseDTO.setStatus(status);
        return giftCardRedemptionResponseDTO;
    }

    public void updateGiftCardBalances(GiftCards giftCards, Double enteredValue, String redemptionType) {

        if(redemptionType.equals("Gift")) {
            if(giftCards.getGiftBalance() <= enteredValue)
                giftCards.setGiftBalance(0.0);
            else
                giftCards.setGiftBalance(giftCards.getGiftBalance()-enteredValue);

            if(Objects.isNull(giftCards.getTotalRedeemed()))
                giftCards.setTotalRedeemed(enteredValue);
            else
                giftCards.setTotalRedeemed(giftCards.getTotalRedeemed()+enteredValue);

        }else if (redemptionType.equals("Void")) {
            if(Objects.isNull(giftCards.getGiftBalance()) || giftCards.getGiftBalance()==0)
                giftCards.setGiftBalance(enteredValue);
            else
                giftCards.setGiftBalance(giftCards.getGiftBalance()+enteredValue);

            if(giftCards.getTotalRedeemed()>=enteredValue)
                giftCards.setTotalRedeemed(giftCards.getTotalRedeemed()-enteredValue);
            else
                giftCards.setTotalRedeemed(0.0);
        }

        giftCardsRepository.save(giftCards);
    }

    public void insertRedemptionTransaction(GiftCards giftCards,Double enteredValue, GiftCardRedemptionRequestDTO giftCardRedemptionRequestDTO) {
        if (giftCardRedemptionRequestDTO.getAmount().getType().equals("Gift")) {
            GiftCardsHistory giftCardsHistory = new GiftCardsHistory();
            giftCardsHistory.setUserId(giftCards.getUserId());
            giftCardsHistory.setGiftCardId(giftCards.getGiftCardId());
            giftCardsHistory.setGiftProgramId(giftCards.getGiftProgramId());
            giftCardsHistory.setGiftCardNumber(giftCards.getGiftCardNumber());
            giftCardsHistory.setTransactionType("GiftRedemption");
            //giftCardsHistory.setAmountType("Currency");
            giftCardsHistory.setEnteredAmount(enteredValue);
            giftCardsHistory.setGiftDifference(enteredValue);
            giftCardsHistory.setGiftBalance(giftCards.getGiftBalance());
            giftCardsHistory.setTransactionDate(LocalDateTime.now());
            giftCardsHistory.setReceiptNumber(giftCardRedemptionRequestDTO.getHeader().getReceiptNumber());
            giftCardsHistory.setStoreNumber(Long.parseLong(giftCardRedemptionRequestDTO.getHeader().getStoreNumber()));
            giftCardsHistoryRepository.save(giftCardsHistory);

        } else if (giftCardRedemptionRequestDTO.getAmount().getType().equals("Void")) { // do we delete redemption record and insert new void record or update existing record as void
            GiftCardsHistory giftCardsHistory = new GiftCardsHistory();
            giftCardsHistory.setUserId(giftCards.getUserId());
            giftCardsHistory.setGiftCardId(giftCards.getGiftCardId());
            giftCardsHistory.setGiftProgramId(giftCards.getGiftProgramId());
            giftCardsHistory.setGiftCardNumber(giftCards.getGiftCardNumber());
            giftCardsHistory.setTransactionType("VoidRedemption");
            //giftCardsHistory.setAmountType("Currency");
            giftCardsHistory.setEnteredAmount(enteredValue);
            giftCardsHistory.setGiftDifference(enteredValue);
            giftCardsHistory.setGiftBalance(giftCards.getGiftBalance());
            giftCardsHistory.setTransactionDate(LocalDateTime.now());
            giftCardsHistory.setReceiptNumber(giftCardRedemptionRequestDTO.getHeader().getReceiptNumber());
            giftCardsHistory.setStoreNumber(Long.parseLong(giftCardRedemptionRequestDTO.getHeader().getStoreNumber()));
            giftCardsHistoryRepository.save(giftCardsHistory);
        }
    }

    public void updateGiftCardExpiryTransaction(GiftCards giftCards, Double enteredValue,String redemptionType) {
        GiftCardsExpiry giftCardsExpiry = giftCardsExpiryRepository.findByUserIdAndGiftProgramIdAndGiftCardNumber(giftCards.getUserId(),giftCards.getGiftProgramId(),giftCards.getGiftCardNumber());
        if(!Objects.isNull(giftCardsExpiry)) {
            if(redemptionType.equals("Gift")) {
                if(giftCardsExpiry.getExpiryAmount()>0) {
                    Double expiryAmount = giftCardsExpiry.getExpiryAmount();
                    if(expiryAmount<enteredValue) {
                        giftCardsExpiry.setExpiryAmount(0.0);
                    }else
                        giftCardsExpiry.setExpiryAmount(expiryAmount-enteredValue);
                }
            }else if (redemptionType.equals("Void")) {

                Double expiryAmount = giftCardsExpiry.getExpiryAmount();
                if(Objects.isNull(expiryAmount) || expiryAmount==0) {
                    giftCardsExpiry.setExpiryAmount(enteredValue);
                }else
                    giftCardsExpiry.setExpiryAmount(expiryAmount+enteredValue);

            }
            giftCardsExpiryRepository.save(giftCardsExpiry);
        }

    }
}