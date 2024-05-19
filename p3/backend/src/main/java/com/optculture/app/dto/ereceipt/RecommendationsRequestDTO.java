package com.optculture.app.dto.ereceipt;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationsRequestDTO {

    private String departments;
    private String productIds;
    private List<Double> priceList;
    private String color;
}
