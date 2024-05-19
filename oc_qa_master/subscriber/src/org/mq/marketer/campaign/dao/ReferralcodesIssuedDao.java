package org.mq.marketer.campaign.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import org.mq.marketer.campaign.beans.LoyaltyMemberItemQtyCounter;
import org.mq.marketer.campaign.beans.LoyaltyProgram;
import org.mq.marketer.campaign.beans.LoyaltyProgramTier;
import org.mq.marketer.campaign.beans.ReferralProgram;
import org.mq.marketer.campaign.beans.ReferralcodesIssued;
//import org.mq.marketer.campaign.beans.RewardReferraltype;
import org.mq.marketer.campaign.beans.SpecialReward;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.dao.AbstractSpringDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.optculture.model.events.Events;
import org.mq.optculture.model.updatecontacts.Loyalty;
import org.mq.optculture.utils.OCConstants;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

public class ReferralcodesIssuedDao extends AbstractSpringDao{
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);

	private SessionFactory sessionFactory;

	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
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
	
	public  List<ReferralcodesIssued> findReferralCodesByOrgId(Long userOrgId, int startIdx, int endIdx ,String status, String fromDate,String toDate,String orderby_colName,String desc_Asc,String promotionName){
		
		List<ReferralcodesIssued> list = null;
		logger.info("Entered ReferalCodesByOrg and satus ====>"+status+"---"+fromDate+"---"+toDate+"----"+userOrgId);
		String query = "";
		String subQry = Constants.STRING_NILL;
		
		query = " FROM ReferralcodesIssued  WHERE orgId ='"+userOrgId.longValue()+"' AND status ='Active'" ;
		
		logger.info("query is :" +query);
		
		list = executeQuery(query,startIdx,endIdx);

		logger.info("List size is  ::" + list.size());
		if (list != null && list.size() > 0) {
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
	
	
	
	
	
	
public ReferralcodesIssued findReferalprogramByUserIdnew(Long UserId,Long couponid) {
	    
		
		String Query="FROM ReferralcodesIssuedDao WHERE userId ="+UserId+" and couponId="+couponid+"";
		logger.info("query===>"+Query);
		List<ReferralcodesIssued> list = getHibernateTemplate().find(Query);
		
		if(list!= null && list.size()>0) {
			return  list.get(0);
		}
		return null;
	}

public long findReferralCodesByOrgId(Long userOrgId, String status, String fromDate,String toDate,String orderby_colName,String desc_Asc,String promotionName){
	
	List<ReferralcodesIssued> list = null;
	logger.info("Entered ReferalCodesByOrg and satus ====>"+status+"---"+fromDate+"---"+toDate+"----"+userOrgId);
	String query = "";
	String subQry = Constants.STRING_NILL;
	
	query = " FROM ReferralcodesIssued  WHERE orgId ='"+userOrgId.longValue()+"' AND status ='Active'";
	
	logger.info("query is :" +query);

	 list = executeQuery(query);

	logger.info("List size is  ::" + list.size());
	if (list != null && list.size() > 0) {
		return list.size();
	}

	return 0;
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
	
	
	
	
	
	
	
	
}



