package org.mq.captiway.scheduler.dao;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.RetailProSalesCSV;
import org.mq.captiway.scheduler.beans.SkuFile;
import org.mq.captiway.scheduler.utility.Constants;
import org.mq.captiway.scheduler.POSFieldsEnum;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

public class SkuFileDaoForDML extends AbstractSpringDaoForDML {
	
	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	
	public SkuFileDaoForDML() {}
	
	private JdbcTemplate jdbcTemplate;
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

    /*public SkuFile find(Long id) {
        return (SkuFile) super.find(SkuFile.class, id);
    }*/

    public void saveOrUpdate(SkuFile skuFile) {
        super.saveOrUpdate(skuFile);
    }

    public void delete(SkuFile skuFile) {
        super.delete(skuFile);
    }

   
    public void saveByCollection(Collection<SkuFile> skuFileCollection){
//    	super.saveOrUpdateAll(retailProSalesCollection);
    	getHibernateTemplate().saveOrUpdateAll(skuFileCollection);
    }

   /* public SkuFile findRecordBySKU(String skuStr ,Long listId) {
		try {
			List<SkuFile> list = null;
			list  = getHibernateTemplate().find("from SkuFile  where sku = '"+skuStr +"' and listId = " + listId);
			
			if(list.size() >0) {
				return list.get(0);
			}
			return null;
		} catch (DataAccessException e) {
			logger.error("Exception ::::" , e);
			return null;
		}
    }
    
    DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public SkuFile findSKUByPriority(TreeMap<String, List<String>> prioMap , String[] lineStr,  Long userId) {
 		
 		try {
 			String query = "FROM SkuFile WHERE userId="+userId+" ";
 			
// 			if(logger.isDebugEnabled()) logger.debug("prioMap="+prioMap);
 			Set<String> keySet = prioMap.keySet();
 			List<SkuFile> skuIdList = null;
 			
 			List<String> tempList=null;
 			for (String eachKey : keySet) {
				
// 				if(logger.isDebugEnabled()) logger.debug("Key ="+eachKey);
 				tempList = prioMap.get(eachKey);
 				for (String eachStr : tempList) {
					int index = Integer.parseInt(eachStr.substring(0,eachStr.indexOf('|')));
					
					//query += " AND "+ eachStr.substring(eachStr.indexOf('|')+1) + " = '"+ lineStr[index] +"' ";
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
 				
// 				if(logger.isDebugEnabled()) logger.debug("QUERY=="+query);
 				
 				skuIdList  = getHibernateTemplate().find(query);
 				
 				if(skuIdList.size() == 1) {
 					return skuIdList.get(0);
 				}
 				else if(skuIdList.size() > 1) {
 					if(logger.isDebugEnabled()) logger.debug("more than 1 object found :"+skuIdList.size());
 					return skuIdList.get(0);
 				}
 				
 				
			} // outer for
 			
 			return null;
 			
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::::" , e);
			return null;
		}
 		
 	}
	
	
	public SkuFile findSKUByPriority(TreeMap<String, List<String>> prioMap , SkuFile skuFile,  Long userId) {
	 		
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
								logger.error("Exception ::::" , e);
								return null;
							}
						}
						else if(posMappingDateFormatStr.toLowerCase().startsWith("string") || posMappingDateFormatStr.toLowerCase().startsWith("udf")) {
//							query += " AND "+ eachStr.substring(eachStr.indexOf('|')+1) + " = '"+ lineStr[index] +"' ";
							query += " AND "+ POSFieldsEnum.findByOCAttribute(tempStr[0].trim()) + " = '"+ valueStr +"' ";
						}else {
							query += " AND "+ POSFieldsEnum.findByOCAttribute(tempStr[0].trim()) + " = "+ valueStr +" ";
						}
						
						
					} // for 
	 				
	 				//logger.debug("QUERY=="+query);
	 				
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
				logger.error("Exception ::::" , e);
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
		else return null;

	}
	 	
*/
}
