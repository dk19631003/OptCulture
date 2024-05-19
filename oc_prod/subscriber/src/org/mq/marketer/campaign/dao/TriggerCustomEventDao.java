package org.mq.marketer.campaign.dao;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.TriggerCustomEvent;
import org.mq.marketer.campaign.general.Constants;

public class TriggerCustomEventDao extends AbstractSpringDao {
	

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);

	public  TriggerCustomEventDao() {
		// TODO Auto-generated constructor stub
	}

	public TriggerCustomEvent find(Long id) {
		return (TriggerCustomEvent) super.find(TriggerCustomEvent.class, id);
	}

	/*public void saveOrUpdate(TriggerCustomEvent triggerCustomEvent) {
		super.saveOrUpdate(triggerCustomEvent);
	}

	public void delete(TriggerCustomEvent triggerCustomEvent) {
		super.delete(triggerCustomEvent);
	}*/

	public List<TriggerCustomEvent> findAll() {
		return super.findAll(TriggerCustomEvent.class);
	}	
	
	public List<TriggerCustomEvent> findAllByUserId(String userName) {
		logger.info("******* user Id :"+ userName);
		List<TriggerCustomEvent> list = getHibernateTemplate().find("FROM TriggerCustomEvent t1 WHERE t1.userName='"+userName+"' OR t1.userName='Captiway' ORDER BY t1.id ");
		logger.info(">>>>>>>>>>> list size :"+ list.size());
		return list;
	}	
	
	public TriggerCustomEvent findByEventName(String customTrigStr, String userName) {
		List<TriggerCustomEvent> list = getHibernateTemplate().find("FROM TriggerCustomEvent WHERE userName='"+userName+"' and  eventName='"+ customTrigStr +"'");
		return (list == null || list.isEmpty()) ? null : list.get(0);
	}
	
	

}
