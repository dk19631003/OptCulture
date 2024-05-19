package com.optculture.app.dto.ereceipt;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductsListResponseDTO {

    private String variant_code;
    private String price;
    private String image_url;
    private String department;
    private String description;
    private String url;
}
