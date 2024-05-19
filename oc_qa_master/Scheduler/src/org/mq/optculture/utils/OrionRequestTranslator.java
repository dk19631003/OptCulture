package org.mq.optculture.utils;

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
import org.mq.captiway.scheduler.beans.Users;
import org.mq.captiway.scheduler.utility.Constants;
import org.mq.optculture.model.DR.DRBody;
import org.mq.optculture.model.DR.DRHead;
import org.mq.optculture.model.DR.DRItem;
import org.mq.optculture.model.DR.DROptCultureDetails;
import org.mq.optculture.model.DR.Receipt;
import org.mq.optculture.model.DR.orion.OrionDRBody;
import org.mq.optculture.model.DR.orion.OrionDRItem;
import org.mq.optculture.model.DR.orion.OrionDRRequest;
import org.mq.optculture.model.DR.orion.OrionMasterCard;
import org.mq.optculture.model.DR.orion.OrionReceipt;
import org.mq.optculture.model.DR.tender.Payments;

public class OrionRequestTranslator {

	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);

	public OrionRequestTranslator() {

	}
	public DRBody convertOrionRequest(OrionDRRequest orionRequest, Users userObj) {

		logger.info("------< Entered into convertOrionRequest >------");

		DRHead orionDRHead = orionRequest.getHead();
		DROptCultureDetails orionOCDetails = orionRequest.getOptcultureDetails();

		OrionDRBody orionDRBody = orionRequest.getBody();
		OrionReceipt orionReceipt = orionDRBody.getReceipt();
		List<OrionDRItem> orionItems = orionDRBody.getItems();
		int itemsCount=0;
		int invcTotalQty=0;


		DRBody Body = new DRBody();

		String membership = orionOCDetails != null && orionOCDetails.getMembershipNumber() != null ? orionOCDetails.getMembershipNumber()
				: Constants.STRING_NILL;
		String email = orionOCDetails.getEmail()!=null && !orionOCDetails.getEmail().isEmpty() ? orionOCDetails.getEmail() : orionReceipt.getBILL_EMAIL();

		Receipt receipt = new Receipt(Constants.STRING_NILL,
				orionReceipt.getINV_TAX_AMT(),
				Constants.STRING_NILL, Constants.STRING_NILL,
				orionReceipt.getINV_DISC_AMT(),
				Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
				Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
				Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
				Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
				orionReceipt.getINV_NET_AMT(),
				Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
				orionReceipt.getINV_RCPT_DT(),
				orionReceipt.getINV_RCPT_NUM(),
				Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, orionReceipt.getCUST_NAME(),
				Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
				Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
				Constants.STRING_NILL, membership, Constants.STRING_NILL, Constants.STRING_NILL,
				Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
				Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
				Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
				Constants.STRING_NILL, Constants.STRING_NILL,
				orionReceipt.getCUST_CODE(),
				Constants.STRING_NILL,orionReceipt.getCUST_NAME(),
				Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
				Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
				orionReceipt.getBILL_MOBILE(),
				Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
				orionReceipt.getLOCATION_CODE(),
				orionReceipt.getLOCN_NAME(),
				Constants.STRING_NILL, orionReceipt.getINV_NET_AMT(),
				orionReceipt.getLOCATION_CODE(),
				Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
				Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
				Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
				orionReceipt.getINV_TAX_AMT(),
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
				email,
				Constants.STRING_NILL, Constants.STRING_NILL,
				orionReceipt.getDocSID(),
				Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
				Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
				orionReceipt.getLOCN_NAME(),
				Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
				Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
				Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
				Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
				Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
				Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL);

		List<DRItem> items = new ArrayList<DRItem>();
		Map<String, DRItem> distinctskus = new HashMap<String, DRItem>();

		if (orionDRHead.getReceiptType() != null
				&& orionDRHead.getReceiptType().equalsIgnoreCase(OCConstants.DR_RECEIPT_TYPE_SALE)) {


			for (OrionDRItem orionItem : orionItems) {

				DRItem item = new DRItem(orionItem.getITEM_CODE(), Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
						Constants.STRING_NILL, Constants.STRING_NILL,
						orionItem.getITEM_DESC(),
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,Constants.STRING_NILL,
						Constants.STRING_NILL, Constants.STRING_NILL,
						orionItem.getITEM_ANLY_CODE_01(), orionItem.getITEM_ANLY_CODE_02(), orionItem.getITEM_ANLY_CODE_03(),
						Constants.STRING_NILL, Constants.STRING_NILL,
						orionItem.getVAT(), orionItem.getINVI_QTY(), orionItem.getINVI_RATE(), orionItem.getDIS(),
						Constants.STRING_NILL, Constants.STRING_NILL,
						Constants.STRING_NILL,
						orionItem.getNET(), orionItem.getGROSS(),
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
						orionItem.getITEM_CODE(), orionItem.getDIS(), Constants.STRING_NILL, Constants.STRING_NILL,
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
						Constants.STRING_NILL, Constants.STRING_NILL, 
						orionItem.getVAT(),
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

				item.setUDF4(orionItem.getITEM_ANLY_CODE_04());
				item.setUDF5(orionItem.getITEM_ANLY_CODE_05());
				item.setUDF7(orionItem.getUOM_CODE());
				item.setUDF11(orionItem.getH_SYS_ID());
				item.setUDF12(orionItem.getREF_SYS_ID());
				item.setBarCode(orionItem.getITEM_BAR_CODE());


				items.add(item);
				distinctskus.put(orionItem.getITEM_CODE(), item);
				itemsCount++;
				receipt.setItemsCount(itemsCount+"");
				try
				{
					invcTotalQty+=Double.parseDouble(orionItem.getINVI_QTY());
				}catch(Exception e) {
					logger.debug("Exception : "+e);
				}
				receipt.setInvcTotalQty(invcTotalQty+"");

			}


		} 
		/*
		else if (orionDRHead.getReceiptType() != null
				&& orionDRHead.getReceiptType().equalsIgnoreCase(OCConstants.DR_RECEIPT_TYPE_RETURN)) {
			List<DetailResults> orionItems = orionDRBody.getDetails().getResults();
			//List<DetailResults> refundItems=orionDRBody.getDetails().getResults();
			Map<String, DetailResults> refundskus = new HashMap<String, DetailResults>();

			//double lineItemPrice=0;
			for (DetailResults orionItem : orionItems) {
				if(!orionItem.getType().equalsIgnoreCase("ItemLine")) continue;
				DRItem item = new DRItem(Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, orionItem.getDescription(),
						Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,Constants.STRING_NILL,
						Constants.STRING_NILL, Constants.STRING_NILL,Constants.STRING_NILL,
						Constants.STRING_NILL,Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
						Constants.STRING_NILL, orionItem.getQty()+"", orionItem.getFinal_unit_price()+""
						, Constants.STRING_NILL, Constants.STRING_NILL, orionItem.getValue()+""
						,Constants.STRING_NILL, orionItem.getCurrent_unit_price()+"" , 
						orionItem.getFinal_unit_price()+""
						,Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL,
						orionItem.getItem_id()+"", Constants.STRING_NILL, Constants.STRING_NILL,
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
				if(orionItem.getItem_custom()!=null) {
					item.setItemCategory(orionItem.getItem_custom().getCategory());
					item.setUDF0(orionItem.getItem_custom().getGroup());
					item.setUDF1(orionItem.getItem_custom().getWidth());
					item.setUDF2(orionItem.getItem_custom().getColor_name());
					item.setUDF3(orionItem.getItem_custom().getStyle_name());
					item.setSize(orionItem.getItem_custom().getSize());
					item.setUPC(orionItem.getItem_custom().getUpc());
					item.setTax2(orionItem.getItem_custom().getTax_category());
				}

				double disc_amt = 0.0;
				try {
					disc_amt = Double.parseDouble(orionItem.getCurrent_unit_price())
							- Double.parseDouble(orionItem.getFinal_unit_price());


				} catch (Exception e) {
				}
				item.setDocItemDiscAmt(disc_amt+"");
				item.setItemLine(orionItem.getId()+"");//item line id
				if(refundskus.containsKey(orionItem.getItem_public_id())) {
					distinctskus.put(orionItem.getItem_public_id(), item);
				}
				items.add(item);
				//logger.info("distinctskus " + distinctskus.size());
				itemsCount++;
				receipt.setItemsCount(itemsCount+"");
				try
				{
					invcTotalQty+=Double.parseDouble(orionItem.getQty());
				}catch(Exception e) {}

				receipt.setInvcTotalQty(invcTotalQty+"");
			}


		} */


		if (orionReceipt.getINV_RCPT_DT() != null && !orionReceipt.getINV_RCPT_DT().isEmpty()) {
			try {
				String strCreatedDate = orionReceipt.getINV_RCPT_DT();
				try {
					receipt.setDocDate(getDocDate(strCreatedDate));
				} catch (Exception e) {
					logger.error("Exception while converting doc date "+strCreatedDate ,e);
				}
			} catch (Exception e) {
				logger.error("Exception ", e);
			}
		}



		Body.setItems(items);
		Body.setReceipt(receipt);
		List<Payments> listOfpayments = new ArrayList<Payments>();

		OrionMasterCard orionMasterCard = orionDRBody.getMASTER_CARD();
		if(orionMasterCard!=null) {
			Payments paymentObj = new Payments(orionMasterCard.getTENDER_AMOUNT(), Constants.STRING_NILL, 
					Constants.STRING_NILL, Constants.STRING_NILL, orionMasterCard.getRTENDER_AMOUNT(), orionMasterCard.getCURRENCY_CODE());
			listOfpayments.add(paymentObj);
		}


		Body.setPayments(listOfpayments);

		logger.info("-------< convertOrionRequest finished >---------");

		return Body;
	}//convertOrionRequest

	private String getDocDate(String strCreatedDate){

		String docDate = "";
		try {
			String fieldValue = strCreatedDate;
			DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a");
			Date date = (Date)formatter.parse(fieldValue); 

			formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
			docDate = formatter.format(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return docDate;
	}//getDocDate





}
