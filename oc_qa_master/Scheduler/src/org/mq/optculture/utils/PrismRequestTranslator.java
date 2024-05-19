package org.mq.optculture.utils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.Users;
import org.mq.captiway.scheduler.utility.Constants;
import org.mq.optculture.model.DR.DRBody;
import org.mq.optculture.model.DR.DRHead;
import org.mq.optculture.model.DR.DRItem;
import org.mq.optculture.model.DR.DROptCultureDetails;
import org.mq.optculture.model.DR.DigitalReceipt;
import org.mq.optculture.model.DR.OCPromotions;
import org.mq.optculture.model.DR.Receipt;
import org.mq.optculture.model.DR.User;
import org.mq.optculture.model.DR.prism.PrismBasedDRRequest;
import org.mq.optculture.model.DR.prism.PrismDRBody;
import org.mq.optculture.model.DR.prism.PrismDRHead;
import org.mq.optculture.model.DR.prism.PrismDRItem;
import org.mq.optculture.model.DR.prism.PrismDRTender;
import org.mq.optculture.model.DR.prism.PrismDRUser;
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

public class PrismRequestTranslator {

	PrismBasedDRRequest prismRequest;
	DigitalReceipt digitalReceipt;
	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	public PrismRequestTranslator(){
		
		
	}
	
	
	public DRBody convertPrismRequest(PrismBasedDRRequest prismRequest,Users user){
		
		
		/*PrismDRUser PrismUser = prismRequest.getHead().getUser();
		User user = new User(PrismUser.getUserName(), PrismUser.getToken(), PrismUser.getOrganizationId());
		Head.setUser(user);
		
		PrismDRHead drHead = prismRequest.getHead() ;
		Head.setEmailReceipt(drHead.getEmailReceipt());
		Head.setEnrollCustomer(drHead.getEnrollCustomer());
		Head.setPrintReceipt(drHead.getPrintReceipt());
		Head.setRequestFormat(drHead.getRequestFormat());
		Head.setRequestSource(drHead.getRequestSource());
		Head.setRequestEndPoint(drHead.getRequestEndPoint());
		
		digitalReceipt.setHead(Head);*/
		PrismDRBody prismBody = prismRequest.getBody();
		DROptCultureDetails ocDetails = prismRequest.getOptcultureDetails();
		String membership = ocDetails!=null && ocDetails.getMembershipNumber()!=null?ocDetails.getMembershipNumber():prismBody.getBt_info1();
		String email=prismBody.getBt_email();;//ocDetails!=null && ocDetails.getEmail()!=null?ocDetails.getEmail():prismBody.getBt_email();
		String phone=prismBody.getBt_primary_phone_no();//ocDetails!=null && ocDetails.getPhone()!=null?ocDetails.getPhone():prismBody.getBt_primary_phone_no();
		DRBody Body = new DRBody();
		Receipt receipt = new Receipt(prismBody.getNotes_order(), prismBody.getTransaction_total_tax_amt(), 
				Constants.STRING_NILL, Constants.STRING_NILL, prismBody.getTotal_discount_amt(), prismBody.getComment1(), 
				prismBody.getComment2(), prismBody.getTax_area_name(), prismBody.getTax_rebate_percent(), prismBody.getTax_rebate_amt(),
				prismBody.getTotal_item_count(), prismBody.getOrder_shipping_amt(), prismBody.getOrder_fee_amt1(), 
				prismBody.getOrder_shipping_percentage(), prismBody.getFee_type1(), prismBody.getFee_tax_perc1(), 
				Constants.STRING_NILL, prismBody.getRounded_due_amt(), prismBody.getTransaction_total_amt(), 
				Constants.STRING_NILL, prismBody.getTransaction_subtotal(), prismBody.getTransaction_total_amt(), 
				Constants.STRING_NILL, Constants.STRING_NILL, prismBody.getCashier_name(), prismBody.getCreated_datetime(), 
				prismBody.getDocument_number(), Constants.STRING_NILL, Constants.STRING_NILL, prismBody.getBt_company_name(),
				prismBody.getBt_first_name(), prismBody.getBt_last_name(), 
				Constants.STRING_NILL, prismBody.getBt_address_line1(), prismBody.getBt_address_line2(), 
				prismBody.getBt_address_line3(), prismBody.getBt_address_line4(), prismBody.getBt_address_line5(), 
				prismBody.getBt_address_line6(), prismBody.getBt_postal_code(), membership, prismBody.getBt_info2(), 
				prismBody.getSerial_number(), Constants.STRING_NILL, prismBody.getSt_company_name(), prismBody.getSt_first_name(), 
				 prismBody.getSt_last_name(), Constants.STRING_NILL, prismBody.getSt_address_line1(), prismBody.getSt_address_line2(), 
				prismBody.getSt_address_line3(), prismBody.getSt_address_line4(), prismBody.getSt_address_line5(), prismBody.getSt_address_line6(), prismBody.getSt_postal_code(), Constants.STRING_NILL, 
				Constants.STRING_NILL, prismBody.getBt_cuid(), Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
				prismBody.getWorkstation_number(), Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
				Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, phone, 
				Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, prismBody.getStore_address_line1(), 
				prismBody.getStore_address_line2(), prismBody.getStore_address_line3(), prismBody.getTender_name(), 
				prismBody.getStore_number(), prismBody.getTracking_number(), Constants.STRING_NILL, prismBody.getSold_qty(), 
				Constants.STRING_NILL, prismBody.getStore_address_line4(), prismBody.getStore_address_line5(), 
				Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
				Constants.STRING_NILL, prismBody.getTransaction_total_tax_amt(),  Constants.STRING_NILL,  
				Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL, 
				 Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL, 
				 Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL, 
				 Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL, 
				 Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL, prismBody.getBt_udf8(),  prismBody.getBt_udf7(), 
				 prismBody.getBt_udf6(),  prismBody.getBt_udf5(),  prismBody.getBt_udf4(),  prismBody.getBt_udf3(),  prismBody.getBt_udf2(), Constants.STRING_NILL, 
				 Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL, 
				 prismBody.getBt_udf1(), Constants.STRING_NILL, email, prismBody.getSt_email() , prismBody.getRef_sale_sid(), prismBody.getSid(), 
				 Constants.STRING_NILL, prismBody.getDoc_tender_type(), prismBody.getGiven_amt(), Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
				 Constants.STRING_NILL, Constants.STRING_NILL, prismBody.getStore_name(), prismBody.getStore_phone1(), Constants.STRING_NILL, Constants.STRING_NILL, 
				 prismBody.getSubsidiary_number(), prismBody.getStore_code(), Constants.STRING_NILL, prismBody.getTransaction_total_tax_amt(), 
				 prismBody.getEmployee1_full_name(), prismBody.getOrig_document_number(), 
				 prismBody.getOriginal_store_number(), prismBody.getOrig_subsidiary_number(), Constants.STRING_NILL, prismBody.getNotes_general(), prismBody.getStore_phone2(), 
				 (!prismBody.getCoupons().isEmpty() ?  prismBody.getCoupons().get(0).getCoupon_code() : Constants.STRING_NILL), prismBody.getComment1(), prismBody.getComment2(), 
						 Constants.STRING_NILL, prismBody.getFiscal_document_number(), Constants.STRING_NILL, prismBody.getBt_employee_id(),
						 phone);
		List<DRItem> items = new ArrayList<DRItem>();
		
		
		List<PrismDRItem> PrismItems = prismBody.getItems();
		for (PrismDRItem prismDRItem : PrismItems) {
			
			DRItem item = new DRItem(prismDRItem.getDcs_code(), prismDRItem.getVendor_code(), Constants.STRING_NILL, 
					Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
					Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, prismDRItem.getItem_description1(), 
					prismDRItem.getItem_description2(), prismDRItem.getAttribute(), prismDRItem.getItem_size(), 
					prismDRItem.getScan_upc(), prismDRItem.getAlu(), prismDRItem.getUdf_string01(), prismDRItem.getUdf_string02(), 
					prismDRItem.getUdf_string03(), prismDRItem.getUdf_string04(), Constants.STRING_NILL, prismDRItem.getTax_percent(), 
					prismDRItem.getTax_amount(), prismDRItem.getQuantity(), prismDRItem.getOriginal_price(), prismDRItem.getDiscount_amt(), 
					Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
					prismDRItem.getOriginal_price(), prismDRItem.getOriginal_price(), prismDRItem.getTax_amount(), Constants.STRING_NILL,
					prismDRItem.getItem_lookup(), prismDRItem.getPrice_lvl(),
					prismDRItem.getInvn_item_uid(), prismDRItem.getDiscount_amt(), Constants.STRING_NILL, Constants.STRING_NILL,
					prismDRItem.getItem_description3(),  prismDRItem.getItem_description4(), Constants.STRING_NILL,
					Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
					Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
					Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
					Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, prismDRItem.getPackage_sequence_number(), 
					Constants.STRING_NILL, Constants.STRING_NILL, 
					Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
					Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
					Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
					Constants.STRING_NILL, Constants.STRING_NILL, prismDRItem.getSerial_number(), prismDRItem.getNote1(), 
					prismDRItem.getNote2(), prismDRItem.getNote3(),prismDRItem.getNote4(),prismDRItem.getNote5(),
					prismDRItem.getNote6(),prismDRItem.getNote7(),prismDRItem.getNote8(),prismDRItem.getNote9(),prismDRItem.getNote10(), Constants.STRING_NILL, prismDRItem.getNetPrice(), 
					prismDRItem.getReturn_reason(), Constants.STRING_NILL, 
					prismDRItem.getDiscount_reason(), prismDRItem.getTax_amount(),  prismDRItem.getOrig_store_number(), prismDRItem.getOrig_subsidiary_number(), prismDRItem.getOrig_document_number(),prismDRItem.getRef_sale_doc_sid());
			
			// APP-4301
			if (prismBody.getHas_return() != null && prismBody.getHas_return().equals("true") 
					&& prismBody.getReturn_qty() != null && !prismBody.getReturn_qty().equals("0") &&
					prismDRItem.getRef_sale_doc_sid() != null &&
					!prismDRItem.getRef_sale_doc_sid().isEmpty()) {
				logger.info("Got Return Item in DR");
				item.setQty("-" + prismDRItem.getQuantity());
				item.setDocItemDisc("-" + prismDRItem.getDiscount_amt());
				item.setDocItemDiscAmt("-" + prismDRItem.getDiscount_amt());
			}
					
			items.add(item);
		}
		
		if(prismBody.getCreated_datetime() != null && !prismBody.getCreated_datetime().isEmpty()) {
    		
    		try {
				OffsetDateTime odt = OffsetDateTime.parse( prismBody.getCreated_datetime());
				
				String trxDate = odt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME ).replace( "T" , " " );
				
				DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date date = (Date)formatter.parse(trxDate); 
				
				formatter = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss");
				String docDate = formatter.format(date);
				prismBody.setCreated_datetime(docDate);
				
			} catch (ParseException e) {
				// TODO Auto-generated catch block
			}
    		
    	}
		
		if(prismBody.getCustomer_dob() != null && !prismBody.getCustomer_dob().isEmpty()) {
    		
    		try {
				OffsetDateTime odt = OffsetDateTime.parse( prismBody.getCustomer_dob());
				
				String trxDate = odt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME ).replace( "T" , " " );
				
				DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date date = (Date)formatter.parse(trxDate); 
				
				formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
				String BirthdayDate = formatter.format(date);
				prismBody.setCustomer_dob(BirthdayDate);
				
			} catch (ParseException e) {
				logger.info("e==>"+e);
			}
    		
		}
		receipt.setDocDate(prismBody.getCreated_datetime());
	
		
		
		logger.info("dob value is "+prismBody.getCustomer_dob());
		logger.info("nationality value is "+prismBody.getCustomer_nationality());

		receipt.setDOB(prismBody.getCustomer_dob());
		receipt.setNationality(prismBody.getCustomer_nationality());
	
		
		logger.info("receipt dob value is "+receipt.getDOB());
		logger.info("receipt nationality value is "+receipt.getNationality());

		
		
		
		
		
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
		
		
		
		List<PrismDRTender> tenders = prismBody.getTenders();
		for (PrismDRTender prismDRTender : tenders) {
			
			String tenderName = prismDRTender.getTender_name();
			
			// ADDED AFTER NEW DR SCHEMA 
			if (tenderName.equalsIgnoreCase("Cash"))  {
				  
				cash = new Cash(prismDRTender.getAmount(),  prismDRTender.getGiven(), prismDRTender.getTaken(), prismDRTender.getCurrency_name());
            }
			else if (tenderName.equalsIgnoreCase("Foreign Currency")) {
				//logger.debug("inside ====" +  tenderName);
				FC FC = new FC(prismDRTender.getAmount(), prismDRTender.getForeign_currency_name(),
						prismDRTender.getTaken(), prismDRTender.getGiven(), prismDRTender.getBase_taken());
				listOfFC.add(FC);
            } else if (tenderName.equalsIgnoreCase("Check") ) {
            	
            	Check check = new Check(prismDRTender.getAmount(), prismDRTender.getCheck_number().toString(), Constants.STRING_NILL, 
            			Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
            			Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, prismDRTender.getTaken(), 
            			prismDRTender.getGiven(), prismDRTender.getCurrency_name());
	            	 listOfDRChecks.add(check);
            }
	            
			else if (prismDRTender.getTender_type() != null && prismDRTender.getTender_type().equals("2") ) {
				CreditCard CC = new CreditCard(prismDRTender.getAmount(), prismDRTender.getCard_number(), 
						prismDRTender.getCard_expiration_month(), prismDRTender.getCard_expiration_year(),
						prismDRTender.getCard_type_name(), prismDRTender.getAuthorization_code(), 
						prismDRTender.getInternal_reference_number(), Constants.STRING_NILL, Constants.STRING_NILL, 
						prismDRTender.getAvs_response_code(), prismDRTender.getTaken(), prismDRTender.getGiven(), 
						prismDRTender.getCurrency_name());
						creditCards.add(CC);
            }	
	                
			else if (tenderName.equalsIgnoreCase("COD") ) {
				cod = new COD(prismDRTender.getAmount(),  prismDRTender.getGiven(), prismDRTender.getTaken(), prismDRTender.getCurrency_name());
	            	 
            }
	            
			else  if (tenderName.equalsIgnoreCase("Charge") ) {
				charge = new Charge(prismDRTender.getAmount(), prismDRTender.getCharge_net_days(),
						prismDRTender.getCharge_discount_days(), prismDRTender.getCharge_discount_percent(), 
						prismDRTender.getTaken(), prismDRTender.getGiven(), prismDRTender.getCurrency_name());
				
	            	
	            	
			}
			else if (tenderName.equalsIgnoreCase("StoreCredit") )   {
				
				StoreCredit SC = new StoreCredit(prismDRTender.getAmount(), prismDRTender.getNew_credit_value(), 
						prismDRTender.getTaken(), prismDRTender.getGiven(), prismDRTender.getCurrency_name());
	            	listOfStoreCredit.add(SC);	
	            	
            }
	            
			else if (tenderName.equalsIgnoreCase("Deposit") ){
	            	//String codVal = deciFormat.format(depositObj.getAmount()).toString() .isEmpty() ? Double.parseDouble("0.00") : deciFormat.format(depositObj.getAmount()).toString() ;
				deposit = new Deposit(prismDRTender.getAmount(),  prismDRTender.getGiven(), prismDRTender.getTaken(), 
						prismDRTender.getCurrency_name());
	            	
            }
	            
			else  if (tenderName.equalsIgnoreCase("Payments") ) {
				
				Payments payment = new Payments(prismDRTender.getAmount(), prismDRTender.getPayment_date(), 
						prismDRTender.getPayment_remark(), prismDRTender.getTaken(), prismDRTender.getGiven(), prismDRTender.getCurrency_name());
						listOfpayments.add(payment);
						
						
	                
            }
	            
			else if (tenderName.equalsIgnoreCase("Gift Certificate") ) {
				
				Gift Gift = new Gift(prismDRTender.getAmount(), prismDRTender.getCertificate_number(), prismDRTender.getCertificate_number(),
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
						prismDRTender.getTaken(), prismDRTender.getGiven(), prismDRTender.getCurrency_name());
	            		listOfGifts.add(Gift);
            }
	            
			else if (tenderName.equalsIgnoreCase("Gift Card") ) {
	            	
				GiftCard giftCard = new GiftCard(prismDRTender.getAmount(), 
						prismDRTender.getCard_number(), prismDRTender.getCard_expiration_month(), 
						prismDRTender.getCard_expiration_year(), prismDRTender.getAuthorization_code(), 
						prismDRTender.getBalance(), prismDRTender.getTaken(), prismDRTender.getGiven(), prismDRTender.getCurrency_name());	
            		//String giftCrdVal = deciFormat.format(tempGiftCardObj.getAmount()).toString().isEmpty() ? Double.parseDouble("0.00") : deciFormat.format(tempGiftCardObj.getAmount()).toString();
            		listOfGiftCards.add(giftCard);
	            	
            }
	            
			else if (tenderName.equalsIgnoreCase("Debit Card") )    {
	            	DebitCard debitCard = new DebitCard(prismDRTender.getAmount(), 
						prismDRTender.getCard_number(), prismDRTender.getCard_expiration_month(), 
						prismDRTender.getCard_expiration_year(), prismDRTender.getAuthorization_code(), 
						prismDRTender.getInternal_reference_number(), prismDRTender.getTrace_number(), 
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, prismDRTender.getTaken(),
						prismDRTender.getGiven(), prismDRTender.getCurrency_name());
	            	listOfDebitCards.add(debitCard);
				
			}
	            // Added another 3 tender types(rarely used)
	            
			else if (tenderName.equalsIgnoreCase("TravelerCheck")) {
				
				TravellerCheck TC = new TravellerCheck(prismDRTender.getAmount(), prismDRTender.getCheck_number().toString(), Constants.STRING_NILL, 
            			Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
            			Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, prismDRTender.getTaken(), 
            			prismDRTender.getGiven(), prismDRTender.getCurrency_name());
				
	            	listOfDRTravellersChecks.add(TC);	
	            	
            }
	            
	            
			else  if (tenderName.equalsIgnoreCase("FCCheck")) {
				FCCheck FCC = new FCCheck(prismDRTender.getAmount(), prismDRTender.getCheck_number().toString(), Constants.STRING_NILL, 
            			Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
            			Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, prismDRTender.getTaken(), 
            			prismDRTender.getGiven(), prismDRTender.getCurrency_name());
				listOfDRFCCecks.add(FCC);
				
				
	            	
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
		
		
		return Body;
		
	}
	
}
