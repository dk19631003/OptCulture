package org.mq.marketer.campaign.dao;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.mq.marketer.campaign.beans.DRSent;
import org.mq.marketer.campaign.beans.EmailQueue;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.general.Constants;
import org.mq.optculture.utils.OCConstants;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

@SuppressWarnings({ "unchecked", "serial","unused"})
public class EmailQueueDaoForDML extends AbstractSpringDaoForDML {
	
	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

    public EmailQueueDaoForDML() {}
	
  /*  public EmailQueue find(Long id) {
        return (EmailQueue) super.find(EmailQueue.class, id);
    }*/

    public void saveOrUpdate(EmailQueue testEmailQueue) {
        super.saveOrUpdate(testEmailQueue);
    }

    public void delete(EmailQueue testEmailQueue) {
        super.delete(testEmailQueue);
    }

    /*public List findAll() {
        return super.findAll(EmailQueue.class);
    }

    public List findByStatus(String status){
    	return getHibernateTemplate().find(" from EmailQueue where status= '" + status + "'");
    }
    
    public List findByType(Long userId,String type){
    	return getHibernateTemplate().find(" from EmailQueue where type='" + type + "' AND user="+ userId + " ORDER BY sentDate DESC");
    }
    public EmailQueue findEqById(long msgId){
    	EmailQueue emailQueue = null;
    	try {
   
			List list = getHibernateTemplate().find(" from EmailQueue where id= " +msgId );
			if (list.size() > 0) {
				emailQueue = (EmailQueue) list.get(0);
			}
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
		}
    	return emailQueue;
    }
    
    public int findCountByDate(Long userId, String startDate, String endDate, String type){
    	try{
    		String query = "SELECT COUNT(type) FROM EmailQueue WHERE user = "+ userId +" AND status = 'Sent' AND sentDate BETWEEN '"+startDate+"' AND '"+endDate+"' ";
    	
    	if(type != null){
    		query += " AND type in ("+type+") ";
    		
    	}
    	query += " GROUP BY type, customTemplates ";
    	return  getHibernateTemplate().find(query).size();
    	}catch (Exception e) {
    		logger.error(" Exception :: ",e);
    	}
    	return 0;
    }
    
    public List<Object[]> fetchRecordsByDate(Long userId, String startDate, String endDate, String type, int firstResulset, int size,String orderby_colName_keywords,String desc_Asc){
    	try{
    		String query = "SELECT eq.cust_temp_name as templateName, eq.type, max(eq.sent_date) as latestSentDate, count(1) sentCount, SUM(IF(eq.delivery_status ='Success', 1,0)) as delivered, SUM(IF(eq.opens >0, 1,0)) as uniopens,SUM(IF(eq.clicks >0, 1,0)) as uniclicks, eq.cust_temp_id, ct.template_name " +
    				" FROM  email_queue eq LEFT OUTER JOIN custom_templates ct ON ct.template_id = eq.cust_temp_id  WHERE  eq.user_id = "+ userId +" AND eq.status = 'Sent' AND eq.sent_date BETWEEN '"+startDate+"' AND '"+endDate+"' ";
    		if(type != null){
        		query += " AND eq.type in ("+type+") ";
        		
        	}
        	query += " GROUP BY eq.type, eq.cust_temp_id order by "+orderby_colName_keywords+" "+desc_Asc+" LIMIT "+firstResulset+", "+size;
    		
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
			             obj[5] = rs.getLong("uniopens");
			             obj[6] = rs.getLong("uniclicks");
			             obj[7] = rs.getLong("cust_temp_id");
			             obj[8] = rs.getString("template_name");
			            
			            return obj;
			        }
			    });
    	}catch (Exception e) {
    		logger.error(" Exception :: ",e);
		}
    	return new ArrayList<Object[]>(0);
    }
    
    public Object[] findConsolidatedReportByTemplateId(Long userId, String startDate, String endDate, String type, Long templateId){
    	try{
    		String query = "SELECT count(eq.id) as sentCount, SUM(IF(eq.delivery_status ='Success', 1,0)) as delivered, SUM(IF(eq.delivery_status in('" +Constants.DR_STATUS_BOUNCED+"','"+Constants.DR_STATUS_DROPPED+"','" +Constants.DR_STATUS_BOUNCE+"'), 1,0)) as bounced, " +
    				" SUM(IF(eq.opens >0, 1,0)) as uniopens,SUM(IF(eq.clicks >0, 1,0)) as uniclicks, SUM(IF(eq.delivery_status in ('" +Constants.DR_STATUS_SPAMMED+"' ,'" +Constants.DR_STATUS_SPAME+"'), 1,0)) as spammed " +
    				" FROM  email_queue eq WHERE eq.user_id = "+ userId +" AND eq.status = 'Sent' AND eq.sent_date BETWEEN '"+startDate+"' AND '"+endDate+"' ";
    		if(type != null){
        		query += " AND type in ('"+type+"') ";
        		
        	}
        	if(templateId == null || templateId == 0){
        		query += " AND cust_temp_id is null ";
        	}else{
        		query += " AND cust_temp_id="+templateId+" ";
        	}
        	
        	List<Object[]> listRows =  jdbcTemplate.query(query, new RowMapper(){

				@Override
				public Object mapRow(ResultSet rs, int rowNum)throws SQLException {
					Object obj[] = new Object[6];
					obj[0] = rs.getLong("sentCount");
					obj[1] = rs.getLong("delivered");
					obj[2] = rs.getLong("bounced");
					obj[3] = rs.getLong("uniopens");
					obj[4] = rs.getLong("uniclicks");
					obj[5] = rs.getLong("spammed");
					return obj;
				}
        		
        	});
        	if(listRows != null && listRows.size() > 0) return listRows.get(0);
    	}catch (Exception e) {
    		logger.error("Exception :: ",e);
    	}
    	
    	return new Integer[6];
    }
    public int findCountByTemplateId(Long userId, String startDate, String endDate, String type, Long templateId){
    	int size = 0;
    	try{
    		String query = "SELECT COUNT(id) FROM EmailQueue WHERE user = "+ userId +" AND status = 'Sent' AND sentDate BETWEEN '"+startDate+"' AND '"+endDate+"' ";
        	
        	if(type != null ){
        		query += " AND type in ('"+type+"') ";
        		
        	}
        	if(templateId == null || templateId == 0){
        		query += " AND customTemplates is null ";
        	}else{
        		query += " AND customTemplates="+templateId+" ";
        	}
    		size= (Integer) getHibernateTemplate().find(query).get(0);
    	}catch (Exception e) {
    		logger.error(" Exception :: ",e);
    	}
    	return size;
    	
    }

	public List<Object[]> findTotalReportRateByTemplateId(Long userId, boolean[] isChecked, String fromDate, String endDate, String tempType, Long templateId,String xaxisType,int tzOffSet) {
		try{
			count(eq.id) as sentCount, SUM(IF(eq.delivery_status ='Success', 1,0)) as delivered, SUM(IF(eq.delivery_status ='Bounced', 1,0)) as bounced, " +
			" SUM(IF(eq.opens >0, 1,0)) as uniopens,SUM(IF(eq.clicks >0, 1,0)) as uniclicks, SUM(IF(eq.delivery_status ='Spammed', 1,0)) as spammed " +
			" FROM  email_queue eq LEFT JOIN custom_templates ct on eq.cust_temp_id=ct.template_id WHERE eq.user_id = "+ userId +" AND eq.status = 'Sent' AND eq.sent_date BETWEEN '"+startDate+"' AND '"+endDate+"' ";
			String column[] = {"count(eq.id) as sentCount", "SUM(IF(eq.delivery_status ='Success', 1,0)) as delivered","SUM(IF(eq.delivery_status in('" +Constants.DR_STATUS_BOUNCED+"','"+Constants.DR_STATUS_DROPPED+"','" +Constants.DR_STATUS_BOUNCE+"'), 1,0)) as bounced",
								"SUM(IF(eq.opens >0, 1,0)) as uniopens","SUM(IF(eq.clicks >0, 1,0)) as uniclicks"};
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
			
			query += " FROM  email_queue eq  WHERE eq.user_id = "+ userId +" AND eq.status = 'Sent' AND eq.sent_date BETWEEN '"+fromDate+"' AND '"+endDate+"' ";
			
			if(tempType != null){
        		query += " AND type in ('"+tempType+"') ";
        		
        	}
        	if(templateId == null || templateId == 0){
        		query += " AND cust_temp_id is null ";
        	}else{
        		query += " AND cust_temp_id="+templateId+" ";
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

	public List<EmailQueue> getRowsByTemplateId(Long userId, Long tempId, String type, String fromDate, String endDate, int firstResultset, int size, String key, String emailSatusTobeFetched) {
		
		try{
			String query = "FROM EmailQueue WHERE user="+userId+" AND type='"+type+"' AND status='Sent' AND sentDate BETWEEN '"+fromDate+"' AND '"+endDate+"' ";
			if(tempId ==null || tempId == 0){
				query += " AND customTemplates is NULL ";
			}else{
				query += " AND customTemplates="+tempId+" ";
			}
			if(key != null){
				query += " AND toEmailId like '%"+key+"%'";
			}
			
			
			
			if(! emailSatusTobeFetched.contains("special_condtion_for_all") ){
				query += "  AND deliveryStatus IN("+emailSatusTobeFetched+")  ";
			}
			query += " ORDER BY sentDate DESC ";
			return executeQuery(query, firstResultset, size);
		}catch (Exception e) {
			logger.info("Exception :: ",e);
		}
		return new ArrayList<EmailQueue>(0);
		
		
		
		
		
		try{



			List<EmailQueue> list = null;
			String openClicksQry="";


			String query = "SELECT sent_date, to_email_id, child_email, delivery_status, opens, clicks, id FROM email_queue " +
					" WHERE user_id="+userId+" AND type='"+type+"' AND status='Sent' AND sent_date BETWEEN '"+fromDate+"' AND '"+endDate+"' ";




			if(tempId ==null || tempId == 0){
				query += " AND cust_temp_id is NULL ";
			}else{
				query += " AND cust_temp_id="+tempId+" ";
			}
			if(key != null){
				query += " AND to_email_id like '%"+key+"%'";
			}



			if(! emailSatusTobeFetched.contains("special_condtion_for_all") ){
				query += "  AND delivery_status IN("+emailSatusTobeFetched+")  ";
			}



			
			//emailSatusTobeFetched = "'Success','spamreport','dropped','bounce','special_condtion_for_all'";
			
			
			
			if(((emailSatusTobeFetched.contains("dropped") || emailSatusTobeFetched.contains("bounce")) && ! emailSatusTobeFetched.contains("special_condtion_for_all"))){ 
				
				if( !emailSatusTobeFetched.contains("Success") && !emailSatusTobeFetched.contains("spamreport")){
					query += "  AND (delivery_status='bounce' OR delivery_status='dropped')  ";
				}else {
					query += "  AND (delivery_status='bounce' OR delivery_status='dropped')   AND  (delivery_status='spamreport' OR delivery_status='Success')   ";
				}
				
			}
			
			
			
			
			
			
			
			
			


			if(emailSatusTobeFetched.contains("_fetch_clicks_also_") && !emailSatusTobeFetched.contains("_fetch_opens_also_") && ! emailSatusTobeFetched.contains("special_condtion_for_all")){
				openClicksQry += " AND clicks is not null AND clicks !=0  ";
				
			}
			else if(emailSatusTobeFetched.contains("_fetch_opens_also_") && !emailSatusTobeFetched.contains("_fetch_clicks_also_") && ! emailSatusTobeFetched.contains("special_condtion_for_all")){
				openClicksQry += " AND opens is not null AND opens !=0  ";
			}
			else if(emailSatusTobeFetched.contains("_fetch_opens_also_") && emailSatusTobeFetched.contains("_fetch_clicks_also_") && ! emailSatusTobeFetched.contains("special_condtion_for_all")){
				openClicksQry += " AND ((opens is not null AND opens !=0)   AND (clicks is not null AND clicks !=0))   ";
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

					EmailQueue emailQueue = new EmailQueue();

					emailQueue.setId(rs.getLong("id"));

					emailQueue.setToEmailId(rs.getString("to_email_id"));
					emailQueue.setChildEmail(rs.getString("child_email"));



					Calendar cal = Calendar.getInstance();
					cal.setTime(rs.getTimestamp("sent_date"));
					emailQueue.setSentDate(cal);



					emailQueue.setDeliveryStatus( rs.getString("delivery_status"));


					emailQueue.setOpens( rs.getInt("opens"));
					emailQueue.setClicks( rs.getInt("clicks"));

					return emailQueue;
				}
			});
			//list = getHibernateTemplate().find(query);
			//list = executeQuery(query, start, end);

			if(list!= null && list.size()>0) {
				return list;
			}

			return new ArrayList<EmailQueue>(0);



		}catch(Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
			return new ArrayList<EmailQueue>(0);
		}
		
		
		
		
		
		
	}

	public int getSizeByTemplateId(Long userId, Long tempId, String type,
			String fromDate, String endDate, String key) {
		
			try{
				String query = "SELECT COUNT(id) FROM EmailQueue WHERE user="+userId+" AND type='"+type+"' AND status='Sent' AND sentDate BETWEEN '"+fromDate+"' AND '"+endDate+"' ";
				if(tempId !=null  && tempId != 0){
					query += " AND customTemplates="+tempId+" ";
				}else{
					query += " AND customTemplates is NULL ";
				}
				if(key != null){
					query += " AND toEmailId like '%"+key+"%'";
				}
				query += " ORDER BY sentDate DESC ";
				return ((Long)getHibernateTemplate().find(query).get(0)).intValue();
		}catch (Exception e) {
			logger.error("Exception :: ",e);
		}
		return 0;
	}*/

	public int updateEmailQueue(String autoEmailType, Long eqId) {
		
		try{
			String query = "UPDATE email_queue SET ";
			if(autoEmailType.equalsIgnoreCase("opens")){
				query += " opens = IF(opens is null, 0, opens) + 1";
				
			}else if(autoEmailType.equalsIgnoreCase("clicks")){
				query += " clicks = IF(clicks is null, 0, clicks) + 1";
			}
			
			query += " WHERE id ="+eqId;
			
		return	jdbcTemplate.update(query);
		}catch (Exception e) {
			logger.error("Exception while updating autoemail report :: ",e);
		}
		return 0;
	}

/*public boolean isMailSentToday(String type, Long userId, int timezoneDiffrenceMinutesInt) {
    	
    	try {
			logger.debug("inside is mail sent today");
			String qry = "FROM EmailQueue WHERE DATE(sentDate) = Date(now()) and  type = '" + type + "' AND status IN('Sent','Active') AND user = " + userId ;
			
			List<EmailQueue> list = getHibernateTemplate().find(qry);
			if(list != null && list.size() > 0) {
	    		 return true;
	    	 }
	    	 else {
	    		 return false;
	    	 }
			String sent_date = "sent_date + INTERVAL "+timezoneDiffrenceMinutesInt+" MINUTE";
			String manipulatedServerTime = "now() + INTERVAL "+timezoneDiffrenceMinutesInt+" MINUTE";
			
			long user_id = userId;
			String qry = "select * FROM email_queue WHERE DATE("+sent_date+") = Date("+manipulatedServerTime+") and  type = '" + type + "' AND status IN('Sent','Active') AND user_id = " + user_id ;
			
			logger.info("qry >>>>>>>>>>>>>> "+qry);
			List<Map<String, Object>> list = jdbcTemplate.queryForList(qry);
			
			if(list != null && list.size() > 0) {
	    		 return true;
	    	 }
	    	 else {
	    		 return false;
	    	 }
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("exception ", e);
			e.printStackTrace();
			return true;
		}
    	
   }*/

}
