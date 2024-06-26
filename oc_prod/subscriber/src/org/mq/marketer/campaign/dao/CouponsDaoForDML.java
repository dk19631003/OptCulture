
package org.mq.marketer.campaign.dao;

import java.util.Calendar;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import org.mq.marketer.campaign.beans.Coupons;
import org.mq.marketer.campaign.general.Constants;
import org.springframework.jdbc.core.JdbcTemplate;

public class CouponsDaoForDML extends AbstractSpringDaoForDML {

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);

	private SessionFactory sessionFactory;

	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}


	/*public Coupons find(Long id) {
		return (Coupons) super.find(Coupons.class, id);
	}*/

	public void saveOrUpdate(Coupons coupons) {
		super.saveOrUpdate(coupons);
	}
   public void updateUsedLoyaltyPoint(Long couponId){
	 String query="update coupons cp join (select coupon_id,sum(used_loyalty_points) as ltypoint from coupon_codes where coupon_id="+couponId+") cd on cp.coupon_id=cd.coupon_id set cp.used_loyalty_points=cd.ltypoint";
	 //  String query="update coupons set cp join (select coupon_id,sum(used_loyalty_points) as ltypoint from coupon_codes where coupon_id="+couponId+") cd on cp.coupon_id=cd.coupon_id set used_loyalty_points=cd.ltypoint";
	   logger.info("Total Redeem point update query :"+query);
	   executeJdbcUpdateQuery(query);
   }
	public void delete(Coupons coupons) {
		super.delete(coupons);
	}
	
	
	
	public int updateAvailableCountBasedOnCouponId(Long couponId, Long availCount,Long orgId) {
		String qry = "UPDATE Coupons set available="+availCount+" WHERE couponId="+couponId +" AND orgId="+orgId.longValue();
		int updateCount = executeUpdate(qry);
		return updateCount;
		
	}
	
	/*
	
	public  int findTotCountCoupons(Long userId) {
	    */	
		///
		/*String qry ="SELECT  COUNT(*) FROM  Coupons  WHERE userId ="+userId.longValue();
		int count =   ((Long)getHibernateTemplate().find(qry).get(0)).intValue();
		
		
		return count;
	    	
	}*/ // findTotCountCoupons
	
	
	/*public List<Coupons> findCouponsByUserIdWithLimits(Long userId, int startIdx, int endIdx) {
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
	
	public List<Coupons> findStaticCouponsByOrgId(Long orgId){
		
		String query = "FROM Coupons  WHERE orgId ="+orgId+" AND expiryType = '"+Constants.COUP_VALIDITY_PERIOD_STATIC+"' ORDER BY couponCreatedDate DESC ";
		List<Coupons> list = getHibernateTemplate().find(query);
		
		if(list!= null && list.size()>0) {
			return list;
		}
		return null;
		
	}*/
	
	/*public List<Coupons> findCouponsByUserId(Long userId) {

		String query = "FROM Coupons  WHERE userId ="+userId.longValue()+" ORDER BY couponCreatedDate DESC ";
		List<Coupons> list = getHibernateTemplate().find(query);
		
		if(list!= null && list.size()>0) {
			return list;
		}
		return null;
	}*/
	
	
	/*public int findCouponsBasedOnNames(long orgId,String name) {
		
		String qry ="SELECT  COUNT(*) FROM  Coupons  WHERE orgId ="+orgId+" " +
				" ANDcouponName='"+name+"' ";
		int count =   ((Long)getHibernateTemplate().find(qry).get(0)).intValue();
		
		return count;
	}*/ // findCoupBasedOnDates
	
	/*public boolean checkCoupByName (String coupName,Long orgId) {
		
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
		
	} */// findCoupByName
	
	
	
/*	public List<Coupons> findCouponsByOrgId(Long userOrgId, int startIdx, int endIdx ,String status, String fromDate,String toDate,String orderby_colName,String desc_Asc){
		List<Coupons> list = null;
		String query  = "";
		if(status.equals("All")) {
			query = "FROM Coupons  WHERE orgId ="+userOrgId.longValue()+" " + 
					" AND userCreatedDate >= '"+fromDate+"' AND userCreatedDate <='"+toDate+"'  ORDER BY "+orderby_colName+" "+desc_Asc;
			
		}else {
			query = "FROM Coupons  WHERE orgId ="+userOrgId.longValue()+" " + 
					" AND userCreatedDate >= '"+fromDate+"' AND userCreatedDate <='"+toDate+"'  AND status='"+status+"' ORDER BY "+orderby_colName+" "+desc_Asc ;
			
			
		}
		list = executeQuery(query, startIdx, endIdx);
		
		logger.info("List size is ::"+list.size());
		if(list!= null && list.size()>0) {
			return list;
		}
			
		return null;
	}*/ // findCouponsByOrgId
	

	/*public int findCoupBasedOnDates(Long userOrgId, String status, String fromDate,String toDate) {
		String qry = "";
		if(status.trim().equals("All")) {
			 qry ="SELECT  COUNT(*) FROM  Coupons  WHERE orgId ="+userOrgId.longValue()+" " +
					" AND userCreatedDate >= '"+fromDate+"' AND userCreatedDate <='"+toDate+"'";

		}else {
			
			qry ="SELECT  COUNT(*) FROM  Coupons  WHERE orgId ="+userOrgId.longValue()+" " +
					" AND userCreatedDate >= '"+fromDate+"' AND userCreatedDate <='"+toDate+"'  AND status='"+status+"'";
		}
		
		int count =   ((Long)getHibernateTemplate().find(qry).get(0)).intValue();
		
		return count;
	}*/ // findCoupBasedOnDates
	
	/*
	public List<Coupons> getAllCoupons(int startIdx, int endIdx) {
		List<Coupons> coupList = null;*/
//		logger.info(">>>>>>>> check the coupons exists <<<<<<<<<<<<<<<<<<");
		
		/*String query = "FROM Coupons order by 1 ASC ";*/
		//return coupList  = getHibernateTemplate().find("FROM Coupons");	
//		logger.info("===update query is ::"+query);
		/*coupList = executeQuery(query, startIdx, endIdx);
		if(coupList!= null && coupList.size()>0) {
			return coupList;
		}
			
		return null;
		
	} */// getAllCoupons
	
	/*public List<Coupons> getAllDynamicCoupons(int startIdx, int endIdx) {
		
		List<Coupons> coupList = null;
		String query = "FROM Coupons where expiryType = '"+Constants.COUP_VALIDITY_PERIOD_DYNAMIC+"' order by 1 ASC ";
		coupList = executeQuery(query, startIdx, endIdx);
		if(coupList!= null && coupList.size()>0) {
			return coupList;
		}
		
		return null;
	}*/
	
	/*public int updateStatistics(Long couponId, Long orgId, boolean isNew ) {
		String subQry = "";
		if(isNew == true) {
			
			subQry = ", (issued=issued+1) ";
			
		}//is New
		
		
		String qry = "UPDATE Coupons set redeemed=(redeemed+1), totRevenue = SUM(totRevenue) "+subQry+" WHERE couponId="+couponId +" AND orgId="+orgId.longValue();
		
		int updateCount = executeUpdate(qry);
		return updateCount;
		
	}*/
	
	public int updateTotalCountForNewCouponCodeRedumtion(Long couponsId,int count) {
		try {
			String qry = "UPDATE coupons SET total_qty = (total_qty+"+count+") WHERE coupon_id = "+couponsId;
			int updateCount = executeJdbcUpdateQuery(qry);
			return updateCount;
		} catch (Exception e) {
			logger.error("updateTotalCountForNewCouponCodeRedumtion ::",e);
			return 0;
		}
	}
	
	
	
	
	/*
	public boolean isExistCoupCodeBasedOnOrgId (String coupcode,Long orgId) {
		
		List<Coupons> coupList = null;
		String query = "";
		query = "FROM Coupons  WHERE orgId ="+orgId+" AND couponCode ='"+coupcode+"'" ;
	*/	/*if(coupId == 0 ) {
			
		}else {
			query = "FROM Coupons  WHERE orgId ="+orgId+" AND couponCode ='"+coupcode+"' AND couponId NOT IN ("+coupId+")" ;
		}*/
		/*logger.info("isExistCoupCodeBasedOnOrgId >> Coup  query is :::"+query);
		coupList  = getHibernateTemplate().find(query);
		if(coupList != null && coupList.size() >0){
			return true;
		}else {
			return false;
		}
		
	} */// isExistCoupCodeBasedOnOrgId
	
	/*public Coupons findCoupon(String coupCodeStr,Long orgId){
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
    */	
		///
	/*	String qry ="SELECT  COUNT(couponId) FROM  Coupons ";
*///		logger.info(" &&&&&&&&&&&&& =====coupon count query is  ::"+qry);
/*		int count =   ((Long)getHibernateTemplate().find(qry).get(0)).intValue();
		
		
		return count;
	    	
	}*/ // findTotCountCoupons
	
	/*public int getDynamicCoupons(){
		
		String qry ="SELECT COUNT(couponId) FROM Coupons where expiryType = '"+Constants.COUP_VALIDITY_PERIOD_DYNAMIC+"' ";
		int count = ((Long)getHibernateTemplate().find(qry).get(0)).intValue();*/
	//	logger.info("Dynamic coupon count is-----"+count);
		/*return count;
	}*/
	/*
	public List<Coupons> listOfSinglePromoCoupons(Long orgId) {
		List<Coupons> coupList = null;
		String 	query = "FROM Coupons  WHERE orgId ="+orgId+" AND couponGeneratedType='single'  " +
							"AND requiredLoyltyPoits is not null  AND loyaltyPoints is not null AND status='Running' " ;
		coupList = getHibernateTemplate().find(query);
    	
		if(coupList != null && coupList.size() >0){
			return coupList;
		}else {
			return null;
		}
    	
    }*/ //listOfSinglePromoCoupons
/*	public boolean isExistCoupon(Long couponId, String couponName, Long orgId){
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
*/	
	 // added for SMS
	
/*	public List<Coupons> findById(String coupItStr) throws Exception {
		
		String qry =" FROM Coupons WHERE couponId in ("+coupItStr+")";
		List<Coupons> couponsList = executeQuery(qry);
		if(couponsList.size()>0){
    		return couponsList;
    	}
        return null;
	}
*/	
	//Added for OptSyn Promos
/*	public List<Long> findUniqueOrgIds(){
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

	public int findTotCountCouponsofOrg(Long userOrgId) {
		String qry ="SELECT  COUNT(*) FROM  Coupons  WHERE orgId ="+userOrgId.longValue();
		int count =   ((Long)getHibernateTemplate().find(qry).get(0)).intValue();
		
		
		return count;
	}
	
*/	
}
