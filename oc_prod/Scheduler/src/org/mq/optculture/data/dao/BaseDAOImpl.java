/**
 * 
 */
package org.mq.optculture.data.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.mq.optculture.exception.BaseDAOException;
import org.springframework.orm.hibernate3.HibernateTemplate;

/**
 * @author manjunath.nunna
 *
 */
public abstract class BaseDAOImpl implements BaseDAO{

	private static final ThreadLocal<Session> threadLocal = new ThreadLocal<Session>();

	private SessionFactory sessionFactory;

	private HibernateTemplate hibernateTemplate;

	/**
	 * 
	 * @return
	 */
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	/**
	 * 
	 * @param sessionFactory
	 */
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
		this.hibernateTemplate = new HibernateTemplate(sessionFactory);
	}

	/**
	 * 
	 * @return
	 */
	public HibernateTemplate getHibernateTemplate(){
		return this.hibernateTemplate;	
	}

	/**
	 * 
	 * @return
	 * @throws BaseDAOException
	 */
	public Session getSession()  throws BaseDAOException{
		Session session = null;
		try{
			session = (Session) threadLocal.get();

			if (session == null || !session.isOpen()) {

				session = (sessionFactory != null) ? sessionFactory.getCurrentSession() : null;
				if(session == null)
					session = (sessionFactory != null) ? sessionFactory.openSession() : null;
					threadLocal.set(session);
			}
		}catch(Exception exp){
			throw new BaseDAOException(exp.getMessage(), exp);
		}

		return session;
	}

	/**
	 * 
	 * @throws BaseDAOException
	 */
	public static void closeSession() throws BaseDAOException {
		try{
			Session session = (Session) threadLocal.get();
			threadLocal.set(null);

			if (session != null && session.isOpen()) {
				session.close();
			}
		}catch(Exception exp){
			throw new BaseDAOException(exp.getMessage(), exp);
		}
	}

	public void save(Object obj) throws BaseDAOException{
		try{
			getHibernateTemplate().save(obj);
		}catch(Exception exp){
			throw new BaseDAOException(exp.getMessage(), exp);
		}
	}

	public void saveOrUpdate(Object obj) throws BaseDAOException{
		try{
			getHibernateTemplate().saveOrUpdate(obj);
		}catch(Exception exp){
			throw new BaseDAOException(exp.getMessage(), exp);
		}
	}
	
	public void update(Object obj) throws BaseDAOException{
		try{
			getHibernateTemplate().update(obj);
		}catch(Exception exp){
			throw new BaseDAOException(exp.getMessage(), exp);
		}
	}
	
	public void delete(Object obj) throws BaseDAOException{
		try{
			getHibernateTemplate().delete(obj);
		}catch(Exception exp){
			throw new BaseDAOException(exp.getMessage(), exp);
		}
	}
	
	public List find(String hqlQuery) throws BaseDAOException{
		try{
		return getHibernateTemplate().find(hqlQuery);
		}catch(Exception exp){
			throw new BaseDAOException(exp.getMessage(), exp);
		}
	}

}
