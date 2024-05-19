package org.mq.marketer.campaign.controller.contacts;

import java.util.Calendar;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.MailingList;
import org.mq.marketer.campaign.beans.SegmentRules;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.dao.CampaignsDao;
import org.mq.marketer.campaign.dao.ContactsDao;
import org.mq.marketer.campaign.dao.MailingListDao;
import org.mq.marketer.campaign.dao.SegmentRulesDao;
import org.mq.marketer.campaign.dao.SegmentRulesDaoForDML;
import org.mq.marketer.campaign.general.ClickHouseSalesQueryGenerator;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.SalesQueryGenerator;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.utils.ServiceLocator;
import org.zkoss.zkplus.spring.SpringUtil;

public class RefreshSegmentThread implements Runnable {
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	public Queue<Object[]> uploadQueue = new LinkedList();
	Object[] pollObj;
	Users currUser;
	
	public void run(){
		while(pollQueue()) {
			refreshSegment();
		}
		
		pollObj = null;
		currUser = GetUser.getUserObj();
		logger.info("refresh segment Thread run curr user >>>>>"+currUser.isEnableClickHouseDBFlag());
		//System.gc();
	} // run
	
	
	
	private void refreshSegment() {
		
		SegmentRules segmentRules = null;
		
		
		try {
			if(pollObj == null) {
				return;
			}
			segmentRules = (SegmentRules)pollObj[0];
			long size = 0;
			long totSize = 0;
			long totMobilesize = 0;
			
			MailingListDao mailingListDao = (MailingListDao)ServiceLocator.getInstance().getDAOByName("mailingListDao");
			ContactsDao contactsDao = (ContactsDao)ServiceLocator.getInstance().getDAOByName("contactsDao");
			SegmentRulesDao segmentRulesDao = (SegmentRulesDao)ServiceLocator.getInstance().getDAOByName("segmentRulesDao");
			SegmentRulesDaoForDML segmentRulesDaoDML = (SegmentRulesDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName("segmentRulesDaoForDML");
			//showSegmentRulePanel(false, segmentRules);
			//TODO need to get the count of contacts matching to this segment rule
			
			List<MailingList> mlList = mailingListDao.findByIds(segmentRules.getSegmentMlistIdsStr());
			Set<MailingList> mlSet = new HashSet<MailingList>();
			long mlBit = 0;
			
			if(mlList != null)  {
			mlSet.addAll(mlList);
			 mlBit = Utility.getMlsBit(mlSet);
			
			}
			
			
			//String generatedquery = SalesQueryGenerator.generateListSegmentQuery(segmentRules.getSegRule(), true, Constants.SEGMENT_ON_EMAIL,mlBit);
			//ClickHouse changes
			String generatedquery = "";
			String generatedTotquery = "";
			String generatedMobilequery = "";
			if(currUser!=null && !currUser.isEnableClickHouseDBFlag()) {				
				generatedquery = SalesQueryGenerator.generateListSegmentQuery(segmentRules.getSegRule(), false, Constants.SEGMENT_ON_EMAIL,mlBit);
				generatedTotquery = SalesQueryGenerator.generateListSegmentQuery(segmentRules.getSegRule(),
						true, Constants.SEGMENT_ON_EXTERNALID, mlBit);
				generatedMobilequery = SalesQueryGenerator.generateListSegmentQuery(segmentRules.getSegRule(),false, Constants.SEGMENT_ON_MOBILE, mlBit);
			}else {
				generatedquery = ClickHouseSalesQueryGenerator.generateListSegmentQuery(segmentRules.getSegRule(), false, Constants.SEGMENT_ON_EMAIL,mlBit);
				generatedTotquery = ClickHouseSalesQueryGenerator.generateListSegmentQuery(segmentRules.getSegRule(),
						true, Constants.SEGMENT_ON_EXTERNALID, mlBit);
				generatedMobilequery = ClickHouseSalesQueryGenerator.generateListSegmentQuery(segmentRules.getSegRule(),false, Constants.SEGMENT_ON_MOBILE, mlBit);

			}
			//String generatedMobilequery = SalesQueryGenerator.generateListSegmentQuery(segmentRules.getSegRule(),true, Constants.SEGMENT_ON_MOBILE, mlBit);
			
			
			
			
			String generatedCountQuery =  null;
			String generatedTotCountquery = null;
			String generatedMobileCountquery = null;
			
			if(generatedquery != null) {
				
				 //generatedCountQuery = SalesQueryGenerator.generateListSegmentCountQuery(segmentRules.getSegRule(), true, Constants.SEGMENT_ON_EMAIL,mlBit);
				//ClickHouse changes
				if(!currUser.isEnableClickHouseDBFlag())
					generatedCountQuery = SalesQueryGenerator.generateListSegmentCountQuery(segmentRules.getSegRule(), false, Constants.SEGMENT_ON_EMAIL,mlBit);
				else
					generatedCountQuery = ClickHouseSalesQueryGenerator.generateListSegmentCountQuery(segmentRules.getSegRule(), false, Constants.SEGMENT_ON_EMAIL,mlBit);

			}

			if(generatedTotquery != null) {
				//ClickHouse changes
				if(!currUser.isEnableClickHouseDBFlag())
					generatedTotCountquery = SalesQueryGenerator.generateListSegmentCountQuery(segmentRules.getSegRule(),
						true, Constants.SEGMENT_ON_EXTERNALID, mlBit);
				else
					generatedTotCountquery = ClickHouseSalesQueryGenerator.generateListSegmentCountQuery(segmentRules.getSegRule(),
							true, Constants.SEGMENT_ON_EXTERNALID, mlBit);
			
				
			}
			
			
			
			//String generatedMobilequery = segmentRules.getMobileSegQuery();
			//generatedMobilequery = SalesQueryGenerator.generateListSegmentQuery(segmentRules.getSegRule(),true, Constants.SEGMENT_ON_MOBILE, mlBit);
			//ClickHouse changes
			if(!currUser.isEnableClickHouseDBFlag())
				generatedMobilequery = SalesQueryGenerator.generateListSegmentQuery(segmentRules.getSegRule(),false, Constants.SEGMENT_ON_MOBILE, mlBit);
			else
				generatedMobilequery = ClickHouseSalesQueryGenerator.generateListSegmentQuery(segmentRules.getSegRule(),false, Constants.SEGMENT_ON_MOBILE, mlBit);
			

			if(generatedMobilequery != null) {
				
				generatedMobilequery = generatedMobilequery.replace("<MOBILEOPTIN>","");
				 //generatedMobileCountquery = SalesQueryGenerator.generateListSegmentCountQuery(segmentRules.getSegRule(),true, Constants.SEGMENT_ON_MOBILE, mlBit);
				//ClickHouse changes
				if(!currUser.isEnableClickHouseDBFlag())
					generatedMobileCountquery = SalesQueryGenerator.generateListSegmentCountQuery(segmentRules.getSegRule(),false, Constants.SEGMENT_ON_MOBILE, mlBit);
				else
					generatedMobileCountquery = ClickHouseSalesQueryGenerator.generateListSegmentCountQuery(segmentRules.getSegRule(),false, Constants.SEGMENT_ON_MOBILE, mlBit);

				generatedMobileCountquery = generatedMobileCountquery.replace("<MOBILEOPTIN>","");
				
			}
			
			
			if(SalesQueryGenerator.CheckForIsLatestCamapignIdsFlag(segmentRules.getSegRule())) {
				String csCampIds = SalesQueryGenerator.getCamapignIdsFroFirstToken(segmentRules.getSegRule());
				
				if(csCampIds != null ) {
					String crIDs = Constants.STRING_NILL;
					CampaignsDao campaignsDao = (CampaignsDao)SpringUtil.getBean("campaignsDao");
					List<Object[]> campList = campaignsDao.findAllLatestSentCampaignsBySql(segmentRules.getUserId(), csCampIds);
					if(campList != null) {
						for (Object[] crArr : campList) {
							
							if(!crIDs.isEmpty()) crIDs += Constants.DELIMETER_COMMA;
							crIDs += ((Long)crArr[0]).longValue();
							
						}
					}
					
					if(generatedquery != null) generatedquery = generatedquery.replace(Constants.INTERACTION_CAMPAIGN_CRID_PH, ("AND cr_id in("+crIDs+")"));
					if(generatedMobilequery != null)  {
						generatedMobilequery = generatedMobilequery.replace(Constants.INTERACTION_CAMPAIGN_CRID_PH, ("AND cr_id in("+crIDs+")"));
						generatedMobilequery = generatedMobilequery.replace("<MOBILEOPTIN>","");
					}
					if(generatedTotquery != null)  generatedTotquery = generatedTotquery.replace(Constants.INTERACTION_CAMPAIGN_CRID_PH, ("AND cr_id in("+crIDs+")"));
					
					if(generatedCountQuery != null) generatedCountQuery = generatedCountQuery.replace(Constants.INTERACTION_CAMPAIGN_CRID_PH, ("AND cr_id in("+crIDs+")"));
					
					logger.info("generatedMobileCountquery----------------------"+generatedMobileCountquery);
					if(generatedMobileCountquery != null) {
						generatedMobileCountquery = generatedMobileCountquery.replace(Constants.INTERACTION_CAMPAIGN_CRID_PH, ("AND cr_id in("+crIDs+")"));
						logger.info("qry--------before------------"+generatedMobileCountquery);
						generatedMobileCountquery = generatedMobileCountquery.replace("<MOBILEOPTIN>","");
						logger.info("qry--------after------------"+generatedMobileCountquery);
					}
					if(generatedTotCountquery != null) generatedTotCountquery = generatedTotCountquery.replace(Constants.INTERACTION_CAMPAIGN_CRID_PH, ("AND cr_id in("+crIDs+")"));
					
					//segmentQuery = segmentQuery.replace(Constants.INTERACTION_CAMPAIGN_CRID_PH, crIDs);
				}
			}
			
			
			
			if(generatedCountQuery != null ) {
				if(!currUser.isEnableClickHouseDBFlag())
					size = contactsDao.getSegmentedContactsCount(generatedCountQuery);
				else
					size = contactsDao.getSegmentedContactsCountFromCH(generatedCountQuery);
				
				segmentRules.setSize(size);
				
				if(generatedquery != null) segmentRules.setEmailSegQuery(generatedquery);
				
				logger.debug("Email :: "+ generatedquery);
				
			}
			if(generatedTotCountquery != null) {
				if(!currUser.isEnableClickHouseDBFlag()) 
					totSize = contactsDao.getSegmentedContactsCount(generatedTotCountquery );
				else
					totSize = contactsDao.getSegmentedContactsCountFromCH(generatedTotCountquery );
				
				segmentRules.setTotSize(totSize);
				
				if(generatedTotquery != null)  segmentRules.setTotSegQuery(generatedTotquery);
				
				logger.debug("total :: "+generatedTotquery);
				
			}
			
			if(generatedMobileCountquery != null) {
				if(!currUser.isEnableClickHouseDBFlag())
					totMobilesize = contactsDao.getSegmentedContactsCount(generatedMobileCountquery);
				else
					totMobilesize = contactsDao.getSegmentedContactsCountFromCH(generatedMobileCountquery);
				
				segmentRules.setTotMobileSize(totMobilesize);
				
				if(generatedMobilequery != null)segmentRules.setMobileSegQuery(generatedMobilequery);
				
				logger.debug("Mobile :: "+generatedMobilequery);
				
			}
			
			/*List list = row.getChildren();
			((Label)list.get(3)).setValue(MyCalendar.calendarToString(Calendar.getInstance(), MyCalendar.FORMAT_DATETIME_STDATE,clientTimeZone));
			((Label)list.get(4)).setValue(""+totSize);
			((Label)list.get(5)).setValue(""+size);
			((Label)list.get(6)).setValue(""+totMobilesize);*/
			
			/*segmentRules.setSize(size);
			segmentRules.setTotSize(totSize);
			segmentRules.setTotMobileSize(totMobilesize);*/
			
			
			
			segmentRules.setLastRefreshedOn(Calendar.getInstance());
			segmentRulesDaoDML.saveOrUpdate(segmentRules);
			
			
            
            
            
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		
		
	}

	boolean pollQueue(){
		pollObj = uploadQueue.poll();
		if(pollObj!=null) {
			return true;
		} 
		else {
			return false;
		}
	} // pollQueue

}
