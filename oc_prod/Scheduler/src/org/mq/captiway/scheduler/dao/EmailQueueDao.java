package org.mq.captiway.scheduler.dao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.Campaigns;
import org.mq.captiway.scheduler.beans.EmailQueue;
import org.mq.captiway.scheduler.beans.Users;
import org.mq.captiway.scheduler.utility.Constants;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

@SuppressWarnings({ "unchecked", "serial","unused"})
public class EmailQueueDao extends AbstractSpringDao {
	
	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
    
	
	private JdbcTemplate jdbcTemplate;
	private EmailQueueDaoForDML emailQueueDaoForDML;
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public EmailQueueDaoForDML getEmailQueueDaoForDML() {
		return emailQueueDaoForDML;
	}

	public void setEmailQueueDaoForDML(EmailQueueDaoForDML emailQueueDaoForDML) {
		this.emailQueueDaoForDML = emailQueueDaoForDML;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
    public EmailQueueDao() {}
	
    public EmailQueue find(Long id) {
        return (EmailQueue) super.find(EmailQueue.class, id);
    }

   /* public void saveOrUpdate(EmailQueue testEmailQueue) {
        super.saveOrUpdate(testEmailQueue);
    }

    public void saveByCollection(Collection<EmailQueue> collection) {
    	super.saveOrUpdateAll(collection);
    }
    */
   /* public void delete(EmailQueue testEmailQueue) {
        super.delete(testEmailQueue);
    }*/

  /*  public void deleteByCollection(Collection emailQueueCollection){
    	getHibernateTemplate().deleteAll(emailQueueCollection);
    }*/
    
    public List<EmailQueue> findAll() {
        return super.findAll(EmailQueue.class);
    }

    public List<EmailQueue> findByStatus(String status){
    	return getHibernateTemplate().find(" from EmailQueue where status= '" + status + "'");
    }

    public List<EmailQueue> findByTypeAndStatus(String type,String status) {
    	
    	/*if(type.equals(Constants.EQ_STATUS_ALL))
    		return getHibernateTemplate().find(" FROM EmailQueue WHERE status= '" + status + "' " +
    				"AND type NOT IN ('"+ Constants.EQ_TYPE_SENDNOW +"')");
    	else
    		return getHibernateTemplate().find(" FROM EmailQueue WHERE status= '" + status + "' " +
    				"AND type IN ('"+ type +"')");*/
    	return getHibernateTemplate().find(" FROM EmailQueue WHERE status= '" + status + "' " 	);
    	
    }

    
    public List<EmailQueue> findByTypeAndStatus(String type, Long userID) {
    	List<EmailQueue> emailQueueList = null;
    	
    	if(type.equals(Constants.EQ_STATUS_ALL)){
    		
    		emailQueueList =  getHibernateTemplate().find(" FROM EmailQueue WHERE user="+userID.longValue()+" AND "
    				+ " status In ('"+Constants.EQ_STATUS_SENT+"','"+Constants.EQ_STATUS_ACTIVE+"') " +
    				" AND type NOT IN ('"+ Constants.EQ_TYPE_SENDNOW +"')  AND DATE(sentDate)=DATE(NOW()) ORDER BY sentDate DESC");
    	}
    	else {
    		emailQueueList = getHibernateTemplate().find("FROM EmailQueue WHERE user="+userID.longValue()+" AND "
    				+ " type IN ('"+ type +"')  " +
    				"AND  DATE(sentDate)=DATE(NOW()) AND status In ('"+Constants.EQ_STATUS_SENT+"','"+Constants.EQ_STATUS_ACTIVE+"') ORDER BY sentDate DESC");
    	}
    	
    	
    	if(emailQueueList != null && emailQueueList.size() > 0) return emailQueueList;
    	
    	return null;
    }
    
    /*public List<Campaigns> getCampaignsByType(String type){
    	List<EmailQueue> emailQueueList = getHibernateTemplate().find("from EmailQueue where type = '" + type + "' and status='Active'");
    	List<Campaigns> campaignList = new ArrayList();
    	Date dt = new Date();
    	for(EmailQueue eq : emailQueueList){
    		campaignList.add(eq.getCampaign());
    		try {
				eq.setStatus("sent");
				eq.setSentDate(dt);
				emailQueueDaoForDML.saveOrUpdate(eq);
			} catch (DataAccessException e) {
				logger.error("Unable to update the EmailQueue status, so deleting :" + e);
				try {
					emailQueueDaoForDML.delete(eq);
				} catch (Exception e1) {
					logger.error("Unable to delete the EmailQueue status :" + e1);
				}
			}
    	}
    	return campaignList;
    }*/
    
    
    
    public EmailQueue findById(Long id) {
    	
    	
    	 List<EmailQueue>  list = getHibernateTemplate().find("FROM EmailQueue WHERE id="+id);
    	 
    	 if(list != null && list.size() > 0) {
    		 return list.get(0);
    	 }
    	 else {
    		 return null;
    	 }
    	
    }
    
    /*public Date getLastSentDate(String type, Long userId) {
    	
    	try {
			logger.debug("inside get last mail sent ");
			String qry = "SELECT MAX(sentDate) FROM EmailQueue WHERE type = '" + type + "' AND status IN('Sent','Active') AND user = " + userId ;
			logger.info("--query for getLastSent is---"+qry);
			return (Date) executeQuery(qry).get(0);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("exception ", e);
			e.printStackTrace();
			return null;
		}
    	
   }*/
    
    
    public Date getLastSentDate(String type, Long userId) {
        
        try {
      logger.debug("inside get last mail sent ");
      String qry = "SELECT MAX(sentDate) FROM EmailQueue WHERE user = " + userId +" AND type = '" + type + "' AND status IN('Sent','Active')" ;
      List list = executeQuery(qry);
      if(list == null || list.size() == 0) return null;
      return (Date) executeQuery(qry).get(0);
     } catch (Exception e) {
      // TODO Auto-generated catch block
      logger.error("exception ", e);
      return null;
     }
        
     }
    
    public boolean isMailSentToday(String type, Long userId, int timezoneDiffrenceMinutesInt) {
    	
    	try {
			logger.debug("inside is mail sent today");
			/*String qry = "FROM EmailQueue WHERE DATE(sentDate) = Date(now()) and  type = '" + type + "' AND status IN('Sent','Active') AND user = " + userId ;
			
			List<EmailQueue> list = getHibernateTemplate().find(qry);
			if(list != null && list.size() > 0) {
	    		 return true;
	    	 }
	    	 else {
	    		 return false;
	    	 }*/
			String sent_date = "sent_date + INTERVAL "+timezoneDiffrenceMinutesInt+" MINUTE";
			String manipulatedServerTime = "now() + INTERVAL "+timezoneDiffrenceMinutesInt+" MINUTE";
			
			long user_id = userId;
			String qry = "select * FROM email_queue WHERE user_id = " + user_id +" AND type = '" + type + "' AND DATE("+sent_date+") = Date("+manipulatedServerTime+") and    status IN('Sent','Active') "  ;
			
			logger.info("qry >>>>>>>>>>>>>> "+qry);
			List<Map<String, Object>> list = jdbcTemplate.queryForList(qry);
			
			if(list != null && list.size() > 0) {
	    		 return true;
	    	 }
	    	 else {
	    		 return false;
	    	 }
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("exception ", e);
			e.printStackTrace();
			return true;
		}
    	
   }

	/*public int updateDeliveryStatus(long id, String delStatus) {
		try{
			String query = "UPDATE EmailQueue SET deliveryStatus='"+delStatus+"' WHERE id="+id;
		return executeUpdate(query);
		}catch (Exception e) {
			logger.error("Exception :: ",e);
		}
		return 0;
	}*/
}

