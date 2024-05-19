package org.mq.marketer.campaign.dao;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import org.mq.marketer.campaign.beans.Contacts;
import org.mq.marketer.campaign.beans.LoyaltyProgram;
import org.mq.marketer.campaign.beans.LoyaltyTransaction;
import org.mq.marketer.campaign.general.Constants;
import org.mq.optculture.utils.OCConstants;
import org.springframework.jdbc.core.JdbcTemplate;

public class LoyaltyTransactionDaoForDML extends AbstractSpringDaoForDML {

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);

	private SessionFactory sessionFactory;
	
	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}
	
	/*public List find(String q, int startCount){
		return super.executeQuery(q, startCount, 100);
	}*/
	
	public Long getCount(String query){
		return jdbcTemplate.queryForLong(query);
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	public LoyaltyTransactionDaoForDML() {
	}
	
	/*public LoyaltyTransaction find(Long id) {
		return (LoyaltyTransaction) super.find(LoyaltyTransaction.class, id);
	}*/

	public void saveOrUpdate(LoyaltyTransaction loyaltyTransaction) {
		super.saveOrUpdate(loyaltyTransaction);
	}
	public void merge(LoyaltyTransaction loyaltyTransaction) {
		super.merge(loyaltyTransaction);
	}
	public void delete(LoyaltyTransaction loyaltyTransaction) {
		super.delete(loyaltyTransaction);
	}
	
	/*public LoyaltyTransaction findByRequestId(String requestId, String serviceType){
		String queryStr = " FROM LoyaltyTransaction WHERE requestId = '"+requestId+"' AND serviceType='"+serviceType+"'";
		List transList = getHibernateTemplate().find(queryStr);
		if(transList != null && transList.size() > 0){
			return (LoyaltyTransaction)transList.get(0);
		}
		else return null;
	}
	
	public LoyaltyTransaction findRequestByDocSid(String userName, String docSid, String transType, String requestStatus) {
		
		String queryStr = "FROM LoyaltyTransaction WHERE userDetail ='"+userName+"' AND type = '"+transType+"'" 
				+" AND requestStatus = '"+requestStatus+"' AND docSID = '"+docSid+"'" ;
		logger.info("Query :: "+queryStr);
		List transList = getHibernateTemplate().find(queryStr);
		if(transList != null && transList.size() > 0){
			return (LoyaltyTransaction)transList.get(0);
		}
		else return null;
		
	}
	
	public LoyaltyTransaction findRequestByReqIdAndDocSid(String userName, String requestId, String docSid, String transType, String requestStatus){
		
		String queryStr = "FROM LoyaltyTransaction WHERE userDetail ='"+userName+"'"
				+ " AND type = '"+transType+"' AND requestStatus = '"+requestStatus+"' AND requestId = '"+requestId+"' AND docSID = '"+docSid+"'";
		logger.info("Query :: "+queryStr);
		List transList = getHibernateTemplate().find(queryStr);
		if(transList != null && transList.size() > 0){
			return (LoyaltyTransaction)transList.get(0);
		}
		else return null;
	}
	
	public LoyaltyTransaction findRequestBycustSidAndReqId(String userName, String custSid, String requestId, String transType, String requestStatus){
		
		String queryStr = "FROM LoyaltyTransaction WHERE userDetail ='"+userName+"' AND type = '"+transType+"' AND requestStatus = '"+requestStatus+"'" +
				" AND requestId = '"+requestId+"' AND customerId = '"+custSid+"'";
		logger.info("Query :: "+queryStr);
		//logger.info("queryStr = "+queryStr);
		List transList = getHibernateTemplate().find(queryStr);
		if(transList != null && transList.size() > 0){
			return (LoyaltyTransaction)transList.get(0);
		}
		else return null;
		
	}
	
	public LoyaltyTransaction findRequestByCardAndReqId(String card, String requestId, String transType, String requestStatus){
		
		String queryStr = "FROM LoyaltyTransaction WHERE requestId = '"+requestId+"' AND cardNumber = '"+card+"'"
				+ " AND type = '"+transType+"' AND requestStatus = '"+requestStatus+"'";
		logger.info("Query :: "+queryStr);
		
		//logger.info("queryStr = "+queryStr);
		List transList = getHibernateTemplate().find(queryStr);
		if(transList != null && transList.size() > 0){
			return (LoyaltyTransaction)transList.get(0);
		}
		else return null;
		
	}
	
	public LoyaltyTransaction findByRequestIdAndType(String requestId, String type){
		String queryStr = " FROM LoyaltyTransaction WHERE requestId = '"+requestId + "' and type = '" + type + "'";
		List transList = getHibernateTemplate().find(queryStr);
		if(transList != null && transList.size() > 0){
			return (LoyaltyTransaction)transList.get(0);
		}
		else return null;
	}

	public LoyaltyTransaction findRequestBy(String requestId, String userDetails, String Transaction, String serviceType) {
		String queryStr = " FROM LoyaltyTransaction WHERE requestId = '"+requestId +"' AND userDetail='"+userDetails+"' AND type = '" + OCConstants.LOYALTY_TRANS_TYPE_RETURN + "'"
				+ " AND requestStatus = '"+OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE +"'";
		List transList = getHibernateTemplate().find(queryStr);
		if(transList != null && transList.size() > 0){
			return (LoyaltyTransaction)transList.get(0);
		}
		else return null;
	}

	
	public LoyaltyTransaction findDuplicateRequestBy(String docSID, String userDetails,  String Transaction, String serviceType) {
		
		String queryStr = " FROM LoyaltyTransaction WHERE docSID = '"+docSID +"' AND userDetail='"+userDetails+"' AND type = '" + OCConstants.LOYALTY_TRANS_TYPE_RETURN + "'"
				+ " AND requestStatus = '"+OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE +"'";
		List transList = getHibernateTemplate().find(queryStr);
		if(transList != null && transList.size() > 0){
			return (LoyaltyTransaction)transList.get(0);
		}
		else return null;
		
	}
			
	
	public LoyaltyTransaction findById(Long baseTransactionId) {
		List<LoyaltyTransaction> list = getHibernateTemplate().find("FROM LoyaltyTransaction WHERE id="+ baseTransactionId );
    	
    	if(list != null && list.size() > 0) {
    		return list.get(0);
    	}
    	return null;
	}
	
	
	
	
	
	*/
	
	
}
