package org.mq.optculture.utils;

	import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.text.DateFormat;
	import java.text.ParseException;
	import java.text.SimpleDateFormat;
	import java.time.OffsetDateTime;
	import java.time.format.DateTimeFormatter;
	import java.util.ArrayList;
	import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.mq.captiway.scheduler.utility.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.Users;
import org.mq.optculture.model.DR.DRBody;
	import org.mq.optculture.model.DR.DRHead;
	import org.mq.optculture.model.DR.DRItem;
import org.mq.optculture.model.DR.DROptCultureDetails;
import org.mq.optculture.model.DR.DigitalReceipt;
import org.mq.optculture.model.DR.OCPromotions;
import org.mq.optculture.model.DR.Receipt;
	import org.mq.optculture.model.DR.User;
import org.mq.optculture.model.DR.magento.MagentoBasedDRRequest;
import org.mq.optculture.model.DR.magento.MagentoCustomerDetails;
import org.mq.optculture.model.DR.magento.MagentoDRBody;
import org.mq.optculture.model.DR.magento.MagentoDRHead;
import org.mq.optculture.model.DR.magento.MagentoDRUser;
import org.mq.optculture.model.DR.magento.MagentoOrderDetails;
import org.mq.optculture.model.DR.magento.MagentoOrderItems;
import org.mq.optculture.model.DR.prism.PrismBasedDRRequest;
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

	public class MagentoRequestTranslator {
		MagentoBasedDRRequest magentoRequest;
		private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
		public MagentoRequestTranslator(){
			
		}
		
		public DRBody convertMagentoRequest(MagentoBasedDRRequest magentoRequest,Users userObj){
			MagentoDRBody magentoBody = magentoRequest.getBody();
			MagentoOrderDetails magentoReceipt = magentoBody.getOrderdetails(); 
			MagentoCustomerDetails magentoCustomerDetails = magentoBody.getCustomerdetails();
			DROptCultureDetails magentoOCDetails = magentoRequest.getOptcultureDetails();
			User magentoUser = magentoRequest.getHead().getUser();
			DRHead drHead = magentoRequest.getHead() ;
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
			
			//digitalReceipt.setHead(Head);
			List<CreditCard> creditCards = new ArrayList<CreditCard>();
			COD cod = null;
			
			DRBody Body = new DRBody();
			boolean isGuestUser = false;
			if(magentoOCDetails.getMembershipNumber()==null)isGuestUser=true;
			String membership = magentoOCDetails.getMembershipNumber()!=null?magentoOCDetails.getMembershipNumber():Constants.STRING_NILL;
			Receipt receipt = new Receipt(Constants.STRING_NILL, magentoReceipt.getBase_tax_amount(), 
					Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
					Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL,
					magentoReceipt.getTotal_qty_ordered(), magentoReceipt.getShipping_amount(), Constants.STRING_NILL, 
					Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL, 
					Constants.STRING_NILL, Constants.STRING_NILL, magentoReceipt.getGrand_total(), 
					Constants.STRING_NILL, magentoReceipt.getSubtotal(), magentoReceipt.getBase_grand_total(), 
					Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, magentoReceipt.getCreated_at(), 
					magentoReceipt.getEntity_id(), Constants.STRING_NILL, Constants.STRING_NILL, magentoCustomerDetails.getCompany(),
					magentoCustomerDetails.getFirstname(), magentoCustomerDetails.getLastname(), 
					Constants.STRING_NILL, Constants.STRING_NILL,Constants.STRING_NILL, 
					Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL, 
					Constants.STRING_NILL,magentoCustomerDetails.getPostcode(), membership, Constants.STRING_NILL, 
					Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
					Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
					Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
					Constants.STRING_NILL, (magentoReceipt.getCustomer_id()!=null?magentoReceipt.getCustomer_id():Constants.STRING_NILL), Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
					Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
					Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, magentoCustomerDetails.getTelephone(), 
					Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
					Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL, 
					magentoReceipt.getStore_id(), Constants.STRING_NILL, Constants.STRING_NILL, magentoReceipt.getTotal_qty_ordered(), 
					Constants.STRING_NILL, Constants.STRING_NILL,Constants.STRING_NILL, 
					Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
					Constants.STRING_NILL, magentoReceipt.getBase_tax_amount(),  Constants.STRING_NILL,  
					Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL, 
					 Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL, 
					 Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL, 
					 Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL, 
					 Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL, Constants.STRING_NILL,Constants.STRING_NILL, 
					 Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL, 
					 Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL, 
					 Constants.STRING_NILL, Constants.STRING_NILL, magentoCustomerDetails.getEmail(), magentoCustomerDetails.getEmail() , Constants.STRING_NILL, magentoReceipt.getIncrement_id(), 
					 Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
					 Constants.STRING_NILL, Constants.STRING_NILL, magentoReceipt.getStore_name(), Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
					 Constants.STRING_NILL,Constants.STRING_NILL, Constants.STRING_NILL, magentoReceipt.getTax_amount(), 
					 Constants.STRING_NILL, Constants.STRING_NILL, 
					 Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,Constants.STRING_NILL, 
					 (magentoOCDetails!=null && magentoOCDetails.getPromotions()!=null && !magentoOCDetails.getPromotions().isEmpty() ?  magentoOCDetails.getPromotions().get(0).getCouponCode() : Constants.STRING_NILL), Constants.STRING_NILL,Constants.STRING_NILL, 
							 Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
							 Constants.STRING_NILL);
			
			List<DRItem> items = new ArrayList<DRItem>();
			List<MagentoOrderItems> magentoItems = magentoBody.getOrderitems();
			Map<String, DRItem> distinctskus = new HashMap<String, DRItem>();
			Map<String, MagentoOrderItems> refundskus = new HashMap<String, MagentoOrderItems>();
			
			if(drHead.getReceiptType()!=null && drHead.getReceiptType().equalsIgnoreCase("Sale")) {
		         double itemDiscAmt = 0; //To send disc amt per qty (later multiplied with qty in Extraction)
		         double itemPrice = 0;
		         String configurableItemId="";
			for (MagentoOrderItems magentoDRItem : magentoItems) {
				//if(magentoDRItem.getParent_item_id()==null) {
				/*if(magentoDRItem.getProduct_type()!=null && !magentoDRItem.getProduct_type().equalsIgnoreCase("bundle")
						&& (magentoDRItem.getProduct_type().equalsIgnoreCase("simple")?magentoDRItem.getParent_item_id()==null:true)) {*/
				if(magentoDRItem.getProduct_type()!=null && !magentoDRItem.getProduct_type().equalsIgnoreCase("bundle")) {
					if(magentoDRItem.getProduct_type().equalsIgnoreCase("configurable")){
						configurableItemId=magentoDRItem.getItem_id();
					}
					if(magentoDRItem.getProduct_type().equalsIgnoreCase("simple") && 
							!configurableItemId.isEmpty() && magentoDRItem.getParent_item_id()!=null 
							&& magentoDRItem.getParent_item_id().equals(configurableItemId))continue;
						
				
				DRItem item = new DRItem(Constants.STRING_NILL,Constants.STRING_NILL, Constants.STRING_NILL, 
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, magentoDRItem.getDescription(), 
						Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL, 
						Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL, 
						Constants.STRING_NILL,Constants.STRING_NILL, Constants.STRING_NILL, magentoDRItem.getTax_percent(), 
						magentoDRItem.getTax_amount(), magentoDRItem.getQty_ordered(), magentoDRItem.getPrice(), magentoDRItem.getDiscount_percent(), 
						Constants.STRING_NILL, magentoDRItem.getRow_total(), Constants.STRING_NILL, 
						Constants.STRING_NILL, magentoDRItem.getPrice(), magentoDRItem.getTax_amount(), Constants.STRING_NILL,
						magentoDRItem.getName(),Constants.STRING_NILL,
						magentoDRItem.getSku(), String.valueOf(itemDiscAmt), Constants.STRING_NILL, Constants.STRING_NILL,
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
						Constants.STRING_NILL,Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
						Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL,
						Constants.STRING_NILL, Constants.STRING_NILL, 
						Constants.STRING_NILL, magentoDRItem.getTax_amount(), Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL);
				items.add(item);
				distinctskus.put(magentoDRItem.getSku(),item);
				}
			//}
			}
			if(magentoOCDetails!=null && magentoOCDetails.getLoyaltyRedeem()!=null) {
				String tenderAmount = magentoOCDetails.getLoyaltyRedeem().getDiscountAmount()!=null?magentoOCDetails.getLoyaltyRedeem().getDiscountAmount():"";
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
			if(magentoOCDetails!=null && magentoOCDetails.getPromotions()!=null) {
			List<OCPromotions> promotions = magentoOCDetails.getPromotions();
			String usedItemNote = userObj.getItemNoteUsed();
			String usedReceiptNote = userObj.getReceiptNoteUsed();
			//String usedItemInfo = userObj.getItemInfo();
			PropertyDescriptor pdItemNote  = null;
			PropertyDescriptor pdReceiptNote = null;
			if(promotions!=null) {
			try {
			for(OCPromotions promotion : promotions) {
				if(promotion.getDiscountType().equalsIgnoreCase("Item")) {
					logger.info("item level promo");
					String[] rewardRatio = null;
					if(promotion.getRewardRatio()!=null && !promotion.getRewardRatio().isEmpty()) {
						rewardRatio = promotion.getRewardRatio().split(""+Constants.DELIMETER_HIPHEN);
					}
					/*String itemNoteStr=promotion.getCouponCode()+""+Constants.DELIMETER_COLON+promotion.getQuantity()+""+Constants.DELIMETER_COLON+
							promotion.getItemDiscount()+(rewardRatio!=null ?""+Constants.DELIMETER_COLON+promotion.getRewardRatio():"");*/
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
		                if(drItem.getInvcItemPrc()!=null && !drItem.getInvcItemPrc().isEmpty()) itemPrice = Double.valueOf(drItem.getInvcItemPrc());
		                itemPrice = (itemPrice*Double.valueOf(drItem.getQty()))-(Double.valueOf(promotion.getItemDiscount())*Double.valueOf(promotion.getQuantityDiscounted()));
		                itemPrice = itemPrice/Double.valueOf(drItem.getQty());
		                //itemPrice-=Double.valueOf(promotion.getItemDiscount());
		                drItem.setInvcItemPrc(String.valueOf(itemPrice));
		                //if(drItem.getDocItemDiscAmt()!=null && !drItem.getDocItemDiscAmt().isEmpty()) itemDiscAmt = Double.valueOf(drItem.getDocItemDiscAmt());
		                //itemDiscAmt+=Double.valueOf(promotion.getItemDiscount());
		                //drItem.setDocItemDiscAmt(String.valueOf(itemDiscAmt));
						logger.info("itemNoteObjStr :"+itemNoteObjStr.toString());
					}
				}else if(promotion.getDiscountType().equalsIgnoreCase("Receipt")) {
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
			logger.info("itemDiscAmt :"+itemDiscAmt);
			}catch(Exception e) {
				logger.error("Exception ",e);
			}
			}
			}
			}else if(drHead.getReceiptType()!=null && drHead.getReceiptType().equalsIgnoreCase("Return")) {
				
				String returnAmount ="";
				
				for (MagentoOrderItems magentoDRItem : magentoItems) {
					
					DRItem item = new DRItem(Constants.STRING_NILL,Constants.STRING_NILL, Constants.STRING_NILL, 
							Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
							Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, magentoDRItem.getDescription(), 
							Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL, 
							Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL, 
							Constants.STRING_NILL,Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
							magentoDRItem.getTax_amount(), Constants.STRING_NILL, magentoDRItem.getPrice(), magentoDRItem.getDiscount_percent(), 
							Constants.STRING_NILL, magentoDRItem.getRow_total(), Constants.STRING_NILL, 
							magentoDRItem.getBase_price(), Constants.STRING_NILL, magentoDRItem.getTax_amount(), Constants.STRING_NILL,
							magentoDRItem.getName(),Constants.STRING_NILL,
							magentoDRItem.getSku(), magentoDRItem.getDiscount_amount(), Constants.STRING_NILL, Constants.STRING_NILL,
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
							Constants.STRING_NILL,Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
							Constants.STRING_NILL, Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL,
							Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL,
							Constants.STRING_NILL, magentoDRItem.getTax_amount(), Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL);
					if(magentoDRItem.getQty_canceled()!=null && !magentoDRItem.getQty_canceled().isEmpty()
							&& (Double.valueOf(magentoDRItem.getQty_canceled())!=0)) {
						receipt.setTotal(magentoReceipt.getGrand_total());
						//returnAmount=magentoReceipt.getGrand_total();
						double itemQty=(Double.valueOf(magentoDRItem.getQty_canceled()));
						item.setQty(String.valueOf((itemQty*=-1)));
						//item.setQty(magentoDRItem.getQty_canceled());
						receipt.setRefDocSID(magentoReceipt.getIncrement_id());
						receipt.setBillToInfo1(membership);
						receipt.setDocSID(magentoReceipt.getEntity_id());
						//receipt.setBillToInfo2(membership);
						double billedPrice = magentoDRItem.getDiscount_amount()!=null?
								Double.valueOf(magentoDRItem.getPrice())-(Double.valueOf(magentoDRItem.getDiscount_amount())/Double.valueOf(magentoDRItem.getQty_canceled())):Double.valueOf(magentoDRItem.getPrice());
						item.setDocItemPrc(String.valueOf(billedPrice));
						items.add(item);
						receipt.setRefDocSID(magentoReceipt.getIncrement_id());
					}else if(magentoDRItem.getQty_canceled()==null) {
						//this should be here, because cancel receipt wont have qty
						for (MagentoOrderItems refundItem : magentoItems) {
							if(Double.parseDouble(refundItem.getQty())!=0 && !refundskus.containsKey(refundItem.getSku())){
								refundskus.put(refundItem.getSku(),refundItem);
							}
						}
						if(magentoDRItem.getRow_total()!=null && refundskus.containsKey(magentoDRItem.getSku())) {
							if(items!=null && !items.isEmpty()) {
								boolean itemAdded = false;
								DRItem existingItem = null;
								Iterator<DRItem> iterItems = items.iterator();
								while (iterItems.hasNext()) {
									existingItem = iterItems.next();
									if(magentoDRItem.getSku().equalsIgnoreCase(existingItem.getItemSID())) itemAdded = true;
								}
								if(!itemAdded) {
									returnAmount=Double.parseDouble(magentoReceipt.getAdjustment())!=0?
											String.valueOf((Double.parseDouble(magentoReceipt.getGrand_total())-Double.parseDouble(magentoReceipt.getAdjustment())))
											:magentoReceipt.getGrand_total();
									//item.setRefDocSID(magentoReceipt.getIncrement_id());
											item.setRefReceipt(magentoReceipt.getOrder_id());
											item.setRefStoreCode(magentoReceipt.getStore_id());
											if(isGuestUser) {
												item.setRefReceipt(magentoReceipt.getOrder_id());
												item.setRefStoreCode(magentoReceipt.getStore_id());
												receipt.setBillToInfo1(membership);
												//receipt.setBillToInfo2(membership);
												}
									receipt.setTotal(returnAmount);
									double itemQty=(Double.valueOf(magentoDRItem.getQty()));
									item.setQty(String.valueOf((itemQty*=-1)));
									//item.setQty(magentoDRItem.getQty());
									double billedPrice = magentoDRItem.getDiscount_amount()!=null?
											Double.valueOf(magentoDRItem.getPrice())-(Double.valueOf(magentoDRItem.getDiscount_amount())/Double.valueOf(magentoDRItem.getQty())):Double.valueOf(magentoDRItem.getPrice());
									item.setDocItemPrc(String.valueOf(billedPrice));
									items.add(item);
								}
							}else {
							returnAmount=Double.parseDouble(magentoReceipt.getAdjustment())!=0?
									String.valueOf((Double.parseDouble(magentoReceipt.getGrand_total())-Double.parseDouble(magentoReceipt.getAdjustment())))
									:magentoReceipt.getGrand_total();
									item.setRefReceipt(magentoReceipt.getOrder_id());
									item.setRefStoreCode(magentoReceipt.getStore_id());
									if(isGuestUser) {
										item.setRefReceipt(magentoReceipt.getOrder_id());
										item.setRefStoreCode(magentoReceipt.getStore_id());
										receipt.setBillToInfo1(membership);
										//receipt.setBillToInfo2(membership);
										}
							//item.setRefDocSID(magentoReceipt.getIncrement_id());
							receipt.setTotal(returnAmount);
							double itemQty=(Double.valueOf(magentoDRItem.getQty()));
							item.setQty(String.valueOf((itemQty*=-1)));
							//item.setQty(magentoDRItem.getQty());
							double billedPrice = magentoDRItem.getDiscount_amount()!=null?
									Double.valueOf(magentoDRItem.getPrice())-(Double.valueOf(magentoDRItem.getDiscount_amount())/Double.valueOf(magentoDRItem.getQty())):Double.valueOf(magentoDRItem.getPrice());
							item.setDocItemPrc(String.valueOf(billedPrice));
							items.add(item);
						  }
						}
					}
					distinctskus.put(magentoDRItem.getSku(),item);
				}
				if(magentoOCDetails!=null && magentoOCDetails.getLoyaltyRedeemReversal()!=null) {
					returnAmount = magentoOCDetails.getLoyaltyRedeemReversal();
					String userRedeemTender = userObj.getRedeemTender();
					if(userRedeemTender!=null && !userRedeemTender.isEmpty()) {
						if(userRedeemTender.contains(""+Constants.DELIMETER_COLON)) {
							String tenderType=userObj.getRedeemTender().split(""+Constants.DELIMETER_COLON)[1];
							CreditCard CC = new CreditCard(Constants.STRING_NILL, Constants.STRING_NILL, 
									Constants.STRING_NILL,Constants.STRING_NILL,
									tenderType, Constants.STRING_NILL, 
									Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
									Constants.STRING_NILL,Constants.STRING_NILL,returnAmount, 
									Constants.STRING_NILL);
									creditCards.add(CC);
					}else {
						cod = new COD(Constants.STRING_NILL,Constants.STRING_NILL,returnAmount,Constants.STRING_NILL);
					}
						
					}
					}
			}
			
			if(magentoReceipt.getCreated_at() != null && !magentoReceipt.getCreated_at().isEmpty()) {
	    		
				try {
					
					String fieldValue = (magentoReceipt.getCreated_at()!=null && !magentoReceipt.getCreated_at().isEmpty()?magentoReceipt.getCreated_at():
						drHead.getRequestDate());
					DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Date date = (Date)formatter.parse(fieldValue); 
					
					formatter = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss");
					String docDate = formatter.format(date);
					receipt.setDocDate(docDate);
					
				} catch (ParseException e) {
						logger.error("Exception ",e);
					}
	    		
	    	}
			
			if(drHead.getReceiptType()!=null && drHead.getReceiptType().equalsIgnoreCase("Return")) receipt.setReceiptType("2");
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
			
			
			return Body;
			
		}
		
	/*public DRBody convertMagentoRequest(MagentoDRBody magentoBody,MagentoOptcultureDetails ocDetails,Users userObj){
			
			
			DRBody Body = new DRBody();
			MagentoOrderDetails body= magentoBody.getOrderdetails();
			//commented fields which I am not sure of
			
			Receipt receipt = new Receipt(prismBody.getNotes_order(), body.getBase_tax_amount(), 
					Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, prismBody.getComment1(), 
					prismBody.getComment2(), prismBody.getTax_area_name(), prismBody.getTax_rebate_percent(), prismBody.getTax_rebate_amt(),
					body.getTotal_qty_ordered(), body.getBase_shipping_amount(), prismBody.getOrder_fee_amt1(), 
					prismBody.getOrder_shipping_percentage(), prismBody.getFee_type1(), prismBody.getFee_tax_perc1(), 
					Constants.STRING_NILL, prismBody.getRounded_due_amt(), body.getBase_grand_total(), 
					Constants.STRING_NILL, body.getSubtotal(), body.getGrand_total(), 
					Constants.STRING_NILL, Constants.STRING_NILL, prismBody.getCashier_name(), body.getCreated_at(), 
					prismBody.getDocument_number(), Constants.STRING_NILL, Constants.STRING_NILL, magentoBody.getCustomerdetails().getCompany(),
					magentoBody.getCustomerdetails().getFirstname(),magentoBody.getCustomerdetails().getLastname(), 
					Constants.STRING_NILL, prismBody.getBt_address_line1(), prismBody.getBt_address_line2(), 
					prismBody.getBt_address_line3(), prismBody.getBt_address_line4(), prismBody.getBt_address_line5(), 
					prismBody.getBt_address_line6(), magentoBody.getCustomerdetails().getPostcode(),magentoBody.getMembership(),magentoBody.getMembership(), 
					prismBody.getSerial_number(), Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
					Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
					Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
					Constants.STRING_NILL, body.getCustomer_id(), Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
					prismBody.getWorkstation_number(), Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
					Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, magentoBody.getCustomerdetails().getTelephone(), 
					Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, prismBody.getStore_address_line1(), 
					prismBody.getStore_address_line2(), prismBody.getStore_address_line3(), prismBody.getDoc_tender_type(), 
					prismBody.getStore_number(), prismBody.getTracking_number(), Constants.STRING_NILL,body.getTotal_qty_ordered(), 
					Constants.STRING_NILL, prismBody.getStore_address_line4(), prismBody.getStore_address_line5(), 
					Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
					Constants.STRING_NILL, body.getBase_tax_amount(),  Constants.STRING_NILL,  
					Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL, 
					 Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL, 
					 Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL, 
					 Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL, 
					 Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL, prismBody.getBt_udf8(),  prismBody.getBt_udf7(), 
					 prismBody.getBt_udf6(),  prismBody.getBt_udf5(),  prismBody.getBt_udf4(),  prismBody.getBt_udf3(),  prismBody.getBt_udf2(), Constants.STRING_NILL, 
					 Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL, 
					 prismBody.getBt_udf1(), Constants.STRING_NILL, magentoBody.getCustomerdetails().getEmail(), prismBody.getSt_email() , Constants.STRING_NILL, body.getIncrement_id(), 
					 Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
					 Constants.STRING_NILL, Constants.STRING_NILL, body.getStore_name(), prismBody.getStore_phone1(), Constants.STRING_NILL, Constants.STRING_NILL, 
					 prismBody.getSubsidiary_number(), prismBody.getStore_code(), Constants.STRING_NILL, body.getTax_amount(), 
					 prismBody.getEmployee1_full_name(), Constants.STRING_NILL, 
					 Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, prismBody.getNotes_general(), prismBody.getStore_phone2(), 
					 (!ocDetails.getPromotions().isEmpty() ?  ocDetails.getPromotions().get(0).getCouponCode() : Constants.STRING_NILL), prismBody.getComment1(), prismBody.getComment2(), 
							 Constants.STRING_NILL, prismBody.getFiscal_document_number(), Constants.STRING_NILL, prismBody.getBt_employee_id(),
							 prismBody.getBt_primary_phone_no());
			
			Receipt receipt = new Receipt(Constants.STRING_NILL, body.getBase_tax_amount(), 
					Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
					Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL,
					body.getTotal_qty_ordered(), body.getBase_shipping_amount(), Constants.STRING_NILL, 
					Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL, 
					Constants.STRING_NILL, Constants.STRING_NILL, body.getBase_grand_total(), 
					Constants.STRING_NILL, body.getSubtotal(), body.getGrand_total(), 
					Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, body.getCreated_at(), 
					Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, magentoBody.getCustomerdetails().getCompany(),
					magentoBody.getCustomerdetails().getFirstname(),magentoBody.getCustomerdetails().getLastname(), 
					Constants.STRING_NILL, Constants.STRING_NILL,Constants.STRING_NILL, 
					Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL, 
					Constants.STRING_NILL, magentoBody.getCustomerdetails().getPostcode(),magentoBody.getMembership(),magentoBody.getMembership(), 
					Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
					Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
					Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
					Constants.STRING_NILL, body.getCustomer_id(), Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
					Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
					Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, magentoBody.getCustomerdetails().getTelephone(), 
					Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,Constants.STRING_NILL, 
					Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL, 
					Constants.STRING_NILL,Constants.STRING_NILL, Constants.STRING_NILL,body.getTotal_qty_ordered(), 
					Constants.STRING_NILL, Constants.STRING_NILL,Constants.STRING_NILL, 
					Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
					Constants.STRING_NILL, body.getBase_tax_amount(),  Constants.STRING_NILL,  
					Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL, 
					 Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL, 
					 Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL, 
					 Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL, 
					 Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL, Constants.STRING_NILL,Constants.STRING_NILL, 
					 Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL, 
					 Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL,  Constants.STRING_NILL, 
					 Constants.STRING_NILL, Constants.STRING_NILL, magentoBody.getCustomerdetails().getEmail(), Constants.STRING_NILL, Constants.STRING_NILL, body.getIncrement_id(), 
					 Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
					 Constants.STRING_NILL, Constants.STRING_NILL, body.getStore_name(), Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
					 Constants.STRING_NILL,Constants.STRING_NILL, Constants.STRING_NILL, body.getTax_amount(), 
					 Constants.STRING_NILL, Constants.STRING_NILL, 
					 Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,Constants.STRING_NILL, 
					 (!ocDetails.getPromotions().isEmpty() ?  ocDetails.getPromotions().get(0).getCouponCode() : Constants.STRING_NILL), Constants.STRING_NILL,Constants.STRING_NILL, 
							 Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
							 Constants.STRING_NILL);
			
			List<DRItem> items = new ArrayList<DRItem>();
			
			List<MagentoOrderItems> magentoItems = magentoBody.getOrderitems();
			for (MagentoOrderItems magentoDRItem : magentoItems) {
				
				// NILL fields that I am not sure of.
				DRItem item = new DRItem(prismDRItem.getDcs_code(), prismDRItem.getVendor_code(), Constants.STRING_NILL, 
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, magentoDRItem.getDescription(), 
						prismDRItem.getItem_description2(), prismDRItem.getAttribute(), prismDRItem.getItem_size(), 
						prismDRItem.getScan_upc(), prismDRItem.getAlu(), prismDRItem.getUdf_string01(), prismDRItem.getUdf_string02(), 
						prismDRItem.getUdf_string03(), prismDRItem.getUdf_string04(), Constants.STRING_NILL, magentoDRItem.getTax_percent(), 
						magentoDRItem.getTax_amount(), magentoDRItem.getQty_ordered(), magentoDRItem.getPrice(), magentoDRItem.getDiscount_percent(), 
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
						magentoDRItem.getOriginal_price(), magentoDRItem.getPrice(), magentoDRItem.getTax_amount(), Constants.STRING_NILL,
						prismDRItem.getItem_lookup(), prismDRItem.getPrice_lvl(),
						magentoDRItem.getSku(), magentoDRItem.getDiscount_amount(), Constants.STRING_NILL, Constants.STRING_NILL,
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
						prismDRItem.getNote2(), prismDRItem.getNote3(), Constants.STRING_NILL, prismDRItem.getNetPrice(), 
						prismDRItem.getReturn_reason(), Constants.STRING_NILL, 
						prismDRItem.getDiscount_reason(), magentoDRItem.getTax_amount(), Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, prismDRItem.getRef_order_doc_sid());

				DRItem item = new DRItem(Constants.STRING_NILL,Constants.STRING_NILL, Constants.STRING_NILL, 
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, magentoDRItem.getDescription(), 
						Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL, 
						Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL, 
						Constants.STRING_NILL,Constants.STRING_NILL, Constants.STRING_NILL, magentoDRItem.getTax_percent(), 
						magentoDRItem.getTax_amount(), magentoDRItem.getQty_ordered(), magentoDRItem.getPrice(), magentoDRItem.getDiscount_percent(), 
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
						magentoDRItem.getOriginal_price(), magentoDRItem.getPrice(), magentoDRItem.getTax_amount(), Constants.STRING_NILL,
						Constants.STRING_NILL,Constants.STRING_NILL,
						magentoDRItem.getSku(), magentoDRItem.getDiscount_amount(), Constants.STRING_NILL, Constants.STRING_NILL,
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
						Constants.STRING_NILL,Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
						Constants.STRING_NILL, Constants.STRING_NILL, 
						Constants.STRING_NILL, magentoDRItem.getTax_amount(), Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL);
				items.add(item);
			}
			
			
			receipt.setDocDate(body.getCreated_at());
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
			
			if(ocDetails.getLoyaltyRedeem()!=null) {
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
						
			//if(cod != null )Body.setCOD(cod);
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
*/
		
	}
