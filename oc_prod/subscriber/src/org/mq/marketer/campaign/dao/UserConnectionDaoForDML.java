package org.mq.marketer.campaign.dao;

import java.util.List;

import org.mq.marketer.campaign.beans.UserConnection;



public class UserConnectionDaoForDML extends AbstractSpringDaoForDML {
	
	public UserConnectionDaoForDML() {
		// TODO Auto-generated constructor stub
	}
	
	/*public List<UserConnection> findByUserName(String userId) {
		List<UserConnection> list = getHibernateTemplate().find(" FROM UserConnection WHERE userId='"+ userId+"' ");
		return list;
	}*/

    public void saveOrUpdate(UserConnection userConnection) {
        super.saveOrUpdate(userConnection);
    }

    public void delete(UserConnection userConnection) {
        super.delete(userConnection);
    }
/*
    public List findAll() {
        return super.findAll(UserConnection.class);
    }
    
    public UserConnection findByProviderType(String userId, String providerType) {
    	List list = getHibernateTemplate().find(" FROM UserConnection WHERE userId='"+ userId + "' AND providerId='"+ providerType+"' ");
    	if(list != null && list.size() > 0) {
    		return (UserConnection)list.get(0);
    	}
    	return null;
    }*/
    
}
