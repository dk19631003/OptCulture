package org.mq.captiway.scheduler.dao;

import java.util.Collection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.AutoSmsQueue;
import org.mq.captiway.scheduler.utility.Constants;
import org.springframework.jdbc.core.JdbcTemplate;

public class AutoSmsQueueDaoForDML extends AbstractSpringDaoForDML {
	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);

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
	
	public void saveByCollection(Collection<AutoSmsQueue> collection) {
    	super.saveOrUpdateAll(collection);
    }

	public void delete(AutoSmsQueue testSmsQueue) {
		super.delete(testSmsQueue);
	}

	/*public List<AutoSmsQueue> findByStatus(String status) {
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
	}*/
	


	public void updateclicksBasedOnSmsQueueId(Long autoSmsQueueSentId) {
			try {
				String updateQuery = "update AutoSmsQueue set clicks = clicks+1 where id = "+autoSmsQueueSentId+"";
				executeUpdate(updateQuery);
			}catch (Exception e) {
				logger.info("Exception while updating clicks Based OnSms QueueId "+e);
			}
		}
}


