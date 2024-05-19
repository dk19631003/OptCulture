package org.mq.captiway.scheduler.dao;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.mq.captiway.scheduler.utility.Constants;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DeadlockLoserDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@SuppressWarnings("unchecked")
public abstract class AbstractSpringDaoForDML  extends HibernateDaoSupport 
														implements ApplicationContextAware{


	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	
	private JdbcTemplate jdbcTemplate;
	public AbstractSpringDaoForDML() { }

    private ApplicationContext applicationContext;

	public void setApplicationContext(ApplicationContext applicationContext){
		this.applicationContext = applicationContext;
	}
	
    public void saveOrUpdate(Object obj) {
        getHibernateTemplate().saveOrUpdate(obj);
    }
    protected void merge(Object obj) {
        getHibernateTemplate().merge(obj);
    }
    protected void update(Object obj) {
        getHibernateTemplate().update(obj);
    }

    protected void delete(Object obj) {
        getHibernateTemplate().delete(obj);
    }

	/*protected Object find(Class clazz, Long id) {
        return getHibernateTemplate().load(clazz, id);
    }

    protected List find(String query) {
        return getHibernateTemplate().find(query);
    }

    protected List findAll(Class clazz) {
        return getHibernateTemplate().find("from " + clazz.getName());
    }*/
    
    
    protected void saveOrUpdateAll(Collection collection){
    	getHibernateTemplate().saveOrUpdateAll(collection);
    }
    
    public void clear(Object obj){
    	try {
    		getHibernateTemplate().evict(obj);
    	} catch (DataAccessException e) {
    		if(logger.isErrorEnabled()) logger.error("Unable to clear the chache - " + e);
    	}
    }
    
    
    /**
     * Useful for inserts and updates
     * @param queryStr - Query
     * @return - int : count of number of updated rows
     */
    public synchronized int executeUpdate(String queryStr) {
    	Session session = null;
    	try{
    		session = getSession();
    		Query query = session.createQuery(queryStr);
    		return query.executeUpdate();
    		
    	}catch(Exception e) {
    		if(logger.isErrorEnabled()) logger.error("** Exception ", e);
    		return 0;
    	}finally {
    		if(session != null) {
	    		session.flush();
	    		session.close();
    		}
    	}
    	
    }
    
    
    /**
     * Useful for select queries.
     * @param queryStr - Query to execute
     * @return - List : returns the list of objects returned by result set, 
     * on exception returns empty list
     */
   /* public List executeQuery(String queryStr) {
    	Session session = null;
    	List resList = new ArrayList();
    	try{
    		session = getSession();
    		Query query = session.createQuery(queryStr);
    		resList = query.list();
    		return resList;
    		
    	}catch(Exception e) {
    		if(logger.isErrorEnabled()) logger.error("** Exception ", e);
    		return resList;
    	}finally {
    		if(session != null) {
	    		session.flush();
	    		session.close();
    		}
    	}
    	
    }*/

	/**
     * Useful for select queries with 
     * @param queryStr - Query to execute
     * @param startFrom - Start index from the result set to fetch 
     * @param count - number of rows to be fetched starts form startFrom param
     * @return - List : returns the list of objects returned by result set, 
     * on exception returns empty list
     */
    /*public List executeQuery(String queryStr, int startFrom, int count ) {
    	Session session = null;
    	List resList = new ArrayList();
    	try{
    		session = getSession();
    		Query query = session.createQuery(queryStr);
    		query.setFirstResult(startFrom);
			query.setMaxResults(count);
    		resList = query.list();
    		return resList;
    		
    	}catch(Exception e) {
    		if(logger.isErrorEnabled()) logger.error("** Exception ", e);
    		return resList;
    	}finally {
    		if(session != null) {
	    		session.flush();
	    		session.close();
    		}
    	}
    	
    }*/ // executeQuery
    
	
	public void executeJdbcQuery(String queryStr){
		
		if(jdbcTemplate == null) {
			jdbcTemplate = (JdbcTemplate)applicationContext.getBean("jdbcTemplate");
		}
		jdbcTemplate.execute(queryStr);
		
	}
	
	public int executeJDBCInsertQuery(String queryStr) throws Exception{
		try {
			if(jdbcTemplate == null) {
				jdbcTemplate = (JdbcTemplate)applicationContext.getBean("jdbcTemplate");
			}
			
			int result = jdbcTemplate.update(queryStr);
			
			// jdbcTemplate.execute(queryStr);
			/*try {
				jdbcTemplate.getDataSource().getConnection().commit();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				logger.error("Exception ::::" , e);
			}*/
			//if(logger.isInfoEnabled()) logger.error(" query to save events::"+queryStr+" result ::");
			return result;
		
		} catch(DeadlockLoserDataAccessException e) {
			
			throw new DeadlockLoserDataAccessException("deadlock occured", new Throwable());
		}
		catch(CannotAcquireLockException e) {
			
			throw new CannotAcquireLockException("contacts table got locked");
		}
		catch (BeansException e) {
			if(logger.isErrorEnabled()) logger.error(" ** Exception while getting the bean jdbcTemplate from the context", e);
			return -1;
		}
		catch (DataAccessException e) {
			if(logger.isErrorEnabled()) logger.error("** Exception while executing the query:"+queryStr, e);
			return -1;
		}
	}
	
	public int executeJdbcUpdateQuery(String queryStr) {
		
		try {
			if(jdbcTemplate == null) {
				jdbcTemplate = (JdbcTemplate)applicationContext.getBean("jdbcTemplate");
			}
			
			int result = jdbcTemplate.update(queryStr);
			
			// jdbcTemplate.execute(queryStr);
			/*try {
				jdbcTemplate.getDataSource().getConnection().commit();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				logger.error("Exception ::::" , e);
			}*/
			//if(logger.isInfoEnabled()) logger.error(" query to save events::"+queryStr+" result ::");
			return result;
		
		} 
		catch (BeansException e) {
				if(logger.isErrorEnabled()) logger.error(" ** Exception while getting the bean jdbcTemplate from the context", e);
				return -1;
		}
		catch (DataAccessException e) {
			if(logger.isErrorEnabled()) logger.error("** Exception while executing the query:"+queryStr, e);
			return -1;
		}
		
	}
	
	// added for sparkbase
	  protected void save(Object obj) {
	        getHibernateTemplate().save(obj);
	    }
	
}
