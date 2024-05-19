package org.mq.marketer.campaign.dao;

import java.util.List;

import org.mq.marketer.campaign.beans.CampaignSchedule;
import org.mq.marketer.campaign.beans.SMSCampaignSchedule;
import org.mq.marketer.campaign.beans.SMSCampaigns;
import org.springframework.dao.DataAccessException;

public class SMSCampaignScheduleDaoForDML extends AbstractSpringDaoForDML {
	
	//private CampaignReportDao campaignReportDao = null;
	public SMSCampaignScheduleDaoForDML() {
			// TODO Auto-generated constructor stub
		}
	
	/*public CampaignReportDao getCampaignReportDao() {
		return campaignReportDao;
	}
	public void setCampaignReportDao(CampaignReportDao campaignReportDao) {
		this.campaignReportDao = campaignReportDao;
	}
	*/
	public void saveOrUpdate(SMSCampaignSchedule smsCampaignSchedule) {
		super.saveOrUpdate(smsCampaignSchedule);
	}

	public void delete(SMSCampaignSchedule smsCampaignSchedule) {
	    super.delete(smsCampaignSchedule);
	}
	
	/*public List findAll() {
	    return super.findAll(SMSCampaignSchedule.class);
	}	
	
	public SMSCampaignSchedule findById(Long id){
		SMSCampaignSchedule smsCampaignSchedule = null;
		String quString = "from SMSCampaignSchedule where smsCsId="+id;
		try{
			
			smsCampaignSchedule =(SMSCampaignSchedule) executeQuery(quString).get(0);
			return smsCampaignSchedule;
		}catch (Exception e) {
			logger.error("Exception ::" , e);
			return null;
		}
	}*/
	
	
	public void deleteByCollection(List<SMSCampaignSchedule> smsCampScheduleList) {
		logger.debug(">>>>>>> Started SMSCampaignScheduleDao :: deleteByCollection <<<<<<< ");
		getHibernateTemplate().deleteAll(smsCampScheduleList);
		logger.debug(">>>>>>> Completed SMSCampaignScheduleDao :: deleteByCollection <<<<<<< ");
		
	}
/*private Long id;
	
	public synchronized Long getCurrentId() {
		
		try {
			if(id == null) {
				
				List list = getHibernateTemplate().find("SELECT MAX(smsCsId) FROM SMSCampaignSchedule ") ;
				logger.info(" List :"+list);
				this.id = (list != null && list.size() > 0 && list.get(0) != null) ? (Long)list.get(0):0 ;
				
			}
			return ++id;
		} catch (DataAccessException e) {
			logger.error("** Exception : while getting the current id ", e);
			return id+100000;
		}
		
	}*/
	 
	public int deleteByCampaignId(Long smsCampaignId) {
		return getHibernateTemplate().bulkUpdate(
				"DELETE FROM SMSCampaignSchedule WHERE smsCampaignId="+smsCampaignId);
	}
	
	public int deleteByCampaignId(String smsCampaignId) {
		return getHibernateTemplate().bulkUpdate(
				"DELETE FROM SMSCampaignSchedule WHERE smsCampaignId in("+smsCampaignId+")");
	}

	/*public List<SMSCampaignSchedule> getBySmsCampaignId(Long smsCampaignId) {
		return getHibernateTemplate().find(
				" FROM SMSCampaignSchedule WHERE smsCampaignId="+smsCampaignId+" ORDER BY scheduledDate");
	}
	
	public List<Object[]> getAllChidren(long smsCsId,long smsCampaignId) {
		List<Object[]> list=null;
			
			list=getHibernateTemplate().find("select cs.smsCsId from SMSCampaignSchedule cs where cs.smsCsId "+ 
					"in(select cs1.parentId from SMSCampaignSchedule cs1 where cs1.parentId="+smsCsId+"and cs1.smsCampaignId="+smsCampaignId+")");
			logger.debug("number of children are************"+list.size());
			if(list != null && list.size() > 0) {
				return list;
			}else {
				return null;
		}
		
	}*/

	public void deleteByCampSchId(Long smsCsId) {
		logger.debug(">>>>>>> Started SMSCampaignScheduleDao :: deleteByCampSchId <<<<<<< ");
		 String qry  = " DELETE FROM SMSCampaignSchedule where smsCsId ="+smsCsId+ " or parentId ="+smsCsId;
		 logger.info(">>>>>>>>>>> qry is  ::"+qry);
		 getHibernateTemplate().bulkUpdate(qry);
		 logger.debug(">>>>>>> Completed SMSCampaignScheduleDao :: deleteByCampSchId <<<<<<< ");
	 }
	
	
	/*public List<SMSCampaignSchedule> findActiveOrDraftSMSCampaignSchedules(Long smsCampaignId){
		logger.debug(">>>> Started findBySMSCampaignId.");
		
		List<SMSCampaignSchedule> smsCampaignScheduleList = null;
		String queryString = "FROM SMSCampaignSchedule WHERE smsCampaignId="+smsCampaignId+" AND status IN(0,2)";
		
		smsCampaignScheduleList = getHibernateTemplate().find(queryString);
		
		if(smsCampaignScheduleList != null && smsCampaignScheduleList.size() > 0){
			
			logger.debug(">>>> Completed findBySMSCampaignId.");
			return smsCampaignScheduleList;
		}
		
		logger.debug(">>>> Completed findBySMSCampaignId, but smsCampaignScheduleList is either null or of zero size.");
		return smsCampaignScheduleList;
	}*/
}
