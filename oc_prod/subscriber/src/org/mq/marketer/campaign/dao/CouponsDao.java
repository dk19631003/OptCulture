package org.mq.marketer.campaign.dao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import org.mq.marketer.campaign.beans.Coupons;
import org.mq.marketer.campaign.beans.OrganizationZone;
import org.mq.marketer.campaign.general.Constants;
import org.mq.optculture.model.mobileapp.Lookup;
import org.mq.optculture.utils.OCConstants;
import org.springframework.jdbc.core.JdbcTemplate;

public class CouponsDao extends AbstractSpringDao {

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);

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
	
	
	public  int findTotCountCoupons(Long userId) {
	    	
		///
		String qry ="SELECT  COUNT(*) FROM  Coupons  WHERE userId ="+userId.longValue();
		int count =   ((Long)getHibernateTemplate().find(qry).get(0)).intValue();
		
		
		return count;
	    	
	} // findTotCountCoupons
	
	
	public List<Coupons> findCouponsByUserIdWithLimits(Long userId, int startIdx, int endIdx) {
		List<Coupons> list = null;
		String query = "FROM Coupons  WHERE userId ="+userId.longValue()+" ORDER BY userCreatedDate DESC ";
		
		list  =  executeQuery(query, startIdx, endIdx);
		
		if(list!= null && list.size()>0) {
			return list;
		}
			
		return null;
	}
	
	
	public List<Coupons> findCouponsByOrgId(Long orgId) {

		String query = "FROM Coupons  WHERE orgId ="+orgId+" ORDER BY couponCreatedDate DESC ";
		List<Coupons> list = getHibernateTemplate().find(query);
		
		if(list!= null && list.size()>0) {
			return list;
		}
		return null;
	}
	
	public List<Coupons> findCouponsinautosmsByOrgId(Long orgId) {

		String query = "FROM Coupons  WHERE orgId ="+orgId+" and useasReferralCode = false ORDER BY couponCreatedDate DESC ";
		List<Coupons> list = getHibernateTemplate().find(query);
		
		if(list!= null && list.size()>0) {
			return list;
		}
		return null;
	}
	
	
	public List<Coupons> findrefCouponsByOrgId(Long orgId) {

		String query = "FROM Coupons  WHERE orgId ="+orgId+" and useasReferralCode = true ORDER BY couponCreatedDate DESC ";
		List<Coupons> list = getHibernateTemplate().find(query);
		
		if(list!= null && list.size()>0) {
			return list;
		}
		return null;
	}
	
	public Coupons findrefCouponsByOrgId2(Long orgId) {

		String query = "FROM Coupons  WHERE orgId ="+orgId+" and useasReferralCode is true ORDER BY couponCreatedDate DESC ";
		List<Coupons> list = getHibernateTemplate().find(query);
		
		if(list!= null && list.size()>0) {
			return  list.get(0);
		}
		return null;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	public List<Coupons> findCouponsByStatus(Long orgId) {

		String query = "FROM Coupons  WHERE orgId ="+orgId+" AND status in ('"+Constants.COUP_STATUS_ACTIVE+"','"+Constants.COUP_STATUS_RUNNING+"') ORDER BY couponCreatedDate DESC ";
		List<Coupons> list = getHibernateTemplate().find(query);
		
		if(list!= null && list.size()>0) {
			return list;
		}
		return null;
	}
	
	
	public List<Coupons> findActiveAndRunningCouponsbyOrgId(Long orgId,String lastFetchedDate,String sourceType){
		
		
		List<Coupons> list = new ArrayList<Coupons>();
		try{
			String query = " FROM Coupons WHERE orgId ="+orgId+" AND status in ('"+Constants.COUP_STATUS_ACTIVE+"','"+Constants.COUP_STATUS_RUNNING+"') ";
			
			if(sourceType.equalsIgnoreCase(OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_LOYALTY_APP) || 
					sourceType.equalsIgnoreCase(OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_MOBILE_APP))
				query+=" AND userCreatedDate >= '"+lastFetchedDate+"' ";
			
			 
			query +=" ORDER BY couponCreatedDate DESC ";
			
			logger.info("query"+query);
			list = getHibernateTemplate().find(query);
			if(list != null && list.size() >0){
				return list;
			}
			
		}catch(Exception e){
			logger.error("Exception in find coupons by orgId and couponStatus", e);
			return list;
		}
		
		return list;
	}
public Coupons findCouponsByCodeAndValue(String couponCode,String couponVlaue){
		
		List<Coupons> coupList = null;
		
		try{
			String query = " FROM Coupons WHERE CTCouponCode='"+couponCode+"' AND CTCouponValue ='"+couponVlaue+"'";
			
			coupList = executeQuery(query);
			if(coupList != null && coupList.size() >0){
				return (Coupons)coupList.get(0);
			}
			
		}catch(Exception e){
			logger.error("Exception in find coupons by couponCode and couponValue", e);
			return null;
		}
		
		return null;
	}
	
	public List<Coupons> findCouponsNewByOrgZone(Long userId,String lastFetchedTime, String sourceType,String orgZoneIDs){	
		List<Coupons> list = new ArrayList<Coupons>();
		try{
			String subQuery="";
		/*	String query = " FROM Coupons WHERE orgId ="+orgId+" AND status in ('"+Constants.COUP_STATUS_ACTIVE+"','"+Constants.COUP_STATUS_RUNNING+"') ";
				query += " AND couponCreatedDate >= '"+lastFetchedTime+"' ";
		*/		if(orgZoneIDs!=null && orgZoneIDs.isEmpty()) return list;		
		
				String query = " SELECT  DISTINCT c FROM Coupons c JOIN c.brand b WHERE b IN("+orgZoneIDs+") "
						+ "AND c.userId ="+userId+" AND c.status in ('"+Constants.COUP_STATUS_ACTIVE+"','"+Constants.COUP_STATUS_RUNNING+"') ";				
				query += " AND c.userCreatedDate >= '"+lastFetchedTime+"' ";		
				query += " AND ( c.userLastModifiedDate is null or c.userLastModifiedDate = c.userCreatedDate ) ";
				query += " AND c.enableOffer = true ";
				query += " AND c.mappedOnZone = true ";
				query +=" ORDER BY c.userCreatedDate DESC ";
			
			
			
			logger.info("query"+query);
			list = executeQuery(query); 
			
			return list;
		}catch(Exception e){
			logger.error("Exception in find coupons by orgId and couponStatus", e);
			return list;
		}

	}
	
	
	public List<Coupons> findCouponForValueCode(Long userId,String valueCode){
		
		List<Coupons> list = new ArrayList<Coupons>();
		logger.info("userId==>"+userId);
		logger.info("valueCode==>"+valueCode);
		try{
		String query = "FROM Coupons where userId='"+userId+"' and loyaltyPoints is not null "
				+ "and (requiredLoyltyPoits is not null OR multiplierValue is not null) and valueCode='"+valueCode+"' ";
		logger.info("query==>"+query);
		list = executeQuery(query);
		
		return list;
		}
		catch(Exception e) {
			logger.info("e==>"+e);
			return list;
		}
		
	}
	
public List<Coupons> findCouponsNew(Long userId,String lastFetchedDate,String sourceType){
		
		   
		List<Coupons> list = new ArrayList<Coupons>();
		try{
			String subQuery="";
			String query = " FROM Coupons WHERE userId ="+userId+" AND status in ('"+Constants.COUP_STATUS_ACTIVE+"','"+Constants.COUP_STATUS_RUNNING+"') ";
			query += " AND userCreatedDate >= '"+lastFetchedDate+"' ";
			query += " AND ( userLastModifiedDate is null or userLastModifiedDate = userCreatedDate ) ";
			query += " AND enableOffer = true ";			
			query +=" ORDER BY userCreatedDate DESC ";
			
			logger.info("query"+query);
			list = getHibernateTemplate().find(query);
			if(list != null && list.size() >0){
				return list;
			}
			
		}catch(Exception e){
			logger.error("Exception in find coupons by orgId and couponStatus", e);
			return list;
		}
		
		return list;
	}

public List<Coupons> findCouponsModifiedByOrgZone(Long userId,String lastFetchedTime, String sourceType,String orgZoneIDs){	
	List<Coupons> list = new ArrayList<Coupons>();
	try{
		String subQuery="";
	/*	String query = " FROM Coupons WHERE orgId ="+orgId+" AND status in ('"+Constants.COUP_STATUS_ACTIVE+"','"+Constants.COUP_STATUS_RUNNING+"') ";
			query += " AND couponCreatedDate >= '"+lastFetchedTime+"' ";
	*/				
			if(orgZoneIDs!=null && orgZoneIDs.isEmpty()) return list;		
		
			String query = " SELECT  DISTINCT c FROM Coupons c JOIN c.brand b WHERE b IN("+orgZoneIDs+") "
					+ "AND c.userId ="+userId+" AND c.status in ('"+Constants.COUP_STATUS_ACTIVE+"','"+Constants.COUP_STATUS_RUNNING+"') ";				
			query += " AND c.userLastModifiedDate >= '"+lastFetchedTime+"' ";		
			query += " AND c.userLastModifiedDate != c.userCreatedDate ";
			query += " AND c.enableOffer = true ";
			query += " AND c.mappedOnZone = true ";
			query +=" ORDER BY c.userLastModifiedDate DESC ";
		
		
		
		logger.info("query"+query);
		list = executeQuery(query);
		
		return list;
	}catch(Exception e){
		logger.error("Exception in find coupons by orgId and couponStatus", e);
		return list;
	}

}



public List<Coupons> findCouponsModified(Long userId,String lastFetchedDate,String sourceType,Lookup lookup){
	
	
	List<Coupons> list = new ArrayList<Coupons>();
	try{
		String query = " FROM Coupons WHERE userId ="+userId+" AND status in ('"+Constants.COUP_STATUS_ACTIVE+"','"+Constants.COUP_STATUS_RUNNING+"') ";
		
		if(sourceType.equalsIgnoreCase(OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_LOYALTY_APP) ||
				sourceType.equalsIgnoreCase(OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_MOBILE_APP))
			query+=" AND userLastModifiedDate >= '"+lastFetchedDate+"' ";
		query += " AND userLastModifiedDate != userCreatedDate ";
		query += " AND enableOffer = true ";
		query +=" ORDER BY userLastModifiedDate DESC ";
		
		logger.info("query"+query);
		list = getHibernateTemplate().find(query);
		if(list != null && list.size() >0){
			return list;
		}
		
	}catch(Exception e){
		logger.error("Exception in find coupons by orgId and couponStatus", e);
		return list;
	}
	
	return list;
}
public List<Coupons> findCouponsDeactivatedByOrgZone(Long userId,String lastFetchedTime, String sourceType,String orgZoneIDs){	
	List<Coupons> list = new ArrayList<Coupons>();
	try{
		String subQuery="";
	/*	String query = " FROM Coupons WHERE orgId ="+orgId+" AND status in ('"+Constants.COUP_STATUS_ACTIVE+"','"+Constants.COUP_STATUS_RUNNING+"') ";
			query += " AND couponCreatedDate >= '"+lastFetchedTime+"' ";
	*/		if(orgZoneIDs!=null && orgZoneIDs.isEmpty()) return list;		
		
			String query = " SELECT  DISTINCT c FROM Coupons c JOIN c.brand b WHERE b IN("+orgZoneIDs+") "
					+ "AND c.userId ="+userId+" AND c.status in ('"+Constants.COUP_STATUS_EXPIRED+"','"+Constants.COUP_STATUS_PAUSED+"')";
			query += " AND (c.userLastModifiedDate >= '"+lastFetchedTime+"' or (c.userLastModifiedDate is null and c.userCreatedDate >= '"+lastFetchedTime+"') )";
			query += " AND c.enableOffer = true ";
			query += " AND c.mappedOnZone = true ";
			query +=" ORDER BY c.userLastModifiedDate DESC ";
		
		
		
		logger.info("query"+query);
		list = executeQuery(query);
		
		return list;
	}catch(Exception e){
		logger.error("Exception in find coupons by orgId and couponStatus", e);
		return list;
	}

}



public List<Coupons> findCouponsDeactivated(Long userId,String lastFetchedDate,String sourceType,Lookup lookup){
	
	
	List<Coupons> list = new ArrayList<Coupons>();
	try{
		String query = " FROM Coupons WHERE userId ="+userId+" AND status in ('"+Constants.COUP_STATUS_EXPIRED+"','"+Constants.COUP_STATUS_PAUSED+"') ";
		
		if(sourceType.equalsIgnoreCase(OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_LOYALTY_APP) || 
				sourceType.equalsIgnoreCase(OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_MOBILE_APP))
			query += " AND userLastModifiedDate >= '"+lastFetchedDate+"' ";
		query += " AND enableOffer = true ";
		query +=" ORDER BY userLastModifiedDate DESC ";
		
		logger.info("query"+query);
		list = getHibernateTemplate().find(query);
		if(list != null && list.size() >0){
			return list;
		}
		
	}catch(Exception e){
		logger.error("Exception in find coupons by orgId and couponStatus", e);
		return list;
	}
	
	return list;
}



	
	public List<Coupons> findStaticCouponsByOrgId(Long orgId){
		
		String query = "FROM Coupons  WHERE orgId ="+orgId+" AND expiryType = '"+Constants.COUP_VALIDITY_PERIOD_STATIC+"' ORDER BY couponCreatedDate DESC ";
		List<Coupons> list = getHibernateTemplate().find(query);
		
		if(list!= null && list.size()>0) {
			return list;
		}
		return null;
		
	}
	
	/*public List<Coupons> findCouponsByUserId(Long userId) {

		String query = "FROM Coupons  WHERE userId ="+userId.longValue()+" ORDER BY couponCreatedDate DESC ";
		List<Coupons> list = getHibernateTemplate().find(query);
		
		if(list!= null && list.size()>0) {
			return list;
		}
		return null;
	}*/
	
	
	public int findCouponsBasedOnNames(long orgId,String name) {
		
		String qry ="SELECT  COUNT(*) FROM  Coupons  WHERE orgId ="+orgId+" " +
				" AND couponName='"+name+"' ";
		int count =   ((Long)getHibernateTemplate().find(qry).get(0)).intValue();
		
		return count;
	} // findCoupBasedOnDates
	
	public boolean checkCoupByName (String coupName,Long orgId) {
		
		List<Coupons> coupList = null;
		coupName = StringEscapeUtils.escapeSql(coupName);
		String query = "FROM Coupons  WHERE orgId ="+orgId+" AND couponName ='"+coupName+"'" ;
		logger.info("Coup  query is :::"+query);
		coupList  = getHibernateTemplate().find(query);
		if(coupList != null && coupList.size() >0){
			return true;
		}else {
			return false;
		}
		
	} // findCoupByName
	
	
	
	public List<Coupons> findCouponsByOrgId(Long userOrgId, int startIdx, int endIdx ,String status, String fromDate,String toDate,String orderby_colName,String desc_Asc,String promotionName){
		List<Coupons> list = null;
		logger.info("Entered CouponsByOrg and satus ====>"+status+"---"+fromDate+"---"+toDate+"----");
		String query = "";
		//Changes start 2.5.2.19
		String subQry = Constants.STRING_NILL;
		
		if(fromDate != null && !fromDate.isEmpty() && toDate != null && !toDate.isEmpty() ) {
			
			subQry += " AND userLastModifiedDate BETWEEN  '" + fromDate + "' AND '" + toDate + "'";
		}
		
		if(status != null && !status.isEmpty() && !status.trim().equals("All")) {
			
			subQry += "  AND status='"	+ status + "'";
		}else {
			subQry += " AND status!='Draft'";
		}
		
		 if(promotionName != null && !promotionName.isEmpty()) {
				
				subQry += "  AND couponName like '%"+ promotionName+"%'";
			}
		
		
		
		 query = " FROM Coupons  WHERE orgId ="+userOrgId.longValue() + subQry + " ORDER BY "+orderby_colName+" "+desc_Asc; ;
		
		
		
		
/*		
		if (status.trim().equals("All") && fromDate.equals("") && toDate.equals("")) {
			
			  query = "FROM Coupons  WHERE orgId ="+userOrgId.longValue()+"  ORDER BY "+orderby_colName+" "+desc_Asc;
			  
			  }
		else if (status.equals("All")) {
			
			  query = "FROM Coupons  WHERE orgId ="+userOrgId.longValue()+" " +
			  " AND userLastModifiedDate >= '"+fromDate+"' AND userLastModifiedDate <='"
			  +toDate+"'  ORDER BY "+orderby_colName+" "+desc_Asc;
			  
			  }
		
		else if(!status.trim().equals("All") && fromDate.equals("") && toDate.equals("")) { query =
			  "FROM Coupons  WHERE orgId ="+userOrgId.longValue()+" AND status='"+status+"' ORDER BY "
			  +orderby_colName+" "+desc_Asc ;
			  }
		else if(status.trim().equals("") && !fromDate.equals("") && !toDate.equals("")) { 
			logger.info("Entered Fourth if");

			  query = "FROM Coupons  WHERE orgId ="+userOrgId.longValue()+" " +
			  " AND userLastModifiedDate >= '"+fromDate+"' AND userLastModifiedDate <='"
			  +toDate+"'  ORDER BY "+orderby_colName+" "+desc_Asc;
				  }
		else { query =
			  "FROM Coupons  WHERE orgId ="+userOrgId.longValue()+" " +
			  " AND userLastModifiedDate >= '"+fromDate+"' AND userLastModifiedDate <='"
			  +toDate+"'  AND status='"+status+"' ORDER BY "
			  +orderby_colName+" "+desc_Asc ;
			  }
			 
*/		//Changes end 2.5.2.19
			/*query = "FROM  Coupons  WHERE orgId =" + userOrgId.longValue() + " " + "AND (userLastModifiedDate>='"
					+ fromDate + "' OR userCreatedDate >= '" + fromDate + "') " + "AND (userLastModifiedDate<='"
					+ toDate + "' OR userCreatedDate <= '" + toDate + "')";
		} else {
			query = "FROM  Coupons  WHERE orgId =" + userOrgId.longValue() + " " + "AND (userLastModifiedDate>='"
					+ fromDate + "' OR userCreatedDate >= '" + fromDate + "') " + "AND (userLastModifiedDate<='"
					+ toDate + "' OR userCreatedDate <= '" + toDate + "')AND status='" + status + "'";

		}
*/		list = executeQuery(query, startIdx, endIdx);

		logger.info("List size is  ::" + list.size());
		if (list != null && list.size() > 0) {
			return list;
		}

		return null;
	} // findCouponsByOrgId
	

	public int findCoupBasedOnDates(Long userOrgId, String status, String fromDate, String toDate,String promotionName) {
		logger.info("Entered First If");

		
		logger.info("inside DAO ========>"+userOrgId+"---"+status+"---"+fromDate+"---"+toDate);
		String subQry = Constants.STRING_NILL; 
		
		 if( fromDate != null && !fromDate.isEmpty() && toDate != null && !toDate.isEmpty() ) {
			
			subQry += " AND userLastModifiedDate BETWEEN  '" + fromDate + "' AND '" + toDate + "'";
		}
		 if(status != null && !status.isEmpty() && !status.trim().equals("All")) {
			
			subQry += "  AND status='"	+ status + "'";
		}else {
			subQry += " AND status!='Draft'";
		}
		 
		 if(promotionName != null && !promotionName.isEmpty()) {
				
				subQry += "  AND couponName like '%"+promotionName+"%'";
			}
		
		String qry = "SELECT  COUNT(*) FROM  Coupons  WHERE orgId =" + userOrgId.longValue() + subQry;
		
		
		
/*		
		if (status.trim().equals("All") && fromDate.equals("") && toDate.equals("") ) {
			logger.info("Entered Second If");
				qry = "SELECT  COUNT(*) FROM  Coupons  WHERE orgId =" + userOrgId.longValue() + " ";
				
				qry = "SELECT COUNT(*) FROM  Coupons  WHERE orgId =" + userOrgId.longValue() + " "
						+ "AND (userLastModifiedDate>='" + fromDate + "' OR userCreatedDate >= '" + fromDate + "') "
						+ "AND (userLastModifiedDate<='" + toDate + "' OR userCreatedDate <= '" + toDate + "')";
			}
		else if (status.trim().equals("All")) {
			qry = "SELECT  COUNT(*) FROM  Coupons  WHERE orgId =" + userOrgId.longValue() + " "
					+ " AND userLastModifiedDate >= '" + fromDate + "' AND userLastModifiedDate <='" + toDate + "'";

			qry = "SELECT COUNT(*) FROM  Coupons  WHERE orgId =" + userOrgId.longValue() + " "
					+ "AND (userLastModifiedDate>='" + fromDate + "' OR userCreatedDate >= '" + fromDate + "') "
					+ "AND (userLastModifiedDate<='" + toDate + "' OR userCreatedDate <= '" + toDate + "')";
		}
		//Changes 2.5.2.19 start
		
		 else if(!status.trim().equals("All") && fromDate.equals("") && toDate.equals(""))  {

			qry = "SELECT  COUNT(*) FROM  Coupons  WHERE orgId =" + userOrgId.longValue() + "  AND status='"
					+ status + "'";
			 qry = "SELECT COUNT(*) FROM  Coupons  WHERE orgId =" + userOrgId.longValue() + " "
					+ "AND (userLastModifiedDate>='" + fromDate + "' OR userCreatedDate >= '" + fromDate + "') "
					+ "AND (userLastModifiedDate<='" + toDate + "' OR userCreatedDate <= '" + toDate + "')";
		}
		 else if(status.trim().equals("") && !fromDate.equals("") && !toDate.equals(""))
		 {
			 
				qry = "SELECT  COUNT(*) FROM  Coupons  WHERE orgId =" + userOrgId.longValue() + " "
						+ " AND userLastModifiedDate >= '" + fromDate + "' AND userLastModifiedDate <='" + toDate + "'";
			 
		 }
		
//Changes 2.5.2.19 end
		else {

			qry = "SELECT  COUNT(*) FROM  Coupons  WHERE orgId =" + userOrgId.longValue() + " "
					+ " AND userLastModifiedDate >= '" + fromDate + "' AND userLastModifiedDate <='" + toDate + "'  AND status='"
					+ status + "'";
			 qry = "SELECT COUNT(*) FROM  Coupons  WHERE orgId =" + userOrgId.longValue() + " "
					+ "AND (userLastModifiedDate>='" + fromDate + "' OR userCreatedDate >= '" + fromDate + "') "
					+ "AND (userLastModifiedDate<='" + toDate + "' OR userCreatedDate <= '" + toDate + "')";
		}*/

		int count = ((Long) getHibernateTemplate().find(qry).get(0)).intValue();
		logger.info("Rows=====>"+count);
		return count;
	} // findCoupBasedOnDates
	
	
	
	//Changes start 2.5.2.19
	
	
	/*//------
	public int findCoupBasedOnStatus(Long userOrgId, String status) {
		String qry = "";
		if (status.trim().equals("All")) {
			qry = "SELECT  COUNT(*) FROM  Coupons  WHERE orgId =" + userOrgId.longValue() + " ";

			qry = "SELECT COUNT(*) FROM  Coupons  WHERE orgId =" + userOrgId.longValue() + " "
					+ "AND (userLastModifiedDate>='" + fromDate + "' OR userCreatedDate >= '" + fromDate + "') "
					+ "AND (userLastModifiedDate<='" + toDate + "' OR userCreatedDate <= '" + toDate + "')";
		} else {

			qry = "SELECT  COUNT(*) FROM  Coupons  WHERE orgId =" + userOrgId.longValue() + "  AND status='"
					+ status + "'";
			 qry = "SELECT COUNT(*) FROM  Coupons  WHERE orgId =" + userOrgId.longValue() + " "
					+ "AND (userLastModifiedDate>='" + fromDate + "' OR userCreatedDate >= '" + fromDate + "') "
					+ "AND (userLastModifiedDate<='" + toDate + "' OR userCreatedDate <= '" + toDate + "')";
		}

		int count = ((Long) getHibernateTemplate().find(qry).get(0)).intValue();

		return count;
	}
*//*//---------
	public List<Coupons> findCouponsByOrgId(Long userOrgId, int startIdx, int endIdx ,String status,String orderby_colName,String desc_Asc){
		List<Coupons> list = null;
		String query = "";
		if (status.equals("All")) {
			
			  query = "FROM Coupons  WHERE orgId ="+userOrgId.longValue()+"  ORDER BY "+orderby_colName+" "+desc_Asc;
			  
			  }else { query =
			  "FROM Coupons  WHERE orgId ="+userOrgId.longValue()+" AND status='"+status+"' ORDER BY "
			  +orderby_colName+" "+desc_Asc ;
			  }
			 

			query = "FROM  Coupons  WHERE orgId =" + userOrgId.longValue() + " " + "AND (userLastModifiedDate>='"
					+ fromDate + "' OR userCreatedDate >= '" + fromDate + "') " + "AND (userLastModifiedDate<='"
					+ toDate + "' OR userCreatedDate <= '" + toDate + "')";
		} else {
			query = "FROM  Coupons  WHERE orgId =" + userOrgId.longValue() + " " + "AND (userLastModifiedDate>='"
					+ fromDate + "' OR userCreatedDate >= '" + fromDate + "') " + "AND (userLastModifiedDate<='"
					+ toDate + "' OR userCreatedDate <= '" + toDate + "')AND status='" + status + "'";

		}
		list = executeQuery(query, startIdx, endIdx);

		logger.info("List size is  ::" + list.size());
		if (list != null && list.size() > 0) {
			return list;
		}

		return null;
	} // findCouponsByOrgId
*/	
	
	
	
	
	
	//Changes end 2.5.2.19
	
	
	
	public List<Coupons> getAllCoupons(int startIdx, int endIdx) {
		List<Coupons> coupList = null;
//		logger.info(">>>>>>>> check the coupons exists <<<<<<<<<<<<<<<<<<");
		
		String query = "FROM Coupons order by 1 ASC ";
		//return coupList  = getHibernateTemplate().find("FROM Coupons");	
//		logger.info("===update query is ::"+query);
		coupList = executeQuery(query, startIdx, endIdx);
		if(coupList!= null && coupList.size()>0) {
			return coupList;
		}
			
		return null;
		
	} // getAllCoupons
	
	public List<Coupons> getAllDynamicCoupons(int startIdx, int endIdx) {
		
		List<Coupons> coupList = null;
		String query = "FROM Coupons where expiryType = '"+Constants.COUP_VALIDITY_PERIOD_DYNAMIC+"' order by 1 ASC ";
		coupList = executeQuery(query, startIdx, endIdx);
		if(coupList!= null && coupList.size()>0) {
			return coupList;
		}
		
		return null;
	}
	
	/*public int updateStatistics(Long couponId, Long orgId, boolean isNew ) {
		String subQry = "";
		if(isNew == true) {
			
			subQry = ", (issued=issued+1) ";
			
		}*///is New
		
		
		/*String qry = "UPDATE Coupons set redeemed=(redeemed+1), totRevenue = SUM(totRevenue) "+subQry+" WHERE couponId="+couponId +" AND orgId="+orgId.longValue();
		
		int updateCount = executeUpdate(qry);
		return updateCount;
		
	}
	*/
	
	public boolean isExistCoupCodeBasedOnOrgId (String coupcode,Long orgId) {
		
		List<Coupons> coupList = null;
		String query = "";
		query = "FROM Coupons  WHERE orgId ="+orgId+" AND couponCode ='"+coupcode+"'" ;
		/*if(coupId == 0 ) {
			
		}else {
			query = "FROM Coupons  WHERE orgId ="+orgId+" AND couponCode ='"+coupcode+"' AND couponId NOT IN ("+coupId+")" ;
		}*/
		logger.info("isExistCoupCodeBasedOnOrgId >> Coup  query is :::"+query);
		coupList  = getHibernateTemplate().find(query);
		if(coupList != null && coupList.size() >0){
			return true;
		}else {
			return false;
		}
		
	} // isExistCoupCodeBasedOnOrgId
	
	public Coupons findCoupon(String coupCodeStr,Long orgId){
		List<Coupons> coupList = null;
		String 	query = "FROM Coupons  WHERE orgId ="+orgId+" AND couponGeneratedType='single'  AND couponCode ='"+coupCodeStr+"' " ;
		coupList = getHibernateTemplate().find(query);
	
		if(coupList != null && coupList.size() >0){
			return coupList.get(0);
		}else {
			return null;
		}
	}
	
	public  int getAllCouponsCount() {
    	
		///
		String qry ="SELECT  COUNT(couponId) FROM  Coupons ";
//		logger.info(" &&&&&&&&&&&&& =====coupon count query is  ::"+qry);
		int count =   ((Long)getHibernateTemplate().find(qry).get(0)).intValue();
		
		
		return count;
	    	
	} // findTotCountCoupons
	
	public int getDynamicCoupons(){
		
		String qry ="SELECT COUNT(couponId) FROM Coupons where expiryType = '"+Constants.COUP_VALIDITY_PERIOD_DYNAMIC+"' ";
		int count = ((Long)getHibernateTemplate().find(qry).get(0)).intValue();
	//	logger.info("Dynamic coupon count is-----"+count);
		return count;
	}
	
	public List<Coupons> listOfSinglePromoCoupons(Long orgId) {
		List<Coupons> coupList = null;
		String 	query = "FROM Coupons  WHERE orgId ="+orgId+" AND couponGeneratedType='single'  " +
							"AND (requiredLoyltyPoits is not null OR multiplierValue is not null)  AND loyaltyPoints is not null AND status='Running' " ;
		coupList = getHibernateTemplate().find(query);
    	
		if(coupList != null && coupList.size() >0){
			return coupList;
		}else {
			return null;
		}
    	
    } //listOfSinglePromoCoupons
	public List<Coupons> getAllMultiplePromos(Long orgId) {
		List<Coupons> coupList = null;
		String 	query = "FROM Coupons  WHERE orgId ="+orgId+" AND couponGeneratedType='"+Constants.COUP_GENT_TYPE_MULTIPLE+"'  " +
							"AND (requiredLoyltyPoits IS NULL OR multiplierValue IS NULL)  AND loyaltyPoints IS NULL AND status='Running' " ;
		coupList = executeQuery(query);
    	
		if(coupList != null && coupList.size() >0){
			return coupList;
		}else {
			return null;
		}
    	
    } //listOfSinglePromoCoupons
	
	public List<Coupons> getAllSinglePromos(Long orgId) {
		List<Coupons> coupList = null;
		String 	query = "FROM Coupons  WHERE orgId ="+orgId+" AND couponGeneratedType='single'  " +
							"AND (requiredLoyltyPoits IS NULL OR multiplierValue IS NULL)  AND loyaltyPoints IS NULL AND status='Running' " ;
		coupList = executeQuery(query);
    	
		if(coupList != null && coupList.size() >0){
			return coupList;
		}else {
			return null;
		}
    	
    } //listOfSinglePromoCoupons
	
	public List<Coupons> getAllLoyaltyPromos(Long orgId) {
		
		List<Coupons> coupList = null;
		String 	query = "FROM Coupons  WHERE orgId ="+orgId+" AND couponGeneratedType='single'  " +
							"AND (requiredLoyltyPoits IS NOT NULL OR multiplierValue IS NOT NULL)  AND loyaltyPoints IS NOT NULL AND status='Running' " ;
		coupList = executeQuery(query);
    	
		if(coupList != null && coupList.size() >0){
			return coupList;
		}else {
			return null;
		}
	}
	
	
	public boolean isExistCoupon(Long couponId, String couponName, Long orgId){
		List<Coupons> coupList = null;
		try{
		String 	query = "FROM Coupons  WHERE orgId ="+orgId+" AND couponId="+couponId+" AND couponName ='"+couponName+"' " ;
		coupList = getHibernateTemplate().find(query);
	
		if(coupList != null && coupList.size() ==1){
			return true;
		}else {
			return false;
		}
		}catch(Exception e){
			logger.error("Exception ::" , e);
			return false;
		}
	}
	
	public List<Coupons> isExpireOrPauseCoupList(String coupIds,Long orgId) {
		
		List<Coupons> coupList = null;
		try{
		String 	query = "FROM Coupons  WHERE orgId="+orgId+" AND couponId in("+coupIds+") AND status in ('Expired','Paused')";
		logger.debug(">>1 stime chking"+query);
		coupList = getHibernateTemplate().find(query);
	
		if(coupList != null && coupList.size() >0){
			return coupList;
		}else {
			return null;
		}
		}catch(Exception e){
			logger.error("Exception ::" , e);
			return null;
		}
		
	}
	
	public List<Coupons> isValidCoupScheduledInCamp(String coupIds,Long orgId, String scheduleDate) {
		List<Coupons> coupList = null;
		try{
		String 	query = "FROM Coupons  WHERE orgId="+orgId+" AND couponId in("+coupIds+") AND couponExpiryDate < '"+scheduleDate+"'";
		logger.debug(">>2 stime chking"+query);
		coupList = getHibernateTemplate().find(query);
	
		if(coupList != null && coupList.size() >0){
			return coupList;
		}else {
			return null;
		}
		}catch(Exception e){
			logger.error("Exception ::" , e);
			return null;
		}
	}
	
	public Coupons findCouponsByIdAndName(Long coupId, String coupName){
		
		List<Coupons> coupList = null;
		
		try{
			String query = " FROM Coupons WHERE couponId="+coupId+" AND couponName ='"+coupName+"' " ;
			
			coupList = getHibernateTemplate().find(query);
			if(coupList != null && coupList.size() >0){
				return (Coupons)coupList.get(0);
			}
			
		}catch(Exception e){
			logger.error("Exception in find coupons by couponId and couponName", e);
			return null;
		}
		
		return null;
	}
	
	public Coupons findCouponsById(Long coupId){
		
		List<Coupons> coupList = null;
		
		try{
			String query = " FROM Coupons WHERE couponId="+coupId;
			
			coupList = getHibernateTemplate().find(query);
			if(coupList != null && coupList.size() >0){
				return (Coupons)coupList.get(0);
			}
			
		}catch(Exception e){
			logger.error("Exception in find coupons by couponId and couponName", e);
			return null;
		}
		
		return null;
	}
	
	public List<Coupons> findCouponsByCoupIdsAndOrgId(String coupIds, Long orgId){
		
		List<Coupons> coupList = null;
		
		try{
			String query = " FROM Coupons  WHERE orgId="+orgId+" AND couponId in("+coupIds+")";
			
			coupList = getHibernateTemplate().find(query);
			if(coupList != null && coupList.size() >0){
				return coupList;
			}else {
				return null;
			}
			
		}catch(Exception e){
			logger.error("Exception in find coupons by couponId and couponName", e);
			return null;
		}
		
	}
	public List<Coupons> findDefaultCoupons(Long userOrgId,int startIndex, int endIndex){
		List<Coupons> coupList = null;
		String qry =" FROM  Coupons  WHERE orgId ="+userOrgId.longValue()+" ORDER BY 1 DESC ";
		
		coupList = executeQuery(qry, startIndex, endIndex);
		if(coupList!= null && coupList.size()>0) {
			return coupList;
		}
		return null;
	}
	
	 // added for SMS
	
	public List<Coupons> findById(String coupItStr) throws Exception {
		
		String qry =" FROM Coupons WHERE couponId in ("+coupItStr+")";
		List<Coupons> couponsList = executeQuery(qry);
		if(couponsList.size()>0){
    		return couponsList;
    	}
        return null;
	}
	
	//Added for OptSyn Promos
	public List<Long> findUniqueOrgIds(){
		String qry =" SELECT DISTINCT(orgId) FROM Coupons ";

		List<Long> orgList = executeQuery(qry);
		return orgList;
	}
	
	
	public Coupons findActiveAndRunningCouponsByOrgId(Long coupId){
		
		List<Coupons> coupList = null;
		
		try{
			String query = " FROM Coupons WHERE couponId="+coupId+" AND status in ('"+Constants.COUP_STATUS_ACTIVE+"','"+Constants.COUP_STATUS_RUNNING+"')";
			
			coupList = getHibernateTemplate().find(query);
			if(coupList != null && coupList.size() >0){
				return (Coupons)coupList.get(0);
			}
			
		}catch(Exception e){
			logger.error("Exception in find coupons by couponId and couponName", e);
			return null;
		}
		
		return null;
	}
	
	public List<String> findCouponNames(Long orgId, String couponIdStr){
		
		List<String> couponNames = null;
		String queryStr = null;
		
		if(couponIdStr != null){
			queryStr = " SELECT couponName FROM Coupons WHERE orgId = "+orgId+" AND couponId IN ("+couponIdStr+") AND status in ('"+Constants.COUP_STATUS_ACTIVE+"','"+Constants.COUP_STATUS_RUNNING+"')";
		}
		else{
			queryStr = " SELECT couponName FROM Coupons WHERE orgId = "+orgId+" AND status in ('"+Constants.COUP_STATUS_ACTIVE+"','"+Constants.COUP_STATUS_RUNNING+"')";
		}
		
		try{
			couponNames = getHibernateTemplate().find(queryStr);
			if(couponNames != null && couponNames.size() >0){
				return couponNames;
			}
			else return null;
		}catch(Exception e){
			logger.error("Exception in finding coupon names ...", e);
			return null;
		}
	}
	
	public int getCouponsCountByStatus(Long userId,String status){
		String query;
		if(status.equals("All")) {
			query = "FROM Coupons  WHERE userId ="+userId.longValue();
		}
		else{
			query = "FROM Coupons  WHERE userId ="+userId.longValue()+" AND status LIKE '"+status+"'";
		}
        
		List<Coupons> list = getHibernateTemplate().find(query);	
		return list.size();
		
	}
    public List<Coupons> getCouponsByStatus(Long userId,String status,int startIdx, int endIdx){
		
    	String query;
		if(status.equals("All")) {
			query = "FROM Coupons  WHERE userId ="+userId.longValue()+" ORDER BY userCreatedDate DESC ";
		}
		else{
			query = "FROM Coupons  WHERE userId ="+userId.longValue()+" AND status LIKE '"+status+"' ORDER BY userCreatedDate DESC ";
		}
    	
		List<Coupons> list = executeQuery(query, startIdx, endIdx);
		return list;
		
	}
    public int getCouponsCountByPromoName(Long userId,String couponName){
    	String query = "FROM Coupons  WHERE userId ="+userId.longValue()+" AND  couponName LIKE '%"+couponName+"%'";
		List<Coupons> list = getHibernateTemplate().find(query);	
		return list.size();
    }
    public List<Coupons> getCouponsByPromoName(Long userId,String couponName,int startIdx, int endIdx){
		
        String query = "FROM Coupons  WHERE userId ="+userId.longValue()+" AND couponName LIKE '%"+couponName+"%'"+" ORDER BY userCreatedDate DESC ";
		List<Coupons> list = executeQuery(query, startIdx, endIdx);
		return list;
		
	}
    public List<Coupons> getPromoCodesBetweenCreationDates(String fromDateString, String toDateString, Long userId,int startIdx, int endIdx){
		String query = null;
		List<Coupons> list = null;
		query="FROM Coupons WHERE userId ="+userId.longValue()+" AND userCreatedDate between '"+fromDateString+"' AND '"+toDateString+"' ORDER BY userCreatedDate DESC";
		logger.info(query);
		list=executeQuery(query, startIdx, endIdx);
		return list;
	}
	
	public int getCountByCreationDate(String fromDateString, String toDateString, Long userId){
		String query = null;
		query="FROM Coupons WHERE userId ="+userId.longValue()+" AND userCreatedDate between '"+fromDateString+"' AND '"+toDateString+"'";
		logger.info(query);
		List<Coupons> list = getHibernateTemplate().find(query);	
    	return list.size();
    	
	}
	
	public Calendar getCouponsDateByStatus(Long userOrgId,String status){
		
		String query;
		if(status.equals("All")) {
			query = "FROM Coupons  WHERE orgId ="+userOrgId.longValue()+" ";
		}
		else{
			query = "FROM Coupons  WHERE orgId ="+userOrgId.longValue()+" AND status LIKE '"+status+"' ";
		}
    	
		List<Coupons> list = executeQuery(query, 0 , 1);
		if(list!=null && list.size()>0){
			return list.get(0).getUserCreatedDate();
		}else{
			return null; 
		}
		
	}

	public int findTotCountCouponsofOrg(Long userOrgId, String fromDate, String endDate) {
		 String qry ="SELECT COUNT(*) FROM Coupons WHERE orgId ="+userOrgId.longValue()+" AND status!='Draft' AND userLastModifiedDate between'"
		 +fromDate+"' AND '"+endDate+"'";
		
		 /*String qry = "SELECT COUNT(*) FROM  Coupons  WHERE orgId =" + userOrgId.longValue() + " "
				+ "AND (userLastModifiedDate>='" + fromDate + "' OR userCreatedDate >= '" + fromDate + "') "
				+ "AND (userLastModifiedDate<='" + endDate + "' OR userCreatedDate <= '" + endDate + "')";*/
		int count = ((Long) getHibernateTemplate().find(qry).get(0)).intValue();
		logger.info("count of couponos based on orgId fromDate " + fromDate + " endDate " + endDate);
		logger.info("count " + count);
		return count;
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
	public Coupons findByUserId(Long userId,Long coupId){
    	logger.info("User Id  >>"+userId);
    	List<Coupons> list= getHibernateTemplate().find("FROM Coupons WHERE userId="+userId+" AND couponId ="+coupId);
    	logger.info("list >>"+list.size());
    	if(list != null && list.size() > 0 ){
    		return list.get(0);
    	}
    	return null;
    }
	public Coupons findById(Long cId) {

    	List<Coupons> couponsList = getHibernateTemplate().find("FROM Coupons WHERE couponId = "+ cId);

    	if(couponsList.size()>0){
    		return couponsList.get(0);
    	}
        return null;
    }
   

}
