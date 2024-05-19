package com.optculture.app.dto.sales;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
public class SalesDto {
    String docSid;
    Double amount=0.00;
    LocalDateTime createdDate;
    String receiptNumber;
}
