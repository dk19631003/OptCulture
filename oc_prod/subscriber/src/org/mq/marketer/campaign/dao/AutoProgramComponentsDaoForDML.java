package org.mq.marketer.campaign.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import org.mq.marketer.campaign.beans.AutoProgramComponents;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

public class AutoProgramComponentsDaoForDML extends AbstractSpringDaoForDML {

	
	public AutoProgramComponentsDaoForDML() {}
	
	public void saveOrUpdate(AutoProgramComponents autoProgramComponents) {
		super.saveOrUpdate(autoProgramComponents);
	}
	
	
	private JdbcTemplate jdbcTemplate;
	
	
	
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

/*	public Long getIdByWinId(String windowId, Long programId) {
		//logger.info("program_id got is====================>"+programId);
		return (Long)getHibernateTemplate().find("select compId from AutoProgramComponents where " +
				"componentWinId='"+windowId+"' and autoProgram="+programId).get(0);
				
		
		
	}

	
	
	
public List<AutoProgramComponents> getProgramComponents(Long programId) {
		
		List<AutoProgramComponents> autoProgramComponentsList = null;
		String queryString ="FROM AutoProgramComponents where autoProgram="+programId;
		autoProgramComponentsList = getHibernateTemplate().find(queryString);
		
		return autoProgramComponentsList;
		
		
		
		
	}*/


public void deleteByCollection(Collection list){
	getHibernateTemplate().deleteAll(list);
}
	

public void deleteByProgramId(Long programId) {
	
	String qry = "DELETE FROM AutoProgramComponents WHERE autoProgram="+programId;
	
	executeUpdate(qry);
	
	
	
	
	
}

/*

public List<String> findConfiguredComponents(String campaignIds) {
	

	try {
		logger.debug(" Email String :" + campaignIds);
		String queryStr = "SELECT distinct support_id FROM auto_program_components where support_id in " +
							"(" +campaignIds+ ") AND comp_type='ACTIVITY_SEND_EMAIL'";
		logger.debug("JdbcTemplate : " + jdbcTemplate);
		List<String> list = jdbcTemplate.query(queryStr, new RowMapper(){

			@Override
			public Object mapRow(ResultSet arg0, int arg1)
					throws SQLException {
				logger.debug("arg0 :" + arg0 + " arg1 :" + arg1);
				return arg0!=null?arg0.getString("support_id"):"";
			} });
		
		return list;
	} catch (DataAccessException e) {
		// TODO Auto-generated catch block
		logger.error("Exception ::" , e);
		return null;
	}

	
}


public List<String> findConfiguredSMSComponents(String smsCampaignIds) {
	

	try {
		logger.debug(" Email String :" + smsCampaignIds);
		String queryStr = "SELECT distinct support_id FROM auto_program_components where support_id in " +
							"(" +smsCampaignIds+ ") AND comp_type='ACTIVITY_SEND_SMS'";
		logger.debug("JdbcTemplate : " + jdbcTemplate);
		List<String> list = jdbcTemplate.query(queryStr, new RowMapper(){

			@Override
			public Object mapRow(ResultSet arg0, int arg1)
					throws SQLException {
				logger.debug("arg0 :" + arg0 + " arg1 :" + arg1);
				return arg0!=null?arg0.getString("support_id"):"";
			} });
		
		return list;
	} catch (DataAccessException e) {
		// TODO Auto-generated catch block
		logger.error("Exception ::" , e);
		return null;
	}

	
}*/
	
}
