package com.optculture.api.controller;

import com.optculture.api.dto.GiftCard.GiftCardRedemptionRequestDTO;
import com.optculture.api.dto.GiftCard.GiftCardRedemptionResponseDTO;
import com.optculture.api.dto.Status;
import com.optculture.api.service.GiftCardRedemptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class GiftCardRedemptionController {

    Logger logger = LoggerFactory.getLogger(GiftCardRedemptionController.class);

    @Autowired
    GiftCardRedemptionService giftCardRedemptionService;

    @PostMapping("/gift-redemption")
    public GiftCardRedemptionResponseDTO performRedemption(@RequestBody GiftCardRedemptionRequestDTO giftCardRedemptionRequestDTO) {
        logger.info("inside gift redemption controller ");
        return giftCardRedemptionService.doRedemption(giftCardRedemptionRequestDTO);
    }
}