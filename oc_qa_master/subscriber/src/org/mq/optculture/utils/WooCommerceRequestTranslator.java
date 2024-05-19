package org.mq.optculture.utils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import org.mq.optculture.model.DR.wooCommerce.WooCommerceCustomerDetails;
import org.mq.optculture.model.DR.wooCommerce.WooCommerceDRBody;
import org.mq.optculture.model.DR.wooCommerce.WooCommerceDRHead;
import org.mq.optculture.model.DR.wooCommerce.WooCommerceDRRequest;
import org.mq.optculture.model.DR.wooCommerce.WooCommerceDRUser;
import org.mq.optculture.model.DR.wooCommerce.WooCommerceOptcultureDetails;
import org.mq.optculture.model.DR.wooCommerce.WooCommerceOrderData;
import org.mq.optculture.model.DR.wooCommerce.WooCommerceOrderDetails;
import org.mq.optculture.model.DR.wooCommerce.WooCommerceOrderItems;
import org.mq.optculture.model.DR.wooCommerce.WooCommercePromotions;
import org.mq.optculture.model.DR.wooCommerce.WooCommerceRefundData;
import org.mq.optculture.model.DR.wooCommerce.WooCommerceReturnDRBody;
import org.mq.optculture.model.DR.wooCommerce.WooCommerceReturnDRRequest;

import com.google.gson.Gson;

	public class WooCommerceRequestTranslator {
		private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
		public WooCommerceRequestTranslator(){
			
		}
		
		public DigitalReceipt convertWooCommerceRequest(WooCommerceDRRequest wooCommerceRequest,Users userObj){
			DigitalReceipt digitalReceipt = new DigitalReceipt();
			WooCommerceDRBody wooCommerceBody = wooCommerceRequest.getBody();
			WooCommerceOrderDetails wooCommerceReceipt = wooCommerceBody.getOrderdetails(); 
			WooCommerceCustomerDetails wooCommerceCustomerDetails = wooCommerceBody.getCustomerdetails();
			DROptCultureDetails wooCommerceOCDetails = wooCommerceRequest.getOptcultureDetails();
			User wooCommerceUser = wooCommerceRequest.getHead().getUser();
			DRHead drHead = wooCommerceRequest.getHead() ;
			DRHead Head = new DRHead();
			
			User user = new User(wooCommerceUser.getUserName(), wooCommerceUser.getToken(), wooCommerceUser.getOrganizationId());
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
			String membership = wooCommerceOCDetails.getMembershipNumber()!=null?wooCommerceOCDetails.getMembershipNumber():Constants.STRING_NILL;
			Receipt receipt = new Receipt(Constants.STRING_NILL, wooCommerceReceipt.getTotal_tax(), 
					Constants.STRING_NILL, Constants.STRING_NILL, wooCommerceReceipt.getTotal_discount(), Constants.STRING_NILL, 
					Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL,
					wooCommerceReceipt.getTotal_line_items_quantity(), wooCommerceReceipt.getTotal_shipping(), Constants.STRING_NILL, 
					Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL, 
					Constants.STRING_NILL, Constants.STRING_NILL,wooCommerceReceipt.getTotal(), 
					Constants.STRING_NILL, wooCommerceReceipt.getSubtotal(), wooCommerceReceipt.getTotal(), 
					Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, wooCommerceReceipt.getCreated_at(), 
					wooCommerceReceipt.getOrder_id(), Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
					wooCommerceCustomerDetails.getFirst_name(), wooCommerceCustomerDetails.getLast_name(), 
					Constants.STRING_NILL, Constants.STRING_NILL,Constants.STRING_NILL, 
					Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL, 
					Constants.STRING_NILL,wooCommerceCustomerDetails.getBilling_address().getPostcode(),membership,Constants.STRING_NILL, 
					Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
					Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
					Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
					Constants.STRING_NILL, (wooCommerceCustomerDetails.getCustomer_id()!=null?wooCommerceCustomerDetails.getCustomer_id():Constants.STRING_NILL), Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
					Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
					Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, wooCommerceCustomerDetails.getBilling_address().getPhone(), 
					Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
					Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL, 
					wooCommerceReceipt.getStore_id(), Constants.STRING_NILL, Constants.STRING_NILL, wooCommerceReceipt.getTotal_line_items_quantity(), 
					Constants.STRING_NILL, Constants.STRING_NILL,Constants.STRING_NILL, 
					Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
					Constants.STRING_NILL, wooCommerceReceipt.getTotal_tax(),  Constants.STRING_NILL,  
					Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL, 
					 Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL, 
					 Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL, 
					 Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL, 
					 Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL, Constants.STRING_NILL,Constants.STRING_NILL, 
					 Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL, 
					 Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL, 
					 Constants.STRING_NILL, Constants.STRING_NILL, wooCommerceCustomerDetails.getBilling_address().getEmail(), Constants.STRING_NILL , Constants.STRING_NILL, wooCommerceReceipt.getIncrement_id(), 
					 Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
					 Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
					 Constants.STRING_NILL,Constants.STRING_NILL, Constants.STRING_NILL, wooCommerceReceipt.getTotal_tax(), 
					 Constants.STRING_NILL, Constants.STRING_NILL, 
					 Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,Constants.STRING_NILL, 
					 (wooCommerceOCDetails!=null && wooCommerceOCDetails.getPromotions()!=null &&
					 !wooCommerceOCDetails.getPromotions().isEmpty() ?  wooCommerceOCDetails.getPromotions().get(0).getCouponCode() : Constants.STRING_NILL), Constants.STRING_NILL,Constants.STRING_NILL, 
							 Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
							 Constants.STRING_NILL);
			
			List<DRItem> items = new ArrayList<DRItem>();
			List<WooCommerceOrderItems> wooCommerceItems = wooCommerceBody.getOrderitems();
			Map<String, DRItem> distinctskus = new HashMap<String, DRItem>();
			if(drHead.getReceiptType()!=null && drHead.getReceiptType().equalsIgnoreCase(OCConstants.DR_RECEIPT_TYPE_SALE)) {
			double itemDiscAmt = 0; //To send disc amt per qty (later multiplied with qty in Extraction)
				double itemPrice= 0;
			for (WooCommerceOrderItems wooCommerceDRItem : wooCommerceItems) {
				
				if(wooCommerceDRItem.getDiscount_amount()!=null && !wooCommerceDRItem.getDiscount_amount().isEmpty()) itemDiscAmt = (Double.valueOf(wooCommerceDRItem.getDiscount_amount()))/(Double.valueOf(wooCommerceDRItem.getQuantity()));
				DRItem item = new DRItem(wooCommerceDRItem.getName(),Constants.STRING_NILL, Constants.STRING_NILL, 
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, wooCommerceDRItem.getName(), 
						Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL, 
						Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL, 
						Constants.STRING_NILL,Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
						wooCommerceDRItem.getTotal_tax(), wooCommerceDRItem.getQuantity(), wooCommerceDRItem.getSubtotal(), Constants.STRING_NILL, 
						Constants.STRING_NILL, wooCommerceDRItem.getTotal(), Constants.STRING_NILL, 
						String.valueOf(Double.valueOf(wooCommerceDRItem.getSubtotal())/(Double.valueOf(wooCommerceDRItem.getQuantity()))),String.valueOf(Double.valueOf(wooCommerceDRItem.getSubtotal())/(Double.valueOf(wooCommerceDRItem.getQuantity()))), wooCommerceDRItem.getTotal_tax(), Constants.STRING_NILL,
						wooCommerceDRItem.getName(),Constants.STRING_NILL,
						wooCommerceDRItem.getSku(), String.valueOf(itemDiscAmt), Constants.STRING_NILL, Constants.STRING_NILL,
						Constants.STRING_NILL,Constants.STRING_NILL, Constants.STRING_NILL,
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
						Constants.STRING_NILL, Constants.STRING_NILL, 
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,Constants.STRING_NILL, 
						Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL,
						Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
						Constants.STRING_NILL, Constants.STRING_NILL, 
						Constants.STRING_NILL, wooCommerceDRItem.getTotal_tax(), Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL);
				
				items.add(item);
				distinctskus.put(wooCommerceDRItem.getSku(),item);
				logger.info("distinctskus "+distinctskus.size());
			}
			if(wooCommerceOCDetails!=null && wooCommerceOCDetails.getLoyaltyRedeem()!=null) {
				String tenderAmount = wooCommerceOCDetails.getLoyaltyRedeem().getDiscountAmount()!=null?wooCommerceOCDetails.getLoyaltyRedeem().getDiscountAmount():"";
				String userRedeemTender = userObj.getRedeemTender();
				if(userRedeemTender!=null && !userRedeemTender.isEmpty()) {
					if(userRedeemTender.contains(""+Constants.DELIMETER_COLON)) {
						String tenderType=userObj.getRedeemTender().split(""+Constants.DELIMETER_COLON)[1];
						CreditCard CC = new CreditCard(Constants.STRING_NILL, Constants.STRING_NILL, 
								Constants.STRING_NILL,Constants.STRING_NILL,
								tenderType, Constants.STRING_NILL, 
								Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
								Constants.STRING_NILL,tenderAmount,Constants.STRING_NILL, 
								Constants.STRING_NILL);
								creditCards.add(CC);
				}else {
					cod = new COD(Constants.STRING_NILL,tenderAmount,Constants.STRING_NILL,Constants.STRING_NILL);
				}
					
				}
				}
			//cc:qty:disc:1.0-2 (ratio)
			if(wooCommerceOCDetails!=null && wooCommerceOCDetails.getPromotions()!=null) {
			List<OCPromotions> promotions = wooCommerceOCDetails.getPromotions();
			String usedItemNote = userObj.getItemNoteUsed();
			String usedReceiptNote = userObj.getReceiptNoteUsed();
			//String usedItemInfo = userObj.getItemInfo();
			PropertyDescriptor pdItemNote  = null;
			PropertyDescriptor pdReceiptNote = null;
			if(promotions!=null) {
			try {
			for(OCPromotions promotion : promotions) {
				if(promotion.getDiscountType().equalsIgnoreCase(OCConstants.DR_PROMO_DISCOUNT_TYPE_ITEM)) {
					logger.info("item level promo");
					String[] rewardRatio = null;
					if(promotion.getRewardRatio()!=null && !promotion.getRewardRatio().isEmpty()) {
						rewardRatio = promotion.getRewardRatio().split(""+Constants.DELIMETER_HIPHEN);
					}
					DRItem drItem = distinctskus.get(promotion.getItemCode());
					logger.info("drItem "+drItem);
					if(drItem!=null) {
						pdItemNote = new PropertyDescriptor(usedItemNote, drItem.getClass());
						if(pdItemNote == null ) continue;
						Object itemNoteObj = pdItemNote.getReadMethod().invoke(drItem);
						if(itemNoteObj == null) continue;
						String itemNoteStr=promotion.getCouponCode()+""+Constants.DELIMETER_COLON+promotion.getQuantityDiscounted()+""+Constants.DELIMETER_COLON+
								promotion.getItemDiscount()+(rewardRatio!=null ?""+Constants.DELIMETER_COLON+promotion.getRewardRatio():"");
						String itemNoteObjStr = itemNoteObj.toString().isEmpty()?itemNoteStr:itemNoteObj.toString()+""+Constants.DELIMITER_PIPE;
						Method setterMethod = drItem.getClass().getDeclaredMethod(pdItemNote.getWriteMethod().getName(), pdItemNote.getPropertyType());
		                setterMethod.invoke(drItem,itemNoteObjStr.toString());
		                /*if(drItem.getDocItemOrigPrc()!=null && !drItem.getDocItemOrigPrc().isEmpty()) itemPrice = Double.valueOf(drItem.getDocItemOrigPrc());
		                itemPrice = (itemPrice*Double.valueOf(drItem.getQty()))-(Double.valueOf(promotion.getItemDiscount())*Double.valueOf(promotion.getQuantityDiscounted()));
		                itemPrice = itemPrice/Double.valueOf(drItem.getQty());
		                drItem.setDocItemOrigPrc(String.valueOf(itemPrice));*/
						logger.info("itemPriceStr :"+itemPrice);
					}
				}else if(promotion.getDiscountType().equalsIgnoreCase(OCConstants.DR_PROMO_DISCOUNT_TYPE_RECEIPT)) {
					String receiptNoteStr=promotion.getCouponCode()+""+Constants.DELIMETER_COLON+promotion.getDiscountAmount();
					pdReceiptNote = new PropertyDescriptor(usedReceiptNote, receipt.getClass());
					if(pdReceiptNote == null ) continue;
					Object receiptNoteObj = pdReceiptNote.getReadMethod().invoke(receipt);
					if(receiptNoteObj == null) continue;
					String receiptNoteObjStr = receiptNoteObj.toString().isEmpty()?receiptNoteStr:receiptNoteObj.toString()+""+Constants.DELIMITER_PIPE;
					Method setterMethod = receipt.getClass().getDeclaredMethod(pdReceiptNote.getWriteMethod().getName(), pdReceiptNote.getPropertyType());
	                setterMethod.invoke(receipt,receiptNoteObjStr.toString());
			}
			}	
			logger.info("itemDiscAmt :"+itemDiscAmt);
			}catch(Exception e) {
				logger.error("Exception ",e);
			}
			}
			}
			}else if(drHead.getReceiptType()!=null && drHead.getReceiptType().equalsIgnoreCase(OCConstants.DR_RECEIPT_TYPE_RETURN)) {
				
				for (WooCommerceOrderItems wooCommerceDRItem : wooCommerceItems) {
					
					//itemDiscAmt = (Double.valueOf(wooCommerceDRItem.getDiscount_amount())/(Double.valueOf(wooCommerceDRItem.getQty_ordered())));
					/*double billedPrice = wooCommerceDRItem.getDiscount_amount()!=null?
							(Double.valueOf(wooCommerceDRItem.getSubtotal())-Double.valueOf(wooCommerceDRItem.getDiscount_amount()))/(Double.valueOf(wooCommerceDRItem.getQuantity()))
							:(Double.valueOf(wooCommerceDRItem.getSubtotal())/Double.valueOf(wooCommerceDRItem.getQuantity()));*/
					DRItem item = new DRItem(wooCommerceDRItem.getName(),Constants.STRING_NILL, Constants.STRING_NILL, 
							Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
							Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, wooCommerceDRItem.getName(), 
							Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL, 
							Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL, 
							Constants.STRING_NILL,Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
							wooCommerceDRItem.getTotal_tax(), wooCommerceDRItem.getQuantity(), wooCommerceDRItem.getSubtotal(), Constants.STRING_NILL, 
							Constants.STRING_NILL, wooCommerceDRItem.getTotal(), Constants.STRING_NILL, 
							String.valueOf(Double.valueOf(wooCommerceDRItem.getSubtotal())/(Double.valueOf(wooCommerceDRItem.getQuantity()))),String.valueOf(Double.valueOf(wooCommerceDRItem.getSubtotal())/(Double.valueOf(wooCommerceDRItem.getQuantity()))), wooCommerceDRItem.getTotal_tax(), Constants.STRING_NILL,
							wooCommerceDRItem.getName(),Constants.STRING_NILL,
							wooCommerceDRItem.getSku(), Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
							Constants.STRING_NILL,Constants.STRING_NILL, Constants.STRING_NILL,
							Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
							Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
							Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
							Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
							Constants.STRING_NILL, Constants.STRING_NILL, 
							Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
							Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
							Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
							Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,Constants.STRING_NILL, 
							Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL,
							Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
							Constants.STRING_NILL, Constants.STRING_NILL, 
							Constants.STRING_NILL, wooCommerceDRItem.getTotal_tax(), Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL);
					
					if(wooCommerceReceipt.getStatus().equalsIgnoreCase("cancelled")) {
						receipt.setTotal(wooCommerceReceipt.getTotal());
						double itemQty=(Double.valueOf(wooCommerceDRItem.getQuantity()));
						item.setQty(String.valueOf((itemQty*=-1)));
						items.add(item);
						receipt.setRefDocSID(wooCommerceReceipt.getIncrement_id());
						receipt.setBillToInfo1(membership);
					}
					item.setRefReceipt(wooCommerceReceipt.getOrder_id());
					item.setRefStoreCode(wooCommerceReceipt.getStore_id());
					distinctskus.put(wooCommerceDRItem.getSku(),item);
					logger.info("distinctskus "+distinctskus.size());
				}
				if(wooCommerceOCDetails!=null && wooCommerceOCDetails.getLoyaltyRedeemReversal()!=null && !wooCommerceOCDetails.getLoyaltyRedeemReversal().isEmpty()) {
					String tenderAmount = wooCommerceOCDetails.getLoyaltyRedeemReversal()!=null?wooCommerceOCDetails.getLoyaltyRedeemReversal():"";
					String userRedeemTender = userObj.getRedeemTender();
					if(userRedeemTender!=null && !userRedeemTender.isEmpty()) {
						if(userRedeemTender.contains(""+Constants.DELIMETER_COLON)) {
							String tenderType=userObj.getRedeemTender().split(""+Constants.DELIMETER_COLON)[1];
							CreditCard CC = new CreditCard(Constants.STRING_NILL, Constants.STRING_NILL, 
									Constants.STRING_NILL,Constants.STRING_NILL,
									tenderType, Constants.STRING_NILL, 
									Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
									Constants.STRING_NILL,Constants.STRING_NILL,tenderAmount, 
									Constants.STRING_NILL);
									creditCards.add(CC);
					}else {
						cod = new COD(Constants.STRING_NILL,Constants.STRING_NILL,tenderAmount,Constants.STRING_NILL);
					}
						
					}
					}
			}
			if(wooCommerceReceipt.getCreated_at() != null && !wooCommerceReceipt.getCreated_at().isEmpty()) {
	    		
				try {
					
					String fieldValue = wooCommerceReceipt.getCreated_at();
					DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Date date = (Date)formatter.parse(fieldValue); 
					
					formatter = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss");
					String docDate = formatter.format(date);
					receipt.setDocDate(docDate);
					
				} catch (ParseException e) {
						logger.error("Exception ",e);
					}
	    		
	    	}
			
			if(drHead.getReceiptType()!=null && drHead.getReceiptType().equalsIgnoreCase(OCConstants.DR_RECEIPT_TYPE_RETURN)) receipt.setReceiptType("2");
			
			Body.setItems(items);
			Body.setReceipt(receipt);
			
			
			//List<CreditCard> creditCards = new ArrayList<CreditCard>();
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
			//COD cod = null;
			Deposit deposit =null;
			Cash cash = null;
			
			if(cod != null )Body.setCOD(cod);
			//if(cash != null) Body.setCash(cash);
			//if(deposit != null) Body.setDeposit(deposit);
			//if(charge != null) Body.setCharge(charge);
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
			
			DROptCultureDetails DROptCultureDetails =null;
			if(wooCommerceOCDetails!=null) {
			Gson gson = new Gson();
			gson = new Gson();
			String json = gson.toJson(wooCommerceOCDetails);
			DROptCultureDetails = gson.fromJson(json,DROptCultureDetails.class);
			}
			digitalReceipt.setOptcultureDetails(DROptCultureDetails);
			return digitalReceipt;
			
		}
		
	public DRBody convertWooCommerceRequest(DRHead drHead,WooCommerceDRBody wooCommerceBody,DROptCultureDetails wooCommerceOCDetails,Users userObj){
			
			logger.info("entered convert");
			
			DRBody Body = new DRBody();
			List<CreditCard> creditCards = new ArrayList<CreditCard>();
			COD cod = null;
			WooCommerceOrderDetails wooCommerceReceipt= wooCommerceBody.getOrderdetails();
			String membership = wooCommerceOCDetails.getMembershipNumber()!=null?wooCommerceOCDetails.getMembershipNumber():Constants.STRING_NILL;
			Receipt receipt = new Receipt(Constants.STRING_NILL, wooCommerceReceipt.getTotal_tax(), 
					Constants.STRING_NILL, Constants.STRING_NILL, wooCommerceReceipt.getTotal_discount(), Constants.STRING_NILL, 
					Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL,
					wooCommerceReceipt.getTotal_line_items_quantity(), wooCommerceReceipt.getTotal_shipping(), Constants.STRING_NILL, 
					Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL, 
					Constants.STRING_NILL, Constants.STRING_NILL,wooCommerceReceipt.getTotal(), 
					Constants.STRING_NILL, wooCommerceReceipt.getSubtotal(), wooCommerceReceipt.getTotal(), 
					Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, wooCommerceReceipt.getCreated_at(), 
					wooCommerceReceipt.getOrder_id(), Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
					wooCommerceBody.getCustomerdetails().getFirst_name(), wooCommerceBody.getCustomerdetails().getLast_name(), 
					Constants.STRING_NILL, Constants.STRING_NILL,Constants.STRING_NILL, 
					Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL, 
					Constants.STRING_NILL,wooCommerceBody.getCustomerdetails().getBilling_address().getPostcode(),membership, Constants.STRING_NILL, 
					Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
					Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
					Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
					Constants.STRING_NILL, wooCommerceBody.getCustomerdetails().getCustomer_id(), Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
					Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
					Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, wooCommerceBody.getCustomerdetails().getBilling_address().getPhone(), 
					Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
					Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL, 
					wooCommerceReceipt.getStore_id(), Constants.STRING_NILL, Constants.STRING_NILL, wooCommerceReceipt.getTotal_line_items_quantity(), 
					Constants.STRING_NILL, Constants.STRING_NILL,Constants.STRING_NILL, 
					Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
					Constants.STRING_NILL, wooCommerceReceipt.getTotal_tax(),  Constants.STRING_NILL,  
					Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL, 
					 Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL, 
					 Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL, 
					 Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL, 
					 Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL, Constants.STRING_NILL,Constants.STRING_NILL, 
					 Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL, 
					 Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL, 
					 Constants.STRING_NILL, Constants.STRING_NILL, wooCommerceBody.getCustomerdetails().getBilling_address().getEmail(), Constants.STRING_NILL , Constants.STRING_NILL, wooCommerceReceipt.getIncrement_id(), 
					 Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
					 Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
					 Constants.STRING_NILL,Constants.STRING_NILL, Constants.STRING_NILL, wooCommerceReceipt.getTotal_tax(), 
					 Constants.STRING_NILL, Constants.STRING_NILL, 
					 Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,Constants.STRING_NILL, 
					 (wooCommerceOCDetails!=null && wooCommerceOCDetails.getPromotions()!=null && !wooCommerceOCDetails.getPromotions().isEmpty() ?  wooCommerceOCDetails.getPromotions().get(0).getCouponCode() : Constants.STRING_NILL), Constants.STRING_NILL,Constants.STRING_NILL, 
							 Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
							 Constants.STRING_NILL);
			
			List<DRItem> items = new ArrayList<DRItem>();
			List<WooCommerceOrderItems> wooCommerceItems = wooCommerceBody.getOrderitems();
			Map<String, DRItem> distinctskus = new HashMap<String, DRItem>();
			double itemDiscAmt = 0; //To send disc amt per qty (later multiplied with qty in Extraction)
			double itemPrice = 0;
			for (WooCommerceOrderItems wooCommerceDRItem : wooCommerceItems) {
				
				itemDiscAmt = (Double.valueOf(wooCommerceDRItem.getDiscount_amount()))/((Double.valueOf(wooCommerceDRItem.getQuantity())));
				DRItem item = new DRItem(wooCommerceDRItem.getName(),Constants.STRING_NILL, Constants.STRING_NILL, 
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, wooCommerceDRItem.getName(), 
						Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL, 
						Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL, 
						Constants.STRING_NILL,Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
						wooCommerceDRItem.getTotal_tax(), wooCommerceDRItem.getQuantity(), wooCommerceDRItem.getSubtotal(), Constants.STRING_NILL, 
						Constants.STRING_NILL, wooCommerceDRItem.getTotal(), Constants.STRING_NILL, 
						String.valueOf(Double.valueOf(wooCommerceDRItem.getSubtotal())/(Double.valueOf(wooCommerceDRItem.getQuantity()))),String.valueOf(Double.valueOf(wooCommerceDRItem.getSubtotal())/(Double.valueOf(wooCommerceDRItem.getQuantity()))), wooCommerceDRItem.getTotal_tax(), Constants.STRING_NILL,
						wooCommerceDRItem.getName(),Constants.STRING_NILL,
						wooCommerceDRItem.getSku(), String.valueOf(itemDiscAmt), Constants.STRING_NILL, Constants.STRING_NILL,
						Constants.STRING_NILL,Constants.STRING_NILL, Constants.STRING_NILL,
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
						Constants.STRING_NILL, Constants.STRING_NILL, 
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,Constants.STRING_NILL, 
						Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL,
						Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
						Constants.STRING_NILL, Constants.STRING_NILL, 
						Constants.STRING_NILL, wooCommerceDRItem.getTotal_tax(), Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL);
				
				if(wooCommerceReceipt.getStatus()!=null && !wooCommerceReceipt.getStatus().isEmpty() && 
						wooCommerceReceipt.getStatus().equalsIgnoreCase("cancelled")) {
					double itemQty=(Double.valueOf(wooCommerceDRItem.getQuantity()));
					item.setQty(String.valueOf((itemQty*=-1)));
				}
				items.add(item);
				distinctskus.put(wooCommerceDRItem.getSku(),item);
				logger.info("distinctskus "+distinctskus.size());
			}
			if(wooCommerceOCDetails!=null && wooCommerceOCDetails.getLoyaltyRedeem()!=null) {
				String tenderAmount = wooCommerceOCDetails.getLoyaltyRedeem().getDiscountAmount()!=null?wooCommerceOCDetails.getLoyaltyRedeem().getDiscountAmount():"";
				String userRedeemTender = userObj.getRedeemTender();
				if(userRedeemTender!=null && !userRedeemTender.isEmpty()) {
					if(userRedeemTender.contains(""+Constants.DELIMETER_COLON)) {
						String tenderType=userObj.getRedeemTender().split(""+Constants.DELIMETER_COLON)[1];
						CreditCard CC = new CreditCard(tenderAmount, Constants.STRING_NILL, 
								Constants.STRING_NILL,Constants.STRING_NILL,
								tenderType, Constants.STRING_NILL, 
								Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
								Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL, 
								Constants.STRING_NILL);
								creditCards.add(CC);
				}else {
					cod = new COD(tenderAmount,Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL);
				}
					
				}
				}
			//cc:qty:disc:1.0-2 (ratio)
			if(wooCommerceOCDetails!=null && wooCommerceOCDetails.getPromotions()!=null)  {
			List<OCPromotions> promotions = wooCommerceOCDetails.getPromotions();
			String usedItemNote = userObj.getItemNoteUsed();
			String usedReceiptNote = userObj.getReceiptNoteUsed();
			//String usedItemInfo = userObj.getItemInfo();
			PropertyDescriptor pdItemNote  = null;
			PropertyDescriptor pdReceiptNote = null;
			try {
			for(OCPromotions promotion : promotions) {
				if(promotion.getDiscountType().equalsIgnoreCase(OCConstants.DR_PROMO_DISCOUNT_TYPE_ITEM)) {
					logger.info("item level promo");
					String[] rewardRatio = null;
					if(promotion.getRewardRatio()!=null && !promotion.getRewardRatio().isEmpty()) {
						rewardRatio = promotion.getRewardRatio().split(""+Constants.DELIMETER_HIPHEN);
					}
					
					DRItem drItem = distinctskus.get(promotion.getItemCode());
					if(drItem!=null) {
						pdItemNote = new PropertyDescriptor(usedItemNote, drItem.getClass());
						if(pdItemNote == null ) continue;
						Object itemNoteObj = pdItemNote.getReadMethod().invoke(drItem);
						if(itemNoteObj == null) continue;
						String itemNoteStr=promotion.getCouponCode()+""+Constants.DELIMETER_COLON+promotion.getQuantityDiscounted()+""+Constants.DELIMETER_COLON+
								promotion.getItemDiscount()+(rewardRatio!=null ?""+Constants.DELIMETER_COLON+promotion.getRewardRatio():"");
						String itemNoteObjStr = itemNoteObj.toString().isEmpty()?itemNoteStr:itemNoteObj.toString()+""+Constants.DELIMITER_PIPE;
						Method setterMethod = drItem.getClass().getDeclaredMethod(pdItemNote.getWriteMethod().getName(), pdItemNote.getPropertyType());
		                setterMethod.invoke(drItem,itemNoteObjStr.toString());
		                /*if(drItem.getDocItemOrigPrc()!=null && !drItem.getDocItemOrigPrc().isEmpty()) itemPrice = Double.valueOf(drItem.getDocItemOrigPrc());
		                itemPrice = (itemPrice*Double.valueOf(drItem.getQty()))-(Double.valueOf(promotion.getItemDiscount())*Double.valueOf(promotion.getQuantityDiscounted()));
		                itemPrice = itemPrice/Double.valueOf(drItem.getQty());
		                drItem.setDocItemOrigPrc(String.valueOf(itemPrice));*/
						logger.info("itemNoteObjStr :"+itemNoteObjStr.toString());
					}
				}else if(promotion.getDiscountType().equalsIgnoreCase(OCConstants.DR_PROMO_DISCOUNT_TYPE_RECEIPT)) {
					String receiptNoteStr=promotion.getCouponCode()+""+Constants.DELIMETER_COLON+promotion.getDiscountAmount();
					pdReceiptNote = new PropertyDescriptor(usedReceiptNote, receipt.getClass());
					if(pdReceiptNote == null ) continue;
					Object receiptNoteObj = pdReceiptNote.getReadMethod().invoke(receipt);
					if(receiptNoteObj == null) continue;
					String receiptNoteObjStr = receiptNoteObj.toString().isEmpty()?receiptNoteStr:receiptNoteObj.toString()+""+Constants.DELIMITER_PIPE;
					Method setterMethod = receipt.getClass().getDeclaredMethod(pdReceiptNote.getWriteMethod().getName(), pdReceiptNote.getPropertyType());
	                setterMethod.invoke(receipt,receiptNoteObjStr.toString());
					logger.info("receiptNoteObjStr :"+receiptNoteObjStr.toString());
			}
			logger.info("itemDiscAmt :"+itemDiscAmt);
			}
			}catch(Exception e) {
				logger.error("Exception ",e);
			}
			}
			if(wooCommerceReceipt.getCreated_at() != null && !wooCommerceReceipt.getCreated_at().isEmpty()) {
	    		
				try {
					
					String fieldValue = wooCommerceReceipt.getCreated_at();
					DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Date date = (Date)formatter.parse(fieldValue); 
					
					formatter = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss");
					String docDate = formatter.format(date);
					receipt.setDocDate(docDate);
					
				} catch (ParseException e) {
						logger.error("Exception ",e);
					}
	    		
	    	}
			
			if(drHead.getReceiptType()!=null && drHead.getReceiptType().equalsIgnoreCase(OCConstants.DR_RECEIPT_TYPE_RETURN)) receipt.setReceiptType("2");
			
			Body.setItems(items);
			Body.setReceipt(receipt);
			
			
			//List<CreditCard> creditCards = new ArrayList<CreditCard>();
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
			//COD cod = null;
			Deposit deposit =null;
			Cash cash = null;
			
			if(wooCommerceOCDetails!=null && wooCommerceOCDetails.getLoyaltyRedeem()!=null) {
				String tenderAmount = wooCommerceOCDetails.getLoyaltyRedeem().getDiscountAmount()!=null?wooCommerceOCDetails.getLoyaltyRedeem().getDiscountAmount():"";
				String userRedeemTender = userObj.getRedeemTender();
				if(userRedeemTender!=null && !userRedeemTender.isEmpty()) {
					if(userRedeemTender.contains(""+Constants.DELIMETER_COLON)) {
						String tenderType=userObj.getRedeemTender().split(""+Constants.DELIMETER_COLON)[1];
						CreditCard CC = new CreditCard(tenderAmount, Constants.STRING_NILL, 
								Constants.STRING_NILL,Constants.STRING_NILL,
								tenderType, Constants.STRING_NILL, 
								Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
								Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL, 
								Constants.STRING_NILL);
								creditCards.add(CC);
				}else {
					cod = new COD(tenderAmount,Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL);
				}
					
				}
				}
						
			if(cod != null )Body.setCOD(cod);
			//if(cash != null) Body.setCash(cash);
			//if(deposit != null) Body.setDeposit(deposit);
			//if(charge != null) Body.setCharge(charge);
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
	
	public DigitalReceipt convertWooCommerceRefundRequest(WooCommerceReturnDRRequest refundReq,Users userObj){
		DigitalReceipt digitalReceipt = new DigitalReceipt();
		DRHead drHead = refundReq.getHead();
		WooCommerceReturnDRBody wooCommerceBody = refundReq.getBody(); 
		DROptCultureDetails wooCommerceOCDetails = refundReq.getOptcultureDetails();
		DRBody Body = new DRBody();
		List<CreditCard> creditCards = new ArrayList<CreditCard>();
		COD cod = null;
		WooCommerceOrderDetails wooCommerceReceipt= wooCommerceBody.getOrderdetails();
		Receipt receipt = new Receipt(Constants.STRING_NILL, wooCommerceReceipt.getTotal_tax(), 
				Constants.STRING_NILL, Constants.STRING_NILL, wooCommerceReceipt.getTotal_discount(), Constants.STRING_NILL, 
				Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL,
				wooCommerceReceipt.getTotal_line_items_quantity(), wooCommerceReceipt.getTotal_shipping(), Constants.STRING_NILL, 
				Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL, 
				Constants.STRING_NILL, Constants.STRING_NILL,wooCommerceReceipt.getTotal(), 
				Constants.STRING_NILL, wooCommerceReceipt.getSubtotal(), wooCommerceReceipt.getTotal(), 
				Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, wooCommerceReceipt.getCreated_at(), 
				wooCommerceReceipt.getOrder_id(), Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
				wooCommerceBody.getCustomerdetails().getFirst_name(), wooCommerceBody.getCustomerdetails().getLast_name(), 
				Constants.STRING_NILL, Constants.STRING_NILL,Constants.STRING_NILL, 
				Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL, 
				Constants.STRING_NILL,Constants.STRING_NILL, wooCommerceOCDetails.getMembershipNumber(), Constants.STRING_NILL, 
				Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
				Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
				Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
				Constants.STRING_NILL, wooCommerceBody.getCustomerdetails().getCustomer_id(), Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
				Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
				Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, wooCommerceBody.getCustomerdetails().getBilling_address().getPhone(), 
				Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
				Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL, 
				wooCommerceReceipt.getStore_id(), Constants.STRING_NILL, Constants.STRING_NILL, wooCommerceReceipt.getTotal_line_items_quantity(), 
				Constants.STRING_NILL, Constants.STRING_NILL,Constants.STRING_NILL, 
				Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
				Constants.STRING_NILL, wooCommerceReceipt.getTotal_tax(),  Constants.STRING_NILL,  
				Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL, 
				 Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL, 
				 Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL, 
				 Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL, 
				 Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL, Constants.STRING_NILL,Constants.STRING_NILL, 
				 Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL, 
				 Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL, 
				 Constants.STRING_NILL, Constants.STRING_NILL, wooCommerceBody.getCustomerdetails().getBilling_address().getEmail(), Constants.STRING_NILL , Constants.STRING_NILL, wooCommerceReceipt.getIncrement_id(), 
				 Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
				 Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
				 Constants.STRING_NILL,Constants.STRING_NILL, Constants.STRING_NILL, wooCommerceReceipt.getTotal_tax(), 
				 Constants.STRING_NILL, Constants.STRING_NILL, 
				 Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,Constants.STRING_NILL, 
				 (wooCommerceOCDetails!=null && wooCommerceOCDetails.getPromotions()!=null && !wooCommerceOCDetails.getPromotions().isEmpty() ?  wooCommerceOCDetails.getPromotions().get(0).getCouponCode() : Constants.STRING_NILL), Constants.STRING_NILL,Constants.STRING_NILL, 
						 Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
						 Constants.STRING_NILL);
		
		List<DRItem> items = new ArrayList<DRItem>();
		List<WooCommerceOrderData> wooCommerceItems = wooCommerceBody.getOrderitems().getOrderdata();
		List<WooCommerceRefundData> wooCommerceRefundItems = wooCommerceBody.getOrderitems().getRefunddata();
		Map<String,String> refundskus = new HashMap<String,String>();
		if(wooCommerceRefundItems!=null && wooCommerceRefundItems.size()>0) {
			for (WooCommerceRefundData refundItem : wooCommerceRefundItems) {
					refundskus.put(refundItem.getRefund_item_id(),refundItem.getQty());
			}
		}else if(wooCommerceRefundItems!=null && wooCommerceRefundItems.size()==0) {
			for (WooCommerceOrderData wooCommerceDRItem : wooCommerceItems) {
				refundskus.put(wooCommerceDRItem.getId(),wooCommerceDRItem.getQuantity());
			}
		}
		Map<String, DRItem> distinctskus = new HashMap<String, DRItem>();
		double itemDiscAmt =0;
			for (WooCommerceOrderData wooCommerceDRItem : wooCommerceItems) {
				double billedPrice = wooCommerceDRItem.getDiscount_amount()!=null?
						(Double.valueOf(wooCommerceDRItem.getSubtotal())-Double.valueOf(wooCommerceDRItem.getDiscount_amount()))/(Double.valueOf(wooCommerceDRItem.getQuantity()))
						:(Double.valueOf(wooCommerceDRItem.getSubtotal())/Double.valueOf(wooCommerceDRItem.getQuantity()));
				itemDiscAmt = (Double.valueOf(wooCommerceDRItem.getDiscount_amount()))/((Double.valueOf(wooCommerceDRItem.getQuantity())));
				DRItem item = new DRItem(wooCommerceDRItem.getName(),Constants.STRING_NILL, Constants.STRING_NILL, 
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, wooCommerceDRItem.getName(), 
						Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL, 
						Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL, 
						Constants.STRING_NILL,Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
						wooCommerceDRItem.getTotal_tax(), Constants.STRING_NILL, wooCommerceDRItem.getSubtotal(), Constants.STRING_NILL, 
						Constants.STRING_NILL, wooCommerceDRItem.getTotal(), Constants.STRING_NILL, 
						String.valueOf(Double.valueOf(wooCommerceDRItem.getSubtotal())/(Double.valueOf(wooCommerceDRItem.getQuantity()))),String.valueOf(billedPrice), wooCommerceDRItem.getTotal_tax(), Constants.STRING_NILL,
						wooCommerceDRItem.getName(),Constants.STRING_NILL,
						wooCommerceDRItem.getSku(), String.valueOf(itemDiscAmt), Constants.STRING_NILL, Constants.STRING_NILL,
						Constants.STRING_NILL,Constants.STRING_NILL, Constants.STRING_NILL,
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
						Constants.STRING_NILL, Constants.STRING_NILL, 
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,Constants.STRING_NILL, 
						Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL,
						Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL, Constants.STRING_NILL, 
						Constants.STRING_NILL, Constants.STRING_NILL, 
						Constants.STRING_NILL, wooCommerceDRItem.getTotal_tax(), Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL);
				if(wooCommerceReceipt.getStatus()!=null && wooCommerceReceipt.getStatus().equalsIgnoreCase("cancelled")) {
					receipt.setTotal(wooCommerceReceipt.getTotal());
					double itemQty=(Double.valueOf(wooCommerceDRItem.getQuantity()));
					item.setQty(String.valueOf((itemQty*=-1)));
					items.add(item);
				}else if(refundskus!=null && refundskus.size()!=0){
					if(refundskus.get(wooCommerceDRItem.getId())!=null) {
						receipt.setTotal(wooCommerceReceipt.getRefund_details().getRefund_amount());
						//double itemQty=(Double.valueOf(refundskus.get(refundskus.get(wooCommerceDRItem.getId()))));
						item.setQty("-"+refundskus.get(wooCommerceDRItem.getId()));
						items.add(item);
						item.setRefReceipt(wooCommerceReceipt.getOrder_id());
						item.setRefStoreCode(wooCommerceReceipt.getStore_id());
					}
				}
				distinctskus.put(wooCommerceDRItem.getSku(),item);
			}
			if(wooCommerceOCDetails!=null && wooCommerceOCDetails.getLoyaltyRedeemReversal()!=null) {
				String tenderAmount = wooCommerceOCDetails.getLoyaltyRedeemReversal()!=null?wooCommerceOCDetails.getLoyaltyRedeemReversal():"";
				String userRedeemTender = userObj.getRedeemTender();
				if(userRedeemTender!=null && !userRedeemTender.isEmpty()) {
					if(userRedeemTender.contains(""+Constants.DELIMETER_COLON)) {
						String tenderType=userObj.getRedeemTender().split(""+Constants.DELIMETER_COLON)[1];
						CreditCard CC = new CreditCard(Constants.STRING_NILL, 
								Constants.STRING_NILL,Constants.STRING_NILL,
								tenderType, Constants.STRING_NILL, 
								Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
								Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL,tenderAmount, 
								Constants.STRING_NILL);
								creditCards.add(CC);
				}else {
					cod = new COD(Constants.STRING_NILL,Constants.STRING_NILL,tenderAmount,Constants.STRING_NILL);
				}
					
				}
				}
		
		if(wooCommerceReceipt.getCreated_at() != null && !wooCommerceReceipt.getCreated_at().isEmpty()) {
    		
			try {
				
				String fieldValue = wooCommerceReceipt.getCreated_at();
				DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date date = (Date)formatter.parse(fieldValue); 
				
				formatter = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss");
				String docDate = formatter.format(date);
				receipt.setDocDate(docDate);
				
			} catch (ParseException e) {
					logger.error("Exception ",e);
				}
    		
    	}
		
		if(drHead.getReceiptType()!=null && drHead.getReceiptType().equalsIgnoreCase(OCConstants.DR_RECEIPT_TYPE_RETURN)) receipt.setReceiptType("2");
		
		digitalReceipt.setHead(drHead);
		digitalReceipt.setOptcultureDetails(wooCommerceOCDetails);
		Body.setItems(items);
		Body.setReceipt(receipt);
		
		
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
		Deposit deposit =null;
		Cash cash = null;
		
		if(cod != null )Body.setCOD(cod);
		//if(cash != null) Body.setCash(cash);
		//if(deposit != null) Body.setDeposit(deposit);
		//if(charge != null) Body.setCharge(charge);
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
		return digitalReceipt;
		
	}

		
	}
