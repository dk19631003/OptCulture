package org.mq.marketer.campaign.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import org.mq.marketer.campaign.beans.CouponCodes;
import org.mq.marketer.campaign.beans.Coupons;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.general.Constants;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

public class CouponCodesDao extends AbstractSpringDao {

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);

	private SessionFactory sessionFactory;

	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}


	public CouponCodes find(Long id) {
		return (CouponCodes) super.find(CouponCodes.class, id);
	}

	/*public void saveOrUpdate(CouponCodes couponcodes) {
		super.saveOrUpdate(couponcodes);
	}

	public void delete(CouponCodes couponcodes) {
		super.delete(couponcodes);
	}*/
	public CouponCodes findById(Long coupId){
		
			String qry="FROM CouponCodes where couponId="+coupId+" ";
			List<CouponCodes> coupCodeList = getHibernateTemplate().find(qry);
	    	if(coupCodeList.size() > 0)
	    		return coupCodeList.get(0);
	    	else 
	    		return null;
		
	}
	
	public  int findTotCountCouponCodes(long coupCodeId) {
	    	
		
	/*	String qry ="SELECT COUNT(*) FROM  CouponCodes  WHERE  couponId ="+coupCodeId + 
				" AND status NOT IN('"+Constants.COUP_CODE_STATUS_INVENTORY+"') group by doc_sid";*/
		String qry ="SELECT COUNT(*) FROM  CouponCodes  WHERE  couponId ="+coupCodeId + 
				" AND status  IN('"+Constants.COUP_CODE_STATUS_REDEEMED+"','"+Constants.COUP_CODE_STATUS_ACTIVE+"') group by doc_sid";
	
		//int count =   ((Long)getHibernateTemplate().find(qry).get(0)).intValue();
		
		List resultList  = getHibernateTemplate().find(qry);
		if(resultList!=null)
			return resultList.size();
		else return 0;
	    	
	} // findTotCountCoupons
	
	
		/*public List<CouponCodes> findByCouponCode(Long coupCodeId,int startIdx,int endIdx) {
			List<CouponCodes> coupCodesList = null;
			String query = "FROM CouponCodes WHERE  couponId ="+coupCodeId+" AND status NOT IN('"+Constants.COUP_CODE_STATUS_INVENTORY+"') ORDER BY redeemedOn DESC "; 
			
			coupCodesList = executeQuery(query, startIdx, endIdx);
			
			if(coupCodesList != null && coupCodesList.size() >0) {
				return coupCodesList;
			}
			return null;
		
	} // findByCouponCode
*/	
	
	

	public List<CouponCodes> findByCouponCode(Long coupCodeId,int startIdx,int endIdx) {
		try {
			List<CouponCodes> coupCodesList = null;
			//String query = "FROM CouponCodes WHERE  couponId ="+coupCodeId+" AND status NOT IN('"+Constants.COUP_CODE_STATUS_INVENTORY+"') ORDER BY redeemedOn DESC "; 

			//String query = "FROM CouponCodes WHERE  couponId ="+coupCodeId+" AND status NOT IN('"+Constants.COUP_CODE_STATUS_INVENTORY+"') GROUP BY docSid ORDER BY redeemedOn DESC ";
			
			/*String query = "select cc1 from coupon_codes cc1 join "
			+ "(select coupon_code_id,doc_sid, max(redeemed_on) as max_redeemed_on from coupon_codes where coupon_id="+coupCodeId+""
					+ "and status not in ('"+Constants.COUP_CODE_STATUS_INVENTORY+"') group by doc_sid ) cc2 on cc1.doc_sid=cc2.doc_sid"
					+ " where 	 cc1.coupon_id="+coupCodeId+" and cc1.status not in ('"+Constants.COUP_CODE_STATUS_INVENTORY+"') "
							+ "and cc2.max_redeemed_on=cc1.redeemed_on Group by cc1.doc_sid,cc1.redeemed_on";*/
			
			/*String query1 = "select cc1 from coupon_codes cc1 join "
					+ "(select coupon_code_id,doc_sid, max(redeemed_on) as max_redeemed_on from coupon_codes where coupon_id="+coupCodeId+""
							+ "and status not in ('"+Constants.COUP_CODE_STATUS_INVENTORY+"') group by doc_sid ) cc2 on cc1.doc_sid=cc2.doc_sid"
							+ " where 	 cc1.coupon_id="+coupCodeId+" and cc1.status not in ('"+Constants.COUP_CODE_STATUS_INVENTORY+"') "
									+ "and cc2.max_redeemed_on=cc1.redeemed_on Group by cc1.doc_sid,cc1.redeemed_on";*/
			
			
			
			
			
			/*String query = "from CouponCodes cc1 where docSid in(select docSid from CouponCodes where couponId="+coupCodeId+""
					+ " and status not in ('Inventory') group by docSid ) and redeemedOn in(select max(redeemedOn) as max_redeemedOn from CouponCodes where couponId="+coupCodeId+" "
							+ "and status not in ('Inventory') group by docSid) order by redeemedOn desc";
			
			coupCodesList = executeQuery(query, startIdx, endIdx);*/

			String query = "select cc1.* from coupon_codes cc1 join "
			+ "(select coupon_code_id,doc_sid,receipt_number, max(redeemed_on) as max_redeemed_on from coupon_codes where coupon_id="+coupCodeId+""
					+ " and status not in ('"+Constants.COUP_CODE_STATUS_INVENTORY+"') group by doc_sid ) cc2 on cc1.doc_sid=cc2.doc_sid"
					+ " where 	 cc1.coupon_id="+coupCodeId+" and cc1.status not in ('"+Constants.COUP_CODE_STATUS_INVENTORY+"') "
							+ "and cc2.max_redeemed_on=cc1.redeemed_on Group by cc1.doc_sid,cc1.redeemed_on order by cc1.redeemed_on desc limit "+startIdx+","+endIdx;
			// limit "+startIdx+","+endIdx+"
			logger.info("Coupon Query ::"+query); 
			coupCodesList = jdbcTemplate.query(query, new RowMapper() {
		        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
		        	CouponCodes couponCodesObj = new CouponCodes();
		            
		        	Coupons cp=new Coupons();
		        	cp.setCouponId(rs.getLong("coupon_id"));
		        	couponCodesObj.setCouponId(cp);
		        	couponCodesObj.setCouponCode(rs.getString("coupon_code"));
		        	couponCodesObj.setStatus(rs.getString("status"));
		        	couponCodesObj.setIssuedTo(rs.getString("issued_to"));
		        	couponCodesObj.setRedeemedTo(rs.getString("redeemed_to"));
		        	couponCodesObj.setCampaignName(rs.getString("campaign_name"));
		        	
		        	Calendar cal = Calendar.getInstance();
	            	
		        	if(rs.getTimestamp("issued_on")!=null){
		        		cal.setTime(rs.getTimestamp("issued_on"));
		        		couponCodesObj.setIssuedOn(cal);
		        	}else{
		        		couponCodesObj.setIssuedOn(null);
		        	}
		        	if(rs.getTimestamp("redeemed_on")!=null){
	            	cal.setTime(rs.getTimestamp("redeemed_on"));
	            	couponCodesObj.setRedeemedOn(cal);
		        	}else{
		        		couponCodesObj.setRedeemedOn(null);
		        	}
	            	couponCodesObj.setStoreNumber(rs.getString("store_number"));
	            	couponCodesObj.setDocSid(rs.getString("doc_sid"));
	            	couponCodesObj.setReceiptNumber(rs.getString("receipt_number"));
	            	couponCodesObj.setTotDiscount(rs.getDouble("tot_discount"));
	            	couponCodesObj.setTotRevenue(rs.getDouble("tot_revenue"));
	            	couponCodesObj.setUsedLoyaltyPoints(rs.getDouble("used_loyalty_points"));
	            	
		    	 return couponCodesObj;
		        }
		    });
			
			
			if(coupCodesList != null && coupCodesList.size() >0) {
				return coupCodesList;
			}
			return null;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			
			logger.error("Exception ",e);
			return null;
			
		}

	} // findByCouponCode

	
	public int getCountByDocsId(Long couponID, String code, String docSid) {

		String query=null;

		if(docSid!=null && !docSid.isEmpty()) {
			query = "SELECT  COUNT(docSid) FROM CouponCodes WHERE couponId="+couponID+" AND couponCode='"+code+"' AND docSid ='"+docSid+"'";
		}
		else {
			return 1;
		}

		int count =   ((Long)getHibernateTemplate().find(query).get(0)).intValue();

		return count;

	} // findByCouponCode

	
	
	
	/**
	 * 
	 * @param couponId
	 * @param status if null returned all cc count
	 * @return count as a long value
	 */
	public long getCouponCodeCountByStatus(Long couponId, String status) {
		
		String query=null;

		if(status==null || status.trim().equalsIgnoreCase("All")) {
			 query = "SELECT  COUNT(ccId) FROM CouponCodes WHERE couponId ="+couponId;
		}
		else {
		 query = "SELECT  COUNT(ccId) FROM CouponCodes WHERE couponId ="+couponId+" AND status ='"+status.trim()+"' ";
		}

		long count =  ((Long)getHibernateTemplate().find(query).get(0)).longValue();
		
		return count;
		
	} // findByCouponCode

	
	/**
	 * 
	 * @param cCode
	 * @param orgId
	 * @return
	 */
	public CouponCodes testForCouponCodes(String cCode, long orgId) {
		List<CouponCodes> list = null;
		String query = "FROM CouponCodes  WHERE orgId ="+orgId+" AND couponCode ='"+cCode+"'";
		list  = getHibernateTemplate().find(query);
		if(list!= null && list.size()>0) {
			return list.get(0);
		}
			
		return null;
	}
	/** find coupon codes object based on coupon code and organizaion 
	 * @param coupCode
	 * @param orgId
	 * @return
	 */
	public CouponCodes findByCouponCode(String coupCode, Long orgId,String status) {
		
		try {
			String qry = "FROM CouponCodes WHERE orgId ="+orgId +" AND couponCode ='"+coupCode+"' AND status ='"+status+"'";
			
			List<CouponCodes> tempList = null;
			
			tempList = executeQuery(qry, 0, 1);
			if(tempList != null && tempList.size() > 0) {
				
				return tempList.get(0);
			}
				
				return null;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
			return null;
		}
		
	}
	
	public long findRedeemdCountByCoupId(Long CouponId) {
		String qry = "SELECT COUNT(ccId) FROM  CouponCodes WHERE couponId ="+CouponId+"  AND status  IN('"+Constants.COUP_CODE_STATUS_REDEEMED+"')";
		logger.info("findRedeemdCountByCoupId quuery is >>::"+qry);
		long count=((Long)getHibernateTemplate().find(qry).get(0)).longValue();
		return count;
	} // findRedeemdCountByCoupId

/** find total issued coupons based on coupon Object
 * @param couponObj
 * @return
 */
/*public long findIssuedCoupCodeByCoup(Long CouponId){
	
	String qry = "SELECT COUNT(ccId) FROM  CouponCodes WHERE couponId ="+CouponId+"  AND status NOT IN('"+Constants.COUP_CODE_STATUS_INVENTORY+"')";
	
	long count=((Long)getHibernateTemplate().find(qry).get(0)).longValue();
	return count;
}//findIssuedCoupCodeByCoup
*/


/** find total redeemed coupons based on status and coupon Object
 * @param couponObj
 * @param status
 * @return
 */
public long findRedeemdCoupCodeByCoup(Long CouponId,Long orgId,String status){
	
	String qry = "SELECT COUNT(ccId) FROM  CouponCodes WHERE couponId ="+CouponId+" AND status='"+status+"'" ;
	
	long count=((Long)getHibernateTemplate().find(qry).get(0)).longValue();
	return count;
}//findRedeemdCoupCodeByCoup

/** find cumulative Total revenue of each individual coupon
 * that value stored in  top level coupon report screen
 * @param couponId
 * @return
 */
public double  findTotRevenue(Long couponId,Long orgId){
	
	String qry="SELECT SUM(totRevenue) FROM CouponCodes WHERE  couponId ="+couponId;
	List<Double> retiList = executeQuery(qry);
	
	if(retiList != null && !retiList.isEmpty()){
		
		double  totRevenue= retiList.get(0) != null ? ((Double)retiList.get(0)).doubleValue() : 0;
		return totRevenue;
	}
	//double  totRevenue=()getHibernateTemplate().find(qry).get(0);
	return 0.0;
	
}

// find coup[on code by email id
public List<CouponCodes> findCouponCodeByEmail(String coupCode,Long orgId,String email) {
	try {
		String qry = "FROM CouponCodes WHERE orgId ="+orgId +" AND couponCode ='"+coupCode+"' AND issuedTo ='"+email+"' " ;
		
		List<CouponCodes> tempList = null;
		tempList = getHibernateTemplate().find(qry);
		
		if(tempList != null && tempList.size() > 0) {
			return tempList;
		}
			
		return null;
	} catch (Exception e) {
		// TODO Auto-generated catch block
		logger.error("Exception ::" , e);
		return null;
	}
	
}
// find coupon coide by phone
public List<CouponCodes> findCouponCodeByPhone(String coupCode,Long orgId,String phone){
	try {
		String qry = "FROM CouponCodes WHERE orgId ="+orgId +" AND couponCode ='"+coupCode+"' AND issuedTo ='"+phone+"' " ;
		
		List<CouponCodes> tempList = null;
		
		tempList = getHibernateTemplate().find(qry);
		
		if(tempList != null && tempList.size() > 0) {
			return tempList;
		}
			
		return null;
	} catch (Exception e) {
		// TODO Auto-generated catch block
		logger.error("Exception ::" , e);
		return null;
	}
	
}
// find coupon code by customer id

public List<CouponCodes> findCouponCodeByCustId(String coupCode,Long orgId,String custId){
	try {
		String qry = "FROM CouponCodes WHERE orgId ="+orgId +" AND couponCode ='"+coupCode+"' AND issuedTo ='"+custId+"' " ;
		
		List<CouponCodes> tempList = null;
		
		tempList = getHibernateTemplate().find(qry);
		
		if(tempList != null && tempList.size() > 0) {
			return tempList;
		}
			
		return null;
	} catch (Exception e) {
		// TODO Auto-generated catch block
		logger.error("Exception ::" , e);
		return null;
	}
	
}


public long findIssuedCoupCodeByCoup(Long CouponId) {

	String qry = "SELECT COUNT(ccId) FROM  CouponCodes WHERE couponId ="
			+ CouponId + "  AND status NOT IN('"
			+ Constants.COUP_CODE_STATUS_INVENTORY + "')";

	long count = ((Long) getHibernateTemplate().find(qry).get(0))
			.longValue();

	return count;
}

//changed for intimacy's requirement
public List<Object[]>  getCouponCodeObj(Long coupId,int firstResult,int maxResults){
	  try {
		  
		  String query = "select couponCode,status,issuedTo,redeemedTo,campaignName,issuedOn,redeemedOn,totDiscount,totRevenue,contactId from CouponCodes  where couponId="+coupId+" AND status NOT IN('"
			+ Constants.COUP_CODE_STATUS_INVENTORY + "') ORDER BY issuedOn DESC";
		  logger.debug(">> query is  ::"+query);
		  List cList = executeQuery(query, firstResult, maxResults);
		  return cList;
	  } catch (Exception e) {
		  logger.error("** Error : " + e.getMessage() + " **");
		  return null;
	  } 
}

	public List<Object[]>  getCouponCodeObj_old(Long coupId,int firstResult,int maxResults){
		  try {
			  
			  
			  
			  String query = "select cc.issued_o, c.*  from CouponCodes cc, Contacts c  where cc.couponId="+coupId+" c. AND status NOT IN('"
				+ Constants.COUP_CODE_STATUS_INVENTORY + "') ORDER BY issuedOn DESC";
			  List cList = executeQuery(query, firstResult, maxResults);
			  return cList;
		  } catch (Exception e) {
			  logger.error("** Error : " + e.getMessage() + " **");
			  return null;
		  } 
	}

	public CouponCodes findCoupCodeByContactId(String contId,Long orgId, String couponCodeStr) {
		
		String qry = "FROM CouponCodes WHERE orgId ="+orgId +" AND couponCode ='"+couponCodeStr+"' AND status NOT IN('"
			+ Constants.COUP_CODE_STATUS_REDEEMED + "', '"+Constants.COUP_CODE_STATUS_EXPIRED+"')  AND contactId  in ("+contId+")" ;
		
		logger.debug(" finding Contact Id is  :::::::::"+qry);
		List<CouponCodes> tempList = null;
		
		tempList = getHibernateTemplate().find(qry);
		
		if(tempList != null && tempList.size() > 0) {
			return tempList.get(0);
		}
			
		return null;
		
	}
	
	
	public List<CouponCodes> findCoupCodeLstByContactId(String contId,Long orgId, String couponCodeStr) {
		
		String qry = "FROM CouponCodes WHERE orgId ="+orgId +" AND couponCode ='"+couponCodeStr+"'  AND contactId  in ("+contId+")" ;
		
		logger.debug(" finding Contact Id is  :::::::::"+qry);
		List<CouponCodes> tempList = null;
		
		tempList = getHibernateTemplate().find(qry);
		
		if(tempList != null && tempList.size() > 0) {
			return tempList;
		}
			
		return null;
		
	}
	
	public List<CouponCodes> isPromoExistForRedeem(String custIdStr,String emailId,String phone, String promoStr,Long orgId) {
		List<CouponCodes> promoList = null;
		
		
		String appnStr = "";
		if(custIdStr != null && custIdStr.trim().length() > 0 && emailId != null && 
				emailId.trim().length() > 0 && phone != null && phone.trim().length() > 0 ){
			appnStr = "( redeemCustId like  '%"+custIdStr+"%' AND( redeemEmailId like  '%"+emailId+"%'  OR redeemPhnId like  '%"+phone+"%'))";
			
		}else if(custIdStr != null && custIdStr.trim().length() > 0 && emailId != null && emailId.trim().length() > 0){
			appnStr = "( redeemCustId like  '%"+custIdStr+"%' AND redeemEmailId like  '%"+emailId+"%')";
		}else if(custIdStr != null && custIdStr.trim().length() > 0 && phone != null && phone.trim().length() > 0){
			appnStr = "( redeemCustId like  '%"+custIdStr+"%' AND redeemPhnId like  '%"+phone+"%')";
		}else if(emailId != null && emailId.trim().length() > 0 && phone != null && phone.trim().length() > 0 ){
			appnStr = "(  redeemEmailId like  '%"+emailId+"%' OR redeemPhnId like  '%"+phone+"%')";
		}else if(custIdStr != null && custIdStr.trim().length() > 0 && (emailId == null || emailId.trim().length() ==0 &&  phone == null || phone.trim().length() == 0)){
			appnStr = "( redeemCustId like  '%"+custIdStr+"%')";
		}else if(emailId != null && emailId.trim().length() > 0 && (custIdStr == null || custIdStr.trim().length() ==0 &&  phone == null || phone.trim().length() == 0)){
			appnStr = "( redeemEmailId like  '%"+emailId+"%')";
		}else if(phone != null && phone.trim().length() > 0 && (custIdStr == null || custIdStr.trim().length() ==0 &&  emailId == null || emailId.trim().length() == 0)){
			appnStr = "( redeemPhnId like  '%"+phone+"%')";
		}
		
		String qry = "FROM CouponCodes WHERE orgId ="+orgId +" AND couponCode ='"+promoStr+"'  AND status IN('"+ Constants.COUP_CODE_STATUS_REDEEMED + "') "+(!appnStr.isEmpty() ? (" AND " +appnStr ) : appnStr);
		
		logger.debug(" finding Contact Id is  :::::::::"+qry);
		promoList = getHibernateTemplate().find(qry);
		return promoList;
		
	}
	
	public List<CouponCodes> isPromoExistForRedeem(String custIdStr,String emailId,String phone, String promoStr,Long orgId, String cardNumber) {
		List<CouponCodes> promoList = null;
		
		
		String appnStr = "";
		if(cardNumber != null && !cardNumber.isEmpty() ){
			appnStr = "( membership=  '"+cardNumber+"' )";
			
		}
		else if(custIdStr != null && custIdStr.trim().length() > 0 && emailId != null && 
				emailId.trim().length() > 0 && phone != null && phone.trim().length() > 0 ){
			appnStr = "( redeemCustId like  '%"+custIdStr+"%' AND ( redeemEmailId like  '%"+emailId+"%'  OR redeemPhnId like  '%"+phone+"%'))";
			
		}else if(custIdStr != null && custIdStr.trim().length() > 0 && emailId != null && emailId.trim().length() > 0){
			appnStr = "( redeemCustId like  '%"+custIdStr+"%' AND redeemEmailId like  '%"+emailId+"%')";
		}else if(custIdStr != null && custIdStr.trim().length() > 0 && phone != null && phone.trim().length() > 0){
			appnStr = "( redeemCustId like  '%"+custIdStr+"%' AND redeemPhnId like  '%"+phone+"%')";
		}else if(emailId != null && emailId.trim().length() > 0 && phone != null && phone.trim().length() > 0 ){
			appnStr = "(  redeemEmailId like  '%"+emailId+"%' OR redeemPhnId like  '%"+phone+"%')";
		}else if(custIdStr != null && custIdStr.trim().length() > 0 && (emailId == null || emailId.trim().length() ==0 &&  phone == null || phone.trim().length() == 0)){
			appnStr = "( redeemCustId like  '%"+custIdStr+"%')";
		}else if(emailId != null && emailId.trim().length() > 0 && (custIdStr == null || custIdStr.trim().length() ==0 &&  phone == null || phone.trim().length() == 0)){
			appnStr = "( redeemEmailId like  '%"+emailId+"%')";
		}else if(phone != null && phone.trim().length() > 0 && (custIdStr == null || custIdStr.trim().length() ==0 &&  emailId == null || emailId.trim().length() == 0)){
			appnStr = "( redeemPhnId like  '%"+phone+"%')";
		}
		
		String qry = "FROM CouponCodes WHERE orgId ="+orgId +" AND couponCode ='"+promoStr+"'  AND status IN('"+ Constants.COUP_CODE_STATUS_REDEEMED + "') "+(!appnStr.isEmpty() ? (" AND " +appnStr ) : appnStr);
		
		logger.debug(" finding Contact Id is  :::::::::"+qry);
		promoList = getHibernateTemplate().find(qry);
		return promoList;
		
	}
	
	
	
	public List<CouponCodes> findByCouponId(Long couponId,int startIdx, int endIdx) {
		List<CouponCodes> coupCodesList = null;
		String query = "FROM CouponCodes WHERE  couponId ="+couponId+" AND status NOT IN('"+Constants.COUP_CODE_STATUS_REDEEMED+"') ORDER BY ccId DESC "; 
		logger.info("CouponCodes findByCouponId query is  :"+query);
		coupCodesList = executeQuery(query, startIdx, endIdx);
		
		if(coupCodesList != null && coupCodesList.size() >0) {
			logger.info("size is  ::"+coupCodesList.size());
			return coupCodesList;
		}
		return null;
		
	}
	
	
	
	public long findCoupCodeCountByCoupAndStatus(Long CouponId,String status){
		String query ="";
			  
			  //query = "SELECT COUNT(ccId) FROM  CouponCodes WHERE  couponId ="+CouponId+" AND status in ('"+Constants.COUP_CODE_STATUS_INVENTORY+"')";
			  query = "SELECT COUNT(ccId) FROM  CouponCodes WHERE  couponId ="+CouponId+" AND status  in ("+status+")"; 
			  logger.info("count qury is ::"+query);
		long count=((Long)getHibernateTemplate().find(query).get(0)).longValue();
		return count;
	}//findRedeemdCoupCodeByCoup
	
	
	public List<Object[]>  getCouponCodeByCond(Long coupId ,int firstResult, int maxResults){
		  try {
			  
			  List cList = null;
			  
			  /*String query = "select contact_id, COUNT(ccId)  from  CouponCodes WHERE couponId="+coupId+"  "
			  		+ " AND status IN ('"+ Constants.COUP_CODE_STATUS_REDEEMED + "') AND contactId is not null GROUP BY contactId";*/
			  
			  String query = "select contact_id, COUNT(coupon_code_id)  from  coupon_codes WHERE coupon_id="+coupId+"  "
				  		+ " AND status IN ('"+ Constants.COUP_CODE_STATUS_REDEEMED + "') AND contact_id is not null GROUP BY contact_id";
			  
			  cList = jdbcTemplate.query(query, new RowMapper() {
					 public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
						 
						 Object[] object =  new Object[2];
						 object[0] = rs.getLong(1);
						 object[1] = rs.getInt(2);
						 
						return object;
					 }
					
				});
				
				
			  
			  
			 // List cList = executeQuery(query, firstResult, maxResults);
			  return cList;
		  } catch (Exception e) {
			  logger.error("Exception ",e);
			  logger.error("** Error : " + e.getMessage() + " **");
			  return null;
		  } 
	}
	
	public Long getCouponCodeCountByCond(Long coupId ){
		
		 try {
			  String query = "from CouponCodes WHERE couponId="+coupId+"  AND status IN('"+ Constants.COUP_CODE_STATUS_REDEEMED + "') GROUP BY contactId";
			  
			 // System.out.println("query is  >>"+query);
			  List returnList = getHibernateTemplate().find(query);
			  if(returnList == null || returnList.size() == 0){
				  return 0l;
			  }else {
				  long count=returnList.size();
				  return count;
			  }
		 } catch (Exception e) {
			 logger.error("Exception ",e);
			  logger.error("** Error : " + e.getMessage() + " **");
			  return 0l;
		  } 
			  
	}

	public List<CouponCodes> findCouponCodeByDocSid(String docSid, Long orgId) {
		List<CouponCodes> couponCodesList = null;
		 try {
			  String query = "from CouponCodes WHERE  orgId = "+orgId+" AND docSid= '"+docSid+"'  ";
			  logger.info("coupon codes list ......................:"+query);
			  couponCodesList = getHibernateTemplate().find(query);
			
			  logger.info("coupon codes list size ::::"+couponCodesList.size());
		 } catch (Exception e) {
			  logger.error("** Exception : " ,e);
		 }
		 return couponCodesList;
	}//findCouponCodeByDocSid

	public CouponCodes findByDocSid(String docSidStr, String reqCoupcode,Long orgId) {
		List<CouponCodes> couponCodesList = null;
		 try {
			  String query = "FROM CouponCodes WHERE  orgId = "+orgId+" AND couponCode = '"+reqCoupcode+"' AND docSid= '"+docSidStr+"' AND status= '"+ Constants.COUP_CODE_STATUS_REDEEMED +"' "
			  				 + " " ;
			  logger.info("coupon codes list ......................:"+query);
			  couponCodesList = getHibernateTemplate().find(query);
			  if(couponCodesList != null && couponCodesList.size() > 0){
					return couponCodesList.get(0);
				}
				else return null;
		 } catch (Exception e) {
			  logger.error("** Exception : " ,e);
		 }
		 return null;
	}

	public int findTotalStoreCount(Long userOrgId, String fromDateString,
			String toDateString) {
		
		String qry ="SELECT  COUNT(DISTINCT storeNumber) FROM  CouponCodes  WHERE orgId ="+userOrgId+" AND " +
				"storeNumber IS NOT NULL AND redeemedOn between '"+fromDateString+"' AND '"+toDateString+"'";
		
		
		if(fromDateString.equals(Constants.STRING_NILL) && fromDateString.equals(Constants.STRING_NILL)){
			qry ="SELECT  COUNT(DISTINCT storeNumber) FROM  CouponCodes  WHERE orgId ="+userOrgId+" " +
					"AND storeNumber IS NOT NULL ";
		}
		
		int count =   ((Long)getHibernateTemplate().find(qry).get(0)).intValue();
		return count;
	}
	
public List<Map<String, Object>> findAllStoresRedemptionList(long userOrgId, int start_Idx, int endIdx, String couponId, String fromDateString, String toDateString) {
    	
    	try {
			List<Object[]> promoCodeList = null;
			
			logger.info("start_Idx is ::" +start_Idx +" >>>>and end Idx is ::"+endIdx);
			//Following will not fetch the records for null store_number if any, its obvious from query written, jussst mentioned explicitly.
			String qry = "SELECT store_number, ROUND(SUM(tot_revenue),2) AS REVENUE, COUNT(coupon_code_id) as COUNT  " +
					"FROM coupon_codes WHERE  coupon_id ="+couponId+" " +
							"AND store_number IS NOT NULL AND coupon_code IS NOT NULL AND redeemed_on between '"+fromDateString+"' AND '"+toDateString+"'" +
							"GROUP BY store_number ORDER BY redeemed_on DESC LIMIT  "+start_Idx+","+endIdx;
			
			
			
			if(couponId.equals("") && !fromDateString.equals(Constants.STRING_NILL) && !toDateString.equals(Constants.STRING_NILL)){
				qry = "SELECT store_number, ROUND(SUM(tot_revenue),2) AS REVENUE, COUNT(coupon_code_id) as COUNT " +
						"FROM coupon_codes WHERE orgId ="+userOrgId+
								" AND store_number IS NOT NULL AND coupon_code IS NOT NULL AND redeemed_on between '"+fromDateString+"' AND '"+toDateString+"'" +
								"GROUP BY store_number ORDER BY redeemed_on DESC LIMIT  "+start_Idx+","+endIdx;
			}else if(!couponId.equals("") && fromDateString.equals(Constants.STRING_NILL) && toDateString.equals(Constants.STRING_NILL)){
				qry = "SELECT store_number, ROUND(SUM(tot_revenue),2) AS REVENUE, COUNT(coupon_code_id) as COUNT  " +
						"FROM coupon_codes WHERE  coupon_id ="+couponId+" " +
								"AND store_number IS NOT NULL AND coupon_code IS NOT NULL "+
								"GROUP BY store_number ORDER BY redeemed_on DESC LIMIT  "+start_Idx+","+endIdx;
			}else if(couponId.equals("") && fromDateString.equals(Constants.STRING_NILL) && toDateString.equals(Constants.STRING_NILL)){
				qry = "SELECT store_number, ROUND(SUM(tot_revenue),2) AS REVENUE, COUNT(coupon_code_id) as " +
						"COUNT FROM coupon_codes WHERE orgId ="+userOrgId+" AND " +
						"store_number IS NOT NULL AND coupon_code IS NOT NULL GROUP BY store_number ORDER BY redeemed_on DESC LIMIT  "+start_Idx+","+endIdx;
			}
			
			//String qry ="SELECT promo_code, ROUND(SUM((quantity*sales_price)+tax),2) AS REVENUE , COUNT(customer_id) AS COUNT FROM  retail_pro_sales  WHERE user_id ="+userId+" AND promo_code IS NOT NULL GROUP BY promo_code ORDER BY sales_date DESC LIMIT  "+start_Idx+","+endIdx;
			logger.info("findPromocodeList is >>>::"+qry);
			return jdbcTemplate.queryForList(qry);
			
		} catch (DataAccessException e) {
			logger.error("Exception ::" , e);
			return null;
		}
    	
    } // findStoreRedemptionList 

public int findTotalCountOfStoreRelatedToPromo(Long userOrgId, Long couponId) {
	String qry ="SELECT  COUNT(DISTINCT storeNumber) FROM  CouponCodes  WHERE orgId ="+userOrgId+" AND " +
			"storeNumber IS NOT NULL AND couponId ="+couponId;
	
	int count =   ((Long)getHibernateTemplate().find(qry).get(0)).intValue();
	return count;
}

public int findTotalCountForStore(Long userOrgId, String store,
		String fromDateString, String toDateString) {
	
	String qry ="SELECT  COUNT(DISTINCT couponId) FROM  CouponCodes  WHERE orgId ="+userOrgId+
			" AND storeNumber = '"+store+"' AND redeemedOn between '"+fromDateString+"' AND '"+toDateString+"'";
	
	
	if(fromDateString.equals(Constants.STRING_NILL) && fromDateString.equals(Constants.STRING_NILL)){
		qry ="SELECT  COUNT(DISTINCT couponId) FROM  CouponCodes  WHERE orgId ="+userOrgId+" " +
				" AND storeNumber = '"+store+"'";
	}
	
	int count =   ((Long)getHibernateTemplate().find(qry).get(0)).intValue();
	return count;
}

public List<Map<String, Object>> findStoreRedemptionList(Long userOrgId,
		String store, int start_Idx, int endIdx, String couponId,
		String fromDateString, String toDateString) {
	try {
		List<Object[]> promoCodeList = null;
		String qry = Constants.STRING_NILL;
		/*//logger.info("start_Idx is ::" +start_Idx +" >>>>and end Idx is ::"+endIdx);
		//Following will not fetch the records for null store_number if any, its obvious from query written, jussst mentioned explicitly.
		String qry = "SELECT coupon_id, ROUND(SUM(tot_revenue),2) AS REVENUE, COUNT(coupon_code_id) as COUNT  " +
				"FROM coupon_codes WHERE orgId ="+userOrgId+" AND coupon_id ="+couponId+" " +
						"AND store_number IS NOT NULL AND coupon_code IS NOT NULL AND store_number = '"+store+"' AND redeemed_on between '"+fromDateString+"' AND '"+toDateString+"'" +
						"GROUP BY coupon_id ORDER BY redeemed_on DESC LIMIT  "+start_Idx+","+endIdx;*/
		
		
		
		/*if(couponId.equals("") && !fromDateString.equals(Constants.STRING_NILL) && !toDateString.equals(Constants.STRING_NILL)){*/
			qry = "SELECT c.coupon_name AS name, ROUND(SUM(cc.tot_revenue),2) AS REVENUE, COUNT(cc.coupon_code_id) as COUNT " +
					"FROM coupon_codes cc left outer join coupons c on cc.coupon_id=c.coupon_id WHERE orgId ="+userOrgId+
							" AND store_number IS NOT NULL AND cc.coupon_code IS NOT NULL AND cc.store_number = '"+store+"' AND cc.redeemed_on between '"+fromDateString+"' AND '"+toDateString+"'" +
							"GROUP BY cc.coupon_id ORDER BY redeemed_on DESC LIMIT  "+start_Idx+","+endIdx;
			/*}else if(!couponId.equals("") && fromDateString.equals(Constants.STRING_NILL) && toDateString.equals(Constants.STRING_NILL)){
			qry = "SELECT store_number, ROUND(SUM(tot_revenue),2) AS REVENUE, COUNT(coupon_code_id) as COUNT  " +
					"FROM coupon_codes WHERE orgId ="+userOrgId+" AND coupon_id ="+couponId+" " +
							"AND store_number IS NOT NULL AND store_number = '"+store+"' AND coupon_code IS NOT NULL "+
							"GROUP BY coupon_id ORDER BY redeemed_on DESC LIMIT  "+start_Idx+","+endIdx;
		}else if(couponId.equals("") && fromDateString.equals(Constants.STRING_NILL) && toDateString.equals(Constants.STRING_NILL)){
			qry = "SELECT store_number, ROUND(SUM(tot_revenue),2) AS REVENUE, COUNT(coupon_code_id) as " +
					"COUNT FROM coupon_codes WHERE orgId ="+userOrgId+" AND " +
					"store_number IS NOT NULL AND store_number = '"+store+"' AND coupon_code IS NOT NULL GROUP BY coupon_id ORDER BY redeemed_on DESC LIMIT  "+start_Idx+","+endIdx;
		}*/
		
		//String qry ="SELECT promo_code, ROUND(SUM((quantity*sales_price)+tax),2) AS REVENUE , COUNT(customer_id) AS COUNT FROM  retail_pro_sales  WHERE user_id ="+userId+" AND promo_code IS NOT NULL GROUP BY promo_code ORDER BY sales_date DESC LIMIT  "+start_Idx+","+endIdx;
		logger.info("findPromocodeList is >>>::"+qry);
		return jdbcTemplate.queryForList(qry);
		
	} catch (DataAccessException e) {
		logger.error("Exception ::" , e);
		return null;
	}
}
/*
	public int updatePromoStatus(Coupons coupObj, int noOfDays){
		
		String qry = "UPDATE CouponCodes set status = '"+Constants.COUP_CODE_STATUS_EXPIRED+"', expiredOn = now() where couponId = "+ coupObj.getCouponId() +" "
				+ "AND status = '"+Constants.COUP_CODE_STATUS_ACTIVE+"' AND DATE_ADD("+ coupObj.getCouponCreatedDate() +", "+ noOfDays +") > now()";
		
		int updateCount = executeUpdate(qry);
		return updateCount;
	}
	
	public int updateCouponByBday(CouponCodes couponCodes, Calendar bday, int noOfDays){
		
		String qry = "UPDATE CouponCodes set status = '"+Constants.COUP_CODE_STATUS_EXPIRED+"', expiredOn = now() where couponId = "+couponCodes.getCouponId()+" "
				+ " AND status = '"+Constants.COUP_CODE_STATUS_ACTIVE+"' AND DATE_ADD("+bday+", "+noOfDays+") > now()";
		int updateCount = executeUpdate(qry);
		return updateCount;
	}
	
	public int updateCouponByAnnv(CouponCodes couponCodes, Calendar annv, int noOfDays){
		
		String qry = "UPDATE CouponCodes set status = '"+Constants.COUP_CODE_STATUS_EXPIRED+"', expiredOn = now() where couponId = "+couponCodes.getCouponId()+" "
				+ " AND status = '"+Constants.COUP_CODE_STATUS_ACTIVE+"' AND DATE_ADD("+annv+", "+noOfDays+") > now()";
		int updateCount = executeUpdate(qry);
		return updateCount;
	}
	*/
	/*public List<CouponCodes> getAllActiveCon(Long couponID, Long orgID){
		
		try{
			List<CouponCodes> couList = null;
			String qry = "FROM CouponCodes WHERE orgId="+orgID+" AND couponId="+couponID+" AND status='"+Constants.COUP_CODE_STATUS_ACTIVE+"' ";
			couList = executeQuery(qry);
			if(couList!= null && couList.size()>0) {
				return couList;
			}
			return null;
		}catch(DataAccessException e){
			logger.error("Exception ::" , e);
			return null;	
		}
	}
*/
public List<CouponCodes> getAllActiveCon(CouponCodes couponCodes){
	
	try{
		List<CouponCodes> couList = null;
		String qry = "FROM CouponCodes where status = '"+Constants.COUP_CODE_STATUS_ACTIVE+"' ";
		couList = executeQuery(qry);
		if(couList!= null && couList.size()>0) {
			return couList;
		}
		return null;
	}catch(DataAccessException e){
		logger.error("Exception ::" , e);
		return null;	
	}
}

	/*
	public void updateRevenueOnStackableTrx(Long coupId, String docSidStr) {
		
		try {
			String qry = " UPDATE CouponCodes SET totRevenue=NULL WHERE  couponId ="+coupId.longValue() +" AND docSid='"+docSidStr+"' AND redeemedOn<now()";
			executeUpdate(qry);
		} catch (Exception e) {
			logger.error("Exception ::" , e);
		}
	}*/
	
public Double getTotRevOnThisRecpt(Long coupId, String docSidStr) {
	
	String qry="SELECT SUM(totRevenue) FROM CouponCodes WHERE  couponId ="+coupId+ "  AND docSid='"+docSidStr+"' AND redeemedOn<now() ";
	
	
	List<Double> retiList = executeQuery(qry);
	
	if(retiList != null && !retiList.isEmpty()){
		
		double  totRevenue= retiList.get(0) != null ? ((Double)retiList.get(0)).doubleValue() : 0;
		return totRevenue;
	}
	//double  totRevenue=()getHibernateTemplate().find(qry).get(0);
	return 0.0;
	
}
	
	public List<CouponCodes> findIssuedCouponsBy(Long orgId, Long contactId, String customerId, String emailID, String mobile, Long couponId){
		
		List<CouponCodes> retList = null;
		try {
			String subQuery = Constants.STRING_NILL;
				
			subQuery += customerId != null && !customerId.isEmpty() ?( "(issuedTo='"+customerId+"')" ) : Constants.STRING_NILL;
			
			if(emailID != null && !emailID.isEmpty()) {
				
				subQuery += (!subQuery.isEmpty() ? " OR " : Constants.STRING_NILL);
				
				subQuery += ( "(issuedTo='"+emailID+"')" );
			}
			if(mobile != null && !mobile.isEmpty()){
				subQuery += (!subQuery.isEmpty() ? " OR " : Constants.STRING_NILL);
				subQuery += ( " ( issuedTo like '%"+mobile+"%')" ) ;
				
			}
			subQuery += (!subQuery.isEmpty() ? " OR " : Constants.STRING_NILL);
			
		
			
			subQuery += ( " (contactId ="+contactId+")") ;
			
			String query = "FROM CouponCodes WHERE orgId="+orgId+" AND couponId="+couponId+" AND status='Active' AND ("+subQuery+")" ;
			
			logger.debug("query ==="+query);
			retList = executeQuery(query);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ", e);
		}
		
		return retList;
	}
	
	
	
public List<CouponCodes> findIssuedCouponsByModified(Long orgId, Long contactId, Long couponId){
		
		List<CouponCodes> retList = null;
		try {
			
			String query = "FROM CouponCodes WHERE orgId="+orgId+" AND couponId="+couponId+" AND status='Active' AND contactId= "+contactId+"";
			
			logger.debug("query for findIssuedCouponsByModified ==="+query);
			retList = executeQuery(query);
			logger.debug("retList size is ==="+retList.size());

		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ", e);
		}
		
		return retList;
	}
	
	
	
	public int findTotCountCouponCodesWithFilter(Long coupId,Map<String,String> searchData) {
		String qry = "";
		boolean statusFlag =false;
		boolean redeemed = false;
		try {
		
		if(searchData!=null && !searchData.isEmpty() && searchData.size() > 0) {
			String searchQuery = null;
			if(searchData.containsKey("storeName")) {
				String storeId = searchData.get("storeName");
				searchQuery = "AND store_number = "+storeId+"";
			}else if(searchData.containsKey("storeName_all")) {
				String storeId = searchData.get("storeName_all");
				searchQuery = "AND store_number IN ("+storeId+")";
			}else if (searchData.containsKey("RedeemedDate")) {
				String[] date = searchData.get("RedeemedDate").split("T");
				searchQuery = "AND redeemed_on BETWEEN '"+date[0]+"' AND '"+date[1]+"'";
			}else if (searchData.containsKey("IssuedDate")) {
				String[] date = searchData.get("IssuedDate").split("T");
				searchQuery = "AND issued_on BETWEEN '"+date[0]+"' AND '"+date[1]+"'";
			}else if (searchData.containsKey("promotionStatus")) {
				statusFlag = true;
				String promotionStatus = searchData.get("promotionStatus");
				if(promotionStatus.equalsIgnoreCase("Redeemed")) {
					redeemed = true;
					qry ="SELECT sum(if(doc_sid is null,1,0)), count(distinct doc_sid) FROM  coupon_codes  WHERE  coupon_id ="+coupId + ""
						+ " AND status  IN('"+Constants.COUP_CODE_STATUS_REDEEMED+"')  ";
				}else if(promotionStatus.equalsIgnoreCase("Active")) {
					redeemed = true;
					qry = "SELECT sum(if(doc_sid is null,1,0)), count(distinct doc_sid),coupon_code_id FROM  coupon_codes  WHERE  coupon_id ="+coupId + ""
						+ " AND status IN('" + Constants.COUP_CODE_STATUS_ACTIVE + "','"+ Constants.COUP_CODE_STATUS_REDEEMED +"','"+ Constants.COUP_CODE_STATUS_EXPIRED +"') AND issued_to IS NOT NULL ";
				}else if(promotionStatus.equalsIgnoreCase("Inventory")) {
					qry = "SELECT COUNT(*) FROM  CouponCodes  WHERE  couponId ="+coupId + ""
							+" AND status IN('" + Constants.COUP_CODE_STATUS_INVENTORY + "') group by ccId";
				}else if(promotionStatus.equalsIgnoreCase("Expired")) {
					qry = "SELECT COUNT(*) FROM  CouponCodes  WHERE  couponId ="+coupId + ""
							+ " AND status IN('" + Constants.COUP_CODE_STATUS_EXPIRED + "') group by ccId";
				}else if(promotionStatus.equalsIgnoreCase("All")) {
					redeemed = true;
					qry = "SELECT sum(if(doc_sid is null,1,0)), count(distinct doc_sid),coupon_code_id FROM  coupon_codes  WHERE  coupon_id ="+coupId + "";
				}
			}
			  if(!statusFlag) {
				qry ="SELECT COUNT(*) FROM  CouponCodes  WHERE  couponId ="+coupId + 
					" "+searchQuery+" AND status NOT IN('"+Constants.COUP_CODE_STATUS_INVENTORY+"') group by coupon_code_id";
			  }
		}else {
			qry ="SELECT COUNT(*) FROM  CouponCodes  WHERE  couponId ="+coupId + 
				" AND status NOT IN('"+Constants.COUP_CODE_STATUS_INVENTORY+"') group by ccId";
		}
		if(redeemed) {
		
			
			logger.info("count qry redeemed :"+qry);
			List<Object[]>  list = null;			
			list= jdbcTemplate.query(qry, new RowMapper() {
		        public Object[] mapRow(ResultSet rs, int rowNum) throws SQLException {
		        	Object[] object = new Object[2];
		        	object[0] = rs.getInt(1);
		        	object[1] = rs.getInt(2);
					return object;
		        }
		        
			});
			
			if(list !=null) {
				logger.info("list.get(0) ::"+list.get(0));
				int sumCount=0,distinctCount=0;
				for (Object[] objects : list) {
					
					sumCount = (int)objects[0];
					distinctCount = (int)objects[1];
				}
				logger.info("sumCount :: "+sumCount+" :: distinctCount :: "+distinctCount);
				return sumCount+distinctCount;
			}else return 0;
			
		}
			
			/*if(!statusFlag) {
				qry ="SELECT sum(if(doc_sid is null,1,0)), count(distinct doc_sid) FROM  CouponCodes  WHERE  couponId ="+coupId + 
					" "+searchQuery+" AND status NOT IN('"+Constants.COUP_CODE_STATUS_INVENTORY+"') group by coupon_code_id";
			  }
		}else {
			qry ="SELECT sum(if(doc_sid is null,1,0)), count(distinct doc_sid) FROM  CouponCodes  WHERE  couponId ="+coupId + 
				" AND status NOT IN('"+Constants.COUP_CODE_STATUS_INVENTORY+"') group by ccId";
		}*/
			
		logger.info("count qry :"+qry);
		List resultList  = getHibernateTemplate().find(qry);
		if(resultList!=null) {
			logger.info("sizeeeeeeeee :"+resultList.size());
			return resultList.size();
		}else return 0;
		}catch (Exception e) {
			// TODO: handle exception
			logger.error("Exception ",e);
			return 0;
		
		}
	}
	
	
	public List<CouponCodes> findByCouponCode(Long coupId,int startIdx,int endIdx, Map<String, String> searchData) {
		try {
			List<CouponCodes> coupCodesList = null;
			//String query = "FROM CouponCodes WHERE  couponId ="+coupCodeId+" AND status NOT IN('"+Constants.COUP_CODE_STATUS_INVENTORY+"') ORDER BY redeemedOn DESC "; 

			//String query = "FROM CouponCodes WHERE  couponId ="+coupCodeId+" AND status NOT IN('"+Constants.COUP_CODE_STATUS_INVENTORY+"') GROUP BY docSid ORDER BY redeemedOn DESC ";
			
			/*String query = "select cc1 from coupon_codes cc1 join "
			+ "(select coupon_code_id,doc_sid, max(redeemed_on) as max_redeemed_on from coupon_codes where coupon_id="+coupCodeId+""
					+ "and status not in ('"+Constants.COUP_CODE_STATUS_INVENTORY+"') group by doc_sid ) cc2 on cc1.doc_sid=cc2.doc_sid"
					+ " where 	 cc1.coupon_id="+coupCodeId+" and cc1.status not in ('"+Constants.COUP_CODE_STATUS_INVENTORY+"') "
							+ "and cc2.max_redeemed_on=cc1.redeemed_on Group by cc1.doc_sid,cc1.redeemed_on";*/
			
			/*String query1 = "select cc1 from coupon_codes cc1 join "
					+ "(select coupon_code_id,doc_sid, max(redeemed_on) as max_redeemed_on from coupon_codes where coupon_id="+coupCodeId+""
							+ "and status not in ('"+Constants.COUP_CODE_STATUS_INVENTORY+"') group by doc_sid ) cc2 on cc1.doc_sid=cc2.doc_sid"
							+ " where 	 cc1.coupon_id="+coupCodeId+" and cc1.status not in ('"+Constants.COUP_CODE_STATUS_INVENTORY+"') "
									+ "and cc2.max_redeemed_on=cc1.redeemed_on Group by cc1.doc_sid,cc1.redeemed_on";*/
			
			
			
			
			
			/*String query = "from CouponCodes cc1 where docSid in(select docSid from CouponCodes where couponId="+coupCodeId+""
					+ " and status not in ('Inventory') group by docSid ) and redeemedOn in(select max(redeemedOn) as max_redeemedOn from CouponCodes where couponId="+coupCodeId+" "
							+ "and status not in ('Inventory') group by docSid) order by redeemedOn desc";
			
			coupCodesList = executeQuery(query, startIdx, endIdx);*/
			String query = "";
			//String subQuery = "";
			boolean statusFlag = false;
			String searchQuery = null;
			
			//String searchQuery = " AND doc_sid is null ";
			if(searchData!=null && !searchData.isEmpty() && searchData.size() > 0) {
				if(searchData.containsKey("storeName")) {
					String storeId = searchData.get("storeName");
					searchQuery = "AND store_number = "+storeId+" ";
				}else if(searchData.containsKey("storeName_all")) {
					String storeId = searchData.get("storeName_all");
					searchQuery = " AND store_number IN ("+storeId+")";
				}else if (searchData.containsKey("RedeemedDate")) {
					String[] date = searchData.get("RedeemedDate").split("T");
					searchQuery = " AND redeemed_on BETWEEN '"+date[0]+"' AND '"+date[1]+"' ";
				}else if (searchData.containsKey("IssuedDate")) {
					String[] date = searchData.get("IssuedDate").split("T");
					searchQuery = " AND issued_on BETWEEN '"+date[0]+"' AND '"+date[1]+"' ";
				}else if (searchData.containsKey("promotionStatus")) {
					statusFlag = true;
					String promotionStatus = searchData.get("promotionStatus");
					if(promotionStatus.equalsIgnoreCase("Redeemed")) {
						/*query = "select cc1.* from coupon_codes cc1 join "
							+ "(select coupon_code_id,doc_sid,receipt_number, max(redeemed_on) as max_redeemed_on from coupon_codes where coupon_id="+coupId+""
							+ " and status not in ('"+Constants.COUP_CODE_STATUS_INVENTORY+"') group by doc_sid ) cc2 on cc1.doc_sid=cc2.doc_sid"
							+ " where cc1.coupon_id="+coupId+" and cc1.status not in ('"+Constants.COUP_CODE_STATUS_INVENTORY+"') "
							+ " and cc2.max_redeemed_on=cc1.redeemed_on Group by cc1.doc_sid,cc1.redeemed_on order by cc1.redeemed_on desc limit "+startIdx+","+endIdx;
							*/
						searchQuery = " AND status IN('"
								+ Constants.COUP_CODE_STATUS_REDEEMED + "') limit "+startIdx+","+endIdx+"";
					}else if(promotionStatus.equalsIgnoreCase("Active")) {
						searchQuery =	 " AND status IN('"
								+ Constants.COUP_CODE_STATUS_ACTIVE + "','"+ Constants.COUP_CODE_STATUS_REDEEMED +"','"+ Constants.COUP_CODE_STATUS_EXPIRED +"') AND issued_to IS NOT NULL limit "+startIdx+","+endIdx+"";
					}else if(promotionStatus.equalsIgnoreCase("Inventory")) {
						searchQuery = " AND status IN('"
								+ Constants.COUP_CODE_STATUS_INVENTORY + "') limit "+startIdx+","+endIdx+"";
					}else if(promotionStatus.equalsIgnoreCase("Expired")) {
						searchQuery = " AND status IN('"
								+ Constants.COUP_CODE_STATUS_EXPIRED + "') limit "+startIdx+","+endIdx+"";
					}else if(promotionStatus.equalsIgnoreCase("All")) {
						searchQuery = " limit "+startIdx+","+endIdx+"";
					}
				 }
				
				/*if(!statusFlag) {
					query = "select cc1.* from coupon_codes cc1 where coupon_id="+coupId+""
							+ " and status not in ('"+Constants.COUP_CODE_STATUS_INVENTORY+"') "+searchQuery+""
							+ " group by cc1.coupon_code_id order by cc1.coupon_code_id desc limit "+startIdx+","+endIdx;
				}*/
			}
			
			
			/*if(searchData!=null && !searchData.isEmpty() && searchData.size() > 0 && searchData.containsKey("promotionStatus")) {
				
				String promotionStatus = searchData.get("promotionStatus");
				if(promotionStatus.equalsIgnoreCase("Redeemed")) {
					query = " select *,sum(tot_discount) as tot_discount1,sum(tot_revenue) as tot_revenue1 from coupon_codes cc left join contacts c on cc.contact_id = c.cid where coupon_id="+coupId+" "+searchQuery+"";
				}else{
					query = " select *, tot_discount as tot_discount1, tot_revenue as tot_revenue1 from coupon_codes cc left join contacts c on cc.contact_id = c.cid where coupon_id="+coupId+" "+searchQuery+"";
				}
			}else {
				query = " select * , tot_discount as tot_discount1, tot_revenue as tot_revenue1 from coupon_codes cc left join contacts c on cc.contact_id = c.cid where coupon_id="+coupId+" "+searchQuery+"";
			}*/
			
			// limit "+startIdx+","+endIdx+"
			query = " select * from coupon_codes cc left join contacts c on cc.contact_id = c.cid where coupon_id="+coupId+" AND doc_sid is null "+searchQuery+""; 
			//query = " select *,sum(tot_discount) as tot_discount1,sum(tot_revenue) as tot_revenue1 from coupon_codes cc left join contacts c on cc.contact_id = c.cid where coupon_id="+coupId+" "+searchQuery+""; 
			logger.info("Coupon Query ::"+query); 
			coupCodesList = jdbcTemplate.query(query, new RowMapper() {
		        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
		        	CouponCodes couponCodesObj = new CouponCodes();
		            
		        	Coupons cp=new Coupons();
		        	cp.setCouponId(rs.getLong("coupon_id"));
		        	couponCodesObj.setCouponId(cp);
		        	couponCodesObj.setCouponCode(rs.getString("coupon_code"));
		        	couponCodesObj.setStatus(rs.getString("status"));
		        	couponCodesObj.setIssuedTo(rs.getString("issued_to"));
		        	couponCodesObj.setRedeemedTo(rs.getString("redeemed_to"));
		        	couponCodesObj.setCampaignName(rs.getString("campaign_name"));
		        	
		        	Calendar cal = Calendar.getInstance();
	            	
		        	if(rs.getTimestamp("issued_on")!=null){
		        		cal.setTime(rs.getTimestamp("issued_on"));
		        		couponCodesObj.setIssuedOn(cal);
		        	}else{
		        		couponCodesObj.setIssuedOn(null);
		        	}
		        	
		        	Calendar cal1 = Calendar.getInstance();
		        	if(rs.getTimestamp("redeemed_on")!=null){
		        		cal1.setTime(rs.getTimestamp("redeemed_on"));
	            	couponCodesObj.setRedeemedOn(cal1);
		        	}else{
		        		couponCodesObj.setRedeemedOn(null);
		        	}
	            	couponCodesObj.setStoreNumber(rs.getString("store_number"));
	            	couponCodesObj.setDocSid(rs.getString("doc_sid"));
	            	couponCodesObj.setReceiptNumber(rs.getString("receipt_number"));
	            	couponCodesObj.setTotDiscount(rs.getDouble("tot_discount"));
	            	couponCodesObj.setTotRevenue(rs.getDouble("tot_revenue"));
	            	//couponCodesObj.setTotDiscount(rs.getDouble("tot_discount1"));
	            	//couponCodesObj.setTotRevenue(rs.getDouble("tot_revenue1"));
	            	couponCodesObj.setUsedLoyaltyPoints(rs.getDouble("used_loyalty_points"));
	            	
		    	 return couponCodesObj;
		        }
		    });
			
			
			if(coupCodesList != null && coupCodesList.size() >0) {
				return coupCodesList;
			}
			return null;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			
			logger.error("Exception ",e);
			return null;
			
		}

	} // findByCouponCode
	
	
	
	public List<CouponCodes> findByCouponCode_docsid_not_null(Long coupId,int startIdx,int endIdx, Map<String, String> searchData) {
		try {
			List<CouponCodes> coupCodesList = null;
			
			String query = "";
			boolean statusFlag = false;
			String searchQuery = null;
			
			//String searchQuery = " AND doc_sid is not null ";
			if(searchData!=null && !searchData.isEmpty() && searchData.size() > 0) {
				if(searchData.containsKey("storeName")) {
					String storeId = searchData.get("storeName");
					searchQuery = "AND store_number = "+storeId+" group by doc_sid order by coupon_code_id desc";
				}else if(searchData.containsKey("storeName_all")) {
					String storeId = searchData.get("storeName_all");
					searchQuery = " AND store_number IN ("+storeId+") group by doc_sid order by coupon_code_id desc";
				}else if (searchData.containsKey("RedeemedDate")) {
					String[] date = searchData.get("RedeemedDate").split("T");
					searchQuery = " AND redeemed_on BETWEEN '"+date[0]+"' AND '"+date[1]+"' group by doc_sid order by coupon_code_id desc";
				}else if (searchData.containsKey("IssuedDate")) {
					String[] date = searchData.get("IssuedDate").split("T");
					searchQuery = " AND issued_on BETWEEN '"+date[0]+"' AND '"+date[1]+"' group by doc_sid order by coupon_code_id desc";
				}else if (searchData.containsKey("promotionStatus")) {
					statusFlag = true;
					String promotionStatus = searchData.get("promotionStatus");
					if(promotionStatus.equalsIgnoreCase("Redeemed")) {
						searchQuery = " AND status IN('"
								+ Constants.COUP_CODE_STATUS_REDEEMED + "') group by doc_sid order by coupon_code_id desc limit "+startIdx+","+endIdx+"";
					}else if(promotionStatus.equalsIgnoreCase("Active")) {
						searchQuery =	 " AND status IN('"
								+ Constants.COUP_CODE_STATUS_ACTIVE + "','"+ Constants.COUP_CODE_STATUS_REDEEMED +"','"+ Constants.COUP_CODE_STATUS_EXPIRED +"') AND issued_to IS NOT NULL group by doc_sid order by coupon_code_id desc limit "+startIdx+","+endIdx+"";
					}else if(promotionStatus.equalsIgnoreCase("Inventory")) {
						searchQuery = " AND status IN('"
								+ Constants.COUP_CODE_STATUS_INVENTORY + "') group by doc_sid order by coupon_code_id desc limit "+startIdx+","+endIdx+"";
					}else if(promotionStatus.equalsIgnoreCase("Expired")) {
						searchQuery = " AND status IN('"
								+ Constants.COUP_CODE_STATUS_EXPIRED + "') group by doc_sid order by coupon_code_id desc limit "+startIdx+","+endIdx+"";
					}else if(promotionStatus.equalsIgnoreCase("All")) {
						searchQuery = "group by doc_sid limit "+startIdx+","+endIdx+"";
					}
				 }
				
			}
			
			query = " select *,sum(tot_discount) as tot_discount1 from coupon_codes cc left join contacts c on cc.contact_id = c.cid where coupon_id="+coupId+"  AND doc_sid is not null "+searchQuery+""; 
			logger.info("Coupon Query22222 ::"+query); 
			coupCodesList = jdbcTemplate.query(query, new RowMapper() {
		        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
		        	CouponCodes couponCodesObj = new CouponCodes();
		            
		        	Coupons cp=new Coupons();
		        	cp.setCouponId(rs.getLong("coupon_id"));
		        	couponCodesObj.setCouponId(cp);
		        	couponCodesObj.setCouponCode(rs.getString("coupon_code"));
		        	couponCodesObj.setStatus(rs.getString("status"));
		        	couponCodesObj.setIssuedTo(rs.getString("issued_to"));
		        	couponCodesObj.setRedeemedTo(rs.getString("redeemed_to"));
		        	couponCodesObj.setCampaignName(rs.getString("campaign_name"));
		        	
		        	Calendar cal = Calendar.getInstance();
	            	
		        	if(rs.getTimestamp("issued_on")!=null){
		        		cal.setTime(rs.getTimestamp("issued_on"));
		        		couponCodesObj.setIssuedOn(cal);
		        	}else{
		        		couponCodesObj.setIssuedOn(null);
		        	}
		        	
		        	Calendar cal1 = Calendar.getInstance();
		        	if(rs.getTimestamp("redeemed_on")!=null){
		        		cal1.setTime(rs.getTimestamp("redeemed_on"));
	            	couponCodesObj.setRedeemedOn(cal1);
		        	}else{
		        		couponCodesObj.setRedeemedOn(null);
		        	}
	            	couponCodesObj.setStoreNumber(rs.getString("store_number"));
	            	couponCodesObj.setDocSid(rs.getString("doc_sid"));
	            	couponCodesObj.setReceiptNumber(rs.getString("receipt_number"));
	            	couponCodesObj.setTotDiscount(rs.getDouble("tot_discount1"));
	            	couponCodesObj.setTotRevenue(rs.getDouble("tot_revenue"));
	            	//couponCodesObj.setTotDiscount(rs.getDouble("tot_discount1"));
	            	//couponCodesObj.setTotRevenue(rs.getDouble("tot_revenue1"));
	            	couponCodesObj.setUsedLoyaltyPoints(rs.getDouble("used_loyalty_points"));
	            	couponCodesObj.setMembership(rs.getString("membership"));
	            	couponCodesObj.setItemInfo(rs.getString("item_info"));
	            	couponCodesObj.setValueCode(rs.getString("value_code"));
	            	
		    	 return couponCodesObj;
		        }
		    });
			
			
			if(coupCodesList != null && coupCodesList.size() >0) {
				return coupCodesList;
			}
			return null;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			
			logger.error("Exception ",e);
			return null;
			
		}

	} // findByCouponCode

	
	
	public List<String> getStoreByUserId(Long currentUserId) {
		String query = "SELECT distinct(storeNumber) FROM  CouponCodes where "
		+ "couponId in (select couponId FROM Coupons c where c.userId = "+currentUserId+") and storeNumber is not null";
		try {
		 List<String> storeList = (List<String>)executeQuery(query);
		 if(storeList!= null && storeList.size()>0) {
				return storeList;
			}
			return null;
		}catch(DataAccessException e){
			logger.error("Exception ::" , e);
			return null;	
		}catch(Exception e){
			logger.error("Exception ::" , e);
			return null;	
		}
	}

	public long findTotalCoupCodeCountByCoupAndStatus(Long couponId) {
		String query ="";
		  query = "SELECT COUNT(ccId) FROM  CouponCodes WHERE  couponId ="+couponId+""; 
		  logger.info("count qury is ::"+query);
	long count=((Long)getHibernateTemplate().find(query).get(0)).longValue();
	return count;
		
	}
	
	public List<CouponCodes> getAllMultipleCodesIssuedIfAny(Long orgID,String coupIds, String custSID, String email, String phone){
		
		try {
			String subQry = Constants.STRING_NILL;
			if(custSID != null && !custSID.trim().isEmpty()) subQry = "issuedTo='"+custSID+"'";
			if(email != null && !email.isEmpty()) subQry = !subQry.isEmpty() ? (subQry + " OR issuedTo='"+email+"'") : "issuedTo='"+email+"'";
			if(phone != null && !phone.isEmpty()) subQry = !subQry.isEmpty() ? (subQry + " OR issuedTo like '%"+phone+"'") : "issuedTo like '%"+phone+"'";
			
			
			String query = "FROM CouponCodes c WHERE orgId="+orgID+" "
					+ " AND c.couponId IN("+coupIds+")"//.status c.couponId.couponGeneratedType='"+Constants.COUP_GENT_TYPE_MULTIPLE+"'  "
							+ " AND ("+subQry+") AND status='Active' group by couponId";
			
			logger.info("query for getAllMultipleCodesIssuedIfAny is"+query);
			return executeQuery(query);
			
			
		} catch (Exception e) {
			return null;
		
		}
		
		
	}
	
	
	
public List<CouponCodes> getAllMultipleCodesIssuedIfAny(Users user,String coupIds, String custSID, String email, String phone){
		
		try {
			String subQry = Constants.STRING_NILL;
			
			String	mobilewithcarrier = "";
			Long orgID=user.getUserOrganization().getUserOrgId();
			String countryCarrier =user.getCountryCarrier()!=null? user.getCountryCarrier().toString()+"":"" ;
			int carriersize=countryCarrier.length();
			logger.info("searchStr is"+phone);
			if(!phone.startsWith(user.getCountryCarrier().toString())){
				
				logger.info("entering without countryCarrier");
				mobilewithcarrier = user.getCountryCarrier().toString()+phone;

			}else {

				logger.info("entering with countryCarrier");
				mobilewithcarrier=phone;
				phone= phone.substring(carriersize);
			}
			if(custSID != null && !custSID.trim().isEmpty()) subQry = "issuedTo='"+custSID+"'";
			if(email != null && !email.isEmpty()) subQry = !subQry.isEmpty() ? (subQry + " OR issuedTo='"+email+"'") : "issuedTo='"+email+"'";
		//	if(phone != null && !phone.isEmpty()) subQry = !subQry.isEmpty() ? (subQry + " OR issuedTo in ('"+searchStr+"','"+mobilewithcarrier+"')") : "issuedTo like '%"+phone+"'";
			
			if(phone != null && !phone.isEmpty()) subQry = !subQry.isEmpty() ? (subQry + " OR issuedTo IN ('"+phone+"','"+mobilewithcarrier+"')") : "issuedTo IN ('"+phone+"','"+mobilewithcarrier+"')";

			
			String query = "FROM CouponCodes c WHERE orgId="+orgID+" "
					+ " AND c.couponId IN("+coupIds+")"//.status c.couponId.couponGeneratedType='"+Constants.COUP_GENT_TYPE_MULTIPLE+"'  "
							+ " AND ("+subQry+") AND status='Active' group by couponId";
			
			return executeQuery(query);
			
			
		} catch (Exception e) {
			return null;
		
		}
		
		
	}

	
	public List<Object[]>  couponCodeReportsExport(String query){
		  try {
			  
			  List ccList = null;
			  
			  
			  /*String query = "select contact_id, COUNT(coupon_code_id)  from  coupon_codes WHERE coupon_id="+coupId+"  "
				  		+ " AND status IN ('"+ Constants.COUP_CODE_STATUS_REDEEMED + "') AND contact_id is not null GROUP BY contact_id";*/
			  
			  ccList = jdbcTemplate.query(query, new RowMapper() {
					 public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
						 
						 Object[] object =  new Object[2];
						 object[0] = rs.getLong(1);
						 object[1] = rs.getInt(2);
						 
						 CouponCodes couponCodesObj = new CouponCodes();
				            
				        	Coupons cp=new Coupons();
				        	cp.setCouponId(rs.getLong("coupon_id"));
				        	couponCodesObj.setCouponId(cp);
				        	couponCodesObj.setCouponCode(rs.getString("coupon_code"));
				        	couponCodesObj.setStatus(rs.getString("status"));
				        	couponCodesObj.setIssuedTo(rs.getString("issued_to"));
				        	couponCodesObj.setRedeemedTo(rs.getString("redeemed_to"));
				        	couponCodesObj.setCampaignName(rs.getString("campaign_name"));
				        	
				        	Calendar cal = Calendar.getInstance();
			            	
				        	if(rs.getTimestamp("issued_on")!=null){
				        		cal.setTime(rs.getTimestamp("issued_on"));
				        		couponCodesObj.setIssuedOn(cal);
				        	}else{
				        		couponCodesObj.setIssuedOn(null);
				        	}
				        	
				        	Calendar cal1 = Calendar.getInstance();
				        	if(rs.getTimestamp("redeemed_on")!=null){
				        		cal1.setTime(rs.getTimestamp("redeemed_on"));
			            	couponCodesObj.setRedeemedOn(cal1);
				        	}else{
				        		couponCodesObj.setRedeemedOn(null);
				        	}
			            	couponCodesObj.setStoreNumber(rs.getString("store_number"));
			            	couponCodesObj.setDocSid(rs.getString("doc_sid"));
			            	couponCodesObj.setReceiptNumber(rs.getString("receipt_number"));
			            	couponCodesObj.setTotDiscount(rs.getDouble("tot_discount"));
			            	couponCodesObj.setTotRevenue(rs.getDouble("tot_revenue"));
			            	//couponCodesObj.setTotDiscount(rs.getDouble("tot_discount1"));
			            	//couponCodesObj.setTotRevenue(rs.getDouble("tot_revenue1"));
			            	couponCodesObj.setUsedLoyaltyPoints(rs.getDouble("used_loyalty_points"));
			            	
				    	 return couponCodesObj;
						 
						//return object;
					 }
					
				});
				
			  return ccList;
		  } catch (Exception e) {
			  logger.error("Exception ",e);
			  logger.error("** Error : " + e.getMessage() + " **");
			  return null;
		  } 
	}

	
}//EOF
