 package org.mq.marketer.campaign.controller.admin;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.CouponDiscountGeneration;
import org.mq.marketer.campaign.beans.Coupons;
import org.mq.marketer.campaign.beans.MailingList;
import org.mq.marketer.campaign.beans.OrganizationStores;
import org.mq.marketer.campaign.beans.POSMapping;
import org.mq.marketer.campaign.beans.SKUTemp;
import org.mq.marketer.campaign.beans.SkuFile;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.custom.MyDatebox;
import org.mq.marketer.campaign.dao.CouponCodesDao;
import org.mq.marketer.campaign.dao.CouponDiscountGenerateDao;
import org.mq.marketer.campaign.dao.CouponDiscountGenerateDaoForDML;
import org.mq.marketer.campaign.dao.CouponsDao;
import org.mq.marketer.campaign.dao.CouponsDaoForDML;
import org.mq.marketer.campaign.dao.MailingListDao;
import org.mq.marketer.campaign.dao.OrganizationStoresDao;
import org.mq.marketer.campaign.dao.POSMappingDao;
import org.mq.marketer.campaign.dao.SkuFileDao;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.CouponCodesUtility;
import org.mq.marketer.campaign.general.LBFilterEventListener;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.MyModel;
import org.mq.marketer.campaign.general.MyModelForCategory;
import org.mq.marketer.campaign.general.PageListEnum;
import org.mq.marketer.campaign.general.PageUtil;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Redirect;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.data.dao.JdbcResultsetHandler;
import org.mq.optculture.data.dao.SpecialRewardsDaoForDML;
import org.mq.optculture.data.dao.ValueCodesDaoForDML;
import org.mq.optculture.utils.OCCSVWriter;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.metainfo.ComponentInfo;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Column;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Doublebox;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Image;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.RowRenderer;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.event.PagingEvent;

public class ViewCouponsController extends GenericForwardComposer implements EventListener, ListitemRenderer<SkuFile>, RowRenderer<Coupons> {
	
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	Long orgId;
	Paging couponsPagingId; 
	
	String filterQuery=null;
	String filterCountQuery=null;
	String qryPrefix=null;
	LBFilterEventListener contactLbELObj =null;	
	
	private Grid contactsGridId;
	
	private Listbox memberPerPageLBId,bctypeListboxId;
	private Rows couponRowsId;
	int totalCount = 0;
	private Users users = GetUser.getUserObj();
	private SkuFileDao skuFileDao;
	private CouponsDao couponsDao;
	private CouponsDaoForDML couponsDaoForDML; 
	private CouponDiscountGenerateDao coupDiscGenDao;
	private CouponDiscountGenerateDaoForDML coupDiscGenDaoForDML;
	private CouponCodesDao couponCodesDao;
	private OrganizationStoresDao organizationStoresDao;
	private static String COUPON_EDIT = "COUPON_EDIT";
	private static String COUPON_DELETE = "COUPON_DELETE";
	private static String COUPON_REPORTS = "COUPON_REPORTS";
	private static String COUPON_STATUS = "COUPON_STATUS";
	private static String COUPON_EXPORT = "COUPON_EXPORT";
	private static String COUPON_DISCOUNT_DELETE = "COUPON_DISCOUNT_DELETE";
	private Tabbox manageCouponsTabBoxId;
	
	private Textbox couponNameTxtBxId,coupDescTxtBxId,dynamicValidityTbId;
	private Radiogroup coupGenRadioGrId;
	private Listbox viewSKULbId,dynamicValidityLbId;
	private Datebox createDateBxId,expiryDateBxId;
//	private Textbox dateBoxId;
	private Textbox singCoupTxtBxId; 
	private Checkbox singCoupChkBxId,multiCoupChkBxId,redeemdsingCoupChkBxId,bcenableCoupChkBxId,unlimitSubChkBxId;
	private Longbox multiCodLimitLongBxId,singlelimitLongbxId,redeemdlimitLongbxId,perSubUselimitLongbxId;//,bcwidthLongbxId,bcheightLongbxId;
	private  static String ERROR_STYLE = "border:1px solid #DD7870;";
	private  static String NORMAL_STYLE = "border:1px solid #7F9DB9;";
	private  static String DISC_TYPE_PERCENTAGE = "Percentage";
	private  static String DISC_TYPE_VALUE = "Value";
	private Div singleSelCoupDivId,multiSelCoupDivId,bcsizeCoupDivId, filtersDivId,promoValidityPeriodDivIdDynamic,productDetailtaDivId;
	private Button genCoupBtnId, selectAllSKUBtnId, deSelectAllSKUBtnId;
	private Radio singRadioId,multRadioId,percentRadioId,dollerRadioId,dynamicRadio,staticRadio;
	private Session sessionScope = null;	
	private Paging skuPaging;
	TimeZone clientTimeZone ;
	MailingListDao mailingListDao = null;
	Coupons tempCouponObj = null;
	String query =null;
	String countQuery=null; 
	private static String SELECT_SKU = "SKU";
	private Rows discountGenRowsId;
	private Div skuVisibleDivId,skuDivId;
	private Div mpvDivId,storeSelectDivId;
	Label totAmtDollerLblId,mintAmtTxtLblId;
	
	private Checkbox  onlylpChkBxId,allStoresChkBxId,multiplePerReceiptChkBxId,skuCkId;
//	private Checkbox lpChkBxId ;
	private Intbox lpIBxId;
	List<CouponDiscountGeneration>  editCoupDiscGenLst = null;
	//Groupbox skuFilterGBxId;
	Radiogroup discountRadioGrId;
	Radio skuRadioId, tpaRadioId;
	private Radiogroup promoRgId; 
	private Checkbox vendorCodeCkId,deptCodeCkId,itemCategoryCkId,dcsCkId,classCkId,subclassCkId; 
	private Listbox  itemCategoryFilterLBId, vendorCodeFilterLBId, deptCodeFilterLBId,bcDimensionListboxId2,
																classFilterLBId,subClassFilterLBId,dcsFilterLBId,addedSKULBId;
	
//	private Listbox storeFilterLBId;
//	private Listbox bcDimensionListboxId, bcDimensionListboxId1;
	LBFilterEventListener viewSkuELObj,viewCouponsELObj,vendorCodeFilterObj;
	
	private static String[] DM_Dimension = {"32X8","26X12","36X12","36X16","48X16","16X16","18X18","20X20","22X22","24X24","26X26","32X32","36X36","40X40","44X44","48X48","52X52","64X64","72X72","80X80","88X88","96X96","104X104","120X120","132X132","144X144"};
	private static String[] AZ_Dimension = {"15X15","19X19","23X23","27X27","31X31","37X37","41X41","45X45","49X49","53X53","57X57","61X61","67X67","71X71","75X75","79X79","83X83","87X87","91X91","95X95","101X101","105X105","109X109","113X113","117X117","121X121","125X125","131X131","135X135","139X139","143X143","147X147","151X151"};
	private static String[] QR_Dimension = {"40X40","80X80","100X100","120X120","150X150","200X200"};
	private static String[] LN_Dimension = {"60X15","60X20","75X25","80X20","90X30","100X25","120X30","120X40","150X50","160X40","200X50"};
	//Map<String, Byte> barCodeMAp
	
	private final String SEARCH_BY_NAME = "Name";
	private final String SEARCH_BY_STATUS = "Status";
	private final String SEARCH_BY_DATE = "Date";
	private Listbox srchLbId;
	private Listbox codeStatusLb;
	private Textbox searchByPromoCodeNameTbId,vendorCodeTBId,deptCodeTBId,itemCategoryTBId,dcsTBId,classTBId,subClassTBId,viewSkuTBId;
	private MyDatebox fromDateboxId;
	private MyDatebox toDateboxId;
	private Div searchByPromoCodeNameDivId;
	private Div searchByCreatedOnDivId;
	private Div searchByStatusDivId;
	//Hbox LbDivId;
	private String fromDateStr;
	private String toDateStr;
	private Column column1Id,column2Id,column3Id,column4Id;
	Doublebox discountDblBxId;
	 Longbox totPurAmtLngBxId;
	 POSMappingDao posMappingDao = null;
	private Image showFilterImgId, hideFilterImgId;
	private  String  userCurrencySymbol = "$ "; 
	
	private Doublebox screenSizeTBId;
	
	public ViewCouponsController(){
		this.sessionScope = Sessions.getCurrent();
	}
	
	private static Map<String, String> genFieldSKUMap = new HashMap<String, String>();
	static{
	genFieldSKUMap.put("Item Category", "itemCategory::itemCategoryCkId::itemCategoryFilterLBId");
	genFieldSKUMap.put("Vendor", "vendorCode::vendorCodeCkId::vendorCodeFilterLBId");
	genFieldSKUMap.put("Department", "departmentCode::deptCodeCkId::deptCodeFilterLBId");
	genFieldSKUMap.put("Class", "classCode::classCkId::classFilterLBId");
	genFieldSKUMap.put("Subclass", "subClassCode::subclassCkId::subClassFilterLBId");
	genFieldSKUMap.put("DCS", "DCS::dcsCkId::dcsFilterLBId");
	}
	Map<String,Checkbox> checkMap=new LinkedHashMap<>();
	Map<String,Listbox> listMap=new LinkedHashMap<>();
	Map<String,String> headerMap=new HashMap<>();
	MailingList posMl=null;
	long orgOwnerUserId=0;
	@Override
	public void doAfterCompose(Component comp) throws Exception {


		super.doAfterCompose(comp);
//		Clients.evalJavaScript("setdate()");
		logger.info("do after compose");
		Clients.evalJavaScript("setScreenSize()");
		checkMap.put("vendorCodeCkId", vendorCodeCkId);
		checkMap.put("deptCodeCkId", deptCodeCkId);
		checkMap.put("itemCategoryCkId", itemCategoryCkId);
		checkMap.put("dcsCkId", dcsCkId);
		checkMap.put("classCkId", classCkId);
		checkMap.put("subclassCkId", subclassCkId);
		
		
		listMap.put("vendorCodeFilterLBId", vendorCodeFilterLBId);
		listMap.put("deptCodeFilterLBId", deptCodeFilterLBId);
		listMap.put("itemCategoryFilterLBId", itemCategoryFilterLBId);
		listMap.put("dcsFilterLBId", dcsFilterLBId);
		listMap.put("classFilterLBId", classFilterLBId);
		listMap.put("subClassFilterLBId", subClassFilterLBId);
		
		
		headerMap.put("1", "Vendor");
		headerMap.put("2", "Department");
		headerMap.put("3", "Item Category");
		headerMap.put("4", "DCS");
		headerMap.put("5", "Class");
		headerMap.put("6", "Subclass");
		headerMap.put("7", "SKU");
		
		
		String style = "font-weight:bold;font-size:15px;color:#313031;" +
				"font-family:Arial,Helvetica,sans-serif;align:left";
		PageUtil.setHeader("Discount Codes","",style,true);
		
		clientTimeZone =(TimeZone)Sessions.getCurrent().getAttribute("clientTimeZone");
		
		HttpServletRequest request = (HttpServletRequest) Executions.getCurrent().getNativeRequest();
		
		Long timeInLong = request.getSession().getCreationTime();
		logger.info("time In long value is  :: "+timeInLong);
		Calendar cal1 = Calendar.getInstance();
		logger.info(" 1st cal1 get Time is  :: "+cal1.getTime());
		cal1.setTimeInMillis(timeInLong);
		logger.info(" 2nd  cal1 get Time is  :: "+cal1.getTime());
		
		
		couponsDao = (CouponsDao)SpringUtil.getBean("couponsDao");
		couponsDaoForDML = (CouponsDaoForDML)SpringUtil.getBean("couponsDaoForDML");
		coupDiscGenDao = (CouponDiscountGenerateDao)SpringUtil.getBean("coupDiscGenDao");
		coupDiscGenDaoForDML = (CouponDiscountGenerateDaoForDML)SpringUtil.getBean("coupDiscGenDaoForDML");
		posMappingDao= (POSMappingDao)SpringUtil.getBean("posMappingDao");
		orgId = users.getUserOrganization().getUserOrgId(); 
		if(orgId == null) {
			logger.debug("no organization exists for this User..");
			return;
		}
		getAllPromoFromDB();
		contactsGridId.setAttribute("defaultOrderBy", "userCreatedDate");
		registerEventListner(0, 5);
		Utility.refreshGridModel(viewCouponsELObj, 0, true);
		 mailingListDao = (MailingListDao)SpringUtil.getBean("mailingListDao");
//		 posList = mailingListDao.findPOSMailingList(users);
		 skuFileDao = (SkuFileDao)SpringUtil.getBean("skuFileDao");
		
			//setDefaultDimensions();
			
		 	contactsGridId.setRowRenderer(this);
			//set Default  user stores
			
			//setDeafultStore();
			
			//coupDiscGenDaoForDML.deleteAllSkuBy(users.getUserId());
			//selectAllSKUBtnId.setAttribute("selectedAll", false);
			//skuCkId.setChecked(true);
			posMl = mailingListDao.findPOSMailingList(users);
			if(posMl == null)
			posMl = mailingListDao.findUserBCRMList(users);
			//selectAllSKUBtnId.setAttribute("selectedAll", false);
			UsersDao usersDao = (UsersDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
			orgOwnerUserId = usersDao.getOwnerofOrg(users.getUserOrganization().getUserOrgId());
			
			// adding currency symbol for each country
			 String currSymbol = Utility.countryCurrencyMap.get(users.getCountryType());
			   if(currSymbol != null && !currSymbol.isEmpty()) userCurrencySymbol = currSymbol + " ";
			 //totAmtDollerLblId.setValue(" " + userCurrencySymbol + " ");
			 //dollerRadioId.setLabel(" " + userCurrencySymbol + " ");
			
	} // doAfterCompose
	 
	
	public void onCheck$skuCkId(){
		if(skuCkId.isChecked()) {
		viewSkuTBId.setValue("");
		viewSKULbId.setVisible(true);
		skuDivId.setVisible(true);
		Long totalSize=(Long) skuFileDao.executeQuery("SELECT COUNT(distinct sku) FROM SkuFile  WHERE userId = "+orgOwnerUserId+" and sku is not null").get(0);
		skuPaging.setTotalSize(totalSize.intValue());
		skuPaging.setPageSize(10);
		skuPaging.setActivePage(0);
		skuPaging.setAttribute(LBFilterEventListener.HANDLE_ON_PAGING, "false");
	    List<Object> result=skuFileDao.getAllSkuBy(orgOwnerUserId, 0, skuPaging.getPageSize());
	    int cnt=viewSKULbId.getItemCount();
		for(; cnt>0;cnt--){
			viewSKULbId.removeItemAt(cnt-1);
		}
	    if(result==null)return;
		Listitem item=null; 
		for(Object data:result) {
			item=new Listitem();
			Object[] obj=(Object[]) data;
			item.setValue(obj);
			new Listcell().setParent(item);
			new Listcell(obj[0].toString()).setParent(item);
			new Listcell(obj[1]!=null?obj[1].toString():"").setParent(item);
			new Listcell(obj[2]!=null?obj[2].toString():"").setParent(item);
			item.setParent(viewSKULbId);
		}
		}
		else {
			viewSKULbId.setVisible(false);
			skuDivId.setVisible(false);
		}
		//LBFilterEventListener.lbFilterSetup(viewSKULbId);
		/*if(skuCkId.isChecked()) {
			viewSKULbId.setVisible(true);
			if(manageCouponsTabBoxId.getSelectedIndex()==1 && viewSKULbId.getItemCount()==0 ){
				logger.info("inside Sku list prepration ");
				prepareSKU();
			}
		}
		else {
			viewSKULbId.setVisible(false);
			viewSKULbId.setSelectedIndex(-1);
		}*/
	}
	private int totalSize=800;
	public void onChange$screenSizeTBId(){
		Double totalWidth=screenSizeTBId.getValue();
		totalSize =totalWidth.intValue();
		logger.info(" ON Change ==========="+totalWidth);
		screenSizeTBId.setVisible(false);
	}
	boolean dataflag=true;
	public void prepraAllAttributeData(){
		dataflag=false;
		List<POSMapping> posList=	posMappingDao.findOnlyByGenType(Constants.POS_MAPPING_TYPE_SKU,users.getUserId());
		String coulmn=null;
		String chId=null;
		String lbId=null;
		if(posList!=null && posList.size()>0){
			for(POSMapping posMapping :posList){
				if(genFieldSKUMap.containsKey(posMapping.getCustomFieldName())){
					String value=genFieldSKUMap.get(posMapping.getCustomFieldName());
					String arr[] =value.split("::");
					coulmn=arr[0];
					 chId=arr[1];
					lbId=arr[2];
						displayAllSKUFileData(coulmn,listMap.get(lbId),checkMap.get(chId));
				}
			}
		}	
	}
	public void prepareSKU(){
		MyModel model = new MyModel(50);
		model.setMultiple(true);
		viewSKULbId.setModel(model);
		viewSKULbId.setMultiple(true);
	    viewSKULbId.setCheckmark(true);
		viewSKULbId.setItemRenderer(new ListitemRenderer() {
			@Override
			public void render(Listitem item, Object data,int arg) throws Exception {
				item.setCheckable(true);
				if(data instanceof Object[]){
					Object[] obj=(Object[]) data;
					item.setValue(obj);
					new Listcell().setParent(item);
					new Listcell(obj[0].toString()).setParent(item);
					new Listcell(obj[1]!=null?obj[1].toString():"").setParent(item);
					new Listcell(obj[2]!=null?obj[2].toString():"").setParent(item);
				}
			}
		});
		LBFilterEventListener.lbFilterSetup(viewSKULbId);
	}
	
	 

	
	public void adjustListBoxes(){
		int children=0;
		for(Entry<String, Checkbox> entry : checkMap.entrySet()){
			Checkbox ck=entry.getValue();
			if(ck.isChecked())
				children++;
		}
		int eachListsWidth = totalSize/children;
		logger.info("Set Width ::"+eachListsWidth);
		for(Entry<String, Listbox> entry : listMap.entrySet()){
			Listbox list=entry.getValue();
			list.setWidth(eachListsWidth+"px");
			if(list.getItemCount()==0)
			 list.setMultiple(false);
			 
		}
	}
	public void displayAllSKUFileData( String column,Listbox ldId,Checkbox ckId){
		int cnt=ldId.getItemCount();
		for(; cnt>0;cnt--){
			ldId.removeItemAt(cnt-1);
		}
			ckId.setVisible(true);
			ckId.setChecked(true);
			ldId.setVisible(true);
			ckId.addEventListener("onCheck", this);
			ckId.setAttribute("listId", ldId);
			MyModelForCategory model=new MyModelForCategory(50,column,orgOwnerUserId);
			model.setMultiple(true);
			ldId.setModel(model);
			ldId.setMultiple(true);
			ldId.setCheckmark(true);
			ldId.setItemRenderer(new ListitemRenderer() {
			@Override
			public void render(Listitem item, Object data,int arg) throws Exception {
				item.setCheckable(true);
				if(data instanceof Object[]){
					Object[] obj=(Object[]) data;
					item.setValue(data);
					new Listcell().setParent(item);
					new Listcell(obj[0].toString()).setParent(item);
				}
			}
		});
	       
			LBFilterEventListener.lbFilterSetup(ldId);
	}
	public void onClick$addCritiraBtnId(){
		int cnt=addedSKULBId.getItemCount();
			List<Listitem> alreadyitems=null;
		boolean flag=false;
		Listitem li=null;
		Listcell cell=null;
		boolean duflag=false;
		for(Entry<String, Listbox> entry : listMap.entrySet()){
			Listbox listbox=entry.getValue();
			Set<Listitem> set=listbox.getSelectedItems();
			if(set!=null && set.size()>0){
				flag=true;
				for(Listitem item:set){
					if(item.getValue()==null)continue;
					Object[] value=item.getValue();
					Listhead head=listbox.getListhead();
					Listheader header=(Listheader) head.getLastChild();
					duflag=false;
					if(addedSKULBId.getItemCount()>0) {
						alreadyitems=addedSKULBId.getItems();
						for(Listitem itm:alreadyitems) {
							Listcell headcl=(Listcell) itm.getChildren().get(2);
							Listcell itemcl=(Listcell) itm.getChildren().get(1);
							if(headcl.getLabel().equals(header.getLabel()) && itemcl.getLabel().equals(value[0])) {
								duflag=true;
								 break;
							}
						}
					}
					if(duflag) continue;
					
					li=new Listitem();
					li.addEventListener("onClick", this);
					cell=new Listcell();
					cell.setParent(li);
					
					cell=new Listcell(value[0].toString());
					cell.setValue("value");
					cell.setParent(li);
					
					cell=new Listcell(header.getLabel());
					cell.setValue("key::"+headerMap.get(header.getValue().toString()));
					cell.setParent(li);
					
					cell=new Listcell(value[1].toString());
					cell.setParent(li);
					li.setParent(addedSKULBId);
				}
				listbox.setSelectedIndex(-1);
			}
		}
		if(viewSKULbId.getSelectedCount()>0 ){
			if(selectAllSKUBtnId.getAttribute("selectionEventSource")!=null && (boolean) selectAllSKUBtnId.getAttribute("selectionEventSource")) {
				logger.info("All sku selected=================");
				flag=true;duflag=false;
				  if(addedSKULBId.getItemCount()>0) {
					alreadyitems=addedSKULBId.getItems();
					List<Listitem> list =new ArrayList<>();
					list.addAll(alreadyitems);
					for(Listitem itm:list)  {
						Listcell itemcl=(Listcell) itm.getChildren().get(2);
						  if( itemcl.getLabel().equals("SKU")) {
							addedSKULBId.removeChild(itm);
						   }
				         }
				       }
					Long totalSize=0l;
					if(searchStr.length()>0)
				    totalSize=(Long) skuFileDao.executeQuery("SELECT COUNT(distinct sku) FROM SkuFile  WHERE userId = "+orgOwnerUserId+" and sku like'%"+searchStr+"%'").get(0);
					else
						 totalSize=(Long) skuFileDao.executeQuery("SELECT COUNT(distinct sku) FROM SkuFile  WHERE userId = "+orgOwnerUserId).get(0);
					li=new Listitem();
					li.addEventListener("onClick", this);
					cell=new Listcell();
					cell.setParent(li);
					
					cell=new Listcell("--All Items--");
					cell.setValue("value");
					cell.setParent(li);
					
					cell=new Listcell("SKU");
					cell.setValue("key::"+"SKU");
					cell.setParent(li);
					
					cell=new Listcell(totalSize.toString());
					cell.setParent(li);
					li.setParent(addedSKULBId);
				   onClick$deSelectAllSKUBtnId();
			}
			else {
			flag=true;
			Set<Listitem> items=viewSKULbId.getSelectedItems();
				for(Listitem item:items){
					if(item.getValue()==null)continue;
					Object[] obj=item.getValue();
					duflag=false;
					if(addedSKULBId.getItemCount()>0) {
						alreadyitems=addedSKULBId.getItems();
						for(Listitem itm:alreadyitems)  {
							Listcell itemcl=(Listcell) itm.getChildren().get(1);
							if( itemcl.getLabel().equals(obj[0].toString())||itemcl.getLabel().equals("--All Items--")) {
								duflag=true;
								 break;
							}
						}
					}
					if(duflag) continue;
					
					li=new Listitem();
					li.addEventListener("onClick", this);
					cell=new Listcell();
					cell.setParent(li);
					
					cell=new Listcell(obj[0].toString());
					cell.setValue("value");
					cell.setParent(li);
					
					cell=new Listcell("SKU");
					cell.setValue("key::"+"SKU");
					cell.setParent(li);
					
					long count=(Long) skuFileDao.executeQuery("SELECT COUNT(*) FROM SkuFile  WHERE userId = "+orgOwnerUserId+" and sku ='"+obj[0].toString()+"'").get(0);
					cell=new Listcell(count+"");
					cell.setParent(li);
					li.setParent(addedSKULBId);
				}
				 viewSKULbId.setSelectedIndex(-1);
			 }
		}
				
		if(!flag){
			MessageUtil.setMessage("Please select at least one attribute code for creating discount rule.","color:blue", "TOP"); 
			return;
		}
		if(cnt==addedSKULBId.getItemCount()) {
			MessageUtil.setMessage("Code already present in critieria for creating discount rule.","color:blue", "TOP"); 
		   return;
	      } 
	}
	
			public void onClick$selectAllSKUBtnId(){
				viewSKULbId.selectAll();
				selectAllSKUBtnId.setVisible(false);
				logger.info(" == number of selected items == " + viewSKULbId.getSelectedCount());
				deSelectAllSKUBtnId.setVisible(true);
				selectAllSKUBtnId.setAttribute("selectionEventSource",true);
				deSelectSkuMap.clear();
		}

           public void onClick$deSelectAllSKUBtnId(){
				viewSKULbId.clearSelection();
				selectAllSKUBtnId.setVisible(true);
				deSelectAllSKUBtnId.setVisible(false);
				selectAllSKUBtnId.setAttribute("selectionEventSource",false);
				}
	private void registerEventListner(int strtIdx, int endIdx){
		try{
		
		
		String value = srchLbId.getSelectedItem().getValue().toString();
    	if(value.equals(SEARCH_BY_NAME)) {
    		
    		filterQuery = 	"FROM Coupons  WHERE userId ="+users.getUserId().longValue()+" AND couponName LIKE '%"+searchByPromoCodeNameTbId.getValue().trim()+"%'"+"  ";
	    }
    	
    	else  if(value.equals(SEARCH_BY_DATE)) {
    		validateSetCreationDate();
    		filterQuery =  "FROM Coupons WHERE userId ="+users.getUserId().longValue()+" AND userCreatedDate between '"+fromDateStr+"' AND '"+toDateStr+"' ";
	    }
       
    	else  if(value.equals(SEARCH_BY_STATUS)) {
    	String	status = codeStatusLb.getSelectedItem().getLabel();
    		if(status.equals("All")) {
    			filterQuery = " FROM  Coupons  WHERE userId ="+users.getUserId().longValue();
    		}
    		else{
    			filterQuery = "FROM Coupons  WHERE userId ="+users.getUserId().longValue()+" AND status LIKE '"+status+"' ";
    		}
    		//couponsCampList = couponsDao.getCouponsByStatus(users.getUserId(), codeStatusLb.getSelectedItem().getLabel(), strtIdx, endIdx);
    		
	    }
    	filterCountQuery = " SELECT COUNT(userId) "+filterQuery;
		
		
		

		qryPrefix="";
		
		
		//contactsGridId.setItemRenderer();
		logger.info("filterCountQuery is ::"+filterCountQuery);
		logger.info("filterQuery is ::"+filterQuery);
		
		Map<Integer, Field> objMap1 = new HashMap<Integer, Field>();


		objMap1.put(1, Coupons.class.getDeclaredField("couponName"));
		objMap1.put(2, Coupons.class.getDeclaredField("couponDescription"));
		//objMap1.put(3, Coupons.class.getDeclaredField("couponGeneratedType"));
		objMap1.put(4, Coupons.class.getDeclaredField("userCreatedDate"));
		//objMap1.put(5, Coupons.class.getDeclaredField("status"));
		//objMap1.put(7, Coupons.class.getDeclaredField("couponExpiryDate"));
		objMap1.put(8, Coupons.class.getDeclaredField("totalQty"));
		couponsPagingId.setPageSize(Integer.parseInt(memberPerPageLBId.getSelectedItem().getLabel()));
		
		viewCouponsELObj = LBFilterEventListener.grFilterSetup(contactsGridId, couponsPagingId, filterQuery, filterCountQuery, qryPrefix, objMap1);
		}catch(Exception e){
			logger.error("Exception :: ",e);
		}

	}
	public void onClick$applyFilterBtnId() throws Exception {
		String searchQuery="";
		List<Listitem> items=vendorCodeFilterLBId.getItems();
		String data="";
		if(vendorCodeTBId.getValue().trim().length()>0){
			searchQuery=vendorCodeTBId.getValue().trim().toUpperCase();
			for(Listitem item:items){
				Listcell cell=(Listcell) item.getLastChild();
				data=cell.getLabel().replaceAll("\\s+", " ");
				if(data.toUpperCase().contains(searchQuery))
					item.setVisible(true);
				else
					item.setVisible(false);
			}
		}
		else{
			for(Listitem item:items){
				item.setVisible(true);
			}
		}
		 items=deptCodeFilterLBId.getItems();
		if(deptCodeTBId.getValue().trim().length()>0){
			searchQuery=deptCodeTBId.getValue().trim().toUpperCase();
			for(Listitem item:items){
				Listcell cell=(Listcell) item.getLastChild();
				data=cell.getLabel().replaceAll("\\s+", " ");
				if(data.toUpperCase().contains(searchQuery))
					item.setVisible(true);
				else
					item.setVisible(false);
			}
		}
		else{
			for(Listitem item:items){
				item.setVisible(true);
			}	
		}
		
		 items=itemCategoryFilterLBId.getItems();
		if(itemCategoryTBId.getValue().trim().length()>0){
			searchQuery=itemCategoryTBId.getValue().trim().toUpperCase();
			for(Listitem item:items){
				Listcell cell=(Listcell) item.getLastChild();
				data=cell.getLabel().replaceAll("\\s+", " ");
				if(data.toUpperCase().contains(searchQuery))
					item.setVisible(true);
				else
					item.setVisible(false);
			}
		}
		else{
			for(Listitem item:items){
				item.setVisible(true);
			}	
		}
			
		 items=dcsFilterLBId.getItems();
		if(dcsTBId.getValue().trim().length()>0){
			searchQuery=dcsTBId.getValue().trim().toUpperCase();
			for(Listitem item:items){
				Listcell cell=(Listcell) item.getLastChild();
				data=cell.getLabel().replaceAll("\\s+", " ");
				if(data.toUpperCase().contains(searchQuery))
					item.setVisible(true);
				else
					item.setVisible(false);
			}
		}
		else{
			for(Listitem item:items){
				item.setVisible(true);
			}	
		}
		 items=classFilterLBId.getItems();
		
		if(classTBId.getValue().trim().length()>0){
			searchQuery=classTBId.getValue().trim().toUpperCase();
			for(Listitem item:items){
				Listcell cell=(Listcell) item.getLastChild();
				data=cell.getLabel().replaceAll("\\s+", " ");
				if(data.toUpperCase().contains(searchQuery))
					item.setVisible(true);
				else
					item.setVisible(false);
			}
		}
		else{
			for(Listitem item:items){
				item.setVisible(true);
			}	
		}
		 items=subClassFilterLBId.getItems();
		if(subClassTBId.getValue().trim().length()>0){
			searchQuery=subClassTBId.getValue().trim().toUpperCase();
			for(Listitem item:items){
				Listcell cell=(Listcell) item.getLastChild();
				data=cell.getLabel().replaceAll("\\s+", " ");
				if(data.toUpperCase().contains(searchQuery))
					item.setVisible(true);
				else
					item.setVisible(false);
			}
		}
		else{
			for(Listitem item:items){
				item.setVisible(true);
			}	
		}
		
		/*items=viewSKULbId.getItems();
		if(viewSkuTBId.getValue().trim().length()>0 && viewSKULbId.isVisible()){
			searchQuery=viewSkuTBId.getValue().trim().toUpperCase();
			for(Listitem item:items){
				//SkuFile skufile=item.getValue();
				Listcell cell=(Listcell) item.getChildren().get(1);
				data=cell.getLabel().replaceAll("\\s+", " ");
				 if(data.toUpperCase().contains(searchQuery))
					item.setVisible(true);
				else
					item.setVisible(false);
			}
		  }
		else{
			for(Listitem item:items){
				item.setVisible(true);
			}	
		}*/
		searchQuery=viewSkuTBId.getValue().trim();
		if(skuCkId.isChecked() && searchQuery.length()>0) {
			viewSKULbId.setVisible(true);
			skuDivId.setVisible(true);
			searchStr=searchQuery;
			Long totalSize=(Long) skuFileDao.executeQuery("SELECT COUNT(distinct sku) FROM SkuFile  WHERE userId = "+orgOwnerUserId+" and sku like'%"+searchQuery+"%'").get(0);
			skuPaging.setTotalSize(totalSize.intValue());
			skuPaging.setPageSize(10);
			skuPaging.setActivePage(0);
			skuPaging.setAttribute(LBFilterEventListener.HANDLE_ON_PAGING, "false");
		    List<Object> result=skuFileDao.getAllSkuSearchBy(orgOwnerUserId, 0, skuPaging.getPageSize(),searchQuery);
		    int cnt=viewSKULbId.getItemCount();
			for(; cnt>0;cnt--){
				viewSKULbId.removeItemAt(cnt-1);
			}
		    if(result==null)return;
			Listitem item=null; 
			for(Object object:result) {
				item=new Listitem();
				Object[] obj=(Object[]) object;
				item.setValue(obj);
				new Listcell().setParent(item);
				new Listcell(obj[0].toString()).setParent(item);
				new Listcell(obj[1]!=null?obj[1].toString():"").setParent(item);
				new Listcell(obj[2]!=null?obj[2].toString():"").setParent(item);
				item.setParent(viewSKULbId);
			}
			}
			else {
				onCheck$skuCkId();
				searchStr="";
			}
		//LBFilterEventListener.lbFilterSetup(viewSKULbId);
	}
	
	private void filterButtonChk(String criteria) {
				
		Set<Listitem> selItems = null;
		String tempStr;
		query = " FROM SkuFile  WHERE userId =  "+users.getUserId().longValue()+" ";
		countQuery = "SELECT COUNT(*) FROM SkuFile  WHERE userId = "+users.getUserId().longValue()+" ";
		String itemCategStr = "";
		selItems = itemCategoryFilterLBId.getSelectedItems();
		if(selItems != null && selItems.size() > 0) {
			for (Listitem eachLid : selItems) {
				if(eachLid.isVisible()==false) continue;
				tempStr = ((Listcell)eachLid.getLastChild()).getLabel();
				if(tempStr.contains("'")){
					tempStr = StringEscapeUtils.escapeSql(tempStr); 
				}
				if(!itemCategStr.isEmpty()) itemCategStr +=", ";
				itemCategStr +="'"+ tempStr+"'";
			}
			if(!itemCategStr.isEmpty()) {
				query = query + " AND itemCategory in ("+itemCategStr+") ";
				countQuery = countQuery + " AND itemCategory in ("+itemCategStr+") ";
			}
		}
		
		
		//Department Code
		selItems = deptCodeFilterLBId.getSelectedItems();
		String deptCodeStr = "";
		if(selItems != null && selItems.size() > 0) {
			for (Listitem eachLid : selItems) {
				if(eachLid.isVisible()==false) continue;
				tempStr = ((Listcell)eachLid.getLastChild()).getLabel();
				if(tempStr.contains("'")){
					tempStr = StringEscapeUtils.escapeSql(tempStr); 
				}
				if(!deptCodeStr.isEmpty()) deptCodeStr +=", ";
				deptCodeStr +="'"+ tempStr+"'";
			}
			if(!deptCodeStr.isEmpty()) {
				query = query + " AND departmentCode in ("+deptCodeStr+") ";
				countQuery = countQuery + " AND departmentCode in ("+deptCodeStr+") ";
			}
		}
		
		
		//Vendor code
		selItems = vendorCodeFilterLBId.getSelectedItems();
		String vendCodeStr = "";
		if(selItems != null && selItems.size() > 0) {
			for (Listitem eachLid : selItems) {
				if(eachLid.isVisible()==false) continue;
				tempStr = ((Listcell)eachLid.getLastChild()).getLabel();
				if(tempStr.contains("'")){
					tempStr = StringEscapeUtils.escapeSql(tempStr); 
				}
				if(!vendCodeStr.isEmpty()) vendCodeStr +=", ";
				vendCodeStr +="'"+ tempStr+"'";
			}
			
			if(!vendCodeStr.isEmpty()) {
				query = query + " AND vendorCode in ("+vendCodeStr+") ";
				countQuery = countQuery + " AND vendorCode in ("+vendCodeStr+") ";
			}
		}

		//Calss Code
		selItems = classFilterLBId.getSelectedItems();
		String calssCodeStr = "";
		if(selItems != null && selItems.size() > 0) {
			for (Listitem eachLid : selItems) {
				if(eachLid.isVisible()==false) continue;
				tempStr = ((Listcell)eachLid.getLastChild()).getLabel();
				if(tempStr.contains("'")){
					tempStr = StringEscapeUtils.escapeSql(tempStr); 
				}
				if(!calssCodeStr.isEmpty()) calssCodeStr +=", ";
				calssCodeStr +="'"+ tempStr+"'";
			}
			
			if(!calssCodeStr.isEmpty()) {
				query = query + " AND classCode in ("+calssCodeStr+") ";
				countQuery = countQuery + " AND classCode in ("+calssCodeStr+") ";
			}
		}
		
		//Sub Class
		selItems = subClassFilterLBId.getSelectedItems();
		String subCalssCodeStr = "";
		if(selItems != null && selItems.size() > 0) {
			for (Listitem eachLid : selItems) {
				if(eachLid.isVisible()==false) continue;
				tempStr = ((Listcell)eachLid.getLastChild()).getLabel();
				if(tempStr.contains("'")){
					tempStr = StringEscapeUtils.escapeSql(tempStr); 
				}
				if(!subCalssCodeStr.isEmpty()) subCalssCodeStr +=", ";
				subCalssCodeStr +="'"+ tempStr+"'";
			}
			
			if(!subCalssCodeStr.isEmpty()) {
				query = query + " AND subClassCode in ("+subCalssCodeStr+") ";
				countQuery = countQuery + " AND subClassCode in ("+subCalssCodeStr+") ";
			}
		}
		
		
		//DCS
		selItems = dcsFilterLBId.getSelectedItems();
		String dcsCodeStr = "";
		if(selItems != null && selItems.size() > 0) {
			for (Listitem eachLid : selItems) {
				if(eachLid.isVisible()==false) continue;
				tempStr = ((Listcell)eachLid.getLastChild()).getLabel();
				if(tempStr.contains("'")){
					
					tempStr = StringEscapeUtils.escapeSql(tempStr); 
				}
				if(!dcsCodeStr.isEmpty()) dcsCodeStr +=", ";
				dcsCodeStr +="'"+ tempStr+"'";
			}
			
			if(!dcsCodeStr.isEmpty()) {
				query = query + " AND DCS in ("+dcsCodeStr+") ";
				countQuery = countQuery + " AND DCS in ("+dcsCodeStr+") ";
			}
		}
		
//		query +="  GROUP BY itemSid";
//		countQuery +="  GROUP BY itemSid";
		
		logger.info(" Apply Filter Query is :: "+query);
		logger.info(" Apply Filter countQuery  is :: "+countQuery);
		query = " FROM SkuFile  WHERE userId =  "+users.getUserId().longValue()+" "+criteria;
		List<SkuFile> list=skuFileDao.executeQuery(query);
		Listitem li=null;
		Listcell cell=null;
		int cnt=viewSKULbId.getItemCount();
		for(; cnt>0;cnt--){
			viewSKULbId.removeItemAt(cnt-1);
		}
		for(SkuFile sku:list){
			li=new Listitem();
			
			li.setValue(sku);
			cell=new Listcell();
			cell.setParent(li);
			
			cell=new Listcell(sku.getSku());
			cell.setParent(li);
			
			cell=new Listcell(sku.getListPrice()!=null?sku.getListPrice().toString():"");
			cell.setParent(li);
			
			cell=new Listcell(sku.getDescription());
			cell.setParent(li);
			
			li.setParent(viewSKULbId);
			
		}
		LBFilterEventListener.lbFilterSetup(viewSKULbId);
		/*query = StringEscapeUtils.escapeSql(query);
		countQuery = StringEscapeUtils.escapeSql(countQuery);*/
	/*	viewSkuELObj.setQuery(query);
		viewSkuELObj.setCountQuery(countQuery);
		Utility.refreshModel(viewSkuELObj,  0, true);*/
		
	}
	 private void setheaders() {
		 
		 String str1= "";
		 String str2 = "";
		 if(percentRadioId.isChecked()) str1 = "Percentage"; else str1 = "Value";
		 column1Id.setLabel(str1); 
		 if(tpaRadioId.isChecked()){
			 str2 = "On Receipt";
		   column2Id.setLabel(str2);
		   column3Id.setVisible(false);
		   column4Id.setVisible(false);
		 }
		 else {
			 str2 = "Type";
			 column2Id.setLabel(str2);
			 column3Id.setVisible(true);
			 column4Id.setVisible(true);
		 }
	 }
	 String limit=null;
	 Long start=null;
	 Long end=null;
	 String searchStr="";
	 Long noOfitem=0l;
		 public void onClick$selectBtnId() {
		 discountDblBxId.setStyle(NORMAL_STYLE);
			totPurAmtLngBxId.setStyle(NORMAL_STYLE);
			
			//Discount
			logger.debug("totPurAmtLngBxId.getValue() is  :;"+discountDblBxId.getValue()+"  puchase value :"+totPurAmtLngBxId.getValue());
			if(discountDblBxId.getValue() == null || discountDblBxId.getValue() <=0) {
				discountDblBxId.setStyle(ERROR_STYLE);  
				MessageUtil.setMessage("Please provide valid discount value.","color:red","TOP");
				return;
			}
			
			if(tpaRadioId.isChecked()  && mpvChkBoxId.isChecked()){
				if(totPurAmtLngBxId.getValue() == null || totPurAmtLngBxId.getValue() <= 0) {
					totPurAmtLngBxId.setStyle(ERROR_STYLE);
					MessageUtil.setMessage("Please provide valid minimum purchase value.","color:red","TOP");
					return;
				}else if( dollerRadioId.isChecked() && (discountDblBxId.getValue() > totPurAmtLngBxId.getValue())) {
					MessageUtil.setMessage("Discount Type can not exceed the  Minimum Purchase Amount.","color:red","TOP");
					return;
				}
			}
			
			if(percentRadioId.isChecked() && 
					discountDblBxId.getValue() > 100) {
				MessageUtil.setMessage("Discount percentage cannot be more than 100.","color:red","TOP");
				return;
			}
			String discTypeStr = "";
		        if(percentRadioId.isChecked()) {
			      discTypeStr = DISC_TYPE_PERCENTAGE ;
			       dollerRadioId.setDisabled(true);
		      }
		      else{
				discTypeStr = DISC_TYPE_VALUE;
				percentRadioId.setDisabled(true);
		        }
		      setheaders();
			if(skuRadioId.isChecked()) {
			Set<Listitem> selctedItemSet = addedSKULBId.getSelectedItems();
			if(selctedItemSet == null || selctedItemSet.size()==0){
				MessageUtil.setMessage("Please select at least one attribute code for creating discount rule.","color:red","TOP");
				return;
			}
			Row row=null;
			Label label=null;
			List<SKUTemp> listSku=null;
			Long startId=null;
			Long endId=null;
			boolean dupflag=true;
			Map<String ,String> map=new LinkedHashMap<>();
			List<SKUTemp> oldlistSku=coupDiscGenDao.findTempSkuBy(users.getUserId(),null,limit);
			if(selctedItemSet != null && selctedItemSet.size() >0) {
				listSku=new LinkedList<>();
				Iterator<Listitem> itr = selctedItemSet.iterator();
				List<Listitem> allItems=addedSKULBId.getItems();
				logger.info(" Total item size :"+allItems.size());
				while(itr.hasNext()){
					Listitem item=itr.next();
					List<Component> cells=item.getChildren();
					SKUTemp skuTemp=null;
					String attribute=null;
					String value=null;
					for(Component cmp:cells){
						Listcell cell =(Listcell) cmp;
						if(cell.getValue()!=null && cell.getValue().toString().startsWith("key")){
							attribute=cell.getValue().toString().split("::")[1];
						}
						else if(cell.getValue()!=null && cell.getValue().equals("value")){
							value=cell.getLabel();
						}
					}
					if(value.equals("--All Items--")) {
						List<Long> templist=coupDiscGenDao.findBySkuUser(users.getUserId(),discountDblBxId.getValue(),"SKU",null,null);
						if(templist!=null && templist.get(0)>0) {
							 MessageUtil.setMessage("Some or all items from your current selection are already added in the discount criteria. \n "
							 		+ "Please delete the previous discount criteria to create this discount criteria.","color:red","TOP");
								return;
						}
						coupDiscGenDaoForDML.insertNewIntoSkuTempBy(users.getUserId(),discountDblBxId.getValue(),"SKU",orgOwnerUserId,searchStr);
						map.put("SKU::"+discountDblBxId.getValue(), "--All Items--");
						dupflag=false;
						continue;
					}
					boolean flag=false;
					if(oldlistSku!=null && oldlistSku.size()>0){
						for(SKUTemp oldSku:oldlistSku){
							if(oldSku.getSkuAttribute().equals(attribute)&&oldSku.getSkuValue().equals(value)&&
									oldSku.getDiscount().equals(Double.parseDouble(discountDblBxId.getValue().toString())))
						flag=true;
						}
					}
					if(flag)continue;
					skuTemp=new SKUTemp();
					skuTemp.setUserId(users.getUserId());
					skuTemp.setDiscount(discountDblBxId.getValue());
					skuTemp.setDiscountType(discTypeStr);
					skuTemp.setSkuAttribute(attribute);
					skuTemp.setSkuValue(value);
					skuTemp.setOwnerId(orgOwnerUserId);
					if(tempCouponObj!=null)
						skuTemp.setCouponId(tempCouponObj.getCouponId());
					listSku.add(skuTemp);
					if(listSku.size()>=500){
					coupDiscGenDaoForDML.saveByCollection(listSku);
					if(startId==null)
					startId=listSku.get(0).getSkuTempId();
					endId=listSku.get(listSku.size()-1).getSkuTempId();
					Collections.reverse(listSku);
					for(SKUTemp sku:listSku){
						if(map.containsKey(sku.getSkuAttribute()+"::"+sku.getDiscount())){
							value=(map.get(sku.getSkuAttribute()+"::"+sku.getDiscount()));
							if(value !=null && value.length()<=100)
							map.put(sku.getSkuAttribute()+"::"+sku.getDiscount(),value.concat(", ")+sku.getSkuValue());
						  }
						else{
							map.put(sku.getSkuAttribute()+"::"+sku.getDiscount(), sku.getSkuValue());
						  }
					     }
					dupflag=false;
					listSku=new ArrayList<>();
					}
					//item.setDisabled(true);
				}
				
				if(listSku.size()>0 && listSku.size()<500){
				coupDiscGenDaoForDML.saveByCollection(listSku);
				if(startId==null)
					startId=listSku.get(0).getSkuTempId();
				endId=listSku.get(listSku.size()-1).getSkuTempId();
				String value=null;
				Collections.reverse(listSku);
				for(SKUTemp sku:listSku){
					if(map.containsKey(sku.getSkuAttribute()+"::"+sku.getDiscount())){
						value=(map.get(sku.getSkuAttribute()+"::"+sku.getDiscount()));
						if(value!=null && value.length()<=100)
						map.put(sku.getSkuAttribute()+"::"+sku.getDiscount(),value.concat(", ")+sku.getSkuValue());
					  }
					else{
						map.put(sku.getSkuAttribute()+"::"+sku.getDiscount(), sku.getSkuValue());
					  }
				     }
				}
				if(dupflag && listSku.size()==0) {
					 MessageUtil.setMessage("All SKU/s are selected for another discount code.","color:red","TOP");
						return;
				}
				end=endId;
				if(start==null)
					start=startId;
				limit=start+"::"+end;
				for(Entry<String, String> entry : map.entrySet()){
					boolean flag=false;
					String type=entry.getKey().split("::")[0];
					type=type.contains("Vendor")?"Vendor Code":type.contains("Department")?"Department Code":type.contains("Subclass")?"Sub-Class":type;
					if(discountGenRowsId.getVisibleItemCount()>0) {
							List<Component> rowList = discountGenRowsId.getChildren();
							if(rowList !=null && rowList.size() > 0) {
								for (Object object : rowList) {
									Row existRow = (Row)object;
									Label tempDisLbl = (Label)existRow.getChildren().get(0);
									Label tempTypelbl=(Label)existRow.getChildren().get(1);
									if(tempDisLbl.getValue() .equals(""+discountDblBxId.getValue())&&tempTypelbl.getValue().equals(type) ) {
										row = existRow;
										String str=row.getValue();
										startId=Long.parseLong(str.split("::")[0]);
										row.setValue(startId+"::"+endId);
										Label codelbl=(Label) existRow.getChildren().get(2);
										String code=codelbl.getValue().concat(","+map.get(entry.getKey()));
										codelbl.setValue(code);
										flag=true;
										List<Long> templist=coupDiscGenDao.findBySkuUser(users.getUserId(),discountDblBxId.getValue(), entry.getKey().split("::")[0],null,null);
										noOfitem=templist.get(0);
										Label noitemLabel=(Label) existRow.getChildren().get(3);
										noitemLabel.setValue(noOfitem.toString());
									}
								}
							}
					}
					if(!flag) {
					row=new Row();	
					row.setValue(startId+"::"+endId);
					label=new Label(discountDblBxId.getValue().toString());
					label.setParent(row);
					
					
					label=new Label(type);
					label.setAttribute("Type", entry.getKey().split("::")[0]);
					label.setParent(row);
					
					label=new Label(map.get(entry.getKey()));
					label.setParent(row);
					List<Long> templist=coupDiscGenDao.findBySkuUser(users.getUserId(),discountDblBxId.getValue(), entry.getKey().split("::")[0],null,null);
					noOfitem=templist.get(0);
					label=new Label(noOfitem.toString());
					label.setParent(row);
					
					Image delImg =  new Image("img/delt_icn.png");
					delImg.setStyle("cursor:pointer;");
					delImg.setAttribute("COUPON_TYPE", COUPON_DISCOUNT_DELETE);
					delImg.addEventListener("onClick", this);
					delImg.setParent(row);
					
					row.setParent(discountGenRowsId);
					}
				}
				allItems.removeAll(selctedItemSet);
				logger.info(" Remain item list size "+allItems);
				Iterator<Listitem> it=allItems.iterator();
						while(it.hasNext()){
							it.next().setParent(addedSKULBId);
						}
			}
			}
			else{
				Long tpAmtLong = totPurAmtLngBxId.getValue() == null ? 0:totPurAmtLngBxId.getValue();
				List<SKUTemp> oldlistSku=coupDiscGenDao.findTempSkuBy(users.getUserId(),null,limit);
				
				List<SKUTemp> skuList=new ArrayList<>();
				if(oldlistSku!=null && oldlistSku.size()>0){
					for(SKUTemp sku:oldlistSku){
						if(sku.getDiscount().equals(Double.parseDouble(discountDblBxId.getValue().toString()))){
							MessageUtil.setMessage( discountDblBxId.getValue().toString()+" Value already selected..,","color:red","TOP");
							return;
						}
						
						else if(sku.getTotPurchaseAmount().equals(tpAmtLong)){
						MessageUtil.setMessage(tpAmtLong!=0?tpAmtLong.toString():"N/A" +" Value already selected..,","color:red","TOP");
						return;
						}
					}
				}
			    SKUTemp	skuTemp=new SKUTemp();
				skuTemp.setUserId(users.getUserId());
				skuTemp.setDiscount(discountDblBxId.getValue());
				skuTemp.setDiscountType(discTypeStr);
				skuTemp.setTotPurchaseAmount(tpAmtLong);
				skuTemp.setOwnerId(orgOwnerUserId);
				if(tempCouponObj!=null)
					skuTemp.setCouponId(tempCouponObj.getCouponId());
				skuList.add(skuTemp);
				coupDiscGenDaoForDML.saveByCollection(skuList);
				if(start==null)
					start=skuList.get(0).getSkuTempId();
				end=skuList.get(skuList.size()-1).getSkuTempId();
				limit=start+"::"+end;
				Row row=new Row();
				Label label=new Label(discountDblBxId.getValue().toString());
				label.setValue(discountDblBxId.getValue().toString());
				label.setParent(row);
				
				label=new Label(tpAmtLong!=0?tpAmtLong.toString():"N/A");
				label.setValue(tpAmtLong!=0?tpAmtLong.toString():"N/A");
				label.setParent(row);
				
				label=new Label("");
				label.setParent(row);
				
				label=new Label("");
				label.setParent(row);
				
				Image delImg =  new Image("img/delt_icn.png");
				delImg.setStyle("cursor:pointer;");
				delImg.setAttribute("COUPON_TYPE", COUPON_DISCOUNT_DELETE);
				delImg.addEventListener("onClick", this);
				delImg.setParent(row);
				
				row.setParent(discountGenRowsId);
			}
		
	 }
	
	
	private void getAllPromoFromDB(){
		totalCount = couponsDao.findTotCountCoupons(users.getUserId());
		//couponsPagingId.addEventListener("onPaging", this);
		couponsPagingId.setTotalSize(totalCount);
		
		//set  default Paging size 
		memberPerPageLBId.setSelectedIndex(0);
		//get only limit of the PromoCode From DB
		//fillCouponsBySize(0,5);
	} // getAllPromoFromDB
	
	private int p=0;
	Map<String,SkuFile> selectOfSkuMap = new HashMap<String, SkuFile>();
	Map<Long,SkuFile> deSelectSkuMap = new HashMap<Long, SkuFile>();
	public void onPaging$skuPaging(ForwardEvent event) {
		logger.debug("Paging Event called here ");
		PagingEvent pagingEvent =(PagingEvent) event.getOrigin();
		int desiredPage = pagingEvent.getActivePage();
		int pSize = pagingEvent.getPageable().getPageSize();
		int ofs = desiredPage * pSize;
		 List<Object> result=skuFileDao.getAllSkuSearchBy(orgOwnerUserId, ofs, (byte) pSize,searchStr);
		 int cnt=viewSKULbId.getItemCount();
			for(; cnt>0;cnt--){
				viewSKULbId.removeItemAt(cnt-1);
			}
		    if(result==null)return;
			Listitem item=null; 
			for(Object data:result) {
				item=new Listitem();
				Object[] obj=(Object[]) data;
				item.setValue(obj);
				new Listcell().setParent(item);
				new Listcell(obj[0].toString()).setParent(item);
				new Listcell(obj[1]!=null?obj[1].toString():"").setParent(item);
				new Listcell(obj[2]!=null?obj[2].toString():"").setParent(item);
				item.setParent(viewSKULbId);
			}
	}
	@Override
	public void render(Listitem li, SkuFile sku, int arg2) throws Exception {
		
		
		li.setValue(sku);
		li.appendChild(new Listcell(""));
		
		if(sku.getSku() != null){
			li.appendChild(new Listcell(sku.getSku()));
		}else {
			li.appendChild(new Listcell(sku.getSku()));
		}
		if(sku.getListPrice() != null) {
			li.appendChild(new Listcell(sku.getListPrice().toString()));
		}else {
			li.appendChild(new Listcell(sku.getSku()));
		}
		if(sku.getDescription() != null) {
			li.appendChild(new Listcell(sku.getDescription()));
		}else {
			li.appendChild(new Listcell(sku.getSku()));
		}
		
	}
	
	
	
	public void onSelect$memberPerPageLBId() {
		String selectStr = memberPerPageLBId.getSelectedItem().getLabel();
		int pNo = Integer.parseInt(selectStr);
		
		couponsPagingId.setPageSize(pNo);
			
		couponsPagingId.setActivePage(0);
		Utility.refreshGridModel(viewCouponsELObj, 0, false);
	//	fillCouponsBySize(0 ,pNo);
	} // onSelect$memberPerPageLBId
	
	private void fillCouponsBySize(int strtIdx, int endIdx) {

		
		//List<Coupons> couponsCampList = couponsDao.findCouponsByUserIdWithLimits(users.getUserId(), strtIdx, endIdx);
		List<Coupons> couponsCampList = null;
		
		
		String value = srchLbId.getSelectedItem().getValue().toString();
    	if(value.equals(SEARCH_BY_NAME)) {
    		couponsCampList = couponsDao.getCouponsByPromoName(users.getUserId(), searchByPromoCodeNameTbId.getValue().trim(), strtIdx, endIdx);
	    }
    	
    	else  if(value.equals(SEARCH_BY_DATE)) {
    		couponsCampList = couponsDao.getPromoCodesBetweenCreationDates(fromDateStr, toDateStr, users.getUserId(), strtIdx, endIdx);
	    }
       
    	else  if(value.equals(SEARCH_BY_STATUS)) {
    		couponsCampList = couponsDao.getCouponsByStatus(users.getUserId(), codeStatusLb.getSelectedItem().getLabel(), strtIdx, endIdx);
	    }
		
		logger.debug(">>>>>"+couponsCampList);
		if(couponsCampList == null) return;
		Components.removeAllChildren(couponRowsId);
		Row tempRow;
		Label tempLbl = null; 
		Div tempDiv = null;
		
		
		for (Coupons coupons : couponsCampList) {
			
			tempRow = new Row();
			
			//Coupon Name
			tempLbl = new Label(coupons.getCouponName());
			tempLbl.setParent(tempRow);
			
			//Description
			/*String desc=coupons.getCouponDescription();
			if(desc != null && desc.contains("[PHCurr]")){
				desc = desc.replace("[PHCurr]", Utility.countryCurrencyMap.get(users.getCountryType()));
			}
			logger.info("desc======"+desc);
			tempLbl = new Label(desc);
			tempLbl.setTooltip(coupons.getCouponDescription());*/
			tempLbl = new Label(coupons.getCouponDescription());
			tempLbl.setParent(tempRow);
			
			//Promo code type Single or Multiple
			/*
			 * if(coupons.getCouponGeneratedType().equalsIgnoreCase("single")) tempLbl = new
			 * Label("Single code"); else
			 * if(coupons.getCouponGeneratedType().equalsIgnoreCase("multiple")) tempLbl =
			 * new Label("Multiple & Random code"); tempLbl.setParent(tempRow);
			 */
			if(coupons.getCouponGeneratedType().equalsIgnoreCase("single")) {
				if(coupons.getLoyaltyPoints() != null && (coupons.getRequiredLoyltyPoits() != null || coupons.getMultiplierValue()!=null)) {
				if(coupons.getValueCode() != null &&
						!coupons.getValueCode().equals(OCConstants.LOYALTY_POINTS)) {
				tempLbl = new Label("Rewards");
				}
			else if(coupons.getValueCode() == null || coupons.getValueCode().equals(OCConstants.LOYALTY_POINTS)){
				tempLbl = new Label("Loyalty");
			}
				}else tempLbl = new Label("Promotions");
			}
				
			else if(coupons.getCouponGeneratedType().equalsIgnoreCase("multiple"))
				tempLbl = new Label("Coupons");
			tempLbl.setParent(tempRow);
			
			logger.info("coupon name is ::"+coupons.getCouponName());
		//	logger.info("created date is ::"+coupons.getCouponCreatedDate().getTime());
			//Created On
			tempLbl = new Label(MyCalendar.calendarToString(coupons.getUserCreatedDate(), MyCalendar.FORMAT_DATEONLY_GENERAL));
			tempLbl.setParent(tempRow);
			
			//Status
			tempLbl = new Label(coupons.getStatus());
			tempLbl.setParent(tempRow);
			
			//Discount
			String tempStr= "";
			if(coupons.getDiscountType().trim().equals("Percentage")) { //TODO set the constants
				tempStr = "% on";
			}else  {
				tempStr = userCurrencySymbol+" on";
			}
			
			if(coupons.getDiscountCriteria().trim().contains("SKU")) {
				tempStr += " Product";
			}else {
				tempStr += " Receipt";
			}
			//tempStr = tempStr+" "+coupons.getDiscountCriteria().trim();
			
			tempLbl = new Label(tempStr);
			tempLbl.setParent(tempRow);
			
			
			//Validity Period
			if(coupons.getExpiryType().equalsIgnoreCase(Constants.COUP_VALIDITY_PERIOD_DYNAMIC)){
				logger.info("dynamic validity-------");
				tempLbl = new Label("Dynamic");
				tempLbl.setParent(tempRow);
			}
			else if(coupons.getExpiryType().equalsIgnoreCase(Constants.COUP_VALIDITY_PERIOD_STATIC)){
				logger.info("static validity-------");
				tempLbl = new Label(MyCalendar.calendarToString(coupons.getCouponCreatedDate(), MyCalendar.FORMAT_DATEONLY_GENERAL)+" - " +
						MyCalendar.calendarToString(coupons.getCouponExpiryDate(), MyCalendar.FORMAT_DATEONLY_GENERAL));
				tempLbl.setParent(tempRow);
			}
			
			//No.of Codes
			
			String numOfCodeStr ="";
			boolean isSingleType = false;
			if(coupons.getCouponGeneratedType().equals("single")) {
				
				if(coupons.getAutoIncrCheck()) {
					numOfCodeStr = "Unlimited";
				}else {
					numOfCodeStr = ""+coupons.getTotalQty();
				}
				
				isSingleType =true;
			}else {
				if(coupons.getAutoIncrCheck()) {
					numOfCodeStr = coupons.getTotalQty()+"(Auto add)";
				}else {
					numOfCodeStr = coupons.getTotalQty()+"";
				}
			}
			tempLbl = new Label(numOfCodeStr);
			tempLbl.setParent(tempRow);
			
			//Actions
			tempDiv = new Div();
			
				//Edit Image
			Image editImg = new Image("/img/email_edit.gif");
			editImg.setStyle("cursor:pointer;margin-right:10px;");
			editImg.setTooltiptext("Edit");
			editImg.setAttribute("COUPON_TYPE", COUPON_EDIT);
			editImg.addEventListener("onClick", this);
			editImg.setParent(tempDiv);
			
				
			//Active Image
			Image tempImg = new Image();
			String statusStr = coupons.getStatus();
			
			//Pause or Active images
			if(statusStr.equals(Constants.COUP_STATUS_ACTIVE)  || statusStr.equals(Constants.COUP_STATUS_RUNNING)
					|| statusStr.equals(Constants.COUP_STATUS_EXPIRED)) {
				
				tempImg.setSrc("/img/pause_icn.png");
				tempImg.setTooltiptext("Pause");
				
			}
			else if(statusStr.equals(Constants.COUP_STATUS_PAUSED)) {
				tempImg.setSrc("/img/play_icn.png");
				tempImg.setTooltiptext(Constants.COUP_STATUS_ACTIVE);
				
			}
			
			tempImg.setAttribute("COUPON_TYPE", COUPON_STATUS);
			tempImg.setStyle("cursor:pointer;margin-right:10px;");
			tempImg.addEventListener("onClick", this);
			tempImg.setParent(tempDiv);
			
			
				//Delete Image
			Image delImg = new Image("/img/action_delete.gif");
			delImg.setStyle("cursor:pointer;margin-right:10px;");
			delImg.setAttribute("COUPON_TYPE", COUPON_DELETE);
			delImg.setTooltiptext("Delete");
			delImg.addEventListener("onClick", this);
			delImg.setParent(tempDiv);
				//reports Image
			Image repImg = new Image("/img/theme/home/reports_icon.png");
			repImg.setStyle("cursor:pointer;margin-right:10px;");
			repImg.setAttribute("COUPON_TYPE", COUPON_REPORTS);
			repImg.setTooltiptext("Reports");
			repImg.addEventListener("onClick", this);
			repImg.setParent(tempDiv);
			
			if(!isSingleType) {
				//export promocodes
				Image exportPromoCodeImg = new Image("/img/icons/Export-of-Promo-codes-icon.png");
				exportPromoCodeImg.setStyle("cursor:pointer;margin-right:10px;");
				exportPromoCodeImg.setAttribute("COUPON_TYPE", COUPON_EXPORT);
				exportPromoCodeImg.setTooltiptext("Export");
				exportPromoCodeImg.addEventListener("onClick", this);
				exportPromoCodeImg.setParent(tempDiv);
			}
			
//			tempDiv.setAttribute("COUPON_OBJ", coupons);
			tempDiv.setParent(tempRow);
			tempRow.setAttribute("COUPON_OBJ", coupons);
			tempRow.setParent(couponRowsId);
			
		} 
	
	}// fillCouponsBySize
	
	
	//allStoresChkBxId
	public void onSelect$manageCouponsTabBoxId() {
//		Sessions.getCurrent().removeAttribute("EDIT_COUPON_OBJ");
		
		coupDiscGenDaoForDML.deleteAllSkuBy(users.getUserId(),null);
		if(posMl == null) {
				MessageUtil.setMessage("Please create POS List.","color:red","TOP");
				manageCouponsTabBoxId.setSelectedIndex(0);
				//Redirect.goToPreviousPage();
				return;
			}
		int cnt=addedSKULBId.getItemCount();
		for(; cnt>0;cnt--){
			addedSKULBId.removeItemAt(cnt-1);
		}
		couponNameTxtBxId.setValue("");
		couponNameTxtBxId.setDisabled(false);
		coupDescTxtBxId.setValue("");
		
		//barcode changes
		bcenableCoupChkBxId.setChecked(false);
		bcsizeCoupDivId.setVisible(false);
		 
		singCoupTxtBxId.setValue("Enter Code");
		singCoupTxtBxId.setDisabled(false);
		singlelimitLongbxId.setValue(null);
		singlelimitLongbxId.setDisabled(false);
		
		singCoupChkBxId.setChecked(false);
		singRadioId.setDisabled(false);
		coupGenRadioGrId.setSelectedIndex(0);
		onCheck$coupGenRadioGrId();
		dynamicValidityTbId.setValue("");
		onlylpChkBxId.setChecked(false);
		multiplePerReceiptChkBxId.setChecked(false);
		lpIBxId.setValue(null);
		loyatyPntshboxId.setVisible(false);
		redeemdlimitLongbxId.setValue(null);
		redeemdlimitLongbxId.setDisabled(false);
		redeemdsingCoupChkBxId.setChecked(false);
		perSubUselimitLongbxId.setValue(1l);
		unlimitSubChkBxId.setChecked(false);
		perSubUselimitLongbxId.setDisabled(false);
		
		multRadioId.setDisabled(false);
		multiCodLimitLongBxId.setValue(null);
		multiCoupChkBxId.setChecked(false);
		percentRadioId.setSelected(true);
		percentRadioId.setDisabled(false);
		dollerRadioId.setDisabled(false);
		skuRadioId.setDisabled(false);
		tpaRadioId.setDisabled(false);
		mpvChkBoxId.setChecked(false);
		totPurAmtLngBxId.setDisabled(true);
		Date date = null;
		Calendar.getInstance();
//		createDateBxId.setConstraint((Constraint) null);
		createDateBxId.setValue(date);
//		createDateBxId.setConstraint("after "+MyCalendar.calendarToString(cal1, MyCalendar.FORMAT_SB_DATEONLY));
		/*cal1.set(Calendar.DATE, cal1.get(Calendar.DATE)+1);
		
		logger.info("cal1 time is  ::"+cal1.getTime());*/
		expiryDateBxId.setValue(date);
		
		staticRadio.setDisabled(false);
		createDateBxId.setDisabled(false);
		expiryDateBxId.setDisabled(false);
		
		dynamicRadio.setDisabled(false);
		dynamicValidityTbId.setDisabled(false);
		dynamicValidityLbId.setDisabled(false);
		
		Components.removeAllChildren(discountGenRowsId);
		discountDblBxId.setValue(null);
		listOfDisItemSidMap.clear();
		listOfDisTotPurMap.clear();
		
		totPurAmtLngBxId.setValue(null);
		tempCouponObj = null;
		genCoupBtnId.setLabel("Save Discount Code");
		
		
		if(viewSKULbId.getItemCount() >0) {
			viewSKULbId.setSelectedIndex(-1);
		}
		allStoresChkBxId.setChecked(false);
		addStoreImgId.setVisible(true);
		Components.removeAllChildren(selectedStoreLbId);
		//onClick$clearFilterBtnId();
		/*if(skuRadioId.isChecked()) {
			storeSelectDivId.setVisible(true);
			onCheck$allStoresChkBxId();
		}*/
		storeNumbCmboBxId.setSelectedItem(null);
		Components.removeAllChildren(selectedStoreLbId);
		onCheck$mpvChkBoxId();
		discountRadioGrId.setSelectedIndex(0);
		onCheck$discountRadioGrId() ;
		onCheck$skuCkId();
		/*
		vendorCodeFilterLBId.setSelectedIndex(-1);
		itemCategoryFilterLBId.setSelectedIndex(-1);
		 deptCodeFilterLBId.setSelectedIndex(-1);
		classFilterLBId.setSelectedIndex(-1);
		subClassFilterLBId.setSelectedIndex(-1);
		 dcsFilterLBId.setSelectedIndex(-1);
		 viewSKULbId.setSelectedIndex(-1);*/
	} //onSelect$manageCouponsTabBoxId
	
	public void onCheck$singCoupChkBxId() {
		if(singCoupChkBxId.isChecked()) {
			singlelimitLongbxId.setDisabled(true);
		}else singlelimitLongbxId.setDisabled(false);
	} // onCheck$singCoupChkBxId
	
	public void onCheck$bcenableCoupChkBxId() {
		if(bcenableCoupChkBxId.isChecked()) {
			bcsizeCoupDivId.setVisible(true);
		}else {
			bcsizeCoupDivId.setVisible(false);
		}
	} // onCheck$singCoupChkBxId
	
	private Image addStoreImgId;
	public void onCheck$allStoresChkBxId() {
		if(allStoresChkBxId.isChecked()) {
			storeNumbCmboBxId.setDisabled(true);
			selectedStoreLbId.setDisabled(true);
			setVisibleItems(selectedStoreLbId,false);
			addStoreImgId.setVisible(false);
		}else {
			storeNumbCmboBxId.setDisabled(false);
			selectedStoreLbId.setDisabled(false);
			setVisibleItems(selectedStoreLbId,true);
//			addStoreImgId.setVisible(true);
			addStoreImgId.setVisible(true);
		}
//		selectedStoreDivId.setVisible(!allStoresChkBxId.isChecked());
	}
	
	private void setVisibleItems(Listbox lstBox, boolean flag) {
		
		List<Listitem> chaildItemLst = lstBox.getItems();
		for (Listitem listitem : chaildItemLst) {
			Image delImg = ((Image)((Listcell)listitem.getLastChild()).getFirstChild());
			delImg.setVisible(flag);
		}
	}
	
	
	public void onSelect$bctypeListboxId(){
		setDefaultDimensions();
	}
	
	
	public void onCheck$coupGenRadioGrId() {
		
		if(coupGenRadioGrId.getSelectedIndex() == 0) {
			multiSelCoupDivId.setVisible(false);
			singleSelCoupDivId.setVisible(true);
			promoValidityPeriodDivIdDynamic.setVisible(false);
			staticRadio.setChecked(true);
			staticRadio.setVisible(false);
			/*dynamicRadio.setDisabled(true);
			dynamicValidityTbId.setDisabled(true);
			dynamicValidityLbId.setDisabled(true);*/
//			loyaltyHbxId.setVisible(true);
		}else  {
			singleSelCoupDivId.setVisible(false);
			multiSelCoupDivId.setVisible(true);
			promoValidityPeriodDivIdDynamic.setVisible(true);
			staticRadio.setVisible(true);
			/*dynamicRadio.setDisabled(false);
			dynamicRadio.setDisabled(false);
			dynamicValidityTbId.setDisabled(false);
			dynamicValidityLbId.setDisabled(false);*/
//			loyaltyHbxId.setVisible(false);
		}
		
		boolean disableFalg =false;
		if(coupGenRadioGrId.getSelectedIndex() == 1) {
			disableFalg = true;
//			lpChkBxId.setChecked(false);
			onlylpChkBxId.setChecked(false);
			multiplePerReceiptChkBxId.setChecked(false);
			lpIBxId.setValue(null);
		}else {
			disableFalg =false;
		}
		
//		lpChkBxId.setDisabled(disableFalg);
		onlylpChkBxId.setDisabled(disableFalg);
		multiplePerReceiptChkBxId.setDisabled(disableFalg);
		lpIBxId.setDisabled(disableFalg);
		
		
	} // onCheck$coupGenRadioGrId
	
	
	public void onCheck$redeemdsingCoupChkBxId() {
		if(redeemdsingCoupChkBxId.isChecked()) {
			redeemdlimitLongbxId.setDisabled(true);
		}else redeemdlimitLongbxId.setDisabled(false);
	} // onCheck$redeemdsingCoupChkBxId
	
	
	private void editCouponSetting (Coupons editCouponObj) {
		
		try {
			genCoupBtnId.setLabel("Save Discount Code");
			
			logger.debug("editing the Promo-code coupon Id "+editCouponObj.getCouponId());
			itemCategoryCkId.setChecked(false);
			deptCodeCkId.setChecked(false);
			vendorCodeCkId.setChecked(false);
			dcsCkId.setChecked(false);
			subclassCkId.setChecked(false);
			classCkId.setChecked(false);
			addedSKULBId.setSelectedIndex(-1);
			//Coupon Name 
			couponNameTxtBxId.setValue(editCouponObj.getCouponName().trim());
			couponNameTxtBxId.setDisabled(true);
			
			//Coupon desc
			coupDescTxtBxId.setValue(editCouponObj.getCouponDescription() != null?editCouponObj.getCouponDescription().trim():"");
			
			//barcode type
			boolean enableBarcode = editCouponObj.getEnableBarcode();
			logger.info("barcode enabled: "+enableBarcode);
			
			if(enableBarcode){
				
				bcenableCoupChkBxId.setChecked(enableBarcode);
				bcsizeCoupDivId.setVisible(true);
				
				List listItems = bctypeListboxId.getChildren();
				Listitem tempItem= null;
				for (Object object : listItems) {
					tempItem = (Listitem)object;
					if(tempItem.getValue().equals(editCouponObj.getBarcodeType())){
						bctypeListboxId.setSelectedItem(tempItem);
						bctypeListboxId.setStyle("margin: 0 2px 0 14px;");
						break;
					}
				}
				setDefaultDimensions(); 
					
					
				String bcDimension = editCouponObj.getBarcodeWidth().toString()+"X"+editCouponObj.getBarcodeHeight();
					
				List bcDimensionItemsList = bcDimensionListboxId2.getChildren();
				for(Object object : bcDimensionItemsList){
					Listitem bcdimItem = (Listitem)object;
					if(bcdimItem.getValue().equals(bcDimension)){
						logger.info("bcdimItem1.getvalue = "+bcdimItem.getValue());
						logger.info("bcdimension = "+bcDimension);
						bcDimensionListboxId2.setSelectedItem(bcdimItem);
					}
				}
					
				}

			//Check Radio Button 
			if(editCouponObj.getCouponGeneratedType().trim().equals(Constants.COUP_GENT_TYPE_SINGLE)) {
				
				coupGenRadioGrId.setSelectedIndex(0);
				multiSelCoupDivId.setVisible(false);
				promoValidityPeriodDivIdDynamic.setVisible(false);
				singleSelCoupDivId.setVisible(true);
				staticRadio.setChecked(true);
				staticRadio.setVisible(false);
				
				singCoupTxtBxId.setValue(editCouponObj.getCouponCode().trim());
				singCoupTxtBxId.setDisabled(true);
				
				if(editCouponObj.getAutoIncrCheck()) {
					singCoupChkBxId.setChecked(true);
					singlelimitLongbxId.setValue(null);
					singlelimitLongbxId.setDisabled(true);
				}else {
					singlelimitLongbxId.setValue(editCouponObj.getTotalQty());
				}
				
				//for Redeemed
				if(editCouponObj.getRedemedAutoChk()) {//redeemdlimitLongbxId,redeemdsingCoupChkBxId
					redeemdsingCoupChkBxId.setChecked(true);
					redeemdlimitLongbxId.setValue(null);
					redeemdlimitLongbxId.setDisabled(true);
				}else {
					redeemdlimitLongbxId.setValue(editCouponObj.getRedeemdCount());
				}
				
				//Set Per Subscription Use Limit
				if(editCouponObj.getSingPromoContUnlimitedRedmptChk() != null && editCouponObj.getSingPromoContUnlimitedRedmptChk()){
					unlimitSubChkBxId.setChecked(true);
					perSubUselimitLongbxId.setValue(null);
					perSubUselimitLongbxId.setDisabled(true);
				}else {
					perSubUselimitLongbxId.setValue(editCouponObj.getSingPromoContRedmptLimit());
				}
				
				//check and set the Loylty points
				if(editCouponObj.getLoyaltyPoints() != null) {
					
					onlylpChkBxId.setChecked(true);
					loyatyPntshboxId.setVisible(true);
				}
				//check and set the Allow multiple use per receipt
				if(editCouponObj.isStackable()){
					multiplePerReceiptChkBxId.setChecked(true);
				}
				//set Required Loylty Points
				lpIBxId.setValue(editCouponObj.getRequiredLoyltyPoits());
				
				//disable the next selected index
			}else {
				
				coupGenRadioGrId.setSelectedIndex(1);
				singleSelCoupDivId.setVisible(false);
				multiSelCoupDivId.setVisible(true);
				promoValidityPeriodDivIdDynamic.setVisible(true);
				staticRadio.setVisible(true);
				
				multiCodLimitLongBxId.setValue(editCouponObj.getTotalQty());
				multiCoupChkBxId.setChecked(editCouponObj.getAutoIncrCheck());
				
				//disable the next selected index
				
			}
			
			multRadioId.setDisabled(true);
			singRadioId.setDisabled(true);
			
			
			
//		TimeZone clientTimeZone =(TimeZone)Sessions.getCurrent().getAttribute("clientTimeZone");
			//Validity Period
			
			String type = editCouponObj.getExpiryType();
			
			if(type.equalsIgnoreCase(Constants.COUP_VALIDITY_PERIOD_DYNAMIC)){
				
			//	staticRadio.setChecked(false);
				dynamicRadio.setChecked(true);
				createDateBxId.setValue(null);
				expiryDateBxId.setValue(null);
				
				staticRadio.setDisabled(true);
				createDateBxId.setDisabled(true);
				expiryDateBxId.setDisabled(true);
				logger.info("---- inside dynamic promo----");
				
				String str = editCouponObj.getExpiryDetails();
				String[] strArr = str.split(Constants.ADDR_COL_DELIMETER);
				dynamicValidityTbId.setValue((strArr[1]== null)?"":strArr[1]);
				
				/*List<Listitem> listItemsList = (List)dynamicValidityLbId.getChildren();
				for(int i=0;i<listItemsList.size();i++){
					
					if(strArr[0].equals(listItemsList.get(i).getLabel())){
						dynamicValidityLbId.setSelectedIndex(i);
						break;
					}
				}*/
				if(strArr[0].equals(Constants.COUP_DYN_ISSUANCE)) dynamicValidityLbId.setSelectedIndex(0);
				else if(strArr[0].equals(Constants.COUP_DYN_BDAY)) dynamicValidityLbId.setSelectedIndex(1);
				else if(strArr[0].equals(Constants.COUP_DYN_ANNV)) dynamicValidityLbId.setSelectedIndex(2);
				
			}
			else if(type.equalsIgnoreCase(Constants.COUP_VALIDITY_PERIOD_STATIC)){
			
			staticRadio.setChecked(true);
//	dynamicRadio.setChecked(false);
			dynamicValidityTbId.setValue("");
			dynamicValidityLbId.setSelectedIndex(0);
			
			dynamicRadio.setDisabled(true);
			dynamicValidityTbId.setDisabled(true);
			dynamicValidityLbId.setDisabled(true);
			logger.info("---- inside static promo----");
			
			Calendar fromDateCal = editCouponObj.getCouponCreatedDate();
			logger.info("from date get Time is  :: "+fromDateCal.getTime() + "     createDateBxId.getValue::::: " + createDateBxId.getValue());
			createDateBxId.setValue(fromDateCal.getTime());
			logger.info("after setting in to datebox is  :: "+createDateBxId.getValue());
			
//		fromDateCal.setTimeZone(clientTimeZone);
//		String fromCalStr = MyCalendar.calendarToString(fromDateCal, MyCalendar.FORMAT_SB_DATEONLY);
			Calendar toDateCal = editCouponObj.getCouponExpiryDate();
//		toDateCal.setTimeZone(clientTimeZone);
			
			//createDateBxId.setConstraint("after "+fromCalStr);
			expiryDateBxId.setValue(toDateCal.getTime());
			}
			
			//Discount Criteria
			String disFirstCriteria = editCouponObj.getDiscountType().trim();
			
			if(disFirstCriteria.equals(DISC_TYPE_PERCENTAGE)) { // TODO Need to Change Hard code value
				percentRadioId.setChecked(true);
			}else {
				dollerRadioId.setChecked(true);
			}
			percentRadioId.setDisabled(true);
			dollerRadioId.setDisabled(true);
//			discountfirstListBxId.setDisabled(true);
			
			
			String disSecCriteria = editCouponObj.getDiscountCriteria().trim();
			if(disSecCriteria.equals(SELECT_SKU)) { // TODO Need to Change Hard code value
				storeSelectDivId.setVisible(true);
				mpvDivId.setVisible(false);
				skuVisibleDivId.setVisible(true);
				skuRadioId.setChecked(true);
				
				itemCategoryCkId.setChecked(true);
				deptCodeCkId.setChecked(true);
				vendorCodeCkId.setChecked(true);
				dcsCkId.setChecked(true);
				productDetailtaDivId.setVisible(true);
				subclassCkId.setChecked(true);
				classCkId.setChecked(true);
				if(dataflag) {
					adjustListBoxes();
					prepraAllAttributeData();
					}
				
				
			}else {
//			storeSelectDivId.setVisible(false);
				mpvDivId.setVisible(true);
				onCheck$mpvChkBoxId();
				skuVisibleDivId.setVisible(false);
				tpaRadioId.setChecked(true);
				itemCategoryCkId.setChecked(false);
				deptCodeCkId.setChecked(false);
				vendorCodeCkId.setChecked(false);
				dcsCkId.setChecked(false);
				productDetailtaDivId.setVisible(false);
				subclassCkId.setChecked(false);
				classCkId.setChecked(false);
//				discountSecListBxId.setSelectedIndex(0);
			}
			//Set Store Details
			logger.debug(">>>>>>>>>>>> editCouponObj is  "+editCouponObj.getAllStoreChk());
			if(editCouponObj.getAllStoreChk() !=null && editCouponObj.getAllStoreChk()){
				allStoresChkBxId.setChecked(true);
				
			}else {
				if(editCouponObj.getSelectedStores() != null) {
					String storeListStr = editCouponObj.getSelectedStores();
					String[] storesArr = storeListStr.split(";=;");
					Listitem tempItem = null;
					Listcell tempCell = null;
					
					for (String eachStore : storesArr) {
						
						tempItem = new Listitem();
						tempCell = new Listcell("Store Name "+ eachStore);
						tempCell.setParent(tempItem);
					 
					 
						tempCell = new Listcell();
						Image delImg = new Image("/img/action_delete.gif");
						delImg.setStyle("cursor:pointer;margin-right:10px;");
						delImg.setTooltiptext("Delete Store");
						delImg.setAttribute("COUPON_TYPE", "DELETE_STORE");
					 	delImg.addEventListener("onClick", this);
					 
					 	delImg.setParent(tempCell);
					 	tempCell.setParent(tempItem);
					 	tempItem.setParent(selectedStoreLbId);
					} // for
				}
			}
			
			onCheck$allStoresChkBxId();
			setheaders();
			coupDiscGenDaoForDML.deleteAllSkuByCouponId(editCouponObj.getCouponId());
			long totSize = coupDiscGenDao.findCountByCoupon(editCouponObj);
			//coupDiscGenDaoForDML.insertIntoSKUTempby(editCouponObj.getUserId(),editCouponObj.getCouponId());
			String value=null;
			Long startId=null;
			Long endId=null;
			Map<String ,String> map=null;
			Row row=null;
   			Label label=null;
   			List<SKUTemp> skuList=null;
          if(totSize > 1000){
        	  logger.info("list total is size>1000=="+totSize);
        	  Components.removeAllChildren(discountGenRowsId);
				long threshold=10000;
				long num_of_chunks=(totSize/threshold);
				if(totSize<threshold)
				{
					num_of_chunks=1;
				}
				else if((totSize%threshold)>0){
					num_of_chunks=(totSize/threshold)+1;
				}
				else
				{
					 num_of_chunks=(totSize/threshold);
				}
				logger.info("Records per chunk : "+threshold);
				long initialIndex=0l;	
				 map=new LinkedHashMap<>();
				for(int loop_var=1;loop_var<=num_of_chunks;loop_var++)
				{
					skuList = coupDiscGenDao.findBySKUTemp(editCouponObj.getCouponId(), initialIndex, threshold);
			
			if(startId==null)
				startId=skuList.get(0).getSkuTempId();
				endId=skuList.get(skuList.size()-1).getSkuTempId();
			if(skuRadioId.isChecked()){
			for(SKUTemp sku:skuList){
					if(map.containsKey(sku.getSkuAttribute()+"::"+sku.getDiscount())){
						value=(map.get(sku.getSkuAttribute()+"::"+sku.getDiscount()));
						if(value!=null && value.length()<=100 &&sku.getSkuValue()!=null)
						map.put(sku.getSkuAttribute()+"::"+sku.getDiscount(),value.concat(", ")+sku.getSkuValue());
				    	}
					else{
						map.put(sku.getSkuAttribute()+"::"+sku.getDiscount(), sku.getSkuValue()!=null?sku.getSkuValue():"");
				
				    }
			     }
			   }
			    initialIndex =(loop_var*threshold)+1;
			  }
				start=startId;
				 end=endId;
				 limit=start+"::"+end;
				for(Entry<String, String> entry : map.entrySet()){
					row=new Row();
					row.setValue(startId+"::"+endId);
					label=new Label(entry.getKey().split("::")[1]);
					label.setParent(row);
					
					String typeCode=entry.getKey().split("::")[0];
					typeCode=typeCode.contains("Vendor")?"Vendor Code":typeCode.contains("Department")?"Department Code":typeCode.contains("Subclass")?"Sub-Class":typeCode;
					label=new Label(typeCode);
					label.setAttribute("Type",entry.getKey().split("::")[0]);
					label.setParent(row);
					
					label=new Label(map.get(entry.getKey()));
					label.setParent(row);
					
					Double discount=Double.parseDouble(entry.getKey().split("::")[1]);
					List<Long> templist=coupDiscGenDao.findBySkuUser(users.getUserId(),discount,entry.getKey().split("::")[0],null,null);
					label=new Label(templist.get(0).toString());
					label.setParent(row);
					
					Image delImg =  new Image("img/delt_icn.png");
					delImg.setStyle("cursor:pointer;");
					delImg.setAttribute("COUPON_TYPE", COUPON_DISCOUNT_DELETE);
					delImg.addEventListener("onClick", this);
					delImg.setParent(row);
					
					row.setParent(discountGenRowsId);
				    }
          }
           else {
        	   logger.info("list size is less then 100"+totSize);
        	   skuList = coupDiscGenDao.findBySKUTemp(editCouponObj.getCouponId());
        	   if(startId==null)
   				startId=skuList.get(0).getSkuTempId();
   				endId=skuList.get(skuList.size()-1).getSkuTempId();
        	   Components.removeAllChildren(discountGenRowsId);
   			map=new LinkedHashMap<>();
   			if(skuRadioId.isChecked()){
   				for(SKUTemp sku:skuList){
					if(map.containsKey(sku.getSkuAttribute()+"::"+sku.getDiscount())){
						value=(map.get(sku.getSkuAttribute()+"::"+sku.getDiscount()));
						if(value!=null && value.length()<=100 &&sku.getSkuValue()!=null)
						map.put(sku.getSkuAttribute()+"::"+sku.getDiscount(),value.concat(", ")+sku.getSkuValue());
				    	}
					else{
						map.put(sku.getSkuAttribute()+"::"+sku.getDiscount(), sku.getSkuValue()!=null?sku.getSkuValue():"");
				
				    }
			     }
   			 start=startId;
   			 end=endId;
   			limit=start+"::"+end;
   			for(Entry<String, String> entry : map.entrySet()){
   				row=new Row();
   				row.setValue(startId+"::"+endId);
   				label=new Label(entry.getKey().split("::")[1]);
   				label.setParent(row);
   				
   				String typeCode=entry.getKey().split("::")[0];
   				typeCode=typeCode.contains("Vendor")?"Vendor Code":typeCode.contains("Department")?"Department Code":typeCode.contains("Subclass")?"Sub-Class":typeCode;
   				label=new Label(typeCode);
   				label.setAttribute("Type",entry.getKey().split("::")[0]);
   				label.setParent(row);
   				
   				label=new Label(map.get(entry.getKey()));
   				label.setParent(row);
   				
   				Double discount=Double.parseDouble(entry.getKey().split("::")[1]);
				List<Long> templist=coupDiscGenDao.findBySkuUser(users.getUserId(),discount,entry.getKey().split("::")[0],null,null);
				label=new Label(templist.get(0).toString());
				label.setParent(row);
   				
   				Image delImg =  new Image("img/delt_icn.png");
   				delImg.setStyle("cursor:pointer;");
   				delImg.setAttribute("COUPON_TYPE", COUPON_DISCOUNT_DELETE);
   				delImg.addEventListener("onClick", this);
   				delImg.setParent(row);
   				
   				row.setParent(discountGenRowsId);
   			    }
   			}
   			
   			else{
   			  skuList = coupDiscGenDao.findBySKUTemp(editCouponObj.getCouponId());
   				for(SKUTemp sku:skuList){
   				//coupDiscGenDaoForDML.saveByCollection(skuList);
   				if(start==null)
   					start=skuList.get(0).getSkuTempId();
   				end=skuList.get(skuList.size()-1).getSkuTempId();
   				limit=start+"::"+end;
   				 row=new Row();
   				row.setValue(start+"::"+end);
   				
   				 label=new Label(sku.getDiscount().toString());
   				label.setParent(row);
   				
   				label=new Label(sku.getTotPurchaseAmount()!=0?sku.getTotPurchaseAmount().toString():"N/A");
   				label.setParent(row);
   				
   				label=new Label("");
   				label.setParent(row);
   				
   				label=new Label("");
				label.setParent(row);
   				
   				Image delImg =  new Image("img/delt_icn.png");
   				delImg.setStyle("cursor:pointer;");
   				delImg.setAttribute("COUPON_TYPE", COUPON_DISCOUNT_DELETE);
   				delImg.addEventListener("onClick", this);
   				delImg.setParent(row);
   				
   				row.setParent(discountGenRowsId);
   			   }
   			} 
           }
			//TODO set the Grid Headers based on selection 
			
			enableDisableDiscountCrtiteriaOptions();
			onCheck$coupGenRadioGrId();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::",e);
		}
				
	} // editCouponSetting
	
	
	
	
	public void onCheck$discountRadioGrId() {
		Radio selRadio = discountRadioGrId.getSelectedItem();
		if(selRadio==null) return;
		
		if(selRadio==skuRadioId) {
			mpvDivId.setVisible(false);
			skuVisibleDivId.setVisible(true);
			//skuFilterGBxId.setVisible(true);
//			storeSelectDivId.setVisible(true);
			itemCategoryCkId.setChecked(true);
			deptCodeCkId.setChecked(true);
			vendorCodeCkId.setChecked(true);
			dcsCkId.setChecked(true);
			subclassCkId.setChecked(true);
			classCkId.setChecked(true);
			productDetailtaDivId.setVisible(true);
			if(dataflag) {
				adjustListBoxes();
				prepraAllAttributeData();
				}
		}
		else if(selRadio==tpaRadioId) {
			mpvDivId.setVisible(true);
			skuVisibleDivId.setVisible(false);
			//skuFilterGBxId.setVisible(false);
//			storeSelectDivId.setVisible(false);
			itemCategoryCkId.setChecked(false);
			deptCodeCkId.setChecked(false);
			vendorCodeCkId.setChecked(false);
			dcsCkId.setChecked(false);
			subclassCkId.setChecked(false);
			classCkId.setChecked(false);
			productDetailtaDivId.setVisible(false);
		} 
		//set the geader Fileds On Grid
		setheaders();
	}
	

	
	
	public void onClick$genCoupBtnId() {
		
		
		//Validation
		if(isVaildCoupon(tempCouponObj) == false) {
		 return;
		}
		
		try {
			int confirm = Messagebox.show("Are you sure you want to save the generated Discount Code?",
					"Save Discount Code Generation  ", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
			if(confirm != Messagebox.OK) return ;
		} catch (Exception e) {
			logger.error("Exception ::", e);
			return ;
		}
		
		//Saving Coupon Obj
		if(tempCouponObj == null) {
			
			tempCouponObj = saveNewCoupObj();
			
			if(tempCouponObj == null) return;
			
		 MessageUtil.setMessage("New Discount Code saved successfully.","color:green","TOP");
		
		}else {
			
			saveEditCoupObj(tempCouponObj);
			Sessions.getCurrent().removeAttribute("EDIT_COUPON_OBJ");
		} // else 
		
		if(!tempCouponObj.getStatus().equals(Constants.COUP_STATUS_EXPIRED)){
			
			CouponCodesUtility ccu = new CouponCodesUtility(tempCouponObj);
			ccu.start();
		}

		
		/*totalCount = couponsDao.findTotCountCoupons(users.getUserId());
		couponsPagingId.setTotalSize(totalCount);
		fillCouponsBySize(0,5);
		couponsPagingId.setActivePage(0);*/
		manageCouponsTabBoxId.setSelectedIndex(0);
		getAllPromoFromDB();
		registerEventListner(0, 5);
		Utility.refreshGridModel(viewCouponsELObj, 0, true);
		onSelect$manageCouponsTabBoxId();
		
	 } //onclick$genCoupBtnId
	
//	private static String regexStr = "[\\s,\\n;\\t]+";   // "[\\s,\\n\\t]+";
	
	public void onChange$expiryDateBxId() {
		
		if(createDateBxId.getValue() == null || expiryDateBxId.getValue()  == null) {
			MessageUtil.setMessage("Please provide valid details.","color:red","TOP");
			return ;
		}
		
		Date  startDate = createDateBxId.getValue();
		Calendar start = Calendar.getInstance();
		start.setTime(startDate);
		start = setDefaultTimeforCal(start, true);
		
		Date  endDate = expiryDateBxId.getValue();
		Calendar end = Calendar.getInstance();
		end.setTime(endDate);
		end = setDefaultTimeforCal(end, false);
		
		if(end.before(start)) {
			MessageUtil.setMessage("'From' date cannot be after 'To' date.", "red");
			return;
		}
		
	} // onSelect$expiryDateBxId
	
	
	private boolean isVaildCoupon(Coupons editCouponObj) {

		 
		 if(editCouponObj == null) {
			 String coupName= couponNameTxtBxId.getValue().trim();
			 
			 if(coupName.length()==0 || coupName.contains("_") || coupName.contains(" ")) {
				 MessageUtil.setMessage("Please provide valid Discount code name.","color:red","TOP");
				 return false;
			 }else if(validateCouponName4SpecialChars(coupName) == false){
				 MessageUtil.setMessage("Please provide valid Discount code name which can include alphanumeric characters and special characters as * and -.","color:red","TOP");
				 return false;
			 }
			 else {
				 //Check the Coupon Name already exists
				 boolean isExistsCoup = couponsDao.checkCoupByName(coupName, orgId); 
				 if(isExistsCoup == true ) {
					 MessageUtil.setMessage("Discount Code name already exists.","color:red","TOP");
					 return false;
				 }
			 }
			 
		 }
		 
		
		//Coupon Code Generation Radio validations
		if(coupGenRadioGrId.getSelectedIndex() == 0) {
			
			singCoupTxtBxId.setStyle(NORMAL_STYLE);
			
			if(singCoupTxtBxId.getValue().trim().length() == 0 || singCoupTxtBxId.getValue().equals("Enter Code")) {
				singCoupTxtBxId.setStyle(ERROR_STYLE);
				MessageUtil.setMessage("Please provide Discount code value.","color:red","TOP");
				return false;
			}else if(singCoupTxtBxId.getValue().trim().equals(Constants.PROMO_OC_LOYALTY)){
				singCoupTxtBxId.setStyle(ERROR_STYLE);
				MessageUtil.setMessage("Please provide another Discount code value.","color:red","TOP");
				return false;
			}
			
			/*Pattern p = Pattern.compile("[a-zA-Z0-9-]+");
			Matcher m = p.matcher(singCoupTxtBxId.getValue().trim());
			if(!m.matches()){
				MessageUtil.setMessage("Please provide valid single Promo-code value.\nSpecial characters except (-) are not allowed.","color:red","TOP");
				return false;
			}*/
			
			if(validateCouponName4SpecialChars(singCoupTxtBxId.getValue().trim()) == false){
				MessageUtil.setMessage("Please provide valid single Discount code value, which can include alphanumeric characters and special characters as * and -.","color:red","TOP");
				return false;
			}
			
			singlelimitLongbxId.setStyle(NORMAL_STYLE);
			//if(singCoupChkBxId.isChecked() == false && singlelimitLongbxId.getValue() == null) {
			if(singCoupChkBxId.isChecked() == false && singlelimitLongbxId.getValue() == null ) {
				
				singlelimitLongbxId.setStyle(ERROR_STYLE);
				MessageUtil.setMessage("Please provide Discount code limit value.","color:red","TOP");
				return false;
			}
			else{
				
				if(!singCoupChkBxId.isChecked() && singlelimitLongbxId.getValue() !=null){
				int limit = Integer.parseInt(singlelimitLongbxId.getValue().toString().trim());
				
				if(limit <= 0){
					MessageUtil.setMessage("Please provide valid Discount code limit value.","color:red","TOP");
					singlelimitLongbxId.setStyle(ERROR_STYLE);
					return false;
				}
				}
			}
			
			
			//Check the Coupon code if  exist
//			long coupId = 0;
			singCoupTxtBxId.setStyle(NORMAL_STYLE);
			if(editCouponObj == null) {
//				coupId = editCouponObj.getCouponId();
				boolean existCoupCode = couponsDao.isExistCoupCodeBasedOnOrgId(singCoupTxtBxId.getValue().trim(),orgId);
				if(existCoupCode == true ) {
					singCoupTxtBxId.setStyle(ERROR_STYLE);
					MessageUtil.setMessage(" This Discount code is already in use. " +
							"Please provide another Discount code.","color:red","TOP");
					return false;
				}
			}
			//logger.info(">>>>>>+isExistsCoup"+existCoupCode);
			
			
			
			
			//for Redeemd count
			redeemdlimitLongbxId.setStyle(NORMAL_STYLE);
			if(redeemdsingCoupChkBxId.isChecked() == false && redeemdlimitLongbxId.getValue() == null) {
				
				redeemdlimitLongbxId.setStyle(ERROR_STYLE);
				MessageUtil.setMessage("Please provide redemption limit value.","color:red","TOP");
				return false;
			}
			else{
				
				if(redeemdlimitLongbxId.getValue() !=null){
				int limit = Integer.parseInt(redeemdlimitLongbxId.getValue().toString().trim());
				
				if(limit < 0){
					MessageUtil.setMessage("Please provide valid Discount code redeemed value.","color:red","TOP");
					redeemdlimitLongbxId.setStyle(ERROR_STYLE);
					return false;
				}
				}
			}
			
			perSubUselimitLongbxId.setStyle(NORMAL_STYLE);
			if(perSubUselimitLongbxId.getValue() == null && !unlimitSubChkBxId.isChecked()) {
				perSubUselimitLongbxId.setStyle(ERROR_STYLE);
				MessageUtil.setMessage("Please provide per subscription Discount code limitation.","color:red","TOP");
				return false;
			}
			else{
				
				if(perSubUselimitLongbxId.getValue() != null){
				int limit = Integer.parseInt(perSubUselimitLongbxId.getValue().toString().trim());
				
				if(limit < 0){
					MessageUtil.setMessage("Please provide valid Discount code per user subscription limit value.","color:red","TOP");
					perSubUselimitLongbxId.setStyle(ERROR_STYLE);
					return false;
				}
				}
			}
			
			
			
			//TODO for LIMIT credentials
			if(editCouponObj != null ) {
				
				if(singCoupChkBxId.isChecked() == false && editCouponObj.getAutoIncrCheck() == false && singlelimitLongbxId.getValue()!= null && singlelimitLongbxId.getValue() > 0 &&
						singlelimitLongbxId.getValue() < editCouponObj.getTotalQty()) {
					MessageUtil.setMessage("Please provide Discount codes limit that is more than or equal to the previous value.","color:red","TOP");
					return false;
				}else if(singCoupChkBxId.isChecked() == false && 
						editCouponObj.getAutoIncrCheck() == true && singlelimitLongbxId.getValue()!= null &&
                        editCouponObj.getIssued()!= null &&
						singlelimitLongbxId.getValue() < editCouponObj.getIssued()){
					MessageUtil.setMessage("Please provide Discount codes issue limit that is more than or equal to the previous value. "+editCouponObj.getIssued(),"color:red","TOP");
					return false;
				}
				
				
				if(redeemdsingCoupChkBxId.isChecked() == false && editCouponObj.getRedemedAutoChk()== false &&
						redeemdlimitLongbxId.getValue()!= null &&
						redeemdlimitLongbxId.getValue() < editCouponObj.getRedeemdCount()) {
					MessageUtil.setMessage("Please provide Discount code limit that is more than or equal to the previous value.","color:red","TOP");
					return false;
				}else if(redeemdsingCoupChkBxId.isChecked() == false && editCouponObj.getRedemedAutoChk()== true &&
						redeemdlimitLongbxId.getValue()!= null &&
								editCouponObj.getRedeemed()!= null &&
						redeemdlimitLongbxId.getValue() < editCouponObj.getRedeemed()){
					MessageUtil.setMessage("Please provide Discount codes issue limit that is more than or equal to the previous value."+editCouponObj.getRedeemed(),"color:red","TOP");
					return false;
				}
			}
			
			//Redeemed With
			lpIBxId.setStyle(NORMAL_STYLE);
			if( onlylpChkBxId.isChecked() && lpIBxId.getValue() == null ){
				MessageUtil.setMessage("Please provide loyalty points.","color:red","TOP");
				lpIBxId.setStyle(ERROR_STYLE);
				return false;
			}
			else{
				if(lpIBxId.getValue() != null){
				int checkValue = Integer.parseInt( lpIBxId.getValue().toString().trim());
				if(checkValue < 0){
				MessageUtil.setMessage("Please provide valid loyalty points.","color:red","TOP");
				lpIBxId.setStyle(ERROR_STYLE);
				return false;
				}
				}
			}
			
			
			
			
			
		}else {
			multiCodLimitLongBxId.setStyle(NORMAL_STYLE);
			
			if(multiCodLimitLongBxId.getValue() == null) {
				multiCodLimitLongBxId.setStyle(ERROR_STYLE);
				MessageUtil.setMessage("Please enter value for number of Discount codes to be generated.","color:red","TOP");
				return false;
			}
			int limit=Integer.parseInt(multiCodLimitLongBxId.getValue().toString().trim());
			if(limit <= 0){
				MessageUtil.setMessage("Please provide valid Discount code limit value.","color:red","TOP");
				multiCodLimitLongBxId.setStyle(ERROR_STYLE);
				return false;
			}
			
			//TODO for LIMIT credentials
			if(editCouponObj != null ) {
				
				if(multiCoupChkBxId.isChecked() == false && 
						multiCodLimitLongBxId.getValue() < editCouponObj.getTotalQty()) {
					MessageUtil.setMessage("Number of Discount codes generated shouldn't be less than the earlier value.","color:red","TOP");
					return false;
				}
			}
			
			
		}
		
		//check Store numbers
		if(!allStoresChkBxId.isChecked() && selectedStoreLbId.getItemCount() == 0) {
			MessageUtil.setMessage("Please provide stores.","color:red","TOP");
			return false;
		}
		if(!allStoresChkBxId.isChecked()) {
			List<Listitem> storeItemsLst = selectedStoreLbId.getItems();
			for (Listitem eachLI : storeItemsLst) {
				String storeStr = ((Listcell)eachLI.getFirstChild()).getLabel();
				storeStr = storeStr.replace("Store Name", "");
				if(storeStr == null || storeStr.trim().length() == 0) {
					MessageUtil.setMessage("Empty store provided.","color:red","TOP");
					return false;
				}
				
				//storeNumStr = storeNumStr == null ?  storeStr.trim() : storeNumStr+";=;"+storeStr.trim();
			}
		}
		
		if(discountGenRowsId.getChildren().size() == 0) {
			MessageUtil.setMessage("Please provide valid discount value.","color:red","TOP");
			return false;
		}
		
		
		
		//Validity Period
		if(promoRgId.getSelectedIndex() == 1){
			if(dynamicValidityTbId.getValue().isEmpty() || dynamicValidityTbId.getValue() == null){
				dynamicValidityTbId.setStyle(ERROR_STYLE);
				MessageUtil.setMessage("Please provide value for number of days.","color:red","TOP");
				return false;
			}
			
			int val = Integer.parseInt(dynamicValidityTbId.getValue());
			logger.info("===Value fro number of days is ===="+ val);
			if(val < 1){
				logger.info("===inside condition====");
				dynamicValidityTbId.setStyle(ERROR_STYLE);
				MessageUtil.setMessage("Please provide value greater than 1 for number of days.","color:red","TOP");
				return false;
			}
		}
		else if(promoRgId.getSelectedIndex() == 0){
		if(createDateBxId.getValue() == null || expiryDateBxId.getValue()  == null) {
			MessageUtil.setMessage("Please provide valid details.","color:red","TOP");
			return false;
		}
		
		Date startDate = createDateBxId.getValue();
		Calendar  start = Calendar.getInstance();
		start.setTime(startDate);
		
		Date endDate = expiryDateBxId.getValue();
		Calendar end = Calendar.getInstance();
		end.setTime(endDate);
		start = setDefaultTimeforCal(start, true);
		end = setDefaultTimeforCal(end, false);
		
		if(end.before(start)) {
			MessageUtil.setMessage("'From' date cannot be after 'To' date.", "red");
			return false;
		}
		
		}
//		if(promofromDatevalidation() == false) return false;
			
		return true;
	 } //isVaildCoupon
	 
	
	
	
	private Coupons saveNewCoupObj() {	
		
		String couponStatus= "";
		Calendar tempCal = null;
		Calendar expCal1 = null;
		
		if(promoRgId.getSelectedIndex() == 1){
			
			/*if(dynamicValidityLbId.getSelectedIndex() == 0){
				
				expCal1.add(Calendar.DATE, Integer.parseInt(dynamicValidityTbId.getValue()));
				expCal1  = setDefaultTimeforCal(expCal1, true);
				Calendar curCal =Calendar.getInstance();
				
				if(curCal.after(expCal1)) {
					 couponStatus = Constants.COUP_STATUS_EXPIRED; 
				 }else {
					 if(tempCal!= null && tempCal.compareTo(curCal) > 0) {
						 couponStatus= Constants.CAMP_STATUS_ACTIVE; 
						 
					 }else {
						 couponStatus= Constants.CAMP_STATUS_RUNNING;
					 }
				 }
				couponStatus= Constants.CAMP_STATUS_RUNNING;
			}
			else if(dynamicValidityLbId.getSelectedIndex() == 1){
				
				expCal1.add(Calendar.DATE, Integer.parseInt(dynamicValidityTbId.getValue()));
				expCal1  = setDefaultTimeforCal(expCal1, true);
				Calendar curCal =Calendar.getInstance();
				
				if(curCal.after(expCal1)) {
					 couponStatus = Constants.COUP_STATUS_EXPIRED; 
				 }else {
					 if(tempCal!= null && tempCal.compareTo(curCal) > 0) {
						 couponStatus= Constants.CAMP_STATUS_ACTIVE; 
						 
					 }else {
						 couponStatus= Constants.CAMP_STATUS_RUNNING;
					 }
				 }
				couponStatus= Constants.CAMP_STATUS_RUNNING;
			}
			else if(dynamicValidityLbId.getSelectedIndex() == 2){
				
				expCal1.add(Calendar.DATE, Integer.parseInt(dynamicValidityTbId.getValue()));
				expCal1  = setDefaultTimeforCal(expCal1, true);
				Calendar curCal =Calendar.getInstance();
				
				if(curCal.after(expCal1)) {
					 couponStatus = Constants.COUP_STATUS_EXPIRED; 
				 }else {
					 if(tempCal!= null && tempCal.compareTo(curCal) > 0) {
						 couponStatus= Constants.CAMP_STATUS_ACTIVE; 
						 
					 }else {
						 couponStatus= Constants.CAMP_STATUS_RUNNING;
					 }
				 }
				couponStatus= Constants.CAMP_STATUS_RUNNING;
			}*/
			couponStatus= Constants.CAMP_STATUS_RUNNING;
			
		}
		else if(promoRgId.getSelectedIndex() == 0){
			
			tempCal = Calendar.getInstance();
			expCal1 = Calendar.getInstance();
			
		 Date startDate = createDateBxId.getValue();
		 tempCal.setTime(startDate);
		 logger.debug("From date server value is  :: "+tempCal.getTime());
		 
		 
		 tempCal  = setDefaultTimeforCal(tempCal, true);
		 Calendar curCal =Calendar.getInstance();
		 
		 
		 Date endDate = expiryDateBxId.getValue();
		 Calendar expCal = Calendar.getInstance();
		 expCal.setTime(endDate);
		 expCal = setDefaultTimeforCal(expCal, false);
		 
//		 logger.info(" ... time is .."+curCal.after(expCal));
		 if(curCal.after(expCal)) {
			 couponStatus = Constants.COUP_STATUS_EXPIRED; 
		 }else {
			 if(tempCal!= null && tempCal.compareTo(curCal) > 0) {
				 couponStatus= Constants.CAMP_STATUS_ACTIVE; 
				 
			 }else {
				 couponStatus= Constants.CAMP_STATUS_RUNNING;
			 }
		 }
		 
		 Date date1 = expiryDateBxId.getValue();
		 expCal1.setTime(date1);
		 expCal1 = setDefaultTimeforCal(expCal1, false);
		 
		}
		 
		 String couponGeneratedTypeStr = "";
		 String couponCodeStr = null;
		 long totalQtyLng = 0;
		 long redemedLong = 0;
		 boolean autoCheckFlag = false;
		 boolean redmdAutChkFlag = false;
		 
		 
		 
		 if(coupGenRadioGrId.getSelectedIndex() == 0) {
			 couponGeneratedTypeStr = Constants.COUP_GENT_TYPE_SINGLE; 
			 couponCodeStr = singCoupTxtBxId.getValue().trim();
			 autoCheckFlag = singCoupChkBxId.isChecked();
			 
			 if(!autoCheckFlag) {
				 
				 totalQtyLng = singlelimitLongbxId.getValue();
			 }
			 redmdAutChkFlag = redeemdsingCoupChkBxId.isChecked();
			 if(!redmdAutChkFlag) {
				 redemedLong = redeemdlimitLongbxId.getValue();
			 }
		 }else {
			 couponGeneratedTypeStr = Constants.COUP_GENT_TYPE_MULTIPLE;
			 totalQtyLng = multiCodLimitLongBxId.getValue();
			 autoCheckFlag = multiCoupChkBxId.isChecked();
			 redmdAutChkFlag= false;
		 }
		 
		 /*Date date1 = expiryDateBxId.getValue();
		 Calendar expCal1 = Calendar.getInstance();
		 expCal1.setTime(date1);
		 expCal1 = setDefaultTimeforCal(expCal1, false);*/
		 String discTypeStr = "";
			 
			if(percentRadioId.isChecked()) discTypeStr = DISC_TYPE_PERCENTAGE ; else  discTypeStr = DISC_TYPE_VALUE;
		 
			//New Coupon Obj
			Coupons couponObj = new Coupons(couponNameTxtBxId.getValue().trim(), coupDescTxtBxId.getValue().trim(), couponStatus, couponGeneratedTypeStr, couponCodeStr,
											discTypeStr, discountRadioGrId.getSelectedItem().getValue().toString(), totalQtyLng, users.getUserId(), Calendar.getInstance(), 
											tempCal,Calendar.getInstance(), expCal1,autoCheckFlag,redemedLong,redmdAutChkFlag);
			
			
			
			
			//set Redeemed With
			//perSubUselimitLongbxId
			//
			if(coupGenRadioGrId.getSelectedIndex() == 0) {
				if(onlylpChkBxId.isChecked()){
					
					couponObj.setLoyaltyPoints((byte)1);
					couponObj.setRequiredLoyltyPoits(lpIBxId.getValue());
				}
				
				//check and set the Allow multiple use per receipt
				couponObj.setStackable(multiplePerReceiptChkBxId.isChecked());
				
				
				
				// set Item per Subscrition
				if(unlimitSubChkBxId.isChecked()){
					couponObj.setSingPromoContUnlimitedRedmptChk(unlimitSubChkBxId.isChecked());
				}else {
					couponObj.setSingPromoContRedmptLimit(perSubUselimitLongbxId.getValue());
				}
			}
			
			//set Store
			if(allStoresChkBxId.isChecked()) couponObj.setAllStoreChk(allStoresChkBxId.isChecked());
			else {
				List<Listitem> storeItemsLst = selectedStoreLbId.getItems();
				String storeNumStr = null;
				for (Listitem eachLI : storeItemsLst) {
					String storeStr = ((Listcell)eachLI.getFirstChild()).getLabel();
					storeStr = storeStr.replace("Store Name", "");
					if(storeStr != null || storeStr.trim().length() > 0){
						
						storeNumStr = storeNumStr == null ?  storeStr.trim() : storeNumStr+";=;"+storeStr.trim();
					}
				}
				couponObj.setSelectedStores(storeNumStr);
			}
			
			
			couponObj.setOrgId(orgId);
			
			//barcode type
			 boolean enableBarcode = false;
			 
			 if(bcenableCoupChkBxId.isChecked()){
				String barcodeType =  bctypeListboxId.getSelectedItem().getValue();
				
				Long barcodeWidth = null;
				Long barcodeHeight = null;
				String bcdim = bcDimensionListboxId2.getSelectedItem().getValue();
				String bcdimArray[] = bcdim.split("X");
				barcodeWidth = Long.parseLong(bcdimArray[0].trim());
				barcodeHeight = Long.parseLong(bcdimArray[1].trim());
				//Long barcodeWidth = bcwidthLongbxId.getValue();
				//Long barcodeHeight = bcheightLongbxId.getValue();
				
				enableBarcode = true;
				couponObj.setBarcodeType(barcodeType);
				couponObj.setBarcodeWidth(barcodeWidth);
				couponObj.setBarcodeHeight(barcodeHeight);
			 }
			 
			 couponObj.setEnableBarcode(enableBarcode);
			
			 if(promoRgId.getSelectedIndex() == 1){
				 couponObj.setExpiryType(Constants.COUP_VALIDITY_PERIOD_DYNAMIC);
				 
				 if("Date of issue of discount code".equals(dynamicValidityLbId.getSelectedItem().getLabel())){
					 
					 couponObj.setExpiryDetails(Constants.COUP_DYN_ISSUANCE + Constants.ADDR_COL_DELIMETER + dynamicValidityTbId.getValue().toString());
				 }
				 
				 else if("Birthday".equals(dynamicValidityLbId.getSelectedItem().getLabel())){
					 
					 couponObj.setExpiryDetails(Constants.COUP_DYN_BDAY + Constants.ADDR_COL_DELIMETER + dynamicValidityTbId.getValue().toString());
				 }
				 
				 else if("Anniversary".equals(dynamicValidityLbId.getSelectedItem().getLabel())){
					 
					 couponObj.setExpiryDetails(Constants.COUP_DYN_ANNV + Constants.ADDR_COL_DELIMETER + dynamicValidityTbId.getValue().toString());
				 }
				 
				 couponObj.setStatus(Constants.COUP_STATUS_RUNNING);
		//		 couponObj.setExpiryDetails(dynamicValidityLbId.getSelectedItem().getLabel() + Constants.ADDR_COL_DELIMETER + dynamicValidityTbId.getValue().toString());
			 }
			 else if(promoRgId.getSelectedIndex() == 0){
				 couponObj.setExpiryType(Constants.COUP_VALIDITY_PERIOD_STATIC);
			//	 couponObj.setStatus(Constants.COUP_STATUS_RUNNING);
			 }
			 couponsDaoForDML.saveOrUpdate(couponObj);
			 logger.info("couponObj.getCouponId()   "+couponObj.getCouponId());
			//coupDiscGenDaoForDML.insertNewIntoCouponDiscountby(couponObj.getUserId(), couponObj.getCouponId());
			coupDiscGenDaoForDML.deleteAllSkuBy(couponObj.getUserId(), null);
			return couponObj;
	 } //saveNewCoupObj
	
	
	
	 private void saveEditCoupObj(Coupons editCouponObj) {
		 	boolean isModified = false;
			
		 	
		 	
			//check and set the Discription
			if(!editCouponObj.getCouponDescription().equals(coupDescTxtBxId.getValue().trim())) {
				isModified = true;
				editCouponObj.setCouponDescription(coupDescTxtBxId.getValue().trim());
			}
			
			
			//barcode type
			 boolean enableBarcode = false;
			 if(bcenableCoupChkBxId.isChecked() != editCouponObj.getEnableBarcode()){
				 isModified = true;
			 }
			 
//			 String bcCoupDim = editCouponObj.getBarcodeWidth()+"X"+editCouponObj.getBarcodeHeight();
			 if(bcenableCoupChkBxId.isChecked()){
				String barcodeType =  bctypeListboxId.getSelectedItem().getValue();
				
				Long barcodeWidth = null;
				Long barcodeHeight = null;
				
			
				String bcdim1 = bcDimensionListboxId2.getSelectedItem().getValue();
				if(!bcdim1.equals(barcodeWidth+"X"+barcodeHeight)){
					
					String bcdimArray1[] = bcdim1.split("X");
					barcodeWidth = Long.parseLong(bcdimArray1[0].trim());
					barcodeHeight = Long.parseLong(bcdimArray1[1].trim());
					isModified = true;
				}
				
				enableBarcode = true;
				editCouponObj.setBarcodeType(barcodeType);
				editCouponObj.setBarcodeWidth(barcodeWidth);
				editCouponObj.setBarcodeHeight(barcodeHeight);
			 }
			 
			 editCouponObj.setEnableBarcode(enableBarcode);
			 
			 logger.info("edit coupon object;;;;"+editCouponObj.getEnableBarcode());
			
			//Coupon-code generation
			if(editCouponObj.getCouponGeneratedType().trim().equals(Constants.COUP_GENT_TYPE_SINGLE) && 
																coupGenRadioGrId.getSelectedIndex() == 0) {
				
				if(!editCouponObj.getCouponCode().trim().equals(singCoupTxtBxId.getValue().trim())) {
					
					editCouponObj.setCouponCode(singCoupTxtBxId.getValue().trim());
					isModified = true;
				}
			
				
				if(editCouponObj.getAutoIncrCheck() == false && singCoupChkBxId.isChecked() == true ) {
					editCouponObj.setTotalQty(new Long(0));
					editCouponObj.setAutoIncrCheck(singCoupChkBxId.isChecked());
					isModified = true;
				}else if(editCouponObj.getAutoIncrCheck() == true && singCoupChkBxId.isChecked() == false ) {
					
					editCouponObj.setTotalQty(singlelimitLongbxId.getValue());
					editCouponObj.setAutoIncrCheck(singCoupChkBxId.isChecked());
					isModified = true;
					
				}else if(editCouponObj.getAutoIncrCheck() == false && singCoupChkBxId.isChecked() == false ) {
					
					if(!editCouponObj.getTotalQty().equals(singlelimitLongbxId.getValue())) {
						editCouponObj.setTotalQty(singlelimitLongbxId.getValue());
						isModified = true;
					}
				}
					
				if(editCouponObj.getRedemedAutoChk() == false && redeemdsingCoupChkBxId.isChecked() == true ) {
					editCouponObj.setRedeemdCount(new Long(0));
					editCouponObj.setRedemedAutoChk(redeemdsingCoupChkBxId.isChecked());
					isModified = true;
				}else if(editCouponObj.getRedemedAutoChk() == true && redeemdsingCoupChkBxId.isChecked() == false ) {
					
					editCouponObj.setRedeemdCount(redeemdlimitLongbxId.getValue());
					editCouponObj.setRedemedAutoChk(redeemdsingCoupChkBxId.isChecked());
					isModified = true;
					
				}else if(editCouponObj.getRedemedAutoChk() == false && redeemdsingCoupChkBxId.isChecked() == false ) {
					
					
					if(!editCouponObj.getRedeemdCount().equals(redeemdlimitLongbxId.getValue())) {
						editCouponObj.setRedeemdCount(redeemdlimitLongbxId.getValue());
						isModified = true;
					}
				}	
				
				
				//set Per Subscription Use Limit
				if((editCouponObj.getSingPromoContUnlimitedRedmptChk() == null || 
						editCouponObj.getSingPromoContUnlimitedRedmptChk() == false) && unlimitSubChkBxId.isChecked() ) {
					isModified = true;
					editCouponObj.setSingPromoContUnlimitedRedmptChk(unlimitSubChkBxId.isChecked());
					editCouponObj.setSingPromoContRedmptLimit(null);
					
				}else if((editCouponObj.getSingPromoContRedmptLimit() != null &&  perSubUselimitLongbxId.getValue() != null
						&& perSubUselimitLongbxId.getValue() !=  editCouponObj.getSingPromoContRedmptLimit() .longValue())  || 
						(editCouponObj.getSingPromoContUnlimitedRedmptChk() != null && editCouponObj.getSingPromoContUnlimitedRedmptChk() && !unlimitSubChkBxId.isChecked())){
					isModified = true;
					editCouponObj.setSingPromoContUnlimitedRedmptChk(false);
					editCouponObj.setSingPromoContRedmptLimit(perSubUselimitLongbxId.getValue() );
				}
				
				
				//set Redeemed with
				if(editCouponObj.getLoyaltyPoints() == null  && onlylpChkBxId.isChecked() ) {
					isModified = true;
					editCouponObj.setRequiredLoyltyPoits(lpIBxId.getValue());
					editCouponObj.setLoyaltyPoints((byte)1);
					
				}
				else if(editCouponObj.getLoyaltyPoints() !=null && lpIBxId.getValue() !=null && 
						lpIBxId.getValue() !=  editCouponObj.getRequiredLoyltyPoits() .longValue()) {
					isModified = true;
					editCouponObj.setRequiredLoyltyPoits(lpIBxId.getValue());
					editCouponObj.setLoyaltyPoints((byte)1);
				}else if(editCouponObj.getLoyaltyPoints() != null && !onlylpChkBxId.isChecked()){
					isModified = true;
					editCouponObj.setRequiredLoyltyPoits(null);
					editCouponObj.setLoyaltyPoints(null);
				}
				
				//check and set the Allow multiple use per receipt
				if(editCouponObj.isStackable() ^ multiplePerReceiptChkBxId.isChecked()){
					isModified = true;
					editCouponObj.setStackable(multiplePerReceiptChkBxId.isChecked());
				}	
				
			}else if(editCouponObj.getCouponGeneratedType().trim().equals(Constants.COUP_GENT_TYPE_SINGLE) && 
																		coupGenRadioGrId.getSelectedIndex() == 1) {
//				logger.debug("Both are not  Same --- ----");
				editCouponObj.setCouponCode(null);
				editCouponObj.setTotalQty(multiCodLimitLongBxId.getValue());
				editCouponObj.setAutoIncrCheck(multiCoupChkBxId.isChecked());
				editCouponObj.setCouponGeneratedType(Constants.COUP_GENT_TYPE_MULTIPLE);
				isModified = true;
				
			}else if(editCouponObj.getCouponGeneratedType().trim().equals(Constants.COUP_GENT_TYPE_MULTIPLE) && 
																				coupGenRadioGrId.getSelectedIndex() == 0) {
//				logger.debug("Both are not  Same --- ----");
				editCouponObj.setCouponCode(singCoupTxtBxId.getValue().trim());
				
				if(singCoupChkBxId.isChecked()) {
					editCouponObj.setTotalQty(new Long(0));
				}else {
					editCouponObj.setTotalQty(singlelimitLongbxId.getValue());
				}
				editCouponObj.setTotalQty(multiCodLimitLongBxId.getValue());
				editCouponObj.setCouponGeneratedType(Constants.COUP_GENT_TYPE_SINGLE);
				isModified = true;
				
				
			}else if(editCouponObj.getCouponGeneratedType().trim().equals(Constants.COUP_GENT_TYPE_MULTIPLE) && 
																		coupGenRadioGrId.getSelectedIndex() == 1) {
//				logger.debug("Both are not  Same -------");
				if(!editCouponObj.getTotalQty().equals(multiCodLimitLongBxId.getValue())) {
					editCouponObj.setTotalQty(multiCodLimitLongBxId.getValue());
					isModified = true;
				}
				if(!editCouponObj.getAutoIncrCheck() == multiCoupChkBxId.isChecked()){
					editCouponObj.setAutoIncrCheck(multiCoupChkBxId.isChecked());
					isModified = true;
				}
				
			}
			
			//set Stores
			
//			if(storeSelectDivId.isVisible()) {
				
			if(allStoresChkBxId.isChecked()) {
				isModified = true;
				editCouponObj.setAllStoreChk(true);
				editCouponObj.setSelectedStores(null);
			}else {
				isModified = true;
				editCouponObj.setAllStoreChk(false);
				List<Listitem> storeItemLst = selectedStoreLbId.getItems();
				String seledStoreStr = null;
				for (Listitem eachItem : storeItemLst) {
					String storeNumber = ((Listcell)eachItem.getFirstChild()).getLabel();
					storeNumber = storeNumber.replace("Store Name", "").trim();
					if((seledStoreStr == null || seledStoreStr.trim().length() == 0) && storeNumber.trim().length() >0) {
						seledStoreStr = storeNumber;
					}else if(storeNumber.trim().length() >0){
						seledStoreStr +=";=;"+storeNumber.trim();
					}
					/*seledStoreStr += seledStoreStr.trim().length()  <= 0 ?  storeNumber.trim() : ";=;"+storeNumber.trim();
					logger.debug(">>> edietable store selection is  ::"+seledStoreStr);*/
				}
				
				editCouponObj.setSelectedStores(seledStoreStr);
			}
			
			//Discount Criteria 
			if(!(editCouponObj.getDiscountType().trim().equals(DISC_TYPE_PERCENTAGE) &&
					percentRadioId.isChecked())) {
				String discountTypeStr ="";
				if(percentRadioId.isChecked()) discountTypeStr = DISC_TYPE_PERCENTAGE ; else  discountTypeStr = DISC_TYPE_VALUE;
				
				editCouponObj.setDiscountType(discountTypeStr);
				isModified = true;
			}
			
			if(!editCouponObj.getDiscountCriteria().trim().equals(discountRadioGrId.getSelectedItem().getValue().toString()))	{
				editCouponObj.setDiscountCriteria(discountRadioGrId.getSelectedItem().getValue().toString());
				isModified = true;
			}
			
			/*if((editCouponObj.getExpiryType().equalsIgnoreCase(Constants.COUP_VALIDITY_PERIOD_STATIC)&&promoRgId.getSelectedIndex() == 1)){
				editCouponObj.setCouponCreatedDate(null);
				editCouponObj.setCouponExpiryDate(null);
				isModified = true;
			}else if((editCouponObj.getExpiryType().equalsIgnoreCase(Constants.COUP_VALIDITY_PERIOD_DYNAMIC)&&promoRgId.getSelectedIndex() == 0)){
				isModified = true;
			}*/
			
			
			
			//Validity period
			
			Calendar fromCal = Calendar.getInstance();
			Calendar expCal = Calendar.getInstance();
			String editCoupStatu= "";
			if(promoRgId.getSelectedIndex() == 1){
				
				if(dynamicValidityLbId.getSelectedIndex() == 0){
					
					/*if(editCouponObj.getCouponCreatedDate().compareTo(fromCal) != 0) {
						//logger.info("after from cal checking");
						editCouponObj.setCouponCreatedDate(fromCal);
						isModified = true;
					}*/
					
					expCal.add(Calendar.DATE, Integer.parseInt(dynamicValidityTbId.getValue()));
					expCal  = setDefaultTimeforCal(expCal, true);
					
					/*if(editCouponObj.getCouponExpiryDate().compareTo(expCal) != 0) {
						//logger.info("after end cal checking");
						
						expCal = setDefaultTimeforCal(expCal, false);
						editCouponObj.setCouponExpiryDate(expCal);
						 
						isModified = true;
					}*/
					
					editCouponObj.setExpiryDetails(Constants.COUP_DYN_ISSUANCE + Constants.ADDR_COL_DELIMETER + dynamicValidityTbId.getValue().toString());
					
				}
				else if(dynamicValidityLbId.getSelectedIndex() == 1){
					
					/*if(editCouponObj.getCouponCreatedDate().compareTo(fromCal) != 0) {
						//logger.info("after from cal checking");
						editCouponObj.setCouponCreatedDate(fromCal);
						isModified = true;
					}*/
					
					expCal.add(Calendar.DATE, Integer.parseInt(dynamicValidityTbId.getValue()));
					expCal  = setDefaultTimeforCal(expCal, true);
					
					/*if(editCouponObj.getCouponExpiryDate().compareTo(expCal) != 0) {
						//logger.info("after end cal checking");
						
						expCal = setDefaultTimeforCal(expCal, false);
						editCouponObj.setCouponExpiryDate(expCal);
						 
						isModified = true;
					}*/
					
					editCouponObj.setExpiryDetails(Constants.COUP_DYN_BDAY + Constants.ADDR_COL_DELIMETER + dynamicValidityTbId.getValue().toString());
				}
				else if(dynamicValidityLbId.getSelectedIndex() == 2){
					
					/*if(editCouponObj.getCouponCreatedDate().compareTo(fromCal) != 0) {
						//logger.info("after from cal checking");
						editCouponObj.setCouponCreatedDate(fromCal);
						isModified = true;
					}*/
					
					expCal.add(Calendar.DATE, Integer.parseInt(dynamicValidityTbId.getValue()));
					expCal  = setDefaultTimeforCal(expCal, true);
					
					/*if(editCouponObj.getCouponExpiryDate().compareTo(expCal) != 0) {
						//logger.info("after end cal checking");
						
						expCal = setDefaultTimeforCal(expCal, false);
						editCouponObj.setCouponExpiryDate(expCal);
						 
						isModified = true;
					}*/
					
					editCouponObj.setExpiryDetails(Constants.COUP_DYN_ANNV + Constants.ADDR_COL_DELIMETER + dynamicValidityTbId.getValue().toString());
					
				}
				
				editCouponObj.setExpiryType(Constants.COUP_VALIDITY_PERIOD_DYNAMIC);
		//		editCouponObj.setExpiryDetails(dynamicValidityTbId.getValue().toString() + Constants.ADDR_COL_DELIMETER + dynamicValidityLbId.getSelectedItem().getLabel());
				editCoupStatu = Constants.CAMP_STATUS_RUNNING;
				
			}
			else if(promoRgId.getSelectedIndex() == 0){
				
			Date fromDate = createDateBxId.getValue();
			fromCal.setTime(fromDate);
			fromCal = setDefaultTimeforCal(fromCal, true);
//			fromCal
			if(editCouponObj.getCouponCreatedDate() != null){
			if(editCouponObj.getCouponCreatedDate().compareTo(fromCal) != 0) {
				//logger.info("after from cal checking");
				editCouponObj.setCouponCreatedDate(fromCal);
				isModified = true;
			}
			}else {
				editCouponObj.setCouponCreatedDate(fromCal);
				isModified = true;
			}
			
			Date endDate = expiryDateBxId.getValue();
			expCal.setTime(endDate);
 
			if(editCouponObj.getCouponExpiryDate() != null){
			if(editCouponObj.getCouponExpiryDate().compareTo(expCal) != 0) {
				//logger.info("after end cal checking");
				
				expCal = setDefaultTimeforCal(expCal, false);
				/*expCal1.set(Calendar.HOUR_OF_DAY, 23);
				expCal1.set(Calendar.MINUTE, 59);
				expCal1.set(Calendar.SECOND, 59);*/
				editCouponObj.setCouponExpiryDate(expCal);
				 
				isModified = true;
			}
			}else {
				editCouponObj.setCouponExpiryDate(expCal);
				isModified = true;
			}
			
			editCouponObj.setExpiryType(Constants.COUP_VALIDITY_PERIOD_STATIC);
			editCouponObj.setExpiryDetails(null);
			Calendar curCal = Calendar.getInstance();
			if(editCouponObj.getStatus().equals(Constants.COUP_STATUS_PAUSED)) {
				editCoupStatu = Constants.COUP_STATUS_PAUSED; 
			}else {
				
				if(curCal.after(expCal)) {
					editCoupStatu = Constants.COUP_STATUS_EXPIRED; 
					
				}else if(curCal.before(fromCal)) {
					editCoupStatu= Constants.CAMP_STATUS_ACTIVE; 
					
				}else {
					editCoupStatu= Constants.CAMP_STATUS_RUNNING;
				}
			}
			
			
			}
			
			
			
			
//			 Calendar fromCal = createDateBxId.getClientValue();
			
			
			//logger.info("editCoupStatu >>>>>"+editCoupStatu +" >>isModified >>"+isModified);
			if(isModified) {
				
				//set the Status
				editCouponObj.setStatus(editCoupStatu);
				
				//set modified user 
				editCouponObj.setLastModifiedUser(users.getUserName());
				
				//set modified user Date
				editCouponObj.setUserLastModifiedDate(Calendar.getInstance());
				
				
				//couponsDao.saveOrUpdate(editCouponObj);
				couponsDaoForDML.saveOrUpdate(editCouponObj);
			}
			coupDiscGenDaoForDML.deleteDiscountGenByCouponId(editCouponObj.getCouponId());
		//	coupDiscGenDaoForDML.insertIntoCouponDiscountby(editCouponObj.getUserId(), editCouponObj.getCouponId());
			coupDiscGenDaoForDML.deleteAllSkuByCouponId(editCouponObj.getCouponId());
	}//saveEditCoupObj
			
	 
	
	 Map<String, List<SkuFile>> listOfDisItemSidMap = new HashMap<String, List<SkuFile>>();
	 Map<String, Long> listOfDisTotPurMap = new HashMap<String, Long>();
	 Map<String,List> listOfItemSidOrTotPurMap = new HashMap<String, List>();
//	 Map<String, List<Long>> listOfDisTotPurMap = new HashMap<String, List<Long>>();
	
	 
	
	
	 private void enableDisableDiscountCrtiteriaOptions() {
		 
		List rowList =  discountGenRowsId.getChildren();
		
		if(rowList.size() >= 1)	{
			skuRadioId.setDisabled(true);
			tpaRadioId.setDisabled(true);
			percentRadioId.setDisabled(true);
			dollerRadioId.setDisabled(true);
//			discountfirstListBxId.setDisabled(true);
		}
		else {
			skuRadioId.setDisabled(false);
			tpaRadioId.setDisabled(false);
			percentRadioId.setDisabled(false);
			dollerRadioId.setDisabled(false);
//			discountfirstListBxId.setDisabled(false);
		}
		
		setheaders();
	 } //
	 private Hbox loyatyPntshboxId;
//	 loyaltyHbxId;
//	 private Checkbox onlylpChkBxId;
	 public void onCheck$onlylpChkBxId() {
		 if(onlylpChkBxId.isChecked()) {
			 loyatyPntshboxId.setVisible(true);
		 }else{
			 loyatyPntshboxId.setVisible(false);
		 }
	 } //onCheck$lpChkBxId
	 
	 private void setDefaultDimensions() {
		 
		 Components.removeAllChildren(bcDimensionListboxId2);
		 if(bctypeListboxId.getSelectedItem().getValue().equals(Constants.COUP_BARCODE_DATAMATRIX)){
			 for(int i=0 ; i < DM_Dimension.length; i++){
				 Listitem tempItem = generateLItem(DM_Dimension[i]);
				 tempItem.setParent(bcDimensionListboxId2);
			 }
			 bcDimensionListboxId2.setSelectedIndex(0);
		 }else if(bctypeListboxId.getSelectedItem().getValue().equals(Constants.COUP_BARCODE_AZTEC)){
			 for(int i=0 ; i < AZ_Dimension.length; i++){
				 Listitem tempItem = generateLItem(AZ_Dimension[i]);
				 tempItem.setParent(bcDimensionListboxId2);
			 }
			 bcDimensionListboxId2.setSelectedIndex(0);
		 }else if(bctypeListboxId.getSelectedItem().getValue().equals(Constants.COUP_BARCODE_QR)){
			 for(int i=0 ; i < QR_Dimension.length; i++){
				 Listitem tempItem = generateLItem(QR_Dimension[i]);
				 tempItem.setParent(bcDimensionListboxId2);
			 } 
			 bcDimensionListboxId2.setSelectedIndex(0);
		 }else {
			 for(int i=0 ; i < LN_Dimension.length; i++){
				 Listitem tempItem = generateLItem(LN_Dimension[i]);
				 tempItem.setParent(bcDimensionListboxId2);
			 } 
			 bcDimensionListboxId2.setSelectedIndex(LN_Dimension.length - 1);
		 }
		 //bcDimensionListboxId2.setSelectedIndex(0);
	 } // setDefaultDimensions
	 
	 private Listitem generateLItem(String itemValStr) {
		 String strLabel = itemValStr.replace("X", " X ");
		 Listitem litem = new Listitem(strLabel, itemValStr);
		 return litem;
	 } // generateLItem
	 
	 
	 private Combobox storeNumbCmboBxId;
	 private Listbox selectedStoreLbId;
	 private void setDeafultStore(){
		 organizationStoresDao =	(OrganizationStoresDao)SpringUtil.getBean("organizationStoresDao");
		 List<OrganizationStores> storeIdList = organizationStoresDao.findAllStores(users.getUserOrganization().getUserOrgId());
		 //logger.debug(">>>> "+storeIdList.size());
		 if(storeIdList == null || storeIdList.size() == 0) return;
		 Comboitem comboItem = null;
		 for (OrganizationStores eachStoe : storeIdList){
			 comboItem = new Comboitem();
			 //logger.debug("achStoe.getHomeStoreId() VALUE IS >>>> "+eachStoe.getHomeStoreId());
			 comboItem.setLabel(eachStoe.getHomeStoreId());
			 comboItem.setParent(storeNumbCmboBxId);
				
		}
		 
	 }
	 public void onClick$addStoreImgId() {
		// logger.debug(">>>> add store click here  >>>");
		// logger.debug( " selected Item values are  :::: "+storeNumbCmboBxId.getValue());
		 if(storeNumbCmboBxId.getValue() == null || storeNumbCmboBxId.getValue().trim().length() == 0) {
			 MessageUtil.setMessage("Please provide store number.", "red", "TOP");
				return;
		 }
		 
		 Listitem tempItem = null;
		 Listcell tempCell = null;
		 if(selectedStoreLbId.getItemCount() == 0) {
			 tempItem = new Listitem();
			 tempCell = new Listcell("Store Name "+ storeNumbCmboBxId.getValue().trim());
			 tempCell.setParent(tempItem);
			 
			 
			 tempCell = new Listcell();
			 Image delImg = new Image("/img/action_delete.gif");
			 delImg.setStyle("cursor:pointer;margin-right:10px;");
			 delImg.setTooltiptext("Delete Store");
			 delImg.setAttribute("COUPON_TYPE", "DELETE_STORE");
			 delImg.addEventListener("onClick", this);
			 
			 delImg.setParent(tempCell);
			 tempCell.setParent(tempItem);
			 tempItem.setParent(selectedStoreLbId);
		 }
		 
		 List<Listitem> listItem= selectedStoreLbId.getItems();
		 boolean existedStore = false;
		 for (Listitem eachItem : listItem) {
			 if(eachItem.getLabel().equals("Store Name "+ storeNumbCmboBxId.getValue().trim())) {
				 existedStore = true;
				 break;
			 }
			 
		 }
		 
		 if(!existedStore){
			 tempItem = new Listitem();
			 tempCell = new Listcell("Store Name "+ storeNumbCmboBxId.getValue().trim());
			 tempCell.setParent(tempItem);
			 
			 
			 tempCell = new Listcell();
			 Image delImg = new Image("/img/action_delete.gif");
			 delImg.setStyle("cursor:pointer;margin-right:10px;");
			 delImg.setTooltiptext("Delete Store");
			 delImg.setAttribute("COUPON_TYPE", "DELETE_STORE");
			 delImg.addEventListener("onClick", this);
			 
			 delImg.setParent(tempCell);
			 tempCell.setParent(tempItem);
			 tempItem.setParent(selectedStoreLbId);
		 }
		 
		 
	 } //
	 
	 
	/* public void onClick$clearFilterBtnId() {
		 
//		 itemCategoryFilterLBId,deptCodeFilterLBId,vendorCodeFilterLBId,classFilterLBId,subClassFilterLBId,dcsFilterLBId
		 itemCategoryFilterLBId.clearSelection();
		 Utility.filterListboxByListitems(itemCategoryFilterLBId,true);
		 deptCodeFilterLBId.clearSelection();
		 Utility.filterListboxByListitems(deptCodeFilterLBId,true);
		 vendorCodeFilterLBId.clearSelection();
		 Utility.filterListboxByListitems(vendorCodeFilterLBId,true);
		 classFilterLBId.clearSelection();
		 Utility.filterListboxByListitems(classFilterLBId,true);
		 subClassFilterLBId.clearSelection();
		 Utility.filterListboxByListitems(subClassFilterLBId,true);
		 dcsFilterLBId.clearSelection();
		 Utility.filterListboxByListitems(dcsFilterLBId,true);
		 
		// filterButtonChk();
	 } // onClick$clearFilterBtnId
*/	 
	 
	 public void onClick$cancelCoupBtnId() {
		 coupDiscGenDaoForDML.deleteAllSkuBy(users.getUserId(),null);
		 getAllPromoFromDB();
		 onSelect$manageCouponsTabBoxId();
		 manageCouponsTabBoxId.setSelectedIndex(0);
	 }// onClick$cancelCoupBtnId
	 
	 Checkbox mpvChkBoxId;
	 public void onCheck$mpvChkBoxId(){
		 
		 if(!mpvChkBoxId.isChecked()) totPurAmtLngBxId.setValue(null);
		 totPurAmtLngBxId.setDisabled(!mpvChkBoxId.isChecked());
	 }
	 

	public void export(Coupons couponObj){
		
		long startTime = System.currentTimeMillis();
		logger.debug("Promo code export called here ..");
		
		
		JdbcResultsetHandler jdbcResultsetHandler = null;
		String sqlqry = Constants.STRING_NILL;
		String userName = Constants.STRING_NILL;
		String usersParentDirectory = Constants.STRING_NILL;
		String exportDir = Constants.STRING_NILL;
		File downloadDir = null;
		String name = Constants.STRING_NILL;
		File file = null;
		BufferedWriter bw = null;
		String filePath = Constants.STRING_NILL;
		
		try {
			jdbcResultsetHandler = new JdbcResultsetHandler();
			sqlqry = "select coupon_code, status from coupon_codes where coupon_id="+couponObj.getCouponId();
			jdbcResultsetHandler.executeStmt(sqlqry);
			int size=jdbcResultsetHandler.totalRecordsSize();
			if(size == 0){
				MessageUtil.setMessage("No Discount Code exists.","color:red","TOP");
				return;
			}
			if(couponObj.getCouponGeneratedType().equalsIgnoreCase("Multiple") && size < couponObj.getTotalQty() ) {
				MessageUtil.setMessage("Discount codes generation is still in process. Please try again after a while.", "RED", "TOP");
				return;
			}

			userName = GetUser.getUserName();
			usersParentDirectory = (String)PropertyUtil.getPropertyValue("usersParentDirectory");
			exportDir = usersParentDirectory + "/" + userName + "/Export/" ;
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


			name = couponObj.getCouponName();
			if(name.contains("/")) {
				name = name.replace("/", "_") ;
			}
			filePath = exportDir +  "Report_" + name + "_" +
					MyCalendar.calendarToString(Calendar.getInstance(), MyCalendar.FORMAT_YEARTOSEC, clientTimeZone);


			filePath = filePath + "_discountCodes.csv";
			logger.debug("Download File path : " + filePath);
			file = new File(filePath);
			bw = new BufferedWriter(new FileWriter(filePath));
			bw.write("\"Discount Code\",\"Status\",\r\n");
			
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
				csvWriter = null;
				
			}
			
					
			Filedownload.save(file, "text/csv");
			file = null;

		} catch (Exception e) {
				logger.error("Error  :: ",e);
			}finally{
				if(jdbcResultsetHandler != null)jdbcResultsetHandler.destroy();
				// making all resources ready for GC.
				couponObj = null; sqlqry = null; userName = null; usersParentDirectory = null; exportDir = null; downloadDir = null; name = null;
				filePath = null;  bw = null;jdbcResultsetHandler= null; file =null;
				//System.gc();
			}
		
		long endTime = System.currentTimeMillis();
		logger.fatal("Time taken to export coupons is :::   :: " + (endTime-startTime));
		}
		
	
	/*public void onChange$createDateBxId(){
		promofromDatevalidation();
	}*/
	 
	/*private boolean promofromDatevalidation(){
		
		
//		Calendar tempCal = createDateBxId.getClientValue();
//		logger.info(" clent value is  :: "+createDateBxId.getClientValue().getTime());
		Calendar tempCal = createDateBxId.getClientValue();
		logger.info("tempCal time is  ::"+tempCal.getTime());
		tempCal = setDefaultTimeforCal(tempCal,true);
		
//		Clients.evalJavaScript("setdate()");
		
		Calendar tempCal2 = Calendar.getInstance();
		logger.info("datebox get value is  ::"+dateBoxId.getValue());
		
//		tempCal2.setTime(dateBoxId.getValue());
		
		logger.info("tempCal2 time is  ::"+tempCal2.getTime());
		tempCal2.setTimeZone(clientTimeZone);
		
//		Executions.getCurrent().get
		logger.info("after setting the Timezone tempCal2 time is  ::"+tempCal2.getTime());
		
		HttpServletRequest request = (HttpServletRequest) Executions.getCurrent().getNativeRequest();
		
		Calendar cal = Calendar.getInstance(request.getLocale());
		
		logger.info("calendar instance from locale:::::::::::::::::    " + cal.getTime());
		
		Calendar cal1 = Calendar.getInstance(clientTimeZone);
		
		logger.info("calendar instance from locale:::::::::::::::::    " + cal1.getTime());
		
//		request.getServletContext().get
		
		tempCal2 = setDefaultTimeforCal(tempCal2,true);
		logger.info("after setting the setDefaultTimeforCal method  tempCal2 time is  ::"+tempCal2.getTime());
		if(tempCouponObj !=null){
			
			Calendar objCreatedCal = tempCouponObj.getCouponCreatedDate();
			String calStr = MyCalendar.calendarToString(objCreatedCal, MyCalendar.FORMAT_SB_DATEONLY);
			if(objCreatedCal.compareTo(tempCal) != 0 && tempCal2.after(tempCal)) {
				MessageUtil.setMessage(" 'From' date  should always  be either future date or "+calStr+".", "red");
				return false;
			}
			
			
		}else {
			
			if(tempCal2.compareTo(tempCal) != 0 && tempCal2.after(tempCal)) {
				logger.info(" show error message here  :: ");
				MessageUtil.setMessage("'From' date  should always  be either current date or future date.", "red");
				return false;
			}
			
		}
		return true;
	}
	 */
	private Calendar setDefaultTimeforCal(Calendar tempCal, boolean isStart) {
		if(isStart) {
			
			tempCal.set(Calendar.HOUR_OF_DAY, 00);
			tempCal.set(Calendar.MINUTE, 00);
			tempCal.set(Calendar.SECOND, 00);
			tempCal.set(Calendar.MILLISECOND, 00);
		}else {
			tempCal.set(Calendar.HOUR_OF_DAY, 23);
			tempCal.set(Calendar.MINUTE, 59);
			tempCal.set(Calendar.SECOND, 59);
			tempCal.set(Calendar.MILLISECOND, 59);
		}
		return tempCal;
	}
	
	@Override
	public void onEvent(Event event) throws Exception {
		try {
			super.onEvent(event);
			Div tempDiv = null;
			Row tempRow = null;
			logger.info(" Event Name:" + event.getName());
			if (event.getTarget() instanceof Image) {
				Image tempImg = (Image) event.getTarget();

				if (tempImg.getParent() instanceof Div) {

					tempDiv = (Div) tempImg.getParent();
				}
			 	
//			List chldLst = tempDiv.getChildren();"COUPON_TYPE", COUPON_DISCOUNT_DELETE)//listOfDisItemSidMap,listOfDisTotPurMap
				if(tempImg.getAttribute("COUPON_TYPE").equals(COUPON_DISCOUNT_DELETE)) {
					
					tempRow = (Row)tempImg.getParent();
					Label keyLabel = (Label)tempRow.getChildren().get(0);
					Label typeLabel=(Label)tempRow.getChildren().get(1);
					String limit=tempRow.getValue();
					//List<CouponDiscountGeneration> deleteDiscountList=new ArrayList<>();
					if(skuRadioId.isChecked()){
					String type=(String) typeLabel.getAttribute("Type");
					Label valueLabel=(Label)tempRow.getChildren().get(2);
					if(valueLabel.getValue().equals("--All Items--"))
					coupDiscGenDaoForDML.deleteTempSkuBy(users.getUserId(),type,keyLabel.getValue(),null,null);
					else
					coupDiscGenDaoForDML.deleteTempSkuBy(users.getUserId(),type,keyLabel.getValue(),limit,null);
					/*if(editCoupDiscGenLst!=null && editCoupDiscGenLst.size()>0){
						for(CouponDiscountGeneration oldCouponDiscount:editCoupDiscGenLst){
							if(oldCouponDiscount.getSkuAttribute().equals(type)&& oldCouponDiscount.getDiscount().equals(Double.parseDouble(keyLabel.getValue())) )
								deleteDiscountList.add(oldCouponDiscount);
						  }
					 }*/
					}
					else{
						coupDiscGenDaoForDML.deleteTempSkuBy(users.getUserId(),keyLabel.getValue(),typeLabel.getValue().equals("N/A")?"0":typeLabel.getValue(),null);
						/*if(editCoupDiscGenLst!=null && editCoupDiscGenLst.size()>0){
							for(CouponDiscountGeneration oldCouponDiscount:editCoupDiscGenLst){
								if(oldCouponDiscount.getDiscount().equals(Double.parseDouble(keyLabel.getValue()))&&
										oldCouponDiscount.getTotPurchaseAmount().equals(Long.parseLong(typeLabel.getValue())) )
									deleteDiscountList.add(oldCouponDiscount);
							  }
					   }*/
					}
					
					/*coupDiscGenDaoForDML.deleteByCollection(deleteDiscountList);
					if(editCoupDiscGenLst!=null && editCoupDiscGenLst.size()>0)
					editCoupDiscGenLst.removeAll(deleteDiscountList);
					//logger.info(" >>>before  deleting the Map is ::"+listOfDisItemSidMap);
					
					if(listOfDisTotPurMap.containsKey(keyLabel.getValue())) listOfDisTotPurMap.remove(keyLabel.getValue());
					if(listOfDisItemSidMap.containsKey(keyLabel.getValue())) listOfDisItemSidMap.remove(keyLabel.getValue());
					//logger.info(" >>>after  deleting the Map is ::"+listOfDisItemSidMap);
					
					*/
					discountGenRowsId.removeChild(tempRow);
					
					enableDisableDiscountCrtiteriaOptions();
					//selectAllSKUBtnId.setAttribute("selectedAll", false);
					deSelectSkuMap.clear();
				}
				
				else if(tempDiv != null && tempDiv.getParent() instanceof Row) {
					
					tempRow = (Row)tempDiv.getParent();
					tempCouponObj = (Coupons)tempRow.getAttribute("COUPON_OBJ");
					logger.debug(">>>>tempCouponObj >>"+tempCouponObj);
					
					if(tempImg.getAttribute("COUPON_TYPE").equals(COUPON_EDIT)) {
//					sessionScope.setAttribute("EDIT_COUPON_OBJ", tempCouponObj);
						
						//editCouponSetting (tempCouponObj);
						
						sessionScope.setAttribute("EDIT_COUPON_OBJECT", tempCouponObj);
						Redirect.goTo("admin/createCoupon");
						
						//Redirect.goTo("admin/couponGeneration");
						//manageCouponsTabBoxId.setSelectedIndex(1);
						
					}
					
					
					else if(tempImg.getAttribute("COUPON_TYPE").equals(COUPON_STATUS)) {
						
						String coupStatusStr = tempCouponObj.getStatus();
						
						if(coupStatusStr.equals(Constants.COUP_STATUS_EXPIRED)) {
							MessageUtil.setMessage("Discount Code has expired. Please reactivate the Discount Code.", "RED", "TOP");
							return;
						}
						
						try {
							int confirm = Messagebox.show("Are you sure you want to change the Discount Code status?",
									"Discount Code Status Changes ", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
							if(confirm != Messagebox.OK) return;
						} catch (Exception e) {
							logger.error("Exception ::", e);
							return;
						}
						
						
						Calendar currCal = Calendar.getInstance();
						String imgSrc = "";
						String toolTipStr = "";
						
						//APP-1362
						if(tempCouponObj.getExpiryType().equalsIgnoreCase(Constants.COUP_VALIDITY_PERIOD_DYNAMIC))
						{
							if(coupStatusStr.equals(Constants.COUP_STATUS_PAUSED))
						    {
								coupStatusStr = Constants.COUP_STATUS_RUNNING;
								imgSrc = "/img/pause_icn.png";
								toolTipStr = "Pause";
							  }
					
							else if (coupStatusStr.equals(Constants.COUP_STATUS_ACTIVE)
									|| coupStatusStr.equals(Constants.COUP_STATUS_RUNNING)) {
								imgSrc = "/img/play_icn.png";
								coupStatusStr = Constants.COUP_STATUS_PAUSED;
								toolTipStr = "Activate";

							}
						}
						else if(tempCouponObj.getExpiryType().equalsIgnoreCase(Constants.COUP_VALIDITY_PERIOD_STATIC))
						{
						if (coupStatusStr.equals(Constants.COUP_STATUS_PAUSED)) {
							if (currCal.after(tempCouponObj.getCouponExpiryDate())) {
								coupStatusStr = Constants.COUP_STATUS_EXPIRED;
								imgSrc = "/img/pause_icn.png";
								toolTipStr = "Pause";
							}
							else if (currCal.after(tempCouponObj.getCouponCreatedDate())
									&& currCal.before(tempCouponObj.getCouponExpiryDate())) {
								imgSrc = "/img/pause_icn.png";

								coupStatusStr = Constants.COUP_STATUS_RUNNING;
								toolTipStr = "Pause";
							} else {
								imgSrc = "/img/pause_icn.png";
								coupStatusStr = Constants.COUP_STATUS_ACTIVE;
								toolTipStr = "Pause";

							}
						}

						else if (coupStatusStr.equals(Constants.COUP_STATUS_ACTIVE)
								|| coupStatusStr.equals(Constants.COUP_STATUS_RUNNING)) {
							imgSrc = "/img/play_icn.png";
							coupStatusStr = Constants.COUP_STATUS_PAUSED;
							toolTipStr = "Activate";

						}
						}
						//
						/*if (coupStatusStr.equals(Constants.COUP_STATUS_PAUSED)) {
							if (currCal.after(tempCouponObj.getCouponExpiryDate())) {
								coupStatusStr = Constants.COUP_STATUS_EXPIRED;
								imgSrc = "/img/pause_icn.png";
								toolTipStr = "Pause";
							}
							else if (currCal.after(tempCouponObj.getCouponCreatedDate())
									&& currCal.before(tempCouponObj.getCouponExpiryDate())) {
								imgSrc = "/img/pause_icn.png";

								coupStatusStr = Constants.COUP_STATUS_RUNNING;
								toolTipStr = "Pause";
							} else {
								imgSrc = "/img/pause_icn.png";
								coupStatusStr = Constants.COUP_STATUS_ACTIVE;
								toolTipStr = "Pause";

							}
						}

						else if (coupStatusStr.equals(Constants.COUP_STATUS_ACTIVE)
								|| coupStatusStr.equals(Constants.COUP_STATUS_RUNNING)) {
							imgSrc = "/img/play_icn.png";
							coupStatusStr = Constants.COUP_STATUS_PAUSED;
							toolTipStr = "Activate";

						}*/
						
						SpecialRewardsDaoForDML specialRewardsDaoForDML = (SpecialRewardsDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.SPECIAL_REWARDS_DAO_FOR_DML);
						if(coupStatusStr.equals(Constants.COUP_STATUS_PAUSED)){
							
							if(tempCouponObj.getSpecialRewadId() != null && tempCouponObj.getPurchaseQty() != null ){//
								specialRewardsDaoForDML.updateSpecialRewardStatus(tempCouponObj.getSpecialRewadId(), Constants.COUP_STATUS_PAUSED);//executeUpdate("UPDATE SpecialReward SET statusSpecialReward='"+Constants.COUP_STATUS_PAUSED+"' WHERE rewardId="+tempCouponObj.getSpecialRewadId());
								
								
							}
						}else if(coupStatusStr.equals(Constants.COUP_STATUS_RUNNING)){
							
							if(tempCouponObj.getSpecialRewadId() != null && tempCouponObj.getPurchaseQty() != null ){//
								specialRewardsDaoForDML.updateSpecialRewardStatus(tempCouponObj.getSpecialRewadId(), OCConstants.LOYALTY_PROGRAM_STATUS_ACTIVE);//executeUpdate("UPDATE SpecialReward SET statusSpecialReward='"+Constants.COUP_STATUS_PAUSED+"' WHERE rewardId="+tempCouponObj.getSpecialRewadId());
							}
						}
						tempCouponObj.setStatus(coupStatusStr);
						// couponsDao.saveOrUpdate(tempCouponObj);
						couponsDaoForDML.saveOrUpdate(tempCouponObj);
						Label tempLbl = (Label) tempRow.getChildren().get(4);
						tempLbl.setValue(coupStatusStr);

						// logger.info("Image Src is >>"+imgSrc+ "
						// >>>coupStatusStr ::"+coupStatusStr+" >>toolTipStr
						// is::"+toolTipStr);
						tempImg.setSrc(imgSrc);
						tempImg.setTooltiptext(toolTipStr);

					}

					else if (tempImg.getAttribute("COUPON_TYPE").equals(COUPON_DELETE)) {
						
						try {
							int confirm = Messagebox.show("Are you sure you want to delete the Discount Code?",
									"Delete Discount Code ", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
							if (confirm != Messagebox.OK)
								return;
						} catch (Exception e) {
							logger.error("Exception ::", e);
							return;
						}

						couponCodesDao = (CouponCodesDao) SpringUtil.getBean("couponCodesDao");
						Long coupId = tempCouponObj.getCouponId();

						int coupCodeInt = couponCodesDao.findTotCountCouponCodes(coupId);
						//logger.info("coupCodeInt>>>"+coupCodeInt);
						if(coupCodeInt > 0 ) {
							MessageUtil.setMessage("Discount Code is currently being used in a scheduled \nEmail / SMS campaign and cannot be deleted.", "red");
							return;
						}
						
						logger.debug("tempCouponObj.getCouponId() >>> "+coupId);
						
						//coupDiscGenDao.deleteByCouponId(coupId);
						coupDiscGenDaoForDML.deleteByCouponId(coupId);
						
						couponRowsId.removeChild(tempRow);
						
						getAllPromoFromDB();
						contactsGridId.setAttribute("defaultOrderBy", "userCreatedDate");
						registerEventListner(0, 5);
						Utility.refreshGridModel(viewCouponsELObj, 0, true);
						MessageUtil.setMessage("Discount Code deleted successfully.", "green", "TOP");
						 
						
					}
					else if(tempImg.getAttribute("COUPON_TYPE").equals(COUPON_REPORTS) && tempCouponObj != null) {
						
						logger.info("tempCouponObj.getCouponId() >>> "+tempCouponObj.getCouponId());
						sessionScope.setAttribute("COUP_REDEEMED_DETAILS", tempCouponObj);
						Redirect.goTo(PageListEnum.REPORT_COUPONS);
						
					}else if(tempImg.getAttribute("COUPON_TYPE").equals(COUPON_EXPORT)){

						try {
							int confirm = Messagebox.show("Are you sure you want to export Discount Code?",
									"Export Discount Code Generation  ", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
							if(confirm != Messagebox.OK) return ;
						} catch (Exception e) {
							logger.error("Exception ::"+e.getStackTrace());
							return ;
						}
						
						
						export(tempCouponObj);
						
					}
				}else if(tempImg.getAttribute("COUPON_TYPE").equals("DELETE_STORE") ) {
					logger.debug("delete store here ");
					//TODO  show message here
					Listitem storeItem = (Listitem)tempImg.getParent().getParent();
					selectedStoreLbId.removeChild(storeItem);
					
				}
				
			}
			
			
			else if(event.getTarget() instanceof Paging) {
				
				
				Paging paging = (Paging) event.getTarget();
				int desiredPage = paging.getActivePage();
				
				PagingEvent pagingEvent = (PagingEvent) event;
				int pSize = pagingEvent.getPageable().getPageSize();
				int ofs = desiredPage * pSize;
				
				fillCouponsBySize(ofs, (byte) pagingEvent.getPageable().getPageSize());
				
			}
			else if(event.getTarget() instanceof Checkbox){
				Checkbox ck=(Checkbox) event.getTarget();
				Listbox listbox=((Listbox) ck.getAttribute("listId"));
				if(ck.isChecked()){
					listbox.setVisible(true);
			   }
				else{
					listbox.setVisible(false);
					listbox.setSelectedIndex(-1);
				}
				adjustListBoxes();
			}
			else if(event.getTarget() instanceof Listitem){
			} 
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::",e);
		}
		
			
		
	} // onEvent

	public void onSelect$srchLbId() {
		String value = srchLbId.getSelectedItem().getValue();
		if(value.equals(SEARCH_BY_NAME)) {
			searchByPromoCodeNameDivId.setVisible(true);
			searchByPromoCodeNameTbId.setText(Constants.STRING_NILL);
			searchByPromoCodeNameTbId.setFocus(true);
			
			searchByCreatedOnDivId.setVisible(false);
			searchByStatusDivId.setVisible(false);
			codeStatusLb.setSelectedIndex(0);
			return;
		}
		else
		if(value.equals(SEARCH_BY_DATE)) {
			searchByPromoCodeNameDivId.setVisible(false);
			searchByCreatedOnDivId.setVisible(true);
			fromDateboxId.setText(Constants.STRING_NILL);
			toDateboxId.setText(Constants.STRING_NILL);
			
			
			searchByStatusDivId.setVisible(false);
			codeStatusLb.setSelectedIndex(0);
			return;
		}
		else
		if(value.equals(SEARCH_BY_STATUS)) {
			searchByPromoCodeNameDivId.setVisible(false);
			searchByCreatedOnDivId.setVisible(false);
			searchByStatusDivId.setVisible(true);
			codeStatusLb.setSelectedIndex(0);
			return;
		}
		
	}//onSelect$srchLbId()
	
	public void onClick$filterBtnId() {
    	couponsPagingId.setActivePage(0);
        registerEventListner(0, couponsPagingId.getPageSize());
        Utility.refreshGridModel(viewCouponsELObj, 0, true);
	}//onClick$filterBtnId()
	
   private boolean validateSetCreationDate() {
		
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
		
	}//validateSetCreationDate()
     private boolean validateSetCampaignName() {
		if(searchByPromoCodeNameTbId.getValue().trim().isEmpty()) {
			MessageUtil.setMessage("Please enter Discount code name.",
					"color:red", "TOP");
			searchByPromoCodeNameTbId.setFocus(true);
			return false;
		}
		return true;
	}//validateSetCampaignName()
     
     public void onClick$resetAnchId() {
 		
     	srchLbId.setSelectedIndex(0);
     	codeStatusLb.setSelectedIndex(0);
     	memberPerPageLBId.setSelectedIndex(0);
    
     	
     	searchByPromoCodeNameDivId.setVisible(false);
     	searchByCreatedOnDivId.setVisible(false);
     	searchByStatusDivId.setVisible(true);
     	
     	
        couponsPagingId.setActivePage(0);
        registerEventListner(0, couponsPagingId.getPageSize());
        Utility.refreshGridModel(viewCouponsELObj, 0, true);
 		
 	}
 private boolean validateCouponName4SpecialChars(String couponName){
		String pattern = "[a-zA-Z0-9\\-\\*]+";
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(couponName);
		
		return m.matches();
    }
     
    public void onClick$showFilterImgId(){
    	
    	logger.info("show");
    	hideFilterImgId.setVisible(true);
    	showFilterImgId.setVisible(false);
    	filtersDivId.setVisible(true);
    	
    }
    
    public void onClick$hideFilterImgId(){
    	
    	logger.info("hide");
    	showFilterImgId.setVisible(true);
    	hideFilterImgId.setVisible(false);
    	filtersDivId.setVisible(false);
    }
    
    
    public void onCheck$promoRgId(){
    	if(promoRgId.getSelectedIndex() == 0){
    		
    		staticRadio.setChecked(true);
			dynamicRadio.setChecked(false);
			dynamicValidityTbId.setValue("");
			dynamicValidityLbId.setSelectedIndex(0);
    	}
    	else if(promoRgId.getSelectedIndex() == 1){
    		
    		staticRadio.setChecked(false);
			dynamicRadio.setChecked(true);
			
			createDateBxId.setValue(null);
			expiryDateBxId.setValue(null);
    	}
    }



	@Override
	public void render(Row tempRow, Coupons coupons, int arg2) throws Exception {
		

		
		Label tempLbl;
		
		//Coupon Name
		tempLbl = new Label(coupons.getCouponName());
		tempLbl.setParent(tempRow);
		
		//Description
		 String currSymbol = Utility.countryCurrencyMap.get(users.getCountryType());
		   if(currSymbol != null && !currSymbol.isEmpty()) userCurrencySymbol = currSymbol;
		String desc=coupons.getCouponDescription();
		if(desc != null && desc.contains("[PHCurr]")){
			desc = desc.replace("[PHCurr]", userCurrencySymbol);
		}
		tempLbl = new Label(desc);
		tempLbl.setTooltiptext(desc);
		tempLbl.setMaxlength(50);
		tempLbl.setParent(tempRow);
		
		//Promo code type Single or Multiple
		/*
		 * if(coupons.getCouponGeneratedType().equalsIgnoreCase("single")) tempLbl = new
		 * Label("Single code"); else
		 * if(coupons.getCouponGeneratedType().equalsIgnoreCase("multiple")) { tempLbl =
		 * new Label("Multiple & Random code"); tempLbl.setParent(tempRow); } else
		 * if(coupons.getCouponGeneratedType().equalsIgnoreCase("Draft")) tempLbl = new
		 * Label("--"); tempLbl.setParent(tempRow);
		 */
		if(coupons.getCouponGeneratedType().equalsIgnoreCase("single")) {
			if(coupons.getLoyaltyPoints() != null && (coupons.getRequiredLoyltyPoits() != null || coupons.getMultiplierValue()!=null)) {
			if(coupons.getValueCode() != null &&
					!coupons.getValueCode().equals(OCConstants.LOYALTY_POINTS)) {
			tempLbl = new Label("Rewards");
			}
		else if(coupons.getValueCode() == null || coupons.getValueCode().equals(OCConstants.LOYALTY_POINTS)){
			tempLbl = new Label("Loyalty");
		}
			}else tempLbl = new Label("Promotions");
		
		}
		else if(coupons.getCouponGeneratedType().equalsIgnoreCase("multiple")) {
			tempLbl = new Label("Coupons");
		tempLbl.setParent(tempRow);
		}
		else if(coupons.getCouponGeneratedType().equalsIgnoreCase("Draft"))
			tempLbl = new Label("--");
		tempLbl.setParent(tempRow);
		
		
		logger.info("coupon name is ::"+coupons.getCouponName());
	//	logger.info("created date is ::"+coupons.getCouponCreatedDate().getTime());
		//Created On
		tempLbl = new Label(MyCalendar.calendarToString(coupons.getUserCreatedDate(), MyCalendar.FORMAT_DATEONLY_GENERAL));
		tempLbl.setParent(tempRow);
		
		//Status
		tempLbl = new Label(coupons.getStatus());
		tempLbl.setParent(tempRow);
		
		//Discount
		String tempStr= "";
		if(coupons.getDiscountType().trim().equals("Percentage")) { //TODO set the constants
			tempStr = "% on";
		}else  {
			tempStr = userCurrencySymbol+" on";
		}
		
		if(coupons.getDiscountCriteria().trim().contains("SKU")) {
			tempStr += " Product";
		}else {
			tempStr += " Receipt";
		}
		//tempStr = tempStr+" "+coupons.getDiscountCriteria().trim();
		
		tempLbl = new Label(tempStr);
		tempLbl.setParent(tempRow);
		
		
		//Validity Period
		if(coupons.getExpiryType().equalsIgnoreCase(Constants.COUP_VALIDITY_PERIOD_DYNAMIC)){
			logger.info("dynamic validity-------");
			tempLbl = new Label("Dynamic");
			tempLbl.setParent(tempRow);
		}
		else if(coupons.getExpiryType().equalsIgnoreCase(Constants.COUP_VALIDITY_PERIOD_STATIC)){
			logger.info("static validity-------");
			tempLbl = new Label(MyCalendar.calendarToString(coupons.getCouponCreatedDate(), MyCalendar.FORMAT_DATEONLY_GENERAL)+" - " +
					MyCalendar.calendarToString(coupons.getCouponExpiryDate(), MyCalendar.FORMAT_DATEONLY_GENERAL));
			tempLbl.setParent(tempRow);
		}else {
			tempLbl = new Label("--");
			tempLbl.setParent(tempRow);
		}
		
		//No.of Codes
		
		String numOfCodeStr ="";
		boolean isSingleType = false;
		if(coupons.getCouponGeneratedType().equals("single")) {
			
			if(coupons.getAutoIncrCheck()) {
				numOfCodeStr = "Unlimited";
			}else {
				numOfCodeStr = ""+coupons.getTotalQty();
			}
			
			isSingleType =true;
		}else {
			if(coupons.getAutoIncrCheck()) {
				numOfCodeStr = coupons.getTotalQty()+"(Auto add)";
			}else {
				numOfCodeStr = coupons.getTotalQty()+"";
			}
		}
		tempLbl = new Label(numOfCodeStr);
		tempLbl.setParent(tempRow);
		
		//Actions
		Div tempDiv = new Div();
		
			//Edit Image
		Image editImg = new Image("/img/email_edit.gif");
		editImg.setStyle("cursor:pointer;margin-right:10px;");
		editImg.setTooltiptext("Edit");
		editImg.setAttribute("COUPON_TYPE", COUPON_EDIT);
		editImg.addEventListener("onClick", this);
		editImg.setParent(tempDiv);
		
			
		//Active Image
		Image tempImg = new Image();
		String statusStr = coupons.getStatus();
		
		//Pause or Active images
		if(statusStr.equals(Constants.COUP_STATUS_ACTIVE)  || statusStr.equals(Constants.COUP_STATUS_RUNNING)
				|| statusStr.equals(Constants.COUP_STATUS_EXPIRED)) {
			
			tempImg.setSrc("/img/pause_icn.png");
			tempImg.setTooltiptext("Pause");
			
		}
		else if(statusStr.equals(Constants.COUP_STATUS_PAUSED)) {
			tempImg.setSrc("/img/play_icn.png");
			tempImg.setTooltiptext(Constants.COUP_STATUS_ACTIVE);
			
		}
		
		tempImg.setAttribute("COUPON_TYPE", COUPON_STATUS);
		tempImg.setStyle("cursor:pointer;margin-right:10px;");
		tempImg.addEventListener("onClick", this);
		tempImg.setParent(tempDiv);
		
		//FBP codes - Running stauts cannot be deleted
		if(coupons!=null && 
				//coupons.getStatus().equals(Constants.COUP_STATUS_RUNNING) && 
				coupons.getSpecialRewadId() != null && coupons.getPurchaseQty() != null){
		}else {
			//Delete Image
		Image delImg = new Image("/img/action_delete.gif");
		delImg.setStyle("cursor:pointer;margin-right:10px;");
		delImg.setAttribute("COUPON_TYPE", COUPON_DELETE);
		delImg.setTooltiptext("Delete");
		delImg.addEventListener("onClick", this);
		delImg.setParent(tempDiv);
		}
			//reports Image
		Image repImg = new Image("/img/theme/home/reports_icon.png");
		repImg.setStyle("cursor:pointer;margin-right:10px;");
		repImg.setAttribute("COUPON_TYPE", COUPON_REPORTS);
		repImg.setTooltiptext("Reports");
		repImg.addEventListener("onClick", this);
		repImg.setParent(tempDiv);
		
		if(!isSingleType && ! (coupons.getCouponGeneratedType().equalsIgnoreCase("Draft"))) {
			//export promocodes
			Image exportPromoCodeImg = new Image("/img/icons/Export-of-Promo-codes-icon.png");
			exportPromoCodeImg.setStyle("cursor:pointer;margin-right:10px;");
			exportPromoCodeImg.setAttribute("COUPON_TYPE", COUPON_EXPORT);
			exportPromoCodeImg.setTooltiptext("Export");
			exportPromoCodeImg.addEventListener("onClick", this);
			exportPromoCodeImg.setParent(tempDiv);
		}
		
//		tempDiv.setAttribute("COUPON_OBJ", coupons);
		tempDiv.setParent(tempRow); 
		tempRow.setAttribute("COUPON_OBJ", coupons);
		//tempRow.setParent(couponRowsId);
		
	
		
	}
}
