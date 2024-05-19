package org.mq.captiway.scheduler.dao;

import org.hibernate.*;
import org.mq.captiway.scheduler.beans.CampaignReport;
import org.mq.captiway.scheduler.beans.CampaignSent;
import org.mq.captiway.scheduler.beans.Campaigns;
import org.mq.captiway.scheduler.beans.Contacts;
import org.mq.captiway.scheduler.beans.MailingList;
import org.mq.captiway.scheduler.beans.Opens;
import org.mq.captiway.scheduler.dao.CampaignSentDao;
import org.mq.captiway.scheduler.dao.OpensDao;
import org.mq.captiway.scheduler.utility.Constants;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.*;

import java.text.*;

import javax.mail.search.SentDateTerm;
import javax.servlet.http.HttpServletRequest;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.hibernate3.*;

@SuppressWarnings( { "unchecked", "unused" })
public class CampaignSentDao extends AbstractSpringDao {

	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	private JdbcTemplate jdbcTemplate;
	
	private Long currentSentId;

	public CampaignSentDao() {
	}

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public CampaignSent find(Long id) {
		return (CampaignSent) super.find(CampaignSent.class, id);
	}

	/*public void saveOrUpdate(CampaignSent campaignSent) {
		super.saveOrUpdate(campaignSent);
	}*/

	/*public void delete(CampaignSent campaignSent) {
		super.delete(campaignSent);
	}*/

	public List findAll() {
		return super.findAll(CampaignSent.class);
	}

	private OpensDao opensDao;

	public void setOpensDao(OpensDao opensDao) {
		this.opensDao = opensDao;
	}

	public OpensDao getOpensDao() {
		return this.opensDao;
	}

	/*public void saveByCollection(Collection<CampaignSent> campList) {
		super.saveOrUpdateAll(campList);
	}*/

	/*public int updateCampaignSent(Long idL, String type) {
		String queryStr = null;
		long id = idL.longValue();
		if(type.equals(Constants.CS_TYPE_OPENS))
			queryStr = 
				" UPDATE CampaignSent SET opens = " +
				" (SELECT count(openId) FROM Opens WHERE sentId = "+ id + ")" +
				" WHERE sentId = " + id;

		else if(type.equals(Constants.CS_TYPE_CLICKS))
			queryStr = 
				" UPDATE CampaignSent SET clicks = " +
				" (SELECT count(clickId) FROM Clicks WHERE sentId = "+ id + ")" +
				" WHERE sentId = " + id;
		
		else if(type.equals(Constants.CS_STATUS_UNSUBSCRIBED))
			queryStr = 
				"UPDATE CampaignSent SET status = '" + Constants.CS_STATUS_UNSUBSCRIBED + 
				"' WHERE sentId = " + id;
		
		else if(type.equalsIgnoreCase(Constants.CS_STATUS_SUCCESS))
			queryStr = 
				"UPDATE CampaignSent SET status = '" + Constants.CS_STATUS_SUCCESS + 
				"' WHERE sentId = " + id;
		else if(type.equalsIgnoreCase(Constants.CS_STATUS_SPAMMED))
			queryStr = "UPDATE CampaignSent SET status = '" + Constants.CS_STATUS_SPAMMED + 
				"' WHERE sentId = " + id;
		else if(type.equalsIgnoreCase(Constants.CS_STATUS_BOUNCED)) {
			queryStr = "UPDATE CampaignSent SET status = '" + Constants.CS_STATUS_BOUNCED + 
				"' WHERE sentId = " + id;
		}	
		return ( (queryStr == null)? 0 : executeUpdate(queryStr) );
		
	}*/

	
	/*public void  updateBulkCampaignSent(Long crId , String type) {
		
		String queryStr = null; 
		
		if(crId == null || type == null) {
			
			if(logger.isDebugEnabled()) logger.debug("No campaign Report or type has  found");
			return ;
		}
		
		try {
			
			if(type.equals(Constants.CR_TYPE_OPENS)) {
				//update open count
				queryStr = "UPDATE campaign_sent cs INNER JOIN " +
							"( SELECT sent_id, count(open_id) open_count" +
								" FROM opens o" +
								" GROUP BY sent_id " +
							") ocs ON ocs.sent_id = cs.sent_id " +
						"SET cs.opens = ocs.open_count where cs.cr_id="+crId.longValue();
				
				executeJdbcUpdateQuery(queryStr);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::::" , e);
		}
		try {
			
			if(type.equals(Constants.CR_TYPE_CLICKS)) {
				//update click count
				queryStr = "UPDATE campaign_sent cs INNER JOIN " +
							"( SELECT sent_id, count(click_id) click_count" +
								" FROM clicks c" +
								" GROUP BY sent_id " +
							") ccs ON ccs.sent_id = cs.sent_id " +
							"SET cs.clicks = ccs.click_count where cs.cr_id="+crId.longValue();
				
				executeJdbcUpdateQuery(queryStr);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::::" , e);
		}
		
		
		
		
	}*/
	
	
	/*public int  finalUpdateCampaignSent(Long crId) {
		
		
		try {
			
				//update open count
				String queryStr = "UPDATE campaign_sent SET opens=1  where cr_id="+crId.longValue()+" AND clicks>0 AND opens=0";
				
				return executeJdbcUpdateQuery(queryStr);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::::" , e);
			return 0;
		}
		
		
		
		
	}*/
	
	
	public CampaignSent findById(long id){
		CampaignSent cs = null;
		try {
			List list = getHibernateTemplate().find(
					"from CampaignSent where sentId = " + id);
			if (list.size() > 0) {
				cs = (CampaignSent) list.get(0);
			}
		} catch (Exception e) {
			logger.error(" ** Exception : ", e ); 
		}
		return cs;
	}

	/*public int setStatusBySentId(Long sentId, String status, Long crId) {
		try {
			int count = jdbcTemplate.update(
					" UPDATE campaign_sent SET status = '" + status + "'" +
					" WHERE  sent_id = " + sentId+" AND cr_id=" + crId +" AND status != '" + status + "'");
			return count;
		} catch (DataAccessException e) {
			logger.error(" ** Exception : ", e ); 
			return 0;
		}
	}*/

	/*public int updateAsSpamBySentId(String sentId, String crId) {
		try {
			
			int count = jdbcTemplate.update(
					" UPDATE campaign_sent SET status = '" + Constants.CS_STATUS_SPAMMED + "'" +
					" WHERE  sent_id = " + sentId + " and status = '" + Constants.CS_STATUS_SUCCESS + "'");
			
			return count;
		} catch (DataAccessException e) {
			logger.error(" ** Exception : ", e ); 
			return 0;
		}
	}*/
	
	public Long getCrIdBySentId(Long sentId) {
		try {
			List list = getHibernateTemplate().find("SELECT cs.campaignReport FROM CampaignSent AS cs WHERE cs.sentId="+sentId);
//			List list = jdbcTemplate.queryForList("SELECT cr_id FROM campaign_sent WHERE sent_id = "+sentId, args)ForLong();
			if(logger.isDebugEnabled()) logger.debug("List :" + list);
			if(list.size() >0 ) {
				return ((CampaignReport)list.get(0)).getCrId();
			}
//			return jdbcTemplate.queryForLong("SELECT cr_id FROM campaign_sent WHERE sent_id = "+sentId);
		} catch (DataAccessException e) {
			logger.error(" ** Exception : ", e ); 
		}
		return null;
	}
	
	/**
	 * Added for Event Trigger 
	 * 
	 * @param crIdsList
	 * @param offset
	 * @return
	 * For  given crIdsList we fetch sentIds if there are any opens.
	 * make sure the value stored from subscriber to cust field is of format %d/%m/%Y ex: 10/07/1985
	 */
	public String getSentIdsByCrIdsForOpens(String crIdsList,int offset) {

		if(logger.isInfoEnabled()) logger.info("inside getSentIdsByCrIdsForOpens");

		String queryStr = "",sentIdsListStr = "";
		List<Integer> sentIdsList = null;

		try {

			queryStr = " SELECT DISTINCT cs.sent_id "+
			" FROM campaign_sent cs, opens op1 " +
			" WHERE cs.sent_id = op1.sent_id "+ 
			" AND cs.cr_id IN ("+crIdsList+") "+
			" AND op1.open_date = (SELECT MAX(op2.open_date) "+
			" FROM opens op2 "+
			" WHERE op2.sent_id = op1.sent_id) ";

			/*
			queryStr = " SELECT DISTINCT cs.sentId "+
			" FROM CampaignSent cs, Clicks ck1 " +
			" WHERE cs.campaignReport IN ("+crIdsList+") "+ 
			" AND cs.sentId = ck1.sentId "+
			" AND ck1.clickDate = (SELECT MAX(clickDate) "+
			" FROM Clicks "+
			" WHERE sentId = ck1.sentId
			" GROUP BY clickUrl) ";
			 */

			if(offset >= 1440){ 

				offset = offset/1440; 
				queryStr += " AND DATEDIFF(now(),op1.open_date) = "+offset+" ";
			}
			else { // offset >= 0	

				if(offset > 0){

					offset = offset/60; //converting minutes to hours
					queryStr += " AND now() > op1.open_date ";
				}
				queryStr += " AND HOUR(TIMEDIFF(now(),op1.open_date)) = "+offset;

			}

			if(logger.isInfoEnabled()) logger.info("executing query "+queryStr);

			sentIdsList = jdbcTemplate.queryForList(queryStr,Integer.class);

			if(sentIdsList == null || sentIdsList.isEmpty()) {

				if(logger.isDebugEnabled()) logger.debug(" no campaignSent records found for given crIds ");
				return "";
			}

			for(Iterator<Integer> iterator = sentIdsList.iterator();iterator.hasNext();){

				if(sentIdsListStr.length() == 0){

					sentIdsListStr += iterator.next();
				}
				else {

					sentIdsListStr += ","+iterator.next();
				}
			}

			return sentIdsListStr;
		}
		catch(Exception e) {

			logger.error(" **Exception ",e);
			return null;
		}

	} // getSentIdsByCrIdsForOpens
	
	/**
	 * added for EventTrigger
	 * 
	 * @param crIdsList
	 * @param urlListStr
	 * @param offset
	 * @return
	 * // make sure the value stored from subscriber to cust field is of format %d/%m/%Y ex: 10/07/1985
	 * 
	 * For  given crIdsList we fetch sentIds if there are any clicks
	 */
	public String getSentIdsByCrIdsForClicks(String crIdsList,String urlListStr,int offset) {

		if(logger.isInfoEnabled()) logger.info("inside getSentIdsByCrIdsClicks");

		String queryStr = "",sentIdsListStr = "";
		List<Integer> sentIdsList = null;

		try {

			queryStr = " SELECT DISTINCT cs.sentId "+
			" FROM CampaignSent cs, Clicks ck1 " +
			" WHERE cs.sentId = ck1.sentId "+ 
			" AND cs.campaignReport IN ("+crIdsList+") " +
			" AND ck1.clickUrl IN ("+urlListStr+") "+
			" AND ck1.clickDate = (SELECT MAX(clickDate) "+
			" FROM Clicks "+
			" WHERE sentId = ck1.sentId" +
			" AND clickUrl = ck1.clickUrl "+
			" GROUP BY clickUrl) ";


			if(offset >= 1440){ 

				offset = offset/1440; 
				queryStr += " AND DATEDIFF(now(),ck1.clickDate) = "+offset+" ";
			}
			else { // offset >= 0	

				if(offset > 0){

					offset = offset/60; //converting minutes to hours
					queryStr += " AND now() > ck1.clickDate ";
				}
				queryStr += " AND HOUR(TIMEDIFF(now(),ck1.clickDate)) = "+offset;

			}

			if(logger.isInfoEnabled()) logger.info("executing query "+queryStr);

			sentIdsList = getHibernateTemplate().find(queryStr);

			if(sentIdsList == null || sentIdsList.isEmpty()) {

				if(logger.isDebugEnabled()) logger.debug(" no campaignSent records found for given crIds ");
				return "";
			}

			for(Iterator<Integer> iterator = sentIdsList.iterator();iterator.hasNext();){

				if(sentIdsListStr.length() == 0){

					sentIdsListStr += iterator.next();
				}
				else {

					sentIdsListStr += ","+iterator.next();
				}
			}

			return sentIdsListStr;
		}
		catch(Exception e) {

			if(logger.isErrorEnabled()) logger.error(" **Exception ",e);
			return null;
		}

	}//getSentIdsByCrIdsForClicks

	
	public List<String> getRecentStatusForEmailId(Long crId, String emailId){
		
		try {
			
			String queryString = 
				" SELECT status FROM campaign_sent WHERE email_id='"+emailId+"' " +
				" ORDER BY sent_id DESC LIMIT 0,5";
			return jdbcTemplate.queryForList(queryString, String.class);
			
		} catch (DataAccessResourceFailureException e) {
			logger.error(" ** Exception : ", e );
			return null;
		} catch (HibernateException e) {
			logger.error(" ** Exception : ", e );
			return null;
		} catch (IllegalStateException e) {
			logger.error(" ** Exception : ", e ); 
			return null;
		}
	}
	
	public synchronized Long getCurrentSentId() {
		
		try {
			
			Long currentSentId =  (Long)getHibernateTemplate().find("SELECT MAX(sentId) FROM CampaignSent ").get(0);
			if(currentSentId == null) {
				currentSentId = 0l;
			}
			this.currentSentId = currentSentId;
			return currentSentId;
		} catch (Exception e) {
			logger.error(" ** Exception : while getting the current sentId from the Database so returning" +
					"currentSentId value as -"+this.currentSentId+100000, e);
			return this.currentSentId+100000;
		}
		
	}
	
	public Long getUserIdBySentId(Long sentId) {
		
		return jdbcTemplate.queryForLong(
				" SELECT cr.user_id FROM campaign_report cr, campaign_sent cs " +
				" WHERE cs.sent_id="+sentId+" AND cs.cr_id= cr.cr_id");
	}
	
	/**
	 * Added for Auto Program 
	 * 
	 * @param activityCondQuery
	 * @return
	 * 
	 * this method will return contact ids based on the given crids and opens clicks criteria( which are mentioned in the given query).
	 */
	
	public String getContactIdsForProgram(String activityCondQuery) {
		if(logger.isDebugEnabled()) logger.debug("the received query is=====>"+activityCondQuery);
		
		String contactIds = "";
		List<Long> contactIdList = jdbcTemplate.queryForList(activityCondQuery, Long.class);
		if(contactIdList.isEmpty()) {
			
			if(logger.isDebugEnabled()) logger.debug("no contacts found for this query"+activityCondQuery);
			return "";
		}
		
		for(Long id : contactIdList) {
			
			if(contactIds.length() > 0 ) contactIds += ",";
			contactIds += id;
		}
		return contactIds;
		
		
		
		
	}
	
	public CampaignSent findSentByEmailId( String emailId, Long crId) {
		
		try {
			String qry = " FROM CampaignSent WHERE emailId='"+emailId+"' AND campaignReport="+crId.longValue();
			
			List<CampaignSent> list = getHibernateTemplate().find(qry);
			
			if(list != null && list.size() > 0) {
				
				return list.get(0);
			}else return null;
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::::" , e);
			return null;
		}
		
		
		
	}
	
	
	
	
	public String getEmailIdBySentId(Long sentId) {
		List<String> list = jdbcTemplate.queryForList(" SELECT email_id from campaign_sent where sent_id=" + sentId,String.class);
		return list.get(0);
	}

}
