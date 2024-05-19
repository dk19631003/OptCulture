package org.mq.marketer.campaign.dao;

import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.HibernateException;
import org.mq.marketer.campaign.beans.CampaignReport;
import org.mq.marketer.campaign.beans.CampaignSchedule;
import org.mq.marketer.campaign.controller.campaign.CampFinalController;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.utils.OCConstants;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.jdbc.core.JdbcTemplate;

@SuppressWarnings({"unchecked"})
public class CampaignScheduleDao extends AbstractSpringDao {

	
	private JdbcTemplate jdbcTemplate;
	
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}



	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
    public CampaignScheduleDao() {}
    
    public CampaignSchedule find(Long id) {
        return (CampaignSchedule) super.find(CampaignSchedule.class, id);
    }

    /*public void saveOrUpdate(CampaignSchedule campaignSchedule) {
        super.saveOrUpdate(campaignSchedule);
    }*/

    /*public void delete(CampaignSchedule campaignSchedule) {
        super.delete(campaignSchedule);
    }*/

	public List findAll() {
        return super.findAll(CampaignSchedule.class);
    }
	
	public List<CampaignSchedule> getByCampaignId(Long campaignId) {
		return getHibernateTemplate().find(
				" FROM CampaignSchedule WHERE campaignId="+campaignId+" ORDER BY scheduledDate ");
	}
	
	public int getActiveCountByCampaignId(Long userID, Long campaignId) {
		try{
    		String query = null;
    		query=" SELECT COUNT(*) FROM CampaignSchedule WHERE user="+userID+" AND campaignId="+campaignId+" AND status=0 ";
    		
    		List list = getHibernateTemplate().find(query);
    		
    		if(list.size()>0)
    			return ((Long)list.get(0)).intValue();
    		
    	}catch(Exception e) {
    		logger.error("**Exception :", e );
    	}
		return 0;
	}
	
	
	private Long id;
	
	public synchronized Long getCurrentId() {
		
		try {
			if(id == null) {
				
				List list = getHibernateTemplate().find("SELECT MAX(csId) FROM CampaignSchedule ") ;
				logger.info(" List :"+list);
				this.id = (list != null && list.size() > 0 && list.get(0) != null) ? (Long)list.get(0):0 ;
				
			}
			return ++id;
		} catch (DataAccessException e) {
			logger.error("** Exception : while getting the current id ", e);
			return id+100000;
		}
		
	}
	
	/*public int deleteByCampaignId(Long campaignId) {
		
		
		return jdbcTemplate.update("DELETE FROM campaign_schedule WHERE campaign_id="+campaignId);
		return getHibernateTemplate().dbulkUpdate(
				"DELETE FROM CampaignSchedule WHERE campaignId="+campaignId);
	}*/
	
	/*public int deleteByCampaignId(String campaignIdStr) {
		return getHibernateTemplate().bulkUpdate(
				"DELETE FROM CampaignSchedule WHERE campaignId in (" + campaignIdStr + ")");
	}*/

	public CampaignSchedule findById(Long campScheduleId) {
		
		List list = getHibernateTemplate().find("FROM CampaignSchedule WHERE parentId ="+campScheduleId);
		
		if(list != null && list.size() > 0) {
			return (CampaignSchedule)list.get(0);
		}
		else {
			return null;
		}
	}
public CampaignSchedule findByCampRepId(Long campRepId) {
		
		List list = getHibernateTemplate().find("FROM CampaignSchedule WHERE crId ="+campRepId);
		
		if(list != null && list.size() > 0) {
			return (CampaignSchedule)list.get(0);
		}
		else {
			return null;
		}
	}

	public List<Object[]> getAllChidren(long csId,long campaignId) {
		List<Object[]> list=null;
			
			list=getHibernateTemplate().find("select cs.csId from CampaignSchedule cs where cs.csId "+ 
					"in(select cs1.parentId from CampaignSchedule cs1 where cs1.parentId="+csId+"and cs1.campaignId="+campaignId+")");
			logger.debug("number of children are************"+list.size());
			if(list != null && list.size() > 0) {
				return list;
			}else {
				return null;
		}
	}

	
	 public int getActiveCampaignCount(Long userIds) {
    	try{
    		String query = null;
    		query=" SELECT count(distinct campaignId) FROM CampaignSchedule WHERE user IN ( "+userIds.longValue()+" ) and status = 0 " ;
    		
    		List list = getHibernateTemplate().find(query);
    		
    		if(list.size()>0)
    			return ((Long)list.get(0)).intValue();
    		
    	}catch(Exception e) {
    		logger.error("**Exception :", e );
    	}
    	return 0;
    }
	 
	 public List<CampaignSchedule> findAllByCampaignId(Long campaignId, Long userId) {
		 
		 List<CampaignSchedule> list = null;
		 list=getHibernateTemplate().find(" FROM   CampaignSchedule where user="+userId.longValue()+ " AND campaignId="+campaignId.longValue()+ " ORDER BY scheduledDate desc");
		return list;
		 
		 
		 
	 }//findAllByCampaignId()
	 
	 /*
	     * this method fetches the desired number of records(dashboard)  
	     */
	    
	    public List<CampaignSchedule> findBynumberOfSchedules(int startFrom, int count,String status) {
	    	
	    	String appendStatusQry = "";
	    	if(status!=null && !status.isEmpty() && !status.equals("--All--")) {
	    		appendStatusQry = " WHERE status = " + status;
	    	}
	    	List<CampaignSchedule> numOfSchedList=null;
	    	try {
				
				String qry=" from CampaignSchedule "+ appendStatusQry + " ORDER BY scheduledDate desc";
				numOfSchedList = executeQuery(qry, startFrom, count);
				logger.info("qry is >>>>>"+qry);

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
			return numOfSchedList;
	    }//findBynumberOfReports()
	 
	    /*
	     * this method fetches the desired number of records for desired user (dashboard)
	     */
	    
	    public List<CampaignSchedule> findByOrgIdAndUserId(Long orgId, Long userId,int startFrom,int count,String status) {
	    	List<CampaignSchedule> numOfSchedList=null;
	    	
	    	String appendStatusQry = "";
	    	try {
		    	if(status!=null && !status.isEmpty() && !status.equals("--All--")) {
		    		appendStatusQry = " AND cs.status = " + status;
		    	}
		    	
		    	String appendQuery = "";
		    	
		    	if(userId != null){
		    		appendQuery = " AND usr.userId = " +userId.longValue();
		    	}
		    	
		    	String qry = "SELECT DISTINCT cs FROM CampaignSchedule cs , Users usr  where cs.user = usr.userId " +
						"AND usr.userOrganization = " + orgId + appendQuery + appendStatusQry + " ORDER BY cs.scheduledDate desc";
		   
			    logger.info(">>>>>>>>>>>>>>>>>>>>>>>>query is ::::"+qry);
				numOfSchedList=executeQuery(qry,startFrom,count);
			    logger.info("scheduled based on selection "+numOfSchedList.size());
	    	
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
		    
			return numOfSchedList;
	    	
	    	
	    }//findByUserName()
	 
	 /*public void deleteCampSchById (Long id) {
		 String qry  = " DELETE FROM CampaignSchedule where csId ="+id+ " or parentId ="+id;
		 logger.info(">>>>>>>>>>> qry is  ::"+qry);
		 getHibernateTemplate().bulkUpdate(qry);
	 }*/

	 /**
	  * This method find's Campaign Schedule by campaingId
	  * @param campaignId
	  * @return
	  */
	 public List<CampaignSchedule> findActiveOrDraftCampaignSchedules(Long campaignId) {
		 logger.debug(">>>> Started findByCampaignId.");
		 List<CampaignSchedule> campaignSchedules = null;
		 String queryString ="FROM CampaignSchedule WHERE campaignId="+campaignId+" and status IN(0,2)";

		 campaignSchedules = getHibernateTemplate().find(queryString);

		 if(campaignSchedules != null && campaignSchedules.size() >0){
			 logger.debug(">>>> Completed findByCampaignId.");
			 return campaignSchedules;
		 }
		 logger.debug(">>>> Completed findByCampaignId.");
		 return campaignSchedules;
	 }//findByCampaignId
	 
	 /**
	  * 
	  * @param startIndex
	  * @param size
	  * @param campaignId
	  * @return
	  */
	public List<CampaignSchedule> findAllSchedules(int firstResult, int maxResults,Long campaignId) {
		   logger.debug("<<<<< Started findAllSchedules .");
		 List<CampaignSchedule> campaignScheduleList = null;
		 try {
			   String queryString  = "FROM CampaignSchedule WHERE campaignId ="+campaignId+" ORDER BY scheduledDate";
//			   logger.debug("queryString :::::"+queryString+"\t :"+firstResult+"\t :"+ maxResults);
			   List list = executeQuery(queryString, firstResult, maxResults);
			   campaignScheduleList = (List<CampaignSchedule>)list;
			   if(campaignScheduleList != null && campaignScheduleList.size() >0){
//				   logger.info("campaigns.size() ::"+campaignScheduleList.size());
				   logger.debug("<<<<< Completed findAllSchedules .");
				   return campaignScheduleList;
			   }
			   logger.debug("<<<<< Completed findAllSchedules .");
			   return campaignScheduleList;
		   } catch (DataAccessException e) {
			   logger.error("Exception ",e);
			   return campaignScheduleList;
		   }
	}//findAllSchedules

	/*public CampaignSchedule findUpComingSchedule(Long campaignId) {
		CampaignSchedule campaignSchedule = null;
		String queryString  = "FROM CampaignSchedule WHERE campaignId ="+campaignId+" ORDER BY scheduledDate";
		
		return null;
	}*/
	/**
	 * This method findUpComingScheduleByStatus
	 * @param campaignId
	 * @param status
	 * @return CampaignScheduleList
	 */
	public CampaignSchedule findUpComingScheduleByStatus(Long campaignId, byte status) {
		logger.debug(">>>>>>> Started  findUpComingScheduleByStatus :: ");
		String queryString  = "FROM CampaignSchedule WHERE campaignId ="+campaignId+" and status="+status+" ORDER BY scheduledDate ";
		try {
//			logger.info("queryString :::::::"+queryString);
			List<CampaignSchedule> list = getHibernateTemplate().find(queryString);

			if(list != null && list.size() > 0){
				logger.debug("<<<<< Completed findUpComingScheduleByStatus .");
				return list.get(0);
			}
		} catch (DataAccessException e) {
			logger.error("Exception ",e);
		}
		logger.debug("<<<<< Completed findUpComingScheduleByStatus .");
		return null;
	}
	/**
	 * This method find All By UpComing Active Schedules
	 * @param campaignId
	 * @return CampaignScheduleList
	 */
	public List<CampaignSchedule> findAllByUpComingActiveSchedules(	Long campaignId) {
		logger.debug(">>>>>>> Started  findAllByUpComingActiveSchedules :: ");
		String queryString  = "FROM CampaignSchedule WHERE campaignId ="+campaignId+" and status="+OCConstants.ACTIVE_EMAIL_STATUS+" ORDER BY scheduledDate ";
		try {
//			logger.info("queryString :::::::"+queryString);
			List<CampaignSchedule> list = getHibernateTemplate().find(queryString);

			if(list != null && list.size() > 0){
				return list;
			}
		} catch (DataAccessException e) {
			logger.error("Exception ",e);
		}
		logger.debug("<<<<< Completed findAllByUpComingActiveSchedules .");
		return null;
	}//findAllByUpComingActiveSchedules

	/**
	 * This method find All Sent Schedules
	 * @param firstResult
	 * @param maxResults
	 * @param campaignId
	 * @return campaignScheduleList
	 */
	public List<CampaignSchedule> findAllSentSchedules(int firstResult,	int maxResults, Long campaignId) {
		logger.debug("<<<<< Started findAllSentSchedules .");
		List<CampaignSchedule> campaignScheduleList = null;
		try {
			String queryString  = "FROM CampaignSchedule WHERE campaignId ="+campaignId+" and status NOT IN(0,2) ORDER BY scheduledDate desc ";
			logger.debug("queryString :::::"+queryString+"\t :"+firstResult+"\t :"+ maxResults);
			List list = executeQuery(queryString, firstResult, maxResults);
			campaignScheduleList = (List<CampaignSchedule>)list;
			if(campaignScheduleList != null && campaignScheduleList.size() >0){
				logger.info("campaigns.size() ::"+campaignScheduleList.size());
				logger.debug("<<<<< Completed findAllSentSchedules .");
				return campaignScheduleList;
			}
			logger.debug("<<<<< Completed findAllSentSchedules .");
			return campaignScheduleList;
		} catch (DataAccessException e) {
			logger.error("Exception ",e);
			return campaignScheduleList;
		}
	}//findAllSentSchedules

	/**
	 * This method delete's CampaignSchedule list
	 * @param list
	 */
	/*public void deleteByCollection(List<CampaignSchedule> list) {
		logger.debug(">>>>>>> Started  deleteByCollection :: ");
		getHibernateTemplate().deleteAll(list);
		logger.debug("<<<<< Completed deleteByCollection .");
	}*///deleteByCollection

	/*public int updateCampaignStatus(Long csId,Long campaignId ,byte status) {
		logger.debug(">>>>>>> Started  updateCampaignStatus :: ");
		int rowsEffected = 0 ;
		String queryString = "UPDATE campaign_schedule SET status="+status+" WHERE cs_id ="+csId+" and campaign_id="+campaignId;
		logger.info("Query String  ..............:"+queryString);
		rowsEffected = jdbcTemplate.update(queryString);
		logger.debug("<<<<< Completed updateCampaignStatus .");
		return rowsEffected;
	}*/

}
