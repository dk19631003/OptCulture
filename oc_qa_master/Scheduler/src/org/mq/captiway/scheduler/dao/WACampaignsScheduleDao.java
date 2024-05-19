package org.mq.captiway.scheduler.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.Users;
import org.mq.captiway.scheduler.beans.WACampaign;
import org.mq.captiway.scheduler.beans.WACampaignsSchedule;
import org.mq.captiway.scheduler.utility.Constants;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

public class WACampaignsScheduleDao extends AbstractSpringDao {

	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	public WACampaignsScheduleDao() {}
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
	private Long id;

	public synchronized Long getCurrentId() {

		try {
			if(id == null) {

				List list = getHibernateTemplate().find("SELECT MAX(waCsId) FROM WACampaignsSchedule ") ;
				if(logger.isDebugEnabled()) logger.debug(" List :"+list);
				this.id = (list != null && list.size() > 0 && list.get(0) != null) ? (Long)list.get(0):0 ;

			}
			return ++id;
		} catch (DataAccessException e) {
			if(logger.isErrorEnabled()) logger.error("** Exception : while getting the current id ", e);
			return id+100000;
		}

	}

	public List<WACampaignsSchedule> getActiveList(String currentDateStr) {

		String query = " SELECT DISTINCT wcs " +
				" FROM WACampaignsSchedule wcs, Users usr " +
				" WHERE wcs.userId = usr.userId AND wcs.scheduledDate <= '" + currentDateStr + "' " +
				" AND wcs.status = 0 AND DATE(wcs.scheduledDate) <= DATE(usr.packageExpiryDate) AND usr.enabled = 1 ";
		logger.info("getActiveList query :"+query);
		return getHibernateTemplate().find(query);

	}

	public List<WACampaignsSchedule> getInactiveList(String currentDateStr) {

		return executeQuery(
				" SELECT DISTINCT wcs " +
						" FROM WACampaignsSchedule wcs, Users usr " +
						" WHERE wcs.userId = usr.userId AND wcs.userId IS NOT NULL AND wcs.scheduledDate <= '" + currentDateStr + "' " +
				" AND wcs.status = 0 AND ( DATE(wcs.scheduledDate) > DATE(usr.packageExpiryDate) OR usr.enabled = 0) ");

	}

	public List<WACampaignsSchedule> getActiveForLongCampList(String currentDateStr,int hrs){
		String qry= " SELECT * FROM wa_campaign_schedule wcs, users usr " +
				" WHERE wcs.user_id = usr.user_id AND wcs.status = 0 "
				+ "AND (DATE_ADD(wcs.scheduled_date,INTERVAL "+hrs+" HOUR)) < '" + currentDateStr + "' "
				+ "AND ( DATE(wcs.scheduled_date) <= DATE(usr.package_expiry_date) AND usr.enabled = 1)";
		logger.info("qry>>>>>>>"+qry);

		List<WACampaignsSchedule> list = null;
		try {

			list = jdbcTemplate.query(qry, new RowMapper() {

				public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
					WACampaignsSchedule waCampaignsSchedule = new WACampaignsSchedule();

					waCampaignsSchedule.setWaCsId(rs.getLong("wa_cs_id"));
					waCampaignsSchedule.setStatus(rs.getByte("status"));
					waCampaignsSchedule.setWaCampaignId(rs.getLong("wa_campaign_id"));

					if(rs.getDate("scheduled_date") != null) {
						Calendar cal = Calendar.getInstance();
						cal.setTime(rs.getTimestamp("scheduled_date"));
						waCampaignsSchedule.setScheduledDate(cal);

					} 

					Users user  = new Users();
					user.setUserId(rs.getLong("user_id"));
					waCampaignsSchedule.setUserId(user.getUserId());


					return waCampaignsSchedule;
				}
			});
		} catch (Exception e) {
			logger.error("Exception ***", e);
		}
		return list;
	}


}
