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
import org.mq.optculture.model.DR.OCPromotions;
import org.mq.optculture.model.DR.Receipt;
import org.mq.optculture.model.DR.User;
import org.mq.optculture.model.DR.shopify.AddressDetails;
import org.mq.optculture.model.DR.shopify.RefundItems;
import org.mq.optculture.model.DR.shopify.Refunds;
import org.mq.optculture.model.DR.shopify.ShopifyBasedDRRequest;
import org.mq.optculture.model.DR.shopify.ShopifyCustomerDetails;
import org.mq.optculture.model.DR.shopify.ShopifyDRBody;
import org.mq.optculture.model.DR.shopify.ShopifyItems;
import org.mq.optculture.model.DR.shopify.ShopifyReceiptDetails;
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
//import org.zkoss.util.logging.Log;

import com.google.gson.Gson;

public class ShopifyRequestTranslator {
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);

	public ShopifyRequestTranslator() {

	}
	
	public DigitalReceipt convertShopifyRequest(ShopifyBasedDRRequest shopifyRequest, Users userObj) {
		logger.info("Shopify translator.DigitalReceipt.convertShopifyRequest");
		DigitalReceipt digitalReceipt = new DigitalReceipt();
		ShopifyDRBody shopifyDRBody = shopifyRequest.getBody();
		ShopifyReceiptDetails shopifyReceiptDetails = shopifyDRBody.getReceipt();
		ShopifyCustomerDetails shopifyCustomerDetails = shopifyDRBody.getReceipt().getCustomer();
		AddressDetails addressDetails = shopifyCustomerDetails != null ? shopifyCustomerDetails.getDefault_address() : null;
		List<ShopifyItems> lineItems = shopifyDRBody.getReceipt().getLine_items();
		List<RefundItems> refunLineItems = shopifyDRBody.getReceipt().getRefund_line_items();
		String strItemsCount = lineItems!=null?lineItems.size() + "":refunLineItems.size()+"";
		DROptCultureDetails shopifyOCDetails = shopifyRequest.getOptcultureDetails();
		User magentoUser = shopifyRequest.getHead().getUser();
		DRHead drHead = shopifyRequest.getHead();
		DRHead Head = new DRHead();
		User user = new User(magentoUser.getUserName(), magentoUser.getToken(), magentoUser.getOrganizationId());
		Head.setUser(user);
		Head.setEmailReceipt(drHead.getEmailReceipt());
		Head.setEnrollCustomer(drHead.getEnrollCustomer());
		Head.setPrintReceipt(drHead.getPrintReceipt());
		Head.setRequestFormat(drHead.getRequestFormat());
		Head.setRequestSource(drHead.getRequestSource());
		Head.setRequestEndPoint(drHead.getRequestEndPoint());
		Head.setIsLoyaltyCustomer(drHead.getIsLoyaltyCustomer());
		digitalReceipt.setHead(Head);
		List<CreditCard> creditCards = new ArrayList<CreditCard>();
		COD cod = null;
		DRBody Body = new DRBody();
		
		String strDocSid = shopifyReceiptDetails.getId() + "";
		String membership = shopifyOCDetails.getMembershipNumber() != null ? shopifyOCDetails.getMembershipNumber()
				: Constants.STRING_NILL;
		String email = shopifyOCDetails!=null?shopifyOCDetails.getEmail():(shopifyCustomerDetails!=null?shopifyCustomerDetails.getEmail():"");
		String totalDiscounts=Constants.STRING_NILL;
		if(shopifyReceiptDetails!=null &&shopifyReceiptDetails.getTotal_discounts()!=null){
			totalDiscounts =shopifyReceiptDetails.getTotal_discounts(); }
		
		
		String strShipping = "0.00";
		try
		{
		  strShipping = shopifyReceiptDetails.getShipping_lines().get(0).getPrice();
		}catch(Exception e) {logger.error(e.getLocalizedMessage());}
		logger.info("shopify total discounts ="+totalDiscounts+",Shipping="+strShipping);
		Receipt receipt = new Receipt(Constants.STRING_NILL, shopifyReceiptDetails.getTotal_tax(),
				Constants.STRING_NILL, Constants.STRING_NILL, totalDiscounts, Constants.STRING_NILL,
				Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
				strItemsCount, strShipping, Constants.STRING_NILL, Constants.STRING_NILL,
				Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
				shopifyReceiptDetails.getTotal_price(), Constants.STRING_NILL,
				shopifyReceiptDetails.getSubtotal_price(), shopifyReceiptDetails.getTotal_price(),
				Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
				shopifyReceiptDetails.getCreated_at(), shopifyDRBody.getRECEIPTNUMBER()!=null?
						shopifyDRBody.getRECEIPTNUMBER():shopifyReceiptDetails.getApp_id(), Constants.STRING_NILL,
				Constants.STRING_NILL, Constants.STRING_NILL, (shopifyCustomerDetails!=null?shopifyCustomerDetails.getFirst_name():Constants.STRING_NILL),
				(shopifyCustomerDetails!=null?shopifyCustomerDetails.getLast_name():Constants.STRING_NILL), Constants.STRING_NILL,
				( addressDetails != null && addressDetails.getAddress1() != null ? addressDetails.getAddress1() : Constants.STRING_NILL) ,
				( addressDetails != null && addressDetails.getAddress2() != null ? addressDetails.getAddress2() : Constants.STRING_NILL) ,
				( addressDetails != null && addressDetails.getCity() != null ? addressDetails.getCity() : Constants.STRING_NILL) ,
				( addressDetails != null && addressDetails.getProvince() != null ? addressDetails.getProvince() : Constants.STRING_NILL), Constants.STRING_NILL,
				Constants.STRING_NILL, (shopifyCustomerDetails!=null && shopifyCustomerDetails.getDefault_address()!=null ?shopifyCustomerDetails.getDefault_address().getZip():Constants.STRING_NILL), membership, membership,
				Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
				Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
				Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
				Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
				(shopifyCustomerDetails!=null && shopifyCustomerDetails.getId() != null ? shopifyCustomerDetails.getId() : Constants.STRING_NILL),
				Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
				Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
				Constants.STRING_NILL, Constants.STRING_NILL, shopifyCustomerDetails!=null && shopifyCustomerDetails.getPhone()!= null?shopifyCustomerDetails.getPhone().replace("+", Constants.STRING_NILL):Constants.STRING_NILL, Constants.STRING_NILL,
				Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
				Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, shopifyDRBody.getSTORELOCATIONID(),
				Constants.STRING_NILL, Constants.STRING_NILL, strItemsCount, Constants.STRING_NILL,
				Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
				Constants.STRING_NILL, Constants.STRING_NILL, shopifyReceiptDetails.getTotal_tax(),
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
				Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, email,
				email, Constants.STRING_NILL, strDocSid, Constants.STRING_NILL,
				Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
				Constants.STRING_NILL, ( addressDetails != null && addressDetails.getCountry() != null ? addressDetails.getCountry() : Constants.STRING_NILL) , Constants.STRING_NILL, shopifyReceiptDetails.getNumber(),
				Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
				Constants.STRING_NILL, Constants.STRING_NILL, shopifyReceiptDetails.getTotal_tax(),
				Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
				Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
				(shopifyOCDetails != null && shopifyOCDetails.getPromotions() != null
						&& !shopifyOCDetails.getPromotions().isEmpty()
								? shopifyOCDetails.getPromotions().get(0).getCouponCode()
								: Constants.STRING_NILL),
				Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
				Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL);
		
		receipt.setBillToCustSID(Constants.STRING_NILL);
		
		List<DRItem> items = new ArrayList<DRItem>();
		Map<String, DRItem> distinctskus = new HashMap<String, DRItem>();
		//Map<String, ShopifyItems> refundskus = new HashMap<String, ShopifyItems>();
		if (drHead.getReceiptType() != null
				&& drHead.getReceiptType().equalsIgnoreCase(OCConstants.DR_RECEIPT_TYPE_SALE)) {
			List<ShopifyItems> shopifyItems = shopifyDRBody.getReceipt().getLine_items();
			double itemDiscAmt = 0; //To send disc amt per qty (later multiplied with qty in Extraction)
			double lineItemPrice= 0;
			for (ShopifyItems shopifyItem : shopifyItems) {
				logger.info("quantity "+shopifyItem.getQuantity());
				itemDiscAmt = (Double.valueOf(shopifyItem.getTotal_discount())/(Double.valueOf(shopifyItem.getQuantity())));
				lineItemPrice = (Double.valueOf(shopifyItem.getPrice())*(Double.valueOf(shopifyItem.getQuantity())));
				DRItem item = new DRItem(Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, shopifyItem.getSku(),
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
						shopifyItem.getQuantity(), shopifyItem.getSub_total(), Constants.STRING_NILL, Constants.STRING_NILL,
						String.valueOf(lineItemPrice), Constants.STRING_NILL, Constants.STRING_NILL, shopifyItem.getSub_total(),
						Constants.STRING_NILL, Constants.STRING_NILL, shopifyItem.getName(), Constants.STRING_NILL,
						//shopifyItem.getSku()
						shopifyItem.getVariant_id(), Constants.STRING_NILL, Constants.STRING_NILL,
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
				items.add(item);
				distinctskus.put(shopifyItem.getVariant_id(), item);
				logger.info("distinctskus " + distinctskus.size());

			}
			if (shopifyOCDetails != null && shopifyOCDetails.getLoyaltyRedeem() != null) {
				String tenderAmount = shopifyOCDetails.getLoyaltyRedeem().getDiscountAmount() != null
						? shopifyOCDetails.getLoyaltyRedeem().getDiscountAmount()
						: "";
				String userRedeemTender = userObj.getRedeemTender();
				if (userRedeemTender != null && !userRedeemTender.isEmpty()) {
					if (userRedeemTender.contains("" + Constants.DELIMETER_COLON)) {
						String tenderType = userObj.getRedeemTender().split("" + Constants.DELIMETER_COLON)[1];
						CreditCard CC = new CreditCard(Constants.STRING_NILL, Constants.STRING_NILL,
								Constants.STRING_NILL, Constants.STRING_NILL, tenderType, Constants.STRING_NILL,
								Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
								Constants.STRING_NILL, tenderAmount, Constants.STRING_NILL, Constants.STRING_NILL);
						creditCards.add(CC);
					} else {
						cod = new COD(Constants.STRING_NILL, tenderAmount, Constants.STRING_NILL,
								Constants.STRING_NILL);
					}

				}
			}

		} else if (drHead.getReceiptType() != null
				&& drHead.getReceiptType().equalsIgnoreCase(OCConstants.DR_RECEIPT_TYPE_RETURN)) {
			List<ShopifyItems> shopifyItems = new ArrayList<ShopifyItems>();
			List<Refunds> refunds=shopifyDRBody.getReceipt().getRefunds();
			Map<String, RefundItems> refundskus = new HashMap<String, RefundItems>();
			String refDocsId = "";
			if(refunds!=null) {
			shopifyItems = shopifyDRBody.getReceipt().getLine_items();
				//cancel order
			for(Refunds refund:refunds) {
				for(RefundItems refunditem:refund.getRefund_line_items()) {
					refundskus.put(refunditem.getLine_item().getVariant_id(),refunditem);
				}
			}
			receipt.setDocSID(shopifyReceiptDetails.getOrder_number());
			//receipt.setRefDocSID(shopifyReceiptDetails.getId());
			refDocsId = shopifyReceiptDetails.getId();
			}else {
				//refund order
				for(RefundItems refunditem:shopifyDRBody.getReceipt().getRefund_line_items()) {
					ShopifyItems refunItem = refunditem.getLine_item(); 
					refunItem.setQuantity(refunditem.getQuantity()); //App 3239
					Double totSub = Double.parseDouble(refunditem.getSubtotal())/Double.parseDouble(refunditem.getQuantity());
					refunItem.setPrice(totSub+"");
					shopifyItems.add(refunItem);
				}
				receipt.setDocSID(shopifyReceiptDetails.getId());
				//receipt.setRefDocSID(shopifyReceiptDetails.getOrder_id());
				refDocsId = shopifyReceiptDetails.getOrder_id();
			}
			double lineItemPrice=0;
			for (ShopifyItems shopifyItem : shopifyItems) {
				logger.info("quantity "+shopifyItem.getQuantity());
				lineItemPrice = (Double.valueOf(shopifyItem.getPrice())*(Double.valueOf(shopifyItem.getQuantity())));
				DRItem item = new DRItem(Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, shopifyItem.getSku(),
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
						"-"+shopifyItem.getQuantity(), shopifyItem.getPrice(), Constants.STRING_NILL, Constants.STRING_NILL,
						String.valueOf(lineItemPrice), Constants.STRING_NILL, Constants.STRING_NILL, shopifyItem.getPrice(),
						Constants.STRING_NILL, Constants.STRING_NILL, shopifyItem.getName(), Constants.STRING_NILL,
						shopifyItem.getVariant_id(), shopifyItem.getTotal_discount(), Constants.STRING_NILL,
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
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, refDocsId);
				if(refundskus.containsKey(shopifyItem.getVariant_id())) {
				distinctskus.put(shopifyItem.getVariant_id(), item);
				}
				items.add(item);
				logger.info("distinctskus " + distinctskus.size());

			}
			
			receipt.setBillToInfo1(membership);
			
			if (shopifyOCDetails != null && shopifyOCDetails.getLoyaltyRedeem() != null) {
				String tenderAmount = shopifyOCDetails.getLoyaltyRedeem().getDiscountAmount() != null
						? shopifyOCDetails.getLoyaltyRedeem().getDiscountAmount()
						: "";
				String userRedeemTender = userObj.getRedeemTender();
				if (userRedeemTender != null && !userRedeemTender.isEmpty()) {
					if (userRedeemTender.contains("" + Constants.DELIMETER_COLON)) {
						String tenderType = userObj.getRedeemTender().split("" + Constants.DELIMETER_COLON)[1];
						CreditCard CC = new CreditCard(Constants.STRING_NILL, Constants.STRING_NILL,
								Constants.STRING_NILL, Constants.STRING_NILL, tenderType, Constants.STRING_NILL,
								Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
								Constants.STRING_NILL, Constants.STRING_NILL,tenderAmount, Constants.STRING_NILL);
						creditCards.add(CC);
					} else {
						cod = new COD(Constants.STRING_NILL, Constants.STRING_NILL,
								tenderAmount,Constants.STRING_NILL);
					}

				}
			}
		}

		if (shopifyReceiptDetails.getCreated_at() != null && !shopifyReceiptDetails.getCreated_at().isEmpty()) {
			try {
				String strCreatedDate=shopifyReceiptDetails.getCreated_at();
			    receipt.setDocDate(getDocDate(strCreatedDate));
			} catch (Exception e) {
				logger.error("Exception ", e);
			}
		}

		if (drHead.getReceiptType() != null
				&& drHead.getReceiptType().equalsIgnoreCase(OCConstants.DR_RECEIPT_TYPE_RETURN))
		receipt.setReceiptType("2");
        logger.info(items);
		Body.setItems(items);
		Body.setReceipt(receipt);
		List<FC> listOfFC = new ArrayList<FC>();
		List<Payments> listOfpayments = new ArrayList<Payments>();
		List<Gift> listOfGifts = new ArrayList<Gift>();
		List<GiftCard> listOfGiftCards = new ArrayList<GiftCard>();
		List<DebitCard> listOfDebitCards = new ArrayList<DebitCard>();
		List<StoreCredit> listOfStoreCredit = new ArrayList<StoreCredit>();
		List<Check> listOfDRChecks = new ArrayList<Check>();
		List<FCCheck> listOfDRFCCecks = new ArrayList<FCCheck>();
		List<TravellerCheck> listOfDRTravellersChecks = new ArrayList<TravellerCheck>();
		if (cod != null)
			Body.setCOD(cod);
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
		if (shopifyOCDetails != null) {
			Gson gson = new Gson();
			gson = new Gson();
			String json = gson.toJson(shopifyOCDetails);
			DROptCultureDetails = gson.fromJson(json, DROptCultureDetails.class);
		}
		digitalReceipt.setOptcultureDetails(DROptCultureDetails);
		return digitalReceipt;

	}

	public DRBody convertShopifyRequest(DRHead drHead, ShopifyDRBody shopifyBody, DROptCultureDetails ocDetails,
			Users userObj) {
		logger.info("Shopify translator.DRBody.convertShopifyRequest...(ProcessDigitalReceiptServiceImpl)");
		DRBody Body = new DRBody();
		ShopifyReceiptDetails shopifyReceiptDetails = shopifyBody.getReceipt();
		ShopifyCustomerDetails shopifyCustomerDetails = shopifyBody.getReceipt().getCustomer();
		AddressDetails addressDetails = shopifyReceiptDetails != null ? shopifyReceiptDetails.getBilling_address() : null;
		List<ShopifyItems> lineItems = shopifyBody.getReceipt().getLine_items();
		List<RefundItems> refunLineItems = shopifyBody.getReceipt().getRefund_line_items();
		String strItemsCount = lineItems!=null?lineItems.size() + "":refunLineItems.size()+"";
		List<CreditCard> creditCards = new ArrayList<CreditCard>();
		COD cod = null;
		String strDocSid = shopifyReceiptDetails.getId() + "";
		String membership = ocDetails.getMembershipNumber() != null ? ocDetails.getMembershipNumber()
				: Constants.STRING_NILL;
		String email = ocDetails!=null?ocDetails.getEmail():(shopifyCustomerDetails!=null?shopifyCustomerDetails.getEmail():"");
		
		String totalDiscounts=Constants.STRING_NILL;
		if(shopifyReceiptDetails!=null &&shopifyReceiptDetails.getTotal_discounts()!=null){
			totalDiscounts =shopifyReceiptDetails.getTotal_discounts(); }
		logger.info("shopify total discounts ="+totalDiscounts);	
		
		String strShipping = "0.00";
		try
		{
		  strShipping = shopifyReceiptDetails.getShipping_lines().get(0).getPrice();
		}catch(Exception e) {logger.error(e.getLocalizedMessage());}
		
		//System.out.println("shopify total discounts ="+totalDiscounts);
		Receipt receipt = new Receipt(Constants.STRING_NILL, shopifyReceiptDetails.getTotal_tax(),
				Constants.STRING_NILL, Constants.STRING_NILL, totalDiscounts, Constants.STRING_NILL,
				Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
				strItemsCount, strShipping, Constants.STRING_NILL, Constants.STRING_NILL,
				Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
				shopifyReceiptDetails.getTotal_price(), Constants.STRING_NILL,
				shopifyReceiptDetails.getSubtotal_price(), shopifyReceiptDetails.getTotal_price(),
				Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
				shopifyReceiptDetails.getCreated_at(), shopifyBody.getRECEIPTNUMBER()!=null?
						shopifyBody.getRECEIPTNUMBER():shopifyReceiptDetails.getApp_id(), Constants.STRING_NILL,
				Constants.STRING_NILL, Constants.STRING_NILL, shopifyCustomerDetails!=null?shopifyCustomerDetails.getFirst_name():Constants.STRING_NILL,
				shopifyCustomerDetails!=null?shopifyCustomerDetails.getLast_name():Constants.STRING_NILL, Constants.STRING_NILL,
						( addressDetails != null && addressDetails.getAddress1() != null ? addressDetails.getAddress1() : Constants.STRING_NILL) ,
						( addressDetails != null && addressDetails.getAddress2() != null ? addressDetails.getAddress2() : Constants.STRING_NILL) ,
						( addressDetails != null && addressDetails.getCity() != null ? addressDetails.getCity(): Constants.STRING_NILL),  Constants.STRING_NILL, Constants.STRING_NILL,
				Constants.STRING_NILL, shopifyCustomerDetails!=null && shopifyCustomerDetails.getDefault_address()!=null ?shopifyCustomerDetails.getDefault_address().getZip():Constants.STRING_NILL, membership, membership,
				Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
				Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
				Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
				Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
				(shopifyCustomerDetails!=null && shopifyCustomerDetails.getId() != null ? shopifyCustomerDetails.getId() : Constants.STRING_NILL),
				Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
				Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
				Constants.STRING_NILL, Constants.STRING_NILL, shopifyCustomerDetails!=null && shopifyCustomerDetails.getPhone()!=null ?shopifyCustomerDetails.getPhone().replace("+", Constants.STRING_NILL):Constants.STRING_NILL, Constants.STRING_NILL,
				Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
				Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, shopifyBody.getSTORELOCATIONID(),
				Constants.STRING_NILL, Constants.STRING_NILL, strItemsCount, Constants.STRING_NILL,
				Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
				Constants.STRING_NILL, Constants.STRING_NILL, shopifyReceiptDetails.getTotal_tax(),
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
				Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, email,
				email, Constants.STRING_NILL, strDocSid, Constants.STRING_NILL,
				Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
				Constants.STRING_NILL, ( addressDetails != null && addressDetails.getCountry() != null ? addressDetails.getCountry() : Constants.STRING_NILL), Constants.STRING_NILL, shopifyReceiptDetails.getNumber(),
				Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
				Constants.STRING_NILL, Constants.STRING_NILL, shopifyReceiptDetails.getTotal_tax(),
				Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
				Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
				(ocDetails != null && ocDetails.getPromotions() != null
						&& !ocDetails.getPromotions().isEmpty()
								? ocDetails.getPromotions().get(0).getCouponCode()
								: Constants.STRING_NILL),
				Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
				Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL);
		List<DRItem> items = new ArrayList<DRItem>();
		
		receipt.setBillToCustSID(Constants.STRING_NILL); //App-3258
		
		List<ShopifyItems> shopifyItems = shopifyBody.getReceipt().getLine_items();
		Map<String, DRItem> distinctskus = new HashMap<String, DRItem>();
		//Map<String, ShopifyItems> refundskus = new HashMap<String, ShopifyItems>();
	
		if (drHead.getReceiptType() != null
				&& drHead.getReceiptType().equalsIgnoreCase(OCConstants.DR_RECEIPT_TYPE_SALE)) {
			double lineItemPrice=0;
			for (ShopifyItems shopifyItem : shopifyItems) {
				lineItemPrice = (Double.valueOf(shopifyItem.getPrice())*(Double.valueOf(shopifyItem.getQuantity())));
				DRItem item = new DRItem(Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, shopifyItem.getSku(),
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
						shopifyItem.getQuantity(), shopifyItem.getSub_total(), Constants.STRING_NILL, Constants.STRING_NILL,
						String.valueOf(lineItemPrice), Constants.STRING_NILL, Constants.STRING_NILL, shopifyItem.getSub_total(),
						Constants.STRING_NILL, Constants.STRING_NILL, shopifyItem.getName(), Constants.STRING_NILL,
						//shopifyItem.getSku()
						shopifyItem.getVariant_id(), Constants.STRING_NILL, Constants.STRING_NILL,
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
				items.add(item);
				distinctskus.put(shopifyItem.getVariant_id(), item);
			} 
			if (ocDetails != null && ocDetails.getLoyaltyRedeem() != null) {
				String tenderAmount = ocDetails.getLoyaltyRedeem().getDiscountAmount() != null
						? ocDetails.getLoyaltyRedeem().getDiscountAmount()
						: "";
				String userRedeemTender = userObj.getRedeemTender();
				if (userRedeemTender != null && !userRedeemTender.isEmpty()) {
					if (userRedeemTender.contains("" + Constants.DELIMETER_COLON)) {
						String tenderType = userObj.getRedeemTender().split("" + Constants.DELIMETER_COLON)[1];
						CreditCard CC = new CreditCard(tenderAmount, Constants.STRING_NILL, Constants.STRING_NILL,
								Constants.STRING_NILL, tenderType, Constants.STRING_NILL, Constants.STRING_NILL,
								Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
								Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL);
						creditCards.add(CC);
					} else {
						cod = new COD(tenderAmount, Constants.STRING_NILL, Constants.STRING_NILL,
								Constants.STRING_NILL);
					}

				}
			}
			
		} else if (drHead.getReceiptType() != null
				&& drHead.getReceiptType().equalsIgnoreCase(OCConstants.DR_RECEIPT_TYPE_RETURN)) {
			String returnAmount = "";
			Map<String, RefundItems> refundskus = new HashMap<String, RefundItems>();
			List<RefundItems> returnItems = new ArrayList<RefundItems>();
			List<Refunds> refunds=shopifyBody.getReceipt().getRefunds();
			String refDocsId = "";
			shopifyItems = new ArrayList<ShopifyItems>();
			/*for(Refunds refund:refunds) {
				for(RefundItems refunditem:refund.getRefund_line_items()) {
					refundskus.put(refunditem.getLine_item().getVariant_id(),refunditem);
				}
			}			
			for(RefundItems refund:returnItems) {
				refundskus.put(refund.getLine_item().getVariant_id(),refund);
			}*/
			
			if(refunds!=null) {
				shopifyItems = shopifyBody.getReceipt().getLine_items();
					//cancel order
				for(Refunds refund:refunds) {
					for(RefundItems refunditem:refund.getRefund_line_items()) {
						refundskus.put(refunditem.getLine_item().getVariant_id(),refunditem);
					}
				}
				receipt.setDocSID(shopifyReceiptDetails.getOrder_number());
				//receipt.setRefDocSID(shopifyReceiptDetails.getId());
				refDocsId = shopifyReceiptDetails.getId();
				}else {
					//refund order
					for(RefundItems refunditem:shopifyBody.getReceipt().getRefund_line_items()) {
						ShopifyItems refunItem = refunditem.getLine_item(); 
						refunItem.setQuantity(refunditem.getQuantity()); //App 3239
						Double totSub = Double.parseDouble(refunditem.getSubtotal())/Double.parseDouble(refunditem.getQuantity());
						refunItem.setPrice(totSub+"");
						shopifyItems.add(refunItem);
					}
					receipt.setDocSID(shopifyReceiptDetails.getId());
					//receipt.setRefDocSID(shopifyReceiptDetails.getOrder_id());
					refDocsId = shopifyReceiptDetails.getOrder_id();
				}
			
			double lineItemPrice=0;
			for (ShopifyItems shopifyItem : shopifyItems) {
				logger.info("quantity "+shopifyItem.getQuantity());
				lineItemPrice = (Double.valueOf(shopifyItem.getPrice())*(Double.valueOf(shopifyItem.getQuantity())));
				DRItem item = new DRItem(Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, shopifyItem.getSku(),
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
						"-"+shopifyItem.getQuantity(), shopifyItem.getPrice(), Constants.STRING_NILL, Constants.STRING_NILL,
						String.valueOf(lineItemPrice), Constants.STRING_NILL, Constants.STRING_NILL, shopifyItem.getPrice(),
						Constants.STRING_NILL, Constants.STRING_NILL, shopifyItem.getName(), Constants.STRING_NILL,
						shopifyItem.getVariant_id(), shopifyItem.getTotal_discount(), Constants.STRING_NILL,
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
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, refDocsId);
				if(refundskus.containsKey(shopifyItem.getVariant_id())) {
				items.add(item);
				distinctskus.put(shopifyItem.getVariant_id(), item);
				}
				logger.info("distinctskus " + distinctskus.size());

			}
			receipt.setDocSID(shopifyReceiptDetails.getOrder_number());
			receipt.setRefDocSID(shopifyReceiptDetails.getId());
			receipt.setBillToInfo1(membership);
			
			if (ocDetails != null && ocDetails.getLoyaltyRedeem() != null) {
				String tenderAmount = ocDetails.getLoyaltyRedeem().getDiscountAmount() != null
						? ocDetails.getLoyaltyRedeem().getDiscountAmount()
						: "";
				String userRedeemTender = userObj.getRedeemTender();
				if (userRedeemTender != null && !userRedeemTender.isEmpty()) {
					if (userRedeemTender.contains("" + Constants.DELIMETER_COLON)) {
						String tenderType = userObj.getRedeemTender().split("" + Constants.DELIMETER_COLON)[1];
						CreditCard CC = new CreditCard(Constants.STRING_NILL, Constants.STRING_NILL,
								Constants.STRING_NILL, Constants.STRING_NILL, tenderType, Constants.STRING_NILL,
								Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
								Constants.STRING_NILL, Constants.STRING_NILL,tenderAmount, Constants.STRING_NILL);
						creditCards.add(CC);
					} else {
						cod = new COD(Constants.STRING_NILL, Constants.STRING_NILL,
								tenderAmount,Constants.STRING_NILL);
					}

				}
			}
		
		}
		if (shopifyReceiptDetails.getCreated_at() != null && !shopifyReceiptDetails.getCreated_at().isEmpty()) {

			try {
				String strCreatedDate=shopifyReceiptDetails.getCreated_at();
			    receipt.setDocDate(getDocDate(strCreatedDate));
			} catch (Exception e) {
				logger.error("Exception ", e);
			}

		}

		if (drHead.getReceiptType() != null
				&& drHead.getReceiptType().equalsIgnoreCase(OCConstants.DR_RECEIPT_TYPE_RETURN))
			receipt.setReceiptType("2");
		Body.setItems(items);
		Body.setReceipt(receipt);
		List<FC> listOfFC = new ArrayList<FC>();
		List<Payments> listOfpayments = new ArrayList<Payments>();
		List<Gift> listOfGifts = new ArrayList<Gift>();
		List<GiftCard> listOfGiftCards = new ArrayList<GiftCard>();
		List<DebitCard> listOfDebitCards = new ArrayList<DebitCard>();
		List<StoreCredit> listOfStoreCredit = new ArrayList<StoreCredit>();
		List<Check> listOfDRChecks = new ArrayList<Check>();
		List<FCCheck> listOfDRFCCecks = new ArrayList<FCCheck>();
		List<TravellerCheck> listOfDRTravellersChecks = new ArrayList<TravellerCheck>();
		if (cod != null) Body.setCOD(cod);
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
	
	public static void main(String[] args) {
		String strDate = "2020-11-23T07:49:41-05:00";
		OffsetDateTime parsedDateTime = OffsetDateTime.parse(strDate);
	    ZonedDateTime dateTimeInMyTimeZone
	            = parsedDateTime.atZoneSameInstant(ZoneId.systemDefault());
	    System.out.println(dateTimeInMyTimeZone);
	    
	    DateTimeFormatter formatterMdyyyy = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm:ss");
	    LocalDateTime localDateTime = dateTimeInMyTimeZone.toLocalDateTime();
	    String strDocDate = localDateTime.format(formatterMdyyyy);
	    System.out.println("doc date"+strDocDate);
		/*DateTimeFormatter formatterYyyyMMdd = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'hh:mm:ssZZZZZ");
		DateTimeFormatter formatterMdyyyy = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm:ss");
		
		LocalDateTime localDateTime = LocalDateTime.parse(strDate,formatterYyyyMMdd);	
		String strDocDate = localDateTime.format(formatterMdyyyy);
		System.out.println("doc date"+strDocDate);*/
	}
}
