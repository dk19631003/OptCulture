package org.mq.captiway.scheduler.dao;

import java.util.List;

import org.mq.captiway.scheduler.beans.AutosmsSenturlShortcode;
import org.mq.optculture.exception.BaseServiceException;
import org.springframework.jdbc.core.JdbcTemplate;

public class AutosmsSenturlShortcodeDao extends AbstractSpringDao{
	
	public AutosmsSenturlShortcodeDao() {
	}
	
	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}	

@SuppressWarnings("unchecked")
public AutosmsSenturlShortcode findByShortCode(String shortCode) throws BaseServiceException{
		try {
			String qry = "FROM AutosmsSenturlShortcode WHERE generatedShortCode='"+shortCode+"'";
			List<AutosmsSenturlShortcode> retList = executeQuery(qry);
				if(retList != null && retList.size() > 0) return retList.get(0);
				return null;
		} catch (Exception e) {
			throw new BaseServiceException("Exception while getting the required object by code "+shortCode);
		}
		
	}
}
