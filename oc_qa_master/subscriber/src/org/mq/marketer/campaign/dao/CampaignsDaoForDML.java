package org.mq.marketer.campaign.dao;


import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

//import javax.servlet.http.HttpServletRequest;
import org.hibernate.HibernateException;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.mq.marketer.campaign.beans.CampaignSchedule;
import org.mq.marketer.campaign.beans.Campaigns;
import org.mq.marketer.campaign.beans.CampaignReport;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.utils.OCConstants;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;


@SuppressWarnings({"unchecked","unused"})
public class CampaignsDaoForDML extends AbstractSpringDaoForDML implements Serializable {
    
	public CampaignsDaoForDML() {}
    private SessionFactory sessionFactory;
    private JdbcTemplate jdbcTemplate;
    
    
    public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	/*public Campaigns find(Long id) {
        return (Campaigns) super.find(Campaigns.class, id);
    }*/

    private CampaignReportDao campaignReportDao = null;
        
    public CampaignReportDao getCampaignReportDao() {
		return campaignReportDao;
	}

	public void setCampaignReportDao(CampaignReportDao campaignReportDao) {
		this.campaignReportDao = campaignReportDao;
	}

	public void saveOrUpdate(Campaigns campaigns) {
		logger.info("status............."+campaigns.getStatus());
		logger.info("draftstatus............."+campaigns.getDraftStatus());
        super.saveOrUpdate(campaigns);
    }

    /*public void delete(Campaigns campaigns) {
        super.delete(campaigns);
    }*/

    /*public List findAll() {
        return super.findAll(Campaigns.class);
    }*/

    /*public List findByCampaignName(String name) {
         
        return getHibernateTemplate().find("from Campaigns where campaignName = '" + name + "' ");
       
    }*/
    
    private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
    /*public List<Campaigns> findByUser(Long userID) {
        return getHibernateTemplate().find("from Campaigns where users.userId= '" + userID + "'");
    }*/
    /**
     * this method is called from dashboardcontroller
     * @param userId
     * @param startFrom
     * @param count
     * @return
     */
    /*public List<Campaigns> findByOrgIdAndUserId(Long orgId, Long userId,int startFrom,int count) {
    	List<Campaigns> numOfCampList=null;
    	try {
    		String appendQuery = "";
			 if(userId != null){
		    		appendQuery = " AND usr.userId = " +userId.longValue();
	    	}
			String qry=" SELECT DISTINCT camp FROM Campaigns camp , Users usr  where camp.users = usr.userId " +
					" AND usr.userOrganization = " + orgId + appendQuery + " ORDER BY camp.modifiedDate desc";
			numOfCampList=executeQuery(qry,startFrom,count);
			
	    } catch( Exception e) {
			logger.error("Exception ::" , e);
		}
	    
		return numOfCampList;
    	
    	
    }*///findByUser()
    	
    /*public List<Campaigns> getCampaignById(String id) {
    	
    	
    	String qry = "FROM Campaigns where campaignId IN("+id+")";
    	
    	
    	return getHibernateTemplate().find(qry);
    	
    	
    	
    }*/
    
    
    /*public Campaigns getSingleCampaign(String name, Long userId) {
        // return super.findAll (Email.class);
    	name = StringEscapeUtils.escapeSql(name);
        List list = getHibernateTemplate().find ("from Campaigns where campaignName = '" + name + "' and users= " + userId);

        if(list==null || list.size()==0) {
        	return null;
        }
        
        if (list.size() > 1) {
            logger.error("** Problem:Size of List is not 1 ;should be ideally 1 ** ");
        }
        
        Campaigns campaign = (Campaigns) list.get(0);

        return campaign; 
    }*/
		// returns total campaigns for the user
	/*public List getCampaignsByUser(String userName){
		return getHibernateTemplate().find("from Campaigns where users.userName= '" + userName + "'");
	}*/
	
	
	
	/*public List<Campaigns> getCampignsByUser(Long userId, int firstResult, int maxResult) {
		
		List<Campaigns> campList = null;
		
		String qry = "FROM Campaigns WHERE users="+userId;
		
		campList = executeQuery(qry, firstResult, maxResult);
		
		if(campList != null && campList.size() > 0) {
			
			return campList;
		}
		
		return null;
	}*/
	
	
	
	
	
	
	// returns the specified campaign for the user
	/*public List getCampaignByUser(String campaignName,String userName){
		return getHibernateTemplate().find("from Campaigns where campaignName = '" + campaignName + "' and users.userName= '" + userName + "'");
	}*/

	/*public List<Campaigns> getCampaignsByStatus(Set<Long> userIds, String status, int firstResult, int maxResult){
		String query=null;
		List<Campaigns> campList=null;
		try{
			String userIdsStr = Utility.getUserIdsAsString(userIds);
			
		if(status.equals("All")) {
			query="FROM Campaigns WHERE users in (" + userIdsStr+" ) order by modifiedDate desc";
		  }else {	
		
			query="FROM Campaigns WHERE users IN ( " + userIdsStr + " ) and status like '" + status + "' order by modifiedDate desc" ;
		  }
		campList=executeQuery(query, firstResult, maxResult);
		
		
	    }catch(Exception e) {
	    	  logger.error("exception while retrieving campaign list",(Throwable)e);
	      }
	
	return campList;
	
	}*/

	/*public List<Campaigns> getCampaignsByStatus(Long userId,  int firstResult, int maxResult,String orderby_colName,String desc_Asc) throws Exception{
	String query=null;
	List<Campaigns> campList=null;
	try{
		//String userIdsStr = Utility.getUserIdsAsString(userIds);
		query="FROM Campaigns WHERE users in (" + userId+" ) order by "+orderby_colName+" "+desc_Asc;
	if(status.equals("All")) {
		query="FROM Campaigns WHERE users in (" + userId+" ) order by modifiedDate desc";
	  }else {	
	
		query="FROM Campaigns WHERE users IN ( " + userId + " ) and status like '" + status + "' order by modifiedDate desc" ;
	  }
	campList=executeQuery(query, firstResult, maxResult);
	
	
    }catch(Exception e) {
    	  logger.error("exception while retrieving campaign list",(Throwable)e);
      }
return campList;

}*/
	
	/*public List<Campaigns> getCampaignsBetweenCreationDates(String fromDateString, String toDateString, Long userId, int firstResult, int maxResult,String orderby_colName,String desc_Asc) throws Exception{
		String query = null;
		List<Campaigns> campList = null;
		try{
			query="FROM Campaigns WHERE users IN ( "+userId+" ) and createdDate between '"+fromDateString+"' AND '"+toDateString+"' order by "+orderby_colName+" "+desc_Asc;
			logger.info(query);
			campList=executeQuery(query, firstResult, maxResult);
		}
		catch(Exception e) {
			logger.error("exception while retrieving campaign list",(Throwable)e);
		}
		return campList;
	}*/
	
	/*public int getCountByCreationDate(String fromDateString, String toDateString, Long userId){
		String query = null;
		List<Campaigns> campList = null;
		try{
			query=" SELECT COUNT(*) FROM Campaigns WHERE users IN ( "+userId+" ) and createdDate between '"+fromDateString+"' AND '"+toDateString+"'";
			logger.info(query);
			List list = executeQuery(query);
    		if(list.size()>0)
    			return ((Long)list.get(0)).intValue();
    		else
    			return 0;
		}
		catch(Exception e) {
			logger.error("exception while retrieving campaign list",(Throwable)e);
		}
		return campList.size();
	}*/
	
	/*public List<Campaigns> getCampaignsByCampaignName(String campaignName,Long userId, int firstResult, int maxResult,String orderby_colName,String desc_Asc) throws Exception{
		String query = null;
		List<Campaigns> campList = null;
		try{
			
			query="FROM Campaigns WHERE users IN ( "+userId+" ) and campaignName like '"+"%"+campaignName+"%"+"' order by "+ orderby_colName +" "+desc_Asc;
			campList = executeQuery(query, firstResult, maxResult);
		}catch(Exception e) {
			logger.error("exception while retrieving campaign list",(Throwable)e);
		}
		logger.info("inside getCampaignsByCampaignName() campList.size() = "+campList.size());
		return campList;
	}*/
	
	/*public int getCountByCampaignName(String campaignName, Long userId) {
		
			try{
    		
    		String query = null;
    	
    		query=" SELECT count(*) FROM Campaigns WHERE users IN ( "+userId+" ) and campaignName LIKE '%"+campaignName+"%'" ;
    		
    		
    		List list = getHibernateTemplate().find(query);
    		if(list.size()>0)
    			return ((Long)list.get(0)).intValue();
    		else
    			return 0;
    	}catch(Exception e){
    		logger.error("**Exception :"+e+"**");
    		return 0;
    	}
		
	
		
		
	}*/
	
	
	/*public List<Campaigns> findAllByUser(Set<Long> userIds) {
		
		String userIdsStr = Utility.getUserIdsAsString(userIds);
		
		String query = "FROM Campaigns WHERE users IN(" +userIdsStr +") order by modifiedDate desc";
		
		return getHibernateTemplate().find(query);
		
		
	}*/
	
	/*public List<Campaigns> findAllByUser(Long userId) {
		
		//String userIdsStr = Utility.getUserIdsAsString(userIds);
		
		String query = "FROM Campaigns WHERE users IN(" +userId +") order by modifiedDate desc";
		
		return getHibernateTemplate().find(query);
		
		
	}*/
	
	
	
	/*public void deleteByCollection(Collection list){
		getHibernateTemplate().deleteAll(list);
	}*/

	public void setStatus(Campaigns campaign,String status){
		int result = getHibernateTemplate().bulkUpdate("update Campaigns set status = '" + status + "' where campaignId = '" + campaign.getCampaignId() + "'");
	}
	
	/**
	 * Checks for the Campaign exist with the campaignName for that userId
	 * 
	 * @param campaignName
	 * @param userId
	 * @return - boolean : Returns true if Campaign exists with the provided name for the user, else returns false 
	 */
	/*public boolean checkName(String campaignName,Long userId){
		campaignName = StringEscapeUtils.escapeSql(campaignName);
		List list = getHibernateTemplate().find("From Campaigns where campaignName = '" + campaignName + "' and users= " + userId);
		if(list.size()>0){
			return true;
		}else{
			return false;
		}
		
	}*/

    /*public List findDueCampaigns() throws Exception {
    	List dueCampaigns = null;
    	try{
    		dueCampaigns = getHibernateTemplate().find("from Campaigns where status  in ('Active','Running','Stopped') and draftStatus = 'complete'");
            logger.info("Total No. of Campaigns:  " + dueCampaigns.size());
    	}catch(Exception e){
    		logger.error(" ** Exception :"+ e +" **");
    	}
    	return dueCampaigns;
    }*/
   
   /* public int getCount(Set<Long> userIds, String status) {
    	try{
    		
    		String query = null;
    		String userIdsStr = Utility.getUserIdsAsString(userIds);
    		
    		if(status.equals("All")) {
    			query=" SELECT count(*) FROM Campaigns WHERE users IN ( "+userIdsStr+" ) ";
    		}
    		else {	
    			query=" SELECT count(*) FROM Campaigns WHERE users IN ( "+userIdsStr+" ) and status LIKE '" + status + "'" ;
    		}
    		
    		List list = getHibernateTemplate().find(query);
    		if(list.size()>0)
    			return ((Long)list.get(0)).intValue();
    		else
    			return 0;
    	}catch(Exception e){
    		logger.error("**Exception :"+e+"**");
    		return 0;
    	}
    }
    */
    
    
    /*public int getCount(Long userId, String status) {
    	try{
    		
    		String query = null;
    		//String userIdsStr = Utility.getUserIdsAsString(userIds);
    		
    		if(status.equals("All")) {
    			query=" SELECT count(*) FROM Campaigns WHERE users IN ( "+userId+" ) ";
    		}
    		else {	
    			query=" SELECT count(*) FROM Campaigns WHERE users IN ( "+userId+" ) and status LIKE '" + status + "'" ;
    		}
    		
    		List list = getHibernateTemplate().find(query);
    		if(list.size()>0)
    			return ((Long)list.get(0)).intValue();
    		else
    			return 0;
    	}catch(Exception e){
    		logger.error("**Exception :"+e+"**");
    		return 0;
    	}
    }*/
    
    /**
     * this method fetches the records based on the created date of campaigns (dashboard)
     */
    /*public List<Campaigns> getLatestCampaings(int firstResult , int maxResults) {
    	List<Campaigns> numOfRepList=null;
    	try {
			
			String qry=" from Campaigns ORDER BY modifiedDate desc";
			numOfRepList = executeQuery(qry, firstResult, maxResults);
			logger.debug("*****"+numOfRepList.size());
			
		} catch (DataAccessResourceFailureException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
		} catch (HibernateException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
		} catch (Exception e) {
			logger.error("Exception ::" , e);
		}
		return numOfRepList;
    }*/
    
    
    public int deleteByCampaignId(String campaignIdStr) {
		return getHibernateTemplate().bulkUpdate(
				"DELETE FROM Campaigns WHERE campaignId in (" + campaignIdStr + ")");
	}
    
    public int deleteByCampaignIdFromIntermediateTable(String campaignIdStr) {
    	
    	String qry = "DELETE FROM mlists_campaigns WHERE campaign_id in("+campaignIdStr+")";
    	return getJdbcTemplate().update(qry);
    	
    	
    }//deleteByCampaignIdFromIntermediateTable
	
    
    /*public Campaigns findIfSegmentAssociates(String searchStr){
    	
    	String hql = "FROM Campaigns WHERE listsType='"+searchStr+"'";
    	
    	List<Campaigns> segAssocitedCampList = getHibernateTemplate().find(hql);
    	
    	if(segAssocitedCampList.size() > 0) {
    		
    		return (Campaigns)segAssocitedCampList.get(0);
    		
    	}
    	
    	return null;
    }*/
   /*public Campaigns findByCampaignId(Long campaignId) {
    	
    	try {
			List<Campaigns> campList =  getHibernateTemplate().find(
					" FROM Campaigns WHERE campaignId = "+campaignId);
			
			if(campList == null || campList.size() == 0) {
				return null;
			}
			else {
				return campList.get(0);
			}
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
			return null;
		}
    }*/
    
   /*public List<Campaigns> findAllSentCampaigns(Long userId) {
		
		try {
			//String userIdsStr = Utility.getUserIdsAsString(userIds);
			String query = "FROM Campaigns WHERE users="+userId.longValue()+" and campaignName" +
						" IN(SELECT DISTINCT campaignName FROM CampaignReport WHERE user="+userId.longValue()+")";
			
			return getHibernateTemplate().find(query);
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ",e);
			return null;
		}
		 
		
		
	}*/
	
   /*public List<Object[]> findAllSentCampaignsBySql(Long userId) {
		
		try {
			//String userIdsStr = Utility.getUserIdsAsString(userIds);
			String queryStr = "SELECT c.campaign_name,c.campaign_id,  max(cr.cr_id) FROM campaigns c, campaign_report cr" +
					" WHERE c.campaign_name=cr.campaign_name AND c.user_id="+userId.longValue()+" GROUP BY cr.campaign_name  ";
			
			List<Object[]> catList = new ArrayList<Object[]>();
			
			catList = jdbcTemplate.query(queryStr,	new RowMapper(){
    			Object[] obj;
				@Override
				public Object mapRow(ResultSet rs, int rowNum)
						throws SQLException {
					obj = new Object[3];
					obj[0] = rs.getString(1);
					obj[1] = rs.getLong(2);
					obj[2] = rs.getLong(3);
					
					return obj;
				}
    			
    		});
    		return catList;
			
			
			
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ",e);
			return null;
		}
		 
		
		
	}*/
   
   
   /*public List<Object[]> findAllLatestSentCampaignsBySql(Long userId, String campIds) {
		
		try {
			//String userIdsStr = Utility.getUserIdsAsString(userIds);
			String queryStr = "SELECT  max(cr.cr_id) FROM campaigns c, campaign_report cr" +
					" WHERE  c.campaign_id IN("+campIds+" ) AND c.campaign_name=cr.campaign_name AND c.user_id="+userId.longValue()+" GROUP BY cr.campaign_name  ";
			
			String queryStr = "SELECT  max(cr.cr_id) FROM campaigns c, campaign_report cr" +
					" WHERE c.campaign_id IN("+campIds+" ) AND c.campaign_name=cr.campaign_name "
							+ " AND c.user_id="+userId.longValue()+" AND cr.user_id="+userId.longValue()+" GROUP BY cr.campaign_name  ";
			
			
			List<Object[]> catList = new ArrayList<Object[]>();
			
			catList = jdbcTemplate.query(queryStr,	new RowMapper(){
   			Object[] obj;
				@Override
				public Object mapRow(ResultSet rs, int rowNum)
						throws SQLException {
					obj = new Object[1];
					obj[0] = rs.getLong(1);
					
					return obj;
				}
   			
   		});
   		return catList;
			
			
			
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ",e);
			return null;
		}
		 
		
		
	}*/

   /**
    * This method get's List of all active campaigns.
    * @param userId
    * @param campaignStatusActive
    * @return campaigns
    */
   /*public List<Campaigns> findCampaignByUserIdAndStatus(Long userId,String campaignStatus) {
	   logger.debug(">>>>>>> Started  findCampaignByUserIdAndStatus :: ");
	   List<Campaigns> campaigns = null;
	   String queryString = "FROM Campaigns WHERE users="+userId.longValue()+" and status='"+campaignStatus+"' order by 1 desc" ;
	   logger.info(" Query String  ::"+queryString);
	   campaigns = getHibernateTemplate().find(queryString);
	   
	   if(campaigns != null && campaigns.size() >0){
		   logger.info("campaigns.size() ::"+campaigns.size());
		   return campaigns;
	   }
	   logger.debug("<<<<< Completed findCampaignByUserIdAndStatus .");
	   return campaigns;
   }*/

   /**
    * Added for 2.3.11
    * This method find All UpComing Schedules
    * @param campaignId
    * @return campaignScheduleList
    */
  /* public List<CampaignSchedule> findAllUpComingSchedules(Long campaignId,byte status) {
	   logger.debug(">>>>>>> Started  findAllUpComingSchedules :: ");
	   List<CampaignSchedule> campaignScheduleList = null;
	   String str ="status=1";
	   if(OCConstants.ACTIVE_EMAIL_STATUS == status){
		   str ="status!=1";
	   }
	   try {
		   String queryString  = "FROM CampaignSchedule WHERE campaignId ="+campaignId+" AND "+str+" ORDER BY scheduledDate DESC";
		   campaignScheduleList = getHibernateTemplate().find(queryString);
		   logger.debug("queryString :::::"+queryString);
		   if(campaignScheduleList != null && campaignScheduleList.size() >0){
			   logger.info("campaigns.size() ::"+campaignScheduleList.size());
			   return campaignScheduleList;
		   }
		   logger.debug("<<<<< Completed findAllUpComingSchedules .");
		   return campaignScheduleList;
	   } catch (DataAccessException e) {
		   logger.error("Exception ",e);
		   return campaignScheduleList;
	   }
   }*///findAllUpComingSchedules

   /**
    * 
    * @param campaignId
    * @return
    */
   /*public int findAllUpComingSchedulesSize(Long campaignId,byte status) {
	   logger.debug(">>>>>>> Started  findAllUpComingSchedulesSize :: ");
	   List<CampaignSchedule> campaignScheduleList = null;
	   int size = 0;
	   String str ="status=1";
	   if(OCConstants.ACTIVE_EMAIL_STATUS == status){
		   str ="status!=1";
	   }
	   try {
		   String queryString  = "FROM CampaignSchedule WHERE campaignId ="+campaignId+" AND "+str+" ORDER BY scheduledDate DESC";
		   campaignScheduleList = getHibernateTemplate().find(queryString);
		   logger.debug("queryString :::::"+queryString);
		   if(campaignScheduleList != null && campaignScheduleList.size() >0){
			   logger.info("campaigns.size() ::"+campaignScheduleList.size());
			   size = campaignScheduleList.size();
			   if(size >0){
				   size = size - 1;
			   }
			   return size;
		   }
		   logger.debug("<<<<< Completed findAllUpComingSchedulesSize .");
		   return size;
	   } catch (DataAccessException e) {
		   logger.error("Exception ",e);
		   return size;
	   }
   }*///findAllUpComingSchedulesSize

  /**
   * This method find All UpComing Schedules Size
   * @param firstResult
   * @param maxResults
   * @param campaignId
   * @param status
   * @return campaignScheduleList
   */
/*public List<CampaignSchedule> findAllUpComingSchedulesSize(int firstResult,int maxResults, Long campaignId, byte status) {
	logger.debug(">>>>>>> Started  findAllUpComingSchedulesSize :: ");
	 List<CampaignSchedule> campaignScheduleList = null;
	 String str ="status=1";
	   if(OCConstants.ACTIVE_EMAIL_STATUS == status){
		   str ="status!=1";
	   }
	   try {
		   String queryString  = "FROM CampaignSchedule WHERE campaignId ="+campaignId+" AND "+str+" ORDER BY scheduledDate";
		   logger.debug("queryString :::::"+queryString+"\t :"+firstResult+"\t :"+ maxResults);
		   List list = executeQuery(queryString, firstResult, maxResults);
		   campaignScheduleList = (List<CampaignSchedule>)list;
		   if(campaignScheduleList != null && campaignScheduleList.size() >0){
			   logger.info("campaigns.size() ::"+campaignScheduleList.size());
			   logger.debug("<<<<< Completed findAllUpComingSchedulesSize .");
			   return campaignScheduleList;
		   }
		   logger.debug("<<<<< Completed findAllUpComingSchedulesSize .");
		   return campaignScheduleList;
	   } catch (DataAccessException e) {
		   logger.error("Exception ",e);
		   return campaignScheduleList;
	   }
}*///findAllUpComingSchedulesSize


}//EOF
