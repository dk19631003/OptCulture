package org.mq.marketer.campaign.controller.service;

import java.util.List;
import java.util.Observable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.CampaignReport;
import org.mq.marketer.campaign.beans.EventTrigger;
import org.mq.marketer.campaign.dao.CampaignReportDao;
import org.mq.marketer.campaign.dao.EventTriggerDao;
import org.mq.marketer.campaign.general.Constants;


public class EventTriggerEventsObservable extends Observable  {

	private EventTriggerEventsObserver eventTriggerEventsObserver;
	
	private EventTriggerDao eventTriggerDao;
	private CampaignReportDao campaignReportDao;

	/*public SalesObservable() {
		
		this.addObserver(eventTriggerObserver);
		
		
	}*/
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	public CampaignReportDao getCampaignReportDao() {
		return campaignReportDao;
	}

	public void setCampaignReportDao(CampaignReportDao campaignReportDao) {
		this.campaignReportDao = campaignReportDao;
	}


	public EventTriggerDao getEventTriggerDao() {
		return eventTriggerDao;
	}


	public void setEventTriggerDao(EventTriggerDao eventTriggerDao) {
		this.eventTriggerDao = eventTriggerDao;
	}


	public EventTriggerEventsObserver getEventTriggerEventsObserver() {
		return eventTriggerEventsObserver;
	}


	public void setEventTriggerEventsObserver(
			EventTriggerEventsObserver eventTriggerEventsObserver) {
		this.eventTriggerEventsObserver = eventTriggerEventsObserver;
	}

	public void notifyForInteractionEvents(Long crId, Long startId, Long endId, String category) {
		logger.info("----just entered-- to observble--- ");
		CampaignReport campaignReport = campaignReportDao.findById(crId);
		
		if(campaignReport == null ) return ;
		
		long userId = campaignReport.getUser().getUserId().longValue();
		
		List<EventTrigger> retList = null;
		
		if(category.equalsIgnoreCase(Constants.CS_TYPE_OPENS))  {
			retList = eventTriggerDao.findAllUserAutoRespondTriggers(userId, Constants.ET_TYPE_ON_CAMPAIGN_OPENED+Constants.STRING_NILL);
		}
		else if (category.equalsIgnoreCase(Constants.CS_TYPE_CLICKS))  {
			
			retList = eventTriggerDao.findAllUserAutoRespondTriggers(userId, Constants.ET_TYPE_ON_CAMPAIGN_CLICKED+Constants.STRING_NILL);
		}
		if( retList == null || startId == null || endId == null) return ;
		
		
		//find only those crid related opens only not all sent by the observable
		List<Long> eventsOfCorrespondingCr = campaignReportDao.findEventsOfCorrespondingCr(crId, startId, endId, category);
		
		if(eventsOfCorrespondingCr == null) return;
		startId = eventsOfCorrespondingCr.get(0);
		endId = eventsOfCorrespondingCr.get((eventsOfCorrespondingCr.size() )- 1);
		
		notifyToObserver(retList, startId, endId, userId, category);
		
		
		
	}

	public void notifyForWebEvents(Long userId, Long startId, Long endId) {
		
		
		
		List<EventTrigger> retList = eventTriggerDao.findAllUserAutoRespondTriggers(userId,
										Constants.ET_TYPE_ON_CONTACT_OPTIN_MEDIUM+Constants.DELIMETER_COMMA+Constants.ET_TYPE_ON_CONTACT_ADDED);
		
		if( retList == null) return ;
		
		if(startId == null || endId == null) return;
		notifyToObserver(retList, startId, endId, userId, Constants.POS_MAPPING_TYPE_CONTACTS);
		
		
		
	}

	public void notifyToObserver(List<EventTrigger> etList, Long startSalesId, Long endSalesId, Long userId, String category ) {
		
		Object[] inputArray = new Object[] {etList, startSalesId, endSalesId, userId, category};
		logger.debug("----notified-- to observer--- ");
		setChanged();
		notifyObservers(inputArray);
		
		
	}
	
	public void notifyToObserver(List<EventTrigger> etList, Long startSalesId, Long endSalesId, Long userId, String category,int trType) {
		
		Object[] objArray = new Object[] {etList, startSalesId, endSalesId, userId, category, trType};
		logger.debug("----notified to observer--- ");
		setChanged();
		logger.debug("----Calling observer update------");
		notifyObservers(objArray);
	}
	
}
