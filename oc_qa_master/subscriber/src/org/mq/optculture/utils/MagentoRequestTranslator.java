package org.mq.optculture.utils;

	import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.text.DateFormat;
	import java.text.ParseException;
	import java.text.SimpleDateFormat;
	import java.time.OffsetDateTime;
	import java.time.format.DateTimeFormatter;
	import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import com.google.gson.Gson;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.LoyaltyTransactionChild;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.general.Constants;
import org.mq.optculture.business.helper.DRToLty.DRToLoyaltyExtractionItems;
import org.mq.optculture.data.dao.LoyaltyTransactionChildDao;
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
		private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
		public MagentoRequestTranslator(){
			
		}
		
		public DigitalReceipt convertMagentoRequest(MagentoBasedDRRequest magentoRequest,Users userObj){
			DigitalReceipt digitalReceipt = new DigitalReceipt();
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
			Head.setRequestDate(drHead.getRequestDate());
			
			digitalReceipt.setHead(Head);
			
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
					Constants.STRING_NILL,magentoCustomerDetails.getPostcode(), membership,membership, 
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
			
			if(drHead.getReceiptType()!=null && drHead.getReceiptType().equalsIgnoreCase(OCConstants.DR_RECEIPT_TYPE_SALE)) {
				double itemDiscAmt = 0; //To send disc amt per qty (later multiplied with qty in Extraction)
				double itemPrice= 0;
				String configurableItemId="";
			for (MagentoOrderItems magentoDRItem : magentoItems) {
				//below if condition is to ignore the internal items of the bundle product items array 
				if(magentoDRItem.getProduct_type()!=null && !magentoDRItem.getProduct_type().equalsIgnoreCase("bundle")){
				if(magentoDRItem.getProduct_type().equalsIgnoreCase("configurable")){
					configurableItemId=magentoDRItem.getItem_id();
				}
				if(magentoDRItem.getProduct_type().equalsIgnoreCase("simple") && 
						!configurableItemId.isEmpty() && magentoDRItem.getParent_item_id()!=null 
						&& magentoDRItem.getParent_item_id().equals(configurableItemId))continue;
					//magento store discount applied on item. 
				itemDiscAmt = (Double.valueOf(magentoDRItem.getDiscount_amount())/(Double.valueOf(magentoDRItem.getQty_ordered())));
				
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
						Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL,
						Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
						Constants.STRING_NILL, Constants.STRING_NILL, 
						Constants.STRING_NILL, magentoDRItem.getTax_amount(), Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL);
				
				items.add(item);
				distinctskus.put(magentoDRItem.getSku(),item);
				logger.info("distinctskus "+distinctskus.size());
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
				if(promotion.getDiscountType().equalsIgnoreCase(OCConstants.DR_PROMO_DISCOUNT_TYPE_ITEM)) {
					logger.info("item level promo");
					String[] rewardRatio = null;
					if(promotion.getRewardRatio()!=null && !promotion.getRewardRatio().isEmpty()) {
						rewardRatio = promotion.getRewardRatio().split(""+Constants.DELIMETER_HIPHEN);
					}
					/*String itemNoteStr=promotion.getCouponCode()+""+Constants.DELIMETER_COLON+promotion.getQuantity()+""+Constants.DELIMETER_COLON+
							promotion.getItemDiscount()+(rewardRatio!=null ?""+Constants.DELIMETER_COLON+promotion.getRewardRatio():"");*/
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
			logger.info("itemDiscAmt :"+itemDiscAmt);
			}catch(Exception e) {
				logger.error("Exception ",e);
			}
			}
			}
			}else if(drHead.getReceiptType()!=null && drHead.getReceiptType().equalsIgnoreCase(OCConstants.DR_RECEIPT_TYPE_RETURN)) {
				
				String returnAmount="";
				// determine contacts loyalty for guest user
				if(isGuestUser) {
					try {
					LoyaltyTransactionChildDao loyaltyTransactionChildDao = (LoyaltyTransactionChildDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
					List<LoyaltyTransactionChild> origTransList = loyaltyTransactionChildDao.findByDocSID(magentoReceipt.getIncrement_id(), 
							userObj.getUserId(), OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE,magentoReceipt.getOrder_id(),magentoReceipt.getStore_id(),null);
					LoyaltyTransactionChild ltyChild = origTransList.get(0);
					membership = ltyChild!=null?ltyChild.getMembershipNumber():Constants.STRING_NILL;
					logger.info("Membership in case of guest user return"+membership);
					}catch(Exception e) {
						logger.error("Exception ",e);
					}
				}
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
							Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL,
							Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL, Constants.STRING_NILL, 
							Constants.STRING_NILL, Constants.STRING_NILL, 
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
					}else if(magentoDRItem.getQty_canceled()==null) {
						for (MagentoOrderItems refundItem : magentoItems) {
							if(Double.parseDouble(refundItem.getQty())!=0 && !refundskus.containsKey(refundItem.getSku())){
								logger.info(""+refundskus.containsKey(refundItem.getSku()));
								refundskus.put(refundItem.getSku(),refundItem);
							}
						}
						if(magentoDRItem.getRow_total()!=null && refundskus.containsKey(magentoDRItem.getSku())) {
							if(items!=null && !items.isEmpty()) {
								//Refund
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
								//cancel - happens on same docsid so setting ref receipt details
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
					//items.add(item);
					distinctskus.put(magentoDRItem.getSku(),item);
				}
				
				if(magentoOCDetails!=null && magentoOCDetails.getLoyaltyRedeemReversal()!=null && !magentoOCDetails.getLoyaltyRedeemReversal().isEmpty()) {
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
			if(magentoOCDetails!=null) {
			Gson gson = new Gson();
			gson = new Gson();
			String json = gson.toJson(magentoOCDetails);
			DROptCultureDetails = gson.fromJson(json,DROptCultureDetails.class);
			}
			digitalReceipt.setOptcultureDetails(DROptCultureDetails);
			return digitalReceipt;
			
		}
		
	public DRBody convertMagentoRequest(DRHead magentoHead,MagentoDRBody magentoBody,DROptCultureDetails ocDetails,Users userObj){
			
			logger.info("entered convert");
			DRBody Body = new DRBody();
			MagentoOrderDetails body= magentoBody.getOrderdetails();
			List<CreditCard> creditCards = new ArrayList<CreditCard>();
			COD cod = null;
			
			boolean isGuestUser = false;
			if(ocDetails.getMembershipNumber()==null)isGuestUser=true;
			String membership = ocDetails.getMembershipNumber()!=null?ocDetails.getMembershipNumber():Constants.STRING_NILL;
			
			Receipt receipt = new Receipt(Constants.STRING_NILL, body.getBase_tax_amount(), 
					Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
					Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL,
					body.getTotal_qty_ordered(), body.getBase_shipping_amount(), Constants.STRING_NILL, 
					Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL, 
					Constants.STRING_NILL, Constants.STRING_NILL, body.getBase_grand_total(), 
					Constants.STRING_NILL, body.getSubtotal(), body.getGrand_total(), 
					Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, body.getCreated_at(), 
					body.getEntity_id(), Constants.STRING_NILL, Constants.STRING_NILL, magentoBody.getCustomerdetails().getCompany(),
					magentoBody.getCustomerdetails().getFirstname(),magentoBody.getCustomerdetails().getLastname(), 
					Constants.STRING_NILL, Constants.STRING_NILL,Constants.STRING_NILL, 
					Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL, 
					Constants.STRING_NILL, magentoBody.getCustomerdetails().getPostcode(),membership,membership, 
					Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
					Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
					Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
					Constants.STRING_NILL, (body.getCustomer_id()!=null?body.getCustomer_id():Constants.STRING_NILL), Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
					Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
					Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, magentoBody.getCustomerdetails().getTelephone(), 
					Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,Constants.STRING_NILL, 
					Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL, 
					body.getStore_id(),Constants.STRING_NILL, Constants.STRING_NILL,body.getTotal_qty_ordered(), 
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
					 (ocDetails!=null && ocDetails.getPromotions()!=null && !ocDetails.getPromotions().isEmpty() ?  ocDetails.getPromotions().get(0).getCouponCode() : Constants.STRING_NILL), Constants.STRING_NILL,Constants.STRING_NILL, 
							 Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
							 Constants.STRING_NILL);
			
			List<DRItem> items = new ArrayList<DRItem>();
			
			List<MagentoOrderItems> magentoItems = magentoBody.getOrderitems();
			Map<String, DRItem> distinctskus = new HashMap<String, DRItem>();
			Map<String, MagentoOrderItems> refundskus = new HashMap<String, MagentoOrderItems>();
			
			if(magentoHead.getReceiptType()!=null && magentoHead.getReceiptType().equalsIgnoreCase(OCConstants.DR_RECEIPT_TYPE_SALE)) {
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
						
				itemDiscAmt = (Double.valueOf(magentoDRItem.getDiscount_amount())/(Double.valueOf(magentoDRItem.getQty_ordered())));
				
				DRItem item = new DRItem(magentoDRItem.getName(),Constants.STRING_NILL, Constants.STRING_NILL, 
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, magentoDRItem.getDescription(), 
						Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL, 
						Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL, 
						Constants.STRING_NILL,Constants.STRING_NILL, Constants.STRING_NILL, magentoDRItem.getTax_percent(), 
						magentoDRItem.getTax_amount(), magentoDRItem.getQty_ordered(), magentoDRItem.getPrice(), magentoDRItem.getDiscount_percent(), 
						Constants.STRING_NILL, magentoDRItem.getRow_total(), Constants.STRING_NILL, 
						Constants.STRING_NILL, magentoDRItem.getPrice(), magentoDRItem.getTax_amount(), Constants.STRING_NILL,
						Constants.STRING_NILL,Constants.STRING_NILL,
						magentoDRItem.getSku(), String.valueOf(itemDiscAmt), Constants.STRING_NILL, Constants.STRING_NILL,
						Constants.STRING_NILL,Constants.STRING_NILL, Constants.STRING_NILL,
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
						magentoDRItem.getName(), Constants.STRING_NILL, 
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,Constants.STRING_NILL, 
						Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL,
						Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL, Constants.STRING_NILL, 
						Constants.STRING_NILL, Constants.STRING_NILL, 
						Constants.STRING_NILL, magentoDRItem.getTax_amount(), Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL);
				items.add(item);
				distinctskus.put(magentoDRItem.getSku(),item);
				}
			}
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
			//cc:qty:disc:1.0-2 (ratio)
			if(ocDetails!=null && ocDetails.getPromotions()!=null) {
			List<OCPromotions> promotions = ocDetails.getPromotions();
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
						//pdItemNote.setValue(itemNoteObjStr.toString(), itemNoteObj);
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
			logger.info("itemDiscAmt :"+itemDiscAmt);
			}catch(Exception e) {
				logger.error("Exception ",e);
			}
			}
			}else if(magentoHead.getReceiptType()!=null && magentoHead.getReceiptType().equalsIgnoreCase(OCConstants.DR_RECEIPT_TYPE_RETURN)) {
				
				String returnAmount="";
				// determine contacts loyalty for guest user
				if(isGuestUser) {
					try {
					LoyaltyTransactionChildDao loyaltyTransactionChildDao = (LoyaltyTransactionChildDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
					List<LoyaltyTransactionChild> origTransList = loyaltyTransactionChildDao.findByDocSID(null, 
							userObj.getUserId(), OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE,body.getOrder_id(),body.getStore_id(),null);
					LoyaltyTransactionChild ltyChild = origTransList.get(0);
					membership = ltyChild.getMembershipNumber();
					logger.info("Membership in case of guest user "+membership);
					}catch(Exception e) {
						logger.error("Exception ",e);
					}
				}
				for (MagentoOrderItems magentoDRItem : magentoItems) {
					
					DRItem item = new DRItem(Constants.STRING_NILL,Constants.STRING_NILL, Constants.STRING_NILL, 
							Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
							Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, magentoDRItem.getDescription(), 
							Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL, 
							Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL, 
							Constants.STRING_NILL,Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, 
							magentoDRItem.getTax_amount(), Constants.STRING_NILL, magentoDRItem.getPrice(), magentoDRItem.getDiscount_percent(), 
							Constants.STRING_NILL, magentoDRItem.getRow_total(), Constants.STRING_NILL, 
							magentoDRItem.getBase_price(), magentoDRItem.getPrice(), magentoDRItem.getTax_amount(), Constants.STRING_NILL,
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
							Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL,
							Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL, Constants.STRING_NILL, 
							Constants.STRING_NILL, Constants.STRING_NILL, 
							Constants.STRING_NILL, magentoDRItem.getTax_amount(), Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL);
					if(magentoDRItem.getQty_canceled()!=null && !magentoDRItem.getQty_canceled().isEmpty()
							&& (Double.valueOf(magentoDRItem.getQty_canceled())!=0)) {
						receipt.setTotal(body.getGrand_total());
						//returnAmount=body.getGrand_total();
						//item.setQty(magentoDRItem.getQty_canceled());
						double itemQty=(Double.valueOf(magentoDRItem.getQty_canceled()));
						item.setQty(String.valueOf((itemQty*=-1)));
						items.add(item);
						receipt.setRefDocSID(body.getIncrement_id());
						receipt.setBillToInfo1(membership);
						receipt.setBillToInfo2(membership);
						receipt.setDocSID(body.getEntity_id());
					}else if(magentoDRItem.getQty_canceled()==null) {
						
						for (MagentoOrderItems refundItem : magentoItems) {
							if(Double.parseDouble(refundItem.getQty())!=0 && !refundskus.containsKey(refundItem.getSku())){
								logger.info(""+refundskus.containsKey(refundItem.getSku()));
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
									returnAmount=Double.parseDouble(body.getAdjustment())!=0?
											String.valueOf((Double.parseDouble(body.getGrand_total())-Double.parseDouble(body.getAdjustment())))
											:body.getGrand_total();
									item.setRefReceipt(body.getOrder_id());
									item.setRefStoreCode(body.getStore_id());
									if(isGuestUser) {
									item.setRefReceipt(body.getOrder_id());
									item.setRefStoreCode(body.getStore_id());
									receipt.setBillToInfo1(membership);
									receipt.setBillToInfo2(membership);
									}
									receipt.setTotal(returnAmount);
									double itemQty=(Double.valueOf(magentoDRItem.getQty()));
									item.setQty(String.valueOf((itemQty*=-1)));
									//item.setQty(magentoDRItem.getQty());
									items.add(item);
								}
							}else {
							returnAmount=Double.parseDouble(body.getAdjustment())!=0?
									String.valueOf((Double.parseDouble(body.getGrand_total())-Double.parseDouble(body.getAdjustment())))
									:body.getGrand_total();
							item.setRefReceipt(body.getOrder_id());
							item.setRefStoreCode(body.getStore_id());
							if(isGuestUser) {
							item.setRefReceipt(body.getOrder_id());
							item.setRefStoreCode(body.getStore_id());
							receipt.setBillToInfo1(membership);
							receipt.setBillToInfo1(membership);
							}
							receipt.setTotal(returnAmount);
							double itemQty=(Double.valueOf(magentoDRItem.getQty()));
							item.setQty(String.valueOf((itemQty*=-1)));
							//item.setQty(magentoDRItem.getQty());
							items.add(item);
						  }
						}
					}
					distinctskus.put(magentoDRItem.getSku(),item);
				}
				if(ocDetails!=null && ocDetails.getLoyaltyRedeemReversal()!=null && !ocDetails.getLoyaltyRedeemReversal().isEmpty()) {
					returnAmount = ocDetails.getLoyaltyRedeemReversal();
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
			
			if(body.getCreated_at() != null && !body.getCreated_at().isEmpty()) {
	    		
				try {
					
					String fieldValue = (magentoBody.getOrderdetails().getCreated_at()!=null && !magentoBody.getOrderdetails().getCreated_at().isEmpty()?
										magentoBody.getOrderdetails().getCreated_at():magentoHead.getRequestDate());;
					DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Date date = (Date)formatter.parse(fieldValue); 
					
					formatter = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss");
					String docDate = formatter.format(date);
					receipt.setDocDate(docDate);
					
				} catch (ParseException e) {
						logger.error("Exception ",e);
					}
	    		
	    	}
			
			
			
			if(magentoHead.getReceiptType()!=null && magentoHead.getReceiptType().equalsIgnoreCase(OCConstants.DR_RECEIPT_TYPE_RETURN)) receipt.setReceiptType("2");
			
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

		
	}
