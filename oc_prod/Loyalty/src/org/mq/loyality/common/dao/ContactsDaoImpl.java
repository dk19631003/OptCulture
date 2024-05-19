package org.mq.loyality.common.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.mq.loyality.common.hbmbean.Contacts;
import org.mq.loyality.common.hbmbean.ContactsLoyalty;
import org.mq.loyality.common.hbmbean.Users;
import org.mq.loyality.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class ContactsDaoImpl extends AbstractDAOImpl<Contacts, Long> implements
		ContactsDao {

	@Autowired
	private SessionFactory sessionFactory;
	@Autowired
	private SessionFactory sessionFactoryForDML;

	private static final Logger logger = LogManager
			.getLogger(Constants.LOYALTY_LOGGER);

	protected ContactsDaoImpl() {
		super(Contacts.class);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Contacts getContacts(long contactId) {
		// TODO Auto-generated method stub
		return findById(contactId);
	}

	@Override
	@Transactional("txMngrForDML")
	public void saveOrUpdate(Contacts data) {
		logger.info("data is------------>" + data.getMobilePhone());
		//super.saveOrUpdate(data);
		sessionFactoryForDML.getCurrentSession().saveOrUpdate(data);
	
	}

	@SuppressWarnings("unchecked")
	@Override
	public ArrayList<Object[]> getTotTransDetails(StringBuffer sb, int page,
			int pageSize) {
		ArrayList<Object[]> transList = null;
		int firstResult = page * pageSize - pageSize;
		SQLQuery sqlQuery = sessionFactory.getCurrentSession().createSQLQuery(
				sb.toString());
		sqlQuery.setFirstResult(firstResult);
		sqlQuery.setMaxResults(pageSize);
		transList = (ArrayList<Object[]>) sqlQuery.list();
		return transList;

	}

	@SuppressWarnings("unchecked")
	@Override
	public ArrayList<Object[]> getTotTransDetails(StringBuffer sb) {
		ArrayList<Object[]> transList = null;
		transList = (ArrayList<Object[]>) sessionFactory.getCurrentSession()
				.createSQLQuery(sb.toString()).list();
		/*
		 * transList=jdbcTemplate.query(sb.toString(),new
		 * RowMapper<TransDetails>() {
		 * 
		 * @Override public TransDetails mapRow(ResultSet rs, int rowNum) throws
		 * SQLException { TransDetails trans=new TransDetails();
		 * trans.setRecieptAmount(rs.getDouble(0));
		 * trans.setRecieptNumber(rs.getLong(1));
		 * trans.setStoreName(rs.getString(2));
		 * trans.setDocSid(rs.getString(4)); trans.setRecieptDate(rs.getDate(5,
		 * Calendar.getInstance()).toString());
		 * trans.setLoyalityBal(rs.getLong(7)); return trans; } }); } catch
		 * (DataAccessException e) { throw new BaseDAOException
		 * ("DAO Exception in  getTotTransDetails!!!!", e); }
		 * logger.info("transLIst is===========axaaaa=============>"+transList);
		 */
		return transList;

	}

	@Override
	public ArrayList<Object[]> getRecieptDetails(String id, String docSid, Long orgId,
			String storeName, Long userId) {
		// TODO Auto-generated method stub

		@SuppressWarnings("unchecked")
		StringBuffer query = new StringBuffer();
		query.append("select  rp.sales_price,rp.reciept_number, IFNULL(o.store_name,''),IFNULL(o.email_id,''), rsku.item_sid,"
				+ " rp.tax,rsku.dcs,rp.tender_type,IFNULL(o.address,''),rp.sales_date,rp.quantity,rsku.sku,rsku.description  FROM retail_pro_sales rp"
				+ "  LEFT JOIN org_stores o ON o.home_store_id = rp.store_number INNER JOIN "
				+ " retail_pro_sku rsku on rsku.sku_id=rp.inventory_id   where  rp.user_id="+userId+" AND rp.doc_sid="
				+ docSid + " ");
		if (storeName != null && !storeName.trim().equals("")) {
			query.append(" and o.org_id=" + orgId);
		}
		ArrayList<Object[]> transList = (ArrayList<Object[]>) sessionFactory
				.getCurrentSession().createSQLQuery(query.toString()).list();
		return transList;
	}

	@Override
	@Transactional("txMngrForDML")
	public void saveOrUpdate(ContactsLoyalty contactLoyality) {
		sessionFactoryForDML.getCurrentSession().saveOrUpdate(contactLoyality);
	}

	@Transactional
	public int updatemobileStatus(String mobile, String status, Users user) {
		if (mobile.startsWith("" + user.getCountryCarrier())) {
			mobile = mobile.substring(("" + user.getCountryCarrier()).length());
		}
		String Qry = "UPDATE Contacts SET mobileStatus='" + status
				+ "' WHERE mobilePhone like '%" + mobile + "' AND users="
				+ user.getUserId().longValue();
		int count = sessionFactoryForDML.getCurrentSession().createSQLQuery(Qry)
				.executeUpdate();
		return count;
	}

	@Override
	@Transactional
	public Double findContactPurchaseDetails(Long userId, Long contactId) {

		try {
			List purcahseList = new ArrayList<Map<String, Object>>();
			String qry = "";

			qry = "SELECT tot_purchase_amt FROM sales_aggregate_data "
					+ "WHERE user_id =" + userId.longValue() + " AND cid ="
					+ contactId.longValue();
			logger.info("query is ::" + qry);
			purcahseList = sessionFactory.getCurrentSession()
					.createSQLQuery(qry).list();

			/* return jdbcTemplate.queryForList(qry); */

			/*
			 * purcahseList = jdbcTemplate.query(qry, new RowMapper() { public
			 * Object mapRow(ResultSet rs, int rowNum) throws SQLException {
			 * 
			 * Object[] promOCodeObjArr = new Object[3];; promOCodeObjArr[0] =
			 * rs.getDouble(1); promOCodeObjArr[1] = rs.getDouble(2);
			 * promOCodeObjArr[2] = rs.getInt(3);
			 * 
			 * return promOCodeObjArr; }
			 * 
			 * });
			 */
			if (purcahseList != null && purcahseList.size() > 0) {
				return (Double) purcahseList.get(0);
			} else
				return null;
		} catch (DataAccessException e) {
			logger.error("Exception ::", e);
			return null;
		}

	}
}
