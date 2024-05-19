package com.optculture.app.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.optculture.app.config.GetLoggedInUser;
import com.optculture.app.repositories.LoyaltySettingRepository;
import com.optculture.app.repositories.UserRepository;
import com.optculture.shared.entities.loyalty.LoyaltySetting;

@Service
public class LoyaltySettingService {

	@Autowired
	private LoyaltySettingRepository loyaltySettingsRepository;

	@Autowired
	private UserRepository userRepository;

	public LoyaltySetting getLoyaltySettingsByUserId(Long userId) {
		return loyaltySettingsRepository.findByUserId(userId);
	}

	public int saveOrUpdateEReceiptConfigured(String eReceiptComponents) {
		Long userId = new GetLoggedInUser().getLoggedInUser().getUserId();
		return loyaltySettingsRepository.saveOrUpdateEReceiptConfigured(userId, eReceiptComponents);
	}

	public String getEReceiptConfigured(Long userId) {
		return loyaltySettingsRepository.findByUserId(userId).getSmartEreceiptJsonConfig();
	}

	public String getEReceiptConfigured() {
		Long userId = new GetLoggedInUser().getLoggedInUser().getUserId();
		return loyaltySettingsRepository.findByUserId(userId).getSmartEreceiptJsonConfig();
	}

}
