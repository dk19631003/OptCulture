


package org.mq.captiway.scheduler.dao;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.Campaigns;
import org.mq.captiway.scheduler.utility.Constants;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;


@SuppressWarnings({"unchecked","serial","unused"})
public class CampaignsDao extends AbstractSpringDao {
	
	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	
	private JdbcTemplate jdbcTemplate;
    
    
    public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
    public CampaignsDao() {}
//    private SessionFactory sessionFactory;

    public Campaigns find(Long id) {
        return (Campaigns) super.find(Campaigns.class, id);
    }

    /*public void saveOrUpdate(Campaigns campaigns) {
        super.saveOrUpdate(campaigns);
    }*/

    /*public void delete(Campaigns campaigns) {
        super.delete(campaigns);
    }*/

    public List<Campaigns> findAll() {
        return super.findAll(Campaigns.class);
    }

    public List<Campaigns> findByCampaignName(String name) {
    	
        return getHibernateTemplate().find(
        		" FROM Campaigns WHERE campaignName = '" + name + "' ");
    }

    public Campaigns findByCampaignName(String name,Long userId) {
    	
        try {
			List<Campaigns> campaignList = getHibernateTemplate().find(
					" FROM Campaigns WHERE campaignName = '" + name + "' AND users=" + userId);
			
			if(campaignList.size()>0){
				return (Campaigns)campaignList.get(0);
			}else
				return null;
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::::" , e);
			return null;
		}
    }
    
    public List<Campaigns> findByUser(Long userId) {
    	
        return getHibernateTemplate().find(
        		" FROM Campaigns WHERE users.userId= '" + userId + "'");
    }


    public Campaigns findByCampaignId(Long campaignId) {
    	
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
			logger.error("Exception ::::" , e);
			return null;
		}
    }
	public List<Object[]> getDraftStatusCampigns(Long userId) {
		
		/*String qry = "FROM Campaigns camp,CampaignSchedule cs WHERE cs.status=0 AND camp.status='Draft'"
				+ "AND camp.users ="+userId+" AND camp.users=cs.user AND camp.campaignId=cs.campaignId";*/
		String qry = "SELECT camp.campaign_name FROM campaigns camp,campaign_schedule cs "
		+"WHERE camp.user_id ="+userId+" "
		+"AND camp.user_id=cs.user_id AND camp.campaign_id=cs.campaign_id AND cs.status=0 AND camp.status='"+Constants.CAMP_STATUS_DRAFT+"' "
				+ " GROUP BY camp.campaign_name" ;
		
		//logger.info("getDraftStatusCampigns "+qry);
		List<Object[]> catList = new ArrayList<Object[]>();
		
		catList = jdbcTemplate.query(qry,	new RowMapper(){
			Object[] obj;
			@Override
			public Object mapRow(ResultSet rs, int rowNum)
					throws SQLException {
				obj = new Object[1];
				obj[0] = rs.getString(1);
				
				return obj;
			}
			
		});
		return catList;
		
	}
    
    /**
     * Updates the campaigns status to either 'Sent' or 'Running' based on 
	 * the no of active schedules for this campaign in campaign_schedule. 
	 * If for this campaignId in campaign_schedule table the number of 
	 * rows exists with status as '0' that means active schedules then the 
	 * campaign will be updated as 'Running' campaign if no rows exists with
	 * the status as '0' then the campaign will be updated as 'Sent'
	 * 
     * @param campaignId -Id(Long) of the campaign. 
     */
    
    /*public void updateCampaignStatus(Long campaignId) {
    	
    	String qryStr = 
    		" UPDATE campaigns c SET c.status = ( SELECT IF(count(cs.status)>0,'Running','Sent')" +
    		" FROM campaign_schedule cs WHERE cs.campaign_id="+campaignId+" AND cs.status=0)" +
    		" WHERE c.campaign_id="+campaignId ;
    	int result = executeJdbcUpdateQuery(qryStr);
    	
    	if(result <= 0) {
    		if(logger.isWarnEnabled()) logger.warn("Campaign status could not be updated for the campaign id :"+campaignId);
    	}
    	
    }*/
    
public List<Object[]> findAllLatestSentCampaignsBySql(Long userId, String campIds) {
		
		try {
			//String userIdsStr = Utility.getUserIdsAsString(userIds);
			//fix for mothers day 2015 campaign
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
		 
		
		
	}

	public Campaigns getSingleCampaign(String name, Long userId) {
		
		name = StringEscapeUtils.escapeSql(name);
	    List list = getHibernateTemplate().find ("from Campaigns where campaignName = '" + name + "' and users= " + userId + "ORDER BY 1 DESC");
	
	    if(list==null || list.size()==0) {
	    	return null;
	    }
	    
	    if (list.size() > 1) {
	        logger.error("** Problem:Size of List is not 1 ;should be ideally 1 ** ");
	    }
	    
	    Campaigns campaign = (Campaigns) list.get(0);
	
	    return campaign; 
	}
    
}
