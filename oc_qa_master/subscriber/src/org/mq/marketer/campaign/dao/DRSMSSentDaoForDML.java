
package org.mq.marketer.campaign.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import org.mq.marketer.campaign.beans.DRSMSSent;
import org.mq.marketer.campaign.general.Constants;
import org.mq.optculture.exception.BaseDAOException;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

public class DRSMSSentDaoForDML extends AbstractSpringDaoForDML {
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);

	private SessionFactory sessionFactory;

	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}


	/*public DRSent find(Long id) {
		return (DRSent) super.find(DRSent.class, id);																																																																																																																																																																																																																																																						
	}*/

	public void saveOrUpdate(DRSMSSent drSmsSent) {
		super.saveOrUpdate(drSmsSent);
	}

	public void delete(DRSMSSent drSmsSent) {
		super.delete(drSmsSent);
	}
		/**
	/**
	 * update click count whenever user clicks on link in DR
	 * @param sentId
	 * @return
	 */
	public int updateClickCount(Long sentId) {
		try {
			String qry=" UPDATE DRSMSSent SET clicks = (clicks+1) WHERE id = "+ sentId.longValue();
					
			return executeUpdate(qry);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
			return 0;
		}
	
	}//updateClickCount()

	
	public synchronized void updateSentCount(Long existingObjId) throws BaseDAOException{
		
		String qry = "UPDATE dr_sms_sent set sent_count=(sent_count+1) WHERE id="+existingObjId.longValue();
		
		executeJdbcUpdateQuery(qry);
		
	}
	
	public int updateOpenCount(Long sentId) {
		try {
			String qry=" UPDATE DRSMSSent SET opens = (opens+1) WHERE id = "+ sentId.longValue();
	
			return executeUpdate(qry);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
			return 0;
		}
	
	}//updateOpenCount
	
	public int updateDeliveryStatus(Long sentId) {
		try {
			String qry=" UPDATE DRSMSSent SET status = '"+Constants.SMS_STATUS_DELIVERED+"' WHERE id = "+ sentId.longValue();
					
			return executeUpdate(qry);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
			return 0;
		}
		
	}//updateOpenCount
	
	public void updateStatus(String status,Long sentId) {	
		
		String updateQry = " UPDATE dr_sms_sent SET status='"+status+"' WHERE id="+sentId+"";				
		
		logger.debug(updateQry);		
		executeJdbcUpdateQuery(updateQry);		
	
	}
	
	
	 /*public void setNewTemplateName(long userId, String newTemplateName, String selectedTemplate) throws Exception {
		 
		 String newTempName="MY_TEMPLATE:"+newTemplateName;
		 newTempName = StringEscapeUtils.escapeSql(newTempName);
		  selectedTemplate = StringEscapeUtils.escapeSql(selectedTemplate);

		 
				 String query = "UPDATE DRSent SET templateName='"+newTempName+"' WHERE templateName='"+selectedTemplate+"' AND userId="+userId;
				 logger.info("qry in de sent::::"+query);
				//
			int count= executeUpdate(query);
				 //getHibernateTemplate().bulkUpdate(query);
				 
			 }//setNewTemplateName()
	
*/	
}
