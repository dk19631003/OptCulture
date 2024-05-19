package org.mq.marketer.campaign.dao;


import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.mq.marketer.campaign.beans.CountryCodes;

import org.springframework.jdbc.core.JdbcTemplate;

public class CountryCodeDao extends AbstractSpringDao {
	
	
private JdbcTemplate jdbcTemplate;
	
    public CountryCodeDao(){}

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public Properties findAllCountryCodes() {
	 	   
		 	   
		 	  List tempList  =  executeQuery("FROM CountryCodes");
		 	  Iterator itr = tempList.iterator();
		 	  Properties props = new Properties();
		 	  
		 	  
		 	   while(itr.hasNext()){
		 		   CountryCodes cCode = (CountryCodes)itr.next();
		 		   props.put(cCode.getCountryName(), cCode.getCallingCode());
		 	   }
		 	   
		 	  return props; 
		    }

}
