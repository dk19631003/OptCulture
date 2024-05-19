package org.mq.loyality.common.dao;


import java.io.Serializable;
import java.util.List;

import org.hibernate.SessionFactory;
import org.mq.loyality.common.hbmbean.OTPGeneratedCodes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


public class OTPGeneratedCodesDao   {
    
	public OTPGeneratedCodesDao() {}
	@Autowired
    private SessionFactory sessionFactory;
	@Autowired
	private SessionFactory sessionFactoryForDML;

	 @Transactional
	public OTPGeneratedCodes findOTPCodeByPhone(String phone, Long userId, String status){
		
		String queryStr = null;
		List list = null;
		
		try{
			
			queryStr = "FROM OTPGeneratedCodes WHERE userId = "+userId+" AND phoneNumber = '"+phone+"'"
					+ " AND status = '"+status+"'";
			list = sessionFactory.getCurrentSession().createQuery(queryStr).list();
			if(list != null && list.size() > 0) return (OTPGeneratedCodes)list.get(0);
			else return null;
			
		}catch(Exception e){
			return null;
		}
		
	}
	@Transactional("txMngrForDML")
	public void saveOrUpdate(OTPGeneratedCodes obj) {
		sessionFactoryForDML.getCurrentSession().saveOrUpdate(obj);
    }	
	
	
	
	
	
   
   
}
