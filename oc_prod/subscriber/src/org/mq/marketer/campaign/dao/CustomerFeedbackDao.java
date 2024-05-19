package org.mq.marketer.campaign.dao;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.CustomerFeedback;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.general.Constants;
import org.springframework.jdbc.core.JdbcTemplate;

public class CustomerFeedbackDao extends AbstractSpringDao{
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	private JdbcTemplate jdbcTemplate;
	 
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	public CustomerFeedback find(Long id) {
		return (CustomerFeedback) super.find(CustomerFeedback.class, id);
	}

	@SuppressWarnings("unchecked")
	public CustomerFeedback findFeedbackByDocSidCustIdandUserId(String dOCSID, String customerNo, Long userId) {
		try {
		List<CustomerFeedback> list = null;
		String query = "FROM CustomerFeedback  WHERE userId ='"+userId.longValue()+"' and customerNo='"+customerNo+"' and DOCSID='"+dOCSID+"' ORDER BY feedBackId DESC ";
		list  =  executeQuery(query);
		if(list!= null && list.size()>0) {
			return list.get(0);
		}
		}catch (Exception e) {
			logger.error("Exception ::"+e);
		}
		return null;
	}

	public CustomerFeedback findFeedbackByUserIdandContactId(Long userId,Long contactId,String source){
		try {
		List<CustomerFeedback> list = null;
		Calendar thirtyDaysAgo = Calendar.getInstance(); 
        thirtyDaysAgo.add(Calendar.DAY_OF_MONTH, -30);
        Date thirtyDaysAgoDate = thirtyDaysAgo.getTime();
        DateFormat targetFormat = new SimpleDateFormat(MyCalendar.FORMAT_DATETIME_STYEAR);
        String formattedDate = targetFormat.format(thirtyDaysAgoDate);
        
        String query = "FROM CustomerFeedback  WHERE userId ='"+userId.longValue()+"' and contactId='"+contactId+"' and  source like '%"+source+"%' and  createdDate  >= '"+formattedDate+"'";
		logger.info("query for findFeedbackByUserIdandContactId "+query);
		list  =  executeQuery(query);
		logger.info("Listsize for findFeedbackByUserIdandContactId "+list.size());
		if(list!= null && list.size()>1) {
			return list.get(0);
		}
		}catch (Exception e) {
			logger.error("Exception ::"+e);
		}
		return null;
	}

	
	
}
