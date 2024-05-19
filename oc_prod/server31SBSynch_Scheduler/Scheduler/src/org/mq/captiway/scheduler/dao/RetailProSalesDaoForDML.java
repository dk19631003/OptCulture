package org.mq.captiway.scheduler.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Calendar;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.OptCultureCSVFileUpload;
import org.mq.captiway.scheduler.beans.MailingList;
//import org.mq.captiway.scheduler.beans.MyStoredProcedure;
import org.mq.captiway.scheduler.beans.Contacts;
import org.mq.captiway.scheduler.beans.ContactsLoyalty;
import org.mq.captiway.scheduler.beans.MastersToTransactionMappings;
import org.mq.captiway.scheduler.beans.RetailProSalesCSV;
import org.mq.captiway.scheduler.beans.SalesUpdateErrorLog;
import org.mq.captiway.scheduler.beans.Users;
import org.mq.captiway.scheduler.utility.Constants;
import org.mq.captiway.scheduler.utility.MyCalendar;
import org.mq.captiway.scheduler.utility.Utility;
import org.mq.optculture.utils.ServiceLocator;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DeadlockLoserDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

public class RetailProSalesDaoForDML extends AbstractSpringDaoForDML {
	
	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	public RetailProSalesDaoForDML() {}
	DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private JdbcTemplate jdbcTemplate;
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

   /* public RetailProSalesCSV find(Long id) {
        return (RetailProSalesCSV) super.find(RetailProSalesCSV.class, id);
    }*/

    public void saveOrUpdate(RetailProSalesCSV retailProSales) {
        super.saveOrUpdate(retailProSales);
    }

    public void delete(RetailProSalesCSV retailProSales) {
        super.delete(retailProSales);
    }

   
    public void saveByCollection(Collection<RetailProSalesCSV> retailProSalesCollection) {
//    	super.saveOrUpdateAll(retailProSalesCollection);
    	getHibernateTemplate().saveOrUpdateAll(retailProSalesCollection);
    }
    
   /* public RetailProSalesCSV findRecordByRecieptNumber(Long recieptNum ,Long listId) {
		try {
			List<RetailProSalesCSV> list = null;
			list  = getHibernateTemplate().find("from RetailProSalesCSV  where recieptNumber ="+recieptNum +"  and listId = " + listId);
			
			if(list.size() >0) {
				return list.get(0);
			}
			return null;
		} catch (DataAccessException e) {
			logger.error("Exception ::::" , e);
			return null;
		}
	}
    
    public RetailProSalesCSV findByExternalId(Long listId, Long externalID) {
		try {
			List<RetailProSalesCSV> list = null;
			list  = getHibernateTemplate().find("FROM RetailProSalesCSV  WHERE listId = " + listId.longValue()+ " AND externalId ="+externalID.longValue() );
			
			if(list.size() >0) {
				return list.get(0);
			}
			return null;
		} catch (DataAccessException e) {
			logger.error("Exception ::::" , e);
			return null;
		}
	}
    */
    
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
    	
    	
    	/*String sql = " REPLACE INTO sales_aggregate_data(customer_id, tot_reciept_count ," +
	    			" tot_purchase_amt, avg_purchase_amt,max_purchase_amt, list_id,cid,user_id) SELECT customer_id," +
	    			"  count(t_count) , sum(t_sum) ,avg(t2.t_sum) , MAX(t_sum) ," +sourceMlistId.longValue()+",cid, " +userId.longValue()+
	    			" FROM (	SELECT customer_id,reciept_number, count(reciept_number) as t_count," +
	    			" sum((sales_price*quantity)+tax-(IF(discount=null,0,discount))) as t_sum, list_id,cid, user_id FROM retail_pro_sales AS t1" +
	    			" WHERE cid is NOT NULL "+
	    			" AND user_id="+userId.longValue()+"  GROUP BY reciept_number) AS t2 " +
	    			" Where t2.cid IS NOT NULL  GROUP BY cid";*/
    	
    	String sql = " REPLACE INTO sales_aggregate_data(customer_id, tot_reciept_count ," +
    			" tot_purchase_amt, avg_purchase_amt,max_purchase_amt, list_id,cid,user_id) SELECT customer_id," +
    			"  count(t_count) , sum(t_sum) ,avg(t2.t_sum) , MAX(t_sum) ," +sourceMlistId.longValue()+",cid, " +userId.longValue()+
    			" FROM (	SELECT customer_id,reciept_number, count(doc_sid) as t_count," +
    			" sum((sales_price*quantity)+tax-(IF(discount is null,0,discount))) as t_sum, list_id,cid, user_id FROM retail_pro_sales AS t1" +
    			" WHERE cid is NOT NULL "+
    			" AND user_id="+userId.longValue()+"  GROUP BY doc_sid) AS t2 " +
    			" Where t2.cid IS NOT NULL  GROUP BY cid";
    	
    	
    	logger.info("qry>>>>>>>"+sql);
		executeJdbcQuery(sql);
		
		
		
		
		
	}
    
    
   /* public RetailProSalesCSV findLastpurchaseRecord(Long contactId, Long userId) {
    	
    	try {
    		
    		String qry = "select store_number, sales_date, ROUND(SUM((sales_price*quantity)+tax-(IF(discount is null,0,discount))), 2) as purchase_amount from retail_pro_sales where user_id ="+userId.longValue()+" and cid="+contactId.longValue()+
    						" group by doc_sid order by sales_date desc ";
    		
    		List<RetailProSalesCSV> rproList = null;
    		 		
    			rproList = jdbcTemplate.query(qry, new RowMapper() {
    	        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
    	        	RetailProSalesCSV retailProSalesCSV = new RetailProSalesCSV();
    	            
    	        	Calendar cal = Calendar.getInstance();
                	cal.setTime(rs.getTimestamp("sales_date"));
    	        	retailProSalesCSV.setSalesDate(cal);
    			retailProSalesCSV.setStoreNumber(rs.getString("store_number"));
    			retailProSalesCSV.setSalesPrice(rs.getDouble("purchase_amount"));
    	    
    			 return retailProSalesCSV;
    	        }
    	    });
    	
    		if(rproList != null)return rproList.get(0);
			String qry = " FROM RetailProSalesCSV WHERE userId ="+userId.longValue()+" and cid="+contactId.longValue()+" ORDER BY salesDate DESC";
			//logger.debug("======================="+qry+"========================");
			List<RetailProSalesCSV> saleObjectList = executeQuery(qry, 0, 1);
			if(saleObjectList.size() == 1) {
				return saleObjectList.get(0);
				
			}
			
			return null;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::::" , e);
			return null;
		}
    }

    public Calendar findLastpurchasedDate(String exteranalId, Long userId) {
    	
    	try {
    		
			String qry = " FROM RetailProSalesCSV WHERE userId ="+userId.longValue()+" and customerId='"+exteranalId+"' ORDER BY salesDate DESC";
			//logger.debug("======================="+qry+"========================");
			List<RetailProSalesCSV> saleObjectList = executeQuery(qry, 0, 1);
			if(saleObjectList.size() == 1) {
				
				return saleObjectList.get(0).getSalesDate();
				
			}
			
			return null;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::::" , e);
			return null;
		}
    }
    
 	public RetailProSalesCSV findSalesByPriority(TreeMap<String, List<String>> prioMap , String[] lineStr,  Long userId) {
 		
 		try {
 			String query = "FROM RetailProSalesCSV WHERE userId="+userId+" ";
 			
 			if(logger.isDebugEnabled()) logger.debug("prioMap="+prioMap);
 			Set<String> keySet = prioMap.keySet();
 			List<RetailProSalesCSV> salesIdList = null;
 			
 			List<String> tempList=null;
 			for (String eachKey : keySet) {
				
 				if(logger.isDebugEnabled()) logger.debug("Key ="+eachKey);
 				tempList = prioMap.get(eachKey);
 				
 				for (String eachStr : tempList) {
					int index = Integer.parseInt(eachStr.substring(0,eachStr.indexOf('|')));
					
					String[] tempStr = eachStr.split("\\|");
					
					if(tempStr[2].toLowerCase().startsWith("string")) {
//						query += " AND "+ eachStr.substring(eachStr.indexOf('|')+1) + " = '"+ lineStr[index] +"' ";
						query += " AND "+ tempStr[1] + " = '"+ lineStr[index] +"' ";
					}else {
						query += " AND "+ tempStr[1] + " = "+ lineStr[index] +" ";
					}
					
					String valueStr = lineStr[index].trim();
					
					String posMappingDateFormatStr = tempStr[2].trim();
					
					if(posMappingDateFormatStr.toLowerCase().startsWith("date") && !tempStr[1].trim().toLowerCase().startsWith("udf")) {
						try {
							posMappingDateFormatStr =  posMappingDateFormatStr.substring(posMappingDateFormatStr.indexOf("(")+1, posMappingDateFormatStr.indexOf(")"));
							valueStr = format.format((new SimpleDateFormat(posMappingDateFormatStr).parse(valueStr)));
							query += " AND "+ tempStr[1] + " = '"+ valueStr +"' ";
						} catch (ParseException e) {
							logger.error("Exception ::::" , e);
							return null;
						}
					}
					else if(posMappingDateFormatStr.toLowerCase().startsWith("string") || posMappingDateFormatStr.toLowerCase().startsWith("udf")) {
//						query += " AND "+ eachStr.substring(eachStr.indexOf('|')+1) + " = '"+ lineStr[index] +"' ";
						query += " AND "+ tempStr[1] + " = '"+ valueStr +"' ";
					}else {
						query += " AND "+ tempStr[1] + " = "+ lineStr[index] +" ";
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
			logger.error("Exception ::::" , e);
			return null;
		}
 		
 	}//findSalesByPriority()
 	
 	  public long updateSalesToContactsMappings(String parentStr,String childStr,long listId) {
 		  if(logger.isDebugEnabled()) logger.debug("childStr Str >>"+childStr);
 		 if(logger.isDebugEnabled()) logger.debug("parentStr Str >>"+parentStr);
 		if(logger.isDebugEnabled()) logger.debug("listId Str >>"+listId);
 		  String qry="UPDATE RetailProSalesCSV rs  SET rs.cid=(SELECT c.contactId From Contacts c where mailingList ="+listId+" AND )" +
 		  		"where rs.listId="+listId+" and rs.cid="+parentStr+  and rs.sid="+childStr";
 		 return getHibernateTemplate().bulkUpdate(" ");
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
					
					db_child_lable = Utility.genFieldSalesMap.get(eachMtoT.getChildId().getCustomFieldName());
					
					if(db_child_lable==null && eachMtoT.getChildId().getCustomFieldName().toLowerCase().startsWith("udf")) {
						db_child_lable = eachMtoT.getChildId().getCustomFieldName().toLowerCase();
					}
					
					db_parent_lable = Utility.genFieldContMap.get(eachMtoT.getParentId().getCustomFieldName());
					
					if(db_parent_lable==null && eachMtoT.getParentId().getCustomFieldName().toLowerCase().startsWith("udf")) {
						db_parent_lable = eachMtoT.getParentId().getCustomFieldName().toLowerCase();
					}
					
					salesToContactStr += "  AND rs."+db_child_lable + "= c."+db_parent_lable+" ";
				} // if
				
				else if(eachMtoT.getType().equals(Constants.TYPE_SALES_TO_Inventory)) {
					
					db_child_lable = Utility.genFieldSalesMap.get(eachMtoT.getChildId().getCustomFieldName());
					
					if(db_child_lable==null && eachMtoT.getChildId().getCustomFieldName().toLowerCase().startsWith("udf")) {
						db_child_lable = eachMtoT.getChildId().getCustomFieldName().toLowerCase();
					}
					
					db_parent_lable = Utility.genFieldSKUMap.get(eachMtoT.getParentId().getCustomFieldName());
					
					if(db_parent_lable==null && eachMtoT.getParentId().getCustomFieldName().toLowerCase().startsWith("udf")) {
						db_parent_lable = eachMtoT.getParentId().getCustomFieldName().toLowerCase();
					}
					
					salesToInventoryStr += "  AND   rs."+db_child_lable + "= s."+db_parent_lable+" ";
				} // if
				
			} 	
			
			 
			
			  /*String salesToContactsQry =" UPDATE retail_pro_sales rs , contacts  c  SET rs.cid=c.cid   WHERE c.list_id ="+listId+" AND rs.cid IS null  AND rs.list_id="+listId +  salesToContactStr;
			  String salesToInventoryQry =" UPDATE retail_pro_sales rs , retail_pro_sku s set rs.inventory_id=s.sku_id WHERE s.list_id ="+listId+" AND rs.inventory_id IS null AND rs.list_id="+listId +  salesToInventoryStr;
			  */
			
			String salesToContactsQry =" UPDATE retail_pro_sales rs , contacts  c  SET rs.cid=c.cid   WHERE "
					+ "c.user_id ="+userId+" AND rs.cid IS null  AND rs.user_id="+userId +  salesToContactStr;
			  String salesToInventoryQry =" UPDATE retail_pro_sales rs , retail_pro_sku s set rs.inventory_id=s.sku_id WHERE s.user_id="+userId+" AND rs.inventory_id IS null AND rs.user_id="+userId +  salesToInventoryStr;
			 
			 /* if(logger.isDebugEnabled()) logger.debug("sales to contacts: "+salesToContactsQry);
			  if(logger.isDebugEnabled()) logger.debug("sales to Inventory: "+salesToInventoryQry);
			  
			  jdbcTemplate.execute(salesToContactsQry);
			  jdbcTemplate.execute(salesToInventoryQry);
			  */
			 // jdbcTemplate.batchUpdate(new String[] {salesToContactsQry,salesToInventoryQry});
			  //added after deadlock situation found
			  try {
				  if(logger.isDebugEnabled()) logger.debug("sales to contacts: "+salesToContactsQry);
				jdbcTemplate.execute(salesToContactsQry);
				// throw new DeadlockLoserDataAccessException("deadlock occured", new Throwable());
			}  catch(org.springframework.dao.DeadlockLoserDataAccessException e){
				logger.error("in catch of DeadlockLoserDataAccessException ", e);
				Calendar now = Calendar.getInstance(); 
				SalesUpdateErrorLogDao salesUpdateErrorLogDao = null;
				SalesUpdateErrorLogDaoForDML salesUpdateErrorLogDaoForDML = null;
				try {
					salesUpdateErrorLogDao = (SalesUpdateErrorLogDao)ServiceLocator.getInstance().getDAOByName("salesUpdateErrorLogDao");
					salesUpdateErrorLogDaoForDML = (SalesUpdateErrorLogDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName("salesUpdateErrorLogDaoForDML");

				} catch (Exception e1) {
					// TODO Auto-generated catch block
					logger.error("another exception ", e1);
				}
				if(salesUpdateErrorLogDao != null ){
					SalesUpdateErrorLog newLog = new SalesUpdateErrorLog(userId, now, now, "F", salesToContactsQry, 0);
					salesUpdateErrorLogDaoForDML.saveOrUpdate(newLog);
				}
				
			}try {
				 if(logger.isDebugEnabled()) logger.debug("sales to Inventory: "+salesToInventoryQry);
			  jdbcTemplate.execute(salesToInventoryQry);
			}  catch(org.springframework.dao.DeadlockLoserDataAccessException e){
				logger.error("in catch of DeadlockLoserDataAccessException ", e);
				Calendar now = Calendar.getInstance(); 
				SalesUpdateErrorLogDao salesUpdateErrorLogDao = null;
				SalesUpdateErrorLogDaoForDML salesUpdateErrorLogDaoForDML = null;

				try {
					salesUpdateErrorLogDao = (SalesUpdateErrorLogDao)ServiceLocator.getInstance().getDAOByName("salesUpdateErrorLogDao");
					salesUpdateErrorLogDaoForDML = (SalesUpdateErrorLogDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName("salesUpdateErrorLogDaoForDML");

				} catch (Exception e1) {
					// TODO Auto-generated catch block
					logger.error("another exception ", e1);
				}
				if(salesUpdateErrorLogDao != null ){
					SalesUpdateErrorLog newLog = new SalesUpdateErrorLog(userId, now, now, "F", salesToInventoryQry, 0);
					//salesUpdateErrorLogDao.saveOrUpdate(newLog);
					salesUpdateErrorLogDaoForDML.saveOrUpdate(newLog);

				}
				
			}
			
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::::" , e);
		}
 		 
 	  }
 	
 	
 	/*public List<RetailProSalesCSV> findLastPurCheseDatePlaceHolderVal(String cids) {
 		
 		logger.info("cids ====>"+cids);
		String QRY_FOR_LOYALTY_PLACEHOLDERS = " SELECT rs FROM RetailProSalesCSV rs,Contacts c WHERE c.externalId IS NOT NULL AND c.users.userId=rs.userId AND " +
		"c.contactId IN( "+cids+") AND rs.cid=c.contactId GROUP BY rs.cid,rs.salesDate HAVING rs.salesDate=MAX(rs.salesDate) ";

		
 		String QRY_FOR_LAST_PURCHASE = " SELECT rs.sales_id,rs.cid,rs.store_number, rs.sales_date, ROUND(SUM((rs.sales_price*rs.quantity)+rs.tax-(IF(rs.discount is null,0,rs.discount))), 2) as purchase_amount FROM retail_pro_sales rs JOIN ( "+

 				" SELECT  cid, max(sales_date) as max_date"+ 
 				" FROM retail_pro_sales"+   
 				" WHERE cid IS NOT NULL AND cid IN ("+cids+") GROUP BY cid ) o"+

 				 " ON rs.cid = o.cid WHERE  rs.cid IS NOT NULL AND rs.cid IN ("+cids+")  AND o.max_date=rs.sales_date Group by rs.cid,rs.sales_date";

 		
 		List<RetailProSalesCSV> rproList = null;
		try {
		
			rproList = jdbcTemplate.query(QRY_FOR_LAST_PURCHASE, new RowMapper() {
	        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
	        	RetailProSalesCSV retailProSalesCSV = new RetailProSalesCSV();
	            
	          
	        	retailProSalesCSV.setSalesId(rs.getLong("sales_id"));
	        	retailProSalesCSV.setCid(rs.getLong("cid"));
	        	Calendar cal = Calendar.getInstance();
            	cal.setTime(rs.getTimestamp("sales_date"));
	        	retailProSalesCSV.setSalesDate(cal);
			retailProSalesCSV.setStoreNumber(rs.getString("store_number"));
			retailProSalesCSV.setSalesPrice(rs.getDouble("purchase_amount"));
	     
 		
 		
			 return retailProSalesCSV;
	        }
	    });
	} catch (Exception e) {
		logger.error("Exception ::::" , e);
	}
		return rproList;
 		
 		
 		
 		
 	}
 	
 	public List<RetailProSalesCSV> findLastPurCheseStorePlaceHolderVal(int currentSizeInt) {
 		
 		
		String QRY_FOR_LOYALTY_PLACEHOLDERS = " SELECT rs FROM RetailProSalesCSV rs,Contacts c WHERE c.externalId IS NOT NULL AND c.users.userId=rs.userId AND " +
		"c.contactId BETWEEN "+currentSizeInt +" AND "+ (currentSizeInt+10000) +" AND rs.cid=c.contactId GROUP BY salesDate HAVING salesDate=MAX(salesDate) ";

		
		List<RetailProSalesCSV> rproList = executeQuery(QRY_FOR_LOYALTY_PLACEHOLDERS);
		
		return rproList;
 		
 		
 		
 		
 	}
 	
 	public Object[] findTotalVisitsAndRevenue(Long userId, String fromDateString, String toDateString){

		//String qry ="SELECT  COUNT(DISTINCT promoCode) FROM  RetailProSalesCSV  WHERE userId ="+userId+" AND promoCode IS NOT NULL";
		String qry ="SELECT  COUNT(DISTINCT doc_sid) as visits, ROUND(SUM((sales.quantity*sales.sales_price) + sales.tax - if(discount is null, 0, discount)),2) as revenue FROM  retail_pro_sales sales  WHERE user_id ="+userId;
				
				if(toString() != null){
					qry +=" AND sales_date between '"+fromDateString+"' AND '"+toDateString+"'";
				}else{
					qry += " AND Date(sales_date) = '"+fromDateString+"'";
				}
		
		List<Map<String, Object>>  list = jdbcTemplate.queryForList(qry);
		
		Object[] obj = new Object[2];
		if(list !=null && list.size() > 0){
			obj[0] = list.get(0).get("visits")==null?0:list.get(0).get("visits");
			obj[1] = list.get(0).get("revenue")==null?new Double(0):list.get(0).get("revenue");
			return obj;
		}
	return obj;
 	}
 	
*/    
}
