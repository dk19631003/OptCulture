/**
 * 
 */
package org.mq.marketer.campaign.dao;

import java.util.List;

import org.mq.marketer.campaign.beans.SmsQueue;

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
public class SmsQueueDao extends AbstractSpringDao {

	/**
	 * 
	 */
	public SmsQueueDao() {}
	
	/**
	 * select
	 * @param id
	 * @return SmsQueue
	 */
	public SmsQueue find(Long id){
		return (SmsQueue) super.find(SmsQueue.class,id);
	}

	/**
	 * save or update
	 * @param smsQueue
	 */
	/*public void saveOrUpdate(SmsQueue smsQueue){
		super.saveOrUpdate(smsQueue);
	}*/
	
	/**
	 * Find all
	 * @return list of SmsQueue
	 */
	public List<SmsQueue>findAll(){
		return super.findAll(SmsQueue.class);
	}
	
	/**
	 * 
	 * @param msgType
	 * @param userId
	 * @return
	 */
	public List<SmsQueue> findByMsgType(String msgType, Long userId){
		return getHibernateTemplate().find("FROM SmsQueue WHERE msgType='"+msgType+"' AND user="+userId);
	}

	
	/**
	 * Find by status
	 * @param status
	 * @param user_id
	 * @return list of SmsQueue
	 */
	public List<SmsQueue> findByStatus(String status, Long user_id){
		return getHibernateTemplate().find("FROM SmsQueue WHERE status='"+status+"' and user="+user_id);
	}
	
	
}//EOF
