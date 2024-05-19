package org.mq.marketer.campaign.dao;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.UsersAdditionalContactDetails;
import org.mq.marketer.campaign.general.Constants;

public class UsersAdditionalContactDetailsDaoForDML extends AbstractSpringDaoForDML {
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	public UsersAdditionalContactDetailsDaoForDML(){}
		
		/*public UsersAdditionalContactDetails find(Long id){
			return (UsersAdditionalContactDetails)super.find(UsersAdditionalContactDetails.class, id);
		}*/
		
		public void saveOrUpdate(UsersAdditionalContactDetails contact){
			super.saveOrUpdate(contact);
		}
		
		public void delete(UsersAdditionalContactDetails contact){
			super.delete(contact);
		}

		/*public List<UsersAdditionalContactDetails> findByUserId(Long userId) {
			String qry = "FROM UsersAdditionalContactDetails WHERE userId =" + userId.longValue() + " ORDER BY priorityLevel ";
			List<UsersAdditionalContactDetails> list =  executeQuery(qry);
			logger.debug("list ::"+list.size());
			if(list!=null && list.size()>0) {
				return list;
			}
			else return null;
		}*/
		

}
