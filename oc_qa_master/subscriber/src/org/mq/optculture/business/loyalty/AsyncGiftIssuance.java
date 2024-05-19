package org.mq.optculture.business.loyalty;

import java.security.SecureRandom;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.GiftCardSkus;
import org.mq.marketer.campaign.beans.GiftCards;
import org.mq.marketer.campaign.beans.GiftCardsExpiry;
import org.mq.marketer.campaign.beans.GiftCardsHistory;
import org.mq.marketer.campaign.beans.GiftPrograms;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.GiftCardSkusDao;
import org.mq.marketer.campaign.dao.GiftCardsDao;
import org.mq.marketer.campaign.dao.GiftCardsDaoForDML;
import org.mq.marketer.campaign.dao.GiftCardsExpiryDaoForDML;
import org.mq.marketer.campaign.dao.GiftCardsHistoryDaoForDML;
import org.mq.marketer.campaign.dao.GiftProgramsDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.optculture.business.helper.DRToLty.DRToLoyaltyExtractionCustomer;
import org.mq.optculture.business.helper.DRToLty.DRToLoyaltyExtractionItems;
import org.mq.optculture.business.helper.DRToLty.DRToLoyaltyExtractionReceipt;
import org.mq.optculture.business.rabbitMQ.CustomerInfo;
import org.mq.optculture.business.rabbitMQ.EventPayload;
import org.mq.optculture.business.rabbitMQ.LoyaltyInfo;
import org.mq.optculture.business.rabbitMQ.PublishQueue;
import org.mq.optculture.business.rabbitMQ.SaleInfo;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;

import com.google.gson.Gson;

public class AsyncGiftIssuance extends Thread {
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	private List<DRToLoyaltyExtractionItems> giftCardItemsList;
	private Users user;
	private DRToLoyaltyExtractionCustomer customerData;
	private DRToLoyaltyExtractionReceipt receipt;
	
	public AsyncGiftIssuance() {
		
	}
	
	public AsyncGiftIssuance(List<DRToLoyaltyExtractionItems> giftCardItemsList, Users user,
			DRToLoyaltyExtractionCustomer customerData,DRToLoyaltyExtractionReceipt receipt) {
		
		this.giftCardItemsList = giftCardItemsList;
		this.user = user;
		this.customerData = customerData;
		this.receipt = receipt;
	}
	
	@Override
	public void run() {
		doGiftIssuance();
	}
	
	public void doGiftIssuance() {
		
		logger.info("gift issuance started");
		
		try {
		
				for(DRToLoyaltyExtractionItems giftCardItem : giftCardItemsList) {
					
					logger.info("single quantity ");
					GiftPrograms giftProgram = getGiftProgram(giftCardItem);
					
					if(giftProgram == null) {
						logger.info("no gift program found");
						continue;
					}
					if(!giftProgram.getProgramStatus().equals(OCConstants.LOYALTY_PROGRAM_STATUS_ACTIVE)) {
						logger.info("gift program is not active");
						continue;
					}
					if(!giftProgram.getCardType().equals(OCConstants.LOYALTY_CARDSET_TYPE_VIRTUAL)) {
						logger.info("program card type is not virtual");
						continue;
					}
					
					if(Double.parseDouble((giftCardItem.getQuantity()).trim()) == 1) {
						
						String cardNumber = generateUniqueRandomNumber();
						String cardPin = generateRandomNumber(giftProgram.getPinLength().intValue());
						GiftCards giftCard = addGiftCard(giftProgram, cardNumber, cardPin,giftCardItem);
						GiftCardsHistory giftCardHistory = insertPurchaseTrx(giftCard,giftCardItem);
						insertExpiryTrx(giftCardHistory,giftCard);
						
						//insert into QUEUE
						Map<String, Object> bindingHeaders = new HashMap();
						bindingHeaders.put("EVENT_TYPE","Gift Issuance");
						bindingHeaders.put("USER_ID",user.getUserId());
						bindingHeaders.put("CONTACT_ID","");
						
						EventPayload data = new EventPayload();
						data.setUserId(user.getUserId()+"");
						
						LoyaltyInfo ltyInfo = new LoyaltyInfo();
						ltyInfo.setCardNumber(giftCard.getGiftCardNumber());
						ltyInfo.setCardPin(giftCard.getGiftCardPin());
						ltyInfo.setValueCode("Currency");
						ltyInfo.setTrxEarnings(giftCard.getGiftBalance()+"");
						String expDate = MyCalendar.calendarToString(giftCard.getExpiryDate(), MyCalendar.FORMAT_DATETIME_STYEAR);
						ltyInfo.setExpiryDate(expDate);
						
						SaleInfo info = new SaleInfo();
						info.setStoreNumber(receipt.getStore());
						info.setReceiptNumber(receipt.getInvcNum());
						info.setDocSid(receipt.getDocSID());
						
						CustomerInfo customerInfo = new CustomerInfo();
						customerInfo.setCustomerId(customerData.getCustomerId());
						customerInfo.setFirstName(customerData.getFirstName());
						customerInfo.setLastName(customerData.getLastName());
						customerInfo.setMobileNumber(customerData.getPhone());
						customerInfo.setEmailAddress(customerData.getEmailAddress());
						customerInfo.setBirthday(customerData.getBirthday());
						customerInfo.setAnniversary(customerData.getAnniversary());
						customerInfo.setGender(customerData.getGender());
						customerInfo.setCreatedDate(customerData.getCreatedDate());
						
						data.setLoyaltyInfo(ltyInfo);
						data.setSaleInfo(info);
						data.setCustomerInfo(customerInfo);
						
						String responseJson = new Gson().toJson(data, EventPayload.class);
						
						PublishQueue.publish("EVENT_QUEUE","EVENT_EXCHANGE",bindingHeaders,responseJson);//queue name,excname,key and message
						
						
					}else if(Double.parseDouble((giftCardItem.getQuantity()).trim())>1) {
						
						for(int i=1;i<=Double.parseDouble(giftCardItem.getQuantity().trim());i++) {
							
							String cardNumber = generateUniqueRandomNumber();
							String cardPin = generateRandomNumber(giftProgram.getPinLength().intValue());
							GiftCards giftCard = addGiftCard(giftProgram, cardNumber, cardPin,giftCardItem);
							GiftCardsHistory giftCardHistory = insertPurchaseTrx(giftCard,giftCardItem);
							insertExpiryTrx(giftCardHistory,giftCard);
							
							//insert into QUEUE
							Map<String, Object> bindingHeaders = new HashMap();
							bindingHeaders.put("EVENT_TYPE","Gift Issuance");
							bindingHeaders.put("USER_ID",user.getUserId());
							bindingHeaders.put("CONTACT_ID","");
							
							EventPayload data = new EventPayload();
							data.setUserId(user.getUserId()+"");
							
							LoyaltyInfo ltyInfo = new LoyaltyInfo();
							ltyInfo.setCardNumber(giftCard.getGiftCardNumber());
							ltyInfo.setCardPin(giftCard.getGiftCardPin());
							ltyInfo.setValueCode("Currency");
							ltyInfo.setTrxEarnings(giftCard.getGiftBalance()+"");
							String expDate = MyCalendar.calendarToString(giftCard.getExpiryDate(), MyCalendar.FORMAT_DATETIME_STYEAR);
							ltyInfo.setExpiryDate(expDate);
							
							SaleInfo info = new SaleInfo();
							info.setStoreNumber(receipt.getStore());
							info.setReceiptNumber(receipt.getInvcNum());
							info.setDocSid(receipt.getDocSID());
							
							CustomerInfo customerInfo = new CustomerInfo();
							customerInfo.setCustomerId(customerData.getCustomerId());
							customerInfo.setFirstName(customerData.getFirstName());
							customerInfo.setLastName(customerData.getLastName());
							customerInfo.setMobileNumber(customerData.getPhone());
							customerInfo.setEmailAddress(customerData.getEmailAddress());
							customerInfo.setBirthday(customerData.getBirthday());
							customerInfo.setAnniversary(customerData.getAnniversary());
							customerInfo.setGender(customerData.getGender());
							customerInfo.setCreatedDate(customerData.getCreatedDate());
							
							data.setLoyaltyInfo(ltyInfo);
							data.setSaleInfo(info);
							data.setCustomerInfo(customerInfo);
							
							String responseJson = new Gson().toJson(data, EventPayload.class);
							
							PublishQueue.publish("EVENT_QUEUE","EVENT_EXCHANGE",bindingHeaders,responseJson);//queue name,excname,key and message
							
						}
					}
					
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		
		logger.info("gift issuance ended ");
	}
	
	public String generateUniqueRandomNumber() {
		
		GiftCardsDao giftCardSDao = null;
		try {
			giftCardSDao = (GiftCardsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.GIFT_CARDS_DAO);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Set<String> cardsList = giftCardSDao.findCardsListByUserId(user.getUserId());
        String number;
        do {
            number = generateRandomNumber(12);
        } while (cardsList.contains(number)); {
        	logger.info("generated random card number : "+number);
        	return number;
        }
    }

    private static String generateRandomNumber(int length) {
    	SecureRandom random = new SecureRandom();
        char[] digits = new char[length];
        digits[0] = (char) (random.nextInt(9) + '1'); // Ensure the first digit is not  0
        for (int i =  1; i < length; i++) {
            digits[i] = (char) (random.nextInt(10) + '0'); // Generate the rest of the digits
        }
        return new String(digits);
    }
    
    public GiftPrograms getGiftProgram(DRToLoyaltyExtractionItems giftCardItem) {
    	
    	GiftPrograms giftProgram = null;
    	try {
    	GiftCardSkusDao giftSkusDao = (GiftCardSkusDao)ServiceLocator.getInstance().getDAOByName(OCConstants.GIFT_CARD_SKUS_DAO);
    	GiftProgramsDao giftProgramDao = (GiftProgramsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.GIFT_PROGRAMS_DAO);
    	GiftCardSkus giftCardSku = giftSkusDao.getSkuByUserIdAndSkuCode(user.getUserId(),giftCardItem.getItemSID());
		
		if(giftCardSku!=null)
			giftProgram =  giftProgramDao.findByUserIDAndProgramId(user.getUserId(),giftCardSku.getGiftProgramId());
		
    	} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return giftProgram;
    }
    
    public GiftCards addGiftCard(GiftPrograms giftPrgm, String cardNumber, String cardPin,
    		DRToLoyaltyExtractionItems giftCardItem) {
    	
    	GiftCards giftCard = new GiftCards();
    	try {
			GiftCardsDaoForDML giftCardSDaoForDML = (GiftCardsDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.GIFT_CARDS_DAO_FORDML);
			
	    	giftCard.setUserId(user.getUserId());
	    	giftCard.setGiftProgramId(giftPrgm.getGiftProgramId());
	    	giftCard.setGiftCardNumber(cardNumber);
	    	giftCard.setGiftCardPin(cardPin);
	    	giftCard.setGiftCardStatus("Active");
	    	giftCard.setGiftBalance(Double.parseDouble(giftCardItem.getDocItemOrigPrc()));
	    	giftCard.setTotalLoaded(Double.parseDouble(giftCardItem.getDocItemOrigPrc()));
	    	if(customerData.getPhone()!=null && !customerData.getPhone().isEmpty())
	    		giftCard.setPurchasedMobile(Long.parseLong(customerData.getPhone()));
	    	if(customerData.getEmailAddress()!=null && !customerData.getEmailAddress().isEmpty())
	    		giftCard.setPurchasedEmail(customerData.getEmailAddress());
	    	giftCard.setPurchasedDate(Calendar.getInstance());
	    	giftCard.setPurchasedItemSid(giftCardItem.getItemSID());
	    	giftCard.setPurchasedStoreId(Long.parseLong(receipt.getStore()));
	    	if(giftPrgm.getExpiryInMonths()!=null) {
	    		int expiryInMonths = giftPrgm.getExpiryInMonths().intValue();
	    		Calendar currentTime = Calendar.getInstance();
				currentTime.add(Calendar.MONTH,expiryInMonths);
	    		giftCard.setExpiryDate(currentTime);
	    	}
	    	giftCardSDaoForDML.saveOrUpdate(giftCard);
	    	
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return giftCard;
    	
    }
    
    public GiftCardsHistory insertPurchaseTrx(GiftCards giftCard,DRToLoyaltyExtractionItems giftCardItem) {
    	
    	GiftCardsHistory giftCardHistory = new GiftCardsHistory();
    	try {
    		GiftCardsHistoryDaoForDML historyDaoForDML = (GiftCardsHistoryDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.GIFT_CARDS_HISTORY_DAO_FORDML);
			
    		giftCardHistory.setUserId(giftCard.getUserId());
    		giftCardHistory.setGiftCardId(giftCard.getGiftCardId());
    		giftCardHistory.setGiftProgramId(giftCard.getGiftProgramId());
    		giftCardHistory.setGiftCardNumber(giftCard.getGiftCardNumber());
    		giftCardHistory.setTransactionType("Purchase");
    		giftCardHistory.setEnteredAmount(Double.parseDouble(giftCardItem.getDocItemOrigPrc()));
    		giftCardHistory.setGiftDifference(Double.parseDouble(giftCardItem.getDocItemOrigPrc()));
    		giftCardHistory.setGiftBalance(Double.parseDouble(giftCardItem.getDocItemOrigPrc()));
    		giftCardHistory.setTransactionDate(Calendar.getInstance());
    		giftCardHistory.setItemInfo(giftCardItem.getItemSID());
    		giftCardHistory.setReceiptNumber(receipt.getDocSID());
    		giftCardHistory.setStoreNumber(receipt.getStore());
    		
    		historyDaoForDML.saveOrUpdate(giftCardHistory);
    		
    	} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return giftCardHistory;
    }
    
    public void insertExpiryTrx(GiftCardsHistory history,GiftCards giftCard) {
    	
    	GiftCardsExpiry giftCardExpiry = new GiftCardsExpiry();
    	try {
    		GiftCardsExpiryDaoForDML expiryDaoForDML  = (GiftCardsExpiryDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.GIFT_CARDS_EXPIRY_DAO_FORDML);
    		
    		giftCardExpiry.setHistoryId(history.getHistoryId());
    		giftCardExpiry.setGiftCardId(giftCard.getGiftCardId());
    		giftCardExpiry.setGiftProgramId(giftCard.getGiftProgramId());
    		giftCardExpiry.setGiftCardNumber(giftCard.getGiftCardNumber());
    		giftCardExpiry.setUserId(giftCard.getUserId());
    		giftCardExpiry.setCreatedDate(Calendar.getInstance());
    		giftCardExpiry.setExpiryAmount(history.getEnteredAmount());
    		giftCardExpiry.setExpiryDate(giftCard.getExpiryDate());
    		
    		expiryDaoForDML.saveOrUpdate(giftCardExpiry);
    		
    	}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

}
