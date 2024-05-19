package com.optculture.app.dto.campaign;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class CampaignReport {

	private String campaignName;
	private LocalDateTime campaignSentDate;
	private long totalCount;
	private String communicationType;
	private String sentTemplate;
	private String[] segmentNames;
	private Map<String, String> eventCountMap;
	private List<HourlyEventCountsDTO> listOfhourlyEventCounts;

}
