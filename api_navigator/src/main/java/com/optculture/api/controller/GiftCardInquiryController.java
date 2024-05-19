package com.optculture.api.controller;

import com.optculture.api.dto.GiftCard.GiftCardInquiryRequestDTO;
import com.optculture.api.dto.GiftCard.GiftCardInquiryResponseDTO;
import com.optculture.api.service.GiftCardInquiryService;
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
public class GiftCardInquiryController {

    Logger logger = LoggerFactory.getLogger(GiftCardInquiryController.class);

    @Autowired
    GiftCardInquiryService giftCardInquiryService;

    @PostMapping("/gift-inquiry")
    public ResponseEntity<GiftCardInquiryResponseDTO> doInquiry(@RequestBody GiftCardInquiryRequestDTO giftCardInquiryRequestDTO) {
        logger.info("inside gift inquiry controller ");
        return ResponseEntity.ok(giftCardInquiryService.getInquiryDetails(giftCardInquiryRequestDTO));
    }
}
