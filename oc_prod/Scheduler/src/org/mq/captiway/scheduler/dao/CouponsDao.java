package org.mq.captiway.scheduler.dao;

import java.util.Collection;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import org.mq.captiway.scheduler.beans.Contacts;
import org.mq.captiway.scheduler.beans.Coupons;
import org.mq.captiway.scheduler.utility.Constants;
import org.mq.optculture.utils.OCConstants;
import org.springframework.jdbc.core.JdbcTemplate;

public class CouponsDao extends AbstractSpringDao {

	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);

	private SessionFactory sessionFactory;

	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}


	public Coupons find(Long id) {
		return (Coupons) super.find(Coupons.class, id);
	}
/*
	public void saveOrUpdate(Coupons coupons) {
		super.saveOrUpdate(coupons);
	}

	public void delete(Coupons coupons) {
		super.delete(coupons);
	}*/
	/*
	public  findCouponByCouponId(long cId) {
		String query = "From Coupons WHERE couponId="+cId;
		return (Coupons)getHibernateTemplate().find(query) ;
		
	}
	*/
	public Coupons findById(Long cId) {
    	if(logger.isDebugEnabled()) logger.debug(">>>>>>>>>>>>entered into couponsdao "+cId);
    	List<Coupons> couponsList = getHibernateTemplate().find("FROM Coupons WHERE couponId = "+ cId);
    	if(logger.isDebugEnabled()) logger.debug(">>>>couponsList >> "+couponsList.size());
    	if(couponsList.size()>0){
    		return couponsList.get(0);
    	}
        return null;
    }
	
	public  int findTotCountCoupons(long orgId) {
	    	
		
		String qry ="SELECT  COUNT(*) FROM  Coupons  WHERE orgId ="+orgId;
		int count =   ((Long)getHibernateTemplate().find(qry).get(0)).intValue();
		
		
		return count;
	    	
	} // findTotCountCoupons
	
	
	public List<Coupons> findCouponsByOrgId(Long orgId, int startIdx, int endIdx) {
		List<Coupons> list = null;
		String query = "FROM Coupons  WHERE orgId ="+orgId+" ORDER BY couponCreatedDate DESC LIMIT  "+startIdx+" , "+endIdx;
		list  = getHibernateTemplate().find(query);
		
		if(list!= null && list.size()>0) {
			return list;
		}
			
		return null;
	}
	
	/*public int updateIssuedCountByCouponId(Long couponId, long totalSize) {
		String qry = "UPDATE Coupons set issued="+totalSize+", available=(totalQty-"+totalSize+") WHERE couponId="+couponId;
		
		int updateCount = executeUpdate(qry);
		return updateCount;
		
	}*/

	// Added for timers code shifted to Scheduler 
	
	public  int getAllCouponsCount() {
	    	
			///
			String qry ="SELECT  COUNT(couponId) FROM  Coupons ";
	//		logger.debug(" &&&&&&&&&&&&& =====coupon count query is  ::"+qry);
			int count =   ((Long)getHibernateTemplate().find(qry).get(0)).intValue();
			
			
			return count;
		    	
		} // findTotCountCoupons
	public List<Coupons> getAllCoupons(int startIdx, int endIdx) {
		List<Coupons> coupList = null;
	//	logger.debug(">>>>>>>> check the coupons exists <<<<<<<<<<<<<<<<<<");
		
		String query = "FROM Coupons WHERE expiryType= '"+Constants.COUP_EXPIRY_TYPE_STATIC+"' order by 1 ASC ";
		//return coupList  = getHibernateTemplate().find("FROM Coupons");	
	//	logger.debug("===update query is ::"+query);
		coupList = executeQuery(query, startIdx, endIdx);
		if(coupList!= null && coupList.size()>0) {
			return coupList;
		}
			
		return null;
		
	} // getAllCoupons
	
/*	
	public void saveByCollection(Collection<Coupons> coupCollection){
		super.saveOrUpdateAll(coupCollection);
	}
	*/	    
	public int getDynamicCoupons(){
		
		String qry ="SELECT COUNT(couponId) FROM Coupons where expiryType = '"+Constants.COUP_EXPIRY_TYPE_DYNAMIC+"' ";
		int count = ((Long)getHibernateTemplate().find(qry).get(0)).intValue();
	//	logger.info("Dynamic coupon count is-----"+count);
		return count;
	}
	
	public List<Coupons> getAllDynamicCoupons(int startIdx, int endIdx) {
		
		List<Coupons> coupList = null;
		String query = "FROM Coupons where expiryType = '"+Constants.COUP_EXPIRY_TYPE_DYNAMIC+"' order by 1 ASC ";
		coupList = executeQuery(query, startIdx, endIdx);
		if(coupList!= null && coupList.size()>0) {
			return coupList;
		}
		
		return null;
	}
	public Coupons getConpounByName(String userId,String name) {
		String query = null;
		query="FROM Coupons WHERE userId in("+userId+") AND couponName='"+name.trim()+"'";
		logger.info("Name query::"+query);
		List<Coupons> list = getHibernateTemplate().find(query);	
		if(list!=null && list.size()>0)
    	return list.get(0);
		return null;
	}
}
