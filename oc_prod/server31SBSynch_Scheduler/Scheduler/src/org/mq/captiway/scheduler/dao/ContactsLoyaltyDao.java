package org.mq.captiway.scheduler.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.ContactsLoyalty;
import org.mq.captiway.scheduler.utility.Constants;
import org.mq.optculture.utils.OCConstants;
//import org.mq.marketer.campaign.beans.SparkBaseCard;
//import org.mq.marketer.campaign.controller.contacts.ContactListUploader;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

public class ContactsLoyaltyDao extends AbstractSpringDao {

	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	
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
			list = getHibernateTemplate().find("from ContactsLoyalty where loyaltyId = " + loyaltyId);
			
			if(list.size() >0) return list.get(0);
			else return null;
			
		} catch (DataAccessException e) {
			logger.error("Exception ::::" , e);
			return null;
		}
    }
    
    public ContactsLoyalty findByContactId(Long userId, long contactId) {
    	try {
    		List<ContactsLoyalty> list = null;
			list = getHibernateTemplate().find("FROM ContactsLoyalty WHERE userId="+userId+" AND contact = " + contactId);
			
			if(list.size() >0) return list.get(0);
			else return null;
			
		} catch (DataAccessException e) {
			logger.error("Error in finding the Contact loyalty for contactId="+contactId , e);
			return null;
		}
    }
 /*   
    public ContactsLoyalty findByContactIdAndOrganizationId(Long organizationId, long contactId) {
    	try {
    		List<ContactsLoyalty> list = null;
    		list = getHibernateTemplate().find("from OrganizationStores where userOrganization = " + organizationId + " AND contact= " + contactId);
    		if(list.size() >0) return list.get(0);
    		else return null;
    	} catch (DataAccessException e) {
    		logger.error("Exception ::::" , e);
    		return null;
    	}
    }
    
   */ 
    
  /*public Long findLoyaltyCountByDates(String startDate, String endDate, String sparkBaseLoc, String contactLoyaltyType) {
	 String subQry = "";
	  if(!contactLoyaltyType.toLowerCase().equals("all")) {
		  
		  subQry = " AND contactLoyaltyType='"+contactLoyaltyType+"'";
		  
		  
	  }
	  
	  
	  
	  String qry = "SELECT COUNT(loyaltyId) FROM ContactsLoyalty WHERE  created_date BETWEEN '"
		  				+startDate+"' AND '"+endDate+"' AND  locationId='"+sparkBaseLoc+"'"+subQry;
	  
	  if(logger.isDebugEnabled()) logger.debug("qry ::"+qry);
	  
	 long count = ((Long)getHibernateTemplate().find(qry).get(0)).longValue();
	 return count;
	  
	  
  }*/
  
  /*public List<ContactsLoyalty> findLoyaltyContactsByDates(String qry, int strtIndex, int size) {
	  
	 
		List<ContactsLoyalty> loyaltyContactList = executeQuery(qry, strtIndex, size);
		
		return loyaltyContactList;
	  
	  
	  
  }*/



	public Long findTotalLoyaltyOptins(long userId, String startDate, String endDate) {
		  
		  String qury = "SELECT COUNT(loyaltyId) FROM  ContactsLoyalty " +
		  				" WHERE userId="+userId+" AND createdDate BETWEEN '"+ startDate +"' AND '"+endDate+"' ";
	//	  if(logger.isDebugEnabled()) logger.debug("query for loyalty Opt-in is..."+qury);
		  List tempList = getHibernateTemplate().find(qury);
		  
		  if(tempList != null && tempList.size() >0) {
			
			  return ((Long) tempList.get(0)).longValue();
		  }else return null;
		  
//		 return((Long) (getHibernateTemplate().find(qury).get(0))).longValue();
	 }
	
	
	public String findTopLocation(String selectType,long userId, String startDate, String endDate) {
		String qury = "";
		String colToken = "location_id";
		
			
		if(selectType.equals("LOCATION_ID")) {
			
			colToken = "location_id";
		qury = "SELECT count(loyalty_id) as tot  ,location_id FROM contacts_loyalty"+
				" WHERE user_id="+userId+" " +
				" AND created_date BETWEEN '"+ startDate +"' AND '"+endDate+"' " +
				" GROUP BY location_id order by tot desc limit 1";
		
	}
		else if(selectType.equals("EMP_ID")){
			colToken = "emp_id";
			qury = "SELECT count(loyalty_id) as tot  ,emp_id FROM contacts_loyalty"+
				   " WHERE user_id="+userId+" " +
				   " AND emp_id is not null"+
				   " AND created_date BETWEEN '"+ startDate +"' AND '"+endDate+"' " +
				   " GROUP BY emp_id order by tot desc limit 1";

		}
		
		if(logger.isDebugEnabled()) logger.debug("findTopLocation of colToken type is.."+colToken+ ":: query is..."+qury);
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
		
		logger.debug("&&&&&&&&&& qry is.."+qury);
		List tempList = getHibernateTemplate().find(qury);
		if(tempList != null && tempList.size() > 0) {
			
			return ((Long) tempList.get(0)).longValue();
		}else return null;
	}*/
	
	
	public ContactsLoyalty findContLoyaltyByCardId(Long userId, String cardId) {
		  
		  
		  String qury = "from ContactsLoyalty WHERE	userId="+userId+" AND cardNumber='"+cardId+"'";
		  if(logger.isDebugEnabled()) logger.debug(qury);
		  List<ContactsLoyalty> tempList = getHibernateTemplate().find(qury);
		  
		  if(tempList != null && tempList.size() >0) {
			
			 return tempList.get(0);
		  } else 
			  return null;
		  
	 }
    
  //TODO modify
	public List<ContactsLoyalty> getLoyaltyForPlaceHolderVal( String cids){
		
		
		try {
			String QRY_FOR_LOYALTY_PLACEHOLDERS = " SELECT cl FROM ContactsLoyalty cl,Contacts c WHERE " +
			"c.contactId IN ("+ cids+" ) AND cl.contact.contactId=c.contactId";

			logger.info("qry for loyalty placeholder ::"+QRY_FOR_LOYALTY_PLACEHOLDERS);
			
			List<ContactsLoyalty> conList = executeQuery(QRY_FOR_LOYALTY_PLACEHOLDERS);
			
			return conList;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::::" , e);
			return null;
		}
	}
	
	// Added for spark base
	
public ContactsLoyalty getContactsLoyaltyByCustId(String customerId, Long userId){
		
		try {
			String query = " FROM ContactsLoyalty WHERE userId = "+userId+" AND customerId = "+customerId;
    		List<ContactsLoyalty> list = null;
			list = getHibernateTemplate().find(query);
			
			if(list.size() >0) return list.get(0);
			else return null;
			
		} catch (DataAccessException e) {
			logger.error("Exception ::::" , e);
			return null;
		}
		
	}
	
	public ContactsLoyalty getContactsLoyaltyByCardId(Long cardId, Long userId) {
		  
		  
		  String query = " FROM ContactsLoyalty WHERE userId = "+userId+" AND cardNumber="+cardId;
		  List<ContactsLoyalty> tempList = getHibernateTemplate().find(query);
		  
		  if(tempList != null && tempList.size() >0) {
			
			 return tempList.get(0);
		  } else 
			  return null;
		  
	 }
	
	//This Only for duplicate ContactLoyalty exist for same Card
		public List<ContactsLoyalty> getContactLoyatyByCardnumber(Long userId, Long cardId){
			String query = " FROM ContactsLoyalty WHERE  userId="+userId+" AND cardNumber="+cardId;
			  List<ContactsLoyalty> tempList = getHibernateTemplate().find(query);
			  return tempList;
		}
		public ContactsLoyalty getLoyaltyByPrgmAndMembrshp(long userId, Long programId,String mobilePhone) {
			try {
	    		List<ContactsLoyalty> list = null;
				list = getHibernateTemplate().find("from ContactsLoyalty where userId="+userId+" AND programId = " + programId+" AND cardNumber = " + mobilePhone );
				
				if(list != null && list.size() >0) return list.get(0);
				else return null;
				
			} catch (DataAccessException e) {
				logger.error("Exception ::" , e);
				return null;
			}
		}

		public ContactsLoyalty getLoyaltyByPrgmAndPhone(Long userId, Long programId, String phone) {
			try {
	    		List<ContactsLoyalty> list = null;
				list = getHibernateTemplate().find("from ContactsLoyalty where userId="+userId+" AND programId = " + programId+" mobilePhone like '%" + phone+"'" );
				
				if(list != null && list.size() >0) return list.get(0);
				else return null;
				
			} catch (DataAccessException e) {
				logger.error("Exception ::" , e);
				return null;
			}
		}

		public List<ContactsLoyalty> findOCLoyaltyListByContactId(Long userId, long contactId) {
	    	List<ContactsLoyalty> list = null;
	    	try {
				list = getHibernateTemplate().find("FROM ContactsLoyalty WHERE userId="+userId+" AND contact = " + contactId + " AND serviceType = '"+OCConstants.LOYALTY_SERVICE_TYPE_OC+"'");
			} catch (DataAccessException e) {
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
			return list;
		
			
		}
}
