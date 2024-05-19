package org.mq.optculture.business.digitalReceipt;

import java.beans.PropertyDescriptor;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.mq.marketer.campaign.beans.Contacts;
import org.mq.marketer.campaign.beans.ContactsLoyalty;
import org.mq.marketer.campaign.beans.DigitalReceiptMyTemplate;
import org.mq.marketer.campaign.beans.DigitalReceiptUserSettings;
import org.mq.marketer.campaign.beans.DigitalReceiptsJSON;
import org.mq.marketer.campaign.beans.LoyaltyProgram;
import org.mq.marketer.campaign.beans.LoyaltyTransactionChild;
import org.mq.marketer.campaign.beans.OrganizationStores;
import org.mq.marketer.campaign.beans.OrganizationZone;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.beans.UsersDomains;
import org.mq.marketer.campaign.dao.ContactsDao;
import org.mq.marketer.campaign.dao.ContactsLoyaltyDao;
import org.mq.marketer.campaign.dao.DigitalReceiptMyTemplatesDao;
import org.mq.marketer.campaign.dao.DigitalReceiptUserSettingsDao;
import org.mq.marketer.campaign.dao.DigitalReceiptsJSONDao;
import org.mq.marketer.campaign.dao.DigitalReceiptsJSONDaoForDML;
import org.mq.marketer.campaign.dao.OrganizationStoresDao;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.dao.ZoneDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.SMSStatusCodes;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.business.couponcode.CouponCodeEnquiryServiceImpl;
import org.mq.optculture.business.helper.DRToLty.DRToLoyaltyExtractionAuth;
import org.mq.optculture.business.helper.DRToLty.DRToLoyaltyExtractionCustomer;
import org.mq.optculture.business.helper.DRToLty.DRToLoyaltyExtractionItems;
import org.mq.optculture.business.helper.DRToLty.DRToLoyaltyExtractionParams;
import org.mq.optculture.business.helper.DRToLty.DRToLoyaltyExtractionReceipt;
import org.mq.optculture.business.helper.DRToLty.DRToLtyExtractionData;
import org.mq.optculture.data.dao.LoyaltyProgramDao;
import org.mq.optculture.data.dao.LoyaltyTransactionChildDao;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.BaseRequestObject;
import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.model.DR.DRBody;
import org.mq.optculture.model.DR.DRHead;
import org.mq.optculture.model.DR.DROptCultureDetails;
import org.mq.optculture.model.DR.DigitalReceipt;
import org.mq.optculture.model.DR.DigitalReceiptResponse;
import org.mq.optculture.model.DR.Receipt;
import org.mq.optculture.model.DR.User;
import org.mq.optculture.model.DR.heartland.HeartlandDRBody;
import org.mq.optculture.model.DR.heartland.HeartlandDRRequest;
import org.mq.optculture.model.DR.magento.MagentoBasedDRRequest;
import org.mq.optculture.model.DR.magento.MagentoDRBody;
import org.mq.optculture.model.DR.orion.OrionDRBody;
import org.mq.optculture.model.DR.orion.OrionDRRequest;
import org.mq.optculture.model.DR.prism.PrismBasedDRRequest;
import org.mq.optculture.model.DR.prism.PrismDRBody;
import org.mq.optculture.model.DR.prism.PrismDRHead;
import org.mq.optculture.model.DR.prism.PrismDRUser;
import org.mq.optculture.model.DR.shopify.ShopifyBasedDRRequest;
import org.mq.optculture.model.DR.shopify.ShopifyDRBody;
import org.mq.optculture.model.DR.tender.CustomTender;
import org.mq.optculture.model.DR.wooCommerce.WooCommerceDRBody;
import org.mq.optculture.model.DR.wooCommerce.WooCommerceDRRequest;
import org.mq.optculture.model.DR.wooCommerce.WooCommerceReturnDRBody;
import org.mq.optculture.model.DR.wooCommerce.WooCommerceReturnDRRequest;
import org.mq.optculture.model.couponcodes.CoupnCodeEnqResponse;
import org.mq.optculture.model.couponcodes.CouponCodeEnqReq;
import org.mq.optculture.model.couponcodes.CouponCodeEnquObj;
import org.mq.optculture.model.couponcodes.CouponCodeInfo;
import org.mq.optculture.model.couponcodes.CouponCodeResponse;
import org.mq.optculture.model.couponcodes.CouponDiscountInfo;
import org.mq.optculture.model.couponcodes.HeaderInfo;
import org.mq.optculture.model.couponcodes.LoyaltyInfo;
import org.mq.optculture.model.couponcodes.PurchasedItems;
import org.mq.optculture.model.couponcodes.UserDetails;
import org.mq.optculture.model.digitalReceipt.SendDRResponse;
import org.mq.optculture.model.digitalReceipt.SendDRResponseInfo;
import org.mq.optculture.model.digitalReceipt.SendDRStatus;
import org.mq.optculture.model.loyalty.Balances;
import org.mq.optculture.model.ocloyalty.LoyaltyEnrollResponse;
import org.mq.optculture.restservice.digitalReceipt.DRToLtyExtractionService;
import org.mq.optculture.utils.HeartlandRequestTranslator;
import org.mq.optculture.utils.MagentoRequestTranslator;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.OrionRequestTranslator;
import org.mq.optculture.utils.PrismRequestTranslator;
import org.mq.optculture.utils.ServiceLocator;
import org.mq.optculture.utils.ShopifyRequestTranslator;
import org.mq.optculture.utils.WooCommerceRequestTranslator;

import com.google.gson.Gson;

public class ProcessDigitalReceiptServiceImpl implements ProcessDigitalReceiptService {

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	private final String FREE_ITEM_NUDGE = "Add Item to Avail Discount.";

	@Override
	public BaseResponseObject processRequest(BaseRequestObject baseRequestObject) throws BaseServiceException {
		DigitalReceiptResponse digitalReceiptResponse = null;
		try {
			Gson gson = new Gson();
			String Source = baseRequestObject.getSource();
			String JsonVal = baseRequestObject.getJsonValue();
			ProcessDigitalReceiptService processDigitalReceiptService = (ProcessDigitalReceiptService) ServiceLocator
					.getInstance().getServiceByName(OCConstants.PROCESS_DR_BUSINESS_SERVICE);
			if (Source != null && Source.equalsIgnoreCase(OCConstants.DR_SOURCE_TYPE_OPTDR)) {
				DigitalReceipt digitalReceipt = null;
				if (baseRequestObject.getMode() != null
						&& baseRequestObject.getMode().equals(OCConstants.DR_OFFLINE_MODE)) {

					digitalReceipt = gson.fromJson(JsonVal, DigitalReceipt.class);
					digitalReceipt.setAction(OCConstants.DIGITAL_RECEIPT_XML_ACTION_SENDEMAIL);
					digitalReceipt.setMode(baseRequestObject.getMode());
				} else {

					digitalReceipt = (DigitalReceipt) baseRequestObject;
				}

				// DigitalReceipt digitalReceipt = (DigitalReceipt) baseRequestObject;

				digitalReceiptResponse = (DigitalReceiptResponse) processDigitalReceiptService
						.processDigitalReceipt(digitalReceipt, digitalReceipt.getMode());

			} else if (Source != null && Source.equalsIgnoreCase(OCConstants.DR_SOURCE_TYPE_PRISM)) {
				PrismBasedDRRequest digitalReceipt = null;

				if (baseRequestObject.getMode() != null
						&& baseRequestObject.getMode().equals(OCConstants.DR_OFFLINE_MODE)) {

					digitalReceipt = gson.fromJson(JsonVal, PrismBasedDRRequest.class);
					digitalReceipt.setAction(OCConstants.DIGITAL_RECEIPT_XML_ACTION_SENDEMAIL);
					digitalReceipt.setMode(baseRequestObject.getMode());

				} else {

					digitalReceipt = (PrismBasedDRRequest) baseRequestObject;
				}
				digitalReceiptResponse = (DigitalReceiptResponse) processDigitalReceiptService
						.processPrismDRRequest(digitalReceipt, digitalReceipt.getMode());
			} else if (Source != null && Source.equalsIgnoreCase(OCConstants.DR_SOURCE_TYPE_HEARTLAND)) { // newly added
																											// APP-3389
				HeartlandDRRequest digitalReceipt = null;

				if (baseRequestObject.getMode() != null
						&& baseRequestObject.getMode().equals(OCConstants.DR_OFFLINE_MODE)) {

					digitalReceipt = gson.fromJson(JsonVal, HeartlandDRRequest.class);
					digitalReceipt.setAction(OCConstants.DIGITAL_RECEIPT_XML_ACTION_SENDEMAIL);
					digitalReceipt.setMode(baseRequestObject.getMode());

				} else {

					digitalReceipt = (HeartlandDRRequest) baseRequestObject;
				}
				digitalReceiptResponse = (DigitalReceiptResponse) processDigitalReceiptService
						.processHeartlandDRRequest(digitalReceipt, digitalReceipt.getMode());
			} else if (Source != null && Source.equalsIgnoreCase(OCConstants.DR_SOURCE_TYPE_Magento)) {
				MagentoBasedDRRequest digitalReceipt = null;

				if (baseRequestObject.getMode() != null
						&& baseRequestObject.getMode().equals(OCConstants.DR_OFFLINE_MODE)) {

					digitalReceipt = gson.fromJson(JsonVal, MagentoBasedDRRequest.class);
					digitalReceipt.setAction(OCConstants.DIGITAL_RECEIPT_XML_ACTION_SENDEMAIL);
					digitalReceipt.setMode(baseRequestObject.getMode());

				} else {

					digitalReceipt = (MagentoBasedDRRequest) baseRequestObject;
				}
				digitalReceiptResponse = (DigitalReceiptResponse) processDigitalReceiptService
						.processMagentoDRRequest(digitalReceipt, digitalReceipt.getMode());
			} else if (Source != null && Source.equalsIgnoreCase(OCConstants.DR_SOURCE_TYPE_WooCommerce)) {
				WooCommerceDRRequest digitalReceipt = null;
				digitalReceipt = gson.fromJson(JsonVal, WooCommerceDRRequest.class);
				WooCommerceReturnDRRequest refundDR = null;
				refundDR = gson.fromJson(JsonVal, WooCommerceReturnDRRequest.class);
				if (baseRequestObject.getMode() != null
						&& baseRequestObject.getMode().equals(OCConstants.DR_OFFLINE_MODE)) {

					if (baseRequestObject.getMsgContent().equalsIgnoreCase("Refund")) {
						refundDR.setAction(OCConstants.DIGITAL_RECEIPT_XML_ACTION_SENDEMAIL);
						refundDR.setMode(baseRequestObject.getMode());
						refundDR = gson.fromJson(JsonVal, WooCommerceReturnDRRequest.class);
						digitalReceiptResponse = (DigitalReceiptResponse) processDigitalReceiptService
								.processWooCommerceRefundDRRequest(refundDR, refundDR.getMode());

					} else {
						digitalReceipt.setAction(OCConstants.DIGITAL_RECEIPT_XML_ACTION_SENDEMAIL);
						digitalReceipt.setMode(baseRequestObject.getMode());
						digitalReceipt = gson.fromJson(JsonVal, WooCommerceDRRequest.class);
						digitalReceiptResponse = (DigitalReceiptResponse) processDigitalReceiptService
								.processWooCommerceDRRequest(digitalReceipt, digitalReceipt.getMode());
					}
				} else {
					if (baseRequestObject.getMsgContent().equalsIgnoreCase("Refund")) {
						refundDR = (WooCommerceReturnDRRequest) baseRequestObject;
						digitalReceiptResponse = (DigitalReceiptResponse) processDigitalReceiptService
								.processWooCommerceRefundDRRequest(refundDR, refundDR.getMode());
					} else {
						digitalReceipt = (WooCommerceDRRequest) baseRequestObject;
						digitalReceiptResponse = (DigitalReceiptResponse) processDigitalReceiptService
								.processWooCommerceDRRequest(digitalReceipt, digitalReceipt.getMode());
					}
				}

			} else if (Source != null && Source.equalsIgnoreCase(OCConstants.DR_SOURCE_TYPE_Shopify)) {
				ShopifyBasedDRRequest digitalReceipt = null;

				if (baseRequestObject.getMode() != null
						&& baseRequestObject.getMode().equals(OCConstants.DR_OFFLINE_MODE)) {

					digitalReceipt = gson.fromJson(JsonVal, ShopifyBasedDRRequest.class);
					digitalReceipt.setAction(OCConstants.DIGITAL_RECEIPT_XML_ACTION_SENDEMAIL);
					digitalReceipt.setMode(baseRequestObject.getMode());

				} else {

					digitalReceipt = (ShopifyBasedDRRequest) baseRequestObject;
				}
				digitalReceiptResponse = (DigitalReceiptResponse) processDigitalReceiptService
						.processShopifytoDRRequest(digitalReceipt, digitalReceipt.getMode());
			} 
			//APP-4773
			else if(Source != null && Source.equalsIgnoreCase(OCConstants.DR_SOURCE_TYPE_ORION)) {
				OrionDRRequest digitalReceipt = null;

				if (baseRequestObject.getMode() != null
						&& baseRequestObject.getMode().equals(OCConstants.DR_OFFLINE_MODE)) {

					digitalReceipt = gson.fromJson(JsonVal, OrionDRRequest.class);
					digitalReceipt.setAction(OCConstants.DIGITAL_RECEIPT_XML_ACTION_SENDEMAIL);
					digitalReceipt.setMode(baseRequestObject.getMode());

				} else {

					digitalReceipt = (OrionDRRequest) baseRequestObject;
				}
				digitalReceiptResponse = (DigitalReceiptResponse) processDigitalReceiptService
						.processOrionDRRequest(digitalReceipt, digitalReceipt.getMode());
			}

		} catch (Exception e) {
			logger.error("Exception ::", e);
			throw new BaseServiceException("Exception occured while processing processRequest::::: ", e);
		}
		return digitalReceiptResponse;

	}

	@Override
	public DigitalReceiptResponse processDigitalReceipt(DigitalReceipt digitalReceipt, String mode)
			throws BaseServiceException {

		try {
			logger.info("Entered into processSendDRRequest service method....");
			DigitalReceiptResponse digitalReceiptResponse = null;

			digitalReceiptResponse = validateJsonFields(digitalReceipt);
			if (digitalReceiptResponse != null)
				return digitalReceiptResponse;
			SendDRStatus status = null;
			if(digitalReceipt.getBody().getReceipt().getReceiptType() != null && digitalReceipt.getBody().getReceipt().getReceiptType().equals("7")) {
				status = new SendDRStatus("0", PropertyUtil.getErrorMessage(100011, OCConstants.ERROR_DR_FLAG),
						OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
				DigitalReceiptResponse posDRResponse = prepareResponseObject(status);
				return posDRResponse;
			}
			String userName = digitalReceipt.getHead().getUser().getUserName();
			String orgId = digitalReceipt.getHead().getUser().getOrganizationId();
			String token = digitalReceipt.getHead().getUser().getToken();
			Users user = getUser(userName, orgId, token);
			if (user == null) {
				status = new SendDRStatus("100011", PropertyUtil.getErrorMessage(100011, OCConstants.ERROR_DR_FLAG),
						OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				DigitalReceiptResponse posDRResponse = prepareResponseObject(status);
				return posDRResponse;
			}
			if (!user.isEnabled()) {
				status = new SendDRStatus("300011", PropertyUtil.getErrorMessage(300011, OCConstants.ERROR_DR_FLAG),
						OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				DigitalReceiptResponse posDRResponse = prepareResponseObject(status);
				return posDRResponse;
			}
			if (user.getPackageExpiryDate().before(Calendar.getInstance())) {
				status = new SendDRStatus("100012", PropertyUtil.getErrorMessage(100012, OCConstants.ERROR_DR_FLAG),
						OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				DigitalReceiptResponse posDRResponse = prepareResponseObject(status);
				return posDRResponse;
			}
			boolean giveOnlySuccessCode = user.getCountryType().equals(Constants.SMS_COUNTRY_INDIA);
			String requestType = digitalReceipt.getHead().getRequestType();
			String docSid = digitalReceipt.getBody().getReceipt().getDocSID();
			// boolean isexists = false;
			String docDate = digitalReceipt.getBody().getReceipt().getDocDate();
			String docTime = digitalReceipt.getBody().getReceipt().getDocTime();
			boolean isexists = false;
			if (docDate != null && !docDate.isEmpty()) {
				Calendar cal = Calendar.getInstance();
				// DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss aaa");
				// String docDateFormatted = formatter.format(docDate);
				try {
					String time = docDate;
					Date date;
					DateFormat formatter=null;
					logger.info("length of docdate is"+time.length());
					if(time.length()>21) {
						logger.info("entering if format");

						formatter = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss aaa");
					}else {
						logger.info("entering else format");

						formatter = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss");
					}
					
					date = (Date) formatter.parse(time);
					cal.setTime(date);

				} catch (Exception e) {
					logger.info("date format not matched with data, setting system date", e);
				}
				logger.debug((cal.getTimeInMillis() / (1000 * 60 * 60 * 24))
						- (Calendar.getInstance().getTimeInMillis() / (1000 * 60 * 60 * 24)));
				if ((Calendar.getInstance().getTimeInMillis() / (1000 * 60 * 60 * 24))
						- (cal.getTimeInMillis() / (1000 * 60 * 60 * 24)) >= 7) {
					isexists = true;
				}

			}
			boolean mustExtract = false;
			if (!isexists) {
				if (docSid != null && !docSid.isEmpty()) {
					logger.debug("checking====");
					isexists = validateDuplicity(digitalReceipt.getBody().getReceipt().getDocSID(), user.getUserId());
				}

				if (!isexists && requestType != null && !requestType.isEmpty()
						&& requestType.equalsIgnoreCase("Resend")) {
					logger.debug("checking====");
					mustExtract = true;
				} else if (isexists && requestType != null && !requestType.isEmpty()
						&& requestType.equalsIgnoreCase("new")) {
					// ignore the request
					if (isexists) {
						logger.debug("checking====");
						status = new SendDRStatus(giveOnlySuccessCode ? "0" :"100013",
								PropertyUtil.getErrorMessage(100013, OCConstants.ERROR_DR_FLAG),
								OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
						DigitalReceiptResponse posDRResponse = prepareResponseObject(status);
						return posDRResponse;
					}

				}else if(isexists && requestType != null && !requestType.isEmpty()
						&& requestType.equalsIgnoreCase("Resend")) {
					if (digitalReceipt.getAction().equals(OCConstants.DIGITAL_RECEIPT_SERVICE_ACTION_SENDEMAIL)) {
						String isLoyaltyCustomer = digitalReceipt.getHead().getIsLoyaltyCustomer();
						if (user.getUserOrganization().getLoyaltyDisplayTemplate() != null
								&& user.getUserOrganization().isSendRealtimeLoyaltyStatus()
						// && isLoyaltyCustomer != null && isLoyaltyCustomer.trim().equals("Y")
						) {  
							
							String displayTemplate = Constants.STRING_NILL;
							String Card = Constants.STRING_NILL;
							String lifeTimePoints = Constants.STRING_NILL;
							
							String loyaltyEarnedToday = Constants.STRING_NILL;
							String pointsRedeemedToday = Constants.STRING_NILL;
							PropertyDescriptor cdi = null;
							String cardInfo = user.getCardInfo();
							//String cardInfoObjStr=Constants.STRING_NILL;
							Receipt receipt = digitalReceipt.getBody().getReceipt();
							String cardNumber = Constants.STRING_NILL;
							if(cardInfo!=null) {
								cdi = new PropertyDescriptor(cardInfo, receipt.getClass());
								Object cardInfoObj = cdi.getReadMethod().invoke(receipt);
								if(cardInfoObj != null && !cardInfoObj.toString().isEmpty() ) {
									cardNumber  = cardInfoObj.toString();
								}
							}
							logger.info("cardInfoObjStr "+cardNumber );
							if(cardNumber  == null || cardNumber.isEmpty()) {
								//find the card by the trx history
								try{
									LoyaltyTransactionChildDao loyaltyTransactionChildDao = (LoyaltyTransactionChildDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
									LoyaltyTransactionChild child = loyaltyTransactionChildDao.findLtyTransByIssuanceAndDocSid(docSid,user.getUserId());
									if(child != null) {
										cardNumber = child.getMembershipNumber();
									}
								} catch (Exception e) {
									logger.error("Exception::",e);
								}

							}
							if(cardNumber == null || cardNumber.isEmpty()) {
								status = new SendDRStatus("100013",
										PropertyUtil.getErrorMessage(100013, OCConstants.ERROR_DR_FLAG),
										OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
								DigitalReceiptResponse posDRResponse = prepareResponseObject(status);
								return posDRResponse;
							}
							String pointsBalance = Constants.STRING_NILL;
							String currencyBalance = Constants.STRING_NILL;
							String[] displayTemplateArr = constructDisplayTemplate(user, digitalReceipt, cardNumber);
							displayTemplate = displayTemplateArr[0];
							Card = displayTemplateArr[1];
							lifeTimePoints = displayTemplateArr[2];
							loyaltyEarnedToday = displayTemplateArr[3];
							pointsRedeemedToday = displayTemplateArr[8];
							pointsBalance = displayTemplateArr[4];
							currencyBalance = displayTemplateArr[5];
							
							
						}
					
				}
			}

				
			}

			Gson gson = new Gson();
			String DRJson = gson.toJson(digitalReceipt);
			DigitalReceiptsJSON digitalReceiptsJSON = saveDRJson(user, DRJson, user.getUserId(),
					digitalReceipt.getBody().getReceipt().getDocSID(), mode, OCConstants.DR_SOURCE_TYPE_OPTDR);
			String displayTemplate = Constants.STRING_NILL;
			String Card = Constants.STRING_NILL;
			String lifeTimePoints = Constants.STRING_NILL;
			
			String loyaltyEarnedToday = Constants.STRING_NILL;
			String pointsRedeemedToday = Constants.STRING_NILL;
			

			String pointsBalance = Constants.STRING_NILL;
			String currencyBalance = Constants.STRING_NILL;

			if (digitalReceipt.getAction().equals(OCConstants.DIGITAL_RECEIPT_SERVICE_ACTION_SENDEMAIL)) {
				String isLoyaltyCustomer = digitalReceipt.getHead().getIsLoyaltyCustomer();
				if (user.getUserOrganization().getLoyaltyDisplayTemplate() != null
						&& user.getUserOrganization().isSendRealtimeLoyaltyStatus()
				// && isLoyaltyCustomer != null && isLoyaltyCustomer.trim().equals("Y")
				) {

					DRToLtyExtractionService drToLtyExtractionImpl = (DRToLtyExtractionService) ServiceLocator
							.getInstance().getServiceByName(OCConstants.DR_TO_LTY_EXTRACTION_IMPL);
					String cardNumber = drToLtyExtractionImpl.processRequest(digitalReceiptsJSON.getDrjsonId() + "",
							mustExtract);
					String[] displayTemplateArr = constructDisplayTemplate(user, digitalReceipt, cardNumber);
					displayTemplate = displayTemplateArr[0];
					Card = displayTemplateArr[1];
					lifeTimePoints = displayTemplateArr[2];
					loyaltyEarnedToday = displayTemplateArr[3];
					pointsRedeemedToday = displayTemplateArr[8];
					pointsBalance = displayTemplateArr[4];
					currencyBalance = displayTemplateArr[5];
				}
			}

			String userJSONSettings = PropertyUtil.getPropertyValueFromDB("DigitalReceiptSetting");
			DigitalReceiptResponse posDRResponse = validateUserDRSettings(userJSONSettings, user.getUserId());

			if (posDRResponse != null) {

				if (!displayTemplate.isEmpty())
					posDRResponse.getRESPONSEINFO().setDISPLAYTEMPLATE(displayTemplate);
				if (!Card.isEmpty())
					posDRResponse.getRESPONSEINFO().setCARDNUMBER(Card);
				// sivaram

				posDRResponse.getRESPONSEINFO()
						.setPOINTSBALANCE((pointsBalance != null && !pointsBalance.isEmpty() ? pointsBalance : ""));
				posDRResponse.getRESPONSEINFO().setCURRENCYBALANCE(
						(currencyBalance != null && !currencyBalance.isEmpty() ? currencyBalance : ""));
				posDRResponse.getRESPONSEINFO().getSTATUS().setERRORCODE(giveOnlySuccessCode ? "0" : posDRResponse.getRESPONSEINFO().getSTATUS().getERRORCODE());
				return posDRResponse;
			}

			String sendEreceipt = digitalReceipt.getHead().getEmailReceipt();

			if (sendEreceipt != null && !sendEreceipt.trim().isEmpty() && sendEreceipt.equals("N")
					&& !user.isReceiptOnSMS() && !user.isReceiptOnWA()) { //removing dependency on SMS to send WA
				logger.info("send DR flag empty/disabled in the request or receipt on sms is off");

				status = new SendDRStatus("0", PropertyUtil.getErrorMessage(300004, OCConstants.ERROR_DR_FLAG),
						OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
				posDRResponse = prepareResponseObject(status);
				if (!displayTemplate.isEmpty())
					posDRResponse.getRESPONSEINFO().setDISPLAYTEMPLATE(displayTemplate);
				if (!Card.isEmpty())
					posDRResponse.getRESPONSEINFO().setCARDNUMBER(Card);

				// sivaram

				posDRResponse.getRESPONSEINFO()
						.setPOINTSBALANCE((pointsBalance != null && !pointsBalance.isEmpty() ? pointsBalance : ""));
				posDRResponse.getRESPONSEINFO().setCURRENCYBALANCE(
						(currencyBalance != null && !currencyBalance.isEmpty() ? currencyBalance : ""));
				posDRResponse.getRESPONSEINFO().getSTATUS().setERRORCODE(giveOnlySuccessCode ? "0" : posDRResponse.getRESPONSEINFO().getSTATUS().getERRORCODE());
				return posDRResponse;
			}

			String templateContent = null;
			String userSelectedTemplate = null;
			String[] templateArr = null;
			Contacts contactobj = null;
			DigitalReceiptUserSettings digitalReceiptUserSettings = null;
			Receipt receipt = digitalReceipt.getBody().getReceipt();

			if (user.isZoneWise()) {
				String storeNo = receipt.getStore();
				String SubsidiaryNo = receipt.getSubsidiaryNumber();
				// find a zone of this SBS and store
				digitalReceiptUserSettings = getZoneMappedTemplate(user, storeNo, SubsidiaryNo);

				if (digitalReceiptUserSettings != null && !digitalReceiptUserSettings.isSettingEnable()) {
					status = new SendDRStatus(giveOnlySuccessCode ? "0" : "3000012",
							PropertyUtil.getErrorMessage(3000012, OCConstants.ERROR_DR_FLAG),
							OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					posDRResponse = prepareResponseObject(status);
					if (!displayTemplate.isEmpty())
						posDRResponse.getRESPONSEINFO().setDISPLAYTEMPLATE(displayTemplate);
					if (!Card.isEmpty())
						posDRResponse.getRESPONSEINFO().setCARDNUMBER(Card);

					// sivaram
					posDRResponse.getRESPONSEINFO()
							.setPOINTSBALANCE((pointsBalance != null && !pointsBalance.isEmpty() ? pointsBalance : ""));
					posDRResponse.getRESPONSEINFO().setCURRENCYBALANCE(
							(currencyBalance != null && !currencyBalance.isEmpty() ? currencyBalance : ""));
					
					return posDRResponse;
				}

			} else {
				DigitalReceiptUserSettingsDao digitalReceiptUserSettingsDao = (DigitalReceiptUserSettingsDao) ServiceLocator
						.getInstance().getDAOByName(OCConstants.DIGITAL_RECEIPT_USER_SETTINGS_DAO);
				digitalReceiptUserSettings = digitalReceiptUserSettingsDao.findByUserId(user.getUserId());
			}

			if (digitalReceiptUserSettings == null) {
				status = new SendDRStatus(giveOnlySuccessCode ? "0" :"3000011", PropertyUtil.getErrorMessage(3000011, OCConstants.ERROR_DR_FLAG),
						OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				posDRResponse = prepareResponseObject(status);
				if (!displayTemplate.isEmpty())
					posDRResponse.getRESPONSEINFO().setDISPLAYTEMPLATE(displayTemplate);
				if (!Card.isEmpty())
					posDRResponse.getRESPONSEINFO().setCARDNUMBER(Card);
				
				//sivaram
		    	posDRResponse.getRESPONSEINFO().setPOINTSBALANCE((pointsBalance != null && !pointsBalance.isEmpty() ? pointsBalance : ""));
		    	posDRResponse.getRESPONSEINFO().setCURRENCYBALANCE((currencyBalance != null && !currencyBalance.isEmpty() ? currencyBalance : ""));
			return posDRResponse;
			}

			if (!digitalReceiptUserSettings.isEnabled() && !digitalReceiptUserSettings.isSmsEnabled() && !user.isReceiptOnWA()) { //removing dependency on SMS to send WA
				status = new SendDRStatus(giveOnlySuccessCode ? "0" :"300004", PropertyUtil.getErrorMessage(300004, OCConstants.ERROR_DR_FLAG),
						OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				posDRResponse = prepareResponseObject(status);
				if (!displayTemplate.isEmpty())
					posDRResponse.getRESPONSEINFO().setDISPLAYTEMPLATE(displayTemplate);
				if (!Card.isEmpty())
					posDRResponse.getRESPONSEINFO().setCARDNUMBER(Card);
				//sivaram
		    	posDRResponse.getRESPONSEINFO().setPOINTSBALANCE((pointsBalance != null && !pointsBalance.isEmpty() ? pointsBalance : ""));
		    	posDRResponse.getRESPONSEINFO().setCURRENCYBALANCE((currencyBalance != null && !currencyBalance.isEmpty() ? currencyBalance : ""));
				return posDRResponse;
			}

			if (sendEreceipt != null && !sendEreceipt.trim().isEmpty() && sendEreceipt.equals("N")
					&& !digitalReceiptUserSettings.isSmsEnabled() && !user.isReceiptOnWA()) {	//removing dependency on SMS to send WA
				logger.info("send DR flag empty/disabled in the request or receipt on sms is off");

				status = new SendDRStatus("0", PropertyUtil.getErrorMessage(300004, OCConstants.ERROR_DR_FLAG),
						OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
				posDRResponse = prepareResponseObject(status);
				if (!displayTemplate.isEmpty())
					posDRResponse.getRESPONSEINFO().setDISPLAYTEMPLATE(displayTemplate);
				if (!Card.isEmpty())
					posDRResponse.getRESPONSEINFO().setCARDNUMBER(Card);
				//sivaram
		    	posDRResponse.getRESPONSEINFO().setPOINTSBALANCE((pointsBalance != null && !pointsBalance.isEmpty() ? pointsBalance : ""));
		    	posDRResponse.getRESPONSEINFO().setCURRENCYBALANCE((currencyBalance != null && !currencyBalance.isEmpty() ? currencyBalance : ""));
				return posDRResponse;
			}
			String emailId = digitalReceipt.getBody().getReceipt().getBillToEMail();
			String mobile = digitalReceipt.getBody().getReceipt().getBillToPhone1();
			boolean isEmailIDExists = (emailId != null && emailId.trim().length() != 0);
			boolean isEmailIdValid = Utility.validateEmail(emailId);
			boolean isMobileExists = mobile != null && mobile.trim().length() != 0;
			boolean isMobileValid = isMobileExists && Utility.phoneParse(mobile, user.getUserOrganization()) != null;

			mobile = !isMobileExists || !isMobileValid ? null : mobile;
			emailId = !isEmailIDExists || !isEmailIdValid ? null : emailId;

			if (digitalReceiptUserSettings.isEnabled()) {
				if (!digitalReceiptUserSettings.isSmsEnabled() && !user.isReceiptOnWA() //removing dependency on SMS to send WA
						|| (digitalReceiptUserSettings.isSmsEnabled() && (!isMobileExists || !isMobileValid))) {

					if (!isEmailIDExists) {
						status = new SendDRStatus(giveOnlySuccessCode ? "0" :"300009",
								PropertyUtil.getErrorMessage(300009, OCConstants.ERROR_DR_FLAG),
								OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
						posDRResponse = prepareResponseObject(status);
						if (!displayTemplate.isEmpty())
							posDRResponse.getRESPONSEINFO().setDISPLAYTEMPLATE(displayTemplate);
						if (!Card.isEmpty())
							posDRResponse.getRESPONSEINFO().setCARDNUMBER(Card);
						//sivaram
				    	posDRResponse.getRESPONSEINFO().setPOINTSBALANCE((pointsBalance != null && !pointsBalance.isEmpty() ? pointsBalance : ""));
				    	posDRResponse.getRESPONSEINFO().setCURRENCYBALANCE((currencyBalance != null && !currencyBalance.isEmpty() ? currencyBalance : ""));
						return posDRResponse;

					} else if (!isEmailIdValid) {
						status = new SendDRStatus(giveOnlySuccessCode ? "0" :"300010",
								PropertyUtil.getErrorMessage(300010, OCConstants.ERROR_DR_FLAG),
								OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
						posDRResponse = prepareResponseObject(status);
						if (!displayTemplate.isEmpty())
							posDRResponse.getRESPONSEINFO().setDISPLAYTEMPLATE(displayTemplate);
						if (!Card.isEmpty())
							posDRResponse.getRESPONSEINFO().setCARDNUMBER(Card);
						//sivaram
				    	posDRResponse.getRESPONSEINFO().setPOINTSBALANCE((pointsBalance != null && !pointsBalance.isEmpty() ? pointsBalance : ""));
				    	posDRResponse.getRESPONSEINFO().setCURRENCYBALANCE((currencyBalance != null && !currencyBalance.isEmpty() ? currencyBalance : ""));
						return posDRResponse;
					}
				}
			}
			if (digitalReceiptUserSettings.isSmsEnabled()) {
				if (!digitalReceiptUserSettings.isEnabled() && !user.isReceiptOnWA() //removing dependency on SMS to send WA
						|| (digitalReceiptUserSettings.isEnabled() && (!isEmailIDExists || !isEmailIdValid))) {
				if (!isMobileExists) {
						status = new SendDRStatus(giveOnlySuccessCode ? "0" :"3000014",
								PropertyUtil.getErrorMessage(3000014, OCConstants.ERROR_DR_FLAG),
								OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
						posDRResponse = prepareResponseObject(status);
						if (!displayTemplate.isEmpty())
							posDRResponse.getRESPONSEINFO().setDISPLAYTEMPLATE(displayTemplate);
						if (!Card.isEmpty())
							posDRResponse.getRESPONSEINFO().setCARDNUMBER(Card);
						//sivaram
				    	posDRResponse.getRESPONSEINFO().setPOINTSBALANCE((pointsBalance != null && !pointsBalance.isEmpty() ? pointsBalance : ""));
				    	posDRResponse.getRESPONSEINFO().setCURRENCYBALANCE((currencyBalance != null && !currencyBalance.isEmpty() ? currencyBalance : ""));
						return posDRResponse;
					} else if (!isMobileValid) {
						status = new SendDRStatus(giveOnlySuccessCode ? "0" :"300015",
								PropertyUtil.getErrorMessage(300015, OCConstants.ERROR_DR_FLAG),
								OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
						posDRResponse = prepareResponseObject(status);
						if (!displayTemplate.isEmpty())
							posDRResponse.getRESPONSEINFO().setDISPLAYTEMPLATE(displayTemplate);
						if (!Card.isEmpty())
							posDRResponse.getRESPONSEINFO().setCARDNUMBER(Card);
						//sivaram
				    	posDRResponse.getRESPONSEINFO().setPOINTSBALANCE((pointsBalance != null && !pointsBalance.isEmpty() ? pointsBalance : ""));
				    	posDRResponse.getRESPONSEINFO().setCURRENCYBALANCE((currencyBalance != null && !currencyBalance.isEmpty() ? currencyBalance : ""));
						return posDRResponse;
					} 
				}


			}

			userSelectedTemplate = digitalReceiptUserSettings.getSelectedTemplateName();
			
			//APP-4189
			List<CustomTender> customTenderList = digitalReceipt.getBody()!=null ? digitalReceipt.getBody().getCustomTender() : null;
			String type = Constants.STRING_NILL ;
			if(customTenderList != null) {
				for(CustomTender customTender : customTenderList) {
					if(customTender.getType()!=null && customTender.getType().equalsIgnoreCase(Constants.CNI) ) {
						type = Constants.CNI;
						// assign the cni number to the receipt object to replace #Receipt.CNI_No#
						receipt.setCNI_No(customTender.getNumber());
						digitalReceipt.getBody().setReceipt(receipt);
					}
				}
			}
			
			try {
				if(!type.isEmpty() && type.equals(Constants.CNI) && digitalReceiptUserSettings.isCreditNoteEnabled() && digitalReceiptUserSettings.getCNTemplateId() != null) {
					
					templateArr = getTemplateContent(digitalReceiptUserSettings.getCNTemplateId(), user.getUserId());
					templateContent = templateArr[0];
				}
			} catch (Exception e) {
				logger.error("error while getting credit note template");
			}
			
			if (templateContent==null && digitalReceiptUserSettings.getMyTemplateId() != null) {

				templateArr = getTemplateContent(digitalReceiptUserSettings.getMyTemplateId(), user.getUserId());
				templateContent = templateArr[0];

				if (templateContent == null) {
					status = new SendDRStatus(giveOnlySuccessCode ? "0" :"300006", PropertyUtil.getErrorMessage(300006, OCConstants.ERROR_DR_FLAG),
							OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					posDRResponse = prepareResponseObject(status);
					if (!displayTemplate.isEmpty())
						posDRResponse.getRESPONSEINFO().setDISPLAYTEMPLATE(displayTemplate);
					if (!Card.isEmpty())
						posDRResponse.getRESPONSEINFO().setCARDNUMBER(Card);
					//sivaram
			    	posDRResponse.getRESPONSEINFO().setPOINTSBALANCE((pointsBalance != null && !pointsBalance.isEmpty() ? pointsBalance : ""));
			    	posDRResponse.getRESPONSEINFO().setCURRENCYBALANCE((currencyBalance != null && !currencyBalance.isEmpty() ? currencyBalance : ""));
					return posDRResponse;
				}

			} else if(templateContent==null){

				templateArr = getSelectedSystemTemplateContent(userSelectedTemplate, user.getUserId());
				templateContent = templateArr[0];

				if (templateContent == null) {
					status = new SendDRStatus(giveOnlySuccessCode ? "0" :"300007", PropertyUtil.getErrorMessage(300007, OCConstants.ERROR_DR_FLAG),
							OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					posDRResponse = prepareResponseObject(status);
					if (!displayTemplate.isEmpty())
						posDRResponse.getRESPONSEINFO().setDISPLAYTEMPLATE(displayTemplate);
					if (!Card.isEmpty())
						posDRResponse.getRESPONSEINFO().setCARDNUMBER(Card);
					//sivaram
			    	posDRResponse.getRESPONSEINFO().setPOINTSBALANCE((pointsBalance != null && !pointsBalance.isEmpty() ? pointsBalance : ""));
			    	posDRResponse.getRESPONSEINFO().setCURRENCYBALANCE((currencyBalance != null && !currencyBalance.isEmpty() ? currencyBalance : ""));
					return posDRResponse;
				}

			}

			String subject = "Your Purchase Receipt";

			ProcessEReceipt processEReceipt = new ProcessEReceipt(subject, templateContent, emailId, templateArr[1],
					user, digitalReceipt.getBody(), digitalReceiptsJSON.getDrjsonId(), userSelectedTemplate,
					digitalReceiptUserSettings, digitalReceipt, mobile);

			if (digitalReceipt.getAction().equals(OCConstants.DIGITAL_RECEIPT_SERVICE_ACTION_SENDEMAIL)) {
				String isLoyaltyCustomer = digitalReceipt.getHead().getIsLoyaltyCustomer();
				if (user.getUserOrganization().getLoyaltyDisplayTemplate() != null
						&& user.getUserOrganization().isSendRealtimeLoyaltyStatus()
				// && isLoyaltyCustomer != null &&
				// (isLoyaltyCustomer.trim().equals("Y")||isLoyaltyCustomer.equals("True"))
				) {

					/*
					 * DRToLtyExtractionService drToLtyExtractionImpl =
					 * (DRToLtyExtractionService)ServiceLocator.getInstance().getServiceByName(
					 * OCConstants.DR_TO_LTY_EXTRACTION_IMPL);
					 * drToLtyExtractionImpl.processRequest(digitalReceiptsJSON.getDrjsonId()+"");
					 * displayTemplate = constructDisplayTemplate(user, digitalReceipt);
					 */
					processEReceipt.setDisplayTemp(displayTemplate);
					processEReceipt.setLifeTimePoints(lifeTimePoints);
					processEReceipt.setLoyaltyEarnedToday(loyaltyEarnedToday);
					processEReceipt.setPointsBalance(pointsBalance);
					processEReceipt.setRedeemedPoints(pointsRedeemedToday);
					processEReceipt.setCurrencyBalance(currencyBalance);
					processEReceipt.start();

				} else {

					processEReceipt.start();
				}
			} else if (digitalReceipt.getAction().equals(OCConstants.DIGITAL_RECEIPT_XML_ACTION_SENDEMAIL)) {
				processEReceipt.processDR();
			}
			// statusFlag = 1;

			status = new SendDRStatus("0", "Digital receipt submitted successfully . Mail would be sent in a moment.",
					OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
			if (!digitalReceiptUserSettings.isEnabled() && digitalReceiptUserSettings.isSmsEnabled())
				status = new SendDRStatus("0",
						"Digital receipt submitted successfully . SMS would be sent in a moment.",
						OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
			posDRResponse = prepareResponseObject(status, displayTemplate, Card);
			
			//sivaram
	    	posDRResponse.getRESPONSEINFO().setPOINTSBALANCE((pointsBalance != null && !pointsBalance.isEmpty() ? pointsBalance : ""));
	    	posDRResponse.getRESPONSEINFO().setCURRENCYBALANCE((currencyBalance != null && !currencyBalance.isEmpty() ? currencyBalance : ""));

			logger.debug("*************** EXITING processDigitalReceipt**************");
			return posDRResponse;

		} catch (Exception e) {
			logger.error("Exception ", e);
		}
		return null;
	}

	public DRToLtyExtractionData convertDRFromOtherSources(DigitalReceipt digitalReceipt, Users user,
			boolean mobileBasedEnroll) {

		try {
			Receipt receipt = digitalReceipt.getBody().getReceipt();
			DROptCultureDetails ocDetails = digitalReceipt.getOptcultureDetails();
			DRToLoyaltyExtractionReceipt DRReceipt = new DRToLoyaltyExtractionReceipt(receipt.getDocDate(),
					receipt.getInvcNum(), receipt.getDocTime(), receipt.getDocSID(), receipt.getStore(),
					receipt.getSubsidiaryNumber(), receipt.getTotal(), receipt.getSubtotal(), null, null, null, null,
					null, null, receipt.getRefDocSID(), receipt.getRefReceipt(), receipt.getRefStoreCode(),
					receipt.getSubsidiaryNumber(), receipt.getDiscount());
			DRReceipt.setBillToInfo1(receipt.getBillToInfo1());
			DRToLoyaltyExtractionCustomer customer = new DRToLoyaltyExtractionCustomer(receipt.getBillToCustSID(), null,
					receipt.getBillToFName(), receipt.getBillToLName(), receipt.getBillToPhone1(),
					receipt.getBillToEMail(), receipt.getBillToAddr1(), null, receipt.getBillToAddr2(),
					receipt.getBillToAddr3(), receipt.getBillToZip(), null, receipt.getBillToUDF1(),
					receipt.getBillToUDF2(), null, null, null, receipt.getBillToInfo2(), null);
			DRToLoyaltyExtractionParams headerFileds = new DRToLoyaltyExtractionParams(
					digitalReceipt.getHead().getEnrollCustomer(), digitalReceipt.getHead().getIsLoyaltyCustomer(),
					digitalReceipt.getHead().getRequestSource(), digitalReceipt.getHead().getRequestType());

			String userName = digitalReceipt.getHead().getUser().getUserName();
			String token = digitalReceipt.getHead().getUser().getToken();
			String orgId = digitalReceipt.getHead().getUser().getOrganizationId();

			DRToLoyaltyExtractionAuth authUser = new DRToLoyaltyExtractionAuth(userName, orgId, token);

			List<org.mq.optculture.model.DR.DRItem> DRitems = digitalReceipt.getBody().getItems();
			List<DRToLoyaltyExtractionItems> items = new ArrayList<DRToLoyaltyExtractionItems>();
			Map<String, DRToLoyaltyExtractionItems> distinctskus = new HashMap<String, DRToLoyaltyExtractionItems>();

			for (org.mq.optculture.model.DR.DRItem item : DRitems) {
				double itemQty = (Double.valueOf(item.getQty()));
				DRToLoyaltyExtractionItems drItem = new DRToLoyaltyExtractionItems(
						item.getItemCategory() != null && !item.getItemCategory().isEmpty() ? item.getItemCategory()
								: item.getDCSName(),
						(item.getDepartmentCode() != null && !item.getDepartmentCode().isEmpty())
								? item.getDepartmentCode()
								: "",
						(item.getItemClass() != null && !item.getItemClass().isEmpty()) ? item.getItemClass() : "",
						(item.getItemSubClass() != null && !item.getItemSubClass().isEmpty()) ? item.getItemSubClass()
								: "",
						(item.getDCS() != null && !item.getDCS().isEmpty()) ? item.getDCS() : "",
						(item.getVendorCode() != null && !item.getVendorCode().isEmpty()) ? item.getVendorCode() : "",
						(item.getUPC() != null && !item.getUPC().isEmpty()) ? item.getUPC() : "",
						item.getDocItemPrc() == null ? item.getInvcItemPrc() : item.getDocItemPrc(), item.getQty(),
						(item.getTax() != null && !item.getTax().isEmpty()) ? item.getTax() : "",
						(item.getDocItemDiscAmt() != null && !item.getDocItemDiscAmt().isEmpty())
								? item.getDocItemDiscAmt()
								: "",
						(item.getDepartmentName() != null && !item.getDepartmentName().isEmpty())
								? item.getDepartmentName()
								: "",
						(item.getItemClassName() != null && !item.getItemClassName().isEmpty())
								? item.getItemClassName()
								: "",
						(item.getItemSubClassName() != null && !item.getItemSubClassName().isEmpty())
								? item.getItemSubClassName()
								: "",
						(item.getVendorName() != null && !item.getVendorName().isEmpty()) ? item.getVendorName() : "",
						(item.getItemSID() != null && !item.getItemSID().isEmpty()) ? item.getItemSID() : "",
						(item.getRefStoreCode() != null && !item.getRefStoreCode().isEmpty()) ? item.getRefStoreCode()
								: "",
						(item.getRefSubsidiaryNumber() != null && !item.getRefSubsidiaryNumber().isEmpty())
								? item.getRefSubsidiaryNumber()
								: "",
						(item.getRefReceipt() != null && !item.getRefReceipt().isEmpty()) ? item.getRefReceipt() : "",
						(item.getRefDocSID() != null && !item.getRefDocSID().isEmpty()) ? item.getRefDocSID() : "",
						(item.getDiscountReason() != null && !item.getDiscountReason().isEmpty()
								? item.getDiscountReason()
								: ""));

				drItem.setDocItemOrigPrc(item.getDocItemOrigPrc() != null && !item.getDocItemOrigPrc().isEmpty()
						? item.getDocItemOrigPrc()
						: "");
				drItem.setInvcItemPrc(
						item.getInvcItemPrc() != null && !item.getInvcItemPrc().isEmpty() ? item.getInvcItemPrc() : "");
				drItem.setDiscount(item.getDocItemDiscAmt() != null && !item.getDocItemDiscAmt().isEmpty()
						? item.getDocItemDiscAmt()
						: (item.getDocItemDisc() != null && !item.getDocItemDisc().isEmpty() ? item.getDocItemDisc()
								: ""));
				if (receipt.getReceiptType() != null && !receipt.getReceiptType().isEmpty()
						&& receipt.getReceiptType().equalsIgnoreCase("2") && itemQty > 0)
					drItem.setQuantity(String.valueOf((itemQty *= -1)));
				items.add(drItem);
				distinctskus.put(item.getItemSID(), drItem);
			}

			/*
			 * ContactsLoyaltyDao contactsLoyaltyDao =
			 * (ContactsLoyaltyDao)ServiceLocator.getInstance().getDAOByName(OCConstants.
			 * CONTACTS_LOYALTY_DAO);
			 * 
			 * PropertyDescriptor cdi = null; String cardInfo = user.getCardInfo(); String
			 * cardInfoObjStr=Constants.STRING_NILL; if(cardInfo!=null) { cdi = new
			 * PropertyDescriptor(cardInfo, receipt.getClass()); Object cardInfoObj =
			 * cdi.getReadMethod().invoke(receipt); if(cardInfoObj != null &&
			 * !cardInfoObj.toString().isEmpty() ) { cardInfoObjStr =
			 * cardInfoObj.toString(); } } logger.info("cardInfoObjStr "+cardInfoObjStr);
			 * ContactsLoyalty contactsLoyalty = null; boolean mobileBasedEnroll=false;
			 * if(cardInfoObjStr == null || cardInfoObjStr.isEmpty()) { LoyaltyProgram
			 * defaultLoyaltyProgram = findDefaultProgram(user.getUserId());
			 * if(defaultLoyaltyProgram !=null) {
			 * if(defaultLoyaltyProgram.getMembershipType().equalsIgnoreCase(OCConstants.
			 * LOYALTY_MEMBERSHIP_TYPE_MOBILE)) { if((receipt.getBillToPhone1() != null &&
			 * !receipt.getBillToPhone1().trim().isEmpty())) {
			 * logger.info("no card number find with mobile based prog"); contactsLoyalty =
			 * contactsLoyaltyDao.findContLoyaltyByCardId(user.getUserId(),receipt.
			 * getBillToPhone1()); if(contactsLoyalty==null);mobileBasedEnroll=true; } } }
			 * }else { logger.info("find with card number "); //look up with card number
			 * contactsLoyalty =
			 * contactsLoyaltyDao.findContLoyaltyByCardId(user.getUserId(),cardInfoObjStr);
			 * } if(contactsLoyalty==null) { String custID = receipt.getBillToCustSID();
			 * if(custID!=null && !custID.isEmpty()){ contactsLoyalty =
			 * findLoyaltyCardInOCByCustId(custID, user.getUserId()); } }
			 * customer.setCardNumber(cardInfoObjStr);
			 * customer.setConLoyalty(contactsLoyalty);
			 */
			DRReceipt.setReceiptType(receipt.getReceiptType());
			DRToLtyExtractionData DRToLtyExtractionData = new DRToLtyExtractionData(customer, null, authUser,
					headerFileds, DRReceipt);

			// cc:qty:disc:1.0-2 (ratio)

			DRToLtyExtractionData.setItems(items);
			DRToLtyExtractionData.setMobileBasedEnroll(mobileBasedEnroll);

			return DRToLtyExtractionData;

		} catch (Exception e) {
			logger.error("Exception ", e);
			return null;
		}

	}

	public String[] constructDisplayTemplate(Users user, DigitalReceipt digitalReceipt, String cardNumber) {
		String[] retArr = new String[9];
		retArr[0] = "";
		retArr[1] = "";
		retArr[2] = "";
		retArr[3] = "";
		retArr[4] = "";
		retArr[5] = ""; // sivaram
		retArr[6] = "";
		retArr[7] = "";
		retArr[8] = "";
		try {
			String DispTemplate = user.getUserOrganization().getFBPTemplate() != null
					? user.getUserOrganization().getFBPTemplate()
					: user.getUserOrganization().getLoyaltyDisplayTemplate();
			String retTemplate = DispTemplate;
			CouponCodeEnqReq couponCodeEnqReq = prepareCoupEnqReq(user, digitalReceipt, cardNumber);
			CouponCodeEnquObj couponCodeEnquObj = new CouponCodeEnquObj();
			couponCodeEnquObj.setCOUPONCODEENQREQ(couponCodeEnqReq);
			Gson gson = new Gson();
			logger.debug("couponcodeEnqReq ====" + gson.toJson(couponCodeEnquObj));
			CouponCodeEnquiryServiceImpl couponEnqService = (CouponCodeEnquiryServiceImpl) ServiceLocator.getInstance()
					.getServiceByName(OCConstants.COUPON_CODE_ENQUIRY_BUSINESS_SERVICE);
			CoupnCodeEnqResponse coupoRespon = couponEnqService.processCouponRequest(couponCodeEnquObj);
			if (coupoRespon != null && coupoRespon.getCOUPONCODERESPONSE() != null
					&& coupoRespon.getCOUPONCODERESPONSE().getSTATUSINFO() != null
					&& coupoRespon.getCOUPONCODERESPONSE().getSTATUSINFO().getERRORCODE().equals("0")
					&& coupoRespon.getCOUPONCODERESPONSE().getCOUPONDISCOUNTINFO() != null) {

				CouponCodeResponse response = coupoRespon.getCOUPONCODERESPONSE();
				List<CouponDiscountInfo> coupDisc = response.getCOUPONDISCOUNTINFO();
				LoyaltyInfo ltyInfo = response.getLOYALTYINFO();
				String balancePoints = Constants.STRING_NILL;
				String balCurr = Constants.STRING_NILL;
				String reddemableCurr = Constants.STRING_NILL;
				String CheckReward = ltyInfo.getCHECKREWARDS();
				retArr[7] = CheckReward != null ? CheckReward :"";
				String pointsEarned = ltyInfo.getPOINTSEARNED() != null && ltyInfo.getPOINTSEARNED().isEmpty() ? "0"
						: ltyInfo.getPOINTSEARNED();
				String currencyEarned = ltyInfo.getCURRENCYEARNED() != null && ltyInfo.getCURRENCYEARNED().isEmpty() ? "0"
						: ltyInfo.getCURRENCYEARNED();
				String pointsredeemed = ltyInfo.getPOINTSREDEEMED() != null && ltyInfo.getPOINTSREDEEMED().isEmpty() ? "0" : ltyInfo.getPOINTSREDEEMED();
				String lifeTimePoints = Constants.STRING_NILL;
				String currSymbol = Utility.countryCurrencyMap.get(user.getCountryType());
				String currencySymbol = currSymbol != null ? currSymbol : "$";
				if (ltyInfo != null) {
					List<Balances> bals = ltyInfo.getBALANCES();
					for (Balances balances : bals) {

						if (balances.getVALUECODE().equalsIgnoreCase("Points"))
							balancePoints = balances.getAMOUNT();
						else if (balances.getVALUECODE().equalsIgnoreCase("USD"))
							balCurr = balances.getAMOUNT();
					}
					reddemableCurr = ltyInfo.getREDEEMABLEAMOUNT();
					lifeTimePoints = ltyInfo.getLIFETIMEPOINTS();
					retArr[2] = lifeTimePoints;
					retArr[3] = pointsEarned;
					retArr[6] = currencyEarned;
					retArr[1] = ltyInfo.getCARDNUMBER();
					retArr[4] = balancePoints; // sivaram
					retArr[5] = reddemableCurr;
					retArr[8] = pointsredeemed;

				} else {

				}
				// "Loyalty balance is TAG:LOYALTYINFO.Balances.POINTS/ points and
				// TAG:CURRENCYCODE/TAG:LOYALTYINFO.Balances.CURRENCY/.
				// \nCumulative loyalty points earned to date is TAG:LOYALTYINFO.LIFETIMEPOINTS/
				// points.\nRedemption currency available is
				// TAG:CURRENCYCODE/TAG:LOYALTYINFO.REDEEMABLEAMOUNT/.
				// Loyalty balance is <TAG:LOYALTYINFO.Balances.POINTS/> points and
				// <TAG:CURRENCYCODE/>
				// <TAG:LOYALTYINFO.Balances.CURRENCY/>.\nCumulative loyalty points earned to
				// date is
				// <TAG:LOYALTYINFO.LIFETIMEPOINTS/> points.\nRedemption currency available is
				// <TAG:CURRENCYCODE/><TAG:LOYALTYINFO.REDEEMABLEAMOUNT/>.\n\n
				// <TAG:DISCOUNTSHEADER><b>The below discounts are eligible to
				// redeem.</b></TAG:DISCOUNTSHEADER>\n

				StringBuffer discountLine = new StringBuffer("");
				StringBuffer nudgediscountLine = new StringBuffer("");
				StringBuffer nudgeLine = new StringBuffer("");
				/*
				 * Pattern p = Pattern.compile(Pattern.quote(
				 * "TAG:DISCOUNTSTAG:COUPONDISCOUNTINFO.COUPONCODE/") + "(.*?)" +
				 * Pattern.quote("/\nTAG:COUPONDISCOUNTINFO.DESCRIPTION/")); Matcher m =
				 * p.matcher(replacedDispTemplate); while (m.find()) { discountLine = new
				 * StringBuffer(m.group(1));
				 * 
				 * }
				 * 
				 * 
				 * Pattern p = Pattern.compile(Pattern.quote(
				 * "TAG:DISCOUNTSTAG:COUPONDISCOUNTINFO.COUPONCODE/") + "(.*?)" +
				 * Pattern.quote("/\nTAG:COUPONDISCOUNTINFO.DESCRIPTION/")); Matcher m =
				 * p.matcher(replacedDispTemplate); StringBuffer discountLine = new
				 * StringBuffer(""); StringBuffer nudgeLine = new StringBuffer(""); while
				 * (m.find()) { discountLine = new StringBuffer(m.group(1));
				 * 
				 * }
				 */
				// { "COUPONCODEENQREQ": { "HEADERINFO": {
				// "REQUESTID":"Promo_20200730110651678"},"COUPONCODEINFO": {
				// "COUPONCODE":"ALL", "SUBSIDIARYNUMBER":"1", "STORENUMBER":"1",
				// "SOURCETYPE":"Store", "DOCSID":"5437913104015659733", "RECEIPTNUMBER":"",
				// "RECEIPTAMOUNT":"218.87", "DISCOUNTAMOUNT":"0.00",
				// "CUSTOMERID":"5300706176317984764", "CARDNUMBER":"8580304500000010",
				// "PHONE":"9110362585",
				// "EMAIL":"proumya.acharya@optculture.com"},"PURCHASEDITEMS": [
				// {"ITEMCODE":"7038569135989002236", "ITEMDISCOUNT":"0.00", "QUANTITY":"1",
				// "ITEMPRICE":"16.88"},{"ITEMCODE":"7038569159074451452",
				// "ITEMDISCOUNT":"0.00", "QUANTITY":"1",
				// "ITEMPRICE":"16.88"},{"ITEMCODE":"7038607268528984060",
				// "ITEMDISCOUNT":"0.00", "QUANTITY":"1",
				// "ITEMPRICE":"17.88"},{"ITEMCODE":"7049139156276416508",
				// "ITEMDISCOUNT":"0.00", "QUANTITY":"1",
				// "ITEMPRICE":"12.99"},{"ITEMCODE":"7049155753883930620",
				// "ITEMDISCOUNT":"0.00", "QUANTITY":"1",
				// "ITEMPRICE":"19.99"},{"ITEMCODE":"7049179280531984380",
				// "ITEMDISCOUNT":"0.00", "QUANTITY":"1",
				// "ITEMPRICE":"49.99"},{"ITEMCODE":"7049190154466430972",
				// "ITEMDISCOUNT":"0.00", "QUANTITY":"1",
				// "ITEMPRICE":"39.99"},{"ITEMCODE":"7060445462136295420",
				// "ITEMDISCOUNT":"0.00", "QUANTITY":"1", "ITEMPRICE":"44.27"}],"USERDETAILS": {
				// "USERNAME":"Smartpromo", "ORGID":"Smartpromo", "TOKEN":"FEEICLOV0O6M6UIZ"}}}

				// <TAG:DISCOUNTS><TAG:COUPONDISCOUNTINFO.COUPONCODE/> expiry:
				// <TAG:COUPONDISCOUNTINFO.VALIDTO/>\n<TAG:COUPONDISCOUNTINFO.DESCRIPTION/>\n\n</TAG:DISCOUNTS>\n\n<TAG:NUDGESHEADER><b>Below
				// is the status of programs
				// entered.</b></TAG:NUDGESHEADER>\n<TAG:NUDGES><TAG:COUPONDISCOUNTINFO.DESCRIPTION/>\n<TAG:COUPONDISCOUNTINFO.NUDGEDESCRIPTION/>\n</TAG:NUDGES>
				for (CouponDiscountInfo couponDiscountInfo : coupDisc) {
					if (couponDiscountInfo.getNUDGEDESCRIPTION().equals(FREE_ITEM_NUDGE)) {
						if (nudgediscountLine.length() > 0)
							nudgediscountLine.append(StringUtils.substringBetween(DispTemplate, "<TAG:DISCOUNTNUDGE>",
									"</TAG:DISCOUNTNUDGE>"));
						// nudgeLine.append("<p>").append(couponDiscountInfo.gnudgeLineetDESCRIPTION()).append("</p>").append("<br></br><p>").append(couponDiscountInfo.getNUDGEDESCRIPTION()).append("</p>");
						/*
						 * if(DispTemplate.contains(
						 * "<TAG:DISCOUNTNUDGES><TAG:COUPONDISCOUNTINFO.DESCRIPTION/>")){
						 * nudgediscountLine.append(couponDiscountInfo.getDESCRIPTION()).append(
						 * StringUtils.substringBetween(DispTemplate,
						 * "<TAG:COUPONDISCOUNTINFO.DESCRIPTION/>",
						 * "<TAG:COUPONDISCOUNTINFO.NUDGEDESCRIPTION/>")); }
						 */
						String discNudgeLine = StringUtils.substringBetween(DispTemplate, "<TAG:DISCOUNTNUDGESHEADER>",
								"</TAG:DISCOUNTNUDGESHEADER>");
						if (discNudgeLine != null)
							discNudgeLine = discNudgeLine.replace("<TAG:COUPONDISCOUNTINFO.COUPONNAME/>",
									couponDiscountInfo.getCOUPONNAME());
						nudgediscountLine.append(discNudgeLine);// .append("</p>");
					}

				}
				for (CouponDiscountInfo couponDiscountInfo : coupDisc) {
					if (couponDiscountInfo.getNUDGEPROMOCODE().equals("NO")
							|| couponDiscountInfo.getNUDGEPROMOCODE().isEmpty()) {
						logger.debug("dicount==" + couponDiscountInfo.getCOUPONCODE());
						if (discountLine.length() > 0)
							discountLine.append("<br></br>");
						discountLine.append(couponDiscountInfo.getCOUPONCODE())
								.append(" expiry: " + couponDiscountInfo.getVALIDTO()).append("<br></br>")
								.append(couponDiscountInfo.getDESCRIPTION());

					} else {
						if (couponDiscountInfo.getNUDGEDESCRIPTION().equals(FREE_ITEM_NUDGE))
							continue;
						logger.debug("nudge==" + couponDiscountInfo.getCOUPONCODE());
						if (nudgeLine.length() > 0)
							nudgeLine.append(
									StringUtils.substringBetween(DispTemplate, "</TAG:NUDGESHEADER>", "<TAG:NUDGES>"));
						// nudgeLine.append("<p>").append(couponDiscountInfo.getDESCRIPTION()).append("</p>").append("<br></br><p>").append(couponDiscountInfo.getNUDGEDESCRIPTION()).append("</p>");
						if (DispTemplate.contains("<TAG:NUDGES><TAG:COUPONDISCOUNTINFO.DESCRIPTION/>")) {
							nudgeLine.append(couponDiscountInfo.getDESCRIPTION())
									.append(StringUtils.substringBetween(DispTemplate,
											"<TAG:COUPONDISCOUNTINFO.DESCRIPTION/>",
											"<TAG:COUPONDISCOUNTINFO.NUDGEDESCRIPTION/>"));
						}
						nudgeLine.append(couponDiscountInfo.getCOUPONNAME() + " ")
								.append(couponDiscountInfo.getNUDGEDESCRIPTION());// .append("</p>");
					}
				}
				logger.debug("discountLine==" + discountLine.toString());
				logger.debug("nudges===" + nudgeLine);
				// \n\nTAG:DISCOUNTSHEADER<b>The below discounts are eligible to
				// redeem.</b></TAG:DISCOUNTSHEADER>\nTAG:DISCOUNTSTAG:COUPONDISCOUNTINFO.COUPONCODE/
				// expiry:
				// TAG:COUPONDISCOUNTINFO.VALIDTO/\nTAG:COUPONDISCOUNTINFO.DESCRIPTION/\n\n</TAG:DISCOUNTS>\n\n
				// TAG:NUDGESHEADER<b>Below is the status of programs
				// entered.</b></TAG:NUDGESHEADER>\nTAG:NUDGESTAG:COUPONDISCOUNTINFO.DESCRIPTION/\nTAG:COUPONDISCOUNTINFO.NUDGEDESCRIPTION/\n\n</TAG:NUDGES>
				// \n\n";
				// Loyalty balance is <TAG:LOYALTYINFO.Balances.POINTS/> points and
				// <TAG:CURRENCYCODE/><TAG:LOYALTYINFO.Balances.CURRENCY/>.\nCumulative loyalty
				// points earned to date is
				// <TAG:LOYALTYINFO.LIFETIMEPOINTS/> points.\nRedemption currency available is
				// <TAG:CURRENCYCODE/><TAG:LOYALTYINFO.REDEEMABLEAMOUNT/>.\n\n
				// <TAG:DISCOUNTSHEADER><b>The below discounts are eligible to redeem.</b>
				// </TAG:DISCOUNTSHEADER>\n<TAG:DISCOUNTS><TAG:COUPONDISCOUNTINFO.COUPONCODE/>
				// expiry:
				// <TAG:COUPONDISCOUNTINFO.VALIDTO/>\n<TAG:COUPONDISCOUNTINFO.DESCRIPTION/>\n\n</TAG:DISCOUNTS>\n\n
				// <TAG:NUDGESHEADER><b>Below is the status of programs
				// entered.</b></TAG:NUDGESHEADER>\n<TAG:NUDGES><TAG:COUPONDISCOUNTINFO.DESCRIPTION/>\n<TAG:COUPONDISCOUNTINFO.NUDGEDESCRIPTION/>\n</TAG:NUDGES>
				logger.debug("DispTemplate==" + DispTemplate);

				/*
				 * Loyalty balance is <TAG:LOYALTYINFO.Balances.POINTS/> points and
				 * <TAG:CURRENCYCODE/><TAG:LOYALTYINFO.Balances.CUR RENCY/>.\nCumulative loyalty
				 * points earned to date is <TAG:LOYALTYINFO.LIFETIMEPOINTS/>
				 * points.\nRedemption currency available is <TAG:CURRENC
				 * YCODE/><TAG:LOYALTYINFO.REDEEMABLEAMOUNT/>.\n\n<TAG:DISCOUNTSHEADER><b>The
				 * below discounts are eligible to redeem.</b></TAG:DISCOUNTSHEADER>\n
				 * <TAG:DISCOUNTS><TAG:COUPONDISCOUNTINFO.COUPONCODE/> expiry:
				 * <TAG:COUPONDISCOUNTINFO.VALIDTO/>\n<TAG:COUPONDISCOUNTINFO.DESCRIPTION/>\n\n<
				 * /TAG: DISCOUNTS>\n\n<TAG:NUDGESHEADER><b>Below is the status of programs
				 * entered.</b></TAG:NUDGESHEADER>\n<TAG:NUDGES><TAG:COUPONDISCOUNTINFO.DESCRI
				 * PTION/>\n<TAG:COUPONDISCOUNTINFO.NUDGEDESCRIPTION/>\n</TAG:NUDGES>
				 */
				// Loyalty balance is 6 points and <U+20B9>465.0.\nCumulative loyalty points
				// earned to date is 940 points.\nRedemption currency available is
				// <U+20B9>465.0.\n\n<b>
//Loyalty balance is <TAG:LOYALTYINFO.Balances.POINTS/> points and <TAG:CURRENCYCODE/><TAG:LOYALTYINFO.Balances.CURRENCY/>.\n
				// Cumulative loyalty points earned to date is <TAG:LOYALTYINFO.LIFETIMEPOINTS/>
				// points.\nRedemption currency available is
				// <TAG:CURRENCYCODE/><TAG:LOYALTYINFO.REDEEMABLEAMOUNT/>.\n\n<TAG:DISCOUNTSHEADER><b>The
				// below discounts are eligible to redeem.</b>
				// </TAG:DISCOUNTSHEADER>\n<TAG:DISCOUNTS><TAG:COUPONDISCOUNTINFO.COUPONCODE/>
				// expiry:
				// <TAG:COUPONDISCOUNTINFO.VALIDTO/>\n<TAG:COUPONDISCOUNTINFO.DESCRIPTION/>\n\n</TAG:DISCOUNTS><TAG:NUDGESHEADER><b>Below
				// is the status of programs
				// entered.</b></TAG:NUDGESHEADER>\n<TAG:NUDGES><TAG:COUPONDISCOUNTINFO.DESCRIPTION/>\n<TAG:COUPONDISCOUNTINFO.NUDGEDESCRIPTION/>\n\n</TAG:NUDGES>

				String ex = "<b><i>PetPeeps Points Earned:</b></i> <TAG:LOYALTYINFO.POINTSEARNED/> \n<b>"
						+ "<i>PetPeeps Points This Year:</b></i> <TAG:LOYALTYINFO.LIFETIMEPOINTS/> \n"
						+ "<b><i>PetPeeps Currency Available:</b></i> <TAG:CURRENCYCODE/><TAG:LOYALTYINFO.Balances.CURRENCY/> \n\n"
						+ "<TAG:NUDGESHEADER><b><i>Frequent Buyer Programs</b></i></TAG:NUDGESHEADER><TAG:NUDGES><i><TAG:COUPONDISCOUNTINFO.COUPONNAME/> <TAG:COUPONDISCOUNTINFO.NUDGEDESCRIPTION/>\n</i></TAG:NUDGES>";
				String discountNUdge = StringUtils.substringBetween(DispTemplate, "<TAG:DISCOUNTNUDGESHEADER>",
						"</TAG:DISCOUNTNUDGE>");
				if (discountNUdge != null) {
					DispTemplate = DispTemplate.replace(
							"<TAG:DISCOUNTNUDGESHEADER>" + discountNUdge + "</TAG:DISCOUNTNUDGE>",
							nudgediscountLine.toString());
				}
				String replacedDispTemplate = DispTemplate.replace("<TAG:LOYALTYINFO.Balances.POINTS/>", balancePoints)
						.replace("<TAG:CURRENCYCODE/>", currencySymbol)
						.replace("<TAG:LOYALTYINFO.Balances.CURRENCY/>", balCurr)
						.replace("<TAG:LOYALTYINFO.LIFETIMEPOINTS/>", lifeTimePoints)
						.replace("<TAG:LOYALTYINFO.REDEEMABLEAMOUNT/>", reddemableCurr)
						.replace("<TAG:LOYALTYINFO.POINTSEARNED/>", pointsEarned)
						.replace("<TAG:LOYALTYINFO.POINTSREDEEMED/>", pointsredeemed)
						.replace("<TAG:LOYALTYINFO.CURRENCYEARNED/>", currencyEarned)
						.replace(
								"<TAG:DISCOUNTSHEADER><b>The below discounts are eligible to redeem.</b></TAG:DISCOUNTSHEADER>\\n<TAG:DISCOUNTS><TAG:COUPONDISCOUNTINFO.COUPONCODE/> expiry: <TAG:COUPONDISCOUNTINFO.VALIDTO/>\\n<TAG:COUPONDISCOUNTINFO.DESCRIPTION/>\\n\\n</TAG:DISCOUNTS>",
								"")
						// .replace("<TAG:DISCOUNTS>","").replace("<TAG:COUPONDISCOUNTINFO.COUPONCODE/>","").replace("expiry:",
						// "").replace("<TAG:COUPONDISCOUNTINFO.VALIDTO/>\\n","").replace("<TAG:COUPONDISCOUNTINFO.DESCRIPTION/>\\n\\n",
						// discountLine.toString()).replace("</TAG:DISCOUNTS>\\n\\n", "")
						.replace("<TAG:NUDGESHEADER>", "").replace("<TAG:NUDGES>", "")
						.replace("<TAG:COUPONDISCOUNTINFO.COUPONNAME/>", "")
						.replace("<TAG:COUPONDISCOUNTINFO.DESCRIPTION/>\\n", "")
						.replace("<TAG:COUPONDISCOUNTINFO.NUDGEDESCRIPTION/>\\n", "")
						.replace("</TAG:NUDGES>", nudgeLine.toString()).replace("</TAG:NUDGESHEADER>\\n", "");

				logger.debug("replacedDispTemplate==" + replacedDispTemplate);
				retArr[0] = replacedDispTemplate;
				return retArr;
			}

		} catch (BaseServiceException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ", e);
		} catch (Exception e) {
			logger.error("Exception ", e);
		}
		return retArr;

	}

	public CouponCodeEnqReq prepareCoupEnqReq(Users user, DigitalReceipt digitalReceipt, String cardNumber) {

		CouponCodeEnqReq couponCodeEnqReq = new CouponCodeEnqReq();

		HeaderInfo headerInfo = new HeaderInfo();
		headerInfo.setREQUESTID("Promo_" + System.currentTimeMillis());

		Receipt receipt = digitalReceipt.getBody().getReceipt();
		CouponCodeInfo couponCodeInfo = new CouponCodeInfo("", "ALL", receipt.getStore(), receipt.getBillToCustSID(),
				receipt.getBillToPhone1(), receipt.getBillToEMail(), "", receipt.getSubsidiaryNumber(), "", "");
		String checkRewardGained = digitalReceipt.getOptcultureDetails() != null ? digitalReceipt.getOptcultureDetails().getCheckReward() : null;
		if(checkRewardGained != null && !checkRewardGained.isEmpty()) {
			couponCodeInfo.setCHECKREWARDS(checkRewardGained);
			
		}
		PropertyDescriptor cdi = null;
		String cardInfo = user.getCardInfo();
		String cardInfoObjStr = Constants.STRING_NILL;
		try {
			if (cardInfo != null) {
				cdi = new PropertyDescriptor(cardInfo, receipt.getClass());
				Object cardInfoObj = cdi.getReadMethod().invoke(receipt);
				if (cardInfoObj != null && !cardInfoObj.toString().isEmpty()) {
					cardInfoObjStr = cardInfoObj.toString();
				}
			}
		} catch (Exception e) {
			logger.error("Exception ", e);
		}
		couponCodeInfo.setRECEIPTAMOUNT("");
		couponCodeInfo.setDISCOUNTAMOUNT("");
		couponCodeInfo.setDOCSID(digitalReceipt.getBody().getReceipt().getDocSID());
		// couponCodeInfo.setCARDNUMBER(receipt.getBillToInfo1());
		couponCodeInfo.setCARDNUMBER(cardInfoObjStr == null || cardInfoObjStr.isEmpty()
				? (cardNumber == null || cardNumber.isEmpty() ? "" : cardNumber)
				: cardInfoObjStr);

		List<PurchasedItems> purchasedItems = new ArrayList<PurchasedItems>();

		UserDetails userDetails = new UserDetails(Utility.getOnlyUserName(user.getUserName()),
				Utility.getOnlyOrgId(user.getUserName()), user.getUserOrganization().getOptSyncKey());
		couponCodeEnqReq.setHEADERINFO(headerInfo);
		couponCodeEnqReq.setCOUPONCODEINFO(couponCodeInfo);
		couponCodeEnqReq.setPURCHASEDITEMS(purchasedItems);
		couponCodeEnqReq.setUSERDETAILS(userDetails);

		return couponCodeEnqReq;

	}

	@Override
	public DigitalReceiptResponse processPrismDRRequest(PrismBasedDRRequest sendDRRequest, String mode)
			throws BaseServiceException {

		// TODO Auto-generated method stub
		logger.info("Entered into processSendDRRequest service method....");
		SendDRResponse sendDRResponse = null;
		SendDRStatus status = null;
		try {

			PrismDRBody drBody = sendDRRequest.getBody();
			PrismDRHead drHead = sendDRRequest.getHead();
			PrismDRUser drUser = drHead.getUser();
			DROptCultureDetails ocDetails = sendDRRequest.getOptcultureDetails();

			String name = drUser.getUserName();
			String orgId = drUser.getOrganizationId();
			String token = drUser.getToken();

			// String emailId =
			// drBody.getBt_email();//(String)jsonReceiptObj.get("BillToEMail");
			String emailId = drBody.getBt_email();
					//ocDetails != null && ocDetails.getEmail() != null ? ocDetails.getEmail()
					//: drBody.getBt_email();// (String)jsonReceiptObj.get("BillToEMail");
			String storeNo = drBody.getOriginal_store_number();
			String storeCode = drBody.getOriginal_store_code();
			String sbsNo = drBody.getSubsidiary_number();
			String mobile = drBody.getBt_primary_phone_no(); //ocDetails != null && !ocDetails.getPhone().isEmpty()  ? ocDetails.getPhone()
					//: drBody.getBt_primary_phone_no();
			/*
			 * if(drBody.getCreated_datetime() != null &&
			 * !drBody.getCreated_datetime().isEmpty()) {
			 * 
			 * try { OffsetDateTime odt = OffsetDateTime.parse(
			 * drBody.getCreated_datetime());
			 * 
			 * String trxDate = odt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME ).replace(
			 * "T" , " " );
			 * 
			 * DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); Date date
			 * = (Date)formatter.parse(trxDate);
			 * 
			 * formatter = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss aaa"); String docDate =
			 * formatter.format(date); drBody.setCreated_datetime(docDate);
			 * 
			 * } catch (ParseException e) { // TODO Auto-generated catch block }
			 * 
			 * }
			 */

			name = name.trim();
			orgId = orgId.trim();
			token = token.trim();

			Users user = getUser(name, orgId, token);

			if (user == null) {
				status = new SendDRStatus("100011", PropertyUtil.getErrorMessage(100011, OCConstants.ERROR_DR_FLAG),
						OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				DigitalReceiptResponse posDRResponse = prepareResponseObject(status);
				return posDRResponse;
			}
			if (!user.isEnabled()) {
				status = new SendDRStatus("300011", PropertyUtil.getErrorMessage(300011, OCConstants.ERROR_DR_FLAG),
						OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				DigitalReceiptResponse posDRResponse = prepareResponseObject(status);
				return posDRResponse;
			}
			if (user.getPackageExpiryDate().before(Calendar.getInstance())) {
				status = new SendDRStatus("100012", PropertyUtil.getErrorMessage(100012, OCConstants.ERROR_DR_FLAG),
						OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				DigitalReceiptResponse posDRResponse = prepareResponseObject(status);
				return posDRResponse;
			}
			String requestType = sendDRRequest.getHead().getRequestType();
			String docSid = sendDRRequest.getBody().getSid();
			if (((requestType == null || requestType.isEmpty())
					|| (requestType != null && !requestType.isEmpty() && requestType.equalsIgnoreCase("new")))
					&& docSid != null && !docSid.isEmpty()) {
				// ignore the request
				boolean isexists = validateDuplicity(docSid, user.getUserId());
				if (isexists) {
					status = new SendDRStatus("100013", PropertyUtil.getErrorMessage(100013, OCConstants.ERROR_DR_FLAG),
							OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					DigitalReceiptResponse posDRResponse = prepareResponseObject(status);
					return posDRResponse;
				}

			}
			Gson gson = new Gson();
			String DRJson = gson.toJson(sendDRRequest);
			DigitalReceiptsJSON digitalReceiptsJSON = saveDRJson(user, DRJson, user.getUserId(), drBody.getSid(), mode,
					OCConstants.DR_SOURCE_TYPE_PRISM);
			String displayTemplate = Constants.STRING_NILL;
			String Card = Constants.STRING_NILL;
			String lifeTimePoints = Constants.STRING_NILL;
			String loyaltyEarnedToday = Constants.STRING_NILL;
			
			String pointsBalance= Constants.STRING_NILL;
			String currencyBalance=Constants.STRING_NILL;		
			String checkRewards = Constants.STRING_NILL;
			if (sendDRRequest.getAction().equals(OCConstants.DIGITAL_RECEIPT_SERVICE_ACTION_SENDEMAIL)) {
				String isLoyaltyCustomer = sendDRRequest.getHead().getIsLoyaltyCustomer();
				if (user.getUserOrganization().getLoyaltyDisplayTemplate() != null
						&& user.getUserOrganization().isSendRealtimeLoyaltyStatus())
				// isLoyaltyCustomer != null && isLoyaltyCustomer.trim().equals("Y"))
				{

					DRToLtyExtractionService drToLtyExtractionImpl = (DRToLtyExtractionService) ServiceLocator
							.getInstance().getServiceByName(OCConstants.DR_TO_LTY_EXTRACTION_IMPL);
					String cardNumber = drToLtyExtractionImpl.processRequest(digitalReceiptsJSON.getDrjsonId() + "");
					PrismRequestTranslator PrismRequestTranslator = new PrismRequestTranslator();
					DigitalReceipt dRJsonRequest = PrismRequestTranslator.convertPrismRequest(sendDRRequest);
					String[] displayTemplateArr = constructDisplayTemplate(user, dRJsonRequest, cardNumber);
					displayTemplate = displayTemplateArr[0];
					Card = displayTemplateArr[1];
					lifeTimePoints = displayTemplateArr[2];
					loyaltyEarnedToday = displayTemplateArr[3];
					pointsBalance = displayTemplateArr[4];
					currencyBalance = displayTemplateArr[5];
					checkRewards = displayTemplateArr[7];
					logger.debug("checkRewards===="+checkRewards );
				}
			}

			String sendEreceipt = drHead.getEmailReceipt();

			String userJSONSettings = PropertyUtil.getPropertyValueFromDB("DigitalReceiptSetting");
			DigitalReceiptResponse posDRResponse = validateUserDRSettings(userJSONSettings, user.getUserId());

			if (posDRResponse != null){
				if (!displayTemplate.isEmpty())
					posDRResponse.getRESPONSEINFO().setDISPLAYTEMPLATE(displayTemplate);
				if (!Card.isEmpty())
					posDRResponse.getRESPONSEINFO().setCARDNUMBER(Card);
				if(!checkRewards.isEmpty())
					posDRResponse.getRESPONSEINFO().setCHECKREWARD(checkRewards);
				//sivaram
		    	posDRResponse.getRESPONSEINFO().setPOINTSBALANCE((pointsBalance != null && !pointsBalance.isEmpty() ? pointsBalance : ""));
		    	posDRResponse.getRESPONSEINFO().setCURRENCYBALANCE((currencyBalance != null && !currencyBalance.isEmpty() ? currencyBalance : ""));
				return posDRResponse;
			}

			if (sendEreceipt != null && !sendEreceipt.trim().isEmpty()
					&& (sendEreceipt.equals("N") || sendEreceipt.equalsIgnoreCase("false"))) {
				logger.info("send DR flag empty/disabled in the request");
				status = new SendDRStatus("0", PropertyUtil.getErrorMessage(300004, OCConstants.ERROR_DR_FLAG),
						OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
				posDRResponse = prepareResponseObject(status);
				if (!displayTemplate.isEmpty())
					posDRResponse.getRESPONSEINFO().setDISPLAYTEMPLATE(displayTemplate);
				if (!Card.isEmpty())
					posDRResponse.getRESPONSEINFO().setCARDNUMBER(Card);
				if(!checkRewards.isEmpty())
					posDRResponse.getRESPONSEINFO().setCHECKREWARD(checkRewards);
				//sivaram
		    	posDRResponse.getRESPONSEINFO().setPOINTSBALANCE((pointsBalance != null && !pointsBalance.isEmpty() ? pointsBalance : ""));
		    	posDRResponse.getRESPONSEINFO().setCURRENCYBALANCE((currencyBalance != null && !currencyBalance.isEmpty() ? currencyBalance : ""));
				return posDRResponse;
			}

			String templateContent = null;
			String userSelectedTemplate = null;
			String[] templateArr = null;
			DigitalReceiptUserSettings digitalReceiptUserSettings = null;

			if (user.isZoneWise()) {
				storeNo = drBody.getStore_number();
				String SubsidiaryNo = drBody.getSubsidiary_number();
				// find a zone of this SBS and store
				digitalReceiptUserSettings = getZoneMappedTemplate(user, storeNo, SubsidiaryNo);

				if (digitalReceiptUserSettings != null && !digitalReceiptUserSettings.isSettingEnable()) {
					status = new SendDRStatus("3000012",
							PropertyUtil.getErrorMessage(3000012, OCConstants.ERROR_DR_FLAG),
							OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					posDRResponse = prepareResponseObject(status);
					if (!displayTemplate.isEmpty())
						posDRResponse.getRESPONSEINFO().setDISPLAYTEMPLATE(displayTemplate);
					if (!Card.isEmpty())
						posDRResponse.getRESPONSEINFO().setCARDNUMBER(Card);
					if(!checkRewards.isEmpty())
						posDRResponse.getRESPONSEINFO().setCHECKREWARD(checkRewards);
					//sivaram
			    	posDRResponse.getRESPONSEINFO().setPOINTSBALANCE((pointsBalance != null && !pointsBalance.isEmpty() ? pointsBalance : ""));
			    	posDRResponse.getRESPONSEINFO().setCURRENCYBALANCE((currencyBalance != null && !currencyBalance.isEmpty() ? currencyBalance : ""));
					return posDRResponse;
				}

			} else {
				DigitalReceiptUserSettingsDao digitalReceiptUserSettingsDao = (DigitalReceiptUserSettingsDao) ServiceLocator
						.getInstance().getDAOByName(OCConstants.DIGITAL_RECEIPT_USER_SETTINGS_DAO);
				digitalReceiptUserSettings = digitalReceiptUserSettingsDao.findByUserId(user.getUserId());
			}

			if (digitalReceiptUserSettings == null) {
				status = new SendDRStatus("3000011", PropertyUtil.getErrorMessage(3000011, OCConstants.ERROR_DR_FLAG),
						OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				posDRResponse = prepareResponseObject(status);
				if (!displayTemplate.isEmpty())
					posDRResponse.getRESPONSEINFO().setDISPLAYTEMPLATE(displayTemplate);
				if (!Card.isEmpty())
					posDRResponse.getRESPONSEINFO().setCARDNUMBER(Card);
				if(!checkRewards.isEmpty())
					posDRResponse.getRESPONSEINFO().setCHECKREWARD(checkRewards);
				//sivaram
		    	posDRResponse.getRESPONSEINFO().setPOINTSBALANCE((pointsBalance != null && !pointsBalance.isEmpty() ? pointsBalance : ""));
		    	posDRResponse.getRESPONSEINFO().setCURRENCYBALANCE((currencyBalance != null && !currencyBalance.isEmpty() ? currencyBalance : ""));
				return posDRResponse;
			}

			if (!digitalReceiptUserSettings.isEnabled()) {// stop sending if the setting are paused for that zone
				if (digitalReceiptUserSettings.isSmsEnabled() && (mobile == null || mobile.trim().length() == 0)) {
					status = new SendDRStatus("3000012",
							PropertyUtil.getErrorMessage(3000012, OCConstants.ERROR_DR_FLAG),
							OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					posDRResponse = prepareResponseObject(status);
					if (!displayTemplate.isEmpty())
						posDRResponse.getRESPONSEINFO().setDISPLAYTEMPLATE(displayTemplate);
					if (!Card.isEmpty())
						posDRResponse.getRESPONSEINFO().setCARDNUMBER(Card);
					if(!checkRewards.isEmpty())
						posDRResponse.getRESPONSEINFO().setCHECKREWARD(checkRewards);
					//sivaram
			    	posDRResponse.getRESPONSEINFO().setPOINTSBALANCE((pointsBalance != null && !pointsBalance.isEmpty() ? pointsBalance : ""));
			    	posDRResponse.getRESPONSEINFO().setCURRENCYBALANCE((currencyBalance != null && !currencyBalance.isEmpty() ? currencyBalance : ""));
					return posDRResponse;
				}
			}
			if (sendEreceipt != null && !sendEreceipt.trim().isEmpty()
					&& (sendEreceipt.equals("Y") || sendEreceipt.equalsIgnoreCase("true"))) {
				if (emailId == null || emailId.trim().length() == 0) {
					status = new SendDRStatus("300009", PropertyUtil.getErrorMessage(300009, OCConstants.ERROR_DR_FLAG),
							OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					posDRResponse = prepareResponseObject(status);
					if (!displayTemplate.isEmpty())
						posDRResponse.getRESPONSEINFO().setDISPLAYTEMPLATE(displayTemplate);
					if (!Card.isEmpty())
						posDRResponse.getRESPONSEINFO().setCARDNUMBER(Card);
					if(!checkRewards.isEmpty())
						posDRResponse.getRESPONSEINFO().setCHECKREWARD(checkRewards);
					//sivaram
			    	posDRResponse.getRESPONSEINFO().setPOINTSBALANCE((pointsBalance != null && !pointsBalance.isEmpty() ? pointsBalance : ""));
			    	posDRResponse.getRESPONSEINFO().setCURRENCYBALANCE((currencyBalance != null && !currencyBalance.isEmpty() ? currencyBalance : ""));
					return posDRResponse;
				}
				if (!Utility.validateEmail(emailId)) {
					status = new SendDRStatus("300010", PropertyUtil.getErrorMessage(300010, OCConstants.ERROR_DR_FLAG),
							OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					posDRResponse = prepareResponseObject(status);
					if (!displayTemplate.isEmpty())
						posDRResponse.getRESPONSEINFO().setDISPLAYTEMPLATE(displayTemplate);
					if (!Card.isEmpty())
						posDRResponse.getRESPONSEINFO().setCARDNUMBER(Card);
					if(!checkRewards.isEmpty())
						posDRResponse.getRESPONSEINFO().setCHECKREWARD(checkRewards);					//sivaram
			    	posDRResponse.getRESPONSEINFO().setPOINTSBALANCE((pointsBalance != null && !pointsBalance.isEmpty() ? pointsBalance : ""));
			    	posDRResponse.getRESPONSEINFO().setCURRENCYBALANCE((currencyBalance != null && !currencyBalance.isEmpty() ? currencyBalance : ""));
					return posDRResponse;
				}

			}
			userSelectedTemplate = digitalReceiptUserSettings.getSelectedTemplateName();

			if (digitalReceiptUserSettings.getMyTemplateId() != null) {

				templateArr = getTemplateContent(digitalReceiptUserSettings.getMyTemplateId(), user.getUserId());
				templateContent = templateArr[0];

				if (templateContent == null) {
					status = new SendDRStatus("300006", PropertyUtil.getErrorMessage(300006, OCConstants.ERROR_DR_FLAG),
							OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					posDRResponse = prepareResponseObject(status);
					if (!displayTemplate.isEmpty())
						posDRResponse.getRESPONSEINFO().setDISPLAYTEMPLATE(displayTemplate);
					if (!Card.isEmpty())
						posDRResponse.getRESPONSEINFO().setCARDNUMBER(Card);
					if(!checkRewards.isEmpty())
						posDRResponse.getRESPONSEINFO().setCHECKREWARD(checkRewards);
					//sivaram
			    	posDRResponse.getRESPONSEINFO().setPOINTSBALANCE((pointsBalance != null && !pointsBalance.isEmpty() ? pointsBalance : ""));
			    	posDRResponse.getRESPONSEINFO().setCURRENCYBALANCE((currencyBalance != null && !currencyBalance.isEmpty() ? currencyBalance : ""));
					return posDRResponse;
				}

			} else {

				templateArr = getSelectedSystemTemplateContent(userSelectedTemplate, user.getUserId());
				templateContent = templateArr[0];

				if (templateContent == null) {
					status = new SendDRStatus("300007", PropertyUtil.getErrorMessage(300007, OCConstants.ERROR_DR_FLAG),
							OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					posDRResponse = prepareResponseObject(status);
					if (!displayTemplate.isEmpty())
						posDRResponse.getRESPONSEINFO().setDISPLAYTEMPLATE(displayTemplate);
					if (!Card.isEmpty())
						posDRResponse.getRESPONSEINFO().setCARDNUMBER(Card);
					if(!checkRewards.isEmpty())
						posDRResponse.getRESPONSEINFO().setCHECKREWARD(checkRewards);
					//sivaram
			    	posDRResponse.getRESPONSEINFO().setPOINTSBALANCE((pointsBalance != null && !pointsBalance.isEmpty() ? pointsBalance : ""));
			    	posDRResponse.getRESPONSEINFO().setCURRENCYBALANCE((currencyBalance != null && !currencyBalance.isEmpty() ? currencyBalance : ""));
					return posDRResponse;
				}

			}

			String subject = "Your Purchase Receipt";

			ProcessEReceipt processEReceipt = new ProcessEReceipt(subject, templateContent, emailId, templateArr[1],
					user, drBody, digitalReceiptsJSON.getDrjsonId(), userSelectedTemplate, digitalReceiptUserSettings,
					sendDRRequest, mobile);
			processEReceipt.setDisplayTemp(displayTemplate);
			processEReceipt.setLifeTimePoints(lifeTimePoints);
			processEReceipt.setLoyaltyEarnedToday(loyaltyEarnedToday);
			// processEReceipt.processPrismDR();

			if (sendDRRequest.getAction().equals(OCConstants.DIGITAL_RECEIPT_SERVICE_ACTION_SENDEMAIL)) {
				processEReceipt.start();
			} else if (sendDRRequest.getAction().equals(OCConstants.DIGITAL_RECEIPT_XML_ACTION_SENDEMAIL)) {
				processEReceipt.processPrismDR();
			}
			
			
			
			// statusFlag = 1;
			status = new SendDRStatus("0", "Digital receipt submitted successfully . Mail would be sent in a moment.",
					OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
			if (!digitalReceiptUserSettings.isEnabled() && digitalReceiptUserSettings.isSmsEnabled())
				status = new SendDRStatus("0",
						"Digital receipt submitted successfully . SMS would be sent in a moment.",
						OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
			// posDRResponse = prepareResponseObject(status);
			posDRResponse = prepareResponseObject(status, displayTemplate, Card);
			if(!checkRewards.isEmpty())
				posDRResponse.getRESPONSEINFO().setCHECKREWARD(checkRewards);
			//sivaram
	    	posDRResponse.getRESPONSEINFO().setPOINTSBALANCE((pointsBalance != null && !pointsBalance.isEmpty() ? pointsBalance : ""));
	    	posDRResponse.getRESPONSEINFO().setCURRENCYBALANCE((currencyBalance != null && !currencyBalance.isEmpty() ? currencyBalance : ""));
			
			logger.debug("*************** EXITING  processPrismDRRequest*********");
			return posDRResponse;

		} catch (Exception e) {
			logger.error("Exception ", e);
		}
		return null;

	}

	@Override
	public DigitalReceiptResponse processMagentoDRRequest(MagentoBasedDRRequest sendDRRequest, String mode)
			throws BaseServiceException {

		// TODO Auto-generated method stub
		logger.info("Entered into processMagentoDRRequest service method....");
		SendDRResponse sendDRResponse = null;
		SendDRStatus status = null;
		try {

			MagentoDRBody magentoDRBody = sendDRRequest.getBody();
			DRHead drHead = sendDRRequest.getHead();
			User drUser = drHead.getUser();

			String name = drUser.getUserName();
			String orgId = drUser.getOrganizationId();
			String token = drUser.getToken();

			String emailId = magentoDRBody.getCustomerdetails().getEmail();// (String)jsonReceiptObj.get("BillToEMail");
			String mobile = magentoDRBody.getCustomerdetails().getTelephone();
			String storeNo = magentoDRBody.getOrderdetails().getStore_id();
			// String storeCode = drBody.getOriginal_store_code();
			// String sbsNo = drBody.getSubsidiary_number();

			name = name.trim();
			orgId = orgId.trim();
			token = token.trim();

			Users user = getUser(name, orgId, token);

			if (user == null) {
				status = new SendDRStatus("100011", PropertyUtil.getErrorMessage(100011, OCConstants.ERROR_DR_FLAG),
						OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				DigitalReceiptResponse posDRResponse = prepareResponseObject(status);
				return posDRResponse;
			}
			if (!user.isEnabled()) {
				status = new SendDRStatus("300011", PropertyUtil.getErrorMessage(300011, OCConstants.ERROR_DR_FLAG),
						OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				DigitalReceiptResponse posDRResponse = prepareResponseObject(status);
				return posDRResponse;
			}
			if (user.getPackageExpiryDate().before(Calendar.getInstance())) {
				status = new SendDRStatus("100012", PropertyUtil.getErrorMessage(100012, OCConstants.ERROR_DR_FLAG),
						OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				DigitalReceiptResponse posDRResponse = prepareResponseObject(status);
				return posDRResponse;
			}
			String requestType = sendDRRequest.getHead().getRequestType();
			String docSid = sendDRRequest.getBody().getOrderdetails().getStatus() != null
					&& sendDRRequest.getBody().getOrderdetails().getStatus().equalsIgnoreCase("canceled")
							? sendDRRequest.getBody().getOrderdetails().getEntity_id()
							: sendDRRequest.getBody().getOrderdetails().getIncrement_id();
			if (requestType != null && !requestType.isEmpty() && requestType.equalsIgnoreCase("new") && docSid != null
					&& !docSid.isEmpty()) {
				// ignore the request
				boolean isexists = validateDuplicity(docSid, user.getUserId());
				if (isexists) {
					status = new SendDRStatus("100013", PropertyUtil.getErrorMessage(100013, OCConstants.ERROR_DR_FLAG),
							OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					DigitalReceiptResponse posDRResponse = prepareResponseObject(status);
					return posDRResponse;
				}

			}
			Gson gson = new Gson();
			String DRJson = gson.toJson(sendDRRequest);
			DigitalReceiptsJSON digitalReceiptsJSON = saveDRJson(user, DRJson, user.getUserId(), docSid, mode,
					OCConstants.DR_SOURCE_TYPE_Magento);
			String displayTemplate = Constants.STRING_NILL;
			String lifeTimePoints = Constants.STRING_NILL;
			
			String loyaltyEarnedToday = Constants.STRING_NILL;
			String pointsBalance= Constants.STRING_NILL;
			String currencyBalance=Constants.STRING_NILL;	
			String Card = Constants.STRING_NILL;
			if (sendDRRequest.getAction().equals(OCConstants.DIGITAL_RECEIPT_SERVICE_ACTION_SENDEMAIL)) {
				String isLoyaltyCustomer = sendDRRequest.getHead().getIsLoyaltyCustomer();
				if (user.getUserOrganization().getLoyaltyDisplayTemplate() != null
						&& user.getUserOrganization().isSendRealtimeLoyaltyStatus())
				// isLoyaltyCustomer != null && isLoyaltyCustomer.trim().equals("Y"))
				{

					DRToLtyExtractionService drToLtyExtractionImpl = (DRToLtyExtractionService) ServiceLocator
							.getInstance().getServiceByName(OCConstants.DR_TO_LTY_EXTRACTION_IMPL);
					String cardNumber = drToLtyExtractionImpl.processRequest(digitalReceiptsJSON.getDrjsonId() + "");
					MagentoRequestTranslator MagentoRequestTranslator = new MagentoRequestTranslator();
					DigitalReceipt dRJsonRequest = MagentoRequestTranslator.convertMagentoRequest(sendDRRequest, user);
					String[] displayTemplateArr = constructDisplayTemplate(user, dRJsonRequest, cardNumber);
					displayTemplate = displayTemplateArr[0];
					Card = displayTemplateArr[1];
					lifeTimePoints = displayTemplateArr[2];
					loyaltyEarnedToday = displayTemplateArr[3];
					pointsBalance = displayTemplateArr[4];
					currencyBalance = displayTemplateArr[5];
			
				}
			}

			String userJSONSettings = PropertyUtil.getPropertyValueFromDB("DigitalReceiptSetting");
			DigitalReceiptResponse posDRResponse = validateUserDRSettings(userJSONSettings, user.getUserId());

			if (posDRResponse != null)
				return posDRResponse;

			String sendEreceipt = drHead.getEmailReceipt();
			if (sendEreceipt != null && !sendEreceipt.trim().isEmpty() && sendEreceipt.equals("N")) {
				logger.info("send DR flag empty/disabled in the request");
				status = new SendDRStatus("0", PropertyUtil.getErrorMessage(300004, OCConstants.ERROR_DR_FLAG),
						OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
				posDRResponse = prepareResponseObject(status);
				return posDRResponse;
			}

			String templateContent = null;
			String userSelectedTemplate = null;
			String[] templateArr = null;
			DigitalReceiptUserSettings digitalReceiptUserSettings = null;

			if (user.isZoneWise()) {
				storeNo = magentoDRBody.getOrderdetails().getStore_id();
				// String SubsidiaryNo = drBody.getSubsidiary_number();
				// find a zone of this SBS and store
				digitalReceiptUserSettings = getZoneMappedTemplate(user, storeNo, null);

				if (digitalReceiptUserSettings != null && !digitalReceiptUserSettings.isSettingEnable()) {
					status = new SendDRStatus("3000012",
							PropertyUtil.getErrorMessage(3000012, OCConstants.ERROR_DR_FLAG),
							OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					posDRResponse = prepareResponseObject(status);
					return posDRResponse;
				}

			} else {
				DigitalReceiptUserSettingsDao digitalReceiptUserSettingsDao = (DigitalReceiptUserSettingsDao) ServiceLocator
						.getInstance().getDAOByName(OCConstants.DIGITAL_RECEIPT_USER_SETTINGS_DAO);
				digitalReceiptUserSettings = digitalReceiptUserSettingsDao.findByUserId(user.getUserId());
			}

			if (digitalReceiptUserSettings == null) {
				status = new SendDRStatus("3000011", PropertyUtil.getErrorMessage(3000011, OCConstants.ERROR_DR_FLAG),
						OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				posDRResponse = prepareResponseObject(status);
				return posDRResponse;
			}

			if (!digitalReceiptUserSettings.isEnabled()) {// stop sending if the setting are paused for that zone
				if (digitalReceiptUserSettings.isSmsEnabled() && (mobile == null || mobile.trim().length() == 0)) {
					status = new SendDRStatus("3000012",
							PropertyUtil.getErrorMessage(3000012, OCConstants.ERROR_DR_FLAG),
							OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					posDRResponse = prepareResponseObject(status);
					return posDRResponse;
				}
			}
			if (emailId == null || emailId.trim().length() == 0) {
				status = new SendDRStatus("300009", PropertyUtil.getErrorMessage(300009, OCConstants.ERROR_DR_FLAG),
						OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				posDRResponse = prepareResponseObject(status);
				return posDRResponse;
			} else if (!Utility.validateEmail(emailId)) {
				status = new SendDRStatus("300010", PropertyUtil.getErrorMessage(300010, OCConstants.ERROR_DR_FLAG),
						OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				posDRResponse = prepareResponseObject(status);
				return posDRResponse;
			}

			userSelectedTemplate = digitalReceiptUserSettings.getSelectedTemplateName();

			if (digitalReceiptUserSettings.getMyTemplateId() != null) {

				templateArr = getTemplateContent(digitalReceiptUserSettings.getMyTemplateId(), user.getUserId());
				templateContent = templateArr[0];

				if (templateContent == null) {
					status = new SendDRStatus("300006", PropertyUtil.getErrorMessage(300006, OCConstants.ERROR_DR_FLAG),
							OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					posDRResponse = prepareResponseObject(status);
					return posDRResponse;
				}

			} else {

				templateArr = getSelectedSystemTemplateContent(userSelectedTemplate, user.getUserId());
				templateContent = templateArr[0];

				if (templateContent == null) {
					status = new SendDRStatus("300007", PropertyUtil.getErrorMessage(300007, OCConstants.ERROR_DR_FLAG),
							OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					posDRResponse = prepareResponseObject(status);
					return posDRResponse;
				}

			}

			String subject = "Your Purchase Receipt";

			JSONObject jsonMainObj = null;
			DRBody drBody = null;
			try {
				MagentoRequestTranslator magentoRequestTranslator = new MagentoRequestTranslator();
				logger.debug("magentoDRBody ===" + magentoDRBody.getOrderdetails().getCreated_at());
				drBody = magentoRequestTranslator.convertMagentoRequest(drHead, magentoDRBody,
						sendDRRequest.getOptcultureDetails(), user);
				Gson gson1 = new Gson();
				String drRequestJson = gson1.toJson(drBody);
				jsonMainObj = (JSONObject) JSONValue.parse(drRequestJson);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				logger.error("Exception ", e1);
			}
			DigitalReceipt digitalReceipt = new DigitalReceipt();
			digitalReceipt.setHead(drHead);
			digitalReceipt.setBody(drBody);
			digitalReceipt.setOptcultureDetails(sendDRRequest.getOptcultureDetails());
			ProcessEReceipt processEReceipt = new ProcessEReceipt(subject, templateContent, emailId, templateArr[1],
					user, drBody, digitalReceiptsJSON.getDrjsonId(), userSelectedTemplate, digitalReceiptUserSettings,
					digitalReceipt, mobile);

			// processEReceipt.processPrismDR();
			processEReceipt.setDisplayTemp(displayTemplate);
			processEReceipt.setLifeTimePoints(lifeTimePoints);
			processEReceipt.setLoyaltyEarnedToday(loyaltyEarnedToday);
			if (sendDRRequest.getAction().equals(OCConstants.DIGITAL_RECEIPT_SERVICE_ACTION_SENDEMAIL)) {
				processEReceipt.start();
			} else if (sendDRRequest.getAction().equals(OCConstants.DIGITAL_RECEIPT_XML_ACTION_SENDEMAIL)) {
				processEReceipt.processDR();
			}
			// statusFlag = 1;

			status = new SendDRStatus("0", "Digital receipt submitted successfully . Mail would be sent in a moment.",
					OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
			//posDRResponse = prepareResponseObject(status);
			posDRResponse = prepareResponseObject(status, displayTemplate, Card);
			
			//sivaram
	    	posDRResponse.getRESPONSEINFO().setPOINTSBALANCE((pointsBalance != null && !pointsBalance.isEmpty() ? pointsBalance : ""));
	    	posDRResponse.getRESPONSEINFO().setCURRENCYBALANCE((currencyBalance != null && !currencyBalance.isEmpty() ? currencyBalance : ""));
			
			
			
			
			logger.debug("*************** EXITING   processMagentoDRRequest***************");
			return posDRResponse;

		} catch (Exception e) {
			logger.error("Exception ", e);
		}
		return null;

	}

	@Override
	public DigitalReceiptResponse processWooCommerceDRRequest(WooCommerceDRRequest sendDRRequest, String mode)
			throws BaseServiceException {

		// TODO Auto-generated method stub
		logger.info("Entered into processMagentoDRRequest service method....");
		SendDRResponse sendDRResponse = null;
		SendDRStatus status = null;
		try {

			WooCommerceDRBody wooCommerceDRBody = sendDRRequest.getBody();
			DRHead drHead = sendDRRequest.getHead();
			User drUser = drHead.getUser();

			String name = drUser.getUserName();
			String orgId = drUser.getOrganizationId();
			String token = drUser.getToken();

			String emailId = wooCommerceDRBody.getCustomerdetails().getEmail();// (String)jsonReceiptObj.get("BillToEMail");
			String mobile = wooCommerceDRBody.getCustomerdetails().getBilling_address().getPhone();
			// String storeNo = magentoDRBody.getOrderdetails().getStore_id();

			name = name.trim();
			orgId = orgId.trim();
			token = token.trim();

			Users user = getUser(name, orgId, token);

			if (user == null) {
				status = new SendDRStatus("100011", PropertyUtil.getErrorMessage(100011, OCConstants.ERROR_DR_FLAG),
						OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				DigitalReceiptResponse posDRResponse = prepareResponseObject(status);
				return posDRResponse;
			}
			if (!user.isEnabled()) {
				status = new SendDRStatus("300011", PropertyUtil.getErrorMessage(300011, OCConstants.ERROR_DR_FLAG),
						OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				DigitalReceiptResponse posDRResponse = prepareResponseObject(status);
				return posDRResponse;
			}
			if (user.getPackageExpiryDate().before(Calendar.getInstance())) {
				status = new SendDRStatus("100012", PropertyUtil.getErrorMessage(100012, OCConstants.ERROR_DR_FLAG),
						OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				DigitalReceiptResponse posDRResponse = prepareResponseObject(status);
				return posDRResponse;
			}
			Gson gson = new Gson();
			String DRJson = gson.toJson(sendDRRequest);
			DigitalReceiptsJSON digitalReceiptsJSON = saveDRJson(user, DRJson, user.getUserId(),
					wooCommerceDRBody.getOrderdetails().getIncrement_id(), mode,
					OCConstants.DR_SOURCE_TYPE_WooCommerce);
			String displayTemplate = Constants.STRING_NILL;
			String lifeTimePoints = Constants.STRING_NILL;
			
			String loyaltyEarnedToday = Constants.STRING_NILL;	
			String Card = Constants.STRING_NILL;
			String pointsBalance = Constants.STRING_NILL;
			String currencyBalance = Constants.STRING_NILL;
			
			if (sendDRRequest.getAction().equals(OCConstants.DIGITAL_RECEIPT_SERVICE_ACTION_SENDEMAIL)) {
				String isLoyaltyCustomer = sendDRRequest.getHead().getIsLoyaltyCustomer();
				if (user.getUserOrganization().getLoyaltyDisplayTemplate() != null
						&& user.getUserOrganization().isSendRealtimeLoyaltyStatus())
				// isLoyaltyCustomer != null && isLoyaltyCustomer.trim().equals("Y"))
				{

					DRToLtyExtractionService drToLtyExtractionImpl = (DRToLtyExtractionService) ServiceLocator
							.getInstance().getServiceByName(OCConstants.DR_TO_LTY_EXTRACTION_IMPL);
					String cardNumber = drToLtyExtractionImpl.processRequest(digitalReceiptsJSON.getDrjsonId() + "");
					WooCommerceRequestTranslator wooCommerceRequestTranslator = new WooCommerceRequestTranslator();
					DigitalReceipt dRJsonRequest = wooCommerceRequestTranslator.convertWooCommerceRequest(sendDRRequest,
							user);
					String[] displayTemplateArr = constructDisplayTemplate(user, dRJsonRequest, cardNumber);
					displayTemplate = displayTemplateArr[0];
					Card = displayTemplateArr[1];
					lifeTimePoints = displayTemplateArr[2];
					loyaltyEarnedToday = displayTemplateArr[3];
					pointsBalance = displayTemplateArr[4];
					currencyBalance = displayTemplateArr[5];
				}
			}

			String userJSONSettings = PropertyUtil.getPropertyValueFromDB("DigitalReceiptSetting");
			DigitalReceiptResponse posDRResponse = validateUserDRSettings(userJSONSettings, user.getUserId());

			if (posDRResponse != null)
				return posDRResponse;

			String sendEreceipt = drHead.getEmailReceipt();
			if (sendEreceipt != null && !sendEreceipt.trim().isEmpty() && sendEreceipt.equals("N")) {
				logger.info("send DR flag empty/disabled in the request");
				status = new SendDRStatus("0", PropertyUtil.getErrorMessage(300004, OCConstants.ERROR_DR_FLAG),
						OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
				posDRResponse = prepareResponseObject(status);
				return posDRResponse;
			}

			String templateContent = null;
			String userSelectedTemplate = null;
			String[] templateArr = null;
			DigitalReceiptUserSettings digitalReceiptUserSettings = null;

			if (user.isZoneWise()) {
				 String storeNo = wooCommerceDRBody.getOrderdetails().getStore_id();
				 //String SubsidiaryNo = drBody.getSubsidiary_number();
				// find a zone of this SBS and store
				 digitalReceiptUserSettings = getZoneMappedTemplate(user, storeNo, null );

				if (digitalReceiptUserSettings != null && !digitalReceiptUserSettings.isSettingEnable()) {
					status = new SendDRStatus("3000012",
							PropertyUtil.getErrorMessage(3000012, OCConstants.ERROR_DR_FLAG),
							OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					posDRResponse = prepareResponseObject(status);
					return posDRResponse;
				}

			} else {
				DigitalReceiptUserSettingsDao digitalReceiptUserSettingsDao = (DigitalReceiptUserSettingsDao) ServiceLocator
						.getInstance().getDAOByName(OCConstants.DIGITAL_RECEIPT_USER_SETTINGS_DAO);
				digitalReceiptUserSettings = digitalReceiptUserSettingsDao.findByUserId(user.getUserId());
			}

			if (digitalReceiptUserSettings == null) {
				status = new SendDRStatus("3000011", PropertyUtil.getErrorMessage(3000011, OCConstants.ERROR_DR_FLAG),
						OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				posDRResponse = prepareResponseObject(status);
				return posDRResponse;
			}

			if (!digitalReceiptUserSettings.isEnabled()) {// stop sending if the setting are paused for that zone
				if (digitalReceiptUserSettings.isSmsEnabled() && (mobile == null || mobile.trim().length() == 0)) {
					status = new SendDRStatus("3000012",
							PropertyUtil.getErrorMessage(3000012, OCConstants.ERROR_DR_FLAG),
							OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					posDRResponse = prepareResponseObject(status);
					return posDRResponse;
				}
			}
			if (emailId == null || emailId.trim().length() == 0) {
				status = new SendDRStatus("300009", PropertyUtil.getErrorMessage(300009, OCConstants.ERROR_DR_FLAG),
						OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				posDRResponse = prepareResponseObject(status);
				return posDRResponse;
			} else if (!Utility.validateEmail(emailId)) {
				status = new SendDRStatus("300010", PropertyUtil.getErrorMessage(300010, OCConstants.ERROR_DR_FLAG),
						OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				posDRResponse = prepareResponseObject(status);
				return posDRResponse;
			}
			userSelectedTemplate = digitalReceiptUserSettings.getSelectedTemplateName();

			if (digitalReceiptUserSettings.getMyTemplateId() != null) {

				templateArr = getTemplateContent(digitalReceiptUserSettings.getMyTemplateId(), user.getUserId());
				templateContent = templateArr[0];

				if (templateContent == null) {
					status = new SendDRStatus("300006", PropertyUtil.getErrorMessage(300006, OCConstants.ERROR_DR_FLAG),
							OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					posDRResponse = prepareResponseObject(status);
					return posDRResponse;
				}

			} else {

				templateArr = getSelectedSystemTemplateContent(userSelectedTemplate, user.getUserId());
				templateContent = templateArr[0];

				if (templateContent == null) {
					status = new SendDRStatus("300007", PropertyUtil.getErrorMessage(300007, OCConstants.ERROR_DR_FLAG),
							OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					posDRResponse = prepareResponseObject(status);
					return posDRResponse;
				}

			}

			String subject = "Your Purchase Receipt";

			JSONObject jsonMainObj = null;
			DRBody drBody = null;
			try {
				WooCommerceRequestTranslator wooCommerceRequestTranslator = new WooCommerceRequestTranslator();
				logger.debug("wooCommerceDRBody ===" + wooCommerceDRBody.getOrderdetails().getCreated_at());
				drBody = wooCommerceRequestTranslator.convertWooCommerceRequest(drHead, wooCommerceDRBody,
						sendDRRequest.getOptcultureDetails(), user);
				Gson gson1 = new Gson();
				String drRequestJson = gson1.toJson(drBody);
				jsonMainObj = (JSONObject) JSONValue.parse(drRequestJson);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				logger.error("Exception ", e1);
			}
			DigitalReceipt digitalReceipt = new DigitalReceipt();
			digitalReceipt.setHead(drHead);
			digitalReceipt.setBody(drBody);
			digitalReceipt.setOptcultureDetails(sendDRRequest.getOptcultureDetails());
			ProcessEReceipt processEReceipt = new ProcessEReceipt(subject, templateContent, emailId, templateArr[1],
					user, drBody, digitalReceiptsJSON.getDrjsonId(), userSelectedTemplate, digitalReceiptUserSettings,
					digitalReceipt, mobile);

			// processEReceipt.processPrismDR();
			processEReceipt.setDisplayTemp(displayTemplate);
			processEReceipt.setLifeTimePoints(lifeTimePoints);
			processEReceipt.setLoyaltyEarnedToday(loyaltyEarnedToday);
			if (sendDRRequest.getAction().equals(OCConstants.DIGITAL_RECEIPT_SERVICE_ACTION_SENDEMAIL)) {
				processEReceipt.start();
			} else if (sendDRRequest.getAction().equals(OCConstants.DIGITAL_RECEIPT_XML_ACTION_SENDEMAIL)) {
				processEReceipt.processDR();
			}
			// statusFlag = 1;

			status = new SendDRStatus("0", "Digital receipt submitted successfully . Mail would be sent in a moment.",
					OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
			posDRResponse = prepareResponseObject(status, displayTemplate, Card);
			
			//sivaram
	    	posDRResponse.getRESPONSEINFO().setPOINTSBALANCE((pointsBalance != null && !pointsBalance.isEmpty() ? pointsBalance : ""));
	    	posDRResponse.getRESPONSEINFO().setCURRENCYBALANCE((currencyBalance != null && !currencyBalance.isEmpty() ? currencyBalance : ""));
			
			logger.debug("*************** EXITING processWooCommerceDRRequest***************");
			return posDRResponse;

		} catch (Exception e) {
			logger.error("Exception ", e);
		}
		return null;

	}

	@Override
	public DigitalReceiptResponse processWooCommerceRefundDRRequest(WooCommerceReturnDRRequest returnRequest,
			String mode) throws BaseServiceException {

		// TODO Auto-generated method stub
		logger.info("Entered into processWooCommerceReturnDRRequest service method....");
		SendDRResponse sendDRResponse = null;
		SendDRStatus status = null;
		try {

			WooCommerceReturnDRBody wooCommerceDRBody = returnRequest.getBody();
			DRHead drHead = returnRequest.getHead();
			User drUser = drHead.getUser();

			String name = drUser.getUserName();
			String orgId = drUser.getOrganizationId();
			String token = drUser.getToken();

			String emailId = wooCommerceDRBody.getCustomerdetails().getEmail();// (String)jsonReceiptObj.get("BillToEMail");
			String mobile = wooCommerceDRBody.getCustomerdetails().getBilling_address().getPhone();
			// String storeNo = magentoDRBody.getOrderdetails().getStore_id();

			name = name.trim();
			orgId = orgId.trim();
			token = token.trim();

			Users user = getUser(name, orgId, token);

			if (user == null) {
				status = new SendDRStatus("100011", PropertyUtil.getErrorMessage(100011, OCConstants.ERROR_DR_FLAG),
						OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				DigitalReceiptResponse posDRResponse = prepareResponseObject(status);
				return posDRResponse;
			}
			if (!user.isEnabled()) {
				status = new SendDRStatus("300011", PropertyUtil.getErrorMessage(300011, OCConstants.ERROR_DR_FLAG),
						OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				DigitalReceiptResponse posDRResponse = prepareResponseObject(status);
				return posDRResponse;
			}
			if (user.getPackageExpiryDate().before(Calendar.getInstance())) {
				status = new SendDRStatus("100012", PropertyUtil.getErrorMessage(100012, OCConstants.ERROR_DR_FLAG),
						OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				DigitalReceiptResponse posDRResponse = prepareResponseObject(status);
				return posDRResponse;
			}
			Gson gson = new Gson();
			String DRJson = gson.toJson(returnRequest);
			DigitalReceiptsJSON digitalReceiptsJSON = saveDRJson(user, DRJson, user.getUserId(),
					wooCommerceDRBody.getOrderdetails().getIncrement_id(), mode,
					OCConstants.DR_SOURCE_TYPE_WooCommerce);

			String displayTemplate = Constants.STRING_NILL;
			String Card = Constants.STRING_NILL;
			if (returnRequest.getAction().equals(OCConstants.DIGITAL_RECEIPT_SERVICE_ACTION_SENDEMAIL)) {
				String isLoyaltyCustomer = returnRequest.getHead().getIsLoyaltyCustomer();
				if (user.getUserOrganization().getLoyaltyDisplayTemplate() != null
						&& user.getUserOrganization().isSendRealtimeLoyaltyStatus())
				// isLoyaltyCustomer != null && isLoyaltyCustomer.trim().equals("Y"))
				{

					DRToLtyExtractionService drToLtyExtractionImpl = (DRToLtyExtractionService) ServiceLocator
							.getInstance().getServiceByName(OCConstants.DR_TO_LTY_EXTRACTION_IMPL);
					String cardNumber = drToLtyExtractionImpl.processRequest(digitalReceiptsJSON.getDrjsonId() + "");
					WooCommerceRequestTranslator wooCommerceRequestTranslator = new WooCommerceRequestTranslator();
					DigitalReceipt dRJsonRequest = wooCommerceRequestTranslator
							.convertWooCommerceRefundRequest(returnRequest, user);
					String[] displayTemplateArr = constructDisplayTemplate(user, dRJsonRequest, cardNumber);
					displayTemplate = displayTemplateArr[0];
					Card = displayTemplateArr[1];
				}
			}

			String userJSONSettings = PropertyUtil.getPropertyValueFromDB("DigitalReceiptSetting");
			DigitalReceiptResponse posDRResponse = validateUserDRSettings(userJSONSettings, user.getUserId());

			if (posDRResponse != null)
				return posDRResponse;

			String sendEreceipt = drHead.getEmailReceipt();
			if (sendEreceipt != null && !sendEreceipt.trim().isEmpty() && sendEreceipt.equals("N")) {
				logger.info("send DR flag empty/disabled in the request");
				status = new SendDRStatus("0", PropertyUtil.getErrorMessage(300004, OCConstants.ERROR_DR_FLAG),
						OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
				posDRResponse = prepareResponseObject(status);
				return posDRResponse;
			}

			String templateContent = null;
			String userSelectedTemplate = null;
			String[] templateArr = null;
			DigitalReceiptUserSettings digitalReceiptUserSettings = null;

			if (user.isZoneWise()) {
				// storeNo = wooCommerceDRBody.getOrderdetails().getStore_id();
				// String SubsidiaryNo = drBody.getSubsidiary_number();
				// find a zone of this SBS and store
				// digitalReceiptUserSettings = getZoneMappedTemplate(user, storeNo, null );

				if (digitalReceiptUserSettings != null && !digitalReceiptUserSettings.isSettingEnable()) {
					status = new SendDRStatus("3000012",
							PropertyUtil.getErrorMessage(3000012, OCConstants.ERROR_DR_FLAG),
							OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					posDRResponse = prepareResponseObject(status);
					return posDRResponse;
				}

			} else {
				DigitalReceiptUserSettingsDao digitalReceiptUserSettingsDao = (DigitalReceiptUserSettingsDao) ServiceLocator
						.getInstance().getDAOByName(OCConstants.DIGITAL_RECEIPT_USER_SETTINGS_DAO);
				digitalReceiptUserSettings = digitalReceiptUserSettingsDao.findByUserId(user.getUserId());
			}

			if (digitalReceiptUserSettings == null) {
				status = new SendDRStatus("3000011", PropertyUtil.getErrorMessage(3000011, OCConstants.ERROR_DR_FLAG),
						OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				posDRResponse = prepareResponseObject(status);
				return posDRResponse;
			}

			if (!digitalReceiptUserSettings.isEnabled()) {// stop sending if the setting are paused for that zone
				if (digitalReceiptUserSettings.isSmsEnabled() && (mobile == null || mobile.trim().length() == 0)) {
					status = new SendDRStatus("3000012",
							PropertyUtil.getErrorMessage(3000012, OCConstants.ERROR_DR_FLAG),
							OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					posDRResponse = prepareResponseObject(status);
					return posDRResponse;
				}
			}
			if (emailId == null || emailId.trim().length() == 0) {
				status = new SendDRStatus("300009", PropertyUtil.getErrorMessage(300009, OCConstants.ERROR_DR_FLAG),
						OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				posDRResponse = prepareResponseObject(status);
				return posDRResponse;
			} else if (!Utility.validateEmail(emailId)) {
				status = new SendDRStatus("300010", PropertyUtil.getErrorMessage(300010, OCConstants.ERROR_DR_FLAG),
						OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				posDRResponse = prepareResponseObject(status);
				return posDRResponse;
			}
			userSelectedTemplate = digitalReceiptUserSettings.getSelectedTemplateName();

			if (digitalReceiptUserSettings.getMyTemplateId() != null) {

				templateArr = getTemplateContent(digitalReceiptUserSettings.getMyTemplateId(), user.getUserId());
				templateContent = templateArr[0];

				if (templateContent == null) {
					status = new SendDRStatus("300006", PropertyUtil.getErrorMessage(300006, OCConstants.ERROR_DR_FLAG),
							OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					posDRResponse = prepareResponseObject(status);
					return posDRResponse;
				}

			} else {

				templateArr = getSelectedSystemTemplateContent(userSelectedTemplate, user.getUserId());
				templateContent = templateArr[0];

				if (templateContent == null) {
					status = new SendDRStatus("300007", PropertyUtil.getErrorMessage(300007, OCConstants.ERROR_DR_FLAG),
							OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					posDRResponse = prepareResponseObject(status);
					return posDRResponse;
				}

			}

			String subject = "Your Purchase Receipt";

			JSONObject jsonMainObj = null;
			DRBody drBody = null;
			try {
				WooCommerceRequestTranslator wooCommerceRequestTranslator = new WooCommerceRequestTranslator();
				logger.debug("wooCommerceDRBody ===" + wooCommerceDRBody.getOrderdetails().getCreated_at());
				DigitalReceipt digitalReceipt = wooCommerceRequestTranslator
						.convertWooCommerceRefundRequest(returnRequest, user);
				drBody = digitalReceipt.getBody();
				Gson gson1 = new Gson();
				String drRequestJson = gson1.toJson(drBody);
				jsonMainObj = (JSONObject) JSONValue.parse(drRequestJson);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				logger.error("Exception ", e1);
			}
			DigitalReceipt digitalReceipt = new DigitalReceipt();
			digitalReceipt.setHead(drHead);
			digitalReceipt.setBody(drBody);
			digitalReceipt.setOptcultureDetails(returnRequest.getOptcultureDetails());
			ProcessEReceipt processEReceipt = new ProcessEReceipt(subject, templateContent, emailId, templateArr[1],
					user, drBody, digitalReceiptsJSON.getDrjsonId(), userSelectedTemplate, digitalReceiptUserSettings,
					digitalReceipt, mobile);

			// processEReceipt.processPrismDR();

			if (returnRequest.getAction().equals(OCConstants.DIGITAL_RECEIPT_SERVICE_ACTION_SENDEMAIL)) {
				processEReceipt.start();
			} else if (returnRequest.getAction().equals(OCConstants.DIGITAL_RECEIPT_XML_ACTION_SENDEMAIL)) {
				processEReceipt.processDR();
			}
			// statusFlag = 1;

			status = new SendDRStatus("0", "Digital receipt submitted successfully . Mail would be sent in a moment.",
					OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
			posDRResponse = prepareResponseObject(status);
			
			
			//display template not required 
			
			
			logger.debug("*************** EXITING  processWooCommerceRefundDRRequest********************");
			return posDRResponse;

		} catch (Exception e) {
			logger.error("Exception ", e);
		}
		return null;

	}

//for APP-3389
	@Override
	public DigitalReceiptResponse processHeartlandDRRequest(HeartlandDRRequest sendDRRequest, String mode)
			throws BaseServiceException {
		// TODO Auto-generated method stub
		logger.info("Entered into processHeartlandDRRequest service method....");
		SendDRStatus status = null;
		try {
			HeartlandDRBody heartlandDRBody = sendDRRequest.getBody();
			DRHead dRHead = sendDRRequest.getHead();
			User heartlandDRUser = dRHead.getUser();

			String name = heartlandDRUser.getUserName();
			String orgId = heartlandDRUser.getOrganizationId();
			String token = heartlandDRUser.getToken();

			String emailId = (heartlandDRBody.getCustomer() != null && heartlandDRBody.getCustomer().getEmail() != null
					&& !heartlandDRBody.getCustomer().getEmail().isEmpty())
							? heartlandDRBody.getCustomer().getEmail()
							: (sendDRRequest.getOptcultureDetails() != null
									? sendDRRequest.getOptcultureDetails().getEmail()
									: Constants.STRING_NILL);

			String mobile = (heartlandDRBody.getCustomer() != null
					&& heartlandDRBody.getCustomer().getPhone_number() != null
					&& !heartlandDRBody.getCustomer().getPhone_number().isEmpty())
							? heartlandDRBody.getCustomer().getPhone_number()
							: (sendDRRequest.getOptcultureDetails() != null
									? sendDRRequest.getOptcultureDetails().getPhone()
									: Constants.STRING_NILL);
			logger.info("email:" + emailId);
			name = name.trim();
			orgId = orgId.trim();
			token = token.trim();

			Users user = getUser(name, orgId, token);

			if (user == null) {
				status = new SendDRStatus("100011", PropertyUtil.getErrorMessage(100011, OCConstants.ERROR_DR_FLAG),
						OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				DigitalReceiptResponse posDRResponse = prepareResponseObject(status);
				return posDRResponse;
			}
			if (!user.isEnabled()) {
				status = new SendDRStatus("300011", PropertyUtil.getErrorMessage(300011, OCConstants.ERROR_DR_FLAG),
						OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				DigitalReceiptResponse posDRResponse = prepareResponseObject(status);
				return posDRResponse;
			}
			if (user.getPackageExpiryDate().before(Calendar.getInstance())) {
				status = new SendDRStatus("100012", PropertyUtil.getErrorMessage(100012, OCConstants.ERROR_DR_FLAG),
						OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				DigitalReceiptResponse posDRResponse = prepareResponseObject(status);
				return posDRResponse;
			}
			String requestType = sendDRRequest.getHead().getRequestType();

			String docSid = sendDRRequest.getBody().getTicket().getId();

			// (sendDRRequest.getBody().getReceipt().getOrder_number()!=null?sendDRRequest.getBody().getReceipt().getOrder_number():
			sendDRRequest.getBody().getTicket().getId();
			if (requestType != null && !requestType.isEmpty() && requestType.equalsIgnoreCase("new") && docSid != null
					&& !docSid.isEmpty()) {
				// ignore the request
				boolean isexists = validateDuplicity(docSid, user.getUserId());
				if (isexists) {
					status = new SendDRStatus("100013", PropertyUtil.getErrorMessage(100013, OCConstants.ERROR_DR_FLAG),
							OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					DigitalReceiptResponse posDRResponse = prepareResponseObject(status);
					return posDRResponse;
				}

			}
			Gson gson = new Gson();
			String DRJson = gson.toJson(sendDRRequest);

			DigitalReceiptsJSON digitalReceiptsJSON = saveDRJson(user, DRJson, user.getUserId(), docSid, mode,
					OCConstants.DR_SOURCE_TYPE_HEARTLAND);
			String displayTemplate = Constants.STRING_NILL;
			String Card = Constants.STRING_NILL;
			String lifeTimePoints = Constants.STRING_NILL;
			
			String loyaltyEarnedToday = Constants.STRING_NILL;	
			String pointsBalance = Constants.STRING_NILL;
			String currencyBalance = Constants.STRING_NILL;
			if (sendDRRequest.getAction().equals(OCConstants.DIGITAL_RECEIPT_SERVICE_ACTION_SENDEMAIL)) {
				String isLoyaltyCustomer = sendDRRequest.getHead().getIsLoyaltyCustomer();
				DRToLtyExtractionImpl drToLtyExtractionImpl = (DRToLtyExtractionImpl) ServiceLocator.getInstance()
						.getServiceByName(OCConstants.DR_TO_LTY_EXTRACTION_IMPL);
				if (user.getUserOrganization().getLoyaltyDisplayTemplate() != null
						&& user.getUserOrganization().isSendRealtimeLoyaltyStatus()) {

					String cardNumber = drToLtyExtractionImpl.processRequest(digitalReceiptsJSON.getDrjsonId() + "");
					HeartlandRequestTranslator heartlandRequestTranslator = new HeartlandRequestTranslator();
					DigitalReceipt dRJsonRequest = heartlandRequestTranslator.convertHeartlandRequest(sendDRRequest,
							user);
					String[] displayTemplateArr = constructDisplayTemplate(user, dRJsonRequest, cardNumber);
					displayTemplate = displayTemplateArr[0];
					Card = displayTemplateArr[1];
					lifeTimePoints = displayTemplateArr[2];
					loyaltyEarnedToday = displayTemplateArr[3];
					pointsBalance = displayTemplateArr[4];
					currencyBalance = displayTemplateArr[5];
				} else {
					if (isLoyaltyCustomer != null && !isLoyaltyCustomer.isEmpty()
							&& isLoyaltyCustomer.equalsIgnoreCase("Y")) {
						gson = new Gson();
						HeartlandRequestTranslator heartlandRequestTranslator = new HeartlandRequestTranslator();
						HeartlandDRRequest heartlandRequest = gson.fromJson(digitalReceiptsJSON.getJsonStr(),
								HeartlandDRRequest.class);
						DigitalReceipt dRJsonRequest = heartlandRequestTranslator
								.convertHeartlandRequest(heartlandRequest, user);
						ContactsLoyaltyDao contactsLoyaltyDao = (ContactsLoyaltyDao) ServiceLocator.getInstance()
								.getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
						PropertyDescriptor cdi = null;
						Receipt receipt = dRJsonRequest.getBody().getReceipt();
						String cardInfo = user.getCardInfo();
						String cardInfoObjStr = Constants.STRING_NILL;
						if (cardInfo != null) {
							cdi = new PropertyDescriptor(cardInfo, receipt.getClass());
							Object cardInfoObj = cdi.getReadMethod().invoke(receipt);
							if (cardInfoObj != null && !cardInfoObj.toString().isEmpty()) {
								cardInfoObjStr = cardInfoObj.toString();
							}
						}
						logger.info("cardInfoObjStr " + cardInfoObjStr);// seems u took shopify processing as reference
																		// here- im not sure this is valid in case
																		// heartland - make a note we will analyze later
						ContactsLoyalty contactsLoyalty = null;
						boolean mobileBasedEnroll = false;
						if (cardInfoObjStr == null || cardInfoObjStr.isEmpty()) {
							LoyaltyProgram defaultLoyaltyProgram = findDefaultProgram(user.getUserId());
							if (defaultLoyaltyProgram != null) {
								if (defaultLoyaltyProgram.getMembershipType()
										.equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE)) {
									if ((receipt.getBillToPhone1() != null
											&& !receipt.getBillToPhone1().trim().isEmpty())) {
										logger.info("no card number find with mobile based prog");
										contactsLoyalty = contactsLoyaltyDao.findContLoyaltyByCardId(user.getUserId(),
												receipt.getBillToPhone1());
										if (contactsLoyalty == null)
											;
										mobileBasedEnroll = true;
									} else {
										mobileBasedEnroll = true;
									}
								}
							}
						} else {
							logger.info("find with card number ");
							// look up with card number
							contactsLoyalty = contactsLoyaltyDao.findContLoyaltyByCardId(user.getUserId(),
									cardInfoObjStr);
						}
						if (contactsLoyalty == null) {

							// enroll and send the cardnumber back
							DRToLtyExtractionData DRToLtyExtractionData = convertDRFromOtherSources(dRJsonRequest, user,
									mobileBasedEnroll);
							// enrollment
							LoyaltyEnrollResponse enrolResponseObject = null;
							enrolResponseObject = drToLtyExtractionImpl.prepareEnrollFromDRRequest(user,
									mobileBasedEnroll, DRToLtyExtractionData.getReceipt(),
									DRToLtyExtractionData.getUser(), DRToLtyExtractionData.getCustomer(),
									DRToLtyExtractionData.getHeaderFileds().getRequestSource());// .info("performing
																								// enrollment");
							String responseJson = new Gson().toJson(enrolResponseObject, LoyaltyEnrollResponse.class);
							if (mobileBasedEnroll) {
								Card = enrolResponseObject.getMembership().getPhoneNumber();
							} else {
								Card = enrolResponseObject.getMembership().getCardNumber();
							}
							logger.info("Enrollment response : " + responseJson);
							logger.info("enrolled card : " + Card);

						} else
							Card = contactsLoyalty.getCardNumber();
					}
				}

			}
			String userJSONSettings = PropertyUtil.getPropertyValueFromDB("DigitalReceiptSetting");
			DigitalReceiptResponse posDRResponse = validateUserDRSettings(userJSONSettings, user.getUserId());

			if (posDRResponse != null) {
				if (!Card.isEmpty())
					posDRResponse.getRESPONSEINFO().setCARDNUMBER(Card);
				//sivaram
		    	posDRResponse.getRESPONSEINFO().setPOINTSBALANCE((pointsBalance != null && !pointsBalance.isEmpty() ? pointsBalance : ""));
		    	posDRResponse.getRESPONSEINFO().setCURRENCYBALANCE((currencyBalance != null && !currencyBalance.isEmpty() ? currencyBalance : ""));
				if (!displayTemplate.isEmpty())
					posDRResponse.getRESPONSEINFO().setDISPLAYTEMPLATE(displayTemplate);
				return posDRResponse;
			}

			String sendEreceipt = dRHead.getEmailReceipt();
			if (sendEreceipt != null && !sendEreceipt.trim().isEmpty() && sendEreceipt.equals("N")) {
				logger.info("send DR flag empty/disabled in the request");
				status = new SendDRStatus("0", PropertyUtil.getErrorMessage(300004, OCConstants.ERROR_DR_FLAG),
						OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
				posDRResponse = prepareResponseObject(status);
				if (!displayTemplate.isEmpty())
					posDRResponse.getRESPONSEINFO().setDISPLAYTEMPLATE(displayTemplate);
				if (!Card.isEmpty())
					posDRResponse.getRESPONSEINFO().setCARDNUMBER(Card);
				//sivaram
		    	posDRResponse.getRESPONSEINFO().setPOINTSBALANCE((pointsBalance != null && !pointsBalance.isEmpty() ? pointsBalance : ""));
		    	posDRResponse.getRESPONSEINFO().setCURRENCYBALANCE((currencyBalance != null && !currencyBalance.isEmpty() ? currencyBalance : ""));
				return posDRResponse;
			}

			String templateContent = null;
			String userSelectedTemplate = null;
			String[] templateArr = null;
			DigitalReceiptUserSettings digitalReceiptUserSettings = null;

			if (user.isZoneWise()) {
				String storeNo = heartlandDRBody.getTicket().getSource_location_id() + "";
				digitalReceiptUserSettings = getZoneMappedTemplate(user, storeNo, null);

				if (digitalReceiptUserSettings != null && !digitalReceiptUserSettings.isSettingEnable()) {
					status = new SendDRStatus("3000012",
							PropertyUtil.getErrorMessage(3000012, OCConstants.ERROR_DR_FLAG),
							OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					posDRResponse = prepareResponseObject(status);
					if (!displayTemplate.isEmpty())
						posDRResponse.getRESPONSEINFO().setDISPLAYTEMPLATE(displayTemplate);
					if (!Card.isEmpty())
						posDRResponse.getRESPONSEINFO().setCARDNUMBER(Card);
					//sivaram
			    	posDRResponse.getRESPONSEINFO().setPOINTSBALANCE((pointsBalance != null && !pointsBalance.isEmpty() ? pointsBalance : ""));
			    	posDRResponse.getRESPONSEINFO().setCURRENCYBALANCE((currencyBalance != null && !currencyBalance.isEmpty() ? currencyBalance : ""));
					return posDRResponse;
				}

			} else {
				DigitalReceiptUserSettingsDao digitalReceiptUserSettingsDao = (DigitalReceiptUserSettingsDao) ServiceLocator
						.getInstance().getDAOByName(OCConstants.DIGITAL_RECEIPT_USER_SETTINGS_DAO);
				digitalReceiptUserSettings = digitalReceiptUserSettingsDao.findByUserId(user.getUserId());
			}

			if (digitalReceiptUserSettings == null) {
				status = new SendDRStatus("3000011", PropertyUtil.getErrorMessage(3000011, OCConstants.ERROR_DR_FLAG),
						OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				posDRResponse = prepareResponseObject(status);
				if (!displayTemplate.isEmpty())
					posDRResponse.getRESPONSEINFO().setDISPLAYTEMPLATE(displayTemplate);
				if (!Card.isEmpty())
					posDRResponse.getRESPONSEINFO().setCARDNUMBER(Card);
				//sivaram
		    	posDRResponse.getRESPONSEINFO().setPOINTSBALANCE((pointsBalance != null && !pointsBalance.isEmpty() ? pointsBalance : ""));
		    	posDRResponse.getRESPONSEINFO().setCURRENCYBALANCE((currencyBalance != null && !currencyBalance.isEmpty() ? currencyBalance : ""));
				return posDRResponse;
			}

			if (!digitalReceiptUserSettings.isEnabled()) {// stop sending if the setting are paused for that zone
				if (digitalReceiptUserSettings.isSmsEnabled() && (mobile == null || mobile.trim().length() == 0)) {
					status = new SendDRStatus("3000012",
							PropertyUtil.getErrorMessage(3000012, OCConstants.ERROR_DR_FLAG),
							OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);

					posDRResponse = prepareResponseObject(status);
				}
				if (!displayTemplate.isEmpty())
					posDRResponse.getRESPONSEINFO().setDISPLAYTEMPLATE(displayTemplate);
				if (!Card.isEmpty())
					posDRResponse.getRESPONSEINFO().setCARDNUMBER(Card);
				//sivaram
		    	posDRResponse.getRESPONSEINFO().setPOINTSBALANCE((pointsBalance != null && !pointsBalance.isEmpty() ? pointsBalance : ""));
		    	posDRResponse.getRESPONSEINFO().setCURRENCYBALANCE((currencyBalance != null && !currencyBalance.isEmpty() ? currencyBalance : ""));
				return posDRResponse;
			}
			if (emailId == null || emailId.trim().length() == 0) {
				status = new SendDRStatus("300009", PropertyUtil.getErrorMessage(300009, OCConstants.ERROR_DR_FLAG),
						OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				posDRResponse = prepareResponseObject(status);
				if (!Card.isEmpty())
					posDRResponse.getRESPONSEINFO().setCARDNUMBER(Card);
				//sivaram
		    	posDRResponse.getRESPONSEINFO().setPOINTSBALANCE((pointsBalance != null && !pointsBalance.isEmpty() ? pointsBalance : ""));
		    	posDRResponse.getRESPONSEINFO().setCURRENCYBALANCE((currencyBalance != null && !currencyBalance.isEmpty() ? currencyBalance : ""));
				if (!displayTemplate.isEmpty())
					posDRResponse.getRESPONSEINFO().setDISPLAYTEMPLATE(displayTemplate);
				return posDRResponse;
			} else if (!Utility.validateEmail(emailId)) {
				status = new SendDRStatus("300010", PropertyUtil.getErrorMessage(300010, OCConstants.ERROR_DR_FLAG),
						OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				posDRResponse = prepareResponseObject(status);
				if (!Card.isEmpty())
					posDRResponse.getRESPONSEINFO().setCARDNUMBER(Card);
				//sivaram
		    	posDRResponse.getRESPONSEINFO().setPOINTSBALANCE((pointsBalance != null && !pointsBalance.isEmpty() ? pointsBalance : ""));
		    	posDRResponse.getRESPONSEINFO().setCURRENCYBALANCE((currencyBalance != null && !currencyBalance.isEmpty() ? currencyBalance : ""));
				if (!displayTemplate.isEmpty())
					posDRResponse.getRESPONSEINFO().setDISPLAYTEMPLATE(displayTemplate);
				return posDRResponse;
			}
			userSelectedTemplate = digitalReceiptUserSettings.getSelectedTemplateName();

			if (digitalReceiptUserSettings.getMyTemplateId() != null) {

				templateArr = getTemplateContent(digitalReceiptUserSettings.getMyTemplateId(), user.getUserId());
				templateContent = templateArr[0];

				if (templateContent == null) {
					status = new SendDRStatus("300006", PropertyUtil.getErrorMessage(300006, OCConstants.ERROR_DR_FLAG),
							OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					posDRResponse = prepareResponseObject(status);
					if (!Card.isEmpty())
						posDRResponse.getRESPONSEINFO().setCARDNUMBER(Card);
					//sivaram
			    	posDRResponse.getRESPONSEINFO().setPOINTSBALANCE((pointsBalance != null && !pointsBalance.isEmpty() ? pointsBalance : ""));
			    	posDRResponse.getRESPONSEINFO().setCURRENCYBALANCE((currencyBalance != null && !currencyBalance.isEmpty() ? currencyBalance : ""));
					if (!displayTemplate.isEmpty())
						posDRResponse.getRESPONSEINFO().setDISPLAYTEMPLATE(displayTemplate);
					return posDRResponse;
				}

			} else {

				templateArr = getSelectedSystemTemplateContent(userSelectedTemplate, user.getUserId());
				templateContent = templateArr[0];

				if (templateContent == null) {
					status = new SendDRStatus("300007", PropertyUtil.getErrorMessage(300007, OCConstants.ERROR_DR_FLAG),
							OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					posDRResponse = prepareResponseObject(status);
					if (!Card.isEmpty())
						posDRResponse.getRESPONSEINFO().setCARDNUMBER(Card);
					//sivaram
			    	posDRResponse.getRESPONSEINFO().setPOINTSBALANCE((pointsBalance != null && !pointsBalance.isEmpty() ? pointsBalance : ""));
			    	posDRResponse.getRESPONSEINFO().setCURRENCYBALANCE((currencyBalance != null && !currencyBalance.isEmpty() ? currencyBalance : ""));
					if (!displayTemplate.isEmpty())
						posDRResponse.getRESPONSEINFO().setDISPLAYTEMPLATE(displayTemplate);
					return posDRResponse;
				}

			}

			String subject = "Your Purchase Receipt";

			JSONObject jsonMainObj = null;
			DigitalReceipt digitalReceipt = new DigitalReceipt();
			DRBody drBody = null;
			try {
				HeartlandRequestTranslator heartlandRequestTranslator = new HeartlandRequestTranslator();
				digitalReceipt = heartlandRequestTranslator.convertHeartlandRequest(sendDRRequest, user);
				drBody = digitalReceipt.getBody();
				Gson gson1 = new Gson();
				String drRequestJson = gson1.toJson(drBody);
				jsonMainObj = (JSONObject) JSONValue.parse(drRequestJson);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				logger.error("Exception ", e1);
			}

			// digitalReceipt.setHead(drHead);
			digitalReceipt.setBody(drBody);
			digitalReceipt.setOptcultureDetails(sendDRRequest.getOptcultureDetails());
			ProcessEReceipt processEReceipt = new ProcessEReceipt(subject, templateContent, emailId, templateArr[1],
					user, drBody, digitalReceiptsJSON.getDrjsonId(), userSelectedTemplate, digitalReceiptUserSettings,
					digitalReceipt, mobile);

			processEReceipt.setDisplayTemp(displayTemplate);
			processEReceipt.setLifeTimePoints(lifeTimePoints);
			processEReceipt.setLoyaltyEarnedToday(loyaltyEarnedToday);
			if (sendDRRequest.getAction().equals(OCConstants.DIGITAL_RECEIPT_SERVICE_ACTION_SENDEMAIL)) {
				processEReceipt.start();
			} else if (sendDRRequest.getAction().equals(OCConstants.DIGITAL_RECEIPT_XML_ACTION_SENDEMAIL)) {
				processEReceipt.processDR();
			}
			// SendDRStatus status = null;
			status = new SendDRStatus("0", "Digital receipt submitted successfully . Mail would be sent in a moment.",
					OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
			// DigitalReceiptResponse posDRResponse
			posDRResponse = prepareResponseObject(status, displayTemplate, Card);
			
			//sivaram
	    	posDRResponse.getRESPONSEINFO().setPOINTSBALANCE((pointsBalance != null && !pointsBalance.isEmpty() ? pointsBalance : ""));
	    	posDRResponse.getRESPONSEINFO().setCURRENCYBALANCE((currencyBalance != null && !currencyBalance.isEmpty() ? currencyBalance : ""));



			logger.debug("*************** EXITING processHeartlandDRRequest***********************");
			return posDRResponse;
		} catch (Exception e) {
			logger.error("Exception ", e);
		}
		return null;

	}

	@Override
	public DigitalReceiptResponse processShopifytoDRRequest(ShopifyBasedDRRequest sendDRRequest, String mode)
			throws BaseServiceException {
		// TODO Auto-generated method stub
		logger.info("Entered into processShopifytoDRRequest service method....");
		SendDRStatus status = null;
		try {
			ShopifyDRBody shopifyDRBody = sendDRRequest.getBody();
			DRHead drHead = sendDRRequest.getHead();
			User drUser = drHead.getUser();

			String name = drUser.getUserName();
			String orgId = drUser.getOrganizationId();
			String token = drUser.getToken();

			String emailId = shopifyDRBody.getReceipt().getCustomer() != null
					? shopifyDRBody.getReceipt().getCustomer().getEmail()
					: (sendDRRequest.getOptcultureDetails() != null ? sendDRRequest.getOptcultureDetails().getEmail()
							: Constants.STRING_NILL);
			String mobile = shopifyDRBody.getReceipt().getCustomer() != null
					? shopifyDRBody.getReceipt().getCustomer().getPhone()
					: (sendDRRequest.getOptcultureDetails() != null ? sendDRRequest.getOptcultureDetails().getPhone()
							: Constants.STRING_NILL);
			logger.info("email:" + emailId);
			String storeNo = shopifyDRBody.getReceipt().getNumber();
			name = name.trim();
			orgId = orgId.trim();
			token = token.trim();

			Users user = getUser(name, orgId, token);

			if (user == null) {
				status = new SendDRStatus("100011", PropertyUtil.getErrorMessage(100011, OCConstants.ERROR_DR_FLAG),
						OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				DigitalReceiptResponse posDRResponse = prepareResponseObject(status);
				return posDRResponse;
			}
			if (!user.isEnabled()) {
				status = new SendDRStatus("300011", PropertyUtil.getErrorMessage(300011, OCConstants.ERROR_DR_FLAG),
						OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				DigitalReceiptResponse posDRResponse = prepareResponseObject(status);
				return posDRResponse;
			}
			if (user.getPackageExpiryDate().before(Calendar.getInstance())) {
				status = new SendDRStatus("100012", PropertyUtil.getErrorMessage(100012, OCConstants.ERROR_DR_FLAG),
						OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				DigitalReceiptResponse posDRResponse = prepareResponseObject(status);
				return posDRResponse;
			}
			String requestType = sendDRRequest.getHead().getRequestType();
			String docSid = sendDRRequest.getHead().getReceiptType().equalsIgnoreCase("sale")
					? sendDRRequest.getBody().getReceipt().getId()
					: (sendDRRequest.getBody().getReceipt().getOrder_number() != null
							? sendDRRequest.getBody().getReceipt().getOrder_number()
							: sendDRRequest.getBody().getReceipt().getId());
			if (requestType != null && !requestType.isEmpty() && requestType.equalsIgnoreCase("new") && docSid != null
					&& !docSid.isEmpty()) {
				// ignore the request
				boolean isexists = validateDuplicity(docSid, user.getUserId());
				if (isexists) {
					status = new SendDRStatus("100013", PropertyUtil.getErrorMessage(100013, OCConstants.ERROR_DR_FLAG),
							OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					DigitalReceiptResponse posDRResponse = prepareResponseObject(status);
					return posDRResponse;
				}

			}
			Gson gson = new Gson();
			String DRJson = gson.toJson(sendDRRequest);

			logger.info(DRJson);
			DigitalReceiptsJSON digitalReceiptsJSON = saveDRJson(user, DRJson, user.getUserId(), docSid, mode,
					OCConstants.DR_SOURCE_TYPE_Shopify);
			String displayTemplate = Constants.STRING_NILL;
			String Card = Constants.STRING_NILL;
			String lifeTimePoints = Constants.STRING_NILL;
			
			String loyaltyEarnedToday = Constants.STRING_NILL;
			String pointsRedeemedToday = Constants.STRING_NILL;
			
			String pointsBalance = Constants.STRING_NILL;
			String currencyBalance = Constants.STRING_NILL;
			
			if (sendDRRequest.getAction().equals(OCConstants.DIGITAL_RECEIPT_SERVICE_ACTION_SENDEMAIL)) {
				String isLoyaltyCustomer = sendDRRequest.getHead().getIsLoyaltyCustomer();
				DRToLtyExtractionImpl drToLtyExtractionImpl = (DRToLtyExtractionImpl) ServiceLocator.getInstance()
						.getServiceByName(OCConstants.DR_TO_LTY_EXTRACTION_IMPL);
				if (user.getUserOrganization().getLoyaltyDisplayTemplate() != null
						&& user.getUserOrganization().isSendRealtimeLoyaltyStatus())
				// isLoyaltyCustomer != null && isLoyaltyCustomer.trim().equals("Y"))
				{

					String cardNumber = drToLtyExtractionImpl.processRequest(digitalReceiptsJSON.getDrjsonId() + "");
					ShopifyRequestTranslator shopifyRequestTranslator = new ShopifyRequestTranslator();
					DigitalReceipt dRJsonRequest = shopifyRequestTranslator.convertShopifyRequest(sendDRRequest, user);
					String[] displayTemplateArr = constructDisplayTemplate(user, dRJsonRequest, cardNumber);
					displayTemplate = displayTemplateArr[0];
					Card = displayTemplateArr[1];
					lifeTimePoints = displayTemplateArr[2];
					loyaltyEarnedToday = displayTemplateArr[3];
					pointsRedeemedToday = displayTemplateArr[8];
					pointsBalance = displayTemplateArr[4];
					currencyBalance = displayTemplateArr[5];
					
				} else {
					if (isLoyaltyCustomer != null && !isLoyaltyCustomer.isEmpty()
							&& isLoyaltyCustomer.equalsIgnoreCase("Y")) {
						gson = new Gson();
						ShopifyRequestTranslator shopifyRequestTranslator = new ShopifyRequestTranslator();
						ShopifyBasedDRRequest shopifyRequest = gson.fromJson(digitalReceiptsJSON.getJsonStr(),
								ShopifyBasedDRRequest.class);
						DigitalReceipt dRJsonRequest = shopifyRequestTranslator.convertShopifyRequest(shopifyRequest,
								user);
						ContactsLoyaltyDao contactsLoyaltyDao = (ContactsLoyaltyDao) ServiceLocator.getInstance()
								.getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
						PropertyDescriptor cdi = null;
						Receipt receipt = dRJsonRequest.getBody().getReceipt();
						String cardInfo = user.getCardInfo();
						String cardInfoObjStr = Constants.STRING_NILL;
						if (cardInfo != null) {
							cdi = new PropertyDescriptor(cardInfo, receipt.getClass());
							Object cardInfoObj = cdi.getReadMethod().invoke(receipt);
							if (cardInfoObj != null && !cardInfoObj.toString().isEmpty()) {
								cardInfoObjStr = cardInfoObj.toString();
							}
						}
						logger.info("cardInfoObjStr " + cardInfoObjStr);
						ContactsLoyalty contactsLoyalty = null;
						boolean mobileBasedEnroll = false;
						if (cardInfoObjStr == null || cardInfoObjStr.isEmpty()) {
							LoyaltyProgram defaultLoyaltyProgram = findDefaultProgram(user.getUserId());
							if (defaultLoyaltyProgram != null) {
								if (defaultLoyaltyProgram.getMembershipType()
										.equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE)) {
									if ((receipt.getBillToPhone1() != null
											&& !receipt.getBillToPhone1().trim().isEmpty())) {
										logger.info("no card number find with mobile based prog");
										String phone =receipt.getBillToPhone1();
										String phoneParse =   phone != null && !phone.isEmpty() ? Utility.phoneParse(phone.trim(),
												user != null ? user.getUserOrganization() : null) : null;
										contactsLoyalty = contactsLoyaltyDao.findContLoyaltyBymobile(user,
															phoneParse);
										if (contactsLoyalty == null)
											;
										mobileBasedEnroll = true;
									} else {
										mobileBasedEnroll = true;
									}
								}
							}
						} else {
							logger.info("find with card number ");
							// look up with card number
							contactsLoyalty = contactsLoyaltyDao.findContLoyaltyByCardId(user.getUserId(),
									cardInfoObjStr);
						}
						if (contactsLoyalty == null) {
							/*
							 * String custID = receipt.getBillToCustSID(); if(custID!=null &&
							 * !custID.isEmpty()){ contactsLoyalty = findLoyaltyCardInOCByCustId(custID,
							 * user.getUserId()); }
							 */
							// enroll and send the cardnumber back
							DRToLtyExtractionData DRToLtyExtractionData = convertDRFromOtherSources(dRJsonRequest, user,
									mobileBasedEnroll);
							// enrollment
							LoyaltyEnrollResponse enrolResponseObject = null;
							enrolResponseObject = drToLtyExtractionImpl.prepareEnrollFromDRRequest(user,
									mobileBasedEnroll, DRToLtyExtractionData.getReceipt(),
									DRToLtyExtractionData.getUser(), DRToLtyExtractionData.getCustomer(),
									DRToLtyExtractionData.getHeaderFileds().getRequestSource());// .info("performing
																								// enrollment");
							if (enrolResponseObject != null) {

								String responseJson = new Gson().toJson(enrolResponseObject,
										LoyaltyEnrollResponse.class);
								if (mobileBasedEnroll) {
									Card = enrolResponseObject.getMembership().getPhoneNumber();

								} else {
									Card = enrolResponseObject.getMembership().getCardNumber();
								}
								logger.info("Enrollment response : " + responseJson);
								logger.info("enrolled card : " + Card);
							}

						} else
							Card = contactsLoyalty.getCardNumber();
					}
				}

			}
			String userJSONSettings = PropertyUtil.getPropertyValueFromDB("DigitalReceiptSetting");
			DigitalReceiptResponse posDRResponse = validateUserDRSettings(userJSONSettings, user.getUserId());

			/*if (posDRResponse != null) {
				if (!Card.isEmpty())
					posDRResponse.getRESPONSEINFO().setCARDNUMBER(Card);
				//sivaram
		    	posDRResponse.getRESPONSEINFO().setPOINTSBALANCE((pointsBalance != null && !pointsBalance.isEmpty() ? pointsBalance : ""));
		    	posDRResponse.getRESPONSEINFO().setCURRENCYBALANCE((currencyBalance != null && !currencyBalance.isEmpty() ? currencyBalance : ""));
				if (!displayTemplate.isEmpty())
					posDRResponse.getRESPONSEINFO().setDISPLAYTEMPLATE(displayTemplate);
				return posDRResponse;
			}*/

			String sendEreceipt = drHead.getEmailReceipt();
			if (sendEreceipt != null && !sendEreceipt.trim().isEmpty() && sendEreceipt.equals("N")) {
				logger.info("send DR flag empty/disabled in the request");
				status = new SendDRStatus("0", PropertyUtil.getErrorMessage(300004, OCConstants.ERROR_DR_FLAG),
						OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
				posDRResponse = prepareResponseObject(status);
				if (!Card.isEmpty())
					posDRResponse.getRESPONSEINFO().setCARDNUMBER(Card);
				//sivaram
		    	posDRResponse.getRESPONSEINFO().setPOINTSBALANCE((pointsBalance != null && !pointsBalance.isEmpty() ? pointsBalance : ""));
		    	posDRResponse.getRESPONSEINFO().setCURRENCYBALANCE((currencyBalance != null && !currencyBalance.isEmpty() ? currencyBalance : ""));
				if (!displayTemplate.isEmpty())
					posDRResponse.getRESPONSEINFO().setDISPLAYTEMPLATE(displayTemplate);
				return posDRResponse;
			}

			String templateContent = null;
			String userSelectedTemplate = null;
			String[] templateArr = null;
			DigitalReceiptUserSettings digitalReceiptUserSettings = null;
			Contacts contactobj = null;
			ContactsDao contactsDao = (ContactsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_DAO);
			contactobj = contactsDao.findContactByPhone(mobile, user.getUserId());

			
			if (user.isZoneWise()) {
				storeNo = shopifyDRBody.getReceipt().getNumber();
				digitalReceiptUserSettings = getZoneMappedTemplate(user, storeNo, null);

				if (digitalReceiptUserSettings != null && !digitalReceiptUserSettings.isSettingEnable()) {
					status = new SendDRStatus("3000012",
							PropertyUtil.getErrorMessage(3000012, OCConstants.ERROR_DR_FLAG),
							OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					posDRResponse = prepareResponseObject(status);
					if (!Card.isEmpty())
						posDRResponse.getRESPONSEINFO().setCARDNUMBER(Card);
					//sivaram
			    	posDRResponse.getRESPONSEINFO().setPOINTSBALANCE((pointsBalance != null && !pointsBalance.isEmpty() ? pointsBalance : ""));
			    	posDRResponse.getRESPONSEINFO().setCURRENCYBALANCE((currencyBalance != null && !currencyBalance.isEmpty() ? currencyBalance : ""));
					if (!displayTemplate.isEmpty())
						posDRResponse.getRESPONSEINFO().setDISPLAYTEMPLATE(displayTemplate);
					return posDRResponse;
				}

			} else {
				DigitalReceiptUserSettingsDao digitalReceiptUserSettingsDao = (DigitalReceiptUserSettingsDao) ServiceLocator
						.getInstance().getDAOByName(OCConstants.DIGITAL_RECEIPT_USER_SETTINGS_DAO);
				digitalReceiptUserSettings = digitalReceiptUserSettingsDao.findByUserId(user.getUserId());
			}

			if (digitalReceiptUserSettings == null) {
				status = new SendDRStatus("3000011", PropertyUtil.getErrorMessage(3000011, OCConstants.ERROR_DR_FLAG),
						OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				posDRResponse = prepareResponseObject(status);
				if (!Card.isEmpty())
					posDRResponse.getRESPONSEINFO().setCARDNUMBER(Card);
				//sivaram
		    	posDRResponse.getRESPONSEINFO().setPOINTSBALANCE((pointsBalance != null && !pointsBalance.isEmpty() ? pointsBalance : ""));
		    	posDRResponse.getRESPONSEINFO().setCURRENCYBALANCE((currencyBalance != null && !currencyBalance.isEmpty() ? currencyBalance : ""));
				if (!displayTemplate.isEmpty())
					posDRResponse.getRESPONSEINFO().setDISPLAYTEMPLATE(displayTemplate);
				return posDRResponse;
			}

			if (!digitalReceiptUserSettings.isEnabled()) {// stop sending if the setting are paused for that zone
				if (digitalReceiptUserSettings.isSmsEnabled() && (mobile == null || mobile.trim().length() == 0)) {
					status = new SendDRStatus("3000012",
							PropertyUtil.getErrorMessage(3000012, OCConstants.ERROR_DR_FLAG),
							OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					posDRResponse = prepareResponseObject(status);
				}
				if (!Card.isEmpty())
					posDRResponse.getRESPONSEINFO().setCARDNUMBER(Card);
				//sivaram
		    	posDRResponse.getRESPONSEINFO().setPOINTSBALANCE((pointsBalance != null && !pointsBalance.isEmpty() ? pointsBalance : ""));
		    	posDRResponse.getRESPONSEINFO().setCURRENCYBALANCE((currencyBalance != null && !currencyBalance.isEmpty() ? currencyBalance : ""));
				if (!displayTemplate.isEmpty())
					posDRResponse.getRESPONSEINFO().setDISPLAYTEMPLATE(displayTemplate);
				return posDRResponse;
			}
			
			
			
			if (emailId == null || emailId.trim().length() == 0) {
				status = new SendDRStatus("300009", PropertyUtil.getErrorMessage(300009, OCConstants.ERROR_DR_FLAG),
						OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				posDRResponse = prepareResponseObject(status);
				if (!Card.isEmpty())
					posDRResponse.getRESPONSEINFO().setCARDNUMBER(Card);
				//sivaram
		    	posDRResponse.getRESPONSEINFO().setPOINTSBALANCE((pointsBalance != null && !pointsBalance.isEmpty() ? pointsBalance : ""));
		    	posDRResponse.getRESPONSEINFO().setCURRENCYBALANCE((currencyBalance != null && !currencyBalance.isEmpty() ? currencyBalance : ""));
				if (!displayTemplate.isEmpty())
					posDRResponse.getRESPONSEINFO().setDISPLAYTEMPLATE(displayTemplate);
				return posDRResponse;
			} else if (!Utility.validateEmail(emailId)) {
				status = new SendDRStatus("300010", PropertyUtil.getErrorMessage(300010, OCConstants.ERROR_DR_FLAG),
						OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				posDRResponse = prepareResponseObject(status);
				if (!Card.isEmpty())
					posDRResponse.getRESPONSEINFO().setCARDNUMBER(Card);
				//sivaram
		    	posDRResponse.getRESPONSEINFO().setPOINTSBALANCE((pointsBalance != null && !pointsBalance.isEmpty() ? pointsBalance : ""));
		    	posDRResponse.getRESPONSEINFO().setCURRENCYBALANCE((currencyBalance != null && !currencyBalance.isEmpty() ? currencyBalance : ""));
				if (!displayTemplate.isEmpty())
					posDRResponse.getRESPONSEINFO().setDISPLAYTEMPLATE(displayTemplate);
				return posDRResponse;
			}
			userSelectedTemplate = digitalReceiptUserSettings.getSelectedTemplateName();

			if (digitalReceiptUserSettings.getMyTemplateId() != null) {

				templateArr = getTemplateContent(digitalReceiptUserSettings.getMyTemplateId(), user.getUserId());
				templateContent = templateArr[0];

				if (templateContent == null) {
					status = new SendDRStatus("300006", PropertyUtil.getErrorMessage(300006, OCConstants.ERROR_DR_FLAG),
							OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					posDRResponse = prepareResponseObject(status);
					if (!Card.isEmpty())
						posDRResponse.getRESPONSEINFO().setCARDNUMBER(Card);
					//sivaram
			    	posDRResponse.getRESPONSEINFO().setPOINTSBALANCE((pointsBalance != null && !pointsBalance.isEmpty() ? pointsBalance : ""));
			    	posDRResponse.getRESPONSEINFO().setCURRENCYBALANCE((currencyBalance != null && !currencyBalance.isEmpty() ? currencyBalance : ""));
					if (!displayTemplate.isEmpty())
						posDRResponse.getRESPONSEINFO().setDISPLAYTEMPLATE(displayTemplate);
					return posDRResponse;
				}

			} else {

				templateArr = getSelectedSystemTemplateContent(userSelectedTemplate, user.getUserId());
				templateContent = templateArr[0];

				if (templateContent == null) {
					status = new SendDRStatus("300007", PropertyUtil.getErrorMessage(300007, OCConstants.ERROR_DR_FLAG),
							OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					posDRResponse = prepareResponseObject(status);
					if (!Card.isEmpty())
						posDRResponse.getRESPONSEINFO().setCARDNUMBER(Card);
					//sivaram
			    	posDRResponse.getRESPONSEINFO().setPOINTSBALANCE((pointsBalance != null && !pointsBalance.isEmpty() ? pointsBalance : ""));
			    	posDRResponse.getRESPONSEINFO().setCURRENCYBALANCE((currencyBalance != null && !currencyBalance.isEmpty() ? currencyBalance : ""));
					if (!displayTemplate.isEmpty())
						posDRResponse.getRESPONSEINFO().setDISPLAYTEMPLATE(displayTemplate);
					return posDRResponse;
				}

			}

			String subject = "Your Purchase Receipt";

			JSONObject jsonMainObj = null;
			DRBody drBody = null;
			try {
				ShopifyRequestTranslator shopifyRequestTranslator = new ShopifyRequestTranslator();
				logger.debug("shopifyDRBody ===" + shopifyDRBody.getReceipt().getCreated_at());
				drBody = shopifyRequestTranslator.convertShopifyRequest(drHead, shopifyDRBody,
						sendDRRequest.getOptcultureDetails(), user);
				logger.info("DrBody.." + drBody);
				Gson gson1 = new Gson();
				String drRequestJson = gson1.toJson(drBody);
				jsonMainObj = (JSONObject) JSONValue.parse(drRequestJson);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				logger.error("Exception ", e1);
			}
			DigitalReceipt digitalReceipt = new DigitalReceipt();
			digitalReceipt.setHead(drHead);
			digitalReceipt.setBody(drBody);
			digitalReceipt.setOptcultureDetails(sendDRRequest.getOptcultureDetails());
			ProcessEReceipt processEReceipt = new ProcessEReceipt(subject, templateContent, emailId, templateArr[1],
					user, drBody, digitalReceiptsJSON.getDrjsonId(), userSelectedTemplate, digitalReceiptUserSettings,
					digitalReceipt, mobile);
			processEReceipt.setDisplayTemp(displayTemplate);
			processEReceipt.setLifeTimePoints(lifeTimePoints);
			processEReceipt.setLoyaltyEarnedToday(loyaltyEarnedToday);
			processEReceipt.setRedeemedPoints(pointsRedeemedToday);
			processEReceipt.setPointsBalance(pointsBalance); // OPS-426
			if (sendDRRequest.getAction().equals(OCConstants.DIGITAL_RECEIPT_SERVICE_ACTION_SENDEMAIL)) {
				
				processEReceipt.start();
			} else if (sendDRRequest.getAction().equals(OCConstants.DIGITAL_RECEIPT_XML_ACTION_SENDEMAIL)) {
				
				processEReceipt.processDR();
			}
			status = new SendDRStatus("0", "Digital receipt submitted successfully . Mail would be sent in a moment.",
					OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
			posDRResponse = prepareResponseObject(status);
			if (!Card.isEmpty())
				posDRResponse.getRESPONSEINFO().setCARDNUMBER(Card);
			//sivaram
	    	posDRResponse.getRESPONSEINFO().setPOINTSBALANCE((pointsBalance != null && !pointsBalance.isEmpty() ? pointsBalance : ""));
	    	posDRResponse.getRESPONSEINFO().setCURRENCYBALANCE((currencyBalance != null && !currencyBalance.isEmpty() ? currencyBalance : ""));
			if (!displayTemplate.isEmpty())
				posDRResponse.getRESPONSEINFO().setDISPLAYTEMPLATE(displayTemplate);
			logger.debug("*************** EXITING processShopifytoDRRequest****************");
			return posDRResponse;

		} catch (Exception e) {
			logger.error("Exception ", e);
		}
		return null;
	}
	
	//APP-4773
	@Override
	public DigitalReceiptResponse processOrionDRRequest(OrionDRRequest sendDRRequest, String mode)
			throws BaseServiceException {
		
		logger.info("Entered into processOrionDRRequest service method....");
		SendDRStatus status = null;
		try {
			OrionDRBody orionDRBody = sendDRRequest.getBody();
			DRHead dRHead = sendDRRequest.getHead();
			User orionDRUser = dRHead.getUser();

			String name = orionDRUser.getUserName();
			String orgId = orionDRUser.getOrganizationId();
			String token = orionDRUser.getToken();

			String emailId = (orionDRBody.getReceipt()!=null && orionDRBody.getReceipt().getBILL_EMAIL()!=null
					&& !orionDRBody.getReceipt().getBILL_EMAIL().isEmpty()) ? orionDRBody.getReceipt().getBILL_EMAIL() : Constants.STRING_NILL;

			String mobile = (orionDRBody.getReceipt()!=null && orionDRBody.getReceipt().getBILL_MOBILE()!=null
					&& !orionDRBody.getReceipt().getBILL_MOBILE().isEmpty()) ? orionDRBody.getReceipt().getBILL_MOBILE() : Constants.STRING_NILL;
			
			logger.info("email:" + emailId);
			name = name.trim();
			orgId = orgId.trim();
			token = token.trim();

			Users user = getUser(name, orgId, token);

			if (user == null) {
				status = new SendDRStatus("100011", PropertyUtil.getErrorMessage(100011, OCConstants.ERROR_DR_FLAG),
						OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				DigitalReceiptResponse posDRResponse = prepareResponseObject(status);
				return posDRResponse;
			}
			if (!user.isEnabled()) {
				status = new SendDRStatus("300011", PropertyUtil.getErrorMessage(300011, OCConstants.ERROR_DR_FLAG),
						OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				DigitalReceiptResponse posDRResponse = prepareResponseObject(status);
				return posDRResponse;
			}
			if (user.getPackageExpiryDate().before(Calendar.getInstance())) {
				status = new SendDRStatus("100012", PropertyUtil.getErrorMessage(100012, OCConstants.ERROR_DR_FLAG),
						OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				DigitalReceiptResponse posDRResponse = prepareResponseObject(status);
				return posDRResponse;
			}
			String requestType = sendDRRequest.getHead().getRequestType();

			String docSid = orionDRBody.getReceipt().getDocSID();

			if (requestType != null && !requestType.isEmpty() && requestType.equalsIgnoreCase("new") && docSid != null
					&& !docSid.isEmpty()) {
				// ignore the request
				boolean isexists = validateDuplicity(docSid, user.getUserId());
				if (isexists) {
					status = new SendDRStatus("100013", PropertyUtil.getErrorMessage(100013, OCConstants.ERROR_DR_FLAG),
							OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					DigitalReceiptResponse posDRResponse = prepareResponseObject(status);
					return posDRResponse;
				}

			}
			Gson gson = new Gson();
			String DRJson = gson.toJson(sendDRRequest);
			
			DigitalReceiptsJSON digitalReceiptsJSON = saveDRJson(user, DRJson, user.getUserId(), docSid, mode,
					OCConstants.DR_SOURCE_TYPE_ORION);
			String displayTemplate = Constants.STRING_NILL;
			String Card = Constants.STRING_NILL;
			String lifeTimePoints = Constants.STRING_NILL;

			String loyaltyEarnedToday = Constants.STRING_NILL;	
			String pointsBalance = Constants.STRING_NILL;
			String currencyBalance = Constants.STRING_NILL;
			if (sendDRRequest.getAction().equals(OCConstants.DIGITAL_RECEIPT_SERVICE_ACTION_SENDEMAIL)) {
				String isLoyaltyCustomer = sendDRRequest.getHead().getIsLoyaltyCustomer();
				DRToLtyExtractionImpl drToLtyExtractionImpl = (DRToLtyExtractionImpl) ServiceLocator.getInstance()
						.getServiceByName(OCConstants.DR_TO_LTY_EXTRACTION_IMPL);
				if (user.getUserOrganization().getLoyaltyDisplayTemplate() != null
						&& user.getUserOrganization().isSendRealtimeLoyaltyStatus()) {

					String cardNumber = drToLtyExtractionImpl.processRequest(digitalReceiptsJSON.getDrjsonId() + "");
					OrionRequestTranslator orionRequestTranslator = new OrionRequestTranslator();
					DigitalReceipt dRJsonRequest = orionRequestTranslator.convertOrionRequest(sendDRRequest,
							user);
					
					String convertedOrionDRRequest = gson.toJson(dRJsonRequest);
					logger.debug("converted Orion Request  \n "+convertedOrionDRRequest);//TODO keep only for testing
					
					String[] displayTemplateArr = constructDisplayTemplate(user, dRJsonRequest, cardNumber);
					displayTemplate = displayTemplateArr[0];
					Card = displayTemplateArr[1];
					lifeTimePoints = displayTemplateArr[2];
					loyaltyEarnedToday = displayTemplateArr[3];
					pointsBalance = displayTemplateArr[4];
					currencyBalance = displayTemplateArr[5];
				} else {
					if (isLoyaltyCustomer != null && !isLoyaltyCustomer.isEmpty()
							&& isLoyaltyCustomer.equalsIgnoreCase("Y")) {
						gson = new Gson();
						OrionRequestTranslator orionRequestTranslator = new OrionRequestTranslator();
						OrionDRRequest orionRequest = gson.fromJson(digitalReceiptsJSON.getJsonStr(),
								OrionDRRequest.class);
						DigitalReceipt dRJsonRequest = orionRequestTranslator
								.convertOrionRequest(orionRequest, user);
						
						String convertedOrionDRRequest = gson.toJson(dRJsonRequest);
						logger.debug("converted Orion Request  \n "+convertedOrionDRRequest);//TODO keep only for testing
						
						ContactsLoyaltyDao contactsLoyaltyDao = (ContactsLoyaltyDao) ServiceLocator.getInstance()
								.getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
						PropertyDescriptor cdi = null;
						Receipt receipt = dRJsonRequest.getBody().getReceipt();
						String cardInfo = user.getCardInfo();
						String cardInfoObjStr = Constants.STRING_NILL;
						if (cardInfo != null) {
							cdi = new PropertyDescriptor(cardInfo, receipt.getClass());
							Object cardInfoObj = cdi.getReadMethod().invoke(receipt);
							if (cardInfoObj != null && !cardInfoObj.toString().isEmpty()) {
								cardInfoObjStr = cardInfoObj.toString();
							}
						}
						logger.info("cardInfoObjStr " + cardInfoObjStr);
						ContactsLoyalty contactsLoyalty = null;
						boolean mobileBasedEnroll = false;
						if (cardInfoObjStr == null || cardInfoObjStr.isEmpty()) {
							LoyaltyProgram defaultLoyaltyProgram = findDefaultProgram(user.getUserId());
							if (defaultLoyaltyProgram != null) {
								if (defaultLoyaltyProgram.getMembershipType()
										.equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE)) {
									if ((receipt.getBillToPhone1() != null
											&& !receipt.getBillToPhone1().trim().isEmpty())) {
										logger.info("no card number find with mobile based prog");
										contactsLoyalty = contactsLoyaltyDao.findContLoyaltyByCardId(user.getUserId(),
												receipt.getBillToPhone1());
										if (contactsLoyalty == null)
											;
										mobileBasedEnroll = true;
									} else {
										mobileBasedEnroll = true;
									}
								}
							}
						} else {
							logger.info("find with card number ");
							// look up with card number
							contactsLoyalty = contactsLoyaltyDao.findContLoyaltyByCardId(user.getUserId(),
									cardInfoObjStr);
						}
						if (contactsLoyalty == null) {

							// enroll and send the cardnumber back
							DRToLtyExtractionData DRToLtyExtractionData = convertDRFromOtherSources(dRJsonRequest, user,
									mobileBasedEnroll);
							// enrollment
							LoyaltyEnrollResponse enrolResponseObject = null;
							enrolResponseObject = drToLtyExtractionImpl.prepareEnrollFromDRRequest(user,
									mobileBasedEnroll, DRToLtyExtractionData.getReceipt(),
									DRToLtyExtractionData.getUser(), DRToLtyExtractionData.getCustomer(),
									DRToLtyExtractionData.getHeaderFileds().getRequestSource());// .info("performing
							// enrollment");
							String responseJson = new Gson().toJson(enrolResponseObject, LoyaltyEnrollResponse.class);
							if (mobileBasedEnroll) {
								Card = enrolResponseObject.getMembership().getPhoneNumber();
							} else {
								Card = enrolResponseObject.getMembership().getCardNumber();
							}
							logger.info("Enrollment response : " + responseJson);
							logger.info("enrolled card : " + Card);

						} else
							Card = contactsLoyalty.getCardNumber();
					}
				}

			}
			String userJSONSettings = PropertyUtil.getPropertyValueFromDB("DigitalReceiptSetting");
			DigitalReceiptResponse posDRResponse = validateUserDRSettings(userJSONSettings, user.getUserId());

			if (posDRResponse != null) {
				if (!Card.isEmpty())
					posDRResponse.getRESPONSEINFO().setCARDNUMBER(Card);
				
				posDRResponse.getRESPONSEINFO().setPOINTSBALANCE((pointsBalance != null && !pointsBalance.isEmpty() ? pointsBalance : ""));
				posDRResponse.getRESPONSEINFO().setCURRENCYBALANCE((currencyBalance != null && !currencyBalance.isEmpty() ? currencyBalance : ""));
				if (!displayTemplate.isEmpty())
					posDRResponse.getRESPONSEINFO().setDISPLAYTEMPLATE(displayTemplate);
				return posDRResponse;
			}

			String sendEreceipt = dRHead.getEmailReceipt();
			if (sendEreceipt != null && !sendEreceipt.trim().isEmpty() && sendEreceipt.equals("N")) {
				logger.info("send DR flag empty/disabled in the request");
				status = new SendDRStatus("0", PropertyUtil.getErrorMessage(300004, OCConstants.ERROR_DR_FLAG),
						OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
				posDRResponse = prepareResponseObject(status);
				if (!displayTemplate.isEmpty())
					posDRResponse.getRESPONSEINFO().setDISPLAYTEMPLATE(displayTemplate);
				if (!Card.isEmpty())
					posDRResponse.getRESPONSEINFO().setCARDNUMBER(Card);

				posDRResponse.getRESPONSEINFO().setPOINTSBALANCE((pointsBalance != null && !pointsBalance.isEmpty() ? pointsBalance : ""));
				posDRResponse.getRESPONSEINFO().setCURRENCYBALANCE((currencyBalance != null && !currencyBalance.isEmpty() ? currencyBalance : ""));
				return posDRResponse;
			}

			String templateContent = null;
			String userSelectedTemplate = null;
			String[] templateArr = null;
			DigitalReceiptUserSettings digitalReceiptUserSettings = null;

			if (user.isZoneWise()) {
				String storeNo = orionDRBody.getReceipt().getLOCATION_CODE();
				digitalReceiptUserSettings = getZoneMappedTemplate(user, storeNo, null);

				if (digitalReceiptUserSettings != null && !digitalReceiptUserSettings.isSettingEnable()) {
					status = new SendDRStatus("3000012",
							PropertyUtil.getErrorMessage(3000012, OCConstants.ERROR_DR_FLAG),
							OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					posDRResponse = prepareResponseObject(status);
					if (!displayTemplate.isEmpty())
						posDRResponse.getRESPONSEINFO().setDISPLAYTEMPLATE(displayTemplate);
					if (!Card.isEmpty())
						posDRResponse.getRESPONSEINFO().setCARDNUMBER(Card);

					posDRResponse.getRESPONSEINFO().setPOINTSBALANCE((pointsBalance != null && !pointsBalance.isEmpty() ? pointsBalance : ""));
					posDRResponse.getRESPONSEINFO().setCURRENCYBALANCE((currencyBalance != null && !currencyBalance.isEmpty() ? currencyBalance : ""));
					return posDRResponse;
				}

			} else {
				DigitalReceiptUserSettingsDao digitalReceiptUserSettingsDao = (DigitalReceiptUserSettingsDao) ServiceLocator
						.getInstance().getDAOByName(OCConstants.DIGITAL_RECEIPT_USER_SETTINGS_DAO);
				digitalReceiptUserSettings = digitalReceiptUserSettingsDao.findByUserId(user.getUserId());
			}

			if (digitalReceiptUserSettings == null) {
				status = new SendDRStatus("3000011", PropertyUtil.getErrorMessage(3000011, OCConstants.ERROR_DR_FLAG),
						OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				posDRResponse = prepareResponseObject(status);
				if (!displayTemplate.isEmpty())
					posDRResponse.getRESPONSEINFO().setDISPLAYTEMPLATE(displayTemplate);
				if (!Card.isEmpty())
					posDRResponse.getRESPONSEINFO().setCARDNUMBER(Card);

				posDRResponse.getRESPONSEINFO().setPOINTSBALANCE((pointsBalance != null && !pointsBalance.isEmpty() ? pointsBalance : ""));
				posDRResponse.getRESPONSEINFO().setCURRENCYBALANCE((currencyBalance != null && !currencyBalance.isEmpty() ? currencyBalance : ""));
				return posDRResponse;
			}

			if (!digitalReceiptUserSettings.isEnabled()) {// stop sending if the setting are paused for that zone
				if (digitalReceiptUserSettings.isSmsEnabled() && (mobile == null || mobile.trim().length() == 0)) {
					status = new SendDRStatus("3000012",
							PropertyUtil.getErrorMessage(3000012, OCConstants.ERROR_DR_FLAG),
							OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);

					posDRResponse = prepareResponseObject(status);
				}
				if (!displayTemplate.isEmpty())
					posDRResponse.getRESPONSEINFO().setDISPLAYTEMPLATE(displayTemplate);
				if (!Card.isEmpty())
					posDRResponse.getRESPONSEINFO().setCARDNUMBER(Card);

				posDRResponse.getRESPONSEINFO().setPOINTSBALANCE((pointsBalance != null && !pointsBalance.isEmpty() ? pointsBalance : ""));
				posDRResponse.getRESPONSEINFO().setCURRENCYBALANCE((currencyBalance != null && !currencyBalance.isEmpty() ? currencyBalance : ""));
				return posDRResponse;
			}
			if (emailId == null || emailId.trim().length() == 0) {
				status = new SendDRStatus("300009", PropertyUtil.getErrorMessage(300009, OCConstants.ERROR_DR_FLAG),
						OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				posDRResponse = prepareResponseObject(status);
				if (!Card.isEmpty())
					posDRResponse.getRESPONSEINFO().setCARDNUMBER(Card);

				posDRResponse.getRESPONSEINFO().setPOINTSBALANCE((pointsBalance != null && !pointsBalance.isEmpty() ? pointsBalance : ""));
				posDRResponse.getRESPONSEINFO().setCURRENCYBALANCE((currencyBalance != null && !currencyBalance.isEmpty() ? currencyBalance : ""));
				if (!displayTemplate.isEmpty())
					posDRResponse.getRESPONSEINFO().setDISPLAYTEMPLATE(displayTemplate);
				return posDRResponse;
			} else if (!Utility.validateEmail(emailId)) {
				status = new SendDRStatus("300010", PropertyUtil.getErrorMessage(300010, OCConstants.ERROR_DR_FLAG),
						OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				posDRResponse = prepareResponseObject(status);
				if (!Card.isEmpty())
					posDRResponse.getRESPONSEINFO().setCARDNUMBER(Card);

				posDRResponse.getRESPONSEINFO().setPOINTSBALANCE((pointsBalance != null && !pointsBalance.isEmpty() ? pointsBalance : ""));
				posDRResponse.getRESPONSEINFO().setCURRENCYBALANCE((currencyBalance != null && !currencyBalance.isEmpty() ? currencyBalance : ""));
				if (!displayTemplate.isEmpty())
					posDRResponse.getRESPONSEINFO().setDISPLAYTEMPLATE(displayTemplate);
				return posDRResponse;
			}
			userSelectedTemplate = digitalReceiptUserSettings.getSelectedTemplateName();

			if (digitalReceiptUserSettings.getMyTemplateId() != null) {

				templateArr = getTemplateContent(digitalReceiptUserSettings.getMyTemplateId(), user.getUserId());
				templateContent = templateArr[0];

				if (templateContent == null) {
					status = new SendDRStatus("300006", PropertyUtil.getErrorMessage(300006, OCConstants.ERROR_DR_FLAG),
							OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					posDRResponse = prepareResponseObject(status);
					if (!Card.isEmpty())
						posDRResponse.getRESPONSEINFO().setCARDNUMBER(Card);

					posDRResponse.getRESPONSEINFO().setPOINTSBALANCE((pointsBalance != null && !pointsBalance.isEmpty() ? pointsBalance : ""));
					posDRResponse.getRESPONSEINFO().setCURRENCYBALANCE((currencyBalance != null && !currencyBalance.isEmpty() ? currencyBalance : ""));
					if (!displayTemplate.isEmpty())
						posDRResponse.getRESPONSEINFO().setDISPLAYTEMPLATE(displayTemplate);
					return posDRResponse;
				}

			} else {

				templateArr = getSelectedSystemTemplateContent(userSelectedTemplate, user.getUserId());
				templateContent = templateArr[0];

				if (templateContent == null) {
					status = new SendDRStatus("300007", PropertyUtil.getErrorMessage(300007, OCConstants.ERROR_DR_FLAG),
							OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					posDRResponse = prepareResponseObject(status);
					if (!Card.isEmpty())
						posDRResponse.getRESPONSEINFO().setCARDNUMBER(Card);

					posDRResponse.getRESPONSEINFO().setPOINTSBALANCE((pointsBalance != null && !pointsBalance.isEmpty() ? pointsBalance : ""));
					posDRResponse.getRESPONSEINFO().setCURRENCYBALANCE((currencyBalance != null && !currencyBalance.isEmpty() ? currencyBalance : ""));
					if (!displayTemplate.isEmpty())
						posDRResponse.getRESPONSEINFO().setDISPLAYTEMPLATE(displayTemplate);
					return posDRResponse;
				}

			}

			String subject = "Your Purchase Receipt";

			DigitalReceipt digitalReceipt = new DigitalReceipt();
			DRBody drBody = null;
			try {
				OrionRequestTranslator orionRequestTranslator = new OrionRequestTranslator();
				digitalReceipt = orionRequestTranslator.convertOrionRequest(sendDRRequest, user);
				drBody = digitalReceipt.getBody();
				
			} catch (Exception e1) {
				logger.error("Exception ", e1);
			}

			digitalReceipt.setBody(drBody);
			digitalReceipt.setOptcultureDetails(sendDRRequest.getOptcultureDetails());
			ProcessEReceipt processEReceipt = new ProcessEReceipt(subject, templateContent, emailId, templateArr[1],
					user, drBody, digitalReceiptsJSON.getDrjsonId(), userSelectedTemplate, digitalReceiptUserSettings,
					digitalReceipt, mobile);

			processEReceipt.setDisplayTemp(displayTemplate);
			processEReceipt.setLifeTimePoints(lifeTimePoints);
			processEReceipt.setLoyaltyEarnedToday(loyaltyEarnedToday);
			if (sendDRRequest.getAction().equals(OCConstants.DIGITAL_RECEIPT_SERVICE_ACTION_SENDEMAIL)) {
				processEReceipt.start();
			} else if (sendDRRequest.getAction().equals(OCConstants.DIGITAL_RECEIPT_XML_ACTION_SENDEMAIL)) {
				processEReceipt.processDR();
			}
			// SendDRStatus status = null;
			status = new SendDRStatus("0", "Digital receipt submitted successfully . Mail would be sent in a moment.",
					OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
			// DigitalReceiptResponse posDRResponse
			posDRResponse = prepareResponseObject(status, displayTemplate, Card);

			posDRResponse.getRESPONSEINFO().setPOINTSBALANCE((pointsBalance != null && !pointsBalance.isEmpty() ? pointsBalance : ""));
			posDRResponse.getRESPONSEINFO().setCURRENCYBALANCE((currencyBalance != null && !currencyBalance.isEmpty() ? currencyBalance : ""));



			logger.debug("*************** EXITING processOrionDRRequest***********************");
			return posDRResponse;
		} catch (Exception e) {
			logger.error("Exception ", e);
		}
		return null;

	}//processOrionDRRequest

	private LoyaltyProgram findDefaultProgram(Long userId) throws Exception {
		LoyaltyProgramDao loyaltyProgramDao = (LoyaltyProgramDao) ServiceLocator.getInstance()
				.getDAOByName(OCConstants.LOYALTY_PROGRAM_DAO);
		LoyaltyProgram loyaltyProgram = loyaltyProgramDao.findDefaultProgramByUserId(userId);

		return loyaltyProgram;
	}

	private String[] getSelectedSystemTemplateContent(String userSelectedTemplate, Long userId)
			throws BaseServiceException {
		String selectedTemplate = null;
		String templateContent = null;
		String[] strArr = new String[2];
		try {
			if (userSelectedTemplate.indexOf("SYSTEM:") != -1) {

				selectedTemplate = userSelectedTemplate.substring(7);
				String digiReciptsDir = PropertyUtil.getPropertyValue("DigitalRecieptsDirectory");
				File templateFile = new File(digiReciptsDir + "/" + selectedTemplate + "/index.html");

				FileReader fr = new FileReader(templateFile);
				BufferedReader br2 = new BufferedReader(fr);
				String line = "";
				StringBuffer sb2 = new StringBuffer();
				while ((line = br2.readLine()) != null) {
					sb2.append(line);
				}
				templateContent = sb2.toString();

				strArr[0] = templateContent;
				strArr[1] = OCConstants.LEGACY_EDITOR;
			}
		} catch (Exception e) {
			throw new BaseServiceException("Exception occured while getting template content ", e);
		}

		return strArr;
	}

	private String[] getTemplateContent(Long myTemplateId, Long userId) throws BaseServiceException {

		String templateContent = null;

		String[] strArr = new String[2];

		try {

			DigitalReceiptMyTemplatesDao digitalReceiptMyTemplatesDao = (DigitalReceiptMyTemplatesDao) ServiceLocator
					.getInstance().getDAOByName(OCConstants.DIGITAL_RECEIPT_MYTEMPLATES_DAO);
			// DigitalReceiptMyTemplate digitalReceiptMyTemplates =
			// digitalReceiptMyTemplatesDao.findByOrgIdAndTemplateId(orgId, myTemplateId);
			DigitalReceiptMyTemplate digitalReceiptMyTemplates = digitalReceiptMyTemplatesDao
					.findByUserId_TemplateId(userId, myTemplateId);

			if (digitalReceiptMyTemplates != null) {
				templateContent = digitalReceiptMyTemplates.getContent();
				strArr[0] = templateContent;
				strArr[1] = digitalReceiptMyTemplates.getEditorType();
			}

			/*
			 * if(digitalReceiptMyTemplates != null &&
			 * (digitalReceiptMyTemplates.getContent()!=null &&
			 * !digitalReceiptMyTemplates.getContent().isEmpty())){
			 * 
			 * StringBuffer contentSb = null;
			 * if(digitalReceiptMyTemplates.getEditorType().equalsIgnoreCase(Constants.
			 * EDITOR_TYPE_BEE)) { contentSb = new StringBuffer(); Document doc =
			 * Jsoup.parseBodyFragment(digitalReceiptMyTemplates.getContent()); //Elements
			 * bodyele = doc.getElementsByTag("body"); //Element body = doc.body(); Elements
			 * elements = doc.select("body").first().children(); if(elements.isEmpty()){
			 * logger.
			 * warn("BEE Html content is null (Body Element not present) in the EmailContent object"
			 * ); logger.
			 * info("This is HTML CONTENT IS EMPTY (Body Element not present)................"
			 * ); return null; }else{
			 * logger.info("this is HTML CONTENT IS NOT EMPTY................" ); for
			 * (Element el : elements) { // System.out.println(el.toString());
			 * contentSb.append(el.toString()); } } templateContent=contentSb.toString();
			 * }else templateContent = digitalReceiptMyTemplates.getContent(); }
			 */

		} catch (Exception e) {
			throw new BaseServiceException("Exception occured while getting template content ", e);
		}
		return strArr;
	}

	private DigitalReceiptUserSettings getZoneMappedTemplate(Users user, String storeNo, String SBSNo)
			throws BaseServiceException {
		try {
			UsersDao usersDao = (UsersDao) ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);

			List<UsersDomains> domainsList = usersDao.getAllDomainsByUser(user.getUserId());// finding this power user's
																							// domain
			logger.debug("==STEP I====");
			if (domainsList == null)
				return null;

			logger.info("==domainsList.get(0).getDomainId()==" + domainsList.get(0).getDomainId());
			// String domainStr =
			// domainsList.get(0).getDomainId().longValue()+Constants.STRING_NILL;

			OrganizationStoresDao orgStoresDao = (OrganizationStoresDao) ServiceLocator.getInstance()
					.getDAOByName(OCConstants.ORGANIZATION_STORES_DAO);
			OrganizationStores orgStore = orgStoresDao.findOrgStoreObject(user.getUserOrganization().getUserOrgId(),
					domainsList.get(0).getDomainId(), SBSNo, storeNo);// find the orgstorerecord to find the which zone
																		// this belongs to
			logger.debug("==STEP II====");
			if (orgStore == null)
				return null;

			ZoneDao zoneDao = (ZoneDao) ServiceLocator.getInstance().getDAOByName(OCConstants.ORGANIZATION_ZONE_DAO);
			OrganizationZone orgZone = zoneDao.findBy(orgStore.getStoreId());

			logger.debug("==STEP III====");
			if (orgZone == null)
				return null;

			DigitalReceiptUserSettings DigitalReceiptUserSettings = null;

			DigitalReceiptUserSettingsDao digitalReceiptUserSettingsDao = (DigitalReceiptUserSettingsDao) ServiceLocator
					.getInstance().getDAOByName(OCConstants.DIGITAL_RECEIPT_USER_SETTINGS_DAO);
			DigitalReceiptUserSettings = digitalReceiptUserSettingsDao.findUserSelectedTemplateBy(user.getUserId(),
					orgZone.getZoneId());// find the template settings for the zone
			// DigitalReceiptUserSettings = zoneDao.findUserSelectedTemplate(userId,
			// storeNo, SBSNo);
			return DigitalReceiptUserSettings;

		} catch (Exception e) {
			throw new BaseServiceException("Exception occured while getting user selected template ", e);
		}
	}

	private DigitalReceiptResponse validateUserDRSettings(String userJSONSettings, Long userId)
			throws BaseServiceException {

		try {
			logger.info("validating user DR settings ....");

			SendDRStatus status = null;
			// String userJSONSettings =
			// PropertyUtil.getPropertyValueFromDB("DigitalReceiptSetting");
			if (userJSONSettings == null) {
				status = new SendDRStatus("300003", PropertyUtil.getErrorMessage(300003, OCConstants.ERROR_DR_FLAG),
						OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				DigitalReceiptResponse posDRResponse = prepareResponseObject(status);
				return posDRResponse;
			}

			DigitalReceiptUserSettingsDao digitalReceiptUserSettingsDao = (DigitalReceiptUserSettingsDao) ServiceLocator
					.getInstance().getDAOByName(OCConstants.DIGITAL_RECEIPT_USER_SETTINGS_DAO);
			boolean enabled = digitalReceiptUserSettingsDao.findIsUserEnabled(userId);
			boolean smsEnabled = digitalReceiptUserSettingsDao.findIsSMSEnabled(userId);
			UsersDao usersDao = (UsersDao) ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
			Users user = usersDao.findByUserId(userId);
			if (!enabled) {
				if (!smsEnabled && !user.isReceiptOnWA()) {//OPS-391
					logger.info("DR email:"+enabled+" , DR sms:"+smsEnabled+" , user receiptOnWA :"+user.isReceiptOnWA());
					status = new SendDRStatus("300004", PropertyUtil.getErrorMessage(300004, OCConstants.ERROR_DR_FLAG),
							OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					DigitalReceiptResponse posDRResponse = prepareResponseObject(status);
					return posDRResponse;
				}
			}

		} catch (Exception e) {
			throw new BaseServiceException("Exception occured while validating user dr settings ", e);
		}
		return null;
	}

	private DigitalReceiptsJSON saveDRJson(Users user, String jsonValue, Long userId, String docSid, String mode,
			String source) throws BaseServiceException {

		DigitalReceiptsJSON digitalReceiptsJSON = null;
		try {
			DigitalReceiptsJSONDaoForDML digitalReceiptsJSONDaoForDML = (DigitalReceiptsJSONDaoForDML) ServiceLocator
					.getInstance().getDAOForDMLByName(OCConstants.DIGITAL_RECEIPTS_JSON_DAOForDML);

			digitalReceiptsJSON = new DigitalReceiptsJSON();
			digitalReceiptsJSON.setJsonStr(jsonValue);
			digitalReceiptsJSON.setStatus((user.isDigitalReceiptExtraction() || user.isEnableLoyaltyExtraction())
					? Constants.DR_JSON_PROCESS_STATUS_NEW
					: OCConstants.DR_STATUS_IGNORED);
			// iteration count used in DR to Lty extraction
			int iterationCount = 0;
			digitalReceiptsJSON.setRetryForLtyExtraction(iterationCount);
			digitalReceiptsJSON.setUserId(userId);
			digitalReceiptsJSON.setCreatedDate(Calendar.getInstance());
			digitalReceiptsJSON.setDocSid(docSid);
			digitalReceiptsJSON.setMode(mode);
			digitalReceiptsJSON.setSource(source);
			// digitalReceiptsJSONDao.saveOrUpdate(digitalReceiptsJSON);
			digitalReceiptsJSONDaoForDML.saveOrUpdate(digitalReceiptsJSON);
		} catch (Exception e) {
			throw new BaseServiceException("Exception occured while saving DR json string ", e);
		}
		return digitalReceiptsJSON;
	}

	private Users getUser(String userName, String orgId, String userToken) throws Exception {

		String completeUserName = userName + Constants.USER_AND_ORG_SEPARATOR + orgId;
		UsersDao usersDao = (UsersDao) ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
		Users user = usersDao.findUserByToken(completeUserName, userToken);
		return user;
	}

	private DigitalReceiptResponse validateUser(DigitalReceipt digitalReceipt) throws Exception {
		SendDRStatus status = null;

		return null;

	}

	private DigitalReceiptResponse prepareResponseObject(SendDRStatus status, String DispTemplate, String Card) {
		DigitalReceiptResponse sendDRResponse = new DigitalReceiptResponse();
		SendDRResponseInfo responseInfo = new SendDRResponseInfo(status);
		responseInfo.setDISPLAYTEMPLATE(DispTemplate);
		responseInfo.setCARDNUMBER(Card);
		sendDRResponse.setRESPONSEINFO(responseInfo);
		return sendDRResponse;
	}

	private DigitalReceiptResponse prepareResponseObject(SendDRStatus status) {
		DigitalReceiptResponse sendDRResponse = new DigitalReceiptResponse();
		SendDRResponseInfo responseInfo = new SendDRResponseInfo(status);
		sendDRResponse.setRESPONSEINFO(responseInfo);
		return sendDRResponse;
	}

	private boolean validateDuplicity(String docSID, Long userId) {

		try {
			DigitalReceiptsJSONDao digitalReceiptsJSONDao = (DigitalReceiptsJSONDao) ServiceLocator.getInstance()
					.getDAOByName(OCConstants.DIGITAL_RECEIPTS_JSON_DAO);

			DigitalReceiptsJSON DigitalReceiptsJSON = digitalReceiptsJSONDao.findBy(docSID, userId);
			return DigitalReceiptsJSON != null;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.debug("Exception ::", e);
			return false;
		}
	}

	private DigitalReceiptResponse validateJsonFields(DigitalReceipt digitalReceipt) throws BaseServiceException {

		SendDRStatus status = null;
		if (digitalReceipt.getHead() == null || digitalReceipt.getBody() == null
				|| digitalReceipt.getBody().getItems() == null || digitalReceipt.getHead().getUser() == null
				|| digitalReceipt.getBody().getItems().size() == 0) {

			status = new SendDRStatus("300003", PropertyUtil.getErrorMessage(300003, OCConstants.ERROR_DR_FLAG),
					OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			DigitalReceiptResponse posDRResponse = prepareResponseObject(status);
			return posDRResponse;

		}

		if (digitalReceipt.getHead().getUser().getUserName() == null
				|| digitalReceipt.getHead().getUser().getUserName().isEmpty()
				|| digitalReceipt.getHead().getUser().getOrganizationId() == null
				|| digitalReceipt.getHead().getUser().getOrganizationId().isEmpty()
				|| digitalReceipt.getHead().getUser().getToken() == null
				|| digitalReceipt.getHead().getUser().getToken().isEmpty()) {

			status = new SendDRStatus("300001", PropertyUtil.getErrorMessage(300001, OCConstants.ERROR_DR_FLAG),
					OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			DigitalReceiptResponse posDRResponse = prepareResponseObject(status);
			return posDRResponse;
		}

		return null;
	}

}
