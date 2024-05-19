/**
 * 
 */
package org.mq.marketer.campaign.dao;

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
public class WAQueueDaoForDML extends AbstractSpringDaoForDML {

	public WAQueueDaoForDML() {}
	

	public void saveOrUpdate(WAQueue waQueue){
		super.saveOrUpdate(waQueue);
	}
	
	
}//EOF
