package org.mq.marketer.campaign.dao;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.ETLFileUploadLogs;
import org.mq.marketer.campaign.general.Constants;
import org.springframework.jdbc.core.JdbcTemplate;

public class ETLFileUploadLogDao extends AbstractSpringDao {
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	public ETLFileUploadLogDao() { }

	private JdbcTemplate jdbcTemplate;
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

    public ETLFileUploadLogs find(Long id) {
        return (ETLFileUploadLogs) super.find(ETLFileUploadLogs.class, id);
    }
    
    public List<ETLFileUploadLogs> findAllById(Long userId) {
    	return getHibernateTemplate().find("FROM ETLFileUploadLogs where userId="+ userId + " ORDER BY uploadTime DESC");
    }
    
    public List<ETLFileUploadLogs> findAllByIdAndSize(Long userId, int startIndex ,int size) {
    	return getHibernateTemplate().find("FROM ETLFileUploadLogs where userId="+ userId + " ORDER BY uploadTime DESC LIMIT "+ startIndex + "," + size);
    }
    
    public List<ETLFileUploadLogs> findAllByIdWithDateRange(Long userId,String startDate,String endDate) {
    	String strQuery = "FROM ETLFileUploadLogs where userId="+ userId + " "
    			+ "and uploadTime BETWEEN '"+startDate+"' AND '"+endDate+"'   ORDER BY uploadTime DESC";
    	logger.debug(strQuery);
    	return getHibernateTemplate().find(strQuery);
    }

  
}
