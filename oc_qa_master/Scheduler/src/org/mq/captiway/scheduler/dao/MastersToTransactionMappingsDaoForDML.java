package org.mq.captiway.scheduler.dao;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import org.mq.captiway.scheduler.beans.MastersToTransactionMappings;
import org.mq.captiway.scheduler.utility.Constants;
import org.springframework.jdbc.core.JdbcTemplate;

public class MastersToTransactionMappingsDaoForDML extends AbstractSpringDaoForDML {

	
	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);

	private SessionFactory sessionFactory;

	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
  }
/*	public MastersToTransactionMappings find(Long id) {
		return (MastersToTransactionMappings) super.find(MastersToTransactionMappings.class, id);
	}*/

	public void saveOrUpdate(MastersToTransactionMappings mastersToTransactionMappings) {
		super.saveOrUpdate(mastersToTransactionMappings);
	}

	public void delete(MastersToTransactionMappings mastersToTransactionMappings) {
		super.delete(mastersToTransactionMappings);
	}
	
/*	public List<MastersToTransactionMappings> findByUserId(Long userId){
		String qry="FROM MastersToTransactionMappings 	WHERE userId="+userId.longValue();
		if(logger.isDebugEnabled()) logger.debug(" qry is "+qry);
		return getHibernateTemplate().find(qry);
		
	}*/
	
}