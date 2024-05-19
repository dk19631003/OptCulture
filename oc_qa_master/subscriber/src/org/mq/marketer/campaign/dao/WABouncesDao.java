package org.mq.marketer.campaign.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.mq.marketer.campaign.beans.WABounces;
import org.mq.marketer.campaign.general.WAStatusCodes;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

public class WABouncesDao extends AbstractSpringDao{

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
	    				" FROM wa_campaign_report cr , wa_bounces b " +
	    				"WHERE cr.wa_cr_id=b.cr_id AND cr.wa_cr_id ="+campRepId+
	    				" AND cr.user_id = " + userId + " GROUP BY cr.wa_cr_id, b.category";
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
	    		
	    	} catch (Exception e) {
	    		logger.error("** Error : " + e.getMessage() + " **");
	    		return null;
			}
	    }
	
	 
	 public int getCount(Long waCampRepId, String status) {
	    	
	    	String qry = "SELECT count(bounceId) FROM WABounces where crId="+waCampRepId+" AND mobile IS NOT NULL AND category IN("+status+")";
	    	logger.debug("QueryQuery ..."+qry);
	    
			int count =	getHibernateTemplate().find(qry).get(0) != null ?  ((Long)getHibernateTemplate().find(qry).get(0)).intValue() : 0;
			return count;
	    	
	    }
	 
	 
	 public int getAllCountByRepId(Long waCampRepId) {
		 
		 String qry = "SELECT count(bounceId) FROM WABounces where crId="+waCampRepId.longValue()+" AND mobile IS NOT NULL ";
			int count = ((Long)getHibernateTemplate().find(qry).get(0)).intValue();	
			return count;
		 
	 }
	
	 public List<String> getBouncesByCat(Long waCampRepId, String status, int startFrom, int count) {
			String queryStr = "SELECT mobile  FROM WABounces WHERE " +
					"crId="+waCampRepId+" AND mobile IS NOT NULL  AND category IN("+status+")";
			
			logger.info("query :" +queryStr);
			
			List<String> list = (List<String>)executeQuery(queryStr, startFrom, count);
			
			return list;
		}
	 
	 public List<WABounces> getBouncesAndReasonByCat(Long waCampRepId, String status, int startFrom, int count) {
			String queryStr = "FROM WABounces WHERE " +
					"crId="+waCampRepId+" AND mobile IS NOT NULL  AND category IN("+status+")";
			
			logger.info("query :" +queryStr);
			
			List<WABounces> list = (List<WABounces>)executeQuery(queryStr, startFrom, count);
			
			return list;
		}
	 
	 public List<WABounces> getBouncesCatOthers(Long waCampRepId, String status, int startFrom, int count) {
			String queryStr = "FROM WABounces WHERE " +
					"crId="+waCampRepId+" AND mobile IS NOT NULL  AND category IN("+status+")";
			
			logger.info("query :" +queryStr);
			
			List<WABounces> list = (List<WABounces>)executeQuery(queryStr, startFrom, count);
			
			return list;
		}
	 
	 public List<Object[]> getAllBounceCategories(Long long1) {
		 
		 String queryStr = "select category,count(sent_id) from wa_bounces where cr_id="+ long1 +" group by category order by count(sent_id) desc";
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
	 
public WABounces findBymobile(Long crId, String mobile) {
		 
		 String qry = "FROM WABounces WHERE crId="+crId+" AND mobile LIKE '%"+mobile+"'";
		 
		 List<WABounces> list = getHibernateTemplate().find(qry);
		 
		 if(list != null && list.size() > 0) {
			 
			 return list.get(0);
		 }
		 else return null;
	 }

public List<Object[]> getAllBounceCategories(String crIdList) {

	 
	String queryStr = "select category,count(sent_id) from wa_bounces where cr_id IN(" + crIdList + ") group by category order by count(sent_id) desc";	 
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


}
