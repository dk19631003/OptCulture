package org.mq.optculture.data.dao;

import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import org.mq.marketer.campaign.beans.LoyaltyProgram;
import org.mq.marketer.campaign.dao.AbstractSpringDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.optculture.exception.LoyaltyProgramException;
import org.mq.optculture.utils.OCConstants;

public class LoyaltyProgramDao extends AbstractSpringDao{

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	private SessionFactory sessionFactory;
	
	/*private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	*/
	public LoyaltyProgramDao() {
	}
	/*
	public void saveOrUpdate(LoyaltyProgram loyaltyProgram) {
		super.saveOrUpdate(loyaltyProgram);
	}
	*/
	 /*public void delete(LoyaltyProgram loyaltyProgram) {
         super.delete(loyaltyProgram);
     }
	*/
	public List<LoyaltyProgram> fetchProgramsByProgramIdStr(String programIdStr) throws LoyaltyProgramException{
		
		List<LoyaltyProgram> listOfPrograms = null;
		
		try{
			
			String queryStr = " FROM LoyaltyProgram WHERE programId IN ("+programIdStr+")";
			logger.info("fetchProgramsByProgramIdStr = "+queryStr);
			return (List<LoyaltyProgram>)getHibernateTemplate().find(queryStr);
			
		}catch(Exception e){
			throw new LoyaltyProgramException("fetch programs failed", e);
		}
	}
	
	public LoyaltyProgram getDynamicProgramByUserId (Long userId) {//APP-1326
		List<LoyaltyProgram> progList = getHibernateTemplate().find( "FROM LoyaltyProgram WHERE userId ='"+userId.longValue()+"' and programType='"+OCConstants.LOYALTY_MEMBERSHIP_TYPE_DYNAMIC+"' ");
		if(progList != null && progList.size() > 0)return  progList.get(0);
		else return null;
	}
	
	public List<LoyaltyProgram> getProgListByUserId(Long userId) {
		List<LoyaltyProgram> progList = getHibernateTemplate().find(" FROM LoyaltyProgram WHERE userId = "+userId.longValue()+" ORDER BY programName");
		if(progList != null && progList.size() > 0)return progList;
		else return null;
	}
	
	public List<LoyaltyProgram> getCustomizedOTPEnabledProgListBy(Long userId, boolean checkRedemption) {
		List<LoyaltyProgram> progList = getHibernateTemplate().find(" Select lp FROM LoyaltyProgram lp,LoyaltyAutoComm la  "
				+ " WHERE lp.userId = "+userId.longValue()+" AND lp.programId = la.programId and lp.status='Active' and " +
				(checkRedemption ? "( la.redemptionOtpAutoSmsTmpltId IS NOT NULL OR la.redemptionOtpAutoEmailTmplId IS NOT NULL)" :
						 " ( la.otpMessageAutoSmsTmpltId IS NOT NULL OR la.otpMessageAutoEmailTmplId IS NOT NULL) " ));
		if(progList != null && progList.size() > 0)return progList;
		else return null;
	}
	
	public List<LoyaltyProgram> getTierEnabledProgListByUserId(Long userId) {
		List<LoyaltyProgram> progList = getHibernateTemplate().find(" FROM LoyaltyProgram WHERE userId = "+userId.longValue()+" and tierEnableFlag ='Y'  ORDER BY programName");
		if(progList != null && progList.size() > 0)return progList;
		else return null;
	}
	

	public LoyaltyProgram findById(Long prgmId) {
		List<LoyaltyProgram> list = getHibernateTemplate().find("FROM LoyaltyProgram WHERE programId="+ prgmId );
    	
    	if(list != null && list.size() > 0) {
    		return list.get(0);
    	}
    	return null;
	}
	
	public LoyaltyProgram findByIdAndUserId(Long prgmId, Long userId) {
		
		try {
			String queryStr = "FROM LoyaltyProgram WHERE programId="+ prgmId.longValue(); 
			
			List<LoyaltyProgram> list = executeQuery(queryStr);
			
			if(list != null && list.size() > 0) {
				return list.get(0);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ", e);
		}
		return null;
	}

	public int getCount(Long userId, String selectedStatus) {
    	try{
    		
    		String query = null;
    		//String userIdsStr = Utility.getUserIdsAsString(userIds);
    		
    		if(selectedStatus.equals("All")) {
    			query=" SELECT count(*) FROM LoyaltyProgram  WHERE userId = "+userId.longValue();
    		}
    		else {	
    			query=" SELECT count(*) FROM LoyaltyProgram WHERE userId = "+userId.longValue()+" AND status LIKE '" + selectedStatus + "'" ;
    		}
    		
    		List list = getHibernateTemplate().find(query);
    		if(list != null && list.size()>0)
    			return ((Long) list.get(0)).intValue();
    		else
    			return 0;
    	}catch(Exception e){
    		logger.error("**Exception :"+e+"**");
    		return 0;
    	}
    	
    	
    	
    	
    }

	public List<LoyaltyProgram> findProgramListByStatus(Long userId,
			String status, int startIndex, int size,String orderby_colName,String desc_Asc) {
		String query=null;
		List<LoyaltyProgram> progList=null;
		try{
			//String userIdsStr = Utility.getUserIdsAsString(userIds);
			logger.info("status"+status);
		if(status.equals("All")) {
			logger.info("in all"+status);
			query="FROM LoyaltyProgram WHERE userId =" + userId.longValue()+"  order by "+orderby_colName+" "+desc_Asc;
		  }else {	
		
			query="FROM LoyaltyProgram WHERE userId =" + userId.longValue()+ " and status like '" + status + "' order by "+orderby_colName+" "+desc_Asc ;
		  }
		logger.info("query---"+query);
		progList = executeQuery(query, startIndex, size);
		
		logger.info("progList3"+progList);
	    }catch(Exception e) {
	    	  logger.error("exception while retrieving campaign list",(Throwable)e);
	      }
	return progList;

}
	
	
	public LoyaltyProgram findDefaultProgramByUserId(Long userId){
		
		List<LoyaltyProgram> programList = null;
		String queryStr = " FROM LoyaltyProgram WHERE userId = "+userId+" AND defaultFlag = 'Y'";
		programList = executeQuery(queryStr);
		
		if(programList != null && programList.size() > 0){
			return programList.get(0);
		}
		else return null;
		
	}
	
	
	
	public int getProgramCount(Long userId,	String key) {  
		String subQry = "SELECT COUNT(programId) FROM  LoyaltyProgram  WHERE userId=" + userId.longValue()+ "" +
				" AND status != '"+OCConstants.LOYALTY_PROGRAM_STATUS_DRAFT+"'";

		if(key != null){

			subQry += " AND programName LIKE '%"+StringEscapeUtils.escapeSql(key)+"%'";
		}

		String query  = subQry +" ORDER BY 1 DESC";

		List tempList = getHibernateTemplate().find(query);

		if(tempList != null && tempList.size()>0)
			return ((Long) tempList.get(0)).intValue();
		else
			return 0;
	}

	

	public List<LoyaltyProgram> getProgList(Long userId,int firstResult, int pageSize,String key) {
		
		List<LoyaltyProgram> progList = null; 
		String subQry = " FROM LoyaltyProgram WHERE userId = "+userId.longValue()+ "" +
				" AND status != '"+OCConstants.LOYALTY_PROGRAM_STATUS_DRAFT+"'";
		
		
		if(key != null){

			subQry += " AND programName LIKE '%"+StringEscapeUtils.escapeSql(key)+"%'";
		}

		String query  = subQry +" ORDER BY 1 DESC";

		List tempList = getHibernateTemplate().find(query);
	  
	    return executeQuery(query,firstResult, pageSize);
		
	}
	
	
	public LoyaltyProgram findMobileBasedProgram(Long userId) {
		
		List<LoyaltyProgram> programList = null;
		String queryStr = " FROM LoyaltyProgram WHERE userId = "+userId+" AND membershipType = '"+OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE+"'";
		programList = getHibernateTemplate().find(queryStr);
		
		if(programList != null && programList.size() > 0){
			return programList.get(0);
		}
		else return null;
	}
	
	public LoyaltyProgram findMobileBasedProgramByUserId(Long userId) {
		
		List<LoyaltyProgram> programList = null;
		String queryStr = " FROM LoyaltyProgram WHERE userId = "+userId+" AND membershipType = '"+OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE+"'";
		programList = getHibernateTemplate().find(queryStr);
		
		if(programList != null && programList.size() > 0){
			return programList.get(0);
		}
		else return null;
	}
	
	public List<LoyaltyProgram> findByOrgIdAndStatus(Long orgId, String status, String mbershipType) {
    	try{
    		
    		String query = " FROM LoyaltyProgram WHERE orgId = "+orgId+" AND membershipType = '"+mbershipType+"'"
    				+ " AND status ='"+status+"'" ;
    		
    		List list = getHibernateTemplate().find(query);
    		if( list != null && list.size()>0)
    			return list;
    		else
    			return null;
    	}catch(Exception e){
    		logger.error("Exception...",e);
    		return null;
    	}
    	
    }
	
	/*public List<LoyaltyProgram> findProgramsBy(Long userId, String status) {
    	try{
    		
    		String query = " FROM LoyaltyProgram WHERE userId = "+userId+" AND membershipType ='"
    				+ OCConstants.LOYALTY_MEMBERSHIP_TYPE_CARD+"'"
    				+ " AND status ='"+status+"'" ;
    		
    		List list = getHibernateTemplate().find(query);
    		if( list != null && list.size()>0)
    			return list;
    		else
    			return null;
    	}catch(Exception e){
    		logger.error("Exception...",e);
    		return null;
    	}
    	
    }*/
	public List<LoyaltyProgram> findProgramsBy(Long userId, String status, String membershipType) {
    	try{
    		
    		String query = " FROM LoyaltyProgram WHERE userId = "+userId+" AND membershipType ='"
    				+membershipType+"'"
    				+ " AND status ='"+status+"'" ;
    		
    		List list = getHibernateTemplate().find(query);
    		if( list != null && list.size()>0)
    			return list;
    		else
    			return null;
    	}catch(Exception e){
    		logger.error("Exception...",e);
    		return null;
    	}
    	
    }
	public LoyaltyProgram findProgramByUserId(Long userId, String status, String programType) {
		
		List<LoyaltyProgram> programList = null;
		String queryStr = " FROM LoyaltyProgram WHERE userId = "+userId+" AND status = '"+status+"' AND programType = '"+programType+"'";
		programList = getHibernateTemplate().find(queryStr);
		
		if(programList != null && programList.size() > 0){
			return programList.get(0);
		}
		else return null;
	}
	
	public List<Long> findAllProgramsIds(){
		
		String queryStr = " SELECT programId FROM LoyaltyProgram ";
		return getHibernateTemplate().find(queryStr);
		
	}

	public List<LoyaltyProgram> fetchAllPrograms() {
		try {
			List<LoyaltyProgram> programList = null;
			String queryStr = " FROM LoyaltyProgram WHERE userId IN(SELECT userId FROM Users WHERE "
					+ "Date(now())<=Date(packageExpiryDate) AND enabled=true  ) "
					+ "AND status = '" +OCConstants.LOYALTY_PROGRAM_STATUS_ACTIVE+ "' "
							+ "AND (rewardExpiryFlag='Y' OR giftAmountExpiryFlag='Y') AND rewardType != '"+OCConstants.REWARD_TYPE_PERK+"' ";
			programList = executeQuery(queryStr);
			
			if(programList != null && programList.size() > 0){
				return programList;
			}
			else return null;
		} catch (Exception e) {
			logger.error("Exception ", e);
			return null;
		}
	}
	public List<LoyaltyProgram> fetchAllMemberExpPrograms() {
		try {
			List<LoyaltyProgram> programList = null;
			String queryStr = " FROM LoyaltyProgram WHERE userId IN(SELECT userId FROM Users WHERE "
					+ "Date(now())<=Date(packageExpiryDate) AND enabled=true  ) AND "
					+ "status = '" +OCConstants.LOYALTY_PROGRAM_STATUS_ACTIVE+ "' AND (membershipExpiryFlag='Y' OR giftMembrshpExpiryFlag='Y')  ";
			programList = executeQuery(queryStr);
			
			if(programList != null && programList.size() > 0){
				return programList;
			}
			else return null;
		} catch (Exception e) {
			logger.error("Exception ", e);
			return null;
		}
	}
	public List<LoyaltyProgram> fetchAllRewardExpiryAutoCommEnabledPrograms() {
		try {
			List<LoyaltyProgram> programList = null;
			String queryStr = " SELECT DISTINCT lp FROM LoyaltyProgram lp, LoyaltyAutoComm la WHERE lp.programId=la.programId AND lp.userId IN(SELECT userId FROM Users WHERE "
					+ "Date(now())<=Date(packageExpiryDate) AND enabled=true  ) "
					+ " AND (lp.rewardExpiryFlag='Y' OR lp.giftAmountExpiryFlag='Y') AND "
					+ "(la.rewardExpiryEmailTmpltId is NOT NULL OR la.rewardExpirySmsTmpltId IS NOT NULL "
					+ "OR la.giftAmtExpiryEmailTmpltId IS NOT NULL OR la.giftAmtExpirySmsTmpltId IS NOT NULL) AND "
					+ "lp.status = '" +OCConstants.LOYALTY_PROGRAM_STATUS_ACTIVE+ "' AND lp.rewardType != '"+OCConstants.REWARD_TYPE_PERK+"'";
			programList = executeQuery(queryStr);
			
			if(programList != null && programList.size() > 0){
				return programList;
			}
			else return null;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return null;
		}
	}
	
	public List<LoyaltyProgram> fetchAllMemberExpiryAutoCommEnabledPrograms() {
		try {
			List<LoyaltyProgram> programList = null;
			String queryStr = " SELECT DISTINCT lp FROM LoyaltyProgram lp, LoyaltyAutoComm la WHERE lp.programId=la.programId AND "
					+ "lp.userId IN(SELECT userId FROM Users WHERE "
					+ "Date(now())<=Date(packageExpiryDate) AND enabled=true  ) AND "
					+ "(lp.membershipExpiryFlag='Y' OR lp.giftMembrshpExpiryFlag='Y') AND "
					+ "(la.mbrshipExpiryEmailTmpltId is NOT NULL OR la.mbrshipExpirySmsTmpltId IS NOT NULL OR "
					+ "la.giftMembrshpExpiryEmailTmpltId IS NOT NULL OR la.giftMembrshpExpirySmsTmpltId IS NOT NULL ) AND "
					+ "lp.status = '" +OCConstants.LOYALTY_PROGRAM_STATUS_ACTIVE+ "' ";
			programList = executeQuery(queryStr);
			
			if(programList != null && programList.size() > 0){
				return programList;
			}
			else return null;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return null;
		}
	}
	
	public List<LoyaltyProgram> getAllProgramsListByUserId(Long userId) {
		List<LoyaltyProgram> programList = null; 
		String queryStr = " FROM LoyaltyProgram WHERE userId = "+userId.longValue()+ 
				" AND status != '"+OCConstants.LOYALTY_PROGRAM_STATUS_DRAFT+"'";
		programList = getHibernateTemplate().find(queryStr);
		
		if(programList != null && programList.size() > 0){
			return programList;
		}
		else return null;
		
		
	}
	
	public List<LoyaltyProgram> fetchAllPerkPrograms() {
		try {
			List<LoyaltyProgram> programList = null;
			String queryStr = " FROM LoyaltyProgram WHERE userId IN(SELECT userId FROM Users WHERE "
					+ "Date(now())<=Date(packageExpiryDate) AND enabled=true  ) AND status = '" +OCConstants.LOYALTY_PROGRAM_STATUS_ACTIVE+ "' AND rewardType = '"+OCConstants.REWARD_TYPE_PERK+"'  ";
			programList = executeQuery(queryStr);
			logger.info("fetching all perk programs >>>>"+queryStr);
			
			if(programList != null && programList.size() > 0){
				return programList;
			}
			else return null;
		} catch (Exception e) {
			logger.error("Exception ", e);
			return null;
		}
	}
	
	public List<LoyaltyProgram> getAllCardBasedProgramsListByUserId(Long userId) {
		List<LoyaltyProgram> programList = null; 
		String queryStr = " FROM LoyaltyProgram WHERE userId = "+userId.longValue()+ 
				" AND status != '"+OCConstants.LOYALTY_PROGRAM_STATUS_DRAFT+"' AND membershipType = '"+OCConstants.LOYALTY_MEMBERSHIP_TYPE_CARD+"'";
		programList = getHibernateTemplate().find(queryStr);
		
		if(programList != null && programList.size() > 0){
			return programList;
		}
		else return null;
		
		
	}
	
	public List<LoyaltyProgram> fetchUserPrograms(Long userID) {
		try {
			List<LoyaltyProgram> programList = null;
			String queryStr = " FROM LoyaltyProgram WHERE userId="+userID+" AND status = '" +OCConstants.LOYALTY_PROGRAM_STATUS_ACTIVE+ "' AND (rewardExpiryFlag='Y' OR giftAmountExpiryFlag='Y') ";
			programList = executeQuery(queryStr);

		if(programList != null && programList.size() > 0){
			return programList;
		}
		else return null;
		} catch (Exception e) {
			logger.error("Exception ", e);
			return null;
		}
	}
	
	public List<Long> fetchAllActivePrograms(Long userId){
		try {
			List<Long> list = null;
			String queryStr = "SELECT programId FROM LoyaltyProgram WHERE userId="+userId+" AND status = '" +OCConstants.LOYALTY_PROGRAM_STATUS_ACTIVE+ "' AND rewardType != '"+OCConstants.REWARD_TYPE_PERK+"' ";
			logger.info("Query >>> "+queryStr);
			list = executeQuery(queryStr);
			
			if(list != null && list.size() > 0 ) {
				return list;
			}else
				return null;
		}
		catch(Exception e) {
			logger.error("**Exception :: "+e);
			return null;
		}
	}
	
	public List<LoyaltyProgram> fetchAllActiveProgramsNames(Long userId){
		try {
			List<LoyaltyProgram> list = null;
			String queryStr = "FROM LoyaltyProgram WHERE userId="+userId+" AND status = '" +OCConstants.LOYALTY_PROGRAM_STATUS_ACTIVE+ "' ";
			logger.info("Query >>> "+queryStr);
			list = executeQuery(queryStr);
			
			if(list != null && list.size() > 0 ) {
				return list;
			}else
				return null;
		}
		catch(Exception e) {
			logger.error("**Exception :: "+e);
			return null;
		}
	}
	
	public LoyaltyProgram getProgramIdByName(String prgmName,Long userId) {
		
		if(prgmName.contains("'")) {
			prgmName = prgmName.replace("'", "''");
		}	
			
		String queryStr = " FROM LoyaltyProgram WHERE programName='"+prgmName+"' AND userId="+userId+" ";
		
		logger.info("getProgramIdByName>>"+queryStr);
        List<LoyaltyProgram> programList = getHibernateTemplate().find(queryStr);
		
		if(programList != null && programList.size() > 0){
			return programList.get(0);
		}
		else return null;
		
	}
	
}
