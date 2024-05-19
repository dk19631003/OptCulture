package com.optculture.api.dto.GiftCard;

import com.optculture.api.dto.Header;
import com.optculture.api.dto.Membership;
import com.optculture.api.dto.UserDetails;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GiftCardInquiryRequestDTO {
    private Header header;
    private Membership membership;
    private UserDetails userDetails;
}
