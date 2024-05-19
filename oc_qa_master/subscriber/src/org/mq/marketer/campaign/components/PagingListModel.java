package org.mq.marketer.campaign.components;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.dao.ApplicationPropertiesDao;
import org.mq.marketer.campaign.general.Constants;
import org.zkoss.zkplus.spring.SpringUtil;

public class PagingListModel<T> extends AbstractPagingListModel<T> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 0L;
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);	 
	private ApplicationPropertiesDao appDao;
	
	public PagingListModel(int startPageNumber, int pageSize, String query, String countQuery) {
		super(startPageNumber, pageSize, query, countQuery);
	}

	@Override
	protected List<T> getPageData(int itemStartNumber, int pageSize, String query, String countQuery) {
		
		//logger.info("In getPAgeData query="+query); -- APP-3914
		if(appDao==null) appDao = (ApplicationPropertiesDao)SpringUtil.getBean("applicationPropertiesDao");
		
		return (List<T>)appDao.executeQuery(query, itemStartNumber, pageSize);
	}

	@Override
	public int getTotalSize() {
		if(appDao==null) appDao = (ApplicationPropertiesDao)SpringUtil.getBean("applicationPropertiesDao");
		return (int) appDao.getCountByCountQuery(get_countQuery()).longValue();
	}

}
