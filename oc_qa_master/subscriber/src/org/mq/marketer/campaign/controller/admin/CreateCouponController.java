package org.mq.marketer.campaign.controller.admin;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
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

import org.apache.camel.com.googlecode.concurrentlinkedhashmap.EntryWeigher;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.CouponDiscountGeneration;
import org.mq.marketer.campaign.beans.Coupons;
import org.mq.marketer.campaign.beans.FormMapping;
import org.mq.marketer.campaign.beans.LoyaltyCardSet;
import org.mq.marketer.campaign.beans.LoyaltyProgram;
import org.mq.marketer.campaign.beans.LoyaltyProgramTier;
import org.mq.marketer.campaign.beans.MailingList;
import org.mq.marketer.campaign.beans.OrganizationStores;
import org.mq.marketer.campaign.beans.POSMapping;
import org.mq.marketer.campaign.beans.SKUTemp;
import org.mq.marketer.campaign.beans.SegmentRules;
import org.mq.marketer.campaign.beans.SkuFile;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.beans.ValueCodes;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.controller.contacts.CouponsEnum;
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
import org.mq.marketer.campaign.general.CouponDescriptionAlgorithm;
import org.mq.marketer.campaign.general.LBFilterEventListener;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.MyModelForCategory;
import org.mq.marketer.campaign.general.PageListEnum;
import org.mq.marketer.campaign.general.PageUtil;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Redirect;
import org.mq.marketer.campaign.general.SalesQueryGenerator;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.controller.loyalty.SpecialRewardEnum;
import org.mq.optculture.data.dao.JdbcResultsetHandler;
import org.mq.optculture.data.dao.LoyaltyCardSetDao;
import org.mq.optculture.data.dao.LoyaltyProgramDao;
import org.mq.optculture.data.dao.LoyaltyProgramTierDao;
import org.mq.optculture.data.dao.ValueCodesDao;
import org.mq.optculture.utils.OCCSVWriter;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.DropEvent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Auxhead;
import org.zkoss.zul.Auxheader;
import org.zkoss.zul.Bandbox;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Column;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Doublebox;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Groupbox;
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
import org.zkoss.zul.Menupopup;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.RowRenderer;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Window;
import org.zkoss.zul.event.PagingEvent;

public class CreateCouponController extends GenericForwardComposer
		implements EventListener, ListitemRenderer<SkuFile>, RowRenderer<Coupons> {

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	Long orgId;
	Paging couponsPagingId;
	
    boolean couponsSlcted,useasReferralCodeSlt;
	String filterQuery = null;
	String filterCountQuery = null;
	String qryPrefix = null;
	String SYMPIPELABEL = "SYMPIPE";
	LBFilterEventListener contactLbELObj = null;

	private Grid contactsGridId;
	private Map<Integer, String> dayMap = new HashMap<>();
	private Listbox memberPerPageLBId, bctypeListboxId, itemAttributeLbId,//valueCodeListboxId,
	productvalueCodeListboxId,discountTypeLbId,targetLb;
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
	private LoyaltyProgramTierDao loyaltyProgramTierDao;
	private LoyaltyCardSetDao loyaltyCardSetDao;
	private static String COUPON_EDIT = "COUPON_EDIT";
	private static String COUPON_DELETE = "COUPON_DELETE";
	private static String PRODUCT_COUPON_DELETE = "PRODUCT_COUPON_DELETE";
	private static String COUPON_RULE_DELETE = "COUPON_RULE_DELETE";
	private static String COUPON_RULE_EDIT = "COUPON_RULE_EDIT";
	private static String COUPON_REPORTS = "COUPON_REPORTS";
	private static String COUPON_STATUS = "COUPON_STATUS";
	private static String COUPON_EXPORT = "COUPON_EXPORT";
	private static String COUPON_DISCOUNT_DELETE = "COUPON_DISCOUNT_DELETE";
	//private static String COUPON_DISCOUNT_EDIT = "COUPON_DISCOUNT_EDIT"; //APP-2720
	private static String DELETE_SELECTED = "DELETE_SELECTED";
//	private static String EDIT_SELECTED = "EDIT_SELECTED"; //APP-2720
	// private Tabbox manageCouponsTabBoxId;

	private Textbox couponNameTxtBxId, coupDescTxtBxId, dynamicValidityTbId;
	private Radiogroup  addCriteriaRadioId;//coupGenRadioGrId //byshailika
	private Listbox viewSKULbId, dynamicValidityLbId, productCategoryId,comparisionLbId;
	private Listitem vendorCodeCkdId;
	private Datebox createDateBxId, expiryDateBxId;
	private Bandbox bandId;
//	private Textbox dateBoxId;
	//private Textbox singCoupTxtBxId; //byshailika
	private Checkbox accumulateCoupChkBxId, discountedItemsCoupChkBxId;//singCoupChkBxId, multiCoupChkBxId, redeemdsingCoupChkBxId, unlimitSubChkBxId, //byshailika
			
	//private Longbox multiCodLimitLongBxId, singlelimitLongbxId, redeemdlimitLongbxId, perSubUselimitLongbxId;// ,bcwidthLongbxId,bcheightLongbxId; //byshailika
	private static String ERROR_STYLE = "border:1px solid #DD7870;";
	private static String NORMAL_STYLE = "border:1px solid #7F9DB9;";
	private static String DISC_TYPE_PERCENTAGE = "Percentage";
	private static String DISC_TYPE_VALUE = "Value";
	private String discountTypeStr;
	private Hbox listBoxesDivId,quantityhboxId;
	private Div singleSelCoupDivId, multiSelCoupDivId, bcsizeCoupDivId, filtersDivId,
			productDetailtaDivId, discountDivId,couponBasicSettingsFirstId,promotionRulesSecondId,excludeDiv,purIntAttributeDivId,EligibilityAttributeDivId,discountAttributeDivId,
			validateDiv,excludeAlreadyDiscountedItemsDiv,AutoDisDiv;
	private Button genCoupBtnId, selectAllSKUBtnId, deSelectAllSKUBtnId, nextCoupBtnId,
	saveContBtnId,addCritiraBtnId,applyFilterBtnId,submitBtnId,clearBtnId,itemEditWindow$submitInEdit,addDiscountButtonId;
	private Radio  percentRadioId, dollerRadioId, dynamicRadio, staticRadio;//multipleSingleId,oneCombinationId;//singRadioId, multRadioId //byshailika
	private Session sessionScope = null;
	private Paging skuPaging;
	TimeZone clientTimeZone;
	MailingListDao mailingListDao = null;
	Coupons tempCouponObj, editCouponObj;
	SKUTemp editedRuleRowObj;
	List<SKUTemp> editCombinedRuleObj;
	String query = null;
	String countQuery = null;
	Listbox eligibleItemRadioId;
	Listitem higestPriceRadioId, higestPricewithoutRadioId, AllEligileItemsRadioId;
	private static String SELECT_SKU = "SKU";
	private Rows discountGenRowsId,productDiscountGenRowsId,receiptDiscountGenRowsId;
	private Div skuVisibleDivId, skuDivId,//valueCodeDivId,
	productvalueCodeDivId,loyaltyRewardsDivId,purchasesDivId,skuVisibleDivIdNew,useasreferralId;
	private Div mpvDivId, storeSelectDivId, numberEligibleDivId;
	Label totAmtDollerLblId, mintAmtTxtLblId, onReceiptLabelId, onReceiptQuantityLabelId, onProductLabelId,
			onProductQuantityLabelId,multiId;

	private Checkbox onlylpChkBxId, allStoresChkBxId,userefcodeChkBxId, multiplePerReceiptChkBxId, skuCkId;//,fbrId,productonlylpChkBxId;
	private Toolbarbutton advancedSettingsTbBtnId;
//	private Checkbox lpChkBxId ;
	private Intbox lpIBxId;
	private Textbox productlpIBxId;
	private Doublebox quantityBxId;
	List<CouponDiscountGeneration> editCoupDiscGenLst = null;
	private Groupbox selectCriteriaGBId,receiptVisibleGBID, 
	//productVisibleGBID,
	rulesGBId,rulesGBIdNew,discountCodeGenerationGBId,rulesDivId,rulesDivIdNew;
	Radiogroup discountRadioGrId;
	Radio skuRadioId, tpaRadioId;
	String edit;
	private Radiogroup promoRgId;
	private Checkbox auxCheckBox,auxCheckBox1,auxCheckBox2,auxCheckBox3,auxCheckBox4, auxCheckBox5, auxCheckBox6, auxCheckBox7, auxCheckBox8,auxCheckBox9;
	private Listheader listHeaderId,listHeaderId1,listHeaderId2,listHeaderId3,listHeaderId4, listHeaderId5, listHeaderId6, listHeaderId7, listHeaderId8,listHeaderId9;
	// private Checkbox
	// vendorCodeCkId,deptCodeCkId,itemCategoryCkId,dcsCkId,classCkId,subclassCkId;
	// private Listitem
	// vendorCodeCkId,deptCodeCkId,itemCategoryCkId,dcsCkId,classCkId,subclassCkId,skuCkId;
	// private Listitem skuCkId;
	private Listbox purchaseAttrLbId,discountAttrLbId,EligibilityAttrLbId,itemCategoryFilterLBId, vendorCodeFilterLBId, deptCodeFilterLBId, bcDimensionListboxId2,
			classFilterLBId, subClassFilterLBId, dcsFilterLBId, addedSKULBId, descriptionFilterLBId,skuFilterLBId,listPriceFilterLBId,addedSKUSegmentLBId,productvalueCode2ListboxId;

//	private Listbox storeFilterLBId;
//	private Listbox bcDimensionListboxId, bcDimensionListboxId1;
	LBFilterEventListener viewSkuELObj, viewCouponsELObj, vendorCodeFilterObj;

	private static String[] DM_Dimension = { "32X8", "26X12", "36X12", "36X16", "48X16", "16X16", "18X18", "20X20",
			"22X22", "24X24", "26X26", "32X32", "36X36", "40X40", "44X44", "48X48", "52X52", "64X64", "72X72", "80X80",
			"88X88", "96X96", "104X104", "120X120", "132X132", "144X144" };
	private static String[] AZ_Dimension = { "15X15", "19X19", "23X23", "27X27", "31X31", "37X37", "41X41", "45X45",
			"49X49", "53X53", "57X57", "61X61", "67X67", "71X71", "75X75", "79X79", "83X83", "87X87", "91X91", "95X95",
			"101X101", "105X105", "109X109", "113X113", "117X117", "121X121", "125X125", "131X131", "135X135",
			"139X139", "143X143", "147X147", "151X151" };
	private static String[] QR_Dimension = { "40X40", "80X80", "100X100", "120X120", "150X150", "200X200" };
	private static String[] LN_Dimension = { "60X15", "60X20", "75X25", "80X20", "90X30", "100X25", "120X30", "120X40",
			"150X50", "160X40", "200X50" };
	// Map<String, Byte> barCodeMAp

	private final String SEARCH_BY_NAME = "Name";
	private final String SEARCH_BY_STATUS = "Status";
	private final String SEARCH_BY_DATE = "Date";
	private Listbox srchLbId;
	private Listbox codeStatusLb;
	private Textbox searchByPromoCodeNameTbId, vendorCodeTBId, deptCodeTBId, itemCategoryTBId, dcsTBId, classTBId,
			subClassTBId, viewSkuTBId,descriptionTBId,skuTBId,listPriceTBId;
	private MyDatebox fromDateboxId;
	private MyDatebox toDateboxId;
	private Div searchByPromoCodeNameDivId;
	private Div searchByCreatedOnDivId;
	private Div searchByStatusDivId;
	// Hbox LbDivId;
	private String fromDateStr;
	private LoyaltyProgramDao loyaltyProgramDao;
	private String toDateStr;
	private Column column1Id, column2Id, column3Id, column4Id, column5Id, column6Id;
	Doublebox discountDblBxId, maxDiscountDblBxId, quantityDblBxId, itemEditWindow$discountDblBxId,itemEditWindow$quantityDblBxId;
	Longbox totPurAmtLngBxId;
	POSMappingDao posMappingDao = null;
	private Image showFilterImgId, hideFilterImgId,
		vendorFilterImage,descriptionFilterImage,deptCodeFilterImage,itemCategoryFilterImage,
		dcsFilterImage,classFilterImage,subClassFilterImage,skuFilterImage;
	
	private ValueCodesDao valueCodesDao;

	private String userCurrencySymbol = "$ ";

	private Doublebox screenSizeTBId;
	boolean filterFlag =false;
	boolean loadAllColumns=false;
	boolean disableDiscType=false;
	private Window itemEditWindow;
	private Label itemCountLblId;
	private Grid receiptDiscGrid,productDiscGrid;//,productDiscountRules,receiptDiscountRules;
	
	//private String couponRuleStr=null;

	/*
	 * private static String HIGHEST_PRICED_WITH_DISCOUNT =
	 * "HighestPriceItemWithDisount"; private static String ALL_ELIGIBLE_ITEMS =
	 * "AllEligibleItems"; private static String HIGHEST_PRICED_WITHOUT_DISCOUNT =
	 * "HighestPriceItemWithOutDiscount";
	 */
	// private boolean isNewPlugin;

	private Div smartPromoDivId, maximumDiscountDivId, mobileOfferThirdId, quantityDivId;
	String oldDesc = "";
	String ruleType=Constants.STRING_NILL;
	String combined="Combined";
	String multipleSingle="MultipleSingle";
	String DiscountQtyPerItem="Item Quantity to discount";
	String MinReceiptTotal="Minimum Receipt Total";
	String discounttype1="Discount Type";
	SKUTemp skutemp=null;
	
	public CreateCouponController() {
		this.sessionScope = Sessions.getCurrent();
		
		valueCodesDao = (ValueCodesDao) SpringUtil.getBean(OCConstants.VALUE_CODES_DAO);
	}

	private Map<String, String> genFieldSKUMap = new HashMap<String, String>();
	private Map<String, String> itemAttributesMap = new HashMap<String, String>();
	private Map<String, String> segRuleSKUMap = new HashMap<String, String>();
	private Map<String, String> udfRuleSKUMap = new HashMap<String, String>();
	private Map<String, String> udfDisplaySKUMap = new HashMap<String, String>();
	private Map<String, String> segRuleEligibleItemsMap = new HashMap<String, String>();
 	private Map<String, String> eligibleItemsMap = new HashMap<String, String>();
	private Map<String, String> discPerQtyMap = new HashMap<String, String>();
	// static{

	// description
	// }
//	Map<String,Checkbox> checkMap=new LinkedHashMap<>();
	Map<String, Listitem> checkMap = new LinkedHashMap<>();
	Map<String, Listbox> listMap = new LinkedHashMap<>();
	Map<String, String> skufileMap = new LinkedHashMap<>();
	Map<String, String> headerMap = new HashMap<>();
	Map<String, String> dbColumnMap = new HashMap<>();
	
private Map<String, String> tokenMap ;
private Map<String ,String> tokenMapForIyD;
	MailingList posMl = null;
	long orgOwnerUserId = 0;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		users = GetUser.getUserObj();
		super.doAfterCompose(comp);
//		Clients.evalJavaScript("setdate()");
		logger.info("do after compose");

		dbColumnMap.put("item_category","itemCategory");
		dbColumnMap.put("vendor_code","vendorCode");
		dbColumnMap.put("department_code","departmentCode");
		dbColumnMap.put("dcs","DCS");
		dbColumnMap.put("class_code","classCode");
		dbColumnMap.put("subclass_code","subClassCode");
		dbColumnMap.put("description","description");
		dbColumnMap.put("sku","sku");
		dbColumnMap.put("list_price","listPrice");
		
		dbColumnMap.put("total_purchase_amt","total_purchase_amt");
		
		segRuleSKUMap.put("Item Category", "item_category");
		segRuleSKUMap.put("Vendor Code", "vendor_code");
		segRuleSKUMap.put("Department", "department_code");
		segRuleSKUMap.put("Class", "class_code");
		segRuleSKUMap.put("Subclass", "subclass_code");
		segRuleSKUMap.put("DCS", "dcs");
		segRuleSKUMap.put("Description", "description");
		segRuleSKUMap.put("SKU", "sku");
		segRuleSKUMap.put("Item Price", "list_price");

		segRuleEligibleItemsMap.put("ALLEI","all items");
		segRuleEligibleItemsMap.put("HPIWD","highest priced items");
		segRuleEligibleItemsMap.put("HPIWOD","highest priced non discounted items");
		segRuleEligibleItemsMap.put("LPIWD","lowest priced items");
		segRuleEligibleItemsMap.put("LPIWOD","lowest priced non discounted items");

		tokenMap = new HashMap<String, String>();
		tokenMap.put("isetoday", "isiytoday");
		tokenMap.put("isequal", "isiyequal");
		tokenMap.put("isebetween", "isiybetween");
		tokenMap.put("iseonOrAfter", "isiyonorafter");
		tokenMap.put("iseonOrBefore", "isiyonorbefore");
		tokenMap.put("iseafter", "isiyafter");
		tokenMap.put("isebefore", "isiybefore");
		tokenMap.put("iswithinnext_days", "iseqiywithinnext_day");
		tokenMap.put("iswithinnext_weeks", "iseqiywithinnext_week");
		tokenMap.put("iswithinnext_months", "iseqiywithinnext_month");
		tokenMapForIyD=new HashMap<>();
		tokenMapForIyD.put("iswithinnext_days", "iseqwithinnext_days");
		tokenMapForIyD.put("iswithinnext_weeks", "iseqwithinnext_weeks");
		tokenMapForIyD.put("iswithinnext_months", "iseqwithinnext_months");

		genFieldSKUMap.put("Item Attribute", "item attribute");
		genFieldSKUMap.put("Minimum Receipt Total", "totPurchaseAmount::totPurchaseAmountCkId::totPurchaseAmountFilterLBId");
		genFieldSKUMap.put("Item Category", "itemCategory::itemCategoryCkId::itemCategoryFilterLBId");
		genFieldSKUMap.put("Vendor Code", "vendorCode::vendorCodeCkId::vendorCodeFilterLBId");
		genFieldSKUMap.put("Department", "departmentCode::deptCodeCkId::deptCodeFilterLBId");
		genFieldSKUMap.put("Class", "classCode::classCkId::classFilterLBId");
		genFieldSKUMap.put("Subclass", "subClassCode::subclassCkId::subClassFilterLBId");
		genFieldSKUMap.put("DCS", "DCS::dcsCkId::dcsFilterLBId");
		genFieldSKUMap.put("Description", "description::descriptionCkId::descriptionFilterLBId");
		genFieldSKUMap.put("SKU", "sku::descriptionCkId::addedSKULBId");
		genFieldSKUMap.put("Item Price", "itemPrice::itemPriceCkId::itemPriceFilterLBId");

		eligibleItemsMap.put("ALLEI","all items");
		eligibleItemsMap.put("HPIWD","highest priced items");
		eligibleItemsMap.put("HPIWOD","highest priced non discounted items");
		eligibleItemsMap.put("LPIWD","lowest priced items");
		eligibleItemsMap.put("LPIWOD","lowest priced non discounted items");
		
		discPerQtyMap.put("ET","Equals");
		discPerQtyMap.put("LTE","Maximum");
		discPerQtyMap.put("GTE","Minimum");
		discPerQtyMap.put("ALL","All");
		
		String style = "font-weight:bold;font-size:15px;color:#313031;"
				+ "font-family:Arial,Helvetica,sans-serif;align:left";
		PageUtil.setHeader("Create Discount Code", "", style, true);

		clientTimeZone = (TimeZone) Sessions.getCurrent().getAttribute("clientTimeZone");

		HttpServletRequest request = (HttpServletRequest) Executions.getCurrent().getNativeRequest();

		Long timeInLong = request.getSession().getCreationTime();
		logger.info("time In long value is  :: " + timeInLong);
		Calendar cal1 = Calendar.getInstance();
		logger.info(" 1st cal1 get Time is  :: " + cal1.getTime());
		cal1.setTimeInMillis(timeInLong);
		logger.info(" 2nd  cal1 get Time is  :: " + cal1.getTime());
		loyaltyProgramDao = (LoyaltyProgramDao) SpringUtil.getBean(OCConstants.LOYALTY_PROGRAM_DAO);
		couponsDao = (CouponsDao) SpringUtil.getBean("couponsDao");
		couponsDaoForDML = (CouponsDaoForDML) SpringUtil.getBean("couponsDaoForDML");
		coupDiscGenDao = (CouponDiscountGenerateDao) SpringUtil.getBean("coupDiscGenDao");
		coupDiscGenDaoForDML = (CouponDiscountGenerateDaoForDML) SpringUtil.getBean("coupDiscGenDaoForDML");
		posMappingDao = (POSMappingDao) SpringUtil.getBean("posMappingDao");
		loyaltyProgramTierDao = (LoyaltyProgramTierDao) SpringUtil.getBean(OCConstants.LOYALTY_PROGRAM_TIER_DAO); 
		loyaltyCardSetDao = (LoyaltyCardSetDao) SpringUtil.getBean(OCConstants.LOYALTY_CARD_SET_DAO);
		orgId = users.getUserOrganization().getUserOrgId();
		if (orgId == null) {
			logger.debug("no organization exists for this User..");
			return;
		}
		
		//productDetailtaDivId.setVisible(false);
		onCheck$accumulateCoupChkBxId();
		productvalueCodeDivId.setVisible(true);
		
		onSelect$discountTypeLbId();
		
		/**
		 * venkata Commented getAllPromoFromDB();
		 * contactsGridId.setAttribute("defaultOrderBy", "userCreatedDate");
		 * registerEventListner(0, 5); Utility.refreshGridModel(viewCouponsELObj, 0,
		 * true);
		 */
		mailingListDao = (MailingListDao) SpringUtil.getBean("mailingListDao");
//		 posList = mailingListDao.findPOSMailingList(users);
		skuFileDao = (SkuFileDao) SpringUtil.getBean("skuFileDao");

		/**
		 * Venkata Commented setDefaultDimensions();
		 * contactsGridId.setRowRenderer(this);
		 * 
		 * 
		 * //set Default user stores setDeafultStore();
		 */
		// coupDiscGenDaoForDML.deleteAllSkuBy(users.getUserId());
		// selectAllSKUBtnId.setAttribute("selectedAll", false);
		// skuCkId.setChecked(true);
		posMl = mailingListDao.findPOSMailingList(users);
		if (posMl == null)
			posMl = mailingListDao.findUserBCRMList(users);
		//selectAllSKUBtnId.setAttribute("selectedAll", false);
		UsersDao usersDao = (UsersDao) ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
		orgOwnerUserId = usersDao.getOwnerofOrg(users.getUserOrganization().getUserOrgId());

		// TODO set in viewcouponsController
		// sessionScope.setAttribute("EDIT_COUPON_OBJECT", tempCouponObj);

		// adding currency symbol for each country
		String currSymbol = Utility.countryCurrencyMap.get(users.getCountryType());
		if (currSymbol != null && !currSymbol.isEmpty())
			userCurrencySymbol = currSymbol + " ";
		totAmtDollerLblId.setValue(" " + userCurrencySymbol + " ");
		dollerRadioId.setLabel(" " + userCurrencySymbol + " ");

		onReceiptLabelId.setValue("Maximum " + userCurrencySymbol + " Discount");

		editCouponObj = (Coupons) sessionScope.getAttribute("EDIT_COUPON_OBJECT");
		if (editCouponObj != null) {
			tempCouponObj = editCouponObj;
			editCouponSetting(editCouponObj);
			couponBasicSettingsFirstId.setSclass("req_step_current");
			if(editCouponObj.getCouponGeneratedType()!= null && !editCouponObj.getCouponGeneratedType().equals("Draft")) {
				if(editCouponObj.getCouponGeneratedType().trim().equals(Constants.COUP_GENT_TYPE_SINGLE)){
					if((editCouponObj.getCouponCode()==null || editCouponObj.getCouponCode().isEmpty())){
						promotionRulesSecondId.setSclass("req_step_incomplete");
					}else {
						promotionRulesSecondId.setSclass("req_step_completed");
					}
				}else {
					if(editCouponObj.getTotalQty() == null) {
						promotionRulesSecondId.setSclass("req_step_incomplete");
					}else {
						promotionRulesSecondId.setSclass("req_step_completed");
					}
				}
			}else {
				promotionRulesSecondId.setSclass("req_step_incomplete");
			}
			if (editCouponObj.isEnableOffer()) {
				mobileOfferThirdId.setVisible(true);
				if((editCouponObj.getOfferHeading()==null || editCouponObj.getOfferHeading().isEmpty())
						&& (editCouponObj.getOfferDescription() == null || editCouponObj.getOfferDescription().isEmpty())) {
					mobileOfferThirdId.setSclass("req_step_incomplete");
					mobileOfferThirdId.setStyle("margin-left: 3px;background-color: #dfdfdf;width:204px !important;");
				}else {
					mobileOfferThirdId.setStyle("margin-left: 3px;background-color: #c5e3a5;width:204px !important;");
					mobileOfferThirdId.setSclass("req_step_completed");
				}
			} 	
			
			CouponDescriptionAlgorithm coupoDes = new CouponDescriptionAlgorithm();
			oldDesc = coupoDes.preparecouponDisc(editCouponObj, users);
			
			
			disableDiscType=true;
			
		} else {
			nextCoupBtnId.setVisible(false);
			defaultSettings();
			SetItemsToBeDragged(2, purchaseAttrLbId);
			SetItemsToBeDragged(3, discountAttrLbId);
			SetItemsToBeDragged(1,EligibilityAttrLbId);
		}
		onCheck$discountRadioGrId();
		
	} // doAfterCompose
	
	/*private void setValueCode() {

		List<ValueCodes> valueCodes = valueCodesDao.findValueCodeBy(users.getUserOrganization().getUserOrgId());
		if (valueCodes != null && valueCodes.size() > 0) {
				valueCodes.forEach(code -> {
					if(!code.isAssociatedWithFBP()){
					Listitem item = new Listitem(code.getValuCode());
					item.setValue(code.getValuCode());
					item.setParent(valueCodeListboxId);
					}
				});
				valueCodeListboxId.setSelectedIndex(0);
			}
	}*/
	private void setValueCodeOnProduct(){
		List<ValueCodes> valueCodes = valueCodesDao.findValueCodeBy(users.getUserOrganization().getUserOrgId());
		List<Listitem> listitem=productvalueCodeListboxId.getItems();
		List<String> valueCodeList = new ArrayList<String>();
		
		for(Listitem item:listitem){

			String itemStr = item.getValue();

			if(itemStr==null) continue;
			valueCodeList.add(itemStr);
		}
			if (valueCodes != null && valueCodes.size() > 0) {
				valueCodes.forEach(code -> {
					if(!code.isAssociatedWithFBP()){
					if(valueCodeList!=null && !valueCodeList.isEmpty()) {
						if(!valueCodeList.contains(code.getValuCode())) {
							Listitem item = new Listitem(code.getValuCode());
							item.setValue(code.getValuCode());
							item.setParent(productvalueCodeListboxId);
						}
					
					}else {
					Listitem item = new Listitem(code.getValuCode());
					item.setValue(code.getValuCode());
					item.setParent(productvalueCodeListboxId);
					}
					}
				});
				productvalueCodeListboxId.setSelectedIndex(0);
			}
		
	}
	

	public void onCheck$percentRadioId() {
		// if(isNewPlugin){
		onProductLabelId.setVisible(true);
		onReceiptLabelId.setVisible(false);
		// }
	}

	public void onCheck$dollerRadioId() {
		// if(isNewPlugin){
		onProductLabelId.setVisible(false);
		onReceiptLabelId.setVisible(true);
		// }
	}

	public void onCheck$accumulateCoupChkBxId() {

		if (accumulateCoupChkBxId.isChecked()) {
			if (percentRadioId.isChecked()) {
				// if(isNewPlugin){
				onProductLabelId.setVisible(true);
				onReceiptLabelId.setVisible(false);
				// }
			} else if (dollerRadioId.isChecked()) {
				onProductLabelId.setVisible(false);
				onReceiptLabelId.setVisible(true);
			}
			maximumDiscountDivId.setVisible(true);
		} else {

			onProductLabelId.setVisible(false);
			onReceiptLabelId.setVisible(false);
			maximumDiscountDivId.setVisible(false);
		}
	}

	/*public void onSelect$eligibleItemRadioId() {
		Listitem selRadio = eligibleItemRadioId.getSelectedItem();

		if (selRadio == null)
			return;
		if (selRadio == higestPriceRadioId) {
			quantityDivId.setVisible(true);
		} else if (selRadio == higestPricewithoutRadioId) {
			quantityDivId.setVisible(true);
		} else if (selRadio == AllEligileItemsRadioId) {
			quantityDivId.setVisible(false);
		}

	}*/

	public void onClick$saveContBtnId() {
		
		Radio selRadio = discountRadioGrId.getSelectedItem()!=null?discountRadioGrId.getSelectedItem():null;
		if (selRadio == null)
			return;

		if (selRadio == skuRadioId && productDiscountGenRowsId.getVisibleItemCount() == 0) {
				MessageUtil.setMessage("Please add rule(s) to continue.",
						"color:blue", "TOP");
				return;
		}else if(selRadio == tpaRadioId && receiptDiscountGenRowsId.getVisibleItemCount() == 0) {
				MessageUtil.setMessage("Please add rule(s) to continue.",
						"color:blue", "TOP");
				return;
		}
		List<Component> childVboxList = new ArrayList<Component>();
		Div chilVDiv = null;
		if(targetDivHasChildren(purIntAttributeDivId))childVboxList.addAll(purIntAttributeDivId.getChildren());
		//har
		int rulesDragged=0;
		for (Object obj : childVboxList) {
			
			if(obj instanceof Div) {
				
				//boolean isValid = true;
				for(Object object : ((Div)obj).getChildren()) {
					
					if(object instanceof Div) {
						
						chilVDiv = (Div)object;
						if(chilVDiv.getSclass().contains("drop_")) {
							continue;
						}
						List childDivList = chilVDiv.getChildren();
						for (Object objects : childDivList) {
							if(objects instanceof Div ){
								chilVDiv = (Div)objects;
								
								if(chilVDiv.getSclass().contains("drop_")) {
									continue;
								}
								rulesDragged+=1;
							}
						}
					}
				}
			}
		}
		//har
		if(tempCouponObj!=null && rulesDragged>0) {
			/*boolean proceed = validateEditedRuleOnSave();
			if(!proceed) return;*/
			String rule = saveRule(true); 
			if(rule==null) {
				MessageUtil.setMessage("Please add at least one rule.", "color:red;");
				return;
			}
		}
		saveContBtnId.setDisabled(true);

		// Validation
		if (isVaildCoupon(tempCouponObj) == false) {
			saveContBtnId.setDisabled(false);
			return;
		}
		
/*		if(selRadio == skuRadioId && tempCouponObj!=null){
		 if(discountTypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("purchases") && tempCouponObj.getPurchaseQty()==null) {
			MessageUtil.setMessage("Please provide Item Quantity to discount", "color:green", "TOP");
			return;
		}
		}
		if(selRadio == skuRadioId && tempCouponObj!=null) {
			
			if(discountTypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("loyaltyRewards") && tempCouponObj.getRequiredLoyltyPoits()==null) {
			MessageUtil.setMessage("Please provide Item Quantity to discount", "color:green", "TOP");
			return;
				}
		}*/

		try {
			/*
			 * int confirm =
			 * Messagebox.show("Are you sure you want to save the generated Promotion?",
			 * "Save Promotion Generation  ", Messagebox.OK | Messagebox.CANCEL,
			 * Messagebox.QUESTION);
			 */
			int confirm = Messagebox.show("Are you sure you want to save Discount Code?", "Save Discount Code",
					Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
			if (confirm != Messagebox.OK) {
				saveContBtnId.setDisabled(false);
				return;
			}
		} catch (Exception e) {
			logger.error("Exception ::", e);
			saveContBtnId.setDisabled(false);
			return;
		}

		saveContBtnId.setDisabled(false);

		if (skuRadioId.isChecked() && productDiscountGenRowsId.isVisible() && productDiscountGenRowsId.getChildren().size() == 0) {
			MessageUtil.setMessage("Already the Discount Code is saved.", "color:green", "TOP");
			return;
		}
		Components.removeAllChildren(productDiscountGenRowsId);
		if (tpaRadioId.isChecked() && receiptDiscountGenRowsId.isVisible() && receiptDiscountGenRowsId.getChildren().size() == 0) {
			MessageUtil.setMessage("Already the Discount Code is saved.", "color:green", "TOP");
			return;
		}
		Components.removeAllChildren(receiptDiscountGenRowsId);

		// Saving Coupon Obj
		if (tempCouponObj == null) {

			tempCouponObj = saveNewCoupObj();

			if (tempCouponObj == null)
				return;

			MessageUtil.setMessage("New Discount Code saved successfully.", "color:green", "TOP");

		} else {

			saveEditCoupObj(tempCouponObj);
			Sessions.getCurrent().removeAttribute("EDIT_COUPON_OBJECT");
		} // else

		if (!tempCouponObj.getStatus().equals(Constants.COUP_STATUS_DRAFT)
				&& !tempCouponObj.getStatus().equals(Constants.COUP_STATUS_EXPIRED)) {

			CouponCodesUtility ccu = new CouponCodesUtility(tempCouponObj);
			ccu.start();
		}

		/*
		 * totalCount = couponsDao.findTotCountCoupons(users.getUserId());
		 * couponsPagingId.setTotalSize(totalCount); fillCouponsBySize(0,5);
		 * couponsPagingId.setActivePage(0);
		 */
		/**
		 * manageCouponsTabBoxId.setSelectedIndex(0); getAllPromoFromDB();
		 * registerEventListner(0, 5); Utility.refreshGridModel(viewCouponsELObj, 0,
		 * true); onSelect$manageCouponsTabBoxId();
		 */

		sessionScope.setAttribute("EDIT_COUPON_OBJECT", tempCouponObj);
		sessionScope.setAttribute("USE_AS_REFERRAL_CODE", false);
		sessionScope.setAttribute("USE_AS_REFERRAL_CODE", userefcodeChkBxId.isChecked());
		logger.info("checkbox value is "+userefcodeChkBxId.isChecked());
		// TODO Navigate to next(second) page
		Redirect.goTo(PageListEnum.ADMIN_VIEW_CREATECOUPONSUCCEEDING);

	} // onclick$genCoupBtnId

	
	
	public void defaultSettings() {

//		Sessions.getCurrent().removeAttribute("EDIT_COUPON_OBJ");

		coupDiscGenDaoForDML.deleteAllSkuBy(users.getUserId(), null);
		if (posMl == null) {
			MessageUtil.setMessage("Please create POS List.", "color:red", "TOP");
			return;
		}
	/*	int cnt = addedSKULBId.getItemCount();
		for (; cnt > 0; cnt--) {
			addedSKULBId.removeItemAt(cnt - 1);
		}*/
		couponNameTxtBxId.setValue("");
		couponNameTxtBxId.setDisabled(false);
		coupDescTxtBxId.setValue("");


		//percentRadioId.setSelected(true);
		//percentRadioId.setDisabled(false);
		//dollerRadioId.setDisabled(false);
		skuRadioId.setDisabled(false);
		tpaRadioId.setDisabled(false);
		//mpvChkBoxId.setChecked(false);
		//totPurAmtLngBxId.setDisabled(true);


		Components.removeAllChildren(receiptDiscountGenRowsId);
		Components.removeAllChildren(productDiscountGenRowsId);
		//discountDblBxId.setValue(null);
		listOfDisItemSidMap.clear();
		listOfDisTotPurMap.clear();

		//totPurAmtLngBxId.setValue(null);
		tempCouponObj = null;

		/*if (viewSKULbId.getItemCount() > 0) {
			viewSKULbId.setSelectedIndex(-1);
		}*/

		/**
		 * Commented by Venkata bcz this need to be handled in createCouponSucceeding
		 * Page allStoresChkBxId.setChecked(false); addStoreImgId.setVisible(true);
		 * Components.removeAllChildren(selectedStoreLbId);
		 * 
		 * storeNumbCmboBxId.setSelectedItem(null);
		 * Components.removeAllChildren(selectedStoreLbId);
		 */

		/*singCoupTxtBxId.setValue(null);
		singCoupTxtBxId.setDisabled(false);
		singlelimitLongbxId.setValue(null);
		singlelimitLongbxId.setDisabled(false);
		singCoupChkBxId.setChecked(false);
		redeemdlimitLongbxId.setValue(null);
		redeemdlimitLongbxId.setDisabled(false);
		redeemdsingCoupChkBxId.setChecked(false);
		perSubUselimitLongbxId.setValue(1l);
		unlimitSubChkBxId.setChecked(false);
		perSubUselimitLongbxId.setDisabled(false);
		
	//	multRadioId.setDisabled(false);
		multiCodLimitLongBxId.setValue(null);
		multiCoupChkBxId.setChecked(false);*/
	//	onCheck$mpvChkBoxId();
		discountRadioGrId.setSelectedIndex(0);
		//onCheck$();
		//onCheck$coupGenRadioGrId();
	//	onSelect$discountTypeLbId();
		// onCheck$skuCkId();
		// onSelect$skuCkId();
		//setValueCode();discountRadioGrId
		
		/*SetItemsToBeDragged(2, purchaseAttrLbId);
		SetItemsToBeDragged(3, discountAttrLbId);*/

	}

	private void editCouponSetting(Coupons editCouponObj) {

		try {
			

			logger.debug("editing the Promo-code coupon Id " + editCouponObj.getCouponId());
			
			
			addedSKUSegmentLBId.setSelectedIndex(-1);

			// Coupon Name
			couponNameTxtBxId.setValue(editCouponObj.getCouponName().trim());
			couponNameTxtBxId.setDisabled(true);

			// Coupon desc
			coupDescTxtBxId.setValue(
					editCouponObj.getCouponDescription() != null ? editCouponObj.getCouponDescription().trim() : "");

			userefcodeChkBxId.setChecked(editCouponObj.isUseasReferralCode());
			
			// barcode type
		

			skuRadioId.setDisabled(true);
			tpaRadioId.setDisabled(true);

			// Discount Criteria
			String disFirstCriteria = editCouponObj.getDiscountType().trim();

			if (disFirstCriteria.equals(DISC_TYPE_PERCENTAGE)) { // TODO Need to Change Hard code value
				//percentRadioId.setChecked(true);
				// if(isNewPlugin){
				onProductLabelId.setVisible(true);
				onReceiptLabelId.setVisible(false);
				// }
			} else {
				dollerRadioId.setChecked(true);
				// if(isNewPlugin){
				onProductLabelId.setVisible(false);
				onReceiptLabelId.setVisible(true);
				// }
			}
			//percentRadioId.setDisabled(true);
			//dollerRadioId.setDisabled(true);

			String disSecCriteria = editCouponObj.getDiscountCriteria().trim();
			String noOfEligibleItems = editCouponObj.getNoOfEligibleItems() != null
					&& !editCouponObj.getNoOfEligibleItems().isEmpty()
							? editCouponObj.getNoOfEligibleItems().toString().trim()
							: "";

			if (disSecCriteria.equals(SELECT_SKU)) { // TODO Need to Change Hard code value

				/**
				 * Commented by Venkata bcz this need to be handled in createCouponSucceeding
				 * Page storeSelectDivId.setVisible(true);
				 */
				//excludeDiv.setVisible(true);
				//numberEligibleDivId.setVisible(true);
				// onReceiptLabelId.setVisible(false);
				// onProductLabelId.setVisible(true);

				//mpvDivId.setVisible(false);
				//skuVisibleDivIdNew.setVisible(true);
				skuRadioId.setChecked(true);
				productvalueCode2ListboxId.setDisabled(true);

				
				//addItemAttributes();
				//productDetailtaDivId.setVisible(true);
				//onSelect$itemAttributeLbId();
				/*if (dataflag) {
					adjustListBoxes();
					//prepraAllAttributeData();
				}*/
				//receiptVisibleGBID.setVisible(false); byshailika
			//	productVisibleGBID.setVisible(true); byshailika
				
				//oneCombinationId.setDisabled(true);
				//multipleSingleId.setDisabled(true);
				ruleType=Constants.STRING_NILL;
				
				if(editCouponObj.isCombineItemAttributes()){
					//oneCombinationId.setSelected(true);
					ruleType=combined;
				}else{
					//multipleSingleId.setSelected(true);
					ruleType=multipleSingle;
				}
				setValueCodeOnProduct();
				//check and set the Loylty points
				if(editCouponObj.getLoyaltyPoints() != null) {
					
				//	productonlylpChkBxId.setChecked(true);
					productloyatyPntshboxId.setVisible(true);
					productvalueCodeListboxId.setVisible(true);
				}
				
				
				//check
				//if(isNewPlugin && editCouponObj.getValueCode() != null) {
				if( editCouponObj.getValueCode() != null && editCouponObj.getLoyaltyPoints() != null && editCouponObj.getRequiredLoyltyPoits() != null 
						&& editCouponObj.getPurchaseQty()==null) {
			//		productonlylpChkBxId.setChecked(true);
					productvalueCodeDivId.setVisible(true);
					List<Listitem> listitem=productvalueCodeListboxId.getItems();
					for(Listitem item:listitem){

						String itemStr = item.getValue();

						if(itemStr==null) continue;

						if(itemStr!=null && (itemStr).equals(editCouponObj.getValueCode())){
							productvalueCodeListboxId.setSelectedItem(item);
						}
					}
				}
				
				if(editCouponObj.getCouponGeneratedType()!=null && editCouponObj.getCouponGeneratedType().trim().equals(Constants.COUP_GENT_TYPE_MULTIPLE)){
				//	productonlylpChkBxId.setDisabled(true);
					//productlpIBxId.setDisabled(true);
					productvalueCodeListboxId.setDisabled(true);
				}
				if(editCouponObj.getRequiredLoyltyPoits()!=null && editCouponObj.getPurchaseQty()==null) productlpIBxId.setValue(editCouponObj.getRequiredLoyltyPoits().toString());
				logger.info("******"+editCouponObj.getPurchaseQty());
				if(editCouponObj.getPurchaseQty()!=null){
					//fbrId.setChecked(true);
					quantityBxId.setValue(editCouponObj.getPurchaseQty());
					logger.info(editCouponObj.getPurchaseQty());
				}
				//fbrId.setDisabled(true);
				//quantityBxId.setDisabled(true);
			//	productonlylpChkBxId.setDisabled(true);
				//verify the below
				//productlpIBxId.setDisabled(true);
				productvalueCodeListboxId.setDisabled(true);
				
				

			} else {
				
				//excludeDiv.setVisible(false);
				setValueCodeOnProduct();
				//mpvDivId.setVisible(true);
				onCheck$mpvChkBoxId();
				skuVisibleDivIdNew.setVisible(false);
				tpaRadioId.setChecked(true);
				//quantityDivId.setVisible(false);
				/*byshailika
				 * if(!discountTypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("DiscPurchases")){
					
					receiptVisibleGBID.setVisible(true);
					productVisibleGBID.setVisible(false);
					
				}else{
					receiptVisibleGBID.setVisible(false);
					productVisibleGBID.setVisible(true);
				}*/
//				discountSecListBxId.setSelectedIndex(0);
				//productDetailtaDivId.setVisible(false);
				//check and set the Loylty points
				if(editCouponObj.getLoyaltyPoints() != null) {
					
					onlylpChkBxId.setChecked(true);
					loyatyPntshboxId.setVisible(true);
					productvalueCode2ListboxId.setVisible(true);
					productvalueCodeListboxId.setVisible(true);
					productvalueCodeListboxId.setDisabled(true);
				}
				
				
				//check
				//if(isNewPlugin && editCouponObj.getValueCode() != null) {
				if( editCouponObj.getValueCode() != null && editCouponObj.getLoyaltyPoints() != null && (editCouponObj.getRequiredLoyltyPoits() != null || editCouponObj.getMultiplierValue()!=null)) {
					onlylpChkBxId.setChecked(true);
					productvalueCodeDivId.setVisible(true);
					List<Listitem> listitem=productvalueCodeListboxId.getItems();
					for(Listitem item:listitem){

						String itemStr = item.getValue();
						
						if(itemStr==null) continue;

						
						if(itemStr!=null && (itemStr).equals(editCouponObj.getValueCode())){
							productvalueCodeListboxId.setSelectedItem(item);
						}
					}
					productvalueCodeListboxId.setDisabled(true);
				}
				
				if(editCouponObj.getCouponGeneratedType()!=null && editCouponObj.getCouponGeneratedType().trim().equals(Constants.COUP_GENT_TYPE_MULTIPLE)){
					onlylpChkBxId.setDisabled(true);
				}
				if(editCouponObj.getMultiplierValue()!=null) {
					productvalueCode2ListboxId.setSelectedIndex(1);
					multiId.setVisible(true);
					productlpIBxId.setValue(editCouponObj.getMultiplierValue().toString());
				}
				if(editCouponObj.getRequiredLoyltyPoits()!=null) {
					productvalueCode2ListboxId.setSelectedIndex(0);
					multiId.setVisible(false);
					productlpIBxId.setValue(editCouponObj.getRequiredLoyltyPoits().toString());
				}
					//lpIBxId.setValue(editCouponObj.getRequiredLoyltyPoits());

			}

			SetItemsToBeDragged(2, purchaseAttrLbId);
			SetItemsToBeDragged(3, discountAttrLbId);
			SetItemsToBeDragged(1,EligibilityAttrLbId);
			//display udfs as UDF on edit from main screen
			List<POSMapping> posList = posMappingDao.findByType("'" + Constants.POS_MAPPING_TYPE_SKU + "'",
					users.getUserId());
			CouponsEnum udfEnum = null;
			if (posList != null) {
				for (POSMapping obj : posList) {

					String column = obj.getCustomFieldName().trim().toLowerCase();
					if (column.startsWith("udf")) {
						udfDisplaySKUMap.put(obj.getCustomFieldName(), obj.getDisplayLabel());
						udfRuleSKUMap.put(obj.getDisplayLabel(), obj.getCustomFieldName().toLowerCase());
					} 
				
				}
			}
			if(editCouponObj != null) {
				
				setheaders(editCouponObj.getDiscountType());
				//editSegment(editCouponObj.getCouponRule());
				
			}
			String TierName="";
			String CardSetName="";
			coupDiscGenDaoForDML.deleteAllSkuByCouponId(editCouponObj.getCouponId());
			long totSize = coupDiscGenDao.findCountByCoupon(editCouponObj);
			List<CouponDiscountGeneration>  coupDisList  = coupDiscGenDao.findCoupCodesByCouponObj(editCouponObj.getCouponId().toString());
			// coupDiscGenDaoForDML.insertIntoSKUTempby(editCouponObj.getUserId(),editCouponObj.getCouponId());
			coupDiscGenDaoForDML.insertIntoSKUTempby_New(editCouponObj.getUserId(), editCouponObj.getCouponId());
			String value = null;
			Long startId = null;
			String dispRuleFinal="";
			Long endId = null;
			Map<String, String> map = null;
			Map<String, String> ruleMap =null;
			Map<String, Long> skuTempMap=new LinkedHashMap<>();
			Row row = null;
			Label label = null;
			List<SKUTemp> skuList = null;
			if (totSize > 1000) {
				logger.info("list total is size>1000==" + totSize);
				//Components.removeAllChildren(discountGenRowsId);
				Components.removeAllChildren(productDiscountGenRowsId);
				Components.removeAllChildren(receiptDiscountGenRowsId);
				long threshold = 10000;
				long num_of_chunks = (totSize / threshold);
				if (totSize < threshold) {
					num_of_chunks = 1;
				} else if ((totSize % threshold) > 0) {
					num_of_chunks = (totSize / threshold) + 1;
				} else {
					num_of_chunks = (totSize / threshold);
				}
				logger.info("Records per chunk : " + threshold);
				long initialIndex = 0l;
				map = new LinkedHashMap<>();
				//long skuTempID=0l;
				for (int loop_var = 1; loop_var <= num_of_chunks; loop_var++) {
					skuList = coupDiscGenDao.findBySKUTemp(editCouponObj.getCouponId(), initialIndex, threshold);
					Comparator<SKUTemp> comparator = Comparator.comparing(SKUTemp::getSkuTempId);
					SKUTemp minObject = skuList.stream().min(comparator).get();
					if (startId == null)
						// startId=skuList.get(0).getSkuTempId();
						startId = minObject.getSkuTempId();
					endId = skuList.get(skuList.size() - 1).getSkuTempId();
					
					if (skuRadioId.isChecked()) {
						for (SKUTemp sku : skuList) {
							String dispRule="";
							//skuTempID=sku.getSkuTempId();
							ruleMap = new LinkedHashMap<>();
							if(sku.getTotPurchaseAmount()!=null && !sku.getTotPurchaseAmount().toString().isEmpty()) {
								if(!dispRule.contains(MinReceiptTotal))dispRule+=" AND "+MinReceiptTotal+"="+sku.getTotPurchaseAmount();
							}
							if(sku.getItemPrice()!=null && !sku.getItemPrice().toString().isEmpty()) {
								if(!dispRule.contains("Item Price"))dispRule+=" AND Item Price"+sku.getItemPriceCriteria()+sku.getItemPrice();
							}
							//if(sku.getNoOfEligibleItems()!=null && !sku.getNoOfEligibleItems().toString().isEmpty())dispRule+=" AND Eligible Items="+sku.getNoOfEligibleItems();
							//if (isNewPlugin) {
							String tier=sku.getTierNum();
							String card=sku.getCardSetNum();
							if(tier==null){tier="";}
							if(card==null) {card="";}
							if (map.containsKey(sku.getSkuAttribute() + "::" + sku.getDiscount() + "::"
									+ (sku.getMaxDiscount()!=null?sku.getMaxDiscount():null) + "::" + (sku.getQuantity()!=null?sku.getQuantity():null)
								+"::"+	(!tier.equalsIgnoreCase("")?sku.getTierNum():null) + "::"+ (!card.equalsIgnoreCase("")?sku.getCardSetNum():null))) {
								value = (map.get(sku.getSkuAttribute() + "::" + sku.getDiscount() + "::"
										+ (sku.getMaxDiscount()!=null?sku.getMaxDiscount():null) + "::" + (sku.getQuantity()!=null?sku.getQuantity():null)
										+"::"+(!tier.equalsIgnoreCase("")?sku.getTierNum():null) + "::"+ (!card.equalsIgnoreCase("")?sku.getCardSetNum():null)));
								if (value != null  && sku.getSkuValue() != null)
									map.put(sku.getSkuAttribute() + "::" + sku.getDiscount() + "::"
											+ (sku.getMaxDiscount()!=null?sku.getMaxDiscount():null) + "::" + (sku.getQuantity()!=null?sku.getQuantity():null)
											+ "::"+(!tier.equalsIgnoreCase("")?sku.getTierNum():null) + "::"+ (!card.equalsIgnoreCase("")?sku.getCardSetNum():null),
											value.concat(", ") + sku.getSkuValue());
								ruleMap.put(sku.getSkuAttribute() + "::" + sku.getDiscount() + "::"
										+ (sku.getMaxDiscount()!=null?sku.getMaxDiscount():null) + "::" + (sku.getQuantity()!=null?sku.getQuantity():null)+"::"+
												(!tier.equalsIgnoreCase("")?sku.getTierNum():null) + "::"+ (!card.equalsIgnoreCase("")?sku.getCardSetNum():null),
										dispRule);
								skuTempMap.put(sku.getSkuAttribute() + "::" + sku.getDiscount() + "::"
										+ (sku.getMaxDiscount()!=null?sku.getMaxDiscount():null) + "::" + (sku.getQuantity()!=null?sku.getQuantity():null)+"::"+
										(!tier.equalsIgnoreCase("")?sku.getTierNum():null) + "::"+ (!card.equalsIgnoreCase("")?sku.getCardSetNum():null)
										, sku.getSkuTempId());
							} else {
								map.put(sku.getSkuAttribute() + "::" + sku.getDiscount() + "::"
										+ (sku.getMaxDiscount()!=null?sku.getMaxDiscount():null) + "::" + (sku.getQuantity()!=null?sku.getQuantity():null)
										+ "::"+(!tier.equalsIgnoreCase("")?sku.getTierNum():null) + "::"+ (!card.equalsIgnoreCase("")?sku.getCardSetNum():null),
										sku.getSkuValue() != null ? sku.getSkuValue() : "");
								ruleMap.put(sku.getSkuAttribute() + "::" + sku.getDiscount() + "::"
										+ (sku.getMaxDiscount()!=null?sku.getMaxDiscount():null) + "::" + (sku.getQuantity()!=null?sku.getQuantity():null)
										+ "::"+(!tier.equalsIgnoreCase("")?sku.getTierNum():null) + "::"+ (!card.equalsIgnoreCase("")?sku.getCardSetNum():null)
										,dispRule);
								skuTempMap.put(sku.getSkuAttribute() + "::" + sku.getDiscount() + "::"
										+ (sku.getMaxDiscount()!=null?sku.getMaxDiscount():null) + "::" + (sku.getQuantity()!=null?sku.getQuantity():null)
										+ "::"+(!tier.equalsIgnoreCase("")?sku.getTierNum():null) + "::"+ (!card.equalsIgnoreCase("")?sku.getCardSetNum():null)
										,sku.getSkuTempId());
							}
							
					}
				}
				initialIndex = (loop_var * threshold) + 1;
			}
				
				start = startId;
				end = endId;
				limit = start + "::" + end;
				if (skuRadioId.isChecked()) {
					productDiscGrid.setVisible(true);
					receiptDiscGrid.setVisible(false);
					Div tempDiv = null;
				for (Entry<String, String> entry : map.entrySet()) {
					row = new Row();

					String[] entryString = entry.getKey().split("::");

					row.setValue(startId + "::" + endId);
					label = new Label(entryString[1]);
					label.setParent(row);

					String typeCode = entryString[0];
					typeCode = typeCode.contains("Vendor") ? "Vendor Code"
							: typeCode.contains("Department") ? "Department Code"
									: typeCode.contains("Subclass") ? "Sub-Class" : typeCode;
					
					if(typeCode.contains("UDF")||typeCode.contains("udf")){
						typeCode = udfDisplaySKUMap.get(typeCode.toUpperCase());
					}
					POSMapping posMapping = posMappingDao.findRecordBycustomFieldName(typeCode, "SKU", users.getUserId());
					
					String finalRule=posMapping.getDisplayLabel()+" = "+map.get(entry.getKey())+(ruleMap.get(entry.getKey())!=null?ruleMap.get(entry.getKey()):"");
					label = new Label(finalRule.length()>100?finalRule.substring(0,99)+"...":finalRule);
					label.setAttribute("Type", entryString[0]);
					//label.setValue(typeCode+" = "+map.get(entry.getKey())+(ruleMap.get(entry.getKey())!=null?ruleMap.get(entry.getKey()):""));
					label.setParent(row);

					/*label = new Label(typeCode);
					label.setAttribute("Type", entryString[0]);
					label.setParent(row);

					label = new Label(map.get(entry.getKey()));
					label.setParent(row);*/

					//String maxDiscount = entryString.length > 2 && !entryString[2].isEmpty() ? (entryString[2]) : "";
					
                   String quantity = entryString.length > 3 && entryString[3]!="null" ? (entryString[3]) : "";
					
					/*label=new Label(maxDiscount.isEmpty() ? "--" : maxDiscount);
					label.setParent(row);*/
					//if(quantity!=null ? column6Id.setVisible(true) : column6Id.setVisible(false) );
					
					if(quantity==null || quantity.equalsIgnoreCase("null")) quantity="--"; 
					
					label= new Label(quantity!=null && !quantity.isEmpty() ? quantity : "--");
					//label= new Label((quantity==null || quantity.isEmpty()) ? "--" : quantity);
					label.setParent(row);
					/*label= new Label((quantity==null || quantity.isEmpty() )? "--" : quantity);
					label.setParent(row)*/;
					 
					//Hbox hbox = new Hbox();
				
				
			            logger.debug(entryString[4]);
			            logger.debug(entryString[5]);
					 if(!entryString[4].equalsIgnoreCase("null") && entryString[5].equalsIgnoreCase("null")) {
						   // 	String TierName=sku.getTierNum();
						    	String val=loyaltyProgramTierDao.getTierName(entryString[4]);
						    	logger.debug("enter");
								label = new Label(val);
								label.setParent(row);
								
								 label = new Label("--");
		            		    	label.setParent(row);
		                        }
		                     
						    if(!entryString[5].equalsIgnoreCase("null") && entryString[4].equalsIgnoreCase("null")) {
						    //	String CardSetName=sku.getCardSetNum();
						    	
						    	String val=loyaltyCardSetDao.getCardName(entryString[5]);
						    	
						    	 label = new Label("--");
		            		    	label.setParent(row);
		            		    	
								label = new Label(val);
								label.setParent(row);
		                        }
						    if(!entryString[4].equalsIgnoreCase("null") && entryString[5].equalsIgnoreCase("null")) {
							    //	String CardSetName=sku.getCardSetNum();
						    	String val=loyaltyProgramTierDao.getTierName(entryString[4]);
							    	String val1=loyaltyCardSetDao.getCardName(entryString[5]);
							    	
							    	 label = new Label(val);
			            		    	label.setParent(row);
			            		    	
									label = new Label(val1);
									label.setParent(row);
			                        }
					
				//	Hbox hbox = new Hbox();

					tempDiv = new Div();
					//APP-2720
					Image editImg = new Image("/img/email_edit.gif");
					editImg.setStyle("cursor:pointer;margin-right:10px;");
					editImg.setAttribute("COUPON_TYPE", COUPON_RULE_EDIT);
					editImg.addEventListener("onClick", this);
					editImg.setParent(tempDiv);
					//editImg.setParent(hbox);*/

					Image delImg = new Image("img/delt_icn.png");
					delImg.setStyle("cursor:pointer;");
					delImg.setAttribute("COUPON_TYPE", COUPON_RULE_DELETE);
					delImg.addEventListener("onClick", this);
					delImg.setParent(tempDiv);
					tempDiv.setParent(row);
					row.setParent(productDiscountGenRowsId);
					row.setAttribute("skutemp",skuTempMap.get(entry.getKey()));
					row.setAttribute("editedRule",coupDisList);
				}
				}else {
					productDiscGrid.setVisible(false);
					receiptDiscGrid.setVisible(true);
					Div tempDiv=null;
					for (Entry<String, String> entry : map.entrySet()) {
					row = new Row();

					String[] entryString = entry.getKey().split("::");

					row.setValue(startId + "::" + endId);
					label = new Label(entryString[1]);
					label.setParent(row);

					String typeCode = entryString[0];
					label = new Label(typeCode+" = "+map.get(entry.getKey()));
					label.setAttribute("Type", entryString[0]);
					label.setParent(row);

					/*label = new Label(typeCode);
					label.setAttribute("Type", entryString[0]);
					label.setParent(row);

					label = new Label(map.get(entry.getKey()));
					label.setParent(row);*/

					String maxDiscount = entryString.length > 2 && entryString[2]!="null"  ? (entryString[2]) : "";
					String quantity = entryString.length > 3 && entryString[3]!="null" ? (entryString[3]) : "";
					
					/*label=new Label(maxDiscount.isEmpty() ? "--" : maxDiscount);
					label.setParent(row);*/
					if(quantity!=null ? column6Id.setVisible(true) : column6Id.setVisible(false) );
					
					if(quantity==null || quantity.equalsIgnoreCase("null")) quantity="--"; 
					
					label= new Label(quantity!=null && !quantity.isEmpty() ? quantity : "--");
					//label= new Label((quantity==null || quantity.isEmpty()) ? "--" : quantity);
					label.setParent(row);
					
					 if(!entryString[4].equalsIgnoreCase("null") && entryString[5].equalsIgnoreCase("null")) {
						   // 	String TierName=sku.getTierNum();
						    	String val=loyaltyProgramTierDao.getTierName(entryString[4]);
								label = new Label(val);
								label.setParent(row);
								
								 label = new Label("--");
		            		    	label.setParent(row);
		                        }
		                     
						    if(!entryString[5].equalsIgnoreCase("null") && entryString[4].equalsIgnoreCase("null")) {
						    //	String CardSetName=sku.getCardSetNum();
						    	
						    	String val=loyaltyCardSetDao.getCardName(entryString[5]);
						    	
						    	 label = new Label("--");
		            		    	label.setParent(row);
		            		    	
								label = new Label(val);
								label.setParent(row);
		                        }
						    if(!entryString[4].equalsIgnoreCase("null") && entryString[5].equalsIgnoreCase("null")) {
							    //	String CardSetName=sku.getCardSetNum();
						    	String val=loyaltyProgramTierDao.getTierName(entryString[4]);
							    	String val1=loyaltyCardSetDao.getCardName(entryString[5]);
							    	
							    	 label = new Label(val);
			            		    	label.setParent(row);
			            		    	
									label = new Label(val1);
									label.setParent(row);
			                        }
					
				//	Hbox hbox = new Hbox();
					tempDiv=new Div();
					//APP-2720
					Image editImg = new Image("/img/email_edit.gif");
					editImg.setStyle("cursor:pointer;margin-right:10px;");
					editImg.setAttribute("COUPON_TYPE", COUPON_RULE_EDIT);
					editImg.addEventListener("onClick", this);
					editImg.setParent(tempDiv);

					Image delImg = new Image("img/delt_icn.png");
					delImg.setStyle("cursor:pointer;");
					delImg.setAttribute("COUPON_TYPE", COUPON_RULE_DELETE);
					delImg.addEventListener("onClick", this);
					delImg.setParent(tempDiv);
					tempDiv.setParent(row);
					row.setParent(receiptDiscountGenRowsId);
					row.setAttribute("skutemp",skuTempMap.get(entry.getKey()));
					row.setAttribute("editedRule",coupDisList);
				}
				}
			} else {
				logger.info("list size is less then 1000 - " + totSize);
				skuList = coupDiscGenDao.findBySKUTemp(editCouponObj.getCouponId());
				Comparator<SKUTemp> comparator = Comparator.comparing(SKUTemp::getSkuTempId);
				SKUTemp minObject = skuList.stream().min(comparator).get();
				if (start == null)
					// start=skuList.get(0).getSkuTempId();
					startId = minObject.getSkuTempId();
				endId = skuList.get(skuList.size() - 1).getSkuTempId();
				Components.removeAllChildren(receiptDiscountGenRowsId);
				Components.removeAllChildren(productDiscountGenRowsId);
				map = new LinkedHashMap<>();
				//long skuTempID=0l;
				if (skuRadioId.isChecked()) {
					ruleMap = new LinkedHashMap<>();
					Map<String, Long> skuId = new LinkedHashMap<>();
					for (SKUTemp sku : skuList) {
						String dispRule="";
						if(sku.getTotPurchaseAmount()!=null && !sku.getTotPurchaseAmount().toString().isEmpty()) {
							if(!dispRule.contains(MinReceiptTotal))dispRule+=""+MinReceiptTotal+"="+sku.getTotPurchaseAmount();
						}
						if(sku.getItemPrice()!=null && !sku.getItemPrice().toString().isEmpty()) {
							if(!dispRule.contains("Item Price") && dispRule.contains("Minimum Receipt Total") ){
								dispRule+=" AND Item Price"+sku.getItemPriceCriteria()+sku.getItemPrice();
							}
							
							if(!dispRule.contains("Item Price"))dispRule+=" Item Price"+sku.getItemPriceCriteria()+sku.getItemPrice();
						}
						//if(sku.getNoOfEligibleItems()!=null && !sku.getNoOfEligibleItems().toString().isEmpty())dispRule+=" AND Eligible Items="+sku.getNoOfEligibleItems();
						//if (isNewPlugin) {
						String tier=sku.getTierNum();
						String card=sku.getCardSetNum();
						if(tier==null){tier="";}
						if(card==null) {card="";}
						if (map.containsKey(sku.getSkuAttribute() + "::" + sku.getDiscount() + "::"
								+ (sku.getMaxDiscount()!=null?sku.getMaxDiscount():null) + "::" + (sku.getQuantity()!=null?sku.getQuantity():null)
								+ "::"+(!tier.equalsIgnoreCase("")?sku.getTierNum():null) + "::"+ (!card.equalsIgnoreCase("")?sku.getCardSetNum():null))) {
							value = (map.get(sku.getSkuAttribute() + "::" + sku.getDiscount() + "::"
									+ (sku.getMaxDiscount()!=null?sku.getMaxDiscount():null) + "::" + (sku.getQuantity()!=null?sku.getQuantity():null)
									+ "::"+(!tier.equalsIgnoreCase("")?sku.getTierNum():null) + "::"+ (!card.equalsIgnoreCase("")?sku.getCardSetNum():null)));
							if (value != null && sku.getSkuValue() != null)
								map.put(sku.getSkuAttribute() + "::" + sku.getDiscount() + "::"
										+ (sku.getMaxDiscount()!=null?sku.getMaxDiscount():null) + "::" + (sku.getQuantity()!=null?sku.getQuantity():null)
										+ "::"+(!tier.equalsIgnoreCase("")?sku.getTierNum():null) + "::"+ (!card.equalsIgnoreCase("")?sku.getCardSetNum():null)
										,
										value.concat(", ") + sku.getSkuValue());
							ruleMap.put(sku.getSkuAttribute() + "::" + sku.getDiscount() + "::"
									+ (sku.getMaxDiscount()!=null?sku.getMaxDiscount():null) + "::" + (sku.getQuantity()!=null?sku.getQuantity():null)
									+ "::"+(!tier.equalsIgnoreCase("")?sku.getTierNum():null) + "::"+ (!card.equalsIgnoreCase("")?sku.getCardSetNum():null)
									,
									dispRule);
							skuId.put(sku.getSkuAttribute() + "::" + sku.getDiscount() + "::"
									+ (sku.getMaxDiscount()!=null?sku.getMaxDiscount():null) + "::" + (sku.getQuantity()!=null?sku.getQuantity():null)
									+ "::"+(!tier.equalsIgnoreCase("")?sku.getTierNum():null) + "::"+ (!card.equalsIgnoreCase("")?sku.getCardSetNum():null)
									, sku.getSkuTempId());
						} else {
							map.put(sku.getSkuAttribute() + "::" + sku.getDiscount() + "::"
									+ (sku.getMaxDiscount()!=null?sku.getMaxDiscount():null) + "::" + (sku.getQuantity()!=null?sku.getQuantity():null)
									+ "::"+(!tier.equalsIgnoreCase("")?sku.getTierNum():null) + "::"+ (!card.equalsIgnoreCase("")?sku.getCardSetNum():null),(sku.getSkuValue() != null ? sku.getSkuValue() : ""));
							logger.debug(map);
							ruleMap.put(sku.getSkuAttribute() + "::" + sku.getDiscount() + "::"
									+ (sku.getMaxDiscount()!=null?sku.getMaxDiscount():null) + "::" + (sku.getQuantity()!=null?sku.getQuantity():null)
									+ "::"+(!tier.equalsIgnoreCase("")?sku.getTierNum():null) + "::"+ (!card.equalsIgnoreCase("")?sku.getCardSetNum():null)
									, dispRule);
							skuId.put(sku.getSkuAttribute() + "::" + sku.getDiscount() + "::"
									+ (sku.getMaxDiscount()!=null?sku.getMaxDiscount():null) + "::" + (sku.getQuantity()!=null?sku.getQuantity():null)
									+ "::"+(!tier.equalsIgnoreCase("")?sku.getTierNum():null) + "::"+ (!card.equalsIgnoreCase("")?sku.getCardSetNum():null)
									, sku.getSkuTempId());
						}

				}
					start = startId;
					end = endId;
					limit = start + "::" + end;
					
					if(!editCouponObj.isCombineItemAttributes()){
					
					Div tempDiv=null;
					for (Entry<String, String> entry : map.entrySet()) {
						row = new Row();

						String[] entryString = entry.getKey().split("::");

						row.setValue(startId + "::" + endId);
						label = new Label(entryString[1]);
						label.setParent(row);

						String typeCode = entryString[0];
					/*	typeCode = typeCode.contains("Vendor") ? "Vendor Code"
								: typeCode.contains("Department") ? "Department Code"
										: typeCode.contains("Subclass") ? "Sub-Class" : typeCode; */
						
						if(typeCode.contains("UDF")||typeCode.contains("udf")){
							typeCode = udfDisplaySKUMap.get(typeCode.toUpperCase());
						}
						if(!typeCode.equalsIgnoreCase("null") ) {
							String finalRule="";
							logger.debug(ruleMap.get(entry.getKey()));
							POSMapping posMapping = posMappingDao.findRecordBycustomFieldName(typeCode, "SKU", users.getUserId());
							if(posMapping!=null) {
							if(ruleMap.get(entry.getKey())==null || ruleMap.get(entry.getKey()).isEmpty() ) {
								
						 finalRule=posMapping.getDisplayLabel()+" = "+map.get(entry.getKey())+""+(ruleMap.get(entry.getKey())!=null?ruleMap.get(entry.getKey()):"");
							}
							
							else
							{
								 finalRule=posMapping.getDisplayLabel()+" = "+map.get(entry.getKey())+" AND "+(ruleMap.get(entry.getKey())!=null?ruleMap.get(entry.getKey()):"");
							}
							}
							else {
								
								if(ruleMap.get(entry.getKey())==null || ruleMap.get(entry.getKey()).isEmpty() ) {
									
									 finalRule=typeCode+" = "+map.get(entry.getKey())+""+(ruleMap.get(entry.getKey())!=null?ruleMap.get(entry.getKey()):"");
										}
										
										else
										{
											 finalRule=typeCode+" = "+map.get(entry.getKey())+" AND "+(ruleMap.get(entry.getKey())!=null?ruleMap.get(entry.getKey()):"");
										}
										}
								
							
						label = new Label(finalRule.length()>100?finalRule.substring(0,99)+"...":finalRule);
						label.setAttribute("Type", entryString[0]);
						//label.setValue(typeCode+" = "+map.get(entry.getKey())+(ruleMap.get(entry.getKey())!=null?ruleMap.get(entry.getKey()):""));
						label.setParent(row);
						}
						else {
							String finalRule=(ruleMap.get(entry.getKey())!=null?ruleMap.get(entry.getKey()):"");
							label = new Label(finalRule.length()>100?finalRule.substring(0,99)+"...":finalRule);
							label.setAttribute("Type", entryString[0]);
							//label.setValue(typeCode+" = "+map.get(entry.getKey())+(ruleMap.get(entry.getKey())!=null?ruleMap.get(entry.getKey()):""));
							label.setParent(row);
						}
						
					/*	label = new Label(typeCode);
						label.setAttribute("Type", entryString[0]);
						label.setParent(row);

						label = new Label(map.get(entry.getKey()));
						label.setParent(row);*/

						Double discount = Double.parseDouble((entryString[1]));
						//String maxDiscount = entryString.length > 2 && !entryString[2].isEmpty() ? (entryString[2]) : "";
						String quantity = entryString.length > 3 && entryString[3]!="null" ? (entryString[3]) : "";
						/*List<Long> templist = null;
						templist = coupDiscGenDao.findBySkuUserCount(users.getUserId(), discount,
								entry.getKey().split("::")[0],maxDiscount + "", quantity + "",
								tempCouponObj != null ? tempCouponObj.getCouponId() + "" : null);
					
						label = new Label(templist.get(0) != null && !(templist.get(0).toString()).isEmpty()
								? templist.get(0).toString()
								: "--");
						label.setParent(row);*/

						/*label=new Label(maxDiscount.isEmpty() ? "--" : maxDiscount);
						label.setParent(row);				
*/					
						//if(quantity!=null ? column6Id.setVisible(true) : column6Id.setVisible(false) );

						
						if(quantity==null || quantity.equalsIgnoreCase("null")) quantity="--"; 
						
						label= new Label(quantity!=null && !quantity.isEmpty() ? quantity : "--");
						label.setParent(row);
						/*label= new Label((quantity==null || quantity.isEmpty() )? "--" : quantity);
						label.setParent(row)*/;
						
						
						 if(!entryString[4].equalsIgnoreCase("null")&& entryString[5].equalsIgnoreCase("null")) {
							   // 	String TierName=sku.getTierNum();
							    	String val=loyaltyProgramTierDao.getTierName(entryString[4]);
							    	
									label = new Label(val);
									label.setParent(row);
									
									 label = new Label("--");
			            		    	label.setParent(row);
			                        }
			                     
							    if(entryString[4].equalsIgnoreCase("null")&& !entryString[5].equalsIgnoreCase("null")) {
							    //	String CardSetName=sku.getCardSetNum();
							    	
							    	String val=loyaltyCardSetDao.getCardName(entryString[5]);
							    	
							    	 label = new Label("--");
			            		    	label.setParent(row);
			            		    	
									label = new Label(val);
									label.setParent(row);
			                        }
							    if(!entryString[4].equalsIgnoreCase("null") && !entryString[5].equalsIgnoreCase("null")) {
								    //	String CardSetName=sku.getCardSetNum();
							    	String val=loyaltyProgramTierDao.getTierName(entryString[4]);
								    	String val1=loyaltyCardSetDao.getCardName(entryString[5]);
								    	
								    	 label = new Label(val);
				            		    	label.setParent(row);
				            		    	
										label = new Label(val1);
										label.setParent(row);
				                        }
							    if(entryString[4].equalsIgnoreCase("null") && entryString[5].equalsIgnoreCase("null")) {
								    	
								    	 label = new Label("--");
				            		    	label.setParent(row);
				            		    	
										label = new Label("--");
										label.setParent(row);
				                        }
						//}
						tempDiv = new Div();
						//APP-2720
						Image editImg = new Image("/img/email_edit.gif");
						editImg.setStyle("cursor:pointer;margin-right:10px;");
						editImg.setAttribute("COUPON_TYPE", COUPON_RULE_EDIT);
						editImg.addEventListener("onClick", this);
						editImg.setParent(tempDiv);
						
						Image delImg = new Image("img/delt_icn.png");
						delImg.setStyle("cursor:pointer;");
						delImg.setAttribute("COUPON_TYPE", COUPON_RULE_DELETE);
						delImg.addEventListener("onClick", this);
						delImg.setParent(tempDiv);
						tempDiv.setParent(row);
						//hbox.setParent(row);

						row.setParent(productDiscountGenRowsId);
						row.setAttribute("skutemp",skuId.get(entry.getKey()));
						row.setAttribute("editedRule",coupDisList);
					}
				}else if(editCouponObj.isCombineItemAttributes()){
					String typeAttr=null;
					String rule=null;
					//String ruleToInsert=null;
					String discount=null;
					String maxDiscount=null;
					String quantity=null;
					row = new Row();
					Div tempDiv=null;
					Map<String, Long> skuTempId = new LinkedHashMap<>();
					for (Entry<String, String> entry : map.entrySet()) {

						String[] entryString = entry.getKey().split("::");
						discount = entryString[1];
						
						String typeCode = entryString[0];
						typeCode = typeCode.contains("Vendor") ? "Vendor Code"
								: typeCode.contains("Department") ? "Department Code"
										: typeCode.contains("Subclass") ? "Sub-Class" : typeCode;
						
						if(typeCode.contains("UDF")||typeCode.contains("udf")){
							typeCode = udfDisplaySKUMap.get(typeCode.toUpperCase());
						}
						 value=map.get(entry.getKey());

						 //maxDiscount = entryString.length > 2 && !entryString[2].isEmpty() ? (entryString[2]) : "";
						 quantity = entryString.length > 3 && !entryString[3].isEmpty() ? (entryString[3]) : "";
						 
						 
						 boolean replace=false;
							String newRule="";
							if(rule==null){
								rule = typeCode+"="+value;
							}else{
								if(rule.contains(":::")){
									String[] ruleArr = rule.split(":::");
									
									for(String rulee : ruleArr){
										
									String	headerLabel =rulee.split("=")[0];
										
										/*if(headerLabel.trim().equals(typeCode)){
											
											logger.info(""+rulee+","+value);
											newRule+=" ::: "+rulee+","+value;
											replace=true;
											
											
										}else*/
									//{
											
											if(!newRule.isEmpty()){
												
												newRule+=" ::: " +rulee;
												
											}else{
												
												newRule=rulee;
												
											}
										//}
									}
										rule=newRule;	
									
									if(!replace){
										
										rule+= " ::: "+typeCode +"="+value;
										
										
									}
									
									
								}else{
									/*if(rule.split("=")[0].equals(typeCode)){
										
										rule=rule+","+value;
										
										
									}else{*/
										rule+= " ::: "+typeCode +"="+value;
															
									//	}
									
								}
								
							}
							
							if(typeAttr==null){
								typeAttr=entry.getKey().split("::")[0];
							}else{
								typeAttr+=","+entry.getKey().split("::")[0];
							}
						
				
							dispRuleFinal=ruleMap.get(entry.getKey());
							skuTempId.put(rule,skuId.get(entry.getKey()));
					}	
					if(rule.contains(":::")){
						
						rule=rule.replaceAll(":::", " AND ");
					}
					
					row.setValue(startId + "::" + endId);
					label = new Label(discount);
					label.setParent(row);
					
					String finalRule=rule+dispRuleFinal;
					label = new Label(finalRule.length()>100?finalRule.substring(0,99)+"...":finalRule);
					label.setAttribute("Type", typeAttr);
					//label.setValue(rule+dispRule);
					label.setParent(row);
					

					/*label=new Label((maxDiscount==null || maxDiscount.isEmpty())? "--" : maxDiscount);
					label.setParent(row);
					*/
					if(quantity==null || quantity.equalsIgnoreCase("null")) quantity="--"; 
					
					label= new Label(quantity!=null && !quantity.isEmpty() ? quantity : "--");
					//label= new Label((quantity==null || quantity.isEmpty()) ? "--" : quantity);
					label.setParent(row);
					if(TierName.isEmpty()) {  TierName=null;    }
					if(CardSetName.isEmpty()) {  CardSetName=null;    }
				    if( TierName!=null &&  CardSetName==null) {
						   // 	String TierName=sku.getTierNum();
						    	String val=loyaltyProgramTierDao.getTierName(TierName);
						    	
								label = new Label(val);
								label.setParent(row);
								 label = new Label("--");
		            		    	label.setParent(row);
		                        }
		                   
						    if(CardSetName!=null && TierName==null) {
						    //	String CardSetName=sku.getCardSetNum();
						    	String val=loyaltyCardSetDao.getCardName(CardSetName);
						    	 label = new Label("--");
		            		    	label.setParent(row);
								label = new Label(val);
								label.setParent(row);
		                        }
						    if(CardSetName==null && TierName==null) {
							    //	String val=loyaltyCardSetDao.getCardName(CardSetName);
							    	 label = new Label("--");
			            		    	label.setParent(row);
									label = new Label("--");
									label.setParent(row);
			                        }
						    if(CardSetName!=null && TierName!=null) {
						    	String val=loyaltyProgramTierDao.getTierName(TierName);
							    	String val1=loyaltyCardSetDao.getCardName(CardSetName);
							    	 label = new Label(val);
			            		    	label.setParent(row);
									label = new Label(val1);
									label.setParent(row);
			                        }
					//APP-2720
					tempDiv = new Div();
					//Hbox hbox = new Hbox(); 
					Image editImg = new Image("/img/email_edit.gif");
					editImg.setStyle("cursor:pointer;margin-right:10px;");
					editImg.setAttribute("COUPON_TYPE", COUPON_RULE_EDIT);
					editImg.addEventListener("onClick", this);
					editImg.setParent(tempDiv);
					
					Image delImg = new Image("img/delt_icn.png");
					delImg.setStyle("cursor:pointer;");
					delImg.setAttribute("COUPON_TYPE", COUPON_RULE_DELETE);
					delImg.addEventListener("onClick", this);
					delImg.setParent(tempDiv);
					tempDiv.setParent(row);
				//	hbox.setParent(row);

					row.setParent(productDiscountGenRowsId);
					String rul=rule;
					if(rule.contains("AND")){
						rul=rule.replaceAll(" AND ",":::");
					}
					row.setAttribute("skutemp",skuTempId.get(rul));
					row.setAttribute("editedRule",coupDisList);
					
				}
				

					// TODO add the

				}

				else {
					skuList = coupDiscGenDao.findBySKUTemp(editCouponObj.getCouponId());
					Div tempDiv=null;
					for (SKUTemp sku : skuList) {
						//skuTempID=sku.getSkuTempId();
						// coupDiscGenDaoForDML.saveByCollection(skuList);
						comparator = Comparator.comparing(SKUTemp::getSkuTempId);
						minObject = skuList.stream().min(comparator).get();
						if (start == null)
							start = minObject.getSkuTempId();
						end = skuList.get(skuList.size() - 1).getSkuTempId();
						limit = start + "::" + end;
						row = new Row();
						row.setValue(start + "::" + end);

						label = new Label(sku.getDiscount().toString());
						label.setParent(row);

						label = new Label(
								sku.getTotPurchaseAmount() != 0 ? sku.getTotPurchaseAmount().toString() : "N/A");
						label.setParent(row);
						String tier=sku.getTierNum();
						String card=sku.getCardSetNum();
						if (tier==null)
						{
							tier="";
							logger.debug(tier);
						}
						if (card==null)
						{
							card="";
							logger.debug(card);
						}
						if(!tier.isEmpty() && card.isEmpty()) {
							   // 	String TierName=sku.getTierNum();
							    	String val=loyaltyProgramTierDao.getTierName(sku.getTierNum());
							    	
									label = new Label(val);
									label.setParent(row);
									 label = new Label("--");
			            		    	label.setParent(row);
			                        }
			                   
							    if(!card.isEmpty()  && tier.isEmpty()) {
							    //	String CardSetName=sku.getCardSetNum();
							    	String val=loyaltyCardSetDao.getCardName(sku.getCardSetNum());
							    	 label = new Label("--");
			            		    	label.setParent(row);
									label = new Label(val);
									label.setParent(row);
			                        }
							    if(card.isEmpty()  && tier.isEmpty() ) {
								    //	String val=loyaltyCardSetDao.getCardName(CardSetName);
								    	 label = new Label("--");
				            		    	label.setParent(row);
										label = new Label("--");
										label.setParent(row);
				                        }
							    if(!card.isEmpty() && !tier.isEmpty() ) {
							    	String val=loyaltyProgramTierDao.getTierName(sku.getTierNum());
								    	String val1=loyaltyCardSetDao.getCardName(sku.getCardSetNum());
								    	 label = new Label(val);
				            		    	label.setParent(row);
										label = new Label(val1);
										label.setParent(row);
				                        }

						//if(isNewPlugin) {
							
						/*	label=new Label(sku.getMaxDiscount()!=null && !sku.getMaxDiscount().toString().isEmpty() ? sku.getMaxDiscount().toString() : "--");
							label.setParent(row);
						*/	
						/*
						 * else { label=new Label(""); label.setParent(row); }
						 */
						
							/*label=new Label("");
							label.setParent(row);*/
						
						//Hbox hbox = new Hbox();
						tempDiv = new Div();
						//APP-2720
						Image editImg = new Image("/img/email_edit.gif");
						editImg.setStyle("cursor:pointer;margin-right:10px;");
						editImg.setAttribute("COUPON_TYPE", COUPON_RULE_EDIT);
						editImg.addEventListener("onClick", this);
						editImg.setParent(tempDiv);
						

						Image delImg = new Image("img/delt_icn.png");
						delImg.setStyle("cursor:pointer;");
						delImg.setAttribute("COUPON_TYPE", COUPON_RULE_DELETE);
						delImg.addEventListener("onClick", this);
						delImg.setParent(tempDiv);
						
						//hbox.setParent(row);
						tempDiv.setParent(row);
						row.setParent(receiptDiscountGenRowsId);
						row.setAttribute("skutemp",sku.getSkuTempId());
						row.setAttribute("editedRule",coupDisList);
					}
				}
			}
			// TODO set the Grid Headers based on selection

			//enableDisableDiscountCrtiteriaOptions();

			//if (isNewPlugin)
				setSmartPromoValues(editCouponObj);

			if (editCouponObj.isEnableOffer()) {
				mobileOfferThirdId.setVisible(true);
			} else {
				mobileOfferThirdId.setVisible(false);
			}
			
			//byshailika
			/*//Check Radio Button 
			if(editCouponObj.getCouponGeneratedType().trim().equals(Constants.COUP_GENT_TYPE_SINGLE)) {
				
				coupGenRadioGrId.setSelectedIndex(0);
				multiSelCoupDivId.setVisible(false);
				singleSelCoupDivId.setVisible(true);
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
				multRadioId.setDisabled(true);
				singRadioId.setDisabled(true);
		
			}else if(editCouponObj.getCouponGeneratedType().trim().equals(Constants.COUP_GENT_TYPE_MULTIPLE)) {
				coupGenRadioGrId.setSelectedIndex(1);
				singleSelCoupDivId.setVisible(false);
				multiSelCoupDivId.setVisible(true);
				//promoValidityPeriodDivIdDynamic.setVisible(true);
			//	staticRadio.setVisible(true);
				
				multiCodLimitLongBxId.setValue(editCouponObj.getTotalQty());
				multiCoupChkBxId.setChecked(editCouponObj.getAutoIncrCheck());
				
				//disable the next selected index
				multRadioId.setDisabled(true);
				singRadioId.setDisabled(true);
		}
			
		

			
			onCheck$coupGenRadioGrId();*/
			//rulesGBId.setVisible(true);
			//discountCodeGenerationGBId.setVisible(true); //byshailika
			//submitBtnId.setVisible(false);
			//clearBtnId.setVisible(false);
			
			String tempLbl="";
			if(editCouponObj.getCouponGeneratedType().equalsIgnoreCase("single")) {
				if(editCouponObj.getLoyaltyPoints() != null && (editCouponObj.getRequiredLoyltyPoits() != null || editCouponObj.getMultiplierValue()!=null)) {
					if(editCouponObj.getValueCode() != null &&
							!editCouponObj.getValueCode().equals(OCConstants.LOYALTY_POINTS)) {
							if(editCouponObj.getPurchaseQty()!=null){
								tempLbl = "Purchases";
								discountTypeLbId.setSelectedIndex(3);
								discountTypeLbId.getItemAtIndex(3).setVisible(true);
			
							}else{
								tempLbl = "Rewards";
								discountTypeLbId.setSelectedIndex(2);
							}
							
					}else if(editCouponObj.getValueCode() == null || editCouponObj.getValueCode().equals(OCConstants.LOYALTY_POINTS)){
						tempLbl = "Loyalty";
						discountTypeLbId.setSelectedIndex(2);
				}
			}else{
				tempLbl = "Promotions";
				discountTypeLbId.setSelectedIndex(0);
			}

			}else if(editCouponObj.getCouponGeneratedType().equalsIgnoreCase("multiple")){
				
				tempLbl = "Coupons";
				discountTypeLbId.setSelectedIndex(1);
			}

			discountTypeLbId.setDisabled(true);
			onSelect$discountTypeLbId();
			

			/**
			 * Commented by Venkata bcz this need to be handled in createCouponSucceeding
			 * Page onCheck$coupGenRadioGrId();
			 */
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
		}

	} // editCouponSetting

	public void addItemAttributes() {
		// getEnumsByCategoryCode
		try {
			int cnt = itemAttributeLbId.getItemCount();
			for (; cnt > 0; cnt--) {
				itemAttributeLbId.removeItemAt(cnt - 1);
			}
			
			Listitem listItem;
			List<POSMapping> posList = posMappingDao.findByType("'" + Constants.POS_MAPPING_TYPE_SKU + "'",
					users.getUserId());
		
			if (posList != null) {
				for (POSMapping obj : posList) {
					if (genFieldSKUMap.containsKey(obj.getCustomFieldName())) {

						listItem = new Listitem();
						listItem.setValue(obj.getCustomFieldName());
						listItem.setLabel(obj.getDisplayLabel());
					//	listItem.setSelected(true);
						listItem.setAttribute("column", genFieldSKUMap.get(obj.getCustomFieldName()).split("::")[0]);
						listItem.setCheckable(true);
						listItem.setParent(itemAttributeLbId);
						
						String type = listItem.getValue().toString().equals("Vendor Code")?"Vendor":
							listItem.getValue().toString().equals("Department Code") ? "Department"
								: listItem.getValue().toString().equals("Sub-Class") ? "Subclass" : listItem.getValue();
							
						headerMap.put(listItem.getIndex() + 1+"", type);
					} else {
						String column = obj.getCustomFieldName().trim().toLowerCase();
						if (column.startsWith("udf")) {
							listItem = new Listitem();
							// listItem.setValue(obj.getCustomFieldName().trim().toLowerCase()+"");
							listItem.setValue(obj.getCustomFieldName().trim().toLowerCase() + "");
							listItem.setLabel(obj.getDisplayLabel());
							listItem.setCheckable(true);
						//	listItem.setSelected(true);
							listItem.setAttribute("column", column);
							listItem.setParent(itemAttributeLbId);
							headerMap.put(listItem.getIndex() + 1+"", listItem.getValue());
							// genFieldSKUMap.put(obj.getCustomFieldName(), );
						} 
							/* else if(column.equalsIgnoreCase("sku")){
								 
								 listItem = new Listitem();
							  listItem.setValue(obj.getCustomFieldName().trim().toLowerCase()+"");
							  listItem.setLabel(obj.getDisplayLabel()); listItem.setCheckable(true);
							  listItem.setSelected(true); listItem.setAttribute("column",column );
							  listItem.setParent(itemAttributeLbId); 
							  
							 }*/
							 

					}
				

				}
			
			}
			
			for(Listitem item:itemAttributeLbId.getItems()){
				logger.info("index=="+item.getIndex());
				logger.info("value=="+item.getValue());
				if(!item.getValue().toString().toLowerCase().startsWith("udf")){	
					String sku = skufileMap.get(item.getValue());
					Listbox lbtId = listMap.get(sku);
						Listhead head = lbtId.getListhead();
						Listheader header = (Listheader) head.getLastChild();
						header.setValue(item.getIndex()+1+"");
				}
			}
			logger.info("headerMap======"+headerMap);
		} catch (Exception e) {
			logger.info("---" + e);
		}
	}

	public void doSkuFill(Listitem skuitem) {
//	public void onCheck$skuCkId(){
		if (skuitem.isSelected()) {
			viewSkuTBId.setValue("");
			viewSkuTBId.setWidth("70px");
			viewSKULbId.setVisible(true);
			skuDivId.setVisible(true);
			Long totalSize = (Long) skuFileDao.executeQuery("SELECT COUNT(distinct sku) FROM SkuFile  WHERE userId = "
					+ orgOwnerUserId + " and sku is not null").get(0);
			skuPaging.setTotalSize(totalSize.intValue());
			skuPaging.setPageSize(10);
			skuPaging.setActivePage(0);
			skuPaging.setAttribute(LBFilterEventListener.HANDLE_ON_PAGING, "false");
			List<Object> result = skuFileDao.getAllSkuBy(orgOwnerUserId, 0, skuPaging.getPageSize());
			int cnt = viewSKULbId.getItemCount();
			for (; cnt > 0; cnt--) {
				viewSKULbId.removeItemAt(cnt - 1);
			}
			if (result == null)
				return;
			Listitem item = null;
			for (Object data : result) {
				item = new Listitem();
				Object[] obj = (Object[]) data;
				item.setValue(obj);
				new Listcell().setParent(item);
				new Listcell(obj[0].toString()).setParent(item);
				new Listcell(obj[1] != null ? obj[1].toString() : "").setParent(item);
				new Listcell(obj[2] != null ? obj[2].toString() : "").setParent(item);
				item.setParent(viewSKULbId);
			}
		} else {
			viewSKULbId.setVisible(false);
			skuDivId.setVisible(false);
		}
		
	}

	private int totalSize = 1200;

	public void onChange$screenSizeTBId() {
		Double totalWidth = screenSizeTBId.getValue();
		totalSize = totalWidth.intValue();
		logger.info(" ON Change ===========" + totalWidth);
		screenSizeTBId.setVisible(false);
	}

	boolean dataflag = true;

	/*public void prepraAllAttributeData() {
		dataflag = false;
		// List<POSMapping> posList=
		// posMappingDao.findOnlyByGenType(Constants.POS_MAPPING_TYPE_SKU,users.getUserId());
		List<POSMapping> posList = posMappingDao.findByType("'" + Constants.POS_MAPPING_TYPE_SKU + "'",
				users.getUserId());
		String column = null;
		String chId = null;
		String lbId = null;
		Set<Listitem> selectedItems = itemAttributeLbId.getSelectedItems();
		if (posList != null && posList.size() > 0) {
			for (POSMapping posMapping : posList) {
				if (genFieldSKUMap.containsKey(posMapping.getCustomFieldName())) {
					String value = genFieldSKUMap.get(posMapping.getCustomFieldName());
					for (Listitem listitem : selectedItems) {
						//logger.info("+" + listitem.getValue());
						//logger.info("" + posMapping.getCustomFieldName());
						if (listitem.getValue().toString().toLowerCase().equalsIgnoreCase("SKU"))
							continue;--by shailu
						if (listitem.getValue().toString().toLowerCase()
								.equalsIgnoreCase(posMapping.getCustomFieldName())) {
							column = (String) listitem.getAttribute("column");
						//	logger.info("name" + column);
							String arr[] = value.split("::");
							// coulmn=arr[0];
							chId = arr[1];
							// lbId=arr[2];itemCategoryFilterLBId
							// displayAllSKUFileData(column,listMap.get(lbId),checkMap.get(chId));
							String sku = skufileMap.get(posMapping.getCustomFieldName());
							Listbox lbtId = listMap.get(sku);
							if(!lbtId.isVisible()){
							displayAllSKUFileData(column, lbtId);
							}
						}
					}
				} else if (posMapping.getCustomFieldName().trim().toLowerCase().startsWith("udf")) {
					for (Listitem listitem : selectedItems) {
				//		logger.info("+" + listitem.getValue());
					//	logger.info("" + posMapping.getCustomFieldName());
						if (listitem.getValue().toString().toLowerCase()
								.equalsIgnoreCase(posMapping.getCustomFieldName())) {
							column = (String) listitem.getAttribute("column");
						//	logger.info("name" + column);
							String sku = skufileMap.get(posMapping.getCustomFieldName().toLowerCase());
							// Listbox lbtId=listMap.get(sku);
							Listbox lbtId = (Listbox) listitem.getAttribute("AssociatedLB");
							if(!lbtId.isVisible()){
								displayAllSKUFileData(column, lbtId);
								}
						}
					}
				}
			}
		}
	}*/
	public void prepraAllAttributeData(String qryStr) {
		dataflag = false;
		// List<POSMapping> posList=
		// posMappingDao.findOnlyByGenType(Constants.POS_MAPPING_TYPE_SKU,users.getUserId());
		List<POSMapping> posList = posMappingDao.findByType("'" + Constants.POS_MAPPING_TYPE_SKU + "'",
				users.getUserId());
		String column = null;
		String chId = null;
		String lbId = null;
		Set<Listitem> selectedItems = itemAttributeLbId.getSelectedItems();
		if (posList != null && posList.size() > 0) {
			for (POSMapping posMapping : posList) {
				if (genFieldSKUMap.containsKey(posMapping.getCustomFieldName())) {
					String value = genFieldSKUMap.get(posMapping.getCustomFieldName());
					for (Listitem listitem : selectedItems) {
						logger.info("+" + listitem.getValue());
						logger.info("" + posMapping.getCustomFieldName());
						/*if (listitem.getValue().toString().toLowerCase().equalsIgnoreCase("SKU"))
							continue;*/
						if (listitem.getValue().toString().toLowerCase()
								.equalsIgnoreCase(posMapping.getCustomFieldName())) {
							column = (String) listitem.getAttribute("column");
							logger.info("name" + column);
							String arr[] = value.split("::");
							// coulmn=arr[0];
							chId = arr[1];
							// lbId=arr[2];itemCategoryFilterLBId
							// displayAllSKUFileData(column,listMap.get(lbId),checkMap.get(chId));
							String sku = skufileMap.get(posMapping.getCustomFieldName());
							Listbox lbtId = listMap.get(sku);
							//lbtId.setEmptyMessage("no "+listitem.getLabel());
							if(loadAllColumns || (!loadAllColumns && !lbtId.isVisible()))
									displayAllSKUFileData(column, lbtId,qryStr);
						}
					}
				} else if (posMapping.getCustomFieldName().trim().toLowerCase().startsWith("udf")) {
					for (Listitem listitem : selectedItems) {
						logger.info("+" + listitem.getValue());
						logger.info("" + posMapping.getCustomFieldName());
						if (listitem.getValue().toString().toLowerCase()
								.equalsIgnoreCase(posMapping.getCustomFieldName())) {
							column = (String) listitem.getAttribute("column");
							logger.info("name" + column);
							String sku = skufileMap.get(posMapping.getCustomFieldName().toLowerCase());
							// Listbox lbtId=listMap.get(sku);
							Listbox lbtId = (Listbox) listitem.getAttribute("AssociatedLB");
							//if(!lbtId.isVisible()){
						//	if(filterFlag || (!filterFlag && !lbtId.isVisible()))
							if(loadAllColumns || (!loadAllColumns && !lbtId.isVisible()))

								displayAllSKUFileData(column, lbtId,qryStr);
							//	}
						}
					}
				}
			}
		}
	}
/*	public void prepareSKU() {
		MyModel model = new MyModel(50);
		model.setMultiple(true);
		viewSKULbId.setModel(model);
		viewSKULbId.setMultiple(true);
		viewSKULbId.setCheckmark(true);
		viewSKULbId.setItemRenderer(new ListitemRenderer() {
			@Override
			public void render(Listitem item, Object data, int arg) throws Exception {
				item.setCheckable(true);
				if (data instanceof Object[]) {
					Object[] obj = (Object[]) data;
					item.setValue(obj);
					new Listcell().setParent(item);
					new Listcell(obj[0].toString()).setParent(item);
					new Listcell(obj[1] != null ? obj[1].toString() : "").setParent(item);
					new Listcell(obj[2] != null ? obj[2].toString() : "").setParent(item);
				}
			}
		});
		LBFilterEventListener.lbFilterSetup(viewSKULbId);
	}*/

	
	public void adjustListBoxes() {
		
		if(!itemAttributeLbId.getSelectedItems().equals(null) && itemAttributeLbId.getSelectedCount()>0){
		
		int eachListsWidth = totalSize / (itemAttributeLbId.getSelectedCount());
		logger.info("Set Width ::" + eachListsWidth);
	
		for (Listitem item : itemAttributeLbId.getSelectedItems()) {
			/*if (item.getValue().toString().equalsIgnoreCase("SKU"))
				continue; --by shailika*/
			String column = null;
			column = (String) item.getAttribute("column");
			logger.info("name" + column);
			String sku = skufileMap.get(item.getValue());
			Listbox lbtId = listMap.get(sku);
			logger.info(sku);
			logger.info(lbtId);
			//lbtId.setWidth(eachListsWidth + "px");
			lbtId.setWidth("175px");
			if (lbtId.getItemCount() == 0){
				lbtId.setMultiple(false);
			}else 	lbtId.setMultiple(true);

		}
		}

	}
	// for adding item attributes
	

	boolean testFlag=false;
	public void displayAllSKUFileData(String column, Listbox ldId,String qryStr) {
		int cnt = ldId.getItemCount();
		for (; cnt > 0; cnt--) {
			ldId.removeItemAt(cnt - 1);
		}
		if(qryStr!=null && !qryStr.isEmpty()){
			MyModelForCategory model = new MyModelForCategory(50, column, orgOwnerUserId,qryStr);
			model.setMultiple(true);
			ldId.setModel(model);

		}else{
			
			MyModelForCategory model = new MyModelForCategory(50, column, orgOwnerUserId);
			model.setMultiple(true);
			ldId.setModel(model);
		}
		ldId.setVisible(true);
		ldId.setMultiple(true);
		ldId.setItemRenderer(new ListitemRenderer() {
			@Override
			public void render(Listitem item, Object data, int arg) throws Exception {
				if (data instanceof Object[]) {
					item.setCheckable(true);
					Object[] obj = (Object[]) data;
					item.setValue(data);
					new Listcell().setParent(item);
					new Listcell(obj[0].toString()).setParent(item);
					testFlag=true;
				}
				if(data==null){
					logger.info("********data is null*****");
					item.setCheckable(false);
					item.setVisible(false);
					//ldId.removeItemFromSelection(item);
					//item.getFirstChild().setVisible(false);
					//ldId.setMultiple(false);
				}
				if(!testFlag){
					ldId.setMultiple(false);
					ldId.setCheckmark(false);
				}
			}
		});

		LBFilterEventListener.lbFilterSetup(ldId);
	}
	
	public void displayAllSKUFileData(String column, Listbox ldId,String qryStr,Double lesserNum,Double greaterNum) {
		int cnt = ldId.getItemCount();
		for (; cnt > 0; cnt--) {
			ldId.removeItemAt(cnt - 1);
		}
		if(qryStr!=null && !qryStr.isEmpty()){
			MyModelForCategory model = new MyModelForCategory(50, column, orgOwnerUserId,qryStr);
			model.setMultiple(true);
			ldId.setModel(model);

		}else{
			
			MyModelForCategory model = new MyModelForCategory(50, column, orgOwnerUserId);
			model.setMultiple(true);
			ldId.setModel(model);
		}
		ldId.setVisible(true);
		ldId.setMultiple(true);
		ldId.setItemRenderer(new ListitemRenderer() {
			@Override
			public void render(Listitem item, Object data, int arg) throws Exception {
				if (data instanceof Object[]) {
					item.setCheckable(true);
					Object[] obj = (Object[]) data;
					item.setValue(data);
					new Listcell().setParent(item);
					new Listcell(obj[0].toString()).setParent(item);
					testFlag=true;
				}
				if(data==null){
					logger.info("********data is null*****");
					item.setCheckable(false);
					item.setVisible(false);
					//ldId.removeItemFromSelection(item);
					//item.getFirstChild().setVisible(false);
					//ldId.setMultiple(false);
				}
				if(!testFlag){
					ldId.setMultiple(false);
					ldId.setCheckmark(false);
				}
			}
		});

		LBFilterEventListener.lbFilterSetup(ldId);
	}

	public void onClick$addCritiraBtnId() {
		
		
		
		/*if(addCriteriaRadioId.getSelectedItem()==null){
			MessageUtil.setMessage("Please select a criteria to add.",
					"color:blue", "TOP");
			return;
			
		}*/
		
		//if(multipleSingleId.isChecked()){
		if(ruleType.equalsIgnoreCase(multipleSingle)) {
			onCheck$multipleSingleId();
			
		int cnt = addedSKULBId.getItemCount();
		List<Listitem> alreadyitems = null;
		boolean flag = false;
		Listitem li = null;
		Listcell cell = null;
		boolean duflag = false;
		for (Entry<String, Listbox> entry : listMap.entrySet()) {
			
			Listbox listbox = entry.getValue();
			if(listbox.equals(viewSKULbId)) continue;
			Set<Listitem> set = listbox.getSelectedItems();
			if (set != null && set.size() > 0) {
				flag = true;
				for (Listitem item : set) {
					if (item.getValue() == null)
						continue;
					Object[] value = item.getValue();
					Listhead head = listbox.getListhead();
					List<Component> list=head.getChildren();
					Listheader header = (Listheader) head.getLastChild();
					//Listheader header1 = (Listheader) list.get(1);
					duflag = false;
					if (addedSKULBId.getItemCount() > 0) {
						alreadyitems = addedSKULBId.getItems();
						for (Listitem itm : alreadyitems) {
							Listcell headcl = (Listcell) itm.getChildren().get(2);
							Listcell itemcl = (Listcell) itm.getChildren().get(1);
						//	logger.info("headcl==="+headcl.getLabel());
						//	logger.info("header.getLabel()==="+header.getLabel());
						//	logger.info("itemcl.getLabel()==="+itemcl.getLabel());
						//	logger.info("value[0]==="+value[0]);
							
							/*if(itemcl.getLabel().contains("AND") || itemcl.getLabel().contains(",")){
								MessageUtil.setMessage("Multiple single rules cannot be added as you have already added a ONE combination rule.", "color:blue",
										"TOP");
								return;
							}*/
							
							String[] items=itemcl.getLabel().split("=");
							String headLbl=items[0];
							String valueLbl=items[1];
							
							
							
							if (headLbl.equals(header.getLabel()) && valueLbl.equals(value[0])) {
								logger.info("duflag==="+duflag);
								duflag = true;
								break;
							}
							
							/*
							if (headcl.getLabel().equals(header.getLabel()) && itemcl.getLabel().equals(value[0])) {
								duflag = true;
								break;
							}*/
						}
					}
					if (duflag)
						continue;

					li = new Listitem();
					li.addEventListener("onClick", this);
					cell = new Listcell();
					cell.setParent(li);
					
					logger.info("value inserted in the listcell======"+header.getLabel()+"="+value[0].toString());
					cell = new Listcell(header.getLabel()+"="+value[0].toString());
					cell.setAttribute("value", value[0].toString());
					logger.info("setting the attribute of value=="+value[0]);
					if(header.getValue()==null){
						cell.setValue("key::" + "SKU");

					}else{
						logger.info("header.getLabel()==="+header.getLabel());
						logger.info("header.getValue().toString()======"+header.getValue().toString());
						logger.info("key and value======"+headerMap.get(header.getValue().toString()));
						//cell.setValue("key::" + headerMap.get(header.getValue().toString()));
						//cell.setValue("key::" +header.getValue().toString());
						cell.setValue("key::" +header.getLabel());
					}
					
					cell.setParent(li);
				/*	

					cell = new Listcell(value[0].toString());
					cell.setValue("value");
					cell.setParent(li);
					
					logger.info("label---"+header.getLabel());
					cell = new Listcell(header.getLabel());
					//cell = new Listcell();
					if(header.getValue()==null){
						cell.setValue("key::" + "SKU");

					}else{
						
						cell.setValue("key::" + headerMap.get(header.getValue().toString()));
					}
					cell.setParent(li);

					cell = new Listcell(value[1].toString());
					cell.setParent(li);*/
					
					
					cell = new Listcell();
					Image img;
					img = new Image("img/delt_icn.png");
					img.setTooltiptext("Delete");
					img.setStyle("margin-right:3px;cursor:pointer;");
					img.setAttribute("delete", "DELETE_SELECTED");
					img.addEventListener("onClick", this);

					img.setParent(cell);
					cell.setParent(li);
					li.setParent(addedSKULBId);

				}
				listbox.setSelectedIndex(-1);
			}
		}


		if (!flag) {
			MessageUtil.setMessage("Please select at least one attribute code for creating discount rule.",
					"color:blue", "TOP");
			return;
		}
		if (cnt == addedSKULBId.getItemCount()) {
			MessageUtil.setMessage("Code already present in critieria for creating discount rule.", "color:blue",
					"TOP");
			return;
		}
	}//else if(oneCombinationId.isChecked()){
		else if(ruleType.equalsIgnoreCase(combined)) {
		onCheck$oneCombinationId();
		
		int cnt = addedSKULBId.getItemCount();
		List<Listitem> alreadyitems = null;
		boolean flag = false;
		Listitem li = null;
		Listcell cell = null;
		boolean duflag = false;
		String rule=null;
		String headerLabel="";
		String ruleToInsert=null;
		String valueToInsert=null;
	//	String REGEX="$AND$";
		//Pattern pattern = Pattern.compile(REGEX);

		for (Entry<String, Listbox> entry : listMap.entrySet()) {
			
			Listbox listbox = entry.getValue();
			Set<Listitem> set = listbox.getSelectedItems();
			Listhead head = listbox.getListhead();
			List<Component> list=head.getChildren();
			Listheader header = (Listheader) head.getLastChild();
			//String ruleToInsert=null;
			if(set.size()>1) {
				MessageUtil.setMessage("You cannot give more than one value of same attribute .",
						"color:blue", "TOP");
				return;
			}
			Object[] value = null;
			if (set != null && set.size() > 0) {
				flag = true;
				for (Listitem item : set) {
					if (item.getValue() == null)
						continue;
					value=item.getValue();
					duflag = false;
					if (addedSKULBId.getItemCount() > 0) {
						alreadyitems = addedSKULBId.getItems();
						for (Listitem itm : alreadyitems) {
							Listcell itemcl = (Listcell) itm.getChildren().get(1);
							if(itemcl.getLabel().contains(" AND ")){
								MessageUtil.setMessage("Only one AND rule is allowed .",
										"color:blue", "TOP");
								return;
							}
							else{

								MessageUtil.setMessage("ONE combination rule cannot be added as you have already added Multiple single rules.",
										"color:blue", "TOP");
								return;
							}
						
						}
					}
					
					boolean replace=false;
					String newRule="";
					if(rule==null){
						rule = header.getLabel()+"="+value[0].toString();
						
						
					}else{
						if(rule.contains(":::")){
							String[] ruleArr = rule.split(":::");
							
							//String[] ruleArr = pattern.split(rule);
							
							for(String rulee : ruleArr){
								
								headerLabel =rulee.split("=")[0];
								
								/*if(headerLabel.trim().equals(header.getLabel().trim().toString())){
									
									if(!newRule.isEmpty()){
										
										newRule+=" ::: "+rulee+","+value[0].toString();
										replace=true;
									}else{
										newRule=rulee+","+value[0].toString();
										replace=true;
									}
									
									
									
								}*/
								//else{
									
									if(!newRule.isEmpty()){
										
									//	newRule+="[AND]" +rulee;
										newRule+=" ::: " +rulee;
										
									}else{
										
										newRule=rulee;
									
									}
								//}
							}
								rule=newRule;	
							
							if(!replace){
								
								if(rule!=null && !rule.isEmpty()){
									
									rule+= " ::: "+header.getLabel()+"="+value[0].toString();
								}else{
									rule=header.getLabel()+"="+value[0].toString();
								}
							
							}
							
							
						}else{
							/*if(rule.split("=")[0].equals(header.getLabel())){
								
								rule=rule+","+value[0].toString();
								
								
							}*/
						//	else{
								if(rule!=null && !rule.trim().isEmpty()){
									rule+= " ::: "+header.getLabel()+"="+value[0].toString();
									
								}else{
									rule= header.getLabel()+"="+value[0].toString();

								}
										
								//}
							
						}
						
					}
					
					
					
				}
				//logger.info("rule to be displayed=="+rule);
				//
					
			}
			
			if(rule!=null){
			if(rule.contains(header.getLabel())){
				
				ruleToInsert=rule;
				if(ruleToInsert.contains(":::")){
					
					
					for(String separateRule : rule.split(":::")){
					//for(String separateRule : pattern.split(rule)){
						if(separateRule.contains("=")){
						String attribute =separateRule.split("=")[0];
						String value1=separateRule.split("=")[1];
						/*if(value1.contains(",")){
							String[] valuesArr = value1.split(",");
							String comma=null;
							for(String valueName : valuesArr){
								
								if(comma==null){
									
									comma= attribute +"="+ valueName;
								}else {
									comma+=" ::: "+ attribute +"="+ valueName;
								}
								
								
							}
							
							ruleToInsert=ruleToInsert.replace(separateRule,comma);
						}*/
					}
					}
				}else{

					if(rule.contains("=")){

						String attribute=rule.split("=")[0];
						String value1 = rule.split("=")[1];
						/*if(value1.contains(",")){
							String[] valuesArr = value1.split(",");
							String comma=null;
							for(String valueName : valuesArr){
								
								if(comma==null){
									
									comma= attribute +"="+ valueName;
								}else {
									comma+=","+ attribute +"="+ valueName;
								}
								
								
							}
							ruleToInsert=ruleToInsert.replace(rule,comma);
						}*/
						
						
					
					}
					
					
				
				}
				
				
			}
			//logger.info(header.getLabel()+"==="+header.getValue().toString()+"=="+headerMap.get(header.getValue().toString()));
			//logger.info("before replace-===="+ruleToInsert);
			//ruleToInsert=ruleToInsert.replaceAll(header.getLabel(), headerMap.get(header.getValue().toString()));
			//logger.info("ruletoinsert1==+ "+ruleToInsert);
			}
			
			
			listbox.setSelectedIndex(-1);
		}
		for (Entry<String, Listbox> entry : listMap.entrySet()) {
			
			Listbox listbox = entry.getValue();
			Set<Listitem> set = listbox.getSelectedItems();
			Listhead head = listbox.getListhead();
			List<Component> list=head.getChildren();
			Listheader header = (Listheader) head.getLastChild();
			//logger.info("before replace-===="+ruleToInsert);

			ruleToInsert=ruleToInsert.replaceAll(header.getLabel(), headerMap.get(header.getValue().toString()));
			
			//logger.info("ruletoinsert1==+ "+ruleToInsert);

			
		}
		
		if(ruleToInsert.contains(":::")){
			
			ruleToInsert=ruleToInsert.replaceAll(":::", ",,,");
		}
		
		logger.info("ruletoinsert2==+ "+ruleToInsert);
		if(rule.contains(":::")) rule=rule.replaceAll(":::", " AND ");
		
		
		if (!flag) {
			MessageUtil.setMessage("Please select at least one attribute code for creating discount rule.",
					"color:blue", "TOP");
			return;
		}
		
		/*String insert=null;
		String[] newArr = ruleToInsert.split(",");
				for (String string : newArr) {
					if(string.contains("=")){
						String item=string.split("=")[0];
						String itemCode=string.split("=")[1];
						if(insert==null){
							insert=item +"=" +itemCode;
						}else {
							if()
						}
					}
				}*/
		
		
		
					/*if (duflag){
						if (!flag) {
							MessageUtil.setMessage("Please select at least one attribute code for creating discount rule.",
									"color:blue", "TOP");
							return;
						}
						
					}*/
		if(ruleToInsert.contains("Vendor Code")){
			ruleToInsert= ruleToInsert.replaceAll("Vendor Code", "Vendor");
		}
		if(ruleToInsert.contains("Dept. Code")){
			ruleToInsert=ruleToInsert.replaceAll("Dept. Code", "Department");
		}
		if(ruleToInsert.contains("Sub-Class")){
			ruleToInsert= ruleToInsert.replaceAll("Sub-Class", "Subclass");
		}
		
		
	
					li = new Listitem();
					li.addEventListener("onClick", this);
					cell = new Listcell();
					cell.setParent(li);
					
					logger.info("key =="+ruleToInsert);
					logger.info("rule ==="+rule);
					cell = new Listcell(rule);
				//	cell.setValue(rule);
				//	cell.setAttribute("value", valueToInsert);
					cell.setValue("key::" +ruleToInsert);
					
					
					cell.setParent(li);
				
					
					
					cell = new Listcell();
				/*	
					//APP-2720
					Image editImg;
					editImg = new Image("/img/email_edit.gif");
					editImg.setTooltiptext("Edit");
					editImg.setStyle("margin-right:3px;cursor:pointer;");
					editImg.setAttribute("editItem", "EDIT_SELECTED");
					editImg.addEventListener("onClick", this);
					editImg.setParent(cell);*/
					
					Image img;
					img = new Image("img/delt_icn.png");
					img.setTooltiptext("Delete");
					img.setStyle("margin-right:3px;cursor:pointer;");
					img.setAttribute("delete", "DELETE_SELECTED");
					img.addEventListener("onClick", this);

					img.setParent(cell);
					cell.setParent(li);
					li.setParent(addedSKULBId);

				
			
		

		
	
		
		
		
	}
		resetCheckBox();
	}
	
	boolean fromMultipleCheck;
	boolean fromOneCombCheck;
	public void onCheck$oneCombinationId(){
		fromOneCombCheck = false;
		if (addedSKUSegmentLBId.getItemCount() > 0 && !fromMultipleCheck)  {
			MessageUtil.setMessage("One combination rule cannot be added as you have already added multiple single rules.",
					"color:blue", "TOP");
			//oneCombinationId.setChecked(false);
			fromOneCombCheck=true;
			//multipleSingleId.setChecked(true);
			ruleType=multipleSingle;
			return;
	}
	//	productonlylpChkBxId.setDisabled(false);
		productlpIBxId.setDisabled(false);
		productvalueCodeListboxId.setDisabled(false);
		quantityBxId.setDisabled(false);
		//fbrId.setDisabled(false);
	
	}
	public void onCheck$multipleSingleId(){
		fromMultipleCheck=false;
		if (addedSKUSegmentLBId.getItemCount() > 0 && !fromOneCombCheck) {
			MessageUtil.setMessage("Multiple single rules cannot be added as you have already added a ONE combination rule.",
					"color:blue", "TOP");
			//multipleSingleId.setChecked(false);
			fromMultipleCheck=true;
			//oneCombinationId.setChecked(true);
			ruleType=combined;
			return;
		}
		//fbrId.setDisabled(true);
		//fbrId.setChecked(false);
		quantityBxId.setDisabled(false);
	}

	public void onClick$selectAllSKUBtnId() {
		viewSKULbId.selectAll();
		selectAllSKUBtnId.setVisible(false);
		logger.info(" == number of selected items == " + viewSKULbId.getSelectedCount());
		deSelectAllSKUBtnId.setVisible(true);
		selectAllSKUBtnId.setAttribute("selectionEventSource", true);
		deSelectSkuMap.clear();
	}

	public void onClick$deSelectAllSKUBtnId() {
		viewSKULbId.clearSelection();
		selectAllSKUBtnId.setVisible(true);
		deSelectAllSKUBtnId.setVisible(false);
		selectAllSKUBtnId.setAttribute("selectionEventSource", false);
	}

	private void registerEventListner(int strtIdx, int endIdx) {
		try {

			String value = srchLbId.getSelectedItem().getValue().toString();
			if (value.equals(SEARCH_BY_NAME)) {

				filterQuery = "FROM Coupons  WHERE userId =" + users.getUserId().longValue() + " AND couponName LIKE '%"
						+ searchByPromoCodeNameTbId.getValue().trim() + "%'" + "  ";
			}

			else if (value.equals(SEARCH_BY_DATE)) {
				validateSetCreationDate();
				filterQuery = "FROM Coupons WHERE userId =" + users.getUserId().longValue()
						+ " AND userCreatedDate between '" + fromDateStr + "' AND '" + toDateStr + "' ";
			}

			else if (value.equals(SEARCH_BY_STATUS)) {
				String status = codeStatusLb.getSelectedItem().getLabel();
				if (status.equals("All")) {
					filterQuery = " FROM  Coupons  WHERE userId =" + users.getUserId().longValue();
				} else {
					filterQuery = "FROM Coupons  WHERE userId =" + users.getUserId().longValue() + " AND status LIKE '"
							+ status + "' ";
				}
				// couponsCampList = couponsDao.getCouponsByStatus(users.getUserId(),
				// codeStatusLb.getSelectedItem().getLabel(), strtIdx, endIdx);

			}
			filterCountQuery = " SELECT COUNT(userId) " + filterQuery;

			qryPrefix = "";

			// contactsGridId.setItemRenderer();
			logger.info("filterCountQuery is ::" + filterCountQuery);
			logger.info("filterQuery is ::" + filterQuery);

			Map<Integer, Field> objMap1 = new HashMap<Integer, Field>();

			objMap1.put(1, Coupons.class.getDeclaredField("couponName"));
			objMap1.put(2, Coupons.class.getDeclaredField("couponDescription"));
			// objMap1.put(3, Coupons.class.getDeclaredField("couponGeneratedType"));
			objMap1.put(4, Coupons.class.getDeclaredField("userCreatedDate"));
			// objMap1.put(5, Coupons.class.getDeclaredField("status"));
			// objMap1.put(7, Coupons.class.getDeclaredField("couponExpiryDate"));
			objMap1.put(8, Coupons.class.getDeclaredField("totalQty"));
			couponsPagingId.setPageSize(Integer.parseInt(memberPerPageLBId.getSelectedItem().getLabel()));

			viewCouponsELObj = LBFilterEventListener.grFilterSetup(contactsGridId, couponsPagingId, filterQuery,
					filterCountQuery, qryPrefix, objMap1);
		} catch (Exception e) {
			logger.error("Exception :: ", e);
		}

	}

	
/*
	public void onClick$vendorFilterImage() throws Exception {
		String searchQuery = "";
		List<Listitem> items = vendorCodeFilterLBId.getItems();
		String data = "";
		if (vendorCodeTBId.getValue().trim().length() > 0) {
			searchQuery = vendorCodeTBId.getValue().trim().toUpperCase();
			for (Listitem item : items) {
				Listcell cell = (Listcell) item.getLastChild();
				data = cell.getLabel().replaceAll("\\s+", " ");
				if (data.toUpperCase().contains(searchQuery))
					item.setVisible(true);
				else
					item.setVisible(false);
			}
		} else {
			for (Listitem item : items) {
				item.setVisible(true);
			}
		}
	}
	public void onClick$deptCodeFilterImage() throws Exception{
		String searchQuery = "";
		List<Listitem> items = deptCodeFilterLBId.getItems();
		String data = "";
		if (deptCodeTBId.getValue().trim().length() > 0) {
			searchQuery = deptCodeTBId.getValue().trim().toUpperCase();
			for (Listitem item : items) {
				Listcell cell = (Listcell) item.getLastChild();
				data = cell.getLabel().replaceAll("\\s+", " ");
				if (data.toUpperCase().contains(searchQuery))
					item.setVisible(true);
				else
					item.setVisible(false);
			}
		} else {
			for (Listitem item : items) {
				item.setVisible(true);
			}
		}
	}
	public void onClick$itemCategoryFilterImage(){
		String searchQuery = "";
		List<Listitem> items = itemCategoryFilterLBId.getItems();
		String data = "";
		if (itemCategoryTBId.getValue().trim().length() > 0) {
			searchQuery = itemCategoryTBId.getValue().trim().toUpperCase();
			for (Listitem item : items) {
				Listcell cell = (Listcell) item.getLastChild();
				data = cell.getLabel().replaceAll("\\s+", " ");
				if (data.toUpperCase().contains(searchQuery))
					item.setVisible(true);
				else
					item.setVisible(false);
			}
		} else {
			for (Listitem item : items) {
				item.setVisible(true);
			}
		}
	}
	public void onClick$dcsFilterImage(){
		String searchQuery = "";
		List<Listitem> items = dcsFilterLBId.getItems();
		String data = "";
		
		if (dcsTBId.getValue().trim().length() > 0) {
			searchQuery = dcsTBId.getValue().trim().toUpperCase();
			for (Listitem item : items) {
				Listcell cell = (Listcell) item.getLastChild();
				data = cell.getLabel().replaceAll("\\s+", " ");
				if (data.toUpperCase().contains(searchQuery))
					item.setVisible(true);
				else
					item.setVisible(false);
			}
		} else {
			for (Listitem item : items) {
				item.setVisible(true);
			}
		}
	}
	
	public void onClick$classFilterImage(){
		String searchQuery = "";
		List<Listitem> items = classFilterLBId.getItems();
		String data = "";
		

		if (classTBId.getValue().trim().length() > 0) {
			searchQuery = classTBId.getValue().trim().toUpperCase();
			for (Listitem item : items) {
				Listcell cell = (Listcell) item.getLastChild();
				data = cell.getLabel().replaceAll("\\s+", " ");
				if (data.toUpperCase().contains(searchQuery))
					item.setVisible(true);
				else
					item.setVisible(false);
			}
		} else {
			for (Listitem item : items) {
				item.setVisible(true);
			}
		}
	}
	public void onClick$subClassFilterImage(){
		String searchQuery = "";
		List<Listitem> items = subClassFilterLBId.getItems();
		String data = "";
		
		if (subClassTBId.getValue().trim().length() > 0) {
			searchQuery = subClassTBId.getValue().trim().toUpperCase();
			for (Listitem item : items) {
				Listcell cell = (Listcell) item.getLastChild();
				data = cell.getLabel().replaceAll("\\s+", " ");
				if (data.toUpperCase().contains(searchQuery))
					item.setVisible(true);
				else
					item.setVisible(false);
			}
		} else {
			for (Listitem item : items) {
				item.setVisible(true);
			}
		}
	}
	public void onClick$descriptionFilterImage(){
			String searchQuery = "";
			List<Listitem> items = descriptionFilterLBId.getItems();
			String data = "";
			if (descriptionTBId.getValue().trim().length() > 0) {
				searchQuery = descriptionTBId.getValue().trim().toUpperCase();
				for (Listitem item : items) {
					Listcell cell = (Listcell) item.getLastChild();
					data = cell.getLabel().replaceAll("\\s+", " ");
					if (data.toUpperCase().contains(searchQuery))
						item.setVisible(true);
					else
						item.setVisible(false);
				}
			} else {
				for (Listitem item : items) {
					item.setVisible(true);
				}
			}
		}
	
	public void onClick$skuFilterImage(){
		
		String searchQuery = "";
		List<Listitem> items = skuFilterLBId.getItems();
		String data = "";
		if (skuTBId.getValue().trim().length() > 0) {
			searchQuery = skuTBId.getValue().trim().toUpperCase();
			for (Listitem item : items) {
				Listcell cell = (Listcell) item.getLastChild();
				data = cell.getLabel().replaceAll("\\s+", " ");
				if (data.toUpperCase().contains(searchQuery))
					item.setVisible(true);
				else
					item.setVisible(false);
			}
		} else {
			for (Listitem item : items) {
				item.setVisible(true);
			}
		}
	
		
	}
	
	public void onClick$udfFilterImage(){
		String searchQuery = "";
		String data = "";
		//logger.info("udfs===");
		List<Listbox> udfs = getUDFLbs();
		if (udfs != null && !udfs.isEmpty()) {

			for (Listbox listbox : udfs) {

				List<Listitem> items = listbox.getItems();
				Textbox tb = (Textbox) listbox.getAttribute("Textbox");
				if (tb.getValue().trim().length() > 0) {
					searchQuery = tb.getValue().trim().toUpperCase();
					for (Listitem item : items) {
						Listcell cell = (Listcell) item.getLastChild();
						data = cell.getLabel().replaceAll("\\s+", " ");
						if (data.toUpperCase().contains(searchQuery))
							item.setVisible(true);
						else
							item.setVisible(false);
					}
				} else {
					for (Listitem item : items) {
						item.setVisible(true);
					}
				}

			}

		}
	}*/


	
	public void onClick$classFilterImage(){
		String searchStr = "";
		searchStr = classTBId.getValue().trim();
		if (searchStr.length() > 0) {
			String qryStr=" And classCode like'%"+searchStr+"%' ";
			displayAllSKUFileData("classCode", classFilterLBId, qryStr);
		}else{
			displayAllSKUFileData("classCode", classFilterLBId, null);

		}
	}


	public void onClick$vendorFilterImage() throws Exception {
		String searchStr = "";
		searchStr = vendorCodeTBId.getValue().trim();
		if (searchStr.length() > 0) {
			String qryStr=" And vendorCode like'%"+searchStr+"%' ";
			displayAllSKUFileData("vendorCode", vendorCodeFilterLBId, qryStr);
		}else{
			displayAllSKUFileData("vendorCode", vendorCodeFilterLBId, null);

		}
	}
	
	public void onClick$deptCodeFilterImage() throws Exception{
		String searchStr = "";
		searchStr = deptCodeTBId.getValue().trim();
		if (searchStr.length() > 0) {
			String qryStr=" And departmentCode like'%"+searchStr+"%' ";
			displayAllSKUFileData("departmentCode", deptCodeFilterLBId, qryStr);
		}else{
			displayAllSKUFileData("departmentCode", deptCodeFilterLBId, null);

		}
	}
	
	public void onClick$itemCategoryFilterImage(){
		String searchStr = "";
		searchStr = itemCategoryTBId.getValue().trim();
		if (searchStr.length() > 0) {
			String qryStr=" And itemCategory like'%"+searchStr+"%' ";
			displayAllSKUFileData("itemCategory", itemCategoryFilterLBId, qryStr);
		}else{
			displayAllSKUFileData("itemCategory", itemCategoryFilterLBId, null);

		}
	}
	
	public void onClick$dcsFilterImage(){
		String searchStr = "";
		searchStr = dcsTBId.getValue().trim();
		if (searchStr.length() > 0) {
			String qryStr=" And DCS like'%"+searchStr+"%' ";
			displayAllSKUFileData("DCS", dcsFilterLBId, qryStr);
		}else{
			displayAllSKUFileData("DCS", dcsFilterLBId, null);

		}
	}
	
	public void onClick$subClassFilterImage(){
		String searchStr = "";
		searchStr = subClassTBId.getValue().trim();
		if (searchStr.length() > 0) {
			String qryStr=" And subClassCode like'%"+searchStr+"%' ";
			displayAllSKUFileData("subClassCode", subClassFilterLBId, qryStr);
		}else{
			displayAllSKUFileData("subClassCode", subClassFilterLBId, null);

		}
	}
	
	/*public void onClick$listPriceFilterImage(){
		String searchStr = "";
		searchStr = listPriceTBId.getValue().trim();
		if (searchStr.length() > 0) {
			String qryStr=" And listPrice like'%"+searchStr+"%' ";
			displayAllSKUFileData("listPrice", listPriceFilterLBId, qryStr);
		}else{
			displayAllSKUFileData("listPrice", listPriceFilterLBId, null);

		}
	}*/
	/*public void onClick$listPriceFilterImage(){
		String searchStr = "";
		searchStr = listPriceTBId.getValue().trim();
		String qryStr="";
		if(searchStr.length()>0 && searchStr.contains(",") && 
				!searchStr.split(",")[0].isEmpty() && !searchStr.split(",")[1].isEmpty()){
			Double lesserNumber=Double.parseDouble(searchStr.split(",")[0]);
			Double greaterNumber=Double.parseDouble(searchStr.split(",")[1]);
			qryStr=" And listPrice > "+lesserNumber+" AND listPrice <"+ greaterNumber;
			displayAllSKUFileData("listPrice", listPriceFilterLBId, qryStr);
		}else if(searchStr.length()>0){
			
			qryStr=" And listPrice like'%"+searchStr+"%' ";
			displayAllSKUFileData("listPrice", listPriceFilterLBId, qryStr);

		}else{
			displayAllSKUFileData("listPrice", listPriceFilterLBId, null);
		}
	}
	*/
	public void onClick$listPriceFilterImage(){
		String searchStr = "";
		searchStr = listPriceTBId.getValue().trim();
		String qryStr="";
		if(searchStr.length()>0 && searchStr.contains(",") && 
				!searchStr.split(",")[0].isEmpty() && !searchStr.split(",")[1].isEmpty()){
			Double lesserNumber=Double.parseDouble(searchStr.split(",")[0]);
			Double greaterNumber=Double.parseDouble(searchStr.split(",")[1]);
			qryStr=" And listPrice > "+lesserNumber+" AND listPrice <"+ greaterNumber;
			displayAllSKUFileData("listPrice", listPriceFilterLBId, qryStr);
		}else if(searchStr.length()>0 && searchStr.contains(">")){

			//Double lesserNumber=Double.parseDouble(searchStr.split(",")[0]);
			Double lesserNumber=Double.parseDouble(searchStr.substring(1));
			qryStr=" And listPrice > "+lesserNumber;
			displayAllSKUFileData("listPrice", listPriceFilterLBId, qryStr);
		
			
		}else if(searchStr.length()>0 && searchStr.contains("<")){
			
			Double greaterNumber=Double.parseDouble(searchStr.substring(1));
			qryStr=" And listPrice < "+greaterNumber;
			displayAllSKUFileData("listPrice", listPriceFilterLBId, qryStr);
			
		}else if(searchStr.length()>0){
			
			
			qryStr=" And listPrice like'%"+searchStr+"%' ";
			displayAllSKUFileData("listPrice", listPriceFilterLBId, qryStr);

		}else{
			displayAllSKUFileData("listPrice", listPriceFilterLBId, null);
		}
	}
	
	public void onOK$listPriceTBId(){
		String searchStr = "";
		searchStr = listPriceTBId.getValue().trim();
		String qryStr="";
		if(searchStr.length()>0 && searchStr.contains(",") && 
				!searchStr.split(",")[0].isEmpty() && !searchStr.split(",")[1].isEmpty()){
			Double lesserNumber=Double.parseDouble(searchStr.split(",")[0]);
			Double greaterNumber=Double.parseDouble(searchStr.split(",")[1]);
			qryStr=" And listPrice > "+lesserNumber+" AND listPrice <"+ greaterNumber;
			displayAllSKUFileData("listPrice", listPriceFilterLBId, qryStr);
		}else if(searchStr.length()>0 && searchStr.contains(">")){

			//Double lesserNumber=Double.parseDouble(searchStr.split(",")[0]);
			Double lesserNumber=Double.parseDouble(searchStr.substring(1));
			qryStr=" And listPrice > "+lesserNumber;
			displayAllSKUFileData("listPrice", listPriceFilterLBId, qryStr);
		
			
		}else if(searchStr.length()>0 && searchStr.contains("<")){
			
			Double greaterNumber=Double.parseDouble(searchStr.substring(1));
			qryStr=" And listPrice < "+greaterNumber;
			displayAllSKUFileData("listPrice", listPriceFilterLBId, qryStr);
			
		}else if(searchStr.length()>0){
			
			
			qryStr=" And listPrice like'%"+searchStr+"%' ";
			displayAllSKUFileData("listPrice", listPriceFilterLBId, qryStr);

		}else{
			displayAllSKUFileData("listPrice", listPriceFilterLBId, null);
		}
	}
	
	/*public void onOK$listPriceTBId(){
		String searchStr = "";
		searchStr = listPriceTBId.getValue().trim();
		String qryStr="";
		if(searchStr.length()>0 && searchStr.contains(",") && 
				!searchStr.split(",")[0].isEmpty() && !searchStr.split(",")[1].isEmpty()){
			Double lesserNumber=Double.parseDouble(searchStr.split(",")[0]);
			Double greaterNumber=Double.parseDouble(searchStr.split(",")[1]);
			qryStr=" And listPrice > "+lesserNumber+" AND listPrice <"+ greaterNumber;
			displayAllSKUFileData("listPrice", listPriceFilterLBId, qryStr);
		}else if(searchStr.length()>0){
			
			qryStr=" And listPrice like'%"+searchStr+"%' ";
			displayAllSKUFileData("listPrice", listPriceFilterLBId, qryStr);

		}else{
			displayAllSKUFileData("listPrice", listPriceFilterLBId, null);
		}
	}*/
	
	public void onClick$descriptionFilterImage(){
		String searchStr = "";
		searchStr = descriptionTBId.getValue().trim();
		if (searchStr.length() > 0) {
			String qryStr=" And description like'%"+searchStr+"%' ";
			displayAllSKUFileData("description", descriptionFilterLBId, qryStr);
		}else{
			displayAllSKUFileData("description", descriptionFilterLBId, null);

		}
	}
	
	public void onClick$skuFilterImage(){
		String searchStr = "";
		searchStr = skuTBId.getValue().trim();
		if (searchStr.length() > 0) {
			String qryStr=" And sku like'%"+searchStr+"%' ";
			displayAllSKUFileData("sku", skuFilterLBId, qryStr);
		}else{
			displayAllSKUFileData("sku", skuFilterLBId, null);

		}
	}
	public void onClick$udfFilterImage(Listbox listbox, String column){
		try{
		Textbox tb = (Textbox) listbox.getAttribute("Textbox");
		String searchStr = "";
	//	String udfColumnName=(String) listbox.getAttribute("column");
		searchStr = tb.getValue().trim();
		if (searchStr.length() > 0) {
			String qryStr=" And "+column+" like'%"+searchStr+"%' ";
			displayAllSKUFileData(column, listbox, qryStr);
		}else{
			displayAllSKUFileData(column, listbox, null);

		}
		}catch(Exception e){
			logger.info("Exception======"+e);
		}
	}
	
	public void onClick$udfFilterImage(){
		
		
		//logger.info("udfs===");
		List<Listbox> udfs = getUDFLbs();
		if (udfs != null && !udfs.isEmpty()) {

			for (Listbox listbox : udfs) {
			  if(listbox.isVisible()){
				
				Textbox tb = (Textbox) listbox.getAttribute("Textbox");
				String searchStr = "";
				String udfColumnName=(String) listbox.getAttribute("column");
				searchStr = tb.getValue().trim();
				if (searchStr.length() > 0) {
					String qryStr=" And "+udfColumnName+" like'%"+searchStr+"%' ";
					displayAllSKUFileData(udfColumnName, listbox, qryStr);
				}else{
					displayAllSKUFileData(udfColumnName, listbox, null);

				}
			   }
			}
		}
	}
	
	// onOk code
	
	public void onOK$classTBId(){
		String searchStr = "";
		searchStr = classTBId.getValue().trim();
		if (searchStr.length() > 0) {
			String qryStr=" And classCode like'%"+searchStr+"%' ";
			displayAllSKUFileData("classCode", classFilterLBId, qryStr);
		}else{
			displayAllSKUFileData("classCode", classFilterLBId, null);

		}
	}


	public void onOK$vendorCodeTBId() throws Exception {
		String searchStr = "";
		searchStr = vendorCodeTBId.getValue().trim();
		if (searchStr.length() > 0) {
			String qryStr=" And vendorCode like'%"+searchStr+"%' ";
			displayAllSKUFileData("vendorCode", vendorCodeFilterLBId, qryStr);
		}else{
			displayAllSKUFileData("vendorCode", vendorCodeFilterLBId, null);

		}
	}
	
	public void onOK$deptCodeTBId() throws Exception{
		String searchStr = "";
		searchStr = deptCodeTBId.getValue().trim();
		if (searchStr.length() > 0) {
			String qryStr=" And departmentCode like'%"+searchStr+"%' ";
			displayAllSKUFileData("departmentCode", deptCodeFilterLBId, qryStr);
		}else{
			displayAllSKUFileData("departmentCode", deptCodeFilterLBId, null);

		}
	}
	
	public void onOK$itemCategoryTBId(){
		String searchStr = "";
		searchStr = itemCategoryTBId.getValue().trim();
		if (searchStr.length() > 0) {
			String qryStr=" And itemCategory like'%"+searchStr+"%' ";
			displayAllSKUFileData("itemCategory", itemCategoryFilterLBId, qryStr);
		}else{
			displayAllSKUFileData("itemCategory", itemCategoryFilterLBId, null);

		}
	}
	
	public void onOK$dcsTBId(){
		String searchStr = "";
		searchStr = dcsTBId.getValue().trim();
		if (searchStr.length() > 0) {
			String qryStr=" And DCS like'%"+searchStr+"%' ";
			displayAllSKUFileData("DCS", dcsFilterLBId, qryStr);
		}else{
			displayAllSKUFileData("DCS", dcsFilterLBId, null);

		}
	}
	
	public void onOK$subClassTBId(){
		String searchStr = "";
		searchStr = subClassTBId.getValue().trim();
		if (searchStr.length() > 0) {
			String qryStr=" And subClassCode like'%"+searchStr+"%' ";
			displayAllSKUFileData("subClassCode", subClassFilterLBId, qryStr);
		}else{
			displayAllSKUFileData("subClassCode", subClassFilterLBId, null);

		}
	}
	
	public void onOK$descriptionTBId(){
		String searchStr = "";
		searchStr = descriptionTBId.getValue().trim();
		if (searchStr.length() > 0) {
			String qryStr=" And description like'%"+searchStr+"%' ";
			displayAllSKUFileData("description", descriptionFilterLBId, qryStr);
		}else{
			displayAllSKUFileData("description", descriptionFilterLBId, null);

		}
	}
	
	public void onOK$skuTBId(){
		String searchStr = "";
		searchStr = skuTBId.getValue().trim();
		if (searchStr.length() > 0) {
			String qryStr=" And sku like'%"+searchStr+"%' ";
			displayAllSKUFileData("sku", skuFilterLBId, qryStr);
		}else{
			displayAllSKUFileData("sku", skuFilterLBId, null);

		}
	}
	public void onOK$udfTextbox(Listbox listbox, String column){
		try{
		Textbox tb = (Textbox) listbox.getAttribute("Textbox");
		String searchStr = "";
	//	String udfColumnName=(String) listbox.getAttribute("column");
		searchStr = tb.getValue().trim();
		if (searchStr.length() > 0) {
			String qryStr=" And "+column+" like'%"+searchStr+"%' ";
			displayAllSKUFileData(column, listbox, qryStr);
		}else{
			displayAllSKUFileData(column, listbox, null);

		}
		}catch(Exception e){
			logger.info("Exception======"+e);
		}
	}
	
	
	/*	
		
	*/
	public void onClick$viewSKUFilterImage(){
		String searchQuery = "";
		searchQuery = viewSkuTBId.getValue().trim();
		Listitem isSkuItemSelected = checkSkuSelected();
		if (isSkuItemSelected != null && searchQuery.length() > 0) {
			viewSKULbId.setVisible(true);
			skuDivId.setVisible(true);
			searchStr = searchQuery;
			Long totalSize = (Long) skuFileDao.executeQuery("SELECT COUNT(distinct sku) FROM SkuFile  WHERE userId = "
					+ orgOwnerUserId + " and sku like'%" + searchQuery + "%'").get(0);
			skuPaging.setTotalSize(totalSize.intValue());
			skuPaging.setPageSize(10);
			skuPaging.setActivePage(0);
			skuPaging.setAttribute(LBFilterEventListener.HANDLE_ON_PAGING, "false");
			List<Object> result = skuFileDao.getAllSkuSearchBy(orgOwnerUserId, 0, skuPaging.getPageSize(), searchQuery);
			int cnt = viewSKULbId.getItemCount();
			for (; cnt > 0; cnt--) {
				viewSKULbId.removeItemAt(cnt - 1);
			}
			if (result == null)
				return;
			Listitem item = null;
			for (Object object : result) {
				item = new Listitem();
				Object[] obj = (Object[]) object;
				item.setValue(obj);
				new Listcell().setParent(item);
				new Listcell(obj[0].toString()).setParent(item);
				new Listcell(obj[1] != null ? obj[1].toString() : "").setParent(item);
				new Listcell(obj[2] != null ? obj[2].toString() : "").setParent(item);
				item.setParent(viewSKULbId);
			}
		} else {
			// onCheck$skuCkId();
			doSkuFill(isSkuItemSelected);
			searchStr = "";
		}
	}

	private Listitem checkSkuSelected() {

		Set<Listitem> items = itemAttributeLbId.getSelectedItems();
		for (Listitem listitem : items) {
			if (listitem.getValue().equals("SKU"))
				return listitem;
		}

		return null;

	}

	private List<Listbox> getUDFLbs() {

		Set<String> keys = listMap.keySet();
		List<Listbox> list = new ArrayList<Listbox>();
		for (String key : keys) {

			if (key.startsWith("udf")) {

				list.add(listMap.get(key));
			}
		}
		return list;
	}
	public void onClick$applyFilterBtnId(){
		loadAllColumns=true;
		filterButtonChk();
		loadAllColumns=false;
	}
	
	private void filterButtonChk() {

		Set<Listitem> selItems = null;
		String tempStr;
		String udfColumnName="";
		query = " FROM SkuFile  WHERE userId =  " + users.getUserId().longValue() + " ";
		countQuery = "SELECT COUNT(*) FROM SkuFile  WHERE userId = " + users.getUserId().longValue() + " ";
		String qry="";
		String itemCategStr = "";
		selItems = itemCategoryFilterLBId.getSelectedItems();
		if (selItems != null && selItems.size() > 0) {
			for (Listitem eachLid : selItems) {
				if (eachLid.isVisible() == false)
					continue;
				tempStr = ((Listcell) eachLid.getLastChild()).getLabel();
				if (tempStr.contains("'")) {
					tempStr = StringEscapeUtils.escapeSql(tempStr);
				}
				if (!itemCategStr.isEmpty())
					itemCategStr += ", ";
				itemCategStr += "'" + tempStr + "'";
			}
			if (!itemCategStr.isEmpty()) {
				qry=qry + " AND itemCategory in (" + itemCategStr + ") ";
				query = query + " AND itemCategory in (" + itemCategStr + ") ";
				countQuery = countQuery + " AND itemCategory in (" + itemCategStr + ") ";
			}
		}
		

		// Department Code
		selItems = deptCodeFilterLBId.getSelectedItems();
		String deptCodeStr = "";
		if (selItems != null && selItems.size() > 0) {
			for (Listitem eachLid : selItems) {
				if (eachLid.isVisible() == false)
					continue;
				tempStr = ((Listcell) eachLid.getLastChild()).getLabel();
				if (tempStr.contains("'")) {
					tempStr = StringEscapeUtils.escapeSql(tempStr);
				}
				if (!deptCodeStr.isEmpty())
					deptCodeStr += ", ";
				deptCodeStr += "'" + tempStr + "'";
			}
			if (!deptCodeStr.isEmpty()) {
				qry = qry + " AND departmentCode in (" + deptCodeStr + ") ";
				query = query + " AND departmentCode in (" + deptCodeStr + ") ";
				countQuery = countQuery + " AND departmentCode in (" + deptCodeStr + ") ";
			}
		}

		// Vendor code
		selItems = vendorCodeFilterLBId.getSelectedItems();
		String vendCodeStr = "";
		if (selItems != null && selItems.size() > 0) {
			for (Listitem eachLid : selItems) {
				if (eachLid.isVisible() == false)
					continue;
				tempStr = ((Listcell) eachLid.getLastChild()).getLabel();
				if (tempStr.contains("'")) {
					tempStr = StringEscapeUtils.escapeSql(tempStr);
				}
				if (!vendCodeStr.isEmpty())
					vendCodeStr += ", ";
				vendCodeStr += "'" + tempStr + "'";
			}

			if (!vendCodeStr.isEmpty()) {
				qry = qry + " AND vendorCode in (" + vendCodeStr + ") ";
				query = query + " AND vendorCode in (" + vendCodeStr + ") ";
				countQuery = countQuery + " AND vendorCode in (" + vendCodeStr + ") ";
			}
		}

		// Calss Code
		selItems = classFilterLBId.getSelectedItems();
		String calssCodeStr = "";
		if (selItems != null && selItems.size() > 0) {
			for (Listitem eachLid : selItems) {
				if (eachLid.isVisible() == false)
					continue;
				tempStr = ((Listcell) eachLid.getLastChild()).getLabel();
				if (tempStr.contains("'")) {
					tempStr = StringEscapeUtils.escapeSql(tempStr);
				}
				if (!calssCodeStr.isEmpty())
					calssCodeStr += ", ";
				calssCodeStr += "'" + tempStr + "'";
			}

			if (!calssCodeStr.isEmpty()) {
				qry = qry + " AND classCode in (" + calssCodeStr + ") ";
				query = query + " AND classCode in (" + calssCodeStr + ") ";
				countQuery = countQuery + " AND classCode in (" + calssCodeStr + ") ";
			}
		}

		// Sub Class
		selItems = subClassFilterLBId.getSelectedItems();
		String subCalssCodeStr = "";
		if (selItems != null && selItems.size() > 0) {
			for (Listitem eachLid : selItems) {
				if (eachLid.isVisible() == false)
					continue;
				tempStr = ((Listcell) eachLid.getLastChild()).getLabel();
				if (tempStr.contains("'")) {
					tempStr = StringEscapeUtils.escapeSql(tempStr);
				}
				if (!subCalssCodeStr.isEmpty())
					subCalssCodeStr += ", ";
				subCalssCodeStr += "'" + tempStr + "'";
			}

			if (!subCalssCodeStr.isEmpty()) {
				qry = qry + " AND subClassCode in (" + subCalssCodeStr + ") ";
				query = query + " AND subClassCode in (" + subCalssCodeStr + ") ";
				countQuery = countQuery + " AND subClassCode in (" + subCalssCodeStr + ") ";
			}
		}

		// DCS
		selItems = dcsFilterLBId.getSelectedItems();
		String dcsCodeStr = "";
		if (selItems != null && selItems.size() > 0) {
			for (Listitem eachLid : selItems) {
				if (eachLid.isVisible() == false)
					continue;
				tempStr = ((Listcell) eachLid.getLastChild()).getLabel();
				if (tempStr.contains("'")) {

					tempStr = StringEscapeUtils.escapeSql(tempStr);
				}
				if (!dcsCodeStr.isEmpty())
					dcsCodeStr += ", ";
				dcsCodeStr += "'" + tempStr + "'";
			}

			if (!dcsCodeStr.isEmpty()) {
				qry = qry + " AND DCS in (" + dcsCodeStr + ") ";
				query = query + " AND DCS in (" + dcsCodeStr + ") ";
				countQuery = countQuery + " AND DCS in (" + dcsCodeStr + ") ";
			}
		}
		
		// Description
				selItems = descriptionFilterLBId.getSelectedItems();
				String descriptionStr = "";
				if (selItems != null && selItems.size() > 0) {
					for (Listitem eachLid : selItems) {
						if (eachLid.isVisible() == false)
							continue;
						tempStr = ((Listcell) eachLid.getLastChild()).getLabel();
						if (tempStr.contains("'")) {

							tempStr = StringEscapeUtils.escapeSql(tempStr);
						}
						if (!descriptionStr.isEmpty())
							descriptionStr += ", ";
						descriptionStr += "'" + tempStr + "'";
					}

					if (!descriptionStr.isEmpty()) {
						qry = qry + " AND description in (" + descriptionStr + ") ";
						query = query + " AND description in (" + descriptionStr + ") ";
						countQuery = countQuery + " AND description in (" + descriptionStr + ") ";
					}
				}
				
				// List Price
				selItems = listPriceFilterLBId.getSelectedItems();
				String listPriceStr = "";
				if (selItems != null && selItems.size() > 0) {
					for (Listitem eachLid : selItems) {
						if (eachLid.isVisible() == false)
							continue;
						tempStr = ((Listcell) eachLid.getLastChild()).getLabel();
						if (tempStr.contains("'")) {
							tempStr = StringEscapeUtils.escapeSql(tempStr);
						}
						if (!listPriceStr.isEmpty())
							listPriceStr += ", ";
						listPriceStr += "'" + tempStr + "'";
					}

					if (!listPriceStr.isEmpty()) {
						qry = qry + " AND listPrice in (" + listPriceStr + ") ";
						query = query + " AND listPrice in (" + listPriceStr + ") ";
						countQuery = countQuery + " AND listPrice in (" + listPriceStr + ") ";
					}
				}
				
				// SKU
				selItems = skuFilterLBId.getSelectedItems();
				String skuStr = "";
				if (selItems != null && selItems.size() > 0) {
					for (Listitem eachLid : selItems) {
						if (eachLid.isVisible() == false)
							continue;
						tempStr = ((Listcell) eachLid.getLastChild()).getLabel();
						if (tempStr.contains("'")) {

							tempStr = StringEscapeUtils.escapeSql(tempStr);
						}
						if (!skuStr.isEmpty())
							skuStr += ", ";
						skuStr += "'" + tempStr + "'";
					}

					if (!skuStr.isEmpty()) {
						qry = qry + " AND sku in (" + skuStr + ") ";
						query = query + " AND sku in (" + skuStr + ") ";
						countQuery = countQuery + " AND sku in (" + skuStr + ") ";
					}
				}
				
				//UDFs
				List<Listbox> udfs = getUDFLbs();
				if (udfs != null && !udfs.isEmpty()) {

					for (Listbox listbox : udfs) {
						selItems = listbox.getSelectedItems();
						String columnName=(String) listbox.getAttribute("CustomFieldName");
						String udfStr = "";
						if (selItems != null && selItems.size() > 0) {
							for (Listitem eachLid : selItems) {
								if (eachLid.isVisible() == false)
									continue;
								tempStr = ((Listcell) eachLid.getLastChild()).getLabel();
								if (tempStr.contains("'")) {

									tempStr = StringEscapeUtils.escapeSql(tempStr);
								}
								if (!udfStr.isEmpty())
									udfStr += ", ";
								udfStr += "'" + tempStr + "'";
							}

							if (!udfStr.isEmpty()) {
								if(!udfColumnName.isEmpty()){
									udfColumnName+=",";
								}
								udfColumnName+=columnName;
								qry=qry + " AND "+columnName+" in (" + udfStr + ") ";
								query = query + " AND "+columnName+" in (" + udfStr + ") ";
								logger.info("query==="+query);
								countQuery = countQuery + " AND "+columnName+" in (" + udfStr + ") ";
								logger.info("countQuery==="+countQuery);

							}
						}
						
					}
				}

		logger.info(" Apply Filter Query is :: " + qry);
		prepraAllAttributeData(qry);
		

	}
	
	
	

	private void populateFilteredLists(List<SkuFile> list,String qry,String udfColumns) {
	// TODO Auto-generated method stub
		Listitem li = null;
		Listcell cell = null;
		
		HashSet<String> skuSet=new HashSet<String>();
		HashSet<String> itemCategorySet=new HashSet<String>();
		HashSet<String> vendorCodeSet=new HashSet<String>();
		HashSet<String> deptCodeSet=new HashSet<String>();
		HashSet<String> classSet=new HashSet<String>();
		HashSet<String> subClassSet=new HashSet<String>();
		HashSet<String> dcsSet=new HashSet<String>();
		HashSet<String> descriptionSet=new HashSet<String>();
		
		if(filterFlag==true){
		int cnt = skuFilterLBId.getItemCount();
		
		for (; cnt > 0; cnt--) {
			skuFilterLBId.removeItemAt(cnt - 1);
		}
		
		cnt = itemCategoryFilterLBId.getItemCount();
		
		for (; cnt > 0; cnt--) {
			itemCategoryFilterLBId.removeItemAt(cnt - 1);
		}
		
		cnt = vendorCodeFilterLBId.getItemCount();
		
		for (; cnt > 0; cnt--) {
			vendorCodeFilterLBId.removeItemAt(cnt - 1);
		}
		
		cnt = deptCodeFilterLBId.getItemCount();
		
		for (; cnt > 0; cnt--) {
			deptCodeFilterLBId.removeItemAt(cnt - 1);
		}
		
		cnt = classFilterLBId.getItemCount();
		
		for (; cnt > 0; cnt--) {
			classFilterLBId.removeItemAt(cnt - 1);
		}
		
		cnt = subClassFilterLBId.getItemCount();
		
		for (; cnt > 0; cnt--) {
			subClassFilterLBId.removeItemAt(cnt - 1);
		}
		
		cnt = dcsFilterLBId.getItemCount();
		
		for (; cnt > 0; cnt--) {
			dcsFilterLBId.removeItemAt(cnt - 1);
		}
		
		cnt = descriptionFilterLBId.getItemCount();
		
		for (; cnt > 0; cnt--) {
			descriptionFilterLBId.removeItemAt(cnt - 1);
		}
		
		}
		for(SkuFile sku : list ){
			if(sku.getSku()!=null && !sku.getSku().isEmpty()){	
				skuSet.add(sku.getSku());
			}
			if(sku.getItemCategory()!=null && !sku.getItemCategory().isEmpty()){
				 itemCategorySet.add(sku.getItemCategory());
			 }
			if(sku.getVendorCode()!=null && !sku.getVendorCode().isEmpty()){
				vendorCodeSet.add(sku.getVendorCode());
			 }
			if(sku.getDepartmentCode()!=null && !sku.getDepartmentCode().isEmpty()){
				deptCodeSet.add(sku.getDepartmentCode());
			}
			if(sku.getClassCode()!=null && !sku.getClassCode().isEmpty()){
				classSet.add(sku.getClassCode());
			}
			if(sku.getSubClassCode()!=null && !sku.getSubClassCode().isEmpty()){
				subClassSet.add(sku.getSubClassCode());
			}
			if(sku.getDCS()!=null && !sku.getDCS().isEmpty()){
				dcsSet.add(sku.getDCS());
			}
			if(sku.getDescription()!=null && !sku.getDescription().isEmpty()){
				descriptionSet.add(sku.getDescription());
			}
			
			/*if(sku.getUdf2()!=null && !sku.getUdf2().isEmpty()){
				udf2Set.add(sku.getUdf2());
			}
			try {
				for(int i=1;i<=12;i++){
						String udfValue=getUdfContactObj(i,sku);
					//	if(i==1) udf1Set.add();
				}
			} catch (Exception e) {
			}*/
			
		}

		/*List<Listbox> udfs = getUDFLbs();
		if (udfs != null && !udfs.isEmpty()) {

			for (Listbox listbox : udfs) {
				 cnt = listbox.getItemCount();
				for (; cnt > 0; cnt--) {
					listbox.removeItemAt(cnt - 1);
				}
			}
		}*/
		List<POSMapping> posList = posMappingDao.findByType("'" + Constants.POS_MAPPING_TYPE_SKU + "'",
				users.getUserId());
		
		if (posList != null && posList.size() > 0) {
			String column = null;
			List<Object[]> results=null;
			String tempQry=null;
			Listbox udfListbox ;
			for (POSMapping posMapping : posList) {
			if (posMapping.getCustomFieldName().trim().toLowerCase().startsWith("udf")) {
					for (Listitem listitem : itemAttributeLbId.getItems()) {
						if (listitem.getValue().toString().toLowerCase()
								.equalsIgnoreCase(posMapping.getCustomFieldName())) {
							column = (String) listitem.getAttribute("column");
							logger.info("name" + column);
							String sku = skufileMap.get(posMapping.getCustomFieldName().toLowerCase());
							 udfListbox = (Listbox) listitem.getAttribute("AssociatedLB");
//							if(!udfListbox.isVisible() ){
								if((!udfListbox.isVisible() && filterFlag==false ) || filterFlag==true){

							 int	cnt = udfListbox.getItemCount();
								for (; cnt > 0; cnt--) {
									udfListbox.removeItemAt(cnt - 1);
								}
								tempQry = " SELECT DISTINCT "+column+",count(*) FROM SkuFile  WHERE userId = "+orgOwnerUserId+" ";
								String[] udfColumnname=null;
								if(udfColumns!=null && udfColumns.contains(",")){
									udfColumnname=udfColumns.split(",");
								}
								if(udfColumnname!=null){
								for (String udf : udfColumnname) {
									if(udf.trim().equalsIgnoreCase(column)){
										if(filterFlag==true){
											tempQry+=qry;
										}
										tempQry+= "and "+column+" is not null group by "+column+" order by 1";
										logger.info("qry1=="+qry);
										logger.info("tempQry1===="+tempQry);
										results = skuFileDao.executeQuery(tempQry);
										for(Object[] udfvalue : results){
											li = new Listitem();
											if (udfvalue instanceof Object[]) {
												Object[] obj = (Object[]) udfvalue;
												li.setValue(udfvalue);
												new Listcell().setParent(li);
												new Listcell(obj[0].toString()).setParent(li);
											//logger.info("obj[0]=="+obj[0].toString());
										
									}
								
									li.setParent(udfListbox);
									udfListbox.setVisible(true);
									udfListbox.setWidth("175px");
									udfListbox.setMultiple(true);
									Listhead head = udfListbox.getListhead();
									Listheader header = (Listheader) head.getLastChild();
									String index=header.getValue().toString();
									int i=Integer.parseInt(index);
									i=i-1;
								}
								}
								LBFilterEventListener.lbFilterSetup(udfListbox);

								}
						}else if(udfColumns!=null && !udfColumns.isEmpty()){
							if(udfColumns.trim().equalsIgnoreCase(column)){	

									if(filterFlag==true){
										tempQry+=qry;
									}
									tempQry+= "and "+column+" is not null group by "+column+" order by 1";
									logger.info("qry2=="+qry);
									logger.info("tempQry2===="+tempQry);
									results = skuFileDao.executeQuery(tempQry);
									for(Object[] udfvalue : results){
										li = new Listitem();
										if (udfvalue instanceof Object[]) {
											Object[] obj = (Object[]) udfvalue;
											li.setValue(udfvalue);
											new Listcell().setParent(li);
											new Listcell(obj[0].toString()).setParent(li);
										//logger.info("obj[0]=="+obj[0].toString());
									
								}
							
								li.setParent(udfListbox);
								udfListbox.setVisible(true);
								udfListbox.setWidth("175px");
								udfListbox.setMultiple(true);
								Listhead head = udfListbox.getListhead();
								Listheader header = (Listheader) head.getLastChild();
								String index=header.getValue().toString();
								int i=Integer.parseInt(index);
								i=i-1;
							
							}
							LBFilterEventListener.lbFilterSetup(udfListbox);

							
							}
						}
						else if(!filterFlag || (filterFlag && (udfColumns==null || udfColumns.isEmpty()))){	
							if(filterFlag==true){
								tempQry+=qry;
							}
								tempQry+= "and "+column+" is not null group by "+column+" order by 1";
								logger.info("qry=="+qry);
								logger.info("tempQry===="+tempQry);
								results = skuFileDao.executeQuery(tempQry);
								for(Object[] udfvalue : results){
									li = new Listitem();
									if (udfvalue instanceof Object[]) {
										Object[] obj = (Object[]) udfvalue;
										li.setValue(udfvalue);
										new Listcell().setParent(li);
										new Listcell(obj[0].toString()).setParent(li);
									//	logger.info("obj[0]=="+obj[0].toString());
							
									}
					
									li.setParent(udfListbox);
									udfListbox.setVisible(true);
									udfListbox.setWidth("175px");
									udfListbox.setMultiple(true);
									Listhead head = udfListbox.getListhead();
									Listheader header = (Listheader) head.getLastChild();
									String index=header.getValue().toString();
									int i=Integer.parseInt(index);
									i=i-1;
								}
						}else{
						//	logger.info("else blockkkk");
							udfListbox.setVisible(true);
							udfListbox.setWidth("175px");
							udfListbox.setMultiple(false);
							udfListbox.setEmptyMessage("No UDFs");
							
						}
								//logger.info("udfListbox.getItemCount()==="+udfListbox.getItemCount());
								if(udfListbox.getItemCount()==0){
									udfListbox.setMultiple(false);
								}
								LBFilterEventListener.lbFilterSetup(udfListbox);

					}
							
						
							
						}
					}
				}
			}
		}
		
	//	Listbox udflbId=listMap.get("udf2FilterLBID");
		/*logger.info("listitemss count====="+udflbId.getItemCount());

		for (; cnt > 0; cnt--) {
			udflbId.removeItemAt(cnt - 1);
		}*/
	/*	if(udf2Set!=null && !udf2Set.isEmpty()){
			for(String skuValue : udf2Set){
				logger.info("udfSet==="+skuValue);
				li = new Listitem();
				
				
				Object[] obj = new Object[2];
				obj[0]=skuValue;
				obj[1]=0;
				
				
				li.setValue(obj);
				
				
				cell = new Listcell();
				cell.setParent(li);
				
				cell=new Listcell(skuValue);
				cell.setParent(li);
			
				li.setParent(udflbId);
				udflbId.setVisible(true);
				udflbId.setWidth("175px");
				Listhead head = udflbId.getListhead();
				Listheader header = (Listheader) head.getLastChild();
				String index=header.getValue().toString();
				int i=Integer.parseInt(index);
				i=i-1;
				
			}	
			}
			
			LBFilterEventListener.lbFilterSetup(udflbId);*/
		
		
	//	for(Listbox listbox : udfs){
		/*try{
			for(SkuFile sku : list){
			for(int i=1;i<=12;i++){
				Listbox listbox=listMap.get("udf"+i+"FilterLBID");
				String udfValue=getUdfContactObj(i,sku);
			
				if(udfValue!=null && !udfValue.isEmpty()){
				logger.info("udfSet==="+udfValue);
				li = new Listitem();
				
				
				Object[] obj = new Object[2];
				obj[0]=udfValue;
				obj[1]=0;
				
				
				li.setValue(obj);
				
				
				cell = new Listcell();
				cell.setParent(li);
				
				cell=new Listcell(udfValue);
				cell.setParent(li);
			
				li.setParent(listbox);
				listbox.setVisible(true);
				listbox.setWidth("175px");
				Listhead head = listbox.getListhead();
				Listheader header = (Listheader) head.getLastChild();
				String index=header.getValue().toString();
				//int i=Integer.parseInt(index);
				i=i-1;
				
			}
				LBFilterEventListener.lbFilterSetup(listbox);
			}
			
			}
		}catch(Exception e){
			logger.info("Exception"+e);
		}
		//}
*/		
		
		//if(!skuFilterLBId.isVisible() || (skuFilterLBId.isVisible() && filterFlag==true)){
			
			/*if(!skuFilterLBId.isVisible()){
				 int	cnt = skuFilterLBId.getItemCount();
					for (; cnt > 0; cnt--) {
						skuFilterLBId.removeItemAt(cnt - 1);
					}
			}*/
		
		if(skuSet!=null && !skuSet.isEmpty()){
			
			
			if((!skuFilterLBId.isVisible() && filterFlag==false) || filterFlag==true){
				 int	cnt = skuFilterLBId.getItemCount();
					for (; cnt > 0; cnt--) {
						skuFilterLBId.removeItemAt(cnt - 1);
					}
		for(String skuValue : skuSet){
			li = new Listitem();
			
			
			Object[] obj = new Object[2];
			obj[0]=skuValue;
			obj[1]=0;
			
			
			li.setValue(obj);
			
			
			cell = new Listcell();
			cell.setParent(li);
			
			cell=new Listcell(skuValue);
			cell.setParent(li);
			
			li.setParent(skuFilterLBId);
			skuFilterLBId.setMultiple(true);
			skuFilterLBId.setVisible(true);
			skuFilterLBId.setWidth("175px");
			Listhead head = skuFilterLBId.getListhead();
			Listheader header = (Listheader) head.getLastChild();
			String index=header.getValue().toString();
			int i=Integer.parseInt(index);
			i=i-1;
			
		}	
		}
		}else{
			skuFilterLBId.setVisible(true);
			skuFilterLBId.setWidth("175px");
			skuFilterLBId.setMultiple(false);
		}
		
		LBFilterEventListener.lbFilterSetup(skuFilterLBId);
		
		
		if(itemCategorySet!=null && !itemCategorySet.isEmpty()){
			/*
			if(!itemCategoryFilterLBId.isVisible()){
				 int	cnt = itemCategoryFilterLBId.getItemCount();
					for (; cnt > 0; cnt--) {
						itemCategoryFilterLBId.removeItemAt(cnt - 1);
					}
			}*/
			if((!itemCategoryFilterLBId.isVisible() && filterFlag==false) || filterFlag==true){
				 int	cnt = itemCategoryFilterLBId.getItemCount();
					for (; cnt > 0; cnt--) {
						itemCategoryFilterLBId.removeItemAt(cnt - 1);
					}

			for(String itemCategoryValue : itemCategorySet ){
			li = new Listitem();
			
			Object[] obj = new Object[2];
			obj[0]=itemCategoryValue;
			obj[1]=0;
			li.setValue(obj);
			
			cell = new Listcell();
			cell.setParent(li);

			cell=new Listcell(itemCategoryValue);
			cell.setParent(li);
			
			li.setParent(itemCategoryFilterLBId);
			itemCategoryFilterLBId.setMultiple(true);
			itemCategoryFilterLBId.setVisible(true);
			itemCategoryFilterLBId.setWidth("175px");
			Listhead head = itemCategoryFilterLBId.getListhead();
			Listheader header = (Listheader) head.getLastChild();
			String index=header.getValue().toString();
			int i=Integer.parseInt(index);
			i=i-1;
			
			
		}
		}
		}else{
			itemCategoryFilterLBId.setVisible(true);
			itemCategoryFilterLBId.setWidth("175px");
			itemCategoryFilterLBId.setMultiple(false);
		}
		
		LBFilterEventListener.lbFilterSetup(itemCategoryFilterLBId);
		
		
		
		if(vendorCodeSet!=null && !vendorCodeSet.isEmpty()){
			if((!vendorCodeFilterLBId.isVisible() && filterFlag==false ) || filterFlag==true){
				 int	cnt = vendorCodeFilterLBId.getItemCount();
					for (; cnt > 0; cnt--) {
						vendorCodeFilterLBId.removeItemAt(cnt - 1);
					}
			
			
		for(String vendorCodeValue : vendorCodeSet ){
			li = new Listitem();
			
			Object[] obj = new Object[2];
			obj[0]=vendorCodeValue;
			obj[1]=0;
			
			li.setValue(obj);
			cell = new Listcell();
			cell.setParent(li);
			
			cell=new Listcell(vendorCodeValue);
			cell.setParent(li);
			
			li.setParent(vendorCodeFilterLBId);
			vendorCodeFilterLBId.setMultiple(true);
			vendorCodeFilterLBId.setVisible(true);
			vendorCodeFilterLBId.setWidth("175px");
			Listhead head = vendorCodeFilterLBId.getListhead();
			Listheader header = (Listheader) head.getLastChild();
			String index=header.getValue().toString();
			int i=Integer.parseInt(index);
			i=i-1;
			
			
		}
		}
	}else{
		vendorCodeFilterLBId.setVisible(true);
		vendorCodeFilterLBId.setWidth("175px");
		vendorCodeFilterLBId.setMultiple(false);
	}
		LBFilterEventListener.lbFilterSetup(vendorCodeFilterLBId);
		
		
		if(deptCodeSet!=null && !deptCodeSet.isEmpty()){
				if((!deptCodeFilterLBId.isVisible() && filterFlag==false) || (filterFlag==true)){
					 int	cnt = deptCodeFilterLBId.getItemCount();
						for (; cnt > 0; cnt--) {
							deptCodeFilterLBId.removeItemAt(cnt - 1);
						}
				
			
		for(String deptCodeValue : deptCodeSet ){
			li = new Listitem();
			
			Object[] obj = new Object[2];
			obj[0]=deptCodeValue;
			obj[1]=0;
			
			li.setValue(obj);
			cell = new Listcell();
			cell.setParent(li);
			
			cell=new Listcell(deptCodeValue);
			cell.setParent(li);
			
			li.setParent(deptCodeFilterLBId);
			deptCodeFilterLBId.setMultiple(true);
			deptCodeFilterLBId.setVisible(true);
			deptCodeFilterLBId.setWidth("175px");
			Listhead head = deptCodeFilterLBId.getListhead();
			Listheader header = (Listheader) head.getLastChild();
			String index=header.getValue().toString();
			int i=Integer.parseInt(index);
			i=i-1;
			
			
		}
		}
		}
		else{
			deptCodeFilterLBId.setVisible(true);
			deptCodeFilterLBId.setWidth("175px");
			deptCodeFilterLBId.setMultiple(false);
		}
		LBFilterEventListener.lbFilterSetup(deptCodeFilterLBId);
		
		
		if(classSet!=null && !classSet.isEmpty()){
			if((!classFilterLBId.isVisible() && filterFlag==false ) || filterFlag==true){
				 int	cnt = classFilterLBId.getItemCount();
					for (; cnt > 0; cnt--) {
						classFilterLBId.removeItemAt(cnt - 1);
					}
			
		
			for(String classValue : classSet ){

			li = new Listitem();
			
			Object[] obj = new Object[2];
			obj[0]=classValue;
			obj[1]=0;
			
			li.setValue(obj);
			cell = new Listcell();
			cell.setParent(li);
			
			cell=new Listcell(classValue);
			cell.setParent(li);
			
			li.setParent(classFilterLBId);
			classFilterLBId.setMultiple(true);
			classFilterLBId.setVisible(true);
			classFilterLBId.setWidth("175px");
			Listhead head = classFilterLBId.getListhead();
			Listheader header = (Listheader) head.getLastChild();
			String index=header.getValue().toString();
			int i=Integer.parseInt(index);
			i=i-1;
			}
			
		}
		}else{
			classFilterLBId.setVisible(true);
			classFilterLBId.setWidth("175px");
			classFilterLBId.setMultiple(false);
		}
		LBFilterEventListener.lbFilterSetup(classFilterLBId);
		
		
		if(subClassSet!=null && !subClassSet.isEmpty()){
			
			if((!subClassFilterLBId.isVisible() && filterFlag==false ) || filterFlag==true){
				 int	cnt = subClassFilterLBId.getItemCount();
					for (; cnt > 0; cnt--) {
						subClassFilterLBId.removeItemAt(cnt - 1);
					}
			
			for(String subClassValue : subClassSet ){
			li = new Listitem();
			
			Object[] obj = new Object[2];
			obj[0]=subClassValue;
			obj[1]=0;
			li.setValue(obj);

			
			cell = new Listcell();
			cell.setParent(li);
			
			cell=new Listcell(subClassValue);
			cell.setParent(li);
			
			li.setParent(subClassFilterLBId);
			subClassFilterLBId.setMultiple(true);
			subClassFilterLBId.setVisible(true);
			subClassFilterLBId.setWidth("175px");
			Listhead head = subClassFilterLBId.getListhead();
			Listheader header = (Listheader) head.getLastChild();
			String index=header.getValue().toString();
			int i=Integer.parseInt(index);
			i=i-1;
			
			}
		}
		}else{
			subClassFilterLBId.setVisible(true);
			subClassFilterLBId.setWidth("175px");
			subClassFilterLBId.setMultiple(false);
		}
		LBFilterEventListener.lbFilterSetup(subClassFilterLBId);
		
		
		if(dcsSet!=null && !dcsSet.isEmpty()){
			
			if((!dcsFilterLBId.isVisible() && filterFlag==false ) || filterFlag==true){
				 int	cnt = dcsFilterLBId.getItemCount();
					for (; cnt > 0; cnt--) {
						dcsFilterLBId.removeItemAt(cnt - 1);
					}
			
			for(String dcsValue : dcsSet ){
			li = new Listitem();
			
			Object[] obj = new Object[2];
			obj[0]=dcsValue;
			obj[1]=0;
			li.setValue(obj);

			
			cell = new Listcell();
			cell.setParent(li);
			
			cell=new Listcell(dcsValue);
			cell.setParent(li);
			
			li.setParent(dcsFilterLBId);
			dcsFilterLBId.setMultiple(true);
			dcsFilterLBId.setVisible(true);
			dcsFilterLBId.setWidth("175px");
			Listhead head = dcsFilterLBId.getListhead();
			Listheader header = (Listheader) head.getLastChild();
			String index=header.getValue().toString();
			int i=Integer.parseInt(index);
			i=i-1;
			
			
			//logger.info("index===="+index);
	//		itemAttributeLbId.setSelectedIndex(i);
		}
		}
		}else{
			dcsFilterLBId.setVisible(true);
			dcsFilterLBId.setWidth("175px");
			dcsFilterLBId.setMultiple(false);
		}
		LBFilterEventListener.lbFilterSetup(dcsFilterLBId);
		
		
		if(descriptionSet!=null && !descriptionSet.isEmpty()){
			
			if((!descriptionFilterLBId.isVisible() && filterFlag==false ) || filterFlag==true){
				 int	cnt = descriptionFilterLBId.getItemCount();
					for (; cnt > 0; cnt--) {
						descriptionFilterLBId.removeItemAt(cnt - 1);
					}
			
			
			for(String descValue : descriptionSet ){
			li = new Listitem();
			
			Object[] obj = new Object[2];
			obj[0]=descValue;
			obj[1]=0;
			li.setValue(obj);

			
			cell = new Listcell();
			cell.setParent(li);
			
			cell=new Listcell(descValue);
			cell.setParent(li);
			
			li.setParent(descriptionFilterLBId);
			descriptionFilterLBId.setMultiple(true);
			descriptionFilterLBId.setVisible(true);
			descriptionFilterLBId.setWidth("175px");
			
			Listhead head = descriptionFilterLBId.getListhead();
			Listheader header = (Listheader) head.getLastChild();
			String index=header.getValue().toString();
			int i=Integer.parseInt(index);
			i=i-1;
			
			
			//logger.info("index===="+index);
		//	itemAttributeLbId.setSelectedIndex(i);
		}
		}
		}else{
			descriptionFilterLBId.setVisible(true);
			descriptionFilterLBId.setWidth("175px");
			descriptionFilterLBId.setMultiple(false);
		}
		
		LBFilterEventListener.lbFilterSetup(descriptionFilterLBId);
		
		
		
		/*POSMappingDao posMappingDao = (POSMappingDao)SpringUtil.getBean("posMappingDao");
		List<POSMapping> posMappingsList = posMappingDao.findByType("'" + Constants.POS_MAPPING_TYPE_SKU + "'",
				users.getUserId());
		int udfIdx = 0;
		String udfCustFieldStr = "";
		HashMap<String, String> udfMap=new HashMap<String,String>();
		for(SkuFile sku : list){
		for (POSMapping posMapping : posMappingsList) {
			if(posMapping.getCustomFieldName().trim().toLowerCase().startsWith("udf")) {
				 udfCustFieldStr=posMapping.getCustomFieldName();
					udfIdx = Integer.parseInt(udfCustFieldStr.substring("UDF".length()));
					String posUDFValue = "";
					try {
						if(getUdfContactObj(udfIdx,sku) != null){
							posUDFValue = getUdfContactObj(udfIdx,sku);
							
							Listbox udfLbId=listMap.get(posUDFValue+"FilterLBID");
							for (; cnt > 0; cnt--) {
								udfLbId.removeItemAt(cnt - 1);
							}
							
							li = new Listitem();
							
							
							Object[] obj = new Object[2];
							obj[0]=udfMap.get(posUDFValue);
							obj[1]=0;
							
							
							li.setValue(obj);
							
							
							cell = new Listcell();
							cell.setParent(li);
							
							cell=new Listcell(posUDFValue);
							cell.setParent(li);
							li.setParent(udfLbId);
							udfLbId.setVisible(true);
							udfLbId.setWidth("175px");
							Listhead head = skuFilterLBId.getListhead();
							Listheader header = (Listheader) head.getLastChild();
							String index=header.getValue().toString();
							int i=Integer.parseInt(index);
							i=i-1;
							
							LBFilterEventListener.lbFilterSetup(udfLbId);

							
						}
					} catch (Exception e) {
							logger.info("Exception"+e);
					}
					
				}
			}
				
		}*/
		
	/*if(udfMap!=null && !udfMap.isEmpty()){
		for(String udfKey : udfMap.keySet()){
			
			Listbox udfLbId=listMap.get(udfKey.toLowerCase()+"FilterLBID");
			for (; cnt > 0; cnt--) {
				udfLbId.removeItemAt(cnt - 1);
			}
			
			li = new Listitem();
			
			
			Object[] obj = new Object[2];
			obj[0]=udfMap.get(udfKey);
			obj[1]=0;
			
			
			li.setValue(obj);
			
			
			cell = new Listcell();
			cell.setParent(li);
			
			cell=new Listcell(udfMap.get(udfKey));
			cell.setParent(li);
			li.setParent(udfLbId);
			udfLbId.setVisible(true);
			udfLbId.setWidth("175px");
			Listhead head = skuFilterLBId.getListhead();
			Listheader header = (Listheader) head.getLastChild();
			String index=header.getValue().toString();
			int i=Integer.parseInt(index);
			i=i-1;
			
			//LBFilterEventListener.lbFilterSetup(udfLbId);
			
		}
	}
	

		
		*/
		
		/*if(filterFlag==true){
			itemAttributeLbId.selectAll();
		}*/
		
		List<Listitem> items = itemAttributeLbId.getItems();

		for (Listitem item : items) {
				String lbID = skufileMap.get(item.getValue());
				Listbox listbox = listMap.get(lbID);
				if(listbox!=null){
				if (item.isSelected()) {
					listbox.setVisible(true);
				} else {
					listbox.setVisible(false);
					listbox.setSelectedIndex(-1);
				}
				//}
			}
		}
		
}

	private void setheaders(String discType) {

		String str1 = "";
		String str2 = "";
		if (discType.equalsIgnoreCase("Percentage")) {
			str1 = "%";
			discountTypeStr=DISC_TYPE_PERCENTAGE;
		}
		else {
			str1= userCurrencySymbol;
			discountTypeStr=DISC_TYPE_VALUE;
		}
		column1Id.setLabel(str1);
		column2Id.setLabel(str1);
	}

	String limit = null;
	Long start = null;
	Long end = null;
	String searchStr = "";
	Long noOfitem = 0l;
	boolean isCommaAllowed=false;
	public void onClick$selectBtnId() {
		discountDblBxId.setStyle(NORMAL_STYLE);
		totPurAmtLngBxId.setStyle(NORMAL_STYLE);

		// Discount
		logger.debug("totPurAmtLngBxId.getValue() is  :;" + discountDblBxId.getValue() + "  puchase value :"
				+ totPurAmtLngBxId.getValue());
		if (discountDblBxId.getValue() == null || discountDblBxId.getValue() <= 0) {
			discountDblBxId.setStyle(ERROR_STYLE);
			MessageUtil.setMessage("Please provide valid discount value.", "color:red", "TOP");
			return;
		}
		
		if (tpaRadioId.isChecked() && mpvChkBoxId.isChecked()) {
			if (totPurAmtLngBxId.getValue() == null || totPurAmtLngBxId.getValue() <= 0) {
				totPurAmtLngBxId.setStyle(ERROR_STYLE);
				MessageUtil.setMessage("Please provide valid minimum purchase value.", "color:red", "TOP");
				return;
			} else if (dollerRadioId.isChecked() && (discountDblBxId.getValue() > totPurAmtLngBxId.getValue())) {
				MessageUtil.setMessage("Discount Type can not exceed the  Minimum Purchase Amount.", "color:red",
						"TOP");
				return;
			}
		}

		if (percentRadioId.isChecked() && discountDblBxId.getValue() > 100) {
			MessageUtil.setMessage("Discount percentage cannot be more than 100.", "color:red", "TOP");
			return;
		}

		//if (isNewPlugin) {
			// Discount
			logger.debug("maxDiscountDblBxId.getValue() is  :;" + maxDiscountDblBxId.getValue());

			if (accumulateCoupChkBxId.isChecked()) {

				
				
				if(maxDiscountDblBxId.getValue() != null) {
					if (skuRadioId.isChecked()) {
						if(dollerRadioId.isChecked()||percentRadioId.isChecked()) {
							if((discountDblBxId.getValue() > maxDiscountDblBxId.getValue())){
								MessageUtil.setMessage("Discount Value can not exceed the Maximum Discount Value.", "color:red",
										"TOP");
								return;
								
							}
						}
					}else if(!skuRadioId.isChecked()) {
						if(dollerRadioId.isChecked()||percentRadioId.isChecked()) {
							if((discountDblBxId.getValue() > maxDiscountDblBxId.getValue())) {
								MessageUtil.setMessage("Discount Value can not exceed the Maximum Discount Value.", "color:red",
										"TOP");
								return;
								
							}
							
						}
					}
						
					}
			}
		//}

		/*String discTypeStr = "";
		if (percentRadioId.isChecked()) {
			discTypeStr = DISC_TYPE_PERCENTAGE;
			dollerRadioId.setDisabled(true);
			//if (isNewPlugin) {
				if (accumulateCoupChkBxId.isChecked()) {
					onProductLabelId.setVisible(true);
					onReceiptLabelId.setVisible(false);
				}
			//}
		} else {
			discTypeStr = DISC_TYPE_VALUE;
			percentRadioId.setDisabled(true);
			//if (isNewPlugin) {
				if (accumulateCoupChkBxId.isChecked()) {
					onProductLabelId.setVisible(true);
					onReceiptLabelId.setVisible(false);
				}
			//}
		}*/
		setheaders(discountTypeStr);
		
		
		
				if (skuRadioId.isChecked()) {
					
					String limitQuantity=null;
					if(comparisionLbId.isVisible()){
						limitQuantity=comparisionLbId.getSelectedItem().getValue().toString();
					}
					
					//if(multipleSingleId.isChecked()){
					if(ruleType.equalsIgnoreCase(multipleSingle)) {
					Set<Listitem> selctedItemSet = addedSKULBId.getSelectedItems();
					if (selctedItemSet == null || selctedItemSet.size() == 0) {
						MessageUtil.setMessage("Please select at least one attribute code for creating discount rule.",
								"color:red", "TOP");
						return;
					}
					Row row = null;
					Label label = null;
					List<SKUTemp> listSku = null;
					Long startId = null;
					Long endId = null;
					boolean dupflag = true;
					Map<String, String> map = new LinkedHashMap<>();
					Map<String, String> map1 = new LinkedHashMap<>();
					List<SKUTemp> oldlistSku = coupDiscGenDao.findTempSkuBy(users.getUserId(), null, limit);
					if (selctedItemSet != null && selctedItemSet.size() > 0) {
						listSku = new LinkedList<>();
						Iterator<Listitem> itr = selctedItemSet.iterator();
						List<Listitem> allItems = addedSKULBId.getItems();
						logger.info(" Total item size :" + allItems.size());
						while (itr.hasNext()) {
							Listitem item = itr.next();
							List<Component> cells = item.getChildren();
							SKUTemp skuTemp = null;
							String attribute = null;
							String value = null;
							String type = null;
							for (Component cmp : cells) {
								Listcell cell = (Listcell) cmp;
								//if(!cell.getAttribute("rule").toString().contains(Constants.DELIMITER_DOUBLE_PIPE+Constants.DELIMITER_DOUBLE_PIPE)){
								if (cell.getValue() != null && cell.getValue().toString().startsWith("key")) {
									attribute = cell.getValue().toString().split("::")[1];
									value=(String) cell.getAttribute("value");
									Set<Listitem> selectedItems = itemAttributeLbId.getSelectedItems();
									for (Listitem li : selectedItems) {
										if (li.getLabel().equalsIgnoreCase(attribute)) {
											type = li.getValue().toString();
										}

									}

								} 
							}
						
							boolean flag = false;
							if (oldlistSku != null && oldlistSku.size() > 0) {
								for (SKUTemp oldSku : oldlistSku) {
									if (oldSku.getSkuAttribute().equals(attribute) && oldSku.getSkuValue().equals(value)
											&& oldSku.getDiscount()
													.equals(Double.parseDouble(discountDblBxId.getValue().toString())))
										flag = true;
								}
							}
							if (flag)
								continue;
							skuTemp = new SKUTemp();
							skuTemp.setUserId(users.getUserId());
							skuTemp.setDiscount(discountDblBxId.getValue());
							skuTemp.setDiscountType(discountTypeStr);
							skuTemp.setSkuAttribute(udfRuleSKUMap.get(attribute)!=null?udfRuleSKUMap.get(attribute):attribute);
							skuTemp.setSkuValue(value);
							skuTemp.setOwnerId(orgOwnerUserId);
							//if (isNewPlugin) {
								if (tpaRadioId.isChecked()) {
									skuTemp.setMaxDiscount(maxDiscountDblBxId.getValue());
								} else if (skuRadioId.isChecked()) {
									if(accumulateCoupChkBxId.isChecked()) {
									skuTemp.setMaxDiscount(maxDiscountDblBxId.getValue());
									// skuTemp.setQuantity(quantityDblBxId.getValue());
									//if (higestPriceRadioId.isSelected() || higestPricewithoutRadioId.isSelected()) {
									skuTemp.setQuantity(quantityDblBxId.getValue() != null ? String.valueOf(quantityDblBxId.getValue()) : null);
									skuTemp.setLimitQuantity(quantityDblBxId.getValue() != null ? limitQuantity : null);
								//	}
								}else {
									//if (higestPriceRadioId.isSelected() || higestPricewithoutRadioId.isSelected()) {
										skuTemp.setQuantity(quantityDblBxId.getValue() != null ? String.valueOf(quantityDblBxId.getValue()) : null);
										skuTemp.setLimitQuantity(quantityDblBxId.getValue() != null ? limitQuantity : null);

									//}
								}
									
								}
							//}

							if (tempCouponObj != null)
								skuTemp.setCouponId(tempCouponObj.getCouponId());
							listSku.add(skuTemp);
							
							if (discountGenRowsId.getVisibleItemCount() > 0) {
								
								List<Component> rowList = discountGenRowsId.getChildren();
								
							if (rowList != null && rowList.size() > 0) {
							for (Object object : rowList) {
									Row existRow = (Row) object;
								//	Label tempCodeLbl = (Label) existRow.getChildren().get(2);
									Label temtypeLbl = (Label) existRow.getChildren().get(1);
									String temptypeLbl=(String) temtypeLbl.getAttribute("Type");
									/*if(temtypeLbl.getValue().contains("AND")){
										MessageUtil.setMessage("Multiple single rules cannot be added as you have already added a ONE combination rule.",
												"color:blue", "TOP");
										return;
									}*/
									for (SKUTemp sku : listSku) {
										if (map1.containsKey(sku.getSkuAttribute() + "::" + sku.getDiscount())) {
											value = (map1.get(sku.getSkuAttribute() + "::" + sku.getDiscount()));
											if (value != null && value.length() <= 100)
												map1.put(sku.getSkuAttribute() + "::" + sku.getDiscount(),
														value.concat(", ") + sku.getSkuValue());
										} else {
											map1.put(sku.getSkuAttribute() + "::" + sku.getDiscount(), sku.getSkuValue());
										}
									}
									for (Entry<String, String> entry : map1.entrySet()) {
										
										String typeLbl=	entry.getKey().split("::")[0].trim();
										if(map1.get(entry.getKey()).contains(",")){
											  String[] leftSideCodes=map1.get(entry.getKey()).split(",");
												for (String leftSideCode : leftSideCodes) {
													if(temtypeLbl.getValue().split("=")[1].contains(",")){
														String[] codeLblArray=temtypeLbl.getValue().split("=")[1].split(",");
														
														for(String codeLbl : codeLblArray){
															if(codeLbl.trim().equalsIgnoreCase(leftSideCode.replaceAll(" ","")) && temptypeLbl.equals(typeLbl)){
																MessageUtil.setMessage(
																		"You tried giving 2 different discounts for the same criteria. \n "
																				+ "Please reconsider or delete the old rule.",
																		"color:red", "TOP");
																return;
															}
														}
												}else if(temtypeLbl.getValue().split("=")[1].trim().equalsIgnoreCase(leftSideCode.replaceAll(" ","")) && temptypeLbl.equals(typeLbl)){
													MessageUtil.setMessage(
															"You tried giving 2 different discounts for the same criteria. \n "
																	+ "Please reconsider or delete the old rule.",
															"color:red", "TOP");
													return;
												}
										  }
											}else if(temtypeLbl.getValue().split("=")[1].contains(",")){

												String[] codeLblArray=temtypeLbl.getValue().split("=")[1].split(",");
												
												for(String codeLbl : codeLblArray){
													if(codeLbl.trim().equalsIgnoreCase(map1.get(entry.getKey())) && temptypeLbl.equals(typeLbl)){
														MessageUtil.setMessage(
																"You tried giving 2 different discounts for the same criteria. \n "
																		+ "Please reconsider or delete the old rule.",
																"color:red", "TOP");
														return;
													}
												}
										
											}
										else{
												
												
												if(temtypeLbl.getValue().split("=")[1].trim().equals(map1.get(entry.getKey())) && temptypeLbl.equals(typeLbl)){
													MessageUtil.setMessage(
															"You tried giving 2 different discounts for the same criteria. \n "
																	+ "Please reconsider or delete the old rule.",
															"color:red", "TOP");
													return;
												}/*else if(listSku.size() == 0){
													MessageUtil.setMessage("All codes are selected for another promotion.", "color:red", "TOP");
													return;
												}*/
										}
									}
								}
							}
						}
							if (listSku.size() >= 500) {
								coupDiscGenDaoForDML.saveByCollection(listSku);
								Comparator<SKUTemp> comparator = Comparator.comparing(SKUTemp::getSkuTempId);
								SKUTemp minObject = listSku.stream().min(comparator).get();
								if (startId == null)
									startId = minObject.getSkuTempId();
								endId = listSku.get(listSku.size() - 1).getSkuTempId();
								Collections.reverse(listSku);
								for (SKUTemp sku : listSku) {
									if (map.containsKey(sku.getSkuAttribute() + "::" + sku.getDiscount())) {
										value = (map.get(sku.getSkuAttribute() + "::" + sku.getDiscount()));
										if (value != null)
											map.put(sku.getSkuAttribute() + "::" + sku.getDiscount(),
													value.concat(", ") + sku.getSkuValue());
									} else {
										map.put(sku.getSkuAttribute() + "::" + sku.getDiscount(), sku.getSkuValue());
									}
								}
								dupflag = false;
								listSku = new ArrayList<>();
							}
							// item.setDisabled(true);
						}

						if (listSku.size() > 0 && listSku.size() < 500) {
							
							logger.info("size of the list========"+listSku.size());
							coupDiscGenDaoForDML.saveByCollection(listSku);
							Comparator<SKUTemp> comparator = Comparator.comparing(SKUTemp::getSkuTempId);
							SKUTemp minObject = listSku.stream().min(comparator).get();
							if (startId == null)
								// startId=skuList.get(0).getSkuTempId();
								startId = minObject.getSkuTempId();
							endId = listSku.get(listSku.size() - 1).getSkuTempId();
							String value = null;
							Collections.reverse(listSku);
							for (SKUTemp sku : listSku) {
								if (map.containsKey(sku.getSkuAttribute() + "::" + sku.getDiscount())) {
									value = (map.get(sku.getSkuAttribute() + "::" + sku.getDiscount()));
									if (value != null)
										map.put(sku.getSkuAttribute() + "::" + sku.getDiscount(),
												value.concat(", ") + sku.getSkuValue());
								} else {
									map.put(sku.getSkuAttribute() + "::" + sku.getDiscount(), sku.getSkuValue());
								}
							}
						}
						/*if (dupflag && listSku.size() == 0) {
							MessageUtil.setMessage("All SKU/s are selected for another discount code.", "color:red", "TOP");
							return;
						}*/

						
						end = endId;
						if (start == null)
							start = startId;
						limit = start + "::" + end;
						for (Entry<String, String> entry : map.entrySet()) {
							boolean flag = false;
							String type = entry.getKey().split("::")[0];
							type = type.contains("Vendor") ? "Vendor Code"
									: type.contains("Department") ? "Department Code"
											: type.contains("Subclass") ? "Sub-Class" : type;
							
							
							/*if(type.toLowerCase().startsWith("udf")){
								List<Listitem> selectedItems=itemAttributeLbId.getItems();
								for (Listitem listitem : selectedItems) {
									if(listitem.getValue().toString().equalsIgnoreCase(type)){
										type=listitem.getLabel();
									}
								}
							}*/
							if(type.contains("UDF")||type.contains("udf")){
								type = udfDisplaySKUMap.get(type);
							}
							
							Label tempRulelbl=null;
							//if(multipleSingleId.isChecked()){
							if(ruleType.equalsIgnoreCase(multipleSingle)) {
							if (discountGenRowsId.getVisibleItemCount() > 0) {
								List<Component> rowList = discountGenRowsId.getChildren();
								if (rowList != null && rowList.size() > 0) {
									for (Object object : rowList) {
										
										boolean maxDiscountExists =  false;
										//boolean quantityExists =(( higestPriceRadioId.isSelected() || higestPricewithoutRadioId.isSelected())&& quantityDblBxId.getValue() != null);
										boolean quantityExists =quantityDblBxId.getValue() != null;

										if(flag){
											continue;
										}
										
										Row existRow = (Row) object;
										
										//if(oneCombinationId.isChecked()){
										if(ruleType.equalsIgnoreCase(combined)) {
											
											 tempRulelbl = (Label) existRow.getChildren().get(1);
										}

										
										
										Label tempDisLbl = (Label) existRow.getChildren().get(0);
										Label tempTypelbl = (Label) existRow.getChildren().get(1);
										Label tempMaxlbl = null;
										Label tempquanlbl = null;
										if(column6Id.isVisible()){
											
											tempquanlbl = (Label)existRow.getChildren().get(2);
										}/*else{
											tempquanlbl.setValue("--");
										}*/
										//if(isNewPlugin) {
										boolean isCheckOnmaXDisc = maxDiscountExists ;
										boolean ischeckOnQuantinty = (quantityExists && !tempquanlbl.getValue().isEmpty() && !tempquanlbl.equals("--") && 
												tempquanlbl.getValue().equals(quantityDblBxId.getValue()));
										
										
										if (tempTypelbl.getValue().split("=")[0].trim().equals(type) && tempDisLbl.getValue().equals("" + discountDblBxId.getValue())) {
											if ((isCheckOnmaXDisc && ischeckOnQuantinty) || ((isCheckOnmaXDisc && !quantityExists ) || (ischeckOnQuantinty && !maxDiscountExists))){
											row = existRow;
											String str = row.getValue();
											startId = Long.parseLong(str.split("::")[0]);
											row.setValue(startId + "::" + endId);
											Label codelbl = (Label) existRow.getChildren().get(1);
											String code = codelbl.getValue().concat("," + map.get(entry.getKey()));
											codelbl.setValue(code);
											flag = true;
											/*List<Long> templist = coupDiscGenDao.findBySkuUserCount(users.getUserId(),
													discountDblBxId.getValue(), entry.getKey().split("::")[0],
													maxDiscountDblBxId != null ? maxDiscountDblBxId.getValue() + "" : null,
															quantityDblBxId != null ?quantityDblBxId.getValue() + "" : null,
													tempCouponObj != null ? tempCouponObj.getCouponId() + "" : null);

											noOfitem = templist.get(0);
											Label noitemLabel = (Label) existRow.getChildren().get(3);
											noitemLabel.setValue(noOfitem.toString());*/
											
											row = existRow;
											String strMdiscount = row.getValue();
											startId = Long.parseLong(strMdiscount.split("::")[0]);
											row.setValue(startId + "::" + endId);
											Label mDiscountlbl = (Label) existRow.getChildren().get(2);
											String mDiscount = maxDiscountDblBxId.getValue() != null ? maxDiscountDblBxId.getValue().toString() :"--";
											//String mDiscount = mDiscountlbl.getValue().concat("," +mDiscountlbl.getValue());
											mDiscountlbl.setValue(mDiscount);
											
											
											row = existRow;
											String strQuantity = row.getValue();
											startId = Long.parseLong(strQuantity.split("::")[0]);
											row.setValue(startId + "::" + endId);
											Label quantitylbl = (Label) existRow.getChildren().get(2);
											String quantity = quantityDblBxId.getValue() != null ? quantityDblBxId.getValue().toString() :"--";
											//String quantity = quantitylbl.getValue().concat("," + quantitylbl.getValue());
											quantitylbl.setValue(quantity);
											
										}
									
									
										else
												
											/*
											 * if (( ((maxDiscountExists && !tempMaxlbl.getValue().isEmpty() &&
											 * !tempMaxlbl.equals("--") &&
											 * tempMaxlbl.getValue().equals(maxDiscountDblBxId.getValue().toString()))&&!
											 * quantityExists) || ((quantityExists && !tempquanlbl.getValue().isEmpty() &&
											 * !tempquanlbl.equals("--") &&
											 * tempquanlbl.getValue().equals(quantityDblBxId.getValue().toString()))
											 * ))&&!maxDiscountExists) {
											 */
												 
											if ((
													((maxDiscountExists)&&
															(!quantityExists && (tempquanlbl==null || (tempquanlbl!=null && (tempquanlbl.getValue().isEmpty() || tempquanlbl.getValue().equals("--"))))))
														//(!quantityExists))

													)) {
												
												
												 existRow = (Row) object;
												 tempDisLbl = (Label) existRow.getChildren().get(0);
												 tempTypelbl = (Label) existRow.getChildren().get(1);
												 tempMaxlbl = (Label)existRow.getChildren().get(2);
												
													//if(isNewPlugin) {
													if (tempDisLbl.getValue().equals("" + discountDblBxId.getValue())&&
															tempTypelbl.getValue().split("=")[0].trim().equals(type)) {
														row = existRow;
														String str = row.getValue();
														startId = Long.parseLong(str.split("::")[0]);
														row.setValue(startId + "::" + endId);
														Label codelbl = (Label) existRow.getChildren().get(1);
														String code = codelbl.getValue().concat("," + map.get(entry.getKey()));
														codelbl.setValue(code);
														flag = true;
														List<Long> templist = coupDiscGenDao.findBySkuUserCount(users.getUserId(),
																discountDblBxId.getValue(), entry.getKey().split("::")[0],
																maxDiscountDblBxId != null ? maxDiscountDblBxId.getValue() + "" : null,
																		quantityDblBxId != null ?quantityDblBxId.getValue() + "" : null,
																tempCouponObj != null ? tempCouponObj.getCouponId() + "" : null);

														noOfitem = templist.get(0);
														Label noitemLabel = (Label) existRow.getChildren().get(2);
														noitemLabel.setValue(noOfitem.toString());
														
														row = existRow;
														String strMdiscount = row.getValue();
														startId = Long.parseLong(strMdiscount.split("::")[0]);
														row.setValue(startId + "::" + endId);
														Label mDiscountlbl = (Label) existRow.getChildren().get(2);
														String mDiscount = maxDiscountDblBxId .getValue()!= null ? maxDiscountDblBxId.getValue().toString() :"--";
														//String mDiscount = mDiscountlbl.getValue().concat("," +mDiscountlbl.getValue());
														mDiscountlbl.setValue(mDiscount);
														
													}
											
											}else 
												if ((!maxDiscountExists )&&
														((quantityExists && tempquanlbl!=null && !tempquanlbl.getValue().isEmpty() && !tempquanlbl.equals("--") && 
																tempquanlbl.getValue().equals(quantityDblBxId.getValue().toString())))){
													 existRow = (Row) object;
													 tempDisLbl = (Label) existRow.getChildren().get(0);
													 tempTypelbl = (Label) existRow.getChildren().get(1);
													 //tempMaxlbl = (Label)existRow.getChildren().get(4);
													 //tempquanlbl = (Label)existRow.getChildren().get(3);
													 //Check once
														/*if(quantityExists){
															
															tempquanlbl = (Label)existRow.getChildren().get(2);
														}*/
													
														//if(isNewPlugin) {
												
														if (tempDisLbl.getValue().toString().equals(discountDblBxId.getValue().toString())&&
																tempTypelbl.getValue().toString().split("=")[0].trim().equals(type.toString())) {
															row = existRow;
															String str = row.getValue();
															startId = Long.parseLong(str.split("::")[0]);
															row.setValue(startId + "::" + endId);
															Label codelbl = (Label) existRow.getChildren().get(1);
															String code = codelbl.getValue().concat("," + map.get(entry.getKey()));
															codelbl.setValue(code);
															flag = true;
															/*List<Long> templist = coupDiscGenDao.findBySkuUserCount(users.getUserId(),
																	discountDblBxId.getValue(), entry.getKey().split("::")[0],
																	maxDiscountDblBxId != null ? maxDiscountDblBxId.getValue() + "" : null,
																			quantityDblBxId != null ?quantityDblBxId.getValue() + "" : null,
																	tempCouponObj != null ? tempCouponObj.getCouponId() + "" : null);

															noOfitem = templist.get(0);
															Label noitemLabel = (Label) existRow.getChildren().get(3);
															noitemLabel.setValue(noOfitem.toString());*/
															
															row = existRow;
															String strQuantity = row.getValue();
															startId = Long.parseLong(strQuantity.split("::")[0]);
															row.setValue(startId + "::" + endId);
															Label quantitylbl = (Label) existRow.getChildren().get(2);
															String quantity = quantityDblBxId.getValue() != null ? quantityDblBxId.getValue().toString() :"--";
															//String quantity = quantitylbl.getValue().concat("," + quantitylbl.getValue());
															quantitylbl.setValue(quantity);	
												}
												}
											else if ((
													((maxDiscountExists && !tempMaxlbl.getValue().isEmpty() && !tempMaxlbl.equals("--") && 
															tempMaxlbl.getValue().equals(maxDiscountDblBxId.getValue().toString()))
															&&(quantityExists && tempquanlbl!=null && !tempquanlbl.getValue().isEmpty() && !tempquanlbl.equals("--") && 
																	tempquanlbl.getValue().equals(quantityDblBxId.getValue().toString())) )
													&& ((quantityExists && !tempquanlbl.getValue().isEmpty() && !tempquanlbl.equals("--") && 
															tempquanlbl.getValue().equals(quantityDblBxId.getValue().toString())) ))
													&&(maxDiscountExists && !tempMaxlbl.getValue().isEmpty() && !tempMaxlbl.equals("--") && 
															tempMaxlbl.getValue().equals(maxDiscountDblBxId.getValue().toString()))) {
												
												
												 existRow = (Row) object;
												 tempDisLbl = (Label) existRow.getChildren().get(0);
												 tempTypelbl = (Label) existRow.getChildren().get(1);
												// tempMaxlbl = (Label)existRow.getChildren().get(2);
												 //tempquanlbl = (Label)existRow.getChildren().get(2);
													if(column6Id.isVisible()){
														
														tempquanlbl = (Label)existRow.getChildren().get(2);
													}
												
													//if(isNewPlugin) {
													if (tempDisLbl.getValue().equals("" + discountDblBxId.getValue())
													//&&tempMaxlbl.getValue().equals("" + maxDiscountDblBxId.getValue())
													&& quantityExists && tempquanlbl!=null && tempquanlbl.getValue().equals("" + quantityDblBxId.getValue())&&
													tempTypelbl.getValue().split("=")[0].trim().equals(type)) {
														row = existRow;
														String str = row.getValue();
														startId = Long.parseLong(str.split("::")[0]);
														row.setValue(startId + "::" + endId);
														Label codelbl = (Label) existRow.getChildren().get(1);
														String code = codelbl.getValue().concat("," + map.get(entry.getKey()));
														codelbl.setValue(code);
														flag = true;
														/*List<Long> templist = coupDiscGenDao.findBySkuUserCount(users.getUserId(),
																discountDblBxId.getValue(), entry.getKey().split("::")[0],
																maxDiscountDblBxId != null ? maxDiscountDblBxId.getValue() + "" : null,
																		quantityDblBxId != null ?quantityDblBxId.getValue() + "" : null,
																tempCouponObj != null ? tempCouponObj.getCouponId() + "" : null);

														noOfitem = templist.get(0);
														Label noitemLabel = (Label) existRow.getChildren().get(3);
														noitemLabel.setValue(noOfitem.toString());*/
														
														row = existRow;
														String strMdiscount = row.getValue();
														startId = Long.parseLong(strMdiscount.split("::")[0]);
														row.setValue(startId + "::" + endId);
														Label mDiscountlbl = (Label) existRow.getChildren().get(2);
														String mDiscount = maxDiscountDblBxId.getValue() != null ? maxDiscountDblBxId.getValue().toString() :"--";
														//String mDiscount = mDiscountlbl.getValue().concat("," +mDiscountlbl.getValue());
														mDiscountlbl.setValue(mDiscount);
														
														
														row = existRow;
														String strQuantity = row.getValue();
														startId = Long.parseLong(strQuantity.split("::")[0]);
														row.setValue(startId + "::" + endId);
														Label quantitylbl = (Label) existRow.getChildren().get(2);
														String quantity = quantityDblBxId.getValue()!= null ? quantityDblBxId.getValue().toString() :"--";
														//String quantity = quantitylbl.getValue().concat("," + quantitylbl.getValue());
														quantitylbl.setValue(quantity);											
													}
											}
											
										
										
											else if ((!maxDiscountExists )
													&& (!quantityExists && (tempquanlbl==null || (tempquanlbl!=null && (tempquanlbl.getValue().isEmpty() || tempquanlbl.getValue().equals("--")))))){
											//	&& (!quantityExists)){

											
											row = existRow;
											String str = row.getValue();
											startId = Long.parseLong(str.split("::")[0]);
											row.setValue(startId + "::" + endId);
											Label codelbl = (Label) existRow.getChildren().get(1);
											String code = codelbl.getValue().concat("," + map.get(entry.getKey()));
											codelbl.setValue(code);
											flag = true;
											/*List<Long> templist = coupDiscGenDao.findBySkuUserCount(users.getUserId(),
													discountDblBxId.getValue(), entry.getKey().split("::")[0],
													maxDiscountDblBxId != null ? maxDiscountDblBxId.getValue() + "" : null,
															quantityDblBxId != null ?quantityDblBxId.getValue() + "" : null,
													tempCouponObj != null ? tempCouponObj.getCouponId() + "" : null);

											noOfitem = templist.get(0);
											Label noitemLabel = (Label) existRow.getChildren().get(3);
											noitemLabel.setValue(noOfitem.toString());*/
											
											row = existRow;
											String strMdiscount = row.getValue();
											startId = Long.parseLong(strMdiscount.split("::")[0]);
											row.setValue(startId + "::" + endId);
											Label mDiscountlbl = (Label) existRow.getChildren().get(2);
											String mDiscount = maxDiscountDblBxId.getValue() != null ? maxDiscountDblBxId.getValue().toString() :"--";
											//String mDiscount = mDiscountlbl.getValue().concat("," +mDiscountlbl.getValue());
											mDiscountlbl.setValue(mDiscount);
											
											
											row = existRow;
											String strQuantity = row.getValue();
											startId = Long.parseLong(strQuantity.split("::")[0]);
											row.setValue(startId + "::" + endId);
											Label quantitylbl = (Label) existRow.getChildren().get(2);
											String quantity = quantityDblBxId.getValue() != null ? quantityDblBxId.getValue().toString() :"--";
											//String quantity = quantitylbl.getValue().concat("," + quantitylbl.getValue());
											quantitylbl.setValue(quantity);
										
										}else 
										{
											flag=false;
											
										}
																
									}
							}
								}}
							}
							if (!flag) {
								row = new Row();
								row.setValue(startId + "::" + endId);
								label = new Label(discountDblBxId.getValue().toString());
								label.setParent(row);

								
								String customFieldValue=null;
								/*Set<Listitem> selectedItems=itemAttributeLbId.getSelectedItems();
								for (Listitem listitem : selectedItems) {
									customFieldValue=listitem.getValue();
								}
*/
								if(type.contains("UDF")||type.contains("udf")){
									type = udfDisplaySKUMap.get(type);
								}								/*label = new Label(type);
								label.setAttribute("Type", entry.getKey().split("::")[0]);
								label.setParent(row);

								label = new Label(map.get(entry.getKey()));
								label.setParent(row);
								
								List<Long> templist=null;
								templist = coupDiscGenDao.findBySkuUserCount(users.getUserId(),
										discountDblBxId.getValue(), entry.getKey().split("::")[0],
										maxDiscountDblBxId != null ? maxDiscountDblBxId.getValue() + "" : null,
												quantityDblBxId != null ? quantityDblBxId.getValue() + "" : null,
														tempCouponObj != null ? tempCouponObj.getCouponId() + "" : null);							
								
								noOfitem = templist.get(0);
								label = new Label(noOfitem.toString());
								label.setParent(row);
								
								
		*/
								//if(oneCombinationId.isChecked()){
								if(ruleType.equalsIgnoreCase(combined)) {
									label = new Label();
									label.setAttribute("Type", entry.getKey().split("::")[0]);
									label.setParent(row);

									
								}else{
									logger.info("values displayed==========="+map.get(entry.getKey()));
									label = new Label(type+" = "+map.get(entry.getKey()));
									label.setAttribute("Type", entry.getKey().split("::")[0]);
									label.setParent(row);
									
								}

								
									if (accumulateCoupChkBxId.isChecked()) {
										/*if(maxDiscountDblBxId.getValue()!=null) {
										label = new Label(maxDiscountDblBxId.getValue().toString());
										label.setValue(maxDiscountDblBxId.getValue().toString());
										label.setParent(row);
									}else {
										label = new Label("--");
										label.setParent(row);
									}*/
									
										//if (higestPriceRadioId.isSelected() || higestPricewithoutRadioId.isSelected()) {
											if(quantityDblBxId.getValue() != null){
												label = new Label(quantityDblBxId.getValue().toString());
												label.setValue(quantityDblBxId.getValue().toString());
												label.setParent(row);
											}else{
												label = new Label("--");
												label.setParent(row);
											}

										/*} else {
											label = new Label("--");
											label.setParent(row);
										}*/
									} else {
										/*label = new Label("--");
										label.setParent(row);*/
										//if (higestPriceRadioId.isSelected() || higestPricewithoutRadioId.isSelected()) {
											if(quantityDblBxId.getValue() != null){
												label = new Label(quantityDblBxId.getValue().toString());
												label.setValue(quantityDblBxId.getValue().toString());
												label.setParent(row);
											}else{
												label = new Label("--");
												label.setParent(row);
											}

										/*} else {
											label = new Label("--");
											label.setParent(row);
										}*/
									}
								
							/*	Hbox hbox = new Hbox();
								
								//APP-2720
								Image editImg = new Image("/img/email_edit.gif");
								editImg.setStyle("cursor:pointer;margin-right:10px;");
								editImg.setAttribute("COUPON_TYPE", COUPON_DISCOUNT_EDIT);
								editImg.setAttribute("COUPON_OBJ",tempCouponObj);
								editImg.addEventListener("onClick", this);
								editImg.setParent(hbox);*/

								Image delImg = new Image("img/delt_icn.png");
								delImg.setStyle("cursor:pointer;");
								delImg.setAttribute("COUPON_TYPE", COUPON_DISCOUNT_DELETE);
								delImg.addEventListener("onClick", this);
								delImg.setParent(row);
								
							//	hbox.setParent(row);

								row.setParent(discountGenRowsId);
							}
						}
						allItems.removeAll(selctedItemSet);
						logger.info(" Remain item list size " + allItems);
						Iterator<Listitem> it = allItems.iterator();
						while (it.hasNext()) {
							it.next().setParent(addedSKULBId);
						}
					}
					}
			
			
			
	//else if(oneCombinationId.isChecked()){
					else if(ruleType.equalsIgnoreCase(combined)) {
			List<Listitem> allItems = addedSKULBId.getItems();
			Set<Listitem> selctedItemSet = addedSKULBId.getSelectedItems();
			if (selctedItemSet == null || selctedItemSet.size() == 0) {
				MessageUtil.setMessage("Please select at least one attribute code for creating discount rule.",
						"color:red", "TOP");
				return;
			}
			if (discountGenRowsId.getVisibleItemCount() > 0) {
				
				MessageUtil.setMessage("Only one AND rule is allowed.",
						"color:blue", "TOP");
				return;
			}
			Row row = null;
			Label label = null;
			List<SKUTemp> listSku = null;
			Long startId = null;
			Long endId = null;
			boolean dupflag = true;
			Map<String, String> map = new LinkedHashMap<>();
			Map<String, String> map1 = new LinkedHashMap<>();
			List<SKUTemp> oldlistSku = coupDiscGenDao.findTempSkuBy(users.getUserId(), null, limit);
			if (selctedItemSet != null && selctedItemSet.size() > 0) {
				listSku = new LinkedList<>();
				Iterator<Listitem> itr = selctedItemSet.iterator();
				allItems = addedSKULBId.getItems();
				logger.info(" Total item size :" + allItems.size());
				while (itr.hasNext()) {
					Listitem item = itr.next();
					List<Component> cells = item.getChildren();
					SKUTemp skuTemp = null;
					String attribute = null;
					String value = null;
					String type = null;
					for (Component cmp : cells) {
						Listcell cell = (Listcell) cmp;
						String[] values=null;
						
						//if(!cell.getLabel().contains("AND")) continue;
						if (cell.getValue() != null && cell.getValue().toString().startsWith("key")) {
							String valueOfKey=cell.getValue().toString().split("::")[1];
							
							if(valueOfKey.contains(",,,")){
								
								values=valueOfKey.split(",,,");
								
							for(String eachStr : values){
								
								attribute = eachStr.split("=")[0].trim();
								value= eachStr.split("=")[1].trim();
							
								/*Set<Listitem> selectedItems = itemAttributeLbId.getSelectedItems();
								for (Listitem li : selectedItems) {
									if (li.getLabel().equalsIgnoreCase(attribute)) {
										type = li.getValue().toString();
									}
									
								}
								*/
								boolean flag = false;
								if (oldlistSku != null && oldlistSku.size() > 0) {
									for (SKUTemp oldSku : oldlistSku) {
										logger.info(oldSku.getSkuAttribute()+ "=="+oldSku.getSkuValue());
										if (oldSku.getSkuAttribute().equals(attribute) && oldSku.getSkuValue().equals(value)
												&& oldSku.getDiscount()
												.equals(Double.parseDouble(discountDblBxId.getValue().toString())))
											flag = true;
									}
								}
								if (flag)
									continue;
								
								
								skuTemp = new SKUTemp();
								skuTemp.setUserId(users.getUserId());
								skuTemp.setDiscount(discountDblBxId.getValue());
								skuTemp.setDiscountType(discountTypeStr);
								skuTemp.setSkuAttribute(udfRuleSKUMap.get(attribute)!=null?udfRuleSKUMap.get(attribute):attribute);
								skuTemp.setSkuValue(value);
								skuTemp.setOwnerId(orgOwnerUserId);
								//if (isNewPlugin) {
								if (skuRadioId.isChecked()) {
									if(accumulateCoupChkBxId.isChecked()) {
										skuTemp.setMaxDiscount(maxDiscountDblBxId.getValue());
										// skuTemp.setQuantity(quantityDblBxId.getValue());
										//if (higestPriceRadioId.isSelected() || higestPricewithoutRadioId.isSelected()) {
											skuTemp.setQuantity(quantityDblBxId.getValue() != null ? String.valueOf(quantityDblBxId.getValue()) : null);
											skuTemp.setLimitQuantity(quantityDblBxId.getValue() != null ? limitQuantity : null);
										//}
									}else {
										//if (higestPriceRadioId.isSelected() || higestPricewithoutRadioId.isSelected()) {
											skuTemp.setQuantity(quantityDblBxId.getValue() != null ? String.valueOf(quantityDblBxId.getValue()) : null);
											skuTemp.setLimitQuantity(quantityDblBxId.getValue() != null ? limitQuantity : null);

										//}
									}
									
								}
								
								if (tempCouponObj != null)
									skuTemp.setCouponId(tempCouponObj.getCouponId());
								listSku.add(skuTemp);
							}
								
						}else{

							
							attribute = valueOfKey.split("=")[0].trim();
							value= valueOfKey.split("=")[1].trim();
						
							/*Set<Listitem> selectedItems = itemAttributeLbId.getSelectedItems();
							for (Listitem li : selectedItems) {
								if (li.getLabel().equalsIgnoreCase(attribute)) {
									type = li.getValue().toString();
								}
								
							}
							*/
							boolean flag = false;
							if (oldlistSku != null && oldlistSku.size() > 0) {
								for (SKUTemp oldSku : oldlistSku) {
									logger.info(oldSku.getSkuAttribute()+ "=="+oldSku.getSkuValue());
									if (oldSku.getSkuAttribute().equals(attribute) && oldSku.getSkuValue().equals(value)
											&& oldSku.getDiscount()
											.equals(Double.parseDouble(discountDblBxId.getValue().toString())))
										flag = true;
								}
							}
							if (flag)
								continue;
							
							
							skuTemp = new SKUTemp();
							skuTemp.setUserId(users.getUserId());
							skuTemp.setDiscount(discountDblBxId.getValue());
							skuTemp.setDiscountType(discountTypeStr);
							skuTemp.setSkuAttribute(udfRuleSKUMap.get(attribute)!=null?udfRuleSKUMap.get(attribute):attribute);
							skuTemp.setSkuValue(value);
							skuTemp.setOwnerId(orgOwnerUserId);
							//if (isNewPlugin) {
							if (skuRadioId.isChecked()) {
								if(accumulateCoupChkBxId.isChecked()) {
									skuTemp.setMaxDiscount(maxDiscountDblBxId.getValue());
									// skuTemp.setQuantity(quantityDblBxId.getValue());
								//	if (higestPriceRadioId.isSelected() || higestPricewithoutRadioId.isSelected()) {
										skuTemp.setQuantity(quantityDblBxId.getValue() != null ? String.valueOf(quantityDblBxId.getValue()) : null);
										skuTemp.setLimitQuantity(quantityDblBxId.getValue() != null ? limitQuantity : null);

								//	}
								}else {
									//if (higestPriceRadioId.isSelected() || higestPricewithoutRadioId.isSelected()) {
										skuTemp.setQuantity(quantityDblBxId.getValue() != null ? String.valueOf(quantityDblBxId.getValue()) : null);
										skuTemp.setLimitQuantity(quantityDblBxId.getValue() != null ? limitQuantity : null);

									//}
								}
								
							}
							
							if (tempCouponObj != null)
								skuTemp.setCouponId(tempCouponObj.getCouponId());
							listSku.add(skuTemp);
						
						}
								
							



						}
					}
			
				
					if (listSku.size() >= 500) {
						coupDiscGenDaoForDML.saveByCollection(listSku);
						Comparator<SKUTemp> comparator = Comparator.comparing(SKUTemp::getSkuTempId);
						SKUTemp minObject = listSku.stream().min(comparator).get();
						if (startId == null)
							startId = minObject.getSkuTempId();
						endId = listSku.get(listSku.size() - 1).getSkuTempId();
						Collections.reverse(listSku);
						for (SKUTemp sku : listSku) {
							if (map.containsKey(sku.getSkuAttribute() + "::" + sku.getDiscount())) {
								value = (map.get(sku.getSkuAttribute() + "::" + sku.getDiscount()));
								if (value != null)
									map.put(sku.getSkuAttribute() + "::" + sku.getDiscount(),
											value.concat(", ") + sku.getSkuValue());
							} else {
								map.put(sku.getSkuAttribute() + "::" + sku.getDiscount(), sku.getSkuValue());
							}
						}
						dupflag = false;
						listSku = new ArrayList<>();
					}
					// item.setDisabled(true);
				}

				if (listSku.size() > 0 && listSku.size() < 500) {
					coupDiscGenDaoForDML.saveByCollection(listSku);
					Comparator<SKUTemp> comparator = Comparator.comparing(SKUTemp::getSkuTempId);
					SKUTemp minObject = listSku.stream().min(comparator).get();
					if (startId == null)
						// startId=skuList.get(0).getSkuTempId();
						startId = minObject.getSkuTempId();
					endId = listSku.get(listSku.size() - 1).getSkuTempId();
					String value = null;
					Collections.reverse(listSku);
					for (SKUTemp sku : listSku) {
						if (map.containsKey(sku.getSkuAttribute() + "::" + sku.getDiscount())) {
							value = (map.get(sku.getSkuAttribute() + "::" + sku.getDiscount()));
							if (value != null)
								map.put(sku.getSkuAttribute() + "::" + sku.getDiscount(),
										value.concat(", ") + sku.getSkuValue());
						} else {
							map.put(sku.getSkuAttribute() + "::" + sku.getDiscount(), sku.getSkuValue());
						}
					}
				}
				/*if (dupflag && listSku.size() == 0) {
					MessageUtil.setMessage("All SKU/s are selected for another promotion.", "color:red", "TOP");
					return;
				}*/
				end = endId;
				if (start == null)
					start = startId;
				limit = start + "::" + end;
				String ruleToInsert=null;
				String rule=null;
			//	String newRule="";
				String typeAttr=null;
				for (Entry<String, String> entry : map.entrySet()) {
					
					if (discountGenRowsId.getVisibleItemCount() > 0) {
						
						MessageUtil.setMessage("Only one AND rule is allowed.",
								"color:blue", "TOP");
						return;
						
						/* List<Component> rowList = discountGenRowsId.getChildren();
						if (rowList != null && rowList.size() > 0) {
							for (Object object : rowList) {
								
								Row existRow = (Row) object;
								
								Label tempTypelbl = (Label) existRow.getChildren().get(1);
								
								if(tempTypelbl.getValue().contains("AND")){
									MessageUtil.setMessage("Only one AND rule is allowed .",
											"color:blue", "TOP");
									return;
								}else{

									MessageUtil.setMessage("ONE combination rule cannot be added as you have already added Multiple single rules.",
											"color:blue", "TOP");
									return;
								}
							}
						}*/
					}
					String type = entry.getKey().split("::")[0];
					type = type.contains("Vendor") ? "Vendor Code"
							: type.contains("Department") ? "Department Code"
									: type.contains("Subclass") ? "Sub-Class" : type;
					
					/*
					if(type.toLowerCase().startsWith("udf")){
						List<Listitem> selectedItems=itemAttributeLbId.getItems();
						for (Listitem listitem : selectedItems) {
							if(listitem.getValue().toString().equalsIgnoreCase(type)){
								type=listitem.getLabel();
							}
						}
					}*/
					if(type.contains("UDF")||type.contains("udf")){
						type = udfDisplaySKUMap.get(type);
					}
				
					String value=map.get(entry.getKey());
					
				
					boolean replace=false;
					String newRule="";
					if(rule==null){
						rule = type+"="+value;
						if(ruleToInsert==null){
							ruleToInsert=type +"="+value;
						}else{
							ruleToInsert+= "," +type +"="+value;

						}
						
					}else{
						if(rule.contains(":::")){
							String[] ruleArr = rule.split(":::");
							
							for(String rulee : ruleArr){
								
							String	headerLabel =rulee.split("=")[0];
								
								if(headerLabel.trim().equals(type)){
									if(ruleToInsert==null){
										ruleToInsert=type +"="+value;					

									}else{
										ruleToInsert+= "," +type +"="+value;

									}
									logger.info(""+rulee+","+value);
									newRule+=" ::: "+rulee+","+value;
									replace=true;
									
									
								}else{
									
									if(!newRule.isEmpty()){
										if(ruleToInsert==null){
											ruleToInsert=type +"="+value;						

										}else{
											ruleToInsert+= "," +type +"="+value;

										}
										newRule+=" ::: " +rulee;
										
									}else{
										
										newRule=rulee;
										if(ruleToInsert==null){
											ruleToInsert=type +"="+value;										

										}else{
											ruleToInsert+= "," +type +"="+value;

										}
									}
								}
							}
								rule=newRule;	
							
							if(!replace){
								
								rule+= " ::: "+type +"="+value;
								
								if(ruleToInsert==null){
									ruleToInsert=type +"="+value;

								}else{
									ruleToInsert+= "," +type +"="+value;

								}
							}
							
							
						}else{
							if(rule.split("=")[0].equals(type)){
								
								rule=rule+","+value;
								if(ruleToInsert==null){
									ruleToInsert=type +"="+value;

								}else{
									ruleToInsert+= "," +type +"="+value;

								}
								
							}else{
								rule+= " ::: "+type +"="+value;
								if(ruleToInsert==null){
									ruleToInsert=type +"="+value;

								}else{
									ruleToInsert+= "," +type +"="+value;

								}						
								}
							
						}
						
					}
					
					
					if(typeAttr==null){
						typeAttr=entry.getKey().split("::")[0];
					}else{
						typeAttr+=","+entry.getKey().split("::")[0];
					}
				
						
				}
				
				rule=rule.replaceAll(":::", " AND ");
				
						row = new Row();
						row.setValue(startId + "::" + endId);
						label = new Label(discountDblBxId.getValue().toString());
						label.setParent(row);

						
						
							label = new Label(rule);
							label.setAttribute("Type", typeAttr);
							label.setParent(row);

							if (accumulateCoupChkBxId.isChecked()) {
								if(maxDiscountDblBxId.getValue()!=null) {
								label = new Label(maxDiscountDblBxId.getValue().toString());
								label.setValue(maxDiscountDblBxId.getValue().toString());
								label.setParent(row);
							}else {
								label = new Label("--");
								label.setParent(row);
							}
							
							//	if (higestPriceRadioId.isSelected() || higestPricewithoutRadioId.isSelected()) {
									if(quantityDblBxId.getValue() != null){
										label = new Label(quantityDblBxId.getValue().toString());
										label.setValue(quantityDblBxId.getValue().toString());
										label.setParent(row);
									}else{
										label = new Label("--");
										label.setParent(row);
									}

								/*} else {
									label = new Label("--");
									label.setParent(row);
								}*/
							} else {
								/*label = new Label("--");
								label.setParent(row);*/
								//if (higestPriceRadioId.isSelected() || higestPricewithoutRadioId.isSelected()) {
									if(quantityDblBxId.getValue() != null){
										label = new Label(quantityDblBxId.getValue().toString());
										label.setValue(quantityDblBxId.getValue().toString());
										label.setParent(row);
									}else{
										label = new Label("--");
										label.setParent(row);
									}

								/*} else {
									label = new Label("--");
									label.setParent(row);
								}*/
							}
						
					/*	Hbox hbox = new Hbox();
						
						//APP-2720
						Image editImg = new Image("/img/email_edit.gif");
						editImg.setStyle("cursor:pointer;margin-right:10px;");
						editImg.setAttribute("COUPON_TYPE", COUPON_DISCOUNT_EDIT);
						editImg.addEventListener("onClick", this);
						editImg.setParent(hbox);*/

							
						Image delImg = new Image("img/delt_icn.png");
						delImg.setStyle("cursor:pointer;");
						delImg.setAttribute("COUPON_TYPE", COUPON_DISCOUNT_DELETE);
						delImg.addEventListener("onClick", this);
						delImg.setParent(row);
						
						//hbox.setParent(row);

						row.setParent(discountGenRowsId);
				
			}
		
				allItems.removeAll(selctedItemSet);
				logger.info(" Remain item list size " + allItems);
				Iterator<Listitem> it = allItems.iterator();
				while (it.hasNext()) {
					it.next().setParent(addedSKULBId);
				}
		}
		}
		else {
			Long tpAmtLong = totPurAmtLngBxId.getValue() == null ? 0 : totPurAmtLngBxId.getValue();
			List<SKUTemp> oldlistSku = coupDiscGenDao.findTempSkuBy(users.getUserId(), null, limit);

			List<SKUTemp> skuList = new ArrayList<>();
			if (oldlistSku != null && oldlistSku.size() > 0) {
				for (SKUTemp sku : oldlistSku) {

					if (sku.getDiscount().equals(Double.parseDouble(discountDblBxId.getValue().toString()))) {
						MessageUtil.setMessage(discountDblBxId.getValue().toString() + " Value already selected..,",
								"color:red", "TOP");
						return;
					}

					else if (sku.getTotPurchaseAmount().equals(tpAmtLong)) {
						MessageUtil.setMessage(
								tpAmtLong != 0 ? tpAmtLong.toString() : "N/A" + " Value already selected..,",
								"color:red", "TOP");
						return;
					} else if (mpvChkBoxId.isChecked()
							&& (sku.getTotPurchaseAmount() != null && tpAmtLong < sku.getTotPurchaseAmount()
									? sku.getDiscount() < (Double.parseDouble(discountDblBxId.getValue().toString()))
									: sku.getDiscount() > (Double
											.parseDouble(discountDblBxId.getValue().toString())))) {
						MessageUtil.setMessage(
								"Minimum Purchase Amount and Discount Type values should be greater Previous Conditions",
								"color:red", "TOP");
						return;
					}

				

				}
			}
			SKUTemp skuTemp = new SKUTemp();
			skuTemp.setUserId(users.getUserId());
			skuTemp.setDiscount(discountDblBxId.getValue());
			skuTemp.setDiscountType(discountTypeStr);
			skuTemp.setTotPurchaseAmount(tpAmtLong);
			skuTemp.setOwnerId(orgOwnerUserId);

			//if (isNewPlugin) {
				if (tpaRadioId.isChecked()) {
					skuTemp.setMaxDiscount(maxDiscountDblBxId.getValue());
				} /*else if (skuRadioId.isChecked()) {
					if(accumulateCoupChkBxId.isChecked()) {
					skuTemp.setMaxDiscount(maxDiscountDblBxId.getValue());
					// skuTemp.setQuantity(quantityDblBxId.getValue());
					if (higestPriceRadioId.isSelected() || higestPricewithoutRadioId.isSelected()) {
						skuTemp.setQuantity(quantityDblBxId.getValue() != null ? String.valueOf(quantityDblBxId.getValue()) : null);
					}
				}else {
					if (higestPriceRadioId.isSelected() || higestPricewithoutRadioId.isSelected()) {
						skuTemp.setQuantity(quantityDblBxId.getValue() != null ? String.valueOf(quantityDblBxId.getValue()) : null);
						}
				}
					
				}*/
			//}

			if (tempCouponObj != null)
				skuTemp.setCouponId(tempCouponObj.getCouponId());
			skuList.add(skuTemp);
			coupDiscGenDaoForDML.saveByCollection(skuList);
			Comparator<SKUTemp> comparator = Comparator.comparing(SKUTemp::getSkuTempId);
			SKUTemp minObject = skuList.stream().min(comparator).get();
			if (start == null)
				// start=skuList.get(0).getSkuTempId();
				start = minObject.getSkuTempId();
			end = skuList.get(skuList.size() - 1).getSkuTempId();
			limit = start + "::" + end;
			Row row = new Row();
			Label label = new Label(discountDblBxId.getValue().toString());
			label.setValue(discountDblBxId.getValue().toString());
			label.setParent(row);

			label = new Label(tpAmtLong != 0 ? tpAmtLong.toString() : "N/A");
			label.setValue(tpAmtLong != 0 ? tpAmtLong.toString() : "N/A");
			label.setParent(row);

			/*label = new Label("");
			label.setParent(row);

			label = new Label("");
			label.setParent(row);*/
			
			//if (isNewPlugin) { 
				/* if (accumulateCoupChkBxId.isChecked()) {
			  if(maxDiscountDblBxId.getValue()!=null) { 
				  label = new Label(maxDiscountDblBxId.getValue().toString());
			  label.setValue(maxDiscountDblBxId.getValue().toString());
			  label.setParent(row); 
			  }else { 
				  label = new Label("--"); 
				  label.setParent(row);
			  
			  } 
			  } else { 
				  label =new Label("--"); 
				  label.setParent(row); 
				  }*/
			  
			  /*} else {
			  
			  label = new Label(""); 
			  label.setParent(row); 
			  }*/
			 
			label = new Label("");
			label.setParent(row);
			
			/*Hbox hbox = new Hbox();
			
			//APP-2720
			Image editImg = new Image("/img/email_edit.gif");
			editImg.setStyle("cursor:pointer;");
			editImg.setAttribute("COUPON_TYPE", COUPON_DISCOUNT_EDIT);
			editImg.addEventListener("onClick", this);
			editImg.setParent(hbox);*/
			
			Image delImg = new Image("img/delt_icn.png");
			delImg.setStyle("cursor:pointer;");
			delImg.setAttribute("COUPON_TYPE", COUPON_DISCOUNT_DELETE);
			delImg.addEventListener("onClick", this);
			delImg.setParent(row);
			
			//hbox.setParent(row);

			row.setParent(discountGenRowsId);
		}
		discountDblBxId.setText("");
		maxDiscountDblBxId.setText("");
		quantityDblBxId.setText("");
		if(mpvChkBoxId.isChecked()){
			totPurAmtLngBxId.setText("");
			mpvChkBoxId.setChecked(false);
			
		}

	}
	public boolean onClick$applyDiscBtnId() {
		String eligibleItems="";
		String discTypeForItem="";
		String discValue="";
		String itemQuantity="";
		String itemQtyType="";
		String minPurchaseAmt="";
		String itemPrice="";
		String itemPriceCriteria="";
	    String	discString="";
	    String disc2=null;
	    String DisC1="";
	    String DisC2="";
	    String DisC3="";
	    String DisC4="";
	    String DisC5="";
	    String EliRule="";
	    String DisC6="";
	    String DisC7="";
		String TierProgram="";
		String Tier=null;
		String CardSetProgram="";
		String CardSet=null;
		String purchaseDate="";
	
		String Program=null;
			/*if (accumulateCoupChkBxId.isChecked()) {

				
				
				if(maxDiscountDblBxId.getValue() != null) {
					if (skuRadioId.isChecked()) {
						if(dollerRadioId.isChecked()||percentRadioId.isChecked()) {
							if((discountDblBxId.getValue() > maxDiscountDblBxId.getValue())){
								MessageUtil.setMessage("Discount Value can not exceed the Maximum Discount Value.", "color:red",
										"TOP");
								return;
								
							}
						}
					}else if(!skuRadioId.isChecked()) {
						if(dollerRadioId.isChecked()||percentRadioId.isChecked()) {
							if((discountDblBxId.getValue() > maxDiscountDblBxId.getValue())) {
								MessageUtil.setMessage("Discount Value can not exceed the Maximum Discount Value.", "color:red",
										"TOP");
								return;
								
							}
							
						}
					}
						
					}
			}*/
		//}

		//setheaders();
		
		
				if (skuRadioId.isChecked()) {
					
					String ruleToDisplay="";
					String operator = "";
					List<Component> childVboxList = new ArrayList<Component>();
					
					if(targetDivHasChildren(purIntAttributeDivId))childVboxList.addAll(purIntAttributeDivId.getChildren());

					Div chilVDiv = null;
					
					StringBuffer segmentRuleSB = new StringBuffer();//"all:"+((MailingList)dispMlLBoxId.getSelectedItem().getValue()).getListId());
					String ruleString = null;
					boolean allValid=true;
					
					for (Object obj : childVboxList) {
						
						if(obj instanceof Div) {
							
							boolean isValid = true;
							for(Object object : ((Div)obj).getChildren()) {
								
								if(isValid==false) allValid=false;
								
								//Reset
								isValid = true;
								
								if(object instanceof Div) {
									
									
									chilVDiv = (Div)object;
									if(chilVDiv.getSclass().contains("drop_")) {
										continue;
									}
									
									ruleString = prepareSegmentRule(chilVDiv);
									if(ruleString == null) {
										isValid = false;
									}
									
								///	if(childVboxList.size() > 1)  operator = "<AND>";
									
									if(ruleString != null && ruleString.trim().length()>0) {	
										logger.info("discsegmentRuleDB============+"+segmentRuleSB);
										logger.info("discsegmentRuleDB============+"+segmentRuleSB!=null);
										logger.info("discsegmentRuleDB============+"+segmentRuleSB.length());

										if(segmentRuleSB!=null && segmentRuleSB.length()!=0) segmentRuleSB.append("||");
											
											segmentRuleSB.append(ruleString+operator);
									}
									if(ruleString != null && chilVDiv.getAttribute("columnName")!=null && chilVDiv.getAttribute("replaceColumnName")!=null ) {
										ruleString = ruleString.replace((String)chilVDiv.getAttribute("replaceColumnName"), (String)chilVDiv.getAttribute("columnName"));
										if(segmentRuleSB!=null) segmentRuleSB.append("||");
										
										segmentRuleSB.append(ruleString);
									}
									
									
								}//ruleDiv
								
							}//tempDiv added for And
							
							
							if(isValid==false) allValid=false;
							
						}//if obj
						
					}//for each vb
					if(!allValid) {
						logger.warn(" >>>>>>>>>>>>> Segmentation validation failed ");
						
						MessageUtil.setMessage("Please provide proper value(s) for highlighted rule(s).", "color:red", "TOP");
						return false;
					}
					
					
					/*if(segmentRuleSB.toString().trim().length()==0) {
						
						MessageUtil.setMessage("There are no valid rules to add (as of now, derivative filter rules are ignored).", "color:red","TOP");
					}*/
					logger.debug("finalStr ::"+segmentRuleSB.toString());
					String rule1 =segmentRuleSB.toString();
					EliRule=saveEligibiltyAttributes();
					logger.debug(EliRule);
					if(!EliRule.isEmpty()) {
						EliRule=EliRule.substring(2);
					}
					rule1=rule1+saveEligibiltyAttributes();
					long purchaseAmt = 0;
					StringBuilder sb = new StringBuilder();
					StringBuilder sb1 = new StringBuilder();
					StringBuilder sb2 = new StringBuilder();
					StringBuilder sb3 = new StringBuilder();
					StringBuilder sb4 = new StringBuilder();
					StringBuilder sb5 = new StringBuilder();
					// Fetch Discount attributes
					String discountRules = saveDiscountAttributes();
					logger.info("discountRules "+discountRules);
					if(discountRules.isEmpty()) return false;
					
					if (rule1.contains(Constants.DELIMITER_DOUBLE_PIPE)) {

						String headerLabel="";
						String ruleToInsert=rule1;
						String valueToInsert="";

						String[] ruleStr = rule1.split("\\|\\|");
						for (String eachRule : ruleStr) {
							if(eachRule==null || eachRule.trim().length()<=0) continue;
					
							if(eachRule.startsWith(DiscountQtyPerItem)) {
							//Discount Quantity per Item|quantity|String:GTE|1
							String qtyTypeStr = eachRule.split("\\|")[2];
							String[] qtyTypeArr = qtyTypeStr.split(":");
							itemQtyType=qtyTypeArr[1];
							itemQuantity=eachRule.split("\\|")[3].replace("<OR>","");
							}
							if(eachRule.startsWith(MinReceiptTotal) && !ruleToDisplay.contains(MinReceiptTotal)) {
								minPurchaseAmt=eachRule.split("\\|")[3].replace("<OR>","");
								if(ruleToDisplay.length()>0)ruleToDisplay+=" AND ";
								ruleToDisplay+=""+MinReceiptTotal+"="+minPurchaseAmt;
							}
							if(eachRule.startsWith("Item Price") && !ruleToDisplay.contains("Item Price")) {
								itemPrice=eachRule.split("\\|")[3].replace("<OR>","");
								itemPriceCriteria=eachRule.split("\\|")[2].split(":")[1];
								if(ruleToDisplay.length()>0)ruleToDisplay+=" AND ";
								ruleToDisplay+="Item Price"+itemPriceCriteria+itemPrice;
							}
							
					
							if(eachRule.contains("#PurchaseTier#")) {
								String disc1 = eachRule.split("\\;=;")[0];
								 TierProgram=eachRule.split("\\;=;")[2];
								 Tier=eachRule.split("\\;=;")[3];
								logger.debug(TierProgram);
								logger.debug(Tier);
								StringBuilder tier=sb1.append(TierProgram+",");
								DisC2 =tier.toString();
								
								StringBuilder tier1=sb2.append(Tier+",");
								DisC3=tier1.toString();
						
							}
							if(eachRule.contains("#PurchaseCardSet#")) {
								String disc1 = eachRule.split("\\;=;")[0];
								CardSetProgram=eachRule.split("\\;=;")[2];
								CardSet=eachRule.split("\\;=;")[3];
								logger.debug(CardSetProgram);
								logger.debug(CardSet);
								StringBuilder card=sb3.append(CardSetProgram+",");
								StringBuilder card1=sb4.append(CardSet+",");
								DisC4=card.toString();
								DisC5=card1.toString();
						
							}
						}
					}
					
					//couponRuleStr+= couponRuleStr!=null?"||"+discountRules:discountRules;
					//harshi
					if (discountRules.contains(Constants.DELIMITER_PIPE)) {

						String headerLabel="";
						String ruleToInsert=discountRules;
						String valueToInsert="";

						String[] ruleStr = discountRules.split("\\|\\|");
						for (String eachRule : ruleStr) {
							if(eachRule==null || eachRule.trim().length()<=0) continue;
					
							if(eachRule.startsWith("Eligible Items")) {
								//Eligible Items|no_of_eligible_items|String:all|null
								String eligible = eachRule.split("\\|")[2];
								String[] eligibleItemsType = eligible.split(":");
								eligibleItems=eligibleItemsType[1];
								//if(ruleToDisplay.length()>0)ruleToDisplay+=" AND ";
								//ruleToDisplay+="Eligible Items"+"="+eligibleItems;
							}
							if(eachRule.startsWith("Discount Type")) {
								//Discount Type|discount_type|String:null|1
									String disc = eachRule.split("\\|")[2];
									String[] discType = disc.split(":");
									discTypeForItem = discType[1];
									discValue = eachRule.split("\\|")[3].split("<OR>")[0];
									discountTypeStr = discValue;
							}
					}
					}
					// harshi
					//String limitQuantity=null;
					/*if(comparisionLbId.isVisible()){
						limitQuantity=comparisionLbId.getSelectedItem().getValue().toString();
					}
*/					
					//if(multipleSingleId.isChecked()){
					if(ruleType.equalsIgnoreCase(multipleSingle)) {
					Set<Listitem> selctedItemSet = addedSKUSegmentLBId.getSelectedItems();
					if (selctedItemSet == null || selctedItemSet.size() == 0) {
						MessageUtil.setMessage("Please select at least one attribute code for creating discount rule.",
								"color:red", "TOP");
						return false;
					}
					if (discTypeForItem.equalsIgnoreCase(DISC_TYPE_PERCENTAGE) && (Double.valueOf(discValue) > 100)) {
						MessageUtil.setMessage("Discount percentage cannot be more than 100.", "color:red", "TOP");
						return false;
					}
					Row row = null;
					Label label = null;
					List<SKUTemp> listSku = null;
					Long startId = null;
					Long endId = null;
					boolean dupflag = true;
					Map<String, String> map = new LinkedHashMap<>();
					Map<String, String> map1 = new LinkedHashMap<>();
					Map<String, Long> skutempmap = new LinkedHashMap<>();
					List<SKUTemp> oldlistSku = coupDiscGenDao.findTempSkuBy(users.getUserId(), null, limit);
					String ruletype;
					if (selctedItemSet != null && selctedItemSet.size() > 0) {
						listSku = new LinkedList<>();
						Iterator<Listitem> itr = selctedItemSet.iterator();
						List<Listitem> allItems = addedSKUSegmentLBId.getItems();
						logger.info(" Total item size :" + allItems.size());
						while (itr.hasNext()) {
							Listitem item = itr.next();
							//ruletype=(String)item.getAttribute("ruleType");
							List<Component> cells = item.getChildren();
							SKUTemp skuTemp = null;
							String attribute = null;
							String value = null;
							//String type = null;
							String[] oneOf=null;
							for (Component cmp : cells) {
								Listcell cell = (Listcell) cmp;
								if (cell.getValue() != null && cell.getValue().toString().startsWith("key")) {
									attribute = cell.getValue().toString().split("::")[1];
									value=(String) cell.getAttribute("value");
									oneOf=isCommaAllowed?value.split("isCommaAllowed"):value.split(",");
								} 
							}
							for (int i = 0; i < oneOf.length; i++) {
							boolean flag = false;
							SKUTemp tempSku=null;
							if (oldlistSku != null && oldlistSku.size() > 0) {
								boolean exists=false;
								for (SKUTemp oldSku : oldlistSku) {
									if(editCombinedRuleObj!=null) {
										List<SKUTemp> tempSkuList = new ArrayList<SKUTemp>();
										tempSkuList=editCombinedRuleObj;
										for(SKUTemp editSku:tempSkuList) {
											if(editSku.getSkuTempId()==oldSku.getSkuTempId()) {
												exists=true;
												if(editSku.getSkuAttribute().equalsIgnoreCase(attribute))tempSku=editSku;
											}
										}
										}
									if(exists) continue;
									if (oldSku.getSkuAttribute().equals(attribute) && oldSku.getSkuValue().equals(oneOf[i])
											&& oldSku.getDiscount()
													.equals(Double.parseDouble(discValue)))
										flag = true;
								}
							}
							if (flag)
								continue;
						//	skuTemp = new SKUTemp();
							/*if(tempSku!=null) {
								skuTemp=tempSku;
							}else {
								skuTemp = new SKUTemp();
							}*/
							if(!DisC2.equalsIgnoreCase("")) {
								  DisC2=DisC2.substring(0, DisC2.length() - 1);
								}
							if(!DisC3.equalsIgnoreCase("")) {
								  DisC3=DisC3.substring(0, DisC3.length() - 1);
								}
							if(!DisC4.equalsIgnoreCase("")) {
								  DisC4=DisC4.substring(0, DisC4.length() - 1);
								}
							if(!DisC5.equalsIgnoreCase("")) {
								  DisC5=DisC5.substring(0, DisC5.length() - 1);
								}
				   if(!DisC2.equalsIgnoreCase("") && DisC2.contains(",") )  {    
					
					   if(DisC2.contains(","))
						   {
						   String[] new1=DisC2.split(",");
						   String[] new2=DisC3.split(",");
						int n=new1.length;
						   if(new1.length>1) {
							   for(int k=0;k<n;k++)
							   {
								 //  SKUTemp skuTemp= new SKUTemp();
								   skuTemp = new SKUTemp();
								  DisC2=new1[k];
								  DisC3=new2[k];
								  if(	ruleType.equalsIgnoreCase(multipleSingle)) {
										skuTemp = new SKUTemp();
										skuTemp.setUserId(users.getUserId());
										if(!discValue.isEmpty()) {
										skuTemp.setDiscount(Double.valueOf(Utility.truncateUptoTwoDecimal(discValue)));
										}
										skuTemp.setDiscountType(discTypeForItem);
										skuTemp.setSkuAttribute(udfRuleSKUMap.get(attribute)!=null?udfRuleSKUMap.get(attribute):attribute);
										skuTemp.setSkuValue(oneOf[i]);
										skuTemp.setOwnerId(orgOwnerUserId);
										skuTemp.setNoOfEligibleItems(eligibleItems);
										if(minPurchaseAmt!=null&&!minPurchaseAmt.isEmpty())skuTemp.setTotPurchaseAmount(Long.parseLong(minPurchaseAmt));
									
										if(itemPrice!=null  && !itemPrice.isEmpty())skuTemp.setItemPrice(Double.valueOf(itemPrice));
										skuTemp.setItemPriceCriteria(itemPriceCriteria); }
									else if (ruleType.isEmpty()) {
										skuTemp.setUserId(users.getUserId());
										if(!discValue.isEmpty()) {
										skuTemp.setDiscount(Double.valueOf(Utility.truncateUptoTwoDecimal(discValue)));
										}
										else {
											skuTemp.setDiscount(null);	
										}
									
									
										skuTemp.setDiscountType(discTypeForItem);
										skuTemp.setTotPurchaseAmount(purchaseAmt);
										skuTemp.setOwnerId(orgOwnerUserId);
									}
									
								
								  if(minPurchaseAmt!=null&&!minPurchaseAmt.isEmpty())skuTemp.setTotPurchaseAmount(Long.parseLong(minPurchaseAmt));
								
								  skuTemp.setProgram(DisC2);
									 skuTemp.setTierNum(DisC3);
									 if(!DisC5.equalsIgnoreCase("")) {
										  DisC5=DisC5.substring(0, DisC5.length() - 1);
										  skuTemp.setCardSetNum(DisC5);
									  }
									  else {
										  skuTemp.setCardSetNum(DisC5);
									  }
							
									skuTemp.setEliRule(EliRule);
							
									
							/*	  if (tempCouponObj != null)
									
										skuTemp.setCouponId(tempCouponObj.getCouponId());
									
								  skuTemp.setUserId(users.getUserId()); */
							
							
								
				
							
				   }
				   }
						   }
				   }
				   else if (!DisC4.equalsIgnoreCase("") && DisC4.contains(","))  {    
					//   DisC4=DisC4.substring(0, DisC4.length() - 1);
					 //  DisC5=DisC5.substring(0, DisC5.length() - 1);
					   if(DisC4.contains(","))
						   {
						   String[] new1=DisC4.split(",");
						   String[] new2=DisC5.split(",");
						int n=new1.length;
						   if(new1.length>1) {
							   for(int k=0;k<n;k++)
							   {
								  // SKUTemp skuTemp= new SKUTemp();
								   skuTemp = new SKUTemp();
								  DisC4=new1[k];
								  DisC5=new2[k];
								  if(	ruleType.equalsIgnoreCase(multipleSingle)) {
										skuTemp = new SKUTemp();
										skuTemp.setUserId(users.getUserId());
										if(!discValue.isEmpty()) {
										skuTemp.setDiscount(Double.valueOf(Utility.truncateUptoTwoDecimal(discValue)));
										}
										skuTemp.setDiscountType(discTypeForItem);
										skuTemp.setSkuAttribute(udfRuleSKUMap.get(attribute)!=null?udfRuleSKUMap.get(attribute):attribute);
										skuTemp.setSkuValue(oneOf[i]);
										skuTemp.setOwnerId(orgOwnerUserId);
										skuTemp.setNoOfEligibleItems(eligibleItems);
										if(minPurchaseAmt!=null&&!minPurchaseAmt.isEmpty())skuTemp.setTotPurchaseAmount(Long.parseLong(minPurchaseAmt));
								
										if(itemPrice!=null  && !itemPrice.isEmpty())skuTemp.setItemPrice(Double.valueOf(itemPrice));
										skuTemp.setItemPriceCriteria(itemPriceCriteria); }
									else if (ruleType.isEmpty()) {
										skuTemp.setUserId(users.getUserId());
										if(!discValue.isEmpty()) {
										skuTemp.setDiscount(Double.valueOf(Utility.truncateUptoTwoDecimal(discValue)));
										}
										
										else {
											skuTemp.setDiscount(null);	
										}
									
									
										skuTemp.setDiscountType(discTypeForItem);
										skuTemp.setTotPurchaseAmount(purchaseAmt);
										skuTemp.setOwnerId(orgOwnerUserId);
									}
									
								
								  if(minPurchaseAmt!=null&&!minPurchaseAmt.isEmpty())skuTemp.setTotPurchaseAmount(Long.parseLong(minPurchaseAmt));
								
								  skuTemp.setProgram(DisC4);
									 if(!DisC3.equalsIgnoreCase("")) {
										  DisC3=DisC3.substring(0, DisC3.length() - 1);
										  skuTemp.setTierNum(DisC3);
									  }
									  else {
										  skuTemp.setTierNum(DisC3);
									  }
							
									skuTemp.setCardSetNum(DisC5);
									skuTemp.setEliRule(EliRule);
							
							
							   }         
						   } 
						   }
						   }
						
								   
						   
						
				   else {
					   logger.debug(DisC2); 
						logger.debug(DisC4);
						 if(!DisC2.equalsIgnoreCase(DisC4)&& !DisC2.isEmpty() &&!DisC4.isEmpty()) {
							 MessageUtil.setMessage("Mutliple programs can't be selected in one discount rule", "color:red;");
							 return false ;
					   }
					 //  SKUTemp skuTemp= new SKUTemp();
					   skuTemp = new SKUTemp();
					   if(	ruleType.equalsIgnoreCase(multipleSingle)) {
							skuTemp = new SKUTemp();
							skuTemp.setUserId(users.getUserId());
							if(!discValue.isEmpty()) {
							skuTemp.setDiscount(Double.valueOf(Utility.truncateUptoTwoDecimal(discValue)));
							}
							skuTemp.setDiscountType(discTypeForItem);
							skuTemp.setSkuAttribute(udfRuleSKUMap.get(attribute)!=null?udfRuleSKUMap.get(attribute):attribute);
							skuTemp.setSkuValue(oneOf[i]);
							skuTemp.setOwnerId(orgOwnerUserId);
							skuTemp.setNoOfEligibleItems(eligibleItems);
							if(minPurchaseAmt!=null&&!minPurchaseAmt.isEmpty())skuTemp.setTotPurchaseAmount(Long.parseLong(minPurchaseAmt));
							if(itemPrice!=null  && !itemPrice.isEmpty())skuTemp.setItemPrice(Double.valueOf(itemPrice));
							skuTemp.setItemPriceCriteria(itemPriceCriteria); }
						else if (ruleType.isEmpty()) {
							skuTemp.setUserId(users.getUserId());
							skuTemp.setNoOfEligibleItems(eligibleItems);
							if(itemPrice!=null  && !itemPrice.isEmpty())skuTemp.setItemPrice(Double.valueOf(itemPrice));{
							skuTemp.setItemPriceCriteria(itemPriceCriteria); }
							if(!discValue.isEmpty()) {
							skuTemp.setDiscount(Double.valueOf(Utility.truncateUptoTwoDecimal(discValue)));
							}
							else {
								skuTemp.setDiscount(null);	
							}
					
							skuTemp.setDiscountType(discTypeForItem);
							skuTemp.setTotPurchaseAmount(purchaseAmt);
							skuTemp.setOwnerId(orgOwnerUserId);
							
						}
				  
					   if(minPurchaseAmt!=null&&!minPurchaseAmt.isEmpty())skuTemp.setTotPurchaseAmount(Long.parseLong(minPurchaseAmt));
				
					   if(DisC2.isEmpty()) {
							 skuTemp.setProgram(DisC4);
							 }
							 else if(DisC4.isEmpty()) {
								 skuTemp.setProgram(DisC2);
							 }
							 else if (!DisC2.isEmpty() && !DisC4.isEmpty())
							 {
								 skuTemp.setProgram(DisC2);
							 }
						skuTemp.setTierNum(DisC3);
						skuTemp.setCardSetNum(DisC5);
					//	skuTemp.setDiscItem(Disc_Item);
						skuTemp.setEliRule(EliRule);
					/*	 if(!purchaseDate.equalsIgnoreCase("")) {
							  purchaseDate=purchaseDate.substring(0, purchaseDate.length() - 1);
							  skuTemp.setDateTime(purchaseDate);
						  }
						  else {
							  skuTemp.setDateTime(purchaseDate);
						  }  */
				   }
					/*	if(	ruleType.equalsIgnoreCase(multipleSingle)) {
							skuTemp = new SKUTemp();
							skuTemp.setUserId(users.getUserId());
							if(!discValue.isEmpty()) {
							skuTemp.setDiscount(Double.valueOf(Utility.truncateUptoTwoDecimal(discValue)));
							}
							skuTemp.setDiscountType(discTypeForItem);
							skuTemp.setSkuAttribute(udfRuleSKUMap.get(attribute)!=null?udfRuleSKUMap.get(attribute):attribute);
							skuTemp.setSkuValue(oneOf[i]);
							skuTemp.setOwnerId(orgOwnerUserId);
							skuTemp.setNoOfEligibleItems(eligibleItems);
							if(minPurchaseAmt!=null&&!minPurchaseAmt.isEmpty())skuTemp.setTotPurchaseAmount(Long.parseLong(minPurchaseAmt));
							if(itemPrice!=null  && !itemPrice.isEmpty())skuTemp.setItemPrice(Double.valueOf(itemPrice));
							skuTemp.setItemPriceCriteria(itemPriceCriteria); }
						else if (ruleType.isEmpty()) {
							skuTemp.setUserId(users.getUserId());
							if(!discValue.isEmpty()) {
							skuTemp.setDiscount(Double.valueOf(Utility.truncateUptoTwoDecimal(discValue)));
							}
							else {
								skuTemp.setDiscount(null);	
							}
							skuTemp.setDiscountType(discTypeForItem);
							skuTemp.setTotPurchaseAmount(purchaseAmt);
							skuTemp.setOwnerId(orgOwnerUserId);
						}
						
					//    skuTemp.setStoreNumber(disc2);
					    skuTemp.setProgram(Program);
						skuTemp.setTierNum(Tier);
						skuTemp.setCardSetNum(CardSet);
						skuTemp.setDiscItem(Disc_Item); */
							//if (isNewPlugin) {
								if (tpaRadioId.isChecked()) {
									skuTemp.setMaxDiscount(maxDiscountDblBxId.getValue());
								} else if (skuRadioId.isChecked()) {
									if(accumulateCoupChkBxId.isChecked()) {
									skuTemp.setMaxDiscount(maxDiscountDblBxId.getValue());
									// skuTemp.setQuantity(quantityDblBxId.getValue());
									//if (higestPriceRadioId.isSelected() || higestPricewithoutRadioId.isSelected()) {
									if(itemQuantity!=null && !itemQuantity.isEmpty() && !itemQuantity.equalsIgnoreCase("null"))skuTemp.setQuantity(String.valueOf(getActualNumber(Double.parseDouble(itemQuantity))));
									skuTemp.setLimitQuantity(itemQtyType);
								//	}
								}else {
									//if (higestPriceRadioId.isSelected() || higestPricewithoutRadioId.isSelected()) {
										if(itemQuantity!=null && !itemQuantity.isEmpty() && !itemQuantity.equalsIgnoreCase("null"))skuTemp.setQuantity(String.valueOf(getActualNumber(Double.parseDouble(itemQuantity))));
										skuTemp.setLimitQuantity(itemQtyType);

									//}
								}
									
								}
							//}

							if (tempCouponObj != null)
								skuTemp.setCouponId(tempCouponObj.getCouponId());
							listSku.add(skuTemp);
						}
							if (productDiscountGenRowsId.getVisibleItemCount() > 0) {
								
								List<Component> rowList = productDiscountGenRowsId.getChildren();
								
							if (rowList != null && rowList.size() > 0 && editCombinedRuleObj==null) {
							for (Object object : rowList) {
									Row existRow = (Row) object;
								//	Label tempCodeLbl = (Label) existRow.getChildren().get(2);
									Label temtypeLbl = (Label) existRow.getChildren().get(1);
									String temptypeLbl=(String) temtypeLbl.getAttribute("Type");
									/*if(temtypeLbl.getValue().contains("AND")){
										MessageUtil.setMessage("Multiple single rules cannot be added as you have already added a ONE combination rule.",
												"color:blue", "TOP");
										return;
									}*/
									for (SKUTemp sku : listSku) {
										if(sku.getDiscount()!=null)
										{
										if (map1.containsKey(sku.getSkuAttribute() + "::" + sku.getDiscount())) {
											value = (map1.get(sku.getSkuAttribute() + "::" + sku.getDiscount()));
											if (value != null && value.length() <= 100)
												map1.put(sku.getSkuAttribute() + "::" + sku.getDiscount(),
														value.concat(", ") + sku.getSkuValue());
											skutempmap.put(sku.getSkuAttribute() + "::" + sku.getDiscount(),sku.getSkuTempId());
										} else {
											map1.put(sku.getSkuAttribute() + "::" + sku.getDiscount(), sku.getSkuValue());
											skutempmap.put(sku.getSkuAttribute() + "::" + sku.getDiscount(),sku.getSkuTempId());
										}
									}
									for (Entry<String, String> entry : map1.entrySet()) {
										if(map1.get(entry.getKey())!=null ) {
										String typeLbl=	entry.getKey().split("::")[0].trim();
										if(map1.get(entry.getKey()).contains(",")){
											  String[] leftSideCodes=map1.get(entry.getKey()).split(",");
												for (String leftSideCode : leftSideCodes) {
													if(temtypeLbl.getValue().split("=")[1].contains(",")){
														String[] codeLblArray=temtypeLbl.getValue().split("=")[1].split(",");
														
														for(String codeLbl : codeLblArray){
															if(codeLbl.trim().equalsIgnoreCase(leftSideCode.replaceAll(" ","")) && temptypeLbl.equals(typeLbl)){
																MessageUtil.setMessage(
																		"You tried giving 2 different discounts for the same criteria. \n "
																				+ "Please reconsider or delete the old rule.",
																		"color:red", "TOP");
																return false;
															}
														}
												}else if(temtypeLbl.getValue().split("=")[1].trim().equalsIgnoreCase(leftSideCode.replaceAll(" ","")) && temptypeLbl.equals(typeLbl)){
													MessageUtil.setMessage(
															"You tried giving 2 different discounts for the same criteria. \n "
																	+ "Please reconsider or delete the old rule.",
															"color:red", "TOP");
													return false;
												}
										  }
											}else if(temtypeLbl.getValue().split("=")[1].contains(",")){

												String[] codeLblArray=temtypeLbl.getValue().split("=")[1].split(",");
												
												for(String codeLbl : codeLblArray){
													if(codeLbl.trim().equalsIgnoreCase(map1.get(entry.getKey())) && temptypeLbl.equals(typeLbl)){
														MessageUtil.setMessage(
																"You tried giving 2 different discounts for the same criteria. \n "
																		+ "Please reconsider or delete the old rule.",
																"color:red", "TOP");
														return false;
													}
												}
										
											}
										else{
												
												
												if(temtypeLbl.getValue().split("=")[1].trim().equals(map1.get(entry.getKey())) && temptypeLbl.equals(typeLbl)){
													MessageUtil.setMessage(
															"You tried giving 2 different discounts for the same criteria. \n "
																	+ "Please reconsider or delete the old rule.",
															"color:red", "TOP");
													return false;
												}/*else if(listSku.size() == 0){
													MessageUtil.setMessage("All codes are selected for another promotion.", "color:red", "TOP");
													return;
												}*/
										}
									}
								}
							}
						}
							}
							}
							if (listSku.size() >= 500) {
								coupDiscGenDaoForDML.saveByCollection(listSku);
								Comparator<SKUTemp> comparator = Comparator.comparing(SKUTemp::getSkuTempId);
								SKUTemp minObject = listSku.stream().min(comparator).get();
								if (startId == null)
									startId = minObject.getSkuTempId();
								endId = listSku.get(listSku.size() - 1).getSkuTempId();
								Collections.reverse(listSku);
								for (SKUTemp sku : listSku) {
									if (map.containsKey(sku.getSkuAttribute() + "::" + sku.getDiscount())) {
										value = (map.get(sku.getSkuAttribute() + "::" + sku.getDiscount()));
										if (value != null)
											map.put(sku.getSkuAttribute() + "::" + sku.getDiscount(),
													value.concat(", ") + sku.getSkuValue());
										skutempmap.put(sku.getSkuAttribute() + "::" + sku.getDiscount(),sku.getSkuTempId());
									} else {
										map.put(sku.getSkuAttribute() + "::" + sku.getDiscount(), sku.getSkuValue());
										skutempmap.put(sku.getSkuAttribute() + "::" + sku.getDiscount(),sku.getSkuTempId());
									}
								}
								dupflag = false;
								listSku = new ArrayList<>();
							}
							// item.setDisabled(true);
						}

						if (listSku.size() > 0 && listSku.size() < 500) {
							
							logger.info("size of the list========"+listSku.size());
							coupDiscGenDaoForDML.saveByCollection(listSku);
							Comparator<SKUTemp> comparator = Comparator.comparing(SKUTemp::getSkuTempId);
							SKUTemp minObject = listSku.stream().min(comparator).get();
							if (startId == null)
								// startId=skuList.get(0).getSkuTempId();
								startId = minObject.getSkuTempId();
							endId = listSku.get(listSku.size() - 1).getSkuTempId();
							String value1 = null;
							Collections.reverse(listSku);
							for (SKUTemp sku : listSku) {
								if (map.containsKey(sku.getSkuAttribute() + "::" + sku.getDiscount())) {
									value1 = (map.get(sku.getSkuAttribute() + "::" + sku.getDiscount()));
									if (value1 != null)
										map.put(sku.getSkuAttribute() + "::" + sku.getDiscount(),
												value1.concat(", ") + sku.getSkuValue());
										skutempmap.put(sku.getSkuAttribute() + "::" + sku.getDiscount(),sku.getSkuTempId());
								} else {
									map.put(sku.getSkuAttribute() + "::" + sku.getDiscount(), sku.getSkuValue());
									skutempmap.put(sku.getSkuAttribute() + "::" + sku.getDiscount(), sku.getSkuTempId());
								}
							}
						}
						/*if (dupflag && listSku.size() == 0) {
							MessageUtil.setMessage("All SKU/s are selected for another discount code.", "color:red", "TOP");
							return;
						}*/

						
						end = endId;
						if (start == null)
							start = startId;
						limit = start + "::" + end;
						for (Entry<String, String> entry : map.entrySet()) {
							boolean flag = false;
							String type = entry.getKey().split("::")[0];
					/*		type = type.contains("Vendor") ? "Vendor Code"
									: type.contains("Department") ? "Department Code"
											: type.contains("Subclass") ? "Sub-Class" : type; */
							
							if(type.contains("UDF")||type.contains("udf")){
								type = udfDisplaySKUMap.get(type.toUpperCase());
							}
							/*if(type.toLowerCase().startsWith("udf")){
								List<Listitem> selectedItems=itemAttributeLbId.getItems();
								for (Listitem listitem : selectedItems) {
									if(listitem.getValue().toString().equalsIgnoreCase(type)){
										type=listitem.getLabel();
									}
								}
							}*/
							
							Label tempRulelbl=null;
							//if(multipleSingleId.isChecked()){
							if(ruleType.equalsIgnoreCase(multipleSingle)) {
							if (productDiscountGenRowsId.getVisibleItemCount() > 0) {
								List<Component> rowList = productDiscountGenRowsId.getChildren();
								//editCombinedRuleObj!=null or editCombinedRuleObj==null doubtful
								if (rowList != null && rowList.size() > 0 && editCombinedRuleObj!=null) {
									for (Object object : rowList) {
										
										boolean maxDiscountExists =  false;
										//boolean quantityExists =(( higestPriceRadioId.isSelected() || higestPricewithoutRadioId.isSelected())&& quantityDblBxId.getValue() != null);
										boolean quantityExists =false;

										if(flag){
											continue;
										}
										
										Row existRow = (Row) object;
										
										//if(oneCombinationId.isChecked()){
										if(ruleType.equalsIgnoreCase(combined)) {
											
											 tempRulelbl = (Label) existRow.getChildren().get(1);
										}

										
										
										Label tempDisLbl = (Label) existRow.getChildren().get(0);
										Label tempTypelbl = (Label) existRow.getChildren().get(1);
										Label tempMaxlbl = null;
										//Label tempquanlbl = null;
										/*if(column6Id.isVisible()){
											
											//tempquanlbl = (Label)existRow.getChildren().get(2);
										}*//*else{
											tempquanlbl.setValue("--");
										}*/
										//if(isNewPlugin) {
										boolean isCheckOnmaXDisc = maxDiscountExists ;
										boolean ischeckOnQuantinty = quantityExists;
										
										
										if (tempTypelbl.getValue().split("=")[0].trim().equals(type) && tempDisLbl.getValue().equals("" + discValue)) {
											if ((isCheckOnmaXDisc && ischeckOnQuantinty) || ((isCheckOnmaXDisc && !quantityExists ) || (ischeckOnQuantinty && !maxDiscountExists))){
											row = existRow;
											String str = row.getValue();
											startId = Long.parseLong(str.split("::")[0]);
											row.setValue(startId + "::" + endId);
											Label codelbl = (Label) existRow.getChildren().get(1);
											String code = codelbl.getValue().concat("," + map.get(entry.getKey()));
											codelbl.setValue(code);
											flag = true;
											/*List<Long> templist = coupDiscGenDao.findBySkuUserCount(users.getUserId(),
													discountDblBxId.getValue(), entry.getKey().split("::")[0],
													maxDiscountDblBxId != null ? maxDiscountDblBxId.getValue() + "" : null,
															quantityDblBxId != null ?quantityDblBxId.getValue() + "" : null,
													tempCouponObj != null ? tempCouponObj.getCouponId() + "" : null);

											noOfitem = templist.get(0);
											Label noitemLabel = (Label) existRow.getChildren().get(3);
											noitemLabel.setValue(noOfitem.toString());*/
											
											row = existRow;
											String strMdiscount = row.getValue();
											startId = Long.parseLong(strMdiscount.split("::")[0]);
											row.setValue(startId + "::" + endId);
											Label mDiscountlbl = (Label) existRow.getChildren().get(2);
											//String mDiscount = maxDiscountDblBxId.getValue() != null ? maxDiscountDblBxId.getValue().toString() :"--";
											//String mDiscount = mDiscountlbl.getValue().concat("," +mDiscountlbl.getValue());
											mDiscountlbl.setValue(discValue);
											
											
											row = existRow;
											String strQuantity = row.getValue();
											startId = Long.parseLong(strQuantity.split("::")[0]);
											row.setValue(startId + "::" + endId);
											Label quantitylbl = (Label) existRow.getChildren().get(2);
											String quantity = itemQuantity != null && !itemQuantity.isEmpty() && !itemQuantity.equalsIgnoreCase("null")? String.valueOf(getActualNumber(Double.parseDouble(itemQuantity))) :"--";
											//String quantity = quantitylbl.getValue().concat("," + quantitylbl.getValue());
											quantitylbl.setValue(quantity);
											
										}
									
									
										else
												
											/*
											 * if (( ((maxDiscountExists && !tempMaxlbl.getValue().isEmpty() &&
											 * !tempMaxlbl.equals("--") &&
											 * tempMaxlbl.getValue().equals(maxDiscountDblBxId.getValue().toString()))&&!
											 * quantityExists) || ((quantityExists && !tempquanlbl.getValue().isEmpty() &&
											 * !tempquanlbl.equals("--") &&
											 * tempquanlbl.getValue().equals(quantityDblBxId.getValue().toString()))
											 * ))&&!maxDiscountExists) {
											 */
												 
											if ((
													((maxDiscountExists)&&
															(!quantityExists))
														//(!quantityExists))

													)) {
												
												
												 existRow = (Row) object;
												 tempDisLbl = (Label) existRow.getChildren().get(0);
												 tempTypelbl = (Label) existRow.getChildren().get(1);
												 tempMaxlbl = (Label)existRow.getChildren().get(2);
												
													//if(isNewPlugin) {
													if (tempDisLbl.getValue().equals("" + discValue)&&
															tempTypelbl.getValue().split("=")[0].trim().equals(type)) {
														row = existRow;
														String str = row.getValue();
														startId = Long.parseLong(str.split("::")[0]);
														row.setValue(startId + "::" + endId);
														Label codelbl = (Label) existRow.getChildren().get(1);
														String code = codelbl.getValue().concat("," + map.get(entry.getKey()));
														codelbl.setValue(code);
														flag = true;
														List<Long> templist = coupDiscGenDao.findBySkuUserCount(users.getUserId(),
																discountDblBxId.getValue(), entry.getKey().split("::")[0],
																maxDiscountDblBxId != null ? maxDiscountDblBxId.getValue() + "" : null,
																		itemQuantity != null ?itemQuantity + "" : null,
																tempCouponObj != null ? tempCouponObj.getCouponId() + "" : null);

														noOfitem = templist.get(0);
														Label noitemLabel = (Label) existRow.getChildren().get(2);
														noitemLabel.setValue(noOfitem.toString());
														
														row = existRow;
														String strMdiscount = row.getValue();
														startId = Long.parseLong(strMdiscount.split("::")[0]);
														row.setValue(startId + "::" + endId);
														Label mDiscountlbl = (Label) existRow.getChildren().get(2);
														//String mDiscount = maxDiscountDblBxId .getValue()!= null ? maxDiscountDblBxId.getValue().toString() :"--";
														//String mDiscount = mDiscountlbl.getValue().concat("," +mDiscountlbl.getValue());
														mDiscountlbl.setValue(discValue);
														
													}
											
											}else 
												if ((!maxDiscountExists )&&
														((quantityExists))){
													 existRow = (Row) object;
													 tempDisLbl = (Label) existRow.getChildren().get(0);
													 tempTypelbl = (Label) existRow.getChildren().get(1);
													 //tempMaxlbl = (Label)existRow.getChildren().get(4);
													 //tempquanlbl = (Label)existRow.getChildren().get(3);
													 //Check once
														/*if(quantityExists){
															
															tempquanlbl = (Label)existRow.getChildren().get(2);
														}*/
													
														//if(isNewPlugin) {
												
														if (tempDisLbl.getValue().toString().equals(discValue)&&
																tempTypelbl.getValue().toString().split("=")[0].trim().equals(type.toString())) {
															row = existRow;
															String str = row.getValue();
															startId = Long.parseLong(str.split("::")[0]);
															row.setValue(startId + "::" + endId);
															Label codelbl = (Label) existRow.getChildren().get(1);
															String code = codelbl.getValue().concat("," + map.get(entry.getKey()));
															codelbl.setValue(code);
															flag = true;
															/*List<Long> templist = coupDiscGenDao.findBySkuUserCount(users.getUserId(),
																	discountDblBxId.getValue(), entry.getKey().split("::")[0],
																	maxDiscountDblBxId != null ? maxDiscountDblBxId.getValue() + "" : null,
																			quantityDblBxId != null ?quantityDblBxId.getValue() + "" : null,
																	tempCouponObj != null ? tempCouponObj.getCouponId() + "" : null);

															noOfitem = templist.get(0);
															Label noitemLabel = (Label) existRow.getChildren().get(3);
															noitemLabel.setValue(noOfitem.toString());*/
															
															row = existRow;
															String strQuantity = row.getValue();
															startId = Long.parseLong(strQuantity.split("::")[0]);
															row.setValue(startId + "::" + endId);
															Label quantitylbl = (Label) existRow.getChildren().get(2);
															String quantity = itemQuantity != null && !itemQuantity.isEmpty() && !itemQuantity.equalsIgnoreCase("null")? String.valueOf(getActualNumber(Double.parseDouble(itemQuantity))) :"--";
															//String quantity = quantitylbl.getValue().concat("," + quantitylbl.getValue());
															quantitylbl.setValue(quantity);	
												}
												}
											else if ((
													((maxDiscountExists && !tempMaxlbl.getValue().isEmpty() && !tempMaxlbl.equals("--") && 
															tempMaxlbl.getValue().equals(maxDiscountDblBxId.getValue().toString()))
															&&(quantityExists)) )
													&&(maxDiscountExists && !tempMaxlbl.getValue().isEmpty() && !tempMaxlbl.equals("--") && 
															tempMaxlbl.getValue().equals(maxDiscountDblBxId.getValue().toString()))) {
												
												
												 existRow = (Row) object;
												 tempDisLbl = (Label) existRow.getChildren().get(0);
												 tempTypelbl = (Label) existRow.getChildren().get(1);
												// tempMaxlbl = (Label)existRow.getChildren().get(2);
												 //tempquanlbl = (Label)existRow.getChildren().get(2);
													/*if(column6Id.isVisible()){
														
														tempquanlbl = (Label)existRow.getChildren().get(2);
													}*/
												
													//if(isNewPlugin) {
													if (tempDisLbl.getValue().equals("" + discValue)
													//&&tempMaxlbl.getValue().equals("" + maxDiscountDblBxId.getValue())
													&& quantityExists &&
													tempTypelbl.getValue().split("=")[0].trim().equals(type)) {
														row = existRow;
														String str = row.getValue();
														startId = Long.parseLong(str.split("::")[0]);
														row.setValue(startId + "::" + endId);
														Label codelbl = (Label) existRow.getChildren().get(1);
														String code = codelbl.getValue().concat("," + map.get(entry.getKey()));
														codelbl.setValue(code);
														flag = true;
														/*List<Long> templist = coupDiscGenDao.findBySkuUserCount(users.getUserId(),
																discountDblBxId.getValue(), entry.getKey().split("::")[0],
																maxDiscountDblBxId != null ? maxDiscountDblBxId.getValue() + "" : null,
																		quantityDblBxId != null ?quantityDblBxId.getValue() + "" : null,
																tempCouponObj != null ? tempCouponObj.getCouponId() + "" : null);

														noOfitem = templist.get(0);
														Label noitemLabel = (Label) existRow.getChildren().get(3);
														noitemLabel.setValue(noOfitem.toString());*/
														
														row = existRow;
														String strMdiscount = row.getValue();
														startId = Long.parseLong(strMdiscount.split("::")[0]);
														row.setValue(startId + "::" + endId);
														Label mDiscountlbl = (Label) existRow.getChildren().get(2);
														//String mDiscount = maxDiscountDblBxId.getValue() != null ? maxDiscountDblBxId.getValue().toString() :"--";
														//String mDiscount = mDiscountlbl.getValue().concat("," +mDiscountlbl.getValue());
														mDiscountlbl.setValue(discValue);
														
														
														row = existRow;
														String strQuantity = row.getValue();
														startId = Long.parseLong(strQuantity.split("::")[0]);
														row.setValue(startId + "::" + endId);
														Label quantitylbl = (Label) existRow.getChildren().get(2);
														String quantity = itemQuantity!= null && !itemQuantity.isEmpty() && !itemQuantity.equalsIgnoreCase("null")? String.valueOf(getActualNumber(Double.parseDouble(itemQuantity))) :"--";
														//String quantity = quantitylbl.getValue().concat("," + quantitylbl.getValue());
														quantitylbl.setValue(quantity);											
													}
											}
											
										
										
											else if ((!maxDiscountExists )
													&& (!quantityExists)){
											//	&& (!quantityExists)){

											
											row = existRow;
											String str = row.getValue();
											startId = Long.parseLong(str.split("::")[0]);
											row.setValue(startId + "::" + endId);
											Label codelbl = (Label) existRow.getChildren().get(1);
											String code = codelbl.getValue().concat("," + map.get(entry.getKey()));
											codelbl.setValue(code);
											flag = true;
											/*List<Long> templist = coupDiscGenDao.findBySkuUserCount(users.getUserId(),
													discountDblBxId.getValue(), entry.getKey().split("::")[0],
													maxDiscountDblBxId != null ? maxDiscountDblBxId.getValue() + "" : null,
															quantityDblBxId != null ?quantityDblBxId.getValue() + "" : null,
													tempCouponObj != null ? tempCouponObj.getCouponId() + "" : null);

											noOfitem = templist.get(0);
											Label noitemLabel = (Label) existRow.getChildren().get(3);
											noitemLabel.setValue(noOfitem.toString());*/
											
											row = existRow;
											String strMdiscount = row.getValue();
											startId = Long.parseLong(strMdiscount.split("::")[0]);
											row.setValue(startId + "::" + endId);
											Label mDiscountlbl = (Label) existRow.getChildren().get(2);
											//String mDiscount = maxDiscountDblBxId.getValue() != null ? maxDiscountDblBxId.getValue().toString() :"--";
											//String mDiscount = mDiscountlbl.getValue().concat("," +mDiscountlbl.getValue());
											mDiscountlbl.setValue(discValue);
											
											
											row = existRow;
											String strQuantity = row.getValue();
											startId = Long.parseLong(strQuantity.split("::")[0]);
											row.setValue(startId + "::" + endId);
											Label quantitylbl = (Label) existRow.getChildren().get(2);
											String quantity = itemQuantity != null && !itemQuantity.isEmpty() && !itemQuantity.equalsIgnoreCase("null")? String.valueOf(getActualNumber(Double.parseDouble(itemQuantity))) :"--";
											//String quantity = quantitylbl.getValue().concat("," + quantitylbl.getValue());
											quantitylbl.setValue(quantity);
										
										}else 
										{
											flag=false;
											
										}
																
									}
							}
								}}
							}
							//if (!flag) {
								
								row = new Row();
								row.setValue(startId + "::" + endId);
								String discount=discValue;
								Div tempDiv=new Div();
								/*if (discTypeForItem.equalsIgnoreCase(DISC_TYPE_PERCENTAGE))
									disccount+= "%";
								else {
									disccount+= userCurrencySymbol;
								}*/
								if(!discount.isEmpty()) {
								label = new Label(String.valueOf(Double.valueOf(Utility.truncateUptoTwoDecimal(discount))));
								label.setParent(row);}
								else {
									label = new Label("");
									label.setParent(row);	
								}
								
								/*label = new Label(discTypeForItem.equalsIgnoreCase(DISC_TYPE_PERCENTAGE)?"%":userCurrencySymbol);
								label.setParent(row);*/
								
								/*String customFieldValue=null;
								Set<Listitem> selectedItems=itemAttributeLbId.getSelectedItems();
								for (Listitem listitem : selectedItems) {
									customFieldValue=listitem.getValue();
								}*/

								/*label = new Label(type);
								label.setAttribute("Type", entry.getKey().split("::")[0]);
								label.setParent(row);

								label = new Label(map.get(entry.getKey()));
								label.setParent(row);
								
								List<Long> templist=null;
								templist = coupDiscGenDao.findBySkuUserCount(users.getUserId(),
										discountDblBxId.getValue(), entry.getKey().split("::")[0],
										maxDiscountDblBxId != null ? maxDiscountDblBxId.getValue() + "" : null,
												quantityDblBxId != null ? quantityDblBxId.getValue() + "" : null,
														tempCouponObj != null ? tempCouponObj.getCouponId() + "" : null);							
								
								noOfitem = templist.get(0);
								label = new Label(noOfitem.toString());
								label.setParent(row);
								
								
		*/
								//if(oneCombinationId.isChecked()){
								if(ruleType.equalsIgnoreCase(combined)) {
									label = new Label();
									label.setAttribute("Type", entry.getKey().split("::")[0]);
									label.setParent(row);

									
								}else{
									logger.info("values displayed==========="+map.get(entry.getKey()));
									if(map.get(entry.getKey())==null) {
										label = new Label(ruleToDisplay);
									//	label.setAttribute(ruleToDisplay));
										label.setParent(row);
									}
									else {
										
										
										POSMapping posMapping = posMappingDao.findRecordBycustomFieldName(type, "SKU", users.getUserId());
										if(posMapping!=null) {
									label = new Label(posMapping.getDisplayLabel()+" = "+map.get(entry.getKey())+(ruleToDisplay.length()>0?" AND "+ruleToDisplay:""));
										}
										else
										{
											if(map.get(entry.getKey())==null) {
												label = new Label(ruleToDisplay);
											//	label.setAttribute(ruleToDisplay));
											
											}
											else {
												if(!type.equalsIgnoreCase("null")) {
											label = new Label(type+" = "+map.get(entry.getKey())+(ruleToDisplay.length()>0?" AND "+ruleToDisplay:""));
												}
												else {
													label = new Label(ruleToDisplay);
												}
												}
										}
									label.setAttribute("Type", entry.getKey().split("::")[0]+(ruleToDisplay.length()>0?" AND "+ruleToDisplay:""));
									label.setParent(row);
								}
								}

								
									
											if(itemQuantity != null && !itemQuantity.isEmpty() && !itemQuantity.equalsIgnoreCase("null")){
												label = new Label(String.valueOf(getActualNumber(Double.parseDouble(itemQuantity)))+"-"+itemQtyType);
												label.setValue(String.valueOf(getActualNumber(Double.parseDouble(itemQuantity))));
												label.setParent(row);
											}else{
												label = new Label("--");
												label.setParent(row);
											}
											if(!DisC3.isEmpty() && DisC5.isEmpty() ) {
												String val=loyaltyProgramTierDao.getTierName(DisC3);
												 label = new Label(val);
											    	label.setParent(row);
											    	 label = new Label("--");
														label.setParent(row);
										}
											if(!DisC5.isEmpty() && DisC3.isEmpty()) {
												String val=loyaltyCardSetDao.getCardName(DisC5);
													 label = new Label("--");
											    	label.setParent(row);
											    	 label = new Label(val);
														label.setParent(row);
											}
											if(!DisC5.isEmpty()  && !DisC3.isEmpty() ) {
												String val=loyaltyProgramTierDao.getTierName(DisC3);
												String val1=loyaltyCardSetDao.getCardName(DisC5);
													 label = new Label(val);
											    	label.setParent(row);
											    	 label = new Label(val1);
														label.setParent(row);
											}
											if(DisC5.isEmpty()  && DisC3.isEmpty()) {
													 label = new Label("--");
											    	label.setParent(row);
											    	 label = new Label("--");
											    	 label.setParent(row);
											}
										
											
								//Hbox hbox = new Hbox();
								
								//APP-2720
								Image editImg = new Image("/img/email_edit.gif");
								editImg.setStyle("cursor:pointer;margin-right:10px;");
								editImg.setAttribute("COUPON_TYPE", COUPON_RULE_EDIT);
								editImg.setAttribute("COUPON_OBJ",tempCouponObj);
								editImg.addEventListener("onClick", this);
								editImg.setParent(tempDiv);
									
									/*label = new Label(eligibleItems);
									label.setParent(row);*/
									

								Image delImg = new Image("img/delt_icn.png");
								delImg.setStyle("cursor:pointer;");
								delImg.setAttribute("COUPON_TYPE", COUPON_RULE_DELETE);
								delImg.addEventListener("onClick", this);
								delImg.setParent(tempDiv);
								
							//	hbox.setParent(row);
								tempDiv.setParent(row);
								row.setParent(productDiscountGenRowsId);
								row.setAttribute("skutemp", skutempmap.get(entry.getKey()));
								
							//}
								//delete the edit mode skus from table & grid
								if(editCombinedRuleObj!=null) {
								for(SKUTemp editsku:editCombinedRuleObj) {
									coupDiscGenDaoForDML.deleteAllSkuBy(users.getUserId(),String.valueOf(editsku.getSkuTempId()+"::"+String.valueOf(editsku.getSkuTempId())));
								}
									if (productDiscountGenRowsId.getVisibleItemCount() > 0) {
										List<Component> rowList = productDiscountGenRowsId.getChildren();
										List<Component> tempRowList = new ArrayList<Component>();
										tempRowList.addAll(productDiscountGenRowsId.getChildren());
										List<Long> tempList = new ArrayList<Long>();
										for(SKUTemp editsku:editCombinedRuleObj) {
											tempList.add(editsku.getSkuTempId());
										}
									if (rowList != null && rowList.size() > 0) {
									for (Object object : tempRowList) {
											Row existRow = (Row) object;
											if(tempList.contains((long)existRow.getAttribute("skutemp"))) {
												productDiscountGenRowsId.removeChild(existRow);
											}
									}
									}
									}
								}
							//delete the edit mode skus from table & grid
							/*if(editCombinedRuleObj!=null) {
							for(SKUTemp editsku:editCombinedRuleObj) {
								coupDiscGenDaoForDML.deleteAllSkuBy(users.getUserId(),String.valueOf(editsku.getSkuTempId()+"::"+String.valueOf(editsku.getSkuTempId())));
							}
								if (productDiscountGenRowsId.getVisibleItemCount() > 0) {
									List<Component> rowList = productDiscountGenRowsId.getChildren();
									List<Long> tempList = new ArrayList<Long>();
									for(SKUTemp editsku:editCombinedRuleObj) {
										tempList.add(editsku.getSkuTempId());
									}
								if (rowList != null && rowList.size() > 0) {
									List<Component> tempRowList = rowList;
								for (Object object : tempRowList) {
										Row existRow = (Row) object;
										if(tempList.contains((long)existRow.getAttribute("skutemp"))) {
											productDiscountGenRowsId.removeChild(existRow);
										}
								}
								}
								}
							}*/
						}
						allItems.removeAll(selctedItemSet);
						logger.info(" Remain item list size " + allItems);
						Iterator<Listitem> it = allItems.iterator();
						while (it.hasNext()) {
							it.next().setParent(addedSKUSegmentLBId);
						}
					}
					}
			
			
			
	//else if(oneCombinationId.isChecked()){
					else if(ruleType.equalsIgnoreCase(combined) ) {
			List<Listitem> allItems = addedSKUSegmentLBId.getItems();
			Set<Listitem> selctedItemSet = addedSKUSegmentLBId.getSelectedItems();
			if (selctedItemSet == null || selctedItemSet.size() == 0) {
				MessageUtil.setMessage("Please select at least one attribute code for creating discount rule.",
						"color:red", "TOP");
				return false;
			}
			/*if (productDiscountGenRowsId.getVisibleItemCount() > 0) {
				
				MessageUtil.setMessage("Only one AND rule is allowed.",
						"color:blue", "TOP");
				return;
			}*/
			Row row = null;
			Label label = null;
			List<SKUTemp> listSku = null;
			Long startId = null;
			Long endId = null;
			boolean dupflag = true;
			Map<String, String> map = new LinkedHashMap<>();
			Map<String, String> map1 = new LinkedHashMap<>();
			//Map<String, Long> skutempmap = new LinkedHashMap<>();
			String skuTempId="";
			List<SKUTemp> oldlistSku = coupDiscGenDao.findTempSkuBy(users.getUserId(), null, limit);
			if (selctedItemSet != null && selctedItemSet.size() > 0) {
				listSku = new LinkedList<>();
				Iterator<Listitem> itr = selctedItemSet.iterator();
				allItems = addedSKUSegmentLBId.getItems();
				logger.info(" Total item size :" + allItems.size());
				while (itr.hasNext()) {
					Listitem item = itr.next();
					List<Component> cells = item.getChildren();
					SKUTemp skuTemp = null;
					String attribute = null;
					String value = null;
					String type = null;
					for (Component cmp : cells) {
						Listcell cell = (Listcell) cmp;
						String[] values=null;
						
						//if(!cell.getLabel().contains("AND")) continue;
						if (cell.getValue() != null && cell.getValue().toString().startsWith("key")) {
							String valueOfKey=cell.getValue().toString().split("::")[1];
							logger.info("cell.getAttribute(value)"+cell.getAttribute("value"));
							if(valueOfKey.contains(",,,")){
								
								values=valueOfKey.split(",,,");
								
							for(String eachStr : values){
								
								//for each skuattr - key attr-value eachStr
								
								
								attribute = eachStr.split("=")[0].trim();
								value= eachStr.split("=")[1].trim();
							
								/*Set<Listitem> selectedItems = itemAttributeLbId.getSelectedItems();
								for (Listitem li : selectedItems) {
									if (li.getLabel().equalsIgnoreCase(attribute)) {
										type = li.getValue().toString();
									}
									
								}
								*/
								boolean flag = false;
								boolean exists=false;
								SKUTemp tempSku=null;
								if (oldlistSku != null && oldlistSku.size() > 0) {
									for (SKUTemp oldSku : oldlistSku) {
										logger.info(oldSku.getSkuAttribute()+ "=="+oldSku.getSkuValue());
										if(editCombinedRuleObj!=null) {
											for(SKUTemp editSku:editCombinedRuleObj) {
												if(editSku.getSkuTempId()==oldSku.getSkuTempId()) {
													exists=true;
													if(editSku.getSkuAttribute().equalsIgnoreCase(attribute))tempSku=editSku;
												}
											}
											}
										if(exists) continue;
										if (oldSku.getSkuAttribute().equals(attribute) && oldSku.getSkuValue().equals(value)
												&& oldSku.getDiscount()
												.equals(Double.parseDouble(discValue)))
											flag = true;
									}
								}
								if (flag)
									continue;
								
								
								skuTemp = new SKUTemp();
								/*if(tempSku!=null) {
									skuTemp=tempSku;
								}else {
									skuTemp = new SKUTemp();
								}*/
								skuTemp.setUserId(users.getUserId());
								skuTemp.setDiscount(Double.valueOf(Utility.truncateUptoTwoDecimal(discValue)));
								skuTemp.setDiscountType(discTypeForItem);
								skuTemp.setSkuAttribute(udfRuleSKUMap.get(attribute)!=null?udfRuleSKUMap.get(attribute):attribute);
								skuTemp.setSkuValue(value);
								skuTemp.setOwnerId(orgOwnerUserId);
								skuTemp.setNoOfEligibleItems(eligibleItems);
								if(minPurchaseAmt!=null&&!minPurchaseAmt.isEmpty())skuTemp.setTotPurchaseAmount(Long.parseLong(minPurchaseAmt));
								if(itemPrice!=null && !itemPrice.isEmpty())skuTemp.setItemPrice(Double.valueOf(itemPrice));
								skuTemp.setItemPriceCriteria(itemPriceCriteria);
								//if (isNewPlugin) {
								if (skuRadioId.isChecked()) {
									if(accumulateCoupChkBxId.isChecked()) {
										skuTemp.setMaxDiscount(maxDiscountDblBxId.getValue());
										// skuTemp.setQuantity(quantityDblBxId.getValue());
										//if (higestPriceRadioId.isSelected() || higestPricewithoutRadioId.isSelected()) {
											if(itemQuantity!=null && !itemQuantity.isEmpty() && !itemQuantity.equalsIgnoreCase("null"))skuTemp.setQuantity(String.valueOf(getActualNumber(Double.parseDouble(itemQuantity))));
											skuTemp.setLimitQuantity(itemQtyType);
										//}
									}else {
										//if (higestPriceRadioId.isSelected() || higestPricewithoutRadioId.isSelected()) {
											if(itemQuantity!=null && !itemQuantity.isEmpty() && !itemQuantity.equalsIgnoreCase("null"))skuTemp.setQuantity(String.valueOf(getActualNumber(Double.parseDouble(itemQuantity))));
											skuTemp.setLimitQuantity(itemQtyType);

										//}
									}
									
								}
								
								if (tempCouponObj != null)
									skuTemp.setCouponId(tempCouponObj.getCouponId());
								listSku.add(skuTemp);
							}
								
						}else{

							
							attribute = valueOfKey.split("=")[0].trim();
							value= valueOfKey.split("=")[1].trim();
						
							/*Set<Listitem> selectedItems = itemAttributeLbId.getSelectedItems();
							for (Listitem li : selectedItems) {
								if (li.getLabel().equalsIgnoreCase(attribute)) {
									type = li.getValue().toString();
								}
								
							}
							*/
							boolean flag = false;
							boolean exists=false;
							SKUTemp tempSku=null;
							if (oldlistSku != null && oldlistSku.size() > 0) {
								for (SKUTemp oldSku : oldlistSku) {
									logger.info(oldSku.getSkuAttribute()+ "=="+oldSku.getSkuValue());
									if(editCombinedRuleObj!=null) {
										for(SKUTemp editSku:editCombinedRuleObj) {
											if(editSku.getSkuTempId()==oldSku.getSkuTempId()) {
												exists=true;
												if(editSku.getSkuAttribute().equalsIgnoreCase(attribute))tempSku=editSku;
											}
										}
										}
									if(exists) continue;
									if (oldSku.getSkuAttribute().equals(attribute) && oldSku.getSkuValue().equals(value)
											&& oldSku.getDiscount()
											.equals(Double.parseDouble(discValue)))
										flag = true;
								}
							}
							if (flag)
								continue;
							
							
							skuTemp = new SKUTemp();
							/*if(tempSku!=null) {
								skuTemp=tempSku;
							}else {
								skuTemp = new SKUTemp();
							}*/
							
							skuTemp.setUserId(users.getUserId());
							skuTemp.setDiscount(Double.valueOf(Utility.truncateUptoTwoDecimal(discValue)));
							skuTemp.setDiscountType(discTypeForItem);
							skuTemp.setSkuAttribute(udfRuleSKUMap.get(attribute)!=null?udfRuleSKUMap.get(attribute):attribute);
							skuTemp.setSkuValue(value);
							skuTemp.setOwnerId(orgOwnerUserId);
							skuTemp.setNoOfEligibleItems(eligibleItems);
							if(minPurchaseAmt!=null&&!minPurchaseAmt.isEmpty())skuTemp.setTotPurchaseAmount(Long.parseLong(minPurchaseAmt));
							if(itemPrice!=null  && !itemPrice.isEmpty())skuTemp.setItemPrice(Double.valueOf(itemPrice));
							skuTemp.setItemPriceCriteria(itemPriceCriteria);
							//if (isNewPlugin) {
							if (skuRadioId.isChecked()) {
								if(accumulateCoupChkBxId.isChecked()) {
									skuTemp.setMaxDiscount(maxDiscountDblBxId.getValue());
									// skuTemp.setQuantity(quantityDblBxId.getValue());
								//	if (higestPriceRadioId.isSelected() || higestPricewithoutRadioId.isSelected()) {
										if(itemQuantity!=null && !itemQuantity.isEmpty() && !itemQuantity.equalsIgnoreCase("null"))skuTemp.setQuantity(String.valueOf(getActualNumber(Double.parseDouble(itemQuantity))));
										skuTemp.setLimitQuantity(itemQtyType);

								//	}
								}else {
									//if (higestPriceRadioId.isSelected() || higestPricewithoutRadioId.isSelected()) {
										if(itemQuantity!=null && !itemQuantity.isEmpty() && !itemQuantity.equalsIgnoreCase("null"))skuTemp.setQuantity(String.valueOf(getActualNumber(Double.parseDouble(itemQuantity))));
										skuTemp.setLimitQuantity(itemQtyType);

									//}
								}
								
							}
							
							if (tempCouponObj != null)
								skuTemp.setCouponId(tempCouponObj.getCouponId());
							listSku.add(skuTemp);
						
						}
								
							



						}
					}
			
				
					if (listSku.size() >= 500) {
						coupDiscGenDaoForDML.saveByCollection(listSku);
						Comparator<SKUTemp> comparator = Comparator.comparing(SKUTemp::getSkuTempId);
						SKUTemp minObject = listSku.stream().min(comparator).get();
						if (startId == null)
							startId = minObject.getSkuTempId();
						endId = listSku.get(listSku.size() - 1).getSkuTempId();
						Collections.reverse(listSku);
						for (SKUTemp sku : listSku) {
							if (map.containsKey(sku.getSkuAttribute() + "::" + sku.getDiscount())) {
								value = (map.get(sku.getSkuAttribute() + "::" + sku.getDiscount()));
								if (value != null)
									map.put(sku.getSkuAttribute() + "::" + sku.getDiscount(),
											value.concat(", ") + sku.getSkuValue());
								map1.put(sku.getSkuAttribute() + "::" + sku.getDiscount(),String.valueOf(sku.getSkuTempId()));
							} else {
								map.put(sku.getSkuAttribute() + "::" + sku.getDiscount(), sku.getSkuValue());
								map1.put(sku.getSkuAttribute() + "::" + sku.getDiscount(),String.valueOf(sku.getSkuTempId()));
							}
						}
						dupflag = false;
						listSku = new ArrayList<>();
					}
					// item.setDisabled(true);
				}

				if (listSku.size() > 0 && listSku.size() < 500) {
					coupDiscGenDaoForDML.saveByCollection(listSku);
					Comparator<SKUTemp> comparator = Comparator.comparing(SKUTemp::getSkuTempId);
					SKUTemp minObject = listSku.stream().min(comparator).get();
					if (startId == null)
						// startId=skuList.get(0).getSkuTempId();
						startId = minObject.getSkuTempId();
					endId = listSku.get(listSku.size() - 1).getSkuTempId();
					String value = null;
					Collections.reverse(listSku);
					for (SKUTemp sku : listSku) {
						if (map.containsKey(sku.getSkuAttribute() + "::" + sku.getDiscount())) {
							value = (map.get(sku.getSkuAttribute() + "::" + sku.getDiscount()));
							if (value != null)
								map.put(sku.getSkuAttribute() + "::" + sku.getDiscount(),
										value.concat(", ") + sku.getSkuValue());
								map1.put(sku.getSkuAttribute() + "::" + sku.getDiscount(),String.valueOf(sku.getSkuTempId()));
						} else {
							map.put(sku.getSkuAttribute() + "::" + sku.getDiscount(), sku.getSkuValue());
							map1.put(sku.getSkuAttribute() + "::" + sku.getDiscount(), String.valueOf(sku.getSkuTempId()));
						}
					}
				}
				/*if (dupflag && listSku.size() == 0) {
					MessageUtil.setMessage("All SKU/s are selected for another promotion.", "color:red", "TOP");
					return;
				}*/
				end = endId;
				if (start == null)
					start = startId;
				limit = start + "::" + end;
				String ruleToInsert=null;
				String rule=null;
			//	String newRule="";
				String typeAttr=null;
				for (Entry<String, String> entry : map.entrySet()) {
					
					if (productDiscountGenRowsId.getVisibleItemCount() > 0) {
						
						/*MessageUtil.setMessage("Only one AND rule is allowed.",
								"color:blue", "TOP");
						return;*/
						
						/* List<Component> rowList = discountGenRowsId.getChildren();
						if (rowList != null && rowList.size() > 0) {
							for (Object object : rowList) {
								
								Row existRow = (Row) object;
								
								Label tempTypelbl = (Label) existRow.getChildren().get(1);
								
								if(tempTypelbl.getValue().contains("AND")){
									MessageUtil.setMessage("Only one AND rule is allowed .",
											"color:blue", "TOP");
									return;
								}else{

									MessageUtil.setMessage("ONE combination rule cannot be added as you have already added Multiple single rules.",
											"color:blue", "TOP");
									return;
								}
							}
						}*/
					}
					String type = entry.getKey().split("::")[0];
					type = type.contains("Vendor") ? "Vendor Code"
							: type.contains("Department") ? "Department Code"
									: type.contains("Subclass") ? "Sub-Class" : type;
					
					
					/*if(type.toLowerCase().startsWith("udf")){
						List<Listitem> selectedItems=itemAttributeLbId.getItems();
						for (Listitem listitem : selectedItems) {
							if(listitem.getValue().toString().equalsIgnoreCase(type)){
								type=listitem.getLabel();
							}
						}
					}*/
					if(type.contains("UDF")||type.contains("udf")){
						type = udfDisplaySKUMap.get(type.toUpperCase());
					}
				
					String value=map.get(entry.getKey());
					skuTempId = map1.get(entry.getKey());
				
					boolean replace=false;
					String newRule="";
					if(rule==null){
						rule = type+"="+value;
						if(ruleToInsert==null){
							ruleToInsert=type +"="+value;
						}else{
							ruleToInsert+= "," +type +"="+value;

						}
						
					}else{
						if(rule.contains(":::")){
							String[] ruleArr = rule.split(":::");
							
							for(String rulee : ruleArr){
								
							String	headerLabel =rulee.split("=")[0];
								
								if(headerLabel.trim().equals(type)){
									if(ruleToInsert==null){
										ruleToInsert=type +"="+value;					

									}else{
										ruleToInsert+= "," +type +"="+value;

									}
									logger.info(""+rulee+","+value);
									newRule+=" ::: "+rulee+","+value;
									replace=true;
									
									
								}else{
									
									if(!newRule.isEmpty()){
										if(ruleToInsert==null){
											ruleToInsert=type +"="+value;						

										}else{
											ruleToInsert+= "," +type +"="+value;

										}
										newRule+=" ::: " +rulee;
										
									}else{
										
										newRule=rulee;
										if(ruleToInsert==null){
											ruleToInsert=type +"="+value;										

										}else{
											ruleToInsert+= "," +type +"="+value;

										}
									}
								}
							}
								rule=newRule;	
							
							if(!replace){
								
								rule+= " ::: "+type +"="+value;
								
								if(ruleToInsert==null){
									ruleToInsert=type +"="+value;

								}else{
									ruleToInsert+= "," +type +"="+value;

								}
							}
							
							
						}else{
							if(rule.split("=")[0].equals(type)){
								
								rule=rule+","+value;
								if(ruleToInsert==null){
									ruleToInsert=type +"="+value;

								}else{
									ruleToInsert+= "," +type +"="+value;

								}
								
							}else{
								rule+= " ::: "+type +"="+value;
								if(ruleToInsert==null){
									ruleToInsert=type +"="+value;

								}else{
									ruleToInsert+= "," +type +"="+value;

								}						
								}
							
						}
						
					}
					
					
					if(typeAttr==null){
						typeAttr=entry.getKey().split("::")[0];
					}else{
						typeAttr+=","+entry.getKey().split("::")[0];
					}
				
						
				}
				
				rule=rule.replaceAll(":::", " AND ");
				
						row = new Row();
						row.setValue(startId + "::" + endId);
						String discount=discValue;
						Div tempDiv =new Div();
						/*if (discTypeForItem.equalsIgnoreCase(DISC_TYPE_PERCENTAGE))
							disccount+= "%";
						else {
							disccount+= userCurrencySymbol;
						}*/
						label = new Label(String.valueOf(Double.valueOf(Utility.truncateUptoTwoDecimal(discount))));
						label.setParent(row);

						/*label = new Label(discTypeForItem.equalsIgnoreCase(DISC_TYPE_PERCENTAGE)?"%":userCurrencySymbol);
						label.setParent(row);*/
						
							label = new Label(rule+(ruleToDisplay.length()>0?" AND "+ruleToDisplay:""));
							label.setAttribute("Type", typeAttr+(ruleToDisplay.length()>0?" AND "+ruleToDisplay:""));
							label.setParent(row);

							if(itemQuantity != null && !itemQuantity.isEmpty() && !itemQuantity.equalsIgnoreCase("null")){
								label = new Label(String.valueOf(getActualNumber(Double.parseDouble(itemQuantity)))+"-"+itemQtyType);
								label.setValue(String.valueOf(getActualNumber(Double.parseDouble(itemQuantity))));
								label.setParent(row);
							}else{
								label = new Label("--");
								label.setParent(row);
							}
							
							/*label = new Label(eligibleItems);
							label.setParent(row);*/
						
						//Hbox hbox = new Hbox();
						
						//APP-2720
						Image editImg = new Image("/img/email_edit.gif");
						editImg.setStyle("cursor:pointer;margin-right:10px;");
						editImg.setAttribute("COUPON_TYPE", COUPON_RULE_EDIT);
						editImg.addEventListener("onClick", this);
						editImg.setParent(tempDiv);

							
						Image delImg = new Image("img/delt_icn.png");
						delImg.setStyle("cursor:pointer;");
						delImg.setAttribute("COUPON_TYPE", COUPON_RULE_DELETE);
						delImg.addEventListener("onClick", this);
						delImg.setParent(tempDiv);
						
						//hbox.setParent(row);
						tempDiv.setParent(row);
						row.setParent(productDiscountGenRowsId);
						row.setAttribute("skutemp",Long.valueOf(skuTempId));
						
						//delete the edit mode skus from table & grid
						if(editCombinedRuleObj!=null) {
						for(SKUTemp editsku:editCombinedRuleObj) {
							coupDiscGenDaoForDML.deleteAllSkuBy(users.getUserId(),String.valueOf(editsku.getSkuTempId()+"::"+String.valueOf(editsku.getSkuTempId())));
						}
							if (productDiscountGenRowsId.getVisibleItemCount() > 0) {
								List<Component> rowList = productDiscountGenRowsId.getChildren();
								List<Long> tempList = new ArrayList<Long>();
								List<Component> tempRowList = new ArrayList<Component>();
								tempRowList.addAll(productDiscountGenRowsId.getChildren());
								for(SKUTemp editsku:editCombinedRuleObj) {
									tempList.add(editsku.getSkuTempId());
								}
							if (rowList != null && rowList.size() > 0) {
							for (Object object : tempRowList) {
									Row existRow = (Row) object;
									String ids[] = null;
									if(existRow.getAttribute("skutemp")!=null) {
										String id=String.valueOf(((long)existRow.getAttribute("skutemp")));
										ids = id.split("::");
									}
									for(String temp:ids) {
									if(tempList.contains(Long.valueOf(temp))) {
										productDiscountGenRowsId.removeChild(existRow);
									}
									}
							}
							}
							}
						}
						/*if(editCombinedRuleObj!=null) {
							if (productDiscountGenRowsId.getVisibleItemCount() > 0) {
								List<Component> rowList = productDiscountGenRowsId.getChildren();
								List<Long> tempList = new ArrayList<Long>();
								List<SKUTemp> tempSkulist=editCombinedRuleObj;
								for(SKUTemp editsku:tempSkulist) {
									tempList.add(editsku.getSkuTempId());
								}
							if (rowList != null && rowList.size() > 0) {
							for (Object object : rowList) {
									Row existRow = (Row) object;
									if(tempList.contains((long)existRow.getAttribute("skutemp"))) {
										productDiscountGenRowsId.removeChild(existRow);
									}
							}
							}
							}
						}*/
				
			}
		
				allItems.removeAll(selctedItemSet);
				logger.info(" Remain item list size " + allItems);
				Iterator<Listitem> it = allItems.iterator();
				while (it.hasNext()) {
					it.next().setParent(addedSKUSegmentLBId);
				}
		}
					int cnt = addedSKUSegmentLBId.getItemCount();
					for (; cnt > 0; cnt--) {
						addedSKUSegmentLBId.removeItemAt(cnt - 1);
					}
		}
		else {
			String discountRules = saveDiscountAttributes();
		if(discountRules.isEmpty())	
		{
			MessageUtil.setMessage("Please add Discount Attribute rule.", "color:red;");
			return false;
		}
		String	EliRule1 =saveEligibiltyAttributes();
		logger.debug(EliRule);
		if(!EliRule1.isEmpty()) {
			EliRule=EliRule1.substring(2);
		}
		//	EliRule=EliRule+"<OR>||";
			logger.debug(EliRule);
		String	discountRules1=discountRules+saveEligibiltyAttributes();
			logger.info("discountRules "+discountRules);
			//couponRuleStr = discountRules;
			if (discountRules.contains(Constants.DELIMITER_PIPE)) {

				String headerLabel="";
				String ruleToInsert=discountRules;
				String valueToInsert="";

				if(ruleToInsert.startsWith("Discount Type")) {
					//Discount Type|discount_type|String:null|1
						String disc = ruleToInsert.split("\\|")[2];
						String[] discType = disc.split(":");
						discTypeForItem = discType[1];
						String discount = ruleToInsert.split("\\|")[3];
						discValue = discount.split("<OR>")[0];
						if (discValue == null || Double.valueOf(discValue) <= 0) {
							MessageUtil.setMessage("Please provide valid discount value.", "color:red", "TOP");
							return false;
						}
				}
				
			}
	/*		String disc2=null;
			String TierProgram=null;
			String Tier=null;
			String CardSetProgram=null;
			String CardSet=null; */
			if (discountRules1.contains(Constants.DELIMITER_DOUBLE_PIPE)) {
			

			String headerLabel="";
			String ruleToInsert1=discountRules1;
			String valueToInsert="";
			
			StringBuilder sb = new StringBuilder();
			StringBuilder sb1 = new StringBuilder();
			StringBuilder sb2 = new StringBuilder();
			StringBuilder sb3 = new StringBuilder();
			StringBuilder sb4 = new StringBuilder();
			StringBuilder sb5 = new StringBuilder();
		//	if(ruleToInsert.contains("#PurchaseStore#")) {
				//Discount Type|discount_type|String:null|1
				String[] ruleStr = ruleToInsert1.split("\\|\\|");
				for (String eachRule : ruleStr) {
					if(eachRule==null || eachRule.trim().length()<=0) continue;
              
				/*	if(eachRule.contains("#PurchaseStore#")) {
						String disc1 = eachRule.split("\\;=;")[0];
						 disc2=eachRule.split("\\;=;")[3];
						logger.debug(disc2);
						StringBuilder DisC=sb5.append(disc2+",");
						logger.debug(DisC);
						DisC1=DisC.toString();
					} */
					
					if(eachRule.contains("#PurchaseTier#")) {
						{
						String disc1 = eachRule.split("\\;=;")[0];
						 TierProgram=eachRule.split("\\;=;")[2];
						 Tier=eachRule.split("\\;=;")[3];
					
						logger.debug(TierProgram);
						logger.debug(Tier);
						
						StringBuilder tier=sb1.append(TierProgram+",");
						DisC2 =tier.toString();
						
						StringBuilder tier1=sb2.append(Tier+",");
						DisC3=tier1.toString(); 
					}
						
					/*	if(!DisC2.isEmpty()) {
							DisC2=DisC2.substring(0, DisC2.length() - 1); 
						}*/
					}
					if(eachRule.contains("#PurchaseCardSet#")) {
						String disc1 = eachRule.split("\\;=;")[0];
						CardSetProgram=eachRule.split("\\;=;")[2];
						CardSet=eachRule.split("\\;=;")[3];
						logger.debug(CardSetProgram);
						logger.debug(CardSet);
						StringBuilder card=sb3.append(CardSetProgram+",");
						StringBuilder card1=sb4.append(CardSet+",");
						logger.debug(card);
						logger.debug(card1);
						DisC4=card.toString();
						//DisC2=card.toString();
						DisC5=card1.toString();
					//	card=	sb3.deleteCharAt(sb3.length()-1);
					//	String CardPro=card.toString();
					//	logger.debug(CardPro);
					//	String[] progra=CardPro.split(",");
					//	logger.debug(progra);
					//	StringBuilder card1=sb4.append(CardSet+",");
					//	card1=	sb4.deleteCharAt(sb4.length()-1);
					//	String CardNum=card1.toString();
					//	logger.debug(CardNum);
					//	String[] progra1=CardPro.split(",");
					//	logger.debug(progra1);
				
					}
				
					String DATETIME="";
					if(eachRule.contains("#PuchaseDate#")) {
						String Time="";
						String DateWeek="";
						String DateMon="";
						String SplDate="";
						String Date="";
						
						if(eachRule.contains("TOD")) {
						String tod = eachRule.split("\\;=;")[3];
		            	String	tod1=eachRule.split("\\;=;")[4];
		             	String	tod2=eachRule.split("\\;=;")[5];
						 Time=tod+Constants.DELIMITER_DOUBLE_PIPE+"time:"+tod1+","+tod2+Constants.ADDR_COL_DELIMETER;
						logger.debug(Time);
						}
						if(eachRule.contains("DOW")) {
							String dow = eachRule.split("\\;=;")[3];
				       	String	dow1=eachRule.split("\\;=;")[4];
							 DateWeek=dow+Constants.DELIMITER_DOUBLE_PIPE+"Day:"+dow1+Constants.ADDR_COL_DELIMETER;
							logger.debug(DateWeek);
							}
	
					if(eachRule.contains("DOM")) {
					
						String dom = eachRule.split("\\;=;")[3];
				     	String	dom1=eachRule.split("\\;=;")[4];
						 DateMon=dom+Constants.DELIMITER_DOUBLE_PIPE+"Day:"+dom1+Constants.ADDR_COL_DELIMETER;
						logger.debug(DateMon);
						}
					if(eachRule.contains("Day")) {
						
						String day = eachRule.split("\\;=;")[3];
					String	day1=eachRule.split("\\;=;")[4];
						 SplDate=day+Constants.DELIMITER_DOUBLE_PIPE+"Day:"+day1+Constants.ADDR_COL_DELIMETER;
						logger.debug(SplDate);
						}
					if(eachRule.contains("Dates")) {
						
						String dates = eachRule.split("\\;=;")[3];
					    String 	dates1=eachRule.split("\\;=;")[4];
					    String	dates2=eachRule.split("\\;=;")[5];
						 Date=dates+Constants.DELIMITER_DOUBLE_PIPE+"Day:"+dates1+","+dates2+Constants.ADDR_COL_DELIMETER;
						 Date=Date.substring(0, Date.length() - 1);
						logger.debug(Date);
						}
				StringBuilder DATE=sb.append(Time+DateWeek+DateMon+SplDate+Date+",");
					logger.debug(DATE);

			     purchaseDate=DATE.toString();
				}
			//	String[] Disc=ruleToInsert.split("||");
				logger.debug(ruleStr);
				logger.debug(DisC1);
				logger.debug(DisC2);
				logger.debug(DisC3);
				logger.debug(DisC4);
				logger.debug(DisC5);
				logger.debug(purchaseDate);
				
			/*	if(CardSetProgram==TierProgram || (!TierProgram.isEmpty() && CardSetProgram.isEmpty()) || (!CardSetProgram.isEmpty()
						&& TierProgram.isEmpty() ) ) {
					if(CardSetProgram==TierProgram) {
						Program=CardSetProgram;
					}
					else if(!TierProgram.isEmpty() && CardSetProgram.isEmpty()) {
						Program=TierProgram;
					}
					else if(!CardSetProgram.isEmpty()&& TierProgram.isEmpty())	{
						Program=CardSetProgram;
					}
					
				}
				else {
					MessageUtil.setMessage("Multiple Programs can't be selected in Single Discount Rule.Please select single Program", "color:red", "TOP");
					
				} */

		
				}	
			
				
		//	}

		
		}
			
			//harshi begin
			String operator = "";
			List<Component> childVboxList = new ArrayList<Component>();
			
			if(targetDivHasChildren(purIntAttributeDivId))childVboxList.addAll(purIntAttributeDivId.getChildren());

			Div chilVDiv = null;
			
			StringBuffer segmentRuleSB = new StringBuffer();//"all:"+((MailingList)dispMlLBoxId.getSelectedItem().getValue()).getListId());
			String ruleStr = null;
			boolean allValid=true;
			
			for (Object obj : childVboxList) {
				
				if(obj instanceof Div) {
					
					boolean isValid = true;
					for(Object object : ((Div)obj).getChildren()) {
						
						if(isValid==false) allValid=false;
						
						//Reset
						isValid = true;
						
						if(object instanceof Div) {
							
							
							chilVDiv = (Div)object;
							if(chilVDiv.getSclass().contains("drop_")) {
								continue;
							}
							
							ruleStr = prepareSegmentRule(chilVDiv);
							if(ruleStr == null) {
								isValid = false;
							}
							
						///	if(childVboxList.size() > 1)  operator = "<AND>";
							
							if(ruleStr != null && ruleStr.trim().length()>0) {	
								logger.info("discsegmentRuleDB============+"+segmentRuleSB);
								logger.info("discsegmentRuleDB============+"+segmentRuleSB!=null);
								logger.info("discsegmentRuleDB============+"+segmentRuleSB.length());

								if(segmentRuleSB!=null && segmentRuleSB.length()!=0) segmentRuleSB.append("||");
									
									segmentRuleSB.append(ruleStr+operator);
							}
							if(ruleStr != null && chilVDiv.getAttribute("columnName")!=null && chilVDiv.getAttribute("replaceColumnName")!=null ) {
								ruleStr = ruleStr.replace((String)chilVDiv.getAttribute("replaceColumnName"), (String)chilVDiv.getAttribute("columnName"));
								if(segmentRuleSB!=null) segmentRuleSB.append("||");
								
								segmentRuleSB.append(ruleStr);
							}
							
							
						}//ruleDiv
						
					}//tempDiv added for And
					
					
					if(isValid==false) allValid=false;
					
				}//if obj
				
			}//for each vb
			if(!allValid) {
				logger.warn(" >>>>>>>>>>>>> Segmentation validation failed ");
				
				MessageUtil.setMessage("Please provide proper value(s) for highlighted rule(s).", "color:red", "TOP");
				return false;
			}
			
			
			/*if(segmentRuleSB.toString().trim().length()==0) {
				
				MessageUtil.setMessage("There are no valid rules to add (as of now, derivative filter rules are ignored).", "color:red","TOP");
			}*/
			logger.debug("finalStr ::"+segmentRuleSB.toString());
			String rule =segmentRuleSB.toString();
			//couponRuleStr+=couponRuleStr!=null?"||"+rule:rule;
			long purchaseAmt = 0;
			if (rule.contains(Constants.DELIMITER_PIPE)) {

				String headerLabel="";
				String ruleToInsert=rule;
				String valueToInsert="";
				String[] RuleStr = rule.split("\\|\\|");
				for (String eachRule : RuleStr) {
					if(eachRule==null || eachRule.trim().length()<=0) continue;
					if(eachRule.startsWith(MinReceiptTotal)) {
						//Receipt Total|total_purchase_amt|number:>=|50<OR>
						String amt = eachRule.split("\\|")[3];
						minPurchaseAmt = amt.split("<OR>")[0];
						purchaseAmt =Long.valueOf(minPurchaseAmt);
					}
				
				}
			}
			// Discount
						logger.debug("totPurAmtLngBxId.getValue() is  :;" + discValue + "  puchase value :"+ purchaseAmt);
					//	if(discValue!=null)
					
						
			//harshi receipt end
			//discountDblBxId.setStyle(NORMAL_STYLE);
			//totPurAmtLngBxId.setStyle(NORMAL_STYLE);

			//if (tpaRadioId.isChecked() && mpvChkBoxId.isChecked()) {
				if (discTypeForItem.equalsIgnoreCase(DISC_TYPE_VALUE) && purchaseAmt>0 && (Double.valueOf(discValue) > purchaseAmt)) {
					MessageUtil.setMessage("Discount Type can not exceed the  Minimum Purchase Amount.", "color:red",
							"TOP");
					return false;
				}
			//}

			if (discTypeForItem.equalsIgnoreCase(DISC_TYPE_PERCENTAGE) && (Double.valueOf(discValue) > 100)) {
				MessageUtil.setMessage("Discount percentage cannot be more than 100.", "color:red", "TOP");
				return false;
			}
						
				/*		else {
							if (discValue.isEmpty()) {
								MessageUtil.setMessage("Please provide valid discount value.", "color:red", "TOP");
								return false;
							}
						} */
			/*//if (isNewPlugin) {
				// Discount
				logger.debug("maxDiscountDblBxId.getValue() is  :;" + maxDiscountDblBxId.getValue());
				String discTypeStr="";
				if (percentRadioId.isChecked()) {
					discTypeStr = DISC_TYPE_PERCENTAGE;
					dollerRadioId.setDisabled(true);
					//if (isNewPlugin) {
						if (accumulateCoupChkBxId.isChecked()) {
							onProductLabelId.setVisible(true);
							onReceiptLabelId.setVisible(false);
						}
					//}
				} else {
					discTypeStr = DISC_TYPE_VALUE;
					percentRadioId.setDisabled(true);
					//if (isNewPlugin) {
						if (accumulateCoupChkBxId.isChecked()) {
							onProductLabelId.setVisible(true);
							onReceiptLabelId.setVisible(false);
						}
					//}
				}

			Long tpAmtLong = totPurAmtLngBxId.getValue() == null ? 0 : totPurAmtLngBxId.getValue();
			
*/			List<SKUTemp> oldlistSku = coupDiscGenDao.findTempSkuBy(users.getUserId(), null, limit);

			List<SKUTemp> skuList = new ArrayList<>();
			if (oldlistSku != null && oldlistSku.size() > 0) {
				for (SKUTemp sku : oldlistSku) {
					if(editedRuleRowObj!=null && editedRuleRowObj.getSkuTempId()==sku.getSkuTempId()) continue;
					if(!discValue.isEmpty()) {
					if (sku.getDiscount().equals(Double.parseDouble(discValue))) {
						
				MessageUtil.setMessage(discValue + " Value already selected...",
								"color:red", "TOP");
						return false;
					}
					
					else if (sku.getTotPurchaseAmount().equals(purchaseAmt)) {
						MessageUtil.setMessage(
								purchaseAmt != 0 ? minPurchaseAmt : "N/A" + " Value already selected..,",
								"color:red", "TOP");
						return false;
					} else if (sku.getTotPurchaseAmount() != null && purchaseAmt < sku.getTotPurchaseAmount()
									? sku.getDiscount() < (Double.parseDouble(discValue))
									: sku.getDiscount() > (Double.parseDouble(discValue))) {
						MessageUtil.setMessage(
								"Minimum Purchase Amount and Discount Type values should be greater Previous Conditions",
								"color:red", "TOP");
						return false;
					}
					}
					
				}
			}
		
			
			if(!DisC1.equalsIgnoreCase("")) {
				  DisC1=DisC1.substring(0, DisC1.length() - 1);
				}	
			if(!DisC2.equalsIgnoreCase("")) {
				  DisC2=DisC2.substring(0, DisC2.length() - 1);
				}
			if(!DisC3.equalsIgnoreCase("")) {
				  DisC3=DisC3.substring(0, DisC3.length() - 1);
				}
			if(!DisC4.equalsIgnoreCase("")) {
				  DisC4=DisC4.substring(0, DisC4.length() - 1);
				}
			if(!DisC5.equalsIgnoreCase("")) {
				  DisC5=DisC5.substring(0, DisC5.length() - 1);
				}
   if(!DisC2.equalsIgnoreCase("") && DisC2.contains(",") )  {    
	
	   
	   if(DisC2.contains(","))
		   {
		   String[] new1=DisC2.split(",");
		   String[] new2=DisC3.split(",");
		int n=new1.length;
		   if(new1.length>1) {
			   for(int i=0;i<n;i++)
			   {
				   SKUTemp skuTemp= new SKUTemp();
				  DisC2=new1[i];
				  DisC3=new2[i];
				  if (tempCouponObj != null)
					
						skuTemp.setCouponId(tempCouponObj.getCouponId());
					
				  skuTemp.setUserId(users.getUserId());
		
				
					 skuTemp.setProgram(DisC2);
					 skuTemp.setTierNum(DisC3);
					if(!discValue.isEmpty())
					{
					skuTemp.setDiscount(Double.valueOf(Utility.truncateUptoTwoDecimal(discValue)));
					}
					
					skuTemp.setDiscountType(discTypeForItem);
					skuTemp.setTotPurchaseAmount(purchaseAmt);
			 
			        
					skuTemp.setOwnerId(orgOwnerUserId);
				
				
					
					skuTemp.setNoOfEligibleItems(eligibleItems);
				
					if(!DisC5.equalsIgnoreCase("")) {
						  DisC5=DisC5.substring(0, DisC5.length() - 1);
						  skuTemp.setCardSetNum(DisC5);
					  }
					  else {
						  skuTemp.setCardSetNum(DisC5);
					  }
					
					skuTemp.setEliRule(EliRule);
		        
			   
		   
		   
   

		            skuList.add(skuTemp);
					coupDiscGenDaoForDML.saveByCollection(skuList);
					Comparator<SKUTemp> comparator = Comparator.comparing(SKUTemp::getSkuTempId);
					SKUTemp minObject = skuList.stream().min(comparator).get();
					if (start == null)
						// start=skuList.get(0).getSkuTempId();
						start = minObject.getSkuTempId();
					end = skuList.get(skuList.size() - 1).getSkuTempId();
					limit = start + "::" + end;
			
   }
   }
		
		   }
   }
   else if (!DisC4.equalsIgnoreCase("") && DisC4.contains(","))  {    
	
	   if(DisC4.contains(","))
		   {
		   String[] new1=DisC4.split(",");
		   String[] new2=DisC5.split(",");
		int n=new1.length;
		   if(new1.length>1) {
			   for(int i=0;i<n;i++)
			   {
				   SKUTemp skuTemp= new SKUTemp();
				  DisC4=new1[i];
				  DisC5=new2[i];
				  if (tempCouponObj != null)
					
						skuTemp.setCouponId(tempCouponObj.getCouponId());
					
				  skuTemp.setUserId(users.getUserId());
			
									 skuTemp.setProgram(DisC4);
					 if(!DisC3.equalsIgnoreCase("")) {
						  DisC3=DisC3.substring(0, DisC3.length() - 1);
						  skuTemp.setTierNum(DisC3);
					  }
					  else {
						  skuTemp.setTierNum(DisC3);
					  }
					
					if(!discValue.isEmpty())
					{
					skuTemp.setDiscount(Double.valueOf(Utility.truncateUptoTwoDecimal(discValue)));
					}
					
					skuTemp.setDiscountType(discTypeForItem);
					skuTemp.setTotPurchaseAmount(purchaseAmt);
			    
			        
					skuTemp.setOwnerId(orgOwnerUserId);
			
				
					
					skuTemp.setNoOfEligibleItems(eligibleItems);
				
					skuTemp.setCardSetNum(DisC5);
					skuTemp.setEliRule(EliRule);
		      
		            skuList.add(skuTemp);
							coupDiscGenDaoForDML.saveByCollection(skuList);
							Comparator<SKUTemp> comparator = Comparator.comparing(SKUTemp::getSkuTempId);
							SKUTemp minObject = skuList.stream().min(comparator).get();
							if (start == null)
								// start=skuList.get(0).getSkuTempId();
								start = minObject.getSkuTempId();
							end = skuList.get(skuList.size() - 1).getSkuTempId();
							limit = start + "::" + end;
					
		   }
		   }
		  
		   else {
			   if(!DisC2.equalsIgnoreCase(DisC4) && !DisC2.isEmpty() &&!DisC4.isEmpty()) {
					 MessageUtil.setMessage("Mutliple programs can't be selected in one discount rule", "color:red;");
					 return false;
				 }
			   SKUTemp skuTemp= new SKUTemp();
				  if (tempCouponObj != null)
					
						skuTemp.setCouponId(tempCouponObj.getCouponId());
					
				  skuTemp.setUserId(users.getUserId());
				
				
					 skuTemp.setProgram(DisC2);
					 skuTemp.setTierNum(DisC3);
					if(!discValue.isEmpty())
					{
					skuTemp.setDiscount(Double.valueOf(Utility.truncateUptoTwoDecimal(discValue)));
					}
					
					skuTemp.setDiscountType(discTypeForItem);
					skuTemp.setTotPurchaseAmount(purchaseAmt);
			    
			        
					skuTemp.setOwnerId(orgOwnerUserId);
				
					
					
					skuTemp.setNoOfEligibleItems(eligibleItems);
				
					skuTemp.setCardSetNum(DisC5);
					skuTemp.setEliRule(EliRule);
		     
		         skuList.add(skuTemp);
					coupDiscGenDaoForDML.saveByCollection(skuList);
					Comparator<SKUTemp> comparator = Comparator.comparing(SKUTemp::getSkuTempId);
					SKUTemp minObject = skuList.stream().min(comparator).get();
					if (start == null)
						// start=skuList.get(0).getSkuTempId();
						start = minObject.getSkuTempId();
					end = skuList.get(skuList.size() - 1).getSkuTempId();
					limit = start + "::" + end;
		   }
				   }
		   }
		
   else {
	   logger.debug(DisC2); 
		logger.debug(DisC4);
	   if(!DisC2.equalsIgnoreCase(DisC4)&& !DisC2.isEmpty() &&!DisC4.isEmpty()) {
			 MessageUtil.setMessage("Mutliple programs can't be selected in one discount rule", "color:red;");
			 return false ;
	   }
	   SKUTemp skuTemp= new SKUTemp();
		 
		  if (tempCouponObj != null)
			
				skuTemp.setCouponId(tempCouponObj.getCouponId());
			
		  skuTemp.setUserId(users.getUserId());
		
			 if(DisC2.isEmpty()) {
			 skuTemp.setProgram(DisC4);
			 }
			 else if(DisC4.isEmpty()) {
				 skuTemp.setProgram(DisC2);
			 }
			 else if (!DisC2.isEmpty() && !DisC4.isEmpty())
			 {
				 skuTemp.setProgram(DisC2);
			 }
			 skuTemp.setTierNum(DisC3);
			if(!discValue.isEmpty())
			{
			skuTemp.setDiscount(Double.valueOf(Utility.truncateUptoTwoDecimal(discValue)));
			}
			
			skuTemp.setDiscountType(discTypeForItem);
			skuTemp.setTotPurchaseAmount(purchaseAmt);
			skuTemp.setOwnerId(orgOwnerUserId);
			skuTemp.setNoOfEligibleItems(eligibleItems);
		
			skuTemp.setCardSetNum(DisC5);
			skuTemp.setEliRule(EliRule);
    
         skuList.add(skuTemp);
			coupDiscGenDaoForDML.saveByCollection(skuList);
			Comparator<SKUTemp> comparator = Comparator.comparing(SKUTemp::getSkuTempId);
			SKUTemp minObject = skuList.stream().min(comparator).get();
			if (start == null)
				// start=skuList.get(0).getSkuTempId();
				start = minObject.getSkuTempId();
			end = skuList.get(skuList.size() - 1).getSkuTempId();
			limit = start + "::" + end;
   }
   
			Div tempDiv=new Div();
		    Row row = new Row();
		    if(!discValue.isEmpty()) {
			Label label = new Label(String.valueOf(Double.valueOf(Utility.truncateUptoTwoDecimal(discValue))));
			label.setValue(String.valueOf(Double.valueOf(Utility.truncateUptoTwoDecimal(discValue))));
			label.setParent(row);
		    }
		    else {
		    	Label label = new Label();
		    	label.setParent(row);
		    }
		    Label	label = new Label(purchaseAmt != 0 ? minPurchaseAmt : "N/A");
			label.setValue(purchaseAmt != 0 ? minPurchaseAmt : "N/A");
			label.setParent(row);
			if(!DisC3.isEmpty() && DisC5.isEmpty() ) {
				String val=loyaltyProgramTierDao.getTierName(DisC3);
			 label = new Label(val);
			label.setParent(row);
			 label = new Label("--");
	    	label.setParent(row);
		    }
	
			if(!DisC5.isEmpty() && DisC3.isEmpty()) {
				String val=loyaltyCardSetDao.getCardName(DisC5);
					 label = new Label("--");
			    	label.setParent(row);
			    	 label = new Label(val);
						label.setParent(row);
			}
			if(!DisC5.isEmpty() &&!DisC3.isEmpty()) {
				String val=loyaltyProgramTierDao.getTierName(DisC3);
				String val1=loyaltyCardSetDao.getCardName(DisC5);
					 label = new Label(val);
			    	label.setParent(row);
			    	 label = new Label(val1);
						label.setParent(row);
			}
			if(DisC5.isEmpty()  && DisC3.isEmpty() ) {
					 label = new Label("--");
			    	label.setParent(row);
			    	 label = new Label("--");
						label.setParent(row);
			}
			//if (isNewPlugin) { 
				/* if (accumulateCoupChkBxId.isChecked()) {
			  if(maxDiscountDblBxId.getValue()!=null) { 
				  label = new Label(maxDiscountDblBxId.getValue().toString());
			  label.setValue(maxDiscountDblBxId.getValue().toString());
			  label.setParent(row); 
			  }else { 
				  label = new Label("--"); 
				  label.setParent(row);
			  
			  } 
			  } else { 
				  label =new Label("--"); 
				  label.setParent(row); 
				  }*/
			  
			  /*} else {
			  
			  label = new Label(""); 
			  label.setParent(row); 
			  }*/
			 
			/*label = new Label("");
			label.setParent(row);*/
			
			//Hbox hbox = new Hbox();
			
			//APP-2720
			Image editImg = new Image("/img/email_edit.gif");
			editImg.setStyle("cursor:pointer;");
			editImg.setAttribute("COUPON_TYPE", COUPON_RULE_EDIT);
			editImg.addEventListener("onClick", this);
			editImg.setParent(tempDiv);
			
			Image delImg = new Image("img/delt_icn.png");
			delImg.setStyle("cursor:pointer;");
			delImg.setAttribute("COUPON_TYPE", COUPON_RULE_DELETE);
			delImg.addEventListener("onClick", this);
			delImg.setParent(tempDiv);
			
			//hbox.setParent(row);
			tempDiv.setParent(row);
			row.setParent(receiptDiscountGenRowsId);
			row.setAttribute("skutemp",end);
		if(editedRuleRowObj!=null) {
				coupDiscGenDaoForDML.deleteAllSkuBy(users.getUserId(),String.valueOf(editedRuleRowObj.getSkuTempId()+"::"+String.valueOf(editedRuleRowObj.getSkuTempId())));
					if (receiptDiscountGenRowsId.getVisibleItemCount() > 0) {
						List<Component> rowList = receiptDiscountGenRowsId.getChildren();
						List<Component> tempRowList = new ArrayList<Component>();
						tempRowList.addAll(receiptDiscountGenRowsId.getChildren());
					if (rowList != null && rowList.size() > 0) {
					for (Object object : tempRowList) {
							Row existRow = (Row) object;
							if(editedRuleRowObj.getSkuTempId()==(long)existRow.getAttribute("skutemp")) {
								receiptDiscountGenRowsId.removeChild(existRow);
							}
					}
					}
					}
				}
		}
		//discountDblBxId.setText("");
		/*maxDiscountDblBxId.setText("");
		quantityDblBxId.setText("");
		if(mpvChkBoxId.isChecked()){
			totPurAmtLngBxId.setText("");
			mpvChkBoxId.setChecked(false);
			
		}*/
            	 
		setheaders(discTypeForItem);
		return true;
	}

	private String saveEligibiltyAttributes() {
		// TODO Auto-generated method stub
		List<Component> childVboxList = new ArrayList<Component>();
		if (targetDivHasChildren(EligibilityAttributeDivId))
			childVboxList.addAll(EligibilityAttributeDivId.getChildren());
	/*	if (childVboxList.size() == 0) {// only the dashed areas r left
			MessageUtil.setMessage("No Attribute(s) added to create reward rule. Please add at least one Attribute(s).", "color:red;");
			return null;
		} */

		Div chilVDiv = null;
		StringBuffer segmentRuleSB = new StringBuffer();// "all:"+((MailingList)dispMlLBoxId.getSelectedItem().getValue()).getListId());
		String ruleStr = null;
		boolean allValid = true;
if(childVboxList.size()!=0) {
		for (Object obj : childVboxList) {

			if (obj instanceof Div) {

				boolean isValid = true;
				for (Object object : ((Div) obj).getChildren()) {

					if (isValid == false)
						allValid = false;

					// Reset
					isValid = true;

					if (object instanceof Div) {
						chilVDiv = (Div) object;
						if (chilVDiv.getSclass().contains("drop_")) {
							continue;
						}
						ruleStr = createSpecialRule(chilVDiv);
						if (ruleStr == null) {
							isValid = false;
						}
						if (ruleStr != null && ruleStr.trim().length() > 0) {
							segmentRuleSB.append(ruleStr + "");
						}
					} // ruleDiv

				} // tempDiv added for And

				if (isValid == false)
					allValid = false;

			} // if obj
		}
		if (!allValid) {
			logger.warn(" >>>>>>>>>>>>> Segmentation validation failed ");

			MessageUtil.setMessage("Please provide proper value(s) for highlighted rule(s).", "color:red", "TOP");
			return null;
		}
	/*	if (segmentRuleSB.toString().trim().length() == 0) {

			MessageUtil.setMessage(
					"There are no valid rules to prepare reward rule (as of now, derivative filter rules are ignored).",
					"color:red", "TOP");
			return null;
		} */
		
		if(validateDuplicacy(segmentRuleSB.toString())) {
			MessageUtil.setMessage(
					"Duplicate rules found. Please ensure each rule is distinct.",
					"color:red", "TOP");
			return null;
		}
		
		return segmentRuleSB.toString();
}
else {
	return "";
}

	}


	private String createSpecialRule(Div ruleVDiv) {
		List childDivList = ruleVDiv.getChildren();
		Div ruleDiv = null;
		boolean retValue = true;
		boolean isValid = true;
		String hashTagValue = null;
		String hashTagValue1 = null;
		StringBuilder sb = new StringBuilder();
		StringBuilder sb1 = new StringBuilder();
		for (Object object : childDivList) {
			if (isValid == false)
				retValue = false;
			// Reset the isValid
			isValid = true;
			if (object instanceof Div) {
				Label fielslbl = null;
				List columns = null;
				String col = null;
				String itemCode = null;
				Listbox ruleLb = null;
				Listbox operatorLb = null;
				Listbox numberLb = null;
				String val1 = "";
				String val2 = "";
				String val3 = "";
				String jsonatt = "";
				String programId = "";
				ruleDiv = (Div) object;
				if (ruleDiv.getSclass().contains("drop_")) {
					continue;
				}
				columns = ruleDiv.getChildren();
				fielslbl = (Label) columns.get(1); //
				col = (String) fielslbl.getAttribute("columnName");
				itemCode = (String) fielslbl.getAttribute("item");
				ruleLb = (Listbox) columns.get(2);
				operatorLb = (Listbox) columns.get(5);
				numberLb = (Listbox) columns.get(7);
				Textbox txtbox = (Textbox) columns.get(8);
				Datebox dateBox1 = (Datebox) columns.get(11);
				Datebox dateBox2 = (Datebox) columns.get(12);
				CouponsEnum couponsEnum;
				couponsEnum = (CouponsEnum) ruleLb.getItemAtIndex(0).getValue();
				hashTagValue = Utility.specialRuleHashTag.get(itemCode);
		
					if (couponsEnum.getParentEnum().name().equals(CouponsEnum.PURCHASE_IN_TIER.name())
							|| couponsEnum.getParentEnum().name()
									.equals(CouponsEnum.PURCHASE_IN_CARD_SET.name())) {
						programId = ruleLb.getSelectedItem().getValue().toString();
						if (ruleLb.getSelectedIndex() == 0 || programId == null || programId.length() == 0) {
							isValid = false;
							setErrorToDiv(ruleDiv, isValid);
							continue;
						}
					} else {
						val1 = ruleLb.getSelectedItem().getValue().toString();
						if (ruleLb.getSelectedIndex() == 0 || val1 == null || val1.length() == 0) {
							isValid = false;
							setErrorToDiv(ruleDiv, isValid);
							continue;
						}
					}
					if (operatorLb.isVisible()) {
						if (val1.isEmpty()) {
							val1 = operatorLb.getSelectedItem().getValue().toString();
							if (operatorLb.getSelectedIndex() == 0 || val1 == null || val1.length() == 0) {
								isValid = false;
								setErrorToDiv(ruleDiv, isValid);
								continue;
							}
						} else {
							val2 = operatorLb.getSelectedItem().getValue().toString();
							if (operatorLb.getSelectedIndex() == 0 || val2 == null || val2.length() == 0) {
								isValid = false;
								setErrorToDiv(ruleDiv, isValid);
								continue;
							}
						}
					}
			//	}

				if (txtbox.isVisible()) {
					if (val1.isEmpty()) {
						val1 = txtbox.getText().trim();
						if (val1 == null || val1.length() == 0) {
							isValid = false;
							setErrorToDiv(ruleDiv, isValid);
							continue;
						}
					} else {
						val2 = txtbox.getText().trim();
						if (val2 == null || val2.length() == 0) {
							isValid = false;
							setErrorToDiv(ruleDiv, isValid);
							continue;
						}
						try{
							Integer.parseInt(val2);
							}
							catch(NumberFormatException e) {
								isValid = false;
								setErrorToDiv(ruleDiv, isValid);
								continue;
							}
					}
				}
				if (dateBox1.isVisible()) {
					if(dateBox1.getValue()==null){
						isValid = false;
						setErrorToDiv(ruleDiv, isValid);
						continue;
					}
					val2 = dateFormat.format(dateBox1.getValue());
				}
				if (dateBox2.isVisible()) {
					if(dateBox2.getValue()==null){
						isValid = false;
						setErrorToDiv(ruleDiv, isValid);
						continue;
					}
					val3 = dateFormat.format(dateBox2.getValue());
					if (dateBox1.getValue().after(dateBox2.getValue())) {
						isValid = false;
						setErrorToDiv(ruleDiv, isValid);
						continue;
					}

				}
				if (numberLb.isVisible()) {
					if (val2.isEmpty()) {
						val2 = numberLb.getSelectedItem().getValue().toString();
						if (val2 == null || val2.length() == 0) {
							isValid = false;
							setErrorToDiv(ruleDiv, isValid);
							continue;
						}
					} else {
						val3 = numberLb.getSelectedItem().getValue().toString();
						if (val3 == null || val3.length() == 0) {
							isValid = false;
							setErrorToDiv(ruleDiv, isValid);
							continue;
						}
						if (Integer.parseInt(val2) > Integer.parseInt(val3)) {
							isValid = false;
							setErrorToDiv(ruleDiv, isValid);
							continue;
						}
					}
				}
				if (programId != null && programId.length() > 0)
					hashTagValue = hashTagValue.replace("<programId>", programId);
				if (jsonatt != null && jsonatt.length() > 0)
					hashTagValue = hashTagValue.replace("<jsonAttribute>", jsonatt);
				if (val1 != null && val1.length() > 0)
					hashTagValue = hashTagValue.replace("<val1>", val1);
				if (val2 != null && val2.length() > 0)
					hashTagValue = hashTagValue.replace("<val2>", val2);
				if (val3 != null && val3.length() > 0)
					hashTagValue = hashTagValue.replace("<val3>", val3);
				// add hash tag
		
				hashTagValue = "||"+itemCode + ";=;" + hashTagValue ;
				sb.append(hashTagValue);
			}
			isValid = true;
			setErrorToDiv(ruleDiv, isValid);
		}
		if (isValid == false)
			retValue = false;
		if (!retValue)
			return null;
		edit=sb.toString();
		return sb.toString();
	}

	private void getAllPromoFromDB() {
		totalCount = couponsDao.findTotCountCoupons(users.getUserId());
		// couponsPagingId.addEventListener("onPaging", this);
		couponsPagingId.setTotalSize(totalCount);

		// set default Paging size
		memberPerPageLBId.setSelectedIndex(0);
		// get only limit of the PromoCode From DB
		// fillCouponsBySize(0,5);
	} // getAllPromoFromDB

	private int p = 0;
	Map<String, SkuFile> selectOfSkuMap = new HashMap<String, SkuFile>();
	Map<Long, SkuFile> deSelectSkuMap = new HashMap<Long, SkuFile>();

	public void onPaging$skuPaging(ForwardEvent event) {
		logger.debug("Paging Event called here ");
		PagingEvent pagingEvent = (PagingEvent) event.getOrigin();
		int desiredPage = pagingEvent.getActivePage();
		int pSize = pagingEvent.getPageable().getPageSize();
		int ofs = desiredPage * pSize;
		List<Object> result = skuFileDao.getAllSkuSearchBy(orgOwnerUserId, ofs, (byte) pSize, searchStr);
		int cnt = viewSKULbId.getItemCount();
		for (; cnt > 0; cnt--) {
			viewSKULbId.removeItemAt(cnt - 1);
		}
		if (result == null)
			return;
		Listitem item = null;
		for (Object data : result) {
			item = new Listitem();
			Object[] obj = (Object[]) data;
			item.setValue(obj);
			new Listcell().setParent(item);
			new Listcell(obj[0].toString()).setParent(item);
			new Listcell(obj[1] != null ? obj[1].toString() : "").setParent(item);
			new Listcell(obj[2] != null ? obj[2].toString() : "").setParent(item);
			item.setParent(viewSKULbId);
		}
	}

	/*@Override
	public void render(Listitem li, SkuFile sku, int arg2) throws Exception {

		li.setValue(sku);
		li.appendChild(new Listcell(""));

		if (sku.getSku() != null) {
			li.appendChild(new Listcell(sku.getSku()));
		} else {
			li.appendChild(new Listcell(sku.getSku()));
		}
		if (sku.getListPrice() != null) {
			li.appendChild(new Listcell(sku.getListPrice().toString()));
		} else {
			li.appendChild(new Listcell(sku.getSku()));
		}
		if (sku.getDescription() != null) {
			li.appendChild(new Listcell(sku.getDescription()));
		} else {
			li.appendChild(new Listcell(sku.getSku()));
		}

	}*/
	
	@Override
	public void render(Listitem li, SkuFile sku, int arg2) throws Exception {

		li.setValue(sku);
		li.appendChild(new Listcell(""));

		if (sku.getSku() != null) {
			li.appendChild(new Listcell(sku.getDescription()));
		} else {
			li.appendChild(new Listcell(sku.getDescription()));
		}
	}

	public void onSelect$memberPerPageLBId() {
		String selectStr = memberPerPageLBId.getSelectedItem().getLabel();
		int pNo = Integer.parseInt(selectStr);

		couponsPagingId.setPageSize(pNo);

		couponsPagingId.setActivePage(0);
		Utility.refreshGridModel(viewCouponsELObj, 0, false);
		// fillCouponsBySize(0 ,pNo);
	} // onSelect$memberPerPageLBId

	private void fillCouponsBySize(int strtIdx, int endIdx) {

		// List<Coupons> couponsCampList =
		// couponsDao.findCouponsByUserIdWithLimits(users.getUserId(), strtIdx, endIdx);
		List<Coupons> couponsCampList = null;

		String value = srchLbId.getSelectedItem().getValue().toString();
		if (value.equals(SEARCH_BY_NAME)) {
			couponsCampList = couponsDao.getCouponsByPromoName(users.getUserId(),
					searchByPromoCodeNameTbId.getValue().trim(), strtIdx, endIdx);
		}

		else if (value.equals(SEARCH_BY_DATE)) {
			couponsCampList = couponsDao.getPromoCodesBetweenCreationDates(fromDateStr, toDateStr, users.getUserId(),
					strtIdx, endIdx);
		}

		else if (value.equals(SEARCH_BY_STATUS)) {
			couponsCampList = couponsDao.getCouponsByStatus(users.getUserId(),
					codeStatusLb.getSelectedItem().getLabel(), strtIdx, endIdx);
		}

		logger.debug(">>>>>" + couponsCampList);
		if (couponsCampList == null)
			return;
		Components.removeAllChildren(couponRowsId);
		Row tempRow;
		Label tempLbl = null;
		Div tempDiv = null;

		for (Coupons coupons : couponsCampList) {

			tempRow = new Row();

			// Coupon Name
			tempLbl = new Label(coupons.getCouponName());
			tempLbl.setParent(tempRow);

			// Description
			tempLbl = new Label(coupons.getCouponDescription());
			tempLbl.setParent(tempRow);

			// Promo code type Single or Multiple
			if (coupons.getCouponGeneratedType().equalsIgnoreCase("single"))
				tempLbl = new Label("Single code");
			else if (coupons.getCouponGeneratedType().equalsIgnoreCase("multiple"))
				tempLbl = new Label("Multiple & Random code");
			tempLbl.setParent(tempRow);

			logger.info("coupon name is ::" + coupons.getCouponName());
			// logger.info("created date is ::"+coupons.getCouponCreatedDate().getTime());
			// Created On
			tempLbl = new Label(
					MyCalendar.calendarToString(coupons.getUserCreatedDate(), MyCalendar.FORMAT_DATEONLY_GENERAL));
			tempLbl.setParent(tempRow);

			// Status
			tempLbl = new Label(coupons.getStatus());
			tempLbl.setParent(tempRow);

			// Discount
			String tempStr = "";
			if (coupons.getDiscountType().trim().equals("Percentage")) { // TODO set the constants
				tempStr = "% on";
			} else {
				tempStr = "$ on";
			}

			if (coupons.getDiscountCriteria().trim().contains("SKU")) {
				tempStr += " Product";
			} else {
				tempStr += " Receipt";
			}
			// tempStr = tempStr+" "+coupons.getDiscountCriteria().trim();

			tempLbl = new Label(tempStr);
			tempLbl.setParent(tempRow);

			// Validity Period
			if (coupons.getExpiryType().equalsIgnoreCase(Constants.COUP_VALIDITY_PERIOD_DYNAMIC)) {
				logger.info("dynamic validity-------");
				tempLbl = new Label("Dynamic");
				tempLbl.setParent(tempRow);
			} else if (coupons.getExpiryType().equalsIgnoreCase(Constants.COUP_VALIDITY_PERIOD_STATIC)) {
				logger.info("static validity-------");
				tempLbl = new Label(
						MyCalendar.calendarToString(coupons.getCouponCreatedDate(), MyCalendar.FORMAT_DATEONLY_GENERAL)
								+ " - " + MyCalendar.calendarToString(coupons.getCouponExpiryDate(),
										MyCalendar.FORMAT_DATEONLY_GENERAL));
				tempLbl.setParent(tempRow);
			}

			// No.of Codes

			String numOfCodeStr = "";
			boolean isSingleType = false;
			if (coupons.getCouponGeneratedType().equals("single")) {

				if (coupons.getAutoIncrCheck()) {
					numOfCodeStr = "Unlimited";
				} else {
					numOfCodeStr = "" + coupons.getTotalQty();
				}

				isSingleType = true;
			} else {
				if (coupons.getAutoIncrCheck()) {
					numOfCodeStr = coupons.getTotalQty() + "(Auto add)";
				} else {
					numOfCodeStr = coupons.getTotalQty() + "";
				}
			}
			tempLbl = new Label(numOfCodeStr);
			tempLbl.setParent(tempRow);

			// Actions
			tempDiv = new Div();

			// Edit Image
			Image editImg = new Image("/img/email_edit.gif");
			editImg.setStyle("cursor:pointer;margin-right:10px;");
			editImg.setTooltiptext("Edit");
			editImg.setAttribute("COUPON_TYPE", COUPON_EDIT);
			editImg.addEventListener("onClick", this);
			editImg.setParent(tempDiv);

			// Active Image
			Image tempImg = new Image();
			String statusStr = coupons.getStatus();

			// Pause or Active images
			if (statusStr.equals(Constants.COUP_STATUS_ACTIVE) || statusStr.equals(Constants.COUP_STATUS_RUNNING)
					|| statusStr.equals(Constants.COUP_STATUS_EXPIRED)) {

				tempImg.setSrc("/img/pause_icn.png");
				tempImg.setTooltiptext("Pause");

			} else if (statusStr.equals(Constants.COUP_STATUS_PAUSED)) {
				tempImg.setSrc("/img/play_icn.png");
				tempImg.setTooltiptext(Constants.COUP_STATUS_ACTIVE);

			}

			tempImg.setAttribute("COUPON_TYPE", COUPON_STATUS);
			tempImg.setStyle("cursor:pointer;margin-right:10px;");
			tempImg.addEventListener("onClick", this);
			tempImg.setParent(tempDiv);

			// Delete Image
			Image delImg = new Image("/img/action_delete.gif");
			delImg.setStyle("cursor:pointer;margin-right:10px;");
			delImg.setAttribute("COUPON_TYPE", COUPON_DELETE);
			delImg.setTooltiptext("Delete");
			delImg.addEventListener("onClick", this);
			delImg.setParent(tempDiv);
			// reports Image
			Image repImg = new Image("/img/theme/home/reports_icon.png");
			repImg.setStyle("cursor:pointer;margin-right:10px;");
			repImg.setAttribute("COUPON_TYPE", COUPON_REPORTS);
			repImg.setTooltiptext("Reports");
			repImg.addEventListener("onClick", this);
			repImg.setParent(tempDiv);

			if (!isSingleType) {
				// export promocodes
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

	// allStoresChkBxId

	
	//byshailika
	/*public void onCheck$singCoupChkBxId() {
		if (singCoupChkBxId.isChecked()) {
			singlelimitLongbxId.setDisabled(true);
		} else
			singlelimitLongbxId.setDisabled(false);
	} // onCheck$singCoupChkBxId
*/
	private Image addStoreImgId;

	public void onCheck$allStoresChkBxId() {
		if (allStoresChkBxId.isChecked()) {
			storeNumbCmboBxId.setDisabled(true);
			selectedStoreLbId.setDisabled(true);
			setVisibleItems(selectedStoreLbId, false);
			addStoreImgId.setVisible(false);
		} else {
			storeNumbCmboBxId.setDisabled(false);
			selectedStoreLbId.setDisabled(false);
			setVisibleItems(selectedStoreLbId, true);
//			addStoreImgId.setVisible(true);
			addStoreImgId.setVisible(true);
		}
//		selectedStoreDivId.setVisible(!allStoresChkBxId.isChecked());
	}

	private void setVisibleItems(Listbox lstBox, boolean flag) {

		List<Listitem> chaildItemLst = lstBox.getItems();
		for (Listitem listitem : chaildItemLst) {
			Image delImg = ((Image) ((Listcell) listitem.getLastChild()).getFirstChild());
			delImg.setVisible(flag);
		}
	}

	public void onSelect$bctypeListboxId() {
		setDefaultDimensions();
	}

	
	//byshailika
	/*public void onCheck$coupGenRadioGrId() {

		if (coupGenRadioGrId.getSelectedIndex() == 0) {
			multiSelCoupDivId.setVisible(false);
			singleSelCoupDivId.setVisible(true);
		
		} else {
			singleSelCoupDivId.setVisible(false);
			multiSelCoupDivId.setVisible(true);
		}

	} // onCheck$coupGenRadioGrId
	*/



	//byshailika
	/*public void onCheck$redeemdsingCoupChkBxId() {
		if (redeemdsingCoupChkBxId.isChecked()) {
			redeemdlimitLongbxId.setDisabled(true);
		} else
			redeemdlimitLongbxId.setDisabled(false);
	} // onCheck$redeemdsingCoupChkBxId
*/


	public void onCheck$discountRadioGrId() {
		Radio selRadio = discountRadioGrId.getSelectedItem()!=null?discountRadioGrId.getSelectedItem():null;
		if (selRadio == null)
			return;

		if (selRadio == skuRadioId) {
			productDiscGrid.setVisible(true);
			receiptDiscGrid.setVisible(false);
			/*excludeDiv.setVisible(true);
		//	addItemAttributes();
			//productDetailtaDivId.setVisible(true);
			numberEligibleDivId.setVisible(true);
			quantityDivId.setVisible(true);
			// onReceiptLabelId.setVisible(false);
			// onProductLabelId.setVisible(true);
							if (accumulateCoupChkBxId.isChecked()) {
								maximumDiscountDivId.setVisible(true);
								quantityDivId.setVisible(true);
							} else {
								maximumDiscountDivId.setVisible(false);
								//quantityDivId.setVisible(false);
								if (higestPriceRadioId.isSelected()
										||higestPricewithoutRadioId.isSelected()) {
									quantityDivId.setVisible(true);
								}
							}
							
							

			mpvDivId.setVisible(false);
			//skuVisibleDivId.setVisible(true);
			logger.debug("dataflag====" + dataflag);
			if (dataflag) {
				// adjustListBoxes();
				// prepraAllAttributeData(null);
			}
*/			
			receiptVisibleGBID.setVisible(false);
			//productVisibleGBID.setVisible(true);
			validateDiv.setVisible(true);
			
			discountTypeLbId.getItemAtIndex(3).setVisible(true);
		//	discountTypeLbId.getItemAtIndex(4).setVisible(true);
			if(editCouponObj==null) {
				setValueCodeOnProduct();
				discountTypeLbId.setSelectedIndex(0);
			}
			
			/*int cnt = addedSKUSegmentLBId.getItemCount();
			for (; cnt > 0; cnt--) {
				addedSKUSegmentLBId.removeItemAt(cnt - 1);
			}
			rulesGBId.setVisible(false);*/
			//rulesGBIdNew.setVisible(false);
			//prepare ruleStr to display discount attributes by default
			if(editCouponObj==null || tempCouponObj!=null) {
			List<Component> existingRules = new ArrayList<Component>();
			existingRules = discountAttributeDivId.getChildren();
			for(Component c:existingRules){
				deleteExistedRulesDiv(c);
			}
			existingRules = purIntAttributeDivId.getChildren();
			for(Component c:existingRules){
				deleteExistedRulesDiv(c);
			}
	
			String discountTypeRuleStr="";
			String eligibleItemsRuleStr="";
			if(tempCouponObj!=null) {
				String discType=tempCouponObj.getDiscountType();
				String eligibleItems = tempCouponObj.getNoOfEligibleItems();
				discountTypeRuleStr="Discount Type|discount_type|number:"+discType+"| ";
				eligibleItemsRuleStr="Eligible Items|no_of_eligible_items|String:"+segRuleEligibleItemsMap.get(eligibleItems)+"|null";
				}else {
				//discountTypeRuleStr="Discount Type|discount_type|number:is| ";
					String discType=discountTypeStr!=null?discountTypeStr:"Percentage";
					discountTypeRuleStr="Discount Type|discount_type|number:"+discType+"| ";
				eligibleItemsRuleStr=(discountTypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("loyaltyRewards")
						||discountTypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("purchases")?
						"Eligible Items|no_of_eligible_items|String:highest priced items|null":"Eligible Items|no_of_eligible_items|String:all items|null");
				}
			
			parseRules(discountTypeRuleStr);
			parseRules(eligibleItemsRuleStr);
		}
		//	AutoDisDiv.setVisible(true);
		excludeAlreadyDiscountedItemsDiv.setVisible(true);
		} else if (selRadio == tpaRadioId) {
			productDiscGrid.setVisible(false);
			receiptDiscGrid.setVisible(true);
			validateDiv.setVisible(false);
		//	AutoDisDiv.setVisible(false);
			/*// onReceiptLabelId.setVisible(true);
			// onProductLabelId.setVisible(false);
			excludeDiv.setVisible(false);
			//productDetailtaDivId.setVisible(false);
                        numberEligibleDivId.setVisible(false);
                        if (accumulateCoupChkBxId.isChecked()) {
            					maximumDiscountDivId.setVisible(true);
            					quantityDivId.setVisible(false);
          
            				}
            			else {
            				maximumDiscountDivId.setVisible(false);
            				quantityDivId.setVisible(false);
            			}

			mpvDivId.setVisible(true);
			skuVisibleDivId.setVisible(false);*/
			//receiptVisibleGBID.setVisible(true);
			//productVisibleGBID.setVisible(false);
			discountTypeLbId.getItemAtIndex(3).setVisible(false);
		//	discountTypeLbId.getItemAtIndex(4).setVisible(false);
			if(editCouponObj==null) {
				setValueCodeOnProduct();
				discountTypeLbId.setSelectedIndex(0);
			}
			/*rulesGBId.setVisible(false);*/
			//rulesGBIdNew.setVisible(false);
			if(editCouponObj==null || tempCouponObj!=null) {
			//prepare ruleStr to display discount attributes by default
			List<Component> existingRules = new ArrayList<Component>();
			existingRules = discountAttributeDivId.getChildren();
			for(Component c:existingRules){
				deleteExistedRulesDiv(c);
			}
			existingRules = purIntAttributeDivId.getChildren();
			for(Component c:existingRules){
				deleteExistedRulesDiv(c);
			}
	
			
			String discountTypeRuleStr="";
			if(tempCouponObj!=null) {
				String discType=tempCouponObj.getDiscountType();
				discountTypeRuleStr="Discount Type|discount_type|number:"+discType+"| ";
				}else {
					String discType=discountTypeStr!=null?discountTypeStr:"Percentage";
					discountTypeRuleStr="Discount Type|discount_type|number:"+discType+"| ";
				}
			
			parseRules(discountTypeRuleStr);
			}
			excludeAlreadyDiscountedItemsDiv.setVisible(false);
		}
		// set the geader Fileds On Grid
		//setheaders(editCouponObj!=null ?editCouponObj.getDiscountType():"Percentage");
		setheaders(editCouponObj!=null ?editCouponObj.getDiscountType():(discountTypeStr!=null?discountTypeStr:"Percentage"));
		if (editCouponObj == null) {
		SetItemsToBeDragged(2, purchaseAttrLbId);
		SetItemsToBeDragged(3, discountAttrLbId);
		SetItemsToBeDragged(1,EligibilityAttrLbId);
		}
		onSelect$discountTypeLbId();
	}

//	private static String regexStr = "[\\s,\\n;\\t]+";   // "[\\s,\\n\\t]+";

	public void onChange$expiryDateBxId() {

		if (createDateBxId.getValue() == null || expiryDateBxId.getValue() == null) {
			MessageUtil.setMessage("Please provide valid details.", "color:red", "TOP");
			return;
		}

		Date startDate = createDateBxId.getValue();
		Calendar start = Calendar.getInstance();
		start.setTime(startDate);
		start = setDefaultTimeforCal(start, true);

		Date endDate = expiryDateBxId.getValue();
		Calendar end = Calendar.getInstance();
		end.setTime(endDate);
		end = setDefaultTimeforCal(end, false);

		if (end.before(start)) {
			MessageUtil.setMessage("'From' date cannot be after 'To' date.", "red");
			return;
		}

	} // onSelect$expiryDateBxId

	private boolean isVaildCoupon(Coupons editCouponObj) {

		if (editCouponObj == null) {
			String coupName = couponNameTxtBxId.getValue().trim();

			if (coupName.length() == 0 || coupName.contains("_") || coupName.contains(" ")) {
				MessageUtil.setMessage("Please provide valid Discount code name.", "color:red", "TOP");
				return false;
			} else if (validateCouponName4SpecialChars(coupName) == false) {
				MessageUtil.setMessage(
						"Please provide valid Discount code name which can include alphanumeric characters and special characters as * and -.",
						"color:red", "TOP");
				return false;
			} else {
				// Check the Coupon Name already exists
				boolean isExistsCoup = couponsDao.checkCoupByName(coupName, orgId);
				if (isExistsCoup == true) {
					MessageUtil.setMessage("Discount code name already exists.", "color:red", "TOP");
					return false;
				}
			}

		}
	
		
		//byshailika
/*if(coupGenRadioGrId.getSelectedIndex() == 0) {
			
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
			
			Pattern p = Pattern.compile("[a-zA-Z0-9-]+");
			Matcher m = p.matcher(singCoupTxtBxId.getValue().trim());
			if(!m.matches()){
				MessageUtil.setMessage("Please provide valid single Promo-code value.\nSpecial characters except (-) are not allowed.","color:red","TOP");
				return false;
			}
			
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
			if(editCouponObj != null && editCouponObj.getCouponCode()==null) {
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
	*/	
		/*lpIBxId.setStyle(NORMAL_STYLE);
		if( discountTypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("loyaltyRewards") && lpIBxId.getValue() == null ){
			MessageUtil.setMessage("Please provide loyalty points/rewards.","color:red","TOP");
			lpIBxId.setStyle(ERROR_STYLE);
			return false;
		}
		else{
			if(lpIBxId.getValue() != null){
			int checkValue = Integer.parseInt( lpIBxId.getValue().toString().trim());
			if(checkValue <= 0){
			MessageUtil.setMessage("Please provide valid loyalty points/rewards.","color:red","TOP");
			lpIBxId.setStyle(ERROR_STYLE);
			return false;
			}
			}
		}*/
		productlpIBxId.setStyle(NORMAL_STYLE);
		if( discountTypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("loyaltyRewards") && productlpIBxId.getValue().isEmpty() ){
			MessageUtil.setMessage("Please provide loyalty points/rewards.","color:red","TOP");
			productlpIBxId.setStyle(ERROR_STYLE);
			return false;
		}
		else{
			if(productlpIBxId.getValue() != null && !productlpIBxId.getValue().isEmpty()){
				double checkValue=0;
				if(productvalueCode2ListboxId.getSelectedItem().getValue().equals("Multiplier")) {
					try {
						checkValue = Double.parseDouble( productlpIBxId.getValue().toString().trim());
					}catch(Exception e) {
						MessageUtil.setMessage("Please provide valid loyalty points/rewards.","color:red","TOP");
						productlpIBxId.setStyle(ERROR_STYLE);
						return false;
					}
				}
				else if(productvalueCode2ListboxId.getSelectedItem().getValue().equals("Value")) {
					try {
						checkValue = Integer.parseInt( productlpIBxId.getValue().toString().trim());
					}catch(Exception e) {
						MessageUtil.setMessage("Please provide valid loyalty points/rewards.","color:red","TOP");
						productlpIBxId.setStyle(ERROR_STYLE);
						return false;
					}
				}
					
			if(checkValue <= 0){
			MessageUtil.setMessage("Please provide valid loyalty points/rewards.","color:red","TOP");
			productlpIBxId.setStyle(ERROR_STYLE);
			return false;
			}
			}
		}
		quantityBxId.setStyle(NORMAL_STYLE);
		if( discountTypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("purchases") && quantityBxId.getValue() == null ){
			MessageUtil.setMessage("Please provide quantity.","color:red","TOP");
			quantityBxId.setStyle(ERROR_STYLE);
			return false;
		}
		else{
			if(quantityBxId.getValue() != null){
			double checkValue = Double.parseDouble(quantityBxId.getValue().toString().trim());
			if(checkValue <= 0){
			MessageUtil.setMessage("Please provide valid quantity.","color:red","TOP");
			quantityBxId.setStyle(ERROR_STYLE);
			return false;
			}
			}
		}


		/*if (discountGenRowsId.getChildren().size() == 0) {
			MessageUtil.setMessage("Please provide valid discount value.", "color:red", "TOP");
			return false;
		}*/

//		if(promofromDatevalidation() == false) return false;

		return true;
	} // isVaildCoupon

	private Coupons saveNewCoupObj() {

		String couponStatus = "Draft";
		Calendar tempCal = null;
		Calendar expCal1 = null;

		// String couponGeneratedTypeStr = null;
		String couponGeneratedTypeStr = "Draft";

		String couponCodeStr = null;
		long totalQtyLng = 0;
		long minTotQty = 0;
		long redemedLong = 0;
		boolean autoCheckFlag = false;
		boolean redmdAutChkFlag = false;
		
				
			
		
		//byshailika
		 /*if(coupGenRadioGrId.getSelectedIndex() == 0) {
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
		 }*/

		/*String discTypeStr = "";

		if (percentRadioId.isChecked())
			discTypeStr = DISC_TYPE_PERCENTAGE;
		else
			discTypeStr = DISC_TYPE_VALUE;*/

		// New Coupon Obj
		Coupons couponObj = new Coupons(couponNameTxtBxId.getValue().trim(), coupDescTxtBxId.getValue().trim(),
				couponStatus, couponGeneratedTypeStr, couponCodeStr, discountTypeStr,
				discountRadioGrId.getSelectedItem().getValue().toString(), totalQtyLng, users.getUserId(),
				Calendar.getInstance(), tempCal, Calendar.getInstance(), expCal1, autoCheckFlag, redemedLong,
				redmdAutChkFlag);
		couponObj.setOrgId(orgId);
		//couponObj.setCouponGeneratedType(couponGeneratedTypeStr);
		
		if(discountTypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("coupons")){
			couponObj.setCouponGeneratedType(Constants.COUP_GENT_TYPE_MULTIPLE);
		}else {
			couponObj.setCouponGeneratedType(Constants.COUP_GENT_TYPE_SINGLE);
		}
	


		/**
		 * New API APP-721
		 */

		if(skuRadioId.isChecked()){
			
			/*if(productonlylpChkBxId.isChecked()){
				
				couponObj.setLoyaltyPoints((byte)1);
				couponObj.setRequiredLoyltyPoits(productlpIBxId.getValue());
				couponObj.setValueCode(productvalueCodeListboxId.getSelectedItem() !=null ? 
						productvalueCodeListboxId.getSelectedItem().getValue():null);
			}else if(fbrId.isChecked() && quantityBxId!=null){
				
				couponObj.setLoyaltyPoints((byte)1);
				couponObj.setPurchaseQty(quantityBxId.getValue());
			}*/
			
			if(productlpIBxId.isVisible() && productlpIBxId!=null && productlpIBxId.getValue()!=null && !productlpIBxId.getValue().isEmpty()){
				
				couponObj.setLoyaltyPoints((byte)1);
				couponObj.setRequiredLoyltyPoits(Integer.parseInt(productlpIBxId.getValue()));
				couponObj.setValueCode(productvalueCodeListboxId.getSelectedItem() !=null ? 
						productvalueCodeListboxId.getSelectedItem().getValue():null);
			}else if(quantityBxId.isVisible() && quantityBxId!=null && quantityBxId.getValue()!=null){
				
				couponObj.setLoyaltyPoints((byte)1);
				couponObj.setPurchaseQty(quantityBxId.getValue());
			}
		}else if(tpaRadioId.isChecked()){

			//if(onlylpChkBxId.isChecked()){
			if(discountTypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("loyaltyRewards")) {
				couponObj.setLoyaltyPoints((byte)1);
				if(productvalueCode2ListboxId.getSelectedItem().getValue().toString().equalsIgnoreCase("Multiplier")){
					couponObj.setMultiplierValue(Double.valueOf(productlpIBxId.getText()));
					couponObj.setRequiredLoyltyPoits(null);
				}else {
					couponObj.setRequiredLoyltyPoits(Integer.parseInt(productlpIBxId.getValue()));
					couponObj.setMultiplierValue(null);
				}
				//TODO check with madam for this
				//if(isNewPlugin)
					couponObj.setValueCode(productvalueCodeListboxId.getSelectedItem() !=null ? 
							productvalueCodeListboxId.getSelectedItem().getValue():null);
			}
		}
		//*******end
		
			couponObj.setUseasReferralCode(userefcodeChkBxId.isChecked());
			couponObj.setExcludeItems(discountedItemsCoupChkBxId.isChecked());
			couponObj.setAccumulateOtherPromotion(accumulateCoupChkBxId.isChecked());
			//couponObj.setCombineItemAttributes(oneCombinationId.isChecked());
			couponObj.setCombineItemAttributes(ruleType.equalsIgnoreCase(combined) ||
					discountTypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("purchases")?true:false);


			/*if (skuRadioId.isChecked() && (eligibleItemRadioId != null && eligibleItemRadioId.getSelectedItem() != null
					&& (!eligibleItemRadioId.getSelectedItem().getValue().toString()
							.equals(Constants.HIGHEST_PRICED_ITEM_WITH_DISCOUNT)
							|| !eligibleItemRadioId.getSelectedItem().getValue().toString()
									.equals(Constants.HIGHEST_PRICED_ITEM_WITH_OUT_DISCOUNT)
							|| !eligibleItemRadioId.getSelectedItem().getValue().toString()
									.equals(Constants.ALL_ELIGIBLE_ITEMS)))) {*/
			/*if (skuRadioId.isChecked() && eligibleItems!=null && !eligibleItems.isEmpty()) {
				//couponObj.setNoOfEligibleItems(eligibleItemRadioId.getSelectedItem().getValue().toString());
				couponObj.setNoOfEligibleItems(eligibleItems);
				
			}*/
			//byshailika
			/*if(coupGenRadioGrId.getSelectedIndex() == 0) {
				// set Item per Subscrition
				if(unlimitSubChkBxId.isChecked()){
					couponObj.setSingPromoContUnlimitedRedmptChk(unlimitSubChkBxId.isChecked());
				}else {
					couponObj.setSingPromoContRedmptLimit(perSubUselimitLongbxId.getValue());
				}
			}*/
			
			/* String couponGeneratedTypeStr = "";
			 String couponCodeStr = null;
			 long totalQtyLng = 0;
			 long redemedLong = 0;
			 boolean autoCheckFlag = false;
			 boolean redmdAutChkFlag = false;
			 */
			 
			 
			
		//}

		// Draft not D or S

		couponObj.setExpiryType("Draft");
		couponObj.setEnableBarcode(false);
		if(couponObj.getPurchaseQty() != null){
			 
			 CreateFrequentBuyerProgram createFrequentBuyerProgram = new CreateFrequentBuyerProgram(couponObj, true);
			 couponObj = createFrequentBuyerProgram.setRequiredRewardSetup();
		 }
		//logger.info("couponRuleStr "+couponRuleStr);
		//couponObj.setCouponRule(couponRuleStr);
		couponsDaoForDML.saveOrUpdate(couponObj);
		logger.info("couponObj.getCouponId()   " + couponObj.getCouponId());
		// coupDiscGenDaoForDML.insertNewIntoCouponDiscountby(couponObj.getUserId(),
		// couponObj.getCouponId());
		coupDiscGenDaoForDML.insertNewIntoCouponDiscountby_New(couponObj.getUserId(), couponObj.getCouponId());
		//****algorithmic desc ****
		CouponDescriptionAlgorithm coupoDes = new CouponDescriptionAlgorithm();
		String desc = coupoDes.preparecouponDisc(couponObj, users);
		couponObj.setTempDesc(desc);
		//***end****
		coupDiscGenDaoForDML.deleteAllSkuBy(couponObj.getUserId(), null);
		return couponObj;
	} // saveNewCoupObj

	private void saveEditCoupObj(Coupons editCouponObj) {
		boolean isModified = false;
		// check and set the Description
		if (!editCouponObj.getCouponDescription().equals(coupDescTxtBxId.getValue().trim())) {
			isModified = true;
			editCouponObj.setCouponDescription(coupDescTxtBxId.getValue().trim());
		}
		if(skuRadioId.isChecked()){
		
		//set Redeemed with
		//	if(editCouponObj.getLoyaltyPoints() == null  && productonlylpChkBxId.isChecked() && productlpIBxId.getValue()!=null) {
			
		/*if(editCouponObj.getLoyaltyPoints() == null  && productonlylpChkBxId.isChecked() && productlpIBxId.getValue()!=null) {
			isModified = true;
			editCouponObj.setRequiredLoyltyPoits(productlpIBxId.getValue());
			editCouponObj.setLoyaltyPoints((byte)1);
			
		}
		else if(editCouponObj.getLoyaltyPoints() !=null && editCouponObj.getRequiredLoyltyPoits()!=null && productlpIBxId.getValue() !=null && 
				productlpIBxId.getValue() !=  editCouponObj.getRequiredLoyltyPoits() .longValue()) {
			isModified = true;
			editCouponObj.setRequiredLoyltyPoits(productlpIBxId.getValue());
			editCouponObj.setLoyaltyPoints((byte)1);
		}else if(editCouponObj.getLoyaltyPoints() != null && !productonlylpChkBxId.isChecked() && editCouponObj.getPurchaseQty()==null && editCouponObj.getSpecialRewadId()==null){
			isModified = true;
			editCouponObj.setRequiredLoyltyPoits(null);
			editCouponObj.setLoyaltyPoints(null);
		}*/
			
			if(editCouponObj.getLoyaltyPoints() == null  && productlpIBxId.getValue()!=null && !productlpIBxId.getValue().isEmpty()) {
				isModified = true;
				editCouponObj.setRequiredLoyltyPoits(Integer.parseInt(productlpIBxId.getValue()));
				editCouponObj.setLoyaltyPoints((byte)1);
				
			}
			else if(editCouponObj.getLoyaltyPoints() !=null && editCouponObj.getRequiredLoyltyPoits()!=null && productlpIBxId.getValue() !=null && !productlpIBxId.getValue().isEmpty() &&
					Integer.parseInt(productlpIBxId.getValue()) !=  editCouponObj.getRequiredLoyltyPoits() .longValue()) {
				isModified = true;
				editCouponObj.setRequiredLoyltyPoits(Integer.parseInt(productlpIBxId.getValue()));
				editCouponObj.setLoyaltyPoints((byte)1);
			}/*else if(editCouponObj.getLoyaltyPoints() != null && productlpIBxId.getValue()!=null && editCouponObj.getPurchaseQty()==null && editCouponObj.getSpecialRewadId()==null){
				isModified = true;
				editCouponObj.setRequiredLoyltyPoits(null);
				editCouponObj.setLoyaltyPoints(null);
			}*/
			
			else if(editCouponObj.getLoyaltyPoints() != null && (productlpIBxId.getValue()==null || productlpIBxId.getValue().isEmpty()) && editCouponObj.getPurchaseQty()==null && editCouponObj.getSpecialRewadId()==null){
				isModified = true;
				editCouponObj.setRequiredLoyltyPoits(null);
				editCouponObj.setLoyaltyPoints(null);
			}
		
		/*if(editCouponObj.getPurchaseQty() == null && fbrId.isChecked() && quantityBxId.getValue()!=null){
			isModified=true;
			editCouponObj.setLoyaltyPoints((byte)1);
			editCouponObj.setPurchaseQty(quantityBxId.getValue());
		}else if(editCouponObj.getPurchaseQty()!=null && fbrId.isChecked() && editCouponObj.getPurchaseQty()!=quantityBxId.getValue()){
			isModified=true;
			editCouponObj.setLoyaltyPoints((byte)1);
			editCouponObj.setPurchaseQty(quantityBxId.getValue());
		}else if(editCouponObj.getPurchaseQty() != null && !fbrId.isChecked() && !productonlylpChkBxId.isChecked()){
			isModified = true;
			editCouponObj.setLoyaltyPoints(null);
			editCouponObj.setPurchaseQty(null);
		}*/
		
		if(editCouponObj.getPurchaseQty() == null && quantityBxId.getValue()!=null){
			isModified=true;
			editCouponObj.setLoyaltyPoints((byte)1);
			editCouponObj.setPurchaseQty(quantityBxId.getValue());
		}else if(editCouponObj.getPurchaseQty()!=null && editCouponObj.getPurchaseQty()!=quantityBxId.getValue()){
			isModified=true;
			editCouponObj.setLoyaltyPoints((byte)1);
			editCouponObj.setPurchaseQty(quantityBxId.getValue());
		}else if(editCouponObj.getPurchaseQty() != null && quantityBxId.getValue()==null){
			isModified = true;
			editCouponObj.setLoyaltyPoints(null);
			editCouponObj.setPurchaseQty(null);
		}
		
		
		if(editCouponObj.getValueCode() == null && !productvalueCodeListboxId.getSelectedItem().getLabel().equalsIgnoreCase("Points") 
				&& productlpIBxId.getValue()!=null && !productlpIBxId.getValue().isEmpty() && editCouponObj.getPurchaseQty()==null && editCouponObj.getSpecialRewadId()==null) {
			isModified = true;
			editCouponObj.setValueCode(productvalueCodeListboxId.getSelectedItem()!=null?productvalueCodeListboxId.getSelectedItem().getValue():null);
		}
		else if(editCouponObj.getValueCode() !=null && productvalueCodeListboxId.getSelectedItem()!=null && 
				productvalueCodeListboxId.getSelectedItem().getValue() !=  editCouponObj.getValueCode() && editCouponObj.getPurchaseQty()==null && editCouponObj.getSpecialRewadId()==null) {
			isModified = true;
			editCouponObj.setValueCode(productvalueCodeListboxId.getSelectedItem()!=null?productvalueCodeListboxId.getSelectedItem().getValue():null);
		}else if(editCouponObj.getValueCode() != null && (productlpIBxId.getValue()==null || productlpIBxId.getValue().isEmpty()) && editCouponObj.getPurchaseQty()==null && editCouponObj.getSpecialRewadId()==null){
			isModified = true;
			editCouponObj.setValueCode(null);
		}
		
		}else if(tpaRadioId.isChecked()){
			//set Redeemed with
			if(editCouponObj.getLoyaltyPoints() == null  && discountTypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("loyaltyRewards") ) {
				isModified = true;
				if(productvalueCode2ListboxId.getSelectedItem().getValue().toString().equalsIgnoreCase("Multiplier")){
					editCouponObj.setMultiplierValue(Double.valueOf(productlpIBxId.getValue()));
					editCouponObj.setRequiredLoyltyPoits(null);
				}else {
					editCouponObj.setMultiplierValue(null);
					editCouponObj.setRequiredLoyltyPoits(Integer.parseInt(productlpIBxId.getValue()));
				}
				editCouponObj.setLoyaltyPoints((byte)1);
				
			}
			else if(editCouponObj.getLoyaltyPoints() !=null && productlpIBxId.getValue() !=null && !productlpIBxId.getValue().isEmpty() &&
					((editCouponObj.getRequiredLoyltyPoits()!=null && Double.parseDouble(productlpIBxId.getValue()) !=  editCouponObj.getRequiredLoyltyPoits().doubleValue()) ||
					(editCouponObj.getMultiplierValue()!=null && Double.parseDouble(productlpIBxId.getValue()) !=  editCouponObj.getMultiplierValue()))) {
				isModified = true;
				if(productvalueCode2ListboxId.getSelectedItem().getValue().toString().equalsIgnoreCase("Multiplier")){
					editCouponObj.setMultiplierValue(Double.valueOf(productlpIBxId.getValue()));
					editCouponObj.setRequiredLoyltyPoits(null);
				}else {
					editCouponObj.setMultiplierValue(null);
					editCouponObj.setRequiredLoyltyPoits(Integer.parseInt(productlpIBxId.getValue()));
				}
				editCouponObj.setLoyaltyPoints((byte)1);
			}else if(editCouponObj.getLoyaltyPoints() != null && !discountTypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("loyaltyRewards")){
				isModified = true;
				editCouponObj.setMultiplierValue(null);
				editCouponObj.setRequiredLoyltyPoits(null);
				editCouponObj.setLoyaltyPoints(null);
			}
			
			
			//set Redeemed with
			//if(isNewPlugin) {
				if(editCouponObj.getValueCode() == null  && discountTypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("loyaltyRewards") ) {
					isModified = true;
					editCouponObj.setValueCode(productvalueCodeListboxId.getSelectedItem()!=null?productvalueCodeListboxId.getSelectedItem().getValue():null);
				}
				else if(editCouponObj.getValueCode() !=null && productvalueCodeListboxId.getSelectedItem()!=null && 
						productvalueCodeListboxId.getSelectedItem().getValue() !=  editCouponObj.getValueCode()) {
					isModified = true;
					editCouponObj.setValueCode(productvalueCodeListboxId.getSelectedItem()!=null?productvalueCodeListboxId.getSelectedItem().getValue():null);
				}else if(editCouponObj.getValueCode() != null && !discountTypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("loyaltyRewards")){
					isModified = true;
					editCouponObj.setValueCode(null);
				}
			//}
			
		}
		
		//set Redeemed with
		//if(isNewPlugin) {
			
		//}
		
		
		logger.info("edit coupon object;;;;" + editCouponObj.getEnableBarcode());

		// Discount Criteria
		/*if (!(editCouponObj.getDiscountType().trim().equals(DISC_TYPE_PERCENTAGE) && percentRadioId.isChecked())) {
			String discountTypeStr = "";
			if (percentRadioId.isChecked())
				discountTypeStr = DISC_TYPE_PERCENTAGE;
			else
				discountTypeStr = DISC_TYPE_VALUE;

			editCouponObj.setDiscountType(discountTypeStr);
			isModified = true;
		}*/
		if (editCouponObj.getDiscountType().trim().equals(DISC_TYPE_PERCENTAGE) && discountTypeStr.equals(DISC_TYPE_PERCENTAGE)) {
				discountTypeStr = DISC_TYPE_PERCENTAGE;
				editCouponObj.setDiscountType(discountTypeStr);
				isModified = true;
		}
		else {
			discountTypeStr = DISC_TYPE_VALUE;
			editCouponObj.setDiscountType(discountTypeStr);
			isModified = true;
		}

		if (!editCouponObj.getDiscountCriteria().trim()
				.equals(discountRadioGrId.getSelectedItem().getValue().toString())) {
			editCouponObj.setDiscountCriteria(discountRadioGrId.getSelectedItem().getValue().toString());
			isModified = true;
		}

		if (!editCouponObj.getDiscountCriteria().trim()
				.equals(discountRadioGrId.getSelectedItem().getValue().toString())) {
			editCouponObj.setDiscountCriteria(discountRadioGrId.getSelectedItem().getValue().toString());
			isModified = true;
		}

		
			editCouponObj.setUseasReferralCode(userefcodeChkBxId.isChecked());
			editCouponObj.setExcludeItems(discountedItemsCoupChkBxId.isChecked());
			editCouponObj.setAccumulateOtherPromotion(accumulateCoupChkBxId.isChecked());
			//editCouponObj.setCombineItemAttributes(oneCombinationId.isChecked());
			editCouponObj.setCombineItemAttributes(ruleType.equalsIgnoreCase(combined)||
					discountTypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("purchases")?true:false);
			isModified = true;

			if (skuRadioId.isChecked() && (eligibleItemRadioId != null && eligibleItemRadioId.getSelectedItem() != null
					&& (!eligibleItemRadioId.getSelectedItem().getValue().toString()
							.equals(Constants.HIGHEST_PRICED_ITEM_WITH_DISCOUNT)
							|| !eligibleItemRadioId.getSelectedItem().getValue().toString()
									.equals(Constants.HIGHEST_PRICED_ITEM_WITH_OUT_DISCOUNT)
							|| !eligibleItemRadioId.getSelectedItem().getValue().toString()
									.equals(Constants.ALL_ELIGIBLE_ITEMS)))) {
				
				//editCouponObj.setNoOfEligibleItems(eligibleItemRadioId.getSelectedItem().getValue().toString());
				/*editCouponObj.setNoOfEligibleItems(
						eligibleItemRadioId.getSelectedIndex() == 0 ? Constants.ALL_ELIGIBLE_ITEMS
								: eligibleItemRadioId.getSelectedIndex() == 1
										? Constants.HIGHEST_PRICED_ITEM_WITH_DISCOUNT
										: Constants.HIGHEST_PRICED_ITEM_WITH_OUT_DISCOUNT);*/
			}
			
			
			//byshailika
			/*//discount code generation
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
				
			}else if(editCouponObj.getCouponGeneratedType().trim().equals(Constants.COUP_GENT_TYPE_SINGLE) && 
							coupGenRadioGrId.getSelectedIndex() == 1) {
				//	logger.debug("Both are not  Same --- ----");
				editCouponObj.setCouponCode(null);
				editCouponObj.setTotalQty(multiCodLimitLongBxId.getValue());
				editCouponObj.setAutoIncrCheck(multiCoupChkBxId.isChecked());
				editCouponObj.setCouponGeneratedType(Constants.COUP_GENT_TYPE_MULTIPLE);
				isModified = true;
				
			}else if(editCouponObj.getCouponGeneratedType().trim().equals(Constants.COUP_GENT_TYPE_MULTIPLE) && 
										coupGenRadioGrId.getSelectedIndex() == 0) {
				//	logger.debug("Both are not  Same --- ----");
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
				//	logger.debug("Both are not  Same -------");
				if(!editCouponObj.getTotalQty().equals(multiCodLimitLongBxId.getValue())) {
					editCouponObj.setTotalQty(multiCodLimitLongBxId.getValue());
					isModified = true;
				}
				if(!editCouponObj.getAutoIncrCheck() == multiCoupChkBxId.isChecked()){
					editCouponObj.setAutoIncrCheck(multiCoupChkBxId.isChecked());
					isModified = true;
				}
				
			}*/
			
		//}

		// Validity period
		// String editCoupStatu= "";

		if (isModified) {

			// We can set it second screen
			// editCouponObj.setEnableBarcode(false);

			// set the Status
			// editCouponObj.setStatus(editCoupStatu);

			// set modified user
			editCouponObj.setLastModifiedUser(users.getUserName());

			// set modified user Date
			editCouponObj.setUserLastModifiedDate(Calendar.getInstance());

			// couponsDao.saveOrUpdate(editCouponObj);
			couponsDaoForDML.saveOrUpdate(editCouponObj);
		}
		coupDiscGenDaoForDML.deleteDiscountGenByCouponId(editCouponObj.getCouponId());
		// coupDiscGenDaoForDML.insertIntoCouponDiscountby_New(editCouponObj.getUserId(),
		// editCouponObj.getCouponId());
		coupDiscGenDaoForDML.insertIntoCouponDiscountby_New(editCouponObj.getUserId(), editCouponObj.getCouponId());
		//****algorithmic desc ****
		CouponDescriptionAlgorithm coupoDes = new CouponDescriptionAlgorithm();
		String desc = coupoDes.preparecouponDisc(editCouponObj, users);
		
			
			/*if(oldDesc.equals(desc) && 
					editCouponObj.getCouponDescription() !=null && !editCouponObj.getCouponDescription().isEmpty() && 
					!editCouponObj.getCouponDescription().equals(desc)){
				
				editCouponObj.setTempDesc(editCouponObj.getCouponDescription());
			}else{
				
				editCouponObj.setTempDesc(desc);
			}*/

		if(editCouponObj.getCouponDescription()==null || editCouponObj.getCouponDescription().isEmpty()){
			editCouponObj.setTempDesc(desc);
		}else if(!editCouponObj.getCouponDescription().equals(desc)) {
			editCouponObj.setTempDesc(desc);
		}
			
		/*if(oldDesc!=null){
			if(!oldDesc.equals(desc)){
				editCouponObj.setTempDesc(desc);
			}else if(editCouponObj.getCouponDescription()==null || editCouponObj.getCouponDescription().isEmpty()){
				editCouponObj.setTempDesc(desc);
			}else{
				editCouponObj.setTempDesc(tempDesc);
			}
		}*/

		
		
		//editCouponObj.setTempDesc(desc);
		//end
		coupDiscGenDaoForDML.deleteAllSkuByCouponId(editCouponObj.getCouponId());
	}// saveEditCoupObj

	public void onCheck$discountedItemsCoupChkBxId() {

		//discountDivId.setVisible(false);
		// System.out.println("discountedItemsCoupChkBxId.isChecked()
		// :::"+discountedItemsCoupChkBxId.isChecked());
		if (discountedItemsCoupChkBxId != null && discountedItemsCoupChkBxId.isChecked()) {
			//discountDivId.setVisible(false);
		} else {
			//discountDivId.setVisible(true);
			discountedItemsCoupChkBxId.setChecked(false);
		}

	}

	Map<String, List<SkuFile>> listOfDisItemSidMap = new HashMap<String, List<SkuFile>>();
	Map<String, Long> listOfDisTotPurMap = new HashMap<String, Long>();
	Map<String, List> listOfItemSidOrTotPurMap = new HashMap<String, List>();
//	 Map<String, List<Long>> listOfDisTotPurMap = new HashMap<String, List<Long>>();

	private void enableDisableDiscountCrtiteriaOptions() {

		List rowList = discountGenRowsId.getChildren();

		if (rowList.size() >= 1) {
			skuRadioId.setDisabled(true);
			tpaRadioId.setDisabled(true);
			percentRadioId.setDisabled(true);
			dollerRadioId.setDisabled(true);
//			discountfirstListBxId.setDisabled(true);
		} else {
			skuRadioId.setDisabled(false);
			tpaRadioId.setDisabled(false);
			percentRadioId.setDisabled(false);
			dollerRadioId.setDisabled(false);
//			discountfirstListBxId.setDisabled(false);
		}

		//setheaders();
	} //

	private Hbox loyatyPntshboxId,productloyatyPntshboxId;

//	 loyaltyHbxId;
//	 private Checkbox onlylpChkBxId;
	public void onCheck$onlylpChkBxId() {
		if (onlylpChkBxId.isChecked()) {
			loyatyPntshboxId.setVisible(true);
			productvalueCodeListboxId.setVisible(true);
		} else {
			loyatyPntshboxId.setVisible(false);
			productvalueCodeListboxId.setVisible(false);
		}
	} // onCheck$lpChkBxId

	private void setDefaultDimensions() {

		Components.removeAllChildren(bcDimensionListboxId2);
		if (bctypeListboxId.getSelectedItem().getValue().equals(Constants.COUP_BARCODE_DATAMATRIX)) {
			for (int i = 0; i < DM_Dimension.length; i++) {
				Listitem tempItem = generateLItem(DM_Dimension[i]);
				tempItem.setParent(bcDimensionListboxId2);
			}
			bcDimensionListboxId2.setSelectedIndex(0);
		} else if (bctypeListboxId.getSelectedItem().getValue().equals(Constants.COUP_BARCODE_AZTEC)) {
			for (int i = 0; i < AZ_Dimension.length; i++) {
				Listitem tempItem = generateLItem(AZ_Dimension[i]);
				tempItem.setParent(bcDimensionListboxId2);
			}
			bcDimensionListboxId2.setSelectedIndex(0);
		} else if (bctypeListboxId.getSelectedItem().getValue().equals(Constants.COUP_BARCODE_QR)) {
			for (int i = 0; i < QR_Dimension.length; i++) {
				Listitem tempItem = generateLItem(QR_Dimension[i]);
				tempItem.setParent(bcDimensionListboxId2);
			}
			bcDimensionListboxId2.setSelectedIndex(0);
		} else {
			for (int i = 0; i < LN_Dimension.length; i++) {
				Listitem tempItem = generateLItem(LN_Dimension[i]);
				tempItem.setParent(bcDimensionListboxId2);
			}
			bcDimensionListboxId2.setSelectedIndex(LN_Dimension.length - 1);
		}
		// bcDimensionListboxId2.setSelectedIndex(0);
	} // setDefaultDimensions

	private Listitem generateLItem(String itemValStr) {
		String strLabel = itemValStr.replace("X", " X ");
		Listitem litem = new Listitem(strLabel, itemValStr);
		return litem;
	} // generateLItem

	private Combobox storeNumbCmboBxId;
	private Listbox selectedStoreLbId;

	private void setDeafultStore() {
		organizationStoresDao = (OrganizationStoresDao) SpringUtil.getBean("organizationStoresDao");
		List<OrganizationStores> storeIdList = organizationStoresDao
				.findAllStores(users.getUserOrganization().getUserOrgId());
		// logger.debug(">>>> "+storeIdList.size());
		if (storeIdList == null || storeIdList.size() == 0)
			return;
		Comboitem comboItem = null;
		for (OrganizationStores eachStoe : storeIdList) {
			comboItem = new Comboitem();
			// logger.debug("achStoe.getHomeStoreId() VALUE IS >>>>
			// "+eachStoe.getHomeStoreId());
			comboItem.setLabel(eachStoe.getHomeStoreId());
			comboItem.setParent(storeNumbCmboBxId);

		}

	}

	public void onClick$addStoreImgId() {
		// logger.debug(">>>> add store click here >>>");
		// logger.debug( " selected Item values are ::::
		// "+storeNumbCmboBxId.getValue());
		if (storeNumbCmboBxId.getValue() == null || storeNumbCmboBxId.getValue().trim().length() == 0) {
			MessageUtil.setMessage("Please provide store number.", "red", "TOP");
			return;
		}

		Listitem tempItem = null;
		Listcell tempCell = null;
		if (selectedStoreLbId.getItemCount() == 0) {
			tempItem = new Listitem();
			tempCell = new Listcell("Store Name " + storeNumbCmboBxId.getValue().trim());
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

		List<Listitem> listItem = selectedStoreLbId.getItems();
		boolean existedStore = false;
		for (Listitem eachItem : listItem) {
			if (eachItem.getLabel().equals("Store Name " + storeNumbCmboBxId.getValue().trim())) {
				existedStore = true;
				break;
			}

		}

		if (!existedStore) {
			tempItem = new Listitem();
			tempCell = new Listcell("Store Name " + storeNumbCmboBxId.getValue().trim());
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


	Checkbox mpvChkBoxId;

	public void onCheck$mpvChkBoxId() {

		if (!mpvChkBoxId.isChecked())
			totPurAmtLngBxId.setValue(null);
		totPurAmtLngBxId.setDisabled(!mpvChkBoxId.isChecked());
	}

	public void export(Coupons couponObj) {

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
			sqlqry = "select coupon_code, status from coupon_codes where coupon_id=" + couponObj.getCouponId();
			jdbcResultsetHandler.executeStmt(sqlqry);
			int size = jdbcResultsetHandler.totalRecordsSize();
			if (size == 0) {
				MessageUtil.setMessage("No Discount code exists.", "color:red", "TOP");
				return;
			}
			if (couponObj.getCouponGeneratedType().equalsIgnoreCase("Multiple") && couponObj.getTotalQty() != size) {
				MessageUtil.setMessage("Discount codes generation is still in process. Please try again after a while.",
						"RED", "TOP");
				return;
			}

			userName = GetUser.getUserName();
			usersParentDirectory = (String) PropertyUtil.getPropertyValue("usersParentDirectory");
			exportDir = usersParentDirectory + "/" + userName + "/Export/";
			downloadDir = new File(exportDir);

			if (downloadDir.exists()) {
				try {
					FileUtils.deleteDirectory(downloadDir);
					logger.debug(downloadDir.getName() + " is deleted");
				} catch (Exception e) {
					logger.error("Exception ::", e);
					logger.debug(downloadDir.getName() + " is not deleted");
				}
			}
			if (!downloadDir.exists()) {
				downloadDir.mkdirs();
			}

			name = couponObj.getCouponName();
			if (name.contains("/")) {
				name = name.replace("/", "_");
			}
			filePath = exportDir + "Report_" + name + "_"
					+ MyCalendar.calendarToString(Calendar.getInstance(), MyCalendar.FORMAT_YEARTOSEC, clientTimeZone);

			filePath = filePath + "_promos.csv";
			logger.debug("Download File path : " + filePath);
			file = new File(filePath);
			bw = new BufferedWriter(new FileWriter(filePath));
			bw.write("\"Promo Code\",\"Status\",\r\n");

			OCCSVWriter csvWriter = new OCCSVWriter(bw);
			try {
				csvWriter = new OCCSVWriter(bw);
				csvWriter.writeAll(jdbcResultsetHandler.getResultSet(), false);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("exception while initiating writer ", e);

			} finally {
				bw.flush();
				csvWriter.flush();
				bw.close();
				csvWriter.close();
				csvWriter = null;

			}

			Filedownload.save(file, "text/csv");
			file = null;

		} catch (Exception e) {
			logger.error("Error  :: ", e);
		} finally {
			if (jdbcResultsetHandler != null)
				jdbcResultsetHandler.destroy();
			// making all resources ready for GC.
			couponObj = null;
			sqlqry = null;
			userName = null;
			usersParentDirectory = null;
			exportDir = null;
			downloadDir = null;
			name = null;
			filePath = null;
			bw = null;
			jdbcResultsetHandler = null;
			file = null;
			// System.gc();
		}

		long endTime = System.currentTimeMillis();
		logger.fatal("Time taken to export coupons is :::   :: " + (endTime - startTime));
	}

	
	private Calendar setDefaultTimeforCal(Calendar tempCal, boolean isStart) {
		if (isStart) {

			tempCal.set(Calendar.HOUR_OF_DAY, 00);
			tempCal.set(Calendar.MINUTE, 00);
			tempCal.set(Calendar.SECOND, 00);
			tempCal.set(Calendar.MILLISECOND, 00);
		} else {
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
			Listcell lc = null;
			Listitem li = null;
			logger.info(" Event Name:" + event.getName());
			if (event.getTarget() instanceof Image) {
				Image tempImg = (Image) event.getTarget();
				if(tempImg.getId().contains("udfFilterImage")){
					Listbox lb=(Listbox) tempImg.getAttribute("listboxName");//, listbox);
					 String column=(String) tempImg.getAttribute("column");//, (String) li.getAttribute("column"));
					 
					 if(lb!=null && column!=null){
						 onClick$udfFilterImage(lb,column);
					 }
				}
				if (tempImg.getParent() instanceof Div) {

					tempDiv = (Div) tempImg.getParent();
				}
				logger.info("COUPON_TYPE============="+tempImg.getAttribute("COUPON_TYPE"));

//			List chldLst = tempDiv.getChildren();"COUPON_TYPE", COUPON_DISCOUNT_DELETE)//listOfDisItemSidMap,listOfDisTotPurMap
				if (tempImg.getAttribute("COUPON_TYPE") != null
						&& tempImg.getAttribute("COUPON_TYPE").equals(PRODUCT_COUPON_DELETE)) {

					//tempRow = (Row) tempImg.getParent().getParent();
					tempRow = (Row) tempImg.getParent();

					Label keyLabel = (Label) tempRow.getChildren().get(0);
					Label typeLabel = (Label) tempRow.getChildren().get(1);
					String limit = tempRow.getValue();
					// List<CouponDiscountGeneration> deleteDiscountList=new ArrayList<>();
					if (skuRadioId.isChecked()) {
						String type = (String) typeLabel.getAttribute("Type");
						Label valueLabel = (Label) tempRow.getChildren().get(2);
						
						if(ruleType.equalsIgnoreCase(multipleSingle)){
							if (valueLabel.getValue().equals("--All Items--"))
								coupDiscGenDaoForDML.deleteTempSkuBy(users.getUserId(), type, keyLabel.getValue(), null,
										tempCouponObj != null ? tempCouponObj.getCouponId() : null);
							else
								coupDiscGenDaoForDML.deleteTempSkuBy(users.getUserId(), type, keyLabel.getValue(), limit,
										tempCouponObj != null ? tempCouponObj.getCouponId() : null);
						}//else if(oneCombinationId.isChecked()){
						else if(ruleType.equalsIgnoreCase(combined)){
							if(type!=null){
							if(type.contains(",")){
								String[] typeArr = type.split(",");
								for(String typeONE : typeArr){
									coupDiscGenDaoForDML.deleteTempSkuBy(users.getUserId(), typeONE.trim(), keyLabel.getValue(), limit,
											tempCouponObj != null ? tempCouponObj.getCouponId() : null);
								}
							}else{
								coupDiscGenDaoForDML.deleteTempSkuBy(users.getUserId(), type.trim(), keyLabel.getValue(), limit,
										tempCouponObj != null ? tempCouponObj.getCouponId() : null);
							}
							}
							
						}
						
						
					} else {
						coupDiscGenDaoForDML.deleteTempSkuBy(users.getUserId(), keyLabel.getValue(),
								typeLabel.getValue().equals("N/A") ? "0" : typeLabel.getValue(), null);
						
					}

					
					discountGenRowsId.removeChild(tempRow);

					enableDisableDiscountCrtiteriaOptions();
					// selectAllSKUBtnId.setAttribute("selectedAll", false);
					deSelectSkuMap.clear();
				}else if(tempImg.getAttribute("COUPON_TYPE") != null
						&& tempImg.getAttribute("COUPON_TYPE").equals(COUPON_RULE_DELETE)) {


					tempRow = (Row) tempImg.getParent().getParent();
					//tempRow = (Row) tempImg.getParent();

					Label keyLabel = (Label) tempRow.getChildren().get(0);
					Label typeLabel = (Label) tempRow.getChildren().get(1);
					String limit = tempRow.getValue();
					// List<CouponDiscountGeneration> deleteDiscountList=new ArrayList<>();
					if (skuRadioId.isChecked()) {
						String type = (String) typeLabel.getAttribute("Type");
						Label valueLabel = (Label) tempRow.getChildren().get(2);
						
						if(ruleType.equalsIgnoreCase(multipleSingle)){
							if (valueLabel.getValue().equals("--All Items--"))
								coupDiscGenDaoForDML.deleteTempSkuBy(users.getUserId(), type, keyLabel.getValue(), null,
										tempCouponObj != null ? tempCouponObj.getCouponId() : null);
							else
								coupDiscGenDaoForDML.deleteTempSkuBy(users.getUserId(), type, keyLabel.getValue(), limit,
										tempCouponObj != null ? tempCouponObj.getCouponId() : null);
						}//else if(oneCombinationId.isChecked()){
						else if(ruleType.equalsIgnoreCase(combined)){
							if(type!=null){
							if(type.contains(",")){
								String[] typeArr = type.split(",");
								for(String typeONE : typeArr){
									coupDiscGenDaoForDML.deleteTempSkuBy(users.getUserId(), typeONE.trim(), keyLabel.getValue(), limit,
											tempCouponObj != null ? tempCouponObj.getCouponId() : null);
								}
							}else{
								coupDiscGenDaoForDML.deleteTempSkuBy(users.getUserId(), type.trim(), keyLabel.getValue(), limit,
										tempCouponObj != null ? tempCouponObj.getCouponId() : null);
							}
							}
							
						}
						if(editCouponObj!=null)ruleType=editCouponObj.isCombineItemAttributes()?combined:multipleSingle;
						productDiscountGenRowsId.removeChild(tempRow);
						
						if(productDiscountGenRowsId.getChildren().size() == 0) ruleType=Constants.STRING_NILL;
					} else {
						coupDiscGenDaoForDML.deleteTempSkuBy(users.getUserId(), keyLabel.getValue(),
								typeLabel.getValue().equals("N/A") ? "0" : typeLabel.getValue(), null);
						receiptDiscountGenRowsId.removeChild(tempRow);	
					}

					
					int cnt = addedSKUSegmentLBId.getItemCount();
					for (; cnt > 0; cnt--) {
						addedSKUSegmentLBId.removeItemAt(cnt - 1);
					}
					deSelectSkuMap.clear();
				
				}else if(tempImg.getAttribute("COUPON_TYPE") != null
						&& tempImg.getAttribute("COUPON_TYPE").equals(COUPON_RULE_EDIT)) {
					tempRow = (Row) tempImg.getParent().getParent();

					Label keyLabel = (Label) tempRow.getChildren().get(0);
					Label typeLabel = (Label) tempRow.getChildren().get(1);
					String limit = tempRow.getValue();
					long rule=0;
					if(tempRow.getAttribute("skutemp")!=null) rule=(long) tempRow.getAttribute("skutemp");
					logger.info("limit "+limit);
					logger.info("rule "+rule);
					List<SKUTemp> listSku = null;
					List<SKUTemp> tempListSku = new ArrayList<SKUTemp>();
					List<SKUTemp> tempLimitListSku = coupDiscGenDao.findTempSkuBy(users.getUserId(),null,limit);
					List<SKUTemp> tempRuleListSku = coupDiscGenDao.findTempSkuBy(users.getUserId(),null,rule==0?limit:String.valueOf(rule)+ "::" +String.valueOf(rule));
					
					if(discountRadioGrId.getSelectedItem()==skuRadioId) {
					String[] indx=limit.split("::");
					List<Component> existingRules = new ArrayList<Component>();
					existingRules = discountAttributeDivId.getChildren();
					for(Component c:existingRules){
						deleteExistedRulesDiv(c);
					}
					existingRules = purIntAttributeDivId.getChildren();
					for(Component c:existingRules){
						deleteExistedRulesDiv(c);
					}
					existingRules = EligibilityAttributeDivId.getChildren();
					for(Component c:existingRules){
						deleteExistedRulesDiv(c);
					}
					if(ruleType.equalsIgnoreCase(combined)){
					listSku = coupDiscGenDao.findTempSkuBy(users.getUserId(),null,limit);
					editCombinedRuleObj=listSku;
					logger.info("listSku size"+listSku.size());
					displayEditRules(editCouponObj,tempListSku!=null && !tempListSku.isEmpty()?tempListSku:listSku);
					}else {
						listSku = coupDiscGenDao.findTempSkuBy(users.getUserId(),null,indx[0].equalsIgnoreCase(indx[1])?String.valueOf(rule)+ "::" +String.valueOf(rule):limit);
						//listSku = coupDiscGenDao.findTempSkuBy(users.getUserId(),null,rule>0?String.valueOf(rule)+ "::" +String.valueOf(rule):limit);
						for(SKUTemp temp:tempLimitListSku) {
							if(!tempLimitListSku.isEmpty() && temp.getDiscount()!=null)
							if(tempRuleListSku.get(0).getQuantity()==null || tempRuleListSku.get(0).getQuantity().isEmpty()) {
					//			if(tempRuleListSku.get(0).getSkuAttribute()!=null)
							if(
									tempRuleListSku.get(0).getDiscount().equals(temp.getDiscount())) { 
								
								tempListSku.add(temp);
							}
							}else if (temp.getDiscount()!=null){
								if(tempRuleListSku.get(0).getSkuAttribute().equals(temp.getSkuAttribute()) && 
										tempRuleListSku.get(0).getDiscount().equals(temp.getDiscount()) &&
										tempRuleListSku.get(0).getQuantity().equals(temp.getQuantity())) {
									
									tempListSku.add(temp);
								}
							}
						}
						editCombinedRuleObj=tempListSku!=null && !tempListSku.isEmpty()?tempListSku:listSku;
						displayEditRules(editCouponObj,tempListSku!=null && !tempListSku.isEmpty()?tempListSku:listSku);
					}
					}else {
						List<Component> existingRules = new ArrayList<Component>();
						existingRules = discountAttributeDivId.getChildren();
						for(Component c:existingRules){
							deleteExistedRulesDiv(c);
						}
						existingRules = purIntAttributeDivId.getChildren();
						for(Component c:existingRules){
							deleteExistedRulesDiv(c);
						}
						existingRules = EligibilityAttributeDivId.getChildren();
						for(Component c:existingRules){
							deleteExistedRulesDiv(c);
						}
						listSku = coupDiscGenDao.findTempSkuBy(users.getUserId(),null,rule>0?String.valueOf(rule)+ "::" +String.valueOf(rule):limit);
						editedRuleRowObj=listSku.get(0);
						logger.info("listSku size"+listSku.size());
						displayEditRules(editCouponObj,tempListSku!=null && !tempListSku.isEmpty()?tempListSku:listSku);
					}
				}
					else if (tempImg.getAttribute("delete")!=null && tempImg.getAttribute("delete").equals(DELETE_SELECTED)) {
					lc = (Listcell) tempImg.getParent();
					li = (Listitem) lc.getParent();
					addedSKUSegmentLBId.removeChild(li);
				} 
				//APP-2720
				/*else if (tempImg.getAttribute("editItem")!=null && tempImg.getAttribute("editItem").equals("EDIT_SELECTED")) {
					lc = (Listcell) tempImg.getParent();
					li = (Listitem) lc.getParent();
				//	Coupons couponObj = (Coupons) tempImg.getAttribute("COUPON_OBJ");
					editQuantityAndDiscount();
					//addedSKULBId.removeChild(li);
				} */
				
				/*else if (tempImg.getAttribute("COUPON_TYPE") != null
						&& tempImg.getAttribute("COUPON_TYPE").equals(COUPON_DISCOUNT_EDIT)){
					logger.info("==Inside on event edit==");
					//Coupons couponObj = (Coupons) tempImg.getAttribute("COUPON_OBJ");
					editQuantityAndDiscount();

					
				}*/

				else if (tempDiv != null && tempDiv.getParent() instanceof Row) {

					tempRow = (Row) tempDiv.getParent();
					tempCouponObj = (Coupons) tempRow.getAttribute("COUPON_OBJ");
					logger.debug(">>>>tempCouponObj >>" + tempCouponObj);

					if (tempImg.getAttribute("COUPON_TYPE").equals(COUPON_EDIT)) {
//					sessionScope.setAttribute("EDIT_COUPON_OBJ", tempCouponObj);

						editCouponSetting(tempCouponObj);
						// Redirect.goTo("admin/couponGeneration");

						/**
						 * Commented by venkata manageCouponsTabBoxId.setSelectedIndex(1);
						 */

					}

					else if (tempImg.getAttribute("COUPON_TYPE").equals(COUPON_STATUS)) {

						String coupStatusStr = tempCouponObj.getStatus();

						if (coupStatusStr.equals(Constants.COUP_STATUS_EXPIRED)) {
							MessageUtil.setMessage("Discount code has expired. Please reactivate the Discount code.", "RED",
									"TOP");
							return;
						}

						try {
							int confirm = Messagebox.show("Are you sure you want to change the Discount code status?",
									"Discount Code Status Changes ", Messagebox.OK | Messagebox.CANCEL,
									Messagebox.QUESTION);
							if (confirm != Messagebox.OK)
								return;
						} catch (Exception e) {
							logger.error("Exception ::", e);
							return;
						}

						Calendar currCal = Calendar.getInstance();
						String imgSrc = "";
						String toolTipStr = "";
						if (coupStatusStr.equals(Constants.COUP_STATUS_PAUSED)) {
							if (currCal.after(tempCouponObj.getCouponExpiryDate())) {
								coupStatusStr = Constants.COUP_STATUS_EXPIRED;
								imgSrc = "/img/pause_icn.png";
								toolTipStr = "Pause";
							} else if (currCal.after(tempCouponObj.getCouponCreatedDate())
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

						tempCouponObj.setStatus(coupStatusStr);
						// couponsDao.saveOrUpdate(tempCouponObj);
						couponsDaoForDML.saveOrUpdate(tempCouponObj);
						Label tempLbl = (Label) tempRow.getChildren().get(4);
						tempLbl.setValue(coupStatusStr);

						// logger.info("Image Src is >>"+imgSrc+ " >>>coupStatusStr ::"+coupStatusStr+"
						// >>toolTipStr is::"+toolTipStr);
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
						// logger.info("coupCodeInt>>>"+coupCodeInt);
						if (coupCodeInt > 0) {
							MessageUtil.setMessage(
									"Discount Code is currently being used in a scheduled Email / SMS campaign and cannot be deleted.",
									"red");
							return;
						}

						logger.debug("tempCouponObj.getCouponId() >>> " + coupId);

						// coupDiscGenDao.deleteByCouponId(coupId);
						coupDiscGenDaoForDML.deleteByCouponId(coupId);

						couponRowsId.removeChild(tempRow);

						getAllPromoFromDB();
						contactsGridId.setAttribute("defaultOrderBy", "userCreatedDate");
						registerEventListner(0, 5);
						Utility.refreshGridModel(viewCouponsELObj, 0, true);
						MessageUtil.setMessage("Discount Code deleted successfully.", "green", "TOP");

					}else if(tempImg.getAttribute("COUPON_TYPE").equals(PRODUCT_COUPON_DELETE)){

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
						// logger.info("coupCodeInt>>>"+coupCodeInt);
						if (coupCodeInt > 0) {
							MessageUtil.setMessage(
									"Discount Code is currently being used in a scheduled Email / SMS campaign and cannot be deleted.",
									"red");
							return;
						}

						logger.debug("tempCouponObj.getCouponId() >>> " + coupId);

						// coupDiscGenDao.deleteByCouponId(coupId);
						coupDiscGenDaoForDML.deleteByCouponId(coupId);

						couponRowsId.removeChild(tempRow);

						getAllPromoFromDB();
						contactsGridId.setAttribute("defaultOrderBy", "userCreatedDate");
						registerEventListner(0, 5);
						Utility.refreshGridModel(viewCouponsELObj, 0, true);
						MessageUtil.setMessage("Discount Code deleted successfully.", "green", "TOP");

					
					} else if (tempImg.getAttribute("COUPON_TYPE").equals(COUPON_REPORTS) && tempCouponObj != null) {

						logger.info("tempCouponObj.getCouponId() >>> " + tempCouponObj.getCouponId());
						sessionScope.setAttribute("COUP_REDEEMED_DETAILS", tempCouponObj);
						Redirect.goTo(PageListEnum.REPORT_COUPONS);

					} else if (tempImg.getAttribute("COUPON_TYPE").equals(COUPON_EXPORT)) {

						try {
							int confirm = Messagebox.show("Are you sure you want to export Discount Code?",
									"Export Dicount Code Generation  ", Messagebox.OK | Messagebox.CANCEL,
									Messagebox.QUESTION);
							if (confirm != Messagebox.OK)
								return;
						} catch (Exception e) {
							logger.error("Exception ::" + e.getStackTrace());
							return;
						}

						export(tempCouponObj);

					}
				} else if (tempImg.getAttribute("COUPON_TYPE")!=null && tempImg.getAttribute("COUPON_TYPE").equals("DELETE_STORE")) {
					logger.debug("delete store here ");
					// TODO show message here
					Listitem storeItem = (Listitem) tempImg.getParent().getParent();
					selectedStoreLbId.removeChild(storeItem);

				}else if (tempImg.getParent() instanceof Checkbox) {
					logger.info("Checkox inside---");
					
				}

			}

			else if (event.getTarget() instanceof Paging) {

				Paging paging = (Paging) event.getTarget();
				int desiredPage = paging.getActivePage();

				PagingEvent pagingEvent = (PagingEvent) event;
				int pSize = pagingEvent.getPageable().getPageSize();
				int ofs = desiredPage * pSize;

				fillCouponsBySize(ofs, (byte) pagingEvent.getPageable().getPageSize());

			} else if (event.getTarget() instanceof Checkbox) {
				Checkbox ck = (Checkbox) event.getTarget();
				
				if(ck.getId().contains("auxCheckBox")){
				
					onCheck$udfcheckbox(true);
					
				}else {
					Listbox listbox = ((Listbox) ck.getAttribute("listId"));
				if (ck.isChecked()) {
					listbox.setVisible(true);
				} else {
					listbox.setVisible(false);
					listbox.setSelectedIndex(-1);
				}
				adjustListBoxes();
				}
			} else if (event.getTarget() instanceof Listbox) {
				/*
				 * logger.info("Inside the event"); Listbox lb=(Listbox) event.getTarget();
				 * for(Listitem item: lb.getItems()){ String column=null; column=(String)
				 * item.getAttribute("column"); logger.info("name"+column); String
				 * sku=skufileMap.get(item.getValue()); Listbox listbox=listMap.get(sku);
				 * if(item.isSelected()){ listbox.setVisible(true); }else{
				 * listbox.setVisible(false); listbox.setSelectedIndex(-1);
				 * 
				 * } adjustListBoxes();
				 * 
				 * }
			 */}else if(event.getTarget() instanceof Textbox){
					 Textbox tempTb = (Textbox) event.getTarget();
						logger.info("before onOK udf search with textbox id"+tempTb.getId());
						if(tempTb.getId().contains("udf")){
							logger.info("inside onOK udf search with textbox id"+tempTb.getId());
							Listbox lb=(Listbox) tempTb.getAttribute("listboxName");//, listbox);
							 String column=(String) tempTb.getAttribute("column");//, (String) li.getAttribute("column"));
							 
							 if(lb!=null && column!=null){
								 onOK$udfTextbox(lb,column);
							 }
						}
					 
					 
				 }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
		}

	} // onEvent

			
				
	
	public void onSelect$itemAttributeLbId() {
		//Set<Listitem> selectedItems = itemAttributeLbId.getSelectedItems();
		filterFlag=false;
		loadAllColumns=false;

		List<Listitem> allItems = itemAttributeLbId.getItems();
		if(itemAttributeLbId.getSelectedCount()>0){
			selectCriteriaGBId.setOpen(true);
			addCritiraBtnId.setVisible(true);
			//addCritiraBtnId.setStyle("margin-left:500px;");
		}else{
			addCritiraBtnId.setVisible(false);
		}
		
		List<Listitem> selectedItems = itemAttributeLbId.getItems();
		for (Listitem listitem : selectedItems) {
			if(listitem.getAttribute("AssociatedLB")==null && listitem.getValue().toString().toLowerCase().startsWith("udf")){
				
				String sku = skufileMap.get(listitem.getValue().toString());
				Listbox lbtId = listMap.get(sku);
				
				if(lbtId!=null && lbtId.getId()!=null){
				//	logger.info("========="+lbtId.getId().toString());
					listitem.setAttribute("AssociatedLB", lbtId);
				}

			}
		}
		
		
		
		for (Listitem li : allItems) {
//			logger.info("inside item attribute div===" + li.getValue().toString().toLowerCase());
			// if(!genFieldSKUMap.containsKey(li.getLabel())){
			if (li.getValue().toString().toLowerCase().startsWith("udf") && li.getAttribute("AssociatedLB") == null) {
				logger.debug("entered ====");
							
				Listbox listbox = new Listbox();
				listbox.setId(li.getValue().toString().toLowerCase() + "FilterLBID");
				listbox.setAttribute("listId", li.getId());
				listbox.setHeight("230px");
				listbox.setWidth("200px");

				//listbox.setSizedByContent(true);
				listbox.setCheckmark(true);
				listbox.setMultiple(true);
				listbox.setEmptyMessage("No " + li.getLabel());
				listbox.setVisible(false);
				listbox.addEventListener("onSelect", this);
				listbox.setParent(listBoxesDivId);
				//listBoxesDivId.setParent(selectCriteriaGBId);
				Listhead listHead = new Listhead();
				Listheader listHeader = new Listheader();
				listHeader.setWidth("30px");
				listHeader.setAlign("center");
				listHeader.setStyle("opacity: 0; pointer-events: none;");
				listHeader.setId(li.getValue().toString().toLowerCase() + "listHeaderId");
				listbox.setAttribute("listhead", listHeader);
				listHeader.setParent(listHead);
				//logger.info("Listheader..............." + li.getValue().toString().toLowerCase() + "listHeaderId");
				
				Listheader listHeader1 = new Listheader();
				listHeader1.setLabel(li.getLabel());
				listHeader1.setParent(listHead);
				logger.info("label==" + li.getLabel());
				listHeader1.setValue(li.getIndex() + 1);
				listHead.setParent(listbox);
				Auxhead auxhead = new Auxhead();
				auxhead.setId(li.getValue().toString().toLowerCase() + "AhId");
				
				Auxheader auxheader1 = new Auxheader();
				Checkbox checkbox = new Checkbox();
				checkbox.setId(li.getValue().toString().toLowerCase() + "auxCheckBox");
				listbox.setAttribute("auxhead", checkbox);
				//logger.info("AuxCheckBox..............." + li.getValue().toString().toLowerCase() + "auxCheckBox");
				
				checkbox.addEventListener("onCheck", this);
				auxheader1.setParent(auxhead);
				checkbox.setParent(auxheader1);
				
			
				Auxheader auxheader = new Auxheader();
				Menupopup menupopup = new Menupopup();
				menupopup.setId(li.getValue().toString().toLowerCase() + "MpId");
				menupopup.setParent(auxheader);
				Toolbarbutton toolbarBtn = new Toolbarbutton();
				toolbarBtn.setContext(menupopup);
				//toolbarBtn.setTooltiptext("filters|cs");
				toolbarBtn.setVisible(false);
				toolbarBtn.setParent(auxheader);
				Textbox textbox = new Textbox();
				textbox.setId(li.getValue().toString().toLowerCase() + "TBId");
				//textbox.setSclass("filterSearchVal");
				textbox.setWidth("85px");
				textbox.setAttribute("listboxName", listbox);
				textbox.setAttribute("column", (String) li.getAttribute("column"));
				textbox.addEventListener("onOK", this);
				textbox.setParent(auxheader);
				Image img=new Image();
				img.setId("udfFilterImage"+li.getValue().toString().toLowerCase());
				img.setSrc("/images/all_segment_icn.png");
				img.setWidth("18px");
				img.setStyle("margin-top:2px;");
				img.addEventListener("onClick", this);
				img.setParent(auxheader);
				img.setAttribute("listboxName", listbox);
				img.setAttribute("column", (String) li.getAttribute("column"));
				auxheader.setParent(auxhead);
				auxhead.setParent(listbox);
				listbox.setAttribute("CustomFieldName", li.getValue().toString().toLowerCase());
				listbox.setAttribute("Textbox", textbox);
				li.setAttribute("AssociatedLB", listbox);
				//listbox.setAttribute("column",(String) li.getAttribute("column"));
				listMap.put(listbox.getId(), listbox);
				skufileMap.put(li.getValue().toString().toLowerCase(), listbox.getId());
				//headerMap.put(li.getIndex() + 1 + "", li.getValue());
				// listMap.put(key, value)
			} /*else if(!li.getValue().toString().toLowerCase().startsWith("udf")){
				
				
				listMap.put("vendorCodeFilterLBId", vendorCodeFilterLBId);
				listMap.put("deptCodeFilterLBId", deptCodeFilterLBId);
				listMap.put("itemCategoryFilterLBId", itemCategoryFilterLBId);
				listMap.put("dcsFilterLBId", dcsFilterLBId);
				listMap.put("classFilterLBId", classFilterLBId);
				listMap.put("subClassFilterLBId", subClassFilterLBId);
				listMap.put("descriptionFilterLBId", descriptionFilterLBId);
				listMap.put("skuFilterLBId",skuFilterLBId);
				
				for (Entry<String, Listbox> entry : listMap.entrySet()) {
					 logger.info("listMap.entrySet()===="+listMap.entrySet());
					Listbox listbox = entry.getValue();
					Listhead head = listbox.getListhead();
					List<Component> list=head.getChildren();
					Listheader header = (Listheader) head.getLastChild();
					header.setValue();
				}
				
			}*//*(li.getValue().toString().equalsIgnoreCase("sku")) {
				//listMap.put("viewSKULbId", viewSKULbId);
			//	headerMap.put(li.getIndex() + 1 + "", li.getValue());
				logger.info("sku=="+li.getValue());
			} */
		}
		adjustListBoxes();
		prepraAllAttributeData(null);
		/*query = " FROM SkuFile  WHERE userId =  " + users.getUserId().longValue() + " ";
		List<SkuFile> list = skuFileDao.executeQuery(query);
		populateFilteredLists(list,"",null);*/
		List<Listitem> items = itemAttributeLbId.getItems();

		for (Listitem item : items) {
			String column = null;
			
				column = (String) item.getAttribute("column");
				//logger.info("name" + column);
				String lbID = skufileMap.get(item.getValue());
				Listbox listbox = listMap.get(lbID);
				if(listbox!=null){
				if (item.isSelected()) {
					listbox.setVisible(true);
				} else {
					listbox.setVisible(false);
					listbox.setSelectedIndex(-1);
				}
			}
		}

	}

	public void onSelect$srchLbId() {
		String value = srchLbId.getSelectedItem().getValue();
		if (value.equals(SEARCH_BY_NAME)) {
			searchByPromoCodeNameDivId.setVisible(true);
			searchByPromoCodeNameTbId.setText(Constants.STRING_NILL);
			searchByPromoCodeNameTbId.setFocus(true);

			searchByCreatedOnDivId.setVisible(false);
			searchByStatusDivId.setVisible(false);
			codeStatusLb.setSelectedIndex(0);
			return;
		} else if (value.equals(SEARCH_BY_DATE)) {
			searchByPromoCodeNameDivId.setVisible(false);
			searchByCreatedOnDivId.setVisible(true);
			fromDateboxId.setText(Constants.STRING_NILL);
			toDateboxId.setText(Constants.STRING_NILL);

			searchByStatusDivId.setVisible(false);
			codeStatusLb.setSelectedIndex(0);
			return;
		} else if (value.equals(SEARCH_BY_STATUS)) {
			searchByPromoCodeNameDivId.setVisible(false);
			searchByCreatedOnDivId.setVisible(false);
			searchByStatusDivId.setVisible(true);
			codeStatusLb.setSelectedIndex(0);
			return;
		}

	}// onSelect$srchLbId()

	public void onClick$filterBtnId() {
		couponsPagingId.setActivePage(0);
		registerEventListner(0, couponsPagingId.getPageSize());
		Utility.refreshGridModel(viewCouponsELObj, 0, true);
	}// onClick$filterBtnId()

	private boolean validateSetCreationDate() {

		if (fromDateboxId.getValue() == null || toDateboxId.getValue() == null) {
			MessageUtil.setMessage("Please enter the required dates.", "color:red", "TOP");

			return false;
		}

		Calendar serverFromDateCal = fromDateboxId.getServerValue();
		Calendar serverToDateCal = toDateboxId.getServerValue();

		Calendar tempClientFromCal = fromDateboxId.getClientValue();
		Calendar tempClientToCal = toDateboxId.getClientValue();

		logger.debug("client From :" + tempClientFromCal + ", client To :" + tempClientToCal);

		// change the time for startDate and endDate in order to consider right
		// from the
		// starting time of startDate to ending time of endDate
		serverFromDateCal.set(Calendar.HOUR_OF_DAY,
				serverFromDateCal.get(Calendar.HOUR_OF_DAY) - tempClientFromCal.get(Calendar.HOUR_OF_DAY));
		serverFromDateCal.set(Calendar.MINUTE,
				serverFromDateCal.get(Calendar.MINUTE) - tempClientFromCal.get(Calendar.MINUTE));
		serverFromDateCal.set(Calendar.SECOND, 0);

		serverToDateCal.set(Calendar.HOUR_OF_DAY,
				23 + serverToDateCal.get(Calendar.HOUR_OF_DAY) - tempClientToCal.get(Calendar.HOUR_OF_DAY));
		serverToDateCal.set(Calendar.MINUTE,
				59 + serverToDateCal.get(Calendar.MINUTE) - tempClientToCal.get(Calendar.MINUTE));
		serverToDateCal.set(Calendar.SECOND, 59);

		if (serverToDateCal.compareTo(serverFromDateCal) < 0) {
			MessageUtil.setMessage("'To' date must be later than 'From' date.", "color:red", "TOP");
			return false;
		}

		fromDateStr = serverFromDateCal.toString();
		toDateStr = serverToDateCal.toString();

		return true;

	}// validateSetCreationDate()

	private boolean validateSetCampaignName() {
		if (searchByPromoCodeNameTbId.getValue().trim().isEmpty()) {
			MessageUtil.setMessage("Please enter promo code name.", "color:red", "TOP");
			searchByPromoCodeNameTbId.setFocus(true);
			return false;
		}
		return true;
	}// validateSetCampaignName()

	
	
	public void onClick$resetAnchId() {
					loadAllColumns=true;
					resetCheckBox();
					prepraAllAttributeData(null);
					loadAllColumns=false;
					/*onClick$applyFilterBtnId();
					
					List<Listitem> items = itemAttributeLbId.getItems();

					for (Listitem item : items) {
							String lbID = skufileMap.get(item.getValue());
							Listbox listbox = listMap.get(lbID);
							if(listbox!=null){
							if (item.isSelected()) {
								listbox.setVisible(true);
								listbox.setSelectedIndex(-1);
							} else {
								listbox.setVisible(false);
								listbox.setSelectedIndex(-1);
							}
						}
					}*/

		//onSelect$itemAttributeLbId();

		

	}

	private boolean validateCouponName4SpecialChars(String couponName) {
		String pattern = "[a-zA-Z0-9\\-\\*]+";
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(couponName);

		return m.matches();
	}

	public void onClick$showFilterImgId() {

		logger.info("show");
		hideFilterImgId.setVisible(true);
		showFilterImgId.setVisible(false);
		filtersDivId.setVisible(true);

	}

	public void onClick$hideFilterImgId() {

		logger.info("hide");
		showFilterImgId.setVisible(true);
		hideFilterImgId.setVisible(false);
		filtersDivId.setVisible(false);
	}

	public void onCheck$promoRgId() {
		if (promoRgId.getSelectedIndex() == 0) {

			staticRadio.setChecked(true);
			dynamicRadio.setChecked(false);
			dynamicValidityTbId.setValue("");
			dynamicValidityLbId.setSelectedIndex(0);
		} else if (promoRgId.getSelectedIndex() == 1) {

			staticRadio.setChecked(false);
			dynamicRadio.setChecked(true);

			createDateBxId.setValue(null);
			expiryDateBxId.setValue(null);
		}
	}

	@Override
	public void render(Row tempRow, Coupons coupons, int arg2) throws Exception {

		Label tempLbl;

		// Coupon Name
		tempLbl = new Label(coupons.getCouponName());
		tempLbl.setParent(tempRow);

		// Description
		tempLbl = new Label(coupons.getCouponDescription());
		tempLbl.setParent(tempRow);

		// Promo code type Single or Multiple
		if (coupons.getCouponGeneratedType().equalsIgnoreCase("single"))
			tempLbl = new Label("Single code");
		else if (coupons.getCouponGeneratedType().equalsIgnoreCase("multiple"))
			tempLbl = new Label("Multiple & Random code");
		tempLbl.setParent(tempRow);

		logger.info("coupon name is ::" + coupons.getCouponName());
		// logger.info("created date is ::"+coupons.getCouponCreatedDate().getTime());
		// Created On
		tempLbl = new Label(
				MyCalendar.calendarToString(coupons.getUserCreatedDate(), MyCalendar.FORMAT_DATEONLY_GENERAL));
		tempLbl.setParent(tempRow);

		// Status
		tempLbl = new Label(coupons.getStatus());
		tempLbl.setParent(tempRow);

		// Discount
		String tempStr = "";
		if (coupons.getDiscountType().trim().equals("Percentage")) { // TODO set the constants
			tempStr = "% on";
		} else {
			tempStr = "$ on";
		}

		if (coupons.getDiscountCriteria().trim().contains("SKU")) {
			tempStr += " Product";
		} else {
			tempStr += " Receipt";
		}
		// tempStr = tempStr+" "+coupons.getDiscountCriteria().trim();

		tempLbl = new Label(tempStr);
		tempLbl.setParent(tempRow);

		// Validity Period
		if (coupons.getExpiryType().equalsIgnoreCase(Constants.COUP_VALIDITY_PERIOD_DYNAMIC)) {
			logger.info("dynamic validity-------");
			tempLbl = new Label("Dynamic");
			tempLbl.setParent(tempRow);
		} else if (coupons.getExpiryType().equalsIgnoreCase(Constants.COUP_VALIDITY_PERIOD_STATIC)) {
			logger.info("static validity-------");
			tempLbl = new Label(MyCalendar.calendarToString(coupons.getCouponCreatedDate(),
					MyCalendar.FORMAT_DATEONLY_GENERAL) + " - "
					+ MyCalendar.calendarToString(coupons.getCouponExpiryDate(), MyCalendar.FORMAT_DATEONLY_GENERAL));
			tempLbl.setParent(tempRow);
		}

		// No.of Codes

		String numOfCodeStr = "";
		boolean isSingleType = false;
		if (coupons.getCouponGeneratedType().equals("single")) {

			if (coupons.getAutoIncrCheck()) {
				numOfCodeStr = "Unlimited";
			} else {
				numOfCodeStr = "" + coupons.getTotalQty();
			}

			isSingleType = true;
		} else {
			if (coupons.getAutoIncrCheck()) {
				numOfCodeStr = coupons.getTotalQty() + "(Auto add)";
			} else {
				numOfCodeStr = coupons.getTotalQty() + "";
			}
		}
		tempLbl = new Label(numOfCodeStr);
		tempLbl.setParent(tempRow);

		// Actions
		Div tempDiv = new Div();

		// Edit Image
		Image editImg = new Image("/img/email_edit.gif");
		editImg.setStyle("cursor:pointer;margin-right:10px;");
		editImg.setTooltiptext("Edit");
		editImg.setAttribute("COUPON_TYPE", COUPON_EDIT);
		editImg.addEventListener("onClick", this);
		editImg.setParent(tempDiv);

		// Active Image
		Image tempImg = new Image();
		String statusStr = coupons.getStatus();

		// Pause or Active images
		if (statusStr.equals(Constants.COUP_STATUS_ACTIVE) || statusStr.equals(Constants.COUP_STATUS_RUNNING)
				|| statusStr.equals(Constants.COUP_STATUS_EXPIRED)) {

			tempImg.setSrc("/img/pause_icn.png");
			tempImg.setTooltiptext("Pause");

		} else if (statusStr.equals(Constants.COUP_STATUS_PAUSED)) {
			tempImg.setSrc("/img/play_icn.png");
			tempImg.setTooltiptext(Constants.COUP_STATUS_ACTIVE);

		}

		tempImg.setAttribute("COUPON_TYPE", COUPON_STATUS);
		tempImg.setStyle("cursor:pointer;margin-right:10px;");
		tempImg.addEventListener("onClick", this);
		tempImg.setParent(tempDiv);

		// Delete Image
		Image delImg = new Image("/img/action_delete.gif");
		delImg.setStyle("cursor:pointer;margin-right:10px;");
		delImg.setAttribute("COUPON_TYPE", COUPON_DELETE);
		delImg.setTooltiptext("Delete");
		delImg.addEventListener("onClick", this);
		delImg.setParent(tempDiv);
		// reports Image
		Image repImg = new Image("/img/theme/home/reports_icon.png");
		repImg.setStyle("cursor:pointer;margin-right:10px;");
		repImg.setAttribute("COUPON_TYPE", COUPON_REPORTS);
		repImg.setTooltiptext("Reports");
		repImg.addEventListener("onClick", this);
		repImg.setParent(tempDiv);

		if (!isSingleType) {
			// export promocodes
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
		// tempRow.setParent(couponRowsId);

	}

	// SMART PROMOTION
	public void setSmartPromoValues(Coupons editCouponObj) {

		if (editCouponObj != null) {

			String disSecCriteria = editCouponObj.getDiscountCriteria() != null
					? editCouponObj.getDiscountCriteria().trim()
					: "";
					String noOfEligibleItems = editCouponObj.getNoOfEligibleItems() != null
							&& !editCouponObj.getNoOfEligibleItems().isEmpty()
									? editCouponObj.getNoOfEligibleItems().toString().trim()
									: "";
									
		/*	if(editCouponObj.isAccumulateOtherPromotion() || editCouponObj.isExcludeItems()!=null ? editCouponObj.isExcludeItems() : false || 
				noOfEligibleItems.equals(Constants.HIGHEST_PRICED_ITEM_WITH_DISCOUNT) || 
				noOfEligibleItems.equals(Constants.HIGHEST_PRICED_ITEM_WITH_OUT_DISCOUNT) ||
				noOfEligibleItems.equals(Constants.ALL_ELIGIBLE_ITEMS)){
			
				smartPromoDivId.setVisible(true);
				String image = smartPromoDivId.isVisible() ? "/img/icons/icon_minus.png" :  "/img/icons/icon_plus.png";
				advancedSettingsTbBtnId.setImage(image);
										
			}else{
				smartPromoDivId.setVisible(false);
				String image = smartPromoDivId.isVisible() ? "/img/icons/icon_minus.png" :  "/img/icons/icon_plus.png";
				advancedSettingsTbBtnId.setImage(image);
			}
			*/
			
			if (disSecCriteria.equals(SELECT_SKU)) {

				accumulateCoupChkBxId.setChecked(editCouponObj.isAccumulateOtherPromotion());
				quantityDivId.setVisible(true);
				// discountedItemsCoupChkBxId.setChecked(editCouponObj.isExcludeItems());
				/*
				 * if (accumulateCoupChkBxId.isChecked()) {
				 * maximumDiscountDivId.setVisible(true); quantityDivId.setVisible(false);
				 * 
				 * } else { maximumDiscountDivId.setVisible(false);
				 * quantityDivId.setVisible(false); }
				 */
				
				/*if (accumulateCoupChkBxId.isChecked()) {
					maximumDiscountDivId.setVisible(true);
					quantityDivId.setVisible(false);

					if (noOfEligibleItems.equals(Constants.HIGHEST_PRICED_ITEM_WITH_DISCOUNT)
							|| noOfEligibleItems.equals(Constants.HIGHEST_PRICED_ITEM_WITH_OUT_DISCOUNT)) {
						quantityDivId.setVisible(true);
					}

				} else {
					maximumDiscountDivId.setVisible(false);
					quantityDivId.setVisible(false);
					if (noOfEligibleItems.equals(Constants.HIGHEST_PRICED_ITEM_WITH_DISCOUNT)
							|| noOfEligibleItems.equals(Constants.HIGHEST_PRICED_ITEM_WITH_OUT_DISCOUNT)) {
						quantityDivId.setVisible(true);
					}
				}*/
				if(editCouponObj.isExcludeItems()!=null){
					if (editCouponObj.isExcludeItems()) {
						discountedItemsCoupChkBxId.setChecked(true);
						//discountDivId.setVisible(false);
					}
				}

				numberEligibleDivId.setVisible(true);

				if (noOfEligibleItems.equals(Constants.HIGHEST_PRICED_ITEM_WITH_DISCOUNT))
					eligibleItemRadioId.setSelectedIndex(1);
				else if (noOfEligibleItems.equals(Constants.HIGHEST_PRICED_ITEM_WITH_OUT_DISCOUNT))
					eligibleItemRadioId.setSelectedIndex(2);
				else if (noOfEligibleItems.equals(Constants.ALL_ELIGIBLE_ITEMS))
					eligibleItemRadioId.setSelectedIndex(0);
				else if (noOfEligibleItems.equals(Constants.LOWEST_PRICED_ITEMS_WITH_DISCOUNT))
					eligibleItemRadioId.setSelectedIndex(3);
				else if (noOfEligibleItems.equals(Constants.LOWEST_PRICED_ITEMS_WITH_OUT_DISCOUNT))
					eligibleItemRadioId.setSelectedIndex(4);
				
				
			
				/*if (noOfEligibleItems.equals(Constants.HIGHEST_PRICED_ITEM_WITH_DISCOUNT)
							|| noOfEligibleItems.equals(Constants.HIGHEST_PRICED_ITEM_WITH_OUT_DISCOUNT)) {
						quantityDivId.setVisible(true);
				} else
						quantityDivId.setVisible(false);
				*///recent

			} else {
				numberEligibleDivId.setVisible(false);
			//	accumulateCoupChkBxId.setChecked(editCouponObj.isAccumulateOtherPromotion());
				// discountedItemsCoupChkBxId.setChecked(editCouponObj.isExcludeItems());

				/*if (accumulateCoupChkBxId.isChecked()) {
					maximumDiscountDivId.setVisible(true);
					quantityDivId.setVisible(false);
					
					if (noOfEligibleItems.equals(Constants.HIGHEST_PRICED_ITEM_WITH_DISCOUNT)
							|| noOfEligibleItems.equals(Constants.HIGHEST_PRICED_ITEM_WITH_OUT_DISCOUNT)) {
						quantityDivId.setVisible(true);
					}

				} else {
					maximumDiscountDivId.setVisible(false);
					quantityDivId.setVisible(false);
					
					if (noOfEligibleItems.equals(Constants.HIGHEST_PRICED_ITEM_WITH_DISCOUNT)
							|| noOfEligibleItems.equals(Constants.HIGHEST_PRICED_ITEM_WITH_OUT_DISCOUNT)) {
						quantityDivId.setVisible(true);
					}
				}
*/
				if(editCouponObj.isExcludeItems()!=null){
					
					if (editCouponObj.isExcludeItems()) {
						discountedItemsCoupChkBxId.setChecked(true);
						//discountDivId.setVisible(false);
					}
				}

			}

		} else {

			if (skuRadioId.isChecked()) {
				accumulateCoupChkBxId.setChecked(false);
				discountedItemsCoupChkBxId.setChecked(false);

				numberEligibleDivId.setVisible(true);

				
			} else {
				accumulateCoupChkBxId.setChecked(false);
				discountedItemsCoupChkBxId.setChecked(false);
			}
		}
	}

	public void onClick$nextCoupBtnId() {

		if (tempCouponObj == null) {
			logger.info("tempCouponObj is null next button won't work.");
			return;
		}
		if (skuRadioId.isChecked() && productDiscountGenRowsId.isVisible() && productDiscountGenRowsId.getChildren().size() == 0) {
			MessageUtil.setMessage("Please provide valid discount rule.", "color:red", "TOP");
			return;
		}
		if (tpaRadioId.isChecked() && receiptDiscountGenRowsId.isVisible() && receiptDiscountGenRowsId.getChildren().size() == 0) {
			MessageUtil.setMessage("Please provide valid discount rule.", "color:red", "TOP");
			return;
		}
		List<Component> childVboxList = new ArrayList<Component>();
		Div chilVDiv =null;
		if(targetDivHasChildren(purIntAttributeDivId))childVboxList.addAll(purIntAttributeDivId.getChildren());
		//har
				int rulesDragged=0;
				for (Object obj : childVboxList) {
					
					if(obj instanceof Div) {
						
						//boolean isValid = true;
						for(Object object : ((Div)obj).getChildren()) {
							
							if(object instanceof Div) {
								
								chilVDiv = (Div)object;
								if(chilVDiv.getSclass().contains("drop_")) {
									continue;
								}
								List childDivList = chilVDiv.getChildren();
								for (Object objects : childDivList) {
									if(objects instanceof Div ){
										chilVDiv = (Div)objects;
										
										if(chilVDiv.getSclass().contains("drop_")) {
											continue;
										}
										rulesDragged+=1;
									}
								}
							}
						}
					}
				}
				//har
		if(tempCouponObj!=null && rulesDragged>0) {
			String rule = saveRule(true); 
			//boolean proceed = validateEditedRuleOnSave();
			if(rule==null) {
				MessageUtil.setMessage("Please add at least one rule.", "color:red;");
				return;
			}
		}
		/*if(skuRadioId.isChecked() && tempCouponObj!=null){
			 if(discountTypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("purchases") && tempCouponObj.getPurchaseQty()==null) {
				MessageUtil.setMessage("Please provide Item Quantity to discount", "color:green", "TOP");
				return;
			}
			}
		if(skuRadioId.isChecked() && tempCouponObj!=null) {
				
				if(discountTypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("loyaltyRewards") && tempCouponObj.getRequiredLoyltyPoits()==null) {
				MessageUtil.setMessage("Please provide Item Quantity to discount", "color:green", "TOP");
				return;
					}
		}		*/
		CouponDescriptionAlgorithm coupoDes = new CouponDescriptionAlgorithm();
		String desc = coupoDes.preparecouponDisc(tempCouponObj, users);
		
		/*if(oldDesc.equals(desc) && 
				editCouponObj.getCouponDescription() !=null && !editCouponObj.getCouponDescription().isEmpty() && 
				!editCouponObj.getCouponDescription().equals(desc)){
			
			tempCouponObj.setTempDesc(editCouponObj.getCouponDescription());
		}else{
			
			tempCouponObj.setTempDesc(desc);
		}*/
			if(tempCouponObj.getCouponDescription()==null || tempCouponObj.getCouponDescription().isEmpty()){
				tempCouponObj.setTempDesc(desc);
			}else if(!tempCouponObj.getCouponDescription().equals(desc)) {
				tempCouponObj.setTempDesc(desc);
			}
		/*if(oldDesc!=null){
			if(!oldDesc.equals(desc)){
				tempCouponObj.setTempDesc(desc);
			}else if(tempCouponObj.getCouponDescription()==null || tempCouponObj.getCouponDescription().isEmpty()){
				tempCouponObj.setTempDesc(desc);
			}else{
				editCouponObj.setTempDesc(tempDesc);
			}
		}*/
		//tempCouponObj.setTempDesc(desc);

		sessionScope.setAttribute("EDIT_COUPON_OBJECT", tempCouponObj);
		// TODO Navigate to next(second) page
		Redirect.goTo(PageListEnum.ADMIN_VIEW_CREATECOUPONSUCCEEDING);

	} // onclick$nextCoupBtnId

	public void onClick$promotionRulesSecondId() {

		if (tempCouponObj == null) {
			logger.info("tempCouponObj is null next button won't work.");
			return;
		}

		if(tempCouponObj.getDiscountCriteria().equalsIgnoreCase("SKU")) {
			if(productDiscountGenRowsId.getChildren().size() == 0) {
			MessageUtil.setMessage("Please provide valid discount rules.", "color:red", "TOP");
			return;
			}
		}
		else if(receiptDiscountGenRowsId.getChildren().size() == 0) {
			MessageUtil.setMessage("Please provide valid discount rules.", "color:red", "TOP");
			return;
		}
		
		CouponDescriptionAlgorithm coupoDes = new CouponDescriptionAlgorithm();
		String desc = coupoDes.preparecouponDisc(tempCouponObj, users);
		logger.info(tempCouponObj.getCouponDescription());
		/*if(oldDesc.equals(desc) && 
				editCouponObj.getCouponDescription() !=null && !editCouponObj.getCouponDescription().isEmpty() && 
				!editCouponObj.getCouponDescription().equals(desc)){
			
			tempCouponObj.setTempDesc(editCouponObj.getCouponDescription());
		}else{
			
			tempCouponObj.setTempDesc(desc);
		}*/
		if(tempCouponObj.getCouponDescription()==null || tempCouponObj.getCouponDescription().isEmpty()){
			tempCouponObj.setTempDesc(desc);
		}else if(!tempCouponObj.getCouponDescription().equals(desc)) {
			tempCouponObj.setTempDesc(desc);
		}
		/*if(oldDesc!=null){
			if(!oldDesc.equals(desc)){
				tempCouponObj.setTempDesc(desc);
			}else if(tempCouponObj.getCouponDescription()==null || tempCouponObj.getCouponDescription().isEmpty()){
				tempCouponObj.setTempDesc(desc);
			}
			
			else{
				editCouponObj.setTempDesc(tempDesc);
			}
		}*/
		//tempCouponObj.setTempDesc(desc);
		
		sessionScope.setAttribute("EDIT_COUPON_OBJECT", tempCouponObj);
		// TODO Navigate to next(second) page
		Redirect.goTo(PageListEnum.ADMIN_VIEW_CREATECOUPONSUCCEEDING);

	}

	public void onClick$mobileOfferThirdId() {

		if (tempCouponObj == null) {
			logger.info("tempCouponObj is null next button won't work.");
			return;
		}
		if (!tempCouponObj.isEnableOffer()) {
			MessageUtil.setMessage("Please enable mobile Offer", "color:blue");
			return;
		}

		session.setAttribute("COUPON_OBJECT_FOR_MOBILE_OFFERS", tempCouponObj);
		Redirect.goTo(PageListEnum.ADMIN_VIEW_OFFERNOTIFICATION);

	}
	

	public void onClick$advancedSettingsTbBtnId() {
		smartPromoDivId.setVisible(!smartPromoDivId.isVisible());
		String image = smartPromoDivId.isVisible() ? "/img/icons/icon_minus.png" :  "/img/icons/icon_plus.png";
		advancedSettingsTbBtnId.setImage(image);
	}
	//****algorithmic desc ****
/*public String preparecouponDisc(Coupons coupon){
		
		List<POSMapping> posMappingsList = posMappingDao.findByType("'"+Constants.POS_MAPPING_TYPE_SKU+"'", users.getUserId());
		Map<String , String> posMappingMap = new HashMap<String , String>();
		if(posMappingsList !=null && !posMappingsList.isEmpty()){
			
			for (POSMapping posMapping : posMappingsList) {
				if(posMapping.getCustomFieldName().toLowerCase().startsWith("udf")){
					posMappingMap.put(posMapping.getCustomFieldName().toLowerCase(), posMapping.getDisplayLabel());

				}else{
					
					posMappingMap.put(posMapping.getCustomFieldName(), posMapping.getDisplayLabel());
				}
			}
		}
		
		
		StringBuilder description = new StringBuilder();
		if(coupon.getDiscountCriteria().equals("Total Purchase Amount")) {
			description.append("coupon on receipt;");
			List<CouponDiscountGeneration> coupDisList = coupDiscGenDao.findByCoupon(coupon);
			String eachDiscDesc = "";
			for (CouponDiscountGeneration coupDisGenObj : coupDisList) {
				
				if(eachDiscDesc.length() > 0) eachDiscDesc += "\n";
				eachDiscDesc += " minimum value "+Utility.countryCurrencyMap.get(users.getCountryType())+
						coupDisGenObj.getTotPurchaseAmount().toString()+Constants.DELIMETER_SEMICOLON+
						(coupon.getDiscountType().equals("Value") ? (Utility.countryCurrencyMap.get(users.getCountryType()) +
								coupDisGenObj.getDiscount()):(coupDisGenObj.getDiscount()+"%")) +" off";
				
			}
			description.append(eachDiscDesc);
			if(coupon.getLoyaltyPoints() != null && coupon.getRequiredLoyltyPoits() != null){
				description .append( " \n"+" against "+coupon.getRequiredLoyltyPoits()+
						" of value code: "+coupon.getValueCode() == null ? OCConstants.LOYALTY_TYPE_POINTS : coupon.getValueCode());
				
			}
		}
		else if(coupon.getDiscountCriteria().equals("SKU") ){
			if(coupon.isCombineItemAttributes()) {
				
				List<Object[]> retList = coupDiscGenDao.findDistinctAttrCombos(coupon.getCouponId());
				if(retList != null && !retList.isEmpty()) {
					String comboQuery = Constants.STRING_NILL;
					Double discount = null;
					Double maxDiscount = null;
					String quantity = null;
					List<ItemAttribute> itemAttrLst = new ArrayList<ItemAttribute>();
					StringBuilder attrDesc = new StringBuilder(Constants.STRING_NILL);
					for (Object[] objects : retList) {
						
						String skuAttribute = (String)objects[0];
						String skuAttributeValue = (String)objects[1];
						
						if(discount == null)discount = objects[2] != null ? (Double)objects[2] : null;
						
						if(maxDiscount == null)maxDiscount = objects[3] != null ? (Double)objects[3] : null;
						
						if(quantity == null)quantity = (String)objects[4];
						if(attrDesc.length() > 0 ) attrDesc.append(Constants.DELIMETER_COMMA);
						attrDesc.append(posMappingMap.get(skuAttribute)+Constants.DELIMETER_COLON+skuAttributeValue);
					}
					description .append( attrDesc+Constants.DELIMETER_SEMICOLON+
							(coupon.getDiscountType().equals("Value") ? 
									(Utility.countryCurrencyMap.get(users.getCountryType()) +discount):(discount+"%"))+" off");
					
				if(coupon.getLoyaltyPoints() != null && coupon.getRequiredLoyltyPoits() != null){
					description .append( " \n"+" against "+coupon.getRequiredLoyltyPoits()+
							" of value code: "+coupon.getValueCode() == null ? OCConstants.LOYALTY_TYPE_POINTS : coupon.getValueCode());
					
				}
					
				
				
				}
			
			}else{
				description = new StringBuilder(Constants.STRING_NILL);
				List<String> retList = coupDiscGenDao.findDistinctAttr(coupon.getCouponId());
				List<Double> retDiscList = coupDiscGenDao.findDistinctCoupDiscount(coupon.getCouponId());
				List<CouponDiscountGeneration> coupDisList = new ArrayList<CouponDiscountGeneration>();
				for (String CDGAttr : retList) {

					List<CouponDiscountGeneration> tempcoupDisList = coupDiscGenDao.findBy(coupon.getCouponId(), users.getUserId(), 
							Utility.CDGAttrToSKUMap.containsKey(CDGAttr) ? Utility.CDGAttrToSKUMap.get(CDGAttr) : CDGAttr, CDGAttr);
					if(tempcoupDisList != null && !tempcoupDisList.isEmpty()) {

						coupDisList.addAll(tempcoupDisList);
					}

				}//for
					
					for (Double discount : retDiscList) {
						
						if(description.length() > 0) description.append( "\n");
						Map<String, Set<String>> attributeMap = new HashMap<String, Set<String>>();
						Set<String> atributeValue = null;
						StringBuilder attrDesc = new StringBuilder(Constants.STRING_NILL);
						for (CouponDiscountGeneration coupDisGenObj : coupDisList) {
						

							logger.info("discount :: "+discount+" coupDisGenObj ::"+coupDisGenObj.getDiscount());
							if(discount.doubleValue() != coupDisGenObj.getDiscount().doubleValue()) continue;
							//if(!discount.equals(coupDisGenObj.getDiscount())) continue;
							if(attributeMap.containsKey(coupDisGenObj.getSkuAttribute())){
								
								atributeValue = attributeMap.get(coupDisGenObj.getSkuAttribute());
								if(atributeValue == null) atributeValue = new HashSet<String>();
								
								atributeValue.add(coupDisGenObj.getSkuValue());
							}else{
								
								atributeValue = new HashSet<String>();
								atributeValue.add(coupDisGenObj.getSkuValue());
							}
							attributeMap.put(coupDisGenObj.getSkuAttribute(), atributeValue);
							
							
						}
						
						for (String attribute : attributeMap.keySet()) {
							Set<String> sttrValuesSet = attributeMap.get(attribute);
							for (String attrVal : sttrValuesSet) {
								if(attrDesc.length() > 0 ) attrDesc.append("OR");
								attrDesc.append(posMappingMap.get(attribute)+Constants.DELIMETER_COLON+attrVal);
							}
						}
						
						description.append(attrDesc+Constants.DELIMETER_SEMICOLON+
								(coupon.getDiscountType().equals("Value") ? 
										(Utility.countryCurrencyMap.get(users.getCountryType()) +discount):(discount+"%"))+" off");
						
					
					
				}
					if(coupon.getLoyaltyPoints() != null && coupon.getRequiredLoyltyPoits() != null){
						description.append( " \n"+" against "+coupon.getRequiredLoyltyPoits()+
								" of value code: "+coupon.getValueCode() == null ? OCConstants.LOYALTY_TYPE_POINTS : coupon.getValueCode());
						
					}
					description = new StringBuilder( "coupon on "+description.toString());
			}
				
				
		}//else SKU
		
		
		
		return description.toString();
	}*/
	
	public void onCheck$auxCheckBox() {
		hideShowCheckBox(auxCheckBox, listHeaderId);
	}
	
	public void onCheck$auxCheckBox1() {
		hideShowCheckBox(auxCheckBox1, listHeaderId1);
	}
	
	public void onCheck$auxCheckBox2() {
		hideShowCheckBox(auxCheckBox2, listHeaderId2);
	}
	
	public void onCheck$auxCheckBox3() {
		hideShowCheckBox(auxCheckBox3, listHeaderId3);
	}
	
	public void onCheck$auxCheckBox4() {
		hideShowCheckBox(auxCheckBox4, listHeaderId4);
	}
	
	public void onCheck$auxCheckBox5() {
		hideShowCheckBox(auxCheckBox5, listHeaderId5);
	}
	
	public void onCheck$auxCheckBox6() {
		hideShowCheckBox(auxCheckBox6, listHeaderId6);
	}
	
	public void onCheck$auxCheckBox7() {
		hideShowCheckBox(auxCheckBox7, listHeaderId7);
	}
	
	public void onCheck$auxCheckBox8() {
		hideShowCheckBox(auxCheckBox8, listHeaderId8);
	}
	
	public void onCheck$auxCheckBox9() {
		hideShowCheckBox(auxCheckBox9, listHeaderId9);
	}
	
	public void onCheck$udfcheckbox(Boolean resetcheck) {
		
		//logger.info("udfs===");
		List<Listbox> udfs = getUDFLbs();
		if (udfs != null && !udfs.isEmpty()) {

			for (Listbox listbox : udfs) {

				List<Listitem> items = listbox.getItems();
				Listheader listhead = (Listheader) listbox.getAttribute("listhead");
				Checkbox auxhead = (Checkbox) listbox.getAttribute("auxhead");
				if (!resetcheck) {
					auxhead.setChecked(false);
				}
				//logger.info("listhead.........."+listhead);
				//logger.info("auxhead..........."+auxhead);
				if(auxhead.isChecked()) {
					listhead.getListbox().getItems().stream().forEach(item -> item.setSelected(true));
				}else {
					listhead.getListbox().getItems().stream().forEach(item -> item.setSelected(false));
				}
	}
		}
	}
	
	public void hideShowCheckBox(Checkbox checkbox,Listheader listHeader) {
		if(checkbox.isChecked()) {
			listHeader.getListbox().getItems().stream().forEach(item -> item.setSelected(true));
		}else {
			listHeader.getListbox().getItems().stream().forEach(item -> item.setSelected(false));
		}
	}
	
	public void resetCheckBox() {
		auxCheckBox.setChecked(false);
		hideShowCheckBox(auxCheckBox, listHeaderId);
		auxCheckBox1.setChecked(false);
		hideShowCheckBox(auxCheckBox1, listHeaderId1);
		auxCheckBox2.setChecked(false);
		hideShowCheckBox(auxCheckBox2, listHeaderId2);
		auxCheckBox3.setChecked(false);
		hideShowCheckBox(auxCheckBox3, listHeaderId3);
		auxCheckBox4.setChecked(false);
		hideShowCheckBox(auxCheckBox4, listHeaderId4);
		auxCheckBox5.setChecked(false);
		hideShowCheckBox(auxCheckBox5, listHeaderId5);
		auxCheckBox6.setChecked(false);
		hideShowCheckBox(auxCheckBox6, listHeaderId6);
		auxCheckBox7.setChecked(false);
		hideShowCheckBox(auxCheckBox7, listHeaderId7);
		auxCheckBox8.setChecked(false);
		hideShowCheckBox(auxCheckBox8, listHeaderId8);
		auxCheckBox9.setChecked(false);
		hideShowCheckBox(auxCheckBox9, listHeaderId9);
		onCheck$udfcheckbox(false);
		
		vendorCodeTBId.setValue("");
		deptCodeTBId.setValue("");
		itemCategoryTBId.setValue("");
		dcsTBId.setValue("");
		classTBId.setValue("");
		subClassTBId.setValue("");
		viewSkuTBId.setValue("");
		descriptionTBId.setValue("");
		skuTBId.setValue("");
		listPriceTBId.setValue("");
		
		List<Listbox> udfs = getUDFLbs();
		if (udfs != null && !udfs.isEmpty()) {

			for (Listbox listbox : udfs) {
				Textbox tb = (Textbox) listbox.getAttribute("Textbox");
				tb.setValue("");
			}
		}
		
	}
	
	/*public void discountTypeSettings(){
		
		multiSelCoupDivId.setVisible(false);
		singleSelCoupDivId.setVisible(true);
		if(discountTypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("promotions")){
			coupGenRadioGrId.setSelectedIndex(0);
			
			
		}else if(discountTypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("coupons")){
			coupGenRadioGrId.setSelectedIndex(1);

			
		}else if(discountTypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("loyaltyRewards")){
			coupGenRadioGrId.setSelectedIndex(0);

			
		}
		singRadioId.setDisabled(true);
		multRadioId.setDisabled(true);
	}
	*/
	public void onSelect$productvalueCode2ListboxId(){
		if(productvalueCode2ListboxId.getSelectedItem().getValue().toString().equalsIgnoreCase("Multiplier")){
			multiId.setVisible(true);
		}
		if(productvalueCode2ListboxId.getSelectedItem().getValue().toString().equalsIgnoreCase("Value"))	{
		multiId.setVisible(false);
		}
		
	}
	public void onSelect$discountTypeLbId(){
		// clear the Qty to discount criteria if the disocunt type is being changed.
		Radio selRadio = discountRadioGrId.getSelectedItem()!=null?discountRadioGrId.getSelectedItem():null;
		if (selRadio == null)
			return;
		int index= discountTypeLbId.getSelectedIndex();
		logger.info(""+sessionScope.getAttribute("DISCOUNT_TYPE_INDEX"));
		
		if (selRadio == skuRadioId) {
				List<Component> existingRules = new ArrayList<Component>();
				Div chilVDiv =null;
				existingRules = purIntAttributeDivId.getChildren();
				//har
				int rulesDragged=0;
				for (Object obj : existingRules) {
					
					if(obj instanceof Div) {
						
						//boolean isValid = true;
						for(Object object : ((Div)obj).getChildren()) {
							
							if(object instanceof Div) {
								
								chilVDiv = (Div)object;
								if(chilVDiv.getSclass().contains("drop_")) {
									continue;
								}
								List childDivList = chilVDiv.getChildren();
								for (Object objects : childDivList) {
									if(objects instanceof Div ){
										chilVDiv = (Div)objects;
										
										if(chilVDiv.getSclass().contains("drop_")) {
											continue;
										}
										rulesDragged+=1;
									}
								}
							}
						}
					}
				}
				//har
				if(rulesDragged>1) {
					int confirm = Messagebox.show("Are you sure you want to change the Discount Code Type? \n"
							+ "You may have to re-create the purchase attribute rules.", "Change Discount Code Type",
							Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
					if (confirm != Messagebox.OK) {
						discountTypeLbId.setSelectedIndex(Integer.parseInt(sessionScope.getAttribute("DISCOUNT_TYPE_INDEX").toString()));
					}else {
				for(Component c:existingRules){
					deleteExistedRulesDiv(c);
				}
				SetItemsToBeDragged(2, purchaseAttrLbId);
				SetItemsToBeDragged(1,EligibilityAttrLbId);
				dropflag=true;
				editedRuleRowObj=null;
				editCombinedRuleObj=null;
				}
				}
				existingRules = discountAttributeDivId.getChildren();
				for(Component c:existingRules){
					deleteExistedRulesDiv(c);
				}
				String discountTypeRuleStr="";
				String eligibleItemsRuleStr="";
				discountTypeRuleStr="Discount Type|discount_type|number:"+discountTypeStr+"| ";
				eligibleItemsRuleStr=(discountTypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("loyaltyRewards")
						||discountTypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("purchases")?
								"Eligible Items|no_of_eligible_items|String:highest priced items|null":"Eligible Items|no_of_eligible_items|String:all items|null");
				
				parseRules(discountTypeRuleStr);
				parseRules(eligibleItemsRuleStr);
				
		}
		if(discountTypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("promotions")){
			//coupGenRadioGrId.setSelectedIndex(0); //byshailika
			//productVisibleGBID.setVisible(false);
			//receiptVisibleGBID.setVisible(false);
			purchasesDivId.setVisible(false);
			comparisionLbId.getItemAtIndex(2).setVisible(true);
			loyaltyRewardsDivId.setVisible(false);
			purchasesDivId.setVisible(false);
			loyatyPntshboxId.setVisible(false);
			productvalueCodeListboxId.setVisible(false);
			useasreferralId.setVisible(false);
			
		}else if(discountTypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("coupons")){
			//coupGenRadioGrId.setSelectedIndex(1); //byshailika
			//productVisibleGBID.setVisible(false);
			//receiptVisibleGBID.setVisible(false);
			useasreferralId.setVisible(true);
			purchasesDivId.setVisible(false);
			comparisionLbId.getItemAtIndex(2).setVisible(true);
			loyaltyRewardsDivId.setVisible(false);
			purchasesDivId.setVisible(false);
			loyatyPntshboxId.setVisible(false);
			productvalueCodeListboxId.setVisible(false);
			
		}else if(discountTypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("loyaltyRewards")){
			//coupGenRadioGrId.setSelectedIndex(0); //byshailika
			if(discountRadioGrId.getSelectedItem().getValue().toString().equalsIgnoreCase("SKU")){
				//productVisibleGBID.setVisible(true);
				//receiptVisibleGBID.setVisible(false);
				productvalueCode2ListboxId.setDisabled(true);
				useasreferralId.setVisible(false);
				loyaltyRewardsDivId.setVisible(true);
				purchasesDivId.setVisible(false);
				comparisionLbId.getItemAtIndex(2).setVisible(false);
				logger.info("index 2==="+comparisionLbId.getItemAtIndex(2));
				logger.info("index 3==="+comparisionLbId.getItemAtIndex(3));

				
				

			}else{
				//productVisibleGBID.setVisible(false);
				//receiptVisibleGBID.setVisible(true);
				productvalueCode2ListboxId.setDisabled(false);
				purchasesDivId.setVisible(false);
				loyaltyRewardsDivId.setVisible(true);
				useasreferralId.setVisible(false);



			}
			loyatyPntshboxId.setVisible(true);
			productvalueCodeListboxId.setVisible(true);
		}else if(discountTypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("purchases")){
			//coupGenRadioGrId.setSelectedIndex(0); //byshailika
			//productVisibleGBID.setVisible(true);
			//receiptVisibleGBID.setVisible(false);
			useasreferralId.setVisible(false);
			loyaltyRewardsDivId.setVisible(false);
			purchasesDivId.setVisible(true);
			comparisionLbId.getItemAtIndex(2).setVisible(false);
			loyatyPntshboxId.setVisible(false);
			productvalueCodeListboxId.setVisible(false);

		}
		
	//	onCheck$coupGenRadioGrId(); //byshailika
	//	singRadioId.setDisabled(true); //byshailika
	//	multRadioId.setDisabled(true); //byshailika
		
		
		sessionScope.setAttribute("DISCOUNT_TYPE", discountTypeLbId.getSelectedItem().getValue().toString());
		sessionScope.setAttribute("DISCOUNT_TYPE_INDEX", discountTypeLbId.getSelectedIndex());
	}
	public void onClick$submitBtnId(){

		//discountCodeGenerationGBId.setVisible(true);
	}
	
	public void onClick$clearBtnId(){
		rulesGBId.setVisible(false);
		rulesGBIdNew.setVisible(false);
	//	discountCodeGenerationGBId.setVisible(false);
		couponNameTxtBxId.setText("");
		discountTypeLbId.setSelectedIndex(0);
		tpaRadioId.setSelected(true);
		discountedItemsCoupChkBxId.setChecked(false);
		excludeDiv.setVisible(false);
		

		editCouponObj = (Coupons) sessionScope.getAttribute("EDIT_COUPON_OBJECT");
		if (editCouponObj != null) {
			tempCouponObj = editCouponObj;
			editCouponSetting(editCouponObj);
			couponBasicSettingsFirstId.setSclass("req_step_current");
			if(editCouponObj.getCouponGeneratedType()!= null && !editCouponObj.getCouponGeneratedType().equals("Draft")) {
				if(editCouponObj.getCouponGeneratedType().trim().equals(Constants.COUP_GENT_TYPE_SINGLE)){
					if((editCouponObj.getCouponCode()==null || editCouponObj.getCouponCode().isEmpty())){
						promotionRulesSecondId.setSclass("req_step_incomplete");
					}else {
						promotionRulesSecondId.setSclass("req_step_completed");
					}
				}else {
					if(editCouponObj.getTotalQty() == null) {
						promotionRulesSecondId.setSclass("req_step_incomplete");
					}else {
						promotionRulesSecondId.setSclass("req_step_completed");
					}
				}
			}else {
				promotionRulesSecondId.setSclass("req_step_incomplete");
			}
			if (editCouponObj.isEnableOffer()) {
				mobileOfferThirdId.setVisible(true);
				if((editCouponObj.getOfferHeading()==null || editCouponObj.getOfferHeading().isEmpty())
						&& (editCouponObj.getOfferDescription() == null || editCouponObj.getOfferDescription().isEmpty())) {
					mobileOfferThirdId.setSclass("req_step_incomplete");
					mobileOfferThirdId.setStyle("margin-left: 3px;background-color: #dfdfdf;width:204px !important;");
				}else {
					mobileOfferThirdId.setStyle("margin-left: 3px;background-color: #c5e3a5;width:204px !important;");
					mobileOfferThirdId.setSclass("req_step_completed");
				}
			} 	
			
			CouponDescriptionAlgorithm coupoDes = new CouponDescriptionAlgorithm();
			oldDesc = coupoDes.preparecouponDisc(editCouponObj, users);
			
			
			//byshailika
			/*if(editCouponObj.getLoyaltyPoints() != null && editCouponObj.getRequiredLoyltyPoits() != null){
				coupGenRadioGrId.setSelectedIndex(0);
				singRadioId.setChecked(true);
				multRadioId.setDisabled(true);
				singRadioId.setDisabled(true);
		
			}*/

			
			onClick$validateButtonId();
		} else {
			nextCoupBtnId.setVisible(false);
			defaultSettings();

		}
		
	}
	
	/*public void editQuantityAndDiscount(){
		//Listitem li = (Listitem) lc.getParent();
		
		
		itemEditWindow.setVisible(true);
		itemEditWindow.setPosition("center");
		itemEditWindow.doHighlighted();
		//itemEditWindow.setAttribute("COUPON_OBJ", couponObj);
		 
	}
	*/
	public void onClick$submitInEdit$itemEditWindow(){
		
		Double discount=itemEditWindow$discountDblBxId.getValue();
		Coupons couponObj=(Coupons) itemEditWindow.getAttribute("COUPON_OBJ");
		//couponObj.setTotDiscount(totDiscount);
		//=itemEditWindow$quantityDblBxId.getValue();
		
		
		
	}
	
	public void dummyMethod(){
		Row tempRow = null;
		Image tempImg = null;
		

		tempRow = (Row) tempImg.getParent().getParent();
		Label keyLabel = (Label) tempRow.getChildren().get(0);
		Label typeLabel = (Label) tempRow.getChildren().get(1);
		String limit = tempRow.getValue();
		// List<CouponDiscountGeneration> deleteDiscountList=new ArrayList<>();
		if (skuRadioId.isChecked()) {
			String type = (String) typeLabel.getAttribute("Type");
			Label valueLabel = (Label) tempRow.getChildren().get(2);
			
			//if(multipleSingleId.isChecked()){
			if(ruleType.equalsIgnoreCase(multipleSingle)){
				if (valueLabel.getValue().equals("--All Items--"))
					coupDiscGenDaoForDML.deleteTempSkuBy(users.getUserId(), type, keyLabel.getValue(), null,
							tempCouponObj != null ? tempCouponObj.getCouponId() : null);
				else
					coupDiscGenDaoForDML.deleteTempSkuBy(users.getUserId(), type, keyLabel.getValue(), limit,
							tempCouponObj != null ? tempCouponObj.getCouponId() : null);
			}//else if(oneCombinationId.isChecked()){
			if(ruleType.equalsIgnoreCase(combined)){
				if(type!=null){
				if(type.contains(",")){
					String[] typeArr = type.split(",");
					for(String typeONE : typeArr){
						coupDiscGenDaoForDML.deleteTempSkuBy(users.getUserId(), typeONE.trim(), keyLabel.getValue(), limit,
								tempCouponObj != null ? tempCouponObj.getCouponId() : null);
					}
				}else{
					coupDiscGenDaoForDML.deleteTempSkuBy(users.getUserId(), type.trim(), keyLabel.getValue(), limit,
							tempCouponObj != null ? tempCouponObj.getCouponId() : null);
				}
				}
				
			}
			
			
		} else {
			coupDiscGenDaoForDML.deleteTempSkuBy(users.getUserId(), keyLabel.getValue(),
					typeLabel.getValue().equals("N/A") ? "0" : typeLabel.getValue(), null);
			
		}

		
		discountGenRowsId.removeChild(tempRow);

		enableDisableDiscountCrtiteriaOptions();
		// selectAllSKUBtnId.setAttribute("selectedAll", false);
		deSelectSkuMap.clear();
	}
	
	public void SetItemsToBeDragged(int category, Listbox targetLb) {
		String scope = null;
		String draggble = "";
		
		
		//boolean isMoreFormsFlag = (mappingsList != null && mappingsList.size() > 0)
		
			if(category == 3) {
			
				draggble = "DisSegment";
			List<CouponsEnum> purchaseAttrList = CouponsEnum.getEnumsByCategoryCode(category);
			
			Listitem item = null;
			
			Components.removeAllChildren(targetLb);
			
			for (CouponsEnum purchaseSegEnum : purchaseAttrList) {
				if(tpaRadioId.isChecked() && purchaseSegEnum.getDispLabel().equalsIgnoreCase("Eligible Items")) continue;
				item = new Listitem(purchaseSegEnum.getDispLabel(), purchaseSegEnum);
				logger.debug("DECCCCCCCCCC  "+purchaseSegEnum.getDispLabel());
				logger.debug("item  "+item);
				item.setDraggable("DisSegment");
				item.setStyle("padding:10px;");
				item.setParent(targetLb);
				item.setAttribute("DBcolumnName", purchaseSegEnum.getColumnName());
				item.setAttribute("item", purchaseSegEnum.getItem());
				logger.debug("purchaseSegEnum.getItem() ::"+purchaseSegEnum.getItem());
			}
			
			return;
		} 
			if(category==1)
			{
		//		draggble = "purIntSegment1";
				
				
				Listitem item = null;
				
				Components.removeAllChildren(targetLb);
				List<CouponsEnum> purchaseAttrList = CouponsEnum.getEnumsByCategoryCode(category);
				for (CouponsEnum purchaseSegEnum : purchaseAttrList) {
					//if(tpaRadioId.isChecked() && purchaseSegEnum.getDispLabel().equalsIgnoreCase("Eligible Items")) continue;
					item = new Listitem(purchaseSegEnum.getDispLabel(), purchaseSegEnum);
					item.setDraggable("purInt1Segment");
					item.setStyle("padding:10px;");
					item.setParent(targetLb);
					item.setAttribute("DBcolumnName", purchaseSegEnum.getColumnName());
					item.setAttribute("item", purchaseSegEnum.getItem());
					item.setId(purchaseSegEnum.getItem());
					logger.debug("purchaseSegEnum.getItem() ::"+purchaseSegEnum.getItem());
				}
				
				return;
			} 
			
		if(category == 2 ) {
			
			draggble = "purIntSegment";
			//scope = "'"+Constants.POS_MAPPING_TYPE_SALES+"','"+Constants.POS_MAPPING_TYPE_SKU+"'";
			scope = "'"+Constants.POS_MAPPING_TYPE_SKU+"'";

		}
		
		POSMappingDao posMappingDao = (POSMappingDao)SpringUtil.getBean("posMappingDao");
		
		//for sales(purchase)
		List<CouponsEnum> purchaseAttrList = CouponsEnum.getEnumsByCategoryCode(category);
		
		List<POSMapping> posMappingsList = posMappingDao.findByType(scope, users.getUserId());
		logger.debug("posMappingsList size::"+posMappingsList.size());
		
		CouponsEnum UDFCouponsEnum = null;
		CouponsEnum valueCodeCouponsEnum = null;
		Listitem item = null;
		Listitem listItem = null;
		
		Components.removeAllChildren(targetLb);
		for (POSMapping posMapping : posMappingsList) {
			itemAttributesMap.put(posMapping.getCustomFieldName(),posMapping.getDisplayLabel());
		}
		for (CouponsEnum purchaseSegEnum : purchaseAttrList) {
			
			//hide itemAttr for receipt type
			if(tpaRadioId.isChecked() && (purchaseSegEnum.getDispLabel().equalsIgnoreCase("Item Attribute")
					||purchaseSegEnum.getDispLabel().equalsIgnoreCase("Item Price"))) continue;
			item = new Listitem(purchaseSegEnum.getDispLabel(), purchaseSegEnum);
			
			if(genFieldSKUMap.containsKey(purchaseSegEnum.getDispLabel())){
			
			item.setDraggable(draggble);
			item.setStyle("padding:10px;");
			item.setParent(targetLb);
			item.setAttribute("DBcolumnName", purchaseSegEnum.getColumnName());
			item.setAttribute("item",purchaseSegEnum.getItem());
			logger.debug("purchaseSegEnum.getItem() 1::"+purchaseSegEnum.getItem());
			for (POSMapping posMapping : posMappingsList) {
				
				if(purchaseSegEnum.isMappingField() == true) {
					
					if( purchaseSegEnum.getPosCustomFieldLbl() != null && 
							purchaseSegEnum.getPosCustomFieldLbl().equals(posMapping.getCustomFieldName()) &&
							posMapping.getOptionalValues() != null) {
						
						item.setAttribute("optionalValues", posMapping.getOptionalValues());
						break;
						
					}//if
					
				}//if
				
			}//for
			
		  }//if
			
	}//for
		
		
for (POSMapping posMapping : posMappingsList) {
			
			if(posMapping.getCustomFieldName().trim().toLowerCase().startsWith("udf")) {
				 UDFCouponsEnum = null; 
				/*if(posMapping.getDataType().toLowerCase().startsWith("date")) {
					UDFCouponsEnum = CouponsEnum.UDF_DATE;
				}
				else if(posMapping.getDataType().equalsIgnoreCase("String")) {
					UDFCouponsEnum = CouponsEnum.UDF_STRING;
				}else if(posMapping.getDataType().equalsIgnoreCase("number") || posMapping.getDataType().equalsIgnoreCase("Double")) {
					UDFCouponsEnum = CouponsEnum.UDF_NUMBER;
				}*/
				
				String cat = "";
				if(posMapping.getMappingType().equals(Constants.POS_MAPPING_TYPE_SALES)) {
					cat = "sal."+posMapping.getCustomFieldName().trim().toLowerCase();
				}
				
				else if(posMapping.getMappingType().equals(Constants.POS_MAPPING_TYPE_SKU)) {
					cat = "sku."+posMapping.getCustomFieldName().trim().toLowerCase();
				}
				
				else if(posMapping.getMappingType().equals(Constants.POS_MAPPING_TYPE_CONTACTS)) {
					cat = "c."+posMapping.getCustomFieldName().trim().toLowerCase();
				}
				
				
				if(posMapping.getDataType().toLowerCase().startsWith("date")) {
					cat = "DATE("+cat+")";
				}//if
				
				if(UDFCouponsEnum != null) {
					item = new Listitem(posMapping.getDisplayLabel(), UDFCouponsEnum);
					item.setDraggable(draggble);
					item.setAttribute("DBcolumnName", cat);
					item.setAttribute("item", posMapping.getDisplayLabel());
					item.setStyle("padding:10px;");
					item.setParent(targetLb);
					if(posMapping.getOptionalValues() != null) {
						item.setAttribute("optionalValues", posMapping.getOptionalValues());
					}
				}
			}//if
			
		} // for
		/*if (posMappingsList != null) {
			for (POSMapping obj : posMappingsList) {
				if (genFieldSKUMap.containsKey(obj.getCustomFieldName())) {

					listItem = new Listitem(obj.getDisplayLabel(), UDFCouponsEnum);
					listItem.setDraggable(draggble);
					//item.setAttribute("DBcolumnName", cat);
					listItem.setAttribute("item", obj.getDisplayLabel());
					listItem.setStyle("padding:10px;");
				//	listItem.setValue(obj.getCustomFieldName());
					listItem.setLabel(obj.getDisplayLabel());
				//	listItem.setSelected(true);
					listItem.setAttribute("column", genFieldSKUMap.get(obj.getCustomFieldName()).split("::")[0]);
					listItem.setCheckable(true);
					listItem.setParent(targetLb);
					
					String type = listItem.getValue().toString().equals("Vendor Code")?"Vendor":
						listItem.getValue().toString().equals("Department Code") ? "Department"
							: listItem.getValue().toString().equals("Sub-Class") ? "Subclass" : listItem.getValue();
						
					//headerMap.put(listItem.getIndex() + 1+"", type);
				} else {
					String column = obj.getCustomFieldName().trim().toLowerCase();
					if (column.startsWith("udf")) {
					//	listItem = new Listitem();
						// listItem.setValue(obj.getCustomFieldName().trim().toLowerCase()+"");
						listItem = new Listitem(obj.getDisplayLabel(), UDFCouponsEnum);
						listItem.setDraggable(draggble);
						//item.setAttribute("DBcolumnName", cat);
						listItem.setAttribute("item", obj.getDisplayLabel());
						listItem.setStyle("padding:10px;");
					//	listItem.setValue(obj.getCustomFieldName().trim().toLowerCase() + "");
						listItem.setLabel(obj.getDisplayLabel());
						listItem.setCheckable(true);
					//	listItem.setSelected(true);
						listItem.setAttribute("column", column);
						listItem.setParent(targetLb);
						headerMap.put(listItem.getIndex() + 1+"", listItem.getValue());
						// genFieldSKUMap.put(obj.getCustomFieldName(), );
					} 
				}
			}
		}*/
		
		
		/*for (POSMapping posMapping : posMappingsList) {
			
			if(posMapping.getCustomFieldName().trim().toLowerCase().startsWith("udf")) {
				 UDFCouponsEnum = null; 
				if(posMapping.getDataType().toLowerCase().startsWith("date")) {
					UDFCouponsEnum = CouponsEnum.UDF_DATE;
				}
				else if(posMapping.getDataType().equalsIgnoreCase("String")) {
					UDFCouponsEnum = CouponsEnum.UDF_STRING;
				}else if(posMapping.getDataType().equalsIgnoreCase("number") || posMapping.getDataType().equalsIgnoreCase("Double")) {
					UDFCouponsEnum = CouponsEnum.UDF_NUMBER;
				}
				
				String cat = "";
				if(posMapping.getMappingType().equals(Constants.POS_MAPPING_TYPE_SALES)) {
					cat = "sal."+posMapping.getCustomFieldName().trim().toLowerCase();
				}
				
				else if(posMapping.getMappingType().equals(Constants.POS_MAPPING_TYPE_SKU)) {
					cat = "sku."+posMapping.getCustomFieldName().trim().toLowerCase();
				}
				
				else if(posMapping.getMappingType().equals(Constants.POS_MAPPING_TYPE_CONTACTS)) {
					cat = "c."+posMapping.getCustomFieldName().trim().toLowerCase();
				}
				
				
				if(posMapping.getDataType().toLowerCase().startsWith("date")) {
					cat = "DATE("+cat+")";
				}//if
				
				if(UDFCouponsEnum != null) {
					item = new Listitem(posMapping.getDisplayLabel(), UDFCouponsEnum);
					item.setDraggable(draggble);
					//item.setAttribute("DBcolumnName", cat);
					item.setAttribute("item", posMapping.getDisplayLabel());
					item.setStyle("padding:10px;");
					item.setParent(targetLb);
					if(posMapping.getOptionalValues() != null) {
						item.setAttribute("optionalValues", posMapping.getOptionalValues());
					}
				}
			}//if
			
		} // for */
		
		
	}
	
		
		
	
	private List<FormMapping> mappingsList;
	
	boolean dropflag=true;
	
	boolean flag = true;
	public void onDrop$EligibilityAttributeDivId(Event event)  
	{
		logger.debug("---ControllerSide -- Drop Event =" + event);

		DropEvent dropEvent = ((DropEvent) ((ForwardEvent) event).getOrigin());
		Component dragged = dropEvent.getDragged();

		logger.debug("----------Dragged target =" + dragged);
		 programsList = loyaltyProgramDao.getAllProgramsListByUserId(GetUser.getUserId());
		 if(programsList==null){
			 MessageUtil.setMessage("Please first create a Loyalty Program to use this attribute.", "color:red");
			return;
	}
		if (dragged instanceof Listitem) {
			// new object
			Listitem item = (Listitem) dragged;
			// final Listitem itemForChild = item;
		//	SpecialRewardEnum segmentEnum = (SpecialRewardEnum) item.getValue();
			CouponsEnum couponsenum=(CouponsEnum) item.getValue();
			List<Component> list = EligibilityAttributeDivId.getChildren();
			if (list.size() > 1) {
				for (Component comp : list) {
					if (comp.getLastChild() instanceof Label)
						continue;
				if(comp.getLastChild()!=null) {
					Div div = (Div) comp.getLastChild().getFirstChild();
					String draggedEnum = (String) div.getAttribute("Type");
				
					List<Component> innerlist= comp.getLastChild().getChildren();
					for(Component innercomp:innerlist){
						if(innercomp instanceof Div){
							Div innerDiv=(Div) innercomp;
							String innerEnum=(String) innerDiv.getAttribute("Type");
						 if (
								couponsenum.name().equals(CouponsEnum.PURCHASE_IN_TIER.name())
								 || couponsenum.name().equals(CouponsEnum.PURCHASE_IN_CARD_SET.name())){
						   if(innerEnum!=null && (innerEnum.equals(couponsenum.name())|| draggedEnum.equals(couponsenum.name()))){
							   MessageUtil.setMessage("This attribute can't be clubbed with AND conjuntion.", "color:red");
								return;
						   }
						 }
						}
				
						
					  }
					}
					
					
				}
			}
			if (flag && couponsenum.name().equals(CouponsEnum.ITEM_PURCHASE_ATTRIBUTE.name())) {
				createSubDivRule1();
				flag = false;
			}

			Div tempDiv = new Div();
			tempDiv.setSclass("");
			tempDiv.setParent(EligibilityAttributeDivId);

			Label AndLabel = new Label("AND");
			AndLabel.setStyle("display:inline-block; width:20px; padding:0 5px;margin:0 10px;font-weight:bold;");
			AndLabel.setParent(tempDiv);

			Div vdiv = new Div();// this is the outer component
			vdiv.setDroppable("purInt1Segment");
			vdiv.setParent(tempDiv);
			vdiv.setSclass("segment_parent_div");
			updateAndLabel(EligibilityAttributeDivId);
			Div obj = createNewRuleDiv1(item);
			obj.setParent(vdiv);
			updateORLabel(vdiv);

			// Generate Drop Div
			if (couponsenum.name().equals(CouponsEnum.ITEM_PURCHASE_ATTRIBUTE.name())
					)
				vdiv.setDroppable("notDraggable");
			else
				vdiv.appendChild(createDropORDiv("Drag Attribute(s) here to create OR combination rule."));

			vdiv.addEventListener("onDrop", myDropListener);
		}

	} // onDrop$programdesignerWinId

	
	private MyDropEventListener myDropListener = new MyDropEventListener();
	private class MyDropEventListener implements EventListener {
		
		
		public MyDropEventListener() {}
		@Override
		public void onEvent(Event event) throws Exception {
			DropEvent dropEvent = (DropEvent) event;
			Component dragged = dropEvent.getDragged();
			if (dragged instanceof Listitem) {
				Listitem item = (Listitem) dragged;
				Div obj = (Div) event.getTarget(); 
				Div testDiv = (Div) obj.getFirstChild();
				CouponsEnum dragEnum = (CouponsEnum) item.getValue();
		
				List<Component> list = EligibilityAttributeDivId.getChildren();
				 boolean isStoreDragged=false;
				 boolean isTierDragged=false;
				 boolean iscardSetDragged =false;
				 boolean isPurchaseDateDragged=false;
				if (list.size() > 1) {
					for (Component comp : list) {
						if (comp.getLastChild() instanceof Label)
							continue;
						if(comp.getLastChild()!=null) {
						Div div = (Div) comp.getLastChild().getFirstChild();
						String outerDraggedEnum = (String) div.getAttribute("Type");
					
						
						if(outerDraggedEnum!=null && (outerDraggedEnum.equals(CouponsEnum.PURCHASE_IN_TIER.name()))){ 
							isTierDragged=true;
						}
						else if(outerDraggedEnum!=null && (outerDraggedEnum.equals(CouponsEnum.PURCHASE_IN_CARD_SET.name()))){ 
							iscardSetDragged=true;
						}
					
						}	
					}
				}
					
		
				 if ( isTierDragged && (dragEnum.name().equals(CouponsEnum.PURCHASE_IN_TIER.name()))) {
					if (testDiv.getAttribute("Type") != null && 
							testDiv.getAttribute("Type").toString().equals(CouponsEnum.PURCHASE_IN_TIER.name())) {
						Div div = createNewRuleDiv1(item);// elements div
						div.setParent(obj);
					} else {
						MessageUtil.setMessage("This attribute can't be clubbed with OR conjuntion.", "color:red");
						return;
					}
				}
				else if ( iscardSetDragged && (dragEnum.name().equals(CouponsEnum.PURCHASE_IN_CARD_SET.name()))) {
					if (testDiv.getAttribute("Type") != null
							&& testDiv.getAttribute("Type").toString().equals(CouponsEnum.PURCHASE_IN_CARD_SET.name())) {
						Div div = createNewRuleDiv1(item);// elements div
						div.setParent(obj);
					} else {
						MessageUtil.setMessage("This attribute can't be clubbed with OR conjuntion.", "color:red");
						return;
					}
				}
			
				else {
					MessageUtil.setMessage("This attribute can't be clubbed with OR conjuntion.", "color:red");
					return;
				}
				}
			
		}//onEvent()
		
	}//class

public Div createNewRuleDiv1(Listitem item) {
		
		int index = item.getIndex();
		
		CouponsEnum couponsEnum = (CouponsEnum)item.getValue();
	
		return getCreatedCompDiv1(couponsEnum, item);
		
		
	}
private Div createDropORDiv(String dragMsg) {
	Div dropDiv = new Div();
	dropDiv.setSclass("drop_or_div");
	Label orLbl = new Label(" OR ");
	orLbl.setStyle("left: -80px; position: absolute;");
	dropDiv.appendChild(orLbl);
	dropDiv.appendChild(new Label(dragMsg));

	return dropDiv;
}

int count = 0;
@SuppressWarnings("unchecked")
private List<LoyaltyProgram> programsList;
public Div getCreatedCompDiv1(CouponsEnum couponsEnum, Listitem item)
{
	List<Listitem> ItemList = EligibilityAttrLbId.getItems();
	
	Listitem Li = null;// for rulelb
	Div testDiv = new Div();
	testDiv.setSclass("segment_child_div");
	testDiv.setAttribute("Type", couponsEnum.name());
	Label orlbl = new Label(" OR ");// or label-----0
	orlbl.setStyle("display:inline-block; width:20px; padding:0 5px;margin:0 10px;font-weight:bold;");
	orlbl.setParent(testDiv);

	Label lbl = new Label();// display label-----1
	lbl.setStyle("padding:0 5px; display:inline-block; width:120px;");

	lbl.setParent(testDiv);
	lbl.setValue(item.getLabel());
	lbl.setAttribute("columnName", (String) item.getAttribute("DBcolumnName"));
	lbl.setAttribute("item", (String) item.getAttribute("item"));

	Listbox rulelb = new Listbox();// rule lb----2
	rulelb.setMold("select");
	rulelb.setStyle("margin-right:5px; width:200px;");
	rulelb.setParent(testDiv);

	Label lbl1 = new Label();
	lbl1.setWidth("20px");
	lbl1.setParent(testDiv);

	final Textbox tb = new Textbox();// string type value--------6
	tb.setStyle("margin-right:5px;");
	tb.setWidth("100px");
	tb.setVisible(false);
	tb.setParent(testDiv);


	rulelb.setAttribute("isTier", false);
	if (couponsEnum.name().equals(CouponsEnum.PURCHASE_IN_TIER.name()))
		rulelb.setAttribute("isTier", true);

	final Listbox operatorlb = new Listbox();// rule options lb----------5
	operatorlb.setMold("select");
	operatorlb.setStyle("margin-right:5px;");
	operatorlb.setParent(testDiv);
	operatorlb.setVisible(false);

	final Label label = new Label();
	label.setVisible(false);
	label.setParent(testDiv);

	final Listbox numberLb = new Listbox();// days/weeks/mnths------4
	numberLb.setMold("select");

	numberLb.setStyle("margin-right:5px;");

	for (int i = 1; i <= 30; i++) {
		Li = new Listitem("" + i);
		Li.setParent(numberLb);
	}
	numberLb.setVisible(false);

	numberLb.setParent(testDiv);
	final Textbox TextBox = new Textbox();// string type value--------6
	TextBox.setStyle("margin-right:5px;");
	TextBox.setWidth("100px");
	TextBox.setParent(testDiv);
	TextBox.setVisible(false);

	final Decimalbox dblBox1 = new Decimalbox();// number type
												// value1------------7
	dblBox1.setStyle("margin-right:5px;");
	dblBox1.setParent(testDiv);
	dblBox1.setVisible(false);

	final Decimalbox dblBox2 = new Decimalbox();// number type
												// value2------------8
	dblBox2.setStyle("margin-right:5px;");
	dblBox2.setParent(testDiv);

	dblBox2.setVisible(false);

	SimpleDateFormat Format = new SimpleDateFormat("yyyy-MM-dd");

	final Datebox db1 = new MyDatebox();// date type value1------------9
	db1.setStyle("margin-right:10px;");
	db1.setFormat(Format.toPattern());
	db1.setReadonly(true);
	db1.setVisible(false);
	db1.setParent(testDiv);

	final Datebox db2 = new MyDatebox();// date type value2------------10
	db2.setStyle("margin-right:10px;");
	db2.setParent(testDiv);
	db2.setReadonly(true);
	db2.setFormat(Format.toPattern());
	db2.setVisible(false);

	Toolbarbutton deleteIcon = new Toolbarbutton();// -------13 (14)
	deleteIcon.setImage("/images/action_delete.gif");
	deleteIcon.setStyle("float:right; margin-right:25px;");
	deleteIcon.setAttribute("Type", couponsEnum.name());
	
		deleteIcon.setParent(testDiv);
	List<String> posAttributeList =new ArrayList<String>(); 
	List<CouponsEnum> RuleEnums = couponsEnum.getChidrenByParentEnum(couponsEnum);// to
																						// prepare
																						// rule
																						// lb
	if (couponsEnum.name().equals(CouponsEnum.ITEM_PURCHASE_ATTRIBUTE.name())) {//Changes posAttributes to be shown.
		RuleEnums = couponsEnum.getChidrenByParentEnumForPurchaseItem(couponsEnum);
		posAttributeList = couponsEnum.getChidrenByParentEnumForPurchaseItemForString(couponsEnum);
	}
	int counter = 0;
	if(!posAttributeList.isEmpty() && couponsEnum.name().equals(CouponsEnum.ITEM_PURCHASE_ATTRIBUTE.name())) {
		CouponsEnum enumUDF = null;
		for (CouponsEnum enums : RuleEnums) {
			Li = new Listitem(posAttributeList.get(counter), enums);
			Li.setParent(rulelb);
				logger.info("counter===>"+counter+"==="+posAttributeList.get(counter));
				counter++;
		}

	} 
	else 
		for (CouponsEnum enums : RuleEnums) {
			Li = new Listitem(enums.getDispLabel(), enums);
			Li.setParent(rulelb);
			counter++;
			logger.info("counter===>"+counter+"==="+enums.getDispLabel());
		
	}


	

	
	CouponsEnum oprEnum = null;

	if (couponsEnum.name().equals(couponsEnum.PURCHASE_IN_TIER.name())
||couponsEnum.name().equals(couponsEnum.PURCHASE_IN_CARD_SET.name())) {
		
		oprEnum = couponsEnum.getChidrenByParentEnum(couponsEnum).get(0);
		 programsList = loyaltyProgramDao.getAllProgramsListByUserId(GetUser.getUserId());
		 logger.debug(programsList);
		 if(programsList!=null) {
		programsList.forEach(program -> {
			Listitem itm = new Listitem(program.getProgramName(), program.getProgramId());
			itm.setParent(rulelb);
			logger.debug(itm);
			
		});
		 
		List<CouponsEnum> childEnumList = couponsEnum.getChidrenByParentEnum(oprEnum);
		for (CouponsEnum en : childEnumList) {
			Listitem listitem = new Listitem(en.getDispLabel(), en);
			listitem.setParent(operatorlb);
		}
		operatorlb.setVisible(true);
		rulelb.setAttribute("Type", "Program");
		}
	else if(couponsEnum.name().equals(couponsEnum.PURCHASE_IN_TIER.name())
			||couponsEnum.name().equals(couponsEnum.PURCHASE_IN_CARD_SET.name()) && programsList!=null ) {
		oprEnum = couponsEnum.getChidrenByParentEnum(couponsEnum).get(0);
		 programsList = loyaltyProgramDao.getAllProgramsListByUserId(GetUser.getUserId());
		programsList.forEach(program -> {
			Listitem itm = new Listitem(program.getProgramName(), program.getProgramId());
			itm.setParent(rulelb);
			logger.debug(itm);
			rulelb.setVisible(false);
			
		});
		List<CouponsEnum> childEnumList = couponsEnum.getChidrenByParentEnum(oprEnum);
		for (CouponsEnum en : childEnumList) {
			Listitem listitem = new Listitem(en.getDispLabel(), en);
			listitem.setParent(operatorlb);
		}
		operatorlb.setVisible(true);
	//	rulelb.setAttribute("Type", "Program");
		
		
	} 
	else {
		oprEnum = couponsEnum.getChidrenByParentEnum(couponsEnum).get(0);
		List<CouponsEnum> childEnumList = couponsEnum.getChidrenByParentEnum(oprEnum);
		if (childEnumList != null && childEnumList.size() > 0) {
			for (CouponsEnum en : childEnumList) {
				Listitem listitem = new Listitem(en.getDispLabel(), en);
				listitem.setParent(operatorlb);
			}
			operatorlb.setVisible(true);
		}
	}

//	else {
		if (rulelb.getItemCount() > 0)
			rulelb.setSelectedIndex(0);
		CouponsEnum TempEnum = null;
		if (operatorlb.isVisible() && operatorlb.getItemCount() > 0) {
			operatorlb.setSelectedIndex(0);
			TempEnum = (CouponsEnum) operatorlb.getSelectedItem().getValue();
		} else {
			TempEnum = (CouponsEnum) rulelb.getSelectedItem().getValue();
		} 
		if (TempEnum != null) {
			String Type = TempEnum.getType();
			if (Type.equalsIgnoreCase("date")) {

				dblBox1.setVisible(false);
				dblBox2.setVisible(false);
				TextBox.setVisible(false);

				createValuesForOperatorForTypeDate1(db1, db2, numberLb, TempEnum);
			}
			  if (Type.equalsIgnoreCase("string") || Type.equalsIgnoreCase("json")) {

				numberLb.setVisible(false);
				db1.setVisible(false);
			//	Db1.setVisible(false);
				dblBox1.setVisible(false);
				dblBox2.setVisible(false);
				createValuesForOperatorTypeSting1(TextBox, TempEnum);

			} else if (Type.equalsIgnoreCase("number")) {

				numberLb.setVisible(false);
				db1.setVisible(false);
			//	Db1.setVisible(false);
				TextBox.setVisible(false);
				createValuesForOperatorTypeNumber(dblBox1, dblBox2, TextBox, TempEnum, storeNumbCmboBxId, storeNumbCmboBxId);

			}
		} 
//	}
		
	deleteIcon.addEventListener("onClick", new EventListener() {
				@Override
			
				public void onEvent(Event event) throws Exception {
					
					Toolbarbutton deleteIcon = (Toolbarbutton) event.getTarget();
					Component componentsDiv = deleteIcon.getParent();
					Component rulesDiv = componentsDiv.getParent();
					Component ruleParent = rulesDiv.getParent();
					Component parent = ruleParent.getParent();
					
							List<Component> list = EligibilityAttributeDivId.getChildren();
							for (Component comp : list) {
								Div tempDiv = (Div) comp;
								if (tempDiv.getAttribute("Type") != null && (tempDiv.getAttribute("Type").toString()
										.equals(SpecialRewardEnum.ITEM_PURCHASE_QUANTIY.name()))) {
									EligibilityAttributeDivId.removeChild(tempDiv);
									flag = true;
									break;
								}
							}

						

					
					rulesDiv.removeChild(componentsDiv);
					if (rulesDiv.getChildren().size() == 0) {

						rulesDiv.getParent().removeChild(rulesDiv);
						ruleParent.getParent().removeChild(ruleParent);
					} else if (rulesDiv.getChildren().size() == 1) {

						logger.info("((Div)rulesDiv.getFirstChild()).getSclass() ::"
								+ ((Div) rulesDiv.getFirstChild()).getSclass());
						if (((Div) rulesDiv.getFirstChild()).getSclass().equalsIgnoreCase("drop_or_div")) {

							rulesDiv.getParent().removeChild(rulesDiv);
							ruleParent.getParent().removeChild(ruleParent);
						}
					} else {

						updateORLabel(rulesDiv);

					}

					updateAndLabel(parent);
				}
				
			});
			rulelb.addEventListener("onSelect", new EventListener(){
				
				public void onEvent(Event event) throws Exception {

					Listbox lb = (Listbox) event.getTarget();
					CouponsEnum ruleEnum = null;
					if (lb.getSelectedItem().getValue() instanceof CouponsEnum) {
						ruleEnum = (CouponsEnum) lb.getSelectedItem().getValue();
						if (couponsEnum.name().equals(CouponsEnum.PURCHASE_IN_TIER.name())
								|| couponsEnum.name().equals(CouponsEnum.PURCHASE_IN_CARD_SET.name())) {
							creteOperatorOptionsForRule1((boolean) rulelb.getAttribute("isTier"),operatorlb, 0l);
						//	creteOperatorOptionsForRule1((boolean) rulelb.getAttribute("isTier"),(boolean) rulelb.getAttribute("isCard"), operatorlb, 0l);
							return;
						}
					} else {
						if (rulelb.getAttribute("Type") != null)
							creteOperatorOptionsForRule1((boolean) rulelb.getAttribute("isTier"),operatorlb,
									lb.getSelectedItem().getValue());
						return;
					}
					CouponsEnum tempEnum = null;
				
					
				
						if (operatorlb.isVisible() && operatorlb.getItemCount() > 0) {
							operatorlb.setSelectedIndex(0);
							tempEnum = (CouponsEnum) operatorlb.getSelectedItem().getValue();
						} else {
							tempEnum = ruleEnum;
						}
				

					if (ruleEnum.getType().equalsIgnoreCase("date")) {
						dblBox1.setVisible(false);
						dblBox2.setVisible(false);
						TextBox.setVisible(false);

						createValuesForOperatorForTypeDate1(db1, db2, numberLb, tempEnum);
					} else if (ruleEnum.getType().equalsIgnoreCase("string")
							|| ruleEnum.getType().equalsIgnoreCase("json")) {
						db1.setVisible(false);
						db2.setVisible(false);
						dblBox1.setVisible(false);
						dblBox2.setVisible(false);
						TextBox.setVisible(false);
						numberLb.setVisible(false);
						createValuesForOperatorTypeSting1(TextBox, tempEnum);

					}

				} 


			});
	
operatorlb.addEventListener("onSelect", new EventListener() {
	@Override
	public void onEvent(Event event) throws Exception {

		Listbox operatorlb = (Listbox) event.getTarget();

		Object obj = operatorlb.getSelectedItem().getValue();

		if (obj instanceof CouponsEnum) {

			CouponsEnum couponsEnum = operatorlb.getSelectedItem().getValue();
			String type = couponsEnum.getType();

			if (type.equalsIgnoreCase("date")) {
				createValuesForOperatorForTypeDate1(db1, db2, numberLb, couponsEnum);

			} else if (type.equalsIgnoreCase("string")) {

				createValuesForOperatorTypeSting1(TextBox, couponsEnum);

			} /*else if (type.equalsIgnoreCase("number")) {

				createValuesForOperatorTypeNumber(dblBox1, dblBox2, TextBox, couponsEnum);

			} */

		}

	}

});
	}
	return testDiv;

}
private void createSubDivRule1() {
	Div tempDiv = new Div();
	tempDiv.setSclass("");
	tempDiv.setParent(EligibilityAttributeDivId);

	Label AndLabel = new Label("AND");
	AndLabel.setStyle("display:inline-block; width:20px; padding:0 5px;margin:0 10px;font-weight:bold;");
	AndLabel.setParent(tempDiv);

	Div vdiv = new Div();// this is the outer component
	// vdiv.setDroppable("purIntSegment");
	vdiv.setParent(tempDiv);
	vdiv.setSclass("segment_parent_div");
	updateAndLabel(EligibilityAttributeDivId);

	List<CouponsEnum> purchaseAttrList = CouponsEnum.getEnumsByCategoryCode(1);

	Listitem subitem = null;
	for (CouponsEnum purchaseSegEnum : purchaseAttrList) {
		subitem = new Listitem(purchaseSegEnum.getDispLabel(), purchaseSegEnum);
		subitem.setStyle("padding:10px;");
		subitem.setAttribute("DBcolumnName", purchaseSegEnum.getColumnName());
		subitem.setAttribute("item", purchaseSegEnum.getItem());
		tempDiv.setAttribute("Type", purchaseSegEnum.name());
	}
	Div obj = createNewRuleDiv1(subitem);
	obj.setParent(vdiv);
	updateORLabel(vdiv);
	vdiv.addEventListener("onDrop", myDropListener);
	}
	public void onDrop$purIntAttributeDivId(Event event) {
		
		logger.debug("---ControllerSide -- Drop Event ="+event);

		DropEvent dropEvent = ((DropEvent) ((ForwardEvent) event).getOrigin());
		Component dragged = dropEvent.getDragged();
		
		logger.debug("----------Dragged target ="+dragged);
		
		if (dragged instanceof Listitem) {
			
			//Receipt total and item price cannot be dragged twice
			List<Component> childVboxList = new ArrayList<Component>();
			childVboxList = purIntAttributeDivId.getChildren();
			if(childVboxList!=null) {
			Div chilVDiv = null;
			boolean isReceiptTotalExists = false;
			boolean isItemPriceExists = false;
			Listitem item = (Listitem) dragged;
			CouponsEnum couponsEnum = (CouponsEnum) item.getValue();
			for (Object obj : childVboxList) {
								
								if(obj instanceof Div) {
									
									//boolean isValid = true;
									for(Object object : ((Div)obj).getChildren()) {
										
										if(object instanceof Div) {
											
											chilVDiv = (Div)object;
											if(chilVDiv.getSclass().contains("drop_")) {
												continue;
											}
											List childDivList = chilVDiv.getChildren();
											for (Object objects : childDivList) {
												if(objects instanceof Div ){
													Label fielslbl=null;
													List columns=null;
													String dispLabel=null;
													chilVDiv = (Div)objects;
													
													if(chilVDiv.getSclass().contains("drop_")) {
														continue;
													}
													columns = chilVDiv.getChildren();
													logger.info(item);
													logger.info(" "+chilVDiv.getAttribute("item"));
													fielslbl = (Label)columns.get(0);
													dispLabel = (String)fielslbl.getAttribute("item");
													logger.info(dispLabel);
													logger.info(couponsEnum.getDispLabel());
													if(dispLabel.equalsIgnoreCase(MinReceiptTotal)&&couponsEnum.getDispLabel().equalsIgnoreCase(dispLabel)) {
														MessageUtil.setMessage("Minimum Receipt Total is already in use", "color:red;");
														return;
													}
												
													if(dispLabel.equalsIgnoreCase("Item Price")&&couponsEnum.getDispLabel().equalsIgnoreCase(dispLabel)) {
														MessageUtil.setMessage("Item Price is already in use", "color:red;");
														return;
													}

														isItemPriceExists=true;
													}
												}
											}
										}
									}
								}
			}
			// new object
			Listitem item = (Listitem) dragged;
			CouponsEnum couponsEnum = (CouponsEnum) item.getValue();
			final Listitem itemForChild = item;
			if (dropflag && couponsEnum.name().equals(CouponsEnum.ITEM_PURCHASE_ATTRIBUTE.name()) && editCombinedRuleObj==null) {
				createSubDivRule();
				dropflag = false;
			}
			Div tempDiv = new Div();
			tempDiv.setSclass("");
			tempDiv.setParent(purIntAttributeDivId);
			
			Label AndLabel = new Label("AND");
			AndLabel.setStyle("display:inline-block; width:20px; padding:0 5px;margin:0 10px;font-weight:bold;");
			AndLabel.setParent(tempDiv);
			
			
			Div vdiv = new Div();//this is the outer component
			vdiv.setDroppable("purIntSegment");
			vdiv.setParent(tempDiv);
			vdiv.setSclass("segment_parent_div");
			updateAndLabel(purIntAttributeDivId);
			//vdiv.setStyle("overflow:auto;background:#EEFFDD;border:1px solid #AAAAAA;margin:15px 10px;");
			//vdiv.setWidth("95%");
			//vdiv.setHeight("150px");
			
			/*Label orlbl = new Label("(OR)");
			orlbl.setParent(obj);
			orlbl.setStyle("")*/
			
			Div obj = createNewRuleDiv(item);
			obj.setParent(vdiv);
			//updateORLabel(vdiv);
			//obj.setStyle("padding:10px 0px;");
			
			// Generate Drop Div
			if (couponsEnum.name().equals(CouponsEnum.ITEM_PURCHASE_ATTRIBUTE.name()))
			vdiv.setDroppable("notDraggable");
			//else
			//vdiv.appendChild(createDropORDiv("Drag Purchase Attribute here to create OR combination rule."));
			
			vdiv.addEventListener("onDrop", myDropListener);
		}
		
	} // onDrop$programdesignerWinId

	private void createSubDivRule() {
	Div tempDiv = new Div();
	tempDiv.setSclass("");
	tempDiv.setParent(purIntAttributeDivId);

	Label AndLabel = new Label("AND");
	AndLabel.setStyle("display:inline-block; width:20px; padding:0 5px;margin:0 10px;font-weight:bold;");
	AndLabel.setParent(tempDiv);

	Div vdiv = new Div();// this is the outer component
	// vdiv.setDroppable("purIntSegment");
	vdiv.setParent(tempDiv);
	vdiv.setSclass("segment_parent_div");
	updateAndLabel(purIntAttributeDivId);

	List<CouponsEnum> purchaseAttrList = CouponsEnum.getEnumsByCategoryCode(4);

	Listitem subitem = null;
	for (CouponsEnum purchaseSegEnum : purchaseAttrList) {
		subitem = new Listitem(purchaseSegEnum.getDispLabel(), purchaseSegEnum);
		subitem.setStyle("padding:10px;");
		subitem.setAttribute("DBcolumnName", purchaseSegEnum.getColumnName());
		subitem.setAttribute("item", purchaseSegEnum.getItem());
		tempDiv.setAttribute("Type", purchaseSegEnum.name());
	}
	Div obj = createNewRuleDiv(subitem);
	obj.setParent(vdiv);
	//updateORLabel(vdiv);
	vdiv.addEventListener("onDrop", myDropListener);
	}
	
	public void updateAndLabel(Component parentDiv) {
		
		logger.info("in update AND label ::"+parentDiv.getId() );
		List<Component> divComp = parentDiv.getChildren();
		
		
		for (Component eachDiv : divComp) {
			
			
			logger.info("each CLASS="+ eachDiv.getClass());
			if(eachDiv instanceof Div) {
				
				if(((Div) eachDiv).getSclass() == null ) {
					
					Component label = eachDiv.getFirstChild();
					//logger.info("each CLASS="+ label.getClass());
					
					
					if(label!=null && label instanceof Label) {
						
						logger.info("updating the AND LABEL ====="+((Label)label).getValue());
						((Label)label).setValue("");
						logger.info("updating the AND LABEL ====="+((Label)label).getValue());

					}
					
					break;
					
					
				}
				
				else if( ((Div)eachDiv).getSclass().contains("drop_")) continue;
				
				
			}
		} // for 
		
		
		
	}
	

	
	
	public Div createNewRuleDiv(Listitem item) {
		
		int index = item.getIndex();
		
		CouponsEnum couponsEnum = (CouponsEnum)item.getValue();
	
		return getCreatedCompDiv(couponsEnum, item);
		
		
	}

//	int attrCount=0;

private void creteOperatorOptionsForRule1(boolean istire,Listbox operatorLb, Long programId) {
	// TODO Auto-generated method stub
	Listitem li = null;
	Components.removeAllChildren(operatorLb);
	if (istire) {
		li = new Listitem("Select Tier", "");
		li.setSelected(true);
		li.setParent(operatorLb);
		List<LoyaltyProgramTier> tierList = loyaltyProgramTierDao.getTierListByPrgmId(programId);
		if (tierList != null && tierList.size() > 0) {
			for (LoyaltyProgramTier tierObj : tierList) {
				li = new Listitem(tierObj.getTierName(), tierObj.getTierId());
				li.setParent(operatorLb);
			}
		}

	} 
	else{
		li = new Listitem("Select Card-set", "");
		li.setSelected(true);
		li.setParent(operatorLb);
		List<LoyaltyCardSet> list = loyaltyCardSetDao.findByProgramId(programId);
		if (list != null && list.size() > 0) {
			for (LoyaltyCardSet cardset : list) {
				li = new Listitem(cardset.getCardSetName(), cardset.getCardSetId());
				li.setParent(operatorLb);
			}
		}
	}
	operatorLb.setVisible(true);
	operatorLb.setSelectedIndex(0);
	
}
private void createValuesForOperatorTypeSting1(Textbox TextBox, CouponsEnum couponsEnum) {
	
		// TODO Auto-generated method stub
	if (couponsEnum == null) {
		logger.debug("segment enum is null....");
		return;
	}
	String val1 = couponsEnum.getType1();
	String val2 = couponsEnum.getType2();
	logger.debug("val1 ::" + val1);
	logger.debug("val2 ::" + val2);
	if (val1 == null && val2 == null) {
		TextBox.setVisible(false);
	} else if (val1 != null && val2 == null) {
		if (couponsEnum.getDispLabel().equals("One of")) {
			TextBox.setTooltiptext("You can enter multiple values as comma (',') separated values.");
		} else {
			TextBox.setTooltiptext("");
		}
		TextBox.setVisible(true);

	} // else if

		
	}

private void createValuesForOperatorForTypeDate1(Datebox db1, Datebox db2, Listbox numberLb, CouponsEnum couponsEnum
		) {
		// TODO Auto-generated method stub
	
	if (couponsEnum == null) {
		logger.debug("segment enum is null....");
		return;
	}
	String val1 = couponsEnum.getType1();
	String val2 = couponsEnum.getType2();
	logger.debug("val2 ::" + val2);

	if (val1 == null && val2 == null) {

		db1.setVisible(false);
		db2.setVisible(false);
		numberLb.setVisible(false);

		// txtBox.setVisible(false);

	} else if (val1 != null && val2 != null) {

		if (!val1.equalsIgnoreCase("date")) {// data type is date but type
												// is:within last-range of
												// days

			if (val1.equalsIgnoreCase("number")) {

				numberLb.setVisible(false);

			} else {
				Components.removeAllChildren(numberLb);
				int num = Integer.parseInt(val1);
				for (int i = 1; i <= num; i++) {
					Listitem li = new Listitem("" + i, i);
		//			if (couponsEnum.name().equals(couponsEnum.DAY_OF_THE_WEEK.name()))
						li = new Listitem(dayMap.get(i), i);
					li.setParent(numberLb);

				}

				numberLb.setSelectedIndex(0);
				numberLb.setVisible(true);
			}
			db1.setVisible(false);
			db2.setVisible(false);

		} else {
			db1.setVisible(true);
			db2.setVisible(true);
			db2.setTooltiptext("Start date should be less than end date.");
			numberLb.setVisible(false);
		}

	} else if (val1 != null && val2 == null) {

		if (val1.equalsIgnoreCase("date")) {

			db2.setVisible(false);
			numberLb.setVisible(false);
			db1.setVisible(true);
			// txtBox.setVisible(false);

		}
	}

		
	}
int attrCount=0;
public Div getCreatedCompDiv(CouponsEnum couponsEnum, Listitem item) {

	
	List<Listitem> itemList = purchaseAttrLbId.getItems();
	if (couponsEnum.name().equals(CouponsEnum.ITEM_PURCHASE_ATTRIBUTE.name())) {
		itemList.forEach(itm -> {
			CouponsEnum spenum = itm.getValue();
			if (spenum.name().equals(CouponsEnum.DISCOUNT_QUANTITY_PER_ITEM.name()))
				itm.setDraggable("notdrag");
		});
		count++;
	}
		Listitem li = null;//for rulelb 
		Div testDiv = new Div();
		
		
		testDiv.setSclass("segment_child_div");
		
		
		
		
		
		Label lbl  = new Label();//display label-----1
		lbl.setStyle("padding:0 5px; display:inline-block; width:150px;");
		
		lbl.setParent(testDiv);
		lbl.setValue(item.getLabel());
		lbl.setAttribute("columnName", (String)item.getAttribute("DBcolumnName"));
		lbl.setAttribute("item", (String)item.getAttribute("item"));
		
		
		Listbox rulelb = new Listbox();//rule lb----2
		rulelb.setMold("select");
		rulelb.setStyle("margin-right:5px; width:190px;");
		rulelb.setParent(testDiv);
		if(couponsEnum.name().equalsIgnoreCase(CouponsEnum.RECEIPT_TOTAL_ATTRIBUTE.name())) { rulelb.setVisible(false);};
	
		if(couponsEnum.name().equalsIgnoreCase(CouponsEnum.DISCOUNT_TYPE.name()) && 
				(disableDiscType || (discountRadioGrId.getSelectedItem()==skuRadioId && productDiscountGenRowsId.getVisibleItemCount()>0) || 
						(discountRadioGrId.getSelectedItem()==tpaRadioId && receiptDiscountGenRowsId.getVisibleItemCount()>0)))
		{ rulelb.setDisabled(true);};
		
		//for lty harshi ---------3
		
		 final Listbox ltylb = new Listbox();
		//if(couponsEnum.name().equalsIgnoreCase("LOYALTY_TRANSACTIONS")){
		ltylb.setMold("select");
		ltylb.setStyle("margin-right:5px;");
	     	ltylb.setParent(testDiv);
	     	ltylb.setVisible(false);
		 //}
		
		final Listbox numberLb = new Listbox();//days/weeks/mnths------4
		numberLb.setMold("select");
		
		numberLb.setStyle("margin-right:5px;");
		
		for(int i=1; i<=30; i++){
			li = new Listitem(""+i);
			li.setParent(numberLb);
		}
		numberLb.setVisible(false);
		
		numberLb.setParent(testDiv);
		
		final Listbox operatorlb = new Listbox();//rule options lb----------5
		operatorlb.setMold("select");
		operatorlb.setStyle("margin-right:5px;");
		operatorlb.setParent(testDiv);
		operatorlb.setVisible(false);
		
		
		
		final Textbox textBox = new Textbox();//string type value--------6
		textBox.setStyle("margin-right:5px;");
		textBox.setParent(testDiv);
		textBox.setVisible(false);
		
		
		final Decimalbox dblBox1 = new Decimalbox();//number type value1------------7
		dblBox1.setStyle("margin-right:5px;");
		dblBox1.setParent(testDiv);
		dblBox1.setVisible(false);
		
		final Decimalbox dblBox2 = new Decimalbox();//number type value2------------8
		dblBox2.setStyle("margin-right:5px;");
		dblBox2.setParent(testDiv);
		
		dblBox2.setVisible(false);
		
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		
		final Datebox db1 = new MyDatebox();//date type value1------------9
		db1.setStyle("margin-right:10px;");
		db1.setFormat(format.toPattern());
		db1.setReadonly(true);
		db1.setVisible(false);
		db1.setParent(testDiv);
		db1.setLenient(false);
		
		
		final Datebox db2 = new MyDatebox();//date type value2------------10
		db2.setStyle("margin-right:10px;");
		db2.setParent(testDiv);
		db2.setReadonly(true);
		db2.setFormat(format.toPattern());
		db1.setLenient(false);
		db2.setVisible(false);
		
		final Listbox formLb = new Listbox();//-------11
		
		formLb.setMold("select");
			//campLb.setStyle("margin-right:5px;");
		formLb.setParent(testDiv);
		formLb.setVisible(false);
		logger.info("item.getLabel().trim() "+item.getLabel().trim());
		
		final Combobox cb1 = new Combobox();//----12
		cb1.setSclass("cb_100w");
		cb1.setStyle("margin-right:5px;");
		cb1.setParent(testDiv);
		cb1.setVisible(false);
		
		final Combobox cb2 = new Combobox();//--------13
		cb2.setSclass("cb_100w");
		cb2.setStyle("margin-right:5px;");
		cb2.setParent(testDiv);
		cb2.setVisible(false);
		final Combobox cb3 = new Combobox();//--------13
		cb3.setSclass("cb_100w");
		cb3.setStyle("margin-right:5px;");
		cb3.setParent(testDiv);
		cb3.setVisible(false);
		String optionalValues = (String)item.getAttribute("optionalValues");
		
		if(optionalValues != null) {
			String optValues[] = optionalValues.split(Constants.ADDR_COL_DELIMETER);
			for (String optVal : optValues) {
				cb1.appendItem(optVal);
				cb2.appendItem(optVal);
			}
		}
	
		Toolbarbutton deleteIcon  = new Toolbarbutton();//-------13 (14)
		deleteIcon.setImage("/images/action_delete.gif");
		deleteIcon.setStyle("float:right; margin-right:25px;");
		deleteIcon.setAttribute("Type", couponsEnum.name());
		if (!couponsEnum.name().equals(CouponsEnum.DISCOUNT_QUANTITY_PER_ITEM.name()))
			deleteIcon.setParent(testDiv);
		//deleteIcon.setParent(testDiv);
		ltylb.setVisible(false);
		List<CouponsEnum> ruleEnums = couponsEnum.getChidrenByParentEnum(couponsEnum);//to prepare rule lb
	
		String rule = "";
		int strtIndex;
		int endIndex;
		
		for (CouponsEnum enums : ruleEnums) {
			
			if(!enums.getDispLabel().isEmpty()&&!enums.getDispLabel().startsWith("UDF")) {
			if(couponsEnum.name().equals(CouponsEnum.DISCOUNT_QUANTITY_PER_ITEM.name())) {
				if((discountTypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("loyaltyRewards")||discountTypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("purchases"))
					&& (enums.getDispLabel().equalsIgnoreCase("All")||enums.getDispLabel().equalsIgnoreCase("Minimum"))){
					continue;
				}
			}

			else if(couponsEnum.name().equals(CouponsEnum.DISCOUNT_ELIGIBLE_ITEMS.name())) {
				
			
				if((discountTypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("loyaltyRewards")||discountTypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("purchases"))
						&& (enums.getDispLabel().equalsIgnoreCase("all items"))){
						continue;
					}
			}
	
		
			li = new Listitem(itemAttributesMap.get(enums.getDispLabel())!=null?itemAttributesMap.get(enums.getDispLabel()):
								enums.getDispLabel(),enums);
			li.setParent(rulelb);
			}
		}
		if(couponsEnum.name().equals(CouponsEnum.ITEM_PURCHASE_ATTRIBUTE.name())){
			List<POSMapping> posList = posMappingDao.findByType("'" + Constants.POS_MAPPING_TYPE_SKU + "'",
					users.getUserId());
			CouponsEnum udfEnum = null;
			if (posList != null) {
				for (POSMapping obj : posList) {

					String column = obj.getCustomFieldName().trim().toLowerCase();
					if (column.startsWith("udf")) {
						li = new Listitem();
						udfEnum = CouponsEnum.getEnumByItem(obj.getCustomFieldName());
						li.setValue(udfEnum);
						li.setLabel(obj.getDisplayLabel());
						li.setAttribute("column", column);
						li.setParent(rulelb);
						segRuleSKUMap.put(obj.getDisplayLabel(), obj.getCustomFieldName());
						udfRuleSKUMap.put(obj.getDisplayLabel(), obj.getCustomFieldName().toLowerCase());
						udfDisplaySKUMap.put(obj.getCustomFieldName(), obj.getDisplayLabel());
					} 
				
				}
			}
		}
			creteOperatorOptionsForRule(ruleEnums.get(0), operatorlb,null);
			
			//}
			/*for (CouponsEnum ltySubEnum : ltySubRuleEnum) {
				li = new Listitem(ltySubEnum.getDispLabel(),ltySubEnum);
				li.setParent(ltylb);
				ltylb.setSelectedIndex(0);
			}*/
		/*if(ltyRuleEnum!=null && ltyRuleEnum.size()>0){
			creteOperatorOptionsForRule(ltyRuleEnum.get(0), operatorlb);
		}*/
		
		if(rulelb.getItemCount() > 0) rulelb.setSelectedIndex(0);
		//if(operatorlb.getItemCount() > 0) operatorlb.setSelectedIndex(0);
		
		
		CouponsEnum tempEnum = null;
		if(operatorlb.isVisible() && operatorlb.getItemCount() > 0){
			
			operatorlb.setSelectedIndex(0);
			tempEnum = (CouponsEnum)operatorlb.getSelectedItem().getValue();
			
		}else {
			
			tempEnum = (CouponsEnum)rulelb.getSelectedItem().getValue();
		}
		
		//test for lty num field
		/*if(ltylb.isVisible() && ltylb.getItemCount() > 0){
			
			tempEnum = (CouponsEnum)ltylb.getSelectedItem().getValue();
		}*/
		
		if (tempEnum != null) {
			String type = tempEnum.getType();
			if (type.equalsIgnoreCase("date")) {

				dblBox1.setVisible(false);
				dblBox2.setVisible(false);
				textBox.setVisible(false);

				createValuesForOperatorForTypeDate(db1, db2, numberLb,
						tempEnum, dblBox1, dblBox2);
			}
			else if (type.equalsIgnoreCase("string")) {

				numberLb.setVisible(false);
				db1.setVisible(false);
				db1.setVisible(false);
				dblBox1.setVisible(false);
				dblBox2.setVisible(false);
				createValuesForOperatorTypeSting(textBox, tempEnum, cb1, cb2,
						formLb);

			} else if (type.equalsIgnoreCase("number") ) {

				numberLb.setVisible(false);
				db1.setVisible(false);
				db1.setVisible(false);
				textBox.setVisible(false);
				createValuesForOperatorTypeNumber(dblBox1, dblBox2, textBox,
						tempEnum, cb1, cb2);

			}
			
	
		}
		
		
		
		
		
		
		deleteIcon.addEventListener("onClick", new EventListener(){
			@Override
			public void onEvent(Event event) throws Exception {
				
				Toolbarbutton deleteIcon = (Toolbarbutton)event.getTarget();
				//logger.info("deleteIcon.getParent().getParent().getChildren().size()"+deleteIcon.getParent().getParent().getChildren().size());
				Component componentsDiv = deleteIcon.getParent();
				//Label  intLabel = (Label)componentsDiv.getChildren().get(0);
				Component rulesDiv = componentsDiv.getParent();
				Component ruleParent = rulesDiv.getParent();
				Component parent = ruleParent.getParent();
				if (deleteIcon.getAttribute("Type").equals(CouponsEnum.ITEM_PURCHASE_ATTRIBUTE.name())){
					count--;
				if (count == 0) {
					itemList.forEach(itm -> {
						CouponsEnum cpenum = itm.getValue();
						if (cpenum.name().equals(CouponsEnum.DISCOUNT_QUANTITY_PER_ITEM.name()))
							itm.setDraggable("outerDivDropable");
					});
					List<Component> list = purIntAttributeDivId.getChildren();
					for (Component comp : list) {
						Div tempDiv = (Div) comp;
						if (tempDiv.getAttribute("Type") != null && (tempDiv.getAttribute("Type").toString()
								.equals(CouponsEnum.DISCOUNT_QUANTITY_PER_ITEM.name()))) {
							purIntAttributeDivId.removeChild(tempDiv);
							dropflag=true;
							break;
						}
					}

				}

			}
			
				
				 logger.info("Count ::"+count);
			
				rulesDiv.removeChild(componentsDiv);
				logger.info("rulesDiv parent is ::"+(Div)rulesDiv.getParent().getParent()+
						" rulesDiv.getChildren().size() "+rulesDiv.getChildren().size());
				
					if(rulesDiv.getChildren().size()==0) {
						
						rulesDiv.getParent().removeChild(rulesDiv);
						ruleParent.getParent().removeChild(ruleParent);
						
						
						
						
					}
					else if(rulesDiv.getChildren().size()==1) {
						
						logger.info("((Div)rulesDiv.getFirstChild()).getSclass() ::"+((Div)rulesDiv.getFirstChild()).getSclass());
						if(((Div)rulesDiv.getFirstChild()).getSclass().equalsIgnoreCase("drop_or_div")) {
							
							rulesDiv.getParent().removeChild(rulesDiv);
							ruleParent.getParent().removeChild(ruleParent);
							
							
						}
					}
					else {
						
						//updateORLabel(rulesDiv);
						
					}
				
				updateAndLabel(parent);
			}
			
		});
		
		
		operatorlb.addEventListener("onSelect", new EventListener(){
			@Override
			public void onEvent(Event event) throws Exception {
				
				Listbox operatorlb = (Listbox)event.getTarget();
				
				Object obj = operatorlb.getSelectedItem().getValue();
				
				if(obj instanceof CouponsEnum) {
					
					CouponsEnum couponsEnum = operatorlb.getSelectedItem().getValue();
					String type = couponsEnum.getType();
					
					if(type.equalsIgnoreCase("date")) {
					createValuesForOperatorForTypeDate(db1, db2, numberLb, couponsEnum, dblBox1, dblBox2);
						
					}else if(type.equalsIgnoreCase("string")) {
						
						
						createValuesForOperatorTypeSting(textBox, couponsEnum, cb1, cb2,formLb);
						
					}else if(type.equalsIgnoreCase("number") ) {
						
						createValuesForOperatorTypeNumber(dblBox1, dblBox2, textBox, couponsEnum, cb1,cb2);
						
					}
					
				}
				
				
				
			}
			
		});
		
		
		ltylb.addEventListener("onSelect", new EventListener(){
			@Override
			public void onEvent(Event event) throws Exception {
				
				Listbox ltylb = (Listbox)event.getTarget();
				
				Object obj = ltylb.getSelectedItem().getValue();
				
				if(obj instanceof CouponsEnum) {
					
					CouponsEnum couponsEnum = ltylb.getSelectedItem().getValue();
					String type = couponsEnum.getType();
					
					if(type.equalsIgnoreCase("date")) { 
						CouponsEnum ltyEnum =null;
						/*if(couponsEnum.getParentEnum().name().equalsIgnoreCase(CouponsEnum.PROMO_CODE_ISSUED.name())|| 
								couponsEnum.getParentEnum().name().equalsIgnoreCase(CouponsEnum.PROMO_CODE_EXPIRED.name())
								|| couponsEnum.getParentEnum().name().equalsIgnoreCase(CouponsEnum.PROMO_CODE_REDEEMED.name())) {
							ltyEnum=couponsEnum.getChidrenByParentEnum(couponsEnum).get(0);
							couponsEnum=ltyEnum;
								List<CouponsEnum> ltySubRuleEnum = couponsEnum.getChidrenByParentEnum(couponsEnum.getParentEnum());
								Components.removeAllChildren(operatorlb);
								for (CouponsEnum ltySubEnum : ltySubRuleEnum) {
									Listitem li = new Listitem(ltySubEnum.getDispLabel(),ltySubEnum);
									li.setParent(operatorlb);
									li.addEventListener("onSelect", this);
									operatorlb.setSelectedIndex(0);
							     }
						}*/
						textBox.setVisible(false);
						createValuesForOperatorForTypeDate(db1, db2, numberLb, couponsEnum, dblBox1, dblBox2);
						
					}else if(type.equalsIgnoreCase("string")) {
						
						
						createValuesForOperatorTypeSting(textBox, couponsEnum, cb1, cb2,formLb);
						
					}else if(type.equalsIgnoreCase("number") ) {
						CouponsEnum ltyEnum =null;
						/*if(couponsEnum.getParentEnum().name().equalsIgnoreCase(CouponsEnum.NUMBER_OF_TRANSACTIONS.name())){
							ltyEnum=couponsEnum.getChidrenByParentEnum(couponsEnum).get(0);
							couponsEnum=ltyEnum;
						}else if(couponsEnum.getParentEnum().name().equalsIgnoreCase(CouponsEnum.PURCHASE_LOYALTY_BALANCE.name()) ||
								couponsEnum.getParentEnum().name().equalsIgnoreCase(CouponsEnum.PURCHASE_LOYALTY_TOTAL_ISSUED_IS.name()) ||
								couponsEnum.getParentEnum().name().equalsIgnoreCase(CouponsEnum.PURCHASE_LOYALTY_BALANCE_IS.name())) {
							ltyEnum=couponsEnum.getChidrenByParentEnum(couponsEnum).get(0);
							ltyEnum=(operatorlb.getSelectedItem().getValue()!=null ? operatorlb.getSelectedItem().getValue() :
								(couponsEnum.getChidrenByParentEnum(couponsEnum)).get(0));
							ltyEnum=couponsEnum.getChidrenByParentEnum(couponsEnum).get(0);
							List<CouponsEnum> ltySubRuleEnum = couponsEnum.getChidrenByParentEnum(ltyEnum.getParentEnum());
							Components.removeAllChildren(operatorlb);
							for (CouponsEnum ltySubEnum : ltySubRuleEnum) {
								Listitem li = new Listitem(ltySubEnum.getDispLabel(),ltySubEnum);
								li.setParent(operatorlb);
								li.addEventListener("onSelect", this);
								operatorlb.setSelectedIndex(0);
						     }
							couponsEnum=ltyEnum;
						}*/
						createValuesForOperatorTypeNumber(dblBox1, dblBox2, textBox, couponsEnum, cb1,cb2);
						
					}
					
				}
				
				
				
			}
			
		});
		
		
		rulelb.addEventListener("onSelect", new EventListener(){
			@Override
			public void onEvent(Event event) throws Exception {
				
				
				Listbox lb = (Listbox)event.getTarget();
				
				
				CouponsEnum ruleEnum = (CouponsEnum)lb.getSelectedItem().getValue();
				
				Components.removeAllChildren(operatorlb);
				creteOperatorOptionsForRule(ruleEnum, operatorlb,rulelb);
				CouponsEnum tempEnum = null;
				if(operatorlb.isVisible() && operatorlb.getItemCount() > 0){
					
					
					operatorlb.setSelectedIndex(0);
					 tempEnum = (CouponsEnum)operatorlb.getSelectedItem().getValue();
				}else {
					
					tempEnum = ruleEnum;
				}
				
				if(ruleEnum.getType().equalsIgnoreCase("date")) {
					dblBox1.setVisible(false);
					dblBox2.setVisible(false);
					textBox.setVisible(false);
					
					createValuesForOperatorForTypeDate(db1, db2, numberLb,tempEnum,dblBox1, dblBox2 );
				}
				else if(ruleEnum.getType().equalsIgnoreCase("string")) {
					
					numberLb.setVisible(false);
					db1.setVisible(false);
					db2.setVisible(false);
					dblBox1.setVisible(false);
					dblBox2.setVisible(false);
					createValuesForOperatorTypeSting(textBox,tempEnum, cb1, cb2, formLb);
					
					
				}else if(ruleEnum.getType().equalsIgnoreCase("number")) {
					textBox.setVisible(false);
					numberLb.setVisible(false);
					db1.setVisible(false);
					db2.setVisible(false);
					//textBox.setVisible(false);
					createValuesForOperatorTypeNumber(dblBox1, dblBox2, textBox, tempEnum, cb1, cb2);
					
				}
				
			}
			
		});
		
		return testDiv;
		
	}


private void createNumberOptionsForRule(CouponsEnum couponEnum, Listbox listbox) {
	Components.removeAllChildren(listbox);
	Listitem li = null;
	for (int i = 1; i <= 24; i++) {
		if (i <= 11)
			li = new Listitem(i + "AM", i);
		else if (i == 12)
			li = new Listitem(i + "PM", i);
		else if (i == 24)
			li = new Listitem(12 + "AM", 0);
		else
			li = new Listitem((i - 12) + "PM", i);
		li.setParent(listbox);
	}
	listbox.setVisible(true);
	listbox.setSelectedIndex(0);
}
public void createValuesForOperatorTypeNumber(Decimalbox  dblBox1, Decimalbox  dblBox2,Textbox textbox, CouponsEnum couponsEnum, Combobox cb1, Combobox cb2) {
	
	if(couponsEnum == null){
		logger.debug("segment enum is null...."); 
		return;
	}
	String val1 = couponsEnum.getType1();
	String val2= couponsEnum.getType2();
	String token = couponsEnum.getToken();
	
	logger.debug("val2 ::" +val2);
	if(val1 == null && val2 == null) {
		
		dblBox1.setVisible(false);
		dblBox2.setVisible(false);
		textbox.setVisible(false);
		cb1.setVisible(false);
		cb2.setVisible(false);
		
	}else if(val1 != null && val2 == null) {
		
		if(val1.equalsIgnoreCase("text") ) {
			
			if(cb1.getItemCount() > 0) {
				
				cb1.setVisible(true);
				textbox.setVisible(false);
				cb2.setVisible(false);
				dblBox1.setVisible(false);
				dblBox2.setVisible(false);
				if(couponsEnum.getDispLabel().equals("One of")){
					cb1.setTooltiptext("You can enter multiple Number type values as comma (',') separated values.");
				}else{
					cb1.setTooltiptext("Enter Only Number type values.");
				}
				
				
			}
		
			else {
				
				if(couponsEnum.getDispLabel().equals("One of")){
					textbox.setTooltiptext("You can enter multiple Number type values as comma (',') separated values.");
				}else{
					textbox.setTooltiptext("Enter Only Number type values.");
				}
				textbox.setVisible(true);
				dblBox1.setVisible(false);
				dblBox2.setVisible(false);
				cb1.setVisible(false);
				cb2.setVisible(false);
			}
			
		}//if
		else {
			if(cb1.getItemCount() > 0) {
				
				cb1.setVisible(true);
				cb2.setVisible(false);
				textbox.setVisible(false);
				dblBox1.setVisible(false);
				dblBox2.setVisible(false);
				
				
			}

			else{
			
				dblBox1.setVisible(true);
				dblBox2.setVisible(false);
				textbox.setVisible(false);
				cb1.setVisible(false);
				cb2.setVisible(false);
			}
		}
		
	}else if(val1 != null && val2 != null) {
		if(cb1.getItemCount() > 0) {
			
			cb1.setVisible(true);
			cb2.setVisible(true);
			cb2.setTooltiptext("Value should be greater than the first value.");
			textbox.setVisible(false);
			dblBox1.setVisible(false);
			dblBox2.setVisible(false);
			
			
		}else {
			
			dblBox1.setVisible(true);
			dblBox2.setVisible(true);
			dblBox2.setTooltiptext("Value should be greater than the first value.");
			textbox.setVisible(false);
			cb1.setVisible(false);
			cb2.setVisible(false);
			
		}
		
	}
	
	
}

public void createValuesForOperatorForTypeDate(Datebox db1, Datebox db2, Listbox numberLb, CouponsEnum couponsEnum,
		Decimalbox dblBox1, Decimalbox dblBox2) {
	
	
	if(couponsEnum == null){
		logger.debug("segment enum is null...."); 
		return;
	}
	String val1 = couponsEnum.getType1();
	String val2= couponsEnum.getType2();
	logger.debug("val2 ::" +val2);
		
		if(val1 == null && val2 == null) {
			
			db1.setVisible(false);
			db2.setVisible(false);
			numberLb.setVisible(false);
			dblBox1.setVisible(false);
			dblBox2.setVisible(false);
			//txtBox.setVisible(false);
			
		}else if(val1 != null && val2 != null) {
			
			if(!val1.equalsIgnoreCase("date")) {// data type is date but type is:within last-range of days
				
				if(val1.equalsIgnoreCase("number")) {
					
					dblBox1.setVisible(true);
					dblBox2.setVisible(true);
					dblBox2.setTooltiptext("Value should be greater than the first value.");
					numberLb.setVisible(false);
					
					
				}else{
					Components.removeAllChildren(numberLb);
					int num = Integer.parseInt(val1);
					for(int i=1; i<=num ; i++) {
						
						Listitem li = new Listitem(""+i, val2);
						li.setParent(numberLb);
						
					}
					
					numberLb.setSelectedIndex(0);
					numberLb.setVisible(true);
					dblBox1.setVisible(false);
					dblBox2.setVisible(false);
				}
				db1.setVisible(false);
				db2.setVisible(false);
				
				
				
			}else{
				db1.setVisible(true);
				db2.setVisible(true);
				db2.setTooltiptext("Start date should be less than end date");
				dblBox1.setVisible(false);
				dblBox2.setVisible(false);
				numberLb.setVisible(false);
			}
			
		}else if(val1 != null && val2 == null) {
			
			if(val1.equalsIgnoreCase("date")) {
			
			db1.setVisible(true);
			db2.setVisible(false);
			numberLb.setVisible(false);
			dblBox1.setVisible(false);
			dblBox2.setVisible(false);
			//txtBox.setVisible(false);
			
			}
		}
}
private List<ValueCodes> valueCodeList;

/**
 * To cretae and return the operator listbox based on the selected rule in rulelb
 * @param ruleEnumArr
 * @return
 */
public void creteOperatorOptionsForRule(CouponsEnum ruleEnum, Listbox operatorLb,Listbox rulelb) {
	
	Listitem li =null;
	
	List<CouponsEnum> childEnumList = ruleEnum.getChidrenByParentEnum(ruleEnum); 
	
	if(childEnumList.size() == 0){
		
			operatorLb.setVisible(false);
			
			return;
	}
	
	for (CouponsEnum childEnum : childEnumList) {
		
		logger.info(rulelb!=null ? rulelb.getSelectedItem() : "");
			li = new Listitem(childEnum.getDispLabel(), childEnum);
			if(childEnum.getType().equalsIgnoreCase("date") && childEnum.getToken().contains("_between")){
				
				li.setTooltiptext("Year will be ignored.");
			}
			li.setParent(operatorLb);
			li.addEventListener("onSelect", this);
			
		
	}//for
	
	operatorLb.setVisible(true);
	
	if(operatorLb.getItemCount() > 0)
	operatorLb.setSelectedIndex(0);
}//creteOperatorOptionsForRule()



public void createValuesForOperatorTypeSting(Textbox textbox, CouponsEnum couponsEnum, Combobox cb1, Combobox cb2, Listbox formLb) {
	
	if(couponsEnum == null){
		logger.debug("segment enum is null...."); 
		return;
	}
	String val1 = couponsEnum.getType1();
	String val2= couponsEnum.getType2();
	logger.debug("val1 ::" +val1);
	logger.debug("val2 ::" +val2);
	if(val1 == null && val2 == null) {
		
		textbox.setVisible(false);
		cb1.setVisible(false);
		cb2.setVisible(false);
		
		String colVal = couponsEnum.getColumnValue(); 
		if(colVal != null && colVal.equals(Constants.CONTACT_OPTIN_MEDIUM_WEBFORM)) {
			
			formLb.setVisible(formLb.getItemCount() > 0);
			
		}//if
		else{
			formLb.setVisible(false);
		}
		
		
		
	}else if(val1 != null && val2 == null) {
		
		if(cb1.getItemCount() > 0) {
			
			cb1.setVisible(true);
			textbox.setVisible(false);
			if(couponsEnum.getDispLabel().equals("One of")){
				cb1.setTooltiptext("You can enter multiple values as comma (',') separated values.");
			}else{
				cb1.setTooltiptext("");
			}
			
		}
		else {
			
			cb1.setVisible(false);
			cb1.setVisible(false);
			if(couponsEnum.getDispLabel().equals("One of")){
				textbox.setTooltiptext("You can enter multiple values as comma (',') separated values.");
			}else{
				textbox.setTooltiptext("");
			}
			textbox.setVisible(true);
			
		}//else
		
	}//else if
	
}


public  void updateORLabel(Component vdiv) {
	
	logger.info("SCLASS="+ ((Div)vdiv).getSclass());
	/*List<Component> divComp = vdiv.getChildren();
	
	for (Component eachDiv : divComp) {

		logger.info("each CLASS="+ eachDiv.getClass());
		
		if( ((Div)eachDiv).getSclass().contains("drop_")) continue;
		
		Component label = eachDiv.getFirstChild();
		
		if(label!=null && label instanceof Label) {
			logger.info("updating the OR LABEL ====="+((Label)label).getValue());
			((Label)label).setValue("");
			logger.info("updating the OR LABEL ====="+((Label)label).getValue());
			//((Label)label).setValue("");
		}
		
		break;
	} // for 
*/	
} // updateORLabel

DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");


	
			
			
			public boolean validateDuplicacy(String segmentRuleSB) {
				String arrRule[]=segmentRuleSB.split("\\|\\|");
				HashSet<String> hashSet = new HashSet<String>();
				
				for (String eleName : arrRule) {
					if (hashSet.add(eleName) == false && !eleName.isEmpty()) { 
					return true;
					}
				}
				for(String eleName :arrRule) {
					String arrRuleOR[]=eleName.split("<OR>");
					HashSet<String> hashSetForOR = new HashSet<String>();
					for (String eleNameOR : arrRuleOR) {
						if (hashSetForOR.add(eleNameOR) == false && !eleNameOR.isEmpty()) { 
						return true;
						}
					}
				}
				
			
				return false;
			
			
			
			}
			
			List<SkuFile> exportMap = new ArrayList<SkuFile>();
			
			/*public void onClick$validateButtonId(){
				logger.info("New rule created is =======+");
				String[] ruleStr;
				String[] ruleString;
				String rule = saveRule(true); // "\\|\\|" 
				// Item Category|sku.item_category|string:does not contain|LG
				String displayLabel;
				String columnName;
				String value;
				StringBuffer errorPop = new StringBuffer();
				int count=0;
				if(rule.contains(Constants.DELIMITER_DOUBLE_PIPE+Constants.DELIMITER_DOUBLE_PIPE)){
					ruleString = rule.split("\\|\\|\\|\\|");
					for(String ruleStr : ruleString){
						
					}
				}
				if (rule.contains(Constants.DELIMITER_DOUBLE_PIPE)) {
					ruleStr = rule.split("\\|\\|");
					for (String eachRule : ruleStr) {
						if(eachRule==null && eachRule.trim().length()<=0) continue;
						
						displayLabel=eachRule.split("\\|")[0];
						columnName=eachRule.split("\\|")[1].substring(4);
						value = eachRule.split("\\|")[3].replace("<OR>", "");
						
						logger.info("eachRule=="+eachRule);
						logger.info("displayLabel==="+displayLabel);
						logger.info("columnName==="+columnName);
						logger.info("value===="+value);
						logger.info(eachRule.split("|")[3]);
						String column;
						
						if(columnName.contains("udf")){
							column=columnName;
						}else{
							column=dbColumnMap.get(columnName);
						}
						logger.info("=====SELECT COUNT(*) FROM SkuFile  WHERE userId = "
								+ orgOwnerUserId + " and "+column+" is not null and "+column+"='"+value+"'");
						
						String query = "SELECT COUNT(*) FROM SkuFile  WHERE userId = "
								+ orgOwnerUserId + " and "+column+" is not null and "+column+"='"+value+"'";
						
						Long totalSize = (Long) skuFileDao.executeQuery("SELECT COUNT(*) FROM SkuFile  WHERE userId = "
								+ orgOwnerUserId + " and "+column+" is not null and "+column+"='"+value+"'").get(0);
						
						logger.info("totalSize======"+totalSize);
						
						if(totalSize>0){
							count++;
							exportMap.put(displayLabel, value);
						}else{
							if(errorPop.length()!=0) errorPop.append("\n");
							
							errorPop.append(displayLabel+" "+ value +" is not valid.");
						}
						
						
						
						
						logger.info("====errorPop====="+errorPop);
						
					}
				}else if(rule.contains(Constants.DELIMITER_PIPE) && rule!=null && rule.trim().length()>0){

				//	if(rule==null && rule.trim().length()<=0) continue;
					
					displayLabel=rule.split("\\|")[0];
					columnName=rule.split("\\|")[1].substring(4);
					value = rule.split("\\|")[3].replace("<OR>", "");
					
					logger.info("eachRule=="+rule);
					logger.info("displayLabel==="+displayLabel);
					logger.info("columnName==="+columnName);
					logger.info("value===="+value);
					logger.info(rule.split("|")[3]);
					String column;
					
					if(columnName.contains("udf")){
						column=columnName;
					}else{
						column=dbColumnMap.get(columnName);
					}
					logger.info("=====SELECT COUNT(*) FROM SkuFile  WHERE userId = "
							+ orgOwnerUserId + " and "+column+" is not null and "+column+"='"+value+"'");
					
					String query = "SELECT COUNT(*) FROM SkuFile  WHERE userId = "
							+ orgOwnerUserId + " and "+column+" is not null and "+column+"='"+value+"'";
					
					Long totalSize = (Long) skuFileDao.executeQuery("SELECT COUNT(*) FROM SkuFile  WHERE userId = "
							+ orgOwnerUserId + " and "+column+" is not null and "+column+"='"+value+"'").get(0);
					
					logger.info("totalSize======"+totalSize);
					
					if(totalSize>0){
						count++;
						exportMap.put(displayLabel, value);
					}else{
						if(errorPop.length()!=0) errorPop.append("\n");
						
						errorPop.append(displayLabel+" "+ value +" is not valid.");
					}
					
					
					
					
					logger.info("====errorPop====="+errorPop);
					
				
					
				}
				
				
				
				
				//ruleId.setValue(errorPop.toString());
				itemCountLblId.setValue(Integer.toString(count));
				logger.info("count is ==================="+count);
				logger.info("item count valueeeeeeeeee==========="+itemCountLblId.getValue());
				
				if(errorPop.length()!=0){
					MessageUtil.setMessage(errorPop.toString(),
							"color:blue", "TOP");
					return;
				}
			}
*/
			
			public String onClick$validateButtonId(){
				logger.info("Prepare rule");
				String[] ruleStr;
				String[] ruleStrAnd;
				String rule = null; 
				rule = saveRule(true); // "\\|\\|" 
				// Item Category|sku.item_category|string:does not contain|LG
				if(rule==null) {
					
					MessageUtil.setMessage("Please add at least one item attribute for creating discount rule.", "color:red;");
					return null;
					
				}
				if(rule!=null && !rule.isEmpty()) {
				String displayLabel;
				String columnName;
				String value;
				StringBuffer errorPop = new StringBuffer();
				StringBuffer comboErrorPop = new StringBuffer();
				String query="";
				long totalValidCount=0;
				long tempCount=0;
				exportMap = null;
								//if(rule.contains(Constants.DELIMITER_DOUBLE_PIPE+Constants.DELIMITER_DOUBLE_PIPE)){
					//ruleStrAnd = rule.split("\\|\\|\\|\\|");
					//for(String eachRuleStr : ruleStrAnd){
						
						if (rule.contains(Constants.DELIMITER_DOUBLE_PIPE)) {
							ruleStr = rule.split("\\|\\|");
							for (String eachRule : ruleStr) {
								if(eachRule==null && eachRule.trim().length()<=0) continue;
								
								displayLabel=eachRule.split("\\|")[0];
								if(displayLabel.equals(DiscountQtyPerItem)||displayLabel.equals(MinReceiptTotal)
										||displayLabel.equalsIgnoreCase("Item Price")) continue;
								columnName=eachRule.split("\\|")[1];
								value = eachRule.split("\\|")[3].replace("<OR>", "");
								if(value.contains(SYMPIPELABEL)) value = value.replace(SYMPIPELABEL,"|");
								logger.info("eachRule=="+eachRule);
								logger.info("displayLabel==="+displayLabel);
								logger.info("columnName==="+columnName);
								logger.info("value===="+value);
								logger.info(eachRule.split("|")[3]);
								String column;
								displayLabel = itemAttributesMap.get(displayLabel)!=null?itemAttributesMap.get(displayLabel):displayLabel;
								if(displayLabel.contains("UDF")||displayLabel.contains("udf"))displayLabel = udfDisplaySKUMap.get(displayLabel);
								if(columnName.contains("udf")){
									column=columnName;
								}else{
									column=dbColumnMap.get(columnName);
								}
								if(tempCount>0 && rule.contains("is in")) {
									MessageUtil.setMessage("One of is not allowed in combination rule.",
											"color:blue", "TOP");
									return null;
								}
								
								// comma seperated values are allowed for Desc and UDFs in Equals.
								String[] sku= ((columnName.equalsIgnoreCase("description") || 
										columnName.contains("udf")) && eachRule.split("\\|")[2].split(":")[1].equalsIgnoreCase("is"))?value.split("isCommaAllowed"):value.split(",");
								String tempStr1="";
								for (int i = 0; i < sku.length; i++) {
									long validCount=0;
									String tempStr=StringEscapeUtils.escapeSql(sku[i]);
								if(eachRule.split("\\|")[2].contains("contains")) {
									tempStr = "'%" + tempStr + "%'";
									if(tempStr1.length()>0) tempStr1+=",";
									tempStr1+=tempStr;
									
								logger.info("=====SELECT COUNT(*) FROM SkuFile  WHERE userId = "
										+ orgOwnerUserId + " and "+column+" is not null and "+column+" LIKE "+tempStr+" ");
								validCount = (Long) skuFileDao.executeQuery("SELECT COUNT(*) FROM SkuFile  WHERE userId = "
										+ orgOwnerUserId + " and "+column+" is not null and "+column+" LIKE "+tempStr+" ").get(0);
								
								if(validCount<=0){
									if(errorPop.length()!=0) errorPop.append("\n");
									
									errorPop.append(displayLabel+" "+ sku[i] +" is not valid.");
								}
								if(query.length()>0) query+=sku.length>1?" OR ":" AND ";
								query += "("+column+" is not null and "+column+" LIKE ("+tempStr1+"))";
								}else {
									tempStr = "'" + tempStr + "'";
									if(tempStr1.length()>0) tempStr1+=",";
									tempStr1+=tempStr;
									
									logger.info("=====SELECT COUNT(*) FROM SkuFile  WHERE userId = "
											+ orgOwnerUserId + " and "+column+" is not null and "+column+" IN ("+tempStr+")");
									validCount = (Long) skuFileDao.executeQuery("SELECT COUNT(*) FROM SkuFile  WHERE userId = "
											+ orgOwnerUserId + " and "+column+" is not null and "+column+" IN ("+tempStr+")").get(0);
									
									if(validCount<=0){
										if(errorPop.length()!=0) errorPop.append("\n");
										
										errorPop.append(displayLabel+" "+ sku[i] +" is not valid.");
									}
									if(query.length()>0) query+=sku.length>1?" OR ":" AND ";
									query += "("+column+" is not null and "+column+" IN ("+tempStr1+"))";
								}
								tempCount=validCount;
							}
							}
							if(query.length()==0) {
								//No item attributes are dragged
								MessageUtil.setMessage("Please select at least one item attribute for creating discount rule.", "color:red");
								return "";
							}
								totalValidCount = (Long) skuFileDao.executeQuery("SELECT COUNT(*) FROM SkuFile  WHERE userId = "
										+ orgOwnerUserId + " and ("+query+")").get(0);;
								
								logger.info("totalSize======"+totalValidCount);
								exportMap = (List<SkuFile>)skuFileDao.executeQuery("FROM SkuFile  WHERE userId = "
										+ orgOwnerUserId + " and ("+query+")");
								if(totalValidCount==0) {
									if(comboErrorPop.length()==0)comboErrorPop.append("No items exist with the given combination of attribute values.");
								}
								//}
							
							//}
							
							
						/*}else if(rule.contains(Constants.DELIMITER_PIPE) && rule!=null && rule.trim().length()>0){

						//	if(rule==null && rule.trim().length()<=0) continue;
							
							displayLabel=rule.split("\\|")[0];
							columnName=rule.split("\\|")[1].substring(4);
							value = rule.split("\\|")[3].replace("<OR>", "");
							
							logger.info("eachRule=="+rule);
							logger.info("displayLabel==="+displayLabel);
							logger.info("columnName==="+columnName);
							logger.info("value===="+value);
							logger.info(rule.split("|")[3]);
							String column;
							
							if(columnName.contains("udf")){
								column=columnName;
							}else{
								column=dbColumnMap.get(columnName);
							}
							logger.info("=====SELECT COUNT(*) FROM SkuFile  WHERE userId = "
									+ orgOwnerUserId + " and "+column+" is not null and "+column+"='"+value+"'");
							
							String query = "SELECT COUNT(*) FROM SkuFile  WHERE userId = "
									+ orgOwnerUserId + " and "+column+" is not null and "+column+"='"+value+"'";
							
							Long totalSize = (Long) skuFileDao.executeQuery("SELECT COUNT(*) FROM SkuFile  WHERE userId = "
									+ orgOwnerUserId + " and "+column+" is not null and "+column+"='"+value+"'").get(0);
							
							logger.info("totalSize======"+totalSize);
							
							if(totalSize>0){
								count++;
								exportMap.put(displayLabel, value);
							}else{
								if(errorPop.length()!=0) errorPop.append("\n");
								
								errorPop.append(displayLabel+" "+ value +" is not valid.");
							}
							
							
							
							
							logger.info("====errorPop====="+errorPop);
							
						
							
						}*/
						}
				//}
				else if (rule.contains(Constants.DELIMITER_PIPE)) {
					
					MessageUtil.setMessage("Please select at least one item attribute for creating discount rule.",
							"color:red", "TOP");
					return null;
					/*
					
					String[] ORrules=rule.split("<OR>");
					if(ORrules.length>=2) {
						for(String orRule:ORrules) {
						
						displayLabel=orRule.split("\\|")[0];
						if(displayLabel.equalsIgnoreCase(DiscountQtyPerItem)||displayLabel.equalsIgnoreCase(MinReceiptTotal)) continue;
						columnName=orRule.split("\\|")[1].substring(4);
						value = orRule.split("\\|")[3].replace("<OR>", "");
						
						logger.info("eachRule=="+rule);
						logger.info("displayLabel==="+displayLabel);
						logger.info("columnName==="+columnName);
						logger.info("value===="+value);
						logger.info(rule.split("|")[3]);
						String column;
						
						if(columnName.contains("udf")){
							column=columnName;
						}else{
							column=dbColumnMap.get(columnName);
						}
						logger.info("=====SELECT COUNT(*) FROM SkuFile  WHERE userId = "
								+ orgOwnerUserId + " and "+column+" is not null and "+column+" IN ('"+value+"')");
						
						String query = "SELECT COUNT(*) FROM SkuFile  WHERE userId = "
								+ orgOwnerUserId + " and "+column+" is not null and "+column+"='"+value+"'";
						
						Long totalSize = (Long) skuFileDao.executeQuery("SELECT COUNT(*) FROM SkuFile  WHERE userId = "
								+ orgOwnerUserId + " and "+column+" is not null and "+column+" IN ('"+value+"')").get(0);
						
						logger.info("totalSize======"+totalSize);
						
						if(totalSize>0){
							count++;
							exportMap.put(displayLabel, value);
						}else{
							if(errorPop.length()!=0) errorPop.append("\n");
							
							errorPop.append(displayLabel+" "+ value +" is not valid.");
						}
						
						
						
						
						logger.info("====errorPop====="+errorPop);
						}
					}else {
						displayLabel=rule.split("\\|")[0];
						if(!(displayLabel.equalsIgnoreCase(DiscountQtyPerItem)||displayLabel.equalsIgnoreCase(MinReceiptTotal))) {
						columnName=rule.split("\\|")[1];
						value = rule.split("\\|")[3].replace("<OR>", "");
						
						logger.info("eachRule=="+rule);
						logger.info("displayLabel==="+displayLabel);
						logger.info("columnName==="+columnName);
						logger.info("value===="+value);
						logger.info(rule.split("|")[3]);
						String column;
						
						if(columnName.contains("udf")){
							column=columnName;
						}else{
							column=dbColumnMap.get(columnName);
						}
						logger.info("=====SELECT COUNT(*) FROM SkuFile  WHERE userId = "
								+ orgOwnerUserId + " and "+column+" is not null and "+column+" IN ('"+value+"')");
						
						String query = "SELECT COUNT(*) FROM SkuFile  WHERE userId = "
								+ orgOwnerUserId + " and "+column+" is not null and "+column+"='"+value+"'";
						
						Long totalSize = (Long) skuFileDao.executeQuery("SELECT COUNT(*) FROM SkuFile  WHERE userId = "
								+ orgOwnerUserId + " and "+column+" is not null and "+column+" IN ('"+value+"')").get(0);
						
						logger.info("totalSize======"+totalSize);
						
						if(totalSize>0){
							count++;
							exportMap.put(displayLabel, value);
						}else{
							if(errorPop.length()!=0) errorPop.append("\n");
							
							errorPop.append(displayLabel+" "+ value +" is not valid.");
						}
						
						
					}
						
						logger.info("====errorPop====="+errorPop);
					}
						
				*/}
				//ruleId.setValue(errorPop.toString());
				itemCountLblId.setValue(Long.toString(totalValidCount));
				logger.info("count is ==================="+count);
				logger.info("item count valueeeeeeeeee==========="+itemCountLblId.getValue());
				
				/*if(errorPop.length()!=0){
					MessageUtil.setMessage(errorPop.toString().length()>100?errorPop.toString().substring(0,99)+"...":errorPop.toString(),
							"color:blue", "TOP");
					return "";
				}
				if(comboErrorPop.length()!=0){
					MessageUtil.setMessage(comboErrorPop.toString().length()>100?comboErrorPop.toString().substring(0,99)+"...":comboErrorPop.toString(),
							"color:blue", "TOP");
					return "";
				}*/
				if(errorPop.length()!=0){
				int confirm = Messagebox.show(errorPop.toString().length()>100?errorPop.toString().substring(0,99)+"...":errorPop.toString()+"\n"
						+ "Are you sure you want to proceed?", "Create Discount Rule",
						Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
				if (confirm != Messagebox.OK) {
					return "";
				}
				return rule;
				}
				if(comboErrorPop.length()!=0){
					int confirm = Messagebox.show(comboErrorPop.toString().length()>100?comboErrorPop.toString().substring(0,99)+"...":comboErrorPop.toString()+"\n"
							+ "Are you sure you want to proceed?", "Create Discount Rule",
							Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
					if (confirm != Messagebox.OK) {
						return "";
					}
					return rule;
				}
				}
				return rule;
			}

			
			private String prepareSegmentRule(Div ruleVDiv) {
				
				//prepare segment rule for each div
				
				StringBuffer ruleSB = new StringBuffer();
				
				List childDivList = ruleVDiv.getChildren();
				Div ruleDiv = null;
				String operator ="";
				
				boolean retValue=true;
				
				boolean isValid = true;
				Coupons coupon=null;
				boolean expiryflag=false;
				
				for (Object object : childDivList) {
					
					if(isValid==false) retValue=false;
					
					//Reset the isValid
					isValid = true;
					if(object instanceof Div ){
						Label fielslbl=null;
						Listbox itemlbl=null;
						List columns=null;
						String col=null;
						String dispLabel=null;
						
				
						ruleDiv = (Div)object;
						
						if(ruleDiv.getSclass().contains("drop_")) {
							continue;
						}
						columns = ruleDiv.getChildren();//gives the rule division
						fielslbl = (Label)columns.get(0); //
						dispLabel = (String)fielslbl.getAttribute("item");
						//if(dispLabel.equalsIgnoreCase("Item Quantity")) continue;
						itemlbl = (Listbox)columns.get(1);
						String multipleCol  = (String)fielslbl.getAttribute("columnName");
						 if(multipleCol != null ) {
							 if(multipleCol.contains("::")) {
							 col = multipleCol.split("::")[1];
							 ruleVDiv.setAttribute("columnName", multipleCol.split("::")[0]);
							 ruleVDiv.setAttribute("replaceColumnName", col);
						 }
						 else{
							 col = (String)fielslbl.getAttribute("columnName");
						 }
						 }
						
						logger.debug("dispLabel ::"+dispLabel+ " colName ::"+col);
						
						if(dispLabel.equalsIgnoreCase("Item Attribute")) {
						col = (String)itemlbl.getAttribute("columnName");
						CouponsEnum enumValue = (CouponsEnum)(itemlbl.getSelectedItem().getValue());
						logger.info(""+enumValue.getDispLabel());
				//		ruleSB.append(itemlbl.getSelectedItem().getLabel()+"|");
						ruleSB.append(enumValue.getDispLabel()+"|");//teke me to the place where we render the below rows
						}else {
						ruleSB.append(dispLabel+"|");
						}
						
						if( col!= null && col.startsWith("total.")) {
							
							continue;
						}
						if(col != null && col.trim().length() > 0) {
							ruleSB.append(col+"|");
						}
						
						//append type
						String type ;
						String constraint;
						CouponsEnum couponsEnum;
						Listbox ruleLb = (Listbox)columns.get(1);
						Listbox formLb = (Listbox)columns.get(10);
						Listbox ltylb = (Listbox)columns.get(3);
						
							if(ruleLb.getSelectedIndex() == -1) {
							
							couponsEnum = (CouponsEnum)ruleLb.getItemAtIndex(0).getValue();
							
							} else {
								
								couponsEnum = (CouponsEnum)ruleLb.getSelectedItem().getValue();
							}
						if(col == null || col.trim().length() == 0) {
							
							col = couponsEnum.getColumnName();
							if(col != null && col.trim().length() > 0) {
								
								ruleSB.append(col+"|");
							}
							
							
						}
						
						String valStr = "";
						boolean isIgnoreYear = false;
						boolean isIssuedOn=false;
						
						//append operation/constraint
						
							Listbox operatorLb = (Listbox)columns.get(4);
							if(operatorLb.isVisible()) {
								if(operatorLb.getSelectedIndex() == -1) {
									
									couponsEnum = (CouponsEnum)operatorLb.getItemAtIndex(0).getValue();
									
									valStr  = operatorLb.getItemAtIndex(0).getLabel();
									
									logger.debug("valStr====>"+valStr);
									
									couponsEnum = (CouponsEnum)operatorLb.getItemAtIndex(0).getValue();
									
									
									if(couponsEnum.getColumnValue() != null && !couponsEnum.getColumnValue().equals("") ) {
										valStr = couponsEnum.getColumnValue();
									}
									
								} else {
									Object obj = operatorLb.getSelectedItem().getValue();
									//for another lty list box
									if(obj instanceof CouponsEnum){
										couponsEnum = (CouponsEnum)operatorLb.getSelectedItem().getValue();
										valStr  = operatorLb.getSelectedItem().getLabel();
										if(couponsEnum.getColumnValue() != null && !couponsEnum.getColumnValue().equals("") ) {
											valStr = couponsEnum.getColumnValue();
											if(formLb.isVisible()) {
												valStr = valStr+Constants.DELIMETER_COLON+formLb.getSelectedItem().getLabel();
												
												
											}//if
										}
									}
									else if (obj instanceof Long){
										valStr = operatorLb.getSelectedItem().getValue().toString();
									}
									else if(obj == null){
										isValid = false;
										setErrorToDiv(ruleDiv, isValid);
										continue;
									}
								}
								
							}
							
							if(col == null || col.trim().length() == 0) {
								
								col = couponsEnum.getColumnName();
								
								if(col != null && col.trim().length() > 0) {
									
									ruleSB.append(col+"|");
								}
								
								
							}
							
							coupon =(Coupons)purchaseAttrLbId.getAttribute("CouponObj");
							type = couponsEnum.getType();
							logger.info("couponsEnum "+couponsEnum.getColumnValue());
							ruleSB.append(type+":");
						    constraint = couponsEnum.getToken();
						    ruleSB.append(constraint+"|");
							//}
						
						//append value
				/*		if(type.equalsIgnoreCase("Date")) {
							
							//Datebox dateBox1 = (Datebox)columns.get(9);
							//Datebox dateBox2 = (Datebox)columns.get(10);
							Listbox numberLb = (Listbox)columns.get(4);
							Decimalbox numberbox1 = (Decimalbox)columns.get(7);
							Decimalbox numberbox2 = (Decimalbox)columns.get(8);
							
							if(!dateBox1.isVisible() && !dateBox2.isVisible() && !numberLb.isVisible() && 
									!numberbox1.isVisible() && !numberbox2.isVisible()) {
								
								if(valStr.isEmpty()) {
									
									valStr = couponsEnum.getColumnValue();
								}
								ruleSB.append(valStr);
								
								
							}//if
							
							if(dateBox1.isVisible()) {
							
								if(dateBox1.getValue() == null) {
									
								//	MessageUtil.setMessage("Please provide the value for the date.", "color:red", "TOP");
									isValid = false;
									setErrorToDiv(ruleDiv, isValid);
									continue;
									
								}
								
								ruleSB.append(dateFormat.format(dateBox1.getValue()));
								if(couponsEnum.getDispLabel().equals("between") || dateBox2.isVisible()){
									
									if(dateBox2.getValue() == null){
										
								//		MessageUtil.setMessage("Please provide the value for the date.", "color:red", "TOP");
										isValid = false;
										setErrorToDiv(ruleDiv, isValid);
										continue;
									}
									
									if(dateBox1.getValue().after(dateBox2.getValue())) {
								//		MessageUtil.setMessage("First date must be later than second date. ", "color:red", "TOP");
										isValid = false;
										setErrorToDiv(ruleDiv, isValid);
										continue;
									}
									ruleSB.append("|"+dateFormat.format(dateBox2.getValue()));
									
								}
								
							}
							else if(numberbox1.isVisible()) {
								

								
								if(numberbox1.getValue() == null) {
								//	MessageUtil.setMessage("Value should not be empty. ", "color:red", "TOP");
									isValid = false;
									setErrorToDiv(ruleDiv, isValid);
									continue;
								}
								ruleSB.append(numberbox1.getValue());
								if(numberbox2.isVisible()) {
									
									if(numberbox2.getValue() == null) {
										//MessageUtil.setMessage("Value should not be empty. ", "color:red", "TOP");
										isValid = false;
										setErrorToDiv(ruleDiv, isValid);
										continue;
									}
									if(numberbox2.getValue().compareTo( numberbox1.getValue()) == -1 ) {
										isValid = false;
										setErrorToDiv(ruleDiv, isValid);
										continue;
										
									}
									ruleSB.append("|"+numberbox2.getValue());
									
								}//if
								
								
							}
							else if(ruleSB.toString().endsWith("|")) { //need to be generic
								
								ruleSB.append("now()");
							}
						}//if date
						else */if(type.equalsIgnoreCase("string"))  {
							
							logger.info(" type == string");
							
							Textbox txtbox = (Textbox)columns.get(5);
							Combobox cb1 = (Combobox)columns.get(11);
							Combobox cb2 = (Combobox)columns.get(12);
							
							if(!txtbox.isVisible() && !cb1.isVisible() && !cb2.isVisible()) {
								if(valStr.isEmpty()) {
									valStr = couponsEnum.getColumnValue();
								}
								ruleSB.append(valStr);
								
							}
							else {
								
								if(txtbox.isVisible()) 	valStr = txtbox.getText().trim();
								if(!couponsEnum.isAllowCommas() && valStr.contains(Constants.DELIMETER_COMMA)) {
									MessageUtil.setMessage("Comma seperated values cannot be given for Equals.", "color:red", "TOP");
									isValid = false;
									setErrorToDiv(ruleDiv, isValid);
									continue;
								}
								isCommaAllowed = !couponsEnum.getDispLabel().equalsIgnoreCase("One of") && couponsEnum.isAllowCommas();
								if(valStr.contains("|")) valStr = valStr.replace("|",SYMPIPELABEL);
								else if(cb1.isVisible()) {
									
									
									valStr = cb1.getValue().trim();
									logger.info(" got combo value as ::"+valStr);
								}
								
								if( valStr.length() <= 0) {
								//	MessageUtil.setMessage("Value should not be empty. ", "color:red", "TOP");
									isValid = false;
									setErrorToDiv(ruleDiv, isValid);
									continue;
								}
								/*if(valStr.contains("|")) {
								//	MessageUtil.setMessage("Value should not contains pipe(|).", "color:red", "TOP");
									isValid = false;
									setErrorToDiv(ruleDiv, isValid);
									continue;
								}*/
								 
								ruleSB.append(valStr);
							}
						}// else if
						else if(type.equalsIgnoreCase("number"))  {
							
							/*Textbox txtbox = (Textbox)columns.get(5);
							Combobox cb1 = (Combobox)columns.get(11);
							Combobox cb2 = (Combobox)columns.get(12);*/
							
							Textbox txtbox = (Textbox)columns.get(5);
							Combobox cb1 = (Combobox)columns.get(11);
							Combobox cb2 = (Combobox)columns.get(12);
							
							Decimalbox numberbox1 = (Decimalbox)columns.get(6);
							Decimalbox numberbox2 = (Decimalbox)columns.get(7);
							//Listbox campLb = (Listbox)columns.get(10); 
							
							if(!numberbox1.isVisible() && !numberbox2.isVisible() && !txtbox.isVisible() && !cb1.isVisible() && !cb2.isVisible() ){//&& !campLb.isVisible()){
								
								if(valStr.isEmpty()) {
									
									valStr = couponsEnum.getColumnValue();
								}
								ruleSB.append(valStr);
								
							}
							else if(numberbox1.isVisible()) {
							
								if(numberbox1.getValue() == null || numberbox1.getValue().compareTo(BigDecimal.ZERO) <0 ) {
								//	MessageUtil.setMessage("Value should not be empty. ", "color:red", "TOP");
									isValid = false;
									setErrorToDiv(ruleDiv, isValid);
									continue;
								}
								ruleSB.append(numberbox1.getValue());
								if( (couponsEnum.getToken().equalsIgnoreCase("between") 
										|| couponsEnum.getToken().equalsIgnoreCase("range") ) 
										&& numberbox2.isVisible()) {
									
									if(numberbox2.getValue() == null||numberbox2.getValue().compareTo(BigDecimal.ZERO) <0) {
										//MessageUtil.setMessage("Value should not be empty. ", "color:red", "TOP");
										isValid = false;
										setErrorToDiv(ruleDiv, isValid);
										continue;
									}
									if(numberbox2.getValue().compareTo( numberbox1.getValue()) == -1 || numberbox2.getValue().compareTo( numberbox1.getValue()) == 0) {
										isValid = false;
										setErrorToDiv(ruleDiv, isValid);
										continue;
										
									}
									ruleSB.append("|"+numberbox2.getValue());
									
								}//if
								
								
							}//if
							else if(cb1.isVisible()) {
								String cb1ValStr = cb1.getValue();
								String cb2ValStr = cb2.getValue();

								
								if(cb1ValStr == null || cb1ValStr.trim().length() == 0 ) {
								//	MessageUtil.setMessage("Value should not be empty. ", "color:red", "TOP");
									isValid = false;
									setErrorToDiv(ruleDiv, isValid);
									continue;
								}
								
								double cb1ValDbl=0;
								
								try{
									
									if(cb1ValStr.contains(",") ) {
										
										if(couponsEnum.getToken().toLowerCase().contains("is in")) {
											String[] valArr = cb1ValStr.split(",");
											for (int i = 0; i < valArr.length; i++) {
												
												cb1ValDbl = Double.parseDouble(valArr[i]);
												
											}//for
										}else {
											
											isValid = false;
											setErrorToDiv(ruleDiv, isValid);
											continue;
										}
										
									}
									else{
										
										cb1ValDbl = Double.parseDouble(cb1ValStr);
										
									}
									
								}catch (NumberFormatException e) {
									isValid = false;
									setErrorToDiv(ruleDiv, isValid);
									continue;
								
								}
								
								ruleSB.append(cb1ValStr);
								if(couponsEnum.getToken().equalsIgnoreCase("between") && cb2.isVisible()) {
									
									if(cb2ValStr == null || cb2ValStr.trim().length() == 0) {
										//MessageUtil.setMessage("Value should not be empty. ", "color:red", "TOP");
										isValid = false;
										setErrorToDiv(ruleDiv, isValid);
										continue;
									}
									double cb2ValDbl = 0;
									try{
										
										cb2ValDbl = Double.parseDouble(cb2ValStr);
										
									}catch (NumberFormatException e) {
										isValid = false;
										setErrorToDiv(ruleDiv, isValid);
										continue;
									
									}
									if(cb1ValDbl >= cb2ValDbl) {
										isValid = false;
										setErrorToDiv(ruleDiv, isValid);
										continue;
										
									}
									ruleSB.append("|"+cb2ValStr);
									
								}//if
								
								
								
								
								
							}// else if
							else if(txtbox.isVisible() && !cb1.isVisible()) {
								
								try {
									String valueStr = txtbox.getText();
									if(valueStr.contains(",")) {
										String[] valArr = valueStr.split(",");
										for (int i = 0; i < valArr.length; i++) {
											if(col != null && col.equals("c.zip") ){
												String value = valArr[i].trim();
											}else{
												
												int value = Integer.parseInt(valArr[i].trim());
											}
											
										}//for
										
									}else {
										if(col != null && col.equals("c.zip") ){
											String value = txtbox.getText().trim();
										}else{
											
											int value = Integer.parseInt(txtbox.getText().trim());
										}
									
									}
									
								}  catch (NumberFormatException e) {
									
									isValid = false;
									setErrorToDiv(ruleDiv, isValid);
									continue;
								}
								ruleSB.append(txtbox.getText());
								
							}
							
							
						
					}//else if
					else if (type.equalsIgnoreCase("boolean")) {
						
						if(valStr.isEmpty()) {
							
							valStr = couponsEnum.getColumnValue();
							
						}
						ruleSB.append(valStr);
						
					}
					
					 
					isValid = true;
					if(ruleDiv!=null)
					setErrorToDiv(ruleDiv, isValid);
						
					if(childDivList.size() > 1 ) operator ="<OR>";
					ruleSB.append(operator);
					
				}//if
			}//for
				
				if(isValid==false) retValue=false;
				
				if(!retValue) return null;
				
				logger.info("ruleSb :: "+ruleSB.toString());
				
				
				return ruleSB.toString();
				
		}// prepareSegmentRule(Vbox ruleVb)
			
			public void setErrorToDiv(Div errorDiv, boolean isValid ) {
				
				if(isValid) {
					errorDiv.setSclass("segment_child_div");
				}
				else {
					errorDiv.setSclass("segment_child_error_div");
				}
				
			}

			public String saveRule(boolean isEdit) {
				

				
				
				String operator = "";
				List<Component> childVboxList = new ArrayList<Component>();
				
				//if(targetDivHasChildren(profileAttributeDivId))childVboxList.addAll(profileAttributeDivId.getChildren());
				if(targetDivHasChildren(purIntAttributeDivId))childVboxList.addAll(purIntAttributeDivId.getChildren());
			    //if(targetDivHasChildren(intAttributeDivId)){}
				
				if(childVboxList.size() == 0) {
					
					//MessageUtil.setMessage("Please add at least one rule.", "color:red;");
					return null;
					
				}

				Div chilVDiv = null;
				
				StringBuffer segmentRuleSB = new StringBuffer();//"all:"+((MailingList)dispMlLBoxId.getSelectedItem().getValue()).getListId());
				String ruleStr = null;
				boolean allValid=true;
				
				for (Object obj : childVboxList) {
					
					if(obj instanceof Div) {
						
						boolean isValid = true;
						for(Object object : ((Div)obj).getChildren()) {
							
							if(isValid==false) allValid=false;
							
							//Reset
							isValid = true;
							
							if(object instanceof Div) {
								
								
								chilVDiv = (Div)object;
								if(chilVDiv.getSclass().contains("drop_")) {
									continue;
								}
								
								ruleStr = prepareSegmentRule(chilVDiv);
								if(ruleStr == null) {
									isValid = false;
								}
								
							///	if(childVboxList.size() > 1)  operator = "<AND>";
								
								if(ruleStr != null && ruleStr.trim().length()>0) {	
									logger.info("segmentRuleDB============+"+segmentRuleSB);
									logger.info("segmentRuleDB============+"+segmentRuleSB!=null);
									logger.info("segmentRuleDB============+"+segmentRuleSB.length());

									if(segmentRuleSB!=null && segmentRuleSB.length()!=0) segmentRuleSB.append("||");
										
										segmentRuleSB.append(ruleStr+operator);
								}
								if(ruleStr != null && chilVDiv.getAttribute("columnName")!=null && chilVDiv.getAttribute("replaceColumnName")!=null ) {
									ruleStr = ruleStr.replace((String)chilVDiv.getAttribute("replaceColumnName"), (String)chilVDiv.getAttribute("columnName"));
									if(segmentRuleSB!=null) segmentRuleSB.append("||");
									
									segmentRuleSB.append(ruleStr);
								}
								
								
							}//ruleDiv
							
						}//tempDiv added for And
						
						
						if(isValid==false) allValid=false;
						
					}//if obj
					
				}//for each vb
				if(!allValid) {
					logger.warn(" >>>>>>>>>>>>> Segmentation validation failed ");
					
					MessageUtil.setMessage("Please provide proper value(s) for highlighted rule(s).", "color:red", "TOP");
					return null;
				}
				
				
				/*if(segmentRuleSB.toString().trim().length()==0) {
					
					MessageUtil.setMessage("There are no valid rules to add (as of now, derivative filter rules are ignored).", "color:red","TOP");
					return segmentRuleSB.toString();
				}*/
				
			/*	
				String[] rulesArr=segmentRuleSB.toString().split("||");
				StringBuilder finalStr= new StringBuilder();
				for (String rule : rulesArr) {
					if(rule!=null && !rule.isEmpty()){
						if(finalStr!=null) finalStr.append(",");
						finalStr.append(rule.split("|")[0] + " = " + rule.split("|")[]);
					}
					
				}
				
				*/
				logger.debug("finalStr ::"+segmentRuleSB.toString());
				if(segmentRuleSB.toString().isEmpty()) {
					
					MessageUtil.setMessage("Please add at least one rule.", "color:red;");
					return "";
					
				}
			return	segmentRuleSB.toString();
			//	return finalStr.toString();
			}
			public String saveDiscountAttributes() {

				

				
				
				String operator = "";
				List<Component> childVboxList = new ArrayList<Component>();
				
				//if(targetDivHasChildren(profileAttributeDivId))childVboxList.addAll(profileAttributeDivId.getChildren());
				if(targetDivHasChildren(discountAttributeDivId))childVboxList.addAll(discountAttributeDivId.getChildren());
			    //if(targetDivHasChildren(intAttributeDivId)){}
				
				if(childVboxList.size() == 0) {
					
					MessageUtil.setMessage("Please add Discount Attribute rules.", "color:red;");
					return "";
					
				}

				Div chilVDiv = null;
				
				StringBuffer segmentRuleSB = new StringBuffer();//"all:"+((MailingList)dispMlLBoxId.getSelectedItem().getValue()).getListId());
				String ruleStr = null;
				boolean allValid=true;
				
				for (Object obj : childVboxList) {
					
					if(obj instanceof Div) {
						
						boolean isValid = true;
						for(Object object : ((Div)obj).getChildren()) {
							
							if(isValid==false) allValid=false;
							
							//Reset
							isValid = true;
							
							if(object instanceof Div) {
								
								
								chilVDiv = (Div)object;
								if(chilVDiv.getSclass().contains("drop_")) {
									continue;
								}
								
								ruleStr = prepareSegmentRule(chilVDiv);
								if(ruleStr == null) {
									isValid = false;
								}
								
							///	if(childVboxList.size() > 1)  operator = "<AND>";
								
								if(ruleStr != null && ruleStr.trim().length()>0) {	
									logger.info("discsegmentRuleDB============+"+segmentRuleSB);
									logger.info("discsegmentRuleDB============+"+segmentRuleSB!=null);
									logger.info("discsegmentRuleDB============+"+segmentRuleSB.length());

									if(segmentRuleSB!=null && segmentRuleSB.length()!=0) segmentRuleSB.append("||");
										
										segmentRuleSB.append(ruleStr+operator);
								}
								if(ruleStr != null && chilVDiv.getAttribute("columnName")!=null && chilVDiv.getAttribute("replaceColumnName")!=null ) {
									ruleStr = ruleStr.replace((String)chilVDiv.getAttribute("replaceColumnName"), (String)chilVDiv.getAttribute("columnName"));
									if(segmentRuleSB!=null) segmentRuleSB.append("||");
									
									segmentRuleSB.append(ruleStr);
								}
								
								
							}//ruleDiv
							
						}//tempDiv added for And
						
						
						if(isValid==false) allValid=false;
						
					}//if obj
					
				}//for each vb
				if(!allValid) {
					logger.warn(" >>>>>>>>>>>>> Segmentation validation failed ");
					
					MessageUtil.setMessage("Please provide proper value(s) for highlighted rule(s).", "color:red", "TOP");
					return null;
				}
				
				
				/*if(segmentRuleSB.toString().trim().length()==0) {
					
					MessageUtil.setMessage("There are no valid rules to add (as of now, derivative filter rules are ignored).", "color:red","TOP");
					return segmentRuleSB.toString();
				}*/
				logger.debug("finalStr ::"+segmentRuleSB.toString());
				
			return	segmentRuleSB.toString();
			
			}
			
			public boolean targetDivHasChildren(Div targetDiv) {
				List<Component> childComponentList = targetDiv.getChildren();
				if(childComponentList.size() == 0 || (childComponentList.size() == 1 && 
						childComponentList.get(0) instanceof Div )) {
					
					Div orANDDiv = (Div)childComponentList.get(0);
					if(orANDDiv.getSclass() != null && orANDDiv.getSclass().equals("drop_and_div") ) return false;
					else return true;
					
				}
				else return true;
				
				
			}
	
			public void onDrop$discountAttributeDivId(Event event) {
				

				
				logger.debug("---ControllerSide -- Drop Event ="+event);

				DropEvent dropEvent = ((DropEvent) ((ForwardEvent) event).getOrigin());
				Component dragged = dropEvent.getDragged();
				
				logger.debug("----------Dragged target ="+dragged);
				
				if (dragged instanceof Listitem) {
					//Receipt total and item price cannot be dragged twice
					List<Component> childVboxList = new ArrayList<Component>();
					childVboxList = discountAttributeDivId.getChildren();
					if(childVboxList!=null) {
					Div chilVDiv = null;
					boolean isReceiptTotalExists = false;
					boolean isItemPriceExists = false;
					Listitem item = (Listitem) dragged;
					CouponsEnum couponsEnum = (CouponsEnum) item.getValue();
					for (Object obj : childVboxList) {
										
										if(obj instanceof Div) {
											
											//boolean isValid = true;
											for(Object object : ((Div)obj).getChildren()) {
												
												if(object instanceof Div) {
													
													chilVDiv = (Div)object;
													if(chilVDiv.getSclass().contains("drop_")) {
														continue;
													}
													List childDivList = chilVDiv.getChildren();
													for (Object objects : childDivList) {
														if(objects instanceof Div ){
															Label fielslbl=null;
															List columns=null;
															String dispLabel=null;
															chilVDiv = (Div)objects;
															
															if(chilVDiv.getSclass().contains("drop_")) {
																continue;
															}
															columns = chilVDiv.getChildren();
															logger.info(" "+chilVDiv.getAttribute("item"));
															fielslbl = (Label)columns.get(0);
															dispLabel = (String)fielslbl.getAttribute("item");
															logger.info("DispLabel "+dispLabel);
															logger.info(couponsEnum.getDispLabel());
															
															if(!dispLabel.equalsIgnoreCase(couponsEnum.getDispLabel()))
															{
																if((!dispLabel.equalsIgnoreCase("Discount Type") && !couponsEnum.getDispLabel().equalsIgnoreCase("Eligible Items"))
																	&&(!dispLabel.equalsIgnoreCase("Eligible Items") && !couponsEnum.getDispLabel().equalsIgnoreCase("Discount Type"))	)
																{	MessageUtil.setMessage("Only one Discount attribute allowed", "color:red;");
																return;
															}
															}
															if(dispLabel.equalsIgnoreCase(discounttype1)&&couponsEnum.getDispLabel().equalsIgnoreCase(dispLabel)) {
																	MessageUtil.setMessage("Discount Type is already in use", "color:red;");
																	return;
																}
															
													
																isItemPriceExists=true;
															}
														}
													}
												}
											}
										}
					}
					// new object
					Listitem item = (Listitem) dragged;
					CouponsEnum couponsEnum = (CouponsEnum) item.getValue();
					final Listitem itemForChild = item;
					
					Div tempDiv = new Div();
					tempDiv.setSclass("");
					tempDiv.setParent(discountAttributeDivId);
					
					Label AndLabel = new Label("AND");
					AndLabel.setStyle("display:inline-block; width:20px; padding:0 5px;margin:0 10px;font-weight:bold;");
					AndLabel.setParent(tempDiv); 
					
					
					Div vdiv = new Div();//this is the outer component
					vdiv.setDroppable("DisSegment");
					vdiv.setParent(tempDiv);
					vdiv.setSclass("segment_parent_div");
					updateAndLabel(discountAttributeDivId);
					
					Div obj = createNewRuleDiv(item);
					obj.setParent(vdiv);
			
					
					vdiv.addEventListener("onDrop", myDropListener);
				}
				}
				
			
			public void onClick$exportBtnId(){
				
				logger.debug("-- just entered into exportCSV --");
				Map<String, String> udfSKUMap = new HashMap<String, String>();
				boolean UDF1,UDF2,UDF3,UDF4,UDF5,UDF6,UDF7,UDF8,UDF9,UDF10,UDF11,UDF12,UDF13,UDF14,UDF15;
				
				if(itemCountLblId.getValue()==null || itemCountLblId.getValue().isEmpty()) {
					Messagebox.show("Please Validate the rules to export.","Info", Messagebox.OK,Messagebox.INFORMATION);
					return;
				}else if(itemCountLblId.getValue().equalsIgnoreCase("0")) {
					Messagebox.show("No valid inventory data found for the given rules.","Info", Messagebox.OK,Messagebox.INFORMATION);
					return;
				}
				StringBuffer sb = new StringBuffer();
				StringBuffer temp = new StringBuffer();
				JdbcResultsetHandler jdbcResultsetHandler = null;
				ResultSet resultSet = null;

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
				
				
				
				
							
							String filePath = exportDir +  "DC_Valid_SKUs" + System.currentTimeMillis() + "." +"csv";
								MyCalendar.calendarToString(Calendar.getInstance(), MyCalendar.FORMAT_YEARTOSEC, clientTimeZone);
								try {
					
										logger.debug("Download File path : " + filePath);
										File file = new File(filePath);
										BufferedWriter bw = new BufferedWriter(new FileWriter(filePath));
										sb.append(
												"\"SKU\",\"Description\",\"ListPrice\",\"ItemCategory\",\"ItemSID\",\"VendorCode\",\"DeptCode\",\"Class\",\"SubClass\",\"DCS\",\"SubsidiaryNo\"");
										//sb.append("\n");
										
										List<POSMapping> posList = posMappingDao.findByType("'" + Constants.POS_MAPPING_TYPE_SKU + "'",
												users.getUserId());
										if (posList != null) {
											for (POSMapping obj : posList) {

												String column = obj.getCustomFieldName().trim().toLowerCase();
												if (column.startsWith("udf")) {
													udfSKUMap.put(obj.getCustomFieldName().toLowerCase(),obj.getDisplayLabel());
												} 
											
											}
										}
										for(int i=0;i<=14;i++) {
											if(udfSKUMap.get("udf"+i)!=null) {
												String udfCol = udfSKUMap.get("udf"+i);
												//sb.append(",\"UDF"+i+"\"");
												sb.append(",\""+udfCol+"\"");
												temp.append(",\"UDF"+i+"\"");
											}
										}
										sb.append("\n");
										bw.write(sb.toString());
										
										
										for (SkuFile entry : exportMap) {
											sb.setLength(0);
											sb.append("\"" + entry.getSku() + "\",");
											sb.append("\"" + entry.getDescription() + "\",");
											sb.append("\"" + entry.getListPrice() + "\",");
											sb.append("\"" + entry.getItemCategory() + "\",");
											sb.append("\"" + entry.getItemSid() + "\",");
											sb.append("\"" + entry.getVendorCode() + "\",");
											sb.append("\"" + entry.getDepartmentCode() + "\",");
											sb.append("\"" + entry.getClassCode() + "\",");
											sb.append("\"" + entry.getSubClassCode() + "\",");
											sb.append("\"" + entry.getDCS() + "\",");
											sb.append("\"" + entry.getSubsidiaryNumber() + "\",");
											if(temp.toString().contains("UDF1")) {
												sb.append("\"" + entry.getUdf1() + "\",");
											}
											if(temp.toString().contains("UDF2")) {
												sb.append("\"" + entry.getUdf2() + "\",");
											}
											if(temp.toString().contains("UDF3")) {
												sb.append("\"" + entry.getUdf3() + "\",");
											}
											if(temp.toString().contains("UDF4")) {
												sb.append("\"" + entry.getUdf4() + "\",");
											}
											if(temp.toString().contains("UDF5")) {
												sb.append("\"" + entry.getUdf5() + "\",");
											}
											if(temp.toString().contains("UDF6")) {
												sb.append("\"" + entry.getUdf6() + "\",");
											}
											if(temp.toString().contains("UDF7")) {
												sb.append("\"" + entry.getUdf7() + "\",");
											}
											if(temp.toString().contains("UDF8")) {
												sb.append("\"" + entry.getUdf8() + "\",");
											}
											if(temp.toString().contains("UDF9")) {
												sb.append("\"" + entry.getUdf9() + "\",");
											}
											if(temp.toString().contains("UDF10")) {
												sb.append("\"" + entry.getUdf10() + "\",");
											}
											if(temp.toString().contains("UDF11")) {
												sb.append("\"" + entry.getUdf11() + "\",");
											}
											if(temp.toString().contains("UDF12")) {
												sb.append("\"" + entry.getUdf12() + "\",");
											}
											if(temp.toString().contains("UDF13")) {
												sb.append("\"" + entry.getUdf13() + "\",");
											}
											if(temp.toString().contains("UDF14")) {
												sb.append("\"" + entry.getUdf14() + "\",");
											}
											sb.append("\n");
											bw.write(sb.toString());
										}
										bw.flush();
										bw.close();

										Filedownload.save(file, "text/csv");

									} catch (IOException e) {
										logger.error("Exception ::",e);
										
									}
									logger.debug("-- exit --");
					
					}
			
			public void onClick$addDiscountButtonId(){
				
				if(editCouponObj==null) {
					
				if (skuRadioId.isChecked() && receiptDiscountGenRowsId.getVisibleItemCount() > 0) {
					coupDiscGenDaoForDML.deleteAllSkuBy(users.getUserId(), null);
					Components.removeAllChildren(receiptDiscountGenRowsId);
				}else if(tpaRadioId.isChecked() && productDiscountGenRowsId.getVisibleItemCount() > 0) {
					coupDiscGenDaoForDML.deleteAllSkuBy(users.getUserId(), null);
					Components.removeAllChildren(productDiscountGenRowsId);
				}
				
				String coupName = couponNameTxtBxId.getValue().trim();

				if (coupName.length() == 0 || coupName.contains("_") || coupName.contains(" ")) {
					MessageUtil.setMessage("Please provide valid Discount code name.", "color:red", "TOP");
					return;
				} else if (validateCouponName4SpecialChars(coupName) == false) {
					MessageUtil.setMessage(
							"Please provide valid Discount code name which can include alphanumeric characters and special characters as * and -.",
							"color:red", "TOP");
					return;
				} else {
					// Check the Coupon Name already exists
					boolean isExistsCoup = couponsDao.checkCoupByName(coupName, orgId);
					if (isExistsCoup == true) {
						MessageUtil.setMessage("Discount code name already exists.", "color:red", "TOP");
						return;
					}
				}
				}
				
				List<Component> childVboxList = new ArrayList<Component>();
				
				//if(targetDivHasChildren(profileAttributeDivId))childVboxList.addAll(profileAttributeDivId.getChildren());
				if(targetDivHasChildren(discountAttributeDivId))childVboxList.addAll(discountAttributeDivId.getChildren());
			    //if(targetDivHasChildren(intAttributeDivId)){}
				
				if(childVboxList.size() == 0) {
					
					MessageUtil.setMessage("Please add Discount Attribute rules.", "color:red;");
					return;
					
				}
				String rule=Constants.STRING_NILL;
				String Disc="";
				if(skuRadioId.isChecked()) {
				rule = onClick$validateButtonId();
				/*if(rule.isEmpty()) {
					
					MessageUtil.setMessage("Please add at least one rule.", "color:red;");
					return;
					
				}*/
				if(rule!=null && !rule.isEmpty()) {
				//Adding the rules to rulebox
				int cnt = addedSKUSegmentLBId.getItemCount();
				List<Listitem> alreadyitems = null;
				boolean flag = false;
				Listitem li = null;
				Listcell cell = null;
				boolean duflag = false;
				String displayLabel;
				String columnName;
				String value;
				String[] ruleStr;
				String[] ruleStrAnd;
				String receiptTotal="";
				String minTotQnty="";
				String Qty="";
				String limitQty="";
				String ItemAttribute=""; 
				String ItemPrice="";   
				String EligibleItems="";
				String Actions="";
				if (productDiscountGenRowsId.getVisibleItemCount() == 0) ruleType=Constants.STRING_NILL;
						if (rule.contains(Constants.DELIMITER_DOUBLE_PIPE)) {

							//onCheck$oneCombinationId();
							
							String headerLabel="";
							//String ruleToInsert=rule;
							String ruleToInsert="";
							String valueToInsert="";

							ruleStr = rule.split("\\|\\|");
							int numOfItemAttributes=0;
							
							for(String eachDraggedRule:ruleStr) {
								if(eachDraggedRule.startsWith(MinReceiptTotal)){
									receiptTotal=eachDraggedRule.split("\\|")[3].split("<OR>")[0];
								}
								else if(eachDraggedRule.startsWith(DiscountQtyPerItem)){
									Qty	=eachDraggedRule.split("\\|")[3]!=null && !eachDraggedRule.split("\\|")[3].isEmpty() &&
											!eachDraggedRule.split("\\|")[3].equals("null")?
											eachDraggedRule.split("\\|")[3]:"";
									Qty = Qty!=null && !Qty.isEmpty()?String.valueOf(getActualNumber(Double.parseDouble(Qty))):"";
									limitQty=eachDraggedRule.split("\\|")[2].split(":")[1];
								}
								else if(eachDraggedRule.startsWith("Item Price")){
									ItemPrice=eachDraggedRule.split("\\|")[3].split("<OR>")[0];
								}
								/*else if(eachDraggedRule.startsWith("Eligible Items")){
									EligibleItems=eachDraggedRule;
								}*/
								else if(eachDraggedRule.startsWith("Discount Type")){
									String disc = eachDraggedRule.split("\\|")[2];
									String[] discType = disc.split(":");
									Disc = discType[1];
								}
								else {
									if(!ruleToInsert.contains(eachDraggedRule.split("\\|")[0]))numOfItemAttributes+=1;
									if(ruleToInsert.length()>0) ruleToInsert+=",,,";
									if(udfRuleSKUMap.get(eachDraggedRule.split("\\|")[0])!=null){
										ruleToInsert+=udfRuleSKUMap.get(eachDraggedRule.split("\\|")[0])+"="+eachDraggedRule.split("\\|")[3];
									}else {
										ruleToInsert+=eachDraggedRule.split("\\|")[0]+"="+eachDraggedRule.split("\\|")[3];
									}
								}
							}
							
							
							ruleToInsert=ruleToInsert.replaceAll(SYMPIPELABEL,"|");
							logger.info("ruletoinsert2==+ "+ruleToInsert);
							//if(rule.contains(Constants.DELIMITER_DOUBLE_PIPE)) rule=rule.replaceAll("\\|\\|", " AND ");
							
							//flag = true;
							
							/*if (!flag) {
								MessageUtil.setMessage("Please select at least one attribute code for creating discount rule.",
										"color:blue", "TOP");
								return;
							}*/
							if (numOfItemAttributes==0) {
								MessageUtil.setMessage("Please select at least one item attribute for creating discount rule.",
										"color:red", "TOP");
								return;
							}
							if(editCombinedRuleObj==null) {
							if(ruleType.equals(combined) && numOfItemAttributes>=1 && productDiscountGenRowsId.getChildren().size()>0) {
								MessageUtil.setMessage("Multiple combination rules are not allowed.",
										"color:blue", "TOP");
								return;
							}
							}
							/*if(ruleType.equals(multipleSingle) && numOfItemAttributes>0 && productDiscountGenRowsId.getChildren().size()>0) {
								MessageUtil.setMessage("Single rule(s) already exist. Delete the existing rule to create a combination rule",
										"color:blue", "TOP");
								return;
							}*/
							if(ruleType.equals(multipleSingle)&&numOfItemAttributes>1) {
								MessageUtil.setMessage("Only one Item Attribute can be added per single rule",
										"color:blue", "TOP");
								return;
							}
							if(ruleToInsert.contains("Vendor Code")){
								ruleToInsert= ruleToInsert.replaceAll("Vendor Code", "Vendor");
							}
							if(ruleToInsert.contains("Dept. Code")){
								ruleToInsert=ruleToInsert.replaceAll("Dept. Code", "Department");
							}
							if(ruleToInsert.contains("Sub-Class")){
								ruleToInsert= ruleToInsert.replaceAll("Sub-Class", "Subclass");
							}
							
							if(ruleType.isEmpty() && numOfItemAttributes>1) ruleType = combined;
							if(ruleType.isEmpty() && numOfItemAttributes==1) ruleType=multipleSingle;
							logger.info("key =="+ruleToInsert);
							ruleStr = ruleToInsert.split(",,,");
							if(ruleType.equals(multipleSingle)) {
							for (String eachRule : ruleStr) {
						
										li = new Listitem();
										li.addEventListener("onClick", this);
										
									
										
										cell = new Listcell();
										cell.setParent(li);
										cell = new Listcell(receiptTotal);
										cell.setAttribute("value", receiptTotal);
										cell.setValue(MinReceiptTotal);
										
										
										
										
										cell = new Listcell();
										cell.setParent(li);
										cell = new Listcell(limitQty+"="+Qty);
										cell.setAttribute("value", limitQty+"="+Qty);
										cell.setValue(DiscountQtyPerItem);
										
										cell = new Listcell();
										cell.setParent(li);
										cell = new Listcell(ItemPrice);
										cell.setAttribute("value", ItemPrice);
										cell.setValue("Item Price");
										
										cell = new Listcell();
										cell.setParent(li);
										cell = new Listcell(eachRule.contains("udf") && udfDisplaySKUMap.get(eachRule.split("=")[0].toUpperCase())!=null?
												udfDisplaySKUMap.get(eachRule.split("=")[0].toUpperCase()+"="+eachRule.split("=")[1]):eachRule);
										cell.setAttribute("value", eachRule.split("=")[1]);
										cell.setValue("key::" +(udfDisplaySKUMap.get(eachRule.split("=")[0].toUpperCase())!=null?udfDisplaySKUMap.get(eachRule.split("=")[0].toUpperCase()):eachRule.split("=")[0]));
										
										
										cell.setParent(li);
										cell = new Listcell();
										Image img;
										img = new Image("img/delt_icn.png");
										img.setTooltiptext("Delete");
										img.setStyle("margin-right:3px;cursor:pointer;");
										img.setAttribute("delete", "DELETE_SELECTED");
										img.addEventListener("onClick", this);

										img.setParent(cell);
										cell.setParent(li);
										li.setSelected(true);
										li.setParent(addedSKUSegmentLBId);
										li.setAttribute("ruleType",ruleType);
							}
						}else if(ruleType.equals(combined)) {
							
							String[] combined = ruleToInsert.split(",,,");
							for(String comb:combined) {
							    String[] num = comb.split("=");
							    if(!(num[0].equalsIgnoreCase("Description")||num[0].contains("udf")) && num[1].contains(",")) {
										MessageUtil.setMessage("Multiple values cannot be given for an Item Attribute in a combination rule",
												"color:blue", "TOP");
										return;
							    }
							}
							
							li = new Listitem();
							li.addEventListener("onClick", this);
							
					
							
							cell = new Listcell();
							cell.setParent(li);
							cell = new Listcell(receiptTotal);
							cell.setAttribute("value", receiptTotal);
							cell.setValue(MinReceiptTotal);
							
							
							
							cell = new Listcell();
							cell.setParent(li);
							cell = new Listcell(limitQty+"="+Qty);
							cell.setAttribute("value", limitQty+"="+Qty);
							cell.setValue(DiscountQtyPerItem);
							
							cell = new Listcell();
							cell.setParent(li);
							cell = new Listcell(ItemPrice);
							cell.setAttribute("value", ItemPrice);
							cell.setValue("Item Price");
							
							cell = new Listcell();
							cell.setParent(li);
							cell = new Listcell(ruleToInsert.replace(",,,"," AND "));
							cell.setAttribute("value", ruleToInsert);
							cell.setValue("key::" +ruleToInsert);
							
							
							cell.setParent(li);
							cell = new Listcell();
							Image img;
							img = new Image("img/delt_icn.png");
							img.setTooltiptext("Delete");
							img.setStyle("margin-right:3px;cursor:pointer;");
							img.setAttribute("delete", "DELETE_SELECTED");
							img.addEventListener("onClick", this);

							img.setParent(cell);
							cell.setParent(li);
							li.setSelected(true);
							li.setParent(addedSKUSegmentLBId);
							li.setAttribute("ruleType",ruleType);
						}
				}else {
					if(rule.contains(Constants.DELIMITER_PIPE)){

					String[] ORrules=rule.split("<OR>");
					if(ORrules.length>=2) {
						for(String orRule:ORrules) {
							displayLabel=orRule.split("\\|")[0];
							columnName=orRule.split("\\|")[1].substring(4);
							value = orRule.split("\\|")[3].replace("<OR>", "");
							if(value.contains(SYMPIPELABEL)) value = value.replace(SYMPIPELABEL,"|");
							flag = true;
							duflag = false;
							if (addedSKUSegmentLBId.getItemCount() > 0) {
													alreadyitems = addedSKUSegmentLBId.getItems();
													for (Listitem itm : alreadyitems) {
														Listcell headcl = (Listcell) itm.getChildren().get(2);
														Listcell itemcl = (Listcell) itm.getChildren().get(1);
							String[] items=itemcl.getLabel().split("=");
														String headLbl=items[0];
														String valueLbl=items[1];
												
							if (headLbl.equals(displayLabel) && valueLbl.equals(value)) {
								logger.info("duflag==="+duflag);
												duflag = true;
												break;
												}
								}
										}
										/*if (duflag)
											continue;*/

										li = new Listitem();
										li.addEventListener("onClick", this);
										cell = new Listcell();
										cell.setParent(li);
										
										logger.info("value inserted in the listcell======"+displayLabel+"="+value);
										cell = new Listcell(displayLabel+"="+value);
										cell.setAttribute("value", value);
										logger.info("setting the attribute of value=="+value);
										if(displayLabel==null||displayLabel.isEmpty()){
											cell.setValue("key::" + "SKU");

										}else{
											logger.info("header.getLabel()==="+displayLabel);
											logger.info("header.getValue().toString()======"+value);
											logger.info("key and value======"+headerMap.get(value));
											//cell.setValue("key::" + headerMap.get(value));
											cell.setValue("key::" + displayLabel);
										}
										
										cell.setParent(li);
										cell = new Listcell();
										Image img;
										img = new Image("img/delt_icn.png");
										img.setTooltiptext("Delete");
										img.setStyle("margin-right:3px;cursor:pointer;");
										img.setAttribute("delete", "DELETE_SELECTED");
										img.addEventListener("onClick", this);

										img.setParent(cell);
										cell.setParent(li);
										li.setSelected(true);
										li.setParent(addedSKUSegmentLBId);
										
										//ruleType="MultipleSingle";
						}
					}else {
						displayLabel=rule.split("\\|")[0];
						columnName=rule.split("\\|")[1];
						value = rule.split("\\|")[3].replace("<OR>", "");
						flag = true;
						duflag = false;
						if (addedSKUSegmentLBId.getItemCount() > 0) {
												alreadyitems = addedSKUSegmentLBId.getItems();
												for (Listitem itm : alreadyitems) {
													Listcell headcl = (Listcell) itm.getChildren().get(2);
													Listcell itemcl = (Listcell) itm.getChildren().get(1);
						String[] items=itemcl.getLabel().split("=");
													String headLbl=items[0];
													String valueLbl=items[1];
											
						if (headLbl.equals(displayLabel) && valueLbl.equals(value)) {
							logger.info("duflag==="+duflag);
											duflag = true;
											break;
											}
							}
									}
									/*if (duflag)
										continue;*/

									li = new Listitem();
									li.addEventListener("onClick", this);
									cell = new Listcell();
									cell.setParent(li);
									
									logger.info("value inserted in the listcell======"+displayLabel+"="+value);
									cell = new Listcell(displayLabel+"="+value);
									cell.setAttribute("value", value);
									logger.info("setting the attribute of value=="+value);
									if(displayLabel==null||displayLabel.isEmpty()){
										cell.setValue("key::" + "SKU");

									}else{
										logger.info("header.getLabel()==="+displayLabel);
										logger.info("header.getValue().toString()======"+value);
										logger.info("key and value======"+headerMap.get(value));
										//cell.setValue("key::" + headerMap.get(value));
										cell.setValue("key::" + displayLabel);
									}
									
									cell.setParent(li);
									cell = new Listcell();
									Image img;
									img = new Image("img/delt_icn.png");
									img.setTooltiptext("Delete");
									img.setStyle("margin-right:3px;cursor:pointer;");
									img.setAttribute("delete", "DELETE_SELECTED");
									img.addEventListener("onClick", this);

									img.setParent(cell);
									cell.setParent(li);
									li.setSelected(true);
									li.setParent(addedSKUSegmentLBId);
									
								//	ruleType=multipleSingle;
								}
				}
				}
						logger.debug(ruleType);
						
						boolean success = onClick$applyDiscBtnId();
						if(success) {
						List<Component> existingRules = new ArrayList<Component>();
						existingRules = purIntAttributeDivId.getChildren();
						for(Component c:existingRules){
							deleteExistedRulesDiv(c);
						}
						SetItemsToBeDragged(2, purchaseAttrLbId);
						dropflag=true;
						editedRuleRowObj=null;
						editCombinedRuleObj=null;

						existingRules = discountAttributeDivId.getChildren();
						for(Component c:existingRules){
							deleteExistedRulesDiv(c);
						}
						existingRules = EligibilityAttributeDivId.getChildren();
						for(Component c:existingRules){
							deleteExistedRulesDiv(c);
						}
					
						dropflag=true;
						editedRuleRowObj=null;
						editCombinedRuleObj=null; 
						String discountTypeRuleStr="";
						String eligibleItemsRuleStr="";
						discountTypeRuleStr="Discount Type|discount_type|number:"+discountTypeStr+"| ";
						eligibleItemsRuleStr=(discountTypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("loyaltyRewards")
								||discountTypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("purchases")?
										"Eligible Items|no_of_eligible_items|String:highest priced items|null":"Eligible Items|no_of_eligible_items|String:all items|null");
						
						parseRules(discountTypeRuleStr);
						parseRules(eligibleItemsRuleStr);
					
						}else {
							int count = addedSKUSegmentLBId.getItemCount();
							for (; count > 0; count--) {
								addedSKUSegmentLBId.removeItemAt(count - 1);
							}
						}
				}
				}else {
					boolean success = onClick$applyDiscBtnId();
					if(success) {
					List<Component> existingRules = new ArrayList<Component>();
					existingRules = purIntAttributeDivId.getChildren();
					for(Component c:existingRules){
						deleteExistedRulesDiv(c);
					}
			
					dropflag=true;
					editedRuleRowObj=null;
					editCombinedRuleObj=null;
					existingRules = discountAttributeDivId.getChildren();
					for(Component c:existingRules){
						deleteExistedRulesDiv(c);
					} 
					existingRules = EligibilityAttributeDivId.getChildren();
					for(Component c:existingRules){
						deleteExistedRulesDiv(c);
					} 		
					String discountTypeRuleStr="";
					discountTypeRuleStr="Discount Type|discount_type|number:"+discountTypeStr+"| ";
					parseRules(discountTypeRuleStr);
					}else {
						int cnt = addedSKUSegmentLBId.getItemCount();
						for (; cnt > 0; cnt--) {
							addedSKUSegmentLBId.removeItemAt(cnt - 1);
						}
					}
				}
				itemCountLblId.setValue("");
			}
			public void deleteExistedRulesDiv(Component parentComponent) {
				List<Component> list = parentComponent.getChildren();
				List<Component> deleteList = new ArrayList<Component>();
				for (Component component : list) {
					
					if(component instanceof Div) {
						
						Div deleteDiv = (Div)component;
						if( deleteDiv.getSclass() != null && deleteDiv.getSclass().startsWith("drop_") ) continue;
						else deleteList.add(deleteDiv);//deleteDiv.setParent(null);//profileAttributeDivId.removeChild(deleteDiv);
						
					}
					else if(component instanceof Label) deleteList.add(component);// component.setParent(null);//profileAttributeDivId.removeChild(component);
					
				}
				
				if(!deleteList.isEmpty()) {
					
					for (Component component : deleteList) {
						
						parentComponent.removeChild(component);
						
					}
					
					
				}
			}//deleteExistedRulesDiv
			private void createDivForEdit1(String segRule) {
				logger.debug("Reward  Rule :" + segRule);
				if (segRule == null) {
					return;
				}
				String[] rowsArr = segRule.split("\\|\\|");
				String ruleStr = "";
				for (int i = 0; i < rowsArr.length; i++) {
					String[] orRuleTokensArr = null;
					String[] tokenArr = null;
					String itemCode = "";
					ruleStr = rowsArr[i];
				//	if (ruleStr.contains("<OR>")) {
						orRuleTokensArr = ruleStr.split("<OR>");
						Div tempDiv = new Div();
						tempDiv.setSclass("");
						tempDiv.setParent(EligibilityAttributeDivId);

						Label AndLabel = new Label("AND");
						AndLabel.setStyle("display:inline-block; width:20px; padding:0 5px;margin:0 10px;font-weight:bold;");
						AndLabel.setParent(tempDiv);

						Div vdiv = new Div();// this is the outer component
						vdiv.setDroppable("outerDivDropable");
						vdiv.setParent(tempDiv);
						vdiv.setSclass("segment_parent_div");
						vdiv.addEventListener("onDrop", myDropListener);
						updateAndLabel(EligibilityAttributeDivId);
						Div obj = null;
						boolean isORDivCreated=true;
						for (int j = 0; j < orRuleTokensArr.length; j++) {
							tokenArr = orRuleTokensArr[j].split(";=;");
							itemCode = tokenArr[0];
							CouponsEnum couponsenum = CouponsEnum.getEnumByItem(itemCode);
							for (Listitem item : EligibilityAttrLbId.getItems()) {
								if (itemCode.equals((String) item.getAttribute("item"))) {
									obj = createNewRuleDiv1(item);
									obj.setParent(vdiv);
									updateORLabel(vdiv);
									break;
								}
							}
							if (flag && itemCode.equals("[#ItemFactor#]")) {
								Listitem item = new Listitem(couponsenum.getDispLabel(), couponsenum);
								item.setAttribute("item", couponsenum.getItem());
								tempDiv.setAttribute("Type", couponsenum.name());
								obj = createNewRuleDiv1(item);
								obj.setParent(vdiv);
								updateORLabel(vdiv);
								flag = false;
							}
							// set all values
							List ruleDivChildren = obj.getChildren();
							Label fielslbl = (Label) ruleDivChildren.get(1); //
							itemCode = (String) fielslbl.getAttribute("item");
							Listbox ruleLb = (Listbox) ruleDivChildren.get(2);
							Listbox operatorLb = (Listbox) ruleDivChildren.get(5);
							Listbox numberLb = (Listbox) ruleDivChildren.get(7);
							Textbox txtbox = (Textbox) ruleDivChildren.get(8);
							Datebox dateBox1 = (Datebox) ruleDivChildren.get(11);
							Datebox dateBox2 = (Datebox) ruleDivChildren.get(12);
							Label templbl = (Label) ruleDivChildren.get(6);
							templbl.setValue("");
							String val1 = "", val2 = "", val3 = "";
							String programId = "";
							String jsonAtt = "";
							boolean dflag = true;
							if (!tokenArr[2].contains("<programId>"))
								programId = tokenArr[2];
							jsonAtt = tokenArr[1].split(":")[1];
							val1 = tokenArr[3];
							val2 = tokenArr[4];
							val3 = tokenArr[5];
							List<Listitem> list = ruleLb.getItems();
							if(isORDivCreated) {
								vdiv.appendChild(createDropORDiv("Drag Attribute(s) here to create OR combination rule."));
								isORDivCreated=false;
							}
					
								if(couponsenum.name().equals(CouponsEnum.PURCHASE_IN_CARD_SET.name()) || couponsenum.name().equals(CouponsEnum.PURCHASE_IN_TIER.name())) {
									for (Listitem itm : list) {
										if (itm.getValue() != null && itm.getValue().toString().equals(programId))
											ruleLb.setSelectedItem(itm);
									}
									//Events.sendEvent("onSelect", ruleLb, null);
									creteOperatorOptionsForRule1(itemCode.equals("[#PurchaseTier#]") ? true : false, operatorLb,
											Long.parseLong(programId));
									list = operatorLb.getItems();
									for (Listitem itm : list) {
										if (itm.getValue() != null && itm.getValue().toString().equals(val1))
											operatorLb.setSelectedItem(itm);
									}
								}
					//		}

							if (dateBox1.isVisible() && val2.contains("-")) {
								try {
									dateBox1.setValue(dateFormat.parse(val2));
								} catch (Exception e) {
									logger.error("Exception", e);
								}
							}
							if (dflag && operatorLb.isVisible()) {
								list = operatorLb.getItems();
								for (Listitem itm : list) {
									if (itm.getValue() != null && itm.getValue().toString().equals(val2))
										operatorLb.setSelectedItem(itm);
								}
							}

							if (numberLb.isVisible()) {
								list = numberLb.getItems();
								for (Listitem itm : list) {
									if (itm.getValue() != null && itm.getValue().toString().equals(val3))
										numberLb.setSelectedItem(itm);
								}
								numberLb.setVisible(true);
								templbl.setValue(" To ");
							}
							if (dateBox2.isVisible() && val3.contains("-")) {
								try {
									dateBox2.setValue(dateFormat.parse(val3));
								} catch (Exception e) {
									logger.error("Exception", e);
								}
							}

						}
					}
				}
			//}
			private void createDivForEdit(String segRule) {
				logger.debug("Reward  Rule :" + segRule);
				if (segRule == null) {
					return;
				}
				String[] rowsArr = segRule.split("\\|\\|");
				String ruleStr = "";
				for (int i = 0; i < rowsArr.length; i++) {
					String[] orRuleTokensArr = null;
					String[] tokenArr = null;
					String itemCode = "";
					ruleStr = rowsArr[i];
					Div tempDiv = new Div();
					tempDiv.setSclass("");
					addSegmentRules(rowsArr[i], tempDiv); 
				}	
			}
		public void addSegmentRules(String ruleStr, Div anotheRuleDivSetDiv) {

			
				try {
					logger.debug("got the segment Rule...."+ruleStr);
					
					String[] orRuleTokensArr = null;
					//String orRuleToken = null;
					String[] tokenArr = null;
					String columnName = null;
					String dispLabel = null;
					String type = null;
					String constraint = null;
					String rule=null;
					String rule2=null;
					String rule3=null;
				
					
					orRuleTokensArr = ruleStr.split("<OR>");
				
						Div vdiv=null;
						vdiv = new Div();//this is the outer component
						vdiv.setParent(anotheRuleDivSetDiv);
						vdiv.setSclass("segment_parent_div");
						vdiv.addEventListener("onDrop", myDropListener);
						
						for (String orRuleToken : orRuleTokensArr) {
							String value1 = null;
							String value2 = null;
							//String campIdVal = null;   
							
							String itemLabel = null;
							List<CouponsEnum> children = null;
							logger.debug("'OR' Rule token is :: "+orRuleToken);
							
							tokenArr = orRuleToken.split("\\|");  
							dispLabel = tokenArr[0];   
				
							columnName = tokenArr[1];    //ALL::null
						
							
							type = tokenArr[2].split(":")[0];
							constraint = tokenArr[2].split(":")[1];
							value1 = tokenArr[3];
							if(value1.contains(SYMPIPELABEL))value1 = value1.replace(SYMPIPELABEL,"|");
							if(tokenArr.length > 4) {
								
								value2 = tokenArr[4];
								
							}//values 
							
							CouponsEnum retEnum = CouponsEnum.getEnumByItem(dispLabel);
							if(retEnum != null) {
								 itemLabel = retEnum.getDispLabel();
								
								children = retEnum.getChidrenByParentEnum(retEnum);
								logger.debug("constraint ::"+constraint+" children ::"+children.size());
								
							}
							
							CouponsEnum seg = CouponsEnum.getEnumByItem(dispLabel);
							//itemLabel = getMostParentDisplayLabel(retEnum);
							
							
							if(seg != null && (seg.getCategoryCode() == 2 || seg.getCategoryCode() == 4) ||
									(seg.getParentEnum()!=null && seg.getParentEnum().getCategoryCode()==2)){
								
								if(anotheRuleDivSetDiv.getParent() == null){
									vdiv.setDroppable("purIntSegment");
									anotheRuleDivSetDiv.setParent(purIntAttributeDivId);
									anotheRuleDivSetDiv.setAttribute("Type", seg.name());
									updateAndLabel(purIntAttributeDivId);
									//vdiv.appendChild(createDropORDiv("Drag Purchase Attribute here to create OR combination rule."));
									
								}
								
								for (Listitem item : purchaseAttrLbId.getItems()) {
									if(itemLabel == null){
										logger.debug("columnName ::"+columnName+" item-column :: "+(String)item.getAttribute("DBcolumnName"));
										if(!columnName.equals((String)item.getAttribute("DBcolumnName"))){
											
											continue;
										}
										
										itemLabel = item.getLabel();
										
									}
									if((seg.getParentEnum()!=null && seg.getParentEnum().getCategoryCode()==2)||seg.getCategoryCode()==4) itemLabel="Item Attribute";
									logger.info("item.getLabel() "+item.getLabel());
									if(item.getLabel().equalsIgnoreCase(itemLabel)) {
										Div obj=null;
										
										if(seg.getCategoryCode()==4) {
											Listitem itemnew = new Listitem(seg.getDispLabel(),seg);
											itemnew.setAttribute("item", seg.getItem());
											vdiv.setAttribute("Type", seg.name());
											obj = createNewRuleDiv(itemnew);
											obj.setParent(vdiv);
											updateORLabel(vdiv);
										}else {
										obj  = createNewRuleDiv(item);
										obj.setParent(vdiv);
										updateORLabel(vdiv);
										}
										List ruleDivChildren =  obj.getChildren();
										Listbox ruleLb = (Listbox)ruleDivChildren.get(1);
										//Listbox ltyLb = (Listbox)ruleDivChildren.get(3);
										Listbox numberLb = (Listbox)ruleDivChildren.get(2);
										Listbox operatorLb = (Listbox)ruleDivChildren.get(4);
										/*Datebox dateBox1 = (Datebox)ruleDivChildren.get(8);
										Datebox dateBox2 = (Datebox)ruleDivChildren.get(9);
										Decimalbox numberbox1 = (Decimalbox)ruleDivChildren.get(6);
										Decimalbox numberbox2 = (Decimalbox)ruleDivChildren.get(7);
										Textbox txtbox = (Textbox)ruleDivChildren.get(5);*/
										Datebox dateBox1 = (Datebox)ruleDivChildren.get(8);
										Datebox dateBox2 = (Datebox)ruleDivChildren.get(9);
										Decimalbox numberbox1 = (Decimalbox)ruleDivChildren.get(6);
										Decimalbox numberbox2 = (Decimalbox)ruleDivChildren.get(7);
										Textbox txtbox = (Textbox)ruleDivChildren.get(5);
									/*	Combobox cb1 = (Combobox)ruleDivChildren.get(12);
										Combobox cb2 = (Combobox)ruleDivChildren.get(13);
										Listbox formLb = (Listbox)ruleDivChildren.get(11);*/
										/*Combobox cb1 = (Combobox)ruleDivChildren.get(11);
										Combobox cb2 = (Combobox)ruleDivChildren.get(12);
										Listbox formLb = (Listbox)ruleDivChildren.get(10);*/
										Combobox cb1 = (Combobox)ruleDivChildren.get(11);
										Combobox cb2 = (Combobox)ruleDivChildren.get(12);
										Listbox formLb = (Listbox)ruleDivChildren.get(10);
										
										//Listbox campLb = (Listbox)ruleDivChildren.get(10);
										// Generate Drop Div
										
										//make selection of the options lb
										//Date(c.created_date)|date:isToday|Today
										//make selection of the options lb
										//Date(c.created_date)|date:isToday|Today
										boolean isSelected = false;
										boolean isLtySelected = false;
										logger.debug("constraint ::"+constraint);
										if(constraint != null ) {
											boolean isParentSelected = false;
											//for loyalty rule display
											for (Listitem ruleItem : ruleLb.getItems()) {
											CouponsEnum couponsEnum = ruleItem.getValue();
											logger.debug("ruleItem ::"+ruleItem.getLabel()+" couponsEnum.getColumnValue() "+couponsEnum.getColumnValue());
											
												if(couponsEnum.getColumnName() != null && !couponsEnum.getColumnName().isEmpty() ){
													if(couponsEnum.getColumnName().equals(columnName) ) {
														ruleItem.setSelected(true);
														Events.sendEvent("onSelect", ruleLb, null);
														isParentSelected = true;
													}
													else continue;
													
												}
												List<CouponsEnum> retList = couponsEnum.getChidrenByParentEnum(couponsEnum);
												if( ( retList == null || retList.isEmpty() ) && couponsEnum.getToken() != null 
														&& !couponsEnum.getToken().isEmpty() && constraint.equals(couponsEnum.getToken())) {
													
													if(!isParentSelected){
														ruleItem.setSelected(true);
														Events.sendEvent("onSelect", ruleLb, null);
													}
													/*if(type.equalsIgnoreCase("Date")) {
														
														populateValuesForOperatorForTypeDate(dateBox1, dateBox2, 
																numberLb, value1, value2, numberbox1, numberbox2, null);
														
													}else*/ if(type.equalsIgnoreCase("String")) {
														
														populateValuesForOperatorForTypeString(txtbox, cb1, cb2, formLb, value1, value2);
														
													}else if(type.equalsIgnoreCase("number")) {
														populateValuesForOperatorForTypeNumber(numberbox1, numberbox2, 
																txtbox, cb1, cb2, value1, value2);
														
													}
													break;
												}
												else if(retList != null && !retList.isEmpty()) {
													logger.debug("for ::"+couponsEnum.name()+" ITEM ::"+ruleItem.getLabel());
													for (CouponsEnum child : retList) {
														logger.debug("child is ::"+child.name());
														if(child.getToken() != null && constraint.equals(child.getToken())) {
															logger.debug("child.getToken() ::"+child.getToken());
															if(!isParentSelected){
																ruleItem.setSelected(true);
																Events.sendEvent("onSelect", ruleLb, null);
															}
															for (Listitem operatorItem : operatorLb.getItems()) {
																CouponsEnum operatorEnum = operatorItem.getValue();
																if(constraint.equals(operatorEnum.getToken())) {
																	operatorItem.setSelected(true);
																	Events.sendEvent("onSelect", operatorLb, null);
																	
																	/*if(type.equalsIgnoreCase("Date")) {
																		
																		populateValuesForOperatorForTypeDate(dateBox1, dateBox2, 
																				numberLb, value1, value2, numberbox1, numberbox2, operatorEnum);
																		
																	}else*/ if(type.equalsIgnoreCase("String")) {
																		
																		populateValuesForOperatorForTypeString(txtbox, cb1, cb2, formLb, value1, value2);
																		
																	}else if(type.equalsIgnoreCase("number")) {
																		populateValuesForOperatorForTypeNumber(numberbox1, numberbox2,
																				txtbox, cb1, cb2, value1, value2);
																	}
																	
																	/*if(numberLb.isVisible()) {
																		
																		int value = Integer.parseInt(value1)/Integer.parseInt(operatorEnum.getType2());
																		for (Listitem numberItem : numberLb.getItems()) {
																			
																			if(numberItem.getLabel().equals(value+Constants.STRING_NILL)) {
																				
																				numberItem.setSelected(true);
																				break;
																			}
																			
																		}//for
																			
																			
																	}//if numberLb visible
		*/															break;
																}
																
															}
															isSelected = true;
															break;
															
															
														}
														
														
													}//for
													
													
												}//if
												if(isSelected) break;
												
												
										
											if(isSelected) break;
										
											
											}
																				
										}//if
										
										
										
										
										
										
										break;
										
									}
									
									
								}
								//DropProfileComponent(true);
								//onDrop$profileAttributeDivId(event);
								
							}
							
							else if(seg.getCategoryCode()==3 || seg.getCategoryCode()==9 || seg.getCategoryCode()==8) {

								if(anotheRuleDivSetDiv.getParent() == null){
									vdiv.setDroppable("DisSegment");
									anotheRuleDivSetDiv.setParent(discountAttributeDivId);
									updateAndLabel(discountAttributeDivId);
									//vdiv.appendChild(createDropORDiv("Drag Purchase Attribute here to create OR combination rule."));
									
								}
								
								for (Listitem item : discountAttrLbId.getItems()) {
									if(itemLabel == null){
										logger.debug("columnName ::"+columnName+" item-column :: "+(String)item.getAttribute("DBcolumnName"));
										if(!columnName.equals((String)item.getAttribute("DBcolumnName"))){
											
											continue;
										}
										
										itemLabel = item.getLabel();
										
									}
									logger.info("item.getLabel() "+item.getLabel());
									if(item.getLabel().equalsIgnoreCase(itemLabel)) {
										Div obj=null;
										
										obj  = createNewRuleDiv(item);
										obj.setParent(vdiv);
										updateORLabel(vdiv);

										List ruleDivChildren =  obj.getChildren();
										Listbox ruleLb = (Listbox)ruleDivChildren.get(1);
										//Listbox ltyLb = (Listbox)ruleDivChildren.get(3);
										Listbox numberLb = (Listbox)ruleDivChildren.get(2);
										Listbox operatorLb = (Listbox)ruleDivChildren.get(4);
										/*Datebox dateBox1 = (Datebox)ruleDivChildren.get(8);
										Datebox dateBox2 = (Datebox)ruleDivChildren.get(9);
										Decimalbox numberbox1 = (Decimalbox)ruleDivChildren.get(6);
										Decimalbox numberbox2 = (Decimalbox)ruleDivChildren.get(7);
										Textbox txtbox = (Textbox)ruleDivChildren.get(5);*/
										Datebox dateBox1 = (Datebox)ruleDivChildren.get(8);
										Datebox dateBox2 = (Datebox)ruleDivChildren.get(9);
										Decimalbox numberbox1 = (Decimalbox)ruleDivChildren.get(6);
										Decimalbox numberbox2 = (Decimalbox)ruleDivChildren.get(7);
										Textbox txtbox = (Textbox)ruleDivChildren.get(5);
									/*	Combobox cb1 = (Combobox)ruleDivChildren.get(12);
										Combobox cb2 = (Combobox)ruleDivChildren.get(13);
										Listbox formLb = (Listbox)ruleDivChildren.get(11);*/
										/*Combobox cb1 = (Combobox)ruleDivChildren.get(11);
										Combobox cb2 = (Combobox)ruleDivChildren.get(12);
										Listbox formLb = (Listbox)ruleDivChildren.get(10);*/
										Combobox cb1 = (Combobox)ruleDivChildren.get(11);
										Combobox cb2 = (Combobox)ruleDivChildren.get(12);
										Listbox formLb = (Listbox)ruleDivChildren.get(10);
										
										//Listbox campLb = (Listbox)ruleDivChildren.get(10);
										// Generate Drop Div
										
										//make selection of the options lb
										//Date(c.created_date)|date:isToday|Today
										//make selection of the options lb
										//Date(c.created_date)|date:isToday|Today
										boolean isSelected = false;
										boolean isLtySelected = false;
										logger.debug("constraint ::"+constraint);
										if(constraint != null ) {
											boolean isParentSelected = false;
											//for loyalty rule display
											for (Listitem ruleItem : ruleLb.getItems()) {
											CouponsEnum couponsEnum = ruleItem.getValue();
											logger.debug("ruleItem ::"+ruleItem.getLabel()+" couponsEnum.getColumnValue() "+couponsEnum.getColumnValue());
											
												if(couponsEnum.getColumnName() != null && !couponsEnum.getColumnName().isEmpty() ){
													if(couponsEnum.getColumnName().equals(columnName) ) {
														ruleItem.setSelected(true);
														Events.sendEvent("onSelect", ruleLb, null);
														isParentSelected = true;
													}
													else continue;
													
												}
												List<CouponsEnum> retList = couponsEnum.getChidrenByParentEnum(couponsEnum);
												if( ( retList == null || retList.isEmpty() ) && couponsEnum.getToken() != null 
														&& !couponsEnum.getToken().isEmpty() && constraint.equals(couponsEnum.getToken())) {
													
													if(!isParentSelected){
														ruleItem.setSelected(true);
														Events.sendEvent("onSelect", ruleLb, null);
													}
													/*if(type.equalsIgnoreCase("Date")) {
														
														populateValuesForOperatorForTypeDate(dateBox1, dateBox2, 
																numberLb, value1, value2, numberbox1, numberbox2, null);
														
													}else*/ if(type.equalsIgnoreCase("String")) {
														
														populateValuesForOperatorForTypeString(txtbox, cb1, cb2, formLb, value1, value2);
														
													}else if(type.equalsIgnoreCase("number")) {
														if(value1 == null || value1.equals("null")) {
															numberbox1.setVisible(false);
															cb1.setVisible(false);
															txtbox.setVisible(false);
														}
														if(value1 == null || value1.equals("null")) {
															numberbox2.setVisible(false);
															cb2.setVisible(false);
															txtbox.setVisible(false);
														}
														populateValuesForOperatorForTypeNumber(numberbox1, numberbox2, 
																txtbox, cb1, cb2, value1, value2);
														
													}
													break;
												}
												else if(retList != null && !retList.isEmpty()) {
													logger.debug("for ::"+couponsEnum.name()+" ITEM ::"+ruleItem.getLabel());
													for (CouponsEnum child : retList) {
														logger.debug("child is ::"+child.name());
														if(child.getToken() != null && constraint.equals(child.getToken())) {
															logger.debug("child.getToken() ::"+child.getToken());
															if(!isParentSelected){
																ruleItem.setSelected(true);
																Events.sendEvent("onSelect", ruleLb, null);
															}
															for (Listitem operatorItem : operatorLb.getItems()) {
																CouponsEnum operatorEnum = operatorItem.getValue();
																if(constraint.equals(operatorEnum.getToken())) {
																	operatorItem.setSelected(true);
																	Events.sendEvent("onSelect", operatorLb, null);
																	
																	/*if(type.equalsIgnoreCase("Date")) {
																		
																		populateValuesForOperatorForTypeDate(dateBox1, dateBox2, 
																				numberLb, value1, value2, numberbox1, numberbox2, operatorEnum);
																		
																	}else*/ if(type.equalsIgnoreCase("String")) {
																		
																		populateValuesForOperatorForTypeString(txtbox, cb1, cb2, formLb, value1, value2);
																		
																	}else if(type.equalsIgnoreCase("number")) {
																		populateValuesForOperatorForTypeNumber(numberbox1, numberbox2,
																				txtbox, cb1, cb2, value1, value2);
																	}
																	
																	/*if(numberLb.isVisible()) {
																		
																		int value = Integer.parseInt(value1)/Integer.parseInt(operatorEnum.getType2());
																		for (Listitem numberItem : numberLb.getItems()) {
																			
																			if(numberItem.getLabel().equals(value+Constants.STRING_NILL)) {
																				
																				numberItem.setSelected(true);
																				break;
																			}
																			
																		}//for
																			
																			
																	}//if numberLb visible
		*/															break;
																}
																
															}
															isSelected = true;
															break;
															
															
														}
														
														
													}//for
													
													
												}//if
												if(isSelected) break;
												
												
										
											if(isSelected) break;
										
											
											}
																				
										}//if
										
										
										
										
										
										
										break;
										
									}
									
									
								}
								//DropProfileComponent(true);
								//onDrop$profileAttributeDivId(event);
								
							
							}
							
							
						}//for
						
				}catch(Exception e) {
							logger.error("Exception ",e);
						}
		}
	
					

		public void populateValuesForOperatorForTypeString(Textbox textbox, Combobox cb1, Combobox cb2, Listbox formLb, String value1, String value2) {
			
			
			if(textbox.isVisible()) {
				
				textbox.setText(value1);
				return;
				
			}
			
			
			if(cb1.isVisible()) {
				
				cb1.setValue(value1);
				if(cb2.isVisible()) {
					cb2.setValue(value2);
					
				}
				
				return;
				
			}
			
			if(formLb.isVisible()){
				
				value1 = value1.substring(value1.lastIndexOf(Constants.DELIMETER_COLON)+1);
				for (Listitem formItem : formLb.getItems()) {
					
					if(value1.equalsIgnoreCase(formItem.getLabel())) {
						
						formItem.setSelected(true);
						break;
					}
					
				}//for
				
				return;
			}
			
		}//populateValuesForOperatorForTypeString()
		
		public void populateValuesForOperatorForTypeNumber(Decimalbox  dblBox1, Decimalbox  dblBox2, 
				Textbox textbox,  Combobox cb1, Combobox cb2,  String value1, String value2)  {
			
			if(textbox.isVisible()) {
				
				textbox.setText(value1);
				return;
				
			}//if
			
			if(cb1.isVisible()) {
				
				cb1.setValue(value1);
				if(cb2.isVisible()) {
					cb2.setValue(value2);
					
				}
				
				return;
				
			}
			
			if(dblBox1.isVisible()) {
				
				try {
					
					if(!value1.equalsIgnoreCase(" ") ) {
					BigDecimal daysDecimalValue1 = new BigDecimal(value1);
					dblBox1.setValue(daysDecimalValue1);
					}
				
					if(dblBox2.isVisible()) {
						
						BigDecimal daysDecimalValue2 = new BigDecimal(value2);
						dblBox2.setValue(daysDecimalValue2);
						
						
					}//if || !value1.isEmpty() || !value1.contains("")
					
					
				} catch (WrongValueException e) {
					logger.error("Exception while parsing the decimal value",e);
				}
				
			}
			
			/*if(campLb.isVisible()) {
				
				for (Listitem campItem : campLb.getItems()) {
					
					if(((Campaigns)campItem.getValue()).getCampaignId().toString().equals(campIdVal)) {
						
						campItem.setSelected(true);
						break;
					}
					
				}
				
			}*/
		}//populateValuesForOperatorForTypeNumber()

		
		public void editSegment(String segmentRule) {
			
				if(segmentRule != null) {
					
					deleteExistedRulesDiv(purIntAttributeDivId);
					deleteExistedRulesDiv(discountAttributeDivId);
					parseRules(segmentRule);
					
				}
				
		}
		private void parseRules(String segRule){
		logger.debug("-- just entered--");
		try{
			
			logger.debug("Segment Rule :"+ segRule);
		
			if(segRule == null ){
				return;
			}
			//listsTypeRgId.setSelectedIndex(1);	
			//segmentRulesVbId.setVisible(true);
			String[] rowsArr = segRule.split("\\|\\|");
			//String[] columnsArr; 
			
			//*************changes made for new approch of interaction attribute******************
			String[]  columnsArr = rowsArr[0].split(":");
			
			for(int i=0;i<rowsArr.length;i++) {
				// top most rule div along with it i need to create a temp div consisting
				Div tempDiv = new Div();
				tempDiv.setSclass("");
				
					
				Label AndLabel = new Label("AND");
				AndLabel.setStyle("display:inline-block; width:20px; padding:0 5px;margin:0 10px;font-weight:bold;");
				AndLabel.setParent(tempDiv);
				if(segRule.contains(";=;")) {
					createDivForEdit1(segRule);
			}
				else {
				addSegmentRules(rowsArr[i], tempDiv); 
				}
				//addRules(rowsArr[i]);
			} // outer for
			//addRuleToolbarId.setVisible(true);
		}catch(Exception e ){
			logger.error("", (Throwable)e);
		}
	}
		int add=0;
		public void displayEditRules(Coupons tempCoupObj,List<SKUTemp> skuTempList) {
			//List<CouponDiscountGeneration> coupDisList = coupDiscGenDao.findByCoupon(tempCoupObj);
			String minReceiptTotalRuleStr="Minimum Receipt Total|total_purchase_amt|number:=|totalPurchaseAmount";
	    	String minTotalQntyRuleStr="Minimum Total Quantity|total_purchase_amt1|number:=|totalQuantityAmount";
			String qtyToDiscRuleStr="Item Quantity to discount|quantity|number:limitQuantity|quantityValue";
			String itemPriceRuleStr="Item Price|item_price|number:itemPriceCriteria|itemPrice";
			String discountTypeRuleStr="Discount Type|discount_type|number:discountType|discountValue";
			String purchaseCard="[#PurchaseCardSet#];=;itemCode;=;<val1>=;<val2>;=;<val3>";
			String purchaseTier="[#PurchaseTier#];=;itemCode;=;<val1>=;<val2>;=;<val3>";
			String purchaseDate="[#PuchaseDate#];=;itemCode;=;<val1>=;<val2>;=;<val3>";
			
			String eligibleItemsRuleStr="Eligible Items|no_of_eligible_items|String:noOfEligibleItems|null";
		//	String DiscountProductsRuleStr="Discount on Products|String:Percentage/Value|discount|";
			

		//	createDivForEdit1(skutemp.getEliRule());
			
			SKUTemp tempObj = skuTempList.get(0);
			String Tier="";
			String Card="";
			
			if(tempCoupObj!=null?tempCoupObj.getDiscountCriteria().equalsIgnoreCase("SKU"):discountRadioGrId.getSelectedItem()==skuRadioId) {
				//if(tempCoupObj.isCombineItemAttributes()) {
					if(tempObj.getTotPurchaseAmount()!=null && !tempObj.getTotPurchaseAmount().toString().isEmpty() && tempObj.getTotPurchaseAmount()>0) {
						minReceiptTotalRuleStr=minReceiptTotalRuleStr.replace("totalPurchaseAmount", tempObj.getTotPurchaseAmount().toString());
						parseRules(minReceiptTotalRuleStr);
					}
					//if(tempObj.getQuantity()!=null) {
					if(tempObj.getLimitQuantity()!=null && !tempObj.getLimitQuantity().isEmpty()) {
						qtyToDiscRuleStr=qtyToDiscRuleStr.replace("limitQuantity", tempObj.getLimitQuantity());
					}else {
						qtyToDiscRuleStr=qtyToDiscRuleStr.replace("limitQuantity", 
								tempObj.getQuantity()!=null && !tempObj.getQuantity().isEmpty() ? "LTE":"ALL");
					}
						qtyToDiscRuleStr=qtyToDiscRuleStr.replace("quantityValue",tempObj.getQuantity()!=null?tempObj.getQuantity():"null");
						parseRules(qtyToDiscRuleStr);
					//}
					Map<String, String> distinctItemAttributes = new HashMap<String, String>();
					String skuAttr ="";
					for(SKUTemp list:skuTempList) {
						String skuValue ="";
						skuAttr=list.getSkuAttribute();
						if(!distinctItemAttributes.containsKey(skuAttr)) {
							if(list.getSkuValue().contains(",") && skuAttr.contains("udf")) {
								skuValue=list.getSkuValue().contains(",")?list.getSkuValue().replace(",",":"):list.getSkuValue();
							}else {
								skuValue=list.getSkuValue();
							}
						}else {
							//if(!skuValue.isEmpty()) skuValue+=",";
							if(list.getSkuValue().contains(",") && skuAttr.contains("udf")) {
							skuValue += distinctItemAttributes.get(skuAttr)+":"+list.getSkuValue();
							}else {
							skuValue += distinctItemAttributes.get(skuAttr)+","+list.getSkuValue();
							}
						}
						distinctItemAttributes.put(list.getSkuAttribute(),skuValue);
					}
					for (String attr : distinctItemAttributes.keySet()) {
						if(attr.equalsIgnoreCase("Description")) {
							isCommaAllowed=true;
						}else isCommaAllowed=false;
						String value = distinctItemAttributes.get(attr);
						String itemAttributeRuleStr=value.contains(",") && (!attr.equalsIgnoreCase("Description") && !value.contains(":"))
								?"attribute|column_name|String:is in|value":"attribute|column_name|String:is|value";
						itemAttributeRuleStr=itemAttributeRuleStr.replace("column_name",attr.equalsIgnoreCase("Vendor") ? segRuleSKUMap.get("Vendor Code")
								: attr.equalsIgnoreCase("Department") ? segRuleSKUMap.get("Department")
										: attr.equalsIgnoreCase("Subclass") ? segRuleSKUMap.get("Subclass") 
												:(attr.contains("udf")||attr.contains("UDF")?attr.toLowerCase():segRuleSKUMap.get(attr)));
						if(itemAttributeRuleStr.contains("udf")||itemAttributeRuleStr.contains("UDF")) {
							itemAttributeRuleStr=itemAttributeRuleStr.replace("attribute",attr.toUpperCase());
						}else {
							itemAttributeRuleStr = itemAttributeRuleStr.replace("attribute",
									attr.equalsIgnoreCase("Vendor") ? "Vendor Code"
											//: attr.equalsIgnoreCase("Department") ? "Department Code"
													//: attr.equalsIgnoreCase("Subclass") ? "Sub-Class" 
											: attr);
						}
						itemAttributeRuleStr=itemAttributeRuleStr.replace("value",value.contains("|")?value.replace("|",SYMPIPELABEL):
												(value.contains(":")?value.replace(":", ","):value));
						parseRules(itemAttributeRuleStr);
					}
					if(tempObj.getItemPrice()!=null && !tempObj.getItemPrice().toString().isEmpty()) {
						itemPriceRuleStr=itemPriceRuleStr.replace("itemPriceCriteria",tempObj.getItemPriceCriteria());
						itemPriceRuleStr=itemPriceRuleStr.replace("itemPrice",tempObj.getItemPrice().toString());
						parseRules(itemPriceRuleStr);
					}
			
					
			
					//}
			
					
					SKUTemp list=skuTempList.get(0);
					if(tempObj.getDiscount()!=null ) {
					discountTypeRuleStr=discountTypeRuleStr.replace("discountType",tempObj.getDiscountType()!=null?tempObj.getDiscountType():tempCoupObj.getDiscountType());
					discountTypeRuleStr=discountTypeRuleStr.replace("discountValue",tempObj.getDiscount().toString());
					parseRules(discountTypeRuleStr);
					}
					logger.debug(tempObj.getDiscount());
					if(tempObj.getNoOfEligibleItems()!=null && !tempObj.getNoOfEligibleItems().isEmpty()) {
						eligibleItemsRuleStr=eligibleItemsRuleStr.replace("noOfEligibleItems",tempObj.getNoOfEligibleItems());
						parseRules(eligibleItemsRuleStr);
					
					} 
					if( list.getEliRule()!=null && list.getEliRule().contains(Constants.DELIMITER_DOUBLE_PIPE) )
					{
						
					String[]	rule1=list.getEliRule().split("\\|\\|");
					for(String rule:rule1)
					{
						if(rule.contains("#PurchaseTier#"))
						{
						
							purchaseTier=purchaseTier.replace("[#PurchaseTier#];=;itemCode;=;<val1>=;<val2>;=;<val3>",rule);
							parseRules(purchaseTier);
						}
						if(rule.contains("#PurchaseCardSet#"))
						{
					
							purchaseCard=purchaseCard.replace("[#PurchaseCardSet#];=;itemCode;=;<val1>=;<val2>;=;<val3>",rule);
							parseRules(purchaseCard);
						}
						if(rule.contains("[#PuchaseDate#]"))
						{
							purchaseDate=purchaseDate.replace("[#PuchaseDate#];=;itemCode;=;<val1>=;<val2>;=;<val3>",rule);
							parseRules(purchaseDate);
						}
					}
						
					}
					if(list.getEliRule()!=null&&!list.getEliRule().contains(Constants.DELIMITER_DOUBLE_PIPE)) {
						
					if(list.getEliRule().contains("#PurchaseTier#")){
						purchaseTier=purchaseTier.replace("[#PurchaseTier#];=;itemCode;=;<val1>=;<val2>;=;<val3>",list.getEliRule());
						parseRules(purchaseTier);
					}
					if(list.getEliRule().contains("[#PurchaseCardSet#]")){
						purchaseCard=purchaseCard.replace("[#PurchaseCardSet#];=;itemCode;=;<val1>=;<val2>;=;<val3>",list.getEliRule());
						parseRules(purchaseCard);
					}
					if(list.getEliRule().contains("[#PuchaseDate#]")){
						purchaseDate=purchaseDate.replace("[#PuchaseDate#];=;itemCode;=;<val1>=;<val2>;=;<val3>",list.getEliRule());
						parseRules(purchaseDate);
					}
	          }
					}	
			

          else {
        	
        
				SKUTemp list=skuTempList.get(0);
				logger.debug(list.getEliRule());
				if( list.getEliRule()!=null && list.getEliRule().contains(Constants.DELIMITER_DOUBLE_PIPE) )
				{
					
				String[]	rule1=list.getEliRule().split("\\|\\|");
				for(String rule:rule1)
				{
					if(rule.contains("#PurchaseTier#"))
					{
					
						purchaseTier=purchaseTier.replace("[#PurchaseTier#];=;itemCode;=;<val1>=;<val2>;=;<val3>",rule);
						parseRules(purchaseTier);
					}
					if(rule.contains("#PurchaseCardSet#"))
					{
					
						purchaseCard=purchaseCard.replace("[#PurchaseCardSet#];=;itemCode;=;<val1>=;<val2>;=;<val3>",rule);
						parseRules(purchaseCard);
					}
					if(rule.contains("[#PuchaseDate#]"))
					{
						purchaseDate=purchaseDate.replace("[#PuchaseDate#];=;itemCode;=;<val1>=;<val2>;=;<val3>",rule);
						parseRules(purchaseDate);
					}
				}
					
				}
				if(list.getEliRule()!=null&&!list.getEliRule().contains(Constants.DELIMITER_DOUBLE_PIPE)) {
					
				if(list.getEliRule().contains("#PurchaseTier#")){
					purchaseTier=purchaseTier.replace("[#PurchaseTier#];=;itemCode;=;<val1>=;<val2>;=;<val3>",list.getEliRule());
					parseRules(purchaseTier);
				}
				if(list.getEliRule().contains("[#PurchaseCardSet#]")){
					purchaseCard=purchaseCard.replace("[#PurchaseCardSet#];=;itemCode;=;<val1>=;<val2>;=;<val3>",list.getEliRule());
					parseRules(purchaseCard);
				}
				if(list.getEliRule().contains("[#PuchaseDate#]")){
					purchaseDate=purchaseDate.replace("[#PuchaseDate#];=;itemCode;=;<val1>=;<val2>;=;<val3>",list.getEliRule());
					parseRules(purchaseDate);
				}
          }
				if(list.getTotPurchaseAmount()!=null && !list.getTotPurchaseAmount().toString().isEmpty() && list.getTotPurchaseAmount()>0) {
					minReceiptTotalRuleStr=minReceiptTotalRuleStr.replace("totalPurchaseAmount", list.getTotPurchaseAmount().toString());
					parseRules(minReceiptTotalRuleStr);
				}
		
				if(tempObj.getDiscount()!=null ) {
				discountTypeRuleStr=discountTypeRuleStr.replace("discountType",tempObj.getDiscountType()!=null?tempObj.getDiscountType():tempCoupObj.getDiscountType());
				discountTypeRuleStr=discountTypeRuleStr.replace("discountValue",list.getDiscount().toString());
				parseRules(discountTypeRuleStr);
				}
		
			
			}  
		}
		    
		
	public String getActualNumber(Double givenDecimalNum){
		
		BigDecimal bigDecimal = new BigDecimal(String.valueOf(givenDecimalNum));
		int intValue = bigDecimal.intValue();
		BigDecimal decimalPart =  bigDecimal.subtract( new BigDecimal(intValue));
		if(decimalPart.doubleValue() != 0.0) return bigDecimal.toPlainString();
		else return intValue+"";
	}
	/*public boolean validateEditedRuleOnSave() {
		String rule = saveRule(true); 
		//if(rule==null || rule.isEmpty()) return false;
		return true;
	}*/
}
			

