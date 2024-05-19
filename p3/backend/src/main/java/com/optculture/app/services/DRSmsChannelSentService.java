package com.optculture.app.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.optculture.app.repositories.DRSmsChannelSentRepository;
import com.optculture.shared.entities.communication.ereceipt.DRSmsChannelSent;

@Service
public class DRSmsChannelSentService {

	@Autowired
	DRSmsChannelSentRepository drSmsChannelSentRepository;

	public DRSmsChannelSent getDRSmsChannelSentByOriginalShortCode(String originalShortCode) {
		return drSmsChannelSentRepository.findFirstByOriginalShortCodeOrderByIdDesc(originalShortCode);
	}

}
