package org.mq.marketer.campaign.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import org.mq.marketer.campaign.beans.CouponDiscountGeneration;
import org.mq.marketer.campaign.beans.Coupons;
import org.mq.marketer.campaign.beans.SKUTemp;
import org.mq.marketer.campaign.beans.SkuFile;
import org.mq.marketer.campaign.general.Constants;
import org.mq.optculture.utils.OCConstants;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

public class CouponDiscountGenerateDao extends AbstractSpringDao {

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);

	private SessionFactory sessionFactory;

	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}


	public CouponDiscountGeneration find(Long id) {
		return (CouponDiscountGeneration) super.find(CouponDiscountGeneration.class, id);
	}

/*	public void saveOrUpdate(CouponDiscountGeneration coupDisGenObj) {
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
	*/
	public List<CouponDiscountGeneration> findByCoupon(Coupons couponObj) {
		 List<CouponDiscountGeneration> coupDisGenList = null;
		 //from Contacts where mailingList in ("+mlIds+")
		 String qry = "FROM CouponDiscountGeneration WHERE coupons IN ("+couponObj.getCouponId()+")  ORDER BY discount ASC"  ;
		 logger.info("chaild List query is >>"+qry);
		 coupDisGenList =executeQuery(qry);
		 return coupDisGenList;
		
		
	} //findByCoupon
	public List<CouponDiscountGeneration> findByCoupon(Coupons couponObj, long startsFrom, long count) {
		 List<CouponDiscountGeneration> coupDisGenList = null;
		 //from Contacts where mailingList in ("+mlIds+")
		 String qry = "FROM CouponDiscountGeneration WHERE coupons IN ("+couponObj.getCouponId()+")  ORDER BY discount ASC"  ;
		 logger.info("chaild List query is >>"+qry);
		 coupDisGenList = executeQuery(qry, (int)startsFrom, (int)count);
		 return coupDisGenList;
		
		
	} //findByCoupon
	public long findCountByCoupon(Coupons couponObj) {
		long coupDisGenListsize = 0l;
		 //from Contacts where mailingList in ("+mlIds+")
		// String qry = "FROM COUNT(couponDisGenId) WHERE coupons IN ("+couponObj.getCouponId()+")  ORDER BY discount DESC"  ;
		String qry = "SELECT COUNT(couponDisGenId) FROM CouponDiscountGeneration WHERE coupons IN ("+couponObj.getCouponId()+") ORDER BY discount ASC"; 
		logger.info("chaild List query is >>"+qry);
		 coupDisGenListsize = ((Long)executeQuery(qry).get(0)).longValue();
		 return coupDisGenListsize;
		
		
	} //findByCoupon
	/*
	public void deleteById(String coupDisGenId) {
		getHibernateTemplate().bulkUpdate("delete from CouponDiscountGeneration where couponDisGenId in (" + coupDisGenId + ")");
		logger.info("DELETE sucess fully");
	}
	
	*/
	
	
	
	
	public List<CouponDiscountGeneration> findCoupCodeByFlag(Coupons couponObj, String valStr) {
		 List<CouponDiscountGeneration> coupDisGenList = null;
		 String qry = "";
		 
		 if(valStr != null && valStr.trim().length() >0) {
			 qry = "FROM CouponDiscountGeneration WHERE coupons IN ("+couponObj.getCouponId()+") AND   itemCategory in ("+valStr+") ORDER BY discount DESC"  ;
		 }else  {
			 qry = "FROM CouponDiscountGeneration WHERE coupons IN ("+couponObj.getCouponId()+")  ORDER BY discount DESC"  ;
		 }
		 logger.info("chaild List query is >>"+qry);
		 coupDisGenList =executeQuery(qry);
		 return coupDisGenList;
		
		
	} //findByCoupon
	public  List<SkuFile> findDiscountedItems( String itemSids, Long userId, String subQuery) {
		
		try {
			String query = "FROM SkuFile WHERE userId="+userId+(!itemSids.isEmpty() ? " AND itemSid IS NOT NULL AND itemSid IN("+itemSids+")" : Constants.STRING_NILL) +subQuery; 
			logger.info("query is >>"+query);
			return executeQuery(query);
		} catch (Exception e) {
			logger.error("error fetching items ", e);
			return null;
		}
		
	}
	public  List<CouponDiscountGeneration>   findDiscountsBy(String couponIds, String itemSids, Long userId) {
		List<CouponDiscountGeneration>  list = null;
		try {
			/*headerMap.put("1", "Vendor");
			headerMap.put("2", "Department");
			headerMap.put("3", "Item Category");
			headerMap.put("4", "DCS");
			headerMap.put("5", "Class");
			headerMap.put("6", "Subclass");
			headerMap.put("7", "SKU");		
			
			
			 List<CouponDiscountGeneration> coupDisGenList = null;
			 String qry = "FROM CouponDiscountGeneration WHERE coupons ="+couponObj.getCouponId()+" AND  SkuAttribute IN(<SKUATTR>) AND SkuValue IN(<SKUVAL>) GROUP BY discount";
			 String skuAttrIn = Constants.STRING_NILL;
			 String skuAttrValIn = Constants.STRING_NILL;
			 if(VC != null && !VC.isEmpty()){
				 
				 skuAttrIn += "'Vendor' ";
				 
			 }
			 if(itemCat != null && !itemCat.isEmpty()) {
				 
				 skuAttrIn += ",'Item Category' ";
			 }
			 if(Dcode != null && !Dcode.isEmpty()){
				 skuAttrIn += ",'Department' ";
				 
			 }
			 if(Class != null && !Class.isEmpty()){
				 
				 skuAttrIn += ",'Class' ";
			 }
			 if(DCS != null && !DCS.isEmpty()) {
				 
				 skuAttrIn += ",'DCS' ";
			 }
			 if(subClass != null && !subClass.isEmpty()){
				 
				 skuAttrIn += ",'Subclass' ";
			 }if(SKU != null && !SKU.isEmpty()){
				 
				 skuAttrIn += ",'SKU' ";
			 }
			
			 logger.info("chaild List query is >>"+qry);
			*/ 
			 String query = "select cg.discount, sku.item_sid, cg.max_discount, cg.attribute, cg.value, cg.quantity, cg.limit_quantity,"
			 		+ " cg.no_of_eligile_items,"
			 		+ "cg.itemPrice, cg.itemPriceCriteria, cg.total_purchase_amt, cg.shipping_fee, "
			 		+ "cg.shipping_fee_type, cg.program,cg.tier_num,cg.CardSet_Num from coupon_discount_generation cg,  "
			 		+ "retail_pro_sku sku where cg.coupon_id in("+couponIds+") and sku.user_id="+userId+( !itemSids.isEmpty() ? " and sku.item_sid is not null AND sku.item_sid in("+itemSids+")  " : "")
			 				+ " and cg.attribute not like 'udf%' and ( if(cg.attribute='Department' , "
			 				+ "sku.department_code=cg.value, sku.department_code=cg.value) "
			 				+ " OR if(cg.attribute='Vendor' , sku.vendor_code=cg.value, sku.vendor_code=cg.value) "
			 				+ " OR if(cg.attribute='Class' , sku.class_code=cg.value, sku.class_code=cg.value) "
			 				+ " OR if(cg.attribute='Subclass' , sku.subclass_code =cg.value, sku.subclass_code =cg.value) "
			 				+ " OR if(cg.attribute='SKU' , sku.sku =cg.value, sku.sku =cg.value) "
			 				+ " OR if(cg.attribute='Item Category' , sku.item_category =cg.value, sku.item_category =cg.value)"
			 				+ " OR if(cg.attribute='DCS' , sku.dcs =cg.value,  sku.dcs =cg.value )"
			 				+"	OR if(cg.attribute='description' , sku.description =cg.value, sku.description =cg.value ) "
			 				+ " OR if(cg.attribute='ItemSID' , sku.item_sid =cg.value, sku.item_sid =cg.value))  group by cg.discount, sku.item_sid";
			 
			 	logger.info("query is >>"+query);
			 
				list= jdbcTemplate.query(query, new RowMapper() {
			        public CouponDiscountGeneration mapRow(ResultSet rs, int rowNum) throws SQLException {
			        	CouponDiscountGeneration object = new CouponDiscountGeneration();
			        	object.setDiscount(rs.getDouble(1));
			        	object.setItemCategory(rs.getString(2)); 
			        	object.setMaxDiscount(rs.getDouble(3));
			        	object.setSkuAttribute(rs.getString(4));
			        	object.setSkuValue(rs.getString(5));
			        	object.setQuantity(rs.getString(6));
			        	object.setLimitQuantity(rs.getString(7));
			        	object.setNoOfEligibleItems(rs.getString(8));
			        	object.setItemPrice(rs.getDouble(9));
			        	object.setItemPriceCriteria(rs.getString(10));
			        	object.setTotPurchaseAmount(rs.getLong(11));//ReceiptTotalCriteria(receiptTotalCriteria);alCriteria(rs.getString(11));
//these are wrong			        	
object.setShippingFee(rs.getString(12));
			        	object.setShippingFeeType(rs.getString(13));
			        	object.setProgram(rs.getString(14));
			        	object.setTierNum(rs.getString(15));
			        	object.setCardSetNum(rs.getString(16));
			        	
			        	return object;
			        }
			        
				});
		} catch (DataAccessException e) {
			logger.error("DataAccessException ", e);
		}catch(Exception  e) {
			
			logger.error("Exception ", e);
		}
		return list;

		 
		 
		
		
	} //findByCoupon
	
	public List<CouponDiscountGeneration> findBy(Long couponId, Long ownerId, String skuCol, String CDGAttr){
		List<CouponDiscountGeneration>  list = null;
		try {
			String query = "select  cg.discount, sku.item_sid, "+ownerId+", cg.max_discount,cg.attribute, cg.value,sku.list_price,"
					+ " cg.quantity, cg.limit_quantity ,cg.no_of_eligile_items,"
			 		+ "cg.itemPrice, cg.itemPriceCriteria , cg.total_purchase_amt, cg.shipping_fee, cg.shipping_fee_type, cg.program, cg.tier_num,cg.CardSet_Num  from coupon_discount_generation cg,  retail_pro_sku sku "+
						" where sku.user_id="+ownerId+" and cg.owner_id="+ownerId+" and sku.user_id=cg.owner_id and "
						+ " cg.coupon_id="+couponId+" and sku.item_sid is not null and sku."+skuCol+" IS NOT NULL "
						+ " and cg.attribute='"+CDGAttr+"' and cg.value=sku."+skuCol;
			logger.debug("query====="+query);
			
			list= jdbcTemplate.query(query, new RowMapper() {
		        public CouponDiscountGeneration mapRow(ResultSet rs, int rowNum) throws SQLException {
		        	CouponDiscountGeneration object = new CouponDiscountGeneration();
		        	object.setDiscount(rs.getDouble(1));
		        	object.setItemCategory(rs.getString(2));
		        	object.setMaxDiscount(rs.getDouble(4));
		        	object.setSkuAttribute(rs.getString(5));
		        	object.setSkuValue(rs.getString(6));
		        	//object.setItemPrice(rs.getDouble(7));
		        	object.setItemListPrice(rs.getString(7));
		        	object.setQuantity(rs.getString(8));
		        	object.setLimitQuantity(rs.getString(9));
		        	object.setNoOfEligibleItems(rs.getString(10));
		        	object.setItemPrice(rs.getDouble(11));
		        	object.setItemPriceCriteria(rs.getString(12));
		        	object.setTotPurchaseAmount(rs.getLong(13));
		        	object.setShippingFee(rs.getString(14));
		        	object.setShippingFeeType(rs.getString(15));
		        	object.setProgram(rs.getString(16));
		        	object.setTierNum(rs.getString(17));
		        	object.setCardSetNum(rs.getString(18));
		        	//object.setReceiptTotalCriteria(rs.getString(13));
					return object;
		        }
		        
			});
		} catch (DataAccessException e) {
			logger.error("DataAccessException ", e);
		}catch(Exception  e) {
			
			logger.error("Exception ", e);
		}
		return list;
	}
	
	
	public List<CouponDiscountGeneration> findByCouponBy(Coupons couponId, boolean isOnProduct, Long userId){
		List<CouponDiscountGeneration>  list = null;
		try {
			if(isOnProduct) {
			String query = "select cg.discount, sku.item_sid from coupon_discount_generation cg,  "
			 		+ "retail_pro_sku sku where cg.coupon_id in("+couponId.getCouponId()+") and sku.user_id="+userId
			 				+ " and ( if(cg.attribute='Department' , sku.department_code=cg.value, sku.department_code=cg.value) "
			 				+ " OR if(cg.attribute='Vendor' , sku.vendor_code=cg.value, sku.vendor_code=cg.value) "
			 				+ " OR if(cg.attribute='Class' , sku.class_code=cg.value, sku.class_code=cg.value) "
			 				+ " OR if(cg.attribute='Subclass' , sku.subclass_code =cg.value, sku.subclass_code =cg.value) "
			 				+ " OR if(cg.attribute='SKU' , sku.sku =cg.value, sku.sku =cg.value) "
			 				+ " OR if(cg.attribute='Item Category' , sku.item_category =cg.value, sku.item_category =cg.value)"
			 				+ " OR if(cg.attribute='DCS' , sku.dcs =cg.value, sku.dcs =cg.value) ) group by cg.discount, sku.item_sid ";
			 
				list= jdbcTemplate.query(query, new RowMapper() {
			        public CouponDiscountGeneration mapRow(ResultSet rs, int rowNum) throws SQLException {
			        	CouponDiscountGeneration object = new CouponDiscountGeneration();
			        	object.setDiscount(rs.getDouble(1));
			        	object.setItemCategory(rs.getString(2)); 
						return object;
			        }
			        
				});
			
			}else {
				
				return findByCoupon(couponId);
			}
		} catch (DataAccessException e) {
			logger.error("DataAccessException ", e);
		}catch(Exception  e) {
			
			logger.error("Exception ", e);
		}
		return list;
	}
	
	
	
	public List<CouponDiscountGeneration> findFromTempPromoDump(Coupons couponId, Long userId){
		List<CouponDiscountGeneration>  list = null;
		try {
			
			String query = "select cg.discount, sku.item_sid from coupon_discount_generation cg,  "
			 		+ "retail_pro_sku sku where cg.coupon_id in("+couponId.getCouponId()+") and sku.user_id="+userId
			 				+ " and ( if(cg.attribute='Department' , sku.department_code=cg.value, sku.department_code=cg.value) "
			 				+ " OR if(cg.attribute='Vendor' , sku.vendor_code=cg.value, sku.vendor_code=cg.value) "
			 				+ " OR if(cg.attribute='Class' , sku.class_code=cg.value, sku.class_code=cg.value) "
			 				+ " OR if(cg.attribute='Subclass' , sku.subclass_code =cg.value, sku.subclass_code =cg.value) "
			 				+ " OR if(cg.attribute='SKU' , sku.sku =cg.value, sku.sku =cg.value) "
			 				+ " OR if(cg.attribute='Item Category' , sku.item_category =cg.value, sku.item_category =cg.value)"
			 				+ " OR if(cg.attribute='DCS' , sku.dcs =cg.value, sku.dcs =cg.value) ) group by cg.discount, sku.item_sid ";
			 
				list= jdbcTemplate.query(query, new RowMapper() {
			        public CouponDiscountGeneration mapRow(ResultSet rs, int rowNum) throws SQLException {
			        	CouponDiscountGeneration object = new CouponDiscountGeneration();
			        	object.setDiscount(rs.getDouble(1));
			        	object.setItemCategory(rs.getString(2)); 
						return object;
			        }
			        
				});
			
			
		} catch (DataAccessException e) {
			logger.error("DataAccessException ", e);
		}catch(Exception  e) {
			
			logger.error("Exception ", e);
		}
		return list;
	}
	
	public List<Object[]> findDistinctDisc(Long couponId, Long ownerId){
		List<Object[]> retList = null;
		try {
			String query = " SELECT DISTINCT( discount),totPurchaseAmount FROM CouponDiscountGeneration WHERE coupons ="+couponId +" AND ownerId="+ownerId;
			retList=  executeQuery(query);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ", e);
		}
		return retList;
	}
/*	public void deleteCoupDis(long couponId, String discountStr ,String deleValueStr, boolean flag) {
		 String qry = "";
		if(flag) {
			qry = "DELETE FROM CouponDiscountGeneration WHERE coupons  = "+couponId+" AND  discount = "+discountStr+" AND itemCategory IN ("+deleValueStr+")";
		}else  {
			qry = "DELETE FROM CouponDiscountGeneration WHERE coupons  = "+couponId+" AND  discount = "+discountStr+" AND totPurchaseAmount IN ("+deleValueStr+")";
		}
		
		logger.info(" query is  ==== "+qry);
		int count = getHibernateTemplate().bulkUpdate(qry);
		
		logger.info("Delete Count from DB is::"+count);
	}*/ //deleteCoupDis
	
	public List<CouponDiscountGeneration> findCoupCodesByCouponObj(String couponIdStr) {
		List<CouponDiscountGeneration> coupDisGenList = null;
		String qry = "FROM CouponDiscountGeneration WHERE coupons in ("+couponIdStr+")";
		logger.info("child List query is >>"+qry);
		coupDisGenList = executeQuery(qry);
		return coupDisGenList;
		
		
	}
	public List<SKUTemp> findTempSkuBy(Long userId,Long couponId ,String limit) {
		List<SKUTemp> coupDisGenList = null;
		String sbquery="";
		if(couponId!=null){
			sbquery=" AND couponId="+couponId.longValue();
		}
		if(limit!=null) {
		String[] indx=limit.split("::");
		sbquery+=" and sku_temp_id between "+ indx[0]+" and "+indx[1];
		}
		String qry = "FROM SKUTemp WHERE userId ="+userId.longValue()+sbquery ;
		logger.info(" SKU Temp Query  >>"+qry);
		coupDisGenList=executeQuery(qry);
		return coupDisGenList;
	}
	
	public List<Object[]> findDistictAttribute(Long userId,Long couponId) {
		List<Object[]>  list = null;
		String sbquery="";
		if(couponId!=null){
			sbquery=" AND coupon_id="+couponId.longValue();
		}
		String qry = "SELECT  sku_attribute ,discount,discount_type FROM sku_temp  where user_id="+userId.longValue()+sbquery+"  group by discount,sku_attribute,discount_type ";
		list= jdbcTemplate.query(qry, new RowMapper() {
	        public Object[] mapRow(ResultSet rs, int rowNum) throws SQLException {
	        	Object[] object = new Object[25];
	        	object[0] = rs.getString(1);
	        	object[1] = rs.getDouble(2); 
	        	object[2] = rs.getString(3);
				return object;
	        }
	        
		});
		return list;
	}
	public List<SKUTemp> findBySKUTemp(Long couponId, long startsFrom, long count) {
		 List<SKUTemp> coupDisGenList = null;
		 //String qry = "FROM SKUTemp WHERE couponId ="+couponId.longValue()+"  ORDER BY  discount  ASC"  ;
		 String qry = "FROM SKUTemp WHERE couponId ="+couponId.longValue();
		 logger.info("Start index "+startsFrom+"  last index :"+count);
		 coupDisGenList = executeQuery(qry, (int)startsFrom, (int)count);
		 return coupDisGenList;
		
		
	} //findByCoupon
	public  List<SKUTemp> findBySKUTemp(Long couponId) {
		List<SKUTemp> coupDisGenList = null;
		//String qry = "FROM SKUTemp WHERE couponId ="+couponId.longValue()+"  ORDER BY  discount  ASC"; 
		String qry = "FROM SKUTemp WHERE couponId ="+couponId.longValue(); 
		coupDisGenList = executeQuery(qry);
		 return coupDisGenList;
		
		
	} //findByCoupon
	public List<Long> findBySkuUser(Long userId,Double discount,String type){
		List<Long> coupDisGenList = null;
		String qry = "Select count(*) FROM SKUTemp WHERE userId ="+userId.longValue()+" and discount="+discount+" and skuAttribute='"+type+"'"; 
		coupDisGenList = executeQuery(qry);
		 return coupDisGenList;
	}
	
	
	
	
	  public List<Long> findBySkuUser(Long userId,Double discount,String
		  type,String maxDiscount,String couponId){ 
		  List<Long> coupDisGenList = null;
		  String subQry = Constants.STRING_NILL;
		  subQry += (maxDiscount == null || maxDiscount.isEmpty()) ? (" AND maxDiscount IS NULL ") : " AND maxDiscount="+maxDiscount;
		  String qry =" Select count(*) FROM SKUTemp WHERE userId ="+userId.longValue()+
				  		" and discount="+discount+" and skuAttribute='"+type+"'  and couponId="+couponId+subQry;
			  coupDisGenList = executeQuery(qry);
		  return coupDisGenList; 
	  }
	 
	 
	public List<Long> findBySkuUserCount(Long userId,Double discount,String type,
			String maxDiscount,String quantity,String couponId){
		List<Long> coupDisGenList = null;
		String subQry = Constants.STRING_NILL;
		
		subQry += (maxDiscount == null || maxDiscount.isEmpty()) ? (" AND maxDiscount IS NULL ") : " AND maxDiscount="+maxDiscount;
		subQry += (quantity == null || quantity.isEmpty()) ? (" AND quantity IS NULL ") : " AND quantity="+quantity;
		subQry += (couponId != null && !couponId.isEmpty()) ? " AND couponId="+couponId:"";
		String qry = "Select count(*) FROM SKUTemp WHERE userId ="+userId.longValue()+
				" and discount="+discount+" and skuAttribute='"+type+"' "+subQry; 
		coupDisGenList = executeQuery(qry);
		 return coupDisGenList;
	}
	public List<Long> findExistingSkuCount(Long userId,String type){
		List<Long> coupDisGenList = null;
		String subQry = Constants.STRING_NILL;
		
		
		String qry = "Select count(*) FROM SKUTemp WHERE userId ="+userId.longValue()+
				" and skuAttribute='"+type+"' "+subQry; 
		
		logger.info("query==="+qry);
		coupDisGenList = executeQuery(qry);
		 return coupDisGenList;
	}
	
	
	public List<String> findDistinctAttr(Long couponId){
		List<String> retList = null;
		try {
			String query = " SELECT DISTINCT( SkuAttribute) FROM CouponDiscountGeneration WHERE coupons ="+couponId;
			retList=  executeQuery(query);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ", e);
		}
		return retList;
	}
	public List<Object[]> findDistinctAttrCombos(Long couponId){
		List<Object[]> retList = null;
		try {
			String query = " SELECT  SkuAttribute, SkuValue , discount,maxDiscount,quantity, "
					+ " limitQuantity, noOfEligibleItems, itemPrice,itemPriceCriteria , "
					+ " totPurchaseAmount,shippingFee, shippingFeeType, Program, tierNum, CardSetNum "
					+ " FROM CouponDiscountGeneration WHERE coupons ="+couponId +" group by SkuAttribute, SkuValue";
			retList=  executeQuery(query);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ", e);
		}
		return retList;
	}
	public List<String> findDistinctUDFAttr(Long couponId){
		List<String> retList = null;
		try {
			String query = " SELECT DISTINCT( SkuAttribute) FROM CouponDiscountGeneration WHERE coupons ="+couponId+" AND SkuAttribute like 'udf%'";
			retList=  executeQuery(query);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ", e);
		}
		return retList;
	}
	public List<Double> findDistinctCoupDiscount(Long couponId){
		List<Double> retList = null;
		try {
			String query = " SELECT DISTINCT( discount) FROM CouponDiscountGeneration WHERE coupons ="+couponId;
			retList=  executeQuery(query);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ", e);
		}
		return retList;
	}
	
	public List<Object[]> findDistinctDiscAndAttr(Long couponId){
		List<Object[]> retList = null;
		try {
			String query = " SELECT  discount, SkuAttribute FROM CouponDiscountGeneration WHERE coupons ="+couponId +" group by discount, SkuAttribute" ;
			retList=  executeQuery(query);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ", e);
		}
		return retList;
	}
	
	public List<Object[]> getDiscAndNumOfItems( Long orgOwner){
		List<Object[]>  list = null;
		try {
			String qry = " SELECT DISTINCT(discount) FROM temp_promo_dump WHERE owner_id="+orgOwner;
			list= jdbcTemplate.query(qry, new RowMapper() {
		        public Object[] mapRow(ResultSet rs, int rowNum) throws SQLException {
		        	Object[] object = new Object[1];
		        	object[0] = rs.getDouble(1);
					return object;
		        }
		        
			});
		} catch (Exception e) {
			logger.error("Exception ", e);
		}
		return list;
	}
	
	public List<String> getItems( Double discount, Long orgOwner){
		//List<Object[]>  list = null;
		List<String>  list = null;
		try {
			String qry = " SELECT itemSid FROM TempPromoDump WHERE ownerId="+orgOwner+""
					+ " AND discount="+discount;
			/*list= jdbcTemplate.query(qry, new RowMapper() {
		        public Object[] mapRow(ResultSet rs, int rowNum) throws SQLException {
		        	Object[] object = new Object[2];
		        	object[0] = rs.getString(1);
					return object;
		        }
		        
			});
			
*/		 list = executeQuery(qry);
			return list ;
			} catch (Exception e) {
			logger.error("Exception ", e);
		}
		return list;
	}
	public List<CouponDiscountGeneration> findAllBy(Long couponId, Long ownerId, String skuCol, String CDGAttr){
		List<CouponDiscountGeneration> list = null;
		try {
		String query = "select cg.discount, "+ownerId+", cg.max_discount,cg.attribute, cg.value,"
		+ " cg.quantity, cg.limit_quantity ,cg.no_of_eligile_items,"
		+ "cg.itemPrice, cg.itemPriceCriteria , cg.total_purchase_amt from coupon_discount_generation cg "+
		" where cg.owner_id="+ownerId+" and "
		+ " cg.coupon_id="+couponId+" "
		+ " and cg.attribute='"+CDGAttr+"' ";
		logger.debug("query====="+query);

		list= jdbcTemplate.query(query, new RowMapper() {
		public CouponDiscountGeneration mapRow(ResultSet rs, int rowNum) throws SQLException {
		CouponDiscountGeneration object = new CouponDiscountGeneration();
		object.setDiscount(rs.getDouble(1));
		//object.setItemCategory(rs.getString(2));
		object.setMaxDiscount(rs.getDouble(3));
		object.setSkuAttribute(rs.getString(4));
		object.setSkuValue(rs.getString(5));
		object.setQuantity(rs.getString(6));
		object.setLimitQuantity(rs.getString(7));
		object.setNoOfEligibleItems(rs.getString(8));
		object.setItemPrice(rs.getDouble(9));
		object.setItemPriceCriteria(rs.getString(10));
		object.setTotPurchaseAmount(rs.getLong(11));
		//object.setReceiptTotalCriteria(rs.getString(13));
		return object;
		}

		});
		} catch (DataAccessException e) {
		logger.error("DataAccessException ", e);
		}catch(Exception e) {

		logger.error("Exception ", e);
		}
		return list;
		}
	
	
	public  List<CouponDiscountGeneration>   findAllDiscountsBy(String couponIds, String itemSids, Long userId) {
		List<CouponDiscountGeneration>  list = null;
		try {
			
			 String query = "select cg.discount, sku.item_sid, cg.max_discount, cg.attribute, cg.value, cg.quantity, cg.limit_quantity,"
			 		+ " cg.no_of_eligile_items,"
			 		+ "cg.itemPrice, cg.itemPriceCriteria, cg.total_purchase_amt, cg.shipping_fee, "
			 		+ "cg.shipping_fee_type, cg.program,cg.tier_num,cg.CardSet_Num, sku.list_price from coupon_discount_generation cg,  "
			 		+ "retail_pro_sku sku where cg.coupon_id in("+couponIds+") and sku.user_id="+userId+( !itemSids.isEmpty() ? " and sku.item_sid is not null AND sku.item_sid in("+itemSids+")  " : "")
			 				+ " and cg.attribute not like 'udf%' and ( if(cg.attribute='Department' , "
			 				+ "sku.department_code=cg.value, sku.department_code=cg.value) "
			 				+ " OR if(cg.attribute='Vendor' , sku.vendor_code=cg.value, sku.vendor_code=cg.value) "
			 				+ " OR if(cg.attribute='Class' , sku.class_code=cg.value, sku.class_code=cg.value) "
			 				+ " OR if(cg.attribute='Subclass' , sku.subclass_code =cg.value, sku.subclass_code =cg.value) "
			 				+ " OR if(cg.attribute='SKU' , sku.sku =cg.value, sku.sku =cg.value) "
			 				+ " OR if(cg.attribute='Item Category' , sku.item_category =cg.value, sku.item_category =cg.value)"
			 				+ " OR if(cg.attribute='DCS' , sku.dcs =cg.value,  sku.dcs =cg.value )"
			 				+"	OR if(cg.attribute='description' , sku.description =cg.value, sku.description =cg.value ) "
			 				+ " OR if(cg.attribute='ItemSID' , sku.item_sid =cg.value, sku.item_sid =cg.value))  group by cg.discount, sku.item_sid";
			 
			 	logger.info("query is >>"+query);
			 
				list= jdbcTemplate.query(query, new RowMapper() {
			        public CouponDiscountGeneration mapRow(ResultSet rs, int rowNum) throws SQLException {
			        	CouponDiscountGeneration object = new CouponDiscountGeneration();
			        	object.setDiscount(rs.getDouble(1));
			        	object.setItemCategory(rs.getString(2)); 
			        	object.setMaxDiscount(rs.getDouble(3));
			        	object.setSkuAttribute(rs.getString(4));
			        	object.setSkuValue(rs.getString(5));
			        	object.setQuantity(rs.getString(6));
			        	object.setLimitQuantity(rs.getString(7));
			        	object.setNoOfEligibleItems(rs.getString(8));
			        	object.setItemPrice(rs.getDouble(9));
			        	object.setItemPriceCriteria(rs.getString(10));
			        	object.setTotPurchaseAmount(rs.getLong(11));//ReceiptTotalCriteria(receiptTotalCriteria);alCriteria(rs.getString(11));
			        	object.setShippingFee(rs.getString(12));
			        	object.setShippingFeeType(rs.getString(13));
			        	object.setProgram(rs.getString(14));
			        	object.setTierNum(rs.getString(15));
			        	object.setCardSetNum(rs.getString(16));
			        	object.setItemListPrice(rs.getString(17));
			        	return object;
			        }
			        
				});
		} catch (DataAccessException e) {
			logger.error("DataAccessException ", e);
		}catch(Exception  e) {
			
			logger.error("Exception ", e);
		}
		return list;

		 
		 
		
		
	} //findByCoupon
}
