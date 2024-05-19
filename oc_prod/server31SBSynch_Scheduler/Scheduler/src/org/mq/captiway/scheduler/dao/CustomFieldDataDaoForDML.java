package org.mq.captiway.scheduler.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import org.mq.captiway.scheduler.beans.Contacts;
import org.mq.captiway.scheduler.beans.CustomFieldData;
import org.mq.captiway.scheduler.utility.Constants;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;


public class CustomFieldDataDaoForDML extends AbstractSpringDaoForDML{  //added for EventTrigger

	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	
	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public CustomFieldDataDaoForDML(){}
	public SessionFactory sessionFactory;

	/*public CustomFieldData find(Long id){
		return (CustomFieldData) super.find(CustomFieldData.class, id);
	}*/

	public void saveOrUpdate(CustomFieldData customFieldData){
		super.saveOrUpdate(customFieldData);
	}

	public void delete(CustomFieldData customFieldData){
		super.delete(customFieldData);
	}

	/*public List findAll(){
		return super.findAll(CustomFieldData.class);
	}

	public CustomFieldData getByContact(long contactId) {
		List list = getHibernateTemplate().find("from CustomFieldData where contact = " + contactId);
		if(list.size()>0)
			return (CustomFieldData)list.get(0);
		else
			return null;
	}*/

	/*public List getCustomFieldData(String cflist, Contacts contact){
		List list = getHibernateTemplate().find("select " + cflist + " from CustomFieldData where contact = " + contact.getContactId());
		return list;
	}*/

	public boolean updateCustomField(String cflist,Contacts contact) {
		int res = getHibernateTemplate().bulkUpdate("update CustomFieldData set " + cflist + " where contact.contactId = '" + contact.getContactId() + "'");
		if(res>0) {
			return true;
		} else
			return false;
	}


	/**
	 *     
	 * @param contactObjs
	 * @return
	 * added for eventTrigger
	 ***********************************************************************
	 *	 Note: The result set doesn't have complete Contacts object
	 *	 To get complete contacts object we could have used 
	 *	 hibernate Template but that needs contact Objs as 
	 *	 input in the query. we created this function to call for TempContacts
	 *   processing during EventTrigger and temp contacts are not complete 
	 *   contacts objs (since their DB struc is diff). That is why 
	 *   using jdbcTemplate and forming a partial contacts obj just with cid. 
	 ***********************************************************************
	 * 
	 */
/*	@SuppressWarnings("unchecked")
	public List<CustomFieldData> getCustomFieldsDataByCids(StringBuffer contactObjs){
		List<CustomFieldData> cfdList = new ArrayList<CustomFieldData>();
		try{

			String queryStr = " SELECT * FROM customfield_data " +
			" WHERE contact_id IN ("+contactObjs+") "; //here emailId id not email address but it is contact object

			if(logger.isDebugEnabled()) logger.debug(" query is "+queryStr);

			cfdList = jdbcTemplate.query(queryStr, new RowMapper() {

				public Object mapRow(ResultSet rs, int rowNum) throws SQLException {

					CustomFieldData cfdObj = new CustomFieldData();
					cfdObj.setcId(rs.getLong("c_id"));

					Contacts contact = new Contacts();
					contact.setContactId(rs.getLong("contact_id"));

					cfdObj.setEmailId(contact);
					cfdObj.setCust1(rs.getString("cust_1"));
					cfdObj.setCust2(rs.getString("cust_2"));
					cfdObj.setCust3(rs.getString("cust_3"));
					cfdObj.setCust4(rs.getString("cust_4"));
					cfdObj.setCust5(rs.getString("cust_5"));
					cfdObj.setCust6(rs.getString("cust_6"));
					cfdObj.setCust7(rs.getString("cust_7"));
					cfdObj.setCust8(rs.getString("cust_8"));
					cfdObj.setCust9(rs.getString("cust_9"));
					cfdObj.setCust10(rs.getString("cust_10"));
					cfdObj.setCust11(rs.getString("cust_11"));
					cfdObj.setCust12(rs.getString("cust_12"));
					cfdObj.setCust13(rs.getString("cust_13"));
					cfdObj.setCust14(rs.getString("cust_14"));
					cfdObj.setCust15(rs.getString("cust_15"));
					cfdObj.setCust16(rs.getString("cust_16"));
					cfdObj.setCust17(rs.getString("cust_17"));
					cfdObj.setCust18(rs.getString("cust_18"));
					cfdObj.setCust19(rs.getString("cust_19"));
					cfdObj.setCust20(rs.getString("cust_20"));

					return cfdObj;
				}
			});

			return cfdList;
			String queryStr = " FROM CustomFieldData " +
    				" WHERE emailId IN ("+contactObjs+") "; //here emailId id not email address but it is contact object

    		if(logger.isDebugEnabled()) logger.debug(" query is "+queryStr);

    		return getHibernateTemplate().find(queryStr);
		}
		catch (Exception e){

			if(logger.isErrorEnabled()) logger.error("**Exception",e);
			return null;
		}
	}
*/
	public void saveByCollection(Collection contactsCollection){ // added for EventTrigger

		super.saveOrUpdateAll(contactsCollection);
	}

	public int deleteFromCfdByList(List<CustomFieldData> cfdList){ // added for EventTrigger

		try{

			int count = 0;

			for (CustomFieldData customFieldData : cfdList) {

				super.delete(customFieldData);
				count++;
			} // for

			return count;
		}
		catch(Exception e){

			logger.error("**Exception ",e);
			return -1;
		}
	} // deleteFromCfdByList
}

