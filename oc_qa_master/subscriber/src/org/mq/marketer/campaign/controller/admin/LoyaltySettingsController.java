package org.mq.marketer.campaign.controller.admin;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.clapper.util.io.FileUtil;
import org.mq.marketer.campaign.beans.EmailQueue;
import org.mq.marketer.campaign.beans.LoyaltySettings;
import org.mq.marketer.campaign.beans.LoyaltyThresholdAlerts;
import org.mq.marketer.campaign.beans.UserEmailAlert;
import org.mq.marketer.campaign.beans.UserOrganization;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.beans.ValueCodes;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.EmailQueueDao;
import org.mq.marketer.campaign.dao.EmailQueueDaoForDML;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.dao.UsersDaoForDML;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.EncryptDecryptLtyMembshpPwd;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PageListEnum;
import org.mq.marketer.campaign.general.PageUtil;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Redirect;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.data.dao.LoyaltySettingsDao;
import org.mq.optculture.data.dao.LoyaltySettingsDaoForDML;
import org.mq.optculture.data.dao.LoyaltyThresholdAlertsDao;
import org.mq.optculture.data.dao.LoyaltyThresholdAlertsDaoForDML;
import org.mq.optculture.data.dao.UserEmailAlertDao;
import org.mq.optculture.data.dao.UserEmailAlertDaoForDML;
import org.mq.optculture.data.dao.ValueCodesDao;
import org.mq.optculture.data.dao.ValueCodesDaoForDML;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Window;
import org.zkoss.zul.event.PagingEvent;


public class LoyaltySettingsController extends GenericForwardComposer {
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	private Listbox selOrgLbId,ltyTypeId,hoursdailyLbId,hoursWeeklyLbId,hoursMonthlyLbId,daysWeeklyLbId,whichDayMonthlyLbId,hoursLbId,daysLbId,whichDayLbId,valueCodePerPageLBId,fontId;
	private Textbox logoTbId,urlTbId,changePwdWinId$currPwdTbId,changePwdWinId$newPwdTbId,changePwdWinId$retypePwdTbId,emailTbId,mobileTbId, percentTbId, valueTbId,emailAddTxtBxId,valueCodeNameTbId,descriptionTbId,bannerNameTbId,tabNameTbId,emailTBoxId,mobileTBoxId;
	private Window previewWin,changePwdWinId;
	Media gMedia = null;
	Media lMedia = null;
	Media hMedia = null;
	Media tMedia = null;
	private Combobox themeId,brandingThemeId,balanceCardThemeId,balanceCardTextThemeId;
	private Div orgDivId, alertsDivId, previewWin$contentDivId,alertMailDivId,allThreeLbDivId,dailyLbDivId,weeklyLbDivId,monthlyLbDivId,ltyRprtDivId;
	private Div dailyDivId,weeklyDivId,monthlyDivId;
	private Groupbox ltySecurityGbId, ltyAlertsGbId;
	private Checkbox enableAlertsChkId,ltyReportAlertsDailyChkId,ltyReportAlertsWeeklyChkId,ltyReportAlertsMonthlyChkId,
	
	firstNameChkBxId,lastNameChkBxId,mobileNumberChkBxId,emailAddressChkBxId,
	
	streetChkBxId,cityChkBxId,stateChkBxId,postalCodeChkBxId,
	
	countryChkBxId,birthdayChkBxId,anniversaryChkBxId,genderChkBxId;
	
	private Radio percentRadioBtId,valueRadioBtId;
	private LoyaltySettings loyaltySettings;
	private UserOrganization userOrg;
	private String menuSrc;
	private Button updateValueCodesBtnId,saveValueCodesBtnId;
	private long ValueCodeId;
	private Listbox valueCodeLbId;
	private Window ValueCodeEditWinId;
	private Textbox ValueCodeEditWinId$valueCodeNameTbId,ValueCodeEditWinId$descriptionTbId,mobileApplogoTbId,homePageImageTbId,tabImageTbId;
	private Paging valueCodePagingId;
	private String key;
	private Users currentUser; 
	private static String[] fontStr= {"Raleway","Open Sans","Playfair Display","Lato","Old Standard TT","Abril Fatface","PT Serif",
			"Ubuntu","Vollkorn","PT Sans","Montserrat","Source Sans Pro","Oswald","Roboto","Nunito","Work Sans"};
	
	public static Map<String,String> THEMES_MAP = new HashMap<String, String>();
	
	static{
		THEMES_MAP.put("Default", "/images/loyalty/previewtheme/Default.jpg");
		THEMES_MAP.put("Blue1", "/images/loyalty/previewtheme/Blue1.jpg");
		THEMES_MAP.put("Blue2", "/images/loyalty/previewtheme/Blue2.jpg");
		THEMES_MAP.put("Brown", "/images/loyalty/previewtheme/Brown.jpg");
		THEMES_MAP.put("Cyan", "/images/loyalty/previewtheme/Cyan.jpg");
		THEMES_MAP.put("Green", "/images/loyalty/previewtheme/Green.jpg");
		THEMES_MAP.put("Green1", "/images/loyalty/previewtheme/Green Ocur.jpg");
		THEMES_MAP.put("Orange", "/images/loyalty/previewtheme/orange.jpg");
		THEMES_MAP.put("Pink", "/images/loyalty/previewtheme/pink.jpg");
		THEMES_MAP.put("Purple", "/images/loyalty/previewtheme/Purple.jpg");
		THEMES_MAP.put("Purple1", "/images/loyalty/previewtheme/Purple1.jpg");
		THEMES_MAP.put("Red", "/images/loyalty/previewtheme/Red.jpg");
		THEMES_MAP.put("White", "/images/loyalty/previewtheme/white.jpg");
		THEMES_MAP.put("Black", "/images/loyalty/previewtheme/Black.jpg");
	}
	
	public static Map<String,String> FONT_MAP = new HashMap<String, String>();
	
	static{
		FONT_MAP.put("Raleway","https://fonts.googleapis.com/css2?family=Raleway&display=");
		FONT_MAP.put("Open Sans","https://fonts.googleapis.com/css2?family=Open+Sans&display=");
		FONT_MAP.put("Playfair Display","https://fonts.googleapis.com/css2?family=Playfair+Display&display=");
		FONT_MAP.put("Lato","https://fonts.googleapis.com/css2?family=Lato&display=");
		FONT_MAP.put("Old Standard TT","https://fonts.googleapis.com/css2?family=Old+Standard+TT&display=");
		FONT_MAP.put("Abril Fatface","https://fonts.googleapis.com/css2?family=Abril+Fatface&display=");
		FONT_MAP.put("PT Serif","https://fonts.googleapis.com/css2?family=PT+Serif&display=");
		FONT_MAP.put("Ubuntu","https://fonts.googleapis.com/css2?family=Ubuntu&display=");
		FONT_MAP.put("Vollkorn","https://fonts.googleapis.com/css2?family=Vollkorn&display=");
		FONT_MAP.put("PT Sans","https://fonts.googleapis.com/css2?family=PT+Sans&display=");
		FONT_MAP.put("Montserrat","https://fonts.googleapis.com/css2?family=Montserrat&display=");
		FONT_MAP.put("Source Sans Pro","https://fonts.googleapis.com/css2?family=Source+Sans+Pro&display=");
		FONT_MAP.put("Oswald","https://fonts.googleapis.com/css2?family=Oswald&display=");
		FONT_MAP.put("Roboto","https://fonts.googleapis.com/css2?family=Roboto&display=");
		FONT_MAP.put("Nunito","https://fonts.googleapis.com/css2?family=Nunito&Display=");
		FONT_MAP.put("Work Sans","https://fonts.googleapis.com/css2?family=Work+Sans&Display=");

	}

	public LoyaltySettingsController() {
		String style = "font-weight:bold;font-size:15px;color:#313031;font-family:Arial,Helvetica,sans-serif;align:left";
		PageUtil.setHeader("Loyalty Settings","",style,true);
		
		menuSrc = (String) Sessions.getCurrent().getAttribute("menuSrc");
	}
	
	@Override
	public void doAfterCompose(Component comp) throws Exception  {
		super.doAfterCompose(comp);
		try {
			logger.debug("------------------menuSrc:::::::"+menuSrc);
			setUserOrg();
			setFonts();
			setThemes();
			setBrandingThemes();
			setbalanceCardThemeIds();
			setbalanceCardTextThemeIds();
			if("MyAccount".equalsIgnoreCase(menuSrc)){

				setDefaultPortalSettings();

				orgDivId.setVisible(false);
				ltySecurityGbId.setVisible(true);
				ltyAlertsGbId.setVisible(true);
			
				defaultPrerequesiteSettings();
				setDefaultAlertSettings();
				setDefaultLoyaltyReportSetting();
			}
			else if("Administrator".equalsIgnoreCase(menuSrc)){
				orgDivId.setVisible(true);
				ltySecurityGbId.setVisible(false);
				ltyAlertsGbId.setVisible(false);
			}
			
			//Changes Loyalty Spec rules
			valueCodeNameTbId.setValue("");
			valueCodeNameTbId.setDisabled(false);
			descriptionTbId.setValue("");
			saveValueCodesBtnId.setLabel("Save");
			saveValueCodesBtnId.removeAttribute("ValueCodeObj");
			int tempCount = Integer.parseInt(valueCodePerPageLBId
					.getSelectedItem().getLabel());
			valueCodePagingId.addEventListener("onPaging", this);
			ValueCodesGrid(null,0,tempCount,0);
			defaultPrerequesiteSettings();

			
			//setDefaultLoyaltyReportSetting();
			//emailAddTxtBxId.setValue(PropertyUtil.getPropertyValueFromDB("LtyRepAlrtEmailId"));
			/*String imgUrl = PropertyUtil.getPropertyValue("ImageServerUrl");
			String logoPath = imgUrl +"UserData/"+GetUser.getUserObj().getUserName()+"/";
			if(userOrg.getBannerPath() !=null) {
				String logoImgName = userOrg.getBannerPath().replace(logoPath, "");
			mobileApplogoTbId.setValue(logoImgName);
			mobileApplogoTbId.setDisabled(true);
			}*/
		} catch (Exception e) {
			logger.error("Exception  ::", e);
		} 
	}
	private  final String ATTR_DAILY= new String("daily");
	private  final String ATTR_WEEKLY=new String("weekly");
	private void setDefaultLoyaltyReportSetting(){
		try{
			boolean dailyFlag = false;
			boolean weeklyFlag = false;
	//		boolean monthlyFlag = false;
			int strTime = 0;
			int strDay = 0;
		//	int strDayOfMonth = 0;
			String emailAddrs = null;
			
			Users user = GetUser.getUserObj();	
			UserEmailAlertDao userEmailAlertDao = (UserEmailAlertDao) ServiceLocator.getInstance().getDAOByName(OCConstants.USER_EMAIL_ALERT_DAO);
			List<UserEmailAlert> userEmailAlertObjList = userEmailAlertDao.findListByUserId(user.getUserId());
			
			if(userEmailAlertObjList != null && userEmailAlertObjList.size() > 0){
				
				for (UserEmailAlert userEmailAlertLstObj : userEmailAlertObjList) {
					
					if(userEmailAlertLstObj.getFrequency().equals(OCConstants.LTY_SETTING_REPORT_FRQ_DAY)){
						ltyReportAlertsDailyChkId.setAttribute(ATTR_DAILY, userEmailAlertLstObj);
						if(userEmailAlertLstObj.isEnabled()) {
							strTime = Integer.parseInt(userEmailAlertLstObj.getTriggerAt());
							hoursLbId.setSelectedIndex(strTime-1);
							if(emailAddrs == null)emailAddrs = userEmailAlertLstObj.getEmailId();

						}
						ltyReportAlertsDailyChkId.setChecked(userEmailAlertLstObj.isEnabled());	
						onCheck$ltyReportAlertsDailyChkId();
							
					}
					else if(userEmailAlertLstObj.getFrequency().equals(OCConstants.LTY_SETTING_REPORT_FRQ_WEEK)){
						
						ltyReportAlertsWeeklyChkId.setAttribute(ATTR_WEEKLY, userEmailAlertLstObj);
						
						if(userEmailAlertLstObj.isEnabled()) {
							String strWeek = userEmailAlertLstObj.getTriggerAt();
							String strWeekArr[] = strWeek.split(Constants.ADDR_COL_DELIMETER);
							strDay = Integer.parseInt(strWeekArr[1]);
							hoursLbId.setSelectedIndex(Integer.parseInt(strWeekArr[0])-1);
							daysLbId.setSelectedIndex(strDay);
							if(emailAddrs == null)emailAddrs = userEmailAlertLstObj.getEmailId();
						}
						ltyReportAlertsWeeklyChkId.setChecked(userEmailAlertLstObj.isEnabled());	
						onCheck$ltyReportAlertsWeeklyChkId();
					}
				}
				
				if(ltyReportAlertsWeeklyChkId.isChecked() || ltyReportAlertsDailyChkId.isChecked()) {
				int mailIdCount = 1;
				String emailAddrArry[] = new String[1];
				logger.info("email ids are ==="+ emailAddrs);
				if(!emailAddrs.contains(Constants.ADDR_COL_DELIMETER)){
					emailAddrArry[0] = emailAddrs;
				}else {
					emailAddrArry = emailAddrs.split(Constants.ADDR_COL_DELIMETER);
				}
		
			
			/*if(dailyFlag == true && weeklyFlag == true && monthlyFlag == true){
				dailyDivId.setVisible(true);
				weeklyDivId.setVisible(true);
				monthlyDivId.setVisible(true);*/
				
					if(emailAddrArry != null && emailAddrArry.length > 0){
						
						for (String emailIds : emailAddrArry) {
							if(mailIdCount == 1) {
								emailAddTxtBxId.setText(emailIds);
							}
							else {
								
								Div alertDiv = new Div();
								Textbox alertTextBx = new Textbox();
								alertTextBx.setText(emailIds);
								alertTextBx.setParent(alertDiv);
								alertTextBx.setWidth("250px");
								alertTextBx.setStyle("margin-left:85px;margin-top: 10px;margin-right:7px ;");
								
								Image delImg = new Image();
								delImg.setAttribute("TYPE", "ALERT_DEL");
								delImg.setSrc("/images/action_delete.gif");
								delImg.setStyle("cursor:pointer;color:#2886B9;font-weight:bold;text-decoration: underline;");
								delImg.setTooltiptext("Delete");
								delImg.addEventListener("onClick", this);
								delImg.setParent(alertDiv);
								
								alertDiv.setParent(alertMailDivId);
								
							}
							mailIdCount ++;
						}
				}
			}
			/*else if(userEmailAlertObjList == null){
				ltyReportAlertsDailyChkId.setAttribute("daily", userEmailAlertObjList);
				ltyReportAlertsWeeklyChkId.setAttribute("weekly", userEmailAlertObjList);
		//		ltyReportAlertsMonthlyChkId.setAttribute("monthly", userEmailAlertObjList);
			}*/
			
		}
		}catch(Exception e){
			logger.error("Exception while setting loyalty report data...",e);
		}
	}
	
	private void setAttributeForCheckBoxIds(Checkbox checkbox, UserEmailAlert userEmailAlertObj, String type){
		logger.info("========== 4 ===========");
		checkbox.setAttribute(type, userEmailAlertObj);
	}
	
	private void setDefaultAlertSettings() {
		try{
			LoyaltyThresholdAlertsDao loyaltyThresholdAlertsDao = (LoyaltyThresholdAlertsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_THRESHOLD_ALERTS_DAO);
			LoyaltyThresholdAlerts alertsObj = loyaltyThresholdAlertsDao.findByUserId(GetUser.getUserObj().getUserId());
			if(alertsObj != null && alertsObj.getEnableAlerts() == OCConstants.FLAG_YES){
				enableAlertsChkId.setChecked(true);
				alertsDivId.setVisible(true);

				emailTbId.setValue(alertsObj.getAlertEmailId() != null ? alertsObj.getAlertEmailId() : "");
				mobileTbId.setValue(alertsObj.getAlertMobilePhn() != null ? alertsObj.getAlertMobilePhn() : "");
				if(OCConstants.LOYALTY_CARDS_AVAILABLE_COUNT_TYPE_PERCENTAGE.equalsIgnoreCase(alertsObj.getCountType())){
					percentRadioBtId.setChecked(true);
					valueRadioBtId.setChecked(false);
					percentTbId.setValue(alertsObj.getCountValue());
					valueTbId.setValue("");
					percentTbId.setDisabled(false);
					valueTbId.setDisabled(true);
				}
				else if(OCConstants.LOYALTY_CARDS_AVAILABLE_COUNT_TYPE_VALUE.equalsIgnoreCase(alertsObj.getCountType())){
					percentRadioBtId.setChecked(false);
					valueRadioBtId.setChecked(true);
					percentTbId.setValue("");
					valueTbId.setValue(alertsObj.getCountValue());
					percentTbId.setDisabled(true);
					valueTbId.setDisabled(false);
				}
			}
			else{
				enableAlertsChkId.setChecked(false);
				alertsDivId.setVisible(false);
			}
		}catch(Exception e){
			logger.error("Exception while setting alert data...",e);
		}

	}
	
	/*private String fetchHtml(String urlStr) {
		try {
			try {
				StringBuffer pageSb = new StringBuffer();
				URL url = new URL(urlStr);
				URLConnection conn = url.openConnection();
				logger.debug("Connection opened to the specified url :" + urlStr);
				DataInputStream in = new DataInputStream (conn.getInputStream ()) ;
				BufferedReader d = new BufferedReader(new InputStreamReader(in));
				logger.debug("Reader obj created");

				String lineStr=null;

				while( (lineStr = d.readLine()) != null ) {
					pageSb.append(lineStr+" ");
				} // while

				logger.debug("Read the data from the URL, data length:" + pageSb.length());
				in.close();
				d.close();

				String content = pageSb.toString(); 

				if(content == null) {
					Messagebox.show("Invalid HTML: Unable to fetch HTML content from the specified URL.",
							"Error", Messagebox.OK, Messagebox.ERROR);
					return null;
				}
				return content;

			} catch (MalformedURLException e) {
				Messagebox.show("Malformed URL: Unable to fetch the web-page from the specified URL.",
						"Error", Messagebox.OK, Messagebox.ERROR);
				logger.error("MalformedURLException : ", e);
			} catch (IOException e) {
				Messagebox.show("Network problem experienced while fetching the page. "
						, "Error", Messagebox.OK, Messagebox.ERROR);
				logger.error("IOException : ", e);
			}
		} catch (Exception e) {
			Messagebox.show("Unknown exception occurred while fetching the page. "
					, "Error", Messagebox.OK, Messagebox.ERROR);
			logger.error("Exception : ", e);
		}
		return null;
	}//fetchHtml
*/
	private void setDefaultPortalSettings() {
		try{
			Long orgId= GetUser.getUserObj().getUserOrganization().getUserOrgId();
			List<Listitem> orgList = selOrgLbId.getItems();
			for (Listitem listitem : orgList) {
				if(listitem.getValue() != null && (Long)listitem.getValue() == orgId.longValue()) {
					selOrgLbId.setSelectedItem(listitem);
					logger.debug("Selected orgId::::" +listitem.getValue());
					break;
				}
			}
			LoyaltySettingsDao loyaltySettingsDao = (LoyaltySettingsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_SETTINGS_DAO);
			loyaltySettings = loyaltySettingsDao.findByOrgId(orgId);
			UsersDao usersDao = (UsersDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
			userOrg = usersDao.findByOrgId(orgId);
			Long userId=usersDao.getOwnerofOrg(userOrg.getUserOrgId());
			Users user=usersDao.find(userId);
			if(loyaltySettings != null){
				if(loyaltySettings.getUrlStr()!=null) {
				String url = loyaltySettings.getUrlStr().replace("http://", "");
				url = url.replace("https://", "");
				urlTbId.setValue(url);
				}
				if(loyaltySettings.getEmail()!=null) {
					emailTBoxId.setValue(loyaltySettings.getEmail());
				}
				if(loyaltySettings.getMobile()!=null) {
					mobileTBoxId.setValue(loyaltySettings.getMobile().toString());
				}

				setColor();
				setBrandColor();
				setbalanceCardColor();
				setbalanceCardTextColor();

				String LtyPath = PropertyUtil.getPropertyValue("loyaltyPortalParentDirectory").trim()+"/" +selOrgLbId.getSelectedItem().getLabel()+"/";
				String path = PropertyUtil.getPropertyValue("usersParentDirectory").trim() +"/"+user.getUserName()+"/RewardApp/";
				String imgName = loyaltySettings.getPath().contains(LtyPath)?loyaltySettings.getPath().replace(LtyPath, ""):loyaltySettings.getPath().replace(path, "");
				logoTbId.setValue(imgName);
				logoTbId.setDisabled(true);
				String homePageImgName=loyaltySettings.getHomePageImagePath()!=null?(loyaltySettings.getHomePageImagePath().contains(LtyPath)?loyaltySettings.getHomePageImagePath().replace(LtyPath, ""):loyaltySettings.getHomePageImagePath().replace(path, "")):"";
				homePageImageTbId.setValue(homePageImgName);
				homePageImageTbId.setDisabled(true);

				List<Listitem> typeList = ltyTypeId.getItems();
				for (Listitem type : typeList) {
					if(type.getValue() != null && loyaltySettings.getLoyaltyType()!=null && type.getValue().toString().equalsIgnoreCase(loyaltySettings.getLoyaltyType())) {
						ltyTypeId.setSelectedItem(type);
						break;
					}
				}
				List<Listitem> fontList = fontId.getItems();
				for (Listitem type : fontList) {
					if(type.getValue() != null && type.getValue().toString().equalsIgnoreCase(loyaltySettings.getFontName())) {
						fontId.setSelectedItem(type);
						break;
					}
				}
				bannerNameTbId.setValue(loyaltySettings.getBannerName());
				tabNameTbId.setValue(loyaltySettings.getTabName());
				String tabImgName=loyaltySettings.getTabImagePath()!=null?loyaltySettings.getTabImagePath().replace(path, ""):"";
				tabImageTbId.setValue(tabImgName);
				tabImageTbId.setDisabled(true);
				
			}
			if(userOrg!=null && userOrg.getBannerPath()!=null) {
				String imgUrl = PropertyUtil.getPropertyValue("ImageServerUrl");
				String bannerPath = userOrg.getBannerPath().replace(imgUrl +"UserData/"+user.getUserName()+"/","");
				bannerPath = bannerPath.replace(PropertyUtil.getPropertyValue("usersParentDirectory").trim() +"/"+user.getUserName()+"/","");
				bannerPath = bannerPath.replace("RewardApp"+"/","");
				String logoImgName = userOrg.getBannerPath()!=null?bannerPath:"";
				mobileApplogoTbId.setValue(logoImgName);
				mobileApplogoTbId.setDisabled(true);
			}
		}catch(Exception e){
			logger.error("Exception while setting portal details...",e);
		}
	}

	private void setThemes() {
		Comboitem ci = null;
		
		ci = new Comboitem();
		ci.setLabel("Select theme");
		ci.setValue(null);
		ci.setParent(themeId);
		themeId.setSelectedItem(ci);
		
		ci = new Comboitem();
		ci.setImage("/images/loyalty/Default.jpg");
		ci.setLabel("Default");
		ci.setValue("#DBDBDB");
		ci.setParent(themeId);
		
		ci = new Comboitem();
		ci.setImage("/images/loyalty/Blue1.jpg");
		ci.setLabel("Blue1");
		ci.setValue("#035398");
		ci.setParent(themeId);
		
		ci = new Comboitem();
		ci.setImage("/images/loyalty/Blue2.jpg");
		ci.setLabel("Blue2");
		ci.setValue("#035398");
		ci.setParent(themeId);
		
		ci = new Comboitem();
		ci.setImage("/images/loyalty/Brown.jpg");
		ci.setLabel("Brown");
		ci.setValue("#420001");
		ci.setParent(themeId);
		
		ci = new Comboitem();
		ci.setImage("/images/loyalty/cayn.jpg");
		ci.setLabel("Cyan");
		ci.setValue("#0B92C9");
		ci.setParent(themeId);
		
		ci = new Comboitem();
		ci.setImage("/images/loyalty/Green1.jpg");
		ci.setLabel("Green");
		ci.setValue("#598925");
		ci.setParent(themeId);
		
		ci = new Comboitem();
		ci.setImage("/images/loyalty/Green Occur.jpg");
		ci.setLabel("Green1");
		ci.setValue("#628A81");
		ci.setParent(themeId);
		
		ci = new Comboitem();
		ci.setImage("/images/loyalty/orange.jpg");
		ci.setLabel("Orange");
		ci.setValue("#D24205");
		ci.setParent(themeId);
		
		ci = new Comboitem();
		ci.setImage("/images/loyalty/pink.jpg");
		ci.setLabel("Pink");
		ci.setValue("#E31C79");
		ci.setParent(themeId);
		
		ci = new Comboitem();
		ci.setImage("/images/loyalty/Purple.jpg");
		ci.setLabel("Purple");
		ci.setValue("#581656");
		ci.setParent(themeId);
		
		ci = new Comboitem();
		ci.setImage("/images/loyalty/Red1.jpg");
		ci.setLabel("Red");
		ci.setValue("#90171C");
		ci.setParent(themeId);
		
		ci = new Comboitem();
		ci.setImage("/images/loyalty/white2.jpg");
		ci.setLabel("White");
		ci.setValue("#FFFFFF");
		ci.setParent(themeId);
		
		ci = new Comboitem();
		ci.setImage("/images/loyalty/Black.jpg");
		ci.setLabel("Black");
		ci.setValue("#000000");
		ci.setParent(themeId);
		//APP-4304
		ci = new Comboitem();
		ci.setImage("/images/loyalty/Blue3.jpg");
		ci.setLabel("Blue3");
		ci.setValue("#122344");
		ci.setParent(themeId);
		
		
	}
	
	private void setbalanceCardTextThemeIds() {
		Comboitem ci = null;
		
		ci = new Comboitem();
		ci.setLabel("Select theme");
		ci.setValue(null);
		ci.setParent(balanceCardTextThemeId);
		balanceCardTextThemeId.setSelectedItem(ci);
		
		ci = new Comboitem();
		ci.setImage("/images/loyalty/Default.jpg");
		ci.setLabel("Default");
		ci.setValue("#DBDBDB");
		ci.setParent(balanceCardTextThemeId);
		
		ci = new Comboitem();
		ci.setImage("/images/loyalty/Blue1.jpg");
		ci.setLabel("Blue1");
		ci.setValue("#035398");
		ci.setParent(balanceCardTextThemeId);
		
		ci = new Comboitem();
		ci.setImage("/images/loyalty/Blue2.jpg");
		ci.setLabel("Blue2");
		ci.setValue("#035398");
		ci.setParent(balanceCardTextThemeId);
		
		ci = new Comboitem();
		ci.setImage("/images/loyalty/Brown.jpg");
		ci.setLabel("Brown");
		ci.setValue("#420001");
		ci.setParent(balanceCardTextThemeId);
		
		ci = new Comboitem();
		ci.setImage("/images/loyalty/cayn.jpg");
		ci.setLabel("Cyan");
		ci.setValue("#0B92C9");
		ci.setParent(balanceCardTextThemeId);
		
		ci = new Comboitem();
		ci.setImage("/images/loyalty/Green1.jpg");
		ci.setLabel("Green");
		ci.setValue("#598925");
		ci.setParent(balanceCardTextThemeId);
		
		ci = new Comboitem();
		ci.setImage("/images/loyalty/Green Occur.jpg");
		ci.setLabel("Green1");
		ci.setValue("#628A81");
		ci.setParent(balanceCardTextThemeId);
		
		ci = new Comboitem();
		ci.setImage("/images/loyalty/orange.jpg");
		ci.setLabel("Orange");
		ci.setValue("#D24205");
		ci.setParent(balanceCardTextThemeId);
		
		ci = new Comboitem();
		ci.setImage("/images/loyalty/pink.jpg");
		ci.setLabel("Pink");
		ci.setValue("#CB0153");
		ci.setParent(balanceCardTextThemeId);
		
		ci = new Comboitem();
		ci.setImage("/images/loyalty/Purple.jpg");
		ci.setLabel("Purple");
		ci.setValue("#581656");
		ci.setParent(balanceCardTextThemeId);
		
		ci = new Comboitem();
		ci.setImage("/images/loyalty/Red1.jpg");
		ci.setLabel("Red");
		ci.setValue("#90171C");
		ci.setParent(balanceCardTextThemeId);
		
		ci = new Comboitem();
		ci.setImage("/images/loyalty/white2.jpg");
		ci.setLabel("White");
		ci.setValue("#FFFFFF");
		ci.setParent(balanceCardTextThemeId);
		
		ci = new Comboitem();
		ci.setImage("/images/loyalty/Black.jpg");
		ci.setLabel("Black");
		ci.setValue("#000000");
		ci.setParent(balanceCardTextThemeId);
		//APP-4304
		ci = new Comboitem();
		ci.setImage("/images/loyalty/Blue3.jpg");
		ci.setLabel("Blue3");
		ci.setValue("#122344");
		ci.setParent(balanceCardTextThemeId);
		
		
		
	}
	private void setBrandingThemes() {
		Comboitem ci = null;
		
		ci = new Comboitem();
		ci.setLabel("Select theme");
		ci.setValue(null);
		ci.setParent(brandingThemeId);
		brandingThemeId.setSelectedItem(ci);
		
		ci = new Comboitem();
		ci.setImage("/images/loyalty/Default.jpg");
		ci.setLabel("Default");
		ci.setValue("#DBDBDB");
		ci.setParent(brandingThemeId);
		
		ci = new Comboitem();
		ci.setImage("/images/loyalty/Blue1.jpg");
		ci.setLabel("Blue1");
		ci.setValue("#035398");
		ci.setParent(brandingThemeId);
		
		ci = new Comboitem();
		ci.setImage("/images/loyalty/Blue2.jpg");
		ci.setLabel("Blue2");
		ci.setValue("#035398");
		ci.setParent(brandingThemeId);
		
		ci = new Comboitem();
		ci.setImage("/images/loyalty/Brown.jpg");
		ci.setLabel("Brown");
		ci.setValue("#420001");
		ci.setParent(brandingThemeId);
		
		ci = new Comboitem();
		ci.setImage("/images/loyalty/cayn.jpg");
		ci.setLabel("Cyan");
		ci.setValue("#0B92C9");
		ci.setParent(brandingThemeId);
		
		ci = new Comboitem();
		ci.setImage("/images/loyalty/Green1.jpg");
		ci.setLabel("Green");
		ci.setValue("#598925");
		ci.setParent(brandingThemeId);
		
		ci = new Comboitem();
		ci.setImage("/images/loyalty/Green Occur.jpg");
		ci.setLabel("Green1");
		ci.setValue("#628A81");
		ci.setParent(brandingThemeId);
		
		ci = new Comboitem();
		ci.setImage("/images/loyalty/orange.jpg");
		ci.setLabel("Orange");
		ci.setValue("#D24205");
		ci.setParent(brandingThemeId);
		
		ci = new Comboitem();
		ci.setImage("/images/loyalty/pink.jpg");
		ci.setLabel("Pink");
		ci.setValue("#E31C79");
		ci.setParent(brandingThemeId);
		
		ci = new Comboitem();
		ci.setImage("/images/loyalty/Purple.jpg");
		ci.setLabel("Purple");
		ci.setValue("#581656");
		ci.setParent(brandingThemeId);
		
		ci = new Comboitem();
		ci.setImage("/images/loyalty/Purple1.jpg");
		ci.setLabel("Purple1");
		ci.setValue("#7c25d0");
		ci.setParent(brandingThemeId);
		
		ci = new Comboitem();
		ci.setImage("/images/loyalty/Red1.jpg");
		ci.setLabel("Red");
		ci.setValue("#90171C");
		ci.setParent(brandingThemeId);
		
		ci = new Comboitem();
		ci.setImage("/images/loyalty/white2.jpg");
		ci.setLabel("White");
		ci.setValue("#FFFFFF");
		ci.setParent(brandingThemeId);
		
		ci = new Comboitem();
		ci.setImage("/images/loyalty/Black.jpg");
		ci.setLabel("Black");
		ci.setValue("#000000");
		ci.setParent(brandingThemeId);
		
		
		ci = new Comboitem();
		ci.setImage("/images/loyalty/lemon.jpg");
		ci.setLabel("Lemon");
		ci.setValue("#e8de89");
		ci.setParent(brandingThemeId);
		//APP-4304
		ci = new Comboitem();
		ci.setImage("/images/loyalty/Blue3.jpg");
		ci.setLabel("Blue3");
		ci.setValue("#122344");
		ci.setParent(brandingThemeId);
		

		ci = new Comboitem();
		ci.setImage("/images/loyalty/Grey.png");
		ci.setLabel("Grey");
		ci.setValue("#97857b");
		ci.setParent(brandingThemeId);
		
	}	
		
		
	
	private void setbalanceCardThemeIds() {
		Comboitem ci = null;
		
		ci = new Comboitem();
		ci.setLabel("Select theme");
		ci.setValue(null);
		ci.setParent(balanceCardThemeId);
		balanceCardThemeId.setSelectedItem(ci);
		
		ci = new Comboitem();
		ci.setImage("/images/loyalty/Default.jpg");
		ci.setLabel("Default");
		ci.setValue("#DBDBDB");
		ci.setParent(balanceCardThemeId);
		
		ci = new Comboitem();
		ci.setImage("/images/loyalty/Blue1.jpg");
		ci.setLabel("Blue1");
		ci.setValue("#035398");
		ci.setParent(balanceCardThemeId);
		
		ci = new Comboitem();
		ci.setImage("/images/loyalty/Blue2.jpg");
		ci.setLabel("Blue2");
		ci.setValue("#035398");
		ci.setParent(balanceCardThemeId);
		
		ci = new Comboitem();
		ci.setImage("/images/loyalty/Brown.jpg");
		ci.setLabel("Brown");
		ci.setValue("#420001");
		ci.setParent(balanceCardThemeId);
		
		ci = new Comboitem();
		ci.setImage("/images/loyalty/cayn.jpg");
		ci.setLabel("Cyan");
		ci.setValue("#0B92C9");
		ci.setParent(balanceCardThemeId);
		
		ci = new Comboitem(); 
		ci.setImage("/images/loyalty/Green1.jpg");
		ci.setLabel("Green");
		ci.setValue("#598925");
		ci.setParent(balanceCardThemeId);
		
		ci = new Comboitem();
		ci.setImage("/images/loyalty/Green Occur.jpg");
		ci.setLabel("Green1");
		ci.setValue("#628A81");
		ci.setParent(balanceCardThemeId);
		
		ci = new Comboitem();
		ci.setImage("/images/loyalty/orange.jpg");
		ci.setLabel("Orange");
		ci.setValue("#D24205");
		ci.setParent(balanceCardThemeId);
		
		ci = new Comboitem();
		ci.setImage("/images/loyalty/pink.jpg");
		ci.setLabel("Pink");
		ci.setValue("#CB0153");
		ci.setParent(balanceCardThemeId);
		
		ci = new Comboitem();
		ci.setImage("/images/loyalty/Purple.jpg");
		ci.setLabel("Purple");
		ci.setValue("#581656");
		ci.setParent(balanceCardThemeId);
		
		ci = new Comboitem();
		ci.setImage("/images/loyalty/Red1.jpg");
		ci.setLabel("Red");
		ci.setValue("#90171C");
		ci.setParent(balanceCardThemeId);
		
		ci = new Comboitem();
		ci.setImage("/images/loyalty/white2.jpg");
		ci.setLabel("White");
		ci.setValue("#FFFFFF");
		ci.setParent(balanceCardThemeId);
		
		ci = new Comboitem();
		ci.setImage("/images/loyalty/Black.jpg");
		ci.setLabel("Black");
		ci.setValue("#000000");
		ci.setParent(balanceCardThemeId);
        //APP-4304
		ci = new Comboitem();
		ci.setImage("/images/loyalty/Blue3.jpg");
		ci.setLabel("Blue3");
		ci.setValue("#122344");
		ci.setParent(balanceCardThemeId);
		
	}

	private void setUserOrg() {
		try {
			Components.removeAllChildren(selOrgLbId);
			UsersDao usersDao = (UsersDao) ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
			List<UserOrganization> orgList	= usersDao.findAllOrganizations();
			
			if(orgList == null) {
				logger.debug("no organization list exist from the DB...");
				return;
			}
			
			Listitem tempList = new Listitem("--Select--");
			tempList.setParent(selOrgLbId);

			Listitem tempItem = null;
			for (UserOrganization userOrganization : orgList) {
				//set Organization Name
				if(userOrganization.getOrganizationName() == null || userOrganization.getOrganizationName().trim().equals("")) continue;
				tempItem = new Listitem(userOrganization.getOrganizationName().trim(),userOrganization.getUserOrgId());
				
				tempItem.setParent(selOrgLbId);
			} // for
			selOrgLbId.setSelectedIndex(0);
		} // setUserOrg()
		catch(Exception e) {
			logger.error("Exception ::",e);
		}
	}
	
	public void setFonts() {

		try {
			Components.removeAllChildren(fontId);
			
			Listitem tempList = new Listitem("--Select--");
			tempList.setParent(fontId);

			Listitem tempItem = null;
			for(int i=0; i<fontStr.length; i++) {
				//set Font Name
				tempItem = new Listitem(fontStr[i],fontStr[i]);
				
				tempItem.setParent(fontId);
			}
			fontId.setSelectedIndex(0);
		}
		catch(Exception e) {
			logger.error("Exception ::",e);
		}
	
	}
	
	public void onSelect$selOrgLbId() {
		try {
			LoyaltySettingsDao loyaltySettingsDao = (LoyaltySettingsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_SETTINGS_DAO);
			UsersDao usersDao = (UsersDao) ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);

			loyaltySettings = loyaltySettingsDao.findByOrgId(Long.parseLong(selOrgLbId.getSelectedItem().getValue().toString()));
			if(loyaltySettings != null){

				List<Listitem> orgList = selOrgLbId.getItems();
				for (Listitem listitem : orgList) {
					if(listitem.getValue() == loyaltySettings.getUserOrgId()) {
						selOrgLbId.setSelectedItem(listitem);
						break;
					}
				}
				if(loyaltySettings.getUrlStr()!=null) {
				String url = loyaltySettings.getUrlStr().replace("http://", "");
				url = url.replace("https://", "");
				urlTbId.setValue(url);
				}
				setColor();
				setBrandColor();
				setbalanceCardColor();
				setbalanceCardTextColor();

				Long userId=usersDao.getOwnerofOrg(selOrgLbId.getSelectedItem().getValue());
				Users user=usersDao.find(userId);
				String LtyPath = PropertyUtil.getPropertyValue("loyaltyPortalParentDirectory").trim()+"/" +selOrgLbId.getSelectedItem().getLabel()+"/";
				String path = PropertyUtil.getPropertyValue("usersParentDirectory").trim() +"/"+user.getUserName()+"/RewardApp/";
				if(loyaltySettings.getPath()!=null) {
				String imgName = loyaltySettings.getPath().contains(LtyPath)?loyaltySettings.getPath().replace(LtyPath, ""):loyaltySettings.getPath().replace(path, "");
				logoTbId.setValue(imgName);
				logoTbId.setDisabled(true);
				}
				if(loyaltySettings.getHomePageImagePath()!=null) {
				String homePageImgName = loyaltySettings.getHomePageImagePath().contains(LtyPath)?loyaltySettings.getHomePageImagePath().replace(LtyPath, ""):loyaltySettings.getHomePageImagePath().replace(path, "");
				homePageImageTbId.setValue(homePageImgName);
				homePageImageTbId.setDisabled(true);
				}
				
				userOrg = usersDao.findByOrgId(selOrgLbId.getSelectedItem().getValue());
				String imgUrl = PropertyUtil.getPropertyValue("ImageServerUrl");
				String bannerPath ="";
				if(userOrg.getBannerPath()!=null) {
					bannerPath= userOrg.getBannerPath().replace(imgUrl +"UserData/"+user.getUserName()+"/","");
				}
				bannerPath = bannerPath.replace(PropertyUtil.getPropertyValue("usersParentDirectory").trim() +"/"+user.getUserName()+"/","");
				bannerPath = bannerPath.replace("RewardApp"+"/","");
				String logoImgName = userOrg.getBannerPath()!=null?bannerPath:"";
				mobileApplogoTbId.setValue(logoImgName);
				mobileApplogoTbId.setDisabled(true);

				List<Listitem> typeList = ltyTypeId.getItems();
				for (Listitem type : typeList) {
					if(type.getValue() != null && loyaltySettings.getLoyaltyType()!=null && type.getValue().toString().equalsIgnoreCase(loyaltySettings.getLoyaltyType())) {
						ltyTypeId.setSelectedItem(type);
						break;
					}
				}
				List<Listitem> fontList = fontId.getItems();
				for (Listitem type : fontList) {
					if(type.getValue() != null && type.getValue().toString().equalsIgnoreCase(loyaltySettings.getFontName())) {
						fontId.setSelectedItem(type);
						break;
					}
				}
				bannerNameTbId.setValue(loyaltySettings.getBannerName());
				tabNameTbId.setValue(loyaltySettings.getTabName());
				String tabImgName=loyaltySettings.getTabImagePath()!=null?loyaltySettings.getTabImagePath().replace(path, ""):"";
				tabImageTbId.setValue(tabImgName);
				tabImageTbId.setDisabled(true);
			}else {
				gMedia = null;
				themeId.setSelectedIndex(0);
				brandingThemeId.setSelectedIndex(0);
				balanceCardThemeId.setSelectedIndex(0);
				balanceCardTextThemeId.setSelectedIndex(0);
				urlTbId.setValue("");
				logoTbId.setValue("");
				homePageImageTbId.setValue("");
				tabNameTbId.setValue("");
				tabImageTbId.setValue("");
				mobileApplogoTbId.setValue("");
				ltyTypeId.setSelectedIndex(0);
				emailTBoxId.setValue("");
				mobileTBoxId.setValue("");
				fontId.setSelectedIndex(0);
				logoTbId.setDisabled(false);
				bannerNameTbId.setValue("");
			}
		}
		catch(Exception e) {
			logger.error("Exception ::",e);
		}
	}
	
	private void setColor() {
		List<Comboitem> list = themeId.getItems();
		for(Comboitem ci : list) {
			if(ci.getValue() != null && loyaltySettings.getColorCode()!=null && loyaltySettings.getColorCode().equalsIgnoreCase(ci.getValue().toString())) {
				themeId.setSelectedItem(ci);
				break;
			}
		}
	}
	private void setBrandColor() {
		List<Comboitem> list = brandingThemeId.getItems();
		if(loyaltySettings.getHomePageColorCode()!=null) {
		for(Comboitem ci : list) {
			if(ci.getValue() != null && loyaltySettings.getHomePageColorCode()!=null && loyaltySettings.getHomePageColorCode().equalsIgnoreCase(ci.getValue().toString())) {
				brandingThemeId.setSelectedItem(ci);
				break;
			}
		}
		}
	}
	private void setbalanceCardColor() {
		List<Comboitem> list = balanceCardThemeId.getItems();
		if(loyaltySettings.getBalanceCardColorCode()!=null) {
		for(Comboitem ci : list) {
			if(ci.getValue() != null && loyaltySettings.getBalanceCardColorCode()!=null && loyaltySettings.getBalanceCardColorCode().equalsIgnoreCase(ci.getValue().toString())) {
				balanceCardThemeId.setSelectedItem(ci);
				break;
			}
		}
		}
	}
	private void setbalanceCardTextColor() {
		List<Comboitem> list = balanceCardTextThemeId.getItems();
		if(loyaltySettings.getBalanceCardTextColorCode()!=null) {
		for(Comboitem ci : list) {
			if(ci.getValue() != null && loyaltySettings.getBalanceCardTextColorCode()!=null && loyaltySettings.getBalanceCardTextColorCode().equalsIgnoreCase(ci.getValue().toString())) {
				balanceCardTextThemeId.setSelectedItem(ci);
				break;
			}
		}
		}
	}

	public void onUpload$browseBtnId(UploadEvent event) {
		browse(event.getMedia());
	}
	
	public void browse(Media media) {
		logger.info("Browse is called");
		try {
			if(!Utility.validateUploadFilName(media.getName()) || media.getName().contains("'")) {
				MessageUtil.setMessage("Image names should be alpha-numeric. Special characters allowed are A-Z, a-z, 0-9, &, +, -, =, @, _ and space, If any other characters are used, images will not be uploaded.", "color:red", "TOP");
			return ;
			}
		} catch (Exception e) {
			
			logger.error("** Exception :",e);
		}
		logoTbId.setValue(media.getName());
		logoTbId.setDisabled(true);
		gMedia = media;
	}
	public void onUpload$browseLogoBtnId(UploadEvent event) {
		browseForLogo(event.getMedia());
	}
	public void onUpload$browseHomePageBtnId(UploadEvent event) {
		browseForHomePage(event.getMedia());
	}
	
	public void onUpload$browseTabImageBtnId(UploadEvent event) {
		browseForTabImage(event.getMedia());
	}
	
	public void browseForLogo(Media media) {
		logger.info("Logo Browse is called");
		//String imagName = media.getName();
		try {
			if(!Utility.validateUploadFilName(media.getName()) || media.getName().contains("'")) {
				MessageUtil.setMessage("Image names should be alpha-numeric. Special characters allowed are A-Z, a-z, 0-9, &, +, -, =, @, _ and space, If any other characters are used, images will not be uploaded.", "color:red", "TOP");
			return ;
			}
			
		} catch (Exception e) {
			
			logger.error("** Exception :",e);
		}
		mobileApplogoTbId.setValue(media.getName());
		mobileApplogoTbId.setDisabled(true);
		lMedia = media;
	}
	public void browseForHomePage(Media media) {
		logger.info("Home image Browse is called");
		try {
			if(!Utility.validateUploadFilName(media.getName()) || media.getName().contains("'")) {
				MessageUtil.setMessage("Image names should be alpha-numeric. Special characters allowed are A-Z, a-z, 0-9, &, +, -, =, @, _ and space, If any other characters are used, images will not be uploaded.", "color:red", "TOP");
			return ;
			}
			
		} catch (Exception e) {
			
			logger.error("** Exception :",e);
		}
		homePageImageTbId.setValue(media.getName());
		homePageImageTbId.setDisabled(true);
		hMedia = media;
	}
	public void browseForTabImage(Media media) {
		logger.info("Tab image Browse is called");
		try {
			if(!Utility.validateUploadFilName(media.getName()) || media.getName().contains("'")) {
				MessageUtil.setMessage("Image names should be alpha-numeric. Special characters allowed are A-Z, a-z, 0-9, &, +, -, =, @, _ and space, If any other characters are used, images will not be uploaded.", "color:red", "TOP");
			return ;
			}
			
		} catch (Exception e) {
			
			logger.error("** Exception :",e);
		}
		tabImageTbId.setValue(media.getName());
		tabImageTbId.setDisabled(true);
		tMedia = media;
	}

	/*public void onClick$uploadBtnId() {
		upload();
	}*/
	
	public boolean upload(String imageType,Users user){
		
		MessageUtil.clearMessage();
		Media media=null;
		if(imageType.equalsIgnoreCase("logo")) media = gMedia;
		else if(imageType.equalsIgnoreCase("homepage")) media = hMedia;
		else if(imageType.equalsIgnoreCase("companylogo")) media = lMedia;
		else if(imageType.equalsIgnoreCase("tablogo")) media = tMedia;
		//Media media = gMedia; 
		
		/*if(media == null) {
			MessageUtil.setMessage("Please select a file.", "color:red", "TOP");
			return;
		}*/
		if(media!=null) {
		logger.info("File name : " + media.getName());
		String ext = FileUtil.getFileNameExtension(media.getName());
		if(ext == null) {
			MessageUtil.setMessage("Upload  jpg or png or gif files only.","color:red","TOP");
			return false;
		}
		if(!ext.equalsIgnoreCase("jpeg") && !ext.equalsIgnoreCase("jpg") && 
				  !ext.equalsIgnoreCase("gif") && !ext.equalsIgnoreCase("png") &&
					 !ext.equalsIgnoreCase("bmp")) {
			MessageUtil.setMessage("Upload jpg or png or gif file only.","color:red","TOP");
			return false;
		}
		uploadLogo(media,imageType,user);
		}
		return true;
	}
	
	public boolean uploadCompanyImage(Users user){
		
		MessageUtil.clearMessage();
		Media media = lMedia; 
		
		boolean isAnySpecChar = isAnySpecialChar(media);
		logger.debug("isAnySpecialChar ===============================7"+isAnySpecChar);
		if(isAnySpecChar){
			double fileSize = Math.ceil((media.getByteData().length))/10240;
			if(fileSize>10240){
				MessageUtil.setMessage(media.getName()+" cannot be uploaded.\n Reason: Size should not exceed 10 MB." , "color:red", "TOP");
			    return false;
			}
		}
		logger.info("File name : " + media.getName());
		String ext = FileUtil.getFileNameExtension(media.getName());
		if(ext == null) {
			MessageUtil.setMessage("Upload  jpg or png or gif files only.","color:red","TOP");
			return false;
		}
		if(!ext.equalsIgnoreCase("jpeg") && !ext.equalsIgnoreCase("jpg") && 
				  !ext.equalsIgnoreCase("gif") && !ext.equalsIgnoreCase("png") &&
					 !ext.equalsIgnoreCase("bmp")) {
			MessageUtil.setMessage("Upload jpg or png or gif file only.","color:red","TOP");
			return false;
		}
		uploadCompanyLogo(media,user);
		return true;
	}
	
	public void uploadLogo(Media media,String imgType,Users user) {
		try {
			if(logger.isDebugEnabled()) logger.debug("-- Just entered--");
			MessageUtil.clearMessage();
			Media m = (Media)media;
			/*if(selOrgLbId.getSelectedItem().getLabel().equalsIgnoreCase("--Select--")) {
				MessageUtil.setMessage("Please select the organization to upload the selected image.","color:red","TOP");
				return;
			}*/
			//String path = PropertyUtil.getPropertyValue("loyaltyPortalParentDirectory").trim() +"/"+selOrgLbId.getSelectedItem().getLabel();
			String path = PropertyUtil.getPropertyValue("usersParentDirectory").trim() +"/"+user.getUserName()+"/RewardApp";
			copyDataFromMediaToFile(path,m,imgType);
		} catch(Exception e) {
			logger.error("** Exception :",e);
		}
	}
	
	public void uploadCompanyLogo(Media media,Users user) {
		try {
			if(logger.isDebugEnabled()) logger.debug("-- Just entered--");
			MessageUtil.clearMessage();
			Media m = (Media)media;
			//String path = PropertyUtil.getPropertyValue("usersParentDirectory").trim() +"/"+user.getUserName();
			String path = PropertyUtil.getPropertyValue("usersParentDirectory").trim() +"/"+user.getUserName()+"/RewardApp";
			copyDataFromMediaToFile(path,m,"companylogo");
		} catch(Exception e) {
			logger.error("** Exception :",e);
		}
	}
	
	public boolean copyDataFromMediaToFile(String path,Media m,String imageType){
		/*String ext = FileUtil.getFileNameExtension(m.getName());
		if(ext != null && !ext.equalsIgnoreCase("jpeg") && !ext.equalsIgnoreCase("jpg") && 
				  !ext.equalsIgnoreCase("gif") && !ext.equalsIgnoreCase("png") &&
					 !ext.equalsIgnoreCase("bmp")){
			MessageUtil.setMessage("Upload jpg or png or gif file only.","color:red","BOTTOM");
			return false;
		}*/
		String textBoxValue = Constants.STRING_NILL;
		if(imageType.equalsIgnoreCase("logo")) textBoxValue=logoTbId.getValue();
		else if(imageType.equalsIgnoreCase("homepage")) textBoxValue=homePageImageTbId.getValue();
		else if(imageType.equalsIgnoreCase("companylogo")) textBoxValue=mobileApplogoTbId.getValue();
		else if(imageType.equalsIgnoreCase("tablogo")) textBoxValue=tabImageTbId.getValue();
		try{
			File file = new File(path);
			if(!file.exists()) {
				file.mkdirs();
			}
			byte[] fi = m.getByteData();
			BufferedInputStream in = new BufferedInputStream (new ByteArrayInputStream (fi)); 
			FileOutputStream out = new FileOutputStream (new File(file.getPath()+"/"+textBoxValue));
			//Copy the contents of the file to the output stream
			byte[] buf = new byte[1024];
			int count = 0;
			while ((count = in.read(buf)) >= 0){
				out.write(buf, 0, count);
			}
			in.close();
			out.close();
			
		}catch(Exception e){
			logger.error("Exception :: error occured while Uploading the Image");
			logger.error("Exception ::", e);
		}
		return true;
	}

	public void onClick$chckAvailbilityBtnId() {
		checkAvailbility(true);
	}
	
	private boolean checkAvailbility(boolean flag) {
		try {
			String url = urlTbId.getValue().trim();
			if(flag && (url == null || url.isEmpty())) {
				MessageUtil.setMessage("Please enter the url", "color:red", "TOP");
				return false;
			}
			url = "http://"+url;
			LoyaltySettingsDao loyaltySettingsDao = (LoyaltySettingsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_SETTINGS_DAO);
			//List<LoyaltySettings> list = loyaltySettingsDao.findByUrl(url);
			List<LoyaltySettings> list = loyaltySettingsDao.matchByUrl(url);

			if(list == null || list.size() == 0
					|| (list.size() == 1 && (Long)selOrgLbId.getSelectedItem().getValue() == list.get(0).getUserOrgId().longValue())) {
				if(flag) {
					MessageUtil.setMessage("The url entered is available.", "color:blue", "TOP");
				}
				return true;
			}else {
				MessageUtil.setMessage("The url entered is not available. Please enter a different url.", "color:red", "TOP");
				urlTbId.setFocus(true);
				return false;
			}
		}catch(Exception e) {
			logger.info("Exception ::",e);
			return false;
		}
	}

	public void onClick$themePreviewBtnId() {
		if(themeId.getSelectedItem().getValue() != null) {
			previewWin.setVisible(true);
			previewWin.setPosition("center");
			previewWin.doHighlighted();
			setPreviewThemes(themeId.getSelectedItem().getLabel().toString());
		}
		else {
			MessageUtil.setMessage("Please select the theme to preview.", "color:red", "TOP");
			return;
		}
	}
	public void onClick$brandingThemePreviewBtnId() {
		if(brandingThemeId.getSelectedItem().getValue() != null) {
			previewWin.setVisible(true);
			previewWin.setPosition("center");
			previewWin.doHighlighted();
			setPreviewThemes(brandingThemeId.getSelectedItem().getLabel().toString());
		}
		else {
			MessageUtil.setMessage("Please select the theme to preview.", "color:red", "TOP");
			return;
		}
	}
	public void onClick$balanceCardThemePreviewBtnId() {
		if(balanceCardThemeId.getSelectedItem().getValue() != null) {
			previewWin.setVisible(true);
			previewWin.setPosition("center");
			previewWin.doHighlighted();
			setPreviewThemes(balanceCardThemeId.getSelectedItem().getLabel().toString());
		}
		else {
			MessageUtil.setMessage("Please select the theme to preview.", "color:red", "TOP");
			return;
		}
	}
	public void onClick$balanceCardTextPreviewBtnId() {
		if(balanceCardTextThemeId.getSelectedItem().getValue() != null) {
			previewWin.setVisible(true);
			previewWin.setPosition("center");
			previewWin.doHighlighted();
			setPreviewThemes(balanceCardTextThemeId.getSelectedItem().getLabel().toString());
		}
		else {
			MessageUtil.setMessage("Please select the theme to preview.", "color:red", "TOP");
			return;
		}
	}
	
	
	private void setPreviewThemes(String value) {
		logger.info(" ::value ::"+value+" THEMES_MAP "+THEMES_MAP.get(value));
		Components.removeAllChildren(previewWin$contentDivId);
		Image img = new Image(THEMES_MAP.get(value));
		img.setStyle("margin-left:10px;");
		img.setParent(previewWin$contentDivId);
	}

	public void onClick$saveBtnId() {
		
		try {

			if(!validateFields()) {
				return;
			}
			
			//String url = "http://"+urlTbId.getValue();
		   // String isValidUrl = fetchHtml(url);
		   // InetAddress isValidUrl = validateUrl(url);
		   
		   /* if(isValidUrl == null) {
		    	return;
		    }*/
			
			if(loyaltySettings == null) {
				loyaltySettings = new LoyaltySettings();
			}
			if(selOrgLbId.getSelectedItem().getValue() != null) {
				loyaltySettings.setUserOrgId(Long.parseLong(selOrgLbId.getSelectedItem().getValue().toString()));
			}
			
			UsersDao usersDao = (UsersDao) ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
			Long ownerUserId =	usersDao.getOwnerofOrg(Long.parseLong(selOrgLbId.getSelectedItem().getValue().toString()));
			if(ownerUserId == null){
				MessageUtil.setMessage("There is no users for selected organization. Please select different organization.", "color:red", "TOP");
				selOrgLbId.setFocus(true);
				return;
			}
			
			loyaltySettings.setOrgOwner(ownerUserId);
			if(themeId.getSelectedItem().getValue()!=null)loyaltySettings.setColorCode(themeId.getSelectedItem().getValue().toString());
			
			if(!checkAvailbility(false)) {
				return;
			}
			String url = urlTbId.getValue().replace("http://", "");
			url = url.replace("https://", "");
			loyaltySettings.setUrlStr("http://"+url);

			if(ltyTypeId.getSelectedItem().getValue() != null) {
				loyaltySettings.setLoyaltyType(ltyTypeId.getSelectedItem().getValue().toString());
			}
			if(emailTBoxId.getValue().trim() != null) {
				loyaltySettings.setEmail(emailTBoxId.getValue().toString());
			}
			if(mobileTBoxId.getText().trim() != null) {
				loyaltySettings.setMobile(mobileTBoxId.getText());
			}
			//loyaltySettings.setUserId(GetUser.getUserObj().getUserId());
			LoyaltySettingsDao loyaltySettingsDao = (LoyaltySettingsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_SETTINGS_DAO);
			LoyaltySettingsDaoForDML loyaltySettingsDaoForDML = (LoyaltySettingsDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_SETTINGS_DAO_FOR_DML);
			//loyaltySettingsDao.saveOrUpdate(loyaltySettings);
			loyaltySettingsDaoForDML.saveOrUpdate(loyaltySettings);
			MessageUtil.setMessage("Portal settings saved successfully.", "color:blue", "TOP");
			//resetFields();
		}
		catch(Exception e) {
			logger.error("Exception ::",e);
		}
	}
	
	public void onClick$prerequsitesaveBtnId() throws Exception {
		
		logger.info("Entering saving prerequesite");
		Long orgId= GetUser.getUserObj().getUserOrganization().getUserOrgId();

		
		LoyaltySettingsDao loyaltySettingsDao = (LoyaltySettingsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_SETTINGS_DAO);
		LoyaltySettings loyaltySettings = loyaltySettingsDao.findByOrgId(orgId);
		if(loyaltySettings == null) {
			loyaltySettings = new LoyaltySettings();
		}
		
			try {

				logger.info("Entering saving prerequesite okk "+loyaltySettings);

				//firstNameChkBxId,lastNameChkBxId,mobileNumberChkBxId,emailAddressChkBxId
					
				loyaltySettings.setIncludeFirstname(firstNameChkBxId.isChecked());
				loyaltySettings.setIncludeLastname(lastNameChkBxId.isChecked());
				loyaltySettings.setIncludeMobilenumber(mobileNumberChkBxId.isChecked());
				loyaltySettings.setIncludeEmailaddress(emailAddressChkBxId.isChecked());
				
				loyaltySettings.setIncludeStreet(streetChkBxId.isChecked());
				loyaltySettings.setIncludeCity(cityChkBxId.isChecked());
				loyaltySettings.setIncludeState(stateChkBxId.isChecked());
				loyaltySettings.setIncludePostalCode(postalCodeChkBxId.isChecked());
				
				loyaltySettings.setIncludeCountry(countryChkBxId.isChecked());
				loyaltySettings.setIncludeBirthday(birthdayChkBxId.isChecked());
				loyaltySettings.setIncludeAnniversary(anniversaryChkBxId.isChecked());
				loyaltySettings.setIncludeGender(genderChkBxId.isChecked());
				
				
				LoyaltySettingsDaoForDML loyaltySettingsDaoForDML = (LoyaltySettingsDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_SETTINGS_DAO_FOR_DML);
				loyaltySettingsDaoForDML.saveOrUpdate(loyaltySettings);

				logger.info("Emailaddress value is"+emailAddressChkBxId.isChecked());
				MessageUtil.setMessage(" settings saved successfully.", "color:blue", "TOP");

				
			}catch(Exception e){

				logger.error("Exception ::", e);
			}
			//MessageUtil.setMessage("Digital receipt settings saved successfully.","color:green;");
			
			//}
	
		
	}
	private void defaultPrerequesiteSettings() {

		
		if(loyaltySettings!=null) {
		
		firstNameChkBxId.setChecked(loyaltySettings.isIncludeFirstname());
		lastNameChkBxId.setChecked(loyaltySettings.isIncludeLastname());
		mobileNumberChkBxId.setChecked(loyaltySettings.isIncludeMobilenumber());
		emailAddressChkBxId.setChecked(loyaltySettings.isIncludeEmailaddress());
		
		streetChkBxId.setChecked(loyaltySettings.isIncludeStreet());
		cityChkBxId.setChecked(loyaltySettings.isIncludeCity());
		stateChkBxId.setChecked(loyaltySettings.isIncludeState());
		postalCodeChkBxId.setChecked(loyaltySettings.isIncludePostalCode());
		
		countryChkBxId.setChecked(loyaltySettings.isIncludeCountry());
		birthdayChkBxId.setChecked(loyaltySettings.isIncludeBirthday());
		anniversaryChkBxId.setChecked(loyaltySettings.isIncludeAnniversary());
		genderChkBxId.setChecked(loyaltySettings.isIncludeGender());
		
		}

	}
	
	
	
	public void onClick$saveAppSettingsBtnId() {
		
		try {
			if(selOrgLbId.getSelectedItem().getValue() == null || selOrgLbId.getSelectedIndex() == 0) {
				MessageUtil.setMessage("Please select the organization.", "color:red","TOP");
				return;
			}
			if(loyaltySettings == null) {
				loyaltySettings = new LoyaltySettings();
			}
			if(selOrgLbId.getSelectedItem().getValue() != null) {
				loyaltySettings.setUserOrgId(Long.parseLong(selOrgLbId.getSelectedItem().getValue().toString()));
			}
			if(fontId.getSelectedItem().getValue() == null) {
				loyaltySettings.setFontName("Raleway");
				loyaltySettings.setFontURL(FONT_MAP.get("Raleway"));
			}else {
				loyaltySettings.setFontName(fontId.getSelectedItem().getValue());
				loyaltySettings.setFontURL(FONT_MAP.get(fontId.getSelectedItem().getValue()));
			}
			UsersDao usersDao = (UsersDao) ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
			Long ownerUserId =	usersDao.getOwnerofOrg(Long.parseLong(selOrgLbId.getSelectedItem().getValue().toString()));
			if(ownerUserId!=null)loyaltySettings.setOrgOwner(ownerUserId);
			if(brandingThemeId.getSelectedItem().getValue()!=null)loyaltySettings.setHomePageColorCode(brandingThemeId.getSelectedItem().getValue().toString());
			loyaltySettings.setBalanceCardColorCode(balanceCardThemeId.getSelectedItem().getValue()!=null?balanceCardThemeId.getSelectedItem().getValue().toString():
				(brandingThemeId.getSelectedItem().getValue()!=null?brandingThemeId.getSelectedItem().getValue().toString():
				(themeId.getSelectedItem().getValue()!=null?themeId.getSelectedItem().getValue().toString():themeId.getSelectedItem().getValue())));
			if(balanceCardTextThemeId.getSelectedItem().getValue()!=null)loyaltySettings.setBalanceCardTextColorCode(balanceCardTextThemeId.getSelectedItem().getValue().toString());
			UserOrganization org = usersDao.findByOrgId(selOrgLbId.getSelectedItem().getValue());
			Long userId=usersDao.getOwnerofOrg(org.getUserOrgId());
			Users user=usersDao.find(userId);
			String path=null;
			String homePageImagePath =null;
			if(loyaltySettings != null) {
							/*String newPath = PropertyUtil.getPropertyValue("loyaltyPortalParentDirectory").trim()+"/" 
												+selOrgLbId.getSelectedItem().getLabel()+"/"+logoTbId.getValue();*/
							String newPath = PropertyUtil.getPropertyValue("usersParentDirectory").trim() +"/"+user.getUserName()+"/RewardApp/"+logoTbId.getValue();
							String oldPath = loyaltySettings.getPath();
							if(!newPath.equalsIgnoreCase(oldPath) && gMedia!=null) {
								boolean isUploadSucess = upload("logo",user);
								if(!isUploadSucess) return;
								path = PropertyUtil.getPropertyValue("usersParentDirectory").trim() +"/"+user.getUserName()+"/RewardApp/"+logoTbId.getValue();
								loyaltySettings.setPath(path);
							}
							if(homePageImageTbId.getValue()!=null && !homePageImageTbId.getValue().isEmpty()) {
							/*String newHomePagePath = PropertyUtil.getPropertyValue("loyaltyPortalParentDirectory").trim()+"/" 
									+selOrgLbId.getSelectedItem().getLabel()+"/"+homePageImageTbId.getValue();*/
							String newHomePagePath = PropertyUtil.getPropertyValue("usersParentDirectory").trim() +"/"+user.getUserName()+"/RewardApp/"+homePageImageTbId.getValue();
							String homePageOldPath = loyaltySettings.getHomePageImagePath();
							if(!newHomePagePath.equalsIgnoreCase(homePageOldPath) && hMedia!=null) {
								boolean isUploadSucess = upload("homepage",user);
								if(!isUploadSucess) return;
								homePageImagePath = PropertyUtil.getPropertyValue("usersParentDirectory").trim() +"/"+user.getUserName()+"/RewardApp/"+homePageImageTbId.getValue();
								if(homePageImageTbId.getValue()!=null && !homePageImageTbId.getValue().isEmpty()) {
									loyaltySettings.setHomePageImagePath(homePageImagePath);
								}
							}
							}
						}
			else {
							boolean isUploadSucess = upload("logo",user);
							if(!isUploadSucess) return;
							path = PropertyUtil.getPropertyValue("usersParentDirectory").trim() +"/"+user.getUserName()+"/RewardApp/"+logoTbId.getValue();
							loyaltySettings.setPath(path);
							if(homePageImageTbId.getValue()!=null && !homePageImageTbId.getValue().isEmpty()) {
							boolean isHomePageUploadSucess = upload("homepage",user);
							if(!isHomePageUploadSucess) return;
							}
							homePageImagePath = PropertyUtil.getPropertyValue("usersParentDirectory").trim() +"/"+user.getUserName()+"/RewardApp/"+homePageImageTbId.getValue();
							if(homePageImageTbId.getValue()!=null && !homePageImageTbId.getValue().isEmpty()) {
								loyaltySettings.setHomePageImagePath(homePageImagePath);
							}
			}
						
						String newTabImagePath = PropertyUtil.getPropertyValue("usersParentDirectory").trim() +"/"+user.getUserName()+"/RewardApp/"+tabImageTbId.getValue();
						String tabImageOldPath = loyaltySettings.getTabImagePath()!=null?loyaltySettings.getTabImagePath():"";
						//String tabImagePath=null;
						if(!newTabImagePath.equalsIgnoreCase(tabImageOldPath) && tMedia!=null) {
							boolean isUploadSucess = upload("tablogo",user);
							if(!isUploadSucess) return;
							//tabImagePath = PropertyUtil.getPropertyValue("usersParentDirectory").trim() +"/"+user.getUserName()+"/RewardApp/"+tabImageTbId.getValue();
							if(tabImageTbId.getValue()!=null && !tabImageTbId.getValue().isEmpty()) {
								loyaltySettings.setTabImagePath(newTabImagePath);
							}
						}
						loyaltySettings.setUserId(ownerUserId);
						loyaltySettings.setBannerName(bannerNameTbId.getValue());
						loyaltySettings.setTabName(tabNameTbId.getValue());
			
						LoyaltySettingsDao loyaltySettingsDao = (LoyaltySettingsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_SETTINGS_DAO);
						LoyaltySettingsDaoForDML loyaltySettingsDaoForDML = (LoyaltySettingsDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_SETTINGS_DAO_FOR_DML);
						//loyaltySettingsDao.saveOrUpdate(loyaltySettings);
						loyaltySettingsDaoForDML.saveOrUpdate(loyaltySettings);
						
						if(org != null && mobileApplogoTbId.getValue()!=null && !mobileApplogoTbId.getValue().isEmpty()&& lMedia!=null) {
							/*String newPath = PropertyUtil.getPropertyValue("usersParentDirectory").trim()+"/" 
												+user.getUserName()+"/"+mobileApplogoTbId.getValue();*/
							String newPath = PropertyUtil.getPropertyValue("usersParentDirectory").trim()+"/" 
									+user.getUserName()+"/RewardApp/"+mobileApplogoTbId.getValue();
							String oldPath = org.getBannerPath();
							String logoPath =null;
							if(!newPath.equalsIgnoreCase(oldPath)) {
								boolean isUploadSucess = uploadCompanyImage(user);
								if(!isUploadSucess) return;
								logoPath = PropertyUtil.getPropertyValue("usersParentDirectory").trim()+"/"+user.getUserName()+"/RewardApp/"+mobileApplogoTbId.getValue();
								org.setBannerPath(logoPath);
							}
							//String logoPath = PropertyUtil.getPropertyValue("usersParentDirectory").trim()+"/"+user.getUserName()+"/"+mobileApplogoTbId.getValue();
						}
						UsersDaoForDML usersDaoForDML = (UsersDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.USERS_DAOForDML);
						usersDaoForDML.saveOrUpdate(org);
		MessageUtil.setMessage("Web App/Mobile App settings saved successfully.", "color:blue", "TOP");
		}
		catch(Exception e) {
			logger.error("Exception ::",e);
		}
		
	}
	private boolean validateFields() {
		if(selOrgLbId.getSelectedItem().getValue() == null || selOrgLbId.getSelectedIndex() == 0) {
			MessageUtil.setMessage("Please select the organization.", "color:red","TOP");
			return false;
		}
		if(urlTbId.getValue() == null || urlTbId.getValue().trim().isEmpty()) {
			MessageUtil.setMessage("Please enter the url.", "color:red","TOP");
			return false;
		}
		
		if(themeId.getSelectedItem().getValue() == null || themeId.getSelectedIndex() == 0) {
			MessageUtil.setMessage("Please select the theme.", "color:red","TOP");
			return false;
		}
		/*if(logoTbId.getValue() == null || logoTbId.getValue().trim().isEmpty()) {
			MessageUtil.setMessage("Please upload the logo.", "color:red","TOP");
			return false;
		}*/
		if(ltyTypeId.getSelectedItem().getValue() == null || ltyTypeId.getSelectedIndex() == 0) {
			MessageUtil.setMessage("Please select the loyalty type.", "color:red","TOP");
			return false;
		}
		/*if (emailTBoxId.getValue().trim().equals("") || emailTBoxId.getValue()==null) {
			MessageUtil.setMessage("Please provide email address", "color:red", "TOP");
			emailTBoxId.setFocus(true);
			return false;
		}
		if(mobileTBoxId.getValue().trim().equals("") || mobileTBoxId.getValue()==null) {
			MessageUtil.setMessage("Please provide mobile number", "color:red", "TOP");
			mobileTBoxId.setFocus(true);
			return false;
		}*/
		if(mobileTBoxId.getText().trim() != null && mobileTBoxId.getText().trim().length()>20) {
			MessageUtil.setMessage("mobile number limit is 20 digits", "color:red", "TOP");
			mobileTBoxId.setFocus(true);
			return false;
		}
		String emailStr = emailTBoxId.getText().trim();
		if(emailStr!=null && emailStr.length() >0 && !Utility.validateEmail(emailStr)) {
			MessageUtil.setMessage("Please enter a valid e-mail address", "Color:red", "Top");
			emailTBoxId.setFocus(true);
			return false;
		}
		if(mobileTBoxId.getText().trim() != null) {
			String patterns = mobileTBoxId.getValue();
			StringBuilder sb = new StringBuilder();
			//String number = "";
			char[] alph = patterns.toCharArray();
			for(char c:alph) {
				if(Character.isDigit(c) || Character.isWhitespace(c) || c=='+') {
					sb.append(c);
				}
				else {
					MessageUtil.setMessage("provide valid mobile number", "Color:red", "Top");
					mobileTBoxId.setFocus(true);
					return false;
				}
			}
		}
		return true;
	}

	/*private void resetFields() {
		gMedia = null;
		selOrgLbId.setSelectedIndex(0);
		themeId.setSelectedIndex(0);;
		urlTbId.setValue("");
		logoTbId.setValue("");
		ltyTypeId.setSelectedIndex(0);
		logoTbId.setDisabled(false);
	}*/
	
	public void onClick$changePwdAId() {
		changePwdWinId.setVisible(true);
		changePwdWinId.setPosition("center");
		changePwdWinId.doHighlighted();
		changePwdWinId$currPwdTbId.setValue("");
		changePwdWinId$newPwdTbId.setValue("");
		changePwdWinId$retypePwdTbId.setValue("");
	}
	
	public void onClick$updateBtnId$changePwdWinId() {
		try {
			if(logger.isDebugEnabled()) logger.debug("--Just Entered updatePassword()");
			
			String current = changePwdWinId$currPwdTbId.getValue();
			String newPassword = changePwdWinId$newPwdTbId.getValue();
			String reTypePassword = changePwdWinId$retypePwdTbId.getValue();
			
			if(current.trim().equals("")){
				MessageUtil.setMessage("Current password field cannot be left empty.", "color:red","TOP");
				changePwdWinId$currPwdTbId.setFocus(true);
				return;
			}
			if(newPassword.trim().equals("")) {
				MessageUtil.setMessage("New password field cannot be left empty.", "color:red","TOP"); 
				changePwdWinId$newPwdTbId.setFocus(true);
				return;
			}
			if(reTypePassword.trim().equals("")) {
				MessageUtil.setMessage("Retype password field cannot be left empty.", "color:red","TOP");
				changePwdWinId$retypePwdTbId.setFocus(true);
				return;
			}
			
			
			
			String pattern = "^(?=.{8,50}$)(?=(.*[A-Z]))(?=(.*[a-z]))(?=(.*[0-9]))(?=.*[-@!#$%^&-+=()]).*$";
			

			Pattern pwdPattern = Pattern.compile(pattern);

			if (newPassword == null || (!pwdPattern.matcher(newPassword.trim()).matches()) || reTypePassword == null
					|| (!pwdPattern.matcher(reTypePassword.trim()).matches())) {
				MessageUtil.setMessage(
						"Password must contain at least 8 characters,1 uppercase,1 lowercase,"
						+ "\n 1 special character (@!#$%^&+-=*'()) Special characters) and 1 number .", "color:red",
						"TOP");
				changePwdWinId$newPwdTbId.setFocus(true);
				return;
			}			 
			
			String encCurrPwd = EncryptDecryptLtyMembshpPwd.encryptSessionID(changePwdWinId$currPwdTbId.getValue());
			String encNewPwd = EncryptDecryptLtyMembshpPwd.encryptSessionID(changePwdWinId$newPwdTbId.getValue());
			
			LoyaltyThresholdAlertsDao loyaltyThresholdAlertsDao = (LoyaltyThresholdAlertsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_THRESHOLD_ALERTS_DAO);
			LoyaltyThresholdAlertsDaoForDML loyaltyThresholdAlertsDaoForDML = (LoyaltyThresholdAlertsDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_THRESHOLD_ALERTS_DAO_FOR_DML);

			LoyaltyThresholdAlerts loyaltyThresholdAlerts = loyaltyThresholdAlertsDao.findByUserId(GetUser.getUserObj().getUserId());
			if(loyaltyThresholdAlerts != null && loyaltyThresholdAlerts.getLtySecurityPwd() != null && !loyaltyThresholdAlerts.getLtySecurityPwd().isEmpty()){
				if(!encCurrPwd.equals(loyaltyThresholdAlerts.getLtySecurityPwd())){
					MessageUtil.setMessage("Current password is incorrect.", "color:red","TOP");
					changePwdWinId$currPwdTbId.setFocus(true);
					return;
				}
			}
			else {
				MessageUtil.setMessage("Password does not exist.", "color:red","TOP");
				changePwdWinId$currPwdTbId.setFocus(true);
				return;
			}
			if(!newPassword.equals(reTypePassword)) {
				 MessageUtil.setMessage("Both passwords must match.", "color:red","TOP");
				 changePwdWinId$retypePwdTbId.setFocus(true);
				 return;
			}
			
			MessageUtil.clearMessage(); 
			loyaltyThresholdAlerts.setLtySecurityPwd(encNewPwd);
			try {
				//loyaltyThresholdAlertsDao.saveOrUpdate(loyaltyThresholdAlerts);
				loyaltyThresholdAlertsDaoForDML.saveOrUpdate(loyaltyThresholdAlerts);
			} catch (Exception e) {
				logger.error("**Error while updating database**");
			}
			changePwdWinId$currPwdTbId.setValue("");
			changePwdWinId$newPwdTbId.setValue("");
			changePwdWinId$retypePwdTbId.setValue("");
			MessageUtil.setMessage("Your password has been updated successfully.", "color:blue","TOP");
			
			//send email to user with updated password
			Users user = GetUser.getUserObj();
			
			int serverTimeZoneValInt = Integer.parseInt(PropertyUtil.getPropertyValueFromDB(Constants.SERVER_TIMEZONE_VALUE));
			int timezoneDiffrenceMinutesInt = 0;
			String timezoneDiffrenceMinutes = user.getClientTimeZone();
			
			if(timezoneDiffrenceMinutes != null)  timezoneDiffrenceMinutesInt = Integer.parseInt(timezoneDiffrenceMinutes);
			timezoneDiffrenceMinutesInt = timezoneDiffrenceMinutesInt - serverTimeZoneValInt;
			
			Calendar time = Calendar.getInstance();
			time.add(Calendar.MINUTE, timezoneDiffrenceMinutesInt);
			
			String message = PropertyUtil.getPropertyValueFromDB("loyaltyProgramChangePwd");
			message = message.replace("[fname]", user.getFirstName() == null ? "" : user.getFirstName());
			message = message.replace("[time]",MyCalendar.calendarToString(time, MyCalendar.FORMAT_DATETIME_STYEAR));
			message = message.replace("[password]", EncryptDecryptLtyMembshpPwd.decryptProgramPwd(loyaltyThresholdAlerts.getLtySecurityPwd()));
			message = message.replace("[username]",  user.getUserName());
			message = message.replace("[orgid]", user.getUserOrganization().getOrgExternalId());

			String subject = "OptCulture - Your loyalty program's password has been updated successfully";

			EmailQueueDao emailQueueDao = (EmailQueueDao) ServiceLocator.getInstance().getDAOByName(OCConstants.EMAILQUEUE_DAO);
			EmailQueueDaoForDML emailQueueDaoForDML = (EmailQueueDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.EMAILQUEUE_DAO_ForDML);
			EmailQueue emailQueue = new EmailQueue(subject, message, Constants.EQ_TYPE_LOYALTY_EMAIL_ALERTS, Constants.EQ_STATUS_ACTIVE, GetUser.getUserObj().getEmailId(), MyCalendar.getNewCalendar(), user);
			//emailQueueDao.saveOrUpdate(emailQueue);
			emailQueueDaoForDML.saveOrUpdate(emailQueue);
			

			changePwdWinId.setVisible(false);	
		} catch (WrongValueException wve) {
			logger.error("** Exception : Wrong Value - "+wve+" **");
		}catch (Exception e) {
			logger.error("** Exception : Error while updating Password - "+ e +" **");
		}
		
	}
	
	public void onClick$forgotPwdAId() {
		try{

			LoyaltyThresholdAlertsDao loyaltyThresholdAlertsDao = (LoyaltyThresholdAlertsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_THRESHOLD_ALERTS_DAO);
			LoyaltyThresholdAlerts loyaltyThresholdAlerts = loyaltyThresholdAlertsDao.findByUserId(GetUser.getUserObj().getUserId());
			if(loyaltyThresholdAlerts != null && (loyaltyThresholdAlerts.getLtySecurityPwd() == null ||
					loyaltyThresholdAlerts.getLtySecurityPwd().isEmpty())){
				MessageUtil.setMessage("Password does not exist.", "color:red", "TOP");
				return;
			}else if(loyaltyThresholdAlerts == null){
				MessageUtil.setMessage("Password does not exist.", "color:red", "TOP");
				return;
			}
			else {
				Users user = GetUser.getUserObj();
				String message = PropertyUtil.getPropertyValueFromDB("loyaltyProgramForgotPwd");
				message = message.replace("[fname]", user.getFirstName() == null ? "" : user.getFirstName());
				message = message.replace("[password]", EncryptDecryptLtyMembshpPwd.decryptProgramPwd(loyaltyThresholdAlerts.getLtySecurityPwd()));
				message = message.replace("[username]",  user.getUserName());
				message = message.replace("[orgid]", user.getUserOrganization().getOrgExternalId());

				String subject = "OptCulture - Your loyalty program's password";

				EmailQueueDao emailQueueDao = (EmailQueueDao) ServiceLocator.getInstance().getDAOByName(OCConstants.EMAILQUEUE_DAO);
				EmailQueueDaoForDML emailQueueDaoForDML = (EmailQueueDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.EMAILQUEUE_DAO_ForDML);
				EmailQueue emailQueue = new EmailQueue(subject, message, Constants.EQ_TYPE_LOYALTY_EMAIL_ALERTS, Constants.EQ_STATUS_ACTIVE, GetUser.getUserObj().getEmailId(), MyCalendar.getNewCalendar(), user);

				//emailQueueDao.saveOrUpdate(emailQueue);
				emailQueueDaoForDML.saveOrUpdate(emailQueue);
				MessageUtil.setMessage("Password has been sent to your configured email address.", "color:blue", "TOP");
			}
		}catch(Exception e){
			logger.error("Exception in placing loyalty template email in email queue...", e);
		}
	}
				
	
	public void onClick$cancelBtnId$changePwdWinId() {
		changePwdWinId$currPwdTbId.setValue("");
		changePwdWinId$newPwdTbId.setValue("");
		changePwdWinId$retypePwdTbId.setValue("");
		changePwdWinId.setVisible(false);
	}
			
	public void onCheck$enableAlertsChkId(){
		try{

			if(enableAlertsChkId.isChecked()){
				alertsDivId.setVisible(true);

				LoyaltyThresholdAlertsDao loyaltyThresholdAlertsDao = (LoyaltyThresholdAlertsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_THRESHOLD_ALERTS_DAO);
				LoyaltyThresholdAlerts alertsObj = loyaltyThresholdAlertsDao.findByUserId(GetUser.getUserObj().getUserId());
				if(alertsObj != null && alertsObj.getEnableAlerts() == OCConstants.FLAG_YES){
					emailTbId.setValue(alertsObj.getAlertEmailId() != null ? alertsObj.getAlertEmailId() : "");
					mobileTbId.setValue(alertsObj.getAlertMobilePhn() != null ? alertsObj.getAlertMobilePhn() : "");
					if(OCConstants.LOYALTY_CARDS_AVAILABLE_COUNT_TYPE_PERCENTAGE.equalsIgnoreCase(alertsObj.getCountType())){
						percentRadioBtId.setChecked(true);
						valueRadioBtId.setChecked(false);
						percentTbId.setValue(alertsObj.getCountValue());
						valueTbId.setValue("");
						percentTbId.setDisabled(false);
						valueTbId.setDisabled(true);
					}
					else if(OCConstants.LOYALTY_CARDS_AVAILABLE_COUNT_TYPE_VALUE.equalsIgnoreCase(alertsObj.getCountType())){
						percentRadioBtId.setChecked(false);
						valueRadioBtId.setChecked(false);
						percentTbId.setValue("");
						valueTbId.setValue(alertsObj.getCountValue());
						percentTbId.setDisabled(true);
						valueTbId.setDisabled(false);
					}
				}
				else{
					Users user = GetUser.getUserObj();
					emailTbId.setValue(user.getEmailId() != null ? user.getEmailId() : "");
					mobileTbId.setValue(user.getPhone() != null ? user.getPhone() : "");
					percentRadioBtId.setChecked(true);
					valueRadioBtId.setChecked(false);
					percentTbId.setValue("");
					valueTbId.setValue("");
					percentTbId.setDisabled(false);
					valueTbId.setDisabled(true);
				}

			}
			else{
				alertsDivId.setVisible(false);
			}

		}catch(Exception e){
			logger.error("Exception:::",e);
		}
	}
	
	public void onClick$percentRadioBtId(){
		try{
			percentTbId.setDisabled(false);
			valueTbId.setDisabled(true);
			
			LoyaltyThresholdAlertsDao loyaltyThresholdAlertsDao = (LoyaltyThresholdAlertsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_THRESHOLD_ALERTS_DAO);
			LoyaltyThresholdAlerts alertsObj = loyaltyThresholdAlertsDao.findByUserId(GetUser.getUserObj().getUserId());
			if(alertsObj != null && alertsObj.getEnableAlerts() == OCConstants.FLAG_YES &&
					OCConstants.LOYALTY_CARDS_AVAILABLE_COUNT_TYPE_PERCENTAGE.equalsIgnoreCase(alertsObj.getCountType())){
				percentTbId.setValue(alertsObj.getCountValue());
				valueTbId.setValue("");
			}
			else{
				percentTbId.setValue("");
				valueTbId.setValue("");
			}
		}catch(Exception e){
			logger.error("Exception:::",e);
		}
	}
	
	public void onClick$valueRadioBtId(){
		try{
			percentTbId.setDisabled(true);
			valueTbId.setDisabled(false);
			
			LoyaltyThresholdAlertsDao loyaltyThresholdAlertsDao = (LoyaltyThresholdAlertsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_THRESHOLD_ALERTS_DAO);
			LoyaltyThresholdAlerts alertsObj = loyaltyThresholdAlertsDao.findByUserId(GetUser.getUserObj().getUserId());
			if(alertsObj != null && alertsObj.getEnableAlerts() == OCConstants.FLAG_YES &&
					OCConstants.LOYALTY_CARDS_AVAILABLE_COUNT_TYPE_VALUE.equalsIgnoreCase(alertsObj.getCountType())){
				percentTbId.setValue("");
				valueTbId.setValue(alertsObj.getCountValue());
			}
			else{
				percentTbId.setValue("");
				valueTbId.setValue("");
			}
		}catch(Exception e){
			logger.error("Exception:::",e);
		}
	}
	
	public void onClick$saveAlertsBtnId() {
		try{
			Users user = GetUser.getUserObj();
			
			LoyaltyThresholdAlertsDao loyaltyThresholdAlertsDao = (LoyaltyThresholdAlertsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_THRESHOLD_ALERTS_DAO);
			LoyaltyThresholdAlertsDaoForDML loyaltyThresholdAlertsDaoForDML = (LoyaltyThresholdAlertsDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_THRESHOLD_ALERTS_DAO_FOR_DML);

			LoyaltyThresholdAlerts alertsObj = loyaltyThresholdAlertsDao.findByUserId(user.getUserId());
			UserEmailAlertDao userEmailAlertDao = (UserEmailAlertDao) ServiceLocator.getInstance().getDAOByName(OCConstants.USER_EMAIL_ALERT_DAO);
			UserEmailAlertDaoForDML userEmailAlertDaoForDML = (UserEmailAlertDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.USER_EMAIL_ALERT_DAO_FOR_DML);
			//List<UserEmailAlert> userEmailAlertObjList = userEmailAlertDao.findListByUserId(user.getUserId());
			
			UserEmailAlert userEmailAlertDaily = (UserEmailAlert) ltyReportAlertsDailyChkId.getAttribute(ATTR_DAILY);
			UserEmailAlert userEmailAlertWeekly = (UserEmailAlert) ltyReportAlertsWeeklyChkId.getAttribute(ATTR_WEEKLY);
	//		UserEmailAlert userEmailAlertMonthly = (UserEmailAlert) ltyReportAlertsMonthlyChkId.getAttribute("monthly");
			
			String emailIdStr = null;
			long numberVal = 0l;
			 if( ltyReportAlertsDailyChkId.isChecked()) {
					
					logger.info("--------Entered ltyReportAlertsDailyChkId.isChecked------");
					
					if(userEmailAlertDaily == null) {
						userEmailAlertDaily = new UserEmailAlert();
						userEmailAlertDaily.setCreatedDate(Calendar.getInstance());
						userEmailAlertDaily.setCreatedBy(user.getUserId());
						userEmailAlertDaily.setUserId(user.getUserId());

						userEmailAlertDaily.setFrequency(OCConstants.LTY_SETTING_REPORT_FRQ_DAY);
						userEmailAlertDaily.setType(OCConstants.LTY_SETTING_REPORT_TYPE);
						
					}
					
					userEmailAlertDaily.setModifiedDate(Calendar.getInstance());
					userEmailAlertDaily.setModifiedBy(user.getUserId());
					emailIdStr = prepareEmailIdStr();
					if(emailIdStr==null){
						MessageUtil.setMessage("Please enter an email address.","color:red","TOP");
						return;
					}
					userEmailAlertDaily.setEmailId(emailIdStr);
					numberVal = Long.parseLong((String)hoursLbId.getSelectedItem().getValue());
					logger.info("---- For dailyListBox time is---"+numberVal);
					userEmailAlertDaily.setTriggerAt(numberVal+Constants.STRING_NILL);
					//userEmailAlertDaoForDML.saveOrUpdate(userEmailAlertDaily);
			}
			if(userEmailAlertDaily != null){
				userEmailAlertDaily.setEnabled(ltyReportAlertsDailyChkId.isChecked());
				//userEmailAlertDao.saveOrUpdate(userEmailAlertDaily);
				userEmailAlertDaoForDML.saveOrUpdate(userEmailAlertDaily);
				
			}
			/*else if(userEmailAlertDaily != null && !ltyReportAlertsDailyChkId.isChecked()){
				userEmailAlertDaily.setEnabled(false);
				//userEmailAlertDao.saveOrUpdate(userEmailAlertDaily);
				userEmailAlertDaoForDML.saveOrUpdate(userEmailAlertDaily);
			}
			
			if(userEmailAlertWeekly == null && ltyReportAlertsWeeklyChkId.isChecked()){
				UserEmailAlert userEmailAlertObj = new UserEmailAlert();
				 
				userEmailAlertObj.setCreatedDate(Calendar.getInstance());
				userEmailAlertObj.setCreatedBy(user.getUserId());
				userEmailAlertObj.setFrequency(OCConstants.LTY_SETTING_REPORT_FRQ_WEEK);
				userEmailAlertObj.setType(OCConstants.LTY_SETTING_REPORT_TYPE);
				emailIdStr = prepareEmailIdStr();
				userEmailAlertObj.setEmailId(emailIdStr);*/
				
			if( ltyReportAlertsWeeklyChkId.isChecked()) {
				
				logger.info("--------Entered ltyReportAlertsWeeklyChkId.isChecked------");
				
				if(userEmailAlertWeekly == null) {
					userEmailAlertWeekly = new UserEmailAlert();
					userEmailAlertWeekly.setCreatedDate(Calendar.getInstance());
					userEmailAlertWeekly.setCreatedBy(user.getUserId());
					userEmailAlertWeekly.setUserId(user.getUserId());

					userEmailAlertWeekly.setFrequency(OCConstants.LTY_SETTING_REPORT_FRQ_WEEK);
					userEmailAlertWeekly.setType(OCConstants.LTY_SETTING_REPORT_TYPE);
					
				}
				
				userEmailAlertWeekly.setModifiedDate(Calendar.getInstance());
				userEmailAlertWeekly.setModifiedBy(user.getUserId());
				emailIdStr = prepareEmailIdStr();
				if(emailIdStr==null){
					MessageUtil.setMessage("Please enter an email address.","color:red","TOP");
					return;
				}
				userEmailAlertWeekly.setEmailId(emailIdStr);
				numberVal = Long.parseLong((String)hoursLbId.getSelectedItem().getValue());
				String str = daysLbId.getSelectedItem().getValue();
				logger.info("---- For weeklyListBox time is---"+numberVal+"and"+str);
				userEmailAlertWeekly.setTriggerAt(numberVal+Constants.ADDR_COL_DELIMETER+str);
				userEmailAlertWeekly.setUserId(user.getUserId());
				//userEmailAlertDaoForDML.saveOrUpdate(userEmailAlertWeekly);
			}
			if(userEmailAlertWeekly != null) {
				
				userEmailAlertWeekly.setEnabled( ltyReportAlertsWeeklyChkId.isChecked());
				//userEmailAlertDao.saveOrUpdate(userEmailAlertObj);
				userEmailAlertDaoForDML.saveOrUpdate(userEmailAlertWeekly);
			}
			
			/*if(userEmailAlertMonthly == null && ltyReportAlertsMonthlyChkId.isChecked()){
				UserEmailAlert userEmailAlertObj = new UserEmailAlert();
				 
				userEmailAlertObj.setCreatedDate(Calendar.getInstance());
				userEmailAlertObj.setCreatedBy(user.getUserId());
				userEmailAlertObj.setFrequency(OCConstants.LTY_SETTING_REPORT_FRQ_MONTH);
				userEmailAlertObj.setType(OCConstants.LTY_SETTING_REPORT_TYPE);
				emailIdStr = prepareEmailIdStr();
				userEmailAlertObj.setEmailId(emailIdStr);
				
				numberVal = Long.parseLong((String)hoursLbId.getSelectedItem().getValue());
				String str = whichDayLbId.getSelectedItem().getValue();
				userEmailAlertObj.setTriggerAt(numberVal+Constants.ADDR_COL_DELIMETER+str);
				userEmailAlertObj.setUserId(user.getUserId());
				userEmailAlertObj.setEnabled(true);
				userEmailAlertDao.saveOrUpdate(userEmailAlertObj);
				
			}
			else if(userEmailAlertMonthly != null && ltyReportAlertsMonthlyChkId.isChecked()){
				userEmailAlertMonthly.setModifiedDate(Calendar.getInstance());
				userEmailAlertMonthly.setModifiedBy(user.getUserId());
				emailIdStr = prepareEmailIdStr();
				userEmailAlertMonthly.setEmailId(emailIdStr);
				numberVal = Long.parseLong((String)hoursLbId.getSelectedItem().getValue());
				String str = whichDayLbId.getSelectedItem().getValue();
				logger.info("---- For monthlyListBox time is---"+numberVal+"and"+str);
				userEmailAlertMonthly.setTriggerAt(numberVal+Constants.ADDR_COL_DELIMETER+str);
				userEmailAlertMonthly.setEnabled(true);
				userEmailAlertDao.saveOrUpdate(userEmailAlertMonthly);
				
			}
			else if(userEmailAlertMonthly != null && !ltyReportAlertsMonthlyChkId.isChecked()){
				userEmailAlertMonthly.setEnabled(false);
				userEmailAlertDao.saveOrUpdate(userEmailAlertMonthly);
			}*/
			
			
			if(alertsObj == null && enableAlertsChkId.isChecked()){
				if(!validAlertFields()) return;
				
				alertsObj = new LoyaltyThresholdAlerts();
				
				alertsObj.setUserId(user.getUserId());
				alertsObj.setOrgId(user.getUserOrganization().getUserOrgId());
				
				alertsObj.setEnableAlerts(OCConstants.FLAG_YES);
				alertsObj.setAlertEmailId(emailTbId.getValue());
				alertsObj.setAlertMobilePhn(mobileTbId.getValue());
				if(percentRadioBtId.isChecked()){
					alertsObj.setCountType(OCConstants.LOYALTY_CARDS_AVAILABLE_COUNT_TYPE_PERCENTAGE);
					alertsObj.setCountValue(percentTbId.getValue());
				}
				else if(valueRadioBtId.isChecked()){
					alertsObj.setCountType(OCConstants.LOYALTY_CARDS_AVAILABLE_COUNT_TYPE_VALUE);
					alertsObj.setCountValue(valueTbId.getValue());
				}
				
				//loyaltyThresholdAlertsDao.saveOrUpdate(alertsObj);
				loyaltyThresholdAlertsDaoForDML.saveOrUpdate(alertsObj);
			}
			else if(alertsObj != null && enableAlertsChkId.isChecked()){
				
				if(!validAlertFields()) return;
				alertsObj.setEnableAlerts(OCConstants.FLAG_YES);
				alertsObj.setAlertEmailId(emailTbId.getValue());
				alertsObj.setAlertMobilePhn(mobileTbId.getValue());
				if(percentRadioBtId.isChecked()){
					alertsObj.setCountType(OCConstants.LOYALTY_CARDS_AVAILABLE_COUNT_TYPE_PERCENTAGE);
					alertsObj.setCountValue(percentTbId.getValue());
				}
				else if(valueRadioBtId.isChecked()){
					alertsObj.setCountType(OCConstants.LOYALTY_CARDS_AVAILABLE_COUNT_TYPE_VALUE);
					alertsObj.setCountValue(valueTbId.getValue());
				}
				
				//loyaltyThresholdAlertsDao.saveOrUpdate(alertsObj);
				loyaltyThresholdAlertsDaoForDML.saveOrUpdate(alertsObj);
			}
			else if (alertsObj != null && !enableAlertsChkId.isChecked()) {
				alertsObj.setEnableAlerts(OCConstants.FLAG_NO);
				alertsObj.setAlertEmailId("");
				alertsObj.setAlertMobilePhn("");
				alertsObj.setCountType(null);
				alertsObj.setCountValue(null);
				
				//loyaltyThresholdAlertsDao.saveOrUpdate(alertsObj);
				loyaltyThresholdAlertsDaoForDML.saveOrUpdate(alertsObj);
			}
			else if(alertsObj == null && !enableAlertsChkId.isChecked()) return;
			MessageUtil.setMessage("Alert settings saved successfully.", "color:blue", "TOP");
			Redirect.goTo(PageListEnum.EMPTY);
			Redirect.goTo(PageListEnum.ADMIN_LOYALTY_SETTINGS);
			
		}catch(Exception e){
			logger.error("Exception :::",e);
		}
	}

	private boolean validAlertFields() {

		if(emailTbId.getValue().trim().length() == 0 && mobileTbId.getValue().trim().length() == 0) {
			MessageUtil.setMessage("Please enter either email or mobile to send alerts.", "red");
			return false;
		}
		
		if(emailTbId.getValue().trim().length() > 0){
			if(!Utility.validateEmail(emailTbId.getValue().trim())){
				MessageUtil.setMessage("Please enter valid email address.", "red");
				return false;
			}
		}
		
		if(mobileTbId.getValue().trim().length() > 0){
			if((Utility.phoneParse(mobileTbId.getValue().trim(),GetUser.getUserObj().getUserOrganization())==null)){
			//if(!Utility.validateUserPhoneNum(mobileTbId.getValue().trim())){
				MessageUtil.setMessage("Please enter valid phone number.", "red");
				return false;
			}
		}
		
		if(!percentRadioBtId.isChecked() && !valueRadioBtId.isChecked()) {
			MessageUtil.setMessage("Please check the alert on radio button.", "red");
			return false;
		}


		if(percentRadioBtId.isChecked()) {
			if(percentTbId.getValue().trim().length() > 0) {
				if(percentTbId.getValue().trim().startsWith("-") || percentTbId.getValue().trim().startsWith("+") || !checkIfNumber(percentTbId.getValue().trim())) {
					MessageUtil.setMessage("Please provide valid number value for % of total cards.", "red");
					return false;
				}

			}
			else if(percentTbId.getValue().trim().length() == 0) {
				MessageUtil.setMessage("Please provide the value for % of total cards.", "red");
				return false;
			}
		}

		else if(valueRadioBtId.isChecked()) {
			if(valueTbId.getValue().trim().length() > 0) {
				if(valueTbId.getValue().trim().startsWith("-") || valueTbId.getValue().trim().startsWith("+") || !checkIfNumber(valueTbId.getValue().trim())) {
					MessageUtil.setMessage("Please provide valid number value for no of cards.", "red");
					return false;
				}
			}

			else if(valueTbId.getValue().trim().length() == 0) {
				MessageUtil.setMessage("Please provide the  value for no of cards.", "red");
				return false;
			}
		}
		return true;
	}
	
	public boolean checkIfNumber(String in) {
        try {
            Long.parseLong(in);
        } catch (NumberFormatException ex) {
            return false;
        }
        return true;
    }// checkIfNumber
	
	public void onClick$addMoreEmailTBId(){
		
		Div alertDiv = new Div();
		Textbox alertTextBx = new Textbox();
		alertTextBx.setParent(alertDiv);
		alertTextBx.setWidth("250px");
		alertTextBx.setStyle("margin-left:85px;margin-top: 10px;margin-right:7px ;");
		
		Image delImg = new Image();
		delImg.setAttribute("TYPE", "ALERT_DEL");
		delImg.setSrc("/images/action_delete.gif");
		delImg.setStyle("cursor:pointer;color:#2886B9;font-weight:bold;text-decoration: underline;");
		delImg.setTooltiptext("Delete");
		delImg.addEventListener("onClick", this);
		delImg.setParent(alertDiv);
		
		alertDiv.setParent(alertMailDivId);
	}
	
	@Override
	public void onEvent(Event event) throws Exception {
		
		// TODO Auto-generated method stub
			super.onEvent(event);
			
			
			
			if (event.getTarget() instanceof Paging) {
				Paging paging = (Paging) event.getTarget();
				int desiredPage = paging.getActivePage();
				PagingEvent pagingEvent = (PagingEvent) event;
				int pSize = pagingEvent.getPageable().getPageSize();
				int ofs = desiredPage * pSize;
				if (paging.getId().equals("valueCodePagingId")) {
				ValueCodesGrid(key,ofs,pSize,desiredPage);
				}
			}
			
			
			if(event.getTarget() instanceof Image) {
				Image img =(Image)event.getTarget();
				Row temRow = null;
				String imgAction = (String)img.getAttribute("TYPE");
				String imgEdit =(String)img.getAttribute("Edit");
				if("ALERT_DEL".equals(imgAction)){
					Div tempDiv= (Div)img.getParent();
					logger.info("ALERT@@");
					alertMailDivId.removeChild(tempDiv);
				}
				if("edit".equals(imgEdit)){
					Listitem lcImg=(Listitem) img.getParent().getParent().getParent();
					ValueCodes valcode=lcImg.getValue();
					valueCodeNameTbId.setValue(valcode.getValuCode());
					valueCodeNameTbId.setDisabled(true);
					descriptionTbId.setValue(valcode.getDescription());
					saveValueCodesBtnId.setLabel("Update");
					saveValueCodesBtnId.setAttribute("ValueCodeObj", valcode);
					//ImageEdit(lcImg.getValue());
				}
				
			}
					
	}
	
	/*private void displayDivComponents() {
		ltyRprtDivId.setVisible(true);
		if(!ltyReportAlertsDailyChkId.isChecked() && !ltyReportAlertsWeeklyChkId.isChecked()){
			ltyRprtDivId.setVisible(false);
		}
		else if(ltyReportAlertsDailyChkId.isChecked() && ltyReportAlertsWeeklyChkId.isChecked()){
			dailyDivId.setVisible(true);
			weeklyDivId.setVisible(true);
		}
		else if(ltyReportAlertsDailyChkId.isChecked()){
			dailyDivId.setVisible(true);
			weeklyDivId.setVisible(false);
		}
		else if(ltyReportAlertsWeeklyChkId.isChecked()){
			dailyDivId.setVisible(true);
			weeklyDivId.setVisible(true);
		}
		else if(!ltyReportAlertsDailyChkId.isChecked() && !ltyReportAlertsWeeklyChkId.isChecked()){
			ltyRprtDivId.setVisible(false);
		}	
	}*/
	
	public void onCheck$ltyReportAlertsDailyChkId(){
		ltyRprtDivId.setVisible(ltyReportAlertsDailyChkId.isChecked() || ltyReportAlertsWeeklyChkId.isChecked());
		dailyDivId.setVisible(ltyReportAlertsDailyChkId.isChecked() || ltyReportAlertsWeeklyChkId.isChecked());
		weeklyDivId.setVisible(ltyReportAlertsWeeklyChkId.isChecked());
	}
	/*private void displayDivComponents() {
		
		ltyRprtDivId.setVisible(true);
		if(!ltyReportAlertsDailyChkId.isChecked() && !ltyReportAlertsWeeklyChkId.isChecked() && !ltyReportAlertsMonthlyChkId.isChecked()){
			ltyRprtDivId.setVisible(false);
		}
		else if(ltyReportAlertsDailyChkId.isChecked() && ltyReportAlertsWeeklyChkId.isChecked() && ltyReportAlertsMonthlyChkId.isChecked()){
			dailyDivId.setVisible(true);
			weeklyDivId.setVisible(true);
			monthlyDivId.setVisible(true);
			
		}
		else if(ltyReportAlertsDailyChkId.isChecked() && ltyReportAlertsWeeklyChkId.isChecked()){
			dailyDivId.setVisible(true);
			weeklyDivId.setVisible(true);
			monthlyDivId.setVisible(false);
		}
		else if(ltyReportAlertsWeeklyChkId.isChecked() && ltyReportAlertsMonthlyChkId.isChecked()){
			dailyDivId.setVisible(true);
			weeklyDivId.setVisible(true);
			monthlyDivId.setVisible(true);
		}
		else if(ltyReportAlertsDailyChkId.isChecked() && ltyReportAlertsMonthlyChkId.isChecked()){
			dailyDivId.setVisible(true);
			weeklyDivId.setVisible(false);
			monthlyDivId.setVisible(true);
		}
		else if(ltyReportAlertsDailyChkId.isChecked()){
			dailyDivId.setVisible(true);
			weeklyDivId.setVisible(false);
			monthlyDivId.setVisible(false);
		}
		else if(ltyReportAlertsWeeklyChkId.isChecked()){
			dailyDivId.setVisible(true);
			weeklyDivId.setVisible(true);
			monthlyDivId.setVisible(false);
		}
		else if(ltyReportAlertsMonthlyChkId.isChecked()){
			dailyDivId.setVisible(true);
			weeklyDivId.setVisible(false);
			monthlyDivId.setVisible(true);
		}
		else if(!ltyReportAlertsDailyChkId.isChecked() && !ltyReportAlertsWeeklyChkId.isChecked() && !ltyReportAlertsMonthlyChkId.isChecked()){
			ltyRprtDivId.setVisible(false);
		}	
		
	}*/
	
	public void onCheck$ltyReportAlertsWeeklyChkId(){
		ltyRprtDivId.setVisible(ltyReportAlertsDailyChkId.isChecked() || ltyReportAlertsWeeklyChkId.isChecked());
		weeklyDivId.setVisible(ltyReportAlertsWeeklyChkId.isChecked());	
		dailyDivId.setVisible(ltyReportAlertsDailyChkId.isChecked() || ltyReportAlertsWeeklyChkId.isChecked());
	}
	
	/*public void onCheck$ltyReportAlertsMonthlyChkId(){
		displayDivComponents();
	}*/
	
	private String prepareEmailIdStr() {
		String emailIdStr = null;
		List<Component> tempDivList = alertMailDivId.getChildren();
		if(emailAddTxtBxId.getValue() != null && !emailAddTxtBxId.getValue().trim().isEmpty()){
			if(!Utility.validateEmail(emailAddTxtBxId.getValue().trim())){
				MessageUtil.setMessage("Please enter valid email address.","color:red","TOP");
				return emailIdStr;
			}
			emailIdStr = emailAddTxtBxId.getValue().trim();
		}
		if(tempDivList != null && tempDivList.size() > 0){
			for (Component tempDiv : tempDivList) {
				Textbox tempTb = (Textbox) tempDiv.getFirstChild();
				
				if(tempTb.getValue() != null && !tempTb.getValue().trim().isEmpty()){
					if(!Utility.validateEmail(tempTb.getValue().trim())){
						MessageUtil.setMessage("Please enter valid email address.","color:red","TOP");
						return emailIdStr;
					}
					if(emailIdStr == null || emailIdStr.isEmpty()){
						emailIdStr = tempTb.getValue().trim();
					}
					else{
						emailIdStr += Constants.ADDR_COL_DELIMETER + tempTb.getValue().trim();
					}
				}
			}
		}

		return emailIdStr;
	}
	
	private void ValueCodesGrid(String SerachKey,int startIndex, int endCount,int pageSize) throws Exception {
        for(int cnt=valueCodeLbId.getItemCount(); cnt>0;cnt--){//For Removing children component UI.
        	valueCodeLbId.removeItemAt(cnt-1);
        }
		UserOrganization OrgID=GetUser.getUserObj().getUserOrganization();
		ValueCodesDao valueCodesDao = (ValueCodesDao) ServiceLocator.getInstance().getDAOByName(OCConstants.VALUE_CODES_DAO);
		List<ValueCodes> ValueCodeList =null;
		
		
		
		SerachKey =SerachKey == null ? Constants.STRING_NILL : SerachKey ;
		Long totRecords=valueCodesDao.findCountByValueCodeByOrgIdAndKey(OrgID.getUserOrgId(),SerachKey);
	try {	
		//Pagination related code for listbox
		valueCodePagingId.setPageSize(endCount);
		valueCodePagingId.setTotalSize(totRecords.intValue());		
		valueCodePagingId.setActivePage(pageSize);
	}
	catch(Exception e) {
		logger.info("e===>"+e);
	}
		
		ValueCodeList = valueCodesDao.findByValueCodeByOrgIdandSearchKey(OrgID.getUserOrgId(),SerachKey,startIndex,endCount);

		for (ValueCodes valCodes : ValueCodeList) {

			Listcell lc;
			Listitem li;
			li = new Listitem();

			lc = new Listcell(valCodes.getValuCode());
			lc.setParent(li);

			lc = new Listcell(valCodes.getDescription());
			lc.setParent(li);

			Hbox hbox = new Hbox();
			
			Image editImg = new Image("/img/email_edit.gif");
			editImg.setTooltiptext("Edit Value-Code");
			editImg.setStyle("cursor:pointer;margin-right:5px;margin-left: 50px;");
			editImg.addEventListener("onClick", this);
			editImg.setAttribute("Edit", "edit");
			editImg.setParent(hbox);
			
			lc = new Listcell();
			hbox.setParent(lc);

			lc.setParent(li);
			li.setParent(valueCodeLbId);
			li.setValue(valCodes);

		}

	
	}
	
	public void onClick$saveValueCodesBtnId(){
	try {
		addValueCodeData();
	} catch (Exception e) {
		// TODO Auto-generated catch block
		logger.info("onClick$saveValueCodesBtnId error===>"+e);
	}	
	}
	
	private void addValueCodeData() throws Exception {
		String valueCode = valueCodeNameTbId.getValue().trim();
		if (valueCode == null || valueCode.trim().length() == 0) {
			MessageUtil.setMessage("Please provide Value-Code Name. Name cannot be left empty.", "color:red", "TOP");
			valueCodeNameTbId.setFocus(true);
			return;
		}
		if (valueCode.length() > 0 && !Utility.validateName(valueCode)) {
			MessageUtil.setMessage("Special characters not allowed in Value-Code Name.", "color:red", "TOP");
			return;
		}
		UserOrganization OrgID=GetUser.getUserObj().getUserOrganization();
		ValueCodesDao valueCodesDao = (ValueCodesDao) ServiceLocator.getInstance().getDAOByName(OCConstants.VALUE_CODES_DAO);
		ValueCodesDaoForDML valueCodesDaoForDML = (ValueCodesDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.VALUE_CODES_DAO_FOR_DML);
		ValueCodes vcode=null;
		vcode=(ValueCodes) saveValueCodesBtnId.getAttribute("ValueCodeObj");
		if(vcode==null){
			List<ValueCodes> ValueCodeList =null;
			ValueCodeList = valueCodesDao.findValueCode(OrgID.getUserOrgId(),valueCode);
			if(ValueCodeList!=null && ValueCodeList.size()>0){
				MessageUtil.setMessage("Value-Code already exist. Please provide another Value-Code.", "color:red", "TOP");
				return;
			}
			vcode=new ValueCodes();
			vcode.setCreatedBy(GetUser.getUserObj().getUserId().toString());
			vcode.setCreatedDate(MyCalendar.getInstance());
			vcode.setOrgId(GetUser.getUserObj().getUserOrganization().getUserOrgId());
			vcode.setValuCode(valueCodeNameTbId.getValue());
		}
		vcode.setDescription(descriptionTbId.getValue());
		vcode.setModifiedBy(GetUser.getUserObj().getUserId().toString());
		vcode.setModifiedDate(MyCalendar.getInstance());
		valueCodesDaoForDML.saveOrUpdate(vcode);
		int tempCount = Integer.parseInt(valueCodePerPageLBId
				.getSelectedItem().getLabel());
		ValueCodesGrid(null,0,tempCount,0);
		valueCodeNameTbId.setValue("");
		valueCodeNameTbId.setDisabled(false);
		descriptionTbId.setValue("");
		saveValueCodesBtnId.setLabel("Save");
		saveValueCodesBtnId.removeAttribute("ValueCodeObj");
	}
	
	private void ImageEdit(ValueCodes valcode) {
		
		
		ValueCodeEditWinId.setVisible(true);
		ValueCodeEditWinId.setPosition("center");
		ValueCodeEditWinId.doHighlighted();
		ValueCodeId=valcode.getId();
		ValueCodeEditWinId$valueCodeNameTbId.setValue(valcode.getValuCode());
		ValueCodeEditWinId$descriptionTbId.setValue(valcode.getDescription());
		ValueCodeEditWinId$valueCodeNameTbId.setDisabled(true);
		
		
	}
	
	public void onClick$updateValueCodesBtnId$ValueCodeEditWinId() throws Exception{
		ValueCodesDaoForDML valueCodesDaoForDML = (ValueCodesDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.VALUE_CODES_DAO_FOR_DML);
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String ModifiedDate = format.format(cal.getTime());
		valueCodesDaoForDML.updateValueCodeById(ValueCodeId,ValueCodeEditWinId$descriptionTbId.getValue(),ModifiedDate,GetUser.getUserId());
		MessageUtil.setMessage("Description saved successfully.", "color:blue", "TOP");
		ValueCodeEditWinId.setVisible(false);
		int tempCount = Integer.parseInt(valueCodePerPageLBId
				.getSelectedItem().getLabel());
		ValueCodesGrid(null,0,tempCount,0);
	}
	
	public void onChanging$searchBoxId(InputEvent event) {
		key = event.getValue();
		try {
			int tempCount = Integer.parseInt(valueCodePerPageLBId
					.getSelectedItem().getLabel());
			ValueCodesGrid(key,0,tempCount,0);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.info("e===>"+e);
		}
	}
	
	public void onSelect$valueCodePerPageLBId() {

		try {

			int tempCount = Integer.parseInt(valueCodePerPageLBId
					.getSelectedItem().getLabel());
			ValueCodesGrid(key,0,tempCount,0);

		} catch (WrongValueException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
		} catch (Exception e) {
			logger.error("Exception ::", e);
		}

	}// onSelect$pageSizeLbId()

	private boolean isAnySpecialChar(Media media){
		//logger.debug("isAnySpecialChar ===============================6");
		String imagName = media.getName();
		logger.info("+++++++++++++++++"+imagName);
		try {
			if(!Utility.validateUploadFilName(imagName) || imagName.contains("'")) {
				
					return true;
			}
			else return false;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
			return false;
			
		}
			
		
	} //isAnySpecialChar()
}
