package org.mq.optculture.data.dao;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import org.mq.marketer.campaign.beans.LoyaltyProgram;
import org.mq.marketer.campaign.dao.AbstractSpringDao;
import org.mq.marketer.campaign.dao.AbstractSpringDaoForDML;
import org.mq.marketer.campaign.general.Constants;
import org.mq.optculture.exception.LoyaltyProgramException;
import org.mq.optculture.utils.OCConstants;

public class LoyaltyProgramDaoForDML extends AbstractSpringDaoForDML{

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
	public LoyaltyProgramDaoForDML() {
	}
	
	public void saveOrUpdate(LoyaltyProgram loyaltyProgram) {
		super.saveOrUpdate(loyaltyProgram);
	}
	
	 public void delete(LoyaltyProgram loyaltyProgram) {
         super.delete(loyaltyProgram);
     }
	
	/*public List<LoyaltyProgram> fetchProgramsByProgramIdStr(String programIdStr) throws LoyaltyProgramException{
		
		List<LoyaltyProgram> listOfPrograms = null;
		
		try{
			
			String queryStr = " FROM LoyaltyProgram WHERE programId IN ("+programIdStr+")";
			logger.info("fetchProgramsByProgramIdStr = "+queryStr);
			return (List<LoyaltyProgram>)getHibernateTemplate().find(queryStr);
			
		}catch(Exception e){
			throw new LoyaltyProgramException("fetch programs failed", e);
		}
	}
	
	public List<LoyaltyProgram> getProgListByUserId(Long userId) {
		List<LoyaltyProgram> progList = getHibernateTemplate().find(" FROM LoyaltyProgram WHERE userId = "+userId.longValue()+" ORDER BY programName");
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
		
		String queryStr = "FROM LoyaltyProgram WHERE programId="+ prgmId.longValue(); 
		
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
	
	public List<LoyaltyProgram> findProgramsBy(Long userId, String status) {
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
    	
    }
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
	
	public List<LoyaltyProgram> getAllCardBasedProgramsListByUserId(Long userId) {
		List<LoyaltyProgram> programList = null; 
		String queryStr = " FROM LoyaltyProgram WHERE userId = "+userId.longValue()+ 
				" AND status != '"+OCConstants.LOYALTY_PROGRAM_STATUS_DRAFT+"' AND membershipType = '"+OCConstants.LOYALTY_MEMBERSHIP_TYPE_CARD+"'";
		programList = getHibernateTemplate().find(queryStr);
		
		if(programList != null && programList.size() > 0){
			return programList;
		}
		else return null;
		
		
	}*/
}
