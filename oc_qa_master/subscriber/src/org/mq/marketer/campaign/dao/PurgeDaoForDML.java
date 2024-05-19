package org.mq.marketer.campaign.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.DomainStatus;
import org.mq.marketer.campaign.general.Constants;
import org.mq.optculture.utils.OCConstants;

public class PurgeDaoForDML  extends AbstractSpringDaoForDML {
	private static final Logger logger =  LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	 public void saveOrUpdateAll(Collection collection){
		 super.saveByCollection(collection);
	 }

	public void initiatePurge(Long userId, Long listId) {
		String queryStr = "insert into purge_queue(user_id,list_id,status,created_date) VALUES ("+ userId+ ","+ listId+ ",'"
				+ Constants.PURGE_STATUS_INCOMPLETED+ "',"	+  "now()) ON DUPLICATE KEY UPDATE  status='"+ Constants.PURGE_STATUS_INCOMPLETED+ "'";
		executeJdbcQuery(queryStr);
	}
}
