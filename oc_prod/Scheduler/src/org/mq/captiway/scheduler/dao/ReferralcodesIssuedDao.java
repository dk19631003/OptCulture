package org.mq.captiway.scheduler.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;

import org.mq.optculture.utils.OCConstants;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.mq.captiway.scheduler.beans.CouponCodes;
import org.mq.captiway.scheduler.utility.Constants;
import org.mq.captiway.scheduler.beans.ReferralcodesIssued;


public class ReferralcodesIssuedDao extends AbstractSpringDao{
	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);

	private SessionFactory sessionFactory;

	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	public List<ReferralcodesIssued> getInventoryCouponCodesByCouponId(long couponId) {
		List<ReferralcodesIssued> list = null;
		
		String query = "FROM ReferralcodesIssued  WHERE couponId ="+couponId +" AND status = '"+ Constants.COUP_CODE_STATUS_INVENTORY+"' ";
		list = executeQuery(query, 0, 1000);

		if(list!= null && list.size()>0) {
			return list;
		}
		return null;
	}
	
	
	public List<ReferralcodesIssued> getRefcodebycontactid(long contactId) {
		List<ReferralcodesIssued> list = null;
		
		String query = "FROM ReferralcodesIssued  WHERE referredCId ="+contactId +" ";
		
		logger.info("query is "+query);
		list = executeQuery(query, 0, 1000);

		logger.info("list is "+list);

		if(list!= null && list.size()>0) {
			return list;
		}
		return null;
	}
	
	
	
	
	
	public ReferralcodesIssued testForrefCodes(String cCode, long orgId) {
		List<ReferralcodesIssued> list = null;
		String query = "FROM ReferralcodesIssued  WHERE orgId ="+orgId+" AND Refcode ='"+cCode+"'";
		list  = getHibernateTemplate().find(query);
		if(list!= null && list.size()>0) {
			return list.get(0);
		}
			
		return null;
	}
	
	
	
	public long getReferralCodeCountByStatus(Long couponId, String status) {
		
		String query=null;

		if(status==null || status.trim().equalsIgnoreCase("All")) {
			 query = "SELECT  COUNT(referralCodeId) FROM ReferralcodesIssued WHERE couponId ="+couponId;
		}
		else {
		 query = "SELECT  COUNT(referralCodeId) FROM ReferralcodesIssued WHERE couponId ="+couponId+" AND status ='"+status.trim()+"' ";
		}

		long count =  ((Long)getHibernateTemplate().find(query).get(0)).longValue();
		
		return count;
		
	} // findByCouponCode
	
	
	
	
	
	
	
	
	
}



