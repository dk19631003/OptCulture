package org.mq.captiway.scheduler.dao;

import java.util.List;

import org.mq.captiway.scheduler.beans.CustomerSalesUpdatedData;
import org.mq.captiway.scheduler.beans.RetailProSalesCSV;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

public class CustomerSalesUpdatedDataDaoForDML  extends AbstractSpringDaoForDML {
	
	private JdbcTemplate jdbcTemplate;
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	 public void saveOrUpdate(CustomerSalesUpdatedData obj1) {
	        super.saveOrUpdate(obj1);
	    }
	
/*	public CustomerSalesUpdatedData findObjByCustAndUserId(String customerId, Long userId) {
		
			try {
				List<CustomerSalesUpdatedData> salesAggDatLst = null;
				salesAggDatLst = getHibernateTemplate().find("FROM CustomerSalesUpdatedData WHERE customerId='"+customerId+"' AND userId="+userId);
				if(salesAggDatLst != null && salesAggDatLst.size() > 0){
					return salesAggDatLst.get(0);
				}else return null;
			} catch (DataAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		
	}*/
}

