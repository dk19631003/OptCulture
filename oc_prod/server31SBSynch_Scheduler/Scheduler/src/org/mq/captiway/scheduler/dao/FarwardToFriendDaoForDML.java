package org.mq.captiway.scheduler.dao;

import java.util.Collection;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import org.mq.captiway.scheduler.beans.CampaignSent;
import org.mq.captiway.scheduler.beans.FarwardToFriend;
import org.mq.captiway.scheduler.utility.Constants;
import org.springframework.jdbc.core.JdbcTemplate;


public class FarwardToFriendDaoForDML extends AbstractSpringDaoForDML {

	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	
	private SessionFactory sessionFactory;

	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}


	/*public FarwardToFriend find(Long id) {
		return (FarwardToFriend) super.find(FarwardToFriend.class, id);
	}
*/
	public void saveOrUpdate(FarwardToFriend farwardToFriend) {
		super.saveOrUpdate(farwardToFriend);
	}

	public void delete(FarwardToFriend farwardToFriend) {
		super.delete(farwardToFriend);
	}
	/*public List<FarwardToFriend> findByStatus(String status){
		
		return getHibernateTemplate().find("FROM FarwardToFriend where status='"+status+"' ORDER BY sentId DESC");
		
	}
	*/
	public void saveByCollection(Collection<FarwardToFriend> forwardList) {
		super.saveOrUpdateAll(forwardList);
	}
	
}
