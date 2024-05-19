package org.mq.marketer.campaign.dao;

import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.mq.marketer.campaign.beans.DigitalReceiptsJSON;
import org.mq.marketer.campaign.beans.UserAgentJSON;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

public class UserAgentJSONDao extends AbstractSpringDao {
	
	
	private JdbcTemplate jdbcTemplate;
	
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public UserAgentJSONDao(){}
	
	public UserAgentJSON find(Long id){
		return (UserAgentJSON)super.find(UserAgentJSON.class, id);
	}
	
	/*public void saveOrUpdate(UserAgentJSON uaJSON){
		super.saveOrUpdate(uaJSON);
	}
	
	public void delete(UserAgentJSON uaJSON){
		super.delete(uaJSON);
	}*/
	

	public UserAgentJSON findByUAStr(String uaStr) {
		try {
			String jsonStr=null;
			
			String query = " FROM UserAgentJSON WHERE userAgentStr = '"+StringEscapeUtils.escapeSql(uaStr)+"' ";
			
			List<UserAgentJSON> list =  getHibernateTemplate().find(query);
			if(list!=null && list.size()>0) return list.get(0);
			else return null;
		} catch (DataAccessException e) {
			return null;
		}
		
	}

}
