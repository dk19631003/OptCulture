package org.mq.marketer.campaign.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.DomainStatus;
import org.mq.marketer.campaign.general.Constants;
import org.mq.optculture.utils.OCConstants;

public class PurgeDao  extends AbstractSpringDao {
	private static final Logger logger =  LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);

	
	public List<DomainStatus> getAllDomainswithStatus(){
		List<DomainStatus> domains = new ArrayList<DomainStatus>(0);
		try{
			String qry = "FROM DomainStatus";
			domains = executeQuery(qry);
		}catch (Exception e) {
			logger.info("Exception :: ",e);
		}
		return domains;
	}
}
