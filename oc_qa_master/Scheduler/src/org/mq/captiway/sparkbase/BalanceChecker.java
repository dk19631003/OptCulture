package org.mq.captiway.sparkbase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.utility.Constants;

import org.mq.captiway.sparkbase.transactionWsdl.*;



import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

import javax.xml.ws.BindingProvider;

public class BalanceChecker {
	
	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	
  private static int count;
  private static SparkbaseTransactionWsdlImplService service;
  private static SparkbaseTransactionWsdlPort client;
  private static BufferedReader in;
  private static Properties properties;

  public static void main(String[] args) {
    properties = new Properties();
    try {
      FileInputStream input = new FileInputStream(
        new File("src/conf/client.properties"));
      properties.load(input);
      input.close();
    }
    catch (FileNotFoundException e) {
      logger.error("Exception ::::" , e);
      return;
    }
    catch (IOException e) {
      logger.error("Exception ::::" , e);
      return;
    }

    logger.info("Starting Proxy Client...");

    //Get the service
    service = new SparkbaseTransactionWsdlImplService();

    //Get the port
    client = service.getPort(SparkbaseTransactionWsdlPort.class);

    //Configure RequestContext
    Map<String, Object> rc = ((BindingProvider)client).getRequestContext();
    // TEST: This data goes a test location on the a local transaction server
    // DO NOT USE IN PRODUCTION
    rc.put(BindingProvider.USERNAME_PROPERTY, properties.get(
      "IntegrationUserName"));
    rc.put(BindingProvider.PASSWORD_PROPERTY, properties.get(
      "IntegrationPassword").toString());

    count = 0;

    InputStreamReader inStream = new InputStreamReader(System.in);
    in = new BufferedReader(inStream);

    while (true) {
      count++;
      try {
        inquiry();
      }
      catch (Exception ex) {
        logger.info("The selected transaction is currently unavaible");
      }

      prompt("Press Enter To Continue...");
    }
  }

  private static void inquiry() {
    try {
      logger.info("Creating Request...");
      Inquiry request = new Inquiry();

      logger.info("Prompting For Request Data...");
      request.setStandardHeader(promptForRequestStandardHeader());
      request.setAccount(promptForAccount());
      request.setQuestionsAndAnswers(promptForQuestionsAndAnswers());

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
        }
        else {
          logger.info("Printing Response...");
          printCustomerDemographics(response.getCustomerInfo());
          printBalances(response.getBalances());
          printHostMessage(response.getHostMessage());
        }
      }
    }
    catch (Exception ex) {
      logger.info("An unexpected server error occured");
      logger.info(ex.getMessage());
      return;
    }
  }

  private static RequestStandardHeaderComponent
    promptForRequestStandardHeader() {
    RequestStandardHeaderComponent standardHeader =
      new RequestStandardHeaderComponent();

    SimpleDateFormat dateFormater = new SimpleDateFormat("yyyyMMddHHmmss");
    Calendar now = Calendar.getInstance();

    int batchId = Integer.parseInt(properties.getProperty("BatchId"));
    int requestId = Integer.parseInt(properties.getProperty("RequestId"));

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
    standardHeader.setLocaleId(
      properties.getProperty("LocaleId").toString());
    standardHeader.setSystemId(
      properties.getProperty("SystemId").toString());
    standardHeader.setClientId(
      properties.getProperty("ClientId").toString());
    standardHeader.setLocationId(
      properties.getProperty("LocationId").toString());
    standardHeader.setTerminalId(
      properties.getProperty("TerminalId").toString());
    standardHeader.setTerminalDateTime(dateFormater.format(now.getTime()));
    standardHeader.setExternalId("");
    standardHeader.setBatchId(Integer.toString(batchId));
    standardHeader.setBatchReference(Integer.toString(count));

    properties.setProperty("BatchId", Integer.toString(batchId));
    properties.setProperty("RequestId", Integer.toString(requestId));

    try {
      FileOutputStream output = new FileOutputStream(
        new File("src/conf/client.properties"));

      properties.store(output, "");
      output.close();
    }
    catch (FileNotFoundException e) {
      logger.error("Exception ::::" , e);
    }
    catch (IOException e) {
      logger.error("Exception ::::" , e);
    }

    return standardHeader;
  }

  private static AccountComponent promptForAccount() {
    AccountComponent account = new AccountComponent();

    account.setAccountId(prompt("Enter the account id > "));
    account.setPin(prompt("Enter the account pin > "));
    account.setEntryType("K");

    return account;
  }

  private static ArrayOfQuestionAndAnswer promptForQuestionsAndAnswers() {
    ArrayOfQuestionAndAnswer questionsAndAnswers =
      new ArrayOfQuestionAndAnswer();
    return questionsAndAnswers;
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

  public static void printCustomerDemographics(
      CustomerInfoComponent aCustomerInfo) {
    logger.info(aCustomerInfo.getFirstName() + " "
      + aCustomerInfo.getMiddleName() + " "
      + aCustomerInfo.getLastName());
  }


  public static void printBalances(ArrayOfBalance aBalances) {
    if (aBalances != null) {
      for (int i = 0; i < aBalances.getBalance().size(); i++) {
        logger.info(aBalances.getBalance().get(i).getAmount()
          + " " + aBalances.getBalance().get(i).getValueCode());
      }
    }
  }


  public static void printHostMessage(String aHostMessage) {
    logger.info(aHostMessage);
  }


  private static String prompt(String aMessage) {
    String input;

    logger.debug(aMessage);
    try {
      input = in.readLine();
    }
    catch (Exception ex)  {
      return "";
    }

    return input;
  }


  private static String prompt(String aMessage, String aValidationPatter) {
    String input;

    logger.debug(aMessage);
    try {
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

    return input;
  }
}
