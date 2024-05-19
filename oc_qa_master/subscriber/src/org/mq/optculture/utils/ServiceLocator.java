package org.mq.optculture.utils;


import org.mq.marketer.campaign.dao.AbstractSpringDao;
import org.mq.marketer.campaign.dao.AbstractSpringDaoForDML;
import org.mq.optculture.business.common.BaseService;
import org.zkoss.zkplus.spring.SpringUtil;


public class ServiceLocator {
	
	private static ServiceLocator serviceLocator;
	private ServiceLocator() {}
	
	public static ServiceLocator getInstance(){
		if(serviceLocator == null){
			serviceLocator = new ServiceLocator();
		}
		return serviceLocator;
	}
	
	/*public static Session getHibernateSession() {
		return ((SessionFactory) SpringUtil.getBean("sessionFactory", SessionFactory.class)).getCurrentSession();
	}*/
	
	public BaseService getServiceByName(String serviceName) {
		return (BaseService)  ApplicationContextProvider.getApplicationContext().getBean(serviceName, BaseService.class);
	}
	
	/*public BaseDAO getDAOByName(String daoName){
		return (BaseDAO) SpringUtil.getBean(daoName, BaseDAO.class);
	}*/
	
	public AbstractSpringDao getDAOByName(String daoName) throws Exception{
		return (AbstractSpringDao) ApplicationContextProvider.getApplicationContext().getBean(daoName, AbstractSpringDao.class);
	}
	
	public AbstractSpringDaoForDML getDAOForDMLByName(String daoName) throws Exception{
		
		return (AbstractSpringDaoForDML) ApplicationContextProvider.getApplicationContext().getBean(daoName, AbstractSpringDaoForDML.class);
	}
	
	public Object getBeanByName(String serviceName) {
		return ApplicationContextProvider.getApplicationContext().getBean(serviceName);
	}
	public Object getServiceById(String serviceName) {
		return ApplicationContextProvider.getApplicationContext().getBean(serviceName);
	}
}
