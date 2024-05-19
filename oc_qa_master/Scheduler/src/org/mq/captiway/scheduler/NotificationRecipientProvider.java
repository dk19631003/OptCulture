package org.mq.captiway.scheduler;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.Contacts;
import org.mq.captiway.scheduler.beans.ContactsLoyalty;
import org.mq.captiway.scheduler.beans.CouponCodes;
import org.mq.captiway.scheduler.beans.NotificationCampaignReport;
import org.mq.captiway.scheduler.beans.NotificationCampaignSent;
import org.mq.captiway.scheduler.beans.RetailProSalesCSV;
import org.mq.captiway.scheduler.dao.ContactsDao;
import org.mq.captiway.scheduler.dao.ContactsDaoForDML;
import org.mq.captiway.scheduler.dao.ContactsLoyaltyDao;
import org.mq.captiway.scheduler.dao.NotificationCampaignSentDao;
import org.mq.captiway.scheduler.dao.NotificationCampaignSentDaoForDML;
import org.mq.captiway.scheduler.dao.RetailProSalesDao;
import org.mq.captiway.scheduler.utility.Constants;
import org.mq.captiway.scheduler.utility.PlaceHolders;
import org.mq.optculture.utils.ServiceLocator;

public class NotificationRecipientProvider {

	private static final  Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);

    private Iterator<Contacts> iter;
    private int totalSizeInt;
    private int currentSizeInt = 0;
    private Long currentSentId;
    private Long lastContactId;
    private Date lastNotificationDate;
    private ContactsDao contactsDao;
    private NotificationCampaignSentDao notificationCampaignSentDao;
    private NotificationCampaignSentDaoForDML notificationCampaignSentDaoForDML ;
    private NotificationCampaignReport notificationCampaignReport;
    private String listIdsStr;
    private boolean isLastNotificationDateUpdated = false;
    private Set<String> totalPhSet=null;
    private int tempCount = 0;
    private ServiceLocator locator ;
    private ContactsDaoForDML contactsDaoForDML;
	private  Map<String, ContactsLoyalty> contactsLoyaltyMap = null;
    private  Map<String, RetailProSalesCSV> contactsLastPurchaseMap = null;
    private List<NotificationCampaignSent> sentList = new ArrayList<NotificationCampaignSent>();
    private Vector<CouponCodes> usedCouponCodesVec = new Vector<CouponCodes>();
    
    
    public synchronized int getTempCount() {
		return tempCount;
	}

	public synchronized void setTempCount(int tempCount) {
		this.tempCount = tempCount;
	}

	public synchronized void addTempCount(int incCount) {
		this.tempCount += incCount;
	}
    public synchronized void addCouponCodeToVec(CouponCodes cc) {
    	usedCouponCodesVec.add(cc);
    } 
    

    private Contacts tempContact;
    
	public NotificationRecipientProvider(NotificationCampaignReport notificationCampaignReport,int theTotalSizeInt, Long campId, Set<String> totalPhSet) {
		try {
			logger.debug("RP Created------------ 2 ------------");
	    	this.notificationCampaignReport = notificationCampaignReport;
	    	this.totalSizeInt = theTotalSizeInt;
	    	this.currentSizeInt = 0;
	    	this.totalPhSet=totalPhSet;
	    	this.locator = ServiceLocator.getInstance();
	    	this.lastNotificationDate = new Date();
	    	this.contactsDao = (ContactsDao)locator.getDAOByName("contactsDao");
	    	this.notificationCampaignSentDao = (NotificationCampaignSentDao)locator.getDAOByName("notificationCampaignSentDao");
	    	this.notificationCampaignSentDaoForDML = (NotificationCampaignSentDaoForDML)locator.getDAOForDMLByName("notificationCampaignSentDaoForDML");
	    	this.contactsDaoForDML = (ContactsDaoForDML)locator.getDAOForDMLByName("contactsDaoForDML");
	    	this.currentSentId = notificationCampaignSentDao.getCurrentSentId();
	        if(logger.isDebugEnabled()) {
	        	logger.info(" >>>>>>>>> List Ids :"+listIdsStr);
	        	logger.info(" Total Contacts to send :"+totalSizeInt);
	        	logger.info(" Current SendId :"+currentSentId);
	        }
		}catch (Exception e) {
			logger.error("NotificationRecipientProvider "+e);
		}

    }

	
	 public synchronized Contacts getNext() throws Exception {
	    	
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
	    	contacts = contactsDao.getSegmentedContacts("SELECT * FROM notification_tempcontacts ",
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
	    			
	    				ContactsLoyaltyDao contactsLoyaltyDao = (ContactsLoyaltyDao)locator.getDAOByName("contactsLoyaltyDao");
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
	    				
	    				RetailProSalesDao retailProSalesDao = (RetailProSalesDao)locator.getDAOByName("retailProSalesDao");
	    				contactsLastPurchaseMap = new HashMap<String, RetailProSalesCSV>();
	    				
	    				List<RetailProSalesCSV> retList = retailProSalesDao.findLastPurCheseDatePlaceHolderVal(cidsStr.toString());
	    				
	    				if(retList == null || retList.size() == 0 ) continue;
	    				
	    				for (RetailProSalesCSV retailProSalesCSV : retList) {
							
	    					contactsLastPurchaseMap.put(retailProSalesCSV.getCid().longValue()+Constants.STRING_NILL, retailProSalesCSV);
	    					
						}
	    				
	    			}//else
	    			
	    		}//for each placeholder
	    		
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
				if(lastContactId != null && !isLastNotificationDateUpdated) {

					DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String updateQryStr = null;
					if(notificationCampaignReport != null) { // campaign submission
						
						updateQryStr ="update contacts c, notification_tempcontacts s  set c.last_notification_date ='"+format.format(lastNotificationDate)+"' where c.cid=s.cid"; 

					}
					
					long startTime = System.currentTimeMillis();
					contactsDaoForDML.executeUpdateQuery(updateQryStr);
					logger.info("Elapsed time for this db tuning for query :=="+updateQryStr+"  is ::: "+(System.currentTimeMillis() - startTime)+" Millisecond");
					
					
					isLastNotificationDateUpdated = true;
				}
				return null;
			}
			/**
			 * Last Contact Id in the list for reference to
			 * update the last mail date of contacts table till this contact
			 */
			lastContactId = contacts.get(contacts.size()-1).getContactId();

			if(logger.isDebugEnabled()) {
				logger.info(">>>>>Got the contacts from DB: "+contacts.size());
			}

			currentSizeInt += 10000;

			this.iter = contacts.listIterator();

			if(notificationCampaignReport != null) {

				// Generates the sentId for reference to CampaignSent object
				// and stores in the Contact's temp variable
				while(this.iter != null && this.iter.hasNext()) {

					tempContact = iter.next();
					++currentSentId;

					
					NotificationCampaignSent notificationCampaignSent = new NotificationCampaignSent(currentSentId, tempContact.getMobilePhone(), 
							notificationCampaignReport, "Status Pending",tempContact.getContactId(), 0, 0, tempContact.getInstanceId());
					
					tempContact.setTempObj(notificationCampaignSent);
					
					sentList.add(notificationCampaignSent);
				}
				if(logger.isDebugEnabled()) {
					logger.info(">>>>>Total Campaign Sent objects to save :"+sentList.size());
				}

				/**
				 *    Saves the CampaignSent objects into the campaign_sent table
				 *    and update the last mail date for the contact to which campaign is sent.
				 */
				try {
					notificationCampaignSentDaoForDML.saveByCollection(sentList);
					sentList.clear();

				}
				catch (Exception e) {
					logger.error(">>>>>Exception while saving sent objects ::"+sentList.size(), e);
					sentList.clear();
				}
			}

			this.iter = contacts.listIterator();
			tempContact = iter.next();

	        return tempContact;

	    } // getNext()
	
	
}
