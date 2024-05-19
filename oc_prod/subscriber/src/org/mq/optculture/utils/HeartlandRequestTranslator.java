package org.mq.optculture.utils;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.general.Constants;
import org.mq.optculture.model.DR.DRBody;
import org.mq.optculture.model.DR.DRHead;
import org.mq.optculture.model.DR.DRItem;
import org.mq.optculture.model.DR.DROptCultureDetails;
import org.mq.optculture.model.DR.DigitalReceipt;
import org.mq.optculture.model.DR.Receipt;
import org.mq.optculture.model.DR.User;
import org.mq.optculture.model.DR.heartland.CustomerAddress;
import org.mq.optculture.model.DR.heartland.DetailResults;
import org.mq.optculture.model.DR.heartland.HeartlandCustomerDetails;
import org.mq.optculture.model.DR.heartland.HeartlandDRBody;
import org.mq.optculture.model.DR.heartland.HeartlandDRRequest;
import org.mq.optculture.model.DR.heartland.HeartlandDetails;
import org.mq.optculture.model.DR.heartland.HeartlandTicketDetails;
import org.mq.optculture.model.DR.heartland.PaymentResult;
import org.mq.optculture.model.DR.tender.COD;
import org.mq.optculture.model.DR.tender.Cash;
import org.mq.optculture.model.DR.tender.Charge;
import org.mq.optculture.model.DR.tender.Check;
import org.mq.optculture.model.DR.tender.CreditCard;
import org.mq.optculture.model.DR.tender.DebitCard;
import org.mq.optculture.model.DR.tender.Deposit;
import org.mq.optculture.model.DR.tender.FC;
import org.mq.optculture.model.DR.tender.FCCheck;
import org.mq.optculture.model.DR.tender.Gift;
import org.mq.optculture.model.DR.tender.GiftCard;
import org.mq.optculture.model.DR.tender.Payments;
import org.mq.optculture.model.DR.tender.StoreCredit;
import org.mq.optculture.model.DR.tender.TravellerCheck;

import com.google.gson.Gson;

public class HeartlandRequestTranslator {

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);

	
	
	public HeartlandRequestTranslator() {

	}
	public DigitalReceipt convertHeartlandRequest(HeartlandDRRequest heartlandRequest, Users userObj) {

		logger.info("Heartland translator.DigitalReceipt.convertHeartlandRequest");
		DigitalReceipt digitalReceipt = new DigitalReceipt();
		HeartlandDRBody heartlandDRBody = heartlandRequest.getBody();
		HeartlandTicketDetails heartlandTicketDetails = heartlandDRBody.getTicket();
		HeartlandCustomerDetails heartlandCustomerDetails = heartlandDRBody.getCustomer();
		CustomerAddress addressDetails = heartlandCustomerDetails != null ? heartlandCustomerDetails.getAddress() : null;
		List<DetailResults> lineItems = heartlandDRBody.getDetails().getResults();
		//List<RefundItems> refunLineItems = shopifyDRBody.getReceipt().getRefund_line_items();
		//String strItemsCount = heartlandTicketDetails!=null ? heartlandTicketDetails.getTotal_item_qty() + "":"";
		//String itemsCount = heartlandDRBody.getDetails()!=null ? heartlandDRBody.getDetails().getTotal() + "":"";
		int itemsCount=0;
		int invcTotalQty=0;

		DROptCultureDetails heartlandOCDetails = heartlandRequest.getOptcultureDetails();
		User heartlandUser = heartlandRequest.getHead().getUser();
		DRHead drHead = heartlandRequest.getHead();
		DRHead Head = new DRHead();
		User user = new User(heartlandUser.getUserName(), heartlandUser.getToken(), heartlandUser.getOrganizationId());
		Head.setUser(user);
		Head.setEmailReceipt(drHead.getEmailReceipt());
		//Head.setEnrollCustomer(drHead.getEnrollCustomer());
		Head.setEnrollCustomer(drHead.getEnrollCustomer()!=null ? drHead.getEnrollCustomer() 
				: (heartlandCustomerDetails!=null ? heartlandCustomerDetails.getCustom().getEnrol():Constants.STRING_NILL));
		Head.setPrintReceipt(drHead.getPrintReceipt());
		Head.setRequestFormat(drHead.getRequestFormat());
		Head.setRequestSource(drHead.getRequestSource());
		Head.setRequestEndPoint(drHead.getRequestEndPoint());
		Head.setIsLoyaltyCustomer(drHead.getIsLoyaltyCustomer());
		digitalReceipt.setHead(Head);
		//List<CreditCard> creditCards = new ArrayList<CreditCard>();
		//COD cod = null;
		DRBody Body = new DRBody();

		String strDocSid = heartlandTicketDetails.getId() + "";
		String membership = heartlandOCDetails != null && heartlandOCDetails.getMembershipNumber() != null ? heartlandOCDetails.getMembershipNumber()
				: Constants.STRING_NILL;
		String email = heartlandOCDetails!=null && heartlandOCDetails.getEmail() != null ? heartlandOCDetails.getEmail()  :"";//(heartlandCustomerDetails!=null?heartlandCustomerDetails.getEmail():"");
		
		Receipt receipt = new Receipt(Constants.STRING_NILL, Constants.STRING_NILL,
				Constants.STRING_NILL, Constants.STRING_NILL, heartlandTicketDetails.getTotal_discounts()+"", Constants.STRING_NILL,
				Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
				itemsCount+"", Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
				Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
				heartlandTicketDetails.getTotal()+"", Constants.STRING_NILL,
				heartlandTicketDetails.getOriginal_subtotal()+"", Constants.STRING_NILL,
				Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
				heartlandTicketDetails.getUpdated_at(), Constants.STRING_NILL, Constants.STRING_NILL,
				Constants.STRING_NILL, Constants.STRING_NILL, 
				(heartlandCustomerDetails!=null?heartlandCustomerDetails.getFirst_name():Constants.STRING_NILL),
				(heartlandCustomerDetails!=null?heartlandCustomerDetails.getLast_name():Constants.STRING_NILL), Constants.STRING_NILL,
				( addressDetails != null && addressDetails.getLine_1() != null ? addressDetails.getLine_1() : Constants.STRING_NILL) ,
				( addressDetails != null && addressDetails.getLine_2() != null ? addressDetails.getLine_2() : Constants.STRING_NILL) ,
				( addressDetails != null && addressDetails.getCity() != null ? addressDetails.getCity() : Constants.STRING_NILL) , 
				( addressDetails != null && addressDetails.getState() != null ? addressDetails.getState() : Constants.STRING_NILL), 
				Constants.STRING_NILL,Constants.STRING_NILL, 
				(( addressDetails != null && addressDetails.getPostal_code() != null ? addressDetails.getPostal_code() : Constants.STRING_NILL)),
				membership, membership,Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
				Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
				Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
				Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
				(heartlandCustomerDetails!=null && heartlandCustomerDetails.getId() != null ? heartlandCustomerDetails.getId() : (heartlandTicketDetails!=null ? heartlandTicketDetails.getCustomer_id() : Constants.STRING_NILL)),
				Constants.STRING_NILL, (heartlandCustomerDetails!=null && ""+heartlandCustomerDetails.getName() != null ? heartlandCustomerDetails.getName() : Constants.STRING_NILL),
				Constants.STRING_NILL, Constants.STRING_NILL,Constants.STRING_NILL, Constants.STRING_NILL, 
				Constants.STRING_NILL, Constants.STRING_NILL,Constants.STRING_NILL, Constants.STRING_NILL, 
				heartlandCustomerDetails!=null?
						((heartlandCustomerDetails.getPhone_number()!=null)?heartlandCustomerDetails.getPhone_number().replace("+", Constants.STRING_NILL):Constants.STRING_NILL):Constants.STRING_NILL, 
						Constants.STRING_NILL,Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
						Constants.STRING_NILL,Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
						Constants.STRING_NILL ,Constants.STRING_NILL, Constants.STRING_NILL, invcTotalQty+"", 
						Constants.STRING_NILL,Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
						Constants.STRING_NILL,Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
						email,Constants.STRING_NILL, heartlandTicketDetails.getParent_transaction_id()+"", strDocSid, Constants.STRING_NILL,heartlandTicketDetails.getTotal_paid()+"", 
						heartlandTicketDetails.getChange()+"", Constants.STRING_NILL, Constants.STRING_NILL,Constants.STRING_NILL, 
						( addressDetails != null && addressDetails.getCountry() != null ? addressDetails.getCountry() : Constants.STRING_NILL) , 
						Constants.STRING_NILL, Constants.STRING_NILL,Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
						Constants.STRING_NILL,heartlandTicketDetails.getSource_location_id()+"", Constants.STRING_NILL, Constants.STRING_NILL,
						heartlandTicketDetails.getSales_rep(), Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
						(heartlandOCDetails != null && heartlandOCDetails.getPromotions() != null && !heartlandOCDetails.getPromotions().isEmpty()
						? heartlandOCDetails.getPromotions().get(0).getCouponCode(): Constants.STRING_NILL),
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL);
		receipt.setStore(heartlandTicketDetails.getSource_location_id()+"");
		receipt.setWorkstation(heartlandTicketDetails.getStation_id()+"");
		receipt.setReceiptType(heartlandTicketDetails.getType());
		receipt.setInvcNum(heartlandTicketDetails.getId());

		List<DRItem> items = new ArrayList<DRItem>();
		Map<String, DRItem> distinctskus = new HashMap<String, DRItem>();
	
		if (drHead.getReceiptType() != null
				&& drHead.getReceiptType().equalsIgnoreCase(OCConstants.DR_RECEIPT_TYPE_SALE)) {
			List<DetailResults> heartlandItems = heartlandDRBody.getDetails().getResults();
			//double itemDiscAmt = 0; //To send disc amt per qty (later multiplied with qty in Extraction)
			
			for (DetailResults heartlandItem : heartlandItems) {
				if(!heartlandItem.getType().equalsIgnoreCase("ItemLine")) continue;
				
				DRItem item = new DRItem(Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, heartlandItem.getDescription(),
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,Constants.STRING_NILL,
						Constants.STRING_NILL, Constants.STRING_NILL,Constants.STRING_NILL,
						Constants.STRING_NILL,Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
						Constants.STRING_NILL, heartlandItem.getQty()+"", heartlandItem.getFinal_unit_price()+"" /*InvcItemPrc*/
						, Constants.STRING_NILL, Constants.STRING_NILL, heartlandItem.getValue()+"" /*extPrc*/
						,Constants.STRING_NILL, heartlandItem.getCurrent_unit_price()+"" /*DocItemOrigPrc*/, 
						heartlandItem.getFinal_unit_price()+"" /*DocItemPrc*/
						,Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
						heartlandItem.getItem_id()+"", Constants.STRING_NILL, Constants.STRING_NILL,
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL);
				
				if(heartlandItem.getItem_custom()!=null) {
					item.setItemCategory(heartlandItem.getItem_custom().getCategory());
					item.setUDF0(heartlandItem.getItem_custom().getGroup());
					item.setUDF1(heartlandItem.getItem_custom().getWidth());
					item.setUDF2(heartlandItem.getItem_custom().getColor_name());
					item.setUDF3(heartlandItem.getItem_custom().getStyle_name());
					item.setSize(heartlandItem.getItem_custom().getSize());
					item.setUPC(heartlandItem.getItem_custom().getUpc());
					item.setTax2(heartlandItem.getItem_custom().getTax_category());
				}
				
				double disc_amt=0.0;
				
				try
				{
					disc_amt= Double.parseDouble(heartlandItem.getCurrent_unit_price())-
							Double.parseDouble(heartlandItem.getFinal_unit_price());
					
					
				}catch(Exception e) {}
				
				item.setDocItemDiscAmt(disc_amt+"");
				item.setItemLine(heartlandItem.getId()+"");//item line id
				items.add(item);
				distinctskus.put(heartlandItem.getItem_id()+"", item);
				//logger.info("distinctskus " + distinctskus.size());
				itemsCount++;
				receipt.setItemsCount(itemsCount+"");
				try
				{
				invcTotalQty+=Double.parseDouble(heartlandItem.getQty());
				}catch(Exception e) {}
				
				receipt.setInvcTotalQty(invcTotalQty+"");

			}

			
		} else if (drHead.getReceiptType() != null
				&& drHead.getReceiptType().equalsIgnoreCase(OCConstants.DR_RECEIPT_TYPE_RETURN)) {
			List<DetailResults> heartlandItems = heartlandDRBody.getDetails().getResults();
			//List<DetailResults> refundItems=heartlandDRBody.getDetails().getResults();
			Map<String, DetailResults> refundskus = new HashMap<String, DetailResults>();
			
			//double lineItemPrice=0;
			for (DetailResults heartlandItem : heartlandItems) {
				if(!heartlandItem.getType().equalsIgnoreCase("ItemLine")) continue;
				DRItem item = new DRItem(Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, heartlandItem.getDescription(),
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,Constants.STRING_NILL,
						Constants.STRING_NILL, Constants.STRING_NILL,Constants.STRING_NILL,
						Constants.STRING_NILL,Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
						Constants.STRING_NILL, heartlandItem.getQty()+"", heartlandItem.getFinal_unit_price()+"" /*InvcItemPrc*/
						, Constants.STRING_NILL, Constants.STRING_NILL, heartlandItem.getValue()+"" /*extPrc*/
						,Constants.STRING_NILL, heartlandItem.getCurrent_unit_price()+"" /*DocItemOrigPrc*/, 
						heartlandItem.getFinal_unit_price()+"" /*DocItemPrc*/
						,Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
						heartlandItem.getItem_id()+"", Constants.STRING_NILL, Constants.STRING_NILL,
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL);
				if(heartlandItem.getItem_custom()!=null) {
					item.setItemCategory(heartlandItem.getItem_custom().getCategory());
					item.setUDF0(heartlandItem.getItem_custom().getGroup());
					item.setUDF1(heartlandItem.getItem_custom().getWidth());
					item.setUDF2(heartlandItem.getItem_custom().getColor_name());
					item.setUDF3(heartlandItem.getItem_custom().getStyle_name());
					item.setSize(heartlandItem.getItem_custom().getSize());
					item.setUPC(heartlandItem.getItem_custom().getUpc());
					item.setTax2(heartlandItem.getItem_custom().getTax_category());
				}
				
				double disc_amt = 0.0;
				try {
					disc_amt = Double.parseDouble(heartlandItem.getCurrent_unit_price())
							- Double.parseDouble(heartlandItem.getFinal_unit_price());
					

				} catch (Exception e) {
				}
				item.setDocItemDiscAmt(disc_amt+"");
				item.setItemLine(heartlandItem.getId()+"");//item line id
				if(refundskus.containsKey(heartlandItem.getItem_public_id())) {
					distinctskus.put(heartlandItem.getItem_public_id(), item);
				}
				items.add(item);
				//logger.info("distinctskus " + distinctskus.size());
				itemsCount++;
				receipt.setItemsCount(itemsCount+"");
				try
				{
				invcTotalQty+=Double.parseDouble(heartlandItem.getQty());
				}catch(Exception e) {}
				
				receipt.setInvcTotalQty(invcTotalQty+"");
				}

			
		}


		if (heartlandTicketDetails.getCreated_at() != null && !heartlandTicketDetails.getCreated_at().isEmpty()) {
			try {
				String strCreatedDate=heartlandTicketDetails.getCreated_at();
				receipt.setDocDate(getDocDate(strCreatedDate));
			} catch (Exception e) {
				logger.error("Exception ", e);
			}
		}
		

		
		Body.setItems(items);
		Body.setReceipt(receipt);
		List<CreditCard> creditCards = new ArrayList<CreditCard>();
		List<FC> listOfFC =new ArrayList<FC>();
		List<Payments> listOfpayments = new ArrayList<Payments>();
		List<Gift> listOfGifts = new ArrayList<Gift>();
		List<GiftCard> listOfGiftCards = new ArrayList<GiftCard>();
		List<DebitCard> listOfDebitCards = new ArrayList<DebitCard>();
		List<StoreCredit> listOfStoreCredit = new ArrayList<StoreCredit>();
		List<Check> listOfDRChecks = new ArrayList<Check>();
		List<FCCheck> listOfDRFCCecks = new ArrayList<FCCheck>();
		List<TravellerCheck> listOfDRTravellersChecks = new ArrayList<TravellerCheck>();
		Charge charge = null;
		COD cod = null;
		Deposit deposit =null;
		Cash cash = null;
		
		
		
		List<PaymentResult> paymentResult = heartlandDRBody.getPayments().getResults();
		double amount=0;
		double given=0;
		double taken=0;
		for (PaymentResult payment : paymentResult) {
			
			String tenderName = payment.getDescription();
			
			if (tenderName.equalsIgnoreCase("Cash") || tenderName.equalsIgnoreCase("Cash refund"))  {
				amount+=(payment.getAmount()==null || payment.getAmount().isEmpty())?0.0:Double.parseDouble(payment.getAmount());
				given+=(payment.getAmount_tendered()==null || payment.getAmount_tendered().isEmpty())?0.0:Double.parseDouble(payment.getAmount_tendered());
				cash = new Cash(amount+"",  Constants.STRING_NILL, given+"", Constants.STRING_NILL);
            }else if (tenderName.equalsIgnoreCase("Foreign Currency")) {
				//logger.debug("inside ====" +  tenderName);
				FC FC = new FC(payment.getAmount()+"", Constants.STRING_NILL,
						Constants.STRING_NILL, payment.getAmount_tendered()+"", Constants.STRING_NILL);
				listOfFC.add(FC);
            }else if (tenderName.equalsIgnoreCase("Check") ) {
            	
            	Check check = new Check(payment.getAmount()+"", Constants.STRING_NILL, Constants.STRING_NILL, 
            			Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
            			Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
            			payment.getAmount_tendered()+"", Constants.STRING_NILL);
	            	 listOfDRChecks.add(check);
            }else if (tenderName.equalsIgnoreCase("Credit Card") ) {
				CreditCard CC = new CreditCard(payment.getAmount()+"", (String)payment.getCredit_card_id(), 
						Constants.STRING_NILL, Constants.STRING_NILL,
						Constants.STRING_NILL,Constants.STRING_NILL, 
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
						Constants.STRING_NILL,Constants.STRING_NILL, payment.getAmount_tendered()+"", 
						Constants.STRING_NILL);
						creditCards.add(CC);
            }else if (tenderName.equalsIgnoreCase("COD") ) {
    			amount+=(payment.getAmount()==null || payment.getAmount().isEmpty())?0.0:Double.parseDouble(payment.getAmount());
				given+=(payment.getAmount_tendered()==null || payment.getAmount_tendered().isEmpty())?0.0:Double.parseDouble(payment.getAmount_tendered());
	
				cod = new COD(amount+"",  Constants.STRING_NILL, given+"", Constants.STRING_NILL);
	            	 
            }else  if (tenderName.equalsIgnoreCase("Charge") ) {
    			amount+=(payment.getAmount()==null || payment.getAmount().isEmpty())?0.0:Double.parseDouble(payment.getAmount());
				given+=(payment.getAmount_tendered()==null || payment.getAmount_tendered().isEmpty())?0.0:Double.parseDouble(payment.getAmount_tendered());
	
				charge = new Charge(payment.getAmount()+"", Constants.STRING_NILL,
						Constants.STRING_NILL, Constants.STRING_NILL, 
						Constants.STRING_NILL, payment.getAmount_tendered()+"", Constants.STRING_NILL);
			}else if (tenderName.equalsIgnoreCase("StoreCredit") )   {
				
				StoreCredit SC = new StoreCredit(payment.getAmount()+"", Constants.STRING_NILL, 
						Constants.STRING_NILL, payment.getAmount_tendered()+"", Constants.STRING_NILL);
	            	listOfStoreCredit.add(SC);	
	        }else if (tenderName.equalsIgnoreCase("Deposit") ){
				amount+=(payment.getAmount()==null || payment.getAmount().isEmpty())?0.0:Double.parseDouble(payment.getAmount());
				given+=(payment.getAmount_tendered()==null || payment.getAmount_tendered().isEmpty())?0.0:Double.parseDouble(payment.getAmount_tendered());
	
				deposit = new Deposit(amount+"",  Constants.STRING_NILL, given+"", Constants.STRING_NILL);
	            	
            }else if (tenderName.equalsIgnoreCase("Gift Card") ) {
	            GiftCard giftCard = new GiftCard(payment.getAmount()+"", 
						Constants.STRING_NILL,Constants.STRING_NILL, 
						Constants.STRING_NILL,Constants.STRING_NILL, 
						Constants.STRING_NILL,Constants.STRING_NILL, payment.getAmount_tendered()+"", Constants.STRING_NILL);	
            		//String giftCrdVal = deciFormat.format(tempGiftCardObj.getAmount()).toString().isEmpty() ? Double.parseDouble("0.00") : deciFormat.format(tempGiftCardObj.getAmount()).toString();
            		listOfGiftCards.add(giftCard);
	        }else if (tenderName.equalsIgnoreCase("Debit Card") )    {
	            	DebitCard debitCard = new DebitCard(payment.getAmount()+"", 
	            			Constants.STRING_NILL,Constants.STRING_NILL, 
	            			Constants.STRING_NILL,Constants.STRING_NILL, 
	            			Constants.STRING_NILL,Constants.STRING_NILL, 
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
						payment.getAmount_tendered()+"", Constants.STRING_NILL);
	            	listOfDebitCards.add(debitCard);
			}else {
				Payments paymentObj = new Payments(payment.getAmount()+"", Constants.STRING_NILL, 
						Constants.STRING_NILL, Constants.STRING_NILL, payment.getAmount_tendered()+"", Constants.STRING_NILL);
						listOfpayments.add(paymentObj);
			}
			
		}//for
		
		if(cod != null )Body.setCOD(cod);
		if(cash != null) Body.setCash(cash);
		if(deposit != null) Body.setDeposit(deposit);
		if(charge != null) Body.setCharge(charge);
		Body.setCheck(listOfDRChecks);
		Body.setCreditCard(creditCards);
		Body.setDebitCard(listOfDebitCards);
		Body.setTravelerCheck(listOfDRTravellersChecks);
		Body.setPayments(listOfpayments);
		Body.setStoreCredit(listOfStoreCredit);
		Body.setFC(listOfFC);
		Body.setFCCheck(listOfDRFCCecks);
		Body.setGift(listOfGifts);
		Body.setGiftCard(listOfGiftCards);
		digitalReceipt.setBody(Body);
		DROptCultureDetails DROptCultureDetails = null;
		if (heartlandOCDetails != null) {
			Gson gson = new Gson();
			gson = new Gson();
			String json = gson.toJson(heartlandOCDetails);
			DROptCultureDetails = gson.fromJson(json, DROptCultureDetails.class);
		}
		digitalReceipt.setOptcultureDetails(DROptCultureDetails);
		logger.info("convertHeartlandRequest..end");
		return digitalReceipt;
	}
	
	private String getDocDate(String strCreatedDate)
	{
		OffsetDateTime parsedDateTime = OffsetDateTime.parse(strCreatedDate);
		ZonedDateTime dateTimeInMyTimeZone
		= parsedDateTime.atZoneSameInstant(ZoneId.systemDefault());
		DateTimeFormatter formatterMdyyyy = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm:ss");
		LocalDateTime localDateTime = dateTimeInMyTimeZone.toLocalDateTime();
		String strDocDate = localDateTime.format(formatterMdyyyy);
		return strDocDate;
	}





}
