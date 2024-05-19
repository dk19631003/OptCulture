package org.mq.loyality.common.dao;


import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.mq.loyality.common.hbmbean.ContactsLoyalty;
import org.mq.loyality.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class ContactsLoyaltyDao {


	@Autowired
	private SessionFactory sessionFactory;
	private static final Logger logger = LogManager.getLogger(Constants.LOYALTY_LOGGER);
	public ContactsLoyaltyDao() {}
	@Transactional
	public ContactsLoyalty getContactsLoyaltyByCustId(Long customerId, Long userId){

		try {
			String query = " FROM ContactsLoyalty WHERE userId = "+userId+" AND customerId = "+customerId;
			List<ContactsLoyalty> list = null;
			list =sessionFactory.getCurrentSession().createQuery(query).list();

			if(list.size() >0) return list.get(0);
			else return null;

		} catch (DataAccessException e) {
			return null;
		}

	}

	/*public List<ContactsLoyalty> getLoyaltyListByCustId(String customerId, Long userId){

		try {
			String query = " FROM ContactsLoyalty WHERE userId = "+userId+" AND customerId = "+customerId;
    		List<ContactsLoyalty> list = null;
			list = getHibernateTemplate().find(query);

			return list;

		} catch (DataAccessException e) {
			logger.error("Exception ::" , e);
			return null;
		}

	}*/

	/*	needed*/
	@Transactional
	public ContactsLoyalty getContactsLoyaltyByCardId(Long cardId) {
		String query = " FROM ContactsLoyalty WHERE  cardNumber='"+cardId+"'";
		List<ContactsLoyalty> tempList = sessionFactory.getCurrentSession().createQuery(query).list();

		if(tempList != null && tempList.size() >0) {

			return tempList.get(0);
		} else 
			return null;

	}
	@Transactional
	public List<ContactsLoyalty> findChildrenByParent(Long userID, Long loyaltyId) {
		

		List<ContactsLoyalty> list = null;
		try {
			list = sessionFactory.getCurrentSession().createQuery("FROM ContactsLoyalty WHERE userId="+userID+" "
					+ "AND transferedTo IS NOT NULL AND transferedTo="+loyaltyId.longValue()).list();

		} catch (DataAccessException e) {
			logger.info("DataAccessException :: ",e);
			return null;
		}catch (Exception e) {
			logger.info("Exception :: ",e);
			return null;
		}
		return list;
	
		
	}
	
	@Transactional
	public ContactsLoyalty findByLoyaltyId(Long userID, Long loyaltyId) {
		

		List<ContactsLoyalty> list = null;
		try {
			list = sessionFactory.getCurrentSession().createQuery("FROM ContactsLoyalty WHERE userId="+userID+" "
					+ "AND loyaltyId="+loyaltyId.longValue()).list();

		} catch (DataAccessException e) {
			logger.info("DataAccessException :: ",e);
			return null;
		}catch (Exception e) {
			logger.info("Exception :: ",e);
			return null;
		}
		if(list !=null && list.size()>0)
		return list.get(0);
		return null;
	
		
	}
}