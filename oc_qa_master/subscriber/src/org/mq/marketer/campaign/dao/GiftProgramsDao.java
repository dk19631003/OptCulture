package org.mq.marketer.campaign.dao;

import java.io.Serializable;
import java.util.List;

import org.mq.marketer.campaign.beans.GiftPrograms;
import org.springframework.jdbc.core.JdbcTemplate;

public class GiftProgramsDao extends AbstractSpringDao implements Serializable{
	
	public GiftProgramsDao() {}
	
	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	public GiftPrograms findByUserIDAndProgramId(Long userId, Long programId) {
		
		String query = "FROM GiftPrograms WHERE userId="+userId+" AND giftProgramId="+programId+" ";
		logger.info("program by id query : "+query);
		List<GiftPrograms> listOfPrgms =  getHibernateTemplate().find(query);
		
		if(listOfPrgms.size()>0) return listOfPrgms.get(0);
		else return null;
	}

}
