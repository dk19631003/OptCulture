package org.mq.captiway.scheduler.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;

import org.mq.captiway.scheduler.beans.AutoProgram;
import org.mq.captiway.scheduler.beans.SMSDeliveryReport;
import org.mq.captiway.scheduler.utility.MyCalendar;
import org.springframework.jdbc.core.RowMapper;

public class AutoProgramDao extends AbstractSpringDao{

	
	
	
	
	public List<AutoProgram> getActivePrograms(String currentDateStr) {
		
		
		return getHibernateTemplate().find(" FROM AutoProgram WHERE " +
				" createdDate <= '"+currentDateStr+"' AND status = 'Active'");
		
		
	}
	
	/**
	 * 
		
		String query = "select * from sms_delivery_report where DATEDIFF(now(),req_generated_date) >= 1 AND status = 'Active' ";
		List<SMSDeliveryReport> activeSmsDlrRepList = null;
		
		try{
			activeSmsDlrRepList = jdbcTemplate.query(query, new RowMapper<SMSDeliveryReport>(){
				SMSDeliveryReport smsDeliveryReport=null;
			
				public SMSDeliveryReport mapRow(ResultSet rs, int rowNum) throws SQLException {

					smsDeliveryReport = new SMSDeliveryReport();
					smsDeliveryReport.setSmsDlrId(rs.getLong("sms_dlr_id"));
					smsDeliveryReport.setRequestId(rs.getString("request_id"));
					smsDeliveryReport.setSmsCampRepId(rs.getLong("sms_camp_rep_id"));
					smsDeliveryReport.setStatus(rs.getString("status"));
					Calendar cal = Calendar.getInstance();
					cal.setTime((rs.getDate("req_generated_date")));
					smsDeliveryReport.setReqGeneratedDate(cal);
				
					return smsDeliveryReport;
				
				}
				
			});
			
			return activeSmsDlrRepList;
			
			
		}catch (Exception e) {
			// TODO: handle exception
			logger.error("Exception ::::" , e);
			return null;
		}
		
	
	 */
	
}
