
package org.mq.marketer.campaign.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import org.mq.marketer.campaign.beans.DRSent;
import org.mq.marketer.campaign.general.Constants;
import org.mq.optculture.exception.BaseDAOException;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

public class DRSentDaoForDML extends AbstractSpringDaoForDML {
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);

	private SessionFactory sessionFactory;

	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}


	/*public DRSent find(Long id) {
		return (DRSent) super.find(DRSent.class, id);																																																																																																																																																																																																																																																						
	}*/

	public void saveOrUpdate(DRSent drSent) {
		super.saveOrUpdate(drSent);
	}

	public void delete(DRSent drSent) {
		super.delete(drSent);
	}
	/*public int findTotalReciepients(Long userId, String key){
		String	 qry ="SELECT  COUNT(Distinct emailId) FROM  DRSent  WHERE userId ="+userId.longValue()+" " ;
		if(key != null){
			qry += " AND email_id like '%"+key+"%'";
		}
		
		return ((Long)getHibernateTemplate().find(qry).get(0)).intValue();
	}
	public int findDRBasedOnDatesForStore(Long userId, String fromDate,String toDate, String key, String store, String emailSatusTobeFetched){
		if(store == null){
			return findDRBasedOnDates(userId, fromDate,toDate,key,emailSatusTobeFetched);
		}else{
			String qry = "SELECT  COUNT(id) FROM  DRSent  WHERE userId ="+userId.longValue()+" " +
					" AND sentDate >= '"+fromDate+"' AND sentDate <='"+toDate+"' AND storeNumber like '"+store+"'";
		
		if(key != null){
			qry += " AND email_id like '%"+key+"%'";
		}
		
		if(! emailSatusTobeFetched.contains("special_condtion_for_all") ){
			qry += "  AND status IN("+emailSatusTobeFetched+")  ";
		}
		
		String	 qry ="SELECT  COUNT(Distinct emailId) FROM  DRSent  WHERE userId ="+userId.longValue()+" " +
					" AND sentDate >= '"+fromDate+"' AND sentDate <='"+toDate+"'";

		int count =   ((Long)getHibernateTemplate().find(qry).get(0)).intValue();
		
		return count;
		}
		
	}
	public int findStoreCountBasedOnDates(Long userId, String fromDate,String toDate, String store, String emailSatusTobeFetched){
		String qry = "SELECT  COUNT(Distinct storeNumber) FROM  DRSent  WHERE userId ="+userId.longValue()+" " +
				" AND sentDate >= '"+fromDate+"' AND sentDate <='"+toDate+"'";
		if(store != null){
			qry += " AND storeNumber = '"+store+"'";
		}
		
		if(! emailSatusTobeFetched.contains("special_condtion_for_all") ){
			qry += "  AND status IN("+emailSatusTobeFetched+")  ";
		}
		
		int count =   ((Long)getHibernateTemplate().find(qry).get(0)).intValue();
		
		return count;
	}
	public int findDRBasedOnDates(Long userId, String fromDate,String toDate,String key, String emailSatusTobeFetched) {
		String qry = "SELECT  COUNT(id) FROM  DRSent  WHERE userId ="+userId.longValue()+" " +
					" AND sentDate >= '"+fromDate+"' AND sentDate <='"+toDate+"'";
		
		if(key != null){
			qry += " AND email_id like '%"+key+"%'";
		}
		
		if(! emailSatusTobeFetched.contains("special_condtion_for_all") ){
			qry += "  AND status IN("+emailSatusTobeFetched+")  ";
		}
		
		
	 * 
	 * 
		String	 qry ="SELECT  COUNT(Distinct emailId) FROM  DRSent  WHERE userId ="+userId.longValue()+" " +
					" AND sentDate >= '"+fromDate+"' AND sentDate <='"+toDate+"'";

		int count =   ((Long)getHibernateTemplate().find(qry).get(0)).intValue();
		
		return count;
	} // findCoupBasedOnDates
	public List<String> findAllStores(Long userId){
		try{
			List<String> list = null;
			String query = "SELECT DISTINCT storeNumber from DRSent WHERE storeNumber IS NOT NULL AND userId = "+userId.longValue();
			list = executeQuery(query);
			return list;
		}catch(Exception e){
			logger.info("Exception while getting stores ",e);
		}
		return new ArrayList<String>();
	}
	public long findTotalStorePurchases(Long userId, String fromDate,String toDate, String store, String emailSatusTobeFetched){
		List<Object[]> list = null;
		String query = "SELECT COUNT(id) FROM DRSent WHERE storeNumber IS NOT NULL AND userId = "+userId.longValue()+
						" AND sentDate >= '"+fromDate+"' AND sentDate <= '"+toDate+"' " ;
		
		
		
	        
		if(! emailSatusTobeFetched.contains("special_condtion_for_all") ){
			query += "  AND status IN("+emailSatusTobeFetched+")  ";
		}
		
			if(store != null) query += " AND storeNumber = '"+store+"'";
								query += "GROUP BY userId";
								
				list = executeQuery(query);
				if(list.size() >0){
					return ((Long)(executeQuery(query).get(0))).longValue();
				}
		return 0;
	}
	public List<Object[]> findStoreReports(Long userId,int start,int end,String fromDate,String toDate, String store, String emailSatusTobeFetched){
		try{
			List<Object[]> list = null;
			String query = "SELECT storeNumber, COUNT(id)  FROM DRSent WHERE storeNumber IS NOT NULL AND userId = "+userId.longValue()+
							" AND sentDate >= '"+fromDate+"' AND sentDate <= '"+toDate+"' " ;
			
			
			if(! emailSatusTobeFetched.contains("special_condtion_for_all") ){
				query += "  AND status IN("+emailSatusTobeFetched+")  ";
			}
				if(store != null) query += " AND storeNumber = '"+store+"'";
									query += " GROUP BY storeNumber ORDER BY 2 DESC";
			list = executeQuery(query, start, end);
			return list;
		}catch (Exception e) {
			logger.info(" Exception while getting stores ",e);
		}
		return null;
	}
	public Map findTotalRecipientsReport(Long userId, String key){
		
		String subQry="SELECT SUM(IF(dr.opens >0, 1,0)) as uniopens,SUM(IF(dr.clicks >0, 1,0)) as uniclicks," +
				"Count(dr.id) as totalSentCount FROM dr_sent dr where   dr.user_id= "+userId.longValue();
		
			if(key != null){

					subQry += " AND dr.email_id like '%"+key+"%'";
			}

			String query  = subQry +" group by dr.user_id";
			List l =  getJdbcTemplate().queryForList(query);
			if(l != null && l.size() >0){
				return  (Map) l.get(0);
			}
			return null;
	}
	public List<DRSent> findRecipientsReports(Long userId,int start,int end, String key){

		
		try {
			List<DRSent > list = null;
			String subQry="SELECT dr.email_id, MAX(dr.sent_date) as lastSent, SUM(IF(dr.opens >0, 1,0)) as uniopens,SUM(IF(dr.clicks >0, 1,0)) as uniclicks," +
							"Count(dr.id) as totalSentCount,dr.id FROM dr_sent dr where   dr.user_id= "+userId.longValue();
					
			if(key != null){

				subQry += " AND dr.email_id like '%"+key+"%'";
			}
			
			String query  = subQry +" group by dr.email_id  ORDER BY lastSent DESC  LIMIT "+" "+start+","+end ;
			String query  = " SELECT emailId,MAX(sentDate), SUM(IF(opens >0, 1,0)) as o,SUM(IF(clicks >0, 1,0)) as c,status,Count(id),id FROM DRSent WHERE userId ="+userId.longValue()+" " +
					" AND sentDate >= '"+fromDate+"' AND sentDate <='"+toDate+"'  group by emailId ";
			
			String query  = " SELECT emailId,MAX(sentDate), SUM(IF(opens >0, 1,0)),SUM(IF(clicks >0, 1,0)),status,Count(id),id FROM DRSent WHERE userId ="+userId.longValue()+" " +
							" AND sentDate >= '"+fromDate+"' AND sentDate <='"+toDate+"'  group by emailId ";
			
			
			
			list = jdbcTemplate.query(query, new RowMapper() {
				
				 public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
			            DRSent drSent= new DRSent();
			            drSent.setId(rs.getLong("id"));
			            drSent.setEmailId(rs.getString("email_id"));
			            drSent.setUniqueOpens( rs.getInt("uniopens"));
			            drSent.setUniqueClicks( rs.getInt("uniclicks"));
			            drSent.setSentCount( rs.getInt("totalSentCount"));
			            return drSent;
			        }
			    });
			//list = getHibernateTemplate().find(query);
			//list = executeQuery(query, start, end);
			
			if(list!= null && list.size()>0) {
				return list;
			}
				
			return null;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
			return null;
		}
	
	}
	
	public List< DRSent> findDrReports(Long userId,int start,int end,String fromDate,String toDate,String key, String store,String emailSatusTobeFetched,String orderby_colName,String desc_Asc) {
		
		try {
			List<DRSent > list = null;
			String subQry="SELECT dr.email_id,MAX(dr.sent_date) as lastSent, SUM(IF(dr.opens >0, 1,0)) as uniopens,SUM(IF(dr.clicks >0, 1,0)) as uniclicks," +
					"(SELECT status from dr_sent  where user_id= "+userId.longValue()+ " and sent_date  >='"+fromDate+"' and  sent_date <= '"+toDate+"' " +
							"AND email_id=dr.email_id order by sent_date desc limit 1 )as status," +
							"Count(dr.id) as totalSentCount,dr.id FROM dr_sent dr where   dr.user_id= "+userId.longValue()+
					" and dr.sent_date  >= '"+fromDate+"' and  dr.sent_date <= '"+toDate+"'";
			String query  = " SELECT emailId,MAX(sentDate), SUM(IF(opens >0, 1,0)) as o,SUM(IF(clicks >0, 1,0)) as c,status,Count(id),id FROM DRSent WHERE userId ="+userId.longValue()+" " +
					" AND sentDate >= '"+fromDate+"' AND sentDate <='"+toDate+"'  group by emailId ";
			
			String query  = " SELECT emailId,MAX(sentDate), SUM(IF(opens >0, 1,0)),SUM(IF(clicks >0, 1,0)),status,Count(id),id FROM DRSent WHERE userId ="+userId.longValue()+" " +
							" AND sentDate >= '"+fromDate+"' AND sentDate <='"+toDate+"'  group by emailId ";
			String subQry="SELECT dr.id, dr.sent_date, dr.email_id, dr.status, dr.store_number, dr.opens, dr.clicks  " +
							" FROM dr_sent dr where   dr.user_id= "+userId.longValue()+
					" and dr.sent_date  >= '"+fromDate+"' and  dr.sent_date <= '"+toDate+"'";
			
			if(key != null){
				
				subQry += " AND dr.email_id like '%"+key+"%'";
			}if( store != null){
				subQry += " AND dr.store_number = '"+store+"'";
			}
			
			
			if(emailSatusTobeFetched.contains("_fetch_clicks_also_") && !emailSatusTobeFetched.contains("_fetch_opens_also_") && ! emailSatusTobeFetched.contains("special_condtion_for_all")){
				subQry += " AND dr.clicks is not null AND dr.clicks !=0  ";
				
			}
			else if(emailSatusTobeFetched.contains("_fetch_opens_also_") && !emailSatusTobeFetched.contains("_fetch_clicks_also_") && ! emailSatusTobeFetched.contains("special_condtion_for_all")){
				subQry += " AND dr.opens is not null AND dr.opens !=0  ";
			}
			else if(emailSatusTobeFetched.contains("_fetch_opens_also_") && emailSatusTobeFetched.contains("_fetch_clicks_also_") && ! emailSatusTobeFetched.contains("special_condtion_for_all")){
				subQry += " AND ((dr.opens is not null AND dr.opens !=0)   AND (dr.clicks is not null AND dr.clicks !=0))   ";
			}
			
			
			if(! emailSatusTobeFetched.contains("special_condtion_for_all") ){
				subQry += "  AND dr.status IN("+emailSatusTobeFetched+")  ";
			}
			
			
			
			
			
			
			
			
			
			
			
			
			if(((emailSatusTobeFetched.contains("dropped") || emailSatusTobeFetched.contains("bounce")) && ! emailSatusTobeFetched.contains("special_condtion_for_all"))){ 
				
				if( !emailSatusTobeFetched.contains("Success") && !emailSatusTobeFetched.contains("spamreport")){
					subQry += "  AND (dr.status='bounce' OR dr.status='dropped')  ";
				}else {
					subQry += "  AND (dr.status='bounce' OR dr.status='dropped')   AND  (dr.status='spamreport' OR dr.status='Success')   ";
				}
				
			}
			
			
			
			
			
			
			
			
			
			String query;
			if(start != -1){
				query  = subQry +" ORDER BY "+orderby_colName+" DESC  LIMIT "+" "+start+","+end ;
			}else{
				query  = subQry +" ORDER BY "+orderby_colName+" "+desc_Asc ;
			}
			
			
			
			logger.info("query >>> "+query);
			
			list = jdbcTemplate.query(query, new RowMapper() {
				
				 public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
			            DRSent drSent= new DRSent();
			            drSent.setId(rs.getLong("id"));
			            Calendar cal = Calendar.getInstance();
			            cal.setTime(rs.getTimestamp("sent_date"));
			            
			            drSent.setSentDate(cal);
			            drSent.setEmailId(rs.getString("email_id"));
			            
			            
			            drSent.setStatus( rs.getString("status"));
			            drSent.setStoreNumber(rs.getString("store_number"));
			            
			            
			            drSent.setOpens(Integer.parseInt(rs.getString("opens")));
			            drSent.setClicks(Integer.parseInt(rs.getString("clicks")));
			            
			
				 public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
					 
					 Object[] drReportArr =  new Object[7];;
					 drReportArr[0] = rs.getString(1);
					 Calendar cal = Calendar.getInstance();
		            	cal.setTime(rs.getTimestamp(2));
		            	
		            	drReportArr[1] = cal;
		            	drReportArr[2] = rs.getInt(3);
		            	drReportArr[3] = rs.getInt(4);
		            	drReportArr[4] = rs.getString(5);
		            	drReportArr[5] = rs.getInt(6);
		            	drReportArr[6] = rs.getLong(7);
					 
					return drReportArr;
				 }
				
			});
			
			            
			            return drSent;
			        }
			    });
			//list = getHibernateTemplate().find(query);
			//list = executeQuery(query, start, end);
			
			if(list!= null && list.size()>0) {
				return list;
			}
				
			return null;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
			return null;
		}
	}
	
	
	
	public long findTotSentCount(Long userId,String fromDate,String toDate, String store){
		String qry="SELECT COUNT(id) FROM DRSent WHERE userId ="+userId.longValue()+"  AND sentDate >= '"+fromDate+"' AND sentDate <='"+toDate+"'";
		if(store !=null && !store.trim().isEmpty()){
			qry += " AND storeNumber = '"+store+"'";
		}
		Long count =   ((Long)getHibernateTemplate().find(qry).get(0)).longValue();
		
		return count;
		
		
	}
	
	public  long findTotDRSentCount(Long userId){
		String qry="SELECT COUNT(id) FROM DRSent WHERE userId ="+userId.longValue()+" ";
		
		Long count =   ((Long)getHibernateTemplate().find(qry).get(0)).longValue();
		
		return count;
		
	}
	
	public long findOpenCount(Long userId,String fromDate,String toDate, String store){
		String qry="SELECT COUNT(id) FROM DRSent WHERE userId ="+userId.longValue()+"  AND sentDate >= '"+fromDate+"' AND sentDate <='"+toDate+"'" +" AND opens > 0";
		if(store !=null && !store.trim().isEmpty()){
			qry += " AND storeNumber = '"+store+"'";
		}
		Long count =   ((Long)getHibernateTemplate().find(qry).get(0)).longValue();
		return count;
		
		
	}//findOpenCount
	
	public long findClickCount(Long userId,String fromDate,String toDate, String store){
		String qry="SELECT  COUNT(id) FROM DRSent WHERE userId ="+userId.longValue()+"  AND sentDate >= '"+fromDate+"' AND sentDate <='"+toDate+"'"+" AND clicks > 0";
		if(store !=null && !store.trim().isEmpty()){
			qry += " AND storeNumber = '"+store+"'";
		}
		Long count =   ((Long)getHibernateTemplate().find(qry).get(0)).longValue();
		
		return count;
		
		
	}//findOpenCount
	
	

	public long findTotBounceCount(Long userId,String fromDate,String toDate, String store){
		String qry="SELECT COUNT(id) FROM DRSent WHERE userId ="+userId.longValue()+""+
						"   AND  sentDate >= '"+fromDate+"' AND sentDate <='"+toDate+"'  AND  " +
								" status in('" +Constants.DR_STATUS_BOUNCED+"','"+Constants.DR_STATUS_DROPPED+"','" +Constants.DR_STATUS_BOUNCE+"') ";
		if(store !=null && !store.trim().isEmpty()){
			qry += " AND storeNumber = '"+store+"'";
		}	
		
		Long count =   ((Long)getHibernateTemplate().find(qry).get(0)).longValue();
		
		return count;
		
		
	}
	
	public long findTotSpammedCount(Long userId,String fromDate,String toDate, String store){
		String qry="SELECT COUNT(id) FROM DRSent WHERE userId ="+userId.longValue()+""+
						"  AND sentDate >= '"+fromDate+"' AND sentDate <='"+toDate+"'  AND   " +
								" status in ('" +Constants.DR_STATUS_SPAMMED+"' ,'" +Constants.DR_STATUS_SPAME+"')" ;
		if(store !=null && !store.trim().isEmpty()){
			qry += " AND storeNumber = '"+store+"'";
		}	
		
		Long count =   ((Long)getHibernateTemplate().find(qry).get(0)).longValue();
		
		return count;
		
		
	}
	
	public DRSent findById(long id){
		DRSent deSent = null;
		try {
			List list = getHibernateTemplate().find(
					"from DRSent where id = " + id );
			if (list.size() > 0) {
				deSent = (DRSent) list.get(0);
			}
		} catch (Exception e) {
			logger.error(" ** Exception : ", e ); 
		}
		return deSent;
		
	}
	
	
	public DRSent findDrById(String email, Long userId){
		DRSent deSent = null;
		try {
			List list = getHibernateTemplate().find(
					"FROM DRSent WHERE userId="+userId.longValue()+" AND emailId = '" + email +"' order by sentDate desc");
			if (list.size() > 0) {
				deSent = (DRSent) list.get(0);
			}
		} catch (Exception e) {
			logger.error(" ** Exception : ", e ); 
		}
		return deSent;
		
	}*/
	/**
	 * update open count whenever user openDR
	 * @param sentId
	 * @return
	 */
	public int updateOpenCount(Long sentId) {
		try {
			String qry=" UPDATE DRSent SET opens = (opens+1) WHERE id = "+ sentId.longValue();
					
			return executeUpdate(qry);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
			return 0;
		}
		
	}//updateOpenCount
	
	/**
	 * update click count whenever user clicks on link in DR
	 * @param sentId
	 * @return
	 */
	public int updateClickCount(Long sentId) {
		try {
			String qry=" UPDATE DRSent SET clicks = (clicks+1) WHERE id = "+ sentId.longValue();
					
			return executeUpdate(qry);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
			return 0;
		}
	
	}//updateClickCount()
	
	/**
	 * to calculate sent rate for DR reports between given dates
	 * @param userId
	 * @param fromDate
	 * @param toDate
	 * @param type
	 * @return
	 */
/*	public List<Map<String,Object>> findTotSentRate(Long userId,String fromDate,String toDate, String type, String store, int tzOffSet){
		String qry="";
		if(type.equals("Days")){
			
			qry="SELECT COUNT(id) AS count, DAY(DATE_ADD(sent_date, INTERVAL "+tzOffSet+" MINUTE)) AS day FROM dr_sent WHERE user_id ="+userId.longValue()+" " +" AND sent_date >= '"+fromDate+"' AND sent_date <='"+toDate+"'   " ;
			if(store !=null && !store.trim().isEmpty()){
				qry += " AND store_number = '"+store+"'";
			}
					qry += " GROUP BY  DAY(DATE_ADD(sent_date, INTERVAL "+tzOffSet+" MINUTE)) ORDER BY 2";
		}else{
			qry="SELECT COUNT(id) AS count, MONTH(DATE_ADD(sent_date, INTERVAL "+tzOffSet+" MINUTE)) AS month FROM dr_sent WHERE user_id ="+userId.longValue()+" " +" AND sent_date >= '"+fromDate+"' AND sent_date <='"+toDate+"'   " ;
					if(store !=null && !store.trim().isEmpty()){
						qry += " AND store_number = '"+store+"'";
					}
					qry += " GROUP BY  MONTH(DATE_ADD(sent_date, INTERVAL "+tzOffSet+" MINUTE)) ORDER BY 2";

		}
	return	jdbcTemplate.queryForList(qry);
			
					
	}//findTotSentRate()
	*//**
	 * to calculate Delivered rate for DR reports between given dates
	 * @param userId
	 * @param fromDate
	 * @param toDate
	 * @param type
	 * @return
	 *//*

	public List<Map<String,Object>> findTotDelRate(Long userId,String fromDate,String toDate, String type, String store, int tzOffSet ){
		String qry="";
		if(type.equals("Days")){
			
			qry="SELECT COUNT(id) AS count, DAY(DATE_ADD(sent_date, INTERVAL "+tzOffSet+" MINUTE)) AS day FROM dr_sent WHERE user_id ="+userId.longValue()+" " +" AND sent_date >= '"+fromDate+"' AND sent_date <='"+toDate+"' AND status='"+Constants.DR_STATUS_SUCCESS+"'";
					if(store !=null && !store.trim().isEmpty()){
						qry += " AND store_number = '"+store+"'";
					}
			qry += "   GROUP BY  DAY(DATE_ADD(sent_date, INTERVAL "+tzOffSet+" MINUTE)) ORDER BY 2";
		}else{
			qry="SELECT  COUNT(id) AS count, MONTH(DATE_ADD(sent_date, INTERVAL "+tzOffSet+" MINUTE)) AS month FROM dr_sent WHERE user_id ="+userId.longValue()+" " +" AND sent_date >= '"+fromDate+"' AND sent_date <='"+toDate+"' AND  status='"+Constants.DR_STATUS_SUCCESS+"'";
			if(store !=null && !store.trim().isEmpty()){
				qry += " AND store_number = '"+store+"'";
			}
			qry += "   GROUP BY  MONTH(DATE_ADD(sent_date, INTERVAL "+tzOffSet+" MINUTE)) ORDER BY 2";

		}
		return  getJdbcTemplate().queryForList(qry);
		
		
		
	}//findTotDelRate
	*//***
	 * to calculate unique opens  rate for DR reports between given dates
	 * @param userId
	 * @param userId
	 * @param fromDate
	 * @param toDate
	 * @param type
	 * @return
	 *//*

	public List<Map<String, Object>> findTotOpenRate(Long userId,String fromDate,String toDate, String type, String store, int tzOffSet){
		String qry="";
		if(type.equals("Days")){
			
			qry="SELECT COUNT(id) AS count, DAY(DATE_ADD(sent_date, INTERVAL "+tzOffSet+" MINUTE)) AS day FROM dr_sent WHERE user_id ="+userId.longValue()+" " +" AND sent_date >= '"+fromDate+"' AND sent_date <='"+toDate+"' AND opens > 0";
					if(store !=null && !store.trim().isEmpty()){
						qry += " AND store_number = '"+store+"'";
					}
			qry += "     GROUP BY  DAY(DATE_ADD(sent_date, INTERVAL "+tzOffSet+" MINUTE)) ORDER BY 2";
		}else{
			qry="SELECT COUNT(id) AS count, MONTH(DATE_ADD(sent_date, INTERVAL "+tzOffSet+" MINUTE)) AS month FROM dr_sent WHERE user_id ="+userId.longValue()+" " +" AND sent_date >= '"+fromDate+"' AND sent_date <='"+toDate+"'  AND opens > 0" ;
					if(store !=null && !store.trim().isEmpty()){
						qry += " AND store_number = '"+store+"'";
					}
			qry += "    GROUP BY  MONTH(DATE_ADD(sent_date, INTERVAL "+tzOffSet+" MINUTE)) ORDER BY 2";

		}
		return  getJdbcTemplate().queryForList(qry);
		
		
		
	}//findTotOpenRate()
	*//**
	 * to calculate unique clicks  rate for DR reports between given dates
	 * @param userId
	 * @param fromDate
	 * @param toDate
	 * @param type
	 * @return
	 *//*

	public List<Map<String, Object>> findTotClickRate(Long userId,String fromDate,String toDate, String type, String store, int tzOffSet){
		String qry="";
		if(type.equals("Days")){
			
			qry="SELECT COUNT(id) AS count, DAY(DATE_ADD(sent_date, INTERVAL "+tzOffSet+" MINUTE)) AS day FROM dr_sent WHERE user_id ="+userId.longValue()+" " +" AND sent_date >= '"+fromDate+"' AND sent_date <='"+toDate+"'  AND clicks > 0";
					if(store !=null && !store.trim().isEmpty()){
						qry += " AND store_number = '"+store+"'";
					}
			qry += "    GROUP BY  DAY(DATE_ADD(sent_date, INTERVAL "+tzOffSet+" MINUTE)) ORDER BY 2";
		}else{
			qry="SELECT COUNT(id) AS count, MONTH(DATE_ADD(sent_date, INTERVAL "+tzOffSet+" MINUTE)) AS month FROM dr_sent WHERE user_id ="+userId.longValue()+" " +" AND sent_date >= '"+fromDate+"' AND sent_date <='"+toDate+"'  AND clicks > 0" ;
					if(store !=null && !store.trim().isEmpty()){
						qry += " AND store_number = '"+store+"'";
					}
			qry += "   GROUP BY  MONTH(DATE_ADD(sent_date, INTERVAL "+tzOffSet+" MINUTE)) ORDER BY 2";

		}
		return  getJdbcTemplate().queryForList(qry);
		
		
		
	}//findTotClickRate
	*//**
	 * to calculate bounce  rate for DR reports between given dates
	 * @param userId
	 * @param fromDate
	 * @param toDate
	 * @param type
	 * @return
	 *//*
	
	public List<Map<String,Object>> findTotBounceRate(Long userId,String fromDate,String toDate, String type, String store, int tzOffSet){
		String qry="";
		if(type.equals("Days")){
			
			qry="SELECT COUNT(id) AS count, DAY(DATE_ADD(sent_date, INTERVAL "+tzOffSet+" MINUTE)) AS day FROM dr_sent WHERE user_id ="+userId.longValue()+" " +" AND sent_date >= '"+fromDate+"' AND sent_date <='"+toDate+"' AND status in('"+Constants.DR_STATUS_BOUNCED+"','"+Constants.DR_STATUS_DROPPED+"','" +Constants.DR_STATUS_BOUNCE+"')";
					if(store !=null && !store.trim().isEmpty()){
						qry += " AND store_number = '"+store+"'";
					}
			qry += "   GROUP BY  DAY(DATE_ADD(sent_date, INTERVAL "+tzOffSet+" MINUTE)) ORDER BY 2";
		}else{
			qry="SELECT COUNT(id) AS count, MONTH(DATE_ADD(sent_date, INTERVAL "+tzOffSet+" MINUTE)) AS month FROM dr_sent WHERE user_id ="+userId.longValue()+" " +" AND sent_date >= '"+fromDate+"' AND sent_date <='"+toDate+"' AND  status in('"+Constants.DR_STATUS_BOUNCED+"','"+Constants.DR_STATUS_DROPPED+"','" +Constants.DR_STATUS_BOUNCE+"')" ;
					if(store !=null && !store.trim().isEmpty()){
						qry += " AND store_number = '"+store+"'";
					}
			qry += "   GROUP BY  MONTH(DATE_ADD(sent_date, INTERVAL "+tzOffSet+" MINUTE)) ORDER BY 2";

		}
		return  getJdbcTemplate().queryForList(qry);
		
		
		
	}//findTotClickRate()
	
	//all view 
	public List <DRSent> findAllDrView(String email,long userId){
		try {
			return getHibernateTemplate().find(
					"FROM DRSent WHERE userId="+userId+" AND emailId = '" + email + "'"+"order by sentDate desc");
		} catch (Exception e) {
			logger.error(" ** Exception : ", e ); 
			return null;
		}
			
	}
	
	public int findConfiguredTemplateCount(Long userId, String templateName) {
			
			try {
				templateName = StringEscapeUtils.escapeSql(templateName);
				String qry = "SELECT COUNT(id) FROM DRSent WHERE userId ="+userId.longValue()+" " +" AND templateName='MY_TEMPLATE:"+templateName+"'"; 
				
				int count =   ((Long)getHibernateTemplate().find(qry).get(0)).intValue();
				
				return count;
			} catch (DataAccessException e) {
				// TODO Auto-generated catch block
				logger.error("Exception ::" , e);
				return 0;
			}
			
			
			
		}
	

	public long findTotDelCount(Long userId,String fromDate,String toDate, String store){
		String qry="SELECT COUNT(id) FROM DRSent WHERE userId ="+userId.longValue()+""+
						"   AND sentDate >= '"+fromDate+"' AND sentDate <='"+toDate+"' AND  status ='" +Constants.DR_STATUS_SUCCESS+"' ";
		if(store !=null && !store.trim().isEmpty()){
			qry += " AND storeNumber = '"+store+"'";
		}		
		
		Long count =   ((Long)getHibernateTemplate().find(qry).get(0)).longValue();
		
		return count;
		
		
	}
	// Added after DR Resend
	
	public DRSent findBy( Long userId , String docSid, String toEmailId ) throws Exception {
		
		List<DRSent> drsentObjList = null;
		
		String qryStr = "FROM DRSent WHERE userId ="+userId.longValue()+""
				+ " AND docSid= '"+docSid+"' and emailId='"+toEmailId+"' ORDER BY sentDate DESC" ;
		drsentObjList = getHibernateTemplate().find(qryStr);
		if(drsentObjList != null &&  drsentObjList.size() > 0){
			return drsentObjList.get(0);
		}else{
			return null;
		}
			
		
	}

	public DRSent findBy( Long userId , String docSid) throws Exception {
		
		List<DRSent> drsentObjList = null;
		
		String qryStr = "FROM DRSent WHERE userId ="+userId.longValue()+""
				+ " AND docSid= '"+docSid+"' ORDER BY sentDate DESC" ;
		drsentObjList = getHibernateTemplate().find(qryStr);
		if(drsentObjList != null &&  drsentObjList.size() > 0){
			return drsentObjList.get(0);
		}else{
			return null;
		}
			
		
	}*/
	
	public synchronized void updateSentCount(Long existingObjId) throws BaseDAOException{
		
		String qry = "UPDATE dr_sent set sent_count=(sent_count+1) WHERE id="+existingObjId.longValue();
		
		executeJdbcUpdateQuery(qry);
		
	}
	
	
	
	 public void setNewTemplateName(long userId, String newTemplateName, String selectedTemplate) throws Exception {
		 
		 String newTempName="MY_TEMPLATE:"+newTemplateName;
		 newTempName = StringEscapeUtils.escapeSql(newTempName);
		  selectedTemplate = StringEscapeUtils.escapeSql(selectedTemplate);

		 
				 String query = "UPDATE DRSent SET templateName='"+newTempName+"' WHERE templateName='"+selectedTemplate+"' AND userId="+userId;
				 logger.info("qry in de sent::::"+query);
				//
			int count= executeUpdate(query);
				 //getHibernateTemplate().bulkUpdate(query);
				 
			 }//setNewTemplateName()
	
}
