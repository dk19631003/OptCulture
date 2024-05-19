package org.mq.marketer.campaign.beans;

import java.io.Serializable;

public class WACampReportLists implements Serializable {
	
	
	private Long waCampRepListId;
	private Long waCampaignReportId;
	private String listsName;
	
	private String SegmentQuery;
	
	
	
	public String getSegmentQuery() {
		return SegmentQuery;
	}

	public void setSegmentQuery(String segmentQuery) {
		SegmentQuery = segmentQuery;
	}
	
	public WACampReportLists() {}



	public Long getWaCampRepListId() {
		return waCampRepListId;
	}



	public void setWaCampRepListId(Long waCampRepListId) {
		this.waCampRepListId = waCampRepListId;
	}



	public Long getWaCampaignReportId() {
		return waCampaignReportId;
	}



	public void setWaCampaignReportId(Long waCampaignReportId) {
		this.waCampaignReportId = waCampaignReportId;
	}



	public String getListsName() {
		return listsName;
	}



	public void setListsName(String listsName) {
		this.listsName = listsName;
	}
	
	
	
	
	
	
}
