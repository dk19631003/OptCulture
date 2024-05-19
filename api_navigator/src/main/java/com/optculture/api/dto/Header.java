package com.optculture.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Header {
    private String requestDate;
    private String storeNumber;
    private String receiptNumber;
    private String docSID;
}
