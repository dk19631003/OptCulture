package com.optculture.app.dto.campaign.email;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrgStoreDTO {
    private Long storeId;
    private String storeName;
    private String address;
}
