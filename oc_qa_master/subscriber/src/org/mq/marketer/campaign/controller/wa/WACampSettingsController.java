package org.mq.marketer.campaign.controller.wa;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.Campaigns;
import org.mq.marketer.campaign.beans.Coupons;
import org.mq.marketer.campaign.beans.MLCustomFields;
import org.mq.marketer.campaign.beans.MailingList;
import org.mq.marketer.campaign.beans.POSMapping;
import org.mq.marketer.campaign.beans.WACampaignsSchedule;
import org.mq.marketer.campaign.beans.WACampaign;
import org.mq.marketer.campaign.beans.SegmentRules;
import org.mq.marketer.campaign.beans.WATemplates;
import org.mq.marketer.campaign.beans.UserCampaignExpiration;
import org.mq.marketer.campaign.beans.UserOrganization;
import org.mq.marketer.campaign.beans.UserWAConfigs;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.controller.SubscriptionDetails;
import org.mq.marketer.campaign.controller.contacts.SegmentEnum;
import org.mq.marketer.campaign.controller.layout.EditorController;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.custom.MyDatebox;
import org.mq.marketer.campaign.dao.CampaignsDao;
import org.mq.marketer.campaign.dao.CampaignsDaoForDML;
import org.mq.marketer.campaign.dao.ContactsDao;
import org.mq.marketer.campaign.dao.CouponCodesDao;
import org.mq.marketer.campaign.dao.CouponsDao;
import org.mq.marketer.campaign.dao.MLCustomFieldsDao;
import org.mq.marketer.campaign.dao.MailingListDao;
import org.mq.marketer.campaign.dao.POSMappingDao;
import org.mq.marketer.campaign.dao.WACampaignScheduleDao;
import org.mq.marketer.campaign.dao.WACampaignScheduleDaoForDML;
import org.mq.marketer.campaign.dao.WACampaignsDao;
import org.mq.marketer.campaign.dao.WACampaignsDaoForDML;
import org.mq.marketer.campaign.dao.SegmentRulesDao;
import org.mq.marketer.campaign.dao.WATemplatesDao;
import org.mq.marketer.campaign.dao.WATemplatesDaoForDML;
import org.mq.marketer.campaign.dao.UserCampaignExpirationDao;
import org.mq.marketer.campaign.dao.UserCampaignExpirationDaoForDML;
import org.mq.marketer.campaign.dao.UserWAConfigsDao;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.LBFilterEventListener;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PageListEnum;
import org.mq.marketer.campaign.general.PageUtil;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Redirect;
import org.mq.marketer.campaign.general.RightsEnum;
import org.mq.marketer.campaign.general.SalesQueryGenerator;
import org.mq.marketer.campaign.general.Utility;
import org.mq.marketer.campaign.general.WAStatusCodes;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.springframework.util.StringUtils;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.A;
import org.zkoss.zul.Bandbox;
import org.zkoss.zul.Button;
import org.zkoss.zul.Column;
import org.zkoss.zul.Columns;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Detail;
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Html;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Popup;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Window;



public class WACampSettingsController extends GenericForwardComposer {

	static final String HTTPS_TOKEN = "https://";

	private MailingListDao mailingListDao;
	private WACampaignsDaoForDML WACampaignsDaoForDML;
	private CampaignsDao campaignsDao;
	private CampaignsDaoForDML campaignsDaoForDML;
	private SegmentRulesDao segmentRulesDao;
	private ContactsDao contactsDao;
	private WACampaignScheduleDao WACampaignScheduleDao;
	private WACampaignScheduleDaoForDML WACampaignScheduleDaoForDML;
	private CouponsDao couponsDao;
	private CouponCodesDao couponCodesDao;
	private WATemplatesDao  WATemplatesDao;
	private WATemplatesDaoForDML  WATemplatesDaoForDML;


	private Image chooseDateTimeImgId;
	private Div navDivId, createNewSegDivId, selectedListDivId, dispRuleDivId,
	frqDivId, prtDtDivId, listNamesDivId;

	private Html previewWin$html;
	private Window previewWin;
	private Long userId;
	private A step1AId,step2AId,step3AId;

	private Set<Long> listIdsSet; 
	private Set<Long> segmentIdsSet; 

	private Label recipentsSourceLblId;
	private MyDatebox prtDtBxId;
	private MyDatebox startDtBxId;
	private MyDatebox endDtBxId;
	private MyDatebox resendOptionWinId$resendOptionDbId;

	private Textbox waCampNameTbId, WAMsgTbId, mblNoTxtBoxId, testWATbId, caretPosTB,waCaretPosTB;
	private Label waCampNameStatusLblId, campNameLblId ,schErrorLblId,resendOptionWinId$errMsgLblId, headerLblId;

	private Listbox frqLbId, resendOptionWinId$resendOptionLbId, insertMergetagsLbId, insertCouponLbId,
	createWaTempWinId$insertMergetagsLbId_createNewTx,createWaTempWinId$insertCouponLbId_createNewTx,createWaTempWinId$insertBarcodeLbId_createNewTx;

	private Rows schedGrdRowsId;


	private Combobox waTempCmbBxId;
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	private Users currUser;
	private WACampaign waCampaign;
	private String isEdit;
	private String draftStatus;
	private Session session;
	private List<Calendar> tempList;
	private  List<WACampaignsSchedule> WACampScheduleList;//contains all the WACampaignSchedule objects
	private Map<Calendar, Rows> rowsMap;
	private Map<Calendar, Row> rowMap;
	private List<WACampaignsSchedule> tempWACampScheduleList;
	private  static String ERROR_STYLE = "border:1px solid #DD7870;";
	private  static String NORMAL_STYLE = "border:1px solid #7F9DB9;";

	/****** will be utilized to know the actions performing with schedules ******/
	private static String TB_ACTION_DELETE = "DELTE";
	private static String TB_ACTION_EDIT = "EDIT";
	private static String TB_ACTION = "TOOLBUTTON ACTION";
	private final String ATTRIBUTE_SOURCE = "Source";


	private MyListener myListener = new MyListener();
	private UserCampaignExpirationDao userCampaignExpirationDao;
	private UserCampaignExpirationDaoForDML userCampaignExpirationDaoForDML;
	private Div includedPromoCodesDivId,campTypeDiv,finalCampaignType;
	private boolean pastDate;

	private boolean areExpiredPromosInAKeyword;
	private String keywordsUsed;
	private Textbox createWaTempWinId$waTempEditId;
	private boolean displayPRTemplateApproval;

	private WACampaignsDao WACampaignsDao;

	public WACampSettingsController() throws Exception {

		userId = GetUser.getUserId();
		mailingListDao = (MailingListDao)SpringUtil.getBean("mailingListDao");
		WACampaignsDao = (WACampaignsDao)ServiceLocator.getInstance().getDAOByName("waCampaignsDao");
		WACampaignsDaoForDML = (WACampaignsDaoForDML)SpringUtil.getBean(OCConstants.WACampaigns_DAO_FOR_DML);
		segmentRulesDao = (SegmentRulesDao)SpringUtil.getBean("segmentRulesDao");
		contactsDao = (ContactsDao)SpringUtil.getBean("contactsDao");
		WACampaignScheduleDao = (WACampaignScheduleDao)ServiceLocator.getInstance().getDAOByName("waCampaignScheduleDao");
		WACampaignScheduleDaoForDML = (WACampaignScheduleDaoForDML)SpringUtil.getBean("waCampaignScheduleDaoForDML");
		couponsDao = (CouponsDao)SpringUtil.getBean("couponsDao");
		couponCodesDao = (CouponCodesDao)SpringUtil.getBean("couponCodesDao");
		campaignsDao = (CampaignsDao)SpringUtil.getBean("campaignsDao");
		campaignsDaoForDML = (CampaignsDaoForDML)SpringUtil.getBean("campaignsDaoForDML");

		

		WATemplatesDao = (WATemplatesDao)ServiceLocator.getInstance().getDAOByName("waTemplatesDao");
		WATemplatesDaoForDML = (WATemplatesDaoForDML )SpringUtil.getBean("waTemplatesDaoForDML");
		WACampScheduleList = new ArrayList<WACampaignsSchedule>();
		rowsMap = new HashMap<Calendar, Rows>();
		rowMap = new HashMap<Calendar, Row>();
		tempWACampScheduleList = new ArrayList<WACampaignsSchedule>();

		pastDate = false;
		areExpiredPromosInAKeyword = false;
		keywordsUsed = "";

		currUser = GetUser.getUserObj();

		session = Sessions.getCurrent();
		isEdit = (String)session.getAttribute("editWACampaign"); 
		waCampaign = (WACampaign)session.getAttribute("waCampaign");
		draftStatus = (String)session.getAttribute("waDraftStatus");

		tempList = new ArrayList<Calendar>();
		String style = "font-weight:bold;font-size:15px;color:#313031;font-family:Arial,Helvetica,sans-serif;align:left";
		PageUtil.setHeader("Create WhatsApp Campaign","",style,true);


		//added for sharing
		listIdsSet = (Set<Long>)session.getAttribute(Constants.LISTIDS_SET);
		segmentIdsSet = (Set<Long>)session.getAttribute(Constants.SEGMENTIDS_SET);


		if(session.getAttribute("isPasswordRequired")!=null) displayPRTemplateApproval  = (Boolean)session.getAttribute("isPasswordRequired");
		
	}

	private List<Div> divList = new ArrayList<Div>();
	private List<A> anchList = new ArrayList<A>();
	private Button saveAsDraftBtnId, saveAsDraftStep1BtnId, gotoStep2BtnId, step1BackBtnId, gotoStep3BtnId, saveAsDraftStep2BtnId;
	@Override
	public void doAfterCompose(Component comp) throws Exception {

		super.doAfterCompose(comp);

		try {
			
			logger.info("<<<<<<<< entered into WACampSettingsController.doAfterCompose >>>>>>>>");
					
			Div mainNavDivId =(Div)Utility.getComponentById("mainNavDivId");
			Components.removeAllChildren(mainNavDivId);

			navDivId.setParent(mainNavDivId);
			navDivId.setVisible(true);
			mainNavDivId.setVisible(true);

			divList.add(step1DivId);
			divList.add(step2DivId);
			divList.add(step3DivId);


			anchList.add(step1AId);
			anchList.add(step2AId);
			anchList.add(step3AId);


			goToNextBtnId.setAttribute("stepNum", getVisibleDiv());
			
			MyCalendar currentCal = new MyCalendar((TimeZone) sessionScope.get("clientTimeZone"));
			currentCal.set(MyCalendar.MINUTE, currentCal.get(MyCalendar.MINUTE) + 15);
			prtDtBxId.setValue(currentCal);
			logger.info("prtDtBxId Value :"+prtDtBxId.getValue());
			startDtBxId.setValue(new MyCalendar(currentCal));
			endDtBxId.setValue(new MyCalendar(currentCal));
			resendOptionWinId$resendOptionDbId.setValue(new MyCalendar(currentCal));



			//TODO :1.enable save, back, save as draft options well
			if( (isEdit!=null && isEdit.equalsIgnoreCase("edit") ) || 
					(draftStatus != null  && !draftStatus.equals(Constants.WA_CAMP_DRAFT_STATUS_STEP_TWO) )) {

				saveAsDraftStep1BtnId.setVisible(false);
				gotoStep2BtnId.setLabel("Save");
				step1BackBtnId.setVisible(true);

				saveAsDraftStep2BtnId.setVisible(false);
				gotoStep3BtnId.setLabel("Save");


			}else {

				saveAsDraftStep1BtnId.setVisible(true);
				gotoStep2BtnId.setLabel("Next");
				step1BackBtnId.setVisible(false);
			}

			if(listIdsSet != null && listIdsSet.size() > 0){

				// Set Editor PH values.
				Set<MailingList> set = new HashSet<MailingList>();

				set.addAll(mailingListDao.findByIds(listIdsSet));
				getPlaceHolderList(set);
			}
		    WaTempSettings();
			waTempCmbBxId.setSelectedIndex(0);

			getCouponsIfAny();

			// one

			if(waCampaign != null) {

				editWACampaign(waCampaign);
             }
          try{

				Set<String> userRoleSet = (Set<String>)session.getAttribute("userRoleSet");

				if(userRoleSet != null) {

					createNewSegDivId.setVisible(userRoleSet.contains(RightsEnum.MenuItem_CreateSegment_VIEW));

				}//if

			}catch (Exception e) {
				logger.error("Exception ::" , e);
			}

			//added in 2.4.6
			if(waCampaign != null)
				populateListOfIncludedPromoCodesLabel();


			waCampNameTbId.setFocus(true);
			LBFilterEventListener.lbFilterSetup(dispsegmentsLbId); 
			LBFilterEventListener.lbFilterSetup(dispMlListsLBoxId);
		} catch (WrongValueException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	} //doAfterCompose
    //on searching segment in 2nd page
	public void onChanging$searchBoxId(InputEvent event) {
		int count=dispsegmentsLbId.getItemCount();
		for (; count > 0; count--) {
			dispsegmentsLbId.removeItemAt(count - 1);
		}
		String key = event.getValue();
		logger.info("got the key ::" + key);
		List<SegmentRules> segList = segmentRulesDao.searchBy(segmentIdsSet,key);
		if(segList != null) {

			Listitem listItem=null;
			for (SegmentRules segRule : segList) {
				listItem = new Listitem();
				listItem.setValue(segRule);
				listItem.setLabel(segRule.getSegRuleName()); 
				listItem.setParent(dispsegmentsLbId); 
			}	
		}
		enableSegment(); 
	}
	public void onChanging$searchBoxIdforlist(InputEvent event){
		int count=dispMlListsLBoxId.getItemCount();
		for (; count > 0; count--) {
			dispMlListsLBoxId.removeItemAt(count - 1);
		}
		String key = event.getValue();
		Listitem listItem;
		List<MailingList> mlList=  mailingListDao.searchMailBy(listIdsSet,key);
		if(mlList != null) {
			for (MailingList mailingList : mlList) {
				listItem = new Listitem();
				listItem.setValue(mailingList);
				listItem.setLabel(mailingList.getListName());
				listItem.setParent(dispMlListsLBoxId); 
			}
		}
		enableMailingList();
	}


	//added in 2.4.6
	private void populateListOfIncludedPromoCodesLabel() {

       try {
			Components.removeAllChildren(includedPromoCodesDivId);
			Set<String> phSet =  new HashSet<String>();

			String content = "";

			String couponIdStr = "";
			logger.info("coupon ph set: "+phSet);

			for(String ph : phSet){
				if(ph.startsWith("CC_")){

					String[] phStr = ph.split("_");

					couponIdStr += couponIdStr.trim().length() <= 0 ? phStr[1] : ","+phStr[1];
				}

			}//for


			logger.info("couponIdStr >>> "+couponIdStr);

			if(couponIdStr.trim().length() == 0){
				Label label1 = new Label("Discount Code(s) used in campaign:   ");
				label1.setParent(includedPromoCodesDivId);
				Label label = new Label("--");
				label.setParent(includedPromoCodesDivId);

				return;
			}else{
				CouponsDao couponsDao= (CouponsDao)SpringUtil.getBean("couponsDao");
				CouponCodesDao couponCodesDao= (CouponCodesDao)SpringUtil.getBean("couponCodesDao");

				Long orgId = currUser.getUserOrganization().getUserOrgId();

				List<Coupons> listOfCoupons = couponsDao.findCouponsByCoupIdsAndOrgId(couponIdStr, orgId);

				Label label1 = new Label("Discount Code(s) used in campaign:   ");
				label1.setParent(includedPromoCodesDivId);
				int numberOfCoupons=0;
				if(listOfCoupons != null && listOfCoupons.size() >0) {
					for (Coupons coupons : listOfCoupons) {

						numberOfCoupons++;
						Label label = new Label(coupons.getCouponName());
						label.setStyle("cursor:pointer;color:blue;text-decoration: underline");
						label.addEventListener("onClick", new MyListener());
						label.setParent(includedPromoCodesDivId);


						if( numberOfCoupons < listOfCoupons.size() ){

							Label label2 = new Label(", ");
							label2.setParent(includedPromoCodesDivId);
						}
}
				}
			}
		} catch (Exception e) {
			logger.error("Exception",e);
		}



	}

	private String getCouponIdsInvolvedInAText(String text){

		try{

			if(text == null){
				logger.error("null text passed");
				return "";
			}

			if(text.trim().length() == 0){
				logger.error("empty text passed");
				return "";
			}


			Set<String> phSet = new HashSet<String>();



			phSet = findCoupPlaceholders(text);

			if(phSet.size() == 0){
				logger.error("no coupons in the text");
				return "";
			}


			String couponIdStr = "";
			for(String ph : phSet){

				if(ph.startsWith("CC_")){

					String[] phStr = ph.split("_");
					if(phStr.length > 2){


						logger.error("Invalid Placeholder: "+ph);
						continue;
					}

					couponIdStr += couponIdStr.trim().isEmpty() ? phStr[1].trim() : ","+phStr[1].trim();


				}

			}

			logger.info("returning couponIdStr >>>>>>>> "+couponIdStr);
			return couponIdStr;


		}catch(Exception e){
			logger.error("Exception occured >>> ",e);
			return "";
		}


	}
	private String isAnyPromoExpiredOrPaused(Set<String> phSet){





		try{

			CouponsDao couponsDao= (CouponsDao)SpringUtil.getBean("couponsDao");
			Long orgId = currUser.getUserOrganization().getUserOrgId();

			String couponIdStr = "";
			for(String ph : phSet){

				if(ph.startsWith("CC_")){

					String[] phStr = ph.split("_");
					if(phStr.length > 2){


						logger.error("Invalid Placeholder: "+ph);
						continue;
					}

					couponIdStr += couponIdStr.trim().isEmpty() ? phStr[1].trim() : ","+phStr[1].trim();
					Long couponId = null;
					try{
						couponId = Long.parseLong(phStr[1]);
					}catch(Exception e){


						logger.error("Exception while parsing to long for phStr[1] : "+phStr[1]+" in ph "+ph);
						continue;
					}

					if(couponId != null){



						List<Coupons> couponsList = couponsDao.findCouponsByCoupIdsAndOrgId(""+couponId, orgId);
						if(couponsList == null){
							logger.error("couponsList is null for couponId : "+couponId+" and orgId "+orgId+", invoved ph is "+ph);
							continue;
						}
					}
				}

			}




			String inValidCoupNames = "";

			if(!couponIdStr.trim().isEmpty()){
				logger.debug("couponIdStr :"+couponIdStr);
				List<Coupons> inValidCoupList = couponsDao.isExpireOrPauseCoupList(couponIdStr, currUser.getUserOrganization().getUserOrgId());

				if(inValidCoupList != null){


					if(inValidCoupList != null && inValidCoupList.size() >0) {

						for (Coupons coupons : inValidCoupList) {
							inValidCoupNames += inValidCoupNames.trim().length() >0 ? ","+coupons.getCouponName() : coupons.getCouponName();
						}

					}

				}

			}

			logger.debug("inValidCoupNames :"+inValidCoupNames);
			logger.debug("couponIdStr :"+couponIdStr);


			return inValidCoupNames;
		}catch(Exception e){
			logger.error("Exception occured, returning no coupons with expired or pending status>>> ",e);
			return "";
		}




	}

	public void onSelect$insertCouponLbId_createNewTx$createWaTempWinId() {
		if(createWaTempWinId$insertCouponLbId_createNewTx.getSelectedIndex() <= 0) {
			return ;
		}
		Coupons selCoupon = createWaTempWinId$insertCouponLbId_createNewTx.getSelectedItem().getValue();
		String replaceStr = Constants.CF_START_TAG+Constants.CC_TOKEN+selCoupon.getCouponId()
		+Constants.CF_END_TAG;
		insertTextForAddTemplatePopup(replaceStr);
		createWaTempWinId$insertCouponLbId_createNewTx.setSelectedIndex(0);
	}

	public void onSelect$insertMergetagsLbId_createNewTx$createWaTempWinId() {
		if(createWaTempWinId$insertMergetagsLbId_createNewTx.getSelectedIndex() == 0) {
			return;
		}
		insertTextForAddTemplatePopup((String)createWaTempWinId$insertMergetagsLbId_createNewTx.getSelectedItem().getValue());		
		}


	public void  getCouponsIfAny() {
		List<Coupons> couponsCampList = couponsDao.findCouponsByStatus(currUser.getUserOrganization().getUserOrgId());
		if(couponsCampList == null || couponsCampList.size() == 0) {

			logger.debug("got no coupons for this org");
			return;
		}

		Listitem item  = null;

		for (Coupons coupons : couponsCampList) {

			item = new Listitem(coupons.getCouponName(), coupons);
			item.setParent(insertCouponLbId);
			item = new Listitem(coupons.getCouponName(), coupons);
			item.setParent(createWaTempWinId$insertCouponLbId_createNewTx);

		}


	}//getCouponsIfAny

	public void onSelect$insertCouponLbId() {


		if(insertCouponLbId.getSelectedIndex() <= 0) {

			logger.debug("selected 0");
			return ;

		}

		Coupons selCoupon = insertCouponLbId.getSelectedItem().getValue();

		String replaceStr = Constants.CF_START_TAG+Constants.CC_TOKEN+selCoupon.getCouponId()
		+Constants.CF_END_TAG;

		insertText(replaceStr);

		insertCouponLbId.setSelectedIndex(0);

	}

	public  List<String> getPlaceHolderList(Set<MailingList> mlistSet) {

		try {
			logger.debug("-- Just Entered --");
			MLCustomFieldsDao mlCustomFieldsDao = (MLCustomFieldsDao)SpringUtil.getBean("mlCustomFieldsDao");
			logger.debug("Got Ml Set of size :" + mlistSet.size());

			POSMappingDao posMappingDao = (POSMappingDao)SpringUtil.getBean("posMappingDao");
			List<String> placeHoldersList = new ArrayList<String>(); 



			List<POSMapping> contactsGENList = posMappingDao.findOnlyByGenType(Constants.POS_MAPPING_TYPE_CONTACTS, GetUser.getUserId() );

			placeHoldersList.addAll(Constants.PLACEHOLDERS_LIST);
			Users user = GetUser.getUserObj();
			if(user.getloyaltyServicetype() != null && user.getloyaltyServicetype().equals(OCConstants.LOYALTY_SERVICE_TYPE_SB) )
			{
				placeHoldersList.removeIf(e -> e.contains("Loyalty Gift Balance"));
			}
			if(user.getloyaltyServicetype() != null && user.getloyaltyServicetype().equals(OCConstants.LOYALTY_SERVICE_TYPE_OC) )
			{
				placeHoldersList.removeIf(e -> e.contains("Loyalty Membership Pin"));			
			}

			if(user.getloyaltyServicetype() != null && !user.getloyaltyServicetype().equals(OCConstants.LOYALTY_SERVICE_TYPE_SB) )
			{
				placeHoldersList.addAll(Constants.OCPLACEHOLDERS_LIST);
			}


			Map<String , String> StoreDefaultPHValues = EditorController.getDefaultStorePhValue(placeHoldersList);

			List<POSMapping> contactsUDFList = posMappingDao.findOnlyByType(Constants.POS_MAPPING_TYPE_CONTACTS, GetUser.getUserId() );



			if(contactsUDFList != null) {

					for (POSMapping posMapping : contactsUDFList) {

					String udfStr;
					if(posMapping.getDefaultPhValue()==null || posMapping.getDefaultPhValue().trim().isEmpty()) {

						udfStr = Constants.UDF_TOKEN + posMapping.getDisplayLabel() +
								Constants.DELIMETER_DOUBLECOLON +
								Constants.CF_START_TAG + Constants.UDF_TOKEN +
								posMapping.getCustomFieldName()  + Constants.DELIMETER_SPACE + Constants.DELIMETER_SLASH + Constants.DELIMETER_SPACE + Constants.DEFUALT_TOKEN+Constants.CF_END_TAG ;


					}
					else {
						udfStr = Constants.UDF_TOKEN + posMapping.getDisplayLabel() +
								Constants.DELIMETER_DOUBLECOLON +
								Constants.CF_START_TAG + Constants.UDF_TOKEN +
								posMapping.getCustomFieldName()+ Constants.DELIMETER_SPACE + Constants.DELIMETER_SLASH + Constants.DELIMETER_SPACE + Constants.DEFUALT_TOKEN + posMapping.getDefaultPhValue() + Constants.CF_END_TAG ;


					}
					placeHoldersList.add(udfStr);
				}//for
}//if
			//END

			for (MailingList mailingList : mlistSet) {

				if(!mailingList.isCustField())  continue;

				List<MLCustomFields> mlcust = mlCustomFieldsDao.findAllByList(mailingList);
				String custField ;
				for (MLCustomFields customField : mlcust) {
					custField = Constants.CF_TOKEN + customField.getCustFieldName() 
					+ Constants.DELIMETER_DOUBLECOLON + Constants.CF_START_TAG + 
					Constants.CF_TOKEN + 
					customField.getCustFieldName().toLowerCase() + Constants.CF_END_TAG;

					if(placeHoldersList.contains(custField)) continue;
					placeHoldersList.add(custField);
				}

			} // for

			Listitem item = null;
			for (String placeHolder : placeHoldersList) {

				if(placeHolder.trim().startsWith("--") || placeHolder.toLowerCase().contains(("place holder"))) { //Ignore
					continue;
				}

				if(placeHolder.startsWith("Unsubscribe Link") || placeHolder.startsWith("Web-Page Version Link") ||
						placeHolder.startsWith("Share on Twitter") || placeHolder.startsWith("Share on Facebook") ||
						placeHolder.startsWith("Forward To Friend") || placeHolder.startsWith("Subscriber Preference Link") ){
					continue;
				}

				String[] phTokenArr =  placeHolder.split("::"); 
				String key = phTokenArr[0];
				StringBuffer value = new StringBuffer(phTokenArr[1]);
				if(StoreDefaultPHValues.containsKey(placeHolder)) {

					value.insert(value.lastIndexOf("^"), StoreDefaultPHValues.get(placeHolder));
					placeHolder = placeHolder.replace(phTokenArr[1], value.toString());
				}


				for (POSMapping posMapping : contactsGENList) {

					if(!key.equalsIgnoreCase(posMapping.getCustomFieldName()) || posMapping.getCustomFieldName().startsWith("UDF")  ) continue;

					if(posMapping.getDefaultPhValue() == null || posMapping.getDefaultPhValue().isEmpty() ) break;

					value.insert(value.lastIndexOf("^"),  posMapping.getDefaultPhValue() );
					logger.debug(" value ::"+value);
				}

				item =  new Listitem(key,value.toString());
				item.setParent(insertMergetagsLbId);
				item = new Listitem(key,value.toString());
				item.setParent(createWaTempWinId$insertMergetagsLbId_createNewTx);


			} // for


			logger.debug("-- Exit --");
			return placeHoldersList;
		} catch (Exception e) {
			logger.error("Exception ::" , e);
			return null;
		}
	}

	/**
	 * updates the current cursor position
	 */

	public void onChange$caretPosTB(){
		logger.debug("---just entered----");
	}


	public void onBlur$caretPosTB() {

		logger.debug("-----just entered: onBlur event----");

	}

	public void onSelect$insertMergetagsLbId() {

		if(insertMergetagsLbId.getSelectedIndex() == 0) {

			return;
		}


		insertText((String)insertMergetagsLbId.getSelectedItem().getValue());
		insertMergetagsLbId.setSelectedIndex(0);

	}
public void insertTextForAddTemplatePopup(String  value) {
		logger.info("insertText");
		String cp = createWaTempWinId$waCaretPosTB.getValue();
		if (cp == null || cp.length() == 0) {
			cp = "0";
		}
		try{
			int caretPos = Integer.parseInt(cp);
			if (caretPos != -1) {
				String currentValue = createWaTempWinId$waTempContentTxtBxId.getValue();
				int charCount = currentValue.length();
			//	String msg=addingHeaderFoorter(currentValue, charCount);
				String newValue = "";
				if(currentValue != null && currentValue.length() >0 ){
					newValue = currentValue.substring(0, caretPos) + value + currentValue.substring(caretPos);
				}else newValue = value;
//				int ccount = value.length();

				createWaTempWinId$waTempContentTxtBxId.setValue(newValue);
			
//				createWaTempWinId$waTempCharCountTxtBxId.setValue(""+ccount);
				
			}
		}catch(Exception e) {
			logger.error("Exception ::" , e);
		}
	}

//	private String addingHeaderFoorter(String currentValue, int charCount) {
//		// TODO Auto-generated method stub
//		return currentValue;
//	}
	public void insertText(String  value){
		logger.info("insertText");

		String cp = caretPosTB.getValue();
		if (cp == null || cp.length() == 0) {
			cp = "0";
		}
		try{
			int caretPos = Integer.parseInt(cp);
			if (caretPos != -1) {
				String currentValue = WAMsgTbId.getValue();
				int charCount = currentValue.length();
		//		String msg=addingHeaderFoorter(currentValue, charCount);
				String newValue = "";
				if(currentValue != null && currentValue.length() >0 ){
					newValue = currentValue.substring(0, caretPos) + value + currentValue.substring(caretPos);
				}else newValue = value;


				WAMsgTbId.setValue(newValue);

				WAMsgTbId.focus();

			}
		}catch(Exception e) {
			logger.error("Exception ::" , e);
		}
	}

	/**
	 * gives the char count of WA
	 * @param event
	 */
	public void onChanging$WAMsgTbId(InputEvent event){
		try{
			getCharCount( event.getValue());


		}catch (Exception e) {
			logger.debug("Exception **",e);
		}
	}//onChanging$WAMsgTbId(-)



	/**
	 * takes the existing WA Campaign object and set the values in  view
	 * @param waCampaign
	 */
	public void editWACampaign(WACampaign WACampaign) {
		try{
			logger.debug("-----just entered for editting the WA camapign------");
			Calendar cal = Calendar.getInstance();

			String msgContent = WACampaign.getMessageContent();
			WAMsgTbId.setValue(msgContent);

			//String msgType = WACampaign.getMessageType();

			waCampNameTbId.setValue(WACampaign.getWaCampaignName());
			waCampNameTbId.setDisabled(true);

			/******* set character count textbox value****/

			getCharCount(msgContent);
	
			WAMsgTbId.setReadonly(false);

			logger.debug("----WA_CAMP_DRAFT_STATUS_STEP_TWO----");
			configureDivDefaultChanges(step2DivId);

			/****get the existing schedules(if any) ********/
			WACampScheduleList = WACampaignScheduleDao.getByWACampaignId(WACampaign.getWaCampaignId());

			if(WACampScheduleList.size()>0) {
				loadSchedule();
				/**
				 * Process Active & Archived Schedules
				 */
				//Active
				List<WACampaignsSchedule> activeCampSchedList = getActiveCampScheduleList(WACampScheduleList);
				if(activeCampSchedList != null && activeCampSchedList.size() > 0){
					createRowUpComingListBox(activeCampSchedList.get(0), 0, true);
					createDivUpComingCampaigns(true, activeCampSchedList.get(0));
				}
				else{
					createDivUpComingCampaigns(false, null);
				}

				//Archived
				List<WACampaignsSchedule> archiveCampSchedList = getArchivedCampScheduleList(WACampScheduleList);

				if(archiveCampSchedList != null &&  archiveCampSchedList.size() > 0){
					drawArchivedDiv(archiveCampSchedList.get(0));
					drawSentListBox(archiveCampSchedList.get(archiveCampSchedList.size()-1));
				}
				else{
					campaignSentDivId.setVisible(false);
				}
			}



			if(draftStatus != null && draftStatus.equals(Constants.WA_CAMP_DRAFT_STATUS_STEP_TWO)) {

				Clients.evalJavaScript("changeStep(2, true);");
			}

			if(WACampaign.getDraftStatus().equals(Constants.WA_CAMP_DRAFT_STATUS_STEP_THREE) ||
					WACampaign.getDraftStatus().equals(Constants.WA_CAMP_DRAFT_STATUS_STEP_COMPLETE) ){

				logger.debug("----WA_CAMP_DRAFT_STATUS_STEP_THREE----");

				campNameLblId.setValue(WACampaign.getWaCampaignName());



				setMlistLinks();

				step2AId.setSclass("req_step_completed");
				Clients.evalJavaScript("changeStep(3, true);");
			}

			//set the modified date to WA campaign
			WACampaign.setModifiedDate(cal);



		}catch (Exception e) {
			logger.error("Exception ::" , e);
		}

	}//editWACampaign


	/**
	 * loads the existing schedules for the selected WA campaign to view call only when we are 
	 * editing the existing WA Campaign
	 */

	public void loadSchedule() {
		try{

			logger.debug("----just entered----");
			Long id = null;
			if(WACampScheduleList == null){
				return;
			}
			WACampaignsSchedule tempSchedule = null;
			
			logger.debug("No of the schedules loaded from DB are :"+WACampScheduleList.size());

			/****** creates the row for each schedule *******/
			for( WACampaignsSchedule WACampaignsSchedule : WACampScheduleList){
				logger.debug("first time");
				tempList.add(WACampaignsSchedule.getScheduledDate());
				/****** create rows(children if any) according to the existing schedules********/
				createRow(WACampaignsSchedule,null);

			}//for each

		}catch ( Exception e) {

			logger.error("** Exception while creating a row ",e);
		}

	}// loadSchedule()




	Label warnLblId;
	public void getCharCount(String msgContent) {
		try {
			int charCount = msgContent.length();
			logger.info("char count :"+charCount);


		} catch (Exception e) {
			logger.debug("Exception while getting the character count",e);
		}//catch

	}//getCharCount


	Div step1DivId,step2DivId,step3DivId;


	public List<MailingList> getMailingLists() {
		List<MailingList> mlLists = null;
		try {
			mlLists = mailingListDao.findByIds(listIdsSet); 
		}catch ( Exception e) {
			logger.error("Exception ::" , e);
		}
		return mlLists;
	}//getMailingLists()

	private Div buttonsDivId;
	public void onClick$proceedBtnId() {

		buttonsDivId.setVisible(true);
		onClick$goToNextBtnId();
		step1DivId.setVisible(false);
		step2DivId.setVisible(true);
		step1AId.setSclass("req_step_completed");
		step2AId.setSclass("req_step_current");
	}

	private Button goToNextBtnId, backButtonId;

	public void onClick$saveAsDraftStep1BtnId() {
		if (!validateCurrDiv(step1DivId, true))
			return;

	}

	public void onClick$gotoStep2BtnId() {

		String msg=WAMsgTbId.getValue();
		logger.info("asd value"+msg);
		if(!Utility.validateName(msg))
		{
			if( Messagebox.show("There are some special characters in this message. Do you want to continue?", "Continue?",
					Messagebox.YES | Messagebox.NO, Messagebox.QUESTION) == Messagebox.NO) {

				return;
			}	
		}

		if(! validateCurrDiv(step1DivId, false)) {
			return;
		}
		populateListOfIncludedPromoCodesLabel();

	}


	public void onClick$gotoStep3BtnId() {

		if(! validateCurrDiv(step2DivId, false)) Redirect.goTo(PageListEnum.CAMPAIGN_WACAMPAIGN_LIST); //return;
		populateListOfIncludedPromoCodesLabel();

	}


	public void onClick$goToNextBtnId() {

		try {

			// for validating atleast on contact is present or not
			if(!otherCampSettings()) return ;


			if(schedGrdRowsId.getChildren().size()>0){ 
				// if added active schedules are there

				if(invalidPromoCodes()) return;


				saveWACampSchedule(schedGrdRowsId);

				if(activeCount > 0){
					if(pastDate){
						pastDate = false;
						return;
					}
					MessageUtil.setMessage("WhatsApp campaign scheduled successfully.", "color:green;");
				}

				else if(activeCount == 0) {

					try {
						if( Messagebox.show("There are no active schedules. " +
								"Do you want to continue?", "Confirm", 
								Messagebox.OK|Messagebox.CANCEL, Messagebox.QUESTION)
								== Messagebox.CANCEL) {
							return;
						}
					} catch (Exception e) {
						logger.error("Exception ::" , e);
					}
				}
				setWACampStatus(); // set the status to WACampaign based on the WACampaignSchedule(s) status

				Redirect.goTo(PageListEnum.CAMPAIGN_WACAMPAIGN_LIST);

			}
			else{

				MessageUtil.setMessage("No active WhatsApp campaign schedules are present." +
						"Please add at least one schedule.", "color:red", "top");
				return ;
			}//else
		} catch (Exception e) {
			logger.error("Exception ::" , e);
		}

	}

	public void saveWACampSchedule(Rows rows) {

		try{
			logger.debug("----just entered-----");
			if(rows != null){
				
				Grid grid = null;
				List<Component> childRowList = null;
				List<Component> rowList = new ArrayList<Component>();
				List<WACampaignsSchedule> passList = new ArrayList<WACampaignsSchedule>();
				List<WACampaignsSchedule> activeList = new ArrayList<WACampaignsSchedule>();

				rowList = rows.getChildren();
				WACampaignsSchedule waCampaignSchedule;

				UserCampaignExpiration triggeredEmail = getTriggeredAlertEmail();
				Calendar expiredOn = null;
				if(triggeredEmail != null){

					expiredOn = triggeredEmail.getSentOn();
					if(expiredOn != null)
					expiredOn.add(Calendar.DAY_OF_YEAR, 7);

				}

				int expiredSchedules = 0 ;
				byte expiredCount = 0;
				List<String> expriedScheduleDates = new ArrayList<String>();
 
				for(Component eachComp : rowList) {
					Row row =(Row)eachComp;
					if(row.isVisible()){
						waCampaignSchedule = (WACampaignsSchedule)row.getValue();
						logger.info("Id .........."+waCampaignSchedule.getwaCsId()+":::"+waCampaignSchedule.getStatusStr());
						if(waCampaignSchedule.getStatus() == 0){
							activeCount++;

						}else if(waCampaignSchedule.getStatus() == 1){
							logger.info("Sent .........."+waCampaignSchedule.getwaCsId()+":::"+waCampaignSchedule.getStatusStr());
							sentCount++;
						}
							passList.add(waCampaignSchedule);

					}//if
				}//for

				boolean isDraft =false,draftStatus=false;//Calendar ;//expiredOn = null;
				Calendar currentCal = Calendar.getInstance();
				for(WACampaignsSchedule campSchedule : passList){


					//if campSchedule status is sent no need to do anything
					if(campSchedule.getStatus() == 1) {
						sentCount++;
						logger.info("Campaign Status is Sent No Need to do Anything.");
						continue;
					}

					//if draft and campSchedule status is active changing it to draft
					if(draftStatus && (campSchedule.getStatus() == 0 || campSchedule.getStatus() == 2 )) {
						campSchedule.setStatus((byte)2);
						if(expiredOn != null && campSchedule.equals(expiredOn)){

							//find the schedule which is last but one in the periodical schedules
							triggeredEmail.setStatus(OCConstants.USER_ALERT_CAMPAIGN_EXPIRED_STATUS_DRAFT);
							triggeredEmail.setModifiedDate(Calendar.getInstance());
							userCampaignExpirationDaoForDML.saveOrUpdate(triggeredEmail);

						}
						continue;
					}

					if(!draftStatus && (campSchedule.getStatus() == 0 || campSchedule.getStatus() == 2 )) {
						if( campSchedule.getStatus() == 2 && campSchedule.getScheduledDate().before(currentCal)){
							logger.info("Setting Status as Expired");
							isDraft = true;
							campSchedule.setStatus((byte)7);
							expiredSchedules ++;
							expriedScheduleDates.add(MyCalendar.calendarToString(campSchedule.getScheduledDate(), MyCalendar.FORMAT_DATETIME_STYEAR));
							expiredCount++;
						}
						else{
							logger.info("Setting Status as Active");
							campSchedule.setStatus((byte)0);
							activeCount++;
							activeList.add(campSchedule);
						}

						if(campSchedule.getStatus() == 2 && expiredOn != null && campSchedule.equals(expiredOn)){

							//find the schedule which is last but one in the periodical schedules
							Calendar now = Calendar.getInstance();
							int onOrAfter = now.compareTo(campSchedule.getScheduledDate()) ;
							String status = onOrAfter <= 0 ? OCConstants.USER_ALERT_CAMPAIGN_EXPIRED_STATUS_ACTIVE : 
								OCConstants.USER_ALERT_CAMPAIGN_EXPIRED_STATUS_EXPIRED;
							triggeredEmail.setStatus(status);
							triggeredEmail.setModifiedDate(Calendar.getInstance());
							userCampaignExpirationDaoForDML.saveOrUpdate(triggeredEmail);

						}
					}
					boolean flag = ((campSchedule.getStatus() == 0) && campSchedule.getScheduledDate().before(currentCal)); 

					// checks whether the scheduled date is less than current date.
					if(flag  || (!flag && isDraft)){
						if(campSchedule.getStatus() == 0 &&
								campSchedule.getScheduledDate().before(currentCal)) {
							logger.info("campSchedule.getScheduledDate() :::::;"+MyCalendar.calendarToString(campSchedule.getScheduledDate(), MyCalendar.FORMAT_SCHEDULE_TIME));
							logger.info("currentCalcurrentCal :::"+MyCalendar.calendarToString(currentCal, MyCalendar.FORMAT_SCHEDULE_TIME));
							schErrorLblId.setValue("Schedule dates can not be past dates");
							pastDate = true;
							return;
						}
					}

				}
				if(expiredSchedules > 0 && expriedScheduleDates.size() > 0){
					MessageUtil.setMessage(getRequiredMessage(expriedScheduleDates,activeList,expiredSchedules),  "color:blue");
				}
				
				
				WACampaignScheduleDaoForDML.saveByCollection(passList);

			}//if

		}catch (Exception e) {
			// TODO: handle exception
			logger.error("Exception ::" , e);
		}//catch
	}//saveWACampSchedule(-) 

	private String getRequiredMessage(List<String> expriedScheduleDates, List<WACampaignsSchedule> passList, int expiredSchedules){

		StringBuffer reqdMSgStrBfr = new StringBuffer();

		if(expiredSchedules == 1 && activeCount == 1){

			reqdMSgStrBfr.append("1 schedule on "+MyCalendar.calendarToString(passList.get(0).getScheduledDate(), MyCalendar.FORMAT_DATEONLY,(TimeZone) sessionScope.get("clientTimeZone"))+" has been activated\nwhile" +
					" 1 unsent, past schedule on "+MyCalendar.calendarToString(MyCalendar.string2Calendar(expriedScheduleDates.get(0)), MyCalendar.FORMAT_DATEONLY,(TimeZone) sessionScope.get("clientTimeZone")) +" has expired ");

		}else if(expiredSchedules == 1 && activeCount > 1){

			reqdMSgStrBfr.append(activeCount+" schedules between "+MyCalendar.calendarToString(passList.get(0).getScheduledDate(), MyCalendar.FORMAT_DATEONLY,(TimeZone) sessionScope.get("clientTimeZone"))+" to "
					+MyCalendar.calendarToString(passList.get(passList.size()-1).getScheduledDate(), MyCalendar.FORMAT_DATEONLY,(TimeZone) sessionScope.get("clientTimeZone")) +" have been activated\nwhile"+
					" 1 unsent, past schedule on "+MyCalendar.calendarToString(MyCalendar.string2Calendar(expriedScheduleDates.get(0)), MyCalendar.FORMAT_DATEONLY,(TimeZone) sessionScope.get("clientTimeZone")) +" has expired ");

		}else if(expiredSchedules > 1 && activeCount == 1){

			reqdMSgStrBfr.append("1 schedule on "+MyCalendar.calendarToString(passList.get(0).getScheduledDate(), MyCalendar.FORMAT_DATEONLY,(TimeZone) sessionScope.get("clientTimeZone"))+
					" has been activated\nwhile "+expiredSchedules+" unsent, past schedules between "+MyCalendar.calendarToString(MyCalendar.string2Calendar(expriedScheduleDates.get(0)), MyCalendar.FORMAT_DATEONLY,(TimeZone) sessionScope.get("clientTimeZone")) +" & "
					+MyCalendar.calendarToString(MyCalendar.string2Calendar(expriedScheduleDates.get(expriedScheduleDates.size()-1)), MyCalendar.FORMAT_DATEONLY,(TimeZone) sessionScope.get("clientTimeZone"))  +" has expired ");

		}else if(expiredSchedules > 1 && activeCount > 1){

			reqdMSgStrBfr.append(activeCount+" schedules between "+MyCalendar.calendarToString(passList.get(0).getScheduledDate(), MyCalendar.FORMAT_DATEONLY,(TimeZone) sessionScope.get("clientTimeZone"))+" to "+
					MyCalendar.calendarToString(passList.get(passList.size()-1).getScheduledDate(), MyCalendar.FORMAT_DATEONLY,(TimeZone) sessionScope.get("clientTimeZone")) +" have been activated\nwhile " +
					expiredSchedules+" unsent, past schedules between "+ MyCalendar.calendarToString(MyCalendar.string2Calendar(expriedScheduleDates.get(0)), MyCalendar.FORMAT_DATEONLY,(TimeZone) sessionScope.get("clientTimeZone")) +" & "
					+MyCalendar.calendarToString(MyCalendar.string2Calendar(expriedScheduleDates.get(expriedScheduleDates.size()-1)), MyCalendar.FORMAT_DATEONLY,(TimeZone) sessionScope.get("clientTimeZone"))+"has expired");

		}else if(expiredSchedules == 0 && activeCount == 1){

			reqdMSgStrBfr.append("1 schedule on "+MyCalendar.calendarToString(passList.get(0).getScheduledDate(), MyCalendar.FORMAT_DATEONLY,(TimeZone) sessionScope.get("clientTimeZone"))+" has been activated");

		}else if(expiredSchedules == 0 && activeCount > 1){

			reqdMSgStrBfr.append(activeCount+" schedules between "+MyCalendar.calendarToString(passList.get(0).getScheduledDate(), MyCalendar.FORMAT_DATEONLY,(TimeZone) sessionScope.get("clientTimeZone"))+" to "+
					MyCalendar.calendarToString(passList.get(passList.size()-1).getScheduledDate(), MyCalendar.FORMAT_DATEONLY,(TimeZone) sessionScope.get("clientTimeZone")) +" have been activated ");

		}else if(expiredSchedules == 1 && activeCount == 0){

			reqdMSgStrBfr.append("1 unsent, past schedule on "+  MyCalendar.calendarToString(MyCalendar.string2Calendar(expriedScheduleDates.get(0)), MyCalendar.FORMAT_DATEONLY,(TimeZone) sessionScope.get("clientTimeZone"))+" has expired ");

		}else if(expiredSchedules > 1 && activeCount == 0){

			reqdMSgStrBfr.append(expiredSchedules+" unsent, past schedules between "+ MyCalendar.calendarToString(MyCalendar.string2Calendar(expriedScheduleDates.get(0)), MyCalendar.FORMAT_DATEONLY,(TimeZone) sessionScope.get("clientTimeZone")) +" & "
					+MyCalendar.calendarToString(MyCalendar.string2Calendar(expriedScheduleDates.get(expriedScheduleDates.size()-1)), MyCalendar.FORMAT_DATEONLY,(TimeZone) sessionScope.get("clientTimeZone"))+" has expired ");
		}

		logger.info("reqdMSg >>> "+reqdMSgStrBfr);

		return reqdMSgStrBfr.toString();
	}
	private UserCampaignExpiration getTriggeredAlertEmail() {

		if(userCampaignExpirationDao == null) {

			try {
				userCampaignExpirationDao = (UserCampaignExpirationDao)ServiceLocator.
						getInstance().getDAOByName(OCConstants.USER_CAMPAIGN_EXPIRATION_DAO);
				userCampaignExpirationDaoForDML = (UserCampaignExpirationDaoForDML)ServiceLocator.
						getInstance().getDAOForDMLByName(OCConstants.USER_CAMPAIGN_EXPIRATION_DAO_FOR_DML);
			} catch (Exception e) {
				logger.error("Exception ", e);
			}
		}

		List<UserCampaignExpiration> existingTriggerList = userCampaignExpirationDao.
				findBy(waCampaign.getUsers().getUserId(), waCampaign.getWaCampaignId());

		UserCampaignExpiration userCampaignExpiration = null;

		if(existingTriggerList != null && existingTriggerList.size() > 0){

			userCampaignExpiration = existingTriggerList.get(0);

		}

		return userCampaignExpiration;
	}

	// used to set the actual status for WACampaign
	int activeCount; 
	int sentCount;
	public void setWACampStatus() {

		try{
			if(activeCount >0 && sentCount > 0){ 
				waCampaign.setStatus(Constants.CAMP_STATUS_RUNNING);
			}
			else if(activeCount == 0 && sentCount > 0) {
				waCampaign.setStatus(Constants.CAMP_STATUS_SENT);
			}
			else if(activeCount >0 && sentCount == 0){
				waCampaign.setStatus(Constants.CAMP_STATUS_ACTIVE);
			}else{
				waCampaign.setStatus(Constants.CAMP_STATUS_DRAFT);
			}
			if(session!=null || currUser != null){
				WACampaignsDaoForDML.saveOrUpdate(waCampaign);
			}
		}catch (Exception e) {
			logger.error("**Exception while setting the status to the WA campaign",e);
		}
	}//setWACampStatus


	private Radiogroup configurelistRgId;
	private Div mlDivId, segDivId;

	public void onCheck$configurelistRgId() {

		if(configurelistRgId.getSelectedIndex() == 0){
			mlDivId.setVisible(true);
			segDivId.setVisible(false);

		}else if(configurelistRgId.getSelectedIndex() == 1) {

			mlDivId.setVisible(false);
			segDivId.setVisible(true);


		}



	}

	public List getSegmentRules() {
		List segLists = null;
		try {
			segLists = segmentRulesDao.findByIds(segmentIdsSet); 
		}catch (Exception e) {
			logger.error("** Exception : " + e);
		}
		return segLists;
	}





	private Listbox dispMlListsLBoxId, dispsegmentsLbId;
	
	//for editing WAcampaign
	public void configureDivDefaultChanges(Div currDiv) {

		if(currDiv.getId().equals(step2DivId.getId())) {

			if(dispMlListsLBoxId.getItemCount() > 0 || dispsegmentsLbId.getItemCount() > 0) return;
			List<MailingList> mlList = getMailingLists();
			if (mlList != null && mlList.size() < 1){
				MessageUtil.setMessage("Please create a contact list.",
						"color:red", "TOP");
				return;
			}
			Listitem listItem;

			if(mlList != null ) {
				for (MailingList mailingList : mlList) {
					listItem = new Listitem();
					listItem.setValue(mailingList);
					listItem.setLabel(mailingList.getListName());
					listItem.setParent(dispMlListsLBoxId); 
				}
			}

			List<SegmentRules> segList = getSegmentRules(); 

			if(segList != null) {
				for (SegmentRules segRule : segList) {
					listItem = new Listitem();
					listItem.setValue(segRule);
					listItem.setLabel(segRule.getSegRuleName());
					listItem.setParent(dispsegmentsLbId); 
				}

			}
			if(waCampaign != null && isEdit !=null ) {

				String listType = waCampaign.getListType();
				if(listType != null) {
					if(listType.equals("Total")) {

						enableMailingList();

					}else{

						enableSegment();


					}

				}//if
			}//if

		}

	}



	public void enableSegment() {


		String segRule = waCampaign.getListType();
		logger.debug("Segment Rule :"+ segRule);
		if(segRule != null && !segRule.equalsIgnoreCase("Total") && segRule.startsWith("Segment")) {

			String segRuleId = segRule.split(""+Constants.DELIMETER_COLON)[1];
			List<SegmentRules> segmenRules = segmentRulesDao.findById(segRuleId);
			if(segmenRules == null) {

				MessageUtil.setMessage("Configured segment no longer exists. You might have deleted it.", "color:red;");
				configurelistRgId.setSelectedIndex(1);
				onCheck$configurelistRgId();
				return ;


			}

			String segRuleIds= "";
			String listIdsStr = "";
			String dispSegRule = "";
			listnamesStr = "";

			for (SegmentRules segmentRules : segmenRules) {

				if(segmentRules == null) {

					MessageUtil.setMessage("One of the segments configured to this campaign no longer exists .You might have deleted it.", "color:red;");
					configurelistRgId.setSelectedIndex(1);
					onCheck$configurelistRgId();
					continue ;

				}

				for(int i=0; i<dispsegmentsLbId.getItemCount(); i++){

					if(dispsegmentsLbId.getItemAtIndex(i).getLabel().equals(segmentRules.getSegRuleName()) ){
						dispsegmentsLbId.addItemToSelection(dispsegmentsLbId.getItemAtIndex(i));


					}//if

				}//for

				if(dispSegRule.length() > 0) dispSegRule += "& \n";
				dispSegRule += dispRule(segmentRules.getSegRule());

				if(listIdsStr.length() > 0) listIdsStr += ",";
				listIdsStr += segmentRules.getSegmentMlistIdsStr();


			}//for

			selRuleLblId.setValue(dispSegRule);

			List<MailingList> mlList = mailingListDao.findByIds(listIdsStr);

			if(mlList == null) {

				logger.debug("Configured Segment's target list is no longer exist.You might have deleted it.");
				configurelistRgId.setSelectedIndex(1);
				onCheck$configurelistRgId();
				return ;


			}

			for (MailingList mailingList : mlList) {

				if(listnamesStr == null) listnamesStr = mailingList.getListName();

				if(listnamesStr != null && listnamesStr.length() > 0) listnamesStr += ", ";
				listnamesStr += mailingList.getListName();

			}//for

			selRuleListLblId.setValue(listnamesStr);


			if(selRuleLblId.getValue().length() != 0) {
				dispRuleDivId.setVisible(true);
			}else{
				dispRuleDivId.setVisible(false);
			}


			configurelistRgId.setSelectedIndex(1);
			onCheck$configurelistRgId();

		}

	}

	public void enableMailingList() {



		String mlListName = "";
		String listNamesStr="";
		configurelistRgId.setSelectedIndex(0);
		Vector<String> mlListNamesVector = new Vector<String>();

		Set<MailingList> mailingListsSet = waCampaign.getMailingLists();

		for(MailingList mailingList : mailingListsSet){
			mlListName = mailingList.getListName();
			mlListNamesVector.add(mlListName);
		}

		/***** to set the selection of configured mailing lists *******/
		for(int i=0; i<dispMlListsLBoxId.getItemCount(); i++){

			if(mlListNamesVector.contains(dispMlListsLBoxId.getItemAtIndex(i).getLabel())){
				dispMlListsLBoxId.addItemToSelection(dispMlListsLBoxId.getItemAtIndex(i));
				if(listNamesStr.length() != 0) listNamesStr+=",";
				listNamesStr += dispMlListsLBoxId.getItemAtIndex(i).getLabel() ;

			}
		}

		selMlLblId.setValue(listNamesStr);

		if(selMlLblId.getValue().length() != 0) {
			selectedListDivId.setVisible(true);
		}else{
			selectedListDivId.setVisible(false);
		}

		configurelistRgId.setSelectedIndex(0);
		onCheck$configurelistRgId();

	}


	public boolean sendTestWA(String mblNumber) {

		try{
			MessageUtil.clearMessage();

			if( mblNumber == null || mblNumber.trim().equals("")) {
				MessageUtil.setMessage("Please provide mobile number to send a test WhatsAPp message.", "color:red;","top");
				return true;
			}
			String templateName="";
			try {
				WATemplates waTempObj = (WATemplates)waTempCmbBxId.getSelectedItem().getValue();
				if(waTempObj!=null) {
					templateName = waTempObj.getTemplateName();
				}
			} catch (Exception e) {
				logger.error("Exception :",e);
			}
			

			UserOrganization userOrganization = currUser.getUserOrganization();
			if(userOrganization == null){
				MessageUtil.setMessage("User does not belong to any organization.", "color:red;","top");
				return false;
			}

			try {
					mblNumber = mblNumber.trim();
					mblNumber = Utility.phoneParse(mblNumber,userOrganization);
					if(mblNumber != null) {
						if(userOrganization.isRequireMobileValidation()){
							if(!mblNumber.startsWith(currUser.getCountryCarrier().toString()) &&
									( (mblNumber.length() >= userOrganization.getMinNumberOfDigits()) && (mblNumber.length() <= userOrganization.getMaxNumberOfDigits()))
									) {

								mblNumber = currUser.getCountryCarrier().toString()+mblNumber;
							}
						}
					}
					else{

						MessageUtil.setMessage("Please provide valid mobile number.","color:red","TOP");
						return true;

					}

			} catch (NumberFormatException e1) {

				MessageUtil.setMessage("Please provide valid mobile number.","color:red","TOP");
				return true;

			}

			logger.debug("mobile numbers to send Test WhatsApp message are====>"+mblNumber);

			try {
				int confirm = Messagebox.show("Are you sure you want to send the test WhatsApp message?","Send test WhatsApp message?",
						Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
				if(confirm != Messagebox.OK){
					mblNoTxtBoxId.setValue("Enter Mobile Number(s)...");
					return false;
				}

			} catch (Exception e) {
				logger.error("Exception :::",e);
			}

			sendWAViaHttpClient(mblNumber,"demo", templateName); //give type=demo for demo purpose
			
			MessageUtil.setMessage("Test message will be sent in a moment.", "color:green;", "top");

		}catch (NumberFormatException e) {
			MessageUtil.setMessage("Please provide valid mobile number to send test WhatsApp.", "color:red;","top");
			logger.error("** Exception",e);
		}catch (Exception e) {
			logger.error("** Exception while sending a test whatsapp",e);
		}

		return true;


	}

	/**
	 * sends a test WA in step 1
	 */

	public void onClick$sendTestWABtnId() { 


		if(sendTestWA (mblNoTxtBoxId.getValue())) {

			mblNoTxtBoxId.setValue("Enter Mobile Number(s)...");
		}

	}//onClick$sendTestWABtnId()
	
	
	/**
	 * 1. get tha WA template basis Template type
	 * 2. get the WA configs
	 * 3. replace the ph and send WA
	 */
	public int sendWAViaHttpClient(String mobileNo, String category, String templateName) {
		
		WATemplatesDao waTemplatesDao = null;
		UserWAConfigsDao userWAConfigsDao = null;
		
		String mobile = Utility.phoneParse(mobileNo.trim(), currUser.getUserOrganization());
		String countryCarrier = currUser.getCountryCarrier() + Constants.STRING_NILL;
		if (mobile.startsWith(countryCarrier) && mobile.length() > currUser.getUserOrganization().getMaxNumberOfDigits()) {
			mobile = mobile.substring(countryCarrier.length());// country code stripped off as 91 already there in template
		}
		int  statusCode = 0;
		
		try {
			waTemplatesDao = (WATemplatesDao)ServiceLocator.getInstance().getDAOByName("waTemplatesDao");
			userWAConfigsDao = (UserWAConfigsDao)ServiceLocator.getInstance().getDAOByName("userWAConfigsDao");
		
			logger.debug("STEP I :: find configured WA Template");
			//WATemplates waTemplate = waTemplatesDao.findTemplateByType(currUser.getUserId(),category);
			WATemplates waTemplate = waTemplatesDao.findTemplateByTypeAndName(currUser.getUserId(),category, templateName);
			if(waTemplate==null || waTemplate.getJsonContent()==null || waTemplate.getProvider()==null || waTemplate.getType()==null || waTemplate.getTemplateRegisteredId()==null) return statusCode;
			
			logger.debug("STEP II :: find configured user WA Configs for the provider");
			UserWAConfigs userWAConfigs =userWAConfigsDao.findWAConfigsByProvider(currUser.getUserId(),waTemplate.getProvider());
			if(userWAConfigs==null || userWAConfigs.getAccessToken()==null || userWAConfigs.getWaAPIEndPoint()==null || userWAConfigs.getFromId()==null) return statusCode;
			
			logger.debug("STEP III :: replace the placeholders");
			String replacedJsonContent = waTemplate.getJsonContent()
										.replace("[WAAPIKey]", userWAConfigs.getAccessToken()) //access key
										.replace("[WAUserID]", userWAConfigs.getFromId()) //channel Id or fromID
										.replace("[WATemplateID]", waTemplate.getTemplateRegisteredId()) //Template namespace
										.replace("[mobile]", mobile)
										.replace("[Name]","Customer"); //for test whatsapp msg
			
			logger.debug("replacedJsonContent =="+replacedJsonContent);
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPost postRequest = new HttpPost(userWAConfigs.getWaAPIEndPoint());
			StringEntity input = new StringEntity(replacedJsonContent,"UTF-8");
			postRequest.setEntity(input);
			postRequest.setHeader("Content-Type", "application/json");
			postRequest.setHeader("Authorization", userWAConfigs.getAccessToken()); //messageBird
			postRequest.setHeader("apikey", userWAConfigs.getAccessToken()); //CM
			//if a template have any other header
			try {
				String headers = waTemplate.getHeaders(); //eg Cookie::__cfruid=4c561e042ebe759324152a99d66c473196b2405a-1670478796
				if(headers!=null && !headers.isEmpty()) {
					String[] headersArr = headers.trim().split(Constants.DELIMETER_COMMA);
					for(String header : headersArr) {
						postRequest.setHeader(header.split(Constants.DELIMETER_DOUBLECOLON)[0], 
												header.split(Constants.DELIMETER_DOUBLECOLON)[1]);
					}
				}
			} catch (Exception e) {
				logger.error("Exception while setting headers",e);
			}
			
			HttpResponse response = httpClient.execute(postRequest);
			logger.info("mobile response ::"+response);
			statusCode = response.getStatusLine().getStatusCode();
	        if (statusCode != 202) {
	            logger.error("Failed : HTTP error code : " + statusCode);
	        }else if(statusCode == 202) {
	        	logger.info("Success : HTTP Success code : " + statusCode);
	        }
			BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
		    String output;
		    StringBuffer totalOutput = new StringBuffer();
		    while ((output = br.readLine()) != null) {
		    	totalOutput.append(output).append("::").append("statusCode:"+statusCode);
		    }
			logger.info("mobile response ::"+totalOutput.toString());
			
		} catch (UnsupportedEncodingException e1) {
			logger.error("Exception ",e1);
		} catch (ClientProtocolException e2) {
			logger.error("Exception ",e2);
		} catch (IOException e3) {
			logger.error("Exception ",e3);
		}catch (Exception e) {
			logger.error("Exception ",e);
		}
		
		return statusCode;
		
	}//sendWAViaHttpClient

	/**
	 * sends a test WA in step 3
	 */

	public void onClick$okBtnId() { 

		if(waCampaign == null) {

			logger.debug("no Root obj found");
			return;

		}
		String sendingMsg = waCampaign.getMessageContent();

	}//onClick$sendTestWABtnId()



	/**
	 * checks whether the given WA campaign name exists or not,if exists dont allows the user further
	 */
	public void onBlur$waCampNameTbId(){
		try {
			MessageUtil.clearMessage();
			/*******check the entered name is valid or not********/
			String emailName = Utility.condense(waCampNameTbId.getValue().trim());
			if(emailName.trim().equals("")){
				// if name is empty
				waCampNameStatusLblId.setStyle("color:red");
				waCampNameStatusLblId.setValue("name should not be empty");
				return;
			}
			else if(!Utility.validateName(emailName)){ // check for special characters
				waCampNameStatusLblId.setStyle("color:red");
				waCampNameStatusLblId.setValue("Special characters not allowed");
				return;
			}//else

			/*******check the entered name is already exists or not********/
			logger.info("nameExists :"+WACampaignsDao);
			boolean nameExists =  WACampaignsDao.checkName(emailName,userId);
			if(nameExists){ // if name already exists
				waCampNameStatusLblId.setStyle("color:red");
				waCampNameStatusLblId.setValue("Already exists");
				return;
			}
			else{ // if name is new
				waCampNameStatusLblId.setStyle("color:#023849");
				waCampNameStatusLblId.setValue("Available");
			}//else

		} catch ( Exception e) {
			logger.error("** Exception : Error occured at WA campaign textbox validation. ",e);
		}//catch

	}//onBlur$waCampNameTbId()

	public boolean validateCurrDiv(Div currDiv, boolean draft) {



		try {

			if(currDiv.getId().equals(step1DivId.getId())) {

				String WACampName = waCampNameTbId.getValue().trim();

				/**** validates WA campaign name *****/
				if(waCampNameTbId.isValid()) {

					if(WACampName.trim().length() == 0) {

						MessageUtil.setMessage("Enter whatsapp campaign name. " +
								"Name cannot be left empty.","color:red","TOP");
						return false;


					}
					if(!Utility.validateName(WACampName)) {

						MessageUtil.setMessage("Enter valid whatsapp campaign name. " +
								"Special characters are not allowed.","color:red","TOP");

						return false;
					}
					if(waCampaign == null) {
						boolean nameExists =  WACampaignsDao.checkName(WACampName,userId);

						if(nameExists){

							MessageUtil.setMessage("This whatsapp campaign name is already in use. " +
									"Please choose a different name.", "color:red", "TOP");
							waCampNameTbId.setFocus(true);
							return false;
						} // else if

					}

				} // if
				else{
					MessageUtil.setMessage("Provide valid whatsapp campaign name.", "color:red", "TOP");
					waCampNameTbId.setFocus(true);
					return false;
				} // else

				
				logger.debug("WA Campaign is  :: "+waCampaign);
			
				/*****validate message content**********+*********/
				String messageContent = WAMsgTbId.getValue();
				WATemplates waTemplates=waTempCmbBxId.getSelectedItem().getValue();
				Long templateId;
				if(waTemplates!=null)
				 templateId= waTemplates.getTemplateId();
				else templateId=Long.parseLong(waCampaign.getWaTemplateId());

				if(messageContent != null && messageContent.trim().length() == 0) {

					MessageUtil.setMessage("WA message cannot be left empty. Please provide message text.", "color:red;", "top");
					return false;

				}

				// validating date placeholder
				String isValidPhStr = Utility.validatePh(messageContent.trim(),GetUser.getUserObj());

				if(isValidPhStr != null) {

					MessageUtil.setMessage("Placeholder "+isValidPhStr+ " is invalid please replace with proper placeholder values.", "color:red;", "top");
					return false;
				}
				//validate coupon placeholders with couponId and userId
				logger.debug("messageContent :"+messageContent);
				Set<String> couponPhSet = findCoupPlaceholders(messageContent.trim());
				logger.debug("couponPhSet :"+couponPhSet);
				String couponIdStr = "";
				for(String ph : couponPhSet){

					if(ph.startsWith("CC_")){

						String[] phStr = ph.split("_");
						if(phStr.length > 2){

							MessageUtil.setMessage("Invalid Placeholder: "+ph, "color:red;", "top");
							return false;
						}

						couponIdStr += couponIdStr.trim().isEmpty() ? phStr[1].trim() : ","+phStr[1].trim();
						Long couponId = null;
						try{
							couponId = Long.parseLong(phStr[1]);
						}catch(Exception e){
							MessageUtil.setMessage("Invalid Placeholder: "+ph, "color:red;", "top");
							return false;
						}

						if(couponId != null){

							CouponsDao couponsDao= (CouponsDao)SpringUtil.getBean("couponsDao");
							Long orgId = currUser.getUserOrganization().getUserOrgId();

							List<Coupons> couponsList = couponsDao.findCouponsByCoupIdsAndOrgId(""+couponId, orgId);
							if(couponsList == null){
								MessageUtil.setMessage("Invalid Placeholder: "+ph, "color:red;", "top");
								return false;
							}
						}
					}

				}

				if(!couponIdStr.trim().isEmpty()){
					logger.debug("couponIdStr :"+couponIdStr);
					List<Coupons> inValidCoupList = couponsDao.isExpireOrPauseCoupList(couponIdStr, currUser.getUserOrganization().getUserOrgId());

					if(inValidCoupList != null){

						String inValidCoupNames = "";
						if(inValidCoupList != null && inValidCoupList.size() >0) {

							for (Coupons coupons : inValidCoupList) {
								inValidCoupNames += inValidCoupNames.trim().length() >0 ? ","+coupons.getCouponName() : coupons.getCouponName();
							}
							MessageUtil.setMessage(	"The Discount Code "+inValidCoupNames+" used in this campaign has either expired or in paused status. " +
									" \n Please change the status of this Discount Code.",
									"color:red", "TOP");
							return false;
						}

					}

				}

				Calendar cal = Calendar.getInstance();

				if(waCampaign == null){
					waCampaign = new WACampaign(cal, currUser);
					waCampaign.setModifiedDate(cal);

				}
				waCampaign.setWaCampaignName(WACampName);


			waCampaign.setMessageContent(messageContent);
                       waCampaign.setWaTemplateId(String.valueOf(templateId));
				logger.info("isEdit ==== >"+isEdit);
				if(gotoStep2BtnId.getLabel().equalsIgnoreCase("Next")) {

					waCampaign.setStatus(Constants.CAMP_STATUS_DRAFT);

					if(waCampaign.getDraftStatus() == null ){
						waCampaign.setDraftStatus(Constants.WA_CAMP_DRAFT_STATUS_STEP_TWO);
					}
				    WACampaignsDaoForDML.saveOrUpdate(waCampaign);
				}
				if(isEdit!=null){
					if(isEdit.equalsIgnoreCase("edit") || (draftStatus != null && draftStatus.equals(Constants.WA_CAMP_DRAFT_STATUS_STEP_THREE))){
						Clients.evalJavaScript("changeStep(3, true);");
						WACampaignsDaoForDML.saveOrUpdate(waCampaign);
					}
					else if(isEdit.equalsIgnoreCase("view")){
						Clients.evalJavaScript("changeStep(2, true);");
					}


				}else{

					configureDivDefaultChanges(step2DivId);
					if(!draft){
						Clients.evalJavaScript("changeStep(2, true);");
					}else{
						waCampaign.setStatus(Constants.CAMP_STATUS_DRAFT);
						if(waCampaign.getDraftStatus() == null )waCampaign.setDraftStatus(Constants.WA_CAMP_DRAFT_STATUS_STEP_TWO);
						WACampaignsDaoForDML.saveOrUpdate(waCampaign);
						Redirect.goTo(PageListEnum.CAMPAIGN_WACAMPAIGN_LIST);
					}
				}

				return true;


			}//if div 1

			else if(currDiv.getId().equals(step2DivId.getId())) {


				if(configurelistRgId.getSelectedIndex() == 0) {

					if(saveMlist() == false) return false;
				}
				else if(configurelistRgId.getSelectedIndex() == 1) {

					if(saveSegRule() == false) return false;

				}

				if(waCampaign == null) {

					logger.debug("need to create the Root Obj prior to the 3rd step");
					return false;

				}

				logger.debug("----WA_CAMP_DRAFT_STATUS_STEP_THREE validate div ----");

				campNameLblId.setValue(waCampaign.getWaCampaignName());

				if(waCampaign.getMessageContent() != null ) {
					String messageType = "";

				}
				waCampaign.setDraftStatus(Constants.WA_CAMP_DRAFT_STATUS_STEP_THREE);
				setMlistLinks();
				goToNextBtnId.setLabel("Schedule");
				backButtonId.setVisible(false);
				Clients.evalJavaScript("changeStep(3, true);");
				WACampaignsDaoForDML.saveOrUpdate(waCampaign);


			}//if div 2

			return true;
		} catch (WrongValueException e) {
			logger.error("Exception ::" , e);
			return false;
		}


	}

	public void onClick$createNewSegAnchId() {

		Redirect.goTo(PageListEnum.CONTACT_MANAGE_SEGMENTS);

	}
	/**
	 * configures the schedules for a specific day
	 */

	public void onClick$prtDtBtnId() {

		if(waCampaign == null) { // to have the WACampaign obj's id 

			logger.debug("no Root object found");
			return;

		}//if
		
		Calendar cal = null;
		schErrorLblId.setValue("");
		schErrorLblId.setVisible(true);
		try {

			schErrorLblId.setValue("");
			logger.info("prtDtBxId value:"+prtDtBxId);
            
			if(prtDtBxId.getValue() == null) {
				schErrorLblId.setValue(" Please select the date");
				return;
			}
			else {
			Calendar tempCal= prtDtBxId.getServerValue();
			logger.info("get Hour :" +tempCal.get(Calendar.HOUR_OF_DAY));
			addDateToGrid(tempCal);
			}

			//ENDS 13, to make similar as email campaign schedules 

		} 
		catch (Exception e) {
			logger.warn("Exception : Date is not selected" + e);
			schErrorLblId.setValue(" Please select the date");
			return;
		}

	} // onClick$prtDtBtnId


	/**
	 * sets the periodical schedules to the WA campaign
	 */
	public void onClick$frqBtnId() {

		try {

			if(waCampaign == null){
                MessageUtil.setMessage("Please fill the entries for WA campaign properly before it is scheduled.", "color:red", "top");
				return;
			}

			schErrorLblId.setValue("");
			Calendar startCal = null;
			Calendar endCal = null;

			try {
				startCal = startDtBxId.getServerValue();
				endCal = endDtBxId.getServerValue();

				//ENDS 1, change  w.r.t similarities with email campaign schedules(change date 16th jan 2016) 

			} 
			catch (Exception e) {
				logger.info("Exception :"+e);
				logger.warn("Exception : startDate / endDate is not selected" + e);
				schErrorLblId.setValue(" Please select the dates");

				return;
			}

			if(startCal == null || endCal == null) {
				schErrorLblId.setValue("Please select the dates");
				return;
			}
			if(startCal.compareTo(Calendar.getInstance()) < 0 ) {
				schErrorLblId.setValue("Start date should be future date");
				return;
			}
			if(	endCal.compareTo(Calendar.getInstance()) < 0 ) {
				schErrorLblId.setValue("End date should be future date");
				return;
			}
			if(endCal.compareTo(startCal) < 0) {
				schErrorLblId.setValue("End date should be after the start date");
				return;
			}

			if(startDtBxId.getServerValue().compareTo( endDtBxId.getServerValue()) == 0) {
				schErrorLblId.setValue("Start and end schedules cannot be same.");
				return ;
			}


			//TODO Same Day Check

			WACampaignsSchedule campaignSchedule1 = addDateCheck(startCal, null, (byte)0);

			if(campaignSchedule1 == null){
				return ;
			}
			int confirm = 0;
			boolean isSameDay = false;
			List<String> dates = new ArrayList<String>();
			Calendar cal = Calendar.getInstance();
			List<String> nowSch = getSchedulesDates(startDtBxId.getServerValue(), endDtBxId.getServerValue(),
					Integer.parseInt((String)frqLbId.getSelectedItem().getValue()),
					((Integer)frqLbId.getSelectedItem().getAttribute("step")).byteValue());

			List<WACampaignsSchedule> activeDraftCampScheduleList =  getActiveCampScheduleList(WACampScheduleList);

			//Like email-----starts
			for(String now : nowSch){
				for (WACampaignsSchedule campSched : activeDraftCampScheduleList) {
					//String now = MyCalendar.calendarToString(cal, MyCalendar.FORMAT_DATEONLY,(TimeZone) sessionScope.get("clientTimeZone"));

					String sameSch = MyCalendar.calendarToString(campSched.getScheduledDate(), MyCalendar.FORMAT_DATEONLY,(TimeZone) sessionScope.get("clientTimeZone"));
					//logger.debug("nownow :"+now+" : sameSchsameSch"+sameSch+" :now.equals(sameSch)"+now.equals(sameSch));
					if( now.equals(sameSch) && (campSched.getStatus() == 0 
							||  campSched.getStatus() == 2 )){

						dates.add(" "+MyCalendar.calendarToString(campSched.getScheduledDate(), MyCalendar.FORMAT_TIME,(TimeZone) sessionScope.get("clientTimeZone")));
					}
				}


			}


			logger.info("dates size is >>>>>>>>> "+(dates != null ? dates.size() : "dates is null")); // remove this after testing

			for(String now : nowSch){
				for (WACampaignsSchedule campSched : activeDraftCampScheduleList) {

					String sameSch = MyCalendar.calendarToString(campSched.getScheduledDate(), MyCalendar.FORMAT_DATEONLY,(TimeZone) sessionScope.get("clientTimeZone"));
					if( now.equals(sameSch) && (campSched.getStatus() == 0 
							||  campSched.getStatus() == 2 )){

						if(activeDraftCampScheduleList != null && activeDraftCampScheduleList.size() > 0){
							confirm = Messagebox.show("Schedule(s) already exist on  "+MyCalendar.calendarToString(activeDraftCampScheduleList.get(0).getScheduledDate(), MyCalendar.FORMAT_MDATEONLY,(TimeZone) sessionScope.get("clientTimeZone"))
							+" to " +  MyCalendar.calendarToString(activeDraftCampScheduleList.get(activeDraftCampScheduleList.size()-1).getScheduledDate(), MyCalendar.FORMAT_MDATEONLY,(TimeZone) sessionScope.get("clientTimeZone"))
							+ " for following time: "
							+"\n "+StringUtils.collectionToCommaDelimitedString(dates)+" \n Do you want to continue & add more?", 
							"Same Day Schedule Found", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
						}
						isSameDay = true; 
						break;
					}
				}
				if(isSameDay) break;
			}
			logger.info("completed for isSameDay .......:"+isSameDay);
			if(confirm == 2){
				logger.info("returning");
				return;
			}

			if(WACampScheduleList != null && (!isSameDay ||(confirm == 1 && isSameDay))) {

				List<WACampaignsSchedule> campaignScheduleList = 
						addDates(startDtBxId.getServerValue(), endDtBxId.getServerValue(),
								Integer.parseInt((String)frqLbId.getSelectedItem().getValue()),
								((Integer)frqLbId.getSelectedItem().getAttribute("step")).byteValue());

				if(campaignScheduleList !=null && campaignScheduleList.size() >0){
					createDivUpComingCampaigns(true,campaignScheduleList.get(campaignScheduleList.size()-1));
					MessageUtil.setMessage("You have added "+campaignScheduleList.size()+" schedules to be sent between "
							+ ""+MyCalendar.calendarToString(campaignScheduleList.get(0).getScheduledDate(), MyCalendar.FORMAT_DATE_FORMAT, (TimeZone) sessionScope.get("clientTimeZone"))
							+" to \n"+MyCalendar.calendarToString(campaignScheduleList.get(campaignScheduleList.size()-1).getScheduledDate(), MyCalendar.FORMAT_DATE_FORMAT, (TimeZone) sessionScope.get("clientTimeZone"))
							+".\n All your active schedules can be viewed by clicking on \n  \'View All Upcoming Schedules\' link.", "color:blue");


					frqBtnId.setAttribute(LATEST_SCH_ON, endCal);
					frqBtnId.setAttribute(START_SCH_ON, startCal);
					frqBtnId.setAttribute(FREEQUENCY, frqLbId.getSelectedItem().getLabel());
				}
				else{
					MessageUtil.setMessage("Please select valid date.", "color:red");
					logger.error("Invalid date...........");
				}
			}

		} 
		catch (NumberFormatException e) {
			logger.error("Exception ::", e);
		}
	}


	/**
	 * getSchedulesDates
	 * @param startDtCal
	 * @param endDtCal
	 * @param frequency
	 * @param step
	 * @return List<String>
	 */
	private List<String> getSchedulesDates(Calendar startDtCal, Calendar endDtCal, 
			Integer frequency, Byte step) {

		if(logger.isDebugEnabled()) {
			logger.debug("-------- just entered getSchedulesDates---------");
		}

		if(startDtCal.compareTo(endDtCal) == 0) {
			schErrorLblId.setValue("Start and end date should not be same");
			return null;
		}

		List<String> csList = new ArrayList<String>();

		WACampaignsSchedule startDtCS = new WACampaignsSchedule(
				startDtCal, waCampaign.getWaCampaignId(), (byte)0,currUser.getUserId() );
		startDtCS.setUserId(currUser.getUserId());

		if( !WACampScheduleList.contains(startDtCS) ) {
			csList.add(MyCalendar.calendarToString(startDtCS.getScheduledDate(), MyCalendar.FORMAT_DATEONLY,(TimeZone) sessionScope.get("clientTimeZone")));

		}

		WACampaignsSchedule nextDtCS;
		Calendar nextDate = Calendar.getInstance();
		Calendar nextDateTemp;
		nextDate.setTime(startDtCal.getTime());



		nextDate.set(frequency, startDtCal.get(frequency)+step);

		/** Generates the dates between the given dates and frequency **/
		while(nextDate.compareTo(endDtCal) <= 0 ) {

			nextDateTemp = Calendar.getInstance();
			nextDateTemp.setTime(nextDate.getTime());

			nextDtCS = new WACampaignsSchedule(nextDateTemp, waCampaign.getWaCampaignId(), (byte)0, currUser.getUserId());

			nextDtCS.setUserId(currUser.getUserId());

			if( !WACampScheduleList.contains(nextDtCS) ) {
				csList.add(MyCalendar.calendarToString(nextDtCS.getScheduledDate(), MyCalendar.FORMAT_DATEONLY,(TimeZone) sessionScope.get("clientTimeZone")));
			}
			nextDate.set(frequency, nextDate.get(frequency)+step);


		}// while

		if(logger.isDebugEnabled()) {
			logger.debug("--------before exiting getSchedulesDates ---------");
		}
		return csList;
	}//getSchedulesDates


	private Button frqBtnId;
	private final String LATEST_SCH_ON = "ENDCAL";
	private final String START_SCH_ON = "STARTCAL";
	private final String FREEQUENCY = "FREQ";
	private Row currentRow;
	private Window resendOptionWinId;
	private Window createWaTempWinId;
	private Textbox createWaTempWinId$waTempNameTxtBxId,createWaTempWinId$waTempContentTxtBxId,createWaTempWinId$waCaretPosTB,
	createWaTempWinId$waTempCharCountTxtBxId;


	/**
	 * prepare schedules to resend the existing  WA campaign
	 */

	public void onClick$resendBtnId$resendOptionWinId() {
		try {
			logger.debug("----just entered to resend the WA campaigns----");
			schErrorLblId.setValue("");
			byte criteria = Byte.parseByte(
					(String)resendOptionWinId$resendOptionLbId.getSelectedItem().getValue());

			WACampaignsSchedule WACampSchedule = (WACampaignsSchedule)currentRow.getValue();
			Calendar resendTempCal = resendOptionWinId$resendOptionDbId.getServerValue();

			if (WACampSchedule.getScheduledDate().getTimeInMillis() +(60*60*1000) > 
			resendTempCal.getTimeInMillis()) {

				resendOptionWinId$errMsgLblId.setValue(
						" Resend date must be atleast one hour after the actual schedule date");
				resendOptionWinId$errMsgLblId.setVisible(true);
				return;
			}//if

			//************** Test for 1 hour gap between scheduled dates ******************
			logger.debug("the time list size is==>"+tempList.size());
			for (Calendar tempCal : tempList) {

				logger.debug("the existing time is===>"+(tempCal.getTimeInMillis() - resendTempCal.getTimeInMillis()));
				if(Math.abs(tempCal.getTimeInMillis() - resendTempCal.getTimeInMillis()) < 3600000 ) {
					resendOptionWinId$errMsgLblId.setValue("Resend date must be at least one hour after the actual schedule date.");
					resendOptionWinId$errMsgLblId.setVisible(true);
					logger.debug("Resend date must be at least one hour after the actual schedule date.");
					return;
				}
			} // for

			tempList.add(resendTempCal);


			/******* add schedule and create a row  *******/
			WACampaignsSchedule WANewCampSchedule = addDate(resendTempCal, WACampSchedule, criteria);

			if(WANewCampSchedule == null) {
				resendOptionWinId$errMsgLblId.setValue("Selected date already exists");
				resendOptionWinId$errMsgLblId.setVisible(true);
				return;
			}

			resendOptionWinId$errMsgLblId.setVisible(false);
			resendOptionWinId.setVisible(false);

			//******** create rows (children if any) according to the given schedules **************
			createRow(WANewCampSchedule, rowsMap.get(WANewCampSchedule.getScheduledDate()));
			Detail detail = (Detail)((Rows)rowsMap.get(WANewCampSchedule.getScheduledDate())).getParent().getParent(); 
			((Grid)((Rows)rowsMap.get(WANewCampSchedule.getScheduledDate())).getParent()).setVisible(true);
			detail.setStyle("display:block;");
			detail.setOpen(true);

		} catch (NumberFormatException e) {
			logger.error("Exception while adding child schedules",e);
			logger.error("Exception ::" , e);
		}//catch

	}//onClick$resendBtnId$resendOptionWinId()


	/**
	 * Creates the list of WACampaignSchedule objects with the given frequency and if any</BR>
	 * date is already scheduled ignores that WACampaignSchedule.
	 * @param startDtCal
	 * @param endDtCal
	 * @param frequency
	 * @param step
	 * @return List of WACampaignSchedule objects
	 */
	private List<WACampaignsSchedule> addDates(Calendar startDtCal, Calendar endDtCal, 
			Integer frequency, Byte step) {

		if(logger.isDebugEnabled()) {
			logger.debug("-------- just entered---------");
		}

		if(startDtCal.compareTo(endDtCal) == 0) {
			schErrorLblId.setValue("Start and end date should not be same");
			return null;
		}

		List<WACampaignsSchedule> csList = new ArrayList<WACampaignsSchedule>();

		WACampaignsSchedule startDtCS = new WACampaignsSchedule(startDtCal,
				waCampaign.getWaCampaignId(), (byte)0, currUser.getUserId() );

				String sameSch="";

		int count = activeCampaingsListlbId.getItemCount();
		boolean isCreditOrExipry   = createRowUpComingListBox(startDtCS,count,false);
		logger.info("campSchedule "+startDtCS.getwaCsId()+": isCreditOrExipry :"+isCreditOrExipry);
		if(isCreditOrExipry == false){
			logger.error("Your Credit Limits are Exipred please contact support");
			return null;
		}
		
		if( !WACampScheduleList.contains(startDtCS) ) {
			WACampScheduleList.add(startDtCS);
			csList.add(startDtCS);
		}

		Row row = new Row();
		row.setValue(startDtCS);

		if(schedGrdRowsId != null) {
			row.setParent(schedGrdRowsId);
		}

		WACampaignsSchedule nextDtCS;
		Calendar nextDate = Calendar.getInstance();
		Calendar nextDateTemp;
		nextDate.setTime(startDtCal.getTime());

		nextDate.set(frequency, startDtCal.get(frequency)+step);

		/** Generates the dates between the given dates and frequency **/
		while(nextDate.compareTo(endDtCal) <= 0 ) {

			nextDateTemp = Calendar.getInstance();
			nextDateTemp.setTime(nextDate.getTime());

			nextDtCS = new WACampaignsSchedule(nextDateTemp,
					waCampaign.getWaCampaignId(), (byte)0, currUser.getUserId() );
			
			count = activeCampaingsListlbId.getItemCount();
			isCreditOrExipry   = createRowUpComingListBox(nextDtCS,count,false);

			logger.info("campSchedule "+nextDtCS.getwaCsId()+": isCreditOrExipry :"+isCreditOrExipry);
			if(isCreditOrExipry == false){
				logger.error("Your Credit Limits are Exipred please contact support");
				break;
			}
			else{
				logger.info("**campSchedule "+nextDtCS.getwaCsId()+": isCreditOrExipry :"+isCreditOrExipry);
			}


			if( !WACampScheduleList.contains(nextDtCS) ) {

				csList.add(nextDtCS);
				WACampScheduleList.add(nextDtCS);
				createRow(nextDtCS, schedGrdRowsId);
			}
			nextDate.set(frequency, nextDate.get(frequency)+step);


		}// while


		logger.debug("size of csList is====> "+csList != null ? csList.size() : "csList is null.");

		if(logger.isDebugEnabled()) {
			logger.debug("--------before exiting---------");
		}
		return csList;

	}//addDates



	private boolean checkAlreadyThereWithinOneHr(List<WACampaignsSchedule> WACampScheduleList,WACampaignsSchedule toBeChekedWACampaignSchedule){
		try{

			for(WACampaignsSchedule aWACampaignSchedule : WACampScheduleList){
				logger.debug("-------- toBeChekedWACampaignSchedule.getScheduledDate())---------"+toBeChekedWACampaignSchedule.getScheduledDate());
				logger.debug("-------- aWACampaignSchedule.getScheduledDate())---------"+aWACampaignSchedule.getScheduledDate());

				if(toBeChekedWACampaignSchedule.getScheduledDate().getTimeInMillis() - aWACampaignSchedule.getScheduledDate().getTimeInMillis() >= 3600000 ){
					//i.e. check with others that new schedule is going to conflict within one hr rule.

				}else{ //'within one hr rule' rule violated
					return true;
				}

			}

			return false;
			//loop is over implies that we didn't find any existing schedule in WACampScheduleList, which is conflicting with toBeChekedWACampaignSchedule.


		}catch(Exception e){
			logger.error("exception:::::::::::::::::"+e);
		}
		return true;
	}


	public void onClick$mblNoTxtBoxId() {

		if(mblNoTxtBoxId.getValue().equals("Enter Mobile Number(s)...")) {

			mblNoTxtBoxId.setValue("");

		}

	}


	/**
	 * prepares the view for schedules
	 * @param cal
	 */
	private void addDateToGrid(Calendar cal) {
		logger.debug(">>>>>>> Started WACampSettingsController :: addDateToGrid <<<<<<< ");
		Calendar currCal = Calendar.getInstance();
		if(cal == null || cal.compareTo(currCal) < 0){
			schErrorLblId.setValue("Please select a future date and time");
			return;
		}

		WACampaignsSchedule WACampSchedule = addDateCheck(cal, null,(byte)0);

		if(WACampSchedule == null) {
			schErrorLblId.setValue("Date already sheduled");
			return;
		}


		int confirm = 0;
		boolean isSameDay = false;

		List<String> dates = new ArrayList<String>();

		for (WACampaignsSchedule campSched : WACampScheduleList) {


			String now = MyCalendar.calendarToString(cal, MyCalendar.FORMAT_DATEONLY,(TimeZone) sessionScope.get("clientTimeZone"));
			String sameSch = MyCalendar.calendarToString(campSched.getScheduledDate(), MyCalendar.FORMAT_DATEONLY,(TimeZone) sessionScope.get("clientTimeZone")); 

			if( now.equals(sameSch) && (campSched.getStatus() == 0 
					||  campSched.getStatus() == 2 )){
				dates.add(" "+MyCalendar.calendarToString(campSched.getScheduledDate(), MyCalendar.FORMAT_TIME,(TimeZone) sessionScope.get("clientTimeZone")));
			}
		}

		for (WACampaignsSchedule campSched : WACampScheduleList) {

			String now = MyCalendar.calendarToString(cal, MyCalendar.FORMAT_DATEONLY,(TimeZone) sessionScope.get("clientTimeZone"));
			String sameSch = MyCalendar.calendarToString(campSched.getScheduledDate(), MyCalendar.FORMAT_DATEONLY,(TimeZone) sessionScope.get("clientTimeZone")); 

			if( now.equals(sameSch) && (campSched.getStatus() == 0 
					|| campSched.getStatus() == 2 )){

				confirm = Messagebox.show("Schedule(s) already exist on "+ MyCalendar.calendarToString(campSched.getScheduledDate(), MyCalendar.FORMAT_MDATEONLY,(TimeZone) sessionScope.get("clientTimeZone"))
				+ " for following time:"
				+"\n "+StringUtils.collectionToCommaDelimitedString(dates)+" \n Do you want to continue & add more?", 
				"Same Day Schedule Found", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);


				isSameDay = true; 
				break;
			}

		}

		if(confirm == 2){
			logger.debug("Message box cancel is clicked.");
			return;
		}

		if(!isSameDay ||(confirm == 1 && isSameDay)) {

			WACampaignsSchedule campSchedule = addDate(cal, null,(byte)0);

			if(campSchedule == null) {
				MessageUtil.setMessage("Date already scheduled.", "color:red", "TOP");
				return;
			}

			int count = activeCampaingsListlbId.getItemCount();
			boolean isCreditOrExipry = createRowUpComingListBox(campSchedule,count,false);
			if(isCreditOrExipry == false){
				logger.error("Your Credit Limits are Exipred please contact support");
				return;
			}
			persistentCamp = false;
			createDivUpComingCampaigns(true,campSchedule);
			createRow(campSchedule, schedGrdRowsId);
			MessageUtil.setMessage("You have added 1 schedule to be sent on "+MyCalendar.calendarToString(cal, MyCalendar.FORMAT_DATE_FORMAT,(TimeZone) sessionScope.get("clientTimeZone"))+".\n All your active schedules can be viewed by clicking on \n \'View All Upcoming Schedules\' link.", "color:blue");
		}
		tempList.add(cal);
		logger.debug(">>>>>>> Completed WACampSettingsController :: addDateToGrid <<<<<<< ");
	}//addDateToGrid




	/**
	 * creates the WACampaignSchedule object with the specified date 
	 * if with the same date no any schedule is existed.
	 * @param selectedDtCal
	 * @param parentId
	 * @param criteria
	 * @return WACampaignSchedule object
	 */
	private WACampaignsSchedule addDate( Calendar selectedDtCal, 
			WACampaignsSchedule WACampSchedule,  byte criteria) {	

		logger.debug("-------- just entered---------");
		
			WACampaignScheduleDao = (WACampaignScheduleDao)SpringUtil.getBean("waCampaignScheduleDao");

			selectedDtCal.set(Calendar.SECOND,0);
			selectedDtCal.set(Calendar.MILLISECOND,0);
		
			Calendar tempCal = Calendar.getInstance();
			tempCal.setTime(selectedDtCal.getTime());

			logger.info("WACampaignScheduleDao ::"+WACampaignScheduleDao);
			Long WACsId = WACampaignScheduleDao.getCurrentId();

			if(logger.isDebugEnabled()) {
				logger.debug(">>>>>>>>> Generated WACsId :"+WACsId);
			}
			WACampaignsSchedule waCampSchedule = new WACampaignsSchedule(tempCal, currUser.getUserId());

			waCampSchedule.setwaCampaignId(waCampaign.getWaCampaignId());
			if(WACampScheduleList.contains(waCampSchedule)) {
				return null;
			}
		

			waCampSchedule.setwaCsId(WACsId);
			waCampSchedule.setStatus((byte)0);
			waCampSchedule.setUserId(currUser.getUserId());
			WACampScheduleList.add(waCampSchedule);
			logger.info("Added one Schedule");


			if(logger.isDebugEnabled()) {
				logger.debug("-------- before returning---------");
			}
		return waCampSchedule;

	}//addDate

	/**
	 * create a row for each schedule of WA campaign and sets the 
	 * WACampaignSchedule object to the created row 
	 * @param WACampaignSchedule 
	 * @param rows
	 */
	private void createRow( WACampaignsSchedule waCampaignSchedule,  Rows rows) {


		//Create Row


		try {
			Row row = new Row();
			row.setValue(waCampaignSchedule);

			if(rows != null) {
				row.setParent(rows);
			}
			else {
				rowMap.put(waCampaignSchedule.getScheduledDate(), row);
			}

			Label tempLabel = new Label(waCampaignSchedule.getDateStrByTimeZone(null,
					(TimeZone) session.getAttribute("clientTimeZone")));
			tempLabel.setParent(row);

			tempLabel = new Label(waCampaignSchedule.getStatusStr());
			tempLabel.setParent(row);


			/**
			 * criteria will be 0 for root schedules for
			 * re send schedules only criteria will be > 0
			 *
			 **/

			Hbox hbox = new Hbox();
			hbox.setParent(row);

			Toolbarbutton tbButton;

			/** * Delete toolbar button** */
			tbButton = new Toolbarbutton("Delete");
			tbButton.setTooltiptext("Delete from schedule");
			tbButton.setImage("/img/action_delete.gif");
			tbButton.setAttribute(TB_ACTION, WACampSettingsController.TB_ACTION_DELETE);
			tbButton.addEventListener("onClick",myListener);

			tbButton.setParent(hbox);

			if(rows != null) {
				rows.invalidate();
			}

		} catch ( Exception e) {

			logger.error("** Exception : while creating a row ", e);
		}

		if (logger.isDebugEnabled()) {
			logger.debug("------------- before exit----------");
		}

	}// createRow()



	private void setMlistLinks() {

		if(waCampaign == null) return;

		if(waCampaign.getListType().startsWith("Segment:"))
		{
			Components.removeAllChildren(listNamesDivId);
			String segment=waCampaign.getListType();
			segment=segment.replace("Segment:", "");
			Hbox mlHbox = new Hbox();
			mlHbox.setSpacing("10px");
			List<SegmentRules> segmentRules=segmentRulesDao.findById(segment);
			A mlLink;
			if(segmentRules!=null){
				for(SegmentRules segmentRule:segmentRules)
				{
					mlLink = new A(segmentRule.getSegRuleName());
					mlLink.setAttribute(ATTRIBUTE_SOURCE, segmentRule);
					mlLink.addEventListener("onClick", new MyListener());
					mlLink.setParent(mlHbox);

				}
			}
			mlHbox.setParent(listNamesDivId);
			recipentsSourceLblId.setValue("Selected Segments(s):");
		}
		else	
		{
			Components.removeAllChildren(listNamesDivId);
			Hbox mlHbox = new Hbox();
			mlHbox.setSpacing("10px");
			Set<MailingList> mlset = waCampaign.getMailingLists();
			A mlLink;
			for (MailingList mailingList : mlset) {
				mlLink = new A(mailingList.getListName());
				mlLink.setAttribute(ATTRIBUTE_SOURCE, mailingList);
				mlLink.addEventListener("onClick", new MyListener());
				mlLink.setParent(mlHbox);
			}
			mlHbox.setParent(listNamesDivId);
			recipentsSourceLblId.setValue("Selected Contact List(s):");
		}
	}
	public void onClick$msgPreviewImgId() {




		if(waCampaign != null) {


			previewWin$html.setContent(waCampaign.getMessageContent());
			previewWin.setVisible(true);

		}


	}


	public void onClick$editMsgImgId() throws Exception {
		isEdit = "edit";

		saveAsDraftStep1BtnId.setVisible(false);
		gotoStep2BtnId.setLabel("Save");
		step1BackBtnId.setVisible(true);
		Clients.evalJavaScript("changeStep(1, true);");		
		WAMsgTbId.setFocus(true);

	}


	public void onClick$editWAListImgId() throws Exception {
		isEdit = "edit";
		saveAsDraftStep2BtnId.setVisible(false);
		gotoStep3BtnId.setLabel("Save");
		Clients.evalJavaScript("changeStep(2, true);");		

	}


	public void onCheck$sendPeriodicallyId() throws Exception {
		frqDivId.setVisible(!frqDivId.isVisible());
		chooseDateTimeImgId.setVisible(false);
		prtDtDivId.setVisible(!prtDtDivId.isVisible());
	}//onCheck$sendPeriodicallyId()

	public void onCheck$prtDateRadioId() throws Exception {

		chooseDateTimeImgId.setVisible(true);
		frqDivId.setVisible(!frqDivId.isVisible());
		prtDtDivId.setVisible(!prtDtDivId.isVisible());

	}//onCheck$prtDateRadioId()




	public void onClick$step1BackBtnId() {

		if(isEdit!=null){
			Clients.evalJavaScript("changeStep(3,true)");
		}else{
			Redirect.goTo(PageListEnum.RM_HOME);
		}
	}

	public void onClick$backStep1ButtonId() {

		if(isEdit!=null){
			if(isEdit.equalsIgnoreCase("edit"))
				Clients.evalJavaScript("changeStep(3,true)");
			else if(isEdit.equalsIgnoreCase("view")){
				Redirect.goTo(PageListEnum.CAMPAIGN_WACAMPAIGN_LIST);
			}
		}else{
			Clients.evalJavaScript("changeStep(1,true)");
		}

	}

	public void onClick$saveAsDraftBtnId() {

		int rowCount = 0;
		Row row = null;

		for(WACampaignsSchedule WACampaignSchedule : WACampScheduleList) {

			if(WACampaignSchedule.getStatus() == 0){
				rowCount++;
			}

		}//for
		if(rowCount != 0) {
			MessageUtil.setMessage("A campaign with upcoming schedules \n cannot be saved as draft."
					+ "\n Please delete all active schedules first.", "color:red");
			return;
		}


		//save the WA campaign
		if(session!=null || currUser != null){
			waCampaign.setStatus(Constants.CAMP_STATUS_DRAFT);
			waCampaign.setDraftStatus(Constants.WA_CAMP_DRAFT_STATUS_STEP_COMPLETE);
			WACampaignsDaoForDML.saveOrUpdate(waCampaign);
			for(WACampaignsSchedule campaignSchedule : WACampScheduleList){
				if(campaignSchedule.getStatus() == 0 || campaignSchedule.getStatus() == 2){
					campaignSchedule.setStatus((byte)2);
				}
				else{
					continue;
				}
			}
			WACampaignScheduleDaoForDML.saveByCollection(WACampScheduleList);
			MessageUtil.setMessage("WA campaign is saved successfully.", "color:green;", "top");

			Redirect.goTo(PageListEnum.CAMPAIGN_WACAMPAIGN_LIST);

		}
		else{
			MessageUtil.setMessage("Problem encountered while saving. Please re-login and try again.", "color:red", "TOP");
			logger.error("** Exception : session is null ** ");
		}//else

	}//onClick$saveAsDraftBtnId

	public void onClick$saveAsDraftStep2BtnId() throws Exception {

		if(configurelistRgId.getSelectedIndex() == 0) {

			if(saveMlist()==false)
				Redirect.goTo(PageListEnum.CAMPAIGN_WACAMPAIGN_LIST);	
			//return;
		}
		else if(configurelistRgId.getSelectedIndex() == 1) {

			if(saveSegRule() == false) return;

		}

		Redirect.goTo(PageListEnum.CAMPAIGN_WACAMPAIGN_LIST);
	}

	public String getDraftStatus(Div currDiv) {


		String draftStatus = null;
		if(currDiv.getId().equals(step1DivId.getId())) {

			draftStatus = Constants.WA_CAMP_DRAFT_STATUS_STEP_TWO;

		}
		else if(currDiv.getId().equals(step2DivId.getId())) {

			draftStatus = Constants.WA_CAMP_DRAFT_STATUS_STEP_THREE;

		}else {

			draftStatus = Constants.WA_CAMP_DRAFT_STATUS_STEP_THREE;
		}

		return draftStatus;

	}//getDraftStatus



	public boolean saveMlist() {

		int num = dispMlListsLBoxId.getItemCount();
		if (num == 0) {
			MessageUtil.setMessage(
					"Please create a contact list to send" +
							" your campaigns to it.","color:red", "TOP");
			return false;
		}
		int mlcount = dispMlListsLBoxId.getSelectedIndex();
		if (mlcount == -1) {
			MessageUtil.setMessage("Select at least one list.", "color:red","TOP");
			return false;
		}

		Set lists = dispMlListsLBoxId.getSelectedItems();
		logger.debug(" No of Mailing lists selected :"+lists.size());


		Set mlSet = new HashSet();
		Listitem li;
		MailingList ml = null;

		String listIdsStr = "";

		for (Object obj : lists) {
			li = (Listitem) obj;
			ml = (MailingList) li.getValue();

			if(listIdsStr.length() != 0) { 
				listIdsStr+=",";
			}	
			listIdsStr += ml.getListId();
			mlSet.add(ml);
		}

		waCampaign.setListType("Total");
		waCampaign.setMailingLists(mlSet);

		if(!otherCampSettings()) {
			return false;
		}


		return true;

	}

	private String listnamesStr;
	public boolean saveSegRule() {

		int num = dispsegmentsLbId.getItemCount();
		if(num == 0) {
			MessageUtil.setMessage(
					"Please create a segment first \n so that you can configure " +
							"it to your campaigns.","color:red", "TOP");
			return false;

		}

		int mlcount = dispsegmentsLbId.getSelectedCount();
		if (mlcount == 0) {
			MessageUtil.setMessage("Please select at least one segment.", "color:red","TOP");
			return false;
		}


		Set<Listitem> selRules = dispsegmentsLbId.getSelectedItems();
		String segRuleIds= "";
		String listIdsStr = "";
		for (Listitem listitem : selRules) {

			SegmentRules segmentRule = (SegmentRules)listitem.getValue();


			if(segRuleIds.length() > 0) {

				segRuleIds += ",";

			}//if
			segRuleIds += segmentRule.getSegRuleId().longValue();

			if(listIdsStr.length() > 0) listIdsStr+= ",";
			listIdsStr += segmentRule.getSegmentMlistIdsStr();

		}//for


		waCampaign.setListType("Segment"+Constants.DELIMETER_COLON+segRuleIds);

		//can avoid setting the set of mailing lists to the campaign
		Set<MailingList> mlSet = new HashSet<MailingList>();

		listnamesStr = "";


		List<MailingList> mlList = mailingListDao.findByIds(listIdsStr);

		if(mlList == null) {

			MessageUtil.setMessage("Configured segment's target list no longer exists. You might have deleted it.", "color:red;");
			configurelistRgId.setSelectedIndex(1);
			onCheck$configurelistRgId();
			return false;


		}

		for (MailingList mailingList : mlList) {

			mlSet.add(mailingList);
			if(listnamesStr == null) listnamesStr = mailingList.getListName();

			if(listnamesStr != null && listnamesStr.length() > 0) listnamesStr += ", ";
			listnamesStr += mailingList.getListName();

		}//for


		waCampaign.setMailingLists(mlSet);

		if(!otherCampSettings()) {
			return false;
		}

		return true;


	}

	public boolean otherCampSettings() {

		long waCount = 0;
		long totalCount = 0;
		Set<MailingList> mlSet = waCampaign.getMailingLists();
		totalCount = contactsDao.getAllMobileCount(mlSet, true);


		MessageUtil.clearMessage();
		String segmentStr = waCampaign.getListType();

		if(segmentStr.startsWith("Segment")) {

			String segRuleIds = segmentStr.split(""+Constants.DELIMETER_COLON)[1];
			List<SegmentRules> segmenRules = segmentRulesDao.findById(segRuleIds);

			if(segmenRules == null) {

				MessageUtil.setMessage("Configured segment no longer exists. You might have deleted it.", "color:red;");
				configurelistRgId.setSelectedIndex(1);
				onCheck$configurelistRgId();
				return false;


			}//if
			String tempQry = "";
			logger.debug("size ::"+segmenRules.size());
			for (SegmentRules segmentRules : segmenRules) {

				Set<MailingList> mlistSet = new HashSet<MailingList>();
				List<MailingList> mlList = mailingListDao.findByIds(segmentRules.getSegmentMlistIdsStr());
				if(mlList == null) {
					logger.debug("continue");
					continue;
				}

				mlistSet.addAll(mlList);
				long mlsbit = Utility.getMlsBit(mlistSet);
				segmentStr = SalesQueryGenerator.generateListSegmentCountQuery(segmentRules.getSegRule(),false, Constants.SEGMENT_ON_MOBILE, mlsbit);
				if(segmentStr == null) {
					MessageUtil.setMessage("Selected invalid segmentation rules.", "color:red", "TOP");
					continue;
				}
				if(SalesQueryGenerator.CheckForIsLatestCamapignIdsFlag(segmentRules.getSegRule())) {
					String csCampIds = SalesQueryGenerator.getCamapignIdsFroFirstToken(segmentRules.getSegRule());

					if(csCampIds != null ) {
						String crIDs = Constants.STRING_NILL;
						List<Object[]> campList = campaignsDao.findAllLatestSentCampaignsBySql(segmentRules.getUserId(), csCampIds);
						if(campList != null) {
							for (Object[] crArr : campList) {

								if(!crIDs.isEmpty()) crIDs += Constants.DELIMETER_COMMA;
								crIDs += ((Long)crArr[0]).longValue();

							}
						}

						segmentStr = segmentStr.replace(Constants.INTERACTION_CAMPAIGN_CRID_PH, ("AND cr_id in("+crIDs+")"));
					}
				}


				segmentStr = segmentStr.replace("<MOBILEOPTIN>", "");
				logger.info(" Generated Query :"+segmentStr);

				if(tempQry.length() > 0) tempQry += " UNION ";

				tempQry += segmentStr;




			}//for
			logger.debug(tempQry);

			waCount = contactsDao.getSegmentedContactsCount(tempQry);

			if(waCount == 0) {
				logger.info("Segment returned 0 unique contacts of "+ totalCount + " available contacts.");
			}

		}//if segment
		else {
			waCount = totalCount;
			if(totalCount == 0) {
			
			}
		}

		if(isEdit!=null) {
			if(isEdit.equalsIgnoreCase("view")) {
				waCampaign.setStatus("Draft");

				if(draftStatus != null && draftStatus.equals(Constants.WA_CAMP_DRAFT_STATUS_STEP_TWO)){
					waCampaign.setDraftStatus(Constants.WA_CAMP_DRAFT_STATUS_STEP_THREE);
				}
				//

			}
			else if(isEdit.equalsIgnoreCase("edit")){
				waCampaign.setModifiedDate(Calendar.getInstance());
			}
		}
		else {
			waCampaign.setStatus("Draft");
			// campaign.setDraftStatus("CampLayout");

			if(draftStatus != null && draftStatus.equals(Constants.WA_CAMP_DRAFT_STATUS_STEP_TWO)) {
				waCampaign.setDraftStatus(Constants.WA_CAMP_DRAFT_STATUS_STEP_THREE);
			}

		}


		try {
			int confirm = Messagebox.show("Total "+waCount+" unique contacts have been configured. Do you want to continue?", "Confirm", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
			if(confirm != 1){
				return false;
			}
		} catch (Exception e1) {
			logger.error("Exception ::",e1);
			return false;
		}
		logger.info("WACampaignsDaoForDML :"+WACampaignsDaoForDML);
		logger.info("WACampaign :"+waCampaign);
		WACampaignsDaoForDML.saveOrUpdate(waCampaign);
		return true;


	}

	public Div getVisibleDiv() {

		Div div = null;
		for(Div viblediv : divList) {

			if(viblediv.isVisible()) {

				return viblediv;

			}//if

		}//for
		return null;
	}

	public A getCurrAnch() {
		//Div div = null;
		for(A currAnch : anchList) {

			if(currAnch.getSclass().equals("req_step_current")) {

				return currAnch;

			}//if

		}//for
		return null;
	}

	public void onSelect$dispsegmentsLbId() {

		Set<Listitem> selRules = dispsegmentsLbId.getSelectedItems();
		String listIdsStr = "";
		String dispSegRule = "";
		listnamesStr = "";

		if(selRules.size() == 0 ) {

			selRuleLblId.setValue(dispSegRule);
			selRuleListLblId.setValue(listnamesStr);
			return;
		}

		for (Listitem listitem : selRules) {

			SegmentRules segmentRule = (SegmentRules)listitem.getValue();


			if(listIdsStr.length() > 0) listIdsStr += ",";
			listIdsStr += segmentRule.getSegmentMlistIdsStr();

			String segRule = segmentRule.getSegRule();
			if(segRule != null) {

				if(dispSegRule.length() > 0) dispSegRule += "& \n";
				dispSegRule += dispRule(segRule);

			}


		}//for

		selRuleLblId.setValue(dispSegRule);
		List<MailingList> mlList = mailingListDao.findByIds(listIdsStr);

		if(mlList == null) {

			MessageUtil.setMessage("Configured segment's target list no longer exists. You might have deleted it.", "color:red;");
			configurelistRgId.setSelectedIndex(1);
			onCheck$configurelistRgId();
			return ;


		}

		for (MailingList mailingList : mlList) {

			if(listnamesStr == null) listnamesStr = mailingList.getListName();

			if(listnamesStr != null && listnamesStr.length() > 0) listnamesStr += ", ";
			listnamesStr += mailingList.getListName();

		}//for



		selRuleListLblId.setValue(listnamesStr);

		if(selRuleLblId.getValue().length() != 0) {
			dispRuleDivId.setVisible(true);
		}else{
			dispRuleDivId.setVisible(false);
		}

	}//onSelect$dispsegmentsLbId()


	public String dispRule(String rule) {
		String dispRule = "";
		String option=null;
		String campaignId = null;
		String campName = "";
		if(rule != null) {


			String[] rowsArr = rule.split("\\|\\|");
			String[] columnsArr; 


			columnsArr = rowsArr[0].split(":");
			if(columnsArr.length > 0) {

				if(columnsArr[0].trim().equalsIgnoreCase("Any") ) { 
					option = "OR";
				} 
				else {
					option = "AND";
				}

				if(columnsArr.length > 2) {

					campaignId = columnsArr[2];
					if(campaignId != null && !campaignId.isEmpty()) {

						List<Campaigns> campLst = campaignsDao.getCampaignById((campaignId));
						if(campLst != null) { 
							for (Campaigns campaigns : campLst) {

								if(!campName.isEmpty()) campName += ", ";
								campName +=  (campaigns != null ? campaigns.getCampaignName() : "");
							}
						}
					}

				}//if

			}//if


			String[] tempStrArr = null;
			String fieldNameStr = null;
			String itemStr = null;
			String dataTypeStr = null;
			String constraintStr = null;
			String data1 = null;
			String data2 = null;
			String data = "";
			String[] tokenArr = null;

			for(int i=1;i<rowsArr.length;i++) {

				tokenArr = rowsArr[i].split("<OR>");
				String innerRule = "";
				for (String token : tokenArr) {

					columnsArr = token.split("\\|");
					if(innerRule.length()>0) innerRule += " "+"OR"+" ";

					itemStr = columnsArr[0].trim();
					fieldNameStr = columnsArr[1].trim();
					tempStrArr = columnsArr[2].trim().split(":");
					dataTypeStr = tempStrArr[0].toUpperCase().trim();
					constraintStr = tempStrArr[1];

					data = data1 = (columnsArr.length>3)?columnsArr[3]:"";


					logger.debug("fieldNameStr :"+fieldNameStr);
					logger.debug("dataTypeStr :"+dataTypeStr);
					logger.debug("constraintStr :"+constraintStr);
					logger.debug("data1 :"+data1);
					logger.debug("data2 :"+data2);

					if(itemStr.equalsIgnoreCase(SegmentEnum.INTERACTION_CLICKS.getItem()) 
							|| itemStr.equalsIgnoreCase(SegmentEnum.INTERACTION_OPENS.getItem()) ) {

						SegmentEnum retEnum = SegmentEnum.getEnumByColumn(fieldNameStr);

						if(retEnum != null) {

							fieldNameStr = retEnum.getParentEnum().getDispLabel();

							constraintStr = retEnum.getDispLabel() +  " IN Campaign(s): "+campName;
							data = "";
						}//if

					}
					else{

						data2 = (columnsArr.length>4)?columnsArr[4]:"";
						if(data2 != null ){
							data = data1+" , "+data2;
						}
					}


					innerRule += "("+fieldNameStr+" "+constraintStr+" "+data+")";


				}//for 


				if(dispRule.length()>0) dispRule += " "+option+" ";
				dispRule += "("+innerRule+")";

			} // outer for

		}

		return dispRule;

	}

	private Label selMlLblId, selRuleListLblId, selRuleLblId;
	public void onSelect$dispMlListsLBoxId() throws Exception {

		logger.debug("-- just entered --");

		Set<Listitem> selectedItems = dispMlListsLBoxId.getSelectedItems();

		String listNamesStr="";

		for (Listitem li : selectedItems) {

			MailingList mailingList = (MailingList) li.getValue();			
			if(listNamesStr.length() != 0) listNamesStr+=",";
			listNamesStr += mailingList.getListName() ;
		}

		selMlLblId.setValue(listNamesStr);

		//visibility div of Selected list
		if(selMlLblId.getValue().length() != 0) {
			selectedListDivId.setVisible(true);
		}else{
			selectedListDivId.setVisible(false);
		}

	}
	private Window viewSegRuleWinId;
	private Label viewSegRuleWinId$segRuleLblId;

	private boolean areTheseSchedulesEqual(WACampaignsSchedule WACampSchedule1, WACampaignsSchedule WACampSchedule2){

		try{
			boolean scheduledDateChek = false;

			boolean criteriaChek = false;

			boolean parentIdChek = false;

			boolean resendLevelChek = false;

			boolean WACampaignIdChek = false;

			boolean statusChek = false;


			if( WACampSchedule1.getScheduledDate() == null &&  WACampSchedule1.getScheduledDate() == null){
				scheduledDateChek = true;
			}else if(WACampSchedule1.getScheduledDate() != null &&  WACampSchedule1.getScheduledDate() != null){
				scheduledDateChek = WACampSchedule1.getScheduledDate().equals(WACampSchedule2.getScheduledDate());
			}else {
				scheduledDateChek = false;
			}

			if( WACampSchedule1.getwaCampaignId() == null &&  WACampSchedule2.getwaCampaignId() == null){
				WACampaignIdChek = true;
			}else if(WACampSchedule1.getwaCampaignId() != null &&  WACampSchedule2.getwaCampaignId() != null){
				WACampaignIdChek = WACampSchedule1.getwaCampaignId().equals(WACampSchedule2.getwaCampaignId());
			}else {
				WACampaignIdChek = false;
			}


			//primitive data type byte
			statusChek = WACampSchedule1.getStatus() == WACampSchedule2.getStatus();


			if(scheduledDateChek && criteriaChek && parentIdChek && resendLevelChek && WACampaignIdChek && statusChek){
				return true;
			}else{
				return false;
			}

		}catch(Exception e){
			logger.info("exception>>>>>>>>>>>>>>>"+e.getStackTrace());
		}

		return false;
	}

	/**
	 * removes the schedules from the DB
	 * @param WACampSchedule
	 */
	private void removeCampScheduleFromList(WACampaignsSchedule campSchedule) {

		try {	
			resendOptionWinId$errMsgLblId.setValue("");
			/**
			 * Delete the schedule
			 */
			WACampScheduleList.remove(campSchedule);

			tempList.remove(campSchedule.getScheduledDate());

			if(schedGrdRowsId != null){
				Row row = null;
				List<Component> schedGrdRowsIdChildrenList = schedGrdRowsId.getChildren();
				if(schedGrdRowsIdChildrenList != null && schedGrdRowsIdChildrenList.size() > 0){
					for(Component aChild : schedGrdRowsIdChildrenList){
						row = (Row)aChild;
						if(row != null){

							if((row.getValue() != null) && areTheseSchedulesEqual((WACampaignsSchedule)(row.getValue()), campSchedule)){
								logger.info("Got to delete>>>>>>>>>>>>>>>>>");
								schedGrdRowsId.removeChild(aChild);
								break;
							}
						}
					}
				}
			}




			WACampaignsSchedule tempCS;


			for (Iterator<WACampaignsSchedule> iterator = tempWACampScheduleList.listIterator();
					iterator.hasNext();) {
				tempCS = iterator.next();

				if((campSchedule.getwaCsId() != null)) {
					if(tempList.contains(tempCS.getScheduledDate())) {
						tempList.remove(tempCS.getScheduledDate());

					}
					if(tempCS.getwaCsId() != null) {
						tempWACampScheduleList.add(tempCS);
					}
					removeCampScheduleFromList(tempCS);
				}//if


			}//for

		} 
		catch (Exception e) {
			logger.error("Exception while removing the waCampaign schedules",e);
		}

	}// removeCampScheduleFromList()


	private Label optOutLblId;

	public void onBlur$mblNoTxtBoxId() throws Exception {

		String mobNum=mblNoTxtBoxId.getValue();
		if(mobNum.equals("Enter Mobile Number(s)...") || mobNum.equals("")){
			mblNoTxtBoxId.setValue("Enter Mobile Number(s)...");

		}
	}


	public void onBlur$testWATbId() throws Exception {

		String mobNum=testWATbId.getValue();
		if(mobNum.equals("Enter Mobile Number(s)...") || mobNum.equals("")) {
			testWATbId.setValue("Enter Mobile Number(s)...");

		}
	}

	public static Set<String> findCoupPlaceholders(String content) {
		content = content.replace("|^", "[").replace("^|", "]");
		String cfpattern = "\\[([^\\[]*?)\\]";

		Pattern r = Pattern.compile(cfpattern,Pattern.CASE_INSENSITIVE);
		Matcher m = r.matcher(content);
		String ph = null;
		Set<String> totalPhSet = new HashSet<String>();

		try {
			while(m.find()) {

				ph = m.group(1);

				if(ph.startsWith("CC_")) {
					totalPhSet.add(ph);
				}
			}

		}catch (Exception e) {
			// TODO: handle exception
			return totalPhSet;
		}

		return totalPhSet;
	}
	/*********** Added for campaign categories**********/

	private Image helpImgId;
	private Popup help2;

	/********************Ended*******************/
	public void onClick$addNewTempTBId() {
		logger.debug("just click addNewTempTBId >>> ");
		createWaTempWinId.setTitle("Create New WA Template");
		createWaTempWinId$waTempNameTxtBxId.setValue("");
		createWaTempWinId$waTempNameTxtBxId.setStyle(NORMAL_STYLE);
		createWaTempWinId$waTempContentTxtBxId.setValue("");
		createWaTempWinId$waTempContentTxtBxId.setStyle(NORMAL_STYLE);
//		createWaTempWinId$waTempCharCountTxtBxId.setValue("");
//    	createWaTempWinId$waTempCharCountTxtBxId.setStyle(NORMAL_STYLE);
		createWaTempWinId$waTempEditId.setValue(Constants.STRING_NILL);
		createWaTempWinId.doHighlighted();

	}

	public void onClick$editNewTempTBId() {
		logger.debug("just click editNewTempTBId >>> ");
		createWaTempWinId.setTitle("Edit WA Template");
		createWaTempWinId$waTempNameTxtBxId.setValue("");
		createWaTempWinId$waTempNameTxtBxId.setStyle(NORMAL_STYLE);
		createWaTempWinId$waTempContentTxtBxId.setValue("");
		createWaTempWinId$waTempContentTxtBxId.setStyle(NORMAL_STYLE);
		createWaTempWinId$waTempEditId.setValue("");
		if(waTempCmbBxId.getSelectedIndex() == 0) {
			return;
		}else{
			WATemplates waTempObj = (WATemplates)waTempCmbBxId.getSelectedItem().getValue();
			if(waTempObj!=null) {
				createWaTempWinId$waTempNameTxtBxId.setValue(waTempObj.getTemplateName());
				createWaTempWinId$waTempEditId.setValue(waTempObj.getTemplateId().toString());
				createWaTempWinId$waTempContentTxtBxId.setValue(waTempObj.getJsonContent());
			}
		}

		createWaTempWinId$waTempCharCountTxtBxId.setStyle(NORMAL_STYLE);
		createWaTempWinId.doHighlighted();
	}


	public void onClick$waTempCancelBtnId$createWaTempWinId() {
		logger.debug("just click onClick$waTempCancelBtnId$createWaTempWinId >>> ");
		createWaTempWinId$waTempNameTxtBxId.setValue("");
		createWaTempWinId$waTempNameTxtBxId.setStyle(NORMAL_STYLE);
		createWaTempWinId$waTempContentTxtBxId.setValue("");
		createWaTempWinId$waTempContentTxtBxId.setStyle(NORMAL_STYLE);
	//	createWaTempWinId$waTempCharCountTxtBxId.setValue("");
	//	createWaTempWinId$waTempCharCountTxtBxId.setStyle(NORMAL_STYLE);
		createWaTempWinId.setVisible(false);

	}// onClick$waTempCancelBtnId$createwaTempWinId

	public void onClick$waTempSaveBtnId$createWaTempWinId() {
		logger.debug("just click onClick$waTempSaveBtnId$createWaTempWinId >>> ");

		createWaTempWinId$waTempNameTxtBxId.setStyle(NORMAL_STYLE);
		createWaTempWinId$waTempContentTxtBxId.setStyle(NORMAL_STYLE);
		String templateName = createWaTempWinId$waTempNameTxtBxId.getValue().trim();
		String contentStr = createWaTempWinId$waTempContentTxtBxId.getValue().trim();
		if(templateName.trim().length() == 0) {
			createWaTempWinId$waTempNameTxtBxId.setStyle(ERROR_STYLE);
			MessageUtil.setMessage("Please provide Template Name.", "color:red", "TOP");
			return;
		}else if(contentStr.trim().length() == 0) {
			createWaTempWinId$waTempContentTxtBxId.setStyle(ERROR_STYLE);

			MessageUtil.setMessage("Please provide Template Content.", "color:red", "TOP");
			return;
		}

		logger.debug("WATemplatesDao value is ::"+WATemplatesDao);

		WATemplates WATempObj = null;
		boolean setIsEdited = false;
		if(createWaTempWinId$waTempEditId!=null && createWaTempWinId$waTempEditId.getValue() !=null && !createWaTempWinId$waTempEditId.getValue().isEmpty()) {
			WATempObj = WATemplatesDao.find(Long.parseLong(createWaTempWinId$waTempEditId.getValue()));
			setIsEdited = true;
			if(!WATempObj.getTemplateName().equals(createWaTempWinId$waTempNameTxtBxId.getValue())) {
				WATempObj = new WATemplates();
			}
		}else {
			boolean isExistFlag = WATemplatesDao.isTemplateNameExistByUserId(currUser.getUserId(),templateName);

			if(isExistFlag){
				MessageUtil.setMessage("Template already exists with the given name", "color:red", "TOP");
				createWaTempWinId$waTempNameTxtBxId.setStyle(ERROR_STYLE);
				return;
			}
			WATempObj = new WATemplates();
		}
		WATempObj.setStatus("0");
		WATempObj.setTemplateName(templateName);
		WATempObj.setUserId(currUser.getUserId());
		WATempObj.setOrgId(currUser.getUserOrganization().getUserOrgId());
		WATempObj.setJsonContent(contentStr);
		WATemplatesDaoForDML.saveOrUpdate(WATempObj);



		Messagebox.show("An email has been sent to Optculture Admin for template approval. "
				+ "Once approved, \n the template will be automatically activated into your account for use. ", "info", Messagebox.OK, Messagebox.INFORMATION); 
		MessageUtil.setMessage("Template saved successfully.", "color:green;");


		String toEmailId = PropertyUtil.getPropertyValueFromDB(Constants.PROPS_KEY_SUPPORT_EMAILID);

		String templateTypeStr = PropertyUtil.getPropertyValueFromDB("WATemplate");
		//APP-2378
		String currUserName = currUser.getUserName();

		if (templateTypeStr != null) {
			templateTypeStr = templateTypeStr.replace("[Template content]", contentStr).replace("[Username]", 
					Utility.getOnlyUserName(currUserName)).replace("[orgID]", Utility.getOnlyOrgId(currUserName));
		}else templateTypeStr = contentStr ;
		logger.info("WA template content : "+ contentStr);


		onClick$waTempCancelBtnId$createWaTempWinId();

		if(setIsEdited = true) {
			WAMsgTbId.setValue(Constants.STRING_NILL);
			setIsEdited = false;
		}else {
			Comboitem comboItemObj = new Comboitem(templateName);
			comboItemObj.setDescription("Pending");
			comboItemObj.setValue(WATempObj);
			comboItemObj.setParent(waTempCmbBxId);
		}


	}// onClick$waTempCancelBtnId$createwaTempWinId

	public void onSelect$waTempCmbBxId(){
		logger.debug("Templates selection started"+waTempCmbBxId.getSelectedItem().getValue());
		if(waTempCmbBxId.getSelectedIndex() == 0) {
			WAMsgTbId.setValue("");
			getCharCount("");
			//WAMsgTbId.setReadonly(false);
		}else{
			WATemplates WATempObj = (WATemplates)waTempCmbBxId.getSelectedItem().getValue();
			WAMsgTbId.setValue(WATempObj.getJsonContent());
			WAMsgTbId.setAttribute("templateRegisteredId", WATempObj.getTemplateRegisteredId());
			getCharCount(WATempObj.getJsonContent());
		}
	}

  private void WaTempSettings() {

		try {
			Components.removeAllChildren(waTempCmbBxId);

			//set the WATemplates
			List<WATemplates>  templateList = WATemplatesDao.findTemplatesByOrgId(GetUser.getUserObj().getUserOrganization().getUserOrgId());
			Comboitem combItem = null;
			combItem = new Comboitem("--select--");
			combItem.setParent(waTempCmbBxId);
//			logger.info("listSize :"+templateList.size());

			if(templateList != null && templateList.size() >0) {

				for (WATemplates eachObj : templateList) {
					combItem = new Comboitem(eachObj.getTemplateName());
					combItem.setValue(eachObj);
					combItem.setParent(waTempCmbBxId);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("exception",e);
		}
	}


	//createwaTempWinId$waCaretPosTB
	public void onChange$waCaretPosTB$createWaTempWinId(){
		logger.debug("-----just entered:123 onChange event----");
	}

	public void onBlur$waCaretPosTB$createWaTempWinId() {

		logger.debug("-----just entered:123 onBlur event----");

	}
	
	public boolean invalidPromoCodes() {
		boolean isValid = false;

		String messageContent = waCampaign.getMessageContent();

		Set<String> couponPhSet = findCoupPlaceholders(messageContent.trim());
		logger.debug("couponPhSet :"+couponPhSet);
		String couponIdStr = "";
		for(String ph : couponPhSet){

			if(ph.startsWith("CC_")){

				String[] phStr = ph.split("_");
				if(phStr.length > 2){

					MessageUtil.setMessage("Invalid Placeholder: "+ph, "color:red;", "top");
					return true;
				}

				couponIdStr += couponIdStr.trim().isEmpty() ? phStr[1].trim() : ","+phStr[1].trim();
				Long couponId = null;
				try{
					couponId = Long.parseLong(phStr[1]);
				}catch(Exception e){
					MessageUtil.setMessage("Invalid Placeholder: "+ph, "color:red;", "top");
					return true;
				}

				if(couponId != null){

					CouponsDao couponsDao= (CouponsDao)SpringUtil.getBean("couponsDao");
					Long orgId = currUser.getUserOrganization().getUserOrgId();

					List<Coupons> couponsList = couponsDao.findCouponsByCoupIdsAndOrgId(""+couponId, orgId);
					if(couponsList == null){
						MessageUtil.setMessage("Invalid Placeholder: "+ph, "color:red;", "top");
						return true;
					}
				}
			}

		}

		if(!couponIdStr.trim().isEmpty()){
			logger.debug("couponIdStr :"+couponIdStr);
			List<Coupons> inValidCoupList = couponsDao.isExpireOrPauseCoupList(couponIdStr, currUser.getUserOrganization().getUserOrgId());

			if(inValidCoupList != null){

				String inValidCoupNames = "";
				if(inValidCoupList != null && inValidCoupList.size() >0) {

					for (Coupons coupons : inValidCoupList) {
						inValidCoupNames += inValidCoupNames.trim().length() >0 ? ","+coupons.getCouponName() : coupons.getCouponName();
					}
					MessageUtil.setMessage(	"The Discount Code "+inValidCoupNames+" used in this campaign has either expired or in paused status. " +
							" \n Please change the status of this Discount Code.",
							"color:red", "TOP");
					return true;
				}

			}

		}

		//By Lavanya

		if(!couponIdStr.isEmpty()){

			List<Coupons> coupList = couponsDao.findCouponsByCoupIdsAndOrgId(couponIdStr,currUser.getUserOrganization().getUserOrgId());

			String segmentStr = waCampaign.getListType();

			Set <MailingList> mlset = waCampaign.getMailingLists();

			if(segmentStr != null && mlset == null || mlset.size() == 0){

				MessageUtil.setMessage("Configured segment no longer exists. You might have deleted it.", "color:red;");
				return true;
			}
			else if(segmentStr == null && mlset == null || mlset.size() == 0)
			{
				MessageUtil.setMessage("WA campaign is not configured to any other mailing list."
						+ "\n Please configure at least one mailing list.", "color:red;");
				return true;
			}

			Long WAcount =getWACount(segmentStr ,mlset);
			logger.info("################## WAcount : "+WAcount);
			
			if(coupList != null && coupList.size() >0){

				for (Coupons eachPromoObj : coupList) {
					logger.debug("eachPromoObj.getAvailable() is  ::"+eachPromoObj.getAvailable());

					if(!eachPromoObj.getAutoIncrCheck()){
						long count = couponCodesDao.findIssuedCoupCodeByCoup(eachPromoObj.getCouponId());
						logger.debug("count is "+count);
						long availCCount = eachPromoObj.getTotalQty() - count;
						logger.debug("availCCount is ************ "+availCCount);
						if(availCCount < WAcount) {
							MessageUtil.setMessage("WA cannot be sent as configured  "+eachPromoObj.getCouponName()+" available Discount Codes count" +
									" \n exceeds the WA send limit.Please  increase Discount Codes issued limit.", "color:red;");
							return true;
						}
					}



				}

			}

		}


		return isValid;
	}

	//By Lavanya
	public Long getWACount(String segmentStr, Set <MailingList> mlset){
		long WACount=0;
		long totalCount = contactsDao.getAllWACount(mlset);
		if(segmentStr.startsWith("Segment")) {

			String segRuleIds = segmentStr.split(""+Constants.DELIMETER_COLON)[1];
			List<SegmentRules> segmenRules = segmentRulesDao.findById(segRuleIds);

			if(segmenRules == null) {

				MessageUtil.setMessage("Configured segment no longer exists. You might have deleted it.", "color:red;");

				return null;


			}//if
			String tempQry = "";
			logger.debug("size ::"+segmenRules.size());
			for (SegmentRules segmentRules : segmenRules) {

				Set<MailingList> mlistSet = new HashSet<MailingList>();
				List<MailingList> mlList = mailingListDao.findByIds(segmentRules.getSegmentMlistIdsStr());
				if(mlList == null) {
					logger.debug("continue");
					continue;
				}

				mlistSet.addAll(mlList);
				long mlsbit = Utility.getMlsBit(mlistSet);

				segmentStr = SalesQueryGenerator.generateListSegmentCountQuery(segmentRules.getSegRule(),false, Constants.SEGMENT_ON_MOBILE,mlsbit);
				if(segmentStr == null) {
					MessageUtil.setMessage("Selected invalid segmentation rules.", "color:red", "TOP");
					continue;
				}
				if(SalesQueryGenerator.CheckForIsLatestCamapignIdsFlag(segmentRules.getSegRule())) {
					String csCampIds = SalesQueryGenerator.getCamapignIdsFroFirstToken(segmentRules.getSegRule());

					if(csCampIds != null ) {
						String crIDs = Constants.STRING_NILL;
						List<Object[]> campList = campaignsDao.findAllLatestSentCampaignsBySql(segmentRules.getUserId(), csCampIds);
						if(campList != null) {
							for (Object[] crArr : campList) {

								if(!crIDs.isEmpty()) crIDs += Constants.DELIMETER_COMMA;
								crIDs += ((Long)crArr[0]).longValue();

							}
						}

						segmentStr = segmentStr.replace(Constants.INTERACTION_CAMPAIGN_CRID_PH, ("AND cr_id in("+crIDs+")"));
					}
				}


				if(tempQry.length() > 0) tempQry += " UNION ";

				tempQry += segmentStr;



			}//for


			WACount = contactsDao.getSegmentedContactsCount(tempQry);



			if(WACount == 0) {

				MessageUtil.setMessage( "Your segment returned 0 active unique contacts of "+ totalCount + " available contacts.","color:red", "TOP");
				return null;
			}


		}//if segment
		else {
			WACount = contactsDao.getUniqueMobileCount(mlset,"Active");


			if(WACount == 0) {
				MessageUtil.setMessage("Your selection returned 0 active unique contacts of "+ totalCount + " available contacts. ", "color:red", "TOP");
				return null;
			}
		}


		return WACount;
	}

	//Added for 2.4.6
	private Window viewAllArchivedSchedulesWinId;
	private Listbox viewAllArchivedSchedulesWinId$campListlbId;
	private Button submitBtnId$viewAllArchivedSchedulesWinId;
	private A viewAllArchivedSchedAnchId;
	private A viewAllArchivedSchedulesWinId$viewMoreArchievedSchedAnchId;
	private Listbox sentCampaingsListlbId;
	private Div campaignSentDivId;
	private Label viewAllArchivedSchedulesWinId$noRecordsArchivedLbId,campaignSentLbId;
	private Listbox activeCampaingsListlbId;
	private Div campaignActiveTillDivId;
	private Label campActiveTillDateLbId,numOfTimeCampActiveLbId;
	private A viewAllActiveSchedAnchId;
	private Window viewAllActiveSchedulesWinId;
	private Bandbox viewAllActiveSchedulesWinId$campActionsBandBoxId;
	private Listbox viewAllActiveSchedulesWinId$campListlbId;
	private Button submitBtnId$viewAllActiveSchedulesWinId;
	private Bandbox campActionsBandBoxId$viewAllActiveSchedulesWinId;
	private Label viewAllActiveSchedulesWinId$bulkDeleteLbId;	
	private Label viewAllActiveSchedulesWinId$noRecordsActiveLbId ;
	private A viewAllActiveSchedulesWinId$viewMoreActiveSchedAnchId;
	private int activeSchCount = 100;
	private int sentSchCount = 100;
	private boolean persistentCamp = true;
	/**
	 * createRowUpComingListBox
	 * @param startDtCS
	 * @param count
	 * @param loadDBSch
	 * @return
	 */
	private boolean createRowUpComingListBox(WACampaignsSchedule startDtCS,int count, boolean loadDBSch){


		logger.debug(">>>>>>> Started  createRowUpComingListBox :: ");
		try {
			//			logger.debug(">>>>>>> Started  createRowUpComingListBox :: ");			

			if(startDtCS.getScheduledDate().after(currUser.getPackageExpiryDate())) {
				MessageUtil.setMessage("Schedule date cannot be after your package expiry date.", "color:red", "TOP");
				return false;
			}

			Listitem li = new Listitem();
			Listcell lc = new Listcell();

			if(0 == startDtCS.getStatus() || 2 == startDtCS.getStatus()){

				//Schedule Date
				li.setValue(startDtCS);
				lc.setParent(li);
				lc = new Listcell();
				lc.setLabel(MyCalendar.calendarToString(startDtCS.getScheduledDate(),MyCalendar.FORMAT_DATE_FORMAT,(TimeZone) sessionScope.get("clientTimeZone")));
				lc.setParent(li);

				//Status
				lc = new Listcell();
				lc.setLabel(startDtCS.getStatusStr());
				lc.setParent(li);

				//Delete
				lc = new Listcell();
				Hbox hbox = new Hbox();
				Image delImg = new Image("/img/action_delete.gif");
				delImg.setTooltiptext("Delete");
				delImg.setStyle("cursor:pointer;");
				delImg.setAttribute("delete", "campScheduleDelete");
				delImg.addEventListener("onClick", new MyListener());
				delImg.setParent(hbox);

				hbox.setParent(lc);	
				lc.setParent(li);

				li.setHeight("30px");


				if(count == 0){
					li.setSelected(true);
					li.setParent(activeCampaingsListlbId);
					logger.info("setting first element");
				}
				else{
					logger.info("Set window as parent or hide");

					try{
						WACampaignsSchedule campSchedChkDate =   (WACampaignsSchedule) ((Listitem)activeCampaingsListlbId.getSelectedItem()).getValue();

						logger.info("When list is not null ::"+MyCalendar.calendarToString(campSchedChkDate.getScheduledDate(),MyCalendar.FORMAT_DATE_FORMAT));
						logger.info("campaignSchedule ::"+MyCalendar.calendarToString(startDtCS.getScheduledDate(),MyCalendar.FORMAT_DATE_FORMAT));
						if(startDtCS.getScheduledDate().before(campSchedChkDate.getScheduledDate())){
							logger.debug("true");


							int count1 =  activeCampaingsListlbId.getItemCount();
							for(; count1>0; count1--) {
								activeCampaingsListlbId.removeItemAt(count-1);
							}

							if(0 == startDtCS.getStatus() || 2 == startDtCS.getStatus()){
								li = new Listitem();
								li.setSelected(true);
								//Schedule Date
								li.setValue(startDtCS);
								lc.setParent(li);
								lc = new Listcell();
								lc.setLabel(MyCalendar.calendarToString(startDtCS.getScheduledDate(),MyCalendar.FORMAT_DATE_FORMAT,(TimeZone) sessionScope.get("clientTimeZone")));
								//logger.info(MyCalendar.calendarToString(campaignSchedule.getScheduledDate(), MyCalendar.FORMAT_DATE_FORMAT,(TimeZone) sessionScope.get("clientTimeZone")));
								lc.setParent(li);

								//Status
								lc = new Listcell();
								lc.setLabel(startDtCS.getStatusStr());
								lc.setParent(li);

								//Delete
								lc = new Listcell();
								hbox = new Hbox();
								delImg = new Image("/img/action_delete.gif");
								delImg.setTooltiptext("Delete");
								delImg.setStyle("cursor:pointer;");
								delImg.setAttribute("delete", "campScheduleDelete");
								delImg.addEventListener("onClick", new MyListener());
								delImg.setParent(hbox);

								hbox.setParent(lc);	
								lc.setParent(li);

								li.setHeight("30px");


								li.setParent(activeCampaingsListlbId);

								li.setParent(activeCampaingsListlbId);

								logger.info("Set window as parent or hide");
							}
						}
					}
					catch(Exception e){
						logger.info("Exception ::",e);
					}
				}
			}
			logger.debug("<<<<< Completed createRowUpComingListBox .");
			return true;
		} catch (Exception e) {
			logger.error("Exception ..........:",e);
			logger.debug("<<<<< Completed createRowUpComingListBox .");
			return false;
		}

	}//createRowUpComingListBox


	/**
	 * This method return's active or draft campScheuleList.
	 * @param wACampScheduleList2
	 * @return activeOrDraftList
	 */
	private List<WACampaignsSchedule> getActiveCampScheduleList(List<WACampaignsSchedule> wACampScheduleList2) {
		List<WACampaignsSchedule> activeOrDraftList = new ArrayList<WACampaignsSchedule>();
		for (WACampaignsSchedule campaignSchedule : wACampScheduleList2) {
			if(campaignSchedule.getStatus() == 0  || campaignSchedule.getStatus() == 2 ) {
				activeOrDraftList.add(campaignSchedule);
			}
		}
		return activeOrDraftList;
	}

	/**
	 * This method returns size for active or draft campaign's.
	 * @param wACampScheduleList2
	 * @return size
	 */
	private String getSize(List<WACampaignsSchedule> wACampScheduleList2) {
		int activeCount = 0;
		for (WACampaignsSchedule campaignSchedule : wACampScheduleList2) {
			if(campaignSchedule.getStatus() == 0  || campaignSchedule.getStatus() == 2 ) {
				activeCount++;
			}
		}
		return activeCount+"";
	}//getSize


	/**
	 * This method handles on click on viewAllActiveSchedAnchId
	 */
	public void onClick$viewAllActiveSchedAnchId(){
		logger.debug(">>>>>>> Started  onClick$viewAllActiveSchedAnchId :: ");
		try {
			viewAllActiveSchedulesWinId.setVisible(true);
			viewAllActiveSchedulesWinId.doHighlighted();
			viewAllActiveSchedulesWinId.setVisible(true);
			viewAllActiveSchedulesWinId$bulkDeleteLbId.setVisible(false);
			viewAllActiveSchedulesWinId$campActionsBandBoxId.setDisabled(true);
			int count =  viewAllActiveSchedulesWinId$campListlbId.getItemCount();
			for(; count>0; count--) {
				viewAllActiveSchedulesWinId$campListlbId.removeItemAt(count-1);
			}
			noOfTimeRedraw = 0;
			activeSchCount = 100;
			redraw(0,activeSchCount);
			viewAllActiveSchedulesWinId.setStyle("scroll:auto;");
		} catch (Exception e) {
			logger.error("Exception ",e);
		}
		logger.debug("<<<<< Completed onClick$viewAllActiveSchedAnchId .");
	}//onClick$viewAllActiveSchedAnchId

	/**
	 * onClick$submitBtnId$viewAllActiveSchedulesWinId
	 */
	public void onClick$submitBtnId$viewAllActiveSchedulesWinId(){
		logger.debug(">>>>>>> Started  onClick$submitBtnId$viewAllActiveSchedulesWinId :: ");
		int count =  viewAllActiveSchedulesWinId$campListlbId.getItemCount();
		for(; count>0; count--) {
			viewAllActiveSchedulesWinId$campListlbId.removeItemAt(count-1);
		}
		viewAllActiveSchedulesWinId.setClosable(false);
		viewAllActiveSchedulesWinId.setVisible(false);
		viewAllActiveSchedulesWinId$bulkDeleteLbId.setVisible(false);
		persistentCamp = true;
		noOfTimeRedraw = 0; 

		logger.debug("<<<<< Completed onClick$submitBtnId$viewAllActiveSchedulesWinId .");
	}//onClick$submitBtnId$viewAllActiveSchedulesWinId


	/**
	 * This method helps to redraw and populate email campaigns.
	 * @param startIndex
	 * @param size
	 */
	int noOfTimeRedraw=0;
	boolean reverseFlag = true;
	List<WACampaignsSchedule> campaignScheduleList = null;
	public void redraw(int startIndex, int size) {
		logger.debug(">>>>>>> Started  redraw :: ");
		logger.info("Redraw form "+startIndex +"--"+size);
		try {
			MessageUtil.clearMessage();
			viewAllActiveSchedulesWinId$viewMoreActiveSchedAnchId.setVisible(true);
			campaignScheduleList  = getActiveCampScheduleList(WACampScheduleList);


			if(campaignScheduleList == null || campaignScheduleList.size()<=0){
				logger.info("NO more campaigns");
				viewAllActiveSchedulesWinId$noRecordsActiveLbId.setVisible(true);
				viewAllActiveSchedulesWinId$viewMoreActiveSchedAnchId.setVisible(false);
				return;
			}

			Collections.sort(campaignScheduleList);


			if(campaignScheduleList != null && campaignScheduleList.size() >0){
				logger.info("*****"+campaignScheduleList.size());
					if(startIndex == 0){
					noOfTimeRedraw ++;
					Listitem li;
					Listcell lc;
					int index =0;
					//for (int i = 0; i <= size; i++) { // 0 -100
					for (int i = 0; i < campaignScheduleList.size(); i++) { 
						//logger.info("Started loop"+i);
						if(i == 101){
							logger.info(i+"..Camp list reach size break......"+size);
							break;
						}
						else{
							index=i;
							li = new Listitem();
							lc = new Listcell();
							viewAllActiveSchedulesWinId$noRecordsActiveLbId.setVisible(false);
							li.setValue(campaignScheduleList.get(i));
							lc.setParent(li);
							lc = new Listcell();
							logger.info(MyCalendar.calendarToString(campaignScheduleList.get(i).getScheduledDate(), MyCalendar.FORMAT_DATE_FORMAT, (TimeZone) sessionScope.get("clientTimeZone")));
							lc.setLabel(MyCalendar.calendarToString(campaignScheduleList.get(i).getScheduledDate(), MyCalendar.FORMAT_DATE_FORMAT, (TimeZone) sessionScope.get("clientTimeZone")));

							lc.setParent(li);

							//Status
							lc = new Listcell();
							lc.setLabel(campaignScheduleList.get(i).getStatusStr());
							lc.setParent(li);

							//Delete
							lc = new Listcell();
							Hbox hbox = new Hbox();
							Image delImg = new Image("/img/action_delete.gif");
							delImg.setTooltiptext("Delete");
							delImg.setStyle("cursor:pointer;");
							delImg.setAttribute("delete", "campScheduleDelete");
							delImg.addEventListener("onClick", new MyListener());
							delImg.setParent(hbox);

							hbox.setParent(lc);	
							lc.setParent(li);

							li.setHeight("30px");
							li.setParent(viewAllActiveSchedulesWinId$campListlbId);

							//}//internal if
						}
						logger.debug("Completed for loop till size  ::"+index);
					}//for loop

				}//first time
				else if(startIndex !=0 ){


					noOfTimeRedraw ++;
					Listitem li;
					Listcell lc;
					logger.info(startIndex+" Start Index & Size ::"+size);
					logger.info("startIndex == campaignScheduleList.size()"+(startIndex == campaignScheduleList.size()));
					for (int i =  startIndex+1; i <= size; i++) {
						//	logger.info("value for index :"+i+" : list size :: "+campaignScheduleList.size());
						if( campaignScheduleList.size() <= i){
							logger.info("NO more campaigns");
							viewAllActiveSchedulesWinId$noRecordsActiveLbId.setVisible(true);
							viewAllActiveSchedulesWinId$viewMoreActiveSchedAnchId.setVisible(false);
							logger.info("Camp list reach size break......");
							break;
						}

						li = new Listitem();
						lc = new Listcell();
						viewAllActiveSchedulesWinId$noRecordsActiveLbId.setVisible(false);
						//Schedule Date

						li.setValue(campaignScheduleList.get(i));
						lc.setParent(li);
						lc = new Listcell();
						/*logger.info("::"+i);*/

						logger.info(MyCalendar.calendarToString(campaignScheduleList.get(i).getScheduledDate(),MyCalendar.FORMAT_DATE_FORMAT,(TimeZone) sessionScope.get("clientTimeZone")));
						lc.setLabel(MyCalendar.calendarToString(campaignScheduleList.get(i).getScheduledDate(),MyCalendar.FORMAT_DATE_FORMAT,(TimeZone) sessionScope.get("clientTimeZone")));
						lc.setParent(li);

						//Status
						lc = new Listcell();
						lc.setLabel(campaignScheduleList.get(i).getStatusStr());
						lc.setParent(li);

						//Delete
						lc = new Listcell();
						Hbox hbox = new Hbox();
						Image delImg = new Image("/img/action_delete.gif");
						delImg.setTooltiptext("Delete");
						delImg.setStyle("cursor:pointer;");
						delImg.setAttribute("delete", "campScheduleDelete");
						delImg.addEventListener("onClick", new MyListener());
						delImg.setParent(hbox);

						hbox.setParent(lc);	
						lc.setParent(li);

						li.setHeight("30px");
						li.setParent(viewAllActiveSchedulesWinId$campListlbId);

						//}//internal if
					}//for loop


				}//more than one time
				else {
					logger.info("NO more campaigns");
					viewAllActiveSchedulesWinId$noRecordsActiveLbId.setVisible(true);
					viewAllActiveSchedulesWinId$viewMoreActiveSchedAnchId.setVisible(false);
				}

			}

		} catch (WrongValueException e) {
			logger.error("WrongValueException ", e);
		} catch (Exception e) {
			logger.error("Exception ", e);
		}
		logger.debug("<<<<< Completed redraw .");
	}//redraw


	private List<WACampaignsSchedule> sentCampaignScheduleList = null;
	/**
	 * redrawSent
	 * @param startIndex
	 * @param size
	 */
	boolean redrawFlag = true;
	public void redrawSent(int startIndex,int size){
		logger.debug(">>>>>>> Started  redrawSent :: ");
		try{
			sentCampaignScheduleList = getArchivedCampScheduleList(WACampScheduleList);

			if(sentCampaignScheduleList == null ){
				viewAllArchivedSchedulesWinId$noRecordsArchivedLbId.setVisible(true);
				viewAllArchivedSchedulesWinId$viewMoreArchievedSchedAnchId.setVisible(false);
				logger.error("No Campaing's existing.........");
				return;
			}

			if(redrawFlag){
				logger.info("reversing list");
				Collections.reverse(sentCampaignScheduleList);
			}



			if(sentCampaignScheduleList != null && sentCampaignScheduleList.size()>0){	
				if(startIndex == 0){
					Listitem li;
					Listcell lc;
					for(int i=0;i<=sentCampaignScheduleList.size()-1;i++ ){

						if(i == size){
							logger.info(i+"..Camp list reach size break......"+size);
							break;
						}
						else{
							li = new Listitem();
							lc = new Listcell();
							viewAllArchivedSchedulesWinId$noRecordsArchivedLbId.setVisible(false);
							viewAllArchivedSchedulesWinId$viewMoreArchievedSchedAnchId.setVisible(true);
							viewAllArchivedSchedAnchId.setVisible(true);
							li.setValue(sentCampaignScheduleList.get(i));
							lc.setParent(li);
							//Date
							lc = new Listcell();
							lc.setLabel(MyCalendar.calendarToString(sentCampaignScheduleList.get(i).getScheduledDate(),MyCalendar.FORMAT_DATE_FORMAT,(TimeZone) sessionScope.get("clientTimeZone")));
							lc.setParent(li);
							//Status
							lc = new Listcell();
							lc.setLabel(sentCampaignScheduleList.get(i).getStatusStr());
							lc.setParent(li);

							li.setHeight("30px");
							li.setParent(viewAllArchivedSchedulesWinId$campListlbId);
						}
					}

				}


				else if(startIndex != 0 ){
					Listitem li;
					Listcell lc;
					for(int i =  startIndex; i <= size; i++){
						if( sentCampaignScheduleList.size() <= i){
							logger.info("NO more campaigns");
							viewAllArchivedSchedulesWinId$noRecordsArchivedLbId.setVisible(true);
							viewAllArchivedSchedulesWinId$viewMoreArchievedSchedAnchId.setVisible(false);
							logger.info("Camp list reach size break......");
							break;
						}

						li = new Listitem();
						lc = new Listcell();
						li = new Listitem();
						lc = new Listcell();
						viewAllArchivedSchedulesWinId$noRecordsArchivedLbId.setVisible(false);
						viewAllArchivedSchedulesWinId$viewMoreArchievedSchedAnchId.setVisible(true);
						viewAllArchivedSchedAnchId.setVisible(true);
						li.setValue(sentCampaignScheduleList.get(i));
						lc.setParent(li);
						//Date
						lc = new Listcell();
						lc.setLabel(MyCalendar.calendarToString(sentCampaignScheduleList.get(i).getScheduledDate(),MyCalendar.FORMAT_DATE_FORMAT,(TimeZone) sessionScope.get("clientTimeZone")));
						lc.setParent(li);
						//Status
						lc = new Listcell();
						lc.setLabel(sentCampaignScheduleList.get(i).getStatusStr());
						lc.setParent(li);

						li.setHeight("30px");
						li.setParent(viewAllArchivedSchedulesWinId$campListlbId);
					}

				}
			}
			else{
				viewAllArchivedSchedulesWinId$noRecordsArchivedLbId.setVisible(true);
				viewAllArchivedSchedulesWinId$viewMoreArchievedSchedAnchId.setVisible(false);
				logger.error("No Campaing's existing.........");
			}
		}
		catch(Exception exception){
			logger.error("Exception",exception);
		}
		logger.debug("<<<<< Completed redrawSent .");
	}//redrawSent

	private List<WACampaignsSchedule> getArchivedCampScheduleList(
			List<WACampaignsSchedule> campScheduleList2) {
		List<WACampaignsSchedule> archiveCampSchedule = new ArrayList<WACampaignsSchedule>();
		for (WACampaignsSchedule campaignSchedule : campScheduleList2) {
			if(!(campaignSchedule.getStatus() == 0)  && !( campaignSchedule.getStatus() == 2 )) {
				logger.info(campaignSchedule.getDateStrByTimeZone(MyCalendar.FORMAT_DATE_FORMAT,(TimeZone) sessionScope.get("clientTimeZone")));
				archiveCampSchedule.add(campaignSchedule);
			}
		}
		//				logger.debug("<<<<< Completed getArchivedCampScheduleList .");
		return archiveCampSchedule;
	}



	/**
	 * drawSentListBox
	 * @param campaignSchedule
	 */
	private void drawSentListBox(WACampaignsSchedule campaignSchedule) {
		logger.debug(">>>>>>> Started  drawSentListBox :: ");
		Listitem li = new Listitem();
		Listcell lc = new Listcell();
		viewAllArchivedSchedulesWinId$noRecordsArchivedLbId.setVisible(false);
		viewAllArchivedSchedulesWinId$viewMoreArchievedSchedAnchId.setVisible(true);
		viewAllArchivedSchedAnchId.setVisible(true);
		li.setValue(campaignSchedule);
		lc.setParent(li);
		//Date
		lc = new Listcell();
		lc.setLabel(MyCalendar.calendarToString(campaignSchedule.getScheduledDate(),MyCalendar.FORMAT_DATE_FORMAT,(TimeZone) sessionScope.get("clientTimeZone")));
		lc.setParent(li);
		//Status
		lc = new Listcell();
		lc.setLabel(campaignSchedule.getStatusStr());
		lc.setParent(li);

		li.setHeight("30px");
		logger.info(MyCalendar.calendarToString(campaignSchedule.getScheduledDate(),MyCalendar.FORMAT_DATE_FORMAT,(TimeZone) sessionScope.get("clientTimeZone")));
		li.setParent(sentCampaingsListlbId);
		logger.debug("<<<<< Completed drawSentListBox .");
	}//drawSentListBox


	/**
	 * onSelect$campListlbId$viewAllActiveSchedulesWinId
	 */
	public void onSelect$campListlbId$viewAllActiveSchedulesWinId() {
		if(viewAllActiveSchedulesWinId$campListlbId.getSelectedCount() == 0){
			viewAllActiveSchedulesWinId$campActionsBandBoxId.setDisabled(true);
			viewAllActiveSchedulesWinId$campActionsBandBoxId.setButtonVisible(false);
		}else if(viewAllActiveSchedulesWinId$campListlbId.getSelectedCount() > 0) {
			viewAllActiveSchedulesWinId$campActionsBandBoxId.setDisabled(false);
			viewAllActiveSchedulesWinId$campActionsBandBoxId.setButtonVisible(true);
		}
	}//onSelect$campListlbId$viewAllActiveSchedulesWinId
	/**
	 * onClick$delSelectedId$viewAllActiveSchedulesWinId
	 */
	public void onClick$delSelectedId$viewAllActiveSchedulesWinId()  {
		//		logger.debug(">>>>>>> Started  onClick$delSelectedId$viewAllActiveSchedulesWinId :: ");
		try {
			int count = viewAllActiveSchedulesWinId$campListlbId.getSelectedCount();
			if(logger.isDebugEnabled()) 
				logger.debug("Number of selected campaigns schedules to delete :" + count);
			if(count == 0) {
				Messagebox.show("Please select at least one schedule to delete.", "Information" , 
						Messagebox.OK, Messagebox.INFORMATION);
				return;
			}
			String msg = "You have chosen "+count+" schedules to delete. Do you want to continue?";
			boolean found = false;
			Set<Listitem> selList = viewAllActiveSchedulesWinId$campListlbId.getSelectedItems();
			logger.info("Selected list of items are ..:"+viewAllActiveSchedulesWinId$campListlbId.getSelectedCount());
			WACampaignsSchedule campaignSchedule;
			List<WACampaignsSchedule> campaignSchedules = new ArrayList<WACampaignsSchedule>();
			for (Listitem li : selList) {
				campaignSchedule = (WACampaignsSchedule)li.getValue();
				campaignSchedules.add(campaignSchedule);

				if(campaignSchedule.getStatus() == OCConstants.ACTIVE_EMAIL_STATUS || 
						campaignSchedule.getStatus() == OCConstants.DRAFT_EMAIL_STATUS) {
					found = true;
				}
			}
			if(found) {
				msg = "You have chosen "+count+" schedules to delete. Do you want to continue?";
				logger.info("msgmsg"+msg);
			}
			try {
				int confirm =Messagebox.show(msg, "Delete Confirmation",Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);

				if(confirm == Messagebox.OK){
					if(deleteCampaignsSchedules(campaignSchedules).equals("success")){
						waCampaign.setStatus(getCampaignStatus(waCampaign));
						WACampaignsDaoForDML.saveOrUpdate(waCampaign);
						MessageUtil.setMessage(count+" schedule(s) deleted successfully.", "green", "TOP");
					}
				}
				else {
					viewAllActiveSchedulesWinId$campActionsBandBoxId.setDisabled(true);
				}
			} catch (Exception e) {
				logger.error("Exception ::", e);
			}	
			viewAllActiveSchedulesWinId$campListlbId.clearSelection();
			logger.info("Item count ::"+viewAllActiveSchedulesWinId$campListlbId.getItemCount());
		} catch (Exception ex) {
			logger.error("** Exception :" , ex);
		}
	}//onClick$delSelectedId$viewAllActiveSchedulesWinId

	private String getCampaignStatus(WACampaign campaign) {

		if(WACampScheduleList == null || WACampScheduleList.size() == 0) {
			logger.debug("All Active or Draft Schedules are deleted.");
			return Constants.CAMP_STATUS_DRAFT;
		}

		WACampaignsSchedule latestCampaignSchedule = null;

		for (WACampaignsSchedule campaignSchedule : WACampScheduleList) {
			if(latestCampaignSchedule == null) latestCampaignSchedule = campaignSchedule;

			if(campaignSchedule.getScheduledDate().after(latestCampaignSchedule.getScheduledDate())){
				latestCampaignSchedule = campaignSchedule;
			}
		}//for

		if(latestCampaignSchedule.getStatus() == 0 || latestCampaignSchedule.getStatus() == 1 || latestCampaignSchedule.getStatus() == 2) 
			return latestCampaignSchedule.getStatusStr();
		else if(latestCampaignSchedule.getStatus() >= 3)
			return "Schedule Failure";
		else
			return "Draft";

	}

	/**
	 * This method helps to delete campaignScheduleList
	 * @param campaignSchedules
	 * @return message
	 */
	private String deleteCampaignsSchedules(List<WACampaignsSchedule> campaignSchedules) {
		logger.debug(">>>>>>> Started  deleteCampaignsSchedules :: ");
		try{
			logger.debug(">>>>>>> Started  deleteCampaignsSchedules :: "+campaignSchedules.size());
			WACampaignScheduleDaoForDML.deleteByCollection(campaignSchedules);

			//Performance Problem to DB hits in Loop, Need to Fixed.

			for (WACampaignsSchedule campaignSchedule : campaignSchedules) {

				Calendar lblDate = (Calendar) campActiveTillDateLbId.getAttribute("setLbDate");
				if(campaignSchedule.getScheduledDate().equals(lblDate)){
					logger.info("Removing attribute");
					campActiveTillDateLbId.removeAttribute("setLbDate");
				}
				removeCampScheduleFromList(campaignSchedule);
				rowsMap.remove(campaignSchedule.getwaCsId());
				rowMap.remove(campaignSchedule.getwaCsId());

			}// For Loop

			// Redrawing Lists
			int count = activeCampaingsListlbId.getItemCount();
			for(; count>0; count--) {
				activeCampaingsListlbId.removeItemAt(count-1);
			}
			count =  viewAllActiveSchedulesWinId$campListlbId.getItemCount();
			for(; count>0; count--) {
				viewAllActiveSchedulesWinId$campListlbId.removeItemAt(count-1);
			}
			if(WACampScheduleList != null && WACampScheduleList.size() > 0){

				List<WACampaignsSchedule> activeCampScheduleList = getActiveCampScheduleList(WACampScheduleList);
				if(activeCampScheduleList.size() > 0){
					createRowUpComingListBox(activeCampScheduleList.get(0), 0, true);
					createDivUpComingCampaigns(true,activeCampScheduleList.get(0));
					noOfTimeRedraw = 0;
					activeSchCount = 100;
					redraw(0, activeSchCount);
				}
				else{
					createDivUpComingCampaigns(false,null);
				}
				//redraw(0, activeSchCount);
			}
			else{
				createDivUpComingCampaigns(false,null);
			}

			return "success";
		} catch (Exception ex) {
			logger.error("** Exception :" , ex);
			return null;
		}
	}//deleteCampaignsSchedules

	/**
	 * This method handles the on click view more event
	 */
	public void onClick$viewMoreActiveSchedAnchId$viewAllActiveSchedulesWinId(){
		noOfTimeRedraw = noOfTimeRedraw + 1;
		redraw(activeSchCount,activeSchCount+100);
		activeSchCount = activeSchCount + 100;
		logger.info("now records are " + activeSchCount);

	}//onClick$viewMoreActiveSchedAnchId$viewAllActiveSchedulesWinId


	/**
	 * This method handles on click on viewAllArchivedSchedAnchId
	 */
	public void onClick$viewAllArchivedSchedAnchId(){
		try {
			viewAllArchivedSchedulesWinId.setVisible(true);
			viewAllArchivedSchedulesWinId.doHighlighted();
			viewAllArchivedSchedulesWinId.setVisible(true);
			sentSchCount = 100;
			int count =  viewAllArchivedSchedulesWinId$campListlbId.getItemCount();
			for(; count>0; count--) {
				viewAllArchivedSchedulesWinId$campListlbId.removeItemAt(count-1);
			}
			redrawSent(0,sentSchCount);
			viewAllArchivedSchedulesWinId.setStyle("scroll:auto;");
		} catch (Exception e) {
			logger.error("Exception ",e);
		}
	}//onClick$viewAllArchivedSchedAnchId

	/**
	 * onClick$submitBtnId$viewAllArchivedSchedulesWinId
	 */
	public void onClick$submitBtnId$viewAllArchivedSchedulesWinId(){
		int count =  viewAllArchivedSchedulesWinId$campListlbId.getItemCount();
		for(; count>0; count--) {
			viewAllArchivedSchedulesWinId$campListlbId.removeItemAt(count-1);
		}
		viewAllArchivedSchedulesWinId.setClosable(false);
		viewAllArchivedSchedulesWinId.setVisible(false);
	}//onClick$submitBtnId$viewAllArchivedSchedulesWinId

	/**
	 * onClick$viewMoreArchievedSchedAnchId$viewAllArchivedSchedulesWinId
	 */
	public void onClick$viewMoreArchievedSchedAnchId$viewAllArchivedSchedulesWinId(){
		redrawSent (sentSchCount,sentSchCount+100);
		sentSchCount = sentSchCount + 100;
		logger.info("now records are " + sentSchCount);
	}//onClick$viewMoreArchievedSchedAnchId$viewAllArchivedSchedulesWinId


	private void drawArchivedDiv(WACampaignsSchedule campaignSchedule){
		if(!(0 == campaignSchedule.getStatus()) && !( 2 == campaignSchedule.getStatus())){
			campaignSentDivId.setVisible(true);
			campaignSentLbId.setValue(MyCalendar.calendarToString(campaignSchedule.getScheduledDate(), MyCalendar.FORMAT_DATE_FORMAT,(TimeZone) sessionScope.get("clientTimeZone")));
			logger.info(MyCalendar.calendarToString(campaignSchedule.getScheduledDate(), MyCalendar.FORMAT_DATE_FORMAT),(TimeZone) sessionScope.get("clientTimeZone"));
			campaignSentLbId.setStyle(OCConstants.FONT_BOLD);
		}
		else{
			campaignSentDivId.setVisible(false);
		}
	}//drawArchivedDiv

	private void createDivUpComingCampaigns(boolean flag, WACampaignsSchedule waCampaignSchedule) {
		logger.debug(">>>>>>> Started  createDivUpComingCampaigns :: ");


		List<WACampaignsSchedule> activeOrDraftCampScheduleList = getActiveCampScheduleList(WACampScheduleList);

		WACampaignsSchedule lastSchedule = getLastSchedule(activeOrDraftCampScheduleList);

		if(lastSchedule != null){
			campActiveTillDateLbId.setValue(MyCalendar.calendarToString(lastSchedule.getScheduledDate(), MyCalendar.FORMAT_DATE_FORMAT, (TimeZone) sessionScope.get("clientTimeZone")));
			campActiveTillDateLbId.setAttribute("setLbDate",lastSchedule.getScheduledDate());
			campaignActiveTillDivId.setVisible(true);
			String size = (WACampScheduleList != null && WACampScheduleList.size() > 0) ? getSize(WACampScheduleList) : 1+"";  
			//int allActiveSchedules = getAcitveScheduleSize(campScheduleList);

			numOfTimeCampActiveLbId.setValue(size +" schedule(s)");
			numOfTimeCampActiveLbId.setStyle(OCConstants.FONT_BOLD);
			logger.info("persistentCamp Date .....: "+MyCalendar.calendarToString(lastSchedule.getScheduledDate(), MyCalendar.FORMAT_DATE_FORMAT, (TimeZone) sessionScope.get("clientTimeZone")));

		}
		else{
			campaignActiveTillDivId.setVisible(false);
		}

		logger.debug("<<<<< Completed createDivUpComingCampaigns .");


	}



	private WACampaignsSchedule getLastSchedule(List<WACampaignsSchedule> activeOrDraftCampScheduleList) {
		WACampaignsSchedule lastestCampaignSchedule = null;

		for (WACampaignsSchedule WACampaignSchedule : activeOrDraftCampScheduleList) {

			if(lastestCampaignSchedule == null ) lastestCampaignSchedule = WACampaignSchedule ;

			if(WACampaignSchedule.getScheduledDate().after(lastestCampaignSchedule.getScheduledDate())){
				lastestCampaignSchedule = WACampaignSchedule;
			}
		}

		return lastestCampaignSchedule;
	}


	private WACampaignsSchedule addDateCheck(Calendar selectedDtCal,
			WACampaignsSchedule parentCampSchedule, byte criteria) {	

		logger.debug("-------- just entered---------");

		selectedDtCal.set(Calendar.SECOND,0);
		selectedDtCal.set(Calendar.MILLISECOND,0);

		Calendar tempCal = Calendar.getInstance();
		tempCal.setTime(selectedDtCal.getTime());

		logger.info("######==========>"+tempCal.get(Calendar.HOUR_OF_DAY));
		

		WACampaignsSchedule WACampSchedule = new WACampaignsSchedule(tempCal, currUser.getUserId()); //--1

		if(WACampScheduleList.contains(WACampSchedule)) {
			MessageUtil.setMessage("Schedule added on same date and time.\n Please select a different time.", "color:red", "TOP");
			return null;
		}

		//STARTS 7, to make similar to email campaign schedules
		int available = 0;

		try{
			if(WACampSchedule != null && WACampSchedule.getScheduledDate().after(currUser.getPackageExpiryDate())) {
				MessageUtil.setMessage("Schedule date cannot be after your package expiry date.", "color:red", "TOP");
				return null;
			}
		}catch(Exception e){
			logger.error("Exception >>>>>>>> ", e);
		}


		WACampSchedule.setwaCampaignId(waCampaign.getWaCampaignId());

		WACampSchedule.setStatus((byte)0);

		try{
			WACampSchedule.setUserId(currUser.getUserId());
		}catch(Exception e){
			logger.error("Exception >>>>>>>> ", e);
		}
		
		if(logger.isDebugEnabled()) {
			logger.debug("-------- before returning---------");
		}
		return WACampSchedule;

	}


	private class MyListener implements EventListener {

		@Override
		public void onEvent(Event event) throws Exception {

			String action = (String)event.getTarget().getAttribute(
					WACampSettingsController.TB_ACTION);
			Rows rows;
			WACampaignsSchedule campSchedule;

			if(event.getTarget() instanceof Image) {
				logger.info("Deleting on click of Image Event");
				Image img = (Image)event.getTarget();
				String imageEventName = img.getAttribute("delete").toString();
				Listitem li = (Listitem)img.getParent().getParent().getParent();

				campSchedule = (WACampaignsSchedule)li.getValue();
				if("campScheduleDelete".equals(imageEventName)){
					/**
					 *  Deletes the row from the rows and removes corresponding
					 *  schedule object from the list when user clicks on delete link 
					 */
					int confirm = Messagebox.show("Are you sure you want to delete the schedule?", 
							"Confirm", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);

					if(confirm == Messagebox.OK) {
						try {
							logger.info(" >>> 1 >>>>>"+campSchedule.getwaCsId());

							UserCampaignExpiration triggeredAlert = getTriggeredAlertEmail();
							if(triggeredAlert != null){

								Calendar expireOn = triggeredAlert.getSentOn();
								expireOn.add(Calendar.DAY_OF_YEAR, 7);
								if(campSchedule.getScheduledDate().equals(expireOn)){

									logger.debug("in deleted ====="+expireOn.get(Calendar.DATE));
									triggeredAlert.setStatus(OCConstants.USER_ALERT_CAMPAIGN_EXPIRED_STATUS_SCHEDULE_DELEATED);
									triggeredAlert.setModifiedDate(Calendar.getInstance());
									userCampaignExpirationDaoForDML.saveOrUpdate(triggeredAlert);
								}
							}

							if(campSchedule.getwaCsId() != null)
								//smsCampaignScheduleDao.deleteByCampSchId(campSchedule.getSmsCsId());
								WACampaignScheduleDaoForDML.deleteByCampSchId(campSchedule.getwaCsId());

							Calendar lblDate = (Calendar) campActiveTillDateLbId.getAttribute("setLbDate");
							if(campSchedule.getScheduledDate().equals(lblDate)){
								logger.info("Removing attribute");
								campActiveTillDateLbId.removeAttribute("setLbDate");
							}
							removeCampScheduleFromList(campSchedule);

							waCampaign.setStatus(getCampaignStatus(waCampaign));
							campaignsDaoForDML.saveOrUpdate(waCampaign);
							rowsMap.remove(campSchedule.getwaCsId());
							rowMap.remove(campSchedule.getwaCsId());
							MessageUtil.setMessage("Schedule deleted successfully.", "green", "TOP");

							int count = activeCampaingsListlbId.getItemCount();
							for(; count>0; count--) {
								activeCampaingsListlbId.removeItemAt(count-1);
							}
							count =  viewAllActiveSchedulesWinId$campListlbId.getItemCount();
							for(; count>0; count--) {
								viewAllActiveSchedulesWinId$campListlbId.removeItemAt(count-1);
							}
							if(WACampScheduleList != null && WACampScheduleList.size() > 0){

								List<WACampaignsSchedule> activeCampScheduleList = getActiveCampScheduleList(WACampScheduleList);
								if(activeCampScheduleList.size() > 0){
									createRowUpComingListBox(activeCampScheduleList.get(0), 0, true);
									createDivUpComingCampaigns(true,activeCampScheduleList.get(0));
									noOfTimeRedraw = 0;
									activeSchCount = 100;
									redraw(0, activeSchCount);
								}
								else{
									createDivUpComingCampaigns(false,null);
								}
								//redraw(0, activeSchCount);
							}
							else{
								createDivUpComingCampaigns(false,null);
							}

						} catch (Exception e) {
							logger.error("Exception ::", e);
						}
					}
				}
			}
			else if( event.getTarget() instanceof A) {

				Object srcObject = event.getTarget().getAttribute(ATTRIBUTE_SOURCE);
				if(srcObject instanceof SegmentRules) {

					SegmentRules segmentRule = (SegmentRules)srcObject;
					String str= segmentRule.getSegRuleToView();
					viewSegRuleWinId$segRuleLblId.setValue(str);
					viewSegRuleWinId.setVisible(true);


				}else if(srcObject instanceof MailingList) {

					MailingList mailingList=(MailingList) srcObject;
					Sessions.getCurrent().setAttribute("mailingList",mailingList);

					if(Sessions.getCurrent().getAttribute("viewType") != null )
						Sessions.getCurrent().removeAttribute("viewType");

					Redirect.goTo(PageListEnum.CONTACT_CONTACT_VIEW);


				}
			}
			else if( event.getTarget() instanceof Toolbarbutton && action != null) {

				//				Commented for 2.3.11
				currentRow = (Row) event.getTarget().getParent().getParent();
				campSchedule = (WACampaignsSchedule)currentRow.getValue();

				if(action.equals(WACampSettingsController.TB_ACTION_DELETE)) {

					int confirm = Messagebox.show("Are you sure you want to delete the schedule?", 
							"Confirm", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);

					if(confirm == Messagebox.OK) {

						try {

							logger.info(" >>> 1 >>>>>"+campSchedule.getwaCsId());
							WACampaignScheduleDaoForDML.deleteByCampaignId(campSchedule.getwaCsId());


							rows = (Rows)currentRow.getParent();
							rows.removeChild(currentRow);

							if(rows.getChildren().size() == 0 && rows.getParent().getParent() instanceof Detail) {
								Detail detail = (Detail)(rows.getParent().getParent()); 
								rows.getParent().setVisible(false);
								detail.setOpen(false);
								detail.setStyle("display:none");
							}

							//rows.invalidate();
							removeCampScheduleFromList(campSchedule);

							waCampaign.setStatus(getCampaignStatus(waCampaign));
							campaignsDaoForDML.saveOrUpdate(waCampaign);


							rowsMap.remove(campSchedule.getwaCsId());
							rowMap.remove(campSchedule.getwaCsId());
							MessageUtil.setMessage("Schedule deleted successfully.", "green", "TOP");

						} catch (Exception e) {
							logger.error("Exception ::", e);
						}

					}



				}
				else if(action.equals(WACampSettingsController.TB_ACTION_EDIT)) {}


			}// if toolbarbutton event

			else if(event.getTarget() instanceof Detail) {

				//Commented for 2.3.11
				Detail detail = (Detail) event.getTarget();
				Grid subGrid = (Grid)detail.getFirstChild();
				rows = subGrid.getRows();

				if(rows.getChildren().size() == 0) {
					rows.getParent().setVisible(false);
				}else {
					List list = rows.getChildren();

					for(Object obj:list){
						Row row=(Row)obj;
						WACampaignsSchedule campShcedule=(WACampaignsSchedule)row.getValue();

						List<Object[]> childList=WACampaignScheduleDao.getAllChidren(campShcedule.getwaCsId(),campShcedule.getwaCampaignId());
						if(childList!=null) {
							logger.debug("/list size is"+childList.size()+"*****"+((Detail)row.getChildren().get(0)).isOpen());
							detail=row.getDetailChild();
							detail.setStyle("display:block;");
							detail.addEventListener("onOpen", myListener);
							
						} //if
					}//for

					rows.getParent().setVisible(true);
				}//else
			}//else if

			else if(event.getTarget() instanceof Label){

				logger.info("included promo code clicked>>>>>>>>>>>>>>>>");
				Redirect.goTo(PageListEnum.ADMIN_VIEW_COUPONS);

			}
		}//onEvent

		private UserCampaignExpiration getTriggeredAlertEmail() {
			// TODO Auto-generated method stub
			return null;
		}

		public String getCampaignStatus(WACampaign wACampaign) {


			if(WACampScheduleList == null || WACampScheduleList.size() == 0) {
				logger.debug("All Active or Draft Schedules are deleted.");
				return Constants.CAMP_STATUS_DRAFT;
			}
			Calendar startCal =null;
			Calendar endCal = null;

			WACampaignsSchedule latestWACampaignSchedule = null;

			for (WACampaignsSchedule campaignSchedule : WACampScheduleList) {
				if(latestWACampaignSchedule == null) latestWACampaignSchedule = campaignSchedule;

				if(campaignSchedule.getScheduledDate().after(latestWACampaignSchedule.getScheduledDate())){
					latestWACampaignSchedule = campaignSchedule;
				}

				if(campaignSchedule.getStatus() != 0 ) continue;

				if(startCal== null && endCal== null){
					startCal = campaignSchedule.getScheduledDate();
					endCal = campaignSchedule.getScheduledDate();


				}

				if(endCal != null && endCal.before(campaignSchedule.getScheduledDate())){
					endCal = campaignSchedule.getScheduledDate();
				}
				if(startCal.after(campaignSchedule.getScheduledDate())){
					startCal = campaignSchedule.getScheduledDate();
				}
			}//for



			if(latestWACampaignSchedule.getStatus() == 0 || latestWACampaignSchedule.getStatus() == 1 || latestWACampaignSchedule.getStatus() == 2) 
				return latestWACampaignSchedule.getStatusStr();
			else if(latestWACampaignSchedule.getStatus() >= 3)
				return "Schedule Failure";
			else
				return "Draft";

		}

	}// class EventListener



	private boolean isCampaignDraftedInLastStep_OR_Sent(WACampaign WACampaign){ 

		if(WACampaign == null)
			return false;

		List<WACampaignsSchedule> WACampaignScheduleList = null;
		try {
			WACampaignScheduleList = WACampaignScheduleDao.getByWACampaignId(WACampaign.getWaCampaignId());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  

		if(WACampaignScheduleList == null || WACampaignScheduleList.size() == 0){
			return false;
		}

		for(WACampaignsSchedule aWACampaignSchedule : WACampaignScheduleList){
			if(aWACampaignSchedule.getStatus() == 1 || aWACampaignSchedule.getStatus() == 2){
				return true;
			}
		}

		return false;

	}
	}
