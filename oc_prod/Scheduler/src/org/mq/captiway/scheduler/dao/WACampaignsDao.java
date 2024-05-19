package org.mq.captiway.scheduler.dao;

import java.util.List;

import org.mq.captiway.scheduler.beans.WACampaign;
import org.springframework.jdbc.core.JdbcTemplate;


public class WACampaignsDao extends AbstractSpringDao {

	public WACampaignsDao(){}

	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}


	public List findAll() {
		return super.findAll(WACampaign.class);
	}

	public WACampaign findByCampaignId(Long waCampaignId) {

		List<WACampaign> campList =  getHibernateTemplate().find(
				" FROM WACampaign WHERE waCampaignId = "+waCampaignId);

		if(campList == null || campList.size() == 0) {
			return null;
		}
		else {
			return campList.get(0);
		}
	}

}
