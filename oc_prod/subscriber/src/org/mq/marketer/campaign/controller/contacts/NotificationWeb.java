package org.mq.marketer.campaign.controller.contacts;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TimeZone;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import org.apache.commons.io.FilenameUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.Campaigns;
import org.mq.marketer.campaign.beans.Contacts;
import org.mq.marketer.campaign.beans.Coupons;
import org.mq.marketer.campaign.beans.IosNotification;
import org.mq.marketer.campaign.beans.MLCustomFields;
import org.mq.marketer.campaign.beans.MailingList;
import org.mq.marketer.campaign.beans.Notification;
import org.mq.marketer.campaign.beans.NotificationCampaignReport;
import org.mq.marketer.campaign.beans.NotificationSchedule;
import org.mq.marketer.campaign.beans.POSMapping;
import org.mq.marketer.campaign.beans.PushNotificationAndroid;
import org.mq.marketer.campaign.beans.PushNotificationIos;
import org.mq.marketer.campaign.beans.PushNotificationData;
import org.mq.marketer.campaign.beans.SegmentRules;
import org.mq.marketer.campaign.beans.UserCampaignCategories;
import org.mq.marketer.campaign.beans.UserCampaignExpiration;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.controller.campaign.NotificationWebList;
import org.mq.marketer.campaign.controller.layout.EditorController;
import org.mq.marketer.campaign.controller.sms.SmsCampSettingsController;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.custom.MyDatebox;
import org.mq.marketer.campaign.dao.CampaignsDao;
import org.mq.marketer.campaign.dao.ContactsDao;
import org.mq.marketer.campaign.dao.CouponsDao;
import org.mq.marketer.campaign.dao.MLCustomFieldsDao;
import org.mq.marketer.campaign.dao.MailingListDao;
import org.mq.marketer.campaign.dao.NotificationCampaignReportDao;
import org.mq.marketer.campaign.dao.NotificationDao;
import org.mq.marketer.campaign.dao.NotificationDaoForDML;
import org.mq.marketer.campaign.dao.NotificationScheduleDao;
import org.mq.marketer.campaign.dao.NotificationScheduleDaoForDML;
import org.mq.marketer.campaign.dao.POSMappingDao;
import org.mq.marketer.campaign.dao.SegmentRulesDao;
import org.mq.marketer.campaign.dao.UserCampaignCategoriesDao;
import org.mq.marketer.campaign.dao.UserCampaignExpirationDao;
import org.mq.marketer.campaign.dao.UserCampaignExpirationDaoForDML;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.LBFilterEventListener;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PageListEnum;
import org.mq.marketer.campaign.general.PageUtil;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Redirect;
import org.mq.marketer.campaign.general.SalesQueryGenerator;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.utils.OCConstants;
import org.springframework.util.StringUtils;
import org.zkoss.image.AImage;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.A;
import org.zkoss.zul.Bandbox;
import org.zkoss.zul.Button;
import org.zkoss.zul.Column;
import org.zkoss.zul.Columns;
import org.zkoss.zul.Detail;
import org.zkoss.zul.Div;
import org.zkoss.zul.Fileupload;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Html;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Window;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

@SuppressWarnings("rawtypes")
public class NotificationWeb extends GenericForwardComposer{

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	private Users currentUser;
	private Session session;
	private Image img,imgLogo,imgBarcode,chooseDateTimeImgId;
	private Label msgLb,msgLbLogo;
	private Listbox mergeTagListboxId,mergeTagBodyListboxId, discountTagListboxId;
	private Listbox  promotionBarCodeId,insertPromotionCouponLbId,categoryLbId;
	private Set<Long> listIdsSet;
	private MailingListDao mailingListDao;
	private CouponsDao couponsDao;
	private Textbox notificationCampaignNameId,notificationBodyId,headerId,redirectUrlId,testNotificationTbId;
	private Div navDivId,step1DivId,step2DivId,step3DivId,frqDivId,prtDtDivId;
	private A step1AId,step2AId,step3AId;
	private Set<Long> segmentIdsSet; 
	private SegmentRulesDao segmentRulesDao;
	private List<Div> divList = new ArrayList<Div>();
	private List<A> anchList = new ArrayList<A>();
	private Button goToNextBtnId,gotoStep2BtnId,step1BackBtnId,saveAsDraftStep1BtnId, gotoStep3BtnId,saveAsDraftStep2BtnId;
	private Div mainNavDivId;
	private NotificationDao notificationDao;
	private NotificationDaoForDML notificationDaoForDML;
	private UserCampaignCategoriesDao userCampaignCategoriesDao;
	private Notification notification;
	private Radiogroup configurelistRgId;
	private Div mlDivId, segDivId;
	private String notificationIsEdit,notificationDraftStatus;
	private ContactsDao contactsDao;
	private CampaignsDao campaignsDao;
	private Label campNameLblId,categoryNameLblId,schErrorLblId;
	private Html previewWin$html;
	private Window previewWin;
	private Rows schedGrdRowsId;
	private UserCampaignExpirationDaoForDML userCampaignExpirationDaoForDML;
	private UserCampaignExpirationDao userCampaignExpirationDao;
	private NotificationScheduleDaoForDML notificationScheduleDaoForDML;
	private NotificationScheduleDao notificationScheduleDao;
	private Listbox activeCampaingsListlbId;
	private MyDatebox prtDtBxId;
	private boolean pastDate;
	private Label campActiveTillDateLbId,numOfTimeCampActiveLbId,resendOptionWinId$errMsgLblId;
	private Div campaignActiveTillDivId,listNamesDivId;
	private  List<NotificationSchedule> notificationScheduleList;
	private static String TB_ACTION = "TOOLBUTTON ACTION";
	private static String TB_ACTION_DELETE = "DELTE";
	private static String TB_ACTION_RESEND = "RESEND";
	private Row currentRow;
	private static byte MAX_RESEND_LEVEL = 2;
	private Map<Calendar, Rows> rowsMap;
	private Map<Calendar, Row> rowMap;
	private Listbox viewAllActiveSchedulesWinId$campListlbId;
	private Window viewSegRuleWinId;
	private Label viewSegRuleWinId$segRuleLblId,recipentsSourceLblId;
	private final String ATTRIBUTE_SOURCE = "Source";
	private Window resendOptionWinId;
	private List<Calendar> tempList;
	private List<NotificationSchedule> tempNotificationCampScheduleList;
	private MyListener myListener = new MyListener();
	private int activeSchCount = 100;
	private Label viewAllActiveSchedulesWinId$noRecordsActiveLbId,campaignSentLbId,viewAllArchivedSchedulesWinId$noRecordsArchivedLbId;
	private A viewAllActiveSchedulesWinId$viewMoreActiveSchedAnchId;
	int noOfTimeRedraw=0;
	private Div campaignSentDivId,selectedListDivId,dispRuleDivId;
	private A viewAllArchivedSchedulesWinId$viewMoreArchievedSchedAnchId,viewAllArchivedSchedAnchId;
	private Listbox sentCampaingsListlbId;
	private Listbox dispMlListsLBoxId, dispsegmentsLbId,frqLbId;
	private String listnamesStr;
	private Label selMlLblId, selRuleListLblId, selRuleLblId;
	private Window viewAllActiveSchedulesWinId;
	private Bandbox viewAllActiveSchedulesWinId$campActionsBandBoxId;
	private Label viewAllActiveSchedulesWinId$bulkDeleteLbId;
	private List<NotificationSchedule> campaignScheduleList = null;
	private String usersParentDirectory = null;
	private String notificationBannerImageDirectory = null;
	private String notificationBannerLogoDirectory = null;
	private Window viewAllArchivedSchedulesWinId;
	private Listbox viewAllArchivedSchedulesWinId$campListlbId;
	private int sentSchCount = 100;
	private static String USER_DATA_URL;
	private static String IMAGES_URL;
	private MyDatebox startDtBxId;
	private MyDatebox endDtBxId;
	private Button frqBtnId;
	private final String LATEST_SCH_ON = "ENDCAL";
	private final String START_SCH_ON = "STARTCAL";
	private final String FREEQUENCY = "FREQ";
	private static String optcultureFireBaseServerKey;
	
	@SuppressWarnings("unchecked")
	public NotificationWeb() {
		currentUser = GetUser.getUserObj();
		session = Sessions.getCurrent();
		String style = "font-weight:bold;font-size:15px;color:#313031;font-family:Arial,Helvetica,sans-serif;align:left";
		PageUtil.setHeader("Push Notification","",style,true);
		mailingListDao = (MailingListDao) SpringUtil.getBean("mailingListDao");
		segmentRulesDao = (SegmentRulesDao)SpringUtil.getBean("segmentRulesDao");
		notificationDao = (NotificationDao)SpringUtil.getBean("notificationDao");
		notificationDaoForDML = (NotificationDaoForDML)SpringUtil.getBean("notificationDaoForDML");
		notificationScheduleDaoForDML = (NotificationScheduleDaoForDML)SpringUtil.getBean("notificationScheduleDaoForDML");
		userCampaignCategoriesDao = (UserCampaignCategoriesDao)SpringUtil.getBean("userCampaignCategoriesDao"); 
		segmentIdsSet = (Set<Long>)session.getAttribute(Constants.SEGMENTIDS_SET);
		listIdsSet = (Set<Long>)session.getAttribute(Constants.LISTIDS_SET);
		notificationIsEdit = (String)session.getAttribute("editNotification"); 
		notification = (Notification)session.getAttribute("notificationCampaign");
		notificationDraftStatus = (String)session.getAttribute("notificationDraftStatus");
		contactsDao = (ContactsDao)SpringUtil.getBean("contactsDao");
		campaignsDao = (CampaignsDao)SpringUtil.getBean("campaignsDao");
		userCampaignExpirationDaoForDML = (UserCampaignExpirationDaoForDML)SpringUtil.getBean(OCConstants.USER_CAMPAIGN_EXPIRATION_DAO_FOR_DML);
		userCampaignExpirationDao = (UserCampaignExpirationDao)SpringUtil.getBean(OCConstants.USER_CAMPAIGN_EXPIRATION_DAO);
		notificationScheduleDao = (NotificationScheduleDao)SpringUtil.getBean("notificationScheduleDao");
		pastDate = false;
		notificationScheduleList = new ArrayList<NotificationSchedule>();
		rowsMap = new HashMap<Calendar, Rows>();
		rowMap = new HashMap<Calendar, Row>();
		tempList = new ArrayList<Calendar>();
		tempNotificationCampScheduleList = new ArrayList<NotificationSchedule>();
		usersParentDirectory = PropertyUtil.getPropertyValue("usersParentDirectory");
		notificationBannerImageDirectory = PropertyUtil.getPropertyValue("notificationBannerImageDirectory");
		notificationBannerLogoDirectory = PropertyUtil.getPropertyValue("notificationBannerLogoDirectory");
		IMAGES_URL = PropertyUtil.getPropertyValue("ApplicationUrl");
		USER_DATA_URL = IMAGES_URL+"UserData/";
		optcultureFireBaseServerKey = PropertyUtil.getPropertyValueFromDB("optcultureFireBaseServerKey");
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		logger.info("push notification");
		mainNavDivId =(Div)Utility.getComponentById("mainNavDivId");
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
		
		// set default date values
		MyCalendar currentCal = new MyCalendar((TimeZone) sessionScope.get("clientTimeZone"));
		currentCal.set(MyCalendar.MINUTE, currentCal.get(MyCalendar.MINUTE) + 15);
		prtDtBxId.setValue(currentCal);
		startDtBxId.setValue(new MyCalendar(currentCal));
		endDtBxId.setValue(new MyCalendar(currentCal));
		listIdsSet = (Set<Long>) session.getAttribute(Constants.LISTIDS_SET);
		Set<MailingList> set = new HashSet<MailingList>();
		if(listIdsSet != null && listIdsSet.size() > 0){
			set.addAll(mailingListDao.findByIds(listIdsSet));
			getPlaceHolderList(set);
		}
		promotionBarCodeImages();
		getCoupons();
		if(currentUser.getSubscriptionEnable() ){
     		getCampCategorties();
     	}else{
     		categoryLbId.setDisabled(true);
     	}
		if(notification != null && notificationIsEdit!=null) {
			editNotificationCampaign(notification);
		}
		LBFilterEventListener.lbFilterSetup(dispsegmentsLbId); 
		LBFilterEventListener.lbFilterSetup(dispMlListsLBoxId);
		if( (notificationIsEdit!=null && notificationIsEdit.equalsIgnoreCase("edit") ) || 
				(notificationDraftStatus != null  && !notificationDraftStatus.equals(Constants.SMS_CAMP_DRAFT_STATUS_STEP_TWO) )) {
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
		helpImgId.setVisible(categoryLbId.isDisabled());
	}
	
	public void onClick$step1BackBtnId() {
		if(notificationIsEdit!=null){
			Clients.evalJavaScript("changeStep(3,true)");
		}else{
			Redirect.goTo(PageListEnum.RM_HOME);
		}
	}
	
	public Div getVisibleDiv() {
			for(Div viblediv : divList) {
				if(viblediv.isVisible()) {
					return viblediv;
				}//if
			}//for
			return null;
		}
	
	public void onClick$uploadBtn() throws Exception {
		msgLb.setValue("");
		Media media = Fileupload.get();
		if(media == null){
			msgLb.setValue("please select a file");
			msgLb.setStyle("color:red;");
			return;
		}
		String type = media.getContentType().split("/")[0];
	    if(validateImageFormat(media)) {
			if (type.equals("image")) {
				File file1  = null;
				if(media!=null && media.getByteData()!=null) {
			        double fileSize = Math.ceil((media.getByteData().length))/1024;
						if(fileSize>1024){
							MessageUtil.setMessage(media.getName()+" cannot be uploaded.\n Reason: Size should not exceed 1 MB." , "color:red", "TOP");
							return;
						}
						String path = usersParentDirectory + File.separator +  currentUser.getUserName() + notificationBannerImageDirectory;
						String imgPath = path + media.getName().trim();
						file1  = new File(imgPath);
						if(file1.exists()) {
								String fileNameWithOutExt = FilenameUtils.removeExtension(media.getName());
									while(file1.exists()) {
										fileNameWithOutExt = getcountFormImageName(file1.getName());
										file1  = new File(path+fileNameWithOutExt);
									}
						}
						
			     } 
				/*
				 * BufferedImage dataBanner = getImage(media.getByteData()); java.awt.Image
				 * pictureBanner = (java.awt.Image) dataBanner.getScaledInstance(277,100,
				 * java.awt.Image.SCALE_DEFAULT); dataBanner = toBufferedImage(pictureBanner);
				 * dataBanner.createGraphics(); img.setContent(dataBanner);
				 */
				if (media instanceof AImage) {
					img.setContent((AImage) media);
					img.setWidth(277+"px");
					img.setHeight(100+"px");
					img.setAttribute("imageName", file1.getName());
				}
		        MessageUtil.setMessage(file1.getName() +" uploaded successfully!", "color: blue;");
			}else {
				 return;
			}
	    }else {
			 return;
		}
	}
	
	
	private String getcountFormImageName(String fileNameWithOutExt) {
		String regex ="(\\(\\d\\)\\.)"; 
		Pattern pattern = Pattern.compile(regex);
		Matcher m = pattern.matcher(fileNameWithOutExt);
		if (m.find()) {
			String url = m.group(1);
			int number = Integer.parseInt(url.replace("(", "").replace(").", ""));
			number = number+1;
			fileNameWithOutExt = fileNameWithOutExt.replace(url, "("+number+").");
		}else {
			String fileNameExt = FilenameUtils.getExtension(fileNameWithOutExt);
			String fileName = FilenameUtils.removeExtension(fileNameWithOutExt);
			fileNameWithOutExt = fileName+"(1)."+fileNameExt;
		}
		
		return fileNameWithOutExt;
	}

	private boolean validateImageFormat(Media media){
		try {
			if(media!=null && (media.getName()!=null && !media.getName().isEmpty())) {
			String imagName = media.getName();
			String ext =  FilenameUtils.getExtension(media.getName());
				if((!ext.equalsIgnoreCase("jpeg") && !ext.equalsIgnoreCase("jpg") && !ext.equalsIgnoreCase("png") && !ext.equalsIgnoreCase("gif"))){
					MessageUtil.setMessage("Upload of image " +imagName+ " failed, upload images of format .jpeg, .jpg, .png, .gif  only.", "color:red", "TOP");
					return false;
				}
				if(!Utility.validateUploadFilName(imagName) || imagName.contains("'")) {
					MessageUtil.setMessage("Image names should be alpha-numeric. Special characters allowed are A-Z, a-z, 0-9, &, +, -, =, @, _ and space, If any other characters are used, images will not be uploaded.", "color:red", "TOP");
					return false;
				}
			String fileNameWithoutExtn = imagName.substring(0, imagName.lastIndexOf('.'));
			logger.debug(imagName +" ImageName  contains speacial characters" +"fileNameWithOutExt ::"+fileNameWithoutExtn);
			}
		}catch (Exception e) {
			logger.error("validate image" +e);
		}
		return true;
	}
	
	public void onClick$uploadBtnLogo() throws Exception {
		msgLbLogo.setValue("");
		Media media = Fileupload.get();
		if(media == null){
			msgLbLogo.setValue("please select a file");
			msgLbLogo.setStyle("color:red;");
			return;
		}
		String type = media.getContentType().split("/")[0];
		if(validateImageFormat(media)) {
			if (type.equals("image")) {
				File file1  = null;
					if(media!=null && media.getByteData()!=null) {
				        double fileSize = Math.ceil((media.getByteData().length))/1024;
							if(fileSize>1024){
								MessageUtil.setMessage(media.getName()+" cannot be uploaded.\n Reason: Size should not exceed 1 MB." , "color:red", "TOP");
								return;
							}
							String path = usersParentDirectory + File.separator +  currentUser.getUserName() + notificationBannerLogoDirectory;
							String imgPath = path +"/" +media.getName().trim();
							file1  = new File(imgPath);
							if(file1.exists()) {
								String fileNameWithOutExt = FilenameUtils.removeExtension(media.getName());
									while(file1.exists()) {
										fileNameWithOutExt = getcountFormImageName(file1.getName());
										file1  = new File(path+fileNameWithOutExt);
									}
							}
							
				     } 
				/*
				 * BufferedImage data = getImage(media.getByteData()); java.awt.Image picture =
				 * (java.awt.Image) data.getScaledInstance(40,40, java.awt.Image.SCALE_DEFAULT);
				 * BufferedImage dataFinal = toBufferedImage(picture);
				 * dataFinal.createGraphics(); imgLogo.setContent(dataFinal);
				 * imgLogo.setAttribute("logoName", media.getName());
				 */
					if (media instanceof AImage) {
						imgLogo.setContent((AImage) media);
						imgLogo.setWidth(40+"px");
						imgLogo.setHeight(40+"px");
						imgLogo.setAttribute("logoName", file1.getName());
					}
			        MessageUtil.setMessage(file1.getName() +" uploaded successfully!", "color: blue;");
			}else {
				 return;
			}
		}else {
			 return;
		}
	}
	
	
	public static BufferedImage toBufferedImage(java.awt.Image img)
	{
	    if (img instanceof BufferedImage)
	    {
	        return (BufferedImage) img;
	    }
	    BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
	    Graphics2D bGr = bimage.createGraphics();
	    bGr.drawImage(img, 0, 0, null);
	    bGr.dispose();
	    return bimage;
	}
	
	
	public BufferedImage getImage(byte[] data) throws Exception
	 {
	    BufferedImage bi = null;
	    ByteArrayInputStream bais = new ByteArrayInputStream(data);
	    bi = ImageIO.read(bais);	    
	    return bi;
	 }
	
	
	public void onSelect$mergeTagListboxId() {
		if(mergeTagListboxId.getSelectedIndex() == 0) {
			return;
		}
		String value = (String)mergeTagListboxId.getSelectedItem().getValue();
		Clients.evalJavaScript("insertAtCursor(document.getElementById('" + headerId.getUuid() + "'), '"+value+"')");
		Clients.evalJavaScript("displayIndivOnKeyup();");
		mergeTagListboxId.setSelectedIndex(0);
	}
	
	public void onSelect$mergeTagBodyListboxId() {
		if(mergeTagBodyListboxId.getSelectedIndex() == 0) {
			return;
		}
		String value = (String)mergeTagBodyListboxId.getSelectedItem().getValue();
		Clients.evalJavaScript("insertAtCursorBody(document.getElementById('" + notificationBodyId.getUuid() + "'), '"+value+"')");
		Clients.evalJavaScript("displayBodyOnKeyup();");
		mergeTagBodyListboxId.setSelectedIndex(0);
	}
	
    public void onSelect$discountTagListboxId() { //for insertion
    	logger.info("entered in insertion");
		
		if(discountTagListboxId.getSelectedIndex() <= 0) {
			
			logger.debug("selected 0");
			return ;
		}
		Coupons selCoupon = discountTagListboxId.getSelectedItem().getValue();
		logger.info("selCoupon >>>>>>"+selCoupon);
		String replaceStr = Constants.CF_START_TAG+Constants.CC_TOKEN+selCoupon.getCouponId()
							+Constants.CF_END_TAG;
		logger.info("replaceStr >>>>>>"+replaceStr);
		Clients.evalJavaScript("insertAtCursorBody(document.getElementById('" + notificationBodyId.getUuid() + "'), '"+replaceStr+"')");
		Clients.evalJavaScript("displayBodyOnKeyup();");
		
		discountTagListboxId.setSelectedIndex(0);
		
	}
	
	public void onSelect$promotionBarCodeId() {
		imgBarcode.setSrc(promotionBarCodeId.getSelectedItem().getValue());
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
			
			if(user.getloyaltyServicetype() != null && !user.getloyaltyServicetype().equals(OCConstants.LOYALTY_SERVICE_TYPE_SB) )
			{
				//logger.info("the current user is a oc user");
				placeHoldersList.addAll(Constants.OCPLACEHOLDERS_LIST);
			}

			
			Map<String , String> StoreDefaultPHValues = EditorController.getDefaultStorePhValue(placeHoldersList);
			
			//Changes to add mapped UDF fields as placeholders
			//1.get all the pos mapped UDFs from the user pos settings(table:pos_mappings)
			List<POSMapping> contactsUDFList = posMappingDao.findOnlyByType(Constants.POS_MAPPING_TYPE_CONTACTS, GetUser.getUserId() );
			
			
				
				if(contactsUDFList != null) {
					
					//2.prepare merge tag and add to placeHoldersList
					//format : display lable :: |^GEN_<UDF>^|
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
			Listitem item1 = null;
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
//				logger.debug("key ::"+key+" value ::"+value);
				if(StoreDefaultPHValues.containsKey(placeHolder)) {
					
					value.insert(value.lastIndexOf("^"), StoreDefaultPHValues.get(placeHolder));
//					logger.info(" store ::"+placeHolder + " ====== value == "+value );
					placeHolder = placeHolder.replace(phTokenArr[1], value.toString());
				}
				
				
				for (POSMapping posMapping : contactsGENList) {
					
					if(!key.equalsIgnoreCase(posMapping.getCustomFieldName()) || posMapping.getCustomFieldName().startsWith("UDF")  ) continue;
					
					if(posMapping.getDefaultPhValue() == null || posMapping.getDefaultPhValue().isEmpty() ) break;
					
					value.insert(value.lastIndexOf("^"),  posMapping.getDefaultPhValue() );
					logger.debug(" value ::"+value);
				}
				item =  new Listitem(key,value.toString());
				item.setParent(mergeTagListboxId);
				item1 =  new Listitem(key,value.toString());
				item1.setParent(mergeTagBodyListboxId);
			} // for
			
			logger.debug("-- Exit --");
			return placeHoldersList;
		} catch (Exception e) {
			logger.error("Exception ::" , e);
			return null;
		}
	}
	
	private void promotionBarCodeImages() {
		this.couponsDao = (CouponsDao)SpringUtil.getBean("couponsDao");
		List<Coupons> couponList = couponsDao.findActiveAndRunningCouponsbyOrgId(currentUser.getUserOrganization().getUserOrgId(),null,"");
		if(couponList!=null && !couponList.isEmpty()) {
			Listitem item = null;
			String ccPreviewUrl = null;
			for (Coupons coupon : couponList) {
				if(coupon.getEnableBarcode() && coupon.getBarcodeType() != null && coupon.getBarcodeWidth() != null
						&& coupon.getBarcodeHeight() != null){

					String couponIdStr = Constants.CC_TOKEN + coupon.getCouponId() +"_"+coupon.getCouponName()+ 	
							"_"+coupon.getBarcodeType()+"_"+coupon.getBarcodeWidth()
							+"_"+coupon.getBarcodeHeight();
					
					String barcodeType = coupon.getBarcodeType().trim();
					
					try {
					if(barcodeType.equals(Constants.COUP_BARCODE_QR)){
						
						String bcqrImg = currentUser.getUserName()+File.separator+
								"Preview"+File.separator+"QRCODE"+File.separator+couponIdStr+".png";
						
						ccPreviewUrl = PropertyUtil.getPropertyValue("ApplicationUrl")+"UserData"+File.separator+bcqrImg;
					}
					else if(barcodeType.equals(Constants.COUP_BARCODE_AZTEC)){
						
						String bcazImg = currentUser.getUserName()+File.separator+
								"Preview"+File.separator+"AZTEC"+File.separator+couponIdStr+".png";
						
						ccPreviewUrl = PropertyUtil.getPropertyValue("ApplicationUrl")+"UserData"+File.separator+bcazImg;
					}
					else if(barcodeType.equals(Constants.COUP_BARCODE_LINEAR)){
						
						String bclnImg = currentUser.getUserName()+File.separator+
								"Preview"+File.separator+"LINEAR"+File.separator+couponIdStr+".png";
						
						
						ccPreviewUrl = PropertyUtil.getPropertyValue("ApplicationUrl")+"UserData"+File.separator+bclnImg;
					}
					else if(barcodeType.equals(Constants.COUP_BARCODE_DATAMATRIX)){
						
						String bcdmImg = currentUser.getUserName()+File.separator+
								"Preview"+File.separator+"DATAMATRIX"+File.separator+couponIdStr+".png";
						
						ccPreviewUrl = PropertyUtil.getPropertyValue("ApplicationUrl")+"UserData"+File.separator+bcdmImg;
					}
					
					
					}catch (Exception e) {
						logger.info("error during bar code image load");
					}
				}
				item =  new Listitem(coupon.getCouponName(),ccPreviewUrl);
				item.setParent(promotionBarCodeId);
			}
		}		
	
	}
	
public void  getCoupons() {
		
		List<Coupons> couponsCampList = couponsDao.findCouponsByStatus(currentUser.getUserOrganization().getUserOrgId());
		if(couponsCampList == null || couponsCampList.size() == 0) {
			logger.debug("got no coupons for this org");
			return;
		}
		
		Listitem item  = null;
		
		for (Coupons coupons : couponsCampList) {
			
			item = new Listitem(coupons.getCouponName(), coupons);
			item.setParent(insertPromotionCouponLbId);
			item = new Listitem(coupons.getCouponName(), coupons);
			item.setParent(discountTagListboxId);
		}
		
		for(Coupons coupon :couponsCampList){
			item = new Listitem(coupon.getCouponName(), coupon);
			if(coupon.getEnableBarcode()){
				item.setParent(insertPromotionCouponLbId);
			}
		}
		
	}

	public void onSelect$insertPromotionCouponLbId() {
		if(insertPromotionCouponLbId.getSelectedIndex() == 0) {
			return;
		}
		Coupons value = (Coupons)insertPromotionCouponLbId.getSelectedItem().getValue();
		String valueCode = Constants.CF_START_TAG+Constants.CC_TOKEN+value.getCouponId()+Constants.CF_END_TAG;
		Clients.evalJavaScript("$('#summernoteHeader').summernote('insertText','"+valueCode+"');");
		insertPromotionCouponLbId.setSelectedIndex(0);
	}
	
	public void sendTestNotification(String phoneNumber, Notification notification) throws Exception {
		 final String uri = "https://fcm.googleapis.com/fcm/send";
		 String[] phoneNumbers =   phoneNumber.split(",");
		 Contacts contactForNotification = null;
		 String imgUrl = PropertyUtil.getPropertyValue("ImageServerUrl");
		 List<String> notRegisterdNo = new ArrayList<String>();
			 for (String phNo :phoneNumbers) {
				 contactForNotification = contactsDao.findContactByPhone(phNo, currentUser.getUserId());
				 if(contactForNotification == null) {
					 notRegisterdNo.add(phNo);
				 } 
				 String json = null;
				 if(contactForNotification!=null) {
					Set<String> phSet = null;
					NotificationWebList list = new NotificationWebList();
				    if(contactForNotification.getPushNotification() && (contactForNotification.getInstanceId()!=null && !contactForNotification.getInstanceId().isEmpty()) && contactForNotification.getDeviceType()!=null) {
				    	
				    	phSet = Utility.getDRConTentPlaceHolder(notification.getNotificationContent());
				    	Map<String, Object> notiContent  = list.replacePlaceHoldersFromContact(contactForNotification, notification.getNotificationContent(), phSet, null, currentUser,null);
				    	phSet = Utility.getDRConTentPlaceHolder(notification.getHeader());
				    	Map<String, Object> notiheader  = list.replacePlaceHoldersFromContact(contactForNotification, notification.getHeader(), phSet, null, currentUser,null);
				    	
				    	PushNotificationIos pushNotificationIos = new PushNotificationIos();
				    	PushNotificationAndroid pushNotificationAndroid = new PushNotificationAndroid();//MA-90

				    	if(contactForNotification.getDeviceType()!=null && contactForNotification.getDeviceType().equals("android")) {
						 	String[] registration_ids = {contactForNotification.getInstanceId()};
						 	pushNotificationAndroid.setRegistration_ids(registration_ids);
							PushNotificationData data = new PushNotificationData();
							data.setImage(USER_DATA_URL+currentUser.getUserName()+"/Notification/bannerImage/"+notification.getNotificationName()+"/"+notification.getBannerImageUrl());
							data.setTitle(notiheader.get("1").toString());
							data.setMutable_content(true);
							data.setMessage(notiContent.get("1").toString());
							data.setBody(notiContent.get("1").toString());
							data.setSummaryText(notiContent.get("1").toString());
							data.setVisibility(0);
							data.setNotId(getRandomNumberInts());
							data.setPriority(2);
							if(data.getImage()!=null && !data.getImage().isEmpty()) {
								data.setStyle("picture");
							}else {
								data.setStyle("inbox");
							}
							pushNotificationAndroid.setNotification(data );
							ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
							json = ow.writeValueAsString(pushNotificationAndroid);
				    	}else if(contactForNotification.getDeviceType()!=null && contactForNotification.getDeviceType().equals("ios")) {
				    		String[] registration_ids = {contactForNotification.getInstanceId()};
				    		pushNotificationIos.setRegistration_ids(registration_ids);
							IosNotification iosNotification = new IosNotification();
							iosNotification.setTitle(notiheader.get("1").toString());
							iosNotification.setBody(notiContent.get("1").toString());
							iosNotification.setSound("default");
							iosNotification.setMutable_content(true);
							PushNotificationData data =  new PushNotificationData();
							data.setNotId(getRandomNumberInts());
							String imagePath = USER_DATA_URL+currentUser.getUserName()+"/Notification/bannerImage/"+notification.getNotificationName()+"/"+notification.getBannerImageUrl();
							String extension = FilenameUtils.getExtension(imagePath);
							if(extension.equalsIgnoreCase("jpg")) {
								data.setImage_url_jpg(imagePath);
							}else if(extension.equalsIgnoreCase("png")) {
								data.setImage_url_png(imagePath);
							}else if(extension.equalsIgnoreCase("gif")) {
								data.setImage_url_gif(imagePath);
							}
							pushNotificationIos.setNotification(iosNotification);
							pushNotificationIos.setData(data);
							ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
							json = ow.writeValueAsString(pushNotificationIos);
							if(data.getImage_url_jpg()!=null && !data.getImage_url_jpg().isEmpty()) {
								json = json.replace("image_url_jpg", "image-url-jpg");
							}else if(data.getImage_url_png()!=null && !data.getImage_url_png().isEmpty()) {
								json = json.replace("image_url_png", "image-url-png");
							}else if(data.getImage_url_gif()!=null && !data.getImage_url_gif().isEmpty()) {
								json = json.replace("image_url_gif", "image-url-gif");
							}
				    	}else if(contactForNotification.getDeviceType()!=null && contactForNotification.getDeviceType().equals("web")) {
							try {
								String[] registration_ids = {contactForNotification.getInstanceId()};
								pushNotificationIos.setRegistration_ids(registration_ids);
								//used IOS pojo here as the json generated for IOS and WEB are same. 
								IosNotification webNotification = new IosNotification();
								webNotification.setTitle(notiheader.get("1").toString());
								webNotification.setBody(notiContent.get("1").toString());
								webNotification.setSound("default");
								webNotification.setMutable_content(true);
								String bannerimage = notification.getBannerImageUrl();
								if(bannerimage!=null && !bannerimage.isEmpty()) {
									bannerimage = USER_DATA_URL+currentUser.getUserName()+"/Notification/bannerImage/"+notification.getNotificationName()+"/"+notification.getBannerImageUrl();
									bannerimage = bannerimage.replace(IMAGES_URL, imgUrl);
								}else {
									bannerimage = "";
								}
								String logo=notification.getLogoImageUrl();
								if(logo!=null && !logo.isEmpty()) {
									logo=USER_DATA_URL+currentUser.getUserName()+"/Notification/logoImage/"+notification.getNotificationName()+"/"+notification.getLogoImageUrl();
									logo=logo.replace(IMAGES_URL, imgUrl);
								}
								webNotification.setIcon(logo);
								webNotification.setImage(bannerimage);
								pushNotificationIos.setNotification(webNotification);
								ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
								json = ow.writeValueAsString(pushNotificationIos);
								logger.info("web json"+json);
							}catch (Exception e) {
							}
						}
					    DefaultHttpClient httpClient = new DefaultHttpClient();
					    postToURL(uri, json, httpClient);
				    }
			    }
			 }
			 if(notRegisterdNo!=null && !notRegisterdNo.isEmpty()) {
				 StringBuffer sb = new StringBuffer();
				 	for (String notRegisterd : notRegisterdNo) {
				 		sb.append(notRegisterd);
				 		sb.append(",");
					}
				 	String finalMobileNo = null;
				 	if (sb.toString().endsWith(",")) {
				 		finalMobileNo = sb.substring(0, sb.length() - 1);
				 	}
			    	MessageUtil.setMessage("Not Registered Mobile No List "+ finalMobileNo +".","color:red","TOP");
			    	testNotificationTbId.setValue(Constants.STRING_NILL);
			    	testNotificationTbId.setValue(finalMobileNo);
			}else {
			 testNotificationTbId.setValue(Constants.STRING_NILL);
			 testNotificationTbId.setPlaceholder("Enter Mobile Number(s)...");
			 MessageUtil.setMessage("Test notification sent successfully !","color:blue","TOP");
			}
	}
	
	public static int getRandomNumberInts(){
	    Random r = new Random( System.currentTimeMillis() );
	    return 10000 + r.nextInt(20000);
	}
	
	 private static String postToURL(String url, String message, DefaultHttpClient httpClient) throws IOException, IllegalStateException, UnsupportedEncodingException {
		 StringBuffer totalOutput = new StringBuffer();
		 try {
	        HttpPost postRequest = new HttpPost(url);
	        StringEntity input = new StringEntity(message,"UTF-8");
	                 
	       // HttpHeaders headers = new HttpHeaders();
	        //headers.set("content-type", "application/json; charset=utf-8");
	        //headers.set("Authorization", optcultureFireBaseServerKey);
	        input.setContentType("application/json");
	        postRequest.setEntity(input); 
	        postRequest.addHeader("Content-Type", "application/json; charset=utf-8");
	        postRequest.addHeader("Authorization", optcultureFireBaseServerKey);

	        HttpResponse response = httpClient.execute(postRequest);
	        logger.info("message==>1"+message);
	        if (response.getStatusLine().getStatusCode() != 200) {
	        	logger.error("Failed : HTTP error code : "+ response.getStatusLine().getStatusCode());
	        } 
	  
	        BufferedReader br = new BufferedReader(
	                new InputStreamReader((response.getEntity().getContent())));
	 
	        String output;
	       
	        while ((output = br.readLine()) != null) {
	            totalOutput.append(output);
	        }
		 }catch (RuntimeException e) {
			 logger.error("Failed : HTTP error code : "+ e);
		}catch (Exception e) {
			 logger.error("Exception while sending test notification "+ e);
		}
		return totalOutput.toString();
	    }
	 
	 public void onClick$gotoStep2BtnId() {
			 if(!validateAndSave(step1DivId,false)) {
				return;
			 }
		 		configureDivDefaultChanges(step2DivId);
		 		Clients.evalJavaScript("changeStep(2, true);");
		}
	 
	 private boolean validateAndSave(Div currentDiv, boolean draft) {
		 String notificationCampName = notificationCampaignNameId.getValue().trim(); 
		 if(currentDiv.getId().equals(step1DivId.getId())) {
		 /**** validates Notification name *****/
			if(notificationCampaignNameId.isValid()) {
				
				if(notificationCampName.trim().length() == 0) {
					MessageUtil.setMessage("Enter Notification campaign name.Name cannot be left empty.","color:red","TOP");
					return false;
				}
				if(!Utility.validateName(notificationCampName)) {
					MessageUtil.setMessage("Enter valid Notification campaign name.Special characters are not allowed.","color:red","TOP");
					return false;
				}
				if(notification == null) {
					boolean nameExists =  notificationDao.checkName(notificationCampName,currentUser.getUserId());
					
					if(nameExists){
						
						MessageUtil.setMessage("This Notification campaign name is already in use.Please choose a different name.", "color:red", "TOP");
						notificationCampaignNameId.setFocus(true);
						return false;
					} // else if
				}
			} // if
			else{
				MessageUtil.setMessage("Provide valid Notification campaign name.", "color:red", "TOP");
				notificationCampaignNameId.setFocus(true);
				return false;
			}
			
			
			if ((notification != null && notification.getBannerImageUrl() != null) || (img.getSrc() != null || img.getContent() != null)) {
				logger.info("loaded image");
			} else {
				MessageUtil.setMessage("Please upload Banner Image.", "color:red", "TOP");
				return false;
			}

			if (imgLogo.getSrc() != null || imgLogo.getContent() != null) {
				logger.info("loaded image");
			} else {
				MessageUtil.setMessage("Please upload Logo Image.", "color:red", "TOP");
				return false;
			}
			 
			
			String headerContent = headerId.getValue();
			if(headerContent != null && headerContent.isEmpty() && headerContent.trim().length() == 0 ) {
				MessageUtil.setMessage("Notification header cannot be left empty. Please provide header text.", "color:red;", "top");
				return false;
			}
			
			String messageContent = notificationBodyId.getValue();
			if(messageContent != null && messageContent.isEmpty() && messageContent.trim().length() == 0 ) {
				MessageUtil.setMessage("Notification message cannot be left empty. Please provide message text.", "color:red;", "top");
				return false;
			}
			
			String isValidPhStr = Utility.validatePh(messageContent.trim(),GetUser.getUserObj());
			if(isValidPhStr != null) {
				MessageUtil.setMessage("Placeholder "+isValidPhStr+ " is invalid please replace with proper placeholder values.", "color:red;", "top");
				return false;
			}
			
			//validation on DC mergetags
			Set<String> couponPhSet = SmsCampSettingsController.findCoupPlaceholders(messageContent.trim());
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
						Long orgId = currentUser.getUserOrganization().getUserOrgId();
						
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
				List<Coupons> inValidCoupList = couponsDao.isExpireOrPauseCoupList(couponIdStr, currentUser.getUserOrganization().getUserOrgId());
				
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
			
			if(notification == null){
				notification = new Notification();
				notification.setCreatedDate(cal);
				notification.setModifiedDate(cal);
			}else {
				notification.setModifiedDate(cal);
			}
			notification.setNotificationName(notificationCampName);
			notification.setHeader(headerContent);
			
			/*********** Added for campaign categories**********/
			
			String website = redirectUrlId.getValue().trim();
			StringBuilder sbWebsite = new StringBuilder(website);
			if(website!=null && !website.isEmpty() && (!website.contains("http://")) && (!website.contains("https://"))) {
				sbWebsite.insert(0,"http://");
			}
			if(website!= null && website.trim().length() > 0 && !validateUrl(sbWebsite.toString().trim())){
				MessageUtil.setMessage("Please provide valid Website URL.", "color:red", "TOP");
				return false;
			}
			
			notification.setRedirectUrl(sbWebsite.toString().trim());
			
			Long categoryName =null;
			if(!currentUser.getSubscriptionEnable())
				categoryLbId.setDisabled(true);
			
			if(currentUser.getSubscriptionEnable()){
				Listitem catgoryItem = categoryLbId.getSelectedItem();
				if(catgoryItem.getIndex() != 0) {
					UserCampaignCategories userCampaignCategories = (UserCampaignCategories)catgoryItem.getValue();
					categoryName = userCampaignCategories.getId();
				}
			}
			notification.setCategory(categoryName);
			
			/**********************ended****************/
			notification.setNotificationContent(messageContent);
			notification.setUserId(currentUser.getUserId());
			
			NotificationCampaignReportDao notificationCampaignSentDao = (NotificationCampaignReportDao) SpringUtil.getBean("notificationCampaignReportDao");
			if((img!=null && img.getContent()!=null)) {
					 String imageName = (String)img.getAttribute("imageName");
					 String path = usersParentDirectory + File.separator +  currentUser.getUserName() + notificationBannerImageDirectory +notificationCampName;
						File file = new File(path);
						if(!file.exists()) {
							file.mkdirs();
						}
				/*
				 * if (notification != null && notification.getBannerImageUrl() != null &&
				 * !notification.getBannerImageUrl().isEmpty()) { String deleteImagePath =
				 * usersParentDirectory + "/" + currentUser.getUserName() +
				 * notificationBannerImageDirectory +notification.getNotificationName()+"/"+
				 * notification.getBannerImageUrl(); File fileDeletePath = new
				 * File(deleteImagePath); if (fileDeletePath.exists()) {
				 * List<NotificationCampaignReport> reportList =
				 * notificationCampaignSentDao.getNotificationSentByUserId(currentUser.getUserId
				 * (),notificationCampName,notification.getBannerImageUrl()); if(reportList ==
				 * null || reportList.isEmpty()) { fileDeletePath.delete(); } } }
				 */
				String imgPath = path + File.separator +imageName;
				File file1  = new File(imgPath);
				if(file1.exists()) {
					imageName = getcountFormImageName(imageName);
					img.removeAttribute("imageName");
					img.setAttribute("imageName",imageName);
					//MessageUtil.setMessage("Image "+imageName+" already exists.","color:red","TOP");
					//return false;
				}
				 if(imageName!=null) {
					 notification.setBannerImageUrl(imageName);
				 }else {
					 return false;
				 }
		}
			

			if((imgLogo!=null && imgLogo.getContent()!=null)) {
					 String logoName = (String)imgLogo.getAttribute("logoName");
					 String path = usersParentDirectory + File.separator +  currentUser.getUserName() + notificationBannerLogoDirectory +notificationCampName ;
						File file = new File(path);
						if(!file.exists()) {
							file.mkdirs();
						}
				 
				 if(notification!=null && notification.getLogoImageUrl()!=null && !notification.getLogoImageUrl().isEmpty()) {
						String deleteImagePath = usersParentDirectory + "/" +  currentUser.getUserName() + notificationBannerLogoDirectory +notification.getNotificationName()+"/"+ notification.getLogoImageUrl();
							File fileDeletePath = new File(deleteImagePath);
							if(fileDeletePath.exists()) {
								List<NotificationCampaignReport> reportList = notificationCampaignSentDao.getNotificationSentByUserId(currentUser.getUserId(),notificationCampName,notification.getLogoImageUrl());
								if(reportList == null || reportList.isEmpty()) {
									fileDeletePath.delete();
								}
							}
						}
				 String imgPath = path + File.separator +logoName;
					File file1  = new File(imgPath);
					if(file1.exists()) {
						logoName = getcountFormImageName(logoName);
						imgLogo.removeAttribute("logoName");
						imgLogo.setAttribute("logoName",logoName);
						//MessageUtil.setMessage("Image "+logoName+" already exists.","color:red","TOP");
						//return false;
					}
				 
				 if(logoName!=null) {
					 notification.setLogoImageUrl(logoName);
				 }else {
					 return false;
				 }
		}
			
			if(gotoStep2BtnId.getLabel().equalsIgnoreCase("Next")) {
				notification.setStatus(Constants.CAMP_STATUS_DRAFT);
				
				if(notification.getDraftStatus() == null ){
					notification.setDraftStatus(Constants.SMS_CAMP_DRAFT_STATUS_STEP_TWO);
				}
				notification.setUserId(currentUser.getUserId());
				notificationDaoForDML.saveOrUpdate(notification);
				if(img!=null && img.getContent()!=null && img.getAttribute("imageName")!=null) {
					try {
						upload(img.getContent(),(String)img.getAttribute("imageName"),notificationBannerImageDirectory,notificationCampName);
					} catch (Exception e) {
						logger.error("Exception :"+e);
					}
				}
				if(imgLogo!=null && imgLogo.getContent()!=null && imgLogo.getAttribute("logoName")!=null) {
					try {
						upload(imgLogo.getContent(),(String)imgLogo.getAttribute("logoName"),notificationBannerLogoDirectory,notificationCampName);
					} catch (Exception e) {
						logger.error("Exception :"+e);
					}
				}
			}
			
			if(!draft){
				Clients.evalJavaScript("changeStep(2, true);");
				notificationDaoForDML.saveOrUpdate(notification);
				if(img!=null && img.getContent()!=null && img.getAttribute("imageName")!=null) {
					try {
						upload(img.getContent(),(String)img.getAttribute("imageName"),notificationBannerImageDirectory,notificationCampName);
					} catch (Exception e) {
						logger.error("Exception :"+e);
					}
				}
				if(imgLogo!=null && imgLogo.getContent()!=null && imgLogo.getAttribute("logoName")!=null) {
					try {
						upload(imgLogo.getContent(),(String)imgLogo.getAttribute("logoName"),notificationBannerLogoDirectory,notificationCampName);
					} catch (Exception e) {
						logger.error("Exception :"+e);
					}
				}
			}else{
				notification.setStatus(Constants.CAMP_STATUS_DRAFT);
				if(notification.getDraftStatus() == null )
					notification.setDraftStatus(Constants.SMS_CAMP_DRAFT_STATUS_STEP_TWO);
				notificationDaoForDML.saveOrUpdate(notification);
				Redirect.goTo(PageListEnum.CAMPAIGN_VIEW_OFFERNOTIFICATION_LIST);
			}
		 } if(currentDiv.getId().equals(step2DivId.getId())) {
			 	if(configurelistRgId.getSelectedIndex() == 0) {
					if(saveMlist() == false) return false;
				}else if(configurelistRgId.getSelectedIndex() == 1) {
					if(saveSegRule() == false) return false;
				}
			 	campNameLblId.setValue(notification.getNotificationName());
			 	
			 	Long categoryId= notification.getCategory();
			 	UserCampaignCategories userCmapObj  = null;
				if(categoryId != null){
					
					userCmapObj =userCampaignCategoriesDao.findById(categoryId.toString());
				}
				if(currentUser.getSubscriptionEnable() &&(userCmapObj != null && userCmapObj.getIsVisible())){
					
					categoryNameLblId.setValue(userCmapObj.getCategoryName());
					
				}else categoryNameLblId.setValue("");
				setMlistLinks();
		 }
			
			return true;
	 }
	 
	 
	 private boolean validateUrl(String website) {
			try { 
				//(new java.net.URL(website)).openStream().close();
						StringBuilder result = new StringBuilder();
				        String line;
				        boolean receivedHtml;
				        URLConnection urlConnection = new URL(website).openConnection();
				        urlConnection.addRequestProperty("User-Agent", "Mozilla/5.0");
				        urlConnection.setReadTimeout(5000);
				        urlConnection.setConnectTimeout(5000);

				        try (//InputStream is = new URL(website).openStream();
				        	InputStream is = urlConnection.getInputStream();
				             BufferedReader br = new BufferedReader(new InputStreamReader(is))) {

				            while ((line = br.readLine()) != null) {
				                result.append(line);
				            }
				            is.close();
				        }

				        receivedHtml = result.toString()!=null && !result.toString().isEmpty() ? true:false;
				        return receivedHtml;
			}catch (Exception e) {
				return false;
			}
		}
	 
	public void upload(Media media, String imageName,String directoryPath,String notificationCampName) throws Exception {
			MessageUtil.clearMessage();
				try{
					String path = usersParentDirectory + "/" +  currentUser.getUserName() + directoryPath + notificationCampName +"/";
					File file = new File(path);
					
					if(!file.exists()) {
						file.mkdirs();
					}
					byte[] fi = media.getByteData();
					BufferedInputStream in = new BufferedInputStream (new ByteArrayInputStream (fi)); 
					FileOutputStream out = new FileOutputStream (new File(file.getPath()+"/"+imageName));
					//Copy the contents of the file to the output stream
					byte[] buf = new byte[1024];
					int count = 0;
					while ((count = in.read(buf)) >= 0){
						out.write(buf, 0, count);
					}
					in.close();
					out.close();
					}catch (Exception e) {
						logger.error("Exception :: error occured while Uploading the Image"+ e);
				}
		}
	 
	 public void onClick$msgPreviewImgId() {
			if(notification != null) {
				previewWin$html.setContent(notification.getNotificationContent());
				previewWin.setVisible(true);
			}
		}
	 
	 public void onClick$editMsgImgId() throws Exception {
			notificationIsEdit = "edit";

			saveAsDraftStep1BtnId.setVisible(false);
			gotoStep2BtnId.setLabel("Save");
			step1BackBtnId.setVisible(true);
			Clients.evalJavaScript("changeStep(1, true);");		
			notificationBodyId.setFocus(true);
			
		}

	private boolean saveMlist() {
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
		
		Set<MailingList> mlSet = new HashSet<MailingList>();
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
		
		notification.setListType("Total");
		notification.setMailingLists(mlSet);
		
		if(!otherCampSettings()) {
			return false;
		}
		return true;
	}
	
	public void onClick$saveAsDraftStep2BtnId() throws Exception {
		if(configurelistRgId.getSelectedIndex() == 0) {
			if(saveMlist()==false)
			Redirect.goTo(PageListEnum.CAMPAIGN_VIEW_OFFERNOTIFICATION_LIST);	
		}
		else if(configurelistRgId.getSelectedIndex() == 1) {
			if(saveSegRule() == false) return;
		}
		Redirect.goTo(PageListEnum.CAMPAIGN_VIEW_OFFERNOTIFICATION_LIST);
	}
	
	public void onClick$backStep1ButtonId() {
		if(notificationIsEdit!=null){
			if(notificationIsEdit.equalsIgnoreCase("edit"))
				Clients.evalJavaScript("changeStep(3,true)");
			else if(notificationIsEdit.equalsIgnoreCase("view")){
				Redirect.goTo(PageListEnum.CAMPAIGN_VIEW_OFFERNOTIFICATION_LIST);
			}
		}else{
			Clients.evalJavaScript("changeStep(1,true)");
		}
	}
	
	public void onClick$editNotificationListImgId() throws Exception {
		notificationIsEdit = "edit";
		saveAsDraftStep2BtnId.setVisible(false);
		gotoStep3BtnId.setLabel("Save");
		Clients.evalJavaScript("changeStep(2, true);");		
		
	}
	
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
		
		
		notification.setListType("Segment"+Constants.DELIMETER_COLON+segRuleIds);
		
		//can avoid setting the set of mailing lists to the campaign
		Set<MailingList> mlSet = new HashSet<MailingList>();
		
		String listnamesStr = "";
		
		
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
		
		notification.setMailingLists(mlSet);
		
		if(!otherCampSettings()) {
			return false;
		}
		
		return true;
		
		
	}
	

	private boolean otherCampSettings() {
		
		long notificationCount = 0;
		long totalCount = 0;
		//long unpurgedCount = 0;
		Set<MailingList> mlSet = notification.getMailingLists();
		totalCount = contactsDao.getAllNotificationCount(mlSet);
		
		
		//campaign.setCouponFlag(false);
		MessageUtil.clearMessage();
		String segmentStr = notification.getListType();
		
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
			for (SegmentRules segmentRules : segmenRules) {
				
				Set<MailingList> mlistSet = new HashSet<MailingList>();
				List<MailingList> mlList = mailingListDao.findByIds(segmentRules.getSegmentMlistIdsStr());
				if(mlList == null) {
					continue;
				}
				
				mlistSet.addAll(mlList);
				long mlsbit = Utility.getMlsBit(mlistSet);
				segmentStr = SalesQueryGenerator.generateListSegmentCountQuery(segmentRules.getSegRule(),false, Constants.SEGMENT_ON_NOTIFICATION, mlsbit);
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
			
			notificationCount = contactsDao.getSegmentedContactsCount(tempQry);
			
			if(notificationCount == 0) {
				MessageUtil.setMessage("Your segment returned 0 unique contacts of "+ totalCount + " available contacts." , "color:red", "TOP");
				return false;
			}
		
		}//if segment
		else {
			notificationCount = totalCount;
			if(totalCount == 0) {
				MessageUtil.setMessage("Your selection returned 0 unique contacts of available contacts.", "color:red", "TOP");
				return false;
			}
		}
		
		// Check if user email count is sufficient to send campaign
		if(notificationIsEdit!=null) {
			if(notificationIsEdit.equalsIgnoreCase("view")) {
				notification.setStatus("Draft");
				//campaign.setDraftStatus("CampLayout");
				
				
				if(notificationDraftStatus != null && notificationDraftStatus.equals(Constants.SMS_CAMP_DRAFT_STATUS_STEP_TWO)){
					notification.setDraftStatus(Constants.SMS_CAMP_DRAFT_STATUS_STEP_THREE);
				}
				 //
				
			}
			else if(notificationIsEdit.equalsIgnoreCase("edit")){
				notification.setModifiedDate(Calendar.getInstance());
			}
		}
		else {
			notification.setStatus("Draft");
			// campaign.setDraftStatus("CampLayout");
			
			if(notificationDraftStatus != null && notificationDraftStatus.equals(Constants.SMS_CAMP_DRAFT_STATUS_STEP_TWO)) {
				notification.setDraftStatus(Constants.SMS_CAMP_DRAFT_STATUS_STEP_THREE);
			}
		}
			notification.setUserId(currentUser.getUserId());
		try {
			int confirm = Messagebox.show("Total "+notificationCount+" unique contacts have been configured. Do you want to continue?", "Confirm", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
			if(confirm != 1){
				return false;
			}
		} catch (Exception e1) {
			logger.error("Exception ::",e1);
			return false;
		}
		notificationDaoForDML.saveOrUpdate(notification);
		return true;
		
		
	}
	
public void onClick$saveAsDraftBtnId() {
		
		if(session!=null || currentUser != null){
			notification.setStatus(Constants.CAMP_STATUS_DRAFT);
			notification.setDraftStatus(Constants.SMS_CAMP_DRAFT_STATUS_STEP_COMPLETE);
			notificationDaoForDML.saveOrUpdate(notification);
			for(NotificationSchedule campaignSchedule : notificationScheduleList){
				if(campaignSchedule.getStatus() == 0 || campaignSchedule.getStatus() == 2){
				campaignSchedule.setStatus((byte)2);
				}
				else{
					continue;
				}
			}
			notificationScheduleDaoForDML.saveByCollection(notificationScheduleList);
			MessageUtil.setMessage("Notification campaign is saved successfully.", "color:green;", "top");
			Redirect.goTo(PageListEnum.CAMPAIGN_VIEW_OFFERNOTIFICATION_LIST);
			
		}
		else{
			MessageUtil.setMessage("Problem encountered while saving. Please re-login and try again.", "color:red", "TOP");
			logger.error("** Exception : session is null ** ");
		}//else
		
	}
	
	
	public void onCheck$configurelistRgId() {
		if(configurelistRgId.getSelectedIndex() == 0){
			mlDivId.setVisible(true);
			segDivId.setVisible(false);
		}else if(configurelistRgId.getSelectedIndex() == 1) {
			mlDivId.setVisible(false);
			segDivId.setVisible(true);
		}
}

	

	public void onClick$gotoStep3BtnId() {
		if(! validateAndSave(step2DivId, false)) { 
			 Redirect.goTo(PageListEnum.CAMPAIGN_VIEW_OFFERNOTIFICATION_LIST);
		 }else {
			 Clients.evalJavaScript("changeStep(3, true);");
		 }
	 }
	 
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
				if(notification != null && notificationIsEdit !=null ) {
					
					String listType = notification.getListType();
					if(listType != null) {
						if(listType.equals("Total")) {
							enableMailingList();
						}else{
							enableSegment();
						}
					}
				}
			}
			
		}
	 
	 public void enableMailingList() {
			String mlListName = "";
			String listNamesStr="";
			configurelistRgId.setSelectedIndex(0);
			Vector<String> mlListNamesVector = new Vector<String>();
			Set<MailingList> mailingListsSet = notification.getMailingLists();
			
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
	 
	 public void enableSegment() {
			String segRule = notification.getListType();
			if(segRule != null && !segRule.equalsIgnoreCase("Total") && segRule.startsWith("Segment")) {
				
				String segRuleId = segRule.split(""+Constants.DELIMETER_COLON)[1];
				List<SegmentRules> segmenRules = segmentRulesDao.findById(segRuleId);
				if(segmenRules == null) {
					
					MessageUtil.setMessage("Configured segment no longer exists. You might have deleted it.", "color:red;");
					configurelistRgId.setSelectedIndex(1);
					onCheck$configurelistRgId();
					return ;
				}
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

	 
	 
	 public List<MailingList> getMailingLists() {
			List<MailingList> mlLists = null;
			try {
				mlLists = mailingListDao.findByIds(listIdsSet); 
			}catch ( Exception e) {
				logger.error("Exception ::" , e);
			}
			return mlLists;
		}
	
	 public List<SegmentRules> getSegmentRules() {
			List<SegmentRules> segLists = null;
			try {
				segLists = segmentRulesDao.findByIds(segmentIdsSet); 
			}catch (Exception e) {
				logger.error("** Exception : " + e);
			}
			return segLists;
		}
	 
	 	private Image helpImgId;
		public void getCampCategorties(){
				List<UserCampaignCategories> userCategoriesList= userCampaignCategoriesDao.findCatByUserId(currentUser.getUserId());
				Listitem campCategory = null;
				if(userCategoriesList == null || userCategoriesList.size() == 0 ){
					categoryLbId.setDisabled(true);
				}
				else{
					for (UserCampaignCategories userCampaignCategories : userCategoriesList) {
						campCategory = new Listitem(userCampaignCategories.getCategoryName(),userCampaignCategories);
						campCategory.setParent(categoryLbId);
					}
					categoryLbId.setDisabled(false);
				}
				helpImgId.setVisible(categoryLbId.isDisabled());
				if(categoryLbId.getItemCount() > 0 ) categoryLbId.setSelectedIndex(0);
			}
		
		public void onClick$saveAsDraftStep1BtnId() {
			if(! validateAndSave(step1DivId, true)) 
				return;
		}
		
		private void editNotificationCampaign(Notification notification) {
			Calendar cal = Calendar.getInstance();
			notificationCampaignNameId.setValue(notification.getNotificationName());
			if(!currentUser.getSubscriptionEnable())categoryLbId.setDisabled(true);
			Long category = notification.getCategory();
			UserCampaignCategories userCmapObj = null;
			if(category != null){
				 userCmapObj =userCampaignCategoriesDao.findByCatId(category, currentUser.getUserId());
			}
			String categoryName ="";
			if(userCmapObj != null){
				 categoryName = userCmapObj.getCategoryName();
				 for(Listitem item : categoryLbId.getItems()) {
					 
					 if(item.getLabel().equals(categoryName)) {
						 item.setSelected(true);
						 break;
					 }
				 }
				 
			}
			headerId.setValue(notification.getHeader());
			redirectUrlId.setValue(notification.getRedirectUrl());
			notificationBodyId.setValue(notification.getNotificationContent());
			if(notification.getBannerImageUrl()!=null && !notification.getBannerImageUrl().isEmpty()) {
				img.setSrc("/UserData/"+currentUser.getUserName()+notificationBannerImageDirectory+notification.getNotificationName()+"/"+notification.getBannerImageUrl());
			}
			if(notification.getLogoImageUrl()!=null && !notification.getLogoImageUrl().isEmpty()) {
				imgLogo.setSrc("/UserData/"+currentUser.getUserName()+notificationBannerLogoDirectory+notification.getNotificationName()+"/"+notification.getLogoImageUrl());
			}
			Clients.evalJavaScript("displayIndivOnKeyup()");
			Clients.evalJavaScript("displayBodyOnKeyup()");
			//2nd step
			configureDivDefaultChanges(step2DivId);
			
			/****get the existing schedules(if any) ********/
			notificationScheduleList = notificationScheduleDao.getByNotificationCampaignId(notification.getNotificationId());

			if(notificationScheduleList.size()>0) {
				//show the rows in the grid according to the existing schedules
				loadSchedule();
				/**
				 * Process Active & Archived Schedules
				 */
				//Active
				List<NotificationSchedule> activeCampSchedList = getActiveCampScheduleList(notificationScheduleList);
				if(activeCampSchedList != null && activeCampSchedList.size() > 0){
					createRowUpComingListBox(activeCampSchedList.get(0), 0, true);
					createDivUpComingCampaigns(true, activeCampSchedList.get(0));
				}
				else{
					createDivUpComingCampaigns(false, null);
				}
				
				//Archived
				List<NotificationSchedule> archiveCampSchedList = getArchivedCampScheduleList(notificationScheduleList);
				
				if(archiveCampSchedList != null &&  archiveCampSchedList.size() > 0){
					drawArchivedDiv(archiveCampSchedList.get(0));
					drawSentListBox(archiveCampSchedList.get(archiveCampSchedList.size()-1));
				}
				else{
					campaignSentDivId.setVisible(false);
				}
			}
			if(notificationDraftStatus != null && notificationDraftStatus.equals(Constants.SMS_CAMP_DRAFT_STATUS_STEP_TWO)) {
				Clients.evalJavaScript("changeStep(2, true);");
			}
			
			if(notification.getDraftStatus().equals(Constants.SMS_CAMP_DRAFT_STATUS_STEP_THREE) ||
					notification.getDraftStatus().equals(Constants.SMS_CAMP_DRAFT_STATUS_STEP_COMPLETE) ){
				
				logger.debug("----SMS_CAMP_DRAFT_STATUS_STEP_THREE----");
				
				
				campNameLblId.setValue(notification.getNotificationName());
				
				// Added for campaign categories..
				if(currentUser.getSubscriptionEnable()){
					if( notification.getCategory() != null ){
						
						Long catId= notification.getCategory();
						UserCampaignCategories userCmapaignObj =userCampaignCategoriesDao.findByCatId(catId, currentUser.getUserId());
						if(userCmapObj != null){
							
							String categoryNameStr =userCmapaignObj.getCategoryName();
							categoryNameLblId.setValue(categoryNameStr);
							
						}
					}	
					
				}
				
				setMlistLinks();
				
				step2AId.setSclass("req_step_completed");
				Clients.evalJavaScript("changeStep(3, true);");
			}
			
			notification.setModifiedDate(cal);
			
		}
		
		@SuppressWarnings("unchecked")
		private void setMlistLinks() {
			
			if(notification == null) return;
			
			if(notification.getListType().startsWith("Segment:"))
			{
				Components.removeAllChildren(listNamesDivId);
				String segment=notification.getListType();
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
			Set<MailingList> mlset = notification.getMailingLists();
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
		
		private void drawSentListBox(NotificationSchedule notificationSchedule) {
			Listitem li = new Listitem();
			Listcell lc = new Listcell();
			viewAllArchivedSchedulesWinId$noRecordsArchivedLbId.setVisible(false);
			viewAllArchivedSchedulesWinId$viewMoreArchievedSchedAnchId.setVisible(true);
			viewAllArchivedSchedAnchId.setVisible(true);
			li.setValue(notificationSchedule);
			lc.setParent(li);
			//Date
			lc = new Listcell();
			lc.setLabel(MyCalendar.calendarToString(notificationSchedule.getScheduledDate(),MyCalendar.FORMAT_DATE_FORMAT,(TimeZone) sessionScope.get("clientTimeZone")));
			lc.setParent(li);
			//Status
			lc = new Listcell();
			lc.setLabel(notificationSchedule.getStatusStr());
			lc.setParent(li);
			li.setHeight("30px");
			li.setParent(sentCampaingsListlbId);
		}

		private List<NotificationSchedule> getArchivedCampScheduleList(List<NotificationSchedule> campScheduleList2) {
			List<NotificationSchedule> archiveCampSchedule = new ArrayList<NotificationSchedule>();
			for (NotificationSchedule campaignSchedule : campScheduleList2) {
				if(!(campaignSchedule.getStatus() == 0)  && !( campaignSchedule.getStatus() == 2 )) {
					archiveCampSchedule.add(campaignSchedule);
				}
			}
			return archiveCampSchedule;
		}
		
		private void drawArchivedDiv(NotificationSchedule campaignSchedule){
			if(!(0 == campaignSchedule.getStatus()) && !( 2 == campaignSchedule.getStatus())){
				//	logger.info("campaignSentDivId.setVisible(true)");
				campaignSentDivId.setVisible(true);
				campaignSentLbId.setValue(MyCalendar.calendarToString(campaignSchedule.getScheduledDate(), MyCalendar.FORMAT_DATE_FORMAT,(TimeZone) sessionScope.get("clientTimeZone")));
				campaignSentLbId.setStyle(OCConstants.FONT_BOLD);
			}
			else{
				campaignSentDivId.setVisible(false);
			}
		}
		
		public void loadSchedule() {
			try{
				
				logger.debug("----just entered----");
				Long id = null;
				if(notificationScheduleList == null){
					return;
				}
				NotificationSchedule tempSchedule = null;
				
				/****** creates the row for each schedule *******/
				for( NotificationSchedule notificationCampaignSchedule : notificationScheduleList){
					tempList.add(notificationCampaignSchedule.getScheduledDate());
					/****** create rows(children if any) according to the existing schedules********/
					createRow(notificationCampaignSchedule,null);
					
				}//for each
				
				Row tempRow;
				
				for( NotificationSchedule notificationCampaignSchedule: notificationScheduleList) {
					
					tempRow = rowMap.get(notificationCampaignSchedule.getScheduledDate());
					
					if(notificationCampaignSchedule.getParentId() == null) {
						

						tempRow.setParent(schedGrdRowsId);
					}
					else {
						id= notificationCampaignSchedule.getParentId();
						tempSchedule = notificationScheduleDao.findById(id);
						tempRow.setParent(rowsMap.get(tempSchedule.getScheduledDate()));
						
					}//else
					
				} // for each
			}catch ( Exception e) {
				logger.error("** Exception while creating a row ",e);
			}
			
		}
		
		public void onSelect$dispMlListsLBoxId() throws Exception {
			
			Set<Listitem> selectedItems = dispMlListsLBoxId.getSelectedItems();
			String listNamesStr="";
			for (Listitem li : selectedItems) {
				
				MailingList mailingList = (MailingList) li.getValue();			
				if(listNamesStr.length() != 0) listNamesStr+=",";
				listNamesStr += mailingList.getListName() ;
			}
			
			selMlLblId.setValue(listNamesStr);
			if(selMlLblId.getValue().length() != 0) {
				selectedListDivId.setVisible(true);
			}else{
				selectedListDivId.setVisible(false);
			}
		}
		
		int activeCount;
		int sentCount;
		public void setnotificationStatus() {
			
			try{
				 if(activeCount >0 && sentCount > 0){
						notification.setStatus(Constants.CAMP_STATUS_RUNNING);
					}
					else if(activeCount == 0 && sentCount > 0) {
						notification.setStatus(Constants.CAMP_STATUS_SENT);
					}
					else if(activeCount >0 && sentCount == 0){
						notification.setStatus(Constants.CAMP_STATUS_ACTIVE);
					}else{
						notification.setStatus(Constants.CAMP_STATUS_DRAFT);
					}
				 if(session!=null || currentUser != null){
					 notificationDaoForDML.saveOrUpdate(notification);
				 }
			}catch (Exception e) {
				logger.error("**Exception while setting the status to the notification campaign",e);
			}
		}
		
		
		public void onClick$goToNextBtnId() {
			try {
				if(currentUser!=null && currentUser.getSubscriptionEnable() && notification.getCategory() ==null){
					MessageUtil.setMessage("You have currently enabled subscriber preference center setting. " +
							"\n please go to the 1st step and select a campaign category first and try again.", "color:red");
					return;
				}
				// for validating atleast on contact is present or not
				if(!otherCampSettings()) return ;
				
				if(schedGrdRowsId.getChildren().size()>0){ // if added active schedules are there
					
					saveNotificationCampSchedule(schedGrdRowsId); // saves the NotificationCampaignSchedules according to the resend level(if any)
					
					if(activeCount > 0){
						if(pastDate){
							pastDate = false;
							return;
						}
						MessageUtil.setMessage("notification scheduled successfully.", "color:green;");
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
					setnotificationStatus(); // set the status to NotificationCampaign based on the NotificationCampaignSchedule(s) status
		
					//Redirect.goTo(PageListEnum.CAMPAIGN_VIEW_Notification_CAMPAIGNS);
					Redirect.goTo(PageListEnum.CAMPAIGN_VIEW_OFFERNOTIFICATION_LIST);
					
				}
				else{
					
					MessageUtil.setMessage("No active notification campaign schedules are present." +
							"Please add at least one schedule.", "color:red", "top");
					return ;
				}//else
			} catch (Exception e) {
				logger.error("Exception ::" , e);
			}

		
		}

		private void saveNotificationCampSchedule(Rows rows) {

			
			try{
				if(rows != null){
					/******** for resend level 0 ******/
					List<Component> rowList = new ArrayList<Component>();
					List<NotificationSchedule> passList = new ArrayList<NotificationSchedule>();
					List<NotificationSchedule> activeList = new ArrayList<NotificationSchedule>();
					
					rowList = rows.getChildren();
					NotificationSchedule notificationSchedule;
					
					
					UserCampaignExpiration triggeredEmail = getTriggeredAlertEmail();
					Calendar expiredOn = null;
					if(triggeredEmail != null){

						expiredOn = triggeredEmail.getSentOn();
						if(expiredOn != null)
						expiredOn.add(Calendar.DAY_OF_YEAR, 7);

					}

					int expiredSchedules = 0 ;
					//byte expiredCount = 0;
					List<String> expriedScheduleDates = new ArrayList<String>();
					/*****save the notificationCampaignSchedules whose resend level is 0 *****/
					for(Component eachComp : rowList) {
						Row row =(Row)eachComp;
						if(row.isVisible()){
							notificationSchedule = (NotificationSchedule)row.getValue();
							if(notificationSchedule.getStatus() == 0){
								activeCount++;
							}else if(notificationSchedule.getStatus() == 1){
								sentCount++;
							}
							if(notificationSchedule.getResendLevel()==0) {
								passList.add(notificationSchedule);
								
							}//if
							
						}//if
					}//for
					
					
					boolean isDraft =false,draftStatus=false;//Calendar ;//expiredOn = null;
					Calendar currentCal = Calendar.getInstance();
					for(NotificationSchedule campSchedule : passList){


						//if campSchedule status is sent no need to do anything
						if(campSchedule.getStatus() == 1) {
							sentCount++;
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

						//if submit and campSchedule status is draft changing it to active
						if(!draftStatus && (campSchedule.getStatus() == 0 || campSchedule.getStatus() == 2 )) {
							//campSchedule.setStatus((byte)0);
							//activeCount++;
							//if status is draft schedule date is before current date make status as expired.

							if( campSchedule.getStatus() == 2 && campSchedule.getScheduledDate().before(currentCal)){
								isDraft = true;
								campSchedule.setStatus((byte)7);
								expiredSchedules ++;
								//expriedScheduleDates.add(MyCalendar.calendarToString(campSchedule.getScheduledDate(), MyCalendar.FORMAT_DATEONLY));
								expriedScheduleDates.add(MyCalendar.calendarToString(campSchedule.getScheduledDate(), MyCalendar.FORMAT_DATETIME_STYEAR));
							}
							else{
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
								schErrorLblId.setValue("Schedule dates can not be past dates");
								/*MessageUtil.setMessage("Schedule dates cannot be past dates.", 
									"color:red", "TOP");*/
								pastDate = true;
								return;
							}
						}
					
						
					}
					
					if(expiredSchedules > 0 && expriedScheduleDates.size() > 0){
						MessageUtil.setMessage(getRequiredMessage(expriedScheduleDates,activeList,expiredSchedules),  "color:blue");
					}
					
					
					notificationScheduleDaoForDML.saveByCollection(passList);

				}//if
				
			}catch (Exception e) {
				logger.error("Exception ::" , e);
			}//catch
		
		}
		
		private UserCampaignExpiration getTriggeredAlertEmail() {
			List<UserCampaignExpiration> existingTriggerList = userCampaignExpirationDao.findBy(notification.getUserId(), notification.getNotificationId());
			UserCampaignExpiration userCampaignExpiration = null;
			if(existingTriggerList != null && existingTriggerList.size() > 0){
				userCampaignExpiration = existingTriggerList.get(0);
			}
			return userCampaignExpiration;
		}
		
		private String getRequiredMessage(List<String> expriedScheduleDates, List<NotificationSchedule> passList, int expiredSchedules){
			
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

			return reqdMSgStrBfr.toString();
		}
		
		
		public void onClick$prtDtBtnId() {
			
			if(notification == null) { // to have the NotificationCampaign obj's id 
				logger.debug("no Root object found");
				return;
			}//if
			
			schErrorLblId.setValue("");
			schErrorLblId.setVisible(true);
			try {
				schErrorLblId.setValue("");
				if(prtDtBxId.getValue() == null) {
					schErrorLblId.setValue(" Please select the date");
					return;
				}
				Calendar tempCal= prtDtBxId.getServerValue();
				addDateToGrid(tempCal);
				//ENDS 13, to make similar as email campaign schedules 
			} 
			catch (Exception e) {
				logger.warn("Exception : Date is not selected" + e);
				schErrorLblId.setValue(" Please select the date");
				return;
			}
		}

		private void addDateToGrid(Calendar cal) {
			//STARTS 14, changed to make similar like email campaign schedules
			Calendar currCal = Calendar.getInstance();
			if(cal == null || cal.compareTo(currCal) < 0){
				schErrorLblId.setValue("Please select a future date and time");
				return;
			}

			NotificationSchedule notifCampSchedule = addDateCheck(cal, null,(byte)0);

			if(notifCampSchedule == null) {
				schErrorLblId.setValue("Date already sheduled");
				return;
			}
			int confirm = 0;
			boolean isSameDay = false;

			List<String> dates = new ArrayList<String>();

			for (NotificationSchedule campSched : notificationScheduleList) {
				
				String now = MyCalendar.calendarToString(cal, MyCalendar.FORMAT_DATEONLY,(TimeZone) sessionScope.get("clientTimeZone"));
				String sameSch = MyCalendar.calendarToString(campSched.getScheduledDate(), MyCalendar.FORMAT_DATEONLY,(TimeZone) sessionScope.get("clientTimeZone")); 
				
				if( now.equals(sameSch) && (campSched.getStatus() == 0 
						||  campSched.getStatus() == 2 )){
					dates.add(" "+MyCalendar.calendarToString(campSched.getScheduledDate(), MyCalendar.FORMAT_TIME,(TimeZone) sessionScope.get("clientTimeZone")));
				}
			}

			for (NotificationSchedule campSched : notificationScheduleList) {

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

				NotificationSchedule campSchedule = addDate(cal, null,(byte)0);

				if(campSchedule == null) {
					//schErrorLblId.setValue("Date already scheduled");
					MessageUtil.setMessage("Date already scheduled.", "color:red", "TOP");
					return;
				}

				//createRow(campSchedule, schedGrdRowsId,false);
				//Create ListBox First element
				int count = activeCampaingsListlbId.getItemCount();
				boolean isCreditOrExipry = createRowUpComingListBox(campSchedule,count,false);
				if(isCreditOrExipry == false){
					logger.error("Your Credit Limits are Exipred please contact support");
					return;
				}
				createDivUpComingCampaigns(true,campSchedule);
				createRow(campSchedule, schedGrdRowsId);
				MessageUtil.setMessage("You have added 1 schedule to be sent on "+MyCalendar.calendarToString(cal, MyCalendar.FORMAT_DATE_FORMAT,(TimeZone) sessionScope.get("clientTimeZone"))+".\n All your active schedules can be viewed by clicking on \n \'View All Upcoming Schedules\' link.", "color:blue");
			}
			tempList.add(cal);
		}
		
		@SuppressWarnings("unchecked")
		private void createRow( NotificationSchedule notifSchedule,  Rows rows) {
			try {
				 Row row = new Row();
				row.setValue(notifSchedule);
				
				if(rows != null) {
					row.setParent(rows);
				}
				else {
					rowMap.put(notifSchedule.getScheduledDate(), row);
				}
				
				if(notifSchedule.getResendLevel() < MAX_RESEND_LEVEL) {
					Detail detail = new Detail();
					detail.setOpen(false);
					detail.setParent(row);	
					detail.setStyle("display:none;");
					detail.addEventListener("onOpen", this);
					
					Grid grid = new Grid();
					Columns cols = new Columns();
					cols.setParent(grid);
					grid.setVisible(false);
					grid.setParent(detail);
					int padding = 3;
					if((notifSchedule.getResendLevel()+1) < MAX_RESEND_LEVEL){
						Column col = new Column();
						col.setWidth("3%");
						col.setParent(cols);
						padding = 0;
					}
					Column col = new Column("Date");
					col.setWidth((27 + padding) + "%");
					col.setParent(cols);
		
					col = new Column("Status");
					col.setWidth("18%");
					col.setParent(cols);
					
					col = new Column("Resend criteria");
					col.setWidth("17%");
					col.setParent(cols);
		
					col = new Column("Actions");
					col.setWidth("35%");
					col.setParent(cols);
					
					Rows tempRows = new org.zkoss.zul.Rows();
					tempRows.setParent(grid);
					if(notifSchedule.getNotificationCsId() == null){
						
					}
					//logger.debug("rows map is===>"+rowsMap);
					rowsMap.put(notifSchedule.getScheduledDate(), tempRows);
				
				}// if resend level

				Label tempLabel = new Label(notifSchedule.getDateStrByTimeZone(null,
										(TimeZone) session.getAttribute("clientTimeZone")));
				tempLabel.setParent(row);
				
				tempLabel = new Label(notifSchedule.getStatusStr());
				tempLabel.setParent(row);

				
				/**
				 * criteria will be 0 for root schedules for
				 * re send schedules only criteria will be > 0
				 *
				 **/
				
				if(notifSchedule.getCriteria() > 0) {
					tempLabel = new Label();
					if (notifSchedule.getCriteria() == 1) {
						tempLabel.setValue("Not opens");
					}
					else if (notifSchedule.getCriteria() == 2) {
						tempLabel.setValue("Not clicked");
					}
					tempLabel.setParent(row);
					
				}// if campaignSchedule.getParent()
				
				Hbox hbox = new Hbox();
				hbox.setParent(row);
				
				Toolbarbutton tbButton;

				/** * Delete toolbar button** */
				tbButton = new Toolbarbutton("Delete");
				tbButton.setTooltiptext("Delete from schedule");
				tbButton.setImage("/img/action_delete.gif");
				tbButton.setAttribute(TB_ACTION, NotificationWeb.TB_ACTION_DELETE);
				tbButton.addEventListener("onClick",myListener);
				
				tbButton.setParent(hbox);

				if(rows != null) {
					rows.invalidate();
				}
				
			} catch ( Exception e) {
				
				logger.error("** Exception : while creating a row ", e);
			}
		}
		
		@SuppressWarnings("unchecked")
		private boolean createRowUpComingListBox(NotificationSchedule campaignSchedule,int count, boolean loadDBSch) {
			try {
				if(campaignSchedule.getScheduledDate().after(currentUser.getPackageExpiryDate())) {
					MessageUtil.setMessage("Schedule date cannot be after your package expiry date.", "color:red", "TOP");
					return false;
				}

				Listitem li = new Listitem();
				Listcell lc = new Listcell();

				if(0 == campaignSchedule.getStatus() || 2 == campaignSchedule.getStatus()){

					//Schedule Date
					li.setValue(campaignSchedule);
					lc.setParent(li);
					lc = new Listcell();
					lc.setLabel(MyCalendar.calendarToString(campaignSchedule.getScheduledDate(),MyCalendar.FORMAT_DATE_FORMAT,(TimeZone) sessionScope.get("clientTimeZone")));
					lc.setParent(li);

					//Status
					lc = new Listcell();
					lc.setLabel(campaignSchedule.getStatusStr());
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
					}
					else{
						try{

							NotificationSchedule campSchedChkDate =   (NotificationSchedule) ((Listitem)activeCampaingsListlbId.getSelectedItem()).getValue();
							if(campaignSchedule.getScheduledDate().before(campSchedChkDate.getScheduledDate())){
								int count1 =  activeCampaingsListlbId.getItemCount();
								for(; count1>0; count1--) {
									activeCampaingsListlbId.removeItemAt(count-1);
								}

								if(0 == campaignSchedule.getStatus() || 2 == campaignSchedule.getStatus()){
									li = new Listitem();
									li.setSelected(true);
									//Schedule Date
									li.setValue(campaignSchedule);
									lc.setParent(li);
									lc = new Listcell();
									lc.setLabel(MyCalendar.calendarToString(campaignSchedule.getScheduledDate(),MyCalendar.FORMAT_DATE_FORMAT,(TimeZone) sessionScope.get("clientTimeZone")));
									lc.setParent(li);

									//Status
									lc = new Listcell();
									lc.setLabel(campaignSchedule.getStatusStr());
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

								}
							}
						}
						catch(Exception e){
							logger.info("Exception ::",e);
						}
					}
				}
				return true;
			} catch (Exception e) {
				logger.error("Exception ..........:",e);
				return false;
			}

		
		}

		private NotificationSchedule addDate( Calendar selectedDtCal, 
				NotificationSchedule parentCampSchedule,  byte criteria) {	
				logger.debug("-------- just entered---------");
				selectedDtCal.set(Calendar.SECOND,0);
				selectedDtCal.set(Calendar.MILLISECOND,0);
				
				Calendar tempCal = Calendar.getInstance();
				tempCal.setTime(selectedDtCal.getTime());
				
				
				NotificationSchedule notificationCampSchedule = new NotificationSchedule(tempCal,criteria, currentUser.getUserId());
				
				if(parentCampSchedule != null) { // if parent is not exists
					
					notificationCampSchedule.setParentId(parentCampSchedule.getNotificationCsId());
					notificationCampSchedule.setResendLevel((byte)(parentCampSchedule.getResendLevel()+1));
				} 
				else {
					notificationCampSchedule.setResendLevel((byte)0);
				}
				
				notificationCampSchedule.setNotificationId(notification.getNotificationId());
				if(notificationScheduleList.contains(notificationCampSchedule)) {
					return null;
				}
				notificationCampSchedule.setStatus((byte)0);
				notificationCampSchedule.setUserId(currentUser.getUserId());
				notificationScheduleList.add(notificationCampSchedule);
				
				return notificationCampSchedule;
				
				}
		
		
		
		private NotificationSchedule addDateCheck(Calendar selectedDtCal,NotificationSchedule parentCampSchedule, byte criteria) {	
			
			selectedDtCal.set(Calendar.SECOND,0);
			selectedDtCal.set(Calendar.MILLISECOND,0);

			 Calendar tempCal = Calendar.getInstance();
			tempCal.setTime(selectedDtCal.getTime());
			
			NotificationSchedule notificationCampSchedule = new NotificationSchedule(tempCal,criteria, currentUser.getUserId()); //--1
			//NotificationCampaignSchedule NotificationCampSchedule = new NotificationCampaignSchedule(NotificationCsId, tempCal, criteria);
			//ENDS 5, to make similar to email campaign schedules
			
			if(parentCampSchedule != null) { // if parent is not exists
				
				notificationCampSchedule.setParentId(parentCampSchedule.getNotificationCsId());
				//ENDS 6, to make similar to email campaign schedules
				
				notificationCampSchedule.setResendLevel((byte)(parentCampSchedule.getResendLevel()+1));
			} 
			else {
				notificationCampSchedule.setResendLevel((byte)0);
			}// else if parentCampSchedule
			
			if(notificationScheduleList.contains(notificationCampSchedule)) {
				MessageUtil.setMessage("Schedule added on same date and time.\n Please select a different time.", "color:red", "TOP");
				return null;
			}

	        try{
	        	if(notificationCampSchedule != null && notificationCampSchedule.getScheduledDate().after(currentUser.getPackageExpiryDate())) {
	    			MessageUtil.setMessage("Schedule date cannot be after your package expiry date.", "color:red", "TOP");
	    			return null;
	    		}
	        }catch(Exception e){
	        	logger.error("Exception >>>>>>>> ", e);
	        }
			
			notificationCampSchedule.setNotificationId(notification.getNotificationId());
			notificationCampSchedule.setStatus((byte)0);
			
			try{
			  notificationCampSchedule.setUserId(currentUser.getUserId());
			}catch(Exception e){
				logger.error("Exception >>>>>>>> ", e);
			}
			return notificationCampSchedule;
			
		}
		
		
		
		private void createDivUpComingCampaigns(boolean flag, NotificationSchedule schedule) {
			logger.debug(">>>>>>> Started  createDivUpComingCampaigns :: ");
			
			List<NotificationSchedule> activeOrDraftCampScheduleList = getActiveCampScheduleList(notificationScheduleList);
			
			NotificationSchedule lastSchedule = getLastSchedule(activeOrDraftCampScheduleList);
			
			if(lastSchedule != null){
				campActiveTillDateLbId.setValue(MyCalendar.calendarToString(lastSchedule.getScheduledDate(), MyCalendar.FORMAT_DATE_FORMAT, (TimeZone) sessionScope.get("clientTimeZone")));
				campActiveTillDateLbId.setAttribute("setLbDate",lastSchedule.getScheduledDate());
				campaignActiveTillDivId.setVisible(true);
				String size = (notificationScheduleList != null && notificationScheduleList.size() > 0) ? getSize(notificationScheduleList) : 1+"";  
				
				numOfTimeCampActiveLbId.setValue(size +" schedule(s)");
				numOfTimeCampActiveLbId.setStyle(OCConstants.FONT_BOLD);
				
			}
			else{
				campaignActiveTillDivId.setVisible(false);
			}
			
		}

		private String getSize(List<NotificationSchedule> notificationScheduleList2) {
			int activeCount = 0;
			for (NotificationSchedule campaignSchedule : notificationScheduleList2) {
				if(campaignSchedule.getStatus() == 0  || campaignSchedule.getStatus() == 2 ) {
					activeCount++;
				}
			}
			return activeCount+"";
		
		}

		private List<NotificationSchedule> getActiveCampScheduleList(List<NotificationSchedule> notificationScheduleListActive) {
			List<NotificationSchedule> activeOrDraftList = new ArrayList<NotificationSchedule>();
			for (NotificationSchedule campaignSchedule : notificationScheduleListActive) {
				if(campaignSchedule.getStatus() == 0  || campaignSchedule.getStatus() == 2 ) {
					activeOrDraftList.add(campaignSchedule);
				}
			}
			return activeOrDraftList;
		
		}

		private NotificationSchedule getLastSchedule(List<NotificationSchedule> activeOrDraftCampScheduleList) {
			NotificationSchedule lastestCampaignSchedule = null;
			
			for (NotificationSchedule camSchedule : activeOrDraftCampScheduleList) {
				
				if(lastestCampaignSchedule == null ) lastestCampaignSchedule = camSchedule ;
				
				if(camSchedule.getScheduledDate().after(lastestCampaignSchedule.getScheduledDate())){
					lastestCampaignSchedule = camSchedule;
				}
			}
			
			return lastestCampaignSchedule;

		}
		
		public void onClick$viewAllActiveSchedAnchId(){
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
		}
		
		public void onClick$submitBtnId$viewAllActiveSchedulesWinId(){
			int count =  viewAllActiveSchedulesWinId$campListlbId.getItemCount();
			for(; count>0; count--) {
				viewAllActiveSchedulesWinId$campListlbId.removeItemAt(count-1);
			}
			viewAllActiveSchedulesWinId.setClosable(false);
			viewAllActiveSchedulesWinId.setVisible(false);
			viewAllActiveSchedulesWinId$bulkDeleteLbId.setVisible(false);
			noOfTimeRedraw = 0; 
			
		}
		
		
		
		@SuppressWarnings("unchecked")
		public void redraw(int startIndex, int size) {

		try {
			MessageUtil.clearMessage();
			//System.gc();
			viewAllActiveSchedulesWinId$viewMoreActiveSchedAnchId.setVisible(true);
			//campaignScheduleList  = campScheduleList;
			campaignScheduleList  = getActiveCampScheduleList(notificationScheduleList);


			if(campaignScheduleList == null || campaignScheduleList.size()<=0){
				viewAllActiveSchedulesWinId$noRecordsActiveLbId.setVisible(true);
				viewAllActiveSchedulesWinId$viewMoreActiveSchedAnchId.setVisible(false);
				return;
			}

			Collections.sort(campaignScheduleList);

			
			if(campaignScheduleList != null && campaignScheduleList.size() >0){
				if(startIndex == 0){
					//					logger.info("No of time redraw ::"+noOfTimeRedraw);
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
							//	logger.info("in else"+i);
							li = new Listitem();
							lc = new Listcell();
							//Active and Draft Schedules
							//if(0 == campaignScheduleList.get(i).getStatus() || 2 == campaignScheduleList.get(i).getStatus()){
							viewAllActiveSchedulesWinId$noRecordsActiveLbId.setVisible(false);
							//Schedule Date
							li.setValue(campaignScheduleList.get(i));
							lc.setParent(li);
							lc = new Listcell();
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


					//					logger.info("No of time redraw ::"+noOfTimeRedraw);
					noOfTimeRedraw ++;
					Listitem li;
					Listcell lc;
					logger.info(startIndex+" Start Index & Size ::"+size);
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
						//Active and Draft Schedules
						//if(0 == campaignScheduleList.get(i).getStatus() || 2 == campaignScheduleList.get(i).getStatus()){
						viewAllActiveSchedulesWinId$noRecordsActiveLbId.setVisible(false);
						//Schedule Date

						li.setValue(campaignScheduleList.get(i));
						lc.setParent(li);
						lc = new Listcell();
						/*logger.info("::"+i);*/

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
					viewAllActiveSchedulesWinId$noRecordsActiveLbId.setVisible(true);
					viewAllActiveSchedulesWinId$viewMoreActiveSchedAnchId.setVisible(false);
				}

			}

		} catch (WrongValueException e) {
			logger.error("WrongValueException ", e);
		} catch (Exception e) {
			logger.error("Exception ", e);
		}
	
		}
		
		private class MyListener implements EventListener {

			@SuppressWarnings({ "unlikely-arg-type", "unchecked" })
			@Override
			public void onEvent(Event event) throws Exception {

				String action = (String)event.getTarget().getAttribute(NotificationWeb.TB_ACTION);
				Rows rows;
				NotificationSchedule campSchedule;

				if(event.getTarget() instanceof Image) {
					Image img = (Image)event.getTarget();
					String imageEventName = img.getAttribute("delete").toString();
					Listitem li = (Listitem)img.getParent().getParent().getParent();

					campSchedule = (NotificationSchedule)li.getValue();
					if("campScheduleDelete".equals(imageEventName)){
						/**
						 *  Deletes the row from the rows and removes corresponding
						 *  schedule object from the list when user clicks on delete link 
						 */
						int confirm = Messagebox.show("Are you sure you want to delete the schedule?", 
								"Confirm", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);

						if(confirm == Messagebox.OK) {
							try {
								// Added 2.3.11 
								//find the triggered email & then compare the 
								//scheduleDate with sent on if it is matched then reduce the time of triggered email
								
								
								UserCampaignExpiration triggeredAlert = getTriggeredAlertEmail();
								if(triggeredAlert != null){
									
									Calendar expireOn = triggeredAlert.getSentOn();
									expireOn.add(Calendar.DAY_OF_YEAR, 7);
									if(campSchedule.getScheduledDate().equals(expireOn)){
										
										//find the schedule which is last but one in the periodical schedules
										//just mark the schedule as delete one
										triggeredAlert.setStatus(OCConstants.USER_ALERT_CAMPAIGN_EXPIRED_STATUS_SCHEDULE_DELEATED);
										triggeredAlert.setModifiedDate(Calendar.getInstance());
										userCampaignExpirationDaoForDML.saveOrUpdate(triggeredAlert);
									}
								}
								
								if(campSchedule.getNotificationCsId() != null)
									notificationScheduleDaoForDML.deleteByCampSchId(campSchedule.getNotificationCsId());
								

								//rows.invalidate();
								Calendar lblDate = (Calendar) campActiveTillDateLbId.getAttribute("setLbDate");
								if(campSchedule.getScheduledDate().equals(lblDate)){
									campActiveTillDateLbId.removeAttribute("setLbDate");
								}
								removeCampScheduleFromList(campSchedule);

								notification.setStatus(getCampaignStatus(notification));
								//campaignsDaoForDML.saveOrUpdate(notification);
								rowsMap.remove(campSchedule.getNotificationCsId());
								rowMap.remove(campSchedule.getNotificationCsId());
								// arrange campShcedList, Clear ListBoxes
								MessageUtil.setMessage("Schedule deleted successfully.", "green", "TOP");
								
								int count = activeCampaingsListlbId.getItemCount();
								for(; count>0; count--) {
									activeCampaingsListlbId.removeItemAt(count-1);
								}
								count =  viewAllActiveSchedulesWinId$campListlbId.getItemCount();
								for(; count>0; count--) {
									viewAllActiveSchedulesWinId$campListlbId.removeItemAt(count-1);
								}
								if(notificationScheduleList != null && notificationScheduleList.size() > 0){

									List<NotificationSchedule> activeCampScheduleList = getActiveCampScheduleList(notificationScheduleList);
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

//					Commented for 2.3.11
					currentRow = (Row) event.getTarget().getParent().getParent();
					campSchedule = (NotificationSchedule)currentRow.getValue();

					if(action.equals(NotificationWeb.TB_ACTION_RESEND)) {

						// if clicks on 'Add Resend'
					//	errMsgLblId.setVisible(false);
						resendOptionWinId.setVisible(true);

					}
					else if(action.equals(NotificationWeb.TB_ACTION_DELETE)) {

						//if clicks on Delete the schedule
//						*//**
//						 *  Deletes the row from the rows and removes corresponding
//						 *  schedule object from the list when user clicks on delete link 
//						 *//*
						int confirm = Messagebox.show("Are you sure you want to delete the schedule?", 
								"Confirm", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);

						if(confirm == Messagebox.OK) {

							try {

								//find the triggeredemail & then compare the 
								//scheduleDate with sent on if it is matched then reduce the time of triggered email
								
								
								notificationScheduleDaoForDML.deleteByCampaignId(campSchedule.getNotificationCsId());


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

								notification.setStatus(getCampaignStatus(notification));
								//campaignsDaoForDML.saveOrUpdate(notification);


								rowsMap.remove(campSchedule.getNotificationCsId());
								rowMap.remove(campSchedule.getNotificationCsId());
								MessageUtil.setMessage("Schedule deleted successfully.", "green", "TOP");

							} catch (Exception e) {
								logger.error("Exception ::", e);
							}

						}



					}

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
							NotificationSchedule campShcedule=(NotificationSchedule)row.getValue();

							List<Object[]> childList=notificationScheduleDao.getAllChidren(campShcedule.getNotificationCsId(),campShcedule.getNotificationId());
							if(childList!=null) {
								detail=row.getDetailChild();
								detail.setStyle("display:block;");
								detail.addEventListener("onOpen", myListener);
							} //if
						}//for

						rows.getParent().setVisible(true);
					}//else
				}//else if
				
				else if(event.getTarget() instanceof Label){
					
					Redirect.goTo(PageListEnum.ADMIN_VIEW_COUPONS);
				
				}
			}
		}
			

			private void removeCampScheduleFromList(NotificationSchedule campSchedule) {
				
				try {	
						/*remove those schedules which are selected for deletion from global schedulelist*/
						resendOptionWinId$errMsgLblId.setValue("");
						/**
						 * Delete the schedule
						 */
						notificationScheduleList.remove(campSchedule);
						
						tempList.remove(campSchedule.getScheduledDate());
						
						
						
						
						if(schedGrdRowsId != null){
							Row row = null;
							List<Component> schedGrdRowsIdChildrenList = schedGrdRowsId.getChildren();
							if(schedGrdRowsIdChildrenList != null && schedGrdRowsIdChildrenList.size() > 0){
								for(Component aChild : schedGrdRowsIdChildrenList){
									row = (Row)aChild;
									if(row != null){
										
										
										
										if((row.getValue() != null) && areTheseSchedulesEqual((NotificationSchedule)(row.getValue()), campSchedule)){
											schedGrdRowsId.removeChild(aChild);
											break;
										}
									}
								}
							}
						}
						
						
						
						
						NotificationSchedule tempCS;
						
						/*recursively call this method if any of other schedule is having the parent id as the schedule about to be deleted*/
						
						for (Iterator<NotificationSchedule> iterator = notificationScheduleList.listIterator();
																	iterator.hasNext();) {
							tempCS = iterator.next();
							
							/*executes only for existing schedules*/
							if(tempCS.getParentId() != null && (campSchedule.getNotificationCsId() != null) &&
									(tempCS.getParentId().longValue() == campSchedule.getNotificationCsId().longValue())) {
								//logger.debug("----7----");
								/*this is may not to do*/
								if(tempList.contains(tempCS.getScheduledDate())) {
									tempList.remove(tempCS.getScheduledDate());
									
								}
								if(tempCS.getNotificationCsId() != null) {
									tempNotificationCampScheduleList.add(tempCS);
								}
								removeCampScheduleFromList(tempCS);
							}//if
							
							
						}//for
						
				} 
				catch (Exception e) {
					logger.error("Exception while removing the NotificationCampaign schedules",e);
				}
				
}



			private boolean areTheseSchedulesEqual(NotificationSchedule notificationSchedule,NotificationSchedule campSchedule) {
				
				try{
					boolean scheduledDateChek = false;
					
					boolean criteriaChek = false;
					
					boolean parentIdChek = false;
					
					boolean resendLevelChek = false;
					
					boolean notificationCampaignIdChek = false;
					
					boolean statusChek = false;
					
					
					if( notificationSchedule.getScheduledDate() == null &&  campSchedule.getScheduledDate() == null){
						scheduledDateChek = true;
					}else if(notificationSchedule.getScheduledDate() != null &&  campSchedule.getScheduledDate() != null){
						scheduledDateChek = notificationSchedule.getScheduledDate().equals(campSchedule.getScheduledDate());
					}else {
						scheduledDateChek = false;
					}
					
					
					
					//primitive data type byte
					criteriaChek = notificationSchedule.getCriteria() == campSchedule.getCriteria();
					
					
					if( notificationSchedule.getParentId() == null &&  campSchedule.getParentId() == null){
						parentIdChek = true;
					}else if(notificationSchedule.getParentId() != null &&  campSchedule.getParentId() != null){
						parentIdChek = notificationSchedule.getParentId().equals(campSchedule.getParentId());
					}else {
						parentIdChek = false;
					}
					
					
					//primitive data type byte
					resendLevelChek = notificationSchedule.getResendLevel() == campSchedule.getResendLevel();
					
					
					
					if( notificationSchedule.getNotificationId() == null &&  campSchedule.getNotificationId() == null){
						notificationCampaignIdChek = true;
					}else if(notificationSchedule.getNotificationId() != null &&  campSchedule.getNotificationId() != null){
						notificationCampaignIdChek = notificationSchedule.getNotificationId().equals(campSchedule.getNotificationId());
					}else {
						notificationCampaignIdChek = false;
					}
					
					
					//primitive data type byte
				    statusChek = notificationSchedule.getStatus() == campSchedule.getStatus();
					
					
					if(scheduledDateChek && criteriaChek && parentIdChek && resendLevelChek && notificationCampaignIdChek && statusChek){
						return true;
					}else{
						return false;
					}
					
				}catch(Exception e){
					logger.info("exception>>>>>>>>>>>>>>>"+e.getStackTrace());
				}
				
				return false;
			}


			private String getCampaignStatus(Notification notification) {
				if(notificationScheduleList == null || notificationScheduleList.size() == 0) {
					logger.debug("All Active or Draft Schedules are deleted.");
					return Constants.CAMP_STATUS_DRAFT;
				}

				Calendar startCal =null;
				Calendar endCal = null;


				NotificationSchedule latestnotificationCampaignSchedule = null;

				for (NotificationSchedule campaignSchedule : notificationScheduleList) {
					if(latestnotificationCampaignSchedule == null) latestnotificationCampaignSchedule = campaignSchedule;

					if(campaignSchedule.getScheduledDate().after(latestnotificationCampaignSchedule.getScheduledDate())){
						latestnotificationCampaignSchedule = campaignSchedule;
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



				if(latestnotificationCampaignSchedule.getStatus() == 0 || latestnotificationCampaignSchedule.getStatus() == 1 || latestnotificationCampaignSchedule.getStatus() == 2) 
					return latestnotificationCampaignSchedule.getStatusStr();
				else if(latestnotificationCampaignSchedule.getStatus() >= 3)
					return "Schedule Failure";
				else
					return "Draft";

			}
			
			
			public void onClick$delSelectedId$viewAllActiveSchedulesWinId()  {
				try {
					int count = viewAllActiveSchedulesWinId$campListlbId.getSelectedCount();
					if(count == 0) {
						Messagebox.show("Please select at least one schedule to delete.", "Information" , 
								Messagebox.OK, Messagebox.INFORMATION);
						return;
					}
					String msg = "You have chosen "+count+" schedules to delete. Do you want to continue?";
					boolean found = false;
					Set<Listitem> selList = viewAllActiveSchedulesWinId$campListlbId.getSelectedItems();
					NotificationSchedule campaignSchedule;
					List<NotificationSchedule> campaignSchedules = new ArrayList<NotificationSchedule>();
					for (Listitem li : selList) {
						campaignSchedule = (NotificationSchedule)li.getValue();
						campaignSchedules.add(campaignSchedule);
						
						if(campaignSchedule.getStatus() == OCConstants.ACTIVE_EMAIL_STATUS || 
								campaignSchedule.getStatus() == OCConstants.DRAFT_EMAIL_STATUS) {
							found = true;
						}
					}
					if(found) {
						msg = "You have chosen "+count+" schedules to delete. Do you want to continue?";
					}
					try {
						int confirm =Messagebox.show(msg, "Delete Confirmation",Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);

						if(confirm == Messagebox.OK){
							if(deleteCampaignsSchedules(campaignSchedules).equals("success")){
								notification.setStatus(getCampaignStatus(notification));
								notificationDaoForDML.saveOrUpdate(notification);
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
				} catch (Exception ex) {
					logger.error("** Exception :" , ex);
				}
				//		logger.debug("<<<<< Completed onClick$delSelectedId$viewAllActiveSchedulesWinId .");
			}
			
			
			@SuppressWarnings("unlikely-arg-type")
			private String deleteCampaignsSchedules(List<NotificationSchedule> campaignSchedules) {
			try{
				notificationScheduleDaoForDML.deleteByCollection(campaignSchedules);

				//Performance Problem to DB hits in Loop, Need to Fixed.

				for (NotificationSchedule campaignSchedule : campaignSchedules) {

					Calendar lblDate = (Calendar) campActiveTillDateLbId.getAttribute("setLbDate");
					if(campaignSchedule.getScheduledDate().equals(lblDate)){
						campActiveTillDateLbId.removeAttribute("setLbDate");
					}
					removeCampScheduleFromList(campaignSchedule);
					rowsMap.remove(campaignSchedule.getNotificationCsId());
					rowMap.remove(campaignSchedule.getNotificationCsId());

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
				if(notificationScheduleList != null && notificationScheduleList.size() > 0){

					List<NotificationSchedule> activeCampScheduleList = getActiveCampScheduleList(notificationScheduleList);
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
		}
			
			public void onClick$submitBtnId$viewAllArchivedSchedulesWinId(){
					int count =  viewAllArchivedSchedulesWinId$campListlbId.getItemCount();
					for(; count>0; count--) {
						viewAllArchivedSchedulesWinId$campListlbId.removeItemAt(count-1);
					}
					viewAllArchivedSchedulesWinId.setClosable(false);
					viewAllArchivedSchedulesWinId.setVisible(false);
				}
		
			public void onClick$viewMoreArchievedSchedAnchId$viewAllArchivedSchedulesWinId(){
				redrawSent (sentSchCount,sentSchCount+100);
				sentSchCount = sentSchCount + 100;
				logger.info("now records are " + sentSchCount);
			}
			
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
			}

			private List<NotificationSchedule> sentCampaignScheduleList = null;
			boolean redrawFlag = true;
			private void redrawSent(int startIndex,int size) {
				try{
					sentCampaignScheduleList = getArchivedCampScheduleList(notificationScheduleList);

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
									viewAllArchivedSchedulesWinId$noRecordsArchivedLbId.setVisible(true);
									viewAllArchivedSchedulesWinId$viewMoreArchievedSchedAnchId.setVisible(false);
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
					}
				}
				catch(Exception exception){
					logger.error("Exception",exception);
				}
			}
			
			public void onClick$okBtnId() throws WrongValueException, Exception { 
				
				if(notification == null) {
					return;
				}
				if(testNotificationTbId.getValue() == null || testNotificationTbId.getValue().isEmpty()) {
					MessageUtil.setMessage("Please Enter Registered Mobile Number.","color:red","TOP");
					return;
				}
				sendTestNotification(testNotificationTbId.getValue(), notification);
			}
			
		public void onCheck$sendPeriodicallyId() throws Exception {
			frqDivId.setVisible(!frqDivId.isVisible());
			chooseDateTimeImgId.setVisible(false);
			//frqDtDivId.setVisible(!frqDtDivId.isVisible());
			prtDtDivId.setVisible(!prtDtDivId.isVisible());
		}
		
		public void onClick$frqBtnId() {
			
			try {
				
				if(notification == null){
					
						MessageUtil.setMessage("Please fill the entries for Notification campaign properly before it is scheduled.", "color:red", "top");
						return;
				}
				
				schErrorLblId.setValue("");
				Calendar startCal = null;
				Calendar endCal = null;
				
				try {
					startCal = startDtBxId.getServerValue();
					endCal = endDtBxId.getServerValue();
					
				
				} 
				catch (Exception e) {
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
				
				//STARTS 2, change  w.r.t similarities with email campaign schedules(change date 16th jan 2016)
				// Following block was not there, now added for same dates entered in start and end cal, w.r.t similarities with email campaign schedules(change date 16th jan 2016) -- rajeev
				if(startDtBxId.getServerValue().compareTo( endDtBxId.getServerValue()) == 0) {
					schErrorLblId.setValue("Start and end schedules cannot be same.");
					return ;
				}

				NotificationSchedule campaignSchedule1 = addDateCheck(startCal, null, (byte)0);

				if(campaignSchedule1 == null){
					return ;
				}
				int confirm = 0;
				boolean isSameDay = false;
				List<String> dates = new ArrayList<String>();
				List<String> nowSch = getSchedulesDates(startDtBxId.getServerValue(), endDtBxId.getServerValue(),
						Integer.parseInt((String)frqLbId.getSelectedItem().getValue()),
						((Integer)frqLbId.getSelectedItem().getAttribute("step")).byteValue());
				
				List<NotificationSchedule> activeDraftCampScheduleList =  getActiveCampScheduleList(notificationScheduleList);
				
				
				//Like email-----starts
				for(String now : nowSch){
					for (NotificationSchedule campSched : activeDraftCampScheduleList) {
						String sameSch = MyCalendar.calendarToString(campSched.getScheduledDate(), MyCalendar.FORMAT_DATEONLY,(TimeZone) sessionScope.get("clientTimeZone"));
						if( now.equals(sameSch) && (campSched.getStatus() == 0 
								||  campSched.getStatus() == 2 )){

							dates.add(" "+MyCalendar.calendarToString(campSched.getScheduledDate(), MyCalendar.FORMAT_TIME,(TimeZone) sessionScope.get("clientTimeZone")));
						}
					}
					
					
				}
			
				
				for(String now : nowSch){
					for (NotificationSchedule campSched : activeDraftCampScheduleList) {
						//String now = MyCalendar.calendarToString(cal, MyCalendar.FORMAT_DATEONLY,(TimeZone) sessionScope.get("clientTimeZone"));

						String sameSch = MyCalendar.calendarToString(campSched.getScheduledDate(), MyCalendar.FORMAT_DATEONLY,(TimeZone) sessionScope.get("clientTimeZone"));
					//	logger.debug("nownow :"+now+" : sameSchsameSch"+sameSch+" :now.equals(sameSch)"+now.equals(sameSch));
						if( now.equals(sameSch) && (campSched.getStatus() == 0 
								||  campSched.getStatus() == 2 )){

							//dates.add(" "+MyCalendar.calendarToString(campSched.getScheduledDate(), MyCalendar.FORMAT_TIME,(TimeZone) sessionScope.get("clientTimeZone")));

							/*if(campScheduleList != null && campScheduleList.size()>0){*/
							if(activeDraftCampScheduleList != null && activeDraftCampScheduleList.size() > 0){
								confirm = Messagebox.show("Schedule(s) already exist on  "+MyCalendar.calendarToString(activeDraftCampScheduleList.get(0).getScheduledDate(), MyCalendar.FORMAT_MDATEONLY,(TimeZone) sessionScope.get("clientTimeZone"))
										+" to " +  MyCalendar.calendarToString(activeDraftCampScheduleList.get(activeDraftCampScheduleList.size()-1).getScheduledDate(), MyCalendar.FORMAT_MDATEONLY,(TimeZone) sessionScope.get("clientTimeZone"))
										+ " for following time: "
										+"\n "+StringUtils.collectionToCommaDelimitedString(dates)+" \n Do you want to continue & add more?", 
										"Same Day Schedule Found", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
							}
							isSameDay = true; 
							break;
							//}
						}
					}
					if(isSameDay) break;
				}
				if(confirm == 2){
					logger.info("returning");
					return;
				}

				

				if(notificationScheduleList != null && (!isSameDay ||(confirm == 1 && isSameDay))) {
					
					List<NotificationSchedule> campaignScheduleList = 
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
					}
				}
				
			} 
			catch (NumberFormatException e) {
				logger.error("Exception ::", e);
			}
		}
	
		private List<String> getSchedulesDates(Calendar startDtCal, Calendar endDtCal, 
				Integer frequency, Byte step) {

			if(startDtCal.compareTo(endDtCal) == 0) {
				schErrorLblId.setValue("Start and end date should not be same");
				//			MessageUtil.setMessage("Start and end date should not be same.", "color:red", "TOP");
				return null;
			}

			List<String> csList = new ArrayList<String>();

			NotificationSchedule startDtCS = new NotificationSchedule(startDtCal, notification.getNotificationId(), (byte)0, (byte)0,currentUser.getUserId() );
			startDtCS.setResendLevel((byte)0);
			startDtCS.setUserId(currentUser.getUserId());
			//startDtCS.setSmsCsId(smsCampaignScheduleDao.getCurrentId());

			// add the starting date in the schedule final list 
			// if it doesn't contain with the same date

			if( !notificationScheduleList.contains(startDtCS) ) {
				//campScheduleList.add(startDtCS);
				//csList.add(MyCalendar.calendarToString(startDtCS.getScheduledDate(), MyCalendar.FORMAT_DATE_FORMAT,(TimeZone) sessionScope.get("clientTimeZone")));
				csList.add(MyCalendar.calendarToString(startDtCS.getScheduledDate(), MyCalendar.FORMAT_DATEONLY,(TimeZone) sessionScope.get("clientTimeZone")));
				
			}

			NotificationSchedule nextDtCS;
			Calendar nextDate = Calendar.getInstance();
			Calendar nextDateTemp;
			nextDate.setTime(startDtCal.getTime());
	        
			
			
			nextDate.set(frequency, startDtCal.get(frequency)+step);

			/** Generates the dates between the given dates and frequency **/
			while(nextDate.compareTo(endDtCal) <= 0 ) {

				nextDateTemp = Calendar.getInstance();
				nextDateTemp.setTime(nextDate.getTime());

				nextDtCS = new NotificationSchedule(nextDateTemp, notification.getNotificationId(), (byte)0, (byte)0, currentUser.getUserId());

				nextDtCS.setResendLevel((byte)0);
				nextDtCS.setUserId(currentUser.getUserId());
	            
				//STARTS 9, to make similar as email campaign schedules
				//nextDtCS.setSmsCsId(smsCampaignScheduleDao.getCurrentId());
				//ENDS 9, to make similar as email campaign schedules
				if( !notificationScheduleList.contains(nextDtCS) ) {
					//csList.add(MyCalendar.calendarToString(nextDtCS.getScheduledDate(), MyCalendar.FORMAT_DATE_FORMAT,(TimeZone) sessionScope.get("clientTimeZone")));
					csList.add(MyCalendar.calendarToString(nextDtCS.getScheduledDate(), MyCalendar.FORMAT_DATEONLY,(TimeZone) sessionScope.get("clientTimeZone")));
					//campScheduleList.add(nextDtCS);
				}
				nextDate.set(frequency, nextDate.get(frequency)+step);


			}// while

			return csList;
		}
		
		
		private List<NotificationSchedule> addDates(Calendar startDtCal, Calendar endDtCal, 
				Integer frequency, Byte step) {
			
			if(startDtCal.compareTo(endDtCal) == 0) {
				schErrorLblId.setValue("Start and end date should not be same");
				return null;
			}
			
			List<NotificationSchedule> csList = new ArrayList<NotificationSchedule>();
			
			NotificationSchedule startDtCS = new NotificationSchedule(startDtCal,
					notification.getNotificationId(), (byte)0, (byte)0, currentUser.getUserId() );
			startDtCS.setResendLevel((byte)0);
			
			
			//STARTS 11, to make similar to email campaign
			int count = activeCampaingsListlbId.getItemCount();
			boolean isCreditOrExipry   = createRowUpComingListBox(startDtCS,count,false);
			//createDivUpComingCampaigns(true,startDtCS);
			if(isCreditOrExipry == false){
				logger.error("Your Credit Limits are Exipred please contact support");
				return null;
			}
			
			if( !notificationScheduleList.contains(startDtCS) ) {
				//startDtCS.setSmsCsId(null);
				notificationScheduleList.add(startDtCS);
				csList.add(startDtCS);
			}
			
			Row row = new Row();
			row.setValue(startDtCS);
				
			if(schedGrdRowsId != null) {
				row.setParent(schedGrdRowsId);
			}
			
			NotificationSchedule nextDtCS;
			Calendar nextDate = Calendar.getInstance();
			Calendar nextDateTemp;
			nextDate.setTime(startDtCal.getTime());
			
			nextDate.set(frequency, startDtCal.get(frequency)+step);
			
			/** Generates the dates between the given dates and frequency **/
			while(nextDate.compareTo(endDtCal) <= 0 ) {
				
				nextDateTemp = Calendar.getInstance();
				nextDateTemp.setTime(nextDate.getTime());
				
				nextDtCS = new NotificationSchedule(nextDateTemp,
						notification.getNotificationId(), (byte)0, (byte)0, currentUser.getUserId() );
				nextDtCS.setResendLevel((byte)0);
				//nextDtCS.setSmsCsId(smsCampaignScheduleDao.getCurrentId());
				
				 count = activeCampaingsListlbId.getItemCount();
				 isCreditOrExipry   = createRowUpComingListBox(nextDtCS,count,false);
				 
				if(isCreditOrExipry == false){
					logger.error("Your Credit Limits are Exipred please contact support");
					break;
				}
				else{
				}
				
				//nextDtCS.setResendLevel((byte)0);
				
				if( !notificationScheduleList.contains(nextDtCS) ) {
					
					csList.add(nextDtCS);
					notificationScheduleList.add(nextDtCS);
					createRow(nextDtCS, schedGrdRowsId);
				}
				nextDate.set(frequency, nextDate.get(frequency)+step);
				
				
			}// while
			
			return csList;
		}
		
		public void onCheck$prtDateRadioId() throws Exception {
			chooseDateTimeImgId.setVisible(true);
			frqDivId.setVisible(!frqDivId.isVisible());
			//frqDtDivId.setVisible(!frqDtDivId.isVisible());
			prtDtDivId.setVisible(!prtDtDivId.isVisible());
			
		}
		
		public void onSelect$campListlbId$viewAllActiveSchedulesWinId() {
			if(viewAllActiveSchedulesWinId$campListlbId.getSelectedCount() == 0){
				viewAllActiveSchedulesWinId$campActionsBandBoxId.setDisabled(true);
				viewAllActiveSchedulesWinId$campActionsBandBoxId.setButtonVisible(false);
			}else if(viewAllActiveSchedulesWinId$campListlbId.getSelectedCount() > 0) {
				viewAllActiveSchedulesWinId$campActionsBandBoxId.setDisabled(false);
				viewAllActiveSchedulesWinId$campActionsBandBoxId.setButtonVisible(true);
			}
		}
}
