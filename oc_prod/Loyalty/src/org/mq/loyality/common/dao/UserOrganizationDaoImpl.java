package org.mq.loyality.common.dao;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.mq.loyality.common.hbmbean.UserOrganization;
import org.mq.loyality.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;


public class UserOrganizationDaoImpl extends AbstractDAOImpl<UserOrganization,Long> implements UserOrganizationDao{
	protected UserOrganizationDaoImpl() {
		super(UserOrganization.class);
	}
	@Autowired
	private SessionFactory sessionFactory;
	private static final Logger logger = LogManager.getLogger(Constants.LOYALTY_LOGGER);

	@Override
	 @Transactional
	public List<UserOrganization> getOrgDetails(Long id) {
		// TODO Auto-generated method stub
		logger.info("url string is=========>"+id);
		Query query = sessionFactory.getCurrentSession().createQuery("from  UserOrganization where userOrgId = :code ");
		query.setParameter("code", id);
		List<UserOrganization> list = query.list();
		logger.info("list in dao id============>"+list);
     	return list;
	}
	
	
}
