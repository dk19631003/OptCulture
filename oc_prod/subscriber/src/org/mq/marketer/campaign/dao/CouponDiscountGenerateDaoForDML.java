package org.mq.marketer.campaign.dao;

import java.util.Collection;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import org.mq.marketer.campaign.beans.CouponDiscountGeneration;
import org.mq.marketer.campaign.beans.Coupons;
import org.mq.marketer.campaign.general.Constants;
import org.springframework.jdbc.core.JdbcTemplate;

public class CouponDiscountGenerateDaoForDML extends AbstractSpringDaoForDML {

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);

	private SessionFactory sessionFactory;

	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}


	/*public CouponDiscountGeneration find(Long id) {
		return (CouponDiscountGeneration) super.find(CouponDiscountGeneration.class, id);
	}*/

	public void saveOrUpdate(CouponDiscountGeneration coupDisGenObj) {
		super.saveOrUpdate(coupDisGenObj);
	}

	public void delete(CouponDiscountGeneration coupDisGenObj) {
		super.delete(coupDisGenObj);
	}
	
	public void deleteByCouponId(Long couponId) {
		logger.info("COupon Id is"+ couponId);
		
		getHibernateTemplate().bulkUpdate("delete from CouponDiscountGeneration where coupons in (" + couponId + ")");
		getHibernateTemplate().bulkUpdate("delete from CouponCodes where couponId in (" + couponId + ")");
		getHibernateTemplate().bulkUpdate("delete from Coupons where couponId in (" + couponId + ")");
	}
	
	/*public List<CouponDiscountGeneration> findByCoupon(Coupons couponObj) {
		 List<CouponDiscountGeneration> coupDisGenList = null;
		 //from Contacts where mailingList in ("+mlIds+")
		 String qry = "FROM CouponDiscountGeneration WHERE coupons IN ("+couponObj.getCouponId()+")  ORDER BY discount DESC"  ;
		 logger.info("chaild List query is >>"+qry);
		 coupDisGenList = getHibernateTemplate().find(qry);
		 return coupDisGenList;
		 
		
	}*/ //findByCoupon
	
	
	public void deleteById(String coupDisGenId) {
		getHibernateTemplate().bulkUpdate("delete from CouponDiscountGeneration where couponDisGenId in (" + coupDisGenId + ")");
		logger.info("DELETE sucess fully");
	}
	
	public void deleteDiscountGenByCouponId(Long couponId) {
		getHibernateTemplate().bulkUpdate("delete from CouponDiscountGeneration where coupons in (" + couponId + ")");
		logger.info("DELETE sucess fully");
	}
	
	/*
	public List<CouponDiscountGeneration> findCoupCodeByFlag(Coupons couponObj, String valStr) {
		 List<CouponDiscountGeneration> coupDisGenList = null;
		 String qry = "";
		 
		 if(valStr != null && valStr.trim().length() >0) {
			 qry = "FROM CouponDiscountGeneration WHERE coupons IN ("+couponObj.getCouponId()+") AND   itemCategory in ("+valStr+") ORDER BY discount DESC"  ;
		 }else  {
			 qry = "FROM CouponDiscountGeneration WHERE coupons IN ("+couponObj.getCouponId()+")  ORDER BY discount DESC"  ;
		 }
		 logger.info("chaild List query is >>"+qry);
		 coupDisGenList = getHibernateTemplate().find(qry);
		 return coupDisGenList;
		
		
	} *///findByCoupon
	
	public void deleteCoupDis(long couponId, String discountStr ,String deleValueStr, boolean flag) {
		 String qry = "";
		if(flag) {
			qry = "DELETE FROM CouponDiscountGeneration WHERE coupons  = "+couponId+" AND  discount = "+discountStr+" AND itemCategory IN ("+deleValueStr+")";
		}else  {
			qry = "DELETE FROM CouponDiscountGeneration WHERE coupons  = "+couponId+" AND  discount = "+discountStr+" AND totPurchaseAmount IN ("+deleValueStr+")";
		}
		
		logger.info(" query is  ==== "+qry);
		int count = getHibernateTemplate().bulkUpdate(qry);
		
		logger.info("Delete Count from DB is::"+count);
	} //deleteCoupDis
	
	/*public List<CouponDiscountGeneration> findCoupCodesByCouponObj(String couponIdStr) {
		List<CouponDiscountGeneration> coupDisGenList = null;
		String qry = "FROM CouponDiscountGeneration WHERE coupons in ("+couponIdStr+")";
		logger.info("child List query is >>"+qry);
		coupDisGenList = getHibernateTemplate().find(qry);
		return coupDisGenList;
		
		
	}*/
	
	
	public void deleteTempSkuBy(Long userId,String attribute,String discount,String limit,Long couponId){
		String sbqry="";
		if(couponId!=null)
			sbqry=" AND coupon_id="+couponId.longValue();
		if(limit!=null) {
		String[] indx=limit.split("::");
		sbqry=sbqry+" and sku_temp_id between "+ indx[0]+" and "+indx[1];
		}
		String query="delete from sku_temp where user_id="+userId.longValue()+" AND sku_attribute='"+attribute.trim()+"' "
				+ " AND discount='"+discount.trim()+"' "+sbqry;
		logger.info("delete query ::"+query);
		jdbcTemplate.update(query);
	}
	public void deleteTempSkuBy(Long userId,String discount,String totalPurAmt,Long couponId){
		String sbqry="";
		if(couponId!=null)
			sbqry=" AND coupon_id="+couponId.longValue();
		String query="delete from sku_temp where user_id="+userId.longValue()+sbqry
				+ " AND discount="+discount.trim()+" AND total_purchase_amt="+totalPurAmt.trim();
		logger.info("delete query ::"+query);
		jdbcTemplate.update(query);
	}
	public void deleteAllSkuBy(Long userId,String limit){
		String subQry="";
		if(limit!=null) {
		String[] indx=limit.split("::");
		subQry=" and sku_temp_id between "+ indx[0]+" and "+indx[1];
		}
		String query="delete from sku_temp where user_id="+userId.longValue()+ subQry ;
		jdbcTemplate.update(query);
	}
	
	/*public void insertIntoCouponDiscountby(Long userId,Long couponId){
		String subQry="";
		if(couponId!=null) {
			subQry=" and coupon_id="+couponId.longValue();
		}
		String query="INSERT INTO  coupon_discount_generation (coupon_id ,discount,total_purchase_amt ,value,attribute,owner_id )  select temp.coupon_id , temp.discount, temp.total_purchase_amt ,temp.sku_value , temp.sku_attribute ,temp.owner_id from sku_temp as temp where user_id="+userId +subQry ;
		logger.info("coupon discount inser query ::"+query);
		jdbcTemplate.update(query);
		}*/
	
	public void insertIntoCouponDiscountby_New(Long userId,Long couponId){
		String subQry="";
		if(couponId!=null) {
			subQry=" and coupon_id="+couponId.longValue();
		}
		String query="INSERT INTO  coupon_discount_generation (coupon_id ,discount,total_purchase_amt ,value,attribute,owner_id, max_discount,quantity, limit_quantity,itemPrice,itemPriceCriteria,no_of_eligile_items,program,tier_num,CardSet_Num,eli_rule )  select temp.coupon_id , temp.discount, temp.total_purchase_amt ,temp.sku_value , temp.sku_attribute ,temp.owner_id, temp.max_discount,temp.quantity,temp.limit_quantity,temp.itemPrice,temp.itemPriceCriteria,temp.no_of_eligile_items,temp.program,temp.tier_num,temp.CardSet_Num,temp.eli_rule from sku_temp as temp where user_id="+userId +subQry ;
		logger.info("coupon discount inser query ::"+query);
		jdbcTemplate.update(query);
		}
	
	
		/*public void insertIntoSKUTempby(Long userId,Long couponId){
		String subQry="";
		if(couponId!=null) {
			subQry=" where coupon_id="+couponId.longValue();
		}
		String query="INSERT INTO   sku_temp(coupon_id , discount, total_purchase_amt  ,sku_value ,sku_attribute,user_id,owner_id )" + 
				"	 select cd.coupon_id , cd.discount, cd. total_purchase_amt ,cd.value,cd.attribute,"+userId+",cd.owner_id from coupon_discount_generation cd"+subQry;
		logger.info("sku insert query "+query);
		jdbcTemplate.update(query);
		}*/
	
	public void insertIntoSKUTempby_New(Long userId,Long couponId){
		String subQry="";
		if(couponId!=null) {
			subQry=" where coupon_id="+couponId.longValue();
		}
		String query="INSERT INTO   sku_temp(coupon_id , discount, total_purchase_amt  ,sku_value ,sku_attribute,user_id,owner_id, max_discount,quantity,limit_quantity,itemPrice,itemPriceCriteria,no_of_eligile_items,program,tier_num,CardSet_Num,eli_rule)" + 
				"	 select cd.coupon_id , cd.discount, cd. total_purchase_amt ,cd.value,cd.attribute,"+userId+",cd.owner_id, cd.max_discount,cd.quantity,cd.limit_quantity,cd.itemPrice,cd.itemPriceCriteria,cd.no_of_eligile_items,cd.program,cd.tier_num,cd.CardSet_Num,cd.eli_rule from coupon_discount_generation cd"+subQry+" order by cd.discount asc";
		logger.info("sku insert query "+query);
		jdbcTemplate.update(query);
		}
	
		public void deleteAllSkuByCouponId(Long couponId){
			String query="delete from sku_temp where coupon_id="+couponId.longValue();
			jdbcTemplate.update(query);
		}
		
		//Commented by venkata
		/*public void insertNewIntoCouponDiscountby(Long userId,Long couponId){
			String query="INSERT INTO  coupon_discount_generation (coupon_id ,discount,total_purchase_amt ,value,attribute ,owner_id)  select "+couponId.longValue()+" , temp.discount, temp.total_purchase_amt ,temp.sku_value , temp.sku_attribute ,temp.owner_id from sku_temp as temp where user_id="+userId ;
			logger.info("coupon discount inser query ::"+query);
			jdbcTemplate.update(query);
			}*/
		
		public void insertNewIntoCouponDiscountby_New(Long userId,Long couponId){
		
			String query="INSERT INTO  coupon_discount_generation (coupon_id ,discount,total_purchase_amt ,value,attribute ,owner_id,max_discount,quantity,limit_quantity,itemPrice,itemPriceCriteria,no_of_eligile_items,program,tier_num,CardSet_Num,eli_rule)  select "+couponId.longValue()+" , temp.discount, temp.total_purchase_amt ,temp.sku_value , temp.sku_attribute ,temp.owner_id,temp.max_discount,temp.quantity,temp.limit_quantity,temp.itemPrice,temp.itemPriceCriteria,temp.no_of_eligile_items,temp.program,temp.tier_num,temp.CardSet_Num,temp.eli_rule from sku_temp as temp where user_id="+userId ;
			logger.info("coupon discount inser query ::"+query); 
			jdbcTemplate.update(query);
			}
		
		
		public void insertNewIntoSkuTempBy(Long userId,double discount,String type,long orgOwnerId,String searchStr){
			String subqry=" and rp.sku is not null group by sku";
			if(searchStr.length()>0)
				subqry=" and rp.sku like'%"+searchStr+"%' group by sku";	
			String query="INSERT INTO  sku_temp(  discount  ,sku_value ,sku_attribute,user_id,owner_id )" + 
					" select "+discount+" ,rp.sku,'"+type+"',"+userId.longValue()+",rp.user_id from retail_pro_sku rp where rp.user_id="+orgOwnerId+subqry;
			logger.info("sku insert query "+query);
			jdbcTemplate.update(query);
			}
		
		//TODO Add MaxDiscount Value in DataBase 
	
		public void insertNewIntoSkuTempBy(Long userId,double discount,String type,long orgOwnerId,String searchStr,String maxdiscount, String couponId,String quantity){
			String subqry=" and rp.sku is not null group by sku";
			if(searchStr.length()>0)
				subqry=" and rp.sku like'%"+searchStr+"%' group by sku";	
			String query="INSERT INTO  sku_temp(  discount  ,sku_value ,sku_attribute,user_id,owner_id, max_discount,coupon_id,quantity )" + 
					" select "+discount+" ,rp.sku,'"+type+"',"+userId.longValue()+",rp.user_id, "+maxdiscount+","+couponId+","+quantity+" from retail_pro_sku rp where rp.user_id="+orgOwnerId+subqry;
			logger.info("sku insert query "+query);
			jdbcTemplate.update(query);
			}
		
		public long insertIntoTempPromoDumpBy(Long couponId, Long ownerId, String skuCol, String CDGAttr){
			try {
				String query = " INSERT IGNORE INTO temp_promo_dump (discount,item_sid, owner_id)"
						+ "(select  cg.discount, sku.item_sid, "+ownerId+" from coupon_discount_generation cg,  retail_pro_sku sku "+
						" where sku.user_id="+ownerId+" and cg.owner_id="+ownerId+" and sku.user_id=cg.owner_id and "
						+ " cg.coupon_id="+couponId+" and sku.item_sid is not null and sku."+skuCol+" IS NOT NULL "
						+ " and cg.attribute='"+CDGAttr+"' and cg.value=sku."+skuCol+")";
				
				logger.debug("query====>"+query);
				return  executeJdbcUpdateQuery(query);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Exception ", e);
				return 0;
			}
		}
		
		
		public long deleteTempPromoDumpBy(Long coupId, Long orgOwner){
			try {
				String qry = " DELETE FROM temp_promo_dump WHERE owner_id="+orgOwner;
				return  executeJdbcUpdateQuery(qry);
			} catch (Exception e) {
				logger.error("Exception ", e);
				return 0;
			}
			
		}
		
		
}
