package com.optculture.app.dto.transaction;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class TransactionItem {
    String transactionType;
    String receiptNumber;
    String docSID;
    LocalDateTime createdDate;
    List<TransactionDto> transactions;
}
