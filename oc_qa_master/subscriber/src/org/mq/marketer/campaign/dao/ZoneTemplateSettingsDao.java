package org.mq.marketer.campaign.dao;

import java.util.List;

import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.beans.ZoneTemplateSettings;
import org.mq.marketer.campaign.general.Constants;
import org.springframework.jdbc.core.JdbcTemplate;

@SuppressWarnings({ "unchecked", "serial","unused"})
public class ZoneTemplateSettingsDao extends AbstractSpringDao{


	JdbcTemplate jdbcTemplate;

	public ZoneTemplateSettingsDao() {}

	public ZoneTemplateSettings find(Long id) {
		return (ZoneTemplateSettings) super.find(ZoneTemplateSettings.class, id);
	}

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public List findAll() {
		return super.findAll(ZoneTemplateSettings.class);
	}
	
	public ZoneTemplateSettings findByZoneId(Long userId, Long zoneId, String autoCommType) {
    	
    	try {
			String qry = "FROM ZoneTemplateSettings WHERE userId="+userId+" AND zoneId="+zoneId+" AND autoCommType='"+autoCommType+"'";
			//return (ZoneTemplateSettings)getHibernateTemplate().find(qry).get(0);
			List<ZoneTemplateSettings> tempList = getHibernateTemplate().find(qry);
			if(tempList!= null && tempList.size()>0)
				return tempList.get(0);
			else 
				return null;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
		}
    	return null;
    	
    }



}

