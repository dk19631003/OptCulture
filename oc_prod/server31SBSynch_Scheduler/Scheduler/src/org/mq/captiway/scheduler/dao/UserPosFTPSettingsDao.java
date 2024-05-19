package org.mq.captiway.scheduler.dao;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.UserPosFTPSettings;
import org.mq.captiway.scheduler.utility.Constants;
import org.springframework.jdbc.core.JdbcTemplate;

public class UserPosFTPSettingsDao extends AbstractSpringDao{
	
	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	
	 public UserPosFTPSettingsDao(){}

	    private JdbcTemplate jdbcTemplate;

		public JdbcTemplate getJdbcTemplate() {
			return jdbcTemplate;
		}

		public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
			this.jdbcTemplate = jdbcTemplate;
		}
		
	    public UserPosFTPSettings find(Long id){
	        return (UserPosFTPSettings) super.find(UserPosFTPSettings.class, id);
	    }

	    /*public void saveOrUpdate(UserPosFTPSettings userPosFTPSettings){
	        super.saveOrUpdate(userPosFTPSettings);
	    }

	    public void delete(UserPosFTPSettings userPosFTPSettings){
	        super.delete(userPosFTPSettings);
	    }*/
	    
	    public List<UserPosFTPSettings> findListByTimeDiff() {
	    	
	    	List<UserPosFTPSettings> posSettingList = null;
	    	String qry = "FROM UserPosFTPSettings A WHERE A.enabled = true AND  NOW() > TIMESTAMPADD (MINUTE, A.scheduledFreqInMintues,  A.lastFetchedTime)";
	    	logger.info("calling query is here == "+qry);
	    	return getHibernateTemplate().find (qry);
//	    	return getHibernateTemplate().find( "FROM UserPosFTPSettings WHERE enabled = true AND NOW() > lastFetchedTime + INTERVAL scheduledFreqInMintues MINUTE");
	    	
	    }
	    
	    
	   /*
	    * method for sending alert mail if sales file is not received in selected period
	    */
	    
	    public List<UserPosFTPSettings> getFtpSettingsforSalesDataAlertMail() {
	    	
	    	logger.debug("inside get Ftp Settings for Sales Data Alert Mail");
	    	
	    	List<UserPosFTPSettings> posSettingList = null;
	    	String qry = "FROM UserPosFTPSettings where checkAlert = true and fileType = 'Sales'";
	    	logger.debug("exit get Ftp Settings for Sales Data Alert Mail");
	    	return getHibernateTemplate().find (qry);
//	    	return getHibernateTemplate().find( "FROM UserPosFTPSettings WHERE enabled = true AND NOW() > lastFetchedTime + INTERVAL scheduledFreqInMintues MINUTE");
	    	
	    }

	    
	    /*public void saveFileName(long userId,String fileType, String fileName) {
	    	
	    	

	    	try {
				String qry  = "insert into pos_files_log(user_id,file_type,file_name,fetched_time) values("+userId+",'"+fileType+"','"+fileName+"', now())";

				if(logger.isDebugEnabled()) logger.debug("Query : " + qry);
				jdbcTemplate.execute(qry);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Exception ::::" , e);
			}
	    	
	    } //saveFileName
*/	    
}
