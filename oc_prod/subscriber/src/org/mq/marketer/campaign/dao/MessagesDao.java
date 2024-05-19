package org.mq.marketer.campaign.dao;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.Messages;
import org.mq.marketer.campaign.general.Constants;
import org.springframework.dao.DataAccessException;

public class MessagesDao extends AbstractSpringDao {

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);

    public MessagesDao() {}

    public Messages find(Long id) {
        return (Messages) super.find(Messages.class, id);
    }
/*
    public void saveOrUpdate(Messages messages) {
        super.saveOrUpdate(messages);
    }*/

   /* public void delete(Messages messages) {
        super.delete(messages);
    }*/

    @SuppressWarnings("unchecked")
	public List findAll() {
        return super.findAll(Messages.class);
    }
    @SuppressWarnings("unchecked")
	public List<Messages> findAllByUser(String userName) {
       return getHibernateTemplate().find("from Messages where users.userName like '" + userName + "' and folder not like 'Trash' order by id desc ");
    }
    @SuppressWarnings("unchecked")
	public List<Messages> findAllByFolder(String userName,String folder) {
       return getHibernateTemplate().find("from Messages where users.userName like '" + userName + "' and folder like '" + folder +"' order by id desc ");
    }
    
    
    public List<Messages> findAllByFolder(String userName,String folder, int startIndex, int count) {
    	
    	String query = "from Messages where users.userName like '" + userName + "' and folder like '" + folder +"' order by id desc ";
    	List<Messages> list = 
        executeQuery(query, startIndex, count);
        return list;
        
     }
     
    
    
    public List<Messages> findAllByUser(String userName, int startFrom, int count) {
        
    	String query = "from Messages where users.userName like '" + userName + "' and folder not like 'Trash' order by id desc ";
    	List<Messages> list = executeQuery(query, startFrom, count);
    	
    	return list;
    	
    	
     }
    
    
    
    
    
	@SuppressWarnings("unchecked")
	public List findByModule(String module, String folder, String userName){
	  	return getHibernateTemplate().find("from Messages where module = '" + module + "' and folder = '" + folder + "' and users.userName like '" + userName + "'");
	}
	
	@SuppressWarnings("unchecked")
	public int findNewCount(Long userId){
	  	int count = 0;
		try{	
		  	List list = getHibernateTemplate().find("select count(*) from Messages where users=" + userId+ " and read = false and folder = 'INBOX'");
		  	if(list.size()>0){
		  		count = ((Long)list.get(0)).intValue();
		  	}
	  	}catch(Exception e){
	  		logger.error("** Exception : " + e);
	  	}
	  	return count;
	}	
	
	
	
	
	
	
	@SuppressWarnings("unchecked")
	public int findNewTrashCount(Long userId){
	  	int count = 0;
		try{	
		  	List list = getHibernateTemplate().find("select count(*) from Messages where users=" + userId+ " and folder = 'Trash'");
		  	if(list.size()>0){
		  		count = ((Long)list.get(0)).intValue();
		  	}
	  	}catch(Exception e){
	  		logger.error("** Exception : " + e);
	  	}
	  	return count;
	}	
	
	public int findCount(Long userId) {
		
		int count = 0;
		String query = "SELECT COUNT(*) FROM Messages WHERE users="+userId+" AND folder NOT like 'Trash' order by id desc";
		try {
			List list = getHibernateTemplate().find(query);
			if(list.size() > 0) {
				count = ((Long)list.get(0)).intValue();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("** Exception : " + e);
		}
		return count;
	}//findCount
	
	
}

