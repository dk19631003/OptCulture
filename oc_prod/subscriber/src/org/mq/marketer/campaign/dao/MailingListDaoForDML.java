package org.mq.marketer.campaign.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.mq.marketer.campaign.beans.MailingList;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.beans.UsersDomains;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.exception.BaseDAOException;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

@SuppressWarnings({ "unchecked", "serial" })
public class MailingListDaoForDML extends AbstractSpringDaoForDML {

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
    private JdbcTemplate jdbcTemplate;

    public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public MailingListDaoForDML() {}
    
    public void saveOrUpdate(MailingList mailingList) {
    	logger.info("saving mailinglist obj with name"+mailingList.getListName());
        super.saveOrUpdate(mailingList);
    }
    public void delete(MailingList mailingList) {
        super.delete(mailingList);
    }

    public void delete(String listIds){
    	//getHibernateTemplate().bulkUpdate("delete from MLCustomFields where mailingList in (" + listIds + ")");
    	//getHibernateTemplate().bulkUpdate("delete from MailingList where listId in (" + listIds + ")");
    	String qry = "DELETE FROM mailing_lists WHERE list_id in("+listIds+")";
    	getJdbcTemplate().update(qry);
    }
    
   /* public void delete(String listIds){
    	//getHibernateTemplate().bulkUpdate("delete from MLCustomFields where mailingList in (" + listIds + ")");
    	//getHibernateTemplate().bulkUpdate("delete from MailingList where listId in (" + listIds + ")");
    	String qry = "UPDATE mailing_lists set status ='Deleted' WHERE list_id in("+listIds+")";
    	getJdbcTemplate().update(qry);
    	}*/
    
public void deleteSharedAssociation(Long listId) {
		
		try {
			String sql = " DELETE FROM mlists_domains WHERE list_id="+listId.longValue();
			
			jdbcTemplate.update(sql);
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
		}
		
	}

public void deleteSharedEmailCampaign(Long listId) {
	
	try {
		String sql = " DELETE FROM mlists_campaigns WHERE list_id="+listId.longValue();
		
		jdbcTemplate.update(sql);
	} catch (DataAccessException e) {
		// TODO Auto-generated catch block
		logger.error("Exception ::" , e);
	}
		
}
public void deleteSharedSMSCampaign(Long listId) {
	
	try {
		String sql = " DELETE FROM mlists_sms_campaigns WHERE list_id="+listId.longValue();
		
		jdbcTemplate.update(sql);
	} catch (DataAccessException e) {
		// TODO Auto-generated catch block
		logger.error("Exception ::" , e);
	}
	
	
	
	
}
public void deleteSharedEventTrigger(Long listId) {
	
	try {
		String sql = " DELETE FROM mlists_trigger WHERE list_id="+listId.longValue();
		
		jdbcTemplate.update(sql);
	} catch (DataAccessException e) {
		// TODO Auto-generated catch block
		logger.error("Exception ::" , e);
	}
	
	
	
	
}

}

