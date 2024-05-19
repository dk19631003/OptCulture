/**
 * 
 */
package org.mq.captiway.scheduler.dao;

import java.util.Collection;
import java.util.List;

import org.mq.captiway.scheduler.beans.ContactSpecificDateEvents;
import org.mq.captiway.scheduler.beans.SmsQueue;

/**
 * This Object helps to perform all the DB Operations
 * 1.Save
 * 2.Update
 * 3.Select
 * 4.SelectAll
 * 5.SelectOnCriteria
 * @author vinod.bokare
 *
 */
public class SmsQueueDaoForDML extends AbstractSpringDaoForDML {

	/**
	 * 
	 */
	public SmsQueueDaoForDML() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Find by id
	 * @param id
	 * @return SmsQueue
	 */
	/*public SmsQueue find(Long id){
		return (SmsQueue) super.find(SmsQueue.class, id);
	}*/
	
	
	/**
	 * Save the SmsQueue Object
	 * @param smsQueue
	 */
	public void saveOrUpdate(SmsQueue smsQueue){
		super.saveOrUpdate(smsQueue);
	}
	
	public void saveByCollection(Collection etEventsList){
		
		  super.saveOrUpdateAll(etEventsList);
	  }
	
	/**
	 * 
	 * @return list of SmsQueue
	 */
	/*public List<SmsQueue> findAll(){
		return super.findAll(SmsQueue.class);
	}
	
	*//**
	 * 
	 * @param msgType
	 * @param userId
	 * @return
	 *//*
	public List<SmsQueue> findByMsgType(String msgType, Long userId){
		return getHibernateTemplate().find("FROM SmsQueue WHERE msgType='"+msgType+"' AND user="+userId);
	}
	
	
	*//**
	 * 
	 * @param msgType
	 * @param userId
	 * @return
	 *//*
	public List<SmsQueue> findByStatus(String status, Long userId){
		return getHibernateTemplate().find("FROM SmsQueue WHERE status='"+status+"' and user="+userId);
	}*/
}//EOF
