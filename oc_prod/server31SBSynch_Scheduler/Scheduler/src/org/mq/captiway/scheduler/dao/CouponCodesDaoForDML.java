

package org.mq.captiway.scheduler.dao;

import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import org.mq.captiway.scheduler.beans.CouponCodes;
import org.mq.captiway.scheduler.beans.Coupons;
import org.mq.captiway.scheduler.utility.Constants;
import org.mq.optculture.utils.OCConstants;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

public class CouponCodesDaoForDML extends AbstractSpringDaoForDML {

	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);

	private SessionFactory sessionFactory;

	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}


	/*public CouponCodes find(Long id) {
		return (CouponCodes) super.find(CouponCodes.class, id);
	}*/

	public void saveOrUpdate(CouponCodes couponcodes) {
		super.saveOrUpdate(couponcodes);
	}

   public void saveByCollection(Collection<CouponCodes> couponCodesCollection){
    	super.saveOrUpdateAll(couponCodesCollection);
    }

	public void delete(CouponCodes couponcodes) {
		super.delete(couponcodes);
	}
	
	/*
	public CouponCodes testForCouponCodes(String cCode, long orgId) {
		List<CouponCodes> list = null;
		String query = "FROM CouponCodes  WHERE couponCode ='"+cCode+"'  AND orgId ="+orgId;
		list  = getHibernateTemplate().find(query);
		if(list!= null && list.size()>0) {
			return list.get(0);
		}
			
		return null;
	}
	

	public CouponCodes getInventorySingleCouponCodeByCouponId(long couponId) {
		List<CouponCodes> list = null;
		
		String query = "FROM CouponCodes  WHERE couponId ="+couponId +" AND status = '"+ Constants.COUP_CODE_STATUS_INVENTORY+"' ";
		list = executeQuery(query, 0, 1);

		if(list!= null && list.size()>0) {
			return list.get(0);
		}
		return null;
	}
	
	
	public List<CouponCodes> getInventoryCouponCodesByCouponId(long couponId) {
		List<CouponCodes> list = null;
		
		String query = "FROM CouponCodes  WHERE couponId ="+couponId +" AND status = '"+ Constants.COUP_CODE_STATUS_INVENTORY+"' ";
		list = executeQuery(query, 0, 1000);

		if(list!= null && list.size()>0) {
			return list;
		}
		return null;
	}
	public List<Integer> getInventoryCCCountByCouponId(long couponId) {
		
		List<Integer> list = null;
		
		String query = "SELECT COUNT(ccId) FROM CouponCodes  WHERE couponId ="+couponId +" AND status = '"+ Constants.COUP_CODE_STATUS_INVENTORY+"' ";
		list = executeQuery(query);

		if(list!= null && list.size()>0) {
			return list;
		}
		return null;
		
		
	}*/
	/**
	 * 
	 * @param couponId
	 * @param status if null returned all cc count
	 * @return count as a long value
	 */
/*	public long getCouponCodeCountByStatus(Long couponId, String status) {
		
		String query=null;

		if(status==null || status.trim().equalsIgnoreCase("All")) {
			 query = "SELECT  COUNT(ccId) FROM CouponCodes WHERE couponId ="+couponId;
		}
		else {
		 query = "SELECT  COUNT(ccId) FROM CouponCodes WHERE couponId ="+couponId+" AND status ='"+status.trim()+"' ";
		}

		long count =  ((Long)getHibernateTemplate().find(query).get(0)).longValue();
		
		return count;
		
	}*/ // findByCouponCode

/*	public long findIssuedCoupCodeByCoup(Long CouponId) {

		String qry = "SELECT COUNT(ccId) FROM  CouponCodes WHERE couponId ="
				+ CouponId + "  AND status NOT IN('"
				+ Constants.COUP_CODE_STATUS_INVENTORY + "')";

		long count = ((Long) getHibernateTemplate().find(qry).get(0))
				.longValue();

		return count;
	}
	
	public List<CouponCodes> getAllActiveCon(CouponCodes couponCodes){
		
		try{
			List<CouponCodes> couList = null;
			String qry = "FROM CouponCodes where status="+Constants.COUP_STATUS_ACTIVE+"";
			couList = executeQuery(qry);
			if(couList!= null && couList.size()>0) {
				return couList;
			}
			return null;
		}catch(DataAccessException e){
			logger.error("Exception ::" , e);
			return null;	
		}
	}*/
	
	public int updatePromoCodeStatus(Coupons coupObj, String noOfDays){
		
		String qry = "UPDATE coupon_codes set status = '"+Constants.COUP_STATUS_EXPIRED+"', expired_on =now() WHERE coupon_id = "+ coupObj.getCouponId() +" AND "
				+ " status = '"+Constants.COUP_STATUS_ACTIVE+"' AND DATEDIFF(now(), DATE_ADD(issued_on, INTERVAL "+ noOfDays +" DAY)) > 0";
		
		int updateCount = executeJdbcUpdateQuery(qry);
		return updateCount;
	}
	
	public int updatePromoCodeStatusByBDay(Coupons coupObj,  String noOfDays){
		
		/*String qry = "UPDATE coupon_codes cp, contacts c SET cp.status= '"+Constants.COUP_STATUS_EXPIRED+"', cp.expired_on = now() WHERE cp.contact_id=c.cid AND cp.coupon_id="+coupObj.getCouponId() +
				" AND cp.status = "+Constants.COUP_STATUS_ACTIVE+" AND c.birth_day is not null  AND Date(DATE_ADD(c.birth_day, "+ noOfDays +")) > now()";*/
		
		/*String qry = "UPDATE coupon_codes cc  JOIN (SELECT cid as contactID, birth_day as bday FROM contacts c WHERE user_id in("+coupObj.getUserId()+") and birth_day is not null  "
				+ "AND DATEDIFF(now(), DATE_ADD(birth_day, INTERVAL "+ noOfDays +"  DAY) ) > 0) o  ON cc.contact_id = o.contactID SET cc.status="+Constants.COUP_STATUS_EXPIRED+" , cc.expired_on=now() "
				+ "where coupon_id = "+ coupObj.getCouponId() +" and cc.status = "+Constants.COUP_STATUS_ACTIVE+" ";*/
		
		String qry = "UPDATE coupon_codes cc, contacts c SET cc.status= '"+Constants.COUP_STATUS_EXPIRED+"', cc.expired_on = now() "
				+ "WHERE cc.contact_id=c.cid AND cc.coupon_id= "+ coupObj.getCouponId() +" AND cc.status = '"+Constants.COUP_STATUS_ACTIVE+"' "
				+ "AND DATEDIFF(now(), DATE_ADD(if(birth_day is null, issued_on, birth_day), INTERVAL "+ noOfDays +" DAY) ) > 0";
		
		int updateCount = executeJdbcUpdateQuery(qry);
		return updateCount;
	}
	
	public int updatePromoCodeStatusByAnniversay(Coupons coupObj, String noOfDays){
		
		/*String qry = "UPDATE coupon_codes cp, contacts c SET cp.status= '"+Constants.COUP_STATUS_EXPIRED+"', cp.expired_on = now() WHERE cp.contact_id=c.cid AND cp.coupon_id="+coupObj.getCouponId() +
				" AND cp.status = "+Constants.COUP_STATUS_ACTIVE+" AND c.anniversay is not null  AND Date(DATE_ADD(c.anniversay, "+ noOfDays +")) > now()";*/
		
		/*String qry = "UPDATE coupon_codes cc  JOIN (SELECT cid as contactID, anniversary_day as bday FROM contacts c WHERE user_id in("+coupObj.getUserId()+") and anniversary_day is not null  "
				+ "AND DATEDIFF(now(), DATE_ADD(anniversary_day, INTERVAL "+ noOfDays +"  DAY) ) > 0) o  ON cc.contact_id = o.contactID SET cc.status="+Constants.COUP_STATUS_EXPIRED+" , cc.expired_on=now() "
				+ "where coupon_id = "+ coupObj.getCouponId() +" and cc.status = "+Constants.COUP_STATUS_ACTIVE+" ";*/
		
		String qry = "UPDATE coupon_codes cc, contacts c SET cc.status= '"+Constants.COUP_STATUS_EXPIRED+"', cc.expired_on = now() "
				+ "WHERE cc.contact_id=c.cid AND cc.coupon_id= "+ coupObj.getCouponId() +" AND cc.status = '"+Constants.COUP_STATUS_ACTIVE+"' "
				+ "AND DATEDIFF(now(), DATE_ADD(if(anniversary_day is null, issued_on, anniversary_day), INTERVAL "+ noOfDays +" DAY) ) > 0";
		
		int updateCount = executeJdbcUpdateQuery(qry);
		return updateCount;
	}
	
}
