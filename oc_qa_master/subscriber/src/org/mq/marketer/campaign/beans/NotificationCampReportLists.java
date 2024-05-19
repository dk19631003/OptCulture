package org.mq.marketer.campaign.beans;

import java.io.Serializable;

public class NotificationCampReportLists implements Serializable {
	
	
	private Long notificationCampRepListId;
	private Long notificationCampaignReportId;
	private String listsName;
	private String SegmentQuery;
	
	public Long getNotificationCampRepListId() {
		return notificationCampRepListId;
	}
	public void setNotificationCampRepListId(Long notificationCampRepListId) {
		this.notificationCampRepListId = notificationCampRepListId;
	}
	public Long getNotificationCampaignReportId() {
		return notificationCampaignReportId;
	}
	public void setNotificationCampaignReportId(Long notificationCampaignReportId) {
		this.notificationCampaignReportId = notificationCampaignReportId;
	}
	public String getListsName() {
		return listsName;
	}
	public void setListsName(String listsName) {
		this.listsName = listsName;
	}
	public String getSegmentQuery() {
		return SegmentQuery;
	}
	public void setSegmentQuery(String segmentQuery) {
		SegmentQuery = segmentQuery;
	}
	
	
}
