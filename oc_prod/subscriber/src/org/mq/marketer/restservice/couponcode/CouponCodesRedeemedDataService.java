 package org.mq.marketer.restservice.couponcode;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.mq.marketer.campaign.beans.Contacts;
import org.mq.marketer.campaign.beans.CouponCodes;
import org.mq.marketer.campaign.beans.Coupons;
import org.mq.marketer.campaign.beans.CustomTemplates;
import org.mq.marketer.campaign.beans.EmailQueue;
import org.mq.marketer.campaign.beans.MyTemplates;
import org.mq.marketer.campaign.beans.UserOrganization;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.service.CaptiwayToSMSApiGateway;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.ContactsDao;
import org.mq.marketer.campaign.dao.CouponCodesDao;
import org.mq.marketer.campaign.dao.CouponCodesDaoForDML;
import org.mq.marketer.campaign.dao.CouponsDao;
import org.mq.marketer.campaign.dao.CouponsDaoForDML;
import org.mq.marketer.campaign.dao.CustomTemplatesDao;
import org.mq.marketer.campaign.dao.EmailQueueDao;
import org.mq.marketer.campaign.dao.EmailQueueDaoForDML;
import org.mq.marketer.campaign.dao.MailingListDao;
import org.mq.marketer.campaign.dao.MyTemplatesDao;
import org.mq.marketer.campaign.dao.POSMappingDao;
import org.mq.marketer.campaign.dao.SMSSettingsDao;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.PurgeList;
import org.mq.marketer.campaign.general.Utility;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;
import org.zkoss.zkplus.spring.SpringUtil;

public class CouponCodesRedeemedDataService  extends AbstractController{
    //private static Logger logger = Logger.getLogger(CouponCodesRedeemedDataService.class);
    private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
    private ContactsDao contactsDao;
    private UsersDao usersDao;

    private CouponsDao couponsDao;
    private CouponsDaoForDML couponsDaoForDML;
    public CouponsDaoForDML getCouponsDaoForDML() {
		return couponsDaoForDML;
	}



	public void setCouponsDaoForDML(CouponsDaoForDML couponsDaoForDML) {
		this.couponsDaoForDML = couponsDaoForDML;
	}



	private CouponCodesDao couponCodesDao;
    private CouponCodesDaoForDML couponCodesDaoForDML;
    public CouponCodesDaoForDML getCouponCodesDaoForDML() {
		return couponCodesDaoForDML;
	}

	public void setCouponCodesDaoForDML(CouponCodesDaoForDML couponCodesDaoForDML) {
		this.couponCodesDaoForDML = couponCodesDaoForDML;
	}
  
    private MailingListDao mailingListDao;
    private POSMappingDao posMappingDao;
    private EmailQueueDao emailQueueDao;
    private EmailQueueDaoForDML emailQueueDaoForDML;
    public EmailQueueDaoForDML getEmailQueueDaoForDML() {
		return emailQueueDaoForDML;
	}



	public void setEmailQueueDaoForDML(EmailQueueDaoForDML emailQueueDaoForDML) {
		this.emailQueueDaoForDML = emailQueueDaoForDML;
	}



	private CustomTemplatesDao customTemplatesDao;
   
   
   /* private EventTriggerEventsObserver eventTriggerEventsObserver;
    private EventTriggerEventsObservable eventTriggerEventsObservable;*/
   
   
    public EmailQueueDao getEmailQueueDao() {
		return emailQueueDao;
	}



	public void setEmailQueueDao(EmailQueueDao emailQueueDao) {
		this.emailQueueDao = emailQueueDao;
	}



	public CustomTemplatesDao getCustomTemplatesDao() {
		return customTemplatesDao;
	}



	public void setCustomTemplatesDao(CustomTemplatesDao customTemplatesDao) {
		this.customTemplatesDao = customTemplatesDao;
	}



	public static Logger getLogger() {
		return logger;
	}



	public CouponCodesRedeemedDataService(){
       
    }

   

    public ContactsDao getContactsDao() {
        return contactsDao;
    }

    public void setContactsDao(ContactsDao contactsDao) {
        this.contactsDao = contactsDao;
    }

    public UsersDao getUsersDao() {
        return usersDao;
    }

    public void setUsersDao(UsersDao usersDao) {
        this.usersDao = usersDao;
    }

    public CouponsDao getCouponsDao() {
        return couponsDao;
    }

    public void setCouponsDao(CouponsDao couponsDao) {
        this.couponsDao = couponsDao;
    }

    public CouponCodesDao getCouponCodesDao() {
        return couponCodesDao;
    }

    public void setCouponCodesDao(CouponCodesDao couponCodesDao) {
        this.couponCodesDao = couponCodesDao;
    }
   
   
    public MailingListDao getMailingListDao() {
        return mailingListDao;
    }



    public void setMailingListDao(MailingListDao mailingListDao) {
        this.mailingListDao = mailingListDao;
    }
   
   
   
    private SMSSettingsDao smsSettingsDao;
   
    public SMSSettingsDao getSmsSettingsDao() {
        return smsSettingsDao;
    }

    public void setSmsSettingsDao(SMSSettingsDao smsSettingsDao) {
        this.smsSettingsDao = smsSettingsDao;
    }
   

   
    private PurgeList purgeList;
   
   
   
   
    public PurgeList getPurgeList() {
        return purgeList;
    }



    public void setPurgeList(PurgeList purgeList) {
        this.purgeList = purgeList;
    }

    public POSMappingDao getPosMappingDao() {
        return posMappingDao;
    }



    public void setPosMappingDao(POSMappingDao posMappingDao) {
        this.posMappingDao = posMappingDao;
    }

    private CaptiwayToSMSApiGateway captiwayToSMSApiGateway;
   
    public CaptiwayToSMSApiGateway getCaptiwayToSMSApiGateway() {
        return captiwayToSMSApiGateway;
    }

    public void setCaptiwayToSMSApiGateway(
            CaptiwayToSMSApiGateway captiwayToSMSApiGateway) {
        this.captiwayToSMSApiGateway = captiwayToSMSApiGateway;
    }
    
    private MyTemplatesDao myTemplatesDao;
   
    public MyTemplatesDao getMyTemplatesDao() {
		return myTemplatesDao;
	}

	public void setMyTemplatesDao(MyTemplatesDao myTemplatesDao) {
		this.myTemplatesDao = myTemplatesDao;
	}



	protected ModelAndView handleRequestInternal(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
//        boolean isEnableEvent = false;
        Long startId = null;
        Long endId = null;

        //register the observable with the observer
//        eventTriggerEventsObservable.addObserver(eventTriggerEventsObserver);
       
   
        PrintWriter pw = response.getWriter();
        response.setContentType("application/json");
       
        JSONObject jsonHeader = null;
        JSONObject userJSONObj = null;
//        JSONObject customerInfo = null;
        JSONObject customerCouponCodesDetails = null;
        JSONObject jsonCouponRedeemedDetails = null;
       
        JSONObject jsonResponseObject = new JSONObject();
       
        String requestId = null;
        String userName = null;
        String userOrg = null;
        String userToken = null;
        String couponName = null;
        String reqCoupcode= null;
        String totalAmount = null;
        String totalDiscount= null;
        String usedLoyaltyPoints= null;
        String email= null;
        String phone=null;
        String custId = null;
        String sourceType = null;
       
        String msg ="";
        int errorCode = -1;
       
        try {
            // print all parameters
           
            Enumeration<String> enumerator = request.getParameterNames();
            logger.debug("Printing all req parametes :");
           logger.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
            while(enumerator.hasMoreElements()) {
               
                String reqParaName = enumerator.nextElement();
               
                logger.debug(" QUERY PARAMETERS  >>> : ");
                logger.debug("parameters  : " + reqParaName);
                logger.debug(" Value : " +request.getParameter(reqParaName));
               
            }
           
           
            // check if jsonVal parameter  value is there

            JSONObject jsonRootObject = null;
           
            if(request.getParameter("jsonVal") != null && request.getParameter("jsonVal").length() > 0) {
                String jsonValStr = request.getParameter("jsonVal");
                jsonRootObject = (JSONObject)JSONValue.parse(jsonValStr);
                logger.debug("--jsonValStr--- >>>"+jsonValStr);
                if(jsonRootObject == null) {
                   
                    logger.debug("Error : Invalid json Object .. Returning. *****");
                    msg = "Error : Invalid json Object. Returning. ****";
                    errorCode = 100901;
                    return null;
                }
           
            }
            else {
                // stream Test
                  InputStream is = request.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                char[] chr = new char[1024];
                int bytesRead = 0;
                StringBuilder sb = new StringBuilder();
               
                while ((bytesRead = br.read(chr)) > 0) {
                     sb.append(chr, 0, bytesRead);
                }
                logger.debug("Rest body value is "+ sb.toString());
                try {
                   
                    jsonRootObject = (JSONObject)JSONValue.parse(sb.toString());
                } catch(Exception e) {
                   
                    logger.debug("Error : Invalid json Object .. Returning. ****");
                    msg = "Error : Invalid json Object. Returning. *****";
                    errorCode = 100902;
                    return null;
                }       
            }
           
            logger.debug("jsonRootOibject is   >>"+jsonRootObject);
            if(jsonRootObject == null) {
               
                logger.debug("Error : Unable to parse the json.. Returning. ****");
                msg = "Error : Unable to parse the json. Returning. ****";
                errorCode = 100903;
                return null;
            }
           
            // couponCodesRedeemedRequest
           
            JSONObject jsonMainObject =  (JSONObject)jsonRootObject.get("COUPONCODEREDEEMREQ");
           
            if(jsonMainObject == null) {
               
                logger.debug("Error : unable to find the Promo-code redeemed info in JSON ****");
                msg = "Error : Unable to find Promo-code Redeemed info in JSON.";
                errorCode = 100904;
                return null;
            }
           
           
           
            //Requeste Id info
           
            jsonHeader = (JSONObject)jsonMainObject.get("HEADERINFO");
           
            if(jsonHeader == null) {
               
                logger.debug("Error : unable to find the HearderInfo in JSON ****");
                msg = "Error : Unable to find the Header info in JSON.";
                errorCode = 100905;
                return null;
            }
           
            requestId = jsonHeader.get("REQUESTID").toString().trim();
           
            if(requestId == null || requestId.trim().length() == 0) {
               
                logger.debug("Error : Request ID of HearderInfo is empty in JSON ****");
                msg = "Error : Request ID of Header info in JSON should not be empty.";
                errorCode = 100906;
                return null;
               
               
            }
            // User Details info
           
            userJSONObj = (JSONObject)jsonMainObject.get("USERDETAILS");
           
            logger.debug("User Details is "+ userJSONObj);
           
            if(userJSONObj == null) {
               
                logger.debug("Error : unable to find the User Details in JSON ****");
                msg = "Error : Unable to find user details in JSON.";
                errorCode = 100907;
                return null;
            }
            //Token info
            userToken = userJSONObj.get("TOKEN") == null ? "" : userJSONObj.get("TOKEN").toString().trim();
           
           
            if(userToken.isEmpty()) {
               
                logger.debug("Error : User Token cannot be empty.");
                msg = "Error :  User token cannot be empty.";
                errorCode = 100908;
                return null;
            }
           
            //user name & User org info
            userName = userJSONObj.get("USERNAME") == null ? "" : userJSONObj.get("USERNAME").toString().trim();
            userOrg = userJSONObj.get("ORGID") == null ? "" : userJSONObj.get("ORGID").toString().trim();
           
            //passwrd = userJSONObj.get("PASSWORD").toString();
           
            if(userName.isEmpty() || userOrg.isEmpty()) {
               
                logger.debug("Error : Username or organisation cannot be empty.");
                msg = "Error :  Username or organisation cannot be empty.";
                errorCode = 100909;
                return null;
            }
           logger.debug("string val...."+userName+ Constants.USER_AND_ORG_SEPARATOR +userOrg);
            Users user = usersDao.findByToken(userName+ Constants.USER_AND_ORG_SEPARATOR +userOrg, userToken );
            if(user == null) {
               
                logger.debug("Unable to find the user Obj");
                msg = "Error : Unable to find the user details.";
                errorCode = 100910;
                return null;
            }
            Long orgId = user.getUserOrganization().getUserOrgId();
            //customerCouponcodesinfo
           
            customerCouponCodesDetails=(JSONObject)jsonMainObject.get("COUPONCODEINFO");
           
           
            if(customerCouponCodesDetails == null) {
               
                //TODO
                logger.debug("Unable to find the Promo-code Details ");
                msg = "Error : Unable to find the Promo-code details.";
                errorCode = 100911;
               
                return null;
               
            }
           
            reqCoupcode =  customerCouponCodesDetails.get("COUPONCODE").toString().trim();
            custId = customerCouponCodesDetails.get("CUSTOMERID") == null ? "" : customerCouponCodesDetails.get("CUSTOMERID").toString().trim();
            email = customerCouponCodesDetails.get("EMAIL") == null ? null : customerCouponCodesDetails.get("EMAIL").toString().trim();
            phone = customerCouponCodesDetails.get("PHONE") == null ? "" : customerCouponCodesDetails.get("PHONE").toString().trim();
            sourceType = customerCouponCodesDetails.get("SOURCETYPE") == null ? sourceType : customerCouponCodesDetails.get("SOURCETYPE").toString().trim();
            
            String redeemStr = null;
            if(custId != null && custId.trim().length() >0){
            	redeemStr = "Customer Id:"+custId;
            }
            if(email != null && email.trim().length() >0){
            	if(redeemStr != null && redeemStr.trim().length() >0) redeemStr += ";Email:"+email;
            	else redeemStr = "Email:"+email;
            }
            if(phone != null && phone.trim().length() >0){
            	if(redeemStr != null && redeemStr.trim().length() >0) redeemStr += ";Phone:"+phone;
            	else redeemStr = "Phone:"+phone;
            }
            logger.info(">>> redeem to is  ::"+redeemStr);
            if(reqCoupcode == null || reqCoupcode.trim().length()== 0) {
               
                logger.debug("Unable to find the Promo-code  ");
                msg = "Error : Unable to find the Promo-code.";
                errorCode = 100912;
                return null;
               
               
            }
           
           
            jsonCouponRedeemedDetails = (JSONObject)jsonMainObject.get("PURCHASECOUPONINFO");
           
            if(jsonCouponRedeemedDetails == null) {
               
                logger.debug("Error : unable to find the  required purchase details in JSON ****");
                msg = "Error : Unable to find the purchase details in JSON.";
                errorCode = 100913;
                return null;
            }
           
            totalAmount = jsonCouponRedeemedDetails.get("TOTALAMOUNT")== null? "" :jsonCouponRedeemedDetails.get("TOTALAMOUNT").toString();       
           
            if(totalAmount == null || totalAmount.isEmpty() ){
                logger.debug("Error : unable to find the  required total amount  details in JSON ****");
                msg = "Error : Unable to find the total amount in JSON.";
                errorCode = 100914;
                return null;
               
            }
            // totalDiscount
            totalDiscount= jsonCouponRedeemedDetails.get("TOTALDISCOUNT") == null? "":jsonCouponRedeemedDetails.get("TOTALDISCOUNT").toString();
            if(totalDiscount == null || totalDiscount.isEmpty()) {
               
                logger.debug("Error : unable to find the  required total discount  details in JSON ****");
                msg = "Error : Unable to find the total discount in JSON.";
                errorCode = 100915;
                return null;
               
            }
            usedLoyaltyPoints =jsonCouponRedeemedDetails.get("USEDLOYALTYPOINTS") == null? "":jsonCouponRedeemedDetails.get("USEDLOYALTYPOINTS").toString();
          
            double totAmt = 0;
               
            try {
               
                totAmt = Double.parseDouble(totalAmount);
               
            } catch (NumberFormatException e) {
                // TODO Auto-generated catch block

                logger.debug("Error :  Value for Total Ammount  shoulb be a Number in JSON ****");
                msg = " Format Error : Value of total amount should be a number in JSON.";
                errorCode = 100916;
                return null;
            }
           
            double totDis=0;
            try{
                totDis = Double.parseDouble(totalDiscount);
            }catch(NumberFormatException e){
                // TODO Auto-generated catch block
                logger.debug("Error : Value for Total Discount  shoulb be a Number in JSON ****");
                msg = " Format Error : Value of total discount should be a number in JSON.";
                errorCode = 100917;
                return null;
               
            }
            // couponId
            CouponCodes couponCodes = couponCodesDao.testForCouponCodes(reqCoupcode, orgId);
           
            
            logger.debug(">>>>>>>>>>>>>>>  couponCodes is "+couponCodes);
            Coupons coupObj =null;
            if(couponCodes == null){
               
                coupObj = couponsDao.findCoupon(reqCoupcode,orgId);
                if(coupObj == null){
                logger.debug("Error : Promo-code not exist in DB ****");
                msg = " Format Error : Invalid Promo-code in JSON.";
                errorCode = 100918;
                return null;
                }
            }else{
           
            // single coupon details
             coupObj =couponCodes.getCouponId();
            }
            
            String docSidStr = customerCouponCodesDetails.get("DOCSID") == null ? null : customerCouponCodesDetails.get("DOCSID").toString().trim();
            
            
           couponName = coupObj.getCouponName() != null && !coupObj.getCouponName().isEmpty() ? coupObj.getCouponName() : null; 
            if(coupObj != null && coupObj.getCouponGeneratedType().equalsIgnoreCase(Constants.COUP_GENT_TYPE_SINGLE) && 
					!coupObj.getCouponCode().equalsIgnoreCase(reqCoupcode)) {
				logger.debug("Error : unable to find thePromo-code Info  in JSON ****");
				msg = "Error : Invalid  promo-code.";
				errorCode = 100918;
				return null;
			}
           
            if(couponCodes != null ) {
            	if(coupObj.getCouponGeneratedType().equalsIgnoreCase(Constants.COUP_GENT_TYPE_SINGLE) && 
            			!couponCodes.getCouponCode().equalsIgnoreCase(reqCoupcode)) {
            		logger.debug("Error : promo code is not macthing with request couponcode  of json coupon "+reqCoupcode);
                	msg = "Error : Invalid  promo-code.";
                	errorCode = 100918;
                	return null;
            	}
            	else if(coupObj.getCouponGeneratedType().equalsIgnoreCase(Constants.COUP_GENT_TYPE_MULTIPLE) && 
            			couponCodes.getCouponCode().length() == 8 && !couponCodes.getCouponCode().equalsIgnoreCase(reqCoupcode)) {
            		logger.debug("Error : promo code is not macthing with request couponcode  of json coupon "+reqCoupcode);
                	msg = "Error : Invalid  promo-code.";
                	errorCode = 100918;
                	return null;
            	}
            	else if(coupObj.getCouponGeneratedType().equalsIgnoreCase(Constants.COUP_GENT_TYPE_MULTIPLE) && 
            			couponCodes.getCouponCode().length() != 8 && !couponCodes.getCouponCode().equals(reqCoupcode)) {
            		logger.debug("Error : promo code is not macthing with request couponcode  of json coupon "+reqCoupcode);
                	msg = "Error : Invalid  promo-code.";
                	errorCode = 100918;
                	return null;
            	}
            }
           
            double usedLoyalty = 0;
           
            // check for coupon status as expiry
            if(coupObj.getExpiryType().equals(Constants.COUP_VALIDITY_PERIOD_STATIC)){
            if(coupObj.getStatus().equals(Constants.COUP_STATUS_ACTIVE) ) {
            	msg = " Format Error : Promo-code can't be redeemed. Its validity is set in future dates.(Promotion status is "+coupObj.getStatus()+")";
                errorCode = 100931;
                return null;
            }
            }
            
            if(!coupObj.getStatus().equals(Constants.COUP_STATUS_RUNNING) ){
               
                logger.debug("Error : Promo-code Object cannot be redeemed ****");
                msg = " Format Error : This promo-code cannot be redeemed (Promotion status is "+coupObj.getStatus()+")";
                errorCode = 100919;
                return null;
               
            }
            
            if(coupObj.getExpiryType().equals(Constants.COUP_VALIDITY_PERIOD_DYNAMIC)){
            if(couponCodes.getStatus().equals(Constants.COUP_STATUS_EXPIRED)){
            	msg = "Error : Promotion expired.";
            	errorCode = 100015;
            	return null;
            }
            }
            
            //StoreNumber validity
          //Check with store number
			logger.info(" >>>>>>>>>>>> coupObj.getAllStoreChk() is  ::"+coupObj.getAllStoreChk());
			String strorNumberStr = null;
			try{
			strorNumberStr = customerCouponCodesDetails.get("STORENUMBER").toString().trim();
			}catch(NullPointerException e){
				strorNumberStr = null;
			}
			if((coupObj.getAllStoreChk() != null && coupObj.getAllStoreChk() == false) ||
					(coupObj.getSelectedStores() != null && coupObj.getSelectedStores().trim().length() > 0)) {
				if(customerCouponCodesDetails.containsKey("STORENUMBER")) {
					
					strorNumberStr = customerCouponCodesDetails.get("STORENUMBER").toString().trim();
					
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
						logger.error("Exception :: occured while parsing the store number");
					}
					
					if(eachStore.equals(strorNumberStr.trim())) {
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
            boolean newCC = false;
            Long contactId = null;
            if(coupObj.getCouponGeneratedType().equals(Constants.COUP_GENT_TYPE_SINGLE)) {
            	
                logger.debug(" coupon type is ::"+coupObj.getCouponGeneratedType());
               
                if(coupObj.getLoyaltyPoints() != null && usedLoyaltyPoints.trim().length() > 0){
                   
                    try {
                        logger.debug(" :::::::::::::: used loyalty points ");

                        usedLoyalty = Double.parseDouble(usedLoyaltyPoints);
                   
                        } catch (NumberFormatException e) {
                            logger.debug("Error :  Value for Used Loyalty Points  shoulb be a Number in JSON ****");
                            msg = " Format Error : Value of  Used Loyalty Points should be a number in JSON.";
                            errorCode = 100929;
                            return null;
                    }
                }
                
                //Check for same docsid
                if(docSidStr != null && !docSidStr.isEmpty()) {
                	couponCodes = couponCodesDao.findByDocSid(docSidStr, reqCoupcode, orgId);
                	if(couponCodes != null) {
                		coupObj.setTotRevenue(coupObj.getTotRevenue() != null ? coupObj.getTotRevenue().longValue()-couponCodes.getTotRevenue().doubleValue()+totAmt : totAmt);
                		couponCodes.setRedeemedOn(Calendar.getInstance());
     	                couponCodes.setTotDiscount(totDis);
     	                couponCodes.setTotRevenue(totAmt);
     	                couponCodes.setUsedLoyaltyPoints(usedLoyalty);
     	                couponCodes.setSourceType(sourceType);
     	                if(docSidStr != null && docSidStr.trim().length() > 0) couponCodes.setDocSid(docSidStr); 
     	                
     	                if(redeemStr != null && redeemStr.trim().length() > 0)
     	                		couponCodes.setRedeemedTo(redeemStr);
     	                if(custId != null && custId.trim().length() > 0)
     	                		couponCodes.setRedeemCustId(custId);
     	                if(email != null && email.trim().length() > 0)
     	                		couponCodes.setRedeemEmailId(email);
     	                if(phone != null && phone.trim().length() > 0)
     	                		couponCodes.setRedeemPhnId(phone);
     	                
     	                logger.debug("CouponCode  Obj is ::"+couponCodes);
     	                //couponCodesDao.saveOrUpdate(couponCodes);
     	                couponCodesDaoForDML.saveOrUpdate(couponCodes);
     	                //couponsDao.saveOrUpdate(coupObj);
     	               couponsDaoForDML.saveOrUpdate(coupObj);
     	               logger.info(" Promo updated sucessfully  ");
     	              if(coupObj.getLoyaltyPoints()!=null){
        	               couponsDaoForDML.updateUsedLoyaltyPoint(coupObj.getCouponId());
     	              }
     	                errorCode= 0;
     	                msg = "Promo updated sucessfully";
     	                return null;
                	}
                }
               
                long redeemdCount = couponCodesDao.findRedeemdCoupCodeByCoup(coupObj.getCouponId(), orgId, Constants.COUP_CODE_STATUS_REDEEMED);
                logger.debug("redeemdCount ::"+redeemdCount+">>coupObj.getRedemedAutoChk()::"+coupObj.getRedemedAutoChk());
                logger.debug("coupObj.getRedeemdCount() >>"+coupObj.getRedeemdCount());
               
                if(coupObj.getRedemedAutoChk()== false &&
                        (coupObj.getRedeemdCount() != null &&  coupObj.getRedeemdCount() <= redeemdCount )) {
                   
                        errorCode = 100921;
                        msg = "Redeemed limit has exceeded.";
                        return null;
                       
                }
                   
                couponCodes=null;
               
//                String issueTo = null;//specifies to whome this cc has issued
                
//	            if(!coupObj.getAllStoreChk()) {
	                	
	                boolean isValidCustFlag = false;
	                String validEmailStr = null;
	                String validPhone = null;
	//                boolean newCoupCode = false;
	                if(email != null && email.trim().length() > 0  && Utility.validateEmail(email)) {
	                	isValidCustFlag = true;
	                	validEmailStr = email;
	                    
	                }
	                UserOrganization organization = user != null ? user.getUserOrganization() : null;
	                if(phone != null && phone.trim().length() > 0 && Utility.phoneParse(phone,organization) != null ) {
	                	isValidCustFlag = true;
	                	validPhone = phone;
	                    
	                }
	                if(custId != null && !custId.isEmpty()){
	                    isValidCustFlag = true;
	                }
	               
	                Contacts contObj = null;
	                
	                
	                if(isValidCustFlag) {
	                       
	                    //Create Contact Object with CoustmerInfo details
	                    contObj = new Contacts();
	                    contObj.setUsers(user);
	                   
	                    //set CustomerId or External Id
	                    if( custId != null && !custId.isEmpty()) {
	                        contObj.setExternalId(custId);
	                    }
	                       
	                    //set EmailId
	                    contObj.setEmailId(validEmailStr);
	                       
	                    //set Mobile Phone
	                    contObj.setMobilePhone(validPhone);
	                   
	                    //check contact Object exist in Db with the Requested users
	                    Users coupOwnerUser = null;
	                    logger.debug(">>> first checking  contact ::here " + contObj);
	                    
	                    //get The DB Contact Object
	                    dbContactObj = findContactByUserId(user.getUserId(), contObj, user);
	                   
	                    if(dbContactObj != null) { // check with jsonUser 
	                    logger.info("  >>> dbContactObj t here " + dbContactObj.getContactId());
	                       
	                        //get CouponCode Object
	                    	List<CouponCodes> cCodeLst = couponCodesDao.findCoupCodeLstByContactId(""+dbContactObj.getContactId(), orgId ,reqCoupcode );
	                    	logger.info("  >>> cCodeLst size is  :: " + cCodeLst);
	                        long redeemCount = 0;
	                        contactId = dbContactObj.getContactId();
	                        if(cCodeLst != null && cCodeLst.size() >0 ) {
		                        
	                        	for (CouponCodes eachCC : cCodeLst) {
		                        	if(eachCC.getStatus().equals(Constants.COUP_CODE_STATUS_REDEEMED)) redeemCount ++;
		                        	else couponCodes = eachCC;
									
								}
	                        	logger.info("  >>> coupObj.getSingPromoContRedmptLimit()  is  :: " + coupObj.getSingPromoContRedmptLimit());
	                        	logger.info("redeemCount is  ::"+redeemCount);
		                        if(coupObj.getSingPromoContRedmptLimit() != null && coupObj.getSingPromoContRedmptLimit() <=  redeemCount) {
		                        	errorCode= 100020;
		                            msg = "Request can't be serviced. Redemption limit set per customer has been reached.";
		                            return null;
		                        }
	                        }
	                        logger.info("123 CouponCode is  :: "+couponCodes);
	                        if(couponCodes == null) {
	                            
	                        	coupOwnerUser =  usersDao.find(coupObj.getUserId());
	                            if(coupOwnerUser.getUserId() != user.getUserId().longValue()) { //Check with the Promo Created User
	                               
	                                dbContactObj = findContactByUserId(coupOwnerUser.getUserId(), contObj, coupOwnerUser);
	                                if(dbContactObj != null) {
	                                	contactId = dbContactObj.getContactId();
	                                	//get CouponCode Object
	                                	cCodeLst = couponCodesDao.findCoupCodeLstByContactId(""+dbContactObj.getContactId(), orgId ,reqCoupcode );
	                                	
	                                	if(cCodeLst != null && cCodeLst.size() >0 ) {
	                                		
	                                		redeemCount = 0;
	                                		for (CouponCodes eachCC : cCodeLst) {
	                                			if(eachCC.getStatus().equals(Constants.COUP_CODE_STATUS_REDEEMED)) redeemCount ++;
	                                			else couponCodes = eachCC;
	                                			
	                                		}
	                                		logger.info("redeemCount is ::"+redeemCount+" obj count "+coupObj.getSingPromoContRedmptLimit());
	                                		if(coupObj.getSingPromoContRedmptLimit() != null && coupObj.getSingPromoContRedmptLimit() <=  redeemCount) {
	                                			errorCode= 100020;
	        		                            msg = "Request can't be serviced. Redemption limit set per customer has been reached.";
	                                			return null;
	                                			
	                                		}
	                                	}
	                                }
	                            }
	                               
	                            if(couponCodes == null) { // check the Contact Org level
	                            	logger.debug(">> checkinhg here Org level contacts ... <<<<<<<<<<<<<<<<<<<<<");
	                                List<Contacts> contactList = getAllContactsByOrg(orgId, contObj);
	                               
	                                if(contactList == null || contactList.size() == 0 ) {
	                                    /*if (!coupObj.getRedemedAutoChk()) {
	                                    	errorCode= 100929;
	                                        msg = "Promo-code not available.";
	                                        return null;
	                                    }else {
	                                        newCC = true;
	                                    }*/
	                                	newCC = true;
	                                }else {
	                                    //get the CouponCode with help of ContactIds
	                                    String contactIdStr = "";
	                                    for (Contacts contacts : contactList) {
	                                    	logger.debug(">>>> org finding contacts here is  ::: "+contacts);
	                                        contactIdStr += contactIdStr.trim().length() == 0 ? ""+contacts.getContactId() :","+contacts.getContactId();
	                                    }
	                                    if(contactIdStr!=null && !contactIdStr.trim().isEmpty()) {
	                            			couponCodes = couponCodesDao.findCoupCodeByContactId(contactIdStr , orgId ,reqCoupcode );
	                            			}
	                                   /*//struckuped here TODO
	                                    if(couponCodes == null && !coupObj.getRedemedAutoChk()) {
	                                        //TODO send error message couponcode not avaialable
	                                    	msg = "Error : Redemption limit exceeded.";
	                        				errorCode = 100013;
	                                        return null;
	                                    }else if(couponCodes == null) {
	                                        newCC = true;
	                                    }*/
	                                    if(couponCodes == null) {
	                                        newCC = true;
	                                    }
	                                }
	                            }
	                        }
	                    }else if(coupObj.getUserId().longValue() != user.getUserId().longValue()){ // check with Contact Promo Creted User
	                    	logger.debug("DB contact is Copuon Owner checking ....");
	                    	coupOwnerUser =  usersDao.find(coupObj.getUserId());
	                    	dbContactObj = findContactByUserId(coupOwnerUser.getUserId(), contObj, coupOwnerUser);
	                        //get CouponCode Object
	                        if(dbContactObj != null) {
	                            //couponCodes = couponCodesDao.findCoupCodeByContactId(""+dbContactObj.getContactId(), orgId ,reqCoupcode );
	                        	contactId = dbContactObj.getContactId();
	                        	List<CouponCodes> cCodeLst = couponCodesDao.findCoupCodeLstByContactId(""+dbContactObj.getContactId(), orgId ,reqCoupcode );
                            	
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
	                            List<Contacts> contactList = getAllContactsByOrg(orgId, contObj);
	                           
	                            if(contactList == null || contactList.size() == 0){
	                                 /*if(!coupObj.getRedemedAutoChk()) {
	                                	 errorCode= 100929;
	                                     msg = "Promo-code not available.";
	                                     return null;
	                                 }else {
	                                     //set the couponcode remaining fields
	                                     newCC = true;
	                                 }*/
	                            	newCC = true;
	                               
	                            }else {
	                                //get the CouponCode with help of ContactIds
	                                String contactIdStr = "";
	                                for (Contacts contacts : contactList) {
	                                    contactIdStr += contactIdStr.trim().length() == 0 ? ""+contacts.getContactId() :","+contacts.getContactId();
	                                }
	                                if(contactIdStr!=null && !contactIdStr.trim().isEmpty()) {
	                        			couponCodes = couponCodesDao.findCoupCodeByContactId(contactIdStr , orgId ,reqCoupcode );
	                        			}
	                               
	                               /* if(couponCodes == null && !coupObj.getRedemedAutoChk()) {
	                                    //TODO send error message couponcode not avaialable
	                                	errorCode= 100929;
	                                    msg = "Promo-code not available.";
	                                    return null;
	                                }else if(couponCodes == null && coupObj.getRedemedAutoChk()) {
	                                   
	                                    newCC = true;
	                                }*/
	                                if(couponCodes == null) {
		                                   
	                                    newCC = true;
	                                }
	                            }
	                        }
	                    }else {  //Check the Contact org level
	                    	
	                    	  List<Contacts> contactList = getAllContactsByOrg(orgId, contObj);
	                          
	                          if(contactList == null || contactList.size() == 0){
	                              /* if(!coupObj.getRedemedAutoChk()) {
	                            	   errorCode= 100929;
	                                   msg = "Promo-code not available.";
	                                   //TODO send error message contacts not exists in org level and coupon obje is Redeemcheck is false
	                                   return null;
	                               }else {
	                                   //set the couponcode remaining fields
	                                   newCC = true;
	                               }*/
	                        	  newCC = true;
	                             
	                          }else {
	                              //get the CouponCode with help of ContactIds
	                              String contactIdStr = "";
	                              for (Contacts contacts : contactList) {
	                                  contactIdStr += contactIdStr.trim().length() == 0 ? ""+contacts.getContactId() :","+contacts.getContactId();
	                              }
	                              if(contactIdStr!=null && !contactIdStr.trim().isEmpty()) {
	                      			couponCodes = couponCodesDao.findCoupCodeByContactId(contactIdStr , orgId ,reqCoupcode );
	                      			}
	                              if(couponCodes == null ) {
		                                 
	                                  newCC = true;
	                              }
	                          }
	                    }
	                }else if(couponCodes == null && !coupObj.getRedemedAutoChk()) {
	                	
	                	
	                	if(coupObj.getRedeemdCount() != null &&  coupObj.getRedeemdCount() > redeemdCount){
	                		newCC = true; 
	                	}else {
	                		
	                		errorCode= 100929;
	                		msg = "Promo-code not available.";
	                		return null;
	                	}
	                	
	                 }else if(couponCodes == null && coupObj.getRedemedAutoChk()){
	                	 
	                	 logger.info("newcc value true setting here  ... ");
	                	 newCC = true; 
	                 }
	               
	              
	               logger.info(" >>>>> newCC is  :: "+newCC);
	                if(newCC) {
	                   
	                	//Check once again promo redemption is eligable for the customer
	                	//if the customer information not existed in contact table
	                	
	                	//logger.info(" >>>>> 1 unlimited redeem chk is  :: "+coupObj.getSingPromoContUnlimitedRedmptChk());
	                	//logger.info(" >>>>> 2 unlimited redeem chk is  :: "+coupObj.getSingPromoContRedmptLimit());
	                	
	                	if(coupObj.getSingPromoContRedmptLimit() != null && redeemStr != null && redeemStr.trim().length() > 0) {
	                		
	                		List<CouponCodes> promoList = couponCodesDao.isPromoExistForRedeem(custId,email,phone,reqCoupcode,orgId);
	                		logger.debug("promoList size is  :: "+promoList.size());
	                		
	                		if(promoList != null && promoList.size() >= coupObj.getSingPromoContRedmptLimit()){
	                			errorCode= 100020;
	                            msg = "Request can't be serviced. Redemption limit set per customer has been reached.";
	                            return null;
	                		}
	                	}
	                    couponCodes = new CouponCodes();
	                    couponCodes.setCouponId(coupObj);
	                    couponCodes.setOrgId(orgId);
	                    couponCodes.setCouponCode(reqCoupcode);
	                }
	               
	                if(contactId != null && couponCodes.getContactId() == null) 
	                	couponCodes.setContactId(contactId);
	                
//	            }
	                couponCodes.setStoreNumber(strorNumberStr !=null ? strorNumberStr.trim():null);
	                couponCodes.setSourceType(sourceType);
	                couponCodes.setStatus(Constants.COUP_CODE_STATUS_REDEEMED);
	                couponCodes.setRedeemedOn(Calendar.getInstance());
	                couponCodes.setTotDiscount(totDis);
	                couponCodes.setTotRevenue(totAmt);
	                couponCodes.setUsedLoyaltyPoints(usedLoyalty);
	                if(docSidStr != null && docSidStr.trim().length() > 0) couponCodes.setDocSid(docSidStr); 
	                
	                // TODO set redeemTo 
	                if(redeemStr != null && redeemStr.trim().length() > 0)
	                		couponCodes.setRedeemedTo(redeemStr);
	                if(custId != null && custId.trim().length() > 0)
	                		couponCodes.setRedeemCustId(custId);
	                if(email != null && email.trim().length() > 0)
	                		couponCodes.setRedeemEmailId(email);
	                if(phone != null && phone.trim().length() > 0)
	                		couponCodes.setRedeemPhnId(phone);
	                
	                logger.debug("CouponCode  Obj is ::"+couponCodes);
	                //couponCodesDao.saveOrUpdate(couponCodes);
	                couponCodesDaoForDML.saveOrUpdate(couponCodes);
	               
	                long allCount = couponCodesDao.findIssuedCoupCodeByCoup(coupObj.getCouponId());
	               
	               
	                coupObj.setRedeemed(coupObj.getRedeemed()!= null ? coupObj.getRedeemed().longValue()+1 : 1);
	                //coupObj.setIssued(newCC ? (coupObj.getIssued() != null ? coupObj.getIssued().longValue()+1 : 1) : coupObj.getIssued().longValue());
	                coupObj.setTotRevenue(coupObj.getTotRevenue() != null ? coupObj.getTotRevenue().longValue()+totAmt : totAmt);
	                //coupObj.setUsedLoyaltyPoints(coupObj.getUsedLoyaltyPoints() != null ? coupObj.getUsedLoyaltyPoints()+usedLoyalty: usedLoyalty);
	               
	                if(coupObj.getTotalQty() != null) {
	                	Long totalCouponCodeCount = couponCodesDao.findTotalCoupCodeCountByCoupAndStatus(coupObj.getCouponId());
						coupObj.setTotalQty(totalCouponCodeCount);
	                	
	                   long availCount = coupObj.getTotalQty().longValue()-allCount;
	                   if(availCount < 0) availCount = 0;
	                    coupObj.setAvailable(availCount);
	                   
	                }
	                //coupObj.setAvailable(coupObj.getTotalQty() != null && ?  )
	                //couponsDao.saveOrUpdate(coupObj);
	                couponsDaoForDML.saveOrUpdate(coupObj);
	                if(coupObj.getLoyaltyPoints()!=null)
     	               couponsDaoForDML.updateUsedLoyaltyPoint(coupObj.getCouponId());
	               /* if(isEnableEvent) {
	                    eventTriggerEventsObservable.notifyForWebEvents(user.getUserId().longValue(),dbContactId, dbContactId);
	                }//if
	    */           
	                errorCode= 0;
	                msg = "Promo redeem sucessfully";
                
            } 
            else {
            	//Check for same docsid
                if(docSidStr != null && !docSidStr.isEmpty()) {
                	if(docSidStr.equalsIgnoreCase(couponCodes.getDocSid()) && 
                		couponCodes.getStatus().equalsIgnoreCase(Constants.COUP_CODE_STATUS_REDEEMED)) {
                		coupObj.setTotRevenue(coupObj.getTotRevenue() != null ? coupObj.getTotRevenue().longValue()-couponCodes.getTotRevenue().doubleValue()+totAmt : totAmt);
                		couponCodes.setRedeemedOn(Calendar.getInstance());
     	                couponCodes.setTotDiscount(totDis);
     	                couponCodes.setTotRevenue(totAmt);
     	                couponCodes.setUsedLoyaltyPoints(usedLoyalty);
     	                couponCodes.setSourceType(sourceType);
     	                if(docSidStr != null && docSidStr.trim().length() > 0) couponCodes.setDocSid(docSidStr); 
     	                
     	                if(redeemStr != null && redeemStr.trim().length() > 0)
     	                		couponCodes.setRedeemedTo(redeemStr);
     	                if(custId != null && custId.trim().length() > 0)
     	                		couponCodes.setRedeemCustId(custId);
     	                if(email != null && email.trim().length() > 0)
     	                		couponCodes.setRedeemEmailId(email);
     	                if(phone != null && phone.trim().length() > 0)
     	                		couponCodes.setRedeemPhnId(phone);
     	                
     	                logger.debug("CouponCode  Obj is ::"+couponCodes);
     	                //couponCodesDao.saveOrUpdate(couponCodes);
     	                couponCodesDaoForDML.saveOrUpdate(couponCodes);
     	                //couponsDao.saveOrUpdate(coupObj);
     	               couponsDaoForDML.saveOrUpdate(coupObj);
     	               errorCode= 0;
     	                msg = "Promo updated sucessfully";
     	               return null; 
                	}
                }
            	
            	// redeem check
                if(couponCodes != null && couponCodes.getStatus()!= null && 
                		couponCodes.getStatus().equals(Constants.COUP_CODE_STATUS_REDEEMED) &&
                         coupObj.getCouponGeneratedType().equals(Constants.COUP_GENT_TYPE_MULTIPLE)){
                       
                        logger.debug("Promo-code already redeemed in ");
                        msg = "Error : Promo-code already redeemed.";
                        errorCode = 100920;
                        return null;   
                }
                couponCodes.setStoreNumber(strorNumberStr !=null ? strorNumberStr.trim():null);
                couponCodes.setSourceType(sourceType);
                couponCodes.setStatus(Constants.COUP_CODE_STATUS_REDEEMED);
                couponCodes.setRedeemedOn(Calendar.getInstance());
                couponCodes.setTotDiscount(totDis);
                couponCodes.setTotRevenue(totAmt);
                couponCodes.setUsedLoyaltyPoints(usedLoyalty);
                if(docSidStr != null && docSidStr.trim().length() > 0) couponCodes.setDocSid(docSidStr); 
                
                // TODO set redeemTo 
                if(redeemStr != null && redeemStr.trim().length() > 0)
                		couponCodes.setRedeemedTo(redeemStr);
                if(custId != null && custId.trim().length() > 0)
                		couponCodes.setRedeemCustId(custId);
                if(email != null && email.trim().length() > 0)
                		couponCodes.setRedeemEmailId(email);
                if(phone != null && phone.trim().length() > 0)
                		couponCodes.setRedeemPhnId(phone);
                
                logger.debug("CouponCode  Obj is ::"+couponCodes);
                //couponCodesDao.saveOrUpdate(couponCodes);
                couponCodesDaoForDML.saveOrUpdate(couponCodes);
               
                long allCount = couponCodesDao.findIssuedCoupCodeByCoup(coupObj.getCouponId());
               
               
                coupObj.setRedeemed(coupObj.getRedeemed()!= null ? coupObj.getRedeemed().longValue()+1 : 1);
                //coupObj.setIssued(newCC ? (coupObj.getIssued() != null ? coupObj.getIssued().longValue()+1 : 1) : coupObj.getIssued().longValue());
                coupObj.setTotRevenue(coupObj.getTotRevenue() != null ? coupObj.getTotRevenue().longValue()+totAmt : totAmt);
                //coupObj.setUsedLoyaltyPoints(coupObj.getUsedLoyaltyPoints() != null ? coupObj.getUsedLoyaltyPoints()+usedLoyalty: usedLoyalty);
               
                if(coupObj.getTotalQty() != null) {
                   long availCount = coupObj.getTotalQty().longValue()-allCount;
                   if(availCount < 0) availCount = 0;
                    coupObj.setAvailable(availCount);
                   
                }
                //coupObj.setAvailable(coupObj.getTotalQty() != null && ?  )
                //couponsDao.saveOrUpdate(coupObj);
                couponsDaoForDML.saveOrUpdate(coupObj);
                if(coupObj.getLoyaltyPoints()!=null)
 	               couponsDaoForDML.updateUsedLoyaltyPoint(coupObj.getCouponId());
                /* if(isEnableEvent) {
                    eventTriggerEventsObservable.notifyForWebEvents(user.getUserId().longValue(),dbContactId, dbContactId);
                }//if
    */           
                errorCode= 0;
                msg = "Promo redeem sucessfully";
            }
       
            
         // *********Referral program code***********************
			if(couponCodes.getContactId() != null) {
				Contacts contactObj = contactsDao.findById(couponCodes.getContactId());
				String refererEmailId = contactObj.getUdf12();
				
				if(refererEmailId != null 
						&& !refererEmailId.trim().isEmpty() 
						&& Utility.validateEmail(refererEmailId)) {
					
					CustomTemplates customTemplates = customTemplatesDao.findByUserRefereeEmail(user.getUserId());
					if(customTemplates != null) {
						String message= null;

						if(customTemplates != null && customTemplates.getHtmlText()!= null) {
							  message = customTemplates.getHtmlText();
						  }else if(Constants.EDITOR_TYPE_BEE.equalsIgnoreCase(customTemplates.getEditorType()) && customTemplates.getMyTemplateId()!=null) {
							  MyTemplates myTemplates = myTemplatesDao.getTemplateByMytemplateId(customTemplates.getMyTemplateId());
							  if(myTemplates != null)message = myTemplates.getContent();
						  }
						message = message.replace("[OrganisationName]",
								user.getUserOrganization().getOrganizationName()).replace(
										"[senderReplyToEmailID]", user.getEmailId());
						
						EmailQueue testEmailQueue = new EmailQueue(customTemplates.getTemplateId(),
								Constants.EQ_TYPE_WELCOME_MAIL, message, "Active",
								refererEmailId, user, MyCalendar.getNewCalendar(),
								" Welcome Mail.", null, null, null,
								contactObj.getContactId());
						
						// testEmailQueue.setChildEmail(childEmail);
						//emailQueueDao.saveOrUpdate(testEmailQueue);
						emailQueueDaoForDML.saveOrUpdate(testEmailQueue);
						
						
						
						/*Contacts refereeContact = contactsDao.findContactByEmailId(refererEmailId, user.getUserId());
						if(refereeContact != null){
							
							
							String message=customTemplates.getHtmlText();
							message = message.replace("[OrganisationName]",
									user.getUserOrganization().getOrganizationName()).replace(
											"[senderReplyToEmailID]", user.getEmailId());
							
							EmailQueue testEmailQueue = new EmailQueue(customTemplates,
									Constants.EQ_TYPE_WELCOME_MAIL, message, "Active",
									refererEmailId, user, MyCalendar.getNewCalendar(),
									" Welcome Mail.", null, null, null,
									contactObj.getContactId());
							
							// testEmailQueue.setChildEmail(childEmail);
							emailQueueDao.saveOrUpdate(testEmailQueue);
							
						}*/
					}//if
				}	
			}

            
            
            return null;
           
        } catch (Exception e) {
            // TODO Auto-generated catch block
            logger.error("Exception ::" , e);
        }finally {
            // header details
            JSONObject headerDetails = new JSONObject();
            headerDetails.put("REQUESTID", requestId);
            jsonResponseObject.put("HEADERINFO", headerDetails);
           
            // user details info
            JSONObject usersJSONObj = new JSONObject();
            usersJSONObj.put("USERNAME", userName);
            usersJSONObj.put("ORGID", userOrg);
            usersJSONObj.put("TOKEN", userToken);
            jsonResponseObject.put("USERDETAILS", usersJSONObj);
           
            // coupon code info
            JSONObject couponCodeDetails = new JSONObject();
            couponCodeDetails.put("COUPONNAME", couponName == null ? Constants.STRING_NILL : couponName);
            couponCodeDetails.put("COUPONCODE", reqCoupcode);
            couponCodeDetails.put("CUSTOMERID", custId);
            couponCodeDetails.put("EMAIL", email);
            couponCodeDetails.put("PHONE", phone);
            couponCodeDetails.put("SOURCETYPE", sourceType);
            jsonResponseObject.put("COUPONCODEINFO", couponCodeDetails);
           
           
            // puchase coupon information
            JSONObject CouponRedeemedDetails = new JSONObject();
            CouponRedeemedDetails.put("TOTALAMOUNT", totalAmount);
            CouponRedeemedDetails.put("TOTALDISCOUNT", totalDiscount);
            CouponRedeemedDetails.put("USEDLOYALTYPOINTS", usedLoyaltyPoints != null? usedLoyaltyPoints:"");
            jsonResponseObject.put("PURCHASECOUPONINFO",CouponRedeemedDetails);
           
            // stgatus response
            JSONObject replyObject = new JSONObject();
            replyObject.put("STATUS", errorCode == 0 ? "Success" : "Failure");
            replyObject.put("MESSAGE", msg);
            replyObject.put("ERRORCODE", errorCode);
            jsonResponseObject.put("STATUSINFO",replyObject);
           
            /*// customer info
       
            JSONObject custJSONObj = new JSONObject();
            custJSONObj.put("CUSTOMERID", custId);
            custJSONObj.put("EMAIL", email);
              custJSONObj.put("PHONE", phone);
            jsonResponseObject.put("CUSTOMERINFO", custJSONObj);*/
           
           
            JSONObject rootObject = new JSONObject();
            rootObject.put("COUPONCODEREDEEMRESPONSE", jsonResponseObject);
             
            pw.write(rootObject.toJSONString());
           
            pw.flush();
            pw.close();
        }

       
       
        return null;
    }
   
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
   
    
  /*  public void performMobileOptIn(Contacts contactObj, Users user) {
       
       
        SMSSettings smsSettings = smsSettingsDao.findByUser(user.getUserId());
        if(smsSettings == null) {
           
            contactObj.setMobileOptin(false);
            contactObj.setMobileStatus(Constants.CON_MOBILE_STATUS_ACTIVE);
            return;
           
           
        }//if
        //
        if(smsSettings.isEnableOptInMessage() && 
                smsSettings.getOptInMedium() != null &&
                (smsSettings.getOptInMedium().byteValue() & 4) > 0) {
           
            if(contactObj.getLastSMSDate() == null && contactObj.isMobileOptin() != true) {
               
                contactObj.setMobileStatus(Constants.CON_MOBILE_STATUS_OPTIN_PENDING);
                contactObj.setLastSMSDate(Calendar.getInstance());
                contactObj.setMobileOptin(false);
               
//                ClickaTellApi clickaTellApi = (ClickaTellApi)SpringUtil.getBean("clickaTellApi");
               
                if(!captiwayToSMSApiGateway.getBalance(Constants.USER_SMSTOOL_CLICKATELL, 1)) {
                   
                    logger.debug("low credits with clickatell");
//                    continue;
                }
               
                if( (  (user.getSmsCount() == null ? 0 : user.getSmsCount()) - (user.getUsedSmsCount() == null ? 0 : user.getUsedSmsCount() ) ) >=  1) {
                   
                    //UsersDao usersDao = (UsersDao)SpringUtil.getBean("usersDao");
                   
                    String msgContent = smsSettings.getKeywordResponse();
                    if(msgContent != null) {
                       
                        msgContent = smsSettings.getMessageHeader() + " "+ msgContent;
                    }
                   
                    //clickaTellApi.sendAutoResponse(PropertyUtil.getPropertyValueFromDB(Constants.SMS_SENDERID), contactObj.getMobilePhone(), msgContent);
                    String mobileStatus =  captiwayToSMSApiGateway.sendSingleMobileDoubleOptin(Constants.USER_SMSTOOL_CLICKATELL, PropertyUtil.getPropertyValueFromDB(Constants.SMS_SENDERID), contactObj.getMobilePhone(), msgContent);
                    contactObj.setMobileStatus(mobileStatus);
                   
                    if(!mobileStatus.equals(Constants.CON_MOBILE_STATUS_OPTIN_PENDING)) {
                       
                        contactsDao.updatemobileStatus(contactObj.getMobilePhone(), mobileStatus, smsSettings.getUserId().getUserId());
                       
                    }
                   
                    if(user.getParentUser() != null) {
                        user = user.getParentUser();
                    }
                    user.setUsedSmsCount( (user.getUsedSmsCount() == null ? 0 : user.getUsedSmsCount()) +1);
                    usersDao.saveOrUpdate(user);
                }else {
                    logger.debug("low credits with user...");
//                    continue;
                   
                }
               
            }//if
           
        }//if
        else {
            contactObj.setMobileStatus(Constants.CON_MOBILE_STATUS_ACTIVE);
        }
    }
  
   private Map<Integer,Object> findPromoByValues(Contacts dbContactObj, Long orgId, String reqCoupcode) {
	   
	   Map<Integer, Object> promoCodeMap = new HashMap<Integer, Object>();
	   List<CouponCodes> cCodeLst = couponCodesDao.findCoupCodeLstByContactId(""+dbContactObj.getContactId(), orgId ,reqCoupcode );
	   CouponCodes couponCodes = null;
	   long redeemCount = 0;
       int errorCode = 0;
       
       if(cCodeLst != null && cCodeLst.size() >0 ) {
           for (CouponCodes eachCC : cCodeLst) {
           	if(eachCC.getStatus().equals(Constants.COUP_CODE_STATUS_REDEEMED)) redeemCount ++;
           	else couponCodes = eachCC;
				
			}
           
           if(couponCodes.getCouponId().getSingPromoContRedmptLimit() != null && 
        		   couponCodes.getCouponId().getSingPromoContRedmptLimit() <=  redeemCount) {
           		errorCode= 20202020;
           		
           		 promoCodeMap.put(20202020, null);
           		return promoCodeMap;
           		
           }
       }
	   
       return promoCodeMap;
       
   }
    
     */
    
}



