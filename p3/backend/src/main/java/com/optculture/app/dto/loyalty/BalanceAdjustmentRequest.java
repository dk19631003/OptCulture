package com.optculture.app.dto.loyalty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BalanceAdjustmentRequest {

    private Long cardNumber;
    private String balanceType;
    private String adjustmentType;
    private Double value;
    private String reason;
    @Override
    public String toString(){
        return this.getCardNumber()+"-"+this.getBalanceType()+"-"+this.getAdjustmentType()+"-"+this.getValue()+"-"+this.getReason();
    }

}
