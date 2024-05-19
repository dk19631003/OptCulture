package org.mq.marketer.campaign.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.EventTrigger;
import org.mq.marketer.campaign.beans.TriggerCustomEvent;
import org.mq.marketer.campaign.general.Constants;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

public class EventTriggerDaoForDML extends AbstractSpringDaoForDML {
	
	
		private JdbcTemplate jdbcTemplate;
	
		public JdbcTemplate getJdbcTemplate() {
			return jdbcTemplate;
		}

		public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
			this.jdbcTemplate = jdbcTemplate;
		}


		private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);

		public  EventTriggerDaoForDML() {
			// TODO Auto-generated constructor stub
		}

		/*public EventTrigger find(Long id) {
			return (EventTrigger) super.find(EventTrigger.class, id);
		}*/

		public void saveOrUpdate(EventTrigger eventTrigger) {
			
			super.saveOrUpdate(eventTrigger);
		}

		/*public void delete(EventTrigger eventTrigger) {
			super.delete(eventTrigger);
		}*/
		public void delete(EventTrigger eventTrigger) {
			super.delete(eventTrigger);
		}

		/*public List<EventTrigger> findAll() {
			return super.findAll(EventTrigger.class);
		}*/	
		
		/*public List<EventTrigger> findAllByUserId(Long userId, int startFrom, int count,boolean check) {
			String value="";
			if(check==false)
				value="lastSentDate";
			else
				value="triggerModifiedDate";
			
			String qry = "FROM EventTrigger  WHERE users="+userId+ " ORDER BY "+value+" DESC ";
			
			List<EventTrigger> list = executeQuery(qry, startFrom, count);
			//List<EventTrigger> list = getHibernateTemplate().find();
			return list;
		}*/	
		
      /*public List<EventTrigger> findAllBetweenDatesByUserId(Long userId, String fromDate, String endDate,int start_Idx, int endIdx, String status) {
    	  
    	    List<EventTrigger> list;
    	    List<EventTrigger> finalList = new ArrayList<EventTrigger>();
    	    String qry=null;
    	    if(fromDate==null && endDate==null)
    	    	qry= "FROM EventTrigger  WHERE users="+userId+" ORDER BY lastSentDate DESC";
    	    else
    	    String qry = "FROM EventTrigger  WHERE users="+userId+" AND lastSentDate BETWEEN '"+fromDate+"' AND '"+endDate+"' ORDER BY lastSentDate DESC";
    	    
			if(start_Idx == -1){ // given for first time loading of the page...in doAfterCompose()
				list = executeQuery(qry);
			}else{
				list = executeQuery(qry,start_Idx, endIdx);
			}
    	    //String qry = "FROM EventTrigger  WHERE users="+userId+ " ORDER BY triggerModifiedDate DESC ";
			logger.info("qry======="+qry);
			
			for(EventTrigger anEventTrigger : list){
				if(status.equals("Active")) {
					
					if((anEventTrigger.getOptionsFlag() & Constants.ET_TRIGGER_IS_ACTIVE_FLAG) == 1){
						finalList.add(anEventTrigger);
					}
	    	    }else if(status.equals("InActive")){
	    	    	
	    	    	if((anEventTrigger.getOptionsFlag() & Constants.ET_TRIGGER_IS_ACTIVE_FLAG) != 1){
						finalList.add(anEventTrigger);
					}
	    	    	
	    	    }else if(status.equals("All")){
	    	    	    finalList.add(anEventTrigger);
	    	    }
			}
			
			
			
			
			logger.info("list.size()======="+list.size());
			logger.info("finalList.size()======="+finalList.size());
			//List<EventTrigger> list = getHibernateTemplate().find();
			return finalList;
		}*/
		
		
		/*public int findAllCountByUserId(Long userId,boolean check) {
			//logger.info("user id.."+ userId);
			String column="";
            if(check==false)
            	column="lastSentDate";
            else
            	column="triggerModifiedDate";
			String qry = "SELECT COUNT(id) FROM EventTrigger  WHERE users="+userId+ " ORDER BY "+column+" DESC ";
			
			List<Long> list = executeQuery(qry);
			if(list.size() > 0) 
				
				return ((Long)list.get(0)).intValue();
			else return 0;
		}*/	
		
		
		/*public List<String> findConfiguredTriggers(String campaignIds) {

			//logger.debug(" Email String :" + campaignIds);
			String queryStr = "SELECT distinct campaign_id FROM event_trigger where campaign_id in (" +campaignIds+ ")";
			//logger.debug("JdbcTemplate : " + jdbcTemplate);
			List<String> list = jdbcTemplate.query(queryStr, new RowMapper(){

				@Override
				public Object mapRow(ResultSet arg0, int arg1)
						throws SQLException {
					logger.debug("arg0 :" + arg0 + " arg1 :" + arg1);
					return arg0!=null?arg0.getString("campaign_id"):"";
				} });
			
			return list;
		
		}*/
		
		
		
		/*public List<String> findConfiguredSMSTriggers(String smsCampaignIds) {

			//logger.debug(" Email String :" + smsCampaignIds);
			String queryStr = "SELECT distinct sms_id FROM event_trigger where sms_id in (" +smsCampaignIds+ ")";
			//logger.debug("JdbcTemplate : " + jdbcTemplate);
			List<String> list = jdbcTemplate.query(queryStr, new RowMapper(){

				@Override
				public Object mapRow(ResultSet arg0, int arg1)
						throws SQLException {
					logger.debug("arg0 :" + arg0 + " arg1 :" + arg1);
					return arg0!=null?arg0.getString("sms_id"):"";
				} });
			
			return list;
		
			
		}*/
		
		/*public boolean findByName(String eventTriggerName, Long userId) {
			
			try {
				String hql = "FROM EventTrigger WHERE users="+userId.longValue()+" AND triggerName='"+StringEscapeUtils.escapeSql(eventTriggerName)+"'";
				
				List<EventTrigger> retList = executeQuery(hql);
				
				if(retList != null && retList.size() > 0) {
					
					return true;
				}
				else return false;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Exception while fetching the ET with the name",e);
				return false;
			}
			
			
			
		}*///findByName
		
		/*public List<EventTrigger> findAllUserAutoRespondTriggers(Long userId, String trTypeStr) {
			
			try {
				
				
				//TODO can pass type to this query and condition query will be prepared accordingly
				String hql = "FROM EventTrigger WHERE users="+userId.longValue()+" AND trType in("+trTypeStr+") AND  bitwise_and(optionsFlag,"
						+Constants.ET_TRIGGER_IS_ACTIVE_FLAG+")="+Constants.ET_TRIGGER_IS_ACTIVE_FLAG ;
			
				
				List<EventTrigger> retList = getHibernateTemplate().find(hql);
				//logger.info("qry ::"+hql +" "+retList.size());
				if(retList != null && retList.size() > 0) {
					
					return retList;
				}
				else return null;
			} catch (DataAccessException e) {
				// TODO Auto-generated catch block
				return null;
			}catch (Exception e) {
				// TODO: handle exception
				logger.error("Exception ::" , e);
				return null;
			}
			
			
		}*/
			
		/*public List<EventTrigger> findAllETByUserAndType(Long userId,int trType){
			String hql = "FROM EventTrigger WHERE users="+userId.longValue()+" AND  bitwise_and(trType,"+trType+") > 0"
					+ " AND  bitwise_and(optionsFlag,"
					+Constants.ET_TRIGGER_IS_ACTIVE_FLAG+")="+Constants.ET_TRIGGER_IS_ACTIVE_FLAG ;
			
			logger.debug(" Trnsaction qry is  :"+hql);
			List<EventTrigger> retList = getHibernateTemplate().find(hql);
			logger.debug(" retList   :"+retList.size());
			return retList;
		}*/
}
