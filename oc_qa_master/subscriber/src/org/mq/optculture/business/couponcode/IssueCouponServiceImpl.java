package org.mq.optculture.business.couponcode;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.mq.marketer.campaign.beans.AutoSMS;
import org.mq.marketer.campaign.beans.AutoSmsQueue;
import org.mq.marketer.campaign.beans.Contacts;
import org.mq.marketer.campaign.beans.ContactsLoyalty;
import org.mq.marketer.campaign.beans.Coupons;
import org.mq.marketer.campaign.beans.EmailQueue;
import org.mq.marketer.campaign.beans.MailingList;
import org.mq.marketer.campaign.beans.OCSMSGateway;
import org.mq.marketer.campaign.beans.POSMapping;
import org.mq.marketer.campaign.beans.SMSSuppressedContacts;
import org.mq.marketer.campaign.beans.UserOrganization;
import org.mq.marketer.campaign.beans.UserSMSGateway;
import org.mq.marketer.campaign.beans.UserSMSSenderId;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.service.CaptiwayToSMSApiGateway;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.AutoSMSDao;
import org.mq.marketer.campaign.dao.AutoSmsQueueDaoForDML;
import org.mq.marketer.campaign.dao.ContactsDao;
import org.mq.marketer.campaign.dao.ContactsDaoForDML;
import org.mq.marketer.campaign.dao.CouponsDao;
import org.mq.marketer.campaign.dao.EmailQueueDao;
import org.mq.marketer.campaign.dao.EmailQueueDaoForDML;
import org.mq.marketer.campaign.dao.MailingListDao;
import org.mq.marketer.campaign.dao.OCSMSGatewayDao;
import org.mq.marketer.campaign.dao.POSMappingDao;
import org.mq.marketer.campaign.dao.SMSSuppressedContactsDao;
import org.mq.marketer.campaign.dao.SMSSuppressedContactsDaoForDML;
import org.mq.marketer.campaign.dao.UserSMSGatewayDao;
import org.mq.marketer.campaign.dao.UserSMSSenderIdDao;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.dao.UsersDaoForDML;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.SMSStatusCodes;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.business.helper.GatewayRequestProcessHelper;
import org.mq.optculture.business.helper.SmsQueueHelper;
import org.mq.optculture.business.importcontacts.ImportContactsBusinessService;
import org.mq.optculture.business.updatecontacts.UpdateContactsBusinessService;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.BaseRequestObject;
import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.model.couponcodes.Coupon;
import org.mq.optculture.model.couponcodes.Customer;
import org.mq.optculture.model.couponcodes.IssueCouponRequest;
import org.mq.optculture.model.couponcodes.IssueCouponResponse;
import org.mq.optculture.model.couponcodes.StatusInfo;
import org.mq.optculture.model.importcontact.ImportRequest;
import org.mq.optculture.model.importcontact.ImportResponse;
import org.mq.optculture.model.updatecontacts.ContactRequest;
import org.mq.optculture.model.updatecontacts.ContactResponse;

import org.mq.optculture.model.updatecontacts.Header;
import org.mq.optculture.model.updatecontacts.User;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.springframework.dao.DataIntegrityViolationException;
import org.zkoss.zkplus.spring.SpringUtil;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

public class IssueCouponServiceImpl implements IssueCouponService{
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	long reportCount=0l; 
	@Override
	public BaseResponseObject processRequest(BaseRequestObject baseRequestObject) throws BaseServiceException {
		BaseResponseObject baseResponseObject = new BaseResponseObject();
		IssueCouponResponse issueCouponResponse = null;

		try {
			logger.debug("-------entered processRequest---------");
			// json to object
			Gson gson = new Gson();
			IssueCouponRequest IssueCouponRequest = null;
			try {
				IssueCouponRequest = gson.fromJson(baseRequestObject.getJsonValue(), IssueCouponRequest.class);
			} catch (JsonSyntaxException e) {
				logger.error("Exception ::", e);
			    StatusInfo status = new StatusInfo("1000000",
						PropertyUtil.getErrorMessage(1000000, OCConstants.ERROR_CONTACTS_FLAG),
						OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				
				issueCouponResponse = prepareFinalResponse( status, IssueCouponRequest);
				//object to json
				String json = gson.toJson(issueCouponResponse);
				baseResponseObject.setJsonValue(json);
				baseResponseObject.setAction(OCConstants.UPDATE_CONTACTS_SERVICE_REQUEST);
				return baseResponseObject;
			}
			IssueCouponService IssueCouponService = (IssueCouponService) ServiceLocator
					.getInstance().getServiceByName(OCConstants.ISSUE_COUPON_BUSINESS_SERVICE);
			issueCouponResponse = (IssueCouponResponse) IssueCouponService
					.processIssueCoupon(IssueCouponRequest);

			// object to json
			String json = gson.toJson(issueCouponResponse);
			baseResponseObject.setJsonValue(json);
			baseResponseObject.setAction(OCConstants.UPDATE_CONTACTS_SERVICE_REQUEST);
			return baseResponseObject;
		} catch (Exception e) {
			logger.error("Exception ::", e);
		}
		logger.debug("-------exit  processRequest---------");
		return baseResponseObject;
	}// processRequest

	
	private AutoSMS findAutoSMSBytempId(Long templtId) throws Exception {
		AutoSMSDao autoSMSDao = (AutoSMSDao) ServiceLocator.getInstance().getDAOByName(OCConstants.AUTO_SMS_DAO);
		return autoSMSDao.getIssuecouponsmsmsg(templtId);
	}
	
	@Override
	public IssueCouponResponse processIssueCoupon(IssueCouponRequest issueCouponRequest) throws BaseServiceException {
		
		IssueCouponResponse issueCouponResponse = new IssueCouponResponse();
		CouponsDao couponsDao = null;
		OCSMSGatewayDao oCSMSGatewayDao;
		UserSMSGatewayDao userSmsGatewayDao;
		AutoSmsQueueDaoForDML smsQueueDaoForDML;
		OCSMSGateway ocSMSGateway = null;
		UserSMSGateway userSMSGateway = null;
		Users user = null;
		ContactsDao contactsDao = null;
		EmailQueueDao emailQueueDao = null;
		EmailQueueDaoForDML emailQueueDaoForDML = null;
		try {
			emailQueueDao = (EmailQueueDao)ServiceLocator.getInstance().getDAOByName(OCConstants.EMAILQUEUE_DAO);
			emailQueueDaoForDML = (EmailQueueDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.EMAILQUEUE_DAO_ForDML);
			contactsDao = (ContactsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_DAO);
			couponsDao = (CouponsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.COUPONS_DAO);
			oCSMSGatewayDao = (OCSMSGatewayDao)ServiceLocator.getInstance().getDAOByName(OCConstants.OCSMSGATEWAY_DAO);
			userSmsGatewayDao = (UserSMSGatewayDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERSMSGATEWAY_DAO);
			smsQueueDaoForDML = (AutoSmsQueueDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.AUTO_SMS_QUEUE_DAO_FOR_DML);

		String accountType = null;
		String couponCode= issueCouponRequest.getCoupon().getCoupon_code();
		String couponValue = issueCouponRequest.getCoupon().getCoupon_value();
		
		Coupons coupon = null;
		if(couponCode != null && !couponCode.isEmpty() && couponValue != null && !couponValue.isEmpty()) {
			
			coupon = couponsDao.findCouponsByCodeAndValue(couponCode, couponValue);
			logger.info("value of coupon is >>>>>"+coupon.getCTCouponCode());
		}
		if(coupon != null) {
			Long userId = coupon.getUserId();
		
		    UsersDao usersDao = (UsersDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
			AutoSMSDao autoSMSDao = (AutoSMSDao) ServiceLocator.getInstance().getDAOByName(OCConstants.AUTO_SMS_DAO);
			user = usersDao.findByUserId(userId);
			logger.info("value of user id is>>>>"+user.getUserId()+"value of the user enabled is what "+user.isEnabled()+"and if user is enabled the sms for coupon "+user.isEnableSMS()+"and value of package expiration is "+user.getPackageExpiryDate().after(Calendar.getInstance()));
			if(user != null && user.isEnabled() ) {
				if(user.getPackageExpiryDate().after(Calendar.getInstance())){
				
				Customer customer = issueCouponRequest.getCustomer();
				String phone = customer.getPhone();
				String email = customer.getEmail();
				logger.info("customers details is -->>>>>>>>>>>>>"+customer);
					 
				String issueCouponMsg="";	
				if(phone != null && !phone.isEmpty()) {
					logger.info("value of user organization is >>>>>"+user.getUserOrganization());
					phone = Utility.phoneParse(phone, user.getUserOrganization());
					
						Long templtId=coupon.getCTCouponSMStempltId();
						AutoSMS  autoSMSobj=null;
						if(templtId!=null) {
						
						try {
						autoSMSobj= findAutoSMSBytempId(templtId);
						issueCouponMsg = autoSMSobj.getMessageContent();

						}catch(Exception e) {
							logger.info("Exception"+e);
						}
						
						}else {

						issueCouponMsg = PropertyUtil.getPropertyValueFromDB("IssueCoupnSMSTemplate");
						
						}
						
						issueCouponMsg = issueCouponMsg.replace("[COUPName]", coupon.getCouponName()).replace("[DiscountAmount]", couponValue);
						issueCouponMsg = issueCouponMsg.replace("[COUPCODE]", "|^CC_"+coupon.getCouponId()+"^|");
						issueCouponMsg = issueCouponMsg.replace("|^", "[").replace("^|", "]");
										
						logger.info("value of issueCouponMSg is and enable sms "+issueCouponMsg+"and "+user.isEnableSMS());
					      if(user.isEnableSMS()){
					    	
					    	accountType = SMSStatusCodes.defaultSMSOptinGatewayTypeMap.get(user.getCountryType());
					        logger.info("value of sms status code is  >>>>>"+accountType);
							OCSMSGateway ocGateway = GatewayRequestProcessHelper.getOcSMSGateway(user, 
									SMSStatusCodes.defaultSMSOptinGatewayTypeMap.get(user.getCountryType()));
							logger.info("value of ocGateway is and no default smstype available for user>>>>>"+ocGateway.getId());
							CaptiwayToSMSApiGateway captiwayToSMSApiGateway = (CaptiwayToSMSApiGateway)ServiceLocator.getInstance().getBeanByName("captiwayToSMSApiGateway");
							if((((user.getSmsCount() == null ? 0 : user.getSmsCount()) - (user.getUsedSmsCount() == null ? 0 : user.getUsedSmsCount())) >=  1)) {
								String senderId = ocGateway.getSenderId();
								logger.info("value of senderId is >>>>>"+senderId);
								UserSMSSenderIdDao userSMSSenderIdDao = (UserSMSSenderIdDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USER_SMS_SENDER_ID_DAO); 
								List<UserSMSSenderId> retList =  userSMSSenderIdDao.findSenderIdBySMSType(user.getUserId(), ocGateway.getAccountType());
								if(retList != null && !retList.isEmpty()) {
									senderId = retList.get(0).getSenderId();
						
									logger.info("value of sender id is after setting by retList>>>>>>"+senderId);
								}
								
								AutoSmsQueue autoSmsQueue = new AutoSmsQueue(issueCouponMsg, Constants.EQ_TYPE_ISSUE_COUPON,"Active",phone,
										accountType,senderId,Calendar.getInstance(),user.getUserId(),null,null);
								
								// templateId logic 
								
								autoSmsQueue.setTemplateRegisteredId(autoSMSobj.getTemplateRegisteredId());

								//autoSmsQueue.setTemplateRegisteredId(PropertyUtil.getPropertyValueFromDB("IssueCouponRegisteredID"));
								logger.info("save Auto sms queue>>>>>>>>>>>>>");
								smsQueueDaoForDML.saveOrUpdate(autoSmsQueue);
								
								StatusInfo status = new StatusInfo("0",OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE,"Successfully issued a coupon-sendsms");
								issueCouponResponse.setStatus(status);
							}else {
								StatusInfo status = new StatusInfo("0",OCConstants.JSON_RESPONSE_FAILURE_MESSAGE,"User SMS count is less than 0 or 0");
								issueCouponResponse.setStatus(status);
							}//user sms count is >= 1
						}
					      else {
							StatusInfo status = new StatusInfo("0",OCConstants.JSON_RESPONSE_FAILURE_MESSAGE,"User is not enabled the sms setting");
							issueCouponResponse.setStatus(status);
						}//if user is enableSms
					
				}
				else if(email != null && !email.isEmpty() && Utility.validateEmail(email)) {

					logger.info(" start Auto email queue for coupontool issuance>>>>>>>>>>>>>");
					EmailQueue testEmailQueue = null;
					String subject = PropertyUtil.getPropertyValueFromDB("IssueCoupnSubject");
					String emailContent = PropertyUtil.getPropertyValueFromDB("IssueCoupnemailTemplate");
					emailContent = emailContent.replace("[COUPName]", coupon.getCouponName()).replace("[DiscountAmount]", couponValue);
					emailContent = emailContent.replace("[COUPCODE]", "|^CC_"+coupon.getCouponId()+"^|");
					emailContent = emailContent.replace("|^", "[").replace("^|", "]");
					testEmailQueue = new EmailQueue(
							subject, emailContent, Constants.EQ_TYPE_ISSUE_COUPON, "Active",email , 
								MyCalendar.getNewCalendar(), user);
								
					emailQueueDaoForDML.saveOrUpdate(testEmailQueue);
					String InstantMailUrl = PropertyUtil.getPropertyValue("InstantMailUrl");
					//Send request to servlet which activates the simpleMailSender(which actually sends the instant mails)
					logger.info("schedule Auto email queue to db>>>>>>>>>>>>>"+InstantMailUrl);
					Utility.pingSchedulerService(InstantMailUrl);
					StatusInfo status = new StatusInfo("0",OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE,"Successfully issued a coupon-sendemail");
					issueCouponResponse.setStatus(status);
				}
				else {
					StatusInfo status = new StatusInfo("0",OCConstants.JSON_RESPONSE_FAILURE_MESSAGE,"No phone or email is given.");
					issueCouponResponse.setStatus(status);
					return issueCouponResponse;
				}//end of phone!=null  and email!=null
			
			}else {
				StatusInfo status = new StatusInfo("0",OCConstants.JSON_RESPONSE_FAILURE_MESSAGE,"User package is expired please renew your subscription... AND User packageExpiry is false or packageExpiry date is before to current..");
				issueCouponResponse.setStatus(status);
				return issueCouponResponse;
			   }
			}else{
				StatusInfo status = new StatusInfo("0",OCConstants.JSON_RESPONSE_FAILURE_MESSAGE,"User is null or not enabled or packageExpiry is false or packageExpiry date is before to current..");
				issueCouponResponse.setStatus(status);
				return issueCouponResponse;
			}//end of user!=null and user.isEabled()
		}
		else {
			StatusInfo status = new StatusInfo("0",OCConstants.JSON_RESPONSE_FAILURE_MESSAGE,"coupons is null not found in db with couponcode and value");
			issueCouponResponse.setStatus(status);
			return issueCouponResponse;
		}//else of coupon !=null
		}catch (Exception e) {
			// TODO: handle exception
			logger.error("Exception ::", e);
			StatusInfo status = new StatusInfo("0",OCConstants.JSON_RESPONSE_FAILURE_MESSAGE,"Exception occured in th file...");
			issueCouponResponse.setStatus(status);
			return issueCouponResponse;
		}
		//update the contact info
		ContactResponse contactResp = prepareContactRequest(issueCouponRequest, user);
		logger.info("contactResponse is:>>>>"+contactResp);
		return issueCouponResponse;
		}
	
	private ContactResponse prepareContactRequest(IssueCouponRequest issueCouponRequest, Users users) {
		try {
			ContactResponse contactResponse = null;
			MailingListDao mailingListDao=null;
			Customer customer = issueCouponRequest.getCustomer();
			//for accessing creation date
			UsersDao usersDao = (UsersDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);

			if((customer != null && customer.getPhone()!= null && !customer.getPhone().isEmpty() && customer.getEmail() != null && !customer.getEmail().isEmpty())||
				(customer != null && customer.getPhone().isEmpty() && customer.getEmail() != null && !customer.getEmail().isEmpty())||
				(customer != null && customer.getPhone() != null && !customer.getPhone().isEmpty() && customer.getEmail().isEmpty())) {
				
				ContactRequest contactRequest = new ContactRequest();
				
				Calendar syscal = new MyCalendar(Calendar.getInstance(), null,
						MyCalendar.dateFormatMap.get(MyCalendar.FORMAT_DATETIME_STYEAR));
				String requestId = OCConstants.DR_LTY_EXTRACTION_REQUEST_ID+users.getToken()+"_"+System.currentTimeMillis();
				logger.info("request id =>>>>>>"+requestId);
				Header header = new Header();
				//mailinglist dao.findpossynclist by userid and pass the name here if no POS sync list then give List1
				MailingList posML=null;
				mailingListDao = (MailingListDao)ServiceLocator.getInstance().getDAOByName(OCConstants.MAILINGLIST_DAO);
				
				posML = mailingListDao.findPOSMailingList(users);
				logger.info("pos ml is >>>>>>>>>"+posML);
				if(posML==null) {
					header.setContactList("List1");//if it its not empty then only the validation is taken place
				}
				else {
					header.setContactList(posML.getListName());
				}
								
				header.setRequestDate(syscal.toString());
				header.setRequestId(requestId);
				header.setSourceType("CT");
				header.setContactSource(Constants.CONTACT_OPTIN_MEDIUM_WEBFORM);
				
				User user = new User();
				user.setUserName(Utility.getOnlyUserName(users.getUserName()));//utility
				user.setOrganizationId(users.getUserOrganization().getOrgExternalId());//VVVVVV question ?
				user.setToken(users.getToken() == null ? usersDao.getUserToken(users.getUserOrganization().getUserOrgId()):users.getToken());	
				//logger.info("token value>>>>>>>>>"+usersDao.getUserToken(users.getUserOrganization().getUserOrgId())); -- APP-3914
				//birthday format check ,  if not matching convert to POS mappings mapped format 
				POSMappingDao posMappingDao = (POSMappingDao) ServiceLocator.getInstance()
						.getDAOByName(OCConstants.POSMAPPING_DAO);
				POSMapping contactPOSMap = null;
				contactPOSMap = posMappingDao.findRecordBycustomFieldName("Birthday",Constants.POS_MAPPING_TYPE_CONTACTS ,users.getUserId());
				logger.info("pos mapping object is >>>>>>>>>>"+contactPOSMap);
				String format = contactPOSMap.getDataType();
				logger.info("format >>>>>>>>"+format);
				String birthday = customer.getBirthday();
				String parsedDate =null;
				
				if(birthday != null && !birthday.isEmpty()) {
					try {
						Date initDate = new SimpleDateFormat("yyyy-MM-dd").parse(birthday);
						logger.info("initdate is>>>>>>>>>>>>>>>>"+initDate);
						String dateformat = format.substring(format.indexOf("(") + 1,
								format.indexOf(")"));
						logger.info("format >>>>>>>>"+dateformat);
						SimpleDateFormat formatter = new SimpleDateFormat(dateformat);
						logger.info("formatter value >>>>>>>>"+formatter);
						parsedDate = formatter.format(initDate);
						logger.info("parsed Date >>>>>>>>"+parsedDate);
					} catch (Exception e) {
						logger.info("BirthDay date format not matched with data", e);
					}
				}
				logger.info("parsed date is:>>>>>>>"+parsedDate);
				org.mq.optculture.model.updatecontacts.Customer updatecontact = 
						new org.mq.optculture.model.updatecontacts.Customer("",customer.getPhone(),customer.getEmail(),customer.getFirst_name(),customer.getLast_name(),customer.getCity(),"",customer.getZip(),"","",customer.getGender(),syscal.toString(),parsedDate,"",customer.getAddress(),
								"",customer.getCustom_field_1(),customer.getCustom_field_2(),customer.getCustom_field_3(),customer.getCustom_field_4(),customer.getCustom_field_5(),customer.getCustom_field_6(),customer.getCustom_field_7(),customer.getSocialid(),customer.getQuestion_1(),"","","","","","");
				contactRequest.setHeader(header);
				contactRequest.setCustomer(updatecontact);
				contactRequest.setUser(user);
				logger.info("updated contact is >>>>>>>>>>"+updatecontact);
				Gson gson = new Gson();
				String finialjsonbeforegivingtoUpdate = gson.toJson(contactRequest);
				
				logger.info("final json to from ocsurvey to updateContactsBusinessService.processUpdateContactRequest..:"+finialjsonbeforegivingtoUpdate);
				
				UpdateContactsBusinessService updateContactsBusinessService=(UpdateContactsBusinessService) ServiceLocator.getInstance().getServiceByName(OCConstants.UPDATE_CONTACTS_BUSINESS_SERVICE);

				ContactResponse baseService = (ContactResponse)updateContactsBusinessService.processUpdateContactRequest(contactRequest);
  
				String finialjson = gson.toJson(baseService);
				logger.info("final json for ocsurvey from updateContactsBusinessService.processUpdateContactRequest..:"+finialjson);
				
				contactResponse = baseService;
				logger.info("contact response is>>>>>>>>>>>>>>>>>>>>"+contactResponse);
			} 
			return contactResponse;
		} catch (BaseServiceException e) {
			logger.error("exception :"+e);
		} catch (Exception e) {
			logger.error("exception :"+e);
		}
		return null;
	}
	private IssueCouponResponse prepareFinalResponse( StatusInfo status, IssueCouponRequest issueCouponRequest)
			throws BaseServiceException {
		logger.debug("-------entered prepareFinalResponse---------");
		IssueCouponResponse issueCouponResponse = new IssueCouponResponse();
		
		issueCouponResponse.setStatus(status);
		logger.debug("-------exit  prepareFinalResponse---------");
		return issueCouponResponse;
	}// prepareFinalResponse
	
	

}
