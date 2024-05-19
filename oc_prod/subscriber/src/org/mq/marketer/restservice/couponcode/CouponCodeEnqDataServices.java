package org.mq.marketer.restservice.couponcode;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.mq.marketer.campaign.beans.Contacts;
import org.mq.marketer.campaign.beans.CouponCodes;
import org.mq.marketer.campaign.beans.CouponDiscountGeneration;
import org.mq.marketer.campaign.beans.Coupons;
import org.mq.marketer.campaign.beans.OrganizationStores;
import org.mq.marketer.campaign.beans.UserOrganization;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.ContactsDao;
import org.mq.marketer.campaign.dao.CouponCodesDao;
import org.mq.marketer.campaign.dao.CouponDiscountGenerateDao;
import org.mq.marketer.campaign.dao.CouponsDao;
import org.mq.marketer.campaign.dao.OrganizationStoresDao;
import org.mq.marketer.campaign.dao.POSMappingDao;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Utility;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Comboitem;

public class CouponCodeEnqDataServices extends AbstractController {
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	public CouponCodeEnqDataServices(){}
	
	UsersDao usersDao;
	
	public UsersDao getUsersDao() {
		return usersDao;
	}

	public void setUsersDao(UsersDao usersDao) {
		this.usersDao = usersDao;
	}
	
	
	CouponCodesDao couponCodesDao;
	public CouponCodesDao getCouponCodesDao() {
		return couponCodesDao;
	}

	public void setCouponCodesDao(CouponCodesDao couponCodesDao) {
		this.couponCodesDao = couponCodesDao;
	}
	
	
	CouponDiscountGenerateDao coupDiscGenDao; 
	public CouponDiscountGenerateDao getCoupDiscGenDao() {
		return coupDiscGenDao;
	}

	public void setCoupDiscGenDao(CouponDiscountGenerateDao coupDiscGenDao) {
		this.coupDiscGenDao = coupDiscGenDao;
	}
	private CouponsDao couponsDao;
	public CouponsDao getCouponsDao() {
		return couponsDao;
	}

	public void setCouponsDao(CouponsDao couponsDao) {
		this.couponsDao = couponsDao;
	}
	
	
	private ContactsDao contactsDao;
	public ContactsDao getContactsDao() {
		return contactsDao;
	}

	public void setContactsDao(ContactsDao contactsDao) {
		this.contactsDao = contactsDao;
	}

	private POSMappingDao posMappingDao;
	public POSMappingDao getPosMappingDao() {
		return posMappingDao;
	}

	public void setPosMappingDao(POSMappingDao posMappingDao) {
		this.posMappingDao = posMappingDao;
	}
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		logger.debug("-- Just Entered --");   
		JSONArray coupDisInfoJsonArrObj = new JSONArray();
		JSONObject coupDisInfoJsonObj = null;
		JSONObject coupCodejsonObj = null;
		JSONObject headerInfoJson = null;
		
		String msg = "";
		int errorCode = -1;
		
		try {
			JSONObject coupCodeEnqJsonObj = null;
			JSONObject rootJsonObj = null;
			
			if((request.getParameter("jsonVal") != null && request.getParameter("jsonVal").length() > 0)) {
				
				logger.debug("request getting from JsonVal");
				
				String jsonValStr = request.getParameter("jsonVal");
				rootJsonObj = (JSONObject)JSONValue.parse(jsonValStr);
				
				if(rootJsonObj == null) {
					
					logger.debug("Error : Invalid json Object .. Returning. ****");
					msg = "Error :  Invalid request. ****";
					errorCode = 100001;
					return  null;
				}
			} else {
				
				// stream Test
				  	InputStream is = request.getInputStream();
					BufferedReader br = new BufferedReader(new InputStreamReader(is));
					char[] chr = new char[1024];
					int bytesRead = 0;
					StringBuilder sb = new StringBuilder();
					
					while ((bytesRead = br.read(chr)) > 0) {
				         sb.append(chr, 0, bytesRead);
				    }
					br.close();
					String gotVal = sb.toString().trim();
					
//					logger.debug("REst body value is \r\n"+ gotVal);
					try {
						rootJsonObj = (JSONObject)JSONValue.parse(gotVal);
//						logger.debug("==="+rootJsonObj);
					} catch(Exception e) {
						logger.error("Exception ::" , e);
						logger.debug("Error : Invalid json Object .. Returning. ****");
						msg = "Error : Invalid request. ****";
						errorCode = 100001;
						return  null;
					}		
				}
			
			
			
			if(rootJsonObj == null) {
				
				logger.debug("Error : Unable to parse the json.. Returning. ****");
				msg = "Error : Invalid request.. ****";
				errorCode = 100002;
				return  null;
				
			}
			
//			
			
			coupCodeEnqJsonObj = (JSONObject)rootJsonObj.get("COUPONCODEENQREQ");
			
			if(coupCodeEnqJsonObj == null){
				msg = "Error : Invalid request.. ****";
				errorCode = 100002;
				return  null;
			}
			
//			logger.debug("coupCodeEnqJsonObj value is "+ coupCodeEnqJsonObj);
			
			
			/***************************/
//			JSONObject headerjsonObj =  (JSONObject)coupCodeEnqJsonObj.get("HEADERINFO");
//			logger.debug("headerjsonObj after value is "+ headerjsonObj);

			
			JSONObject userDetjsonObj =  (JSONObject)coupCodeEnqJsonObj.get("USERDETAILS");
//			logger.debug("userDetjsonObj value is "+ userDetjsonObj);
			
			
			if(userDetjsonObj == null) {
				
				logger.debug("Error : unable to find the User details in JSON ****");
				msg = "Error : Invalid request..";
				errorCode = 100003;
			}
			
			
			String userNameStr  = userDetjsonObj.get("USERNAME").toString().trim();
			
			if(userNameStr == null || userNameStr.trim().length() == 0) {
				logger.debug("Error : unable to find the User Name  in JSON ****");
				msg = "Error : Invalid request..";
				errorCode = 100004;
				return null;
			}
			
			String orgId = userDetjsonObj.get("ORGID").toString().trim();
			if(orgId == null || orgId.trim().length() == 0) {
				logger.debug("Error : unable to find the User Organization  in JSON ****");
				msg = "Error : Invalid request.";
				errorCode = 100005;
				return null;
			}
			
			String tokenStr = userDetjsonObj.get("TOKEN").toString().trim();
			if(tokenStr == null || tokenStr.trim().length() == 0) {
				logger.debug("Error : unable to find the User Token  in JSON ****");
				msg = "Error : Invalid request.";
				errorCode = 100006;
				return null;
			}
			
			//check the User obj from DB
			Users user = usersDao.findByToken(userNameStr+Constants.USER_AND_ORG_SEPARATOR+orgId , tokenStr);
			
			if(user == null) {
				logger.debug("Error : User not exists in DB ****");
				msg = "Error : Invalid user-credentials.";
				errorCode = 100011;
				return null;
			}
			logger.debug("User Id is :: "+user.getUserId()+" And Promocode enquiry Json :::"+rootJsonObj);
			//***************************/
			
			headerInfoJson = (JSONObject)coupCodeEnqJsonObj.get("HEADERINFO");
			
			coupCodejsonObj =  (JSONObject)coupCodeEnqJsonObj.get("COUPONCODEINFO");
			
			if(coupCodejsonObj == null) {
				
				logger.debug("Error : unable to find the Promo-code Info  in JSON ****");
				msg = "Error : Invalid request. Missing promo-code.";
				errorCode = 100007;
				return null;
			}
			String coupCodeStr = coupCodejsonObj.get("COUPONCODE").toString().trim();
			String customerId = "";
			if(coupCodejsonObj.get("CUSTOMERID") != null) {
				customerId =  coupCodejsonObj.get("CUSTOMERID").toString();
			}
			String phoneStr ="";
			if(coupCodejsonObj.get("PHONE") != null) {
				phoneStr =  coupCodejsonObj.get("PHONE").toString();
			}
			String emailIdStr ="";
			if(coupCodejsonObj.get("EMAIL") != null) {
				emailIdStr =  coupCodejsonObj.get("EMAIL").toString();
			}
//			String emailIdStr =  coupCodejsonObj.get("EMAIL").toString();
			
			
			if(coupCodeStr == null  || coupCodeStr.trim().length() == 0 ) {
				
				logger.debug("Error : unable to find thePromo-code Info  in JSON ****");
				msg = "Error : Invalid request. Missing promo-code.";
				errorCode = 100008;
				return null;
			}
						
			
//			logger.debug(">>>org ID is <<"+user.getUserOrganization().getUserOrgId()+ " >>coupCodeStr<<<"+coupCodeStr);
			
			
			if(!coupCodeStr.equals("OC-LOYALTY")) {
				
				//Get Coupon Object
				CouponCodes  coupCodeObj = couponCodesDao.testForCouponCodes(coupCodeStr, user.getUserOrganization().getUserOrgId());
				if(coupCodeObj != null && !coupCodeObj.getCouponCode().equals(coupCodeStr)) {
					
					logger.debug("Error : promo code is not macthing with request couponcode  of json coupon "+coupCodeStr);
					msg = "Error : Invalid  promo-code.";
					errorCode = 100918;
					return null;
					
				}
				
				Coupons coupObj = null;
				if(coupCodeObj == null) {
					
					coupObj = couponsDao.findCoupon(coupCodeStr,user.getUserOrganization().getUserOrgId());
					
					if(coupObj == null){
						logger.debug("Error : promo code is not macthing with request couponcode  of json coupon "+coupCodeStr);
						msg = "Error : Invalid Promo-code Enquiry in JSON";
						errorCode = 100012;
						return null;
					}
				}else{
					coupObj  = coupCodeObj.getCouponId();
				}
				
				if(coupObj.getExpiryType().equals(Constants.COUP_VALIDITY_PERIOD_DYNAMIC)){
					if(coupCodeObj.getStatus().equals(Constants.COUP_STATUS_EXPIRED)){
						msg = "Error : Promotion expired.";
	            		errorCode = 100015;
	            		return null;
					}
				}
				
				if(coupObj != null && coupObj.getCouponGeneratedType().equals(Constants.COUP_GENT_TYPE_SINGLE) && 
						!coupObj.getCouponCode().equals(coupCodeStr)) {
					logger.debug("Error : unable to find thePromo-code Info  in JSON ****");
					msg = "Error : Invalid  promo-code.";
					errorCode = 100918;
					return null;
				}
				



				String serverTimeZoneVal = PropertyUtil.getPropertyValueFromDB(Constants.SERVER_TIMEZONE_VALUE);
				int serverTimeZoneValInt = Integer.parseInt(serverTimeZoneVal);
				String timezoneDiffrenceMinutes = user.getClientTimeZone();
				int timezoneDiffrenceMinutesInt = 0;
				
				if(timezoneDiffrenceMinutes != null)  timezoneDiffrenceMinutesInt = Integer.parseInt(timezoneDiffrenceMinutes);
				timezoneDiffrenceMinutesInt = timezoneDiffrenceMinutesInt-serverTimeZoneValInt;
				
				Calendar cal = Calendar.getInstance();
				cal.add(Calendar.MINUTE, timezoneDiffrenceMinutesInt);
				
				if(cal.after(coupObj.getCouponExpiryDate())) {
					errorCode = 100015;
					msg = "Promotion expired.";
					return null;
									
				}
				
				if(coupObj.getExpiryType().equals(Constants.COUP_VALIDITY_PERIOD_STATIC)){
				if(coupObj.getStatus().equals(Constants.COUP_STATUS_ACTIVE) ) {
	            	msg = " Format Error : Promo-code can't be redeemed. Its validity is set in future dates. ";
	                errorCode = 100931;
	                return null;
	            }
				}
				
	            if(!coupObj.getStatus().equals(Constants.COUP_STATUS_RUNNING) ){
	               
	                logger.debug("Error : Promo-code Object cannot be redeemed ****");
	                msg = " Format Error : Promo-code can't be redeemed. Promo-code is "+coupObj.getStatus();
	                errorCode = 100919;
	                return null;
	               
	            }
				
				//Check with store number
				String strorNumberStr = "";
				
				if((coupObj.getAllStoreChk() != null && coupObj.getAllStoreChk() == false) ||
						(coupObj.getSelectedStores() != null && coupObj.getSelectedStores().trim().length() > 0)) {
					if(coupCodejsonObj.containsKey("STORENUMBER")) {
						
						strorNumberStr = coupCodejsonObj.get("STORENUMBER").toString().trim();
						
						if(strorNumberStr == null || strorNumberStr.length() == 0) {
							errorCode = 100017;
							msg = "Missing store number.";
							return null;
						}
						
					} else {
						errorCode = 100018;
						msg = "Invalid store number.";
						return null;
					}
					
					//Check with Store
					
					String seledStoreStr =  coupObj.getSelectedStores();
					String[] storeListArr  = seledStoreStr.split(";=;");
					boolean storeExisted = false;
					
					Long jsonStoreLong = null;
					try {
						jsonStoreLong =	Long.parseLong(strorNumberStr);
					} catch (Exception e) {
						logger.error("Exception ::" , e);
					}
					
					
					Long eachStoreLong = null;
					
					for (String eachStore : storeListArr) {
						try {
							eachStoreLong =	Long.parseLong(eachStore.trim());
							if(jsonStoreLong != null && eachStoreLong == jsonStoreLong) {
								storeExisted = true ;
								break;
							}
							
						} catch (Exception e) {
							logger.error("Exception ::" , e);
						}
						if(eachStore.trim().equals(strorNumberStr.trim())) {
							storeExisted = true ;
							break;
						}
						
					}
					
					
					if(!storeExisted) {
						errorCode = 100021;
						msg = "This store not available for this promo";
						return null;
					}
					
				}
				
				
				
				
				Contacts dbContactObj = null;
				if(coupObj.getCouponGeneratedType().equals(Constants.COUP_GENT_TYPE_SINGLE) ) {
					
					Long orgIdLong = user.getUserOrganization().getUserOrgId();
					long redeemdCount = couponCodesDao.findRedeemdCoupCodeByCoup(coupObj.getCouponId(), orgIdLong, Constants.COUP_CODE_STATUS_REDEEMED);
	                
					logger.debug("redeemdCount ::"+redeemdCount+" and coupObj.getRedemedAutoChk()::"+coupObj.getRedemedAutoChk());
	                logger.debug("coupObj.getRedeemdCount() >>"+coupObj.getRedeemdCount());
	               
	                if(coupObj.getRedemedAutoChk()== false &&
	                        (coupObj.getRedeemdCount() != null &&  coupObj.getRedeemdCount() <= redeemdCount )) {
	                   
	                        errorCode = 100921;
	                        msg = "Redemption limit exceeded.";
	                        return null;
	                       
	                }
	                   
					logger.debug("getSingPromoContUnlimitedRedmptChk  Store Checking  :: "+coupObj.getSingPromoContUnlimitedRedmptChk());
					
					if(coupObj.getSingPromoContUnlimitedRedmptChk() != null && !coupObj.getSingPromoContUnlimitedRedmptChk()) {
							
						
						boolean isValidCustFlag = false;
						String validEmailStr = null;
		                String validPhone = null;
						//check Valid emailId 
						if(emailIdStr != null && emailIdStr.trim().length() > 0  && Utility.validateEmail(emailIdStr)) {
							isValidCustFlag = true; 
							validEmailStr = emailIdStr.trim();
						}
						//Check Valid Phone number
						UserOrganization organization = user !=null ? user.getUserOrganization() : null ; 
						if(phoneStr != null && phoneStr.trim().length() >0 && 	Utility.phoneParse(phoneStr,organization) != null) {
							isValidCustFlag = true; 
							validPhone = phoneStr.trim();
						}
						//Check CustomerId
						if(customerId != null && !customerId.isEmpty())  {
							isValidCustFlag = true; 	
						}

						Contacts contObj = null;
						CouponCodes couponCodes =null;
		                
						if(isValidCustFlag) {
		                       
		                    //Create Contact Object with CoustmerInfo details
		                    contObj = new Contacts();
		                    contObj.setUsers(user);
		                   
		                    //set CustomerId or External Id
		                    if( customerId != null && !customerId.isEmpty()) {
		                        contObj.setExternalId(customerId);
		                    }
		                       
		                    //set EmailId
		                    contObj.setEmailId(validEmailStr);
		                       
		                    //set Mobile Phone
		                    contObj.setMobilePhone(validPhone);
		                   
		                    //check contact Object exist in Db with the Requested users
		                    Users coupOwnerUser = null;
		                    
		                    //get The DB Contact Object
		                    dbContactObj = findContactByUserId(user.getUserId(), contObj, user);
		                   
		                   
		                    if(dbContactObj != null) { // check with jsonUser 
		                        //get CouponCode Object
		                        //couponCodes = couponCodesDao.findCoupCodeByContactId(""+dbContactObj.getContactId(), orgIdLong ,coupCodeStr );
		                       logger.info("1stime  dbConatct Id is  ::"+dbContactObj.getContactId());
		                      //get CouponCode Object
		                    	List<CouponCodes> cCodeLst = couponCodesDao.findCoupCodeLstByContactId(""+dbContactObj.getContactId(), orgIdLong ,coupCodeStr );
		                        
		                        long redeemCount = 0;
		                        
		                        if(cCodeLst != null && cCodeLst.size() >0 ) {
			                        for (CouponCodes eachCC : cCodeLst) {
			                        	if(eachCC.getStatus().equals(Constants.COUP_CODE_STATUS_REDEEMED)) redeemCount ++;
			                        	else couponCodes = eachCC;
										
									}
			                        
			                        if(coupObj.getSingPromoContRedmptLimit() != null && 
			                        		coupObj.getSingPromoContRedmptLimit().longValue() <=  redeemCount) {
			                        	errorCode= 100020;
			                            msg = "Request can't be serviced. Redemption limit set per customer has been reached.";
			                            return null;
			                        }
			                        
		                        }
		                        
		                        if(couponCodes == null) {
		                            
		                        	coupOwnerUser =  usersDao.find(coupObj.getUserId());
		                            if(coupOwnerUser.getUserId() != user.getUserId().longValue()) { //Check with the Promo Created User
		                               
		                                dbContactObj = findContactByUserId(coupOwnerUser.getUserId(), contObj, coupOwnerUser);
		                                
		                                logger.info("2nd time   dbContactObj checking " + dbContactObj.getContactId());
		                                if(dbContactObj != null) {
		                                	//get CouponCode Object
//		                                	couponCodes = couponCodesDao.findCoupCodeByContactId(""+dbContactObj.getContactId(), orgIdLong ,coupCodeStr );
		                                	cCodeLst = couponCodesDao.findCoupCodeLstByContactId(""+dbContactObj.getContactId(), orgIdLong ,coupCodeStr );
		                                	
		                                	redeemCount = 0;
		    		                        
		    		                        if(cCodeLst != null && cCodeLst.size() >0 ) {
		    			                        for (CouponCodes eachCC : cCodeLst) {
		    			                        	if(eachCC.getStatus().equals(Constants.COUP_CODE_STATUS_REDEEMED)) redeemCount ++;
		    			                        	else couponCodes = eachCC;
		    										
		    									}
		    			                        
		    			                        if(coupObj.getSingPromoContRedmptLimit() != null && 
		    			                        		coupObj.getSingPromoContRedmptLimit().longValue() <=  redeemCount) {
		    			                        	errorCode= 100020;
		    			                            msg = "Request can't be serviced. Redemption limit set per customer has been reached.";
		    			                            return null;
		    			                        }
		    		                        }
		                                
		                                
		                                }
		                            }
		                               
		                            if(couponCodes == null) { // check the Contact Org level
		                                List<Contacts> contactList = getAllContactsByOrg(orgIdLong, contObj);
		                               
		                                if(contactList == null || contactList.size() == 0 ) {
		                                    if (!coupObj.getRedemedAutoChk()) {
		                                    	msg = "Error : Redemption limit exceeded.";
		                        				errorCode = 100013;
		                                        return null;
		                                    }
		                                }else {
		                                    //get the CouponCode with help of ContactIds
		                                    String contactIdStr = "";
		                                    for (Contacts contacts : contactList) {
		                                        contactIdStr += contactIdStr.trim().length() == 0 ? ""+contacts.getContactId() :","+contacts.getContactId();
		                                    }
		                                    couponCodes = couponCodesDao.findCoupCodeByContactId(contactIdStr , orgIdLong ,coupCodeStr );
		                                   
		                                    if(couponCodes == null && !coupObj.getRedemedAutoChk() && 
		                                    		coupObj.getRedeemdCount() != null &&  coupObj.getRedeemdCount() <= redeemdCount) {
		                                    	msg = "Error : Redemption limit exceeded.";
		                        				errorCode = 100013;
		                                        return null;
		                                    }
		                                }
		                            }
		                        }
		                    }else if(coupObj.getUserId().longValue() != user.getUserId().longValue()){ // check with Contact Promo Creted User
		                    	
		                    	coupOwnerUser =  usersDao.find(coupObj.getUserId());
		                    	dbContactObj = findContactByUserId(coupOwnerUser.getUserId(), contObj, coupOwnerUser);
		                    	
		                    	logger.info(">>> coupOwnerUser.getUserId() ::"+coupOwnerUser.getUserId()+" :: dbContact Id is  "+dbContactObj);
		                        
		                    	//get CouponCode Object
		                        if(dbContactObj != null) {
		                           
//		                            couponCodes = couponCodesDao.findCoupCodeByContactId(""+dbContactObj.getContactId(), orgIdLong ,coupCodeStr );
		                        	List<CouponCodes> cCodeLst = couponCodesDao.findCoupCodeLstByContactId(""+dbContactObj.getContactId(), orgIdLong ,coupCodeStr );
                                	
                                	long redeemCount = 0;
    		                        
    		                        if(cCodeLst != null && cCodeLst.size() >0 ) {
    			                        for (CouponCodes eachCC : cCodeLst) {
    			                        	if(eachCC.getStatus().equals(Constants.COUP_CODE_STATUS_REDEEMED)) redeemCount ++;
    			                        	else couponCodes = eachCC;
    										
    									}
    			                        
    			                        if(coupObj.getSingPromoContRedmptLimit() != null && 
    			                        		coupObj.getSingPromoContRedmptLimit().longValue() <=  redeemCount) {
    			                        	errorCode= 100020;
    			                            msg = "Request can't be serviced. Redemption limit set per customer has been reached.";
    			                            return null;
    			                        }
    			                        
    		                        }
		                        }
		                        
		                        if(couponCodes == null) { //Check the Contact org level
		                            List<Contacts> contactList = getAllContactsByOrg( orgIdLong, contObj);
		                           
		                            if(contactList == null || contactList.size() == 0){
		                                 if(!coupObj.getRedemedAutoChk()) {
		                                	 msg = "Error : Redemption limit exceeded.";
		                        			 errorCode = 100013;
		                                     //TODO send error message contacts not exists in org level and coupon obje is Redeemcheck is false
		                                     return null;
		                                 }
		                            }else {
		                                //get the CouponCode with help of ContactIds
		                                String contactIdStr = "";
		                                for (Contacts contacts : contactList) {
		                                    contactIdStr += contactIdStr.trim().length() == 0 ? ""+contacts.getContactId() :","+contacts.getContactId();
		                                }
		                                couponCodes = couponCodesDao.findCoupCodeByContactId(contactIdStr , orgIdLong ,coupCodeStr );
		                               
		                                if(couponCodes == null && !coupObj.getRedemedAutoChk() && 
		                                		coupObj.getRedeemdCount() != null &&  coupObj.getRedeemdCount() <= redeemdCount) {
		                                	msg = "Error : Redemption limit exceeded.";
	                        				errorCode = 100013;
		                                    return null;
		                                }
		                            }
		                        }
		                    }else {  //Check the Contact org level
		                    	  List<Contacts> contactList = getAllContactsByOrg(orgIdLong, contObj);
		                          
		                          if(contactList == null || contactList.size() == 0){
		                               if(!coupObj.getRedemedAutoChk()) {
		                            	   msg = "Error : Redemption limit exceeded.";
	                        				errorCode = 100013;
		                                   //TODO send error message contacts not exists in org level and coupon obje is Redeemcheck is false
		                                   return null;
		                               }
		                          }else {
		                              //get the CouponCode with help of ContactIds
		                              String contactIdStr = "";
		                              for (Contacts contacts : contactList) {
		                                  contactIdStr += contactIdStr.trim().length() == 0 ? ""+contacts.getContactId() :","+contacts.getContactId();
		                              }
		                              couponCodes = couponCodesDao.findCoupCodeByContactId(contactIdStr , orgIdLong ,coupCodeStr );
		                             
		                              if(couponCodes == null && !coupObj.getRedemedAutoChk() &&
		                            		  coupObj.getRedeemdCount() != null &&  coupObj.getRedeemdCount() <= redeemdCount) {
		                                  //TODO send error message couponcode not avaialable
		                            	  msg = "Error : Redemption limit exceeded.";
		                            	  errorCode = 100013;
		                                  return null;
		                              }
		                          }
		                    }
		                }else if(couponCodes == null && !coupObj.getRedemedAutoChk() &&
		                		coupObj.getRedeemdCount() != null &&  coupObj.getRedeemdCount() <= redeemdCount) {
		                        //TODO send error message couponcode not avaialable
		                	msg = "Error : Redemption limit exceeded.";
            				errorCode = 100013;
		                        return null;
		                 }
		               
		              
						
					}
															
				}
				else if(coupObj.getCouponGeneratedType().equals(Constants.COUP_GENT_TYPE_MULTIPLE) && 
						coupCodeObj.getStatus().trim().equals(Constants.COUP_CODE_STATUS_REDEEMED)) {
					
					errorCode = 100014;
					msg = "Promo-code already redeemed.";
					return null;
					
				}
				
				
				
				
				JSONArray  purItemJsonArrObj = null;
				
				//check strore number if Coupon Object is SKU type
				if(coupObj.getDiscountCriteria().equals("SKU") ) {
					
					purItemJsonArrObj = (JSONArray)coupCodeEnqJsonObj.get("PURCHASEDITEMS");
					if(purItemJsonArrObj == null) {
						
						logger.debug("Error : unable to find the Promo-code purchase Info in JSON ****");
						msg = "Error : Missing purchase details.";
						errorCode = 100009;
						return null;
					}
					
					if(purItemJsonArrObj.size() == 0) {
						logger.debug("Error : No Item Code Info from JSON ****");
						msg = "Error : Missing purchased items info.";
						errorCode = 100010;
						return null;
					}
					
				}
				
				
				
				
				
				
				//Responce Json 
				coupDisInfoJsonObj = new JSONObject();
				coupDisInfoJsonObj.put("COUPONNAME", coupObj.getCouponName());
				coupDisInfoJsonObj.put("COUPONCODE", coupCodeStr);
				String loyaltyPointsStr = coupObj.getRequiredLoyltyPoits() == null ? "" : ""+coupObj.getRequiredLoyltyPoits();
				coupDisInfoJsonObj.put("LOYALTYPOINTS", loyaltyPointsStr);
						
				
				// COUPON LOYALTY CHECK 
				if(coupObj.getRequiredLoyltyPoits() != null && coupObj.getLoyaltyPoints() != null) {
					coupDisInfoJsonObj.put("COUPONTYPE", "ONLY-LOYALTY"); 
					/*if(coupObj.getLoyaltyPoints() == 0) {
						
						coupDisInfoJsonObj.put("COUPONTYPE", "LOYALTY"); 
					}else {
					}*/
				}else {
					
					coupDisInfoJsonObj.put("COUPONTYPE", "REGULAR");
				}
				
				
				if(coupObj.getExpiryType().equalsIgnoreCase(Constants.COUP_VALIDITY_PERIOD_STATIC)){
					coupDisInfoJsonObj.put("VALIDFROM", MyCalendar.calendarToString(
							coupObj.getCouponCreatedDate(), MyCalendar.FORMAT_DATETIME_STYEAR));
					
					coupDisInfoJsonObj.put("VALIDTO", MyCalendar.calendarToString(coupObj.
													  getCouponExpiryDate(), MyCalendar.FORMAT_DATETIME_STYEAR));
					}
					else{
						coupDisInfoJsonObj.put("VALIDFROM", MyCalendar.calendarToString(
								coupObj.getUserCreatedDate(), MyCalendar.FORMAT_DATETIME_STYEAR));
						Calendar cal1 = Calendar.getInstance();
						cal1.set(2020, 11, 31, 23, 59, 59);
						coupDisInfoJsonObj.put("VALIDTO", MyCalendar.calendarToString(cal1, MyCalendar.FORMAT_DATETIME_STYEAR));
						
					}
				
				boolean isItemCode = false;
				String disCountType ="";
				if(coupObj.getDiscountType().equals("Percentage") && coupObj.getDiscountCriteria().equals("Total Purchase Amount")) {
					
					coupDisInfoJsonObj.put("DISCOUNTCRITERIA", "PERCENTAGE-MVP");
					disCountType = "P";
				}
				else if(coupObj.getDiscountType().equals("Percentage") && coupObj.getDiscountCriteria().equals("SKU")) {
					coupDisInfoJsonObj.put("DISCOUNTCRITERIA", "PERCENTAGE-IC");
					isItemCode = true;
					disCountType = "P";
				}
				else if(coupObj.getDiscountType().equals("Value") && coupObj.getDiscountCriteria().equals("Total Purchase Amount")) {
					coupDisInfoJsonObj.put("DISCOUNTCRITERIA", "VALUE-MVP");
					disCountType = "V";
				}
				else {
					coupDisInfoJsonObj.put("DISCOUNTCRITERIA", "VALUE-I");
					isItemCode = true;
					disCountType = "V";
				}
				
				
				
				Set<String> itemCodeSet = new HashSet<String>();
				if(purItemJsonArrObj != null && purItemJsonArrObj.size() > 0) {
					
					for (Object object : purItemJsonArrObj) {
						if(object instanceof JSONObject){
							
							JSONObject tempObj = (JSONObject)object;
							
							if(isItemCode) {
								itemCodeSet.add(tempObj.get("ITEMCODE").toString());
								
							}
						}
					} //for
				}
				String itemCodeStr = "";
				if(itemCodeSet != null && itemCodeSet.size() > 0){
					
					for (String string : itemCodeSet) {
						if(itemCodeStr.trim().length() > 0) {
							itemCodeStr = itemCodeStr+", '"+string+"'";
						}else {
							itemCodeStr = itemCodeStr +"'"+string+"'";
						}
					}
				}
				
				List<CouponDiscountGeneration>  coupDisList = coupDiscGenDao.findCoupCodeByFlag(coupObj ,itemCodeStr);
				logger.info(">>> coupDisList size  is  "+coupDisList.size());
				
				/*JSONObject disCiuntJson = null;
				JSONArray  disInfJsonArr = new JSONArray();*/
				
				JSONArray itemCodeJsonArr  = new JSONArray();
				
				Map<Double, JSONObject> isSameDisMap = new HashMap<Double, JSONObject>();
				
				if(coupDisList != null && coupDisList.size() > 0 ) {
					JSONObject  tempObj1 = null;
					JSONArray	tempArrObj = null;
					
					for (CouponDiscountGeneration coupDisGenObj : coupDisList) {
						
						
						if(!(isSameDisMap.containsKey(coupDisGenObj.getDiscount()))) {
							tempObj1 = new JSONObject();
							tempObj1.put("VALUE", coupDisGenObj.getDiscount());
							tempObj1.put("VALUECODE", disCountType);
							tempArrObj = new JSONArray();
							JSONObject tempObj2 = new JSONObject();
							
							
							if(isItemCode) {
								
								tempObj1.put("MINPURCHASEVALUE", "");
								tempObj2.put("ITEMCODE", coupDisGenObj.getItemCategory());
								tempArrObj.add(tempObj2);
								
								
							}else {
								
								tempObj1.put("MINPURCHASEVALUE", coupDisGenObj.getTotPurchaseAmount());
//								tempObj2.put("ITEMCODE", "");
								
							}
							tempObj1.put("ITEMCODEINFO", tempArrObj);
							
							isSameDisMap.put(coupDisGenObj.getDiscount(), tempObj1);
							
						}else  {
							
							tempObj1 = (JSONObject)isSameDisMap.get(coupDisGenObj.getDiscount());
							tempArrObj =(JSONArray) tempObj1.get("ITEMCODEINFO");
							
							JSONObject tempObj2 = new JSONObject();
							tempObj2.put("ITEMCODE", coupDisGenObj.getItemCategory());
							tempArrObj.add(tempObj2);
							tempObj1.put("ITEMCODEINFO", tempArrObj);
							
						}
						
						
					} // for
					
					Set<Double> keySet = isSameDisMap.keySet();
					for (Double double1 : keySet) {
						itemCodeJsonArr.add(isSameDisMap.get(double1));
					}
//					itemCodeJsonArr.add(tempObj1);
					
					errorCode = 0;
					msg = "Promo-code enquiry is successful.";
					
					
					coupDisInfoJsonObj.put("DISCOUNTINFO", itemCodeJsonArr);
					coupDisInfoJsonArrObj.add(coupDisInfoJsonObj);
					
				}else {
					msg = "Error : This promo-code is not applicable to any of the purchased items.";
					errorCode = 100016;
					JSONArray noItemCodeObj = new JSONArray();
					coupDisInfoJsonObj.put("DISCOUNTINFO", noItemCodeObj);
					coupDisInfoJsonArrObj.add(coupDisInfoJsonObj);
					return null;
				}
				
			}else {
				
				List<Coupons> listPromoCoupons = couponsDao.listOfSinglePromoCoupons(user.getUserOrganization().getUserOrgId());
				//CouponCodes  coupCodeObj = couponCodesDao.testForCouponCodes(coupCodeStr, user.getUserOrganization().getUserOrgId());
				if(listPromoCoupons == null || listPromoCoupons.size() == 0 ) {
					msg = "Error : No promo-code found for redemption with loyalty points.";
					errorCode = 100019;
					return null;
				}
				String coupIdStr = "";
				for (Coupons coupons : listPromoCoupons) {
					if(coupIdStr.trim().length()  ==  0) coupIdStr = ""+coupons.getCouponId();
					else coupIdStr += ","+coupons.getCouponId();
				}
				
				List<CouponDiscountGeneration>  coupDisList  = coupDiscGenDao.findCoupCodesByCouponObj(coupIdStr);
				Map<Long , List<CouponDiscountGeneration>> setOfCoupDisMap = new HashMap<Long, List<CouponDiscountGeneration>>();
				List<CouponDiscountGeneration> tempCoupDisList = null;
				logger.debug("coupDisGenList size is :: "+coupDisList.size());
				
				for (CouponDiscountGeneration eachCoupDisGenObj : coupDisList) {
					
					if(setOfCoupDisMap.containsKey(eachCoupDisGenObj.getCoupons().getCouponId())){
						tempCoupDisList = setOfCoupDisMap.get(eachCoupDisGenObj.getCoupons().getCouponId());
						tempCoupDisList.add(eachCoupDisGenObj);
					}else{
						tempCoupDisList = new ArrayList<CouponDiscountGeneration>();
						tempCoupDisList.add(eachCoupDisGenObj);
					}
					setOfCoupDisMap.put(eachCoupDisGenObj.getCoupons().getCouponId(), tempCoupDisList);
				}
				
				Set<Long> coupObjIdSet = setOfCoupDisMap.keySet();
				for (Long eachCoupId : coupObjIdSet) {
					tempCoupDisList = setOfCoupDisMap.get(eachCoupId);
					Coupons eachCouponObj = ((CouponDiscountGeneration)tempCoupDisList.get(0)).getCoupons();
					
				/*}
				
				for (Coupons eachCouponObj : listPromoCoupons) {*/
					
					coupDisInfoJsonObj = new JSONObject();
					coupDisInfoJsonObj.put("COUPONNAME", eachCouponObj.getCouponName());
					coupDisInfoJsonObj.put("COUPONCODE", eachCouponObj.getCouponCode());
					String loyaltyPointsStr = eachCouponObj.getRequiredLoyltyPoits() == null ? "" : ""+eachCouponObj.getRequiredLoyltyPoits();
					coupDisInfoJsonObj.put("LOYALTYPOINTS", loyaltyPointsStr);
					
					if(eachCouponObj.getRequiredLoyltyPoits() != null && eachCouponObj.getLoyaltyPoints() != null) {
						coupDisInfoJsonObj.put("COUPONTYPE", "ONLY-LOYALTY"); 
						
					}else {
						
						coupDisInfoJsonObj.put("COUPONTYPE", "REGULAR");
					}
					
					
					if(eachCouponObj.getExpiryType().equalsIgnoreCase(Constants.COUP_VALIDITY_PERIOD_STATIC)){
					coupDisInfoJsonObj.put("VALIDFROM", MyCalendar.calendarToString(
							eachCouponObj.getCouponCreatedDate(), MyCalendar.FORMAT_DATETIME_STYEAR));
					
					coupDisInfoJsonObj.put("VALIDTO", MyCalendar.calendarToString(eachCouponObj.
													  getCouponExpiryDate(), MyCalendar.FORMAT_DATETIME_STYEAR));
					}
					else{
						coupDisInfoJsonObj.put("VALIDFROM", MyCalendar.calendarToString(
								eachCouponObj.getUserCreatedDate(), MyCalendar.FORMAT_DATETIME_STYEAR));
						Calendar cal = Calendar.getInstance();
						cal.set(2020, 11, 31, 23, 59, 59);
						coupDisInfoJsonObj.put("VALIDTO", MyCalendar.calendarToString(cal, MyCalendar.FORMAT_DATETIME_STYEAR));
						
					}
					
					
					//////////////////////////////////////////////////////
					
					boolean isItemCode = false;
					String disCountType ="";
					if(eachCouponObj.getDiscountType().equals("Percentage") && eachCouponObj.getDiscountCriteria().equals("Total Purchase Amount")) {
						
						coupDisInfoJsonObj.put("DISCOUNTCRITERIA", "PERCENTAGE-MVP");
						disCountType = "P";
					}
					else if(eachCouponObj.getDiscountType().equals("Percentage") && eachCouponObj.getDiscountCriteria().equals("SKU")) {
						coupDisInfoJsonObj.put("DISCOUNTCRITERIA", "PERCENTAGE-IC");
						isItemCode = true;
						disCountType = "P";
					}
					else if(eachCouponObj.getDiscountType().equals("Value") && eachCouponObj.getDiscountCriteria().equals("Total Purchase Amount")) {
						coupDisInfoJsonObj.put("DISCOUNTCRITERIA", "VALUE-MVP");
						disCountType = "V";
					}
					else {
						coupDisInfoJsonObj.put("DISCOUNTCRITERIA", "VALUE-I");
						isItemCode = true;
						disCountType = "V";
					}
					
					//List<CouponDiscountGeneration>  coupDisList = coupDiscGenDao.findCoupCodesByCouponObj(eachCouponObj);
					//logger.debug("Coupon Name is ::"+eachCouponObj.getCouponName()+" :: coup Dis Size is ::"+tempCoupDisList.size());
					
					JSONArray itemCodeJsonArr  = new JSONArray();
					
					Map<Double, JSONObject> isSameDisMap1 = new HashMap<Double, JSONObject>();
					
					isSameDisMap1.clear();
					
					if(tempCoupDisList != null && tempCoupDisList.size() > 0 ) {
						
						JSONObject  tempObj1 = null;
						JSONArray	tempArrObj = null;
						boolean flag = false;
						for (CouponDiscountGeneration coupDisGenObj : tempCoupDisList) {
							
							if(!(isSameDisMap1.containsKey(coupDisGenObj.getDiscount()))) {
								
								tempObj1 = new JSONObject();
								flag = true;
								
								tempObj1.put("VALUE", coupDisGenObj.getDiscount());
								tempObj1.put("VALUECODE", disCountType);
								tempArrObj = new JSONArray();
								JSONObject tempObj2 = new JSONObject();
								
								
								if(isItemCode) {
									
									tempObj1.put("MINPURCHASEVALUE", "");
									tempObj2.put("ITEMCODE", coupDisGenObj.getItemCategory());
									tempArrObj.add(tempObj2);
									
									
								}else {
									
									tempObj1.put("MINPURCHASEVALUE", coupDisGenObj.getTotPurchaseAmount());
//									tempObj2.put("ITEMCODE", "");
									
								}
								tempObj1.put("ITEMCODEINFO", tempArrObj);
								isSameDisMap1.put(coupDisGenObj.getDiscount(), tempObj1);
								
							}else  {
								logger.debug(">>is second time  ::"+isSameDisMap1);
								tempObj1 = (JSONObject)isSameDisMap1.get(coupDisGenObj.getDiscount());
								tempArrObj =(JSONArray) tempObj1.get("ITEMCODEINFO");
								
								JSONObject tempObj2 = new JSONObject();
								tempObj2.put("ITEMCODE", coupDisGenObj.getItemCategory());
								tempArrObj.add(tempObj2);
								tempObj1.put("ITEMCODEINFO", tempArrObj);
								isSameDisMap1.put(coupDisGenObj.getDiscount(), tempObj1);
								
							}
//							logger.debug("add to here >>> tempObj1 is"+tempObj1);
						} // for
						
						Set<Double> keySet = isSameDisMap1.keySet();
						for (Double double1 : keySet) {
							itemCodeJsonArr.add(isSameDisMap1.get(double1));
						}
						errorCode = 0;
						msg = "Promo-code enquiry is successful.";
						
						
						coupDisInfoJsonObj.put("DISCOUNTINFO", itemCodeJsonArr);
						coupDisInfoJsonArrObj.add(coupDisInfoJsonObj);
						
					}
					
					
//					coupDisInfoJsonArrObj.add(coupDisInfoJsonObj);
					
				}
				errorCode = 0;
				msg = "Promo-code enquiry is successful.";
			}
		} catch (Exception e) {
			logger.error("Exception ::" , e);
		}
		
		finally {
			
			JSONObject respJsonObj = new JSONObject();
			
			
			JSONObject coupCodeRespObj = new JSONObject();
			respJsonObj.put("COUPONCODERESPONSE", coupCodeRespObj);
			
			if(coupDisInfoJsonArrObj != null){
				coupCodeRespObj.put("COUPONDISCOUNTINFO", coupDisInfoJsonArrObj);
			}
			
			
			String statusStr= "";
			
			if(errorCode == 0) {
				statusStr = "Success";
			}else {
				statusStr = "Failure";
			}
			
			JSONObject statusObj = new JSONObject();
			
			statusObj.put("ERRORCODE", errorCode);
			statusObj.put("MESSAGE", msg);
			statusObj.put("STATUS", statusStr);
			coupCodeRespObj.put("STATUSINFO", statusObj);
			
			//coupCodeRespObj.put("COUPONCODEINFO", coupCodejsonObj);
			coupCodeRespObj.put("HEADERINFO", headerInfoJson);
			
			PrintWriter pw = response.getWriter();
			logger.debug("Response json object : "+ respJsonObj);
			pw.write(respJsonObj.toJSONString());
			
			pw.flush();
			pw.close();
			
		}
		
		return null;
		
	} // handleRequestInternal
	
	private Contacts findContactByUserId(Long userId, Contacts contObj, Users user){
        TreeMap<String, List<String>> prioMap =  Utility.getPriorityMap(userId, Constants.POS_MAPPING_TYPE_CONTACTS, posMappingDao);
        if(prioMap == null) return null;
        return contObj = contactsDao.findContactByUniqPriority(prioMap,contObj, userId, user);
       
    }
   
    private List<Contacts> getAllContactsByOrg(Long orgId, Contacts contObj) {
       
        List<Contacts> contactList = null;
        //check the list of Contacts from Organization
        List<Users> orgUserList = usersDao.getUsersListByOrg(orgId);
        Map<Long, TreeMap<String, List<String>>> usersTreeMap = Utility.getPriorityMapByUsersList(orgUserList,Constants.POS_MAPPING_TYPE_CONTACTS, posMappingDao);
       
        return  contactList = contactsDao.findContactsByUserList(contObj, orgUserList, usersTreeMap);
    } // getAllContactsByOrg
	
	
	
    

    
    
    
    
}
