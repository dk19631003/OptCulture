package com.optculture.app.controllers;

import com.optculture.app.dto.ereceipt.ProductsListResponseDTO;
import com.optculture.app.dto.ereceipt.RecommendationsRequestDTO;
import com.optculture.app.services.RecommendationsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/auto-recommendations")
public class RecommendationsController {
    Logger logger = LoggerFactory.getLogger(RecommendationsController.class);

    @Autowired
    RecommendationsService recommendationsService;

    @PostMapping("/{id}")
    public ResponseEntity<List<ProductsListResponseDTO>> showList(@PathVariable("id") String userId, @RequestBody RecommendationsRequestDTO recommendationsRequestDTO) {
        logger.info("inside RecommendationsController "+userId);
        return ResponseEntity.ok(recommendationsService.findTopRecommendations(recommendationsRequestDTO,Long.parseLong(userId)));
    }
}
