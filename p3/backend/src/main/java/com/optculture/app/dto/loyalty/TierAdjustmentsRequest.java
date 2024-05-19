package com.optculture.app.dto.loyalty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TierAdjustmentsRequest {
    private Long cardNumber;
    private Long tierId;
    private  String reason;
}
