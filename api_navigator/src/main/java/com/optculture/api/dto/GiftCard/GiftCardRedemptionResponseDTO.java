package com.optculture.api.dto.GiftCard;

import com.optculture.api.dto.Status;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GiftCardRedemptionResponseDTO {
    private Status status;
    private String cardNumber;
    private String redeemedAmount;
}