package org.mq.marketer.campaign.controller.report;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.TimeZone;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.Coupons;
import org.mq.marketer.campaign.beans.MailingList;
import org.mq.marketer.campaign.beans.SuppressedContacts;
import org.mq.marketer.campaign.beans.Unsubscribes;
import org.mq.marketer.campaign.beans.UserActivities;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.custom.MyDatebox;
import org.mq.marketer.campaign.dao.MailingListDao;
import org.mq.marketer.campaign.dao.OrganizationStoresDao;
import org.mq.marketer.campaign.dao.RetailProSalesDao;
import org.mq.marketer.campaign.dao.UserActivitiesDaoForDML;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PageListEnum;
import org.mq.marketer.campaign.general.PageUtil;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Redirect;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.event.PagingEvent;

public class OptIntelReportsController extends GenericForwardComposer {
	
	private Rows promoCodeRowsId,promotionRedmRowsId,promotionRedmDetailRowsId,storeRedmRowsId,storeRedmDetailRowsId;
	private RetailProSalesDao retailProsalesDao;
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	private Grid promoCodeGridId,storeRedmDetailGridId,storeRedmGridId,promotionRedmDetailGridId,promotionRedmGridId;
	private Paging promoCodePagingId,promoPagingId,promoDetailPagingId,storePagingId,storeDetailPagingId;
	private Listbox memberPerPageLBId,memberPerPagePromoLBId,memberPerPagePromoDetailLBId,memberPerPageStoreLBId,memberPerPageStoreDetailLBId;
	private MailingListDao mailingListDao;
	private Label storeID,promoID;
	private Div pagingPromoDivId,pagingStoreDivId,pagingPromoDetailDivId,pagingStoreDetailDivId,backToOverviewDivId;
	private Button exportBtnPromoId,exportBtnStoreId,exportBtnPromoDetailId,exportBtnStoreDetailId,backBtnId;
	private String currentStore,currentPromoCode;
	private static final String PROMO = "promo"; 
	private static final String PROMODETAIL = "promoDetail"; 
	private static final String STORE = "store"; 
	private static final String STOREDETAIL = "storeDetail"; 
	
	private OrganizationStoresDao organizationStoresDao;
	private UserActivitiesDaoForDML userActivitiesDaoForDML = null;
	private List<Map<String, Object>> storeNumberNameMapList;
	private MyDatebox fromDateboxId;
	private MyDatebox toDateboxId;
	
	private String fromDateStr;
	private String toDateStr;
	
	private Label resetAnchId;
	
	MailingList mList = null;
	int totalCount = 0;
	TimeZone tz;
//	private Users users= null;
	
	private Users users = GetUser.getUserObj();
	public OptIntelReportsController() { 
		
		String style = "font-weight:bold;font-size:15px;color:#313031;font-family:Arial,Helvetica,sans-serif;align:left";
		//PageUtil.setHeader("Promotional Redemption","",style,true);
		PageUtil.setHeader("In-Store Promotions Report","",style,true);
		
		
		
	}
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		
		super.doAfterCompose(comp);
		
		retailProsalesDao = (RetailProSalesDao)SpringUtil.getBean("retailProSalesDao");
		mailingListDao = (MailingListDao)SpringUtil.getBean("mailingListDao");
		organizationStoresDao =	(OrganizationStoresDao)SpringUtil.getBean("organizationStoresDao");
		userActivitiesDaoForDML = (UserActivitiesDaoForDML)SpringUtil.getBean("userActivitiesDaoForDML");
		//defaultSettings();
		
		
		tz =(TimeZone)Sessions.getCurrent().getAttribute("clientTimeZone"); 
		
		mList = mailingListDao.findPOSMailingList(users);
		if(mList == null) return;
		
		Long userId = GetUser.getUserObj().getUserId();
		if(userActivitiesDaoForDML!=null) {
		UserActivities userActivity = new UserActivities("Visited In-store promotions report page", "Visited pages", Calendar.getInstance(),userId );
		userActivitiesDaoForDML.saveOrUpdate(userActivity);
		}
		
		
		storeNumberNameMapList  = organizationStoresDao.findStoreNumberNameMapList(GetUser.getUserObj().getUserOrganization().getUserOrgId());
		
		fromDateStr = Constants.STRING_NILL;
		toDateStr = Constants.STRING_NILL;
		
		Calendar cal = MyCalendar.getNewCalendar();
		toDateboxId.setValue(cal);
		logger.debug("ToDate (server) :" + cal);
		//cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) - 1);
		cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) - 3);
		cal.set(Calendar.DATE, cal.get(Calendar.DATE) + 1);
		logger.debug("FromDate (server) :" + cal);
		fromDateboxId.setValue(cal);
		
	    validateAndSetSearchDates();
		
		totalCount = retailProsalesDao.findTotalCount(users.getUserId(),fromDateStr,toDateStr);
		//promoCodePagingId.addEventListener("onPaging", this);
		
		storePagingId.addEventListener("onPaging", this);
		storePagingId.setAttribute("onPaging", "store");
		storeDetailPagingId.addEventListener("onPaging",this);
		storeDetailPagingId.setAttribute("onPaging", "storeDetail");
	
		promoPagingId.addEventListener("onPaging", this);
		promoPagingId.setAttribute("onPaging", "promo");
		promoDetailPagingId.addEventListener("onPaging", this);
		promoDetailPagingId.setAttribute("onPaging", "promoDetail");
		
		
		
		/*fromDateboxId.setText(Constants.STRING_NILL);
		toDateboxId.setText(Constants.STRING_NILL);*/
		
		
		
		int promoListSize = retailProsalesDao.findTotalCount(users.getUserId(),fromDateStr,toDateStr);
		int storeListSize = retailProsalesDao.findTotalCountForStore(users.getUserId(),fromDateStr,toDateStr);
		setSizeOfPageAndFillStoreAndPromoGrids(promoListSize,storeListSize);
		
		logger.info("pos mailing list is not null...");
		
		//promoCodePagingId.setTotalSize(totalCount);
		backToOverviewDivId.setVisible(false);
		storeRedmDetailGridId.setVisible(false);
		promotionRedmDetailGridId.setVisible(false);
		
		//fillPromoCodeLBBySize(0,5);
		//promoCodeGridId.setPaginal(promoCodePagingId);
		
	} //doAfterCompose

	
	
	public void onSelect$memberPerPageLBId() {
		
		try {
			
			logger.info("just enter here");
		
			String selectStr = memberPerPageLBId.getSelectedItem().getLabel();
			int pNo = Integer.parseInt(selectStr);
			
			promoCodePagingId.setPageSize(pNo);
				
			fillPromoCodeLBBySize(0 ,pNo);
			
		} catch (NumberFormatException e) {
			logger.error("Exception ::" , e);
		}
	} // onClick$memberPerPageLBId
	
  public void onSelect$memberPerPagePromoLBId() {
		
		try {
			
			logger.info("just enter here");
			
			
			String selectStr = memberPerPagePromoLBId.getSelectedItem().getLabel();
			int pNo = Integer.parseInt(selectStr);
			
			promoPagingId.setPageSize(pNo);
	    	promoPagingId.setActivePage(0);
				
			fillPromotionRedemption(0,pNo,orderby_promotion_colName,desc_Asc);
			
		} catch (NumberFormatException e) {
			logger.error("Exception ::" , e);
		}
	} // onClick$memberPerPageLBId
  
  public void onSelect$memberPerPagePromoDetailLBId() {
		
		try {
			logger.info("just enter here");
			
			
			String selectStr = memberPerPagePromoDetailLBId.getSelectedItem().getLabel();
			int pNo = Integer.parseInt(selectStr);
			
			promoDetailPagingId.setPageSize(pNo);
			promoDetailPagingId.setActivePage(0);	
			fillPromoRedemptionDetail(0,pNo,currentPromoCode);
			
		} catch (NumberFormatException e) {
			logger.error("Exception ::" , e);
		}
	} // onClick$memberPerPageLBId
  
  public void onSelect$memberPerPageStoreLBId() {
		
		try {
			
			logger.info("just enter here");
			
			
			String selectStr = memberPerPageStoreLBId.getSelectedItem().getLabel();
			int pNo = Integer.parseInt(selectStr);
			
			storePagingId.setPageSize(pNo);
			storePagingId.setActivePage(0);	
				
			fillStoreRedemption(0,pNo,orderby__store_colName,desc_Asc);
			
		} catch (NumberFormatException e) {
			logger.error("Exception ::" , e);
		}
	} // onClick$memberPerPageLBId
	
  public void onSelect$memberPerPageStoreDetailLBId() {
		
		try {
			
			logger.info("just enter here");
			
			String selectStr = memberPerPageStoreDetailLBId.getSelectedItem().getLabel();
			int pNo = Integer.parseInt(selectStr);
			
			storeDetailPagingId.setPageSize(pNo);
			storeDetailPagingId.setActivePage(0);	
				
			fillStoreRedemptionDetail(0,pNo,currentStore);
			
		} catch (NumberFormatException e) {
			logger.error("Exception ::" , e);
		}
	} // onClick$memberPerPageLBId
	
	private void fillPromoCodeLBBySize(int fromInt, int lbxLenght) {

		try {
			Components.removeAllChildren(promoCodeRowsId);
			NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
			/*int count =  promoCodeRowsId.getItemCount();
			
			for(; count>0; count--) {
				promoCodeRowsId.removeAt(count-1);
			}*/
			
			List<Map<String, Object>> promoObjArrList  = retailProsalesDao.findPromocodeList(users.getUserId(), fromInt, lbxLenght);
			
			if(promoObjArrList == null || promoObjArrList.size() == 0 ) {
				 logger.info(" *** No promo code data exists for this user");
				 return;
			 }
			
			 logger.info(">>> PromoObjArrList size is :"+promoObjArrList.size());
			 Row tempRow = null;
			 
			 for (Map<String, Object> eachMap: promoObjArrList) {
				 
				 tempRow  = new Row();
				 //SELECT promo_code, ROUND(SUM((quantity*sales_price)+tax),2) AS REVENUE , COUNT(customer_id) AS COUNT
				 //PromoCode
				 Label PromoCodeLbl = new Label();
				 if(eachMap.containsKey("promo_code") && eachMap.get("promo_code") != null) {
					 
					 PromoCodeLbl.setValue(eachMap.get("promo_code").toString());
				 }
				 PromoCodeLbl.setParent(tempRow);
				
				 //Revenue
				 Label revenueLbl = new Label();
				 if(eachMap.containsKey("REVENUE") && eachMap.get("REVENUE") != null) {
					 revenueLbl.setValue(numberFormat.format(eachMap.get("REVENUE")));
					 //revenueLbl.setValue(eachMap.get("REVENUE").toString());
				 }
				 revenueLbl.setParent(tempRow);
				 
				 //No. of customer
				 Label totalCustmerLbl = new Label();
				 if(eachMap.containsKey("COUNT") && eachMap.get("COUNT") != null) {
					 totalCustmerLbl.setValue(numberFormat.format(eachMap.get("COUNT")));
					 //totalCustmerLbl.setValue(eachMap.get("COUNT").toString());
				 }
				 totalCustmerLbl.setParent(tempRow);
				 
				 tempRow.setParent(promoCodeRowsId);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
		}
	} // fildPromoCodeListBySize
	
	
	
	@Override
	public void onEvent(Event event) throws Exception {
		// TODO Auto-generated method stub
		super.onEvent(event);
		
		if(event.getTarget() instanceof Paging) {
			
			Paging paging = (Paging) event.getTarget();
			int desiredPage = paging.getActivePage();
			logger.info("desiredPage value is ::" +desiredPage);
			int pSize = paging.getPageSize();
			int ofs = desiredPage * pSize;
			logger.info("pSize============="+pSize );
			logger.info("ofs is====="+ofs);
			//fillPromoCodeLBBySize(ofs, pSize);
			
			if(paging.getAttribute("onPaging").equals("promo")) {
				
				this.promoPagingId.setActivePage(desiredPage);
				fillPromotionRedemption(ofs, pSize,orderby_promotion_colName,desc_Asc);
				
			}else if(paging.getAttribute("onPaging").equals("store")) {
				
				this.storePagingId.setActivePage(desiredPage);
				fillStoreRedemption(ofs, pSize,orderby__store_colName,desc_Asc);
				
			}else if(paging.getAttribute("onPaging").equals("promoDetail")) {
				
				this.promoDetailPagingId.setActivePage(desiredPage);
				fillPromoRedemptionDetail(ofs, pSize, currentPromoCode);
				
			}else if(paging.getAttribute("onPaging").equals("storeDetail")) {
				
				this.storeDetailPagingId.setActivePage(desiredPage);
				fillStoreRedemptionDetail(ofs, pSize, currentStore);
				
			}
			//fillPromoCodeLBBySize(ofs, pSize);
			
		}else if(event.getTarget() instanceof Label ){

			Label tempLable = (Label)event.getTarget();
			Row tempRow = (Row)tempLable.getParent();
			String PromoCodeOrStore = (String)tempRow.getValue();
			
			logger.debug("PromoCodeOrStore is  :: "+PromoCodeOrStore);
			
			if(PromoCodeOrStore.equals("promo_code")){
				storeID.setVisible(false);
				promoID.setVisible(true);
				currentPromoCode = tempLable.getValue();
				
				promoID.setValue("Store redemption of promotion : "+currentPromoCode);
				
				
				hideAndShowSomeStuffs("promo_code",true);
				int promoListSize = retailProsalesDao.findTotalCountOfStoreRelatedToPromo(GetUser.getUserId(), tempLable.getValue(),fromDateStr,toDateStr);
				setSizeOfPageAndFillPromoDetailsGrid(promoListSize);
				//showPromoDetails(tempLable.getValue());
				
				
			}else if(PromoCodeOrStore.equals("store_number")){
				
				storeID.setVisible(true);
				promoID.setVisible(false);
				currentStore =(String) tempLable.getAttribute("original value");
				
				//System.out.println("currentStore>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>))))))))))))))))))"+currentStore);
				
				storeID.setValue("Promotions redeemed at store : "+tempLable.getValue());
				
				hideAndShowSomeStuffs("store_number",true);
				int storeListSize = retailProsalesDao.findTotalCountOfPromoRelatedToStore(GetUser.getUserId(), currentStore,fromDateStr,toDateStr);
				setSizeOfPageAndFillStoreDetailsGrid(storeListSize);
				//showStoreDetails(tempLable.getValue());
			}
			
			
			
		}
	}
	
	private int fillPromotionRedemption(int fromInt, int lbxLenght,String orderby_colName,String desc_Asc) {

		try {
			Components.removeAllChildren(promotionRedmRowsId);
			int size;
			/*int count =  promoCodeRowsId.getItemCount();
			
			for(; count>0; count--) {
				promoCodeRowsId.removeAt(count-1);
			}*/
			
			List<Map<String, Object>> promoObjArrList  = retailProsalesDao.findPromotionalRedemptionList(users.getUserId(), fromInt, lbxLenght,"",fromDateStr, toDateStr,orderby_colName,desc_Asc);
			
			if(promoObjArrList == null || promoObjArrList.size() == 0 ) {
				 size = 0;
				 logger.info(" *** No promo code data exists for this user");
				 return size;
			 }
			
			 logger.info(">>> PromoObjArrList size is :"+promoObjArrList.size());
			 size = promoObjArrList.size();
			 Row tempRow = null;
			 
			 for (Map<String, Object> eachMap: promoObjArrList) {
				 
				 tempRow  = new Row();
				 //SELECT promo_code, ROUND(SUM((quantity*sales_price)+tax),2) AS REVENUE , COUNT(customer_id) AS COUNT
				 //PromoCode
				 Label PromoCodeLbl = new Label();
				 if(eachMap.containsKey("promo_code") && eachMap.get("promo_code") != null) {
					 PromoCodeLbl.setValue(eachMap.get("promo_code").toString());
					 PromoCodeLbl.setStyle("cursor:pointer;color:blue;text-decoration: underline;");
					 PromoCodeLbl.addEventListener("onClick", this);
				 }
				 PromoCodeLbl.setParent(tempRow);
				
				 //Revenue
				 Label revenueLbl = new Label();
				 if(eachMap.containsKey("REVENUE") && eachMap.get("REVENUE") != null) {
					 revenueLbl.setValue(eachMap.get("REVENUE").toString());
				 }
				 revenueLbl.setParent(tempRow);
				 
				 //No. of customer
				 Label totalCustmerLbl = new Label();
				 if(eachMap.containsKey("COUNT") && eachMap.get("COUNT") != null) {
					 
					 totalCustmerLbl.setValue(eachMap.get("COUNT").toString());
				 }
				 totalCustmerLbl.setParent(tempRow);
				 
				 if(eachMap.containsKey("sales_date") && eachMap.get("sales_date") != null) {
					 (new Label(eachMap.get("sales_date").toString())).setParent(tempRow);
				 }
				 
				 tempRow.setValue("promo_code");
				 
				 tempRow.setParent(promotionRedmRowsId);
				 
				
			}
			 return size;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
			return 0;
		}
	} // fillPromotionRedemption
	
	private int fillPromoRedemptionDetail(int fromInt, int lbxLenght, String promoCode) {

		try {
			Components.removeAllChildren(promotionRedmDetailRowsId);
			int size;
			
			/*int count =  promoCodeRowsId.getItemCount();
			
			for(; count>0; count--) {
				promoCodeRowsId.removeAt(count-1);
			}*/
			
			List<Map<String, Object>> promoObjArrList  = retailProsalesDao.findStoreRedemptionList(users.getUserId(), fromInt, lbxLenght, promoCode,fromDateStr,toDateStr,"sales_date","desc");// sm change
			
			if(promoObjArrList == null || promoObjArrList.size() == 0 ) {
				 
				 logger.info(" *** No promo code data exists for this user");
				 return 0;
			 }
			
			 logger.info(">>> PromoObjArrList size is :"+promoObjArrList.size());
			 size = promoObjArrList.size();
			 Row tempRow = null;
			 String storeName;
			 
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
					 revenueLbl.setValue(eachMap.get("REVENUE").toString());
				 }
				 revenueLbl.setParent(tempRow);
				 
				 //No. of customer
				 Label totalCustmerLbl = new Label();
				 if(eachMap.containsKey("COUNT") && eachMap.get("COUNT") != null) {
					 
					 totalCustmerLbl.setValue(eachMap.get("COUNT").toString());
				 }
				 totalCustmerLbl.setParent(tempRow);
				 
				 if(eachMap.containsKey("sales_date") && eachMap.get("sales_date") != null) {
					 (new Label(eachMap.get("sales_date").toString())).setParent(tempRow);
				 }
				 
				 tempRow.setParent(promotionRedmDetailRowsId);
			}
			 
			return size;  
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
			return 0;
		}
	} //fillPromoRedemptionDetail
	
	private int fillStoreRedemption(int fromInt, int lbxLenght,String orderby_colName,String desc_Asc) {

		try {
			int size;
			Components.removeAllChildren(storeRedmRowsId);
			
			/*int count =  promoCodeRowsId.getItemCount();
			
			for(; count>0; count--) {
				promoCodeRowsId.removeAt(count-1);
			}*/
			
			List<Map<String, Object>> promoObjArrList  = retailProsalesDao.findStoreRedemptionList(users.getUserId(), fromInt, lbxLenght,"",fromDateStr,toDateStr,orderby_colName,desc_Asc); //-- sm change
			
			if(promoObjArrList == null || promoObjArrList.size() == 0 ) {
				 logger.info(" *** No promo code data exists for this user");
				 return 0;
			 }
			
			 logger.info(">>> PromoObjArrList size is :"+promoObjArrList.size());
			 size = promoObjArrList.size();
			 Row tempRow = null;
			 String storeName;
			 
			 for (Map<String, Object> eachMap: promoObjArrList) {
				 
				 tempRow  = new Row();
				 //SELECT promo_code, ROUND(SUM((quantity*sales_price)+tax),2) AS REVENUE , COUNT(customer_id) AS COUNT
				 //PromoCode
				 Label PromoCodeLbl = new Label();
				 if(eachMap.containsKey("store_number") && eachMap.get("store_number") != null) {
					 storeName = fetchStoreName(eachMap.get("store_number").toString());
					 //PromoCodeLbl.setValue(eachMap.get("store_number").toString());
					 PromoCodeLbl.setValue(storeName);
					 PromoCodeLbl.setStyle("cursor:pointer;color:blue;text-decoration: underline;");
					 PromoCodeLbl.addEventListener("onClick", this);
					 PromoCodeLbl.setAttribute("original value", eachMap.get("store_number").toString());
				 }
				 PromoCodeLbl.setParent(tempRow);
				
				 //Revenue
				 Label revenueLbl = new Label();
				 if(eachMap.containsKey("REVENUE") && eachMap.get("REVENUE") != null) {
					 revenueLbl.setValue(eachMap.get("REVENUE").toString());
				 }
				 revenueLbl.setParent(tempRow);
				 
				 //No. of customer
				 Label totalCustmerLbl = new Label();
				 if(eachMap.containsKey("COUNT") && eachMap.get("COUNT") != null) {
					 
					 totalCustmerLbl.setValue(eachMap.get("COUNT").toString());
				 }
				 totalCustmerLbl.setParent(tempRow);
				 
				 if(eachMap.containsKey("sales_date") && eachMap.get("sales_date") != null) {
					 (new Label(eachMap.get("sales_date").toString())).setParent(tempRow);
				 }
				 tempRow.setValue("store_number");
				 tempRow.setParent(storeRedmRowsId);
			}
			 return size;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
			return 0;
		}
	} // fillStoreRedemption
	
	private int fillStoreRedemptionDetail(int fromInt, int lbxLenght, String storeNumber) {

		try {
			int size;
			Components.removeAllChildren(storeRedmDetailRowsId);
			
			/*int count =  promoCodeRowsId.getItemCount();
			
			for(; count>0; count--) {
				promoCodeRowsId.removeAt(count-1);
			}*/
			
			List<Map<String, Object>> promoObjArrList  = retailProsalesDao.findPromotionalRedemptionList(users.getUserId(), fromInt, lbxLenght, storeNumber,fromDateStr,toDateStr,"sales_date","desc");
			
			if(promoObjArrList == null || promoObjArrList.size() == 0 ) {
				 logger.info(" *** No promo code data exists for this user");
				 return 0;
			 }
			
			 logger.info(">>> PromoObjArrList size is :"+promoObjArrList.size());
			 size = promoObjArrList.size();
			 Row tempRow = null;
			 
			 for (Map<String, Object> eachMap: promoObjArrList) {
				 
				 tempRow  = new Row();
				 //SELECT promo_code, ROUND(SUM((quantity*sales_price)+tax),2) AS REVENUE , COUNT(customer_id) AS COUNT
				 //PromoCode
				 Label PromoCodeLbl = new Label();
				 if(eachMap.containsKey("promo_code") && eachMap.get("promo_code") != null) {
					 PromoCodeLbl.setValue(eachMap.get("promo_code").toString());
				 }
				 PromoCodeLbl.setParent(tempRow);
				
				 //Revenue
				 Label revenueLbl = new Label();
				 if(eachMap.containsKey("REVENUE") && eachMap.get("REVENUE") != null) {
					 revenueLbl.setValue(eachMap.get("REVENUE").toString());
				 }
				 revenueLbl.setParent(tempRow);
				 
				 //No. of customer
				 Label totalCustmerLbl = new Label();
				 if(eachMap.containsKey("COUNT") && eachMap.get("COUNT") != null) {
					 
					 totalCustmerLbl.setValue(eachMap.get("COUNT").toString());
				 }
				 totalCustmerLbl.setParent(tempRow);
				 
				 if(eachMap.containsKey("sales_date") && eachMap.get("sales_date") != null) {
					 (new Label(eachMap.get("sales_date").toString())).setParent(tempRow);
				 }
				 
				 tempRow.setParent(storeRedmDetailRowsId);
			}
			 
			 return size;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
			return 0;
		}
	} // fillStoreRedemptionDetail
	
	private void showPromoDetails(String promoCode){
		storeRedmDetailGridId.setVisible(false);
		storeRedmGridId.setVisible(false);
		promotionRedmGridId.setVisible(false);
		
		promotionRedmDetailGridId.setVisible(true);
		
		fillPromoRedemptionDetail(0,5,promoCode);
	}
	
	private void showStoreDetails(String storeNumber){
		storeRedmDetailGridId.setVisible(true);
		storeRedmGridId.setVisible(false);
		promotionRedmGridId.setVisible(false);
		
		promotionRedmDetailGridId.setVisible(false);
		
		fillStoreRedemptionDetail(0,5,storeNumber);
	}
	
	private void hideAndShowSomeStuffs(String promoCodeOrStore, boolean overviewToDetail){
		
		if(promoCodeOrStore.equals("promo_code")){
			
			if(overviewToDetail){
				
				storeRedmDetailGridId.setVisible(false);
				storeRedmGridId.setVisible(false);
				promotionRedmGridId.setVisible(false);
				promotionRedmDetailGridId.setVisible(true);
				
				pagingPromoDivId.setVisible(false);
				pagingStoreDivId.setVisible(false);
				pagingPromoDetailDivId.setVisible(true);
				pagingStoreDetailDivId.setVisible(false);
				backToOverviewDivId.setVisible(true);
				
			}else{
				
				storeRedmDetailGridId.setVisible(false);
				storeRedmGridId.setVisible(true);
				promotionRedmGridId.setVisible(true);
				promotionRedmDetailGridId.setVisible(false);
				
				pagingPromoDivId.setVisible(true);
				pagingStoreDivId.setVisible(true);
				pagingPromoDetailDivId.setVisible(false);
				pagingStoreDetailDivId.setVisible(false);
				backToOverviewDivId.setVisible(false);
				
				storeID.setVisible(true);
				promoID.setVisible(true);
				
			}
			
		}else if(promoCodeOrStore.equals("store_number")){
			
			if(overviewToDetail){
				
				storeRedmDetailGridId.setVisible(true);
				storeRedmGridId.setVisible(false);
				promotionRedmGridId.setVisible(false);
				promotionRedmDetailGridId.setVisible(false);
				
				pagingPromoDivId.setVisible(false);
				pagingStoreDivId.setVisible(false);
				pagingPromoDetailDivId.setVisible(false);
				pagingStoreDetailDivId.setVisible(true);
				backToOverviewDivId.setVisible(true);
				
			}else{
				
				storeRedmDetailGridId.setVisible(false);
				storeRedmGridId.setVisible(true);
				promotionRedmGridId.setVisible(true);
				promotionRedmDetailGridId.setVisible(false);
				
				pagingPromoDivId.setVisible(true);
				pagingStoreDivId.setVisible(true);
				pagingPromoDetailDivId.setVisible(false);
				pagingStoreDetailDivId.setVisible(false);
				backToOverviewDivId.setVisible(false);
				
				storeID.setVisible(true);
				promoID.setVisible(true);
				
			}
		}
	}
	
	public void onClick$backBtnId(){
		hideAndShowSomeStuffs("promo_code",false);
		
		fromDateboxId.setText(Constants.STRING_NILL);
		toDateboxId.setText(Constants.STRING_NILL);
		fromDateStr = Constants.STRING_NILL;
		toDateStr = Constants.STRING_NILL;
		
		Calendar cal = MyCalendar.getNewCalendar();
		toDateboxId.setValue(cal);
		logger.debug("ToDate (server) :" + cal);
		//cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) - 1);
		cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) - 3);
		cal.set(Calendar.DATE, cal.get(Calendar.DATE) + 1); 
		logger.debug("FromDate (server) :" + cal);
		fromDateboxId.setValue(cal);
		
		validateAndSetSearchDates();
		
		
		
		int promoListSize = retailProsalesDao.findTotalCount(users.getUserId(),fromDateStr,toDateStr);
		int storeListSize = retailProsalesDao.findTotalCountForStore(users.getUserId(),fromDateStr,toDateStr);

		storePagingId.setTotalSize(storeListSize);
		promoPagingId.setTotalSize(promoListSize);
		storePagingId.setActivePage(0);
		promoPagingId.setActivePage(0);
		
		promoID.setValue("Promotions Redemptions");
		storeID.setValue("Store Redemptions");
		promoPagingId.setPageSize(5);
		promoPagingId.setActivePage(0);
		memberPerPagePromoLBId.setSelectedIndex(0);
		fillPromotionRedemption(0,5,"sales_date","desc");
		
		storePagingId.setPageSize(5);
		storePagingId.setActivePage(0);
		memberPerPageStoreLBId.setSelectedIndex(0);
		fillStoreRedemption(0,5,"sales_date","desc");
		
	}
	
	 private void setSizeOfPageAndFillStoreAndPromoGrids(int promoListSize, int storeListSize) {
	    	
	    	logger.debug("promoListSize is    "+promoListSize);
	    	logger.debug("storeListSize is    "+storeListSize);
	    	
	    	
	    	storePagingId.setTotalSize(storeListSize);
	    	promoPagingId.setTotalSize(promoListSize);
	    	storePagingId.setActivePage(0);
	    	promoPagingId.setActivePage(0);
	    	
	    	
	    	String selectStr = memberPerPagePromoLBId.getSelectedItem().getLabel();
			int pNo = Integer.parseInt(selectStr);
			
			fillPromotionRedemption(0, pNo,orderby_promotion_colName,desc_Asc);
			
			selectStr = memberPerPageStoreLBId.getSelectedItem().getLabel();
			pNo = Integer.parseInt(selectStr);
			fillStoreRedemption(0, pNo,orderby__store_colName,desc_Asc);
			
	    }
	 
	 private void setSizeOfPageAndFillStoreDetailsGrid(int storeListSize) {
	    	
	    	logger.debug("storeListSize is    "+storeListSize);
	    	
	    	
	    	storeDetailPagingId.setTotalSize(storeListSize);
	    	storeDetailPagingId.setActivePage(0);
	    	
	    	
	    	String selectStr = memberPerPageStoreDetailLBId.getSelectedItem().getLabel();
			int pNo = Integer.parseInt(selectStr);
			
			fillStoreRedemptionDetail(0, pNo, currentStore);
			
			
			
	    }
	 private void setSizeOfPageAndFillPromoDetailsGrid(int promoListSize) {
	    	

	    	logger.debug("promoListSize is    "+promoListSize);
	    	
	    	
	    	promoDetailPagingId.setTotalSize(promoListSize);
	    	promoDetailPagingId.setActivePage(0);
	    	
	    	
	    	String selectStr = memberPerPagePromoDetailLBId.getSelectedItem().getLabel();
			int pNo = Integer.parseInt(selectStr);
			
			fillPromoRedemptionDetail(0, pNo, currentPromoCode);
			
	    }
	 ///------------------wrking-------------------
	
	 public void onClick$exportBtnPromoId(){
		 export(PROMO);
	 }
     public void onClick$exportBtnPromoDetailId(){
    	 export(PROMODETAIL);
	 }
     public void onClick$exportBtnStoreId(){
    	 export(STORE);
     }
     public void onClick$exportBtnStoreDetailId(){
    	 export(STOREDETAIL);
     }
    
	
	 private void export(String whichGridToExport){

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
			String exportDir = usersParentDirectory + "/" + userName + "/OptintelReports/" ;
			File downloadDir = new File(exportDir);
			
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
									
					filePath = exportDir +  "InStorePromotionsReport"+ "_" + System.currentTimeMillis() + ".csv";
					
					logger.debug("Download File path : " + filePath);
					File file = new File(filePath);
					BufferedWriter bw = new BufferedWriter(new FileWriter(filePath));
					//bw.write("\"Email Address\",\"Reason\" \r\n");
					sb = new StringBuffer();
					
					if(whichGridToExport.equalsIgnoreCase(PROMO)) {
						sb.append("\"Promotion\",\"Revenue\",\"No. Of Redemptions\"");
						sb.append("\r\n");
						bw.write(sb.toString());
						int count = retailProsalesDao.findTotalCount(users.getUserId(),fromDateStr,toDateStr);
						
						for(int i=0; i < count; i+=size) {
							sb.setLength(0);
							List<Map<String, Object>> promoObjArrList  = retailProsalesDao.findPromotionalRedemptionList(users.getUserId(), i, size,"",fromDateStr,toDateStr,orderby_promotion_colName,desc_Asc);
							
							
							if(promoObjArrList.size() > 0) {
								
								for (Map<String, Object> eachMap: promoObjArrList) {
									 //promocode
									 sb.append("\"");
									 if(eachMap.containsKey("promo_code") && eachMap.get("promo_code") != null) {
										 sb.append(eachMap.get("promo_code").toString());
									 }
									 sb.append("\",");
									 
									 
									 //Revenue
									 sb.append("\"");
									 if(eachMap.containsKey("REVENUE") && eachMap.get("REVENUE") != null) {
										 sb.append(eachMap.get("REVENUE").toString());
									 }
									 sb.append("\",");
									 
									 
									 
									 //No. of redemptions
									 sb.append("\"");
									 if(eachMap.containsKey("COUNT") && eachMap.get("COUNT") != null) {
										 sb.append(eachMap.get("COUNT").toString());
									 }
									 sb.append("\"\r\n");
								}
							}
							bw.write(sb.toString());
							promoObjArrList = null;
							//System.gc();
						}
					}else if(whichGridToExport.equalsIgnoreCase(STOREDETAIL)){
						
						sb.append(storeID.getValue());
						sb.append("\r\n");
						sb.append("\"Promotion\",\"Revenue\",\"No. Of Redemptions\"");
						sb.append("\r\n");
						bw.write(sb.toString());
						int count = retailProsalesDao.findTotalCountOfPromoRelatedToStore(users.getUserId(), currentStore,fromDateStr,toDateStr);
						
						for(int i=0; i < count; i+=size) {
							sb.setLength(0);
							List<Map<String, Object>> promoObjArrList  = retailProsalesDao.findPromotionalRedemptionList(users.getUserId(), i, size, currentStore,fromDateStr,toDateStr,orderby_promotion_colName,desc_Asc);
							
							
							if(promoObjArrList.size() > 0) {
								
								for (Map<String, Object> eachMap: promoObjArrList) {
									 //promocode
									 sb.append("\"");
									 if(eachMap.containsKey("promo_code") && eachMap.get("promo_code") != null) {
										 sb.append(eachMap.get("promo_code").toString());
									 }
									 sb.append("\",");
									 
									 
									 //Revenue
									 sb.append("\"");
									 if(eachMap.containsKey("REVENUE") && eachMap.get("REVENUE") != null) {
										 sb.append(eachMap.get("REVENUE").toString());
									 }
									 sb.append("\",");
									 
									 
									 
									 //No. of redemptions
									 sb.append("\"");
									 if(eachMap.containsKey("COUNT") && eachMap.get("COUNT") != null) {
										 sb.append(eachMap.get("COUNT").toString());
									 }
									 sb.append("\"\r\n");
								}
							}
							bw.write(sb.toString());
							promoObjArrList = null;
							//System.gc();
						}
					
						
					}else if(whichGridToExport.equalsIgnoreCase(STORE)){
						

						sb.append("\"Store\",\"Revenue\",\"No. Of Redemptions\"");
						sb.append("\r\n");
						bw.write(sb.toString());
						int count = retailProsalesDao.findTotalCountForStore(users.getUserId(),fromDateStr,toDateStr);
						String storeName;
						for(int i=0; i < count; i+=size) {
							sb.setLength(0);
							List<Map<String, Object>> promoObjArrList  = retailProsalesDao.findStoreRedemptionList(users.getUserId(), i, size,"",fromDateStr,toDateStr,orderby__store_colName,desc_Asc);
							
							
							if(promoObjArrList.size() > 0) {
								
								for (Map<String, Object> eachMap: promoObjArrList) {
									 //promocode
									 sb.append("\"");
									 if(eachMap.containsKey("store_number") && eachMap.get("store_number") != null) {
										 storeName = fetchStoreName(eachMap.get("store_number").toString());
										 sb.append(storeName);
									 }
									 sb.append("\",");
									 
									 
									 //Revenue
									 sb.append("\"");
									 if(eachMap.containsKey("REVENUE") && eachMap.get("REVENUE") != null) {
										 sb.append(eachMap.get("REVENUE").toString());
									 }
									 sb.append("\",");
									 
									 
									 
									 //No. of redemptions
									 sb.append("\"");
									 if(eachMap.containsKey("COUNT") && eachMap.get("COUNT") != null) {
										 sb.append(eachMap.get("COUNT").toString());
									 }
									 sb.append("\"\r\n");
								}
							}
							bw.write(sb.toString());
							promoObjArrList = null;
							//System.gc();
						}
					
						
					
						
					}else if(whichGridToExport.equalsIgnoreCase(PROMODETAIL)){
						
						sb.append(promoID.getValue());
						sb.append("\r\n");
						sb.append("\"Store\",\"Revenue\",\"No. Of Redemptions\"");
						sb.append("\r\n");
						bw.write(sb.toString());
						int count = retailProsalesDao.findTotalCountOfStoreRelatedToPromo(users.getUserId(), currentPromoCode,fromDateStr,toDateStr);
						String storeName;
						for(int i=0; i < count; i+=size) {
							sb.setLength(0);
							List<Map<String, Object>> promoObjArrList  = retailProsalesDao.findStoreRedemptionList(users.getUserId(), i, size,currentPromoCode,fromDateStr,toDateStr,orderby__store_colName,desc_Asc);
							
							
							if(promoObjArrList.size() > 0) {
								
								for (Map<String, Object> eachMap: promoObjArrList) {
									 //promocode
									 sb.append("\"");
									 if(eachMap.containsKey("store_number") && eachMap.get("store_number") != null) {
										 storeName = fetchStoreName(eachMap.get("store_number").toString());
										 sb.append(storeName);
									 }
									 sb.append("\",");
									 
									 
									 //Revenue
									 sb.append("\"");
									 if(eachMap.containsKey("REVENUE") && eachMap.get("REVENUE") != null) {
										 sb.append(eachMap.get("REVENUE").toString());
									 }
									 sb.append("\",");
									 
									 
									 
									 //No. of redemptions
									 sb.append("\"");
									 if(eachMap.containsKey("COUNT") && eachMap.get("COUNT") != null) {
										 sb.append(eachMap.get("COUNT").toString());
									 }
									 sb.append("\"\r\n");
								}
							}
							bw.write(sb.toString());
							promoObjArrList = null;
							//System.gc();
						}
					
						
					
						
					
						
					}
					
					bw.flush();
					bw.close();
					Filedownload.save(file, "text/plain");
				} catch (IOException e) {
					
				}
				logger.debug("-- exit --");
			}
		
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
	 
	 private boolean validateAndSetSearchDates() {
			
			if(fromDateboxId.getValue() == null || toDateboxId.getValue() == null ){
				MessageUtil.setMessage("Please enter the required dates.",
						"color:red", "TOP");
				
				
				return false;
			}
			
			Calendar serverFromDateCal = fromDateboxId.getServerValue();
			Calendar serverToDateCal = toDateboxId.getServerValue();

			Calendar tempClientFromCal = fromDateboxId.getClientValue();
			Calendar tempClientToCal = toDateboxId.getClientValue();
			
			logger.debug("client From :" + tempClientFromCal + ", client To :"
					+ tempClientToCal);

			// change the time for startDate and endDate in order to consider right
			// from the
			// starting time of startDate to ending time of endDate
			serverFromDateCal.set(
					Calendar.HOUR_OF_DAY,
					serverFromDateCal.get(Calendar.HOUR_OF_DAY)
							- tempClientFromCal.get(Calendar.HOUR_OF_DAY));
			serverFromDateCal.set(
					Calendar.MINUTE,
					serverFromDateCal.get(Calendar.MINUTE)
							- tempClientFromCal.get(Calendar.MINUTE));
			serverFromDateCal.set(Calendar.SECOND, 0);

			serverToDateCal.set(Calendar.HOUR_OF_DAY,
					23 + serverToDateCal.get(Calendar.HOUR_OF_DAY)
							- tempClientToCal.get(Calendar.HOUR_OF_DAY));
			serverToDateCal.set(
					Calendar.MINUTE,
					59 + serverToDateCal.get(Calendar.MINUTE)
							- tempClientToCal.get(Calendar.MINUTE));
			serverToDateCal.set(Calendar.SECOND, 59);

			if (serverToDateCal.compareTo(serverFromDateCal) < 0) {
				MessageUtil.setMessage("'To' date must be later than 'From' date.",
						"color:red", "TOP");
				return false;
			}
			
			fromDateStr = serverFromDateCal.toString();
			toDateStr = serverToDateCal.toString();
			
			
			return true;
			
		}
	 
	 public String orderby_promotion_colName="sales_date",orderby__store_colName="sales_date",desc_Asc="desc";
	 public int pNoL;
	 public void desc2ascasc2desc(String pagingFor)
	    {
		 	
		 	
	    	if(desc_Asc=="desc")
				desc_Asc="asc";
			else
				desc_Asc="desc";
	    	if(pagingFor=="Promotion")
	    		pNoL=Integer.parseInt(memberPerPagePromoLBId.getSelectedItem().getLabel());
	    	else
	    		pNoL=Integer.parseInt(memberPerPageStoreLBId.getSelectedItem().getLabel());
	    	
		
	    }
	 
	 public void onClick$sortbyPromotion(){
		 orderby_promotion_colName = "promo_code";
			desc2ascasc2desc("Promotion");
			fillPromotionRedemption(0, pNoL,orderby_promotion_colName,desc_Asc);
	
		}
		public void onClick$sortbyRevenue(){
			orderby_promotion_colName = "REVENUE";
			desc2ascasc2desc("Promotion");
			fillPromotionRedemption(0, pNoL,orderby_promotion_colName,desc_Asc);
			
		}
		public void onClick$sortbyNoOfRedemptions(){
			orderby_promotion_colName = "COUNT";
			desc2ascasc2desc("Promotion");
			fillPromotionRedemption(0, pNoL,orderby_promotion_colName,desc_Asc);
			
		}
		public void onClick$sortbyStore(){
			orderby__store_colName = "store_number";
			desc2ascasc2desc("Store");
			fillStoreRedemption(0, pNoL,orderby__store_colName,desc_Asc);
			
		}
		public void onClick$sortbyStoreRevenue(){
			orderby__store_colName = "REVENUE";
			desc2ascasc2desc("Store");
			fillStoreRedemption(0, pNoL,orderby__store_colName,desc_Asc);
			
		}
		public void onClick$sortbyStoreNoOfRedemptions(){
			orderby__store_colName = "COUNT";
			desc2ascasc2desc("Store");
			fillStoreRedemption(0, pNoL,orderby__store_colName,desc_Asc);
			
		}

	 
	 public void onClick$resetAnchId() {
		// onClick$backBtnId();
		 Calendar cal = MyCalendar.getNewCalendar();
		 toDateboxId.setValue(cal);
		 logger.debug("ToDate (server) :" + cal);
		 //cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) - 1);
		 cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) - 3);
		 cal.set(Calendar.DATE, cal.get(Calendar.DATE) + 1);
		 logger.debug("FromDate (server) :" + cal);
		 fromDateboxId.setValue(cal);
		 memberPerPagePromoLBId.setSelectedIndex(0);
		 memberPerPageStoreLBId.setSelectedIndex(0);
		 orderby_promotion_colName="sales_date";
		 desc_Asc="desc";
		 onClick$getBetweenDatesBtnId();
		 

	 }

	 public void onClick$getBetweenDatesBtnId(){
		 boolean status = validateAndSetSearchDates();
		 if(status == false) return;

         
		 
		 
		 if(promotionRedmGridId.isVisible()){// it means we are on overview
			 
			 int promoListSize = retailProsalesDao.findTotalCount(users.getUserId(),fromDateStr,toDateStr);
			 int storeListSize = retailProsalesDao.findTotalCountForStore(users.getUserId(),fromDateStr,toDateStr);
			 
			 storePagingId.setTotalSize(storeListSize);
			 promoPagingId.setTotalSize(promoListSize);
			 storePagingId.setActivePage(0);
			 promoPagingId.setActivePage(0);
			 
			 String selectStr = memberPerPagePromoLBId.getSelectedItem().getLabel();
			 int pNo = Integer.parseInt(selectStr);
			 fillPromotionRedemption(0, pNo,orderby_promotion_colName,desc_Asc);

			 selectStr = memberPerPageStoreLBId.getSelectedItem().getLabel();
			 pNo = Integer.parseInt(selectStr);
			 fillStoreRedemption(0, pNo,orderby__store_colName,desc_Asc);
			 
		 }else if(promotionRedmDetailGridId.isVisible()){
			 
			 
			 int count = retailProsalesDao.findTotalCountOfStoreRelatedToPromo(users.getUserId(), currentPromoCode, fromDateStr,toDateStr);
			 promoDetailPagingId.setTotalSize(count);
			 promoDetailPagingId.setActivePage(0);
			 
			 String selectStr = memberPerPagePromoDetailLBId.getSelectedItem().getLabel();
			 int pNo = Integer.parseInt(selectStr);
			 fillPromoRedemptionDetail(0, pNo, currentPromoCode);
			 
			 
		 }else if(storeRedmDetailGridId.isVisible()){
			 
			 int count = retailProsalesDao.findTotalCountOfPromoRelatedToStore(users.getUserId(), currentStore,fromDateStr,toDateStr);
			 storeDetailPagingId.setTotalSize(count);
			 storeDetailPagingId.setActivePage(0);
			 
			 String selectStr = memberPerPageStoreDetailLBId.getSelectedItem().getLabel();
			 int pNo = Integer.parseInt(selectStr);
			 fillStoreRedemptionDetail(0, pNo, currentStore);
			 
		 }


	 }
	
}
