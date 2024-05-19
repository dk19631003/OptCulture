


package org.mq.marketer.campaign.dao;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.mq.marketer.campaign.beans.CampaignSent;
import org.mq.marketer.campaign.controller.report.CampaignSentStatus;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.Utility;
import org.springframework.jdbc.core.JdbcTemplate;

@SuppressWarnings({ "unchecked", "serial" })
public class CampaignSentDaoForDML extends AbstractSpringDaoForDML {

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	public CampaignSentDaoForDML() {}
	  private JdbcTemplate jdbcTemplate;
	    
	    

	    public JdbcTemplate getJdbcTemplate() {
			return jdbcTemplate;
		}

		public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
			this.jdbcTemplate = jdbcTemplate;
		}
  
    /*public CampaignSent find(Long id) {
        return (CampaignSent) super.find(CampaignSent.class, id);
    }*/

    public void saveOrUpdate(CampaignSent campaignSent) {
        super.saveOrUpdate(campaignSent);
    }

    /*public void delete(CampaignSent campaignSent) {
        super.delete(campaignSent);
    }*/

    /*public List findAll() {
        return super.findAll(CampaignSent.class);
    }*/
    
	/*public CampaignSent findById(long id) throws Exception {
       List list = getHibernateTemplate().find("from CampaignSent where sentId = " + id);
	   CampaignSent cs = null;
	   if(list.size()>0){
			cs = (CampaignSent) list.get(0);
	   }
	   return cs;
    }*/
	

	
	/*public Long getTotalOpensByCrId(Long crId){
		return (Long)getHibernateTemplate().find(" select sum(opens) from CampaignSent where campaignReport = "+crId+" and opens > 0").get(0);
	}*/

	  
	  /*public List<Object[]> getAllEmailsByCrId(Long crId,int firstResult,int maxResults) {
		  try {
			  String query = "select sentId,emailId,opens,clicks from CampaignSent as cs where  cs.campaignReport =" + crId;
			  List cList = executeQuery(query, firstResult, maxResults);
			  return cList;
		  } catch (Exception e) {
			  logger.error("** Error : " + e.getMessage() + " **");
			  return null;
		  } 
	  }*/
	  /*public List<Object[]> getAllDelEmailsByCrId(Long crId,int firstResult,int maxResults) {
		  try {
			  String query = "select sentId,emailId,opens,clicks from CampaignSent as cs where  cs.campaignReport =" + crId+" AND status in ('"+Constants.CS_STATUS_SUCCESS+"','"+Constants.CS_STATUS_UNSUBSCRIBED+"')";
			  List cList = executeQuery(query, firstResult, maxResults);
			  return cList;
		  } catch (Exception e) {
			  logger.error("** Error : " + e.getMessage() + " **");
			  return null;
		  } 
	  }*/
	  /*public List<Object[]> getOpenedEmailsByCrId(Long crId,int firstResult,int maxResults) {
		  try {
			  //String query = "select cs.sentId,cs.emailId,cs.opens,o.openDate from CampaignSent as cs, Opens  o where  cs.campaignReport =" + crId +" and cs.opens>0 and cs.sentId=o.sentId and o.openDate=(SELECT max(op.openDate) FROM Opens op where op.sentId=cs.sentId) order by cs.opens desc";
			  String query = "SELECT o.sentId, (SELECT c.emailId FROM CampaignSent c WHERE c.campaignReport=" + crId +
			  " AND o.sentId=c.sentId), COUNT(o.sentId), MAX(o.openDate) FROM Opens o WHERE o.sentId IN (SELECT cs.sentId FROM CampaignSent cs WHERE cs.campaignReport=" + crId +
			  " AND cs.opens>0) GROUP BY o.sentId  ORDER BY MAX(o.openDate) DESC";
			  
			  List cList = executeQuery(query, firstResult, maxResults);
			  return cList;
		  } catch (Exception e) {
			  logger.error("** Error : " + e.getMessage() + " **");
			  return null;
		  } 
	  }*/
	  
	  /*public List<Object[]> getClickedEmailsByCrId(Long crId,int firstResult,int maxResults) {
		  try {
			  String query = "select cs.sentId,cs.emailId,cs.clicks,c.clickUrl from CampaignSent as cs, Clicks  c where  cs.campaignReport =" + crId +" and cs.clicks>0 and cs.sentId=c.sentId and c.clickDate=(SELECT max(ck.clickDate) FROM Clicks ck where ck.sentId=cs.sentId) group by cs.emailId  order by cs.clicks desc";

			  List cList = executeQuery(query, firstResult, maxResults);
			  return cList;
		  } catch (Exception e) {
			  logger.error("** Error : " + e.getMessage() + " **");
			  return null;
		  } 
	  }*/
	  
	  /*public List<Object[]> getUnsubscribesByCrId(Long crId,int firstResult,int maxResults) {
		  try {
			  String query = "select sentId,emailId from CampaignSent where campaignReport =" + crId +" and status='Unsubscribed'";

			  
			  List<Object[]> list =  executeQuery(query, firstResult, maxResults);
			  return list;
		  }catch (Exception e) {
			  logger.error("** Error : " + e.getMessage() + " **");
			  return null;
		  } 
	  }*/
	  
	  /*public List<Object[]> getSpammedEmailsByCrId(Long crId,int firstResult,int maxResults) {
			try {
				String query = "select sentId,emailId from CampaignSent where campaignReport =" + crId +" and status='spammed'";

				List<Object[]> list =  executeQuery(query, firstResult, maxResults);
				return list;
			}catch (Exception e) {
				logger.error("** Error : " + e.getMessage() + " **");
				return null;
			} 
		}*/
	  /*public int getSpamEmailCount(Long crId) {
		  int spamCount = 0;
		  try {
			 //List res = getHibernateTemplate().find("select count(*) from CampaignSent where status='" + CampaignSentStatus.SPAMMED + "' and campaignReport =" + crId);
			 List res = getHibernateTemplate().find("select count(sentId) from CampaignSent where   campaignReport =" + crId+ " and status='" + CampaignSentStatus.SPAMMED + "'");
			 if(res.size()>0){
				 spamCount = ((Long)res.get(0)).intValue();
			 }
		  } catch (Exception e) {
			  logger.error("Exception : ", e);
			  spamCount = 0;
		  }
		  return spamCount;
	  }*/
	  /*this method gives the sentcount (dashboard)*/
	  /*public String getSentCountByDomain(String domain,long crId) {
		  String query="select count(sentId) from CampaignSent where campaignReport="+crId+" and emailId like '%"+domain+"%'";
		  String sentCount=null;
		  try{
			  List count=executeQuery(query);
			  logger.debug("size--->"+count.size());
			  sentCount=""+(Long)count.get(0);
		  }catch (Exception e) {
			logger.error("exception while fetching the sentCount"+e);
		}
		  return sentCount;
	  }*/
	  
	  /*public Long getUserIdBySentId(Long sentId) {
			
			return jdbcTemplate.queryForLong(
					" SELECT cr.user_id FROM campaign_report cr, campaign_sent cs " +
					" WHERE cs.sent_id="+sentId+" AND cs.cr_id= cr.cr_id");
		}*/
	  
	  /*public CampaignSent findById(long id){
			CampaignSent cs = null;
			try {
				List list = getHibernateTemplate().find(
						"from CampaignSent where sentId = " + id);
				if (list.size() > 0) {
					cs = (CampaignSent) list.get(0);
				}
			} catch (Exception e) {
				logger.error(" ** Exception : ", e ); 
			}
			return cs;
		}*/
	  
	  public int updateCampaignSent(Long idL, String type) {
			String queryStr = null;
			long id = idL.longValue();
			if(type.equals(Constants.CS_TYPE_OPENS))
				queryStr = 
					" UPDATE CampaignSent SET opens = " +
					" (SELECT count(openId) FROM Opens WHERE sentId = "+ id + ")" +
					" WHERE sentId = " + id;

			else if(type.equals(Constants.CS_TYPE_CLICKS))
				queryStr = 
					" UPDATE CampaignSent SET clicks = " +
					" (SELECT count(clickId) FROM Clicks WHERE sentId = "+ id + ")" +
					" WHERE sentId = " + id;
			
			else if(type.equals(Constants.CS_STATUS_UNSUBSCRIBED))
				queryStr = 
					"UPDATE CampaignSent SET status = '" + Constants.CS_STATUS_UNSUBSCRIBED + 
					"' WHERE sentId = " + id;
			
			else if(type.equalsIgnoreCase(Constants.CS_STATUS_SUCCESS))
				queryStr = 
					"UPDATE CampaignSent SET status = '" + Constants.CS_STATUS_SUCCESS + 
					"' WHERE sentId = " + id;
			else if(type.equalsIgnoreCase(Constants.CS_STATUS_SPAMMED))
				queryStr = "UPDATE CampaignSent SET status = '" + Constants.CS_STATUS_SPAMMED + 
					"' WHERE sentId = " + id;
			else if(type.equalsIgnoreCase(Constants.CS_STATUS_BOUNCED)) {
				queryStr = "UPDATE CampaignSent SET status = '" + Constants.CS_STATUS_BOUNCED + 
					"' WHERE sentId = " + id;
			}	
			return ( (queryStr == null)? 0 : executeUpdate(queryStr) );
			
		}
	  
	  public int updateCampaignSent(String type, Long cr_id, Long startId, Long endId, Set<Long> openSentIdsSet) {
			String queryStr = null;
			//long id = idL.longValue();
			 String sentIdStr = Utility.getIdsAsString(openSentIdsSet);
			 
			 if(sentIdStr.isEmpty()) return 0;
			
			
			String fromtable = "";
			String tableId = "";
			if(type.equals(Constants.CS_TYPE_OPENS)){
				
				fromtable = "opens";
				tableId = "open_id" ;
				
			}
			
			else if(type.equals(Constants.CS_TYPE_CLICKS)){
				
				fromtable = "clicks";
				tableId = "click_id" ;
				
			}
				/*queryStr = 
					"UPDATE campaign_sent cs " +
					" JOIN (SELECT count("+tableId+") as cnt, sent_id as sent_id FROM "+fromtable+
					" WHERE sent_id IN("+sentIdStr+") AND "+tableId+" BETWEEN "+startId+" AND "+endId+" GROUP BY sent_id) o " +
					" ON cs.sent_id = o.sent_id" +
					"	SET cs."+fromtable+"=o.cnt where cs.cr_id="+cr_id.longValue();*/
				
				queryStr = 
						"UPDATE campaign_sent cs " +
						" JOIN (SELECT count("+tableId+") as cnt, sent_id as sent_id FROM "+fromtable+
						" WHERE sent_id IN("+sentIdStr+") GROUP BY sent_id) o " +
						" ON cs.sent_id = o.sent_id" +
						"	SET cs."+fromtable+"=o.cnt, cs.status='Success' where cs.cr_id="+cr_id.longValue();

			logger.info(" Email update query ::"+queryStr);
			return ( (queryStr == null)? 0 : jdbcTemplate.update(queryStr));
			
		}
	  
	  /*public List<Long> findCrIdsBySent(Set<Long> sentIdsSet) {
			 
			 String sentIdStr = Utility.getIdsAsString(sentIdsSet);
			 
			 if(sentIdStr.isEmpty()) return null;
			 
			 String qry = " SELECT DISTINCT campaignReport.crId FROM CampaignSent WHERE sentId IN("+sentIdStr+")";
			 logger.info("qry ::"+qry);
			 List<Long> crIds = executeQuery(qry);
			 if(crIds != null && crIds.size() > 0) {
				 
				
				 return crIds;
				 
			 }else {
				 
				 return null;
			 }
			 
			 
		 }*/
	  /*public List findSentAndCrIdsBySent(Set<Long> sentIdsSet) {
			 
			 String sentIdStr = Utility.getIdsAsString(sentIdsSet);
			 
			 if(sentIdStr.isEmpty()) return null;
			 
			 String qry = " SELECT DISTINCT sentId, campaignReport.crId FROM CampaignSent WHERE sentId IN("+sentIdStr+")";
			 logger.info("qry ::"+qry);
			 List crIds = executeQuery(qry);
			 if(crIds != null && crIds.size() > 0) {
				 return crIds;
			 }
			 else {
				 return null;
			 }
	}*/
	
	  
	 public int  finalUpdateCampaignSent(Long crId) {
			
			
			try {
				
					//update open count
					String queryStr = "UPDATE campaign_sent SET opens=1  where cr_id="+crId.longValue()+" AND clicks>0 AND opens=0";
					
					return jdbcTemplate.update(queryStr);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Exception ::" , e);
				return 0;
			}
			
			
			
			
		}
	 
	 public int  finalUpdateForUnsubCampaignSent(Long crId) {
			
			
			try {
				
					//update open count
					String queryStr = "UPDATE campaign_sent SET opens=1  where cr_id="+crId.longValue()+" AND status='"+Constants.CS_STATUS_UNSUBSCRIBED+"' AND opens=0";
					
					return jdbcTemplate.update(queryStr);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Exception ::" , e);
				return 0;
			}
			
			
			
			
		}
	 
	 /*public List<Object[]> findNoOpens(Set<Long> clickSentIdsSet) {
	    	
	    	String sentIdsStr = Utility.getIdsAsString(clickSentIdsSet);
	    	
	    	if(sentIdsStr.isEmpty()) return null;
	    	
	    	String qry = "SELECT DISTINCT cs.sentId, c.clickDate, cs.emailId  FROM Clicks c ," +
	    			" CampaignSent cs WHERE cs.sentId  IN("+sentIdsStr+") AND" +
	    					" c.sentId=cs.sentId AND cs.clicks>0 AND cs.opens=0 GROUP BY cs.sentId";
	    	
	    	List<Object[]> retList = executeQuery(qry);
	    	
	    	if(retList != null && retList.size() > 0) return retList;
	    	else return null;
	    	
	    }*///findNoOpens
	 
	 
	 /*public List<Map<String, Object>> findEmaiIBySentIds(String sentIds) {
	    	
	    	
	    	
	    	try {
				String qry = "SELECT  sent_id, email_id  from campaign_sent  WHERE sent_id  IN("+sentIds+") ";
				logger.info(" >>> qry here is ::"+qry);
				return jdbcTemplate.queryForList(qry);
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Exception ::" , e);
				return null;
			}
	    	
	    }*///findNoOpens
	 
}
