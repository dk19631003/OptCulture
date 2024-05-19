package org.mq.captiway.scheduler;

import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.MailingList;
import org.mq.captiway.scheduler.beans.Messages;
import org.mq.captiway.scheduler.beans.SMSCampaigns;
import org.mq.captiway.scheduler.beans.SegmentRules;
import org.mq.captiway.scheduler.beans.Users;
import org.mq.captiway.scheduler.dao.CampaignsDao;
import org.mq.captiway.scheduler.dao.ContactsDaoForDML;
import org.mq.captiway.scheduler.dao.MailingListDao;
import org.mq.captiway.scheduler.dao.SegmentRulesDao;
import org.mq.captiway.scheduler.utility.ClickHouseSalesQueryGenerator;
import org.mq.captiway.scheduler.utility.Constants;
import org.mq.captiway.scheduler.utility.SalesQueryGenerator;
import org.mq.captiway.scheduler.utility.Utility;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;

public class PrepareSegmentedContacts extends Thread{

	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	private String segmentList;
	private Object campaign;
	private Object campSchedule;
	private String type; //SMS, email, WA
	private Users user;
	private Long campaignID;
	private Long scheduleID;
	private String token;
	
	public PrepareSegmentedContacts() {
		// TODO Auto-generated constructor stub
	}

	public PrepareSegmentedContacts(String segmentList, Object CampaignId, 
			Object CampScheduleID, String type, Users user, Long campaignID, Long scheduleID, String token) {
		this.segmentList = segmentList;
		this.campaign = CampaignId;
		this.campSchedule = CampScheduleID;
		this.type = type;
		this.user = user;
		this.campaignID = campaignID;
		this.scheduleID =scheduleID;
		this.token = token;
	}
	
	@Override
	public void run() {
		
		try {
			if(segmentList ==null || campaign ==null || campSchedule==null) return;
			 //proceed if its done
			String mlStr = Constants.STRING_NILL;
			String listsName = "";
			String listIdsStr = "";
			String segmentListQuery = Constants.STRING_NILL;
			String qryStr = null;
			String tableToInsert = Constants.STRING_NILL;
			int configuredCount = 0;
			int totalContacts = 0;
			int totalAvailabeleContacts = 0;
			String tolCountQry = "";

			SegmentRulesDao segmentRulesDao = (SegmentRulesDao)ServiceLocator.getInstance().getDAOByName(OCConstants.SEGMENTRULES_DAO);
			MailingListDao mailingListDao = (MailingListDao)ServiceLocator.getInstance().getDAOByName(OCConstants.MAILINGLIST_DAO);
			CampaignsDao campaignsDao = (CampaignsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CAMPAIGNS_DAO);
			ContactsDaoForDML contactsDaoForDML = (ContactsDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.CONTACTS_DAO_FOR_DML);
			boolean success = true;
			String msgStr = "";
			Calendar cal = Calendar.getInstance();
			List<SegmentRules> segmentRules = segmentRulesDao
					.findById(segmentList.split(":")[1]);

			try {

				if (segmentRules == null) {

					success = false;
					msgStr = "Segments configured to this campaign no longer exists. You might have deleted them.";
					return;
				}// if none of configured segments found

				for (SegmentRules segmentRule : segmentRules) {

					if (segmentRule == null) {

						msgStr = "One of the segments configured to this campaign no longer exists. You might have deleted it.";
						continue;

					}// if
					if (segmentRule.getSegRule() == null) {

						msgStr = "Invalid segments configured to this campaign ";
						continue;
					}
					Set<MailingList> mlistSet = new HashSet<MailingList>();
					List<MailingList> mailinglLists = mailingListDao.findByIds(segmentRule.getSegmentMlistIdsStr());
					if (mailinglLists == null) {
						continue;
					}

					mlistSet.addAll(mailinglLists);
					long mlsbit = Utility.getMlsBit(mlistSet);

					//String segmentQuery = SalesQueryGenerator.generateSMSListSegmentQuery(segmentRule.getSegRule(), mlsbit);
					
					//ClickHouse changes
					
					 String segmentQuery = ""; 
					 if(!user.isEnableClickHouseDBFlag()) 
						 segmentQuery =type.equals("SMS") || type.equals("WA")? SalesQueryGenerator.generateSMSListSegmentQuery( segmentRule.getSegRule(),mlsbit): SalesQueryGenerator.generateListSegmentQuery(segmentRule.getSegRule(),mlsbit);//:null; 
					 else 
						 segmentQuery = type.equals("SMS") || type.equals("WA")? ClickHouseSalesQueryGenerator.generateSMSListSegmentQuery( segmentRule.getSegRule(), mlsbit):
								 ClickHouseSalesQueryGenerator.generateListSegmentQuery(segmentRule.getSegRule(), mlsbit);//:null;
					 

					if (segmentQuery == null) {

						msgStr = "Invalid segments configured to this campaign ";
						continue;
					}

					//ClickHouse changes
					/*String segmentCountQry = "";
					if(!user.isEnableClickHouseDBFlag())
						segmentCountQry = type.equals("SMS") ? SalesQueryGenerator
							.generateSMSListSegmentCountQuery(
									segmentRule.getSegRule(), mlsbit) : type.equals("Email") ? SalesQueryGenerator.generateListSegmentCountQuery(segmentRule.getSegRule(), mlsbit):null;
					else
						segmentCountQry = type.equals("SMS") ? ClickHouseSalesQueryGenerator
						.generateSMSListSegmentCountQuery(
								segmentRule.getSegRule(), mlsbit) :  type.equals("Email") ? ClickHouseSalesQueryGenerator.generateListSegmentCountQuery(segmentRule.getSegRule(), mlsbit): null;

					if (segmentCountQry == null) {

						msgStr = "Invalid segments configured to this campaign ";
						continue;
					}*/

					if (SalesQueryGenerator
							.CheckForIsLatestCamapignIdsFlag(segmentRule
									.getSegRule())) {
						String csCampIds = SalesQueryGenerator
								.getCamapignIdsFroFirstToken(segmentRule
										.getSegRule());

						if (csCampIds != null) {
							String crIDs = Constants.STRING_NILL;
							List<Object[]> campList = campaignsDao
									.findAllLatestSentCampaignsBySql(
											segmentRule.getUserId(),
											csCampIds);
							if (campList != null) {
								for (Object[] crArr : campList) {

									if (!crIDs.isEmpty())
										crIDs += Constants.DELIMETER_COMMA;
									crIDs += ((Long) crArr[0])
											.longValue();

								}
							}

							segmentQuery = segmentQuery
									.replace(
											Constants.INTERACTION_CAMPAIGN_CRID_PH,
											("AND cr_id in(" + crIDs + ")"));
							/*segmentCountQry = segmentCountQry
									.replace(
											Constants.INTERACTION_CAMPAIGN_CRID_PH,
											("AND cr_id in(" + crIDs + ")"));*/
						}
					}
					if(type.equalsIgnoreCase("SMS")) {
						SMSCampaigns smsCampaign = (SMSCampaigns)campaign;
						segmentQuery = segmentQuery
								.replace(
										"<MOBILEOPTIN>",
										!smsCampaign.isEnableEntireList() ? " AND c.mobile_opt_in=1"
												: "");
						/*segmentCountQry = segmentCountQry
								.replace(
										"<MOBILEOPTIN>",
										!smsCampaign.isEnableEntireList() ? " AND c.mobile_opt_in=1"
												: "");*/
					}else {
						segmentQuery = segmentQuery.replace("<MOBILEOPTIN>","");
					}

					
					if (!segmentListQuery.isEmpty())
						segmentListQuery += " UNION ";
					segmentListQuery += segmentQuery;
					
					tableToInsert = type.equals("SMS")?"temp_sms_segmented_contacts" : 
						type.equals("Email") ?"temp_email_segmented_contacts" : "temp_wa_segmented_contacts";
					qryStr = "INSERT IGNORE INTO "+tableToInsert+" ("
							+ Constants.QRY_COLUMNS_TEMP_CONTACTS
							+ ", cf_value, domain, event_source_id)  "
							+ segmentQuery;
					logger.debug("qryStr====="+qryStr);
					totalAvailabeleContacts = configuredCount +=  contactsDaoForDML.executeJDBCInsertQuery(qryStr.replace("<TIMESTAMP>", "'"+token+"'"));

					

				}// for

				logger.debug("=== total contacts inserted ==="+totalAvailabeleContacts);
				
				//if(totalAvailabeleContacts > 0) {
					contactsDaoForDML.updateSegmentedContactsParams(campaignID,  scheduleID,  type, token, tableToInsert);
					contactsDaoForDML.updateTheStatus( campaignID,  scheduleID,  type);
				//}
				
			} catch (Exception e) {
				success = false;
				if (logger.isErrorEnabled())
					logger.error(
							"** Exception while getting the available count"
									+ " for the user:"
									+ user.getUserId(), e);
			} 
			/*
			 * SegmentRules segmentRules =
			 * segmentRulesDao.findById(listType.split(":")[1]);
			 * 
			 * if(segmentRules == null) {
			 * 
			 * Messages messages = new Messages("SMS Campaign",
			 * "Segment is missed",
			 * "Segment associated to this campaign is not found,You might deleted this before this schedule,"
			 * + "Campaign can not be sent.", Calendar.getInstance(),
			 * "Inbox", false, "Info", smsCampaign.getUsers());
			 * 
			 * messagesDao.saveOrUpdate(messages);
			 * 
			 * return; }
			 * 
			 * if(segmentRules != null) { listType =
			 * segmentRules.getSegRule(); }
			 */
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception====", e);
		}
	
		
	
	}
	
}
