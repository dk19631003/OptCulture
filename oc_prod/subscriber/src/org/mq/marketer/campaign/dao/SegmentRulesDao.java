package org.mq.marketer.campaign.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.mq.marketer.campaign.beans.MailingList;
import org.mq.marketer.campaign.beans.SegmentRules;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.beans.UsersDomains;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.Utility;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;


public class SegmentRulesDao extends AbstractSpringDao{
	
	public SegmentRulesDao() {}
	
	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	public SegmentRules find(Long id) {
		return (SegmentRules) super.find(SegmentRules.class, id);
	}

	public List<SegmentRules> findAll() {
		return super.findAll(SegmentRules.class);
	}
	
	
	public List<SegmentRules> findAll(int start, int range) {

		String qry = "FROM SegmentRules";
		
		return executeQuery(qry, start, range);
	
	
	}
	
	
	public List<SegmentRules> findByUser(Set<Long> userIds) {
		
		String userIdsStr = Utility.getUserIdsAsString(userIds);
		
		return getHibernateTemplate().find("FROM SegmentRules WHERE userId IN ("+userIdsStr+")");
		
		
	}
	
	
	  /**
     * called in various Controllers and is used to retrieve the
     * the segment objects which are in current session.</BR>
     * added for sharing.
     * @param mlId
     * @return list of MailingList object of an user
     */
    public List<SegmentRules> findByIds(Set<Long> segmentIdsSet) {
    	try {
    		if(segmentIdsSet == null ) return null;
    		
    		
    		String segmentIds = Utility.getIdsAsString(segmentIdsSet);
    		
    		if(segmentIds.isEmpty() ) return null;
    		
			List<SegmentRules> segList = null;
			segList = getHibernateTemplate().find("SELECT DISTINCt s  FROM SegmentRules s WHERE  segRuleId in ("+segmentIds+")");
			if(segList.size()>0){
				return segList;
			}
			else return null;
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
			return null;
		}
    	
    }
    public List<SegmentRules> searchBy(Set<Long> segmentIdsSet,String searchStr) {
    	try {
    		if(segmentIdsSet == null ) return null;
    		
    		
    		String segmentIds = Utility.getIdsAsString(segmentIdsSet);
    		
    		if(segmentIds.isEmpty() ) return null;
    		
			List<SegmentRules> segList = null;
			String subQry = "";
			if(searchStr != null && !searchStr.isEmpty()){
				subQry = " AND segRuleName LIKE '%"+ searchStr +"%' ";
			}
			segList = getHibernateTemplate().find("SELECT DISTINCt s  FROM SegmentRules s WHERE  segRuleId in ("+segmentIds+") "+subQry);
			if(segList.size()>0){
				return segList;
			}
			else return null;
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
			return null;
		}
    	
    }
	
    public List<SegmentRules> findByIds(Set<Long> segmentIdsSet, String type, int firstResult, int maxResults, String segmentName, String fromDate, String endDate) {
	
		if(segmentIdsSet == null ) return null;
		
		
		String segmentIds = Utility.getIdsAsString(segmentIdsSet);
			
		if(segmentIds.isEmpty() ) return null;
		List<SegmentRules> segList = null;

		String subQry = "";
		if(segmentName != null && !segmentName.isEmpty()){
			subQry = " AND segRuleName LIKE '%"+ segmentName +"%' ";
		}
		else if(fromDate !=null && !fromDate.isEmpty() && endDate != null && !endDate.isEmpty()){
			subQry = " AND createdDate BETWEEN '"+ fromDate +"' AND '"+ endDate +"' ";
		}
		
		String qry = "FROM SegmentRules WHERE segRuleId IN ("+segmentIds+") AND segmentType='"+type+"' " + subQry + " ORDER BY modifiedDate desc ";
	
		segList = executeQuery(qry, firstResult, maxResults);
		
		if(segList.size()>0){
			return segList;
		}
		else return null;
		/*String userIdsStr = Utility.getUserIdsAsString(userIds);
		return executeQuery("FROM SegmentRules WHERE userId IN ("+userIdsStr+") AND segmentType='"+type+"' ORDER BY createdDate desc ", firstResult, maxResults);
		*///return getHibernateTemplate().efind("FROM SegmentRules WHERE userId IN ("+userIdsStr+")");
		
		
	}
    
	
	public List<SegmentRules> findByUser(Set<Long> userIds, String type, int firstResult, int maxResults) {
		
		String userIdsStr = Utility.getUserIdsAsString(userIds);
		return executeQuery("FROM SegmentRules WHERE userId IN ("+userIdsStr+") AND segmentType='"+type+"' ORDER BY modifiedDate desc ", firstResult, maxResults);
		//return getHibernateTemplate().efind("FROM SegmentRules WHERE userId IN ("+userIdsStr+")");
		
		
	}
	
	public int findCountByUser(Set<Long> userIds) {
		
	String userIdsStr = Utility.getUserIdsAsString(userIds);
		
		return ((Long)getHibernateTemplate().find("SELECT COUNT(segRuleId) " +
				"FROM SegmentRules WHERE userId IN ("+userIdsStr+")").get(0)).intValue();
		
		
		
	}
	
	
	public int findCountByIds(Set<Long> segIds, String segmentName, String fromDate, String endDate) {
		String subQry = "";
		
		if(segmentName != null && !segmentName.isEmpty()){
			subQry = " AND segRuleName LIKE '%"+ segmentName +"%' ";
		}
		else if(fromDate !=null && !fromDate.isEmpty() && endDate != null && !endDate.isEmpty()){
			subQry = " AND createdDate BETWEEN '"+ fromDate +"' AND '"+ endDate +"' ";
		}
		String segIdsStr = Utility.getIdsAsString(segIds);
		if(segIdsStr.isEmpty() ) return 0;
			
		String qry = "SELECT COUNT(segRuleId) " +
				" FROM SegmentRules WHERE segRuleId IN ("+segIdsStr+")" +subQry;
		
			return ((Long)getHibernateTemplate().find(qry).get(0)).intValue();
			
			
			
		}
	
	
	public List<SegmentRules> findByUser(Long userId) {
		
		//String userIdsStr = Utility.getUserIdsAsString(userIds);
		
		return getHibernateTemplate().find("FROM SegmentRules WHERE userId IN ("+userId+")");
		
		
	}
	public List<SegmentRules> findAllByCurrentUser(Long currUserId) {
		
		String qry = "FROM SegmentRules WHERE userId ="+currUserId.longValue()+"";
		
		return getHibernateTemplate().find(qry);
		
	}
	
	
	public List<Long> findAllIdsByCurrentUser(Long currUserId) {
		
		String qry = "SELECT segRuleId FROM SegmentRules WHERE userId IN("+currUserId+")";
		
		return getHibernateTemplate().find(qry);
		
	}
	
	public List<SegmentRules> findAllBySharedUser(Long sharedUserId) {
		
		String qry = "FROM SegmentRules WHERE userId ="+sharedUserId;
		
		return getHibernateTemplate().find(qry);
		
	}
	
	/**
	 * Checks for theSegmentetion Rule exist with the segRuleName for that userId
	 * 
	 * @param segRuleName
	 * @param userId
	 * @return - boolean : Returns true if Campaign exists with the provided name for the user, else returns false 
	 */
	public boolean checkName(String segRuleName,Long userId){
		List list = getHibernateTemplate().find("From SegmentRules where segRuleName = '" + segRuleName + "' and userId= " + userId);
		if(list.size()>0){
			return true;
		}else{
			return false;
		}
		
	}
	
	public List<SegmentRules> findById(String segRuleId) {
		
		if(segRuleId == null ) return null;
		
		List<SegmentRules> segList = getHibernateTemplate().find("FROM SegmentRules WHERE segRuleId in("+segRuleId+")");
		
		if(segList.size()>0) {
			return segList;
		
		}
		return null;
	}
	
	public List<SegmentRules> findAllBySegType(String type, Long userId) {
		
		String qry = "FROM SegmentRules WHERE userId="+ userId + " AND segmentType='"+ type +"'";
		return getHibernateTemplate().find(qry);
		
		
		
		
	}
	
	public List<Map<String, Object>> executeJdbcQueryForList(String qry) {
		try {
			
			return jdbcTemplate.queryForList(qry);
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			logger.error(" Exception : ",(Throwable)e);
			
		}
		return null;
	}
	
	
	//added for sharing
	/**
	 * will give the non shared segments of the users
	 */
public List<Long> findNonSharedSegments(Long userID, String sharedSegmentIds) {
		
		String subQry = "";
		
		if(sharedSegmentIds != null && sharedSegmentIds.length() > 0 ) {
			
			
			subQry = " AND segRuleId NOT IN("+sharedSegmentIds+")";
		}
		
		List<Long> segmentList = getHibernateTemplate().find("SELECT segRuleId FROM SegmentRules WHERE userId="+userID.longValue()+subQry);
				
		
		if(segmentList != null && segmentList.size() > 0) {
			
			return segmentList;
		}else{
			return null;
		}
		
	}
	



public List<UsersDomains> findSharedToDomainsByListID(Long segRuleId) {
	
	String Query ="SELECT  DISTINCT ud FROM SegmentRules s JOIN s.sharedToDomain  ud WHERE s.segRuleId IN("+segRuleId.longValue()+")";//+subQry;
	return executeQuery(Query);
	
	
	
	
	
	
}
public List<String> findStatusBySegment(String campId,String List, Long user_id, String type)
{
	try {
	    if(type.equals("campaign"))
	    {
		logger.debug("Ids for campaign are :" + List);
		String queryStr = "SELECT distinct mc.campaign_id FROM mlists_campaigns mc, campaign_schedule cs WHERE cs.user_id ="+user_id+" AND  mc.list_id IN('" + List +"') " +
				" AND cs.campaign_id IN("+campId+") AND cs.campaign_id=mc.campaign_id  AND cs.status=0";
		logger.info("--------->"+queryStr);	
		logger.debug("JdbcTemplate : " + jdbcTemplate);
		
		//List<String> list =executeQuery(queryStr);
			List<String> list = jdbcTemplate.query(queryStr, new RowMapper(){

				@Override
				public Object mapRow(ResultSet arg0, int arg1)
						throws SQLException {
					logger.debug("arg0 :" + arg0 + " arg1 :" + arg1);
					return arg0!=null?arg0.getString("campaign_id"):"";
				} });
			return list;
	    }else if(type.equals("SMS"))
	    {
	    	logger.debug("Ids for sms are :" + List);
	    	String queryStr="SELECT distinct msc.sms_campaign_id FROM mlists_sms_campaigns msc, SMS_campaign_schedule scs  " +
	    			"WHERE scs.user_id =" + user_id +" AND msc.list_id IN('"+List+"') " +
	    							"AND scs.sms_campaign_id IN("+campId+") AND msc.sms_campaign_id=scs.sms_campaign_id AND scs.status=0";
	    	logger.info("--------->"+queryStr);
	    	logger.debug("JdbcTemplate : " + jdbcTemplate);
			List<String> list = jdbcTemplate.query(queryStr, new RowMapper(){

				@Override
				public Object mapRow(ResultSet arg0, int arg1)
						throws SQLException {
					logger.debug("arg0 :" + arg0 + " arg1 :" + arg1);
					return arg0!=null?arg0.getString("sms_campaign_id"):"";
				} });
			return list;
	    }
	}
	 catch (Exception e) {
		 e.printStackTrace();
		logger.info(" ** Exception :", (Throwable) e);
	}
	return null;
}


}
