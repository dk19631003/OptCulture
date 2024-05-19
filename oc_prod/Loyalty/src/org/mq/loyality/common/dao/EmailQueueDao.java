package org.mq.loyality.common.dao;


import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import org.mq.loyality.common.hbmbean.EmailQueue;
import org.mq.loyality.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@SuppressWarnings({ "unchecked", "serial","unused"})
@Repository
public class EmailQueueDao  {
	
	@Autowired
	private SessionFactory sessionFactory;
	@Autowired
	private SessionFactory sessionFactoryForDML;
	private static final Logger logger = LogManager.getLogger(Constants.LOYALTY_LOGGER);

    public EmailQueueDao() {}
	
    @Transactional
    public EmailQueue find(Long id) {
        return (EmailQueue) sessionFactory.getCurrentSession().load(EmailQueue.class, id);
    }

	@Transactional("txMngrForDML")
    public void saveOrUpdate(EmailQueue testEmailQueue) {
        sessionFactoryForDML.getCurrentSession().saveOrUpdate(testEmailQueue);
    }

    @Transactional
    public void delete(EmailQueue testEmailQueue) {
    	sessionFactoryForDML.getCurrentSession().delete(testEmailQueue);
    }

    @Transactional
    public List findAll() {
        return sessionFactory.getCurrentSession().createQuery("from EmailQueue").list();
    }

    @Transactional
    public List findByStatus(String status){
    	return sessionFactory.getCurrentSession().createQuery(" from EmailQueue where status= '" + status + "'").list();
    }
    
    @Transactional
    public List findByType(Long userId,String type){
    	return sessionFactory.getCurrentSession().createQuery(" from EmailQueue where type='" + type + "' AND user="+ userId + " ORDER BY sentDate DESC").list();
    }
 
    @Transactional
    public EmailQueue findEqById(long msgId){
    	EmailQueue emailQueue = null;
    	try {
   
			List list =sessionFactory.getCurrentSession().createQuery(" from EmailQueue where id= " +msgId ).list();
			if (list.size() > 0) {
				emailQueue = (EmailQueue) list.get(0);
			}
		} catch (DataAccessException e) {
			logger.info(" Exception :: ",e);
		}
    	return emailQueue;
    }
}

