package org.mq.marketer.campaign.dao;


import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.mq.marketer.campaign.beans.Contacts;
import org.mq.marketer.campaign.beans.ContactsLoyalty;
import org.mq.marketer.campaign.beans.LoyaltyProgramTrans;
import org.mq.marketer.campaign.beans.SparkBaseCard;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.contacts.ContactListUploader;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.general.Constants;
import org.mq.optculture.data.dao.JdbcResultsetHandler;
import org.mq.optculture.model.ocloyalty.MatchedCustomerReport;
import org.mq.optculture.utils.OCConstants;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;


public class ContactsLoyaltyDao extends AbstractSpringDao implements Serializable{

    public ContactsLoyaltyDao() {}

	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

    public ContactsLoyalty find(Long id) {
        return (ContactsLoyalty) super.find(ContactsLoyalty.class, id);
    }
    
    
    
    public ContactsLoyalty findAllByLoyaltyId(long loyaltyId) {
    	try {
    		List<ContactsLoyalty> list = null;
			list = executeQuery("from ContactsLoyalty where loyaltyId = " + loyaltyId);
			
			if(list.size() >0) return list.get(0);
			else return null;
			
		} catch (DataAccessException e) {
			logger.error("Exception ::" , e);
			return null;
		}
    }
    
    public List<ContactsLoyalty> findAllByLoyaltyIdStr(String loyaltyId) {
    	try {
    		List<ContactsLoyalty> list = null;
			list = executeQuery("from ContactsLoyalty where loyaltyId IN(" + loyaltyId+" )");
			
			if(list.size() >0) return list;
			else return null;
			
		} catch (DataAccessException e) {
			logger.error("Exception ::" , e);
			return null;
		}
    }
    
    public ContactsLoyalty findByContactId(Long userID, long contactId) {
    	try {
    		List<ContactsLoyalty> list = null;
			list = executeQuery("from ContactsLoyalty where userId="+userID+" AND contact = " + contactId);
			//logger.info("list1==>"+list);
			if(list != null && list.size() >0) return list.get(0);
			else return null;
			
		} catch (DataAccessException e) {
			logger.error("Exception ::" , e);
			return null;
		}catch (Exception e) {
			logger.error("Exception ::" , e);
			return null;
		}
    }
    
    public List<ContactsLoyalty> findLoyaltyListByContactId(Long userID, long contactId) {
    	List<ContactsLoyalty> list = null;
    	try {
			list = getHibernateTemplate().find("from ContactsLoyalty where userId="+userID+" AND contact = " + contactId+" ORDER BY 1 DESC");
		} catch (DataAccessException e) {
			logger.error("Exception ::" , e);
			return null;
		}
    	return list;
    }
    
    public List<ContactsLoyalty> findOCLoyaltyListByContactId(Long userID, long contactId) {
    	List<ContactsLoyalty> list = null;
    	try {
			list = getHibernateTemplate().find("FROM ContactsLoyalty WHERE userId="+userID+" AND contact = " + contactId + " AND serviceType = '"+OCConstants.LOYALTY_SERVICE_TYPE_OC+"'");
		} catch (DataAccessException e) {
			logger.error("Exception ::" , e);
			return null;
		}
    	return list;
    }
    
    public List<ContactsLoyalty> findLoyaltyListByCidStr(String contactIdStr, Long userId) {
    	List<ContactsLoyalty> list = null;
    	try {
			list = getHibernateTemplate().find("FROM ContactsLoyalty WHERE  userId = "+userId+" AND contact IN (" + contactIdStr + ")");
		} catch (DataAccessException e) {
			logger.error("Exception ::" , e);
			return null;
		}
    	return list;
    }
    
 
  public Long findLoyaltyCountByDates(Long userID, String startDate, String endDate, String sparkBaseLoc, String contactLoyaltyType) {
	 String subQry = "";
	  if(!contactLoyaltyType.toLowerCase().equals("all")) {
		  
		  subQry = " AND contactLoyaltyType='"+contactLoyaltyType+"'";
		  
		  
	  }
	  
	  
	  
	  String qry = "SELECT COUNT(loyaltyId) FROM ContactsLoyalty WHERE userId = "+userID+" AND createdDate BETWEEN '"
		  				+startDate+"' AND '"+endDate+"' AND  locationId='"+sparkBaseLoc+"'"+subQry;
	  
	  logger.info("qry ::"+qry);
	  
	 long count = ((Long)getHibernateTemplate().find(qry).get(0)).longValue();
	 return count;
	  
	  
  }
  
  public Long findLoyaltyCountBy(String startDate, String endDate, String sparkBaseLoc, String sourceType, String mode, long userID) {
		 String subQry = "";
		 if(sparkBaseLoc != null){
			 subQry += " AND  locationId='"+sparkBaseLoc+"'";
			 
		 }
		 if(sourceType.toLowerCase().equals("webform")){
			 sourceType = "all";
		 }
		  if(!sourceType.toLowerCase().equals("all")) {
			  if(sourceType.toLowerCase().equals("webform")){
				  sourceType = "WebForm";
				  
				}else if(sourceType.toLowerCase().equals("e-commerce")){
					sourceType = "eComm";
				}
			  subQry += " AND sourceType='"+sourceType+"'";
			  
			  
		  }
		  
		  //String modeQrySubString = "";
		  if(!"ALL".equalsIgnoreCase(mode)){
				subQry += " AND mode = '" + mode + "'";
		  }

		  String qry = "SELECT COUNT(loyaltyId) FROM ContactsLoyalty WHERE userId="+userID+" AND createdDate BETWEEN '"
			  				+startDate+"' AND '"+endDate+"'"  +subQry;
		  
		  logger.info("qry ::"+qry);
		  
		 long count = ((Long)getHibernateTemplate().find(qry).get(0)).longValue();
		 return count;
		  
		  
	  }
  
  public Long findLoyaltyCountByDatesAndMode(String startDate, String endDate, String sparkBaseLoc, 
		  String contactLoyaltyType, String mode, long userID) {
		 String subQry = "";
		 if(sparkBaseLoc != null) {
			 subQry += " AND  locationId='"+sparkBaseLoc+"'";
			 
		 }
		  if(!contactLoyaltyType.toLowerCase().equals("all")) {
			  
			  subQry += " AND contactLoyaltyType='"+contactLoyaltyType+"'";
			  
			  
		  }
		  
		  //String modeQrySubString = "";
		  if(!"ALL".equalsIgnoreCase(mode)){
				subQry += " AND mode = '" + mode + "'";
		  }

		  String qry = "SELECT COUNT(loyaltyId) FROM ContactsLoyalty WHERE userId="+userID+" AND createdDate BETWEEN '"
			  				+startDate+"' AND '"+endDate+"'"  +subQry;
		  
		  logger.info("qry ::"+qry);
		  
		 long count = ((Long)getHibernateTemplate().find(qry).get(0)).longValue();
		 return count;
		  
		  
	  }
  
  public List<ContactsLoyalty> findLoyaltyContactsBy(String qry, int strtIndex, int size) {
	  
	 
		List<ContactsLoyalty> loyaltyContactList = executeQuery(qry, strtIndex, size);
		
		return loyaltyContactList;
	  
	  
	  
  }
  public List<ContactsLoyalty> findLoyaltyContactsByDates(String qry, int strtIndex, int size) {
	  
		 
		List<ContactsLoyalty> loyaltyContactList = executeQuery(qry, strtIndex, size);
		
		return loyaltyContactList;
	  
	  
	  
}



	public Long findTotalSBLoyaltyOptins(long userId, String serviceType, String startDate, String endDate) {
		  
		  String qury = "SELECT COUNT(loyaltyId) FROM  ContactsLoyalty " +
		  				" WHERE userId="+userId+" AND serviceType='"+serviceType+"' AND createdDate BETWEEN '"+ startDate +"' AND '"+endDate+"' ";
	//	  logger.info("query for loyalty Opt-in is..."+qury);
		  List tempList = getHibernateTemplate().find(qury);
		  
		  if(tempList != null && tempList.size() >0) {
			
			  return ((Long) tempList.get(0)).longValue();
		  }else return null;
		  
//		 return((Long) (getHibernateTemplate().find(qury).get(0))).longValue();
	 }
	
	public Long findTotalOCLoyaltyOptins(long userId, String serviceType, String startDate, String endDate) {
		  
		  String qury = "SELECT COUNT(loyaltyId) FROM  ContactsLoyalty " +
		  				" WHERE userId="+userId+" AND serviceType='"+serviceType+"' AND (rewardFlag='L' OR rewardFlag='GL') AND createdDate BETWEEN '"+ startDate +"' AND '"+endDate+"' ";
	//	  logger.info("query for loyalty Opt-in is..."+qury);
		  List tempList = getHibernateTemplate().find(qury);
		  
		  if(tempList != null && tempList.size() >0) {
			
			  return ((Long) tempList.get(0)).longValue();
		  }else return null;
		  
//		 return((Long) (getHibernateTemplate().find(qury).get(0))).longValue();
	 }
	
	
	public String findTopLocation(String selectType,String serviceType,long userId, String startDate, String endDate) {
		String qury = "";
		String colToken = "pos_location_id";
		
			
		if(selectType.equals("LOCATION_ID")) {
			
			colToken = "pos_location_id";
		qury = "SELECT count(loyalty_id) as tot  ,pos_location_id FROM contacts_loyalty"+
				" WHERE user_id="+userId+" " +
				" AND service_type='"+serviceType+"' AND (reward_flag='L' OR reward_flag='GL') AND created_date BETWEEN '"+ startDate +"' AND '"+endDate+"' " +
				" AND pos_location_id IS NOT NULL GROUP BY pos_location_id order by tot desc limit 1";
		
		}
		else if(selectType.equals("EMP_ID")){
			colToken = "emp_id";
			qury = "SELECT count(loyalty_id) as tot  ,emp_id FROM contacts_loyalty"+
				   " WHERE user_id="+userId+" " +
				   " AND emp_id is not null"+
				   " AND created_date BETWEEN '"+ startDate +"' AND '"+endDate+"' " +
				   " AND emp_id IS NOT NULL GROUP BY emp_id order by tot desc limit 1";

		}
		
		logger.info("findTopLocation of colToken type is.."+colToken+ ":: query is..."+qury);
		
		List<Map<String, Object>>  retMap = jdbcTemplate.queryForList(qury);
		
		if(retMap != null && retMap.size() > 0) {
			Map<String, Object> innerMap = retMap.get(0);
			return (String)innerMap.get(colToken);			
		}
		return null;
	}
	
	
	/*public Long findMaxValueByType(String selectType,long userId, String startDate, String endDate) {
		String qury = "";
		if(selectType.equals("LOCATION_ID")) {
			
			qury = "SELECT MAX(locationId) " +
					" FROM  ContactsLoyalty " +
					" WHERE userId="+userId+" " +
					" AND createdDate BETWEEN '"+ startDate +"' AND '"+endDate+"' " +
					" GROUP BY locationId";
		}else if(selectType.equals("EMP_ID")){
			qury = "SELECT COUNT(empId)  " +
					" FROM  ContactsLoyalty " +
					" WHERE userId="+userId+" " +
					" AND createdDate BETWEEN '"+ startDate +"' AND '"+endDate+"' " +
					" GROUP BY empId";
		}
		
		logger.info("&&&&&&&&&& qry is.."+qury);
		List tempList = getHibernateTemplate().find(qury);
		if(tempList != null && tempList.size() > 0) {
			
			return ((Long) tempList.get(0)).longValue();
		}else return null;
	}*/
	
	
	public ContactsLoyalty findContLoyaltyByCardId(Long userID, String cardId) {
		  
		  
		  String qury = "from ContactsLoyalty WHERE	userId="+userID+" AND cardNumber='"+cardId+"'";
		  logger.info(qury);
		  List<ContactsLoyalty> tempList = getHibernateTemplate().find(qury);
		  
		  if(tempList != null && tempList.size() >0) {
			
			 return tempList.get(0);
		  } else 
			  return null;
		  
	 }
	public ContactsLoyalty findContLoyaltyBymobile(Users user, String phone) {
		  
		String countryCarrier =user.getCountryCarrier()!=null?user.getCountryCarrier()+"":"" ;//APP-2208
		
		int i=countryCarrier.length();
		
		logger.info("phone "+phone);

		if(phone.startsWith(countryCarrier)) {
			 
			logger.info("entering phone length"+i);

			logger.info("entering phone Z+");
			phone=phone.substring(i);
	
		}
		  String qury = "from ContactsLoyalty WHERE	userId="+user.getUserId()+" AND cardNumber = '"+phone+"'";
		  logger.info(qury);
		  List<ContactsLoyalty> tempList = getHibernateTemplate().find(qury);
		  
		  if(tempList != null && tempList.size() >0) {
			
			 return tempList.get(0);
		  } else 
			  return null;
		  
	 }
	
	public ContactsLoyalty findContLoyaltyByemaild(Users user, String emailid) {
		  
		
		  String qury = "from ContactsLoyalty WHERE	userId="+user.getUserId()+" AND emailId like '"+emailid+"'";
		  logger.info(qury);
		  List<ContactsLoyalty> tempList = getHibernateTemplate().find(qury);
		  
		  if(tempList != null && tempList.size() >0) {
			
			 return tempList.get(0);
		  } else 
			  return null;
		  
	 }
	
	/*
	 * This Method fetches the total number of Loyalty opt-ins based on USERID, OPTINMEDIUM, and given FROM and TO dates
	 * 
	 */
	
	/*public ContactsLoyalty findContLoyaltyByCardIdAndPinAndLoc(Long cardId, String pin,String locId) {
		  
		  
		  String qury = "from ContactsLoyalty WHERE	cardNumber="+cardId+" AND cardPin='"+pin+"' " +
		  		" AND locationId='"+locId+"' ";

		  List<ContactsLoyalty> tempList = getHibernateTemplate().find(qury);
		  
		  if(tempList != null && tempList.size() >0) {
			
			 return tempList.get(0);
		  } else 
			  return null;
		  
	 } // findContLoyaltyByCardIdAndPinAndLoc
	*/
	
	public ContactsLoyalty findContLoyaltyByCardIdAndPin(long userID, Long cardId, String pin, boolean isOCCard) {
		  
		String appQry = "";
		if(isOCCard){
			appQry = " AND membershipStatus = '"+OCConstants.LOYALTY_MEMBERSHIP_STATUS_ACTIVE+"' AND (rewardFlag ='"+OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L+"' OR rewardFlag='"+OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_GL+"') ";
		}
		  
		  String qury = "from ContactsLoyalty WHERE	userId="+userID+" AND cardNumber='"+cardId+"'"+(pin != null && !pin.isEmpty() ? (" AND cardPin = '"+pin+"'") : "");

		  List<ContactsLoyalty> tempList = getHibernateTemplate().find(qury);
		  
		  if(tempList != null && tempList.size() >0) {
			
			 return tempList.get(0);
		  } else 
			  return null;
		  
	 } // findContLoyaltyByCardIdAndPin


  
	/*
	 * This Method fetches the total number of Loyalty opt-ins based on USERID, OPTINMEDIUM, and given FROM and TO dates
	 * 
	 */
	
	public Long findTotalLoyaltyOptinsByUserId(long userId, String contactLoyaltyType, String startDate, String endDate) {
		  
		String query = "";
		String dateSubQry = "";
		if(startDate != null && endDate != null) {
			
			dateSubQry = " AND createdDate BETWEEN '"+ startDate +"' AND '"+endDate+"'";
		}
		
		if(contactLoyaltyType.equals("All")){
			logger.info(">>>>>>>>>>>IN find total loyalty.... All.........");
			
			query = "SELECT COUNT(loyaltyId) FROM  ContactsLoyalty cl,Contacts c " +
	  				" WHERE  c.userId="+userId+" AND cl.userId="+userId+dateSubQry;
		}
		else {
			logger.info(">>>>>>>>>>>IN find total loyalty.... ........."+contactLoyaltyType);
			query = "SELECT COUNT(loyaltyId) FROM  ContactsLoyalty " +
	  				" WHERE userId="+userId+dateSubQry+"   AND contactLoyaltyType='"+contactLoyaltyType+"'";
		}
		  
		List tempList = getHibernateTemplate().find(query);
		  
		if(tempList != null && tempList.size() >0) {
		 return (Long)tempList.get(0);
		}
		else return null;
	 }//findTotalLoyaltyOptinsByUserId
	
	
	public Long findTotalLoyaltyOptinsBySearch(long userId, String optInMedium, String startDate,
			String endDate, String searchByKey, String searchByValue, String serviceType) {
	
	try {
		String optinSrcQry ="";
		String searchByQry ="";
		String dateSubQry = "";
		if(startDate != null && endDate != null) {
			
			dateSubQry = " AND cl.created_date BETWEEN '"+ startDate +"' AND '"+endDate+"'";
		}
		
		if(optInMedium!=null) {
			optinSrcQry = " AND cl.contact_loyalty_type = '"+optInMedium+"' ";
		}
		
		if(searchByKey!=null && searchByValue!=null) {
			if(searchByKey.equalsIgnoreCase("card")) {
				searchByQry = " AND cl.card_number='"+searchByValue+"' ";
			}
			else if(searchByKey.equalsIgnoreCase("first_name") ||
					searchByKey.equalsIgnoreCase("last_name") ||
					searchByKey.equalsIgnoreCase("email_id")) {
				//searchByQry = " AND c."+searchByKey+" LIKE '%"+searchByValue+"%' ";
				searchByQry = " AND c."+searchByKey+" LIKE '%"+StringEscapeUtils.escapeSql(searchByValue)+"%' ";
			}
			else if(searchByKey.equalsIgnoreCase("mobile_phone")) {
				searchByQry = " AND c."+searchByKey+"="+searchByValue+" ";
			} 
		}
		
		String query = null;

		query = "SELECT count(cl.loyalty_id) "+
				" FROM contacts_loyalty cl left join contacts c on cl.contact_id = c.cid "+
				" where cl.user_id = "+ userId+" AND (cl.service_type = '"+serviceType+"'  or cl.service_type IS NULL)"+dateSubQry+
				optinSrcQry + searchByQry ;
		
		logger.info("SB Query ::"+query);
		
		Long tempCount = jdbcTemplate.queryForLong(query);
		  
		if(tempCount != null) {
			return tempCount;			
		}
		return null;
	} catch (Exception e) {
		logger.info("Error while fetching loyalty cards report options count...."+e);
		return null;
	}
	
	}
	
	public Long findTotalLoyaltyCards(long userId, boolean isRegistered,String serviceType){
		
		try {
			String searchQry = "";
			if(isRegistered) {
				
				searchQry += " AND isRegistered=1 ";
			}
			
			String query = "SELECT COUNT(loyaltyId) FROM  ContactsLoyalty WHERE userId = "+userId+ " AND(serviceType = '"+serviceType+"'  OR serviceType IS NULL) "+searchQry;
				
			//logger.info("query ::"+query);
			
			List<Long> tempList = getHibernateTemplate().find(query);
			  
			if(tempList != null && tempList.size() >0) {
			 return tempList.get(0);
			}
			else return null;
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
			return null;
		}
	
	}
	
	
	
	public List<Object[]> findContactLoyaltyOptinsBySearch(long userId, int startIndex, int pageSize, 
			String optInStr, String fromDate,String toDate, String searchByKey, String searchByValue,String serviceType,String orderby_colName,String desc_Asc) {
			
		String optinSrcQry ="";
		String searchByQry ="";
		String dateSubQry = "";
		if(fromDate != null && toDate != null ) {
			
			dateSubQry = " AND cl.created_date >= '"+fromDate+"' AND cl.created_date <= '"+toDate+ "'";
		}
		
		if(optInStr!=null) {
			optinSrcQry = " AND cl.contact_loyalty_type = '"+optInStr+"' ";
		}
		
		if(searchByKey!=null && searchByValue!=null) {
			if(searchByKey.equalsIgnoreCase("card")) {
				searchByQry = " AND cl.card_number='"+searchByValue+"' ";
			}
			else if(searchByKey.equalsIgnoreCase("first_name") ||
					searchByKey.equalsIgnoreCase("last_name") ||
					searchByKey.equalsIgnoreCase("email_id")) {
				searchByQry = " AND c."+searchByKey+" LIKE '%"+StringEscapeUtils.escapeSql(searchByValue)+"%' ";
			}
			else if(searchByKey.equalsIgnoreCase("mobile_phone")) {
				searchByQry = " AND c."+searchByKey+"='"+searchByValue+"' ";
			} 
		}
		
		String query = null;

		query = "SELECT cl.card_number, c.first_name, c.last_name, c.email_id, c.mobile_phone, "+ 
				" c.address_one, c.address_two, c.city, c.state, c.country, c.zip, "+
				" cl.created_date, cl.contact_loyalty_type, cl.card_pin, cl.card_type, cl.contact_id, cl.pos_location_id "+
				" FROM contacts_loyalty cl left join contacts c on cl.contact_id = c.cid  "+
				" where cl.user_id = "+ userId+" AND (cl.service_type = '"+serviceType+"'  or cl.service_type IS NULL) "+ dateSubQry +
				optinSrcQry + searchByQry +
				" order by "+orderby_colName+" "+desc_Asc;
		
		
		List<Object[]> loyaltyOptins = null;
		
	
		try{		
			loyaltyOptins= jdbcTemplate.query(query+" LIMIT "+startIndex+", "+pageSize, new RowMapper() {
	
		        public Object[] mapRow(ResultSet rs, int rowNum) throws SQLException {
		        
		        	Object[] object = new Object[12];
		        	
		        	object[0] = rs.getString(1); //card number
		        	object[1] = rs.getString(2); //first name
		        	object[2] = rs.getString(3); //last name
		        	object[3] = rs.getString(4); //email id 
		        	object[4] = rs.getString(5); //phone number
		        	String addrStr = "";
		        	addrStr += (rs.getString(6)==null||rs.getString(6).toString().trim().isEmpty()?"": addrStr.trim().length() > 0 ? ","+rs.getString(6).toString().trim() :rs.getString(6).toString().trim());
		        	addrStr += (rs.getString(7)==null||rs.getString(7).toString().trim().isEmpty()?"": addrStr.trim().length() > 0 ? ","+rs.getString(7).toString().trim() :rs.getString(7).toString().trim());
		        	addrStr +=	(rs.getString(8)==null||rs.getString(8).toString().trim().isEmpty()?"": addrStr.trim().length() > 0 ? ","+rs.getString(8).toString().trim() :rs.getString(8).toString().trim());
		        	addrStr += (rs.getString(9)==null||rs.getString(9).toString().trim().isEmpty()?"": addrStr.trim().length() > 0 ? ","+rs.getString(9).toString().trim() :rs.getString(9).toString().trim());
		        	addrStr += (rs.getString(10)==null||rs.getString(10).toString().trim().isEmpty()?"": addrStr.trim().length() > 0 ? ","+rs.getString(10).toString().trim() :rs.getString(10).toString().trim());
		        	addrStr += (rs.getString(11)==null||rs.getString(11).toString().trim().isEmpty()?"": addrStr.trim().length() > 0 ? ","+rs.getString(11).toString().trim() :rs.getString(11).toString().trim());
		        	
		       /* 	while(addrStr.startsWith("::")){
		        		addrStr = addrStr.replaceFirst("[::]+", "");
		        	}*/
		        	
		        	//object[5] = addrStr.replaceAll("[::]+", ",");
		        	object[5] = addrStr; //address
		        	object[6] = rs.getString(12); //opt in date
		        	object[7] = rs.getString(13); //opt in source
		        	object[8] = rs.getString(14); //card pin
		        	object[9] = rs.getString(15); //card type
		        	object[10] = rs.getString(16); //contact id
		        	object[11] = rs.getString(17); //pos_location_id
		        	return object;
		        }
		        
			});
			
			return loyaltyOptins;
		} catch (Exception e) {
			logger.error("Exception in finding contact loyalty optins");
			logger.error("Exception ::" , e);
			return null;
		}
		
	}// findContactLoyaltyOptins
	
	public List<Object[]> findContactLoyaltyOptinsByuserId(long userId,  boolean isRegistered, int startIndex, int pageSize,String serviceType) {
		logger.debug(">>>>>>>>>>>>> entered in findContactLoyaltyOptinsByuserId");
		String query = null;
		String searchByQry ="";
		String selectionQry = "";
		
		if(isRegistered) {
			
			searchByQry += " AND cl.is_registered=1 ";
			selectionQry = " , cl.is_registered " ;
		}
		List<Object[]> loyaltyOptins = null;
		
			query = "SELECT cl.card_number, c.first_name, c.last_name, c.email_id, c.mobile_phone,"+
					" c.address_one, c.address_two, c.city, c.state, c.country, c.zip,"+
					" cl.created_date, cl.contact_loyalty_type, cl.card_pin"+selectionQry+
					" FROM contacts_loyalty cl left join contacts c on cl.contact_id = c.cid  "+
					" where cl.user_id = "+ userId +" AND (cl.service_type = '"+serviceType+"' or cl.service_type IS NULL) "+searchByQry+
					" order by cl.created_date desc";
			logger.info("SB Query ::"+query);
		
		try{	
			final int size = isRegistered ? 10 : 9;
			loyaltyOptins= jdbcTemplate.query(query+" LIMIT "+startIndex+", "+pageSize, new RowMapper() {
			        public Object[] mapRow(ResultSet rs, int rowNum) throws SQLException {
		        
		        	Object[] object = new Object[size];
		        	
		        	object[0] = rs.getString(1);
		        	object[1] = rs.getString(2);
		        	object[2] = rs.getString(3);
		        	object[3] = rs.getString(4);
		        	object[4] = rs.getString(5);
		        	String addrStr = "";
		        	addrStr += (rs.getString(6)==null||rs.getString(6).toString().trim().isEmpty()?"": addrStr.trim().length() > 0 ? ","+rs.getString(6).toString().trim() :rs.getString(6).toString().trim());
		        	addrStr += (rs.getString(7)==null||rs.getString(7).toString().trim().isEmpty()?"": addrStr.trim().length() > 0 ? ","+rs.getString(7).toString().trim() :rs.getString(7).toString().trim());
		        	addrStr +=	(rs.getString(8)==null||rs.getString(8).toString().trim().isEmpty()?"": addrStr.trim().length() > 0 ? ","+rs.getString(8).toString().trim() :rs.getString(8).toString().trim());
		        	addrStr += (rs.getString(9)==null||rs.getString(9).toString().trim().isEmpty()?"": addrStr.trim().length() > 0 ? ","+rs.getString(9).toString().trim() :rs.getString(9).toString().trim());
		        	addrStr += (rs.getString(10)==null||rs.getString(10).toString().trim().isEmpty()?"": addrStr.trim().length() > 0 ? ","+rs.getString(10).toString().trim() :rs.getString(10).toString().trim());
		        	addrStr += (rs.getString(11)==null||rs.getString(11).toString().trim().isEmpty()?"": addrStr.trim().length() > 0 ? ","+rs.getString(11).toString().trim() :rs.getString(11).toString().trim());
       	
		        /*	while(addrStr.startsWith("::")){
		        		addrStr = addrStr.replaceFirst("[::]+", "");
		        	}
       	
		        	object[5] = addrStr.replaceAll("[::]+", ",");*/
		        	
		        	object[5] = addrStr;
		        	object[6] = rs.getString(12);
		        	object[7] = rs.getString(13);
		        	if(object.length > 9) {
		        		object[9] = rs.getByte(15);
		        	}
		        	
		        	return object;
		        }
		        
			});
			logger.debug("<<<<<<<<<<<<< completed findContactLoyaltyOptinsByuserId ");
			return loyaltyOptins;
		} catch (Exception e) {
			logger.error("Exception in finding contact loyalty optins");
			logger.error("Exception ::" , e);
			logger.debug("<<<<<<<<<<<<< completed findContactLoyaltyOptinsByuserId ");
			return null;
		}
	}	
	
	public ContactsLoyalty getContactsLoyaltyByCustId(String customerId, Long userId){//APP-4728
		
		try {
			String query = " FROM ContactsLoyalty WHERE userId = "+userId+" AND customerId = '"+customerId+"' ";
    		List<ContactsLoyalty> list = null;
    		logger.info("getContactsLoyaltyByCustId "+query);
			list = getHibernateTemplate().find(query);
			
			if(list.size() >0) return list.get(0);
			else return null;
			
		} catch (DataAccessException e) {
			logger.error("Exception ::" , e);
			return null;
		}
		
	}
	
	
	
	public ContactsLoyalty getContactsLoyaltyByCustId(String cardNumber, String customerId, Long userId){
		
		try {
			
			String subQry = Constants.STRING_NILL;
			
			if(cardNumber != null && !cardNumber.isEmpty()){
				subQry = (" AND cardNumber='"+cardNumber+"'") ;
			}else if(customerId != null && !customerId.isEmpty()){
				subQry = (" AND customerId = '"+customerId+"'");
			}
			
			/*subQry += customerId != null && !customerId.isEmpty() ?(" AND customerId = '"+customerId+"'") : Constants.STRING_NILL;
			subQry += cardNumber != null && !cardNumber.isEmpty() ?(" AND cardNumber="+cardNumber) : Constants.STRING_NILL;*/
			if(subQry.isEmpty()) return null;
			String query = " FROM ContactsLoyalty WHERE userId = "+userId+subQry;
    		List<ContactsLoyalty> list = null;
			list = executeQuery(query);
			
			if(list.size() >0) return list.get(0);
			else return null;
			
		} catch (DataAccessException e) {
			logger.error("Exception ::" , e);
			return null;
		}
		
	}
	
	
	public List<ContactsLoyalty> getLoyaltyListByCustId(String customerId, Long userId){
		
		try {
			String query = " FROM ContactsLoyalty WHERE userId = "+userId+" AND customerId = '"+customerId+"'";
    		List<ContactsLoyalty> list = null;
			list = getHibernateTemplate().find(query);
			
			return list;
			
		} catch (DataAccessException e) {
			logger.error("Exception ::" , e);
			return null;
		}
		
	}
	
	public ContactsLoyalty getContactsLoyaltyByCardId(String cardId, Long userId) {
		  
		  
		  String query = " FROM ContactsLoyalty WHERE userId = "+userId+" AND cardNumber='"+cardId+"'";
		  List<ContactsLoyalty> tempList = getHibernateTemplate().find(query);
		  
		  if(tempList != null && tempList.size() >0) {
			
			 return tempList.get(0);
		  } else 
			  return null;
		  
	 }
	
	public ContactsLoyalty getContactsLoyaltyByMobile(Long mobile, Long userId) {
		  
		  
		  String query = " FROM ContactsLoyalty WHERE userId = "+userId+" AND serviceType ='SB' AND mobilePhone like '%"+mobile+"'";
		  List<ContactsLoyalty> tempList = getHibernateTemplate().find(query);
		  
		  if(tempList != null && tempList.size() ==1) {
			
			 return tempList.get(0);
		  } else 
			  return null;
		  
	 }
	
	
	//This Only for duplicate ContactLoyalty exist for same Card
	public List<ContactsLoyalty> getContactLoyatyByCardnumber(Long cardId){
		String query = " FROM ContactsLoyalty WHERE  cardNumber='"+cardId+"'";
		  List<ContactsLoyalty> tempList = getHibernateTemplate().find(query);
		  return tempList;
	}

	public List<ContactsLoyalty> fetchCurrentActiveTransForExpiry(Calendar cal) {
		List<ContactsLoyalty> contactsList = null;
		try{
			
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-mm HH:mm::ss");
			String date = format.format(cal.getTime());
			
			String queryStr = " FROM ContactsLoyalty WHERE cardStatus = 'Active' "
					+ " AND membershipExpirationDate IS NOT NULL " 
					+ " AND membershipExpirationDate <= '"+date+"'";
					//+"' AND expiryStatus = 'New'";
			
			contactsList  = (List<ContactsLoyalty>)getHibernateTemplate().find(queryStr);
			
		}catch(Exception e){
			logger.error(">>> Exception in fetchCurrentActiveTransForExpiry dao >>>",e);
		}
		return contactsList;
		
	}

	/*public List<Object[]> findTotRegistrationsRate(Long userId, Long prgmId,
			String startDateStr, String endDateStr, String storeNo, Long cardsetId, boolean isTransacted, String typeDiff,String type,String employeeIdStr,Long tierId) {

		String subQry = "";
		if(storeNo != null && storeNo.length() != 0) {
			subQry += " AND cl.pos_location_id in ("+storeNo+")";
		}
		if(cardsetId != null) {
			subQry += " AND cl.card_set_id =" + cardsetId.longValue();
		}
		if(tierId != null) {
			subQry += " AND cl.program_tier_id =" + tierId.longValue();
		}
		if(employeeIdStr != null && employeeIdStr.length() != 0) {
			subQry += " AND cl.emp_id in ("+employeeIdStr+")";
		}

		String apndQry = "";
		if(type.equalsIgnoreCase("loyalty")) {
			apndQry = " AND  (cl.reward_flag='"+OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L+"' OR cl.reward_flag='"+OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_GL+"')";
		}else {
			apndQry = " AND cl.reward_flag='"+OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_G+"'";
		}
		
		
		String query = "";
		String qry = " , ( SELECT loyalty_id FROM loyalty_transaction_child WHERE program_id = " + prgmId.longValue() + 
						  " AND transaction_type !='"+OCConstants.LOYALTY_TRANS_TYPE_ENROLLMENT+"' AND source_type = '"+ OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_STORE +"'" +
							" GROUP BY loyalty_id  HAVING  COUNT(loyalty_id) >= 1)  tc " ; 

		
		String qry = " , fetchCurrentActiveTransForExpiry( SELECT loyalty_id FROM loyalty_transaction_child WHERE program_id = " + prgmId.longValue() + 
				  " AND transaction_type !='"+OCConstants.LOYALTY_TRANS_TYPE_ENROLLMENT+"'"+// AND source_type = '"+ OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_STORE +"'" +
					" GROUP BY loyalty_id  HAVING  COUNT(loyalty_id) >= 1)  tc " ; 

		if(typeDiff.equalsIgnoreCase("days")) { 

			if(isTransacted) {
				query="SELECT COUNT(cl.loyalty_id),DATE(cl.created_date) FROM contacts_loyalty cl" + qry +
						" WHERE cl.loyalty_id = tc.loyalty_id AND cl.user_id = " + userId.longValue() +  subQry +""
						+apndQry+" AND cl.created_date BETWEEN '"+startDateStr+"' AND '"+endDateStr+"'  " +
						" GROUP BY  DATE(cl.created_date) ORDER BY DATE(cl.created_date)";
			}
			else {
				query="SELECT COUNT(cl.loyalty_id),DATE(cl.created_date) FROM contacts_loyalty cl " +
						" WHERE cl.user_id = " + userId.longValue() + " AND cl.program_id = "+ prgmId.longValue() +   subQry +""
						+apndQry+" AND  cl.created_date BETWEEN '"+startDateStr+"' AND '"+endDateStr+"'  " +
						" GROUP BY  DATE(cl.created_date) ORDER BY DATE(cl.created_date)";
			}
		}else if(typeDiff.equalsIgnoreCase("months")) { 

			if(isTransacted) {
				query="SELECT COUNT(cl.loyalty_id),MONTH(cl.created_date) FROM contacts_loyalty cl" + qry +
						" WHERE cl.loyalty_id = tc.loyalty_id AND cl.user_id = " + userId.longValue() +  subQry +""
						+apndQry+" AND  cl.created_date BETWEEN '"+startDateStr+"' AND '"+endDateStr+"'  " +
						" GROUP BY  MONTH(cl.created_date) ORDER BY MONTH(cl.created_date)";
			}
			else {
				query="SELECT COUNT(cl.loyalty_id),MONTH(cl.created_date) FROM contacts_loyalty cl " +
						" WHERE cl.user_id = " + userId.longValue() + " AND cl.program_id = "+ prgmId.longValue()  +  subQry +""
						+apndQry+" AND cl.created_date BETWEEN '"+startDateStr+"' AND '"+endDateStr+"'  " +
						" GROUP BY  MONTH(cl.created_date) ORDER BY MONTH(cl.created_date)";
			}
		}
		else if(typeDiff.equalsIgnoreCase("years")) { 

			if(isTransacted) {
				query="SELECT COUNT(cl.loyalty_id),YEAR(cl.created_date) FROM contacts_loyalty cl" + qry +
						" WHERE cl.loyalty_id = tc.loyalty_id AND cl.user_id = " + userId.longValue() +  subQry +""
						+apndQry+" AND  cl.created_date BETWEEN '"+startDateStr+"' AND '"+endDateStr+"'  " +
						" GROUP BY  YEAR(cl.created_date) ORDER BY YEAR(cl.created_date)";
			}
			else {
				query="SELECT COUNT(cl.loyalty_id),YEAR(cl.created_date) FROM contacts_loyalty cl " +
						" WHERE cl.user_id = " + userId.longValue() + " AND cl.program_id = "+ prgmId.longValue()  +  subQry +""
						+apndQry+"  AND cl.created_date BETWEEN '"+startDateStr+"' AND '"+endDateStr+"'  " +
						" GROUP BY  YEAR(cl.created_date) ORDER BY YEAR(cl.created_date)";
			}
		}

		
		List<Object[]> tempList = null;
		
		
		tempList= jdbcTemplate.query(query, new RowMapper() {

	        public Object[] mapRow(ResultSet rs, int rowNum) throws SQLException {
	        
	        	Object[] object = new Object[2];
	        	
	        	object[0] = rs.getString(1); 
	        	object[1] = rs.getString(2); 
	        	return object;
	        }
	        
		});
		

			return tempList;

	}*/
	public List<Object[]> findTotRegistrationsRate(Long userId, Long prgmId,
			String startDateStr, String endDateStr, String subsidiaryNo, String storeNo, Long cardsetId, boolean isTransacted, String typeDiff,String type,String employeeIdStr,Long tierId) {

		String subQry = "";
		if(subsidiaryNo != null && subsidiaryNo.length() != 0) {
			subQry += " AND cl.subsidiary_number in ("+subsidiaryNo+")";
		}
		if(storeNo != null && storeNo.length() != 0) {
			subQry += " AND cl.pos_location_id in ("+storeNo+")";
		}
		if(cardsetId != null) {
			subQry += " AND cl.card_set_id =" + cardsetId.longValue();
		}
		if(tierId != null) {
			subQry += " AND cl.program_tier_id =" + tierId.longValue();
		}
		if(employeeIdStr != null && employeeIdStr.length() != 0) {
			subQry += " AND cl.emp_id in ("+employeeIdStr+")";
		}

		String apndQry = "";
		/*if(type.equalsIgnoreCase("loyalty")) {
			apndQry = " AND  (cl.reward_flag='"+OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L+"' OR cl.reward_flag='"+OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_GL+"')";
		}else {
			apndQry = " AND cl.reward_flag='"+OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_G+"'";
		}*/
		
		
		String query = "";
		/*String qry = " , ( SELECT loyalty_id FROM loyalty_transaction_child WHERE program_id = " + prgmId.longValue() + 
						  " AND transaction_type !='"+OCConstants.LOYALTY_TRANS_TYPE_ENROLLMENT+"' AND source_type = '"+ OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_STORE +"'" +
							" GROUP BY loyalty_id  HAVING  COUNT(loyalty_id) >= 1)  tc " ; 
*/
		String qry = " ,( SELECT Distinct loyalty_id FROM loyalty_transaction_child WHERE user_id= " + userId.longValue() +" AND program_id = " + prgmId.longValue() + 
				  " AND transaction_type !='"+OCConstants.LOYALTY_TRANS_TYPE_ENROLLMENT+"' AND  created_date BETWEEN '"+startDateStr+"' AND '"+endDateStr+"'  "+//AND source_type = '"+ OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_STORE +"'" +
					")  tc " ; 

		
		if(typeDiff.equalsIgnoreCase("days")) { 

			if(isTransacted) {
				

						
				query="SELECT SUM(IF((cl.reward_flag='"+OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L+"'"
						+ " OR cl.reward_flag='"+OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_GL+"'), 1,0)) as loyalty,"
								+ "SUM(IF((cl.reward_flag='"+OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_G+"' ), 1,0)) as gift,"
														+ "DATE(cl.created_date) FROM contacts_loyalty cl" + qry +
						" WHERE cl.loyalty_id = tc.loyalty_id AND cl.user_id = " + userId.longValue() +  subQry +""
						+apndQry+" AND cl.created_date BETWEEN '"+startDateStr+"' AND '"+endDateStr+"'  " +
						" GROUP BY  DATE(cl.created_date) ORDER BY DATE(cl.created_date)";
			}
			else {
				query="SELECT SUM(IF((cl.reward_flag='"+OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L+"'"
						+ " OR cl.reward_flag='"+OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_GL+"'), 1,0)) as loyalty,"
								+ "SUM(IF((cl.reward_flag='"+OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_G+"' ), 1,0)) as gift"
				//query="SELECT COUNT(cl.loyalty_id),DATE(cl.created_date) FROM contacts_loyalty cl " +
						+" ,DATE(cl.created_date) FROM contacts_loyalty cl  WHERE cl.user_id = " + userId.longValue() + " AND cl.program_id = "+ prgmId.longValue() +   subQry +""
						+apndQry+" AND  cl.created_date BETWEEN '"+startDateStr+"' AND '"+endDateStr+"'  " +
						" GROUP BY  DATE(cl.created_date) ORDER BY DATE(cl.created_date)";
			}
		}else if(typeDiff.equalsIgnoreCase("months")) { 

			if(isTransacted) {
				query="SELECT SUM(IF((cl.reward_flag='"+OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L+"'"
						+ " OR cl.reward_flag='"+OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_GL+"'), 1,0)) as loyalty,"
								+ "SUM(IF((cl.reward_flag='"+OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_G+"'), 1,0)) as gift,DATE(cl.created_date) FROM contacts_loyalty cl" + qry +
						" WHERE cl.loyalty_id = tc.loyalty_id AND cl.user_id = " + userId.longValue() +  subQry +""
						+apndQry+" AND  cl.created_date BETWEEN '"+startDateStr+"' AND '"+endDateStr+"'  " +
						" GROUP BY  MONTH(cl.created_date) ORDER BY MONTH(cl.created_date)";
			}
			else {
				query="SELECT SUM(IF((cl.reward_flag='"+OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L+"'"
						+ " OR cl.reward_flag='"+OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_GL+"'), 1,0)) as loyalty,"
								+ "SUM(IF((cl.reward_flag='"+OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_G+"' ), 1,0)) as gift"
				//query="SELECT COUNT(cl.loyalty_id),MONTH(cl.created_date) FROM contacts_loyalty cl " +
						+" ,MONTH(cl.created_date) FROM contacts_loyalty cl " +
						" WHERE cl.user_id = " + userId.longValue() + " AND cl.program_id = "+ prgmId.longValue()  +  subQry +""
						+apndQry+" AND cl.created_date BETWEEN '"+startDateStr+"' AND '"+endDateStr+"'  " +
						" GROUP BY  MONTH(cl.created_date) ORDER BY MONTH(cl.created_date)";
			}
		}
		else if(typeDiff.equalsIgnoreCase("years")) { 

			if(isTransacted) {
				query="SELECT SUM(IF((cl.reward_flag='"+OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L+"'"
						+ " OR cl.reward_flag='"+OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_GL+"'), 1,0)) as loyalty,"
								+ "SUM(IF((cl.reward_flag='"+OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_G+"' ), 1,0)) as gift,YEAR(cl.created_date) FROM contacts_loyalty cl" + qry +
						" WHERE cl.loyalty_id = tc.loyalty_id AND cl.user_id = " + userId.longValue() +  subQry +""
						+apndQry+" AND  cl.created_date BETWEEN '"+startDateStr+"' AND '"+endDateStr+"'  " +
						" GROUP BY  YEAR(cl.created_date) ORDER BY YEAR(cl.created_date)";
			}
			else {
				query="SELECT SUM(IF((cl.reward_flag='"+OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L+"'"
						+ " OR cl.reward_flag='"+OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_GL+"'), 1,0)) as loyalty,"
								+ "SUM(IF((cl.reward_flag='"+OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_G+"' ), 1,0)) as gift"
						//query="SELECT COUNT(cl.loyalty_id),MONTH(cl.created_date) FROM contacts_loyalty cl " +
								+" ,YEAR(cl.created_date) FROM contacts_loyalty cl " +
						" WHERE cl.user_id = " + userId.longValue() + " AND cl.program_id = "+ prgmId.longValue()  +  subQry +""
						+apndQry+"  AND cl.created_date BETWEEN '"+startDateStr+"' AND '"+endDateStr+"'  " +
						" GROUP BY  YEAR(cl.created_date) ORDER BY YEAR(cl.created_date)";
			}
		}

		logger.debug("query for plot data ==>"+query );
		List<Object[]> tempList = null;
		
		
		tempList= jdbcTemplate.query(query, new RowMapper() {

	        public Object[] mapRow(ResultSet rs, int rowNum) throws SQLException {
	        
	        	Object[] object = new Object[3];
	        	
	        	object[0] = rs.getString(1); 
	        	object[1] = rs.getString(2); 
	        	object[2] = rs.getString(3); 
	        	//object[3] = rs.getString(4); 
	        	return object;
	        }
	        
		});
		

			return tempList;

	}
	public Map<Long, Long> getTierEnrolledCount(Long userId, Long prgmId, String startDateStr,
			String endDateStr,String linkCardSetStr,String linkTierStr) {
			String subQry = "";
			/*if(transType != null) {
				subQry += " AND transactionType ='" + transType + "' ";
			}*/
			/*if(storeNo != null && storeNo.length() != 0) {
				subQry += " AND posStoreLocationId in ("+storeNo+")";
			}
			if(cardsetId != null) {
				subQry += " AND cardSetId =" + cardsetId.longValue();
			}
			if(employeeIdStr != null && employeeIdStr.length() != 0 ){
				subQry += " AND empId in ("+employeeIdStr+")";
			}*/
			if(linkCardSetStr != null && linkCardSetStr.length() != 0){
				subQry += " AND cardSetId in ("+linkCardSetStr+")";
			}
			if(linkTierStr != null && linkTierStr.length() != 0){
				subQry += " AND programTierId in ("+linkTierStr+")";
			}
			
			
		String query  = " SELECT programTierId, COUNT(loyaltyId) FROM ContactsLoyalty " +
				" WHERE userId = " + userId.longValue()  + " AND programId = " + prgmId.longValue() + subQry +
				" AND createdDate BETWEEN '"+startDateStr+"' AND '"+endDateStr+"' GROUP BY programTierId ";

		List<Object[]> list = getHibernateTemplate().find(query);
		Map<Long, Long> cardSetMap = new HashMap<Long, Long>();
		for(Object[] obj:list){
			cardSetMap.put((Long) obj[0], (Long)obj[1]);
		}
		 return cardSetMap;
		
	}
	
	public Map<Long, Long> getTierEnrolledCount(Long userId, Long prgmId, String startDateStr,
			String endDateStr,String linkCardSetStr,String linkTierStr,boolean isTransacted,String storeNo,String employeeIdStr) {
			String subQry = "";
			String query = "";
			/*if(transType != null) {
				subQry += " AND transactionType ='" + transType + "' ";
			}*/
			if(storeNo != null && storeNo.length() != 0) {
				subQry += " AND cl.pos_location_id in ("+storeNo+")";
			}
			
			if(employeeIdStr != null && employeeIdStr.length() != 0 ){
				subQry += " AND cl.emp_id in ("+employeeIdStr+")";
			}
			if(linkCardSetStr != null && linkCardSetStr.length() != 0){
				subQry += " AND cl.card_set_id in ("+linkCardSetStr+")";
			}
			if(linkTierStr != null && linkTierStr.length() != 0){
				subQry += " AND cl.program_tier_id in ("+linkTierStr+")";
			}
			if(isTransacted){
				/*String qry = " , (SELECT loyalty_id FROM loyalty_transaction_child WHERE program_id = " + prgmId.longValue() + 
						" AND transaction_type !='"+OCConstants.LOYALTY_TRANS_TYPE_ENROLLMENT+ "' AND source_type = '"+ OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_STORE +"' " +
						" GROUP BY loyalty_id HAVING  COUNT(loyalty_id) >= 1 ) tc " ;  
			*/	
				String qry = " , (SELECT loyalty_id FROM loyalty_transaction_child WHERE program_id = " + prgmId.longValue() + 
						" AND transaction_type !='"+OCConstants.LOYALTY_TRANS_TYPE_ENROLLMENT+ "'" +// AND source_type = '"+ OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_STORE +"' " +
						" GROUP BY loyalty_id HAVING  COUNT(loyalty_id) >= 1 ) tc " ;  
			
				query = " SELECT cl.program_tier_id, COUNT(cl.loyalty_id) FROM contacts_loyalty cl " + qry +
						" WHERE cl.loyalty_id = tc.loyalty_id AND cl.user_id = " + userId.longValue()  + " AND cl.program_id = " + prgmId.longValue() + subQry +
						" AND cl.created_date BETWEEN '"+startDateStr+"' AND '"+endDateStr+"' GROUP BY cl.program_tier_id ";

			}else{
			
			query  = " SELECT cl.program_tier_id, COUNT(cl.loyalty_id) FROM contacts_loyalty cl " +
				" WHERE cl.user_id = " + userId.longValue()  + " AND cl.program_id = " + prgmId.longValue() + subQry +
				" AND cl.created_date BETWEEN '"+startDateStr+"' AND '"+endDateStr+"' GROUP BY cl.program_tier_id ";
			}
			logger.debug("ENROLL"+query);

			List<Object[]> list = jdbcTemplate.query(query, new RowMapper() {

		        public Object[] mapRow(ResultSet rs, int rowNum) throws SQLException {
		        
		        	Object[] object = new Object[2];
		        	
		        	object[0] = rs.getLong(1); 
		        	object[1] = rs.getLong(2);
		        	return object;
		        }
		        
			});
		Map<Long, Long> cardSetMap = new HashMap<Long, Long>();
		for(Object[] obj:list){
			cardSetMap.put((Long) obj[0], (Long)obj[1]);
		}
		 return cardSetMap;
		
	}
	public List<Object[]> getTierUpgradedCount(Long userId, Long prgmId, String startDateStr,
			String endDateStr,Long tierId) {
			String subQry = "";
			/*if(transType != null) {
				subQry += " AND transactionType ='" + transType + "' ";
			}*/
			/*if(storeNo != null && storeNo.length() != 0) {
				subQry += " AND posStoreLocationId in ("+storeNo+")";
			}
			if(cardsetId != null) {
				subQry += " AND cardSetId =" + cardsetId.longValue();
			}
			if(employeeIdStr != null && employeeIdStr.length() != 0 ){
				subQry += " AND empId in ("+employeeIdStr+")";
			}*/
			
			/*if(tierStr != null && tierStr.length() != 0){
				subQry += " AND program_tier_id in ("+tierStr+")";
			}*/
			
			
		String query  = " SELECT SUM(IF(tier_upgraded_date IS NOT NULL, 1,0)) FROM contacts_loyalty " +
				" WHERE user_id = " + userId.longValue()  + " AND program_id = " + prgmId.longValue() + " AND program_tier_id = " + tierId.longValue() +
				" AND created_date BETWEEN '"+startDateStr+"' AND '"+endDateStr+"' ";

		List<Object[]> tempList = jdbcTemplate.query(query, new RowMapper() {

	        public Object[] mapRow(ResultSet rs, int rowNum) throws SQLException {
	        
	        	Object[] object = new Object[1];
	        	
	        	object[0] = rs.getLong(1); 
	        	return object;
	        }
	        
		});
		

			return tempList;
	}
		/*Map<Long, Long> cardSetMap = new HashMap<Long, Long>();
		for(Object[] obj:tempList){
			cardSetMap.put((Long) obj[0], (Long)obj[1]);
		}
		 return cardSetMap;
	}*/
	
	public List<Object[]> getTierUpgradedCount(Long userId, Long prgmId, String startDateStr,
			String endDateStr,Long tierId,boolean isTransacted,String storeNo,Long cardsetId,String employeeIdStr) {
			String subQry = "";
			String query = "";
			/*if(transType != null) {
				subQry += " AND transactionType ='" + transType + "' ";
			}*/
			if(storeNo != null && storeNo.length() != 0) {
				subQry += " AND cl.pos_location_id in ("+storeNo+")";
			}
			if(cardsetId != null) {
				subQry += " AND cl.card_set_id =" + cardsetId.longValue();
			}
			if(tierId != null) {
				subQry += " AND cl.program_tier_id =" + tierId.longValue();
			}
			if(employeeIdStr != null && employeeIdStr.length() != 0 ){
				subQry += " AND cl.emp_id in ("+employeeIdStr+")";
			}
			
			/*if(tierStr != null && tierStr.length() != 0){
				subQry += " AND program_tier_id in ("+tierStr+")";
			}*/
			if(isTransacted){
				/*String qry = " , (SELECT loyalty_id FROM loyalty_transaction_child WHERE program_id = " + prgmId.longValue() + 
						" AND transaction_type !='"+OCConstants.LOYALTY_TRANS_TYPE_ENROLLMENT+ "' AND source_type = '"+ OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_STORE +"' " +
						" GROUP BY loyalty_id HAVING  COUNT(loyalty_id) >= 1 ) tc " ; 
			*/	String qry = " , (SELECT loyalty_id FROM loyalty_transaction_child WHERE program_id = " + prgmId.longValue() + 
						" AND transaction_type !='"+OCConstants.LOYALTY_TRANS_TYPE_ENROLLMENT+ "'" +// AND source_type = '"+ OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_STORE +"' " +
						" GROUP BY loyalty_id HAVING  COUNT(loyalty_id) >= 1 ) tc " ; 
				 
				
				query  = " SELECT SUM(IF(cl.tier_upgraded_date IS NOT NULL, 1,0)) FROM contacts_loyalty cl " + qry +
						" WHERE cl.loyalty_id = tc.loyalty_id AND cl.user_id = " + userId.longValue()  + " AND cl.program_id = " + prgmId.longValue()  + subQry +
						" AND cl.created_date BETWEEN '"+startDateStr+"' AND '"+endDateStr+"' ";
			}else{
			
		         query  = " SELECT SUM(IF(cl.tier_upgraded_date IS NOT NULL, 1,0)) FROM contacts_loyalty cl" +
				" WHERE cl.user_id = " + userId.longValue()  + " AND cl.program_id = " + prgmId.longValue() +  subQry +
				" AND cl.created_date BETWEEN '"+startDateStr+"' AND '"+endDateStr+"' ";
			}
			

		List<Object[]> tempList = jdbcTemplate.query(query, new RowMapper() {

	        public Object[] mapRow(ResultSet rs, int rowNum) throws SQLException {
	        
	        	Object[] object = new Object[1];
	        	
	        	object[0] = rs.getLong(1); 
	        	return object;
	        }
	        
		});
		

			return tempList;
	}
	
	/*public List<Object[]> getTotalMemberships(Long userId, Long prgmId, String startDateStr,
			String endDateStr,Long tierId,boolean isTransacted,String storeNo,Long cardsetId,String employeeIdStr) {
			String subQry = "";
			String query = "";
			if(transType != null) {
				subQry += " AND transactionType ='" + transType + "' ";
			}
			if(storeNo != null && storeNo.length() != 0) {
				subQry += " AND cl.pos_location_id in ("+storeNo+")";
			}
			if(cardsetId != null) {
				subQry += " AND cl.card_set_id =" + cardsetId.longValue();
			}
			if(tierId != null) {
				subQry += " AND cl.program_tier_id =" + tierId.longValue();
			}
			if(employeeIdStr != null && employeeIdStr.length() != 0 ){
				subQry += " AND cl.emp_id in ("+employeeIdStr+")";
			}
			
			if(tierStr != null && tierStr.length() != 0){
				subQry += " AND program_tier_id in ("+tierStr+")";
			}
			if(isTransacted){
				String qry = " , (SELECT loyalty_id FROM loyalty_transaction_child WHERE program_id = " + prgmId.longValue() + 
						" AND transaction_type !='"+OCConstants.LOYALTY_TRANS_TYPE_ENROLLMENT+ "' AND source_type = '"+ OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_STORE +"' " +
						" GROUP BY loyalty_id HAVING  COUNT(loyalty_id) >= 1 ) tc " ; 
				
				String qry = " , (SELECT loyalty_id FROM loyalty_transaction_child WHERE program_id = " + prgmId.longValue() + 
						" AND transaction_type !='"+OCConstants.LOYALTY_TRANS_TYPE_ENROLLMENT+ "'" + // AND source_type = '"+ OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_STORE +"' " +
						" GROUP BY loyalty_id HAVING  COUNT(loyalty_id) >= 1 ) tc " ; 
				 query  = " SELECT COUNT(cl.loyalty_id) FROM contacts_loyalty cl " + qry +
						" WHERE cl.loyalty_id = tc.loyalty_id AND cl.user_id = " + userId.longValue()  + " AND cl.program_id = " + prgmId.longValue() + subQry +
						" AND cl.created_date BETWEEN '"+startDateStr+"' AND '"+endDateStr+"' ";
			}else{
			
		         query  = " SELECT COUNT(cl.loyalty_id) FROM contacts_loyalty cl" +
				" WHERE cl.user_id = " + userId.longValue()  + " AND cl.program_id = " + prgmId.longValue() +  subQry +
				" AND cl.created_date BETWEEN '"+startDateStr+"' AND '"+endDateStr+"' ";
			}

		List<Object[]> tempList = jdbcTemplate.query(query, new RowMapper() {

	        public Object[] mapRow(ResultSet rs, int rowNum) throws SQLException {
	        
	        	Object[] object = new Object[1];
	        	
	        	object[0] = rs.getLong(1); 
	        	return object;
	        }
	        
		});
		

			return tempList;
	}*/
	public List<Object[]> getTotalMemberships(Long userId, Long prgmId, String startDateStr,
			String endDateStr,Long tierId,boolean isTransacted,String subsidiaryNo,String storeNo,Long cardsetId,String employeeIdStr) {
			String subQry = "";
			String query = "";
			/*if(transType != null) {
				subQry += " AND transactionType ='" + transType + "' ";
			}*/
			if(subsidiaryNo != null && subsidiaryNo.length() != 0) {
				subQry += " AND cl.subsidiary_number in ("+subsidiaryNo+")";
			}
			if(storeNo != null && storeNo.length() != 0) {
				subQry += " AND cl.pos_location_id in ("+storeNo+")";
			}
			if(cardsetId != null) {
				subQry += " AND cl.card_set_id =" + cardsetId.longValue();
			}
			if(tierId != null) {
				subQry += " AND cl.program_tier_id =" + tierId.longValue();
			}
			if(employeeIdStr != null && employeeIdStr.length() != 0 ){
				subQry += " AND cl.emp_id in ("+employeeIdStr+")";
			}
			
			/*if(tierStr != null && tierStr.length() != 0){
				subQry += " AND program_tier_id in ("+tierStr+")";
			}*/
			if(isTransacted){
				/*String qry = " , (SELECT loyalty_id FROM loyalty_transaction_child WHERE program_id = " + prgmId.longValue() + 
						" AND transaction_type !='"+OCConstants.LOYALTY_TRANS_TYPE_ENROLLMENT+ "' AND source_type = '"+ OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_STORE +"' " +
						" GROUP BY loyalty_id HAVING  COUNT(loyalty_id) >= 1 ) tc " ; 
				*/
				String qry = " , (SELECT Distinct loyalty_id FROM loyalty_transaction_child WHERE user_id = " + userId.longValue()+
						" AND program_id = " + prgmId.longValue() + 
						" AND transaction_type !='"+OCConstants.LOYALTY_TRANS_TYPE_ENROLLMENT+ "'" + // AND source_type = '"+ OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_STORE +"' " +
						"  AND created_date BETWEEN '"+startDateStr+"' AND '"+endDateStr+"'  ) tc " ; 
				 query  = " SELECT COUNT(cl.loyalty_id), cl.program_tier_id FROM contacts_loyalty cl " + qry +
						" WHERE cl.loyalty_id = tc.loyalty_id AND cl.user_id = " + userId.longValue()  + " AND cl.program_id = " + prgmId.longValue() + subQry +
						" AND cl.created_date BETWEEN '"+startDateStr+"' AND '"+endDateStr+"' AND cl.program_tier_id IS NOT NULL group by cl.program_tier_id ";
			}else{
			
		         query  = " SELECT COUNT(cl.loyalty_id), cl.program_tier_id FROM contacts_loyalty cl" +
				" WHERE cl.user_id = " + userId.longValue()  + " AND cl.program_id = " + prgmId.longValue() +  subQry +
				" AND cl.created_date BETWEEN '"+startDateStr+"' AND '"+endDateStr+"' AND cl.program_tier_id IS NOT NULL group by cl.program_tier_id ";
			}

		List<Object[]> tempList = jdbcTemplate.query(query, new RowMapper() {

	        public Object[] mapRow(ResultSet rs, int rowNum) throws SQLException {
	        
	        	Object[] object = new Object[2];
	        	
	        	object[0] = rs.getLong(1); 
	        	object[1] = rs.getLong(2); 
	        	return object;
	        }
	        
		});
		

			return tempList;
	}
	
	
	
	/*public int getRegistrationsCount(Long userId, Long prgmId,
			String startDateStr, String endDateStr, String key, String storeNo, Long cardsetId, boolean isTransacted,String status,String employeeIdStr,Long tierId) {  
		
		String subQry = "";
		if(key != null){
			subQry += " AND cl.card_number LIKE '%"+key+"%'";
		}
		if(storeNo != null && storeNo.length() != 0) {
			subQry += " AND cl.pos_location_id in ("+storeNo+")";
		}
		if(cardsetId != null) {
			subQry += " AND cl.card_set_id =" + cardsetId.longValue();
		}
		if(tierId != null) {
			subQry += " AND cl.program_tier_id =" + tierId.longValue();
		}
		if(status != null) {
        	subQry += " AND cl.membership_status = '"+ status +"'";
        }
		if(employeeIdStr != null && employeeIdStr.length() != 0 ) {
        	subQry += " AND cl.emp_id in ("+employeeIdStr+")";
        }
	
		String query = "";
		if(isTransacted) {

			String qry  = " , ( SELECT loyalty_id FROM loyalty_transaction_child WHERE program_id = " + prgmId.longValue() + 
						  " AND transaction_type !='"+OCConstants.LOYALTY_TRANS_TYPE_ENROLLMENT+"' AND source_type = '"+ OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_STORE +"'" +
							" GROUP BY loyalty_id  HAVING  COUNT(loyalty_id) >= 1)  tc " ; 
			String qry  = " , ( SELECT loyalty_id FROM loyalty_transaction_child WHERE program_id = " + prgmId.longValue() + 
					  " AND transaction_type !='"+OCConstants.LOYALTY_TRANS_TYPE_ENROLLMENT+"'" +// AND source_type = '"+ OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_STORE +"'" +
						" GROUP BY loyalty_id  HAVING  COUNT(loyalty_id) >= 1)  tc " ; 
		
			
			query = "SELECT COUNT(cl.loyalty_id) FROM contacts_loyalty cl " +  qry + " WHERE cl.loyalty_id = tc.loyalty_id AND cl.user_id= " + userId.longValue() + 
					 subQry +	" AND cl.created_date BETWEEN '" + startDateStr + "' AND '"+ endDateStr +"' ";
		}
		else {
			query = "SELECT COUNT(cl.loyalty_id) FROM contacts_loyalty cl WHERE cl.user_id= " + userId.longValue() + " AND cl.program_id = " + prgmId.longValue() + subQry +
					" AND cl.created_date BETWEEN '" + startDateStr + "' AND '"+ endDateStr +"' ";
		}

		int count = jdbcTemplate.queryForInt(query);
	    return count;    
		
	}*/
	public int getRegistrationsCount(Long userId, Long prgmId,
			String startDateStr, String endDateStr, String key, String subsidiaryNo, String storeNo, Long cardsetId, boolean isTransacted,String status,String employeeIdStr,Long tierId) {  
		
		String subQry = "";
		if(key != null){
			subQry += " AND cl.card_number LIKE '%"+key.trim()+"%'";
		}
		if(subsidiaryNo != null && subsidiaryNo.length() != 0) {
			subQry += " AND cl.subsidiary_number in ("+subsidiaryNo+")";
		}
		if(storeNo != null && storeNo.length() != 0) {
			subQry += " AND cl.pos_location_id in ("+storeNo+")";
		}
		if(cardsetId != null) {
			subQry += " AND cl.card_set_id =" + cardsetId.longValue();
		}
		if(tierId != null) {
			subQry += " AND cl.program_tier_id =" + tierId.longValue();
		}
		if(status != null) {
        	subQry += " AND cl.membership_status = '"+ status +"'";
        }
		if(employeeIdStr != null && employeeIdStr.length() != 0 ) {
        	subQry += " AND cl.emp_id in ("+employeeIdStr+")";
        }
	
		String query = "";
		if(isTransacted) {

			/*String qry  = " , ( SELECT loyalty_id FROM loyalty_transaction_child WHERE program_id = " + prgmId.longValue() + 
						  " AND transaction_type !='"+OCConstants.LOYALTY_TRANS_TYPE_ENROLLMENT+"' AND source_type = '"+ OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_STORE +"'" +
							" GROUP BY loyalty_id  HAVING  COUNT(loyalty_id) >= 1)  tc " ; */
			String qry  = " ,( SELECT distinct loyalty_id FROM loyalty_transaction_child WHERE user_id ="+userId.longValue()+" and program_id=" + prgmId.longValue() + 
					  " AND transaction_type !='"+OCConstants.LOYALTY_TRANS_TYPE_ENROLLMENT+"'"+ //AND source_type = '"+ OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_STORE +"'" +
						 " AND created_date BETWEEN '" + startDateStr + "' AND '"+ endDateStr +"' )  tc " ; 
		
			
			query = "SELECT COUNT(cl.loyalty_id) FROM contacts_loyalty cl " +  qry + " WHERE  cl.loyalty_id = tc.loyalty_id and cl.user_id= " + userId.longValue() + " AND cl.program_id ="+ prgmId.longValue()+
					 subQry +	" AND cl.created_date BETWEEN '" + startDateStr + "' AND '"+ endDateStr +"' ";
		}
		else {
			query = "SELECT COUNT(cl.loyalty_id) FROM contacts_loyalty cl WHERE cl.user_id= " + userId.longValue() + " AND cl.program_id = " + prgmId.longValue() + subQry +
					" AND cl.created_date BETWEEN '" + startDateStr + "' AND '"+ endDateStr +"' ";
		}

		int count = jdbcTemplate.queryForInt(query);
	    return count;    
		
	}
	
	
	public int getRegistrationsCountforkey(Long userId, Long prgmId,String key) {  
		
		String subQry = "";
		if(key != null){
			subQry += " AND cl.card_number LIKE '%"+key+"%'";
		}
		String query = "";
		query = "SELECT COUNT(cl.loyalty_id) FROM contacts_loyalty cl "
				+ "WHERE cl.user_id= " + userId.longValue() + " AND cl.program_id = " + prgmId.longValue() + subQry;
		int count = jdbcTemplate.queryForInt(query);
	    return count;    
		
	}

	public Map<String, Object> getAllDestCards(Long userId,	Long prgmId){
		Map<String, Object> destMap = new HashMap<String, Object>();
		List <Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		String query = "SELECT loyalty_id, card_number from contacts_loyalty where user_id= " + userId.longValue() +
				" AND program_id = " + prgmId.longValue() +" AND loyalty_id in (select distinct transfered_to FROM contacts_loyalty " +
				" where user_id= " + userId.longValue() +" AND program_id = " + prgmId.longValue()  +" AND transfered_to is not null )";
		list = jdbcTemplate.queryForList(query);
		if(list != null && list.size()>0){
			for(Map m : list){
				destMap.put(m.get("loyalty_id").toString(),m.get("card_number"));
				}
		}
		return destMap;
	}
	public List<Object[]> getContactLtyList(Long userId, Long prgmId,
			String startDateStr, String endDateStr, int firstResult, int size,String key, String subsidiaryNo, String storeNo, Long cardsetId, boolean isTransacted,String status,String employeeIdStr,Long tierId) {
		final DecimalFormat f = new DecimalFormat("#0.00");
		String subQry = "";
		if(key != null){
			subQry += " AND cl.card_number LIKE '%"+key.trim()+"%'";
		}
		if(subsidiaryNo != null && subsidiaryNo.length() != 0) {
			subQry += " AND cl.subsidiary_number in ("+subsidiaryNo+")";
		}
		if(storeNo != null && storeNo.length() != 0) {
			subQry += " AND cl.pos_location_id in ("+storeNo+")";
		}
		if(cardsetId != null) {
			subQry += " AND cl.card_set_id =" + cardsetId.longValue();
		}
		if(tierId != null) {
			subQry += " AND cl.program_tier_id =" + tierId.longValue();
		}
        if(status != null) {
        	subQry += " AND cl.membership_status = '"+ status +"'";
        }
        if(employeeIdStr != null && employeeIdStr.length() != 0) {
        	subQry += " AND cl.emp_id in ("+employeeIdStr+")";
        }
		String query = "";
		/*if(isTransacted) {

			String qry = " , (SELECT loyalty_id FROM loyalty_transaction_child WHERE program_id = " + prgmId.longValue() + 
						" AND transaction_type !='"+OCConstants.LOYALTY_TRANS_TYPE_ENROLLMENT+ "'" +// AND source_type = '"+ OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_STORE +"' " +
						" GROUP BY loyalty_id HAVING  COUNT(loyalty_id) >= 1 ) tc " ; 

			query = " SELECT cl.card_number, cl.membership_status,  cl.contact_loyalty_type, cl.created_date, cl.contact_id, cl.loyalty_balance, cl.giftcard_balance,cl.pos_location_id, cl.reward_flag,cl.gift_balance, cl.holdpoints_balance, cl.holdAmount_balance,cl.total_loyalty_earned,cl.program_tier_id, cl.subsidiary_number" +
					" FROM contacts_loyalty cl " + qry + " WHERE cl.loyalty_id = tc.loyalty_id AND cl.user_id= " + userId.longValue() + 
					  subQry + " AND cl.created_date >= '" + startDateStr + "' AND cl.created_date <='" + endDateStr + "' " + 
					" ORDER BY cl.created_date DESC";
		}
		else {
			query = " SELECT cl.card_number, cl.membership_status,  cl.contact_loyalty_type, cl.created_date, cl.contact_id, cl.loyalty_balance,cl.giftcard_balance,cl.pos_location_id, cl.reward_flag, cl.gift_balance, cl.holdpoints_balance, cl.holdAmount_balance,cl.total_loyalty_earned,cl.program_tier_id, cl.subsidiary_number" +
					" FROM contacts_loyalty cl WHERE cl.user_id= " + userId.longValue() + " AND cl.program_id = " + prgmId.longValue() + subQry +
					" AND cl.created_date >= '" + startDateStr + "' AND cl.created_date <='"+ endDateStr +"' " + 
					" ORDER BY cl.created_date DESC";
		}*/
		/*if(isTransacted) {
			query="SELECT  cl.card_number, cl.membership_status,  cl.contact_loyalty_type, cl.created_date, cl.contact_id,cl.loyalty_balance, cl.giftcard_balance,cl.pos_location_id, cl.reward_flag,cl.gift_balance,"
	                   +"cl.holdpoints_balance, cl.holdAmount_balance,cl.total_loyalty_earned,cl.program_tier_id,cl.subsidiary_number,tc.numberOfVisits,tc.lifetimepurchase ,cc.first_name,cc.last_name,cc.mobile_phone,cc.email_id"
	                   + " FROM  contacts_loyalty cl,contacts cc  ,(SELECT loyalty_id, count(distinct docsid) as numberOfVisits,SUM(if(transaction_type = 'Issuance' AND entered_amount_type = 'Purchase',entered_amount, 0)) as lifetimepurchase"
	                   +" FROM loyalty_transaction_child WHERE program_id ="+ prgmId.longValue()+" AND transaction_type !='Enrollment'  GROUP BY loyalty_id HAVING  COUNT(loyalty_id) >= 1 ) tc "
	                  +" WHERE  cl.contact_id=cc.cid and cl.loyalty_id = tc.loyalty_id AND cl.user_id= " + userId.longValue() +" AND cl.program_id ="+ prgmId.longValue()+ subQry+" AND cl.created_date >= '" +startDateStr +"' AND cl.created_date <='"+ endDateStr +" ' ORDER BY cl.created_date DESC";

		}
		else {
			query="SELECT cl.card_number, cl.membership_status,  cl.contact_loyalty_type, cl.created_date, cl.contact_id,cl.loyalty_balance, cl.giftcard_balance,cl.pos_location_id, cl.reward_flag,cl.gift_balance,"
                   +"cl.holdpoints_balance, cl.holdAmount_balance,cl.total_loyalty_earned,cl.program_tier_id,cl.subsidiary_number,tc.numberOfVisits,tc.lifetimepurchase  ,cc.first_name,cc.last_name,cc.mobile_phone,cc.email_id "
                   + " FROM contacts cc , contacts_loyalty cl  LEFT JOIN(SELECT loyalty_id, count(distinct docsid) as numberOfVisits,SUM(if(transaction_type = 'Issuance' AND entered_amount_type = 'Purchase',entered_amount, 0)) as lifetimepurchase"
                   +" FROM loyalty_transaction_child WHERE program_id ="+ prgmId.longValue()+" GROUP BY loyalty_id HAVING  COUNT(loyalty_id) >= 1) tc  on cl.loyalty_id = tc.loyalty_id"
                  +" WHERE cl.contact_id=cc.cid   AND cl.user_id= " + userId.longValue() +" AND cl.program_id ="+ prgmId.longValue()+ subQry+"   AND cl.created_date >= '" +startDateStr +"' AND cl.created_date <='"+ endDateStr +" ' ORDER BY cl.created_date DESC";

		}*/
		if(isTransacted) {
			query="SELECT  cl.card_number, cl.membership_status,  cl.contact_loyalty_type, cl.created_date, cl.contact_id,cl.loyalty_balance, cl.giftcard_balance,cl.pos_location_id, cl.reward_flag,cl.gift_balance,"
	                   +"cl.holdpoints_balance, cl.holdAmount_balance,cl.total_loyalty_earned,cl.program_tier_id,cl.subsidiary_number,tc.numberOfVisits,(if(cl.cummulative_purchase_value>0,(cl.cummulative_purchase_value-(if(cl.cummulative_return_value>=0, cl.cummulative_return_value,0))), 0)) ,cc.first_name,cc.last_name,cc.mobile_phone,cc.email_id"
	                   + " FROM  contacts_loyalty cl,contacts cc  ,(SELECT loyalty_id, count(distinct docsid) as numberOfVisits"
	                   +" FROM loyalty_transaction_child WHERE user_id= "+ userId.longValue()+" AND program_id ="+ prgmId.longValue()+" AND transaction_type !='Enrollment' AND created_date >= '" +startDateStr +"' AND created_date <='"+ endDateStr +"' ) tc "
	                  +" WHERE  cl.contact_id=cc.cid and cl.loyalty_id = tc.loyalty_id AND cl.user_id= " + userId.longValue() +" AND cl.program_id ="+ prgmId.longValue()+ subQry+" AND cl.created_date >= '" +startDateStr +"' AND cl.created_date <='"+ endDateStr +" ' ORDER BY cl.created_date DESC";

		}
		else {
			query="SELECT cl.card_number, cl.membership_status,  cl.contact_loyalty_type, cl.created_date, cl.contact_id,cl.loyalty_balance, cl.giftcard_balance,cl.pos_location_id, cl.reward_flag,cl.gift_balance,"
                   +"cl.holdpoints_balance, cl.holdAmount_balance,cl.total_loyalty_earned,cl.program_tier_id,cl.subsidiary_number,tc.numberOfVisits,(if(cl.cummulative_purchase_value>0,(cl.cummulative_purchase_value-(if(cl.cummulative_return_value>=0, cl.cummulative_return_value,0))), 0))  ,cc.first_name,cc.last_name,cc.mobile_phone,cc.email_id "
                   + " FROM contacts cc , contacts_loyalty cl  LEFT JOIN(SELECT loyalty_id, count(distinct docsid) as numberOfVisits"
                   +" FROM loyalty_transaction_child WHERE user_id= "+ userId.longValue()+" AND program_id ="+ prgmId.longValue()+" AND created_date >= '" +startDateStr +"' AND created_date <='"+ endDateStr +"') tc  on cl.loyalty_id = tc.loyalty_id"
                  +" WHERE cl.contact_id=cc.cid   AND cl.user_id= " + userId.longValue() +" AND cl.program_id ="+ prgmId.longValue()+ subQry+"   AND cl.created_date >= '" +startDateStr +"' AND cl.created_date <='"+ endDateStr +"' ORDER BY cl.created_date DESC";

		}
		
		

		List<Object[]> tempList = null;
		
		logger.info("***Export Query :"+query);
			tempList= jdbcTemplate.query(query+" LIMIT "+firstResult+", "+size, new RowMapper() {
	
		        public Object[] mapRow(ResultSet rs, int rowNum) throws SQLException {
		        
		        	Object[] object = new Object[21];
		        	
		        	object[0] = rs.getString(1); //card number
		        	object[1] = rs.getString(2); //membership_status
		        	object[2] = rs.getString(3); //optin_medium
		        	object[3] = rs.getString(4); //created_date
		        	object[4] = rs.getLong(5); //contact_id
		        	object[5] = rs.getLong(6); //loyalty_balance
		        	object[6] = rs.getDouble(7); //giftcard_balance
		        	object[7] = rs.getString(8); //pos_location_id
		        	object[8] = rs.getString(9); //reward_flag
		        	object[9] = rs.getDouble(10); //gift_balance
		        	object[10] = rs.getLong(11); //hold_points_balance
		        	object[11] = rs.getDouble(12); //hold_currency_balance
		        	object[12] = rs.getString(13);//total_loyalty_earned
		        	object[13] = rs.getLong(14);//tier_id
		        	object[14] = rs.getString(15);// subsidiary_number
		        	object[15] = rs.getLong(16);
		        	object[16] = rs.getDouble(17);
		        	object[17] = rs.getString(18);
		        	object[18] = rs.getString(19);
		        	object[19] = rs.getString(20);
		        	object[20] = rs.getString(21);
		        	return object;
		        }
		        
			});
			
			return tempList;
		
	}

/*public List<Object[]> getContactLtyListforkey(Long userId, Long prgmId,int firstResult, int size,String key) {
			final DecimalFormat f = new DecimalFormat("#0.00");
			String subQry = "";
			if(key != null){
				subQry += " AND cl.card_number LIKE '%"+key+"%'";
			}
			String query = "";
		    query = " SELECT cl.card_number, cl.membership_status,  cl.contact_loyalty_type, cl.created_date, cl.contact_id, cl.loyalty_balance,cl.giftcard_balance,cl.pos_location_id, cl.reward_flag, cl.gift_balance, cl.holdpoints_balance, cl.holdAmount_balance,cl.total_loyalty_earned,cl.program_tier_id" +
						" FROM contacts_loyalty cl WHERE cl.user_id= " + userId.longValue() + " AND cl.program_id = " + prgmId.longValue() + subQry;
						
		    List<Object[]> tempList = null;
			
			
				tempList= jdbcTemplate.query(query+" LIMIT "+firstResult+", "+size, new RowMapper() {
		
			        public Object[] mapRow(ResultSet rs, int rowNum) throws SQLException {
			        
			        	Object[] object = new Object[14];
			        	
			        	object[0] = rs.getString(1); //card number
			        	object[1] = rs.getString(2); //membership_status
			        	object[2] = rs.getString(3); //optin_medium
			        	object[3] = rs.getString(4); //created_date
			        	object[4] = rs.getLong(5); //contact_id
			        	object[5] = rs.getLong(6); //loyalty_balance
			        	object[6] = rs.getDouble(7); //giftcard_balance
			        	object[7] = rs.getString(8); //pos_location_id
			        	object[8] = rs.getString(9); //reward_flag
			        	object[9] = rs.getDouble(10); //gift_balance
			        	object[10] = rs.getLong(11); //hold_points_balance
			        	object[11] = rs.getDouble(12); //hold_currency_balance
			        	object[12] = rs.getString(13);//total_loyalty_earned
			        	object[13] = rs.getLong(14);//program_tier_id
			        	return object;
			        }
			        
				});
				
				return tempList;
			
		}
*/
	public List<Object[]> getContactLtyListforkey(Long userId, Long prgmId,int firstResult, int size,String key) {
		final DecimalFormat f = new DecimalFormat("#0.00");
		String subQry = "";
		if(key != null){
			subQry += " AND cl.card_number LIKE '%"+key+"%'";
		}
		String query = "";
	    query = " SELECT cl.card_number, cl.membership_status,  cl.contact_loyalty_type, cl.created_date, cl.contact_id, cl.loyalty_balance,cl.giftcard_balance,cl.pos_location_id, cl.reward_flag, cl.gift_balance, cl.holdpoints_balance, cl.holdAmount_balance,cl.total_loyalty_earned,cl.program_tier_id,cl.subsidiary_number" +
					" FROM contacts_loyalty cl WHERE cl.user_id= " + userId.longValue() + " AND cl.program_id = " + prgmId.longValue() + subQry;
					
	    List<Object[]> tempList = null;
		
		
			tempList= jdbcTemplate.query(query+" LIMIT "+firstResult+", "+size, new RowMapper() {
	
		        public Object[] mapRow(ResultSet rs, int rowNum) throws SQLException {
		        
		        	Object[] object = new Object[15];
		        	
		        	object[0] = rs.getString(1); //card number
		        	object[1] = rs.getString(2); //membership_status
		        	object[2] = rs.getString(3); //optin_medium
		        	object[3] = rs.getString(4); //created_date
		        	object[4] = rs.getLong(5); //contact_id
		        	object[5] = rs.getLong(6); //loyalty_balance
		        	object[6] = rs.getDouble(7); //giftcard_balance
		        	object[7] = rs.getString(8); //pos_location_id
		        	object[8] = rs.getString(9); //reward_flag
		        	object[9] = rs.getDouble(10); //gift_balance
		        	object[10] = rs.getLong(11); //hold_points_balance
		        	object[11] = rs.getDouble(12); //hold_currency_balance
		        	object[12] = rs.getString(13);//total_loyalty_earned
		        	object[13] = rs.getLong(14);//program_tier_id
		        	object[14] = rs.getString(15);// subsidiary_number
		        	return object;
		        }
		        
			});
			
			return tempList;
		
	}

//modify
	public  Object[] getLiabilityData(Long userId, Long programId, String storeNo, Long cardsetId,Long tierId) {
		final DecimalFormat f = new DecimalFormat("#0.00");
		String subQry = "";
		if(storeNo != null) {
			subQry += " AND pos_location_id ='" + storeNo +"' ";
		}
		if(cardsetId != null) {
			subQry += " AND card_set_id =" + cardsetId.longValue();
		}
		if(tierId != null) {
			subQry += " AND program_tier_id =" + tierId.longValue();
		}
		
		String qry = " SELECT COUNT(loyalty_id), SUM(giftcard_balance), SUM(loyalty_balance) " +
					 " FROM contacts_loyalty "+
					 " WHERE user_id="+userId+" AND program_id =" + programId + 
					 " AND membership_status = '" + OCConstants.LOYALTY_MEMBERSHIP_STATUS_ACTIVE + "' "+ subQry;
		
		
		/*String query = " SELECT SUM(IF((membership_status = '" + OCConstants.LOYALTY_MEMBERSHIP_STATUS_ACTIVE + "'  ), 1,0)) as activecards, "
				+ " SUM(if(membership_status = '" + OCConstants.LOYALTY_MEMBERSHIP_STATUS_ACTIVE + "' ,giftcard_balance,0)) as activeamount, "
				+ " SUM(if(membership_status = '" + OCConstants.LOYALTY_MEMBERSHIP_STATUS_ACTIVE + "' ,loyalty_balance,0)) as activeltyamount ,"
				+" SELECT SUM(IF((membership_status = '" + OCConstants.LOYALTY_MEMBERSHIP_STATUS_EXPIRED + "'  ), 1,0)) as expiredcards, "
				+ " SUM(if(membership_status = '" + OCConstants.LOYALTY_MEMBERSHIP_STATUS_EXPIRED + "' ,giftcard_balance,0)) as expiredamount, "
				+ " SUM(if(membership_status = '" + OCConstants.LOYALTY_MEMBERSHIP_STATUS_EXPIRED + "' ,loyalty_balance,0)) as expiredltyamount, "
				+" SELECT SUM(IF((membership_status = '" + OCConstants.LOYALTY_MEMBERSHIP_STATUS_SUSPENDED + "'  ), 1,0)) as suspendedcards, "
				+ " SUM(if(membership_status = '" + OCConstants.LOYALTY_MEMBERSHIP_STATUS_SUSPENDED + "' ,giftcard_balance,0)) as suspendedamount, "
				+ " SUM(if(membership_status = '" + OCConstants.LOYALTY_MEMBERSHIP_STATUS_SUSPENDED + "' ,loyalty_balance,0)) as suspendedamount "
				+ " FROM contacts_loyalty "+
				 " WHERE user_id="+userId+" AND program_id =" + programId + 
				 " AND membership_status = '" + OCConstants.LOYALTY_MEMBERSHIP_STATUS_ACTIVE + "' "+ subQry;
	
		*/
		List<Object[]> tempList = jdbcTemplate.query(qry, new RowMapper() {
			 public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				 
				 Object[] object =  new Object[3];
				 object[0] = rs.getLong(1);
				 object[1] = f.format(rs.getDouble(2));
				 object[2] = rs.getLong(3);
				 
				return object;
			 }
			
		});
		
		if(tempList != null && tempList.size()>0) {
			return tempList.get(0);
		}
		else
			return null;
		
	}
	public  Object[] getAggregatedLiabilityData(Long userId, Long programId, String storeNo, Long cardsetId,Long tierId) {
		final DecimalFormat f = new DecimalFormat("#0.00");
		String subQry = "";
		if(storeNo != null) {
			subQry += " AND pos_location_id ='" + storeNo +"' ";
		}
		if(cardsetId != null) {
			subQry += " AND card_set_id =" + cardsetId.longValue();
		}
		if(tierId != null) {
			subQry += " AND program_tier_id =" + tierId.longValue();
		}
		
		/*String qry = " SELECT COUNT(loyalty_id), SUM(giftcard_balance), SUM(loyalty_balance) " +
					 " FROM contacts_loyalty "+
					 " WHERE user_id="+userId+" AND program_id =" + programId + 
					 " AND membership_status = '" + OCConstants.LOYALTY_MEMBERSHIP_STATUS_ACTIVE + "' "+ subQry;
		*/
		
		String query = " SELECT SUM(IF((membership_status = '" + OCConstants.LOYALTY_MEMBERSHIP_STATUS_ACTIVE + "'  ), 1,0)) as activecards, "
				+ " SUM(if(membership_status = '" + OCConstants.LOYALTY_MEMBERSHIP_STATUS_ACTIVE + "' ,giftcard_balance,0)) as activeamount, "
				+ " SUM(if(membership_status = '" + OCConstants.LOYALTY_MEMBERSHIP_STATUS_ACTIVE + "' ,loyalty_balance,0)) as activeltyamount ,"
				+"  SUM(IF((membership_status = '" + OCConstants.LOYALTY_MEMBERSHIP_STATUS_EXPIRED + "'  ), 1,0)) as expiredcards, "
				+ " SUM(if(membership_status = '" + OCConstants.LOYALTY_MEMBERSHIP_STATUS_EXPIRED + "' ,giftcard_balance,0)) as expiredamount, "
				+ " SUM(if(membership_status = '" + OCConstants.LOYALTY_MEMBERSHIP_STATUS_EXPIRED + "' ,loyalty_balance,0)) as expiredltyamount, "
				+"  SUM(IF((membership_status = '" + OCConstants.LOYALTY_MEMBERSHIP_STATUS_SUSPENDED + "'  ), 1,0)) as suspendedcards, "
				+ " SUM(if(membership_status = '" + OCConstants.LOYALTY_MEMBERSHIP_STATUS_SUSPENDED + "' ,giftcard_balance,0)) as suspendedamount, "
				+ " SUM(if(membership_status = '" + OCConstants.LOYALTY_MEMBERSHIP_STATUS_SUSPENDED + "' ,loyalty_balance,0)) as suspendedamount "
				+ " FROM contacts_loyalty "+
				 " WHERE user_id="+userId+" AND program_id =" + programId + subQry;
	//" AND membership_status IN ('" + OCConstants.LOYALTY_MEMBERSHIP_STATUS_ACTIVE + "','" + OCConstants.LOYALTY_MEMBERSHIP_STATUS_SUSPENDED+" ' ) "
	
		logger.info("liability query is"+query);
		List<Object[]> tempList = jdbcTemplate.query(query, new RowMapper() {
			 public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				 
				 Object[] object =  new Object[9];
				 object[0] = rs.getLong(1);
				 object[1] = f.format(rs.getDouble(2));
				 object[2] = rs.getLong(3);
				 object[3] = rs.getLong(4);
				 object[4] = rs.getLong(5);
				 object[5] = rs.getLong(6);
				 object[6] = rs.getLong(7);
				 object[7] = rs.getLong(8);
				 object[8] = rs.getLong(9);
				 
				return object;
			 }
			
		});
		
		if(tempList != null && tempList.size()>0) {
			return tempList.get(0);
		}
		else
			return null;
		
	}
	//modify
	public  Object[] getLiabilityDataforSuspended(Long userId, Long programId,Long cardsetId,Long tierId) {
		final DecimalFormat f = new DecimalFormat("#0.00");
		String subQry = "";
		if(cardsetId != null) {
			subQry += " AND card_set_id =" + cardsetId.longValue();
		}
		if(tierId != null) {
			subQry += " AND program_tier_id =" + tierId.longValue();
		}
		
		String qry = " SELECT COUNT(loyalty_id), SUM(giftcard_balance), SUM(loyalty_balance) " +
					 " FROM contacts_loyalty "+
					 " WHERE user_id="+userId+" AND program_id =" + programId + 
					 " AND membership_status = '" + OCConstants.LOYALTY_MEMBERSHIP_STATUS_SUSPENDED + "' "+ subQry;
		
		List<Object[]> tempList = jdbcTemplate.query(qry, new RowMapper() {
			 public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				 
				 Object[] object =  new Object[3];
				 object[0] = rs.getLong(1);
				 object[1] = f.format(rs.getDouble(2));
				 object[2] = rs.getLong(3);
				 
				return object;
			 }
			
		});
		
		if(tempList != null && tempList.size()>0) {
			return tempList.get(0);
		}
		else
			return null;
		
	}
	//modify
	public  Object[] getLiabilityDataforExpired(Long userId, Long programId,Long cardsetId,Long tierId) {
		final DecimalFormat f = new DecimalFormat("#0.00");
		String subQry = "";
		if(cardsetId != null) {
			subQry += " AND card_set_id =" + cardsetId.longValue();
		}
		if(tierId != null) {
			subQry += " AND program_tier_id =" + tierId.longValue();
		}
		
		String qry = " SELECT COUNT(loyalty_id), SUM(giftcard_balance), SUM(loyalty_balance) " +
					 " FROM contacts_loyalty "+
					 " WHERE user_id="+userId+" AND program_id =" + programId + 
					 " AND membership_status = '" + OCConstants.LOYALTY_MEMBERSHIP_STATUS_EXPIRED + "' "+ subQry;
		
		List<Object[]> tempList = jdbcTemplate.query(qry, new RowMapper() {
			 public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				 
				 Object[] object =  new Object[3];
				 object[0] = rs.getLong(1);
				 object[1] = f.format(rs.getDouble(2));
				 object[2] = rs.getLong(3);
				 
				return object;
			 }
			
		});
		
		if(tempList != null && tempList.size()>0) {
			return tempList.get(0);
		}
		else
			return null;
		
	}


	public ContactsLoyalty findByMobilePhone(String mobilePhone,String mobileWithCarrier, Long programId, Long userId){
		
		String query = " FROM ContactsLoyalty WHERE   userId = "+userId + " AND programId = "+programId+" AND rewardFlag IN ('L','GL') "
		+" AND mobilePhone in ( '"+mobilePhone+"', '"+mobileWithCarrier+"') "
				;
		  List<ContactsLoyalty> tempList = getHibernateTemplate().find(query);
		  if(tempList != null && tempList.size() > 0) return tempList.get(0);
		  return null;
		
	}
	
	public ContactsLoyalty findByMembershipNoAndUserId(Long membershipNo,Long mobileWithCarrier, Long programId, Long userId){
		
		String query = " FROM ContactsLoyalty WHERE  userId = "+userId +" AND programId = "+programId+" AND cardNumber in ('"+membershipNo+"', '"+mobileWithCarrier+"')";
		  List<ContactsLoyalty> tempList = getHibernateTemplate().find(query);
		  if(tempList != null && tempList.size() > 0) return tempList.get(0);
		  return null;
		
	}
	
	
	public ContactsLoyalty findByPhoneAndOrgId(String mobilePhone, Long orgId){
		
		String query = " FROM ContactsLoyalty WHERE  orgId = "+orgId+ " AND mobilePhone = '"+mobilePhone+"' " ;
		  List<ContactsLoyalty> tempList = getHibernateTemplate().find(query);
		  if(tempList != null && tempList.size() > 0) return tempList.get(0);
		  return null;
		
	}
	
	public ContactsLoyalty findByProgram(String cardNumber, Long programId, Long userId){
		
		String query = " FROM ContactsLoyalty WHERE userId = "+userId+" AND  programId = "+programId+ " AND cardNumber = '"+cardNumber+"'" ;
		  List<ContactsLoyalty> tempList = getHibernateTemplate().find(query);
		  if(tempList != null && tempList.size() > 0) return tempList.get(0);
		  return null;
		
	}
	/*public ContactsLoyalty findOnlyCard(Long cardNumber, Long programId, Long userId){
		
		String query = " FROM ContactsLoyalty WHERE userId = "+userId +" AND programId = "+programId+
				" AND membershipType='"+OCConstants.LOYALTY_MEMBERSHIP_TYPE_CARD+"' AND cardNumber = "+cardNumber ;
		  List<ContactsLoyalty> tempList = getHibernateTemplate().find(query);
		  if(tempList != null && tempList.size() > 0) return tempList.get(0);
		  return null;
		
	}*/
	public ContactsLoyalty findMembership(Long mobilePhone, String programIdStr, Long userId){
		
		String query = " FROM ContactsLoyalty WHERE  userId = "+userId+"  AND programId IN ("+programIdStr+") AND cardNumber = '"+mobilePhone+"'" ;
		  List<ContactsLoyalty> tempList = getHibernateTemplate().find(query);
		  if(tempList != null && tempList.size() > 0) return tempList.get(0);
		  return null;
	}
	
	public ContactsLoyalty findMembershipByPhone(Long mobilePhone, Long userId){
		
		String query = " FROM ContactsLoyalty WHERE  userId = "+userId+" AND  cardNumber = '"+mobilePhone+"'";
		  List<ContactsLoyalty> tempList = getHibernateTemplate().find(query);
		  if(tempList != null && tempList.size() > 0) return tempList.get(0);
		  return null;
		
	}
	
	public ContactsLoyalty findMembershipByPhone(Long mobilePhone, String membershipType, Long userId){
		
		  //String query = " FROM ContactsLoyalty WHERE userId = "+userId+" AND cardNumber = "+mobilePhone+" AND membershipType = '"+membershipType+"'";
		  String query = " FROM ContactsLoyalty WHERE userId = "+userId+" AND cardNumber = '"+mobilePhone+"' AND membershipType = '"+membershipType+"'";//APP-1208
		  List<ContactsLoyalty> tempList = getHibernateTemplate().find(query);
		  if(tempList != null && tempList.size() > 0) return tempList.get(0);
		  return null;
		
	}
	
	public List<ContactsLoyalty> findMembershipByMobile(String mobilePhone, Long userId){
		
		String query = " FROM ContactsLoyalty WHERE  userId = "+userId+" AND serviceType = '"+OCConstants.LOYALTY_SERVICE_TYPE_OC+"' AND mobilePhone = '"+mobilePhone+"'" 
				+ " AND rewardFlag IN ('"+OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L+
				"','"+OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_GL+"')"  ; 
		  List<ContactsLoyalty> tempList = getHibernateTemplate().find(query);
		  if(tempList != null && tempList.size() > 0) return tempList;
		  return null;
	}

	public List<ContactsLoyalty> findMembershipByEmail(String Email, Long userId){
		
		
		  String query="select cl FROM Contacts c ,ContactsLoyalty cl where c.contactId = cl.contact and c.users="+userId+" AND cl.userId="+userId+" and c.emailId='"+Email+"' ";
		  logger.info("query====>"+query);
		  List<ContactsLoyalty> tempList = executeQuery(query);
		  if(tempList != null && tempList.size() > 0) return tempList;
		  return null;
	
	}
	public List<ContactsLoyalty> findMembershipByCustomerId(String Email, Long userId){
		
		
		  String query="FROM ContactsLoyalty  where userId="+userId+" and customerId='"+Email+"' ";
		  logger.info("query====>"+query);
		  List<ContactsLoyalty> tempList = executeQuery(query);
		  if(tempList != null && tempList.size() > 0) return tempList;
		  return null;
	
	}
	public ContactsLoyalty findMembershipByEmailInCl(String Email, Long userId,Long programId ){
		
		
		  String query="FROM ContactsLoyalty  where userId="+userId+"  AND programId = "+programId+" and emailId='"+Email+"' ";
		  logger.info("query====>"+query);
		  List<ContactsLoyalty> tempList = executeQuery(query);
		  if(tempList != null && tempList.size() > 0) return tempList.get(0);
		  return null;
	
	}
	
	public List<ContactsLoyalty> findMembershipByEmailORPhone(String Email, String phone, Users user){
		
		String subQry = Constants.STRING_NILL;
	
	/*	if(phone.length()==12) {
			
			phone=phone.substring(2);
		}*/
		
		if(phone != null && !phone.isEmpty()){
			String countryCarrier =user.getCountryCarrier()!=null?user.getCountryCarrier()+"":"" ;//APP-2208
			
			int i=countryCarrier.length();
			
			logger.info("phone "+phone);
			if(user.getCountryType().equals(Constants.SMS_COUNTRY_US) && phone.startsWith(countryCarrier)) {
				 
				logger.info("entering phone length"+i);

				logger.info("entering phone Z+");
				phone=phone.substring(i);
		
			}
			subQry = " AND (cardNumber='"+phone+"' OR (mobilePhone LIKE '%"+phone+"'))"; 
		}
		else if(Email != null && !Email.isEmpty()){
			subQry = " AND emailId ='"+Email+"'"; 
		}
		String query = " FROM ContactsLoyalty WHERE  userId = "+user.getUserId()+subQry +" AND serviceType = '"+OCConstants.LOYALTY_SERVICE_TYPE_OC+"'" 
				+ " AND rewardFlag IN ('"+OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L+
				"','"+OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_GL+"')"  ; 
		
		boolean isIndia = user.getCountryType().equals(Constants.SMS_COUNTRY_INDIA);//APP-4080 query tune up
		logger.info("isIndia && user countryType"+isIndia+" "+user.getCountryType());
		if(isIndia) query = " FROM ContactsLoyalty  WHERE userId="+user.getUserId() +" AND cardNumber='"+phone+"' "
				+ " AND rewardFlag IN ('"+OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L+
				"','"+OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_GL+"')"  ;
		
		  List<ContactsLoyalty> tempList = executeQuery(query, 0,1);
		  logger.info("req query =>"+query);
		  if(tempList != null && tempList.size() > 0) {
			  return tempList;
		  }else {
			  query = " FROM ContactsLoyalty  WHERE userId="+user.getUserId() +" AND mobilePhone='"+phone+"' "
						+ " AND rewardFlag IN ('"+OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L+
						"','"+OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_GL+"')"   ;
			  tempList = executeQuery(query, 0,1); //APP-4105
			  logger.info("req query =>"+query);
			  if(tempList != null && tempList.size() > 0) {

				  return tempList;
			  }else {				  
				  return null;
			  }
		  }
		/*  JdbcResultsetHandler jdbcResultsetHandler = new JdbcResultsetHandler();
		  String query="select cl.* FROM contacts ,contacts_loyalty cl ON c.cid = cl.contact_id where c.user_id='"+userId+"' and c.email_id='"+Email+"' ";
		  jdbcResultsetHandler.executeStmt(query);
		  ResultSet resultSet = jdbcResultsetHandler.getResultSet();;
		  return resultSet;
		*/  
	}
	
public List<ContactsLoyalty> findmembershipByEmailORPhone(String Email, String phone, Users user){
		
		String subQry = Constants.STRING_NILL;
		Long userId=user.getUserId();
		String countryCarrier =user.getCountryCarrier()!=null?user.getCountryCarrier()+"":"" ;//APP-2208
		
		int i=countryCarrier.length();
		
		logger.info("phone "+phone);

		if(phone.startsWith(countryCarrier)) {
			 
			logger.info("entering phone length"+i);

			logger.info("entering phone Z+");
			phone=phone.substring(i);
	
		}
		
		if(phone != null && !phone.isEmpty()){
			
			subQry = " AND (cardNumber = '"+phone+"' OR(mobilePhone LIKE '%"+phone+"'))"; 
		}
		else if(Email != null && !Email.isEmpty()){
			subQry = " AND emailId ='"+Email+"'"; 
		}
		 
		String query = " FROM ContactsLoyalty WHERE  userId = "+userId+subQry+" AND serviceType = '"+OCConstants.LOYALTY_SERVICE_TYPE_OC+"'" 
				+ " AND rewardFlag IN ('"+OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L+
				"','"+OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_GL+"')"   ; 
		
		boolean isIndia = user.getCountryType().equals(Constants.SMS_COUNTRY_INDIA); //APP-4080 query tune up
		logger.info("isIndia && user countryType"+isIndia+" "+user.getCountryType());
		
		if(isIndia) query = " FROM ContactsLoyalty  WHERE userId="+user.getUserId() +" AND cardNumber='"+phone+"' "
				+ " AND rewardFlag IN ('"+OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L+
				"','"+OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_GL+"')"   ;
		
		  List<ContactsLoyalty> tempList = executeQuery(query, 0,1);
		  logger.info("req query =>"+query);
		  if(tempList != null && tempList.size() > 0) {

			  return tempList;}
			  
			  else {  
				  query = " FROM ContactsLoyalty  WHERE userId="+user.getUserId() +" AND mobilePhone='"+phone+"' "
							+ " AND rewardFlag IN ('"+OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L+
							"','"+OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_GL+"')"   ;
				  tempList = executeQuery(query, 0,1);//APP-4105
				  logger.info("req query =>"+query);
				  if(tempList != null && tempList.size() > 0) {
					  
					  return tempList;
					  
			  }else { return null; }
			  }
		/*  JdbcResultsetHandler jdbcResultsetHandler = new JdbcResultsetHandler();
		  String query="select cl.* FROM contacts ,contacts_loyalty cl ON c.cid = cl.contact_id where c.user_id='"+userId+"' and c.email_id='"+Email+"' ";
		  jdbcResultsetHandler.executeStmt(query);
		  ResultSet resultSet = jdbcResultsetHandler.getResultSet();;
		  return resultSet;
		*/  
	}
	
	
	
	
	
	
	
	
	
	
	
	public List<Object[]> getEnrollmentCountByOptinMedium(Long prgmId, Long userId,
			String optInType, String startDateStr, String endDateStr) {
		
		
		String query = "SELECT COUNT(loyaltyId),contactLoyaltyType FROM  ContactsLoyalty " +
				" WHERE userId=" + userId.longValue() + " AND programId = " + prgmId.longValue() +
				" AND contactLoyaltyType in("+ optInType+  ") " +
				" AND createdDate BETWEEN '"+startDateStr+"' AND '"+endDateStr+"' group by contactLoyaltyType " ;

		List<Object[]> tempList = executeQuery(query);//getHibernateTemplate().find(query);

		/*if(tempList != null && tempList.size()>0)
			return ((Long) tempList.get(0)).intValue();
		else*/
			return tempList;
	}

	/*public List<Object[]> getStoreContactLtyList(Long prgmId, Long userId, String startDate, String endDate,boolean isMobile, String type) {
		
		String apndQry = "";
		if(!isMobile) {
			if(type.equalsIgnoreCase("loyalty")) {
				apndQry = "AND  (rewardFlag='"+OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L+"' OR rewardFlag='"+OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_GL+"')";
			}else {
				apndQry = "AND rewardFlag='"+OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_G+"'";
			}
		}
		
		String query = "SELECT posStoreLocationId,COUNT(loyaltyId) FROM ContactsLoyalty " +
				       "WHERE userId = "+userId+" AND programId = "+prgmId +apndQry+
						" AND  created_date BETWEEN '" + startDate + "' AND '"+ endDate +"' AND posStoreLocationId IS NOT NULL GROUP BY posStoreLocationId";
		  List<Object[]> tempList = getHibernateTemplate().find(query);
		  if(tempList != null && tempList.size() > 0) return tempList;
		  return null;
	}*/
	
public List<Object[]> getStoreContactLtyList(Long prgmId, Long userId, String startDate, String endDate,boolean isMobile, String type) {
		
		String apndQry = "";
		if(!isMobile) {
			if(type.equalsIgnoreCase("loyalty")) {
				apndQry = "AND  (rewardFlag='"+OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L+"' OR rewardFlag='"+OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_GL+"')";
			}else {
				apndQry = "AND rewardFlag='"+OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_G+"'";
			}
		}
		
		String query = "SELECT posStoreLocationId,COUNT(loyaltyId),subsidiaryNumber FROM ContactsLoyalty " +
				       "WHERE userId = "+userId+" AND programId = "+prgmId +apndQry+
						" AND  created_date BETWEEN '" + startDate + "' AND '"+ endDate +"' AND posStoreLocationId IS NOT NULL AND subsidiaryNumber is NOT NULL GROUP BY posStoreLocationId, subsidiaryNumber";
		  List<Object[]> tempList = getHibernateTemplate().find(query);
		  if(tempList != null && tempList.size() > 0) return tempList;
		  return null;
	}
	
	public int getCustomersCountByTierId(Long userId, Long tierId) {
		String query = "SELECT COUNT(loyaltyId) FROM  ContactsLoyalty " +
				" WHERE userId=" + userId.longValue() + " AND programTierId = " + tierId.longValue() ;

		List tempList = getHibernateTemplate().find(query);

		if(tempList != null && tempList.size()>0)
			return ((Long) tempList.get(0)).intValue();
		else
			return 0;
	}

	public List<ContactsLoyalty> getCustomersByTierId(Long userId, Long tierId) {
		String query = " FROM  ContactsLoyalty " +
				" WHERE userId=" + userId.longValue() + " AND programTierId = " + tierId.longValue() ;
		
		List<ContactsLoyalty> tempList = getHibernateTemplate().find(query);
		if(tempList != null && tempList.size() > 0){
			return tempList;
		}
		return null;
	}
    /**
	 * 
	 * @param membershipNo
	 * @param userId
	 * @return
	 */
	
	public ContactsLoyalty findByMembershipNoAndUserId(Long membershipNo , Long userId){
		
		String query = " FROM ContactsLoyalty WHERE  userId = "+userId + " AND cardNumber = '"+membershipNo+"'   AND serviceType= '"+OCConstants.LOYALTY_SERVICE_TYPE_OC+"'";
		
		logger.info("query"+query);
		  List<ContactsLoyalty> tempList = getHibernateTemplate().find(query);
		  if(tempList != null && tempList.size() > 0) return tempList.get(0);
		  return null;
	}//findByMembershipNoAndUserId
	
public ContactsLoyalty findBy(String membershipNo , Long userId){
		
		String query = " FROM ContactsLoyalty WHERE  userId = "+userId + " AND cardNumber = '"+membershipNo+"'   AND serviceType= '"+OCConstants.LOYALTY_SERVICE_TYPE_OC+"'";
		
		logger.info("query"+query);
		  List<ContactsLoyalty> tempList = getHibernateTemplate().find(query);
		  if(tempList != null && tempList.size() > 0) return tempList.get(0);
		  return null;
	}//findByMembershipNoAndUserId
	
	//Changes 2.5.4.0 start
public ContactsLoyalty findByMembershipNoAndUserIdStrictly(Long membershipNo , Long userId){
		
		String query = " FROM ContactsLoyalty WHERE  userId = "+userId + " AND cardNumber = '"+membershipNo+"' ";
		
		logger.info("query"+query);
		  List<ContactsLoyalty> tempList = getHibernateTemplate().find(query);
		  if(tempList != null && tempList.size() > 0) return tempList.get(0);
		  return null;
	}
//Changes 2.5.4.0 end
	
	/**
	 * 
	 * @param fullName
	 * @param userId
	 * @return
	 *//*
	public  List<Object[]>  findContactLtyBySearch(Long userId,String searchByKey,String searchByValue){
		
		String searchByQry ="";
		
		String query = "";
		
		
		if(searchByKey!=null && searchByValue!=null) {
			if(	searchByKey.equalsIgnoreCase("email_id")) {
				searchByQry = " AND cont.email_id = '"+searchByValue+"'";
			}
			else if(searchByKey.equalsIgnoreCase("mobile_phone")) {
				searchByQry = " AND cont.mobile_phone = '"+searchByValue+"' ";
			} 
			else if(searchByKey.equalsIgnoreCase("fullName")){
				 String [] strarr = searchByValue.split(" ");
				System.out.println("searchValsearchVal:"+searchByValue+":\t Length "+strarr.length +"");
				
				System.out.println("fullName "+strarr.length);
				if(strarr.length == 2){
					searchByQry = " AND cont.first_name ='"+strarr[0]+"' AND cont.last_name= '"+strarr[1]+"'";
				}
			}//full name
			else if(searchByKey.equalsIgnoreCase("first_name")){
					
					searchByQry = " AND cont.first_name ='"+searchByValue+"'";
				}//f name
			else if(searchByKey.equalsIgnoreCase("last_name")){
					searchByQry =  " AND cont.last_name ='"+searchByValue+"'";
				
				
			}//last name
			
		}//searchByQry
		
		query = " Select clty.loyalty_id,clty.contact_id,clty.card_number,clty.program_id,clty.card_set_id "
				+ " FROM contacts_loyalty clty,contacts cont "
				+ "where clty.user_id="+userId+" AND clty.service_type ='"+OCConstants.LOYALTY_SERVICE_TYPE_OC+"' AND clty.contact_id=cont.cid  "+searchByQry;
	
		logger.info("query"+query);
		  List<Object[]> tempList = jdbcTemplate.query(query, new RowMapper() {
				 public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
					 
					 Object[] object =  new Object[5];
					 object[0] = rs.getLong(1); //loyalty_id	
					 object[1] = rs.getLong(2); //contact_id	 
					 object[2] = rs.getLong(3); //card_number
					 object[3] = rs.getLong(4); //program_id
					 object[4] = rs.getLong(5); //card_set_id	
					 return object;
				 }
				
			});
		  if(tempList != null && tempList.size() > 0) 
			  return tempList;
		  else
			  return null;
	}//findContactLtyByFullName
*/	
	
	/**
	 * This method get's ContactLoyalty List from Persistent Storage.
	 * @param fullName
	 * @param userId
	 * @return contactLoyaltyList
	 */
	public   List<Map<String, Object>>  findContactLtyBySearchCriteria(Users user,String searchByKey,String searchByValue){
		logger.debug(">>>>>>>>>>>>> entered in findContactLtyBySearchCriteria");
		String searchByQry ="";
		String query = "";
		if(searchByKey!=null && searchByValue!=null) {
			searchByValue = StringEscapeUtils.escapeSql(searchByValue);
			logger.info("Search value::"+searchByValue+ " "+searchByKey);
			if(	searchByKey.equalsIgnoreCase("email_id")) {
				searchByQry = " AND cont.email_id = '"+searchByValue+"'"; 
			}
			else if(searchByKey.equalsIgnoreCase("mobile_phone")) {
				
				boolean isIndia = user.getCountryType().equals(Constants.SMS_COUNTRY_INDIA);
				logger.info("isIndia flag"+isIndia);
				if(isIndia) {
				searchByQry = " AND cont.mobile_phone = '"+searchByValue+"' ";
				}else {
				searchByQry = " AND cont.mobile_phone like '%"+searchByValue+"' ";
				}
			
			} 
			else if(searchByKey.equalsIgnoreCase("fullName")){
				 String [] strarr = searchByValue.split(",");
			//	 String [] strarr = searchByValue.split(",");// 
				if(strarr.length == 2){
					//searchByQry = " AND cont.first_name ='"+strarr[0]+"' AND cont.last_name= '"+strarr[1]+"'";
					searchByQry = " AND cont.first_name like '"+strarr[0]+"%' AND cont.last_name like '"+strarr[1]+"%'";
				}
			}//full name
			else if(searchByKey.equalsIgnoreCase("first_name")){
					//searchByQry = " AND cont.first_name ='"+searchByValue+"'";
					searchByQry = " AND cont.first_name like '"+searchByValue+"%'";
				}//f name
			else if(searchByKey.equalsIgnoreCase("last_name")){
					//searchByQry =  " AND cont.last_name ='"+searchByValue+"'";
					searchByQry =  " AND cont.last_name like '"+searchByValue+"%'";
			}//last name
			 
		}//searchByQry 
		 if(!searchByQry.isEmpty())   {
			 
			 query = " Select clty.loyalty_id,clty.contact_id,clty.card_number,clty.program_id,clty.card_set_id,clty.customer_id,clty.giftcard_balance,t.tier_name,clty.loyalty_balance,clty.pos_location_id "
					 + " FROM contacts_loyalty clty,contacts cont,loyalty_program_tier t "
					 + "where cont.user_id="+user.getUserId()+" AND clty.user_id="+user.getUserId()+" AND clty.contact_id=cont.cid and clty.program_id=t.program_id and clty.program_tier_id=t.tier_id AND clty.service_type ='"+OCConstants.LOYALTY_SERVICE_TYPE_OC+"'"+ searchByQry;
			 
			 logger.info("query"+query);
			 logger.debug("<<<<<<<<<<<<< completed findContactLtyBySearchCriteria ");
			 return jdbcTemplate.queryForList(query);
		 }
		 
		 return null;
		
	}//findContactLtyBySearchCriteria
	
	
	
	public long getCountOfLtyCustomersToExpireByCreatedDateForComm(Long userId, Long programID, Long tierId, String currentDateStr, char resetFlag) {
		try{
			
			String appQry = "";
			if(resetFlag == OCConstants.FLAG_YES){
				appQry = " AND tier_upgraded_date IS NULL " ;
			}
			
			String query =  "SELECT Count(cl.loyalty_id) FROM  contacts_loyalty cl, contacts c " +
					" WHERE c.user_id="+userId+" AND cl.user_id="+userId+" "
					+ "AND c.cid=cl.contact_id AND (c.email_id IS NOT NULL OR c.mobile_phone IS NOT NULL) AND program_id = " + programID+
					" AND program_tier_id = " + tierId.longValue() +
					" AND (reward_flag = '" + OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L + "' OR reward_flag = '" + OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_GL+ "') " +
					" AND membership_status = '" +OCConstants.LOYALTY_MEMBERSHIP_STATUS_ACTIVE+ "' " + appQry +
					" AND STR_TO_DATE(CONCAT(YEAR(cl.created_date),'-', MONTH(cl.created_date)), '%Y-%m') <= STR_TO_DATE('"+currentDateStr+"', '%Y-%m') ";
			
			logger.info("Exp Date query ::"+query);
			
			return jdbcTemplate.queryForLong(query);
		} catch (DataAccessException e) {
			return 0;
		}catch(Exception e){
			return 0;
		}
	}
	
//modify
	public List<Object[]> getLtyCustomersToExpireByCreatedDateForComm(Long userId, Long programID, Long tierId, String currentDateStr, char resetFlag, int startFrom, int maxRecord) {
		
		String appQry = "";
		if(resetFlag == OCConstants.FLAG_YES){
			appQry = " AND tier_upgraded_date IS NULL " ;
		}
		
		String query = "SELECT cl.loyalty_id, cl.user_id, cl.contact_id, cl.card_number, c.email_id, c.mobile_phone FROM "
				+ " contacts_loyalty cl, contacts c " +
				" WHERE c.user_id="+userId+" AND cl.user_id="+userId+" "
				+ "AND c.cid=cl.contact_id AND (c.email_id IS NOT NULL OR c.mobile_phone IS NOT NULL) AND "
				+ "program_id = " + programID +" AND program_tier_id = " + tierId.longValue() +
				" AND (reward_flag = '" + OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L + "' OR reward_flag = '" + OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_GL+ "') " +
				" AND membership_status = '" +OCConstants.LOYALTY_MEMBERSHIP_STATUS_ACTIVE+ "' " + appQry +
				" AND STR_TO_DATE(CONCAT(YEAR(cl.created_date),'-', MONTH(cl.created_date)), '%Y-%m') <= STR_TO_DATE('"+currentDateStr+"', '%Y-%m') limit "+startFrom+","+maxRecord;
		logger.info("Exp Date query ::"+query);
		List<Object[]> tempList = jdbcTemplate.query(query, new RowMapper() {
			@Override
			public Object[] mapRow(ResultSet rs, int rowNum) throws SQLException {
				
				Object[] objArr = new Object[6];
				objArr[0] = rs.getLong("cl.loyalty_id");
				objArr[1] = rs.getLong("cl.user_id");
				objArr[2] = rs.getLong("cl.contact_id");
				objArr[3] = rs.getString("cl.card_number");
				objArr[4] = rs.getString("c.email_id");
				objArr[5] = rs.getString("c.mobile_phone");
				return objArr;
			}
		});
		if(tempList != null && tempList.size() > 0){
			return tempList;
		}
		return null;
	}
	//modify
	public List<Object[]> getLtyCustomersToExpireByUpgradedDateForComm(Long userId,Long programID, Long tierId, String currentDateStr, int startFrom, int maxrecord) {
		
		String query = "SELECT cl.loyalty_id, cl.user_id, cl.contact_id, cl.card_number, c.email_id, c.mobile_phone FROM "
				+ " contacts_loyalty cl, contacts c " +
				" WHERE c.user_id="+userId+" AND cl.user_id="+userId+" "
				+ "AND c.cid=cl.contact_id AND (c.email_id IS NOT NULL OR c.mobile_phone IS NOT NULL) AND program_id = " + programID +" AND program_tier_id = " + tierId.longValue() +
				" AND (reward_flag = '" + OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L + "' OR reward_flag = '" + OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_GL+ "') " +
				" AND membership_status = '" +OCConstants.LOYALTY_MEMBERSHIP_STATUS_ACTIVE+ "' " +
				" AND if(tier_upgraded_date IS NOT NULL , STR_TO_DATE(CONCAT(YEAR(tier_upgraded_date),'-', MONTH(tier_upgraded_date)), '%Y-%m') "
				+ " <= STR_TO_DATE('"+currentDateStr+"', '%Y-%m'),STR_TO_DATE(CONCAT(YEAR(cl.created_date),'-', MONTH(cl.created_date)), '%Y-%m') "
						+ "<= STR_TO_DATE('"+currentDateStr+"', '%Y-%m') ) limit "+startFrom+","+maxrecord ;
		logger.info("query ==="+query);	
		List<Object[]> tempList = jdbcTemplate.query(query, new RowMapper() {
			@Override
			public Object[] mapRow(ResultSet rs, int rowNum) throws SQLException {
				
				Object[] objArr = new Object[6];
				objArr[0] = rs.getLong("cl.loyalty_id");
				objArr[1] = rs.getLong("cl.user_id");
				objArr[2] = rs.getLong("cl.contact_id");
				objArr[3] = rs.getString("cl.card_number");
				objArr[4] = rs.getString("c.email_id");
				objArr[5] = rs.getString("c.mobile_phone");
				return objArr;
			}
		});
		if(tempList != null && tempList.size() > 0){
			return tempList;
		}
		return null;
	}
	
	
public long getCountOfLtyCustomersToExpireByUpgradedDateForComm(Long userId,Long programID, Long tierId, String currentDateStr) {
		
		try {
			String query = "SELECT Count(cl.loyalty_id) FROM  contacts_loyalty cl, contacts c " +
					" WHERE c.user_id="+userId+" AND cl.user_id="+userId+" "
					+ "AND c.cid=cl.contact_id AND (c.email_id IS NOT NULL OR c.mobile_phone IS NOT NULL) AND program_id = " + programID+
					" AND program_tier_id = " + tierId.longValue() +
					" AND (reward_flag = '" + OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L + "' OR reward_flag = '" + OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_GL+ "') " +
					" AND membership_status = '" +OCConstants.LOYALTY_MEMBERSHIP_STATUS_ACTIVE+ "' " +
					" AND if(tier_upgraded_date IS NOT NULL , STR_TO_DATE(CONCAT(YEAR(tier_upgraded_date),'-', MONTH(tier_upgraded_date)), '%Y-%m') "
							+ " <= STR_TO_DATE('"+currentDateStr+"', '%Y-%m'),STR_TO_DATE(CONCAT(YEAR(cl.created_date),'-', MONTH(cl.created_date)), '%Y-%m') "
									+ "<= STR_TO_DATE('"+currentDateStr+"', '%Y-%m') )" ;
					logger.info("query ==="+query);		
			return jdbcTemplate.queryForLong(query);
		} catch (DataAccessException e) {
			return 0;
		}catch(Exception e){
			return 0;
		}
	}
	
	/*public long getCountOfGiftCustomersToExpire(Long userId, Long programId, String currentDateStr) {
		
		
		try {
			String query = "SELECT Count(loyalty_id) FROM  contacts_loyalty " +
					" WHERE user_id="+userId+" AND program_id = " + programId.longValue() +
					" AND reward_flag = '" + OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_G + "'"	+
					" AND membership_status = '" +OCConstants.LOYALTY_MEMBERSHIP_STATUS_ACTIVE+ "' " +
					" AND CONCAT(YEAR(created_date),'-', MONTH(created_date)) <= '"+currentDateStr+"' ";
			logger.info("query ==="+query);	
			return jdbcTemplate.queryForLong(query);
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			return 0;
		}catch(Exception e){
			return 0;
		}
		
	}*/
	
	public long getCountOfGiftCustomersToExpireForComm(Long userId, Long programId, String currentDateStr) {
	
	
		try {
			String query = "SELECT Count(cl.loyalty_id) FROM  contacts_loyalty " +
					 " contacts_loyalty cl, contacts c " +
					" WHERE c.user_id="+userId+" AND cl.user_id="+userId+" "
					+ "AND c.cid=cl.contact_id AND (c.email_id IS NOT NULL OR c.mobile_phone IS NOT NULL) AND program_id = " + programId.longValue() +
					" AND reward_flag = '" + OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_G + "'"	+
					" AND membership_status = '" +OCConstants.LOYALTY_MEMBERSHIP_STATUS_ACTIVE+ "' " +
					" AND CONCAT(YEAR(cl.created_date),'-', MONTH(cl.created_date)) <= '"+currentDateStr+"' ";
			logger.info("query ==="+query);	
			return jdbcTemplate.queryForLong(query);
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			return 0;
		}catch(Exception e){
			return 0;
		}
	
	}
	
	//modify
	public List<Object[]> getGiftCustomersToExpireForComm(Long userId, Long programId, String currentDateStr, int startFrom, int maxRecords) {
		
		/*String query = "SELECT loyalty_id, user_id, contact_id, card_number FROM  contacts_loyalty " +
				" WHERE user_id="+userId+" AND program_id = " + programId.longValue() +
				" AND reward_flag = '" + OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_G + "'"	+
				" AND membership_status = '" +OCConstants.LOYALTY_MEMBERSHIP_STATUS_ACTIVE+ "' " +
				" AND CONCAT(YEAR(created_date),'-', MONTH(created_date)) <= '"+currentDateStr+"' limit  "+startFrom+","+maxRecords;*/
		String query = "SELECT cl.loyalty_id, cl.user_id, cl.contact_id, cl.card_number, c.email_id, c.mobile_phone FROM "
				+ " contacts_loyalty cl, contacts c " +
				" WHERE c.user_id="+userId+" AND cl.user_id="+userId+" "
				+ "AND c.cid=cl.contact_id AND (c.email_id IS NOT NULL OR c.mobile_phone IS NOT NULL) AND program_id = " + programId.longValue() +
				" AND reward_flag = '" + OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_G + "'"	+
				" AND membership_status = '" +OCConstants.LOYALTY_MEMBERSHIP_STATUS_ACTIVE+ "' " +
				" AND CONCAT(YEAR(cl.created_date),'-', MONTH(cl.created_date)) <= '"+currentDateStr+"' limit  "+startFrom+","+maxRecords;
		logger.info("query ==="+query);	
		List<Object[]> tempList = jdbcTemplate.query(query, new RowMapper() {
			@Override
			public Object[] mapRow(ResultSet rs, int rowNum) throws SQLException {
				
				Object[] objArr = new Object[6];
				objArr[0] = rs.getLong("cl.loyalty_id");
				objArr[1] = rs.getLong("cl.user_id");
				objArr[2] = rs.getLong("cl.contact_id");
				objArr[3] = rs.getString("card_number");
				objArr[4] = rs.getString("c.email_id");
				objArr[5] = rs.getString("c.mobile_phone");
				return objArr;
			}
		});
		if(tempList != null && tempList.size() > 0){
			return tempList;
		}
		return null;
	}
public List<Object[]> getGiftCustomersToExpire(Long userId, Long programId, String currentDateStr, int startFrom, int maxRecords) {
		
		String query = "SELECT loyalty_id, user_id, contact_id, card_number FROM  contacts_loyalty " +
				" WHERE user_id="+userId+" AND program_id = " + programId.longValue() +
				" AND reward_flag = '" + OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_G + "'"	+
				" AND membership_status = '" +OCConstants.LOYALTY_MEMBERSHIP_STATUS_ACTIVE+ "' " +
				" AND CONCAT(YEAR(created_date),'-', MONTH(created_date)) <= '"+currentDateStr+"' limit  "+startFrom+","+maxRecords;
		/*String query = "SELECT cl.loyalty_id, cl.user_id, cl.contact_id, cl.card_number, c.email_id, c.mobile_phone FROM "
				+ " contacts_loyalty cl, contacts c " +
				" WHERE c.user_id="+userId+" AND cl.user_id="+userId+" "
				+ "AND c.cid=cl.contact_id AND (c.email_id IS NOT NULL OR c.mobile_phone IS NOT NULL) AND program_id = " + programId.longValue() +
				" AND reward_flag = '" + OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_G + "'"	+
				" AND membership_status = '" +OCConstants.LOYALTY_MEMBERSHIP_STATUS_ACTIVE+ "' " +
				" AND CONCAT(YEAR(created_date),'-', MONTH(created_date)) <= '"+currentDateStr+"' limit  "+startFrom+","+maxRecords;*/
		logger.info("query ==="+query);	
		List<Object[]> tempList = jdbcTemplate.query(query, new RowMapper() {
			@Override
			public Object[] mapRow(ResultSet rs, int rowNum) throws SQLException {
				
				Object[] objArr = new Object[6];
				objArr[0] = rs.getLong("loyalty_id");
				objArr[1] = rs.getLong("user_id");
				objArr[2] = rs.getLong("contact_id");
				objArr[3] = rs.getString("card_number");
				return objArr;
			}
		});
		if(tempList != null && tempList.size() > 0){
			return tempList;
		}
		return null;
	}

public ContactsLoyalty findBy(Users user, String emailID, String mobilePhone ){
	
	List<ContactsLoyalty> loyaltyList = null;
	try {
		String queryStr = null;
		String subQry = "";
		
		
		
		if(mobilePhone != null && !mobilePhone.isEmpty()){
			
			subQry += " (cardNumber='"+mobilePhone+"' OR (mobilePhone IS NOT NULL AND mobilePhone like '%"+mobilePhone+"'))";
			
		}
		if(emailID != null && !emailID.isEmpty()){
			if(subQry.length() > 0) subQry += " OR ";
			subQry += " (emailId IS NOT NULL AND emailId='"+emailID+"') ";
		}
		
		if(subQry.isEmpty()) return null;
		
		
				
		queryStr = " FROM ContactsLoyalty  WHERE userId="+user.getUserId() +" AND ("+subQry+")"
						+ " AND serviceType = '"+OCConstants.LOYALTY_SERVICE_TYPE_OC+"'"
				+ " AND membershipStatus = '"+OCConstants.LOYALTY_MEMBERSHIP_STATUS_ACTIVE+"'";//  ORDER BY cummulativePurchaseValue DESC ";
		
		boolean isIndia = user.getCountryType().equals(Constants.SMS_COUNTRY_INDIA);//APP-4080 query tune up
		logger.info("isIndia && user countryType"+isIndia+" "+user.getCountryType());
		if(isIndia) queryStr = " FROM ContactsLoyalty  WHERE userId="+user.getUserId() +" AND cardNumber='"+mobilePhone+"' "
				+ " AND membershipStatus = '"+OCConstants.LOYALTY_MEMBERSHIP_STATUS_ACTIVE+"' ";
		
		logger.info(queryStr);
		/*Query query = getSession().createQuery(queryStr);
		query.setFirstResult(currentSizeInt);
		query.setMaxResults(offset);*/
		
		//return (List<ContactsLoyalty>)query.list();
		
		loyaltyList = executeQuery(queryStr, 0,1);//just needed limit 1
		
		if(loyaltyList != null && !loyaltyList.isEmpty()){
			return loyaltyList.get(0);
			
		}else {
			
			queryStr = " FROM ContactsLoyalty  WHERE userId="+user.getUserId() +" AND mobilePhone='"+mobilePhone+"' "
					+ " AND membershipStatus = '"+OCConstants.LOYALTY_MEMBERSHIP_STATUS_ACTIVE+"' ";
			loyaltyList = executeQuery(queryStr, 0,1);//just needed limit 1
			
			if(loyaltyList != null && !loyaltyList.isEmpty()){ //APP-4105
				return loyaltyList.get(0);
				
			}else { return null;}
			
		}
		
	} catch (Exception e) {
		// TODO Auto-generated catch block
		logger.error("Exception ", e);
	}
	return null;

}
	


public ContactsLoyalty findBydate(Long userId, String emailID, String mobilePhone,String date ){
	
	List<ContactsLoyalty> loyaltyList = null;
	try {
		String queryStr = null;
		String subQry = "";
		
		if(emailID != null && !emailID.isEmpty()){
			subQry += "(emailId IS NOT NULL AND emailId='"+emailID+"')";
		}
		
		if(mobilePhone != null && !mobilePhone.isEmpty()){
			if(subQry.length() > 0) subQry += "OR";
			
			subQry += "(mobilePhone IS NOT NULL AND mobilePhone like '%"+mobilePhone+"')";
			
		}
		
		if(subQry.isEmpty()) return null;
		
		
				
		queryStr = " FROM ContactsLoyalty  WHERE userId="+userId
						+ " AND serviceType = '"+OCConstants.LOYALTY_SERVICE_TYPE_OC+"'"
				+ " AND membershipStatus = '"+OCConstants.LOYALTY_MEMBERSHIP_STATUS_ACTIVE+"' AND createdDate like  '"+date+"%' AND ("+subQry+") ORDER BY cummulativePurchaseValue DESC ";
		logger.info(queryStr);
		/*Query query = getSession().createQuery(queryStr);
		query.setFirstResult(currentSizeInt);
		query.setMaxResults(offset);*/
		
		//return (List<ContactsLoyalty>)query.list();
		
		loyaltyList = executeQuery(queryStr);
		
		if(loyaltyList != null && !loyaltyList.isEmpty()){
			return loyaltyList.get(0);
			
		}
		
		return null;
		
	} catch (Exception e) {
		// TODO Auto-generated catch block
		logger.error("Exception ", e);
	}
	return null;

}
	
			
		
	//modify
	public List<Object[]> getAllOCMembershipsForComm(Long userId, Long programId, int currentSizeInt, int offset){
		
		List<Object[]> loyaltyList = null;
		try {
			String queryStr = null;
			queryStr = "SELECT   cl, c.emailId, c.mobilePhone FROM ContactsLoyalty cl, Contacts c "
					+ "WHERE c.users="+userId+" AND cl.userId="+userId+" AND c.contactId = cl.contact AND "
							+ "cl.programId="+programId+"  AND cl.serviceType = '"+OCConstants.LOYALTY_SERVICE_TYPE_OC+"'"
					+ " AND cl.membershipStatus = '"+OCConstants.LOYALTY_MEMBERSHIP_STATUS_ACTIVE+"' AND (c.emailId IS NOT NULL OR c.mobilePhone IS NOT NULL) ORDER BY cl.loyaltyId ASC LIMIT "+currentSizeInt+","+offset;
			logger.info(queryStr);
			/*Query query = getSession().createQuery(queryStr);
			query.setFirstResult(currentSizeInt);
			query.setMaxResults(offset);*/
			
			//return (List<ContactsLoyalty>)query.list();
			
			loyaltyList = executeQuery(queryStr,currentSizeInt, offset);
			/*try{
				
				queryStr = "SELECT * FROM ContactsLoyalty LIMIT "+currentSizeInt+", "+offset;
				loyaltyList = jdbcTemplate.query(queryStr, new RowMapper() {
					
					@Override
					public Object mapRow(ResultSet rs, int arg1) throws SQLException {
						
						ContactsLoyalty loyalty = new ContactsLoyalty();
						loyalty.setCardNumber(rs.getLong(""));
						loyalty.setContact(rs.getl)
						
						
						return null;
					}
				});
				
			}catch(Exception e){
				logger.error("Exception in getting all memberships ...", e);
			}*/
			
			//return loyaltyList;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ", e);
		}
		return loyaltyList;
	}
public List<ContactsLoyalty> getAllOCMemberships(Long userId, Long programId, int currentSizeInt, int offset){
		
		List<ContactsLoyalty> loyaltyList = null;
		try {
			String queryStr = null;
			queryStr = " FROM ContactsLoyalty WHERE userId="+userId+" AND programId="+programId+"  AND serviceType = '"+OCConstants.LOYALTY_SERVICE_TYPE_OC+"'"
					+ " AND membershipStatus = '"+OCConstants.LOYALTY_MEMBERSHIP_STATUS_ACTIVE+"' ORDER BY loyaltyId ASC LIMIT "+currentSizeInt+","+offset;
			
			/*Query query = getSession().createQuery(queryStr);
			query.setFirstResult(currentSizeInt);
			query.setMaxResults(offset);*/
			
			//return (List<ContactsLoyalty>)query.list();
			
			loyaltyList = executeQuery(queryStr,currentSizeInt, offset);
			/*try{
				
				queryStr = "SELECT * FROM ContactsLoyalty LIMIT "+currentSizeInt+", "+offset;
				loyaltyList = jdbcTemplate.query(queryStr, new RowMapper() {
					
					@Override
					public Object mapRow(ResultSet rs, int arg1) throws SQLException {
						
						ContactsLoyalty loyalty = new ContactsLoyalty();
						loyalty.setCardNumber(rs.getLong(""));
						loyalty.setContact(rs.getl)
						
						
						return null;
					}
				});
				
			}catch(Exception e){
				logger.error("Exception in getting all memberships ...", e);
			}*/
			
			//return loyaltyList;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ", e);
		}
		return loyaltyList;
	}

	public Long findEnrolledCount(String qury) {
		logger.info("findEnrolledCount : "+qury);
		  List tempList = getHibernateTemplate().find(qury);
		  if(tempList != null && tempList.size() >0) {
			  return ((Long) tempList.get(0));
		  }else return null;
	}
	
	/*public List<MatchedCustomerReportPojo> fetchEnrolledData(String queryStr, int offset, int maxRecords){
		
		try{
           Query query = getSession().createQuery(queryStr);
           query.setMaxResults(maxRecords);
           query.setFirstResult(offset);
           return query.list();
		}catch(Exception e){
			logger.info("Exception while fetching loyalty enroll history data...", e);
			return null;
		}
		
	}*/
	
	public List<Object[]> fetchEnrolledHistoryData(String queryStr, final int startIndex, final int pageSize) {
		logger.info("Fetch enrolled historydata qury with page size paramers.: "+queryStr);
		List<Object[]> loyaltyOptins = null;
		
		try{	
			loyaltyOptins= jdbcTemplate.query(queryStr+" LIMIT "+startIndex+", "+pageSize, new RowMapper() {
			        public Object[] mapRow(ResultSet rs, int rowNum) throws SQLException {
		        
		        	Object[] object = new Object[8];
		        	
		        	object[0] = rs.getLong(1);
		        	object[1] = rs.getString(2);
		        	object[2] = rs.getString(3);//date
		        	object[3] = rs.getString(4);
		        	object[4] = rs.getString(5);
		        	object[5] = rs.getString(6);
		        	object[6] = rs.getString(7);
		        	object[7] = rs.getString(8);
		        	
		        	return object;
		        }
		        
			});
			
			return loyaltyOptins;
		} catch (Exception e) {
			logger.error("Exception in finding contact loyalty history");
			logger.error("Exception ::" , e);
			return null;
		}
	}
	
	public List<Object[]> fetchEnrolledHistoryData(String queryStr) {
		
		logger.info("Fetch enrolled historydata qury without page size paramers.: "+queryStr);
		
		List<Object[]> loyaltyOptins = null;
		
		try{	
			loyaltyOptins= jdbcTemplate.query(queryStr, new RowMapper() {
			        public Object[] mapRow(ResultSet rs, int rowNum) throws SQLException {
		        
		        	Object[] object = new Object[8];
		        	
		        	object[0] = rs.getLong(1);
		        	object[1] = rs.getString(2);
		        	object[2] = rs.getString(3);//date
		        	object[3] = rs.getString(4);
		        	object[4] = rs.getString(5);
		        	object[5] = rs.getString(6);
		        	object[6] = rs.getString(7);
		        	object[7] = rs.getString(8);
		        	
		        	return object;
		        }
		        
			});
			
			return loyaltyOptins;
		} catch (Exception e) {
			logger.error("Exception in finding contact loyalty history");
			logger.error("Exception ::" , e);
			return null;
		}
	}
	
	public ContactsLoyalty findByContactIdStrAndPrgmId(Long userId, String contIdStr, Long programId) {
		try {
    		List<ContactsLoyalty> list = null;
			list = getHibernateTemplate().find("from ContactsLoyalty where userId="+userId+"  AND programId = " + programId +" AND contact IN (" + contIdStr +")"+  
					" AND (rewardFlag = '"+OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L+"' OR rewardFlag = '"+OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_GL+"') ");
			
			if(list != null && list.size() >0) return list.get(0);
			else return null;
			
		} catch (DataAccessException e) {
			logger.error("Exception ::" , e);
			return null;
		}
	}

	public Long findTotGiftIssuance(Long userId, String serviceType, String startDate,String endDate) {
		
		String query = "SELECT COUNT(loyaltyId) FROM  ContactsLoyalty " +
				" WHERE userId=" + userId.longValue() + " AND serviceType='"+serviceType+"' AND (rewardFlag='G' OR rewardFlag='GL') " +
				" AND createdDate BETWEEN '"+startDate+"' AND '"+endDate+"'  " ;

		List tempList = getHibernateTemplate().find(query);

		if(tempList != null && tempList.size()>0)
			return ((Long) tempList.get(0));
		else
			return 0L;
	}



public List<Object[]> getLoyaltyCardsByuserIdStr(String userIdStr, int startIndex, int pageSize) {
		
		String query = null;
		
		List<Object[]> loyaltyOptins = null;
		
			query = "SELECT cl.card_number, cl.card_pin, cl.loyalty_balance, cl.giftcard_balance, "
					+ " c.external_id, c.email_id, c.mobile_phone "+
					" FROM contacts_loyalty cl left join contacts c on cl.contact_id = c.cid  "+
					" where cl.user_id in ("+ userIdStr+")"+
					" order by cl.card_number asc";
		logger.info("loyaltybal dump query = "+query);
		try{	
			loyaltyOptins= jdbcTemplate.query(query+" LIMIT "+startIndex+", "+pageSize, new RowMapper() {
			        public Object[] mapRow(ResultSet rs, int rowNum) throws SQLException {
		        
		        	Object[] object = new Object[7];
		        	object[0] = rs.getString(1);
		        	object[1] = rs.getString(2);
		        	object[2] = rs.getString(3);
		        	object[3] = rs.getString(4);
		        	object[4] = rs.getString(5);
		        	object[5] = rs.getString(6);
		        	object[6] = rs.getString(7);
		        	return object;
		        }
			});
			
			return loyaltyOptins;
		} catch (Exception e) {
			logger.error("Exception in finding contact loyalty optins");
			logger.error("Exception ::" , e);
			return null;
		}
	}

	public ContactsLoyalty getLoyaltyByPrgmAndMembrshp(Long userID, Long programId,String mobilePhone) {
		try {
    		List<ContactsLoyalty> list = null;
			list = getHibernateTemplate().find("from ContactsLoyalty where userId="+userID+" AND programId = " + programId+" AND cardNumber = '" + mobilePhone+"'" );
			
			if(list != null && list.size() >0) return list.get(0);
			else return null;
			
		} catch (DataAccessException e) {
			logger.error("Exception ::" , e);
			return null;
		}
	}
	
	public ContactsLoyalty getLoyaltyByPrgmAndPhone(Users user, Long programId, String phone) {
		try {
    		List<ContactsLoyalty> list = null;
    		boolean isIndia = user.getCountryType().equals(Constants.SMS_COUNTRY_INDIA); //APP-4080 slow query tune up
    		logger.info("isIndia && user countryType"+isIndia+" "+user.getCountryType());
    		String valueStr = !isIndia ? "like '%"+phone+"'" : "='"+phone+"'";
    		logger.info("value string "+valueStr);
			list = getHibernateTemplate().find("from ContactsLoyalty where "
					+ "userId="+user.getUserId()+" AND programId = " + programId+" AND mobilePhone " + valueStr +" " );
			
			logger.info("ContactsLoyalty by phone "+list.size());
			
			if(list != null && list.size() >0) return list.get(0);
			else return null;
			
		} catch (DataAccessException e) {
			logger.error("Exception ::" , e);
			return null;
		}
	}
	public ContactsLoyalty getLoyaltyByPrgmAndEmail(Long userID, Long programId, String EmailId) {
		try {
    		List<ContactsLoyalty> list = null;
			list = getHibernateTemplate().find("from ContactsLoyalty where userId="+userID+" AND programId = " + programId+" AND EmailId = " + EmailId  );
			
			if(list != null && list.size() >0) return list.get(0);
			else return null;
			
		} catch (DataAccessException e) {
			logger.error("Exception ::" , e);
			return null;
		}
	}
	//modify
	/*public ContactsLoyalty findByMembrshpPwd(String encPwd) {
		try {
			
			String query = " FROM ContactsLoyalty WHERE membershipPwd = '"+encPwd+"' ";
			
			List<ContactsLoyalty> list =  getHibernateTemplate().find(query);
			if(list != null && list.size() >0) return list.get(0);
			else return null;
		} catch (DataAccessException e) {
			logger.error("Exception ::" , e);
			return null;
		}
	}*/

	public ContactsLoyalty findLoyaltyById(Long loyaltyId, Long programId, Long userId) {
		
		String query = " FROM ContactsLoyalty WHERE loyaltyId = "+loyaltyId;//+" AND programId = "+programId+" AND userId = "+userId;
		  List<ContactsLoyalty> tempList = getHibernateTemplate().find(query);
		  if(tempList != null && tempList.size() > 0) return tempList.get(0);
		  return null;
		
	}

	public List<ContactsLoyalty> findLoyaltyListByMobile(Long userId, String mobile) {
		List<ContactsLoyalty> list = null;
		try {
			list = getHibernateTemplate().find("FROM ContactsLoyalty WHERE "
					+ " userId="+userId+" AND serviceType='"+OCConstants.LOYALTY_SERVICE_TYPE_SB+"' AND mobilePhone = '" + mobile+"'");

		} catch (DataAccessException e) {
			logger.error("Exception ::" , e);
			return null;
		}catch (Exception e) {
			logger.error("Exception ::" , e);
			return null;
		}
		return list;
	}
	
	
	
	public List<ContactsLoyalty> findChildrenByParent(Long userID, Long loyaltyId) {
		

		List<ContactsLoyalty> list = null;
		try {
			list = getHibernateTemplate().find("FROM ContactsLoyalty WHERE userId="+userID+" "
					+ "AND transferedTo IS NOT NULL AND transferedTo="+loyaltyId.longValue());

		} catch (DataAccessException e) {
			logger.error("Exception ::" , e);
			return null;
		}catch (Exception e) {
			logger.error("Exception ::" , e);
			return null;
		}
		//return list;
		if(list != null && list.size() > 0 )
			return list;
		else 
			return null;
		
	}
public List<Object[]> findChildrenByParentForComm(Long userID, Long loyaltyId) {
		

		List<Object[]> list = null;
		
		try {
			list = getHibernateTemplate().find("SELECT   cl, c.emailId, c.mobilePhone FROM ContactsLoyalty cl, Contacts c "
					+ "WHERE c.users="+userID+" AND cl.userId="+userID+" AND c.contactId = cl.contact AND "
					+ " cl.transferedTo IS NOT NULL AND cl.transferedTo="+loyaltyId.longValue());

		} catch (DataAccessException e) {
			logger.error("Exception ::" , e);
			return null;
		}catch (Exception e) {
			logger.error("Exception ::" , e);
			return null;
		}
		//return list;
		if(list != null && list.size() > 0 )
			return list;
		else 
			return null;
	
		
	}
	//modify
		public List<ContactsLoyalty> findMembershipByContactId(Long userId, String contactId){
		try {
			String query = " FROM ContactsLoyalty WHERE userId="+userId+" AND contact = "+Long.parseLong(contactId)+" ";
			
			 List<ContactsLoyalty> tempList = getHibernateTemplate().find(query);
			
			  if(tempList != null && tempList.size() > 0) return tempList;
			  return null;
		}catch (Exception e) {
			logger.error("Exception ::" , e);
			return null;
		}
			
	}
		
		public List<ContactsLoyalty> findLoyaltyListBy(Long userID, long contactId) { 
			List<ContactsLoyalty> list = null;
			try {
				list = getHibernateTemplate().find("from ContactsLoyalty where userId="+userID+" AND contact = " + contactId+" ORDER BY 1 DESC");
			} catch (DataAccessException e) {
				logger.error("Exception ::" , e);
				return null;
			}
			return list;
		}
		public  Object[] getLiabilityAndRedeemableValue(Long userId,Long programId, Long tierId ) {
			final DecimalFormat f = new DecimalFormat("#0.00");
			String subQry = "";
			
			String qry = " SELECT SUM(giftcard_balance), SUM(loyalty_balance) " +
						 " FROM contacts_loyalty "+
						 " WHERE user_id="+userId+" AND program_id =" + programId +" AND program_tier_id = "+tierId+
						 " AND membership_status != '" + OCConstants.LOYALTY_MEMBERSHIP_STATUS_CLOSED + "' "+ subQry;
			
			List<Object[]> tempList = jdbcTemplate.query(qry, new RowMapper() {
				 public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
					 
					 Object[] object =  new Object[2];
					// object[0] = rs.getObject(0);
					 object[0] = rs.getLong(1);
					 object[1] = rs.getLong(2);
					 
					return object;
				 }
				
			});
			
			if(tempList != null && tempList.size()>0) {
				return tempList.get(0);
			}
			else
				return null;
			
		}
	
}//EOF
