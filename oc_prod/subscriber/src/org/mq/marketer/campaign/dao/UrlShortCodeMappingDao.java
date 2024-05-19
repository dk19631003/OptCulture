package org.mq.marketer.campaign.dao;

import java.util.List;

import org.mq.marketer.campaign.beans.UrlShortCodeMapping;
import org.mq.marketer.campaign.general.Constants;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class UrlShortCodeMappingDao extends AbstractSpringDao{

	public UrlShortCodeMappingDao() {}
	
	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	public UrlShortCodeMapping find(Long id) {
		return (UrlShortCodeMapping) super.find(UrlShortCodeMapping.class, id);
	}

	/*public void saveOrUpdate(UrlShortCodeMapping urlShortCodeMapping) {
		super.saveOrUpdate(urlShortCodeMapping);
	}

	public void delete(UrlShortCodeMapping urlShortCodeMapping) {
		super.delete(urlShortCodeMapping);
	}*/

	public List findAll() {
		return super.findAll(UrlShortCodeMapping.class);
	}
	
	public List<UrlShortCodeMapping> getAllUserCustomShortCodes(Long userId, String type) {
		
		String qry = "FROM UrlShortCodeMapping WHERE userId="+userId+ " AND urlType='"+type+"'";
		 
		List<UrlShortCodeMapping> shortCodeList = getHibernateTemplate().find(qry);
		
		
		return shortCodeList;
		
	}
	
public List<UrlShortCodeMapping> findBy(Long userId, String shortCodeStr) {
    	
    	List<UrlShortCodeMapping> list = executeQuery("FROM UrlShortCodeMapping WHERE userId="+userId.longValue()+" AND  shortCode IN(" + shortCodeStr + ")" );
    	if(list == null || list.size() <= 0) {
    		return null;
    	}
    	return list;
		
		
    	
    	
    }
	

}
