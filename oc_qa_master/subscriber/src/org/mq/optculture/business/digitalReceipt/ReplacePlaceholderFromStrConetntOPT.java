package org.mq.optculture.business.digitalReceipt;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.JSONArray;

import org.mq.marketer.campaign.beans.Address;
import org.mq.marketer.campaign.beans.Contacts;
import org.mq.marketer.campaign.beans.ContactsLoyalty;
import org.mq.marketer.campaign.beans.DRSent;
import org.mq.marketer.campaign.beans.LoyaltyProgram;
import org.mq.marketer.campaign.beans.LoyaltyProgramTier;
import org.mq.marketer.campaign.beans.LoyaltySettings;
import org.mq.marketer.campaign.beans.LoyaltyTransactionChild;
import org.mq.marketer.campaign.beans.OTPGeneratedCodes;
import org.mq.marketer.campaign.beans.OrganizationStores;
import org.mq.marketer.campaign.beans.POSMapping;
import org.mq.marketer.campaign.beans.RetailProSalesCSV;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.ContactsDao;
import org.mq.marketer.campaign.dao.ContactsLoyaltyDao;
import org.mq.marketer.campaign.dao.DRSentDao;
import org.mq.marketer.campaign.dao.DigitalReceiptUserSettingsDao;
import org.mq.marketer.campaign.dao.OrganizationStoresDao;
import org.mq.marketer.campaign.dao.POSMappingDao;
import org.mq.marketer.campaign.dao.RetailProSalesDao;
import org.mq.marketer.campaign.dao.SkuFileDao;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.EncryptDecryptLtyMembshpPwd;
import org.mq.marketer.campaign.general.POSFieldsEnum;
import org.mq.marketer.campaign.general.PlaceHolders;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.business.helper.LoyaltyProgramHelper;
import org.mq.optculture.business.loyalty.LoyaltyProgramService;
import org.mq.optculture.data.dao.LoyaltyProgramDao;
import org.mq.optculture.data.dao.LoyaltyProgramTierDao;
import org.mq.optculture.data.dao.LoyaltyTransactionExpiryDao;
import org.mq.optculture.model.RecommendationResponseObject;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.springframework.beans.BeansException;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

//public class ReplacePlaceholderFromStrConetnt implements Runnable , ApplicationContextAware{
	public class ReplacePlaceholderFromStrConetntOPT {	
		
		private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
		/*ContactsDao contactsDao;
		POSMappingDao posMappingDao;
		OrganizationStoresDao organizationStoresDao;
		UsersDao usersDao;
		RetailProSalesDao retailProSalesDao;
		ContactsLoyaltyDao contactsLoyaltyDao;*/
		
		public Object[] replacePlaceHolderFromStrContent(Object[] pollObj, DRSent drSent) {
			Object[] replcePhContentStrObj = new Object[2];
			String templateContentStr = null;
			StringBuffer previwPhKeyValSb = null;
			try {
				POSMappingDao posMappingDao = null;
				ContactsDao contactsDao = null;
				Contacts contactObj= null ;
				OTPGeneratedCodes otpgencodesObj=null;
				
				Set<String> phSet ;
				templateContentStr =(String) pollObj[0];
				JSONObject drJson = (JSONObject)pollObj[1];
				Users users = (Users)pollObj[2];
				String productId = Constants.STRING_NILL;
				if(pollObj.length > 3) {
					 productId = (String)pollObj[3];
				}
				/*List<Object> daoObjeList = (List<Object>) pollObj[3];
				
				for (Object object : daoObjeList) {
					if(object instanceof OrganizationStoresDao) organizationStoresDao = (OrganizationStoresDao)object;
					else if(object instanceof POSMappingDao) posMappingDao = (POSMappingDao)object;
					else if(object instanceof ContactsDao) contactsDao = (ContactsDao)object;
					else if(object instanceof UsersDao) usersDao = (UsersDao)object;
					else if(object instanceof RetailProSalesDao) retailProSalesDao = (RetailProSalesDao)object;
					else if(object instanceof ContactsLoyaltyDao) contactsLoyaltyDao = (ContactsLoyaltyDao)object;
				}*/
				
				phSet = Utility.getDRConTentPlaceHolder(templateContentStr);
				Set<String> coupPhSet = Utility.findCoupPlaceholders(templateContentStr);
				
				//logger.info(" initial PHSet is ::"+phSet);
				
				if((phSet== null || phSet.size() == 0) && (coupPhSet == null || coupPhSet.size() == 0)) return replcePhContentStrObj;
				
				templateContentStr = templateContentStr.replaceAll(Pattern.quote("|^"), "[").replaceAll(Pattern.quote("^|"), "]");
				
				//Replacing Place holders from Request Json
				Map<String, Object> totalContValuesMap = new HashMap<String, Object>();
				Map<String, String> udfDatatypeMap = new HashMap<String, String>();
				boolean isContactFlag = false;
				if(users.isDigitalReceiptExtraction()){
					
					totalContValuesMap = replacePlaceHoldersFromJson(templateContentStr, drJson, phSet, users);
					if(totalContValuesMap.containsKey("1")) templateContentStr = (String)totalContValuesMap.get("1");
					if(totalContValuesMap.containsKey("2")) phSet = (Set)totalContValuesMap.get("2");
					if(totalContValuesMap.containsKey("3")) previwPhKeyValSb = (StringBuffer)totalContValuesMap.get("3");
					if(totalContValuesMap.containsKey("4")) {
						contactObj = (Contacts)totalContValuesMap.get("4");
						isContactFlag = true;
					}
					if(totalContValuesMap.containsKey("5")) udfDatatypeMap = (Map)totalContValuesMap.get("5");
					
					//logger.info(" 1st time phSet is  ::"+phSet);
					//logger.info(" 1st time previwPhKeyValSb is  ::"+previwPhKeyValSb);
					//logger.info(" 1st time udfDatatypeMap is  ::"+udfDatatypeMap);
					
				}
				
				logger.info("contactObj == "+contactObj);
				if(contactObj == null && phSet.size() >0) {
					contactObj = new Contacts();
					contactObj.setUsers(users);
					JSONObject recieptObj = (JSONObject)drJson.get("Receipt");
					/*if(recieptObj.containsKey("BillToCustSID") && recieptObj.containsValue("BillToCustSID")) {
						contactObj.setExternalId(recieptObj.get("BillToCustSID").toString());
						isContactFlag = true;
						
					}
					if(recieptObj.containsKey("BillToEMail") && recieptObj.containsValue("BillToEMail")) {
						contactObj.setEmailId(recieptObj.get("BillToEMail").toString());
						isContactFlag = true;
					}
					if(recieptObj.containsKey("BillToPhone1") && recieptObj.containsValue("BillToPhone1")) {
						contactObj.setMobilePhone(recieptObj.get("BillToPhone1").toString());
						isContactFlag = true;
					}*/
					
					if(recieptObj.containsKey("BillToCustSID")) { 
						String billToCust = recieptObj.get("BillToCustSID").toString();	
						if(!billToCust.isEmpty()){
							contactObj.setExternalId(recieptObj.get("BillToCustSID").toString());

							isContactFlag = true;
						}
					}
					if(recieptObj.containsKey("BillToEMail")) {
						String billToEMail = recieptObj.get("BillToEMail").toString();	
						
						if (!billToEMail.isEmpty()) {
							contactObj.setEmailId(recieptObj.get("BillToEMail")
									.toString());
							isContactFlag = true;
						}
					}
					if(recieptObj.containsKey("BillToPhone1")) {
						String billToPhone1 = recieptObj.get("BillToPhone1").toString();	
						
						if (!billToPhone1.isEmpty()) {
							contactObj.setMobilePhone(recieptObj.get(
									"BillToPhone1").toString());
							isContactFlag = true;
						}
					}
				}
				
				logger.info("contactObj === "+contactObj);
				contactsDao = (ContactsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_DAO);
				posMappingDao = (POSMappingDao)ServiceLocator.getInstance().getDAOByName(OCConstants.POSMAPPING_DAO);
				
				if(isContactFlag) {
					TreeMap<String, List<String>> treeMap = 
							Utility.getPriorityMap(users.getUserId(), Constants.POS_MAPPING_TYPE_CONTACTS, posMappingDao);
					
					contactObj = contactsDao.findContactByUniqPriority(treeMap, contactObj, users.getUserId().longValue(), users);
				}
				
				logger.info("contactObj ==== "+contactObj);
				
				
				try{

					if(phSet != null && phSet.size() >0){

						totalContValuesMap = replaceStoreRelatedMergeTags(drJson,templateContentStr, phSet,users,previwPhKeyValSb);


						if(totalContValuesMap.containsKey("1")) templateContentStr = (String)totalContValuesMap.get("1");
						if(totalContValuesMap.containsKey("2")) phSet = (Set)totalContValuesMap.get("2");
						if(totalContValuesMap.containsKey("3")) previwPhKeyValSb = (StringBuffer)totalContValuesMap.get("3");

					}
					
				}catch(Exception e){
					logger.error("Exception <<<<<<>>>>>> ",e);
				}
				
				
				logger.info("printing ph set before here.. "+phSet);
				// Replacing remaining place holders from DB object
				if(contactObj != null && phSet.size() >0) {
					
					totalContValuesMap	= 	replacePlaceHoldersFromContact(contactObj,otpgencodesObj, templateContentStr, phSet, previwPhKeyValSb, users,udfDatatypeMap,productId);
					
					if(totalContValuesMap.containsKey("1")) templateContentStr = (String)totalContValuesMap.get("1");
					if(totalContValuesMap.containsKey("2")) phSet = (Set)totalContValuesMap.get("2");
					if(totalContValuesMap.containsKey("3")) previwPhKeyValSb = (StringBuffer)totalContValuesMap.get("3");
					
					logger.info("printing ph set end contact here.. "+phSet);

					//logger.info(" 2nd time phSet is  ::"+phSet);
					//logger.info(" 2nd time previwPhKeyValSb is  ::"+previwPhKeyValSb);
					
				}
				logger.debug("1 :::  still exist Phset is :::"+phSet); 
				
				//Replace Date PlaceHolders
				if(phSet != null && phSet.size() >0) {
					totalContValuesMap = replaceDateTypePhValue(templateContentStr,phSet,previwPhKeyValSb);
	
					if(totalContValuesMap.containsKey("1")) templateContentStr = (String)totalContValuesMap.get("1");
					if(totalContValuesMap.containsKey("2")) phSet = (Set)totalContValuesMap.get("2");
					if(totalContValuesMap.containsKey("3")) previwPhKeyValSb = (StringBuffer)totalContValuesMap.get("3");
				//	logger.info(" 3rd time phSet is  ::"+phSet);
				//	logger.info(" 3rd time previwPhKeyValSb is  ::"+previwPhKeyValSb);
					
				}
				
				
				
				
				
				
				
				
				logger.debug("2 ::: still exist Phset is :::"+phSet);
				//If still exist Phset simply remove all
				if(phSet != null && phSet.size() > 0){
					
					
					 Iterator<String> phItr = phSet.iterator();
					 while (phItr.hasNext()) {
						String phStr = (String) phItr.next();
						String prePhStr = phStr;
						int defIndex = phStr.indexOf('=');
						String defVal="";
						if(defIndex != -1) {
							
							defVal = (phStr.length() == defIndex+1 )  ?  Constants.STRING_NILL : phStr.substring(defIndex+1);
							phStr = phStr.substring(0,phStr.indexOf("/")).trim();
							/*defVal = cfStr.substring(defIndex+1);
							cfStr = cfStr.substring(0,defIndex);*/
						} // if
						
						
						//logger.debug("Still exists phStr is ::"+phStr);
						templateContentStr = templateContentStr.replace("["+ prePhStr+"]", defVal);
						
						if (previwPhKeyValSb != null) { // change made on 23rd april 2015
							if (previwPhKeyValSb.length() > 0)
								previwPhKeyValSb
										.append(Constants.ADDR_COL_DELIMETER);
							previwPhKeyValSb.append("[" + prePhStr + "]"
									+ Constants.DELIMETER_DOUBLECOLON + defVal);
						}
						
						/*if(previwPhKeyValSb.length() > 0) previwPhKeyValSb.append(Constants.ADDR_COL_DELIMETER);
						
						previwPhKeyValSb.append("[" + prePhStr + "]" + Constants.DELIMETER_DOUBLECOLON +defVal);*/
						
					}
				}
				
				
				
				
				logger.info("coup ph set"+coupPhSet);
				
				if(coupPhSet != null && coupPhSet.size() > 0){
					JSONObject recieptObj = (JSONObject)drJson.get("Receipt");
					String  to = Constants.STRING_NILL;
					if(recieptObj.containsKey("BillToEMail")) {
						to = recieptObj.get("BillToEMail").toString();
					}
							
					String existingPhValStr = null;
					 String[] placeholdersArr = null;
					 boolean isCouponExists = false;
					 if(drSent != null) {
						 //[CC_442_aug-single]::aug;=;[CC_443_aug-multiple]::zw6z4d
						 existingPhValStr = drSent.getPhValStr();
						 if(existingPhValStr != null){
							if(existingPhValStr.contains(Constants.ADDR_COL_DELIMETER)){
								 placeholdersArr = existingPhValStr.split(Constants.ADDR_COL_DELIMETER);
								 for (String eachPH : placeholdersArr) {
									 
									 String[] eachPhArr = eachPH.split(Constants.DELIMETER_DOUBLECOLON);
									 if(eachPhArr[0].toLowerCase().startsWith("[cc_")) {
										 isCouponExists = true;
										 break;
									 }
									 
								 }
							 }else{
								 
								 placeholdersArr = new String[1];
								 placeholdersArr[0] = existingPhValStr;
								 if(placeholdersArr[0].split(Constants.DELIMETER_DOUBLECOLON)[0].toLowerCase().startsWith("[cc_")) {
									 isCouponExists = true;
								 }
							 }
						 
						 }
					 }
						for (String cfStr : coupPhSet) {
							
							 if(cfStr.startsWith("CC_")) {
								 String value=Constants.STRING_NILL;
								 if(isCouponExists) {
									 
									try {
										//TODO RE-PREPARE
										 for (String eachPH : placeholdersArr) {
											
											 String[] eachPhArr = eachPH.split(Constants.DELIMETER_DOUBLECOLON);
											 if(!eachPhArr[0].startsWith("[CC_")) continue;
											 
											 
											 if(("["+ cfStr + "]").equalsIgnoreCase(eachPhArr[0])) {
												 
												 value = eachPhArr[1];
												 break;
											 }
										}
									} catch (Exception e) {
										// TODO Auto-generated catch block
										logger.error("Exception ::", e);
									}
								 }
								 
								 if(value.isEmpty() || (isCouponExists && value.equals(Constants.NO_PROMOTION_STR))) {
									 String type = Constants.COUP_GENT_CAMPAIGN_TYPE_DR;
									//value = (value.equals(Constants.NO_PROMOTION_STR)) ? Constants.STRING_NILL : value;
									 value = Constants.STRING_NILL;
									 try {
										//to make coupon providing logic to be sync ,let the scheduler
										 //only offer a coupon code
										 logger.info("Issued to is:::"+to);
										String postData = "cfStr="+cfStr+"&issuedTo="+to+"&type="+type;
										URL url = new URL(PropertyUtil.getPropertyValue(Constants.COUP_PROVIDER_FOR_SUBSCRIBER_URL));
										logger.info("COUP_PROVIDER_FOR_SUBSCRIBER_URL :::"+url);
										HttpURLConnection urlconnection = (HttpURLConnection) url.openConnection();
										
										urlconnection.setRequestMethod("POST");
										urlconnection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
										urlconnection.setDoOutput(true);
	
										OutputStreamWriter out = new OutputStreamWriter(urlconnection.getOutputStream());
										out.write(postData);
										out.flush();
										out.close();
										
										
										BufferedReader in = new BufferedReader(	new InputStreamReader(urlconnection.getInputStream()));
										
										String decodedString = Constants.STRING_NILL;
										while ((decodedString = in.readLine()) != null) {
											value += decodedString;
										}
										in.close();
										logger.info("response is======>"+value);
									
										
									
									 } catch (BeansException e) {
									// TODO Auto-generated catch block
									logger.error("Exception ::" , e);
									return null;
								} catch (MalformedURLException e) {
									// TODO Auto-generated catch block
									logger.error("Exception ::" , e);
									return null;
								} catch (ProtocolException e) {
									// TODO Auto-generated catch block
									logger.error("Exception ::" , e);
									return null;
								} catch (IOException e) {
									// TODO Auto-generated catch block
									logger.error("Exception ::" , e);
									return null;
								}
								 }
								 if(value.isEmpty()) value = Constants.NO_PROMOTION_STR;
									
									templateContentStr = templateContentStr.replace("[" + cfStr + "]", value);
									if(previwPhKeyValSb == null) {
										previwPhKeyValSb = new StringBuffer();
										
									}
									if(previwPhKeyValSb.length() > 0) previwPhKeyValSb.append(Constants.ADDR_COL_DELIMETER);
									
									previwPhKeyValSb.append("[" + cfStr + "]" + Constants.DELIMETER_DOUBLECOLON +value);
									logger.info("place holder Str is::"+previwPhKeyValSb.toString());
									
							}//if
							
						}//for
					
				}// if
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Exception ::" , e);
			}finally{
				
				replcePhContentStrObj[0] = templateContentStr;
				replcePhContentStrObj[1] = previwPhKeyValSb;
				return replcePhContentStrObj;
			}
		} // replacePlaceHolderFromStrContent
		
		private Map replacePlaceHoldersFromJson(String templateCnt, JSONObject drJson, Set<String> phSet, Users users) {
//			logger.info(" eneter Here  >>>>>>>>>>>>>>>>>>>>>>> ");
			Contacts contactObj = new Contacts();
			contactObj.setUsers(users);
			
			Map<String,String> udfDataTypeMap = new HashMap<String, String>();
			Map<String, Object> contentValuesMap = new HashMap<String, Object>();
			
	//		Map<String, String> ocDrMap = null;
			POSMappingDao posMappingDao = null;
			try{
				posMappingDao = (POSMappingDao)ServiceLocator.getInstance().getDAOByName(OCConstants.POSMAPPING_DAO);
			}catch(Exception e){
				logger.error("Exception in getting daos.", e);
			}
			List<POSMapping> posMappList= posMappingDao.findAllByUserId(users.getUserId().longValue());
//			logger.info(" posMappingList Size is ::"+posMappList.size());
			StringBuffer prevPhStrBuff = new StringBuffer();
			if(posMappList != null && posMappList.size() > 0) {
				
				for (POSMapping posMapObj : posMappList) {
					
					if(posMapObj.getCustomFieldName().startsWith("UDF")) {
//						logger.info(" posMapObj.getCustomFieldName()  is ::"+posMapObj.getCustomFieldName());
						udfDataTypeMap.put(posMapObj.getCustomFieldName(), posMapObj.getDataType());
					}
					
					if(posMapObj.getDigitalReceiptAttribute() == null) continue;
					
					String custFieldValStr = posMapObj.getCustomFieldName();
					if(posMapObj.getMappingType().equals(Constants.POS_MAPPING_TYPE_CONTACTS) &&
							(custFieldValStr.equals(POSFieldsEnum.externalId.getOcAttr()) ||
									custFieldValStr.equals(POSFieldsEnum.emailId.getOcAttr()) ||
									custFieldValStr.equals(POSFieldsEnum.mobilePhone.getOcAttr()))) {
						
						String drCusFieldIdStr = posMapObj.getDigitalReceiptAttribute();
						String[] drCustStraArr = drCusFieldIdStr.split("::");
						JSONObject tempJsonObj =(JSONObject) drJson.get(drCustStraArr[0]);
						//get the customer id value from Json
						drCusFieldIdStr =(String) tempJsonObj.get(drCustStraArr[1]);
						
						if(drCusFieldIdStr != null && drCusFieldIdStr.trim().length() >0){
							
							if(custFieldValStr.equals("Customer ID")) 	{
								//logger.debug(">>>> 1::"+drCusFieldIdStr);
								contactObj.setExternalId(drCusFieldIdStr.trim());
							}
							if(custFieldValStr.equals("Email")) 	{
								//logger.debug(">>>> 2::"+drCusFieldIdStr);
								contactObj.setEmailId(drCusFieldIdStr.trim());
							}
							if(custFieldValStr.equals("Mobile")) 	{
								//logger.debug(">>>> 3::"+drCusFieldIdStr);
								contactObj.setMobilePhone((drCusFieldIdStr.trim()));
							}
						}
					}
					
					
					Iterator<String> phSetItr = phSet.iterator();
					
					while (phSetItr.hasNext()) {
						String eachPhStr = (String) phSetItr.next();
						String startStr = eachPhStr.substring(0,4);
						
						if( eachPhStr.startsWith(Constants.UDF_TOKEN) && !posMapObj.getMappingType().equals(Constants.POS_MAPPING_TYPE_CONTACTS))break;
						
						if(eachPhStr.startsWith("GEN_") || eachPhStr.startsWith(Constants.UDF_TOKEN) || eachPhStr.startsWith("REC_") ) {
							eachPhStr = eachPhStr.substring(4);
							
							String searchStr = eachPhStr;
							if(eachPhStr.contains("/")) {
								searchStr = eachPhStr.substring(0,eachPhStr.indexOf("/")).trim();
								/*String[] defaultstr = eachPhStr.split("/");
								searchStr = defaultstr[0];*/
								
							}
							
							//if(ocDrMap.containsKey(posDrKeyAttr)) {
								if(POSFieldsEnum.findByPhAttribute(searchStr) == null || 
										!posMapObj.getCustomFieldName().equals(POSFieldsEnum.findByPhAttribute(searchStr).getOcAttr())) {
										continue;
								}
							
	//							String tempDrJsonKey = ocDrMap.get(posDrKeyAttr);
								String tempDrJsonKey = posMapObj.getDigitalReceiptAttribute();
								String[] keyJsonObj = tempDrJsonKey.split("::");
								String drDataType = posMapObj.getDrDataType();
	//							logger.debug("DR attribute is :"+tempDrJsonKey+" :Data Type is :"+drDataType);
								if(drJson.containsKey(keyJsonObj[0]) ) {
									
									JSONObject tempJsonObj = null;
									String replacevalueStr = "";
									try {
										tempJsonObj = (JSONObject) drJson.get(keyJsonObj[0]);
										replacevalueStr = (String)tempJsonObj.get(keyJsonObj[1]); 
										
									} catch (Exception e) {
										
										JSONArray tempJsonArrObj = (JSONArray)drJson.get(keyJsonObj[0]);
										for (int i = 0; i < tempJsonArrObj.size(); i++) {
											try {
												tempJsonObj = (JSONObject)tempJsonArrObj.get(i);
												replacevalueStr = (String)tempJsonObj.get(keyJsonObj[1]);
												if(replacevalueStr != null && replacevalueStr.trim().length()  > 0) break;
											} catch (Exception e1) {
												replacevalueStr = "";
											}
										}
										
									}
									
									if(drDataType != null && drDataType.startsWith("Date") && replacevalueStr!=null && replacevalueStr.trim().length() >0) {
										try {
											drDataType = drDataType.substring(drDataType.indexOf("(")+1, drDataType.indexOf(")"));
											Calendar cal =Calendar.getInstance();
											
											cal.setTime(new SimpleDateFormat(drDataType).parse(replacevalueStr));
											replacevalueStr = MyCalendar.calendarToString(cal, MyCalendar.FORMAT_DATEONLY_GENERAL);
										} catch (ParseException e) {
											logger.error("Exception ::" , e);
											replacevalueStr = "";
										}
									}
									
									if(replacevalueStr != null && replacevalueStr.trim().length()  > 0) {
										templateCnt = templateCnt.replace("["+startStr+eachPhStr+"]", replacevalueStr);
										
										if(prevPhStrBuff.length() > 0) prevPhStrBuff.append(Constants.ADDR_COL_DELIMETER);
										prevPhStrBuff.append("["+startStr+eachPhStr+"]" + Constants.DELIMETER_DOUBLECOLON + replacevalueStr);
										
										phSetItr.remove();
									}
								}
						}
					
					} // while
					
				} // for
				//logger.debug(">>> "+contactObj.getExternalId());
			}
		//	logger.info(" Udf Map is  ::"+udfDataTypeMap);
			contentValuesMap.put("1", templateCnt);
			contentValuesMap.put("2", phSet);
			contentValuesMap.put("3", prevPhStrBuff);
			contentValuesMap.put("4", contactObj);
			contentValuesMap.put("5", udfDataTypeMap);
			return contentValuesMap;
		} // replacePlaceHoldersFromJson
		
		private Map<String, Object> replacePlaceHoldersFromContact(Contacts contact, OTPGeneratedCodes otpgencodes,String tempHtmlContent,
																		Set<String> totalPhSet, StringBuffer phKeyValueSb, Users users, Map<String, String> udfDtatTypeMap,String productIds) {
			String value="";
			String preStr = "";
			
			Set<String> recoTag = new HashSet<String>();

			if(phKeyValueSb == null) phKeyValueSb = new StringBuffer();
			Map<String, Object> phContentValMap= new HashMap<String, Object>();
			Iterator<String> phSetItr = totalPhSet.iterator();
			List<RecommendationResponseObject> recommlist = new ArrayList<>(); // for replacing those here.
			HttpPostClass httpPost = new HttpPostClass();
			while (phSetItr.hasNext()) {
				String cfStr = (String) phSetItr.next();
				
				preStr = cfStr;
				if(cfStr.startsWith("REC_")) {
					recoTag.add(cfStr); // adding all the recommend tags to process separately.. 
					logger.info("added the reco : "+cfStr);
					phSetItr.remove();
					continue;
				}
				if(cfStr.startsWith("GEN_")) {
					
					cfStr = cfStr.substring(4);
					String defVal="";
					int defIndex = cfStr.indexOf('=');
					
					if(defIndex != -1) {
						
						defVal = (cfStr.length() == defIndex+1 )  ?  Constants.STRING_NILL : cfStr.substring(defIndex+1);
						cfStr = cfStr.substring(0,cfStr.indexOf("/")).trim();
						/*defVal = cfStr.substring(defIndex+1);
						cfStr = cfStr.substring(0,defIndex);*/
					} // if
					
					if(cfStr.equalsIgnoreCase("emailId")) {
						value = (contact.getEmailId() ==null) ?defVal : contact.getEmailId() ;
					}
					else if(cfStr.equalsIgnoreCase("email")) {
						value = (contact.getEmailId() ==null) ? defVal :contact.getEmailId()  ;
					}
					else if(cfStr.equalsIgnoreCase("firstName")) {
						value = (contact.getFirstName() ==null) ?defVal : contact.getFirstName();
					}
					else if(cfStr.equalsIgnoreCase("lastName"))	{
						value = (contact.getLastName() ==null) ?defVal : contact.getLastName()  ;
					}
					else if(cfStr.equalsIgnoreCase("addressOne")) {
						value = (contact.getAddressOne() ==null) ?defVal :contact.getAddressOne() ;
					}
					else if(cfStr.equalsIgnoreCase("addressTwo")) {
						value = (contact.getAddressTwo() ==null) ?defVal : contact.getAddressTwo() ;
					}
					else if(cfStr.equalsIgnoreCase("city"))	{
						value = (contact.getCity() ==null) ?defVal : contact.getCity()  ;
					}
					else if(cfStr.equalsIgnoreCase("state")) {
						value = (contact.getState() ==null) ?defVal : contact.getState() ;
					}
					else if(cfStr.equalsIgnoreCase("country")) {
						value = (contact.getCountry() ==null )?defVal :contact.getCountry() ;
					}
					else if(cfStr.equalsIgnoreCase("pin"))	{
						value = contact.getZip() ==null ?defVal : contact.getZip()  ;
					}
					//otp
					else if(cfStr.equalsIgnoreCase("otpCode"))	{
						value = otpgencodes.getOtpCode() ==null ?defVal : otpgencodes.getOtpCode()  ;
					}
					
					
					else if(cfStr.equalsIgnoreCase("phone")) {
						value = contact.getMobilePhone() != null && contact.getMobilePhone().length() != 0 ? contact.getMobilePhone() : Constants.STRING_NILL;
						if(value.isEmpty()) value=defVal;
					}
					else if(cfStr.equalsIgnoreCase("gender")) {
						value = contact.getGender() == null ? defVal : contact.getGender();
					}
					else if(cfStr.equalsIgnoreCase("birthday") ) {					
									if( contact.getBirthDay() != null) {	
										value = MyCalendar.calendarToString(contact.getBirthDay(), MyCalendar.FORMAT_DATEONLY_GENERAL);
									}else{
										if(defVal.isEmpty());
										value=defVal;
									}
					}
					else if(cfStr.equalsIgnoreCase("anniversary") ) {
								if(contact.getAnniversary() != null) {
									value = MyCalendar.calendarToString(contact.getAnniversary(), MyCalendar.FORMAT_DATEONLY_GENERAL);
								}
								else{
									if(defVal.isEmpty()) ;
									value=defVal;
								}
					}
					else if(cfStr.equalsIgnoreCase("createdDate") ) {
						if(contact.getCreatedDate() != null) {
							value = MyCalendar.calendarToString(contact.getCreatedDate(), MyCalendar.FORMAT_DATEONLY_GENERAL);
						}
						else{
							if(defVal.isEmpty()) ;
							value=defVal;
						}
			}
					
					else if(cfStr.equalsIgnoreCase("organizationName") ) {

						value = getUserOrganization(users, defVal);
					}
					
					/*else if(cfStr.toLowerCase().startsWith(PlaceHolders.CAMPAIGN_PH_STARTSWITH_STORE)) { // need to remove this
						value= "";
	//					value = getStorePlaceholders(contact,cfStr);
					}
					
					else if(cfStr.toLowerCase().startsWith(PlaceHolders.CAMPAIGN_ADDRESS_PH_STARTSWITH_LASETPURCHASE)) { //TODO
						logger.debug(">>> cfstr ::"+cfStr);
						value = "";
	//					value = getContactLastPurchaseStoreAddr(contact);
					}*/
					else if(cfStr.toLowerCase().startsWith(PlaceHolders.CAMPAIGN_PH_STARTSWITH_LOYALTY)) {
						
						value = getLoyaltyPlaceholders(contact,cfStr,defVal);
						
					}
					
					else if(cfStr.startsWith(PlaceHolders.CAMPAIGN_PH_STARTSWITH_LASETPURCHASE)) {
						try {
							value = getlastpurchasePlaceHolders(contact,cfStr,users,defVal);
						} catch (Exception e) {
							logger.error("Exception: "+e);
						}
					}
								  
					else {
						value = "";
					}
	
					//logger.debug(">>>>>>>>> Gen token <<<<<<<<<<< :" + cfStr + " - Value :" + value);
					try {
						
						if(value != null && value.trim().length() > 0) {
							
							//cfStr = cfStr.toLowerCase();
							tempHtmlContent = tempHtmlContent.replace("["+preStr+"]", value);
																				
						} else {
							value = defVal;
							tempHtmlContent = tempHtmlContent.replace("["+preStr+"]", value);
							
						}
							
					} catch (Exception e) {
						//logger.error("Exception while adding the General Fields as place holders ", e);
					}
					
					if(phKeyValueSb.length() > 0) phKeyValueSb.append(Constants.ADDR_COL_DELIMETER);
					
					phKeyValueSb.append("[" + preStr + "]" + Constants.DELIMETER_DOUBLECOLON + value);
					
					phSetItr.remove();
					
				} 
				else if(cfStr.startsWith("UDF_")) {
					
						cfStr = cfStr.substring(4);
						String defVal="";
						int defIndex = cfStr.indexOf('=');
						
						if(defIndex != -1) {
							defVal = (cfStr.length() == defIndex+1 )  ?  Constants.STRING_NILL : cfStr.substring(defIndex+1);
							cfStr = cfStr.substring(0,cfStr.indexOf("/")).trim();
							/*defVal = cfStr.substring(defIndex+1);
							cfStr = cfStr.substring(0,defIndex);*/
						} // if
						
				//		logger.info(">>>>> udfDataType Map ::"+udfDtatTypeMap);
					if(!udfDtatTypeMap.containsKey(cfStr)) continue;
					String udfDataTypeStr = udfDtatTypeMap.get(cfStr);
					int idx = Integer.parseInt(cfStr.substring("UDF".length()));
					
					try {
						value = getConatctCustFields(contact, idx,udfDataTypeStr,defVal);
						if(value==null || value.isEmpty()) value=defVal;
					} catch (Exception e) {
						logger.error("Exception ::" , e);
					}
					
					if(value != null && value.trim().length() > 0) 
						tempHtmlContent = tempHtmlContent.replace("["+preStr+"]", value);
					 else {
						value =defVal;
						tempHtmlContent = tempHtmlContent.replace("["+preStr+"]",value);
					}
					
					if(phKeyValueSb.length() > 0) phKeyValueSb.append(Constants.ADDR_COL_DELIMETER);
					
					phKeyValueSb.append("[" + preStr + "]" + Constants.DELIMETER_DOUBLECOLON + value);
					
					phSetItr.remove();
				}
			} // while of all ph set processing
			
			// recommendation block for all recom tags.
			// write replace logic for the merge tags : recommendations
			int totalCount = recoTag.size(); // to check no recommendation ( 5 tags - 0 suggestions)
			int replacedCount = 0; // to check case -(10 tags -  2 suggestion )
			if(recoTag != null  && recoTag.size() > 0) {

				if(productIds.length() > 0) {
			
				if(recommlist == null   || recommlist.size() <= 0) {
					
					try {
						SkuFileDao skufileDao = (SkuFileDao)ServiceLocator.getInstance().getDAOByName(OCConstants.SKU_FILE_DAO);						
					//	List<String> variantCodes = skufileDao.getVariantBarcodeFromItemSID(1207L, productIds);
						//	List<String> variantCodes = skufileDao.getVariantBarcodeFromItemSID(719L, productIds);
							List<String> variantCodes = skufileDao.getVariantBarcodeFromItemSID(1072L, productIds);

						if(variantCodes !=null && variantCodes.size()>0) {
						String productStr = Constants.STRING_NILL;
						for(String eachCode : variantCodes) {
							if(eachCode != null && !eachCode.isEmpty()) {
								if(!productStr.isEmpty()) {
									productStr+=Constants.DELIMETER_COMMA;
								}
								productStr+=eachCode;
							}
						}
						recommlist= httpPost.fetchRecommendationByHTTP(productStr,contact.getUsers().getUserId());
						}
				}catch(Exception e) {
					logger.error("Exception during fetching suggstion data : ",e);
				}	
			} // if recommlist >0
			Iterator<String> recommendItr = recoTag.iterator();
			while (recommendItr.hasNext()) {
			//	String replaceStr = Constants.STRING_NILL;
				String cfStr = (String) recommendItr.next();	
				preStr = cfStr;
				cfStr = cfStr.substring(4);
		
	//	#reco_recommendllink_1
		if(recommlist  != null && recommlist.size()> 0) {

		String replaceValueStr = "";
		String[] index = cfStr.split("_");  // index[0]: product link/image url/description , index[1] : 1/2/3/4
		int pos = Integer.parseInt(index[1]); // number 1/ 2/3/4
		RecommendationResponseObject recomm = new RecommendationResponseObject();
	//	String checkIndex = index[1];

		try {
			recomm = recommlist.get(pos-1);
			if(recomm == null) {
				break; // if it has less suggestion and more tags case. - unable to 
			}
			logger.info("Reco object : "+recomm.getVariant_code()+" :: image :: "+recomm.getImage_url() +" :: description :: "+
																											recomm.getDescription());
			
			
		}catch(IndexOutOfBoundsException ie ) {
			logger.info("No objects are detected. removing the tags. at"+(pos-1));
			replaceValueStr = "";
			if(cfStr.contains("RecoProductLink") && cfStr.contains(index[1])) {

				replaceValueStr = recomm.getVariant_code(); // need to be link here. for now providing the code.
				logger.info("added the reco prod : "+cfStr + "Replaced : "+replaceValueStr);

			}
			else if(cfStr.contains("ImageDescription") && cfStr.contains(index[1])) {
				replaceValueStr = recomm.getDescription();
				logger.info("added the reco desc : "+cfStr + "Replaced : "+replaceValueStr);

				
			}
			else if(cfStr.contains("RecommendedImage") && cfStr.contains(index[1])) {
				replaceValueStr = recomm.getImage_url();
				logger.info("added the reco image : "+cfStr + "Replaced : "+replaceValueStr);

				
			}
			tempHtmlContent = tempHtmlContent.replace("["+preStr+"]", replaceValueStr);
			
			continue;
		}
		catch(Exception e) {
			logger.error("Exception while getting recommendation : "+ e);
			
			break; // less suggestion more tags.
		}
	// if reco_recomproduct && recoProductlink1 has 1 => replaces for recomm first object.
		if(index[0].equalsIgnoreCase("RecoProductLink") && cfStr.contains(index[1])) {

			replaceValueStr = recomm.getVariant_code(); // need to be link here. for now providing the code.
			logger.info("added the reco prod : "+cfStr + "Replaced : "+replaceValueStr);

		}
		else if(index[0].equalsIgnoreCase("ImageDescription") && cfStr.contains(index[1])) {
			replaceValueStr = recomm.getDescription();
			logger.info("added the reco desc : "+cfStr + "Replaced : "+replaceValueStr);

			
		}
		else if(index[0].equalsIgnoreCase("RecommendedImage") && cfStr.contains(index[1])) {
			replaceValueStr = recomm.getImage_url();
			logger.info("added the reco image : "+cfStr + "Replaced : "+replaceValueStr);

			
		}
		else {
			replaceValueStr = Constants.STRING_NILL;// replace with empty. to handle not exisiting. 
			logger.info("added the reco no tag: "+cfStr + "Replaced : "+replaceValueStr);


		}
		if(replaceValueStr != null) {
			
			//cfStr = cfStr.toLowerCase();
			tempHtmlContent = tempHtmlContent.replace("["+preStr+"]", replaceValueStr);
			logger.info("added the reco all : "+cfStr + "Replaced : "+replaceValueStr);
			replacedCount+=1;
			recommendItr.remove(); // removing after processing tags. will not contain in the total phset
			
																
		}
		}else {
		logger.info("There are no suggestion given for this contact., got null so replacing with the empty tagGF"+recommlist);
		String emptyTag =  Constants.STRING_NILL;
		tempHtmlContent = tempHtmlContent.replace("["+preStr+"]", emptyTag);
		recommendItr.remove(); // removing after processing tags. will not contain in the total phset
		totalCount -=1;
		//need to add here also no suggestion
		if(totalCount == 0  &&  replacedCount == 0)
			tempHtmlContent = tempHtmlContent.replace("[CU_VISIBLE]","display:none;");

		}
			} // while
				}
			}
			else{
				// need to remove suggestion product block if no responses.
				tempHtmlContent = tempHtmlContent.replace("[CU_VISIBLE]","display:none;");
				logger.info("Removed suggestion if no tags are present. ");
				
			}
			logger.info("after processing reco tags>>>>>>");
		//end of the recommendations.
			//logger.debug(">>>>>>>>>>>>>>>>"+tempHtmlContent);
			phContentValMap.put("1", tempHtmlContent);	
			phContentValMap.put("2", totalPhSet);	
			phContentValMap.put("3", phKeyValueSb);	
			return phContentValMap;
			
		} // replacePlaceHoldersFromContact
		
		
		/*
		 * This method retrieves contacts loyalty details to replace place holders in campaign subject line	
		 */

		private String getLoyaltyPlaceholders(Contacts contact, String placeholder,String defVal) {
			try {

				ContactsLoyaltyDao contactsLoyaltyDao = (ContactsLoyaltyDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
				String loyaltyPlaceholder = defVal;
				if(contact.getContactId() != null  ){ 
					//ContactsLoyalty contactsLoyalty = contactsLoyaltyDao.findByContactId(campaign.getUsers().getUserOrganization().getUserOrgId(), contact.getContactId());
					logger.info("**************************** Contact Id ************* "+contact.getContactId());
					ContactsLoyalty contactsLoyalty = contactsLoyaltyDao.findByContactId(contact.getUsers().getUserId(), contact.getContactId());
					//logger.info("gotloyalty obj ==="+contactsLoyalty+" "+placeholder);

					if(contactsLoyalty != null){
						if(OCConstants.LOYALTY_MEMBERSHIP_STATUS_CLOSED.equalsIgnoreCase(contactsLoyalty.getMembershipStatus()) && contactsLoyalty.getTransferedTo() != null){
							
							contactsLoyalty = contactsLoyaltyDao.findAllByLoyaltyId(contactsLoyalty.getTransferedTo());
						}
						loyaltyPlaceholder =  replaceLoyaltyPlaceHolders(placeholder,contactsLoyalty,defVal);
						logger.info("Replaced Loyalty Place Holder :: "+loyaltyPlaceholder);
					}//if contacts loyalty not equal null
				}
				return loyaltyPlaceholder != null && !loyaltyPlaceholder.trim().isEmpty() ? loyaltyPlaceholder : defVal;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Contacts loyalty place holders: "+e);
				//logger.error("Exception ::" , e);
				return null;
			}
		}


		/**
		 * This method replace loyalty place holders
		 * @param placeholder
		 * @param contactsLoyalty
		 * @param defVal
		 * @return Loyalty Place Holder
		 * @throws Exception 
		 */
		private String replaceLoyaltyPlaceHolders(String placeholder,ContactsLoyalty contactsLoyalty,String defVal) throws Exception {

			if(contactsLoyalty == null){
				return defVal;
			}
			if(placeholder == null  && defVal == null){
				logger.error("Value is null placeholder :: "+placeholder+"\t contactsLoyalty "+contactsLoyalty+"\t defVal"+defVal);
				return defVal;
			}

			logger.info("In replaceLoyaltyPlaceHolders :: " +  placeholder);
			DecimalFormat decimalFormat = new DecimalFormat("#0.00");
			String loyaltyPlaceholder="";
			//LOYALTYCARDPIN
			if(PlaceHolders.CAMPAIGN_PH_LOYALTY_MEMBERSHIP_PIN.equalsIgnoreCase(placeholder)){
				loyaltyPlaceholder = contactsLoyalty.getCardPin()!= null ? contactsLoyalty.getCardPin(): defVal;
				logger.info("Membership Pin ::"+contactsLoyalty.getCardPin());
			}
			//SB LOYALTYCARDPIN
			if(PlaceHolders.CAMPAIGN_PH_LOYALTYCARDPIN.equalsIgnoreCase(placeholder)){
				loyaltyPlaceholder = contactsLoyalty.getCardPin()!= null ? contactsLoyalty.getCardPin(): defVal;
				logger.info("Membership Pin ::"+contactsLoyalty.getCardPin());
			}
			//REFRESHEDON
			else if(PlaceHolders.CAMPAIGN_PH_LOYALTY_REFRESHEDON.equalsIgnoreCase(placeholder) ){
				loyaltyPlaceholder = contactsLoyalty.getLastFechedDate() ==  null ? defVal :  MyCalendar.calendarToString(contactsLoyalty.getLastFechedDate(), MyCalendar.FORMAT_DATEONLY_GENERAL);
			}
			//OC MEMBERSHIP_NUMBER
			else if(PlaceHolders.CAMPAIGN_PH_LOYALTY_MEMBERSHIP_NUMBER.equalsIgnoreCase(placeholder)){
				loyaltyPlaceholder = contactsLoyalty.getCardNumber() != null ? contactsLoyalty.getCardNumber()+"":defVal;
				logger.info("Membership Number ::"+contactsLoyalty.getCardNumber());
			}
			//SB LOYALTY_CARDNUMBER
			else if(PlaceHolders.CAMPAIGN_PH_LOYALTY_CARDNUMBER.equalsIgnoreCase(placeholder)){
				loyaltyPlaceholder = contactsLoyalty.getCardNumber() != null ? contactsLoyalty.getCardNumber()+"":defVal;
				logger.info("LOYALTY_CARDNUMBER ::"+contactsLoyalty.getCardNumber());
			}
			//MEMBER_TIER
			else if(PlaceHolders.CAMPAIGN_PH_LOYALTY_MEMBER_TIER.equalsIgnoreCase(placeholder)){
				loyaltyPlaceholder = contactsLoyalty.getProgramTierId() != null ? getMemberTier(contactsLoyalty.getProgramTierId() , defVal) : defVal; 
				logger.info("Member Tier ::"+loyaltyPlaceholder);
			}
			//MEMBER_STATUS
			else if(PlaceHolders.CAMPAIGN_PH_LOYALTY_MEMBER_STATUS.equalsIgnoreCase(placeholder)){
				logger.info("MEMBER_STATUS contactsLoyalty.getMembershipStatus() ::"+contactsLoyalty.getMembershipStatus());
				loyaltyPlaceholder = contactsLoyalty.getMembershipStatus() != null ? contactsLoyalty.getMembershipStatus() : defVal;
				logger.info("MEMBER_STATUS ::"+loyaltyPlaceholder);
			}
			//LOYALTY_ENROLLMENT_DATE
			else if(PlaceHolders.CAMPAIGN_PH_LOYALTY_ENROLLMENT_DATE.equalsIgnoreCase(placeholder)){
				loyaltyPlaceholder = contactsLoyalty.getCreatedDate() != null ?  MyCalendar.calendarToString(contactsLoyalty.getCreatedDate(), MyCalendar.FORMAT_DATETIME_STYEAR) : defVal ;
				logger.info("LOYALTY_ENROLLMENT_DATE ::"+loyaltyPlaceholder);
			}
			//LOYALTY_ENROLLMENT_SOURCE
			else if(PlaceHolders.CAMPAIGN_PH_LOYALTY_ENROLLMENT_SOURCE.equalsIgnoreCase(placeholder)){
				loyaltyPlaceholder = contactsLoyalty.getContactLoyaltyType() != null ? getEnrollmentSource(contactsLoyalty.getContactLoyaltyType() , defVal) : defVal;
				logger.info("LOYALTY_ENROLLMENT_SOURCE ::"+loyaltyPlaceholder);
			}
			//LOYALTY_ENROLLMENT_STORE
			else if(PlaceHolders.CAMPAIGN_PH_LOYALTY_ENROLLMENT_STORE.equalsIgnoreCase(placeholder)){
				loyaltyPlaceholder = contactsLoyalty.getPosStoreLocationId() != null ? contactsLoyalty.getPosStoreLocationId()+"":defVal;
				logger.info("LOYALTY_ENROLLMENT_STORE ::"+loyaltyPlaceholder);
			}
			//LOYALTY_REGISTERED_PHONE
			else if(PlaceHolders.CAMPAIGN_PH_LOYALTY_REGISTERED_PHONE.equalsIgnoreCase(placeholder)){
				loyaltyPlaceholder = contactsLoyalty.getMobilePhone() != null ? contactsLoyalty.getMobilePhone():defVal;
				logger.info("LOYALTY_REGISTERED_PHONE ::"+loyaltyPlaceholder);
			}
			//LOYALTY_POINTS_BALANCE
			else if(PlaceHolders.CAMPAIGN_PH_LOYALTY_POINTS_BALANCE.equalsIgnoreCase(placeholder)) {
				loyaltyPlaceholder = (contactsLoyalty.getLoyaltyBalance() != null&& contactsLoyalty.getMembershipStatus().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_STATUS_ACTIVE)) ? contactsLoyalty.getLoyaltyBalance().longValue()+"" : defVal;
				logger.info("LOYALTY_POINTS_BALANCE ::"+loyaltyPlaceholder);
			}
			//LOYALTY_MEMBERSHIP_CURRENCY_BALANCE
			else if(PlaceHolders.CAMPAIGN_PH_LOYALTY_MEMBERSHIP_CURRENCY_BALANCE.equalsIgnoreCase(placeholder)){
				loyaltyPlaceholder = (contactsLoyalty.getGiftcardBalance() != null && contactsLoyalty.getMembershipStatus().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_STATUS_ACTIVE)) ?  decimalFormat.format(contactsLoyalty.getGiftcardBalance()) : defVal;
				logger.info("LOYALTY_MEMBERSHIP_CURRENCY_BALANCE ::"+loyaltyPlaceholder);
			}
			//GIFTCARD_BALANCE
			else if(PlaceHolders.CAMPAIGN_PH_GIFTCARD_BALANCE.equalsIgnoreCase(placeholder)){
				loyaltyPlaceholder = (contactsLoyalty.getGiftcardBalance() != null&& contactsLoyalty.getMembershipStatus().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_STATUS_ACTIVE)) ?  decimalFormat.format(contactsLoyalty.getGiftcardBalance()) : defVal;
				logger.info("GIFTCARD_BALANCE ::"+loyaltyPlaceholder);
			}
			//LOYALTY_GIFT_BALANCE
			else if(PlaceHolders.CAMPAIGN_PH_LOYALTY_GIFT_BALANCE.equalsIgnoreCase(placeholder)){
				loyaltyPlaceholder = (contactsLoyalty.getGiftBalance() != null&& contactsLoyalty.getMembershipStatus().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_STATUS_ACTIVE)) ? decimalFormat.format(contactsLoyalty.getGiftBalance()) : defVal;
				logger.info("LOYALTY_GIFT_BALANCE ::"+loyaltyPlaceholder);
			}
			//LOYALTY_LIFETIME_PURCHASE_VALUE
			else if(PlaceHolders.CAMPAIGN_PH_LOYALTY_LIFETIME_PURCHASE_VALUE.equalsIgnoreCase(placeholder)){
				//defVal=userCurrencySymbol+"0.00";
				Double lpv = null;
				lpv= LoyaltyProgramHelper.getLPV(contactsLoyalty);
				loyaltyPlaceholder = (lpv != null&& contactsLoyalty.getMembershipStatus().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_STATUS_ACTIVE)) ? decimalFormat.format(lpv) : defVal;
				logger.info("CAMPAIGN_PH_LOYALTY_LIFETIME_PURCHASE_VALUE ::"+loyaltyPlaceholder);
			}
			//OC LOYALTY_LIFETIME_POINTS
			else if(PlaceHolders.CAMPAIGN_PH_LOYALTY_LIFETIME_POINTS.equalsIgnoreCase(placeholder)){
				loyaltyPlaceholder = (contactsLoyalty.getTotalLoyaltyEarned() != null && contactsLoyalty.getMembershipStatus().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_STATUS_ACTIVE)) ? contactsLoyalty.getTotalLoyaltyEarned().longValue()+" Points" : defVal;
				logger.info("LOYALTY_LIFETIME_POINTS ::"+loyaltyPlaceholder);
			}
			//redeemable currency
	    	else if(PlaceHolders.CAMPAIGN_PH_LOYALTY_RC.equalsIgnoreCase(placeholder)){
	    		
	    		if(contactsLoyalty.getProgramTierId() != null && contactsLoyalty.getMembershipStatus().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_STATUS_ACTIVE)) {
	    			try {
	    				double loyaltyAmount = contactsLoyalty.getGiftcardBalance() == null ? 0.0 : contactsLoyalty.getGiftcardBalance();
	    				double giftAmount = contactsLoyalty.getGiftBalance() == null ? 0.0 : contactsLoyalty.getGiftBalance();
	    				double pointsAmount = 0.0;
	    				double totalReedmCurr = 0.0;
	    				
	    				pointsAmount = LoyaltyProgramHelper.calculatePointsAmount(contactsLoyalty, new LoyaltyProgramService().getTierObj(contactsLoyalty.getProgramTierId()));
	    				
	    				totalReedmCurr = loyaltyAmount + pointsAmount + giftAmount;
	    				Double totalRedeemableAmount = 	loyaltyAmount + pointsAmount + giftAmount;
	    				loyaltyPlaceholder = totalRedeemableAmount.toString();
	    				logger.info("CAMPAIGN_PH_LOYALTY_LRC"+loyaltyPlaceholder);

	    			} catch (Exception e) {
	    				// TODO Auto-generated catch block
	    				logger.error("Exception ::"+e);
	    				
	    			}
	    			
	    		}
	    	}
			//LOYALTY_GIFT_CARD_EXPIRATION
			else if(PlaceHolders.CAMPAIGN_PH_LOYALTY_GIFT_CARD_EXPIRATION_DATE.equalsIgnoreCase(placeholder)){
				loyaltyPlaceholder = getGiftCardExpirationDate(contactsLoyalty ,defVal);
				logger.info("LOYALTY_GIFT_CARD_EXPIRATION ::"+loyaltyPlaceholder);
			}
			//LOYALTY_HOLD_BALANCE
			else if(PlaceHolders.CAMPAIGN_PH_LOYALTY_HOLD_BALANCE.equalsIgnoreCase(placeholder)) {
				loyaltyPlaceholder = getHoldBalance(contactsLoyalty,defVal);
				logger.info("LOYALTY_HOLD_BALANCE ::"+loyaltyPlaceholder);
			}
			//LOYALTY_REWARD_ACTIVATION_PERIOD
			else if(PlaceHolders.CAMPAIGN_PH_LOYALTY_REWARD_ACTIVATION_PERIOD.equalsIgnoreCase(placeholder)){
				loyaltyPlaceholder = contactsLoyalty.getProgramTierId() != null ? getRewardActivationPeriod(contactsLoyalty.getProgramTierId(),defVal) : defVal ;
				logger.info("LOYALTY_REWARD_ACTIVATION_PERIOD::"+loyaltyPlaceholder);
			}
			//LOYALTY_LAST_EARNED_VALUE
			else if(PlaceHolders.CAMPAIGN_PH_LOYALTY_LAST_EARNED_VALUE.equalsIgnoreCase(placeholder)){
				loyaltyPlaceholder = contactsLoyalty.getCardNumber() != null ? getLastEarnedValue(contactsLoyalty.getLoyaltyId(),OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE,defVal,contactsLoyalty.getUserId()) : defVal;
				logger.info("LOYALTY_LAST_EARNED_VALUE::"+loyaltyPlaceholder);
			}
			//LOYALTY_LAST_REDEEMED_VALUE
			else if(PlaceHolders.CAMPAIGN_PH_LOYALTY_LAST_REDEEMED_VALUE.equalsIgnoreCase(placeholder)){
				loyaltyPlaceholder = contactsLoyalty.getCardNumber() != null ? getLastRedeemedValue(contactsLoyalty.getLoyaltyId(),OCConstants.LOYALTY_TRANS_TYPE_REDEMPTION,defVal,contactsLoyalty.getUserId()) : defVal;
				logger.info("LOYALTY_LAST_REDEEMED_VALUE::"+loyaltyPlaceholder);
			}
			else if(PlaceHolders. CAMPAIGN_PH_LOYALTY_MEMBERSHIP_PASSWORD.equalsIgnoreCase(placeholder)){
				loyaltyPlaceholder = getMembershipPassword(contactsLoyalty,defVal);
				logger.info("LOYALTY_MEMBERSHIP_PASSWORD::"+loyaltyPlaceholder);
			} 
			//LOYALTY_LOGIN_URL
			else if(PlaceHolders.CAMPAIGN_PH_LOYALTY_LOGIN_URL.equalsIgnoreCase(placeholder)){
				loyaltyPlaceholder = getLoyaltyURL(contactsLoyalty,defVal);
				logger.info("LOYALTY_LOGIN_URL::"+loyaltyPlaceholder);
			}
			/*//ORGANIZATION_NAME
			else if(PlaceHolders.CAMPAIGN_PH_ORGANIZATION_NAME.equalsIgnoreCase(placeholder)){
				loyaltyPlaceholder = getUserOrganization(contactsLoyalty,defVal);
				logger.info("PH_ORGANIZATION_NAME::"+loyaltyPlaceholder);
			}*/
			//REWARD_EXPIRATION_PERIOD
			else if(PlaceHolders.CAMPAIGN_PH_LOYALTY_REWARD_EXPIRATION_PERIOD.equalsIgnoreCase(placeholder)){
				loyaltyPlaceholder = getRewardExpirationPeriod(contactsLoyalty ,defVal);
				logger.info("LOYALTY_REWARD_EXPIRATION_Period ::"+loyaltyPlaceholder);
			}
			//MEMBERSHIP_EXPIRATION_DATE
			else if(PlaceHolders.CAMPAIGN_PH_LOYALTY_MEMBERSHIP_EXPIRATION_DATE.equalsIgnoreCase(placeholder)){
				loyaltyPlaceholder =getLoyaltyMembershipExpirationDate(contactsLoyalty, defVal);	
				logger.info("MEMBERSHIP_EXPIRATION_DATE ::"+loyaltyPlaceholder);
			}
			//LOYALTY_GIFT_AMOUNT_EXPIRATION_PERIOD
			else if(PlaceHolders.CAMPAIGN_PH_LOYALTY_GIFT_AMOUNT_EXPIRATION_PERIOD.equalsIgnoreCase(placeholder)){
				loyaltyPlaceholder = getGiftAmountExpirationPeriod(contactsLoyalty,defVal);
				logger.info("LOYALTY_GIFT_AMOUNT_EXPIRATION_PERIOD :: "+loyaltyPlaceholder);
			}
			//LOYALTY_LAST_BONUS_VALUE
			else if(PlaceHolders.CAMPAIGN_PH_LOYALTY_LAST_BONUS_VALUE.equalsIgnoreCase(placeholder)){
				loyaltyPlaceholder = contactsLoyalty.getCardNumber() != null ? getLastBonusValue(contactsLoyalty.getLoyaltyId(),OCConstants.LOYALTY_TRANS_TYPE_BONUS,defVal,contactsLoyalty.getUserId()) : defVal;
				logger.info("LOYALTY_LAST_BONUS_VALUE :: "+loyaltyPlaceholder);
			}
			//REWARD_EXPIRING_VALUE
			else if(PlaceHolders.CAMPAIGN_PH_REWARD_EXPIRING_VALUE.equalsIgnoreCase(placeholder)){
				loyaltyPlaceholder = getRewardExpiringValue(contactsLoyalty,defVal);
				logger.info("REWARD_EXPIRING_VALUE :: "+loyaltyPlaceholder);
			}
			//GIFT_AMOUNT_EXPIRING_VALUE
			else if(PlaceHolders.CAMPAIGN_PH_GIFT_AMOUNT_EXPIRING_VALUE.equalsIgnoreCase(placeholder)){
				loyaltyPlaceholder = getGiftAmountExpiringValue(contactsLoyalty,defVal);
				logger.info("GIFT_AMOUNT_EXPIRING_VALUE :: "+loyaltyPlaceholder);
			}
			logger.info("Completed replace holder method");
			return loyaltyPlaceholder;
		}//replaceLoyaltyPlaceHolders
		
		/**
		 * This is to show gift amount expiry value
		 * @param contactsLoyalty
		 * @param defVal
		 * @return
		 */

		private String getGiftAmountExpiringValue(ContactsLoyalty contactsLoyalty,String defVal) {
			logger.info("--Start of getGiftAmountExpiringValue--");
			String giftExpValue = defVal;
			try {
				if(contactsLoyalty.getProgramId()== null) return giftExpValue;
				LoyaltyProgramDao loyaltyProgramDao = (LoyaltyProgramDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_DAO);
				LoyaltyProgram program = loyaltyProgramDao.findById(contactsLoyalty.getProgramId());

				if(OCConstants.FLAG_YES == program.getGiftAmountExpiryFlag() && program.getGiftAmountExpiryDateType() != null 
						&& program.getGiftAmountExpiryDateValue() != null){

					Calendar cal = Calendar.getInstance();
					if(OCConstants.LOYALTY_MEMBERSHIP_EXPIRYDATE_TYPE_MONTH.equals(program.getGiftAmountExpiryDateType())){
						cal.add(Calendar.MONTH, -(program.getGiftAmountExpiryDateValue().intValue()));
					}
					else if(OCConstants.LOYALTY_MEMBERSHIP_EXPIRYDATE_TYPE_YEAR.equals(program.getGiftAmountExpiryDateType())){
						cal.add(Calendar.YEAR, -(program.getGiftAmountExpiryDateValue().intValue()));
					}
					String expDate = "";
					if(cal.get(Calendar.MONTH) == 11) {
						expDate = cal.get(Calendar.YEAR)+"-12";
					} 
					else {
						cal.add(Calendar.MONTH, 1);
						expDate = cal.get(Calendar.YEAR)+"-"+cal.get(Calendar.MONTH);
					}
					logger.info("expDate = "+expDate);

					Object[] expiryValueArr = fetchExpiryValues(contactsLoyalty.getLoyaltyId(), expDate, OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_G);

					if(expiryValueArr != null && expiryValueArr[2] != null){
						DecimalFormat decimalFormat = new DecimalFormat("#0.00");
						double expGift = Double.valueOf(expiryValueArr[2].toString());
						if(expGift > 0){
							giftExpValue = decimalFormat.format(expGift);  
						}
					}
				}
			}
			catch(Exception e) {
				logger.error("Exception ::",e);
			}
			logger.info("--Exit of getGiftAmountExpiringValue--");
			return giftExpValue;
		}//getGiftAmountExpiringValue

		/**
		 * This is to show reward expiry value
		 * @param contactsLoyalty
		 * @param defVal
		 * @return
		 */
		private String getRewardExpiringValue(ContactsLoyalty contactsLoyalty,String defVal) {
			logger.info("--Start of getRewardExpiringValue--");
			String rewardExpVal = defVal ;
			try {
				if(contactsLoyalty.getProgramId()== null) return rewardExpVal;
				LoyaltyProgramDao loyaltyProgramDao = (LoyaltyProgramDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_DAO);
				LoyaltyProgram program = loyaltyProgramDao.findById(contactsLoyalty.getProgramId());

				if(contactsLoyalty.getProgramTierId()== null) return rewardExpVal;
				LoyaltyProgramTierDao loyaltyProgramTierDao = (LoyaltyProgramTierDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_TIER_DAO);
				LoyaltyProgramTier loyaltyProgramTier = loyaltyProgramTierDao.findByTierId(contactsLoyalty.getProgramTierId());

				if(OCConstants.FLAG_YES == program.getRewardExpiryFlag() && loyaltyProgramTier.getRewardExpiryDateType() != null 
						&& loyaltyProgramTier.getRewardExpiryDateValue() != null){

					Calendar cal = Calendar.getInstance();
					if(OCConstants.LOYALTY_MEMBERSHIP_EXPIRYDATE_TYPE_MONTH.equals(loyaltyProgramTier.getRewardExpiryDateType())){
						cal.add(Calendar.MONTH, -(loyaltyProgramTier.getRewardExpiryDateValue().intValue()));
					}
					else if(OCConstants.LOYALTY_MEMBERSHIP_EXPIRYDATE_TYPE_YEAR.equals(loyaltyProgramTier.getRewardExpiryDateType())){
						cal.add(Calendar.YEAR, -(loyaltyProgramTier.getRewardExpiryDateValue().intValue()));
					}

					String expDate = "";
					if(cal.get(Calendar.MONTH) == 11) {
						expDate = cal.get(Calendar.YEAR)+"-12";
					} 
					else {
						cal.add(Calendar.MONTH, 1);
						expDate = cal.get(Calendar.YEAR)+"-"+cal.get(Calendar.MONTH);
					}
					logger.info("expDate = "+expDate);
					Object[] expiryValueArr = fetchExpiryValues(contactsLoyalty.getLoyaltyId(), expDate, OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L);

					if(expiryValueArr != null ) { 
						DecimalFormat decimalFormat = new DecimalFormat("#0.00");
						if(expiryValueArr[1] != null && Long.valueOf(expiryValueArr[1].toString()) > 0 && expiryValueArr[2] != null
								&& Double.valueOf(expiryValueArr[2].toString()) >  0.0){
							rewardExpVal = Long.valueOf(expiryValueArr[1].toString())+" Points"+
														" & "+decimalFormat.format(Double.valueOf(expiryValueArr[2].toString()));
						}
						else if(expiryValueArr[1] != null && Long.valueOf(expiryValueArr[1].toString()) > 0 && (expiryValueArr[2] == null ||
								Double.valueOf(expiryValueArr[2].toString()) == 0.0)) {
							rewardExpVal = Long.valueOf(expiryValueArr[1].toString())+" Points";
						}
						else if(expiryValueArr[2] != null && Double.valueOf(expiryValueArr[2].toString()) >  0.0
								&& (expiryValueArr[1] == null || Long.valueOf(expiryValueArr[1].toString()) == 0)){
							rewardExpVal = decimalFormat.format(Double.valueOf(expiryValueArr[2].toString()));
						}
						else {
							rewardExpVal = defVal;
						}
					}
				}
			}
			catch(Exception e) {
				logger.error("Exception ::",e);
			}
			logger.info("--Exit of getRewardExpiringValue--");
			return rewardExpVal;
		}//getRewardExpiringValue

		/**
		 * To fetch expiry values
		 * @param cardNumber
		 * @param expDate
		 * @param rewardFlag
		 * @return
		 * @throws Exception
		 */
		private Object[] fetchExpiryValues(Long loyaltyId, String expDate, String rewardFlag) throws Exception {
			logger.info("--Start of fetchExpiryValues--");
			LoyaltyTransactionExpiryDao expiryDao = (LoyaltyTransactionExpiryDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_EXPIRY_DAO);
			logger.info("--Exit of fetchExpiryValues--");
			return expiryDao.fetchOnlyExpiryValues(loyaltyId, expDate, rewardFlag);
		}//fetchExpiryValues

		/**
		 * This is to show last bonus value earned
		 * @param cardNumber
		 * @param transactionType
		 * @param defVal
		 * @return
		 */
		private String getLastBonusValue(Long loyaltyId,String transactionType, String defVal,Long userId) {
			logger.info("--Start of getLastBonusValue--");
			String loyaltyPlaceholder = "";
			DecimalFormat decimalFormat = new DecimalFormat("#0.00");
			LoyaltyProgramService ltyPrgmService =  new LoyaltyProgramService();
			LoyaltyTransactionChild loyaltyTransactionChild = null;
			loyaltyTransactionChild = ltyPrgmService.getTransByMembershipNoAndTransType(loyaltyId, transactionType,userId);
			if(loyaltyTransactionChild != null){
				if(loyaltyTransactionChild.getEarnedAmount() != null && loyaltyTransactionChild.getEarnedPoints() != null){
					loyaltyPlaceholder = decimalFormat.format(loyaltyTransactionChild.getEarnedAmount())+" & "+loyaltyTransactionChild.getEarnedPoints().intValue()+" Points";
				}
				else if(loyaltyTransactionChild.getEarnedAmount() != null && loyaltyTransactionChild.getEarnedPoints() == null){
					loyaltyPlaceholder = decimalFormat.format(loyaltyTransactionChild.getEarnedAmount());
				}
				else if(loyaltyTransactionChild.getEarnedAmount() == null && loyaltyTransactionChild.getEarnedPoints() != null){
					loyaltyPlaceholder = loyaltyTransactionChild.getEarnedPoints().intValue()+" Points";
				}
				else{
					loyaltyPlaceholder = defVal;
				}
			}
			else{
				loyaltyPlaceholder = defVal;
			}
			logger.info("--Exit of getLastBonusValue--");
			return loyaltyPlaceholder;
		}//getLastBonusValue
		private String getresetPasswordURL(ContactsLoyalty contactsLoyalty,String defVal) {
			
			logger.debug(">>>>>>>>>>>>> entered in getLoyaltyURL");
			String loyaltyUrl = defVal;
			try {
			LoyaltyProgramService loyaltyProgramService =  new LoyaltyProgramService();
			UsersDao usersDao = (UsersDao) ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
			Users user = usersDao.find(contactsLoyalty.getUserId());
			LoyaltySettings loyaltySettings = loyaltyProgramService.findLoyaltySettingsByOrgId(user.getUserOrganization().getUserOrgId());

			if(loyaltySettings != null){
				loyaltyUrl = loyaltySettings.getUrlStr()+PropertyUtil.getPropertyValueFromDB("forgotPasswordLink");
				
					loyaltyUrl = "<a href="+loyaltyUrl+">"+PropertyUtil.getPropertyValueFromDB("forgotPasswordText")+"</a>";
				
			}
			logger.debug("<<<<<<<<<<<<< completed getLoyaltyURL ");
			}
			catch(Exception e) {
				logger.error("Exception ::",e);
			}
			return loyaltyUrl;
		}//getLoyaltyURL

		/**
		 * This method return's the decrypt password
		 * @param contactsLoyalty
		 * @param defVal
		 * @return password
		 */
		private String getMembershipPassword(ContactsLoyalty contactsLoyalty,String defVal) {
			logger.debug(">>>>>>>>>>>>> entered in getMembershipPassword");
			String password = defVal;
			if(!contactsLoyalty.getRewardFlag().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_G)) {
				password =getresetPasswordURL(contactsLoyalty, defVal);//contactsLoyalty.getMembershipPwd() != null ? EncryptDecryptLtyMembshpPwd.decrypt(contactsLoyalty.getMembershipPwd()) : defVal;
			}
			/*try {
					password = contactsLoyalty.getMembershipPwd() != null ? EncryptDecryptLtyMembshpPwd.decrypt( contactsLoyalty.getMembershipPwd()) : defVal;
			} catch (Exception e) {
				logger.error("Expection while replacing place holder :: ",e);
			}*/
			logger.debug("<<<<<<<<<<<<< completed getMembershipPassword ");
			return password;
		}//getMembershipPassword
		
		
		/**
		 * This method returns the Loyalty Url
		 * @param contactsLoyalty
		 * @param defVal
		 * @return loyaltyUrl
		 */
		private String getLoyaltyURL(ContactsLoyalty contactsLoyalty,String defVal) {
			
			logger.debug(">>>>>>>>>>>>> entered in getLoyaltyURL");
			String loyaltyUrl = defVal;
			try {
			LoyaltyProgramService loyaltyProgramService =  new LoyaltyProgramService();
			UsersDao usersDao = (UsersDao) ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
			Users user = usersDao.find(contactsLoyalty.getUserId());
			LoyaltySettings loyaltySettings = loyaltyProgramService.findLoyaltySettingsByOrgId(user.getUserOrganization().getUserOrgId());

			if(loyaltySettings != null){
				loyaltyUrl = loyaltySettings.getUrlStr();
				loyaltyUrl = "<a href="+loyaltyUrl+">"+loyaltyUrl+"</a>";
			}
			logger.debug("<<<<<<<<<<<<< completed getLoyaltyURL ");
			}
			catch(Exception e) {
				logger.error("Exception ::",e);
			}
			return loyaltyUrl;
		}//getLoyaltyURL
		
		/**
		 * This method get's user Organization.
		 * @param users 
		 * @param defVal 
		 * @return userOrganization
		 */
		private String getUserOrganization(Users users, String defVal) {
			logger.debug(">>>>>>>>>>>>> entered in getUserOrganization");
			String organizationName = defVal;
			try{
				organizationName = users.getUserOrganization().getOrganizationName();
			}catch (Exception e) {
				logger.error("Exception ::",e);
			}
			logger.debug("<<<<<<<<<<<<< completed getUserOrganization ");
			return organizationName;
		
		}//getUserOrganization
		

		/**
		 * This method gets gift Amount Expiration Period
		 * @param contactsLoyalty
		 * @param defVal
		 * @return
		 */
		private String getGiftAmountExpirationPeriod(ContactsLoyalty contactsLoyalty, String defVal) {
			logger.debug(">>>>>>>>>>>>> entered in getGiftAmountExpirationPeriod");
			String giftAmountExpirationPeriod = defVal;

			LoyaltyProgramService ltyPrgmService =  new LoyaltyProgramService();
			LoyaltyProgram loyaltyProgram =  null;

			if(contactsLoyalty.getProgramId() != null  && contactsLoyalty.getRewardFlag() != null){
				loyaltyProgram = ltyPrgmService.getProgmObj(contactsLoyalty.getProgramId());

				if(loyaltyProgram != null && loyaltyProgram.getGiftMembrshpExpiryFlag() == OCConstants.FLAG_YES){

					if(OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_G.equalsIgnoreCase(contactsLoyalty.getRewardFlag()) ||
							OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_GL.equalsIgnoreCase(contactsLoyalty.getRewardFlag())){

						if(loyaltyProgram.getGiftAmountExpiryDateValue() != null  && loyaltyProgram.getGiftAmountExpiryDateValue() != 0 
								&& loyaltyProgram.getGiftAmountExpiryDateType() != null && !loyaltyProgram.getGiftAmountExpiryDateType().isEmpty())
						{
							giftAmountExpirationPeriod = loyaltyProgram.getGiftAmountExpiryDateValue()+" "+loyaltyProgram.getGiftAmountExpiryDateType()+OCConstants.MORETHANONEOCCURENCE;
						}//if

					}//if oc 
				}//if lty !=null
			}//if cont
			logger.debug("<<<<<<<<<<<<< completed getGiftAmountExpirationPeriod ");
			return giftAmountExpirationPeriod;
		}//getGiftAmountExpirationPeriod
		
		
	   /* *//**
	     * This method gets  GiftCard  Amount Expiration Date
	     * @param contactsLoyalty
	     * @param defVal
	     * @return giftAmountExpriationDate
	     *//*
		private String getGiftCardExpirationDate(ContactsLoyalty contactsLoyalty, String defVal) {
			logger.debug(">>>>>>>>>>>>> entered in getLoyaltyGiftAmountExpriationDate");
			String giftAmountExpriationDate = defVal;

			//helper class obj
			LoyaltyProgramService ltyPrgmService =  new LoyaltyProgramService();
			LoyaltyProgramTier loyaltyProgramTier = null;
			LoyaltyProgram loyaltyProgram =  null;

			if(contactsLoyalty.getProgramId() != null && contactsLoyalty.getProgramTierId() != null && contactsLoyalty.getRewardFlag() != null){
				loyaltyProgram = ltyPrgmService.getProgmObj(contactsLoyalty.getProgramId());
				loyaltyProgramTier = ltyPrgmService.getTierObj(contactsLoyalty.getProgramTierId());

				if(loyaltyProgram != null && loyaltyProgramTier != null){

					if(OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_G.equalsIgnoreCase(contactsLoyalty.getRewardFlag())){

						if(loyaltyProgram.getGiftMembrshpExpiryFlag() == 'Y'){
							giftAmountExpriationDate = LoyaltyProgramHelper.getGiftMbrshipExpiryDate(contactsLoyalty.getCreatedDate(), 
									loyaltyProgram.getGiftMembrshpExpiryDateType(), loyaltyProgram.getGiftMembrshpExpiryDateValue());
						}//if 

					}//
				}//loyaltyProgram && loyaltyProgramTier 

			}
			logger.debug("<<<<<<<<<<<<< completed getLoyaltyGiftAmountExpriationDate ");
			return giftAmountExpriationDate;
		}//getLoyaltyGiftAmountExpriationDate
*/		
		/**
		 * This method get's Loyalty Membership Expiration Date
		 * @param contactsLoyalty
		 * @param defVal
		 * @return membershipExpriationDate
		 */
		private String getLoyaltyMembershipExpirationDate(ContactsLoyalty contactsLoyalty, String defVal) {
			logger.debug(">>>>>>>>>>>>> entered in getLoyaltyMembershipExpriationDate");
			String membershipExpriationDate = defVal;
			LoyaltyProgramService ltyPrgmService =  new LoyaltyProgramService();
			LoyaltyProgramTier loyaltyProgramTier = null;
			LoyaltyProgram loyaltyProgram =  null;

			if(contactsLoyalty.getProgramId() != null && contactsLoyalty.getProgramTierId() != null && contactsLoyalty.getRewardFlag() != null){
				loyaltyProgram = ltyPrgmService.getProgmObj(contactsLoyalty.getProgramId());
				loyaltyProgramTier = ltyPrgmService.getTierObj(contactsLoyalty.getProgramTierId());

				if(loyaltyProgram != null && loyaltyProgramTier != null){

					if(OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L.equalsIgnoreCase(contactsLoyalty.getRewardFlag()) || OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_GL.equalsIgnoreCase(contactsLoyalty.getRewardFlag())){
						////if flag L or GL
						if(loyaltyProgram.getMembershipExpiryFlag() == 'Y' && loyaltyProgramTier.getMembershipExpiryDateType() != null 
								&& loyaltyProgramTier.getMembershipExpiryDateValue() != null){

							boolean upgdReset = loyaltyProgram.getMbrshipExpiryOnLevelUpgdFlag() == 'Y' ? true : false;
							
							membershipExpriationDate = LoyaltyProgramHelper.getMbrshipExpiryDate(contactsLoyalty.getCreatedDate(), contactsLoyalty.getTierUpgradedDate(), 
									upgdReset, loyaltyProgramTier.getMembershipExpiryDateType(), loyaltyProgramTier.getMembershipExpiryDateValue());
						}
					}//if

				}//loyaltyProgram && loyaltyProgramTier 
			}
			logger.debug("<<<<<<<<<<<<< completed getLoyaltyMembershipExpriationDate ");
			return membershipExpriationDate;
		}//getLoyaltyMembershipExpriationDate

		/**
		 * This method get's Reward Expiration Period
		 * @param contactsLoyalty
		 * @param defVal
		 * @return
		 */
		private String getRewardExpirationPeriod(ContactsLoyalty contactsLoyalty, String defVal) {
			logger.debug(">>>>>>>>>>>>> entered in getRewardExpirationPeriod");
			String rewardExpirationPeriod = defVal;

			Long tierId =  contactsLoyalty.getProgramTierId();

			if(tierId != null){
				LoyaltyProgramService ltyPrgmService =  new LoyaltyProgramService();
				LoyaltyProgramTier loyaltyProgramTier = null;
				LoyaltyProgram loyaltyProgram =  null;

				if(contactsLoyalty.getProgramId() != null  && contactsLoyalty.getRewardFlag() != null){
					loyaltyProgram = ltyPrgmService.getProgmObj(contactsLoyalty.getProgramId());
					loyaltyProgramTier = ltyPrgmService.getTierObj(contactsLoyalty.getProgramTierId());

					if(loyaltyProgram != null && loyaltyProgramTier != null && loyaltyProgram.getRewardExpiryFlag()==OCConstants.FLAG_YES){

						if(OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L.equalsIgnoreCase(contactsLoyalty.getRewardFlag()) || OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_GL.equalsIgnoreCase(contactsLoyalty.getRewardFlag())){

							if(loyaltyProgramTier != null && loyaltyProgramTier.getRewardExpiryDateValue() != null 
									&& loyaltyProgramTier.getRewardExpiryDateValue() != 0 
									&& loyaltyProgramTier.getRewardExpiryDateType() != null 
									&& !loyaltyProgramTier.getRewardExpiryDateType().isEmpty())
							{
								rewardExpirationPeriod = loyaltyProgramTier.getRewardExpiryDateValue()+" "
										+loyaltyProgramTier.getRewardExpiryDateType()+OCConstants.MORETHANONEOCCURENCE;
							}//if

						}//if oc 
					}//if lty !=null
				}//if cont
			}//tier id
			logger.debug("<<<<<<<<<<<<< completed getRewardExpirationPeriod ");
			return rewardExpirationPeriod;
		}//getRewardExpirationPeriod


		
		
		
		
		/**
		 * This method fetch the place holder for MemberTier
		 * @param programTierId
		 * @return MemberTier
		 */
		private String getMemberTier(Long programTierId,String defValue) {

			LoyaltyProgramTier loyaltyProgramTier = null;
			//helper class obj
			LoyaltyProgramService ltyPrgmService =  new LoyaltyProgramService();
			String tier = "" ,level ="",loyaltyPlaceholder="";

			loyaltyProgramTier = ltyPrgmService.getTierObj(programTierId);
			if(loyaltyProgramTier != null){
				tier = loyaltyProgramTier.getTierName() ;
				level = " ( Level : "+(loyaltyProgramTier.getTierType() == null ? "" : loyaltyProgramTier.getTierType())+" )";
				loyaltyPlaceholder = tier + level ; //it will tier name + level
			}
			else{
				loyaltyPlaceholder = defValue; //default value to be replaced
			}
			return loyaltyPlaceholder;
		}//getMemberTier

		/**
		 * This method calculate the Membership Expiration
		 * @param programId
		 * @param programTierId
		 * @param rewardFlag
		 * @return ExpirationDate
		 */
		private String getMemberShipExpirationDate(ContactsLoyalty contactsLoyalty ,String defValue) {
			//helper class obj
			LoyaltyProgramService ltyPrgmService =  new LoyaltyProgramService();
			LoyaltyProgramTier loyaltyProgramTier = null;
			LoyaltyProgram loyaltyProgram =  null;
			String loyaltyPlaceholder ="";

			if(contactsLoyalty.getProgramId() != null && contactsLoyalty.getProgramTierId() != null && contactsLoyalty.getRewardFlag() != null){
				loyaltyProgram = ltyPrgmService.getProgmObj(contactsLoyalty.getProgramId());
				loyaltyProgramTier = ltyPrgmService.getTierObj(contactsLoyalty.getProgramTierId());

				if(loyaltyProgram != null && loyaltyProgramTier != null){

					if(OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L.equalsIgnoreCase(contactsLoyalty.getRewardFlag()) || OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_GL.equalsIgnoreCase(contactsLoyalty.getRewardFlag())){

						if(loyaltyProgram.getMembershipExpiryFlag() == 'Y' && loyaltyProgramTier.getMembershipExpiryDateType() != null 
								&& loyaltyProgramTier.getMembershipExpiryDateValue() != null){

							loyaltyPlaceholder = LoyaltyProgramHelper.getMbrshipExpiryDate(contactsLoyalty.getCreatedDate(), contactsLoyalty.getTierUpgradedDate(), 
									false, loyaltyProgramTier.getMembershipExpiryDateType(), loyaltyProgramTier.getMembershipExpiryDateValue());
						}//if flag L or GL
					}//if
					else if(OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_G.equalsIgnoreCase(contactsLoyalty.getRewardFlag())){

						if(loyaltyProgram.getGiftMembrshpExpiryFlag() == 'Y'){
							loyaltyPlaceholder = LoyaltyProgramHelper.getGiftMbrshipExpiryDate(contactsLoyalty.getCreatedDate(), 
									loyaltyProgram.getGiftMembrshpExpiryDateType(), loyaltyProgram.getGiftMembrshpExpiryDateValue());
						}//if 

					}//else if flag G
				}//loyaltyProgram && loyaltyProgramTier 
				else{
					loyaltyPlaceholder = defValue ;
				}

			}// if programId, progTierId
			else{
				loyaltyPlaceholder = defValue ;
			}

			return loyaltyPlaceholder;
		}//getMemberShipExpirationDate


		/**
		 * This method return Enrollment source
		 * @param loyaltyType
		 * @param defVal
		 * @return EnrollmentSource
		 */
		private String getEnrollmentSource(String loyaltyType, String defVal) {
			String loyaltyPH = "";
			if(Constants.CONTACT_LOYALTY_TYPE_POS.equalsIgnoreCase(loyaltyType)) {
				loyaltyPH = Constants.CONTACT_LOYALTY_TYPE_STORE;
			}
			else {
				loyaltyPH = loyaltyType;
			}
			return loyaltyPH;
		}//getEnrollmentSource	

		  /**
	     * This method gets  Gift-Card   Expiration Date
	     * @param contactsLoyalty
	     * @param defVal
	     * @return getGiftCardExpirationDate
	     */
		private String getGiftCardExpirationDate(ContactsLoyalty contactsLoyalty, String defVal) {
			logger.debug(">>>>>>>>>>>>> entered in getGiftCardExpirationDate");
			String giftCardExpriationDate = defVal;

			LoyaltyProgramService ltyPrgmService =  new LoyaltyProgramService();
			LoyaltyProgram loyaltyProgram =  null;

			if(contactsLoyalty.getProgramId() != null){
				loyaltyProgram = ltyPrgmService.getProgmObj(contactsLoyalty.getProgramId());
				if(loyaltyProgram != null && contactsLoyalty.getRewardFlag() != null && OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_G.equalsIgnoreCase(contactsLoyalty.getRewardFlag())){
					if(loyaltyProgram.getGiftMembrshpExpiryFlag() == 'Y' && loyaltyProgram.getGiftMembrshpExpiryDateType() != null 
							&& loyaltyProgram.getGiftMembrshpExpiryDateValue() != null){
						giftCardExpriationDate = LoyaltyProgramHelper.getGiftMbrshipExpiryDate(contactsLoyalty.getCreatedDate(), 
								loyaltyProgram.getGiftMembrshpExpiryDateType(), loyaltyProgram.getGiftMembrshpExpiryDateValue());
					}//if 
				}
			}
			logger.debug("<<<<<<<<<<<<< completed getGiftCardExpirationDate ");
			return giftCardExpriationDate;
		}//getGiftCardExpirationDate


		/**
		 * This method fetch Hold Bal & points
		 * @param contactsLoyalty
		 * @param defVal
		 * @return Hold Balance or Points
		 */

		private String getHoldBalance(ContactsLoyalty contactsLoyalty,String defVal) {
			DecimalFormat decimalFormat = new DecimalFormat("#0.00");
			String loyaltyPlaceholder ="";
			if(contactsLoyalty.getHoldAmountBalance() != null && contactsLoyalty.getHoldPointsBalance() != null&& contactsLoyalty.getMembershipStatus().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_STATUS_ACTIVE)){
				loyaltyPlaceholder = decimalFormat.format(contactsLoyalty.getHoldAmountBalance()) +" & "+contactsLoyalty.getHoldPointsBalance().intValue()+ " Points";;
			}
			else if(contactsLoyalty.getHoldAmountBalance() != null && contactsLoyalty.getHoldPointsBalance() == null&& contactsLoyalty.getMembershipStatus().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_STATUS_ACTIVE)){
				loyaltyPlaceholder = decimalFormat.format(contactsLoyalty.getHoldAmountBalance());// +" & "+contactsLoyalty.getHoldPointsBalance().intValue();
			}
			else if(contactsLoyalty.getHoldAmountBalance() == null && contactsLoyalty.getHoldPointsBalance() != null&& contactsLoyalty.getMembershipStatus().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_STATUS_ACTIVE)){
				loyaltyPlaceholder = contactsLoyalty.getHoldPointsBalance().intValue() + " Points";
			}
			else{
				loyaltyPlaceholder =  defVal;
			}
			return loyaltyPlaceholder;
		}//getHoldBalance

		/**
		 * This method fetch the Reward Activation Period
		 * @param programTierId
		 * @param defValue
		 * @return Reward Activation Period
		 */

		private String getRewardActivationPeriod(Long programTierId,String defValue) {

			LoyaltyProgramTier loyaltyProgramTier = null;
			String loyaltyPlaceholder = defValue;
			//helper class obj
			LoyaltyProgramService ltyPrgmService =  new LoyaltyProgramService();
			loyaltyProgramTier = ltyPrgmService.getTierObj(programTierId);
			if(loyaltyProgramTier != null && loyaltyProgramTier.getActivationFlag() == OCConstants.FLAG_YES){
				loyaltyPlaceholder = loyaltyProgramTier.getPtsActiveDateValue() +" "+ loyaltyProgramTier.getPtsActiveDateType()+OCConstants.MORETHANONEOCCURENCE;
			}
			else{
				loyaltyPlaceholder = defValue ;
			}
			return loyaltyPlaceholder;
		}//getRewardActivationPeriod

		/**
		 * This method calculate Last Earned Value
		 * @param cardNumber
		 * @param loyaltyTransTypeIssuance
		 * @return Last Earned Value
		 */
		private String getLastEarnedValue(Long loyaltyId,String loyaltyTransTypeIssuance,String defValue,Long userId) {
			String loyaltyPlaceholder = "";
			DecimalFormat decimalFormat = new DecimalFormat("#0.00");
			//helper class obj
			LoyaltyProgramService ltyPrgmService =  new LoyaltyProgramService();
			LoyaltyTransactionChild child = null;
			child = ltyPrgmService.getTransByMembershipNoAndTransType(loyaltyId, loyaltyTransTypeIssuance,userId);
			if(child != null){
				if(child.getEarnedAmount() != null ){
					loyaltyPlaceholder = decimalFormat.format(child.getEarnedAmount()); //+ " "+child.getValueCode();//+" & "+child.getEarnedPoints().intValue()+"";
				}
				if(child.getEarnedPoints() != null) {
					loyaltyPlaceholder += !loyaltyPlaceholder.isEmpty() ? "&"  :"";
					loyaltyPlaceholder = child.getEarnedPoints().intValue()+"";//+" "+child.getValueCode();;
				}
				/*if(child.getEarnedReward() != null) {
					loyaltyPlaceholder += !loyaltyPlaceholder.isEmpty() ? "&"  :"";
					loyaltyPlaceholder = child.getEarnedReward().intValue()+""+(child.getValueCode() != null ?  child.getValueCode() :"");//" "+child.getValueCode();;
				}*/
				//loyaltyPlaceholder += !loyaltyPlaceholder.isEmpty() ? " "+child.getValueCode()  :"";
				/*
				 * else if(child.getEarnedAmount() != null && child.getEarnedPoints() == null){
				 * loyaltyPlaceholder = decimalFormat.format(child.getEarnedAmount()); } else
				 * if(child.getEarnedAmount() == null && child.getEarnedPoints() != null){
				 * loyaltyPlaceholder = child.getEarnedPoints().intValue()+""; } 
				 */
				if(loyaltyPlaceholder.isEmpty()){
					loyaltyPlaceholder = defValue;
				}
			}
			else{
				loyaltyPlaceholder = defValue;
			}
			return loyaltyPlaceholder;
		}//getLastEarnedValue

		/**
		 * This method calculate Last Redeemed Value
		 * @param cardNumber
		 * @param loyaltyTransTypeIssuance
		 * @return Last Redeemed Value
		 */
		private String getLastRedeemedValue(Long loyaltyId,String loyaltyTransTypeRedemption,String defValue,Long userId) {
			String loyaltyPlaceholder = "";
			DecimalFormat decimalFormat = new DecimalFormat("#0.00");
			//helper class obj
			LoyaltyProgramService ltyPrgmService =  new LoyaltyProgramService();
			LoyaltyTransactionChild child = null;
			child = ltyPrgmService.getTransByMembershipNoAndTransType(loyaltyId, loyaltyTransTypeRedemption,userId);
			if(child != null){
				if(child.getEnteredAmount() != null && child.getEnteredAmountType().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_POINTSREDEEM)){
					loyaltyPlaceholder = child.getEnteredAmount().intValue()+" Points";
				}
				else if(child.getEnteredAmount() != null && child.getEnteredAmountType().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_AMOUNTREDEEM)){
					loyaltyPlaceholder = decimalFormat.format(child.getEnteredAmount());
				}
				else{
					loyaltyPlaceholder = defValue;
				}
			}
			else{
				loyaltyPlaceholder = defValue;
			}
			return loyaltyPlaceholder;

		}//getLastRedeemedValue

		private String getlastpurchasePlaceHolders(Contacts contact, String placeholder,  Users users,String defVal ) throws Exception {
			String value = "";
			RetailProSalesDao retailProSalesDao = (RetailProSalesDao)ServiceLocator.getInstance().getDAOByName("retailProSalesDao");
			RetailProSalesCSV lastSaleRecord = retailProSalesDao.findLastpurchaseRecord(contact.getContactId(), contact.getUsers().getUserId());//(contact.getExternalId(),contact.getUsers().getUserId());
			logger.info("lastsalerecord"+lastSaleRecord);
			if(lastSaleRecord == null) return defVal;
			logger.debug("placeholders***************************"+placeholder);
			if(placeholder.equals(PlaceHolders.CAMPAIGN_PH_LASTPURCHASE_STOREADDRESS)) {
				////logger.debug("orgid"+orgId);
				if(users.getUserOrganization().getUserOrgId() != null) {
					////logger.debug("last purchase store address >>>>>>>>>>>>>>>>>>>>>.");
					value = getLastPurchaseStoreAddr(contact, users,defVal);
					logger.debug("value 1***************************"+value);
				}

			}else if(placeholder.equals(PlaceHolders.CAMPAIGN_PH_LASTPURCHASE_DATE)) {
				////logger.debug("last purchase date>>>>>>>>>>>>>>>>>>");
				value = getContactLastPurchasedDate(contact,defVal);
				logger.debug("value 2***************************"+value);
			}
			
			else if(placeholder.toLowerCase().endsWith(PlaceHolders.CAMPAIGN_PH_LASTPURCHASE_AMOUNT.toLowerCase())) {
				
				
			
							Double lastPurchaseDetails = lastSaleRecord.getSalesPrice();
							if(lastPurchaseDetails != null){
									value = lastPurchaseDetails.toString();
									logger.info("CAMPAIGN_PH_LASTPURCHASE_AMOUNT value1"+lastPurchaseDetails);
							}
							else {
								value=defVal;
							}
						
					}
			return value;

		}


		private String getLastPurchaseStoreAddr(Contacts contact, Users users,String defVal) {
			//need to get the contact last puchased store address
			//RetailProSalesDao retailProSalesDao = (RetailProSalesDao)context.getBean("retailProSalesDao");
			//OrganizationStoresDao organizationStoresDao = (OrganizationStoresDao)context.getBean("organizationStoresDao");
			RetailProSalesDao retailProSalesDao = null;
			OrganizationStoresDao organizationStoresDao = null;
			try{
				retailProSalesDao = (RetailProSalesDao)ServiceLocator.getInstance().getDAOByName(OCConstants.RETAILPRO_SALES_DAO);
				organizationStoresDao = (OrganizationStoresDao)ServiceLocator.getInstance().getDAOByName(OCConstants.ORGANIZATION_STORES_DAO);
			}catch(Exception e){
				logger.error("Exception in getting dao.", e);
			}


			String storeAddress =  null;
			Long orgId= users.getUserOrganization().getUserOrgId();
			if(contact.getContactId() != null) {


				//MailingList mailingList = contact.getMailingListByType(Constants.CONTACT_OPTIN_MEDIUM_POS);

				String storeNum = retailProSalesDao.findLastpurchasedStore(contact.getContactId(),contact.getUsers().getUserId());
				if(storeNum != null) {
					OrganizationStores organizationStores = organizationStoresDao.findByStoreLocationId(orgId, storeNum);

					if(organizationStores == null) {
						storeAddress=defVal;


					}else {
						//String pattern = "[;=;]+";
						//logger.debug("entered into organization stores=====================");
						if(storeAddress == null) storeAddress = Constants.STRING_NILL;
						String strAddr[] = organizationStores.getAddressStr().split(Constants.ADDR_COL_DELIMETER);
						int count = 0;
						for(String str : strAddr){
							count++;

							if(count == 7 && storeAddress.length()>0 && str.trim().length()>0){
								storeAddress = storeAddress+" | Phone: "+str;
							}
							else if(storeAddress.length()==0 && str.trim().length()>0){
								storeAddress = storeAddress+str;
							}
							else if(storeAddress.length()>0 && str.trim().length()>0){
								storeAddress = storeAddress+", "+str;
							}
						}

						//storeAddress = organizationStores.getAddressStr().replace(Constants.ADDR_COL_DELIMETER, " | ");
						//storeAddress = organizationStores.getAddressStr().replaceAll(pattern, " | ");
					}
				}
				else {
					storeAddress= defVal;
				}
			}

			return storeAddress != null && !storeAddress.trim().isEmpty() ? storeAddress : defVal;
		}//getLastPurchaseStoreAddr();


		private String getContactLastPurchasedDate(Contacts contact,String defVal) {

			String date = defVal;
			//MailingList mailingList = contact.getMailingListByType(Constants.CONTACT_OPTIN_MEDIUM_POS);
			RetailProSalesDao retailProSalesDao = null;
			try{
				retailProSalesDao = (RetailProSalesDao)ServiceLocator.getInstance().getDAOByName(OCConstants.RETAILPRO_SALES_DAO);
			}catch(Exception e){
				logger.error("Exception in getting dao.", e);
			}
			if(contact.getContactId() != null) {
				Calendar Mycalender = retailProSalesDao.findLastpurchasedDate(contact.getContactId(),contact.getUsers().getUserId());
				if(Mycalender != null){
					date = MyCalendar.calendarToString(Mycalender, MyCalendar.FORMAT_DATEONLY_GENERAL);
				}
				else {
					date=defVal;
				}
			}

			return date== null || date.trim().isEmpty() ? defVal : date;
		}//getContactLastPurchasedDate();

		private Map replaceDateTypePhValue( String templateContentStr, Set<String> phSet, StringBuffer previwPhKeyValSb) {
			Map<String, Object> phContentValMap= new HashMap<String, Object>();
			Iterator<String> phItr = phSet.iterator();

			while (phItr.hasNext()) {
				String phKeyStr = (String) phItr.next();

				if(!phKeyStr.startsWith(Constants.DATE_PH_DATE_)) continue;

				String valueStr = "";

				if(phKeyStr.startsWith(Constants.DATE_PH_DATE_)){

					if(phKeyStr.equalsIgnoreCase(Constants.DATE_PH_DATE_today)){
						Calendar cal = MyCalendar.getNewCalendar();
						valueStr = MyCalendar.calendarToString(cal, MyCalendar.FORMAT_DATEONLY_COMPLETE_MONTH);
					}
					else if(phKeyStr.equalsIgnoreCase(Constants.DATE_PH_DATE_tomorrow)){
						Calendar cal = MyCalendar.getNewCalendar();
						cal.set(Calendar.DATE, cal.get(Calendar.DATE)+1);
						valueStr = MyCalendar.calendarToString(cal, MyCalendar.FORMAT_DATEONLY_COMPLETE_MONTH);

					}
					else if(phKeyStr.endsWith(Constants.DATE_PH_DAYS)){

						try {
							String[] days = phKeyStr.split("_");
							Calendar cal = MyCalendar.getNewCalendar();
							cal.set(Calendar.DATE, cal.get(Calendar.DATE)+Integer.parseInt(days[1].trim()));
							valueStr = MyCalendar.calendarToString(cal, MyCalendar.FORMAT_DATEONLY);

						} catch (Exception e) {
							//logger.debug("exception in parsing date placeholder");
							//logger.debug("valueStr>>>>>>>>"+valueStr);
							valueStr ="";

						}
					}

				}

				if(valueStr == null || valueStr.trim().length() == 0) valueStr ="";

				templateContentStr = templateContentStr.replace("["+phKeyStr+"]", valueStr);

				//logger.debug("key >>>>"+phKeyStr+" >>> value is::"+valueStr);
				if(previwPhKeyValSb.length() > 0) previwPhKeyValSb.append(Constants.ADDR_COL_DELIMETER);
				previwPhKeyValSb.append("[" + phKeyStr + "]" + Constants.DELIMETER_DOUBLECOLON + valueStr);

				phItr.remove();
			}
			phContentValMap.put("1", templateContentStr);
			phContentValMap.put("2", phSet);
			phContentValMap.put("3", previwPhKeyValSb);

			return phContentValMap;
		}

		private String getConatctCustFields(Contacts contact , int index, String datatType, String defVal) throws Exception {

			String udfDataStr = defVal;
			Calendar cal = null;
			boolean isDateType =false;

			if(datatType.startsWith("Date")) {
				datatType = datatType.substring(datatType.indexOf("(")+1, datatType.indexOf(")"));
				datatType = MyCalendar.dateFormatMap.get(datatType);
				cal = Calendar.getInstance();
				isDateType = true;
			}

			switch(index){
			case 1: {
				if(contact.getUdf1() == null) return defVal;
				if(isDateType) {
					cal.setTime(new SimpleDateFormat(datatType).parse(contact.getUdf1()));
					return udfDataStr = cal == null ? "":  MyCalendar.calendarToString(cal, MyCalendar.FORMAT_DATEONLY_GENERAL);
				}else return contact.getUdf1();
			}
			case 2: {
				if(contact.getUdf2() == null) return defVal;
				if(isDateType) {
					cal.setTime(new SimpleDateFormat(datatType).parse(contact.getUdf2()));
					return udfDataStr = cal == null ? "":  MyCalendar.calendarToString(cal, MyCalendar.FORMAT_DATEONLY_GENERAL);
				}else return contact.getUdf2();
			}
			case 3: {
				if(contact.getUdf3() == null) return defVal;
				if(isDateType) {
					cal.setTime(new SimpleDateFormat(datatType).parse(contact.getUdf3()));
					return udfDataStr = cal == null ? "":  MyCalendar.calendarToString(cal, MyCalendar.FORMAT_DATEONLY_GENERAL);
				}else return contact.getUdf3();
			}
			case 4: {
				if(contact.getUdf4() == null) return defVal;
				if(isDateType) {
					cal.setTime(new SimpleDateFormat(datatType).parse(contact.getUdf4()));
					return udfDataStr = cal == null ? "":  MyCalendar.calendarToString(cal, MyCalendar.FORMAT_DATEONLY_GENERAL);
				}else return contact.getUdf4();
			}
			case 5: {
				if(contact.getUdf5() == null) return defVal;
				if(isDateType) {
					cal.setTime(new SimpleDateFormat(datatType).parse(contact.getUdf5()));
					return udfDataStr = cal == null ? "":  MyCalendar.calendarToString(cal, MyCalendar.FORMAT_DATEONLY_GENERAL);
				}else return contact.getUdf5();
			}
			case 6: {
				if(contact.getUdf6() == null) return defVal;
				if(isDateType) {
					cal.setTime(new SimpleDateFormat(datatType).parse(contact.getUdf6()));
					return udfDataStr = cal == null ? "":  MyCalendar.calendarToString(cal, MyCalendar.FORMAT_DATEONLY_GENERAL);
				}else return contact.getUdf6();
			}
			case 7: {
				if(contact.getUdf7() == null) return defVal;
				if(isDateType) {
					cal.setTime(new SimpleDateFormat(datatType).parse(contact.getUdf7()));
					return udfDataStr = cal == null ? "":  MyCalendar.calendarToString(cal, MyCalendar.FORMAT_DATEONLY_GENERAL);
				}else return contact.getUdf7();
			}
			case 8: {
				if(contact.getUdf8() == null) return defVal;
				if(isDateType) {
					cal.setTime(new SimpleDateFormat(datatType).parse(contact.getUdf8()));
					return udfDataStr = cal == null ? "":  MyCalendar.calendarToString(cal, MyCalendar.FORMAT_DATEONLY_GENERAL);
				}else return contact.getUdf8();
			}
			case 9: {
				if(contact.getUdf9() == null) return defVal;
				if(isDateType) {
					cal.setTime(new SimpleDateFormat(datatType).parse(contact.getUdf9()));
					return udfDataStr = cal == null ? "":  MyCalendar.calendarToString(cal, MyCalendar.FORMAT_DATEONLY_GENERAL);
				}else return contact.getUdf9();
			}
			case 10: {
				if(contact.getUdf10() == null) return defVal;
				if(isDateType) {
					cal.setTime(new SimpleDateFormat(datatType).parse(contact.getUdf10()));
					return udfDataStr = cal == null ? "":  MyCalendar.calendarToString(cal, MyCalendar.FORMAT_DATEONLY_GENERAL);
				}else return contact.getUdf10();
			}
			case 11: {
				if(contact.getUdf11() == null) return defVal;
				if(isDateType) {
					cal.setTime(new SimpleDateFormat(datatType).parse(contact.getUdf11()));
					return udfDataStr = cal == null ? "":  MyCalendar.calendarToString(cal, MyCalendar.FORMAT_DATEONLY_GENERAL);
				}else return contact.getUdf11();
			}
			case 12: {
				if(contact.getUdf12() == null) return defVal;
				if(isDateType) {
					cal.setTime(new SimpleDateFormat(datatType).parse(contact.getUdf12()));
					return udfDataStr = cal == null ? "":  MyCalendar.calendarToString(cal, MyCalendar.FORMAT_DATEONLY_GENERAL);
				}else return contact.getUdf12();
			}
			case 13: {
				if(contact.getUdf13() == null) return defVal;
				if(isDateType) {
					cal.setTime(new SimpleDateFormat(datatType).parse(contact.getUdf13()));
					return udfDataStr = cal == null ? "":  MyCalendar.calendarToString(cal, MyCalendar.FORMAT_DATEONLY_GENERAL);
				}else return contact.getUdf13();
			}
			case 14: {
				if(contact.getUdf14() == null) return defVal;
				if(isDateType) {
					cal.setTime(new SimpleDateFormat(datatType).parse(contact.getUdf14()));
					return udfDataStr = cal == null ? "":  MyCalendar.calendarToString(cal, MyCalendar.FORMAT_DATEONLY_GENERAL);
				}else return contact.getUdf14();
			}
			case 15: {
				if(contact.getUdf15() == null) return defVal;
				if(isDateType) {
					cal.setTime(new SimpleDateFormat(datatType).parse(contact.getUdf15()));
					return udfDataStr = cal == null ? "":  MyCalendar.calendarToString(cal, MyCalendar.FORMAT_DATEONLY_GENERAL);
				}else return contact.getUdf15();
			}

			}
			return udfDataStr;
		} // getConatctCustFields

		

/*
		*//**
		 * This method replace loyalty place holders
		 * @param placeholder
		 * @param contactsLoyalty
		 * @param defVal
		 * @return Loyalty Place Holder
		 *//*
		private String replaceLoyaltyPlaceHolders(String placeholder,ContactsLoyalty contactsLoyalty,String defVal) {
	    	
	    	if(contactsLoyalty == null){
	    		return defVal;
	    	}
	    	if(placeholder == null  && defVal == null){
	    		logger.error("Value is null placeholder :: "+placeholder+"\t contactsLoyalty "+contactsLoyalty+"\t defVal"+defVal);
	    		return defVal;
	    	}
	    	
	    	logger.info("In replaceLoyaltyPlaceHolders :: " +  placeholder);
	    	DecimalFormat decimalFormat = new DecimalFormat("#0.00");
	    	String loyaltyPlaceholder="";
	    	//LOYALTYCARDPIN
	    	if(PlaceHolders.CAMPAIGN_PH_LOYALTY_MEMBERSHIP_PIN.equalsIgnoreCase(placeholder)){
	    			loyaltyPlaceholder = contactsLoyalty.getCardPin()!= null ? contactsLoyalty.getCardPin(): defVal;
	    	}//REFRESHEDON
	    	else if(PlaceHolders.CAMPAIGN_PH_LOYALTY_REFRESHEDON.equalsIgnoreCase(placeholder) ){
	    		loyaltyPlaceholder = contactsLoyalty.getLastFechedDate() ==  null ? defVal :  MyCalendar.calendarToString(contactsLoyalty.getLastFechedDate(), MyCalendar.FORMAT_DATEONLY_GENERAL);
	       	}//MEMBERSHIP_NUMBER
	    	else if(PlaceHolders.CAMPAIGN_PH_LOYALTY_MEMBERSHIP_NUMBER.equalsIgnoreCase(placeholder)){
	    		loyaltyPlaceholder = contactsLoyalty.getCardNumber() != null ? contactsLoyalty.getCardNumber()+"":defVal;
	    		logger.info("Membership Number ::"+contactsLoyalty.getCardNumber());
	    	}//MEMBER_TIER
	    	else if(PlaceHolders.CAMPAIGN_PH_LOYALTY_MEMBER_TIER.equalsIgnoreCase(placeholder)){
	    		loyaltyPlaceholder = contactsLoyalty.getProgramTierId() != null ? getMemberTier(contactsLoyalty.getProgramTierId() , defVal) : defVal; 
	    		logger.info("Member Tier ::"+loyaltyPlaceholder);
	    	}//MEMBER_STATUS
	    	else if(PlaceHolders.CAMPAIGN_PH_LOYALTY_MEMBER_STATUS.equalsIgnoreCase(placeholder)){
	    		logger.info("MEMBER_STATUS contactsLoyalty.getMembershipStatus() ::"+contactsLoyalty.getMembershipStatus());
	    		loyaltyPlaceholder = contactsLoyalty.getMembershipStatus() != null ? contactsLoyalty.getMembershipStatus() : defVal;
	    		logger.info("MEMBER_STATUS ::"+loyaltyPlaceholder);
	    	}//MEMBERSHIP_EXPIRATION_DATE
	    	else if(PlaceHolders.CAMPAIGN_PH_LOYALTY_MEMBERSHIP_EXPIRATION_DATE.equalsIgnoreCase(placeholder)){
	    		loyaltyPlaceholder = getMemberShipExpirationDate(contactsLoyalty , defVal);	
	    		logger.info("MEMBERSHIP_EXPIRATION_DATE ::"+loyaltyPlaceholder);
	    	}//LOYALTY_ENROLLMENT_DATE
	    	else if(PlaceHolders.CAMPAIGN_PH_LOYALTY_ENROLLMENT_DATE.equalsIgnoreCase(placeholder)){
	    		loyaltyPlaceholder = contactsLoyalty.getCreatedDate() != null ?  MyCalendar.calendarToString(contactsLoyalty.getCreatedDate(), MyCalendar.FORMAT_DATETIME_STYEAR) : defVal ;
	    		logger.info("LOYALTY_ENROLLMENT_DATE ::"+loyaltyPlaceholder);
	    	}//LOYALTY_ENROLLMENT_SOURCE
	    	else if(PlaceHolders.CAMPAIGN_PH_LOYALTY_ENROLLMENT_SOURCE.equalsIgnoreCase(placeholder)){
	    			loyaltyPlaceholder = contactsLoyalty.getLoyaltyType() != null ? getEnrollmentSource(contactsLoyalty.getLoyaltyType() , defVal) : defVal;
	    			logger.info("LOYALTY_ENROLLMENT_SOURCE ::"+loyaltyPlaceholder);
	    	}//LOYALTY_ENROLLMENT_STORE
	    	else if(PlaceHolders.CAMPAIGN_PH_LOYALTY_ENROLLMENT_STORE.equalsIgnoreCase(placeholder)){
	    		loyaltyPlaceholder = contactsLoyalty.getPosStoreLocationId() != null ? contactsLoyalty.getPosStoreLocationId()+"":defVal;
	    		logger.info("LOYALTY_ENROLLMENT_STORE ::"+loyaltyPlaceholder);
	    	}//LOYALTY_REGISTERED_PHONE
	    	else if(PlaceHolders.CAMPAIGN_PH_LOYALTY_REGISTERED_PHONE.equalsIgnoreCase(placeholder)){
	    		loyaltyPlaceholder = contactsLoyalty.getMobilePhone() != null ? contactsLoyalty.getMobilePhone()+"":defVal;
	    		logger.info("LOYALTY_REGISTERED_PHONE ::"+loyaltyPlaceholder);
	    	}//LOYALTY_POINTS_BALANCE
	    	else if(PlaceHolders.CAMPAIGN_PH_LOYALTY_POINTS_BALANCE.equalsIgnoreCase(placeholder)) {
	    		loyaltyPlaceholder = contactsLoyalty.getLoyaltyBalance() != null ? decimalFormat.format(contactsLoyalty.getLoyaltyBalance()) : defVal;
	    		logger.info("LOYALTY_POINTS_BALANCE ::"+loyaltyPlaceholder);
	    	}//LOYALTY_CURRENCY_BALANCE
	    	else if(PlaceHolders.CAMPAIGN_PH_LOYALTY_CURRENCY_BALANCE.equalsIgnoreCase(placeholder)){
	    		loyaltyPlaceholder = contactsLoyalty.getGiftcardBalance() != null ?  decimalFormat.format(contactsLoyalty.getGiftcardBalance()) : defVal;
	    		logger.info("LOYALTY_CURRENCY_BALANCE ::"+loyaltyPlaceholder);
	    	}//LOYALTY_GIFT_BALANCE
	    	else if(PlaceHolders.CAMPAIGN_PH_LOYALTY_GIFT_BALANCE.equalsIgnoreCase(placeholder)){
	    		loyaltyPlaceholder = contactsLoyalty.getGiftBalance() != null ? decimalFormat.format(contactsLoyalty.getGiftBalance()) : defVal;
	    		logger.info("LOYALTY_GIFT_BALANCE ::"+loyaltyPlaceholder);
	    	}//LOYALTY_REWARD_EXPIRATION
	    	else if(PlaceHolders.CAMPAIGN_PH_LOYALTY_REWARD_EXPIRATION_DATE.equalsIgnoreCase(placeholder)){
	    		loyaltyPlaceholder = getRewardExpirationDate(contactsLoyalty ,defVal);
	    		logger.info("LOYALTY_REWARD_EXPIRATION ::"+loyaltyPlaceholder);
	    	}//LOYALTY_GIFT_CARD_EXPIRATION
	    	else if(PlaceHolders.CAMPAIGN_PH_LOYALTY_GIFT_CARD_EXPIRATION_DATE.equalsIgnoreCase(placeholder)){
	    		loyaltyPlaceholder = getGiftCardExpirationDate(contactsLoyalty ,defVal);
	    		logger.info("LOYALTY_GIFT_CARD_EXPIRATION ::"+loyaltyPlaceholder);
	    	}//LOYALTY_HOLD_BALANCE
	    	else if(PlaceHolders.CAMPAIGN_PH_LOYALTY_HOLD_BALANCE.equalsIgnoreCase(placeholder)) {
	    		loyaltyPlaceholder = getHoldBalance(contactsLoyalty,defVal);
	    		logger.info("LOYALTY_HOLD_BALANCE ::"+loyaltyPlaceholder);
	    	}//LOYALTY_REWARD_ACTIVATION_PERIOD
	    	else if(PlaceHolders.CAMPAIGN_PH_LOYALTY_REWARD_ACTIVATION_PERIOD.equalsIgnoreCase(placeholder)){
	    		loyaltyPlaceholder = contactsLoyalty.getProgramTierId() != null ? getRewardActivationPeriod(contactsLoyalty.getProgramTierId(),defVal) : defVal ;
	    		logger.info("LOYALTY_REWARD_ACTIVATION_PERIOD::"+loyaltyPlaceholder);
	    	}//LOYALTY_LAST_EARNED_VALUE
	    	else if(PlaceHolders.CAMPAIGN_PH_LOYALTY_LAST_EARNED_VALUE.equalsIgnoreCase(placeholder)){

	    		loyaltyPlaceholder = contactsLoyalty.getCardNumber() != null ? getLastEarnedValue(contactsLoyalty.getCardNumber(),OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE,defVal) : defVal;
	    		logger.info("LOYALTY_LAST_EARNED_VALUE::"+loyaltyPlaceholder);
	    	}//LOYALTY_LAST_REDEEMED_VALUE
	    	else if(PlaceHolders.CAMPAIGN_PH_LOYALTY_LAST_REDEEMED_VALUE.equalsIgnoreCase(placeholder)){
	    		loyaltyPlaceholder = contactsLoyalty.getCardNumber() != null ? getLastRedeemedValue(contactsLoyalty.getCardNumber(),OCConstants.LOYALTY_TRANS_TYPE_REDEMPTION,defVal) : defVal;
	    		logger.info("LOYALTY_LAST_REDEEMED_VALUE::"+loyaltyPlaceholder);
	    	}
	    	logger.info("Completed replace holder method");
	    	return loyaltyPlaceholder;
	    }//replaceLoyaltyPlaceHolders
*/
/*
		*//**
		 * This method replace loyalty place holders
		 * @param placeholder
		 * @param contactsLoyalty
		 * @param defVal
		 * @return Loyalty Place Holder
		 *//*
		private String replaceLoyaltyPlaceHolders(String placeholder,ContactsLoyalty contactsLoyalty,String defVal) {

			if(contactsLoyalty == null){
				return defVal;
			}
			if(placeholder == null  && defVal == null){
				logger.error("Value is null placeholder :: "+placeholder+"\t contactsLoyalty "+contactsLoyalty+"\t defVal"+defVal);
				return defVal;
			}

			logger.info("In replaceLoyaltyPlaceHolders :: " +  placeholder);
			DecimalFormat decimalFormat = new DecimalFormat("#0.00");
			String loyaltyPlaceholder="";
			//LOYALTYCARDPIN
			if(PlaceHolders.CAMPAIGN_PH_LOYALTY_MEMBERSHIP_PIN.equalsIgnoreCase(placeholder)){
				loyaltyPlaceholder = contactsLoyalty.getCardPin()!= null ? contactsLoyalty.getCardPin(): defVal;
				logger.info("Membership Pin ::"+contactsLoyalty.getCardPin());
			}//REFRESHEDON
			else if(PlaceHolders.CAMPAIGN_PH_LOYALTY_REFRESHEDON.equalsIgnoreCase(placeholder) ){
				loyaltyPlaceholder = contactsLoyalty.getLastFechedDate() ==  null ? defVal :  MyCalendar.calendarToString(contactsLoyalty.getLastFechedDate(), MyCalendar.FORMAT_DATEONLY_GENERAL);
			}//MEMBERSHIP_NUMBER
			else if(PlaceHolders.CAMPAIGN_PH_LOYALTY_MEMBERSHIP_NUMBER.equalsIgnoreCase(placeholder)){
				loyaltyPlaceholder = contactsLoyalty.getCardNumber() != null ? contactsLoyalty.getCardNumber()+"":defVal;
				logger.info("Membership Number ::"+contactsLoyalty.getCardNumber());
			}//MEMBER_TIER
			else if(PlaceHolders.CAMPAIGN_PH_LOYALTY_MEMBER_TIER.equalsIgnoreCase(placeholder)){
				loyaltyPlaceholder = contactsLoyalty.getProgramTierId() != null ? getMemberTier(contactsLoyalty.getProgramTierId() , defVal) : defVal; 
				logger.info("Member Tier ::"+loyaltyPlaceholder);
			}//MEMBER_STATUS
			else if(PlaceHolders.CAMPAIGN_PH_LOYALTY_MEMBER_STATUS.equalsIgnoreCase(placeholder)){
				logger.info("MEMBER_STATUS contactsLoyalty.getMembershipStatus() ::"+contactsLoyalty.getMembershipStatus());
				loyaltyPlaceholder = contactsLoyalty.getMembershipStatus() != null ? contactsLoyalty.getMembershipStatus() : defVal;
				logger.info("MEMBER_STATUS ::"+loyaltyPlaceholder);
			}//MEMBERSHIP_EXPIRATION_DATE
			else if(PlaceHolders.CAMPAIGN_PH_LOYALTY_MEMBERSHIP_EXPIRATION_DATE.equalsIgnoreCase(placeholder)){
				loyaltyPlaceholder = getMemberShipExpirationDate(contactsLoyalty , defVal);	
				logger.info("MEMBERSHIP_EXPIRATION_DATE ::"+loyaltyPlaceholder);
			}//MEMBERSHIP_EXPIRATION_PERIOD
			else if(PlaceHolders.CAMPAIGN_PH_LOYALTY_MEMBERSHIP_EXPIRATION_PERIOD.equalsIgnoreCase(placeholder)){
				//TODO
				loyaltyPlaceholder = getMemberShipExpirationPeriod(contactsLoyalty , defVal);	
				logger.info("MEMBERSHIP_EXPIRATION_PERIOD ::"+loyaltyPlaceholder);
			} //LOYALTY_ENROLLMENT_DATE
			else if(PlaceHolders.CAMPAIGN_PH_LOYALTY_ENROLLMENT_DATE.equalsIgnoreCase(placeholder)){
				loyaltyPlaceholder = contactsLoyalty.getCreatedDate() != null ?  MyCalendar.calendarToString(contactsLoyalty.getCreatedDate(), MyCalendar.FORMAT_DATETIME_STYEAR) : defVal ;
				logger.info("LOYALTY_ENROLLMENT_DATE ::"+loyaltyPlaceholder);
			}//LOYALTY_ENROLLMENT_SOURCE
			else if(PlaceHolders.CAMPAIGN_PH_LOYALTY_ENROLLMENT_SOURCE.equalsIgnoreCase(placeholder)){
				loyaltyPlaceholder = contactsLoyalty.getLoyaltyType() != null ? getEnrollmentSource(contactsLoyalty.getLoyaltyType() , defVal) : defVal;
				logger.info("LOYALTY_ENROLLMENT_SOURCE ::"+loyaltyPlaceholder);
			}//LOYALTY_ENROLLMENT_STORE
			else if(PlaceHolders.CAMPAIGN_PH_LOYALTY_ENROLLMENT_STORE.equalsIgnoreCase(placeholder)){
				loyaltyPlaceholder = contactsLoyalty.getPosStoreLocationId() != null ? contactsLoyalty.getPosStoreLocationId()+"":defVal;
				logger.info("LOYALTY_ENROLLMENT_STORE ::"+loyaltyPlaceholder);
			}//LOYALTY_REGISTERED_PHONE
			else if(PlaceHolders.CAMPAIGN_PH_LOYALTY_REGISTERED_PHONE.equalsIgnoreCase(placeholder)){
				loyaltyPlaceholder = contactsLoyalty.getMobilePhone() != null ? contactsLoyalty.getMobilePhone()+"":defVal;
				logger.info("LOYALTY_REGISTERED_PHONE ::"+loyaltyPlaceholder);
			}//LOYALTY_POINTS_BALANCE
			else if(PlaceHolders.CAMPAIGN_PH_LOYALTY_POINTS_BALANCE.equalsIgnoreCase(placeholder)) {
				loyaltyPlaceholder = contactsLoyalty.getLoyaltyBalance() != null ? decimalFormat.format(contactsLoyalty.getLoyaltyBalance()) : defVal;
				logger.info("LOYALTY_POINTS_BALANCE ::"+loyaltyPlaceholder);
			}//LOYALTY_CURRENCY_BALANCE
			else if(PlaceHolders.CAMPAIGN_PH_LOYALTY_CURRENCY_BALANCE.equalsIgnoreCase(placeholder)){
				loyaltyPlaceholder = contactsLoyalty.getGiftcardBalance() != null ?  decimalFormat.format(contactsLoyalty.getGiftcardBalance()) : defVal;
				logger.info("LOYALTY_CURRENCY_BALANCE ::"+loyaltyPlaceholder);
			}//LOYALTY_GIFT_BALANCE
			else if(PlaceHolders.CAMPAIGN_PH_LOYALTY_GIFT_BALANCE.equalsIgnoreCase(placeholder)){
				loyaltyPlaceholder = contactsLoyalty.getGiftBalance() != null ? decimalFormat.format(contactsLoyalty.getGiftBalance()) : defVal;
				logger.info("LOYALTY_GIFT_BALANCE ::"+loyaltyPlaceholder);
			}//LOYALTY_REWARD_EXPIRATION
			else if(PlaceHolders.CAMPAIGN_PH_LOYALTY_REWARD_EXPIRATION_DATE.equalsIgnoreCase(placeholder)){
				loyaltyPlaceholder = getRewardExpirationDate(contactsLoyalty ,defVal);
				logger.info("LOYALTY_REWARD_EXPIRATION ::"+loyaltyPlaceholder);
			}//LOYALTY_REWARD_EXPIRATION_Period
			else if(PlaceHolders.CAMPAIGN_PH_LOYALTY_REWARD_EXPIRATION_PERIOD.equalsIgnoreCase(placeholder)){
				//TODO
				loyaltyPlaceholder = getRewardExpirationPeriod(contactsLoyalty ,defVal);
				logger.info("LOYALTY_REWARD_EXPIRATION_Period ::"+loyaltyPlaceholder);
			}//LOYALTY_GIFT_CARD_EXPIRATION
			else if(PlaceHolders.CAMPAIGN_PH_LOYALTY_GIFT_CARD_EXPIRATION_DATE.equalsIgnoreCase(placeholder)){
				loyaltyPlaceholder = getGiftCardExpirationDate(contactsLoyalty ,defVal);
				logger.info("LOYALTY_GIFT_CARD_EXPIRATION ::"+loyaltyPlaceholder);
			}//LOYALTY_HOLD_BALANCE
			else if(PlaceHolders.CAMPAIGN_PH_LOYALTY_HOLD_BALANCE.equalsIgnoreCase(placeholder)) {
				loyaltyPlaceholder = getHoldBalance(contactsLoyalty,defVal);
				logger.info("LOYALTY_HOLD_BALANCE ::"+loyaltyPlaceholder);
			}//LOYALTY_REWARD_ACTIVATION_PERIOD
			else if(PlaceHolders.CAMPAIGN_PH_LOYALTY_REWARD_ACTIVATION_PERIOD.equalsIgnoreCase(placeholder)){
				loyaltyPlaceholder = contactsLoyalty.getProgramTierId() != null ? getRewardActivationPeriod(contactsLoyalty.getProgramTierId(),defVal) : defVal ;
				logger.info("LOYALTY_REWARD_ACTIVATION_PERIOD::"+loyaltyPlaceholder);
			}//LOYALTY_LAST_EARNED_VALUE
			else if(PlaceHolders.CAMPAIGN_PH_LOYALTY_LAST_EARNED_VALUE.equalsIgnoreCase(placeholder)){

				loyaltyPlaceholder = contactsLoyalty.getCardNumber() != null ? getLastEarnedValue(contactsLoyalty.getCardNumber(),OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE,defVal) : defVal;
				logger.info("LOYALTY_LAST_EARNED_VALUE::"+loyaltyPlaceholder);
			}//LOYALTY_LAST_REDEEMED_VALUE
			else if(PlaceHolders.CAMPAIGN_PH_LOYALTY_LAST_REDEEMED_VALUE.equalsIgnoreCase(placeholder)){
				loyaltyPlaceholder = contactsLoyalty.getCardNumber() != null ? getLastRedeemedValue(contactsLoyalty.getCardNumber(),OCConstants.LOYALTY_TRANS_TYPE_REDEMPTION,defVal) : defVal;
				logger.info("LOYALTY_LAST_REDEEMED_VALUE::"+loyaltyPlaceholder);
			}// LOYALTY_MEMBERSHIP_PASSWORD 
			else if(PlaceHolders. CAMPAIGN_PH_LOYALTY_MEMBERSHIP_PASSWORD.equalsIgnoreCase(placeholder)){
				loyaltyPlaceholder = getMembershipPassword(contactsLoyalty,defVal);
				logger.info(" CAMPAIGN_PH_LOYALTY_MEMBERSHIP_PASSWORD::"+loyaltyPlaceholder);
			} //LOYALTY_LOGIN_URL
			else if(PlaceHolders.CAMPAIGN_PH_LOYALTY_LOGIN_URL.equalsIgnoreCase(placeholder)){
				loyaltyPlaceholder = getLoyaltyURL(contactsLoyalty,defVal);
				logger.info("CAMPAIGN_PH_LOYALTY_LOGIN_URL::"+loyaltyPlaceholder);
			}//LOYALTY_GIFT_AMOUNT_EXPIRATION_DATE
			else if(PlaceHolders.CAMPAIGN_PH_LOYALTY_GIFT_AMOUNT_EXPIRATION_DATE.equalsIgnoreCase(placeholder)){
				//TODO
				loyaltyPlaceholder = getLoyaltyGiftAmountExpriationDate(contactsLoyalty,defVal);
				logger.info("LOYALTY_GIFT_AMOUNT_EXPIRATION_DATE::"+loyaltyPlaceholder);
			}//CAMPAIGN_PH_LOYALTY_GIFT_MEMBERSHIP_EXPIRATION_DATE
			else if(PlaceHolders.CAMPAIGN_PH_LOYALTY_GIFT_MEMBERSHIP_EXPIRATION_DATE.equalsIgnoreCase(placeholder)){
				//TODO
				loyaltyPlaceholder = getLoyaltyMembershipExpirationDate(contactsLoyalty,defVal);
				logger.info("LOYALTY_GIFT_MEMBERSHIP_EXPIRATION_DATE::"+loyaltyPlaceholder);
			}// PH_ORGANIZATION_NAME
			else if(PlaceHolders.PH_ORGANIZATION_NAME.equalsIgnoreCase(placeholder)){
				loyaltyPlaceholder = contactsLoyalty.getCardNumber() != null ? getLastRedeemedValue(contactsLoyalty.getCardNumber(),OCConstants.LOYALTY_TRANS_TYPE_REDEMPTION,defVal) : defVal;
				logger.info("PH_ORGANIZATION_NAME::"+loyaltyPlaceholder);
			}  
			logger.info("Completed replace holder method");
			return loyaltyPlaceholder;
		}//replaceLoyaltyPlaceHolders	
	
		
*/		

		
		private Map<String, Object> replaceStoreRelatedMergeTags(JSONObject drJson,String templateContentStr, Set<String> phSet, Users users,StringBuffer previwPhKeyValSb){
			//2.4.8 related starts
			
			
			Map<String, Object> phContentValMap= new HashMap<String, Object>();
			try{

				

				OrganizationStoresDao organizationStoresDao = null;

				try{
					organizationStoresDao = (OrganizationStoresDao)ServiceLocator.getInstance().getDAOByName(OCConstants.ORGANIZATION_STORES_DAO);
				}
				catch(Exception e){
					logger.error("Exception while getting dao...",e);
				}



				List<OrganizationStores> organizationStoresList = organizationStoresDao.findByOrganization(users.getUserOrganization().getUserOrgId());
				
				JSONObject receiptJSONObj = (JSONObject)drJson.get("Receipt");
				Object storeJsonObj = receiptJSONObj.get("Store");
				String store=null;
				
				if(storeJsonObj != null && !storeJsonObj.toString().trim().isEmpty()){
					store = storeJsonObj.toString().trim();
				}

				logger.info("store from JSON >>> "+store);
				
				OrganizationStores requiredOrganizationStore = null;
				for(OrganizationStores anOrganizationStore : organizationStoresList){
					if(anOrganizationStore.getHomeStoreId() != null && anOrganizationStore.getHomeStoreId().equals(store)){
						requiredOrganizationStore = anOrganizationStore;
					}
				}



	       
				
				Address address = requiredOrganizationStore.getAddress();
				
				
				if(previwPhKeyValSb == null) previwPhKeyValSb = new StringBuffer();
				
				Iterator<String> phSetItr = phSet.iterator();
				
				logger.info("phSet >>>>>>>> "+phSet);
				while (phSetItr.hasNext()) {

					String replacevalueStr = "";
					
					String cfStr = (String) phSetItr.next();
					
					if(cfStr.contains("REC_")) {
						continue;
					}
					
					String aCorresFieldOfStore = cfStr.substring(0,cfStr.indexOf("/")).trim();		
					logger.info("cfStr >>>>>> "+cfStr+"  >>>>>> aCorresFieldOfStore  >>>>>>> "+aCorresFieldOfStore);
					
					if("GEN_storeName".equalsIgnoreCase(aCorresFieldOfStore)){
						
						replacevalueStr = requiredOrganizationStore.getStoreName().trim();
						
					}else if("GEN_storeManager".equalsIgnoreCase(aCorresFieldOfStore)){
						
						replacevalueStr = requiredOrganizationStore.getStoreManagerName();
						
					}else if("GEN_storePhone".equalsIgnoreCase(aCorresFieldOfStore)){
						
						replacevalueStr = address.getPhone();
						
					}else if("GEN_storeEmail".equalsIgnoreCase(aCorresFieldOfStore)){
						
						
						replacevalueStr = requiredOrganizationStore.getEmailId();
					}else if("GEN_storeStreet".equalsIgnoreCase(aCorresFieldOfStore)){
						
						replacevalueStr = address.getAddressOne();
						
					}else if("GEN_storeCity".equalsIgnoreCase(aCorresFieldOfStore)){
						
						replacevalueStr = address.getCity();
						
					}else if("GEN_storeState".equalsIgnoreCase(aCorresFieldOfStore)){
						
						replacevalueStr = address.getState();
						
					}else if("GEN_storeZip".equalsIgnoreCase(aCorresFieldOfStore)){
						replacevalueStr = address.getPin();
					}
					
					
					if(replacevalueStr != null && replacevalueStr.trim().length()  > 0) {
						templateContentStr = templateContentStr.replace("["+cfStr+"]", replacevalueStr);
						logger.info(" tag : "+cfStr+" replaced with : "+replacevalueStr);
						if(previwPhKeyValSb.length() > 0) previwPhKeyValSb.append(Constants.ADDR_COL_DELIMETER);
						previwPhKeyValSb.append("["+cfStr+"]" + Constants.DELIMETER_DOUBLECOLON + replacevalueStr);

						phSetItr.remove();
					}
				}
				

				
			
				
			}catch(Exception e){
				logger.error("Exception >>> ",e);
			}
			
			finally{
				phContentValMap.put("1", templateContentStr);	
				phContentValMap.put("2", phSet);	
				phContentValMap.put("3", previwPhKeyValSb);	
				return phContentValMap;
			}
			
			//2.4.8 related ends
		}
		/*
		 * This method process 
		 * 1. find out the REC tags.
		 * 2. Replace those REC tags.
		 * 	parameters : entire content with the click url  has |^rec tags.
		 * 	result : entire content with replaced |^Rec values. 
		 */

		public String replaceRecommTags(String htmlContent) {
			// TODO Auto-generated method stub
			String checkPattern = "\\\\REC_(.*?)\\^\\|";
			String subStr = Constants.STRING_NILL;
			subStr = htmlContent;
			String alink = Constants.STRING_NILL;
			int posStart = 0;
			int posEnd = 0;
			htmlContent = htmlContent.replaceAll("target=\"_blank\"","");// "style=[LINK_VISIBILITY]"); // style="visibility: hidden"
			Pattern r = Pattern.compile(checkPattern,Pattern.CASE_INSENSITIVE);
			Matcher m = r.matcher(htmlContent);
			Set<String> totalRECPhSet = null;
			String result = htmlContent;
			String ph = null;
			totalRECPhSet = new HashSet<String>();
			String cfNameListStr = "";
//ignore all these
			try {
				while(m.find()) {

					ph = m.group(1); //.toUpperCase()
//					if(logger.isInfoEnabled()) logger.info("Ph holder :" + ph);

					if(ph.startsWith("REC")) {
						totalRECPhSet.add(ph);
					}
				}

		}catch(Exception e) {
			logger.info("Error in retrieving the recommendation tag."+ e);
		}
			return htmlContent;

	}
	}
	class HttpPostClass {
		 private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);

		public List<RecommendationResponseObject> fetchRecommendationByHTTP(String productIds, Long userId) {
			// TODO Auto-generated method stub
			List<RecommendationResponseObject> recoObject = new ArrayList<>();
			


			try {
			//	RetailProSalesDao retailProSalesDao = (RetailProSalesDao)ServiceLocator.getInstance().getDAOByName(OCConstants.RETAILPRO_SALES_DAO);
				
				/*
				 * 
	{
    "variant_code": "SULDSH52STONE",
    "class_code": "LADIES WESTERNWEAR",
    "image_url": "https://cdn.shopify.com/s/files/1/0623/4754/2777/products/8907546578333_4.jpg?v=1670242851",
    "description": "Women's Blue Cotton Solid Shorts"
  },
  {
    "variant_code": "LST00015SKYBLUE",
    "class_code": "LADIES SLEEPWEAR",
    "image_url": "https://cdn.shopify.com/s/files/1/0623/4754/2777/products/LST00015SKYBLUE_1_e4a42e1d-573d-4fc6-b696-5cc10d8271c5.jpg?v=1683904536",
    "description": "Women's Sky Blue Cotton Printed Top"
  },
  {
    "variant_code": "SP02BLACK",
    "class_code": "LADIES ACCESSORIES",
    "image_url": "https://cdn.shopify.com/s/files/1/0623/4754/2777/products/8907546411487_4.jpg?v=1672307734",
    "description": "Women's Black Cotton Solid Slip"
  }
	{
    "variant_code": "SP02BLACK",
    "class_code": "LADIES ACCESSORIES",
    "image_url": "https://cdn.shopify.com/s/files/1/0623/4754/2777/products/8907546411487_4.jpg?v=1672307734",
    "description": "Women's Black Cotton Solid Slip"
  }			 */
			//	String stylunId = userId+"";
				String recomLink = PropertyUtil.getPropertyValue("RecommendLink");
			//	String phone = "9704146900";
				recomLink = recomLink.replace("{user_id}", userId+""); //.replace("{phone}", phone);
			//	int Status = PostRequest(recomLink);
				 recoObject = PostRequest(recomLink,productIds);
				 if(recoObject != null  && recoObject.size() > 0) {
			/*	if(Status == -1) {
					logger.info("Post returned -1, unable to post the data.");
					return null;
				}else {
					logger.info("Post returned 200, Successfully posted.");

					//successfully posted 
				//need to get the data from the posted reqested.
				}*/
				
					
			//	Iterator valueCodeItr = codes.iterator();
				String prodCode = Constants.STRING_NILL;
				RecommendationResponseObject eachRecoObj = null;
			//	while(valueCodeItr.hasNext()) {
				/*	prodCode = (String) valueCodeItr.next();
					eachRecoObj = new RecommendationResponseObject();
					eachRecoObj.setVariant_code(prodCode);

				} */
			/*	RecommendationResponseObject recomm1 = new RecommendationResponseObject();

				recomm1.setVariant_code("SULDSH52STONE");
				recomm1.setClass_code("LADIES WESTERNWEAR");
				recomm1.setImage_url("https://cdn.shopify.com/s/files/1/0623/4754/2777/products/8907546578333_4.jpg?v=1670242851");
				recomm1.setDescription("Women's Blue Cotton Solid Shorts");
				recomm.add(recomm1);
			
				
				RecommendationResponseObject recomm2 = new RecommendationResponseObject();

				recomm2.setVariant_code("LST00015SKYBLUE");
				recomm2.setClass_code("LADIES SLEEPWEAR");
				recomm2.setImage_url("https://cdn.shopify.com/s/files/1/0623/4754/2777/products/LST00015SKYBLUE_1_e4a42e1d-573d-4fc6-b696-5cc10d8271c5.jpg?v=1683904536");
				recomm2.setDescription("Women's Sky Blue Cotton Printed Top");
				recomm.add(recomm2);
				
				RecommendationResponseObject recomm3 = new RecommendationResponseObject();

				recomm3.setVariant_code("SP02BLACK");
				recomm3.setClass_code("LADIES ACCESSORIES");
				recomm3.setImage_url("https://cdn.shopify.com/s/files/1/0623/4754/2777/products/8907546411487_4.jpg?v=1672307734");
				recomm3.setDescription("Women's Black Cotton Solid Slip");
				recomm.add(recomm3);*/
				
			/*	RecommendationResponseObject recomm4 = new RecommendationResponseObject();

				recomm4.setVariant_code("SULDSH52STONERepeated");
				recomm4.setDepartment("LADIES ACCESSORIES Repeated");
				recomm4.setImage_url("https://cdn.shopify.com/s/files/1/0623/4754/2777/products/8907546578333_4.jpg?v=1670242851");
				recomm4.setDescription("Women's Black Cotton Solid Slip repeated");
				recoObject.add(recomm4);
*/

			}
			}catch(Exception e) {
				e.printStackTrace();
			}
			return recoObject;
		}
		public List<RecommendationResponseObject> PostRequest(String recomLink,String productIds) {
			
			   int responseCode = -1;
			   List<RecommendationResponseObject> recommList = new ArrayList<>() ;
			   RecommendationResponseObject recomm ;
			try {
				
				
	            URL url = new URL(recomLink);
	            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	            JSONArray json = null ;
	            String jsonStr = null;
	            connection.setRequestMethod("POST");
	            connection.setDoOutput(true);

	            connection.setRequestProperty("Content-Type", "application/json");
	        //    String[] products = productIds.split(Constants.DELIMITER_PIPE);
	            if(productIds !=null && productIds.length() > 0) {
	            String arr = productIds.replace(Constants.DELIMETER_COMMA, "\", \"");
	            
	          //"{\"product_id\": [\"MGT00067AMBERYELLOW\", \"MGT00075EIFFELTOWER\", \"MGT00032EIFFELTOWER\", \"MGT00059GULL\", 
	           // \"MCT00025DUSTYPINK\", \"TBCTS00001NAVY\", \"TBCTS00001WATERCRESS\"]}";
	            String jsonData = "{\"product_id\" : [ \""+arr+"\"]}";//"{\"product_id\": [\"MGT00067AMBERYELLOW\", \"MGT00075EIFFELTOWER\", \"MGT00032EIFFELTOWER\", \"MGT00059GULL\", \"MCT00025DUSTYPINK\", \"TBCTS00001NAVY\", \"TBCTS00001WATERCRESS\"]}";//"{\"key1\":\"value1\",\"key2\":\"value2\"}";
	         if(jsonData.length() >0) {
	            DataOutputStream outStream = new DataOutputStream(connection.getOutputStream());
	            outStream.writeBytes(jsonData);
	            outStream.flush();
	            outStream.close();

	             responseCode = connection.getResponseCode();
	            System.out.println("Response Code: " + responseCode);

	            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	            String line;
	            StringBuilder response = new StringBuilder();

	            while ((line = reader.readLine()) != null) {
	                response.append(line);
	            }
	            reader.close();
	            connection.disconnect();
	            try {
					
					json = (JSONArray)JSONValue.parse(response.toString());
			    	logger.info("JSON Root obj is ::"+json);

				} catch(Exception e) {
					
					logger.error("Error : Invalid json Object .. Returning. ****");
					return null;
				}
	           

			//	jsonStr = jsonResponse.toJSONString();
				for(int i = 0; i < json.size() ; i++) {
	            // Print the parsed JSON object
					recomm =  new RecommendationResponseObject();
				JSONObject obj = (JSONObject)json.get(i);
				if(obj.containsKey("description")) {
					recomm.setDescription((String)obj.get("description"));
				}
				if(obj.containsKey("Department")) {
					recomm.setDepartment((String)obj.get("Department"));
				}
				if(obj.containsKey("image_url")) {
					recomm.setImage_url((String)obj.get("image_url"));
				}
				if(obj.containsKey("variant_code")) {
					String codeURL = (String)obj.get("variant_code");
					codeURL = PropertyUtil.getPropertyValue("StylunsearchURL").replace("{variant_code}", codeURL);
					recomm.setVariant_code(codeURL);
				}
				recommList.add(recomm);
				} // for

	         } //  if string. product
	            } // if json
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
		
			return recommList;
		}
		
		public List<RecommendationResponseObject> getRequest(String link){
			
			List<RecommendationResponseObject> res = new ArrayList<>();
			RecommendationResponseObject recomm ;
			 try {
		            URL url = new URL(link);
		            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		            connection.setRequestMethod("GET");
		            int responseCode = connection.getResponseCode();
		            logger.info("Response Code: " + responseCode);
		            connection.disconnect();

		            JSONArray json = null ;
		            String jsonStr = null;
		          //  json = (JSONArray)
		         
		            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		            String line;
		            StringBuilder response = new StringBuilder();

		            while ((line = reader.readLine()) != null) {
		                response.append(line);
		            }

		            reader.close();
		            try {
						
						json = (JSONArray)JSONValue.parse(response.toString());
				    	logger.info("JSON Root obj is ::"+json);

					} catch(Exception e) {
						
						logger.error("Error : Invalid json Object .. Returning. ****");
						return null;
					}
		           

				//	jsonStr = jsonResponse.toJSONString();
					for(int i = 0; i < json.size() ; i++) {
		            // Print the parsed JSON object
						recomm =  new RecommendationResponseObject();
					JSONObject obj = (JSONObject)json.get(i);
					if(obj.containsKey("description")) {
						recomm.setDescription((String)obj.get("description"));
					}
					if(obj.containsKey("class_code")) {
						recomm.setDepartment((String)obj.get("Department"));
					}
					if(obj.containsKey("image_url")) {
						recomm.setImage_url((String)obj.get("image_url"));
					}
					if(obj.containsKey("variant_code")) {
						recomm.setVariant_code((String)obj.get("variant_code"));
					}
					res.add(recomm);
					} // for

		            
		        } catch (Exception e) {
		            e.printStackTrace();
		        }
			
			return res;
			
		}
		
	}
