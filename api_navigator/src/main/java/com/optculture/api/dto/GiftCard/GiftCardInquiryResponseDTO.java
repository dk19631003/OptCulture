package com.optculture.api.dto.GiftCard;

import com.optculture.api.dto.Status;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GiftCardInquiryResponseDTO {
    private Status status;
    private String cardNumber;
    private String productCode;
    private String productName;
    private String availableAmount;
    private String expDate;
}
