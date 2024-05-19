package org.mq.marketer.campaign.dao;

import java.io.Serializable;
import java.util.List;

import org.mq.marketer.campaign.beans.Contacts;
import org.mq.marketer.campaign.beans.SparkBaseTransactions;
import org.mq.marketer.campaign.dao.AbstractSpringDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.optculture.utils.OCConstants;
import org.springframework.jdbc.core.JdbcTemplate;

public class SparkBaseTransactionsDaoForDML extends AbstractSpringDaoForDML implements Serializable{
	
	public SparkBaseTransactionsDaoForDML(){}
	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	public void save(SparkBaseTransactions sparkBaseTransactions) throws Exception{
		super.save(sparkBaseTransactions);
	}
	
	/*public SparkBaseTransactions find(Long id) {
		String qry = "FROM SparkBaseTransactions WHERE id="+id;
		List<SparkBaseTransactions> list = getHibernateTemplate().find(qry);
		return list.get(0);
//		return (SparkBaseTransactions) super.find(SparkBaseTransactions.class, id);
	}

	
	
	public List<SparkBaseTransactions> findAllNewTransaction(){
		
		List<SparkBaseTransactions> listOfnewTransaction = null;
		String typeStr= "'"+Constants.SB_TRANSACTION_LOYALTY_ISSUANCE+"','"+Constants.SB_TRANSACTION_GIFT_ISSUANCE+"','"+
								Constants.SB_TRANSACTION_LOYALTY_REDEMPTION+"','"+
								Constants.SB_TRANSACTION_GIFT_REDEMPTION+"','"+
								Constants.SB_TRANSACTION_ADJUSTMENT+"'";
		String qry ="FROM SparkBaseTransactions WHERE status='NEW' AND contactId is NOT NULL "
				+ "AND userId is NOT NULL AND type in ("+typeStr+")";
		
		logger.debug("Fetch All ew Trasaction qry is :::::"+qry);
		return listOfnewTransaction = getHibernateTemplate().find(qry);
		
		
		
	}

	public Double getTotAmtIssued(Long userId, String startDate,String endDate) {
		
		String query = "SELECT SUM(amountEntered) FROM SparkBaseTransactions WHERE userId ="+userId+" "
				+ "AND type ='"+OCConstants.SPARKBASE_TRANSACTION_TYPE_LOYALTY_ISSUANCE+"'"
				+ "AND createdDate  BETWEEN '"+startDate+"' AND '"+endDate+"'  ";

		logger.info("findAmountIssued ...query >>>> .."+query);
		List tempList = getHibernateTemplate().find(query);
		if(tempList != null && tempList.size() == 1) {
			if(tempList.get(0) != null) {
				return (Double)tempList.get(0);
			}else return 0.0;
		}
		else return 0.0;
	}
	
	
	*/
}
