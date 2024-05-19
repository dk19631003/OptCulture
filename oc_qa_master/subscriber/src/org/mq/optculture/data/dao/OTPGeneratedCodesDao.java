package org.mq.optculture.data.dao;


import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.hibernate.HibernateException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.mq.marketer.campaign.beans.OTPGeneratedCodes;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.dao.AbstractSpringDao;
import org.mq.marketer.campaign.general.Constants;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;


@SuppressWarnings({"unchecked","unused"})
public class OTPGeneratedCodesDao extends AbstractSpringDao implements Serializable {
    
	public OTPGeneratedCodesDao() {}
    private SessionFactory sessionFactory;
    private JdbcTemplate jdbcTemplate;
    
    public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public OTPGeneratedCodes findOTPCodeByPhone(String phone, Long userId, String status,String email,String OTP){
		
		String queryStr = null;
		List list = null;
		
		try{
			
			String sbqry="";
			if(phone!=null &&phone.length()>0){
			sbqry="	AND phoneNumber like '%"+phone+"' ";
			if(email!=null && email.length()>0)
				sbqry="	AND (phoneNumber like '%"+phone+"'  OR email = '"+email+"') ";
			}
			else{
			if(email!=null && email.length()>0)
			sbqry="	AND email = '"+email+"' ";
			}
			
			if(OTP!=null && !OTP.isEmpty()) {
				sbqry="	AND otpCode = '"+OTP+"' ";
			}
			
			
			queryStr = "FROM OTPGeneratedCodes WHERE userId = "+userId+sbqry
					+ " AND status = '"+status+"'";
			list = getHibernateTemplate().find(queryStr);
			if(list != null && list.size() > 0) return (OTPGeneratedCodes)list.get(0);
				else return null;
			
		}catch(Exception e){
			logger.error("Exception in find otp code by phone ...", e);
			return null;
		}
		
		
	}
   
	
	
	public OTPGeneratedCodes findOTPCodeByPhonenew(String phone, Users user, String status,String email,String OTP){
		
		String queryStr = null;
		List list = null;
		
		try{
			
			String sbqry="";
			String phnqry="";
			if(user.getCountry().equalsIgnoreCase(Constants.SMS_COUNTRY_INDIA)) {
				logger.info("entering india ");
				phnqry= "phoneNumber = '"+phone+"'" ;
			}else {
				phnqry= "phoneNumber like '%"+phone+"' ";
			}
			logger.info(" phnqry value is"+phnqry);
			
			if(phone!=null &&phone.length()>0){
				sbqry="	AND "+phnqry ;
				logger.info(" phnqry value is"+sbqry);
			if(email!=null && email.length()>0)
				sbqry="AND ( "+phnqry+" OR email = '"+email+"') ";
			
			}
			else{
			if(email!=null && email.length()>0)
			sbqry="	AND email = '"+email+"' ";
			}
			
			if(OTP!=null && !OTP.isEmpty()) {
				sbqry=sbqry+" AND otpCode = '"+OTP+"' ";
			}
			logger.info(" sbqry value is"+sbqry);

			queryStr = "FROM OTPGeneratedCodes WHERE userId = "+user.getUserId().longValue()+sbqry
					+ " AND status = '"+status+"'";
			logger.info("queystr value is "+queryStr);

			list = getHibernateTemplate().find(queryStr);
			
			if(list != null && list.size() > 0) return (OTPGeneratedCodes)list.get(0);
				else return null;
			
		}catch(Exception e){
			logger.error("Exception in find otp code by phone ...", e);
			return null;
		}
		
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
   
   
}
