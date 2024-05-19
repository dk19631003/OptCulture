package org.mq.marketer.campaign.dao;


import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.mq.marketer.campaign.beans.AutoSmsQueue;
import org.mq.marketer.campaign.general.Constants;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;


public class AutoSmsQueueDao extends AbstractSpringDao {

	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	
	public AutoSmsQueueDao() {}

	public AutoSmsQueue find(Long id) {
		return (AutoSmsQueue) super.find(AutoSmsQueue.class, id);
	}

	/*public void saveOrUpdate(AutoSmsQueue testSmsQueue) {
		super.saveOrUpdate(testSmsQueue);
	}*/

	/*public void delete(AutoSmsQueue testSmsQueue) {
		super.delete(testSmsQueue);
	}*/


	public List<AutoSmsQueue> findByStatus(String status) {
		return getHibernateTemplate().find(" from AutoSmsQueue where status= '" + status + "'");
	}

	public List<AutoSmsQueue> findByType(Long userId,String type) {
		return getHibernateTemplate().find(" from AutoSmsQueue where type='" + type + "' AND user="+ userId + " ORDER BY sentDate DESC");
	}
	public AutoSmsQueue findById(long msgId) {
		AutoSmsQueue autoSmsQueue = null;
		try {
			List<AutoSmsQueue> list = getHibernateTemplate().find(" from AutoSmsQueue where id= " +msgId );
			if (list.size() > 0) {
				autoSmsQueue = (AutoSmsQueue) list.get(0);
			}
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
		}
		return autoSmsQueue;
	}
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<Object[]> fetchRecordsByDate(Long userId, String startDate, String endDate, String type, int firstResulset, int size,String orderby_colName_keywords,String desc_Asc) {
		try{
			
			
			String test = "COUNT(CASE dlr_status WHEN 'bounced'  THEN 1 ELSE NULL END) AS bounced ";
				   test += ", COUNT(CASE dlr_status WHEN 'delivered' THEN 1 ELSE NULL END) AS delivered ";
			String query = "SELECT ct.template_name as templateName, eq.type, max(eq.sent_date) as latestSentDate, count(1) sentCount ,"+test+", eq.template_Id as temp_id , sum(IF(eq.clicks > 0, 1,0)) as clicks" +
			" FROM  auto_sms_queue eq LEFT OUTER JOIN auto_sms ct ON ct.auto_sms_id = eq.template_Id  WHERE  eq.user_id = "+ userId + (type != null ? (" AND eq.type in ("+type+") "): "")+" "
					+ "AND eq.sent_date BETWEEN '"+startDate+"' AND '"+endDate+"' AND eq.status = 'Sent' AND dlr_status IS NOT NULL ";
			
        	query += " GROUP BY eq.template_Id,eq.type order by 1 LIMIT "+firstResulset+", "+size;
    		
    		return  jdbcTemplate.query(query, new RowMapper() {
				
				 public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
					 Object[] obj = new Object[9];
		             obj[0] = rs.getString("templateName") != null ? rs.getString("templateName") : "";
		             obj[1] = rs.getString("type");
		             Calendar cal = null;
						Timestamp t=rs.getTimestamp("latestSentDate");
						if(t!=null)
						{
						if(new Date(t.getTime()) != null) {
							cal = Calendar.getInstance();
							cal.setTime(t);
						} 
						}
						
		             obj[2] = cal;
		             obj[3] = rs.getLong("sentCount");
		             obj[4] = rs.getLong("delivered");
		             obj[5] = rs.getLong("clicks");
		             obj[6] = rs.getLong("bounced");
		             obj[7] = rs.getLong("temp_id");
		             obj[8] = rs.getString("templateName");
		            
		            return obj;
			        }
			    });
    	}catch (Exception e) {
    		logger.error(" Exception :: ",e);
		}
    	return new ArrayList<Object[]>(0);
	}
	

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Object[] fetchRecordsByTemplateId(Long userId, String startDate, String endDate, String type,Long templateId) {

    	try{
    		String query = "SELECT sum(IF(eq.clicks > 0, 1,0)) as urlclicks,ct.template_name as templateName,SUM(IF(eq.dlr_status ='delivered', 1,0)) as delivered,"
    				+ "SUM(IF(eq.dlr_status ='Status Pending', 1,0)) as statusPending,"			
    				+ " SUM(IF(eq.dlr_status in('" +Constants.DR_STATUS_BOUNCED+"','"+Constants.DR_STATUS_DROPPED+"','" +Constants.DR_STATUS_BOUNCE+"'), 1,0)) as bounced,"
    				+ " eq.type, count(eq.id) sentCount , eq.template_Id as temp_id "
    				+ " FROM  auto_sms_queue eq LEFT OUTER JOIN auto_sms ct ON ct.auto_sms_id = eq.template_Id  WHERE  eq.user_id = "+ userId + (type != null ? (" AND eq.type in ('"+type+"') "): "")+"  "
					+ " AND eq.sent_date BETWEEN '"+startDate+"' AND '"+endDate+"' AND eq.status = 'Sent'"
					+ " AND dlr_status IS NOT NULL";
    		
    		
        	if(templateId == null || templateId == 0){
        		query += " AND eq.template_Id is null ";
        	}else{
        		query += " AND eq.template_Id ="+templateId+" ";
        	}
        	
        	List<Object[]> listRows =  jdbcTemplate.query(query, new RowMapper(){

				@Override
				public Object mapRow(ResultSet rs, int rowNum)throws SQLException {
					Object obj[] = new Object[7];
					obj[0] = rs.getLong("sentCount");
					obj[1] = rs.getLong("delivered");
					obj[2] = rs.getLong("bounced");
					obj[3] = 0;
					obj[4] = rs.getLong("urlclicks");
					obj[5] = rs.getLong("statusPending");
					obj[6] = 0;
					return obj;
				}
        		
        	});
        	if(listRows != null && listRows.size() > 0) return listRows.get(0);
    	}catch (Exception e) {
    		logger.error("Exception :: ",e);
    	}
    	
    	return new Integer[6];
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<Object[]> findTotalSmsReportRateByTemplateId(Long userId, boolean[] isChecked, String fromDate,
			String endDate, String tempType, Long templateId, String xaxisType, int tzOffSet) {

		try{
			String column[] = {"count(eq.id) as sentCount", "SUM(IF(eq.dlr_status ='delivered', 1,0)) as delivered","SUM(IF(eq.dlr_status in('" +Constants.DR_STATUS_BOUNCED+"','"+Constants.DR_STATUS_DROPPED+"','" +Constants.DR_STATUS_BOUNCE+"'), 1,0)) as bounced", "SUM(IF(eq.dlr_status ='Status Pending', 1,0)) as statusPending"," SUM(IF(eq.clicks > 0, 1,0)) as uniqueClicks"};
			String query ="";
			String grpBy ="";
			if(xaxisType.equalsIgnoreCase("Days")){
				query = " SELECT DAY(DATE_ADD(sent_date, INTERVAL "+tzOffSet+" MINUTE)) AS dayOrMonth ";
				for(int i=0;i<column.length;i++){
					if(isChecked[i])query += " , "+column[i];
				}
				
				grpBy = " GROUP BY  DAY(DATE_ADD(sent_date, INTERVAL "+tzOffSet+" MINUTE)) ORDER BY 1";
				
			}else{
				query = " SELECT MONTH(DATE_ADD(sent_date, INTERVAL "+tzOffSet+" MINUTE)) AS dayOrMonth ";
				for(int i=0;i<column.length;i++){
					if(isChecked[i])query += " , "+column[i];
				}
				
				grpBy = " GROUP BY  MONTH(DATE_ADD(sent_date, INTERVAL "+tzOffSet+" MINUTE)) ORDER BY 1";
			}
			
			query += " FROM  auto_sms_queue eq  WHERE eq.user_id = "+ userId+ (tempType != null ? (" AND eq.type in ('"+tempType+"') "): "")+" "
					+ " AND eq.sent_date BETWEEN '"+fromDate+"' AND '"+endDate+"' AND eq.status = 'Sent' AND dlr_status IS NOT NULL";
			
        	if(templateId == null || templateId == 0){
        		query += " AND template_Id is null ";
        	}else{
        		query += " AND template_Id="+templateId+" ";
        	}
			 
        	query += grpBy;
        	
        	return jdbcTemplate.query(query , new RowMapper(){

				@Override
				public Object mapRow(ResultSet rs, int rowNum)	throws SQLException {
					ResultSetMetaData resultSetMetaData= rs.getMetaData();
					int columnCount = resultSetMetaData.getColumnCount();
					
					Object obj[] = new Object[columnCount];
					for(int i=1;i<=columnCount;i++){
						obj[i-1]=rs.getLong(i);
					}
					return obj;
				}
        		
        	});
        	
		}catch (Exception e) {
			logger.error("Exception :: ",e);
		}

		return new ArrayList<Object[]>(0);
	
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<AutoSmsQueue> getRowsByTemplateId(Long userId, Long tempId, String type, String fromDate, String endDate, int firstResultset, int size, String key, String emailSatusTobeFetched) {
		try{
			List<AutoSmsQueue> list = null;
			String openClicksQry="";
			String query = "SELECT sent_date, to_Mobile_No, dlr_status, id ,IF(clicks > 0, 1,0) as clicks FROM auto_sms_queue " +
					" WHERE user_id="+userId+" AND type='"+type+"'  AND sent_date BETWEEN '"+fromDate+"' AND '"+endDate+"' AND status='Sent'";

			query += " AND dlr_status IS NOT NULL";


			if(tempId ==null || tempId == 0){
				query += " AND template_Id is NULL ";
			}else{
				query += " AND template_Id="+tempId+" ";
			}
			if(key != null){
				query += " AND to_Mobile_No like '%"+key+"%'";
			}

			if(!emailSatusTobeFetched.contains("status_sent")) {
			
				if(emailSatusTobeFetched.contains("special_condtion_for_all") ){
					query += "  AND dlr_status IN("+emailSatusTobeFetched+")  ";
				}
	
				if(((emailSatusTobeFetched.contains("dropped") || emailSatusTobeFetched.contains("bounce")) && ! emailSatusTobeFetched.contains("special_condtion_for_all"))){ 
					
					if( !emailSatusTobeFetched.contains("Success") && !emailSatusTobeFetched.contains("spamreport")){
						query += "  AND (dlr_status='bounce' OR dlr_status='bounced' OR dlr_status='dropped')  ";
					}else {
						query += "  AND (dlr_status='bounce' OR dlr_status='bounced' OR dlr_status='dropped')   AND  (dlr_status='spamreport' OR dlr_status='Success' OR dlr_status='delivered')   ";
					}
					
				}
				
				if(emailSatusTobeFetched.contains("Status Pending") && ! emailSatusTobeFetched.contains("special_condtion_for_all") ){
					query += "  AND dlr_status = 'Status Pending'";
				}
				
				if(emailSatusTobeFetched.contains("_fetch_clicks_also_") && ! emailSatusTobeFetched.contains("special_condtion_for_all")){
					openClicksQry += " AND clicks is not null AND clicks !=0  ";
				}
			}
			
			query += openClicksQry;
			
			if(firstResultset != -1){
				query += " ORDER BY sent_date DESC   LIMIT "+" "+firstResultset+","+size ;
			}else{
				query += " ORDER BY sent_date DESC  ";
			}

			logger.info("query >>> "+query);
			list = jdbcTemplate.query(query, new RowMapper() {

				public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
					AutoSmsQueue smsQueue = new AutoSmsQueue();
					smsQueue.setId(rs.getLong("id"));
					smsQueue.setToMobileNo(rs.getString("to_Mobile_No"));
					Calendar cal = Calendar.getInstance();
					cal.setTime(rs.getTimestamp("sent_date"));
					smsQueue.setSentDate(cal);
					smsQueue.setDlrStatus( rs.getString("dlr_status"));
					smsQueue.setClicks(rs.getInt("clicks"));
					return smsQueue;
				}
			});

			if(list!= null && list.size()>0) {
				return list;
			}
			return new ArrayList<AutoSmsQueue>(0);
		}catch(Exception e) {
			logger.error("Exception ::" , e);
			return new ArrayList<AutoSmsQueue>(0);
		}
	}
	
	public AutoSmsQueue findByMrIdAndMobile(String id, String mobile) {

		  logger.info(""+mobile);
		  if(mobile.startsWith("91")&& mobile.length() == 12) {
				mobile = mobile.substring(2);
		  }
			//String query = "FROM AutoSmsQueue where messageId='"+mrId+"' AND toMobileNo like '%"+mobile+"%'";
		  String query = "FROM AutoSmsQueue where id='"+id+"' AND toMobileNo like '%"+mobile+"%'";
			logger.info("query--"+query);
			try{
				List<AutoSmsQueue> retAutoSMSQueueList =  getHibernateTemplate().find(query);
				if(retAutoSMSQueueList != null && retAutoSMSQueueList.size() > 0) {
					
					return retAutoSMSQueueList.get(0);
				}
				}catch(Exception e){
					logger.error("Exception ",e);
				}
			
			return null;
		}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public int getClickCountByTempId(Long userId, Long tempId, String type, String key) {


		List<Object[]> list = null;
		
		try{
			String query = "SELECT ac.originalUrl as url, sq.clicks as totalClicks, IF(sq.clicks > 0, 1,0) as uniqueClicks  "
					+ "FROM Autosms_url ac INNER JOIN  auto_sms_queue sq ON ac.auto_SmsQueue_SentId=sq.id " +
					" WHERE sq.user_id=" + userId +" AND sq.type ='"+type+"' AND dlr_status IS NOT NULL";
					
					if(tempId ==null || tempId == 0){
						query += " AND template_Id is NULL ";
					}else{
						query += " AND template_Id="+tempId+" ";
					}
					
					if(key != null){
						query += " AND click_url like '%"+key+"%'";
					}
					
					query += " GROUP BY auto_SmsQueue_SentId ";
					
					list = jdbcTemplate.query(query, new RowMapper(){
						
						@Override
						public Object mapRow(ResultSet rs, int arg1)
								throws SQLException {
							Object obj [] = new Object[3];
							obj[0] = rs.getString("url");
							obj[1] = rs.getInt("uniqueClicks");
							obj[2] = rs.getInt("totalClicks");
							return obj;
						}
						
					});
		}catch (Exception e) {
			logger.error("Exception :: ",e);
		}
		return list.size();
	
	
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<Object[]> getClicksByTempId(Long userId, Long tempId, String type, String key, int startIndex,int size) {

		List<Object[]> list = null;
		
		try{
			String query = "SELECT ac.originalUrl as url, sq.clicks as totalClicks, IF(sq.clicks > 0, 1,0) as uniqueClicks  "
					+ "FROM Autosms_url ac INNER JOIN  auto_sms_queue sq ON ac.auto_SmsQueue_SentId=sq.id " +
					" WHERE sq.user_id=" + userId +" AND sq.type ='"+type+"' AND dlr_status IS NOT NULL";
					
					if(tempId ==null || tempId == 0){
						query += " AND template_Id is NULL ";
					}else{
						query += " AND template_Id="+tempId+" ";
					}
					
					if(key != null){
						query += " AND click_url like '%"+key+"%'";
					}
					
					query += " GROUP BY auto_SmsQueue_SentId LIMIT "+startIndex+" , "+size;
					
					list = jdbcTemplate.query(query, new RowMapper(){
						
						@Override
						public Object mapRow(ResultSet rs, int arg1)
								throws SQLException {
							Object obj [] = new Object[3];
							obj[0] = rs.getString("url");
							obj[1] = rs.getInt("uniqueClicks");
							obj[2] = rs.getInt("totalClicks");
							return obj;
						}
						
					});
		}catch (Exception e) {
			logger.error("Exception :: ",e);
		}
		return list;
	
	}
	
}
