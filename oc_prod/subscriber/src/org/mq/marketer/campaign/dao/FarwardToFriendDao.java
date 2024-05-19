package org.mq.marketer.campaign.dao;

import java.util.Collection;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import org.mq.marketer.campaign.beans.DRSent;
import org.mq.marketer.campaign.beans.FarwardToFriend;
import org.mq.marketer.campaign.general.Constants;
import org.springframework.jdbc.core.JdbcTemplate;

public class FarwardToFriendDao extends AbstractSpringDao {
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);

	private SessionFactory sessionFactory;

	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}


	public FarwardToFriend find(Long id) {
		return (FarwardToFriend) super.find(FarwardToFriend.class, id);
	}

	/*public void saveOrUpdate(FarwardToFriend farwardToFriend) {
		super.saveOrUpdate(farwardToFriend);
	}

	public void delete(FarwardToFriend farwardToFriend) {
		super.delete(farwardToFriend);
	}*/
	
	public Long findUniqEmailCount(Long userId ,long crId){
		String qry="SELECT COUNT(DISTINCT toEmailId) FROM FarwardToFriend WHERE userId ="+userId.longValue()+"  AND crId="+crId+"  ";
		
		Long count =   ((Long)getHibernateTemplate().find(qry).get(0)).longValue();
		
		return count;
		
		
		
		
	}
	
	 public List<Object[]> getForwardEmailsByCrId(Long crId,int firstResult,int maxResults) {
			try {
				String query = "select toEmailId,COUNT(toEmailId) from FarwardToFriend where crId =" + crId.longValue() +" GROUP BY toEmailId ";

				List<Object[]> list =  executeQuery(query, firstResult, maxResults);
				return list;
			}catch (Exception e) {
				logger.error("** Error : " + e.getMessage() + " **");
				return null;
			} 
		}
	 
	 public int findByUniqEmailCount(long crId){
			String qry="SELECT COUNT(DISTINCT toEmailId) FROM FarwardToFriend WHERE  crId="+crId+"  ";
			
			int count =  ((Long)getHibernateTemplate().find(qry).get(0)).intValue();
			
			return count;
			
			
			
			
		}
	
	
}
