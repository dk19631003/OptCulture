package com.optculture.app.controllers;

import java.util.Map;

import com.optculture.app.config.GetLoggedInUser;
import com.optculture.shared.entities.org.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.optculture.app.dto.ereceipt.EreceiptType;
import com.optculture.app.services.ContactService;
import com.optculture.app.services.CustomerFeedbackService;
import com.optculture.app.services.EreceiptService;
import com.optculture.app.services.LoyaltySettingService;
import com.optculture.app.services.ReferralService;

@RestController
@CrossOrigin
@RequestMapping("/api/ereceipt")
public class EReceiptController {

	private final Logger logger = LoggerFactory.getLogger(EReceiptController.class);

	@Autowired
	private EreceiptService ereceiptService;

	@Autowired
	private ContactService contactService;

	@Autowired
	private CustomerFeedbackService customerFeedbackService;
	
	@Autowired
	private LoyaltySettingService loyaltySettingService;

	@Autowired
	ReferralService referralService;

	@Autowired
	GetLoggedInUser getLoggedInUser;

	@GetMapping("/{originalShortCode}")
	public EreceiptType getEReceiptData(@PathVariable(name = "originalShortCode") String originalShortCode,
			@RequestParam String channelType) {
		logger.info("Original Short Code :: {}", originalShortCode);
		return ereceiptService.getEReceiptData(originalShortCode, channelType);
	}

	@PostMapping("/update-contact")
	public void updateContactDetails(@RequestBody Map<String, Object> contactData) {
		contactService.updateEreceiptContact(contactData);
	}

	@PostMapping("/nps")
	public void saveCustomerFeedback(@RequestBody Map<String, Object> customerFeedBackData) {
		customerFeedbackService.saveCustomerFeedBack(customerFeedBackData);
	}
  
	@PostMapping("/save-components")
	public ResponseEntity saveEReceiptConfigured(@RequestBody String eReceiptComponents) {
		int updatedRecords = loyaltySettingService.saveOrUpdateEReceiptConfigured(eReceiptComponents);
		return updatedRecords > 0 ? new ResponseEntity<>("SUCCESS", HttpStatus.OK) : new ResponseEntity<>("FAILED", HttpStatus.OK);
	}

	@GetMapping("/get-components")
	public String findConfiguredEReceipt() {
		return loyaltySettingService.getEReceiptConfigured();
	}

	@GetMapping("/items-list")
	public ResponseEntity getReceiptItemsList(@RequestParam(defaultValue = "--",required = false) String receiptNumber, @RequestParam(defaultValue = "--",required = false) String docSid){
		User user=getLoggedInUser.getLoggedInUser();
		return ereceiptService.getReceiptItemsList(docSid,receiptNumber,user.getUserId());
	}

	@PostMapping("/referral")
	public ResponseEntity getReferralData(@RequestBody Map<String, Object> data){
		logger.info("Inside ReferralController");
		return ResponseEntity.ok(referralService.findReferralData(data));

	}
}
