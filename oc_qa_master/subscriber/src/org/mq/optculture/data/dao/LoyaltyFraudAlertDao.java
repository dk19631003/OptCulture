
package org.mq.optculture.data.dao;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.mq.marketer.campaign.beans.LoyaltyFraudAlert;
import org.mq.marketer.campaign.beans.LoyaltyTransactionChild;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.AbstractSpringDao;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

public class LoyaltyFraudAlertDao extends AbstractSpringDao implements Serializable{
	private JdbcTemplate jdbcTemplate;
	
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
public List<Map<String, Object>> getFraudAlertTrxByUserId(Long userId ,String havingCondition,String dateCondition,int start,int end){
	
    String query="SELECT  SUM(if(transaction_type in('Enrollment','Issuance','Redemption','Return','Transfer'),1,0)) as num_of_trx_count , membership_number,SUM(if(transaction_type='Issuance',1,0)) as issuance_count ,sum(if(transaction_type='Redemption',1,0)) as redemption_count,"
			+"SUM(if(transaction_type='Issuance',earned_amount,0)) as issuance_amt ,SUM(if(transaction_type='Issuance',earned_points,0)) as issuance_point ,SUM(if(transaction_type='Redemption',entered_amount,0)) as redemption_amt FROM loyalty_transaction_child where "
       +	"user_id="+userId+" "+  dateCondition+"  group by membership_number "+havingCondition+" order by num_of_trx_count DESC  LIMIT "+start+" , "+end;
 
    logger.info("Query "+query);
	
	return  getJdbcTemplate().queryForList(query);
}
public int getCountFraudAlertTrx(Long userId,String havingCondition,String dateCondition){
	
	String query="SELECT  SUM(if(transaction_type in('Enrollment','Issuance','Redemption','Return','Transfer'),1,0)) as num_of_trx_count , membership_number,SUM(if(transaction_type='Issuance',1,0)) as issuance_count ,sum(if(transaction_type='Redemption',1,0)) as redemption_count,"
			+"SUM(if(transaction_type='Issuance',earned_amount,0)) as issuance_amt ,SUM(if(transaction_type='Issuance',earned_points,0)) as issuance_point,SUM(if(transaction_type='Redemption',entered_amount,0)) as redemption_amt FROM loyalty_transaction_child where "
       + "user_id="+userId	+" "+  dateCondition+"  group by membership_number "+havingCondition ;
	logger.info("count query "+query);
	return  getJdbcTemplate().queryForList(query).size();
}
 public List<LoyaltyTransactionChild> getAllFraudAlertTrxbyCardNo(Long userId,String cardNo,String havingCondition,String dateCondition){
		
	/*String query="SELECT * FROM LoyaltyTransactionChild where userId="+userId +" AND membershipNumber='"+cardNo+"'"+" AND transactionType in('Enrollment','Issuance','Redemption','Return','Transfer')"
       +	  dateCondition;
*/	
	String query="SELECT * FROM loyalty_transaction_child where user_id="+userId +" AND membership_number='"+cardNo+"'"+" AND transaction_type in('Enrollment','Issuance','Redemption','Return','Transfer')"
		       +	  dateCondition+" order by created_date desc";
			
	logger.info("All Trx query "+query);
	//List<LoyaltyTransactionChild> list=getHibernateTemplate().find(query);
	List<LoyaltyTransactionChild> list;
		list = jdbcTemplate.query(query, new RowMapper<LoyaltyTransactionChild>() {
			DateFormat df = new SimpleDateFormat( MyCalendar.FORMAT_DATETIME_STYEAR);					
			public LoyaltyTransactionChild mapRow(ResultSet result, int rowNum) throws SQLException {
				LoyaltyTransactionChild child=new LoyaltyTransactionChild();
				try {
					Date date = df.parse(result.getString("created_date"));
					Calendar cal = Calendar.getInstance();
					cal.setTime(date);
					child.setCreatedDate(cal);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				
				child.setDocSID(result.getString("docsid"));
				child.setStoreNumber(result.getString("store_number"));
				child.setTransactionType(result.getString("transaction_type"));
				child.setEnteredAmount(result.getDouble("entered_amount"));
				child.setAmountBalance(result.getDouble("amount_balance"));
				child.setAmountDifference(result.getString("amount_difference"));
				return child;
			}
		     
		});
		if(list!=null && list.size()>0) return list;
	
	return null;
 }
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
 public List<Map<String, Object>> getFraudAlertTrxByUserId(Long userId ,String havingCondition,String dateCondition){
		String subquery="select count(temp.membership_number) as no_of_cards,sum(temp.num_of_trx_count)as total_trx ,sum(temp.issuance_count) as total_issuance "
				+ ",sum(temp.redemption_count) as total_redemption ,sum(temp.issuance_amt) as total_issuance_amt ,sum(temp.redemption_amt) as total_redemption_amt From ("; 
	    String query=subquery+"SELECT  SUM(if(transaction_type in('Enrollment','Issuance','Redemption','Return','Transfer'),1,0)) as num_of_trx_count , membership_number,SUM(if(transaction_type='Issuance',1,0)) as issuance_count ,sum(if(transaction_type='Redemption',1,0)) as redemption_count,"
				+"SUM(if(transaction_type='Issuance',earned_amount,0)) as issuance_amt,SUM(if(transaction_type='Issuance',earned_points,0)) as issuance_point ,SUM(if(transaction_type='Redemption',entered_amount,0)) as redemption_amt FROM loyalty_transaction_child where user_id="+userId
	    +	  dateCondition+" group by membership_number "+havingCondition+" order by num_of_trx_count DESC ) as temp";
	 
	   
		
		return  getJdbcTemplate().queryForList(query);
	}
 }
