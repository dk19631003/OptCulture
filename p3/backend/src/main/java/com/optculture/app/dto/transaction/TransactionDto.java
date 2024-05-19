package com.optculture.app.dto.transaction;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@AllArgsConstructor
@RequiredArgsConstructor
@Data
public class TransactionDto {
    String transactionType;
    String points="0";
    String amount="0.00";
    String docSID;
    String tierId;

}
