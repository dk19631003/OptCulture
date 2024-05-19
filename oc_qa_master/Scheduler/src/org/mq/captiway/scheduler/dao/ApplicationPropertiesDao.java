package org.mq.captiway.scheduler.dao;



import java.util.List;

import org.hibernate.SessionFactory;
import org.mq.captiway.scheduler.beans.ApplicationProperties;


public class ApplicationPropertiesDao extends AbstractSpringDao {
	
     public ApplicationPropertiesDao() {
		// TODO Auto-generated constructor stub
	 }
     
    private SessionFactory sessionFactory;

    public ApplicationProperties findByPropertyKey(String key) {
    	List<ApplicationProperties> list = getHibernateTemplate().find("from ApplicationProperties where key= '"+ key+"'");
        if(list != null && list.size() > 0) {
        	return list.get(0);
        } else {
        	return null;
        }	
    }

   
    public List<ApplicationProperties> findAll() {
        return super.findAll(ApplicationProperties.class);
    }
     
}
