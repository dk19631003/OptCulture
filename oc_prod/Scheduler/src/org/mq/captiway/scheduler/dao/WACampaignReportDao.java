package org.mq.captiway.scheduler.dao;

import java.util.List;

import org.mq.captiway.scheduler.beans.WACampaignReport;
import org.springframework.jdbc.core.JdbcTemplate;

public class WACampaignReportDao extends AbstractSpringDao {

	public WACampaignReportDao() {}

	private JdbcTemplate jdbcTemplate;
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public WACampaignReport find(Long id) {
		return (WACampaignReport) super.find(WACampaignReport.class, id);
	}

	public List findAll() {
		return super.findAll(WACampaignReport.class);
	}

	public WACampaignReport findByRepId(Long repId){

		return (WACampaignReport)getHibernateTemplate().find("from WACampaignReport where waCrId = "+repId).get(0);

	}

}
