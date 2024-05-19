package com.optculture.app.controllers;

import com.optculture.app.dto.loyalty.BalanceAdjustmentRequest;
import com.optculture.app.dto.loyalty.BalanceAdjustmentResponse;
import com.optculture.app.dto.loyalty.TierAdjustmentsRequest;
import com.optculture.app.services.BalanceAdjustmentService;
import com.optculture.app.services.TierAdjustmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AdjustmentsController {
    Logger logger = LoggerFactory.getLogger(AdjustmentsController.class);

    @Autowired
    BalanceAdjustmentService balanceAdjustmentService;
    @Autowired
    TierAdjustmentService tierAdjustmentService;

    @PostMapping("/balance-adjustment")
    public ResponseEntity<BalanceAdjustmentResponse> performBalanceAdjustment(@RequestBody BalanceAdjustmentRequest balanceAdjustmentRequest) {
        logger.info("Inside of Class adjustmentController");
        return ResponseEntity.ok(balanceAdjustmentService.doBalanceAdjustment(balanceAdjustmentRequest));
    }


    @PostMapping("/tier-adjustment")
    public ResponseEntity performTierAdjustment(@RequestBody TierAdjustmentsRequest tierAdjustmentsRequest) {
        logger.info("Inside of Class adjustmentController");
        return ResponseEntity.ok(tierAdjustmentService.doTierAdjustments(tierAdjustmentsRequest));
    }
}
