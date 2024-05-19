package org.mq.captiway.scheduler;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.Vector;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.CampaignReport;
import org.mq.captiway.scheduler.beans.CampaignSent;
import org.mq.captiway.scheduler.beans.Contacts;
import org.mq.captiway.scheduler.beans.ContactsLoyalty;
import org.mq.captiway.scheduler.beans.CouponCodes;
import org.mq.captiway.scheduler.beans.Coupons;
import org.mq.captiway.scheduler.beans.LoyaltyProgram;
import org.mq.captiway.scheduler.beans.LoyaltyProgramTier;
import org.mq.captiway.scheduler.beans.LoyaltySettings;
import org.mq.captiway.scheduler.beans.LoyaltyTransactionChild;
import org.mq.captiway.scheduler.beans.MailingList;
import org.mq.captiway.scheduler.beans.RetailProSalesCSV;
import org.mq.captiway.scheduler.beans.Users;
import org.mq.captiway.scheduler.beans.UsersDomains;
import org.mq.captiway.scheduler.beans.Vmta;
import org.mq.captiway.scheduler.dao.CampaignSentDao;
import org.mq.captiway.scheduler.dao.CampaignSentDaoForDML;
import org.mq.captiway.scheduler.dao.ContactsDao;
import org.mq.captiway.scheduler.dao.ContactsDaoForDML;
import org.mq.captiway.scheduler.dao.ContactsLoyaltyDao;
import org.mq.captiway.scheduler.dao.CouponCodesDao;
import org.mq.captiway.scheduler.dao.CouponCodesDaoForDML;
import org.mq.captiway.scheduler.dao.CouponsDao;
import org.mq.captiway.scheduler.dao.CouponsDaoForDML;
import org.mq.captiway.scheduler.dao.RetailProSalesDao;
import org.mq.captiway.scheduler.dao.UsersDao;
import org.mq.captiway.scheduler.dao.VmtaDao;
import org.mq.captiway.scheduler.services.ExternalSMTPSender;
import org.mq.captiway.scheduler.utility.Constants;
import org.mq.captiway.scheduler.utility.ContactPHValue;
import org.mq.captiway.scheduler.utility.CouponCodesUtility;
import org.mq.captiway.scheduler.utility.EncryptDecryptLtyMembshpPwd;
import org.mq.captiway.scheduler.utility.MyCalendar;
import org.mq.captiway.scheduler.utility.PlaceHolders;
import org.mq.captiway.scheduler.utility.PrepareFinalHTML;
import org.mq.optculture.business.helper.LoyaltyProgramHelper;
import org.mq.optculture.business.helper.LoyaltyProgramService;
import org.mq.optculture.data.dao.LoyaltyProgramDao;
import org.mq.optculture.data.dao.LoyaltyProgramTierDao;
import org.mq.optculture.data.dao.LoyaltyTransactionExpiryDao;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;


/**
 * Simple class from which the sender threads can obtain recipient email
 * addresses. In real life, this would be an interface to your data base.
 */
public class RecipientProvider {

	private static final  Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);

    private Iterator<Contacts> iter;
    /*public Iterator<Contacts> getIter() {
		return iter;
	}

	public void setIter(Iterator<Contacts> iter) {
		this.iter = iter;
	}
*/
	private int totalSizeInt;
    private int currentSizeInt = 0;
    private Long currentSentId;
    private Long lastContactId;
    private Date lastMailDate;

    private ContactsDao contactsDao;
    private ContactsDaoForDML contactsDaoForDML;
    public ContactsDaoForDML getContactsDaoForDML() {
		return contactsDaoForDML;
	}

	public void setContactsDaoForDML(ContactsDaoForDML contactsDaoForDML) {
		this.contactsDaoForDML = contactsDaoForDML;
	}

	private CampaignSentDao campaignSentDao;
    private CampaignSentDaoForDML campaignSentDaoForDML;
    private CouponCodesDao couponCodesDao;
    private CouponCodesDaoForDML couponCodesDaoForDML;
    ContactsLoyaltyDao contactsLoyaltyDao;// = (ContactsLoyaltyDao)context.getBean("contactsLoyaltyDao");
    private CouponsDao couponsDao;
    private CouponsDaoForDML couponsDaoForDML;
    private VmtaDao vmtaDao;

    private CampaignReport campaignReport;
    private Long campaignId;
    private String listIdsStr;
    private boolean isLastMailDateUpdated = false;

    private Set<String> totalPhSet=null;
    
    private  Map<String, ContactsLoyalty> contactsLoyaltyMap = null;
    private  Map<String, RetailProSalesCSV> contactsLastPurchaseMap = null;
    
    private List<CampaignSent> sentList = new ArrayList<CampaignSent>();

    private Hashtable<String, Queue<CouponCodes>> couponCodesMap = null;
    
    private Vector<CouponCodes> usedCouponCodesVec = new Vector<CouponCodes>();
    
    private ApplicationContext context;
    
    public synchronized void addCouponCodeToVec(CouponCodes cc) {
    	usedCouponCodesVec.add(cc);
    } //
    
    //TODO need to Check if it is required
	public RecipientProvider(ApplicationContext context, CampaignReport theCampaignReport,
    		String theListIdsStr, int theTotalSizeInt, Long campId) {
		
		this(context, theCampaignReport, theListIdsStr, theTotalSizeInt, campId,null);
	}

	public RecipientProvider(ApplicationContext context, CampaignReport theCampaignReport,
    		String theListIdsStr, int theTotalSizeInt, Long campId, Set<String> totalPhSet) {

		//logger.debug("RP Created------------ 2 ------------");
		this.context = context; 

    	campaignReport = theCampaignReport;
    	campaignId = campId;
    	listIdsStr = theListIdsStr;
    	totalSizeInt = theTotalSizeInt;
    	currentSizeInt = 0;
    	this.totalPhSet=totalPhSet;
    	
    	lastMailDate = new Date();

    	contactsDao = (ContactsDao)context.getBean("contactsDao");
    	contactsDaoForDML = (ContactsDaoForDML)context.getBean("contactsDaoForDML");
    	campaignSentDao = (CampaignSentDao)context.getBean("campaignSentDao");
        campaignSentDaoForDML = (CampaignSentDaoForDML)context.getBean("campaignSentDaoForDML");
    	vmtaDao = (VmtaDao)context.getBean("vmtaDao");
    	couponCodesDao = (CouponCodesDao)context.getBean("couponCodesDao");
    	couponCodesDaoForDML = (CouponCodesDaoForDML)context.getBean("couponCodesDaoForDML");
    	couponsDao = (CouponsDao)context.getBean("couponsDao");
    	couponsDaoForDML = (CouponsDaoForDML)context.getBean("couponsDaoForDML");
    	contactsLoyaltyDao = (ContactsLoyaltyDao)context.getBean("contactsLoyaltyDao");
//    	Long userId = campaignReport.getUser().getUserId();

//    	contactsDao.deleteTempContacts();
//    	contactsDao.createTempContacts(qryStr, userId);

    	campaignReport.setSent(totalSizeInt);
        currentSentId = campaignSentDao.getCurrentSentId();


        if(logger.isDebugEnabled()) {
        	logger.info(" >>>>>>>>> List Ids :"+listIdsStr);
        	logger.info(" Total Contacts to send :"+totalSizeInt);
        	logger.info(" Current SendId :"+currentSentId);
        }

    }

    public RecipientProvider(ApplicationContext context, MailingList mailingList,
    		int theTotalSizeInt) {

    	//logger.debug("RP Created------------ 1 ------------");
    	contactsDao = (ContactsDao)context.getBean("contactsDao");
    	contactsDaoForDML = (ContactsDaoForDML)context.getBean("contactsDaoForDML");
    	campaignSentDao = (CampaignSentDao)context.getBean("campaignSentDao");
        campaignSentDaoForDML = (CampaignSentDaoForDML)context.getBean("campaignSentDaoForDML");
    	vmtaDao = (VmtaDao)context.getBean("vmtaDao");
    	contactsLoyaltyDao = (ContactsLoyaltyDao)context.getBean("contactsLoyaltyDao");
    	this.listIdsStr = mailingList.getListId().toString();
    	totalSizeInt = theTotalSizeInt;

//    	totalSizeInt = contactsDao.getEmailCount(listIdsStr, Constants.CONT_STATUS_OPTIN_PENDING);
    	lastMailDate = new Date();

//    	String qryStr =
//    		" INSERT IGNORE INTO tempcontacts (SELECT * FROM contacts WHERE list_id IN ("+listIdsStr+")" +
//    		" AND email_status LIKE '" + Constants.CONT_STATUS_OPTIN_PENDING +"' AND (optin IS null OR optin<3) " +
//    		" AND (last_mail_date is null OR (DATEDIFF(now(), last_mail_date) >7)) ORDER BY cid)";
//
//    	contactsDao.deleteTempContacts();
//    	contactsDao.createTempContacts(qryStr, mailingList.getUsers().getUserId());
    }
    
    public RecipientProvider(ApplicationContext context, MailingList mailingList,
    		int theTotalSizeInt, Set<String> totalPhSet) {

    	//logger.debug("RP Created------------ 1 ------------");
    	contactsDao = (ContactsDao)context.getBean("contactsDao");
    	contactsDaoForDML = (ContactsDaoForDML)context.getBean("contactsDaoForDML");
    	campaignSentDao = (CampaignSentDao)context.getBean("campaignSentDao");
    	campaignSentDaoForDML = (CampaignSentDaoForDML)context.getBean("campaignSentDaoForDML");
    	vmtaDao = (VmtaDao)context.getBean("vmtaDao");
    	contactsLoyaltyDao = (ContactsLoyaltyDao)context.getBean("contactsLoyaltyDao");
    	this.listIdsStr = mailingList.getListId().toString();
    	totalSizeInt = theTotalSizeInt;

//    	totalSizeInt = contactsDao.getEmailCount(listIdsStr, Constants.CONT_STATUS_OPTIN_PENDING);
    	lastMailDate = new Date();
    	this.totalPhSet = totalPhSet;
    	this.context = context;

//    	String qryStr =
//    		" INSERT IGNORE INTO tempcontacts (SELECT * FROM contacts WHERE list_id IN ("+listIdsStr+")" +
//    		" AND email_status LIKE '" + Constants.CONT_STATUS_OPTIN_PENDING +"' AND (optin IS null OR optin<3) " +
//    		" AND (last_mail_date is null OR (DATEDIFF(now(), last_mail_date) >7)) ORDER BY cid)";
//
//    	contactsDao.deleteTempContacts();
//    	contactsDao.createTempContacts(qryStr, mailingList.getUsers().getUserId());
    }
    
    Contacts tempContact;
    long currentContId = 0l;
    String userAddress = null;

    public synchronized Contacts getNext() {
    	
    	//logger.info("<< getNext() just entered : "+ Thread.currentThread().getName());

        if (this.iter != null && this.iter.hasNext()) {
        	tempContact = iter.next();
        	
        	
            return tempContact;
        }

        if(totalSizeInt ==0 ) {
        	if(logger.isWarnEnabled()) logger.warn("********** Found 0 Active contacts to send the mail");
        	return null;
        }
    		logger.info(" Getting the 10000 contact objects from Contacts Table from :"+
    				currentSizeInt+"-"+(currentSizeInt+10000));
    		
    	List<Contacts> contacts = null;
    	contacts = contactsDao.getSegmentedContacts("SELECT * FROM tempcontacts ",
    												currentSizeInt, 10000);
    	
    	
    	//added for substitution tags
    	StringBuffer cidsStr = new StringBuffer();//.STRING_NILL;
    	
    	
    	if(contacts != null && contacts.size() > 0 && totalPhSet != null && totalPhSet.size() > 0) {//only foe each 10,000
    		//logger.info("got list of contacts "+contacts.size());
    		
    		if(contactsLoyaltyMap != null) 		contactsLoyaltyMap.clear();//check for null
    		if(contactsLastPurchaseMap != null) contactsLastPurchaseMap.clear();
    		
    		boolean isMetLoyalty = false; 
    		boolean isMetLastPurChase = false;
    		for (String cfStr : totalPhSet) {
    			
    			if( (contactsLoyaltyMap != null &&  contactsLastPurchaseMap != null) && 
    					(contactsLoyaltyMap.size() > 0) && contactsLastPurchaseMap.size() > 0 ) break;
    			
    			cfStr = cfStr.substring(4);
    			if( !cfStr.toLowerCase().startsWith(PlaceHolders.CAMPAIGN_PH_STARTSWITH_LOYALTY.toLowerCase()) && 
    					!cfStr.toLowerCase().startsWith(PlaceHolders.CAMPAIGN_PH_STARTSWITH_LASETPURCHASE.toLowerCase()) && 
    					!cfStr.toLowerCase().startsWith(PlaceHolders.CAMPAIGN_ADDRESS_PH_STARTSWITH_LASETPURCHASE.toLowerCase()) ) continue;
    			
    			
    			if(!isMetLoyalty && !isMetLastPurChase) {
    				
    				for (Contacts contact : contacts) {
    					
    		    		if(cidsStr.length() > 0) cidsStr.append(Constants.COMMA);
    		    		
    		    		cidsStr.append(contact.getContactId().longValue());
    		    		
    				}//for
    			}
    			
    			if( !isMetLoyalty && cfStr.toLowerCase().startsWith(PlaceHolders.CAMPAIGN_PH_STARTSWITH_LOYALTY) ) { //only loyalty placeholders
    				
    				isMetLoyalty = true;
    			
    				ContactsLoyaltyDao contactsLoyaltyDao = (ContactsLoyaltyDao)context.getBean("contactsLoyaltyDao");
    				contactsLoyaltyMap = new HashMap<String, ContactsLoyalty>();
    				
    				List<ContactsLoyalty> retList = contactsLoyaltyDao.getLoyaltyForPlaceHolderVal(cidsStr.toString());
    				
    				if(retList == null || retList.size() == 0 ) continue;
    				
    				for (ContactsLoyalty contactsLoyalty : retList) {
						
    					contactsLoyaltyMap.put(contactsLoyalty.getContact().getContactId().longValue()+Constants.STRING_NILL, contactsLoyalty);
    					
					}
     				
    				
    			}//if
    			else if( !isMetLastPurChase && (cfStr.toLowerCase().startsWith(PlaceHolders.CAMPAIGN_PH_STARTSWITH_LASETPURCHASE.toLowerCase()) || 
    					cfStr.toLowerCase().startsWith(PlaceHolders.CAMPAIGN_ADDRESS_PH_STARTSWITH_LASETPURCHASE.toLowerCase()) ) ) { //only last purchase date
    				
    				isMetLastPurChase = true;
    				
    				RetailProSalesDao retailProSalesDao = (RetailProSalesDao)context.getBean("retailProSalesDao");
    				contactsLastPurchaseMap = new HashMap<String, RetailProSalesCSV>();
    				
    				List<RetailProSalesCSV> retList = retailProSalesDao.findLastPurCheseDatePlaceHolderVal(cidsStr.toString());
    				
    				if(retList == null || retList.size() == 0 ) continue;
    				
    				for (RetailProSalesCSV retailProSalesCSV : retList) {
						
    					contactsLastPurchaseMap.put(retailProSalesCSV.getCid().longValue()+Constants.STRING_NILL, retailProSalesCSV);
    					
					}
    				
    			}//else
    			
    		}//for each placeholder
    		
    		/*logger.debug("loyaltymap size "+contactsLoyaltyMap.size());
    		logger.debug("purchase size "+contactsLastPurchaseMap.size());
    		*/
    		
    		
    	}//if placeholders
    	
    

    	/**
    	 *
    	 * If CampaignReport is null that means double optin mails submission
    	 * else Campaign submission.
    	 *
    	 */

		if(contacts == null || contacts.size() == 0) {

			/**
			 * If lastContactId not null then updates the 'lastMailDate'
			 * for the contacts to which mails were sent
			 */
			if(lastContactId != null && !isLastMailDateUpdated) {

				DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String updateQryStr = null;
				if(campaignReport != null) { // campaign submission
					/*	updateQryStr =
						" UPDATE contacts SET last_mail_date='"+ format.format(lastMailDate) + "'" +
					    " WHERE list_id IN ("+listIdsStr+") AND cid<="+lastContactId;
					 */

					/*
					 * above query has been modified to accomodate both campaign schedule and trigger scenarios
					 * this is to update the last_mail_date of contacts to whom campaign/trigger is sent
					 */

					
					updateQryStr ="update contacts c, tempcontacts s  set c.last_mail_date ='"+format.format(lastMailDate)+"' where c.cid=s.cid"; 
					
					
					
					
					/*updateQryStr =
						" UPDATE contacts SET last_mail_date='"+ format.format(lastMailDate) + "'" +
						" WHERE cid IN ( SELECT cid from tempcontacts) ";*/

				}
				else { // Double optin mail submission
					
					
					
					updateQryStr ="update contacts c, tempcontacts s  set c.last_mail_date ='"+format.format(lastMailDate)+"', c.optin=c.optin+1 where c.cid=s.cid"; 
					
					/*updateQryStr =
							" UPDATE contacts SET last_mail_date='"+ format.format(lastMailDate) + "', " +
							" optin=optin+1 WHERE cid IN (SELECT cid from tempcontacts) ";*/
					
						//	" UPDATE contacts SET last_mail_date='"+ format.format(lastMailDate) + "', " +
						//	" optin=optin+1 WHERE list_id IN ("+listIdsStr+") AND cid<="+lastContactId;
				}
				
				
				long startTime = System.currentTimeMillis();
				contactsDaoForDML.executeUpdateQuery(updateQryStr);
				logger.info("Elapsed time for this db tuning, for query :=="+updateQryStr+"  is ::: "+(System.currentTimeMillis() - startTime)+" Millisecond");
				isLastMailDateUpdated = true;
			}
			return null;
		}
		/**
		 * Last Contact Id in the list for reference to
		 * update the last mail date of contacts table till this contact
		 */
		lastContactId = contacts.get(contacts.size()-1).getContactId();

		currentContId = contacts.get(contacts.size()-1).getContactId();

		if(logger.isDebugEnabled()) {
			logger.info(">>>>>Got the contacts from DB: "+contacts.size());
		}

		currentSizeInt += 10000;

		this.iter = contacts.listIterator();

		if(campaignReport != null) {

			// Generates the sentId for reference to CampaignSent object
			// and stores in the Contact's temp variable
			while(this.iter != null && this.iter.hasNext()) {

				tempContact = iter.next();
				++currentSentId;

				CampaignSent campaignSent = new CampaignSent(currentSentId,tempContact.getEmailId(),
						campaignReport, Constants.CS_STATUS_SUCCESS, tempContact.getContactId(),campaignId);
				
				tempContact.setTempObj(campaignSent);
				
				
				sentList.add(campaignSent);
			}
			if(logger.isDebugEnabled()) {
				logger.info(">>>>>Total Campaign Sent objects to save :"+sentList.size());
			}

			/**
			 *    Saves the CampaignSent objects into the campaign_sent table
			 *    and update the last mail date for the contact to which campaign is sent.
			 */
			try {

				//campaignSentDao.saveByCollection(sentList);
				campaignSentDaoForDML.saveByCollection(sentList);
				sentList.clear();

			}
			catch (Exception e) {
				logger.error("Exception ::::" , e);
			}
		}

		this.iter = contacts.listIterator();
		tempContact = iter.next();

        return tempContact;

    } // getNext()

    
	public String getlastPurchaseDate(String contactId, String defVal) {
		
		try {
			//logger.info("getlastPurchaseDate for contact ::"+contactId);
			String date = Constants.STRING_NOTAVAILABLE;
			RetailProSalesCSV retcsv = contactsLastPurchaseMap.get(contactId);
			if(retcsv == null ) {
			//	logger.info("sale getlastPurchaseDate is null for cid ::"+contactId);
				
				return defVal;
			}
			
			Calendar Mycalender = retcsv.getSalesDate();
			
			if(Mycalender != null) {
				
					date = MyCalendar.calendarToString(Mycalender, MyCalendar.FORMAT_DATEONLY_GENERAL);
			}
			
			if(date == null ) return defVal;
			return date;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("exception while giving the ph value ",e);
			return defVal;
		}
		
	}

	public String getlastPurchaseStore(String contactId, boolean isAddressFlag, String defVal) {
		try {
			//logger.info("getlastPurchaseStore for contact ::"+contactId);
			
			if(isAddressFlag && userAddress == null) {
				
				Users user = campaignReport.getUser();
				UsersDao usersDao = (UsersDao)context.getBean("usersDao");
				List<UsersDomains> domainsList = usersDao.getAllDomainsByUser(user.getUserId());
				String userDomainStr = "";
				Set<UsersDomains> domainSet = new HashSet<UsersDomains>();//currentUser.getUserDomains();
				if(domainsList != null) {
					domainSet.addAll(domainsList);
					for (UsersDomains usersDomains : domainSet) {
						
						if(userDomainStr.length()>0) userDomainStr+=",";
						userDomainStr += usersDomains.getDomainName();
						
					}
				}
				String footerAddr[] = PrepareFinalHTML.getOrgAndSenderAddress(user, true, userDomainStr);
				
				userAddress = footerAddr[1];
				
				
			}
			String store = Constants.STRING_NILL;
			RetailProSalesCSV retcsv = contactsLastPurchaseMap.get(contactId);
			
			
			if(retcsv == null) {
				//logger.info("sale getlastPurchaseStore is null for cid ::"+contactId);
				if(isAddressFlag && defVal == null) return userAddress;
				else return defVal;
			}
			store = retcsv.getStoreNumber();
			
			if(store == null ) {
				
				if(isAddressFlag && defVal == null ) return userAddress;
				else return defVal;
				
				
			}
			
			String storeAddr = ExternalSMTPSender.getStoreAddress(store, isAddressFlag);
			
			if(storeAddr == null || storeAddr.trim().isEmpty()) {
				
				if(isAddressFlag && defVal == null ) return userAddress;
				else return defVal;
			}
			
			return storeAddr;
		} catch (BeansException e) {
			// TODO Auto-generated catch block
			logger.error("exception while giving the ph value ",e);
			if(isAddressFlag && defVal == null) return userAddress;
			
			return defVal;
		}catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("exception while giving the ph value ",e);
			if(isAddressFlag && defVal == null) return userAddress;
			return defVal;
		}
		
	}
	
	public String getlastPurchaseDetails(String contactId, String cfStr, ContactPHValue contactPHValue, String defVal) {
		try{
			
			if(cfStr.toLowerCase().endsWith(PlaceHolders.CAMPAIGN_PH_LASTPURCHASE_DATE.toLowerCase())){
				
				return getlastPurchaseDate(contactId, defVal); 
			}
			else if(cfStr.toLowerCase().endsWith(PlaceHolders.CAMPAIGN_PH_LASTPURCHASE_STOREADDRESS.toLowerCase())) {
				
				return getlastPurchaseStore(contactId, false, defVal);
			}
			else{
				String storeDetails = Constants.STRING_NILL;
				RetailProSalesCSV retcsv = contactsLastPurchaseMap.get(contactId);
				
				
				if(retcsv == null) {
					
					 return defVal;
				}
								
				if(cfStr.toLowerCase().endsWith(PlaceHolders.CAMPAIGN_PH_LASTPURCHASE_AMOUNT.toLowerCase())){
					
					Double lastPuechaseAmount = retcsv.getSalesPrice();
					if(lastPuechaseAmount != null)
					{
						return lastPuechaseAmount.toString();
					}else {
						return defVal;
					}
									
				}
				storeDetails = retcsv.getStoreNumber();
				
				if(storeDetails == null ) {
					
				 return defVal;
					
				}
				
				String storeVal = ExternalSMTPSender.getStorePlaceholder(storeDetails, cfStr, contactPHValue, defVal);
				
				if(storeVal == null || storeVal.trim().isEmpty()) {
					
					 return defVal;
				}
				
				return storeVal;
			}//store details
		}
		catch (BeansException e) {
			// TODO Auto-generated catch block
			logger.error("exception while giving the ph value ",e);
			
			return defVal;
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("exception while giving the ph value ",e);
			return defVal;
		}
	}
	
	
	
	
	
    public String getLoyaltyPlaceHolderVal(String contactId, String placeholder, ContactPHValue contactPHValue, String defVal) {
    	
    	try {
    		
			ContactsLoyalty contactsLoyalty = contactsLoyaltyMap.get(contactId);
			String loyaltyPlaceholder = defVal;
			
					/*if(placeholder.equals(PlaceHolders.CAMPAIGN_PH_LOYALTY_POINTS_BALANCE)) {
						if(contactsLoyalty != null ) loyaltyPlaceholder = contactsLoyalty.getLoyaltyBalance() != null ? contactsLoyalty.getLoyaltyBalance()+"" :defVal ;
						
						contactPHValue.setLoyaltyPointsBalance(loyaltyPlaceholder);
						
					}	
					else if(placeholder.equals(PlaceHolders.CAMPAIGN_PH_GIFTCARD_BALANCE)){
						if(contactsLoyalty != null ) loyaltyPlaceholder = contactsLoyalty.getGiftcardBalance() != null ?contactsLoyalty.getGiftcardBalance()+"":defVal;
						
						contactPHValue.setLoyaltygiftcardBalance(loyaltyPlaceholder);
						
					}
					else if(placeholder.equals(PlaceHolders.CAMPAIGN_PH_LOYALTY_CARDNUMBER)){
						
						if(contactsLoyalty != null ) loyaltyPlaceholder = contactsLoyalty.getCardNumber() != null ? contactsLoyalty.getCardNumber()+"":defVal;
						
						contactPHValue.setLoyaltyCardNumber(loyaltyPlaceholder);
						
					}
					else if(placeholder.equals(PlaceHolders.CAMPAIGN_PH_LOYALTYCARDPIN)){
						if(contactsLoyalty != null ) loyaltyPlaceholder = contactsLoyalty.getCardPin() !=null ? contactsLoyalty.getCardPin():defVal;
						
						contactPHValue.setLoyaltyCardPin(loyaltyPlaceholder);
						
					}
					else if(placeholder.equals(PlaceHolders.CAMPAIGN_PH_LOYALTY_REFRESHEDON)){
						if(contactsLoyalty != null ) {
							if(contactsLoyalty.getLastFechedDate() ==  null) {
								loyaltyPlaceholder=defVal;
							}
							else {
								
								loyaltyPlaceholder = MyCalendar.calendarToString(contactsLoyalty.getLastFechedDate(), MyCalendar.FORMAT_DATEONLY_GENERAL);
							}
						}
						contactPHValue.setLoyaltyRefreshedOn(loyaltyPlaceholder);
					}*/
			if(contactsLoyalty != null  && OCConstants.LOYALTY_MEMBERSHIP_STATUS_CLOSED.equalsIgnoreCase(contactsLoyalty.getMembershipStatus()) && contactsLoyalty.getTransferedTo() != null){
				
				contactsLoyalty = contactsLoyaltyDao.findAllByLoyaltyId(contactsLoyalty.getTransferedTo());
			}
			loyaltyPlaceholder = replaceLoyaltyPlaceHolders(placeholder,contactsLoyalty,defVal);
			logger.info("**************************** loyaltyPlaceholder Id ************* "+loyaltyPlaceholder);
			 if(loyaltyPlaceholder != null) return loyaltyPlaceholder;
			else return defVal;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("exception while giving the ph value ",e);
			return defVal;
		}
    	
    	
    }
   
    
    /**
     * This method replace loyalty place holders
     * @param placeholder
     * @param contactsLoyalty
     * @param defVal
     * @return Loyalty Place Holder
     */
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
    	//OC LOYALTYMEMBERSHIPPIN
    	if(PlaceHolders.CAMPAIGN_PH_LOYALTY_MEMBERSHIP_PIN.equalsIgnoreCase(placeholder)){
    			loyaltyPlaceholder = contactsLoyalty.getCardPin()!= null ? contactsLoyalty.getCardPin(): defVal;
    			logger.info("Membership Number ::"+contactsLoyalty.getCardPin());
    	}
    	//SB LOYALTYCARDPIN
    	else if(PlaceHolders.CAMPAIGN_PH_LOYALTYCARDPIN.equalsIgnoreCase(placeholder)){
			loyaltyPlaceholder = contactsLoyalty.getCardPin()!= null ? contactsLoyalty.getCardPin(): defVal;
			logger.info("Membership Number ::"+contactsLoyalty.getCardPin());
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
    	//SB CARDNUMBER
    	else if(PlaceHolders.CAMPAIGN_PH_LOYALTY_CARDNUMBER.equalsIgnoreCase(placeholder)){
    		loyaltyPlaceholder = contactsLoyalty.getCardNumber() != null ? contactsLoyalty.getCardNumber()+"":defVal;
    		logger.info("Membership Number ::"+contactsLoyalty.getCardNumber());
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
    		loyaltyPlaceholder = contactsLoyalty.getLoyaltyBalance() != null ? contactsLoyalty.getLoyaltyBalance().longValue()+" Points" : defVal;
    		logger.info("LOYALTY_POINTS_BALANCE ::"+loyaltyPlaceholder);
    	}
    	//OC LOYALTY_MEMBERSHIP_CURRENCY_BALANCE
    	else if(PlaceHolders.CAMPAIGN_PH_LOYALTY_MEMBERSHIP_CURRENCY_BALANCE.equalsIgnoreCase(placeholder)){
    		loyaltyPlaceholder = contactsLoyalty.getGiftcardBalance() != null ?  decimalFormat.format(contactsLoyalty.getGiftcardBalance()) : defVal;
    		logger.info("LOYALTY_CURRENCY_BALANCE ::"+loyaltyPlaceholder);
    	}
    	//SB GIFTCARD_BALANCE
    	else if(PlaceHolders.CAMPAIGN_PH_GIFTCARD_BALANCE.equalsIgnoreCase(placeholder)){
    		loyaltyPlaceholder = contactsLoyalty.getGiftcardBalance() != null ?  decimalFormat.format(contactsLoyalty.getGiftcardBalance()) : defVal;
    		logger.info("LOYALTY_CURRENCY_BALANCE ::"+loyaltyPlaceholder);
    	}
       	//LOYALTY_GIFT_BALANCE
    	else if(PlaceHolders.CAMPAIGN_PH_LOYALTY_GIFT_BALANCE.equalsIgnoreCase(placeholder)){
    		loyaltyPlaceholder = contactsLoyalty.getGiftBalance() != null ? decimalFormat.format(contactsLoyalty.getGiftBalance()) : defVal;
    		logger.info("LOYALTY_GIFT_BALANCE ::"+loyaltyPlaceholder);
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

    		loyaltyPlaceholder = contactsLoyalty.getCardNumber() != null ? getLastEarnedValue(contactsLoyalty.getLoyaltyId(),OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE,defVal) : defVal;
    		logger.info("LOYALTY_LAST_EARNED_VALUE::"+loyaltyPlaceholder);
    	}
    	//LOYALTY_LAST_REDEEMED_VALUE
    	else if(PlaceHolders.CAMPAIGN_PH_LOYALTY_LAST_REDEEMED_VALUE.equalsIgnoreCase(placeholder)){
    		loyaltyPlaceholder = contactsLoyalty.getCardNumber() != null ? getLastRedeemedValue(contactsLoyalty.getLoyaltyId(),OCConstants.LOYALTY_TRANS_TYPE_REDEMPTION,defVal) : defVal;
    		logger.info("LOYALTY_LAST_REDEEMED_VALUE::"+loyaltyPlaceholder);
    	}
    	else if(PlaceHolders.CAMPAIGN_PH_LOYALTY_MEMBERSHIP_PASSWORD.equalsIgnoreCase(placeholder)){
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
    	} */
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
    		loyaltyPlaceholder = contactsLoyalty.getCardNumber() != null ? getLastBonusValue(contactsLoyalty.getLoyaltyId(),OCConstants.LOYALTY_TRANS_TYPE_BONUS,defVal) : defVal;
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
		return expiryDao.fetchExpiryValues(loyaltyId, expDate, rewardFlag);
	}//fetchExpiryValues

	/**
	 * This is to show last bonus value earned
	 * @param cardNumber
	 * @param transactionType
	 * @param defVal
	 * @return
	 */
	private String getLastBonusValue(Long loyaltyId,String transactionType, String defVal) {
		logger.info("--Start of getLastBonusValue--");
		String loyaltyPlaceholder = defVal;
		DecimalFormat decimalFormat = new DecimalFormat("#0.00");
		LoyaltyProgramService ltyPrgmService =  new LoyaltyProgramService();
		LoyaltyTransactionChild loyaltyTransactionChild = null;
		loyaltyTransactionChild = ltyPrgmService.getTransByMembershipNoAndTransType(loyaltyId, transactionType);
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
		}
		logger.info("--Exit of getLastBonusValue--");
		return loyaltyPlaceholder;
	}//getLastBonusValue
    
    
    /**
	 * This method return's the decrypt password
	 * @param contactsLoyalty
	 * @param defVal
	 * @return password
	 */
	private String getMembershipPassword(ContactsLoyalty contactsLoyalty,String defVal) {
		logger.debug(">>>>>>>>>>>>> entered in getMembershipPassword");
		String password = defVal;
		try {
			if(!contactsLoyalty.getRewardFlag().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_G)) {
				password = contactsLoyalty.getMembershipPwd() != null ? EncryptDecryptLtyMembshpPwd.decrypt( contactsLoyalty.getMembershipPwd()) : defVal;
			}
		} catch (Exception e) {
			logger.error("Expection while replacing place holder :: ",e);
		}
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
	 * @param contactsLoyalty 
	 * @param defVal 
	 * @return userOrganization
	 */
	/*private String getUserOrganization(ContactsLoyalty contactsLoyalty, String defVal) {
		logger.debug(">>>>>>>>>>>>> entered in getUserOrganization");
		String userOrganizationName = defVal;
		LoyaltyProgramService loyaltyProgramService = new LoyaltyProgramService();

		userOrganizationName = loyaltyProgramService.findUserOrgNameByUserId(contactsLoyalty.getUserId(),defVal);

		logger.debug("<<<<<<<<<<<<< completed getUserOrganization ");
		return userOrganizationName;
	}//getUserOrganization
	*/

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
     * This method fetch Hold Bal & points
     * @param contactsLoyalty
     * @param defVal
     * @return Hold Balance or Points
     */

    private String getHoldBalance(ContactsLoyalty contactsLoyalty,String defVal) {
    	DecimalFormat decimalFormat = new DecimalFormat("#0.00");
    	String loyaltyPlaceholder ="";
    	if(contactsLoyalty.getHoldAmountBalance() != null && contactsLoyalty.getHoldPointsBalance() != null){
    		loyaltyPlaceholder = decimalFormat.format(contactsLoyalty.getHoldAmountBalance()) +" & "+contactsLoyalty.getHoldPointsBalance().intValue()+ " Points";;
    	}
    	else if(contactsLoyalty.getHoldAmountBalance() != null && contactsLoyalty.getHoldPointsBalance() == null){
    		loyaltyPlaceholder = decimalFormat.format(contactsLoyalty.getHoldAmountBalance());// +" & "+contactsLoyalty.getHoldPointsBalance().intValue();
    	}
    	else if(contactsLoyalty.getHoldAmountBalance() == null && contactsLoyalty.getHoldPointsBalance() != null){
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
    		loyaltyPlaceholder = loyaltyProgramTier.getPtsActiveDateValue() + " "+loyaltyProgramTier.getPtsActiveDateType()+OCConstants.MORETHANONEOCCURENCE;
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
    private String getLastEarnedValue(Long loyaltyId,String loyaltyTransTypeIssuance,String defValue) {
    	String loyaltyPlaceholder = "";
    	DecimalFormat decimalFormat = new DecimalFormat("#0.00");
    	//helper class obj
    	LoyaltyProgramService ltyPrgmService =  new LoyaltyProgramService();
    	LoyaltyTransactionChild child = null;
    	child = ltyPrgmService.getTransByMembershipNoAndTransType(loyaltyId, loyaltyTransTypeIssuance);
    	if(child != null){
    		if(child.getEarnedAmount() != null && child.getEarnedPoints() != null){
    			loyaltyPlaceholder = decimalFormat.format(child.getEarnedAmount())+" & "+child.getEarnedPoints().intValue()+" Points";
    		}
    		else if(child.getEarnedAmount() != null && child.getEarnedPoints() == null){
    			loyaltyPlaceholder = decimalFormat.format(child.getEarnedAmount());
    		}
    		else if(child.getEarnedAmount() == null && child.getEarnedPoints() != null){
    			loyaltyPlaceholder = child.getEarnedPoints().intValue()+" Points";
    		}
    		else{
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
    private String getLastRedeemedValue(Long loyaltyId,String loyaltyTransTypeRedemption,String defValue) {
    	String loyaltyPlaceholder = "";
    	DecimalFormat decimalFormat = new DecimalFormat("#0.00");
    	//helper class obj
    	LoyaltyProgramService ltyPrgmService =  new LoyaltyProgramService();
    	LoyaltyTransactionChild child = null;
    	child = ltyPrgmService.getTransByMembershipNoAndTransType(loyaltyId, loyaltyTransTypeRedemption);
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


    private Map<String, List<String>> vmtaMap = new HashMap<String, List<String>>();
    private int currVmtaToSelect=0;
    
    public synchronized String getVmtaList(String vmta) {
    	
    	String returnVmta=null;
    	//if(logger.isDebugEnabled()) logger.debug("=============called with "+vmta+"=============");
    	if(logger.isDebugEnabled()) logger.debug("=============called with "+vmta+"=============");
    	
    	if(!vmtaMap.containsKey(vmta)) {

    		List<String> tempList = new ArrayList<String>();
    		
    		// Populate the VMTA list from DB
    		
    		Vmta myVmta =  vmtaDao.findByVmtaName(vmta);
    		
    		if(myVmta.getPooledVmtas() !=null) {
    		
    			List<Vmta> vmtaList = vmtaDao.getVmtasByPooledVmtas(myVmta.getPooledVmtas());
    			
    			for (Vmta tempVmta : vmtaList) {
					tempList.add(tempVmta.getVmtaName());
				}
    		}
    		else {
    			tempList.add(myVmta.getVmtaName());
    		}
    		    		
    		vmtaMap.put(vmta, tempList);
    		
    	} // if
    	
    	
		List<String> tempList = vmtaMap.get(vmta);
		
		if(currVmtaToSelect >= tempList.size()) currVmtaToSelect=0;
		returnVmta = tempList.get(currVmtaToSelect);
    	
		if(logger.isDebugEnabled()) logger.debug("===========returned with "+returnVmta+" :: CurrVMTAtoSelect ==="+currVmtaToSelect);
		
    	currVmtaToSelect++;

		return returnVmta;
    } //
    
    public synchronized void flushCouponCodesToDB(boolean flushFlag) {
    	
    	if(usedCouponCodesVec==null || usedCouponCodesVec.isEmpty()) return;
    	
		if(flushFlag==true || usedCouponCodesVec.size()>100) {
			//couponCodesDao.saveByCollection(usedCouponCodesVec);
			couponCodesDaoForDML.saveByCollection(usedCouponCodesVec);
			usedCouponCodesVec.clear();
		} // if
    }

    
    
	public synchronized CouponCodes  getCouponCode(String tempStr) {
		try {
			
			if(couponCodesMap==null) couponCodesMap = new Hashtable<String, Queue<CouponCodes>>();
			
			Queue<CouponCodes> phCouponCodeQue = couponCodesMap.get(tempStr);
			
			if(phCouponCodeQue==null || phCouponCodeQue.size() == 0) {
				//fillCouponCodes();
				
				flushCouponCodesToDB(true);
				
				try {
					for (String eachPh : totalPhSet) {
						
						if(eachPh.startsWith("CC_")) {
						
							if(logger.isDebugEnabled()) logger.debug("Filling CouponCodes ::"+eachPh);
							try {
								
								Queue<CouponCodes> tempQue = null;
								tempQue = couponCodesMap.get(eachPh);
								
								if(tempQue!=null && !tempQue.isEmpty()) {
									continue; // skip if it is not Empty
								}
								
								String[] strArr = eachPh.split("_");
								
								if(logger.isDebugEnabled()) logger.debug("Filling Coupons with Id = "+strArr[1]);
								//TODO need to handle this exception
								
								List<CouponCodes> couponCodesList = couponCodesDao.getInventoryCouponCodesByCouponId(Long.parseLong(strArr[1].trim()));
								if(couponCodesList!=null && couponCodesList.size() > 0) {
									if(logger.isDebugEnabled()) logger.debug("=============== couponCodesList=="+couponCodesList.size());
								}
								else {
									if(logger.isDebugEnabled()) logger.debug("=============== No CouponCodes found for the Coupons Id=="+strArr[1]);
									
									// TODO handle single type of Coupons
									Coupons coupon = couponsDao.findById(Long.parseLong(strArr[1].trim()));
									if(coupon==null || coupon.getAutoIncrCheck()==false) {
										continue;
									}
									else if(coupon!=null && 
											coupon.getCouponGeneratedType().equals(Constants.COUP_GENT_TYPE_MULTIPLE) &&
											coupon.getAutoIncrCheck()==true) { // CC Multi Type Auto 
										
										Long cqty = coupon.getTotalQty();
										if(cqty==null) cqty = new Long(0);
										
										coupon.setTotalQty(new Long(cqty.longValue()+1000));
										//couponsDao.saveOrUpdate(coupon);
										couponsDaoForDML.saveOrUpdate(coupon);
										
										CouponCodesUtility ccu = new CouponCodesUtility(context, coupon);
										ccu.start();
										if(logger.isDebugEnabled()) logger.debug("Waiting to create the 1000  Promo-codes...");
										ccu.join();
										if(logger.isDebugEnabled()) logger.debug("Created 1000  Promo-codes.");
										
										couponCodesList = couponCodesDao.getInventoryCouponCodesByCouponId(coupon.getCouponId());
										
									}
									else if(coupon!=null && coupon.getAutoIncrCheck()==true) { // CC Single Type Auto 
										couponCodesList = new ArrayList<CouponCodes>();
										CouponCodes tempCC = null;
										
										for (int i = 0; i < 1000; i++) {
											tempCC = new CouponCodes();
											tempCC.setOrgId(coupon.getOrgId());
											tempCC.setCouponId(coupon);
											tempCC.setCouponCode(coupon.getCouponCode()); // Set the Coupon code from Coupon
											couponCodesList.add(tempCC);
										} // for i
									} // else
									
								} // else
								
								if(couponCodesList!=null && !couponCodesList.isEmpty()) {
									
									tempQue = new PriorityQueue<CouponCodes>();
									tempQue.addAll(couponCodesList);
									
									couponCodesMap.put(eachPh, tempQue);
									
								} // if
							} catch (Exception e) {
								logger.error("Exception ::::" , e);
							}
							
						} // if
						
					} // for eachPh
					
					if(logger.isDebugEnabled()) logger.debug("After filling the CouponCodes, provider.Map ="+couponCodesMap);
					
				} catch (Exception e) {
					logger.error("** Error in filling the  Promo-codes",e);
				}
				
				phCouponCodeQue = couponCodesMap.get(tempStr);
				if(phCouponCodeQue==null) {
					return null;
				} // if
			} // outer if
			
			CouponCodes tempCC = phCouponCodeQue.poll();

			return tempCC;
			
		} catch (Exception e) {
			logger.error("Exception ::::" , e);
		}
		
		return null;
	}

	 
    
    

} // class RecipientProvider
