package org.mq.marketer.campaign.dao;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import org.mq.marketer.campaign.beans.CustomerSalesUpdatedData;
import org.mq.marketer.campaign.general.Constants;


@SuppressWarnings( { "unchecked", "serial", "unused", "deprecation" })
public class CustomerSalesUpdateDataDaoForDML extends AbstractSpringDaoForDML {

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);

	private SessionFactory sessionFactory;

	public CustomerSalesUpdateDataDaoForDML() {
	}
	
	/*public CustomerSalesUpdatedData find(Long id) {
		return (CustomerSalesUpdatedData) super.find(CustomerSalesUpdatedData.class, id);
	}*/

	public void saveOrUpdate(CustomerSalesUpdatedData customerSalesUpdatedData) {
		super.saveOrUpdate(customerSalesUpdatedData);
	}
	public void merge(CustomerSalesUpdatedData customerSalesUpdatedData) {
		super.merge(customerSalesUpdatedData);
	}
	public void delete(CustomerSalesUpdatedData customerSalesUpdatedData) {
		super.delete(customerSalesUpdatedData);
	}
/*
	
	public CustomerSalesUpdatedData findByCustId(String customerId, Long userId){
		
		String queryStr = " FROM CustomerSalesUpdatedData WHERE customerId = '"+customerId+"' AND userId = "+userId;
		try{
			List<CustomerSalesUpdatedData> list = getHibernateTemplate().find(queryStr);
		
			if(list != null && list.size() > 0){ return (CustomerSalesUpdatedData)list.get(0);}
			else return null;
		}catch(Exception e){
			logger.error("Exception while fetching customersalesupdate data...", e);
		}
		return null;
	}

	public double getLifeTimePurchaseValue(String customerId, Long userId) {
		double totPurchaseAmt = 0.0;
		String queryStr = "Select totPurchaseAmt FROM CustomerSalesUpdatedData WHERE customerId = '"+customerId+"' AND userId = "+userId;
		List<Double> tempList = getHibernateTemplate().find(queryStr);
		
		if(tempList != null && tempList.size() > 0){ 
			return  tempList.get(0).doubleValue();
		}
		else return 0.0;
		
		
	}
	*/
}
