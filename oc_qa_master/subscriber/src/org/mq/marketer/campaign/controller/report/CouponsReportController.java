package org.mq.marketer.campaign.controller.report;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.mq.marketer.campaign.beans.CouponDiscountGeneration;
import org.mq.marketer.campaign.beans.Coupons;
import org.mq.marketer.campaign.beans.POSMapping;
import org.mq.marketer.campaign.beans.UserActivities;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.custom.MyDatebox;
import org.mq.marketer.campaign.dao.CouponCodesDao;
import org.mq.marketer.campaign.dao.CouponDiscountGenerateDao;
import org.mq.marketer.campaign.dao.CouponsDao;
import org.mq.marketer.campaign.dao.OrganizationStoresDao;
import org.mq.marketer.campaign.dao.POSMappingDao;
import org.mq.marketer.campaign.dao.UserActivitiesDaoForDML;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.POSFieldsEnum;
import org.mq.marketer.campaign.general.PageListEnum;
import org.mq.marketer.campaign.general.PageUtil;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Redirect;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.data.dao.JdbcResultsetHandler;
import org.mq.optculture.utils.OCCSVWriter;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;
import org.zkoss.zul.event.PagingEvent;

import com.google.zxing.Writer;

public class CouponsReportController extends GenericForwardComposer implements EventListener{

	private static Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	private MyDatebox toDateboxId, fromDateboxId, storeFromDateboxId, storeToDateboxId;
	private Listbox srchLbId,statusLbId, pageSizeLbId,memberPerPageStoreLBId,exportCouponReportLbId,exportStoreRedmLbId;
	private CouponsDao couponsDao;
	private TimeZone clientTimeZone;
	private Rows couponRowsId,storeRedmRowsId;
	private Users users;
	private CouponDiscountGeneration couponDiscountGeneration;
	private CouponDiscountGenerateDao couponDiscountGenerateDao;
	private UserActivitiesDaoForDML userActivitiesDaoForDML = null;
	//private RetailProSalesDao retailProsalesDao;
	private CouponCodesDao couponCodesDao;
	private String fromDate, endDate;
	private OrganizationStoresDao organizationStoresDao;
	private List<Map<String, Object>> storeNumberNameMapList;
	private String storeFromDateStr,storeToDateStr;
	private Calendar fromDateCal, toDateCal ;
	
	 private Window custExport,custExportAsStore;
	 private Div custExport$chkDivId,custExportAsStore$chkStoreDivId;
	 private Combobox exportCbId;
	
	private final String SEARCH_BY_STATUS = "Status";
	private final String SEARCH_BY_DATE = "Date";
	private final String SEARCH_BY_PROMOTIONNAME = "PromotionName";
	
	//private Listbox statusLbId;
	private Div searchByPromoCodeNameDivId;
	private Div searchByLastModifiedDivId;
	private Div searchByStatusDivId;
	
	private Textbox searchByPromoCodeNameTbId;
	private  String  userCurrencySymbol = "$ "; 


	 
	 
	 
	
	
	
	
private static Map<String, String> genFieldContMap;
    
    static {
      logger = LogManager.getLogger("subscriber");
        (CouponsReportController.genFieldContMap = new LinkedHashMap<String, String>()).put("Email", "c.email_id");
        CouponsReportController.genFieldContMap.put("Mobile", "c.mobile_phone");
        CouponsReportController.genFieldContMap.put("First Name", "c.first_name");
        CouponsReportController.genFieldContMap.put("Last Name", "c.last_name");
        CouponsReportController.genFieldContMap.put("Street", "c.address_one");
        CouponsReportController.genFieldContMap.put("Address Two", "c.address_two");
        CouponsReportController.genFieldContMap.put("City", "c.city");
        CouponsReportController.genFieldContMap.put("State", "c.state");
        CouponsReportController.genFieldContMap.put("Country", "c.country");
        CouponsReportController.genFieldContMap.put("ZIP", "c.zip");
        CouponsReportController.genFieldContMap.put("Customer ID", "c.external_id");
        CouponsReportController.genFieldContMap.put("Gender", "c.gender");
        CouponsReportController.genFieldContMap.put("BirthDay", "c.birth_day");
        CouponsReportController.genFieldContMap.put("Anniversary", "c.anniversary_day");
        CouponsReportController.genFieldContMap.put("Home Store", "c.home_store");
        CouponsReportController.genFieldContMap.put("UDF1", "c.udf1");
        CouponsReportController.genFieldContMap.put("UDF2", "c.udf2");
        CouponsReportController.genFieldContMap.put("UDF3", "c.udf3");
        CouponsReportController.genFieldContMap.put("UDF4", "c.udf4");
        CouponsReportController.genFieldContMap.put("UDF5", "c.udf5");
        CouponsReportController.genFieldContMap.put("UDF6", "c.udf6");
        CouponsReportController.genFieldContMap.put("UDF7", "c.udf7");
        CouponsReportController.genFieldContMap.put("UDF8", "c.udf8");
        CouponsReportController.genFieldContMap.put("UDF9", "c.udf9");
        CouponsReportController.genFieldContMap.put("UDF10", "c.udf10");
        CouponsReportController.genFieldContMap.put("UDF11", "c.udf11");
        CouponsReportController.genFieldContMap.put("UDF12", "c.udf12");
        CouponsReportController.genFieldContMap.put("UDF13", "c.udf13");
        CouponsReportController.genFieldContMap.put("UDF14", "c.udf14");
        CouponsReportController.genFieldContMap.put("UDF15", "c.udf15");
    }
	
	
	
	
	
	public CouponsReportController() {
		session = Sessions.getCurrent();
		String style = "font-weight:bold;font-size:15px;color:#313031;"
				+ "font-family:Arial,Helvetica,sans-serif;align:left";
		PageUtil.setHeader("Discount Code Reports", "", style, true);
		users = GetUser.getUserObj();
		this.couponsDao = (CouponsDao) SpringUtil.getBean("couponsDao");
		this.couponDiscountGenerateDao = (CouponDiscountGenerateDao) SpringUtil
				.getBean("couponDiscountGenerateDao");
		couponCodesDao = (CouponCodesDao) SpringUtil.getBean("couponCodesDao");
		//retailProsalesDao = (RetailProSalesDao)SpringUtil.getBean("retailProSalesDao");
		clientTimeZone = (TimeZone) session.getAttribute("clientTimeZone");
		organizationStoresDao =	(OrganizationStoresDao)SpringUtil.getBean("organizationStoresDao");
		userActivitiesDaoForDML = (UserActivitiesDaoForDML)SpringUtil.getBean("userActivitiesDaoForDML");
	}
	private int pageSize = 0;

	public void doAfterCompose(Component comp) throws Exception {
		// TODO Auto-generated method stub
		super.doAfterCompose(comp);
		Long userId = GetUser.getUserObj().getUserId();
		if(userActivitiesDaoForDML!=null) {
		UserActivities userActivity = new UserActivities("Visited coupons report page", "Visited pages", Calendar.getInstance(),userId );
		userActivitiesDaoForDML.saveOrUpdate(userActivity);
		}
		
		//Default DateSettings
		//setDateValues();
		
		//DefaultSetting Based on Dates
		
		//String fromDate = MyCalendar.calendarToString(getStartDate(), MyCalendar.FORMAT_DATETIME_STYEAR);
		//String endDate = MyCalendar.calendarToString(getEndDate(), MyCalendar.FORMAT_DATETIME_STYEAR);
//		String statusStr = statusLbId.getSelectedItem().getLabel();
		int tempCount = Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel());
		
		//List<Coupons> couponsList = couponsDao.findDefaultCoupons(users.getUserOrganization().getUserOrgId(),0,tempCount);
		// Changes 2.5.2.19
		String status = statusLbId.getSelectedItem().getLabel();
		//defaultSettingForDateByStatus(status);
		List<Coupons> couponsList = couponsDao.findCouponsByOrgId(users.getUserOrganization().getUserOrgId(),0,tempCount, status, fromDate,endDate,orderby_colName,desc_Asc,null);
		
		int count = couponsDao.findTotCountCouponsofOrg(users.getUserOrganization().getUserOrgId(),fromDate,endDate);	
		if(couponsList!= null ) {
		 totalSize  = couponsList.size();
		}
//				couponsDao.findCoupBasedOnDates(currentUser.getUserId(),statusStr,fromDate, endDate);
				
		logger.debug(">>>>>>>>totalSize ::"+totalSize);
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
		
		int storeListSize = couponCodesDao.findTotalStoreCount(users.getUserOrganization().getUserOrgId(),storeFromDateStr,storeToDateStr);
		couponRepListBottomPagingId.setTotalSize(count);
		couponRepListBottomPagingId.setPageSize(tempCount);
		couponRepListBottomPagingId.setActivePage(0);
		couponRepListBottomPagingId.addEventListener("onPaging", this);
		couponRepListBottomPagingId.setAttribute("onPaging", "coupon");
		
		storePagingId.setTotalSize(storeListSize);
		storePagingId.setPageSize(Integer.parseInt(memberPerPageStoreLBId.getSelectedItem().getLabel()));
		storePagingId.setActivePage(0);
		storePagingId.addEventListener("onPaging",this);
		storePagingId.setAttribute("onPaging", "store");
		storeNumberNameMapList  = organizationStoresDao.findStoreNumberNameMapList(GetUser.getUserObj().getUserOrganization().getUserOrgId());
		
		
		String statusStr = statusLbId.getSelectedItem().getLabel();
		
		//getCouponReports(0, tempCount);
		renderingPromoList(couponsList);
		fillStoreRedemption(0,Integer.parseInt(memberPerPageStoreLBId.getSelectedItem().getLabel()));
		//defaultSettingForDateByStatus(statusStr);
		 String currSymbol = Utility.countryCurrencyMap.get(users.getCountryType());
		   if(currSymbol != null && !currSymbol.isEmpty()) userCurrencySymbol = currSymbol + " ";
		
		onClick$getReportBtnId();
		
		
		
	} // doAfterCompose


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
	
	//Changes Start 2.5.2.19
	
	public void onSelect$srchLbId() {
		//Components.removeAllChildren(couponRowsId);
		//couponRepListBottomPagingId.setTotalSize(0);
		String value = srchLbId.getSelectedItem().getValue();
		
		if(value.equals(SEARCH_BY_DATE)) {
			searchByPromoCodeNameTbId.setValue(null); 
			searchByPromoCodeNameDivId.setVisible(false);
			searchByLastModifiedDivId.setVisible(true);
			fromDateboxId.setText(Constants.STRING_NILL);
			toDateboxId.setText(Constants.STRING_NILL);
			
			
			searchByStatusDivId.setVisible(false);
			statusLbId.setSelectedIndex(0);
			return;
		}
		else if(value.equals(SEARCH_BY_STATUS)) {
			 
			searchByPromoCodeNameTbId.setValue(null);
			searchByPromoCodeNameDivId.setVisible(false);
			searchByLastModifiedDivId.setVisible(false);
			searchByStatusDivId.setVisible(true);
			statusLbId.setSelectedIndex(0);
			return;
		}
		else if(value.equals(SEARCH_BY_PROMOTIONNAME)) {
			 
			searchByPromoCodeNameDivId.setVisible(true);
			searchByLastModifiedDivId.setVisible(false);
			searchByStatusDivId.setVisible(false);
			return;
		}
		
	}//onSelect$srchLbId()

	//Changes End 2.5.2.19
	
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
	
	


	private Paging couponRepListBottomPagingId,storePagingId;
	
	private String fetchStoreName(String store_number){
		 try{
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
		 
		 }catch(Exception e){
			logger.error("Exception while fetching storeName",e);
			return "Store ID "+store_number;
		 }
	}
	
	private int fillStoreRedemption(int fromInt, int lbxLenght) {

		try {
			int size;
			Components.removeAllChildren(storeRedmRowsId);
			
			/*int count =  promoCodeRowsId.getItemCount();
			
			for(; count>0; count--) {
				promoCodeRowsId.removeAt(count-1);
			}*/
			
			List<Map<String, Object>> promoObjArrList  = couponCodesDao.findAllStoresRedemptionList(users.getUserOrganization().getUserOrgId(), fromInt, lbxLenght,"",storeFromDateStr,storeToDateStr); //-- sm change
			
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
					 revenueLbl.setValue(decimalFormat.format(eachMap.get("REVENUE")));
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
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//sorting hard coded
	
	
	public void fromDate_EndDate(){
		//changes 2.5.2.19
			if(fromDateboxId.getValue() == null || toDateboxId.getValue() == null) {
				MessageUtil.setMessage("Please specify the dates", "red");
				return;
			}
	
			Calendar  start = fromDateboxId.getClientValue();
			Calendar end = toDateboxId.getClientValue();
	
			if(end.before(start)) {
				MessageUtil.setMessage("'From' date cannot be later than 'To' date.", "red");
				return;
			}
	
			fromDate = MyCalendar.calendarToString(getStartDate(fromDateboxId), MyCalendar.FORMAT_DATETIME_STYEAR);
			endDate = MyCalendar.calendarToString(getEndDate(toDateboxId), MyCalendar.FORMAT_DATETIME_STYEAR);
	}	
	
	
	public String orderby_colName="userCreatedDate",desc_Asc="desc"; 
	    
	    public void desc2ascasc2desc()
	    {
	    	if(desc_Asc=="desc")
				desc_Asc="asc";
			else
				desc_Asc="desc";
		
	    }
		 
		
		public void onClick$sortbyPromotionName() {
			orderby_colName = "couponName";
			desc2ascasc2desc();	
			if(srchLbId.getSelectedItem().getValue().equals(SEARCH_BY_DATE))fromDate_EndDate();
			getCouponReports(0,Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel()));
			
		 }
		 
		 public void onClick$sortbyStatus() {
				orderby_colName = "status";
				desc2ascasc2desc();	
				if(srchLbId.getSelectedItem().getValue().equals(SEARCH_BY_DATE))fromDate_EndDate();
				getCouponReports(0,Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel()));
				
			 }
		 
		
		 public void onClick$sortbyissued() {
				orderby_colName = "issued";
				desc2ascasc2desc();	
				if(srchLbId.getSelectedItem().getValue().equals(SEARCH_BY_DATE))fromDate_EndDate();
				getCouponReports(0,Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel()));
					
			 }
		 public void onClick$sortbyredeemed() {
				orderby_colName = "redeemed";
				desc2ascasc2desc();	
				if(srchLbId.getSelectedItem().getValue().equals(SEARCH_BY_DATE))fromDate_EndDate();
				getCouponReports(0,Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel()));
			 }
			 
		 public void onClick$sortbytotRevenue() {
				orderby_colName = "totRevenue";
				desc2ascasc2desc();	
				if(srchLbId.getSelectedItem().getValue().equals(SEARCH_BY_DATE))fromDate_EndDate();
				getCouponReports(0,Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel()));
			 }

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

	public void onSelect$pageSizeLbId() {
		
		try {
			/*String fromDate = MyCalendar.calendarToString(getStartDate(), MyCalendar.FORMAT_DATETIME_STYEAR);
			String endDate = MyCalendar.calendarToString(getEndDate(), MyCalendar.FORMAT_DATETIME_STYEAR);
			String status = statusLbId.getSelectedItem().getLabel();*/
			
			int tempCount = Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel());
			couponRepListBottomPagingId.setPageSize(tempCount);
			couponRepListBottomPagingId.setActivePage(0);
		
			getCouponReports(couponRepListBottomPagingId.getActivePage()*couponRepListBottomPagingId.getPageSize(),tempCount);
			
			
		} catch (WrongValueException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
		}
		
	
		
	}//onSelect$pageSizeLbId() 

	
	
	int totalSize = 0;
	

	/*//Changes Start 2.5.2.19
	public void getCouponReportsBasedOnStatus(int startIndex, int count) {
		logger.debug("-- just entered --");
		MessageUtil.clearMessage();
		
		
		List<Coupons> couponsList =  null;
		
			String status = statusLbId.getSelectedItem().getLabel();
			
			//pageSize = Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel());
			couponsList = couponsDao.findCouponsByOrgId(users.getUserOrganization().getUserOrgId(),startIndex,count, status,orderby_colName,desc_Asc);
			
		
		
		renderingPromoList(couponsList);

	}*///getCouponReports()
//Chnages End 2.5.2.19
	
	
	//Changes 2.5.2.19 
	public void getCouponReports(int startIndex, int count) {
		logger.debug("-- just entered --");
		MessageUtil.clearMessage();
		
		
		List<Coupons> couponsList =  null;
		if(fromDateboxId.getValue()== null && toDateboxId.getValue()== null&&!searchByStatusDivId.isVisible()&&!searchByPromoCodeNameDivId.isVisible()){
			//int tempCount = Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel());
			//couponsList = couponsDao.findDefaultCoupons(currentUser.getUserId(),startIndex,count);
			MessageUtil.setMessage("Please specify the dates", "red");
			return;
			/*if(couponsList!= null ) {
			 totalSize  = couponsList.size();
			}*/	
		}else{
			/*Calendar calObj =  getStartDate(fromDateboxId);
			
			fromDate = MyCalendar.calendarToString(calObj, MyCalendar.FORMAT_DATETIME_STYEAR);
			endDate = MyCalendar.calendarToString(getEndDate(toDateboxId), MyCalendar.FORMAT_DATETIME_STYEAR);*/
			String status="";
			String promotionName="";
			if(searchByStatusDivId.isVisible()){
			status = statusLbId.getSelectedItem().getLabel();
			}else if(searchByPromoCodeNameDivId.isVisible()){
				promotionName = searchByPromoCodeNameTbId.getValue().trim();
			}
			
			logger.info("====>"+status);
			//pageSize = Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel());
			couponsList = couponsDao.findCouponsByOrgId(users.getUserOrganization().getUserOrgId(),startIndex,count, status, fromDate,endDate,orderby_colName,desc_Asc,promotionName);
			
		}
		
		renderingPromoList(couponsList);

	}//getCouponReports()
	
	
	private void renderingPromoList(List<Coupons> coupList){
		Components.removeAllChildren(couponRowsId);
		if(coupList == null) {
			//TODO
			logger.debug(" No Promo-codes exists ");
			return;
		}
		Label tempLabel = null;
		
		//List<CouponDiscountGeneration> dis
		DecimalFormat decimalFormat = new DecimalFormat("#0.00");
		for (Coupons coupObj : coupList) {
			
			String lastModifiedDate=null;
			lastModifiedDate=MyCalendar.calendarToString(coupObj.getUserLastModifiedDate(), MyCalendar.FORMAT_DATEONLY_GENERAL,clientTimeZone );
			 
			String validity = null;
			if(coupObj.getExpiryType().equals(Constants.COUP_VALIDITY_PERIOD_STATIC)){
				
				validity =MyCalendar.calendarToString(coupObj.getCouponCreatedDate(), MyCalendar.FORMAT_DATEONLY_GENERAL)+" - "+MyCalendar.calendarToString(coupObj.getCouponExpiryDate(),MyCalendar.FORMAT_DATEONLY_GENERAL);
			}
			else if(coupObj.getExpiryType().equals(Constants.COUP_VALIDITY_PERIOD_DYNAMIC)){
				validity = "Dynamic";
			}

			String discount =  "";
			if(coupObj.getDiscountType().equals("Percentage")) {
				discount = "% on ";
			}else {
				discount = userCurrencySymbol+" on ";
			//	discount = "$ on ";
			}
			//discount= discount+" "+coupObj.getDiscountCriteria();
			if(coupObj.getDiscountCriteria().trim().contains("SKU")) {
				discount += " Product";
			}else {
				discount += " Receipt";
			}
					//couponDiscountGeneration.getDiscount() !=null?""+couponDiscountGeneration.getDiscount() : "";

			//String totRevenue = couponDiscountGeneration.getTotPurchaseAmount() !=null?""+couponDiscountGeneration.getTotPurchaseAmount() : "";

			Row row = new Row();
			row.setParent(couponRowsId);
			//Coup Name
			tempLabel= new Label(coupObj.getCouponName().trim());
			tempLabel.setStyle("cursor:pointer;color:blue;text-decoration: underline;");
			tempLabel.addEventListener("onClick", this);
			row.appendChild(tempLabel);
			
			//Status
			row.appendChild(new Label(coupObj.getStatus()));
			//created on
			row.appendChild(new Label(lastModifiedDate));
			//Validity
			row.appendChild(new Label(validity));
			//Discount
			row.appendChild(new Label(discount));
			
			
			String totStr=""; 
			String availableStr="";
			if(coupObj.getAutoIncrCheck()){
				totStr="Unlimited";
				availableStr="Unlimited";
			}else{
				totStr=""+coupObj.getTotalQty();
				long allCount = couponCodesDao.findIssuedCoupCodeByCoup(coupObj.getCouponId());
				Long totalCouponCodeCount = couponCodesDao.findTotalCoupCodeCountByCoupAndStatus(coupObj.getCouponId());
				coupObj.setTotalQty(totalCouponCodeCount);
				long availCount = coupObj.getTotalQty().longValue()-allCount;
				//availableStr=""+(coupObj.getAvailable()!= null ? coupObj.getAvailable():"");
				availableStr=""+availCount;
			}
			//Codes generated
			row.appendChild(new Label(totStr));
				
			
			//issued
			row.appendChild(new Label(coupObj.getIssued() != null? ""+coupObj.getIssued():"" ));
			
			//Redeemed
			row.appendChild(new Label(coupObj.getRedeemed() != null?  ""+coupObj.getRedeemed():"" ));
			//Available
			row.appendChild(new Label(availableStr));
			//Revenue
			row.appendChild(new Label(coupObj.getTotRevenue() != null? ""+decimalFormat.format(coupObj.getTotRevenue()):"" ));
			
			// no of redeemed pooints
			row.appendChild(new Label(coupObj.getUsedLoyaltyPoints()!= null?coupObj.getUsedLoyaltyPoints().intValue()+"":"0" ));
			
			row.setValue(coupObj);
			
			row.setParent(couponRowsId);

		} // for
	} //renderingPromoList
	
	

	public void onClick$resetAnchId() {
		srchLbId.setSelectedIndex(0);
		onSelect$srchLbId();
		//statusLbId.setSelectedIndex(0);
		orderby_colName="couponId";
		desc_Asc="desc";
		pageSizeLbId.setSelectedIndex(0);
		onClick$getReportBtnId();

/*		setDateValues();
		String fromDate = MyCalendar.calendarToString(getStartDate(), MyCalendar.FORMAT_DATETIME_STYEAR);
		String endDate = MyCalendar.calendarToString(getEndDate(), MyCalendar.FORMAT_DATETIME_STYEAR);
		String statusStr = statusLbId.getSelectedItem().getLabel();
		
		totalSize  = couponsDao.findCoupBasedOnDates(currentUser.getUserId(),statusStr,fromDate, endDate);
		
		int tempCount = Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel());
		logger.debug(">>>>>>>>totalSize ::"+totalSize);
		
		couponRepListBottomPagingId.setTotalSize(totalSize);
		
     	getCouponReports(0, tempCount);
     	
		String status = statusLbId.getSelectedItem().getLabel();
		Calendar couponDate = null;
		Calendar cal = Calendar.getInstance();
		couponDate = couponsDao.getCouponsDateByStatus(users.getUserOrganization().getUserOrgId(),status);
     	
		if(couponDate != null) {
			fromDateboxId.setValue(couponDate);
		}else {
			fromDateboxId.setValue(cal);
		}
		
		toDateboxId.setValue(cal);
		cal.set(Calendar.MONTH, cal.get(Calendar.MONTH)-1);
		logger.debug("FromDate (server) :"+cal);
		fromDateboxId.setValue(cal);

		

     	toDateboxId.setValue(cal);
     	cal.set(Calendar.MONTH, cal.get(Calendar.MONTH)-1);
		logger.debug("FromDate (server) :"+cal);
		fromDateboxId.setValue(cal);
     	
     	fromDate = MyCalendar.calendarToString(getStartDate(fromDateboxId), MyCalendar.FORMAT_DATETIME_STYEAR,(TimeZone) sessionScope.get("clientTimeZone"));
		endDate = MyCalendar.calendarToString(getEndDate(toDateboxId), MyCalendar.FORMAT_DATETIME_STYEAR,(TimeZone) sessionScope.get("clientTimeZone"));
		fromDate_EndDate();
     	int count = couponsDao.findTotCountCouponsofOrg(users.getUserOrganization().getUserOrgId(), fromDate, endDate);
     	couponRepListBottomPagingId.setTotalSize(count);
     	pageSizeLbId.setSelectedIndex(0);
     	int pageSize = Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel());
     	couponRepListBottomPagingId.setPageSize(pageSize);
     	couponRepListBottomPagingId.setActivePage(0);
	 
		List<Coupons> couponsList = couponsDao.findDefaultCoupons(users.getUserOrganization().getUserOrgId(),
														couponRepListBottomPagingId.getActivePage()*couponRepListBottomPagingId.getPageSize(),couponRepListBottomPagingId.getPageSize());
		if(couponsList!= null ) {
		 totalSize  = couponsList.size();
		}
//				couponsDao.findCoupBasedOnDates(currentUser.getUserId(),statusStr,fromDate, endDate);
				
		logger.debug(">>>>>>>>totalSize ::"+totalSize);
		
		//getCouponReports(0, tempCount);
		renderingPromoList(couponsList);*/
     	
     	
     	
     	
     	

	}// onclick$resetAnchId()

	public void setDateValues() {

		Calendar cal = MyCalendar.getNewCalendar();
		toDateboxId.setValue(cal);
		logger.debug("ToDate (server) :" + cal);
		cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) - 1);
		logger.debug("FromDate (server) :" + cal);
		fromDateboxId.setValue(cal);

	}//setDateValues()
	public void onClick$getStoreBetweenDatesBtnId() {
		
		Calendar  start = storeFromDateboxId.getClientValue();
		Calendar end = storeToDateboxId.getClientValue();
		
		if(end.before(start)) {
			MessageUtil.setMessage("'From' date cannot be later than 'To' date.", "red");
			return;
		}
		
		storeFromDateStr = MyCalendar.calendarToString(getStartDate(storeFromDateboxId), MyCalendar.FORMAT_DATETIME_STYEAR);
		storeToDateStr = MyCalendar.calendarToString(getEndDate(storeToDateboxId), MyCalendar.FORMAT_DATETIME_STYEAR);
		 
		int storeListSize = couponCodesDao.findTotalStoreCount(users.getUserOrganization().getUserOrgId(),storeFromDateStr,storeToDateStr);
		 
		 storePagingId.setTotalSize(storeListSize);
		 storePagingId.setActivePage(0);
		 
		 int pNo = Integer.parseInt( memberPerPageStoreLBId.getSelectedItem().getLabel());
		 fillStoreRedemption(0, pNo);
	}
	public void onClick$getReportBtnId() {
		try {
		//	int index=statusLbId.getSelectedIndex();
		//	fromDate_EndDate();
			//Changes Start 2.5.2.19
			fromDate="";
			endDate="";
			String statusStr=null;
			String promotionName=null;
			
			logger.info("=========>Entering OnClick");
			Components.removeAllChildren(couponRowsId);
			couponRepListBottomPagingId.setTotalSize(0);
			if(srchLbId.getSelectedItem().getValue().equals(SEARCH_BY_STATUS)){
			statusStr = statusLbId.getSelectedItem().getLabel();
			}
			else if(srchLbId.getSelectedItem().getValue().equals(SEARCH_BY_DATE)){
				logger.info("OLA Entered else");
				if(fromDateboxId.getValue() == null || toDateboxId.getValue() == null) {
					MessageUtil.setMessage("Please specify the dates", "red");
					return;
				}
		
				fromDate_EndDate();
			}
			else if(srchLbId.getSelectedItem().getValue().equals(SEARCH_BY_PROMOTIONNAME)){
			
				promotionName = searchByPromoCodeNameTbId.getValue().trim();
				
				if(promotionName==null || promotionName.isEmpty()){
					MessageUtil.setMessage("Please enter Discount Code Name.","color:red", "TOP");
					return;
				}
			}
			logger.info("====>"+fromDate+"----"+endDate);
						
		//	onSelect$pageSizeLbId();
			totalSize =couponsDao.findCoupBasedOnDates(users.getUserOrganization().getUserOrgId(),statusStr,fromDate, endDate,promotionName);
			couponRepListBottomPagingId.setTotalSize(totalSize);
			
			int tempCount = Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel());
			couponRepListBottomPagingId.setPageSize(tempCount);
			couponRepListBottomPagingId.setActivePage(0);
			couponRepListBottomPagingId.addEventListener("onPaging", this);
			couponRepListBottomPagingId.setAttribute("onPaging", "coupon");
			getCouponReports(0,tempCount);
			
		
			//Changes end 2.5.2.19
			
			
		
			
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
		}
	}//onClick$getBitweenDatesBtnId()
	
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
	
	
	public void onChange$fromDateboxId (){
		if(fromDateboxId.getValue() ==null || toDateboxId.getValue()== null) return;
		Calendar  start = fromDateboxId.getClientValue();
		Calendar end = toDateboxId.getClientValue();
		
		if(end.before(start)) {
			MessageUtil.setMessage("'From' date cannot be later than 'To' date.", "red");
			return;
		}
	}
	
	public void onChange$toDateboxId(){
		Calendar  start = null,end = null;
		try {
		  start = fromDateboxId.getClientValue();
		  end = toDateboxId.getClientValue();
		}catch (Exception e) {
		  end = toDateboxId.getClientValue();
		}
		if(end.before(start)) {
			MessageUtil.setMessage("'From' date cannot be later than 'To' date.", "red");
			return;
		}
	}
	
	private void defaultSettingForDateByStatus(String status) {
		
		
		Calendar cal = Calendar.getInstance();
		toDateboxId.setValue(cal);
		logger.debug("ToDate (server) :"+cal);
		cal.set(Calendar.MONTH, cal.get(Calendar.MONTH)-1);
		logger.debug("FromDate (server) :"+cal);
		fromDateboxId.setValue(cal);
		
		/*Calendar cal = Calendar.getInstance();
		toDateboxId.setValue(cal);
		//toDateboxId.s
		cal.set(Calendar.MONTH, cal.get(Calendar.MONTH)-1);
		logger.debug("FromDate (server) :"+cal);
		fromDateboxId.setValue(cal);*/
			/*couponDate = couponsDao.getCouponsDateByStatus(users.getUserOrganization().getUserOrgId(),status);
		if(couponDate != null){
			fromDateboxId.setValue(couponDate);
		}
		else{
			fromDateboxId.setValue(cal);
		}*/
		/*fromDateCal = getFromDateCal();
		toDateCal = getToDateCal();*/
		
		
		
		fromDate = MyCalendar.calendarToString(getStartDate(fromDateboxId), MyCalendar.FORMAT_DATETIME_STYEAR,(TimeZone) sessionScope.get("clientTimeZone"));

		endDate = MyCalendar.calendarToString(getEndDate(toDateboxId), MyCalendar.FORMAT_DATETIME_STYEAR,(TimeZone) sessionScope.get("clientTimeZone"));
		
	}
	
	public void onSelect$statusLbId() {
		
		String status = statusLbId.getSelectedItem().getValue();
		defaultSettingForDateByStatus(status);
	}
	
	
	public void onEvent(Event event) throws Exception {
		// TODO Auto-generated method stub
		super.onEvent(event);
		
		 if(event.getTarget() instanceof Label ) {
		
		Label tempLable = (Label)event.getTarget();
		Row tempRow = (Row)tempLable.getParent();
		
		if(tempRow.getValue()!=null){
		
		Coupons couponObj = (Coupons)tempRow.getValue();
		
		logger.debug("couponObj is  :: "+couponObj);
		if(couponObj != null){
			
			session.setAttribute("COUP_REDEEMED_DETAILS", couponObj);
		}
		}else{
			String store = ""+tempLable.getAttribute("original value");
			session.removeAttribute("COUP_REDEEMED_DETAILS");
			session.setAttribute("STORE_REDEEMED_DETAILS", store);
		}
		Long userId = GetUser.getUserObj().getUserId();
		if(userActivitiesDaoForDML!=null) {
		UserActivities userActivity = new UserActivities("Visited detailed coupons report page", "Visited pages", Calendar.getInstance(),userId );
		userActivitiesDaoForDML.saveOrUpdate(userActivity);
		}
		Redirect.goTo(PageListEnum.REPORT_COUPONS);
		
	} else if(event.getTarget() instanceof Paging) {
		/*String fromDate = MyCalendar.calendarToString(getStartDate(), MyCalendar.FORMAT_DATETIME_STYEAR);
		String endDate = MyCalendar.calendarToString(getEndDate(), MyCalendar.FORMAT_DATETIME_STYEAR);
		String statusStr = statusLbId.getSelectedItem().getLabel();*/
		Paging paging = (Paging) event.getTarget();
		int desiredPage = paging.getActivePage();
		PagingEvent pagingEvent = (PagingEvent) event;
		int pSize = pagingEvent.getPageable().getPageSize();
		int ofs = desiredPage * pSize;
		
		if(paging.getAttribute("onPaging").equals("coupon")) {
			this.couponRepListBottomPagingId.setActivePage(desiredPage);
			getCouponReports(ofs, (byte) pagingEvent.getPageable().getPageSize());
			
		}else if(paging.getAttribute("onPaging").equals("store")) {
			
			this.storePagingId.setActivePage(desiredPage);
			fillStoreRedemption(ofs, pSize);
			
		}
		
		
	}
	
	}
	//for export button
		 public void onClick$exportBtnStoreRedmId() {
				createWindowForRedm();
				custExportAsStore.setVisible(true);
				custExportAsStore.doHighlighted();
				
			
			}
		 public void onClick$exportBtnCouponReportId() {
				createWindow();
				custExport.setVisible(true);
				custExport.doHighlighted();

			}

		  public void onClick$exportBtnRedeemReportId() {
		        if (this.exportCbId.getSelectedItem().getValue().equals("csv")) {
		            this.exportCsv(this.exportCbId);
		        }
		        else if (this.exportCbId.getSelectedItem().getValue().equals("xls")) {
		          //  this.exportExcel(this.exportCbId);
		        }
		    }
		 
		 
		 
		 
		 
		 
		 
		 
			 public void onClick$selectAllAnchr$custExport() {
				 anchorEvent(true);
			 }

			 public void onClick$clearAllAnchr$custExport() {
				anchorEvent(false);
			 }
			 public void onClick$selectAllAnchr$custExportAsStore() {
				 anchorEvent(true);
			 }

			 public void onClick$clearAllAnchr$custExportAsStore() {
				anchorEvent(false);
			 }


			 public void anchorEvent(boolean flag) {
				List<Component> chkList = custExport$chkDivId.getChildren();
				 Checkbox tempChk = null;
				 for (int i = 0; i < chkList.size(); i++) {
					 if(!(chkList.get(i) instanceof Checkbox)) continue;

					 tempChk = (Checkbox)chkList.get(i);
					 tempChk.setChecked(flag);

				 } // for
			 }
		public void	createWindowForRedm(){
			try{
			Components.removeAllChildren(custExportAsStore$chkStoreDivId);

			Checkbox tempChk2 = new Checkbox("Store");
			tempChk2.setSclass("custCheck");
			tempChk2.setParent(custExportAsStore$chkStoreDivId);
			tempChk2.setChecked(true);
	  
		    tempChk2 = new Checkbox("Revenue");
			tempChk2.setSclass("custCheck");
			tempChk2.setParent(custExportAsStore$chkStoreDivId);
			tempChk2.setChecked(true);
			
		    tempChk2 = new Checkbox("No. Of Redemptions");
			tempChk2.setSclass("custCheck");
			tempChk2.setParent(custExportAsStore$chkStoreDivId);
			tempChk2.setChecked(true);
		} catch (Exception e) {
			logger.error("Exception ::", e);
		}
				}
		 public void onClick$selectFieldStoreBtnId$custExportAsStore() {

			 custExportAsStore.setVisible(false);
			 List<Component> chkList = custExportAsStore$chkStoreDivId.getChildren();

			 int indexes[]=new int[chkList.size()];
			 
			 boolean checked=false;

			 for(int i=0;i<chkList.size();i++) {
				 indexes[i]=-1;
			 } // for

			 Checkbox tempChk = null;

			 for (int i = 0; i < chkList.size(); i++) {
				 if(!(chkList.get(i) instanceof Checkbox)) continue;

				 tempChk = (Checkbox)chkList.get(i);

				 if(tempChk.isChecked()) {
					 indexes[i]=0;
					 checked=true;
				 }else{
						indexes[i]=-1;
					}

			 } // for


			 if( ((Checkbox)custExportAsStore$chkStoreDivId.getLastChild()).isChecked()) {

				 checked=true;
			 }

			 if(checked) {

				 int confirm=Messagebox.show("Do you want to export with selected fields ?", "Confirm",Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
				 if(confirm==1){
					 try{
						 String fileFormate = exportStoreRedmLbId.getSelectedItem().getValue().toString();
							if (fileFormate.contains("xls")) {
								exportExcelStoreRedm(fileFormate, indexes);
							} else if (fileFormate.contains("csv")) {
								exportCSVStoreRedm(fileFormate, indexes);
							}

					 }catch(Exception e){
						 logger.error("Exception caught :: ",e);
					 }
				 }
				 else{
					 custExportAsStore.setVisible(true);
				 }

			 }
			 else {

				 MessageUtil.setMessage("Please select atleast one field", "red");
				 custExportAsStore.setVisible(false);
			 }

		 }
		 public void exportExcelStoreRedm(String type,int[] indexes){
			 try {
					String userName = GetUser.getUserName();
					JdbcResultsetHandler jdbcResultsetHandler = null;
					
					String usersParentDirectory = (String) PropertyUtil.getPropertyValue("usersParentDirectory");

					String exportDir = usersParentDirectory + "/" + userName + "/Coupon/" ;
					File downloadDir = new File(exportDir);
					if (!downloadDir.exists()) {
						downloadDir.mkdirs();
					}

					String filePath = exportDir +  "StoreRedemption"+ "_" + System.currentTimeMillis() +"."+type;
					File file = new File(filePath);
					logger.debug("Writing to the file : " + filePath);
					FileOutputStream fileOut = new FileOutputStream(filePath);
					HSSFWorkbook hwb = new HSSFWorkbook();
					HSSFSheet sheet = hwb.createSheet("Promotion Report");
					HSSFRow row = sheet.createRow((short) 0);
					HSSFCell cell = null;
					String status = statusLbId.getSelectedItem().getLabel();
					
					row = sheet.createRow(0);
					int cellNo = 0;
					if (indexes[0] == 0) {
						
						cell = row.createCell((cellNo++));
						cell.setCellValue("Store");
					}
					if (indexes[1] == 0) {
						
						cell = row.createCell(cellNo++);
						cell.setCellValue("Revenue");
					}
					if (indexes[2] == 0) {
						
						cell = row.createCell(cellNo++);
						cell.setCellValue("No. Of Redemptions");
					}
						
					String qry = "SELECT store_number, ROUND(SUM(tot_revenue),2) AS REVENUE, COUNT(coupon_code_id) as COUNT " +
								"FROM coupon_codes WHERE orgId ="+users.getUserOrganization().getUserOrgId()+
										" AND store_number IS NOT NULL AND coupon_code IS NOT NULL AND redeemed_on between '"+storeFromDateStr+"' AND '"+storeToDateStr+"'" +
										"GROUP BY store_number ORDER BY redeemed_on DESC  ";
					
						jdbcResultsetHandler = new JdbcResultsetHandler();
						jdbcResultsetHandler.executeStmt(qry);
						
						ResultSet resultSet = jdbcResultsetHandler.getResultSet();
					  
						if (resultSet != null && !resultSet.isAfterLast()) {
							int rowId=1;
							while(resultSet.next()){
								logger.info("======="+resultSet.getString(2));
								row = sheet.createRow(rowId++);
								int columnId = 0;
								cell = null;
								if (indexes[0] == 0) {
									cell = row.createCell(columnId++);
									cell.setCellValue(fetchStoreName(resultSet.getString(1)));
								}
								if (indexes[1] == 0) {
									cell = row.createCell(columnId++);
									cell.setCellValue(resultSet.getString(2));
								}
								if (indexes[2] == 0) {
									cell = row.createCell(columnId++);
									cell.setCellValue(resultSet.getString(3));
								}
								
							  }
					     	}	
			
						
					     hwb.write(fileOut);
					    fileOut.flush();
						fileOut.close();
				
					Filedownload.save(file, "application/vnd.ms-excel");
					logger.debug("exited");

				} catch (Exception e) {
					logger.error("** Exception : ", e);
				}
			 
		 }
		 public void exportCSVStoreRedm(String type,int[] indexes){
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

					try {
										
						filePath = exportDir +  "Store_Redemption"+ "_" + System.currentTimeMillis() +"."+type;
						
						logger.debug("Download File path : " + filePath);
						File file = new File(filePath);
						bw = new BufferedWriter(new FileWriter(filePath));
						//bw.write("\"Email Address\",\"Reason\" \r\n");
						sb = new StringBuffer();

						 String udfFldsLabel= "";
							
						 if(indexes[0]==0) {
							 udfFldsLabel = "\""+"Store"+"\""+",";
						 }
						 if(indexes[1]==0) {
							 udfFldsLabel += "\""+"Revenue"+"\""+",";
						 }
						 if(indexes[2]==0) {
							 udfFldsLabel += "\""+"No. Of Redemptions"+"\""+",";
						 }

						//sb.append("\"Store\",\"Revenue\",\"No. Of Redemptions\"");
						 sb.append(udfFldsLabel);
						sb.append("\r\n");
						bw.write(sb.toString());
						

						String qry = "SELECT store_number, ROUND(SUM(tot_revenue),2) AS REVENUE, COUNT(coupon_code_id) as COUNT " +
								"FROM coupon_codes WHERE orgId ="+users.getUserOrganization().getUserOrgId()+
										" AND store_number IS NOT NULL AND coupon_code IS NOT NULL AND redeemed_on between '"+storeFromDateStr+"' AND '"+storeToDateStr+"'" +
										"GROUP BY store_number ORDER BY redeemed_on DESC  ";
					
						jdbcResultsetHandler = new JdbcResultsetHandler();
						jdbcResultsetHandler.executeStmt(qry);
						
						ResultSet resultSet = jdbcResultsetHandler.getResultSet();
					
						sb = new StringBuffer();
						if (resultSet != null && !resultSet.isAfterLast()) {
							while(resultSet.next()){
								/*sb.append(fetchStoreName(resultSet.getString(1))+",");
								sb.append(resultSet.getString(2)+",");
								sb.append(resultSet.getString(3));
								sb.append("\n");*/
								if(indexes[0]==0) {
									sb.append("\"");sb.append(fetchStoreName(resultSet.getString(1))); sb.append("\",");
								}
								if(indexes[1]==0) {
									sb.append("\"");sb.append(resultSet.getString(2)); sb.append("\",");
								}
								if(indexes[2]==0) {
									sb.append("\"");sb.append(resultSet.getString(3)); sb.append("\",");
								}
								sb.append("\r\n");
							}
							
						}
						bw.write(sb.toString());
						bw.flush();
						bw.close();
						
						Filedownload.save(file, "text/csv");
					} catch (Exception e) {
						logger.info("Exception :: ",e);
					}finally{
						if(jdbcResultsetHandler!=null ) jdbcResultsetHandler.destroy();
						userName = null;usersParentDirectory = null;exportDir = null;downloadDir = null;
						filePath = null;bw=null;
						jdbcResultsetHandler = null;jdbcResultsetHandler = null;
						//System.gc();
					}
					logger.debug("-- exit --");
		 }
			 public void createWindow()	{

					try {
						Components.removeAllChildren(custExport$chkDivId);

						Checkbox tempChk2 = new Checkbox("Discount Code Name");
						tempChk2.setSclass("custCheck");
						tempChk2.setParent(custExport$chkDivId);
						tempChk2.setWidth("200px");
						tempChk2.setChecked(true);
	              
					    tempChk2 = new Checkbox("Status");
						tempChk2.setSclass("custCheck");
						tempChk2.setParent(custExport$chkDivId);
						tempChk2.setWidth("200px");
						tempChk2.setChecked(true);
						
					    tempChk2 = new Checkbox("Last Modified Date");
						tempChk2.setSclass("custCheck");
						tempChk2.setParent(custExport$chkDivId);
						tempChk2.setWidth("200px");
						tempChk2.setChecked(true);


						tempChk2 = new Checkbox("Validity");
						tempChk2.setSclass("custCheck");
						tempChk2.setParent(custExport$chkDivId);
						tempChk2.setWidth("200px");
						tempChk2.setChecked(true);

						tempChk2 = new Checkbox("Discount");
						tempChk2.setSclass("custCheck");
						tempChk2.setParent(custExport$chkDivId);
						tempChk2.setWidth("200px");
						tempChk2.setChecked(true);

						tempChk2 = new Checkbox("Codes Generated");
						tempChk2.setSclass("custCheck");
						tempChk2.setParent(custExport$chkDivId);
						tempChk2.setWidth("200px");
						tempChk2.setChecked(true);

						tempChk2 = new Checkbox("Issued");
						tempChk2.setSclass("custCheck");
						tempChk2.setParent(custExport$chkDivId);
						tempChk2.setWidth("200px");
						tempChk2.setChecked(true);
						
						tempChk2 = new Checkbox("Redeemed");
						tempChk2.setSclass("custCheck");
						tempChk2.setParent(custExport$chkDivId);
						tempChk2.setWidth("200px");
						tempChk2.setChecked(true);
						
						tempChk2 = new Checkbox("Available");
						tempChk2.setSclass("custCheck");
						tempChk2.setParent(custExport$chkDivId);
						tempChk2.setWidth("200px");
						tempChk2.setChecked(true);

						tempChk2 = new Checkbox("Revenue");
						tempChk2.setSclass("custCheck");
						tempChk2.setParent(custExport$chkDivId);
						tempChk2.setWidth("200px");
						tempChk2.setChecked(true);
						
						tempChk2 = new Checkbox("Total Points Redeemed");
						tempChk2.setSclass("custCheck");
						tempChk2.setParent(custExport$chkDivId);
						tempChk2.setWidth("200px");
						tempChk2.setChecked(true);

					} catch (Exception e) {
						logger.error("Exception ::", e);
					}
				}
			 public void onClick$selectFieldBtnId$custExport() {

				 custExport.setVisible(false);
				 List<Component> chkList = custExport$chkDivId.getChildren();

				 int indexes[]=new int[chkList.size()];
				 
				 boolean checked=false;

				 for(int i=0;i<chkList.size();i++) {
					 indexes[i]=-1;
				 } // for

				 Checkbox tempChk = null;

				 for (int i = 0; i < chkList.size(); i++) {
					 if(!(chkList.get(i) instanceof Checkbox)) continue;

					 tempChk = (Checkbox)chkList.get(i);

					 if(tempChk.isChecked()) {
						 indexes[i]=0;
						 checked=true;
					 }else{
							indexes[i]=-1;
						}

				 } // for


				 if( ((Checkbox)custExport$chkDivId.getLastChild()).isChecked()) {

					 checked=true;
				 }

				 if(checked) {

					 int confirm=Messagebox.show("Do you want to export with selected fields ?", "Confirm",Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
					 if(confirm==1){
						 try{
							 String fileFormate = exportCouponReportLbId.getSelectedItem().getValue().toString();
								/*if (fileFormate.contains("xls")) {
									exportExcel(fileFormate, indexes);
								} else if (fileFormate.contains("csv")) {
									exportCSV(fileFormate, indexes);
								}*/
							 
							 switch(fileFormate)
							  {
								  case "xls": exportExcel(fileFormate, indexes);break;
								  case "csv": exportCSV(fileFormate, indexes);break;
								  case "csvzip": exportCsvZIP(indexes);break;
								  case "xlszip": exportExcelZip(indexes);break;
							  }

						 }catch(Exception e){
							 logger.error("Exception caught :: ",e);
						 }
					 }
					 else{
						 custExport.setVisible(true);
					 }

				 }
				 else {

					 MessageUtil.setMessage("Please select atleast one field", "red");
					 custExport.setVisible(false);
				 }

			 }
			 
			  public Map<String, POSMapping> getOrderedMappingSet(final List<POSMapping> mappingList) {
			        final Map<String, POSMapping> orderedMap = new LinkedHashMap<String, POSMapping>();
			        for (final String custFldkey : CouponsReportController.genFieldContMap.keySet()) {
			            for (final POSMapping posMapping : mappingList) {
			                if (posMapping.getCustomFieldName().equals(custFldkey)) {
			                    orderedMap.put(custFldkey, posMapping);
			                    break;
			                }
			            }
			        }
			        return orderedMap;
			    }
			    
			 public void exportCsv(final Combobox exportCbId) {
			        final long startTime = System.currentTimeMillis();
			        POSMappingDao posMappingDao = null;
			        String type = "";
			        String userName = "";
			        String usersParentDirectory = "";
			        String exportDir = "";
			        File downloadDir = null;
			        String name = "";
			        String filePath = "";
			        File file = null;
			        BufferedWriter bw = null;
			        List<POSMapping> posMappingsList = null;
			        Map<String, POSMapping> orderedMappingMap = null;
			        String udfFldsLabel = "";
			        String contactFieldsInDB = "";
			        JdbcResultsetHandler jdbcResultsetHandler = null;
                    String query = "";

			        JdbcResultsetHandler exportJdbcResultsetHandler = null;
			        Label_4833: {
			            try {
			                CouponsReportController.logger.debug("-- just entered --");
			                posMappingDao = (POSMappingDao)SpringUtil.getBean("posMappingDao");
			                final Long currUserId = GetUser.getUserId();
			                type = exportCbId.getSelectedItem().getLabel();
			                userName = GetUser.getUserName();
			                usersParentDirectory = PropertyUtil.getPropertyValue("usersParentDirectory");
			                exportDir = String.valueOf(usersParentDirectory) + "/" + userName + "/Coupon/";
			                downloadDir = new File(exportDir);
			                if (downloadDir.exists()) {
			                    try {
			                        FileUtils.deleteDirectory(downloadDir);
			                        CouponsReportController.logger.debug(String.valueOf(downloadDir.getName()) + " is deleted");
			                    }
			                    catch (Exception e) {
			                        CouponsReportController.logger.error("Exception ::", (Throwable)e);
			                        CouponsReportController.logger.debug(String.valueOf(downloadDir.getName()) + " is not deleted");
			                    }
			                }
			                if (!downloadDir.exists()) {
			                    downloadDir.mkdirs();
			                }
			                if (type.contains("csv")) {
			                    filePath = String.valueOf(exportDir) + "Coupon_" + "_";
			                    filePath = String.valueOf(filePath) + "_DiscountCodes.csv";
			                    CouponsReportController.logger.debug("Download File path : " + filePath);
			                    file = new File(filePath);
			                    try {
			                        bw = new BufferedWriter(new FileWriter(filePath));
			                    }
			                    catch (FileNotFoundException e4) {
			                        filePath = String.valueOf(exportDir) + "Coupon_" + System.currentTimeMillis() + "_";
			                        filePath = String.valueOf(filePath) + "_DiscountCodes.csv";
			                        file = new File(filePath);
			                        bw = new BufferedWriter(new FileWriter(filePath));
			                    }
			                    posMappingsList = (List<POSMapping>)posMappingDao.findByType("'Contacts'", currUserId);
			                    orderedMappingMap = this.getOrderedMappingSet(posMappingsList);
			                    for (final String custFldKey : orderedMappingMap.keySet()) {
			                        if (udfFldsLabel.length() > 0) {
			                            udfFldsLabel = String.valueOf(udfFldsLabel) + ",";
			                        }
			                        if (custFldKey.contentEquals(POSFieldsEnum.customerId.getOcAttr())) {
			                            continue;
			                        }
			                        udfFldsLabel = String.valueOf(udfFldsLabel) + "\"" + orderedMappingMap.get(custFldKey).getDisplayLabel().trim() + "\"";
			                        contactFieldsInDB = String.valueOf(contactFieldsInDB) + "," + CouponsReportController.genFieldContMap.get(custFldKey).trim();
			                    }
			                    CouponsReportController.logger.debug(udfFldsLabel);
			                    bw.write("\"Discount code\",\"Status\",\"Issued to\",\"Redeemed by\",\"Membership #\",\"Campaign Name\",\"Issued On\",\"Redeemed On\",\"Store\",\"Subsidiary\",\"Receipt#\",\"Discount On Item\",\"Total Discount\",\"Total Revenue\",\"Rewards Redeemed if any\",\"Item Info\"," + udfFldsLabel + " \r\n");
			                    final String searchQuery = "";
			                 
			                    
		                    query = "select cc.coupon_code,cc.status,cc.issued_to,cc.redeemed_to,cc.membership,cc.campaign_name,cc.issued_on,cc.redeemed_on,cc.store_number,cc.subsidiary_number,cc.receipt_number,cc.tot_discount,cc.tot_revenue,cc.used_loyalty_points,cc.value_code,cc.item_info" + contactFieldsInDB + " " + "from coupon_codes cc left join (select * from contacts where user_id=" + this.users.getUserId() + "  group by external_id) c on (cc.redeem_cust_id = c.external_id) " + "where coupon_id in " + "(select coupon_id from coupons where user_id=" + this.users.getUserId() + ") AND status  IN('" + "Redeemed" + "') order by cc.redeemed_on desc";
  
			                 String subQry = "";
			             	String status = statusLbId.getSelectedItem().getLabel();
							String promotionName = searchByPromoCodeNameTbId.getValue().trim();
			               	 
							if( fromDate != null && !fromDate.isEmpty() && endDate != null && !endDate.isEmpty() ) {
			         			
			               		 
			               		 logger.info("entering last modified date");
			               		 logger.info("entering fromDate date"+fromDate);
			               		 logger.info("entering endDate date"+endDate);

			               		 
			               		
			               		subQry += " AND co.user_last_modified_date BETWEEN  '" + fromDate + "' AND '" + endDate + "'";
			               	
			            //    	 query = "select cc.coupon_code,cc.status,cc.issued_to,cc.redeemed_to,cc.membership,cc.campaign_name,cc.issued_on,cc.redeemed_on,cc.store_number,cc.subsidiary_number,cc.receipt_number,cc.tot_discount,cc.tot_revenue,cc.used_loyalty_points,cc.value_code,cc.item_info" + contactFieldsInDB + " " + "from coupon_codes cc left join (select * from contacts where user_id=" + this.users.getUserId() + "  group by external_id) c on (cc.redeem_cust_id = c.external_id) left join (select co.user_last_modified_date,co.user_id,co.coupon_id from coupons co ) as co on co.coupon_id = cc.coupon_id where co.coupon_id in (select coupon_id from coupons)  AND status  IN('" + "Redeemed" + "')" +subQry+   " order by cc.redeemed_on desc";

			               	 
			               	 }
			               	if(status != null && !status.isEmpty() && !status.trim().equals("All")) {
			        			
			        			subQry += "  AND co.coupon_status='"	+ status + "'";
			        		}else {
			        			subQry += " AND co.coupon_status!='"+ status +"'";
			        		}
			        		
			        		if(promotionName != null && !promotionName.isEmpty()) {
			        				
			        				subQry += "  AND co.coupon_name like '%"+ promotionName+"%'";
			        		}
		                	
			        		query = "select cc.coupon_code,cc.status,cc.issued_to,cc.redeemed_to,cc.membership,cc.campaign_name,cc.issued_on,cc.redeemed_on,cc.store_number,cc.subsidiary_number,cc.receipt_number,cc.tot_discount,cc.tot_revenue,cc.used_loyalty_points,cc.value_code,cc.item_info" + contactFieldsInDB + " " + "from coupon_codes cc left join (select * from contacts where user_id=" + this.users.getUserId() + "  group by external_id) c on (cc.redeem_cust_id = c.external_id) left join (select co.user_last_modified_date,co.user_id,co.coupon_id,co.coupon_status,co.coupon_name from coupons co ) as co on co.coupon_id = cc.coupon_id where co.coupon_id in (select coupon_id from coupons)  AND status  IN('" + "Redeemed" + "')  AND co.user_id="+this.users.getUserId()+"" +subQry+   " order by cc.redeemed_on desc";

			               	 
			               	 
			               	 
			                    final boolean statusFlag = false;
			                  //  query = "select cc.coupon_code,cc.status,cc.issued_to,cc.redeemed_to,cc.membership,cc.campaign_name,cc.issued_on,cc.redeemed_on,cc.store_number,cc.subsidiary_number,cc.receipt_number,cc.tot_discount,cc.tot_revenue,cc.used_loyalty_points,cc.value_code,cc.item_info" + contactFieldsInDB + " " + "from coupon_codes cc left join (select * from contacts where user_id=" + this.users.getUserId() + "  group by external_id) c on (cc.redeem_cust_id = c.external_id) " + "where coupon_id in " + "(select coupon_id from coupons where user_id=" + this.users.getUserId() + ") AND status  IN('" + "Redeemed" + "')"   + query +   " order by cc.redeemed_on desc";
			                    logger.info("query  is"+query);

			                    logger.info("subquery is"+subQry);
			                    
			                    
			                 /*   if(subQry!=null)  { 
				                    logger.info(" entering subquery is");

				                	 query = "select cc.coupon_codes,cc.status,cc.issued_to,cc.redeemed_to,cc.membership,cc.campaign_name,cc.issued_on,cc.redeemed_on,cc.store_number,cc.subsidiary_number,cc.receipt_number,cc.tot_discount,cc.tot_revenue,cc.used_loyalty_points,cc.value_code,cc.item_info" + contactFieldsInDB + " " + "from coupon_codes cc left join (select * from contacts where user_id=" + this.users.getUserId() + "  group by external_id) c on (cc.redeem_cust_id = c.external_id) left join (select co.user_last_modified_date,co.coupon_id from coupons co ) as co on co.coupon_id = cc.coupon_id where co.coupon_id in (select coupon_id from coupons)  AND status  IN('" + "Redeemed" + "')" +subQry  + query +   " order by cc.redeemed_on desc";
		
			                    }*/
			                   
			                   
			                    //	query = "select cc.coupon_code,cc.status,cc.issued_to,cc.redeemed_to,cc.membership,cc.campaign_name,cc.issued_on,cc.redeemed_on,cc.store_number,cc.subsidiary_number,cc.receipt_number,cc.tot_discount,cc.tot_revenue,cc.used_loyalty_points,cc.value_code,cc.item_info" + contactFieldsInDB + " " + "from coupon_codes cc left join (select * from contacts where user_id=" + this.users.getUserId() + "  group by external_id) c on (cc.redeem_cust_id = c.external_id) " + "where coupon_id in " + "(select coupon_id from coupons where user_id=" + this.users.getUserId() + ") AND status  IN('" + "Redeemed" + "') order by cc.redeemed_on desc";
				                  
			                    																																																								
			                    
			        /*    select cc.coupon_code,cc.status,cc.issued_to,cc.redeemed_to,cc.membership,cc.campaign_name,cc.issued_on,cc.redeemed_on,cc.store_number,cc.subsidiary_number,cc.receipt_number,cc.tot_discount,cc.tot_revenue,cc.used_loyalty_points,cc.value_code,cc.item_info,c.email_id,c.mobile_phone,c.first_name,c.last_name,
			            c.address_one,c.address_two,c.city,c.state,c.country,c.zip,c.gender,c.birth_day,c.anniversary_day,c.home_store,c.udf1,c.udf2,c.udf3,c.udf4,c.udf5,c.udf6,c.udf8,c.udf9,c.udf10,c.udf12,c.udf13,c.udf14,c.udf15 from coupon_codes cc left join (select * from contacts where user_id=3  group by external_id) c on (cc.redeem_cust_id = c.external_id) left join (select co.user_last_modified_date,co.coupon_id from coupons co ) as co on co.coupon_id = cc.coupon_id where co.coupon_id in (select coupon_id from coupons)  AND status  IN('Redeemed') AND    
			                  
			            		
			            		
			            		co.user_last_modified_date  between '2023-02-01 ' AND '2023-03-03' order by cc.redeemed_on desc;        
			                    
			                    
			                    
			                    logger.info("entering query"+query);*/

			                    
			                    String exportQuery = "select c.id,c.mobile_status,c.email_status,c.subscription_type,c.cf_value,c.membership,c.domain,c.categories,c.optin_medium,c.home_phone,c.subsidiary_number,c.doc_sid,c.discountOnItem,c.discount,c.tot_revenue,c.loyalty_used_points,c.item_info" + contactFieldsInDB + " from tempexportreport c where 1=2";
			                    exportQuery = exportQuery.replaceFirst("c.birth_day", "c.urls");
			                    exportQuery = exportQuery.replaceFirst("c.anniversary_day", "c.created_date");
			                    CouponsReportController.logger.info("exportQuery is :: " + exportQuery);
			                    jdbcResultsetHandler = new JdbcResultsetHandler();
			                    jdbcResultsetHandler.executeStmt(query);
			                    exportJdbcResultsetHandler = new JdbcResultsetHandler();
			                    exportJdbcResultsetHandler.executeStmt(exportQuery, true);
			                    final ResultSet resultSet = jdbcResultsetHandler.getResultSet();
			                    final ResultSet exportResultSet = exportJdbcResultsetHandler.getResultSet();
			                  
			                  /*  if(exportResultSet.getFetchSize()==0) {
			                	logger.info("entering if ");
			                    	
			                    	MessageUtil.setMessage("No records exist in the selected search.", "color:red", "TOP");
			        			return;
			                    }*/
			                    
			                    final DecimalFormat decimalFormat = new DecimalFormat("#0.00");
			                    double random = 0.0;
			                    contactFieldsInDB = contactFieldsInDB.substring(1);
			                    Calendar cal = Calendar.getInstance();
			                    while (resultSet.next()) {
			                        final String itemInfo = resultSet.getString("item_info");
			                        CouponsReportController.logger.info("item info from resultset " + itemInfo);
			                        if (itemInfo != null) {
			                            String[] itemInfosArr = null;
			                            double totdisc = 0.0;
			                            if (itemInfo.contains("|")) {
			                                itemInfosArr = itemInfo.split("\\|");
			                                String[] array;
			                                for (int length = (array = itemInfosArr).length, i = 0; i < length; ++i) {
			                                    final String eachitem = array[i];
			                                    final String[] discAmt = eachitem.split(new StringBuilder().append(eachitem.startsWith("P:") ? "," : Character.valueOf(':')).toString());
			                                    totdisc += ((discAmt.length >= 3) ? (Double.parseDouble(discAmt[2]) * Double.parseDouble(discAmt[1])) : Double.parseDouble(discAmt[1]));
			                                }
			                                String[] array2;
			                                for (int length2 = (array2 = itemInfosArr).length, j = 0; j < length2; ++j) {
			                                    final String info = array2[j];
			                                    random = Math.random();
			                                    exportResultSet.moveToInsertRow();
			                                    exportResultSet.updateString("id", new StringBuilder().append(this.users.getUserId()).append(random).append(System.currentTimeMillis()).toString());
			                                    String[] split;
			                                    for (int length3 = (split = contactFieldsInDB.split(",")).length, k = 0; k < length3; ++k) {
			                                        String custFldkey = split[k];
			                                        custFldkey = custFldkey.replaceFirst("c.", "");
			                                        if (custFldkey.equals("birth_day")) {
			                                            try {
			                                                cal = Calendar.getInstance();
			                                                cal.setTimeInMillis(resultSet.getTimestamp(custFldkey).getTime());
			                                            }
			                                            catch (NullPointerException e5) {
			                                                cal = null;
			                                            }
			                                            exportResultSet.updateString("urls", (resultSet.getTimestamp(custFldkey) != null) ? MyCalendar.calendarToString(cal, "dd MMM, yyyy HH:mm aaa") : null);
			                                        }
			                                        else if (custFldkey.equals("anniversary_day")) {
			                                            exportResultSet.updateTimestamp("created_date", (resultSet.getTimestamp(custFldkey) != null) ? resultSet.getTimestamp(custFldkey) : null);
			                                        }
			                                        else {
			                                            exportResultSet.updateString(custFldkey, resultSet.getString(custFldkey));
			                                        }
			                                    }
			                                    exportResultSet.updateString("mobile_status", resultSet.getString("coupon_code"));
			                                    if (resultSet.getString("status") != null && resultSet.getString("status").equalsIgnoreCase("Active")) {
			                                        exportResultSet.updateString("email_status", "Issued");
			                                    }
			                                    else if (resultSet.getString("status") != null && resultSet.getString("status").equalsIgnoreCase("Inventory")) {
			                                        exportResultSet.updateString("email_status", "Not Issued");
			                                    }
			                                    else {
			                                        exportResultSet.updateString("email_status", resultSet.getString("status"));
			                                    }
			                                    exportResultSet.updateString("subscription_type", resultSet.getString("issued_to"));
			                                    exportResultSet.updateString("cf_value", resultSet.getString("redeemed_to"));
			                                    exportResultSet.updateString("domain", resultSet.getString("campaign_name"));
			                                    try {
			                                        cal = Calendar.getInstance();
			                                        cal.setTimeInMillis(resultSet.getTimestamp("issued_on").getTime());
			                                    }
			                                    catch (NullPointerException e6) {
			                                        cal = null;
			                                    }
			                                    exportResultSet.updateString("categories", MyCalendar.calendarToString(cal, "dd MMM, yyyy HH:mm aaa", this.clientTimeZone));
			                                    try {
			                                        cal = Calendar.getInstance();
			                                        cal.setTimeInMillis(resultSet.getTimestamp("redeemed_on").getTime());
			                                    }
			                                    catch (NullPointerException e6) {
			                                        cal = null;
			                                    }
			                                    exportResultSet.updateString("optin_medium", MyCalendar.calendarToString(cal, "dd MMM, yyyy HH:mm aaa", this.clientTimeZone));
			                                    exportResultSet.updateString("home_phone", (resultSet.getString("store_number") != null) ? this.fetchStoreName(resultSet.getString("store_number")) : "");
			                                    exportResultSet.updateString("doc_sid", resultSet.getString("receipt_number"));
			                                    exportResultSet.updateDouble("discount", totdisc);
			                                    exportResultSet.updateString("tot_revenue", (Double.valueOf(resultSet.getDouble("tot_revenue")) != null) ? (new StringBuilder(String.valueOf(Double.parseDouble(decimalFormat.format(resultSet.getDouble("tot_revenue"))))).toString().equals("0.0") ? "null" : new StringBuilder(String.valueOf(Double.parseDouble(decimalFormat.format(resultSet.getDouble("tot_revenue"))))).toString()) : "null");
			                                    exportResultSet.updateString("loyalty_used_points", (Double.valueOf(resultSet.getDouble("used_loyalty_points")) != null) ? (String.valueOf(resultSet.getDouble("used_loyalty_points")) + " " + ((resultSet.getString("value_code") != null) ? resultSet.getString("value_code") : "")) : "0");
			                                    final String[] discAmt = info.split(new StringBuilder().append(info.startsWith("P:") ? "," : Character.valueOf(':')).toString());
			                                    exportResultSet.updateString("item_info", discAmt[0].startsWith("P:") ? discAmt[0].split(":")[1] : discAmt[0]);
			                                    exportResultSet.updateDouble("discountOnItem", Double.parseDouble((discAmt.length >= 3) ? discAmt[2] : discAmt[1]));
			                                    exportResultSet.updateString("membership", (resultSet.getString("membership") != null) ? resultSet.getString("membership") : "");
			                                    exportResultSet.updateString("subsidiary_number", (resultSet.getString("subsidiary_number") != null) ? resultSet.getString("subsidiary_number") : "");
			                                    exportResultSet.insertRow();
			                                    exportResultSet.moveToCurrentRow();
			                                }
			                            }
			                            else {
			                                random = Math.random();
			                                exportResultSet.moveToInsertRow();
			                                exportResultSet.updateString("id", new StringBuilder().append(this.users.getUserId()).append(random).append(System.currentTimeMillis()).toString());
			                                String[] split2;
			                                for (int length4 = (split2 = contactFieldsInDB.split(",")).length, l = 0; l < length4; ++l) {
			                                    String custFldkey2 = split2[l];
			                                    custFldkey2 = custFldkey2.replaceFirst("c.", "");
			                                    if (custFldkey2.equals("birth_day")) {
			                                        try {
			                                            cal = Calendar.getInstance();
			                                            cal.setTimeInMillis(resultSet.getTimestamp(custFldkey2).getTime());
			                                        }
			                                        catch (NullPointerException e6) {
			                                            cal = null;
			                                        }
			                                        exportResultSet.updateString("urls", (resultSet.getTimestamp(custFldkey2) != null) ? MyCalendar.calendarToString(cal, "dd MMM, yyyy HH:mm aaa") : null);
			                                    }
			                                    else if (custFldkey2.equals("anniversary_day")) {
			                                        exportResultSet.updateTimestamp("created_date", (resultSet.getTimestamp(custFldkey2) != null) ? resultSet.getTimestamp(custFldkey2) : null);
			                                    }
			                                    else {
			                                        exportResultSet.updateString(custFldkey2, resultSet.getString(custFldkey2));
			                                    }
			                                }
			                                exportResultSet.updateString("mobile_status", resultSet.getString("coupon_code"));
			                                if (resultSet.getString("status") != null && resultSet.getString("status").equalsIgnoreCase("Active")) {
			                                    exportResultSet.updateString("email_status", "Issued");
			                                }
			                                else if (resultSet.getString("status") != null && resultSet.getString("status").equalsIgnoreCase("Inventory")) {
			                                    exportResultSet.updateString("email_status", "Not Issued");
			                                }
			                                else {
			                                    exportResultSet.updateString("email_status", resultSet.getString("status"));
			                                }
			                                exportResultSet.updateString("subscription_type", resultSet.getString("issued_to"));
			                                exportResultSet.updateString("cf_value", resultSet.getString("redeemed_to"));
			                                exportResultSet.updateString("domain", resultSet.getString("campaign_name"));
			                                try {
			                                    cal = Calendar.getInstance();
			                                    cal.setTimeInMillis(resultSet.getTimestamp("issued_on").getTime());
			                                }
			                                catch (NullPointerException e7) {
			                                    cal = null;
			                                }
			                                exportResultSet.updateString("categories", MyCalendar.calendarToString(cal, "MM/dd/yyyy HH:mm aaa", this.clientTimeZone));
			                                try {
			                                    cal = Calendar.getInstance();
			                                    cal.setTimeInMillis(resultSet.getTimestamp("redeemed_on").getTime());
			                                }
			                                catch (NullPointerException e7) {
			                                    cal = null;
			                                }
			                                exportResultSet.updateString("optin_medium", MyCalendar.calendarToString(cal, "MM/dd/yyyy HH:mm aaa", this.clientTimeZone));
			                                exportResultSet.updateString("home_phone", (resultSet.getString("store_number") != null) ? this.fetchStoreName(resultSet.getString("store_number")) : "");
			                                exportResultSet.updateString("doc_sid", resultSet.getString("receipt_number"));
			                                exportResultSet.updateDouble("discount", (Double.valueOf(resultSet.getDouble("tot_discount")) != null) ? Double.valueOf(Double.parseDouble(decimalFormat.format(resultSet.getDouble("tot_discount")))) : null);
			                                exportResultSet.updateString("tot_revenue", (Double.valueOf(resultSet.getDouble("tot_revenue")) != null) ? (new StringBuilder(String.valueOf(Double.parseDouble(decimalFormat.format(resultSet.getDouble("tot_revenue"))))).toString().equals("0.0") ? "null" : new StringBuilder(String.valueOf(Double.parseDouble(decimalFormat.format(resultSet.getDouble("tot_revenue"))))).toString()) : "null");
			                            
			                                
			                                
			                                
			                                
			                                exportResultSet.updateString("loyalty_used_points", (Double.valueOf(resultSet.getDouble("used_loyalty_points")) != null) ? (String.valueOf(resultSet.getDouble("used_loyalty_points")) + " " + ((resultSet.getString("value_code") != null) ? resultSet.getString("value_code") : "")) : "0");    
			                                
			                                
			                                
			                                
			                                
			                           //     exportResultSet.updateString("loyalty_used_points", (Double.valueOf(resultSet.getDouble("used_loyalty_points")) != null) ? (String.valueOf(Double.parseDouble(resultSet.getDouble("used_loyalty_points"))) + " " + ((resultSet.getString("value_code") != null) ? resultSet.getString("value_code") : "")) : "0");
			                                final String[] discAmt2 = itemInfo.split(new StringBuilder().append(itemInfo.startsWith("P:") ? "," : Character.valueOf(':')).toString());
			                                exportResultSet.updateString("item_info", discAmt2[0].startsWith("P:") ? discAmt2[0].split(":")[1] : discAmt2[0]);
			                                exportResultSet.updateDouble("discountOnItem", Double.parseDouble((discAmt2.length >= 3) ? discAmt2[2] : discAmt2[1]));
			                                exportResultSet.updateString("membership", (resultSet.getString("membership") != null) ? resultSet.getString("membership") : "");
			                                exportResultSet.updateString("subsidiary_number", (resultSet.getString("subsidiary_number") != null) ? resultSet.getString("subsidiary_number") : "");
			                                exportResultSet.insertRow();
			                                exportResultSet.moveToCurrentRow();
			                            }
			                        }
			                        else {
			                            random = Math.random();
			                            exportResultSet.moveToInsertRow();
			                            exportResultSet.updateString("id", new StringBuilder().append(this.users.getUserId()).append(random).append(System.currentTimeMillis()).toString());
			                            String[] split3;
			                            for (int length5 = (split3 = contactFieldsInDB.split(",")).length, n = 0; n < length5; ++n) {
			                                String custFldkey3 = split3[n];
			                                custFldkey3 = custFldkey3.replaceFirst("c.", "");
			                                if (custFldkey3.equals("birth_day")) {
			                                    try {
			                                        cal = Calendar.getInstance();
			                                        cal.setTimeInMillis(resultSet.getTimestamp(custFldkey3).getTime());
			                                    }
			                                    catch (NullPointerException e8) {
			                                        cal = null;
			                                    }
			                                    exportResultSet.updateString("urls", (resultSet.getTimestamp(custFldkey3) != null) ? MyCalendar.calendarToString(cal, "dd MMM, yyyy HH:mm aaa") : null);
			                                }
			                                else if (custFldkey3.equals("anniversary_day")) {
			                                    exportResultSet.updateTimestamp("created_date", (resultSet.getTimestamp(custFldkey3) != null) ? resultSet.getTimestamp(custFldkey3) : null);
			                                }
			                                else {
			                                    exportResultSet.updateString(custFldkey3, resultSet.getString(custFldkey3));
			                                }
			                            }
			                            exportResultSet.updateString("mobile_status", resultSet.getString("coupon_code"));
			                            if (resultSet.getString("status") != null && resultSet.getString("status").equalsIgnoreCase("Active")) {
			                                exportResultSet.updateString("email_status", "Issued");
			                            }
			                            else if (resultSet.getString("status") != null && resultSet.getString("status").equalsIgnoreCase("Inventory")) {
			                                exportResultSet.updateString("email_status", "Not Issued");
			                            }
			                            else {
			                                exportResultSet.updateString("email_status", resultSet.getString("status"));
			                            }
			                            exportResultSet.updateString("subscription_type", resultSet.getString("issued_to"));
			                            exportResultSet.updateString("cf_value", resultSet.getString("redeemed_to"));
			                            exportResultSet.updateString("domain", resultSet.getString("campaign_name"));
			                            try {
			                                cal = Calendar.getInstance();
			                                cal.setTimeInMillis(resultSet.getTimestamp("issued_on").getTime());
			                            }
			                            catch (NullPointerException e9) {
			                                cal = null;
			                            }
			                            exportResultSet.updateString("categories", MyCalendar.calendarToString(cal, "MM/dd/yyyy HH:mm aaa", this.clientTimeZone));
			                            try {
			                                cal = Calendar.getInstance();
			                                cal.setTimeInMillis(resultSet.getTimestamp("redeemed_on").getTime());
			                            }
			                            catch (NullPointerException e9) {
			                                cal = null;
			                            }
			                            exportResultSet.updateString("optin_medium", MyCalendar.calendarToString(cal, "MM/dd/yyyy HH:mm aaa", this.clientTimeZone));
			                            exportResultSet.updateString("home_phone", (resultSet.getString("store_number") != null) ? this.fetchStoreName(resultSet.getString("store_number")) : "");
			                            exportResultSet.updateString("doc_sid", resultSet.getString("receipt_number"));
			                            exportResultSet.updateDouble("discount", (Double.valueOf(resultSet.getDouble("tot_discount")) != null) ? Double.valueOf(Double.parseDouble(decimalFormat.format(resultSet.getDouble("tot_discount")))) : null);
			                            exportResultSet.updateString("tot_revenue", (Double.valueOf(resultSet.getDouble("tot_revenue")) != null) ? (new StringBuilder(String.valueOf(Double.parseDouble(decimalFormat.format(resultSet.getDouble("tot_revenue"))))).toString().equals("0.0") ? "null" : new StringBuilder(String.valueOf(Double.parseDouble(decimalFormat.format(resultSet.getDouble("tot_revenue"))))).toString()) : "null");
			                            exportResultSet.updateString("loyalty_used_points", (Double.valueOf(resultSet.getDouble("used_loyalty_points")) != null) ? (String.valueOf((resultSet.getDouble("used_loyalty_points")) + " " + ((resultSet.getString("value_code") != null) ? resultSet.getString("value_code") : ""))) : "0");
			                            exportResultSet.updateString("item_info", (resultSet.getString("item_info") != null) ? resultSet.getString("item_info") : "");
			                            exportResultSet.updateDouble("discountOnItem", (Double.valueOf(resultSet.getDouble("tot_discount")) != null) ? Double.valueOf(Double.parseDouble(decimalFormat.format(resultSet.getDouble("tot_discount")))) : null);
			                            exportResultSet.updateString("membership", (resultSet.getString("membership") != null) ? resultSet.getString("membership") : "");
			                            exportResultSet.updateString("subsidiary_number", (resultSet.getString("subsidiary_number") != null) ? resultSet.getString("subsidiary_number") : "");
			                            exportResultSet.insertRow();
			                            exportResultSet.moveToCurrentRow();
			                        }
			                    }
			                	OCCSVWriter csvWriter = new OCCSVWriter(bw);
			                   
			                    Label_4571: {
			                        try {
			                            csvWriter = new OCCSVWriter(bw);
			                            csvWriter.writeAll(exportResultSet, false, 1);
			                        }
			                        catch (Exception e2) {
			                            CouponsReportController.logger.error("exception while initiating writer ", (Throwable)e2);
			                            break Label_4571;
			                        }
			                        finally {
			                            bw.flush();
			                            csvWriter.flush();
			                            bw.close();
			                            csvWriter.close();
			                            jdbcResultsetHandler.destroy();
			                            bw = null;
			                            jdbcResultsetHandler = null;
			                        }
			                      
			                    }
			                    Filedownload.save(file, "text/csv");
			                }
			            }
			            catch (Exception e3) {
			                CouponsReportController.logger.error("exception while initiating writer ", (Throwable)e3);
			                break Label_4833;
			            }
			            finally {
			                if (exportJdbcResultsetHandler != null) {
			                    exportJdbcResultsetHandler.rollback();
			                    exportJdbcResultsetHandler.destroy();
			                    exportJdbcResultsetHandler = null;
			                }
			                if (jdbcResultsetHandler != null) {
			                    jdbcResultsetHandler.destroy();
			                }
			                posMappingDao = null;
			                type = null;
			                userName = null;
			                usersParentDirectory = null;
			                exportDir = null;
			                downloadDir = null;
			                name = null;
			                filePath = null;
			                file = null;
			                bw = null;
			                posMappingsList = null;
			                orderedMappingMap = null;
			                udfFldsLabel = null;
			                contactFieldsInDB = null;
			                jdbcResultsetHandler = null;
			                jdbcResultsetHandler = null;
			            }
			            if (exportJdbcResultsetHandler != null) {
			                exportJdbcResultsetHandler.rollback();
			                exportJdbcResultsetHandler.destroy();
			                exportJdbcResultsetHandler = null;
			            }
			            if (jdbcResultsetHandler != null) {
			                jdbcResultsetHandler.destroy();
			            }
			            posMappingDao = null;
			            type = null;
			            userName = null;
			            usersParentDirectory = null;
			            exportDir = null;
			            downloadDir = null;
			            name = null;
			            filePath = null;
			            file = null;
			            bw = null;
			            posMappingsList = null;
			            orderedMappingMap = null;
			            udfFldsLabel = null;
			            contactFieldsInDB = null;
			            jdbcResultsetHandler = null;
			            jdbcResultsetHandler = null;
			        }
			        final long endTime = System.currentTimeMillis();
			        CouponsReportController.logger.fatal("Time taken to export coupons is :::::::::::::::::::::::: " + (endTime - startTime));
              		 logger.info("entering query"+query);

			 
			 } 
			 
			 
			 
			 
			 
			 
			 
			 private void exportCSV(String type, int[] indexes) {
				 	logger.debug("-- just entered into exportCSV --");
					StringBuffer sb = null;
					String userName = GetUser.getUserName();
					String usersParentDirectory = (String)PropertyUtil.getPropertyValue("usersParentDirectory");
					String exportDir = usersParentDirectory + "/" + userName + "/Export/" ;
					File downloadDir = new File(exportDir);
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
					
						
						String filePath = exportDir +  "DiscountCode_Reports_" + System.currentTimeMillis() + "." +type;
							MyCalendar.calendarToString(Calendar.getInstance(), MyCalendar.FORMAT_YEARTOSEC, clientTimeZone);
							try {
				
									logger.debug("Download File path : " + filePath);
									File file = new File(filePath);
									BufferedWriter bw = new BufferedWriter(new FileWriter(filePath));
									String status = statusLbId.getSelectedItem().getLabel();
									String promotionName = searchByPromoCodeNameTbId.getValue().trim();
									int count = couponsDao.findCoupBasedOnDates(users.getUserOrganization().getUserOrgId(),status,fromDate, endDate,promotionName);
									if(count == 0) {
										Messagebox.show("No discount code report found.","Info", Messagebox.OK,Messagebox.INFORMATION);
										return;
									}
									 String udfFldsLabel= "";
							
									 if(indexes[0]==0) {
										 udfFldsLabel = "\""+"Discount Code Name"+"\""+",";
									 }
									 if(indexes[1]==0) {
										 udfFldsLabel += "\""+"Status"+"\""+",";
									 }
									 if(indexes[2]==0) {
										 udfFldsLabel += "\""+"Last Modified Date"+"\""+",";
									 }
									 if(indexes[3]==0) {
										 udfFldsLabel += "\""+"Validity"+"\""+",";
									 }
									 if(indexes[4]==0) {
										 udfFldsLabel += "\""+"Discount"+"\""+",";
									 }	
									 if(indexes[5]==0) {
										 udfFldsLabel += "\""+"Codes Generated"+"\""+",";
									 }	
									 if(indexes[6]==0) {

										 udfFldsLabel += "\""+"Issued"+"\""+",";
									 }
									 if(indexes[7]==0) {

										 udfFldsLabel += "\""+"Redeemed"+"\""+",";
									 }
									 if(indexes[8]==0) {

										 udfFldsLabel += "\""+"Available"+"\""+",";
									 }
									 if(indexes[9]==0) {

										 udfFldsLabel += "\""+"Revenue"+"\""+",";
									 }
									 if(indexes[10]==0) {

										 udfFldsLabel += "\""+"Total Points Redeemed"+"\""+",";
									 }
									 sb = new StringBuffer();
									 sb.append(udfFldsLabel);
									 sb.append("\r\n");

									 bw.write(sb.toString());
									 //System.gc();
									
									int size = count;
									List<Coupons> couponsList = null;
									for (int i = 0; i < count; i+=size) {
										sb = new StringBuffer();
										//pageSize = Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel());
										couponsList = couponsDao.findCouponsByOrgId(users.getUserOrganization().getUserOrgId(),0,count, status, fromDate,endDate,orderby_colName,desc_Asc,promotionName);
									          if(couponsList!=null && couponsList.size()>0)
											for (Coupons coupon : couponsList) {
												String validity = null;
												if(coupon.getExpiryType().equals(Constants.COUP_VALIDITY_PERIOD_STATIC)){
													
													validity =MyCalendar.calendarToString(coupon.getCouponCreatedDate(), MyCalendar.FORMAT_DATEONLY_GENERAL)+" - "+MyCalendar.calendarToString(coupon.getCouponExpiryDate(),MyCalendar.FORMAT_DATEONLY_GENERAL);
												//	validity =MyCalendar.calendarToString(coupon.getCouponCreatedDate(), MyCalendar.FORMAT_DATEONLY_GENERAL, clientTimeZone)+" - "+MyCalendar.calendarToString(coupon.getCouponExpiryDate(),MyCalendar.FORMAT_DATEONLY_GENERAL ,clientTimeZone);
												}
												else if(coupon.getExpiryType().equals(Constants.COUP_VALIDITY_PERIOD_DYNAMIC)){
													validity = "Dynamic";
												}

												String discount =  "";
												if(coupon.getDiscountType().equals("Percentage")) {
													discount = "% on ";
												}else {
													discount = "Curr. on ";
												}
												//discount= discount+" "+coupObj.getDiscountCriteria();
												if(coupon.getDiscountCriteria().trim().contains("SKU")) {
													discount += " Product";
												}else {
													discount += " Receipt";
												}
													
												String generateCode=""; 
												String availableStr="";
												if(coupon.getAutoIncrCheck()){
													generateCode="Unlimited";
													availableStr="Unlimited";
												}else{
													generateCode=""+coupon.getTotalQty();
													availableStr=""+(coupon.getAvailable()!= null ? coupon.getAvailable():"");
												}
												//last modified date
												String lastModifiedDate=null;
												lastModifiedDate=MyCalendar.calendarToString(coupon.getUserLastModifiedDate(), MyCalendar.FORMAT_DATEONLY_GENERAL,clientTimeZone );
												
												DecimalFormat decimalFormat = new DecimalFormat("#0.00");
												if(indexes[0]==0) {
													sb.append("\"");sb.append(coupon.getCouponName().trim()); sb.append("\",");
												}
												if(indexes[1]==0) {
													sb.append("\"");sb.append(coupon.getStatus()); sb.append("\",");
												}
												if(indexes[2]==0) {
													sb.append("\"");sb.append(lastModifiedDate); sb.append("\",");
												}
												if(indexes[3]==0) {
													sb.append("\"");sb.append(validity); sb.append("\",");
												}
												if(indexes[4]==0) {
													sb.append("\"");sb.append(discount); sb.append("\",");
												}
												if(indexes[5]==0) {
													sb.append("\"");sb.append(generateCode); sb.append("\",");
												}
												if(indexes[6]==0) {
												sb.append("\"");sb.append(coupon.getIssued() != null? ""+coupon.getIssued():""); sb.append("\",");
												}
												if(indexes[7]==0) {
												sb.append("\"");sb.append(coupon.getRedeemed() != null?  ""+coupon.getRedeemed():"" ); sb.append("\",");
												}
												if(indexes[8]==0) {
												sb.append("\"");sb.append(availableStr); sb.append("\",");
												}
												if(indexes[9]==0) {
													sb.append("\"");sb.append(coupon.getTotRevenue() != null? ""+decimalFormat.format(coupon.getTotRevenue()):"" ); sb.append("\",");
												}
												if(indexes[10]==0) {
													sb.append("\"");sb.append(coupon.getUsedLoyaltyPoints()!= null? ""+coupon.getUsedLoyaltyPoints().intValue():"0" ); sb.append("\",");
												}
												sb.append("\r\n");
											}
									
							
										bw.write(sb.toString());
										couponsList = null;
										sb = null;
										//System.gc();
									}
									bw.flush();
									bw.close();
									Filedownload.save(file, "text/plain");
								} catch (IOException e) {
									logger.error("Exception ::",e);
									
								}
								logger.debug("-- exit --");
				
				}
			 public void exportExcel(String type, int[] indexes) {
					try {
						String userName = GetUser.getUserName();
						String usersParentDirectory = (String) PropertyUtil.getPropertyValue("usersParentDirectory");

						File downloadDir = new File(usersParentDirectory + "/" + userName + "/List/download/");

						if (!downloadDir.exists()) {
							downloadDir.mkdirs();
						}

						String filePath = usersParentDirectory + "/" + userName + "/List/download/DiscountCode_Reports_"
								+ System.currentTimeMillis() + "." + type;
						File file = new File(filePath);
						logger.debug("Writing to the file : " + filePath);
						FileOutputStream fileOut = new FileOutputStream(filePath);
						HSSFWorkbook hwb = new HSSFWorkbook();
						HSSFSheet sheet = hwb.createSheet("Promotion Report");
						HSSFRow row = sheet.createRow((short) 0);
						HSSFCell cell = null;
						String status = statusLbId.getSelectedItem().getLabel();
						String promotionName = searchByPromoCodeNameTbId.getValue().trim();
						int count = couponsDao.findCoupBasedOnDates(users.getUserOrganization().getUserOrgId(),status,fromDate, endDate,promotionName);
					
						if (count == 0) {
							Messagebox.show("No discount code report found.", "Info", Messagebox.OK, Messagebox.INFORMATION);
							return;
					      }
						
						row = sheet.createRow(0);
						int cellNo = 0;
						if (indexes[0] == 0) {
							
							cell = row.createCell((cellNo++));
							cell.setCellValue("Discount Code Name");
						}
						if (indexes[1] == 0) {
							
							cell = row.createCell(cellNo++);
							cell.setCellValue("Status");
						}
						if (indexes[2] == 0) {
							
							cell = row.createCell(cellNo++);
							cell.setCellValue("Last Modified Date");
						}
						if (indexes[3] == 0) {
							
							cell = row.createCell(cellNo++);
							cell.setCellValue("Validity");
						}
						if (indexes[4] == 0) {
							
							cell = row.createCell(cellNo++);
							cell.setCellValue("Discount");
							
						}
						if (indexes[5] == 0) {
							
							cell = row.createCell(cellNo++);
							cell.setCellValue("Codes Generated");
						}
						if (indexes[6] == 0) {
							
							cell = row.createCell(cellNo++);
							cell.setCellValue("Issued");
						}
	                     if (indexes[7] == 0) {
							
							cell = row.createCell(cellNo++);
							cell.setCellValue("Redeemed");
						}
	                     if (indexes[8] == 0) {
	 						
	 						cell = row.createCell(cellNo++);
	 						cell.setCellValue("Available");
	 					}
	                     if (indexes[9] == 0) {
	 						
	 						cell = row.createCell(cellNo++);
	 						cell.setCellValue("Revenue");
	 					}
	                     if (indexes[10] == 0) {
		 						
		 						cell = row.createCell(cellNo++);
		 						cell.setCellValue("Total Points Redeemed");
		 					}
						List<Coupons> couponsList = null;
						int size = count;
						for (int i = 0; i < count; i += size) {
							couponsList = couponsDao.findCouponsByOrgId(users.getUserOrganization().getUserOrgId(),0,count, status, fromDate,endDate,orderby_colName,desc_Asc,promotionName);
							  
							if (couponsList != null && couponsList.size() > 0){
								int rowId = 1;
								for (Coupons coupon: couponsList) {
									String validity = null;
									if(coupon.getExpiryType().equals(Constants.COUP_VALIDITY_PERIOD_STATIC)){
										
										validity =MyCalendar.calendarToString(coupon.getCouponCreatedDate(), MyCalendar.FORMAT_DATEONLY_GENERAL)+" - "+MyCalendar.calendarToString(coupon.getCouponExpiryDate(),MyCalendar.FORMAT_DATEONLY_GENERAL);
									}
									else if(coupon.getExpiryType().equals(Constants.COUP_VALIDITY_PERIOD_DYNAMIC)){
										validity = "Dynamic";
									}

									String discount =  "";
									if(coupon.getDiscountType().equals("Percentage")) {
										discount = "% on ";
									}else {
										discount = "Curr. on ";
										//discount=userCurrencySymbol+" on ";
									}
									//discount= discount+" "+coupObj.getDiscountCriteria();
									if(coupon.getDiscountCriteria().trim().contains("SKU")) {
										discount += " Product";
									}else {
										discount += " Receipt";
									}
										
									String generateCode=""; 
									String availableStr="";
									if(coupon.getAutoIncrCheck()){
										generateCode="Unlimited";
										availableStr="Unlimited";
									}else{
										generateCode=""+coupon.getTotalQty();
										availableStr=""+(coupon.getAvailable()!= null ? coupon.getAvailable():"");
									}
									//last modified date
									String lastModifiedDate=null;
									lastModifiedDate=MyCalendar.calendarToString(coupon.getUserLastModifiedDate(), MyCalendar.FORMAT_DATEONLY_GENERAL,clientTimeZone );
									
									DecimalFormat decimalFormat = new DecimalFormat("#0.00");
									row = sheet.createRow(rowId++);
									int columnId = 0;
									cell = null;
									if (indexes[0] == 0) {
										cell = row.createCell(columnId++);
										cell.setCellValue(coupon.getCouponName().trim());
									}
									if (indexes[1] == 0) {
										cell = row.createCell(columnId++);
										cell.setCellValue(coupon.getStatus());
									}
									if (indexes[2] == 0) {
										cell = row.createCell(columnId++);
										cell.setCellValue(lastModifiedDate);
									}
									if (indexes[3] == 0) {
										cell = row.createCell(columnId++);
										cell.setCellValue(validity);
									}
									if (indexes[4] == 0) {
										cell = row.createCell(columnId++);
										cell.setCellValue(discount);
										
									}
									if (indexes[5] == 0) {
										cell = row.createCell(columnId++);
										cell.setCellValue(generateCode);
									}
									if (indexes[6] == 0) {
										cell = row.createCell(columnId++);
										cell.setCellValue(coupon.getIssued() != null? ""+coupon.getIssued():"");
									}
									if (indexes[7] == 0) {
										cell = row.createCell(columnId++);
										cell.setCellValue(coupon.getRedeemed() != null?  ""+coupon.getRedeemed():"" );
									}
									if (indexes[8] == 0) {
										cell = row.createCell(columnId++);
										cell.setCellValue(availableStr);
									}
									if (indexes[9] == 0) {
										cell = row.createCell(columnId++);
										cell.setCellValue(coupon.getTotRevenue() != null? ""+decimalFormat.format(coupon.getTotRevenue()):"" );
									}
									if (indexes[10] == 0) {
										cell = row.createCell(columnId++);
										cell.setCellValue(coupon.getUsedLoyaltyPoints() != null? ""+coupon.getUsedLoyaltyPoints().intValue():"0" );
									}
									
									
								  }
						     	}	
						     }
							
						     hwb.write(fileOut);
						    fileOut.flush();
							fileOut.close();
					
						Filedownload.save(file, "application/vnd.ms-excel");
						logger.debug("exited");

					} catch (Exception e) {
						logger.error("** Exception : ", e);
					}

				}
			 
			 private void exportCsvZIP(int[] indexes) {
				 	logger.debug("-- just entered into exportCsvZIP --");
					StringBuffer sb = null;
					String userName = GetUser.getUserName();
					String usersParentDirectory = (String)PropertyUtil.getPropertyValue("usersParentDirectory");
					String exportDir = usersParentDirectory + "/" + userName + "/Export/" ;
					File downloadDir = new File(exportDir);
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
					
						
						String filePath = exportDir +  "DiscountCode_Reports_" + System.currentTimeMillis() + ".csv";
							MyCalendar.calendarToString(Calendar.getInstance(), MyCalendar.FORMAT_YEARTOSEC, clientTimeZone);
							try {
				
									logger.debug("Download File path : " + filePath);
									File file = new File(filePath);
								try	(BufferedWriter bw = new BufferedWriter(new FileWriter(filePath));)
								{
									String status = statusLbId.getSelectedItem().getLabel();
									String promotionName = searchByPromoCodeNameTbId.getValue().trim();
									int count = couponsDao.findCoupBasedOnDates(users.getUserOrganization().getUserOrgId(),status,fromDate, endDate,promotionName);
									if(count == 0) {
										Messagebox.show("No discount code report found.","Info", Messagebox.OK,Messagebox.INFORMATION);
										return;
									}
									 String udfFldsLabel= "";
							
									 if(indexes[0]==0) {
										 udfFldsLabel = "\""+"Discount Code Name"+"\""+",";
									 }
									 if(indexes[1]==0) {
										 udfFldsLabel += "\""+"Status"+"\""+",";
									 }
									 if(indexes[2]==0) {
										 udfFldsLabel += "\""+"Last Modified Date"+"\""+",";
									 }
									 if(indexes[3]==0) {
										 udfFldsLabel += "\""+"Validity"+"\""+",";
									 }
									 if(indexes[4]==0) {
										 udfFldsLabel += "\""+"Discount"+"\""+",";
									 }	
									 if(indexes[5]==0) {
										 udfFldsLabel += "\""+"Codes Generated"+"\""+",";
									 }	
									 if(indexes[6]==0) {

										 udfFldsLabel += "\""+"Issued"+"\""+",";
									 }
									 if(indexes[7]==0) {

										 udfFldsLabel += "\""+"Redeemed"+"\""+",";
									 }
									 if(indexes[8]==0) {

										 udfFldsLabel += "\""+"Available"+"\""+",";
									 }
									 if(indexes[9]==0) {

										 udfFldsLabel += "\""+"Revenue"+"\""+",";
									 }
									 if(indexes[10]==0) {

										 udfFldsLabel += "\""+"Total Points Redeemed"+"\""+",";
									 }
									 sb = new StringBuffer();
									 sb.append(udfFldsLabel);
									 sb.append("\r\n");

									 bw.write(sb.toString());
									 //System.gc();
									
									int size = count;
									List<Coupons> couponsList = null;
									for (int i = 0; i < count; i+=size) {
										sb = new StringBuffer();
										//pageSize = Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel());
										couponsList = couponsDao.findCouponsByOrgId(users.getUserOrganization().getUserOrgId(),0,count, status, fromDate,endDate,orderby_colName,desc_Asc,promotionName);
									          if(couponsList!=null && couponsList.size()>0)
											for (Coupons coupon : couponsList) {
												String validity = null;
												if(coupon.getExpiryType().equals(Constants.COUP_VALIDITY_PERIOD_STATIC)){
													
													validity =MyCalendar.calendarToString(coupon.getCouponCreatedDate(), MyCalendar.FORMAT_DATEONLY_GENERAL)+" - "+MyCalendar.calendarToString(coupon.getCouponExpiryDate(),MyCalendar.FORMAT_DATEONLY_GENERAL);
												//	validity =MyCalendar.calendarToString(coupon.getCouponCreatedDate(), MyCalendar.FORMAT_DATEONLY_GENERAL, clientTimeZone)+" - "+MyCalendar.calendarToString(coupon.getCouponExpiryDate(),MyCalendar.FORMAT_DATEONLY_GENERAL ,clientTimeZone);
												}
												else if(coupon.getExpiryType().equals(Constants.COUP_VALIDITY_PERIOD_DYNAMIC)){
													validity = "Dynamic";
												}

												String discount =  "";
												if(coupon.getDiscountType().equals("Percentage")) {
													discount = "% on ";
												}else {
													discount = "Curr. on ";
												}
												//discount= discount+" "+coupObj.getDiscountCriteria();
												if(coupon.getDiscountCriteria().trim().contains("SKU")) {
													discount += " Product";
												}else {
													discount += " Receipt";
												}
													
												String generateCode=""; 
												String availableStr="";
												if(coupon.getAutoIncrCheck()){
													generateCode="Unlimited";
													availableStr="Unlimited";
												}else{
													generateCode=""+coupon.getTotalQty();
													availableStr=""+(coupon.getAvailable()!= null ? coupon.getAvailable():"");
												}
												//last modified date
												String lastModifiedDate=null;
												lastModifiedDate=MyCalendar.calendarToString(coupon.getUserLastModifiedDate(), MyCalendar.FORMAT_DATEONLY_GENERAL,clientTimeZone );
												
												DecimalFormat decimalFormat = new DecimalFormat("#0.00");
												if(indexes[0]==0) {
													sb.append("\"");sb.append(coupon.getCouponName().trim()); sb.append("\",");
												}
												if(indexes[1]==0) {
													sb.append("\"");sb.append(coupon.getStatus()); sb.append("\",");
												}
												if(indexes[2]==0) {
													sb.append("\"");sb.append(lastModifiedDate); sb.append("\",");
												}
												if(indexes[3]==0) {
													sb.append("\"");sb.append(validity); sb.append("\",");
												}
												if(indexes[4]==0) {
													sb.append("\"");sb.append(discount); sb.append("\",");
												}
												if(indexes[5]==0) {
													sb.append("\"");sb.append(generateCode); sb.append("\",");
												}
												if(indexes[6]==0) {
												sb.append("\"");sb.append(coupon.getIssued() != null? ""+coupon.getIssued():""); sb.append("\",");
												}
												if(indexes[7]==0) {
												sb.append("\"");sb.append(coupon.getRedeemed() != null?  ""+coupon.getRedeemed():"" ); sb.append("\",");
												}
												if(indexes[8]==0) {
												sb.append("\"");sb.append(availableStr); sb.append("\",");
												}
												if(indexes[9]==0) {
													sb.append("\"");sb.append(coupon.getTotRevenue() != null? ""+decimalFormat.format(coupon.getTotRevenue()):"" ); sb.append("\",");
												}
												if(indexes[10]==0) {
													sb.append("\"");sb.append(coupon.getUsedLoyaltyPoints()!= null? ""+coupon.getUsedLoyaltyPoints().intValue():"0" ); sb.append("\",");
												}
												sb.append("\r\n");
											}
									
							
										bw.write(sb.toString());
										couponsList = null;
										sb = null;
										//System.gc();
									}
								}
								
									String zipFilePath = filePath+".zip";
									//String zipFilePath = filePath.replace(".csv", ".zip");
									File zipFile = new File(zipFilePath);
									logger.info("Zip File:"+zipFile);
									try (FileOutputStream fos = new FileOutputStream(zipFilePath);
											ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(fos))) {
										zos.putNextEntry(new ZipEntry(file.getName()));
						
										byte[] bytes = Files.readAllBytes(Paths.get(filePath));
										zos.write(bytes, 0, bytes.length);
										zos.closeEntry();
									}
						            
						            Filedownload.save(zipFile, "application/zip");
									
								} catch (IOException e) {
									logger.error("Exception ::",e);
									
								}
								logger.debug("-- exit --");
				
				}
			 
			 public void exportExcelZip(int[] indexes) {
				 logger.debug("-- just entered into exportExcelZip --");
					try {
						String userName = GetUser.getUserName();
						String usersParentDirectory = (String) PropertyUtil.getPropertyValue("usersParentDirectory");

						File downloadDir = new File(usersParentDirectory + "/" + userName + "/List/download/");

						if (!downloadDir.exists()) {
							downloadDir.mkdirs();
						}

						String filePath = usersParentDirectory + "/" + userName + "/List/download/DiscountCode_Reports_"
								+ System.currentTimeMillis() + ".xls";
						File file = new File(filePath);
						logger.debug("Writing to the file : " + filePath);
						try (FileOutputStream fileOut = new FileOutputStream(filePath);)
						{
						HSSFWorkbook hwb = new HSSFWorkbook();
						HSSFSheet sheet = hwb.createSheet("Promotion Report");
						HSSFRow row = sheet.createRow((short) 0);
						HSSFCell cell = null;
						String status = statusLbId.getSelectedItem().getLabel();
						String promotionName = searchByPromoCodeNameTbId.getValue().trim();
						int count = couponsDao.findCoupBasedOnDates(users.getUserOrganization().getUserOrgId(),status,fromDate, endDate,promotionName);
					
						if (count == 0) {
							Messagebox.show("No discount code report found.", "Info", Messagebox.OK, Messagebox.INFORMATION);
							return;
					      }
						
						row = sheet.createRow(0);
						int cellNo = 0;
						if (indexes[0] == 0) {
							
							cell = row.createCell((cellNo++));
							cell.setCellValue("Discount Code Name");
						}
						if (indexes[1] == 0) {
							
							cell = row.createCell(cellNo++);
							cell.setCellValue("Status");
						}
						if (indexes[2] == 0) {
							
							cell = row.createCell(cellNo++);
							cell.setCellValue("Last Modified Date");
						}
						if (indexes[3] == 0) {
							
							cell = row.createCell(cellNo++);
							cell.setCellValue("Validity");
						}
						if (indexes[4] == 0) {
							
							cell = row.createCell(cellNo++);
							cell.setCellValue("Discount");
							
						}
						if (indexes[5] == 0) {
							
							cell = row.createCell(cellNo++);
							cell.setCellValue("Codes Generated");
						}
						if (indexes[6] == 0) {
							
							cell = row.createCell(cellNo++);
							cell.setCellValue("Issued");
						}
	                     if (indexes[7] == 0) {
							
							cell = row.createCell(cellNo++);
							cell.setCellValue("Redeemed");
						}
	                     if (indexes[8] == 0) {
	 						
	 						cell = row.createCell(cellNo++);
	 						cell.setCellValue("Available");
	 					}
	                     if (indexes[9] == 0) {
	 						
	 						cell = row.createCell(cellNo++);
	 						cell.setCellValue("Revenue");
	 					}
	                     if (indexes[10] == 0) {
		 						
		 						cell = row.createCell(cellNo++);
		 						cell.setCellValue("Total Points Redeemed");
		 					}
						List<Coupons> couponsList = null;
						int size = count;
						for (int i = 0; i < count; i += size) {
							couponsList = couponsDao.findCouponsByOrgId(users.getUserOrganization().getUserOrgId(),0,count, status, fromDate,endDate,orderby_colName,desc_Asc,promotionName);
							  
							if (couponsList != null && couponsList.size() > 0){
								int rowId = 1;
								for (Coupons coupon: couponsList) {
									String validity = null;
									if(coupon.getExpiryType().equals(Constants.COUP_VALIDITY_PERIOD_STATIC)){
										
										validity =MyCalendar.calendarToString(coupon.getCouponCreatedDate(), MyCalendar.FORMAT_DATEONLY_GENERAL)+" - "+MyCalendar.calendarToString(coupon.getCouponExpiryDate(),MyCalendar.FORMAT_DATEONLY_GENERAL);
									}
									else if(coupon.getExpiryType().equals(Constants.COUP_VALIDITY_PERIOD_DYNAMIC)){
										validity = "Dynamic";
									}

									String discount =  "";
									if(coupon.getDiscountType().equals("Percentage")) {
										discount = "% on ";
									}else {
										discount = "Curr. on ";
										//discount=userCurrencySymbol+" on ";
									}
									//discount= discount+" "+coupObj.getDiscountCriteria();
									if(coupon.getDiscountCriteria().trim().contains("SKU")) {
										discount += " Product";
									}else {
										discount += " Receipt";
									}
										
									String generateCode=""; 
									String availableStr="";
									if(coupon.getAutoIncrCheck()){
										generateCode="Unlimited";
										availableStr="Unlimited";
									}else{
										generateCode=""+coupon.getTotalQty();
										availableStr=""+(coupon.getAvailable()!= null ? coupon.getAvailable():"");
									}
									//last modified date
									String lastModifiedDate=null;
									lastModifiedDate=MyCalendar.calendarToString(coupon.getUserLastModifiedDate(), MyCalendar.FORMAT_DATEONLY_GENERAL,clientTimeZone );
									
									DecimalFormat decimalFormat = new DecimalFormat("#0.00");
									row = sheet.createRow(rowId++);
									int columnId = 0;
									cell = null;
									if (indexes[0] == 0) {
										cell = row.createCell(columnId++);
										cell.setCellValue(coupon.getCouponName().trim());
									}
									if (indexes[1] == 0) {
										cell = row.createCell(columnId++);
										cell.setCellValue(coupon.getStatus());
									}
									if (indexes[2] == 0) {
										cell = row.createCell(columnId++);
										cell.setCellValue(lastModifiedDate);
									}
									if (indexes[3] == 0) {
										cell = row.createCell(columnId++);
										cell.setCellValue(validity);
									}
									if (indexes[4] == 0) {
										cell = row.createCell(columnId++);
										cell.setCellValue(discount);
										
									}
									if (indexes[5] == 0) {
										cell = row.createCell(columnId++);
										cell.setCellValue(generateCode);
									}
									if (indexes[6] == 0) {
										cell = row.createCell(columnId++);
										cell.setCellValue(coupon.getIssued() != null? ""+coupon.getIssued():"");
									}
									if (indexes[7] == 0) {
										cell = row.createCell(columnId++);
										cell.setCellValue(coupon.getRedeemed() != null?  ""+coupon.getRedeemed():"" );
									}
									if (indexes[8] == 0) {
										cell = row.createCell(columnId++);
										cell.setCellValue(availableStr);
									}
									if (indexes[9] == 0) {
										cell = row.createCell(columnId++);
										cell.setCellValue(coupon.getTotRevenue() != null? ""+decimalFormat.format(coupon.getTotRevenue()):"" );
									}
									if (indexes[10] == 0) {
										cell = row.createCell(columnId++);
										cell.setCellValue(coupon.getUsedLoyaltyPoints() != null? ""+coupon.getUsedLoyaltyPoints().intValue():"0" );
									}
									
									
								  }
						     	}	
						     }							
						     hwb.write(fileOut);
						    fileOut.flush();
						  }// try close
							
							String zipFilePath = filePath+".zip";
							//String zipFilePath = filePath.replace(".xls", ".zip");
							File zipFile = new File(zipFilePath);
							try (FileOutputStream fos = new FileOutputStream(zipFilePath);
									ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(fos))) {
								zos.putNextEntry(new ZipEntry(file.getName()));
				
								byte[] bytes = Files.readAllBytes(Paths.get(filePath));
								zos.write(bytes, 0, bytes.length);
								zos.closeEntry();
							}
					
						Filedownload.save(zipFile, "application/zip");
						logger.debug("exited");

					} catch (Exception e) {
						logger.error("** Exception : ", e);
					}

				}

			 

}
