package org.mq.marketer.campaign.beans;

import java.io.Serializable;

public class SMSCampReportLists implements Serializable {
	
	
	private Long smsCampRepListId;
	private Long smsCampaignReportId;
	private String listsName;
	
	private String SegmentQuery;
	
	
	
	public String getSegmentQuery() {
		return SegmentQuery;
	}

	public void setSegmentQuery(String segmentQuery) {
		SegmentQuery = segmentQuery;
	}
	
	public SMSCampReportLists() {}



	public Long getSmsCampRepListId() {
		return smsCampRepListId;
	}



	public void setSmsCampRepListId(Long smsCampRepListId) {
		this.smsCampRepListId = smsCampRepListId;
	}



	public Long getSmsCampaignReportId() {
		return smsCampaignReportId;
	}



	public void setSmsCampaignReportId(Long smsCampaignReportId) {
		this.smsCampaignReportId = smsCampaignReportId;
	}



	public String getListsName() {
		return listsName;
	}



	public void setListsName(String listsName) {
		this.listsName = listsName;
	}
	
	
	
	
	
	
}
