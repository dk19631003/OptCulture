package org.mq.marketer.campaign.dao;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.SocialAccountPageSettings;
import org.mq.marketer.campaign.general.Constants;


public class SocialAccountPageSettingsDaoForDML extends AbstractSpringDaoForDML {
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
 
	public SocialAccountPageSettingsDaoForDML() {
		// TODO Auto-generated constructor stub
	}
	
	 /*public SocialAccountPageSettings find(Long id) {
	        return (SocialAccountPageSettings) super.find(SocialAccountPageSettings.class, id);
	    }*/

	    public void saveOrUpdate(SocialAccountPageSettings socialAccountPageSettings) {
	        super.saveOrUpdate(socialAccountPageSettings);
	    }

	    public void delete(SocialAccountPageSettings socialAccountPageSettings) {
	        super.delete(socialAccountPageSettings);
	    }

/*	    public List findAll() {
	        return super.findAll(SocialAccountPageSettings.class);
	    }
	    
	    public List<SocialAccountPageSettings> findAllByUserName(String userId) {
	    		List<SocialAccountPageSettings> list = getHibernateTemplate().find("FROM SocialAccountPageSettings WHERE userId='"+ userId +"'");
	    		return list;
	    }
	    
	    public SocialAccountPageSettings findByDetails(String userName,String profilePageName, String profPageId) {
	    	
	    	logger.info("FROM SocialAccountPageSettings WHERE userId='"+ userName +
	    			"' AND profilePageName='"+ profilePageName +"' AND profilePageId='"+ profPageId +"'");
	    	
	    	List<SocialAccountPageSettings> list = getHibernateTemplate().find("FROM SocialAccountPageSettings WHERE userId='"+ userName +
	    			"' AND profilePageName='"+ profilePageName +"' AND profilePageId='"+ profPageId +"'");
	    	
	    	if(list != null && list.size() > 0) {
	    		return list.get(0);
	    	}
	    	
	    	return null;
	    }
	    
	    
	     * removes providers all pages ..
	     
	    public List<SocialAccountPageSettings> findAllPagesByProviderType(String userId,String profilePageType) {
	    	
	    	List<SocialAccountPageSettings> list =  getHibernateTemplate().find("FROM SocialAccountPageSettings WHERE userId='"+ userId +
	    			"' AND profilePageType='"+ profilePageType + "_MAIN' OR profilePageType='"+ profilePageType + "_PAGE' " );
	    	
	    	return list;
	    }*/
	
}
