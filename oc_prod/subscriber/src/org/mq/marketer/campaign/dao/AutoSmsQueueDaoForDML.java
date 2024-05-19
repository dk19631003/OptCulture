package org.mq.marketer.campaign.dao;


import java.util.List;

import org.mq.marketer.campaign.beans.AutoSmsQueue;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;


public class AutoSmsQueueDaoForDML extends AbstractSpringDaoForDML {

	public AutoSmsQueueDaoForDML() {}
	
	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	/*public AutoSmsQueue find(Long id) {
		return (AutoSmsQueue) super.find(AutoSmsQueue.class, id);
	}*/

	public void saveOrUpdate(AutoSmsQueue testSmsQueue) {
		super.saveOrUpdate(testSmsQueue);
	}

	public void delete(AutoSmsQueue testSmsQueue) {
		super.delete(testSmsQueue);
	}


	/*public List<AutoSmsQueue> findByStatus(String status) {
		return getHibernateTemplate().find(" from AutoSmsQueue where status= '" + status + "'");
	}

	public List<AutoSmsQueue> findByType(Long userId,String type) {
		return getHibernateTemplate().find(" from AutoSmsQueue where type='" + type + "' AND user="+ userId + " ORDER BY sentDate DESC");
	}
	public AutoSmsQueue findById(long msgId) {
		AutoSmsQueue autoSmsQueue = null;
		try {
			List<AutoSmsQueue> list = getHibernateTemplate().find(" from AutoSmsQueue where id= " +msgId );
			if (list.size() > 0) {
				autoSmsQueue = (AutoSmsQueue) list.get(0);
			}
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
		}
		return autoSmsQueue;
	}*/
	
	public void updateStatus(String status,Long sentId) {	
		
		String updateQry = " UPDATE auto_sms_queue SET dlr_status='"+status+"' WHERE id="+sentId+"";				
		
		logger.debug(updateQry);		
		executeJdbcUpdateQuery(updateQry);		
	
	}
public void updateStatus(String status,String refId,String mobile) {	
		
    	//common method for Equence & Synapse
		//String updateQry = " UPDATE sms_campaign_sent SET status='"+status+"' WHERE sent_id="+sentId+"";
    	if(mobile.startsWith("91")&& mobile.length() == 12) {
			
			
			mobile = mobile.substring(2);
		}
    	String updateQry = " UPDATE auto_sms_queue SET dlr_status='"+status+"' WHERE message_id='"+refId+"' AND to_Mobile_No like '%"+mobile+"%'";
		
		logger.debug(updateQry);		
		executeJdbcUpdateQuery(updateQry);		
	
	}
}
