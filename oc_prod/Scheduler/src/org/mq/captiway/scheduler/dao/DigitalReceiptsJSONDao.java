package org.mq.captiway.scheduler.dao;

import java.util.List;

import org.mq.captiway.scheduler.beans.DigitalReceiptsJSON;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.utility.Constants;

public class DigitalReceiptsJSONDao extends AbstractSpringDao{
	
	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	
	private JdbcTemplate jdbcTemplate;
	
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public DigitalReceiptsJSONDao(){}
	
	public DigitalReceiptsJSON find(Long id){
		return (DigitalReceiptsJSON)super.find(DigitalReceiptsJSON.class, id);
	}
	
	/*public void saveOrUpdate(DigitalReceiptsJSON drJSON){
		super.saveOrUpdate(drJSON);
	}*/
	
	/*public void delete(DigitalReceiptsJSON drJSON){
		super.delete(drJSON);
	}*/
	
	public List<DigitalReceiptsJSON> findDRJSONsByUserId(Long userId, String status){
		
		List<DigitalReceiptsJSON> drJsonList = null;
		try {
			
			String query = " FROM DigitalReceiptsJSON WHERE userId = " + userId+ " AND status = '"+status+"'";
			drJsonList = executeQuery(query);

			if (drJsonList.size() > 0) {
				return drJsonList;
			} else {
				return null;
			}
		} catch (DataAccessException e) {
			logger.error("Exception ::::" , e);
			return null;
		}catch (Exception e) {
			logger.error("Exception ::::" , e);
			return null;
		}
	}
	
public List<DigitalReceiptsJSON> findDRJSONsByUserId(Long userId, String status, int startFrom, int count){
		
		List<DigitalReceiptsJSON> drJsonList = null;
		try {
			
			String queryStr = " FROM DigitalReceiptsJSON WHERE userId = " + userId+ " AND status = '"+status+"'";
			drJsonList = executeQuery(queryStr, startFrom, count);

			if (drJsonList.size() > 0) {
				return drJsonList;
			} else {
				return null;
			}
		} catch (DataAccessException e) {
			logger.error("Exception ::::" , e);
			return null;
		}catch (Exception e) {
			logger.error("Exception ::::" , e);
			return null;
		}
	}
	
public long findDRJSONsCountByUserId(Long userId, String status){
		
		List<Long> drJsonList = null;
		try {
			
			String query = " SELECT COUNT(drjsonId) FROM DigitalReceiptsJSON WHERE userId = " + userId+ " AND status = '"+status+"'";
			drJsonList = executeQuery(query);

			if (drJsonList.size() > 0) {
				return ((Long)drJsonList.get(0)).longValue();
			} else {
				return 0;
			}
		} catch (DataAccessException e) {
			logger.error("Exception ::::" , e);
			return 0;
		}catch (Exception e) {
			logger.error("Exception ::::" , e);
			return 0;
		}
	}
	public String findJsonStrById(Long jsonObjId){
		
		try {
			String query = " select json_str from digital_receipts_json WHERE dr_json_id=?";
			
			String jsonStr = (String)getJdbcTemplate().queryForObject(query, new Object[] { jsonObjId }, String.class);
			return jsonStr;
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::::" , e);
			return null;
		}
		
		
	}

}