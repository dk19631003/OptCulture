package org.mq.loyality.common.dao;

import java.util.List;

import org.hibernate.SessionFactory;
import org.mq.loyality.common.hbmbean.SMSSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class SMSSettingsDao{

	@Autowired
	private SessionFactory sessionFactory;
public SMSSettingsDao() {}
	
	/**
	 * method returns the user's organization sms settings(if any) 
	 * @param orgId
	 * @return
	 */
@Transactional
	public List<SMSSettings> findByUser(Long userId) {
		
		String qry = " FROM SMSSettings WHERE userId="+userId.longValue();
		
		List<SMSSettings> list = sessionFactory.getCurrentSession().createQuery(qry).list();
		
		if(list != null && list.size() > 0) {
			
			return list;
			
		}else{
			
			return null;
		}
		
		
		
		
	}

	

}
