package org.mq.captiway.scheduler.dao;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.HomesPassed;
import org.mq.captiway.scheduler.utility.Constants;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

public class HomesPassedDaoForDML extends AbstractSpringDaoForDML{
	
	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public HomesPassedDaoForDML() { }
	
	
	private JdbcTemplate jdbcTemplate;
	
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	public void saveOrUpdate(HomesPassed homesPassedObj) {
        super.saveOrUpdate(homesPassedObj);
    }
	
	  public void saveByCollection(Collection<HomesPassed> homesPassedCollection) {
//	    	super.saveOrUpdateAll(retailProSalesCollection);
	    	getHibernateTemplate().saveOrUpdateAll(homesPassedCollection);
	    }
	  
	 /* public HomesPassed findByAddressUnitID(Long listId,Long addressUnitId) {
		  
		  
		  try {
			//logger.debug(" addressUnitId >>>>>>>>>>> "+addressUnitId + " listId>>>>>>>>>>=" +listId);
			  
			  String qry = "FROM HomesPassed WHERE listId="+listId.longValue()+" " +
				" AND addressUnitId ="+addressUnitId.longValue();
			  
			  List<HomesPassed>  homesPassedtList  = getHibernateTemplate().find(qry);
			  //logger.debug(" QRY >>>>>>>>>."+qry+ " homesPassedtList >>>>>>>>>>>.. "+homesPassedtList );
			  
			  
			if(homesPassedtList != null && homesPassedtList.size() >0) {
				return homesPassedtList.get(0);
			}else return null;
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::::" , e);
			return null;
		}
		  
	  }
	  
	  public HomesPassed findHomesPassedByPriority(TreeMap<String, List<String>> prioMap , String[] lineStr,  Long userId) {
	 		
	 		try {
	 			String query = "FROM HomesPassed WHERE userId="+userId.longValue()+" ";
	 			
	 			if(logger.isDebugEnabled()) logger.debug("prioMap="+prioMap);
	 			Set<String> keySet = prioMap.keySet();
	 			List<HomesPassed> homesIdList = null;
	 			
	 			List<String> tempList=null;
	 			for (String eachKey : keySet) {
					
	 				if(logger.isDebugEnabled()) logger.debug("Key ="+eachKey);
	 				tempList = prioMap.get(eachKey);
	 				for (String eachStr : tempList) {
						int index = Integer.parseInt(eachStr.substring(0,eachStr.indexOf('|')));
						
						//query += " AND "+ eachStr.substring(eachStr.indexOf('|')+1) + " = '"+ lineStr[index] +"' ";
						
						String[] tempStr = eachStr.split("\\|");
						
						if(tempStr[2].toLowerCase().startsWith("string")) {
//							query += " AND "+ eachStr.substring(eachStr.indexOf('|')+1) + " = '"+ lineStr[index] +"' ";
							query += " AND "+ tempStr[1] + " = '"+ lineStr[index] +"' ";
						}else {
							query += " AND "+ tempStr[1] + " = "+ lineStr[index] +" ";
						}
						
						String valueStr = lineStr[index].trim();
						
						String posMappingDateFormatStr = tempStr[2].trim();
						
						if(posMappingDateFormatStr.toLowerCase().startsWith("date") && !tempStr[1].trim().toLowerCase().startsWith("udf")) {
							try {
								posMappingDateFormatStr =  posMappingDateFormatStr.substring(posMappingDateFormatStr.indexOf("(")+1, posMappingDateFormatStr.indexOf(")"));
								valueStr = format.format((new SimpleDateFormat(posMappingDateFormatStr).parse(valueStr)));
								query += " AND "+ tempStr[1] + " = '"+ valueStr +"' ";
							} catch (ParseException e) {
								logger.error("Exception ::::" , e);
								return null;
							}
						}
						else if(posMappingDateFormatStr.toLowerCase().startsWith("string") || posMappingDateFormatStr.toLowerCase().startsWith("udf")) {
//							query += " AND "+ eachStr.substring(eachStr.indexOf('|')+1) + " = '"+ lineStr[index] +"' ";
							query += " AND "+ tempStr[1] + " = '"+ valueStr +"' ";
						}else {
							query += " AND "+ tempStr[1] + " = "+ lineStr[index] +" ";
						}
						
					} // for 
	 				
	 				if(logger.isDebugEnabled()) logger.debug("QUERY=="+query);
	 				
	 				homesIdList  = getHibernateTemplate().find(query);
	 				
	 				if(homesIdList.size() == 1) {
	 					return homesIdList.get(0);
	 				}
	 				else if(homesIdList.size() > 1) {
	 					if(logger.isDebugEnabled()) logger.debug("more than 1 object found :"+homesIdList.size());
	 					return homesIdList.get(0);
	 				}
	 				
	 				
				} // outer for
	 			
	 			return null;
	 			
			} catch (DataAccessException e) {
				// TODO Auto-generated catch block
				logger.error("Exception ::::" , e);
				return null;
			}
	 		
	 	}
	 	*/
	  
	  

}

