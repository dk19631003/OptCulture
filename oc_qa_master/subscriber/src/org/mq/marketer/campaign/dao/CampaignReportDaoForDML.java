package org.mq.marketer.campaign.dao;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.mq.marketer.campaign.beans.CampaignReport;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.general.Constants;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

@SuppressWarnings({ "unchecked", "unused" })
public class CampaignReportDaoForDML extends AbstractSpringDaoForDML {

    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
 
	public CampaignReportDaoForDML() {}
	
	 private JdbcTemplate jdbcTemplate;
	 
	 
 	
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	/*private CampaignSentDao campaignSentDao = null;
	
	public CampaignSentDao getCampaignSentDao() {
		return campaignSentDao;
	}
	
	public void setCampaignSentDao(CampaignSentDao campaignSentDao) {
		this.campaignSentDao = campaignSentDao;
	}*/

	/*public CampaignReport find(Long id) {
        return (CampaignReport) super.find(CampaignReport.class, id);
    }

    public void saveOrUpdate(CampaignReport CampaignReport) {
        super.saveOrUpdate(CampaignReport);
    }

    public void delete(CampaignReport CampaignReport) {
        super.delete(CampaignReport);
    }

    public List findAll() {
        return super.findAll(CampaignReport.class);
    }
    
    public void deleteByCollection(Collection campaignReportCollection){
    	getHibernateTemplate().deleteAll(campaignReportCollection);
    }*/
    
    /*public void deleteByCampaign(String campaignName,String userName){
    	List campReport = getHibernateTemplate().find("from CampaignReport where camapaignName = '" +
    			campaignName + "' and users.userName ='" + userName + "'");
    	deleteByCollection(campReport);
    }
    
    public List<CampaignReport> getRecentCampignReportList(Long userId){
    	List campaignReportList = null;
    	try{
    		String qry = "from CampaignReport where user =" + userId +
    				" and status in ('running','sent') AND sourceType LIKE 'CampaignSchedule%' order by sentDate desc";
    		
    		campaignReportList = executeQuery(qry, 0, 7);
    		
    	}catch (Exception e){
			logger.error(" ** Exception : Error while getting the recent campaigns report"+ e +" **");
		}
    	return campaignReportList;
    }
    
    public List<String> getCampaignList(Long userId){
    	
    	return getHibernateTemplate().find("select distinct campaignName from CampaignReport" +
    			" where user =" + userId + " and  status='sent' ");
    }
    
    public List<String> getCampaignList(String userId, String sourceType){
    	String query = "";
    	if(sourceType.equalsIgnoreCase("--All--")){
    		query = "select distinct campaignName from CampaignReport" +" where user IN(" +
    					userId + ") and  status='sent' ";
    	}
    	else {
    		query = "select distinct campaignName from CampaignReport" +
			" where user IN(" + userId + ") and  sourceType like '"+sourceType+"%' "+"and  status='Sent' ";
    	}
    	return getHibernateTemplate().find(query);
    }
    
    public List<Object[]> getSentCampaignDetail(Long userId){
		
    	try {
			return getHibernateTemplate().find("select sum(sent),sum(unsubscribes)," +
					"sum(bounces),sum(opens),sum(clicks) from CampaignReport where " +
					"user = " + userId + " and status in('sent')");
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
			return null;
		}
    	

    	
    	CampaignReport campaignsReport = new CampaignReport();
		try{
			Date fromDate = new Date();
			fromDate.setMonth(fromDate.getMonth() - 3);
			
			String qry = "select sum(sent),sum(opens),sum(clicks),sum(unsubscribes)," +
			"sum(spams),sum(bounces) from campaign_report where user_id = "+
			userId + " and status in('sent')";
			
			logger.info("the query to be executed is======>"+qry);
			
			List<Object[]> catList = jdbcTemplate.query(qry,new RowMapper(){
    			Object[] obj;
				@Override
				public Object mapRow(ResultSet rs, int rowNum)
						throws SQLException {
					obj = new Object[6];
					obj[0] = rs.getLong(1);
					obj[1] = rs.getLong(2);
					obj[2] = rs.getLong(3);
					obj[3] = rs.getLong(4);
					obj[4] = rs.getLong(5);
					obj[5] = rs.getLong(6);
					
					return obj;
				}
    			
    		});
			
			return catList;
			
			
		}catch(Exception e){
			logger.error("** Exception "+e.getMessage()+" **");
			return null;
		}
	
    	
    	
    	
    	
    	
    	
    }
    
    //added newly
    public List<Object[]> getSentCampaignDetail(String userId, String str){
		
    	try {
			return getHibernateTemplate().find("select sum(sent),sum(opens),sum(clicks),sum(unsubscribes)," +
					"sum(spams),sum(bounces) from CampaignReport where " +
					"user IN(" + userId + ") and status in('sent')");
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
			return null;
		}
    }
    
 	public List<CampaignReport> getAllReports(String userId,String fromDateStr,String toDateStr,int startIndex, int endIndex,String orderby_colName,String desc_Asc) throws Exception{
 		
 		return getCampaignReports(null, userId, fromDateStr, toDateStr, false, null, startIndex, endIndex,orderby_colName,desc_Asc);
    }
 	
 	public List<CampaignReport> getAllReports(String userId,String fromDateStr,String toDateStr, String sourceType, int startIndex, int endIndex,String orderby_colName,String desc_Asc) throws Exception{
 		
 		return getCampaignReports(null, userId, fromDateStr, toDateStr, false, sourceType, startIndex, endIndex,orderby_colName,desc_Asc);
    }
 	
 	
public List<CampaignReport> getReportByCampaignName(String campaignName,Long userId) throws Exception {
    	
    	return getCampaignReports(campaignName, userId, null, null, false);
    }
 	
public List<CampaignReport> getReportsByCampaignName(String campaignName,Long userId)  {
	
	try {
	List<CampaignReport> repList = null;
	
	String qry  = " FROM CampaignReport WHERE  campaignName ='" + campaignName + "'" +
					" AND status ='Sent' AND  sourceType = 'CampaignSchedule' ORDER BY  sentDate DESC";
	
	repList = executeQuery(qry);
	if(repList != null && repList.size() > 0) return repList;
	
	else return null;
	
	
	}catch (Exception e) {
		// TODO Auto-generated catch block
		logger.error("Exception ::" , e);
		return null;
	}
	
	
	
}

 	
 	
  
    public List<CampaignReport> getReportByCampaignName(String campaignName,Long userId, int startIndex, int endIndex) throws Exception {
    	
    	return getCampaignReports(campaignName, userId, null, null, false, startIndex, endIndex);
    }

    public List<CampaignReport> getReportsByCampaignName(String campaignName, 
    		String userId, String fromDateStr, String toDateStr, boolean isLike, String sourceType, int startIndex, int endIndex,String orderby_colName,String desc_Asc) throws Exception {
    	
    	
    		return getCampaignReports(campaignName, userId, fromDateStr, toDateStr, isLike, sourceType, startIndex, endIndex,orderby_colName,desc_Asc);
    	
    }
    this method is called from reports to get the campreports specific to the user (in case of dashboard) 
    public List<CampaignReport> getReportByCampaignName(Long userId,String campaignName) throws Exception{
    	String queryStr="from CampaignReport where campaignName='"+campaignName+"'and user="+userId;
    	logger.debug("===========>"+queryStr);
    	return getHibernateTemplate().find(queryStr);
    	
    }
    
    public List<CampaignReport> getCampaignReports(String campaignName, 
    		String userId, String fromDateStr, String toDateStr, boolean isLike, String sourceType, int startIndex, int endIndex,String orderby_colName,String desc_Asc) throws Exception {
    	
    	String checkCon = "";
    	
    	if(campaignName != null) {
    		checkCon += " campaignName " + (isLike? ("like '%"+ campaignName +"%' ") : "='" + campaignName + "'") + " AND ";
    	}
    	
 		if(userId != null) {
 			checkCon += " user IN (" + userId + ") AND";
 		} 
 		
 		if(fromDateStr != null && toDateStr != null) {
 			checkCon += " sentDate between '" + fromDateStr + "' AND '" + toDateStr + "' AND ";
 		}
 		if(sourceType != null && !sourceType.equalsIgnoreCase("--All--")) {
 			checkCon += " sourceType like '"+sourceType+"%'  AND ";
 		}
    	
 		String queryStr = " FROM CampaignReport WHERE " + checkCon + " status IN ('sent') ORDER BY  "+orderby_colName+" "+desc_Asc;
 		logger.debug("Query  : " + queryStr);
    	
 		//return getHibernateTemplate().find(queryStr);
 		
 		return executeQuery(queryStr, startIndex, endIndex);
 		
 		
    } //getCampaignReports
    
    public List<CampaignReport> getCampaignReports(String campaignName, 
    		Long userId, String fromDateStr, String toDateStr, boolean isLike) throws Exception {
    	
    	String checkCon = "";
    	
    	if(campaignName != null) {
    		checkCon += " campaignName " + (isLike? ("like '%"+ campaignName +"%' ") : "='" + campaignName + "'") + " AND ";
    	}
    	
 		if(userId != null) {
 			checkCon += " user ='" + userId + "' AND";
 		} 
 		
 		if(fromDateStr != null && toDateStr != null) {
 			checkCon += " sentDate between '" + fromDateStr + "' AND '" + toDateStr + "' AND ";
 		}
 		String queryStr = " FROM CampaignReport WHERE " + checkCon + " status IN ('sent')";
 		logger.debug("Query : " + queryStr);
    	
 		return getHibernateTemplate().find(queryStr);
 		
 		
    } //getCampaignReports
    
    
    public List<CampaignReport> getCampaignReports(String campaignName, 
    		Long userId, String fromDateStr, String toDateStr, boolean isLike, int startIndex, int endIndex) throws Exception {
    	
    	String checkCon = "";
    	
    	if(campaignName != null) {
    		checkCon += " campaignName " + (isLike? ("like '%"+ campaignName +"%' ") : "='" + campaignName + "'") + " AND ";
    	}
    	
 		if(userId != null) {
 			checkCon += " user ='" + userId + "' AND";
 		} 
 		
 		if(fromDateStr != null && toDateStr != null) {
 			checkCon += " sentDate between '" + fromDateStr + "' AND '" + toDateStr + "' AND ";
 		}
 		String queryStr = " FROM CampaignReport WHERE " + checkCon + " status IN ('sent')";
 		logger.debug("Query : " + queryStr);
    	
 		//return getHibernateTemplate().find(queryStr);
 		
 		return executeQuery(queryStr, startIndex, endIndex);
    } //getCampaignReports
    
    
    public long getReportCountByCampaign(String campaignName,Long userId){
 
    	return ((Long)getHibernateTemplate().find("select count(*) from CampaignReport" +
    			" where campaignName ='" + campaignName + "' and user= " + userId).get(0)).longValue();
    }
    
    public List<Object[]> getCampaignConsolidatedReport(String campaignName,Long userId){
    	
    	return getHibernateTemplate().find("select sum(sent),sum(opens),sum(clicks),sum(unsubscribes)," +
    			"sum(spams),sum(bounces) from CampaignReport where user= " +
    			userId + " and campaignName ='" + campaignName + "' ");
    }
    
 public List<Object[]> getCampaignConsolidatedReport(String campaignName,Long userId, String str){
    	
    	return getHibernateTemplate().find("select sum(sent),sum(unsubscribes)," +
    			"sum(bounces),sum(opens),sum(clicks) from CampaignReport where user= " +
    			userId + " and campaignName ='" + campaignName + "' ");
    }
    
    public List<Object[]> getCampaignsInLast3Months(Long userId){
    	
    	CampaignReport campaignsReport = new CampaignReport();
		try{
			Date fromDate = new Date();
			fromDate.setMonth(fromDate.getMonth() - 3);
			return getHibernateTemplate().find("select sum(sent),sum(unsubscribes)," +
					"sum(bounces),sum(opens),sum(clicks) from CampaignReport where user = "+
					userId + " and sentDate > '" + format.format(fromDate) + "' and status in('sent')") ;
		}catch(Exception e){
			logger.error("** Exception "+e.getMessage()+" **");
			return null;
		}
	}

   //added newly 
 public List<Object[]> getCampaignsInLast3Months(String userId, String str) {
    	
    	CampaignReport campaignsReport = new CampaignReport();
		try{
			Date fromDate = new Date();
			fromDate.setMonth(fromDate.getMonth() - 3);
			
			String qry = "select sum(sent),sum(opens),sum(clicks),sum(unsubscribes)," +
			"sum(spams),sum(bounces) from campaign_report where user_id IN( "+
			userId + ") and sent_date > '" + format.format(fromDate) + "' and status in('sent')";
			
			logger.info("the query to be executed is======>"+qry);
			
			List<Object[]> catList = jdbcTemplate.query(qry,new RowMapper(){
    			Object[] obj;
				@Override
				public Object mapRow(ResultSet rs, int rowNum)
						throws SQLException {
					obj = new Object[6];
					obj[0] = rs.getLong(1);
					obj[1] = rs.getLong(2);
					obj[2] = rs.getLong(3);
					obj[3] = rs.getLong(4);
					obj[4] = rs.getLong(5);
					obj[5] = rs.getLong(6);
					
					return obj;
				}
    			
    		});
			
			return catList;
			
			
		}catch(Exception e){
			logger.error("** Exception "+e.getMessage()+" **");
			return null;
		}
	}
    
    public List<CampaignReport> getReportsByEmailNameLike(Long userId,
    		String emailName, String fromDateStr, String toDateStr) {
    	
    	return getHibernateTemplate().find("from CampaignReport  where sentDate between" +
    			" '" + fromDateStr + "' and '" + toDateStr +
    			"' and campaignName like '%"+emailName+"%' and user="+userId + " and status in ('sent')"); 
    } 
    
    
     * this method fetches the desired number of records(dashboard)  
     
    
    public List<CampaignReport> findBynumberOfReports(int startFrom, int count) {
    	
    	List<CampaignReport> numOfRepList=null;
    	try {
			
			String qry=" from CampaignReport ORDER BY sentDate desc";
			numOfRepList = executeQuery(qry, startFrom, count);

    	} catch (DataAccessResourceFailureException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
		} catch (HibernateException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
		} catch( Exception e) {
			logger.error("Exception ::" , e);
		}
		return numOfRepList;
    }//findBynumberOfReports()
    
    
     * this method fetches the desired number of records for desired user (dashboard)
     
    
    public List<CampaignReport> findByOrgIdAndUserId(Long orgId, Long userId,int startFrom,int count) {
    	
    	
    	List<CampaignReport> numOfRepList=null;
    	try {
			 String appendQuery = "";
			 if(userId != null){
		    		appendQuery = " AND usr.userId = " +userId.longValue();
	    	}
			String qry=" SELECT DISTINCT cr FROM CampaignReport cr , Users usr  where cr.user = usr.userId " +
					"AND usr.userOrganization = " + orgId + appendQuery + " ORDER BY cr.sentDate desc";
			numOfRepList=executeQuery(qry,startFrom,count);
			
	    } catch( Exception e) {
			logger.error("Exception ::" , e);
		}
	    
		return numOfRepList;
    	
    	
    }//findByUserName()
    
   public int getReportsCountByUser(long userId) {
	   int totalCount=0;
	   List<Long> countList = getHibernateTemplate().find("select count(crId) from CampaignReport where user = " + userId);
       if(countList.size()>0) {
       	return countList.get(0).intValue();
       }else {
       	return 0;
       }
	   
	      }
   
   
    * Returns List of urls in a given campaign .
    
   public List<String> getUrlListByCampaignId(String campaignName, Long userId) {
	   
	   try {
		
			String queryString = " SELECT url FROM campaign_urls WHERE cr_id in " +
			" (SELECT cr_id from campaign_report where user_id = "+ userId +" AND campaign_name='"+ campaignName +"') ";
		   
		   //what happens if he deleted a campaign which are sent 
			String queryString = " SELECT DISTINCT cu.url FROM campaign_urls cu,campaign_report cr  WHERE cu.cr_id = cr.cr_id AND " +
			" cr.user_id = "+ userId +" AND cr.campaign_name='"+ campaignName +"' ";
			
			List<String> list = jdbcTemplate.query(queryString, new RowMapper() {

				@Override
				public Object mapRow(ResultSet arg0, int arg1)
						throws SQLException {
					try {
						//to avoid unnecessary chars inserted when campaign sending, this decoding is required 
						return arg0!=null? URLDecoder.decode(arg0.getString("url"), "UTF-8"):"";
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						return null;
					}
				} });
			return list;
		} catch (Exception e) {
			logger.error(" ** Exception :", (Throwable) e);
			return null;
		}
   }//getUrlListByCampaignId
   
   
   public List<CampaignReport> findAsAdmin(String fromDateStr, String toDateStr, String sourceType, int startIndex, int endIndex,String userId,String orderby_colName,String desc_Asc) {
	   
	   
	   
	   return findAllReportsAsAdmin(fromDateStr, toDateStr, sourceType, startIndex, endIndex,userId,orderby_colName,desc_Asc);
   }
   
   public List<CampaignReport> findAllReportsAsAdmin(String fromDateStr, String toDateStr, String sourceType, int startIndex, int endIndex,String userId,String orderby_colName,String desc_Asc) {
	   String checkOn = "";
	   String checkUsrId = "";
	   if(sourceType != null && !sourceType.contains("--All--")) {
		   
		   checkOn += " AND sourceType like '%"+sourceType+"%'";
	   }
	   if(userId != null) {
		   checkUsrId += " AND user IN (" +userId + ") ";
	   }
	   String qry = "FROM CampaignReport WHERE sentDate between '" + fromDateStr + "' AND '" + toDateStr + "'" + checkOn + checkUsrId + 
			   " AND  status IN('sent') ORDER BY  "+orderby_colName+" "+desc_Asc;
	   //return getHibernateTemplate().find(qry);
	   return executeQuery(qry, startIndex, endIndex);
	   
   }
   
   
   
   public int getReportCount(String userId, String campaignName, boolean isLike, String sourceType, String fromDateStr, String toDateStr) {
	   
	   
	   String checkCon = "";
   	
   	if(campaignName != null) {
   		checkCon += " campaignName " + (isLike? ("like '%"+ campaignName +"%' ") : "='" + campaignName + "'") + " AND ";
   	}
   	
		if(userId != null) {
			checkCon += " user IN(" + userId + ") AND";
		} 
		
		if(fromDateStr != null && toDateStr != null) {
			checkCon += " sentDate between '" + fromDateStr + "' AND '" + toDateStr + "' AND ";
		}
		if(sourceType != null && !sourceType.equalsIgnoreCase("--All--")) {
			checkCon += " sourceType like '"+sourceType+"%'  AND ";
		}
   	
		String queryStr = " select COUNT(crId) FROM CampaignReport WHERE " + checkCon + " status IN ('sent') ORDER BY  sentDate DESC";
		logger.debug("Query  : " + queryStr);
	   
	   
	   
	   return ((Long)getHibernateTemplate().find(queryStr).get(0)).intValue();
	   
	   
   }
   
   
   
   public long getTodayMessages(Long userId) {
		try {
			
			String qry="SELECT SUM(sent) FROM CampaignReport WHERE user ="+ userId.longValue() + 
					" AND DATEDIFF(now(), sentDate)=0 ";

			List list = getHibernateTemplate().find(qry);
		
			if(list.size() > 0 && list.get(0) != null) {
				
				Object obj = list.get(0);
				logger.debug("obj "+obj);
				if(obj instanceof Long){
					
					return ((Long)obj).longValue();
				}
				
			} // if
			
		} catch (Exception e) {
			logger.error("Exception ::" , e);
		}
		return 0;
	} // getTodayMessages
   
//   public List getCampaignBasedOnContact(long user_id, long contact_id ,  String limit) {
   public List getCampaignBasedOnContact(long user_id, String emailId ,  String limit) {
	   
	   try {
			List<Object[]> latestCampList = null;
			
			logger.info("user_id ::" +user_id +" >>>>and emailId is ::"+emailId);
			String qry = "";
			if(limit.equals(Constants.LIMIT) ) {
				
				qry = "select cr.campaign_name , cr.sent_date " +
					  " from campaign_report cr, campaign_sent cs " +
					  " where cs.contact_id ="+ contact_id+" and cr.user_id = "+ user_id +" and cs.cr_id = cr.cr_id " +
					  " order by cr.sent_date desc limit 5";
				
				qry = "select cr.campaign_name , cr.sent_date " +
						  " from campaign_report cr, campaign_sent cs " +
						  " where cs.email_id = '"+ emailId+"' and cr.user_id = "+ user_id +" and cs.cr_id = cr.cr_id " +
						  " order by cr.sent_date desc limit 5";
				
				
			}else if(limit.equals(Constants.VIEW_ALL)) {
				qry = "select cr.campaign_name , cr.sent_date " +
					  " from campaign_report cr, campaign_sent cs " +
					  " where cs.email_id = '"+ emailId+"' and cr.user_id = "+ user_id +" and cs.cr_id = cr.cr_id " +
					  " order by cr.sent_date desc ";
			}
//			logger.info("query is ::"+qry);
			latestCampList = jdbcTemplate.query(qry, new RowMapper() {
				 public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
					 
					 Object[] promOCodeObjArr =  new Object[2];;
					 promOCodeObjArr[0] = rs.getString(1);
					 promOCodeObjArr[1] = rs.getDate(2);
					 
					return promOCodeObjArr;
				 }
				
			});
			return latestCampList;
		} catch (DataAccessException e) {
			logger.error("Exception ::" , e);
			return null;
		}
	   
   } // getCampaignBasedOnContact
   
   
   //public List getCampOpenBasedOnCont(long user_id, long contact_id ,  String limit) {
   public List getCampOpenBasedOnCont(long user_id, String emilId ,  String limit) {
	   try {
			List<Object[]> latestOpenCampList = null;
			
			logger.info("user_id ::" +user_id +" >>>>and contact_id is ::"+emilId);
			String qry = "";
			if(limit.equals(Constants.LIMIT) ) {
				
				qry ="SELECT cr.campaign_name ,o.open_date " + 
					 "FROM  campaign_report cr,opens o,campaign_sent cs " +
					 "where cr.user_id ="+user_id+" and cs.cr_id = cr.cr_id and  cs.sent_id = o.sent_id and  cs.contact_id = "+contact_id+" and cs.opens > 0 "+   
					 "order by o.open_date desc limit 5";
				qry ="SELECT cr.campaign_name ,o.open_date " + 
						 "FROM  campaign_report cr,opens o,campaign_sent cs " +
						 "where cr.user_id ="+user_id+" and cs.cr_id = cr.cr_id and  cs.sent_id = o.sent_id and  cs.email_id = '"+emilId+"' and cs.opens > 0 "+   
						 "order by o.open_date desc limit 5";
				
				
			}
			else if(limit.equals(Constants.VIEW_ALL)) {
				 qry="SELECT cr.campaign_name ,o.open_date " + 
					 "FROM  campaign_report cr,opens o,campaign_sent cs " +
					 "where cr.user_id ="+user_id+" and cs.cr_id = cr.cr_id and  cs.sent_id = o.sent_id and  cs.contact_id = "+contact_id+" and cs.opens > 0 "+   
					 "order by o.open_date desc ";
				 qry="SELECT cr.campaign_name ,o.open_date " + 
						 "FROM  campaign_report cr,opens o,campaign_sent cs " +
						 "where cr.user_id ="+user_id+" and cs.cr_id = cr.cr_id and  cs.sent_id = o.sent_id and  cs.email_id = '"+emilId+"' and cs.opens > 0 "+   
						 "order by o.open_date desc ";
			}
//			logger.info("query is ::"+qry);
			latestOpenCampList = jdbcTemplate.query(qry, new RowMapper() {
				 public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
					 
					 Object[] promOCodeObjArr =  new Object[2];;
					 promOCodeObjArr[0] = rs.getString(1);
					 promOCodeObjArr[1] = rs.getDate(2);
					 
					return promOCodeObjArr;
				 }
				
			});
			return latestOpenCampList;
		} catch (DataAccessException e) {
			logger.error("Exception ::" , e);
			return null;
		}
   } // getCampOpenBasedOnCont
	
   
   
//   public List getCampClickBasedOnCont(long user_id, long contact_id ,  String limit) {
   public List getCampClickBasedOnCont(long user_id, String emailId ,  String limit) {
	   try {
			List<Object[]> latestClickCampList = null;
			
			logger.info("user_id ::" +user_id +" >>>>and email  is ::"+emailId);
			String qry = "";
			
			if(limit.equals(Constants.LIMIT) ) {
				
				qry ="SELECT cl.click_Url,cr.campaign_name,cl.click_date " + 
					 "FROM campaign_sent cs,clicks cl,campaign_report cr " +
					 "where cr.user_id ="+user_id+" and cs.cr_id= cr.cr_id and cs.sent_id = cl.sent_id and cs.contact_id="+contact_id+" and cs.clicks > 0 "+   
					 "order by cl.click_date desc limit 5";
				
			}
			else if(limit.equals(Constants.VIEW_ALL)) {
				qry ="SELECT cl.click_Url,cr.campaign_name,cl.click_date " + 
						 "FROM campaign_sent cs,clicks cl,campaign_report cr " +
						 "where cr.user_id ="+user_id+" and cs.cr_id= cr.cr_id and cs.sent_id = cl.sent_id and cs.contact_id="+contact_id+" and cs.clicks > 0 "+   
						 "order by cl.click_date desc ";
			}
			
			
			if(limit.equals(Constants.LIMIT) ) {
				
				qry ="SELECT cl.click_Url,cr.campaign_name,cl.click_date " + 
					 "FROM campaign_sent cs,clicks cl,campaign_report cr " +
					 "where cr.user_id ="+user_id+" and cs.cr_id= cr.cr_id and cs.sent_id = cl.sent_id and cs.email_id='"+emailId+"' and cs.clicks > 0 "+   
					 "order by cl.click_date desc limit 5";
				
			}
			else if(limit.equals(Constants.VIEW_ALL)) {
				qry ="SELECT cl.click_Url,cr.campaign_name,cl.click_date " + 
						 "FROM campaign_sent cs,clicks cl,campaign_report cr " +
						 "where cr.user_id ="+user_id+" and cs.cr_id= cr.cr_id and cs.sent_id = cl.sent_id and cs.email_id='"+emailId+"' and cs.clicks > 0 "+   
						 "order by cl.click_date desc ";
			}
			
			
			
//			logger.info("query is ::"+qry);
			latestClickCampList = jdbcTemplate.query(qry, new RowMapper() {
				 public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
					 
					 Object[] promOCodeObjArr =  new Object[3];
					 promOCodeObjArr[0] = rs.getString(1);
					 promOCodeObjArr[1] = rs.getString(2);
					 promOCodeObjArr[2] = rs.getDate(3);
					 
					return promOCodeObjArr;
				 }
				
			});
			return latestClickCampList;
		} catch (DataAccessException e) {
			logger.error("Exception ::" , e);
			return null;
		}
   } // getCampOpenBasedOnCont
	
   public List findCountOfCampDetBasedOnCont(String emailId, long userId) {
		   try {
			List<Object[]> totalCampSent = null;
			 
			
			
//			   String qry = "SELECT count(sent_id),sum(opens),sum(clicks) FROM campaign_sent where contact_id="+contact_id;
			   String qry = "select count(sent_id),sum(cs.opens),sum(cs.clicks)  " +
			   		" from campaign_report cr, campaign_sent cs" +
			   		" where cs.email_id = '"+emailId+"' and cr.user_id ="+userId+" and cs.cr_id = cr.cr_id ";
		   
		   		totalCampSent = jdbcTemplate.query(qry, new RowMapper() {
				 public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
					 
					 Object[] tempObj =  new Object[3];
					 tempObj[0] = rs.getInt(1);
					 tempObj[1] = rs.getInt(2);
					 tempObj[2] = rs.getInt(3);
					return tempObj;
				 }
				
			});
		   		return totalCampSent;
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
			return null;
		}
		   		
   } // findCountOfCampDetBasedOnCont
   
   
   public long findCountOfMsgSent(String emailId, long userId) {
	   
	   String qry = "select count(sent_id) " +
			   " from campaign_report cr, campaign_sent cs" +
			   " where cr.user_id ="+userId+" and cs.cr_id = cr.cr_id and  cs.email_id = '"+emailId+"'  ";
		
	   return jdbcTemplate.queryForLong(qry);
	   
   }
   
   
   public long findCountOfMsgOpen(String emailId, long userId) {
	   
	   String qry = "select count(distinct o.sent_id) " +
			   " from campaign_report cr, campaign_sent cs, opens o" +
			   " where cr.user_id ="+userId+" and cs.cr_id = cr.cr_id and  cs.email_id = '"+emailId+"'  " +
			   " and o.sent_id=cs.sent_id";
		
	   return jdbcTemplate.queryForLong(qry);
	   
   }
 
   
   public long findCountOfMsgClick(String emailId, long userId) {
	   
	   String qry = "select count(distinct cl.click_url) " +
			   " from campaign_report cr, campaign_sent cs, clicks cl" +
			   " where cr.user_id ="+userId+" and  cs.cr_id = cr.cr_id and cs.email_id = '"+emailId+"'  " +
			   " and cl.sent_id=cs.sent_id;";
		
	   return jdbcTemplate.queryForLong(qry);
	   
   }*/
 
   public int updateCampaignReport(Long crId, String type) {
   	try {
		    	String queryStr = null;
		    	if(type.equals(Constants.CR_TYPE_OPENS))
		    		queryStr = "update CampaignReport set " + Constants.CR_TYPE_OPENS + 
		    			" = (SELECT count(sentId) FROM CampaignSent WHERE campaignReport =" + crId +
		    			" AND " + Constants.CS_TYPE_OPENS + " > 0) WHERE crId =" + crId;
		    	
		    	else if(type.equals(Constants.CR_TYPE_CLICKS))
		    		queryStr = "update CampaignReport set " + Constants.CR_TYPE_CLICKS + 
		    		" = (SELECT count(sentId) FROM CampaignSent WHERE campaignReport =" + crId + 
		    		" AND " + Constants.CS_TYPE_CLICKS + " > 0) WHERE crId =" + crId;
		    	
		    	//TODO: get bounce count from bounces table
		    	else if(type.equals(Constants.CR_TYPE_BOUNCES)) 
		    		queryStr = "update CampaignReport set " + Constants.CR_TYPE_BOUNCES + 
		    		" = (SELECT count(sentId) FROM CampaignSent WHERE campaignReport =" + crId + 
		    		" AND " + Constants.CS_TYPE_STATUS + " = '" + Constants.CS_STATUS_BOUNCED +
		    		"') WHERE crId =" + crId;
		
		    	else if(type.equals(Constants.CR_TYPE_SPAM))
		    		queryStr = "update CampaignReport set " + Constants.CR_TYPE_SPAM + 
		    		" = (SELECT count(sentId) FROM CampaignSent WHERE campaignReport =" + crId + 
		    		" AND " + Constants.CS_TYPE_STATUS + " = '" + Constants.CS_STATUS_SPAMMED +
		    		"') WHERE crId =" + crId;
		
		    	//TODO: get Unsubs from unsubscribes tables
		    	else if(type.equals(Constants.CR_TYPE_UNSUBSCRIBES) || type.equalsIgnoreCase("resubscribe"))
		    		queryStr = "update CampaignReport set " + Constants.CR_TYPE_UNSUBSCRIBES + 
		    		" = (SELECT count(sentId) FROM CampaignSent WHERE campaignReport =" + crId + 
		    		" AND " + Constants.CS_TYPE_STATUS+ " = '" + Constants.CS_STATUS_UNSUBSCRIBED +
		    		"') WHERE crId =" + crId;
		
		    	return ( (queryStr == null)? 0 : executeUpdate(queryStr) );
   	} catch(Exception e) {
   		logger.error("** Exception while updateing the reports : crId : "+crId, e);

   		return 0;
   	}
   }
   
   /*public CampaignReport findById(Long campaignReportId) {
	   
   try {
	   String hql = "FROM CampaignReport WHERE crId = "+campaignReportId.longValue();
	   
	   List<CampaignReport> retList = executeQuery(hql);
	   
	   if(retList != null && retList.size() > 0) {
		   
		   return retList.get(0);
	   }
	   else return null;
	} catch (Exception e) {
		// TODO Auto-generated catch block
		logger.error("Exception ::" , e);
		return null;
	}
	   
	   
	   
   }
 
   
   public List<Long> findEventsOfCorrespondingCr(Long crId, Long startId, Long endId, String category) {
	   
	   try {
		   String qry = Constants.STRING_NILL;
		   if(category.equalsIgnoreCase(Constants.CS_TYPE_OPENS) ) {
			   
			   qry = " SELECT o.openId FROM Opens o,  CampaignSent cs WHERE  cs.campaignReport="+crId.longValue()+
			   				"  AND o.sentId=cs.sentId AND o.openId BETWEEN "+startId.longValue()+" AND "+endId.longValue();
			   
		   }else if(category.equalsIgnoreCase(Constants.CS_TYPE_CLICKS) ){
			   
			   qry = " SELECT c.clickId FROM Clicks c,  CampaignSent cs WHERE  cs.campaignReport="+crId.longValue()+
			   				"  AND c.sentId=cs.sentId AND c.clickId BETWEEN "+startId.longValue()+" AND "+endId.longValue();
		   
		   }
		   
		   logger.info("qry::"+qry);
		   List<Long> retList = executeQuery(qry);
		   if(retList != null && retList.size() > 0) return retList;
		   else return null;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
			return null;
		}
		   
   }*///findEventsOfCorrespondingCr
	   

   
   
   
   /*public List<Long> findEventsOfCorrespondingCr(Long crId, Long startId, Long endId, String category) {
	   
   try {
	   String qry = Constants.STRING_NILL;
	   if(category.equalsIgnoreCase(Constants.CS_TYPE_OPENS) ) {
		   
		   qry = " SELECT o.sentId FROM Opens o,  CampaignSent cs WHERE" +
		   		" cs.campaignReport="+crId.longValue()+" AND o.sentId=cs.sentId AND o.openId BETWEEN "+startId.longValue()+" AND "+endId.longValue();
		   
		   
		   
	   }else if(category.equalsIgnoreCase(Constants.CS_TYPE_CLICKS) ){
		   
		   qry = " SELECT c.sentId FROM Clicks c, CampaignSent cs WHERE  " +
		   		" cs.campaignReport="+crId.longValue()+" AND c.sentId=cs.sentId AND c.clickId BETWEEN "+startId.longValue()+" AND "+endId.longValue();
	   
	   }
	   
	   logger.info("qry::"+qry);
	   List<Long> retList = executeQuery(qry);
	   if(retList != null && retList.size() > 0) return retList;
	   else return null;
	} catch (Exception e) {
		// TODO Auto-generated catch block
		logger.error("Exception ::" , e);
		return null;
	}
	   
   }*/
   
}
