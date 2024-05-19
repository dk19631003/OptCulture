package org.mq.optculture.data.dao;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import org.mq.captiway.scheduler.beans.LoyaltyProgram;
import org.mq.captiway.scheduler.dao.AbstractSpringDao;
import org.mq.captiway.scheduler.utility.Constants;
import org.mq.optculture.utils.OCConstants;

public class LoyaltyProgramDao extends AbstractSpringDao{

	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	
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
	
	/*public void saveOrUpdate(LoyaltyProgram loyaltyProgram) {
		super.saveOrUpdate(loyaltyProgram);
	}
	
	 public void delete(LoyaltyProgram loyaltyProgram) {
         super.delete(loyaltyProgram);
     }*/
	
	public List<LoyaltyProgram> fetchProgramsByProgramIdStr(String programIdStr) {
		
		List<LoyaltyProgram> listOfPrograms = null;
		
		try{
			
			String queryStr = " FROM LoyaltyProgram WHERE programId IN ("+programIdStr+")";
			logger.info("fetchProgramsByProgramIdStr = "+queryStr);
			listOfPrograms = getHibernateTemplate().find(queryStr);
			
		}catch(Exception e){
			logger.error("fetch programs failed", e);
		}
		return listOfPrograms;
	}
	
	public List<LoyaltyProgram> getProgListByUserId(Long userId) {
		List<LoyaltyProgram> progList = getHibernateTemplate().find(" FROM LoyaltyProgram WHERE userId = "+userId.longValue()+" ORDER BY programName");
		if(progList != null && progList.size() > 0)return progList;
		else return null;
	}

	public LoyaltyProgram findById(Long prgmId) {
		List<LoyaltyProgram> list = getHibernateTemplate().find("FROM LoyaltyProgram WHERE programId="+ prgmId.longValue() );
    	
    	if(list != null && list.size() > 0) {
    		return list.get(0);
    	}
    	return null;
	}
	
	public LoyaltyProgram findByIdAndUserId(Long prgmId, Long userId) {
		
		String queryStr = "FROM LoyaltyProgram WHERE programId="+ prgmId.longValue()+" AND userId ="+userId; 
		
		List<LoyaltyProgram> list = getHibernateTemplate().find(queryStr);
    	
    	if(list != null && list.size() > 0) {
    		return list.get(0);
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
			String status, int startIndex, int size) {
		String query=null;
		List<LoyaltyProgram> progList=null;
		try{
			//String userIdsStr = Utility.getUserIdsAsString(userIds);
			logger.info("status"+status);
		if(status.equals("All")) {
			logger.info("in all"+status);
			query="FROM LoyaltyProgram WHERE userId =" + userId.longValue()+"  order by createdDate desc";
		  }else {	
		
			query="FROM LoyaltyProgram WHERE userId =" + userId.longValue()+ " and status like '" + status + "' order by createdDate desc" ;
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
		programList = getHibernateTemplate().find(queryStr);
		
		if(programList != null && programList.size() > 0){
			return programList.get(0);
		}
		else return null;
		
	}
	
	
	
	public int getProgramCount(Long userId,	String key) {  
		String subQry = "SELECT COUNT(programId) FROM  LoyaltyProgram  WHERE userId=" + userId.longValue()+ "" +
				" AND status != '"+OCConstants.LOYALTY_PROGRAM_STATUS_DRAFT+"'";

		if(key != null){

			subQry += " AND programName LIKE '%"+key+"%'";
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

			subQry += " AND programName LIKE '%"+key+"%'";
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
	
	public List<LoyaltyProgram> findByuserIdAndStatus(Long userId, String status, String mbershipType) {
    	try{
    		
    		String query = " FROM LoyaltyProgram WHERE userId = "+userId+" AND membershipType = '"+mbershipType+"'"
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
	
	public LoyaltyProgram findProgramByUserId(Long userId, String status, String type) {
		
		List<LoyaltyProgram> programList = null;
		String queryStr = " FROM LoyaltyProgram WHERE userId = "+userId+" AND status = '"+status+"' AND membershipType = '"+type+"'";
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
	
	public List<LoyaltyProgram> fetchAllPerkPrograms() {
		try {
			List<LoyaltyProgram> programList = null;
			String queryStr = " FROM LoyaltyProgram WHERE userId IN(SELECT userId FROM Users WHERE "
					+ "Date(now())<=Date(packageExpiryDate) AND enabled=true  ) AND status = '" +OCConstants.LOYALTY_PROGRAM_STATUS_ACTIVE+ "' AND rewardType = '"+OCConstants.REWARD_TYPE_PERK+"'  ";
			
			logger.debug("fetching all perk programs >>>>"+queryStr);
			programList = getHibernateTemplate().find(queryStr);
			logger.info("list of perks programs size"+programList.size());
			
			if(programList != null && programList.size() > 0){
				return programList;
			}
			else return null;
		} catch (Exception e) {
			logger.error("Exception ", e);
			return null;
		}
	}

	public List<LoyaltyProgram> fetchAllPrograms() {
		List<LoyaltyProgram> programList = null;
		String queryStr = " FROM LoyaltyProgram WHERE status = '" +OCConstants.LOYALTY_PROGRAM_STATUS_ACTIVE+ "' ";
		programList = getHibernateTemplate().find(queryStr);
		
		if(programList != null && programList.size() > 0){
			return programList;
		}
		else return null;
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
	
	public List<LoyaltyProgram> findListByUserIdAndStatus(Long userId){
		List<LoyaltyProgram> programList = null;
		String qry = "FROM LoyaltyProgram WHERE userId = "+userId.longValue()+" AND status = '"+OCConstants.LOYALTY_PROGRAM_STATUS_ACTIVE+"' ";
		programList = getHibernateTemplate().find(qry);
		if(programList != null && programList.size() > 0){
			return programList;
		}
		else return null;
	}
}
