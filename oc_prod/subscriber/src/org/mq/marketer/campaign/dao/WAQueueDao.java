/**
 * 
 */
package org.mq.marketer.campaign.dao;

import java.util.List;

import org.mq.marketer.campaign.beans.WAQueue;

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
public class WAQueueDao extends AbstractSpringDao {

	
	public WAQueueDao() {}
	

	public WAQueue find(Long id){
		return (WAQueue) super.find(WAQueue.class,id);
	}

	/**
	 * save or update
	 * @param waQueue
	 */
	
	/**
	 * Find all
	 * @return list of waQueue
	 */
	public List<WAQueue>findAll(){
		return super.findAll(WAQueue.class);
	}
	
	/**
	 * 
	 * @param msgType
	 * @param userId
	 * @return
	 */
	public List<WAQueue> findByMsgType(String msgType, Long userId){
		return getHibernateTemplate().find("FROM WAQueue WHERE msgType='"+msgType+"' AND user="+userId);
	}

	
	/**
	 * Find by status
	 * @param status
	 * @param user_id
	 * @return list of WAQueue
	 */
	public List<WAQueue> findByStatus(String status, Long user_id){
		return getHibernateTemplate().find("FROM WAQueue WHERE status='"+status+"' and user="+user_id);
	}
	
	
}//EOF
