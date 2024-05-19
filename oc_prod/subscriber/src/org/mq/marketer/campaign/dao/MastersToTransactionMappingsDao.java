package org.mq.marketer.campaign.dao;

import java.util.Collection;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import org.mq.marketer.campaign.beans.MastersToTransactionMappings;
import org.mq.marketer.campaign.general.Constants;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

public class MastersToTransactionMappingsDao extends AbstractSpringDao {

	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);

	private SessionFactory sessionFactory;

	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
  }
	public MastersToTransactionMappings find(Long id) {
		return (MastersToTransactionMappings) super.find(MastersToTransactionMappings.class, id);
	}

	/*public void saveOrUpdate(MastersToTransactionMappings mastersToTransactionMappings) {
		super.saveOrUpdate(mastersToTransactionMappings);
	}

	public void delete(MastersToTransactionMappings mastersToTransactionMappings) {
		super.delete(mastersToTransactionMappings);
	}
	
	  public void saveByCollection(Collection mastersToTransactionMappings) {
//	    	super.saveOrUpdateAll(retailProSalesCollection);
		  logger.info(">>>>>>>>>>>>>>>>.");
	    	getHibernateTemplate().saveOrUpdateAll(mastersToTransactionMappings);
	    }*/
	public List<MastersToTransactionMappings> findByUserId(long userId){
		
		try {
			List<MastersToTransactionMappings> list=null;
			list=getHibernateTemplate().find("FROM MastersToTransactionMappings WHERE userId="+userId);
			if(list != null && list.size() > 0){
				return list;
			}else return null;
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
			return null;
		}
	}
	
	/*public void deleteByIds(String ids) {
		
		String query = "delete from MastersToTransactionMappings where id in ("+ids+")";
		logger.info("deleteByIds query is :"+query);
		int count = getHibernateTemplate().bulkUpdate(query);
		logger.info(" delete count is  ::"+count);
	}*/
	
}