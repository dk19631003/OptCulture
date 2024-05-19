package org.mq.loyality.common.dao;

import java.io.Serializable;
import java.util.List;

import org.hibernate.SessionFactory;
import org.mq.loyality.common.hbmbean.UserSMSGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class UserSMSGatewayDao   {
	
	@Autowired
	 private SessionFactory sessionFactory;

	 @Transactional
	public UserSMSGateway findByUserId(Long userId, String accountType) {
		
		String qry = "FROM UserSMSGateway WHERE userId="+userId.longValue()+" AND accountType='"+accountType+"'";
		
		List<UserSMSGateway> retList =  sessionFactory.getCurrentSession().createQuery(qry).list();
		
		if(retList != null && retList.size() > 0) {
			
			return retList.get(0);
			
		}else{
			
			return null;
		}
		
	}
}
