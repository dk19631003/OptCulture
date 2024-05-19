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
import org.mq.captiway.scheduler.OptCultureCSVFileUpload;
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
import org.mq.optculture.model.DR.wooCommerce.WooCommerceOrderData;
import org.mq.optculture.model.DR.wooCommerce.WooCommerceOrderDetails;
import org.mq.optculture.model.DR.wooCommerce.WooCommerceOrderItems;
import org.mq.optculture.model.DR.wooCommerce.WooCommercePromotions;
import org.mq.optculture.model.DR.wooCommerce.WooCommerceRefundData;
import org.mq.optculture.model.DR.wooCommerce.WooCommerceReturnDRBody;
import org.mq.optculture.model.DR.wooCommerce.WooCommerceReturnDRRequest;

	public class WooCommerceRequestTranslator {
		private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
		public WooCommerceRequestTranslator(){
			
		}
		
		public DRBody convertWooCommerceRequest(WooCommerceDRRequest wooCommerceRequest,Users userObj){
			
			
			DRBody Body = new DRBody();
			WooCommerceDRBody wooCommerceBody = wooCommerceRequest.getBody();
			WooCommerceOrderDetails wooCommerceReceipt= wooCommerceBody.getOrderdetails();
			DROptCultureDetails ocDetails = wooCommerceRequest.getOptcultureDetails();
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
					Constants.STRING_NILL,Constants.STRING_NILL, wooCommerceBody.getMembership(), Constants.STRING_NILL, 
					Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
					Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
					Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
					Constants.STRING_NILL, wooCommerceBody.getCustomerdetails().getCustomer_id(), Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
					Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
					Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, wooCommerceBody.getCustomerdetails().getBilling_address().getPhone(), 
					Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
					Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL, 
					Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, wooCommerceReceipt.getTotal_line_items_quantity(), 
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
					 (ocDetails!=null && ocDetails.getPromotions()!=null && !ocDetails.getPromotions().isEmpty() ? ocDetails.getPromotions().get(0).getCouponCode() : Constants.STRING_NILL), Constants.STRING_NILL,Constants.STRING_NILL, 
							 Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
							 Constants.STRING_NILL);
			
			List<DRItem> items = new ArrayList<DRItem>();
			List<WooCommerceOrderItems> wooCommerceItems = wooCommerceBody.getOrderitems();
			//List<WooCommerceRefundData> wooCommerceRefundItems = wooCommerceBody.getOrderitems().getRefunddata();
			Map<String, DRItem> distinctskus = new HashMap<String, DRItem>();
			Map<String,String> refundskus = new HashMap<String,String>();
			/*if(wooCommerceRefundItems!=null) {
				for (WooCommerceRefundData refundItem : wooCommerceRefundItems) {
						refundskus.put(refundItem.getRefund_item_id(),refundItem.getQty());
				}
				}	*/		
			if(wooCommerceRequest.getHead().getReceiptType()!=null && wooCommerceRequest.getHead().getReceiptType().equalsIgnoreCase(OCConstants.DR_RECEIPT_TYPE_SALE)) {
			for (WooCommerceOrderItems wooCommerceDRItem : wooCommerceItems) {
				
				
				DRItem item = new DRItem(wooCommerceDRItem.getName(),Constants.STRING_NILL, Constants.STRING_NILL, 
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, wooCommerceDRItem.getName(), 
						Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL, 
						Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL, 
						Constants.STRING_NILL,Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
						wooCommerceDRItem.getTotal_tax(), wooCommerceDRItem.getQuantity(), wooCommerceDRItem.getPrice(), Constants.STRING_NILL, 
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
						wooCommerceDRItem.getPrice(), wooCommerceDRItem.getPrice(), wooCommerceDRItem.getTotal_tax(), Constants.STRING_NILL,
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
				
				items.add(item);
				distinctskus.put(wooCommerceDRItem.getSku(),item);
				logger.info("distinctskus "+distinctskus.size());
			}
			}else if(wooCommerceRequest.getHead().getReceiptType()!=null && wooCommerceRequest.getHead().getReceiptType().equalsIgnoreCase(OCConstants.DR_RECEIPT_TYPE_RETURN)) {
				String returnAmount="";
				
				for (WooCommerceOrderItems wooCommerceDRItem : wooCommerceItems) {
					
					//itemDiscAmt = (Double.valueOf(wooCommerceDRItem.getDiscount_amount())/(Double.valueOf(wooCommerceDRItem.getQty_ordered())));
					DRItem item = new DRItem(wooCommerceDRItem.getName(),Constants.STRING_NILL, Constants.STRING_NILL, 
							Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
							Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, wooCommerceDRItem.getName(), 
							Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL, 
							Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL, 
							Constants.STRING_NILL,Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
							wooCommerceDRItem.getTotal_tax(), Constants.STRING_NILL, wooCommerceDRItem.getSubtotal(), Constants.STRING_NILL, 
							Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
							String.valueOf(Double.valueOf(wooCommerceDRItem.getSubtotal())/(Double.valueOf(wooCommerceDRItem.getQuantity()))),Constants.STRING_NILL, wooCommerceDRItem.getTotal_tax(), Constants.STRING_NILL,
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
					
					if(wooCommerceReceipt.getStatus()!=null && wooCommerceReceipt.getStatus().equalsIgnoreCase("cancelled")) {
						receipt.setTotal(wooCommerceReceipt.getTotal());
						double itemQty = (Double.valueOf(wooCommerceDRItem.getQuantity()));
						item.setQty(String.valueOf((itemQty*=-1)));
					}
					items.add(item);
					logger.info("qty "+item.getQty());
					distinctskus.put(wooCommerceDRItem.getSku(),item);
					logger.info("distinctskus "+distinctskus.size());
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
			
			if(wooCommerceRequest.getHead().getReceiptType()!=null && wooCommerceRequest.getHead().getReceiptType().equalsIgnoreCase(OCConstants.DR_RECEIPT_TYPE_RETURN)) receipt.setReceiptType("2");
			
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
			
			if(ocDetails!=null && ocDetails.getLoyaltyRedeem()!=null) {
				String tenderAmount = wooCommerceRequest.getOptcultureDetails().getLoyaltyRedeem().getDiscountAmount()!=null?ocDetails.getLoyaltyRedeem().getDiscountAmount():"";
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
		public DRBody convertWooCommerceRefundRequest(WooCommerceReturnDRRequest wooCommerceRequest,Users userObj){
			
			
			DRBody Body = new DRBody();
			DRHead drHead = wooCommerceRequest.getHead();
			WooCommerceOrderDetails wooCommerceReceipt= wooCommerceRequest.getBody().getOrderdetails();
			WooCommerceCustomerDetails customerDetails= wooCommerceRequest.getBody().getCustomerdetails();
			DROptCultureDetails ocDetails = wooCommerceRequest.getOptcultureDetails();
			Receipt receipt = new Receipt(Constants.STRING_NILL, wooCommerceReceipt.getTotal_tax(), 
					Constants.STRING_NILL, Constants.STRING_NILL, wooCommerceReceipt.getTotal_discount(), Constants.STRING_NILL, 
					Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL,
					wooCommerceReceipt.getTotal_line_items_quantity(), wooCommerceReceipt.getTotal_shipping(), Constants.STRING_NILL, 
					Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL, 
					Constants.STRING_NILL, Constants.STRING_NILL,wooCommerceReceipt.getTotal(), 
					Constants.STRING_NILL, wooCommerceReceipt.getSubtotal(), wooCommerceReceipt.getTotal(), 
					Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, wooCommerceReceipt.getCreated_at(), 
					wooCommerceReceipt.getOrder_id(), Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
					wooCommerceRequest.getBody().getCustomerdetails().getFirst_name(), wooCommerceRequest.getBody().getCustomerdetails().getLast_name(), 
					Constants.STRING_NILL, Constants.STRING_NILL,Constants.STRING_NILL, 
					Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL, 
					Constants.STRING_NILL,Constants.STRING_NILL, wooCommerceRequest.getBody().getMembership(), Constants.STRING_NILL, 
					Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
					Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
					Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
					Constants.STRING_NILL, wooCommerceRequest.getBody().getCustomerdetails().getCustomer_id(), Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
					Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
					Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, wooCommerceRequest.getBody().getCustomerdetails().getBilling_address().getPhone(), 
					Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
					Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL, 
					Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, wooCommerceReceipt.getTotal_line_items_quantity(), 
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
					 Constants.STRING_NILL, Constants.STRING_NILL, wooCommerceRequest.getBody().getCustomerdetails().getBilling_address().getEmail(), Constants.STRING_NILL , Constants.STRING_NILL, wooCommerceReceipt.getIncrement_id(), 
					 Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
					 Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
					 Constants.STRING_NILL,Constants.STRING_NILL, Constants.STRING_NILL, wooCommerceReceipt.getTotal_tax(), 
					 Constants.STRING_NILL, Constants.STRING_NILL, 
					 Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,Constants.STRING_NILL, 
					 (ocDetails!=null && ocDetails.getPromotions()!=null && !ocDetails.getPromotions().isEmpty() ?  ocDetails.getPromotions().get(0).getCouponCode() : Constants.STRING_NILL), Constants.STRING_NILL,Constants.STRING_NILL, 
							 Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
							 Constants.STRING_NILL);
			
			List<DRItem> items = new ArrayList<DRItem>();
			List<WooCommerceOrderData> wooCommerceItems = wooCommerceRequest.getBody().getOrderitems().getOrderdata();
			List<WooCommerceRefundData> wooCommerceRefundItems = wooCommerceRequest.getBody().getOrderitems().getRefunddata();
			Map<String,String> refundskus = new HashMap<String,String>();
			if(wooCommerceRefundItems!=null) {
				for (WooCommerceRefundData refundItem : wooCommerceRefundItems) {
						refundskus.put(refundItem.getRefund_item_id(),refundItem.getQty());
				}
				}
			Map<String, DRItem> distinctskus = new HashMap<String, DRItem>();
			
			if(drHead!=null && drHead.getReceiptType().equalsIgnoreCase(OCConstants.DR_RECEIPT_TYPE_SALE)) {
			for (WooCommerceOrderData wooCommerceDRItem : wooCommerceItems) {
				
				
				DRItem item = new DRItem(wooCommerceDRItem.getName(),Constants.STRING_NILL, Constants.STRING_NILL, 
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, wooCommerceDRItem.getName(), 
						Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL, 
						Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL, 
						Constants.STRING_NILL,Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
						wooCommerceDRItem.getTotal_tax(), wooCommerceDRItem.getQuantity(), wooCommerceDRItem.getSubtotal(), Constants.STRING_NILL, 
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
						Constants.STRING_NILL, wooCommerceDRItem.getSubtotal(), wooCommerceDRItem.getTotal_tax(), Constants.STRING_NILL,
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
				
				items.add(item);
				distinctskus.put(wooCommerceDRItem.getSku(),item);
				logger.info("distinctskus "+distinctskus.size());
			}
			}else if(drHead.getReceiptType()!=null && drHead.getReceiptType().equalsIgnoreCase(OCConstants.DR_RECEIPT_TYPE_RETURN)) {
				
				for (WooCommerceOrderData wooCommerceDRItem : wooCommerceItems) {
					
					DRItem item = new DRItem(wooCommerceDRItem.getName(),Constants.STRING_NILL, Constants.STRING_NILL, 
							Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
							Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, wooCommerceDRItem.getName(), 
							Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL, 
							Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL, 
							Constants.STRING_NILL,Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
							wooCommerceDRItem.getTotal_tax(), Constants.STRING_NILL, wooCommerceDRItem.getSubtotal(), Constants.STRING_NILL, 
							Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
							Constants.STRING_NILL, wooCommerceDRItem.getSubtotal(), wooCommerceDRItem.getTotal_tax(), Constants.STRING_NILL,
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
							Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL,
							Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL, Constants.STRING_NILL, 
							Constants.STRING_NILL, Constants.STRING_NILL, 
							Constants.STRING_NILL, wooCommerceDRItem.getTotal_tax(), Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL);
					if(wooCommerceReceipt.getStatus()!=null && wooCommerceReceipt.getStatus().equalsIgnoreCase("cancelled")) {
						receipt.setTotal(wooCommerceReceipt.getTotal());
						double itemQty = (Double.valueOf(wooCommerceDRItem.getQuantity()));
						item.setQty(String.valueOf((itemQty*=-1)));
						items.add(item);
					}else if(refundskus!=null && refundskus.size()!=0){
						if(refundskus.get(wooCommerceDRItem.getId())!=null) {
							receipt.setTotal(wooCommerceReceipt.getRefund_details().getRefund_amount());
							item.setQty(refundskus.get(wooCommerceDRItem.getId()));
							item.setRefReceipt(wooCommerceReceipt.getOrder_id());
							item.setRefStoreCode(wooCommerceReceipt.getStore_id());
							items.add(item);
						}
					}
					distinctskus.put(wooCommerceDRItem.getSku(),item);
				}
			}
			//cc:qty:disc:1.0-2 (ratio)
			if(ocDetails!=null) {
			List<OCPromotions> promotions = ocDetails.getPromotions();
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
						rewardRatio = promotion.getRewardRatio().split(""+Constants.DELIMETER_COLON);
					}
					String itemNoteStr=promotion.getCouponCode()+""+Constants.DELIMETER_COLON+promotion.getQuantityDiscounted()+""+Constants.DELIMETER_COLON+
							promotion.getItemDiscount()+(rewardRatio!=null ?""+Constants.DELIMETER_COLON+rewardRatio[0]+Constants.DELIMETER_HIPHEN+rewardRatio[1]:"");
					DRItem drItem = distinctskus.get(promotion.getItemCode());
					logger.info("drItem "+drItem);
					if(drItem!=null) {
						pdItemNote = new PropertyDescriptor(usedItemNote, drItem.getClass());
						if(pdItemNote == null ) continue;
						Object itemNoteObj = pdItemNote.getReadMethod().invoke(drItem);
						if(itemNoteObj == null) continue;
						String itemNoteObjStr = itemNoteObj.toString().isEmpty()?itemNoteStr:itemNoteObj.toString()+""+Constants.DELIMITER_PIPE;
						Method setterMethod = drItem.getClass().getDeclaredMethod(pdItemNote.getWriteMethod().getName(), pdItemNote.getPropertyType());
		                setterMethod.invoke(drItem,itemNoteObjStr.toString());
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
			}	
			}catch(Exception e) {
				logger.error("Exception ",e);
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
			
			if(ocDetails!=null && ocDetails.getLoyaltyRedeem()!=null) {
				String tenderAmount = ocDetails.getLoyaltyRedeem().getDiscountAmount()!=null?ocDetails.getLoyaltyRedeem().getDiscountAmount():"";
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
		
	}
