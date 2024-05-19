package org.mq.marketer.sparkbase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.Contacts;
import org.mq.marketer.campaign.beans.ContactsLoyalty;
import org.mq.marketer.campaign.beans.SparkBaseLocationDetails;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.sparkbase.transactionWsdl.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

import javax.xml.ws.BindingProvider;

public class SparkBaseService {
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
  
	public static final String INQUIRY			= "INQUIRY";
	public static final String ACCOUNT_HISTORY	= "ACCOUNT_HISTORY";
	public static final String ENROLLMENT		= "ENROLLMENT";
	public static final String LOYALTYISSUANCE	= "LOYALTYISSUANCE";
	public static final String LOYALTYREDEMPTION	= "LOYALTYREDEMPTION";
	public static final String GIFTISSUANCE	= "GIFTISSUANCE";
	public static final String GIFTREDEMPTION	= "GIFTREDEMPTION";
	public static final String ADJUSTMENT	= "ADJUSTMENT";
	
	private static int count;
	private static SparkbaseTransactionWsdlImplService service=null;
	private static SparkbaseTransactionWsdlPort client=null;
	//private static BufferedReader in;
	//private static Properties sbProperties;
  
	private static SparkBaseLocationDetails sbLocation;
	private static ContactsLoyalty contactLoyalty;
	private static Contacts contact;
	private static boolean updateLoyaltyData;
	private static String[] enteredAmount;

	/**
	 * 
	 * @param requestType
	 * @param sbLocationObj
	 * @param contactLoyaltyObj
	 * @param contactObj
	 */
	public static synchronized Object fetchData(String requestType, 
			SparkBaseLocationDetails sbLocationObj,	ContactsLoyalty contactLoyaltyObj, 
			Contacts contactObj,String[] amount, boolean updateLoyaltyDataFlag) {
		try {
			sbLocation=sbLocationObj;
			contactLoyalty = contactLoyaltyObj;
			contact = contactObj;
			updateLoyaltyData = updateLoyaltyDataFlag;
			enteredAmount = amount;
			
			// Start Proxy if not started
			if(service==null) {
			    service = new SparkbaseTransactionWsdlImplService(); //Get the service
			    client = service.getPort(SparkbaseTransactionWsdlPort.class); //Get the port
			    logger.info("Starting Proxy Client...");
			}
			
		    //Configure RequestContext
		    Map<String, Object> rc = ((BindingProvider)client).getRequestContext();
		    // TEST: This data goes a test location on the a local transaction server
		    // DO NOT USE IN PRODUCTION

		    rc.put(BindingProvider.USERNAME_PROPERTY, sbLocation.getIntegrationUserName());
		    rc.put(BindingProvider.PASSWORD_PROPERTY, sbLocation.getIntegrationPassword());

		    //ErrorMessageComponent errMsg = null;
		    Object object = null;
		    
			if(requestType.equals(INQUIRY)) {
				object = inquiry();
			}
			else if(requestType.equals(ENROLLMENT)) {
				object = enrollment();
			}
			else if(requestType.equals(ACCOUNT_HISTORY)) {
				object = accountHistory();
			}
			else if(requestType.equals(LOYALTYISSUANCE)) {
				object = loyaltyIssuance();
			}
			else if(requestType.equals(LOYALTYREDEMPTION)) {
				object = loyaltyRedemption();
			}
			else if(requestType.equals(GIFTISSUANCE)) {
				object = giftIssuance();
			}
			else if(requestType.equals(GIFTREDEMPTION)) {
				object = giftRedemption();
			}
			else if(requestType.equals(ADJUSTMENT)) {
				object = adjustment();
			}
			
			return object;
			
		} catch (Exception e) {
			logger.error("Exception ::" , e);
		}
		return null;
	} //

 /* public static void main(String[] args) {
    
	initializeSparkBase();
	  
    count = 0;

    InputStreamReader inStream = new InputStreamReader(System.in);
    in = new BufferedReader(inStream);

    while (true) {
      count++;

      logger.info("Select A Type Of Transaction.");
      logger.info("1. Inquiry");
      logger.info("2. Account History");
      logger.info("3. Employee Report");
      logger.info("4. Terminal Report");
      logger.info("5. Gift Issuance");
      logger.info("6. Gift Redemption");
      logger.info("7. Enrollment");
      logger.info("8. Loyalty Issuance");
      logger.info("9. Loyalty Redemption");
      logger.info("10. Promotion Issuance");
      logger.info("11. Promotion Redemption");
      logger.info("12. Merchandise Return");
      logger.info("13. Multiple Issuances");
      logger.info("14. Hold");
      logger.info("15. Hold Redemption");
      logger.info("16. Hold Return");
      logger.info("17. Quick Transaction");
      logger.info("18. Transfer");
      logger.info("19. Renew");
      logger.info("20. Tip");
      logger.info("21. Void");
      logger.info("99. Quit");

      String input = prompt("Enter a command > ");
      input = input.toUpperCase();
      try {
        if (Pattern.matches("^((1)|(INQUIRY)|(I))$", input)) {
          inquiry();
        }
        else if (Pattern.matches("^((2)|(ACCOUNT HISTORY)|(AH))$", input)) {
          accountHistory();
        }
        else if (Pattern.matches("^((3)|(EMPLOYEE REPORT)|(ER))$", input)) {
          employeeReport();
        }
        else if (Pattern.matches("^((4)|(TERMINAL REPORT)|(TR))$", input)) {
          terminalReport();
        }
        else if (Pattern.matches("^((5)|(GIFT ISSUANCE)|(GI))$", input)) {
          giftIssuance();
        }
        else if (Pattern.matches("^((6)|(GIFT REDEMPTION)|(GR))$", input)) {
          giftRedemption();
        }
        else if (Pattern.matches("^((7)|(ENROLLMENT)|(E))$", input)) {
          enrollment();
        }
        else if (Pattern.matches("^((8)|(LOYALTY ISSUANCE)|(LI))$", input)) {
          loyaltyIssuance();
        }
        else if (Pattern.matches("^((9)|(LOYALTY REDEMPTION)|(LR))$", input)) {
          loyaltyRedemption();
        }
        else if (Pattern.matches(
          "^((10)|(PROMOTION ISSUANCE)|(PI))$", input)) {
          promotionIssuance();
        }
        else if (Pattern.matches(
          "^((11)|(PROMOTION REDEMPTION)|(PR))$", input)) {
          promotionRedemption();
        }
        else if (Pattern.matches(
          "^((12)|(MERCHANDISE RETURN)|(MR))$", input)) {
          merchandiseReturn();
        }
        else if (Pattern.matches(
          "^((13)|(MULTIPLE ISSUANCES)|(MI))$", input)) {
          multipleIssuance();
        }
        else if (Pattern.matches(
          "^((14)|(HOLD)|(H))$", input)) {
          hold();
        }
        else if (Pattern.matches(
          "^((15)|(HOLD REDEMPTION)|(HR))$", input)) {
          holdRedemption();
        }
        else if (Pattern.matches(
          "^((16)|(HOLD RETURN)|(HU))$", input)) {
          holdReturn();
        }
        else if (Pattern.matches(
          "^((17)|(QUICK TRANSACTION)|(QT))$", input)) {
          quickTransaction();
        }
        else if (Pattern.matches("^((18)|(TRANSFER)|(X))$", input)) {
          transfer();
        }
        else if (Pattern.matches("^((19)|(RENEW)|(R))$", input)) {
          renew();
        }
        else if (Pattern.matches("^((20)|(TIP)|(T))$", input)) {
          tip();
        }
        else if (Pattern.matches("^((21)|(VOID)|(V))$", input)) {
          voidTransaction();
        }
        else if (Pattern.matches("^((99)|(QUIT)|(Q))$", input)) {
          return;
        }
        else if (input == "") {
          continue;
        }
        else {
          logger.info("Invalid Input!");
        }
      }
      catch (Exception ex) {
        logger.info("The selected transaction is currently unavaible");
      }

      prompt("Press Enter To Continue...");
    }
  }*/

  private static Object inquiry() {
    
	  try {
    	
      logger.info("Creating Request...");
      Inquiry request = new Inquiry();

      logger.info("Prompting For Request Data...");
      request.setStandardHeader(promptForRequestStandardHeader());
      request.setAccount(promptForAccount());
      //request.setQuestionsAndAnswers(promptForQuestionsAndAnswers());

      logger.info("Invoking Request...");
      InquiryResponse response = client.inquiry(request);

      logger.info("Retriving Response...");
      if (response == null) {
        logger.info("Response Not Received!");
      }
      else {
        ResponseStandardHeaderComponent standardHeader =
          response.getStandardHeader();

        if (standardHeader.getStatus().equals("E")) {
          logger.info("Printing Error...");
          printErrorMessage(response.getErrorMessage());
          return response.getErrorMessage();
        }
        else {
          logger.info("Printing Response...");
          printIdentification(response.getIdentification());
          printExpirationDate(response.getExpirationDate());
          printBalances(response.getBalances());
          printCustomerDemographics(response.getCustomerInfo());
          printHostMessage(response.getHostMessage());
          printPrintCodes(response.getPrintCodes());
          
          return response;
        }
      }
    }
    catch (Exception ex) {
      logger.info("An unexpected server error occured");
      logger.info(ex.getMessage());
    }
    return null;
  }

  private static ErrorMessageComponent accountHistory() {
    try {
      logger.info("Creating Request...");
      AccountHistory request = new AccountHistory();

      logger.info("Prompting For Request Data...");
      request.setStandardHeader(promptForRequestStandardHeader());
      request.setAccount(promptForAccount());
      request.setReport(promptForReport());

      logger.info("Invoking Request...");
      AccountHistoryResponse response = client.accountHistory(request);

      logger.info("Retriving Response...");
      if (response == null) {
        logger.info("Response Not Received!");
      }
      else {
        ResponseStandardHeaderComponent standardHeader = response.getStandardHeader();

        if (standardHeader.getStatus().equals("E")) {
          logger.info("Printing Error...");
          printErrorMessage(response.getErrorMessage());
          return response.getErrorMessage();
        }
        else {
          logger.info("Printing Response...");
          printPrintableSegment(response.getPrintableData());
        }
      }
    }
    catch (Exception ex) {
      logger.info("An unexpected server error occured");
      logger.info(ex.getMessage());
    }
    return null;
  }

  private static void employeeReport() {
    try {
      logger.info("Creating Request...");
      EmployeeReport request = new EmployeeReport();

      logger.info("Prompting For Request Data...");
      request.setStandardHeader(promptForRequestStandardHeader());
      request.setReportOnEmployeeId(promptForReportOnEmployee());
      request.setReport(promptForReport());

      logger.info("Invoking Request...");
      EmployeeReportResponse response = client.employeeReport(request);

      logger.info("Retriving Response...");
      if (response == null) {
        logger.info("Response Not Received!");
      }
      else {
        ResponseStandardHeaderComponent standardHeader =
          response.getStandardHeader();

        if (standardHeader.getStatus().equals("E")) {
          logger.info("Printing Error...");
          printErrorMessage(response.getErrorMessage());
        }
        else {
          logger.info("Printing Response...");
          printPrintableSegment(response.getPrintableData());
        }
      }
    }
    catch (Exception ex) {
      logger.info("An unexpected server error occured");
      logger.info(ex.getMessage());
      return;
    }
  }

  private static void terminalReport() {
    try {
      logger.info("Creating Request...");
      TerminalReport request = new TerminalReport();

      logger.info("Prompting For Request Data...");
      request.setStandardHeader(promptForRequestStandardHeader());
      request.setReport(promptForReport());

      logger.info("Invoking Request...");
      TerminalReportResponse response = client.terminalReport(request);

      logger.info("Retriving Response...");
      if (response == null) {
        logger.info("Response Not Received!");
      }
      else {
        ResponseStandardHeaderComponent standardHeader =
          response.getStandardHeader();

        if (standardHeader.getStatus().equals("E")) {
          logger.info("Printing Error...");
          printErrorMessage(response.getErrorMessage());
        }
        else {
          logger.info("Printing Response...");
          printPrintableSegment(response.getPrintableData());
        }
      }
    }
    catch (Exception ex) {
      logger.info("An unexpected server error occured");
      logger.info(ex.getMessage());
      return;
    }
  }

  private static Object giftIssuance() {
    try {
      logger.info("Creating Request...");
      GiftIssuance request = new GiftIssuance();

      logger.info("Prompting For Request Data...");
      request.setStandardHeader(promptForRequestStandardHeader());
      request.setAccount(promptForAccount());
      request.setAmount(promptForAmount("USD"));
      //request.setActivating(promptForActivating());
      //request.setPromotionCodes(promptForPromotionCodes());
      //request.setQuestionsAndAnswers(promptForQuestionsAndAnswers());
      //request.setCustomerInfo(promptForRequestCustomerInfo());

      logger.info("Invoking Request...");
      GiftIssuanceResponse response = client.giftIssuance(request);

      logger.info("Retriving Response...");
      if (response == null) {
        logger.info("Response Not Received!");
      }
      else {
        ResponseStandardHeaderComponent standardHeader =
          response.getStandardHeader();

        if (standardHeader.getStatus().equals("E")) {
          logger.info("Printing Error...");
          printErrorMessage(response.getErrorMessage());
          return response.getErrorMessage();
        }
        else {
          logger.info("Printing Response...");
          printIdentification(response.getIdentification());
          printExpirationDate(response.getExpirationDate());
          printBalances(response.getBalances());
          printCustomerDemographics(response.getCustomerInfo());
          printHostMessage(response.getHostMessage());
          printPrintCodes(response.getPrintCodes());
          return response;
        }
      }
    }
    catch (Exception ex) {
      logger.info("An unexpected server error occured");
      logger.info(ex.getMessage());
      //return;
    }
    return null;
  }

  private static Object giftRedemption() {
    try {
      logger.info("Creating Request...");
      GiftRedemption request = new GiftRedemption();

      logger.info("Prompting For Request Data...");
      request.setStandardHeader(promptForRequestStandardHeader());
      request.setAccount(promptForAccount());
      request.setAmount(promptForAmount("USD"));
      //request.setIncludeTip(promptForIncludeTip());
      //request.setActivating(promptForActivating());
      //request.setPromotionCodes(promptForPromotionCodes());
      //request.setQuestionsAndAnswers(promptForQuestionsAndAnswers());
      //request.setCustomerInfo(promptForRequestCustomerInfo());

      logger.info("Invoking Request...");
      GiftRedemptionResponse response = client.giftRedemption(request);

      logger.info("Retriving Response...");
      if (response == null) {
        logger.info("Response Not Received!");
      }
      else {
        ResponseStandardHeaderComponent standardHeader =
          response.getStandardHeader();

        if (standardHeader.getStatus().equals("E")) {
          logger.info("Printing Error...");
          ErrorMessageComponent errorMessage = response.getErrorMessage();
          
          logger.info(errorMessage.getInDepthMessage());
          return response.getErrorMessage();
        }
        else {
          logger.info("Printing Response...");

          if (standardHeader.getStatus().equals("E")) {
            logger.info("Printing Error...");
            printErrorMessage(response.getErrorMessage());
            return response.getErrorMessage();
          }
          else {
            logger.info("Printing Response...");
            printIdentification(response.getIdentification());
            printExpirationDate(response.getExpirationDate());
            printBalances(response.getBalances());
            printAmountRemaining(response.getAmountRemaining());
            printCustomerDemographics(response.getCustomerInfo());
            printHostMessage(response.getHostMessage());
            printPrintCodes(response.getPrintCodes());
            return response;
          }
        }
      }
    }
    catch (Exception ex) {
      logger.info("An unexpected server error occured");
      logger.info(ex.getMessage());
      //return;
    }
    return null;
  }

  private static Object adjustment(){
	  try {
	      logger.info("Creating Adjustment Request...");
	      Adjustment request = new Adjustment();

	      logger.info("Prompting For Request Data...");
	      request.setStandardHeader(promptForRequestStandardHeader());
	      request.setAccount(promptForAccount());
	      request.setAmount(promptForAmount("Points"));
	      
	      logger.info("Invoking Request...");
	      AdjustmentResponse response = client.adjustment(request);
	      
	      logger.info("Retriving Response...");
	      if (response == null) {
	        logger.info("Response Not Received!");
	      }
	      else {
	        ResponseStandardHeaderComponent standardHeader =
	          response.getStandardHeader();

	        if (standardHeader.getStatus().equals("E")) {
	          logger.info("Printing Error...");
	          //printErrorMessage(response.getErrorMessage());
	          return response.getErrorMessage();
	        }
	        else {
	          logger.info("Printing Response...");
	          printIdentification(response.getIdentification());
	          printExpirationDate(response.getExpirationDate());
	          printBalances(response.getBalances());
	          printCustomerDemographics(response.getCustomerInfo());
	          printHostMessage(response.getHostMessage());
	          printPrintCodes(response.getPrintCodes());
	          return response;
	        }
	      }
	    }
	    catch (Exception ex) {
	      logger.info("An unexpected server error occured");
	      logger.info(ex.getMessage());
	      
	    }
	    return null;
  }
  
  
  
  private static ErrorMessageComponent enrollment() {
    try {
      logger.info("Creating Request...");
      Enrollment request = new Enrollment();

      logger.info("Prompting For Request Data...");
      request.setStandardHeader(promptForRequestStandardHeader());
      request.setAccount(promptForAccount());
      //request.setAmount(promptForAmount("Points"));
      request.setActivating(promptForActivating());
      request.setPromotionCodes(promptForPromotionCodes());
      request.setQuestionsAndAnswers(promptForQuestionsAndAnswers());
      request.setCustomerInfo(promptForRequestCustomerInfo());

      logger.info("Invoking Request...");
      EnrollmentResponse response = client.enrollment(request);

      logger.info("Retriving Response...");
      if (response == null) {
        logger.info("Response Not Received!");
      }
      else {
        ResponseStandardHeaderComponent standardHeader =
          response.getStandardHeader();

        if (standardHeader.getStatus().equals("E")) {
          logger.info("Printing Error...");
          printErrorMessage(response.getErrorMessage());
          return response.getErrorMessage();
        }
        else {
          logger.info("Printing Response...");
          printIdentification(response.getIdentification());
          printExpirationDate(response.getExpirationDate());
          printBalances(response.getBalances());
          printCustomerDemographics(response.getCustomerInfo());
          printHostMessage(response.getHostMessage());
          printPrintCodes(response.getPrintCodes());
        }
      }
    } catch (Exception ex) {
      logger.info("An unexpected server error occured");
      logger.info(ex.getMessage());
      
      ErrorMessageComponent errMsg = new ErrorMessageComponent();

      if(ex.getMessage().contains("Unauthorized")) {
    	  errMsg.setBriefMessage("Invalid integration username/password for the given location id.");
    	  errMsg.setInDepthMessage("Invalid integration username/password for the given location id.");
      }
      else {
    	  errMsg.setBriefMessage("An unexpected server error occured");
    	  errMsg.setInDepthMessage(ex.getMessage());
      }
      return errMsg;
    } // catch
    
    return null;
  }

  private static Object loyaltyIssuance() {
    try {
      logger.info("Creating Request...");
      LoyaltyIssuance request = new LoyaltyIssuance();

      logger.info("Prompting For Request Data...");
      request.setStandardHeader(promptForRequestStandardHeader());
      request.setAccount(promptForAccount());
      request.setAmount(promptForAmount("Points"));
      //request.setActivating(promptForActivating());
      //request.setPromotionCodes(promptForPromotionCodes());
      //request.setQuestionsAndAnswers(promptForQuestionsAndAnswers());
      //request.setCustomerInfo(promptForRequestCustomerInfo());

      logger.info("Invoking Request...");
      LoyaltyIssuanceResponse response = client.loyaltyIssuance(request);

      logger.info("Retriving Response...");
      if (response == null) {
        logger.info("Response Not Received!");
      }
      else {
        ResponseStandardHeaderComponent standardHeader =
          response.getStandardHeader();

        if (standardHeader.getStatus().equals("E")) {
          logger.info("Printing Error...");
          //printErrorMessage(response.getErrorMessage());
          return response.getErrorMessage();
        }
        else {
          logger.info("Printing Response...");
          printIdentification(response.getIdentification());
          printExpirationDate(response.getExpirationDate());
          printBalances(response.getBalances());
          printAmountRemaining(response.getAmountRemaining());
          printCustomerDemographics(response.getCustomerInfo());
          printHostMessage(response.getHostMessage());
          printPrintCodes(response.getPrintCodes());
          return response;
        }
      }
    }
    catch (Exception ex) {
      logger.info("An unexpected server error occured");
      logger.info(ex.getMessage());
      
    }
    return null;
  }

  private static Object loyaltyRedemption() {
    try {
      logger.info("Creating Request...");
      LoyaltyRedemption request = new LoyaltyRedemption();

      logger.info("Prompting For Request Data...");
      request.setStandardHeader(promptForRequestStandardHeader());
      request.setAccount(promptForAccount());
      request.setAmount(promptForAmount("Points"));
      //request.setActivating("N");
      //request.setPromotionCodes(promptForPromotionCodes());
      //request.setQuestionsAndAnswers(promptForQuestionsAndAnswers());
      //request.setCustomerInfo(promptForRequestCustomerInfo());

      logger.info("Invoking Request...");
      LoyaltyRedemptionResponse response = client.loyaltyRedemption(request);

      logger.info("Retriving Response...");
      if (response == null) {
        logger.info("Response Not Received!");
      }
      else {
        ResponseStandardHeaderComponent standardHeader =
          response.getStandardHeader();

        if (standardHeader.getStatus().equals("E")) {
          logger.info("Printing Error...");
          printErrorMessage(response.getErrorMessage());
          return response.getErrorMessage();
        }
        else {
          logger.info("Printing Response...");
          printIdentification(response.getIdentification());
          printExpirationDate(response.getExpirationDate());
          printBalances(response.getBalances());
          printCustomerDemographics(response.getCustomerInfo());
          printHostMessage(response.getHostMessage());
          printPrintCodes(response.getPrintCodes());
          return response;
        }
      }
    }
    catch (Exception ex) {
      logger.info("An unexpected server error occured");
      logger.info(ex.getMessage());
      
    }
    return null;
  }

  private static void promotionIssuance() {
    try {
      logger.info("Creating Request...");
      PromotionIssuance request = new PromotionIssuance();

      logger.info("Prompting For Request Data...");
      request.setStandardHeader(promptForRequestStandardHeader());
      request.setAccount(promptForAccount());
      request.setActivating(promptForActivating());
      request.setAmount(promptForAmount("Points"));
      request.setPromotionCodes(promptForPromotionCodes());
      request.setQuestionsAndAnswers(promptForQuestionsAndAnswers());
      request.setCustomerInfo(promptForRequestCustomerInfo());

      logger.info("Invoking Request...");
      PromotionIssuanceResponse response = client.promotionIssuance(request);

      logger.info("Retriving Response...");
      if (response == null) {
        logger.info("Response Not Received!");
      }
      else {
        ResponseStandardHeaderComponent standardHeader =
          response.getStandardHeader();

        if (standardHeader.getStatus().equals("E")) {
          logger.info("Printing Error...");
          printErrorMessage(response.getErrorMessage());
        }
        else {
          logger.info("Printing Response...");
          printIdentification(response.getIdentification());
          printExpirationDate(response.getExpirationDate());
          printBalances(response.getBalances());
          printCustomerDemographics(response.getCustomerInfo());
          printHostMessage(response.getHostMessage());
          printPrintCodes(response.getPrintCodes());
        }
      }
    }
    catch (Exception ex) {
      logger.info("An unexpected server error occured");
      logger.info(ex.getMessage());
      return;
    }
  }

  private static void promotionRedemption() {
    try {
      logger.info("Creating Request...");
      PromotionRedemption request = new PromotionRedemption();

      logger.info("Prompting For Request Data...");
      request.setStandardHeader(promptForRequestStandardHeader());
      request.setAccount(promptForAccount());
      request.setActivating(promptForActivating());
      request.setAmount(promptForAmount("Points"));
      request.setPromotionCodes(promptForPromotionCodes());
      request.setQuestionsAndAnswers(promptForQuestionsAndAnswers());
      request.setCustomerInfo(promptForRequestCustomerInfo());

      logger.info("Invoking Request...");
      PromotionRedemptionResponse response =
        client.promotionRedemption(request);

      logger.info("Retriving Response...");
      if (response == null) {
        logger.info("Response Not Received!");
      }
      else {
        ResponseStandardHeaderComponent standardHeader =
          response.getStandardHeader();

        if (standardHeader.getStatus().equals("E")) {
          logger.info("Printing Error...");
          printErrorMessage(response.getErrorMessage());
        }
        else {
          logger.info("Printing Response...");
          printIdentification(response.getIdentification());
          printExpirationDate(response.getExpirationDate());
          printBalances(response.getBalances());
          printAmountRemaining(response.getAmountRemaining());
          printCustomerDemographics(response.getCustomerInfo());
          printHostMessage(response.getHostMessage());
          printPrintCodes(response.getPrintCodes());
        }
      }
    }
    catch (Exception ex) {
      logger.info("An unexpected server error occured");
      logger.info(ex.getMessage());
      return;
    }
  }

  private static void merchandiseReturn() {
    try {
      logger.info("Creating Request...");
      MerchandiseReturn request = new MerchandiseReturn();

      logger.info("Prompting For Request Data...");
      request.setStandardHeader(promptForRequestStandardHeader());
      request.setAccount(promptForAccount());
      request.setAmount(promptForAmount("USD"));
      request.setActivating(promptForActivating());
      request.setPromotionCodes(promptForPromotionCodes());
      request.setQuestionsAndAnswers(promptForQuestionsAndAnswers());
      request.setCustomerInfo(promptForRequestCustomerInfo());

      logger.info("Invoking Request...");
      MerchandiseReturnResponse response = client.merchandiseReturn(request);

      logger.info("Retriving Response...");
      if (response == null) {
        logger.info("Response Not Received!");
      }
      else {
        ResponseStandardHeaderComponent standardHeader =
          response.getStandardHeader();

        if (standardHeader.getStatus().equals("E")) {
          logger.info("Printing Error...");
          printErrorMessage(response.getErrorMessage());
        }
        else {
          logger.info("Printing Response...");
          printIdentification(response.getIdentification());
          printExpirationDate(response.getExpirationDate());
          printBalances(response.getBalances());
          printCustomerDemographics(response.getCustomerInfo());
          printHostMessage(response.getHostMessage());
          printPrintCodes(response.getPrintCodes());
        }
      }
    }
    catch (Exception ex) {
      logger.info("An unexpected server error occured");
      logger.info(ex.getMessage());
      return;
    }
  }

  private static void multipleIssuance() {
    try {
      logger.info("Creating Request...");
      MultipleIssuance request = new MultipleIssuance();

      logger.info("Prompting For Request Data...");
      request.setStandardHeader(promptForRequestStandardHeader());
      request.setMultipleIssuance(promptForMultipleIssuance());
      request.setAmount(promptForAmount("USD"));
      request.setActivating(promptForActivating());

      logger.info("Invoking Request...");
      MultipleIssuanceResponse response = client.multipleIssuance(request);

      logger.info("Retriving Response...");
      if (response == null) {
        logger.info("Response Not Received!");
      }
      else {
        ResponseStandardHeaderComponent standardHeader =
          response.getStandardHeader();

        if (standardHeader.getStatus().equals("E")) {
          logger.info("Printing Error...");
          printErrorMessage(response.getErrorMessage());
        }
        else {
          logger.info("Printing Response...");
          printIdentification(response.getIdentification());
        }
      }
    }
    catch (Exception ex) {
      logger.info("An unexpected server error occured");
      logger.info(ex.getMessage());
      return;
    }
  }

  private static void hold() {
    try {
      logger.info("Creating Request...");
      Hold request = new Hold();

      logger.info("Prompting For Request Data...");
      request.setStandardHeader(promptForRequestStandardHeader());
      request.setAccount(promptForAccount());
      request.setAmount(promptForAmount("USD"));
      request.setQuestionsAndAnswers(promptForQuestionsAndAnswers());
      request.setCustomerInfo(promptForRequestCustomerInfo());

      logger.info("Invoking Request...");
      HoldResponse response = client.hold(request);

      logger.info("Retriving Response...");
      if (response == null) {
        logger.info("Response Not Received!");
      }
      else {
        ResponseStandardHeaderComponent standardHeader =
          response.getStandardHeader();

        if (standardHeader.getStatus().equals("E")) {
          logger.info("Printing Error...");
          printErrorMessage(response.getErrorMessage());
        }
        else {
          logger.info("Printing Response...");
          printIdentification(response.getIdentification());
          printExpirationDate(response.getExpirationDate());
          printBalances(response.getBalances());
          printCustomerDemographics(response.getCustomerInfo());
          printHostMessage(response.getHostMessage());
          printPrintCodes(response.getPrintCodes());
        }
      }
    }
    catch (Exception ex) {
      logger.info("An unexpected server error occured");
      logger.info(ex.getMessage());
      return;
    }
  }

  private static void holdRedemption() {
    try {
      logger.info("Creating Request...");
      HoldRedemption request = new HoldRedemption();

      logger.info("Prompting For Request Data...");
      request.setStandardHeader(promptForRequestStandardHeader());
      request.setAccount(promptForAccount());
      request.setAmount(promptForAmount("USD"));
      request.setLeavingHeld(promptForLeaveHeld());
      request.setTransactionId(promptForTransactionId());
      request.setQuestionsAndAnswers(promptForQuestionsAndAnswers());
      request.setCustomerInfo(promptForRequestCustomerInfo());

      logger.info("Invoking Request...");
      HoldRedemptionResponse response = client.holdRedemption(request);

      logger.info("Retriving Response...");
      if (response == null) {
        logger.info("Response Not Received!");
      }
      else {
        ResponseStandardHeaderComponent standardHeader =
          response.getStandardHeader();

        if (standardHeader.getStatus().equals("E")) {
          logger.info("Printing Error...");
          printErrorMessage(response.getErrorMessage());
        }
        else {
          logger.info("Printing Response...");
          printIdentification(response.getIdentification());
          printExpirationDate(response.getExpirationDate());
          printBalances(response.getBalances());
          printCustomerDemographics(response.getCustomerInfo());
          printHostMessage(response.getHostMessage());
          printPrintCodes(response.getPrintCodes());
        }
      }
    }
    catch (Exception ex) {
      logger.info("An unexpected server error occured");
      logger.info(ex.getMessage());
      return;
    }

  }

  private static void holdReturn() {
    try {
      logger.info("Creating Request...");
      HoldReturn request = new HoldReturn();

      logger.info("Prompting For Request Data...");
      request.setStandardHeader(promptForRequestStandardHeader());
      request.setAccount(promptForAccount());
      request.setAmount(promptForAmount("USD"));
      request.setTransactionId(promptForTransactionId());

      logger.info("Invoking Request...");
      HoldReturnResponse response = client.holdReturn(request);

      logger.info("Retriving Response...");
      if (response == null) {
        logger.info("Response Not Received!");
      }
      else {
        ResponseStandardHeaderComponent standardHeader =
          response.getStandardHeader();

        if (standardHeader.getStatus().equals("E")) {
          logger.info("Printing Error...");
          printErrorMessage(response.getErrorMessage());
        }
        else {
          logger.info("Printing Response...");
          printIdentification(response.getIdentification());
          printExpirationDate(response.getExpirationDate());
          printBalances(response.getBalances());
          printCustomerDemographics(response.getCustomerInfo());
          printHostMessage(response.getHostMessage());
          printPrintCodes(response.getPrintCodes());
        }
      }
    }
    catch (Exception ex) {
      logger.info("An unexpected server error occured");
      logger.info(ex.getMessage());
      return;
    }
  }

  private static void quickTransaction() {
    try {
      logger.info("Creating Request...");
      QuickTransaction request = new QuickTransaction();

      logger.info("Prompting For Request Data...");
      request.setStandardHeader(promptForRequestStandardHeader());
      request.setAccount(promptForAccount());
      request.setQuickCode(promptForQuickCode());

      logger.info("Invoking Request...");
      QuickTransactionResponse response = client.quickTransaction(request);

      logger.info("Retriving Response...");
      if (response == null) {
        logger.info("Response Not Received!");
      }
      else {
        ResponseStandardHeaderComponent standardHeader =
          response.getStandardHeader();

        if (standardHeader.getStatus().equals("E")) {
          logger.info("Printing Error...");
          printErrorMessage(response.getErrorMessage());
        }
        else {
          logger.info("Printing Response...");
          printIdentification(response.getIdentification());
          printExpirationDate(response.getExpirationDate());
          printBalances(response.getBalances());
          printCustomerDemographics(response.getCustomerInfo());
          printHostMessage(response.getHostMessage());
          printPrintCodes(response.getPrintCodes());
        }
      }
    }
    catch (Exception ex) {
      logger.info("An unexpected server error occured");
      logger.info(ex.getMessage());
      return;
    }
  }

  private static void transfer() {
    try {
      logger.info("Creating Request...");
      Transfer request = new Transfer();

      logger.info("Prompting For Request Data...");
      request.setStandardHeader(promptForRequestStandardHeader());
      request.setAccount(promptForAccount());
      request.setTransfer(promptForTransfer());
      request.setActivating(promptForActivating());
      request.setQuestionsAndAnswers(promptForQuestionsAndAnswers());
      request.setCustomerInfo(promptForRequestCustomerInfo());

      logger.info("Invoking Request...");
      TransferResponse response = client.transfer(request);

      logger.info("Retriving Response...");
      if (response == null) {
        logger.info("Response Not Received!");
      }
      else {
        ResponseStandardHeaderComponent standardHeader =
          response.getStandardHeader();

        if (standardHeader.getStatus().equals("E")) {
          logger.info("Printing Error...");
          printErrorMessage(response.getErrorMessage());
        }
        else {
          logger.info("Printing Response...");
          printIdentification(response.getIdentification());
          printExpirationDate(response.getExpirationDate());
          printBalances(response.getBalances());
          printCustomerDemographics(response.getCustomerInfo());
          printHostMessage(response.getHostMessage());
          printPrintCodes(response.getPrintCodes());
        }
      }
    }
    catch (Exception ex) {
      logger.info("An unexpected server error occured");
      logger.info(ex.getMessage());
      return;
    }
  }

  private static void renew() {
    try {
      logger.info("Creating Request...");
      Renew request = new Renew();

      logger.info("Prompting For Request Data...");
      request.setStandardHeader(promptForRequestStandardHeader());
      request.setAccount(promptForAccount());
      request.setNewExpirationDate(promptForNewExpirationDate());
      request.setQuestionsAndAnswers(promptForQuestionsAndAnswers());
      request.setCustomerInfo(promptForRequestCustomerInfo());

      logger.info("Invoking Request...");
      RenewResponse response = client.renew(request);

      logger.info("Retriving Response...");
      if (response == null) {
        logger.info("Response Not Received!");
      }
      else {
        ResponseStandardHeaderComponent standardHeader =
          response.getStandardHeader();

        if (standardHeader.getStatus().equals("E")) {
          logger.info("Printing Error...");
          printErrorMessage(response.getErrorMessage());
        }
        else {
          logger.info("Printing Response...");
          printIdentification(response.getIdentification());
          printExpirationDate(response.getExpirationDate());
          printBalances(response.getBalances());
          printCustomerDemographics(response.getCustomerInfo());
          printHostMessage(response.getHostMessage());
          printPrintCodes(response.getPrintCodes());
        }
      }
    }
    catch (Exception ex) {
      logger.info("An unexpected server error occured");
      logger.info(ex.getMessage());
      return;
    }
  }

  private static void tip() {
    try {
      logger.info("Creating Request...");
      Tip request = new Tip();

      logger.info("Prompting For Request Data...");
      request.setStandardHeader(promptForRequestStandardHeader());
      request.setAccount(promptForAccount());
      request.setAmount(promptForAmount("USD"));
      request.setQuestionsAndAnswers(promptForQuestionsAndAnswers());
      request.setSearch(promptForSearch());

      logger.info("Invoking Request...");
      TipResponse response = client.tip(request);

      logger.info("Retriving Response...");
      if (response == null) {
        logger.info("Response Not Received!");
      }
      else {
        ResponseStandardHeaderComponent standardHeader =
          response.getStandardHeader();

        if (standardHeader.getStatus().equals("E")) {
          logger.info("Printing Error...");
          printErrorMessage(response.getErrorMessage());
        }
        else {
          logger.info("Printing Response...");
          printIdentification(response.getIdentification());
          printExpirationDate(response.getExpirationDate());
          printBalances(response.getBalances());
          printAmountRemaining(response.getAmountRemaining());
          printCustomerDemographics(response.getCustomerInfo());
          printHostMessage(response.getHostMessage());
          printPrintCodes(response.getPrintCodes());
        }
      }
    }
    catch (Exception ex) {
      logger.info("An unexpected server error occured");
      logger.info(ex.getMessage());
      return;
    }
  }

  private static void voidTransaction() {
    try {
      logger.info("Creating Request...");
      VoidTransaction request = new VoidTransaction();

      logger.info("Prompting For Request Data...");
      request.setStandardHeader(promptForRequestStandardHeader());
      request.setAccount(promptForAccount());
      request.setSearch(promptForSearch());
      request.setQuestionsAndAnswers(promptForQuestionsAndAnswers());

      logger.info("Invoking Redemption...");
      VoidTransactionResponse response = client.voidTransaction(request);

      logger.info("Retriving Response...");
      if (response == null) {
        logger.info("Response Not Received!");
      }
      else {
        ResponseStandardHeaderComponent standardHeader =
          response.getStandardHeader();

        if (standardHeader.getStatus().equals("E")) {
          logger.info("Printing Error...");
          printErrorMessage(response.getErrorMessage());
        }
        else {
          logger.info("Printing Response...");
          printIdentification(response.getIdentification());
          printExpirationDate(response.getExpirationDate());
          printBalances(response.getBalances());
          printCustomerDemographics(response.getCustomerInfo());
          printHostMessage(response.getHostMessage());
          printPrintCodes(response.getPrintCodes());
        }
      }
    }
    catch (Exception ex) {
      logger.info("An unexpected server error occured");
      logger.info(ex.getMessage());
      return;
    }
  }

  private static int requestId =0;
  private static int batchId=0;
  
  private static RequestStandardHeaderComponent promptForRequestStandardHeader() {
	  
    RequestStandardHeaderComponent standardHeader = new RequestStandardHeaderComponent();

    SimpleDateFormat dateFormater = new SimpleDateFormat("yyyyMMddHHmmss");
    Calendar now = Calendar.getInstance();

    //int batchId = Integer.parseInt(sbProperties.getProperty("BatchId"));
    //int requestId = Integer.parseInt(sbProperties.getProperty("RequestId"));
    if(batchId == 100000) {
    	
    	batchId = 0;
    }
    batchId++;
    requestId++;

    int remaining = requestId;
    char[] digits = {'0','1','2','3','4','5','6','7',
      '8','9','A','B','C','D','E','F','G','H','I','J',
      'K','L','M','N','O','P','Q','R','S','T','U','V',
      'W','X','Y','Z'};
    String base36RequestId = "";
    while (0 < remaining) {
      char digit = digits[remaining % 36];
      base36RequestId = digit + base36RequestId;
      remaining = (remaining - (remaining % 36)) / 36;
    }

    // This is set in the client.properties file
    standardHeader.setRequestId(base36RequestId);
    standardHeader.setLocaleId(sbLocation.getLocaleId()); //   sbProperties.getProperty("LocaleId").toString());
    standardHeader.setSystemId(sbLocation.getSystemId()); //   sbProperties.getProperty("SystemId").toString());
    standardHeader.setClientId(sbLocation.getClientId()); //   sbProperties.getProperty("ClientId").toString());
    standardHeader.setLocationId(sbLocation.getLocationId()); // sbProperties.getProperty("LocationId").toString());
    standardHeader.setTerminalId(sbLocation.getTerminalId()); // sbProperties.getProperty("TerminalId").toString());
    standardHeader.setTerminalDateTime(dateFormater.format(now.getTime()));
    standardHeader.setInitiatorType(sbLocation.getInitiatorType()); // sbProperties.getProperty("InitiatorType").toString());
    standardHeader.setInitiatorId(sbLocation.getInitiatorId()); //  sbProperties.getProperty("InitiatorId").toString());
    standardHeader.setInitiatorPassword(sbLocation.getInitiatorPassword()); //  sbProperties.getProperty("InitiatorPassword").toString());
    standardHeader.setExternalId("");
    
    standardHeader.setBatchId(Integer.toString(batchId));
    standardHeader.setBatchReference(Integer.toString(count));

/*  sbProperties.setProperty("BatchId", Integer.toString(batchId));
    sbProperties.setProperty("RequestId", Integer.toString(requestId));

    try {
      FileOutputStream output = new FileOutputStream(
        new File("src/conf/client.properties"));

      sbProperties.store(output, "");
      output.close();
    }
    catch (FileNotFoundException e) {
      logger.error("Exception ::", e);
    }
    catch (IOException e) {
      logger.error("Exception ::", e);
    }*/

    return standardHeader;
  }
  

  private static AccountComponent promptForAccount() {
    AccountComponent account = new AccountComponent();
    
 /*   account.setAccountId(prompt("Enter the account id > "));
    account.setPin(prompt("Enter the account pin > "));
    account.setEntryType("K");*/

    account.setAccountId(contactLoyalty.getCardNumber());
    
    String pin = contactLoyalty.getCardPin();
    
    if(pin != null && !pin.trim().isEmpty()) {
    account.setPin(contactLoyalty.getCardPin());
    }
    //account.setPin(prompt("Enter the account pin > "));
    account.setEntryType("K");

    return account;
  }

  private static CustomerInfoComponent promptForRequestCustomerInfo() {
    CustomerInfoComponent customerInfo = new CustomerInfoComponent();

    String enter = "N";//prompt("Enter customer demographics (Y/N) > ", "^[YyNn]$" );
    if (enter.equals("Y") || enter.equals("y")) {
      customerInfo.setCustomerType(prompt(
        "Enter the customer type (1/2/G) > ", "^[12Gg]?$"));
      customerInfo.setFirstName(prompt(
        "Enter the customer's first name > "));
      customerInfo.setMiddleName(prompt(
        "Enter the customer's middle name> "));
      customerInfo.setLastName(prompt(
        "Enter the customer's last name > "));
      customerInfo.setAddress1(prompt(
        "Enter the customer's street address > "));
      customerInfo.setAddress2(prompt(
        "Enter the customer's appartment number > "));
      customerInfo.setCity(prompt(
        "Enter the customer's city > "));
      customerInfo.setState(prompt(
        "Enter the customer's state > "));
      customerInfo.setPostal(prompt(
        "Enter the customer's postal > "));
      customerInfo.setCountry(prompt(
        "Enter the customer's country > "));
      customerInfo.setMailPref(prompt(
        "Enter the customer's mail preference (I/O)  > ", "^[IiOo]?$"));
      customerInfo.setPhone(prompt(
        "Enter the customer's phone number > "));
      customerInfo.setIsMobile(prompt(
        "Enter the is the phone mobile > "));
      customerInfo.setPhonePref(prompt(
        "Enter the customer's phone preference (I/O)  > ", "^[IiOo]?$"));
      customerInfo.setEmail(prompt(
        "Enter the customer's e-mail address > "));
      customerInfo.setEmailPref(prompt(
        "Enter the e-mail preference (I/O) > ", "^[IiOo]?$"));
      customerInfo.setBirthday(prompt(
        "Enter the customer's birthday > "));
      customerInfo.setAnniversary(prompt(
        "Enter the customer's anniversary > "));
      customerInfo.setGender(prompt(
        "Enter the customer's gender (M/F) > ", "^[MmFf]?$"));
    }
    else {
      customerInfo.setCustomerType("");
      customerInfo.setFirstName(contact.getFirstName()==null || contact.getFirstName().trim().isEmpty() ? "" : contact.getFirstName());
      customerInfo.setMiddleName("");
      customerInfo.setLastName(contact.getLastName() == null || contact.getLastName().trim().isEmpty() ? "" : contact.getLastName());
      customerInfo.setAddress1(contact.getAddressOne() == null || contact.getAddressOne().trim().isEmpty() ? "" : contact.getAddressOne());
      customerInfo.setAddress2(contact.getAddressTwo() == null || contact.getAddressTwo().trim().isEmpty() ? "" : contact.getAddressTwo());
      customerInfo.setCity(contact.getCity() == null || contact.getCity().trim().isEmpty() ? "" : contact.getCity());
      customerInfo.setState(contact.getState() == null || contact.getState().trim().isEmpty() ? "" : contact.getState());
      customerInfo.setPostal("");
      customerInfo.setCountry(contact.getCountry() == null || contact.getCountry().trim().isEmpty() ? "" : contact.getCountry());
      customerInfo.setMailPref("");
      //customerInfo.setPhone(contact.getPhone() == null || contact.getPhone().longValue()==0 ? "" : contact.getPhone() + "");
 	  customerInfo.setPhone(contact.getMobilePhone() == null || contact.getMobilePhone().trim().length() ==0 ? "" : contact.getMobilePhone() + "");      
	  customerInfo.setIsMobile("");
      customerInfo.setPhonePref("");
      customerInfo.setEmail(contact.getEmailId()==null || contact.getEmailId().trim().isEmpty() ? "" : contact.getEmailId());
      customerInfo.setEmailPref("");

      customerInfo.setBirthday("");
      if(contact.getBirthDay() != null) {
    	  String retStr = MyCalendar.calendarToString(contact.getBirthDay(), MyCalendar.FORMAT_SB_DATEONLY);
    	  if(!retStr.equals("--")) customerInfo.setBirthday(retStr);
      }
   
      customerInfo.setAnniversary("");
      if(contact.getAnniversary() != null) {
    	  String retStr = MyCalendar.calendarToString(contact.getAnniversary(), MyCalendar.FORMAT_SB_DATEONLY);
    	  if(!retStr.equals("--")) customerInfo.setAnniversary(retStr);
      }
      
      
      customerInfo.setGender("");
      if(contact.getGender() != null && contact.getGender().trim().length()>0) {
    	  char ch = contact.getGender().toUpperCase().charAt(0);
    	  if(ch=='M' || ch=='F') customerInfo.setGender(""+ch); 
      }
      
    	
    /*	customerInfo.setCustomerType("");
        customerInfo.setFirstName("");
        customerInfo.setMiddleName("");
        customerInfo.setLastName("");
        customerInfo.setAddress1("");
        customerInfo.setAddress2("");
        customerInfo.setCity("");
        customerInfo.setState("");
        customerInfo.setPostal("");
        customerInfo.setCountry("");
        customerInfo.setMailPref("");
        customerInfo.setPhone("");
        customerInfo.setIsMobile("");
        customerInfo.setPhonePref("");
        customerInfo.setEmail(contact.getEmailId());
        customerInfo.setEmailPref("");
        customerInfo.setBirthday("");
        customerInfo.setAnniversary("");
        customerInfo.setGender("");*/
    }

    return customerInfo;
  }

  private static MultipleIssuanceComponent promptForMultipleIssuance() {
    MultipleIssuanceComponent multipleIssuances =
      new MultipleIssuanceComponent();
    ArrayOfMultipleIssuanceAccount accounts =
      new ArrayOfMultipleIssuanceAccount();

    multipleIssuances.setSpecifiedBy(prompt(
      "Enter the account specification stlye (I/R) > ", "^[IiRr]$"));

    String enter = prompt(
      "Add a multiple issuance account (Y/N) > ", "^[YyNn]$" );
    while (enter.equals("Y") || enter.equals("y")) {
      MultipleIssuanceAccount account = new MultipleIssuanceAccount();

      account.setAccountId(prompt("Enter account id > "));
      account.setPin(prompt("Enter pin > "));
      account.setEntryType("K");
      accounts.getAccount().add(account);

      enter = prompt(
        "Add a multiple issuance account (Y/N) > ", "^[YyNn]$");
    }

    multipleIssuances.setAccounts(accounts);

    return multipleIssuances;
  }

  private static String promptForNewExpirationDate() {
    return prompt("Enter the new expiration date > ", "^([0-9]{8})?$");
  }

  private static String promptForQuickCode() {
    return prompt("Enter the quick code > ");
  }

  private static ReportComponent promptForReport() {
    ReportComponent report = new ReportComponent();

    report.setType("S"); // prompt("Enter the report type (S/D) > ", "^[SsDd]?$"));
    report.setMinimumDate(""); // prompt("Enter the minimum date > ", "^([0-9]{8})?$"));
    report.setMaximumDate(""); // prompt("Enter the maximum date > ", "^([0-9]{8})?$"));
    report.setOffset(""); // prompt("Enter the offset > ", "^[0-9]{0,2}$"));
    report.setMaxRecords(""); // prompt("Enter the maximum records > ", "^[0-9]{0,2}$"));

    return report;
  }

  private static String promptForReportOnEmployee() {
     return prompt("Enter the employee id > ");
  }

  private static SearchComponent promptForSearch() {
    SearchComponent transaction = new SearchComponent();

    transaction.setBatchId(prompt("Enter the batch id > "));
    transaction.setBatchReference(prompt("Enter the batch reference > "));
    transaction.setTransactionId(prompt("Enter the transaction id> "));
    transaction.setApprovalCode(prompt("Enter the approval code > "));

    return transaction;
  }

  private static TransferComponent promptForTransfer() {
    TransferComponent transfer = new TransferComponent();

    transfer.setDestAccountId(prompt("Enter the destination account id > "));
    transfer.setDestPin(prompt("Enter the destination account pin > "));
    transfer.setDestEntryType("K");
    transfer.setCloseReason("");
    transfer.setValueCode("USD");
    transfer.setEnteredAmount(prompt("Enter the transfer amount > "));

    return transfer;
  }

  private static String promptForActivating() {
    return "Y"; //prompt("Activating (Y/N) > ", "^[YyNn]$");
  }

  private static AmountComponent promptForAmount(String aValueCode) {
    AmountComponent amount = new AmountComponent();

    amount.setValueCode(enteredAmount[0]);
    amount.setEnteredAmount(enteredAmount[1]);// prompt("Enter the amount > "));
    //amount.setNsfAllowed(prompt("Allow NSF  (Y/N) >", "^[YyNn]$"));

    return amount;
  }

  private static String promptForIncludeTip() {
    return prompt("Include tip in transaction (Y/N) > " );
  }

  private static String promptForLeaveHeld() {
    return prompt("Leave Held (Y/N) > ", "^[YyNn]?$");
  }

  private static String promptForTransactionId() {
    return prompt("Transaction Id > ", "^[YyNn]?$");
  }

  private static ArrayOfPromotionCode promptForPromotionCodes() {
    ArrayOfPromotionCode promotionCodes = new ArrayOfPromotionCode();

    String enter = "N";// prompt("Add a promotion code (Y/N) > ", "^[YyNn]$");
    while (enter.equals("Y") || enter.equals("y")) {
      PromotionCode promotionCode = new PromotionCode();

      promotionCode.setCode(prompt("Enter code > "));
      promotionCode.setQuantity(prompt("Enter quantity > ", "^[0-9]{0,2}$"));

      promotionCodes.getPromotionCode().add(promotionCode);

      enter = prompt("Add a promotion code (Y/N) > ", "^[YyNn]$");
    }

    return promotionCodes;
  }

  private static ArrayOfQuestionAndAnswer promptForQuestionsAndAnswers() {
    ArrayOfQuestionAndAnswer questionsAndAnswers =
      new ArrayOfQuestionAndAnswer();

    String enter = "N"; // prompt("Add a question and answer (Y/N) > ", "^[YyNn]$");
    while (enter.equals("Y") || enter.equals("y") ) {
      QuestionAndAnswer questionAndAnswer = new QuestionAndAnswer();

      questionAndAnswer.setCode(prompt("Enter code > "));
      questionAndAnswer.setAnswer(prompt("Enter answer > "));

      questionsAndAnswers.getQuestionAndAnswer().add(questionAndAnswer);

      enter = prompt("Add a question and answer (Y/N) > ", "^[YyNn]$");
    }

    return questionsAndAnswers;
  }

  public static void printHeader(ResponseStandardHeaderComponent aHeader) {
    logger.info("Standard Header");
    logger.info("  Request Id = " + aHeader.getRequestId() + "");
    logger.info("  Status = " + aHeader.getStatus() + "");
  }

  public static void printErrorMessage(ErrorMessageComponent aErrorMessage) {
    logger.info("Error Message");
    logger.info(
      "  Rejection Id = " + aErrorMessage.getRejectionId() + "");
    logger.info(
      "  Error Code = " + aErrorMessage.getErrorCode() + "");
    logger.info(
      "  Brief Message = " + aErrorMessage.getBriefMessage() + "");
    logger.info(
      "  In-Depth Message = " + aErrorMessage.getInDepthMessage() + "");
  }

  public static void printPrintableSegment(String aPrintableSegment) {
    logger.info("Printable Segment");
    logger.info("" + aPrintableSegment + "");
    
    if(updateLoyaltyData==false) return;
    String amount=null;
    
    String lines[] = aPrintableSegment.split("\n");
    for (String eachLine : lines) {
    	eachLine = eachLine.toUpperCase().trim();
		logger.info("===>"+eachLine);
	
		if(eachLine.contains("(G)IFT (I)SS:")) {
			amount = eachLine.substring(eachLine.lastIndexOf(' ')).trim();
			contactLoyalty.setTotalGiftcardAmount(Double.parseDouble(amount));
		}
		else if(eachLine.contains("(G)IFT (R)DMP:")) {
			amount = eachLine.substring(eachLine.lastIndexOf(' ')).trim();
			contactLoyalty.setTotalGiftcardRedemption(Double.parseDouble(amount));
		} 
		else if(eachLine.contains("(L)OY (I)SS:")) {
			amount = eachLine.substring(eachLine.lastIndexOf(' ')).trim();
			contactLoyalty.setTotalLoyaltyEarned(Double.parseDouble(amount));
		} 
		else if(eachLine.contains("(L)OY (R)DMP:")) {
			amount = eachLine.substring(eachLine.lastIndexOf(' ')).trim();
			contactLoyalty.setTotalLoyaltyRedemption(Double.parseDouble(amount));
		} 
	}
  }

  public static void printIdentification(
      IdentificationComponent aIdentification) {
    logger.info("Identification");
    logger.info(
      "  Transaction Id = " + aIdentification.getTransactionId() + "");
    logger.info(
      "  Approval Code = " + aIdentification.getApprovalCode() + "");
    logger.info(
      "  Demonstration = " + aIdentification.getDemonstration() + "");
  }

  public static void printCustomerDemographics(
      CustomerInfoComponent aCustomerInfo) {
    logger.info("Customer Demographics");
    logger.info(
      "  Customer Type = " + aCustomerInfo.getCustomerType() + "");
    logger.info(
      "  First Name = " + aCustomerInfo.getFirstName() + "");
    logger.info(
      "  Middle Name = " + aCustomerInfo.getMiddleName() + "");
    logger.info(
      "  Last Name = " + aCustomerInfo.getLastName() + "");
    logger.info(
      "  Address 1 = " + aCustomerInfo.getAddress1() + "");
    logger.info(
      "  Address 2 = " + aCustomerInfo.getAddress2() + "");
    logger.info(
      "  City = " + aCustomerInfo.getCity() + "");
    logger.info(
      "  State = " + aCustomerInfo.getState() + "");
    logger.info(
      "  Postal = " + aCustomerInfo.getPostal() + "");
    logger.info(
      "  Country = " + aCustomerInfo.getCountry() + "");
    logger.info(
      "  Mail Pref = " + aCustomerInfo.getMailPref() + "");
    logger.info(
      "  Phone = " + aCustomerInfo.getPhone() + "");
    logger.info(
      "  Is Mobile = " + aCustomerInfo.getIsMobile() + "");
    logger.info(
      "  Phone Pref = " + aCustomerInfo.getPhonePref() + "");
    logger.info(
      "  E-Mail = " + aCustomerInfo.getEmail() + "");
    logger.info(
      "  E-Mail Pref = " + aCustomerInfo.getEmailPref() + "");
    logger.info(
      "  Birthday = " + aCustomerInfo.getBirthday() + "");
    logger.info(
      "  Anniversary = " + aCustomerInfo.getAnniversary() + "");
    logger.info(
      "  Gender = " + aCustomerInfo.getGender() + "");
  }


  public static void printBalances(ArrayOfBalance aBalances) {
    if (aBalances != null) {
      logger.info("Balances");
      String valueCode = null;
      String amount = null;
      for (int i = 0; i < aBalances.getBalance().size(); i++) {
    	  valueCode = aBalances.getBalance().get(i).getValueCode();
    	  amount = aBalances.getBalance().get(i).getAmount();
    	  
        logger.info("  Value Code = " + valueCode);
        logger.info("  Amount = " + amount);
        logger.info("  Difference = " + aBalances.getBalance().get(i).getDifference());
        logger.info("  Exchange Rate = " + aBalances.getBalance().get(i).getExchangeRate());
        
        if(updateLoyaltyData && valueCode.equalsIgnoreCase("Points")) {
        	contactLoyalty.setLoyaltyBalance(Double.parseDouble(amount));
        } 
        else if(updateLoyaltyData) {
        	contactLoyalty.setGiftcardBalance(Double.parseDouble(amount));
        	contactLoyalty.setValueCode(valueCode);
        } 
        
      }
    }
  }


  public static void printPrintCodes(ArrayOfPrintCode aPrintCodes) {
    if (aPrintCodes != null) {
      logger.info("Print Codes");
      for (int i = 0; i < aPrintCodes.getPrintCode().size(); i++) {
        logger.info(
          "  Code = " + aPrintCodes.getPrintCode().get(i).getCode());
      }
    }
  }


  public static void printExpirationDate(String aExpirationDate) {
    logger.info("Expiration Date");
    logger.info("  Date = " + aExpirationDate);
  }


  public static void printAmountRemaining(String aAmountRemaining) {
    logger.info("Amount Remaining");
    logger.info("  Amount = " + aAmountRemaining);
  }


  public static void printHostMessage(String aHostMessage) {
    logger.info("Host Message");
    logger.info("  Message = " + aHostMessage);
  }


  private static String prompt(String aMessage) {
    String input="";

    logger.debug(aMessage);
    try {
      //input = in.readLine();
    }
    catch (Exception ex)  {
      return "";
    }

    return input;
  }


  private static String prompt(String aMessage, String aValidationPatter) {
    String input="";

    logger.debug(aMessage);
/*    try {
      input = in.readLine();
      while (!Pattern.matches(aValidationPatter, input)) {
        logger.info( "Invalid Input!" );
        logger.debug(aMessage);
        input = in.readLine();
      }
    }
    catch (Exception ex)  {
      return "";
    }
*/
    return input;
  }
}
