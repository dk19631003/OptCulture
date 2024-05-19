package org.mq.optculture.data.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import org.mq.marketer.campaign.beans.CouponCodes;
import org.mq.marketer.campaign.beans.LoyaltyMemberItemQtyCounter;
import org.mq.marketer.campaign.beans.LoyaltyProgram;
import org.mq.marketer.campaign.beans.LoyaltyProgramTier;
import org.mq.marketer.campaign.beans.ReferralProgram;
import org.mq.marketer.campaign.beans.ReferralcodesRedeemed;
import org.mq.marketer.campaign.beans.RewardReferraltype;
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

public class ReferralcodesRedeemedDao extends AbstractSpringDao{
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);

	private SessionFactory sessionFactory;

	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public ReferralcodesRedeemed  testForrefcode(String cCode, Long orgId,Long Refereecid) {
		List<ReferralcodesRedeemed> list = null;
		String query = "FROM ReferralcodesRedeemed  WHERE orgId ="+orgId+" AND refcode ='"+cCode+"'  AND   refereecid="+Refereecid+"   AND refcodestatus IN('"+Constants.COUP_CODE_STATUS_REDEEMED+"')"; 
				
		logger.info("query for countredeem is"+query);
		list  = getHibernateTemplate().find(query);
		if(list!= null && list.size()>0) {
			return list.get(0);
		}
			
		return null;
	}
	
	
	public ReferralcodesRedeemed  testIfOfferAvailed(Long orgId,Long Refereecid) {
		List<ReferralcodesRedeemed> list = null;
		String query = "FROM ReferralcodesRedeemed  WHERE orgId ="+orgId+"   AND   refereecid="+Refereecid+"   AND refcodestatus IN('"+Constants.COUP_CODE_STATUS_REDEEMED+"')"; 
				
		logger.info("query for countredeem2 is"+query);
		list  = getHibernateTemplate().find(query);
		if(list!= null && list.size()>0) {
			return list.get(0);
		}
			
		return null;
	}
	
	
	public ReferralcodesRedeemed  testForCouponCodes2(String cCode, Long orgId) {
		List<ReferralcodesRedeemed> list = null;
		String query = "FROM ReferralcodesRedeemed  WHERE orgId ="+orgId+" AND refcode ='"+cCode+"' AND refcodestatus IN('"+Constants.COUP_CODE_STATUS_REDEEMED+"')"; 
				
		logger.info("query for countredeem is"+query);
		list  = getHibernateTemplate().find(query);
		if(list!= null && list.size()>0) {
			return list.get(0);
		}
			
		return null;
	}
	
	
	
	

	public Long findRedeemdCountByRefId(Long referralcodeId,Long referredCid) {
		
	
		String qry = "SELECT COUNT(*) FROM  ReferralcodesRedeemed WHERE referredcid="+referredCid+" AND referralcodeid ="+referralcodeId+"  AND refcodestatus  IN('"+Constants.COUP_CODE_STATUS_REDEEMED+"')";

		
		logger.info("findRedeemdCountByCoupId quuery is >>::"+qry);
		Long count=((Long)getHibernateTemplate().find(qry).get(0));
		return count;
	} // findRedeemdCountByCoupId
	
	public Long findRedeemdCountByCode(String refCode) {
		String qry = "SELECT COUNT(*) FROM  ReferralcodesRedeemed WHERE refcode='"+refCode+"' AND refcodestatus IN('"+Constants.COUP_CODE_STATUS_REDEEMED+"')";
		logger.info("findRedeemdCountByCode query is >>::"+qry);
		Long count=((Long)getHibernateTemplate().find(qry).get(0));
		return count;
		
	} //findRedeemdCountByCode

	
	public List<ReferralcodesRedeemed> findRedeemedMemberListByCode(String refCode,int startIdx,int endIdx) {
		// TODO Auto-generated method stub
		List<ReferralcodesRedeemed> list = null;
		String qry = " FROM  ReferralcodesRedeemed WHERE refcode='"+refCode+"'";
		logger.info("findRedeemdCountByCode query is >>::"+qry);
		list  = executeQuery(qry,startIdx,endIdx);
		return list;
	} // findRedeemedMemberListByCode
	










}



