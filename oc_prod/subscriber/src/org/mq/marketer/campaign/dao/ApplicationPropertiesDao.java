package org.mq.marketer.campaign.dao;

import java.util.List;

import org.hibernate.SessionFactory;
import org.mq.marketer.campaign.beans.ApplicationProperties;
import org.mq.marketer.campaign.beans.Opens;

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

   /* public void saveOrUpdate(ApplicationProperties applicationProperties) {
        super.saveOrUpdate(applicationProperties);
    }

    public void delete(ApplicationProperties applicationProperties) {
        super.delete(applicationProperties);
    }*/

    public List findAll() {
        return super.findAll(ApplicationProperties.class);
    }
     
}
