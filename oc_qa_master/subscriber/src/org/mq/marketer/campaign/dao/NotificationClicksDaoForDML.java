package org.mq.marketer.campaign.dao;

import java.io.Serializable;



import org.springframework.jdbc.core.JdbcTemplate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.Clicks;
import org.mq.marketer.campaign.general.Constants;


@SuppressWarnings({"unchecked","serial"})
public class NotificationClicksDaoForDML extends AbstractSpringDaoForDML implements Serializable {
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
    public NotificationClicksDaoForDML() {}
    
    private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	/*public Clicks find(Long id) {
        return (Clicks) super.find(Clicks.class, id);
    }
*/
    public void saveOrUpdate(Clicks clicks) {
        super.saveOrUpdate(clicks);
    }

    public void delete(Clicks clicks) {
        super.delete(clicks);
    }
}
