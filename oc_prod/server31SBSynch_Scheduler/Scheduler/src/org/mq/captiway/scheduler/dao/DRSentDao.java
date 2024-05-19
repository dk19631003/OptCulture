package org.mq.captiway.scheduler.dao;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import org.mq.captiway.scheduler.beans.DRSent;
import org.mq.captiway.scheduler.utility.Constants;
import org.springframework.jdbc.core.JdbcTemplate;

public class DRSentDao extends AbstractSpringDao {
	
	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);

	private SessionFactory sessionFactory;

	
	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}


	public DRSent find(Long id) {
		return (DRSent) super.find(DRSent.class, id);
	}

	/*public void saveOrUpdate(DRSent drSent) {
		super.saveOrUpdate(drSent);
	}*/

	/*public void delete(DRSent drSent) {
		super.delete(drSent);
	}*/
	
	public int findDRBasedOnDates(Long userId, String fromDate,String toDate) {
		String qry = "";
		
			 qry ="SELECT  COUNT(Distinct emailId) FROM  DRSent  WHERE userId ="+userId.longValue()+" " +
					" AND sentDate >= '"+fromDate+"' AND sentDate <='"+toDate+"'";

		int count =   ((Long)getHibernateTemplate().find(qry).get(0)).intValue();
		
		return count;
	} // findCoupBasedOnDates
	
	public List< Object[]> findDrReports(Long userId,int start,int end,String fromDate,String toDate) {
		
		try {
			List<Object[] > list = null;
			String query  = " SELECT emailId,MAX(sentDate), SUM(opens),SUM(clicks),status,Count(id) FROM DRSent WHERE userId ="+userId.longValue()+" " +
							" AND sentDate >= '"+fromDate+"' AND sentDate <='"+toDate+"'  group by emailId " ;
			
			list = executeQuery(query, start, end);
			
			logger.info("List size is ::"+list.size());
			if(list!= null && list.size()>0) {
				return list;
			}
				
			return null;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::::" , e);
			return null;
		}
	}
	
	
	
	public long findTotSentCount(Long userId,String fromDate,String toDate){
		String qry="SELECT COUNT(id) FROM DRSent WHERE userId ="+userId.longValue()+" " +" AND sentDate >= '"+fromDate+"' AND sentDate <='"+toDate+"'";
		
		Long count =   ((Long)getHibernateTemplate().find(qry).get(0)).longValue();
		
		return count;
		
		
	}
	
	public long findOpenCount(Long userId,String fromDate,String toDate){
		String qry="SELECT COUNT(id) FROM DRSent WHERE userId ="+userId.longValue()+" " +" AND sentDate >= '"+fromDate+"' AND sentDate <='"+toDate+"'" +" AND opens > 0";
		Long count =   ((Long)getHibernateTemplate().find(qry).get(0)).longValue();
		
		return count;
		
		
	}//findOpenCount
	
	public long findClickCount(Long userId,String fromDate,String toDate){
		String qry="SELECT  COUNT(id) FROM DRSent WHERE userId ="+userId.longValue()+" " +" AND sentDate >= '"+fromDate+"' AND sentDate <='"+toDate+"'"+" AND clicks > 0";
		
		Long count =   ((Long)getHibernateTemplate().find(qry).get(0)).longValue();
		
		return count;
		
		
	}//findOpenCount
	
	

	/*public long findTotBounceCount(Long userId,String fromDate,String toDate){
		String qry="SELECT COUNT(emailId) FROM DRSent WHERE userId ="+userId.longValue()+""+
						" AND  status ='" +Constants.DR_STATUS_BOUNCED+"' "+" " +" AND sentDate >= '"+fromDate+"' AND sentDate <='"+toDate+"'";
				
		
		Long count =   ((Long)getHibernateTemplate().find(qry).get(0)).longValue();
		
		return count;
		
		
	}
	
	public long findTotSpammedCount(Long userId,String fromDate,String toDate){
		String qry="SELECT COUNT(emailId) FROM DRSent WHERE userId ="+userId.longValue()+""+
						" AND  status ='" +Constants.DR_STATUS_SPAMMED+"' "+" " +" AND sentDate >= '"+fromDate+"' AND sentDate <='"+toDate+"'";
				
		
		Long count =   ((Long)getHibernateTemplate().find(qry).get(0)).longValue();
		
		return count;
		
		
	}*/
	
	public DRSent findById(long id){
		DRSent deSent = null;
		try {
			List list = getHibernateTemplate().find(
					"from DRSent where id = " + id);
			if (list.size() > 0) {
				deSent = (DRSent) list.get(0);
			}
		} catch (Exception e) {
			logger.error(" ** Exception : ", e ); 
		}
		return deSent;
	}
  
	/*public int updateOpenCount(Long sentId) {
		try {
			String qry=" UPDATE DRSent SET opens = (opens+1) WHERE id = "+ sentId.longValue();
					
			return executeUpdate(qry);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::::" , e);
			return 0;
		}
		
		
		
		
		
	}*/
	/*public int updateClickCount(Long sentId) {
		try {
			String qry=" UPDATE DRSent SET clicks = (clicks+1) WHERE id = "+ sentId.longValue();
					
			return executeUpdate(qry);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::::" , e);
			return 0;
		}
		
		
		
		
		
	}*/
	/*public int updateStaus(long sentId,String status){
		
		int retCnt = 0;
		try {
			
			String qry = "UPDATE DRSent SET status ='"+status+"' WHERE id = "+ sentId;
			retCnt = executeUpdate(qry);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("got  exception** in dao",e);
			return 0;
		}
		 return retCnt;
	}
	*/
  

	
}
