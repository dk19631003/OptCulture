package com.optculture.api.dto.GiftCard;

import com.optculture.api.dto.Amount;
import com.optculture.api.dto.Header;
import com.optculture.api.dto.Membership;
import com.optculture.api.dto.UserDetails;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GiftCardRedemptionRequestDTO {
    private Header header;
    private Membership membership;
    private Amount amount;
    private UserDetails userDetails;
}