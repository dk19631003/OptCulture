package com.optculture.app.dto.updatePurchaseMobile;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateMobileRequestDTO {

    private String wrongMembership;
    private String correctMobile;
    private String receiptNumber;
    private String docsId;
}
