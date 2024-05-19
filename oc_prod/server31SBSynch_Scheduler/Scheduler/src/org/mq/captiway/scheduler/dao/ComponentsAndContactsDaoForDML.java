package org.mq.captiway.scheduler.dao;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.ComponentsAndContacts;
import org.mq.captiway.scheduler.utility.Constants;

public class ComponentsAndContactsDaoForDML extends AbstractSpringDaoForDML{
	
	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	
	
	
	
	public void saveByCollection(Collection cmponentsAndContactsCollection) { //added for EventTrigger
    	
		if(logger.isInfoEnabled()) logger.info("entered savebycollection componentsAndContactsDao ");
    	try{

    		super.saveOrUpdateAll(cmponentsAndContactsCollection);
    		
    	}
    	catch(Exception e) {

    		logger.info(" exception ");
    		logger.error("Exception ::::" , e);
    		logger.error("**Exception while saveOrUpdate ",e);
    	}
    } // saveByCollection
 	
	
	
/*	
	public long findCountByComId(String componentWinId, Long compId, Long progId) {
		
		
		return ((Long)getHibernateTemplate().find("select count(contactId) from ComponentsAndContacts " +
			"where componentId="+compId+" and programId="+progId+" and componentWinId='"+componentWinId+"'").get(0)).longValue();
		
	}
	
	
	public List<ComponentsAndContacts> getexistingContactsData(Long progId, Long compId, int stage) {
		
		List<ComponentsAndContacts> componentAndContactList = null;
		String qryStr = "FROM ComponentsAndContacts where programId="+progId+" and componentId="+compId+" and stage="+stage;
		
		componentAndContactList = getHibernateTemplate().find(qryStr);
		
		return componentAndContactList;
		
		
	}
	
	public List<ComponentsAndContacts> getListByQry(String qryStr) {
		
		
		List<ComponentsAndContacts> componentAndContactList = null;
		
		componentAndContactList = getHibernateTemplate().find(qryStr);
		
		return componentAndContactList;
		
		
	}
	
	public List<Object[]> getContactIds( Long progId) {
		
		List<Object[]> cidList = null;
		String qry = "select contactId from ComponentsAndContacts where  ProgramId = "+progId;
		
		cidList = getHibernateTemplate().find(qry);
		return cidList;
	}
	
	
	public int getCountForProgram(Long programId) {

		String qry = "SELECT count(ccId) FROM ComponentsAndContacts WHERE programId="+programId;
		
		List<Long> countList = getHibernateTemplate().find(qry);
		if(countList.size()>0) {
			
	       	return countList.get(0).intValue();
		}
		else {
	       	return 0;
		}
		   
		
	}//getCountForProgram
*/	
	DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public int updateActivityDateAndPath(Calendar cal, Long programId, String tempPath, Long compId, String cids) {
		
		Date activityDate = cal.getTime();
		String formatDate = format.format(activityDate);
		
		String qry = "UPDATE components_contacts SET activity_date='"+formatDate+"" +
						"',path=concat(path,',"+tempPath+"'),component_id="+compId+",component_win_id='"+tempPath+"'" +
						" WHERE  program_id="+programId+" AND contact_id in("+cids+")";
		
		int updateCount = executeJdbcUpdateQuery(qry);
		
		if(logger.isDebugEnabled()) logger.debug("the number of records updated with the activity date "+ formatDate+" " +
					"and with the path"+tempPath+" are..."+updateCount);
		
		/*qry = "UPDATE components_contacts SET path=contact(path,',"+tempPath+"') WHERE program_id="+programId;
		updateCount = executeJdbcUpdateQuery(qry);
		
		if(logger.isDebugEnabled()) logger.debug("the number of records updated with the path "+tempPath+" are ...."+updateCount);*/
		
		return updateCount;
		
	}
	 
	/**
	 * this will return the list of componentsAndContacts objects which are present in tempContacts.
	 * called from the pmtamailmergesubmitter while processing the emailsending activity component.
	 * @param cidStr
	 * @param programId
	 * @return
	 */
	
	
	/*public List<ComponentsAndContacts> getByContactIds(String cidStr, Long programId) {
		
		List<ComponentsAndContacts> compList = null;
		String query = " From ComponentsAndContacts WHERE contactId in("+cidStr+") AND programId="+programId;
		
		compList = getHibernateTemplate().find(query);
		
		return compList;
		
		
		
		
		
	}*/
	
	
	
}
