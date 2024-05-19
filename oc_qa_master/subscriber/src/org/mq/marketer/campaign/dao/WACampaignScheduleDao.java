package org.mq.marketer.campaign.dao;

import java.util.List;

import org.mq.marketer.campaign.beans.CampaignSchedule;
import org.mq.marketer.campaign.beans.WACampaignsSchedule;
import org.mq.marketer.campaign.beans.WACampaign;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

public class WACampaignScheduleDao extends AbstractSpringDao {
	
	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	private CampaignReportDao campaignReportDao = null;
	public WACampaignScheduleDao() {
			// TODO Auto-generated constructor stub
		}
	
	public CampaignReportDao getCampaignReportDao() {
		return campaignReportDao;
	}
	public void setCampaignReportDao(CampaignReportDao campaignReportDao) {
		this.campaignReportDao = campaignReportDao;
	}
	
/*	public void saveOrUpdate(WACampaignSchedule WACampaignSchedule) {
		super.saveOrUpdate(WACampaignSchedule);
	}

	public void delete(WACampaignSchedule WACampaignSchedule) {
	    super.delete(WACampaignSchedule);
	}*/
	
	public List findAll() {
	    return super.findAll(WACampaignsSchedule.class);
	}	
	
	public  WACampaignsSchedule findById(Long id){
		WACampaignsSchedule WACampaignSchedule = null;
		String quString = "from WACampaignsSchedule where WACsId="+id;
		try{
			
			WACampaignSchedule =(WACampaignsSchedule) executeQuery(quString).get(0);
			return WACampaignSchedule;
		}catch (Exception e) {
			logger.error("Exception ::" , e);
			return null;
		}
	}
	
public WACampaignsSchedule findByWACampRepId(Long WACampRepId) {
		
		List list= getHibernateTemplate().find("FROM WACampaignsSchedule WHERE crId ="+WACampRepId);
		
		if(list != null && list.size() > 0) {
			return (WACampaignsSchedule)list.get(0);
		}
		else {
			return null;
		}
	}
	
	 private Long id;
	
	public synchronized Long getCurrentId() {
		
		try {
			if(id == null) {
				
				List list = getHibernateTemplate().find("SELECT MAX(waCsId) FROM WACampaignsSchedule ") ;
				logger.info(" List :"+list);
				this.id = (list != null && list.size() > 0 && list.get(0) != null) ? (Long)list.get(0):0 ;
				
			}
			return ++id;
		} catch (DataAccessException e) {
			logger.error("** Exception : while getting the current id ", e);
			return id+100000;
		}
		
	}
	 
	public int deleteByCampaignId(Long WACampaignId) {
		return getHibernateTemplate().bulkUpdate(
				"DELETE FROM WACampaignSchedule WHERE waCampaignId="+WACampaignId);
	}
	
	public int deleteByCampaignId(String WACampaignId) {
		return getHibernateTemplate().bulkUpdate(
				"DELETE FROM WACampaignSchedule WHERE waCampaignId in("+WACampaignId+")");
	}

	public List<WACampaignsSchedule> getByWACampaignId(Long WACampaignId) {
		return getHibernateTemplate().find(
				" FROM WACampaignsSchedule WHERE waCampaignId="+WACampaignId+" ORDER BY scheduledDate");
	}
	
	public List<Object[]> getAllChidren(long WACsId,long WACampaignId) {
		List<Object[]> list=null;
			
			list=getHibernateTemplate().find("select cs.waCsId from WACampaignsSchedule cs where cs.waCsId "+ 
					"in(select cs1.parentId from WACampaignsSchedule cs1 where cs1.parentId="+WACsId+"and cs1.waCampaignId="+WACampaignId+")");
			logger.debug("number of children are************"+list.size());
			if(list != null && list.size() > 0) {
				return list;
			}else {
				return null;
		}
		
	}
	public int getWACampaignCount(Long userIds) {
    	try{
    		String query = null;
    		query=" SELECT count(distinct waCampaignId) FROM WACampaignsSchedule WHERE userId IN ( "+userIds.longValue()+" ) and status = 0 " ;
    		
    		List list = getHibernateTemplate().find(query);
    		
    		if(list != null && list.size()>0)
    			return ((Long)list.get(0)).intValue();
    		
    	}catch(Exception e) {
    		logger.error("**Exception :", e );
    	}
    	return 0;
    }

	public void deleteByCampSchId(Long WACsId) {
		logger.debug(">>>>>>> Started WACampaignScheduleDao :: deleteByCampSchId <<<<<<< ");
		 String qry  = " DELETE FROM WACampaignSchedule where waCsId ="+WACsId+ " or parentId ="+WACsId;
		 logger.info(">>>>>>>>>>> qry is  ::"+qry);
		 getHibernateTemplate().bulkUpdate(qry);
		 logger.debug(">>>>>>> Completed WACampaignScheduleDao :: deleteByCampSchId <<<<<<< ");
	 }
	
	
	public List<WACampaignsSchedule> findActiveOrDraftWACampaignSchedules(Long WACampaignId){
		logger.debug(">>>> Started findByWaCampaignId.");
		
		List<WACampaignsSchedule> WACampaignScheduleList = null;
		String queryString = "FROM WACampaignsSchedule WHERE waCampaignId="+WACampaignId+" AND status IN(0,2)";
		
		WACampaignScheduleList = getHibernateTemplate().find(queryString);
		
		if(WACampaignScheduleList != null && WACampaignScheduleList.size() > 0){
			
			logger.debug(">>>> Completed findByWACampaignId.");
			return WACampaignScheduleList;
		}
		
		logger.debug(">>>> Completed findByWACampaignId, but WACampaignScheduleList is either null or of zero size.");
		return WACampaignScheduleList;
	}
}
