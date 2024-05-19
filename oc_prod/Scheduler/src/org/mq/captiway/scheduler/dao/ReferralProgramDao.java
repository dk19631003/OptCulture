package org.mq.captiway.scheduler.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import org.mq.captiway.scheduler.beans.Coupons;
import org.mq.captiway.scheduler.beans.ReferralProgram;
import org.mq.captiway.scheduler.utility.Constants;


import org.mq.optculture.utils.OCConstants;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

public class ReferralProgramDao extends AbstractSpringDao{
	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);

	private SessionFactory sessionFactory;

	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	public int getAllRefprgrmCount() {
		// TODO Auto-generated method stub
		String qry ="SELECT COUNT(referralId) from ReferralProgram";
		int size = ((Long) executeQuery(qry).get(0)).intValue();
		return size;
	}

	public List<ReferralProgram> getAllRefPrgms(int startIdx, int endIdx) {
		List<ReferralProgram> eventList = null;
		
		String query = "FROM ReferralProgram  order by 1 ASC ";
		
		eventList = executeQuery(query, startIdx, endIdx);
		if(eventList!= null && eventList.size()>0) {
			return eventList;
		}
			
		return null;
		
	} 
	
	public Coupons findrefCouponsByOrgId2(Long orgId) {

		String query = "FROM Coupons  WHERE orgId ="+orgId+" and useasReferralCode is true ORDER BY couponCreatedDate DESC ";
		List<Coupons> list = getHibernateTemplate().find(query);
		
		if(list!= null && list.size()>0) {
			return  list.get(0);
		}
		return null;
	}
	
	
	
	public ReferralProgram findReferalprogramByUserId(Long UserId) {
    
    		
    		String Query="FROM ReferralProgram WHERE userId ="+UserId+" and status='"+OCConstants.REFERRAL_STATUS_RUNNING+"'";
    		logger.info("query===>"+Query);
    		List<ReferralProgram> list = getHibernateTemplate().find(Query);
    		
    		if(list!= null && list.size()>0) {
    			return  list.get(0);
    		}
    		return null;
		}

	
	
	public ReferralProgram findReferalprogramByActive(Long UserId,Long  couponid) {
	    
		
		String Query="FROM ReferralProgram WHERE userId ="+UserId+" and couponId="+couponid+" and status='"+OCConstants.REFERRAL_STATUS_ACTIVE+"'";
		logger.info("query===>"+Query);
		List<ReferralProgram> list = getHibernateTemplate().find(Query);
		
		if(list!= null && list.size()>0) {
			return  list.get(0);
		}
		return null;
	}



	
	
	
	
	
	
	public ReferralProgram findReferalprogramByUserIdnew(Long UserId,Long couponid) {
	    
		
		String Query="FROM ReferralProgram WHERE userId ="+UserId+" and couponId="+couponid+"";
		logger.info("query===>"+Query);
		List<ReferralProgram> list = getHibernateTemplate().find(Query);
		
		if(list!= null && list.size()>0) {
			return  list.get(0);
		}
		return null;
	}
	
	
	
	
	
	public int findCountByStatusInMyReferralPrgms(Long userId,String status) {
    	try {
    		String sbquery="";
    		if(status!=null && status.length()>0)
    			sbquery=" AND status like '"+status+"'";
    		String query ="SELECT COUNT(*) from ReferralProgram where userId ="+userId+"" +sbquery;
    		logger.info("query is"+query);
    	
    		return ((Long) find(query).get(0)).intValue();
			
		} catch (DataAccessException e) { 
			logger.error("Exception ::" , e);
			return 0;
		}
    }
	
	public List<ReferralProgram> findRewarRuleByName(String ruleName,Long userId) {
	
		String Query="FROM ReferralProgram WHERE userId ="+userId +" AND programName='"+ruleName+"'";
		logger.info("query===>"+Query);
		return  executeQuery(Query);
	}
	
	
	public List<ReferralProgram> findReferalprogramsByUserId(Long UserId,String status,int startIndx,int endIndx) {
    
    		try {
    			String sbquery=""; 
    		if(status!=null &&
    				  status.length()>0)
    			sbquery=" AND status like '"+status+"'";
    	
    		String Query="FROM ReferralProgram WHERE userId ="+UserId+sbquery+"";
    		logger.info("query===>"+Query);
    		return executeQuery(Query,startIndx,endIndx);
		} catch (DataAccessException e) {
			logger.error("Exception ::" , e);
			return null;
		}
    	}
    	
	
	public List<ReferralProgram> findReferalprogramsByUserId1(Long UserId) {
    	try {
    		
    		String Query="FROM ReferralProgram WHERE userId ="+UserId+" ";
    		logger.info("query===>"+Query);
    		return executeQuery(Query);
		} catch (DataAccessException e) {
			logger.error("Exception ::" , e);
			return null;
		}

	}
}

