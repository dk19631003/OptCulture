package org.mq.marketer.campaign.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import org.mq.marketer.campaign.beans.WACampaignUrls;
import org.mq.marketer.campaign.general.Constants;
import org.springframework.jdbc.core.JdbcTemplate;

public class WACampaignUrlsDao extends AbstractSpringDao {
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	

	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	
	public WACampaignUrls find(Long id) {
		return (WACampaignUrls) super.find(WACampaignUrls.class, id);
	}

	/*public void saveOrUpdate(SMSCampaignUrls smsCampaignUrls) throws Exception {
		super.saveOrUpdate(smsCampaignUrls);
	}

	public void delete(SMSCampaignUrls smsCampaignUrls) {
		super.delete(smsCampaignUrls);
	}*/
	
	
	 public List<WACampaignUrls> getClickList(long crId) throws Exception{
	    	
    	List<WACampaignUrls> list;
		
			String queryString ;
			queryString = "FROM WACampaignUrls WHERE crId="+crId;

				
			list = executeQuery(queryString);
			
			return list;

			/*for (Object[] obj : list) {
				
				clickList.add(obj);
			}
			return clickList;*/
	 }
	
/*	 public Long  getClickCountByCrId(long crId) throws Exception{
	    	
		String queryString ;
		queryString = "SELECT COUNT(*) FROM SMSCampaignUrls WHERE crId="+crId ;

			
		return (Long)getHibernateTemplate().find(queryString).get(0);
		
			
	}*/
}
