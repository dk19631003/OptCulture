package org.mq.optculture.data.dao;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.Contacts;

import org.mq.marketer.campaign.beans.ContactsLoyalty;
import org.mq.marketer.campaign.beans.DRSent;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.general.Constants;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.utils.ServiceLocator;
import org.springframework.jdbc.datasource.DriverManagerDataSource;


/**
 * 
 */

/**
 * 
 * File : JdbcResultsetHandler.java
 * 
 * Description:
 * 		JdbcResultsetHandler is the util class which performs JDBC operations.
 * 
 * 
 * @author Manjunath Nunna
 * 
 * Created Date  :
 * 
 */
public class ContactsJdbcResultsetHandler extends JdbcResultsetHandler{
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	//	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	//	private static String VF_DATA_SOURCE_JNDI = "ds/vodaFoneDatasource";


	public ContactsJdbcResultsetHandler() {
		super();
	}

	/**
	 * This method will fetch records from resultset. The return values will key=value separated by ";"
	 * @param rowsToReturn
	 * @return List<String>
	 */
	public List<DRSent> getDRReport(int rowsToReturn)
	{
		List<DRSent> aList = null;
		if(rowsToReturn == 0) {
			rowsToReturn = DEFAULT_ROWS_TO_RETURN;
		}
		
		try {
			if (resultSet != null && !resultSet.isAfterLast()) {
				aList = new ArrayList<DRSent>();
				int rowsRead = 0;
				while (rowsRead < rowsToReturn && resultSet.next()) {

					DRSent drsent = new DRSent();
					drsent.setEmailId(resultSet.getString("email_id"));
					
					Calendar cal = null;
				
					Timestamp t=resultSet.getTimestamp("sentDate");
					if(t!=null)
					{
					if(new Date(t.getTime()) != null) {
						cal = Calendar.getInstance();
						cal.setTime(t);
						
						drsent.setSentDate(cal);
					} 
					}
					else {
						drsent.setSentDate(null);
					}
					
						 			
					/*String str=resultSet.getString("sentDate");
					
					int l=str.length();
					str=str.substring(0,l-2);
				
					Date date = resultSet.getDate("sentDate");
					
					if(str != null) {
						  cal = Calendar.getInstance();
						    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						    try {
								cal.setTime(sdf.parse(str));
							} catch (ParseException e) {
								// TODO Auto-generated catch block
								logger.error("Exception in setTime() method", e);
							
							}
					} */
					/*else {
						drsent.setSentDate(null);
					}*/
					//drsent.setSentDate(cal);
					
					drsent.setStatus(resultSet.getString("status"));
					drsent.setUniqueClicks(resultSet.getLong("uniclicks"));
					drsent.setUniqueOpens(resultSet.getLong("uniopens"));
					
					aList.add(drsent);
					rowsRead++;
				}
			}
			else {
				logger.info("Before calling destroy Method:::::::::::::::::::::::;;;;;");
				destroy();
			}
			
		}catch (SQLException e) {
			logger.error("Exception in getRecords() method", e);
			destroy();
		}
		if(aList != null) setCurrentFetchingCount(aList.size()+getCurrentFetchingCount());
		return aList;
	}
	public List<Contacts> getContacts(int rowsToReturn) {
		if(rowsToReturn == 0) {
			rowsToReturn = DEFAULT_ROWS_TO_RETURN;
		}
		List<Contacts> aList = null;
		try {
			if (resultSet != null && !resultSet.isAfterLast()) {
				aList = new ArrayList<Contacts>();
				int rowsRead = 0;
				while (rowsRead < rowsToReturn && resultSet.next()) {

					Contacts contact = new Contacts();
					contact.setContactId(resultSet.getLong("cid"));
					contact.setEmailId(resultSet.getString("email_id"));
					contact.setFirstName(resultSet.getString("first_name"));
					contact.setLastName(resultSet.getString("last_name"));
					contact.setAddressOne(resultSet.getString("address_one"));
					contact.setAddressTwo(resultSet.getString("address_two"));
					contact.setCity(resultSet.getString("city"));
					contact.setState(resultSet.getString("state"));
					contact.setCountry(resultSet.getString("country"));
					//		            contact.setPin(resultSet.getInt("pin"));
					contact.setZip(resultSet.getString("zip"));
					contact.setPurged(resultSet.getBoolean("purged"));
					contact.setOptin(resultSet.getByte("optin"));
					//		            contact.setPhone(resultSet.getLong("phone"));
					contact.setMobilePhone(resultSet.getString("mobile_phone"));
					contact.setGender(resultSet.getString("gender"));
					contact.setHomeStore(resultSet.getString("home_store"));
					contact.setExternalId(resultSet.getString("external_id"));
					contact.setHpId(resultSet.getLong("hp_id"));
					contact.setLoyaltyCustomer(resultSet.getByte("loyalty_customer"));
					
					contact.setMobileStatus(resultSet.getString("mobile_status"));
					Users user  = new Users();
					user.setUserId(resultSet.getLong("user_id"));
					contact.setUsers(user);
					contact.setMlBits(resultSet.getLong("mlbits"));

					Calendar cal = null;
					Timestamp t=resultSet.getTimestamp("birth_day");
					if(t!=null)
					{
					if(new Date(t.getTime()) != null) {
						cal = Calendar.getInstance();
						cal.setTime(t);
						contact.setBirthDay(cal);
					} 
					}
					else {
						contact.setBirthDay(null);
					}
					t=resultSet.getTimestamp("anniversary_day");
					if(t!=null)
					{
					if(new Date(t.getTime()) != null) {
						cal = Calendar.getInstance();
						cal.setTime(t);
						contact.setAnniversary(cal);
					}
					}else {
						contact.setAnniversary(null);
					}
					t=resultSet.getTimestamp("created_date");
					if(t!=null)
					{
					if(new Date(t.getTime()) != null) {
						cal = Calendar.getInstance();
						cal.setTime(t);
						contact.setCreatedDate(cal);
					} 
					}else {
						contact.setCreatedDate(null);
					}
					t=resultSet.getTimestamp("last_status_change");
					if(t!=null)
					{
					if(new Date(t.getTime()) != null) {
						cal = Calendar.getInstance();
						cal.setTime(t);
						contact.setLastStatusChange(cal);
					} 
					}else {
						contact.setLastStatusChange(null);
					}
					t=resultSet.getTimestamp("last_mail_date");
					if(t!=null)
					{
					if(new Date(t.getTime()) != null) {
						cal = Calendar.getInstance();
						cal.setTime(t);
						contact.setLastMailDate(cal);
					}
					}else {
						contact.setLastMailDate(null);
					}
					cal = Calendar.getInstance();
					contact.setEmailStatus(resultSet.getString("email_status"));
					contact.setOptin(resultSet.getByte("optin"));
					contact.setSubscriptionType(resultSet.getString("subscription_type"));
					
					contact.setUdf1(resultSet.getString("udf1"));
					contact.setUdf2(resultSet.getString("udf2"));
					contact.setUdf3(resultSet.getString("udf3"));
					contact.setUdf4(resultSet.getString("udf4"));
					contact.setUdf5(resultSet.getString("udf5"));
					contact.setUdf6(resultSet.getString("udf6"));
					contact.setUdf7(resultSet.getString("udf7"));
					contact.setUdf8(resultSet.getString("udf8"));
					contact.setUdf9(resultSet.getString("udf9"));
					contact.setUdf10(resultSet.getString("udf10"));
					contact.setUdf11(resultSet.getString("udf11"));
					contact.setUdf12(resultSet.getString("udf12"));
					contact.setUdf13(resultSet.getString("udf13"));
					contact.setUdf14(resultSet.getString("udf14"));
					contact.setUdf15(resultSet.getString("udf15"));
					
					
					
					
					
					aList.add(contact);
					
					/*StringBuilder keyValues = new StringBuilder();
					for (int column = 1; column <= columnCount; column++) {
						keyValues.append(metadata.getColumnName(column).toLowerCase()).
						append("=").append(resultSet.getString(column)).
						append(";").toString();
					}
					aList.add(keyValues.toString());*/
					rowsRead++;
				}
				
			} else {
				logger.info("Before calling destroy Method:::::::::::::::::::::::;;;;;");
				destroy();
			}
		} catch (SQLException e) {
			logger.error("Exception in getRecords() method", e);
			destroy();
		}
		if(aList != null) setCurrentFetchingCount(aList.size()+getCurrentFetchingCount());
		return aList;
	}

	public List<ContactsLoyalty> getContactsLoyalty(int rowsToReturn) {
		if(rowsToReturn == 0) {
			rowsToReturn = DEFAULT_ROWS_TO_RETURN;
		}
		List<ContactsLoyalty> aList = null;
		try {
			logger.info("resultset is null"+resultSet.isAfterLast()+"current row is: "+resultSet.getRow());
			if (resultSet != null && !resultSet.isAfterLast()) {
				aList = new ArrayList<ContactsLoyalty>();
				int rowsRead = 0;
				while (rowsRead < rowsToReturn && resultSet.next()) {
					Contacts contact= new Contacts();
					
					ContactsLoyalty contactLoyalty = new ContactsLoyalty();
					
					//contact.setContactId(resultSet.getLong("contactId "));
					contact.setEmailId(resultSet.getString("emailId"));
					contact.setFirstName(resultSet.getString("firstName"));
					contact.setLastName(resultSet.getString("lastName"));
					contact.setAddressOne(resultSet.getString("addressOne"));
					contact.setAddressTwo(resultSet.getString("addressTwo"));
					contact.setCity(resultSet.getString("city"));
					contact.setState(resultSet.getString("state"));
					contact.setCountry(resultSet.getString("country"));
					//		            contact.setPin(resultSet.getInt("pin"));
					contact.setZip(resultSet.getString("zip"));
					//contact.setPurged(resultSet.getBoolean("purged"));
					//contact.setOptin(resultSet.getByte("optin"));
					//		            contact.setPhone(resultSet.getLong("phone"));
					contact.setMobilePhone(resultSet.getString("mobilePhone"));
					contact.setGender(resultSet.getString("gender"));
					contact.setHomeStore(resultSet.getString("homeStore"));
					contact.setExternalId(resultSet.getString("externalId"));
					//contact.setHpId(resultSet.getLong("hp_id"));
					//contact.setLoyaltyCustomer(resultSet.getByte("loyaltyCustomer"));

					contact.setMobileStatus(resultSet.getString("mobileStatus"));
					Users user  = new Users();
					user.setUserId(resultSet.getLong("userId"));
					contact.setUsers(user);
				//	contact.setMlBits(resultSet.getLong("mlbits"));

					Calendar cal = null;
					Timestamp t=resultSet.getTimestamp("birthDay");
					if(t!=null)
					{
					if(new Date(t.getTime()) != null) {
						cal = Calendar.getInstance();
						cal.setTime(t);
						contact.setBirthDay(cal);
					} 
					}
					else {
						contact.setBirthDay(null);
					}
					t=resultSet.getTimestamp("anniversaryDay");
					if(t!=null)
					{
					if(new Date(t.getTime()) != null) {
						cal = Calendar.getInstance();
						cal.setTime(t);
						contact.setAnniversary(cal);
					}
					}else {
						contact.setAnniversary(null);
					}
					t=resultSet.getTimestamp("createdDate");
					if(t!=null)
					{
					if(new Date(t.getTime()) != null) {
						cal = Calendar.getInstance();
						cal.setTime(t);
						contact.setCreatedDate(cal);
					} 
					}else {
						contact.setCreatedDate(null);
					}
					/*t=resultSet.getTimestamp("last_status_change");
					if(new Date(t.getTime()) != null) {
						cal = Calendar.getInstance();
						cal.setTime(t);
						contact.setLastStatusChange(cal);
					} else {
						contact.setLastStatusChange(null);
					}*/
					/*t=resultSet.getTimestamp("last_mail_date");
					if(new Date(t.getTime()) != null) {
						cal = Calendar.getInstance();
						cal.setTime(t);
						contact.setLastMailDate(cal);
					} else {
						contact.setLastMailDate(null);
					}*/
					cal = Calendar.getInstance();

					contact.setEmailStatus(resultSet.getString("emailStatus"));
				//	contact.setOptin(resultSet.getByte("optin"));
					//contact.setSubscriptionType(resultSet.getString("subscription_type"));
					contact.setUdf1(resultSet.getString("udf1"));
					contact.setUdf2(resultSet.getString("udf2"));
					contact.setUdf3(resultSet.getString("udf3"));
					contact.setUdf4(resultSet.getString("udf4"));
					contact.setUdf5(resultSet.getString("udf5"));
					contact.setUdf6(resultSet.getString("udf6"));
					contact.setUdf7(resultSet.getString("udf7"));
					contact.setUdf8(resultSet.getString("udf8"));
					contact.setUdf9(resultSet.getString("udf9"));
					contact.setUdf10(resultSet.getString("udf10"));
					contact.setUdf11(resultSet.getString("udf11"));
					contact.setUdf12(resultSet.getString("udf12"));
					contact.setUdf13(resultSet.getString("udf13"));
					contact.setUdf14(resultSet.getString("udf14"));
					contact.setUdf15(resultSet.getString("udf15"));
					
					contactLoyalty.setContact(contact);
					contactLoyalty.setCardNumber(resultSet.getString("loyaltyCardNumber")==null?null:resultSet.getString("loyaltyCardNumber"));
					contactLoyalty.setCardPin(resultSet.getString("loyaltyCardPin"));
					contactLoyalty.setLoyaltyBalance(resultSet.getString("loyaltyBalance")==null?null:resultSet.getDouble("loyaltyBalance"));
					contactLoyalty.setGiftcardBalance(resultSet.getString("loyaltyGiftCardBalance")==null?null:resultSet.getDouble("loyaltyGiftCardBalance"));
					
					
					aList.add(contactLoyalty);
					
					/*StringBuilder keyValues = new StringBuilder();
					for (int column = 1; column <= columnCount; column++) {
						keyValues.append(metadata.getColumnName(column).toLowerCase()).
						append("=").append(resultSet.getString(column)).
						append(";").toString();
					}
					aList.add(keyValues.toString());*/
					rowsRead++;
				}
				
			} 
			else {
				logger.info("Before calling destroy Method:::::::::::::::::::::::;;;;;");
				destroy();
			}
		} catch (SQLException e) {
			logger.error("Exception in getRecords() method", e);
			destroy();
		}
		if(aList != null) setCurrentFetchingCount(aList.size()+getCurrentFetchingCount());
		return aList;
	}
	/**
	 * This method will close the connection object created during initialization.
	 *
	 */
	public void destroy() {
		super.destroy();
	}

}
