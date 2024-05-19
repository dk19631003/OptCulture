package org.mq.marketer.campaign.dao;


import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.AutoEmailClicks;
import org.mq.marketer.campaign.general.Constants;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

public class AutoEmailClicksDao extends AbstractSpringDao implements Serializable {
	
private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
    public AutoEmailClicksDao() {}
    
    private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	
	public AutoEmailClicks getClickByUrl(Long eqId, String url){
		List<AutoEmailClicks> list = null;
		try{
			String query = " FROM AutoEmailClicks WHERE eqId="+eqId+" AND clickUrl='"+url+"'";
			list = getHibernateTemplate().find(query);
			if(list != null && list.size() > 0){
			return	list.get(0);
			}
		}catch (Exception e) {
			logger.error("Exception :: ",e);
		}
		return null;
	}
	
	public int getClicksCountByTempId(Long userId, Long tempId,String type, String key){
		
		try{
			String query ="SELECT COUNT(distinct click_url) FROM auto_email_clicks ac INNER JOIN  email_queue eq ON ac.eq_id=eq.id " +
					" WHERE user_id=" + userId +" AND type ='"+type+"' ";
			
					if(tempId ==null || tempId == 0){
						query += " AND cust_temp_id is NULL ";
					}else{
						query += " AND cust_temp_id="+tempId+" ";
					}
					
					if(key != null){
						query += " AND click_url like '%"+key+"%'";
					}
					
				 Long count =	jdbcTemplate.queryForLong(query);
				 return count.intValue();
	 }catch(EmptyResultDataAccessException e1){
		 logger.info("No Urls for this template");
		 
	 }catch(Exception e){
		 logger.info("Exception :: ",e);
	 }
	 return 0;
 }
	
	
	@SuppressWarnings("unchecked")
	public List<Object[]> getClicksByTempId(Long userId, Long tempId,String type, String key, int startIndex, int size) {
		List<Object[]> list = null;
		
		try{
			String query = "SELECT click_url as url, SUM(click_count) as totalClicks, COUNT(click_count) as uniqueClicks  FROM auto_email_clicks ac INNER JOIN  email_queue eq ON ac.eq_id=eq.id " +
					" WHERE user_id=" + userId +" AND type ='"+type+"' ";
					
					if(tempId ==null || tempId == 0){
						query += " AND cust_temp_id is NULL ";
					}else{
						query += " AND cust_temp_id="+tempId+" ";
					}
					
					if(key != null){
						query += " AND click_url like '%"+key+"%'";
					}
					
					query += " GROUP BY click_url LIMIT "+startIndex+" , "+size;
					
					list = jdbcTemplate.query(query, new RowMapper(){
						
						@Override
						public Object mapRow(ResultSet rs, int arg1)
								throws SQLException {
							Object obj [] = new Object[3];
							obj[0] = rs.getString("url");
							obj[1] = rs.getInt("uniqueClicks");
							obj[2] = rs.getInt("totalClicks");
							return obj;
						}
						
					});
		}catch (Exception e) {
			logger.error("Exception :: ",e);
		}
		return list;
	}

}
