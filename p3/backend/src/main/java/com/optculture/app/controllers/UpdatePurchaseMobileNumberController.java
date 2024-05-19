package com.optculture.app.controllers;

import com.optculture.app.config.GetLoggedInUser;
import com.optculture.app.dto.updatePurchaseMobile.UpdateMobileRequestDTO;
import com.optculture.app.dto.updatePurchaseMobile.UpdateMobileResponseDTO;
import com.optculture.app.services.UpdatePurchaseMobileService;
import com.optculture.shared.entities.org.User;
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
public class UpdatePurchaseMobileNumberController {

    Logger logger = LoggerFactory.getLogger(UpdatePurchaseMobileNumberController.class);
    @Autowired
    UpdatePurchaseMobileService updateMobileService;

    @Autowired
    private GetLoggedInUser getLoggedInUser;

    @PostMapping("/update-mobile")
    public ResponseEntity<UpdateMobileResponseDTO> updateMobile(@RequestBody UpdateMobileRequestDTO updateMobileRequestDTO) {
        logger.info("inside update mobile");
        User currentUser=getLoggedInUser.getLoggedInUser();
        logger.info("logged in user id is {}", currentUser.getUserId());
        return ResponseEntity.ok(updateMobileService.updateMobile(updateMobileRequestDTO,currentUser));
    }
}
