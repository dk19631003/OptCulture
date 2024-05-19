package org.mq.marketer.campaign.dao;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.SkuFile;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.POSFieldsEnum;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

public class SkuFileDao extends AbstractSpringDao {
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	public SkuFileDao() {}
	
	private JdbcTemplate jdbcTemplate;
	
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

    public SkuFile find(Long id) {
        return (SkuFile) super.find(SkuFile.class, id);
    }

    /*public void saveOrUpdate(SkuFile skuFile) {
        super.saveOrUpdate(skuFile);
    }

    public void delete(SkuFile skuFile) {
        super.delete(skuFile);
    }*/

   
   /* public void saveByCollection(Collection skuFileCollection){
//    	super.saveOrUpdateAll(retailProSalesCollection);
    	getHibernateTemplate().saveOrUpdateAll(skuFileCollection);
    }*/

    public SkuFile findRecordBySKU(Long skuId ,Long userId) {
		try {
			List<SkuFile> list = null;
			list  = executeQuery("from SkuFile  where skuId = "+skuId +" and userId = " + userId);
			
			if(list.size() >0) {
				return list.get(0);
			}
			return null;
		} catch (DataAccessException e) {
			logger.error("Exception ::" , e);
			return null;
		}
    }
    
    public SkuFile findRecordBy(String itemSID ,Long userId) {
		try {
			List<SkuFile> list = null;
			list  = executeQuery("from SkuFile  where  userId = " + userId +" AND itemSid = '"+itemSID +"' ");
			
			if(list.size() >0) {
				return list.get(0);
			}
			return null;
		} catch (DataAccessException e) {
			logger.error("Exception ::" , e);
			return null;
		}
    }
    
    public List<Object[]> getBySKU(String qry){
      	 return getHibernateTemplate().find(qry);
       }
   /* public List<SkuFile> findSKUByLimit(Long listId ,int startIdx,int endIdx) {
    	
    	List<SkuFile> list = null;
		String query = "FROM SkuFile  where  listId = " + listId ;
		logger.info("KU File query is >>>"+query);
//		list  = getHibernateTemplate().find(query);
		list = executeQuery(query, startIdx, endIdx);
    	return list;
    }*/
    
    public List<SkuFile> findSkuByOptField(long userId,  String fieldValues) {
    	
    	List<SkuFile> list = null;
    	try {
			//logger.info(" &&& fieldValues ==="+fieldValues);
			String query = "FROM SkuFile  where  userId = " + userId + " and itemSid in("+fieldValues+")";
			logger.info("query ===>"+query);
			list  = executeQuery(query);
			return list;
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::",e);
		}
    	return list;
    	
    }
    
    DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
   	public SkuFile findSKUByPriority(TreeMap<String, List<String>> prioMap , String[] lineStr,  Long userId) {
    		
    		try {
    			String query = "FROM SkuFile WHERE userId="+userId+" ";
    			
//    			logger.debug("prioMap="+prioMap);
    			Set<String> keySet = prioMap.keySet();
    			List<SkuFile> skuIdList = null;
    			
    			List<String> tempList=null;
    			for (String eachKey : keySet) {
   				
//    				logger.debug("Key ="+eachKey);
    				tempList = prioMap.get(eachKey);
    				for (String eachStr : tempList) {
   					int index = Integer.parseInt(eachStr.substring(0,eachStr.indexOf('|')));
   					
   					//query += " AND "+ eachStr.substring(eachStr.indexOf('|')+1) + " = '"+ lineStr[index] +"' ";
   					String[] tempStr = eachStr.split("\\|");
   					
   					/*if(tempStr[2].toLowerCase().startsWith("string")) {
//   						query += " AND "+ eachStr.substring(eachStr.indexOf('|')+1) + " = '"+ lineStr[index] +"' ";
   						query += " AND "+ tempStr[1] + " = '"+ lineStr[index] +"' ";
   					}else {
   						query += " AND "+ tempStr[1] + " = "+ lineStr[index] +" ";
   					}*/
   					
   					String valueStr = lineStr[index].trim();
   					
   					String posMappingDateFormatStr = tempStr[2].trim();
   					
   					if(posMappingDateFormatStr.toLowerCase().startsWith("date") && !tempStr[1].trim().toLowerCase().startsWith("udf")) {
   						try {
   							posMappingDateFormatStr =  posMappingDateFormatStr.substring(posMappingDateFormatStr.indexOf("(")+1, posMappingDateFormatStr.indexOf(")"));
   							valueStr = format.format((new SimpleDateFormat(posMappingDateFormatStr).parse(valueStr)));
   							query += " AND "+ tempStr[1] + " = '"+ valueStr +"' ";
   						} catch (ParseException e) {
   							logger.error("Exception ::" , e);
   							return null;
   						}
   					}
   					else if(posMappingDateFormatStr.toLowerCase().startsWith("string") || posMappingDateFormatStr.toLowerCase().startsWith("udf")) {
//   						query += " AND "+ eachStr.substring(eachStr.indexOf('|')+1) + " = '"+ lineStr[index] +"' ";
   						query += " AND "+ tempStr[1] + " = '"+ valueStr +"' ";
   					}else {
   						query += " AND "+ tempStr[1] + " = "+ lineStr[index] +" ";
   					}
   					
   					
   					
   					
   					
   					
   				} // for 
    				
//    				logger.debug("QUERY=="+query);
    				
    				skuIdList  = getHibernateTemplate().find(query);
    				
    				if(skuIdList.size() == 1) {
    					return skuIdList.get(0);
    				}
    				else if(skuIdList.size() > 1) {
    					logger.debug("more than 1 object found :"+skuIdList.size());
    					return skuIdList.get(0);
    				}
    				
    				
   			} // outer for
    			
    			return null;
    			
   		} catch (DataAccessException e) {
   			// TODO Auto-generated catch block
   			logger.error("Exception ::" , e);
   			return null;
   		}
    		
    	}
   	
   	
    //DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
 		public SkuFile findSKUByPriority(TreeMap<String, List<String>> prioMap , SkuFile skuFile,  Long userId) {
 	 		
 	 		logger.info("Entering findSKUByPriority method");
 			try {
 	 			String query = "FROM SkuFile WHERE userId="+userId+" ";
 	 			
 	 			Set<String> keySet = prioMap.keySet();
 	 			List<SkuFile> skuIdList = null;
 	 			
 	 			List<String> tempList=null;
 	 		outer:	for (String eachKey : keySet) {
 					
 	 				//logger.debug("Key ="+eachKey);
 	 				tempList = prioMap.get(eachKey);
 	 				
 	 				int size = tempList.size();
 	 				for (String eachStr : tempList) {
 						//int index = Integer.parseInt(eachStr.substring(0,eachStr.indexOf('|')));
 						
 						String[] tempStr = eachStr.split("\\|");
 						
 						String valueStr = getSkuFieldValue(skuFile, tempStr[0]);
 						logger.debug("valueStr is"+valueStr);
 						
 						size = size-1;
 	 					if(valueStr == null || valueStr.trim().length() == 0){
 	 						if(size != 0)continue;
 	 						continue outer;
 	 					}
 						
 						String posMappingDateFormatStr = tempStr[1].trim();
 						
 						if(posMappingDateFormatStr.toLowerCase().startsWith("date") && !tempStr[0].trim().toLowerCase().startsWith("udf")) {
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
// 							query += " AND "+ eachStr.substring(eachStr.indexOf('|')+1) + " = '"+ lineStr[index] +"' ";
 							query += " AND "+ POSFieldsEnum.findByOCAttribute(tempStr[0].trim()) + " = '"+ valueStr +"' ";
 						}else {
 							query += " AND "+ POSFieldsEnum.findByOCAttribute(tempStr[0].trim()) + " = "+ valueStr +" ";
 						}
 						
 						
 					} // for 
 	 				
 	 				logger.debug("QUERY=="+query);
 	 				
 	 				skuIdList  = getHibernateTemplate().find(query);
 	 				
 	 				if(skuIdList.size() == 1) {
 	 					return skuIdList.get(0);
 	 				}
 	 				else if(skuIdList.size() > 1) {
 	 					logger.debug("more than 1 object found :"+skuIdList.size());
 	 					return skuIdList.get(0);
 	 				}
 	 				
 	 				
 				} // outer for
 	 			
 	 			return null;
 	 			
 			} catch (DataAccessException e) {
 				// TODO Auto-generated catch block
 				logger.error("Exception ::" , e);
 				return null;
 			}
 	 		
 	 	}
 	 	
	private String getSkuFieldValue(SkuFile skuObj, String skuFieldStr) {

		if (skuFieldStr.equals(POSFieldsEnum.listPrice.getOcAttr())) {return "" + skuObj.getListPrice();}
		else if (skuFieldStr.equals(POSFieldsEnum.description.getOcAttr())) {return skuObj.getDescription();}
		else if (skuFieldStr.equals(POSFieldsEnum.sku.getOcAttr())) {return skuObj.getSku();} 
		else if (skuFieldStr.equals(POSFieldsEnum.itemCategory.getOcAttr())) {return skuObj.getItemCategory();}
		else if (skuFieldStr.equals(POSFieldsEnum.itemSid.getOcAttr())) {return skuObj.getItemSid();}
		else if (skuFieldStr.equals(POSFieldsEnum.storeNumber.getOcAttr())) {return skuObj.getStoreNumber();}
		else if (skuFieldStr.equals(POSFieldsEnum.udf1.getOcAttr())) {return skuObj.getUdf1();}
		else if (skuFieldStr.equals(POSFieldsEnum.udf2.getOcAttr())) {return skuObj.getUdf2();}
		else if (skuFieldStr.equals(POSFieldsEnum.udf3.getOcAttr())) {return skuObj.getUdf3();}
		else if (skuFieldStr.equals(POSFieldsEnum.udf4.getOcAttr())) {return skuObj.getUdf4();}
		else if (skuFieldStr.equals(POSFieldsEnum.udf5.getOcAttr())) {return skuObj.getUdf5();}
		else if (skuFieldStr.equals(POSFieldsEnum.udf6.getOcAttr())) {return skuObj.getUdf6();}
		else if (skuFieldStr.equals(POSFieldsEnum.udf7.getOcAttr())) {return skuObj.getUdf7();}
		else if (skuFieldStr.equals(POSFieldsEnum.udf8.getOcAttr())) {return skuObj.getUdf8();}
		else if (skuFieldStr.equals(POSFieldsEnum.udf9.getOcAttr())) {return skuObj.getUdf9();}
		else if (skuFieldStr.equals(POSFieldsEnum.udf10.getOcAttr())) {return skuObj.getUdf10();}

		else return null;

	}
   	
	public List<SkuFile> findSkuByQry(long userId,  String itemSidStr) {
    	
    	if(itemSidStr == null || itemSidStr.trim().length() == 0)   		return null;
    	logger.info(" itemSidStr  ==="+itemSidStr);
    	
    	String query = " FROM SkuFile  WHERE  userId = " + userId + " AND itemSid in ("+itemSidStr+")";
    	logger.info(" findSkuByQry query is "+query);
    	List<SkuFile> list = null;
		list  = getHibernateTemplate().find(query);
		return list;
    	
    }

	public SkuFile getSkuFileByCategory(String selectdCategory, String productCode, Long userId) {  
		
		String query = " FROM SkuFile  WHERE  userId = " + userId + " AND "+selectdCategory+" = '"+productCode+"'" ;
    	logger.info(" findSkuByQry query is "+query);
    	List<SkuFile> list = null;
		list  = getHibernateTemplate().find(query);
		if(list!= null && list.size() > 0)return list.get(0);
		return null;
	}
   	public List<Object> getAllSkuBy(Long userId,int startIndx,int endIndx){
   		String query = "select distinct sku,listPrice,description from SkuFile where userId =  "+userId.longValue()+"  And sku is not null ";
   		return executeQuery(query,startIndx,endIndx);	
		
   	}
   	public List<Object> getAllSkuSearchBy(Long userId,int startIndx,int endIndx,String searchStr){
   		String sbqry=" And sku is not null ";
   		if(searchStr.length()>0)
   			sbqry=" And sku like'%"+searchStr+"%' ";
   		String query = "select distinct sku,listPrice,description from SkuFile where userId =  "+userId.longValue()+sbqry;
   		return executeQuery(query,startIndx,endIndx);	
		
   	}
   	public List<SkuFile> getAllSkuByUserId(Long userId,int startIndx,int count,String searchStr){
   		String subQueryStr = "";
   		logger.info("searchStr :: "+searchStr);
   		if(searchStr!=null && !searchStr.trim().isEmpty()) {
   			String fieldArr[] = searchStr.split("::");
   			String filedName = fieldArr[0].trim();
   			String fieldVal = fieldArr[1].trim();
   			
   			if(filedName.equalsIgnoreCase("sid")) subQueryStr += " AND itemSid like '"+StringEscapeUtils.escapeSql(fieldVal)+"'";
   			else if(filedName.equalsIgnoreCase("description")) subQueryStr += " AND description like '%"+StringEscapeUtils.escapeSql(fieldVal)+"%'";
   			else if(filedName.equalsIgnoreCase("category")) subQueryStr += " AND itemCategory like '%"+StringEscapeUtils.escapeSql(fieldVal)+"%'";
   		}
   		String query = "FROM SkuFile where user_id="+userId.longValue()+subQueryStr+" ORDER BY skuId DESC";
   		logger.info("query :: "+query);
   		return executeQuery(query,startIndx,count);	
		
   	}
   	public int findItemsCount(Long userOrgId,String searchStr){
   		try{
   			
   			String subQueryStr = "";
   			logger.info("searchStr :: "+searchStr);
   			if(searchStr!=null && !searchStr.trim().isEmpty()) {
   	   			String fieldArr[] = searchStr.split("::");
   	   			String filedName = fieldArr[0].trim();
   	   			String fieldVal = fieldArr[1].trim();
   	   			
   	   			if(filedName.equalsIgnoreCase("sid")) subQueryStr += " AND itemSid like '"+StringEscapeUtils.escapeSql(fieldVal)+"'";
   	   			else if(filedName.equalsIgnoreCase("description")) subQueryStr += " AND description like '%"+StringEscapeUtils.escapeSql(fieldVal)+"%'";
   	   			else if(filedName.equalsIgnoreCase("category")) subQueryStr += " AND itemCategory like '%"+StringEscapeUtils.escapeSql(fieldVal)+"%'";
   	   		}
   	   		
   	   		String query = " SELECT count(*) FROM SkuFile where user_id ="+userOrgId+subQueryStr;
   	   		logger.info("query :: "+query);
   	   	
   	   		List itemsList = getHibernateTemplate().find(query);
   	   		if(itemsList.size()>0)
   	   			return ((Long)itemsList.get(0)).intValue();
   	   		else
   	   			return 0;
   		}catch(Exception e){
   			logger.error("**Exception :"+e+"**");
   			return 0;
   		}
   	}
   	
   	public List<String> getVariantBarcodeFromItemSID(Long userId, String itemSid){
   		List<String> res = new ArrayList<String>();
   		try {
   	// select udf12 from retail_pro_sku where user_id=1207 and item_sid in ('ETH881195','ETH873971') and ifnull(udf12,'')<>'' ;
   	
   		String query = "SELECT udf12 FROM SkuFile WHERE userId ="+userId+" and itemSid in"+
   		"("+itemSid+") and udf12 is not null"; 
   		
   	/*	String query = "select \n" + 
   				"   case \n" + 
   				"     when udf12 is not NULL then udf12\n" + 
   				"     else '' \n" + 
   				"   end\n" + 
   				"from  SkuFile WHERE userId ="+userId+" and itemSid in ("+itemSid+")";
*/   		
   		logger.info("query : "+query);
   		
   		 res = getHibernateTemplate().find(query);
			
   		 logger.info("my reco code" + res );

   		
   		if(res.size()>0) {
	   			return res;
   		}	else
	   			return null;
   				
   		}catch(Exception e) {
   			logger.info("Exception in varaint code : ",e);
   			return null;
   		}
   		
      	} // getVariantCode
   	
}
