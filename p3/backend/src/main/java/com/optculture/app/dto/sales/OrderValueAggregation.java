package com.optculture.app.dto.sales;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class OrderValueAggregation {
    Double avgOrderValue;
    Double maxOrderValue;
    Integer noOfOrders;
    Double  totalAmountSpent;
}
