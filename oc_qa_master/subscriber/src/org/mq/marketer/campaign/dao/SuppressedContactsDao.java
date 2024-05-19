package org.mq.marketer.campaign.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.mq.marketer.campaign.beans.Contacts;
import org.mq.marketer.campaign.beans.MailingList;
import org.mq.marketer.campaign.beans.SMSSuppressedContacts;
import org.mq.marketer.campaign.beans.SuppressedContacts;
import org.mq.optculture.data.dao.JdbcResultsetHandler;
import org.mq.optculture.exception.BaseDAOException;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.importcontact.Report;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.mq.marketer.campaign.general.Constants;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.mq.marketer.campaign.beans.Users;

@SuppressWarnings({ "unchecked", "serial","unused"})
public class SuppressedContactsDao extends AbstractSpringDao {

    public SuppressedContactsDao() {}
	
    private SessionFactory sessionFactory;
    
    public SuppressedContacts find(Long id) {
        return (SuppressedContacts) super.find(SuppressedContacts.class, id);
    }

   

    
    private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
    
    
    public List findAll() {
        return super.findAll(SuppressedContacts.class);
    }

    public List findByUserName(String userName){
    	return getHibernateTemplate().find("from SuppressedContacts where users.userName = '" + userName + "'");
    }
    
    public List<SuppressedContacts> findAllByUserId(Long userId) {
    	try {
			return getHibernateTemplate().find("from SuppressedContacts where user.userId="+userId);
		} catch (DataAccessException e) {
			logger.error("Exception ::" , e);
			return null;
		}
    }
    
   
    
    /*public List<SuppressedContacts> searchContactsById(Long userId,String searchStr, String type, int firstResult, int size) {
    	String queryStr = "from SuppressedContacts where user.userId="+ userId + " and type='"+type+"' and email like '%"+ searchStr +"%'";
    	
    	return executeQuery(queryStr, firstResult, size);
    }*/
    /*//ISH
    public List<SuppressedContacts> searchContactsById(Long userId,String searchStr, String type) {
    	String queryStr = "from SuppressedContacts where user.userId="+ userId + " and type='"+type+"' and email like '%"+ searchStr +"%'";
    	logger.info("searchContactsById---------->"+queryStr);
    	List<SuppressedContacts> list = executeQuery(queryStr);
    	return list;
    }*/
    
    public int getCountBySearchStr(Long userId, String searchStr, String type) {
    	
    	String query = "select count(*) from SuppressedContacts where user.userId="+userId +" and type ='"+type+"' and email like '%"+ searchStr +"%'";
    	return ((Long)((getHibernateTemplate().find(query)).get(0))).intValue();
    	
    }
    
    
    public int getTotCountByUserId(Long userId) {
    	String query = "select count(*) from SuppressedContacts where user.userId="+userId;
    	return ((Long)((getHibernateTemplate().find(query)).get(0))).intValue();
    }
    
    
    
    public int getTotCountByUserId(Long userId, String type, String searchStr) {
    	
    	String appendQry="";
    	String query;
    	if(searchStr.trim().length() > 0) {
    		
    		appendQry = " AND email like '%"+ searchStr +"%'";
    	}
    	
    	if(type.equals("all")){
    		
    		query = "select count(*) from SuppressedContacts where user.userId="+userId+appendQry;
    	}
    	else{
    		
    		query = "select count(*) from SuppressedContacts where user.userId="+userId+" AND type='" + type + "'"+appendQry;
    	}
    	
    	return ((Long)((getHibernateTemplate().find(query)).get(0))).intValue();
    }
    
   
    
    public List<SuppressedContacts> getContactsByType(Long userId,String type) {
    	return getHibernateTemplate().find("from SuppressedContacts where user.userId="+ userId + " and type='" + type + "'");
    } 
    
    public List<SuppressedContacts> findAllByUsrId(Long userId,String type, String searchStr,int firstResult, int size) {
    	logger.info("type :"+ type);
    	
    	String qryStr;
    	String appendQry="";
    	if(searchStr.trim().length() > 0) {
    		
    		appendQry = " AND email like '%"+ searchStr +"%'";
    	}
    	//changes start 2.5.3.0 
    	if(type.equals("all")) {
    		qryStr = "from SuppressedContacts where user.userId="+ userId +" and type like '%%' "+appendQry + " ORDER BY suppressedtime DESC";
    	}
    	else {
    		qryStr = "from SuppressedContacts where user.userId="+ userId +" and type='" + type + "'"+appendQry + " ORDER BY suppressedtime DESC";
    	}
    	//changes end 2.5.3.0 
    	logger.info("QueryForSuppress"+qryStr+" Type");
    	
    	List<SuppressedContacts> list= executeQuery(qryStr, firstResult, size);
    	//logger.info("list size============"+list.size());
       	return list;
    }
    
    public List<Object[]> getSuppressedContactsByReport(Long userId, String ListNames, int startFrom, int count, String suppressedTime) {
    	
    	
    	try {
			List<Object[]> catList = new ArrayList<Object[]>();
			
			/*String queryStr = "SELECT s.email,s.type FROM SuppressedContacts s,Contacts c where s.user="+userId+"" +
							" AND s.email=c.emailId AND c.mailingList in(SELECT m.listId FROM MailingList m where m.users="+userId+")";
			*/
			
			
			/*String queryStr = "SELECT COUNT(DISTINCT s.mobile) FROM SMSSuppressedContacts s,SMSCampaignSent c, MailingList m WHERE c.smsCampaignReport="+repId+" AND s.user="+userId+"" +
			  "  AND m.listName IN ("+campRepLists+") AND s.mobile LIKE CONCAT('%', c.mobileNumber, '%')" ;
*/
			

			/*String queryStr = "SELECT DISTINCT s.mobile,s.type FROM SMSSuppressedContacts s,Contacts c, MailingList m WHERE " +
					" (s.user="+userId+" OR s.user IS NULL ) AND c.phone IS NOT NULL AND c.mailingList=m.listId AND m.listName IN ("+ListNames+") AND s.mobile LIKE CONCAT('%', c.phone, '%') AND s.suppressedtime < '"+suppressedTime+"'" ;
*/
			
			/*String queryStr = " SELECT DISTINCT(s.email),s.type FROM SuppressedContacts s,Contacts c, MailingList m, c.mlSet ml where s.user="+userId+"" +
			" AND ml.listId=m.listId AND m.listName IN ("+ListNames+") AND c.emailId IS NOT NULL AND c.emailId !='' AND  s.email=c.emailId AND s.suppressedtime < '"+suppressedTime+"'" ;*/
			
			/*String queryStr = " SELECT DISTINCT s.email,s.type FROM suppressed_contacts s,contacts c, mailing_lists m, contacts_mlists cm where s.user_id="+userId.longValue()+"" +
					" AND c.cid = cm.cid AND cm.list_id=m.list_id AND m.list_name IN ("+ListNames+") AND c.email_id IS NOT NULL AND c.email_id !='' AND  s.email=c.email_id AND s.suppressed_time < '"+suppressedTime+"'" ;*/
			
			String queryStr = " SELECT DISTINCT s.email, s.type FROM SuppressedContacts s,Contacts c, MailingList m WHERE s.user="+userId+" AND c.users="+userId+" AND m.users="+userId+
			" AND m.listName IN ("+ListNames+") AND bitwise_and(c.mlBits, m.mlBit)>0 AND c.emailId IS NOT NULL AND c.emailId !='' AND  s.email=c.emailId AND s.suppressedtime < '"+suppressedTime+"'" ;
			
			
			/*catList = jdbcTemplate.query(queryStr, new RowMapper() {

		        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
		        	Object[] object  = new Object[2];
		        	object[0] = rs.getString(1);
		        	object[1] = rs.getString(2);
		        	return object;
		        }
			});*/
			catList = executeQuery(queryStr, startFrom, count);
			return catList;
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
			return null;
		}
    	
    }
    
    public int getCountByReport(Long userId, String campRepLists, String suppressedTime) {
    	
    	
    	/*String queryStr = "SELECT COUNT(s.id) FROM SuppressedContacts s,Contacts c where s.user="+userId+"" +
						" AND s.email=c.emailId  AND c.mailingList in(SELECT m.listId FROM MailingList m where m.users="+userId+")";
*/
    	/*String queryStr = "SELECT COUNT(s.id) FROM SuppressedContacts s,CampaignSent cs where s.user="+userId+"" +
						  " AND s.email=cs.emailId  AND cs.campaignReport="+campRepId; 
    	*/
    	
    	/*String queryStr = " SELECT DISTINCT(s.email),s.type FROM SuppressedContacts s,Contacts c, MailingList m where s.user="+userId+"" +
		" AND c.mailingList=m.listId AND m.listName IN ("+ListNames+") AND c.emailId IS NOT NULL AND c.emailId !='' AND  s.email=c.emailId AND s.suppressedtime < '"+suppressedTime+"'" ;
*/
    	
    	/*String queryStr = "SELECT COUNT(DISTINCT s.email) FROM SuppressedContacts s,Contacts c, MailingList m, c.mlSet ml  WHERE s.user="+userId+"" +
    	" AND ml.listId=m.listId AND m.listName IN ("+campRepLists+") AND c.emailId IS NOT NULL AND c.emailId !='' AND  s.email=c.emailId AND s.suppressedtime < '"+suppressedTime+"'" ;*/ 
    try{	
    	/*String queryStr = "SELECT COUNT(DISTINCT s.email) FROM suppressed_contacts s,contacts c, mailing_lists m, contacts_mlists cm  WHERE s.user_id="+userId.longValue()+"" +
    	    	" AND c.cid = cm.cid AND cm.list_id=m.list_id AND m.list_name IN ("+campRepLists+") AND c.email_id IS NOT NULL AND c.email_id !='' AND  s.email=c.email_id AND s.suppressed_time < '"+suppressedTime+"'" ;*/ 
    	 
    	String queryStr = "SELECT COUNT(DISTINCT s.email) FROM SuppressedContacts s,Contacts c, MailingList m WHERE s.user="+userId+" AND c.users="+userId+" AND m.users="+userId+
    	" AND m.listName IN ("+campRepLists+") AND bitwise_and(c.mlBits, m.mlBit)>0 AND c.emailId IS NOT NULL AND c.emailId !='' AND  s.email=c.emailId AND s.suppressedtime < '"+suppressedTime+"'" ;
    	
    	//int count = ((Long)getHibernateTemplate().find(queryStr).get(0)).intValue();
    	
    	int count = ((Long)getHibernateTemplate().find(queryStr).get(0)).intValue();
//    	return getHibernateTemplate().queryForInt(queryStr);
    	return count;
    }catch(Exception e){
    	logger.error("Exception while getting email counts in campaignreports ");
    	logger.error("Exception ::" , e);
    	return 0;
    }
    }

	public List<SuppressedContacts> isAlreadySuppressedContact(String contactEmailId, Long userId) {
		// TODO Auto-generated method stub
		
		Map<String,Boolean> existingMap = new HashMap<String, Boolean>();
		
		List<SuppressedContacts> suppsedContactList =null;
		String qry= "select * from  suppressed_contacts where user_id="+ userId + " and email REGEXP '" + contactEmailId + "' and type in('" +Constants.SUPP_TYPE_BOUNCED +"','" +Constants.CS_STATUS_SPAMMED+ "')";
		
		try {
			suppsedContactList = jdbcTemplate.query(qry, new RowMapper() {
	
		        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
		        	SuppressedContacts suppressedContacts = new SuppressedContacts();
		            
		        	suppressedContacts.setId(rs.getLong("id"));
		        	suppressedContacts.setEmail(rs.getString("email"));
		        	
		        	
		        	 if(rs.getDate("suppressed_time") != null) {
			            	Calendar cal = Calendar.getInstance();
			            	cal.setTime(rs.getTimestamp("suppressed_time"));
			            	suppressedContacts.setSuppressedtime(cal);
			            	
			            } 
		        	 
		        	 suppressedContacts.setType(rs.getString("type"));
		        	
		        	Users user  = new Users();
					user.setUserId(rs.getLong("user_id"));
					suppressedContacts.setUser(user);
					
					suppressedContacts.setReason(rs.getString("reason"));
		    		
		            
		            return suppressedContacts;
		        }
		    });
		
		} catch (Exception e) {
			logger.error("Exception ***", e);
		}
		return suppsedContactList;
	}
    
	public String isAlreadySuppressedContact(Long userId, String contactEmailId){
		
		try {
			String status = null;
			
			String query = "FROM SuppressedContacts where user.userId="+userId +" and  email like '"+ contactEmailId +"' and type in('" +Constants.SUPP_TYPE_BOUNCED +"','" +Constants.CS_STATUS_SPAMMED+ "')";
			List<SuppressedContacts>  suppContList =  getHibernateTemplate().find(query);
			if(suppContList != null && suppContList.size() > 0) {
				
				status = suppContList.get(0).getType();
			}
			
			return status;
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			return null;
		}
		
	}
	
public boolean findByUserId(Long userId, String contactEmailId){
		boolean found = false;
		try {			
			String query = "FROM SuppressedContacts where user.userId="+userId +" and  email like '"+ contactEmailId +"'";
			List<SuppressedContacts>  suppContList =  getHibernateTemplate().find(query);
			if(suppContList != null && ! suppContList.isEmpty()) {
				found = true;
			}
			
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
		}
		return found;
	}
	
	

	//Changes 2.5.3.0 start
    public List<SuppressedContacts> findSuppressedContactsByReport(Users user,Report report) {
  	  
    	int strtIndex =0;
    	int size =1000;
    	//MailingListDao mailingListDao=null;
		//MailingList mailingList=new MailingList();
		//JdbcResultsetHandler jdbcResultsetHandler = new JdbcResultsetHandler();
		logger.info("Enter findSuppressedContactsByDates");

		String qry = " FROM SuppressedContacts WHERE user=" +user.getUserId();
		
		
		
		String subQry ="";
			
		if(report.getStartDate()!=null && report.getEndDate()!=null&&!report.getStartDate().isEmpty()&&!report.getEndDate().isEmpty()){
			subQry+=" AND suppressedtime BETWEEN '"+report.getStartDate()+"' AND '"+report.getEndDate()+"'";
		}
//		if(report.getContactSource()!=null &&!report.getContactSource().isEmpty()&& !report.getContactSource().equalsIgnoreCase(OCConstants.IMPORT_CONTACT_TYPE_ALL)) {
//			subQry+=" AND optin_medium like '%"+report.getContactSource()+"%'";
//		}
//		if(report.getContactList()!=null&&!report.getContactList().isEmpty() && !report.getContactList().equalsIgnoreCase("All")) {
//			try {
//				mailingListDao=(MailingListDao) ServiceLocator.getInstance().getDAOByName(OCConstants.MAILINGLIST_DAO);
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			mailingList=mailingListDao.findByListName(report.getContactList(), user.getUserId());
//			subQry+=" AND c.mlbits & "+ mailingList.getMlBit() + " > 0";
//		}
//		if(report.getStore()!=null && !report.getStore().isEmpty()){
//			subQry+=" AND home_store='"+report.getStore()+"'";
//		}
		if(report.getOffset()!=null && !report.getOffset().isEmpty()) {
			strtIndex =Integer.parseInt(report.getOffset());
		}
		if(report.getMaxRecords()!=null && !report.getMaxRecords().isEmpty()) {
			size =Integer.parseInt(report.getMaxRecords())>1000?1000:Integer.parseInt(report.getMaxRecords());
		}
	
		
//		subQry +=" limit "+strtIndex+","+size;
//		
//		logger.info(qry+subQry);
//		jdbcResultsetHandler.executeStmt(qry+subQry);
//		ResultSet resultSet = jdbcResultsetHandler.getResultSet();
//    	List<Contacts> ContactsList = getHibernateTemplate().find(qry);
		logger.info("QUERY FOR SUPPRESS=====>"+qry+subQry);
		List<SuppressedContacts> SuppressedContactList = executeQuery(qry+subQry,strtIndex,size);
  		
		
  		
  		
  		logger.info("Exit findSuppressedContactsByDates");
  		return SuppressedContactList;
  	  
  	  
  	  
  }
  //Changes 2.5.3.0 end
	
	
	
  
}

