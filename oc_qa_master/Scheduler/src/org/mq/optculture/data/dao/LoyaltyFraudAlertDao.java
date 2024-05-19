
package org.mq.optculture.data.dao;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.LoyaltyFraudAlert;
import org.mq.captiway.scheduler.beans.LoyaltyProgram;
import org.mq.captiway.scheduler.beans.LoyaltyTransactionChild;
import org.mq.captiway.scheduler.beans.UserEmailAlert;
import org.mq.captiway.scheduler.dao.AbstractSpringDao;
import org.mq.captiway.scheduler.utility.Constants;
import org.mq.optculture.utils.OCConstants;
import org.springframework.jdbc.core.JdbcTemplate;

public class LoyaltyFraudAlertDao extends AbstractSpringDao implements Serializable{
	private JdbcTemplate jdbcTemplate;
	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
public List<LoyaltyFraudAlert> findByUserId(Long userId) {
		
		String query = " FROM LoyaltyFraudAlert WHERE createdByUserId = " + userId;
		List<LoyaltyFraudAlert>  list=   getHibernateTemplate().find(query);
		if(list!=null && list.size()>0) 
		return list;
		return null;
			
	}
public List<LoyaltyFraudAlert> findSentEmailRule(Long userId,boolean enable) {
	
	String query = " FROM LoyaltyFraudAlert WHERE createdByUserId = " + userId+" AND enabled="+enable;
	List<LoyaltyFraudAlert>  list=   getHibernateTemplate().find(query);
	if(list!=null && list.size()>0) 
	return list;
	return null;
		
}
public List<Map<String, Object>> getFraudAlertTrxByUserId(Long userId ,String havingCondition,String dateCondition){
	String subquery="select count(temp.membership_number) as no_of_cards,sum(temp.num_of_trx_count)as total_trx ,sum(temp.issuance_count) as total_issuance "
			+ ",sum(temp.redemption_count) as total_redemption ,sum(temp.issuance_amt) as total_issuance_amt ,sum(temp.redemption_amt) as total_redemption_amt From ("; 
    String query=subquery+"SELECT  SUM(if(transaction_type in('Enrollment','Issuance','Redemption','Return','Transfer'),1,0)) as num_of_trx_count , membership_number,SUM(if(transaction_type='Issuance',1,0)) as issuance_count ,sum(if(transaction_type='Redemption',1,0)) as redemption_count,"
			+"SUM(if(transaction_type='Issuance',earned_amount,0)) as issuance_amt,SUM(if(transaction_type='Issuance',earned_points,0)) as issuance_point ,SUM(if(transaction_type='Redemption',entered_amount,0)) as redemption_amt FROM loyalty_transaction_child where user_id="+userId
    +	  dateCondition+" group by membership_number "+havingCondition+" order by num_of_trx_count DESC ) as temp";
 
   
	
	return  getJdbcTemplate().queryForList(query);
}
/*public int getCountFraudAlertTrx(Long userId,String trxRule,String dateRule){
	String havingCondition=getTrxRule(trxRule); 
	String dateCondition=getDateRule(dateRule);
	String query="SELECT  SUM(if(transaction_type in('Enrollment','Issuance','Redemption'),1,0)) as num_of_trx_count , membership_number,SUM(if(transaction_type='Issuance',1,0)) as issuance_count ,sum(if(transaction_type='Redemption',1,0)) as redemption_count,"
			+"SUM(if(transaction_type='Issuance',earned_amount,0)) as issuance_amt ,SUM(if(transaction_type='Redemption',conversion_amt,0)) as redemption_amt FROM loyalty_transaction_child where user_id="+userId
     +	  dateCondition+"  group by membership_number "+havingCondition ;
	
	return  getJdbcTemplate().queryForList(query).size();
}*/

 

 public LoyaltyFraudAlert getFraudAlertRuleByName(Long userId ,String ruleName){
	 String query="From LoyaltyFraudAlert WHERE createdByUserId="+userId+" AND ruleName= '"+ruleName+"'";
	logger.info("Name query "+query);
	 List<LoyaltyFraudAlert>  list=   getHibernateTemplate().find(query);
		if(list!=null && list.size()>0) 
		return list.get(0);
		return null;
	 
 }
 public List<Long> getAllusersId(){
	 String query="select distinct(created__by_userId) from loyalty_fraud_alert";
		List<Long> list=new ArrayList<>();
		
		List<Map<String ,Object>> rows = getJdbcTemplate().queryForList(query);
		for(Map map:rows){
			list.add((Long) map.get("created__by_userId"));
		}
		if(list!=null && list.size()>0) 
	return list;
		return null;
 }
	
}
