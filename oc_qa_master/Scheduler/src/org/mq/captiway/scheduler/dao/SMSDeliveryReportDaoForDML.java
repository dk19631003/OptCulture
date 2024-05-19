package org.mq.captiway.scheduler.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.mq.captiway.scheduler.beans.Contacts;
import org.mq.captiway.scheduler.beans.SMSCampaignReport;
import org.mq.captiway.scheduler.beans.SMSCampaignSchedule;
import org.mq.captiway.scheduler.beans.SMSDeliveryReport;
import org.mq.captiway.scheduler.utility.Constants;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.utility.Constants;

public class SMSDeliveryReportDaoForDML extends AbstractSpringDaoForDML{

	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	private JdbcTemplate jdbcTemplate;
	
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}
	
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public SMSDeliveryReportDaoForDML() {
		// TODO Auto-generated constructor stub
	}
	
	public void saveOrUpdate(SMSDeliveryReport smsDeliveryReport) {
        super.saveOrUpdate(smsDeliveryReport);
    }

	
	public void saveByCollection(Collection<SMSDeliveryReport> campList) {
		super.saveOrUpdateAll(campList);
	}
	
	/**
	 * this method called by the smsCampaignScheduler to check the Active object(for which we need to fetch the reports)
	 * @param currentDateStr
	 * @return
	 */
	
	/*public List<SMSDeliveryReport> getActiveList(String currentDateStr) {
		
		//String query = "select * from sms_delivery_report where DATEDIFF(now(),req_generated_date) >= 1 AND status = 'Active' ";
		
		String query = "SELECT * from sms_delivery_report where TIMESTAMPDIFF(MINUTE,req_generated_date,now()) >=7 AND status='"+Constants.CR_DLVR_STATUS_ACTIVE+"'";
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
					//smsDeliveryReport.setIsTransactional(rs.getString("is_transactional"));
					smsDeliveryReport.setUserSMSTool(rs.getLong("user_sms_tool"));
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
		
	}//getActiveList
	
	
public List<SMSDeliveryReport> getExpiredList(String currentDateStr) {
		
		//String query = "select * from sms_delivery_report where DATEDIFF(now(),req_generated_date) >= 1 AND status = 'Active' ";
		
		String query = "SELECT * from sms_delivery_report where TIMESTAMPDIFF(DAY,req_generated_date,now()) >=2 AND status='"+Constants.CR_DLVR_STATUS_ACTIVE+"'";
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
					//smsDeliveryReport.setIsTransactional(rs.getString("is_transactional"));
					smsDeliveryReport.setUserSMSTool(rs.getLong("user_sms_tool"));
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
		
	}//getActiveList
*/	
	
	
}//class

