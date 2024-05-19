package org.mq.marketer.campaign.dao;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import org.mq.marketer.campaign.beans.*;
import org.mq.marketer.campaign.general.Constants;
import org.springframework.jdbc.core.JdbcTemplate;

public class PromoTrxLogDaoForDML extends AbstractSpringDaoForDML{

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);

	private SessionFactory sessionFactory;
	
	public void saveOrUpdate(PromoTrxLog promoTrxLog) {
	    super.saveOrUpdate(promoTrxLog);
	}
		
}
