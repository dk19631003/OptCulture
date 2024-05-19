package org.mq.marketer.campaign.dao;

import java.util.List;
import java.util.Map;

import org.mq.marketer.campaign.beans.DRSent;
import org.mq.marketer.campaign.beans.DigitalReceiptsJSON;
import org.mq.marketer.campaign.general.Constants;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

public class DigitalReceiptsJSONDao extends AbstractSpringDao{
	
	
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
	}
	*/
	/*
	public void delete(DigitalReceiptsJSON drJSON){
		super.delete(drJSON);
	}*/
	
	public List<DigitalReceiptsJSON> findDRJSONsByUserId(Long userId, String status){
		
		List<DigitalReceiptsJSON> drJsonList = null;
		try {
			
			String query = " FROM DigitalReceiptsJSON WHERE userId = " + userId+ " AND status = '"+status+"'";
			drJsonList = getHibernateTemplate().find(query);

			if (drJsonList.size() > 0) {
				return drJsonList;
			} else {
				return null;
			}
		} catch (DataAccessException e) {
			logger.error("Exception ::", e);
			return null;
		}
	}
	public String findJsonStrById(Long jsonObjId){
		
		try {
			String query = " select json_str from digital_receipts_json WHERE dr_json_id=?";
			
			String jsonStr = (String)getJdbcTemplate().queryForObject(query, new Object[] { jsonObjId }, String.class);
			return jsonStr;
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
			return null;
		}
		
		
	}

	//Added following code for store_number of dr_sent table, will remove in future 4.2.3
	//started here
	public List<DRSent> getDRSent(Long user_id, int start, int end) {
		String str = " FROM DRSent where  userId="+user_id;
		return executeQuery(str, start, end);
		
	}
	
	public List<Map<String, Object>> getDRSentDate(Long user_id, String fromCal, String endCal) {
		String str = " SELECT DATE(sent_date) AS sentDate FROM dr_sent where  user_id="+user_id+" AND DATE(sent_date) BETWEEN '"+fromCal+"' AND '"+endCal+"'  AND doc_sid is not null GROUP BY DATE(sent_date)";
		return  getJdbcTemplate().queryForList(str);
		
	}
	
	public List<DRSent> getDRSent(Long user_id, String fromCal) {
		String str = " FROM DRSent where  userId="+user_id+" AND DATE(sentDate) = '"+fromCal+"' AND docSid is not null";
		return executeQuery(str);
		
	}
	
	public List<DRSent> getDRSent(Long user_id, String fromCal, String endCal) {
		String str = " FROM DRSent where  userId="+user_id+" AND DATE(sentDate) BETWEEN '"+fromCal+"' AND '"+endCal+"'  AND docSid is not null GROUP BY DATE(sentDate)";
		return executeQuery(str);
		
	}

	public int getTotalCount(Long user_id) {
		// TODO Auto-generated method stub
		String query = "SELECT COUNT(*) FROM DRSent where userId="+user_id;
		List l = executeQuery(query);
		if(l!=null && l.size()>0){
			return Integer.valueOf(l.get(0).toString());
		}
		return 0;
	}
	
	public int getTotalCount(Long user_id, String fromCal, String endCal) {
		// TODO Auto-generated method stub
		String query = "SELECT COUNT(*) FROM DRSent where userId="+user_id+" AND sentDate BETWEEN '"+fromCal+"' AND '"+endCal+"'";
		List l = executeQuery(query);
		if(l!=null && l.size()>0){
			return Integer.valueOf(l.get(0).toString());
		}
		return 0;
	}
	public List<DigitalReceiptsJSON> getDrJson(String id) {
		String query = "FROM DigitalReceiptsJSON where drjsonId in ("+id+")";
		return executeQuery(query);
	}
	
	public List<DigitalReceiptsJSON> getDrJson(Long user_id, String fromCal) {
		String query = "FROM DigitalReceiptsJSON where userId="+user_id+" AND DATE(createdDate) = '"+fromCal+"'";
		return executeQuery(query);
	}
	
	public DigitalReceiptsJSON getDigitalReceiptJson(String id) {
		String query = "FROM DigitalReceiptsJSON where drjsonId ="+id;
		return (DigitalReceiptsJSON)executeQuery(query).get(0);
	}
	public DigitalReceiptsJSON findBy(String docSID, Long userID) {
		try {
		String query = "FROM DigitalReceiptsJSON where userId ="+userID+" AND docSid='"+docSID+"'";
		List<DigitalReceiptsJSON> retList = executeQuery(query);
		if(retList== null || retList.isEmpty()) return null;
		else return retList.get(0);
		} catch (Exception e) {
		// TODO Auto-generated catch block
		logger.error("Exception ", e);
		return null;
		}
		}
	//ended here
}
