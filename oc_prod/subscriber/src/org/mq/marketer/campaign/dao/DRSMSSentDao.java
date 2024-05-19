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
import org.mq.marketer.campaign.beans.DRSMSSent;
import org.mq.marketer.campaign.general.Constants;
import org.mq.optculture.exception.BaseDAOException;
import org.mq.optculture.utils.OCConstants;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

public class DRSMSSentDao extends AbstractSpringDao {
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);

	private SessionFactory sessionFactory;

	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}


	public DRSMSSent find(Long id) {
		return (DRSMSSent) super.find(DRSMSSent.class, id);
	}

	/*public void saveOrUpdate(DRSMSSent drSmsSent) {
		super.saveOrUpdate(drSmsSent);
	}*/

	/*public void delete(DRSMSSent drSmsSent) {
		super.delete(drSmsSent);
	}*/
	public int findTotalReciepients(Long userId, String key){
		String	 qry ="SELECT  COUNT(Distinct mobile) FROM  DRSMSSent  WHERE userId ="+userId.longValue()+" " ;
		if(key != null){
			qry += " AND mobile like '%"+key+"%'";
		}

		return ((Long)getHibernateTemplate().find(qry).get(0)).intValue();
	}
	public int findDRBasedOnDatesForStore(Long userId, String fromDate,String toDate, String key, String store, String smsStatusTobeFetched){
		if(store == null){
			return findDRBasedOnDates(userId, fromDate,toDate,key,smsStatusTobeFetched);
		}else{
			String qry = "SELECT  COUNT(id) FROM  DRSMSSent  WHERE userId ="+userId.longValue()+" " +
					" AND sentDate >= '"+fromDate+"' AND sentDate <='"+toDate+"' AND storeNumber like '"+store+"'";

			if(key != null){
				qry += " AND mobile like '%"+key+"%'";
			}

			if(! smsStatusTobeFetched.contains("special_condtion_for_all") ){
				qry += "  AND status IN("+smsStatusTobeFetched+")  ";
			}
			/*	
		String	 qry ="SELECT  COUNT(Distinct emailId) FROM  DRSMSSent  WHERE userId ="+userId.longValue()+" " +
					" AND sentDate >= '"+fromDate+"' AND sentDate <='"+toDate+"'";
			 */
			int count =   ((Long)getHibernateTemplate().find(qry).get(0)).intValue();

			return count;
		}

	}
	public int findStoreCountBasedOnDates(Long userId, String fromDate,String toDate, String store, String emailSatusTobeFetched){
		String qry = "SELECT  COUNT(Distinct storeNumber) FROM  DRSMSSent  WHERE userId ="+userId.longValue()+" " +
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
	public int findDRBasedOnDates(Long userId, String fromDate,String toDate,String key, String smsStatusTobeFetched) {
		String qry = "SELECT  COUNT(id) FROM  DRSMSSent  WHERE userId ="+userId.longValue()+" " +
				" AND sentDate >= '"+fromDate+"' AND sentDate <='"+toDate+"'";

		if(key != null){
			qry += " AND mobile like '%"+key+"%'";
		}

		if(! smsStatusTobeFetched.contains("special_condtion_for_all") ){
			qry += "  AND status IN("+smsStatusTobeFetched+")  ";
		}

		/*	
		 * 
		 * 
		String	 qry ="SELECT  COUNT(Distinct emailId) FROM  DRSMSSent  WHERE userId ="+userId.longValue()+" " +
					" AND sentDate >= '"+fromDate+"' AND sentDate <='"+toDate+"'";
		 */
		int count =   ((Long)getHibernateTemplate().find(qry).get(0)).intValue();

		return count;
	} // findCoupBasedOnDates
	public List<String> findAllStores(Long userId){
		try{
			List<String> list = null;
			String query = "SELECT DISTINCT storeNumber from DRSMSSent WHERE storeNumber IS NOT NULL AND userId = "+userId.longValue();
			list = executeQuery(query);
			return list;
		}catch(Exception e){
			logger.info("Exception while getting stores ",e);
		}
		return new ArrayList<String>();
	}
	public long findTotalStorePurchases(Long userId, String fromDate,String toDate, String store, String emailSatusTobeFetched){
		List<Object[]> list = null;
		String query = "SELECT COUNT(id) FROM DRSMSSent WHERE storeNumber IS NOT NULL AND userId = "+userId.longValue()+
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
			String query = "SELECT storeNumber, COUNT(id)  FROM DRSMSSent WHERE storeNumber IS NOT NULL AND userId = "+userId.longValue()+
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

	public List<Object[]> findDeliveryStatusPendingReports(Long userId,int start,int end,String fromDate,String toDate, String store, String status){
		try{
			List<Object[]> list = null;
			String query = "SELECT mobile,sentDate FROM DRSMSSent WHERE userId = "+userId.longValue()+
					" AND sentDate >= '"+fromDate+"' AND sentDate <= '"+toDate+"' AND status IN('"+status+"') " ;


			if(store != null) query += " AND storeNumber = '"+store+"'";
			list = executeQuery(query, start, end);
			logger.info("query :"+query);
			return list;
		}catch (Exception e) {
			logger.info(" Exception while getting stores ",e);
		}
		return null;
	}
	public Map findTotalRecipientsReport(Long userId, String key){

		String subQry="SELECT SUM(IF(dr.opens >0, 1,0)) as uniopens,SUM(IF(dr.clicks >0, 1,0)) as uniclicks," +
				"Count(dr.id) as totalSentCount FROM dr_sms_sent dr where   dr.user_id= "+userId.longValue();

		if(key != null){

			subQry += " AND dr.mobile like '%"+key+"%'";
		}

		String query  = subQry +" group by dr.user_id";
		List l =  getJdbcTemplate().queryForList(query);
		if(l != null && l.size() >0){
			return  (Map) l.get(0);
		}
		return null;
	}
	public Map findTotalStatusPendingReport(Long userId,String fromDate, String toDate, String key, String status){

		String subQry="SELECT Count(dr.id) as totalPendingCount FROM dr_sms_sent dr where dr.user_id= "+userId.longValue()+
				" AND dr.sent_date >= '"+fromDate+"' AND dr.sent_date <= '"+toDate+"' AND dr.status='"+status+"'";

		if(key != null){

			subQry += " AND dr.mobile like '%"+key+"%'";
		}

		String query  = subQry +" group by dr.user_id";
		List l =  getJdbcTemplate().queryForList(query);
		if(l != null && l.size() >0){
			return  (Map) l.get(0);
		}
		return null;
	}
	public List<DRSMSSent> findRecipientsReports(Long userId,int start,int end, String key){


		try {
			List<DRSMSSent > list = null;
			String subQry="SELECT dr.mobile, MAX(dr.sent_date) as lastSent, SUM(IF(dr.opens >0, 1,0)) as uniopens,SUM(IF(dr.clicks >0, 1,0)) as uniclicks," +
					"Count(dr.id) as totalSentCount,dr.id, dr.doc_sid FROM dr_sms_sent dr where   dr.user_id= "+userId.longValue();

			if(key != null){

				subQry += " AND dr.mobile like '%"+key+"%'";
			}

			String query  = subQry +" group by dr.mobile  ORDER BY lastSent DESC  LIMIT "+" "+start+","+end ;



			list = jdbcTemplate.query(query, new RowMapper() {

				public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
					DRSMSSent drSmsSent= new DRSMSSent();
					drSmsSent.setId(rs.getLong("id"));
					drSmsSent.setMobile(rs.getString("mobile"));
					drSmsSent.setUniqueOpens( rs.getInt("uniopens"));
					drSmsSent.setUniqueClicks( rs.getInt("uniclicks"));
					drSmsSent.setSentCount( rs.getInt("totalSentCount"));
					drSmsSent.setDocSid(rs.getString("doc_sid"));
					return drSmsSent;
				}
			});

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


	public List<DRSMSSent> findStatusPendingReports(Long userId,int start,int end, String key){
		try {
			List<DRSMSSent > list = null;
			String subQry="SELECT dr.mobile,dr.sent_date FROM dr_sms_sent dr where dr.user_id= "+userId.longValue()+" and dr.status='Submitted'" ;

			if(key != null){

				subQry += " AND dr.mobile like '%"+key+"%'";
			}

			String query  = subQry +" group by dr.mobile  ORDER BY dr.sent_date DESC  LIMIT "+" "+start+","+end ;


			list = jdbcTemplate.query(query, new RowMapper() {

				public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
					DRSMSSent drSmsSent= new DRSMSSent();
					//drSmsSent.setId(rs.getLong("id"));
					drSmsSent.setMobile(rs.getString("mobile"));
					Calendar cal = Calendar.getInstance();
					cal.setTime(rs.getTimestamp("sent_date"));

					drSmsSent.setSentDate(cal);
					return drSmsSent;
				}
			});

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


	public List< DRSMSSent> findDrReports(Long userId,int start,int end,String fromDate,String toDate,String key, String store,String smsStatusTobeFetched,String orderby_colName,String desc_Asc) {

		try {
			List<DRSMSSent > list = null;
			String subQry="SELECT dr.id, dr.sent_date, dr.mobile, dr.status, dr.store_number, dr.SBS_no, dr.opens, dr.clicks ,dr.sent_count,dr.doc_sid  " + //,dr.html_content " +
					" FROM dr_sms_sent dr where   dr.user_id= "+userId.longValue()+
					" and dr.sent_date  >= '"+fromDate+"' and  dr.sent_date <= '"+toDate+"'";

			if(key != null){

				subQry += " AND dr.mobile like '%"+key+"%'";
			}if( store != null){
				subQry += " AND dr.store_number = '"+store+"'";
			}


			if(smsStatusTobeFetched.contains("_fetch_clicks_also_") && !smsStatusTobeFetched.contains("_fetch_opens_also_") && ! smsStatusTobeFetched.contains("special_condtion_for_all")){
				subQry += " AND dr.clicks is not null AND dr.clicks !=0  ";

			}
			else if(smsStatusTobeFetched.contains("_fetch_opens_also_") && !smsStatusTobeFetched.contains("_fetch_clicks_also_") && ! smsStatusTobeFetched.contains("special_condtion_for_all")){
				subQry += " AND dr.opens is not null AND dr.opens !=0  ";
			}
			else if(smsStatusTobeFetched.contains("_fetch_opens_also_") && smsStatusTobeFetched.contains("_fetch_clicks_also_") && ! smsStatusTobeFetched.contains("special_condtion_for_all")){
				subQry += " AND ((dr.opens is not null AND dr.opens !=0)   AND (dr.clicks is not null AND dr.clicks !=0))   ";
			}


			if(! smsStatusTobeFetched.contains("special_condtion_for_all") ){
				subQry += "  AND dr.status IN("+smsStatusTobeFetched+")  ";
			}

			if(((smsStatusTobeFetched.contains("dropped") || smsStatusTobeFetched.contains("bounce")) && ! smsStatusTobeFetched.contains("special_condtion_for_all"))){ 

				if( !smsStatusTobeFetched.contains("Delivered") && !smsStatusTobeFetched.contains("spamreport")){
					subQry += "  AND (dr.status='bounce' OR dr.status='bounced' OR dr.status='dropped')  ";
				}else {
					subQry += "  AND (dr.status='bounce' OR dr.status='bounced' OR dr.status='dropped')   AND  (dr.status='spamreport' OR dr.status='Success' OR dr.status='delivered')   ";
				}

			}
			/*if(((smsStatusTobeFetched.contains("dropped") || smsStatusTobeFetched.contains("bounce")) && ! smsStatusTobeFetched.contains("special_condtion_for_all"))){ 









				if( !smsStatusTobeFetched.contains("Success") && !smsStatusTobeFetched.contains("spamreport")){
					subQry += "  AND (dr.status='bounce' OR dr.status='dropped')  ";
				}else {
					subQry += "  AND (dr.status='bounce' OR dr.status='dropped')   AND  (dr.status='spamreport' OR dr.status='Success')   ";
				}

			}*/









			String query;
			if(start != -1){
				query  = subQry +" ORDER BY "+orderby_colName+" "+desc_Asc+"  LIMIT "+" "+start+","+end ;
			}else{
				query  = subQry +" ORDER BY "+orderby_colName+" "+desc_Asc ;
			}



			logger.info("query >>> "+query);

			list = jdbcTemplate.query(query, new RowMapper() {

				public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
					DRSMSSent drSmsSent= new DRSMSSent();
					drSmsSent.setId(rs.getLong("id"));
					Calendar cal = Calendar.getInstance();
					cal.setTime(rs.getTimestamp("sent_date"));

					drSmsSent.setSentDate(cal);
					drSmsSent.setMobile(rs.getString("mobile"));


					drSmsSent.setStatus( rs.getString("status"));
					drSmsSent.setStoreNumber(rs.getString("store_number"));
					drSmsSent.setSentCount(rs.getInt("sent_count"));

					drSmsSent.setOpens(Integer.parseInt(rs.getString("opens")));
					drSmsSent.setClicks(Integer.parseInt(rs.getString("clicks")));
					drSmsSent.setDocSid(rs.getString("doc_sid"));
					drSmsSent.setSbsNumber(rs.getString("SBS_no"));
					//drSmsSent.setHtmlStr(rs.getString("html_content"));

					/* public Object mapRow(ResultSet rs, int rowNum) throws SQLException {

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
					 */

					return drSmsSent;
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

	public List< DRSMSSent> findPenidngReports(Long userId,int start,int end,String fromDate,String toDate,String key, String store,String smsStatusTobeFetched,String orderby_colName,String desc_Asc) {

		try {
			List<DRSMSSent > list = null;

			String subQry="SELECT dr.mobile,dr.sent_date FROM dr_sms_sent dr where dr.user_id= "+userId.longValue()+
					" and dr.sent_date  >= '"+fromDate+"' and  dr.sent_date <= '"+toDate+"'";

			if(key != null){

				subQry += " AND dr.mobile like '%"+key+"%'";
			}if( store != null){
				subQry += " AND dr.store_number = '"+store+"'";
			}


			//if(((emailSatusTobeFetched.contains("submitted")))){ 

			subQry += "  AND (dr.status='Submitted')";
			//}

			/*String query;
			if(start != -1){
				query  = subQry +" ORDER BY "+orderby_colName+" "+desc_Asc+"  LIMIT "+" "+start+","+end ;
			}else{
				query  = subQry +" ORDER BY "+orderby_colName+" "+desc_Asc ;
			}*/

			String query=subQry;

			logger.info("query >>> "+query);

			list = jdbcTemplate.query(query, new RowMapper() {

				public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
					DRSMSSent drSmsSent= new DRSMSSent();
					drSmsSent.setMobile(rs.getString("mobile"));
					Calendar cal = Calendar.getInstance();
					cal.setTime(rs.getTimestamp("sent_date"));

					drSmsSent.setSentDate(cal);

					return drSmsSent;
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
		String qry="SELECT COUNT(id) FROM DRSMSSent WHERE userId ="+userId.longValue()+"  AND sentDate >= '"+fromDate+"' AND sentDate <='"+toDate+"'";
		if(store !=null && !store.trim().isEmpty()){
			qry += " AND storeNumber = '"+store+"'";
		}
		Long count =   ((Long)getHibernateTemplate().find(qry).get(0)).longValue();

		return count;


	}

	public  long findTotDRSMSSentCount(Long userId){
		String qry="SELECT COUNT(id) FROM DRSMSSent WHERE userId ="+userId.longValue()+" ";

		Long count =   ((Long)getHibernateTemplate().find(qry).get(0)).longValue();

		return count;

	}

	public long findOpenCount(Long userId,String fromDate,String toDate, String store){
		String qry="SELECT COUNT(id) FROM DRSMSSent WHERE userId ="+userId.longValue()+"  AND sentDate >= '"+fromDate+"' AND sentDate <='"+toDate+"'" +" AND opens > 0";
		if(store !=null && !store.trim().isEmpty()){
			qry += " AND storeNumber = '"+store+"'";
		}
		Long count =   ((Long)getHibernateTemplate().find(qry).get(0)).longValue();
		return count;


	}//findOpenCount

	public long findClickCount(Long userId,String fromDate,String toDate, String store){
		String qry="SELECT  COUNT(id) FROM DRSMSSent WHERE userId ="+userId.longValue()+"  AND sentDate >= '"+fromDate+"' AND sentDate <='"+toDate+"'"+" AND clicks > 0";
		if(store !=null && !store.trim().isEmpty()){
			qry += " AND storeNumber = '"+store+"'";
		}
		Long count =   ((Long)getHibernateTemplate().find(qry).get(0)).longValue();

		return count;


	}//findOpenCount



	public long findTotBounceCount(Long userId,String fromDate,String toDate, String store){
		String qry="SELECT COUNT(id) FROM DRSMSSent WHERE userId ="+userId.longValue()+""+
				"   AND  sentDate >= '"+fromDate+"' AND sentDate <='"+toDate+"'  AND  " +
				" status in('" +Constants.DR_STATUS_BOUNCED+"','"+Constants.DR_STATUS_DROPPED+"','" +Constants.DR_STATUS_BOUNCE+"') ";
		if(store !=null && !store.trim().isEmpty()){
			qry += " AND storeNumber = '"+store+"'";
		}	

		Long count =   ((Long)getHibernateTemplate().find(qry).get(0)).longValue();

		return count;


	}

	public long findTotNotSentCount(Long userId,String fromDate,String toDate, String store){
		String qry="SELECT COUNT(id) FROM DRSMSSent WHERE userId ="+userId.longValue()+""+
				"   AND  sentDate >= '"+fromDate+"' AND sentDate <='"+toDate+"'  AND  " +
				" status ='"+Constants.DR_STATUS_SUBMITTED+"' ";
		if(store !=null && !store.trim().isEmpty()){
			qry += " AND storeNumber = '"+store+"'";
		}	

		Long count =   ((Long)getHibernateTemplate().find(qry).get(0)).longValue();

		return count;


	}

	public long findTotSpammedCount(Long userId,String fromDate,String toDate, String store){
		String qry="SELECT COUNT(id) FROM DRSMSSent WHERE userId ="+userId.longValue()+""+
				"  AND sentDate >= '"+fromDate+"' AND sentDate <='"+toDate+"'  AND   " +
				" status in ('" +Constants.DR_STATUS_SPAMMED+"' ,'" +Constants.DR_STATUS_SPAME+"')" ;
		if(store !=null && !store.trim().isEmpty()){
			qry += " AND storeNumber = '"+store+"'";
		}	

		Long count =   ((Long)getHibernateTemplate().find(qry).get(0)).longValue();

		return count;


	}

	public DRSMSSent findById(long id){
		DRSMSSent deSent = null;
		try {
			List list = getHibernateTemplate().find(
					"from DRSMSSent where id = " + id );
			if (list.size() > 0) {
				deSent = (DRSMSSent) list.get(0);
			}
		} catch (Exception e) {
			logger.error(" ** Exception : ", e ); 
		}
		return deSent;

	}


	public DRSMSSent findDrById(String mobile, Long userId){
		DRSMSSent deSent = null;
		try {
			List list = getHibernateTemplate().find(
					"FROM DRSMSSent WHERE userId="+userId.longValue()+" AND mobile = '" + mobile +"' order by sentDate desc");
			if (list.size() > 0) {
				deSent = (DRSMSSent) list.get(0);
			}
		} catch (Exception e) {
			logger.error(" ** Exception : ", e ); 
		}
		return deSent;

	}
	/**
	 * update open count whenever user openDR
	 * @param sentId
	 * @return
	 */
	/*public int updateOpenCount(Long sentId) {
		try {
			String qry=" UPDATE DRSMSSent SET opens = (opens+1) WHERE id = "+ sentId.longValue();

			return executeUpdate(qry);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
			return 0;
		}

	}//updateOpenCount
	 */
	/**
	 * update click count whenever user clicks on link in DR
	 * @param sentId
	 * @return
	 */
	/*public int updateClickCount(Long sentId) {
		try {
			String qry=" UPDATE DRSMSSent SET clicks = (clicks+1) WHERE id = "+ sentId.longValue();

			return executeUpdate(qry);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
			return 0;
		}

	}//updateClickCount()
	 */
	/**
	 * to calculate sent rate for DR reports between given dates
	 * @param userId
	 * @param fromDate
	 * @param toDate
	 * @param type
	 * @return
	 */
	public List<Map<String,Object>> findTotSentRate(Long userId,String fromDate,String toDate, String type, String store, int tzOffSet){
		String qry="";
		if(type.equals("Days")){

			qry="SELECT COUNT(id) AS count, DAY(DATE_ADD(sent_date, INTERVAL "+tzOffSet+" MINUTE)) AS day FROM dr_sms_sent WHERE user_id ="+userId.longValue()+" " +" AND sent_date >= '"+fromDate+"' AND sent_date <='"+toDate+"'   " ;
			if(store !=null && !store.trim().isEmpty()){
				qry += " AND store_number = '"+store+"'";
			}
			qry += " GROUP BY  DAY(DATE_ADD(sent_date, INTERVAL "+tzOffSet+" MINUTE)) ORDER BY 2";
		}else{
			qry="SELECT COUNT(id) AS count, MONTH(DATE_ADD(sent_date, INTERVAL "+tzOffSet+" MINUTE)) AS month FROM dr_sms_sent WHERE user_id ="+userId.longValue()+" " +" AND sent_date >= '"+fromDate+"' AND sent_date <='"+toDate+"'   " ;
			if(store !=null && !store.trim().isEmpty()){
				qry += " AND store_number = '"+store+"'";
			}
			qry += " GROUP BY  MONTH(DATE_ADD(sent_date, INTERVAL "+tzOffSet+" MINUTE)) ORDER BY 2";

		}
		return	jdbcTemplate.queryForList(qry);


	}//findTotSentRate()
	/**
	 * to calculate Delivered rate for DR reports between given dates
	 * @param userId
	 * @param fromDate
	 * @param toDate
	 * @param type
	 * @return
	 */

	public List<Map<String,Object>> findTotDelRate(Long userId,String fromDate,String toDate, String type, String store, int tzOffSet ){
		String qry="";
		if(type.equals("Days")){

			qry="SELECT COUNT(id) AS count, DAY(DATE_ADD(sent_date, INTERVAL "+tzOffSet+" MINUTE)) AS day FROM dr_sms_sent WHERE user_id ="+userId.longValue()+" " +" AND sent_date >= '"+fromDate+"' AND sent_date <='"+toDate+"' AND status='"+Constants.SMS_STATUS_DELIVERED+"'";
			if(store !=null && !store.trim().isEmpty()){
				qry += " AND store_number = '"+store+"'";
			}
			qry += "   GROUP BY  DAY(DATE_ADD(sent_date, INTERVAL "+tzOffSet+" MINUTE)) ORDER BY 2";
		}else{
			qry="SELECT  COUNT(id) AS count, MONTH(DATE_ADD(sent_date, INTERVAL "+tzOffSet+" MINUTE)) AS month FROM dr_sms_sent WHERE user_id ="+userId.longValue()+" " +" AND sent_date >= '"+fromDate+"' AND sent_date <='"+toDate+"' AND  status='"+Constants.SMS_STATUS_DELIVERED+"'";
			if(store !=null && !store.trim().isEmpty()){
				qry += " AND store_number = '"+store+"'";
			}
			qry += "   GROUP BY  MONTH(DATE_ADD(sent_date, INTERVAL "+tzOffSet+" MINUTE)) ORDER BY 2";

		}
		return  getJdbcTemplate().queryForList(qry);



	}//findTotDelRate
	/***
	 * to calculate unique opens  rate for DR reports between given dates
	 * @param userId
	 * @param userId
	 * @param fromDate
	 * @param toDate
	 * @param type
	 * @return
	 */

	public List<Map<String, Object>> findTotOpenRate(Long userId,String fromDate,String toDate, String type, String store, int tzOffSet){
		String qry="";
		if(type.equals("Days")){

			qry="SELECT COUNT(id) AS count, DAY(DATE_ADD(sent_date, INTERVAL "+tzOffSet+" MINUTE)) AS day FROM dr_sms_sent WHERE user_id ="+userId.longValue()+" " +" AND sent_date >= '"+fromDate+"' AND sent_date <='"+toDate+"' AND opens > 0";
			if(store !=null && !store.trim().isEmpty()){
				qry += " AND store_number = '"+store+"'";
			}
			qry += "     GROUP BY  DAY(DATE_ADD(sent_date, INTERVAL "+tzOffSet+" MINUTE)) ORDER BY 2";
		}else{
			qry="SELECT COUNT(id) AS count, MONTH(DATE_ADD(sent_date, INTERVAL "+tzOffSet+" MINUTE)) AS month FROM dr_sms_sent WHERE user_id ="+userId.longValue()+" " +" AND sent_date >= '"+fromDate+"' AND sent_date <='"+toDate+"'  AND opens > 0" ;
			if(store !=null && !store.trim().isEmpty()){
				qry += " AND store_number = '"+store+"'";
			}
			qry += "    GROUP BY  MONTH(DATE_ADD(sent_date, INTERVAL "+tzOffSet+" MINUTE)) ORDER BY 2";

		}
		return  getJdbcTemplate().queryForList(qry);



	}//findTotOpenRate()
	/**
	 * to calculate unique clicks  rate for DR reports between given dates
	 * @param userId
	 * @param fromDate
	 * @param toDate
	 * @param type
	 * @return
	 */

	public List<Map<String, Object>> findTotClickRate(Long userId,String fromDate,String toDate, String type, String store, int tzOffSet){
		String qry="";
		if(type.equals("Days")){

			qry="SELECT COUNT(id) AS count, DAY(DATE_ADD(sent_date, INTERVAL "+tzOffSet+" MINUTE)) AS day FROM dr_sms_sent WHERE user_id ="+userId.longValue()+" " +" AND sent_date >= '"+fromDate+"' AND sent_date <='"+toDate+"'  AND clicks > 0";
			if(store !=null && !store.trim().isEmpty()){
				qry += " AND store_number = '"+store+"'";
			}
			qry += "    GROUP BY  DAY(DATE_ADD(sent_date, INTERVAL "+tzOffSet+" MINUTE)) ORDER BY 2";
		}else{
			qry="SELECT COUNT(id) AS count, MONTH(DATE_ADD(sent_date, INTERVAL "+tzOffSet+" MINUTE)) AS month FROM dr_sms_sent WHERE user_id ="+userId.longValue()+" " +" AND sent_date >= '"+fromDate+"' AND sent_date <='"+toDate+"'  AND clicks > 0" ;
			if(store !=null && !store.trim().isEmpty()){
				qry += " AND store_number = '"+store+"'";
			}
			qry += "   GROUP BY  MONTH(DATE_ADD(sent_date, INTERVAL "+tzOffSet+" MINUTE)) ORDER BY 2";

		}
		return  getJdbcTemplate().queryForList(qry);



	}//findTotClickRate
	/**
	 * to calculate bounce  rate for DR reports between given dates
	 * @param userId
	 * @param fromDate
	 * @param toDate
	 * @param type
	 * @return
	 */

	public List<Map<String,Object>> findTotBounceRate(Long userId,String fromDate,String toDate, String type, String store, int tzOffSet){
		String qry="";
		if(type.equals("Days")){

			qry="SELECT COUNT(id) AS count, DAY(DATE_ADD(sent_date, INTERVAL "+tzOffSet+" MINUTE)) AS day FROM dr_sms_sent WHERE user_id ="+userId.longValue()+" " +" AND sent_date >= '"+fromDate+"' AND sent_date <='"+toDate+"' AND status in('"+Constants.DR_STATUS_BOUNCED+"','"+Constants.DR_STATUS_DROPPED+"','" +Constants.DR_STATUS_BOUNCE+"')";
			if(store !=null && !store.trim().isEmpty()){
				qry += " AND store_number = '"+store+"'";
			}
			qry += "   GROUP BY  DAY(DATE_ADD(sent_date, INTERVAL "+tzOffSet+" MINUTE)) ORDER BY 2";
		}else{
			qry="SELECT COUNT(id) AS count, MONTH(DATE_ADD(sent_date, INTERVAL "+tzOffSet+" MINUTE)) AS month FROM dr_sms_sent WHERE user_id ="+userId.longValue()+" " +" AND sent_date >= '"+fromDate+"' AND sent_date <='"+toDate+"' AND  status in('"+Constants.DR_STATUS_BOUNCED+"','"+Constants.DR_STATUS_DROPPED+"','" +Constants.DR_STATUS_BOUNCE+"')" ;
			if(store !=null && !store.trim().isEmpty()){
				qry += " AND store_number = '"+store+"'";
			}
			qry += "   GROUP BY  MONTH(DATE_ADD(sent_date, INTERVAL "+tzOffSet+" MINUTE)) ORDER BY 2";

		}
		return  getJdbcTemplate().queryForList(qry);



	}//findTotClickRate()

	public List<Map<String,Object>> findTotStatusPendingRate(Long userId,String fromDate,String toDate, String type, String store, int tzOffSet){
		String qry="";
		if(type.equals("Days")){

			qry="SELECT COUNT(id) AS count, DAY(DATE_ADD(sent_date, INTERVAL "+tzOffSet+" MINUTE)) AS day FROM dr_sms_sent WHERE user_id ="+userId.longValue()+" " +" AND sent_date >= '"+fromDate+"' AND sent_date <='"+toDate+"' AND status in('"+Constants.DR_STATUS_SUBMITTED+"')";
			if(store !=null && !store.trim().isEmpty()){
				qry += " AND store_number = '"+store+"'";
			}
			qry += "   GROUP BY  DAY(DATE_ADD(sent_date, INTERVAL "+tzOffSet+" MINUTE)) ORDER BY 2";
		}else{
			qry="SELECT COUNT(id) AS count, MONTH(DATE_ADD(sent_date, INTERVAL "+tzOffSet+" MINUTE)) AS month FROM dr_sms_sent WHERE user_id ="+userId.longValue()+" " +" AND sent_date >= '"+fromDate+"' AND sent_date <='"+toDate+"' AND  status in('"+Constants.DR_STATUS_SUBMITTED+"')" ;
			if(store !=null && !store.trim().isEmpty()){
				qry += " AND store_number = '"+store+"'";
			}
			qry += "   GROUP BY  MONTH(DATE_ADD(sent_date, INTERVAL "+tzOffSet+" MINUTE)) ORDER BY 2";

		}
		return  getJdbcTemplate().queryForList(qry);

	}

	//all view 
	public List <DRSMSSent> findAllDrView(String mobile,long userId){
		try {
			return getHibernateTemplate().find(
					"FROM DRSMSSent WHERE userId="+userId+" AND mobile = '" + mobile + "'"+"order by sentDate desc");
		} catch (Exception e) {
			logger.error(" ** Exception : ", e ); 
			return null;
		}

	}

	public int findConfiguredTemplateCount(Long userId, String templateName) {

		try {
			templateName = StringEscapeUtils.escapeSql(templateName);
			String qry = "SELECT COUNT(id) FROM DRSMSSent WHERE userId ="+userId.longValue()+" " +" AND templateName='MY_TEMPLATE:"+templateName+"'"; 

			int count =   ((Long)getHibernateTemplate().find(qry).get(0)).intValue();

			return count;
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
			return 0;
		}



	}


	public long findTotDelCount(Long userId,String fromDate,String toDate, String store){
		String qry="SELECT COUNT(id) FROM DRSMSSent WHERE userId ="+userId.longValue()+""+
				"   AND sentDate >= '"+fromDate+"' AND sentDate <='"+toDate+"' AND  status ='" +Constants.SMS_STATUS_DELIVERED+"' ";
		if(store !=null && !store.trim().isEmpty()){
			qry += " AND storeNumber = '"+store+"'";
		}		

		Long count =   ((Long)getHibernateTemplate().find(qry).get(0)).longValue();

		return count;


	}
	// Added after DR Resend

	public DRSMSSent findBy( Long userId , String docSid, String mobile ) throws Exception {

		List<DRSMSSent> drsentObjList = null;

		String qryStr = "FROM DRSMSSent WHERE userId ="+userId.longValue()+""
				+ " AND docSid= '"+docSid+"' and mobile='"+mobile+"' ORDER BY sentDate DESC" ;
		drsentObjList = getHibernateTemplate().find(qryStr);
		if(drsentObjList != null &&  drsentObjList.size() > 0){
			return drsentObjList.get(0);
		}else{
			return null;
		}


	}

	public DRSMSSent findBy( Long userId , String docSid) throws Exception {

		List<DRSMSSent> drsentObjList = null;

		String qryStr = "FROM DRSMSSent WHERE userId ="+userId.longValue()+""
				+ " AND docSid= '"+docSid+"' ORDER BY sentDate DESC" ;
		drsentObjList = getHibernateTemplate().find(qryStr);
		if(drsentObjList != null &&  drsentObjList.size() > 0){
			return drsentObjList.get(0);
		}else{
			return null;
		}


	}

	/*public synchronized void updateSentCount(Long existingObjId) throws BaseDAOException{

		String qry = "UPDATE dr_sms_sent set sent_count=(sent_count+1) WHERE id="+existingObjId.longValue();

		executeJdbcUpdateQuery(qry);

	}*/



	/* public void setNewTemplateName(long userId, String newTemplateName, String selectedTemplate) throws Exception {

		 String newTempName="MY_TEMPLATE:"+newTemplateName;
		 newTempName = StringEscapeUtils.escapeSql(newTempName);
		  selectedTemplate = StringEscapeUtils.escapeSql(selectedTemplate);


				 String query = "UPDATE DRSMSSent SET templateName='"+newTempName+"' WHERE templateName='"+selectedTemplate+"' AND userId="+userId;
				 logger.info("qry in de sent::::"+query);
				//
			int count= executeUpdate(query);
				 //getHibernateTemplate().bulkUpdate(query);

			 }//setNewTemplateName()
	 */

	public long getCountBy(Long userId,String fromDate,String toDate){
		try {
			String qry="SELECT COUNT(id) FROM DRSMSSent WHERE userId ="+userId.longValue()+""+
					"  AND sentDate >= '"+fromDate+"' AND sentDate <='"+toDate+"'" ;


			Long count =   ((Long)getHibernateTemplate().find(qry).get(0)).longValue();

			return count;
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			logger.error("Exception occurred while fetching the count ", e);
		}
		return 0;

	}

	public List<DRSMSSent> find(Long userId,String fromDate,String toDate){
		List<DRSMSSent> retList = null;
		try {

			String qry=" FROM DRSMSSent WHERE userId ="+userId.longValue()+""+
					"  AND sentDate >= '"+fromDate+"' AND sentDate <='"+toDate+"'" ;


			retList =   executeQuery(qry);

			return retList;
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			logger.error("Exception occurred while fetching the count ", e);
		}
		return retList;

	}

	
	
	
	/**
	 * Zone and Store based	
	 */

	public int findDRBasedOnDatesForZoneandStore(Long userId, String fromDate,String toDate, String key, String store, String emailSatusTobeFetched,Long zoneId){
		if(zoneId ==null && store == null){
			return findDRBasedOnDatesZone(userId, fromDate,toDate,key,emailSatusTobeFetched);
		}else{

			String qry = null;
			if(zoneId !=null && store!=null){
				qry = "SELECT  COUNT(id) FROM  DRSMSSent  WHERE userId ="+userId.longValue()+" " +
						" AND sentDate >= '"+fromDate+"' AND sentDate <='"+toDate+"' AND zone_id = "+zoneId+" AND storeNumber like '"+store+"'";

			}else if(zoneId !=null){
				qry = "SELECT  COUNT(id) FROM  DRSMSSent  WHERE userId ="+userId.longValue()+" " +
						" AND sentDate >= '"+fromDate+"' AND sentDate <='"+toDate+"' AND zone_id = "+zoneId;

			}else{
				qry = "SELECT  COUNT(id) FROM  DRSMSSent  WHERE userId ="+userId.longValue()+" " +
						" AND sentDate >= '"+fromDate+"' AND sentDate <='"+toDate+"' AND storeNumber like '"+store+"'";
			}
			if(key != null){
				qry += " AND mobile like '%"+key+"%'";
			}

			if(! emailSatusTobeFetched.contains("special_condtion_for_all") ){
				qry += "  AND status IN("+emailSatusTobeFetched+")  ";
			}
			/*	
		String	 qry ="SELECT  COUNT(Distinct emailId) FROM  DRSMSSent  WHERE userId ="+userId.longValue()+" " +
					" AND sentDate >= '"+fromDate+"' AND sentDate <='"+toDate+"'";
			 */
			int count =   ((Long)getHibernateTemplate().find(qry).get(0)).intValue();

			return count;
		}

	}


	public int findDRBasedOnDatesZone(Long userId, String fromDate,String toDate,String key, String emailSatusTobeFetched) {
		String qry = "SELECT  COUNT(id) FROM  DRSMSSent  WHERE userId ="+userId.longValue()+" " +
				" AND sentDate >= '"+fromDate+"' AND sentDate <='"+toDate+"'";

		if(key != null){
			qry += " AND mobile like '%"+key+"%'";
		}

		if(! emailSatusTobeFetched.contains("special_condtion_for_all") ){
			qry += "  AND status IN("+emailSatusTobeFetched+")  ";
		}

		/*	
		 * 
		 * 
	String	 qry ="SELECT  COUNT(Distinct emailId) FROM  DRSMSSent  WHERE userId ="+userId.longValue()+" " +
				" AND sentDate >= '"+fromDate+"' AND sentDate <='"+toDate+"'";
		 */
		int count =   ((Long)getHibernateTemplate().find(qry).get(0)).intValue();

		return count;
	}

//APP-3360
	public List< DRSMSSent> findDrReports(Long userId,int start,int end,String fromDate,String toDate,String key, String store,String emailSatusTobeFetched,String orderby_colName,String desc_Asc,Long zoneId) {

		try {
			List<DRSMSSent > list = null;
			/*String subQry="SELECT dr.email_id,MAX(dr.sent_date) as lastSent, SUM(IF(dr.opens >0, 1,0)) as uniopens,SUM(IF(dr.clicks >0, 1,0)) as uniclicks," +
					"(SELECT status from dr_sms_sent  where user_id= "+userId.longValue()+ " and sent_date  >='"+fromDate+"' and  sent_date <= '"+toDate+"' " +
							"AND email_id=dr.email_id order by sent_date desc limit 1 )as status," +
							"Count(dr.id) as totalSentCount,dr.id FROM dr_sms_sent dr where   dr.user_id= "+userId.longValue()+
					" and dr.sent_date  >= '"+fromDate+"' and  dr.sent_date <= '"+toDate+"'";*/
			/*String query  = " SELECT emailId,MAX(sentDate), SUM(IF(opens >0, 1,0)) as o,SUM(IF(clicks >0, 1,0)) as c,status,Count(id),id FROM DRSMSSent WHERE userId ="+userId.longValue()+" " +
					" AND sentDate >= '"+fromDate+"' AND sentDate <='"+toDate+"'  group by emailId ";*/

			/*String query  = " SELECT emailId,MAX(sentDate), SUM(IF(opens >0, 1,0)),SUM(IF(clicks >0, 1,0)),status,Count(id),id FROM DRSMSSent WHERE userId ="+userId.longValue()+" " +
							" AND sentDate >= '"+fromDate+"' AND sentDate <='"+toDate+"'  group by emailId ";*/
			String subQry="SELECT dr.id, dr.sent_date, dr.mobile, dr.status, dr.store_number, dr.SBS_no,"
					+ " dr.opens, dr.clicks, dr.sent_count, dr.doc_sid "+//,dr.html_content " +
					" FROM dr_sms_sent dr where   dr.user_id= "+userId.longValue()+
					" and dr.sent_date  >= '"+fromDate+"' and  dr.sent_date <= '"+toDate+"'";

			if(key != null){

				subQry += " AND dr.email_id like '%"+key+"%'";
			}if( store != null){
				subQry += " AND dr.store_number = '"+store+"'";
			}

			if( zoneId != null){
				subQry += " AND dr.zone_Id = "+zoneId;
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
				query  = subQry +" ORDER BY "+orderby_colName+" "+desc_Asc+"  LIMIT "+" "+start+","+end ;
			}else{
				query  = subQry +" ORDER BY "+orderby_colName+" "+desc_Asc ;
			}



			logger.info("query >>> "+query);

			list = jdbcTemplate.query(query, new RowMapper() {

				public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
					DRSMSSent drSmsSent= new DRSMSSent();
					drSmsSent.setId(rs.getLong("id"));
					Calendar cal = Calendar.getInstance();
					cal.setTime(rs.getTimestamp("sent_date"));

					drSmsSent.setSentDate(cal);
					drSmsSent.setMobile(rs.getString("mobile"));


					drSmsSent.setStatus( rs.getString("status"));
					drSmsSent.setStoreNumber(rs.getString("store_number"));
					//APP-3360
					drSmsSent.setSentCount(rs.getInt("sent_count"));


					//drSmsSent.setClicks(Integer.parseInt(rs.getString("clicks")));
					drSmsSent.setOpens(Integer.parseInt(rs.getString("opens")));
					drSmsSent.setClicks(Integer.parseInt(rs.getString("clicks")));
					drSmsSent.setDocSid(rs.getString("doc_sid"));
					drSmsSent.setSbsNumber(rs.getString("SBS_no"));
					//drSmsSent.setHtmlStr(rs.getString("html_content"));

					/* public Object mapRow(ResultSet rs, int rowNum) throws SQLException {

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
					 */

					return drSmsSent;
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


	public List< DRSMSSent> findPenidngReports(Long userId,int start,int end,String fromDate,String toDate,String key, String store,String emailSatusTobeFetched,String orderby_colName,String desc_Asc,Long zoneId) {
		try {
			List<DRSMSSent > list = null;

			String subQry="SELECT dr.mobile,dr.sent_date FROM dr_sms_sent dr where dr.user_id= "+userId.longValue()+
					" and dr.sent_date  >= '"+fromDate+"' and  dr.sent_date <= '"+toDate+"'";

			if(key != null){

				subQry += " AND dr.mobile like '%"+key+"%'";
			}if( store != null){
				subQry += " AND dr.store_number = '"+store+"'";
			}
			if( zoneId != null){
				subQry += " AND dr.zone_Id = "+zoneId;
			}

			//if(((emailSatusTobeFetched.contains("submitted")))){ 

			subQry += "  AND (dr.status='Submitted')";
			//}

			/*String query;
			if(start != -1){
				query  = subQry +" ORDER BY "+orderby_colName+" "+desc_Asc+"  LIMIT "+" "+start+","+end ;
			}else{
				query  = subQry +" ORDER BY "+orderby_colName+" "+desc_Asc ;
			}*/

			String query=subQry;

			logger.info("query >>> "+query);

			list = jdbcTemplate.query(query, new RowMapper() {

				public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
					DRSMSSent drSmsSent= new DRSMSSent();
					drSmsSent.setMobile(rs.getString("mobile"));
					Calendar cal = Calendar.getInstance();
					cal.setTime(rs.getTimestamp("sent_date"));

					drSmsSent.setSentDate(cal);

					return drSmsSent;
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

	public long findTotSentCount(Long userId,String fromDate,String toDate, String store, Long zoneId){
		String qry="SELECT COUNT(id) FROM DRSMSSent WHERE userId ="+userId.longValue()+"  AND sentDate >= '"+fromDate+"' AND sentDate <='"+toDate+"'";
		if(store !=null && !store.trim().isEmpty()){
			qry += " AND storeNumber = '"+store+"'";
		}
		if(zoneId !=null ){
			qry += " AND zoneId = "+zoneId;
		}
		Long count =   ((Long)getHibernateTemplate().find(qry).get(0)).longValue();

		return count;
	}


	public long findTotNotSentCount(Long userId,String fromDate,String toDate, String store, Long zoneId){
		String qry="SELECT COUNT(id) FROM DRSMSSent WHERE userId ="+userId.longValue()+""+
				"   AND  sentDate >= '"+fromDate+"' AND sentDate <='"+toDate+"'  AND  " +
				" status ='"+Constants.DR_STATUS_SUBMITTED+"' ";
		if(store !=null && !store.trim().isEmpty()){
			qry += " AND storeNumber = '"+store+"'";
		}	
		if(zoneId !=null){
			qry += " AND zoneId = "+zoneId;
		}
		Long count =   ((Long)getHibernateTemplate().find(qry).get(0)).longValue();

		return count;


	}


	public long findTotBounceCount(Long userId,String fromDate,String toDate, String store, Long zoneId){
		String qry="SELECT COUNT(id) FROM DRSMSSent WHERE userId ="+userId.longValue()+""+
				"   AND  sentDate >= '"+fromDate+"' AND sentDate <='"+toDate+"'  AND  " +
				" status in('" +Constants.DR_STATUS_BOUNCED+"','"+Constants.DR_STATUS_DROPPED+"','" +Constants.DR_STATUS_BOUNCE+"') ";
		if(store !=null && !store.trim().isEmpty()){
			qry += " AND storeNumber = '"+store+"'";
		}	

		if(zoneId !=null){
			qry += " AND zoneId = "+zoneId;
		}
		Long count =   ((Long)getHibernateTemplate().find(qry).get(0)).longValue();

		return count;


	}


	public long findTotDelCount(Long userId,String fromDate,String toDate, String store, Long zoneId){
		String qry="SELECT COUNT(id) FROM DRSMSSent WHERE userId ="+userId.longValue()+""+
				"   AND sentDate >= '"+fromDate+"' AND sentDate <='"+toDate+"' AND  status ='" +Constants.SMS_STATUS_DELIVERED+"' ";
		if(store !=null && !store.trim().isEmpty()){
			qry += " AND storeNumber = '"+store+"'";
		}		
		if(zoneId !=null){
			qry += " AND zoneId = "+zoneId;
		}
		Long count =   ((Long)getHibernateTemplate().find(qry).get(0)).longValue();

		return count;


	}

	public long findOpenCount(Long userId,String fromDate,String toDate, String store, Long zoneId){
		String qry="SELECT COUNT(id) FROM DRSMSSent WHERE userId ="+userId.longValue()+"  AND sentDate >= '"+fromDate+"' AND sentDate <='"+toDate+"'" +" AND opens > 0";
		if(store !=null && !store.trim().isEmpty()){
			qry += " AND storeNumber = '"+store+"'";
		}
		if(zoneId !=null){
			qry += " AND zoneId = "+zoneId;
		}
		Long count =   ((Long)getHibernateTemplate().find(qry).get(0)).longValue();
		return count;


	}//findOpenCount

	public long findClickCount(Long userId,String fromDate,String toDate, String store, Long zoneId){
		String qry="SELECT  COUNT(id) FROM DRSMSSent WHERE userId ="+userId.longValue()+"  AND sentDate >= '"+fromDate+"' AND sentDate <='"+toDate+"'"+" AND clicks > 0";
		if(store !=null && !store.trim().isEmpty()){
			qry += " AND storeNumber = '"+store+"'";
		}
		if(zoneId !=null){
			qry += " AND zoneId = "+zoneId;
		}
		Long count =   ((Long)getHibernateTemplate().find(qry).get(0)).longValue();

		return count;


	}//findOpenCount


	public long findTotSpammedCount(Long userId,String fromDate,String toDate, String store,Long zoneId){
		String qry="SELECT COUNT(id) FROM DRSMSSent WHERE userId ="+userId.longValue()+""+
				"  AND sentDate >= '"+fromDate+"' AND sentDate <='"+toDate+"'  AND   " +
				" status in ('" +Constants.DR_STATUS_SPAMMED+"' ,'" +Constants.DR_STATUS_SPAME+"')" ;
		if(store !=null && !store.trim().isEmpty()){
			qry += " AND storeNumber = '"+store+"'";
		}	
		if(zoneId !=null){
			qry += " AND zoneId = "+zoneId;
		}
		Long count =   ((Long)getHibernateTemplate().find(qry).get(0)).longValue();

		return count;


	}


	public List<Object[]> findDeliveryStatusPendingReports(Long userId,int start,int end,String fromDate,String toDate, String store, String status, Long zoneId){
		try{
			List<Object[]> list = null;
			String query = "SELECT mobile,sentDate FROM DRSMSSent WHERE userId = "+userId.longValue()+
					" AND sentDate >= '"+fromDate+"' AND sentDate <= '"+toDate+"' AND status IN('"+status+"') " ;


			if(store != null) query += " AND storeNumber = '"+store+"'";
			if(zoneId !=null){
				query += " AND zoneId = "+zoneId;
			}
			list = executeQuery(query, start, end);
			logger.info("query :"+query);
			return list;
		}catch (Exception e) {
			logger.info(" Exception while getting stores ",e);
		}
		return null;
	}

	public long findTotalStorePurchases(Long userId, String fromDate,String toDate, String store, String emailSatusTobeFetched, Long zoneId){
		List<Object[]> list = null;
		String query = "SELECT COUNT(id) FROM DRSMSSent WHERE storeNumber IS NOT NULL AND userId = "+userId.longValue()+
				" AND sentDate >= '"+fromDate+"' AND sentDate <= '"+toDate+"' " ;

		if(! emailSatusTobeFetched.contains("special_condtion_for_all") ){
			query += "  AND status IN("+emailSatusTobeFetched+")  ";
		}

		if(store != null) query += " AND storeNumber = '"+store+"'";

		if(zoneId != null) query += " AND zoneId = "+zoneId;

		query += "GROUP BY userId";

		list = executeQuery(query);
		if(list.size() >0){
			return ((Long)(executeQuery(query).get(0))).longValue();
		}
		return 0;
	}


	public int findStoreCountBasedOnDates(Long userId, String fromDate,String toDate, String store, String emailSatusTobeFetched, Long zoneId){
		String qry = "SELECT  COUNT(Distinct storeNumber) FROM  DRSMSSent  WHERE userId ="+userId.longValue()+" " +
				" AND sentDate >= '"+fromDate+"' AND sentDate <='"+toDate+"'";
		if(store != null){
			qry += " AND storeNumber = '"+store+"'";
		}

		if(zoneId != null){
			qry += " AND zoneId = "+zoneId;
		}


		if(! emailSatusTobeFetched.contains("special_condtion_for_all") ){
			qry += "  AND status IN("+emailSatusTobeFetched+")  ";
		}

		int count =   ((Long)getHibernateTemplate().find(qry).get(0)).intValue();

		return count;
	}

	public List<Object[]> findStoreReports(Long userId,int start,int end,String fromDate,String toDate, String store, String emailSatusTobeFetched, Long zoneId){
		try{
			List<Object[]> list = null;
			String query = "SELECT storeNumber, COUNT(id)  FROM DRSMSSent WHERE storeNumber IS NOT NULL AND userId = "+userId.longValue()+
					" AND sentDate >= '"+fromDate+"' AND sentDate <= '"+toDate+"' " ;


			if(! emailSatusTobeFetched.contains("special_condtion_for_all") ){
				query += "  AND status IN("+emailSatusTobeFetched+")  ";
			}
			if(store != null) query += " AND storeNumber = '"+store+"'";
			if(zoneId != null){
				query += " AND zoneId = "+zoneId;
			}

			query += " GROUP BY storeNumber ORDER BY 2 DESC";
			list = executeQuery(query, start, end);
			return list;
		}catch (Exception e) {
			logger.info(" Exception while getting stores ",e);
		}
		return null;
	}

	
	public int findConfiguredTemplateCount(Long userId, Long myTemplateId) {

		try {
			///templateName = StringEscapeUtils.escapeSql(templateName);
			String qry = "SELECT COUNT(id) FROM DRSMSSent WHERE userId ="+userId.longValue()+" " +" AND myTemplateId="+myTemplateId; 

			int count =   ((Long)getHibernateTemplate().find(qry).get(0)).intValue();

			return count;
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
			return 0;
		}
	}

	public List<Map<String,Object>> findTotStatusPendingRate(Long userId,String fromDate,String toDate, String type, String store, int tzOffSet, Long zoneId){
		String qry="";
		if(type.equals("Days")){

			qry="SELECT COUNT(id) AS count, DAY(DATE_ADD(sent_date, INTERVAL "+tzOffSet+" MINUTE)) AS day FROM dr_sms_sent WHERE user_id ="+userId.longValue()+" " +" AND sent_date >= '"+fromDate+"' AND sent_date <='"+toDate+"' AND status in('"+Constants.DR_STATUS_SUBMITTED+"')";
			if(store !=null && !store.trim().isEmpty()){
				qry += " AND store_number = '"+store+"'";
			}
			
			if(zoneId !=null){
				qry += " AND zone_Id = "+zoneId;
			}
			qry += "   GROUP BY  DAY(DATE_ADD(sent_date, INTERVAL "+tzOffSet+" MINUTE)) ORDER BY 2";
		}else{
			qry="SELECT COUNT(id) AS count, MONTH(DATE_ADD(sent_date, INTERVAL "+tzOffSet+" MINUTE)) AS month FROM dr_sms_sent WHERE user_id ="+userId.longValue()+" " +" AND sent_date >= '"+fromDate+"' AND sent_date <='"+toDate+"' AND  status in('"+Constants.DR_STATUS_SUBMITTED+"')" ;
			if(store !=null && !store.trim().isEmpty()){
				qry += " AND store_number = '"+store+"'";
			}
			if(zoneId !=null){
				qry += " AND zone_Id = "+zoneId;
			}
			qry += "   GROUP BY  MONTH(DATE_ADD(sent_date, INTERVAL "+tzOffSet+" MINUTE)) ORDER BY 2";

		}
		return  getJdbcTemplate().queryForList(qry);
	}

	
	
	/**
	 * to calculate bounce  rate for DR reports between given dates
	 * @param userId
	 * @param fromDate
	 * @param toDate
	 * @param type
	 * @param zoneId
	 * @return
	 */

	public List<Map<String,Object>> findTotBounceRate(Long userId,String fromDate,String toDate, String type, String store, int tzOffSet,Long zoneId){
		String qry="";
		if(type.equals("Days")){

			qry="SELECT COUNT(id) AS count, DAY(DATE_ADD(sent_date, INTERVAL "+tzOffSet+" MINUTE)) AS day FROM dr_sms_sent WHERE user_id ="+userId.longValue()+" " +" AND sent_date >= '"+fromDate+"' AND sent_date <='"+toDate+"' AND status in('"+Constants.DR_STATUS_BOUNCED+"','"+Constants.DR_STATUS_DROPPED+"','" +Constants.DR_STATUS_BOUNCE+"')";
			if(store !=null && !store.trim().isEmpty()){
				qry += " AND store_number = '"+store+"'";
			}
			
			if(zoneId !=null){
				qry += " AND zone_Id = "+zoneId;
			}
			
			qry += "   GROUP BY  DAY(DATE_ADD(sent_date, INTERVAL "+tzOffSet+" MINUTE)) ORDER BY 2";
		}else{
			qry="SELECT COUNT(id) AS count, MONTH(DATE_ADD(sent_date, INTERVAL "+tzOffSet+" MINUTE)) AS month FROM dr_sms_sent WHERE user_id ="+userId.longValue()+" " +" AND sent_date >= '"+fromDate+"' AND sent_date <='"+toDate+"' AND  status in('"+Constants.DR_STATUS_BOUNCED+"','"+Constants.DR_STATUS_DROPPED+"','" +Constants.DR_STATUS_BOUNCE+"')" ;
			if(store !=null && !store.trim().isEmpty()){
				qry += " AND store_number = '"+store+"'";
			}
			
			if(zoneId !=null){
				qry += " AND zone_Id = "+zoneId;
			}
			qry += "   GROUP BY  MONTH(DATE_ADD(sent_date, INTERVAL "+tzOffSet+" MINUTE)) ORDER BY 2";

		}
		return  getJdbcTemplate().queryForList(qry);



	}//findTotClickRate()
	
	
	/**
	 * to calculate unique clicks  rate for DR reports between given dates
	 * @param userId
	 * @param fromDate
	 * @param toDate
	 * @param type
	 * @return
	 */

	public List<Map<String, Object>> findTotClickRate(Long userId,String fromDate,String toDate, String type, String store, int tzOffSet, Long zoneId){
		String qry="";
		if(type.equals("Days")){

			qry="SELECT COUNT(id) AS count, DAY(DATE_ADD(sent_date, INTERVAL "+tzOffSet+" MINUTE)) AS day FROM dr_sms_sent WHERE user_id ="+userId.longValue()+" " +" AND sent_date >= '"+fromDate+"' AND sent_date <='"+toDate+"'  AND clicks > 0";
			if(store !=null && !store.trim().isEmpty()){
				qry += " AND store_number = '"+store+"'";
			}
			if(zoneId !=null){
				qry += " AND zone_Id = "+zoneId;
			}
			qry += "    GROUP BY  DAY(DATE_ADD(sent_date, INTERVAL "+tzOffSet+" MINUTE)) ORDER BY 2";
		}else{
			qry="SELECT COUNT(id) AS count, MONTH(DATE_ADD(sent_date, INTERVAL "+tzOffSet+" MINUTE)) AS month FROM dr_sms_sent WHERE user_id ="+userId.longValue()+" " +" AND sent_date >= '"+fromDate+"' AND sent_date <='"+toDate+"'  AND clicks > 0" ;
			if(store !=null && !store.trim().isEmpty()){
				qry += " AND store_number = '"+store+"'";
			}
			if(zoneId !=null){
				qry += " AND zone_Id = "+zoneId;
			}
			qry += "   GROUP BY  MONTH(DATE_ADD(sent_date, INTERVAL "+tzOffSet+" MINUTE)) ORDER BY 2";

		}
		return  getJdbcTemplate().queryForList(qry);



	}//findTotClickRate
	
	
	/***
	 * to calculate unique opens  rate for DR reports between given dates
	 * @param userId
	 * @param userId
	 * @param fromDate
	 * @param toDate
	 * @param type
	 * @return
	 */

	public List<Map<String, Object>> findTotOpenRate(Long userId,String fromDate,String toDate, String type, String store, int tzOffSet, Long zoneId){
		String qry="";
		if(type.equals("Days")){

			qry="SELECT COUNT(id) AS count, DAY(DATE_ADD(sent_date, INTERVAL "+tzOffSet+" MINUTE)) AS day FROM dr_sms_sent WHERE user_id ="+userId.longValue()+" " +" AND sent_date >= '"+fromDate+"' AND sent_date <='"+toDate+"' AND opens > 0";
			if(store !=null && !store.trim().isEmpty()){
				qry += " AND store_number = '"+store+"'";
			}
			if(zoneId !=null){
				qry += " AND zone_Id = "+zoneId;
			}
			qry += "     GROUP BY  DAY(DATE_ADD(sent_date, INTERVAL "+tzOffSet+" MINUTE)) ORDER BY 2";
		}else{
			qry="SELECT COUNT(id) AS count, MONTH(DATE_ADD(sent_date, INTERVAL "+tzOffSet+" MINUTE)) AS month FROM dr_sms_sent WHERE user_id ="+userId.longValue()+" " +" AND sent_date >= '"+fromDate+"' AND sent_date <='"+toDate+"'  AND opens > 0" ;
			if(store !=null && !store.trim().isEmpty()){
				qry += " AND store_number = '"+store+"'";
			}
			if(zoneId !=null){
				qry += " AND zone_Id = "+zoneId;
			}
			qry += "    GROUP BY  MONTH(DATE_ADD(sent_date, INTERVAL "+tzOffSet+" MINUTE)) ORDER BY 2";

		}
		return  getJdbcTemplate().queryForList(qry);



	}//findTotOpenRate()
	
	/**
	 * to calculate Delivered rate for DR reports between given dates
	 * @param userId
	 * @param fromDate
	 * @param toDate
	 * @param type
	 * @param zoneId
	 * @return
	 */

	public List<Map<String,Object>> findTotDelRate(Long userId,String fromDate,String toDate, String type, String store, int tzOffSet,Long zoneId ){
		String qry="";
		if(type.equals("Days")){

			qry="SELECT COUNT(id) AS count, DAY(DATE_ADD(sent_date, INTERVAL "+tzOffSet+" MINUTE)) AS day FROM dr_sms_sent WHERE user_id ="+userId.longValue()+" " +" AND sent_date >= '"+fromDate+"' AND sent_date <='"+toDate+"' AND status='"+Constants.SMS_STATUS_DELIVERED+"'";
			if(store !=null && !store.trim().isEmpty()){
				qry += " AND store_number = '"+store+"'";
			}
			if(zoneId !=null){
				qry += " AND zone_Id = "+zoneId;
			}
			qry += "   GROUP BY  DAY(DATE_ADD(sent_date, INTERVAL "+tzOffSet+" MINUTE)) ORDER BY 2";
		}else{
			qry="SELECT  COUNT(id) AS count, MONTH(DATE_ADD(sent_date, INTERVAL "+tzOffSet+" MINUTE)) AS month FROM dr_sms_sent WHERE user_id ="+userId.longValue()+" " +" AND sent_date >= '"+fromDate+"' AND sent_date <='"+toDate+"' AND  status='"+Constants.SMS_STATUS_DELIVERED+"'";
			if(store !=null && !store.trim().isEmpty()){
				qry += " AND store_number = '"+store+"'";
			}
			if(zoneId !=null ){
				qry += " AND zone_Id = "+zoneId;
			}
			qry += "   GROUP BY  MONTH(DATE_ADD(sent_date, INTERVAL "+tzOffSet+" MINUTE)) ORDER BY 2";
		}
		return  getJdbcTemplate().queryForList(qry);
	}//findTotDelRate
	
	/**
	 * to calculate sent rate for DR reports between given dates
	 * @param userId
	 * @param fromDate
	 * @param toDate
	 * @param type
	 * @return
	 */
	public List<Map<String,Object>> findTotSentRate(Long userId,String fromDate,String toDate, String type, String store, int tzOffSet, Long zoneId){
		String qry="";
		if(type.equals("Days")){

			qry="SELECT COUNT(id) AS count, DAY(DATE_ADD(sent_date, INTERVAL "+tzOffSet+" MINUTE)) AS day FROM dr_sms_sent WHERE user_id ="+userId.longValue()+" " +" AND sent_date >= '"+fromDate+"' AND sent_date <='"+toDate+"'   " ;
			if(store !=null && !store.trim().isEmpty()){
				qry += " AND store_number = '"+store+"'";
			}
			if(zoneId !=null){
				qry += " AND zone_Id = "+zoneId;
			}
			qry += " GROUP BY  DAY(DATE_ADD(sent_date, INTERVAL "+tzOffSet+" MINUTE)) ORDER BY 2";
		}else{
			qry="SELECT COUNT(id) AS count, MONTH(DATE_ADD(sent_date, INTERVAL "+tzOffSet+" MINUTE)) AS month FROM dr_sms_sent WHERE user_id ="+userId.longValue()+" " +" AND sent_date >= '"+fromDate+"' AND sent_date <='"+toDate+"'   " ;
			if(store !=null && !store.trim().isEmpty()){
				qry += " AND store_number = '"+store+"'";
			}
			qry += " GROUP BY  MONTH(DATE_ADD(sent_date, INTERVAL "+tzOffSet+" MINUTE)) ORDER BY 2";

		}
		return	jdbcTemplate.queryForList(qry);


	}//findTotSentRate()
public List<DRSMSSent> findByIds(String msgIds) {
		
		List<DRSMSSent> retSmsCampaignSentList = getHibernateTemplate().find("FROM DRSMSSent " +
				"WHERE id IN("+msgIds+") ");

		if(retSmsCampaignSentList != null && retSmsCampaignSentList.size() > 0) {
		
			return retSmsCampaignSentList;
		}

		return null;
		
		
	}
public List<DRSMSSent> findByMsgIds(String msgIds) {
		
		List<DRSMSSent> retSmsCampaignSentList = getHibernateTemplate().find("FROM DRSMSSent " +
				"WHERE messageId IN("+msgIds+") ");

		if(retSmsCampaignSentList != null && retSmsCampaignSentList.size() > 0) {
		
			return retSmsCampaignSentList;
		}

		return null;
		
		
	}

public DRSMSSent findlongUrlByShortCode(String shortCode) {
	try {
		
		
		String query = "FROM DRSMSSent WHERE  originalShortCode='" + shortCode + "' ORDER BY id DESC";
    	List<DRSMSSent> list = executeQuery(query, 0,1);
    	if(list == null || list.size() <1) {
    		
    		return null;
    	}
    	return list.get(0);
		
		
		
	} catch (Exception e) {
		return null;
	}
}
}
