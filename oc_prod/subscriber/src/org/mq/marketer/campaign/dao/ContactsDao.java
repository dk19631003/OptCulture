package org.mq.marketer.campaign.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.mq.marketer.campaign.beans.Contacts;
import org.mq.marketer.campaign.beans.MailingList;
import org.mq.marketer.campaign.beans.SuppressedContacts;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.custom.MyCalendar;
//import org.mq.marketer.campaign.beans.MyStoredProcedure;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.data.dao.JdbcResultsetHandler;
import org.mq.optculture.model.importcontact.Lookup;
import org.mq.optculture.model.importcontact.Report;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.zkoss.zkplus.spring.SpringUtil;
 
@SuppressWarnings({ "unchecked", "serial", "unused", "deprecation" })
public class ContactsDao extends AbstractSpringDao {

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);

	private SessionFactory sessionFactory;

	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public ContactsDao() {
	}

	public Contacts find(Long id) {
		return (Contacts) super.find(Contacts.class, id);
	}

	public List findAll() {
		return super.findAll(Contacts.class);
	}

	/*
	 * public List<Contacts> findByList(Long listId, int firstResult, int
	 * maxResults) { logger.debug("Just Entered  ");
	 * 
	 * try {//String qry
	 * =" from Contacts c ,MailingList ml where c.mailingList=ml.listId and c.contactId= "
	 * +6;
	 * 
	 * String query =
	 * "select distinct c from Contacts c join c.mlSet ml where ml.listId= " +
	 * listId + " order by c.emailId";
	 * 
	 * //String query = "from Contacts where mailingList= " + listId +
	 * " order by emailId";
	 * logger.info("listid="+listId+" firstresult="+firstResult+" max="+maxResults);
	 * List<Contacts> cList = executeQuery(query, firstResult, maxResults);
	 * logger.debug(""+cList.size()); logger.debug("Exiting before return"); return
	 * cList; } catch (Exception e) { logger.error("** Error : " + e.getMessage() +
	 * " **"); return null; } }
	 */

	public List<Contacts> findByList(MailingList mList, int firstResult, int maxResults) {

		try {// String qry =" from Contacts c ,MailingList ml where c.mailingList=ml.listId
				// and c.contactId= "+6;

			// String query = "select distinct c from Contacts c join c.mlSet ml where
			// ml.listId= " + listId + " order by c.emailId";

			String query = "FROM Contacts WHERE users = " + mList.getUsers().getUserId() + " AND bitwise_and(mlBits, "
					+ mList.getMlBit() + ") > 0 order by emailId";

			List<Contacts> cList = executeQuery(query, firstResult, maxResults);
			logger.debug("" + cList.size());
			logger.debug("Exiting before return");
			return cList;
		} catch (Exception e) {
			logger.error("** Error : " + e.getMessage() + " **");
			return null;
		}
	}

	/**
	 * this method is called from ViewContactsController and it retrieves all the
	 * distinct (emailId) from the contacts table and which is equilant of the
	 * contact_score with single UserId
	 * 
	 * @return
	 */

	public List<Object[]> findByScoreList(Set<Long> listIds, String contactIds) {

		try {
			String listIdsStr = Utility.getIdsAsString(listIds);
			if (listIdsStr.isEmpty())
				return null;
			List<Object[]> contactAndScore = null;
			String qry = "SELECT c.emailId,cs " + " FROM ContactScores cs, Contacts c, MailingList m "
					+ " WHERE m.listId in(" + listIdsStr + ") AND c.emailId LIKE cs.emailId "
					+ " AND cs.user=m.users  AND c.emailId in(" + contactIds + ")" + " GROUP BY c.emailId ";
			contactAndScore = executeQuery(qry);

			return contactAndScore;

		} catch (DataAccessException e) {
			logger.error("**Exception : error while getting from the query object ", e);
			return null;
		}

	}

	// added for reports
	/*
	 * public long getCountByReport(String listNames, Long userId) {
	 * 
	 * 
	 * String qry =
	 * "SELECT COUNT(distinct c.contactId) FROM Contacts c join c.mlSet ml WHERE ml.listId in(SELECT listId FROM MailingList WHERE users="
	 * +userId+" AND listName in("+listNames+")) ";
	 * 
	 * long totContacts =
	 * ((Long)getHibernateTemplate().find(qry).get(0)).intValue(); return
	 * totContacts;
	 * 
	 * }
	 */

	/**
	 * this method is called from ViewContactsController and it retrieves all the
	 * contacts of the all the mailinglists for provided mlids id
	 * 
	 * @param listId
	 * @return
	 */
	/*
	 * public List<Contacts> find(String mlIds, int firstResults, int maxResult) {
	 * List<Contacts> contactsList = null; logger.debug("--just entered--"); try {
	 * 
	 * // String queryStr =
	 * "from Contacts where mailingList in ("+mlIds+") order by emailId"; String
	 * queryStr =
	 * "select distinct c from Contacts c join c.mlSet ml where ml.listId in ("
	 * +mlIds+") ORDER BY c.createdDate DESC"; contactsList = executeQuery(queryStr,
	 * firstResults, maxResult); return contactsList;
	 * 
	 * 
	 * }catch (Exception e) { logger.error("** Exception ",e); return null; } }
	 */

	public List<Contacts> find(Set<MailingList> mlSet, int firstResults, int maxResult) {
		List<Contacts> contactsList = null;
		try {
			Iterator<MailingList> mlIt = mlSet.iterator();

			long mlsbit = Utility.getMlsBit(mlSet);
			String queryStr = Utility.getMultiUserQry(mlSet, mlsbit);

			if (queryStr != null) {
				queryStr = "SELECT DISTINCT c " + queryStr + " ORDER BY c.createdDate DESC ";
			} else {
				Long userId = mlIt.next().getUsers().getUserId();
				queryStr = " FROM Contacts  WHERE users = " + userId + " AND bitwise_and(mlBits," + mlsbit
						+ ")>0 ORDER BY createdDate DESC";
			}

			contactsList = executeQuery(queryStr, firstResults, maxResult);
			return contactsList;

		} catch (Exception e) {
			logger.error("** Exception ", e);
			return null;
		}
	}

	/**
	 * this method is called from ViewContactsController and based on the given
	 * status it retrieves all the contacts of the all the mailinglists for provided
	 * mlids id
	 * 
	 * @param listId
	 * @return
	 */

	public List<Contacts> find(Set<MailingList> mlSet, String emailStatus, int firstResults, int maxResult,
			String mobileStatus) {
		List<Contacts> contactsList = null;
		try {
			Iterator<MailingList> mlIt = mlSet.iterator();

			long mlsbit = Utility.getMlsBit(mlSet);
			String queryStr = Utility.getMultiUserQry(mlSet, mlsbit);

			if (queryStr != null) {
				queryStr = "SELECT DISTINCT c " + queryStr + " AND c.emailStatus='" + emailStatus
						+ "' AND c.mobileStatus ='" + mobileStatus + "' ";
			} else {
				Long userId = mlIt.next().getUsers().getUserId();
				queryStr = " FROM Contacts  WHERE users = " + userId + " AND bitwise_and(mlBits," + mlsbit + ")>0  "
						+ " AND emailStatus='" + emailStatus + "' AND mobileStatus ='" + mobileStatus + "' ";
			}

			contactsList = executeQuery(queryStr, firstResults, maxResult);
			return contactsList;

		} catch (Exception e) {
			logger.error("** Exception ", e);
			return null;
		}

	}

	public long findByAll(Set<MailingList> mlSet, String status, String mobStatus) {
		List<Contacts> contactsList = null;

		Iterator<MailingList> mlIt = mlSet.iterator();

		long mlsbit = Utility.getMlsBit(mlSet);
		String queryStr = Utility.getMultiUserQry(mlSet, mlsbit);

		if (queryStr != null) {
			queryStr = "SELECT COUNT(DISTINCT c.contactId) " + queryStr + "AND c.emailStatus='" + status
					+ "'  AND c.mobileStatus='" + mobStatus + "' ";
		} else {
			Long userId = mlIt.next().getUsers().getUserId();
			queryStr = "SELECT COUNT(contactId) FROM Contacts " + " WHERE users = " + userId
					+ " AND bitwise_and(mlBits," + mlsbit + ")>0  " + "AND emailStatus='" + status
					+ "'  AND mobileStatus='" + mobStatus + "' ";
		}

		long count = ((Long) (executeQuery(queryStr).get(0))).longValue();
		return count;

	}

	/**
	 * this method is called from ViewContactsController based on the given status
	 * and searchString it retrieves all the contacts of the all the mailinglists
	 * for provided mlids id
	 * 
	 * @param mlIds
	 * @param searchStr
	 * @param status
	 * @param searchStr
	 * @param firstResults
	 * @param maxResult
	 * @param mobStr
	 * @param mobStatus
	 * @return
	 */

	public List<Contacts> find(Set<MailingList> mlSet, String searchStr, String status, int firstResults, int maxResult,
			String mobStr, String mobStatus) {
		List<Contacts> contactsList = null;
		logger.debug("--just entered--");
		try {
			Iterator<MailingList> mlIt = mlSet.iterator();

			long mlsbit = Utility.getMlsBit(mlSet);
			String queryStr = Utility.getMultiUserQry(mlSet, mlsbit);

			String subQry = "";

			if (!searchStr.isEmpty()) {

				subQry += " AND c.emailId like '%" + searchStr + "%'";

			}

			if (!mobStr.isEmpty()) {

				subQry += " AND c.mobilePhone ='" + mobStr + "'";

			}

			if (!status.equalsIgnoreCase("--All--")) {

				subQry += " AND c.emailStatus = '" + status + "'";
			}

			if (!mobStatus.equalsIgnoreCase("--All--")) {
				if (mobStatus.equalsIgnoreCase(Constants.CON_MOBILE_STATUS_PENDING)) {

					subQry += " AND c.mobileStatus IS NULL";
				} else {
					subQry += " AND c.mobileStatus = '" + mobStatus + "'";
				}
			}

			if (queryStr != null) {
				queryStr = "SELECT DISTINCT c " + queryStr;

			} else {
				Long userId = mlIt.next().getUsers().getUserId();
				queryStr = " FROM Contacts c WHERE c.users = " + userId + " AND bitwise_and(c.mlBits," + mlsbit
						+ " )>0  " + subQry;
			}

			/*
			 * if(status.equalsIgnoreCase("--All--")||
			 * mobStatus.equalsIgnoreCase("--All--")) { queryStr =
			 * "from Contacts where mailingList in ("+mlIds+") and emailId like '%"
			 * +searchStr+"%'   and phone like '%"+mobStr+"%'  "; }else { queryStr =
			 * "from Contacts where mailingList in ("+mlIds+") and emailId like '%"
			 * +searchStr+"%' and emailStatus like '%"+status+"%' and   phone like'%"
			 * +mobStr+"%'  and mobileStatus like'%"+mobStatus+"%' "; }
			 * logger.info(" query is "+queryStr);
			 */
			contactsList = executeQuery(queryStr, firstResults, maxResult);
			return contactsList;

		} catch (Exception e) {
			logger.error("** Exception ", e);
			return null;
		}

	}

	/**
	 * this overridden method is called from ViewContactsController based on the
	 * given status and searchString it retrieves all the contacts of the all the
	 * mailinglists for provided mlids id
	 * 
	 * @param mlIds
	 * @param searchStr
	 * @param status
	 * @param searchStr
	 * @param mobStr
	 * @param mobStatus
	 * @return
	 */
	public long findAll(Set<MailingList> mlSet, String searchStr, String status, String mobStr, String mobStatus) {

		long count = 0;

		Iterator<MailingList> mlIt = mlSet.iterator();

		long mlsbit = Utility.getMlsBit(mlSet);
		String queryStr = Utility.getMultiUserQry(mlSet, mlsbit);

		String subQry = "";

		if (!searchStr.isEmpty()) {

			subQry += " AND c.emailId like '%" + searchStr + "%'";

		}

		if (!mobStr.isEmpty()) {

			subQry += " AND c.mobilePhone ='" + mobStr + "'";

		}

		if (!status.equalsIgnoreCase("--All--")) {

			subQry += " AND c.emailStatus = '" + status + "'";
		}

		if (!mobStatus.equalsIgnoreCase("--All--")) {
			if (mobStatus.equalsIgnoreCase(Constants.CON_MOBILE_STATUS_PENDING)) {

				subQry += " AND c.mobileStatus IS NULL";
			} else {
				subQry += " AND c.mobileStatus = '" + mobStatus + "'";
			}
		}

		if (queryStr != null) {
			queryStr = "SELECT COUNT(c.contactId) " + subQry;

		} else {
			Long userId = mlIt.next().getUsers().getUserId();
			queryStr = " SELECT COUNT(c.contactId) FROM Contacts c " + " WHERE c.users = " + userId
					+ " AND bitwise_and(c.mlBits," + mlsbit + " )>0 " + subQry;
		}

		/*
		 * if(status.equalsIgnoreCase("--All--") ||
		 * mobStatus.equalsIgnoreCase("--All--")) { queryStr =
		 * "select count(contactId) from Contacts where mailingList in ("
		 * +mlIds+") and emailId like '%"+searchStr+"%'  and phone like '%"+mobStr+"%' "
		 * ; }else { queryStr =
		 * "select count(contactId) from Contacts where mailingList in ("
		 * +mlIds+") and emailId like '%"+searchStr+"%'  and emailStatus='"
		 * +status+"'  and phone like '%"+mobStr+"%'  and mobileStatus ='"
		 * +mobStatus+"' "; }
		 */
		count = ((Long) (executeQuery(queryStr).get(0))).longValue();
		return count;
	}

	public Long findSize(MailingList mlList) {

		String queryString = "SELECT COUNT(DISTINCT contactId) FROM Contacts " + "WHERE users ="
				+ mlList.getUsers().getUserId() + "AND bitwise_and(mlBits," + mlList.getMlBit() + " )>0 ";
		return (Long) (getHibernateTemplate().find(queryString)).get(0);
	}

	public Long getUnPurgedSize(MailingList mlList) {

		String queryString = "SELECT COUNT(DISTINCT contactId) FROM Contacts  " + " WHERE users ="
				+ mlList.getUsers().getUserId() + " AND bitwise_and(mlBits," + mlList.getMlBit()
				+ " )>0 AND purged = false";
		return (Long) (getHibernateTemplate().find(queryString)).get(0);
	}

	public long getEmailCount(Set<MailingList> mlSet, boolean onlyActive) { // here mlIds are comma
		// separated ids of mailing
		// lists
		Iterator<MailingList> mlIt = mlSet.iterator();
		Long userId = mlIt.next().getUsers().getUserId();

		long mlsbit = Utility.getMlsBit(mlSet);

		if (onlyActive) {
			return ((Long) (getHibernateTemplate().find("SELECT COUNT(DISTINCT emailId) FROM Contacts WHERE users = "
					+ userId + " AND bitwise_and(mlBits," + mlsbit + " )>0 " + " AND emailStatus like 'Active'"))
							.get(0)).longValue();
		} else {
			return ((Long) (getHibernateTemplate().find("SELECT COUNT(DISTINCT c.emailId) FROM Contacts WHERE users = "
					+ userId + " AND bitwise_and(mlBits," + mlsbit + " )>0 ")).get(0)).longValue();
		}

	}

	public long getMobileCount(Set<MailingList> mlSet, boolean entirelist) { // here mlIds are comma
		// separated ids of mailing
		// lists
		Iterator<MailingList> mlIt = mlSet.iterator();
		Long userId = mlIt.next().getUsers().getUserId();

		long mlsbit = Utility.getMlsBit(mlSet);
		String qry = "SELECT COUNT(DISTINCT mobile_phone) FROM Contacts WHERE users = " + userId
				+ " AND bitwise_and(mlBits," + mlsbit + " )>0 " + " AND mobileStatus like 'Active'";
		if (entirelist) {
			return ((Long) (getHibernateTemplate().find(qry)).get(0)).longValue();
		} else {
			return ((Long) (getHibernateTemplate().find(qry + " AND mobileOptin=1")).get(0)).longValue();
		}

	}

	/*
	 * public long getActiveEmailCount(String mlIds) { // here mlIds are comma //
	 * separated ids of mailing // lists return ((Long) (getHibernateTemplate()
	 * .find("select count(distinct emailId) from Contacts where mailingList in (" +
	 * mlIds + ") and emailStatus like 'Active'")).get(0)) .longValue();
	 * 
	 * }
	 */

	/**
	 * This method returns the number of unique e-mails for the contact lists which
	 * u passed, if any error occurs returns -1
	 * 
	 * @param lists
	 *            is the comma separated list Ids
	 * @param status
	 *            is the contact status
	 */

	public long getUniqueCount(Set<MailingList> mlSet, String status) {

		Iterator<MailingList> mlIt = mlSet.iterator();

		long mlsbit = Utility.getMlsBit(mlSet);
		String queryStr = Utility.getMultiUserQry(mlSet, mlsbit);

		if (queryStr != null) {
			queryStr = "SELECT COUNT( c.emailId) " + queryStr + "AND c.emailId IS NOT NULL AND c.emailStatus='" + status
					+ "' ";

		}
		// Changes App-current
		else {
			Long userId = mlIt.next().getUsers().getUserId();
			queryStr = "SELECT COUNT(distinct emailId) FROM Contacts WHERE users = " + userId
					+ " AND bitwise_and(mlBits," + mlsbit + " )>0 AND emailId IS NOT NULL AND emailStatus='" + status
					+ "' ";
		}

		long count = ((Long) (executeQuery(queryStr).get(0))).longValue();
		return count;

	}

	// By lavanya
	public long getUniqueMobileCount(Set<MailingList> mlSet, String status) {

		Iterator<MailingList> mlIt = mlSet.iterator();

		long mlsbit = Utility.getMlsBit(mlSet);
		String queryStr = Utility.getMultiUserQry(mlSet, mlsbit);

		if (queryStr != null) {
			queryStr = "SELECT COUNT( c.mobilePhone) " + queryStr + "AND c.mobilePhone IS NOT NULL AND c.mobileStatus='"
					+ status + "' ";

		} else {
			Long userId = mlIt.next().getUsers().getUserId();
			queryStr = "SELECT COUNT(mobilePhone) FROM Contacts WHERE users = " + userId + " AND bitwise_and(mlBits,"
					+ mlsbit + " )>0 AND mobilePhone IS NOT NULL AND mobileStatus='" + status + "' ";
		}

		logger.info("qry>>>>>>>" + queryStr);
		long count = ((Long) (executeQuery(queryStr).get(0))).longValue();
		return count;

	}

	public long getAllEmailCount(Set<MailingList> mlSet) {

		Iterator<MailingList> mlIt = mlSet.iterator();

		long mlsbit = Utility.getMlsBit(mlSet);
		String queryStr = Utility.getMultiUserQry(mlSet, mlsbit);

		if (queryStr != null) {
			queryStr = "SELECT COUNT(DISTINCT c.emailId) " + queryStr;

		} else {
			Long userId = mlIt.next().getUsers().getUserId();
			queryStr = "SELECT COUNT(emailId) FROM Contacts WHERE users = " + userId + " AND bitwise_and(mlBits,"
					+ mlsbit + " )>0";
		}

		long count = ((Long) (executeQuery(queryStr).get(0))).longValue();
		return count;

	}

	// By Lavanya
	public long getAllSMSCount(Set<MailingList> mlSet) {

		Iterator<MailingList> mlIt = mlSet.iterator();

		long mlsbit = Utility.getMlsBit(mlSet);
		String queryStr = Utility.getMultiUserQry(mlSet, mlsbit);

		if (queryStr != null) {
			queryStr = "SELECT COUNT(DISTINCT c.mobilePhone) " + queryStr;

		} else {
			Long userId = mlIt.next().getUsers().getUserId();
			queryStr = "SELECT COUNT(mobilePhone) FROM Contacts WHERE users = " + userId + " AND bitwise_and(mlBits,"
					+ mlsbit + " )>0";
		}

		long count = ((Long) (executeQuery(queryStr).get(0))).longValue();
		return count;

	}
	//APP-4288
	public long getAllWACount(Set<MailingList> mlSet) {

		Iterator<MailingList> mlIt = mlSet.iterator();

		long mlsbit = Utility.getMlsBit(mlSet);
		String queryStr = Utility.getMultiUserQry(mlSet, mlsbit);

		if (queryStr != null) {
			queryStr = "SELECT COUNT(DISTINCT c.mobilePhone) " + queryStr;

		} else {
			Long userId = mlIt.next().getUsers().getUserId();
			queryStr = "SELECT COUNT(mobilePhone) FROM Contacts WHERE users = " + userId + " AND bitwise_and(mlBits,"
					+ mlsbit + " )>0";
		}

		long count = ((Long) (executeQuery(queryStr).get(0))).longValue();
		return count;

	}

	// TODO :proumya do this work
	public long getAllMobileCount(Set<MailingList> mlSet, boolean isEntireList) {

		try {
			Iterator<MailingList> mlIt = mlSet.iterator();

			long mlsbit = Utility.getMlsBit(mlSet);
			String queryStr = Utility.getMultiUserQry(mlSet, mlsbit);

			if (queryStr != null) {
				queryStr = "SELECT COUNT(DISTINCT c.mobilePhone) " + queryStr + " AND c.mobilePhone IS NOT NULL"
						+ " AND c.mobilePhone!='' AND c.mobileStatus='" + Constants.CON_MOBILE_STATUS_ACTIVE + "'"
						+ (isEntireList ? "" : " AND c.mobileOptin=1") + "  ";

			} else {
				Long userId = mlIt.next().getUsers().getUserId();
				/*
				 * queryStr = "SELECT COUNT(DISTINCT mobilePhone) FROM Contacts WHERE users = "+
				 * userId + " AND bitwise_and(mlBits," + mlsbit +
				 * " )>0"+" AND mobilePhone IS NOT NULL" +
				 * " AND mobilePhone!='' AND mobileStatus='"+Constants.CON_MOBILE_STATUS_ACTIVE+
				 * "'"+(isEntireList ? "" : " AND mobileOptin=1") +"  ";
				 */
				queryStr = "SELECT COUNT(d.cnt) FROM ( SELECT COUNT(mobile_phone) as cnt FROM contacts WHERE user_id = "
						+ userId + " AND mlbits&" + mlsbit + ">0" + " AND mobile_phone IS NOT NULL"
						+ " AND mobile_phone!='' AND mobile_status='" + Constants.CON_MOBILE_STATUS_ACTIVE + "'"
						+ (isEntireList ? "" : " AND mobile_opt_in=1") + " group by mobile_phone) as d  ";

			}

			// long count = ((Long) (executeQuery(queryStr).get(0))).longValue();
			long count = jdbcTemplate.queryForInt(queryStr);
			return count;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("** Exception -  ", e);
		}
		return 0;

	}

	public long getAllContactsCount(Long userId) {
		/*
		 * Iterator<MailingList> mlIt = mlSet.iterator(); String idsStr = "";
		 * while(mlIt.hasNext()) {
		 * 
		 * if(idsStr.length() > 0) idsStr += ","; idsStr +=
		 * mlIt.next().getListId().longValue()+"";
		 * 
		 * 
		 * }
		 */

		/*
		 * Iterator<MailingList> mlIt = mlSet.iterator(); long mlsbit =
		 * Utility.getMlsBit(mlSet);
		 * 
		 * String queryStr = Utility.getMultiUserQry(mlSet, mlsbit);
		 * 
		 * if(queryStr != null) { queryStr =
		 * "SELECT COUNT(DISTINCT c.contactId) "+queryStr; } else {
		 * 
		 * MailingList ml = mlIt.next(); Users mlUser = ml.getUsers();
		 * 
		 * 
		 * Long userId = mlUser.getParentUser() != null ?
		 * mlUser.getParentUser().getUserId() : mlUser.getUserId() ;
		 * 
		 * 
		 * queryStr = " SELECT COUNT(c.contactId) FROM Contacts c WHERE c.users = "+
		 * userId + " AND bitwise_and(c.mlBits," + mlsbit + " )>0"; }
		 */
		/*
		 * String queryStr =
		 * " SELECT COUNT(DISTINCT c.contactId) FROM Contacts c , MailingList m WHERE c.users = m.users AND m.listId IN("
		 * +idsStr+")"+ " AND bitwise_and(c.mlBits, m.mlBit  )>0";
		 */

		//APP-4421
		/*String queryStr =  " SELECT COUNT(c.contactId) FROM Contacts c WHERE c.users = " + userId
						+ " AND c.mlBits > 0 AND ( emailStatus = 'Active' OR mobileStatus = 'Active'  "
						+ "OR contactId in (SELECT DISTINCT contact FROM ContactsLoyalty  WHERE userId = "
						+ userId + " AND membershipStatus ='Active' ) )";*/

		String queryStr = " SELECT COUNT(c.contactId) FROM Contacts c WHERE c.users = " + userId
				+ " AND c.mlBits > 0 AND ( emailStatus = 'Active' OR mobileStatus = 'Active' OR loyaltyCustomer = 1)" ;
		
		//old query
		/*String queryStr = " SELECT COUNT(c.contactId) FROM Contacts c WHERE c.users = " + userId
				+ " AND c.mlBits > 0 AND ( emailStatus = 'Active' OR mobileStatus = 'Active' OR loyaltyCustomer = 1 OR contactId in (SELECT DISTINCT contact FROM ContactsLoyalty  WHERE userId = "
				+ userId + " AND (membershipStatus ='Active' OR membershipStatus is NULL)) )";*/
		
		logger.info("getAllContactsCount >>>"+queryStr);

		return (((Long) (getHibernateTemplate().find(queryStr)).get(0)).longValue());
	}

	public long getAllContactsCount(Set<MailingList> mlSet) {
		Iterator<MailingList> mlIt = mlSet.iterator();
		String idsStr = "";
		while (mlIt.hasNext()) {

			if (idsStr.length() > 0)
				idsStr += ",";
			idsStr += mlIt.next().getListId().longValue() + "";

		}

		/*
		 * Iterator<MailingList> mlIt = mlSet.iterator(); long mlsbit =
		 * Utility.getMlsBit(mlSet);
		 * 
		 * String queryStr = Utility.getMultiUserQry(mlSet, mlsbit);
		 * 
		 * if(queryStr != null) { queryStr =
		 * "SELECT COUNT(DISTINCT c.contactId) "+queryStr; } else {
		 * 
		 * MailingList ml = mlIt.next(); Users mlUser = ml.getUsers();
		 * 
		 * 
		 * Long userId = mlUser.getParentUser() != null ?
		 * mlUser.getParentUser().getUserId() : mlUser.getUserId() ;
		 * 
		 * 
		 * queryStr = " SELECT COUNT(c.contactId) FROM Contacts c WHERE c.users = "+
		 * userId + " AND bitwise_and(c.mlBits," + mlsbit + " )>0"; }
		 */
		String queryStr = " SELECT COUNT(DISTINCT c.contactId) FROM Contacts c , MailingList m WHERE c.users = m.users AND m.listId IN("
				+ idsStr + ")" + " AND bitwise_and(c.mlBits, m.mlBit  )>0";

		return (((Long) (getHibernateTemplate().find(queryStr)).get(0)).longValue());
	}
	/*
	 * public long getAllContactsCount(String mlIds) { return jdbcTemplate.
	 * queryForLong("select count(cid) from contacts_mlists where list_id in ("+
	 * mlIds + ")");
	 * 
	 * }
	 */

	public long getAllUnpurgedCount(Set<MailingList> mlSet) {

		Iterator<MailingList> mlIt = mlSet.iterator();
		long mlsbit = Utility.getMlsBit(mlSet);

		String queryStr = Utility.getMultiUserQry(mlSet, mlsbit);

		if (queryStr != null) {
			queryStr = "SELECT COUNT(DISTINCT c.emailId) " + queryStr + " AND purged is false";
		} else {
			Long userId = mlIt.next().getUsers().getUserId();
			queryStr = "SELECT COUNT(emailId) FROM Contacts  WHERE users = " + userId + " AND bitwise_and(mlBits,"
					+ mlsbit + " )>0 AND purged is false";
		}

		return (((Long) (getHibernateTemplate().find(queryStr)).get(0)).longValue());

	}

	public int getSegmentedContactsCount(String qry) {
		try {
			String countQry = "SELECT COUNT(*) FROM (" + qry + ") AS tempCount";
			return jdbcTemplate.queryForInt(countQry);
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			logger.error(" Exception : ", (Throwable) e);
			return 0;
		}
	}

	public List<Contacts> getUnpurgedContactsByListId(MailingList mList, Long contactId, int size) {
		try {
			String queryStr = " FROM Contacts WHERE users = " + mList.getUsers().getUserId()
					+ " AND bitwise_and(mlBits, " + mList.getMlBit() + ") > 0" + " AND purged=false AND contactId >"
					+ contactId + " ORDER BY contactId";
			return executeQuery(queryStr, 0, size);

			// return getHibernateTemplate().find("(FROM Contacts c WHERE c.mailingList ="+
			// listId+" AND c.purged=false) LIMIT "+startIndex+","+size);

		} catch (DataAccessException e) {
			// TODO: handle exception
			logger.error("** Exception : while getting unpurged contacts size by size", (Throwable) e);
			return null;
		}
	}

	public List<Contacts> findAllByMlId(MailingList mList) {
		logger.debug("---just entered----");
		List<Contacts> contacts = null;
		try {
			contacts = getHibernateTemplate().find(" FROM  Contacts WHERE users = " + mList.getUsers().getUserId()
					+ " AND bitwise_and(mlBits, " + mList.getMlBit() + ") > 0 AND mList.formMappingFlag=" + true);
		} catch (Exception e) {
			// TODO: handle exception
			logger.debug("error in formmappingDao");
		}
		logger.debug("=======>" + contacts.size());
		if (contacts.size() > 0) {
			return contacts;
		}
		return null;
	}

	public boolean findByEmailId(String emailId, MailingList mList) {

		logger.debug("just enter ContactDao");
		List<Contacts> contactsList = null;
		String qryStr = " FROM Contacts WHERE users = " + mList.getUsers().getUserId() + " AND bitwise_and(mlBits, "
				+ mList.getMlBit() + ") > 0 AND emailId = '" + emailId;
		contactsList = executeQuery(qryStr);
		if (contactsList.size() > 0) {
			return true;
		} else {
			return false;
		}

	}

	public Contacts findContactByEmailId(String emailId, Long userId) {

		logger.debug("just enter ContactDao");
		List<Contacts> contactsList = null;
		String qryStr = "SELECT DISTINCT c FROM Contacts c WHERE c.emailId='" + emailId + "'AND c.users=" + userId;
		contactsList = executeQuery(qryStr);
		if (contactsList.size() > 0) {
			return (Contacts) contactsList.get(0);
		} else {
			return null;
		}

	}
	public Contacts findContactByCid(Long cid, Long userId) {

		logger.debug("just enter ContactDao");
		List<Contacts> contactsList = null;
		String qryStr = "SELECT DISTINCT c FROM Contacts c WHERE c.contactId='" +cid+ "'AND c.users=" + userId;
	
		logger.info("qryStr value is "+qryStr);
		
		contactsList = executeQuery(qryStr);
		if (contactsList.size() > 0) {
			return (Contacts) contactsList.get(0);
		} else {
			return null;
		}

	}

	public Contacts findContactByPhone(String phoneId, Long userId) {

		List<Contacts> contactsList = null;
		String qryStr = "SELECT DISTINCT c FROM Contacts c WHERE c.mobilePhone='" + phoneId + "' AND c.users=" + userId;
		contactsList = executeQuery(qryStr);
		if (contactsList.size() > 0) {
			return (Contacts) contactsList.get(0);
		} else {
			return null;
		}

	}

	/**
	 * this method retrieves the contacts from DB based on the given emailstatus of
	 * contacts(called in ViewContactsController)
	 * 
	 * @param id
	 * @param status
	 * @param firstResult
	 * @param maxResults
	 * @return
	 */
	public List<Contacts> findByStatus(MailingList mList, String status, int firstResult, int maxResults,
			String mobileStatus) {
		logger.debug("---just entered---" + status);
		List<Contacts> contactList = null;

		try {
			String query = "SELECT DISTINCT c FROM Contacts c WHERE users = " + mList.getUsers().getUserId()
					+ " AND bitwise_and(mlBits, " + mList.getMlBit() + ") > 0 AND c.emailStatus='" + status
					+ "' + AND c.mobileStatus='" + mobileStatus + "' ORDER BY c.emailId";
			contactList = executeQuery(query, firstResult, maxResults);
			logger.debug("Exiting before return");
			return contactList;
		} catch (Exception e) {
			logger.error("** Error : " + e.getMessage() + " **");
			return null;
		}

	}

	public long findAllByStatus(MailingList mList, String status, String mobStatus) {
		long count = 0;
		String query = "SELECT COUNT(DISTINCT c.contactId) FROM Contacts c WHERE users = "
				+ mList.getUsers().getUserId() + " AND bitwise_and(mlBits, " + mList.getMlBit()
				+ ") > 0 AND c.emailStatus='" + status + "'  AND c.mobileStatus = '" + mobStatus
				+ "' ORDER BY c.emailId";
		count = ((Long) (executeQuery(query).get(0))).longValue();
		return count;

	}

	/**
	 * this method retrieves the contacts from DB based on the emailstatus given
	 * SearchString of emailid(called in ViewContactsController)
	 * 
	 * @param id
	 * @param searchstring
	 * @param status
	 * @param firstResult
	 * @param maxResults
	 * @return
	 */
	public List<Contacts> findByemailIdAndMobile(MailingList mList, String searchString, String status, int firstResult,
			int maxResults, String mobStr, String mobStatus) {
		logger.debug("---just entered---");
		List<Contacts> contacts = null;
		try {

			String subQry = "";

			if (!searchString.isEmpty()) {

				subQry += " AND c.emailId like '%" + searchString + "%'";

			}

			if (!mobStr.isEmpty()) {

				subQry += " AND c.mobilePhone ='" + mobStr + "'";

			}

			if (!status.equalsIgnoreCase("--All--")) {

				subQry += " AND c.emailStatus = '" + status + "'";
			}

			if (!mobStatus.equalsIgnoreCase("--All--")) {
				if (mobStatus.equalsIgnoreCase(Constants.CON_MOBILE_STATUS_PENDING)) {

					subQry += " AND c.mobileStatus IS NULL";
				} else {
					subQry += " AND c.mobileStatus = '" + mobStatus + "'";
				}
			}

			String query = "SELECT DISTINCT c FROM Contacts c WHERE c.users = " + mList.getUsers().getUserId()
					+ " AND bitwise_and(c.mlBits, " + mList.getMlBit() + ") > 0 " + subQry;

			/*
			 * if(status.equalsIgnoreCase("--All--")||
			 * mobStatus.equalsIgnoreCase("--All--")) {
			 * query="from Contacts where mailingList= " + id + " and emailId like '%"+
			 * searchString +"%'+ phone like '%"+mobStr+"%'"; } else {
			 * query="from Contacts where mailingList= " + id + " and emailId like '%"+
			 * searchString
			 * +"%' and emailStatus = '"+status+"'  + and mobileStatus = '"+mobStatus+"'"; }
			 */
			contacts = executeQuery(query, firstResult, maxResults);
			logger.debug("the number of contacts retrieved are>>>>" + contacts.size());
			return contacts;
		} catch (Exception e) {
			logger.debug("Exception while getting the contacts", e);
			return null;
		}

	}// findByemailIdAndMobile

	/**
	 * this overridden method retrieves the contacts from DB based on the
	 * emailstatus given SearchString of emailid(called in ViewContactsController)
	 * 
	 * @param id
	 * @param searchstring
	 * @param status
	 * 
	 * @return
	 */
	public long findAllByemailIdAndMobile(MailingList mList, String searchStr, String status, String mobStr,
			String mobStatus) {
		long count = 0;
		// String query="";

		String subQry = "";

		if (!searchStr.isEmpty()) {

			subQry += " AND c.emailId like '%" + searchStr + "%'";

		}

		if (!mobStr.isEmpty()) {

			subQry += " AND c.mobilePhone ='" + mobStr + "'";

		}

		if (!status.equalsIgnoreCase("--All--")) {

			subQry += " AND c.emailStatus = '" + status + "'";
		}

		if (!mobStatus.equalsIgnoreCase("--All--")) {
			if (mobStatus.equalsIgnoreCase(Constants.CON_MOBILE_STATUS_PENDING)) {

				subQry += " AND c.mobileStatus IS NULL";
			} else {
				subQry += " AND c.mobileStatus = '" + mobStatus + "'";
			}
		}

		String query = " SELECT COUNT(c.contactId) FROM Contacts c WHERE c.users = " + mList.getUsers().getUserId()
				+ " AND bitwise_and(c.mlBits, " + mList.getMlBit() + ") > 0 " + subQry;

		/*
		 * if(status.equalsIgnoreCase("--All--")||
		 * mobStatus.equalsIgnoreCase("--All--")) {
		 * query="select count(contactId) from Contacts where mailingList= " + listId +
		 * " and emailId like '%"+ searchStr +"%' + phone like '%"+mobStr+"%' " ; } else
		 * { query="select count(contactId) from Contacts where mailingList= " + listId
		 * + " and emailId like '%"+ searchStr
		 * +"%' and emailStatus = '"+status+"' + and mobileStatus = '"+mobStatus+"'" ; }
		 */
		count = ((Long) (executeQuery(query).get(0))).longValue();
		return count;
	}// findAllByemailIdAndMobile

	public long getAllContactsByPhn(Set<MailingList> mlSet, boolean flag) {
		long count = 0;
		String query = "";
		Iterator<MailingList> mlIt = mlSet.iterator();
		Long userId = mlIt.next().getUsers().getUserId();
		long mlsbit = Utility.getMlsBit(mlSet);

		try {
			if (!flag) {
				query = "SELECT COUNT(DISTINCT contactId) FROM Contacts WHERE users = " + userId
						+ " AND bitwise_and(mlBits," + mlsbit + " )>0 " + " AND mobilePhone IS NOT NULL";
			} else {
				query = "SELECT COUNT(DISTINCT contactId) FROM Contacts WHERE users = " + userId
						+ " AND bitwise_and(mlBits," + mlsbit + " )>0 ";

			}
			count = ((Long) (executeQuery(query).get(0))).longValue();
			logger.debug("the total contacts are " + count);
			return count;
		} catch (Exception e) {
			logger.error("Exception ::", e);
			return 0;
		}
	}

	public List<Contacts> findContactByListId(MailingList mList) {

		String query = " FROM Contacts WHERE users = " + mList.getUsers().getUserId() + " AND bitwise_and(mlBits, "
				+ mList.getMlBit() + ") > 0 ORDER BY emailId";

		List<Contacts> cList = executeQuery(query);
		logger.info("---------::" + cList);
		return cList;
	}

	public Contacts findContctsFromListId(String mailiId, MailingList mList) {

		String query = " FROM Contacts WHERE users = " + mList.getUsers().getUserId() + " AND bitwise_and(mlBits, "
				+ mList.getMlBit() + ") > 0 AND emailId  = '" + mailiId + "'";
		List<Contacts> cList = executeQuery(query);
		// logger.info("---------::"+cList);
		if (cList.size() > 0)
			return (Contacts) cList.get(0);
		else
			return null;
	}

	public long getOptinContactsCount(long userId, Calendar fromCal, Calendar toCal) {
		try {

			String fromStr = MyCalendar.calendarToString(fromCal, MyCalendar.FORMAT_DATETIME_STYEAR);
			String toStr = MyCalendar.calendarToString(toCal, MyCalendar.FORMAT_DATETIME_STYEAR);
			/*
			 * Iterator<MailingList> mlIt = mlSet.iterator(); long mlsbit =
			 * Utility.getMlsBit(mlSet);
			 * 
			 * String queryStr = Utility.getMultiUserQry(mlSet, mlsbit);
			 * 
			 * if(queryStr != null) { queryStr =
			 * "SELECT COUNT(DISTINCT c.contactId) "+queryStr+
			 * " AND c.createdDate BETWEEN '"+fromStr+"' AND '"
			 * +toStr+"'  AND c.emailStatus = '"+Constants.CONT_STATUS_ACTIVE+"'"; } else {
			 * Long userId = mlIt.next().getUsers().getUserId(); queryStr =
			 * "SELECT COUNT(DISTINCT c.contactId) FROM Contacts c WHERE c.users = "+ userId
			 * + " AND bitwise_and(c.mlBits," + mlsbit + " )>0"+
			 * " AND c.createdDate BETWEEN '"+fromStr+"' AND '"
			 * +toStr+"'  AND c.emailStatus = '"+Constants.CONT_STATUS_ACTIVE+"'"; }
			 */
			String queryStr = " SELECT COUNT(c.contactId) FROM Contacts c WHERE c.users = " + userId
					+ " AND c.mlBits > 0 AND c.createdDate BETWEEN '" + fromStr + "' AND '" + toStr
					+ "'  AND ( c.emailStatus = '" + Constants.CONT_STATUS_ACTIVE + "'" + " OR c.mobileStatus = '"
					+ Constants.CONT_STATUS_ACTIVE + "' )";

			List list = getHibernateTemplate().find(queryStr);

			if (list.size() > 0) {
				return ((Long) list.get(0)).longValue();
			} // if

		} catch (Exception e) {
			logger.error("Exception ::", e);
		}
		return 0;
	} //

	public long getOptOutContactsCount(Long userId, Calendar fromCal, Calendar toCal) {
		try {
			String fromStr = MyCalendar.calendarToString(fromCal, MyCalendar.FORMAT_DATETIME_STYEAR);
			String toStr = MyCalendar.calendarToString(toCal, MyCalendar.FORMAT_DATETIME_STYEAR);

			/*
			 * Iterator<MailingList> mlIt = mlSet.iterator(); long mlsbit =
			 * Utility.getMlsBit(mlSet); String queryStr = Utility.getMultiUserQry(mlSet,
			 * mlsbit);
			 * 
			 * if(queryStr != null) { queryStr =
			 * "SELECT COUNT(DISTINCT c.contactId) "+queryStr+
			 * " AND c.lastStatusChange BETWEEN '"+fromStr+"' AND '"
			 * +toStr+"' AND c.emailStatus = '"+Constants.CONT_STATUS_UNSUBSCRIBED+"'"; }
			 * else { Long userId = mlIt.next().getUsers().getUserId(); queryStr =
			 * "SELECT COUNT(DISTINCT c.contactId) FROM Contacts c WHERE users = "+ userId +
			 * " AND bitwise_and(mlBits," + mlsbit + " )>0 " +
			 * " AND c.lastStatusChange BETWEEN '"+fromStr+"' AND '"
			 * +toStr+"' AND c.emailStatus = '"+Constants.CONT_STATUS_UNSUBSCRIBED+"'"; }
			 */

			String queryStr = " SELECT COUNT(c.contactId) FROM Contacts c WHERE c.users = " + userId
					+ " AND c.mlBits > 0 AND c.lastStatusChange BETWEEN '" + fromStr + "' AND '" + toStr
					+ "'  AND ( c.emailStatus = '" + Constants.CONT_STATUS_UNSUBSCRIBED + "'" + " OR c.mobileStatus = '"
					+ Constants.CON_MOBILE_STATUS_OPTED_OUT + "' )";

			List list = getHibernateTemplate().find(queryStr);

			if (list.size() > 0) {
				return ((Long) list.get(0)).longValue();
			} // if

		} catch (Exception e) {
			logger.error("Exception ::", e);
		}
		return 0;
	} //

	/*
	 * public List<Object[]> findContactPurchaseDetails(long mailingListId, String
	 * customerId) {
	 * 
	 * try { logger.info("customerId ::"+ customerId
	 * +" >>> mailingListId ::"+mailingListId); List<Object[]> purcahseList = null;
	 * String qry
	 * ="SELECT tot_purchase_amt,max_purchase_amt,tot_reciept_count FROM sales_aggregate_data "
	 * + "WHERE list_id ="+mailingListId+" AND customer_id ='"+customerId+"'"; //
	 * logger.info("query is ::"+qry); purcahseList = jdbcTemplate.query(qry, new
	 * RowMapper() { public Object mapRow(ResultSet rs, int rowNum) throws
	 * SQLException {
	 * 
	 * Object[] promOCodeObjArr = new Object[3];; promOCodeObjArr[0] =
	 * rs.getDouble(1); promOCodeObjArr[1] = rs.getDouble(2); promOCodeObjArr[2] =
	 * rs.getInt(3);
	 * 
	 * return promOCodeObjArr; }
	 * 
	 * }); if(purcahseList != null && purcahseList.size() >0) {
	 * logger.info("purcahseList.size() >>>>>>"+purcahseList.size()); return
	 * purcahseList; }else return null; } catch (DataAccessException e) {
	 * logger.error("Exception ::" , e); return null; }
	 * 
	 * } // findPromocodeList
	 */

	public List<Map<String, Object>> findContactPurchaseDetails(Users user, Long contactId) {

		try {
			List<Object[]> purcahseList = null;
			String qry = "";
			//check the user FTP settings are exists, if yes then the second query otherwise first.
			int  posSettingList = jdbcTemplate.queryForInt("select count(*) FROM sales_aggregate_data "
					+ "WHERE user_id =" + user.getUserId().longValue() + " AND cid =" + contactId.longValue());
	    	
	    	if( posSettingList >0) {
	    		
	    		qry = "SELECT tot_purchase_amt,max_purchase_amt,tot_reciept_count FROM sales_aggregate_data "
						+ "WHERE user_id =" + user.getUserId().longValue() + " AND cid =" + contactId.longValue();
	    	}else{
	    		
	    		if(user.getCountryType().equalsIgnoreCase(Constants.SMS_COUNTRY_INDIA)) {
					
	    			qry =  "SELECT SUM(totsal.tot_purchase_amt) as tot_purchase_amt, "
	    					+ "max(totsal.tot_purchase_amt) as max_purchase_amt, count(totsal.tot_reciept_count)  as tot_reciept_count "
	    					+ "from (select cid, sum((sales_price*quantity)-(IF(discount is null,0,discount))) as tot_purchase_amt,"
	    					+ "  doc_sid as tot_reciept_count FROM retail_pro_sales "
	    					+ " WHERE user_id="+user.getUserId().longValue() + " AND cid =" + contactId.longValue()+"	GROUP BY doc_sid) as totsal group by totsal.cid";
	    			
				}else {
					
	    			qry =  "SELECT SUM(totsal.tot_purchase_amt) as tot_purchase_amt, "
	    					+ "max(totsal.tot_purchase_amt) as max_purchase_amt, count(totsal.tot_reciept_count)  as tot_reciept_count "
	    					+ "from (select cid, sum((sales_price*quantity)+tax-(IF(discount is null,0,discount))) as tot_purchase_amt,"
	    					+ "  doc_sid as tot_reciept_count FROM retail_pro_sales "
	    					+ " WHERE user_id="+user.getUserId().longValue() + " AND cid =" + contactId.longValue()+"	GROUP BY doc_sid) as totsal group by totsal.cid";
	    			

				}
	    	}
			
			// logger.info("query is ::"+qry);
			return jdbcTemplate.queryForList(qry);

			/*
			 * purcahseList = jdbcTemplate.query(qry, new RowMapper() { public Object
			 * mapRow(ResultSet rs, int rowNum) throws SQLException {
			 * 
			 * Object[] promOCodeObjArr = new Object[3];; promOCodeObjArr[0] =
			 * rs.getDouble(1); promOCodeObjArr[1] = rs.getDouble(2); promOCodeObjArr[2] =
			 * rs.getInt(3);
			 * 
			 * return promOCodeObjArr; }
			 * 
			 * });
			 */
			/*
			 * if(purcahseList != null && purcahseList.size() >0) { return purcahseList;
			 * }else return null;
			 */
		} catch (DataAccessException e) {
			logger.error("Exception ::", e);
			return null;
		}

	} // findPromocodeList
	public List<Contacts> getSegmentedLtyContacts(String query, int startIndex, int count) {


		List<Contacts> list = null;
		try {

			list = jdbcTemplate.query(query + " LIMIT " + startIndex + ", " + count, new RowMapper() {

				public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
					Contacts contact = new Contacts();
					contact.setContactId(rs.getLong("cid"));
					contact.setEmailId(rs.getString("email_id"));
					contact.setFirstName(rs.getString("first_name"));
					contact.setLastName(rs.getString("last_name"));
					contact.setAddressOne(rs.getString("address_one"));
					contact.setAddressTwo(rs.getString("address_two"));
					contact.setCity(rs.getString("city"));
					contact.setState(rs.getString("state"));
					contact.setCountry(rs.getString("country"));
					// contact.setPin(rs.getInt("pin"));
					contact.setZip(rs.getString("zip"));
					contact.setPurged(rs.getBoolean("purged"));
					contact.setOptin(rs.getByte("optin"));
					// contact.setPhone(rs.getLong("phone"));
					contact.setMobilePhone(rs.getString("mobile_phone"));
					contact.setGender(rs.getString("gender"));
					contact.setHomeStore(rs.getString("home_store"));
					contact.setExternalId(rs.getString("external_id"));
					contact.setHpId(rs.getLong("hp_id"));
					contact.setLoyaltyCustomer(rs.getByte("loyalty_customer"));

					contact.setMobileStatus(rs.getString("mobile_status"));
					Users user = new Users();
					user.setUserId(rs.getLong("user_id"));
					contact.setUsers(user);
					contact.setMlBits(rs.getLong("mlbits"));

					Calendar cal = null;
					if (rs.getDate("birth_day") != null) {
						cal = Calendar.getInstance();
						cal.setTime(rs.getTimestamp("birth_day"));
						contact.setBirthDay(cal);
					} else {
						contact.setBirthDay(null);
					}

					if (rs.getDate("anniversary_day") != null) {
						cal = Calendar.getInstance();
						cal.setTime(rs.getTimestamp("anniversary_day"));
						contact.setAnniversary(cal);
					} else {
						contact.setAnniversary(null);
					}
					if (rs.getDate("created_date") != null) {
						cal = Calendar.getInstance();
						cal.setTime(rs.getTimestamp("created_date"));
						contact.setCreatedDate(cal);
					} else {
						contact.setCreatedDate(null);
					}
					if (rs.getDate("last_status_change") != null) {
						cal = Calendar.getInstance();
						cal.setTime(rs.getTimestamp("last_status_change"));
						contact.setLastStatusChange(cal);
					} else {
						contact.setLastStatusChange(null);
					}
					if (rs.getDate("last_mail_date") != null) {
						cal = Calendar.getInstance();
						cal.setTime(rs.getTimestamp("last_mail_date"));
						contact.setLastMailDate(cal);
					} else {
						contact.setLastMailDate(null);
					}
					cal = Calendar.getInstance();

					contact.setEmailStatus(rs.getString("email_status"));
					contact.setOptin(rs.getByte("optin"));
					contact.setSubscriptionType(rs.getString("subscription_type"));
					contact.setUdf1(rs.getString("udf1"));
					contact.setUdf2(rs.getString("udf2"));
					contact.setUdf3(rs.getString("udf3"));
					contact.setUdf4(rs.getString("udf4"));
					contact.setUdf5(rs.getString("udf5"));
					contact.setUdf6(rs.getString("udf6"));
					contact.setUdf7(rs.getString("udf7"));
					contact.setUdf8(rs.getString("udf8"));
					contact.setUdf9(rs.getString("udf9"));
					contact.setUdf10(rs.getString("udf10"));
					contact.setUdf11(rs.getString("udf11"));
					contact.setUdf12(rs.getString("udf12"));
					contact.setUdf13(rs.getString("udf13"));
					contact.setUdf14(rs.getString("udf14"));
					contact.setUdf15(rs.getString("udf15"));
					
					contact.setCardNumber(rs.getString("card_number"));
					contact.setCardPin(rs.getString("card_pin"));
					contact.setHoldCurrency(rs.getDouble("holdAmount_balance"));
					contact.setHoldPoints(rs.getDouble("holdpoints_balance"));
					contact.setRewardBalance(rs.getDouble("giftcard_balance"));
					contact.setPoints(rs.getDouble("loyalty_balance"));
					contact.setGiftBalance(rs.getDouble("gift_balance"));
					
					
					
					
					return contact;
				}
			});
		} catch (Exception e) {
			logger.error("Exception ::", e);
		}
		return list;
	
	
	}
	public List<Contacts> getSegmentedContacts(String query, int startIndex, int count) {

		List<Contacts> list = null;
		try {

			list = jdbcTemplate.query(query + " LIMIT " + startIndex + ", " + count, new RowMapper() {

				public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
					Contacts contact = new Contacts();
					contact.setContactId(rs.getLong("cid"));
					contact.setEmailId(rs.getString("email_id"));
					contact.setFirstName(rs.getString("first_name"));
					contact.setLastName(rs.getString("last_name"));
					contact.setAddressOne(rs.getString("address_one"));
					contact.setAddressTwo(rs.getString("address_two"));
					contact.setCity(rs.getString("city"));
					contact.setState(rs.getString("state"));
					contact.setCountry(rs.getString("country"));
					// contact.setPin(rs.getInt("pin"));
					contact.setZip(rs.getString("zip"));
					contact.setPurged(rs.getBoolean("purged"));
					contact.setOptin(rs.getByte("optin"));
					// contact.setPhone(rs.getLong("phone"));
					contact.setMobilePhone(rs.getString("mobile_phone"));
					contact.setGender(rs.getString("gender"));
					contact.setHomeStore(rs.getString("home_store"));
					contact.setExternalId(rs.getString("external_id"));
					contact.setHpId(rs.getLong("hp_id"));
					contact.setLoyaltyCustomer(rs.getByte("loyalty_customer"));

					contact.setMobileStatus(rs.getString("mobile_status"));
					Users user = new Users();
					user.setUserId(rs.getLong("user_id"));
					contact.setUsers(user);
					contact.setMlBits(rs.getLong("mlbits"));

					Calendar cal = null;
					if (rs.getDate("birth_day") != null) {
						cal = Calendar.getInstance();
						cal.setTime(rs.getTimestamp("birth_day"));
						contact.setBirthDay(cal);
					} else {
						contact.setBirthDay(null);
					}

					if (rs.getDate("anniversary_day") != null) {
						cal = Calendar.getInstance();
						cal.setTime(rs.getTimestamp("anniversary_day"));
						contact.setAnniversary(cal);
					} else {
						contact.setAnniversary(null);
					}
					if (rs.getDate("created_date") != null) {
						cal = Calendar.getInstance();
						cal.setTime(rs.getTimestamp("created_date"));
						contact.setCreatedDate(cal);
					} else {
						contact.setCreatedDate(null);
					}
					if (rs.getDate("last_status_change") != null) {
						cal = Calendar.getInstance();
						cal.setTime(rs.getTimestamp("last_status_change"));
						contact.setLastStatusChange(cal);
					} else {
						contact.setLastStatusChange(null);
					}
					if (rs.getDate("last_mail_date") != null) {
						cal = Calendar.getInstance();
						cal.setTime(rs.getTimestamp("last_mail_date"));
						contact.setLastMailDate(cal);
					} else {
						contact.setLastMailDate(null);
					}
					cal = Calendar.getInstance();

					contact.setEmailStatus(rs.getString("email_status"));
					contact.setOptin(rs.getByte("optin"));
					contact.setSubscriptionType(rs.getString("subscription_type"));
					contact.setUdf1(rs.getString("udf1"));
					contact.setUdf2(rs.getString("udf2"));
					contact.setUdf3(rs.getString("udf3"));
					contact.setUdf4(rs.getString("udf4"));
					contact.setUdf5(rs.getString("udf5"));
					contact.setUdf6(rs.getString("udf6"));
					contact.setUdf7(rs.getString("udf7"));
					contact.setUdf8(rs.getString("udf8"));
					contact.setUdf9(rs.getString("udf9"));
					contact.setUdf10(rs.getString("udf10"));
					contact.setUdf11(rs.getString("udf11"));
					contact.setUdf12(rs.getString("udf12"));
					contact.setUdf13(rs.getString("udf13"));
					contact.setUdf14(rs.getString("udf14"));
					contact.setUdf15(rs.getString("udf15"));
					
					/*contact.setCardNumber(rs.getString("card_number"));
					contact.setCardPin(rs.getString("card_pin"));
					contact.setHoldCurrency(rs.getDouble("holdAmount_balance"));
					contact.setHoldPoints(rs.getDouble("holdpoints_balance"));
					contact.setRewardBalance(rs.getDouble("giftcard_balance"));
					contact.setPoints(rs.getDouble("loyalty_balance"));
					contact.setGiftBalance(rs.getDouble("gift_balance"));
					*/
					
					
					
					return contact;
				}
			});
		} catch (Exception e) {
			logger.error("Exception ::", e);
		}
		return list;
	}

	public Contacts findContactByValues(String emaiId, String phoneNum, String externalID, Long userId) {

		try {
			List<Contacts> contactIdList = null;

			String query = "";

			if (externalID != null && externalID.trim().length() > 0) {
				query = "FROM Contacts c WHERE c.users=" + userId + " AND c.externalId ='" + externalID + "' ";
			} else if (emaiId != null && emaiId.trim().length() > 0) {
				query = "FROM Contacts c WHERE c.users=" + userId + " AND c.emailId ='" + emaiId + "' ";
			} else if (phoneNum != null && phoneNum.trim().length() > 0) {
				query = "FROM Contacts c WHERE c.users=" + userId + " AND c.mobilePhone ='" + phoneNum + "' ";
			}

			logger.info("query ::" + query);

			if (query.trim().equals("")) {
				return null;
			}
			contactIdList = getHibernateTemplate().find(query);

			if (contactIdList.size() > 0) {
				return contactIdList.get(0);
			} else {

				return null;
			}
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
			return null;
		}

	}

	public Contacts findById(Long cid) {

		String query = "FROM Contacts WHERE contactId=" + cid;
		List<Contacts> list = getHibernateTemplate().find(query);
		if (list != null && list.size() > 0) {

			return list.get(0);
		} else
			return null;

	}

	/*
	 * public Contacts findByQuery (String qry,Long list_id) { String query = "";
	 * if(qry != null && qry.trim().length() >0){ query =
	 * "select c FROM Contacts c join c.mlSet ml WHERE ml.listId = "+list_id+
	 * " AND "+qry; } else { query =
	 * "select c FROM Contacts c join c.mlSet ml WHERE ml.listId = "+list_id; }
	 * logger.info("Final Query is ::"+query); List<Contacts> list =
	 * getHibernateTemplate().find(query); if(list.size() > 0) {
	 * 
	 * return list.get(0); } else return null; }
	 */

	public Contacts findByQuery(String qry, Long user_id) {
		String query = "";
		if (qry != null && qry.trim().length() > 0) {
			query = "FROM Contacts WHERE users = " + user_id + " AND " + qry;
		} else {
			query = "FROM Contacts WHERE users = " + user_id;
		}
		// String query = "FROM Contacts WHERE users = "+user_id+ " AND "+qry;
		List<Contacts> list = getHibernateTemplate().find(query);
		if (list.size() > 0) {

			return list.get(0);
		} else
			return null;
	}

	/*
	 * public void addEntryContactsMlists(long cid, long list_id){ try{
	 * jdbcTemplate.execute("insert into contacts_mlists(cid, list_id) values("+cid+
	 * ","+list_id+")"); } catch(Exception e){
	 * logger.error("Exception while inserting entry into contacts_mlists"+e); } }
	 */

	DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public Contacts findContactByPriority(TreeMap<String, List<String>> prioMap, String[] lineStr, Users user) {

		try {
			String query = null;
			String tempQry = null;
			String notQry = "";

			logger.info("prioMap=" + prioMap);
			Set<String> keySet = prioMap.keySet();
			List<Contacts> contactIdList = null;

			List<String> tempList = null;

			outer: for (String eachKey : keySet) {

				query = " FROM Contacts  WHERE users=" + user.getUserId() + " ";
				tempQry = notQry;

				// logger.info("Key ="+eachKey);
				tempList = prioMap.get(eachKey);
				int size = tempList.size();
				for (String eachStr : tempList) {
					int index = Integer.parseInt(eachStr.substring(0, eachStr.indexOf('|')));

					// query += " AND "+ eachStr.substring(eachStr.indexOf('|')+1) + " = '"+
					// lineStr[index] +"' ";
					String[] tempStr = eachStr.split("\\|");
					String valueStr = lineStr[index].trim();

					// logger.info("valueStr=="+valueStr);
					size = size - 1;
					if (valueStr == null || valueStr.trim().isEmpty()) {
						if (size != 0)
							continue;

						continue outer;
					}

					String posMappingDateFormatStr = tempStr[2].trim();

					if (posMappingDateFormatStr.toLowerCase().startsWith("date")
							&& !tempStr[1].trim().toLowerCase().startsWith("udf")) {
						try {
							posMappingDateFormatStr = posMappingDateFormatStr.substring(
									posMappingDateFormatStr.indexOf("(") + 1, posMappingDateFormatStr.indexOf(")"));
							valueStr = format.format((new SimpleDateFormat(posMappingDateFormatStr).parse(valueStr)));
							tempQry += " AND " + tempStr[1] + " = '" + valueStr + "' ";
							if (tempStr[1] != null && tempStr[1].trim().length() > 0) {
								notQry += " AND " + tempStr[1] + " is null ";
							}

							else {
								notQry += "";
							}
						} catch (ParseException e) {
							logger.error("Exception ::", e);
							return null;
						}
					} else if (posMappingDateFormatStr.toLowerCase().startsWith("string")
							|| tempStr[1].trim().toLowerCase().startsWith("udf")) {
						// query += " AND "+ eachStr.substring(eachStr.indexOf('|')+1) + " = '"+
						// lineStr[index] +"' ";

						boolean isIndia = user!=null ? user.getCountryType().equals(Constants.SMS_COUNTRY_INDIA) : false;
						if (tempStr[1].equalsIgnoreCase("Mobile") || tempStr[1].equalsIgnoreCase("mobilePhone")) {

							if (valueStr.startsWith("1")) {
								valueStr = valueStr.substring(1);
							} else if (valueStr.startsWith("91") && valueStr.trim().length() > 10) {
								valueStr = valueStr.substring(valueStr.trim().length() - 10);
							} else if (valueStr.startsWith("92") && valueStr.trim().length() > 10) {
								valueStr = valueStr.substring(valueStr.trim().length() - 10);
							}
							//Changes 2.5.4.0
							else if (!isIndia && valueStr.startsWith("971") && valueStr.trim().length() > 7) {
								valueStr = valueStr
										.substring(valueStr.trim().length() - (valueStr.trim().length() - 3)); // for
																												// uae
							}
							logger.info("isIndia ====="+isIndia);
							valueStr = !isIndia ? "%"+valueStr : valueStr;
						}
						tempQry += " AND " + tempStr[1] + (!isIndia ? " LIKE " : "=")+" '" + valueStr + "'";
						if (tempStr[1] != null && tempStr[1].trim().length() > 0) {
							notQry += " AND " + tempStr[1] + " is null ";
						} else {
							notQry += "";
						}

					} else {
						tempQry += " AND " + tempStr[1] + " = " + lineStr[index] + " ";
						if (tempStr[1] != null && tempStr[1].trim().length() > 0) {
							notQry += " AND " + tempStr[1] + " is null ";
						} else {
							notQry += "";
						}

					}

				} // for

				query += tempQry;

				// logger.info("QUERY=="+query);

				contactIdList = getHibernateTemplate().find(query);

				if (contactIdList.size() == 1) {
					return contactIdList.get(0);
				} else if (contactIdList.size() > 1) {
					logger.info("more than 1 object found :" + contactIdList.size());
					return contactIdList.get(0);
				}

			} // outer for

			return null;

		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
			return null;
		}

	}// findContactByPriority

	
	//Changes start 2.5.4.0

	public List<Contacts> findContactByPriorityList(TreeMap<String, List<String>> prioMap, String[] lineStr, Users user, long userId) {

		try {
			String query = null;
			String tempQry = null;
			String notQry = "";

			logger.info("prioMap=" + prioMap);
			Set<String> keySet = prioMap.keySet();
			List<Contacts> contactIdList = null;

			List<String> tempList = null;

			outer: for (String eachKey : keySet) {

				query = " FROM Contacts  WHERE users=" + userId + " ";
				tempQry = notQry;

				// logger.info("Key ="+eachKey);
				tempList = prioMap.get(eachKey);
				int size = tempList.size();
				for (String eachStr : tempList) {
					int index = Integer.parseInt(eachStr.substring(0, eachStr.indexOf('|')));

					// query += " AND "+ eachStr.substring(eachStr.indexOf('|')+1) + " = '"+
					// lineStr[index] +"' ";
					String[] tempStr = eachStr.split("\\|");
					String valueStr = lineStr[index].trim();

					// logger.info("valueStr=="+valueStr);
					size = size - 1;
					if (valueStr == null || valueStr.trim().isEmpty()) {
						if (size != 0)
							continue;

						continue outer;
					}

					String posMappingDateFormatStr = tempStr[2].trim();

					if (posMappingDateFormatStr.toLowerCase().startsWith("date")
							&& !tempStr[1].trim().toLowerCase().startsWith("udf")) {
						try {
							posMappingDateFormatStr = posMappingDateFormatStr.substring(
									posMappingDateFormatStr.indexOf("(") + 1, posMappingDateFormatStr.indexOf(")"));
							valueStr = format.format((new SimpleDateFormat(posMappingDateFormatStr).parse(valueStr)));
							tempQry += " AND " + tempStr[1] + " = '" + valueStr + "' ";
							if (tempStr[1] != null && tempStr[1].trim().length() > 0) {
								notQry += " AND " + tempStr[1] + " is null ";
							}

							else {
								notQry += "";
							}
						} catch (ParseException e) {
							logger.error("Exception ::", e);
							return null;
						}
					} else if (posMappingDateFormatStr.toLowerCase().startsWith("string")
							|| tempStr[1].trim().toLowerCase().startsWith("udf")) {
						// query += " AND "+ eachStr.substring(eachStr.indexOf('|')+1) + " = '"+
						// lineStr[index] +"' ";
						boolean isIndia = user!=null ? user.getCountryType().equals(Constants.SMS_COUNTRY_INDIA) : false;
						if (tempStr[1].equalsIgnoreCase("Mobile") || tempStr[1].equalsIgnoreCase("mobilePhone")) {

							if (valueStr.startsWith("1")) {
								valueStr = valueStr.substring(1);
							} else if (valueStr.startsWith("91") && valueStr.trim().length() > 10) {
								valueStr = valueStr.substring(valueStr.trim().length() - 10);
							} else if (valueStr.startsWith("92") && valueStr.trim().length() > 10) {
								valueStr = valueStr.substring(valueStr.trim().length() - 10);
							}
							//Changes 2.5.4.0
							else if (!isIndia && valueStr.startsWith("971") && valueStr.trim().length() > 7) {
								valueStr = valueStr
										.substring(valueStr.trim().length() - (valueStr.trim().length() - 3)); // for
																												// uae
							}
							logger.info("isIndia ====="+isIndia);
							valueStr = !isIndia ? "%"+valueStr : valueStr;
						}
						tempQry += " AND " + tempStr[1] + (!isIndia ? " LIKE " : "=")+" '" + valueStr + "'";
						if (tempStr[1] != null && tempStr[1].trim().length() > 0) {
							notQry += " AND " + tempStr[1] + " is null ";
						} else {
							notQry += "";
						}

					} else {
						tempQry += " AND " + tempStr[1] + " = " + lineStr[index] + " ";
						if (tempStr[1] != null && tempStr[1].trim().length() > 0) {
							notQry += " AND " + tempStr[1] + " is null ";
						} else {
							notQry += "";
						}

					}

				} // for

				query += tempQry;

				logger.info("QUERY=="+query);

				contactIdList = getHibernateTemplate().find(query);
				return contactIdList;



			} // outer for

			return null;

		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
			return null;
		}

	}
	
	
	
	//Changes end 2.5.4.0
	
	
	
	
	public static Map<String, String> contPojoFieldMap = new HashMap<String, String>();

	static {
		contPojoFieldMap.put("Email", "emailId");
		contPojoFieldMap.put("First Name", "firstName");
		contPojoFieldMap.put("Last Name", "lastName");
		contPojoFieldMap.put("Street", "addressOne");
		contPojoFieldMap.put("Address Two", "addressTwo");
		contPojoFieldMap.put("City", "city");
		contPojoFieldMap.put("State", "state");
		contPojoFieldMap.put("Country", "country");
		contPojoFieldMap.put("ZIP", "zip");
		contPojoFieldMap.put("Mobile", "mobilePhone");
		contPojoFieldMap.put("Customer ID", "externalId");
		contPojoFieldMap.put("Addressunit ID", "hpId");
		contPojoFieldMap.put("Gender", "gender");
		contPojoFieldMap.put("Home Store", "homeStore");
		contPojoFieldMap.put("BirthDay", "birthDay");
		contPojoFieldMap.put("Anniversary", "anniversary");
	}

	public Contacts findContactByUniqPriority(TreeMap<String, List<String>> prioMap, Contacts contactObj, long userId) {
		return this.findContactByUniqPriority(prioMap, contactObj, userId, null);
	}

	public Contacts findContactByUniqPriority(TreeMap<String, List<String>> prioMap, Contacts contactObj, long userId, Users user) {

		try {
			String query = null;
			String tempQry = null;
			String notQry = "";

			logger.info("started findContactByUniqPriority");
			// logger.info("prioMap="+prioMap);
			Set<String> keySet = prioMap.keySet();
			List<Contacts> contactIdList = null;

			List<String> tempList = null;

			outer: for (String eachKey : keySet) {
				query = " FROM Contacts  WHERE users=" + userId + " ";

				tempQry = notQry;

				// logger.info("Key ="+eachKey);
				tempList = prioMap.get(eachKey);
				int size = tempList.size();
				for (String eachStr : tempList) {
					// logger.info(">>> eachStr >>"+eachStr);
					String[] tempStr = eachStr.split("\\|");

					// logger.info("tempStr[0]"+tempStr[0]+" :::tempStr[1]"+tempStr[1]);

					String valueStr = getContactFiled(contactObj, tempStr[0]);

					// logger.info(">>>tempStr[0]>>>"+tempStr[0]+ "Conatct Value is ::"+valueStr);
					size = size - 1;
					if (valueStr == null || valueStr.trim().length() == 0) {
						if (size != 0)
							continue;
						continue outer;
					}

					valueStr = valueStr.trim();

					if (tempStr[0].startsWith("UDF")) {

						tempQry += " AND " + tempStr[0] + " = '" + valueStr + "' ";

						if (tempStr[0] != null && tempStr[0].trim().length() > 0) {
							notQry += " AND " + tempStr[0] + " is null ";
						} else {
							notQry += "";
						}
					} else {

						if (tempStr[1].toLowerCase().equals("string")) {
							
							boolean isIndia = user!=null ? user.getCountryType().equals(Constants.SMS_COUNTRY_INDIA) : false;

							if (tempStr[0].equals("Mobile")) {

								if (valueStr.startsWith("1")) {
									valueStr = valueStr.substring(1);
								} else if (valueStr.startsWith("91") && valueStr.trim().length() > 10) {
									valueStr = valueStr.substring(valueStr.trim().length() - 10);
								} else if (valueStr.startsWith("92") && valueStr.trim().length() > 10) {
									valueStr = valueStr.substring(valueStr.trim().length() - 10);
								}
								//Changes 2.5.4.0
								else if (!isIndia && valueStr.startsWith("971") && valueStr.trim().length() > 7) {
									valueStr = valueStr
											.substring(valueStr.trim().length() - (valueStr.trim().length() - 3)); // for
																													// uae
								}
								logger.info("isIndia ====="+isIndia);
								valueStr = !isIndia ? "%"+valueStr : valueStr;
							}

							tempQry += " AND  " + contPojoFieldMap.get(tempStr[0]) + (!isIndia ? " LIKE " : "=")+" '" + valueStr + "'";
							// logger.info("tempQry here ::"+tempQry);
							// notQry += " AND "+contPojoFieldMap.get(tempStr[0]) + " is null ";

						} else {
							tempQry += " AND  " + contPojoFieldMap.get(tempStr[0]) + "=" + valueStr + "";

						}
						if (tempStr[0] != null && tempStr[0].trim().length() > 0) {
							notQry += " AND " + contPojoFieldMap.get(tempStr[0]) + " is null ";
						} else {
							notQry += "";
						}

					}

				}
				query += tempQry;

				logger.info("QUERY=="+query);

				contactIdList = getHibernateTemplate().find(query);
				// logger.info("contactIdList size is ::"+contactIdList.size());
				if (contactIdList.size() == 1) {
					return contactIdList.get(0);
				} else if (contactIdList.size() > 1) {
					logger.info("more than 1 object found :" + contactIdList.size());
					return contactIdList.get(0);
				}

			} // outer for

			return null;

		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
			return null;
		}

	} // findContactByPriority
	public List<Contacts> findContactBy(TreeMap<String, List<String>> prioMap, Contacts contactObj, Users user) {

		try {
			String query = null;
			String tempQry = null;
			String notQry = "";

			// logger.info("prioMap="+prioMap);
			Set<String> keySet = prioMap.keySet();
			List<Contacts> contactIdList = null;

			List<String> tempList = null;

			outer: for (String eachKey : keySet) {
				query = " FROM Contacts  WHERE users=" + user.getUserId() + " ";

				tempQry = notQry;

				// logger.info("Key ="+eachKey);
				tempList = prioMap.get(eachKey);
				int size = tempList.size();
				for (String eachStr : tempList) {
					logger.info(">>> eachStr >>"+eachStr);
					String[] tempStr = eachStr.split("\\|");

					// logger.info("tempStr[0]"+tempStr[0]+" :::tempStr[1]"+tempStr[1]);

					String valueStr = getContactFiled(contactObj, tempStr[0]);

					// logger.info(">>>tempStr[0]>>>"+tempStr[0]+ "Conatct Value is ::"+valueStr);
					size = size - 1;
					if (valueStr == null || valueStr.trim().length() == 0) {
						if (size != 0)
							continue;
						continue outer;
					}

					valueStr = valueStr.trim();

					if (tempStr[0].startsWith("UDF")) {

						tempQry += " AND " + tempStr[0] + " = '" + valueStr + "' ";

						if (tempStr[0] != null && tempStr[0].trim().length() > 0) {
							notQry += " AND " + tempStr[0] + " is null ";
						} else {
							notQry += "";
						}
					} else {

						if (tempStr[1].toLowerCase().equals("string")) {
							
							boolean isIndia = user.getCountryType().equals(Constants.SMS_COUNTRY_INDIA);

							if (tempStr[0].equals("Mobile")) {

								if (valueStr.startsWith("1")) {
									valueStr = valueStr.substring(1);
								} else if (valueStr.startsWith("91") && valueStr.trim().length() > 10) {
									valueStr = valueStr.substring(valueStr.trim().length() - 10);
								} else if (valueStr.startsWith("92") && valueStr.trim().length() > 10) {
									valueStr = valueStr.substring(valueStr.trim().length() - 10);
								}
								//Changes 2.5.4.0
								else if ( !isIndia && valueStr.startsWith("971") && valueStr.trim().length() > 7) {
									valueStr = valueStr
											.substring(valueStr.trim().length() - (valueStr.trim().length() - 3)); // for
																													// uae
								}

								logger.info("isIndia ===="+isIndia);
								valueStr = !isIndia ? "%"+valueStr : valueStr;
							}

							tempQry += " AND  " + contPojoFieldMap.get(tempStr[0]) + (!isIndia ? " LIKE " : "=")+" '" + valueStr + "'";
							//tempQry += " AND  " + contPojoFieldMap.get(tempStr[0]) + " LIKE '" + valueStr + "'";
							// logger.info("tempQry here ::"+tempQry);
							// notQry += " AND "+contPojoFieldMap.get(tempStr[0]) + " is null ";

						} else {
							tempQry += " AND  " + contPojoFieldMap.get(tempStr[0]) + "=" + valueStr + "";

						}
						if (tempStr[0] != null && tempStr[0].trim().length() > 0) {
							notQry += " AND " + contPojoFieldMap.get(tempStr[0]) + " is null ";
						} else {
							notQry += "";
						}

					}

				}
				query += tempQry;

				logger.info("QUERY=="+query);

				contactIdList = executeQuery(query);
				// logger.info("contactIdList size is ::"+contactIdList.size());
				/*if (contactIdList.size() == 1) {
					return contactIdList.get(0);
				} else if (contactIdList.size() > 1) {
					logger.info("more than 1 object found :" + contactIdList.size());
					return contactIdList.get(0);
				}*/

				return contactIdList;
			} // outer for

			return null;

		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
			return null;
		}catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
			return null;
		}

	} // findContactByPriority
	private String getContactFiled(Contacts contObj, String contFiledStr) {

		if (contFiledStr.equals("Email"))
			return contObj.getEmailId();
		else if (contFiledStr.equals("First Name"))
			return contObj.getFirstName();
		else if (contFiledStr.equals("Last Name"))
			return contObj.getLastName();
		else if (contFiledStr.equals("Street"))
			return contObj.getAddressOne();
		else if (contFiledStr.equals("Address Two"))
			return contObj.getAddressTwo();
		else if (contFiledStr.equals("City"))
			return contObj.getCity();
		else if (contFiledStr.equals("State"))
			return contObj.getState();
		else if (contFiledStr.equals("Country"))
			return contObj.getCountry();
		else if (contFiledStr.equals("ZIP"))
			return contObj.getZip();
		else if (contFiledStr.equals("Mobile"))
			return contObj.getMobilePhone();
		else if (contFiledStr.equals("Customer ID"))
			return contObj.getExternalId();
		// else if(contFiledStr.equals("Addressunit ID")) return contObj.getHpId();
		else if (contFiledStr.equals("Gender"))
			return contObj.getGender();
		else if (contFiledStr.equals("BirthDay"))
			return contObj.getBirthDay() == null ? ""
					: MyCalendar.calendarToString(contObj.getBirthDay(), MyCalendar.FORMAT_DATETIME_STYEAR);
		else if (contFiledStr.equals("Anniversary"))
			return contObj.getAnniversary() == null ? ""
					: MyCalendar.calendarToString(contObj.getAnniversary(), MyCalendar.FORMAT_DATETIME_STYEAR);
		else if (contFiledStr.equals("UDF1"))
			return contObj.getUdf1();
		else if (contFiledStr.equals("UDF2"))
			return contObj.getUdf2();
		else if (contFiledStr.equals("UDF3"))
			return contObj.getUdf3();
		else if (contFiledStr.equals("UDF4"))
			return contObj.getUdf4();
		else if (contFiledStr.equals("UDF5"))
			return contObj.getUdf5();
		else if (contFiledStr.equals("UDF6"))
			return contObj.getUdf6();
		else if (contFiledStr.equals("UDF7"))
			return contObj.getUdf7();
		else if (contFiledStr.equals("UDF8"))
			return contObj.getUdf8();
		else if (contFiledStr.equals("UDF9"))
			return contObj.getUdf9();
		else if (contFiledStr.equals("UDF10"))
			return contObj.getUdf10();
		else if (contFiledStr.equals("UDF11"))
			return contObj.getUdf11();
		else if (contFiledStr.equals("UDF12"))
			return contObj.getUdf12();
		else if (contFiledStr.equals("UDF13"))
			return contObj.getUdf13();
		else if (contFiledStr.equals("UDF14"))
			return contObj.getUdf14();
		else if (contFiledStr.equals("UDF15"))
			return contObj.getUdf15();

		else
			return null;

	}
	
	public List<Contacts> findMatchedContactListByUniqPriority(TreeMap<String, List<String>> prioMap,
			Contacts contactObj, long userId) {
		return this.findMatchedContactListByUniqPriority(prioMap, contactObj, userId, null);
	}

	public List<Contacts> findMatchedContactListByUniqPriority(TreeMap<String, List<String>> prioMap,
			Contacts contactObj, long userId, Users user) {

		try {
			String query = null;
			String tempQry = null;
			String notQry = "";

			logger.info("started findMatchedContactListByUniqPriority ");
			// logger.info("prioMap="+prioMap);
			Set<String> keySet = prioMap.keySet();
			List<Contacts> contactIdList = null;

			List<String> tempList = null;

			outer: for (String eachKey : keySet) {
				query = " FROM Contacts  WHERE users=" + userId + " ";

				tempQry = notQry;

				// logger.info("Key ="+eachKey);
				tempList = prioMap.get(eachKey);
				int size = tempList.size();
				for (String eachStr : tempList) {
					// logger.info(">>> eachStr >>"+eachStr);
					String[] tempStr = eachStr.split("\\|");

					// logger.info("tempStr[0]"+tempStr[0]+" :::tempStr[1]"+tempStr[1]);

					String valueStr = getContactFiled(contactObj, tempStr[0]);

					// logger.info(">>>tempStr[0]>>>"+tempStr[0]+ "Conatct Value is ::"+valueStr);
					size = size - 1;
					if (valueStr == null || valueStr.trim().length() == 0) {
						if (size != 0)
							continue;
						continue outer;
					}

					valueStr = valueStr.trim();

					if (tempStr[0].startsWith("UDF")) {

						tempQry += " AND " + tempStr[0] + " = '" + valueStr + "' ";

						if (tempStr[0] != null && tempStr[0].trim().length() > 0) {
							notQry += " AND " + tempStr[0] + " is null ";
						} else {
							notQry += "";
						}
					} else {

						if (tempStr[1].toLowerCase().equals("string")) {

							boolean isIndia = user!=null ? user.getCountryType().equals(Constants.SMS_COUNTRY_INDIA):false;
							if (tempStr[0].equals("Mobile")) {

								if (valueStr.startsWith("1")) {
									valueStr = valueStr.substring(1);
								} else if (valueStr.startsWith("91") && valueStr.trim().length() > 10) {
									valueStr = valueStr.substring(valueStr.trim().length() - 10);
								}
								logger.info("isIndia ===="+isIndia);
								valueStr = !isIndia ? "%"+valueStr : valueStr;
							}

							tempQry += " AND  " + contPojoFieldMap.get(tempStr[0]) + (!isIndia ? " LIKE " : "=")+" '" + valueStr + "'";
							// logger.info("tempQry here ::"+tempQry);
							// notQry += " AND "+contPojoFieldMap.get(tempStr[0]) + " is null ";

						} else {
							tempQry += " AND  " + contPojoFieldMap.get(tempStr[0]) + "=" + valueStr + "";

						}
						if (tempStr[0] != null && tempStr[0].trim().length() > 0) {
							notQry += " AND " + contPojoFieldMap.get(tempStr[0]) + " is null ";
						} else {
							notQry += "";
						}

					}

				}
				query += tempQry;

				logger.info("QUERY=="+query);

				contactIdList = getHibernateTemplate().find(query);
				// logger.info("contactIdList size is ::"+contactIdList.size());
				/*
				 * if(contactIdList.size() == 1) { return contactIdList.get(0); } else
				 * if(contactIdList.size() > 1) {
				 * logger.info("more than 1 object found :"+contactIdList.size()); return
				 * contactIdList.get(0); }
				 */

				return contactIdList;

			} // outer for

			return null;

		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
			return null;
		}

	} // findMatchedContactListByUniqPriority

	public List<Contacts> findContactByListIdStr(Set<MailingList> mlSet) {

		Iterator<MailingList> mlIt = mlSet.iterator();
		long mlsbit = Utility.getMlsBit(mlSet);
		String queryStr = Utility.getMultiUserQry(mlSet, mlsbit);

		if (queryStr != null) {
			queryStr = "SELECT DISTINCT c " + queryStr;
		} else {
			Long userId = mlIt.next().getUsers().getUserId();
			queryStr = " FROM Contacts WHERE users = " + userId + " AND bitwise_and(mlBits," + mlsbit + " )>0 ";
		}

		List<Contacts> cList = executeQuery(queryStr);
		return cList;
	}

	public List<Contacts> findContactBycidStr(String cIdStr) {

		String query = " FROM Contacts WHERE contactId in (" + cIdStr + ")";
		List<Contacts> cList = executeQuery(query);
		return cList;
	}

	public List<Contacts> findContactsByMlBit(Long userId, Long mlBit) {
		String query = "FROM Contacts WHERE users = " + userId + " AND bitwise_and(mlBits, " + mlBit.longValue()
				+ " )>0 ";

		List<Contacts> cList = executeQuery(query);
		if (cList.size() == 0)
			return null;

		return cList;
	}

	/*
	 * public List<Contacts> findAllByMlId(String mlIds) {
	 * 
	 * List<Contacts> contactList = null;
	 * 
	 * String qry =
	 * " SELECT DISTINCT c FROM Contacts c, MailingList ml WHERE ml.listId IN ("
	 * +mlIds+") "+
	 * " AND c.users = ml.users AND bitwise_and(c.mlBits, ml.mlBit)>0 ";
	 * 
	 * contactList = getHibernateTemplate().find(qry); return contactList;
	 * 
	 * 
	 * }
	 */

	/*
	 * public void setContactFieldsOnDeletion(long userId){
	 * 
	 * byte optinVal = 0;
	 * 
	 * String query = " UPDATE Contacts " + " SET purged = "+new
	 * Boolean(false)+", emailStatus = '"+Constants.CONT_STATUS_PURGE_PENDING+"', "+
	 * " lastStatusChange = null , lastMailDate = null, "+
	 * " optin = "+optinVal+", subscriptionType = null, "+ " optinMedium = null "+
	 * " WHERE users ="+userId +" AND mlBits = 0";
	 * 
	 * logger.debug(" set contact fields on deletion query :: "+query); long result
	 * = executeUpdate(query); }
	 */

	/**
	 * Added for EventTrigger
	 * 
	 * @param mailingList
	 * @param emailId
	 * @return
	 * 
	 * 		checking if a given contact is present in the ML. Separated the logic
	 *         into two queries since
	 */
	public Long getContactIdByEmailIdAndMlist(MailingList mailingList, String emailId) {

		try {

			String queryStr = " FROM Contacts c , MailingList m "
					+ " WHERE c.users = m.users  AND bitwise_and(c.mlBits," + mailingList.getMlBit().longValue()
					+ ") > 0 AND  c.emailId LIKE '" + emailId + "' " + " AND ml.listId = " + mailingList.getListId()
					+ " ";

			logger.info("query is " + queryStr);
			List<Contacts> contactList = getHibernateTemplate().find(queryStr);

			if (contactList != null && contactList.size() > 0) {

				// logger.debug("not null returning contact
				// id"+contactList.get(0).getContactId());
				return contactList.get(0).getContactId();
			}

			// logger.debug("null obj returning zero");
			return 0l;

		} catch (Exception e) {

			logger.info("**Exception ", e);
			return null;
		}

	} // getIdByEmailIdAndMailingList

	/*
	 * Fetch contacts from all mailinglists for the given user.
	 */

	public List<Contacts> findBy(Long currUserId, String emailStr) {

		try {
			String qry = " From Contacts WHERE users= " + currUserId.longValue() + " AND emailId IN(" + emailStr + ")";

			List<Contacts> retList = executeQuery(qry);
			return retList;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
			return null;
		}

	}

	public List<Contacts> findContactsByUserList(Contacts contObj, List<Users> userList,
			Map<Long, TreeMap<String, List<String>>> usersTreeMap) {

		logger.info("entering findContactsByUserList method");
		List<Contacts> userContactList = new ArrayList<Contacts>();
		for (Users users : userList) {

			TreeMap<String, List<String>> prioMap = usersTreeMap.get(users.getUserId());
			if (prioMap == null || prioMap.size() == 0)
				continue;

			String query = null;
			String tempQry = null;
			String notQry = "";

			// logger.debug("prioMap="+prioMap);
			Set<String> keySet = prioMap.keySet();
			List<Contacts> contactIdList = null;

			List<String> tempList = null;

			outer: for (String eachKey : keySet) {
				query = " FROM Contacts  WHERE users=" + users.getUserId().longValue() + " ";

				tempQry = notQry;

				// logger.debug("Key ="+eachKey);
				tempList = prioMap.get(eachKey);
				int size = tempList.size();
				for (String eachStr : tempList) {
					// logger.debug(">>> eachStr >>"+eachStr);
					String[] tempStr = eachStr.split("\\|");

					// logger.debug("tempStr[0]"+tempStr[0]+" :::tempStr[1]"+tempStr[1]);

					String valueStr = getContactFiled(contObj, tempStr[0]);
					// logger.debug(">>>tempStr[0]>>>"+tempStr[0]+ "Conatct Value is ::"+valueStr);
					size = size - 1;
					if (valueStr == null || valueStr.trim().length() == 0) {
						if (size != 0)
							continue;
						continue outer;
					}

					if (tempStr[0].startsWith("UDF")) {

						tempQry += " AND " + tempStr[0] + " = '" + valueStr + "' ";

						if (tempStr[0] != null && tempStr[0].trim().length() > 0) {
							notQry += " AND " + tempStr[0] + " is null ";
						} else {
							notQry += "";
						}
					} else {

						if (tempStr[1].toLowerCase().equals("string")) {

							boolean isIndia = false;
							if (tempStr[0].equals("Mobile")) {
								
								if(users.getCountryType().equalsIgnoreCase(Constants.SMS_COUNTRY_INDIA)){
										
									isIndia = true;
 								}

							}
							valueStr = !isIndia ? "%"+valueStr : valueStr;

							tempQry += " AND  " + contPojoFieldMap.get(tempStr[0]) + (!isIndia ? " LIKE " : "=")+" '" + valueStr + "'";
							// logger.debug("tempQry here ::"+tempQry);
							// notQry += " AND "+contPojoFieldMap.get(tempStr[0]) + " is null ";

						} else {
							tempQry += " AND  " + contPojoFieldMap.get(tempStr[0]) + "=" + valueStr + "";

						}
						if (tempStr[0] != null && tempStr[0].trim().length() > 0) {
							notQry += " AND " + contPojoFieldMap.get(tempStr[0]) + " is null ";
						} else {
							notQry += "";
						}

					}
				}
				query += tempQry;

				logger.debug("QUERY=="+query);

				contactIdList = getHibernateTemplate().find(query);
				if (contactIdList != null && contactIdList.size() > 0)
					userContactList.add(contactIdList.get(0));

			} // outer for

		}
		return userContactList;
	} // findContactsByUserList

	/*
	 * public int findTotPosLoc(Long userId){ try { String query =
	 * " SELECT COUNT(DISTINCT homeStore) FROM Contacts  WHERE users ="+userId.
	 * longValue()+" AND  regexp(homeStore, '^-?[0-9]') = 1"; List posLocList =
	 * getHibernateTemplate().find(query); if(posLocList.size() > 0) return
	 * ((Long)posLocList.get(0)).intValue(); else{
	 * 
	 * return 0; } } catch (DataAccessException e) { // TODO Auto-generated catch
	 * block logger.error("Exception ::" , e); return 0; }
	 * 
	 * }
	 */
	public List<Object> findPosLocId(Long userId) {
		try {
			String query = " SELECT DISTINCT homeStore FROM Contacts  WHERE users =" + userId.longValue()
					+ " AND   regexp(homeStore,'^-?[0-9]') = 1";
			List<Object> posLocList = getHibernateTemplate().find(query);
			if (posLocList.size() < 0) {

				return null;
			} else {
				return posLocList;
			}
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
			return null;
		}

	}

	public long findUniqActiveContacts(Long userId) {

		String queryStr = " SELECT COUNT(c.contactId) FROM Contacts c WHERE c.users = " + userId
				+ " AND c.mlBits > 0  ";
		Long count = ((Long) getHibernateTemplate().find(queryStr).get(0)).longValue();

		return count;
		// if

	}

	public Contacts findMobileContactByUser(String mobilePhone, Users user) {
		/*
		 * if(mobilePhone.startsWith("1")) {
		 * 
		 * mobilePhone = mobilePhone.substring(1); }
		 */
		if (mobilePhone.startsWith("" + user.getCountryCarrier())) {

			mobilePhone = mobilePhone.substring(("" + user.getCountryCarrier()).length());
		}

		String qry = "FROM Contacts WHERE users=" + user.getUserId() + " AND mobilePhone like '%" + mobilePhone + "'";

		List<Contacts> contactsList = getHibernateTemplate().find(qry);

		if (contactsList != null && contactsList.size() > 0) {

			return contactsList.get(0);

		} // if
		else {

			return null;
		}

	}

	public List<Contacts> findByQuery(String qry) {
		/*
		 * if(mobilePhone.startsWith("1")) {
		 * 
		 * mobilePhone = mobilePhone.substring(1); }
		 */

		// String qry = "FROM Contacts WHERE users="+user.getUserId()+" AND mobilePhone
		// like '%"+mobilePhone+"'";

		List<Contacts> contactsList = getHibernateTemplate().find(qry);

		if (contactsList != null && contactsList.size() > 0) {

			return contactsList;

		} // if
		else {

			return null;
		}

	}

	public List<Long> findCidsList(String mobilePhone, Long userId) {

		List<Long> cidList = null;
		String queryStr = " SELECT contactId FROM Contacts WHERE users=" + userId + " AND mobilePhone like '%"
				+ mobilePhone + "'";
		cidList = getHibernateTemplate().find(queryStr);
		if (cidList != null && cidList.size() > 0)
			return cidList;
		return null;

	}

	public long getCountForActiveMembership(Long userId, MailingList mlist) {

		String query = "SELECT COUNT(DISTINCT c.cid) FROM contacts c, contacts_loyalty cL WHERE c.user_id =" + userId
				+ " AND cL.user_id= " + userId + " And c.cid=cL.contact_id " + " AND c.mlbits& " + mlist.getMlBit()
				+ ">0" + " AND c.loyalty_customer=1 And cL.membership_status='Active'";

		logger.debug(" COUNT FOR Active membership query :: " + query);
		long count = jdbcTemplate.queryForInt(query);
		return count;
	}

	// Changes 2.5.3.0 start
	public ResultSet findContactsByReport(Report report, Users user, String reportType) {
		String countryCarrier =user.getCountryCarrier()!=null?user.getCountryCarrier()+"":"" ;//OMA-66
		logger.info("---- Entered findContactsByDates ----");
		int strtIndex = 0;
		int size = 1000;
		MailingListDao mailingListDao = null;
		MailingList mailingList = new MailingList();
		JdbcResultsetHandler jdbcResultsetHandler = new JdbcResultsetHandler();

		String qry = "SELECT  c.*,cl.card_number,cl.pos_location_id,cl.subsidiary_number as sbs_num,sc.id,sc.type as emailReason,sc.suppressed_time as emailSuppressedTime,ssc.type,ssc.suppressed_time,u.date as unsubscribeSuppressedTime "
				+ "FROM contacts c LEFT JOIN contacts_loyalty cl "
				+ "ON c.cid = cl.contact_id AND c.user_id=cl.user_id LEFT JOIN suppressed_contacts sc "
				+ "ON c.email_id=sc.email AND c.user_id = sc.user_id LEFT JOIN sms_suppressed_contacts ssc "
				+ "ON (c.mobile_phone=ssc.mobile||concat("+countryCarrier+",c.mobile_phone)= ssc.mobile) AND c.user_id = ssc.user_id LEFT JOIN unsubscribes u "////OMA-66
				+ "ON c.email_id=u.email_id AND c.user_id = u.user_id " + "WHERE c.user_id= " + user.getUserId();
		if(reportType.equalsIgnoreCase(OCConstants.IMPORT_TYPE_BULK)){
			qry = "SELECT  c.*,cl.card_number,cl.pos_location_id,cl.subsidiary_number as sbs_num "
					//+ "sc.id,sc.type as emailReason,sc.suppressed_time as emailSuppressedTime,ssc.type,ssc.suppressed_time,u.date as unsubscribeSuppressedTime "
					+ " FROM contacts c LEFT JOIN contacts_loyalty cl "
					+ " ON c.cid = cl.contact_id AND c.user_id=cl.user_id"
					//+ " LEFT JOIN suppressed_contacts sc "
					//+ "ON c.email_id=sc.email AND c.user_id = sc.user_id LEFT JOIN sms_suppressed_contacts ssc "
					//+ "ON (c.mobile_phone=ssc.mobile||concat("+countryCarrier+",c.mobile_phone)= ssc.mobile) AND c.user_id = ssc.user_id LEFT JOIN unsubscribes u "////OMA-66
					//+ "ON c.email_id=u.email_id AND c.user_id = u.user_id " + 
					+" WHERE c.user_id= " + user.getUserId() +" AND cl.user_id="+ user.getUserId();
		}
		String subQry = "";
		logger.info("REPORT=====>" + report.getContactList());

		if (report.getStartDate() != null && report.getEndDate() != null && !report.getStartDate().isEmpty()
				&& !report.getEndDate().isEmpty()) {
				subQry += " AND (( cl.created_date BETWEEN '" + report.getStartDate() + "' AND '" + report.getEndDate() + "') "
						+ " OR"
						+ " (c.modified_date is not null AND c.modified_date BETWEEN '" + report.getStartDate() + "' AND '" + report.getEndDate() + "' ) )";
				
				}
		
		if (report.getContactSource() != null && !report.getContactSource().isEmpty()
				&& !report.getContactSource().equalsIgnoreCase(OCConstants.IMPORT_CONTACT_TYPE_ALL)) {
			String source = report.getContactSource();
			if (source.toString().equalsIgnoreCase("Store")) {
				source = Constants.CONTACT_OPTIN_MEDIUM_POS;
			} else if (source.toString().equalsIgnoreCase("Webform")) {
				source = Constants.CONTACT_OPTIN_MEDIUM_WEBFORM;
			} else if (source.toString().equalsIgnoreCase("eComm")) {
				source = Constants.CONTACT_OPTIN_MEDIUM_ECOMMERCE;
			} else if (source.toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_LOYALTY_APP)){
				source = OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_LOYALTY_APP;
			}else if (source.toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_MOBILE_APP)){
				source = OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_MOBILE_APP;
			}
			
			subQry += " AND c.optin_medium like '%" + source + "%'";
		}
		if (report.getContactList() != null && !report.getContactList().isEmpty()
				&& !report.getContactList().equalsIgnoreCase("All")) {
			try {
				mailingListDao = (MailingListDao) ServiceLocator.getInstance()
						.getDAOByName(OCConstants.MAILINGLIST_DAO);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mailingList = mailingListDao.findByListName(report.getContactList(), user.getUserId());
			subQry += " AND c.mlbits & " + mailingList.getMlBit() + " > 0";
		}
		if (report.getStore() != null && !report.getStore().isEmpty()) {
			subQry += " AND c.home_store='" + report.getStore() + "'";
		}
		//APP-1775
		if(report.getContactType() !=null && !report.getContactType().isEmpty()
				&& report.getContactType().equalsIgnoreCase("PushNotificationsTrue")) {
			subQry += " AND c.push_notification=true ";
		}

		if (report.getMaxRecords() != null && !report.getMaxRecords().isEmpty()) {
			size = Integer.parseInt(report.getMaxRecords());
			subQry += " limit " + size;
		}
		if (report.getOffset() != null && !report.getOffset().isEmpty()) {
			strtIndex = Integer.parseInt(report.getOffset());
			subQry += " offset " + (strtIndex<=0 ? 0 : strtIndex-1);
		}

		// subQry +=" limit "+strtIndex+","+size;

		logger.info(qry + subQry);
		jdbcResultsetHandler.executeStmt(qry + subQry);
		ResultSet resultSet = jdbcResultsetHandler.getResultSet();
		// List<Contacts> ContactsList = getHibernateTemplate().find(qry);

		logger.info("---- Exit findContactsByDates ----");
		return resultSet;

	}

	public ResultSet findContactsInfoBy(Users user, String customerIDs, String emailIDs, String mobileStr){
		//For overcoming MySQL syntax problem. 
		
		
		if(customerIDs.isEmpty() && emailIDs.isEmpty() && mobileStr.isEmpty()) {
			return null;
		}
		
		JdbcResultsetHandler jdbcResultsetHandler = new JdbcResultsetHandler();
		String countryCarrier =user.getCountryCarrier()!=null?user.getCountryCarrier()+"":"" ;//APP-2208
		String qry = "SELECT  c.*,cl.card_number,cl.pos_location_id,cl.subsidiary_number as sbs_num "
				+ " FROM contacts c,  contacts_loyalty cl "
				+ " WHERE c.user_id="+user.getUserId()+" and cl.user_id ="+user.getUserId()+
				" AND c.cid = cl.contact_id AND (c.mobile_phone=cl.mobile_phone||concat("+countryCarrier+",c.mobile_phone) = cl.mobile_phone) AND c.user_id=cl.user_id ";
				if(!customerIDs.isEmpty())		
					qry+= " AND c.external_id in("+customerIDs+") ";
				if(!emailIDs.isEmpty())
					qry+= " AND c.email_id in("+emailIDs+") ";
				if(!mobileStr.isEmpty())
					qry+= " AND c.mobile_phone  REGEXP '"+ mobileStr +"'  ";
		
		logger.info("qry==>"+qry);		
		jdbcResultsetHandler.executeStmt(qry);
		ResultSet resultSet = jdbcResultsetHandler.getResultSet();

		return resultSet;
	
		
	}
	
	
	public ResultSet findContactsByLookup(Lookup lookup, Users user) {
		JdbcResultsetHandler jdbcResultsetHandler = new JdbcResultsetHandler();
		String countryCarrier =user.getCountryCarrier()!=null?user.getCountryCarrier()+"":"" ;//OMA-66
			
		String qry = "SELECT  c.*,cl.card_number,cl.pos_location_id,cl.subsidiary_number as sbs_num,sc.type as emailReason,sc.suppressed_time as emailSuppressedTime,ssc.type,ssc.suppressed_time,u.date as unsubscribeSuppressedTime "
				+ "FROM contacts c LEFT JOIN contacts_loyalty cl "
				+ "ON c.cid = cl.contact_id AND c.user_id=cl.user_id LEFT JOIN suppressed_contacts sc "
				+ "ON c.email_id=sc.email AND c.user_id = sc.user_id LEFT JOIN sms_suppressed_contacts ssc "
				+ "ON (c.mobile_phone=ssc.mobile||concat("+countryCarrier+",c.mobile_phone) = ssc.mobile) AND c.user_id = ssc.user_id LEFT JOIN unsubscribes u "//OMA-66
				+ "ON c.email_id=u.email_id AND c.user_id = u.user_id " + "WHERE c.user_id= " + user.getUserId()
				+ " AND ";
		int count = 0;
		String subQry = " ";

		if (lookup.getEmailAddress() != null && !lookup.getEmailAddress().isEmpty()) {
			subQry = qry + " c.email_id ='" + lookup.getEmailAddress() + "'";
			count++;
		}
		if (lookup.getPhone() != null && !lookup.getPhone().isEmpty()) {
			if (count > 0)
				subQry += " UNION ";

			subQry += qry + " c.mobile_phone ='" + lookup.getPhone() + "'";
			count++;
		}
		if (lookup.getMembershipNumber() != null && !lookup.getMembershipNumber().isEmpty()) {
			if (count > 0)
				subQry += " UNION ";

			subQry += qry + " cl.card_number ='" + lookup.getMembershipNumber() + "'";
		}
		// subQry += " ) ";
		logger.info("LOOKUP QUERY IS ====>" + subQry);
		jdbcResultsetHandler.executeStmt(subQry);
		ResultSet resultSet = jdbcResultsetHandler.getResultSet();

		return resultSet;
	}

	// Changes 2.5.3.0 end
	
	//added for mobileApp
	public ResultSet executeQueryForMobAppAPI(String memberShipNumber, String cid, String receiptNumber, 
			String startDate, String endDate, String offset, String maxecords , Long userId) {
		
		String subQuery = (startDate != null && !startDate.isEmpty()) ? (" AND sal.sales_date >= '"+startDate+"' "): Constants.STRING_NILL;
		
		 subQuery += (endDate != null && !endDate.isEmpty()) ? (" AND sal.sales_date <= '"+endDate+"' "): Constants.STRING_NILL;
		 
		subQuery += (receiptNumber != null && !receiptNumber.isEmpty()) ? (" and sal.reciept_number='"+receiptNumber+"' ") : Constants.STRING_NILL;
		
		subQuery += (cid != null && !cid.isEmpty()) ? (" and sal.cid= "+cid+" AND tc.contact_id="+cid+" AND c.cid="+cid ) : Constants.STRING_NILL;
		
		String query = " select sal.* , c.*,tc.* from retail_pro_sales sal left outer join "
				+ " contacts c on sal.cid=c.cid left outer join "
				+ " loyalty_transaction_child tc on c.cid=tc.contact_id where sal.user_id="+userId
				+ "  and c.user_id="+userId+" and tc.user_id="+userId + subQuery+" limit "+offset+ Constants.DELIMETER_COMMA+maxecords;
		
		logger.debug("query is ==>"+query);
		
		JdbcResultsetHandler jdbcResultsetHandler = new JdbcResultsetHandler();
		jdbcResultsetHandler.executeStmt(query );
		ResultSet resultSet = jdbcResultsetHandler.getResultSet();

		return resultSet;

		//execute
		
	}
	
	public long getAllNotificationCount(Set<MailingList> mlSet) {

		try {
			Iterator<MailingList> mlIt = mlSet.iterator();

			long mlsbit = Utility.getMlsBit(mlSet);
			String queryStr = Utility.getMultiUserQry(mlSet, mlsbit);

			if (queryStr != null) {
				queryStr = "SELECT COUNT(DISTINCT instance_id) " + queryStr + "AND push_notification = true AND instance_id IS NOT NULL"
						+ " AND instance_id!='' AND device_Type IS NOT NULL AND device_Type !='' ";

			} else {
				Long userId = mlIt.next().getUsers().getUserId();
				queryStr = "SELECT COUNT(instance_id) as cnt FROM contacts WHERE user_id = "
						+ userId + " AND mlbits&" + mlsbit + ">0" + " AND push_notification = true AND instance_id IS NOT NULL"
						+ " AND instance_id!='' AND device_Type IS NOT NULL AND device_Type !='' ";

			}

			long count = jdbcTemplate.queryForInt(queryStr);
			return count;
		} catch (Exception e) {
			logger.error("** Exception -  ", e);
		}
		return 0;

	}

	public Contacts findByMobileNumberAndInstanceId(String instanceId, String mobileNumber) {
		try {
			String qry = " From Contacts WHERE mobilePhone ='"+mobileNumber+"' AND instanceId = '"+instanceId+"'";
			List<Contacts> retList = executeQuery(qry);
			if(retList!=null && !retList.isEmpty() && retList.size() > 0) {
				return retList.get(0);
			}else {
				return null;
			}
		} catch (Exception e) {
			logger.error("Exception ::", e);
			return null;
		}
	}

	
	


}
