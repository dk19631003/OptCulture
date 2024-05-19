package org.mq.captiway.scheduler.dao;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import org.mq.captiway.scheduler.beans.SparkBaseTransactions;
import org.springframework.jdbc.core.JdbcTemplate;
import org.mq.captiway.scheduler.dao.AbstractSpringDao;
import org.mq.captiway.scheduler.utility.Constants;

public class SparkBaseTransactionsDaoForDML extends AbstractSpringDaoForDML implements Serializable{
	
	public SparkBaseTransactionsDaoForDML(){}
	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	public void save(SparkBaseTransactions sparkBaseTransactions) throws Exception {
		super.save(sparkBaseTransactions);
	}
	
	/*public SparkBaseTransactions find(Long id) {
		String qry = "FROM SparkBaseTransactions WHERE id="+id;
		List<SparkBaseTransactions> list = getHibernateTemplate().find(qry);
		return list.get(0);
//		return (SparkBaseTransactions) super.find(SparkBaseTransactions.class, id);
	}
	*/
	
	public void saveByCollection(Collection<SparkBaseTransactions> collectionObj) {
		getHibernateTemplate().saveOrUpdateAll(collectionObj);
	}
	
	/*public List<SparkBaseTransactions> findAllNewTransaction(){
		
		List<SparkBaseTransactions> listOfnewTransaction = null;
		String typeStr= "'"+Constants.SB_TRANSACTION_LOYALTY_ISSUANCE+"','"+Constants.SB_TRANSACTION_GIFT_ISSUANCE+"','"+
				Constants.SB_TRANSACTION_LOYALTY_REDEMPTION+"','"+
				Constants.SB_TRANSACTION_GIFT_REDEMPTION+"','"+
				Constants.SB_TRANSACTION_ADJUSTMENT+"'";
		return listOfnewTransaction = getHibernateTemplate().find("FROM SparkBaseTransactions WHERE status='NEW' AND cardId is not null AND type in ("+typeStr+")");
		
		
		
	}*/
	
	
	
}
