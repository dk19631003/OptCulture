package org.mq.marketer.campaign.controller.contacts;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.EventListener;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import org.zkoss.zul.Textbox;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.ReferralcodesIssued;
import org.mq.marketer.campaign.dao.ReferralcodesIssuedDao;
import org.mq.marketer.campaign.beans.Contacts;
import org.mq.marketer.campaign.beans.ContactsLoyalty;
import org.mq.marketer.campaign.beans.LoyaltyProgram;
import org.mq.marketer.campaign.beans.LoyaltyProgramTier;
import org.mq.marketer.campaign.beans.MailingList;
import org.mq.marketer.campaign.beans.POSMapping;
import org.mq.marketer.campaign.beans.RetailProSalesCSV;
import org.mq.marketer.campaign.beans.SparkBaseLocationDetails;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.CampaignReportDao;
import org.mq.marketer.campaign.dao.CampaignsDao;
import org.mq.marketer.campaign.dao.ContactsDao;
import org.mq.marketer.campaign.dao.ContactsLoyaltyDao;
import org.mq.marketer.campaign.dao.ContactsLoyaltyDaoForDML;
import org.mq.marketer.campaign.dao.MailingListDao;
import org.mq.marketer.campaign.dao.OrganizationStoresDao;
import org.mq.marketer.campaign.dao.POSMappingDao;
import org.mq.marketer.campaign.dao.RetailProSalesDao;
import org.mq.marketer.campaign.dao.SkuFileDao;
import org.mq.marketer.campaign.dao.SparkBaseLocationDetailsDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PageUtil;
import org.mq.marketer.campaign.general.Redirect;
import org.mq.marketer.campaign.general.Utility;
import org.mq.marketer.sparkbase.SparkBaseService;
import org.mq.marketer.sparkbase.SparkBaseServiceAsync;
import org.mq.marketer.sparkbase.transactionWsdl.ErrorMessageComponent;
import org.mq.marketer.sparkbase.transactionWsdl.InquiryResponse;
import org.mq.optculture.business.helper.LoyaltyProgramHelper;
import org.mq.optculture.business.loyalty.LoyaltyProgramService;
import org.mq.optculture.data.dao.LoyaltyProgramTierDao;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.springframework.util.StringUtils;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.HtmlNativeComponent;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.A;
import org.zkoss.zul.Div;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Space;
import org.zkoss.zul.Window;

public class ContactDetailsController extends GenericForwardComposer implements EventListener {
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	private Session sessionScope = null;
	private Label contactNameLblId,emailLblId,mobileLblId,cardLblId,firstNameLblId,lastNameLblId,addrLblId,AvyLblId,
				  genderLblId,dobLblId,subscribedLblId,totPurchaseAmtLblId,maxAmtPurchaseLblId,
				  numOfPurchaseLblId,firstPurchaseDateLblId,lastPurchaseDateLblId,homeStoreLblId,homeStoreNameLblId,
				  lastPurchaseStorelId,lastPurchaseStoreNamelId,createDateLblId,ModifiedDateLblId,verifiedLblId,optinLblId,cellVerifiedLblId,  cellCarrLblId,mobileoptinLblId, mobileoptinsrcLblId, mobileoptinDateLblId,
				  
				  numOfCampSentLblId,numOfCampOpenLblId,numOfClicksLblId;
	private Label balanceCurrencyLblId,balancePointsLblId,lifetimePointsLblId,totalCurrencyEarnedLblId,pointsRedemedLblId,currencyRedemedLblId,totalLoyaltyIssuanceLblId,totalRedemptionsLblId,
				  totalGiftTopupAmountLblId,giftAmountRedemedLblId,giftAmtBalLblId,lastRefreshedlId,lastRefreshedOnlId,holdBalValLblId,referralcodeLblId;
	private Label enrolledInLtyPrgmLblId,membershipStatusLblId,optinMediumLblId,optinDateLblId,storeNumberLblId,tierNameLblId,expirationDateLblId;
	private ReferralcodesIssuedDao refcodesissuedDao;

	private A refreshLoyaltyAId;
	private A ltyOverViewMembershipNumberAnchId,ltyInfomembershipNumberAnchId;
	private  String  userCurrencySymbol = "$ ";  

	private TimeZone clientTimeZone;//APP - 337
	private Contacts contact ;
	private ContactsDao contactsDao ;
	private POSMappingDao posMappingDao ;
	private MailingListDao mailingListDao ;
	private CampaignsDao campaignsDao;
	private CampaignReportDao campaignReportDao;
	private SkuFileDao skuFileDao;
	private RetailProSalesDao retailProSalesDao ;
	private OrganizationStoresDao organizationStoresDao;
	private ContactsLoyaltyDao contactsLoyaltyDao;
	private ContactsLoyaltyDaoForDML contactsLoyaltyDaoForDML;
	private MailingList contPOSMl = null;
	private Div holdBalDivId,udfFieldsId,udfFieldsValue;
	
	private Set<Long> listIdsSet; 
	private Set<Long> segmentIdsSet; 
	private Textbox testId;
	
	public ContactDetailsController() {
		this.sessionScope = Sessions.getCurrent();
		String style = "font-weight:bold;font-size:15px;color:#313031;font-family:Arial,Helvetica,sans-serif;align:left";
		PageUtil.setHeader("Contact List","",style,true);
	}
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		logger.debug(">>>>>>>>>>>>> entered in doAfterCompose");
		super.doAfterCompose(comp);
		clearLtyOverviewFeilds();
		clearLtyInfoFeilds();
		contact = (Contacts)sessionScope.getAttribute("VIEW_CONTACT_DETAILS");
		
		//added for sharing
		listIdsSet = (Set<Long>)sessionScope.getAttribute(Constants.LISTIDS_SET);
		segmentIdsSet = (Set<Long>)sessionScope.getAttribute(Constants.SEGMENTIDS_SET);
		
		clientTimeZone =(TimeZone)Sessions.getCurrent().getAttribute("clientTimeZone");//APP - 337
		//contPOSMl = contact.getMailingListByType(Constants.CONTACT_OPTIN_MEDIUM_POS);
		this.contactsDao = (ContactsDao)SpringUtil.getBean("contactsDao");
		this.retailProSalesDao = (RetailProSalesDao)SpringUtil.getBean("retailProSalesDao");
		this.organizationStoresDao = (OrganizationStoresDao)SpringUtil.getBean("organizationStoresDao");
		this.mailingListDao = (MailingListDao)SpringUtil.getBean("mailingListDao");
		this.posMappingDao = (POSMappingDao)SpringUtil.getBean("posMappingDao");
		this.campaignsDao = (CampaignsDao)SpringUtil.getBean("campaignsDao");
		this.campaignReportDao = (CampaignReportDao)SpringUtil.getBean("campaignReportDao");
		this.skuFileDao = (SkuFileDao)SpringUtil.getBean("skuFileDao");
		this.retailProSalesDao = (RetailProSalesDao)SpringUtil.getBean("retailProSalesDao");
		this.contactsLoyaltyDao = (ContactsLoyaltyDao)SpringUtil.getBean("contactsLoyaltyDao");
		contactsLoyaltyDaoForDML = (ContactsLoyaltyDaoForDML)SpringUtil.getBean("contactsLoyaltyDaoForDML");
		
		
		
		if(contact == null) {
			logger.debug(" Gettiing Contact is  null  from the sessions");
			return;
		}
		 
		String currSymbol = Utility.countryCurrencyMap.get(GetUser.getUserObj().getCountryType());
		   if(currSymbol != null && !currSymbol.isEmpty()) userCurrencySymbol = currSymbol + " ";
		  
		
		
		
		 defaultContactDetailSettings();
		logger.debug("<<<<<<<<<<<<< completed doAfterCompose ");
	} // doAfterCompose
	
	
	/**
	 * This method set's default contact details.
	 */
	public void defaultContactDetailSettings() {
		logger.debug(">>>>>>>>>>>>> entered in defaultContactDetailSettings");
		//displaying Basic Information
		displayBasicInforamtion();
		//Displaying purchase History
		displayPurchaseHistory();
		//Displaying OptIn Information
		displayOptInInfo();
		//Displaying Loyalty Data
		updateLoyaltyData();
		//Displaying Interaction Overview
		displayInteractionOverView();

		//default LatestDetails Settings
		try {
			onClick$latesCampignAnchId();
			onClick$latestPurchaseAnchId();
		} catch (Exception e) {
			logger.error("Exception ::", e);
		}
		logger.debug("<<<<<<<<<<<<< completed defaultContactDetailSettings ");
	} //defaultContactDetailSettings
	
	
	/**
	 * This method display's basic contact details.
	 */
	private void displayBasicInforamtion() {
		logger.debug(">>>>>>>>>>>>> entered in displayBasicInforamtion");
		//Set First Name
		firstNameLblId.setValue(contact.getFirstName() != null ? contact.getFirstName() : Constants.STRING_NILL );
		//Set LastName
		lastNameLblId.setValue(contact.getLastName() != null ? contact.getLastName() : Constants.STRING_NILL);
		//Set Full Name
		contactNameLblId.setValue((contact.getFirstName() != null ? contact.getFirstName() : Constants.STRING_NILL)+(contact.getLastName() != null ? contact.getLastName() : Constants.STRING_NILL));
		//Set EmailId
		emailLblId.setValue(contact.getEmailId() != null ? contact.getEmailId() : Constants.STRING_NILL);
		//Set Mobile Phone
		mobileLblId.setValue(contact.getMobilePhone() != null ? contact.getMobilePhone() : Constants.STRING_NILL);
		//Set Address
		addrLblId.setValue(getAddress());
		//Set Anniversary
		if(contact.getAnniversary() != null){
			AvyLblId.setValue(MyCalendar.calendarToString(contact.getAnniversary(),MyCalendar.FORMAT_STDATE));
		}
		
		String udfFields=displayUdfFields();
		testId.setValue(udfFields);
		Clients.evalJavaScript("test();");
		
		//udfFieldsId.setValue(value);

		// Set Gender
		if(contact.getGender() != null) {
			if(("female".equals(contact.getGender().trim().toLowerCase()) || 
				"f".equals(contact.getGender().trim().toLowerCase()) ||"male".equals(contact.getGender().trim().toLowerCase()) ||
				"m".equals(contact.getGender().trim().toLowerCase()))) {
			genderLblId.setValue(contact.getGender().trim());
			}else {
				genderLblId.setValue(contact.getGender().trim());
			}
		}
		//Date Of Birth
		if(contact.getBirthDay() != null){
			dobLblId.setValue(MyCalendar.calendarToString(contact.getBirthDay(),MyCalendar.FORMAT_STDATE));
		}

		List<MailingList> conMlLists = mailingListDao.findByContactBit(contact.getUsers().getUserId(), contact.getMlBits());

		Iterator<MailingList> mlIter = conMlLists.iterator();
		while(mlIter.hasNext()){
			contPOSMl = mlIter.next();
			if(contPOSMl.getListType().equals(Constants.CONTACT_OPTIN_MEDIUM_POS)){
				break;
			}
		}

		//Subscriber to
		//TODO: need to handle it with phone based search and POS external id
		//List<Map<String , Object>> mlNamesList = mailingListDao.findContListsByUserId(GetUser.getUsersSet(), contact.getEmailId());
		if(contact.getEmailId() != null) {

			List<Map<String , Object>> mlNamesList = mailingListDao.findContListsByListId(listIdsSet, contact.getEmailId());
			String listNameStr = "";
			//logger.info(" listName size is >>"+mlNamesList.size());

			if(mlNamesList != null && mlNamesList.size() >0) {
				for (Map<String , Object> eachMap: mlNamesList) {
					if(listNameStr.length() == 0) {
						listNameStr = eachMap.get("list_name").toString();
					}else listNameStr = listNameStr+", "+eachMap.get("list_name").toString();
				}

			}
			subscribedLblId.setValue(listNameStr.trim());
		}
		logger.debug("<<<<<<<<<<<<< completed displayBasicInforamtion ");
	}//displayBasicInforamtion
	
	
	private String displayUdfFields(){
		POSMappingDao posMappingDao = (POSMappingDao)SpringUtil.getBean("posMappingDao");
		String scope = "'"+Constants.POS_MAPPING_TYPE_CONTACTS+"'";
		List<POSMapping> posMappingsList = posMappingDao.findByType(scope, contact.getUsers().getUserId());
		int udfIdx = 0;
		String udfCustFieldStr = "";
		StringBuffer udfBuffer=null;
		udfBuffer = new StringBuffer();
		try {
		
		for (POSMapping posMapping : posMappingsList) {
			if(posMapping.getCustomFieldName().trim().toLowerCase().startsWith("udf")) {
				String displaylabel=posMapping.getDisplayLabel();
				 udfCustFieldStr=posMapping.getCustomFieldName();
					udfIdx = Integer.parseInt(udfCustFieldStr.substring("UDF".length()));
					String posUDFValue = "";
					if(getUdfContactObj(udfIdx) != null){
						posUDFValue = getUdfContactObj(udfIdx);
						if(udfBuffer.length() > 0) udfBuffer.append(Constants.ADDR_COL_DELIMETER);
						udfBuffer.append(displaylabel+" "+Constants.DELIMITER_DOUBLE_PIPE+" "+posUDFValue); 
					}
					
				}
				
			
			}
		
		} catch (Exception e) {
			logger.error("Exception ::", e);
			return "";
		}
		return udfBuffer.toString();	
		}
	
	private String getUdfContactObj(int udfIdx) throws Exception{
		
		String udfValue= "";
			switch(udfIdx) {
			case 1:  return  udfValue = contact.getUdf1();
			case 2:  return  udfValue = contact.getUdf2();
			case 3:  return  udfValue = contact.getUdf3();
			case 4:  return  udfValue = contact.getUdf4();
			case 5:  return  udfValue = contact.getUdf5();
			case 6:  return  udfValue = contact.getUdf6();
			case 7:  return  udfValue = contact.getUdf7();
			case 8:  return  udfValue = contact.getUdf8();
			case 9:  return  udfValue = contact.getUdf9();
			case 10:  return  udfValue = contact.getUdf10();
			case 11:  return  udfValue = contact.getUdf11();
			case 12:  return  udfValue = contact.getUdf12();
			case 13:  return  udfValue = contact.getUdf13();
			case 14:  return  udfValue = contact.getUdf14();
			case 15:  return  udfValue = contact.getUdf15();
			
			}
			return udfValue ;
		
		
	}

	/**
	 * This method display's purchase history details.
	 */
	private void displayPurchaseHistory() {
		logger.debug(">>>>>>>>>>>>> entered in displayPurchaseHistory");

		if(contPOSMl != null ) {

			//List<Object[]> contactPurcahseList = contactsDao.findContactPurchaseDetails(contact.getUsers().getUserId(), contact.getExternalId().trim());
			List<Map<String, Object>> contactPurcahseList = contactsDao.findContactPurchaseDetails(contact.getUsers(), contact.getContactId());

			if(contactPurcahseList != null && contactPurcahseList.size() == 1) {
				//tot_purchase_amt,max_purchase_amt,tot_reciept_count
				for (Map<String, Object> eachMap : contactPurcahseList) {
					if(eachMap.containsKey("tot_purchase_amt")){

						totPurchaseAmtLblId.setValue( eachMap.get("tot_purchase_amt") != null ? Utility.getAmountInUSFormat(eachMap.get("tot_purchase_amt")): userCurrencySymbol +"0.00");
					}
					if(eachMap.containsKey("max_purchase_amt")){
						maxAmtPurchaseLblId.setValue(eachMap.get("max_purchase_amt")!=null ? Utility.getAmountInUSFormat(eachMap.get("max_purchase_amt")): userCurrencySymbol +"0.00");
					}
					if(eachMap.containsKey("tot_reciept_count")){
						numOfPurchaseLblId.setValue(eachMap.get("tot_reciept_count")!=null ? eachMap.get("tot_reciept_count").toString():"0");
					}
				}

			}
			//First Purchase Date 
			//RetailProSalesCSV  retailProSales = retailProSalesDao.findRecordByCustomerId(contact.getExternalId().trim() , "ASC", contact.getMailingList().getListId());
			RetailProSalesCSV  retailProSales = retailProSalesDao.findRecordByContactId(contact.getContactId() , "ASC", contact.getUsers().getUserId());
			if(retailProSales != null) {

				firstPurchaseDateLblId.setValue(retailProSales.getSalesDate() != null ? MyCalendar.calendarToString(retailProSales.getSalesDate(), MyCalendar.FORMAT_STDATE) : "");
			}

			//Last Purchase Date
			//retailProSales = retailProSalesDao.findRecordByCustomerId(contact.getExternalId().trim() , "DESC" , contact.getMailingList().getListId());
			retailProSales = retailProSalesDao.findRecordByContactId(contact.getContactId() , "DESC" , contact.getUsers().getUserId());
			if(retailProSales != null) {

				if(retailProSales.getSalesDate() != null) {

					lastPurchaseDateLblId.setValue(MyCalendar.calendarToString(retailProSales.getSalesDate(), MyCalendar.FORMAT_STDATE));
				}

				//Last Purchase store
				if(retailProSales.getStoreNumber() != null){
					lastPurchaseStorelId.setValue(retailProSales.getStoreNumber());
					String storeName="";
					storeName = organizationStoresDao.findStoreNameByStoreId(contact.getUsers().getUserOrganization().getUserOrgId(), lastPurchaseStorelId.getValue());
					if(storeName!=null) lastPurchaseStoreNamelId.setValue(storeName);
					
				}
			}

			//HomeStore  
			//APP-3873-->Added Code so that Store Number will be displayed as per the Subsidiary ID(IF present)
			if(contact.getHomeStore() != null) {
				logger.info("Home Store ID:"+contact.getHomeStore());
				logger.info("Subsidiary ID:"+contact.getSubsidiaryNumber());
				homeStoreLblId.setValue(contact.getHomeStore());
				String storeName="";
				String subsidiaryNumber=(contact.getSubsidiaryNumber()==null||contact.getSubsidiaryNumber().isEmpty() ? null:contact.getSubsidiaryNumber());
				if(subsidiaryNumber!=null)
				{
					storeName= organizationStoresDao.findStoreNamesPerSubsidiaryIDAndStoreId(contact.getUsers().getUserOrganization().getUserOrgId(), homeStoreLblId.getValue(), subsidiaryNumber);
				}
				else
				{
					storeName=organizationStoresDao.findStoreNameByStoreId(contact.getUsers().getUserOrganization().getUserOrgId(), homeStoreLblId.getValue());
				}
				
				if(storeName!=null)
				{
					homeStoreNameLblId.setValue(storeName);
				}
					
			}

		}


		logger.debug("<<<<<<<<<<<<< completed displayPurchaseHistory ");
	}//displayPurchaseHistory
	
	/**
	 * This method display optInInfo
	 */
	private void displayOptInInfo() {
		logger.debug(">>>>>>>>>>>>> entered in displayOptInInfo");
		//Created date
		createDateLblId.setValue(MyCalendar.calendarToString(contact.getCreatedDate(),MyCalendar.FORMAT_DATEONLY_GENERAL,clientTimeZone));//APP - 337
		ModifiedDateLblId.setValue(MyCalendar.calendarToString(contact.getModifiedDate(),MyCalendar.FORMAT_DATETIME_STDATE,clientTimeZone));
		//Verified
		String contactStatus = contact.getEmailStatus();
		if(contact.getEmailStatus()  != null) {

			if(contactStatus.equals(Constants.CONT_STATUS_ACTIVE)  || contactStatus.equals(Constants.CONT_STATUS_INACTIVE) 
					|| contactStatus.equals(Constants.CONT_STATUS_UNSUBSCRIBED)) {

				verifiedLblId.setValue("True");
			} else verifiedLblId.setValue("False");
		}else verifiedLblId.setValue("False");


		//Opt-in
		String optInStatusStr = ""; 
		if(contactStatus.equals(Constants.CONT_STATUS_ACTIVE)){
			optInStatusStr = "True";
		}
		else {
			optInStatusStr = "False";
		}
		optinLblId.setValue(optInStatusStr);
		//Set Mobile OptIn
		mobileoptinLblId.setValue(contact.isMobileOptin() ? "True" : "False");
		TimeZone clientTimeZone = (TimeZone)sessionScope.getAttribute("clientTimeZone");
		//Set Mobile OptIn Date
		mobileoptinDateLblId.setValue(contact.getMobileOptinDate() != null ? MyCalendar.
				calendarToString(contact.getMobileOptinDate(), MyCalendar.FORMAT_DATETIME_STDATE, clientTimeZone) :"--" );
		//Set Mobile OptIn Date
		mobileoptinsrcLblId.setValue(contact.getMobileOptinSource() != null ? contact.getMobileOptinSource() : "");
		//Cell Verified && Cell Carrier
		cellVerifiedLblId.setValue((contact.getLastSMSDate() != null || contact.getMobileOptinDate() != null) ? "Verified":"Not Verified");
		cellCarrLblId.setValue("");
		logger.debug("<<<<<<<<<<<<< completed displayOptInInfo ");

	}//displayOptInInfo

	/**
	 * This method display's InteractionOverView Details
	 */
	private void displayInteractionOverView() {
		logger.debug(">>>>>>>>>>>>> entered in displayInteractionOverView");
		if(contact.getEmailId() != null) {
			long campSentCount = campaignReportDao.findCountOfMsgSent(contact.getEmailId(),contact.getUsers().getUserId());
			if( campSentCount > 0 )
				numOfCampSentLblId.setValue(""+campSentCount);


			long campOpenCount = campaignReportDao.findCountOfMsgOpen(contact.getEmailId(),contact.getUsers().getUserId());
			if( campOpenCount > 0 )
				numOfCampOpenLblId.setValue(""+campOpenCount);



			long campClickCount = campaignReportDao.findCountOfMsgClick(contact.getEmailId(),contact.getUsers().getUserId());
			if( campClickCount > 0 )
				numOfClicksLblId.setValue(""+campClickCount);
		}
		logger.debug("<<<<<<<<<<<<< completed displayInteractionOverView ");
		
	}//displayInteractionOverView

	/**
	 * This method process the address detial's of the contact.
	 * @return address
	 */
	private String getAddress() {
		logger.debug(">>>>>>>>>>>>> entered in getAddress");
		String addr = Constants.STRING_NILL;
		//Address One
		if(contact.getAddressOne() != null && contact.getAddressOne().trim().length() >0){
			if(addr.trim().length() > 0) {
				addr += ", ";
			}
			addr += contact.getAddressOne().trim();
		}if(contact.getAddressTwo() != null && contact.getAddressTwo().trim().length() >0) {
			if(addr.trim().length() > 0){
				addr += ", ";
			}
			addr += contact.getAddressTwo().trim();
		}if(contact.getCity() != null && contact.getCity().trim().length() >0) {

			if(addr.trim().length() > 0){
				addr += ", ";
			}
			addr += contact.getCity().trim();

		}if(contact.getState() != null && contact.getState().trim().length() >0) {
			if(addr.trim().length() > 0){
				addr += ", ";
			}

			addr += contact.getState().trim();

		}if(contact.getCountry() != null && contact.getCountry().trim().length() >0) {
			if(addr.trim().length() > 0){
				addr += ", ";
			}
			addr += contact.getCountry().trim();
		}/*if(contact.getPin() > 0) {
			if(addr.trim().length() > 0){
				addr += ", ";
			}
			addr += contact.getPin();
		} //Pin
		 */		

		if(contact.getZip() != null && contact.getZip().length() > 0) {
			if(addr.trim().length() > 0){
				addr += ", ";
			}
			addr += contact.getZip();
		} //Pin
		logger.debug("<<<<<<<<<<<<< completed getAddress ");
		return addr;
	}//getAddress

	
 
	
	/**
	 * This method dispaly's Loyalty data.
	 */
	private void updateLoyaltyData(){
		logger.debug(">>>>>>>>>>>>> entered in updateLoyaltyData");
		List<String> cardNumberList = new ArrayList<String>();
		Components.removeAllChildren(ltyOverViewMembershipNumberAnchId);
		Components.removeAllChildren(ltyInfomembershipNumberAnchId);
		try {
			//ContactsLoyalty contactLoyalty =  contactsLoyaltyDao.findByContactId(contact.getContactId());

			List<ContactsLoyalty> contactsLoyalties = contactsLoyaltyDao.findLoyaltyListByContactId(contact.getUsers().getUserId(), contact.getContactId());
			if(contactsLoyalties != null && contactsLoyalties.size()>0){
				//for loyalty contacts
				int i=0;
				for (ContactsLoyalty contactsLoyalty : contactsLoyalties) {
					logger.info("contactsLoyalty.getCardNumber()contactsLoyalty.getCardNumber()"+contactsLoyalty.getCardNumber());
					cardNumberList.add(contactsLoyalty.getCardNumber().toString());
					multipleMembershipNumber(contactsLoyalty,"both",(i == 0 ? true : false));
					i++;
				}

				/*if(contactsLoyalties.get(0)!= null){
					displayLoyaltyInfo(contactsLoyalties.get(0));
					displayLoyaltyOverview(contactsLoyalties.get(0));
				}*/
				//To display Add card# as part of customer details on top of the screen after mobile number info.
//				StringUtils.collectionToCommaDelimitedString(cardNumberList);
				cardLblId.setValue(StringUtils.collectionToCommaDelimitedString(cardNumberList).trim());
			}
			else{
				logger.error("Not a loyalty contacts..................");
			}

		} catch (Exception e) {
			logger.error("Exception ::", e);


		}
		logger.debug("<<<<<<<<<<<<< completed updateLoyaltyData ");
	}//update Loyalty Data
	
	A membershipNumberAnch = null;
	private void multipleMembershipNumber(ContactsLoyalty contactsLoyalty,String type, boolean setValue) {
		logger.debug(">>>>>>>>>>>>> entered in multipleMembershipNumber");
		Space space = null;
		logger.info("****************** Type **********"+type);
			/*if(contactsLoyalty.getServiceType()==null){
				return;
			}*/
			if("info".equalsIgnoreCase(type)){
				//clear info fields
//				clearLtyInfoFeilds();
				space = new Space();
				membershipNumberAnch = new A(contactsLoyalty.getCardNumber()+" ");
				membershipNumberAnch.setAttribute("ContactsLoyaltyInfo", contactsLoyalty);
				if(setValue){
					membershipNumberAnch.setStyle("color:black");
				}
				else{
					membershipNumberAnch.setSclass("myLinks");
				}
				membershipNumberAnch.addEventListener("onClick", this);
				space.setParent(membershipNumberAnch);
				membershipNumberAnch.setParent(ltyInfomembershipNumberAnchId);
//				displayLoyaltyInfo(contactsLoyalty);
				
			}
			else if("overView".equalsIgnoreCase(type)){
				space = new Space();
				//clear overview fields 
//				clearLtyOverviewFeilds();
				membershipNumberAnch = new A(contactsLoyalty.getCardNumber()+" ");
				membershipNumberAnch.setAttribute("ContactsLoyaltyOverView", contactsLoyalty);
				if(setValue){
					membershipNumberAnch.setStyle("color:black");
				}
				else{
					membershipNumberAnch.setSclass("myLinks");
				}
				membershipNumberAnch.addEventListener("onClick", this);
				space.setParent(membershipNumberAnch);
				membershipNumberAnch.setParent(ltyOverViewMembershipNumberAnchId);
//				displayLoyaltyOverview(contactsLoyalty);
			}
			else if("both".equalsIgnoreCase(type)){
				if(setValue){
					clearLtyInfoFeilds();
					clearLtyOverviewFeilds();
				}
				//For LoyaltyInfo
				space = new Space();
				membershipNumberAnch = new A(contactsLoyalty.getCardNumber()+" ");
				membershipNumberAnch.setAttribute("ContactsLoyaltyInfo", contactsLoyalty);
				if(setValue){
					membershipNumberAnch.setStyle("color:black");
				}
				else{
					membershipNumberAnch.setSclass("myLinks");
				}
				membershipNumberAnch.addEventListener("onClick", this);
				space.setParent(membershipNumberAnch);
				membershipNumberAnch.setParent(ltyInfomembershipNumberAnchId);
				if(setValue){
					displayLoyaltyInfo(contactsLoyalty);
				}
				
				//For LoyaltyOverView
				space = new Space();
				membershipNumberAnch = new A(contactsLoyalty.getCardNumber()+" ");
				membershipNumberAnch.setAttribute("ContactsLoyaltyOverView", contactsLoyalty);
				if(setValue){
					membershipNumberAnch.setStyle("color:black");
				}
				else{
					membershipNumberAnch.setSclass("myLinks");
				}
				membershipNumberAnch.addEventListener("onClick", this);
				space.setParent(membershipNumberAnch);
				membershipNumberAnch.setParent(ltyOverViewMembershipNumberAnchId);
				if(setValue){
					displayLoyaltyOverview(contactsLoyalty);
				}
//				i++;
			}
			logger.debug("<<<<<<<<<<<<< completed multipleMembershipNumber ");
	}//multipleMembershipNumber

	@Override
	public void onEvent(Event event) throws Exception {
		logger.debug(">>>>>>>>>>>>> entered in onEvent");
		super.onEvent(event);
		if(event == null) return;

		if(event.getTarget() instanceof A) {
			A tempAnch = (A)event.getTarget();
			
			ContactsLoyalty contactsLoyalty = (ContactsLoyalty)tempAnch.getAttribute("ContactsLoyaltyInfo");
			if(contactsLoyalty != null){
				displayLoyaltyInfo(contactsLoyalty);
				List<ContactsLoyalty> contactsLoyalties = contactsLoyaltyDao.findLoyaltyListByContactId(contact.getUsers().getUserId(), contact.getContactId());
				if(contactsLoyalties != null){
					Components.removeAllChildren(ltyInfomembershipNumberAnchId);
					for (ContactsLoyalty contactsLoyalty2 : contactsLoyalties) {
						if(contactsLoyalty.getCardNumber().equals(contactsLoyalty2.getCardNumber())){
							multipleMembershipNumber(contactsLoyalty2,"info",true);
						}
						else{
							multipleMembershipNumber(contactsLoyalty2,"info",false);
						}
					}

				}
			}
			
		}

		if(event.getTarget() instanceof A) {
			
			A tempAnch = (A)event.getTarget();
			ContactsLoyalty contactsLoyalty = (ContactsLoyalty)tempAnch.getAttribute("ContactsLoyaltyOverView");
			if(contactsLoyalty != null){
				displayLoyaltyOverview(contactsLoyalty);
				List<ContactsLoyalty> contactsLoyalties = contactsLoyaltyDao.findLoyaltyListByContactId(contact.getUsers().getUserId(), contact.getContactId());
				if(contactsLoyalties != null){
					Components.removeAllChildren(ltyOverViewMembershipNumberAnchId);
					for (ContactsLoyalty contactsLoyalty2 : contactsLoyalties) {
						if(contactsLoyalty.getCardNumber().equals(contactsLoyalty2.getCardNumber())){
							multipleMembershipNumber(contactsLoyalty2,"overView",true);
						}
						else{
							multipleMembershipNumber(contactsLoyalty2,"overView",false);
						}
					}

				}
			}
		}
	logger.debug("<<<<<<<<<<<<< completed onEvent ");
	}//onEvent
	
	
	private String getSymbolForValueCode(String valueCode) {
		String retVal=valueCode;
		
		if(valueCode.equalsIgnoreCase("USD")) retVal="$";
		
		return retVal;
	}
	
	public void onClick$refreshLoyaltyAId(){
		logger.debug(">>>>>>>>>>>>> entered in onClick$refreshLoyaltyAId");
		ContactsLoyalty contactLoyalty =  contactsLoyaltyDao.findByContactId(contact.getUsers().getUserId(), contact.getContactId());
		if(contactLoyalty==null) {
			Messagebox.show("Not a loyalty contact.","Information",Messagebox.OK,Messagebox.INFORMATION);
			return;
		}
		SparkBaseLocationDetailsDao sblDao = 
				(SparkBaseLocationDetailsDao)SpringUtil.getBean("sparkBaseLocationDetailsDao");
		
		SparkBaseLocationDetails sbDetails = sblDao.findBylocationId(
				GetUser.getUserObj().getUserOrganization().getUserOrgId(), contactLoyalty.getLocationId());
		
		InquiryResponse errMsg = null;
		
		// Get the latest Loyalty balance info
		errMsg =(InquiryResponse)SparkBaseServiceAsync.getInstance().fetchData(SparkBaseServiceAsync.INQUIRY, sbDetails, contactLoyalty, contact,null, true);
		
		if(errMsg==null) {
			MessageUtil.setMessage("Error occured while fetching the data.", "color:red;");
			return;
		} 

		// Get the latest Account History for total counts
		ErrorMessageComponent errorMsg = (ErrorMessageComponent)SparkBaseServiceAsync.getInstance().fetchData(SparkBaseServiceAsync.ACCOUNT_HISTORY, sbDetails, contactLoyalty, contact,null, true);
		if(errorMsg!=null) {
			showSparkBaseErrorMessage(errorMsg);
			return;
		} // if

		contactLoyalty.setLastFechedDate(Calendar.getInstance());
		contactsLoyaltyDaoForDML.saveOrUpdate(contactLoyalty);
		updateLoyaltyData();
		logger.debug("<<<<<<<<<<<<< completed onClick$refreshLoyaltyAId ");
	}//onClick$refreshLoyaltyAId
	
	private void showSparkBaseErrorMessage(ErrorMessageComponent errMsg) {
		
		 logger.info("Error Message");
		 
		 String errMsgStr = 
			 "Error Message : \n\t " +
			 "  Rejection Id = " + errMsg.getRejectionId() + "\n\t" +
		     "  Error Code = " + errMsg.getErrorCode() + "\n\t" +
		     "  Brief Message = " + errMsg.getBriefMessage() + "\n\t" +
		     "  In-Depth Message = " + errMsg.getInDepthMessage();
		    
		 MessageUtil.setMessage(errMsgStr, "color:red;");
	}
	
	private Window latestDetailsWinId;
	
	private Div campDetailsDivId,openDetailsDivId,clickDetailsDivId;
	 private Rows latestCampRowsId,latestOpenedCampRowsId,latestCampClicksRowsId;
	
	 private Div latestDetailsWinId$viewAllCampDetailsDivId,latestDetailsWinId$viewAllCampOpendDetailsDivId,
		latestDetailsWinId$viewClickDetailsDivId,latestDetailsWinId$viewPurchaseDetailsDivId,
		latestDetailsWinId$viewSkuDetailsDivId,latestDetailsWinId$viewItemCategDetailsDivId,latestDetailsWinId$viewPromoDetailsDivId;

	 private Rows latestDetailsWinId$viewAllCampDetailsRowsId,latestDetailsWinId$viewAllCampOpenedCampRowsId,
	 	 latestDetailsWinId$viewAllCampClicksRowsId,latestDetailsWinId$viewAllPurchaseRowsId,
	 	 latestDetailsWinId$viewAllSKURowsId,latestDetailsWinId$viewAllItemCategRowsId,latestDetailsWinId$viewAllpromosRowsId;
//latestDetailsWinId,viewAllCampDetailsDivId,viewAllCampDetailsRowsId
	
	
	//Latest Campaign Details
	public void onClick$latesCampignAnchId() {
		
		clickDetailsDivId.setVisible(false);
		openDetailsDivId.setVisible(false);
		campDetailsDivId.setVisible(true);
		
		if(contact.getEmailId() == null || contact.getEmailId().trim().length() ==0 ) return;
		
		List<Object[]> latestCampList =  campaignReportDao.getCampaignBasedOnContact(contact.getUsers().getUserId(), contact.getEmailId(), Constants.LIMIT);
	//	List<Object[]> latestCampList =  campaignReportDao.getCampaignBasedOnContact(contact.getUsers().getUserId(), contact.getContactId(), Constants.LIMIT);
		
		if(latestCampList == null || latestCampList.size() == 0 ) {
			return;
		}
		
		 Row tempRow = null;
		 Components.removeAllChildren(latestCampRowsId);
		 for (Object[] objectArr : latestCampList) {
			 
			 tempRow  = new Row();
			 
			 //Campaign Name
			 Label campignNameLbl = new Label();
			 if(objectArr[0] != null) {
				 
				 campignNameLbl.setValue(objectArr[0].toString());
			 }
			 campignNameLbl.setParent(tempRow);
			 
			 //email
			 Label emailLbl = new Label();
			 if(objectArr[1] != null) {
				 
				 emailLbl.setValue(objectArr[1].toString());
			 }
			 emailLbl.setParent(tempRow);
			
			 //Sent On
			 Label opendOnLbl = new Label();
			 if(objectArr[2] != null) {
				 
				 opendOnLbl.setValue(objectArr[2].toString());
			 }
			 opendOnLbl.setParent(tempRow);
			 
			 
			 tempRow.setParent(latestCampRowsId);
		}
		
	} // onClick$latesCampignTbBtnId
	
	//viewAll Campaigns
	public void onClick$viewAllCampAnchId() {
		
		if(contact == null ){
			logger.error("contact not existed .. problem with session .. "+contact);
			return;
		}
		latestDetailsWinId.doHighlighted();
		
		openSubWindDiv(latestDetailsWinId$viewAllCampDetailsDivId);
		
//		latestDetailsWinId$viewAllCampDetailsDivId.setVisible(true);
		
		logger.debug("---Contact UserId is::"+contact.getUsers().getUserId());
		logger.debug("---Contact Id is:"+contact.getContactId());
		if(contact.getEmailId() == null || contact.getEmailId().trim().length() ==0 ) return;
		List<Object[]> latestCampList =  campaignReportDao.getCampaignBasedOnContact(contact.getUsers().getUserId(), contact.getEmailId(), Constants.VIEW_ALL);
	//	List<Object[]> latestCampList =  campaignReportDao.getCampaignBasedOnContact(contact.getUsers().getUserId(), contact.getContactId(), Constants.VIEW_ALL);
		
		if(latestCampList == null && latestCampList.size() == 0 ) {
			return;
		}
		
		Components.removeAllChildren(latestDetailsWinId$viewAllCampDetailsRowsId);
		 Row tempRow = null;
		 
		 for (Object[] objectArr : latestCampList) {
			 
			 tempRow  = new Row();
			 
			 //Campaign Name
			 Label campignNameLbl = new Label();
			 if(objectArr[0] != null) {
				 
				 campignNameLbl.setValue(objectArr[0].toString());
			 }
			 campignNameLbl.setParent(tempRow);
			 
			 //Email
			 Label emailNameLbl = new Label();
			 if(objectArr[1] != null) {
				 
				 emailNameLbl.setValue(objectArr[1].toString());
			 }
			 emailNameLbl.setParent(tempRow);
			 
			 //Sent On
			 Label opendOnLbl = new Label();
			 if(objectArr[2] != null) {
				 
				 opendOnLbl.setValue(objectArr[2].toString());
			 }
			 opendOnLbl.setParent(tempRow);
			 
			 
			 tempRow.setParent(latestDetailsWinId$viewAllCampDetailsRowsId);
		}
	} // onClick$viewAllCampAnchId
	
	
	//latest Camp Open Details
	public void onClick$latesCampOpensAnchId() {
		
		clickDetailsDivId.setVisible(false);
		campDetailsDivId.setVisible(false);
		openDetailsDivId.setVisible(true);
		if(contact.getEmailId() == null) return;
		
//		List<Object[]> latestOpenList = campaignReportDao.getCampOpenBasedOnCont(contact.getMailingList().getUsers().getUserId(), contact.getContactId(), Constants.LIMIT);
		List<Object[]> latestOpenList = campaignReportDao.getCampOpenBasedOnCont(contact.getUsers().getUserId(), contact.getEmailId(), Constants.LIMIT);
		
		if(latestOpenList == null || latestOpenList.size() == 0 ) {
			return;
		}
		Components.removeAllChildren(latestOpenedCampRowsId);
		 Row tempRow = null;
		 
		 for (Object[] objectArr : latestOpenList) {
			 
			 tempRow  = new Row();
			 
			 //Campaign Name
			 Label campignNameLbl = new Label();
			 if(objectArr[0] != null) {
				 
				 campignNameLbl.setValue(objectArr[0].toString());
			 }
			 campignNameLbl.setParent(tempRow);
			
			 //Viewed On 
			 Label opendOnLbl = new Label();
			 if(objectArr[1] != null) {
				 
				 opendOnLbl.setValue(objectArr[1].toString());
			 }
			 opendOnLbl.setParent(tempRow);
			 
			 
			 tempRow.setParent(latestOpenedCampRowsId);
		}
	
	
	} // onClick$latesCampOpensTbBtnId
	
	//view All Opened Campaign Details
	public void onClick$viewAllOpenedCampAnchId() {
		latestDetailsWinId.doHighlighted();
		openSubWindDiv(latestDetailsWinId$viewAllCampOpendDetailsDivId);
		if(contact.getEmailId() == null) return;
		
//		List<Object[]> viewAllOpenList = campaignReportDao.getCampOpenBasedOnCont(contact.getMailingList().getUsers().getUserId(), contact.getContactId(), Constants.VIEW_ALL);
		List<Object[]> viewAllOpenList = campaignReportDao.getCampOpenBasedOnCont(contact.getUsers().getUserId(), contact.getEmailId() , Constants.VIEW_ALL);
		
		if(viewAllOpenList == null || viewAllOpenList.size() == 0 ) {
			return;
		}
		Components.removeAllChildren(latestDetailsWinId$viewAllCampOpenedCampRowsId);
		 Row tempRow = null;
		 
		 for (Object[] objectArr : viewAllOpenList) {
			 
			 tempRow  = new Row();
			 
			 //Campaign Name
			 Label campignNameLbl = new Label();
			 if(objectArr[0] != null) {
				 
				 campignNameLbl.setValue(objectArr[0].toString());
			 }
			 campignNameLbl.setParent(tempRow);
			
			 //Viewed On 
			 Label opendOnLbl = new Label();
			 if(objectArr[1] != null) {
				 
				 opendOnLbl.setValue(objectArr[1].toString());
			 }
			 opendOnLbl.setParent(tempRow);
			 
			 
			 tempRow.setParent(latestDetailsWinId$viewAllCampOpenedCampRowsId);
		}
		
		
	}
	
	
	
	//Latest Clicks details
	public void onClick$latesCampClicksAnchId() {
		
		campDetailsDivId.setVisible(false);
		openDetailsDivId.setVisible(false);
		clickDetailsDivId.setVisible(true);
		if(contact.getEmailId() == null) return;
		List<Object[]> latestClicksList = campaignReportDao.getCampClickBasedOnCont(contact.getUsers().getUserId(), contact.getEmailId(), Constants.LIMIT);
				
		if(latestClicksList == null || latestClicksList.size() == 0 ) {
			return;
		}
		
		Components.removeAllChildren(latestCampClicksRowsId);
		 Row tempRow = null;
		 Label tempLbl = null;
		 for (Object[] objectArr : latestClicksList) {
			 
			 tempRow  = new Row();
			 
			//Email Campaign link
			 tempLbl = new Label();
			 if(objectArr[0] != null) {
				 
				 tempLbl.setValue(objectArr[0].toString());
			 }
			 tempLbl.setParent(tempRow);
			 
			 
			 //Campaign Name
			 tempLbl = new Label();
			 if(objectArr[1] != null) {
				 
				 tempLbl.setValue(objectArr[1].toString());
			 }
			 tempLbl.setParent(tempRow);
			
			 //Clicked On
			 tempLbl = new Label();
			 if(objectArr[2] != null) {
				 
				 tempLbl.setValue(objectArr[2].toString());
			 }
			 tempLbl.setParent(tempRow);
			 
			 
			 tempRow.setParent(latestCampClicksRowsId);
		}
	
		
		
		
	} // onClick$latesCampClicksTbBtnId
	
	
	// view All Clicked Url Details
	public void onClick$viewAllClickURLAnchId() {
		
		
		latestDetailsWinId.doHighlighted();
		
		openSubWindDiv(latestDetailsWinId$viewClickDetailsDivId);
		if(contact.getEmailId() == null) return;
		
		List<Object[]> viewAllClicksList = campaignReportDao.getCampClickBasedOnCont(contact.getUsers().getUserId(), contact.getEmailId(), Constants.VIEW_ALL);
		
		if(viewAllClicksList == null || viewAllClicksList.size() == 0 ) {
			return;
		}
		Components.removeAllChildren(latestDetailsWinId$viewAllCampClicksRowsId);
		 Row tempRow = null;
		 Label tempLbl = null;
		 
		 for (Object[] objectArr : viewAllClicksList) {
			 
			 tempRow  = new Row();
			 
			//Email Campaign link
			 tempLbl = new Label();
			 if(objectArr[0] != null){
				 
				 tempLbl.setValue(objectArr[0].toString());
			 }
			 tempLbl.setParent(tempRow);
			 
			 
			 //Campaign Name
			 tempLbl = new Label();
			 if(objectArr[1] != null) {
				 
				 tempLbl.setValue(objectArr[1].toString());
			 }
			 tempLbl.setParent(tempRow);
			
			 //Clicked On
			 tempLbl = new Label();
			 if(objectArr[2] != null) {
				 
				 tempLbl.setValue(objectArr[2].toString());
			 }
			 tempLbl.setParent(tempRow);
			 
			 tempRow.setParent(latestDetailsWinId$viewAllCampClicksRowsId);
		}
	
	} //onClick$viewAllClickURLAnchId
	
	
	
	/**  Latest Pos Details**/
	
   private	Div purchaseDetailsDivId,skuDetailsDivId,itemCategDetailsDivId,promoDetailsDivId;
   private  Rows latestPurchaseRowsId,latestSKURowsId,latestItemCategRowsId,latestpromosRowsId;
	
	public void onClick$latestPurchaseAnchId() {

		
		skuDetailsDivId.setVisible(false);
		itemCategDetailsDivId.setVisible(false);
		promoDetailsDivId.setVisible(false);
		purchaseDetailsDivId.setVisible(true);
		
		//if(contPOSMl == null || contact.getExternalId() == null || contact.getExternalId().trim().length() == 0) return;
		if(contPOSMl == null) return;
		List<Map<String, Object>> latestPurchaseList = retailProSalesDao.findPurchaseDetBasedOnCid(contact.getContactId(), contact.getUsers().getUserId(), Constants.LIMIT);
		
		if(latestPurchaseList == null || latestPurchaseList.size() == 0) return;
		
		Components.removeAllChildren(latestPurchaseRowsId);
		
		Row tempRow = null;
		Label tempLbl = null;
		
		
		for (Map<String, Object> eachMap : latestPurchaseList) {
			
			try {
				tempRow = new Row();
				
				tempLbl = new Label();
				
				if(eachMap.containsKey("reciept_number") && eachMap.get("reciept_number") != null) {
					
					tempLbl.setValue(""+eachMap.get("reciept_number"));
				}
				tempLbl.setParent(tempRow);
				
				//((Qty*SalesPrice)+Tax) Amount as SALES_PRICE
				tempLbl = new Label();
				if(eachMap.containsKey("SALES_PRICE") && eachMap.get("SALES_PRICE") != null) {
					
					tempLbl.setValue(Utility.getAmountInUSFormat(eachMap.get("SALES_PRICE")));
				}
				/*tempLbl.setValue(""+((Integer.parseInt(retailProSalesCSV.getQuantity())*retailProSalesCSV.getSalesPrice())+
																								retailProSalesCSV.getTax()));*/
				tempLbl.setParent(tempRow);
				
				//Purchased On
				
//				tempLbl = new Label(MyCalendar.calendarToString(retailProSalesCSV.getSalesDate(),MyCalendar.FORMAT_DATEONLY_GENERAL));
				tempLbl = new Label();
				if(eachMap.containsKey("sales_date") && eachMap.get("sales_date") != null) {
					logger.info("eachMap.get(sales_date) is ::"+eachMap.get("sales_date"));
					Calendar tempCal = Calendar.getInstance(); 
					SimpleDateFormat sdf = new SimpleDateFormat(MyCalendar.FORMAT_DATETIME_STYEAR);
					tempCal.setTime(sdf.parse(eachMap.get("sales_date").toString()));
							//(Calendar)eachMap.get("sales_date");
					tempLbl.setValue(MyCalendar.calendarToString(tempCal,MyCalendar.FORMAT_YEARTODATE));
				}
				tempLbl.setParent(tempRow);
				tempRow.setParent(latestPurchaseRowsId);
				
			} catch (Exception e) {
				logger.error("Exception ::", e);
			}
		}
	} //onClick$latestPurchaseAnchId
	
	public void onClick$viewAllPurchaseAnchId() {
		
		latestDetailsWinId.doHighlighted();
		
		openSubWindDiv(latestDetailsWinId$viewPurchaseDetailsDivId);
		
//		if(contPOSMl == null || contact.getExternalId() == null || contact.getExternalId().trim().length() == 0) return;
		if(contPOSMl == null) return;
		List<Map<String,Object>> viewAllPurchaseList = retailProSalesDao.findPurchaseDetBasedOnCid(contact.getContactId(), contact.getUsers().getUserId(), Constants.VIEW_ALL);
		
		if(viewAllPurchaseList == null || viewAllPurchaseList.size() == 0) return;
		
		Components.removeAllChildren(latestDetailsWinId$viewAllPurchaseRowsId);
		
		Row tempRow = null;
		Label tempLbl = null;
		for (Map<String, Object> eachMap : viewAllPurchaseList) {
			
			try {
				tempRow = new Row();
				
				tempLbl = new Label();
				if(eachMap.containsKey("reciept_number") && eachMap.get("reciept_number") != null) {
					
					tempLbl.setValue(""+eachMap.get("reciept_number"));
				}
				tempLbl.setParent(tempRow);
				//((Qty*SalesPrice)+Tax) Amount
				tempLbl = new Label();
				if(eachMap.containsKey("SALES_PRICE") && eachMap.get("SALES_PRICE") != null) {
					
					tempLbl.setValue(Utility.getAmountInUSFormat(eachMap.get("SALES_PRICE")));
				}
				
				tempLbl.setParent(tempRow);
				
				//Purchased On
				
//				tempLbl = new Label(MyCalendar.calendarToString(retailProSalesCSV.getSalesDate(),MyCalendar.FORMAT_DATEONLY_GENERAL));
				tempLbl = new Label();
				if(eachMap.containsKey("sales_date") && eachMap.get("sales_date") != null) {
					//tempLbl.setValue(objectArr[1].toString());
					Calendar tempCal = Calendar.getInstance(); 
					SimpleDateFormat sdf = new SimpleDateFormat(MyCalendar.FORMAT_DATETIME_STYEAR);
					tempCal.setTime(sdf.parse(eachMap.get("sales_date").toString()));
					tempLbl.setValue(MyCalendar.calendarToString(tempCal,MyCalendar.FORMAT_YEARTODATE));
				}
				tempLbl.setParent(tempRow);
				tempRow.setParent(latestDetailsWinId$viewAllPurchaseRowsId);
				
			} catch (Exception e) {
				logger.error("Exception ::", e);
			}
		}
		
		
	}
	
	
	public void onClick$latestSKUAnchId() {
		
		try {
			itemCategDetailsDivId.setVisible(false);
			promoDetailsDivId.setVisible(false);
			purchaseDetailsDivId.setVisible(false);
			skuDetailsDivId.setVisible(true);
//			if(contPOSMl == null || contact.getExternalId() == null || contact.getExternalId().trim().length() == 0) return;
			if(contPOSMl == null) return;
			List<Map<String, Object>> latestSKUList = retailProSalesDao.findSkuDetBasedOnCid(contact.getContactId(), contact.getUsers().getUserId(), Constants.LIMIT);
			
			if(latestSKUList == null || latestSKUList.size() == 0) return;
			
			Components.removeAllChildren(latestSKURowsId);
			
			Row tempRow = null;
			Label tempLbl = null;
			
			for (Map<String, Object> eachMap :  latestSKUList) {
				tempRow = new Row();
				
				//sales.reciept_number, sales.sku, sku.item_category, sum(sales.quantity) as qty, sales.sales_date
				 //reciept Num ( objectArr[0].toString()) ignored
				
				//SKU
				tempLbl = new Label();
				if(eachMap.containsKey("sku") && eachMap.get("sku") != null) {
					
					tempLbl.setValue(eachMap.get("sku").toString());
				}
				tempLbl.setParent(tempRow);
				
				//Item Category
				tempLbl = new Label();
				if(eachMap.containsKey("item_category") && eachMap.get("item_category") != null) {
					
					tempLbl.setValue(eachMap.get("item_category").toString());
				}
				tempLbl.setParent(tempRow);
				
				//No of Items
				tempLbl = new Label();
				if(eachMap.containsKey("qty") && eachMap.get("qty") != null) {
					
					tempLbl.setValue(eachMap.get("qty").toString());
				}
				tempLbl.setParent(tempRow);
				
				//Purchased On
				tempLbl = new Label();
				if(eachMap.containsKey("sales_date") && eachMap.get("sales_date") != null) {
					Calendar tempCal = Calendar.getInstance(); 
					SimpleDateFormat sdf = new SimpleDateFormat(MyCalendar.FORMAT_DATETIME_STYEAR);
					tempCal.setTime(sdf.parse(eachMap.get("sales_date").toString()));
					tempLbl.setValue(MyCalendar.calendarToString(tempCal,MyCalendar.FORMAT_YEARTODATE));
				}
				tempLbl.setParent(tempRow);
				
				tempRow.setParent(latestSKURowsId);
				
}
		} catch (ParseException e) {
			logger.error("Exception ::", e);
		}
			
	} //onClick$latestSKUAnchId
	
	public void onClick$viewAllSKUAnchId() {
		
		try {
			latestDetailsWinId.doHighlighted();
			openSubWindDiv(latestDetailsWinId$viewSkuDetailsDivId);
			
//			if(contPOSMl == null || contact.getExternalId() == null || contact.getExternalId().trim().length() == 0) return;
			if(contPOSMl == null) return;
			List<Map<String, Object>> viewAllSKUList = retailProSalesDao.findSkuDetBasedOnCid(contact.getContactId(), contact.getUsers().getUserId(), Constants.VIEW_ALL);
			
			if(viewAllSKUList == null || viewAllSKUList.size() == 0) return;
			
			Components.removeAllChildren(latestDetailsWinId$viewAllSKURowsId);
			
			Row tempRow = null;
			Label tempLbl = null;
//		for (RetailProSalesCSV retailProSalesCSV : latestPurchaseList) {
			
			for (Map<String, Object> eachMap : viewAllSKUList) {
				
				tempRow = new Row();
				//objectArr[0] ignore reciept number
				
				//SKU
				tempLbl = new Label();
				if(eachMap.containsKey("sku") && eachMap.get("sku") != null) {
					tempLbl.setValue(eachMap.get("sku").toString());
				}
				tempLbl.setParent(tempRow);
				
				//Item Category
				tempLbl = new Label();
				if(eachMap.containsKey("item_category") && eachMap.get("item_category") != null) {
					tempLbl.setValue(eachMap.get("item_category").toString());
				}
				tempLbl.setParent(tempRow);
				
				//No of Items
				tempLbl = new Label();
				if(eachMap.containsKey("qty") && eachMap.get("qty") != null) {
					tempLbl.setValue(eachMap.get("qty").toString());
				}
				tempLbl.setParent(tempRow);
				
				//Purchased On
				tempLbl = new Label();
				if(eachMap.containsKey("sales_date") && eachMap.get("sales_date") != null) {
					Calendar tempCal = Calendar.getInstance(); 
					SimpleDateFormat sdf = new SimpleDateFormat(MyCalendar.FORMAT_DATETIME_STYEAR);
					tempCal.setTime(sdf.parse(eachMap.get("sales_date").toString()));
					tempLbl.setValue(MyCalendar.calendarToString(tempCal,MyCalendar.FORMAT_YEARTODATE));
				}
				tempLbl.setParent(tempRow);
				tempRow.setParent(latestDetailsWinId$viewAllSKURowsId);
			}
		} catch (ParseException e) {
			logger.error("Exception ::", e);
		}
		
		
	}
	
	
	public void onClick$latestItemCategAnchId() {
		try {
			promoDetailsDivId.setVisible(false);
			purchaseDetailsDivId.setVisible(false);
			skuDetailsDivId.setVisible(false);
			itemCategDetailsDivId.setVisible(true);

			//if(contPOSMl == null || contact.getExternalId() == null || contact.getExternalId().trim().length() == 0) return;
			if(contPOSMl == null) return; 
			List<Map<String, Object>> latestSKUList = retailProSalesDao.findItemCategBasedOnCont(contact.getContactId(), contact.getUsers().getUserId(), Constants.LIMIT);

			if(latestSKUList == null || latestSKUList.size() == 0) return;

			Components.removeAllChildren(latestItemCategRowsId);

			Row tempRow = null;
			Label tempLbl = null;
			//		for (RetailProSalesCSV retailProSalesCSV : latestPurchaseList) {

			for (Map<String, Object> eachMap : latestSKUList) {
				//System.out.println("------ eachMap" +eachMap);
				tempRow = new Row();

				//Item category
				//				distinct(sku.item_category) ,sum(sales.quantity) as qty 
				tempLbl = new Label();
				if(eachMap.containsKey("item_category") && eachMap.get("item_category") != null) {
					tempLbl.setValue(eachMap.get("item_category").toString());
				}
				tempLbl.setParent(tempRow);

				// Vendor_code

				tempLbl = new Label();
				if(eachMap.containsKey("vendor_code") && eachMap.get("vendor_code") != null) {

					tempLbl.setValue(eachMap.get("vendor_code").toString());
				}
				tempLbl.setParent(tempRow);

				//no.of Item Purchased
				tempLbl = new Label();
				if(eachMap.containsKey("qty") && eachMap.get("qty") != null) {

					tempLbl.setValue(eachMap.get("qty").toString());
				}
				tempLbl.setParent(tempRow);


				tempRow.setParent(latestItemCategRowsId);


			}
		} catch (Exception e) {
			logger.error("Exception ::", e);
		}
	} //onClick$latestItemCategAnchId 
	
	
	public void onClick$viewAllItemCategAnchId() {
		latestDetailsWinId.doHighlighted();
		openSubWindDiv(latestDetailsWinId$viewItemCategDetailsDivId);

		//	if(contPOSMl == null || contact.getExternalId() == null || contact.getExternalId().trim().length() == 0) return;
		if(contPOSMl == null) return;
		List<Map<String, Object>> latestSKUList = retailProSalesDao.findItemCategBasedOnCont(contact.getContactId(), contact.getUsers().getUserId(), Constants.VIEW_ALL);

		if(latestSKUList == null || latestSKUList.size() == 0) return;

		Components.removeAllChildren(latestDetailsWinId$viewAllItemCategRowsId);

		Row tempRow = null;
		Label tempLbl = null;
		//	for (RetailProSalesCSV retailProSalesCSV : latestPurchaseList) {

		for (Map<String, Object> eachMap : latestSKUList) {
			tempRow = new Row();
			//Item category
			tempLbl = new Label();
			if(eachMap.containsKey("item_category") && eachMap.get("item_category") != null) {
				tempLbl.setValue(eachMap.get("item_category").toString());
			}
			tempLbl.setParent(tempRow);

			// Vendor_code

			tempLbl = new Label();
			if(eachMap.containsKey("vendor_code") && eachMap.get("vendor_code") != null) {

				tempLbl.setValue(eachMap.get("vendor_code").toString());
			}
			tempLbl.setParent(tempRow);

			//No of Item purchased
			tempLbl = new Label();
			if(eachMap.containsKey("qty") && eachMap.get("qty") != null) {

				tempLbl.setValue(eachMap.get("qty").toString());
			}
			tempLbl.setParent(tempRow);


			tempRow.setParent(latestDetailsWinId$viewAllItemCategRowsId);
		}

	}
	
	public void onClick$latestPromosAnchId() {
		
		purchaseDetailsDivId.setVisible(false);
		skuDetailsDivId.setVisible(false);
		itemCategDetailsDivId.setVisible(false);
		promoDetailsDivId.setVisible(true);
		
//		if(contPOSMl == null || contact.getExternalId() == null || contact.getExternalId().trim().length() == 0) return;
		if(contPOSMl == null) return;
		
		List<Map<String, Object>> latestPromoList = retailProSalesDao.findPromoBasedOnCont(contact.getContactId(), contact.getUsers().getUserId(), Constants.LIMIT);
		
		if(latestPromoList == null || latestPromoList.size() == 0) return;
		
		Components.removeAllChildren(latestpromosRowsId);
		
		Row tempRow = null;
		Label tempLbl = null;
//		for (RetailProSalesCSV retailProSalesCSV : latestPurchaseList) {
		//sales.promo_code, sales.sales_date, ROUND((sales.quantity*sales.sales_price)+sales.tax,2) as price, sku.store_number " +
		for (Map<String, Object> eachMap: latestPromoList) {
			tempRow = new Row();
			//PromoCode
			tempLbl = new Label();
			if(eachMap.containsKey("promo_code") && eachMap.get("promo_code") != null) {
				tempLbl.setValue(eachMap.get("promo_code").toString());
			}
			tempLbl.setParent(tempRow);
			
			//Redeemed On
			tempLbl = new Label();
			if(eachMap.containsKey("sales_date") && eachMap.get("sales_date") != null) {
				
				tempLbl.setValue(eachMap.get("sales_date").toString());
			}
			tempLbl.setParent(tempRow);
			
			//Purchased Amount
			tempLbl = new Label();
			if(eachMap.containsKey("price") && eachMap.get("price") != null) {
				
				tempLbl.setValue(eachMap.get("price").toString());
			}	
			
			tempLbl.setParent(tempRow);
			
			//Purchased at
			tempLbl = new Label();
			if(eachMap.containsKey("store_number") && eachMap.get("store_number") != null) {
				
				tempLbl.setValue(eachMap.get("store_number").toString());
			}
			tempLbl.setParent(tempRow);
			
			tempRow.setParent(latestpromosRowsId);
		}
		
	} //onClick$latestPromosAnchId
	
	public void onClick$viewAllPromosAnchId() {
		
		latestDetailsWinId.doHighlighted();
		openSubWindDiv(latestDetailsWinId$viewPromoDetailsDivId);
//		if(contPOSMl == null || contact.getExternalId() == null || contact.getExternalId().trim().length() == 0) return;
		if(contPOSMl == null ) return;
		List<Map<String, Object>> latestPromoList = retailProSalesDao.findPromoBasedOnCont(contact.getContactId(), contact.getUsers().getUserId(), Constants.LIMIT);
		
		if(latestPromoList == null || latestPromoList.size() == 0) return;
		
		Components.removeAllChildren(latestDetailsWinId$viewAllpromosRowsId);
		
		Row tempRow = null;
		Label tempLbl = null;
//		for (RetailProSalesCSV retailProSalesCSV : latestPurchaseList) {
		
		for (Map<String, Object> eachMap: latestPromoList) {
			tempRow = new Row();
			//PromoCode
			tempLbl = new Label();
			if(eachMap.containsKey("promo_code") && eachMap.get("promo_code") != null) {
				tempLbl.setValue(eachMap.get("promo_code").toString());
			}
			tempLbl.setParent(tempRow);
			
			//Redeemed On
			tempLbl = new Label();
			if(eachMap.containsKey("sales_date") && eachMap.get("sales_date") != null) {
				
				tempLbl.setValue(eachMap.get("sales_date").toString());
			}
			tempLbl.setParent(tempRow);
			
			//Purchased Amount
			tempLbl = new Label();
			if(eachMap.containsKey("price") && eachMap.get("price") != null) {
				
				tempLbl.setValue(eachMap.get("price").toString());
			}	
			
			tempLbl.setParent(tempRow);
			
			//Purchased at
			tempLbl = new Label();
			if(eachMap.containsKey("store_number") && eachMap.get("store_number") != null) {
				
				tempLbl.setValue(eachMap.get("store_number").toString());
			}
			tempLbl.setParent(tempRow);
			tempRow.setParent(latestDetailsWinId$viewAllpromosRowsId);
		}
		
	}	
	
	private void openSubWindDiv(Div tempDiv) {
		
		
		Div[] openSubWindDiv = {latestDetailsWinId$viewAllCampDetailsDivId,latestDetailsWinId$viewAllCampOpendDetailsDivId,
								latestDetailsWinId$viewClickDetailsDivId,latestDetailsWinId$viewPurchaseDetailsDivId,
								latestDetailsWinId$viewSkuDetailsDivId,latestDetailsWinId$viewItemCategDetailsDivId,
								latestDetailsWinId$viewPromoDetailsDivId};
		
		for(int i=0; i<openSubWindDiv.length; i++ ) {
			
			if(openSubWindDiv[i] == tempDiv) {
				openSubWindDiv[i].setVisible(true);
			}else openSubWindDiv[i].setVisible(false);
		}
		
		
	} // openSubWindDiv
	
		public void onClick$backToListBtnId() {
		
//		sessionScope.removeAttribute("VIEW_CONTACT_DETAILS");
			Redirect.goToPreviousPage();
		}
		
		
		/**
		 * This method display loyalty overview details
		 * @param contactsLoyalty
		 */
		private void displayLoyaltyOverview(ContactsLoyalty contactsLoyalty){
			logger.debug(">>>>>>>>>>>>> entered in displayLoyaltyOverview");
			try {
			if(contactsLoyalty != null){
				LoyaltyProgramService ltyPrgmService = new LoyaltyProgramService();
				clearLtyOverviewFeilds();
				long countOfIssuance  = ltyPrgmService.getCountOfIssuance(GetUser.getUserId().longValue(),contactsLoyalty.getCardNumber(),OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE);
				long countOfRedemption = ltyPrgmService.getCountOfRedemption(GetUser.getUserId().longValue(),contactsLoyalty.getCardNumber(),OCConstants.LOYALTY_TRANS_TYPE_REDEMPTION);
				ltyOverViewMembershipNumberAnchId.setStyle("color:red;");
				//ltyOverViewMembershipNumberLblId.setValue(contactsLoyalty.getCardNumber() == null ? "":contactsLoyalty.getCardNumber()+"");
				//Balance Currency
				balanceCurrencyLblId.setValue(contactsLoyalty.getGiftcardBalance() == null ? userCurrencySymbol +" 0.00": Utility.getAmountInUSFormat(contactsLoyalty.getGiftcardBalance()));
				//Balance Points
				balancePointsLblId.setValue(contactsLoyalty.getLoyaltyBalance() == null ? "0 Points" :contactsLoyalty.getLoyaltyBalance().intValue()+" Points");
				//Hold currency balance
				if(contactsLoyalty.getHoldAmountBalance() != null && !contactsLoyalty.getHoldAmountBalance().toString().isEmpty() && contactsLoyalty.getHoldAmountBalance() != 0.0 
						&& contactsLoyalty.getHoldPointsBalance() != null && !contactsLoyalty.getHoldPointsBalance().toString().isEmpty() && contactsLoyalty.getHoldPointsBalance() != 0.0) {
					holdBalDivId.setVisible(true);
					holdBalValLblId.setValue(Utility.getAmountInUSFormat(contactsLoyalty.getHoldAmountBalance()) + " & "+ 
												contactsLoyalty.getHoldPointsBalance().intValue()+" Points");
				}
				else if(contactsLoyalty.getHoldAmountBalance() != null && !contactsLoyalty.getHoldAmountBalance().toString().isEmpty() && contactsLoyalty.getHoldAmountBalance() != 0.0
						&& (contactsLoyalty.getHoldPointsBalance() == null || contactsLoyalty.getHoldPointsBalance().toString().isEmpty() || contactsLoyalty.getHoldPointsBalance() == 0.0)) {
					holdBalDivId.setVisible(true);
					holdBalValLblId.setValue(Utility.getAmountInUSFormat(contactsLoyalty.getHoldAmountBalance()));
				}
				else if(contactsLoyalty.getHoldPointsBalance() != null && !contactsLoyalty.getHoldPointsBalance().toString().isEmpty() && contactsLoyalty.getHoldPointsBalance() != 0.0
						&& (contactsLoyalty.getHoldAmountBalance() == null || contactsLoyalty.getHoldAmountBalance().toString().isEmpty() || contactsLoyalty.getHoldAmountBalance() == 0.0)) {
					holdBalDivId.setVisible(true);
					holdBalValLblId.setValue(contactsLoyalty.getHoldPointsBalance().intValue()+" Points");
				}
				if(contactsLoyalty.getProgramTierId() != null) {
						LoyaltyProgramTierDao loyaltyProgramTierDao = (LoyaltyProgramTierDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_TIER_DAO);
						LoyaltyProgramTier loyaltyProgramTier = loyaltyProgramTierDao.findByTierId(contactsLoyalty.getProgramTierId());
						if(loyaltyProgramTier.getActivationFlag() == OCConstants.FLAG_YES) {
							holdBalDivId.setVisible(true);
						}
				}
				ReferralcodesIssuedDao refcodesissued=null;
				try {
					refcodesissued = (ReferralcodesIssuedDao) ServiceLocator.getInstance().getDAOByName(OCConstants.REFERRALCODES_DAO);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				List<ReferralcodesIssued> refissuedobj=refcodesissued.getRefcodebycontactid(contact.getContactId());

				//Life time Points
				lifetimePointsLblId.setValue(contactsLoyalty.getTotalLoyaltyEarned() == null ? "0 Points" :  contactsLoyalty.getTotalLoyaltyEarned().intValue()+" Points");
				//Total currency
				totalCurrencyEarnedLblId.setValue(contactsLoyalty == null || contactsLoyalty.getTotalGiftcardAmount() == null ? userCurrencySymbol +"0.00" :Utility.getAmountInUSFormat(contactsLoyalty.getTotalGiftcardAmount()));
				//Points Redeemed
				pointsRedemedLblId.setValue(contactsLoyalty == null || contactsLoyalty.getTotalLoyaltyRedemption() == null ? "0 Points" : contactsLoyalty.getTotalLoyaltyRedemption().intValue()+" Points");
				//Currency Redeemed
				currencyRedemedLblId.setValue(contactsLoyalty == null || contactsLoyalty.getTotalGiftcardRedemption() == null ? userCurrencySymbol + "0.00" : Utility.getAmountInUSFormat(contactsLoyalty.getTotalGiftcardRedemption()));
				//Total Loyalty Issuance
				totalLoyaltyIssuanceLblId.setValue(countOfIssuance+"");
				//Total Redemptions
				totalRedemptionsLblId.setValue(countOfRedemption+"");
				
				//Referralcode 
				referralcodeLblId.setValue(refissuedobj!=null ? refissuedobj.get(0).getRefcode() : " ");
				
				if(OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE.equalsIgnoreCase(contactsLoyalty.getMembershipType())){
//					giftBalDivId.setVisible(false);
					//Total Gift TopUp
					totalGiftTopupAmountLblId.setValue(Constants.NOT_APPLICABLE);
					//Gift Amount Redeemed
					giftAmountRedemedLblId.setValue(Constants.NOT_APPLICABLE);
					//Gift Amount Balance
					giftAmtBalLblId.setValue(Constants.NOT_APPLICABLE);

				}
				else if(OCConstants.LOYALTY_MEMBERSHIP_TYPE_CARD.equalsIgnoreCase(contactsLoyalty.getMembershipType())){
//					giftBalDivId.setVisible(true);
					//Total Gift TopUp
					totalGiftTopupAmountLblId.setValue(contactsLoyalty == null || contactsLoyalty.getTotalGiftAmount() == null ? userCurrencySymbol + "0.00" : Utility.getAmountInUSFormat(contactsLoyalty.getTotalGiftAmount()));
					//Gift Amount Redeemed
					giftAmountRedemedLblId.setValue(contactsLoyalty == null || contactsLoyalty.getTotalGiftRedemption() == null ? userCurrencySymbol + "0.00" : Utility.getAmountInUSFormat(contactsLoyalty.getTotalGiftRedemption()));
					//Gift Amount Balance
					giftAmtBalLblId.setValue(contactsLoyalty == null || contactsLoyalty.getGiftBalance() == null ? userCurrencySymbol + "0.00" : Utility.getAmountInUSFormat(contactsLoyalty.getGiftBalance()));
				}
				
				if(contactsLoyalty.getServiceType() == null || !OCConstants.LOYALTY_SERVICE_TYPE_OC.equalsIgnoreCase(contactsLoyalty.getServiceType()) ){
					logger.debug("contactsLoyalty.getServiceType()contactsLoyalty.getServiceType():: "+contactsLoyalty.getServiceType());
					lastRefreshedlId.setVisible(true);
					refreshLoyaltyAId.setVisible(true);
					lastRefreshedOnlId.setVisible(true);
					lastRefreshedlId.setValue(contactsLoyalty == null ? "" : MyCalendar.calendarToString(contactsLoyalty.getLastFechedDate(), MyCalendar.FORMAT_SCHEDULE));
				}
				else{
					logger.debug("contactsLoyalty.getServiceType()contactsLoyalty.getServiceType():: "+contactsLoyalty.getServiceType()+"\t:"+contactsLoyalty.getCardNumber());
					lastRefreshedlId.setVisible(false);
					refreshLoyaltyAId.setVisible(false);
					lastRefreshedOnlId.setVisible(false);
				}
			}
			else{
				logger.error("Unable to set the values as contacts loyalty object is null");
			}
			}catch(Exception e) {
				logger.error("Exception ::",e);
			}
			logger.debug("<<<<<<<<<<<<< completed displayLoyaltyOverview ");
		}//displayLoyaltyOverview
		
		/**
		 * This method display loyalty Info details
		 * @param contactsLoyalty
		 */
		private void displayLoyaltyInfo(ContactsLoyalty contactsLoyalty){
			logger.debug(">>>>>>>>>>>>> entered in displayLoyaltyInfo");
			if(contactsLoyalty != null){
				clearLtyInfoFeilds();
				//Enrolled For Loyalty
				enrolledInLtyPrgmLblId.setValue("Yes");
				//Membership Number
				ltyInfomembershipNumberAnchId.setStyle("cursor:pointer;color:black;");
			//	ltyInfomembershipNumberLblId.setValue(contactsLoyalty.getCardNumber() == null ? "":contactsLoyalty.getCardNumber()+"");
				////Membership Status
				membershipStatusLblId.setValue( contactsLoyalty.getMembershipStatus() == null ? "" : contactsLoyalty.getMembershipStatus());
				//OptIn Medium
				
				String optinMedium = contactsLoyalty.getContactLoyaltyType();//APP - 1774
				optinMedium =optinMedium.replace("_", " ");//APP-1774
				if(contactsLoyalty.getContactLoyaltyType() != null){
					if(Constants.CONTACT_LOYALTY_TYPE_POS.equalsIgnoreCase(contactsLoyalty.getContactLoyaltyType())) {
						optinMediumLblId.setValue( Constants.STRING_NILL);
						optinMediumLblId.setValue( Constants.CONTACT_LOYALTY_TYPE_STORE);
						logger.info("Optin Medium ::"+Constants.CONTACT_LOYALTY_TYPE_STORE);
					}
					else {
						optinMediumLblId.setValue( Constants.STRING_NILL);
						optinMediumLblId.setValue(optinMedium);
						logger.info("Optin Medium ::"+optinMedium);
					}
				}
				//Optin Date
				optinDateLblId.setValue(contactsLoyalty.getCreatedDate() == null ? "--" : MyCalendar.calendarToString(contactsLoyalty.getCreatedDate(), MyCalendar.FORMAT_DATETIME_STYEAR,clientTimeZone));//APP - 337
				logger.info("Optin Date :: "+MyCalendar.calendarToString(contactsLoyalty.getCreatedDate(), MyCalendar.FORMAT_DATETIME_STYEAR));
				//Store Number
				storeNumberLblId.setValue(contactsLoyalty.getPosStoreLocationId() == null ? "" : contactsLoyalty.getPosStoreLocationId());
				//Tier Name
				tierNameLblId.setValue(contactsLoyalty.getProgramTierId() != null ? getTierName(contactsLoyalty) : "");
				
				//Expiration Date
				expirationDateLblId.setValue(contactsLoyalty.getProgramTierId() != null ? getExpirationDate(contactsLoyalty) : "");

			}
			else{
				enrolledInLtyPrgmLblId.setValue("No");
				logger.error("Unable to set the Lty Info as contacts loyalty object is null");
			}
			logger.debug("<<<<<<<<<<<<< completed displayLoyaltyInfo ");
		}//displayLoyaltyInfo
		
		
		/**
		 * This method get the Tier Name
		 * @param contactsLoyalty
		 * @return Tier Name
		 */
		private String getTierName(ContactsLoyalty contactsLoyalty) {
			LoyaltyProgramService ltyPrgmService = new LoyaltyProgramService();
			LoyaltyProgram loyaltyProgram = ltyPrgmService.getProgmObj(contactsLoyalty.getProgramId());
			LoyaltyProgramTier loyaltyProgramTier = null;
			String tierName ="";
			if(loyaltyProgram != null && loyaltyProgram.getTierEnableFlag() == OCConstants.FLAG_YES && contactsLoyalty.getProgramTierId() !=null){
				loyaltyProgramTier = ltyPrgmService.getTierObj(contactsLoyalty.getProgramTierId().longValue());
			}
			if(loyaltyProgramTier != null){
				String tier = loyaltyProgramTier.getTierName() ;
				String level = " ( Level : "+(loyaltyProgramTier.getTierType() == null ? "" : loyaltyProgramTier.getTierType())+" )";
				tierName =  tier+level;
			}
			else{
				tierName = Constants.STRING_NILL;
			}
			return tierName;
		}//getTierName

		/**
		 * This method calculates expiration Date
		 * @param contactsLoyalty
		 * @return ExpirationDate
		 */
		private String getExpirationDate(ContactsLoyalty contactsLoyalty) {
			LoyaltyProgramService ltyPrgmService = new LoyaltyProgramService();
			LoyaltyProgram loyaltyProgram =null;
			LoyaltyProgramTier loyaltyProgramTier = null;
			String expirationDate="";
			if(contactsLoyalty.getProgramId() != null){
				loyaltyProgram = ltyPrgmService.getProgmObj(contactsLoyalty.getProgramId());
			}

			if(contactsLoyalty.getProgramTierId() !=null){
				loyaltyProgramTier = ltyPrgmService.getTierObj(contactsLoyalty.getProgramTierId().longValue());
			}
			
			if(contactsLoyalty != null && loyaltyProgram != null && contactsLoyalty.getRewardFlag() != null){

				if(OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L.equalsIgnoreCase(contactsLoyalty.getRewardFlag()) || OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_GL.equalsIgnoreCase(contactsLoyalty.getRewardFlag())){
					if(loyaltyProgramTier != null && loyaltyProgram.getMembershipExpiryFlag() == 'Y' && loyaltyProgramTier.getMembershipExpiryDateType() != null 
							&& loyaltyProgramTier.getMembershipExpiryDateValue() != null){
						
						boolean upgdReset = loyaltyProgram.getMbrshipExpiryOnLevelUpgdFlag() == 'Y' ? true : false;
						
						expirationDate = LoyaltyProgramHelper.getMbrshipExpiryDate(contactsLoyalty.getCreatedDate(), contactsLoyalty.getTierUpgradedDate(), 
								upgdReset, loyaltyProgramTier.getMembershipExpiryDateType(), loyaltyProgramTier.getMembershipExpiryDateValue());
					}//if
				}//if
				else if(OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_G.equalsIgnoreCase(contactsLoyalty.getRewardFlag())){

					if(loyaltyProgram.getGiftMembrshpExpiryFlag() == 'Y'){
						expirationDate =  LoyaltyProgramHelper.getGiftMbrshipExpiryDate(contactsLoyalty.getCreatedDate(), 
								loyaltyProgram.getGiftMembrshpExpiryDateType(), loyaltyProgram.getGiftMembrshpExpiryDateValue());
					}//if

				}//else if

			}//if
			else{
				expirationDate = Constants.STRING_NILL;
			}
			
			return expirationDate;
		}//getExpirationDate

			
			/**
			 * This method set's label to default value
			 */
			private void clearLtyOverviewFeilds(){
				logger.debug(">>>>>>>>>>>>> entered in clearLtyOverviewFeilds");
			//	ltyOverViewMembershipNumberLblId.setValue(Constants.STRING_NILL);
				
				balanceCurrencyLblId.setValue(Constants.STRING_NILL);
				balancePointsLblId.setValue(Constants.STRING_NILL);
				lifetimePointsLblId.setValue(Constants.STRING_NILL);
				totalCurrencyEarnedLblId.setValue(Constants.STRING_NILL);
				pointsRedemedLblId.setValue(Constants.STRING_NILL);
				currencyRedemedLblId.setValue(Constants.STRING_NILL);
				totalLoyaltyIssuanceLblId.setValue(Constants.STRING_NILL);
				totalRedemptionsLblId.setValue(Constants.STRING_NILL);
				totalGiftTopupAmountLblId.setValue(Constants.STRING_NILL);
				giftAmountRedemedLblId.setValue(Constants.STRING_NILL);
				lastRefreshedlId.setValue(Constants.STRING_NILL);
				logger.debug("<<<<<<<<<<<<< completed clearLtyOverviewFeilds ");

			}//clearFeilds
			/**
			 * This method clears Loyalty info fields
			 */
			private void clearLtyInfoFeilds(){
				logger.debug(">>>>>>>>>>>>> entered in clearLtyInfoFeilds");
				//ltyInfomembershipNumberLblId.setValue(Constants.STRING_NILL);
				enrolledInLtyPrgmLblId.setValue(Constants.STRING_NILL);
				membershipStatusLblId.setValue(Constants.STRING_NILL);
				optinMediumLblId.setValue(Constants.STRING_NILL);
				optinDateLblId.setValue(Constants.STRING_NILL);
				storeNumberLblId.setValue(Constants.STRING_NILL);
				tierNameLblId.setValue(Constants.STRING_NILL);
				expirationDateLblId.setValue(Constants.STRING_NILL);
				logger.debug("<<<<<<<<<<<<< completed clearLtyInfoFeilds ");
			}
} // class
