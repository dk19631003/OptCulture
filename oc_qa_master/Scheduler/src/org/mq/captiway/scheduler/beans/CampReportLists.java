package org.mq.captiway.scheduler.beans;

import java.io.Serializable;

public class CampReportLists implements Serializable{
	
	private Long campRepListId;
	private String listsName;
	private Long campaignReportId;
	private String SegmentQuery;
	
	
	public CampReportLists() {	}
	
	
	public CampReportLists(Long campaignReportId) {
		this.campaignReportId = campaignReportId; 
		
	}
	
	public Long getCampRepListId() {
		return campRepListId;
	}


	public void setCampRepListId(Long campRepListId) {
		this.campRepListId = campRepListId;
	}


	public String getListsName() {
		return listsName;
	}


	public void setListsName(String listsName) {
		this.listsName = listsName;
	}


	public Long getCampaignReportId() {
		return campaignReportId;
	}


	public void setCampaignReportId(Long campaignReportId) {
		this.campaignReportId = campaignReportId;
	}


	public String getSegmentQuery() {
		return SegmentQuery;
	}


	public void setSegmentQuery(String segmentQuery) {
		SegmentQuery = segmentQuery;
	}
	
	

}
