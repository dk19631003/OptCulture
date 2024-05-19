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
import org.mq.marketer.campaign.beans.DRSMSChannelSent;
import org.mq.marketer.campaign.general.Constants;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

public class DRSMSChannelSentDao extends AbstractSpringDao {
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);

	private SessionFactory sessionFactory;

	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}


	public DRSMSChannelSent find(Long id) {
		return (DRSMSChannelSent) super.find(DRSMSChannelSent.class, id);
	}

	public int findTotalReciepients(Long userId, String key){
		String	 qry ="SELECT  COUNT(Distinct mobile) FROM  DRSMSChannelSent  WHERE userId ="+userId.longValue()+" " ;
		if(key != null){
			qry += " AND mobile like '%"+key+"%'";
		}

		return ((Long)getHibernateTemplate().find(qry).get(0)).intValue();
	}
	public int findDRBasedOnDatesForStore(Long userId, String fromDate,String toDate, String key, String store, String smsStatusTobeFetched){
		if(store == null){
			return findDRBasedOnDates(userId, fromDate,toDate,key,smsStatusTobeFetched);
		}else{
			String qry = "SELECT  COUNT(id) FROM  DRSMSChannelSent  WHERE userId ="+userId.longValue()+" " +
					" AND sentDate >= '"+fromDate+"' AND sentDate <='"+toDate+"' AND storeNumber like '"+store+"'";

			if(key != null){
				qry += " AND mobile like '%"+key+"%'";
			}

			if(! smsStatusTobeFetched.contains("special_condtion_for_all") ){
				qry += "  AND status IN("+smsStatusTobeFetched+")  ";
			}
			int count =   ((Long)getHibernateTemplate().find(qry).get(0)).intValue();

			return count;
		}

	}
	public int findStoreCountBasedOnDates(Long userId, String fromDate,String toDate, String store, String emailSatusTobeFetched){
		String qry = "SELECT  COUNT(Distinct storeNumber) FROM  DRSMSChannelSent  WHERE userId ="+userId.longValue()+" " +
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
		String qry = "SELECT  COUNT(id) FROM  DRSMSChannelSent  WHERE userId ="+userId.longValue()+" " +
				" AND sentDate >= '"+fromDate+"' AND sentDate <='"+toDate+"'";

		if(key != null){
			qry += " AND mobile like '%"+key+"%'";
		}

		if(! smsStatusTobeFetched.contains("special_condtion_for_all") ){
			qry += "  AND status IN("+smsStatusTobeFetched+")  ";
		}

		int count =   ((Long)getHibernateTemplate().find(qry).get(0)).intValue();

		return count;
	} // findCoupBasedOnDates
	public List<String> findAllStores(Long userId){
		try{
			List<String> list = null;
			String query = "SELECT DISTINCT storeNumber from DRSMSChannelSent WHERE storeNumber IS NOT NULL AND userId = "+userId.longValue();
			list = executeQuery(query);
			return list;
		}catch(Exception e){
			logger.info("Exception while getting stores ",e);
		}
		return new ArrayList<String>();
	}
	public long findTotalStorePurchases(Long userId, String fromDate,String toDate, String store, String emailSatusTobeFetched){
		List<Object[]> list = null;
		String query = "SELECT COUNT(id) FROM DRSMSChannelSent WHERE storeNumber IS NOT NULL AND userId = "+userId.longValue()+
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
			String query = "SELECT storeNumber, COUNT(id)  FROM DRSMSChannelSent WHERE storeNumber IS NOT NULL AND userId = "+userId.longValue()+
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
			String query = "SELECT mobile,sentDate FROM DRSMSChannelSent WHERE userId = "+userId.longValue()+
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
				"Count(dr.id) as totalSentCount FROM dr_sms_channel_sent dr where   dr.user_id= "+userId.longValue();

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

		String subQry="SELECT Count(dr.id) as totalPendingCount FROM dr_sms_channel_sent dr where dr.user_id= "+userId.longValue()+
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
	public List<DRSMSChannelSent> findRecipientsReports(Long userId,int start,int end, String key){


		try {
			List<DRSMSChannelSent > list = null;
			String subQry="SELECT dr.mobile, MAX(dr.sent_date) as lastSent, SUM(IF(dr.opens >0, 1,0)) as uniopens,SUM(IF(dr.clicks >0, 1,0)) as uniclicks," +
					"Count(dr.id) as totalSentCount,dr.id, dr.doc_sid FROM dr_sms_channel_sent dr where   dr.user_id= "+userId.longValue();

			if(key != null){

				subQry += " AND dr.mobile like '%"+key+"%'";
			}

			String query  = subQry +" group by dr.mobile  ORDER BY lastSent DESC  LIMIT "+" "+start+","+end ;



			list = jdbcTemplate.query(query, new RowMapper() {

				public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
					DRSMSChannelSent drSmsChannelSent= new DRSMSChannelSent();
					drSmsChannelSent.setId(rs.getLong("id"));
					drSmsChannelSent.setMobile(rs.getString("mobile"));
					drSmsChannelSent.setUniqueOpens( rs.getInt("uniopens"));
					drSmsChannelSent.setUniqueClicks( rs.getInt("uniclicks"));
					drSmsChannelSent.setSentCount( rs.getInt("totalSentCount"));
					drSmsChannelSent.setDocSid(rs.getString("doc_sid"));
					return drSmsChannelSent;
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


	public List<DRSMSChannelSent> findStatusPendingReports(Long userId,int start,int end, String key){
		try {
			List<DRSMSChannelSent > list = null;
			String subQry="SELECT dr.mobile,dr.sent_date FROM dr_sms_channel_sent dr where dr.user_id= "+userId.longValue()+" and dr.status='Submitted'" ;

			if(key != null){

				subQry += " AND dr.mobile like '%"+key+"%'";
			}

			String query  = subQry +" group by dr.mobile  ORDER BY dr.sent_date DESC  LIMIT "+" "+start+","+end ;


			list = jdbcTemplate.query(query, new RowMapper() {

				public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
					DRSMSChannelSent drSmsChannelSent= new DRSMSChannelSent();
					//drSmsChannelSent.setId(rs.getLong("id"));
					drSmsChannelSent.setMobile(rs.getString("mobile"));
					Calendar cal = Calendar.getInstance();
					cal.setTime(rs.getTimestamp("sent_date"));

					drSmsChannelSent.setSentDate(cal);
					return drSmsChannelSent;
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


	public List< DRSMSChannelSent> findDrReports(Long userId,int start,int end,String fromDate,String toDate,String key, String store,String smsStatusTobeFetched,String orderby_colName,String desc_Asc) {

		try {
			List<DRSMSChannelSent > list = null;
			String subQry="SELECT dr.id, dr.sent_date, dr.mobile, dr.status, dr.store_number, dr.opens, dr.clicks ,dr.sent_count,dr.doc_sid ,dr.html_content " +
					" FROM dr_sms_channel_sent dr where   dr.user_id= "+userId.longValue()+
					" and dr.sent_date  >= '"+fromDate+"' and  dr.sent_date <= '"+toDate+"' and dr.status NOT IN('"+Constants.DR_STATUS_SUBMITTED+"')";

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
					DRSMSChannelSent drSmsChannelSent= new DRSMSChannelSent();
					drSmsChannelSent.setId(rs.getLong("id"));
					Calendar cal = Calendar.getInstance();
					cal.setTime(rs.getTimestamp("sent_date"));

					drSmsChannelSent.setSentDate(cal);
					drSmsChannelSent.setMobile(rs.getString("mobile"));


					drSmsChannelSent.setStatus( rs.getString("status"));
					drSmsChannelSent.setStoreNumber(rs.getString("store_number"));
					drSmsChannelSent.setSentCount(rs.getInt("sent_count"));

					drSmsChannelSent.setOpens(Integer.parseInt(rs.getString("opens")));
					drSmsChannelSent.setClicks(Integer.parseInt(rs.getString("clicks")));
					drSmsChannelSent.setDocSid(rs.getString("doc_sid"));
					drSmsChannelSent.setHtmlContent(rs.getString("html_content"));


					return drSmsChannelSent;
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

	public List< DRSMSChannelSent> findPenidngReports(Long userId,int start,int end,String fromDate,String toDate,String key, String store,String smsStatusTobeFetched,String orderby_colName,String desc_Asc) {

		try {
			List<DRSMSChannelSent > list = null;

			String subQry="SELECT dr.mobile,dr.sent_date FROM dr_sms_channel_sent dr where dr.user_id= "+userId.longValue()+
					" and dr.sent_date  >= '"+fromDate+"' and  dr.sent_date <= '"+toDate+"'";

			if(key != null){

				subQry += " AND dr.mobile like '%"+key+"%'";
			}if( store != null){
				subQry += " AND dr.store_number = '"+store+"'";
			}



			subQry += "  AND (dr.status='Submitted')";

			String query=subQry;

			logger.info("query >>> "+query);

			list = jdbcTemplate.query(query, new RowMapper() {

				public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
					DRSMSChannelSent drSmsChannelSent= new DRSMSChannelSent();
					drSmsChannelSent.setMobile(rs.getString("mobile"));
					Calendar cal = Calendar.getInstance();
					cal.setTime(rs.getTimestamp("sent_date"));

					drSmsChannelSent.setSentDate(cal);

					return drSmsChannelSent;
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



	public long findTotSentCount(Long userId,String fromDate,String toDate, String store){
		String qry="SELECT COUNT(id) FROM DRSMSChannelSent WHERE userId ="+userId.longValue()+"  AND sentDate >= '"+fromDate+"' AND sentDate <='"+toDate+"'";
		if(store !=null && !store.trim().isEmpty()){
			qry += " AND storeNumber = '"+store+"'";
		}
		Long count =   ((Long)getHibernateTemplate().find(qry).get(0)).longValue();

		return count;


	}

	public  long findTotDRSMSChannelSentCount(Long userId){
		String qry="SELECT COUNT(id) FROM DRSMSChannelSent WHERE userId ="+userId.longValue()+" ";

		Long count =   ((Long)getHibernateTemplate().find(qry).get(0)).longValue();

		return count;

	}

	public long findOpenCount(Long userId,String fromDate,String toDate, String store){
		String qry="SELECT COUNT(id) FROM DRSMSChannelSent WHERE userId ="+userId.longValue()+"  AND sentDate >= '"+fromDate+"' AND sentDate <='"+toDate+"'" +" AND opens > 0";
		if(store !=null && !store.trim().isEmpty()){
			qry += " AND storeNumber = '"+store+"'";
		}
		Long count =   ((Long)getHibernateTemplate().find(qry).get(0)).longValue();
		return count;


	}//findOpenCount

	public long findClickCount(Long userId,String fromDate,String toDate, String store){
		String qry="SELECT  COUNT(id) FROM DRSMSChannelSent WHERE userId ="+userId.longValue()+"  AND sentDate >= '"+fromDate+"' AND sentDate <='"+toDate+"'"+" AND clicks > 0";
		if(store !=null && !store.trim().isEmpty()){
			qry += " AND storeNumber = '"+store+"'";
		}
		Long count =   ((Long)getHibernateTemplate().find(qry).get(0)).longValue();

		return count;


	}//findOpenCount



	public long findTotBounceCount(Long userId,String fromDate,String toDate, String store){
		String qry="SELECT COUNT(id) FROM DRSMSChannelSent WHERE userId ="+userId.longValue()+""+
				"   AND  sentDate >= '"+fromDate+"' AND sentDate <='"+toDate+"'  AND  " +
				" status in('" +Constants.DR_STATUS_BOUNCED+"','"+Constants.DR_STATUS_DROPPED+"','" +Constants.DR_STATUS_BOUNCE+"') ";
		if(store !=null && !store.trim().isEmpty()){
			qry += " AND storeNumber = '"+store+"'";
		}	

		Long count =   ((Long)getHibernateTemplate().find(qry).get(0)).longValue();

		return count;


	}

	public long findTotNotSentCount(Long userId,String fromDate,String toDate, String store){
		String qry="SELECT COUNT(id) FROM DRSMSChannelSent WHERE userId ="+userId.longValue()+""+
				"   AND  sentDate >= '"+fromDate+"' AND sentDate <='"+toDate+"'  AND  " +
				" status ='"+Constants.DR_STATUS_SUBMITTED+"' ";
		if(store !=null && !store.trim().isEmpty()){
			qry += " AND storeNumber = '"+store+"'";
		}	

		Long count =   ((Long)getHibernateTemplate().find(qry).get(0)).longValue();

		return count;


	}

	public long findTotSpammedCount(Long userId,String fromDate,String toDate, String store){
		String qry="SELECT COUNT(id) FROM DRSMSChannelSent WHERE userId ="+userId.longValue()+""+
				"  AND sentDate >= '"+fromDate+"' AND sentDate <='"+toDate+"'  AND   " +
				" status in ('" +Constants.DR_STATUS_SPAMMED+"' ,'" +Constants.DR_STATUS_SPAME+"')" ;
		if(store !=null && !store.trim().isEmpty()){
			qry += " AND storeNumber = '"+store+"'";
		}	

		Long count =   ((Long)getHibernateTemplate().find(qry).get(0)).longValue();

		return count;


	}

	public DRSMSChannelSent findById(long id){
		DRSMSChannelSent drSMSChannelSent = null;
		try {
			List list = getHibernateTemplate().find(
					"from DRSMSChannelSent where id = " + id );
			if (list.size() > 0) {
				drSMSChannelSent = (DRSMSChannelSent) list.get(0);
			}
		} catch (Exception e) {
			logger.error(" ** Exception : ", e ); 
		}
		return drSMSChannelSent;

	}


	public DRSMSChannelSent findDrById(String mobile, Long userId){
		DRSMSChannelSent drSMSChannelSent = null;
		try {
			List list = getHibernateTemplate().find(
					"FROM DRSMSChannelSent WHERE userId="+userId.longValue()+" AND mobile = '" + mobile +"' order by sentDate desc");
			if (list.size() > 0) {
				drSMSChannelSent = (DRSMSChannelSent) list.get(0);
			}
		} catch (Exception e) {
			logger.error(" ** Exception : ", e ); 
		}
		return drSMSChannelSent;

	}

	public List<Map<String,Object>> findTotSentRate(Long userId,String fromDate,String toDate, String type, String store, int tzOffSet){
		String qry="";
		if(type.equals("Days")){

			qry="SELECT COUNT(id) AS count, DAY(DATE_ADD(sent_date, INTERVAL "+tzOffSet+" MINUTE)) AS day FROM dr_sms_channel_sent WHERE user_id ="+userId.longValue()+" " +" AND sent_date >= '"+fromDate+"' AND sent_date <='"+toDate+"'   " ;
			if(store !=null && !store.trim().isEmpty()){
				qry += " AND store_number = '"+store+"'";
			}
			qry += " GROUP BY  DAY(DATE_ADD(sent_date, INTERVAL "+tzOffSet+" MINUTE)) ORDER BY 2";
		}else{
			qry="SELECT COUNT(id) AS count, MONTH(DATE_ADD(sent_date, INTERVAL "+tzOffSet+" MINUTE)) AS month FROM dr_sms_channel_sent WHERE user_id ="+userId.longValue()+" " +" AND sent_date >= '"+fromDate+"' AND sent_date <='"+toDate+"'   " ;
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

			qry="SELECT COUNT(id) AS count, DAY(DATE_ADD(sent_date, INTERVAL "+tzOffSet+" MINUTE)) AS day FROM dr_sms_channel_sent WHERE user_id ="+userId.longValue()+" " +" AND sent_date >= '"+fromDate+"' AND sent_date <='"+toDate+"' AND status='"+Constants.SMS_STATUS_DELIVERED+"'";
			if(store !=null && !store.trim().isEmpty()){
				qry += " AND store_number = '"+store+"'";
			}
			qry += "   GROUP BY  DAY(DATE_ADD(sent_date, INTERVAL "+tzOffSet+" MINUTE)) ORDER BY 2";
		}else{
			qry="SELECT  COUNT(id) AS count, MONTH(DATE_ADD(sent_date, INTERVAL "+tzOffSet+" MINUTE)) AS month FROM dr_sms_channel_sent WHERE user_id ="+userId.longValue()+" " +" AND sent_date >= '"+fromDate+"' AND sent_date <='"+toDate+"' AND  status='"+Constants.SMS_STATUS_DELIVERED+"'";
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

			qry="SELECT COUNT(id) AS count, DAY(DATE_ADD(sent_date, INTERVAL "+tzOffSet+" MINUTE)) AS day FROM dr_sms_channel_sent WHERE user_id ="+userId.longValue()+" " +" AND sent_date >= '"+fromDate+"' AND sent_date <='"+toDate+"' AND opens > 0";
			if(store !=null && !store.trim().isEmpty()){
				qry += " AND store_number = '"+store+"'";
			}
			qry += "     GROUP BY  DAY(DATE_ADD(sent_date, INTERVAL "+tzOffSet+" MINUTE)) ORDER BY 2";
		}else{
			qry="SELECT COUNT(id) AS count, MONTH(DATE_ADD(sent_date, INTERVAL "+tzOffSet+" MINUTE)) AS month FROM dr_sms_channel_sent WHERE user_id ="+userId.longValue()+" " +" AND sent_date >= '"+fromDate+"' AND sent_date <='"+toDate+"'  AND opens > 0" ;
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

			qry="SELECT COUNT(id) AS count, DAY(DATE_ADD(sent_date, INTERVAL "+tzOffSet+" MINUTE)) AS day FROM dr_sms_channel_sent WHERE user_id ="+userId.longValue()+" " +" AND sent_date >= '"+fromDate+"' AND sent_date <='"+toDate+"'  AND clicks > 0";
			if(store !=null && !store.trim().isEmpty()){
				qry += " AND store_number = '"+store+"'";
			}
			qry += "    GROUP BY  DAY(DATE_ADD(sent_date, INTERVAL "+tzOffSet+" MINUTE)) ORDER BY 2";
		}else{
			qry="SELECT COUNT(id) AS count, MONTH(DATE_ADD(sent_date, INTERVAL "+tzOffSet+" MINUTE)) AS month FROM dr_sms_channel_sent WHERE user_id ="+userId.longValue()+" " +" AND sent_date >= '"+fromDate+"' AND sent_date <='"+toDate+"'  AND clicks > 0" ;
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

			qry="SELECT COUNT(id) AS count, DAY(DATE_ADD(sent_date, INTERVAL "+tzOffSet+" MINUTE)) AS day FROM dr_sms_channel_sent WHERE user_id ="+userId.longValue()+" " +" AND sent_date >= '"+fromDate+"' AND sent_date <='"+toDate+"' AND status in('"+Constants.DR_STATUS_BOUNCED+"','"+Constants.DR_STATUS_DROPPED+"','" +Constants.DR_STATUS_BOUNCE+"')";
			if(store !=null && !store.trim().isEmpty()){
				qry += " AND store_number = '"+store+"'";
			}
			qry += "   GROUP BY  DAY(DATE_ADD(sent_date, INTERVAL "+tzOffSet+" MINUTE)) ORDER BY 2";
		}else{
			qry="SELECT COUNT(id) AS count, MONTH(DATE_ADD(sent_date, INTERVAL "+tzOffSet+" MINUTE)) AS month FROM dr_sms_channel_sent WHERE user_id ="+userId.longValue()+" " +" AND sent_date >= '"+fromDate+"' AND sent_date <='"+toDate+"' AND  status in('"+Constants.DR_STATUS_BOUNCED+"','"+Constants.DR_STATUS_DROPPED+"','" +Constants.DR_STATUS_BOUNCE+"')" ;
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

			qry="SELECT COUNT(id) AS count, DAY(DATE_ADD(sent_date, INTERVAL "+tzOffSet+" MINUTE)) AS day FROM dr_sms_channel_sent WHERE user_id ="+userId.longValue()+" " +" AND sent_date >= '"+fromDate+"' AND sent_date <='"+toDate+"' AND status in('"+Constants.DR_STATUS_SUBMITTED+"')";
			if(store !=null && !store.trim().isEmpty()){
				qry += " AND store_number = '"+store+"'";
			}
			qry += "   GROUP BY  DAY(DATE_ADD(sent_date, INTERVAL "+tzOffSet+" MINUTE)) ORDER BY 2";
		}else{
			qry="SELECT COUNT(id) AS count, MONTH(DATE_ADD(sent_date, INTERVAL "+tzOffSet+" MINUTE)) AS month FROM dr_sms_channel_sent WHERE user_id ="+userId.longValue()+" " +" AND sent_date >= '"+fromDate+"' AND sent_date <='"+toDate+"' AND  status in('"+Constants.DR_STATUS_SUBMITTED+"')" ;
			if(store !=null && !store.trim().isEmpty()){
				qry += " AND store_number = '"+store+"'";
			}
			qry += "   GROUP BY  MONTH(DATE_ADD(sent_date, INTERVAL "+tzOffSet+" MINUTE)) ORDER BY 2";

		}
		return  getJdbcTemplate().queryForList(qry);

	}

	//all view 
	public List <DRSMSChannelSent> findAllDrView(String mobile,long userId){
		try {
			return getHibernateTemplate().find(
					"FROM DRSMSChannelSent WHERE userId="+userId+" AND mobile = '" + mobile + "'"+"order by sentDate desc");
		} catch (Exception e) {
			logger.error(" ** Exception : ", e ); 
			return null;
		}

	}

	public int findConfiguredTemplateCount(Long userId, String templateName) {

		try {
			templateName = StringEscapeUtils.escapeSql(templateName);
			String qry = "SELECT COUNT(id) FROM DRSMSChannelSent WHERE userId ="+userId.longValue()+" " +" AND templateName='MY_TEMPLATE:"+templateName+"'"; 

			int count =   ((Long)getHibernateTemplate().find(qry).get(0)).intValue();

			return count;
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
			return 0;
		}



	}


	public long findTotDelCount(Long userId,String fromDate,String toDate, String store){
		String qry="SELECT COUNT(id) FROM DRSMSChannelSent WHERE userId ="+userId.longValue()+""+
				"   AND sentDate >= '"+fromDate+"' AND sentDate <='"+toDate+"' AND  status ='" +Constants.SMS_STATUS_DELIVERED+"' ";
		if(store !=null && !store.trim().isEmpty()){
			qry += " AND storeNumber = '"+store+"'";
		}		

		Long count =   ((Long)getHibernateTemplate().find(qry).get(0)).longValue();

		return count;


	}
	// Added after DR Resend

	public DRSMSChannelSent findBy( Long userId , String docSid, String mobile ) throws Exception {

		List<DRSMSChannelSent> drsentObjList = null;

		String qryStr = "FROM DRSMSChannelSent WHERE userId ="+userId.longValue()+""
				+ " AND docSid= '"+docSid+"' and mobile='"+mobile+"' ORDER BY sentDate DESC" ;
		drsentObjList = getHibernateTemplate().find(qryStr);
		if(drsentObjList != null &&  drsentObjList.size() > 0){
			return drsentObjList.get(0);
		}else{
			return null;
		}


	}

	public DRSMSChannelSent findBy( Long userId , String docSid) throws Exception {

		List<DRSMSChannelSent> drsentObjList = null;

		String qryStr = "FROM DRSMSChannelSent WHERE userId ="+userId.longValue()+""
				+ " AND docSid= '"+docSid+"' ORDER BY sentDate DESC" ;
		drsentObjList = getHibernateTemplate().find(qryStr);
		if(drsentObjList != null &&  drsentObjList.size() > 0){
			return drsentObjList.get(0);
		}else{
			return null;
		}


	}


	public long getCountBy(Long userId,String fromDate,String toDate){
		try {
			String qry="SELECT COUNT(id) FROM DRSMSChannelSent WHERE userId ="+userId.longValue()+""+
					"  AND sentDate >= '"+fromDate+"' AND sentDate <='"+toDate+"'" ;


			Long count =   ((Long)getHibernateTemplate().find(qry).get(0)).longValue();

			return count;
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			logger.error("Exception occurred while fetching the count ", e);
		}
		return 0;

	}

	public List<DRSMSChannelSent> find(Long userId,String fromDate,String toDate){
		List<DRSMSChannelSent> retList = null;
		try {

			String qry=" FROM DRSMSChannelSent WHERE userId ="+userId.longValue()+""+
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
				qry = "SELECT  COUNT(id) FROM  DRSMSChannelSent  WHERE userId ="+userId.longValue()+" " +
						" AND sentDate >= '"+fromDate+"' AND sentDate <='"+toDate+"' AND zone_id = "+zoneId+" AND storeNumber like '"+store+"'";

			}else if(zoneId !=null){
				qry = "SELECT  COUNT(id) FROM  DRSMSChannelSent  WHERE userId ="+userId.longValue()+" " +
						" AND sentDate >= '"+fromDate+"' AND sentDate <='"+toDate+"' AND zone_id = "+zoneId;

			}else{
				qry = "SELECT  COUNT(id) FROM  DRSMSChannelSent  WHERE userId ="+userId.longValue()+" " +
						" AND sentDate >= '"+fromDate+"' AND sentDate <='"+toDate+"' AND storeNumber like '"+store+"'";
			}
			if(key != null){
				qry += " AND mobile like '%"+key+"%'";
			}

			if(! emailSatusTobeFetched.contains("special_condtion_for_all") ){
				qry += "  AND status IN("+emailSatusTobeFetched+")  ";
			}

			int count =   ((Long)getHibernateTemplate().find(qry).get(0)).intValue();

			return count;
		}

	}


	public int findDRBasedOnDatesZone(Long userId, String fromDate,String toDate,String key, String emailSatusTobeFetched) {
		String qry = "SELECT  COUNT(id) FROM  DRSMSChannelSent  WHERE userId ="+userId.longValue()+" " +
				" AND sentDate >= '"+fromDate+"' AND sentDate <='"+toDate+"'";

		if(key != null){
			qry += " AND mobile like '%"+key+"%'";
		}

		if(! emailSatusTobeFetched.contains("special_condtion_for_all") ){
			qry += "  AND status IN("+emailSatusTobeFetched+")  ";
		}

		int count =   ((Long)getHibernateTemplate().find(qry).get(0)).intValue();

		return count;
	}


	public List< DRSMSChannelSent> findDrReports(Long userId,int start,int end,String fromDate,String toDate,String key, String store,String emailSatusTobeFetched,String orderby_colName,String desc_Asc,Long zoneId) {

		try {
			List<DRSMSChannelSent > list = null;
			String subQry="SELECT dr.id, dr.sent_date, dr.mobile, dr.status, dr.store_number, dr.opens, dr.clicks ,dr.doc_sid ,dr.html_content " +
					" FROM dr_sms_channel_sent dr where   dr.user_id= "+userId.longValue()+
					" and dr.sent_date  >= '"+fromDate+"' and  dr.sent_date <= '"+toDate+"' and dr.status NOT IN('"+Constants.DR_STATUS_SUBMITTED+"')";

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
					DRSMSChannelSent drSmsChannelSent= new DRSMSChannelSent();
					drSmsChannelSent.setId(rs.getLong("id"));
					Calendar cal = Calendar.getInstance();
					cal.setTime(rs.getTimestamp("sent_date"));

					drSmsChannelSent.setSentDate(cal);
					drSmsChannelSent.setMobile(rs.getString("mobile"));


					drSmsChannelSent.setStatus( rs.getString("status"));
					drSmsChannelSent.setStoreNumber(rs.getString("store_number"));


					drSmsChannelSent.setClicks(Integer.parseInt(rs.getString("clicks")));
					drSmsChannelSent.setDocSid(rs.getString("doc_sid"));
					drSmsChannelSent.setHtmlContent(rs.getString("html_content"));


					return drSmsChannelSent;
				}
			});

			if(list!= null && list.size()>0) {
				return list;
			}

			return null;
		} catch (Exception e) {
			logger.error("Exception ::" , e);
			return null;
		}
	}


	public List< DRSMSChannelSent> findPenidngReports(Long userId,int start,int end,String fromDate,String toDate,String key, String store,String emailSatusTobeFetched,String orderby_colName,String desc_Asc,Long zoneId) {
		try {
			List<DRSMSChannelSent > list = null;

			String subQry="SELECT dr.mobile,dr.sent_date FROM dr_sms_channel_sent dr where dr.user_id= "+userId.longValue()+
					" and dr.sent_date  >= '"+fromDate+"' and  dr.sent_date <= '"+toDate+"'";

			if(key != null){

				subQry += " AND dr.mobile like '%"+key+"%'";
			}if( store != null){
				subQry += " AND dr.store_number = '"+store+"'";
			}
			if( zoneId != null){
				subQry += " AND dr.zone_Id = "+zoneId;
			}


			subQry += "  AND (dr.status='Submitted')";


			String query=subQry;

			logger.info("query >>> "+query);

			list = jdbcTemplate.query(query, new RowMapper() {

				public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
					DRSMSChannelSent drSmsChannelSent= new DRSMSChannelSent();
					drSmsChannelSent.setMobile(rs.getString("mobile"));
					Calendar cal = Calendar.getInstance();
					cal.setTime(rs.getTimestamp("sent_date"));

					drSmsChannelSent.setSentDate(cal);

					return drSmsChannelSent;
				}
			});

			if(list!= null && list.size()>0) {
				return list;
			}

			return null;
		} catch (Exception e) {
			logger.error("Exception ::" , e);
			return null;
		}
	}

	public long findTotSentCount(Long userId,String fromDate,String toDate, String store, Long zoneId){
		String qry="SELECT COUNT(id) FROM DRSMSChannelSent WHERE userId ="+userId.longValue()+"  AND sentDate >= '"+fromDate+"' AND sentDate <='"+toDate+"'";
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
		String qry="SELECT COUNT(id) FROM DRSMSChannelSent WHERE userId ="+userId.longValue()+""+
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
		String qry="SELECT COUNT(id) FROM DRSMSChannelSent WHERE userId ="+userId.longValue()+""+
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
		String qry="SELECT COUNT(id) FROM DRSMSChannelSent WHERE userId ="+userId.longValue()+""+
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
		String qry="SELECT COUNT(id) FROM DRSMSChannelSent WHERE userId ="+userId.longValue()+"  AND sentDate >= '"+fromDate+"' AND sentDate <='"+toDate+"'" +" AND opens > 0";
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
		String qry="SELECT  COUNT(id) FROM DRSMSChannelSent WHERE userId ="+userId.longValue()+"  AND sentDate >= '"+fromDate+"' AND sentDate <='"+toDate+"'"+" AND clicks > 0";
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
		String qry="SELECT COUNT(id) FROM DRSMSChannelSent WHERE userId ="+userId.longValue()+""+
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
			String query = "SELECT mobile,sentDate FROM DRSMSChannelSent WHERE userId = "+userId.longValue()+
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
		String query = "SELECT COUNT(id) FROM DRSMSChannelSent WHERE storeNumber IS NOT NULL AND userId = "+userId.longValue()+
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
		String qry = "SELECT  COUNT(Distinct storeNumber) FROM  DRSMSChannelSent  WHERE userId ="+userId.longValue()+" " +
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
			String query = "SELECT storeNumber, COUNT(id)  FROM DRSMSChannelSent WHERE storeNumber IS NOT NULL AND userId = "+userId.longValue()+
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
			String qry = "SELECT COUNT(id) FROM DRSMSChannelSent WHERE userId ="+userId.longValue()+" " +" AND myTemplateId="+myTemplateId; 

			int count =   ((Long)getHibernateTemplate().find(qry).get(0)).intValue();

			return count;
		} catch (DataAccessException e) {
			logger.error("Exception ::" , e);
			return 0;
		}
	}

	public List<Map<String,Object>> findTotStatusPendingRate(Long userId,String fromDate,String toDate, String type, String store, int tzOffSet, Long zoneId){
		String qry="";
		if(type.equals("Days")){

			qry="SELECT COUNT(id) AS count, DAY(DATE_ADD(sent_date, INTERVAL "+tzOffSet+" MINUTE)) AS day FROM dr_sms_channel_sent WHERE user_id ="+userId.longValue()+" " +" AND sent_date >= '"+fromDate+"' AND sent_date <='"+toDate+"' AND status in('"+Constants.DR_STATUS_SUBMITTED+"')";
			if(store !=null && !store.trim().isEmpty()){
				qry += " AND store_number = '"+store+"'";
			}
			
			if(zoneId !=null){
				qry += " AND zone_Id = "+zoneId;
			}
			qry += "   GROUP BY  DAY(DATE_ADD(sent_date, INTERVAL "+tzOffSet+" MINUTE)) ORDER BY 2";
		}else{
			qry="SELECT COUNT(id) AS count, MONTH(DATE_ADD(sent_date, INTERVAL "+tzOffSet+" MINUTE)) AS month FROM dr_sms_channel_sent WHERE user_id ="+userId.longValue()+" " +" AND sent_date >= '"+fromDate+"' AND sent_date <='"+toDate+"' AND  status in('"+Constants.DR_STATUS_SUBMITTED+"')" ;
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

			qry="SELECT COUNT(id) AS count, DAY(DATE_ADD(sent_date, INTERVAL "+tzOffSet+" MINUTE)) AS day FROM dr_sms_channel_sent WHERE user_id ="+userId.longValue()+" " +" AND sent_date >= '"+fromDate+"' AND sent_date <='"+toDate+"' AND status in('"+Constants.DR_STATUS_BOUNCED+"','"+Constants.DR_STATUS_DROPPED+"','" +Constants.DR_STATUS_BOUNCE+"')";
			if(store !=null && !store.trim().isEmpty()){
				qry += " AND store_number = '"+store+"'";
			}
			
			if(zoneId !=null){
				qry += " AND zone_Id = "+zoneId;
			}
			
			qry += "   GROUP BY  DAY(DATE_ADD(sent_date, INTERVAL "+tzOffSet+" MINUTE)) ORDER BY 2";
		}else{
			qry="SELECT COUNT(id) AS count, MONTH(DATE_ADD(sent_date, INTERVAL "+tzOffSet+" MINUTE)) AS month FROM dr_sms_channel_sent WHERE user_id ="+userId.longValue()+" " +" AND sent_date >= '"+fromDate+"' AND sent_date <='"+toDate+"' AND  status in('"+Constants.DR_STATUS_BOUNCED+"','"+Constants.DR_STATUS_DROPPED+"','" +Constants.DR_STATUS_BOUNCE+"')" ;
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

			qry="SELECT COUNT(id) AS count, DAY(DATE_ADD(sent_date, INTERVAL "+tzOffSet+" MINUTE)) AS day FROM dr_sms_channel_sent WHERE user_id ="+userId.longValue()+" " +" AND sent_date >= '"+fromDate+"' AND sent_date <='"+toDate+"'  AND clicks > 0";
			if(store !=null && !store.trim().isEmpty()){
				qry += " AND store_number = '"+store+"'";
			}
			if(zoneId !=null){
				qry += " AND zone_Id = "+zoneId;
			}
			qry += "    GROUP BY  DAY(DATE_ADD(sent_date, INTERVAL "+tzOffSet+" MINUTE)) ORDER BY 2";
		}else{
			qry="SELECT COUNT(id) AS count, MONTH(DATE_ADD(sent_date, INTERVAL "+tzOffSet+" MINUTE)) AS month FROM dr_sms_channel_sent WHERE user_id ="+userId.longValue()+" " +" AND sent_date >= '"+fromDate+"' AND sent_date <='"+toDate+"'  AND clicks > 0" ;
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

			qry="SELECT COUNT(id) AS count, DAY(DATE_ADD(sent_date, INTERVAL "+tzOffSet+" MINUTE)) AS day FROM dr_sms_channel_sent WHERE user_id ="+userId.longValue()+" " +" AND sent_date >= '"+fromDate+"' AND sent_date <='"+toDate+"' AND opens > 0";
			if(store !=null && !store.trim().isEmpty()){
				qry += " AND store_number = '"+store+"'";
			}
			if(zoneId !=null){
				qry += " AND zone_Id = "+zoneId;
			}
			qry += "     GROUP BY  DAY(DATE_ADD(sent_date, INTERVAL "+tzOffSet+" MINUTE)) ORDER BY 2";
		}else{
			qry="SELECT COUNT(id) AS count, MONTH(DATE_ADD(sent_date, INTERVAL "+tzOffSet+" MINUTE)) AS month FROM dr_sms_channel_sent WHERE user_id ="+userId.longValue()+" " +" AND sent_date >= '"+fromDate+"' AND sent_date <='"+toDate+"'  AND opens > 0" ;
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

			qry="SELECT COUNT(id) AS count, DAY(DATE_ADD(sent_date, INTERVAL "+tzOffSet+" MINUTE)) AS day FROM dr_sms_channel_sent WHERE user_id ="+userId.longValue()+" " +" AND sent_date >= '"+fromDate+"' AND sent_date <='"+toDate+"' AND status='"+Constants.SMS_STATUS_DELIVERED+"'";
			if(store !=null && !store.trim().isEmpty()){
				qry += " AND store_number = '"+store+"'";
			}
			if(zoneId !=null){
				qry += " AND zone_Id = "+zoneId;
			}
			qry += "   GROUP BY  DAY(DATE_ADD(sent_date, INTERVAL "+tzOffSet+" MINUTE)) ORDER BY 2";
		}else{
			qry="SELECT  COUNT(id) AS count, MONTH(DATE_ADD(sent_date, INTERVAL "+tzOffSet+" MINUTE)) AS month FROM dr_sms_channel_sent WHERE user_id ="+userId.longValue()+" " +" AND sent_date >= '"+fromDate+"' AND sent_date <='"+toDate+"' AND  status='"+Constants.SMS_STATUS_DELIVERED+"'";
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

			qry="SELECT COUNT(id) AS count, DAY(DATE_ADD(sent_date, INTERVAL "+tzOffSet+" MINUTE)) AS day FROM dr_sms_channel_sent WHERE user_id ="+userId.longValue()+" " +" AND sent_date >= '"+fromDate+"' AND sent_date <='"+toDate+"'   " ;
			if(store !=null && !store.trim().isEmpty()){
				qry += " AND store_number = '"+store+"'";
			}
			if(zoneId !=null){
				qry += " AND zone_Id = "+zoneId;
			}
			qry += " GROUP BY  DAY(DATE_ADD(sent_date, INTERVAL "+tzOffSet+" MINUTE)) ORDER BY 2";
		}else{
			qry="SELECT COUNT(id) AS count, MONTH(DATE_ADD(sent_date, INTERVAL "+tzOffSet+" MINUTE)) AS month FROM dr_sms_channel_sent WHERE user_id ="+userId.longValue()+" " +" AND sent_date >= '"+fromDate+"' AND sent_date <='"+toDate+"'   " ;
			if(store !=null && !store.trim().isEmpty()){
				qry += " AND store_number = '"+store+"'";
			}
			qry += " GROUP BY  MONTH(DATE_ADD(sent_date, INTERVAL "+tzOffSet+" MINUTE)) ORDER BY 2";

		}
		return	jdbcTemplate.queryForList(qry);


	}//findTotSentRate()
	
	public DRSMSChannelSent findlongUrlByShortCode(String shortCode) {
		try {
			
			
			String query = "FROM DRSMSChannelSent WHERE  originalShortCode='" + shortCode + "' ORDER BY id DESC";
	    	List<DRSMSChannelSent> list = executeQuery(query, 0,1);
	    	if(list == null || list.size() <1) {
	    		
	    		return null;
	    	}
	    	return list.get(0);
			
			
			
		} catch (Exception e) {
			return null;
		}
	}
}
