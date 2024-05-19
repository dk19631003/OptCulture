package org.mq.marketer.campaign.dao;




import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.mq.marketer.campaign.beans.ContactsLoyalty;
import org.mq.marketer.campaign.beans.MastersToTransactionMappings;
import org.mq.marketer.campaign.beans.RetailProSalesCSV;
import org.mq.marketer.campaign.beans.SkuFile;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.POSFieldsEnum;
import org.mq.optculture.utils.OCConstants;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

public class RetailProSalesDaoForDML extends AbstractSpringDaoForDML {
	
	public RetailProSalesDaoForDML() {}
	
	private JdbcTemplate jdbcTemplate;
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

    /*public RetailProSalesCSV find(Long id) {
        return (RetailProSalesCSV) super.find(RetailProSalesCSV.class, id);
    }*/

    public void saveOrUpdate(RetailProSalesCSV retailProSales) {
        super.saveOrUpdate(retailProSales);
        //logger.info("IN saveorupdate.....");
    }

    public void delete(RetailProSalesCSV retailProSales) {
        super.delete(retailProSales);
    }

   
    public void saveByCollection(Collection retailProSalesCollection) {
//    	super.saveOrUpdateAll(retailProSalesCollection);
    	getHibernateTemplate().saveOrUpdateAll(retailProSalesCollection);
    }
    
    /*public List findRecordsByRecieptNumber(Long recieptNum ,Long userId) {
		try {
			List<RetailProSalesCSV> list = null;
			list  = getHibernateTemplate().find("from RetailProSalesCSV  where recieptNumber ="+recieptNum +"  and userId = " + userId);
			
			if(list != null && list.size() >0) {
				return list;
			}
			return null;
		} catch (Exception e) {
			logger.error("Exception ::" , e);
			return null;
		}
	}

    
    public List<Map<String, Object>> findPromocodeList(long userId, int start_Idx, int endIdx) {
    	
    	try {
			List<Object[]> promoCodeList = null;
			
			logger.info("start_Idx is ::" +start_Idx +" >>>>and end Idx is ::"+endIdx);
			String qry ="SELECT promo_code, ROUND(SUM((quantity*sales_price)+tax-(IF(discount is null,0,discount))),2) AS REVENUE , COUNT(customer_id) AS COUNT FROM  retail_pro_sales  WHERE user_id ="+userId+" AND promo_code IS NOT NULL GROUP BY promo_code ORDER BY sales_date DESC LIMIT  "+start_Idx+","+endIdx;
			logger.info("findPromocodeList is >>>::"+qry);
			return jdbcTemplate.queryForList(qry);
			
		} catch (DataAccessException e) {
			logger.error("Exception ::" , e);
			return null;
		}
    	
    } // findPromocodeList
    
    public List<Map<String, Object>> findPromotionalRedemptionList(long userId, int start_Idx, int endIdx, String storeNumber,String fromDateString, String toDateString,String orderby_colName,String desc_Asc) {
    	
    	try {
			List<Object[]> promoCodeList = null;
			
			logger.info("start_Idx is ::" +start_Idx +" >>>>and end Idx is ::"+endIdx);
			//Following will not fetch the records for null promo_code if any, its obvious from query written, jussst mentioned explicitly.
			String qry = "SELECT promo_code, ROUND(SUM((quantity*sales_price)+tax-(IF(discount is null,0,discount))),2) AS REVENUE, COUNT(sales_id) as COUNT,sales_date FROM retail_pro_sales " +
					"WHERE user_id ="+userId+" AND store_number ='"+storeNumber+"' " +
					"AND promo_code IS NOT NULL AND store_number IS NOT NULL AND sales_date between '"+fromDateString+"' AND '"+toDateString+"'" +
					"GROUP BY promo_code " +
					"ORDER BY "+orderby_colName+" "+desc_Asc+" LIMIT  "+start_Idx+","+endIdx;
			
			if(storeNumber.equals("") && !fromDateString.equals(Constants.STRING_NILL) && !toDateString.equals(Constants.STRING_NILL)){
				qry = "SELECT promo_code, ROUND(SUM((quantity*sales_price)+tax-(IF(discount is null,0,discount))),2) AS REVENUE, COUNT(sales_id) as COUNT,sales_date FROM retail_pro_sales WHERE user_id ="+userId+" " +
						"AND promo_code IS NOT NULL AND store_number IS NOT NULL AND sales_date between '"+fromDateString+"' AND '"+toDateString+"'" +
						"GROUP BY promo_code ORDER BY "+orderby_colName+" "+desc_Asc+" LIMIT  "+start_Idx+","+endIdx;
			}else if(!storeNumber.equals("") && fromDateString.equals(Constants.STRING_NILL) && toDateString.equals(Constants.STRING_NILL)){
				qry = "SELECT promo_code, ROUND(SUM((quantity*sales_price)+tax-(IF(discount is null,0,discount))),2) AS REVENUE, COUNT(sales_id) as COUNT,sales_date FROM retail_pro_sales WHERE user_id ="+userId+" " +
						"AND store_number ='"+storeNumber+"' "+"AND promo_code IS NOT NULL AND store_number IS NOT NULL" +
						" GROUP BY promo_code ORDER BY "+orderby_colName+" "+desc_Asc+" LIMIT  "+start_Idx+","+endIdx;
			}else if(storeNumber.equals("") && fromDateString.equals(Constants.STRING_NILL) && toDateString.equals(Constants.STRING_NILL)){
				qry = "SELECT promo_code, ROUND(SUM((quantity*sales_price)+tax-(IF(discount is null,0,discount))),2) AS REVENUE, COUNT(sales_id) as COUNT,sales_date FROM retail_pro_sales WHERE user_id ="+userId+" " +
						"AND promo_code IS NOT NULL AND store_number IS NOT NULL" +
						" GROUP BY promo_code ORDER BY "+orderby_colName+" "+desc_Asc+" LIMIT  "+start_Idx+","+endIdx;
			}
			
			
			//String qry ="SELECT promo_code, ROUND(SUM((quantity*sales_price)+tax),2) AS REVENUE , COUNT(customer_id) AS COUNT FROM  retail_pro_sales  WHERE user_id ="+userId+" AND promo_code IS NOT NULL GROUP BY promo_code ORDER BY sales_date DESC LIMIT  "+start_Idx+","+endIdx;
			logger.info("findPromocodeList is >>>::"+qry);
			return jdbcTemplate.queryForList(qry);
			
		} catch (DataAccessException e) {
			logger.error("Exception ::" , e);
			return null;
		}
    	
    } // findPromotionalRedemptionList 
    
   public List<Map<String, Object>> findStoreRedemptionList(long userId, int start_Idx, int endIdx, String promoCode, String fromDateString, String toDateString, String orderby_colName, String desc_Asc) {
    	
    	try {
			List<Object[]> promoCodeList = null;
			
			logger.info("start_Idx is ::" +start_Idx +" >>>>and end Idx is ::"+endIdx);
			//Following will not fetch the records for null store_number if any, its obvious from query written, jussst mentioned explicitly.
			String qry = "SELECT store_number, ROUND(SUM((quantity*sales_price)+tax-(IF(discount is null,0,discount))),2) AS REVENUE, COUNT(sales_id) as COUNT ,sales_date " +
					"FROM retail_pro_sales WHERE user_id ="+userId+" AND promo_code ='"+promoCode+"' " +
							"AND store_number IS NOT NULL AND promo_code IS NOT NULL AND sales_date between '"+fromDateString+"' AND '"+toDateString+"'" +
							"GROUP BY store_number ORDER BY "+orderby_colName+" "+desc_Asc+" LIMIT  "+start_Idx+","+endIdx;
			
			
			
			if(promoCode.equals("") && !fromDateString.equals(Constants.STRING_NILL) && !toDateString.equals(Constants.STRING_NILL)){
				qry = "SELECT store_number, ROUND(SUM((quantity*sales_price)+tax-(IF(discount is null,0,discount))),2) AS REVENUE, COUNT(sales_id) as COUNT ,sales_date " +
						"FROM retail_pro_sales WHERE user_id ="+userId+
								" AND store_number IS NOT NULL AND promo_code IS NOT NULL AND sales_date between '"+fromDateString+"' AND '"+toDateString+"'" +
								"GROUP BY store_number ORDER BY "+orderby_colName+" "+desc_Asc+" LIMIT  "+start_Idx+","+endIdx;
			}else if(!promoCode.equals("") && fromDateString.equals(Constants.STRING_NILL) && toDateString.equals(Constants.STRING_NILL)){
				qry = "SELECT store_number, ROUND(SUM((quantity*sales_price)+tax-(IF(discount is null,0,discount))),2) AS REVENUE, COUNT(sales_id) as COUNT ,sales_date " +
						"FROM retail_pro_sales WHERE user_id ="+userId+" AND promo_code ='"+promoCode+"' " +
								"AND store_number IS NOT NULL AND promo_code IS NOT NULL "+
								"GROUP BY store_number ORDER BY "+orderby_colName+" "+desc_Asc+" LIMIT  "+start_Idx+","+endIdx;
			}else if(promoCode.equals("") && fromDateString.equals(Constants.STRING_NILL) && toDateString.equals(Constants.STRING_NILL)){
				qry = "SELECT store_number, ROUND(SUM((quantity*sales_price)+tax-(IF(discount is null,0,discount))),2) AS REVENUE, COUNT(sales_id) as " +
						"COUNT ,sales_date FROM retail_pro_sales WHERE user_id ="+userId+" AND " +
						"store_number IS NOT NULL AND promo_code IS NOT NULL GROUP BY store_number ORDER BY "+orderby_colName+" "+desc_Asc+" LIMIT  "+start_Idx+","+endIdx;
			}
			
			//String qry ="SELECT promo_code, ROUND(SUM((quantity*sales_price)+tax),2) AS REVENUE , COUNT(customer_id) AS COUNT FROM  retail_pro_sales  WHERE user_id ="+userId+" AND promo_code IS NOT NULL GROUP BY promo_code ORDER BY sales_date DESC LIMIT  "+start_Idx+","+endIdx;
			logger.info("findPromocodeList is >>>::"+qry);
			return jdbcTemplate.queryForList(qry);
			
		} catch (DataAccessException e) {
			logger.error("Exception ::" , e);
			return null;
		}
    	
    } // findStoreRedemptionList 
   
 
   
   
    
	public  int findTotalCount(long userId,String fromDateString, String toDateString) {
	    	
		
		//String qry ="SELECT  COUNT(DISTINCT promoCode) FROM  RetailProSalesCSV  WHERE userId ="+userId+" AND promoCode IS NOT NULL";
		String qry ="SELECT  COUNT(DISTINCT promoCode) FROM  RetailProSalesCSV  WHERE userId ="+userId+" AND promoCode " +
				"IS NOT NULL AND storeNumber IS NOT NULL AND salesDate between '"+fromDateString+"' AND '"+toDateString+"'";
		
		if(fromDateString.equals(Constants.STRING_NILL) && fromDateString.equals(Constants.STRING_NILL)){
			qry ="SELECT  COUNT(DISTINCT promoCode) FROM  RetailProSalesCSV  WHERE userId ="+userId+" AND promoCode " +
					"IS NOT NULL AND storeNumber IS NOT NULL"; 
		}
		int count =   ((Long)getHibernateTemplate().find(qry).get(0)).intValue();
		
		
		return count;
	    	
	} // findPromocodeList
	
	 
		public  int findTotalCountOfStoreRelatedToPromo(long userId, String promoCode,String fromDateString, String toDateString) {
		    	
			
			String qry = "SELECT COUNT(sales_id) as COUNT FROM retail_pro_sales WHERE user_id ="+userId+" " +
					"AND promo_code ='"+promoCode+"' AND store_number IS NOT NULL AND sales_date between '"+fromDateString+"' AND '"+toDateString+"'GROUP BY store_number";
			
			if(fromDateString.equals(Constants.STRING_NILL) && fromDateString.equals(Constants.STRING_NILL)){
				qry = "SELECT COUNT(sales_id) as COUNT FROM retail_pro_sales WHERE user_id ="+userId+" " +
						"AND promo_code ='"+promoCode+"' AND store_number IS NOT NULL GROUP BY store_number";
			}
			
			List<Map<String, Object>> promoObjArrList = jdbcTemplate.queryForList(qry);
			//System.out.println("promoObjArrList.size()>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+promoObjArrList.size());
			return promoObjArrList.size();
		    	
		} // findPromocodeList
		
		 
		public  int findTotalCountOfPromoRelatedToStore(long userId, String storeNumber,String fromDateString, String toDateString) {
		    	
		
			
			String qry = "SELECT COUNT(sales_id) as COUNT FROM retail_pro_sales WHERE user_id ="+userId+" " +
					"AND store_number ='"+storeNumber+"' AND promo_code IS NOT NULL  AND sales_date between '"+fromDateString+"' AND '"+toDateString+"' GROUP BY promo_code";
			if(fromDateString.equals(Constants.STRING_NILL) && fromDateString.equals(Constants.STRING_NILL)){
				qry = "SELECT COUNT(sales_id) as COUNT FROM retail_pro_sales WHERE user_id ="+userId+" " +
						"AND store_number ='"+storeNumber+"' AND promo_code IS NOT NULL  GROUP BY promo_code";
			}
			
			List<Map<String, Object>> promoObjArrList = jdbcTemplate.queryForList(qry);
			//System.out.println("promoObjArrList.size()>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+promoObjArrList.size());
			return promoObjArrList.size();
		    	
		} // findPromocodeList
	
	 
    public  int findTotalCountForStore(long userId,String fromDateString, String toDateString) {
		    	
			
			String qry ="SELECT  COUNT(DISTINCT storeNumber) FROM  RetailProSalesCSV  WHERE userId ="+userId+" AND " +
					"storeNumber IS NOT NULL AND promoCode IS NOT NULL AND salesDate between '"+fromDateString+"' AND '"+toDateString+"'";
			
			
			if(fromDateString.equals(Constants.STRING_NILL) && fromDateString.equals(Constants.STRING_NILL)){
				qry ="SELECT  COUNT(DISTINCT storeNumber) FROM  RetailProSalesCSV  WHERE userId ="+userId+" " +
						"AND storeNumber IS NOT NULL AND promoCode IS NOT NULL";
			}
			
			int count =   ((Long)getHibernateTemplate().find(qry).get(0)).intValue();
			return count;
		    	
		} // findTotalCountForStore
	
	public RetailProSalesCSV findRecordByContactId(Long contactId, String typeStr ,Long userId) {
		try {
				// order by sales_date asc limit 1
				List<RetailProSalesCSV> list = null;
				String query = "";
				if(typeStr.trim().equals("ASC")) {
					query = "from RetailProSalesCSV  where userId ="+userId.longValue()+" and cid ="+contactId.longValue() +"  order by sales_date asc limit 1";
				}else if(typeStr.trim().equals("DESC")) {
					query = "from RetailProSalesCSV  where  userId ="+userId.longValue()+" and cid ="+contactId.longValue() +"  order by sales_date desc limit 1";
				}
				logger.info("purchase info query >>"+query);
				
				list  = getHibernateTemplate().find(query);
				
				if( list!= null && list.size() >0) {
					return list.get(0);
				}
				return null;
			} catch (DataAccessException e) {
				// TODO Auto-generated catch block
				logger.error("Exception ::" , e);
				return null;
			}
	} //findRecordByCustomerId
	
	public RetailProSalesCSV findRecordByContactId(String contactIds, String typeStr ,Long userId) {
		try {
				// order by sales_date asc limit 1
				List<RetailProSalesCSV> list = null;
				String query = "";
				if(typeStr.trim().equals("ASC")) {
					query = "from RetailProSalesCSV  where userId ="+userId.longValue()+" and cid in ("+contactIds +")  order by sales_date asc limit 1";
				}else if(typeStr.trim().equals("DESC")) {
					query = "from RetailProSalesCSV  where  userId ="+userId.longValue()+" and cid in ("+contactIds +")  order by sales_date desc limit 1";
				}
				logger.info("purchase info query >>"+query);
				
				list  = getHibernateTemplate().find(query);
				
				if( list!= null && list.size() >0) {
					return list.get(0);
				}
				return null;
			} catch (DataAccessException e) {
				// TODO Auto-generated catch block
				logger.error("Exception ::" , e);
				return null;
			}
	} //findRecordByCustomerId
	
	
	public List findPurchaseDetBasedOnCustomerId(String customerId, long list_id, String limit) {
		
		try {
			logger.info("findPurchaseDetBasedOnCustomerId customerId ::"+customerId+ " :: and list Id is "+list_id);
			logger.info("limit ::"+limit);
			// order by sales_date asc limit 1
			List<RetailProSalesCSV> list = null;
			
			SELECT sum((quantity*sales_price)+tax ),sales_date   
			FROM retail_pro_sales  
			where list_id =998 and  customer_id ='6102225839011660016' group by sales_date order by sales_date desc limit 5 ;
			String qry = "";
			if(limit.trim().equals(Constants.LIMIT)) {
				qry = "SELECT ROUND(sum((quantity*sales_price)+tax),2 ),sales_date  " +
					  " FROM retail_pro_sales " +
					  " where list_id ="+list_id+" and  customer_id ='"+customerId +"' " +
					  " group by sales_date order by sales_date desc limit 5 ";
				
				//list  = getHibernateTemplate().find("from RetailProSalesCSV  where listId ="+list_id+" and  customerId ='"+customerId +"'  order by sales_date desc limit 5");
			}else if(limit.trim().equals(Constants.VIEW_ALL)) {
				
				qry = "SELECT ROUND(sum((quantity*sales_price)+tax),2), sales_date " +
					  " FROM retail_pro_sales " +
					  " where list_id ="+list_id+" and  customer_id ='"+customerId +"' " +
					  " group by sales_date order by sales_date desc ";
				//list  = getHibernateTemplate().find("from RetailProSalesCSV  where listId ="+list_id+" and  customerId ='"+customerId +"'  order by sales_date desc ");
			}
			
			
			logger.info("QUERY is ::"+qry);
			list = jdbcTemplate.query(qry, new RowMapper() {
				
			 public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				 
				 Object[] tempObjArr =  new Object[2];
				 tempObjArr[0] = rs.getDouble(1);
				 tempObjArr[1] = rs.getDate(2);
				return tempObjArr;
			 	}
			
			});
			
			
			if(list.size() >0) {
				return list;
			}else 
			return null;
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
			return null;
		}
		
		
	}
	
	
	public List<Map<String, Object>> findPurchaseDetBasedOnCid(Long contactId, Long userId, String limit) {
		
		try {
			// order by sales_date asc limit 1
			List<RetailProSalesCSV> list = null;
			
			SELECT sum((quantity*sales_price)+tax ),sales_date   
			FROM retail_pro_sales  
			where list_id =998 and  customer_id ='6102225839011660016' group by sales_date order by sales_date desc limit 5 ;
			String qry = "";
			if(limit.trim().equals(Constants.LIMIT)) {
				qry = "SELECT ROUND(sum((quantity*sales_price)+tax-(IF(discount is null,0,discount))),2 ) as SALES_PRICE,sales_date  " +
					  " FROM retail_pro_sales " +
					  " where user_id ="+userId.longValue()+" and  cid ="+contactId.longValue() +" " +
					  " group by sales_date order by sales_date desc limit 5 ";
				
				//list  = getHibernateTemplate().find("from RetailProSalesCSV  where listId ="+list_id+" and  customerId ='"+customerId +"'  order by sales_date desc limit 5");
			}else if(limit.trim().equals(Constants.VIEW_ALL)) {
				
				qry = "SELECT ROUND(sum((quantity*sales_price)+tax-(IF(discount is null,0,discount))),2) as SALES_PRICE, sales_date " +
					  " FROM retail_pro_sales " +
					  " where user_id ="+userId.longValue()+" and  cid ="+contactId.longValue() +" " +
					  " group by sales_date order by sales_date desc ";
				//list  = getHibernateTemplate().find("from RetailProSalesCSV  where listId ="+list_id+" and  customerId ='"+customerId +"'  order by sales_date desc ");
			}
			
			
			logger.info("QUERY is ::"+qry);
			return jdbcTemplate.queryForList(qry);
			
			
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
			return null;
		}
		
		
	}
	
	
	public List<Map<String, Object>> findSkuDetBasedOnCid(Long contactId, long userId, String limit) {
		
		
		try {
			List<Object[]> latestClickCampList = null;
			
			String qry = "";
			
			if(limit.equals(Constants.LIMIT) ) {
				qry =" SELECT sales.reciept_number, sales.sku, sku.item_category, sum(sales.quantity) as qty, sales.sales_date " + 
					 " FROM  retail_pro_sku sku, retail_pro_sales sales  " +
					 " where sales.user_id ="+userId+" AND sku.user_id ="+userId+" " +
					 " AND sku.sku_id = sales.inventory_id and sales.cid= "+contactId+"  " +
					 " group by sales.sku order by sales_date desc limit 5"; 
				
			}
			else if(limit.equals(Constants.VIEW_ALL)) {
				qry ="SELECT sales.reciept_number, sales.sku, sku.item_category, sum(sales.quantity) as qty, sales.sales_date " + 
					 " FROM  retail_pro_sku sku, retail_pro_sales sales " +
					 " where sales.user_id ="+userId+" AND sku.user_id ="+userId+"  " +
					 " AND sku.sku_id = sales.inventory_id and sales.cid= "+contactId+"  " +
					 " group by sales.sku order by sales_date desc ";   
			}
			logger.info("findSkuDetBasedOnCUstimerId query is ::"+qry);
			return jdbcTemplate.queryForList(qry);
			
		} catch (DataAccessException e) {
			logger.error("Exception ::" , e);
			return null;
		}
	} // findSkuDetBasedOnCUstimerId
	
	
	public List findItemCategBasedOnCont(Long conatctId, Long userId, String limit) {
		try {
			List<Object[]> itemCategoryList = null;
			
			logger.info("findItemCategBasedOnCont conatctId ::" +conatctId +" >>>>and userId  is ::"+userId +" :: limit is >>"+limit);
			String qry = "";
			
			if(limit.equals(Constants.LIMIT) ) {
				qry ="SELECT distinct(sku.item_category) ,sku.vendor_code, sum(sales.quantity) as qty " + 
					 " FROM retail_pro_sku sku , retail_pro_sales sales  " +
					 " where  sales.user_id ="+userId.longValue()+"  AND sku.user_id ="+userId.longValue()+"   and sku.sku_id = sales.inventory_id and sales.cid= "+conatctId.longValue()+" " +
					 " group by sku.item_category order by sales.sales_date desc limit 5"; 
				
			}
			else if(limit.equals(Constants.VIEW_ALL)) {
				qry ="SELECT distinct(sku.item_category) , sku.vendor_code, sum(sales.quantity) as qty " + 
					 " FROM retail_pro_sku sku,   retail_pro_sales sales  " +
					 " where  sales.user_id ="+userId.longValue()+"  AND sku.user_id ="+userId.longValue()+"  and sku.sku_id = sales.inventory_id and sales.cid= "+conatctId.longValue()+" " +
					 " group by sku.item_category order by sales.sales_date desc "; 
			}
			logger.info(" findItemCategBasedOnCont query is ::"+qry);
			return jdbcTemplate.queryForList(qry);
			
		} catch (DataAccessException e) {
			logger.error("Exception ::" , e);
			return null;
		}
	} // findItemCategBasedOnCont
	
	
	public List<Map<String, Object>> findPromoBasedOnCont(Long contactId, Long userId, String limit) {
		
		try {
//			List<Object[]> promoCodeList = null;
			
			logger.info("contactId ::" +contactId +" >>>>and list_id is ::"+userId);
			String qry = "";
			
			
			if(limit.equals(Constants.LIMIT) ) {
				qry ="SELECT sales.promo_code, sales.sales_date, ROUND((sales.quantity*sales.sales_price)+sales.tax-(IF(sales.discount is null,0,sales.discount)),2) as price, sku.store_number " + 
					 " FROM retail_pro_sku sku, retail_pro_sales sales  " +
					 " where  sales.user_id ="+userId.longValue()+"  AND sku.user_id ="+userId.longValue()+" and sku.sku_id=sales.inventory_id " +
					 " and sales.cid= "+contactId.longValue()+" and sales.promo_code is not null " +
					 " order by sales_date desc limit 5"; 
				
			}
			else if(limit.equals(Constants.VIEW_ALL)) {
				qry ="SELECT sales.promo_code, sales.sales_date, ROUND((sales.quantity*sales.sales_price)+sales.tax-(IF(sales.discount is null,0,sales.discount)),2) , sku.store_number " + 
					 " FROM retail_pro_sku sku, retail_pro_sales sales  " +
					 " where  sales.user_id ="+userId.longValue()+"  AND sku.user_id ="+userId.longValue()+"  and sku.sku_id=sales.inventory_id " +
					 " and sales.cid= "+contactId.longValue()+" and sales.promo_code is not null " +
					 " order by sales_date desc "; 
			}
			logger.info(" findPromoBasedOnCont query is ::"+qry);
			
			return jdbcTemplate.queryForList(qry);
			
			
		} catch (DataAccessException e) {
			logger.error("Exception ::" , e);
			return null;
		}
		
		
	}
	public Object[] getLastPurchaseAmountByContactId(Long contactId, String lastPurDate, Long userId) {
		List<Object[]> lastPurAmtArr = null;
		String qry = " SELECT cid, ROUND(SUM((quantity * sales_price) + tax-(IF(discount is null,0,discount))),2) " +
				     " FROM  retail_pro_sales " + 
				     " WHERE cid = " + contactId +
				     " AND user_id = " + userId +
				     " AND sales_date = '" + lastPurDate +"'" +
				     " GROUP BY reciept_number " ;
		lastPurAmtArr = jdbcTemplate.query(qry, new RowMapper() {
			 public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				 
				 Object[] object =  new Object[2];
				 object[0] = rs.getDouble(1); // contact id
				 object[1] = rs.getDouble(2); // last purchase amount
				 
				return object;
			 }
			
		});
		if(lastPurAmtArr != null && lastPurAmtArr.size() > 0) {
			return lastPurAmtArr.get(0);
		}
		return null;
		
	}
	
	public List<Object[]> getLastPurchaseSKUDetailsByContactId(Long contactId, String lastPurDate, Long userId){
		List<Object[]> skuListArr = null;
		String qry = " SELECT item_sid, sku, sales_price, SUM(quantity), ROUND(SUM((quantity * sales_price) + tax-(IF(discount is null,0,discount))),2)" +
					" FROM retail_pro_sales where user_id = " + userId +
					" AND cid = " + contactId +
					" AND sales_date = '" +lastPurDate+ "'" +
					" GROUP BY item_sid" ;
		try {
		skuListArr = jdbcTemplate.query(qry, new RowMapper() {
			 public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				 
				 Object[] object =  new Object[5];
				 object[0] = rs.getString(1); // item sid
				 object[1] = rs.getString(2); // sku
				 object[2] = rs.getDouble(3); // sales price
				 object[3] = rs.getString(4); // quantity
				 object[4] = rs.getDouble(5); // amount

				 return object;
			 }
			
		});
		return skuListArr;
		}catch (Exception e) {
			logger.error("Exception ::" , e);
			return null;
		}
		if(skuListArr != null && skuListArr.size() > 0) {
			return skuListArr.get(0);
		}
		return null;
	}
	
	public List<Object[]> getLastPurchaseSKUDetailsByDocsid(String docSid, Long userId){
		List<Object[]> skuListArr = null;

		String qry ="SELECT  sales.sku,sku.item_category, sales.sales_price, sales.quantity"
				+ " FROM retail_pro_sku sku,   retail_pro_sales sales"
				+ "	WHERE sku.sku_id = sales.inventory_id AND sales.user_id ="+userId.longValue()
				+ " AND sales.doc_sid = '"+ docSid +"' ";
		logger.info("SKUDetailsByDocsid.:"+qry);
		try {
			skuListArr = jdbcTemplate.query(qry, new RowMapper() {
				public Object mapRow(ResultSet rs, int rowNum) throws SQLException {

					Object[] object =  new Object[4];
					object[0] = rs.getString(1); // sku
					object[1] = rs.getString(2); // item category
					object[2] = rs.getDouble(3); // sales price
					object[3] = rs.getLong(4); // quantity
					return object;
				}

			});
			return skuListArr;
		}catch (Exception e) {
			logger.error("Exception ::" , e);
			return null;
		}
	}
	
	
	public Object[] getTotalRevenueAndNoOfPurchase(Long userId,String startDate,String endDate) {
		
		List<Object[]> optintelActivityArr = null;
		
		String qry ="SELECT  ROUND(SUM((quantity*sales_price) + tax-(IF(discount=null,0,discount))),2) , COUNT(distinct reciept_number) FROM  retail_pro_sales  " +
				" WHERE user_id="+ userId + " AND sales_date >='" + startDate + "' AND sales_date <='"+ endDate + "'" ;
		String qry ="SELECT  ROUND(SUM((quantity*sales_price) + tax-(IF(discount is null,0,discount))),2) , COUNT(distinct doc_sid) FROM  retail_pro_sales  " +
				" WHERE user_id="+ userId + " AND sales_date >='" + startDate + "' AND sales_date <='"+ endDate + "'" ;
		logger.info("getTotalRevenueAndNoOfPurchase >>>>>"+qry);
		optintelActivityArr = jdbcTemplate.query(qry, new RowMapper() {
			 public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				 
				 Object[] object =  new Object[2];
				 object[0] = rs.getDouble(1);
				 object[1] = rs.getInt(2);
				 
				return object;
			 }
			
		});
		
		
		if(optintelActivityArr != null && optintelActivityArr.size() > 0) {
			return optintelActivityArr.get(0);
		}
		return null;
	}
	
	public Object[] getPromosTotalAndRevenue(Long userId,String startDate,String endDate) {
		
		List<Object[]> optintelActivityArr = null;
		
		String qry ="SELECT  ROUND(SUM((quantity*sales_price) + tax-(IF(discount is null,0,discount))),2), COUNT(distinct reciept_number) FROM  retail_pro_sales  WHERE user_id="+ userId + " AND sales_date >= '"+ startDate +"' AND sales_date <= '"+ endDate +"' AND promo_code IS NOT NULL";
		optintelActivityArr = jdbcTemplate.query(qry, new RowMapper() {
			 public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				 
				 Object[] object =  new Object[2];
				 object[0] = rs.getDouble(1);
				 object[1] = rs.getInt(2);
				 
				return object;
			 }
			
		});
		
		
		if(optintelActivityArr != null && optintelActivityArr.size() > 0) {
			return optintelActivityArr.get(0);
		}
		return null;
	}
	
	public Object[] getReturningAndNewCustomersCount(Long listId) {
		
		List<Object[]> optintelActivityArr = null;
		
		String qry ="SELECT  SUM(sales_price + tax), COUNT(distinct reciept_number) FROM  retail_pro_sales  WHERE list_id="+ listId + " AND promo_code IS NOT NULL";
		optintelActivityArr = jdbcTemplate.query(qry, new RowMapper() {
			 public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				 
				 Object[] object =  new Object[2];
				 object[0] = rs.getDouble(1);
				 object[1] = rs.getInt(2);
				 
				return object;
			 }
			
		});
		
		
		if(optintelActivityArr != null && optintelActivityArr.size() > 0) {
			return optintelActivityArr.get(0);
		}
		return null;
	}
	
	public int getNewCustomers(Long userId, String startDate,String endDate) {
		try {
//			String qry = "SELECT count(r.sales_id) from retail_pro_sales r,contacts c where c.list_id=" + listId + " AND r.list_id=" + listId + " AND r.customer_id=c.external_id AND c.created_date >= '" + startDate + "' AND c.created_date <= '" + endDate + "'";
			String qry = "SELECT count(r.sales_id) from retail_pro_sales r,contacts c where c.user_id=" + userId + " AND r.user_id=" + userId + " AND r.cid is not null" +
					" AND r.cid=c.cid AND c.created_date >= '" + startDate + "' AND c.created_date <= '" + endDate + "'";
			
			
			
//			String qry = "SELECT COUNT(cid) FROM contacts WHERE user_id=" + userId + " AND  external_id IS NOT NULL AND created_date >= '" + startDate + "' AND created_date <= '" + endDate + "'";
			
			String qry =" SELECT count(distinct r.cid) " +
				 " from retail_pro_sales r " +
				 " where r.user_id= " + userId + " AND r.cid is not null " +
				 " and r.sales_date >= '" + startDate + "' AND r.sales_date <= '" + endDate + "' " +
				 " and r.cid not in (SELECT distinct rs.cid " +
				 					" from retail_pro_sales rs" +
				 					" where rs.user_id= " + userId + " AND rs.cid is not null " +
				 					" and rs.sales_date > '" + endDate + "' AND r.sales_date < '" + startDate + "')";
			
			
			 String qry = " SELECT COUNT(t.cidcount) FROM  ( 	"
					+ " SELECT  COUNT(r.cid) AS cidcount FROM retail_pro_sales r WHERE r.user_id= "
					+ +userId +" AND r.cid IS NOT NULL GROUP BY r.cid HAVING MIN(r.sales_date) BETWEEN "
							+ " '"+startDate+"' AND '" + endDate + "' ) as t ";
			

			
			logger.info(" >>>>getNewCustomers  qry ::"+qry);
			int newCustomerCount = jdbcTemplate.queryForInt(qry);
			logger.info(" new Customer Count ::"+newCustomerCount);
			return newCustomerCount;
			List<Object[]> objArrlist = jdbcTemplate.query(qry, new RowMapper() {
				 public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
					 
					 Object[] object =  new Object[1];
					 object[0] = rs.getInt(1);
					 
					return object;
				 }
				
			});
			
			if(objArrlist != null) {
				return objArrlist.get(0);
			}
			return null;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
			return 0;
		}
	}
	
	public Object[] getReturningCustomers(Long userId, String startDate) {
		
		
		String qry = "SELECT COUNT(cid) " +
				     " FROM contacts " +
				     " WHERE user_id=" + userId.longValue() + " AND  external_id is not null AND created_date < '"+ startDate +"'";
		
		logger.info("getReturningCustomers ..."+qry);
		List<Object[]> objArrlist = jdbcTemplate.query(qry, new RowMapper() {
			 public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				 
				 Object[] object =  new Object[1];
				 object[0] = rs.getInt(1);
				 
				return object;
			 }
			
		});
		
		if(objArrlist != null) {
			return objArrlist.get(0);
		}
		return null;
	}
	
	
	
	public int getReturningCustomers(Long userId, String startDate,String endDate) {
		
		select count(distinct cid ) 
		from retail_pro_sales r 
		where r.user_id=4 and r.cid is not null 
		and r.sales_date >= '2013-01-21 00:00:00' AND r.sales_date <= '2013-02-27 23:59:59'
		and r.cid in (select distinct rs.cid 
				from retail_pro_sales rs
				where rs.user_id=4 and rs.cid is not null
				and rs.sales_date < '2013-01-21 00:00:00');
		
		
		
		String qry ="select count(distinct cid ) " +
				 " from retail_pro_sales r " +
				 " where r.user_id= " + userId + " AND r.cid is not null " +
				 " and r.sales_date >= '" + startDate + "' AND r.sales_date <= '" + endDate + "' " +
				 " and r.cid in (SELECT distinct rs.cid " +
				 					" from retail_pro_sales rs" +
				 					" where rs.user_id= " + userId + " AND rs.cid is not null " +
				 					" and r.sales_date < '" + startDate + "')";
		
		
		 String qry = " SELECT  count(DISTINCT rs.cid) FROM retail_pro_sales rs JOIN "+ 
					" (SELECT  DISTINCT cid from retail_pro_sales   WHERE user_id =" + userId + " AND "+ 
					" sales_date BETWEEN '" + startDate + "' AND '" + endDate + "' AND cid is not null "+ 
					" ) o ON rs.cid = o.cid WHERE rs.user_id =" + userId + " AND rs.sales_date < '" + startDate + "' AND rs.cid is not null ";

		
			logger.info(" >>>>getReturningCustomers  qry ::"+qry);
			int returnCustCount = jdbcTemplate.queryForInt(qry);
			logger.info(" new Customer Count ::"+returnCustCount);
			return returnCustCount;
		
	}
	
	
	
	
	
	public String getHighestPurchasedSKU(long userId,String startDate, String endDate) {
		
		String sql = "SELECT sku FROM retail_pro_sales " +
					 " WHERE user_id=" + userId +" " +
					 " AND sales_date >= '" + startDate + "' AND sales_date <='" + endDate + "' " +
					" GROUP BY sku ORDER BY sales_price DESC LIMIT 0,1";
		
		String sql = "SELECT count(d.inventory_id) as cnt,inventory_id FROM (SELECT  inventory_id from retail_pro_sales rs " +
				 " WHERE user_id=" + userId +" " +
				 " AND sales_date BETWEEN '" + startDate + "' AND '" + endDate + "' " +
				" GROUP BY inventory_id,doc_sid) AS d group by d.inventory_id ORDER BY cnt DESC LIMIT 1";
		
		
		
		
		logger.info("getHighestPurchasedSKU ::"+sql);
		List<Map<String, Object>> skuList = jdbcTemplate.queryForList(sql);
		String skuStr = null;
		if(skuList != null && skuList.size() > 0) {
			for (Map<String, Object> eachMap : skuList) {
				if(eachMap.get("inventory_id") != null) skuStr = eachMap.get("inventory_id").toString();
				break;
			}
		}
		return skuStr;
		
	}

	public String getFrequentlyPurchasedItemCategory(long userId,String startDate, String endDate) {
		
		String sql = "SELECT sku FROM retail_pro_sales " +
					 " WHERE user_id=" + userId +" " +
					 " AND sales_date >= '" + startDate + "' AND sales_date <='" + endDate + "' " +
					" GROUP BY sku ORDER BY sales_price DESC LIMIT 0,1";
		
		String sql = "SELECT count(d.inventory_id) as cnt,item_category FROM (SELECT  sku.item_category,rs.inventory_id from retail_pro_sales rs, retail_pro_sku sku " +
			     " WHERE rs.user_id=" + userId +" AND sku.user_id=" +userId+ " AND rs.inventory_id=sku.sku_id "+
			     " AND sales_date BETWEEN '" + startDate + "' AND '" + endDate + "' " +
			    " GROUP BY rs.inventory_id,rs.doc_sid, sku.item_category) AS d group by d.item_category ORDER BY cnt DESC LIMIT 1";
		
		
		
		
		logger.info("getFrequentlyPurchasedItemCategory ::"+sql);
		List<Map<String, Object>> itemList = jdbcTemplate.queryForList(sql);
		String itemStr = null;
		if(itemList != null && itemList.size() > 0) {
			for (Map<String, Object> eachMap : itemList) {
				if(eachMap.get("item_category") != null) itemStr = eachMap.get("item_category").toString();
				break;
			}
		}
		return itemStr;
		
	}
	
	
	public String getMaxCountSKUProd(long userId,String startDate, String endDate) {
			
			String sql = "SELECT sku FROM retail_pro_sales " +
						 " WHERE user_id=" + userId +" " +
						 " AND sales_date >= '" + startDate + "' AND sales_date <='" + endDate + "' " +
						 " GROUP BY sku order by count(sku) DESC ,sales_date DESC, sales_id DESC   LIMIT 0,1";
		
		String sql = "SELECT d.store_number FROM (SELECT sum((quantity*sales_price)+tax-(IF(discount is null,0,discount))) as REVENUE  , store_number  FROM retail_pro_sales rs " +
				 " WHERE user_id=" + userId +" " +
				 " AND sales_date BETWEEN '" + startDate + "' AND '" + endDate + "' " +
				" GROUP BY store_number  ) as d ORDER BY d.revenue DESC LIMIT 1";
		
			logger.info("getMaxCountSKUProd >>> "+sql);
			List<Map<String, Object>> maxSkuList = jdbcTemplate.queryForList(sql);
			String maxSkuStr = null;
			if(maxSkuList != null && maxSkuList.size() > 0) {
				for (Map<String, Object> eacMap : maxSkuList) {
					if(eacMap.containsKey("store_number")){
						if(eacMap.get("store_number") != null) maxSkuStr = eacMap.get("store_number").toString();
						break;
					}
					
				}
			}
			return maxSkuStr;
			
		} // getMaxCountSKUProd
	
	
	
	

	
	
	
	
	
	
	public String getHighestRevenueDay(long userId,String startDate, String endDate) {
		
		SELECT DATE_FORMAT(sales_date , '%Y-%m-%d'),  sum(sales_price) FROM retail_pro_sales 
		 WHERE list_id=947  AND sales_date >= '2012-02-01 00:00:00' AND sales_date <='2012-07-31 23:59:59'  
		group by DATE_FORMAT(sales_date , '%Y-%m-%d') order by 2 desc limit 1 
		
		String sql = "SELECT DATE_FORMAT(sales_date , '%Y-%m-%d'),  ROUND(sum((quantity*sales_price)+tax-(IF(discount is null,0,discount))),2) as REVENUE " +
					 " FROM retail_pro_sales  WHERE user_id=" + userId +" " +
					 " AND sales_date >= '" + startDate + "' AND sales_date <='" + endDate + "' " +
					 " group by DATE_FORMAT(sales_date , '%Y-%m-%d') order by REVENUE desc limit 1";
		
		logger.info("getHighestRevenueDay ..."+sql);
		List<Object[]> objArrList = jdbcTemplate.query(sql, new RowMapper() {
			 public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				 
				 Object[] object =  new Object[2];
				 object[0] = rs.getDate(1);
				 object[1] = rs.getDouble(2);
				return object;
			 }
			
		});
		
		if(objArrList != null && objArrList.size() > 0) {
			Object[] obj = new Object[1];
			obj = objArrList.get(0);
			Calendar cal=Calendar.getInstance();
			cal.setTime((Date)obj[0]);
			
			return MyCalendar.calendarToString(cal, MyCalendar.FORMAT_STDATE);
		}
		
		return null;
	}
	
	
	
	//getting TotalShoppers Count between the Dates
	public long findTotalShoppers(long listId,  String startDate, String endDate) {
		
		String 	qury = "SELECT  COUNT(DISTINCT customerId) " +
					" FROM  RetailProSalesCSV " +
					" WHERE listId="+listId+
					" AND salesDate BETWEEN '"+ startDate +"' AND '"+endDate+"' ";
		logger.info("query is..."+qury);
		return((Long) (getHibernateTemplate().find(qury).get(0))).longValue();
		
	} //findTotalShoppers
	
	public List<Map<String, Object>> findTotalShoppers(long userId,  String startDate, String endDate) {
		
		String qry = "SELECT count(DISTINCT cid) AS CONTACTID, ROUND(SUM((quantity*sales_price) + tax-(IF(discount is null,0,discount))),2) AS REVENUE" +
					 " FROM retail_pro_sales " +
					 " WHERE user_id=" + userId + 
					 " AND sales_date BETWEEN '"+ startDate +"' AND '"+endDate+"' ";
		
		logger.info("findTotalShoppers... >>>"+qry);
		return jdbcTemplate.queryForList(qry);
		List<Object[]> objArrlist = jdbcTemplate.query(qry, new RowMapper() {
			 public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				 
				 Object[] object =  new Object[2];
				 object[0] = rs.getLong(1);
				 object[1] = rs.getDouble(2);
				 
				return object;
			 }
			
		});
		
		if(objArrlist != null) {
			return objArrlist.get(0);
		}else{
			
			return null;
		}
	}
	
	
	
	
	public Long findTotalCountByServiceType(long userId, String serviceTypeStr, String startDate, String endDate) {
		
		String query =  "SELECT COUNT(DISTINCT sales.doc_sid) as visits " +
				" FROM  retail_pro_sales sales,(SELECT contact_id,service_type FROM contacts_loyalty " +
			    " WHERE user_id = " + userId + ") cl " +
				" WHERE sales.user_id = "+userId+" AND sales.cid IS NOT NULL AND cl.service_type='"+serviceTypeStr+"' " +
				" AND cl.contact_id = sales.cid " +
				" AND sales.sales_date BETWEEN '"+ startDate +"' AND '"+endDate+"' ";

		logger.info("findTotalRevenueByServiceType ...qury >>>> .."+query);
		 List<Map<String,Object>> tempList = jdbcTemplate.queryForList(query);
		   if(tempList != null && tempList.size() > 0) {
			   if(tempList.get(0) == null) {
				   return 0L;
			   }
			   return new Long (tempList.get(0).get("visits") != null ? (Long)tempList.get(0).get("visits"):0);
		   }
		   else return 0L;
} 
		
		
		List tempList = getHibernateTemplate().find(query);
		if(tempList != null && tempList.size() == 1) {
			return (Long)tempList.get(0);
		}
		else return 0L;

	} // findTotalCountByCardType
	
	public Double findTotalRevenueByServiceType(long userId,String serviceTypeStr, String startDate, String endDate) {
		
//		List totalRevnueList = null;
		
		logger.info("startDate .."+startDate);
		logger.info("endDate .."+endDate);
//		String qury = "";
		
		qury =  "SELECT  SUM(sales.salesPrice + sales.tax ) " +
				" FROM  RetailProSalesCSV sales,ContactsLoyalty cl " +
				" WHERE cl.userId="+userId+" AND cl.cardType IN("+cardTypeStr+") " +
				" AND cl.customerId = sales.customerId " +
				" AND sales.salesDate BETWEEN '"+ startDate +"' AND '"+endDate+"' ";
		
		String qury =  "SELECT  SUM(sales.sales_price * sales.quantity + sales.tax -  if(sales.discount is null, 0, sales.discount))  as amt" +
				" FROM  retail_pro_sales sales,(SELECT DISTINCT contact_id,service_type FROM contacts_loyalty " +
				" WHERE user_id = " + userId + ")cl " +
				" WHERE sales.user_id = "+userId+" AND sales.cid IS NOT NULL AND cl.service_type='"+serviceTypeStr+"' " +
				" AND cl.contact_id = sales.cid " +
				" AND sales.sales_date BETWEEN '"+ startDate +"' AND '"+endDate+"' ";

		
		logger.info("findTotalRevenueByServiceType ...qury >>>> .."+qury);
		 List<Map<String,Object>> tempList = jdbcTemplate.queryForList(qury);
		   if(tempList != null && tempList.size() > 0) {
			   if(tempList.get(0) == null) {
				   return 0.0;
			   }
			   return new Double (tempList.get(0).get("amt") != null ? (Double)tempList.get(0).get("amt"):0.0  );
		   }
		   else return 0.0;
	} // findTotalRevenueByServiceType
	
	
	public long findReedmptionByTypes(long userId, String startDate, String endDate) {
		
		
		
		String qury ="SELECT  count(distinct sales.reciept_number) " +
				" FROM  retail_pro_sales sales,contacts_loyalty cl " +
				" WHERE cl.user_id = "+userId+ " AND sales.user_id = "+userId+
				" AND cl.contact_id = sales.cid " +
				" AND sales_date BETWEEN '"+ startDate +"' AND '"+endDate+"' ";
		
		 logger.info("findReedmptionByTypes ..query is..."+qury);
		  
		  return(jdbcTemplate.queryForLong(qury));
		
		
	} // findReedmptionByTypes
	
	public List<Map<String , Object>> findAvgSpendTimeByLoyShopper(String startDate, String endDate, Long userId) {
		
		List<Object[]> tempList = null;

		String qry ="select  count(distinct cla.card_type) as count, ROUND(SUM((rs.quantity*rs.sales_price) + rs.tax -(IF(rs.discount is null,0,rs.discount))),2) as price " +
					" from contacts_loyalty cla,retail_pro_sales rs" +
					" where cla.user_id = "+userId+" and rs.user_id="+userId+"" +
					" and cla.card_type in ('loylty', 'Gift Card' , 'Both') " +
					" and rs.cid = cla.contact_id " +
					" and rs.sales_date between '"+ startDate +"' AND '"+endDate+"' ";
		
		logger.info("findAvgSpendTimeByLoyShopper query is ::"+qry);
		List<Map<String , Object>> qryList = jdbcTemplate.queryForList(qry);
		return qryList;
		
		
	}
	
	public List getSalesDateBetWeenDates(Long userId, String fromDateStr, String toDateStr, int firstCount, int count) {
		
		try {
			String qry = "SELECT DISTINCT Date(salesDate) From RetailProSalesCSV WHERE userId="+userId.longValue()+" AND DATE(salesDate) BETWEEN '"+fromDateStr+"' AND '"+toDateStr+"' ORDER BY salesDate  ";
			
			
			logger.info("qry ::"+qry);
			
			
			List<Object[]> retList = executeQuery(qry, firstCount, count);
			logger.info("retList ::"+retList.size());

			return retList;
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
			return null;
		}
	}
	
	
	public List getMissingReceiptNumbers(Long userId, long from, long to, int firstCount, int count) {
		
	
		try {
			String qry = "SELECT DISTINCT recieptNumber FROM RetailProSalesCSV WHERE userId="+userId.longValue()+" AND recieptNumber BETWEEN "+from+" AND "+to+" ORDER BY recieptNumber  ";
			
			
			logger.info("qry ::"+qry);
			
			
			List retList = executeQuery(qry, firstCount, count);
			logger.info("retList ::"+retList.size());
	
			return retList;
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
			return null;
		}
	
	}//getSalesDateBetWeenDates
	
	 DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public RetailProSalesCSV findSalesByPriority(TreeMap<String, List<String>> prioMap , RetailProSalesCSV sales,  Long userId) {
 		
 		try {
 			String query = "FROM RetailProSalesCSV WHERE userId="+userId+" ";
 			
 			if(logger.isDebugEnabled()) logger.debug("prioMap="+prioMap);
 			Set<String> keySet = prioMap.keySet();
 			List<RetailProSalesCSV> salesIdList = null;
 			
 			List<String> tempList=null;
 			outer:	for (String eachKey : keySet) {
				
 				if(logger.isDebugEnabled()) logger.debug("Key ="+eachKey);
 				tempList = prioMap.get(eachKey);
 				
 				int size = tempList.size();
 				for (String eachStr : tempList) {
					int index = Integer.parseInt(eachStr.substring(0,eachStr.indexOf('|')));
					
					String[] tempStr = eachStr.split("\\|");
					
					String valueStr = getSalesFieldValue(sales, tempStr[0]);
					
					size = size-1;
	 				if(valueStr == null || valueStr.trim().length() == 0){
	 					if(size != 0)continue;
	 					continue outer;
	 				}
					
					String posMappingDateFormatStr = tempStr[1].trim();
					
					if(posMappingDateFormatStr.toLowerCase().startsWith("date") && !tempStr[1].trim().toLowerCase().startsWith("udf")) {
						try {
							posMappingDateFormatStr =  posMappingDateFormatStr.substring(posMappingDateFormatStr.indexOf("(")+1, posMappingDateFormatStr.indexOf(")"));
							valueStr = format.format((new SimpleDateFormat(posMappingDateFormatStr).parse(valueStr)));
							query += " AND "+ POSFieldsEnum.findByOCAttribute(tempStr[0].trim()) + " = '"+ valueStr +"' ";
						} catch (ParseException e) {
							logger.error("Exception ::" , e);
							return null;
						}
					}
					else if(posMappingDateFormatStr.toLowerCase().startsWith("string") || posMappingDateFormatStr.toLowerCase().startsWith("udf")) {
//						query += " AND "+ eachStr.substring(eachStr.indexOf('|')+1) + " = '"+ lineStr[index] +"' ";
						query += " AND "+ POSFieldsEnum.findByOCAttribute(tempStr[0].trim()) + " = '"+ valueStr +"' ";
					}else {
						query += " AND "+ POSFieldsEnum.findByOCAttribute(tempStr[0].trim()) + " = "+ valueStr +" ";
					}
					
					
				} // for 
 				
 				if(logger.isDebugEnabled()) logger.debug("QUERY=="+query);
 				
 				salesIdList  = getHibernateTemplate().find(query);
 				
 				if(salesIdList.size() == 1) {
 					return salesIdList.get(0);
 				}
 				else if(salesIdList.size() > 1) {
 					if(logger.isDebugEnabled()) logger.debug("more than 1 object found :"+salesIdList.size());
 					return salesIdList.get(0);
 				}
 				
 				
			} // outer for
 			
 			return null;
 			
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
			return null;
		}
 		
 	}
	
	public String getSalesFieldValue(RetailProSalesCSV salesObj, String salesFieldStr) {

		if (salesFieldStr.equals(POSFieldsEnum.docSid.getOcAttr())) {return "" + salesObj.getDocSid();}
		else if (salesFieldStr.equals(POSFieldsEnum.quantity.getOcAttr())) {return ""+salesObj.getQuantity();}
		else if (salesFieldStr.equals(POSFieldsEnum.salesDate.getOcAttr())) {return ""+salesObj.getSalesDate();} 
		else if (salesFieldStr.equals(POSFieldsEnum.salesPrice.getOcAttr())) {return ""+salesObj.getSalesPrice();}
		else if (salesFieldStr.equals(POSFieldsEnum.tenderType.getOcAttr())) {return salesObj.getTenderType();}
		else if (salesFieldStr.equals(POSFieldsEnum.sku.getOcAttr())) {return salesObj.getSku();}
		else if (salesFieldStr.equals(POSFieldsEnum.externalId.getOcAttr())) {return salesObj.getCustomerId();}
		else if (salesFieldStr.equals(POSFieldsEnum.promoCode.getOcAttr())) {return salesObj.getPromoCode();}
		else if (salesFieldStr.equals(POSFieldsEnum.tax.getOcAttr())) {return ""+salesObj.getTax();}
		else if (salesFieldStr.equals(POSFieldsEnum.recieptNumber.getOcAttr())) {return ""+salesObj.getRecieptNumber();}
		else if (salesFieldStr.equals(POSFieldsEnum.itemSid.getOcAttr())) {return salesObj.getItemSid();}
		else if (salesFieldStr.equals(POSFieldsEnum.storeNumber.getOcAttr())) {return salesObj.getStoreNumber();}
		else if (salesFieldStr.equals(POSFieldsEnum.discount.getOcAttr())) {return ""+salesObj.getDiscount();}
		else return null;

	}
*/	
	
	public void  updateSalesToContactsMappings(Set<MastersToTransactionMappings> mappingSet, Long listId, Long userId) {
 		
		try {
			String salesToContactStr="";
			String salesToInventoryStr="";
			String db_parent_lable=null;
			String db_child_lable=null;
			
			for (MastersToTransactionMappings eachMtoT : mappingSet) {
				if(eachMtoT.getType().equals(Constants.TYPE_SALES_TO_CONTACTS)) {
					
					db_child_lable = POSFieldsEnum.findByOCAttribute(eachMtoT.getChildId().getCustomFieldName()).getDbColumn();
					
					if(db_child_lable==null && eachMtoT.getChildId().getCustomFieldName().toLowerCase().startsWith("udf")) {
						db_child_lable = eachMtoT.getChildId().getCustomFieldName().toLowerCase();
					}
					
					db_parent_lable = POSFieldsEnum.findByOCAttribute(eachMtoT.getParentId().getCustomFieldName()).getDbColumn();
					
					if(db_parent_lable==null && eachMtoT.getParentId().getCustomFieldName().toLowerCase().startsWith("udf")) {
						db_parent_lable = eachMtoT.getParentId().getCustomFieldName().toLowerCase();
					}
					
					salesToContactStr += "  AND rs."+db_child_lable + "= c."+db_parent_lable+" ";
				} // if
				
				else if(eachMtoT.getType().equals(Constants.TYPE_SALES_TO_Inventory)) {
					
					db_child_lable = POSFieldsEnum.findByOCAttribute(eachMtoT.getChildId().getCustomFieldName()).getDbColumn();
					
					if(db_child_lable==null && eachMtoT.getChildId().getCustomFieldName().toLowerCase().startsWith("udf")) {
						db_child_lable = eachMtoT.getChildId().getCustomFieldName().toLowerCase();
					}
					
					db_parent_lable = POSFieldsEnum.findByOCAttribute(eachMtoT.getParentId().getCustomFieldName()).getDbColumn();
					
					if(db_parent_lable==null && eachMtoT.getParentId().getCustomFieldName().toLowerCase().startsWith("udf")) {
						db_parent_lable = eachMtoT.getParentId().getCustomFieldName().toLowerCase();
					}
					
					salesToInventoryStr += "  AND   rs."+db_child_lable + "= s."+db_parent_lable+" ";
				} // if
				
			} 	
			
			 
			
			  /*String salesToContactsQry =" UPDATE retail_pro_sales rs , contacts  c  SET rs.cid=c.cid   WHERE c.list_id ="+listId+" AND rs.cid IS null  AND rs.list_id="+listId +  salesToContactStr;
			  String salesToInventoryQry =" UPDATE retail_pro_sales rs , retail_pro_sku s set rs.inventory_id=s.sku_id WHERE s.list_id ="+listId+" AND rs.inventory_id IS null AND rs.list_id="+listId +  salesToInventoryStr;
			  */
			
			String salesToContactsQry =" UPDATE retail_pro_sales rs , contacts  c  SET rs.cid=c.cid   WHERE c.user_id ="+userId+" AND rs.cid IS null  AND rs.user_id="+userId +  salesToContactStr;
			  String salesToInventoryQry =" UPDATE retail_pro_sales rs , retail_pro_sku s set rs.inventory_id=s.sku_id WHERE s.user_id="+userId+" AND rs.inventory_id IS null AND rs.user_id="+userId +  salesToInventoryStr;
			  
			  if(logger.isDebugEnabled()) logger.debug("sales to contacts: "+salesToContactsQry);
			  if(logger.isDebugEnabled()) logger.debug("sales to Inventory: "+salesToInventoryQry);
			  
			 // jdbcTemplate.batchUpdate(new String[] {salesToContactsQry,salesToInventoryQry});
			  jdbcTemplate.execute(salesToContactsQry);
			  jdbcTemplate.execute(salesToInventoryQry);
			
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
		}
 		 
 	  }
	
	 public void updateSalesAggregateData(Long sourceMlistId, Long userId) {
			
	    	/*String sql = "REPLACE INTO sales_aggregate_data(customer_id, tot_reciept_count ,"+
						"tot_purchase_amt, avg_purchase_amt, max_purchase_amt, list_id) " +
						"SELECT customer_id,  count(t_count) , sum(t_sum) ," +
						"	avg(t2.t_sum) , MAX(t_sum) ," +sourceMlistId+" "+
						"	 FROM (	SELECT customer_id,reciept_number, count(reciept_number) as t_count," +
						" sum((sales_price*quantity)+tax) as t_sum, list_id" +
						"	FROM retail_pro_sales AS t1 WHERE customer_id is not NULL" +
						"	and list_id ="+sourceMlistId+"  GROUP BY reciept_number " +
						") AS t2 group by customer_id";
			*/
	    	
	    	
	    	String sql = " REPLACE INTO sales_aggregate_data(customer_id, tot_reciept_count ," +
		    			" tot_purchase_amt, avg_purchase_amt,max_purchase_amt, list_id,cid,user_id) SELECT customer_id," +
		    			"  count(t_count) , sum(t_sum) ,avg(t2.t_sum) , MAX(t_sum) ," +sourceMlistId.longValue()+",cid, " +userId.longValue()+
		    			" FROM (	SELECT customer_id,reciept_number, count(reciept_number) as t_count," +
		    			" sum((sales_price*quantity)+tax-(IF(discount is null,0,discount))) as t_sum, list_id,cid, user_id FROM retail_pro_sales AS t1" +
		    			" WHERE cid is NOT NULL "+
		    			" AND user_id="+userId.longValue()+"  GROUP BY reciept_number) AS t2 " +
		    			" Where t2.cid IS NOT NULL  GROUP BY cid";
	    	logger.info("sql query is : "+sql);
			executeJdbcQuery(sql);
			
			
		}




	  /* public Calendar findLastpurchasedDate(Long  cid, Long userId) {
	   	
	    	try {
	    		
				String qry = " FROM RetailProSalesCSV WHERE userId ="+userId.longValue()+" and cid="+cid.longValue()+" ORDER BY salesDate DESC";
				//logger.debug("======================="+qry+"========================");
				List<RetailProSalesCSV> saleObjectList = executeQuery(qry, 0, 1);
				if(saleObjectList.size() == 1) {
					
					return saleObjectList.get(0).getSalesDate();
					
				}
				
				return null;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Exception ::" , e);
				return null;
			}
	    }
	   //Rajeev
	   @SuppressWarnings("unchecked")
	public String findLastpurchasedAmount(Long  cid, Long userId) {
		   	
			
	    	try {
	    		DecimalFormat d=new DecimalFormat("#0.00");
				String qry = "SELECT SUM((sales_price*quantity)+tax- if(discount is null, 0, discount)) as amt" +
						" FROM retail_pro_sales WHERE user_id ="+userId.longValue()+" " +
								"and cid="+cid.longValue()+" GROUP BY doc_sid ORDER BY sales_date DESC LIMIT 1";//ORDER BY salesDate DESC HAVING salesDate = max(salesDate)
				//logger.debug("======================="+qry+"========================");
				logger.info("query is :::: "+qry);
				List<Map<String,Object>> tempList = jdbcTemplate.queryForList(qry);
				   if(tempList != null && tempList.size() > 0) {
					   if(tempList.get(0) == null) {
						   return "0.0";
					   }
					   return new Double (tempList.get(0).get("amt") != null ? (Double)tempList.get(0).get("amt"):0.0  ).toString();
				   }
				   else return null;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Exception ::" , e);
				return null;
			}
	    }
	   
	   public String findLastpurchasedStore(Long cid , Long userId) {
	   	
	    	try {
	    		
				String qry = " FROM RetailProSalesCSV WHERE userId ="+userId.longValue()+" and cid="+cid.longValue()+" ORDER BY salesDate DESC";
				//logger.debug("======================="+qry+"========================");
				List<RetailProSalesCSV> saleObjectList = executeQuery(qry, 0, 1);
				if(saleObjectList.size() == 1) {
					return saleObjectList.get(0).getStoreNumber();
					
				}
				
				return null;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Exception ::" , e);
				return null;
			}
	    }

	   public Double findTotalRevenueByCustId(long userId, String customerId, String startDate, String endDate) {
			//Rajeev

			String query =  "SELECT  SUM(sales.sales_price + sales.tax -  if(sales.discount is null, 0, sales.discount))  as amt" +
					" FROM  retail_pro_sales sales" +
					" WHERE sales.user_id = "+userId+" AND sales.customer_id ='"+customerId+"'" +
					" AND sales.sales_date BETWEEN '"+ startDate +"' AND '"+endDate+"' ";
			logger.info("findTotalRevenueByServiceTypeAndRewardFlag ...query >>>> .."+query);
			 List<Map<String,Object>> tempList = jdbcTemplate.queryForList(query);
			   if(tempList != null && tempList.size() > 0) {
				   if(tempList.get(0) == null) {
					   return 0.0;
				   }
				   return new Double (tempList.get(0).get("amt") != null ? (Double)tempList.get(0).get("amt"):0.0  );
			   }
			   else return 0.0;
		} // findTotalRevenueByCustId
	   
	   public List<Object[]> getTierVisitsAndRevenue(Long userId,
			   Long prgmId, String startDateStr, String endDateStr,Long tierId) {
		   
		   String qry = "";
		   qry = "SELECT  COUNT(DISTINCT sales.doc_sid),ROUND(SUM((sales.quantity*sales.sales_price)+ sales.tax -(IF(sales.discount is null,0,sales.discount))),2) " +
					   " FROM  retail_pro_sales sales, (SELECT DISTINCT contact_id,program_tier_id FROM contacts_loyalty " +
					   " WHERE user_id = " + userId + " AND program_id = " + prgmId + " AND program_tier_id IS NOT NULL AND program_tier_id = " + tierId + ")cl " +
					   " WHERE sales.user_id = " + userId + " AND sales.cid IS NOT NULL AND sales.cid = cl.contact_id" + 
					   " AND sales.sales_date BETWEEN '" + startDateStr + "' AND '"+ endDateStr + "'" ;
					   //" GROUP BY sales.cid,sales.store_number,cl.program_tier_id " ;
		
		   List<Object[]> tempList = null;
		   
		   tempList = jdbcTemplate.query(qry, new RowMapper() {
			   public Object mapRow(ResultSet rs, int rowNum) throws SQLException {

				   Object[] object =  new Object[2];
				   object[0] = rs.getLong(1);
				   object[1] = rs.getDouble(2);

				   return object;
			   }
		   });
		   return tempList;
	   }
	   
	   public List<Object[]> getTierVisitsAndRevenue(Long userId,
			   Long prgmId, String startDateStr, String endDateStr,String storeNo,Long cardsetId,Long tierId) {
		   String storeQry = "";
		   String cardsetQry = "";
		   if(storeNo != null && storeNo.length() != 0) {
			   storeQry += " AND sales.store_number in ("+storeNo+")";
			}
			if(cardsetId != null) {
				cardsetQry += " AND card_set_id =" + cardsetId.longValue();
			}
		   
		   String qry = "";
		   qry = "SELECT  COUNT(DISTINCT sales.doc_sid),ROUND(SUM((sales.quantity*sales.sales_price)+ sales.tax -(IF(sales.discount is null,0,sales.discount))),2) " +
					   " FROM  retail_pro_sales sales, (SELECT DISTINCT contact_id,program_tier_id FROM contacts_loyalty " +
					   " WHERE user_id = " + userId + " AND program_id = " + prgmId + "  AND program_tier_id IS NOT NULL AND program_tier_id = " + tierId + cardsetQry +")cl " +
					   " WHERE sales.user_id = " + userId + " AND sales.cid IS NOT NULL AND sales.cid = cl.contact_id" +  storeQry +
					   " AND sales.sales_date BETWEEN '" + startDateStr + "' AND '"+ endDateStr + "'" ;
					   //" GROUP BY sales.cid,sales.store_number,cl.program_tier_id " ;
		
		   List<Object[]> tempList = null;
		   
		   tempList = jdbcTemplate.query(qry, new RowMapper() {
			   public Object mapRow(ResultSet rs, int rowNum) throws SQLException {

				   Object[] object =  new Object[2];
				   object[0] = rs.getLong(1);
				   object[1] = rs.getDouble(2);

				   return object;
			   }
		   });
		   return tempList;
	   }
	   
	   
	   
	   
	   public List<Object[]> getStorelevelKPI(Long userId,
			   Long prgmId, String startDateStr, String endDateStr) {
		   
		   String qry = "";
		   qry = "SELECT  sales.store_number,COUNT(DISTINCT sales.doc_sid),ROUND(SUM((sales.quantity*sales.sales_price)+ sales.tax -(IF(sales.discount is null,0,sales.discount))),2) " +
					   " FROM  retail_pro_sales sales, (SELECT DISTINCT contact_id FROM contacts_loyalty " +
					   " WHERE user_id = " + userId + " AND program_id = " + prgmId + ") cl  " +
					   " WHERE sales.user_id = " + userId + " AND sales.cid IS NOT NULL AND sales.cid = cl.contact_id" + 
					   " AND sales.sales_date BETWEEN '" + startDateStr + "' AND '"+ endDateStr + "' AND sales.store_number IS NOT NULL " +
					   " GROUP BY sales.store_number " ;
		
		   List<Object[]> tempList = null;
		   
		   tempList = jdbcTemplate.query(qry, new RowMapper() {
			   public Object mapRow(ResultSet rs, int rowNum) throws SQLException {

				   Object[] object =  new Object[3];
				   object[0] = rs.getString(1);
				   object[1] = rs.getLong(2);
				   object[2] = rs.getDouble(3);

				   return object;
			   }
		   });
		   return tempList;
	   }
	   
	   
	   

	   //Added for Loyalty Reports
	   public List<Object[]> getLoyaltyRevenueByPrgmId(Long userId,
			   Long prgmId, String startDateStr, String endDateStr, String storeNo, Long cardsetId, String typeDiff) {
		   String storeQry = "";
		   String cardsetQry = "";
		   if(storeNo != null && storeNo.length() != 0) {
			   storeQry += " AND sales.store_number in ("+storeNo+")";
			}
			if(cardsetId != null) {
				cardsetQry += " AND card_set_id =" + cardsetId.longValue();
			}
		   String qry = "";
		   if(typeDiff.equalsIgnoreCase("days")) { 
			   qry = "SELECT  ROUND(SUM((sales.quantity*sales.sales_price) + sales.tax -(IF(sales.discount is null,0,sales.discount))),2) , DATE(sales.sales_date) " +
					   " FROM  retail_pro_sales sales, (SELECT DISTINCT contact_id FROM contacts_loyalty " +
					   " WHERE user_id = " + userId + " AND program_id = " + prgmId + cardsetQry+ ") cl  " +
					   " WHERE sales.user_id = " + userId + " AND sales.cid IS NOT NULL AND sales.cid = cl.contact_id" + storeQry + 
					   " AND sales.sales_date BETWEEN '" + startDateStr + "' AND '"+ endDateStr + "'" +
					   " GROUP BY DATE(sales.sales_date) ORDER BY DATE(sales.sales_date) " ;
		   }
		   else if(typeDiff.equalsIgnoreCase("months")) {
			   qry = "SELECT  ROUND(SUM((sales.quantity*sales.sales_price) + sales.tax -(IF(sales.discount is null,0,sales.discount))),2) , MONTH(sales.sales_date) " +
					   " FROM  retail_pro_sales sales, (SELECT DISTINCT contact_id FROM contacts_loyalty " +
					   " WHERE user_id = " + userId + " AND program_id = " + prgmId + cardsetQry+ ") cl  " +
					   " WHERE sales.user_id = " + userId + " AND sales.cid IS NOT NULL AND sales.cid = cl.contact_id" + storeQry +
					   " AND sales.sales_date BETWEEN '" + startDateStr + "' AND '"+ endDateStr + "'" +
					   " GROUP BY MONTH(sales.sales_date) ORDER BY MONTH(sales.sales_date) " ;
		   }
		   else if(typeDiff.equalsIgnoreCase("years")) {
			   qry = "SELECT  ROUND(SUM((sales.quantity*sales.sales_price) + sales.tax -(IF(sales.discount is null,0,sales.discount))),2) , YEAR(sales.sales_date) " +
					   " FROM  retail_pro_sales sales, (SELECT DISTINCT contact_id FROM contacts_loyalty " +
					   " WHERE user_id = " + userId + " AND program_id = " + prgmId + cardsetQry+ ") cl  " +
					   " WHERE sales.user_id = " + userId + " AND sales.cid IS NOT NULL AND sales.cid = cl.contact_id" + storeQry +
					   " AND sales.sales_date BETWEEN '" + startDateStr + "' AND '"+ endDateStr + "'" +
					   " GROUP BY YEAR(sales.sales_date) ORDER BY YEAR(sales.sales_date) " ;
		   }

		   

		   List<Object[]> tempList = null;
		   
		   tempList = jdbcTemplate.query(qry, new RowMapper() {
			   public Object mapRow(ResultSet rs, int rowNum) throws SQLException {

				   Object[] object =  new Object[2];
				   object[0] = rs.getDouble(1);
				   object[1] = rs.getString(2);

				   return object;
			   }
		   });
		   return tempList;
	   }
	   
	   public Double getTotalRevenueByUserId(Long userId, String startDateStr,
			   String endDateStr) {
//Rajeev
		   String qry = " SELECT ROUND(SUM((quantity*sales_price) + tax - if(discount is null, 0, discount)),2) as amt FROM retail_pro_sales " +
				   " WHERE user_id="+ userId + " AND sales_date BETWEEN '" + startDateStr + "' AND '"+ endDateStr + "'" ;

		   List<Map<String,Object>> tempList = jdbcTemplate.queryForList(qry);
		   if(tempList != null && tempList.size() > 0) {
			   if(tempList.get(0) == null) {
				   return 0.0;
			   }
			   return new Double (tempList.get(0).get("amt") != null ? (Double)tempList.get(0).get("amt"):0.0  );
		   }
		   else return 0.0;

	   }

	public List<Object[]> getLoyaltyVisitsByPrgmId(Long userId, Long prgmId,
			String startDateStr, String endDateStr, String storeNo, Long cardsetId, String typeDiff) {
		String storeQry = "";
		   String cardsetQry = "";
		   if(storeNo != null && storeNo.length() != 0) {
			   storeQry += " AND sales.store_number in ("+storeNo+")";
			}
			if(cardsetId != null) {
				cardsetQry += " AND card_set_id =" + cardsetId.longValue();
			}
		   String qry = "";
		   if(typeDiff.equalsIgnoreCase("days")) { 
			   qry = "SELECT  COUNT(DISTINCT sales.doc_sid) , DATE(sales.sales_date) " +
					   " FROM  retail_pro_sales sales, (SELECT contact_id FROM contacts_loyalty " +
					   " WHERE user_id = " + userId + " AND program_id = " + prgmId + cardsetQry + ") cl  " +
					   " WHERE sales.user_id = " + userId + " AND sales.cid IS NOT NULL AND sales.cid = cl.contact_id" + storeQry + 
					   " AND sales.sales_date BETWEEN '" + startDateStr + "' AND '"+ endDateStr + "'" +
					   " GROUP BY DATE(sales.sales_date) ORDER BY DATE(sales.sales_date) " ;
		   }
		   else if(typeDiff.equalsIgnoreCase("months")) {
			   qry = "SELECT  COUNT(DISTINCT sales.doc_sid) , MONTH(sales.sales_date) " +
					   " FROM  retail_pro_sales sales, (SELECT contact_id FROM contacts_loyalty " +
					   " WHERE user_id = " + userId + " AND program_id = " + prgmId + cardsetQry+ ") cl  " +
					   " WHERE sales.user_id = " + userId + " AND sales.cid IS NOT NULL AND  sales.cid = cl.contact_id" + storeQry +
					   " AND sales.sales_date BETWEEN '" + startDateStr + "' AND '"+ endDateStr + "'" +
					   " GROUP BY MONTH(sales.sales_date) ORDER BY MONTH(sales.sales_date) " ;
		   }
		   else if(typeDiff.equalsIgnoreCase("years")) {
			   qry = "SELECT  COUNT(DISTINCT sales.doc_sid) , YEAR(sales.sales_date) " +
					   " FROM  retail_pro_sales sales, (SELECT contact_id FROM contacts_loyalty " +
					   " WHERE user_id = " + userId + " AND program_id = " + prgmId + cardsetQry+ ") cl  " +
					   " WHERE sales.user_id = " + userId + " AND sales.cid IS NOT NULL AND  sales.cid = cl.contact_id" + storeQry +
					   " AND sales.sales_date BETWEEN '" + startDateStr + "' AND '"+ endDateStr + "'" +
					   " GROUP BY YEAR(sales.sales_date) ORDER BY YEAR(sales.sales_date) " ;
		   }

		   List<Object[]> tempList = null;
		   
		   tempList = jdbcTemplate.query(qry, new RowMapper() {
			   public Object mapRow(ResultSet rs, int rowNum) throws SQLException {

				   Object[] object =  new Object[2];
				   object[0] = rs.getLong(1);
				   object[1] = rs.getString(2);

				   return object;
			   }
		   });
		   return tempList;
	}

	public Long getTotalVisitsByUserId(Long userId, String startDateStr,
			String endDateStr) {

		   String qry = " SELECT COUNT(DISTINCT docSid) FROM RetailProSalesCSV " +
				   " WHERE userId="+ userId + " AND salesDate BETWEEN '" + startDateStr + "' AND '"+ endDateStr + "'" ;

		   List tempList = getHibernateTemplate().find(qry);
		   if(tempList != null && tempList.size() > 0) {
			   return (Long) tempList.get(0);
		   }
		   else return 0l;

	}

	public List<Object[]> getNonLoyaltyRevenue(Long userId,
			String startDateStr, String endDateStr, String storeNo,
			 String typeDiff) {
		String subQry = "";
		   if(nonLtyContIsStr != null) {
			   subQry += " AND sales.cid IN ("+nonLtyContIsStr+") ";
		   }
		  if(storeNo != null && storeNo.length() != 0) {
			   subQry += " AND sales.store_number in ("+storeNo+")";
			}
		   String qry = "";
		   if(typeDiff.equalsIgnoreCase("days")) { 
			   qry = "SELECT  ROUND(SUM((sales.quantity*sales.sales_price) + sales.tax -(IF(sales.discount is null,0,sales.discount))),2) , DATE(sales.sales_date) " +
					   " FROM  retail_pro_sales sales "+
					   " WHERE sales.user_id = " + userId +" AND (sales.cid IS  NULL OR" +
				   	   " sales.cid NOT IN (SELECT contact_id FROM contacts_loyalty WHERE user_id = "+ userId +" AND contact_id IS NOT NULL))"+ subQry + 
					   " AND sales.sales_date BETWEEN '" + startDateStr + "' AND '"+ endDateStr + "'" +
					   " GROUP BY DATE(sales.sales_date) ORDER BY DATE(sales.sales_date) " ;
		   }
		   else if(typeDiff.equalsIgnoreCase("months")) {
			   qry = "SELECT  ROUND(SUM((sales.quantity*sales.sales_price) + sales.tax -(IF(sales.discount is null,0,sales.discount))),2) , MONTH(sales.sales_date) " +
					   " FROM  retail_pro_sales sales"+
					   " WHERE sales.user_id = " + userId +" AND (sales.cid IS  NULL OR" +
				   	   " sales.cid NOT IN (SELECT contact_id FROM contacts_loyalty WHERE user_id = "+ userId +" AND contact_id IS NOT NULL ))"+ subQry + 
					   " AND sales.sales_date BETWEEN '" + startDateStr + "' AND '"+ endDateStr + "'" +
					   " GROUP BY MONTH(sales.sales_date) ORDER BY MONTH(sales.sales_date) " ;
		   }
		   else if(typeDiff.equalsIgnoreCase("years")) {
			   qry = "SELECT  ROUND(SUM((sales.quantity*sales.sales_price) + sales.tax -(IF(sales.discount is null,0,sales.discount))),2) , YEAR(sales.sales_date) " +
					   " FROM  retail_pro_sales sales " +
					   " WHERE sales.user_id = " + userId +" AND (sales.cid IS NULL OR " +
				   	   " sales.cid NOT IN (SELECT contact_id FROM contacts_loyalty WHERE user_id = "+ userId +" AND contact_id IS NOT NULL ))"+ subQry + 
					   " AND sales.sales_date BETWEEN '" + startDateStr + "' AND '"+ endDateStr + "'" +
					   " GROUP BY YEAR(sales.sales_date) ORDER BY YEAR(sales.sales_date) " ;
		   }

		   logger.debug("non loyalty revenue qry :"+qry);

		   List<Object[]> tempList = null;
		   
		   tempList = jdbcTemplate.query(qry, new RowMapper() {
			   public Object mapRow(ResultSet rs, int rowNum) throws SQLException {

				   Object[] object =  new Object[2];
				   object[0] = rs.getDouble(1);
				   object[1] = rs.getString(2);

				   return object;
			   }
		   });
		   return tempList;
	}

	public List<Object[]> getTotalRevenue(Long userId,
			String startDateStr, String endDateStr, String storeNo,
			 String typeDiff) {
		String subQry = "";
		if(storeNo != null && storeNo.length() != 0) {
			   subQry += " AND sales.store_number in ("+storeNo+")";
			}
		   String qry = "";
		   if(typeDiff.equalsIgnoreCase("days")) { 
			   qry = "SELECT  ROUND(SUM((sales.quantity*sales.sales_price) + sales.tax -(IF(sales.discount is null,0,sales.discount))),2) , DATE(sales.sales_date) " +
					   " FROM  retail_pro_sales sales "+
					   " WHERE sales.user_id = " + userId + subQry +  
					   " AND sales.sales_date BETWEEN '" + startDateStr + "' AND '"+ endDateStr + "'" +
					   " GROUP BY DATE(sales.sales_date) ORDER BY DATE(sales.sales_date) " ;
		   }
		   else if(typeDiff.equalsIgnoreCase("months")) {
			   qry = "SELECT  ROUND(SUM((sales.quantity*sales.sales_price) + sales.tax -(IF(sales.discount is null,0,sales.discount))),2) , MONTH(sales.sales_date) " +
					   " FROM  retail_pro_sales sales"+
					   " WHERE sales.user_id = " + userId + subQry + 
					   " AND sales.sales_date BETWEEN '" + startDateStr + "' AND '"+ endDateStr + "'" +
					   " GROUP BY MONTH(sales.sales_date) ORDER BY MONTH(sales.sales_date) " ;
		   }
		   else if(typeDiff.equalsIgnoreCase("years")) {
			   qry = "SELECT  ROUND(SUM((sales.quantity*sales.sales_price) + sales.tax -(IF(sales.discount is null,0,sales.discount))),2) , YEAR(sales.sales_date) " +
					   " FROM  retail_pro_sales sales " +
					   " WHERE sales.user_id = " + userId + subQry + 
					   " AND sales.sales_date BETWEEN '" + startDateStr + "' AND '"+ endDateStr + "'" +
					   " GROUP BY YEAR(sales.sales_date) ORDER BY YEAR(sales.sales_date) " ;
		   }

		   

		   List<Object[]> tempList = null;
		   
		   tempList = jdbcTemplate.query(qry, new RowMapper() {
			   public Object mapRow(ResultSet rs, int rowNum) throws SQLException {

				   Object[] object =  new Object[2];
				   object[0] = rs.getDouble(1);
				   object[1] = rs.getString(2);

				   return object;
			   }
		   });
		   return tempList;
	}
	
	public List<Object[]> getNonLoyaltyVisits(Long userId,String startDateStr, String endDateStr, String storeNo,String typeDiff) {
		String subQry = "";
		   if(nonLtyContIsStr != null) {
			   subQry += " AND sales.cid IN ("+nonLtyContIsStr+") ";
		   }
		if(storeNo != null && storeNo.length() != 0) {
			   subQry += " AND sales.store_number in ("+storeNo+")";
			}
		   String qry = "";
		   if(typeDiff.equalsIgnoreCase("days")) { 
			   qry = "SELECT  COUNT(DISTINCT sales.doc_sid) , DATE(sales.sales_date) " +
					   " FROM  retail_pro_sales sales "+
					   " WHERE sales.user_id = " + userId +" AND (sales.cid IS NULL OR " +
				   	   " sales.cid NOT IN (SELECT contact_id FROM contacts_loyalty WHERE user_id = "+ userId +" AND contact_id IS NOT NULL ))"+ subQry + 
					   " AND sales.sales_date BETWEEN '" + startDateStr + "' AND '"+ endDateStr + "'" +
					   " GROUP BY DATE(sales.sales_date) ORDER BY DATE(sales.sales_date) " ;
		   }
		   else if(typeDiff.equalsIgnoreCase("months")) {
			   qry = "SELECT  COUNT(DISTINCT sales.doc_sid) , MONTH(sales.sales_date) " +
					   " FROM  retail_pro_sales sales"+
					   " WHERE sales.user_id = " + userId +" AND (sales.cid IS  NULL OR" +
				   	   " sales.cid NOT IN (SELECT contact_id FROM contacts_loyalty WHERE user_id = "+ userId +" AND contact_id IS NOT NULL ))"+ subQry + 
					   " AND sales.sales_date BETWEEN '" + startDateStr + "' AND '"+ endDateStr + "'" +
					   " GROUP BY MONTH(sales.sales_date) ORDER BY MONTH(sales.sales_date) " ;
		   }
		   else if(typeDiff.equalsIgnoreCase("years")) {
			   qry = "SELECT  COUNT(DISTINCT sales.doc_sid) , YEAR(sales.sales_date) " +
					   " FROM  retail_pro_sales sales " +
					   " WHERE sales.user_id = " + userId +" AND ( sales.cid IS  NULL OR" +
				   	   " sales.cid NOT IN (SELECT contact_id FROM contacts_loyalty WHERE user_id = "+ userId +" AND contact_id IS NOT NULL))"+ subQry + 
					   " AND sales.sales_date BETWEEN '" + startDateStr + "' AND '"+ endDateStr + "'" +
					   " GROUP BY YEAR(sales.sales_date) ORDER BY YEAR(sales.sales_date) " ;
		   }

		   

		   List<Object[]> tempList = null;
		   
		   tempList = jdbcTemplate.query(qry, new RowMapper() {
			   public Object mapRow(ResultSet rs, int rowNum) throws SQLException {

				   Object[] object =  new Object[2];
				   object[0] = rs.getLong(1);
				   object[1] = rs.getString(2);

				   return object;
			   }
		   });
		   return tempList;
	}

	public List<Object[]> getTotalVisits(Long userId,
			String startDateStr, String endDateStr, String storeNo,
			 String typeDiff) {
		String subQry = "";
		if(storeNo != null && storeNo.length() != 0) {
			   subQry += " AND sales.store_number in ("+storeNo+")";
			}
		   String qry = "";
		   if(typeDiff.equalsIgnoreCase("days")) { 
			   qry = "SELECT  COUNT(DISTINCT sales.doc_sid) , DATE(sales.sales_date) " +
					   " FROM  retail_pro_sales sales "+
					   " WHERE sales.user_id = " + userId + subQry +  
					   " AND sales.sales_date BETWEEN '" + startDateStr + "' AND '"+ endDateStr + "'" +
					   " GROUP BY DATE(sales.sales_date) ORDER BY DATE(sales.sales_date) " ;
		   }
		   else if(typeDiff.equalsIgnoreCase("months")) {
			   qry = "SELECT  COUNT(DISTINCT sales.doc_sid) , MONTH(sales.sales_date) " +
					   " FROM  retail_pro_sales sales"+
					   " WHERE sales.user_id = " + userId + subQry + 
					   " AND sales.sales_date BETWEEN '" + startDateStr + "' AND '"+ endDateStr + "'" +
					   " GROUP BY MONTH(sales.sales_date) ORDER BY MONTH(sales.sales_date) " ;
		   }
		   else if(typeDiff.equalsIgnoreCase("years")) {
			   qry = "SELECT COUNT(DISTINCT sales.doc_sid) , YEAR(sales.sales_date) " +
					   " FROM  retail_pro_sales sales " +
					   " WHERE sales.user_id = " + userId + subQry + 
					   " AND sales.sales_date BETWEEN '" + startDateStr + "' AND '"+ endDateStr + "'" +
					   " GROUP BY YEAR(sales.sales_date) ORDER BY YEAR(sales.sales_date) " ;
		   }

		   

		   List<Object[]> tempList = null;
		   
		   tempList = jdbcTemplate.query(qry, new RowMapper() {
			   public Object mapRow(ResultSet rs, int rowNum) throws SQLException {

				   Object[] object =  new Object[2];
				   object[0] = rs.getLong(1);
				   object[1] = rs.getString(2);

				   return object;
			   }
		   });
		   return tempList;
	}
	   
	public Object[] getCumulativePurchase(Long userId, Long contactId, String startDate, String endDate) {
		
		List<Object[]> cumulativePurchaseArr = null;
		
		String qry ="SELECT ROUND(SUM((quantity*sales_price) + tax -(IF(discount is null,0,discount))),2) FROM  retail_pro_sales  " +
				" WHERE user_id="+ userId + " AND cid = "+contactId.longValue()+
				" AND sales_date <='" + startDate + "' AND sales_date >='"+ endDate + "'" ;
		logger.info("getCumulativePurchase >>>>>"+qry);
		
		cumulativePurchaseArr = jdbcTemplate.query(qry, new RowMapper() {
			 public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				 Object[] object =  new Object[2];
				 object[0] = rs.getDouble(1);
				return object;
			 }
		});
		
		if(cumulativePurchaseArr != null && cumulativePurchaseArr.size() > 0) {
			return cumulativePurchaseArr.get(0);
		}
		return null;
	}
	*//**
	 * This method calculate total count of loyalties of service type oc with reward flag 'L' or 'GL'
	 * @param userId
	 * @param serviceType
	 * @param startDate
	 * @param endDate
	 * @return count
	 *//*
	public Long findTotalCountByServiceTypeAndRewardFlag(Long userId,String serviceType,String startDate, String endDate) {

		String query =  "SELECT  COUNT(DISTINCT sales.doc_sid) as visits" +
				" FROM  retail_pro_sales sales,(SELECT contact_id,service_type,reward_flag FROM contacts_loyalty " +
				" WHERE user_id = " + userId + ") cl " +
				" WHERE sales.user_id = "+userId+" AND sales.cid IS NOT NULL AND cl.service_type='"+serviceType+"' "
				+ "AND  (cl.reward_flag='L' OR cl.reward_flag='GL') "+
				" AND cl.contact_id = sales.cid " +
				" AND sales.sales_date BETWEEN '"+ startDate +"' AND '"+endDate+"' ";


		logger.info("findTotalCountByServiceTypeAndRewardFlag ...query >>>> .."+query);
		 List<Map<String,Object>> tempList = jdbcTemplate.queryForList(query);
		   if(tempList != null && tempList.size() > 0) {
			   if(tempList.get(0) == null) {
				   return 0L;
			   }
			   return new Long (tempList.get(0).get("visits") != null ? (Long)tempList.get(0).get("visits"):0);
		   }
		   else return 0L;
} 

	*//**
	 * This method find's the sum of sales price & sales tax for service type 'OC'
	 * @param userId
	 * @param serviceType
	 * @param rewardFlag
	 * @param startDate
	 * @param endDate
	 * @return SUM(salesPrice + tax )
	 *//*
	public Double findTotalRevenueByServiceTypeAndRewardFlag(Long userId,String serviceType, String rewardFlag, String startDate,
			String endDate) {
		String subQuery = "";

		if("loyaltyGift".equalsIgnoreCase(rewardFlag)){
			subQuery = "(cl.reward_flag='L' OR cl.reward_flag='GL')";
		}
		else{
			subQuery = "cl.rewardFlag='G'";
		}//Rajeev
		String query =  "SELECT  ROUND(SUM(sales.sales_price * sales.quantity + sales.tax - if(sales.discount is null, 0, sales.discount)))  as amt" +
				" FROM  retail_pro_sales sales,(SELECT DISTINCT contact_id,service_type,reward_flag FROM contacts_loyalty " +
					   " WHERE user_id = " + userId + ") cl " +
				" WHERE sales.user_id = "+userId+" AND sales.cid IS NOT NULL AND cl.service_type='"+serviceType+"' AND  " +subQuery+
				" AND cl.contact_id = sales.cid " +
				" AND sales.sales_date BETWEEN '"+ startDate +"' AND '"+endDate+"' ";


		logger.info("findTotalRevenueByServiceTypeAndRewardFlag ...query >>>> .."+query);
		 List<Map<String,Object>> tempList = jdbcTemplate.queryForList(query);
		   if(tempList != null && tempList.size() > 0) {
			   if(tempList.get(0) == null) {
				   return 0.0;
			   }
			   return new Double (tempList.get(0).get("amt") != null ? (Double)tempList.get(0).get("amt"):0.0  );
		   }
		   else return 0.0;
		

	}//findTotalRevenueByServiceTypeAndRewardFlag

	*//**
	 * This method gets count of LoyaltyTransactionchild based on userId,TranactionType, startDate,enddate
	 * @param userId
	 * @param startDate
	 * @param endDate
	 * @return
	 *//*
	public Long findRedemptionForOcType(Long userId,String transactionType, String startDate,String endDate) {

		String query="SELECT COUNT(trans_child_id) FROM loyalty_transaction_child " +
				" WHERE user_id = " + userId +" AND transaction_type = '"+transactionType+"'"
				+ " AND created_date BETWEEN '"+startDate+"' AND '"+endDate+"'  ";
		logger.info("findReedmptionByTypes ..query is..."+query);

		return(jdbcTemplate.queryForLong(query));

	}//findReedmptionForOcType


	*//**
	 * This method fetch Receipt Number from retail_pro_sales based on the doc_sid
	 * @param docSID
	 * @return list
	 *//*
	public RetailProSalesCSV findReceiptNumberByDocsid(String docSid, Long userId) {
		logger.debug(">>>>>>>>>>>>> entered in findReceiptNumberByDocsid");
		List<RetailProSalesCSV> list = null;
		try {
			list = getHibernateTemplate().find("from RetailProSalesCSV where docSid ='" + docSid+"'  AND userId ="+userId.longValue());

			if(list.size() >0) {
				return (RetailProSalesCSV) list.get(0);
			}
			else{
				return null;
			}

		} catch (DataAccessException e) {
			logger.error("Exception ::" , e);
			return null;
		}
	}//findReceiptNumberByDocsid

	
	public Object[] getLastPurchaseQtyAndAmtByDocSid(String docSid, Long userId) {
		List<Object[]> lastPurArr = null;
		String qry = " SELECT SUM(quantity), ROUND(SUM((quantity * sales_price) + tax -(IF(discount is null,0,discount))),2)" +
					" FROM retail_pro_sales WHERE doc_sid = '" + docSid + "' AND user_id ="+userId.longValue();
		try {
			lastPurArr = jdbcTemplate.query(qry, new RowMapper() {
				public Object mapRow(ResultSet rs, int rowNum) throws SQLException {

					Object[] object =  new Object[2];
					object[0] = rs.getLong(1); // quantity
					object[1] = rs.getDouble(2); // purchase amount

					return object;
				}

			});
			if(lastPurArr != null && lastPurArr.size() > 0) {
				return lastPurArr.get(0);
			}
			return null;
		}catch (Exception e) {
			logger.error("Exception ::" , e);
			return null;
		}
	} // getLastPurchaseAmtByDocSid
	*/
}//EOF