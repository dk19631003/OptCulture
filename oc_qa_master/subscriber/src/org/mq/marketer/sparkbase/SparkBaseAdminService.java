package org.mq.marketer.sparkbase;

import java.util.Calendar;
import java.util.List;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.ContactsLoyalty;
import org.mq.marketer.campaign.beans.SparkBaseCard;
import org.mq.marketer.campaign.controller.service.UpdateSparkBaseCardsController;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.sparkbase.sparkbaseAdminWsdl.Account;
import org.mq.marketer.sparkbase.sparkbaseAdminWsdl.AccountsView;
import org.mq.marketer.sparkbase.sparkbaseAdminWsdl.AccountsViewResponse;
import org.mq.marketer.sparkbase.sparkbaseAdminWsdl.ArrayOfAccount;
import org.mq.marketer.sparkbase.sparkbaseAdminWsdl.ArrayOfBalance;
import org.mq.marketer.sparkbase.sparkbaseAdminWsdl.ArrayOfCard;
import org.mq.marketer.sparkbase.sparkbaseAdminWsdl.Balance;
import org.mq.marketer.sparkbase.sparkbaseAdminWsdl.BalancesView;
import org.mq.marketer.sparkbase.sparkbaseAdminWsdl.BalancesViewResponse;
import org.mq.marketer.sparkbase.sparkbaseAdminWsdl.Card;
import org.mq.marketer.sparkbase.sparkbaseAdminWsdl.CardsView;
import org.mq.marketer.sparkbase.sparkbaseAdminWsdl.CardsViewResponse;
import org.mq.marketer.sparkbase.sparkbaseAdminWsdl.Customer;
import org.mq.marketer.sparkbase.sparkbaseAdminWsdl.CustomersView;
import org.mq.marketer.sparkbase.sparkbaseAdminWsdl.CustomersViewResponse;
import org.mq.marketer.sparkbase.sparkbaseAdminWsdl.RequestHeader;
import org.mq.marketer.sparkbase.sparkbaseAdminWsdl.SparkbaseAdminV45WsdlImplService;
import org.mq.marketer.sparkbase.sparkbaseAdminWsdl.SparkbaseAdminV45WsdlPort;

public class SparkBaseAdminService {

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	public static final String BALANCESVIEW = "BALANCESVIEW";
	public static final String CUSTOMERINFO = "CUSTOMERINFO";
	public static final String CARDSVIEW = "CARDSVIEW";
	

	private static SparkbaseAdminV45WsdlImplService service;
	private static SparkbaseAdminV45WsdlPort client;
	private static Properties properties;

	private static ContactsLoyalty contactLoyalty;
	private static boolean updateLoyaltyData;

	public static synchronized Object fetchData(String requestType,
			String accountId, ContactsLoyalty contactLoyaltyObj,
			boolean updateLoyaltyDataFlag) {

		try {
			contactLoyalty = contactLoyaltyObj;
			updateLoyaltyData = updateLoyaltyDataFlag;

			properties = new Properties();
			try {
				properties.put("sbAdminClientId",PropertyUtil.getPropertyValueFromDB("sbAdminClientId"));
				properties.put("sbAdminIntegrationAuth", PropertyUtil.getPropertyValueFromDB("sbAdminIntegrationAuth"));
				properties.put("sbAdminIntegrationPass", PropertyUtil.getPropertyValueFromDB("sbAdminIntegrationPass"));
				properties.put("sbAdminUsername",PropertyUtil.getPropertyValueFromDB("sbAdminUsername"));
				properties.put("sbAdminPassword",PropertyUtil.getPropertyValueFromDB("sbAdminPassword"));
				properties.put("sbAdminInitiatorIP", PropertyUtil.getPropertyValueFromDB("sbAdminInitiatorIP"));
			} catch (Exception e) {
				logger.error("Exception ::" , e);
				return null;
			}

			logger.debug("Starting Admin Service...");
			if (service == null) {
				service = new SparkbaseAdminV45WsdlImplService();
			}

			logger.debug("Starting Admin Client ...");
			client = service.getSparkbaseAdminV45WsdlPortPort();

			Object object = null;
			if (requestType.equals(BALANCESVIEW)) {
				object = adminBalancesView(accountId);
			} else if (requestType.equals(CUSTOMERINFO)) {
				object = customerInfo(accountId);
			}

			return object;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception in SB Admin fetchData " + e);
		}
		return null;

	} // fetchBalancesData

	public static Object cardsView(String cardID, SparkBaseCard sbCard) {
		
		try {
			logger.debug("entered into adminbalaceview...");
			
			//create properties
			if(properties == null) {
				properties = new Properties();
				try {
					properties.put("sbAdminClientId",PropertyUtil.getPropertyValueFromDB("sbAdminClientId"));
					properties.put("sbAdminIntegrationAuth", PropertyUtil.getPropertyValueFromDB("sbAdminIntegrationAuth"));
					properties.put("sbAdminIntegrationPass", PropertyUtil.getPropertyValueFromDB("sbAdminIntegrationPass"));
					properties.put("sbAdminUsername",PropertyUtil.getPropertyValueFromDB("sbAdminUsername"));
					properties.put("sbAdminPassword",PropertyUtil.getPropertyValueFromDB("sbAdminPassword"));
					properties.put("sbAdminInitiatorIP", PropertyUtil.getPropertyValueFromDB("sbAdminInitiatorIP"));
				} catch (Exception e) {
					logger.error("Exception ::" , e);
					return null;
				}
			}
			logger.debug("Starting Admin Service...");
			if (service == null) {
				service = new SparkbaseAdminV45WsdlImplService();
			}

			logger.debug("Starting Admin Client ...");
			client = service.getSparkbaseAdminV45WsdlPortPort();

			
			
			
			
			Card cardToGet = new Card();
			cardToGet.setCardId(cardID);
			
			CardsView cardToView = new CardsView();
			
			cardToView.setHeader(promptForHeader());
			cardToView.setCard(cardToGet);
			logger.debug("Connecting to cardsView API");
			CardsViewResponse cardViewResponse = client.cardsView(cardToView);
			logger.debug("Retrieving CardsView Response");
			
			if (cardViewResponse == null) {
				logger.debug("cardView Response Not Received!");
				return null;
			}
			else {
				printRequestedCard(cardViewResponse.getCards(),  sbCard, cardID);
			}

			return cardViewResponse;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception in SB Admin CardsView method " , e);
		}
		return null;
	}
	
	
	public static Object adminBalancesView(String accountId) {
		logger.debug("entered into adminbalaceview...");
		try {
			
			Balance balance = new Balance();
			balance.setAccountId(accountId);
			
			BalancesView requestBal = new BalancesView();
			requestBal.setHeader(promptForHeader());
			
			requestBal.setBalance(balance);
			logger.debug("Connecting to balancesView API");
			BalancesViewResponse balanceResponse = client.balancesView(requestBal);

			logger.debug("Retriving BalancesView Response");
			if (balanceResponse == null) {
				logger.debug("balance Response Not Received!");
				return null;
			}

			else {
				printBalances(balanceResponse.getBalances());
			}

			return balanceResponse;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception in SB Admin adminBalancesView method " + e);
		}
		return null;

	}// adminBalancesView

	public static Object customerInfo(String accountId) {
		logger.debug("Entered into customerinfo ...");

		String primaryCustId = null;

		// AccountsView API
		try {
			
			Account account = new Account();
			account.setAccountId(accountId);
			
			AccountsView accountView = new AccountsView();
			accountView.setHeader(promptForHeader());
			accountView.setAccount(account);
			
			AccountsViewResponse accountResponse = client.accountsView(accountView);
			if (accountResponse != null) {
				ArrayOfAccount aAccounts = accountResponse.getAccounts();
				primaryCustId = aAccounts.getAccounts().get(0).getPrimaryCustomerId();

				// CustomersView API
				Customer customer = new Customer();
				customer.setCustomerId(primaryCustId);
				
				CustomersView customersView = new CustomersView();
				customersView.setHeader(promptForHeader());
				customersView.setCustomer(customer);

				CustomersViewResponse custResponse = client.customersView(customersView);

				if (custResponse != null) {

					return custResponse;
				} else {
					logger.debug("CustomersViewResponse Object is null with accountId::"
									+ accountId+ " and CustId:: "+ primaryCustId);
					return null;
				}
			}
			logger.debug("AccountsViewResponse Object is null with accountId :: "+ accountId);
			return null;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception in SB Admin customerInfo" + e);
			return null;
		}
	}// customerInfo

	public static void printBalances(ArrayOfBalance aBalances) {
		if (aBalances != null) {
			logger.debug("Balances");
			String valueCode = null;
			String amount = null;
			for (int i = 0; i < aBalances.getBalances().size(); i++) {
				valueCode = aBalances.getBalances().get(i).getType();
				amount = aBalances.getBalances().get(i).getBalance();

				logger.debug("  Value Code = " + valueCode);
				logger.debug("  Amount = " + amount);

				if (updateLoyaltyData && valueCode.equalsIgnoreCase("Points")) {
					contactLoyalty.setLoyaltyBalance(Double.parseDouble(amount));
					contactLoyalty.setLastFechedDate(Calendar.getInstance());
				} else if (updateLoyaltyData && valueCode.equalsIgnoreCase("Currency")) {
					contactLoyalty.setGiftcardBalance(Double.parseDouble(amount));
					contactLoyalty.setValueCode(aBalances.getBalances().get(i).getCurrency());
					contactLoyalty.setLastFechedDate(Calendar.getInstance());
				}

			}
		}
	}// printBalances

	public static SparkBaseCard printRequestedCard(ArrayOfCard arrOfCard, SparkBaseCard sbCard, String requestedCard) {
		if (arrOfCard != null) {
			
			logger.debug("cards");
			
			Card eachCard = null;
			
			List<Card> responseCardList = arrOfCard.getCards();
			for (int i = 0; i < responseCardList.size(); i++) {
			
				eachCard = responseCardList.get(i);
				if(eachCard.getCardId().trim().equals(requestedCard)) {
					
					logger.debug("  PIN = " + eachCard.getPin());
					sbCard.setCardPin(eachCard.getPin());
					return sbCard;
				}
				

			}//for
			
		}//if
		
		return null;
	}// printBalances
	
	
	
	
	private static RequestHeader promptForHeader() {
		RequestHeader header = new RequestHeader();
		logger.debug("Admin Api RequestHeader Prepration...");
		header.setClient(properties.getProperty("sbAdminClientId"));
		header.setIntegrationAuth(properties.getProperty("sbAdminIntegrationAuth"));
		header.setIntegrationPass(properties.getProperty("sbAdminIntegrationPass"));
		header.setInitiatorType("user");
		header.setInitiatorId(properties.getProperty("sbAdminUsername"));
		header.setInitiatorPass(properties.getProperty("sbAdminPassword"));
		header.setInitiatorIP(properties.getProperty("sbAdminInitiatorIP"));

		return header;
	}// promptForHeader

}
