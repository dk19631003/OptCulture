package org.mq.marketer.campaign.controller.campaign;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.DigitalReceiptMyTemplate;
import org.mq.marketer.campaign.beans.DigitalReceiptUserSettings;
import org.mq.marketer.campaign.beans.MyFolders;
import org.mq.marketer.campaign.beans.OrganizationStores;
import org.mq.marketer.campaign.beans.SecRoles;
import org.mq.marketer.campaign.beans.UserFromEmailId;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.beans.UsersDomains;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.controller.gallery.MyTempEditorController;
import org.mq.marketer.campaign.controller.layout.EditorController;
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
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PageListEnum;
import org.mq.marketer.campaign.general.PageUtil;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.PurgeList;
import org.mq.marketer.campaign.general.Redirect;
import org.mq.marketer.campaign.general.SpamChecker;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.springframework.dao.DataIntegrityViolationException;
import org.zkforge.ckez.CKeditor;
import org.zkoss.image.AImage;
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
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Menupopup;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Popup;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Vbox;
import org.zkoss.zul.Window;
import org.zkoss.zul.event.PagingEvent;

public class EReceiptsLaunchingController extends GenericForwardComposer implements EventListener, ListitemRenderer {

	MyFoldersDao myFolderDao;
	MyFoldersDaoForDML myFolderDaoForDML;

	Listbox digitalTemplatesListId;
	String digitalRecieptTemplatesPath;
	CKeditor digitalRecieptsCkEditorId;
	DigitalReceiptMyTemplatesDao digiRecptMyTemplatesDao;
	DigitalReceiptMyTemplatesDaoForDML digiRecptMyTemplatesDaoForDML;
	MailingListDao mailingListDao;
	Window winId, testDRWinId, makeACopyWinId, viewDetailsWinId, renameTemplateWinId,copywinId,copyTemplateswinId,editTemplateNamewinId,MoveTemplatewinId,RenameMoveTemplatewinId;
	Textbox winId$templatNameTbId, testDRWinId$testDRTbId,copywinId$templatNameTbId,editTemplateNamewinId$templatNameTbId;
	Tab myTemplatesTabId, digiRcptReportsTabId;
	Listbox digiRecptReportListId, myTemplatesListId,dispSBSLBoxId, dispStoreLBoxId, mytemplatesLbId, tempsettingsLbId;
	Label selectedTemplateLblId, testDRWinId$msgLblId, viewDetailsWinId$templateNameLblId,
	viewDetailsWinId$creationDateLblId, viewDetailsWinId$lastAccLblId,regFrmEmlAnchId,regRepEmlAnchId,CreateDRInfId,PowerUserDRInfId;
	private Menuitem saveAsMyChoiceMId,sendTestReceiptMId;
	// Listbox digiRcptSettingLstId1;
	Grid templateAndJSONMappingGridId;
	Button addFieldBtnId, saveBtnId;
	Textbox jsonKeyFieldTbxId;
	// Radiogroup templateTypeGrpId;
	Tabbox tabBoxId;
	Toolbarbutton sendTestBtnId;

	final String  STORE_WISE_DR_SETTINGS_OBJ="DRObj";

	/**
	 * Digital Receipt settings
	 * 
	 */

	Image manageActionsImg = new Image("/img/theme/arrow.png");

	private Popup regEmailPopupId, regRepEmailPopupId;
	private Textbox cFromNameTb, cSubTb, cFromEmailTb,cRepEmailTb, cWebLinkTextTb,
	cPhoneTbId;
	private Button cancelBtnId, submitBt1nId, renameTempltBtnId$renameTemplateWinId, configTemplateBtnId;
	private Combobox cFromEmailCb,cRepEmailCb;
	private Checkbox toNameChkId, cWebPageCb, enableSendingChkBxId, shippingAmntChkBxId, feeAmntChkBxId, taxAmntChkBxId, discAmntChkBxId;//,enableStoreChkBxId;
	private Radiogroup cPermRemRb,frmEmaildynamicOrNotRgId,frmNamedynamicOrNotRgId;
	private Div permRemDivId, enableStoreDivId;
	private Div persToDivId, cWebLinkHboxId, DynamicId, Dynamic1Id,repRegdivId,froRegdivId;
	private Textbox permRemTextId;
	private Listbox phLbId, orgUnitlbId;
	private Textbox cWebLinkUrlTextTb,makeACopyWinId$templatNameTbId,renameTemplateWinId$newTempltNameTbId,RenameMoveTemplatewinId$templatNameTbId;

	// digiRcptSettingLstId2,digiRcptSettingLstId3,digiRcptSettingLstId4,digiRcptSettingLstId5,
	// digiRcptSettingLstId6;

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);

	private static final String SYSTEM_TEMPLATE = "SYSTEM:";
	private static final String MY_TEMPLATE = "MY_TEMPLATE:";
	private final String DEFAULT_TEMPLATE_NAME = "No template selected.";
	//String[] templateNamesArr;
	private Users currentUser;
	private String userName;
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
	public static boolean editTemplate,newTemplate;
	private int editTemplatefolder;
	private String newTemplatefolder;;

	private Session session;
	private Menupopup manageActionsMpId;
	private boolean onSave,isEditTemplateName,isMoveTemplate,isRenameMoveTemplate;
	private OrganizationStoresDao organizationDao;
	TimeZone clientTimeZone ;
	private Groupbox enableStoreWiseGBID;
	private UsersDomainsDao domainDao;
	private Tab LegacyEditorId;
	//DRTemplates
	private final String  USER_DR_NEW_EDITOR_TEMPLATES_DIRECTORY= "userDRNewEditorTemplatesDirectory";

	private Listbox newEditorTemplatesListId,legacyEditorTemplatesListId,templatePerPageLBId;
	private Div templListDivId,iconDescriptionDivId,powIconDescriptionDivId,supIconDescriptionDivId;
	//private Groupbox templatesGBoxId;
	
	Textbox templateTextName;
	
	 private Paging  templatePagingId;

	//private Window winId;
	private Listbox winId$myTempListId,myTempListId,copywinId$folderId,copyTemplateswinId$folderId,MoveTemplatewinId$folderId,editTemplateNamewinId$modifiedNameBtnId,RenameMoveTemplatewinId$folderId;
	//private Listbox myTemplatesListId;

	private Session sessionScope = null;

	private String currentUserRole;

	static final String drAdmin = "DRAdmin";
	static final String drPower = "DRPower";

	private Div newMyTemplatesSubWinId$createFolderDivId;
	private Button newMyTemplatesSubWinId$okBtnId; 
	private Window newMyTemplatesSubWinId;

	private Textbox newMyTemplatesSubWinId$newFolderTbId;
	private Label newMyTemplatesSubWinId$folderErrorMsgLblId,copywinId$folderErrorMsgLblId,MoveTemplatewinId$folderErrorMsgLblId,editTemplateNamewinId$folderErrorMsgLblId,RenameMoveTemplatewinId$folderErrorMsgLblId;

	Window testWinId;
	Label testWinId$msgLblId;
	Textbox emailNameTbId,testWinId$testTbId;
	private Button testWinId$sendTestMailBtnId;

	private Menupopup manageFolderMpId;

	private Toolbarbutton createTemplateTBarBtnId;
	private Image folderImgId;

	Image manageNewEditorFolderImg = new Image("/img/theme/arrow.png");
	private int selectfolder;
	static final String USER_PARENT_DIR=PropertyUtil.getPropertyValue("usersParentDirectory");
	static final String USER_NEW_EDITOR_THUMBNAIL_DIR=PropertyUtil.getPropertyValue("userDRTemplateNewEditorDirectory");
	static final String USER_LEGACY_EDITOR_THUMBNAIL_DIR=PropertyUtil.getPropertyValue("userDRTemplateLegacyEditorDirectory");
	public EReceiptsLaunchingController() {
		// TODO Auto-generated constructor stub
		session = Sessions.getCurrent();
		isAdmin = (Boolean)session.getAttribute("isAdmin");
		clientTimeZone = (TimeZone)session.getAttribute("clientTimeZone");
		sessionScope = Sessions.getCurrent();
		myFolderDao = (MyFoldersDao) SpringUtil.getBean("myFoldersDao");
		myFolderDaoForDML = (MyFoldersDaoForDML) SpringUtil.getBean("myFoldersDaoForDML");
		digitalReceiptUserSettingsDao= (DigitalReceiptUserSettingsDao) SpringUtil.getBean("digitalReceiptUserSettingsDao");
		digitalReceiptUserSettingsDaoForDML= (DigitalReceiptUserSettingsDaoForDML) SpringUtil.getBean("digitalReceiptUserSettingsDaoForDML");

	}

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		try {
			super.doAfterCompose(comp);
			currentUser = GetUser.getUserObj();
			//userName = GetUser.getUserName();
			userName = currentUser.getUserName();
			//PageUtil.setHeader("Digital Receipt Templates", "", "", true);

			String style = "font-weight:bold;font-size:15px;color:#313031;font-family:Arial,Helvetica,sans-serif;align:left";
			PageUtil.setHeader("e-Receipt Templates","","",true);

			 String new_Editor_thumbnail_folder =  USER_PARENT_DIR+File.separator+currentUser.getUserName()+USER_NEW_EDITOR_THUMBNAIL_DIR;
			 String legacy_Editor_thumbnail_folder=USER_PARENT_DIR+File.separator+currentUser.getUserName()+USER_LEGACY_EDITOR_THUMBNAIL_DIR;
			 
			 File myTemplateFile = new File(new_Editor_thumbnail_folder);
			  if(!myTemplateFile.exists()) {
				  myTemplateFile.mkdirs();
				}
			 			 
			 File myTemplateFileLegacy = new File(legacy_Editor_thumbnail_folder);
			 if(!myTemplateFileLegacy.exists()) {
				 myTemplateFileLegacy.mkdirs();
			}
			 
			 
			 
			
/*			digitalRecieptTemplatesPath = PropertyUtil.getPropertyValue("digitalRecieptTemplatesFolder");

			if (digitalRecieptTemplatesPath == null) {
				logger.debug("Digital reciepts Template Folder Does Not Exist ... returning . :: "+ digitalRecieptTemplatesPath);
				return;
			}

			File digitalTemplatesDirectory = new File(digitalRecieptTemplatesPath);
			templateNamesArr = digitalTemplatesDirectory.list();

			if (templateNamesArr == null || templateNamesArr.length < 1) {
				logger.debug("Digital reciepts Directory is not having any files ... returning  ");
				return;
			}
			Arrays.sort(templateNamesArr);

			Listitem li = null;

			for (int i = 0; i < templateNamesArr.length; i++) {

				Listcell lc = new Listcell();
				lc.setLabel(templateNamesArr[i]);
				li = new Listitem();
				lc.setParent(li);
				li.setValue(templateNamesArr[i]);
				li.setTooltiptext(templateNamesArr[i]);
				li.addEventListener("onClick", this);
				li.setParent(digitalTemplatesListId);
			}*/
			
			logger.info("currentUser.getUserOrganization().getUserOrgId() :"+currentUser.getUserOrganization().getUserOrgId());

			List<MyFolders> myFoldersDDFoldersList = myFolderDao.findByOrgIdFolderName(currentUser.getUserOrganization().getUserOrgId(),OCConstants.DnD_DIGITAL_RECEIPT_TEMPLATES,OCConstants.DRAG_DROP_EDITOR_DR_TEMPLATES_FOLDER_DEFAULT);
			if(myFoldersDDFoldersList.size()==0){

				MyFolders myfolder =new MyFolders(OCConstants.DRAG_DROP_EDITOR_DR_TEMPLATES_FOLDER_DEFAULT, MyCalendar.getNewCalendar(), currentUser.getUserId(), currentUser.getUserOrganization().getUserOrgId(),OCConstants.DnD_DIGITAL_RECEIPT_TEMPLATES);
				myfolder.setModifiedDate(MyCalendar.getNewCalendar());
				myFolderDaoForDML.saveOrUpdate(myfolder);

			}
/*			List<MyFolders> myFoldersDDFoldersList2 = myFolderDao.findByOrgIdFolderName(currentUser.getUserOrganization().getUserOrgId(),OCConstants.DnD_DIGITAL_RECEIPT_TEMPLATES,OCConstants.DRAG_DROP_EDITOR_DR_TEMPLATES_FOLDER_DRAFT);
			if(myFoldersDDFoldersList2.size()==0){

				MyFolders myfolder =new MyFolders(OCConstants.DRAG_DROP_EDITOR_DR_TEMPLATES_FOLDER_DRAFT, MyCalendar.getNewCalendar(), currentUser.getUserId(), currentUser.getUserOrganization().getUserOrgId(),OCConstants.DnD_DIGITAL_RECEIPT_TEMPLATES);
				myfolder.setModifiedDate(MyCalendar.getNewCalendar());
				myFolderDaoForDML.saveOrUpdate(myfolder);

			}*/
			List<MyFolders> myFoldersLeagacyFoldersList = myFolderDao.findByOrgIdFolderName(currentUser.getUserOrganization().getUserOrgId(),OCConstants.LEGACY_DIGITAL_RECEIPT_TEMPLATES,OCConstants.DR_LEGACY_EDITOR_FOLDER);
			if(myFoldersLeagacyFoldersList.size()==0){

				MyFolders myfolder =new MyFolders(OCConstants.DR_LEGACY_EDITOR_FOLDER, MyCalendar.getNewCalendar(), currentUser.getUserId(), currentUser.getUserOrganization().getUserOrgId(),OCConstants.LEGACY_DIGITAL_RECEIPT_TEMPLATES);
				myfolder.setModifiedDate(MyCalendar.getNewCalendar());
				myFolderDaoForDML.saveOrUpdate(myfolder);

			}						

			getNewEditorTemplatesFromDb(currentUser.getUserId());
			/* commented by Venkata 26_02_2019
			 * 
			 * if(drAdmin.endsWith(getEditorRoleSet())){
				currentUserRole=drAdmin;
				powIconDescriptionDivId.setVisible(false);
				PowerUserDRInfId.setVisible(false);
				getNewEditorTemplatesFromDb(currentUser.getUserId());
			}else if(drPower.endsWith(getEditorRoleSet())){
				currentUserRole=drPower;
				createTemplateTBarBtnId.setVisible(false);
				folderImgId.setVisible(false);
				LegacyEditorId.setVisible(false);
				CreateDRInfId.setVisible(false);
				supIconDescriptionDivId.setVisible(false);
				getNewEditorTemplatesFromDb(currentUser.getUserId());
			}*/	
			

			if(sessionScope!=null && session!=null)
			{
				sessionScope.removeAttribute("DRTemplate");
				session.removeAttribute("isDRTemplateEdit");
				session.removeAttribute("isDraftTemplateEdit");

				session.removeAttribute("DRTemplate");
			}
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ", e);;
		}
	}

	public static void generateThumbnil(File file, String template, String pathTojs) {
		try {
			ProcessBuilder pb = new ProcessBuilder(pathTojs+"bin/phantomjs",pathTojs+"htmltoImage/htmltoImage.js", file.getAbsolutePath()+"/DR_Template_Img.html", template);
			Process p = pb.start();
			int exitVal = p.waitFor(); // do a wait here to prevent it running for ever
			if (exitVal != 0) {
				logger.error("EXIT-STATUS - " + p.toString());
			}
			p.destroy();
		}catch (Exception e) {
			logger.error("loading image error");
		}
		
	}
	
	public String getEditorRoleSet(){

		try {

			SecRolesDao secRolesDao = (SecRolesDao)SpringUtil.getBean("secRolesDao");
			List<SecRoles> userRolesList = secRolesDao.findByUserId(currentUser.getUserId());

			for(SecRoles sB_Users:userRolesList){
				if(sB_Users.getType().equalsIgnoreCase(drAdmin)){

					return "DRAdmin";
				}else if(sB_Users.getType().equalsIgnoreCase(drPower)){

					return "DRPower";

				}
			}
			return "empty";
		}catch (Exception e) {
			logger.error("Exception ", e);
			return "empty";
		}		
	}

	public void getNewEditorTemplatesFromDb(long userId) {

		try {
			Components.removeAllChildren(newEditorTemplatesListId);

			digiRecptMyTemplatesDao = (DigitalReceiptMyTemplatesDao) SpringUtil.getBean("digitalReceiptMyTemplatesDao");
			digiRecptMyTemplatesDaoForDML = (DigitalReceiptMyTemplatesDaoForDML) SpringUtil.getBean("digitalReceiptMyTemplatesDaoForDML");


			setUserFolders();
			newEditorTemplatesListId.setSelectedIndex(0);
			onSelect$newEditorTemplatesListId();


			/*commented by venkata 26_02_2019
			 * 
			 * if(currentUserRole.equals(drAdmin)){
				setSuperUserFolders();
			}else if(currentUserRole.equals(drPower)){
				setPowerUserFolders();
			}


			if(newEditorTemplatesListId.getItems().size() >0){
				if(session.hasAttribute("editTemplate") && (Boolean)session.getAttribute("editTemplate"))
				{
					editTemplatefolder = (int)session.getAttribute("editTemplatefolder");
					newEditorTemplatesListId.setSelectedIndex(editTemplatefolder);
					session.removeAttribute("editTemplate");
					session.removeAttribute("editTemplatefolder");

				}
				else if(session.hasAttribute("newTemplate") && (Boolean)session.getAttribute("newTemplate")){

					newTemplatefolder = (String)session.getAttribute("newTemplatefolder");

					int idx=0;
					for(Listitem item : newEditorTemplatesListId.getItems()){

						MyFolders myFolders= (MyFolders)item.getValue();
						 //logger.info("myFolders.getFolderName() :"+myFolders.getFolderName());
						if(myFolders.getFolderName().equalsIgnoreCase(newTemplatefolder))
						{	
							idx = item.getIndex();
						}
					}

					newEditorTemplatesListId.setSelectedIndex(idx);
					session.removeAttribute("newTemplate");
					session.removeAttribute("newTemplatefolder");
				}else{
					newEditorTemplatesListId.setSelectedIndex(0);
				}

				if(currentUserRole.equalsIgnoreCase(drAdmin))
					manageNewEditorFolderImg.addEventListener("onClick", this);
				onSelect$newEditorTemplatesListId();

		}
			 */

			/*if(newEditorTemplatesListId.getItems().size() >0){
				newEditorTemplatesListId.setSelectedIndex(0);
				if(currentUserRole.equalsIgnoreCase(drAdmin))
					manageNewEditorFolderImg.addEventListener("onClick", this);
				onSelect$newEditorTemplatesListId();
			}*/

		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);;
		}

	}

	public void setSuperUserFolders(){
		try {

			List<MyFolders> org_Folders = myFolderDao.findByOrgIdOrderByFolderNames(currentUser.getUserOrganization().getUserOrgId(),OCConstants.DnD_DIGITAL_RECEIPT_TEMPLATES);
			//myTemp= new File(myTempPathStr);
			//templateList = myTemp.listFiles();
			//logger.debug("myTemp.listFiles().length"+myTemp.listFiles().length);

			for(MyFolders myfolders :org_Folders) {
				logger.debug("------in if case----"+myfolders.getFolderName());
				String folderName = myfolders.getFolderName();						
				if(folderName!=null && !folderName.isEmpty() && (folderName.equals(OCConstants.DRAG_DROP_EDITOR_DR_TEMPLATES_FOLDER_DEFAULT)))
				{
					Listitem item = new Listitem();
					item.setValue(myfolders);
					Listcell cell = new Listcell();
					Label tempLbl = new Label(folderName);
					tempLbl.setStyle("width: 90%; display: inline-block;");
					cell.appendChild(tempLbl);
					cell.setParent(item);
					item.setParent(newEditorTemplatesListId);
				}
			}

			for(MyFolders myfolders :org_Folders) {
				logger.debug("------in if case----"+myfolders.getFolderName());
				String folderName = myfolders.getFolderName();						
				//List<MyTemplates> tempList =myTemplatesDao.findDefaultMyTemplates(userId, folderName, Constants.NEWEDITOR_TEMPLATES_PARENT);
				//List<DigitalReceiptMyTemplate> tempList = digiRecptMyTemplatesDao.findDRMyTemplates(userId, folderName, Constants.NEWEDITOR_TEMPLATES_PARENT);

				if(folderName!=null && !folderName.isEmpty() && (folderName.equals(OCConstants.DRAG_DROP_EDITOR_DR_TEMPLATES_FOLDER_DEFAULT)))
					continue;
				Listitem item = new Listitem();
				item.setValue(myfolders);
				//logger.info("tempList.size()   ----:"+tempList.size());
				//venkat
				Listcell cell = new Listcell();
				//Label tempLbl = new Label(folderName+" ("+tempList.size() +")");
				Label tempLbl = new Label(folderName);
				tempLbl.setStyle("width: 90%; display: inline-block;");
				cell.appendChild(tempLbl);
				cell.setParent(item);
				//item.setLabel(folderName);
				item.setParent(newEditorTemplatesListId);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void setUserFolders(){
		try {
			List<MyFolders> myFolderList = null;

			myFolderList = myFolderDao.findUserOrgLevelFolders(currentUser.getUserOrganization().getUserOrgId());

			if(myFolderList==null) return;
			else{

				for(MyFolders myfolders :myFolderList) {
					logger.debug("------in if case----"+myfolders.getFolderName());
					String folderName = myfolders.getFolderName();						
					Listitem item = new Listitem();
					item.setValue(myfolders);
					Listcell cell = new Listcell();
					/*Label tempLbl = new Label(folderName);
					tempLbl.setStyle("width: 90%; display: inline-block;");
					cell.appendChild(tempLbl);*/
					cell.setLabel(folderName);
					cell.setStyle("width: 90%; display: inline-block;");
					cell.setParent(item);
					item.setParent(newEditorTemplatesListId);
				}
			}
			
			newEditorTemplatesListId.setSelectedIndex(0);
			logger.info("newEditorTemplatesListId label: "+newEditorTemplatesListId.getSelectedItem().getLabel());
			//logger.info("newEditorTemplatesListId style: "+newEditorTemplatesListId.getSelectedItem().getStyle());
			/*commented by Venkata 26_02_2019 12.51 PM
			 * list1 = myFolderDao.findPowerUserFolders(currentUser,OCConstants.DnD_DIGITAL_RECEIPT_TEMPLATES);

			if(list1==null) return;
			else{

				StringBuffer bufferIds = new StringBuffer();
				for(Map<String, Object> eachMap : list1){

					if(bufferIds.length() > 0) bufferIds.append(",");
					bufferIds.append(eachMap.get("ids").toString());
				}

				String myFolderIds=bufferIds.toString();

				if(myFolderIds==null||myFolderIds.isEmpty()) return;
				List<MyFolders> org_Folders = myFolderDao.findPUFolders(myFolderIds);

				for(MyFolders myfolders :org_Folders) {
					logger.debug("------in if case----"+myfolders.getFolderName());
					String folderName = myfolders.getFolderName();						
					Listitem item = new Listitem();
					item.setValue(myfolders);
					Listcell cell = new Listcell();
					Label tempLbl = new Label(folderName);
					tempLbl.setStyle("width: 90%; display: inline-block;");
					cell.appendChild(tempLbl);
					cell.setParent(item);
					item.setParent(newEditorTemplatesListId);
				}
			}*/
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
		}
	}

	public void onSelect$templatePerPageLBId(){
		int n = Integer.parseInt(templatePerPageLBId.getSelectedItem().getLabel().trim());
		templatePagingId.setPageSize(n);
		templatePagingId.addEventListener("onPaging", this); 
		setMyTemplates(newEditorTemplatesListId.getSelectedItem().getLabel(),0,templatePagingId.getPageSize(),null);
	}
	
	public void onOK$templateTextName(){
		String tempName=templateTextName.getText().trim();
		logger.info("Search template name "+tempName);
		String folderName= newEditorTemplatesListId.getSelectedItem().getLabel();
		int n = Integer.parseInt(templatePerPageLBId.getSelectedItem().getLabel().trim());
		templatePagingId.setTotalSize(digiRecptMyTemplatesDao.findDRMyTemplatesCountByUserID(currentUser.getUserId(), folderName , OCConstants.DRAG_AND_DROP_EDITOR,tempName));
		templatePagingId.setPageSize(n);
		templatePagingId.setActivePage(0);
		templatePagingId.addEventListener("onPaging", this);
		setMyTemplates(folderName,0,templatePagingId.getPageSize(),tempName);
	}
	
	public void onSelect$newEditorTemplatesListId(){
		selectfolder = newEditorTemplatesListId.getSelectedIndex();
		Components.removeAllChildren(templListDivId);
		String folderName=null;

		try{

			if(newEditorTemplatesListId==null||newEditorTemplatesListId.getSelectedItem()==null || newEditorTemplatesListId.getSelectedItem().getLabel()==null)
				logger.info("newEditorTemplatesListId :"+newEditorTemplatesListId +" newEditorTemplatesListId.getSelectedItem(): "+newEditorTemplatesListId.getSelectedItem()+"newEditorTemplatesListId.getSelectedItem().getLabel() :"+newEditorTemplatesListId.getSelectedItem().getLabel());
			//folderName = newEditorTemplatesListId.getSelectedItem().getLabel();
			//logger.info("newEditorTemplatesListId :"+newEditorTemplatesListId +" newEditorTemplatesListId.getSelectedItem(): "+newEditorTemplatesListId.getSelectedItem()+"newEditorTemplatesListId.getSelectedItem().getLabel() :"+newEditorTemplatesListId.getSelectedItem().getLabel());

		} 
		catch (ClassCastException e1) {
			// TODO: handle exception
			logger.error("ClassCastException ::" , e1);
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("newEditorTemplatesListId.getSelectedItem() Trace");
			newEditorTemplatesListId.setSelectedIndex(0);
			onSelect$newEditorTemplatesListId();
		}
		if(newEditorTemplatesListId.getSelectedIndex()==0){
			int n = Integer.parseInt(templatePerPageLBId.getSelectedItem().getLabel().trim());
			folderName = OCConstants.DRAG_DROP_EDITOR_DR_TEMPLATES_FOLDER_DEFAULT;
			supIconDescriptionDivId.setVisible(true);
			powIconDescriptionDivId.setVisible(false);
			templatePagingId.setPageSize(n);
			templatePagingId.setActivePage(0);
			//templatePagingId.setTotalSize(15);
			templatePagingId.setTotalSize(digiRecptMyTemplatesDao.findDRMyTemplatesCountByUserID(currentUser.getUserId(), folderName, OCConstants.DRAG_AND_DROP_EDITOR,null));
			templatePagingId.addEventListener("onPaging", this);
			setMyTemplates(folderName,0,templatePagingId.getPageSize(),null);
		}else{
			int n = Integer.parseInt(templatePerPageLBId.getSelectedItem().getLabel().trim());
			folderName = OCConstants.DR_LEGACY_EDITOR_FOLDER;
			powIconDescriptionDivId.setVisible(true);
			supIconDescriptionDivId.setVisible(false);
			templatePagingId.setPageSize(n);
			templatePagingId.setActivePage(0);
			templatePagingId.setTotalSize(digiRecptMyTemplatesDao.findDRMyTemplatesCountByUserID(currentUser.getUserId(), folderName, OCConstants.LEGACY_EDITOR,null));
			templatePagingId.addEventListener("onPaging", this);
			setMyTemplates(folderName,0,templatePagingId.getPageSize(),null);
		}
	}

	
	public void setMyTemplates(String folderName,int startIndx,int endIndx,String tempName){

		File category = null;
		File[] templateList = null;
		String templatePrevStr = null;
		String appUrl = PropertyUtil.getPropertyValue("ApplicationUrl");
		String preview_Path = null;
		String userTemplatesDirectory = PropertyUtil.getPropertyValue(USER_DR_NEW_EDITOR_TEMPLATES_DIRECTORY);
		 
		Components.removeAllChildren(templListDivId);
		folderName = newEditorTemplatesListId.getSelectedItem().getLabel();
		logger.info(" folder Name "+folderName);
		if(folderName==null) return;
		final String useIconPathStr = "/images/Edit_icn.png";
		final String deleteIconPathStr = "/img/theme/action_delete.gif";
		final String draft = "/img/theme/draft_icon.png";
		final String renameIcon = "/img/theme/rename.png";
		final String testmailIcon="/img/email_page.gif";
		
		try {
			Image templateImg = null;

			Toolbarbutton preview,edit,deleteTb,shareTb = null,copyTb,testMailTb,draftTb,editTemplateName,moveTemplate,editMoveTemplateName,restoredTb,starTb,reNameTemplate;
			Label templateName;
			Hbox hbox = null,hbox1 = null;
			String preview_Src ="";
			String previewIconPathStr = "/img/theme/preview_icon.png";
			String vbStyle = "templatePrviewVb";
			String custom_temp_default_preview = "/img/custom_temp_preview.gif";
			List<DigitalReceiptMyTemplate> myTemplatesList = null;
			
			if(newEditorTemplatesListId.getSelectedIndex()==0)
				myTemplatesList = digiRecptMyTemplatesDao.findDRMyTemplatesByEditorType(currentUser.getUserId(), OCConstants.DRAG_AND_DROP_EDITOR,startIndx,endIndx,tempName);
			else
				myTemplatesList = digiRecptMyTemplatesDao.findDRMyTemplatesByEditorType(currentUser.getUserId(), OCConstants.LEGACY_EDITOR,startIndx,endIndx,tempName);
			if(myTemplatesList==null)return;
			/*List<Long> tempIds=digiRecptMyTemplatesDao.getAllStarredTempByUserId(currentUser.getUserId());
				String IdsStr = Constants.STRING_NILL;
				for(DigitalReceiptMyTemplate mytemplates:myTemplatesList){
					if(!IdsStr.isEmpty()) IdsStr += ",";
					IdsStr += mytemplates.getMyTemplateId()+Constants.STRING_NILL;
				}
				if(!IdsStr.isEmpty()){
				List<Long> activeTempIds = digitalReceiptUserSettingsDao.findBy(accessAccIds,IdsStr);

				for(DigitalReceiptMyTemplate mytemplates:myTemplatesList){
					for (Long tempId : activeTempIds) {
						if(mytemplates.getMyTemplateId().equals(tempId)) mytemplates.setActive(true);
					}//for
				 }//for
				}*/
			
			
			
/** Cutycapt */			
/*			String IMG_CMD= PropertyUtil.getPropertyValueFromDB("imgGenerationCmd");
			 File htmlFile=null;		 
			 File imgFile=null;
			 String thumbnailImgPath	=USER_PARENT_DIR+File.separator+currentUser.getUserName()+USER_NEW_EDITOR_THUMBNAIL_DIR;
			 String thumbnailHtmlPath=USER_PARENT_DIR+File.separator+currentUser.getUserName()+USER_NEW_EDITOR_THUMBNAIL_DIR;
			 logger.info("before for loop :"+System.currentTimeMillis());
			 for(DigitalReceiptMyTemplate mytemplate : myTemplatesList){
			
				 String htmlFileNameWithPath=thumbnailHtmlPath+File.separator+"DR_Template_"+mytemplate.getMyTemplateId()+".html";
				 String imgNameWithPath=thumbnailImgPath+File.separator+"DR_Template_Img_"+mytemplate.getMyTemplateId()+".png";
				 
				 if(mytemplate.getContent()!=null||mytemplate.getAutoSaveHtmlContent()!=null){  

					 imgFile=new File(imgNameWithPath);
					 if(imgFile!=null && imgFile.exists())
						 imgFile.delete();
					 
					 htmlFile =new File(htmlFileNameWithPath); 
					 logger.info("htmlFile : "+htmlFile);
					 logger.info("imgNameWithPath : "+imgNameWithPath);
					 
					 
					 BufferedWriter bw = new BufferedWriter(new FileWriter(htmlFile));
					 if(mytemplate.getContent()!=null)
						 bw.write(mytemplate.getContent());
					 else
						 bw.write(mytemplate.getAutoSaveHtmlContent());
					 bw.close();
					 String command =IMG_CMD + htmlFile +" --out="+imgNameWithPath;
					 logger.info("command "+command);
					 logger.info("img started "+mytemplate.getMyTemplateId()+".png");
					 logger.info("before Processss :"+System.currentTimeMillis());
					 Process process = Runtime.getRuntime().exec(command);
					 process.waitFor();
					 logger.info("After Processss :"+System.currentTimeMillis());
					 logger.info("     done  "+mytemplate.getMyTemplateId()+".png");
					 if(htmlFile!= null && htmlFile.exists())
						 htmlFile.delete();	
				 }
			 }*/
			 logger.info("After for loop :"+System.currentTimeMillis());
			 String pathTojs= PropertyUtil.getPropertyValueFromDB("pathToPhantomjs");
			 String imgPath	=USER_PARENT_DIR+File.separator+currentUser.getUserName()+USER_NEW_EDITOR_THUMBNAIL_DIR;
			 File newEditorfolder = new File(imgPath);
			 if(!newEditorfolder.exists()) {
				 newEditorfolder.mkdir();
			 }
			 for (DigitalReceiptMyTemplate mytemplate : myTemplatesList) {
				 File newDir = new File(imgPath+File.separator+mytemplate.getName());;
				 if(!newDir.exists()){
					 newDir.mkdir();
				 }
				 boolean isFound = false;
				 String thumbnailPath=imgPath+File.separator+mytemplate.getName()+"/DR_Template_Img.png";
						final Vbox vbox = new Vbox();
						vbox.setSclass(vbStyle);
						vbox.setAlign("center");
						vbox.setHeight("315px");

						/*Label active;
						if(mytemplate.isActive()) {
							active = new Label();
							active.setParent(vbox);
							active.setValue("ACTIVE");
							active.setStyle("font-size:10px;font-weight:bold;color:#54a93a;border:1px solid #54a93a;padding:2px 0px;margin-right:73px;");
						} else*/ vbox.setStyle("padding-top:20px;");

						/**
						 * Commented following line to avoid cutycapt Thumbnails 
						 */
						/*templateImg = new Image();
						templateImg.setContent(new AImage(thumbnailPath));*/
						String data = null;
						if(newDir.exists() && newDir.isDirectory() && newDir.listFiles().length !=0) {
							data = new String(Files.readAllBytes(Paths.get(newDir.getPath()+File.separator+"DR_Template_Img.html")));
						}else {
							if(mytemplate.getContent()!=null) {	
								data = mytemplate.getContent();
							}else {
								data = mytemplate.getAutoSaveHtmlContent();
							}
						}
						try {
							File imgPathfile = new File(newDir.getPath()+File.separator+"DR_Template_Img.png");
							if(mytemplate.getContent()!=null) {
								String htmlContent = Utility.mergeTagsForPreviewAndTestMail(mytemplate.getContent().trim(), "preview");
								if(!data.trim().equals(htmlContent) || !imgPathfile.exists()) {
									if(imgPathfile.exists()) {
										imgPathfile.delete();
									}
									FileWriter fw = new FileWriter(newDir+"/DR_Template_Img.html");
									fw.write(htmlContent);
									fw.close();
									generateThumbnil(newDir,thumbnailPath, pathTojs);
								}
							}else if(mytemplate.getAutoSaveHtmlContent()!=null && !mytemplate.getAutoSaveHtmlContent().isEmpty()) {
								String htmlContent = Utility.mergeTagsForPreviewAndTestMail(mytemplate.getAutoSaveHtmlContent().trim(), "preview");
								if(!data.trim().equals(htmlContent.trim()) || !imgPathfile.exists()) {
									if(imgPathfile.exists()) {
										imgPathfile.delete();
									}
									FileWriter fw = new FileWriter(newDir+"/DR_Template_Img.html");
									fw.write(htmlContent);
									fw.close();
									generateThumbnil(newDir,thumbnailPath, pathTojs);
								}
							}
						}catch(Exception e) {
							logger.error("error creating image in dr receipt :"+e);
						}
						
						templateImg = new Image("/UserData/"+userName+USER_NEW_EDITOR_THUMBNAIL_DIR+File.separator+mytemplate.getName()+"/DR_Template_Img.png");
						//templateImg = new Image(custom_temp_default_preview);
										
						templateImg.setWidth("150px");
						templateImg.setHeight("220px");
						templateImg.setStyle("border: #CCCCCC 0.2px solid;");

						preview_Src = preview_Path+"/"+mytemplate.getName()+"/email.html";
						preview = new Toolbarbutton("");
						preview.setImage(previewIconPathStr);
						preview.setTooltiptext("Preview");
						preview.setAttribute("MyTemplate", mytemplate);
						preview.setAttribute("type", "Preview");
						preview.setTooltiptext("Preview");
						preview.addEventListener("onClick", this);
						
						testMailTb =new Toolbarbutton("");
						testMailTb.setImage(testmailIcon);
						testMailTb.setTooltiptext("Send Test Email");
						testMailTb.setAttribute("SendTestMail", mytemplate);
						testMailTb.setAttribute("type", "SendTestMail");
						testMailTb.setTooltiptext("Send Test Email");
						testMailTb.addEventListener("onClick", this);
						
						reNameTemplate = new Toolbarbutton();
						reNameTemplate.setImage(renameIcon);
						reNameTemplate.setTooltiptext("Rename");
						reNameTemplate.setAttribute("MyTemplate", mytemplate);
						reNameTemplate.setAttribute("type", "RenameTemplateName");
						reNameTemplate.addEventListener("onClick", this);
						
						copyTb = new Toolbarbutton();
						copyTb.setImage("/img/copy.gif");
						copyTb.setTooltiptext("Copy");
						copyTb.setAttribute("MyTemplate", mytemplate);
						copyTb.setAttribute("type", "CopyMyTemplate");
						copyTb.addEventListener("onClick", this);
						
						templateName = new Label(mytemplate.getName());
						templateName.setMaxlength(30);
						hbox = new Hbox();
						hbox.setWidth("215px");
						hbox.setPack("center");
						hbox.setSclass("st_hb");
						
						preview.setParent(hbox);
						testMailTb.setParent(hbox);
						reNameTemplate.setParent(hbox);
						copyTb.setParent(hbox);

						if(mytemplate.getJsonContent()!=null & mytemplate.getContent()!=null & mytemplate.getEditorType().equals(OCConstants.DRAG_AND_DROP_EDITOR)){

						edit = new Toolbarbutton();
						edit.setImage(useIconPathStr);
						edit.setTooltiptext("Edit");
						edit.setAttribute("Template", mytemplate);
						edit.setAttribute("type", "EditMyTemplate");
						edit.addEventListener("onClick", this);

						edit.setParent(hbox);
						}else if(mytemplate.getContent()!=null & mytemplate.getEditorType().equals(OCConstants.LEGACY_EDITOR)){

								edit = new Toolbarbutton();
								edit.setImage(useIconPathStr);
								edit.setTooltiptext("Edit");
								edit.setAttribute("Template", mytemplate);
								edit.setAttribute("type", "EditMyTemplate");
								edit.addEventListener("onClick", this);

								edit.setParent(hbox);
						}
						
						
						if(mytemplate.getAutoSaveJsonContent()!=null & mytemplate.getAutoSaveHtmlContent()!=null){

							draftTb = new Toolbarbutton();
							draftTb.setImage(draft);
							draftTb.setTooltiptext("Draft");
							draftTb.setAttribute("DraftMyTemplate", mytemplate);
							draftTb.setAttribute("type", "DraftTemplate"); 
							draftTb.addEventListener("onClick", this);

							draftTb.setParent(hbox);

						}
						
						 deleteTb = new Toolbarbutton();
						 deleteTb.setImage(deleteIconPathStr);
						 deleteTb.setTooltiptext("Delete");
						 deleteTb.setAttribute("MyTemplate", mytemplate);
						 deleteTb.setAttribute("type", "delete"); 
						 deleteTb.addEventListener("onClick", this);

						 deleteTb.setParent(hbox);

						
						templateImg.setParent(vbox);
						templateName.setParent(vbox);
						hbox.setParent(vbox);
						vbox.setParent(templListDivId);
					

				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Exception ::" , e);;
			}
				 
				 
/*				 Vbox vbox = new Vbox();
				 vbox.setSclass(vbStyle);
				 vbox.setAlign("center");
				 vbox.setHeight("315px");
				 logger.info("Thumbnail path "+thumbnailPath);
				 templateImg = new Image();
				 templateImg.setContent(new AImage(thumbnailPath));
				 templateImg.setWidth("150px");
				 templateImg.setHeight("220px");
				 if(!folderName.equals(OCConstants.Trash)){
					 if(mytemplate.isActive()) {
						  Label active; 
							active = new Label();
							active.setParent(vbox);
							active.setValue("ACTIVE");
							active.setStyle("font-size:10px;font-weight:bold;color:#54a93a;border:1px solid #54a93a;padding-top:2px;padding-bottom:2px;margin-right:73px;");
						}else vbox.setStyle("padding-top:20px;");

					 preview = new Toolbarbutton();
					 preview.setImage(previewIconPathStr);
					 preview.setAttribute(MyTemplate, mytemplate);
					 preview.setAttribute("type", "Preview");
					 preview.setTooltiptext("Preview");
					 preview.addEventListener("onClick", this);

					 editMoveTemplateName = new Toolbarbutton();
					 editMoveTemplateName.setImage(renameIcon);
					 editMoveTemplateName.setTooltiptext("Edit Template Setting");
					 editMoveTemplateName.setAttribute(MyTemplate, mytemplate);
					 editMoveTemplateName.setAttribute("type", "EditTemplateSettings");
					 editMoveTemplateName.addEventListener("onClick", this);


					 copyTb = new Toolbarbutton();
					 copyTb.setImage("/img/copy.gif");
					 copyTb.setTooltiptext("Copy/Move");
					 copyTb.setAttribute(MyTemplate, mytemplate);
					 copyTb.setAttribute("type", "CopyMyTemplate");
					 copyTb.addEventListener("onClick", this);


					 templateName = new Label(mytemplate.getName());
					 templateName.setMaxlength(30);
					 hbox = new Hbox();
					 hbox.setWidth("215px");
					 hbox.setPack("center");
					 hbox.setSclass("st_hb");

					 hbox1 = new Hbox();
					 hbox1.setWidth("5px");
					 templateName.setParent(hbox1);

					 preview.setParent(hbox);

					 if(mytemplate.getContent()!=null & mytemplate.getJsonContent()!=null){
						 starTb = new Toolbarbutton();
						 starTb.setImage("/img/theme/star.png");
						 starTb.setTooltiptext("Star");
						 for(Long ids:tempIds){
						 if(mytemplate.getMyTemplateId().equals(ids)){
							 starTb.setImage("/img/theme/filled_stared.png");
							 starTb.setTooltiptext("Unstar");
						    }
						 }
						 starTb.setAttribute(MyTemplate, mytemplate);
						 starTb.setAttribute("type", "starredTemplate");
						 starTb.addEventListener("onClick", this);
						 starTb.setParent(hbox);

						 edit = new Toolbarbutton();
						 edit.setImage(useIconPathStr);
						 edit.setTooltiptext("Edit");
						 edit.setAttribute(MyTemplate, mytemplate);
						 edit.setAttribute("type", "EditMyTemplate");
						 edit.addEventListener("onClick", this);


						 preview.setParent(hbox);

						 testMailTb = new Toolbarbutton();
						 testMailTb.setImage("/img/email_page.gif");
						 testMailTb.setTooltiptext("Send Test Email");
						 testMailTb.setAttribute(MyTemplate, mytemplate);
						 testMailTb.setAttribute("type", "SendTestMail");
						 testMailTb.addEventListener("onClick", this);

						 //editMoveTemplateName.setStyle("padding: 0px 0px 0px 6px;");
						 testMailTb.setParent(hbox);
						 edit.setParent(hbox);
					 }
					 copyTb.setParent(hbox);
					 editMoveTemplateName.setParent(hbox);



					 if(mytemplate.getAutoSaveJsonContent()!=null & mytemplate.getAutoSaveHtmlContent()!=null){

						 draftTb = new Toolbarbutton();
						 draftTb.setImage(draft);
						 draftTb.setTooltiptext("DRAFT");
						 draftTb.setAttribute(MyTemplate, mytemplate);
						 draftTb.setAttribute("type", "DraftTemplate"); 
						 draftTb.addEventListener("onClick", this);

						 draftTb.setParent(hbox);

					 }

					 deleteTb = new Toolbarbutton();
					 deleteTb.setImage(deleteIconPathStr);
					 deleteTb.setTooltiptext("Delete DR templates");
					 deleteTb.setAttribute(MyTemplate, mytemplate);
					 deleteTb.setAttribute("type", "deleteTemporary"); 
					 deleteTb.addEventListener("onClick", this);

					 deleteTb.setParent(hbox);

					 templateImg.setParent(vbox);

					 hbox1.setParent(vbox);
					 hbox.setParent(vbox);
					 vbox.setParent(templListDivId);
				 }
				 else{
					 templateName = new Label(mytemplate.getName());
					 templateName.setMaxlength(30);
					 hbox = new Hbox();
					 hbox.setWidth("215px");
					 hbox.setPack("center");
					 hbox.setSclass("st_hb");
					 hbox1 = new Hbox();
					 hbox1.setWidth("5px");
					 templateImg.setParent(vbox);
					 templateName.setParent(hbox1);
					 hbox1.setParent(vbox);
					 restoredTb = new Toolbarbutton();
					 restoredTb.setImage("/img/icons/icon_undo.png");
					 restoredTb.setTooltiptext("Restore");
					 restoredTb.setAttribute(MyTemplate, mytemplate);
					 restoredTb.setAttribute("type", "restoreTemplate");
					 restoredTb.addEventListener("onClick", this);
					 restoredTb.setParent(hbox);
					 hbox.setParent(vbox);

					 deleteTb = new Toolbarbutton();
					 deleteTb.setImage(deleteIconPathStr);
					 deleteTb.setTooltiptext("Delete DR templates permanently");
					 deleteTb.addEventListener("onClick",this );
					 deleteTb.setAttribute(MyTemplate, mytemplate);
					 deleteTb.setAttribute("type", "deletePermanently");
					 deleteTb.setParent(hbox);
					 hbox.setParent(vbox);
					 vbox.setParent(templListDivId);

				 }
			 }

		} 
		catch (Exception e) {
			logger.error("Exception ::" , e);;
		}*/


	
		
/*		if(folderName==null) return;
		preview_Path =  appUrl + "/UserData/"+currentUser.getUserName()+userTemplatesDirectory+File.separator+folderName;

		final String useIconPathStr = "/images/Edit_icn.png";

		final String deleteIconPathStr = "/img/theme/action_delete.gif";
		final String draft = "/img/theme/draft_icon.png";
		final String renameIcon = "/img/theme/rename.png";
		final String moveIcon = "/img/theme/move.png";

		try {

			Image templateImg = null;

			Toolbarbutton preview,edit,deleteTb,shareTb,draftTb;
			Label templateName;
			Hbox hbox;
			String preview_Src ="";
			String previewIconPathStr = "/img/theme/preview_icon.png";
			String vbStyle = "templatePrviewVb";
			String custom_temp_default_preview = "/img/custom_temp_preview.gif";

			Listitem selLi = newEditorTemplatesListId.getSelectedItem();

			MyFolders myfolders = (MyFolders)selLi.getValue();
			logger.info("selLi   --------:"+selLi.getValue());

			MyTemplatesDao myTemplatesDao = (MyTemplatesDao)SpringUtil.getBean("myTemplatesDao");

			List<DigitalReceiptMyTemplate> myTemplatesList = digiRecptMyTemplatesDao.findDRMyTemplatesOrgID(currentUser.getUserId(), myfolders.getFolderName(), OCConstants.DRAG_AND_DROP_EDITOR);

			logger.info("Template list size "+myTemplatesList.size());

			String thumbnailImgPath	=USER_PARENT_DIR+File.separator+currentUser.getUserName()+USER_NEW_EDITOR_THUMBNAIL_DIR;
			String thumbnailHtmlPath=USER_PARENT_DIR+File.separator+currentUser.getUserName()+USER_NEW_EDITOR_THUMBNAIL_DIR;
			String IMG_CMD= PropertyUtil.getPropertyValueFromDB("imgGenerationCmd");
			File htmlFile=null;		
			File imgFile=null;
			for(DigitalReceiptMyTemplate mytemplate : myTemplatesList){
				String htmlFileNameWithPath=thumbnailHtmlPath+"/DR_Template_"+mytemplate.getMyTemplateId()+".html";
				String imgNameWithPath=thumbnailImgPath+"/DR_Template_Img_"+mytemplate.getMyTemplateId()+".png";

				if(mytemplate.getContent()!=null||mytemplate.getAutoSaveHtmlContent()!=null){  
					imgFile=new File(imgNameWithPath);
					if(imgFile!=null && imgFile.exists())
						imgFile.delete();
					htmlFile =new File(htmlFileNameWithPath); 
					BufferedWriter bw = new BufferedWriter(new FileWriter(htmlFile));
					if(mytemplate.getContent()!=null)
						bw.write(mytemplate.getContent());
					else
						bw.write(mytemplate.getAutoSaveHtmlContent());
					bw.close();
					String command =IMG_CMD + htmlFile +" --out="+imgNameWithPath;
					logger.info("command "+command);
					logger.info("img started "+mytemplate.getMyTemplateId()+".png");
					Process process = Runtime.getRuntime().exec(command);
					process.waitFor();
					logger.info("     done  "+mytemplate.getMyTemplateId()+".png");
					if(htmlFile!= null && htmlFile.exists())
						htmlFile.delete();	
				}
				else
					continue; 
			}
			String imgPath	=USER_PARENT_DIR+File.separator+currentUser.getUserName()+USER_NEW_EDITOR_THUMBNAIL_DIR;
			for (DigitalReceiptMyTemplate mytemplate : myTemplatesList) {
				String thumbnailPath=imgPath+"/DR_Template_Img_"+mytemplate.getMyTemplateId()+".png";


				List<SharedZone> sharedZoneList = sharedZoneDao.findAllItems(mytemplate.getMyTemplateId().toString(), OCConstants.DnD_DIGITAL_RECEIPT_TEMPLATES);

				boolean currentTemplateShared= false;
				for(SharedZone sharedZonelist : sharedZoneList){
					//Set<Users> users = sharedZoneList.get(0).getUserSet();
					Set<Users> users = sharedZonelist.getUserSet();
					for(Users user:users){
						logger.info("user.getUserId()="+user.getUserId()+"currentUser.getUserId()="+currentUser.getUserId());
						if((user.getUserId()).equals(currentUser.getUserId())){ 
							logger.info("currentTemplateShared :");
							currentTemplateShared=true;
						}	
					}
				}
				if(currentTemplateShared){
					logger.info("selLi   --------:"+selLi.getValue());


					final Vbox vbox = new Vbox();
					vbox.setSclass(vbStyle);
					vbox.setAlign("center");
					vbox.setHeight("315px");

					Label active;
					if(mytemplate.isActive()) {
						active = new Label();
						active.setParent(vbox);
						active.setValue("ACTIVE");
						active.setStyle("font-size:10px;font-weight:bold;color:#54a93a;border:1px solid #54a93a;padding:2px 0px;margin-right:73px;");
					} else vbox.setStyle("padding-top:20px;");

					templateImg = new Image();
					templateImg.setContent(new AImage(thumbnailPath));
					templateImg.setWidth("150px");
					templateImg.setHeight("220px");

					preview_Src = preview_Path+"/"+mytemplate.getName()+"/email.html";
					preview = new Toolbarbutton("");
					preview.setImage(previewIconPathStr);
					preview.setTooltiptext("Preview");
					preview.setAttribute("MyTemplate", mytemplate);
					preview.setAttribute("type", "Preview");
					preview.setTooltiptext("Preview");
					preview.addEventListener("onClick", this);

					edit = new Toolbarbutton();
					edit.setImage(useIconPathStr);
					edit.setTooltiptext("Edit");
					edit.setAttribute("Template", mytemplate);
					edit.setAttribute("type", "EditMyTemplate");
					edit.addEventListener("onClick", this);

					templateName = new Label(mytemplate.getName());
					templateName.setMaxlength(30);
					hbox = new Hbox();
					hbox.setWidth("215px");
					hbox.setPack("center");
					hbox.setSclass("st_hb");

					preview.setParent(hbox);
					edit.setParent(hbox);

					if(mytemplate.getAutoSaveJsonContent()!=null & mytemplate.getAutoSaveHtmlContent()!=null){

						draftTb = new Toolbarbutton();
						draftTb.setImage(draft);
						draftTb.setTooltiptext("DRAFT");
						draftTb.setAttribute("DraftMyTemplate", mytemplate);
						draftTb.setAttribute("type", "DraftTemplate"); 
						draftTb.addEventListener("onClick", this);

						draftTb.setParent(hbox);

					}

					templateImg.setParent(vbox);
					templateName.setParent(vbox);
					hbox.setParent(vbox);
					vbox.setParent(templListDivId);
				}

			}
			logger.info("folder"+selLi);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);;
		}*/


	}
	


	/*	public void onSelect$newEditorTemplatesListId(){

		selectfolder = newEditorTemplatesListId.getSelectedIndex();
		Components.removeAllChildren(templListDivId);

		File category = null;
		File[] templateList = null;
		String templatePrevStr = null;
		String appUrl = PropertyUtil.getPropertyValue("ApplicationUrl");
		String preview_Path = null;

		String userTemplatesDirectory = PropertyUtil.getPropertyValue(USER_DR_NEW_EDITOR_TEMPLATES_DIRECTORY);
		String folderName=null;

		try{

			if(newEditorTemplatesListId==null||newEditorTemplatesListId.getSelectedItem()==null || newEditorTemplatesListId.getSelectedItem().getLabel()==null)
				logger.info("newEditorTemplatesListId :"+newEditorTemplatesListId +" newEditorTemplatesListId.getSelectedItem(): "+newEditorTemplatesListId.getSelectedItem()+"newEditorTemplatesListId.getSelectedItem().getLabel() :"+newEditorTemplatesListId.getSelectedItem().getLabel());
			folderName = newEditorTemplatesListId.getSelectedItem().getLabel();
		} 
		catch (ClassCastException e1) {
			// TODO: handle exception
			logger.error("ClassCastException ::" , e1);
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("newEditorTemplatesListId.getSelectedItem() Trace");
			newEditorTemplatesListId.setSelectedIndex(0);
			onSelect$newEditorTemplatesListId();
		}

		if(folderName==null) return;
		preview_Path =  appUrl + "/UserData/"+currentUser.getUserName()+userTemplatesDirectory+File.separator+folderName;

		final String useIconPathStr = "/images/Edit_icn.png";

		final String share2 = "/img/theme/share2.png";
		final String deleteIconPathStr = "/img/theme/action_delete.gif";

		final String draft = "/img/theme/draft_icon.png";

		final String renameIcon = "/img/theme/rename.png";
		final String moveIcon = "/img/theme/move.png";

		if(currentUserRole.equals(drAdmin)){

			try {

				try {

					Listitem selList = newEditorTemplatesListId.getSelectedItem();

					if(((Label)((Listcell)selList.getFirstChild()).getFirstChild()).getValue().equals(OCConstants.DRAG_DROP_EDITOR_DR_TEMPLATES_FOLDER_DEFAULT)||((Label)((Listcell)selList.getFirstChild()).getFirstChild()).getValue().equals(OCConstants.DRAG_DROP_EDITOR_DR_TEMPLATES_FOLDER_DRAFT)){
					}else{
						manageNewEditorFolderImg.setParent(selList.getFirstChild());
						manageNewEditorFolderImg.setAttribute("parentDir", Constants.NEWEDITOR_TEMPLATES_PARENT);
					}

					Image templateImg = null;

					Toolbarbutton preview,edit,deleteTb,shareTb,copyTb,testMailTb,draftTb,editTemplateName,moveTemplate,editMoveTemplateName;
					Label templateName;
					Hbox hbox,hbox1;
					String preview_Src ="";
					String previewIconPathStr = "/img/theme/preview_icon.png";
					String vbStyle = "templatePrviewVb";
					String custom_temp_default_preview = "/img/custom_temp_preview.gif";

					Listitem selLi = newEditorTemplatesListId.getSelectedItem();

					MyFolders myfolders = (MyFolders)selLi.getValue();
					logger.info("selLi   --------:"+selLi.getValue());

					MyTemplatesDao myTemplatesDao = (MyTemplatesDao)SpringUtil.getBean("myTemplatesDao");

					List<DigitalReceiptMyTemplate> myTemplatesList = digiRecptMyTemplatesDao.findDRMyTemplatesByEditorType(currentUser.getUserId(), myfolders.getFolderName(), OCConstants.DRAG_AND_DROP_EDITOR);
					logger.info("Template list size "+myTemplatesList.size());
					String IdsStr = Constants.STRING_NILL;
					for(DigitalReceiptMyTemplate mytemplates:myTemplatesList){

						if(!IdsStr.isEmpty()) IdsStr += ",";

						IdsStr += mytemplates.getMyTemplateId()+Constants.STRING_NILL;

					}


					List<Long> activeTempIds = digitalReceiptUserSettingsDao.findBy(currentUser.getUserOrganization().getUserOrgId(),IdsStr  );

					for(DigitalReceiptMyTemplate mytemplates:myTemplatesList){

						for (Long tempId : activeTempIds) {

							if(mytemplates.getMyTemplateId().equals(tempId)) mytemplates.setActive(true);

						}//for

					}//for

					String thumbnailImgPath	=USER_PARENT_DIR+File.separator+currentUser.getUserName()+USER_NEW_EDITOR_THUMBNAIL_DIR;
					String thumbnailHtmlPath=USER_PARENT_DIR+File.separator+currentUser.getUserName()+USER_NEW_EDITOR_THUMBNAIL_DIR;
					String IMG_CMD= PropertyUtil.getPropertyValueFromDB("imgGenerationCmd");
					File htmlFile=null;		 
					File imgFile=null;
					for(DigitalReceiptMyTemplate mytemplate : myTemplatesList){
						String htmlFileNameWithPath=thumbnailHtmlPath+"/DR_Template_"+mytemplate.getMyTemplateId()+".html";
						String imgNameWithPath=thumbnailImgPath+"/DR_Template_Img_"+mytemplate.getMyTemplateId()+".png";
						if(mytemplate.getContent()!=null||mytemplate.getAutoSaveHtmlContent()!=null){  
							imgFile=new File(imgNameWithPath);
	                		if(imgFile!=null && imgFile.exists())
	                			imgFile.delete();
							 htmlFile =new File(htmlFileNameWithPath); 
							 BufferedWriter bw = new BufferedWriter(new FileWriter(htmlFile));
							    if(mytemplate.getContent()!=null)
							        bw.write(mytemplate.getContent());
							    else
							    	 bw.write(mytemplate.getAutoSaveHtmlContent());
							     bw.close();
							   String command =IMG_CMD + htmlFile +" --out="+imgNameWithPath;
						            logger.info("command "+command);
						            logger.info("img started "+mytemplate.getMyTemplateId()+".png");
						         	Process process = Runtime.getRuntime().exec(command);
						         	process.waitFor();
						         	logger.info("     done  "+mytemplate.getMyTemplateId()+".png");
						         	if(htmlFile!= null && htmlFile.exists())
						         		htmlFile.delete();	
						}
							 else
								 continue; 
						}

					String imgPath	=USER_PARENT_DIR+File.separator+currentUser.getUserName()+USER_NEW_EDITOR_THUMBNAIL_DIR;
					for (DigitalReceiptMyTemplate mytemplate : myTemplatesList) {
						SharedZone sharedZone = sharedZoneDao.findItem(currentUser.getUserId().toString(),mytemplate.getMyTemplateId().toString(), OCConstants.DnD_DIGITAL_RECEIPT_TEMPLATES);
						boolean isFound = false;
						String thumbnailPath=imgPath+"/DR_Template_Img_"+mytemplate.getMyTemplateId()+".png";
						Vbox vbox = new Vbox();
						vbox.setSclass(vbStyle);
						vbox.setAlign("center");
						vbox.setHeight("315px");
						logger.info("Thumbnail path "+thumbnailPath);
						Label active;
						if(mytemplate.isActive()) {
							active = new Label();
							active.setParent(vbox);
							active.setValue("ACTIVE");
							active.setStyle("font-size:10px;font-weight:bold;color:#54a93a;border:1px solid #54a93a;padding:2px 0px;margin-right:73px;");
						}else vbox.setStyle("padding-top:20px;");
						templateImg = new Image();
						templateImg.setContent(new AImage(thumbnailPath));
						templateImg.setWidth("150px");
						templateImg.setHeight("220px");
						shareTb = new Toolbarbutton();
						shareTb.setImage(share2);
						shareTb.setTooltiptext("SHARE");
						shareTb.setAttribute("SharedTemplate", sharedZone);
						shareTb.setAttribute("SharedMyTemplate", mytemplate);
						shareTb.setAttribute("type", "ShareTemplate");
						shareTb.addEventListener("onClick", this);

						preview = new Toolbarbutton();
						preview.setImage(previewIconPathStr);
						preview.setAttribute("MyTemplate", mytemplate);
						preview.setAttribute("type", "Preview");
						preview.setTooltiptext("Preview");
						preview.addEventListener("onClick", this);

						editMoveTemplateName = new Toolbarbutton();
						editMoveTemplateName.setImage(renameIcon);
						editMoveTemplateName.setTooltiptext("Rename/Move Template");
						editMoveTemplateName.setAttribute("Template", mytemplate);
						editMoveTemplateName.setAttribute("SharedZoneTemplate", sharedZone);
						editMoveTemplateName.setAttribute("type", "RenameMoveTemplate");
						editMoveTemplateName.addEventListener("onClick", this);

						copyTb = new Toolbarbutton();
						copyTb.setImage("/img/copy.gif");
						copyTb.setTooltiptext("Copy");
						copyTb.setAttribute("CopyTemplate", mytemplate);
						copyTb.setAttribute("type", "CopyMyTemplate");
						copyTb.addEventListener("onClick", this);

						templateName = new Label(mytemplate.getName());
						templateName.setMaxlength(30);
						hbox = new Hbox();
						hbox.setWidth("215px");
						hbox.setPack("center");
						hbox.setSclass("st_hb");

						hbox1 = new Hbox();
						hbox1.setWidth("5px");
						templateName.setParent(hbox1);


						if(mytemplate.getContent()!=null & mytemplate.getJsonContent()!=null){

							edit = new Toolbarbutton();
							edit.setImage(useIconPathStr);
							edit.setTooltiptext("Edit");
							edit.setAttribute("Template", mytemplate);
							edit.setAttribute("SharedZoneTemplate", sharedZone);
							edit.setAttribute("type", "EditMyTemplate");
							edit.addEventListener("onClick", this);

							preview.setParent(hbox);

							testMailTb = new Toolbarbutton();
							testMailTb.setImage("/img/email_page.gif");
							testMailTb.setTooltiptext("Send Test Mail");
							testMailTb.setAttribute("SendTestMail", mytemplate);
							testMailTb.setAttribute("type", "SendTestMail");
							testMailTb.addEventListener("onClick", this);

							editMoveTemplateName.setParent(hbox1);

							testMailTb.setParent(hbox);
							edit.setParent(hbox);

							copyTb.setParent(hbox);
						} else hbox.setStyle("padding-top:14px;padding-bottom:8px;");

						if(mytemplate.getAutoSaveJsonContent()!=null & mytemplate.getAutoSaveHtmlContent()!=null){

							draftTb = new Toolbarbutton();
							draftTb.setImage(draft);
							draftTb.setTooltiptext("DRAFT");
							draftTb.setAttribute("DraftMyTemplate", mytemplate);
							draftTb.setAttribute("type", "DraftTemplate"); 
							draftTb.addEventListener("onClick", this);

							draftTb.setParent(hbox);

						}

						deleteTb = new Toolbarbutton();
						deleteTb.setImage(deleteIconPathStr);
						deleteTb.setTooltiptext("Delete this from DR templates");

	 *//**
	 * add onClick event listener to delete when clicked
	 *//*
						deleteTb.addEventListener("onClick", new EventListener() {

							@Override
							public void onEvent(Event event) throws Exception {//how we can allow the othr user's template deletion
								try {

									//Concurrence issue
									boolean templateInUse= digiRecptMyTemplatesDao.isPowerUserUsingTemplate(mytemplate.getMyTemplateId());
									if(templateInUse){
										MessageUtil.setMessage("Delete action is not allowed as this template is currently having used by another user.","green", "top");
										return;
									}

									boolean isConfigured = digitalReceiptUserSettingsDao.isTemplateConfigured(mytemplate.getMyTemplateId());

									if(isConfigured){

										MessageUtil.setMessage("Unable to delete the template because it is configured to Digital Receipt.", "color:red", "TOP");

									}
									else{

										int confirm = Messagebox.show("Are you sure you want to delete the template - "
												+ mytemplate.getName() + "?",
												"Confirm", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
										if(confirm != 1){
											return;
										}

										templListDivId.removeChild(vbox);
										digiRecptMyTemplatesDaoForDML.deleteByUserIdAndName(currentUser.getUserId(), mytemplate.getName(), mytemplate.getFolderName(), OCConstants.DRAG_AND_DROP_EDITOR);
										MessageUtil.setMessage("Template '" + mytemplate.getName() + "' deleted successfully.", "green", "top");	
									}
								} catch (Exception e) {
									// TODO Auto-generated catch block
									logger.error("Exception ::" , e);;
								}

							}
						});
						deleteTb.setParent(hbox);

						if(mytemplate.getContent()!=null & mytemplate.getJsonContent()!=null){
							shareTb.setParent(hbox);
						}
						templateImg.setParent(vbox);

						hbox1.setParent(vbox);
						hbox.setParent(vbox);
						vbox.setParent(templListDivId);

					}
					logger.info("folder"+selLi);

				} catch (Exception e) {
					// TODO Auto-generated catch block
					logger.error("Exception ::" , e);;
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Exception ::" , e);;
			}

		}
		else if(drPower.equals(drPower)){

			try {

				Image templateImg = null;

				Toolbarbutton preview,edit,deleteTb,shareTb,draftTb;
				Label templateName;
				Hbox hbox;
				String preview_Src ="";
				String previewIconPathStr = "/img/theme/preview_icon.png";
				String vbStyle = "templatePrviewVb";
				String custom_temp_default_preview = "/img/custom_temp_preview.gif";

				Listitem selLi = newEditorTemplatesListId.getSelectedItem();

				MyFolders myfolders = (MyFolders)selLi.getValue();
				logger.info("selLi   --------:"+selLi.getValue());

				MyTemplatesDao myTemplatesDao = (MyTemplatesDao)SpringUtil.getBean("myTemplatesDao");

				List<DigitalReceiptMyTemplate> myTemplatesList = digiRecptMyTemplatesDao.findDRMyTemplatesOrgID(currentUser.getUserOrganization().getUserOrgId(), myfolders.getFolderName(), OCConstants.DRAG_AND_DROP_EDITOR);

				logger.info("Template list size "+myTemplatesList.size());

				String IdsStr = Constants.STRING_NILL;
				for(DigitalReceiptMyTemplate mytemplates:myTemplatesList){

					if(!IdsStr.isEmpty()) IdsStr += ",";

					IdsStr += mytemplates.getMyTemplateId()+Constants.STRING_NILL;

				}

				List<Long> activeTempIds = digitalReceiptUserSettingsDao.findBy(currentUser.getUserOrganization().getUserOrgId(),IdsStr  );

				for(DigitalReceiptMyTemplate mytemplates:myTemplatesList){

					for (Long tempId : activeTempIds) {

						if(mytemplates.getMyTemplateId().equals(tempId)) mytemplates.setActive(true);

					}//for

				}//for

				String thumbnailImgPath	=USER_PARENT_DIR+File.separator+currentUser.getUserName()+USER_NEW_EDITOR_THUMBNAIL_DIR;
				String thumbnailHtmlPath=USER_PARENT_DIR+File.separator+currentUser.getUserName()+USER_NEW_EDITOR_THUMBNAIL_DIR;
				String IMG_CMD= PropertyUtil.getPropertyValueFromDB("imgGenerationCmd");
				File htmlFile=null;		
				File imgFile=null;
				for(DigitalReceiptMyTemplate mytemplate : myTemplatesList){
					String htmlFileNameWithPath=thumbnailHtmlPath+"/DR_Template_"+mytemplate.getMyTemplateId()+".html";
					String imgNameWithPath=thumbnailImgPath+"/DR_Template_Img_"+mytemplate.getMyTemplateId()+".png";

					if(mytemplate.getContent()!=null||mytemplate.getAutoSaveHtmlContent()!=null){  
						imgFile=new File(imgNameWithPath);
	                		if(imgFile!=null && imgFile.exists())
	                			imgFile.delete();
							 htmlFile =new File(htmlFileNameWithPath); 
							 BufferedWriter bw = new BufferedWriter(new FileWriter(htmlFile));
							    if(mytemplate.getContent()!=null)
							        bw.write(mytemplate.getContent());
							    else
							    	 bw.write(mytemplate.getAutoSaveHtmlContent());
							     bw.close();
							   String command =IMG_CMD + htmlFile +" --out="+imgNameWithPath;
						            logger.info("command "+command);
						            logger.info("img started "+mytemplate.getMyTemplateId()+".png");
						         	Process process = Runtime.getRuntime().exec(command);
						         	process.waitFor();
						         	logger.info("     done  "+mytemplate.getMyTemplateId()+".png");
						         	if(htmlFile!= null && htmlFile.exists())
						         		htmlFile.delete();	
						}
							 else
								 continue; 
						}
					String imgPath	=USER_PARENT_DIR+File.separator+currentUser.getUserName()+USER_NEW_EDITOR_THUMBNAIL_DIR;
				for (DigitalReceiptMyTemplate mytemplate : myTemplatesList) {
					String thumbnailPath=imgPath+"/DR_Template_Img_"+mytemplate.getMyTemplateId()+".png";


					List<SharedZone> sharedZoneList = sharedZoneDao.findAllItems(mytemplate.getMyTemplateId().toString(), OCConstants.DnD_DIGITAL_RECEIPT_TEMPLATES);

					boolean currentTemplateShared= false;
					for(SharedZone sharedZonelist : sharedZoneList){
						//Set<Users> users = sharedZoneList.get(0).getUserSet();
						Set<Users> users = sharedZonelist.getUserSet();
						for(Users user:users){
							logger.info("user.getUserId()="+user.getUserId()+"currentUser.getUserId()="+currentUser.getUserId());
							if((user.getUserId()).equals(currentUser.getUserId())){ 
								logger.info("currentTemplateShared :");
								currentTemplateShared=true;
							}	
						}
					}
					if(currentTemplateShared){
						logger.info("selLi   --------:"+selLi.getValue());


						final Vbox vbox = new Vbox();
						vbox.setSclass(vbStyle);
						vbox.setAlign("center");
						vbox.setHeight("315px");

						Label active;
						if(mytemplate.isActive()) {
							active = new Label();
							active.setParent(vbox);
							active.setValue("ACTIVE");
							active.setStyle("font-size:10px;font-weight:bold;color:#54a93a;border:1px solid #54a93a;padding:2px 0px;margin-right:73px;");
						} else vbox.setStyle("padding-top:20px;");

						templateImg = new Image();
						templateImg.setContent(new AImage(thumbnailPath));
						templateImg.setWidth("150px");
						templateImg.setHeight("220px");

						preview_Src = preview_Path+"/"+mytemplate.getName()+"/email.html";
						preview = new Toolbarbutton("");
						preview.setImage(previewIconPathStr);
						preview.setTooltiptext("Preview");
						preview.setAttribute("MyTemplate", mytemplate);
						preview.setAttribute("type", "Preview");
						preview.setTooltiptext("Preview");
						preview.addEventListener("onClick", this);

						edit = new Toolbarbutton();
						edit.setImage(useIconPathStr);
						edit.setTooltiptext("Edit");
						edit.setAttribute("Template", mytemplate);
						edit.setAttribute("type", "EditMyTemplate");
						edit.addEventListener("onClick", this);

						templateName = new Label(mytemplate.getName());
						templateName.setMaxlength(30);
						hbox = new Hbox();
						hbox.setWidth("215px");
						hbox.setPack("center");
						hbox.setSclass("st_hb");

						preview.setParent(hbox);
						edit.setParent(hbox);

						if(mytemplate.getAutoSaveJsonContent()!=null & mytemplate.getAutoSaveHtmlContent()!=null){

							draftTb = new Toolbarbutton();
							draftTb.setImage(draft);
							draftTb.setTooltiptext("DRAFT");
							draftTb.setAttribute("DraftMyTemplate", mytemplate);
							draftTb.setAttribute("type", "DraftTemplate"); 
							draftTb.addEventListener("onClick", this);

							draftTb.setParent(hbox);

						}

						 templateImg.setParent(vbox);
						 templateName.setParent(vbox);
						 hbox.setParent(vbox);
						 vbox.setParent(templListDivId);
					}

				}
				logger.info("folder"+selLi);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Exception ::" , e);;
			}

		}

	}
	  */
	/*public void onSelect$newEditorTemplatesListId__Original(){

		Components.removeAllChildren(templListDivId);
		SharedZoneDao sharedZoneDao = (SharedZoneDao) SpringUtil.getBean("sharedZoneDao");
		SharedZoneDaoForDML sharedZoneDaoForDML = (SharedZoneDaoForDML) SpringUtil.getBean("sharedZoneDaoForDML");

		File category = null;
		File[] templateList = null;
		List<File[]> templateFileList = new ArrayList<File[]>();
		String templatePrevStr = null;
		String appUrl = PropertyUtil.getPropertyValue("ApplicationUrl");
		String preview_Path = null;

		String userTemplatesDirectory = PropertyUtil.getPropertyValue(USER_DR_NEW_EDITOR_TEMPLATES_DIRECTORY);

		String folderName = newEditorTemplatesListId.getSelectedItem().getLabel();
		templateFileList.clear();
		String myTempPathStr = PropertyUtil.getPropertyValue("usersParentDirectory")+
				File.separator+ currentUser.getUserName()+userTemplatesDirectory+File.separator+folderName;
		logger.debug("myTempPathStr ===>"+myTempPathStr);
		category= new File(myTempPathStr);
		templateList = category.listFiles();

		preview_Path =  appUrl + "/UserData/"+currentUser.getUserName()+userTemplatesDirectory+File.separator+folderName;

		Image templateImg = null;

		Toolbarbutton preview,edit,deleteTb,shareTb;
		Label templateName;
		Hbox hbox;
		String preview_Src ="";
		final String useIconPathStr = "/images/Edit_icn.png";
		final String share2 = "/images/theme/share2.png";

		String previewIconPathStr = "/img/theme/preview_icon.png";
		final String deleteIconPathStr = "/img/theme/action_delete.gif";
		String vbStyle = "templatePrviewVb";
		String custom_temp_default_preview = "/img/custom_temp_preview.gif";

		try {
			Listitem selLi = newEditorTemplatesListId.getSelectedItem();
			List<DigitalReceiptMyTemplate> myTemplates = (List<DigitalReceiptMyTemplate>)selLi.getValue();
			logger.info("selLi   --------:"+selLi.getValue());

			for (DigitalReceiptMyTemplate mytemplate : myTemplates) {

				List<SharedZone> sharedZoneList = sharedZoneDao.findAllItems(mytemplate.getMyTemplateId().toString(), OCConstants.DnD_DIGITAL_RECEIPT_TEMPLATES);
				SharedZone sharedZone=null;
				if(sharedZoneList.size()>0) sharedZone =sharedZoneList.get(0);

				boolean isFound = false;
				for(File templateLst:templateList) {

					final File template = templateLst;

					if(!mytemplate.getName().equals(template.getName())) {
						logger.info("myTemplate ::"+mytemplate.getName()+" template.getName() "+template.getName());
						continue;
					}

					isFound = true;
					if(template.isDirectory()){

						logger.debug(template.getPath());
						final Vbox vbox = new Vbox();
						vbox.setSclass(vbStyle);
						vbox.setAlign("center");
						vbox.setHeight("315px");

						if(! new File(template.getPath()+File.separator+"preview.gif").exists()) {
							templateImg = new Image(custom_temp_default_preview);
						}
						else{
							templateImg = new Image(templatePrevStr+"/"+template.getName()+"/preview.gif");
						}

						shareTb = new Toolbarbutton();
						shareTb.setImage(useIconPathStr);
						shareTb.setTooltiptext("SHARE");
						shareTb.setAttribute("SharedTemplate", sharedZone);
						shareTb.setAttribute("SharedMyTemplate", mytemplate);
						shareTb.setAttribute("type", "ShareTemplate");
						shareTb.addEventListener("onClick", this);


						preview_Src = preview_Path+"/"+template.getName()+"/email.html";
						preview = new Toolbarbutton("");
						preview.setImage(previewIconPathStr);
						preview.setTooltiptext("Preview");
						preview.setAttribute("MyTemplate", mytemplate);
						preview.setAttribute("type", "Preview");
						preview.setTooltiptext("Preview");
						preview.addEventListener("onClick", this);

						edit = new Toolbarbutton();
						edit.setImage(useIconPathStr);
						edit.setTooltiptext("Edit");
						edit.setAttribute("Template", mytemplate);
						edit.setAttribute("SharedZoneTemplate", sharedZone);
						edit.setAttribute("type", "EditMyTemplate");
						edit.addEventListener("onClick", this);

						templateName = new Label(template.getName());
						templateName.setMaxlength(30);
						hbox = new Hbox();
						hbox.setWidth("215px");
						hbox.setPack("center");
						hbox.setSclass("st_hb");

						preview.setParent(hbox);
						edit.setParent(hbox);

						deleteTb = new Toolbarbutton();
						deleteTb.setImage(deleteIconPathStr);
						deleteTb.setTooltiptext("Delete this from DR templates");

						*//**
						 * add onClick event listener to delete when clicked
						 *//*
						deleteTb.addEventListener("onClick", new EventListener() {

							@Override
							public void onEvent(Event event) throws Exception {//how we can allow the othr user's template deletion
								try {
									int confirm = Messagebox.show("Are you sure you want to delete the template - "
											+ template.getName() + "?",
											"Confirm", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);

									if(confirm != 1){
										return;
									}
								} catch (Exception e) {
									// TODO Auto-generated catch block
									logger.error("Exception ::" , e);;
								}

								template.delete();
								templListDivId.removeChild(vbox);
								digiRecptMyTemplatesDaoForDML.deleteByUserIdAndName(currentUser.getUserId(), template.getName(), OCConstants.DRAG_AND_DROP_EDITOR);
								FileUtils.deleteDirectory(template);
								MessageUtil.setMessage("Template '" + template.getName() + "' deleted successfully.", "green", "top");	

							}

						});
						deleteTb.setParent(hbox);

						shareTb.setParent(hbox);

						templateImg.setParent(vbox);
						templateName.setParent(vbox);
						hbox.setParent(vbox);
						vbox.setParent(templListDivId);

					}

					if(isFound) break;
				}

			}
			logger.info("folder"+selLi);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
		}

	}
*/
	public void getPowerUsers(){

		String userTemplatesDirectory = PropertyUtil.getPropertyValue(USER_DR_NEW_EDITOR_TEMPLATES_DIRECTORY);
		currentUser.getUserOrganization().getUserOrgId();
		try {
			Components.removeAllChildren(winId$myTempListId);
			Listitem listItem;

			UsersDao usersDao =(UsersDao) ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
			SecRolesDao secRolesDao = (SecRolesDao)SpringUtil.getBean("secRolesDao");

			List<Users> orgUserList = usersDao.getUsersListByOrg(currentUser.getUserOrganization().getUserOrgId());

			for(Users user:orgUserList){

				List<SecRoles> userRolesList = secRolesDao.findByUserId(user.getUserId());
				Set<SecRoles> rolesSet = new HashSet<SecRoles>();

				for(SecRoles sB_Users:userRolesList){
					if(sB_Users.getType().equalsIgnoreCase("DRAdmin")){
						logger.info("DRAdmin  :"+user.getUserName());
					}
					else if(sB_Users.getType().equalsIgnoreCase("DRPower")){
						listItem = new Listitem();
						listItem.setValue(user);
						//listItem.setLabel(user.getUserName());
						listItem.setLabel(Utility.getOnlyUserName(user.getUserName()));
						listItem.setParent(winId$myTempListId); 

					}

				}

			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);;
		}

	}

/*	public boolean saveSharedlist(SharedZone sharedzone,DigitalReceiptMyTemplate DRSharedTemplates) {
		logger.debug("-- just entered --");
		try {
			SharedZoneDao sharedZoneDao = (SharedZoneDao) SpringUtil.getBean("sharedZoneDao");
			SharedZoneDaoForDML sharedZoneDaoForDML = (SharedZoneDaoForDML) SpringUtil.getBean("sharedZoneDaoForDML");

			Set lists = winId$myTempListId.getSelectedItems();
			logger.debug(" No of Power Users lists selected :"+lists.size());

			Set<Users> UserSet = new HashSet<Users>();
			Listitem li;
			Users ml = null;

			for (Object obj : lists) {
				li = (Listitem) obj;
				ml = (Users) li.getValue();

				UserSet.add(ml);
			}

			if(sharedzone!=null){

				logger.info("emptyyyyyyyyyyyyyyyyyyyy");
				sharedzone.setSharedOn(Calendar.getInstance());
				sharedzone.setUserSet(UserSet);

				sharedZoneDaoForDML.saveOrUpdate(sharedzone);

				if(UserSet.isEmpty()) sharedZoneDaoForDML.delete(sharedzone);

			}
			else{
				SharedZone sharedzonenew = new SharedZone();

				sharedzonenew.setSharedBy(currentUser.getUserId());
				sharedzonenew.setSharedItem(DRSharedTemplates.getMyTemplateId());
				sharedzonenew.setSharedItemCategory(OCConstants.DnD_DIGITAL_RECEIPT_TEMPLATES);
				sharedzonenew.setSharedOn(Calendar.getInstance());
				sharedzonenew.setUserSet(UserSet);

				sharedZoneDaoForDML.saveOrUpdate(sharedzonenew);
			}

			logger.debug("No of Power Users lists :" + UserSet.size());

		} catch (Exception e) {
			logger.error("** Exception : " ,(Throwable) e);
			return false;
		}
		winId.setVisible(false);
		return true;
	}
*/
/*	public void onClick$myTemplatesSubmtBtnId$winId(){

		try{
			if(logger.isDebugEnabled())logger.debug("-- just entered --");
			SharedZone SharedTemplates=(SharedZone)winId.getAttribute("TemplateShared");
			DigitalReceiptMyTemplate DRSharedTemplates=(DigitalReceiptMyTemplate)winId.getAttribute("DRTemplateShared");
			if(winId$myTempListId.getSelectedItems().size()==0){

				int confirm = Messagebox.show("Are you sure you want to exit without sharing the template '"
						+  DRSharedTemplates.getName()  + "'?",
						"Confirm", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
				if(confirm == 1){
					winId.setVisible(false);
					saveSharedlist(SharedTemplates,DRSharedTemplates);
					newEditorTemplatesListId.setSelectedIndex(selectfolder);
					onSelect$newEditorTemplatesListId();
					return;
				}else{
					return;
				}

			}
			saveSharedlist(SharedTemplates,DRSharedTemplates);
			MessageUtil.setMessage("Template shared successfully.", "green", "top");	

			newEditorTemplatesListId.setSelectedIndex(selectfolder);
			onSelect$newEditorTemplatesListId();
		}catch (Exception e) {
			logger.error("** Exception :", e );
		}
	}
*/
	public void onSelect$tabBoxId(){

		try {

			if(tabBoxId.getSelectedIndex() == 0) {
				getNewEditorTemplatesFromDb(currentUser.getUserId());
			}
			else if(tabBoxId.getSelectedIndex() == 1) {

				getLegacyTemplatesFromDb(currentUser.getUserId());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);;
		}
	}//onClick$tabBoxId()

	public void getLegacyTemplatesFromDb__BackUp(long userId) {

		try {
			Components.removeAllChildren(legacyEditorTemplatesListId);
			digiRecptMyTemplatesDao = (DigitalReceiptMyTemplatesDao) SpringUtil.getBean("digitalReceiptMyTemplatesDao");
			digiRecptMyTemplatesDaoForDML = (DigitalReceiptMyTemplatesDaoForDML) SpringUtil.getBean("digitalReceiptMyTemplatesDaoForDML");

			MyFoldersDao myFolderDao = (MyFoldersDao) SpringUtil.getBean("myFoldersDao");
			MyFoldersDaoForDML myFolderDaoForDML = (MyFoldersDaoForDML) SpringUtil.getBean("myFoldersDaoForDML");


			List<MyFolders> org_Folders = myFolderDao.findByOrgId(currentUser.getUserOrganization().getUserOrgId(),OCConstants.LEGACY_DIGITAL_RECEIPT_TEMPLATES);

			for(MyFolders myfolders:org_Folders) {

				logger.debug("------in if case----"+myfolders.getFolderName());
				String folderName = myfolders.getFolderName();						

				Listitem item = new Listitem();

				item.setValue(myfolders);
				item.setLabel(folderName);
				item.setParent(legacyEditorTemplatesListId);
			}
			if(legacyEditorTemplatesListId.getItems().size() >0){
				legacyEditorTemplatesListId.setSelectedIndex(0);
				onSelect$legacyEditorTemplatesListId();
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);;
		}

	}

/*	public void onSelect$legacyEditorTemplatesListId__BackUp(){

		Components.removeAllChildren(templListDivId);
		File category = null;
		File[] templateList = null;
		List<File[]> templateFileList = new ArrayList<File[]>();
		String templatePrevStr = null;
		String appUrl = PropertyUtil.getPropertyValue("ApplicationUrl");
		String preview_Path = null;

		String userTemplatesDirectory = PropertyUtil.getPropertyValue(USER_DR_NEW_EDITOR_TEMPLATES_DIRECTORY);

		String folderName = legacyEditorTemplatesListId.getSelectedItem().getLabel();
		templateFileList.clear();
		String myTempPathStr = PropertyUtil.getPropertyValue("usersParentDirectory")+
				File.separator+ currentUser.getUserName()+userTemplatesDirectory+File.separator+folderName;
		logger.debug("myTempPathStr ===>"+myTempPathStr);
		category= new File(myTempPathStr);
		templateList = category.listFiles();

		preview_Path =  appUrl + "/UserData/"+currentUser.getUserName()+userTemplatesDirectory+File.separator+folderName;

		final String useIconPathStr = "/images/Edit_icn.png";
		final String deleteIconPathStr = "/img/theme/action_delete.gif";

		if(currentUserRole.equals(drAdmin)){

			try {

				try {

					Image templateImg = null;

					Toolbarbutton preview,edit,deleteTb,shareTb;
					Label templateName;
					Hbox hbox;
					String preview_Src ="";
					String previewIconPathStr = "/img/theme/preview_icon.png";
					String vbStyle = "templatePrviewVb";
					String custom_temp_default_preview = "/img/custom_temp_preview.gif";

					Listitem selLi = legacyEditorTemplatesListId.getSelectedItem();

					MyFolders myfolders = (MyFolders)selLi.getValue();
					logger.info("selLi   --------:"+selLi.getValue());

					MyTemplatesDao myTemplatesDao = (MyTemplatesDao)SpringUtil.getBean("myTemplatesDao");

					List<DigitalReceiptMyTemplate> myTemplatesList = digiRecptMyTemplatesDao.findDRMyTemplatesByEditorType(currentUser.getUserId(), myfolders.getFolderName(), OCConstants.LEGACY_EDITOR);

					String IdsStr = Constants.STRING_NILL;
					for(DigitalReceiptMyTemplate mytemplates:myTemplatesList){

						if(!IdsStr.isEmpty()) IdsStr += ",";

						IdsStr += mytemplates.getMyTemplateId()+Constants.STRING_NILL;

					}


					List<Long> activeTempIds = digitalReceiptUserSettingsDao.findBy(currentUser.getUserOrganization().getUserOrgId(),IdsStr  );

					for(DigitalReceiptMyTemplate mytemplates:myTemplatesList){

						for (Long tempId : activeTempIds) {

							if(mytemplates.getMyTemplateId().equals(tempId)) mytemplates.setActive(true);

						}//for

					}//for

					for (DigitalReceiptMyTemplate mytemplate : myTemplatesList) {

						final Vbox vbox = new Vbox();
						vbox.setSclass(vbStyle);
						vbox.setAlign("center");
						vbox.setStyle("height:315px");

						Label active;
						if(mytemplate.isActive()) {
							active = new Label();
							active.setParent(vbox);
							active.setValue("ACTIVE");
							active.setStyle("font-size:10px;font-weight:bold;color:#54a93a;border:1px solid #54a93a;padding:2px 0px;margin-right:73px;");
						} else vbox.setStyle("padding-top:20px;");

						templateImg = new Image(custom_temp_default_preview);
						preview = new Toolbarbutton("");
						preview.setImage(previewIconPathStr);
						preview.setTooltiptext("Preview");
						preview.setAttribute("MyTemplate", mytemplate);
						preview.setAttribute("type", "Preview");
						preview.setTooltiptext("Preview");
						preview.addEventListener("onClick", this);


						edit = new Toolbarbutton();
						edit.setImage(useIconPathStr);
						edit.setTooltiptext("Edit");
						edit.setAttribute("Template", mytemplate);
						//edit.setAttribute("SharedZoneTemplate", sharedZone);
						edit.setAttribute("type", "EditMyTemplate");
						edit.addEventListener("onClick", this);

						templateName = new Label(mytemplate.getName());
						templateName.setMaxlength(30);
						hbox = new Hbox();
						hbox.setWidth("215px");
						hbox.setPack("center");
						hbox.setSclass("st_hb");

						preview.setParent(hbox);
						edit.setParent(hbox);

						deleteTb = new Toolbarbutton();
						deleteTb.setImage(deleteIconPathStr);
						deleteTb.setTooltiptext("Delete this from DR templates");

						*//**
						 * add onClick event listener to delete when clicked
						 *//*
						deleteTb.addEventListener("onClick", new EventListener() {

							@Override
							public void onEvent(Event event) throws Exception {//how we can allow the othr user's template deletion
								try {
									int confirm = Messagebox.show("Are you sure you want to delete the template - "
											+ mytemplate.getName() + "?",
											"Confirm", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
									if(confirm != 1){
										return;
									}
								} catch (Exception e) {
									// TODO Auto-generated catch block
									logger.error("Exception ::" , e);;
								}
								//mytemplate.delete();
								templListDivId.removeChild(vbox);
								digiRecptMyTemplatesDaoForDML.deleteByUserIdAndName(currentUser.getUserId(), mytemplate.getName(), OCConstants.DRAG_AND_DROP_EDITOR);
								MessageUtil.setMessage("Template '" + mytemplate.getName() + "' deleted successfully.", "green", "top");	

							}
						});
						deleteTb.setParent(hbox);
						templateImg.setParent(vbox);
						templateName.setParent(vbox);
						hbox.setParent(vbox);
						vbox.setParent(templListDivId);

					}
					logger.info("folder"+selLi);

				} catch (Exception e) {
					// TODO Auto-generated catch block
					logger.error("Exception ::" , e);;
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Exception ::" , e);;
			}

		}

	}
*/
	public void getLegacyTemplatesFromDb(long userId) {

		try {
			Components.removeAllChildren(legacyEditorTemplatesListId);

			digiRecptMyTemplatesDao = (DigitalReceiptMyTemplatesDao) SpringUtil.getBean("digitalReceiptMyTemplatesDao");
			digiRecptMyTemplatesDaoForDML = (DigitalReceiptMyTemplatesDaoForDML) SpringUtil.getBean("digitalReceiptMyTemplatesDaoForDML");

			List<MyFolders> org_Folders = myFolderDao.findByOrgId(currentUser.getUserOrganization().getUserOrgId(),OCConstants.LEGACY_DIGITAL_RECEIPT_TEMPLATES);

			for(MyFolders myfolders :org_Folders) {
				logger.debug("------in if case----"+myfolders.getFolderName());
				String folderName = myfolders.getFolderName();						
				Listitem item = new Listitem();
				item.setValue(myfolders);

				Listcell cell = new Listcell();
				Label tempLbl = new Label(folderName);
				tempLbl.setStyle("width: 90%; display: inline-block;");

				cell.appendChild(tempLbl);

				cell.setParent(item);
				item.setParent(legacyEditorTemplatesListId);
			}

			if(legacyEditorTemplatesListId.getItems().size() >0){
				legacyEditorTemplatesListId.setSelectedIndex(0);
				if(currentUserRole.equalsIgnoreCase(drAdmin))
					onSelect$legacyEditorTemplatesListId();
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);;
		}
	}

	public void onSelect$legacyEditorTemplatesListId(){
		Components.removeAllChildren(templListDivId);
		File category = null;
		File[] templateList = null;
		List<File[]> templateFileList = new ArrayList<File[]>();
		String templatePrevStr = null;
		String appUrl = PropertyUtil.getPropertyValue("ApplicationUrl");
		String preview_Path = null;

		String userTemplatesDirectory = PropertyUtil.getPropertyValue(USER_DR_NEW_EDITOR_TEMPLATES_DIRECTORY);

		final String useIconPathStr = "/images/Edit_icn.png";
		final String deleteIconPathStr = "/img/theme/action_delete.gif";
		final String testmailIcon="/img/email_page.gif";
			try {
				Image templateImg = null;

				Toolbarbutton preview,edit,deleteTb,shareTb,testEmail;
				Label templateName;
				Hbox hbox;
				String preview_Src ="";
				String previewIconPathStr = "/img/theme/preview_icon.png";
				String vbStyle = "templatePrviewVb";
				String custom_temp_default_preview = "/img/custom_temp_preview.gif";


				//List<DigitalReceiptMyTemplate> myTemplatesList = digiRecptMyTemplatesDao.findDRMyTemplatesByEditorType(currentUser.getUserId(), OCConstants.DR_LEGACY_EDITOR_FOLDER, OCConstants.LEGACY_EDITOR);
				List<DigitalReceiptMyTemplate> myTemplatesList = digiRecptMyTemplatesDao.findDRMyTemplatesByEditorType(currentUser.getUserId(), OCConstants.LEGACY_EDITOR,0,0,null);
				
				String thumbnailImgPath=USER_PARENT_DIR+File.separator+currentUser.getUserName()+USER_LEGACY_EDITOR_THUMBNAIL_DIR;
				String thumbnailHtmlPath=USER_PARENT_DIR+File.separator+currentUser.getUserName()+USER_LEGACY_EDITOR_THUMBNAIL_DIR;
				File htmlFile=null;
				File imgFile=null;
				String IMG_CMD= PropertyUtil.getPropertyValueFromDB("imgGenerationCmd");
				 logger.info("before for loop :"+System.currentTimeMillis());

				for (DigitalReceiptMyTemplate mytemplate : myTemplatesList) {


					String htmlFileNameWithPath=thumbnailHtmlPath+"/DR_Template_"+mytemplate.getMyTemplateId()+".html";
					String imgNameWithPath=thumbnailImgPath+"/DR_Template_Img_"+mytemplate.getMyTemplateId()+".png";
					if(mytemplate.getContent()!=null){   
						imgFile=new File(imgNameWithPath);
						if(imgFile!=null && imgFile.exists())
							imgFile.delete(); 
						htmlFile =new File(htmlFileNameWithPath); 
						BufferedWriter bw = new BufferedWriter(new FileWriter(htmlFile));
						bw.write(mytemplate.getContent());
						bw.close();
						String command =IMG_CMD + htmlFile +" --out="+imgNameWithPath;
						logger.info("command "+command);
						logger.info("img started "+mytemplate.getMyTemplateId()+".png");
						
						logger.info("Before Processss :"+System.currentTimeMillis());
						Process process = Runtime.getRuntime().exec(command);
						process.waitFor();
						logger.info("After Processss :"+System.currentTimeMillis());
						
						logger.info("     done  "+mytemplate.getMyTemplateId()+".png");
						if(htmlFile!= null && htmlFile.exists())
							htmlFile.delete();	
					}
					
				}
				
				 logger.info("after for loop :"+System.currentTimeMillis());

				String imgPath	=USER_PARENT_DIR+File.separator+currentUser.getUserName()+USER_LEGACY_EDITOR_THUMBNAIL_DIR;
				for (DigitalReceiptMyTemplate mytemplate : myTemplatesList) {
					final Vbox vbox = new Vbox();
					vbox.setSclass(vbStyle);
					vbox.setAlign("center");

/*					Label active;
					if(mytemplate.isActive()) {
						active = new Label();
						active.setParent(vbox);
						active.setValue("ACTIVE");
						active.setStyle("font-size:10px;font-weight:bold;color:#54a93a;border:1px solid #54a93a;padding:2px 0px;margin-right:73px;");
					} else vbox.setStyle("padding-top:20px;");
*/
					String thumbnailPath=imgPath+"/DR_Template_Img_"+mytemplate.getMyTemplateId()+".png";
					templateImg = new Image();
					templateImg.setContent(new AImage(thumbnailPath));
					templateImg.setWidth("150px");
					templateImg.setHeight("220px");
					preview = new Toolbarbutton("");
					preview.setImage(previewIconPathStr);
					preview.setTooltiptext("Preview");
					preview.setAttribute("MyTemplate", mytemplate);
					preview.setAttribute("type", "Preview");
					preview.setTooltiptext("Preview");
					preview.addEventListener("onClick", this);
					
					
					
					testEmail = new Toolbarbutton();
					testEmail.setImage(testmailIcon);
					testEmail.setTooltiptext("Send Test Email");
					testEmail.setAttribute("Template", mytemplate);
					testEmail.setAttribute("type", "SendTestMail");
					testEmail.setTooltiptext("Send Test Email");
					testEmail.addEventListener("onClick", this);

					edit = new Toolbarbutton();
					edit.setImage(useIconPathStr);
					edit.setTooltiptext("Edit");
					edit.setAttribute("Template", mytemplate);
					edit.setAttribute("type", "EditMyTemplate");
					edit.addEventListener("onClick", this);

					templateName = new Label(mytemplate.getName());
					templateName.setMaxlength(30);
					hbox = new Hbox();
					hbox.setWidth("215px");
					hbox.setPack("center");
					hbox.setSclass("st_hb");

					preview.setParent(hbox);
					edit.setParent(hbox);

					deleteTb = new Toolbarbutton();
					deleteTb.setImage(deleteIconPathStr);
					deleteTb.setTooltiptext("Delete this from DR templates");

					/**
					 * add onClick event listener to delete when clicked
					 */
					deleteTb.addEventListener("onClick", new EventListener() {

						@Override
						public void onEvent(Event event) throws Exception {//how we can allow the othr user's template deletion
							try {
								boolean isConfigured = digitalReceiptUserSettingsDao.isTemplateConfigured(mytemplate.getMyTemplateId());

								if(isConfigured){

									MessageUtil.setMessage("Unable to delete the template because it is configured to e-Receipt.", "color:red", "TOP");

								}
								else{	

									int confirm = Messagebox.show("Are you sure you want to delete the template - "
											+ mytemplate.getName() + "?",
											"Confirm", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
									if(confirm != 1){
										return;
									}

									templListDivId.removeChild(vbox);
									digiRecptMyTemplatesDaoForDML.deleteByUserIdAndName(currentUser.getUserId(), mytemplate.getName(), null, OCConstants.LEGACY_EDITOR);
									MessageUtil.setMessage("Template '" + mytemplate.getName() + "' deleted successfully.", "green", "top");	
								}	

							} catch (Exception e) {
								// TODO Auto-generated catch block
								logger.error("Exception ::" , e);;
							}

						}
					});
					deleteTb.setParent(hbox);
					templateImg.setParent(vbox);
					templateName.setParent(vbox);
					hbox.setParent(vbox);
					vbox.setParent(templListDivId);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Exception ::" , e);;
			}
	
		
		/*
	
		Components.removeAllChildren(templListDivId);
		File category = null;
		File[] templateList = null;
		List<File[]> templateFileList = new ArrayList<File[]>();
		String templatePrevStr = null;
		String appUrl = PropertyUtil.getPropertyValue("ApplicationUrl");
		String preview_Path = null;

		String userTemplatesDirectory = PropertyUtil.getPropertyValue(USER_DR_NEW_EDITOR_TEMPLATES_DIRECTORY);

		final String useIconPathStr = "/images/Edit_icn.png";
		final String deleteIconPathStr = "/img/theme/action_delete.gif";

		if(currentUserRole.equals(drAdmin)){

			try {
				Image templateImg = null;

				Toolbarbutton preview,edit,deleteTb,shareTb;
				Label templateName;
				Hbox hbox;
				String preview_Src ="";
				String previewIconPathStr = "/img/theme/preview_icon.png";
				String vbStyle = "templatePrviewVb";
				String custom_temp_default_preview = "/img/custom_temp_preview.gif";


				List<DigitalReceiptMyTemplate> myTemplatesList = digiRecptMyTemplatesDao.findDRMyTemplatesByEditorType(currentUser.getUserId(), OCConstants.DR_LEGACY_EDITOR_FOLDER, OCConstants.LEGACY_EDITOR);

				String IdsStr = Constants.STRING_NILL;
				for(DigitalReceiptMyTemplate mytemplates:myTemplatesList){

					if(!IdsStr.isEmpty()) IdsStr += ",";

					IdsStr += mytemplates.getMyTemplateId()+Constants.STRING_NILL;

				}


				List<Long> activeTempIds = digitalReceiptUserSettingsDao.findBy(currentUser.getUserOrganization().getUserOrgId(),IdsStr  );

				for(DigitalReceiptMyTemplate mytemplates:myTemplatesList){

					for (Long tempId : activeTempIds) {

						if(mytemplates.getMyTemplateId().equals(tempId)) mytemplates.setActive(true);

					}//for

				}//for

				String thumbnailImgPath=USER_PARENT_DIR+File.separator+currentUser.getUserName()+USER_LEGACY_EDITOR_THUMBNAIL_DIR;
				String thumbnailHtmlPath=USER_PARENT_DIR+File.separator+currentUser.getUserName()+USER_LEGACY_EDITOR_THUMBNAIL_DIR;
				File htmlFile=null;
				File imgFile=null;
				String IMG_CMD= PropertyUtil.getPropertyValueFromDB("imgGenerationCmd");
				for (DigitalReceiptMyTemplate mytemplate : myTemplatesList) {
					String htmlFileNameWithPath=thumbnailHtmlPath+"/DR_Template_"+mytemplate.getMyTemplateId()+".html";
					String imgNameWithPath=thumbnailImgPath+"/DR_Template_Img_"+mytemplate.getMyTemplateId()+".png";
					if(mytemplate.getContent()!=null||mytemplate.getAutoSaveHtmlContent()!=null){   
						imgFile=new File(imgNameWithPath);
						if(imgFile!=null && imgFile.exists())
							imgFile.delete(); 
						htmlFile =new File(htmlFileNameWithPath); 
						BufferedWriter bw = new BufferedWriter(new FileWriter(htmlFile));
						if(mytemplate.getContent()!=null)
							bw.write(mytemplate.getContent());
						else
							bw.write(mytemplate.getAutoSaveHtmlContent());
						bw.close();
						String command =IMG_CMD + htmlFile +" --out="+imgNameWithPath;
						logger.info("command "+command);
						logger.info("img started "+mytemplate.getMyTemplateId()+".png");
						Process process = Runtime.getRuntime().exec(command);
						process.waitFor();
						logger.info("     done  "+mytemplate.getMyTemplateId()+".png");
						if(htmlFile!= null && htmlFile.exists())
							htmlFile.delete();	
					}
					else
						continue; 
				}
				String imgPath	=USER_PARENT_DIR+File.separator+currentUser.getUserName()+USER_LEGACY_EDITOR_THUMBNAIL_DIR;
				for (DigitalReceiptMyTemplate mytemplate : myTemplatesList) {
					final Vbox vbox = new Vbox();
					vbox.setSclass(vbStyle);
					vbox.setAlign("center");

					Label active;
					if(mytemplate.isActive()) {
						active = new Label();
						active.setParent(vbox);
						active.setValue("ACTIVE");
						active.setStyle("font-size:10px;font-weight:bold;color:#54a93a;border:1px solid #54a93a;padding:2px 0px;margin-right:73px;");
					} else vbox.setStyle("padding-top:20px;");

					String thumbnailPath=imgPath+"/DR_Template_Img_"+mytemplate.getMyTemplateId()+".png";
					templateImg = new Image();
					templateImg.setContent(new AImage(thumbnailPath));
					templateImg.setWidth("150px");
					templateImg.setHeight("220px");
					preview = new Toolbarbutton("");
					preview.setImage(previewIconPathStr);
					preview.setTooltiptext("Preview");
					preview.setAttribute("MyTemplate", mytemplate);
					preview.setAttribute("type", "Preview");
					preview.setTooltiptext("Preview");
					preview.addEventListener("onClick", this);

					edit = new Toolbarbutton();
					edit.setImage(useIconPathStr);
					edit.setTooltiptext("Edit");
					edit.setAttribute("Template", mytemplate);
					edit.setAttribute("type", "EditMyTemplate");
					edit.addEventListener("onClick", this);

					templateName = new Label(mytemplate.getName());
					templateName.setMaxlength(30);
					hbox = new Hbox();
					hbox.setWidth("215px");
					hbox.setPack("center");
					hbox.setSclass("st_hb");

					preview.setParent(hbox);
					edit.setParent(hbox);

					deleteTb = new Toolbarbutton();
					deleteTb.setImage(deleteIconPathStr);
					deleteTb.setTooltiptext("Delete this from DR templates");

					*//**
					 * add onClick event listener to delete when clicked
					 *//*
					deleteTb.addEventListener("onClick", new EventListener() {

						@Override
						public void onEvent(Event event) throws Exception {//how we can allow the othr user's template deletion
							try {
								boolean isConfigured = digitalReceiptUserSettingsDao.isTemplateConfigured(mytemplate.getMyTemplateId());

								if(isConfigured){

									MessageUtil.setMessage("Unable to delete the template because it is configured to Digital Receipt.", "color:red", "TOP");

								}
								else{	

									int confirm = Messagebox.show("Are you sure you want to delete the template - "
											+ mytemplate.getName() + "?",
											"Confirm", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
									if(confirm != 1){
										return;
									}

									templListDivId.removeChild(vbox);
									digiRecptMyTemplatesDaoForDML.deleteByUserIdAndName(currentUser.getUserId(), mytemplate.getName(), null, OCConstants.LEGACY_EDITOR);
									MessageUtil.setMessage("Template '" + mytemplate.getName() + "' deleted successfully.", "green", "top");	
								}	

							} catch (Exception e) {
								// TODO Auto-generated catch block
								logger.error("Exception ::" , e);;
							}

						}
					});
					deleteTb.setParent(hbox);
					templateImg.setParent(vbox);
					templateName.setParent(vbox);
					hbox.setParent(vbox);
					vbox.setParent(templListDivId);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Exception ::" , e);;
			}

		}
	*/}

	Map<Long, String> DomainNameMap = new HashMap<Long, String>();
/*	public void setUserOrgUnit() {
		Long orgId = currentUser.getUserOrganization().getUserOrgId();

		List<UsersDomains> usersDomainsList = domainDao.getAllDomainsByOrganizationID(orgId);

		for (UsersDomains domain : usersDomainsList) {
			Listitem tempList = new Listitem(domain.getDomainName(), domain);
			logger.debug("domainName ::" + domain.getDomainName());
			tempList.setParent(orgUnitlbId);
			DomainNameMap.put(domain.getDomainId(), domain.getDomainName());
		}
		orgUnitlbId.setSelectedIndex(0);

	}*/
	public void onSelect$orgUnitlbId() {
		if(orgUnitlbId.getSelectedIndex() == 0){
			dispSBSLBoxId.setSelectedIndex(0);
			mytemplatesLbId.setSelectedIndex(0);
			return;
		}
		UsersDomains usersDomains = (UsersDomains)orgUnitlbId.getSelectedItem().getValue();
		String orgUnitsIds = usersDomains.getDomainId().longValue()+Constants.STRING_NILL;
		List<OrganizationStores> retSBS = organizationDao.findSubsidaryByOrgUnitId(currentUser.getUserOrganization().getUserOrgId(), orgUnitsIds);
		Listitem listItem = null;
		int count = dispSBSLBoxId.getItemCount();
		if( count> 1) {
			for(;count>1;count--) {
				dispSBSLBoxId.removeItemAt(count-1);
			}

		}//if
		if(retSBS != null){
			for (OrganizationStores organizationStores : retSBS) {
				listItem = new Listitem(organizationStores.getSubsidiaryId());
				listItem.setValue(organizationStores);
				listItem.setParent(dispSBSLBoxId); 
			}
		}

	}

/*	public void setMyTemplates() {

		List<DigitalReceiptMyTemplate> retList = digiRecptMyTemplatesDao.findAllByOrgId(currentUser.getUserOrganization().getUserOrgId());
		Listitem listItem=null;
		for (DigitalReceiptMyTemplate digitalReceiptMyTemplate : retList) {

			listItem = new Listitem(digitalReceiptMyTemplate.getName(), digitalReceiptMyTemplate);
			listItem.setParent(mytemplatesLbId); 

		}

		List<DigitalReceiptUserSettings> retObj = digitalReceiptUserSettingsDao.findBy(currentUser.getUserOrganization().getUserOrgId());
		int count = tempsettingsLbId.getItemCount() ;
		for(; count>1;count--) {
			tempsettingsLbId.removeItemAt(count-1);
		}
		for (DigitalReceiptUserSettings digitalReceiptUserSettings : retObj) {
			listItem = new Listitem();
			Listcell cell = new Listcell(DomainNameMap.get(digitalReceiptUserSettings.getDomainId()));
			cell.setParent(listItem);

			cell = new Listcell(digitalReceiptUserSettings.getSBSNo());
			cell.setParent(listItem);

			OrganizationStores orgStores = organizationDao.findSubsidaryBy(digitalReceiptUserSettings.getOrgId(), digitalReceiptUserSettings.getDomainId(), digitalReceiptUserSettings.getSBSNo());

			cell = new Listcell(orgStores != null && orgStores.getSubsidiaryName() != null ? orgStores.getSubsidiaryName() : "--");
			cell.setParent(listItem);

			cell = new Listcell(Utility.getOnlyUserName(usersDao.findNameById(digitalReceiptUserSettings.getUserId().longValue()+"")));
			cell.setParent(listItem);

			String templateName = digitalReceiptUserSettings.getSelectedTemplateName();
			if(templateName.startsWith(MY_TEMPLATE)) templateName = templateName.substring(MY_TEMPLATE.length());
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

			cell = new Listcell(digitalReceiptUserSettings.getReplytoemail() != null ? digitalReceiptUserSettings.getReplytoemail() : "--");
			cell.setTooltiptext(digitalReceiptUserSettings.getReplytoemail() != null ? digitalReceiptUserSettings.getReplytoemail() : "--");
			cell.setParent(listItem);

			listItem.setParent(tempsettingsLbId);

		}

	}*/
	
/*	public void onSelect$dispSBSLBoxId() {
		if(dispSBSLBoxId.getSelectedIndex() == 0){
			mytemplatesLbId.setSelectedIndex(0);

			return;
		}

		OrganizationStores orgStore = dispSBSLBoxId.getSelectedItem().getValue();

		String SBSNo = orgStore.getSubsidiaryId();

		Long userId = findPowerUser(((UsersDomains)orgUnitlbId.getSelectedItem().getValue()).getDomainId());
		DigitalReceiptUserSettings digitalReceiptUserSettings = digitalReceiptUserSettingsDao.findUserSelectedTemplate(userId, null, SBSNo);

		logger.debug("digitalReceiptUserSettings===="+digitalReceiptUserSettings);
		if(digitalReceiptUserSettings != null) {
			String selectedTemplate = digitalReceiptUserSettings.getSelectedTemplateName();
			logger.debug("selectedTemplate===="+selectedTemplate);
			if (selectedTemplate.startsWith(MY_TEMPLATE)) {
				selectedTemplateLblId.setAttribute("templateName", selectedTemplate);
				selectedTemplateLblId.setValue(selectedTemplate.substring(MY_TEMPLATE.length()));

			}
			List<Listitem> retList = mytemplatesLbId.getItems();
			String templateName = selectedTemplate.substring(MY_TEMPLATE.length());
			logger.debug("templateName===="+templateName);
			for (Listitem listitem : retList) {
				if(listitem.getIndex()==0) continue;
				if(listitem.getLabel().equalsIgnoreCase(templateName)) {
					listitem.setSelected(true);
					break;
				}
			}
			logger.debug("mytemplatesLbId===="+mytemplatesLbId.getSelectedIndex());
			defaultDigiSettings(digitalReceiptUserSettings);

		}
		saveBtnId.setAttribute(STORE_WISE_DR_SETTINGS_OBJ, digitalReceiptUserSettings);
	}*/

		private void setEditorContent(Listitem li) {

			try {
				logger.info("Tab box index is : "+ tabBoxId.getSelectedIndex());
				if (tabBoxId.getSelectedIndex() == 0) { // My Templates

					DigitalReceiptMyTemplate digitalReceiptMyTemplate = digiRecptMyTemplatesDao.findByUserNameAndTemplateName(currentUser.getUserOrganization().getUserOrgId(),	(String) li.getValue());
					manageActionsImg.setParent(li.getFirstChild());
					String modifiedPHAsCommentStr = digitalReceiptMyTemplate.getContent();

					if (modifiedPHAsCommentStr != null && modifiedPHAsCommentStr.length() > 1) {
						modifiedPHAsCommentStr = modifiedPHAsCommentStr.replace("##BEGIN ITEMS##", "<!--##BEGIN ITEMS##-->")
								.replace("##END ITEMS##", "<!--##END ITEMS##-->");
						modifiedPHAsCommentStr = modifiedPHAsCommentStr.replace("##BEGIN PAYMENTS##", "<!--##BEGIN PAYMENTS##-->")
								.replace("##END PAYMENTS##","<!--##END PAYMENTS##-->");
					}

					digitalRecieptsCkEditorId.setValue(modifiedPHAsCommentStr);
					return;
				}

				logger.info("element clicked...path : "+ digitalRecieptTemplatesPath + "/" + li.getValue() + "/"
						+ "index.html");
				File tempFile = new File(digitalRecieptTemplatesPath + "/"+ li.getValue() + "/" + "index.html");

				if (!tempFile.exists()) {
					logger.debug("Index file in the selected Folder does not Exist ... returning");
					return;
				}

				BufferedReader br = new BufferedReader(new FileReader(tempFile));

				StringBuffer sb = new StringBuffer();
				String tempStr = null;
				while ((tempStr = br.readLine()) != null) {
					sb.append(tempStr);
				}
				String templateStr = sb.toString();

				templateStr = templateStr.replace("##BEGIN ITEMS##","<!--##BEGIN ITEMS##-->")
						.replace("##END ITEMS##","<!--##END ITEMS##-->");

				templateStr = templateStr.replace("##BEGIN PAYMENTS##","<!--##BEGIN PAYMENTS##-->")
						.replace("##END PAYMENTS##","<!--##END PAYMENTS##-->");

				digitalRecieptsCkEditorId.setValue(templateStr);
				// digitalRecieptSettingHboxId.setVisible(true);
				// selectedTemplateLblId.setValue(li.getValue()+"");

				logger.info("templates is set ..done !!");
			} catch (FileNotFoundException e) {
				logger.error("Exception ::", e);
			} catch (Exception e) {
				logger.error("Exception ::", e);
			}

		}

		public void onClick$sendTestBtnId() {
			testDRWinId$msgLblId.setValue("");
			boolean isValid = true;

			long size =	Utility.validateHtmlSize(digitalRecieptsCkEditorId.getValue());
			if(size >100) {
				String msgcontent = OCConstants.HTML_VALIDATION;
				msgcontent = msgcontent.replace("[size]", size+Constants.STRING_NILL);
				MessageUtil.setMessage(msgcontent, "color:Blue");
				isValid = true;
			}

			if(isValid ) {

				isValid = validateDRContent();

			}
			//Check whether user is expired or not
			if(isValid && Calendar.getInstance().after(currentUser.getPackageExpiryDate())){
				testDRWinId.setVisible(false);
				logger.debug("Current User, with userId:: "+currentUser.getUserId()+" has been expired, hence cannot send test mail");
				MessageUtil.setMessage("Your account validity period has expired. Please renew your subscription to continue.", "color:red", "TOP");
				isValid = false;
			}
			if(isValid) {

				DigitalReceiptUserSettings digitalReceiptUserSettings = digitalReceiptUserSettingsDao.findByUserId(currentUser.getUserId());

				if(digitalReceiptUserSettings == null) {
					MessageUtil.setMessage("Test receipt can not be sent without settings.\n"
							+ "Please save settings to get latest values in the email.", "color:red", "TOP");
					isValid = false;
				}

				if(isValid && cSubTb.isValid() && cSubTb.getValue().trim().isEmpty()){

					MessageUtil.setMessage("Please provide subject. Subject should not be left empty.", "color:red", "TOP");
					isValid = false;
				}


				//  check for from name n it is valid or not
				if(isValid && cFromNameTb.isValid() && frmNamedynamicOrNotRgId.getSelectedIndex() == 0) {
					if(cFromNameTb.getValue().trim().isEmpty() ) {

						MessageUtil.setMessage("Provide 'From Name' in settings page.", "color:red", "TOP");
						isValid = false;

					}

					if(isValid) {

						isValid = Utility.validateFromName(cFromNameTb.getValue());
						if(!isValid) {

							MessageUtil.setMessage("Provide valid 'From Name' in settings page.Special characters are not allowed.", "color:red", "TOP");
							isValid = false;
						}
					}
				}

			}

			if(isValid) {
				testDRWinId$testDRTbId.setValue("");
				testDRWinId.setVisible(true);
				testDRWinId.setPosition("center");
				testDRWinId.doHighlighted();
			}

		}

		public void onClick$sendTestMailBtnId$testDRWinId() {
			String emailId = testDRWinId$testDRTbId.getValue();

			boolean isValid = sendTestMail(emailId);
			if(isValid){
				testDRWinId$testDRTbId.setValue("");
			}

		}
		public void onClick$cancelSendTestMailBtnId$testDRWinId() {

			testDRWinId$msgLblId.setValue("");
			testDRWinId$msgLblId.setValue("");
			testDRWinId.setVisible(false);

		}

		public void onClick$folderImgId() {

			makeDisplayWindow(true,null);

		}

		public void makeDisplayWindow(boolean isNew, String folderName) {

			if(isNew) {

				newMyTemplatesSubWinId.setTitle("Folder Creation");
				newMyTemplatesSubWinId$okBtnId.setLabel("Add Folder");

			}else {
				logger.info("Change Folder Name");
				newMyTemplatesSubWinId.setTitle("Modify Folder");
				newMyTemplatesSubWinId$okBtnId.setLabel("Change Folder Name");
				newMyTemplatesSubWinId$okBtnId.setAttribute("folderName", folderName);
			}
			newMyTemplatesSubWinId$newFolderTbId.setValue("");
			newMyTemplatesSubWinId$folderErrorMsgLblId.setValue(""); 
			//newMyTemplatesSubWinId$okBtnId.setAttribute("parentDir",parentDir);

			newMyTemplatesSubWinId$createFolderDivId.setVisible(true);
			newMyTemplatesSubWinId.doHighlighted();

		}

/*		public void onClick$okBtnId$newMyTemplatesSubWinId() {

			MessageUtil.clearMessage();
			try {
				String folderNameAttr = (String)newMyTemplatesSubWinId$okBtnId.getAttribute("folderName");
				String folderName = newMyTemplatesSubWinId$newFolderTbId.getValue().trim();
				if(folderName.trim().isEmpty() || folderName == null) {
					newMyTemplatesSubWinId$folderErrorMsgLblId.setValue("Please provide the Folder Name");
					return;
				}
				else if(!Utility.validateName(folderName)){
					newMyTemplatesSubWinId$folderErrorMsgLblId.setValue("Provide a valid name with out any special characters");
					return;
				}
				logger.debug("exist folder name"+folderNameAttr+"::replace with:"+folderName+" path ");
				folderName = folderName.replaceAll(" +", "_");


				MyFoldersDao myFolderDao = (MyFoldersDao) SpringUtil.getBean("myFoldersDao");
				MyFoldersDaoForDML myFolderDaoForDML = (MyFoldersDaoForDML) SpringUtil.getBean("myFoldersDaoForDML");

				List<MyFolders> org_Folders = myFolderDao.findByOrgId(currentUser.getUserOrganization().getUserOrgId(),OCConstants.DnD_DIGITAL_RECEIPT_TEMPLATES);

				if(org_Folders!=null && org_Folders.size()>0){
					for(MyFolders myfolders:org_Folders) {
						logger.debug("------in if case----"+myfolders.getFolderName());
						String myFolderName = myfolders.getFolderName();	
						if(myFolderName.equalsIgnoreCase(folderName)) {
							newMyTemplatesSubWinId$folderErrorMsgLblId.setValue("Please provide another name for folder.");
							return;
						}
					}
				}

				List<MyFolders> orgMyFolders = myFolderDao.findByOrgIdFolderName(currentUser.getUserOrganization().getUserOrgId(),OCConstants.DnD_DIGITAL_RECEIPT_TEMPLATES,folderName);

				String msgStr="";
				if(orgMyFolders.size()==0){
					try{
						if(folderNameAttr != null) {

							msgStr =" updated";
							List<DigitalReceiptMyTemplate> myDRTemplatesList = digiRecptMyTemplatesDao.findDRMyTemplatesOrgID(currentUser.getUserOrganization().getUserOrgId(), folderNameAttr, OCConstants.DRAG_AND_DROP_EDITOR);

							for (DigitalReceiptMyTemplate mytemplate : myDRTemplatesList) {
								if(mytemplate.getFolderName().equals(folderNameAttr)){
									mytemplate.setFolderName(folderName);
									digiRecptMyTemplatesDaoForDML.saveOrUpdate(mytemplate);
								}

							}

							List<MyFolders> myFoldersList = myFolderDao.findByOrgIdFolderName(currentUser.getUserOrganization().getUserOrgId(),OCConstants.DnD_DIGITAL_RECEIPT_TEMPLATES,folderNameAttr);
							if(myFoldersList!=null){
								MyFolders myFolderobj = myFoldersList.get(0);
								if(myFolderobj.getFolderName().equals(folderNameAttr)){
									myFolderobj.setFolderName(folderName);
									myFolderobj.setModifiedDate(MyCalendar.getNewCalendar());
									myFolderDaoForDML.saveOrUpdate(myFolderobj);
								}
							}

						}
						else {

							msgStr =" created";
							MyFolders myfolder =new MyFolders(folderName, MyCalendar.getNewCalendar(), currentUser.getUserId(), currentUser.getUserOrganization().getUserOrgId(),OCConstants.DnD_DIGITAL_RECEIPT_TEMPLATES);
							myfolder.setModifiedDate(MyCalendar.getNewCalendar());
							myFolderDaoForDML.saveOrUpdate(myfolder);

						}


						newMyTemplatesSubWinId$okBtnId.removeAttribute("folderName");
						newMyTemplatesSubWinId.setVisible(false);

						MessageUtil.setMessage("New folder is" +msgStr+"  with the name : '" + folderName + "'", "color:blue", "TOP");

						getNewEditorTemplatesFromDb(currentUser.getUserId());

					}catch (Exception e) {
						newMyTemplatesSubWinId$folderErrorMsgLblId.setValue("Problem in creating the new folder");
						logger.error("Exception ::" , e);;
						return;
					}
				}else{

					MessageUtil.setMessage("Folder name already exists.", "color:red", "TOP");
					return;
				}

			} catch (Exception e) {
				logger.error("problem occured from the createNewFolder method ",e);
			}

		} // onClick$okBtnId$newGallerySubWinId
*/
/*		public void onClick$createTemplateTBarBtnId(){

			session.removeAttribute("isDRTemplateEdit");
			session.removeAttribute("DRTemplate");
			session.removeAttribute("isDraftTemplateEdit");

			Redirect.goTo(PageListEnum.CAMPAIGN_DIGITAL_RECEIPTS_TEMPALTES_DRAG_AND_DROP_EDIOTR);
		}

*/	
		public void onClick$createTemplateTBarBtnId(){

			session.removeAttribute("isDRTemplateEdit");
			session.removeAttribute("DRTemplate");
			session.removeAttribute("isDraftTemplateEdit");
			
			MyTempEditorController.getPlaceHolderList();
			EditorController.getCouponsList();

			Redirect.goTo(PageListEnum.CAMPAIGN_E_RECEIPT_BEE_EDITOR);
		}
		
		private boolean sendTestMail(String emailId) {

			try {
				boolean isValid = true;			
				if(emailId != null && emailId.trim().length() > 0) {

					if(logger.isDebugEnabled())logger.debug("Sending the test mail....");

					MessageUtil.clearMessage();
					testDRWinId$msgLblId.setValue("");
					String[] emailArr = null;
					if(isValid) {

						emailArr = emailId.split(",");
						for (String email : emailArr) {

							if(!Utility.validateEmail(email.trim())){
								testDRWinId$msgLblId.setValue("Invalid Email address:'"+email+"'");
								isValid = false;
								break;
							}


						}//for
					}

					if(isValid) {
						for (String email : emailArr) {

							Utility.sendInstantMail(null, cSubTb.getValue(), digitalRecieptsCkEditorId.getValue(),
									Constants.EQ_TYPE_TEST_DIGITALRCPT, email, null );

						}//for
						testDRWinId.setVisible(false);
						MessageUtil.setMessage("Test email will be sent in a moment.", "color:blue", "TOP");

					}		

				}else {
					isValid = false;
					testDRWinId$msgLblId.setValue("Invalid Email address");
					testDRWinId$msgLblId.setVisible(true);
				}
				return isValid;
			}
			catch(Exception e) {
				logger.error("** Exception : " , e);
				return false;
			}

		}

		public boolean validateDRContent() {

			String ckEditorContent = digitalRecieptsCkEditorId.getValue();
			boolean isValid = true;
			if (ckEditorContent == null || ckEditorContent.length() < 1) {
				MessageUtil.setMessage("Please provide content to save.", "red","top");
				isValid = false;
			}

			String isValidPhStr = Utility.validatePh(ckEditorContent, currentUser);

			if(isValid) {

				if(isValidPhStr != null && !isValidPhStr.equals("GEN_updatePreferenceLink")){

					MessageUtil.setMessage("Invalid Placeholder: "+isValidPhStr, "red");
					isValid = false;
				}else if(isValidPhStr != null && isValidPhStr.equals("GEN_updatePreferenceLink")){

					MessageUtil.setMessage("You have currently disabled subscriber preference center setting.\n To continue, either enable this setting or remove \n 'Subscriber Preference Link' placeholder from your content.", "color:red");
					isValid = false;

				}

			}
			if(isValid) {
				String isValidCouponAndBarcode = null;
				isValidCouponAndBarcode = Utility.validateCCPh(ckEditorContent, currentUser);
				if(isValidCouponAndBarcode != null){
					isValid = false;
				}
			}
			return isValid;
		}

		public void onClick$submitBtnId() {

			boolean isValid = validateDRContent();
			if(!isValid) return;
			winId$templatNameTbId.setValue("");
			winId.setVisible(true);
			winId.setPosition("center");
			winId.doHighlighted();

		}

/*		public void onClick$saveInMyTempBtnId$winId() {

			if (winId$templatNameTbId.getValue().trim().equals("")) {
				MessageUtil.setMessage("Template name cannot be left empty.", "red","top");
				return;
			}

			DigitalReceiptMyTemplate digitalReceiptMyTemplate = digiRecptMyTemplatesDao.findByUserNameAndTemplateName(currentUser.getUserOrganization().getUserOrgId(),winId$templatNameTbId.getValue());

			if (digitalReceiptMyTemplate != null) {

				MessageUtil.setMessage("Template name already exists, please choose another name.","red", "top");
				return;
			}

			logger.info("saving the object");

			// Replace PH comments
			String editorContent = digitalRecieptsCkEditorId.getValue();

			String isValidPhStr = Utility.validatePh(editorContent, currentUser);

			if(isValidPhStr != null && !isValidPhStr.equals("GEN_updatePreferenceLink")){

				MessageUtil.setMessage("Invalid Placeholder: "+isValidPhStr, "red");
				return ;
			}else if(isValidPhStr != null && isValidPhStr.equals("GEN_updatePreferenceLink")){

				MessageUtil.setMessage("You have currently disabled subscriber preference center setting.\n To continue, either enable this setting or remove \n 'Subscriber Preference Link' placeholder from your content.", "color:red");
				return;

			}

			String isValidCouponAndBarcode = null;
			isValidCouponAndBarcode = Utility.validateCCPh(digitalRecieptsCkEditorId.getValue(), currentUser);
			if(isValidCouponAndBarcode != null){
				return ;
			}

			editorContent = editorContent.replace("<!--##BEGIN ITEMS##-->","##BEGIN ITEMS##")
					.replace("<!--##END ITEMS##-->","##END ITEMS##");
			editorContent = editorContent.replace("<!--##BEGIN PAYMENTS##-->","##BEGIN PAYMENTS##")
					.replace("<!--##END PAYMENTS##-->","##END PAYMENTS##");

			String templateName = winId$templatNameTbId.getValue();
			digitalReceiptMyTemplate = new DigitalReceiptMyTemplate(templateName, editorContent,Calendar.getInstance(), currentUser.getUserId(), currentUser.getUserOrganization().getUserOrgId());

			winId.setVisible(false);
			digitalReceiptMyTemplate.setModifiedDate(Calendar.getInstance());
			digiRecptMyTemplatesDaoForDML.saveOrUpdate(digitalReceiptMyTemplate);

			MessageUtil.setMessage("Template '" + templateName + "' saved successfully.", "green", "top");
			if(currentUser.isEnableStoreWiseTemplates()) {

				setMyTemplates();
			}
			tabBoxId.setSelectedIndex(0);
			//onClick$tabBoxId();
		}
*/

		private Window previewWin;
		//private Html previewWin$html;

		private Iframe previewWin$html;

		@Override
		public void onEvent(Event event) throws Exception {
			// TODO Auto-generated method stub
			super.onEvent(event);

			if (event.getTarget() instanceof Listitem) {
				Listitem li = (Listitem) event.getTarget();

				setEditorContent(li);

			}

			if (event.getTarget() instanceof Toolbarbutton) {

				Object eventTarget = event.getTarget();
				Toolbarbutton tb = (Toolbarbutton)eventTarget;

				boolean isEdit=false;

				String type = (String)tb.getAttribute("type");

				
				if(type.equalsIgnoreCase("EditMyTemplate")){
					DigitalReceiptMyTemplate digitalReceiptMyTemplate = (DigitalReceiptMyTemplate)tb.getAttribute("Template");
					isEdit=true;
					String editorType = digitalReceiptMyTemplate.getEditorType();
					PageListEnum  page = null;
					logger.info("editor type is"+editorType);
					if(editorType.equalsIgnoreCase(Constants.EDITOR_TYPE_BEE)) {
						if(digitalReceiptMyTemplate.getAutoSaveHtmlContent()!=null && digitalReceiptMyTemplate.getAutoSaveJsonContent()!=null ){

							int confirm = Messagebox.show("A more recent version of this template is saved\n as draft.  Click 'Cancel' to review draft by\n clicking on 'Draft' icon. Else, click 'OK' to\n edit published version (this may overwrite\n changes saved in draft version of this template).", "Confirm", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);

							if(confirm == Messagebox.CANCEL) {
								return;
							}else if(confirm == Messagebox.OK){
								sessionScope.setAttribute( "DReditorType", editorType);
								sessionScope.setAttribute("DRTemplate", digitalReceiptMyTemplate);

								sessionScope.setAttribute("isDRTemplateEdit",isEdit);

								session.setAttribute("editTemplate",true);
								session.setAttribute("editTemplatefolder",selectfolder);

								page = PageListEnum.CAMPAIGN_E_RECEIPT_BEE_EDITOR;
							}
						}
						else {

							sessionScope.setAttribute( "DReditorType", editorType);
							sessionScope.setAttribute("DRTemplate", digitalReceiptMyTemplate);
							sessionScope.setAttribute("isDRTemplateEdit",isEdit);

							session.setAttribute("editTemplate",true);
							session.setAttribute("editTemplatefolder",selectfolder);

							page = PageListEnum.CAMPAIGN_E_RECEIPT_BEE_EDITOR;
						}
					}else if(editorType.equalsIgnoreCase(OCConstants.LEGACY_EDITOR)){

						sessionScope.setAttribute("DRTemplate", digitalReceiptMyTemplate);
						sessionScope.setAttribute("isDRTemplateEdit",isEdit);

						page = PageListEnum.CAMPAIGN_E_RECEIPT_LEGACY_EDITOR;
					}

					if(page!=null) 
						Redirect.goTo(page);
					else return;

				}
				else if(type.equalsIgnoreCase("Preview")){ 

					DigitalReceiptMyTemplate digitalReceiptMyTemplate = (DigitalReceiptMyTemplate)tb.getAttribute("MyTemplate");
					
					if (digitalReceiptMyTemplate.getEditorType().equals(OCConstants.DRAG_AND_DROP_EDITOR)){
						if ( digitalReceiptMyTemplate.getContent()!=null && digitalReceiptMyTemplate.getJsonContent()!=null) {
							String htmlContentForPreview = Utility.mergeTagsForPreviewAndTestMail(digitalReceiptMyTemplate.getContent(),"preview");
							Utility.showPreview(previewWin$html, currentUser.getUserName(), htmlContentForPreview);
						}else { 
							String htmlContentForPreview = Utility.mergeTagsForPreviewAndTestMail(digitalReceiptMyTemplate.getAutoSaveHtmlContent(),"preview");
							Utility.showPreview(previewWin$html, currentUser.getUserName(), htmlContentForPreview);
						}
					}
					else {
						String htmlContentForPreview = Utility.mergeTagsForPreviewAndTestMail(digitalReceiptMyTemplate.getContent(),"preview");
						Utility.showPreview(previewWin$html, currentUser.getUserName(), htmlContentForPreview);
					}
					
					previewWin.setVisible(true);
				}
				else if(type.equalsIgnoreCase("CopyMyTemplate")) {

					DigitalReceiptMyTemplate dRtemplate = (DigitalReceiptMyTemplate)tb.getAttribute("MyTemplate");
					copywinId.setAttribute("TemplateCopy", dRtemplate);
					isEdit= true;

					String editorType = dRtemplate.getEditorType();

					copywinId.setVisible(true);
					copywinId.setPosition("center");
					String nameStr=dRtemplate.getName();
					if(nameStr.length() > 17 ){

						nameStr = nameStr.substring(0, 16);
						copywinId$templatNameTbId.setValue("Copy of "+nameStr);
					}else{

						copywinId$templatNameTbId.setValue("Copy of "+nameStr);
					}

					getMyFoldersFromDb(editorType);
					
					copywinId$folderId.setVisible(false);
					
					copywinId.doHighlighted();
					
				}else if(type.equalsIgnoreCase("DraftTemplate")) {

					DigitalReceiptMyTemplate dRtemplate = (DigitalReceiptMyTemplate)tb.getAttribute("DraftMyTemplate");

					isEdit=true;
					String editorType = dRtemplate.getEditorType();
					PageListEnum  page = null;
					logger.info("editor type is"+editorType);
					if(editorType.equalsIgnoreCase(Constants.EDITOR_TYPE_BEE)) {

						page = PageListEnum.CAMPAIGN_E_RECEIPT_BEE_EDITOR;
					}else if(editorType.equalsIgnoreCase(OCConstants.LEGACY_EDITOR)){

						page = PageListEnum.CAMPAIGN_E_RECEIPT_LEGACY_EDITOR;
					}

					sessionScope.setAttribute("DRTemplate", dRtemplate);
					sessionScope.setAttribute("isDraftTemplateEdit",isEdit);

					Redirect.goTo(page);
				}else if(type.equalsIgnoreCase("RenameMoveTemplate")){
					DigitalReceiptMyTemplate dRtemplate = (DigitalReceiptMyTemplate)tb.getAttribute("Template");
					if(dRtemplate!=null){
						
						RenameMoveTemplatewinId$folderErrorMsgLblId.setValue("");
						RenameMoveTemplatewinId.setAttribute("RenameMoveTemplate", dRtemplate);
						isEdit= true;

						RenameMoveTemplatewinId.setVisible(true);
						RenameMoveTemplatewinId.setPosition("center");
						String nameStr=dRtemplate.getName();
						if(nameStr.length() > 35 ){
							nameStr = nameStr.substring(0,34);
							RenameMoveTemplatewinId$templatNameTbId.setValue(""+nameStr);
						}else{
							RenameMoveTemplatewinId$templatNameTbId.setValue(""+nameStr);
						}
						RenameMoveTemplatewinId$templatNameTbId.setValue(nameStr);
						getMyFolders(dRtemplate.getFolderName());
						RenameMoveTemplatewinId.doHighlighted();
					}

				}else if(type.equalsIgnoreCase("RenameTemplateName")){
					DigitalReceiptMyTemplate dRtemplate = (DigitalReceiptMyTemplate)tb.getAttribute("MyTemplate");
					if(dRtemplate!=null){
						
						RenameMoveTemplatewinId$folderErrorMsgLblId.setValue("");
						RenameMoveTemplatewinId.setAttribute("RenameMoveTemplate", dRtemplate);
						isEdit= true;

						RenameMoveTemplatewinId.setVisible(true);
						RenameMoveTemplatewinId.setPosition("center");
						String nameStr=dRtemplate.getName();
						if(nameStr.length() > 35 ){
							nameStr = nameStr.substring(0,34);
							RenameMoveTemplatewinId$templatNameTbId.setValue(""+nameStr);
						}else{
							RenameMoveTemplatewinId$templatNameTbId.setValue(""+nameStr);
						}
						RenameMoveTemplatewinId$templatNameTbId.setValue(nameStr);
						RenameMoveTemplatewinId.doHighlighted();
					}

				}
				else if(type.equalsIgnoreCase("SendTestMail")){
					DigitalReceiptMyTemplate dRtemplate = (DigitalReceiptMyTemplate)tb.getAttribute("SendTestMail");
					if(dRtemplate!=null){

						testWinId.setAttribute("TestMailTemplate", dRtemplate);
						testWinId$testTbId.setValue("");
						testWinId.setVisible(true);
						testWinId.setPosition("center");
						testWinId.doHighlighted();
					}
				}else if(type.equalsIgnoreCase("delete")){
					
					DigitalReceiptMyTemplate dRtemplate = (DigitalReceiptMyTemplate)tb.getAttribute("MyTemplate");
					

/*					
 * TODO deletion confirmation
 * boolean isConfigured = digitalReceiptUserSettingsDao.isTemplateConfigured(dRtemplate.getMyTemplateId());
					if(isConfigured){

						int confirm = Messagebox.show("Del done in the template will be published " + "and will reflect in all digital receipts sent in future. " + "Do you want to continue?", "Information", 
								Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
						if(confirm != 1) return;

					}*/
					boolean isConfigured = digitalReceiptUserSettingsDao.isTemplateConfigured(dRtemplate.getMyTemplateId());
					if(isConfigured){
						MessageUtil.setMessage("Template can't be deleted. It's configured to e-Receipt.", "color:red", "TOP");
						return;
					}else 
					{
						int confirm = Messagebox.show("Are you sure you want to delete the Template - "
									+ dRtemplate.getName() + "?",
									"Confirm", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
					if(confirm != 1){
							return;
						}
					}
					digiRecptMyTemplatesDaoForDML.deleteTempLabelById(dRtemplate.getMyTemplateId());
					try {
					String imgPath	=USER_PARENT_DIR+File.separator+currentUser.getUserName()+USER_NEW_EDITOR_THUMBNAIL_DIR+File.separator+dRtemplate.getName();
					File pathToDelete = new File(imgPath);
						if(pathToDelete.exists() && pathToDelete.isDirectory()) {
							FileUtils.deleteDirectory(pathToDelete);
						}
					}catch (Exception e) {
						logger.error("Exception while deletting directory in DR id : "+dRtemplate.getMyTemplateId()+" Name : "+dRtemplate.getName()+"");
					}
					onSelect$newEditorTemplatesListId();
				}
				
			}
			else if(event.getTarget() instanceof Paging){
				Paging paging = (Paging) event.getTarget();
				int desiredPage = paging.getActivePage();
				PagingEvent pagingEvent = (PagingEvent) event;
				int pSize = pagingEvent.getPageable().getPageSize();
				int ofs = desiredPage * pSize;
				setMyTemplates(newEditorTemplatesListId.getSelectedItem().getLabel(),ofs,(byte) pagingEvent.getPageable().getPageSize(),null);
				/*if(newEditorTemplatesListId.getSelectedItem().getLabel().equals(OCConstants.Categories)){
				}
				else if(newEditorTemplatesListId.getSelectedItem().getLabel().equals(OCConstants.Starred)){
					List<Long> tempIds=digiRecptMyTemplatesDao.getAllStarredTempByUserId(currentUser.getUserId());
					StringBuilder sb=new StringBuilder();
					String subqury=null;
					if(tempIds!=null && tempIds.size()>0 ){
						for(Long ids:tempIds){
							if(sb.length()>0)sb.append(",");
							sb.append(ids);
						}
						 subqury="AND myTemplateId IN("+sb.toString()+") AND markedDeleted=false";
					}
					setMyTemplates(subqury,ofs,(byte) pagingEvent.getPageable().getPageSize(),null);
				}
				else{
				setMyTemplates( newEditorTemplatesListId.getSelectedItem().getValue(),ofs,(byte) pagingEvent.getPageable().getPageSize(),null);
				}*/
			}
			else if (event.getTarget() instanceof Image) {
				Object eventTarget = event.getTarget();
				Image img = (Image)eventTarget;
				manageFolderMpId.open(img,"after_start"); 
				manageFolderMpId.setAttribute("target", img);
			}

		}
		
		
		/** 28_02_2019
		 * */
/*		@Override
		public void onEvent(Event event) throws Exception {
			// TODO Auto-generated method stub
			super.onEvent(event);

		
			 if (event.getTarget() instanceof Checkbox) {
				Checkbox chk = (Checkbox) event.getTarget();
				logger.info("Check item "+chk.isChecked());
				Listbox box=(Listbox) chk.getParent().getParent().getParent();
				List<Listitem> items=box.getItems();
				StringBuilder sb=new StringBuilder();
				for(Listitem item:items){
					Checkbox chk1=(Checkbox) item.getFirstChild().getFirstChild();
					if(chk1.isChecked()){
						item.setSelected(true);
						DRTemplateLabel label=item.getValue();
						if(sb.length()>0)sb.append(",");
						sb.append(label.getLabelId());
					}
					else{
						item.setSelected(false);
					}
				}
				labelIds=sb.toString();
				templatePagingId.setAttribute("LabelIDS", labelIds);
				int count=box.getSelectedCount();
				Components.removeAllChildren(templListDivId);
				 int n = Integer.parseInt(templatePerPageLBId.getSelectedItem().getLabel().trim());
					templatePagingId.setTotalSize(digiRecptMyTemplatesDao.getTotalCountByLabel(accessAccIds,labelIds,count,null));
					templatePagingId.setPageSize(n);
					templatePagingId.setActivePage(0);
					templatePagingId.addEventListener("onPaging", this);
				if(count>0)
				setTemplateByLabel(labelIds,count,0,templatePagingId.getPageSize(),null);
				
			}
			else if (event.getTarget() instanceof Image) {
				logger.info("img clicked ...");
				Image img = (Image) event.getTarget();
				String type=(String) img.getAttribute("type");
				if(type.equals("AddNewLabel")){
					logger.info(" ======inside add new label=========");
					newLabelCreationWinId$newLabelName.setText(Constants.STRING_NILL);
					newLabelCreationWinId.setVisible(true);
					newLabelCreationWinId.setPosition("center");
					newLabelCreationWinId.doHighlighted();
				}
				if(type.equals("EditLabel")){
					logger.info("=inside edit label========");
					labelSetting.setVisible(true);
					labelSetting.setPosition("center");
					labelSetting.doHighlighted();
					getLabelDetail();
				}
				if(type.equalsIgnoreCase("edit")){
					newLabelCreationWinId$newLabelName.setValue(Constants.STRING_NILL);
					newLabelCreationWinId.setVisible(true);
					newLabelCreationWinId.setPosition("center");
					newLabelCreationWinId.doHighlighted();
					newLabelCreationWinId.setTitle("Edit Label info");
					newLabelCreationWinId$okBtnId.setLabel("Update");
					Listitem item=(Listitem) img.getParent().getParent().getParent();
					DRTemplateLabel label=item.getValue();
					Listitem temp=new Listitem();
				    List<Listitem> items=	newLabelCreationWinId$LabelsubAccListId.getItems();
				    for(Listitem itm:items){
				    	Account acc=itm.getValue();
				    	if(acc.getAccountId().equals(label.getSubAccountId())){
				    		newLabelCreationWinId$LabelsubAccListId.setSelectedItem(itm);
				    		break;
				    	}
				    }
					newLabelCreationWinId$LabelsubAccListId.setDisabled(true);
					newLabelCreationWinId$newLabelName.setText(label.getLabelName());
					newLabelCreationWinId$okBtnId.setAttribute("DRTemplatLabel", label);
				}
				if(type.equalsIgnoreCase("delete")){
					Listitem item=(Listitem) img.getParent().getParent().getParent();
					DRTemplateLabel label=item.getValue();
					int confirm = Messagebox.show("Are you sure you want to delete the label - "
							+ label.getLabelName() + "?",
							"Confirm", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
					if(confirm != 1){
						return;
					}
					List<DigitalReceiptMyTemplate> myTemplatesList = digiRecptMyTemplatesDao.findDRMyTemplatesByLabel(""+label.getSubAccountId(),""+label.getLabelId(),1,0,1,null);
					if(myTemplatesList!=null){
						MessageUtil.setMessage("Label can't be deleted. It's belong to template.", "color:red", "TOP");
						return;
					}
				    digiRecptMyTemplatesDaoForDML.deleteTempLabelById(label.getLabelId());
					drTemplateLabelDAOForDML.delete(label);
					labelSetting$labelListLbId.removeChild(item);
					onSelect$newEditorTemplatesListId();
				}
			}
			else if (event.getTarget() instanceof Toolbarbutton) {
				Object eventTarget = event.getTarget();
				Toolbarbutton tb = (Toolbarbutton)eventTarget;
				boolean isEdit=false;
				Hbox hb=(Hbox) tb.getParent();
				Vbox vb=(Vbox) hb.getParent();
				String type = (String)tb.getAttribute("type");
				DigitalReceiptMyTemplate digitalReceiptMyTemplate = (DigitalReceiptMyTemplate)tb.getAttribute(MyTemplate);
				if(digitalReceiptMyTemplate==null){
					return;
				}
				Account account=filterByAccountLBId.getSelectedItem().getValue();
				if(type.equalsIgnoreCase("ShareTemplate")){
					try {
						winId.setVisible(true);
						winId.setPosition("center");
						getPowerUsers();
						SharedZone shareZoneObj = (SharedZone)tb.getAttribute("SharedTemplate");
						winId.setAttribute("TemplateShared", shareZoneObj);
						winId.setAttribute("DRTemplateShared", digitalReceiptMyTemplate);
						Set<Users> usersSet=null;
						if(shareZoneObj!=null) usersSet = shareZoneObj.getUserSet();
						if(usersSet!=null){
							List<Listitem> lbList = winId$myTempListId.getItems();

							for(Object obj:usersSet){

								Users userslist = (Users)obj;
								for(Listitem li:lbList){

									Users lbusers = (Users)li.getValue();
									if(lbusers.getUserName().equals(userslist.getUserName())){
										winId$myTempListId.addItemToSelection(li);
									}
								} //for
							} //for
						}
						winId.doHighlighted();

					} catch (Exception e) {
						// TODO Auto-generated catch block
						logger.error("Exception ::" , e);;
					}
				}
				else if(type.equalsIgnoreCase("EditMyTemplate")){
					DigitalReceiptMyTemplate digitalReceiptMyTemplateLatest= digiRecptMyTemplatesDao.findByAccountIdAndTemplateId(digitalReceiptMyTemplate.getSubAccountId(),digitalReceiptMyTemplate.getMyTemplateId());
					if(digitalReceiptMyTemplateLatest==null){
						MessageUtil.setMessage("Template not found.","red", "top");
						return;
					}
					sessionScope.setAttribute("EditSelectedAccount", account);
					isEdit=true;
					String editorType = digitalReceiptMyTemplate.getEditorType();
					PageListEnum  page = null;
					logger.info("editor type is"+editorType);
					if(editorType.equalsIgnoreCase(Constants.EDITOR_TYPE_BEE)) {

						if(digitalReceiptMyTemplate.getAutoSaveHtmlContent()!=null && digitalReceiptMyTemplate.getAutoSaveJsonContent()!=null ){

						int confirm = Messagebox.show("A more recent version of this template is saved as draft. Click 'Cancel' to review draft by clicking on 'Draft' icon. Else, click 'OK' to edit published version (this may overwrite changes saved in draft version of this template).", "Confirm", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
						if(confirm == Messagebox.CANCEL) {
							return;
						}else if(confirm == Messagebox.OK){
							sessionScope.setAttribute( "DReditorType", editorType);
							sessionScope.setAttribute("DRTemplate", digitalReceiptMyTemplate);
							sessionScope.setAttribute("isDRTemplateEdit",isEdit);
							session.setAttribute("editTemplate",true);
							page = PageListEnum.CAMPAIGN_DIGITAL_RECEIPTS_TEMPALTES_DRAG_AND_DROP_EDIOTR;
						}
					}
						else {

							sessionScope.setAttribute( "DReditorType", editorType);
							sessionScope.setAttribute("DRTemplate", digitalReceiptMyTemplate);
							sessionScope.setAttribute("isDRTemplateEdit",isEdit);
							page = PageListEnum.CAMPAIGN_DIGITAL_RECEIPTS_TEMPALTES_DRAG_AND_DROP_EDIOTR;
						}
					}
					tb.removeAttribute(MyTemplate);
					if(page!=null) 
						Redirect.goTo(page);
					else return;

				}
				else if(type.equalsIgnoreCase("Preview")){ 
					if (digitalReceiptMyTemplate.getContent()!=null && digitalReceiptMyTemplate.getJsonContent()!=null)
						Utility.showPreview(previewWin$html, AccountHelper.getAccountNameById(digitalReceiptMyTemplate.getAccountId()), digitalReceiptMyTemplate.getContent());
					else
						Utility.showPreview(previewWin$html, AccountHelper.getAccountNameById(digitalReceiptMyTemplate.getAccountId()), digitalReceiptMyTemplate.getAutoSaveHtmlContent());
					previewWin.setVisible(true);

				}
				else if(type.equalsIgnoreCase("CopyMyTemplate")) {
					DigitalReceiptMyTemplate dRtemplate = (DigitalReceiptMyTemplate)tb.getAttribute(MyTemplate);
					copywinId$cpmvRGId.setAttribute("MyTemplate", dRtemplate);
					isEditTempalteDRMyTemplates=dRtemplate;
					copywinId.setAttribute("TemplateCopy", dRtemplate);
					copywinId$cpmvRGId.setSelectedIndex(0);
					isEdit= true;
					String editorType = dRtemplate.getEditorType();
					copywinId.setVisible(true);
					copywinId.setPosition("center");
					copywinId$folderErrorMsgLblId.setValue("");
					copywinId.doHighlighted();
					newLabelCreationWinId.setVisible(false);
					onCheck$cpmvRGId$copywinId();
				}else if(type.equalsIgnoreCase("DraftTemplate")) {
					sessionScope.setAttribute("EditSelectedAccount", account);
					isEdit=true;
					String editorType = digitalReceiptMyTemplate.getEditorType();
					PageListEnum  page = null;
					if(editorType.equalsIgnoreCase(Constants.EDITOR_TYPE_BEE)) {
						page = PageListEnum.CAMPAIGN_DIGITAL_RECEIPTS_TEMPALTES_DRAG_AND_DROP_EDIOTR;
					}else if(editorType.equalsIgnoreCase(OCConstants.LEGACY_EDITOR)){

						page = PageListEnum.DIGITAL_RECEIPTS_TEMPALTES_LEGACY_EDIOTR;
					}
					sessionScope.setAttribute("DRTemplate", digitalReceiptMyTemplate);
					sessionScope.setAttribute("isDraftTemplateEdit",isEdit);
	      
					Redirect.goTo(page);
				}else if(type.equalsIgnoreCase("EditTemplateSettings")){

					RenameMoveTemplatewinId$folderErrorMsgLblId.setValue("");
					RenameMoveTemplatewinId.setAttribute("RenameMoveTemplate", digitalReceiptMyTemplate);
					isEdit= true;
					RenameMoveTemplatewinId.setVisible(true);
					RenameMoveTemplatewinId.setPosition("center");
					RenameMoveTemplatewinId.doHighlighted();
					String nameStr=digitalReceiptMyTemplate.getName();
					List<Listitem> items=RenameMoveTemplatewinId$accountLbId.getItems();
					for(Listitem item:items){
						Account acc=item.getValue();
						if(acc.getAccountId().equals(digitalReceiptMyTemplate.getSubAccountId())){
							RenameMoveTemplatewinId$accountLbId.setSelectedItem(item);
							break;
						}
					}
					RenameMoveTemplatewinId$accountLbId.setDisabled(true);
					if((nameStr).startsWith(OCConstants.DRAG_DROP_EDITOR_DR_TEMPLATES_FOLDER_DRAFT)){
						RenameMoveTemplatewinId$templatNameTbId.setValue("");
					}
					else{
					  if(nameStr.length() > 35 ){
						nameStr = nameStr.substring(0,34);
						RenameMoveTemplatewinId$templatNameTbId.setValue(""+nameStr);
					   }else{
						RenameMoveTemplatewinId$templatNameTbId.setValue(""+nameStr);
					 }
					}
					onSelect$accountLbId$RenameMoveTemplatewinId();
					List<Long> labelIds=digiRecptMyTemplatesDao.getLabelsByTempId(digitalReceiptMyTemplate.getMyTemplateId());
					if(labelIds!=null && !labelIds.isEmpty()){
					List<Listitem> listitems=RenameMoveTemplatewinId$labellistboxId.getItems();
					Set<Listitem> selectedSet=new HashSet<>();
					  for(Listitem item:listitems){
						DRTemplateLabel label=item.getValue();
						for(Long id:labelIds){
						 if(label!=null && label.getLabelId().equals(id)){
							 selectedSet.add(item);
						 break;
					    }
					    }
					  }
					  RenameMoveTemplatewinId$labellistboxId.setSelectedItems(selectedSet);
					}
				}
				else if(type.equalsIgnoreCase("SendTestMail")){
					DigitalReceiptMyTemplate dRtemplate = (DigitalReceiptMyTemplate)tb.getAttribute(MyTemplate);
					if(dRtemplate!=null){

						testWinId.setAttribute("TestMailTemplate", dRtemplate);
						testWinId$testTbId.setValue("");
						testWinId.setVisible(true);
						testWinId.setPosition("center");
						testWinId.doHighlighted();
					}
				}
				else if(type.equalsIgnoreCase("deleteTemporary")){
					boolean isConfigured = digitalReceiptUserSettingsDao.isTemplateConfigured(digitalReceiptMyTemplate.getMyTemplateId());
					if(isConfigured){
						MessageUtil.setMessage("Template can't be deleted. It's configured to Digital Receipt.", "color:red", "TOP");
						return;
					}
					else{
						int confirm = Messagebox.show("Are you sure you want to delete the template - "
								+ digitalReceiptMyTemplate.getName() + "?",
								"Confirm", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
						if(confirm != 1){
							return;
						}
						    digiRecptMyTemplatesDaoForDML.deleteTemplateLabel(digitalReceiptMyTemplate.getMyTemplateId());
							digiRecptMyTemplatesDaoForDML.deleteAsStarredTemplate(digitalReceiptMyTemplate.getMyTemplateId(), currentUser.getUserId());
						int count=digiRecptMyTemplatesDaoForDML.deleteTemplateTemporary(digitalReceiptMyTemplate.getSubAccountId(),digitalReceiptMyTemplate.getMyTemplateId());
						if(count>0)
							MessageUtil.setMessage("Template '" + digitalReceiptMyTemplate.getName() + "' deleted successfully.", "green", "top");	
					} 
						int total=templatePagingId.getTotalSize();
						templatePagingId.setTotalSize(total-1);
					templListDivId.removeChild(vb);
				}
				else if(type.equalsIgnoreCase("deletePermanently")){
					boolean isConfigured = digitalReceiptUserSettingsDao.isTemplateConfigured(digitalReceiptMyTemplate.getMyTemplateId());
					if(isConfigured){
						MessageUtil.setMessage("Template can't be deleted. It's configured to Digital Receipt.", "color:red", "TOP");
						return;
					}
					else{
						int confirm = Messagebox.show("Are you sure you want to delete the template permanently- "
								+ digitalReceiptMyTemplate.getName() + "?",
								"Confirm", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
						if(confirm != 1){
							return;
						}
						   digiRecptMyTemplatesDaoForDML.deleteTemplateLabel(digitalReceiptMyTemplate.getMyTemplateId());
							digiRecptMyTemplatesDaoForDML.deleteAsStarredTemplate(digitalReceiptMyTemplate.getMyTemplateId(), currentUser.getUserId());
						  digiRecptMyTemplatesDaoForDML.deleteTemplatePermanantly(digitalReceiptMyTemplate.getSubAccountId(),digitalReceiptMyTemplate.getMyTemplateId());
							MessageUtil.setMessage("Template '" + digitalReceiptMyTemplate.getName() + "' deleted successfully.", "green", "top");	
					}
					int total=templatePagingId.getTotalSize();
					templatePagingId.setTotalSize(total-1);
					templListDivId.removeChild(vb);
				} 

				else if(type.equalsIgnoreCase("restoreTemplate")){
					int confirm = Messagebox.show("Are you sure you want to restore this template "
							+ digitalReceiptMyTemplate.getName() + "?",
							"Confirm", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
					if(confirm != 1){
						return;
					}
					int count=digiRecptMyTemplatesDaoForDML.restoreTemplateBy(digitalReceiptMyTemplate.getSubAccountId(),digitalReceiptMyTemplate.getMyTemplateId());
					if(count>0)
						MessageUtil.setMessage("Template '" + digitalReceiptMyTemplate.getName() + "' restored successfully.", "green", "top");
					int total=templatePagingId.getTotalSize();
					templatePagingId.setTotalSize(total-1);
					templListDivId.removeChild(vb);
					//	setMyTemplates();		
				} 
				else if(type.equalsIgnoreCase("starredTemplate")){
					logger.info(" ===========event raise on starred =======");
				   if(tb.getTooltiptext().equals("Unstar")){
					tb.setImage("/img/theme/star.png");
					tb.setTooltiptext("Star");
					digiRecptMyTemplatesDaoForDML.deleteAsStarredTemplate(digitalReceiptMyTemplate.getMyTemplateId(), currentUser.getUserId());
					  }
					else{
						tb.setImage("/img/theme/filled_stared.png");
						tb.setTooltiptext("Unstar");
						digiRecptMyTemplatesDaoForDML.insertAsStarredTemplate(digitalReceiptMyTemplate.getMyTemplateId(), currentUser.getUserId());
					}
			} 
				if(newEditorTemplatesListId.getSelectedItem().getLabel().equalsIgnoreCase(OCConstants.Starred)){
					int total=templatePagingId.getTotalSize();
				    templatePagingId.setTotalSize(total-1);
					templListDivId.removeChild(vb);
				}
			}
			else if(event.getTarget() instanceof Paging){
				Paging paging = (Paging) event.getTarget();
				int desiredPage = paging.getActivePage();
				PagingEvent pagingEvent = (PagingEvent) event;
				int pSize = pagingEvent.getPageable().getPageSize();
				int ofs = desiredPage * pSize;
				if(newEditorTemplatesListId.getSelectedItem().getLabel().equals(OCConstants.Categories)){
				}
				else if(newEditorTemplatesListId.getSelectedItem().getLabel().equals(OCConstants.Starred)){
					List<Long> tempIds=digiRecptMyTemplatesDao.getAllStarredTempByUserId(currentUser.getUserId());
					StringBuilder sb=new StringBuilder();
					String subqury=null;
					if(tempIds!=null && tempIds.size()>0 ){
						for(Long ids:tempIds){
							if(sb.length()>0)sb.append(",");
							sb.append(ids);
						}
						 subqury="AND myTemplateId IN("+sb.toString()+") AND markedDeleted=false";
					}
					setMyTemplates(subqury,ofs,(byte) pagingEvent.getPageable().getPageSize(),null);
				}
				else{
				setMyTemplates( newEditorTemplatesListId.getSelectedItem().getValue(),ofs,(byte) pagingEvent.getPageable().getPageSize(),null);
				}
			}

		}*/

		public void onClick$sendTestMailBtnId$testWinId() {		
			String emailId = testWinId$testTbId.getValue();
			boolean isValid = validateEmailAddr(emailId);
			if(isValid){
				testWinId$sendTestMailBtnId.setDisabled(true);
				DigitalReceiptMyTemplate drMyTemplates=(DigitalReceiptMyTemplate)testWinId.getAttribute("TestMailTemplate");

				testWinId$msgLblId.setValue("");
				String emailId1 = testWinId$testTbId.getText();

				String[] emailArr = emailId1.split(",");
				for (String email : emailArr) {

					sendTestMail(drMyTemplates.getContent(), email);
				}//for
				testWinId.setVisible(false);
				testWinId$sendTestMailBtnId.setDisabled(false);

			}
		}

		public void onClick$cancelSendTestMailBtnId$testWinId() {
			testWinId$msgLblId.setValue("");
			testWinId$msgLblId.setValue("");
			testWinId.setVisible(false);
		}

		protected boolean sendTestMail(String htmlStuff,String emailId) {

			EmailQueueDao emailQueueDao = (EmailQueueDao)SpringUtil.getBean("emailQueueDao");

			try {
				if( htmlStuff == null || htmlStuff.isEmpty() ){
					MessageUtil.setMessage("There is no content to send a test mail.", "color:red");

					return false;
				}

				if(logger.isDebugEnabled())logger.debug("Sending the test mail....");

				MessageUtil.clearMessage();
				String isValidPhStr = null;
				isValidPhStr = Utility.validatePh(htmlStuff, currentUser);

				if(isValidPhStr != null && !isValidPhStr.equals("GEN_updatePreferenceLink")){

					MessageUtil.setMessage("Invalid Placeholder: "+isValidPhStr, "red");
					return false;
				}else if(isValidPhStr != null && isValidPhStr.equals("GEN_updatePreferenceLink")){

					MessageUtil.setMessage("You have currently disabled subscriber preference center setting.\n To continue, either  enable this setting or remove \n 'Subscriber Preference Link' placeholder from your content.", "color:red");
					return false;

				}

				String isValidCouponAndBarcode = null;
				isValidCouponAndBarcode = Utility.validateCCPh(htmlStuff, currentUser);
				if(isValidCouponAndBarcode != null){
					return false;
				}


				long size =	Utility.validateHtmlSize(htmlStuff);
				if(size >100) {
					String msgcontent = OCConstants.HTML_VALIDATION;
					msgcontent = msgcontent.replace("[size]", size+Constants.STRING_NILL);
					MessageUtil.setMessage(msgcontent, "color:Blue");
				}

				if(!checkSpam(htmlStuff)){
					return false;
				}

				String[] emailArr = null;

				emailArr = emailId.split(",");
				for (String email : emailArr) {

					if(!Utility.validateEmail(email.trim())) {

						Messagebox.show("Please enter a valid email address. '"+email+"' is invalid.", "Error", Messagebox.OK, Messagebox.ERROR);
						return false;
					}

				}//for

				//Check whether user is expired or not
				if(Calendar.getInstance().after(currentUser.getPackageExpiryDate())){
					logger.debug("Current User::"+currentUser.getUserId()+" is expired, hence cannot send test mail");
					MessageUtil.setMessage("Your account validity period has expired. \n Please renew your subscription to continue.", "color:red", "TOP");
					return false;
				}
				//TODO
				for (String email : emailArr) {
					logger.info("Test Mail "+ email);
					Utility.sendInstantMail(null, Constants.EQ_TYPE_TEST_DIGITALRCPT, htmlStuff,
							Constants.EQ_TYPE_TEST_DIGITALRCPT, email, null );

				}//for

				Messagebox.show("Test mail will be sent in a moment.", "Information", 
						Messagebox.OK, Messagebox.INFORMATION);

				return true;
			} catch (Exception e) {
				logger.error("Exception : "+ e);
				return true;
			}

		}

		protected boolean checkSpam(String htmlStuff) {
			logger.debug("-- Just Entered --");

			boolean result = false;

			String response = getSpamResult(htmlStuff); 
			logger.debug("Spam Report : \n" + response );

			if(response==null) {
				return true;
			}

			try {
				if(response.indexOf("(")<0) {
					logger.debug("Problem while getting spam report");
					return result;
				}
				String output = response.substring(response.indexOf("Content analysis details"));
				String scoreStr = output.substring(output.indexOf("(")+1,output.indexOf(")"));
				String[] scores = scoreStr.split(" ");
				for (String token : scores) {
					logger.debug("Token : " + token);

				}

				float hit = Float.parseFloat(scores[0]);
				if(0 <= hit && hit <= 2) {
					result = true;
				}
				else if(2 < hit && hit <= 4) {
					int confirm = Messagebox.show("Email has spam content. Do you want to continue saving the email?\n" +
							"Click on \"Check for Spam score\" for more details.",
							"Confirm", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
					result = (confirm == 1);
				}
				else if(hit > 4) {
					MessageUtil.setMessage("Email has SPAM content. Click on \"Check for Spam score\" for more details.", "color:red", "TOP");
					result = false;
				}

				logger.debug(" Result : "+ result);
			} catch (NumberFormatException e) {
				logger.debug("NumberFormatException : ",(Throwable)e);
			}catch (IndexOutOfBoundsException e) {
				logger.debug("IndexOutOfBoundsException : ",(Throwable)e);
			} catch (Exception e) {
				logger.debug("Exception : ",(Throwable)e);
			}
			return result;
		}

		protected String getSpamResult(String htmlStuff) {

			if(htmlStuff == null || htmlStuff.isEmpty()) return null;

			String sub = "My Template";
			String emlFilePath = PropertyUtil.getPropertyValue("usersParentDirectory") + 
					"/" + userName + "/message.eml";
			if(logger.isDebugEnabled())logger.debug("Eml file Path : " + emlFilePath);

			StringBuffer response = (new SpamChecker()).checkSpam(sub, htmlStuff, emlFilePath, true); 
			return ( response==null?null:response.toString() );
		}

		public boolean validateEmailAddr(String emailId) {
			try {
				boolean isValid = true;			
				if(emailId != null && emailId.trim().length() > 0) {

					if(logger.isDebugEnabled())logger.debug("Sending the test mail....");

					MessageUtil.clearMessage();
					testWinId$msgLblId.setValue("");
					String[] emailArr = null;
					if(isValid) {

						emailArr = emailId.split(",");
						for (String email : emailArr) {

							if(!Utility.validateEmail(email.trim())){
								testWinId$msgLblId.setValue("Invalid Email address:'"+email+"'");
								isValid = false;
								break;
							}
else {
								//Testing invalid email domains -APP-308
								String result = PurgeList.checkForValidDomainByEmailId(email);
								if(!result.equalsIgnoreCase("Active")) {
									testWinId$msgLblId.setValue("Invalid Email address:'"+email+"'");
									isValid = false;
									break;
								}
}

						}//for
					}

				}else {
					isValid = false;
					testWinId$msgLblId.setValue("Invalid Email address");
					testWinId$msgLblId.setVisible(true);
				}
				return isValid;
			}
			catch(Exception e) {
				logger.error("** Exception : " , e);
				return false;
			}

		}

		public void onClick$myTemplatesSubmtBtnId$copywinId(){

			try{
				if(logger.isDebugEnabled())logger.debug("-- just entered --");
				DigitalReceiptMyTemplate drMyTemplates=(DigitalReceiptMyTemplate)copywinId.getAttribute("TemplateCopy");
				Textbox nameTbId = (Textbox)copywinId.getFellowIfAny("templatNameTbId");

				MessageUtil.clearMessage();

				String folderNameAttr = nameTbId.getValue();
				String templateName = nameTbId.getValue().trim();
				if(templateName.trim().isEmpty() || templateName == null) {
					copywinId$folderErrorMsgLblId.setValue("Please provide the template name.");
					return;
				}
				else if(!Utility.validateName(templateName)){
					copywinId$folderErrorMsgLblId.setValue("Provide a valid name with out any special characters");
					return;
				}
				logger.debug("exist folder name"+folderNameAttr+"::replace with:"+templateName+" path ");
				templateName = templateName.replaceAll(" +", "_");

				/*if(drMyTemplates.getEditorType().equalsIgnoreCase(Constants.EDITOR_TYPE_BEE)){
					saveInMyTemplates(templateName,drMyTemplates.getContent(),copywinId$folderId.getSelectedItem().getLabel(),drMyTemplates.getJsonContent(), true, drMyTemplates);
				}*/

				saveInMyTemplates(templateName,drMyTemplates.getContent(),copywinId$folderId.getSelectedItem().getLabel(),drMyTemplates.getJsonContent(), true, drMyTemplates);

			}catch (Exception e) {
				logger.error("** Exception :", e );
			}
		}

		public void onClick$moveMyTemplateSubmtBtnId$MoveTemplatewinId(){

			try{
				if(logger.isDebugEnabled())logger.debug("-- just entered --");
				DigitalReceiptMyTemplate drMyTemplates=(DigitalReceiptMyTemplate)MoveTemplatewinId.getAttribute("MoveTemplate");

				if(drMyTemplates!=null && drMyTemplates.getEditorType().equalsIgnoreCase(Constants.EDITOR_TYPE_BEE)){

					isMoveTemplate=true;
					if(drMyTemplates!=null)isMoveTempalteDRMyTemplates= drMyTemplates;

					saveInMyTemplates(drMyTemplates.getName(),drMyTemplates.getContent(),MoveTemplatewinId$folderId.getSelectedItem().getLabel(),drMyTemplates.getJsonContent(),drMyTemplates);
				}
			}catch (Exception e) {
				logger.error("** Exception :", e );
			}
		}
		DigitalReceiptMyTemplate isEditTempalteDRMyTemplates,isMoveTempalteDRMyTemplates,isRenameMoveDRMyTemplate;
		public void onClick$modifiedNameBtnId$editTemplateNamewinId(){

			try{
				if(logger.isDebugEnabled())logger.debug("-- just entered --");
				DigitalReceiptMyTemplate drMyTemplates=(DigitalReceiptMyTemplate)editTemplateNamewinId.getAttribute("EditTemplateName");
				Textbox nameTbId = (Textbox)editTemplateNamewinId.getFellowIfAny("templatNameTbId");

				MessageUtil.clearMessage();

				String folderNameAttr = nameTbId.getValue();
				String templateName = nameTbId.getValue().trim();
				if(templateName.trim().isEmpty() || templateName == null) {
					editTemplateNamewinId$folderErrorMsgLblId.setValue("Please provide the template Name");
					return;
				}
				else if(!Utility.validateName(templateName)){
					editTemplateNamewinId$folderErrorMsgLblId.setValue("Provide a valid name with out any special characters");
					return;
				}
				logger.debug("exist folder name"+folderNameAttr+"::replace with:"+templateName+" path ");
				templateName = templateName.replaceAll(" +", "_");

				if(drMyTemplates.getEditorType().equalsIgnoreCase(Constants.EDITOR_TYPE_BEE)){
					//saveInMyTemplates(templateName,drMyTemplates.getContent(),editTemplateNamewinId$modifiedNameBtnId.getSelectedItem().getLabel(),drMyTemplates.getJsonContent());
					isEditTemplateName=true;
					if(drMyTemplates!=null)isEditTempalteDRMyTemplates= drMyTemplates;
					saveInMyTemplates(templateName,drMyTemplates.getContent(),drMyTemplates.getFolderName(),drMyTemplates.getJsonContent(), false, null);

				}
			}catch (Exception e) {
				logger.error("** Exception :", e );
			}
		}

		public void onClick$renameMoveTemplateBtnId$RenameMoveTemplatewinId(){

			try{

				if(logger.isDebugEnabled())logger.debug("-- just entered --");
				DigitalReceiptMyTemplate drMyTemplates=(DigitalReceiptMyTemplate)RenameMoveTemplatewinId.getAttribute("RenameMoveTemplate");
				Textbox nameTbId = (Textbox)RenameMoveTemplatewinId.getFellowIfAny("templatNameTbId");

				MessageUtil.clearMessage();

				String folderNameAttr = nameTbId.getValue();
				String templateName = nameTbId.getValue().trim();
				if(templateName.trim().isEmpty() || templateName == null) {
					RenameMoveTemplatewinId$folderErrorMsgLblId.setValue("Please provide the template Name");
					return;
				}
				else if(!Utility.validateName(templateName)){
					RenameMoveTemplatewinId$folderErrorMsgLblId.setValue("Provide a valid name with out any special characters");
					return;
				}
				logger.debug("exist folder name"+folderNameAttr+"::replace with:"+templateName+" path ");
				templateName = templateName.replaceAll(" +", "_");

				if(drMyTemplates!=null && (drMyTemplates.getEditorType().equalsIgnoreCase(Constants.EDITOR_TYPE_BEE) || drMyTemplates.getEditorType().equalsIgnoreCase(OCConstants.LEGACY_EDITOR))){

					isRenameMoveTemplate=true;
					if(drMyTemplates!=null)isRenameMoveDRMyTemplate= drMyTemplates;

					saveInMyTemplate(templateName,drMyTemplates.getContent(),drMyTemplates.getJsonContent(),drMyTemplates);
				}
			}catch (Exception e) {
				logger.error("** Exception :", e );
			}
		}

		public void onClick$cancelBtnId$RenameMoveTemplatewinId(){
			if(RenameMoveTemplatewinId!=null&&RenameMoveTemplatewinId.isVisible())
				RenameMoveTemplatewinId.setVisible(false);
		}
		
		//Rename/Move Template / DataIntigrity exception
		public void saveInMyTemplate(String dRTemplateName,String beeHtmlContent,String beeJsonContent,DigitalReceiptMyTemplate dRMyTemplate){
			try {
				
/*				beeHtmlContent = beeHtmlContent.replace("<div id=\"##BEGIN ITEMS##\"></div>","##BEGIN ITEMS##")
						.replace("<div id=\"##END ITEMS##\"></div>","##END ITEMS##");
				beeHtmlContent = beeHtmlContent.replace("<div id=\"##BEGIN PAYMENTS##\"></div>", "##BEGIN PAYMENTS##")
						.replace("<div id=\"##END PAYMENTS##\"></div>", "##END PAYMENTS##");
				beeHtmlContent = beeHtmlContent.replace("<div id=\"##BEGIN REF##\"></div>","##BEGIN REF##")
						.replace("<div id=\"##END REF##\"></div>","##END REF##");
				beeHtmlContent = beeHtmlContent.replace("<div id=\"##BEGIN CHANGE PAYMENTS##\"></div>", "##BEGIN CHANGE PAYMENTS##")
						.replace("<div id=\"##END CHANGE PAYMENTS##\"></div>", "##END CHANGE PAYMENTS##");
*/
				int index=0;
				
				if(dRMyTemplate!=null){
				
					dRMyTemplate.setModifiedby(currentUser.getUserId());
					dRMyTemplate.setName(dRTemplateName);
					dRMyTemplate.setModifiedDate(Calendar.getInstance());
					digiRecptMyTemplatesDaoForDML.saveOrUpdate(dRMyTemplate);
					
					MessageUtil.setMessage("Template's name changed successfully.","green", "top");
					
					if(dRMyTemplate.getEditorType().equals(OCConstants.LEGACY_EDITOR))
						index=1;
				}

				if(RenameMoveTemplatewinId!=null&&RenameMoveTemplatewinId.isVisible())
				{
					RenameMoveTemplatewinId.setVisible(false);
					newEditorTemplatesListId.setSelectedIndex(index);
					onSelect$newEditorTemplatesListId();
				}

			} catch(DataIntegrityViolationException die) {
				if(isRenameMoveTemplate) isRenameMoveTemplate=false;

				MessageUtil.setMessage("Template with given name already exist in the selected folder.", "red","top");
				logger.error("Exception ::", die);

			} catch(Exception e) {
				logger.error("** Exception : while saving the template in MyTemplates", (Throwable)e);
				logger.error("Exception ::", e);
			}
		}

		public void saveInMyTemplates(String dRTemplateName,String beeHtmlContent,String folderName,
				String beeJsonContent, boolean isFromCopy, DigitalReceiptMyTemplate baseDRMyTemplate){

			try {

				if (beeHtmlContent!=null) {
					beeHtmlContent = beeHtmlContent.replace("<!--##BEGIN ITEMS##-->", "##BEGIN ITEMS##")
							.replace("<!--##END ITEMS##-->", "##END ITEMS##");
					beeHtmlContent = beeHtmlContent.replace("<!--##BEGIN PAYMENTS##-->", "##BEGIN PAYMENTS##")
							.replace("<!--##END PAYMENTS##-->", "##END PAYMENTS##");
				}
				if(isEditTemplateName && isEditTempalteDRMyTemplates!=null){

					DigitalReceiptMyTemplate dRMyTemplate =isEditTempalteDRMyTemplates;

					dRMyTemplate.setName(dRTemplateName);
					dRMyTemplate.setModifiedby(currentUser.getUserId());

					digiRecptMyTemplatesDaoForDML.saveOrUpdate(dRMyTemplate);
					isEditTempalteDRMyTemplates=null;

				}else{
					DigitalReceiptMyTemplate dRMyTemplate = new DigitalReceiptMyTemplate(dRTemplateName.trim(), beeHtmlContent,Calendar.getInstance(), currentUser.getUserId(), currentUser.getUserOrganization().getUserOrgId());

					dRMyTemplate.setJsonContent(beeJsonContent);
					if(baseDRMyTemplate.getEditorType().equals(Constants.EDITOR_TYPE_BEE))
						dRMyTemplate.setEditorType(Constants.EDITOR_TYPE_BEE);
					else
						dRMyTemplate.setEditorType(OCConstants.LEGACY_EDITOR);
					dRMyTemplate.setFolderName(folderName);
					dRMyTemplate.setModifiedby(currentUser.getUserId());
					if(isFromCopy && baseDRMyTemplate != null){
						dRMyTemplate.setAutoSaveHtmlContent(baseDRMyTemplate.getAutoSaveHtmlContent());
						dRMyTemplate.setAutoSaveJsonContent(baseDRMyTemplate.getAutoSaveJsonContent());

					}

					digiRecptMyTemplatesDaoForDML.saveOrUpdate(dRMyTemplate);

					MessageUtil.setMessage("Template copied successfully.","green", "top");
				}

				if(copywinId!=null&&copywinId.isVisible())
					copywinId.setVisible(false);
				else if(MoveTemplatewinId!=null&&MoveTemplatewinId.isVisible())
					MoveTemplatewinId.setVisible(false);
				else if(editTemplateNamewinId!=null&&MoveTemplatewinId.isVisible())
					editTemplateNamewinId.setVisible(false);
				Listbox lb = newEditorTemplatesListId;

				int index=0;
				if (lb != null) {
					List list = lb.getItems();
					for (Object obj : list) {
						Listitem li = (Listitem) obj;
						//logger.info("li.getLabel() ="+li.getLabel() +" = "+folderName);
												
						//logger.info("li.getLabel() ="+ ((Label)(li.getChildren().get(0))).getValue() +" = "+folderName);
						//logger.info("li.getLabel() ="+ ((Label)(li.getChildren().get(0).getChildren().get(0))).getValue() +" = "+folderName);
						
						
						logger.info("li.getLabel() ="+ ((Listcell)(li.getChildren().get(0))).getLabel() +" = "+folderName);
						
											
						//String selectFolder = ((Listcell)(li.getChildren().get(0))).getLabel();

						String selectFolder = ((Listcell)(li.getChildren().get(0))).getLabel();

						if((selectFolder!=null || selectFolder.isEmpty()) &&  selectFolder.equalsIgnoreCase(folderName)) {
							index = lb.getIndexOfItem((Listitem) obj);
							break;
						}
					}
				}
				newEditorTemplatesListId.setSelectedIndex(index);
				onSelect$newEditorTemplatesListId();


			} catch(DataIntegrityViolationException die) {
				if(isEditTempalteDRMyTemplates!=null) isEditTempalteDRMyTemplates=null;
				MessageUtil.setMessage("Template with given name already exist in the selected folder.", "red","top");
				logger.error("Exception ::", die);

			} catch(Exception e) {
				logger.error("** Exception : while saving the template in MyTemplates", (Throwable)e);
				logger.error("Exception ::", e);
			}

		}

		//For DataIntigrity exception
		public void saveInMyTemplates(String dRTemplateName,String beeHtmlContent,String folderName,String beeJsonContent,DigitalReceiptMyTemplate dRMyTemplate){

			try {


				beeHtmlContent = beeHtmlContent.replace("<div id=\"##BEGIN ITEMS##\"></div>","##BEGIN ITEMS##")
						.replace("<div id=\"##END ITEMS##\"></div>","##END ITEMS##");
				beeHtmlContent = beeHtmlContent.replace("<div id=\"##BEGIN PAYMENTS##\"></div>", "##BEGIN PAYMENTS##")
						.replace("<div id=\"##END PAYMENTS##\"></div>", "##END PAYMENTS##");
				beeHtmlContent = beeHtmlContent.replace("<div id=\"##BEGIN REF##\"></div>","##BEGIN REF##")
						.replace("<div id=\"##END REF##\"></div>","##END REF##");
				beeHtmlContent = beeHtmlContent.replace("<div id=\"##BEGIN CHANGE PAYMENTS##\"></div>", "##BEGIN CHANGE PAYMENTS##")
						.replace("<div id=\"##END CHANGE PAYMENTS##\"></div>", "##END CHANGE PAYMENTS##");

				if(isMoveTemplate){
					if(folderName!=null && folderName.equals(dRMyTemplate.getFolderName())){
						MessageUtil.setMessage("Template with given name already exist in the selected folder.", "red","top");
						return;
					}
					dRMyTemplate.setFolderName(folderName);
					dRMyTemplate.setModifiedby(currentUser.getUserId());

					digiRecptMyTemplatesDaoForDML.saveOrUpdate(dRMyTemplate);
					MessageUtil.setMessage("Template(s) moved \n successfully to '"+folderName+"' folder","green", "top");
					isMoveTemplate=false;
				}

				if(MoveTemplatewinId!=null&&MoveTemplatewinId.isVisible())
					MoveTemplatewinId.setVisible(false);

				Redirect.goTo(PageListEnum.EMPTY);
				Redirect.goTo(PageListEnum.CAMPAIGN_E_RECEIPTS);

			} catch(DataIntegrityViolationException die) {
				if(isMoveTemplate) isMoveTemplate=false;

				MessageUtil.setMessage("Template with given name already exist in the selected folder.", "red","top");
				//logger.error("** Exception : while saving template in to MyTemplates", (Throwable)die);
				logger.error("Exception ::", die);

			} catch(Exception e) {
				logger.error("** Exception : while saving the template in MyTemplates", (Throwable)e);
				logger.error("Exception ::", e);
			}

		}

		public void getMyFoldersFromDb(String editorType){

			try {
				
				Components.removeAllChildren(copywinId$folderId);
				//Components.removeAllChildren(MoveTemplatewinId$folderId);

				//MyFoldersDao myFolderDao = (MyFoldersDao) SpringUtil.getBean("myFoldersDao");
				//MyFoldersDaoForDML myFolderDaoForDML = (MyFoldersDaoForDML) SpringUtil.getBean("myFoldersDaoForDML");
			//List<MyFolders> org_Folders =  myFolderDao.findByOrgIdOrderByFolderNames(currentUser.getUserOrganization().getUserOrgId(),OCConstants.DnD_DIGITAL_RECEIPT_TEMPLATES);

				if(copywinId$folderId!=null){
								String folderName = "";
							Listitem item = new Listitem();

							if(editorType.equals(OCConstants.DRAG_AND_DROP_EDITOR))
								folderName = OCConstants.DRAG_DROP_EDITOR_DR_TEMPLATES_FOLDER_DEFAULT;
							else 
								folderName = OCConstants.DR_LEGACY_EDITOR_FOLDER;

							item.setLabel(folderName);
							item.getIndex();
							item.setParent(copywinId$folderId);
					
					if(copywinId$folderId.getItemCount() > 0) {
						List<Listitem> foldersList = copywinId$folderId.getItems();
						int index=0;
						copywinId$folderId.setSelectedIndex(index);
					}
				}
			

			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Exception ::" , e);;
			}

		}
		
		
		/*		public void getMyFoldersFromDb(String editoType){

			try {
				Components.removeAllChildren(copywinId$folderId);
				Components.removeAllChildren(MoveTemplatewinId$folderId);

				MyFoldersDao myFolderDao = (MyFoldersDao) SpringUtil.getBean("myFoldersDao");
				MyFoldersDaoForDML myFolderDaoForDML = (MyFoldersDaoForDML) SpringUtil.getBean("myFoldersDaoForDML");

			
				List<MyFolders> org_Folders = myFolderDao.findByOrgIdOrderByFolderNames(currentUser.getUserOrganization().getUserOrgId(),OCConstants.DnD_DIGITAL_RECEIPT_TEMPLATES);

				if(copywinId$folderId!=null){

					for(MyFolders myfolders :org_Folders) {
						logger.debug("------in if case----"+myfolders.getFolderName());
						String folderName = myfolders.getFolderName();						
						if(folderName!=null && !folderName.isEmpty() && (folderName.equals(OCConstants.DRAG_DROP_EDITOR_DR_TEMPLATES_FOLDER_DEFAULT)))
						{

							Listitem item = new Listitem();

							item.setValue(myfolders);
							item.setLabel(folderName);
							item.getIndex();
							item.setParent(copywinId$folderId);
						}
					}

					for(MyFolders myfolders:org_Folders) {
						logger.debug("------in if case----"+myfolders.getFolderName());
						String folderName = myfolders.getFolderName();						
						if(folderName!=null && !folderName.isEmpty() && (folderName.equals(OCConstants.DRAG_DROP_EDITOR_DR_TEMPLATES_FOLDER_DEFAULT)))
							continue;

						Listitem item = new Listitem();

						item.setValue(myfolders);
						item.setLabel(folderName);
						item.getIndex();
						item.setParent(copywinId$folderId);

					}				
					if(copywinId$folderId.getItemCount() > 0) {
						List<Listitem> foldersList = copywinId$folderId.getItems();
						int index=0;
						copywinId$folderId.setSelectedIndex(index);
					}
				}
				if(MoveTemplatewinId$folderId!=null){

					for(MyFolders myfolders :org_Folders) {
						logger.debug("------in if case----"+myfolders.getFolderName());
						String folderName = myfolders.getFolderName();						
						if(folderName!=null && !folderName.isEmpty() && (folderName.equals(OCConstants.DRAG_DROP_EDITOR_DR_TEMPLATES_FOLDER_DEFAULT)))
						{

							if((newEditorTemplatesListId.getSelectedItem().getLabel()).equals(myfolders.getFolderName())) continue;

							Listitem item = new Listitem();

							item.setValue(myfolders);
							item.setLabel(folderName);
							item.getIndex();
							item.setParent(MoveTemplatewinId$folderId);
						}
					}

					for(MyFolders myfolders:org_Folders) {
						logger.debug("------in if case----"+myfolders.getFolderName());
						String folderName = myfolders.getFolderName();						

						if((newEditorTemplatesListId.getSelectedItem().getLabel()).equals(myfolders.getFolderName())) continue;

						if(folderName!=null && !folderName.isEmpty() && (folderName.equals(OCConstants.DRAG_DROP_EDITOR_DR_TEMPLATES_FOLDER_DEFAULT)))
							continue;

						Listitem item = new Listitem();

						item.setValue(myfolders);
						item.setLabel(folderName);
						item.getIndex();
						item.setParent(MoveTemplatewinId$folderId);

					}				
					if(MoveTemplatewinId$folderId.getItemCount() > 0) {
						List<Listitem> foldersList = MoveTemplatewinId$folderId.getItems();
						int index=0;
						MoveTemplatewinId$folderId.setSelectedIndex(index);
					}

				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Exception ::" , e);;
			}

		}
*/
		public void getMyFolders(MyFolders myfoldersSelected){

			try {
				Components.removeAllChildren(copyTemplateswinId$folderId);

				MyFoldersDao myFolderDao = (MyFoldersDao) SpringUtil.getBean("myFoldersDao");
				MyFoldersDaoForDML myFolderDaoForDML = (MyFoldersDaoForDML) SpringUtil.getBean("myFoldersDaoForDML");


				List<MyFolders> org_Folders = myFolderDao.findByOrgIdOrderByFolderNames(currentUser.getUserOrganization().getUserOrgId(),OCConstants.DnD_DIGITAL_RECEIPT_TEMPLATES);

				for(MyFolders myfolders :org_Folders) {
					logger.debug("------in if case----"+myfolders.getFolderName());
					String folderName = myfolders.getFolderName();						
					if(folderName!=null && !folderName.isEmpty() && (folderName.equals(OCConstants.DRAG_DROP_EDITOR_DR_TEMPLATES_FOLDER_DEFAULT)))
					{
						if(myfoldersSelected.getFolderName().equals(myfolders.getFolderName())) continue;

						Listitem item = new Listitem();

						item.setValue(myfolders);
						item.setLabel(folderName);
						item.getIndex();
						item.setParent(copyTemplateswinId$folderId);
					}
				}

				for(MyFolders myfolders:org_Folders) {
					if(myfoldersSelected.getFolderName().equals(myfolders.getFolderName())) continue;
					String folderName = myfolders.getFolderName();						

					if(folderName!=null && !folderName.isEmpty() && (folderName.equals(OCConstants.DRAG_DROP_EDITOR_DR_TEMPLATES_FOLDER_DEFAULT)))
						continue;

					logger.debug("------in if case----"+myfolders.getFolderName());

					Listitem item = new Listitem();

					item.setValue(myfolders);
					item.setLabel(folderName);
					item.getIndex();
					item.setParent(copyTemplateswinId$folderId);
				}				
				if(copyTemplateswinId$folderId.getItemCount() > 0) {
					List<Listitem> foldersList = copyTemplateswinId$folderId.getItems();
					int index=0;
					copyTemplateswinId$folderId.setSelectedIndex(index);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Exception ::" , e);;
			}

		}

		public void getMyFolders(String folderName){

			try {

				Components.removeAllChildren(RenameMoveTemplatewinId$folderId);

				MyFoldersDao myFolderDao = (MyFoldersDao) SpringUtil.getBean("myFoldersDao");
				MyFoldersDaoForDML myFolderDaoForDML = (MyFoldersDaoForDML) SpringUtil.getBean("myFoldersDaoForDML");


				List<MyFolders> org_Folders = myFolderDao.findByOrgIdOrderByFolderNames(currentUser.getUserOrganization().getUserOrgId(),OCConstants.DnD_DIGITAL_RECEIPT_TEMPLATES);

				for(MyFolders myfolders :org_Folders) {
					logger.debug("------in if case----"+myfolders.getFolderName());
					String folderName1 = myfolders.getFolderName();						
					if(folderName1!=null && !folderName1.isEmpty() && (folderName1.equals(OCConstants.DRAG_DROP_EDITOR_DR_TEMPLATES_FOLDER_DEFAULT)))
					{
						logger.debug("------in if case----"+myfolders.getFolderName());
						String foldername = myfolders.getFolderName();						
						Listitem item = new Listitem();

						item.setValue(myfolders);
						item.setLabel(foldername);
						item.setParent(RenameMoveTemplatewinId$folderId);
					}
				}

				for(MyFolders myfolders:org_Folders) {
					logger.debug("------in if case----"+myfolders.getFolderName());
					String foldername1 = myfolders.getFolderName();						

					if(foldername1!=null && !foldername1.isEmpty() && (foldername1.equals(OCConstants.DRAG_DROP_EDITOR_DR_TEMPLATES_FOLDER_DEFAULT)))
						continue;

					Listitem item = new Listitem();

					item.setValue(myfolders);
				}				
				if(RenameMoveTemplatewinId$folderId.getItemCount() > 0) {
					RenameMoveTemplatewinId$folderId.setSelectedIndex(selectfolder-1);
					Listitem item1= RenameMoveTemplatewinId$folderId.getSelectedItem();
					item1.getLabel();
					item1.setLabel(item1.getLabel()+" (Current Folder)");
					RenameMoveTemplatewinId$folderId.setSelectedItem(item1);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Exception ::" , e);;
			}

		}

		private Menuitem changeFolderMId,deleteFolderMId,viewFolderMId;

/*		public void onClick$changeFolderMId(){
			Listitem selLi = (Listitem)((Listcell)((Image)manageFolderMpId.getAttribute("target")).getParent()).getParent();

			String folderName = (String)selLi.getLabel();
			logger.debug("folderName==="+folderName);

			MyFolders myfolders = (MyFolders)selLi.getValue();

			logger.debug("selLi "+myfolders.getFolderName());


			List<DigitalReceiptMyTemplate> myTemplatesLists = digiRecptMyTemplatesDao.findDRMyTemplatesOrgID(currentUser.getUserOrganization().getUserOrgId(), myfolders.getFolderName(), OCConstants.DRAG_AND_DROP_EDITOR);

			int myTemplatesListsSize= myTemplatesLists.size();
			boolean powerUserIsUsingTheTemplate = false;

			if(myTemplatesLists!=null & myTemplatesListsSize>0){

				for (Iterator iterator = myTemplatesLists.iterator(); iterator.hasNext();) {
					DigitalReceiptMyTemplate digitalReceiptMyTemplate = (DigitalReceiptMyTemplate) iterator.next();

					if(digitalReceiptMyTemplate.isInUse()){
						powerUserIsUsingTheTemplate = true;
						break;
					}

				}

				if(powerUserIsUsingTheTemplate){
					MessageUtil.setMessage("Rename action is not allowed as this folder is currently having used by another user.","green", "top");
					return;
				}

			}

			makeDisplayWindow(false, myfolders.getFolderName(), null);
		}
*/

		public void makeDisplayWindow(boolean isNew, String folderName, String parentDir) {

			if(isNew) {

				newMyTemplatesSubWinId.setTitle("Folder Creation");
				newMyTemplatesSubWinId$okBtnId.setLabel("Add Folder");

			}else {
				logger.info("Change Folder Name");
				newMyTemplatesSubWinId.setTitle("Modify Folder");
				newMyTemplatesSubWinId$okBtnId.setLabel("Change Folder Name");
				newMyTemplatesSubWinId$okBtnId.setAttribute("folderName", folderName);
			}
			newMyTemplatesSubWinId$newFolderTbId.setValue("");
			newMyTemplatesSubWinId$folderErrorMsgLblId.setValue(""); //clear the errormsg from newGallerySubWinId  if any
			//newMyTemplatesSubWinId$okBtnId.setAttribute("folderName", (String)myTemplatesListId.getSelectedItem().getAttribute("folderName"));
			newMyTemplatesSubWinId$okBtnId.setAttribute("parentDir",parentDir);

			newMyTemplatesSubWinId$createFolderDivId.setVisible(true);
			newMyTemplatesSubWinId.doHighlighted();
		}

		public void onClick$viewFolderMId(){

			Listitem selLi = (Listitem)((Listcell)((Image)manageFolderMpId.getAttribute("target")).getParent()).getParent();
			String folderName = (String)selLi.getLabel();
			logger.debug("folderName==="+folderName);
			MyFolders myfolders = (MyFolders)selLi.getValue();
			logger.debug("selLi "+myfolders.getFolderName());

			viewDetailsWinId$templateNameLblId.setValue(myfolders.getFolderName());
			String createdDateLbl = MyCalendar.calendarToString(myfolders.getCreatedDate(), MyCalendar.FORMAT_DATETIME_STYEAR, clientTimeZone);
			viewDetailsWinId$creationDateLblId.setValue(createdDateLbl);
			String modifedDateLbl;
			if(myfolders.getModifiedDate() != null){
				modifedDateLbl = MyCalendar.calendarToString(myfolders.getModifiedDate(),MyCalendar.FORMAT_DATETIME_STYEAR, clientTimeZone);
			}
			else{
				modifedDateLbl ="---";
			}
			viewDetailsWinId$lastAccLblId.setValue(modifedDateLbl);
			viewDetailsWinId.doHighlighted();

		}//onClick$viewDetailsMId()

/*		public void onClick$deleteFolderMId(){

			try {

				String userName = GetUser.getUserObj().getUserName();

				Listitem selLi = (Listitem)((Listcell)((Image)manageFolderMpId.getAttribute("target")).getParent()).getParent();
				MyFolders myfolders = (MyFolders)selLi.getValue();			

				List<DigitalReceiptMyTemplate> myTemplatesLists = digiRecptMyTemplatesDao.findDRMyTemplatesOrgID(currentUser.getUserOrganization().getUserOrgId(), myfolders.getFolderName(), OCConstants.DRAG_AND_DROP_EDITOR);

				int myTemplatesListsSize= myTemplatesLists.size();
				boolean powerUserIsUsingTheTemplate = false;

				if(myTemplatesLists!=null & myTemplatesListsSize>0){

					for (Iterator iterator = myTemplatesLists.iterator(); iterator.hasNext();) {
						DigitalReceiptMyTemplate digitalReceiptMyTemplate = (DigitalReceiptMyTemplate) iterator.next();

						if(digitalReceiptMyTemplate.isInUse()){
							powerUserIsUsingTheTemplate = true;
							break;
						}

					}

					if(powerUserIsUsingTheTemplate){
						MessageUtil.setMessage("Delete action is not allowed as this folder is currently having used by another user.","green", "top");
						return;
					}

				}

				if(myTemplatesLists!=null & myTemplatesLists.size()>0){
					int confirm = Messagebox.show("Do you want to delete the folder '" +myfolders.getFolderName() + "' permanently?", "Confirm", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION);

					if(confirm==Messagebox.YES){

						copyTemplateswinId.setVisible(true);
						copyTemplateswinId.setPosition("center");

						getMyFolders(myfolders);
						copyTemplateswinId.doHighlighted();
					}
				}else{

					int confirm = Messagebox.show("Do you want to delete the folder '" +myfolders.getFolderName() + "' permanently?", "Confirm", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION);

					if(confirm==Messagebox.YES){
						myFolderDaoForDML.delete(myfolders);
						MessageUtil.setMessage("'"+myfolders.getFolderName()+"' folder deleted \n successfully.","green", "top");
						copyTemplateswinId.setVisible(false);
					}
					getNewEditorTemplatesFromDb(currentUser.getUserId());

				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Exception ::" , e);;
			}
		}
*/
/*		public void onClick$myTemplatesSubmtBtnId$copyTemplateswinId(){

			try{

				Listitem selLi = (Listitem)((Listcell)((Image)manageFolderMpId.getAttribute("target")).getParent()).getParent();

				MyFolders myfolders = (MyFolders)selLi.getValue();
				String folderName = myfolders.getFolderName();

				MyFolders newMyfolder = (MyFolders)copyTemplateswinId$folderId.getSelectedItem().getValue();
				String folderToBeCopied= newMyfolder.getFolderName();

				String now = MyCalendar.calendarToString(Calendar.getInstance(), MyCalendar.FORMAT_DATETIME_STYEAR);
				if(folderToBeCopied!=null){

					int count = digiRecptMyTemplatesDaoForDML.setFolderName(currentUser.getUserId(), folderToBeCopied, folderName, Constants.EDITOR_TYPE_BEE, now);
					if(count!=0){
						MessageUtil.setMessage("Template(s) copied \n successfully to '"+folderToBeCopied+"'. folder","green", "top");
						myFolderDaoForDML.delete(myfolders);
						MessageUtil.setMessage("'"+myfolders.getFolderName()+"' folder deleted \n successfully.","green", "top");
					}
					else{
						Messagebox.show("Unable to copy the template(s) because template(s) with duplicate name already exist in '"+folderToBeCopied+"' folder.", "Error", Messagebox.OK, Messagebox.ERROR);
					}
				}else{
					MessageUtil.setMessage("Templates are not copied. ","green", "top");
				}
				copyTemplateswinId.setVisible(false);
				getNewEditorTemplatesFromDb(currentUser.getUserId());
			}catch (Exception e) {
				logger.error("** Exception :", e );
			}
		}
*/
		@Override
		public void render(Listitem li, Object arg1, int arg2) throws Exception {
			// TODO Auto-generated method stub
			String tempStr = (String) arg1;
			li.setLabel(tempStr);
			li.setValue(tempStr);

			logger.info("LI VAL :" + li + " tempStr=" + tempStr + " arg2 "+ arg2);
		}

		// config to sys temp
		public void onClick$sysTemplateBtnId() {
			try {
				String templateName = digitalTemplatesListId.getSelectedItem().getValue();
				templateName = SYSTEM_TEMPLATE + templateName;
				String val = digitalReceiptUserSettingsDao.findUserSelectedTemplate(currentUser.getUserId());
				if (val == null) {
					digitalReceiptUserSettingsDaoForDML.addDigiRecptUserSelectedTemplate(currentUser.getUserId(), templateName);
				} else {
					digitalReceiptUserSettingsDaoForDML.updateDigiUserSettings(currentUser.getUserId(),templateName);

				}

				String isValidPhStr = Utility.validatePh(digitalRecieptsCkEditorId.getValue(), currentUser);

				if(isValidPhStr != null && !isValidPhStr.equals("GEN_updatePreferenceLink")){

					MessageUtil.setMessage("Invalid Placeholder: "+isValidPhStr, "red");
					return ;
				}else if(isValidPhStr != null && isValidPhStr.equals("GEN_updatePreferenceLink")){

					MessageUtil.setMessage("You have currently disabled subscriber preference center setting.\n To continue, either enable this setting or remove \n 'Subscriber Preference Link' placeholder from your content.", "color:red");
					return;

				}

				String isValidCouponAndBarcode = null;
				isValidCouponAndBarcode = Utility.validateCCPh(digitalRecieptsCkEditorId.getValue(), currentUser);
				if(isValidCouponAndBarcode != null){
					return ;
				}

				selectedTemplateLblId.setValue(templateName);
				selectedTemplateLblId.setAttribute("templateName", templateName);
				MessageUtil.setMessage("This template has been configured\n successfully. All future e-Receipts\n will be sent with this template.",
						"green", "top");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Exception ::", e);
			}
		}

		// config to my temp
		public void onClick$myTemplateBtnId() {

			try {

				logger.info("digi List :"+ myTemplatesListId.getSelectedIndex());
				logger.info("digi List V :"+ myTemplatesListId.getSelectedItem().getValue());

				String templateName = myTemplatesListId.getSelectedItem().getValue();

				templateName = MY_TEMPLATE + templateName;
				if(enableSendingChkBxId.isChecked()) {/*

				//String SBS = dispSBSLBoxId.getSelectedItem().getValue().getSBS;
				//String storeNo = dispStoreLBoxId.getSelectedItem().getValue().getStore();
				OrganizationStores store=dispSBSLBoxId.getSelectedItem().getValue();
				String SBS=store.getSubsidiaryId();
				String storeNo=store.getHomeStoreId();
				Long orgUnitId = store.getDomainId();

				DigitalReceiptUserSettings retObj = digitalReceiptUserSettingsDao.findUserSelectedTemplate(currentUser.getUserId(), SBS, storeNo);
				if (retObj == null) {
					//digitalReceiptUserSettingsDao.addDigiRecptUserSelectedTemplate(currentUser.getUserId(), templateName);
					//digitalReceiptUserSettingsDaoForDML.addDigiRecptUserSelectedTemplate(currentUser.getUserId(), templateName, storeNo, SBS);
					retObj = new DigitalReceiptUserSettings();
					retObj.setUserId(currentUser.getUserId());
					retObj.setSelectedTemplateName(templateName);
					retObj.setStoreNo(storeNo);
					retObj.setSBSNo(SBS);
					digitalReceiptUserSettingsDaoForDML.saveOrUpdate(retObj);

				} else {
					//digitalReceiptUserSettingsDao.updateDigiUserSettings(currentUser.getUserId(), templateName);
					//digitalReceiptUserSettingsDaoForDML.updateDigiUserSettings(currentUser.getUserId(), templateName, storeNo, SBS);
					retObj.setSelectedTemplateName(templateName);
					retObj.setStoreNo(storeNo);
					retObj.setSBSNo(SBS);
					digitalReceiptUserSettingsDaoForDML.saveOrUpdate(retObj);

				}

				saveBtnId.setAttribute(STORE_WISE_DR_SETTINGS_OBJ, retObj) ;

				 */}//else{

				String val = digitalReceiptUserSettingsDao.findUserSelectedTemplate(currentUser.getUserId());
				if (val == null) {
					//digitalReceiptUserSettingsDao.addDigiRecptUserSelectedTemplate(currentUser.getUserId(), templateName);
					digitalReceiptUserSettingsDaoForDML.addDigiRecptUserSelectedTemplate(currentUser.getUserId(), templateName);
				} else {
					//digitalReceiptUserSettingsDao.updateDigiUserSettings(currentUser.getUserId(), templateName);
					digitalReceiptUserSettingsDaoForDML.updateDigiUserSettings(currentUser.getUserId(), templateName);
				}

				//}

				String isValidPhStr = Utility.validatePh(digitalRecieptsCkEditorId.getValue(), currentUser);

				if(isValidPhStr != null && !isValidPhStr.equals("GEN_updatePreferenceLink")){

					MessageUtil.setMessage("Invalid Placeholder: "+isValidPhStr, "red");
					return ;
				}else if(isValidPhStr != null && isValidPhStr.equals("GEN_updatePreferenceLink")){

					MessageUtil.setMessage("You have currently disabled subscriber preference center setting.\n To continue, either enable this setting or remove \n 'Subscriber Preference Link' placeholder from your content.", "color:red");
					return;

				}

				String isValidCouponAndBarcode = null;
				isValidCouponAndBarcode = Utility.validateCCPh(digitalRecieptsCkEditorId.getValue(), currentUser);
				if(isValidCouponAndBarcode != null){
					return ;
				}
				// Bar code validation
				/*String isValidCCDim = null;
			isValidCCDim = Utility.validateCouponDimensions(editorContent);
			if(isValidCCDim != null){
				return ;
			}*/

				selectedTemplateLblId.setValue(templateName);
				selectedTemplateLblId.setAttribute("templateName", templateName);
				MessageUtil.setMessage("This template has been configured\n successfully. All future e-Receipts\n will be sent with this template.",
						"green", "top");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Exception ::", e);
			}
		}

		public void onClick$configTemplateBtnId() {

			if (tabBoxId.getSelectedIndex() == 0) {

				onClick$myTemplateBtnId();
			} else if (tabBoxId.getSelectedIndex() == 1) {

				onClick$sysTemplateBtnId();

			}
		}

		private Toolbarbutton updateTemplateAId;
		private Toolbarbutton submitBtnId;

		public void onClick$updateTemplateAId() {

			if (tabBoxId.getSelectedIndex() == 0) {

				DigitalReceiptMyTemplate digitalReceiptMyTemplate;
				digitalReceiptMyTemplate = (DigitalReceiptMyTemplate) myTemplatesListId.getSelectedItem().getAttribute("myTemplateObj");

				if (digitalReceiptMyTemplate != null) {

					//	DRSentDao drSentDao = (DRSentDao)SpringUtil.getBean("drSentDao");
					//int configCount = drSentDao.findConfiguredTemplateCount(currentUser.getUserOrganization().getUserOrgId(),digitalReceiptMyTemplate.getName() );

					int configCount = drSentDao.findConfiguredTemplateCount(currentUser.getUserOrganization().getUserOrgId(),digitalReceiptMyTemplate.getMyTemplateId() );
					
					String editorContent = digitalRecieptsCkEditorId.getValue();

					String isValidPhStr = Utility.validatePh(editorContent, currentUser);

					if(isValidPhStr != null && !isValidPhStr.equals("GEN_updatePreferenceLink")){

						MessageUtil.setMessage("Invalid Placeholder: "+isValidPhStr, "red");
						return ;
					}else if(isValidPhStr != null && isValidPhStr.equals("GEN_updatePreferenceLink")){

						MessageUtil.setMessage("You have currently disabled subscriber preference center setting.\n To continue, either enable this setting or remove \n 'Subscriber Preference Link' placeholder from your content.", "color:red");
						return;

					}

					String isValidCouponAndBarcode = null;
					isValidCouponAndBarcode = Utility.validateCCPh(editorContent, currentUser);
					if(isValidCouponAndBarcode != null){
						return ;
					}


					if(configCount > 0) {

						int confirm = Messagebox.show("Changes in the template will be reflected in all digital " +
								"receipts sent from now. \n Do you want to continue?", "Update Template",
								Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);

						if(confirm != 1) return;

					}

					editorContent = editorContent.replace("<!--##BEGIN ITEMS##-->","##BEGIN ITEMS##")
							.replace("<!--##END ITEMS##-->","##END ITEMS##");
					editorContent = editorContent.replace("<!--##BEGIN PAYMENTS##-->", "##BEGIN PAYMENTS##")
							.replace("<!--##END PAYMENTS##-->", "##END PAYMENTS##");

					digitalReceiptMyTemplate.setContent(editorContent);
					digitalReceiptMyTemplate.setModifiedDate(Calendar.getInstance());
					digitalReceiptMyTemplate.setModifiedby(currentUser.getUserId());
					digiRecptMyTemplatesDaoForDML.saveOrUpdate(digitalReceiptMyTemplate);
					MessageUtil.setMessage("Template modified successfully.","green", "top");
				} else {
					MessageUtil.setMessage("Failed to update template.", "red","top");
				}

				Redirect.goTo(PageListEnum.EMPTY);
				Redirect.goTo(PageListEnum.CAMPAIGN_DIGITAL_RECEIPTS);
			}

		}

		public void defaultDigiSettings(){

			List<UserFromEmailId> userFromEmailIdList = userFromEmailIdDao.getEmailsByUserId(currentUser.getUserId());
			logger.info("List size : " + userFromEmailIdList.size());

			Components.removeAllChildren(cFromEmailCb);
			Components.removeAllChildren(cRepEmailCb);

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

			}
			if (currentUser != null && currentUser.getEmailId() != null) {
				cRepEmailCb.appendItem(currentUser.getEmailId());
			}

			if (listOfEmailIds.size() > 0) {
				for (Object obj : listOfEmailIds) {
					String anEmailId = (String) obj;
					cFromEmailCb.appendItem(anEmailId);

				}
			}
			if (listOfEmailIds.size() > 0) {
				for (Object obj : listOfEmailIds) {
					String anEmailId = (String) obj;
					cRepEmailCb.appendItem(anEmailId);

				}
			}

			if (!(cFromEmailCb.getItemCount() > 0)) {
				cFromEmailCb.appendItem("No emails registered.");

			}
			cFromEmailCb.setSelectedIndex(0);


			cFromNameTb.setValue(currentUser.getCompanyName());

			if(!(cRepEmailCb.getItemCount() > 0))
			{
				cRepEmailCb.appendItem("No emails registered.");
			}
			cRepEmailCb.setSelectedIndex(0);

		}

/*		public void defaultDigiSettings(DigitalReceiptUserSettings digitalReceiptUserSettings) {

			logger.debug("====entered defaultDigiSettings===="+digitalReceiptUserSettings);
			if(digitalReceiptUserSettings == null) {

				return;
			}

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
			for (int index = 0; index < cFromEmailCb.getItemCount(); index++) {

				if (cFromEmailCb.getItemAtIndex(index).getLabel().equals(fromEmailId)) {
					cFromEmailCb.setSelectedIndex(index);
				}
			}
			String replayEmailId = digitalReceiptUserSettings.getReplytoemail();
			for (int index = 0; index < cRepEmailCb.getItemCount(); index++) {


				if (cRepEmailCb.getItemAtIndex(index).getLabel().equals(replayEmailId)) {
					cRepEmailCb.setSelectedIndex(index);
				}
			}

			enableSendingChkBxId.setChecked(currentUser.isEnableDRSending());
			shippingAmntChkBxId.setChecked(digitalReceiptUserSettings.isIncludeShipping());
			feeAmntChkBxId.setChecked(digitalReceiptUserSettings.isIncludeFee());
			taxAmntChkBxId.setChecked(digitalReceiptUserSettings.isIncludeTax());
			discAmntChkBxId.setChecked(digitalReceiptUserSettings.isIncludeGlobalDiscount());

			frmEmaildynamicOrNotRgId.setSelectedIndex(digitalReceiptUserSettings.isIncludeDynamicFrmEmail() ? 1 : 0);

			frmNamedynamicOrNotRgId.setSelectedIndex(digitalReceiptUserSettings.isIncludeDynamicFrmName() ? 1 : 0);

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
*/
		public void onClick$regFrmEmlAnchId() {

			regEmailPopupId.setAttribute("flagStr", "From-Email");
		}
		public void onClick$regRepEmlAnchId() {

			regRepEmailPopupId.setAttribute("flagStr", "Reply-Email");
		}

		public void onClick$manageStoresLink1AnchId() {
			String redirectToStr = PageListEnum.USERADMIN_ORG_STORES.getPagePath();

			PageUtil.setFromPage(PageListEnum.CAMPAIGN_DIGITAL_RECEIPTS.getPagePath());
			Redirect.goTo(redirectToStr);
		}

		public void onClick$manageStoresLink2AnchId() {
			onClick$manageStoresLink1AnchId();
		}

		Popup help, help1;

		public void onFocus$cSubTb() {

			// cSubTb.setPopup(help);
			help.open(cSubTb, "end_after");

		}

		public void onClick$cancelBtnId() {

			regEmailPopupId.close();

		}

		public void onClick$cancel1BtnId() {

			regRepEmailPopupId.close();

		}
/*		public Long findPowerUser(Long domainId) {
			List<Long> userIdsList = domainDao.getUsersByDomainId(domainId);
			SecRolesDao secRolesDao = (SecRolesDao)SpringUtil.getBean("secRolesDao");
			for (Long userId : userIdsList) {

				List<SecRoles> rolesList = secRolesDao.findByUserId(userId);

				String role = "";

				for (SecRoles secRole : rolesList) {

					if(secRole.getType().equalsIgnoreCase("DRPower")) return userId;
				}

			}

			return null;
		}
*/
/*		public void onClick$saveBtnId() {
			try {
				//myTemplatesListId,
				if(dispSBSLBoxId.getSelectedIndex() == 0 ) {

					MessageUtil.setMessage("Please Select Subsidiary. ", "color:red");
					return;
				}
				if(mytemplatesLbId.getSelectedIndex()== 0) {

					MessageUtil.setMessage("Please Select a template. ", "color:red");
					return;
				}

				DigitalReceiptUserSettings digitalReceiptUserSettings  = null;

				Long userId = null;
				if(saveBtnId.getAttribute(STORE_WISE_DR_SETTINGS_OBJ) != null) {
					digitalReceiptUserSettings = (DigitalReceiptUserSettings)saveBtnId.getAttribute(STORE_WISE_DR_SETTINGS_OBJ);
					digitalReceiptUserSettings.setSelectedTemplateName(MY_TEMPLATE+mytemplatesLbId.getSelectedItem().getLabel());
					userId= digitalReceiptUserSettings.getUserId();
					logger.debug("=====edit========"+digitalReceiptUserSettings);
				}else{
					logger.debug("=====new========");

					userId = findPowerUser(((UsersDomains)orgUnitlbId.getSelectedItem().getValue()).getDomainId());
					if(userId == null) {

						MessageUtil.setMessage("No Power User exists in Selected Org.Unit ", "color:red");
						return;
					}
					digitalReceiptUserSettings = new DigitalReceiptUserSettings();

					digitalReceiptUserSettings.setUserId(userId);
					digitalReceiptUserSettings.setOrgId(currentUser.getUserOrganization().getUserOrgId());
					digitalReceiptUserSettings.setDomainId(((UsersDomains)orgUnitlbId.getSelectedItem().getValue()).getDomainId());
					digitalReceiptUserSettings.setSelectedTemplateName(MY_TEMPLATE+mytemplatesLbId.getSelectedItem().getLabel());
					//retObj.setStoreNo(storeNo);
					digitalReceiptUserSettings.setSBSNo(((OrganizationStores)dispSBSLBoxId.getSelectedItem().getValue()).getSubsidiaryId());

				}

				usersDaoForDML.updateEnableDrSending(enableSendingChkBxId.isChecked(), currentUser.getUserOrganization().getUserOrgId());
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

					if(frmNamedynamicOrNotRgId.getSelectedIndex()==0 )
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
					if(frmEmaildynamicOrNotRgId.getSelectedIndex() == 0)
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

				digitalReceiptUserSettings.setIncludeShipping(shippingAmntChkBxId.isChecked());
				digitalReceiptUserSettings.setIncludeFee(feeAmntChkBxId.isChecked());
				digitalReceiptUserSettings.setIncludeTax(taxAmntChkBxId.isChecked());
				digitalReceiptUserSettings.setIncludeGlobalDiscount(discAmntChkBxId.isChecked());

				int selIndxEml = frmEmaildynamicOrNotRgId.getSelectedIndex();
				int selIndxName = frmNamedynamicOrNotRgId.getSelectedIndex();

				digitalReceiptUserSettings.setIncludeDynamicFrmEmail(selIndxEml == 0 ? false : true);
				digitalReceiptUserSettings.setIncludeDynamicFrmName(selIndxName == 0 ? false : true );

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

				if(cPermRemRb.getSelectedIndex() == 0){
					if(!permRemTextId.getValue().trim().equals("")){
						digitalReceiptUserSettings.setPermissionRemainderFlag(true);
						digitalReceiptUserSettings.setPermissionRemainderText(permRemTextId.getValue());
					}
				}else{
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

				digitalReceiptUserSettings.setSubject(cSubTb.getValue().trim());

				if(frmNamedynamicOrNotRgId.getSelectedIndex()==0)
					digitalReceiptUserSettings.setFromName(cFromNameTb.getValue().trim());


				if(frmEmaildynamicOrNotRgId.getSelectedIndex()==0)
					digitalReceiptUserSettings.setFromEmail(cFromEmailCb.getValue().trim());

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
							digitalReceiptUserSettingsDaoForDML.saveOrUpdate(digitalReceiptUserSettings);
							Redirect.goTo(PageListEnum.EMPTY);
							Redirect.goTo(PageListEnum.CAMPAIGN_DIGITAL_RECEIPTS);
						}catch(Exception e){

							logger.error("Exception ::", e);
						}
						MessageUtil.setMessage("Digital receipt settings saved successfully.","color:green;");

					}

				}catch(Exception e1){
					logger.error("Exception ::", e1);
				}
			} catch (Exception e) {

				logger.error("Exception ::", e);
			}
		}
*/
		public void onClick$submitBt1nId() {
			try {

				if(frmEmaildynamicOrNotRgId.getSelectedIndex() == 0){
					registerNewFromEmail(cFromEmailTb,(String)regEmailPopupId.getAttribute("flagStr"));
				}

			} catch (Exception e) {

				logger.error("Exception ::", e);
			}
		}

		public void onClick$submitBt2nId() {
			try {

				registerNewFromEmail(cRepEmailTb,(String)regRepEmailPopupId.getAttribute("flagStr"));

			} catch (Exception e) {

				logger.error("Exception ::", e);
			}
		}

		public void registerNewFromEmail(Textbox cFromEmailTb,String flagStr) throws Exception {
			String newFromEmail = cFromEmailTb.getValue();

			if(newFromEmail.trim().equals("")){
				regEmailPopupId.close();
				MessageUtil.setMessage("Email field cannot be left empty.", "color:red", "TOP");
				return;
			}

			if(!Utility.validateEmail(newFromEmail.trim())) {
				regEmailPopupId.close();
				MessageUtil.setMessage("Please enter a valid email address.", "color:red", "TOP");
				cFromEmailTb.setValue("");
				return;
			}

			try {
				if(newFromEmail.equalsIgnoreCase(currentUser.getEmailId())) {
					MessageUtil.setMessage("Given email address already exists.", "color:red", "TOP");
					cFromEmailTb.setValue("");
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
				cFromEmailTb.setValue("");*/
				} else if(userFromEmailId.getStatus() == 0) {

					//MessageUtil.setMessage("Given email ID approval is pending.", "color:red", "TOP");
					try {
						int confirm = Messagebox.show("The given email address is pending for approval . Do you want to resend the verification?","Send Verification ?",
								Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
						if(confirm != 1) {
							regEmailPopupId.close();
							cFromEmailTb.setValue("");
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
				cFromEmailTb.setValue("");
					 */
				} 
				else {
					regEmailPopupId.close();
					MessageUtil.setMessage("Given email address already exists.", "color:red", "TOP");
					cFromEmailTb.setValue("");
				}
			} catch (Exception e) {
				regEmailPopupId.close();
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
				MessageUtil.clearMessage();
				Messagebox.show("We have sent you a confirmation email to "+newFromEmail+".\n " +
						"Follow the instructions in the email and this new "+flagStr+" will be verified.", "Register email", Messagebox.OK, Messagebox.INFORMATION);
				cFromEmailTb.setValue("");
			} catch (WrongValueException e) {
				logger.error("Exception ::", e);
			}catch (Exception e) {

				//	logger.error("** Exception while sending verification",e);

			}

		}

		// DR reports moved to reports menu

		/*private Include viewReportsIncId;
	public void onClick$digiRcptReportsTabId() {
//		session.setAttribute("viewType", "contact");
	viewReportsIncId.setSrc(null);
	viewReportsIncId.setSrc("/zul/campaign/drReport.zul");
	//Redirect.goTo(PageListEnum.CAMPAIGN_DIGITAL_RECEIPTS_REPORTS);

	}
		 */

		/*public void onClick$deleteTemplateMId(){

			if (tabBoxId.getSelectedIndex() == 0) {

				DigitalReceiptMyTemplate digitalReceiptMyTemplate;
				digitalReceiptMyTemplate = (DigitalReceiptMyTemplate) myTemplatesListId.getSelectedItem().getAttribute("myTemplateObj");

				if (digitalReceiptMyTemplate != null) {

					int confirm = Messagebox.show("Do you want to delete the template '" + myTemplatesListId.getSelectedItem().getValue() + "' permanently?", "Confirm", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
					if(confirm != 1) return;

					//digiRecptMyTemplatesDao.delete(digitalReceiptMyTemplate);
					digiRecptMyTemplatesDaoForDML.delete(digitalReceiptMyTemplate);
					selectedTemplateLblId.setValue("No template configured");
					MessageUtil.setMessage("Template deleted successfully.","green", "top");	
					if(currentUser.isEnableStoreWiseTemplates()) {

						setMyTemplates();
					}
					tabBoxId.setSelectedIndex(0);
					//	onClick$tabBoxId();

				}

				else {
					MessageUtil.setMessage("Failed to delete the template.", "red","top");
				}

			}
		}//onClick$deleteTemplateMId()
*/
		public void onClick$saveAsMId(){
			onSave=false;
			makeACopyWinId.setTitle("Save As");
			onSave=true;
			onClick$makeCopyMId();

		}
		public void onClick$makeCopyMId(){
			if(onSave == false){
				makeACopyWinId.setTitle("Make A Copy");
			}
			String ckEditorContent = digitalRecieptsCkEditorId.getValue();
			if (ckEditorContent == null || ckEditorContent.length() < 1) {
				MessageUtil.setMessage("Please provide content to save.", "red","top");
				onSave=false;
				return;
			}

			String isValidPhStr = Utility.validatePh(ckEditorContent, currentUser);

			if(isValidPhStr != null && !isValidPhStr.equals("GEN_updatePreferenceLink")){

				MessageUtil.setMessage("Invalid Placeholder: "+isValidPhStr, "red");
				onSave=false;
				return ;
			}else if(isValidPhStr != null && isValidPhStr.equals("GEN_updatePreferenceLink")){

				MessageUtil.setMessage("You have currently disabled subscriber preference center setting.\n To continue, either enable this setting or remove \n 'Subscriber Preference Link' placeholder from your content.", "color:red");
				onSave=false;
				return;

			}

			String isValidCouponAndBarcode = null;
			isValidCouponAndBarcode = Utility.validateCCPh(ckEditorContent, currentUser);
			if(isValidCouponAndBarcode != null){
				onSave=false;
				return ;
			}
			// Bar code validation
			/*String isValidCCDim = null;
			isValidCCDim = Utility.validateCouponDimensions(editorContent);
			if(isValidCCDim != null){
				return ;
			}*/

			// winId.setVisible(true);
			makeACopyWinId$templatNameTbId.setValue("");
			makeACopyWinId.setVisible(true);
			makeACopyWinId.setPosition("center");
			makeACopyWinId.doHighlighted();

		}//onClick$makeCopyMId()
		public void onClick$sendTestReceiptMId(){
			onClick$sendTestBtnId();
		}//onClick$sendTestReceiptMId()

		public void onClick$saveAsMyChoiceMId(){
			onClick$configTemplateBtnId();
		}//onClick$saveAsMyChoiceMId()

/*		public void onClick$saveInMyTempBtnId$makeACopyWinId() {

			makeACopyWinId.setVisible(true);
			if (makeACopyWinId$templatNameTbId.getValue().trim().equals("")) {
				MessageUtil.setMessage("Template name cannot be left empty.", "red","top");
				onSave=false;
				return;
			}

			DigitalReceiptMyTemplate digitalReceiptMyTemplate = digiRecptMyTemplatesDao.findByUserNameAndTemplateName(currentUser.getUserOrganization().getUserOrgId(),makeACopyWinId$templatNameTbId.getValue());

			if (digitalReceiptMyTemplate != null) {

				MessageUtil.setMessage("Template name already exists, please choose another name.","red", "top");
				onSave=false;
				return;
			}

			logger.info("saving the object");

			// Replace PH comments
			String editorContent = digitalRecieptsCkEditorId.getValue();

			String isValidPhStr = Utility.validatePh(editorContent, currentUser);

			if(isValidPhStr != null && !isValidPhStr.equals("GEN_updatePreferenceLink")){

				MessageUtil.setMessage("Invalid Placeholder: "+isValidPhStr, "red");
				onSave=false;
				return ;
			}else if(isValidPhStr != null && isValidPhStr.equals("GEN_updatePreferenceLink")){

				MessageUtil.setMessage("You have currently disabled subscriber preference center setting.\n To continue, either enable this setting or remove \n 'Subscriber Preference Link' placeholder from your content.", "color:red");
				onSave=false;
				return;

			}

			String isValidCouponAndBarcode = null;
			isValidCouponAndBarcode = Utility.validateCCPh(digitalRecieptsCkEditorId.getValue(), currentUser);
			if(isValidCouponAndBarcode != null){
				onSave=false;
				return ;
			}
			// Bar code validation
			String isValidCCDim = null;
		isValidCCDim = Utility.validateCouponDimensions(editorContent);
		if(isValidCCDim != null){
			return ;
		}

			editorContent = editorContent.replace("<!--##BEGIN ITEMS##-->","##BEGIN ITEMS##")
					.replace("<!--##END ITEMS##-->","##END ITEMS##");
			editorContent = editorContent.replace("<!--##BEGIN PAYMENTS##-->","##BEGIN PAYMENTS##")
					.replace("<!--##END PAYMENTS##-->","##END PAYMENTS##");

			digitalReceiptMyTemplate = new DigitalReceiptMyTemplate(makeACopyWinId$templatNameTbId.getValue(), editorContent,Calendar.getInstance(), currentUser.getUserId(), currentUser.getUserOrganization().getUserOrgId());

			makeACopyWinId.setVisible(false);
			digitalReceiptMyTemplate.setModifiedDate(Calendar.getInstance());
			//digiRecptMyTemplatesDao.saveOrUpdate(digitalReceiptMyTemplate);
			digiRecptMyTemplatesDaoForDML.saveOrUpdate(digitalReceiptMyTemplate);
			MessageUtil.setMessage("Template saved successfully.", "green", "top");
			onSave=false;
			if(currentUser.isEnableStoreWiseTemplates()) {

				setMyTemplates();
			}
			tabBoxId.setSelectedIndex(0);
			//onClick$tabBoxId();
		}
*/
		public void onClick$renameTemplateMId(){
			//renameTemplateWinId$newTempltNameTbId.setValue("");
			String selectedTemplateName;
			selectedTemplateName = myTemplatesListId.getSelectedItem().getValue().toString();

			renameTemplateWinId$newTempltNameTbId.setValue(selectedTemplateName);
			renameTemplateWinId.setVisible(true);
			renameTemplateWinId.setPosition("center");
			renameTemplateWinId.doHighlighted();
		}//onClick$renameTemplateMId()

		public void onClick$cancelTempltBtnId$renameTemplateWinId(){
			renameTemplateWinId.setVisible(false);
		}//onClick$renameTemplateMId()

/*		public void onClick$renameTempltBtnId$renameTemplateWinId(){

			String currentConfiguredTemplate = digitalReceiptUserSettingsDao.findUserSelectedTemplate(currentUser.getUserId());
			if(currentConfiguredTemplate != null){
				currentConfiguredTemplate = currentConfiguredTemplate.substring(MY_TEMPLATE.length());
			}
			//String currentConfiguredTemplate = selectedTemplateLblId.getValue().substring(12);
			//because from 12 onwards actual name starts, as length of MY_TEMPLATE: is 12. 
			String selectedTemplateName = myTemplatesListId.getSelectedItem().getValue().toString();

			if(renameTemplateWinId$newTempltNameTbId.getValue().trim().isEmpty()){
				MessageUtil.setMessage("New template name can't be empty, please provide a name.","red", "top");
				renameTemplateWinId$newTempltNameTbId.setFocus(true);
				return;
			}
			DigitalReceiptMyTemplate digitalReceiptMyTemplate = digiRecptMyTemplatesDao.findByUserNameAndTemplateName(currentUser.getUserOrganization().getUserOrgId(),renameTemplateWinId$newTempltNameTbId.getValue().trim());
			if (digitalReceiptMyTemplate != null) {

				MessageUtil.setMessage("Template name already exists, please choose another name.","red", "top");
				return;
			}

			logger.info("-------------------->myTemplatesListId.getSelectedItem().toString().trim() = "+myTemplatesListId.getSelectedItem().getValue().toString().trim());

			try {
				String tempName="MY_TEMPLATE:"+myTemplatesListId.getSelectedItem().getValue().toString();
				Long userId=currentUser.getUserId();
				Long orgId = currentUser.getUserOrganization().getUserOrgId();
				String now = MyCalendar.calendarToString(Calendar.getInstance(), MyCalendar.FORMAT_DATETIME_STYEAR);
				//digiRecptMyTemplatesDao.setNewTemplateName(userId, renameTemplateWinId$newTempltNameTbId.getText().trim(), myTemplatesListId.getSelectedItem().getValue().toString(), now);
				digiRecptMyTemplatesDaoForDML.setNewTemplateName(orgId, renameTemplateWinId$newTempltNameTbId.getText().trim(), myTemplatesListId.getSelectedItem().getValue().toString(), now);
				//digitalReceiptUserSettingsDao.setNewTemplateName(userId, renameTemplateWinId$newTempltNameTbId.getText().trim(),tempName);
				digitalReceiptUserSettingsDaoForDML.setNewTemplateName(orgId, renameTemplateWinId$newTempltNameTbId.getText().trim(),tempName);

				//drSentDao.setNewTemplateName(userId, renameTemplateWinId$newTempltNameTbId.getText().trim(),tempName);
				drSentDaoForDML.setNewTemplateName(orgId, renameTemplateWinId$newTempltNameTbId.getText().trim(),tempName);

				renameTemplateWinId.setVisible(false);
				if(selectedTemplateName.equals(currentConfiguredTemplate))
					selectedTemplateLblId.setValue(renameTemplateWinId$newTempltNameTbId.getText().trim());

				MessageUtil.setMessage("Template name changed.","", "top");
			} catch (WrongValueException e) {
				// TODO Auto-generated catch block
				logger.error("Exception ", e);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Exception ", e);
			}

			if(currentUser.isEnableStoreWiseTemplates()) {

				setMyTemplates();
			}
			tabBoxId.setSelectedIndex(0);
			//onClick$tabBoxId();
		}//onClick$renameTempltBtnId$renameTemplateWinId()
*/
	}
