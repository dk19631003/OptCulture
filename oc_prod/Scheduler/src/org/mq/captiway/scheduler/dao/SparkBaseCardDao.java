package org.mq.captiway.scheduler.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.mq.captiway.scheduler.beans.SparkBaseCard;
import org.mq.captiway.scheduler.utility.Constants;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

public class SparkBaseCardDao extends AbstractSpringDao {
	
	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);

    public SparkBaseCardDao() {}

	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

    public SparkBaseCard find(Long id) {
        return (SparkBaseCard) super.find(SparkBaseCard.class, id);
    }

    public List<SparkBaseCard> findAllByLocId(Long sparkBaseLocationId) {
    	try {
    		List<SparkBaseCard> list = null;
			list = getHibernateTemplate().find("from SparkBaseCard where sparkBaseLocationId=" + sparkBaseLocationId);
			
			if(list != null && list.size() >0) return list;
			else return null;
			
		} catch (DataAccessException e) {
			logger.error("Exception ::::" , e);
			return null;
		}
    }
    
    public List<SparkBaseCard> findCardsByTransAndLocId(Long sparkBaseLocationId, String transCardsStr) {
    	try {
    		List<SparkBaseCard> list = null;
			list = getHibernateTemplate().find("from SparkBaseCard where sparkBaseLocationId=" + sparkBaseLocationId +
					" AND cardId IN ( "+transCardsStr +" ) ");
			
			if(list != null && list.size() >0) return list;
			else return null;
			
		} catch (DataAccessException e) {
			logger.error("Exception ::::" , e);
			return null;
		}
    }
    

    
    public List<SparkBaseCard> findAllByOrganizationId(long sparkBaseLocationId) {
    	try {
    		logger.info("sparkBaseLocationId is :"+sparkBaseLocationId);
    		List<SparkBaseCard> list = null;
			list = getHibernateTemplate().find("from SparkBaseCard where sparkBaseLocationId = " + sparkBaseLocationId);
			
			if(list.size() >0) return list;
			else return null;
			
		} catch (DataAccessException e) {
			logger.error("Exception ::::" , e);
			return null;
		}
    	
    }
    
    /**
     *  Fetchs all card status and their count for given locDetailsId
     * @return
     */
    public List<Object[]> findCardCountByStatus(Long sparkBaseLocDetsId) {
    	
    	String sql = "SELECT status,COUNT(status) as count FROM sparkbase_cards where sparkbase_location_id=" + sparkBaseLocDetsId + " GROUP BY status  ";
    	
    	List<Object[]> objArrList = jdbcTemplate.query(sql, new RowMapper() {
    		
    		@Override
    		public Object mapRow(ResultSet rs, int rowNo) throws SQLException {
    			
    				logger.info("---22---");
    				Object[] obj = new Object[2]; 
    				obj[0] = rs.getString("status");
    				obj[1] = rs.getInt("count");
    				
    			return obj;
    		}
		});
    	
    	return objArrList;
    }
    
  /**
   * fetch one INVENTORY type card among all the available card for given sparkbase store location</BR>
   * @param sparkBaseLocationId
   * @return
   */
    public SparkBaseCard findAvailableCardByStore(long sparkBaseLocationId, String cardType) {
    	try {
    		logger.info("sparkBaseLocationId is :"+sparkBaseLocationId);
    		List<SparkBaseCard> list = null;
    		
			String qry = "FROM SparkBaseCard WHERE sparkBaseLocationId = " + sparkBaseLocationId+ "" +
					" AND status='"+Constants.SPARKBASE_CARD_STATUS_INVENTORY+"' AND cardType='"+cardType+"'";
			
			list = executeQuery(qry, 0, 1);
			
			if(list.size() >0) return list.get(0);
			else return null;
			
		} catch (DataAccessException e) {
			logger.error("Exception ::::" , e);
			return null;
		}
    	
    }
    
    
    public List<SparkBaseCard> findByCardIdAndStatus(Long sparkBaseLocationId,Long cardId,String cardPin) {
    		
    	    logger.info("sparkBaseLocationId is :"+sparkBaseLocationId);
    
    		List<SparkBaseCard> list = null;
			list = getHibernateTemplate().find("from SparkBaseCard WHERE sparkBaseLocationId = " + sparkBaseLocationId +
					" AND cardId="+ cardId + " AND cardPin='"+ cardPin + "' ");
			
			if(list != null && list.size() >0) return list;
			else return null;
    }
    
    public Long findTotalInventoryCardsByLocId(Long sbLocId){
    
    	String query = "";
    	List<Long> list = null;
    	query = "select count(sparkBaseCard_id) from SparkBaseCard where sparkBaseLocationId = " +
    	sbLocId.longValue()+" and status = '"+Constants.SPARKBASE_CARD_STATUS_INVENTORY+"'";
    	logger.info("before query..........");
    	list = getHibernateTemplate().find(query);
    	logger.info("after query.........."+list);
    	if(list != null && list.size()>0){
    		logger.info("total inventory list size is ...>>>>>"+list.size());
    		logger.info("total inventory  is ...>>>>>"+list.get(0));
    		return list.get(0);
    	}
    	
    	return null;
	
    }
    
    public Long findTotalCardsByLocId(Long sbLocId) {
    	String query = "";
    	List<Long> list = null;
    	query = "select count(sparkBaseCard_id) from SparkBaseCard where sparkBaseLocationId = " +sbLocId.longValue();
    	logger.info("before query..........");
    	list = getHibernateTemplate().find(query);
    	logger.info("after query.........."+list);
    	if(list != null && list.size()>0){
    		return list.get(0);
    	}
    	
    	return null;
    }
    
    public List<Object[]> findAllInventoryCardsByLocId(Long sparkBaseLocDetsId, int startIndex, int pageSize) {
    	
    	String query = "SELECT card_id, card_pin, status, card_type from sparkbase_cards where sparkbase_location_id=" + 
    	sparkBaseLocDetsId.longValue()+" and status = '"+Constants.SPARKBASE_CARD_STATUS_INVENTORY+"'";
    	
    	List<Object[]> cardList = null;
		
    	
		try{		
			cardList= jdbcTemplate.query(query+" LIMIT "+startIndex+", "+pageSize, new RowMapper() {
	
		        public Object[] mapRow(ResultSet rs, int rowNum) throws SQLException {
		        
		        	Object[] object = new Object[4];
		        	
		        	object[0] = rs.getString(1);
		        	object[1] = rs.getString(2);
		        	object[2] = rs.getString(3);
		        	object[3] = rs.getString(4);
		        	return object;
		        }
		        
			});
			
			return cardList;
		}
		catch(Exception e){
			logger.error("Exception in finding loyalty inventory cards");
			logger.error("Exception ::::" , e);
			return null;
		}
    }

 public List<SparkBaseCard> findByCardId(Long sparkBaseLocationId,Long cardId) {
		
	    logger.info("sparkBaseLocationId is :"+sparkBaseLocationId);

		List<SparkBaseCard> list = null;
		list = getHibernateTemplate().find("from SparkBaseCard WHERE sparkBaseLocationId = " + sparkBaseLocationId +
				" AND cardId="+ cardId );
		
		if(list != null && list.size() >0) return list;
		else return null;
}
 
 
 
 public SparkBaseCard getCardByCardId(Long sparkBaseLocationId,Long cardId) {
		
	    logger.info("sparkBaseLocationId is :"+sparkBaseLocationId);

		List<SparkBaseCard> list = null;
		list = getHibernateTemplate().find("from SparkBaseCard WHERE sparkBaseLocationId = " + sparkBaseLocationId +
				" AND cardId="+ cardId );
		
		if(list != null && list.size() >0) return list.get(0);
		else return null;
}
    
}
