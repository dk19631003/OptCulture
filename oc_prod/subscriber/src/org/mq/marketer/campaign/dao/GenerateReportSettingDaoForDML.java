package org.mq.marketer.campaign.dao;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.GenerateReportSetting;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.general.Constants;
import org.springframework.jdbc.core.JdbcTemplate;

public class GenerateReportSettingDaoForDML extends AbstractSpringDaoForDML{

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
    public GenerateReportSettingDaoForDML(){}

   /* public Users find(Long id){
    	
    	return findByUserId(id);*/
    	
//        return (Users) super.find(Users.class, id);
  /*  }*/

    private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

    public void saveOrUpdate(GenerateReportSetting generateReportSetting){
        super.saveOrUpdate(generateReportSetting);
    }

}
