package org.mq.marketer.campaign.controller.report;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.mq.marketer.campaign.beans.ContactsLoyalty;
import org.mq.marketer.campaign.beans.CouponCodes;
import org.mq.marketer.campaign.beans.Coupons;
import org.mq.marketer.campaign.beans.LoyaltyTransactionChild;
import org.mq.marketer.campaign.beans.OrganizationStores;
import org.mq.marketer.campaign.beans.POSMapping;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.beans.RetailProSalesCSV;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.custom.MyDatebox;
import org.mq.marketer.campaign.dao.CampaignSentDao;
import org.mq.marketer.campaign.dao.ContactsLoyaltyDao;
import org.mq.marketer.campaign.dao.CouponCodesDao;
import org.mq.marketer.campaign.dao.OrganizationStoresDao;
import org.mq.marketer.campaign.dao.POSMappingDao;
import org.mq.marketer.campaign.dao.SMSCampaignSentDao;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.dao.RetailProSalesDao;
import org.mq.marketer.campaign.dao.SMSCampaignSentDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.POSFieldsEnum;
import org.mq.marketer.campaign.general.PageListEnum;
import org.mq.marketer.campaign.general.PageUtil;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Redirect;
import org.mq.optculture.data.dao.JdbcResultsetHandler;
import org.mq.optculture.data.dao.LoyaltyTransactionChildDao;
import org.mq.optculture.utils.OCCSVWriter;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.event.PagingEvent;

public class CouponsController extends GenericForwardComposer {
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
//	private Users currentUser;
//	private CouponDiscountGenerateDao couponDiscountGenerateDao;
//	private CouponsDao couponsDao;
//	private TimeZone clientTimeZone;
//	private Grid couponsGridId;
	private Rows couponsRowsId,promotionRedmDetailRowsId,storeRedmRowsId;
	private Div couponsReportDivId,storeReportDivId;
//	private Coupons coupons;
//	private CouponDiscountGeneration couponDiscountGeneration;
	private Paging couponListBottomPagingId,promoDetailPagingId,storePagingId;
	private MyDatebox storeFromDateboxId, storeToDateboxId;
	private Listbox pageSizeLbId,memberPerPagePromoDetailLBId,exportFilterPromoDetailLbId,memberPerPageStoreLBId;
	private Label couponLblId, validityLblId, discountLblId,storeID,promoID;
	Coupons coupObj;
	CouponCodesDao couponCodesDao;
	CampaignSentDao campaignSentDao;
	SMSCampaignSentDao SMSCampaignSentDao;
	private RetailProSalesDao retailProsalesDao;
	private List<Map<String, Object>> storeNumberNameMapList;
	private TimeZone clientTimeZone;
	private OrganizationStoresDao organizationStoresDao;
	private String store;
	private String storeFromDateStr,storeToDateStr;
	private Users users;
	private Listbox srchLbId;
	private Div RedeemedDateId,IssuedDateId,StoreNameId,statusId;
	private Listbox searchByStoreNameId,filterByStatusId;
//	private  String emailOrPhone ;
	private MyDatebox fromRedeemedDateboxId,toRedeemedDateboxId;
	private MyDatebox fromIssuedDateboxId,toIssuedDateboxId;
	String userIds;
	public CouponsController() {
		session = Sessions.getCurrent();
//		currentUser = GetUser.getUserObj();
		
		String style = "font-weight:bold;font-size:15px;color:#313031;"
				+ "font-family:Arial,Helvetica,sans-serif;align:left";
		users = GetUser.getUserObj();
		PageUtil.setHeader("Discount Code Report", "", style, true);
		SMSCampaignSentDao = (SMSCampaignSentDao)SpringUtil.getBean("SMSCampaignSentDao");
		campaignSentDao = (CampaignSentDao)SpringUtil.getBean("campaignSentDao");
		couponCodesDao = (CouponCodesDao) SpringUtil.getBean("couponCodesDao");
		retailProsalesDao= (RetailProSalesDao)SpringUtil.getBean("retailProSalesDao");
		organizationStoresDao =	(OrganizationStoresDao)SpringUtil.getBean("organizationStoresDao");
//		this.couponsDao = (CouponsDao) SpringUtil.getBean("couponsDao");
		/*this.couponDiscountGenerateDao = (CouponDiscountGenerateDao) SpringUtil
				.getBean("couponDiscountGenerateDao");*/
//		clientTimeZone = (TimeZone) session.getAttribute("clientTimeZone");

	}

	public void doAfterCompose(Component comp) throws Exception {
		// TODO Auto-generated method stub
		super.doAfterCompose(comp);
//		emailOrPhone = currentUser.getEmailId() != null ? currentUser.getEmailId() : currentUser.getPhone() != null ?currentUser.getPhone() :"";
		/*
		 * couponListBottomPagingId.setActivePage(0);
		 * couponListBottomPagingId.addEventListener("onPaging", this);
		 */
		
		if(session == null ){
			logger.error("Session Object is Null so redirecting.....");
			Redirect.goToPreviousPage();
		}
		UsersDao usersDao = (UsersDao) ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
		List<Users> userList = usersDao.getUsersListByOrg(users.getUserOrganization().getUserOrgId());
		StringBuilder sb=new StringBuilder();
		userList.forEach(user ->{
			if(sb.length()>0)sb.append(",");
			sb.append(user.getUserId());
		});
		userIds=sb.toString();
		
		coupObj = (Coupons) session.getAttribute("COUP_REDEEMED_DETAILS");
		logger.info(" getting coupon Obj from Session ::"+coupObj);
		clientTimeZone = (TimeZone) session.getAttribute("clientTimeZone");
		// validity
		storeNumberNameMapList  = organizationStoresDao.findStoreNumberNameMapList(GetUser.getUserObj().getUserOrganization().getUserOrgId());
		String validity = null;
		if (coupObj == null) {
			couponsReportDivId.setVisible(false);
			storeReportDivId.setVisible(true);
			store = (String)session.getAttribute("STORE_REDEEMED_DETAILS");
			storeID.setValue("Discount Codes redeemed at store : "+fetchStoreName(store));
			Calendar cal = MyCalendar.getNewCalendar();
			storeToDateboxId.setValue(cal);
			logger.debug("ToDate (server) :" + cal);
			//cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) - 1);
			cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) - 3);
			cal.set(Calendar.DATE, cal.get(Calendar.DATE) + 1);
			logger.debug("FromDate (server) :" + cal);
			storeFromDateboxId.setValue(cal);
			
			storeFromDateStr = MyCalendar.calendarToString(getStartDate(storeFromDateboxId), MyCalendar.FORMAT_DATETIME_STYEAR);
			storeToDateStr = MyCalendar.calendarToString(getEndDate(storeToDateboxId), MyCalendar.FORMAT_DATETIME_STYEAR);
			int storeListSize = couponCodesDao.findTotalCountForStore(users.getUserOrganization().getUserOrgId(),store,storeFromDateStr,storeToDateStr);
			storePagingId.setTotalSize(storeListSize);
			storePagingId.setPageSize(Integer.parseInt(memberPerPageStoreLBId.getSelectedItem().getLabel()));
			storePagingId.setActivePage(0);
			storePagingId.addEventListener("onPaging",this);
			storePagingId.setAttribute("onPaging", "store");
			fillStoreRedemption(0,Integer.parseInt(memberPerPageStoreLBId.getSelectedItem().getLabel()));
			
		}else {
			promoID.setValue("Store redemption of discount code : "+coupObj.getCouponName());
			if(coupObj.getExpiryType().equals(Constants.COUP_VALIDITY_PERIOD_STATIC)){
				validity = MyCalendar.calendarToString(
				coupObj.getCouponCreatedDate(),
				MyCalendar.FORMAT_DATEONLY_GENERAL)
				+ ""
				+ " to "
				+ MyCalendar.calendarToString(coupObj.getCouponExpiryDate(),
						MyCalendar.FORMAT_DATEONLY_GENERAL);
			}
			else if(coupObj.getExpiryType().equals(Constants.COUP_VALIDITY_PERIOD_DYNAMIC)){
				 validity = "Dynamic";
			}
		// discount
		String discount = "";
		if (coupObj.getDiscountType().equals("Percentage")) {
			discount = "% on ";
		} else {
			discount = " $ on ";
		}
		
		//discount= discount+" "+coupObj.getDiscountCriteria();
		if(coupObj.getDiscountCriteria().trim().contains("SKU")) {
			discount += " Product";
		}else {
			discount += " Receipt";
		}
		//discount = discount + " " + coupObj.getDiscountCriteria();
		
		//Coup Name
		couponLblId.setValue(coupObj.getCouponName());
		validityLblId.setValue(validity);
		discountLblId.setValue(discount);
		//int totalSize = couponCodesDao.findTotCountCouponCodes(coupObj.getCouponId());
		setPageCount();
		promoDetailPagingId.addEventListener("onPaging", this);
		promoDetailPagingId.setAttribute("onPaging", "storeDetail");
		int tempCount = Integer.parseInt(pageSizeLbId.getSelectedItem()
				.getLabel());
		
		getCouponCodes(0, tempCount,getFilterData());
		storeNumberNameMapList  = organizationStoresDao.findStoreNumberNameMapList(GetUser.getUserObj().getUserOrganization().getUserOrgId());
		int promoListSize = couponCodesDao.findTotalCountOfStoreRelatedToPromo(GetUser.getUserObj().getUserOrganization().getUserOrgId(),coupObj.getCouponId());
		setSizeOfPageAndFillPromoDetailsGrid(promoListSize);
		}
		setDefaultStores();
	} // doAfterCompose
	
	public void onClick$storeResetAnchId() {
		// onClick$backBtnId();
		 Calendar cal = MyCalendar.getNewCalendar();
		 storeToDateboxId.setValue(cal);
		 logger.debug("ToDate (server) :" + cal);
		 //cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) - 1);
		 cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) - 3);
		 cal.set(Calendar.DATE, cal.get(Calendar.DATE) + 1);
		 logger.debug("FromDate (server) :" + cal);
		 storeFromDateboxId.setValue(cal);
		 
		 onClick$getStoreBetweenDatesBtnId();
		 

	 }
	
	public void onClick$getStoreBetweenDatesBtnId() {
		
		Calendar  start = storeFromDateboxId.getClientValue();
		Calendar end = storeToDateboxId.getClientValue();
		
		if(end.before(start)) {
			MessageUtil.setMessage("'From' date cannot be later than 'To' date.", "red");
			return;
		}
		
		storeFromDateStr = MyCalendar.calendarToString(getStartDate(storeFromDateboxId), MyCalendar.FORMAT_DATETIME_STYEAR);
		storeToDateStr = MyCalendar.calendarToString(getEndDate(storeToDateboxId), MyCalendar.FORMAT_DATETIME_STYEAR);
		 
		int storeListSize = couponCodesDao.findTotalCountForStore(users.getUserOrganization().getUserOrgId(),store,storeFromDateStr,storeToDateStr);
		 
		 storePagingId.setTotalSize(storeListSize);
		 storePagingId.setActivePage(0);
		 
		 int pNo = Integer.parseInt( memberPerPageStoreLBId.getSelectedItem().getLabel());
		 fillStoreRedemption(0, pNo);
	}
	
public void onSelect$memberPerPageStoreLBId() {
		
		try {
			
			logger.info("just enter here");
			
			
			String selectStr = memberPerPageStoreLBId.getSelectedItem().getLabel();
			int pNo = Integer.parseInt(selectStr);
			
			storePagingId.setPageSize(pNo);
			storePagingId.setActivePage(0);	
				
			fillStoreRedemption(0,pNo);
			
		} catch (NumberFormatException e) {
			logger.error("Exception ::" , e);
		}
	} // onClick$memberPerPageLBId
	private int fillStoreRedemption(int fromInt, int lbxLenght) {

		try {
			int size;
			Components.removeAllChildren(storeRedmRowsId);
			
			/*int count =  promoCodeRowsId.getItemCount();
			
			for(; count>0; count--) {
				promoCodeRowsId.removeAt(count-1);
			}*/
			
			List<Map<String, Object>> promoObjArrList  = couponCodesDao.findStoreRedemptionList(users.getUserOrganization().getUserOrgId(),store, fromInt, lbxLenght,"",storeFromDateStr,storeToDateStr); //-- sm change
			
			if(promoObjArrList == null || promoObjArrList.size() == 0 ) {
				 logger.info(" *** No promo code data exists for this user");
				 return 0;
			 }
			
			 logger.info(">>> PromoObjArrList size is :"+promoObjArrList.size());
			 size = promoObjArrList.size();
			 Row tempRow = null;
			 String promo;
			 DecimalFormat decimalFormat = new DecimalFormat("#0.00");
			 for (Map<String, Object> eachMap: promoObjArrList) {
				 
				 tempRow  = new Row();
				 //SELECT promo_code, ROUND(SUM((quantity*sales_price)+tax),2) AS REVENUE , COUNT(customer_id) AS COUNT
				 //PromoCode
				 Label PromoCodeLbl = new Label();
				 if(eachMap.containsKey("name") && eachMap.get("name") != null) {
					// promo = fetchStoreName(eachMap.get("couponId").toString());
					 //PromoCodeLbl.setValue(eachMap.get("store_number").toString());
					 PromoCodeLbl.setValue(eachMap.get("name").toString());
					// PromoCodeLbl.setStyle("cursor:pointer;color:blue;text-decoration: underline;");
					// PromoCodeLbl.addEventListener("onClick", this);
					// PromoCodeLbl.setAttribute("original value", eachMap.get("store_number").toString());
				 }
				 PromoCodeLbl.setParent(tempRow);
				
				 //Revenue
				 Label revenueLbl = new Label();
				 if(eachMap.containsKey("REVENUE") && eachMap.get("REVENUE") != null) {
					 revenueLbl.setValue(decimalFormat.format(eachMap.get("REVENUE")));
				 }
				 revenueLbl.setParent(tempRow);
				 
				 //No. of customer
				 Label totalCustmerLbl = new Label();
				 if(eachMap.containsKey("COUNT") && eachMap.get("COUNT") != null) {
					 
					 totalCustmerLbl.setValue(eachMap.get("COUNT").toString());
				 }
				 totalCustmerLbl.setParent(tempRow);
				 
				
				// tempRow.setValue("store_number");
				 tempRow.setParent(storeRedmRowsId);
			}
			 return size;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
			return 0;
		}
	} // fillStoreRedemption
	
public Calendar getStartDate(MyDatebox fromDateboxId){

		
		try {
			Calendar serverFromDateCal = fromDateboxId.getServerValue();
			Calendar tempClientFromCal = fromDateboxId.getClientValue();
			serverFromDateCal.set(Calendar.HOUR_OF_DAY, 
					serverFromDateCal.get(Calendar.HOUR_OF_DAY)-tempClientFromCal.get(Calendar.HOUR_OF_DAY));
			serverFromDateCal.set(Calendar.MINUTE, 
					serverFromDateCal.get(Calendar.MINUTE)-tempClientFromCal.get(Calendar.MINUTE));
			serverFromDateCal.set(Calendar.SECOND, 0);
			return serverFromDateCal;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::::",e);
			return null;
		}
		
	}
	
	
	
	
	public Calendar getEndDate(MyDatebox toDateboxId) {
		
		
		Calendar serverToDateCal = toDateboxId.getServerValue();
		
		Calendar tempClientToCal = toDateboxId.getClientValue();
		
		
		//change the time for startDate and endDate in order to consider right from the 
		// starting time of startDate to ending time of endDate
		
		serverToDateCal.set(Calendar.HOUR_OF_DAY, 
				23+serverToDateCal.get(Calendar.HOUR_OF_DAY)-tempClientToCal.get(Calendar.HOUR_OF_DAY));
		serverToDateCal.set(Calendar.MINUTE, 
				59+serverToDateCal.get(Calendar.MINUTE)-tempClientToCal.get(Calendar.MINUTE));
		serverToDateCal.set(Calendar.SECOND, 59);		
		
		return serverToDateCal;
		
		
		
	}
	public void getCouponCodes(int startIdx, int endIdx ,Map<String, String> searchData) {

		try {
			List<CouponCodes> couponCodeLsit = couponCodesDao.findByCouponCode(coupObj.getCouponId(), startIdx, endIdx, searchData);
			
			
			
			int offSetCount  = 0;
			int tempCount = Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel());

			/* int offSetCount  = endIdx-startIdx;
			if(startIdx>0 && endIdx>0 && (startIdx==endIdx || startIdx>endIdx)) {
				startIdx=0;	
				offSetCount=endIdx;
			}
			if(startIdx>endIdx) offSetCount  = startIdx-endIdx;*/
			
			if (couponCodeLsit == null || couponCodeLsit.size() == 0) {
				logger.info(" No Promo-codes exists ");
				//return;
				//findsize	34
				List<CouponCodes> totalCouponCodeLsit = couponCodesDao.findByCouponCode(coupObj.getCouponId(), 0, startIdx+endIdx, searchData);	
				if(totalCouponCodeLsit!=null && totalCouponCodeLsit.size()>0 && startIdx>=totalCouponCodeLsit.size()) {
					//startIdx = startIdx-findsize  // 40-34 = 6 

					//logger.info(" startIdx :"+startIdx +" offSetCount :"+offSetCount +" totalCouponCodeLsit.size() :"+totalCouponCodeLsit.size());
			
					startIdx = startIdx-totalCouponCodeLsit.size();
					//offSetCount = startIdx!=0 ? tempCount - (startIdx%tempCount) : endIdx;
					offSetCount = endIdx;
					//logger.info(" startIdx :"+startIdx +" offSetCount :"+offSetCount);
				
				}else {
					offSetCount=endIdx;
				}
			}
			
			if(couponCodeLsit!=null && couponCodeLsit.size()>0  && (startIdx==0 || startIdx>couponCodeLsit.size())) {
				
				if(couponCodeLsit.size()<tempCount) {
					offSetCount = tempCount-couponCodeLsit.size();
					startIdx =  (couponCodeLsit.size()%tempCount) ==0 ||  (couponCodeLsit.size()%tempCount) < tempCount ? 0 : startIdx;
				}else if(startIdx!=0){			
					startIdx = startIdx-couponCodeLsit.size();
					offSetCount = startIdx!=0 ? tempCount - (startIdx%tempCount) : endIdx;
				}
			}
			
			if(startIdx==endIdx) offSetCount =endIdx;
			

			Components.removeAllChildren(couponsRowsId);
			DecimalFormat decimalFormat = new DecimalFormat("#0.00");
			int size=0;
			if(couponCodeLsit!=null && !couponCodeLsit.isEmpty()) {
				
				//offSetCount = couponCodeLsit.size()<offSetCount ? offSetCount-couponCodeLsit.size() : 0;  
				
				for (CouponCodes coupCodesObj : couponCodeLsit) {
					double totdisc=0.0;
					String itemInfo=coupCodesObj.getItemInfo();
					if(itemInfo!=null && !itemInfo.isEmpty()) {
						//Display multiple rows based on the number of items in the itemInfo
						String[] itemInfoArray = itemInfo.split("\\"+Constants.DELIMITER_PIPE);
						for(String eachitem : itemInfoArray) {
							String[] discAmt = eachitem.split(""+(eachitem.startsWith("P:")?Constants.DELIMETER_COMMA:Constants.DELIMETER_COLON));
							totdisc += (discAmt.length>=3?(Double.parseDouble(discAmt[2]) * Double.parseDouble(discAmt[1])):(Double.parseDouble(discAmt[1])));
						}
					for(String eachitem : itemInfoArray) {
					Row row = new Row();
					// Coupon Code
					row.appendChild(new Label(coupCodesObj.getCouponCode() != null ? coupCodesObj.getCouponCode() : ""));

					// Status
					if(coupCodesObj.getStatus().equalsIgnoreCase(Constants.COUP_CODE_STATUS_ACTIVE)) {
						row.appendChild(new Label("Issued"));
					}else if(coupCodesObj.getStatus().equalsIgnoreCase(Constants.COUP_CODE_STATUS_INVENTORY)) {
						row.appendChild(new Label("Not Issued"));
					}else {
						row.appendChild(new Label(coupCodesObj.getStatus()));
					}
					// issued Email Id /Mobile //TODO
					row.appendChild(new Label(coupCodesObj.getIssuedTo()  != null? coupCodesObj.getIssuedTo() : ""));
					
					// Redeemed To
					String redeemVal  = coupCodesObj.getRedeemedTo();
					if(redeemVal != null) {
						
						if(redeemVal.startsWith("Customer Id")) {
							
							int pos = redeemVal.indexOf(";");
							if(pos == -1) {
								redeemVal = "";
							}else {
								redeemVal = redeemVal.substring(++pos);
							}
							
						}  else if(redeemVal.endsWith(";")) {
							
							redeemVal="";
						}		
					}
					logger.info("redeemValvalue:: "+redeemVal);
					
					
					row.appendChild(new Label(redeemVal  != null? redeemVal : ""));	
					
					//Membership #
					row.appendChild(new Label(coupCodesObj.getMembership()!= null? coupCodesObj.getMembership() : ""));
					// campaignName
					row.appendChild(new Label(coupCodesObj.getCampaignName()));

					// issued On
					row.appendChild(new Label(MyCalendar.calendarToString(
							coupCodesObj.getIssuedOn(),
							MyCalendar.FORMAT_DATETIME_STDATE_PROMO , clientTimeZone)));
					// Redeemed On
					row.appendChild(new Label(MyCalendar.calendarToString(
							coupCodesObj.getRedeemedOn(),
							MyCalendar.FORMAT_DATETIME_STDATE_PROMO , clientTimeZone)));
					// Store
					row.appendChild(new Label(coupCodesObj.getStoreNumber() != null ? fetchStoreName(coupCodesObj.getStoreNumber()):""));
					
					// DocsId
					//row.appendChild(new Label(coupCodesObj.getStoreNumber() != null ? coupCodesObj.getDocSid()+"":""));
					
					//ReceiptNumber
				        row.appendChild(new Label(coupCodesObj.getStoreNumber() != null ?(coupCodesObj.getReceiptNumber() != null ? coupCodesObj.getReceiptNumber()+"": "--"):""));

				
				        //count	
					 if(coupCodesObj.getDocSid()!= null && !coupCodesObj.getDocSid().isEmpty())
					{	
						int count = couponCodesDao.getCountByDocsId(coupCodesObj.getCouponId().getCouponId(),coupCodesObj.getCouponCode(),coupCodesObj.getDocSid());
						row.appendChild(new Label(count!=0 ? count+"":""));
					}else {
						row.appendChild(new Label(""));
					}
					 //Rewards redeemed if any
					row.appendChild(new Label(coupCodesObj.getUsedLoyaltyPoints() != null ? ""+coupCodesObj.getUsedLoyaltyPoints().intValue()+" "+
					 (coupCodesObj.getValueCode()!=null ?coupCodesObj.getValueCode() :"") : "0"));
					
					String[] discAmt = eachitem.split(""+(eachitem.startsWith("P:")?Constants.DELIMETER_COMMA:Constants.DELIMETER_COLON));
					//disc
					row.appendChild(new Label(decimalFormat.format(Double.valueOf((discAmt.length>=3)?discAmt[2]:discAmt[1]))));
					// total discount
					row.appendChild(new Label(decimalFormat.format(totdisc)));
					//row.appendChild(new Label( coupCodesObj.getTotDiscount() != null ?""+ decimalFormat.format(coupCodesObj.getTotDiscount()) : ""));
					// total Revenue
					row.appendChild(new Label( coupCodesObj.getTotRevenue() != null ? ""+decimalFormat.format(coupCodesObj.getTotRevenue()) : ""));
			       
					//Item Info
					//row.appendChild(new Label( eachitem != null && !eachitem.isEmpty() ? discAmt[0] : ""));
					row.appendChild(new Label( eachitem != null && !eachitem.isEmpty() ?(eachitem.startsWith("P:")?discAmt[0].split(""+Constants.DELIMETER_COLON)[1]:discAmt[0]):""));
					//row.appendChild(new Label( coupCodesObj.getUsedLoyaltyPoints() != null ? ""+coupCodesObj.getUsedLoyaltyPoints().intValue() : "0"));

					row.setValue(coupCodesObj);
					row.setParent(couponsRowsId);
					
					size+=1;
					}
					}else {
						Row row = new Row();
						// Coupon Code
						row.appendChild(new Label(coupCodesObj.getCouponCode() != null ? coupCodesObj.getCouponCode() : ""));

						// Status
						if(coupCodesObj.getStatus().equalsIgnoreCase(Constants.COUP_CODE_STATUS_ACTIVE)) {
							row.appendChild(new Label("Issued"));
						}else if(coupCodesObj.getStatus().equalsIgnoreCase(Constants.COUP_CODE_STATUS_INVENTORY)) {
							row.appendChild(new Label("Not Issued"));
						}else {
							row.appendChild(new Label(coupCodesObj.getStatus()));
						}
						// issued Email Id /Mobile //TODO
						row.appendChild(new Label(coupCodesObj.getIssuedTo()  != null? coupCodesObj.getIssuedTo() : ""));
						
						// Redeemed To
						String redeemVal  = coupCodesObj.getRedeemedTo();
						if(redeemVal != null) {
							
							if(redeemVal.startsWith("Customer Id")) {
								
								int pos = redeemVal.indexOf(";");
								if(pos == -1) {
									redeemVal = "";
								}else {
									redeemVal = redeemVal.substring(++pos);
								}
								
							}  else if(redeemVal.endsWith(";")) {
								
								redeemVal="";
							}	
						}
						logger.info("redeemValvalue:: "+redeemVal);
						row.appendChild(new Label(redeemVal  != null? redeemVal : ""));	
						
						//Membership #
						row.appendChild(new Label(coupCodesObj.getMembership()!= null? coupCodesObj.getMembership() : ""));
						// campaignName
						row.appendChild(new Label(coupCodesObj.getCampaignName()));

						// issued On
						row.appendChild(new Label(MyCalendar.calendarToString(
								coupCodesObj.getIssuedOn(),
								MyCalendar.FORMAT_DATETIME_STDATE_PROMO , clientTimeZone)));
						// Redeemed On
						row.appendChild(new Label(MyCalendar.calendarToString(
								coupCodesObj.getRedeemedOn(),
								MyCalendar.FORMAT_DATETIME_STDATE_PROMO , clientTimeZone)));
						// Store
						row.appendChild(new Label(coupCodesObj.getStoreNumber() != null ? fetchStoreName(coupCodesObj.getStoreNumber()):""));
						
						// DocsId
						//row.appendChild(new Label(coupCodesObj.getStoreNumber() != null ? coupCodesObj.getDocSid()+"":""));
						
						//ReceiptNumber
					        row.appendChild(new Label(coupCodesObj.getStoreNumber() != null ?(coupCodesObj.getReceiptNumber() != null ? coupCodesObj.getReceiptNumber()+"": "--"):""));

					
					        //count	
						 if(coupCodesObj.getDocSid()!= null && !coupCodesObj.getDocSid().isEmpty())
						{	
							int count = couponCodesDao.getCountByDocsId(coupCodesObj.getCouponId().getCouponId(),coupCodesObj.getCouponCode(),coupCodesObj.getDocSid());
							row.appendChild(new Label(count!=0 ? count+"":""));
						}else {
							row.appendChild(new Label(""));
						}
						 //Rewards redeemed if any
						row.appendChild(new Label(coupCodesObj.getUsedLoyaltyPoints() != null ? ""+coupCodesObj.getUsedLoyaltyPoints().intValue()+" "+
						 (coupCodesObj.getValueCode()!=null ?coupCodesObj.getValueCode() :"") : "0"));
						
						//disc
						row.appendChild(new Label( coupCodesObj.getTotDiscount() != null ?""+ decimalFormat.format(coupCodesObj.getTotDiscount()) : ""));
						// total discount
						row.appendChild(new Label( coupCodesObj.getTotDiscount() != null ?""+ decimalFormat.format(coupCodesObj.getTotDiscount()) : ""));
						// total Revenue
						row.appendChild(new Label( coupCodesObj.getTotRevenue() != null ? ""+decimalFormat.format(coupCodesObj.getTotRevenue()) : ""));
				       
						//Item Info
						row.appendChild(new Label("--"));
						//row.appendChild(new Label( coupCodesObj.getUsedLoyaltyPoints() != null ? ""+coupCodesObj.getUsedLoyaltyPoints().intValue() : "0"));

						row.setValue(coupCodesObj);
						row.setParent(couponsRowsId);
						
						size+=1;
					}
				}
			}
			
			
			if(offSetCount==0) return;
			
			List<CouponCodes> couponCodeLsit2 = couponCodesDao.findByCouponCode_docsid_not_null(coupObj.getCouponId(), startIdx, offSetCount, searchData);
			
			if (couponCodeLsit2 == null || couponCodeLsit2.size() == 0) {
				logger.info(" docsid list No Discount codes exists ");
				//return;
			}

			
			if(couponCodeLsit2!=null && !couponCodeLsit2.isEmpty()) {
				for (CouponCodes coupCodesObj : couponCodeLsit2) {
					
					double totdisc=0.0;
					String itemInfo=coupCodesObj.getItemInfo();
					if(itemInfo!=null && !itemInfo.isEmpty()) {
						//Display multiple rows based on the number of items in the itemInfo
					String[] itemInfoArray = itemInfo.split("\\"+Constants.DELIMITER_PIPE);
					for(String eachitem : itemInfoArray) {
						String[] discAmt = eachitem.split(""+(eachitem.startsWith("P:")?Constants.DELIMETER_COMMA:Constants.DELIMETER_COLON));
						totdisc += (discAmt.length>=3?(Double.parseDouble(discAmt[2]) * Double.parseDouble(discAmt[1])):(Double.parseDouble(discAmt[1])));
					}
					for(String eachitem : itemInfoArray) {
						
					
					Row row = new Row();
					// Coupon Code
					row.appendChild(new Label(coupCodesObj.getCouponCode() != null ? coupCodesObj.getCouponCode() : ""));

					// Status
					if(coupCodesObj.getStatus().equalsIgnoreCase(Constants.COUP_CODE_STATUS_ACTIVE)) {
						row.appendChild(new Label("Issued"));
					}else if(coupCodesObj.getStatus().equalsIgnoreCase(Constants.COUP_CODE_STATUS_INVENTORY)) {
						row.appendChild(new Label("Not Issued"));
					}else {
						row.appendChild(new Label(coupCodesObj.getStatus()));
					}
					// issued Email Id /Mobile //TODO
					row.appendChild(new Label(coupCodesObj.getIssuedTo()  != null? coupCodesObj.getIssuedTo() : ""));
					
					// Redeemed To
					String redeemVal  = coupCodesObj.getRedeemedTo();
					if(redeemVal != null) {
						
						if(redeemVal.startsWith("Customer Id")) {
							
							int pos = redeemVal.indexOf(";");
							if(pos == -1) {
								redeemVal = "";
							}else {
								redeemVal = redeemVal.substring(++pos);
							}
							
						}  else if(redeemVal.endsWith(";")) {
							
							redeemVal="";
						}
					}
					logger.info("redeemValvalue:: "+redeemVal);
					row.appendChild(new Label(redeemVal  != null? redeemVal : ""));	
					
					//Membership #
					row.appendChild(new Label(coupCodesObj.getMembership()!= null? coupCodesObj.getMembership() : ""));
					// campaignName
					row.appendChild(new Label(coupCodesObj.getCampaignName()));

					// issued On
					row.appendChild(new Label(MyCalendar.calendarToString(
							coupCodesObj.getIssuedOn(),
							MyCalendar.FORMAT_DATETIME_STDATE_PROMO , clientTimeZone)));
					// Redeemed On
					row.appendChild(new Label(MyCalendar.calendarToString(
							coupCodesObj.getRedeemedOn(),
							MyCalendar.FORMAT_DATETIME_STDATE_PROMO , clientTimeZone)));
					// Store
					row.appendChild(new Label(coupCodesObj.getStoreNumber() != null ? fetchStoreName(coupCodesObj.getStoreNumber()):""));
					
					// DocsId
					//row.appendChild(new Label(coupCodesObj.getStoreNumber() != null ? coupCodesObj.getDocSid()+"":""));
					
					//ReceiptNumber
				        row.appendChild(new Label(coupCodesObj.getStoreNumber() != null ?(coupCodesObj.getReceiptNumber() != null ? coupCodesObj.getReceiptNumber()+"": "--"):""));

				
				        //count	
					 if(coupCodesObj.getDocSid()!= null && !coupCodesObj.getDocSid().isEmpty())
					{	
						int count = couponCodesDao.getCountByDocsId(coupCodesObj.getCouponId().getCouponId(),coupCodesObj.getCouponCode(),coupCodesObj.getDocSid());
						row.appendChild(new Label(count!=0 ? count+"":""));
					}else {
						row.appendChild(new Label(""));
					}
					 
					// Rewards redeemed if any 
					 //row.appendChild(new Label( coupCodesObj.getUsedLoyaltyPoints() != null ? ""+coupCodesObj.getUsedLoyaltyPoints().intValue() : "0"));
					 row.appendChild(new Label(coupCodesObj.getUsedLoyaltyPoints() != null ? ""+coupCodesObj.getUsedLoyaltyPoints().intValue()+" "+
							 (coupCodesObj.getValueCode()!=null ?coupCodesObj.getValueCode() :"") : "0"));
							
					String[] discAmt = eachitem.split(""+(eachitem.startsWith("P:")?Constants.DELIMETER_COMMA:Constants.DELIMETER_COLON));
					//disc
					row.appendChild(new Label(decimalFormat.format(Double.valueOf((discAmt.length>=3)?discAmt[2]:discAmt[1]))));
					// total discount
					//row.appendChild(new Label( coupCodesObj.getTotDiscount() != null ?""+ decimalFormat.format(coupCodesObj.getTotDiscount()) : ""));
					row.appendChild(new Label(decimalFormat.format(totdisc)));
					// total Revenue
					row.appendChild(new Label( coupCodesObj.getTotRevenue() != null ? ""+decimalFormat.format(coupCodesObj.getTotRevenue()) : ""));
			       
					//Item Info
					row.appendChild(new Label( eachitem != null && !eachitem.isEmpty() ? (eachitem.startsWith("P:")?discAmt[0].split(""+Constants.DELIMETER_COLON)[1]:discAmt[0]) : ""));
					//row.appendChild(new Label( coupCodesObj.getUsedLoyaltyPoints() != null ? ""+coupCodesObj.getUsedLoyaltyPoints().intValue() : "0"));

					row.setValue(coupCodesObj);
					row.setParent(couponsRowsId);
					
					size+=1;
				}
				}else {
					Row row = new Row();
					// Coupon Code
					row.appendChild(new Label(coupCodesObj.getCouponCode() != null ? coupCodesObj.getCouponCode() : ""));

					// Status
					if(coupCodesObj.getStatus().equalsIgnoreCase(Constants.COUP_CODE_STATUS_ACTIVE)) {
						row.appendChild(new Label("Issued"));
					}else if(coupCodesObj.getStatus().equalsIgnoreCase(Constants.COUP_CODE_STATUS_INVENTORY)) {
						row.appendChild(new Label("Not Issued"));
					}else {
						row.appendChild(new Label(coupCodesObj.getStatus()));
					}
					// issued Email Id /Mobile //TODO
					row.appendChild(new Label(coupCodesObj.getIssuedTo()  != null? coupCodesObj.getIssuedTo() : ""));
					
					// Redeemed To
					String redeemVal  = coupCodesObj.getRedeemedTo();
					if(redeemVal != null) {
						
						if(redeemVal.startsWith("Customer Id")) {
							
							int pos = redeemVal.indexOf(";");
							if(pos == -1) {
								redeemVal = "";
							}else {
								redeemVal = redeemVal.substring(++pos);
							}
							
						}  else if(redeemVal.endsWith(";")) {
							
							redeemVal="";
						}
					}
					logger.info("redeemValvalue:: "+redeemVal);
					row.appendChild(new Label(redeemVal  != null? redeemVal : ""));	
					
					//Membership #
					row.appendChild(new Label(coupCodesObj.getMembership()!= null? coupCodesObj.getMembership() : ""));
					// campaignName
					row.appendChild(new Label(coupCodesObj.getCampaignName()));

					// issued On
					row.appendChild(new Label(MyCalendar.calendarToString(
							coupCodesObj.getIssuedOn(),
							MyCalendar.FORMAT_DATETIME_STDATE_PROMO , clientTimeZone)));
					// Redeemed On
					row.appendChild(new Label(MyCalendar.calendarToString(
							coupCodesObj.getRedeemedOn(),
							MyCalendar.FORMAT_DATETIME_STDATE_PROMO , clientTimeZone)));
					// Store
					row.appendChild(new Label(coupCodesObj.getStoreNumber() != null ? fetchStoreName(coupCodesObj.getStoreNumber()):""));
					
					// DocsId
					//row.appendChild(new Label(coupCodesObj.getStoreNumber() != null ? coupCodesObj.getDocSid()+"":""));
					
					//ReceiptNumber
				        row.appendChild(new Label(coupCodesObj.getStoreNumber() != null ?(coupCodesObj.getReceiptNumber() != null ? coupCodesObj.getReceiptNumber()+"": "--"):""));

				
				        //count	
					 if(coupCodesObj.getDocSid()!= null && !coupCodesObj.getDocSid().isEmpty())
					{	
						int count = couponCodesDao.getCountByDocsId(coupCodesObj.getCouponId().getCouponId(),coupCodesObj.getCouponCode(),coupCodesObj.getDocSid());
						row.appendChild(new Label(count!=0 ? count+"":""));
					}else {
						row.appendChild(new Label(""));
					}
					 
					// Rewards redeemed if any 
					 //row.appendChild(new Label( coupCodesObj.getUsedLoyaltyPoints() != null ? ""+coupCodesObj.getUsedLoyaltyPoints().intValue() : "0"));
					 row.appendChild(new Label(coupCodesObj.getUsedLoyaltyPoints() != null ? ""+coupCodesObj.getUsedLoyaltyPoints().intValue()+" "+
							 (coupCodesObj.getValueCode()!=null ?coupCodesObj.getValueCode() :"") : "0"));
					
					// disc
					row.appendChild(new Label( coupCodesObj.getTotDiscount() != null ?""+ decimalFormat.format(coupCodesObj.getTotDiscount()) : ""));
					// total discount
					row.appendChild(new Label( coupCodesObj.getTotDiscount() != null ?""+ decimalFormat.format(coupCodesObj.getTotDiscount()) : ""));
					// total Revenue
					row.appendChild(new Label( coupCodesObj.getTotRevenue() != null ? ""+decimalFormat.format(coupCodesObj.getTotRevenue()) : ""));
			       
					//Item Info
					row.appendChild(new Label("--"));
					//row.appendChild(new Label( coupCodesObj.getUsedLoyaltyPoints() != null ? ""+coupCodesObj.getUsedLoyaltyPoints().intValue() : "0"));

					row.setValue(coupCodesObj);
					row.setParent(couponsRowsId);
					
					size+=1;
				}
				}
				logger.info("size after item info split "+size);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
		}

	} // getCoupons
	
	private void setSizeOfPageAndFillPromoDetailsGrid(int promoListSize) {
    	

    	logger.debug("promoListSize is    "+promoListSize);
    	
    	
    	promoDetailPagingId.setTotalSize(promoListSize);
    	promoDetailPagingId.setActivePage(0);
    	
    	
    	String selectStr = memberPerPagePromoDetailLBId.getSelectedItem().getLabel();
		int pNo = Integer.parseInt(selectStr);
		
		fillPromoRedemptionDetail(0, pNo, coupObj.getCouponId());
		
    }

 private String fetchStoreName(String store_number){
		 
		 for (Map<String, Object> eachMap: storeNumberNameMapList) {

			 if(eachMap.get("home_store_id").toString().equals(store_number)){
				 if(eachMap.get("store_name") != null){
					 return eachMap.get("store_name").toString();
				 }else{
					 return "Store ID "+store_number;
				 }
			 }
		 }
		 
		 return "Store ID "+store_number;
	}
	
	private int fillPromoRedemptionDetail(int fromInt, int lbxLenght, Long couponId) {

		try {
			Components.removeAllChildren(promotionRedmDetailRowsId);
			int size;
			
			/*int count =  promoCodeRowsId.getItemCount();
			
			for(; count>0; count--) {
				promoCodeRowsId.removeAt(count-1);
			}*/
			
			List<Map<String, Object>> promoObjArrList  = couponCodesDao.findAllStoresRedemptionList(GetUser.getUserObj().getUserOrganization().getUserOrgId(), fromInt, lbxLenght, couponId+"",Constants.STRING_NILL,Constants.STRING_NILL);// sm change
			
			if(promoObjArrList == null || promoObjArrList.size() == 0 ) {
				 
				 logger.info(" *** No promo code data exists for this user");
				 return 0;
			 }
			
			 logger.info(">>> PromoObjArrList size is :"+promoObjArrList.size());
			 size = promoObjArrList.size();
			 Row tempRow = null;
			 String storeName;
			 DecimalFormat decimalFormat = new DecimalFormat("#0.00");
			 for (Map<String, Object> eachMap: promoObjArrList) {
				 
				 tempRow  = new Row();
				 //SELECT promo_code, ROUND(SUM((quantity*sales_price)+tax),2) AS REVENUE , COUNT(customer_id) AS COUNT
				 //PromoCode
				 Label PromoCodeLbl = new Label();
				 if(eachMap.containsKey("store_number") && eachMap.get("store_number") != null) {
					// PromoCodeLbl.setValue(eachMap.get("store_number").toString());
					 storeName = fetchStoreName(eachMap.get("store_number").toString());
					 //PromoCodeLbl.setValue(eachMap.get("store_number").toString());
					 PromoCodeLbl.setValue(storeName);
				 }
				 PromoCodeLbl.setParent(tempRow);
				
				 //Revenue
				 Label revenueLbl = new Label();
				 if(eachMap.containsKey("REVENUE") && eachMap.get("REVENUE") != null) {
					 revenueLbl.setValue(decimalFormat.format(eachMap.get("REVENUE")));
				 }
				 revenueLbl.setParent(tempRow);
				 
				 //No. of customer
				 Label totalCustmerLbl = new Label();
				 if(eachMap.containsKey("COUNT") && eachMap.get("COUNT") != null) {
					 
					 totalCustmerLbl.setValue(eachMap.get("COUNT").toString());
				 }
				 totalCustmerLbl.setParent(tempRow);
				 
				 tempRow.setParent(promotionRedmDetailRowsId);
			}
			 
			return size;  
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
			return 0;
		}
	} //fillPromoRedemptionDetail
	public void onClick$exportBtnStoreId(){


		logger.debug("-- just entered --");
		//int count = getCount();
		//int count = recordSize;
		int size = 1000;
		String searchStr = "";
		/*
		if(!suppLstSearchTbId.getValue().equals("") && !suppLstSearchTbId.getValue().equalsIgnoreCase("Search Email Address")) {
			
			searchStr = suppLstSearchTbId.getValue();
			
		}*/

		TimeZone tz =(TimeZone)Sessions.getCurrent().getAttribute("clientTimeZone"); 
		//String type = (String)supptypeLbId.getSelectedItem().getValue();
		//String fileType = exportFilterLbId.getSelectedItem().getLabel();
		String fileType = "csv";
		//String category = (String)supptypeLbId.getSelectedItem().getLabel();
		String userName = GetUser.getUserName();
		String usersParentDirectory = (String)PropertyUtil.getPropertyValue("usersParentDirectory");
		String exportDir = usersParentDirectory + "/" + userName + "/Coupon/" ;
		File downloadDir = new File(exportDir);
		JdbcResultsetHandler jdbcResultsetHandler = null;
		BufferedWriter bw = null;
		
		if(downloadDir.exists()){
			try {
				FileUtils.deleteDirectory(downloadDir);
				logger.debug(downloadDir.getName() + " is deleted");
			} catch (Exception e) {
				logger.error("Exception");
				logger.warn(downloadDir.getName() + " is not deleted");
			}
		}
		if(!downloadDir.exists()){
			downloadDir.mkdirs();
		}
		
		String filePath = "";
		StringBuffer sb = null;

		logger.debug("Writing to the file : " + filePath);

		
		if(fileType.contains("csv")){
			try {
								
				filePath = exportDir +  "StoreRedemption"+ "_" + System.currentTimeMillis() + ".csv";
				
				logger.debug("Download File path : " + filePath);
				File file = new File(filePath);
				bw = new BufferedWriter(new FileWriter(filePath));
				//bw.write("\"Email Address\",\"Reason\" \r\n");
				sb = new StringBuffer();

				

				sb.append("\"Discount Code\",\"Revenue\",\"No. Of Redemptions\"");
				sb.append("\r\n");
				bw.write(sb.toString());

				String qry = "SELECT c.coupon_name AS name, ROUND(SUM(cc.tot_revenue),2) AS REVENUE, COUNT(cc.coupon_code_id) as COUNT " +
				"FROM coupon_codes cc left outer join coupons c on cc.coupon_id=c.coupon_id WHERE orgId ="+users.getUserOrganization().getUserOrgId()+
						" AND store_number IS NOT NULL AND cc.coupon_code IS NOT NULL AND cc.store_number = '"+store+"' AND cc.redeemed_on between '"+storeFromDateStr+"' AND '"+storeToDateStr+"'" +
						"GROUP BY cc.coupon_id ORDER BY redeemed_on DESC";
			
				jdbcResultsetHandler = new JdbcResultsetHandler();
				jdbcResultsetHandler.executeStmt(qry);
				
				OCCSVWriter csvWriter = new OCCSVWriter(bw);
				try {
					csvWriter = new OCCSVWriter(bw);
					csvWriter.writeAll(jdbcResultsetHandler.getResultSet(), false);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					logger.error("exception while initiating writer ",e);
					
				}finally{
					bw.flush();
					csvWriter.flush();
					bw.close();
					csvWriter.close();
					csvWriter =null;
				}
				
				
				
				Filedownload.save(file, "text/csv");
			} catch (Exception e) {
				logger.info("Exception :: ",e);
			}finally{
				if(jdbcResultsetHandler!=null ) jdbcResultsetHandler.destroy();
				;userName = null;usersParentDirectory = null;exportDir = null;downloadDir = null;
				filePath = null;bw=null;
				jdbcResultsetHandler = null;jdbcResultsetHandler = null;
				//System.gc();
			}
			logger.debug("-- exit --");
		}
				
    }
	
	public void onSelect$pageSizeLbId() {
		int tempCount = Integer.parseInt(pageSizeLbId.getSelectedItem()
				.getLabel());
		couponListBottomPagingId.setPageSize(tempCount);
		couponListBottomPagingId.setActivePage(0);
		getCouponCodes(0, tempCount,getFilterData());

	} // onSelect$pageSizeLbId
	
public void onSelect$memberPerPagePromoDetailLBId() {
		
		try {
			logger.info("just enter here");
			
			
			String selectStr = memberPerPagePromoDetailLBId.getSelectedItem().getLabel();
			int pNo = Integer.parseInt(selectStr);
			
			promoDetailPagingId.setPageSize(pNo);
			promoDetailPagingId.setActivePage(0);	
			fillPromoRedemptionDetail(0,pNo,coupObj.getCouponId());
			
		} catch (NumberFormatException e) {
			logger.error("Exception ::" , e);
		}
	} // onClick$memberPerPageLBId
	public void onClick$backBtnId(){
		
//		session.removeAttribute("COUP_REDEEMED_DETAILS");
//		Redirect.goToPreviousPage();
		Redirect.goTo(PageListEnum.REPORT_COUPON_REPORTS);
		
		
	}
	
	public void onEvent(Event event) throws Exception {
		// TODO Auto-generated method stub
		super.onEvent(event);

		//onSelect$pageSizeLbId();

		if (event.getTarget() instanceof Paging) {
			Paging paging = (Paging) event.getTarget();
			
			int desiredPage = paging.getActivePage();
			
			PagingEvent pagingEvent = (PagingEvent) event;
			int pSize = pagingEvent.getPageable().getPageSize();
			int ofs = desiredPage * pSize;
			if(paging.getAttribute("onPaging").equals("couponDetail")){
			Map<String, String> searchData = new HashMap<String, String>();
			if(StoreNameId.isVisible()) {
				if( searchByStoreNameId.getSelectedIndex() == 0) {
					StringBuilder storeIdBuilder = new StringBuilder();
					 List<Listitem> storeIdes = searchByStoreNameId.getItems();
					 for(Listitem store:storeIdes) {
						 if(!store.getValue().equals("All")) {
							 storeIdBuilder.append(store.getValue().toString());
							 storeIdBuilder.append(SEPARATOR);
						 }
					 }
					 String csv = storeIdBuilder.toString();
					 csv = storeIdBuilder.substring(0, storeIdBuilder.length() - ",".length());
					searchData.put("storeName_all", csv);
				}else {
					searchData.put("storeName", searchByStoreNameId.getSelectedItem().getValue());
				}
			}else if(RedeemedDateId.isVisible() && fromRedeemedDateboxId.getText()!=null && !fromRedeemedDateboxId.getText().isEmpty()
					&& toRedeemedDateboxId.getText()!=null && !toRedeemedDateboxId.getText().isEmpty()) {
				StringBuilder redeemedDate = new StringBuilder();
				String fromDate = MyCalendar.calendarToString(getStartDate(fromRedeemedDateboxId), MyCalendar.FORMAT_DATETIME_STYEAR);
				String toDate = MyCalendar.calendarToString(getEndDate(toRedeemedDateboxId), MyCalendar.FORMAT_DATETIME_STYEAR);
				searchData.put("RedeemedDate", redeemedDate.append(fromDate).append("T").append(toDate).toString());
			}else if(IssuedDateId.isVisible() && fromIssuedDateboxId.getText()!=null && !fromIssuedDateboxId.getText().isEmpty() && toIssuedDateboxId.getText()!=null && !toIssuedDateboxId.getText().isEmpty()) {
				StringBuilder issuedDate = new StringBuilder();
				String fromDate = MyCalendar.calendarToString(getStartDate(fromIssuedDateboxId), MyCalendar.FORMAT_DATETIME_STYEAR);
				String toDate = MyCalendar.calendarToString(getEndDate(toIssuedDateboxId), MyCalendar.FORMAT_DATETIME_STYEAR);
				searchData.put("IssuedDate", issuedDate.append(fromDate).append("T").append(toDate).toString());
			}else if(statusId.isVisible() && filterByStatusId.getSelectedItem()!=null && filterByStatusId.getSelectedItem().getValue()!=null) {
				searchData.put("promotionStatus", filterByStatusId.getSelectedItem().getValue());
			}
			getCouponCodes(ofs, (byte) pagingEvent.getPageable().getPageSize(),searchData);
			}else if(paging.getAttribute("onPaging").equals("storeDetail")){
				fillPromoRedemptionDetail(ofs, (byte) pagingEvent.getPageable().getPageSize(), coupObj.getCouponId());
			}else if(paging.getAttribute("onPaging").equals("store")){
				this.storePagingId.setActivePage(desiredPage);
				fillStoreRedemption(ofs, pSize);
			}
		}
	} // onEvent
	private Combobox exportCbId;
	public void onClick$exportBtnId() {
		if (exportCbId.getSelectedItem().getValue().equals("csv")) {
			exportCsv(exportCbId);
		} else if (exportCbId.getSelectedItem().getValue().equals("xls")) {
			exportExcel(exportCbId);
		}
	}
	public void exportCsv(Combobox exportCbId){
		
			long startTime = System.currentTimeMillis();
			POSMappingDao posMappingDao = null;
			String type = Constants.STRING_NILL;
			String userName = Constants.STRING_NILL;
			String usersParentDirectory = Constants.STRING_NILL;
			String exportDir = Constants.STRING_NILL;
			File downloadDir = null;
			String name = Constants.STRING_NILL;
			String filePath = Constants.STRING_NILL;
			File file = null;
			BufferedWriter bw = null;
			List<POSMapping> posMappingsList = null;
			Map<String, POSMapping> orderedMappingMap = null;
			String udfFldsLabel = Constants.STRING_NILL;
			String contactFieldsInDB = Constants.STRING_NILL;
			JdbcResultsetHandler jdbcResultsetHandler = null;
			JdbcResultsetHandler exportJdbcResultsetHandler = null;
		try {
			logger.debug("-- just entered --");
			
			if(couponsRowsId.getChildren().size() == 0) {
				MessageUtil.setMessage("No reports existed ","color:red","TOP");
				return;
			}
			
			 posMappingDao = (POSMappingDao)SpringUtil.getBean("posMappingDao");
			Long currUserId = GetUser.getUserId();
			
			type = exportCbId.getSelectedItem().getLabel();
			userName = GetUser.getUserName();
			usersParentDirectory = (String)PropertyUtil.getPropertyValue("usersParentDirectory");
			exportDir = usersParentDirectory + "/" + userName + "/Coupon/" ;
			downloadDir = new File(exportDir);
			
			if(downloadDir.exists()){
				try {
					FileUtils.deleteDirectory(downloadDir);
					logger.debug(downloadDir.getName() + " is deleted");
				} catch (Exception e) {
					logger.error("Exception ::" , e);
					logger.debug(downloadDir.getName() + " is not deleted");
				}
			}
			if(!downloadDir.exists()){
				downloadDir.mkdirs();
			}
			
			if(type.contains("csv")){
				
				long coupId = coupObj.getCouponId();
				//CouponCodes couponCodes = couponCodesDao.findById(coupId);
				name = coupObj.getCouponName();
				if(name.contains("/")) {
					
					name = name.replace("/", "_") ;
					
				}
					filePath = exportDir +  "Coupon_" + name + "_" +
					MyCalendar.calendarToString(coupObj.getCouponCreatedDate(), MyCalendar.FORMAT_YEARTOSEC, clientTimeZone);
				
					filePath = filePath + "_DiscountCodes.csv";
					logger.debug("Download File path : " + filePath);
					file = new File(filePath);
					try{
					bw = new BufferedWriter(new FileWriter(filePath));
					}catch (FileNotFoundException e) {
						filePath = exportDir +  "Coupon_" + System.currentTimeMillis()+"_" +
								MyCalendar.calendarToString(coupObj.getCouponCreatedDate(), MyCalendar.FORMAT_YEARTOSEC, clientTimeZone);
							
								filePath = filePath + "_DiscountCodes.csv";
								file = new File(filePath);
						bw = new BufferedWriter(new FileWriter(filePath));
						// TODO: handle exception
					}				
					posMappingsList = posMappingDao.findByType("'"+Constants.POS_MAPPING_TYPE_CONTACTS+"'", currUserId);
					
					//logger.debug("POS Mapping List :"+posMappingsList);
					
					orderedMappingMap = getOrderedMappingSet(posMappingsList);
					
					//logger.debug("map ::"+orderedMappingMap);
					
					for (String custFldKey : orderedMappingMap.keySet()) {
						
						if(custFldKey.contentEquals(POSFieldsEnum.customerId.getOcAttr())) {
						
							continue;
						}
						if(udfFldsLabel.length() > 0) udfFldsLabel += ",";
						udfFldsLabel += "\""+orderedMappingMap.get(custFldKey).getDisplayLabel().trim()+"\"";
						contactFieldsInDB +=","+genFieldContMap.get(custFldKey).trim();
					
						
					}//for
					
					//bw.write("\"Promo-code\",\"Status\",\"Issued to\",\"Redeemed by\",\"Membership #\",\"Campaign Name\",\"Issued On\",\"Redeemed On\","
						//	+ "\"Store\",\"Subsidiary\",\"Docsid\",\"Receipt#\",\"Total Discount\",\"Total Revenue\",\"Rewards Redeemed\",\"Item Info\","+udfFldsLabel+" \r\n");//1178
					bw.write("\"Discount code\",\"Status\",\"Issued to\",\"Redeemed by\",\"Membership #\",\"Campaign Name\",\"Issued On\",\"Redeemed On\","
							+ "\"Store\",\"Subsidiary\",\"Receipt#\",\"Discount On Item\",\"Total Discount\",\"Total Revenue\",\"Rewards Redeemed if any\",\"Item Info\","
							+ ""+udfFldsLabel+" \r\n");
					
					//String query = "select cc.coupon_code,cc.status,cc.issued_to,cc.redeemed_to,cc.campaign_name,cc.issued_on,cc.redeemed_on,cc.store_number,cc.doc_sid,cc.tot_discount,cc.tot_revenue"+contactFieldsInDB+" from coupon_codes cc left join contacts c on cc.contact_id = c.cid where coupon_id="+coupId+" AND status NOT IN('"
					//		+ Constants.COUP_CODE_STATUS_INVENTORY + "') ORDER BY cc.issued_on DESC";
					
					Map<String,String> searchData = getFilterData();
					String searchQuery = "";
					String query = "";
					boolean statusFlag = false;
					if(searchData.containsKey("storeName")) {
						String storeId = searchData.get("storeName");
						searchQuery = "AND store_number = "+storeId+"";
					}else if (searchData.containsKey("RedeemedDate")) {
						String[] date = searchData.get("RedeemedDate").split("T");
						searchQuery = "AND redeemed_on BETWEEN '"+date[0]+"' AND '"+date[1]+"'";
					}else if (searchData.containsKey("IssuedDate")) {
						String[] date = searchData.get("IssuedDate").split("T");
						searchQuery = "AND issued_on BETWEEN '"+date[0]+"' AND '"+date[1]+"'";
					}else if (searchData.containsKey("promotionStatus")) {
						String promotionStatus = searchData.get("promotionStatus");
						statusFlag = true;
						if(promotionStatus.equalsIgnoreCase("Redeemed")) {
							query = " AND status IN('" + Constants.COUP_CODE_STATUS_REDEEMED + "') ";
						}else if(promotionStatus.equalsIgnoreCase("Active")) {
							query = " AND status IN('" + Constants.COUP_CODE_STATUS_ACTIVE + "','"+ Constants.COUP_CODE_STATUS_REDEEMED +"','"+ Constants.COUP_CODE_STATUS_EXPIRED +"') AND issued_to IS NOT NULL";
						}else if(promotionStatus.equalsIgnoreCase("Inventory")) {
							query = " AND status IN('" + Constants.COUP_CODE_STATUS_INVENTORY + "')";
						}else if(promotionStatus.equalsIgnoreCase("Expired")) {
							query = " AND status IN('" + Constants.COUP_CODE_STATUS_EXPIRED + "') ";
						}else if(promotionStatus.equalsIgnoreCase("All")) {
							query = "";
						}
					/*	query = "select cc.coupon_code,cc.status,cc.issued_to,cc.redeemed_to,cc.membership,cc.campaign_name,cc.issued_on,cc.redeemed_on,cc.store_number,cc.subsidiary_number,cc.receipt_number,cc.tot_discount,cc.tot_revenue,"
								+ "cc.used_loyalty_points,cc.value_code,cc.item_info"+contactFieldsInDB+" "
								+ "from coupon_codes cc left join (select * from contacts where user_id="+users.getUserId()+"  group by external_id) c on (cc.redeem_cust_id = c.external_id   )  "
								+ "where coupon_id="+coupId+" "+query+" order by cc.redeemed_on desc";*/
					
					//old query commented bcz of taking time
					/*	query = "select cc.coupon_code,cc.status,cc.issued_to,cc.redeemed_to,cc.membership,cc.campaign_name,cc.issued_on,cc.redeemed_on,cc.store_number,cc.subsidiary_number,cc.receipt_number,cc.tot_discount,cc.tot_revenue,"
								+ "cc.used_loyalty_points,cc.value_code,cc.item_info"+contactFieldsInDB+" "
								+ "from coupon_codes cc left join (select * from contacts c where user_id="+users.getUserId()+") c on (if(cc.redeem_cust_id IS NULL AND cc.redeem_phn_id IS NOT NULL,cc.redeem_phn_id=c.mobile_phone,cc.redeem_cust_id = c.external_id)) "
								+ "where cc.orgId="+users.getUserOrganization().getUserOrgId()+" AND coupon_id="+coupId+" "+query+" order by cc.redeemed_on desc"; */
					
						logger.info(" filters exported");
						logger.info(" filters exported searchQuery"+searchQuery);

					
						String firstPartQuery = "SELECT "
							    + "cc.coupon_code, cc.status, cc.issued_to, cc.redeemed_to, cc.membership, cc.campaign_name, "
							    + "cc.issued_on, cc.redeemed_on, cc.store_number, cc.subsidiary_number, cc.receipt_number, "
							    + "cc.tot_discount, cc.tot_revenue, cc.used_loyalty_points, cc.value_code, cc.item_info"
							    + contactFieldsInDB
							    + " FROM coupon_codes cc "
							    + "LEFT JOIN (SELECT * FROM contacts WHERE user_id=" + users.getUserId() + ") c "
							    + "ON (cc.redeem_phn_id = c.mobile_phone) "
							    + "WHERE cc.orgId=" + users.getUserOrganization().getUserOrgId() + " "
							    + "AND coupon_id=" + coupId + " "
							    + query
							    + " ";

							String secondPartQuery = "SELECT "
							    + "cc.coupon_code, cc.status, cc.issued_to, cc.redeemed_to, cc.membership, cc.campaign_name, "
							    + "cc.issued_on, cc.redeemed_on, cc.store_number, cc.subsidiary_number, cc.receipt_number, "
							    + "cc.tot_discount, cc.tot_revenue, cc.used_loyalty_points, cc.value_code, cc.item_info"
							    + contactFieldsInDB
							    + " FROM coupon_codes cc "
							    + "LEFT JOIN (SELECT * FROM contacts WHERE user_id=" + users.getUserId() + ") c "
							    + "ON (cc.redeem_cust_id = c.external_id) "
							    + "WHERE cc.orgId=" + users.getUserOrganization().getUserOrgId() + " "
							    + "AND coupon_id=" + coupId + " "
							    + query
							    + " ";

							query = firstPartQuery + " UNION " + secondPartQuery + " ORDER BY redeemed_on DESC LIMIT 0,20000";
							
							logger.info("final query for export is :: "+query);
					}
					
					if(searchQuery!=null && !searchQuery.isEmpty() && !statusFlag) {
						
				/*		query = "select cc.coupon_code,cc.status,cc.issued_to,cc.redeemed_to,cc.membership,cc.campaign_name,cc.issued_on,cc.redeemed_on,cc.store_number,cc.subsidiary_number,cc.receipt_number,cc.tot_discount,cc.tot_revenue,"
								+ "cc.used_loyalty_points,cc.value_code,cc.item_info"+contactFieldsInDB+" "
								+ "from coupon_codes cc left join (select * from contacts where user_id="+users.getUserId()+"  group by external_id) c on (cc.redeem_cust_id = c.external_id) where coupon_id="+coupId+""
							+ "  "+searchQuery+" AND status NOT IN('"+ Constants.COUP_CODE_STATUS_INVENTORY + "')  order by cc.redeemed_on desc";*/
				
						
					/*	query = "select cc.coupon_code,cc.status,cc.issued_to,cc.redeemed_to,cc.membership,cc.campaign_name,cc.issued_on,cc.redeemed_on,cc.store_number,cc.subsidiary_number,cc.receipt_number,cc.tot_discount,cc.tot_revenue,"
								+ "cc.used_loyalty_points,cc.value_code,cc.item_info"+contactFieldsInDB+" "
								+ "from coupon_codes cc left join (select * from contacts where user_id="+users.getUserId()+")  c on (if(cc.redeem_cust_id IS NULL AND cc.redeem_phn_id IS NOT NULL,cc.redeem_phn_id=c.mobile_phone,cc.redeem_cust_id = c.external_id))  where  cc.orgId="+users.getUserOrganization().getUserOrgId()+" AND coupon_id="+coupId+"  "
							+ "  "+searchQuery+" AND status NOT IN('"+ Constants.COUP_CODE_STATUS_INVENTORY + "')  order by cc.redeemed_on desc";*/
				
						
						String firstPartQuery = "SELECT "
							    + "cc.coupon_code, cc.status, cc.issued_to, cc.redeemed_to, cc.membership, cc.campaign_name, "
							    + "cc.issued_on, cc.redeemed_on, cc.store_number, cc.subsidiary_number, cc.receipt_number, "
							    + "cc.tot_discount, cc.tot_revenue, cc.used_loyalty_points, cc.value_code, cc.item_info"
							    + contactFieldsInDB
							    + " FROM coupon_codes cc "
							    + "LEFT JOIN (SELECT * FROM contacts WHERE user_id=" + users.getUserId() + ") c "
							    + "ON (cc.redeem_phn_id = c.mobile_phone) "
							    + "WHERE cc.orgId=" + users.getUserOrganization().getUserOrgId() + " "
							    + "AND coupon_id=" + coupId + " "
							    + searchQuery
							    + " AND status NOT IN('" + Constants.COUP_CODE_STATUS_INVENTORY + "')";

							String secondPartQuery = "SELECT "
							    + "cc.coupon_code, cc.status, cc.issued_to, cc.redeemed_to, cc.membership, cc.campaign_name, "
							    + "cc.issued_on, cc.redeemed_on, cc.store_number, cc.subsidiary_number, cc.receipt_number, "
							    + "cc.tot_discount, cc.tot_revenue, cc.used_loyalty_points, cc.value_code, cc.item_info"
							    + contactFieldsInDB
							    + " FROM coupon_codes cc "
							    + "LEFT JOIN (SELECT * FROM contacts WHERE user_id=" + users.getUserId() + ") c "
							    + "ON (cc.redeem_cust_id = c.external_id) "
							    + "WHERE cc.orgId=" + users.getUserOrganization().getUserOrgId() + " "
							    + "AND coupon_id=" + coupId + " "
							    + searchQuery
							    + " AND status NOT IN('" + Constants.COUP_CODE_STATUS_INVENTORY + "')";

							 	query = firstPartQuery + " UNION " + secondPartQuery + " ORDER BY redeemed_on DESC LIMIT 0,20000";

						
						logger.info("final query for export with searchquery is :: "+query);

						logger.info("default settings exported");
					
					
					}
					
					//String query11 ="select cc1.* from coupon_codes cc1 join (select coupon_code_id,doc_sid, max(redeemed_on) as max_redeemed_on from coupon_codes where coupon_id="+coupId+" and status not in ('"+Constants.COUP_CODE_STATUS_INVENTORY+"') group by doc_sid ) cc2 on cc1.doc_sid=cc2.doc_sid where cc1.coupon_id="+coupId+" and cc1.status not in ('"+Constants.COUP_CODE_STATUS_INVENTORY+"') and cc2.max_redeemed_on=cc1.redeemed_on Group by cc1.doc_sid,cc1.redeemed_on";
					
					/*** Group by Doc_sid 
					 ***/	
					//String query_working_fine ="select cc1.coupon_code,cc1.status,cc1.issued_to,cc1.redeemed_to,cc1.campaign_name,cc1.issued_on,cc1.redeemed_on,cc1.store_number,cc1.doc_sid,cc1.tot_discount,cc1.tot_revenue"+contactFieldsInDB+" from coupon_codes cc1 join (select coupon_code_id,doc_sid, max(redeemed_on) as max_redeemed_on from coupon_codes where coupon_id="+coupId+" and status not in ('"+Constants.COUP_CODE_STATUS_INVENTORY+"') group by doc_sid ) cc2 on cc1.doc_sid=cc2.doc_sid left join contacts c on cc1.contact_id = c.cid where cc1.coupon_id="+coupId+" and cc1.status not in ('"+Constants.COUP_CODE_STATUS_INVENTORY+"') and cc2.max_redeemed_on=cc1.redeemed_on Group by cc1.doc_sid,cc1.redeemed_on";

					//String exportQuery = "select c.id,c.mobile_status,c.email_status,c.subscription_type,c.cf_value,c.domain,c.categories,c.optin_medium,c.home_phone,c.doc_sid,c.discount,c.tot_revenue,c.loyalty_used_points,c.item_info,c.membership,c.subsidiary_number"+contactFieldsInDB+" from tempexportreport c where 1=2";
					
					//harshi
					String exportQuery = "select c.id,c.mobile_status,c.email_status,c.subscription_type,c.cf_value,c.membership,c.domain,c.categories,c.optin_medium,c.home_phone,c.subsidiary_number,c.doc_sid,c.discountOnItem,c.discount,c.tot_revenue,c.loyalty_used_points,c.item_info"+contactFieldsInDB+" from tempexportreport c where 1=2";
					
					exportQuery = exportQuery.replaceFirst("c.birth_day", "c.urls");
					exportQuery = exportQuery.replaceFirst("c.anniversary_day", "c.created_date");
					
					logger.info("query is :: "+query);
					logger.info("exportQuery is :: "+exportQuery);
					jdbcResultsetHandler = new JdbcResultsetHandler();
					jdbcResultsetHandler.executeStmt(query);
					
					exportJdbcResultsetHandler = new JdbcResultsetHandler();
					exportJdbcResultsetHandler.executeStmt(exportQuery, true);
					
					ResultSet exportResultSet, resultSet;
					resultSet = jdbcResultsetHandler.getResultSet();
					exportResultSet = exportJdbcResultsetHandler.getResultSet();
					DecimalFormat decimalFormat = new DecimalFormat("#0.00");
					double random = 0;
					contactFieldsInDB = contactFieldsInDB.substring(1);
					Calendar cal = Calendar.getInstance();
					while(resultSet.next()){
						logger.info("CouponsController.exportCsv"+resultSet);
						String itemInfo = resultSet.getString("item_info");
						logger.info("item info from resultset "+itemInfo);
						if(itemInfo!=null) {
							String[] itemInfosArr =null;
							double totdisc=0.0;
							if(itemInfo.contains(""+Constants.DELIMITER_PIPE)){
								itemInfosArr = itemInfo.split("\\"+Constants.DELIMITER_PIPE);
								for(String eachitem : itemInfosArr) {
									String[] discAmt = eachitem.split(""+(eachitem.startsWith("P:")?Constants.DELIMETER_COMMA:Constants.DELIMETER_COLON));
									totdisc += (discAmt.length>=3?(Double.parseDouble(discAmt[2]) * Double.parseDouble(discAmt[1])):(Double.parseDouble(discAmt[1])));
								}
								for (String info : itemInfosArr) {
									random = Math.random();
									exportResultSet.moveToInsertRow();
									exportResultSet.updateString("id", users.getUserId()+""+random+""+System.currentTimeMillis());
									for (String custFldkey : contactFieldsInDB.split(",")) {
										custFldkey = custFldkey.replaceFirst("c.", "");
										if(custFldkey.equals("birth_day")){
											try{
												cal = Calendar.getInstance();
												cal.setTimeInMillis(resultSet.getTimestamp(custFldkey).getTime());
												}catch(NullPointerException e){
													cal =null;
												}
											exportResultSet.updateString("urls", resultSet.getTimestamp(custFldkey)!=null?MyCalendar.calendarToString(cal,MyCalendar.FORMAT_DATETIME_STDATE):null);
										}else if(custFldkey.equals("anniversary_day")){
											exportResultSet.updateTimestamp("created_date", resultSet.getTimestamp(custFldkey)!=null?resultSet.getTimestamp(custFldkey):null);
										}
									else{
										exportResultSet.updateString(custFldkey, resultSet.getString(custFldkey));
									}
									}
									/*String query = "select cc.coupon_code,cc.status,cc.issued_to,cc.redeemed_to,cc.campaign_name,cc.issued_on,cc.redeemed_on,cc.store_number,cc.tot_discount,cc.tot_revenue"+contactFieldsInDB+" from coupon_codes cc left join contacts c on cc.contact_id = c.cid where coupon_id="+coupId+" AND status NOT IN('"
											+ Constants.COUP_CODE_STATUS_INVENTORY + "') ORDER BY cc.issued_on DESC";
									String exportQuery = "select c.id,c.mobile_status,c.user_id,c.subscription_type,c.cf_value,c.domain,c.categories,c.optin_medium,,c.hp_id,c.email_status,home_phone"+contactFieldsInDB+" from tempexportreport c where 1=2";
									MyCalendar.calendarToString(
								coupCodesObj.getIssuedOn(),
								MyCalendar.FORMAT_DATETIME_STDATE , clientTimeZone)*/
									exportResultSet.updateString("mobile_status", resultSet.getString("coupon_code"));
									if(resultSet.getString("status")!=null && resultSet.getString("status").equalsIgnoreCase(Constants.COUP_CODE_STATUS_ACTIVE)) {
										exportResultSet.updateString("email_status", "Issued");
									}else if(resultSet.getString("status")!=null && resultSet.getString("status").equalsIgnoreCase(Constants.COUP_CODE_STATUS_INVENTORY)){
										exportResultSet.updateString("email_status", "Not Issued");
									}else {
										exportResultSet.updateString("email_status", resultSet.getString("status"));
									}
									exportResultSet.updateString("subscription_type", resultSet.getString("issued_to"));
									exportResultSet.updateString("cf_value", resultSet.getString("redeemed_to"));
									exportResultSet.updateString("domain", resultSet.getString("campaign_name"));
									
									try{
										cal = Calendar.getInstance();
									cal.setTimeInMillis(resultSet.getTimestamp("issued_on").getTime());
									}catch(NullPointerException e){
										cal =null;
									}
									exportResultSet.updateString("categories", MyCalendar.calendarToString(cal ,MyCalendar.FORMAT_DATETIME_STDATE , clientTimeZone));
									try{
										cal = Calendar.getInstance();
										cal.setTimeInMillis(resultSet.getTimestamp("redeemed_on").getTime());
									}catch(NullPointerException e){
										cal =null;
									}
									exportResultSet.updateString("optin_medium", MyCalendar.calendarToString(cal ,MyCalendar.FORMAT_DATETIME_STDATE , clientTimeZone));
									exportResultSet.updateString("home_phone",resultSet.getString("store_number") !=null? fetchStoreName(resultSet.getString("store_number")):"");
									
							    	//exportResultSet.updateString("doc_sid", resultSet.getString("doc_sid"));
									exportResultSet.updateString("doc_sid", resultSet.getString("receipt_number"));
									/*String receiptNo=resultSet.getString("receipt_number");
									RetailProSalesCSV rProSalesObj=null;
									if(receiptNo==null){
										rProSalesObj=retailProsalesDao.findReceiptNumberByDocsid(resultSet.getString("doc_sid"), coupObj.getUserId());
									}
									exportResultSet.updateString("doc_sid",receiptNo!=null?receiptNo:(rProSalesObj!=null && rProSalesObj.getRecieptNumber()!=null)?rProSalesObj.getRecieptNumber().toString():"" );
									*/
									exportResultSet.updateDouble("discount", totdisc);
									
									exportResultSet.updateString("tot_revenue", (Double)resultSet.getDouble("tot_revenue")!=null?(((Double.parseDouble(decimalFormat.format((Double)resultSet.getDouble("tot_revenue")))+"").equals("0.0"))?"null":Double.parseDouble(decimalFormat.format((Double)resultSet.getDouble("tot_revenue")))+""):"null");
									
									exportResultSet.updateString("loyalty_used_points", (Double)resultSet.getDouble("used_loyalty_points")!=null?
											((Double)resultSet.getDouble("used_loyalty_points")).intValue()+" "+
											(resultSet.getString("value_code")!=null?resultSet.getString("value_code"):""):"0");
									String[] discAmt = info.split(""+(info.startsWith("P:")?Constants.DELIMETER_COMMA:Constants.DELIMETER_COLON));
									exportResultSet.updateString("item_info",(discAmt[0].startsWith("P:")?discAmt[0].split(""+Constants.DELIMETER_COLON)[1]:discAmt[0]) );
									exportResultSet.updateDouble("discountOnItem",Double.parseDouble((discAmt.length>=3)?discAmt[2]:discAmt[1]));
									exportResultSet.updateString("membership", (resultSet.getString("membership"))!=null ? resultSet.getString("membership") : "");
									exportResultSet.updateString("subsidiary_number", (resultSet.getString("subsidiary_number"))!=null ? resultSet.getString("subsidiary_number") : "");
									
									exportResultSet.insertRow();
									exportResultSet.moveToCurrentRow();	
								}
							}else {

								random = Math.random();
								exportResultSet.moveToInsertRow();
								exportResultSet.updateString("id", users.getUserId()+""+random+""+System.currentTimeMillis());
								for (String custFldkey : contactFieldsInDB.split(",")) {
									custFldkey = custFldkey.replaceFirst("c.", "");
									if(custFldkey.equals("birth_day")){
										try{
											cal = Calendar.getInstance();
											cal.setTimeInMillis(resultSet.getTimestamp(custFldkey).getTime());
											}catch(NullPointerException e){
												cal =null;
											}
										exportResultSet.updateString("urls", resultSet.getTimestamp(custFldkey)!=null?MyCalendar.calendarToString(cal,MyCalendar.FORMAT_DATETIME_STDATE):null);
									}else if(custFldkey.equals("anniversary_day")){
										exportResultSet.updateTimestamp("created_date", resultSet.getTimestamp(custFldkey)!=null?resultSet.getTimestamp(custFldkey):null);
									}
								else{
									exportResultSet.updateString(custFldkey, resultSet.getString(custFldkey));
								}
								}
								/*String query = "select cc.coupon_code,cc.status,cc.issued_to,cc.redeemed_to,cc.campaign_name,cc.issued_on,cc.redeemed_on,cc.store_number,cc.tot_discount,cc.tot_revenue"+contactFieldsInDB+" from coupon_codes cc left join contacts c on cc.contact_id = c.cid where coupon_id="+coupId+" AND status NOT IN('"
										+ Constants.COUP_CODE_STATUS_INVENTORY + "') ORDER BY cc.issued_on DESC";
								String exportQuery = "select c.id,c.mobile_status,c.user_id,c.subscription_type,c.cf_value,c.domain,c.categories,c.optin_medium,,c.hp_id,c.email_status,home_phone"+contactFieldsInDB+" from tempexportreport c where 1=2";
								MyCalendar.calendarToString(
							coupCodesObj.getIssuedOn(),
							MyCalendar.FORMAT_DATETIME_STDATE , clientTimeZone)*/
								exportResultSet.updateString("mobile_status", resultSet.getString("coupon_code"));
								if(resultSet.getString("status")!=null && resultSet.getString("status").equalsIgnoreCase(Constants.COUP_CODE_STATUS_ACTIVE)) {
									exportResultSet.updateString("email_status", "Issued");
								}else if(resultSet.getString("status")!=null && resultSet.getString("status").equalsIgnoreCase(Constants.COUP_CODE_STATUS_INVENTORY)){
									exportResultSet.updateString("email_status", "Not Issued");
								}else {
									exportResultSet.updateString("email_status", resultSet.getString("status"));
								}
								exportResultSet.updateString("subscription_type", resultSet.getString("issued_to"));
								exportResultSet.updateString("cf_value", resultSet.getString("redeemed_to"));
								exportResultSet.updateString("domain", resultSet.getString("campaign_name"));
								
								try{
									cal = Calendar.getInstance();
								cal.setTimeInMillis(resultSet.getTimestamp("issued_on").getTime());
								}catch(NullPointerException e){
									cal =null;
								}
								exportResultSet.updateString("categories", MyCalendar.calendarToString(cal ,MyCalendar.FORMAT_DATETIME_STDATE_PROMO , clientTimeZone));
								try{
									cal = Calendar.getInstance();
									cal.setTimeInMillis(resultSet.getTimestamp("redeemed_on").getTime());
								}catch(NullPointerException e){
									cal =null;
								}
								exportResultSet.updateString("optin_medium", MyCalendar.calendarToString(cal ,MyCalendar.FORMAT_DATETIME_STDATE_PROMO , clientTimeZone));
								exportResultSet.updateString("home_phone",resultSet.getString("store_number") !=null? fetchStoreName(resultSet.getString("store_number")):"");
								
						    	//exportResultSet.updateString("doc_sid", resultSet.getString("doc_sid"));
								exportResultSet.updateString("doc_sid", resultSet.getString("receipt_number"));
								/*String receiptNo=resultSet.getString("receipt_number");
								RetailProSalesCSV rProSalesObj=null;
								if(receiptNo==null){
									rProSalesObj=retailProsalesDao.findReceiptNumberByDocsid(resultSet.getString("doc_sid"), coupObj.getUserId());
								}
								exportResultSet.updateString("doc_sid",receiptNo!=null?receiptNo:(rProSalesObj!=null && rProSalesObj.getRecieptNumber()!=null)?rProSalesObj.getRecieptNumber().toString():"" );
								*/
								exportResultSet.updateDouble("discount", (Double)resultSet.getDouble("tot_discount")!=null?Double.parseDouble(decimalFormat.format((Double)resultSet.getDouble("tot_discount"))):null);
								
								exportResultSet.updateString("tot_revenue", (Double)resultSet.getDouble("tot_revenue")!=null?(((Double.parseDouble(decimalFormat.format((Double)resultSet.getDouble("tot_revenue")))+"").equals("0.0"))?"null":Double.parseDouble(decimalFormat.format((Double)resultSet.getDouble("tot_revenue")))+""):"null");
								
								exportResultSet.updateString("loyalty_used_points", (Double)resultSet.getDouble("used_loyalty_points")!=null?
										((Double)resultSet.getDouble("used_loyalty_points")).intValue()+" "+
										(resultSet.getString("value_code")!=null?resultSet.getString("value_code"):""):"0");
								String[] discAmt = itemInfo.split(""+(itemInfo.startsWith("P:")?Constants.DELIMETER_COMMA:Constants.DELIMETER_COLON));
								exportResultSet.updateString("item_info",discAmt[0].startsWith("P:")?discAmt[0].split(""+Constants.DELIMETER_COLON)[1]:discAmt[0]);
								exportResultSet.updateDouble("discountOnItem",Double.parseDouble((discAmt.length>=3)?discAmt[2]:discAmt[1]));
								exportResultSet.updateString("membership", (resultSet.getString("membership"))!=null ? resultSet.getString("membership") : "");
								exportResultSet.updateString("subsidiary_number", (resultSet.getString("subsidiary_number"))!=null ? resultSet.getString("subsidiary_number") : "");
								
								exportResultSet.insertRow();
								exportResultSet.moveToCurrentRow();	
							
							}
						}else {
						random = Math.random();
						exportResultSet.moveToInsertRow();
						exportResultSet.updateString("id", users.getUserId()+""+random+""+System.currentTimeMillis());
						for (String custFldkey : contactFieldsInDB.split(",")) {
							custFldkey = custFldkey.replaceFirst("c.", "");
							if(custFldkey.equals("birth_day")){
								try{
									cal = Calendar.getInstance();
									cal.setTimeInMillis(resultSet.getTimestamp(custFldkey).getTime());
									}catch(NullPointerException e){
										cal =null;
									}
								exportResultSet.updateString("urls", resultSet.getTimestamp(custFldkey)!=null?MyCalendar.calendarToString(cal,MyCalendar.FORMAT_DATETIME_STDATE):null);
							}else if(custFldkey.equals("anniversary_day")){
								exportResultSet.updateTimestamp("created_date", resultSet.getTimestamp(custFldkey)!=null?resultSet.getTimestamp(custFldkey):null);
							}
						else{
							exportResultSet.updateString(custFldkey, resultSet.getString(custFldkey));
						}
						}
						/*String query = "select cc.coupon_code,cc.status,cc.issued_to,cc.redeemed_to,cc.campaign_name,cc.issued_on,cc.redeemed_on,cc.store_number,cc.tot_discount,cc.tot_revenue"+contactFieldsInDB+" from coupon_codes cc left join contacts c on cc.contact_id = c.cid where coupon_id="+coupId+" AND status NOT IN('"
								+ Constants.COUP_CODE_STATUS_INVENTORY + "') ORDER BY cc.issued_on DESC";
						String exportQuery = "select c.id,c.mobile_status,c.user_id,c.subscription_type,c.cf_value,c.domain,c.categories,c.optin_medium,,c.hp_id,c.email_status,home_phone"+contactFieldsInDB+" from tempexportreport c where 1=2";
						MyCalendar.calendarToString(
					coupCodesObj.getIssuedOn(),
					MyCalendar.FORMAT_DATETIME_STDATE , clientTimeZone)*/
						exportResultSet.updateString("mobile_status", resultSet.getString("coupon_code"));
						if(resultSet.getString("status")!=null && resultSet.getString("status").equalsIgnoreCase(Constants.COUP_CODE_STATUS_ACTIVE)) {
							exportResultSet.updateString("email_status", "Issued");
						}else if(resultSet.getString("status")!=null && resultSet.getString("status").equalsIgnoreCase(Constants.COUP_CODE_STATUS_INVENTORY)){
							exportResultSet.updateString("email_status", "Not Issued");
						}else {
							exportResultSet.updateString("email_status", resultSet.getString("status"));
						}
						exportResultSet.updateString("subscription_type", resultSet.getString("issued_to"));
						exportResultSet.updateString("cf_value", resultSet.getString("redeemed_to"));
						exportResultSet.updateString("domain", resultSet.getString("campaign_name"));
						
						try{
							cal = Calendar.getInstance();
						cal.setTimeInMillis(resultSet.getTimestamp("issued_on").getTime());
						}catch(NullPointerException e){
							cal =null;
						}
						exportResultSet.updateString("categories", MyCalendar.calendarToString(cal ,MyCalendar.FORMAT_DATETIME_STDATE_PROMO, clientTimeZone));
						try{
							cal = Calendar.getInstance();
							cal.setTimeInMillis(resultSet.getTimestamp("redeemed_on").getTime());
						}catch(NullPointerException e){
							cal =null;
						}
						exportResultSet.updateString("optin_medium", MyCalendar.calendarToString(cal ,MyCalendar.FORMAT_DATETIME_STDATE_PROMO , clientTimeZone));
						exportResultSet.updateString("home_phone",resultSet.getString("store_number") !=null? fetchStoreName(resultSet.getString("store_number")):"");
						
				    	//exportResultSet.updateString("doc_sid", resultSet.getString("doc_sid"));
						exportResultSet.updateString("doc_sid", resultSet.getString("receipt_number"));
						/*String receiptNo=resultSet.getString("receipt_number");
						RetailProSalesCSV rProSalesObj=null;
						if(receiptNo==null){
							rProSalesObj=retailProsalesDao.findReceiptNumberByDocsid(resultSet.getString("doc_sid"), coupObj.getUserId());
						}
						exportResultSet.updateString("doc_sid",receiptNo!=null?receiptNo:(rProSalesObj!=null && rProSalesObj.getRecieptNumber()!=null)?rProSalesObj.getRecieptNumber().toString():"" );
						*/
						exportResultSet.updateDouble("discount", (Double)resultSet.getDouble("tot_discount")!=null?Double.parseDouble(decimalFormat.format((Double)resultSet.getDouble("tot_discount"))):null);
						
						exportResultSet.updateString("tot_revenue", (Double)resultSet.getDouble("tot_revenue")!=null?(((Double.parseDouble(decimalFormat.format((Double)resultSet.getDouble("tot_revenue")))+"").equals("0.0"))?"null":Double.parseDouble(decimalFormat.format((Double)resultSet.getDouble("tot_revenue")))+""):"null");
						
						exportResultSet.updateString("loyalty_used_points", (Double)resultSet.getDouble("used_loyalty_points")!=null?
								((Double)resultSet.getDouble("used_loyalty_points")).intValue()+" "+
								(resultSet.getString("value_code")!=null?resultSet.getString("value_code"):""):"0");
						exportResultSet.updateString("item_info", (resultSet.getString("item_info"))!=null ? resultSet.getString("item_info") : "");
						exportResultSet.updateDouble("discountOnItem", (Double)resultSet.getDouble("tot_discount")!=null?Double.parseDouble(decimalFormat.format((Double)resultSet.getDouble("tot_discount"))):null);
						exportResultSet.updateString("membership", (resultSet.getString("membership"))!=null ? resultSet.getString("membership") : "");
						exportResultSet.updateString("subsidiary_number", (resultSet.getString("subsidiary_number"))!=null ? resultSet.getString("subsidiary_number") : "");
						
						exportResultSet.insertRow();
						exportResultSet.moveToCurrentRow();
						}
						}
						
					OCCSVWriter csvWriter = new OCCSVWriter(bw);
					try {
						csvWriter = new OCCSVWriter(bw);
						csvWriter.writeAll(exportResultSet,false, 1);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						logger.error("exception while initiating writer ",e);
						
					}finally{
						bw.flush();
						csvWriter.flush();
						bw.close();
						csvWriter.close();
						jdbcResultsetHandler.destroy();
						bw = null;
						jdbcResultsetHandler = null;
						//exportJdbcResultsetHandler = null;
					}
					
					
					Filedownload.save(file, "text/csv");
			}
		} catch (Exception e) {
			logger.error("exception while initiating writer ",e);
			
		} finally{
			
			if(exportJdbcResultsetHandler!=null){exportJdbcResultsetHandler.rollback(); exportJdbcResultsetHandler.destroy();exportJdbcResultsetHandler=null;}
			if(jdbcResultsetHandler!=null ) jdbcResultsetHandler.destroy();
			posMappingDao = null;type = null;userName = null;usersParentDirectory = null;exportDir = null;downloadDir = null;name = null;
			filePath = null;file = null;bw = null;posMappingsList = null;orderedMappingMap = null;udfFldsLabel = null;contactFieldsInDB = null;
			jdbcResultsetHandler = null;jdbcResultsetHandler = null;
			//System.gc();
		}
		long endTime = System.currentTimeMillis();
		logger.fatal("Time taken to export coupons is :::::::::::::::::::::::: " + (endTime-startTime));
	} 
	public void exportExcel(Combobox exportCbId) {

		
		long startTime = System.currentTimeMillis();
		POSMappingDao posMappingDao = null;
		String type = Constants.STRING_NILL;
		String userName = Constants.STRING_NILL;
		String usersParentDirectory = Constants.STRING_NILL;
		String exportDir = Constants.STRING_NILL;
		File downloadDir = null;
		String name = Constants.STRING_NILL;
		String filePath = Constants.STRING_NILL;
		File file = null;
		BufferedWriter bw = null;
		List<POSMapping> posMappingsList = null;
		Map<String, POSMapping> orderedMappingMap = null;
		String udfFldsLabel = Constants.STRING_NILL;
		String contactFieldsInDB = Constants.STRING_NILL;
		JdbcResultsetHandler jdbcResultsetHandler = null;
		JdbcResultsetHandler exportJdbcResultsetHandler = null;
		HSSFWorkbook hwb = new HSSFWorkbook();
		HSSFSheet sheet = hwb.createSheet("Discount code Report");
		HSSFRow row = sheet.createRow((short) 0);
		HSSFCellStyle cellStyle= hwb.createCellStyle();
		HSSFCell cell = null;
	try {
		logger.debug("--Excel just entered --");
		
		if(couponsRowsId.getChildren().size() == 0) {
			MessageUtil.setMessage("No reports existed ","color:red","TOP");
			return;
		}
		 posMappingDao = (POSMappingDao)SpringUtil.getBean("posMappingDao");
		Long currUserId = GetUser.getUserId();
		
		type = exportCbId.getSelectedItem().getLabel();
		userName = GetUser.getUserName();
		usersParentDirectory = (String)PropertyUtil.getPropertyValue("usersParentDirectory");
		exportDir = usersParentDirectory + "/" + userName + "/Coupon/" ;
		downloadDir = new File(exportDir);
		if(downloadDir.exists()){
			try {
				FileUtils.deleteDirectory(downloadDir);
				logger.debug(downloadDir.getName() + " is deleted");
			} catch (Exception e) {
				logger.error("Exception ::" , e);
				logger.debug(downloadDir.getName() + " is not deleted");
			}
		}
		if(!downloadDir.exists()){
			downloadDir.mkdirs();
		}
		if(type.contains("xls")){
			
			long coupId = coupObj.getCouponId();
			name = coupObj.getCouponName();
			if(name.contains("/")) {
				
				name = name.replace("/", "_") ;
				
			}
				filePath = exportDir +  "Coupon_" + name + "_" +
				MyCalendar.calendarToString(coupObj.getCouponCreatedDate(), MyCalendar.FORMAT_YEARTOSEC, clientTimeZone);
			
				filePath = filePath + "_DiscountCodes.xls";
				logger.debug("Download File path : " + filePath);
				file = new File(filePath);
				FileOutputStream fileOut = new FileOutputStream(filePath);
				
				posMappingsList = posMappingDao.findByType("'"+Constants.POS_MAPPING_TYPE_CONTACTS+"'", currUserId);
				
				
				orderedMappingMap = getOrderedMappingSet(posMappingsList);
				
				
				for (String custFldKey : orderedMappingMap.keySet()) {
					
					//if(udfFldsLabel.length() > 0) udfFldsLabel += ",";
					if(custFldKey.contentEquals(POSFieldsEnum.customerId.getOcAttr())) continue;
					if(udfFldsLabel.length() > 0) udfFldsLabel += ",";
					udfFldsLabel += "\""+orderedMappingMap.get(custFldKey).getDisplayLabel().trim()+"\"";
					contactFieldsInDB +=","+genFieldContMap.get(custFldKey).trim();
					
					
					
				}//for
				
				
				
				row = sheet.createRow(0);
				int cellNo = 0;
				cell = row.createCell((cellNo++));
				cell.setCellValue("Discount code");

				cell = row.createCell((cellNo++));
				cell.setCellValue("Status");

				cell = row.createCell(cellNo++);
				cell.setCellValue("Issued to");

				cell = row.createCell(cellNo++);
				cell.setCellValue("Redeemed by");

				cell = row.createCell(cellNo++);
				cell.setCellValue("Membership #");

				cell = row.createCell((cellNo++));
				cell.setCellValue("Campaign Name");

				cell = row.createCell((cellNo++));
				cell.setCellValue("Issued On");

				cell = row.createCell((cellNo++));
				cell.setCellValue("Redeemed On");

				cell = row.createCell((cellNo++));
				cell.setCellValue("Store");

				cell = row.createCell(cellNo++);
				cell.setCellValue("Subsidiary");

				cell = row.createCell(cellNo++);
				cell.setCellValue("Receipt#");
				
				cell = row.createCell(cellNo++);
				cell.setCellValue("Discount On Item");

				cell = row.createCell(cellNo++);
				cell.setCellValue("Total Discount");

				cell = row.createCell((cellNo++));
				cell.setCellValue("Total Revenue");

				cell = row.createCell((cellNo++));
				cell.setCellValue("Rewards Redeemed if any");

				cell = row.createCell((cellNo++));
				cell.setCellValue("Item Info");
				
				for(String custFldKey : orderedMappingMap.keySet()) {
					if(custFldKey.contentEquals(POSFieldsEnum.customerId.getOcAttr())) continue;
				cell = row.createCell((cellNo++));
				cell.setCellValue(orderedMappingMap.get(custFldKey).getDisplayLabel().trim());
				}
				
				
				Map<String,String> searchData = getFilterData();
				String searchQuery = "";
				String query = "";
				boolean statusFlag = false;
				if(searchData.containsKey("storeName")) {
					String storeId = searchData.get("storeName");
					searchQuery = "AND store_number = "+storeId+"";
				}else if (searchData.containsKey("RedeemedDate")) {
					String[] date = searchData.get("RedeemedDate").split("T");
					searchQuery = "AND redeemed_on BETWEEN '"+date[0]+"' AND '"+date[1]+"'";
				}else if (searchData.containsKey("IssuedDate")) {
					String[] date = searchData.get("IssuedDate").split("T");
					searchQuery = "AND issued_on BETWEEN '"+date[0]+"' AND '"+date[1]+"'";
				}else if (searchData.containsKey("promotionStatus")) {
					String promotionStatus = searchData.get("promotionStatus");
					statusFlag = true;
					if(promotionStatus.equalsIgnoreCase("Redeemed")) {
						query = " AND status IN('" + Constants.COUP_CODE_STATUS_REDEEMED + "') ";
					}else if(promotionStatus.equalsIgnoreCase("Active")) {
						query = " AND status IN('" + Constants.COUP_CODE_STATUS_ACTIVE + "','"+ Constants.COUP_CODE_STATUS_REDEEMED +"','"+ Constants.COUP_CODE_STATUS_EXPIRED +"') AND issued_to IS NOT NULL";
					}else if(promotionStatus.equalsIgnoreCase("Inventory")) {
						query = " AND status IN('" + Constants.COUP_CODE_STATUS_INVENTORY + "')";
					}else if(promotionStatus.equalsIgnoreCase("Expired")) {
						query = " AND status IN('" + Constants.COUP_CODE_STATUS_EXPIRED + "') ";
					}else if(promotionStatus.equalsIgnoreCase("All")) {
						query = "";
					}
					
					/*query = "select cc.coupon_code,cc.status,cc.issued_to,cc.redeemed_to,cc.membership,cc.campaign_name,cc.issued_on,cc.redeemed_on,cc.store_number,cc.subsidiary_number,cc.receipt_number,cc.tot_discount,cc.tot_revenue,"
							+ "cc.used_loyalty_points,cc.value_code,cc.item_info"+contactFieldsInDB+" "
							+ "from coupon_codes cc left join (select * from contacts where user_id="+users.getUserId()+"  group by external_id) c on (cc.redeem_cust_id = c.external_id) "
							+ "where coupon_id="+coupId+" "+query+" order by cc.redeemed_on desc";*/
				
				
				
					query = "select cc.coupon_code,cc.status,cc.issued_to,cc.redeemed_to,cc.membership,cc.campaign_name,cc.issued_on,cc.redeemed_on,cc.store_number,cc.subsidiary_number,cc.receipt_number,cc.tot_discount,cc.tot_revenue,"
							+ "cc.used_loyalty_points,cc.value_code,cc.item_info"+contactFieldsInDB+" "
							+ "from coupon_codes cc left join (select * from contacts c where user_id="+users.getUserId()+") c on (if(cc.redeem_cust_id IS NULL AND cc.redeem_phn_id IS NOT NULL,cc.redeem_phn_id=c.mobile_phone,cc.redeem_cust_id = c.external_id)) "
							+ "where cc.orgId="+users.getUserOrganization().getUserOrgId()+" AND coupon_id="+coupId+" "+query+" order by cc.redeemed_on desc"; 
				
				
				
				
				
				
				}
				
				if(searchQuery!=null && !searchQuery.isEmpty() && !statusFlag) {
					/*query = "select cc.coupon_code,cc.status,cc.issued_to,cc.redeemed_to,cc.membership,cc.campaign_name,cc.issued_on,cc.redeemed_on,cc.store_number,cc.subsidiary_number,cc.receipt_number,cc.tot_discount,cc.tot_revenue,"
							+ "cc.used_loyalty_points,cc.value_code,cc.item_info"+contactFieldsInDB+" "
							+ "from coupon_codes cc left join (select * from contacts where user_id="+users.getUserId()+"  group by external_id) c on (cc.redeem_cust_id = c.external_id) where coupon_id="+coupId+""
						+ "  "+searchQuery+" AND status NOT IN('"+ Constants.COUP_CODE_STATUS_INVENTORY + "')  order by cc.redeemed_on desc";*/
			
				
				
					
					query = "select cc.coupon_code,cc.status,cc.issued_to,cc.redeemed_to,cc.membership,cc.campaign_name,cc.issued_on,cc.redeemed_on,cc.store_number,cc.subsidiary_number,cc.receipt_number,cc.tot_discount,cc.tot_revenue,"
							+ "cc.used_loyalty_points,cc.value_code,cc.item_info"+contactFieldsInDB+" "
							+ "from coupon_codes cc left join (select * from contacts where user_id="+users.getUserId()+")  c on (if(cc.redeem_cust_id IS NULL AND cc.redeem_phn_id IS NOT NULL,cc.redeem_phn_id=c.mobile_phone,cc.redeem_cust_id = c.external_id))  where  cc.orgId="+users.getUserOrganization().getUserOrgId()+" AND coupon_id="+coupId+"  "
						+ "  "+searchQuery+" AND status NOT IN('"+ Constants.COUP_CODE_STATUS_INVENTORY + "')  order by cc.redeemed_on desc";
			
					
					
					
				
				
				
				
				
				
				}
				
				String exportQuery = "select c.id,c.mobile_status,c.email_status,c.subscription_type,c.cf_value,c.membership,c.domain,c.categories,c.optin_medium,c.home_phone,c.subsidiary_number,c.doc_sid,c.discountOnItem,c.discount,c.tot_revenue,c.loyalty_used_points,c.item_info"+contactFieldsInDB+" from tempexportreport c where 1=2";
				
				exportQuery = exportQuery.replaceFirst("c.birth_day", "c.urls");
				exportQuery = exportQuery.replaceFirst("c.anniversary_day", "c.created_date");
				
				logger.info("Excel query is :: "+query);
				logger.info("Excel exportQuery is :: "+exportQuery);
				jdbcResultsetHandler = new JdbcResultsetHandler();
				jdbcResultsetHandler.executeStmt(query);
				
				exportJdbcResultsetHandler = new JdbcResultsetHandler();
				exportJdbcResultsetHandler.executeStmt(exportQuery, true);
				
				ResultSet exportResultSet, resultSet;
				resultSet = jdbcResultsetHandler.getResultSet();
				exportResultSet = exportJdbcResultsetHandler.getResultSet();
				DecimalFormat decimalFormat = new DecimalFormat("#0.00");
				double random = 0;
				contactFieldsInDB = contactFieldsInDB.substring(1);
				Calendar cal = Calendar.getInstance();
				int rowId = 1;
				while(resultSet.next()){
					//row = sheet.createRow(rowId++);
					int columnId = 0;
					cell = null;
					String itemInfo = resultSet.getString("item_info");
					logger.info("Excel item info from resultset "+itemInfo);
					if(itemInfo!=null) {
						String[] itemInfosArr =null;
						double totdisc=0.0;
						if(itemInfo.contains(""+Constants.DELIMITER_PIPE)){
							itemInfosArr = itemInfo.split("\\"+Constants.DELIMITER_PIPE);
							for(String eachitem : itemInfosArr) {
								String[] discAmt = eachitem.split(""+(eachitem.startsWith("P:")?Constants.DELIMETER_COMMA:Constants.DELIMETER_COLON));
								totdisc += (discAmt.length>=3?(Double.parseDouble(discAmt[2]) * Double.parseDouble(discAmt[1])):(Double.parseDouble(discAmt[1])));
							}
							for (String info : itemInfosArr) {
								row = sheet.createRow(rowId++);
								columnId = 0;
								random = Math.random();
								exportResultSet.moveToInsertRow();
								//cell = row.createCell(columnId++);
								exportResultSet.updateString("id", users.getUserId()+""+random+""+System.currentTimeMillis());
								//cell.setCellValue(exportResultSet.getString("id"));
								
								cell = row.createCell(columnId++);
								exportResultSet.updateString("mobile_status", resultSet.getString("coupon_code"));
								cell.setCellValue(exportResultSet.getString("mobile_status"));
								if(resultSet.getString("status")!=null && resultSet.getString("status").equalsIgnoreCase(Constants.COUP_CODE_STATUS_ACTIVE)) {
									cell = row.createCell(columnId++);
									exportResultSet.updateString("email_status", "Issued");
									cell.setCellValue(exportResultSet.getString("email_status"));
								}else if(resultSet.getString("status")!=null && resultSet.getString("status").equalsIgnoreCase(Constants.COUP_CODE_STATUS_INVENTORY)){
									cell = row.createCell(columnId++);
									exportResultSet.updateString("email_status", "Not Issued");
									cell.setCellValue(exportResultSet.getString("email_status"));
								}else {
									cell = row.createCell(columnId++);
									exportResultSet.updateString("email_status", resultSet.getString("status"));
									cell.setCellValue(exportResultSet.getString("email_status"));
								}
								cell = row.createCell(columnId++);
								exportResultSet.updateString("subscription_type", resultSet.getString("issued_to"));
								cell.setCellValue(exportResultSet.getString("subscription_type"));
								
								cell = row.createCell(columnId++);
								exportResultSet.updateString("cf_value", resultSet.getString("redeemed_to"));
								cell.setCellValue(exportResultSet.getString("cf_value"));
								
								cell = row.createCell(columnId++);
								exportResultSet.updateString("membership", (resultSet.getString("membership"))!=null ? resultSet.getString("membership") : "");
								cell.setCellValue(exportResultSet.getString("membership"));
								
								cell = row.createCell(columnId++);
								exportResultSet.updateString("domain", resultSet.getString("campaign_name"));
								cell.setCellValue(exportResultSet.getString("domain"));
								
								try{
									cal = Calendar.getInstance();
								cal.setTimeInMillis(resultSet.getTimestamp("issued_on").getTime());
								}catch(NullPointerException e){
									cal =null;
								}
								cell = row.createCell(columnId++);
								exportResultSet.updateString("categories", MyCalendar.calendarToString(cal ,MyCalendar.FORMAT_DATETIME_STDATE_PROMO , clientTimeZone));
								cell.setCellValue(exportResultSet.getString("categories"));
								try{
									cal = Calendar.getInstance();
									cal.setTimeInMillis(resultSet.getTimestamp("redeemed_on").getTime());
								}catch(NullPointerException e){
									cal =null;
								}
								cell = row.createCell(columnId++);
								exportResultSet.updateString("optin_medium", MyCalendar.calendarToString(cal ,MyCalendar.FORMAT_DATETIME_STDATE_PROMO, clientTimeZone));
								cell.setCellValue(exportResultSet.getString("optin_medium"));
								
								cell = row.createCell(columnId++);
								exportResultSet.updateString("home_phone",resultSet.getString("store_number") !=null? fetchStoreName(resultSet.getString("store_number")):"");
								cell.setCellValue(exportResultSet.getString("home_phone"));
								
								cell = row.createCell(columnId++);
								exportResultSet.updateString("subsidiary_number", (resultSet.getString("subsidiary_number"))!=null ? resultSet.getString("subsidiary_number") : "");
								cell.setCellValue(exportResultSet.getString("subsidiary_number"));
								
								cell = row.createCell(columnId++);
								exportResultSet.updateString("doc_sid", resultSet.getString("receipt_number"));
								cell.setCellValue(exportResultSet.getString("doc_sid"));
								
								String[] itemDisc = info.split(""+(info.startsWith("P:")?Constants.DELIMETER_COMMA:Constants.DELIMETER_COLON));
								
								cell = row.createCell(columnId++);
								exportResultSet.updateDouble("discountOnItem",Double.parseDouble((itemDisc.length>=3)?itemDisc[2]:itemDisc[1]));
								cell.setCellValue(exportResultSet.getDouble("discountOnItem"));
								
								cell = row.createCell(columnId++);
								exportResultSet.updateDouble("discount", totdisc);
								cell.setCellValue(exportResultSet.getDouble("discount"));
								
								cell = row.createCell(columnId++);
								exportResultSet.updateString("tot_revenue", (Double)resultSet.getDouble("tot_revenue")!=null?(((Double.parseDouble(decimalFormat.format((Double)resultSet.getDouble("tot_revenue")))+"").equals("0.0"))?"null":Double.parseDouble(decimalFormat.format((Double)resultSet.getDouble("tot_revenue")))+""):"null");
								cell.setCellValue(exportResultSet.getString("tot_revenue"));
								
								cell = row.createCell(columnId++);
								exportResultSet.updateString("loyalty_used_points", (Double)resultSet.getDouble("used_loyalty_points")!=null?
										((Double)resultSet.getDouble("used_loyalty_points")).intValue()+" "+
										(resultSet.getString("value_code")!=null?resultSet.getString("value_code"):""):"0");
								cell.setCellValue(exportResultSet.getString("loyalty_used_points"));
								
								cell = row.createCell(columnId++);
								exportResultSet.updateString("item_info",itemDisc[0].startsWith("P:")?itemDisc[0].split(""+Constants.DELIMETER_COLON)[1]:itemDisc[0]);
								cell.setCellValue(exportResultSet.getString("item_info"));
								
								for (String custFldkey : contactFieldsInDB.split(",")) {
									custFldkey = custFldkey.replaceFirst("c.", "");
									if(custFldkey.equals("birth_day")){
										try{
											cal = Calendar.getInstance();
											cal.setTimeInMillis(resultSet.getTimestamp(custFldkey).getTime());
											}catch(NullPointerException e){
												cal =null;
											}
										cell = row.createCell(columnId++);
										exportResultSet.updateString("urls", resultSet.getTimestamp(custFldkey)!=null?MyCalendar.calendarToString(cal,MyCalendar.FORMAT_DATETIME_STDATE):null);
										cell.setCellValue(exportResultSet.getString("urls"));
									}else if(custFldkey.equals("anniversary_day")){
										/*
										 * cell = row.createCell(columnId++);
										 * exportResultSet.updateTimestamp("created_date",
										 * resultSet.getTimestamp(custFldkey)!=null?resultSet.getTimestamp(custFldkey):
										 * null); cell.setCellValue(exportResultSet.getString("created_date"));
										 */
										cell = row.createCell(columnId++);
										if((resultSet.getTimestamp(custFldkey)!=null?resultSet.getTimestamp(custFldkey):null) !=null) {
											exportResultSet.updateTimestamp("created_date", resultSet.getTimestamp(custFldkey)!=null?resultSet.getTimestamp(custFldkey):null);
											cell.setCellValue(exportResultSet.getTimestamp("created_date"));
										}else {
											cell.setCellValue("--");
										}
									}
								else{
									cell = row.createCell(columnId++);
									exportResultSet.updateString(custFldkey, resultSet.getString(custFldkey));
									cell.setCellValue(exportResultSet.getString(custFldkey));
								}
								}
							}
						}else {
							row = sheet.createRow(rowId++);

							random = Math.random();
							exportResultSet.moveToInsertRow();
							//cell = row.createCell(columnId++);
							exportResultSet.updateString("id", users.getUserId()+""+random+""+System.currentTimeMillis());
							//cell.setCellValue(exportResultSet.getString("id"));
							cell = row.createCell(columnId++);
							exportResultSet.updateString("mobile_status", resultSet.getString("coupon_code"));
							cell.setCellValue(exportResultSet.getString("mobile_status"));
							if(resultSet.getString("status")!=null && resultSet.getString("status").equalsIgnoreCase(Constants.COUP_CODE_STATUS_ACTIVE)) {
								cell = row.createCell(columnId++);
								exportResultSet.updateString("email_status", "Issued");
								cell.setCellValue(exportResultSet.getString("email_status"));
							}else if(resultSet.getString("status")!=null && resultSet.getString("status").equalsIgnoreCase(Constants.COUP_CODE_STATUS_INVENTORY)){
								cell = row.createCell(columnId++);
								exportResultSet.updateString("email_status", "Not Issued");
								cell.setCellValue(exportResultSet.getString("email_status"));
							}else {
								cell = row.createCell(columnId++);
								exportResultSet.updateString("email_status", resultSet.getString("status"));
								cell.setCellValue(exportResultSet.getString("email_status"));
							}
							cell = row.createCell(columnId++);
							exportResultSet.updateString("subscription_type", resultSet.getString("issued_to"));
							cell.setCellValue(exportResultSet.getString("subscription_type"));
							
							cell = row.createCell(columnId++);
							exportResultSet.updateString("cf_value", resultSet.getString("redeemed_to"));
							cell.setCellValue(exportResultSet.getString("cf_value"));
							
							cell = row.createCell(columnId++); 
							exportResultSet.updateString("membership",(resultSet.getString("membership"))!=null ? resultSet.getString("membership"): ""); 
							cell.setCellValue(exportResultSet.getString("membership"));
							
							cell = row.createCell(columnId++);
							exportResultSet.updateString("domain", resultSet.getString("campaign_name"));
							cell.setCellValue(exportResultSet.getString("domain"));
							
							
							try{
								cal = Calendar.getInstance();
							cal.setTimeInMillis(resultSet.getTimestamp("issued_on").getTime());
							}catch(NullPointerException e){
								cal =null;
							}
							cell = row.createCell(columnId++);
							exportResultSet.updateString("categories", MyCalendar.calendarToString(cal ,MyCalendar.FORMAT_DATETIME_STDATE_PROMO , clientTimeZone));
							cell.setCellValue(exportResultSet.getString("categories"));
							try{
								cal = Calendar.getInstance();
								cal.setTimeInMillis(resultSet.getTimestamp("redeemed_on").getTime());
							}catch(NullPointerException e){
								cal =null;
							}
							cell = row.createCell(columnId++);
							exportResultSet.updateString("optin_medium", MyCalendar.calendarToString(cal ,MyCalendar.FORMAT_DATETIME_STDATE_PROMO , clientTimeZone));
							cell.setCellValue(exportResultSet.getString("optin_medium"));
							
							cell = row.createCell(columnId++);
							exportResultSet.updateString("home_phone",resultSet.getString("store_number") !=null? fetchStoreName(resultSet.getString("store_number")):"");
							cell.setCellValue(exportResultSet.getString("home_phone"));
							
							cell = row.createCell(columnId++);
							exportResultSet.updateString("subsidiary_number", (resultSet.getString("subsidiary_number"))!=null ? resultSet.getString("subsidiary_number") : "");
							cell.setCellValue(exportResultSet.getString("subsidiary_number"));
							
							cell = row.createCell(columnId++);
							exportResultSet.updateString("doc_sid", resultSet.getString("receipt_number"));
							cell.setCellValue(exportResultSet.getString("doc_sid"));
							
							String[] info = itemInfo.split(""+(itemInfo.startsWith("P:")?Constants.DELIMETER_COMMA:Constants.DELIMETER_COLON));
							
							cell = row.createCell(columnId++);
							exportResultSet.updateDouble("discountOnItem",Double.parseDouble((info.length>=3)?info[2]:info[1]));
							cell.setCellValue(exportResultSet.getDouble("discountOnItem"));
							
							cell = row.createCell(columnId++);
							exportResultSet.updateDouble("discount", (Double)resultSet.getDouble("tot_discount")!=null?Double.parseDouble(decimalFormat.format((Double)resultSet.getDouble("tot_discount"))):null);
							cell.setCellValue(exportResultSet.getDouble("discount"));
							
							cell = row.createCell(columnId++);
							exportResultSet.updateString("tot_revenue", (Double)resultSet.getDouble("tot_revenue")!=null?(((Double.parseDouble(decimalFormat.format((Double)resultSet.getDouble("tot_revenue")))+"").equals("0.0"))?"null":Double.parseDouble(decimalFormat.format((Double)resultSet.getDouble("tot_revenue")))+""):"null");
							cell.setCellValue(exportResultSet.getString("tot_revenue"));
							
							cell = row.createCell(columnId++);
							exportResultSet.updateString("loyalty_used_points", (Double)resultSet.getDouble("used_loyalty_points")!=null?
									((Double)resultSet.getDouble("used_loyalty_points")).intValue()+" "+
									(resultSet.getString("value_code")!=null?resultSet.getString("value_code"):""):"0");
							cell.setCellValue(exportResultSet.getString("loyalty_used_points"));
							
							cell = row.createCell(columnId++);
							exportResultSet.updateString("item_info",info[0].startsWith("P:")?info[0].split(""+Constants.DELIMETER_COLON)[1]:info[0]) ;
							cell.setCellValue(exportResultSet.getString("item_info"));
							
							for (String custFldkey : contactFieldsInDB.split(",")) {
								custFldkey = custFldkey.replaceFirst("c.", "");
								if(custFldkey.equals("birth_day")){
									try{
										cal = Calendar.getInstance();
										cal.setTimeInMillis(resultSet.getTimestamp(custFldkey).getTime());
										}catch(NullPointerException e){
											cal =null;
										}
									cell = row.createCell(columnId++);
									exportResultSet.updateString("urls", resultSet.getTimestamp(custFldkey)!=null?MyCalendar.calendarToString(cal,MyCalendar.FORMAT_DATETIME_STDATE):null);
									cell.setCellValue(exportResultSet.getString("urls"));
								}else if(custFldkey.equals("anniversary_day")){
									/*
									 * cell = row.createCell(columnId++);
									 * exportResultSet.updateTimestamp("created_date",
									 * resultSet.getTimestamp(custFldkey)!=null?resultSet.getTimestamp(custFldkey):
									 * null); cell.setCellValue(exportResultSet.getTimestamp("created_date"));
									 */
									cell = row.createCell(columnId++);
									if((resultSet.getTimestamp(custFldkey)!=null?resultSet.getTimestamp(custFldkey):null) !=null) {
										exportResultSet.updateTimestamp("created_date", resultSet.getTimestamp(custFldkey)!=null?resultSet.getTimestamp(custFldkey):null);
										cell.setCellValue(exportResultSet.getTimestamp("created_date"));
									}else {
										cell.setCellValue("--");
									}
								}
							else{
								cell = row.createCell(columnId++);
								exportResultSet.updateString(custFldkey, resultSet.getString(custFldkey));
								cell.setCellValue(exportResultSet.getString(custFldkey));
							}
							}
							
							
							//exportResultSet.insertRow();
							//exportResultSet.moveToCurrentRow();	
						
						}
					}else {

					row = sheet.createRow(rowId++);
					random = Math.random();
					exportResultSet.moveToInsertRow();
					
					//cell = row.createCell(columnId++);
					exportResultSet.updateString("id", users.getUserId()+""+random+""+System.currentTimeMillis());
					//cell.setCellValue(exportResultSet.getString("id"));
					
					cell = row.createCell(columnId++);
					exportResultSet.updateString("mobile_status", resultSet.getString("coupon_code"));
					cell.setCellValue(exportResultSet.getString("mobile_status"));
					if(resultSet.getString("status")!=null && resultSet.getString("status").equalsIgnoreCase(Constants.COUP_CODE_STATUS_ACTIVE)) {
						cell = row.createCell(columnId++);
						exportResultSet.updateString("email_status", "Issued");
						cell.setCellValue(exportResultSet.getString("email_status"));
					}else if(resultSet.getString("status")!=null && resultSet.getString("status").equalsIgnoreCase(Constants.COUP_CODE_STATUS_INVENTORY)){
						cell = row.createCell(columnId++);
						exportResultSet.updateString("email_status", "Not Issued");
						cell.setCellValue(exportResultSet.getString("email_status"));
					}else {
						cell = row.createCell(columnId++);
						exportResultSet.updateString("email_status", resultSet.getString("status"));
						cell.setCellValue(exportResultSet.getString("email_status"));
					}
					cell = row.createCell(columnId++);
					exportResultSet.updateString("subscription_type", resultSet.getString("issued_to"));
					cell.setCellValue(exportResultSet.getString("subscription_type"));
					
					cell = row.createCell(columnId++);
					exportResultSet.updateString("cf_value", resultSet.getString("redeemed_to"));
					cell.setCellValue(exportResultSet.getString("cf_value"));
					
					cell = row.createCell(columnId++);
					exportResultSet.updateString("membership", (resultSet.getString("membership"))!=null ? resultSet.getString("membership") : "");
					cell.setCellValue(exportResultSet.getString("membership"));
					
					cell = row.createCell(columnId++);
					exportResultSet.updateString("domain", resultSet.getString("campaign_name"));
					cell.setCellValue(exportResultSet.getString("domain"));
					
					try{
						cal = Calendar.getInstance();
					cal.setTimeInMillis(resultSet.getTimestamp("issued_on").getTime());
					}catch(NullPointerException e){
						cal =null;
					}
					cell = row.createCell(columnId++);
					exportResultSet.updateString("categories", MyCalendar.calendarToString(cal ,MyCalendar.FORMAT_DATETIME_STDATE_PROMO , clientTimeZone));
					cell.setCellValue(exportResultSet.getString("categories"));
					try{
						cal = Calendar.getInstance();
						cal.setTimeInMillis(resultSet.getTimestamp("redeemed_on").getTime());
					}catch(NullPointerException e){
						cal =null;
					}
					cell = row.createCell(columnId++);
					exportResultSet.updateString("optin_medium", MyCalendar.calendarToString(cal ,MyCalendar.FORMAT_DATETIME_STDATE_PROMO , clientTimeZone));
					cell.setCellValue(exportResultSet.getString("optin_medium"));
					
					cell = row.createCell(columnId++);
					exportResultSet.updateString("home_phone",resultSet.getString("store_number") !=null? fetchStoreName(resultSet.getString("store_number")):"");
					cell.setCellValue(exportResultSet.getString("home_phone"));

					cell = row.createCell(columnId++);
					exportResultSet.updateString("subsidiary_number", (resultSet.getString("subsidiary_number"))!=null ? resultSet.getString("subsidiary_number") : "");
					cell.setCellValue(exportResultSet.getString("subsidiary_number"));
					
					cell = row.createCell(columnId++);
					exportResultSet.updateString("doc_sid", resultSet.getString("receipt_number"));
					cell.setCellValue(exportResultSet.getString("doc_sid"));
					
					cell = row.createCell(columnId++);
					exportResultSet.updateDouble("discount", (Double)resultSet.getDouble("tot_discount")!=null?Double.parseDouble(decimalFormat.format((Double)resultSet.getDouble("tot_discount"))):null);
					cell.setCellValue(exportResultSet.getDouble("discount"));
					
					cell = row.createCell(columnId++);
					exportResultSet.updateDouble("discountOnItem", (Double)resultSet.getDouble("tot_discount")!=null?Double.parseDouble(decimalFormat.format((Double)resultSet.getDouble("tot_discount"))):null);
					cell.setCellValue(exportResultSet.getDouble("discountOnItem"));

					cell = row.createCell(columnId++);
					exportResultSet.updateString("tot_revenue", (Double)resultSet.getDouble("tot_revenue")!=null?(((Double.parseDouble(decimalFormat.format((Double)resultSet.getDouble("tot_revenue")))+"").equals("0.0"))?"null":Double.parseDouble(decimalFormat.format((Double)resultSet.getDouble("tot_revenue")))+""):"null");
					cell.setCellValue(exportResultSet.getString("tot_revenue"));
					
					cell = row.createCell(columnId++);
					exportResultSet.updateString("loyalty_used_points", (Double)resultSet.getDouble("used_loyalty_points")!=null?
							((Double)resultSet.getDouble("used_loyalty_points")).intValue()+" "+
							(resultSet.getString("value_code")!=null?resultSet.getString("value_code"):""):"0");
					cell.setCellValue(exportResultSet.getString("loyalty_used_points"));
					
					cell = row.createCell(columnId++);
					exportResultSet.updateString("item_info", (resultSet.getString("item_info"))!=null ? resultSet.getString("item_info") : "");
					cell.setCellValue(exportResultSet.getString("item_info"));
					
					for (String custFldkey : contactFieldsInDB.split(",")) {
						custFldkey = custFldkey.replaceFirst("c.", "");
						if(custFldkey.equals("birth_day")){
							try{
								cal = Calendar.getInstance();
								cal.setTimeInMillis(resultSet.getTimestamp(custFldkey).getTime());
								}catch(NullPointerException e){
									cal =null;
								}
							cell = row.createCell(columnId++);
							exportResultSet.updateString("urls", resultSet.getTimestamp(custFldkey)!=null?MyCalendar.calendarToString(cal,MyCalendar.FORMAT_DATETIME_STDATE):null);
							cell.setCellValue(exportResultSet.getString("urls"));
						}else if(custFldkey.equals("anniversary_day")){
							cell = row.createCell(columnId++);
							if((resultSet.getTimestamp(custFldkey)!=null?resultSet.getTimestamp(custFldkey):null) !=null) {
								exportResultSet.updateTimestamp("created_date", resultSet.getTimestamp(custFldkey)!=null?resultSet.getTimestamp(custFldkey):null);
								cell.setCellValue(exportResultSet.getTimestamp("created_date"));
							}else {
								cell.setCellValue("--");
							}
						}
					else{
						cell = row.createCell(columnId++);
						exportResultSet.updateString(custFldkey, resultSet.getString(custFldkey));
						cell.setCellValue(exportResultSet.getString(custFldkey));
					}
					}
					
					
					//exportResultSet.insertRow();
					//exportResultSet.moveToCurrentRow();
					}
					}
					
				hwb.write(fileOut);
				fileOut.flush();
				fileOut.close();
			Filedownload.save(file, "application/vnd.ms-excel");
				
		}
	} catch (Exception e) {
		logger.error("exception while initiating writer ",e);
		
	} finally{
		
		if(exportJdbcResultsetHandler!=null){exportJdbcResultsetHandler.rollback(); exportJdbcResultsetHandler.destroy();exportJdbcResultsetHandler=null;}
		if(jdbcResultsetHandler!=null ) jdbcResultsetHandler.destroy();
		posMappingDao = null;type = null;userName = null;usersParentDirectory = null;exportDir = null;downloadDir = null;name = null;
		filePath = null;file = null;bw = null;posMappingsList = null;orderedMappingMap = null;udfFldsLabel = null;contactFieldsInDB = null;
		jdbcResultsetHandler = null;jdbcResultsetHandler = null;
		//System.gc();
	}
	long endTime = System.currentTimeMillis();
	logger.fatal("Time taken to export coupons is :::::::::::::::::::::::: " + (endTime-startTime));
		
	}
	public void onClick$exportBtnPromoDetailId() {
		
		long startTime = System.currentTimeMillis();
		String type = Constants.STRING_NILL;
		String userName = Constants.STRING_NILL;
		String usersParentDirectory = Constants.STRING_NILL;
		String exportDir = Constants.STRING_NILL;
		File downloadDir = null;
		String name = Constants.STRING_NILL;
		String filePath = Constants.STRING_NILL;
		File file = null;
		BufferedWriter bw = null;
		StringBuffer sb = null;
		JdbcResultsetHandler jdbcResultsetHandler = null;
	try {
		logger.debug("-- just entered --");
		
		if(promotionRedmDetailRowsId.getChildren().size() == 0) {
			MessageUtil.setMessage("No reports existed ","color:red","TOP");
			return;
		}
		
		type = exportFilterPromoDetailLbId.getSelectedItem().getLabel();
		userName = GetUser.getUserName();
		usersParentDirectory = (String)PropertyUtil.getPropertyValue("usersParentDirectory");
		exportDir = usersParentDirectory + "/" + userName + "/Coupon/" ;
		downloadDir = new File(exportDir);
		
		if(downloadDir.exists()){
			try {
				FileUtils.deleteDirectory(downloadDir);
				logger.debug(downloadDir.getName() + " is deleted");
			} catch (Exception e) {
				logger.error("Exception ::" , e);
				logger.debug(downloadDir.getName() + " is not deleted");
			}
		}
		if(!downloadDir.exists()){
			downloadDir.mkdirs();
		}
		
		if(type.contains("csv")){
			
			long coupId = coupObj.getCouponId();
			CouponCodes couponCodes = couponCodesDao.findById(coupId);
			name = couponCodes.getCouponCode();
			if(name.contains("/")) {
				
				name = name.replace("/", "_") ;
				
			}
				filePath = exportDir +  "Store_" + name + "_" +
				MyCalendar.calendarToString(coupObj.getCouponCreatedDate(), MyCalendar.FORMAT_YEARTOSEC, clientTimeZone);
			
				filePath = filePath + "_DiscountCodes.csv";
				logger.debug("Download File path : " + filePath);
				file = new File(filePath);
				bw = new BufferedWriter(new FileWriter(filePath));
								
				bw.write("\"Store\",\"Revenue\",\"No. Of Redemptions\""+" \r\n");
				
				
				String qry = "SELECT store_number, ROUND(SUM(tot_revenue),2) AS REVENUE, COUNT(coupon_code_id) as COUNT  " +
						"FROM coupon_codes WHERE orgId ="+GetUser.getUserObj().getUserOrganization().getUserOrgId()+" AND coupon_id ="+coupObj.getCouponId()+" " +
								"AND store_number IS NOT NULL AND coupon_code IS NOT NULL "+
								"GROUP BY store_number ORDER BY redeemed_on DESC";
				
				jdbcResultsetHandler = new JdbcResultsetHandler();
				jdbcResultsetHandler.executeStmt(qry);
				
				ResultSet resultSet = jdbcResultsetHandler.getResultSet();
				sb = new StringBuffer();
				if (resultSet != null && !resultSet.isAfterLast()) {
					while(resultSet.next()){
						sb.append(fetchStoreName(resultSet.getString(1))+",");
						sb.append(resultSet.getString(2)+",");
						sb.append(resultSet.getString(3));
						sb.append("\n");
					}
					
				}
				
				bw.write(sb.toString());
				bw.flush();
				bw.close();
				Filedownload.save(file, "text/csv");
		}
	} catch (Exception e) {
		logger.error("exception while initiating writer ",e);
		
	} finally{
		if(jdbcResultsetHandler!=null ) jdbcResultsetHandler.destroy();
		type = null;userName = null;usersParentDirectory = null;exportDir = null;downloadDir = null;name = null;
		filePath = null;file = null;bw = null;
		jdbcResultsetHandler = null;
		//System.gc();
	}
	long endTime = System.currentTimeMillis();
	logger.fatal("Time taken to export coupons is :::::::::::::::::::::::: " + (endTime-startTime));
}
	 private static Map<String, String> genFieldContMap = new LinkedHashMap<String, String>();
		
		static{
			
			genFieldContMap.put("Email", "c.email_id");
			genFieldContMap.put("Mobile", "c.mobile_phone");
			genFieldContMap.put("First Name", "c.first_name");
			genFieldContMap.put("Last Name", "c.last_name");
			genFieldContMap.put("Street", "c.address_one");
			genFieldContMap.put("Address Two", "c.address_two");
			genFieldContMap.put("City", "c.city");
			genFieldContMap.put("State", "c.state");
			genFieldContMap.put("Country", "c.country");
			genFieldContMap.put("ZIP", "c.zip");
			genFieldContMap.put("Customer ID", "c.external_id" );
			genFieldContMap.put("Gender", "c.gender");
			genFieldContMap.put("BirthDay", "c.birth_day");
			genFieldContMap.put("Anniversary", "c.anniversary_day");
			genFieldContMap.put("Home Store", "c.home_store");
			genFieldContMap.put("UDF1", "c.udf1");
			genFieldContMap.put("UDF2", "c.udf2");
			genFieldContMap.put("UDF3", "c.udf3");
			genFieldContMap.put("UDF4", "c.udf4");
			genFieldContMap.put("UDF5", "c.udf5");
			genFieldContMap.put("UDF6", "c.udf6");
			genFieldContMap.put("UDF7", "c.udf7");
			genFieldContMap.put("UDF8", "c.udf8");
			genFieldContMap.put("UDF9", "c.udf9");
			genFieldContMap.put("UDF10", "c.udf10");
			genFieldContMap.put("UDF11", "c.udf11");
			genFieldContMap.put("UDF12", "c.udf12");
			genFieldContMap.put("UDF13", "c.udf13");
			genFieldContMap.put("UDF14", "c.udf14");
			genFieldContMap.put("UDF15", "c.udf15");
		}
	
	
	
public Map<String, POSMapping> getOrderedMappingSet(List<POSMapping> mappingList) {
		
		
		Map<String,	POSMapping> orderedMap = new LinkedHashMap<String, POSMapping>();
		for (String custFldkey : genFieldContMap.keySet()) {
			
			//logger.debug("keySet is::"+custFldkey);
			
			
			for (POSMapping posMapping : mappingList) {
				
				if(posMapping.getCustomFieldName().equals(custFldkey)) {
					
					orderedMap.put(custFldkey, posMapping);
					break;
				}
				
			}
			
		}
		
		return orderedMap;
		
		
	}

	public void onSelect$srchLbId() {
		if(srchLbId.getSelectedItem().getValue().equals("RedeemedDate")) {
			RedeemedDateId.setVisible(true);
			IssuedDateId.setVisible(false);
			StoreNameId.setVisible(false);
			statusId.setVisible(false);
			filterByStatusId.setSelectedIndex(0);
			fromIssuedDateboxId.setText(Constants.STRING_NILL);
			toIssuedDateboxId.setText(Constants.STRING_NILL);
		}else if(srchLbId.getSelectedItem().getValue().equals("StoreName")) {
			RedeemedDateId.setVisible(false);
			IssuedDateId.setVisible(false);
			StoreNameId.setVisible(true);
			statusId.setVisible(false);
			filterByStatusId.setSelectedIndex(0);
		}else if(srchLbId.getSelectedItem().getValue().equals("IssuedDate")) {
			RedeemedDateId.setVisible(false);
			IssuedDateId.setVisible(true);
			StoreNameId.setVisible(false);
			statusId.setVisible(false);
			filterByStatusId.setSelectedIndex(0);
			fromRedeemedDateboxId.setText(Constants.STRING_NILL);
			toRedeemedDateboxId.setText(Constants.STRING_NILL);
		}else if(srchLbId.getSelectedItem().getValue().equals("status")) {
			statusId.setVisible(true);
			RedeemedDateId.setVisible(false);
			IssuedDateId.setVisible(false);
			StoreNameId.setVisible(false);
			
		}
	}

	public void onClick$getReportBtnId() {
	
		//couponListBottomPagingId.setActivePage(0);
		Calendar  start = null;
		 Calendar end = null;
		if(RedeemedDateId.isVisible() && fromRedeemedDateboxId!=null && toRedeemedDateboxId!=null) {
			if(fromRedeemedDateboxId.getText()!=null && !fromRedeemedDateboxId.getText().isEmpty()
					&& toRedeemedDateboxId.getText()!=null && !toRedeemedDateboxId.getText().isEmpty()) {
				 logger.info("entred fromRedeemedDatebox");
				 start = fromRedeemedDateboxId.getClientValue();
				 end = toRedeemedDateboxId.getClientValue();
			}else {
				MessageUtil.setMessage("Please specify the dates","color:red","TOP");
				return;
			}
		}
		if(IssuedDateId.isVisible() && fromIssuedDateboxId!=null && toIssuedDateboxId!=null) {
			if(fromIssuedDateboxId.getText()!=null && !fromIssuedDateboxId.getText().isEmpty()
					&& toIssuedDateboxId.getText()!=null && !toIssuedDateboxId.getText().isEmpty()) {
				logger.info("entered fromIssuedDatebox");
				 start = fromIssuedDateboxId.getClientValue();
				 end = toIssuedDateboxId.getClientValue();
			}else {
				MessageUtil.setMessage("Please specify the dates","color:red","TOP");
				return;
			}
		}
		if(start!=null && end!=null) {
			if(end.before(start)) {
				MessageUtil.setMessage("'From' date cannot be later than 'To' date.", "red");
				return;
			}
		}
		//int totalSize = couponCodesDao.findTotCountCouponCodesWithFilter(coupObj.getCouponId(),getFilterData());
		 setPageCount();
		 getCouponCodes(0, Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel()), getFilterData());
	}
	
	private static final String SEPARATOR = ",";
	public Map<String,String> getFilterData(){
		Map<String,String> searchData = new HashMap<String,String>();
		if(StoreNameId.isVisible() && searchByStoreNameId.getSelectedItem()!=null) {
			if( searchByStoreNameId.getSelectedIndex() == 0) {
				StringBuilder storeIdBuilder = new StringBuilder();
				 List<Listitem> storeIdes = searchByStoreNameId.getItems();
				 for(Listitem store:storeIdes) {
					 if(!store.getValue().equals("All")) {
						 storeIdBuilder.append(store.getValue().toString());
						 storeIdBuilder.append(SEPARATOR);
					 }
				 }
				 String csv = storeIdBuilder.toString();
				 if(csv!=null && !csv.isEmpty()) {
					 csv = storeIdBuilder.substring(0, storeIdBuilder.length() - ",".length());
				 }else {
					 csv = null;
				 }
				searchData.put("storeName_all", csv);
			}else {
				searchData.put("storeName", searchByStoreNameId.getSelectedItem().getValue());
			}
		}else if(RedeemedDateId.isVisible() && fromRedeemedDateboxId.getText()!=null && !fromRedeemedDateboxId.getText().isEmpty()
				&& toRedeemedDateboxId.getText()!=null && !toRedeemedDateboxId.getText().isEmpty()) {
			StringBuilder redeemedDate = new StringBuilder();
			String fromDate = MyCalendar.calendarToString(getStartDate(fromRedeemedDateboxId), MyCalendar.FORMAT_DATETIME_STYEAR);
			String toDate = MyCalendar.calendarToString(getEndDate(toRedeemedDateboxId), MyCalendar.FORMAT_DATETIME_STYEAR);
			searchData.put("RedeemedDate", redeemedDate.append(fromDate).append("T").append(toDate).toString());
		}else if(IssuedDateId.isVisible() && fromIssuedDateboxId.getText()!=null && !fromIssuedDateboxId.getText().isEmpty()
				&& toIssuedDateboxId.getText()!=null && !toIssuedDateboxId.getText().isEmpty() ) {
			StringBuilder issuedDate = new StringBuilder();
			String fromDate = MyCalendar.calendarToString(getStartDate(fromIssuedDateboxId), MyCalendar.FORMAT_DATETIME_STYEAR);
			String toDate = MyCalendar.calendarToString(getEndDate(toIssuedDateboxId), MyCalendar.FORMAT_DATETIME_STYEAR);
			searchData.put("IssuedDate", issuedDate.append(fromDate).append("T").append(toDate).toString());
		}else if(statusId.isVisible() && filterByStatusId.getSelectedItem()!=null && filterByStatusId.getSelectedItem().getValue()!=null) {
			searchData.put("promotionStatus", filterByStatusId.getSelectedItem().getValue());
		}
		return searchData;
	}
	
	
	public void onClick$resetAnchId() {
		int tempCount = Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel());
		couponListBottomPagingId.setPageSize(tempCount);
		couponListBottomPagingId.setActivePage(0);
		srchLbId.setSelectedIndex(0);
		onSelect$srchLbId();
		fromRedeemedDateboxId.setText(null);
		toRedeemedDateboxId.setText(null);
		fromIssuedDateboxId.setText(null);
		toIssuedDateboxId.setText(null);
		filterByStatusId.setSelectedIndex(0);
		searchByStoreNameId.setSelectedIndex(0);
		getCouponCodes(0, tempCount, getFilterData());
		int totalSize = couponCodesDao.findTotCountCouponCodesWithFilter(coupObj.getCouponId(),getFilterData());
		 couponListBottomPagingId.setTotalSize(totalSize);
		
	}
	
	
private void setDefaultStores() {
		List<String> ltyStoreList = couponCodesDao.getStoreByUserId(users.getUserId());
		List<OrganizationStores> storeIdList = organizationStoresDao.findAllStores(users.getUserOrganization().getUserOrgId());
		if (storeIdList == null || storeIdList.size() == 0)return;
		Listitem storeItem = null;
		outer: for (String storeName : ltyStoreList) {
		for (OrganizationStores org : storeIdList) {
			if (org.getHomeStoreId().equalsIgnoreCase(storeName)) {
				storeItem = new Listitem(org.getStoreName(), storeName);
                storeItem.setParent(searchByStoreNameId);
				continue outer;
			}
		}
		storeItem = new Listitem("Store ID " + storeName, storeName);
		storeItem.setParent(searchByStoreNameId);
	}

}

	private void setPageCount() {
		int totalSize = couponCodesDao.findTotCountCouponCodesWithFilter(coupObj.getCouponId(),getFilterData());
		couponListBottomPagingId.setTotalSize(totalSize);
		couponListBottomPagingId.setActivePage(0);
		couponListBottomPagingId.addEventListener("onPaging", this);
		couponListBottomPagingId.setAttribute("onPaging", "couponDetail");
		//int tempCount = Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel());
		//couponListBottomPagingId.setPageSize(tempCount);
	} 

} // class
