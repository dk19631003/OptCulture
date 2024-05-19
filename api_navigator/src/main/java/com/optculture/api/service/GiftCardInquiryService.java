package com.optculture.api.service;

import com.optculture.api.dto.GiftCard.GiftCardInquiryRequestDTO;
import com.optculture.api.dto.GiftCard.GiftCardInquiryResponseDTO;
import com.optculture.api.dto.Status;
import com.optculture.api.repositories.GiftCard.GiftCardSkusRepository;
import com.optculture.api.repositories.GiftCard.GiftCardsRepository;
import com.optculture.api.repositories.GiftCard.GiftProgramsRepository;
import com.optculture.api.repositories.UserRepository;
import com.optculture.shared.entities.GiftCard.GiftCardSkus;
import com.optculture.shared.entities.GiftCard.GiftCards;
import com.optculture.shared.entities.GiftCard.GiftPrograms;
import com.optculture.shared.entities.org.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class GiftCardInquiryService {

    Logger logger = LoggerFactory.getLogger(GiftCardInquiryService.class);

    @Autowired
    UserRepository userRepository;

    @Autowired
    GiftCardsRepository giftCardsRepository;

    @Autowired
    GiftProgramsRepository giftProgramsRepository;

    @Autowired
    GiftCardSkusRepository giftCardSkusRepository;

    public GiftCardInquiryResponseDTO getInquiryDetails(GiftCardInquiryRequestDTO giftCardInquiryRequestDTO) {

        GiftCardInquiryResponseDTO giftCardInquiryResponseDTO = new GiftCardInquiryResponseDTO();
        if(Objects.isNull(giftCardInquiryRequestDTO)) {
            Status status = new Status("1001","Request is Empty","failure");
            giftCardInquiryResponseDTO.setStatus(status);
            return giftCardInquiryResponseDTO;
        }


        //Request validations
        if(Objects.isNull(giftCardInquiryRequestDTO.getUserDetails()) ||
                Objects.isNull(giftCardInquiryRequestDTO.getHeader()) ||
                Objects.isNull(giftCardInquiryRequestDTO.getMembership())) {
            Status status = new Status("1002","Mandatory details missing","failure");
            giftCardInquiryResponseDTO.setStatus(status);
            return giftCardInquiryResponseDTO;
        }
        if(Objects.isNull(giftCardInquiryRequestDTO.getUserDetails().getUserName()) || giftCardInquiryRequestDTO.getUserDetails().getUserName().isEmpty() ||
                Objects.isNull(giftCardInquiryRequestDTO.getUserDetails().getToken()) || giftCardInquiryRequestDTO.getUserDetails().getToken().isEmpty() ||
                Objects.isNull(giftCardInquiryRequestDTO.getUserDetails().getOrganizationId()) || giftCardInquiryRequestDTO.getUserDetails().getOrganizationId().isEmpty()) {
            Status status = new Status("1003","Missing user mandatory details","failure");
            giftCardInquiryResponseDTO.setStatus(status);
            return giftCardInquiryResponseDTO;
        }
        if(Objects.isNull(giftCardInquiryRequestDTO.getHeader().getRequestDate()) || giftCardInquiryRequestDTO.getHeader().getRequestDate().isEmpty() ||
                Objects.isNull(giftCardInquiryRequestDTO.getHeader().getReceiptNumber()) || giftCardInquiryRequestDTO.getHeader().getReceiptNumber().isEmpty()) {
            Status status = new Status("1004","Missing header mandatory details","failure");
            giftCardInquiryResponseDTO.setStatus(status);
            return giftCardInquiryResponseDTO;
        }
        if(Objects.isNull(giftCardInquiryRequestDTO.getMembership().getCardNumber()) || giftCardInquiryRequestDTO.getMembership().getCardNumber().isEmpty()) {
            Status status = new Status("1005","Missing Card number","failure");
            giftCardInquiryResponseDTO.setStatus(status);
            return giftCardInquiryResponseDTO;
        }


        //User validation
        String userName = giftCardInquiryRequestDTO.getUserDetails().getUserName()+"__org__"+giftCardInquiryRequestDTO.getUserDetails().getOrganizationId();
        String token = giftCardInquiryRequestDTO.getUserDetails().getToken();
        User user = userRepository.findByUserNameAndToken(userName,token);
        if(Objects.isNull(user)) {
            logger.info("searching with optsync key");
            user = userRepository.findByUserNameOptSyncKey(userName,token);
        }
        if(Objects.isNull(user)) {
            Status status = new Status("1006","No User details found","failure");
            giftCardInquiryResponseDTO.setStatus(status);
            return giftCardInquiryResponseDTO;
        }
        logger.info("user name "+user.getUserName());


        //Gift Card validations
        String giftCardNumber = giftCardInquiryRequestDTO.getMembership().getCardNumber();
        GiftCards giftCards = giftCardsRepository.findFirstByUserIdAndGiftCardNumber(user.getUserId(),giftCardNumber);

        if(Objects.isNull(giftCards)) {
            Status status = new Status("1007","No gift card details found","failure");
            giftCardInquiryResponseDTO.setStatus(status);
            return giftCardInquiryResponseDTO;
        }
        if(!Objects.isNull(giftCards.getExpiryDate()) && giftCards.getExpiryDate().isBefore(LocalDateTime.now())) {
            Status status = new Status("1008","Gift card is expired","failure");
            giftCardInquiryResponseDTO.setStatus(status);
            return giftCardInquiryResponseDTO;
        }

        if(!Objects.isNull(giftCards.getGiftCardStatus()) && !giftCards.getGiftCardStatus().equals("Active")) {
            Status status = new Status("1009","Gift card is not active","failure");
            giftCardInquiryResponseDTO.setStatus(status);
            return giftCardInquiryResponseDTO;
        }


        //Gift program validation
        if(!Objects.isNull(giftCards.getGiftProgramId())) {
            GiftPrograms giftPrograms = giftProgramsRepository.findByGiftProgramId(giftCards.getGiftProgramId());
            if(!Objects.isNull(giftPrograms) && !Objects.isNull(giftPrograms.getExpiryInMonths()) &&
                    giftPrograms.getProgramStatus().equals("Active")) {
                if(!Objects.isNull(giftCards.getExpiryDate())) {
                    logger.info("expiry date : "+giftCards.getExpiryDate());
                    giftCardInquiryResponseDTO.setExpDate(giftCards.getExpiryDate()+"");
                }
            }
            if(!Objects.isNull(giftCards.getPurchasedItemSid())) {
                GiftCardSkus giftCardSkus = giftCardSkusRepository.findByUserIdAndSkuCode(giftCards.getUserId(),giftCards.getPurchasedItemSid());
                if(!Objects.isNull(giftCardSkus) && !Objects.isNull(giftCardSkus.getSkuCode())
                        && !Objects.isNull(giftCardSkus.getSkuName())) {
                    giftCardInquiryResponseDTO.setProductCode(giftCardSkus.getSkuCode());
                    giftCardInquiryResponseDTO.setProductName(giftCardSkus.getSkuName());
                }
            }
        }

        Status status = new Status("0","Inquiry Successful","Success");
        giftCardInquiryResponseDTO.setCardNumber(giftCards.getGiftCardNumber());
        giftCardInquiryResponseDTO.setAvailableAmount(giftCards.getGiftBalance().toString());
        giftCardInquiryResponseDTO.setStatus(status);
        return giftCardInquiryResponseDTO;
    }

}
