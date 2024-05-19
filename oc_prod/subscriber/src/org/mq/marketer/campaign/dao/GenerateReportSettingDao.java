package org.mq.marketer.campaign.dao;

import java.util.Iterator;
import java.util.List;

import org.mq.marketer.campaign.beans.GenerateReportSetting;
import org.mq.marketer.campaign.general.Constants;
import org.mq.optculture.utils.OCConstants;
import org.springframework.jdbc.core.JdbcTemplate;

public class GenerateReportSettingDao extends AbstractSpringDao{
	
private JdbcTemplate jdbcTemplate;
	
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	
	public List<GenerateReportSetting> findAll() {
		
				
		String query = " SELECT id " +
				" FROM generate_report_setting  " +
				" WHERE enable=true ";
				/*+ "and (if(freequency='D',"
				+ "DATEDIFF(now(),last_generated_on)>=1, DATEDIFF(now(),last_generated_on)>=7) OR "
				+ "(last_generated_on IS NULL AND last_generated_file IS NULL))";*/
		logger.debug("query ==="+query);
		List<GenerateReportSetting> generateReportSettingList =null;
		List <Long> idsList=null;
		String idsListStr = Constants.STRING_NILL;
		idsList = jdbcTemplate.queryForList(query,Long.class);

		if(idsList.isEmpty()) {

			if(logger.isDebugEnabled()) logger.debug("No Active Trigger FOund....Exiting");
			return generateReportSettingList;
		}

		for(Iterator<Long> iterator=idsList.iterator();iterator.hasNext();){
			if(idsListStr.length() <= 0) {
				idsListStr += iterator.next();
			}
			else {
				idsListStr += ","+iterator.next();
			}

		} //idsListStr has all the active trigger ids list

		query = " FROM GenerateReportSetting " +" WHERE id IN ("+idsListStr+") ";

		//if(logger.isInfoEnabled()) logger.info("inside EventTriggerDao.EventTrigger() executing the query "+queryStr);
		generateReportSettingList = getHibernateTemplate().find(query);
		return generateReportSettingList;

		
		
	}
	
	public  List<GenerateReportSetting> findBy(Long orgId){
		String query = " FROM GenerateReportSetting where orgId="+orgId;
			 return getHibernateTemplate().find(query);
	}
	
	
}
