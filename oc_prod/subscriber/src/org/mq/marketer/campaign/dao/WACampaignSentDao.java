package org.mq.marketer.campaign.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.mq.marketer.campaign.beans.WACampaignSent;
import org.mq.marketer.campaign.controller.service.ClickaTellApi;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.WAStatusCodes;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

public class WACampaignSentDao extends AbstractSpringDao{
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	private SessionFactory sessionFactory;
	private JdbcTemplate jdbcTemplate;
	
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public WACampaignSentDao() {}
  
    public WACampaignSent find(Long id) {
        return (WACampaignSent) super.find(WACampaignSent.class, id);
    }

    public List findAll() {
        return super.findAll(WACampaignSent.class);
    }
	
    
    public int getCount(Long waCampRepId, String status) {
    	
    	String qry = "SELECT count(sentId) FROM WACampaignSent where waCampaignReport="+waCampRepId+" AND status IN("+status+")";
		int count = ((Long)getHibernateTemplate().find(qry).get(0)).intValue();	
		return count;
    	
    	
    	
    	
    }
    
    
    public List<Object[]> getAllMobileNumsByCrId(Long waCrId,int firstResult,int maxResults) {
		  try {
			  String query = "select sentId,mobileNumber,opens,clicks,status from WACampaignSent as cs where  cs.waCampaignReport =" + waCrId;

			  List cList = executeQuery(query, firstResult, maxResults);
			  logger.info("list is---->"+cList+" "+cList.size());
			  return cList;
		  } catch (Exception e) {
			  logger.error("** Error : " + e.getMessage() + " **");
			  return null;
		  } 
	  }
	
	public List<String> getUndeliveredResonsByCampReportId(Long waCampReportId) {
		List<String> reasons = null;
		try {
			
			String query = "SELECT distinct(status) from WACampaignSent where waCampaignReport ="+waCampReportId+
							" and status not in('" + Constants.WA_CAMPAIGN_SUCCESS + "')";
			reasons = executeQuery(query);
			reasons.add(0, "All");
				return reasons;
			
		} catch (Exception e) {
			logger.error("Exception :::",e);
			return null;
		}
	}
	
	public List<WACampaignSent> getCampSentByUndeliveredCategory(Long waCampReportId,String status) {
		
		List<WACampaignSent> waCampSentList = null;
		String query ="";
		try {
			if(status.equalsIgnoreCase(Constants.WA_CAMPAIGN_SENT_STATUS_ALL)){
				query = "from WACampaignSent where waCampaignReport ="+waCampReportId+
							" and status not in('" + Constants.WA_CAMPAIGN_SUCCESS + "')";
			}else {
				
				query = " from WACampaignSent where waCampaignReport ="+waCampReportId+
						" and status = '"+status+"'";
				
			}
			
			waCampSentList = executeQuery(query);
				return waCampSentList;
			
		} catch (Exception e) {
			logger.error("Exception :::",e);
			return null;
		}
		
	}
	
	
	 public List getCategoryPercentageByCrId( Long campRepId) {
	    	try {
	    		List<Object[]> catList = new ArrayList<Object[]>();
	    		String queryStr = "SELECT status, COUNT(sent_id)" +
	    				" FROM  wa_campaign_sent  " +
	    				"WHERE wa_cr_id="+campRepId+
	    				"  GROUP BY  status";
	    		logger.info("Query :" + queryStr);
	    		catList = jdbcTemplate.query(queryStr,	new RowMapper(){
	    			Object[] obj;
					@Override
					public Object mapRow(ResultSet rs, int rowNum)
							throws SQLException {
						obj = new Object[2];
						obj[0] = rs.getString(1);
						obj[1] = rs.getLong(2);
						
						
						return obj;
					}
	    			
	    		});
	    		return catList;
	    	} catch (Exception e) {
	    		logger.error("** Error : " + e.getMessage() + " **");
	    		return null;
			}
	    }
	
	
	public int getNDNCRejectedCount(Long waCampRepId) {
		
		String qry = "SELECT count(sentId) FROM WACampaignSent where waCampaignReport="+waCampRepId+" AND status='" + Constants.NDNC_REJECTED + "'";
		int NDNCCount = ((Long)getHibernateTemplate().find(qry).get(0)).intValue();	
		return NDNCCount;
		
	}
	
	
	public List<String> getNDNCRejected(Long waCampRepId, int startFrom, int count) {
		String queryStr = "select mobileNumber from WACampaignSent where waCampaignReport="+waCampRepId+" AND status='" + Constants.NDNC_REJECTED + "'";
		
		List<String> list = (List<String>)executeQuery(queryStr, startFrom, count);
		
		return list;
	}
	
	
	
	public List<String> getRecieved(Long waCampRepId, int startFrom, int count) {
		
		String queryStr = "select mobileNumber from WACampaignSent where " +
				"waCampaignReport="+waCampRepId+" AND status='"+WAStatusCodes.WA_STATUS_RECEIVED+"'";
		
		List<String> list = (List<String>)executeQuery(queryStr, startFrom, count);
		
		return list;
	}
	
	
	
	public List<Object[]> getRepByStatus(Long waCampRepId, String status, int startFrom, int count) {
		
		
		String qry = "SELECT mobileNumber, opens,clicks, status From WACampaignSent WHERE waCampaignReport="+waCampRepId+
						" AND status IN ("+status+")";
		logger.info("query :" +qry);
		return executeQuery(qry, startFrom, count );
		
	}//getRepByStatus
	
	
	
	public List<String> getDelivered(Long waCampRepId, int startFrom, int count) {
		String queryStr = "select mobileNumber from WACampaignSent where waCampaignReport="+waCampRepId+" AND status='"+WAStatusCodes.WA_STATUS_DELIVERED_TO_RECEPIENT+"'";
		
		List<String> list = (List<String>)executeQuery(queryStr, startFrom, count);
		
		return list;
	}
	
	 public List<Object[]> getOpenedMobilesByCrId(Long crId,int firstResult,int maxResults) {
		  try {
			  String query = "SELECT o.sentId, (SELECT c.mobileNumber FROM WACampaignSent c WHERE c.waCampaignReport=" + crId +
			  " AND o.sentId=c.sentId), COUNT(o.sentId), MAX(o.openDate) FROM WAOpens o WHERE o.sentId IN (SELECT cs.sentId FROM WACampaignSent cs WHERE cs.waCampaignReport=" + crId +
			  " AND cs.opens>0) GROUP BY o.sentId  ORDER BY COUNT(o.sentId) DESC";
			  
			  List cList = executeQuery(query, firstResult, maxResults);
			  return cList;
		  } catch (Exception e) {
			  logger.error("** Error : " + e.getMessage() + " **");
			  return null;
		  } 
	  }
	  
	  public List<Object[]> getClickedMobilesByCrId(Long crId,int firstResult,int maxResults) {
		  try {
			  String query = "select cs.sentId,cs.mobileNumber,cs.clicks,c.clickUrl from WACampaignSent " +
			  		"as cs, WAClicks  c where  cs.waCampaignReport =" + crId +" and cs.clicks>0 and " +
			  				"cs.sentId=c.sentId and c.clickDate=(SELECT max(ck.clickDate) FROM WAClicks ck " +
			  				"where ck.sentId=cs.sentId) group by cs.sentId order by cs.clicks desc";

			  List cList = executeQuery(query, firstResult, maxResults);
			  return cList;
		  } catch (Exception e) {
			  logger.error("** Error : " + e.getMessage() + " **");
			  return null;
		  } 
	  }
	  
	  public List<Object[]> getAllSentCategories(Long waCampRepId) {
			  String queryStr = "select  status, count(sent_id) from wa_campaign_sent where status Not " +
			 		"IN('"+ Constants.WA_CAMPAIGN_STATUS_BOUNCE + "','" +
			 		WAStatusCodes.WA_STATUS_RECEIVED+ "') And wa_cr_id =" + waCampRepId + " group by status order by count(sent_id)  desc";
			 
			 List<Object[]> catList = new ArrayList<Object[]>();
			 catList = jdbcTemplate.query(queryStr,	new RowMapper(){
	 			Object[] obj;
					@Override
					public Object mapRow(ResultSet rs, int rowNum)
							throws SQLException {
						obj = new Object[2];
						obj[0] = rs.getString(1);
						obj[1] = rs.getLong(2);
						
						
						return obj;
					}
	 			
	 		});
	 		return catList;
			 
		 }
	
	  public int findTotClicksByCrid(Long crId) throws Exception{
		  
		  
		String  queryString = "SELECT SUM(clicks) FROM WACampaignSent WHERE waCampaignReport="+crId.longValue() ;
		return ((Long) executeQuery(queryString).get(0)).intValue();
		  
	  }
	  
	  public WACampaignSent findByMrIdAndMobile(String mrId, String mobile) {
			
		  logger.info(""+mobile);
		  if(mobile.length() == 12) {
				
				
				mobile = mobile.substring(2);
			}
			String query = "FROM WACampaignSent where apiMsgId='"+mrId+"' AND mobileNumber like '%"+mobile+"%'";
			logger.info("query--"+query);
			try{
			List<WACampaignSent> retWaCampaignSentList =  getHibernateTemplate().find(query);
			if(retWaCampaignSentList != null && retWaCampaignSentList.size() > 0) {
				
				return retWaCampaignSentList.get(0);
			}
			}catch(Exception e){
				logger.error("Exception ",e);
			}			

			return null;
		}
	  public WACampaignSent findByRefIdAndMobile(String mrId, String mobile) {
			
		  logger.info(""+mobile);
		  String query = "FROM WACampaignSent where apiMsgId='"+mrId+"' AND mobileNumber like '%"+mobile+"%'";
			logger.info("query--"+query);
			try{
			List<WACampaignSent> retWaCampaignSentList =  getHibernateTemplate().find(query);
			if(retWaCampaignSentList != null && retWaCampaignSentList.size() > 0) {
				
				return retWaCampaignSentList.get(0);
			}
			}catch(Exception e){
				logger.error("Exception ",e);
			}
		
			return null;
		}
}
