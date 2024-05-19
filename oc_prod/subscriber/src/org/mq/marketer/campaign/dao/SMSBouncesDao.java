package org.mq.marketer.campaign.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.mq.marketer.campaign.beans.SMSBounces;
import org.mq.marketer.campaign.general.SMSStatusCodes;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

public class SMSBouncesDao extends AbstractSpringDao{

	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	 public List getCategoryPercentageByCrId(Long userId, Long campRepId) {
	    	try {
	    		List<Object[]> catList = new ArrayList<Object[]>();
	    		String queryStr = "SELECT category, COUNT(b.bounce_id)" +
	    				" FROM sms_campaign_report cr , sms_bounces b " +
	    				"WHERE cr.sms_cr_id=b.cr_id AND cr.sms_cr_id ="+campRepId+
	    				" AND cr.user_id = " + userId + " GROUP BY cr.sms_cr_id, b.category";
	    		logger.info("Query :" + queryStr);
	    		catList = jdbcTemplate.query(queryStr,	new RowMapper(){
	    			Object[] obj;
					@Override
					public Object mapRow(ResultSet rs, int rowNum)
							throws SQLException {
						obj = new Object[2];
						obj[0] = rs.getString(1);
						obj[1] = rs.getLong(2);
						
						
						return obj;
					}
	    			
	    		});
	    		return catList;
	    		/*String queryStr = "SELECT b.category, ROUND( (COUNT(b.bounceId) /cr.sent) * 100 , 2)," +
	    		" cr.crId FROM CampaignReport cr , Bounces b " +
	    		"WHERE cr.crId = b.crId " +
	    		"AND cr.user.userId = " + userId + " GROUP BY cr.crId, b.category";
	    		//return executeQuery(queryStr);
	    		*/
	    	} catch (Exception e) {
	    		logger.error("** Error : " + e.getMessage() + " **");
	    		return null;
			}
	    }
	
	 
	 public int getCount(Long smsCampRepId, String status) {
	    	
	    	String qry = "SELECT count(bounceId) FROM SMSBounces where crId="+smsCampRepId+" AND mobile IS NOT NULL AND category IN("+status+")";
	    	logger.debug("QueryQuery ..."+qry);
	    
			int count =	getHibernateTemplate().find(qry).get(0) != null ?  ((Long)getHibernateTemplate().find(qry).get(0)).intValue() : 0;
			return count;
	    	
	    }
	 
	 
	 public int getAllCountByRepId(Long smsCampRepId) {
		 
		 String qry = "SELECT count(bounceId) FROM SMSBounces where crId="+smsCampRepId.longValue()+" AND mobile IS NOT NULL ";
			int count = ((Long)getHibernateTemplate().find(qry).get(0)).intValue();	
			return count;
		 
	 }
	
	 public List<String> getBouncesByCat(Long smsCampRepId, String status, int startFrom, int count) {
			String queryStr = "SELECT mobile  FROM SMSBounces WHERE " +
					"crId="+smsCampRepId+" AND mobile IS NOT NULL  AND category IN("+status+")";
			
			logger.info("query :" +queryStr);
			
			List<String> list = (List<String>)executeQuery(queryStr, startFrom, count);
			
			return list;
		}
	 
	 public List<SMSBounces> getBouncesAndReasonByCat(Long smsCampRepId, String status, int startFrom, int count) {
			String queryStr = "FROM SMSBounces WHERE " +
					"crId="+smsCampRepId+" AND mobile IS NOT NULL  AND category IN("+status+")";
			
			logger.info("query :" +queryStr);
			
			List<SMSBounces> list = (List<SMSBounces>)executeQuery(queryStr, startFrom, count);
			
			return list;
		}
	 
	 public List<SMSBounces> getBouncesCatOthers(Long smsCampRepId, String status, int startFrom, int count) {
			String queryStr = "FROM SMSBounces WHERE " +
					"crId="+smsCampRepId+" AND mobile IS NOT NULL  AND category IN("+status+")";
			
			logger.info("query :" +queryStr);
			
			List<SMSBounces> list = (List<SMSBounces>)executeQuery(queryStr, startFrom, count);
			
			return list;
		}
	 
	 public List<Object[]> getAllBounceCategories(Long long1) {
		 
		 String queryStr = "select category,count(sent_id) from sms_bounces where cr_id="+ long1 +" group by category order by count(sent_id) desc";
		 logger.debug("QueryQuery ..."+queryStr);
		 List<Object[]> catList = new ArrayList<Object[]>();
		 catList = jdbcTemplate.query(queryStr,	new RowMapper(){
 			Object[] obj;
				@Override
				public Object mapRow(ResultSet rs, int rowNum)
						throws SQLException {
					obj = new Object[2];
					obj[0] = rs.getString(1);
					obj[1] = rs.getLong(2);
					
					
					return obj;
				}
 			
 		});
 		return catList;
		 
	 }
	 
public SMSBounces findBymobile(Long crId, String mobile) {
		 
		 String qry = "FROM SMSBounces WHERE crId="+crId+" AND mobile = '"+mobile+"'";
		 
		 List<SMSBounces> list = getHibernateTemplate().find(qry);
		 
		 if(list != null && list.size() > 0) {
			 
			 return list.get(0);
		 }
		 else return null;
	 }

public List<Object[]> getAllBounceCategories(String crIdList) {

	 
	String queryStr = "select category,count(sent_id) from sms_bounces where cr_id IN(" + crIdList + ") group by category order by count(sent_id) desc";	 
	logger.debug("QueryQuery ..."+queryStr);
	 List<Object[]> catList = new ArrayList<Object[]>();
	 catList = jdbcTemplate.query(queryStr,	new RowMapper(){
		Object[] obj;
			@Override
			public Object mapRow(ResultSet rs, int rowNum)
					throws SQLException {
				obj = new Object[2];
				obj[0] = rs.getString(1);
				obj[1] = rs.getLong(2);
				
				
				return obj;
			}
		
	});
	return catList;
}

/*public List<Object[]> getAllBouncesCategories(String crIdList) {

	 
	 String queryStr = "select category,count(sent_id) from sms_bounces where cr_id IN ("+ crIdList +") group by category order by count(sent_id) desc";
	 logger.debug("QueryQuery ..."+queryStr);
	 List<Object[]> catList = new ArrayList<Object[]>();
	 catList = jdbcTemplate.query(queryStr,	new RowMapper(){
		Object[] obj;
			@Override
			public Object mapRow(ResultSet rs, int rowNum)
					throws SQLException {
				obj = new Object[2];
				obj[0] = rs.getString(1);
				obj[1] = rs.getLong(2);
				
				
				return obj;
			}
		
	});
	return catList;
	 

}*/
}
