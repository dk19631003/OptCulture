package org.mq.captiway.sparkbase;
import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.ContactsLoyalty;
import org.mq.captiway.scheduler.beans.SparkBaseTransactions;
import org.mq.captiway.scheduler.dao.SparkBaseTransactionsDao;
import org.mq.captiway.scheduler.dao.SparkBaseTransactionsDaoForDML;
import org.mq.captiway.scheduler.utility.Constants;
import org.mq.captiway.scheduler.utility.MyCalendar;
import org.mq.captiway.scheduler.utility.PropertyUtil;
import org.mq.captiway.sparkbase.sparkbaseAdminWsdl.*;
import org.mq.optculture.utils.ServiceLocator;




public class SparkBaseTransactionsView {
	
	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
  private static SparkbaseAdminV45WsdlImplService service;
  private static SparkbaseAdminV45WsdlPort client;
  private static Properties properties;
  
  
  static{
	  
	  
	  properties = new Properties();
	    try {
	    	properties.put("sbAdminClientId", PropertyUtil.getPropertyValueFromDB("sbAdminClientId"));
	    	properties.put("sbAdminIntegrationAuth", PropertyUtil.getPropertyValueFromDB("sbAdminIntegrationAuth"));
	    	properties.put("sbAdminIntegrationPass", PropertyUtil.getPropertyValueFromDB("sbAdminIntegrationPass"));
	    	properties.put("sbAdminUsername", PropertyUtil.getPropertyValueFromDB("sbAdminUsername"));
	    	properties.put("sbAdminPassword", PropertyUtil.getPropertyValueFromDB("sbAdminPassword"));
	    	properties.put("sbAdminInitiatorIP", PropertyUtil.getPropertyValueFromDB("sbAdminInitiatorIP"));
	    	
	     // FileInputStream input = new FileInputStream(new File("src/conf/client.properties"));
	     // properties.load(input);
	     // input.close();
	    }
	    catch (Exception e) {
	      logger.error("Exception ::::" , e);
	    }
  }
  
  public  long lastFetchTotalCount = 0;
  public  long currOffset = 0;
  
  private  String transationLocationId=null;
  private  Map<String, String> transCardSet=new HashMap<String, String>();
  
  
  
  public long getLastFetchTotalCount() {
	return lastFetchTotalCount;
}

public void setLastFetchTotalCount(long lastFetchTotalCount) {
	this.lastFetchTotalCount = lastFetchTotalCount;
}

public long getCurrOffset() {
	return currOffset;
}

public void setCurrOffset(long currOffset) {
	this.currOffset = currOffset;
}

public String getTransationLocationId() {
	return transationLocationId;
}

public void setTransationLocationId(String transationLocationId) {
	this.transationLocationId = transationLocationId;
}

public Map<String, String> getTransCardSet() {
	return transCardSet;
}

public void setTransCardSet(Map<String, String> transCardSet) {
	this.transCardSet = transCardSet;
}


  public SparkBaseTransactionsView() {
	  lastFetchTotalCount = 0;
	  currOffset = 0;
  }
  
  public  Map<String, String> fetchTransactionCardsByLocationId(
		  String transLocationId, Calendar minCal, Calendar maxCal) {
	  
	  transationLocationId =transLocationId;
	  
	  

    logger.debug("Starting Client 1...");
    
    if(service==null) {
    	service = new SparkbaseAdminV45WsdlImplService();
    }
    
    logger.debug("Starting Client 2...");
    client = service.getSparkbaseAdminV45WsdlPortPort();

      try {
    	transCardSet.clear();
        transactionsView(minCal, maxCal);
        
        logger.debug("transCardSet="+transCardSet);
        
        if(transCardSet.size()>0) {
        	return transCardSet;
        }
      }
      catch (Exception ex) {
        logger.error("An unexpected error occured" +ex);
        logger.error(ex.getMessage());
      }
      
      return null;
    } // fetchTransactionByLocationId

  
  
  private  void transactionsView(Calendar minCal, Calendar maxCal) {
    try {
      logger.debug("Prompting For Request Data...");
      RequestHeader header = promptForHeader();
      Transaction transaction = promptForTransaction();
      String offsetStr = "";// Util.prompt("Enter the offset: ", "^[0-9]*$");
      /*Long offset = 0L;
      if (0 < offsetStr.length()) {
        offset = Long.parseLong(offsetStr);
      }*/

      String processedTimeMin = ""; //Util.prompt("Enter the minimum date (YYYYMMDD): ", "^([12][0-9]{3}[01][0-9][0-3][0-9])?$");
      String processedTimeMax = ""; //Util.prompt("Enter the maximum date (YYYYMMDD): ", "^([12][0-9]{3}[01][0-9][0-3][0-9])?$");
      
      logger.debug("MinCal="+minCal);
      logger.debug("MaxCal="+maxCal);
      
      if(minCal != null) {
    	  processedTimeMin = MyCalendar.calendarToString(minCal, MyCalendar.FORMAT_SB_DATETIME);
    	  
      }
      if(maxCal != null) {
    	  processedTimeMax = MyCalendar.calendarToString(maxCal, MyCalendar.FORMAT_SB_DATEONLY);
      }

      logger.debug("From Time: "+processedTimeMin +"  To Time: "+processedTimeMax +" CurrOffset = "+currOffset);
      
      logger.debug("Creating Request...");
      TransactionsView request = new TransactionsView();
      request.setHeader(header);
      request.setTransaction(transaction);
      //request.setOffset(offset);
      request.setOffset(currOffset);
      
      request.setProcessedTimeMin(processedTimeMin);
      request.setProcessedTimeMax(processedTimeMax);

      logger.debug("Invoking Request...");
      TransactionsViewResponse response = client.transactionsView(request);

      logger.debug("Retriving Response...");
      if (response == null) {
        logger.debug("Response Not Received!");
        return;
      }
      else {
        // TO DO: Break in to printing methods
        logger.debug("Printing Response...");
        logger.debug("response.getTotal()="+response.getTotal());
        if(response.getTotal() != null)
        	lastFetchTotalCount = response.getTotal().longValue();
        else lastFetchTotalCount = 0l;
        	
       /* if (null != response.getTransactions()) {
          if (1 < response.getTransactions().getTransactions().size()) {
            logger.info(response.getTransactions().getTransactions().size()
              + " transactions were found.");*/
            
        	SparkBaseTransactions sbTrans = null;
            ArrayOfTransaction arrOfTrans =  response.getTransactions();
            List<Transaction> transList = arrOfTrans.getTransactions();
            
            for (Transaction eachTrns : transList) {

            	// Add
				/*logger.info("==>> "+eachTrns.getLocationId()+" "+
				eachTrns.getCardId()+" "+eachTrns.getLocationName()+" "+eachTrns.getType());*/
				
            	if(eachTrns.getType().equals(Constants.SB_TRANSACTION_INQUIRY) ||
            			eachTrns.getType().equals(Constants.SB_TRANSACTION_ACCOUNT_HISTORY)) {
            		continue; // Skip these transaction types
            	}
            	
            	//String cardWithAccountId = eachTrns.getCardId()+"_"+eachTrns.getAccountId();
            	
            	//transCardSet.add(cardWithAccountId);
            	
				/*logger.info("==>> "+eachTrns.getLocationId()+" "+
				eachTrns.getCardId()+" "+eachTrns.getAccountId()+" "+ eachTrns.getLocationName()+" "+eachTrns.getType()+" "+eachTrns.getProcessedTime());*/
				
				transCardSet.put(eachTrns.getCardId(), eachTrns.getAccountId());
			} // for
            
            	
            /*Object[] obj = {transList};
	        TransactionTypeEventTrigger transactTypET = new TransactionTypeEventTrigger();
	        transactTypET.uploadQueue.add(obj);
	      	Thread tr =new Thread(transactTypET);
	      	tr.start();*/
            
            
            
            
            saveTransactionsToDb(transList);
            
          }
         /* else {
            logger.info("1 transaction was found.");
          }
        }
        else {
          logger.info("No transactions were found.");
        }
        if (null != response.getErrors()) {
        	
        	logger.info("Error Count = "+response.getErrors().getErrors().size());
        	
          if (1 < response.getErrors().getErrors().size()) {
            logger.info(response.getErrors().getErrors().size() + " errors were found At 1st place");
          }
          else {
            logger.info("1 error was found.");
          }
          for (int i = 0; i < response.getErrors().getErrors().size(); i++) {
            logger.info(response.getErrors().getErrors().get(i).getParameter()
              + " " + response.getErrors().getErrors().get(i).getMessage());
          }
        }
      }*/
    }
    catch (Exception ex) {
    	
      logger.error("An unexpected error occured  ",ex);
      logger.error(ex.getMessage());
      return;
    }
  }

  /**
   * Saves sparkbase transactions in optculture db.
   * @param transList
   */
  private static void saveTransactionsToDb(List<Transaction> transList) {
	  		
	  	int timeAddinMiutes = 0;
	  	Properties properties =  null;
	  	try {
			properties = new Properties();
			URL url = PropertyUtil.class.getClassLoader().getResource("application.properties");

			if(logger.isDebugEnabled()) logger.debug("loading properties file - url = " + url);
			 properties.load(url.openStream());
			 
			 String appUrl = properties.getProperty("OpenTrackUrl");
//			 logger.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>><<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
//			 logger.debug("appUrl is ::" +appUrl);
			 
			 if(appUrl.contains("sch.optculture.com") || appUrl.contains("14.140.147") || appUrl.contains("mailhandler") ) {
				 timeAddinMiutes = 60;
			 }else {
				 timeAddinMiutes = 690; 
			 }
			 
		} catch (IOException e3) {
			// TODO Auto-generated catch block
			logger.error("Exception :::", e3);
		}
	  	
	  	logger.debug(" >> timeAddinMiutes is  ::"+timeAddinMiutes);

		  //List<SparkBaseTransactions> sbTransList = new ArrayList<SparkBaseTransactions>();
		  SparkBaseTransactions sbTrans = null;
		  Object object = null;
		  ContactsLoyalty contactLoyalty = null;
		  SparkBaseTransactionsDao sparkbaseTransactionsDao = null;
		  SparkBaseTransactionsDaoForDML sparkbaseTransactionsDaoForDML = null;
		  
		  try {
			  sparkbaseTransactionsDao = (SparkBaseTransactionsDao)ServiceLocator.getInstance().getDAOByName("sparkBaseTransactionsDao");
			  sparkbaseTransactionsDaoForDML = (SparkBaseTransactionsDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName("sparkBaseTransactionsDaoForDML");
			} catch (Exception e2) {
				return;
			}
		  
		  
		  for (Transaction eachTrns : transList) {
          	if(eachTrns.getType().equals(Constants.SB_TRANSACTION_INQUIRY) ||
          			eachTrns.getType().equals(Constants.SB_TRANSACTION_ACCOUNT_HISTORY)) {
          		continue; // Skip these transaction types
          	}
          	
          	if(eachTrns.getType().equals(Constants.SB_TRANSACTION_ADJUSTMENT) ||
          			eachTrns.getType().equals(Constants.SB_TRANSACTION_ENROLLMENT) ||
          			eachTrns.getType().equals(Constants.SB_TRANSACTION_GIFT_ISSUANCE) ||
          			eachTrns.getType().equals(Constants.SB_TRANSACTION_GIFT_REDEMPTION) ||
          			eachTrns.getType().equals(Constants.SB_TRANSACTION_LOYALTY_ISSUANCE) ||
          			eachTrns.getType().equals(Constants.SB_TRANSACTION_LOYALTY_REDEMPTION)){
          
          			sbTrans = new SparkBaseTransactions();
          			try{
          				if(eachTrns.getAmountEntered() != null && !eachTrns.getAmountEntered().trim().isEmpty())
          				sbTrans.setAmountEntered(Double.parseDouble(eachTrns.getAmountEntered()));
          			}catch(Exception ex){
          				//logger.error("Exception in paring sparkbase transaction amountentered." , ex);
          			}
          			sbTrans.setCardId(eachTrns.getCardId());
          			sbTrans.setCreatedDate(Calendar.getInstance());
          			
          			sbTrans.setLocationId(eachTrns.getLocationId());
          			sbTrans.setLocationName(eachTrns.getLocationName());
          			
          			Calendar procTimeCal = Calendar.getInstance();
          			DateFormat sdf = new SimpleDateFormat(MyCalendar.FORMAT_DATETIME_STYEAR);
          			try {
						procTimeCal.setTime(sdf.parse(eachTrns.getProcessedTime()));
					} catch (ParseException ex) {
						
						logger.error("exception in parsing sb transaction processed time.",ex);
					}
          			sbTrans.setProcessedTime(procTimeCal);
          			
          			try {
						//set server time
						Calendar cal1 = Calendar.getInstance();
						cal1.setTime(sdf.parse(eachTrns.getProcessedTime()));
						cal1.add(Calendar.MINUTE, timeAddinMiutes); //Check and add the minutes time
						sbTrans.setServerTime(cal1);
					} catch (ParseException e1) {
						logger.error("parsing exception is  :::",e1);
					}
          			
          			
          			
          			sbTrans.setStatus(Constants.SPARKBASE_TRANSACTION_STATUS_NEW);
          			sbTrans.setType(eachTrns.getType());
          			sbTrans.setTransactionId(eachTrns.getTransactionId());
          			
          			ArrayOfTransactionBalance atb = eachTrns.getBalances();
          			List<TransactionBalance> transBalList = atb.getTransactionBalances();
          			
          			for (TransactionBalance tbl : transBalList){
          				try{
          					sbTrans.setDifference(Double.parseDouble(tbl.getDifference()));
          				}catch(Exception e){
          					//logger.error("Exception in sparkbase transaction difference.", e);
          				}
          			}
          			
          			contactLoyalty = new ContactsLoyalty();
          			object = SparkBaseAdminService.fetchData(SparkBaseAdminService.BALANCESVIEW, eachTrns.getAccountId(), contactLoyalty,true);
					if(object == null) {
						 continue;
					}
          			sbTrans.setGiftcardBalance(contactLoyalty.getGiftcardBalance());
          			sbTrans.setLoyaltyBalance(contactLoyalty.getLoyaltyBalance());
          			try{
          				//sparkbaseTransactionsDao.save(sbTrans);
          				sparkbaseTransactionsDaoForDML.save(sbTrans);
          			}catch(Exception ex){
          				//logger.info("Exception in saving sb transaction.", ex);
          				//logger.debug("exception in saving ....");//
          				//continue;
          			}
          			//logger.debug("After exception..................");
          			//sbTransList.add(sbTrans);
    			}
			}
		  
  }
  
  		
  private static RequestHeader promptForHeader() {
    RequestHeader header = new RequestHeader();

    // NOTE: Some fields are excluded or hard coded
    // for demostartion purposes.
    // DO NOT USE IN PRODUCTION
    header.setClient(properties.getProperty("sbAdminClientId"));
    header.setIntegrationAuth(properties.getProperty("sbAdminIntegrationAuth"));
    header.setIntegrationPass(properties.getProperty("sbAdminIntegrationPass"));
    header.setInitiatorType("user");
    header.setInitiatorId(properties.getProperty("sbAdminUsername"));
    header.setInitiatorPass(properties.getProperty("sbAdminPassword"));
    header.setInitiatorIP(properties.getProperty("sbAdminInitiatorIP"));

    return header;
  }

  private  Transaction promptForTransaction() {
    Transaction transaction = new Transaction();

    // NOTE: Some fields are excluded for demostartion purposes.
    // DO NOT USE IN PRODUCTION
    //transaction.setCardId(Util.prompt("Enter the card id: ", "^([0-9A-Za-z]{7,25})?$"));
    
    //transaction.setTransactionId(Util.prompt("Enter the transaction id: ",   "^([0-9]{1,10})?$"));
    
    transaction.setLocationId(transationLocationId); // Util.prompt("Enter the location id: ", "^([0-9A-Za-z]{1,25})?$"));

    return transaction;
  }
}
