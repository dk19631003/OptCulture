package org.mq.captiway.scheduler.dao;

import java.util.Collection;
import java.util.List;

import org.mq.captiway.scheduler.beans.AutoSmsQueue;
import org.mq.captiway.scheduler.beans.SMSCampaignSent;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

public class AutoSmsQueueDao extends AbstractSpringDao {

	public AutoSmsQueueDao() {}
	
	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public AutoSmsQueue find(Long id) {
		return (AutoSmsQueue) super.find(AutoSmsQueue.class, id);
	}

	/*public void saveOrUpdate(AutoSmsQueue testSmsQueue) {
		super.saveOrUpdate(testSmsQueue);
	}
	
	public void saveByCollection(Collection<AutoSmsQueue> collection) {
    	super.saveOrUpdateAll(collection);
    }*/

	/*public void delete(AutoSmsQueue testSmsQueue) {
		super.delete(testSmsQueue);
	}*/


	public List<AutoSmsQueue> findByStatus(String status) {
		return getHibernateTemplate().find(" from AutoSmsQueue where status= '" + status + "' ORDER BY userId DESC");
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
	}
	
	public List<AutoSmsQueue> findByMrIdAndMobile(String id) {

		  
		//String query = "FROM AutoSmsQueue where messageId='"+mrId+"' AND toMobileNo like '%"+mobile+"%'";
	  String query = "FROM AutoSmsQueue where id in("+id+") ";
		try{
			List<AutoSmsQueue> retAutoSMSQueueList =  getHibernateTemplate().find(query);
			if(retAutoSMSQueueList != null && retAutoSMSQueueList.size() > 0) {
				
				return retAutoSMSQueueList;
			}
			}catch(Exception e){
			}
		
		return null;
	}
public List<AutoSmsQueue> findByMsgIds(String msgIds) {
		
		List<AutoSmsQueue> retSmsCampaignSentList = getHibernateTemplate().find("FROM AutoSmsQueue " +
				"WHERE messageId IN("+msgIds+") ");

		if(retSmsCampaignSentList != null && retSmsCampaignSentList.size() > 0) {
		
			return retSmsCampaignSentList;
		}

		return null;
		
		
	}
	
}


