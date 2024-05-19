package org.mq.captiway.scheduler.dao;

import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.Messages;
import org.mq.captiway.scheduler.beans.Users;
import org.mq.captiway.scheduler.utility.Constants;

public class MessagesDaoForDML extends AbstractSpringDaoForDML {
	
	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);

    public MessagesDaoForDML() {}

   /* public Messages find(Long id) {
        return (Messages) super.find(Messages.class, id);
    }*/

    public void saveOrUpdate(Messages messages) {
        super.saveOrUpdate(messages);
    }
    public void delete(Messages messages) {
        super.delete(messages);
    }

   /* @SuppressWarnings("unchecked")
	public List findAll() {
        return super.findAll(Messages.class);
    }
    */
    public void saveByCollection(Collection<Messages> collection) {
    	super.saveOrUpdateAll(collection);
    }

   /* public boolean findSameMsgWithInSameDay(Users user, String msg, String subject, String module) {
    	
    	
    	try {
			String message = "FROM Messages WHERE users.userId="+user.getUserId().longValue()+" " +
					"AND subject='"+subject+"' AND module='"+module+"' AND message='"+StringEscapeUtils.escapeSql(msg)+"' AND DAY(createdDate) = DAY(NOW())";
			
			logger.info("message ::"+message);
			
			List<Messages> list = executeQuery(message);
			
			if(list != null && list.size()>0) {
				
				return true;
				
			}else{
				
				return false;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return false;
		}
    	
    }//
    
    */
    
}

