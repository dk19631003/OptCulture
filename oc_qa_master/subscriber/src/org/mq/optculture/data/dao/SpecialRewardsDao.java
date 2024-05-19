package org.mq.optculture.data.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import org.mq.marketer.campaign.beans.LoyaltyMemberItemQtyCounter;
import org.mq.marketer.campaign.beans.LoyaltyProgram;
import org.mq.marketer.campaign.beans.SpecialReward;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.dao.AbstractSpringDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.optculture.model.updatecontacts.Loyalty;
import org.mq.optculture.utils.OCConstants;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

public class SpecialRewardsDao extends AbstractSpringDao{
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);

	private SessionFactory sessionFactory;

	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	public int findCountByStatus(Long userId,String status) {
    	try {
    		String sbquery="";
    		if(status!=null && status.length()>0)
    			sbquery=" AND statusSpecialReward like'"+status+"'";
    		String query ="SELECT COUNT(*) from SpecialReward where createdBy ="+userId+sbquery;
			return ((Long) find(query).get(0)).intValue();
			
		} catch (DataAccessException e) { 
			logger.error("Exception ::" , e);
			return 0;
		}
    }
	public int findCountByStatusInManageRewards(Long userId,String status) {
    	try {
    		String sbquery="";
    		if(status!=null && status.length()>0)
    			sbquery=" AND statusSpecialReward like'"+status+"'";
    		String query ="SELECT COUNT(*) from SpecialReward where createdBy ="+userId+ " AND associatedWithFBP=false "+sbquery;
			return ((Long) find(query).get(0)).intValue();
			
		} catch (DataAccessException e) { 
			logger.error("Exception ::" , e);
			return 0;
		}
    }
	public List<SpecialReward> findSpecialRewardsByUserId(Long UserId,String status,int startIndx,int endIndx) {
    	try {
    		String sbquery="";
    		if(status!=null && status.length()>0)
    			sbquery=" AND statusSpecialReward like'"+status+"'";
    		String Query="FROM SpecialReward WHERE createdBy ="+UserId+sbquery+" order by createdDate desc";
    		logger.info("query===>"+Query);
    		return executeQuery(Query,startIndx,endIndx);
		} catch (DataAccessException e) {
			logger.error("Exception ::" , e);
			return null;
		}
    }
	public List<SpecialReward> findSpecialRewardsByUserIdInManageRewards(Long UserId,String status,int startIndx,int endIndx) {
    	try {
    		String sbquery="";
    		if(status!=null && status.length()>0)
    			sbquery=" AND statusSpecialReward like'"+status+"'";
    		String Query="FROM SpecialReward WHERE createdBy ="+UserId+sbquery+" AND associatedWithFBP=false order by createdDate desc";
    		logger.info("query===>"+Query);
    		return executeQuery(Query,startIndx,endIndx);
		} catch (DataAccessException e) {
			logger.error("Exception ::" , e);
			return null;
		}
    }
	public List<SpecialReward> findRewarRuleByName(String ruleName,Long userId) {
		String Query="FROM SpecialReward WHERE createdBy ="+userId +" AND rewardName='"+ruleName+"'";
		logger.info("query===>"+Query);
		return  executeQuery(Query);
	}
	
	public List<SpecialReward> findRewardRuleByValueCode(String valueCode,Long userId) {
		String Query="FROM SpecialReward WHERE createdBy ="+userId +" AND rewardName='"+valueCode+"'";
		logger.info("query===>"+Query);
		return  executeQuery(Query);
	}
	
	public List<LoyaltyProgram> findByRewardId(Long rewardId) {
        String query = " SELECT DISTINCT (program_id) FROM spreward_program WHERE sprule_id=" +rewardId;
        List<Long> list= jdbcTemplate.query(query, new RowMapper() {
			 public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				return  rs.getLong("program_id");
			 }
		 } );
       StringBuilder sb=new StringBuilder();
      list.forEach(id->{
    	  if(sb.length()>0)sb.append(",");
    	  sb.append(id);
      });
      if(sb.length()==0)return null;
      query="FROM LoyaltyProgram where programId IN("+sb.toString()+")";
      return executeQuery(query);
    }
	
	public List<Object[]> getTransactionBySprewarId(Long sprwardid,String memNum){
		String query="SELECT sum(earned_reward),sum(entered_amount) from loyalty_transaction_child "
				+ "where special_reward_id="+sprwardid+" AND transaction_type='"+OCConstants.LOYALTY_TRANSACTION_ISSUANCE+"' GROUP BY membership_number";
		/*String query="SELECT sum(earned_reward),sum(entered_amount) from loyalty_transaction_child "
				+ "where docsid IN (select docsid from loyalty_transaction_child where special_reward_id="+sprwardid+" "
						+ "AND transaction_type='"+OCConstants.LOYALTY_TRANSACTION_ISSUANCE+"') "
						+ "GROUP BY membership_number";*/
		List<Object[]> list= jdbcTemplate.query(query, new RowMapper() {
			 public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				 Object obj[]=new Object[2];
				 obj[0]=rs.getLong("sum(earned_reward)");
				 obj[1]=rs.getLong("sum(entered_amount)");
				 return obj;
			 }
		 } );
		return list;
				
	}
    public String findHighestMultiplierInProgram(Long ProgramId, Long orgID) {

    try {
	String qry = "SELECT Max(l.RewardValue)  FROM SpecialReward l join "
			+ "l.loyaltyPrograms ur WHERE WHERE l.orgId="+orgID+" AND ur.programId="+ProgramId+" and l.RewardType='Multiplier'";	
	return ((String)(executeQuery(qry).get(0)));
    }
    catch(Exception e) {
    	logger.error("e===>"+e);
    	return null;
    }
    }	
    public List<SpecialReward> findSpecialRewardInProgram(Long ProgramId, Long orgID) {

    try {	
	String qry = "SELECT Distinct l FROM SpecialReward l  "
			+ "join l.loyaltyPrograms lp   WHERE l.orgId="+orgID+" AND lp.programId="+ProgramId+" and l.statusSpecialReward='Active'"  ; //and l.RewardType='Multiplier'";	
	return executeQuery(qry);
    }
    catch(Exception e) {
    	logger.error("e===>"+e);
    	return null;
    }
    }	
	
    
    public List<SpecialReward> findItemBasedSpecialRewardInProgram(Long ProgramId, Long orgID) {

        try {	
    	String qry = "SELECT Distinct l FROM SpecialReward l  join l.loyaltyPrograms lp  "
    			+ " WHERE  l.orgId="+orgID+" AND  lp.programId="+ProgramId+" and l.statusSpecialReward='Active' "
    					+ "AND l.rewardRule like '%[#ItemFactor#]%' AND l.enableReturnOnCurrentRule=true"  ; //and l.RewardType='Multiplier'";	
    	return executeQuery(qry);
        }
        catch(Exception e) {
        	logger.error("e===>"+e);
        	return null;
        }
        }	
    
    public List<String> findSpecialRuleInProgram(Long ProgramId) {

    try {	
    	String qry = "SELECT distinct l.RewardRule FROM SpecialReward l join l.loyaltyPrograms ur WHERE ur.programId="+ProgramId;	
    	return executeQuery(qry);
    }
    catch(Exception e) {
    	logger.error("e===>"+e);
    	return null;
    }
    }	
    
   
    
    public List<SpecialReward> findBy(String spIDs){
    	
    	String query = "FROM SpecialReward WHERE rewardId IN("+spIDs+")";
    	
    	List<SpecialReward> retList = executeQuery(query);
    	
    	return retList;
    }
    
}
