package org.mq.loyality.common.dao;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.mq.loyality.common.hbmbean.UserOrganization;
import org.mq.loyality.exception.BaseDAOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;


public class AbstractDAOImpl<E, I extends Serializable> implements AbstractDAO<E, I> {
	private Class<E> entityClass;
	private static final int HIBERNATE_JDBC_BATCH_SIZE = 20;
	protected AbstractDAOImpl(Class<E> entityClass) {
		this.entityClass = entityClass;
	}
	private static final Logger LOG = LoggerFactory.getLogger(AbstractDAOImpl.class);
	@Autowired
	private  SessionFactory sessionFactory;
	@Autowired
	private SessionFactory sessionFactoryForDML;

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public Session getCurrentSession() {
		return sessionFactory.getCurrentSession();
	}
	
	public Session getCurrentSessionForDML() {
		return sessionFactoryForDML.getCurrentSession();
	}
	
@SuppressWarnings("unchecked")
	@Override
	public E findById(I id) {
		return (E) getCurrentSession().get(entityClass, id);
	}

	@Override
	public void saveOrUpdate(E e) {
		getCurrentSessionForDML().saveOrUpdate(e);
	}

	@Override
	public void delete(E e) {
		getCurrentSessionForDML().delete(e);
	}



	@SuppressWarnings("unchecked")
	@Override
	public List<E> findByCustomQuery(String criteria, E e) {
		List<E> list = null;
		try {
			String sqlQuery = "from " + e.getClass().getName() + " where " + criteria;
			Query query = getCurrentSession().createQuery(sqlQuery);
			list = query.list();
		} catch (HibernateException hbmx) {
			LOG.error("Error @ findByCustomQuery Method ::", hbmx);
			throw new BaseDAOException("Error @ findByCustomQuery Method :", hbmx);
		}

		return list;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<E> findByCriteria(List<Criterion> criterionList) {
		Criteria criteria = getCurrentSession().createCriteria(entityClass);
		for (Criterion criterion : criterionList) {
			criteria.add(criterion);
		}
		return criteria.list();
	}

	

	

	

}
