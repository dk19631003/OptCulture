package com.optculture.app.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.optculture.app.custom.ResponseObject;
import com.optculture.app.dto.campaign.CampaignReport;
import com.optculture.app.services.CommunicationReportService;
import com.optculture.shared.entities.communication.CommunicationEvent;
import com.optculture.shared.util.Constants;

@RestController
@CrossOrigin
@RequestMapping("/api/campaign-report")
public class CommunicationReportController {

	@Autowired
	CommunicationReportService communicationReportService;

	@GetMapping("/{crId}")
	public CampaignReport getCampaignReport(@PathVariable long crId) {
		return communicationReportService.getCampaignReport(crId);
	}

	@GetMapping("/search")
	public List<CommunicationEvent> getRecipientEvents(@RequestParam(required = true) long crId,
			@RequestParam(defaultValue = "", required = false) String recipient,
			@RequestParam(defaultValue = "All", required = true) String status) {
		String recipientCond = (recipient.isEmpty() || recipient.isBlank()) ? "" : " AND recipient ILIKE '" + recipient + "%' ";
		String statusCond = (status.equals("All")) ? "" : " AND event_type = '" + status + "' ";
		return communicationReportService.getCampaignRecipientEventsList(crId, recipientCond, statusCond);
	}

}
