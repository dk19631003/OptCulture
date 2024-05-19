package org.mq.optculture.data.dao;

import java.io.Serializable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.LtySettingsActivityLogs;
import org.mq.marketer.campaign.dao.AbstractSpringDao;
import org.mq.marketer.campaign.dao.AbstractSpringDaoForDML;
import org.mq.marketer.campaign.general.Constants;

public class LtySettingsActivityLogsDaoForDML extends AbstractSpringDaoForDML implements Serializable{

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);

	public LtySettingsActivityLogsDaoForDML() {
	}

	public void saveOrUpdate(LtySettingsActivityLogs ltySettingsActivityLogs) {
		super.saveOrUpdate(ltySettingsActivityLogs);
	}

}
