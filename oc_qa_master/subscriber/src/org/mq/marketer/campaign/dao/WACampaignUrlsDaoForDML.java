package org.mq.marketer.campaign.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
//import org.mq.marketer.campaign.beans.SMSCampaignUrls;
import org.mq.marketer.campaign.beans.WACampaignUrls;
import org.mq.marketer.campaign.general.Constants;
import org.springframework.jdbc.core.JdbcTemplate;

public class WACampaignUrlsDaoForDML extends AbstractSpringDaoForDML {
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	

	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	
	/*public SMSCampaignUrls find(Long id) {
		return (SMSCampaignUrls) super.find(SMSCampaignUrls.class, id);
	}*/

	public void saveOrUpdate(WACampaignUrls waCampaignUrls) throws Exception {
		super.saveOrUpdate(waCampaignUrls);
	}

	public void delete(WACampaignUrls waCampaignUrls) {
		super.delete(waCampaignUrls);
	}
	
	
	/* public List<SMSCampaignUrls> getClickList(long crId) throws Exception{
	    	
    	List<SMSCampaignUrls> list;
		
			String queryString ;
			queryString = "FROM SMSCampaignUrls WHERE crId="+crId;

				
			list = executeQuery(queryString);
			
			return list;

			for (Object[] obj : list) {
				
				clickList.add(obj);
			}
			return clickList;
	 }*/
	
/*	 public Long  getClickCountByCrId(long crId) throws Exception{
	    	
		String queryString ;
		queryString = "SELECT COUNT(*) FROM SMSCampaignUrls WHERE crId="+crId ;

			
		return (Long)getHibernateTemplate().find(queryString).get(0);
		
			
	}*/
}
