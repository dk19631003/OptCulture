package org.mq.marketer.campaign.controller.campaign;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.apache.commons.collections.functors.IfClosure;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.DigitalReceiptMyTemplate;
import org.mq.marketer.campaign.beans.DigitalReceiptUserSettings;
import org.mq.marketer.campaign.beans.MyFolders;
import org.mq.marketer.campaign.beans.OrganizationStores;
import org.mq.marketer.campaign.beans.OrganizationZone;
import org.mq.marketer.campaign.beans.SecRoles;
import org.mq.marketer.campaign.beans.UserFromEmailId;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.beans.UsersDomains;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.custom.MyCalendar;

import org.mq.marketer.campaign.dao.CampaignsDao;
import org.mq.marketer.campaign.dao.DRSentDao;
import org.mq.marketer.campaign.dao.DRSentDaoForDML;
import org.mq.marketer.campaign.dao.DigitalReceiptMyTemplatesDao;
import org.mq.marketer.campaign.dao.DigitalReceiptMyTemplatesDaoForDML;
import org.mq.marketer.campaign.dao.DigitalReceiptUserSettingsDao;
import org.mq.marketer.campaign.dao.DigitalReceiptUserSettingsDaoForDML;
import org.mq.marketer.campaign.dao.EmailQueueDao;
import org.mq.marketer.campaign.dao.MailingListDao;
import org.mq.marketer.campaign.dao.MyFoldersDao;
import org.mq.marketer.campaign.dao.MyFoldersDaoForDML;
import org.mq.marketer.campaign.dao.OrganizationStoresDao;
import org.mq.marketer.campaign.dao.SecRolesDao;
import org.mq.marketer.campaign.dao.UserFromEmailIdDao;
import org.mq.marketer.campaign.dao.UserFromEmailIdDaoForDML;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.dao.UsersDaoForDML;
import org.mq.marketer.campaign.dao.UsersDomainsDao;
import org.mq.marketer.campaign.dao.ZoneDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PageListEnum;
import org.mq.marketer.campaign.general.PageUtil;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Redirect;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.zkforge.ckez.CKeditor;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Html;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Menupopup;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Popup;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Space;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Window;
import org.zkoss.zul.event.PagingEvent;

//TODO 18_03_201public class ERecieptsSettingsController extends GenericForwardComposer implements EventListener, ListitemRenderer {

public class ERecieptsSettingsController extends GenericForwardComposer implements EventListener {

	Listbox digitalTemplatesListId,puserListId,selDateformatLbId;
	String digitalRecieptTemplatesPath;
	CKeditor digitalRecieptsCkEditorId;
	DigitalReceiptMyTemplatesDao digiRecptMyTemplatesDao;
	DigitalReceiptMyTemplatesDaoForDML digiRecptMyTemplatesDaoForDML;
	MailingListDao mailingListDao;
	//Window winId, testDRWinId, makeACopyWinId, viewDetailsWinId, renameTemplateWinId;
	Textbox winId$templatNameTbId, testDRWinId$testDRTbId;
	Tab myTemplatesTabId, digiRcptReportsTabId;
	Listbox digiRecptReportListId, myTemplatesListId,dispSBSLBoxId, dispStoreLBoxId, mytemplatesLbId, tempsettingsLbId,dispZoneLBoxId;
	Label selectedTemplateLblId, testDRWinId$msgLblId, viewDetailsWinId$templateNameLblId,
	viewDetailsWinId$creationDateLblId, viewDetailsWinId$lastAccLblId,regFrmEmlAnchId,regRepEmlAnchId,regReplyEmlAnchId,fromEmailerrorLBId,ToEmailerrorLBId ;
	// Listbox digiRcptSettingLstId1;
	Grid templateAndJSONMappingGridId;
	Button addFieldBtnId, tmepsaveBtnId,enableDRSendingsaveBtnId;
	Textbox jsonKeyFieldTbxId;
	// Radiogroup templateTypeGrpId;
	Tabbox tabBoxId;
	Tree orgtreeId,foldertreeId;
	Toolbarbutton sendTestBtnId;

	final String  STORE_WISE_DR_SETTINGS_OBJ="DRObj";

	MyFoldersDao myFolderDao;
	MyFoldersDaoForDML myFolderDaoForDML;

	Image manageActionsImg = new Image("/img/theme/arrow.png");

	private Popup regEmailPopupId, regRepEmailPopupId, regReplyToEmailPopupId;
	private Textbox cFromNameTb, cSubTb, cFromEmailTb,cRepEmailTb, cWebLinkTextTb,
	cPhoneTbId, cReplyEmailTb;
	private Combobox cFromEmailCb,cRepEmailCb, cReplyEmailCb;
	private Checkbox toNameChkId, cWebPageCb,  shippingAmntChkBxId, feeAmntChkBxId, taxAmntChkBxId, discAmntChkBxId, enableSendingChkBxId,enableSMSSendingChkBxId, totalAmntChkBxId;//,enableStoreChkBxId;
	private Div permRemDivId, enableStoreDivId,zoneWiseId,zoneSettingsId,enableSMSSendingDivId,enableSMSSendingCheckboxDivId,fromAndReplyDivId;
	private Div persToDivId, cWebLinkHboxId, DynamicId, Dynamic1Id,repRegdivId,froRegdivId;
	private Textbox permRemTextId;
	private Listbox phLbId, orgUnitlbId,dispFolderBoxId,searchByOrgID,settingsPerPageLBId;
	private Textbox cWebLinkUrlTextTb,makeACopyWinId$templatNameTbId,renameTemplateWinId$newTempltNameTbId;
	private Paging settingsPagingId;
	Radio sameNameRadioId,dynamicNameRadioId,sameFromEmailRadioId,dynamicFromEmailRadioId,sameReplyEmailRadioId,dynamicReplyEmailRadioId;


	// digiRcptSettingLstId2,digiRcptSettingLstId3,digiRcptSettingLstId4,digiRcptSettingLstId5,
	// digiRcptSettingLstId6;

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);


	private static final String SYSTEM_TEMPLATE = "SYSTEM:";
	private static final String MY_TEMPLATE = "MY_TEMPLATE:";
	private final String DEFAULT_TEMPLATE_NAME = "No template selected.";
	String[] templateNamesArr;
	private Users currentUser;
	String[] phArray = { "firstName", "lastName", "fullName" };
	private UserFromEmailIdDao userFromEmailIdDao;
	private UserFromEmailIdDaoForDML userFromEmailIdDaoForDML;
	//private DigitalReceiptUserSettings digitalReceiptUserSettings;
	private DigitalReceiptUserSettingsDao digitalReceiptUserSettingsDao;
	private DigitalReceiptUserSettingsDaoForDML digitalReceiptUserSettingsDaoForDML;
	private DRSentDao drSentDao;
	private DRSentDaoForDML drSentDaoForDML;
	private CampaignsDao campaignsDao;
	private EmailQueueDao emailQueueDao;
	private UsersDao usersDao;
	private UsersDaoForDML usersDaoForDML;
	private boolean isAdmin;
	private Session session;
	private Menupopup manageActionsMpId;
	private boolean onSave;
	private OrganizationStoresDao organizationDao;
	TimeZone clientTimeZone ;
	private Groupbox enableStoreWiseGBID;
	private UsersDomainsDao domainDao;
	private ZoneDao zoneDao;


	private OrganizationStoresDao organizationStoresDao;
	private List<UserFromEmailId> userFromEmailIdList = null;


	private UsersDomainsDao userDomainDao;
	
	private Button cancelBtnId, submitBt1nId, cancelReplyBtnId , submitReplyBtnId;


	private Radiogroup frmNamedynamicOrNotRgId, frmEmaildynamicOrNotRgId,cPermRemRb, replyEmaildynamicOrNotRgId;

	private boolean isSuperUser;

	public ERecieptsSettingsController() {
		// TODO Auto-generated constructor stub
		session = Sessions.getCurrent();
		currentUser = GetUser.getUserObj();
		this.campaignsDao = (CampaignsDao) SpringUtil.getBean("campaignsDao");
		emailQueueDao = (EmailQueueDao) SpringUtil.getBean("emailQueueDao");
		usersDao = (UsersDao) SpringUtil.getBean("usersDao");
		drSentDao = (DRSentDao) SpringUtil.getBean("drSentDao");
		drSentDaoForDML = (DRSentDaoForDML) SpringUtil.getBean("drSentDaoForDML");
		PageUtil.setHeader("e-Receipt Settings", "", "", true);
		digiRecptMyTemplatesDao = (DigitalReceiptMyTemplatesDao) SpringUtil.getBean("digitalReceiptMyTemplatesDao");
		digiRecptMyTemplatesDaoForDML = (DigitalReceiptMyTemplatesDaoForDML) SpringUtil.getBean("digitalReceiptMyTemplatesDaoForDML");
		mailingListDao = (MailingListDao) SpringUtil.getBean("mailingListDao");
		digitalReceiptUserSettingsDao= (DigitalReceiptUserSettingsDao) SpringUtil.getBean("digitalReceiptUserSettingsDao");
		digitalReceiptUserSettingsDaoForDML= (DigitalReceiptUserSettingsDaoForDML) SpringUtil.getBean("digitalReceiptUserSettingsDaoForDML");
		organizationDao=(OrganizationStoresDao) SpringUtil.getBean("organizationStoresDao");
		this.domainDao = (UsersDomainsDao) SpringUtil.getBean("usersDomainsDao");
		this.userFromEmailIdDao = (UserFromEmailIdDao) SpringUtil.getBean("userFromEmailIdDao");
		this.userFromEmailIdDaoForDML = (UserFromEmailIdDaoForDML) SpringUtil.getBean("userFromEmailIdDaoForDML");
		this.usersDaoForDML = (UsersDaoForDML)SpringUtil.getBean(OCConstants.USERS_DAOForDML);
		this.zoneDao=(ZoneDao) SpringUtil.getBean("zoneDao");
		myFolderDao = (MyFoldersDao) SpringUtil.getBean("myFoldersDao");
		clientTimeZone = (TimeZone)session.getAttribute("clientTimeZone");
		myFolderDaoForDML = (MyFoldersDaoForDML) SpringUtil.getBean("myFoldersDaoForDML");

		this.organizationStoresDao = (OrganizationStoresDao) SpringUtil.getBean("organizationStoresDao");

		this.userDomainDao = (UsersDomainsDao) SpringUtil.getBean("usersDomainsDao");

	}

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		try {
			super.doAfterCompose(comp);
			defaultDateFormat();	//app-3706
			defaultFromAndReplySettings();//app-3766
			if(!currentUser.isReceiptOnSMS()) {
				enableSMSSendingDivId.setVisible(false);
				enableSMSSendingCheckboxDivId.setVisible(false);
			}
			createDefaultFolders();
			if(currentUser.isZoneWise()){			
				setzoneWise();
				zoneSettingsId.setVisible(true);
				
				defaultZoneDigiSettings();
				
				getAllSettings();
			}
			else
				setLegacyWise();

			//setZone();
			//getAllSettings();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ", e);
		}
	}
	
	private void defaultDateFormat(){	//app-3706
		logger.debug("---Entered defaultDateFormat---");
		DigitalReceiptUserSettings digitalReceiptUserSettings = digitalReceiptUserSettingsDao.findByUserId(currentUser.getUserId());
		
		List<Listitem> dateformatList = selDateformatLbId.getItems();
		try {
			if(digitalReceiptUserSettings!=null) {
				for(Listitem li : dateformatList) {
					String dateformat=li.getLabel();
					if(dateformat != null || dateformat.equals("")) {
						if(dateformat.equals(digitalReceiptUserSettings.getDateFormat())) {
							selDateformatLbId.setSelectedItem(li);
							li.setSelected(true);
							break;
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error("Exception ::", e);
		}
		logger.debug("---Entered defaultDateFormat---");
	}
	private void defaultFromAndReplySettings(){	//app-3766
		logger.debug("---Entered defaultFromAndReplySettings---");
		DigitalReceiptUserSettings digitalReceiptUserSettings = digitalReceiptUserSettingsDao.findByUserId(currentUser.getUserId());
		
		try {
			boolean enabled = digitalReceiptUserSettings.isEnabled();
			//enabled ? enableSendingChkBxId.setChecked(true) : enableSendingChkBxId.setChecked(false);
			if (enabled) {
				enableSendingChkBxId.setChecked(true);
			} else {
				enableSendingChkBxId.setChecked(false);
			}
			onCheck$enableSendingChkBxId();
		} catch (Exception e) {
			logger.error("Exception ::", e);
		}
	}//defaultFromAndReplySettings
	

	/*
	 * public void onCheck$sameNameRadioId() { cFromNameTb.setVisible(true); }
	 */
	public void onCheck$sameNameRadioId() {
		cFromNameTb.setDisabled(false);
	}
	public void onCheck$dynamicNameRadioId() {
		cFromNameTb.setDisabled(true);
	}
	public void onCheck$sameFromEmailRadioId() {
		cFromEmailCb.setDisabled(false);
	}
	public void onCheck$dynamicFromEmailRadioId() {
		cFromEmailCb.setDisabled(true);
	}
	public void onCheck$sameReplyEmailRadioId() {
		cReplyEmailCb.setDisabled(false);
	}
	public void onCheck$dynamicReplyEmailRadioId() {
		cReplyEmailCb.setDisabled(true);
	}
	

    public void createDefaultFolders(){
        try{
            List<MyFolders> myFoldersDDFoldersList = myFolderDao.findByOrgIdFolderName(currentUser.getUserOrganization().getUserOrgId(),OCConstants.DnD_DIGITAL_RECEIPT_TEMPLATES,OCConstants.DRAG_DROP_EDITOR_DR_TEMPLATES_FOLDER_DEFAULT);
            if(myFoldersDDFoldersList.size()==0){
 
                MyFolders myfolder =new MyFolders(OCConstants.DRAG_DROP_EDITOR_DR_TEMPLATES_FOLDER_DEFAULT, MyCalendar.getNewCalendar(), currentUser.getUserId(), currentUser.getUserOrganization().getUserOrgId(),OCConstants.DnD_DIGITAL_RECEIPT_TEMPLATES);
                myfolder.setModifiedDate(MyCalendar.getNewCalendar());
                myFolderDaoForDML.saveOrUpdate(myfolder);
 
            }
 
            List<MyFolders> myFoldersLeagacyFoldersList = myFolderDao.findByOrgIdFolderName(currentUser.getUserOrganization().getUserOrgId(),OCConstants.LEGACY_DIGITAL_RECEIPT_TEMPLATES,OCConstants.DR_LEGACY_EDITOR_FOLDER);
            if(myFoldersLeagacyFoldersList.size()==0){
 
                MyFolders myfolder =new MyFolders(OCConstants.DR_LEGACY_EDITOR_FOLDER, MyCalendar.getNewCalendar(), currentUser.getUserId(), currentUser.getUserOrganization().getUserOrgId(),OCConstants.LEGACY_DIGITAL_RECEIPT_TEMPLATES);
                myfolder.setModifiedDate(MyCalendar.getNewCalendar());
                myFolderDaoForDML.saveOrUpdate(myfolder);
 
            }
 
        }catch (Exception e) {
            // TODO Auto-generated catch block
            logger.error("Exception ", e);;
        }
    }

	public void setzoneWise(){

		
		if(currentUser!=null){

			zoneWiseId.setVisible(true);
			Long domanID = usersDao.findDomainByUserId(currentUser.getUserId());

			UsersDomains userdomain= null;
			userdomain = userDomainDao.find(domanID);

			if (userdomain == null) {
				logger.debug("no domain exist in the DB...");
				return;
			}

			setUserOrgUnit(userdomain);
			orgUnitlbId.setVisible(true);

/*			List<OrganizationZone> liszone = zoneDao.getZoneStoresBydomainId(domanID+"");
			Listitem listItem1 = null;
			
			for(int i = dispZoneLBoxId.getItemCount() ; i>1 ; i--) {
				dispZoneLBoxId.removeItemAt(i);
			}
			
			if(liszone != null){
				for (OrganizationZone zone : liszone) {
					listItem1 = new Listitem(zone.getZoneName());
					listItem1.setValue(zone);
					listItem1.setParent(dispZoneLBoxId);
				}
			}

			if(dispZoneLBoxId.getItemCount()>1){
				dispZoneLBoxId.setSelectedIndex(0);
			}*/	
		}

	}



	/*	public void setzoneWise(){


		StringBuffer userdomainsBuffer = new StringBuffer();
		String userdomains= "";

		if(currentUser!=null){

			zoneWiseId.setVisible(true);
			Long domanID = usersDao.findDomainByUserId(currentUser.getUserId());

			isSuperUser	= usersDao.isSuperUser(currentUser.getUserId(),domanID);
			isPowerUser	= usersDao.isPowerUser(currentUser.getUserId(),domanID);
			List<UsersDomains> orgList = null;
			UsersDomains userdomain= null;

			if(isSuperUser){


				setUserOrgUnit();
				orgUnitlbId.setVisible(true);

				orgList = userDomainDao.FindByOrgId(currentUser.getUserOrganization().getUserOrgId());

				if (orgList == null) {
					logger.debug("no organization list exist in the DB...");
					return;
				}
				if (orgList != null && orgList.size() > 0) {
					for (UsersDomains orgunit : orgList) {
						if(userdomainsBuffer.length()>0) userdomainsBuffer.append(",");
						userdomainsBuffer.append(orgunit.getDomainId().toString());
					}
					userdomains = userdomainsBuffer.toString();
				}

				List<OrganizationZone> liszones = zoneDao.getZoneStoresBydomainId(userdomains);
				Listitem listItem = null;

				if(liszones != null){
					for (OrganizationZone zone : liszones) {
						listItem = new Listitem(zone.getZoneName());
						listItem.setValue(zone);
						listItem.setParent(dispZoneLBoxId);
					}
				}
			}	
			else if(isPowerUser){

				userdomain = userDomainDao.find(domanID);

				if (userdomain == null) {
					logger.debug("no organization list exist in the DB...");
					return;
				}
				if (userdomain != null) {
					userdomains = userdomain.toString();
				}
				List<OrganizationZone> liszone = zoneDao.getZoneStoresBydomainId(userdomains);
				Listitem listItem1 = null;

				if(liszone != null){
					for (OrganizationZone zone : liszone) {
						listItem1 = new Listitem(zone.getZoneName());
						listItem1.setValue(zone);
						listItem1.setParent(dispZoneLBoxId);
					}
				}
			}
			if(dispZoneLBoxId.getItemCount()>1){
				dispZoneLBoxId.setSelectedIndex(0);
			}	
		}

	}
	 */

	public void setLegacyWise(){

		setFolders();

		DigitalReceiptUserSettings digitalReceiptUserSettings = digitalReceiptUserSettingsDao.findUserSelectedTemplateByUserId(currentUser.getUserId());

		if(digitalReceiptUserSettings != null) {

			Long selectedTemplateID = digitalReceiptUserSettings.getMyTemplateId();
			//DigitalReceiptMyTemplate drMyTemp= digiRecptMyTemplatesDao.findByUserNameAndTemplateName(currentUser.getUserOrganization().getUserOrgId(), selectedTemplateID);
			//DigitalReceiptMyTemplate drMyTemp= digiRecptMyTemplatesDao.findByOrgIdAndTemplateName(currentUser.getUserOrganization().getUserOrgId(), selectedTemplateID);
			DigitalReceiptMyTemplate drMyTemp= digiRecptMyTemplatesDao.findByUserId_TemplateId(currentUser.getUserId(), selectedTemplateID);
			
			
			logger.info("selected template "+digitalReceiptUserSettings.getSelectedTemplateName());
			dispFolderBoxId.setSelectedIndex(0);

			if(drMyTemp!=null){
				List<Listitem> listitem=dispFolderBoxId.getItems();


				for(Listitem item:listitem){

					MyFolders myfolders = item.getValue();

					if(myfolders==null) continue;

					if(myfolders.getFolderName()!=null && (myfolders.getFolderName()).equals(drMyTemp.getFolderName())){
						dispFolderBoxId.setSelectedItem(item);
					}
				}
			}
			List<Listitem> items=mytemplatesLbId.getItems();
			logger.info("item count "+mytemplatesLbId.getItemCount());
			if(mytemplatesLbId.getItemCount()>=1){
				onSelect$dispFolderBoxId();
			}
			for(Listitem item:items){
				DigitalReceiptMyTemplate dr=item.getValue();
				if(dr!=null)
					if(dr.getMyTemplateId().equals(selectedTemplateID)){
						mytemplatesLbId.setSelectedItem(item);
						break;
					}
			}
			logger.debug("mytemplatesLbId===="+mytemplatesLbId.getSelectedIndex());
			defaultDigiSettings(digitalReceiptUserSettings);

		}
		else if(digitalReceiptUserSettings == null) {
			defaultDigiSettings();

			dispFolderBoxId.setSelectedIndex(0);
			mytemplatesLbId.setSelectedIndex(0);
			cSubTb.setValue("");
		}
		
		if(sameNameRadioId.isChecked()) 
			cFromNameTb.setDisabled(false);		
			else if(dynamicNameRadioId.isChecked())
				cFromNameTb.setDisabled(true);
		
		if(sameFromEmailRadioId.isChecked()) 
			cFromEmailCb.setDisabled(false);		
			else if(dynamicFromEmailRadioId.isChecked())
				cFromEmailCb.setDisabled(true);
		
		if(sameReplyEmailRadioId.isChecked()) 
			cReplyEmailCb.setDisabled(false);		
			else if(dynamicReplyEmailRadioId.isChecked())
				cReplyEmailCb.setDisabled(true);
		//onSelect$dispFolderBoxId();
	}

	/*	public void setLegacyWise(){

		setFolders();

		Long domanID = usersDao.findDomainByUserId(currentUser.getUserId());

		isSuperUser	= usersDao.isSuperUser(currentUser.getUserId(),domanID);
		isPowerUser	= usersDao.isPowerUser(currentUser.getUserId(),domanID);

		DigitalReceiptUserSettings digitalReceiptUserSettings = digitalReceiptUserSettingsDao.findUserSelectedTemplateByUserId(currentUser.getUserId());

		if(digitalReceiptUserSettings != null) {

			Long selectedTemplateID = digitalReceiptUserSettings.getMyTemplateId();
			//DigitalReceiptMyTemplate drMyTemp= digiRecptMyTemplatesDao.findByUserNameAndTemplateName(currentUser.getUserOrganization().getUserOrgId(), selectedTemplateID);
			DigitalReceiptMyTemplate drMyTemp= digiRecptMyTemplatesDao.findByOrgIdAndTemplateName(currentUser.getUserOrganization().getUserOrgId(), selectedTemplateID);

			logger.info("selected template "+digitalReceiptUserSettings.getSelectedTemplateName());
			dispFolderBoxId.setSelectedIndex(0);

			if(drMyTemp!=null){
				List<Listitem> listitem=dispFolderBoxId.getItems();


				for(Listitem item:listitem){

					MyFolders myfolders = item.getValue();

					if(myfolders==null) continue;

					if(myfolders.getFolderName()!=null && (myfolders.getFolderName()).equals(drMyTemp.getFolderName())){
						dispFolderBoxId.setSelectedItem(item);
					}
			      }
			}
				List<Listitem> items=mytemplatesLbId.getItems();
	   				logger.info("item count "+mytemplatesLbId.getItemCount());
	   				if(mytemplatesLbId.getItemCount()>=1){
	   					onSelect$dispFolderBoxId();
	   				}
	   			for(Listitem item:items){
	   				DigitalReceiptMyTemplate dr=item.getValue();
	   				if(dr!=null)
	   				if(dr.getMyTemplateId().equals(selectedTemplateID)){
	   					mytemplatesLbId.setSelectedItem(item);
	   					break;
	   				}
	   			}
	   			logger.debug("mytemplatesLbId===="+mytemplatesLbId.getSelectedIndex());
	   			defaultDigiSettings(digitalReceiptUserSettings);

		}
		else if(digitalReceiptUserSettings == null) {
			defaultDigiSettings();

			dispFolderBoxId.setSelectedIndex(0);
			mytemplatesLbId.setSelectedIndex(0);
			cSubTb.setValue("");
		}

		//onSelect$dispFolderBoxId();

	}*/


	/*public void setZone(){

		StringBuffer userdomainsBuffer = new StringBuffer();
		String userdomains= "";

		if(currentUser!=null){

			Long domanID = usersDao.findDomainByUserId(currentUser.getUserId());

			isSuperUser	= usersDao.isSuperUser(currentUser.getUserId(),domanID);
			isPowerUser	= usersDao.isPowerUser(currentUser.getUserId(),domanID);
			List<UsersDomains> orgList = null;
			UsersDomains userdomain= null;

			if(isSuperUser){

				orgList = userDomainDao.FindByOrgId(currentUser.getUserOrganization().getUserOrgId());

				if (orgList == null) {
					logger.debug("no organization list exist in the DB...");
					return;
				}
				if (orgList != null && orgList.size() > 0) {
					for (UsersDomains orgunit : orgList) {
						if(userdomainsBuffer.length()>0) userdomainsBuffer.append(",");
						userdomainsBuffer.append(orgunit.getDomainId().toString());
					}
					userdomains = userdomainsBuffer.toString();
				}

				List<OrganizationZone> liszones = zoneDao.getZoneStoresBydomainId(userdomains);
				Listitem listItem = null;

				if(liszones != null){
					for (OrganizationZone zone : liszones) {
						listItem = new Listitem(zone.getZoneName());
						listItem.setValue(zone);
						listItem.setParent(dispZoneLBoxId);
					}
				}
			}	
			else if(isPowerUser){

				userdomain = userDomainDao.find(domanID);

				if (userdomain == null) {
					logger.debug("no organization list exist in the DB...");
					return;
				}
				if (userdomain != null) {
					userdomains = userdomain.toString();
				}
				List<OrganizationZone> liszone = zoneDao.getZoneStoresBydomainId(userdomains);
				Listitem listItem1 = null;

				if(liszone != null){
					for (OrganizationZone zone : liszone) {
						listItem1 = new Listitem(zone.getZoneName());
						listItem1.setValue(zone);
						listItem1.setParent(dispZoneLBoxId);
					}
				}
			}
			if(dispZoneLBoxId.getItemCount()>1){
				dispZoneLBoxId.setSelectedIndex(0);
			}	
		}
	}*/

	public void onSelect$dispZoneLBoxId(){
		logger.info("======inside zone selection ===========");
		if(dispZoneLBoxId.getSelectedIndex() == 0){
			mytemplatesLbId.setSelectedIndex(0);
			dispFolderBoxId.setSelectedIndex(0);
			return;
		}

		setFolders();

		//Account acc=orgUnitlbId.getSelectedItem().getValue();
		//UsersDomains userDomain=(UsersDomains)orgUnitlbId.getSelectedItem().getValue();

		OrganizationZone orgZone = dispZoneLBoxId.getSelectedItem().getValue();
		//Long powerUserID = findPowerUserByDomainId(((UsersDomains)orgUnitlbId.getSelectedItem().getValue()).getDomainId());

		DigitalReceiptUserSettings digitalReceiptUserSettings = digitalReceiptUserSettingsDao.findUserSelectedTemplateByZone(currentUser.getUserId(), null, orgZone.getZoneId());

		if(digitalReceiptUserSettings != null) {

			Long selectedTemplateID = digitalReceiptUserSettings.getMyTemplateId();
			//DigitalReceiptMyTemplate drMyTemp= digiRecptMyTemplatesDao.findByUserNameAndTemplateName(currentUser.getUserOrganization().getUserOrgId(), selectedTemplateID);
			//DigitalReceiptMyTemplate drMyTemp= digiRecptMyTemplatesDao.findByOrgIdAndTemplateName(currentUser.getUserOrganization().getUserOrgId(), selectedTemplateID);
			DigitalReceiptMyTemplate drMyTemp= digiRecptMyTemplatesDao.findByUserId_TemplateId(currentUser.getUserId(), selectedTemplateID);

			
			logger.info("selected template "+digitalReceiptUserSettings.getSelectedTemplateName());
			dispFolderBoxId.setSelectedIndex(0);

			if(drMyTemp!=null){
				List<Listitem> listitem=dispFolderBoxId.getItems();


				for(Listitem item:listitem){

					MyFolders myfolders = item.getValue();

					if(myfolders==null) continue;

					if(myfolders.getFolderName()!=null && (myfolders.getFolderName()).equals(drMyTemp.getFolderName())){
						dispFolderBoxId.setSelectedItem(item);
					}
				}
			}
			List<Listitem> items=mytemplatesLbId.getItems();
			logger.info("item count "+mytemplatesLbId.getItemCount());
			if(mytemplatesLbId.getItemCount()>=1){
				onSelect$dispFolderBoxId();
			}
			for(Listitem item:items){
				DigitalReceiptMyTemplate dr=item.getValue();
				if(dr!=null)
					if(dr.getMyTemplateId().equals(selectedTemplateID)){
						mytemplatesLbId.setSelectedItem(item);
						break;
					}
			}
			logger.debug("mytemplatesLbId===="+mytemplatesLbId.getSelectedIndex());
			defaultDigiSettings(digitalReceiptUserSettings);

		}
		else if(digitalReceiptUserSettings == null) {
			defaultDigiSettings();

			dispFolderBoxId.setSelectedIndex(0);
			mytemplatesLbId.setSelectedIndex(0);
			cSubTb.setValue("");
		}
		
		if(sameNameRadioId.isChecked()) 
			cFromNameTb.setDisabled(false);		
			else if(dynamicNameRadioId.isChecked())
				cFromNameTb.setDisabled(true);
		
		if(sameFromEmailRadioId.isChecked()) 
			cFromEmailCb.setDisabled(false);		
			else if(dynamicFromEmailRadioId.isChecked())
				cFromEmailCb.setDisabled(true);
		
		if(sameReplyEmailRadioId.isChecked()) 
			cReplyEmailCb.setDisabled(false);		
			else if(dynamicReplyEmailRadioId.isChecked())
				cReplyEmailCb.setDisabled(true);
	}


	public void onSelect$dispFolderBoxId(){
		try {
			logger.info("=inside template set====");

			if(dispZoneLBoxId.getSelectedIndex()==0 && currentUser.isZoneWise()){
				MessageUtil.setMessage("Please choose zone first.", "color:blue", "TOP");
				dispFolderBoxId.setSelectedIndex(0);
				return;
			}

			int count = mytemplatesLbId.getItemCount();
			for (; count > 1; count--) {
				//mytemplatesLbId.removeItemAt(count - 1);
				mytemplatesLbId.removeItemAt(count - 1);
			}

			List<DigitalReceiptMyTemplate> drMyTemp = null;
			if(currentUser.isZoneWise()){

				OrganizationZone orgZone = dispZoneLBoxId.getSelectedItem().getValue();
				DigitalReceiptUserSettings digitalReceiptUserSettings = digitalReceiptUserSettingsDao.findUserSelectedTemplateByZone(currentUser.getUserId(), null, orgZone.getZoneId());

				//logger.info("ZoneWISE : :FolderName:"+dispFolderBoxId.getSelectedItem().getLabel()+" :isSuperUser:"+isSuperUser+" :isPowerUser: "+isPowerUser);

				if((dispFolderBoxId.getSelectedItem().getLabel()).equals(OCConstants.DRAG_DROP_EDITOR_DR_TEMPLATES_FOLDER_DEFAULT)){	
					drMyTemp = digiRecptMyTemplatesDao.findByUserId_PublishTemplatesonly(currentUser.getUserId(), OCConstants.DRAG_DROP_EDITOR_DR_TEMPLATES_FOLDER_DEFAULT);
				}else if((dispFolderBoxId.getSelectedItem().getLabel()).equals(OCConstants.DR_LEGACY_EDITOR_FOLDER)){	
					drMyTemp = digiRecptMyTemplatesDao.findByUserId_PublishTemplatesonly(currentUser.getUserId(), OCConstants.DR_LEGACY_EDITOR_FOLDER);
				}	

			}else{

				if((dispFolderBoxId.getSelectedItem().getLabel()).equals(OCConstants.DRAG_DROP_EDITOR_DR_TEMPLATES_FOLDER_DEFAULT)){	
					drMyTemp = digiRecptMyTemplatesDao.findByUserId_PublishTemplatesonly(currentUser.getUserId(), OCConstants.DRAG_DROP_EDITOR_DR_TEMPLATES_FOLDER_DEFAULT);
				}else if((dispFolderBoxId.getSelectedItem().getLabel()).equals(OCConstants.DR_LEGACY_EDITOR_FOLDER)){	
					drMyTemp = digiRecptMyTemplatesDao.findByUserId_PublishTemplatesonly(currentUser.getUserId(), OCConstants.DR_LEGACY_EDITOR_FOLDER);
				}	
			}
			if(drMyTemp!=null && drMyTemp.size()>0){
				Listitem listItem=null;
				for (DigitalReceiptMyTemplate digitalReceiptMyTemplate : drMyTemp) {
					listItem=new Listitem(digitalReceiptMyTemplate.getName());
					listItem.setValue(digitalReceiptMyTemplate);
					listItem.setParent(mytemplatesLbId);
					//logger.info(": digitalReceiptMyTemplate.getName() :"+digitalReceiptMyTemplate.getName()+" :digitalReceiptMyTemplate: "+digitalReceiptMyTemplate);
				}
				mytemplatesLbId.setSelectedIndex(0);
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	} 


	/*	public void onSelect$dispFolderBoxId(){
		try {
			logger.info("=inside template set====");
			//Account acc=(Account)orgUnitlbId.getSelectedItem().getValue();

			//UsersDomains userDomain=(UsersDomains)orgUnitlbId.getSelectedItem().getValue();

			if(dispZoneLBoxId.getSelectedIndex()==0 && currentUser.isZoneWise()){
				MessageUtil.setMessage("Please choose zone first.", "color:blue", "TOP");
				dispFolderBoxId.setSelectedIndex(-1);
				return;
			}

			int count = mytemplatesLbId.getItemCount();
			for (; count > 1; count--) {
				mytemplatesLbId.removeItemAt(count - 1);
			}

			List<DigitalReceiptMyTemplate> drMyTemp = null;
			if(currentUser.isZoneWise()){

				OrganizationZone orgZone = dispZoneLBoxId.getSelectedItem().getValue();
				DigitalReceiptUserSettings digitalReceiptUserSettings = digitalReceiptUserSettingsDao.findUserSelectedTemplateByZone(currentUser.getUserId(), null, orgZone.getZoneId());

				//List<DigitalReceiptMyTemplate> drMyTemp = null;	
				logger.info("ZoneWISE : :FolderName:"+dispFolderBoxId.getSelectedItem().getLabel()+" :isSuperUser:"+isSuperUser+" :isPowerUser: "+isPowerUser);

					if((dispFolderBoxId.getSelectedItem().getLabel()).equals(OCConstants.DRAG_DROP_EDITOR_DR_TEMPLATES_FOLDER_DEFAULT)){	
						if(isSuperUser)
							drMyTemp = digiRecptMyTemplatesDao.findByOrgId_PublishTemplatesonly(currentUser.getUserOrganization().getUserOrgId(), OCConstants.DRAG_DROP_EDITOR_DR_TEMPLATES_FOLDER_DEFAULT);
						else if(isPowerUser)
							drMyTemp = digiRecptMyTemplatesDao.findByUserId_PublishTemplatesonly(currentUser.getUserId(), OCConstants.DRAG_DROP_EDITOR_DR_TEMPLATES_FOLDER_DEFAULT);

					}else if((dispFolderBoxId.getSelectedItem().getLabel()).equals(OCConstants.DR_LEGACY_EDITOR_FOLDER)){	
						if(isSuperUser)
							drMyTemp = digiRecptMyTemplatesDao.findByOrgId_PublishTemplatesonly(currentUser.getUserOrganization().getUserOrgId(), OCConstants.DR_LEGACY_EDITOR_FOLDER);
						else if(isPowerUser)
							drMyTemp = digiRecptMyTemplatesDao.findByUserId_PublishTemplatesonly(currentUser.getUserId(), OCConstants.DR_LEGACY_EDITOR_FOLDER);
					}	
					Listitem listItem=null;
					for (DigitalReceiptMyTemplate digitalReceiptMyTemplate : drMyTemp) {
						listItem=new Listitem(digitalReceiptMyTemplate.getName(),digitalReceiptMyTemplate);
						listItem.setParent(mytemplatesLbId);
					}


			}else{

				//List<DigitalReceiptMyTemplate> drMyTemp = null;	
				if((dispFolderBoxId.getSelectedItem().getLabel()).equals(OCConstants.DRAG_DROP_EDITOR_DR_TEMPLATES_FOLDER_DEFAULT)){	
					if(isSuperUser){
						//drMyTemp = digiRecptMyTemplatesDao.findByOrgId_PublishTemplatesonly(currentUser.getUserOrganization().getUserOrgId(), OCConstants.DRAG_DROP_EDITOR_DR_TEMPLATES_FOLDER_DEFAULT);
						drMyTemp = digiRecptMyTemplatesDao.findByUserId_PublishTemplatesonly(currentUser.getUserId(), OCConstants.DRAG_DROP_EDITOR_DR_TEMPLATES_FOLDER_DEFAULT);

					}else if(isPowerUser)
						drMyTemp = digiRecptMyTemplatesDao.findByUserId_PublishTemplatesonly(currentUser.getUserId(), OCConstants.DRAG_DROP_EDITOR_DR_TEMPLATES_FOLDER_DEFAULT);

				}else if((dispFolderBoxId.getSelectedItem().getLabel()).equals(OCConstants.DR_LEGACY_EDITOR_FOLDER)){	
					if(isSuperUser){
						//drMyTemp = digiRecptMyTemplatesDao.findByOrgId_PublishTemplatesonly(currentUser.getUserOrganization().getUserOrgId(), OCConstants.DR_LEGACY_EDITOR_FOLDER);
						drMyTemp = digiRecptMyTemplatesDao.findByUserId_PublishTemplatesonly(currentUser.getUserId(), OCConstants.DRAG_DROP_EDITOR_DR_TEMPLATES_FOLDER_DEFAULT);

					}else if(isPowerUser)
						drMyTemp = digiRecptMyTemplatesDao.findByUserId_PublishTemplatesonly(currentUser.getUserId(), OCConstants.DR_LEGACY_EDITOR_FOLDER);
				}	
				Listitem listItem=null;
				for (DigitalReceiptMyTemplate digitalReceiptMyTemplate : drMyTemp) {
					listItem=new Listitem(digitalReceiptMyTemplate.getName(),digitalReceiptMyTemplate);
					listItem.setParent(mytemplatesLbId);
				}

			}
			if(drMyTemp!=null && drMyTemp.size()>0){
				Listitem listItem=null;
				for (DigitalReceiptMyTemplate digitalReceiptMyTemplate : drMyTemp) {
					listItem=new Listitem(digitalReceiptMyTemplate.getName());
					listItem.setValue(digitalReceiptMyTemplate);
					listItem.setParent(mytemplatesLbId);
					logger.info(": digitalReceiptMyTemplate.getName() :"+digitalReceiptMyTemplate.getName()+" :digitalReceiptMyTemplate: "+digitalReceiptMyTemplate);
				}
			}
			//mytemplatesLbId.setSelectedIndex(0);		

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	} 
	 */


	/*	public void onSelect$dispFolderBoxId(){
		try {
			logger.info("=inside template set====");
			//Account acc=(Account)orgUnitlbId.getSelectedItem().getValue();

			//UsersDomains userDomain=(UsersDomains)orgUnitlbId.getSelectedItem().getValue();

			if(dispZoneLBoxId.getSelectedIndex()==0){
				MessageUtil.setMessage("Please choose zone first.", "color:blue", "TOP");
				dispFolderBoxId.setSelectedIndex(-1);
				return;
			}
			int count = mytemplatesLbId.getItemCount();
			for (; count > 1; count--) {
				mytemplatesLbId.removeItemAt(count - 1);
			}
			Set<Listitem> items=dispFolderBoxId.getSelectedItems();

				boolean unlabel=false;
		StringBuilder sb=new StringBuilder();
		for(Listitem item:items){
			DRTemplateLabel label=item.getValue();
			if(label!=null){
				if(sb.length()>0)sb.append(",");
				sb.append(label.getLabelId());
			}
			else if(label==null)unlabel=true;
		}
		int cnt= dispFolderBoxId.getSelectedCount();
		if(unlabel)
			cnt=cnt-1; 
		List<DRTemplateLabel> list= drTemplateLabelDAO.findBy(acc.getAccountId());
		 List<DigitalReceiptMyTemplate> tempObj=null;
		 logger.info("Selected label "+sb.toString());
		if(sb!=null && sb.length()>0){
			tempObj= digiRecptMyTemplatesDao.findDRMyTemplatesByLabel(""+acc.getAccountId(),sb.toString(),cnt,0,100,null);
		}
		else if(list==null || list.size()==0){
			tempObj= digiRecptMyTemplatesDao.findAllTemplatesByAccount(acc.getAccountId());
		}
		else{
			tempObj= digiRecptMyTemplatesDao.findTemplatesByAccountNotLabel(acc.getAccountId());
		}
			OrganizationZone orgZone = dispZoneLBoxId.getSelectedItem().getValue();
			//if(orgUnitlbId.getSelectedIndex()==0) return;

			//Long powerUserID = findPowerUserByDomainId(userDomain.getDomainId());
			//DRTemplateListObj= digiRecptMyTemplatesDao.findTemplatesByAccountNotLabel(userDomain.getDomainId());

			DigitalReceiptUserSettings digitalReceiptUserSettings = digitalReceiptUserSettingsDao.findUserSelectedTemplateByZone(currentUser.getUserId(), null, orgZone.getZoneId());

			if(digitalReceiptUserSettings != null) {

	   			Long selectedTemplateID = digitalReceiptUserSettings.getMyTemplateId();
	   			DigitalReceiptMyTemplate drMyTemp= digiRecptMyTemplatesDao.findByUserNameAndTemplateName(powerUserID, selectedTemplateID);
	   			if(drMyTemp!=null){

	   			String selectedTemplate = drMyTemp.getName();
	   			logger.debug("selectedTemplate===="+selectedTemplate);
	   			if (selectedTemplate != null) {
	   				selectedTemplateLblId.setAttribute("templateName", selectedTemplate);
	   				selectedTemplateLblId.setValue(selectedTemplate);
	   			}

	   				boolean folderSelected = false;
	   				boolean mytemplateSelected = true;
	   				String selectedFolder = drMyTemp.getFolderName();
	   				List<Listitem> retList1 = dispFolderBoxId.getItems();
	   				if(selectedFolder!=null)
	   				{	for (Listitem listitem : retList1) {
	   					if(listitem.getIndex()==0) continue;
	   					if(listitem.getLabel().equalsIgnoreCase(selectedFolder)) {
	   						listitem.setSelected(true);
	   						folderSelected = true;
	   						break;
	   					}
	   				}
	   				}
	   				if(folderSelected) onSelect$dispFolderBoxId();
	   				else mytemplatesFolderLbId.setSelectedIndex(0);
	   				List<Listitem> retList = mytemplatesLbId.getItems();

	   				for (Listitem listitem : retList) {
	   					if(listitem.getIndex()==0) continue;
	   					if(listitem.getLabel().equalsIgnoreCase(selectedTemplate)) {
	   						listitem.setSelected(true);
	   						mytemplateSelected = false;
	   						break;
	   					}
	   				}
	   				if(mytemplateSelected) mytemplatesLbId.setSelectedIndex(0);
	   			}
	   			logger.debug("mytemplatesLbId===="+mytemplatesLbId.getSelectedIndex());
	   			defaultDigiSettings(digitalReceiptUserSettings);

	   		}


			List<DigitalReceiptMyTemplate> drMyTemp = null;	
				if((dispFolderBoxId.getSelectedItem().getLabel()).equals(OCConstants.DRAG_DROP_EDITOR_DR_TEMPLATES_FOLDER_DEFAULT)){	
					if(isSuperUser)
						drMyTemp = digiRecptMyTemplatesDao.findByOrgId_PublishTemplatesonly(currentUser.getUserOrganization().getUserOrgId(), OCConstants.DRAG_DROP_EDITOR_DR_TEMPLATES_FOLDER_DEFAULT);
					else if(isPowerUser)
						drMyTemp = digiRecptMyTemplatesDao.findByUserId_PublishTemplatesonly(currentUser.getUserId(), OCConstants.DRAG_DROP_EDITOR_DR_TEMPLATES_FOLDER_DEFAULT);

				}else if((dispFolderBoxId.getSelectedItem().getLabel()).equals(OCConstants.DR_LEGACY_EDITOR_FOLDER)){	
					if(isSuperUser)
						drMyTemp = digiRecptMyTemplatesDao.findByOrgId_PublishTemplatesonly(currentUser.getUserOrganization().getUserOrgId(), OCConstants.DR_LEGACY_EDITOR_FOLDER);
					else if(isPowerUser)
						drMyTemp = digiRecptMyTemplatesDao.findByUserId_PublishTemplatesonly(currentUser.getUserId(), OCConstants.DR_LEGACY_EDITOR_FOLDER);
				}	
				Listitem listItem=null;
				for (DigitalReceiptMyTemplate digitalReceiptMyTemplate : drMyTemp) {
					listItem=new Listitem(digitalReceiptMyTemplate.getName(),digitalReceiptMyTemplate);
					listItem.setParent(mytemplatesLbId);
				}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	} 
	 */

	//TODO
	public void onClick$enableDRSendingsaveBtnId() {
		//myTemplatesListId,

		DigitalReceiptUserSettings digitalReceiptUserSettings  = digitalReceiptUserSettingsDao.findByUserId(currentUser.getUserId());

		if(digitalReceiptUserSettings == null) {
			digitalReceiptUserSettings = new DigitalReceiptUserSettings();
		}


		//Set The Template Name
		logger.info("===============================digitalReceiptUserSettings.getSelectedTemplateName()        "+digitalReceiptUserSettings.getSelectedTemplateName());
		if(digitalReceiptUserSettings.getSelectedTemplateName() != null) {

			/*String selTemplateName = ((String)selectedTemplateLblId.getAttribute("templateName")).trim();
			if(selTemplateName.equals(DEFAULT_TEMPLATE_NAME)){

				digitalReceiptUserSettings.setSelectedTemplateName(SYSTEM_TEMPLATE+digitalTemplatesListId.getSelectedItem().getValue());
			}
			else{

				digitalReceiptUserSettings.setSelectedTemplateName(selTemplateName);

			}*/

		}
		else{
			MessageUtil.setMessage("To enable e-Receipts, you would need to first create a template and save it as your choice.","color:blue;");
			enableSendingChkBxId.setChecked(false);
			//enableSMSSendingChkBxId.setChecked(false);
			return;
		}
		//app-3706
		String selectedDateformat = "";
		try {
			selectedDateformat = selDateformatLbId.getSelectedItem().getLabel();
		} catch (Exception e) {
			logger.info("exception occured in date format selection");
		}
		try {
			int confirm = Messagebox.show(
					"Are you sure you want to change the setting?", "Prompt",
					Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);

			if (confirm == Messagebox.OK) {

				try {

					if(currentUser.isZoneWise()) {
						
						/**
						 * User wise setting
						 * 
						 */
						digitalReceiptUserSettingsDaoForDML.updateDigiUserSetting(currentUser.getUserId(), enableSendingChkBxId.isChecked(),enableSMSSendingChkBxId.isChecked(), shippingAmntChkBxId.isChecked(),
								feeAmntChkBxId.isChecked(), taxAmntChkBxId.isChecked(), discAmntChkBxId.isChecked(), totalAmntChkBxId.isChecked(),selectedDateformat);//app-3706
						
						
					}else {	
						digitalReceiptUserSettings.setEnabled(enableSendingChkBxId.isChecked());
						digitalReceiptUserSettings.setSmsEnabled(enableSMSSendingChkBxId.isChecked());
						digitalReceiptUserSettings.setIncludeShipping(shippingAmntChkBxId.isChecked());
						digitalReceiptUserSettings.setIncludeFee(feeAmntChkBxId.isChecked());
						digitalReceiptUserSettings.setIncludeTax(taxAmntChkBxId.isChecked());
						digitalReceiptUserSettings.setIncludeGlobalDiscount(discAmntChkBxId.isChecked());
						digitalReceiptUserSettings.setIncludeTotalAmount(totalAmntChkBxId.isChecked());
						digitalReceiptUserSettings.setDateFormat(selectedDateformat);

						digitalReceiptUserSettingsDaoForDML.saveOrUpdate(digitalReceiptUserSettings);
					}
				}catch(Exception e){

					logger.error("Exception ::", e);
				}
				//MessageUtil.setMessage("Digital receipt settings saved successfully.","color:green;");


			}
		}catch(Exception e1){
			logger.error("Exception ::", e1);
		}
	}


	public void onClick$tmepsaveBtnId() {
		try {
			//myTemplatesListId,

			
			if(currentUser.isZoneWise()){
				if(orgUnitlbId.getSelectedIndex() == 0 ) {

					MessageUtil.setMessage("Please Select Organization Unit. ", "color:red");
					return;
				}
				if(dispZoneLBoxId.getSelectedIndex() == 0 ) {

					MessageUtil.setMessage("Please Select Zone. ", "color:red");
					return;
				}
			}
			
			if(dispFolderBoxId.getSelectedIndex()== 0) {

				MessageUtil.setMessage("Please Select Folder. ", "color:red");
				return;
			}
			
			if(mytemplatesLbId.getSelectedIndex()== 0) {

				MessageUtil.setMessage("Please Select a template. ", "color:red");
				return;
			}

			
			DigitalReceiptUserSettings digitalReceiptUserSettings  = null;

			//logger.info("mytemplatesLbId.getSelectedItem() :"+mytemplatesLbId);
			//logger.info("mytemplatesLbId.getSelectedItem() :"+mytemplatesLbId.getSelectedIndex());
			logger.info("mytemplatesLbId.getSelectedItem() :"+mytemplatesLbId.getSelectedItem());
			//logger.info("mytemplatesLbId.getSelectedItem() :"+mytemplatesLbId.getChildren());
			//logger.info("mytemplatesLbId.getSelectedItem() :"+mytemplatesLbId.getSelectedItem().getValue());

			
			
			DigitalReceiptMyTemplate d1= (DigitalReceiptMyTemplate)mytemplatesLbId.getSelectedItem().getValue();
			if(d1==null)return;

			if(currentUser.isZoneWise()){

				if(tmepsaveBtnId.getAttribute(STORE_WISE_DR_SETTINGS_OBJ) != null) {
					digitalReceiptUserSettings = (DigitalReceiptUserSettings)tmepsaveBtnId.getAttribute(STORE_WISE_DR_SETTINGS_OBJ);
					//digitalReceiptUserSettings.setSelectedTemplateName(MY_TEMPLATE+d1.getName());
					digitalReceiptUserSettings.setSelectedTemplateName(d1.getName());
					digitalReceiptUserSettings.setMyTemplateId(d1.getMyTemplateId());
					//digitalReceiptUserSettings.setUserId(currentUser.getUserId());
					logger.debug("=====edit========"+digitalReceiptUserSettings);
				}else{
					logger.debug("=====new========");

					digitalReceiptUserSettings = new DigitalReceiptUserSettings();
					digitalReceiptUserSettings.setUserId(currentUser.getUserId());

					//digitalReceiptUserSettings.setSelectedTemplateName(MY_TEMPLATE+d1.getName());
					digitalReceiptUserSettings.setSelectedTemplateName(d1.getName());

					digitalReceiptUserSettings.setMyTemplateId(d1.getMyTemplateId());


					digitalReceiptUserSettings.setZoneId(((OrganizationZone)dispZoneLBoxId.getSelectedItem().getValue()).getZoneId());

				}

			}else{

				if(tmepsaveBtnId.getAttribute(STORE_WISE_DR_SETTINGS_OBJ) != null) {
					digitalReceiptUserSettings = (DigitalReceiptUserSettings)tmepsaveBtnId.getAttribute(STORE_WISE_DR_SETTINGS_OBJ);
					//digitalReceiptUserSettings.setSelectedTemplateName(MY_TEMPLATE+d1.getName());
					digitalReceiptUserSettings.setSelectedTemplateName(d1.getName());
					digitalReceiptUserSettings.setMyTemplateId(d1.getMyTemplateId());
					//digitalReceiptUserSettings.setUserId(currentUser.getUserId());
					logger.debug("=====Legacy_edit========"+digitalReceiptUserSettings);
				}else{
					logger.debug("=====Legacy========");

					digitalReceiptUserSettings = new DigitalReceiptUserSettings();
					digitalReceiptUserSettings.setUserId(currentUser.getUserId());
					//digitalReceiptUserSettings.setSelectedTemplateName(MY_TEMPLATE+d1.getName());
					digitalReceiptUserSettings.setSelectedTemplateName(d1.getName());
					digitalReceiptUserSettings.setMyTemplateId(d1.getMyTemplateId());

				}

			}

			/*		DigitalReceiptUserSettings digitalReceiptUserSettings  = digitalReceiptUserSettingsDao.findByUserId(currentUser.getUserId());

			if(digitalReceiptUserSettings == null) {

				digitalReceiptUserSettings = new DigitalReceiptUserSettings();

			}*/

			// check for subject and it is valid or not 
			if(cSubTb.isValid() && !cSubTb.getValue().isEmpty()){

				digitalReceiptUserSettings.setSubject(cSubTb.getValue());
			}
			else{
				MessageUtil.setMessage("Please provide subject. Subject should not be left empty.", "color:red", "TOP");
				cSubTb.setFocus(true);
				return;
			}

			//  check for from name n it is valid or not
			if( cFromNameTb.isValid() &&  Utility.validateFromName(cFromNameTb.getValue())){

				if(frmNamedynamicOrNotRgId.getSelectedIndex()==0 )
					digitalReceiptUserSettings.setFromName(cFromNameTb.getValue());
			}
			else{
				MessageUtil.setMessage("Provide valid 'From Name'. Special characters are not allowed.", "color:red", "TOP");
				cFromNameTb.setFocus(true);
				return;
			}

			// check for from email address n display all the fromEmails n those fromEmails are valid or not 
			if(enableSendingChkBxId.isChecked() && fromAndReplyDivId.isVisible() ) { //app-3766
				
            String errmsg = "Please select or register another email address.";
            
            try {
            	if(cFromEmailCb.getSelectedItem().getLabel().indexOf('@') < 0) {
            		MessageUtil.setMessage("Register a 'From Email' to create an email.","color:red", "TOP");
            	}
            	else if(!(cFromEmailCb.getSelectedIndex()==-1)) {
            		if(frmEmaildynamicOrNotRgId.getSelectedIndex() == 0)
            			digitalReceiptUserSettings.setFromEmail(cFromEmailCb.getSelectedItem().getLabel());
            	}
            	else {
            		MessageUtil.setMessage("Provide valid 'From Email'.", "color:red", "TOP");
            		cFromEmailCb.setFocus(true);
            		return;
            	}            
            } catch (Exception e2) {
                // TODO Auto-generated catch block
                logger.info("cFromEmailCb.getSelectedItem() :"+cFromEmailCb.getSelectedItem());
                logger.error("Exception ::", e2);
                if(frmEmaildynamicOrNotRgId.getSelectedIndex() == 0 && cFromEmailCb.getSelectedItem()==null){
                    ToEmailerrorLBId.setVisible(false);
                    fromEmailerrorLBId.setValue(errmsg);
                    logger.debug("Some errors found:"+fromEmailerrorLBId.getValue()+"===="+fromEmailerrorLBId.isVisible());
                    fromEmailerrorLBId.setVisible(true);
                    return;
                }
                
 
            }

			//new
            try {
            	if(cReplyEmailCb.getSelectedItem().getLabel().indexOf('@') < 0) {
            		MessageUtil.setMessage("Register a 'Reply Email' to create an email.","color:red", "TOP");
            	}
            	else if(!(cReplyEmailCb.getSelectedIndex()==-1)) {
            		if(replyEmaildynamicOrNotRgId.getSelectedIndex() == 0)
            			digitalReceiptUserSettings.setReplyToEmail(cReplyEmailCb.getSelectedItem().getLabel());
            	}
            	else {
            		MessageUtil.setMessage("Provide valid 'Reply Email'.", "color:red", "TOP");
            		cReplyEmailCb.setFocus(true);
            		return;
            	}
            } catch (Exception e2) {
                // TODO Auto-generated catch block
                logger.error("Exception ::", e2);
                if(replyEmaildynamicOrNotRgId.getSelectedIndex() == 0 && cReplyEmailCb.getSelectedItem()==null){
                    fromEmailerrorLBId.setVisible(false);
                    ToEmailerrorLBId.setValue(errmsg);
                    logger.debug("Some errors found:"+ToEmailerrorLBId.getValue()+"===="+ToEmailerrorLBId.isVisible());
                    ToEmailerrorLBId.setVisible(true);
                    return;
                }
                
            }
			}//if
			
			
			digitalReceiptUserSettings.setEnabled(enableSendingChkBxId.isChecked());
			digitalReceiptUserSettings.setSmsEnabled(enableSMSSendingChkBxId.isChecked());
			digitalReceiptUserSettings.setIncludeShipping(shippingAmntChkBxId.isChecked());
			digitalReceiptUserSettings.setIncludeFee(feeAmntChkBxId.isChecked());
			digitalReceiptUserSettings.setIncludeTax(taxAmntChkBxId.isChecked());
			digitalReceiptUserSettings.setIncludeGlobalDiscount(discAmntChkBxId.isChecked());
			digitalReceiptUserSettings.setIncludeTotalAmount(totalAmntChkBxId.isChecked());


			int selIndxEml = frmEmaildynamicOrNotRgId.getSelectedIndex();
			int selIndxName = frmNamedynamicOrNotRgId.getSelectedIndex();
			
			int selIndxReplyEml = replyEmaildynamicOrNotRgId.getSelectedIndex();


			digitalReceiptUserSettings.setIncludeDynamicFrmEmail(selIndxEml == 0 ? false : true);
			digitalReceiptUserSettings.setIncludeDynamicFrmName(selIndxName == 0 ? false : true );
			
			digitalReceiptUserSettings.setIncludeDynamicReplyToEmail(selIndxReplyEml == 0 ? false : true);



			// ***** optional settings **************

			//web page version 

			if(cWebPageCb.isChecked()){
				digitalReceiptUserSettings.setWebLinkFlag(true);
				digitalReceiptUserSettings.setWebLinkText(cWebLinkTextTb.getValue());
				if(!cWebLinkUrlTextTb.getValue().equals("")){
					digitalReceiptUserSettings.setWebLinkUrlText(cWebLinkUrlTextTb.getValue());
				}else{
					MessageUtil.setMessage("Provide web-link Url text.", "color:red", "TOP");
					cWebLinkUrlTextTb.setFocus(true);
					return;
				}
			}else{
				digitalReceiptUserSettings.setWebLinkFlag(false);
			}

			//permission reminder 

			if(cPermRemRb.getSelectedIndex() == 0){
				if(!permRemTextId.getValue().trim().equals("")){
					digitalReceiptUserSettings.setPermissionRemainderFlag(true);
					digitalReceiptUserSettings.setPermissionRemainderText(permRemTextId.getValue());
				}
			}else{
				digitalReceiptUserSettings.setPermissionRemainderFlag(false);
			}

			//personalize to field

			if(toNameChkId.isChecked()){
				digitalReceiptUserSettings.setPersonalizeTo(true);
				if(phLbId.getSelectedIndex()>0){
					digitalReceiptUserSettings.setToName((String)phLbId.getSelectedItem().getValue());
				}else{
					phLbId.setSelectedIndex(0);
					digitalReceiptUserSettings.setToName((String)phLbId.getSelectedItem().getValue());
				}
			}else{
				digitalReceiptUserSettings.setPersonalizeTo(false);
			}
			MessageUtil.clearMessage();


			digitalReceiptUserSettings.setSubject(cSubTb.getValue().trim());

			if(frmNamedynamicOrNotRgId.getSelectedIndex()==0)
				digitalReceiptUserSettings.setFromName(cFromNameTb.getValue().trim());


			digitalReceiptUserSettings.setUserId(currentUser.getUserId());
			
			if(enableSendingChkBxId.isChecked() && fromAndReplyDivId.isVisible() ) {
				if(frmEmaildynamicOrNotRgId.getSelectedIndex()==0)
					digitalReceiptUserSettings.setFromEmail(cFromEmailCb.getValue().trim());
				
				if(replyEmaildynamicOrNotRgId.getSelectedIndex()==0)
					digitalReceiptUserSettings.setReplyToEmail(cReplyEmailCb.getValue().trim());
			}

			digitalReceiptUserSettings.setCreatedDate(Calendar.getInstance());
			//Set The Template Name
			logger.info("===============================digitalReceiptUserSettings.getSelectedTemplateName()        "+digitalReceiptUserSettings.getSelectedTemplateName());
			if(digitalReceiptUserSettings.getSelectedTemplateName() != null) {

				/*String selTemplateName = ((String)selectedTemplateLblId.getAttribute("templateName")).trim();
				if(selTemplateName.equals(DEFAULT_TEMPLATE_NAME)){
					digitalReceiptUserSettings.setSelectedTemplateName(SYSTEM_TEMPLATE+digitalTemplatesListId.getSelectedItem().getValue());
				}
				else{
					digitalReceiptUserSettings.setSelectedTemplateName(selTemplateName);
				}*/

				DigitalReceiptMyTemplate dr= mytemplatesLbId.getSelectedItem().getValue();

				if((dr.getName()).equals(DEFAULT_TEMPLATE_NAME)){
					digitalReceiptUserSettings.setSelectedTemplateName(SYSTEM_TEMPLATE+digitalTemplatesListId.getSelectedItem().getValue());
				}
				else{
					digitalReceiptUserSettings.setSelectedTemplateName(dr.getName());
				}

			}
			else{
				MessageUtil.setMessage("To enable e-Receipts, you would need to first create a template and save it as your choice.","color:blue;");
				enableSendingChkBxId.setChecked(false);
				enableSMSSendingChkBxId.setChecked(false);
				return;
			}
			try {
				int confirm = Messagebox.show(
						"Are you sure you want to save the e-Receipt settings?", "Prompt",
						Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);

				if (confirm == Messagebox.OK) {

					try {
						//digitalReceiptUserSettingsDao.saveOrUpdate(digitalReceiptUserSettings);
						digitalReceiptUserSettingsDaoForDML.saveOrUpdate(digitalReceiptUserSettings);
						MessageUtil.setMessage("e-Receipt settings saved successfully.","color:green;","TOP");
						/*tmepsaveBtnId.removeAttribute(STORE_WISE_DR_SETTINGS_OBJ);
						Redirect.goTo(PageListEnum.EMPTY);
						Redirect.goTo(PageListEnum.CAMPAIGN_E_RECEIPT_SETTINGS);
                                             */

					}catch(Exception e){

						logger.error("Exception ::", e);
					}
					//MessageUtil.setMessage("Digital receipt settings saved successfully.","color:green;");


				}
			}catch(Exception e1){
				logger.error("Exception ::", e1);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
		}
		tmepsaveBtnId.removeAttribute(STORE_WISE_DR_SETTINGS_OBJ);
		Redirect.goTo(PageListEnum.EMPTY);
		Redirect.goTo(PageListEnum.CAMPAIGN_E_RECEIPT_SETTINGS);
	}



	public void defaultDigiSettings(DigitalReceiptUserSettings digitalReceiptUserSettings) {


		List<UserFromEmailId> userFromEmailIdList = userFromEmailIdDao.getEmailsByUserId(currentUser.getUserId());
		logger.info("List size : " + userFromEmailIdList.size());

		Components.removeAllChildren(cFromEmailCb);
		Components.removeAllChildren(cReplyEmailCb);

		tmepsaveBtnId.setAttribute(STORE_WISE_DR_SETTINGS_OBJ, digitalReceiptUserSettings);

		List<String> listOfEmailIds = new ArrayList<String>();

		for(UserFromEmailId anUserFromEmailId: userFromEmailIdList){
			if(anUserFromEmailId.getEmailId() != null){

				if( !listOfEmailIds.contains(anUserFromEmailId.getEmailId())){
					listOfEmailIds.add(anUserFromEmailId.getEmailId());
				}
			}
		}

		if (currentUser != null && currentUser.getEmailId() != null) {
			cFromEmailCb.appendItem(currentUser.getEmailId());
			cReplyEmailCb.appendItem(currentUser.getEmailId());
		}

		if (listOfEmailIds.size() > 0) {
			for (Object obj : listOfEmailIds) {
				String anEmailId = (String) obj;
				cFromEmailCb.appendItem(anEmailId);
				cReplyEmailCb.appendItem(anEmailId);
			}
		}


		/*if (userFromEmailIdList.size() > 0) {
			for (Object obj : userFromEmailIdList) {
				UserFromEmailId userFromEmailId = (UserFromEmailId) obj;
				cFromEmailCb.appendItem(userFromEmailId.getEmailId());

			}
		}*/

		if (!(cFromEmailCb.getItemCount() > 0)) {
			cFromEmailCb.appendItem("No emails registered.");

		}
		cFromEmailCb.setSelectedIndex(0);
		
		if (!(cReplyEmailCb.getItemCount() > 0)) {
			cReplyEmailCb.appendItem("No emails registered.");

		}
		cReplyEmailCb.setSelectedIndex(0);


		cFromNameTb.setValue(currentUser.getCompanyName());

		//DigitalReceiptUserSettings digitalReceiptUserSettings = digitalReceiptUserSettingsDao.findByUserId(currentUser.getUserId());


		if(digitalReceiptUserSettings == null) {

			return;
		}

		// default  user subject 
		if(digitalReceiptUserSettings.getSubject() != null){
			cSubTb.setValue(digitalReceiptUserSettings.getSubject());
		}

		if(digitalReceiptUserSettings.getFromName() != null){
			cFromNameTb.setValue(digitalReceiptUserSettings.getFromName());
		}

		String fromEmailId = digitalReceiptUserSettings.getFromEmail();
		for (int index = 0; index < cFromEmailCb.getItemCount(); index++) {

			// logger.debug(cFromEmailCb.getItemAtIndex(index).getLabel() +
			// " == " + fromEmailId);
			if (cFromEmailCb.getItemAtIndex(index).getLabel().equals(fromEmailId)) {
				cFromEmailCb.setSelectedIndex(index);
			}
		}
		
		
		String replyEmailId = digitalReceiptUserSettings.getReplyToEmail();
		for (int index = 0; index < cReplyEmailCb.getItemCount(); index++) {

			// logger.debug(cReplyEmailCb.getItemAtIndex(index).getLabel() +
			// " == " + fromEmailId);
			if (cReplyEmailCb.getItemAtIndex(index).getLabel().equals(replyEmailId)) {
				cReplyEmailCb.setSelectedIndex(index);
			}
		}
		
        if(cFromEmailCb.getItemCount()>0 && fromEmailId!=null && !fromEmailId.isEmpty() && !(cFromEmailCb.getValue().trim()).equals(fromEmailId))
            cFromEmailCb.setValue(fromEmailId);
        
        if(cReplyEmailCb.getItemCount()>0 && replyEmailId!=null && !replyEmailId.isEmpty() && !(cReplyEmailCb.getValue().trim()).equals(replyEmailId))
            cReplyEmailCb.setValue(replyEmailId);

		enableSendingChkBxId.setChecked(digitalReceiptUserSettings.isEnabled());
		enableSMSSendingChkBxId.setChecked(digitalReceiptUserSettings.isSmsEnabled());
		shippingAmntChkBxId.setChecked(digitalReceiptUserSettings.isIncludeShipping());
		feeAmntChkBxId.setChecked(digitalReceiptUserSettings.isIncludeFee());
		taxAmntChkBxId.setChecked(digitalReceiptUserSettings.isIncludeTax());
		discAmntChkBxId.setChecked(digitalReceiptUserSettings.isIncludeGlobalDiscount());
		totalAmntChkBxId.setChecked(digitalReceiptUserSettings.isIncludeTotalAmount());


		frmEmaildynamicOrNotRgId.setSelectedIndex(digitalReceiptUserSettings.isIncludeDynamicFrmEmail() ? 1 : 0);
		frmNamedynamicOrNotRgId.setSelectedIndex(digitalReceiptUserSettings.isIncludeDynamicFrmName() ? 1 : 0);

		replyEmaildynamicOrNotRgId.setSelectedIndex(digitalReceiptUserSettings.isIncludeDynamicReplyToEmail() ? 1 : 0);

		
		// web page version
		cWebPageCb.setChecked(digitalReceiptUserSettings.isWebLinkFlag());
		if (digitalReceiptUserSettings.isWebLinkFlag()) {
			cWebLinkHboxId.setVisible(true);
			cWebLinkTextTb
			.setValue(digitalReceiptUserSettings.getWebLinkText());
			cWebLinkUrlTextTb.setValue(digitalReceiptUserSettings.getWebLinkUrlText());
		} else {
			cWebLinkHboxId.setVisible(false);

		}

		// permission reminder
		cPermRemRb.setSelectedIndex(digitalReceiptUserSettings.isPermissionRemainderFlag() ? 0 : 1);
		if (digitalReceiptUserSettings.isPermissionRemainderFlag()) {
			permRemDivId.setVisible(true);
			permRemTextId.setValue(digitalReceiptUserSettings.getPermissionRemainderText());
		} else {

			permRemDivId.setVisible(false);
		}

		// personalize to field
		toNameChkId.setChecked(digitalReceiptUserSettings.isPersonalizeTo());
		if (digitalReceiptUserSettings.isPersonalizeTo()) {
			persToDivId.setVisible(true);
			String to = digitalReceiptUserSettings.getToName();
			for (int i = 0; i < phArray.length; i++) {
				if (phArray[i].equalsIgnoreCase(to)) {
					phLbId.setSelectedIndex(i);
				}
			}
		} else {
			persToDivId.setVisible(false);

		}

	}// defaultDigiSettings()


	public void defaultDigiSettings() {


		List<UserFromEmailId> userFromEmailIdList = userFromEmailIdDao.getEmailsByUserId(currentUser.getUserId());
		logger.info("List size : " + userFromEmailIdList.size());

		Components.removeAllChildren(cFromEmailCb);
		Components.removeAllChildren(cReplyEmailCb);




		List<String> listOfEmailIds = new ArrayList<String>();

		for(UserFromEmailId anUserFromEmailId: userFromEmailIdList){
			if(anUserFromEmailId.getEmailId() != null){

				if( !listOfEmailIds.contains(anUserFromEmailId.getEmailId())){
					listOfEmailIds.add(anUserFromEmailId.getEmailId());
				}
			}
		}

		if (currentUser != null && currentUser.getEmailId() != null) {
			cFromEmailCb.appendItem(currentUser.getEmailId());
			cReplyEmailCb.appendItem(currentUser.getEmailId());
		}

		if (listOfEmailIds.size() > 0) {
			for (Object obj : listOfEmailIds) {
				String anEmailId = (String) obj;
				cFromEmailCb.appendItem(anEmailId);
				cReplyEmailCb.appendItem(anEmailId);


			}
		}


		/*if (userFromEmailIdList.size() > 0) {
			for (Object obj : userFromEmailIdList) {
				UserFromEmailId userFromEmailId = (UserFromEmailId) obj;
				cFromEmailCb.appendItem(userFromEmailId.getEmailId());

			}
		}*/

		if (!(cFromEmailCb.getItemCount() > 0)) {
			cFromEmailCb.appendItem("No emails registered.");

		}
		cFromEmailCb.setSelectedIndex(0);
		
		
		if (!(cReplyEmailCb.getItemCount() > 0)) {
			cReplyEmailCb.appendItem("No emails registered.");

		}
		cReplyEmailCb.setSelectedIndex(0);


		cFromNameTb.setValue(currentUser.getCompanyName());

		DigitalReceiptUserSettings digitalReceiptUserSettings = digitalReceiptUserSettingsDao.findByUserId(currentUser.getUserId());

		if(digitalReceiptUserSettings == null) {

			return;
		}

		// default  user subject 
		if(digitalReceiptUserSettings.getSubject() != null){
			cSubTb.setValue(digitalReceiptUserSettings.getSubject());
		}

		if(digitalReceiptUserSettings.getFromName() != null){
			cFromNameTb.setValue(digitalReceiptUserSettings.getFromName());
		}

		String fromEmailId = digitalReceiptUserSettings.getFromEmail();
		for (int index = 0; index < cFromEmailCb.getItemCount(); index++) {

			// logger.debug(cFromEmailCb.getItemAtIndex(index).getLabel() +
			// " == " + fromEmailId);
			if (cFromEmailCb.getItemAtIndex(index).getLabel().equals(fromEmailId)) {
				cFromEmailCb.setSelectedIndex(index);
			}
		}

		String replyEmailId = digitalReceiptUserSettings.getReplyToEmail();
		for (int index = 0; index < cReplyEmailCb.getItemCount(); index++) {

			// logger.debug(cReplyEmailCb.getItemAtIndex(index).getLabel() +
			// " == " + fromEmailId);
			if (cReplyEmailCb.getItemAtIndex(index).getLabel().equals(replyEmailId)) {
				cReplyEmailCb.setSelectedIndex(index);
			}
		}
		
		enableSendingChkBxId.setChecked(digitalReceiptUserSettings.isEnabled());
		enableSMSSendingChkBxId.setChecked(digitalReceiptUserSettings.isSmsEnabled());
		shippingAmntChkBxId.setChecked(digitalReceiptUserSettings.isIncludeShipping());
		feeAmntChkBxId.setChecked(digitalReceiptUserSettings.isIncludeFee());
		taxAmntChkBxId.setChecked(digitalReceiptUserSettings.isIncludeTax());
		discAmntChkBxId.setChecked(digitalReceiptUserSettings.isIncludeGlobalDiscount());
		totalAmntChkBxId.setChecked(digitalReceiptUserSettings.isIncludeTotalAmount());


		frmEmaildynamicOrNotRgId.setSelectedIndex(digitalReceiptUserSettings.isIncludeDynamicFrmEmail() ? 1 : 0);

		frmNamedynamicOrNotRgId.setSelectedIndex(digitalReceiptUserSettings.isIncludeDynamicFrmName() ? 1 : 0);

		replyEmaildynamicOrNotRgId.setSelectedIndex(digitalReceiptUserSettings.isIncludeDynamicReplyToEmail() ? 1 : 0);

		
		// web page version
		cWebPageCb.setChecked(digitalReceiptUserSettings.isWebLinkFlag());
		if (digitalReceiptUserSettings.isWebLinkFlag()) {
			cWebLinkHboxId.setVisible(true);
			cWebLinkTextTb
			.setValue(digitalReceiptUserSettings.getWebLinkText());
			cWebLinkUrlTextTb.setValue(digitalReceiptUserSettings.getWebLinkUrlText());
		} else {
			cWebLinkHboxId.setVisible(false);

		}
		// permission reminder
		cPermRemRb.setSelectedIndex(digitalReceiptUserSettings.isPermissionRemainderFlag() ? 0 : 1);
		if (digitalReceiptUserSettings.isPermissionRemainderFlag()) {
			permRemDivId.setVisible(true);
			permRemTextId.setValue(digitalReceiptUserSettings.getPermissionRemainderText());
		} else {

			permRemDivId.setVisible(false);
		}

		// personalize to field

		toNameChkId.setChecked(digitalReceiptUserSettings.isPersonalizeTo());
		if (digitalReceiptUserSettings.isPersonalizeTo()) {
			persToDivId.setVisible(true);
			String to = digitalReceiptUserSettings.getToName();
			for (int i = 0; i < phArray.length; i++) {
				if (phArray[i].equalsIgnoreCase(to)) {
					phLbId.setSelectedIndex(i);
				}
			}
		} else {
			persToDivId.setVisible(false);

		}

	}// defaultDigiSettings()



	public void defaultZoneDigiSettings() {

		DigitalReceiptUserSettings digitalReceiptUserSettings = digitalReceiptUserSettingsDao.findByUserId(currentUser.getUserId());
		if(digitalReceiptUserSettings != null) {
			enableSendingChkBxId.setChecked(digitalReceiptUserSettings.isEnabled());
			enableSMSSendingChkBxId.setChecked(digitalReceiptUserSettings.isSmsEnabled());
			shippingAmntChkBxId.setChecked(digitalReceiptUserSettings.isIncludeShipping());
			feeAmntChkBxId.setChecked(digitalReceiptUserSettings.isIncludeFee());
			taxAmntChkBxId.setChecked(digitalReceiptUserSettings.isIncludeTax());
			discAmntChkBxId.setChecked(digitalReceiptUserSettings.isIncludeGlobalDiscount());
			totalAmntChkBxId.setChecked(digitalReceiptUserSettings.isIncludeTotalAmount());

		}

	}
	
	
	public void getAllSettings() {
		int n = Integer.parseInt(settingsPerPageLBId.getSelectedItem().getLabel().trim());
		settingsPagingId.setTotalSize(digitalReceiptUserSettingsDao.getTotalCountBy(currentUser.getUserId()));
		settingsPagingId.setPageSize(n);
		settingsPagingId.setActivePage(0);
		
		settingsPagingId.addEventListener("onPaging", this);
		settingsPagingId.setAttribute("onPaging","eReceipt");
		getAllSettingReport( 0,settingsPagingId.getPageSize());
	}

	public void onSelect$settingsPerPageLBId(){
		int n = Integer.parseInt(settingsPerPageLBId.getSelectedItem().getLabel().trim());
		settingsPagingId.setPageSize(n);
		settingsPagingId.setActivePage(0);
		settingsPagingId.addEventListener("onPaging", this); 
		getAllSettingReport( settingsPagingId.getActivePage()*settingsPagingId.getPageSize(),n);
	}



	public void getAllSettingReport(int startIndx,int endIndx) {
		int count = tempsettingsLbId.getItemCount();
		for(; count>0;count--) {
			tempsettingsLbId.removeItemAt(count-1);
		}

		Listitem listItem=null;
		List<DigitalReceiptUserSettings> retObj=null;

		retObj=digitalReceiptUserSettingsDao.getTemplatesByUserId(currentUser.getUserId(),startIndx,endIndx);

		for (DigitalReceiptUserSettings digitalReceiptUserSettings : retObj) {
			logger.info(" inside loop  ::"+digitalReceiptUserSettings.getZoneId());
			if(digitalReceiptUserSettings.getZoneId() == null) continue;
			String zomeName="";
			OrganizationZone zone=	zoneDao.getSelectedZoneDetailsByZoneId(digitalReceiptUserSettings.getZoneId());
			if(zone==null) continue;
			zomeName=zone.getZoneName();
			logger.info(" zone Name :"+zomeName);
			listItem = new Listitem();
			/*Listcell cell = new Listcell(DomainNameMap.get(digitalReceiptUserSettings.getSubAccountId()));
			cell.setParent(listItem);
				cell = new Listcell(zomeName);
			cell.setParent(listItem);*/

			Listcell cell = new Listcell(zomeName);
			cell.setParent(listItem);


			List<OrganizationStores> storeList = null;
			String sbsub = null;
			Map<String, String> subAndStoreMap = new HashMap<String, String>();
			Map<String ,String> subsNameMap=new HashMap<>();
			StringBuilder sbStore = new StringBuilder();
			StringBuilder group=new StringBuilder();
			storeList = zoneDao.getGroupOfDetailByZone(zone.getZoneId());
			if (storeList != null)
				for (OrganizationStores stores : storeList) {
					logger.info("store Name  " + stores.getStoreName());
					String subName=sbsub=stores.getSubsidiaryName() != null ? stores.getSubsidiaryName() : "";
					String store=stores.getStoreName() != null ?  stores.getStoreName()  : "";
					String subId=stores.getSubsidiaryId() != null ? stores.getSubsidiaryId() : "";
					subsNameMap.put(subId, subName);
					if(subAndStoreMap.containsKey(subId))
					{	
						String tempst=subAndStoreMap.get(subId);
						logger.info("temp Store "+tempst);
						sbStore.replace(0, tempst.length(), "");
						sbStore.append(tempst+" , ");
						sbStore.append(store);
						logger.info("Store "+store);
						subAndStoreMap.put(subId,sbStore.toString());

					}
					else
					{ 
						subAndStoreMap.put(subId,store.toString());
					}

				}
			StringBuilder subandStores =new StringBuilder();
			Set<String> subkey= subAndStoreMap.keySet();
			for (String subsidiary : subkey) {
				String stores =subAndStoreMap.get(subsidiary);
				if(StringUtils.isBlank(subandStores.toString())){
					subandStores.append("Subsidiary : "+subsNameMap.get(subsidiary)+" ; "+" "+"Stores : "+stores+" , ");
				}
				else{
					subandStores.append("Subsidiary : "+subsNameMap.get(subsidiary)+" ; "+" "+"Stores : "+stores+" , ");

				}

			}
			group.append(subandStores.toString());

			StringBuilder newGroup=	group.deleteCharAt(group.length()-2);



			cell = new Listcell(newGroup.toString());
			cell.setParent(listItem);

			Long selectedTemplateID = digitalReceiptUserSettings.getMyTemplateId();
			//DigitalReceiptMyTemplate drMyTemp= digiRecptMyTemplatesDao.findByOrgIdAndTemplateId(currentUser.getUserOrganization().getUserOrgId(), selectedTemplateID);
			DigitalReceiptMyTemplate drMyTemp= digiRecptMyTemplatesDao.findByUserId_TemplateId(currentUser.getUserId(), selectedTemplateID);

			if(drMyTemp==null) continue;

			String templateName = drMyTemp.getName();
			//if(templateName.startsWith(MY_TEMPLATE)) templateName = templateName.substring(MY_TEMPLATE.length());
			cell = new Listcell(templateName);
			cell.setParent(listItem);

			cell = new Listcell(digitalReceiptUserSettings.getSubject() != null ? digitalReceiptUserSettings.getSubject() : "--");
			cell.setTooltiptext(digitalReceiptUserSettings.getSubject() != null ? digitalReceiptUserSettings.getSubject() : "--");
			cell.setParent(listItem);

			cell = new Listcell(digitalReceiptUserSettings.getFromName() != null ? digitalReceiptUserSettings.getFromName() : "--");
			cell.setTooltiptext(digitalReceiptUserSettings.getFromName() != null ? digitalReceiptUserSettings.getFromName() : "--");
			cell.setParent(listItem);

			cell = new Listcell(digitalReceiptUserSettings.getFromEmail() != null ? digitalReceiptUserSettings.getFromEmail() : "--");
			cell.setTooltiptext(digitalReceiptUserSettings.getFromEmail() != null ? digitalReceiptUserSettings.getFromEmail() : "--");
			cell.setParent(listItem);

			cell = new Listcell(digitalReceiptUserSettings.getReplyToEmail() != null ? digitalReceiptUserSettings.getReplyToEmail() : "--");
			cell.setTooltiptext(digitalReceiptUserSettings.getReplyToEmail() != null ? digitalReceiptUserSettings.getReplyToEmail() : "--");
			cell.setParent(listItem);
			 

			Image img ;
			Hbox hbox = null;
			hbox=new Hbox();

			if(digitalReceiptUserSettings.isSettingEnable()){
				img= new Image("/img/pause_icn.png");
				img.setStyle("margin-right:5px;cursor:pointer;");
				img.setTooltiptext("Pause Settings");
				img.setAttribute("imageEventName", "inactive");
				img.setAttribute("DigitalReceiptUserSettings", digitalReceiptUserSettings);
				img.setAttribute("type", "ChangeSetting");
				img.addEventListener("onClick",this);
				Space space = new Space();
				space.setParent(hbox);
				img.setParent(hbox);
			}
			else {
				hbox=new Hbox();
				img= new Image("/img/play_icn.png");
				img.setStyle("margin-right:5px;cursor:pointer;");
				img.setTooltiptext("Activate Settings");
				img.setAttribute("imageEventName", "active");
				img.setAttribute("DigitalReceiptUserSettings", digitalReceiptUserSettings);
				img.setAttribute("type", "ChangeSetting");
				img.addEventListener("onClick",this);
				Space space = new Space();
				space.setParent(hbox);
				img.setParent(hbox);

			}
			img = new Image("/img/theme/preview_icon.png");
			img.setTooltiptext("Preview");
			img.setStyle("cursor:pointer;margin-right:5px;");
			img.addEventListener("onClick", this);
			img.setAttribute("type", "PreviewTemplate");
			img.setAttribute("DigitalReceiptUserSettings", digitalReceiptUserSettings);
			Space space = new Space();
			space.setParent(hbox);
			img.setParent(hbox);
			
			img = new Image("/img/theme/action_delete.gif");
			img.setTooltiptext("Delete");
			img.setStyle("cursor:pointer;margin-right:5px;");
			img.addEventListener("onClick", this);
			img.setAttribute("type", "deleteSettings");
			img.setAttribute("DigitalReceiptUserSettings", digitalReceiptUserSettings);
			space = new Space();
			space.setParent(hbox);
			img.setParent(hbox);
			

			cell = new Listcell();
			hbox.setParent(cell);
			cell.setParent(listItem);


			listItem.setValue(digitalReceiptUserSettings);
			listItem.setParent(tempsettingsLbId);

		}



	}


	public void onClick$manageStoresLink1AnchId() {
		String redirectToStr = PageListEnum.USERADMIN_ORG_STORES.getPagePath();
		
		PageUtil.setFromPage(PageListEnum.CAMPAIGN_DIGITAL_RECEIPTS.getPagePath());
		Redirect.goTo(redirectToStr);
	}
	
	public void onClick$manageStoresLink2AnchId() {
		onClick$manageStoresLink1AnchId();
	}
	
	public void onClick$manageStoresLinkReplyAnchId() {
		onClick$manageStoresLink1AnchId();
	}

	private Window previewWin;
	private Html previewWin$html;

	@Override
	public void onEvent(Event event) throws Exception {
		// TODO Auto-generated method stub
		super.onEvent(event);

		if (event.getTarget() instanceof Listitem) {
			Listitem li = (Listitem) event.getTarget();
           
			//setEditorContent(li);
		}
		if (event.getTarget() instanceof Image) {
			logger.info("img clicked ...");
			Image img = (Image) event.getTarget();
			if (img.getAttribute("type") != null) {
			Listitem list=(Listitem) img.getParent().getParent().getParent();
			DigitalReceiptUserSettings digitalReceiptUserSettings=list.getValue();
			if(img.getAttribute("type").equals("ChangeSetting")){
			if(digitalReceiptUserSettings.isSettingEnable()){
			
				int confirm = Messagebox.show("Are you sure you want to change the setting?", "Prompt", 
						 Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
				if(confirm != 1) return;
				digitalReceiptUserSettings.setSettingEnable(false);
				digitalReceiptUserSettingsDaoForDML.saveOrUpdate(digitalReceiptUserSettings);
				img.setSrc("/img/play_icn.png");
				img.setTooltiptext("Activate Settings");
				
			}
			else{
				int confirm = Messagebox.show("Are you sure you want to change the setting?", "Prompt", 
						 Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
				if(confirm != 1) return;
			
				digitalReceiptUserSettings.setSettingEnable(true);
				digitalReceiptUserSettingsDaoForDML.saveOrUpdate(digitalReceiptUserSettings);
				img.setSrc("/img/pause_icn.png");
				img.setTooltiptext("Pause Settings");
			 }
			}else if(img.getAttribute("type").equals("deleteSettings")){
					//if(digitalReceiptUserSettings.isSettingEnable()){
					
						int confirm = Messagebox.show("Are you sure you want to delete the setting?", "Prompt", 
								 Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
						if(confirm != 1) return;
						
								digitalReceiptUserSettingsDaoForDML.delete(digitalReceiptUserSettings);

								
		   	   				MessageUtil.setMessage("Digital Receipt Setting deleted sucessfully.","color:blue", "TOP");
		   	   			getAllSettings();

						
						
					}
			else{

				DigitalReceiptMyTemplatesDao digitalReceiptMyTemplatesDao = (DigitalReceiptMyTemplatesDao)ServiceLocator.getInstance().getDAOByName(OCConstants.DIGITAL_RECEIPT_MYTEMPLATES_DAO);
				DigitalReceiptMyTemplate digitalReceiptMyTemplates= digitalReceiptMyTemplatesDao.findByUserId_TemplateId(currentUser.getUserId(), digitalReceiptUserSettings.getMyTemplateId());
				
				previewWin$html.setContent(digitalReceiptMyTemplates.getContent());
				previewWin.setVisible(true);
			}
			}
		}
		else if(event.getTarget() instanceof Paging) {
			/*String fromDate = MyCalendar.calendarToString(getStartDate(), MyCalendar.FORMAT_DATETIME_STYEAR);
			String endDate = MyCalendar.calendarToString(getEndDate(), MyCalendar.FORMAT_DATETIME_STYEAR);
			String statusStr = statusLbId.getSelectedItem().getLabel();*/
			Paging paging = (Paging) event.getTarget();
			int desiredPage = paging.getActivePage();
			PagingEvent pagingEvent = (PagingEvent) event;
			int pSize = pagingEvent.getPageable().getPageSize();
			int ofs = desiredPage * pSize;
			
			if(paging.getAttribute("onPaging").equals("eReceipt")) {
				this.settingsPagingId.setActivePage(desiredPage);
				getAllSettingReport(ofs, (byte) pagingEvent.getPageable().getPageSize());
				
			}
		}
		
		/*if (event.getTarget() instanceof Toolbarbutton) {
			Listitem li = (Listitem) ((Toolbarbutton) event.getTarget())
					.getParent().getParent();
			EmailQueue emailQueue = (EmailQueue) li
					.getAttribute("digiRcptEmailQueueObj");
			if (emailQueue != null) {
				String emailContent = emailQueue.getMessage();
				previewWin$html.setContent(emailContent);
				previewWin.setVisible(true);
			}
		}*/

	}


	public void onClick$regFrmEmlAnchId() {

		regEmailPopupId.setAttribute("flagStr", "From-Email");
	}

	public void onClick$submitBt1nId() {
		try {
			
			if(frmEmaildynamicOrNotRgId.getSelectedIndex() == 0){
				registerNewFromEmail(cFromEmailTb,(String)regEmailPopupId.getAttribute("flagStr"));
			}
			
			
		} catch (Exception e) {
			
			logger.error("Exception ::", e);
		}
	}
	
	
	public void onClick$cancelBtnId() {

		regEmailPopupId.close();

	}
	

	
	public void onClick$regReplyEmlAnchId() {

		regReplyToEmailPopupId.setAttribute("flagStr", "Reply To-Email");
	}

	public void onClick$submitReplyBtnId() {
		try {
			
			if(frmEmaildynamicOrNotRgId.getSelectedIndex() == 0){
				registerNewFromEmail(cReplyEmailTb,(String)regReplyToEmailPopupId.getAttribute("flagStr"));
			}
			
			
		} catch (Exception e) {
			
			logger.error("Exception ::", e);
		}
	}
	
	
	public void onClick$cancelReplyBtnId() {

		regReplyToEmailPopupId.close();

	}
	
	
	
	
	
	public void registerNewFromEmail(Textbox cEmailTb,String flagStr) throws Exception {
		String newFromEmail = cEmailTb.getValue();
		
		if(newFromEmail.trim().equals("")){
			regEmailPopupId.close();
			regReplyToEmailPopupId.close();
			MessageUtil.setMessage("Email field cannot be left empty.", "color:red", "TOP");
			return;
		}
		
		if(!Utility.validateEmail(newFromEmail.trim())) {
			regEmailPopupId.close();
			regReplyToEmailPopupId.close();
			MessageUtil.setMessage("Please enter a valid email address.", "color:red", "TOP");
			cEmailTb.setValue("");
			return;
	 	}
		
		try {
			if(newFromEmail.equalsIgnoreCase(currentUser.getEmailId())) {
				MessageUtil.setMessage("Given email address already exists.", "color:red", "TOP");
				cEmailTb.setValue("");
				return;
			}
			UserFromEmailIdDao userFromEmailIdDao = (UserFromEmailIdDao)SpringUtil.getBean("userFromEmailIdDao");
			UserFromEmailIdDaoForDML userFromEmailIdDaoForDML = (UserFromEmailIdDaoForDML)SpringUtil.getBean("userFromEmailIdDaoForDML");
			EmailQueueDao emailQueueDao = (EmailQueueDao)SpringUtil.getBean("emailQueueDao");
			UserFromEmailId userFromEmailId =  userFromEmailIdDao.checkEmailId(newFromEmail, currentUser.getUserId());
			String confirmationURL = PropertyUtil.getPropertyValue("confirmationURL") + "?requestedAction=fromEmail&userId=" + currentUser.getUserId() + "&email=" + newFromEmail;
			if(userFromEmailId == null) {
				userFromEmailId = new UserFromEmailId(currentUser, newFromEmail, 0);
				//userFromEmailIdDao.saveOrUpdate(userFromEmailId);
				userFromEmailIdDaoForDML.saveOrUpdate(userFromEmailId);

				addVerificationMailToQueue(flagStr, confirmationURL, newFromEmail);
				
				/*String emailStr = "Hi " + user.getFirstName() + ",<br/> You recently added a new email address  to your "+flagStr+".<br/>" 
						+ " To verify that you own this email address, simply click on the link below :<br/> " 
						+ "  <a href='"+ confirmationURL +"'>"+ confirmationURL + "</a>" 
						+ "<br/><br/> If you can't click the link, you can verify your email address by copy/paste(or typing) "
						+ "the above URL address into the browser.<br/>Regards,<br/>The Captiway Team";
				EmailQueue emailQueue = new EmailQueue("Captiway - Register new "+flagStr,emailStr, Constants.EQ_TYPE_USER_MAIL_VERIFY,"Active",newFromEmail,MyCalendar.getNewCalendar(),user);
			 	emailQueueDao.saveOrUpdate(emailQueue);
				regEmailPopupId.close();
				MessageUtil.clearMessage();
				Messagebox.show("We have sent you a confirmation email to "+newFromEmail+".\n Follow the instructions in the email and this new "+flagStr+" will be verified.");
				cEmailTb.setValue("");*/
			} else if(userFromEmailId.getStatus() == 0) {
				
				//MessageUtil.setMessage("Given email ID approval is pending.", "color:red", "TOP");
				 try {
					int confirm = Messagebox.show("The given email address is pending for approval . Do you want to resend the verification?","Send Verification ?",
						 		Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
					if(confirm != 1) {
						regEmailPopupId.close();
						regReplyToEmailPopupId.close();
						cEmailTb.setValue("");
						return ;
					}
				} catch (Exception e) {
					logger.error("Exception ::", e);
				}
				
				addVerificationMailToQueue(flagStr, confirmationURL, newFromEmail);
				
				/*String emailStr = "Hi " + user.getFirstName() + ",<br/> You recently added a new email address  to your "+flagStr+".<br/>" 
				+ " To verify that you own this email address, simply click on the link below :<br/> " 
				+ "  <a href='"+ confirmationURL +"'>"+ confirmationURL + "</a>" 
				+ "<br/><br/> If you can't click the link, you can verify your email address by copy/paste(or typing) "
				+ "the above URL address into the browser.<br/>Regards,<br/>The Captiway Team";
				EmailQueue emailQueue = new EmailQueue("Captiway - Register new "+flagStr,emailStr, Constants.EQ_TYPE_USER_MAIL_VERIFY,"Active",newFromEmail,MyCalendar.getNewCalendar(),user);
			 	emailQueueDao.saveOrUpdate(emailQueue);
				regEmailPopupId.close();
				MessageUtil.clearMessage();
				Messagebox.show("We have sent you a confirmation email to "+newFromEmail+".\n Follow the instructions in the email and this new "+flagStr+" will be verified.");
				cEmailTb.setValue("");
				*/
			} 
			else {
				regEmailPopupId.close();
				regReplyToEmailPopupId.close();
				MessageUtil.setMessage("Given email address already exists.", "color:red", "TOP");
				cEmailTb.setValue("");
			}
		} catch (Exception e) {
			regEmailPopupId.close();
			regReplyToEmailPopupId.close();
			//logger.error("Exception :"+e);
		}
	}// registerNewFromEmail()
	
	private void addVerificationMailToQueue(String flagStr, String confirmationURL, String newFromEmail) {
		
		try {
			String branding = GetUser.getUserObj().getUserOrganization().getBranding();

			String brandStr="OptCulture";
			if(branding!=null && branding.equalsIgnoreCase("CAP")) {
				brandStr="Captiway";
			}
			
			String emailStr = "Hi " + currentUser.getFirstName() + ",<br/> You recently added a new email address  to your "+flagStr+".<br/>" 
			+ " To verify that you own this email address, simply click on the link below :<br/> " 
			+ "  <a href='"+ confirmationURL +"'>"+ confirmationURL + "</a>" 
			+ "<br/><br/> If you can't click the link, you can verify your email address by copy/paste(or typing) "
			+ "the above URL address into the browser.<br/>Regards,<br/>The "+brandStr+" Team";
			
			
			
			Utility.sendInstantMail(null,brandStr+" - Register new "+flagStr, emailStr,
					Constants.EQ_TYPE_USER_MAIL_VERIFY,newFromEmail, null);
			/*EmailQueue emailQueue = new EmailQueue(brandStr+" - Register new "+flagStr,emailStr, Constants.EQ_TYPE_USER_MAIL_VERIFY,"Active",newFromEmail,MyCalendar.getNewCalendar(),currentUser);
			emailQueueDao.saveOrUpdate(emailQueue);*/
			regEmailPopupId.close();
			regReplyToEmailPopupId.close();
			MessageUtil.clearMessage();
			Messagebox.show("We have sent you a confirmation email to "+newFromEmail+".\n " +
					"Follow the instructions in the email and this new "+flagStr+" will be verified.", "Register email", Messagebox.OK, Messagebox.INFORMATION);
			cFromEmailTb.setValue("");
			cReplyEmailTb.setValue("");

		} catch (WrongValueException e) {
			logger.error("Exception ::", e);
		}catch (Exception e) {

	//	logger.error("** Exception while sending verification",e);
		} 
		
	}


	/*public void defaultDigiSetting(DigitalReceiptUserSettings digitalReceiptUserSettings) {


		//DigitalReceiptUserSettings digitalReceiptUserSettings = digitalReceiptUserSettingsDao.findByUserId(currentUser.getUserId());
		logger.debug("====entered defaultDigiSettings===="+digitalReceiptUserSettings);
		if(digitalReceiptUserSettings == null) {

			return;
		}
		tmepsaveBtnId.setAttribute(STORE_WISE_DR_SETTINGS_OBJ, digitalReceiptUserSettings);
		// default  user subject 
		if(digitalReceiptUserSettings.getSubject() != null){

			logger.debug("====sub==="+digitalReceiptUserSettings.getSubject());
			cSubTb.setValue(digitalReceiptUserSettings.getSubject());
		}

		if(digitalReceiptUserSettings.getFromName() != null){
			logger.debug("====getFromName ==="+digitalReceiptUserSettings.getFromName());
			cFromNameTb.setValue(digitalReceiptUserSettings.getFromName());
		}

		String fromEmailId = digitalReceiptUserSettings.getFromEmail();
		boolean isfromEamil = false;
		boolean isToEamil = false;
		for (int index = 0; index < cFromEmailCb.getItemCount(); index++) {
			if (cFromEmailCb.getItemAtIndex(index).getLabel().equals(fromEmailId)) {
				cFromEmailCb.setSelectedIndex(index);
			}
			else{
				if(!isfromEamil){
				cFromEmailCb.appendItem(fromEmailId);
				cFromEmailCb.setSelectedIndex(index);
				isfromEamil=true;
				}
			}
		}
		if(cFromEmailCb.getItemCount()==0){
			cFromEmailCb.appendItem(fromEmailId);
			cFromEmailCb.setSelectedIndex(0);
		}
		String replayEmailId = digitalReceiptUserSettings.getReplytoemail();
		for (int index = 0; index < cRepEmailCb.getItemCount(); index++) {


			if (cRepEmailCb.getItemAtIndex(index).getLabel().equals(replayEmailId)) {
				cRepEmailCb.setSelectedIndex(index);
			}
			else{
				if(!isToEamil){
					cRepEmailCb.appendItem(replayEmailId);
					cRepEmailCb.setSelectedIndex(index);
				isToEamil=true;
				}
			}
		}
           if(cRepEmailCb.getItemCount()==0){
        	   cRepEmailCb.appendItem(replayEmailId);
        	   cRepEmailCb.setSelectedIndex(0); 
           }
		shippingAmntChkBxId.setChecked(digitalReceiptUserSettings.isIncludeShipping());
		feeAmntChkBxId.setChecked(digitalReceiptUserSettings.isIncludeFee());
		taxAmntChkBxId.setChecked(digitalReceiptUserSettings.isIncludeTax());
		discAmntChkBxId.setChecked(digitalReceiptUserSettings.isIncludeGlobalDiscount());


		// web page version
		cWebPageCb.setChecked(digitalReceiptUserSettings.isWebLinkFlag());
		if (digitalReceiptUserSettings.isWebLinkFlag()) {
			cWebLinkHboxId.setVisible(true);
			cWebLinkTextTb
			.setValue(digitalReceiptUserSettings.getWebLinkText());
			cWebLinkUrlTextTb.setValue(digitalReceiptUserSettings.getWebLinkUrlText());
		} else {
			cWebLinkHboxId.setVisible(false);

		}
		// permission reminder
		if (digitalReceiptUserSettings.isPermissionRemainderFlag()) {
			permRemDivId.setVisible(true);
			permRemTextId.setValue(digitalReceiptUserSettings.getPermissionRemainderText());
		} else {

			permRemDivId.setVisible(false);
		}

		// personalize to field

		toNameChkId.setChecked(digitalReceiptUserSettings.isPersonalizeTo());
		if (digitalReceiptUserSettings.isPersonalizeTo()) {
			persToDivId.setVisible(true);
			String to = digitalReceiptUserSettings.getToName();
			for (int i = 0; i < phArray.length; i++) {
				if (phArray[i].equalsIgnoreCase(to)) {
					phLbId.setSelectedIndex(i);
				}
			}
		} else {
			persToDivId.setVisible(false);

		}

	}// defaultDigiSettings()
	 */	










	/*	public void onClick$tmepsaveBtnId() {
		try {

			if(orgUnitlbId.getSelectedIndex() == 0 ) {

				MessageUtil.setMessage("Please Select Account. ", "color:red");
				return;
			}

			if(dispZoneLBoxId.getSelectedIndex() == 0 ) {

				MessageUtil.setMessage("Please Select Zone. ", "color:red");
				return;
			}
			if(mytemplatesLbId.getSelectedIndex()== 0) {

				MessageUtil.setMessage("Please Select a template. ", "color:red");
				return;
			}

			DigitalReceiptUserSettings digitalReceiptUserSettings  = null;
			DigitalReceiptMyTemplate d1= (DigitalReceiptMyTemplate)mytemplatesLbId.getSelectedItem().getValue();
			if(d1==null)return;
			if(tmepsaveBtnId.getAttribute(STORE_WISE_DR_SETTINGS_OBJ) != null) {
				digitalReceiptUserSettings = (DigitalReceiptUserSettings)tmepsaveBtnId.getAttribute(STORE_WISE_DR_SETTINGS_OBJ);
				digitalReceiptUserSettings.setSelectedTemplateName(MY_TEMPLATE+d1.getName());
				digitalReceiptUserSettings.setMyTemplateId(d1.getMyTemplateId());
				//digitalReceiptUserSettings.setUserId(currentUser.getUserId());
				logger.debug("=====edit========"+digitalReceiptUserSettings);
			}else{
				logger.debug("=====new========");

				digitalReceiptUserSettings = new DigitalReceiptUserSettings();
				digitalReceiptUserSettings.setUserId(currentUser.getUserId());
				digitalReceiptUserSettings.setAccount(currentUser.getAccount());
				digitalReceiptUserSettings.setSubAccountId(((Account)orgUnitlbId.getSelectedItem().getValue()).getAccountId());
				digitalReceiptUserSettings.setSelectedTemplateName(MY_TEMPLATE+d1.getName());
				digitalReceiptUserSettings.setMyTemplateId(d1.getMyTemplateId());
				//retObj.setStoreNo(storeNo);
				//digitalReceiptUserSettings.setSBSNo(((OrganizationStores)dispSBSLBoxId.getSelectedItem().getValue()).getSubsidiaryId());
				digitalReceiptUserSettings.setZoneId(((OrganizationZone)dispZoneLBoxId.getSelectedItem().getValue()).getZoneId());

				//digitalReceiptUserSettingsDaoForDML.saveOrUpdate(digitalReceiptUserSettings);
				//MessageUtil.setMessage(".", "color:red", "TOP");
			}


			//usersDaoForDML.updateEnableDrSending(enableSendingChkBxId.isChecked(), currentUser.getUserOrganization().getUserOrgId());

			GetUser.reloadUserIntoSession();
			logger.info("======================saving details");
			// check for subject and it is valid or not 
			if(cSubTb.isValid() && !cSubTb.getValue().isEmpty()){

				digitalReceiptUserSettings.setSubject(cSubTb.getValue());
			}
			else{
				MessageUtil.setMessage("Please provide subject. Subject should not be left empty.", "color:red", "TOP");
				cSubTb.setFocus(true);
				return;
			}
			logger.info("======================saving details");
			//  check for from name n it is valid or not
			if( cFromNameTb.isValid() &&  Utility.validateFromName(cFromNameTb.getValue())){
					digitalReceiptUserSettings.setFromName(cFromNameTb.getValue());
			}
			else{
				MessageUtil.setMessage("Provide valid 'From Name'. Special characters are not allowed.", "color:red", "TOP");
				cFromNameTb.setFocus(true);
				return;
			}

			// check for from email address n display all the fromEmails n those fromEmails are valid or not 
			logger.info("======================saving details");

			if(cFromEmailCb.isVisible() && cFromEmailCb.getSelectedItem().getLabel().indexOf('@') < 0) {
				MessageUtil.setMessage("Register a 'From Email' to create an email.","color:red", "TOP");
				return;
			}
			else if(!(cFromEmailCb.getSelectedIndex()==-1)) {
					digitalReceiptUserSettings.setFromEmail(cFromEmailCb.getSelectedItem().getLabel());
			}
			else {
				MessageUtil.setMessage("Provide valid 'From Email'.", "color:red", "TOP");
				cFromEmailCb.setFocus(true);
				return;
			}
			logger.info("======================saving details");
			//check for from email address n display all the fromEmails n those fromEmails are valid or not
			if(cRepEmailCb.getSelectedItem().getLabel().indexOf('@') < 0) {
				MessageUtil.setMessage("Register a 'Replay Email' to create an email.","color:red", "TOP");
				return;
			}
			else if(!(cRepEmailCb.getSelectedIndex()==-1)) {
				digitalReceiptUserSettings.setReplytoemail(cRepEmailCb.getSelectedItem().getLabel());

			}



			logger.info("======================saving details");

			//digitalReceiptUserSettings.setEnabled(enableSendingChkBxId.isChecked());
			//currentUser.setEnableDRSending(true);
			digitalReceiptUserSettings.setIncludeShipping(shippingAmntChkBxId.isChecked());
			digitalReceiptUserSettings.setIncludeFee(feeAmntChkBxId.isChecked());
			digitalReceiptUserSettings.setIncludeTax(taxAmntChkBxId.isChecked());
			digitalReceiptUserSettings.setIncludeGlobalDiscount(discAmntChkBxId.isChecked());

			// ***** optional settings **************

			//web page version 
			logger.info("======================saving details");
			if(cWebPageCb.isChecked()){
				digitalReceiptUserSettings.setWebLinkFlag(true);
				digitalReceiptUserSettings.setWebLinkText(cWebLinkTextTb.getValue());
				if(!cWebLinkUrlTextTb.getValue().equals("")){
					digitalReceiptUserSettings.setWebLinkUrlText(cWebLinkUrlTextTb.getValue());
				}else{
					MessageUtil.setMessage("Provide web-link Url text.", "color:red", "TOP");
					cWebLinkUrlTextTb.setFocus(true);
					return;
				}
			}else{
				digitalReceiptUserSettings.setWebLinkFlag(false);
			}
			logger.info("======================saving details");
			//permission reminder 


				if(!permRemTextId.getValue().trim().equals("")){
					digitalReceiptUserSettings.setPermissionRemainderFlag(true);
					digitalReceiptUserSettings.setPermissionRemainderText(permRemTextId.getValue());
				}
			else{
				digitalReceiptUserSettings.setPermissionRemainderFlag(false);
			}

			//personalize to field
			logger.info("======================saving details");
			if(toNameChkId.isChecked()){
				digitalReceiptUserSettings.setPersonalizeTo(true);
				if(phLbId.getSelectedIndex()>0){
					digitalReceiptUserSettings.setToName((String)phLbId.getSelectedItem().getValue());
				}else{
					phLbId.setSelectedIndex(0);
					digitalReceiptUserSettings.setToName((String)phLbId.getSelectedItem().getValue());
				}
			}else{
				digitalReceiptUserSettings.setPersonalizeTo(false);
			}
			//MessageUtil.clearMessage();




			digitalReceiptUserSettings.setSubject(cSubTb.getValue().trim());

				digitalReceiptUserSettings.setFromName(cFromNameTb.getValue().trim());
			digitalReceiptUserSettings.setCreatedDate(Calendar.getInstance());



			//Set The Template Name
			if(digitalReceiptUserSettings.getSelectedTemplateName() == null) {
				logger.info("get selectedtemplatename"+digitalReceiptUserSettings.getSelectedTemplateName());
				String selTemplateName = ((String)selectedTemplateLblId.getAttribute("templateName")).trim();
				logger.info(" templatename"+selTemplateName);
				if(selTemplateName.equals(DEFAULT_TEMPLATE_NAME)){

					digitalReceiptUserSettings.setSelectedTemplateName(SYSTEM_TEMPLATE+digitalTemplatesListId.getSelectedItem().getValue());

				}
				else{

					digitalReceiptUserSettings.setSelectedTemplateName(selTemplateName);
				}
			}
			try {
				int confirm = Messagebox.show(
						"Are you sure you want to save the digital receipt settings?", "Prompt",
						Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);

				if (confirm == Messagebox.OK) {

					try {
						logger.info("======================saving details");
						//digitalReceiptUserSettingsDao.saveOrUpdate(digitalReceiptUserSettings);
						logger.info("*************************save user Id"+digitalReceiptUserSettings.getUserId());
						digitalReceiptUserSettings.setSettingEnable(true);
						digitalReceiptUserSettingsDaoForDML.saveOrUpdate(digitalReceiptUserSettings);
						MessageUtil.setMessage("Digital receipt settings saved successfully.","color:green;","TOP");
						tmepsaveBtnId.removeAttribute(STORE_WISE_DR_SETTINGS_OBJ);
						Redirect.goTo(PageListEnum.EMPTY);
						Redirect.goTo(PageListEnum.CAMPAIGN_DIGITAL_RECEIPTS);
					}catch(Exception e){

						logger.error("Exception ::", e);
					}
				}
			}catch(Exception e1){
				logger.error("Exception ::", e1);
			}
		} catch (Exception e) {

			logger.error("Exception ::", e);
		}
	}*/



	public void setUserOrgUnit(UsersDomains userdomain) {
		try {
			for(int i = orgUnitlbId.getItemCount() ; i>1 ; i--) {
				orgUnitlbId.removeItemAt(i);
			}

			if (userdomain != null) {
				Listitem templist = new Listitem(userdomain.getDomainName(), userdomain);
				// templist.setValue(orgunit);
				templist.setParent(orgUnitlbId);
			}
			orgUnitlbId.setSelectedIndex(0);
			onSelect$orgUnitlbId();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("exception in set OrgUnit", e);
		}
	}

	/*	public void setUserOrgUnit() {
		try {
			for(int i = orgUnitlbId.getItemCount() ; i>1 ; i--) {
				orgUnitlbId.removeItemAt(i);
			}
			List<UsersDomains> orgList = userDomainDao.FindByOrgId(currentUser.getUserOrganization().getUserOrgId());
			//List<UsersDomains> orgList = usersDao.getAllDomainsByUser(currentUser.getUserId());
			if (orgList == null) {
				logger.debug("no organization list exist in the DB...");
				return;
			}
			if (orgList != null && orgList.size() > 0) {
				for (UsersDomains orgunit : orgList) {
					Listitem templist = new Listitem(orgunit.getDomainName(), orgunit);
					// templist.setValue(orgunit);
					templist.setParent(orgUnitlbId);
				}
			  }
			orgUnitlbId.setSelectedIndex(0);
				onSelect$orgUnitlbId();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("exception in set OrgUnit", e);
		}
	}*/


	public void onSelect$orgUnitlbId() {
		if(orgUnitlbId.getSelectedIndex() == 0){
			dispZoneLBoxId.setSelectedIndex(0);
			mytemplatesLbId.setSelectedIndex(0);
			int dispZoneCount = dispZoneLBoxId.getItemCount();
			if( dispZoneCount> 1) {
				for(;dispZoneCount>1;dispZoneCount--) {
					dispZoneLBoxId.removeItemAt(dispZoneCount-1);
				}
			}
			int mytemplatesCount = mytemplatesLbId.getItemCount();
			for (; mytemplatesCount > 1; mytemplatesCount--) {
				mytemplatesLbId.removeItemAt(mytemplatesCount - 1);
			}
			return;
		}

		UsersDomains usersDomains = (UsersDomains)orgUnitlbId.getSelectedItem().getValue();
		List<OrganizationZone> liszones = zoneDao.getZoneStoresBydomainId(""+usersDomains.getDomainId());

		Listitem listItem = null;
		/*int count=dispZoneLBoxId.getItemCount(); 
		if( count> 1) {
			for(;count>1;count--) {
				dispZoneLBoxId.removeItemAt(count-1);
			}
		}*/
		if(liszones != null){
			for (OrganizationZone zone : liszones) {
				listItem = new Listitem(zone.getZoneName());
				listItem.setValue(zone);
				listItem.setParent(dispZoneLBoxId);
			}
		}
		if(dispZoneLBoxId.getItemCount()>1){
			dispZoneLBoxId.setSelectedIndex(0);
			setFolders();
		}
		//TODO 
		//defaultDigiSettings(account);
	}





	/*	public void onSelect$orgUnitlbId() {
		if(orgUnitlbId.getSelectedIndex() == 0){
			dispZoneLBoxId.setSelectedIndex(0);
			mytemplatesLbId.setSelectedIndex(0);
			int dispZoneCount = dispZoneLBoxId.getItemCount();
			if( dispZoneCount> 1) {
				for(;dispZoneCount>1;dispZoneCount--) {
					dispZoneLBoxId.removeItemAt(dispZoneCount-1);
				}
			}
			int mytemplatesCount = mytemplatesLbId.getItemCount();
			for (; mytemplatesCount > 1; mytemplatesCount--) {
				mytemplatesLbId.removeItemAt(mytemplatesCount - 1);
			}
			return;
		}

		UsersDomains usersDomains = (UsersDomains)orgUnitlbId.getSelectedItem().getValue();
		String orgUnitsIds = usersDomains.getDomainId().longValue()+Constants.STRING_NILL;
		//List<OrganizationZone> liszones = zoneDao.getAllZoneBydomainId(accId);
		List<OrganizationZone> liszones = zoneDao.getZoneStoresBydomainId(""+usersDomains.getDomainId());

		Listitem listItem = null;
		int count=dispZoneLBoxId.getItemCount(); 
	    if( count> 1) {
		for(;count>1;count--) {
			dispZoneLBoxId.removeItemAt(count-1);
		}
	    }
		if(liszones != null){
			for (OrganizationZone zone : liszones) {
				listItem = new Listitem(zone.getZoneName());
				listItem.setValue(zone);
				listItem.setParent(dispZoneLBoxId);
			}
		}
		if(dispZoneLBoxId.getItemCount()>1)
			dispZoneLBoxId.setSelectedIndex(0);
		setFolders();
		//TODO 
		//defaultDigiSettings(account);
	}
	 */

	/*    public void onSelect$dispZoneLBoxId(){
 	   logger.info("======inside zone selection ===========");
 	   if(orgUnitlbId.getSelectedIndex()==0) return;
		if(dispZoneLBoxId.getSelectedIndex() == 0){
			mytemplatesLbId.setSelectedIndex(0);
			dispFolderBoxId.setSelectedIndex(0);
			return;
		}

		//Account acc=orgUnitlbId.getSelectedItem().getValue();
		UsersDomains userDomain=(UsersDomains)orgUnitlbId.getSelectedItem().getValue();

		OrganizationZone orgZone = dispZoneLBoxId.getSelectedItem().getValue();


   		Long powerUserID = findPowerUserByDomainId(((UsersDomains)orgUnitlbId.getSelectedItem().getValue()).getDomainId());



		DigitalReceiptUserSettings digitalReceiptUserSettings = digitalReceiptUserSettingsDao.findUserSelectedTemplateByZone(powerUserID, null, orgZone.getZoneId());


		if(digitalReceiptUserSettings != null) {
			Long selectedTemplateID = digitalReceiptUserSettings.getMyTemplateId();

   			//DigitalReceiptMyTemplate drMyTemp= digiRecptMyTemplatesDao.findByUserNameAndTemplateName(currentUser.getUserOrganization().getUserOrgId(), selectedTemplateID);
   			DigitalReceiptMyTemplate drMyTemp= digiRecptMyTemplatesDao.findByUserIDAndTemplateName(powerUserID, selectedTemplateID);


			logger.info("selected template "+digitalReceiptUserSettings.getSelectedTemplateName());
		   dispFolderBoxId.setSelectedIndex(0);
			if(drlabelId!=null){
		    List<Listitem> listitem=dispFolderBoxId.getItems();
		    for(Listitem item:listitem){
		    	DRTemplateLabel label=item.getValue();
		    	if(label!=null){
		    		if(label.getLabelId().equals(drlabelId)){
		    			dispFolderBoxId.setSelectedItem(item);
		    			continue;
		    		}
		    	}
		      }
			}
				List<Listitem> items=mytemplatesLbId.getItems();
				logger.info("item count "+mytemplatesLbId.getItemCount());
				if(mytemplatesLbId.getItemCount()>=1){
					onSelect$dispFolderBoxId();
				}
			for(Listitem item:items){
				DigitalReceiptMyTemplate dr=item.getValue();
				if(dr!=null)
				if(dr.getMyTemplateId().equals(selectedTemplateID)){
					mytemplatesLbId.setSelectedItem(item);
					break;
				}
			}
			logger.debug("mytemplatesLbId===="+mytemplatesLbId.getSelectedIndex());
			//TODO 18_03_2019
			//defaultDigiSettings(digitalReceiptUserSettings);
			}
		else if(digitalReceiptUserSettings == null) {
			dispFolderBoxId.setSelectedIndex(0);
			mytemplatesLbId.setSelectedIndex(0);
			cSubTb.setValue("");
		}


    }
	 */

	public void setFolders(){
		int count=dispFolderBoxId.getItemCount(); 
		if( count> 1) {
			for(;count>1;count--) 
				dispFolderBoxId.removeItemAt(count-1);
		}
		try {
			List<MyFolders> myFolderList = null;
			//Super User
			myFolderList = myFolderDao.findUserOrgLevelFolders(currentUser.getUserOrganization().getUserOrgId());

			if(myFolderList==null) return;
			else{
				for(MyFolders myfolders :myFolderList) {
					logger.debug("------in if case----"+myfolders.getFolderName());
					String folderName = myfolders.getFolderName();						
					Listitem item = new Listitem();
					item.setValue(myfolders);
					Listcell cell = new Listcell();
					cell.setLabel(folderName);
					//cell.setStyle("width: 90%; display: inline-block;");
					cell.setParent(item);
					item.setParent(dispFolderBoxId);
				}
				dispFolderBoxId.setSelectedIndex(0);
			}
			logger.info("mytemplatesFolderLbId label: "+dispFolderBoxId.getSelectedItem().getLabel());
			//onSelect$dispFolderBoxId();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	/*	public void onSelect$dispFolderBoxId(){
		try {
			logger.info("=inside template set====");
			//Account acc=(Account)orgUnitlbId.getSelectedItem().getValue();

			UsersDomains userDomain=(UsersDomains)orgUnitlbId.getSelectedItem().getValue();

			if(orgUnitlbId.getSelectedIndex()==0){
				MessageUtil.setMessage("Please choose zone first.", "color:blue", "TOP");
				dispFolderBoxId.setSelectedIndex(-1);
				return;
			}
			int count = mytemplatesLbId.getItemCount();
			for (; count > 1; count--) {
				mytemplatesLbId.removeItemAt(count - 1);
			}
			Set<Listitem> items=dispFolderBoxId.getSelectedItems();

				boolean unlabel=false;
		StringBuilder sb=new StringBuilder();
		for(Listitem item:items){
			DRTemplateLabel label=item.getValue();
			if(label!=null){
				if(sb.length()>0)sb.append(",");
				sb.append(label.getLabelId());
			}
			else if(label==null)unlabel=true;
		}
		int cnt= dispFolderBoxId.getSelectedCount();
		if(unlabel)
			cnt=cnt-1; 
		List<DRTemplateLabel> list= drTemplateLabelDAO.findBy(acc.getAccountId());
		 List<DigitalReceiptMyTemplate> tempObj=null;
		 logger.info("Selected label "+sb.toString());
		if(sb!=null && sb.length()>0){
			tempObj= digiRecptMyTemplatesDao.findDRMyTemplatesByLabel(""+acc.getAccountId(),sb.toString(),cnt,0,100,null);
		}
		else if(list==null || list.size()==0){
			tempObj= digiRecptMyTemplatesDao.findAllTemplatesByAccount(acc.getAccountId());
		}
		else{
			tempObj= digiRecptMyTemplatesDao.findTemplatesByAccountNotLabel(acc.getAccountId());
		}
			OrganizationZone orgZone = dispZoneLBoxId.getSelectedItem().getValue();
			if(orgUnitlbId.getSelectedIndex()==0) return;

			Long powerUserID = findPowerUserByDomainId(userDomain.getDomainId());
			//DRTemplateListObj= digiRecptMyTemplatesDao.findTemplatesByAccountNotLabel(userDomain.getDomainId());
			if(powerUserID!=null){
				DigitalReceiptUserSettings digitalReceiptUserSettings = digitalReceiptUserSettingsDao.findUserSelectedTemplateByZone(powerUserID, null, orgZone.getZoneId());


				if(digitalReceiptUserSettings != null) {

	   			Long selectedTemplateID = digitalReceiptUserSettings.getMyTemplateId();
	   			DigitalReceiptMyTemplate drMyTemp= digiRecptMyTemplatesDao.findByUserNameAndTemplateName(powerUserID, selectedTemplateID);
	   			if(drMyTemp!=null){

	   			String selectedTemplate = drMyTemp.getName();
	   			logger.debug("selectedTemplate===="+selectedTemplate);
	   			if (selectedTemplate != null) {
	   				selectedTemplateLblId.setAttribute("templateName", selectedTemplate);
	   				selectedTemplateLblId.setValue(selectedTemplate);
	   			}

	   				boolean folderSelected = false;
	   				boolean mytemplateSelected = true;
	   				String selectedFolder = drMyTemp.getFolderName();
	   				List<Listitem> retList1 = dispFolderBoxId.getItems();
	   				if(selectedFolder!=null)
	   				{	for (Listitem listitem : retList1) {
	   					if(listitem.getIndex()==0) continue;
	   					if(listitem.getLabel().equalsIgnoreCase(selectedFolder)) {
	   						listitem.setSelected(true);
	   						folderSelected = true;
	   						break;
	   					}
	   				}
	   				}
	   				if(folderSelected) onSelect$dispFolderBoxId();
	   				else mytemplatesFolderLbId.setSelectedIndex(0);
	   				List<Listitem> retList = mytemplatesLbId.getItems();

	   				for (Listitem listitem : retList) {
	   					if(listitem.getIndex()==0) continue;
	   					if(listitem.getLabel().equalsIgnoreCase(selectedTemplate)) {
	   						listitem.setSelected(true);
	   						mytemplateSelected = false;
	   						break;
	   					}
	   				}
	   				if(mytemplateSelected) mytemplatesLbId.setSelectedIndex(0);
	   			}
	   			logger.debug("mytemplatesLbId===="+mytemplatesLbId.getSelectedIndex());
	   			defaultDigiSettings(digitalReceiptUserSettings);

	   		}

				List<DigitalReceiptMyTemplate> drMyTemp = null;	

				if((dispFolderBoxId.getSelectedItem().getLabel()).equals(OCConstants.DRAG_DROP_EDITOR_DR_TEMPLATES_FOLDER_DEFAULT))	
					drMyTemp = digiRecptMyTemplatesDao.findByUserId_PublicTemplatesonly(powerUserID, OCConstants.DRAG_DROP_EDITOR_DR_TEMPLATES_FOLDER_DEFAULT);
				else if((dispFolderBoxId.getSelectedItem().getLabel()).equals(OCConstants.DR_LEGACY_EDITOR_FOLDER))	
					drMyTemp = digiRecptMyTemplatesDao.findByUserId_PublicTemplatesonly(powerUserID, OCConstants.DR_LEGACY_EDITOR_FOLDER);

				Listitem listItem=null;
				for (DigitalReceiptMyTemplate digitalReceiptMyTemplate : drMyTemp) {
					listItem=new Listitem(digitalReceiptMyTemplate.getName(),digitalReceiptMyTemplate);
					listItem.setParent(mytemplatesLbId);
				}
			}else return;

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	} 
	 */


	public Long findPowerUserByDomainId(Long domainId) {

		List<Map<String, Object>> tempList = usersDao.findSuperUsersOfSelDomain(domainId);

		for(Map<String, Object> tempRec : tempList) {
			return ((Long)tempRec.get("user_id")).longValue(); 
		}//for

		return null;

	}

	public void onCheck$enableSendingChkBxId() {
		
		logger.info("entered into onCheck$enableSendingChkBxId :"+enableSendingChkBxId.isChecked());
		if(enableSendingChkBxId.isChecked()) {
			fromAndReplyDivId.setVisible(true);
		}else {
			fromAndReplyDivId.setVisible(false);
		}
		
	}//onCheck$enableSendingChkBxId
	

} 
