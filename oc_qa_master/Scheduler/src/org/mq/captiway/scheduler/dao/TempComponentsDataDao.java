package org.mq.captiway.scheduler.dao;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.AutoProgramComponents;
import org.mq.captiway.scheduler.utility.Constants;
import org.springframework.jdbc.core.JdbcTemplate;

public class TempComponentsDataDao extends AbstractSpringDao {
	
	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	
	private JdbcTemplate jdbcTemplate;
	
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	
	
	public TempComponentsDataDao() {
	}
	
	/*public void saveByCollection(Collection tempCompDataCollection) { 
    	
		if(logger.isInfoEnabled()) logger.info("entered savebycollection contactsDao ");
    	try{

    		super.saveOrUpdateAll(tempCompDataCollection);
    		
    	}
    	catch(Exception e) {

    		if(logger.isInfoEnabled()) logger.info(" exception ");
    		logger.error("Exception ::::" , e);
    		if(logger.isErrorEnabled()) logger.error("**Exception while saveOrUpdate ",e);
    	}
    } // saveByCollection
	*/
	public String getLabelOfPreviousStage(Long programId, int stage) {

	String qry = "SELECT label FROM TempComponentsData WHERE programId="+programId+" AND stage="+stage;
	
	return (String)getHibernateTemplate().find(qry).get(0);
		
	}
	
	
	public String getContactIdsStr(String subQuery) {
		
		String qry = subQuery;
		String contactIds = "";
		List<Long> contactIdList = jdbcTemplate.queryForList(subQuery, Long.class);
		if(contactIdList.isEmpty()) {
			
			if(logger.isDebugEnabled()) logger.debug("no contacts found for this query"+subQuery);
			return "";
		}
		
		for(Long id : contactIdList) {
			
			if(contactIds.length() > 0 ) contactIds += ",";
			contactIds += id;
		}
		return contactIds;
	}
	
	/**this method allows to get the contact ids from the different tables of program module 
	 * called from AutoProgram publisher to fetch contactIds from temo_components_data table
	 * @param query
	 * @return
	 */
	public List<Long> getContactIdList(String query) {
		
		List<Long> contactIdList = jdbcTemplate.queryForList(query, Long.class);
		
		if(contactIdList.size() > 0) {
			return contactIdList;
		}
		else {
			return null;
		}
		
	}
	/*
	 * idsList = jdbcTemplate.queryForList(queryStr,Long.class);

			if(idsList.isEmpty()) {

				if(logger.isDebugEnabled()) logger.debug("No Active Trigger FOund....Exiting");
				return eventTriggerList;
			}

			for(Iterator<Long> iterator=idsList.iterator();iterator.hasNext();){
				if(idsListStr.length() <= 0) {
					idsListStr += iterator.next();
				}
				else {
					idsListStr += ","+iterator.next();
				}

			} //idsListStr has all the active trigger ids list
	 */

	
	public long getMinPrimaryKey(AutoProgramComponents currComp) {
		
		return jdbcTemplate.queryForLong("SELECT MIN(temp_id) FROM temp_components_data WHERE program_id="
				+currComp.getAutoProgram().getProgramId()+" AND component_id="+currComp.getCompId());
		
		
		
		
	}//getMinPrimaryKey
	
	public long getCountToCalPercentage(AutoProgramComponents currComp) {
		
		
		String qry = "SELECT COUNT(contact_id) FROM temp_components_data WHERE component_id="+currComp.getCompId()+" AND component_win_id='"+
						currComp.getComponentWinId()+"'";
    	
    	
    	return jdbcTemplate.queryForLong(qry);
		
		
		
		
		
		
		
	}//getCountToCalPercentage
	
	
	
	
	
}
