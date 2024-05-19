package org.mq.optculture.data.dao;

import java.util.Calendar;
import java.util.Date;

import org.codehaus.jackson.map.deser.std.CalendarDeserializer;
import org.mq.marketer.campaign.beans.ValueCodes;
import org.mq.marketer.campaign.dao.AbstractSpringDaoForDML;
import org.springframework.jdbc.core.JdbcTemplate;

public class ValueCodesDaoForDML extends AbstractSpringDaoForDML {

	public ValueCodesDaoForDML() {
	}
	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	
	public void saveOrUpdate(ValueCodes valueCodes) {
		super.saveOrUpdate(valueCodes);
	}
	
	public void updateValueCodeById(Long Id,String Description,String ModifiedDate,Long ModifiedBy) throws Exception{
		
		String qry = "UPDATE ValueCodes SET Description='"+Description+"',ModifiedDate='"+ModifiedDate+"',ModifiedBy='"+ModifiedBy+"' WHERE Id="+Id;
		
		executeUpdate(qry);


	}

	

}
