package org.mq.marketer.campaign.dao;

import java.util.Collection;

import org.mq.marketer.campaign.beans.ETLFileUploadLogs;
import org.springframework.jdbc.core.JdbcTemplate;

public class ETLFileUploadLogDaoForDML extends AbstractSpringDaoForDML {

	
	public ETLFileUploadLogDaoForDML() { }

	private JdbcTemplate jdbcTemplate;
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}


    public void saveOrUpdate(ETLFileUploadLogs fileUploadLogs) {
        super.saveOrUpdate(fileUploadLogs);
    }

    public void delete(ETLFileUploadLogs fileUploadLogs) {
        super.delete(fileUploadLogs);
    }
    
 
    public void saveByCollections(Collection<ETLFileUploadLogs> fileUploadLogsCollection) {
    	// TODO Auto-generated method stub
    	super.saveByCollection(fileUploadLogsCollection);
    }
    
    
     

	
	
}
