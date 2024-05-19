package org.mq.marketer.campaign.dao;


import java.util.Calendar;
import java.util.List;

import org.hibernate.SessionFactory;
import org.mq.marketer.campaign.beans.Opens;
import org.mq.marketer.campaign.beans.UserExternalSMTPSettings;

@SuppressWarnings({ "unchecked", "unused"})
public class ExternalSMTPSettingsDao extends AbstractSpringDao {
	    public ExternalSMTPSettingsDao() {}
	    private SessionFactory sessionFactory;

	    public UserExternalSMTPSettings find(Long id) {
	        return (UserExternalSMTPSettings) super.find(UserExternalSMTPSettings.class, id);
	    }

	   /* public void saveOrUpdate(UserExternalSMTPSettings userExternalSMTPSettings) {
	        super.saveOrUpdate(userExternalSMTPSettings);
	    }

	    public void delete(UserExternalSMTPSettings userExternalSMTPSettings) {
	        super.delete(userExternalSMTPSettings);
	    }
*/
	    public List findAll() {
	        return super.findAll(UserExternalSMTPSettings.class);
	    }
	    
	    public UserExternalSMTPSettings findByUserId(Long userId) {
	    	List<UserExternalSMTPSettings> list = getHibernateTemplate().find("from UserExternalSMTPSettings where users="+ userId);
	    	return list.get(0);
	    }
	    
}

