package org.mq.optculture.data.dao;

import java.io.Serializable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.LtySettingsActivityLogs;
import org.mq.marketer.campaign.dao.AbstractSpringDao;
import org.mq.marketer.campaign.general.Constants;

public class LtySettingsActivityLogsDao extends AbstractSpringDao implements Serializable{

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);

	public LtySettingsActivityLogsDao() {
	}

	/*public void saveOrUpdate(LtySettingsActivityLogs ltySettingsActivityLogs) {
		super.saveOrUpdate(ltySettingsActivityLogs);
	}*/

}
