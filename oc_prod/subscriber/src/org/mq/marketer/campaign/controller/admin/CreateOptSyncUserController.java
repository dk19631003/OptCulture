package org.mq.marketer.campaign.controller.admin;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.UpdateOptSyncData;
import org.mq.marketer.campaign.beans.UserOrganization;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.dao.UpdateOptSyncDataDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.LBFilterEventListener;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PageListEnum;
import org.mq.marketer.campaign.general.PageUtil;
import org.mq.marketer.campaign.general.Redirect;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.model.opySync.OptSyncService;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.zkoss.zhtml.Div;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.A;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Window;
import org.zkoss.zul.event.PagingEvent;



/**
 * This object help's to Create OptSync PlugInId & OptSyncKey for any User Organization as it is controlled by OC Administrator.
 * It also help's to Activate or Pause Email Alert's and Display,Modify and Delete plugIn details.
 * @author vinod.bokare
 *
 */
public class CreateOptSyncUserController extends GenericForwardComposer implements EventListener,ListitemRenderer<UpdateOptSyncData> {
	/**
	 * Default serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager	.getLogger(Constants.SUBSCRIBER_LOGGER);
	private Textbox optSynAuthKeyTbId, optSyncPluginTbId, pluginTbId,
	emailIdTbId,emailAddress2TbId,emailAddress3TbId,emailAddress4TbId,emailAddress5TbId,optSyncPluginTbIdView,pluginTbIdView,emailIdTbIdView,emailAddress2TbIdView ,emailAddress3TbIdView,emailAddress4TbIdView,emailAddress5TbIdView;;
	private Toolbarbutton addOptSyncPluginTBId,addOneMoreEmailTBId1,addOneMoreEmailTBId2,addOneMoreEmailTBId3,addOneMoreEmailTBId4,deleteEmailTBId2,deleteEmailTBId3,deleteEmailTBId4,deleteEmailTBId5,deleteEmailTBId1;
	private Groupbox newOptSyncPluginGbId,newOptSyncPluginGbIdView;
	private Label emailAddress2Label,emailAddress3Label,emailAddress4Label,emailAddress5Label;
	private Button saveBtnId,resetBtnId,cancelBtnId;  
	private boolean isemailAddress2textbox=false,isemailAddress3textbox=false,isemailAddress4textbox=false,isemailAddress5textbox=false;
	private Listbox orgListBxId,usersListBxId ;
	private Checkbox enableOptSyncChkbxId;
	private Label emailAddress2LabelView,emailAddress3LabelView,emailAddress4LabelView,emailAddress5LabelView;
	private A generateKeyAnchId,generateAnchId ;
	private String type;
	private UpdateOptSyncData sessionUpdateOptSyncData;

	private Listbox optSyncLBId,itemsPerPageLBId,itemsPerPageLBId1;
	private Paging optSyncPagingId;
	private LBFilterEventListener   viewUDateOptSynObj;
	private static final String allStr = "--All--";
	private static final String selectStr="--Select User--";
	private boolean isViewDetails = false;
	
	// Added after 2.3.9
	private Window createWinId$opySyncAlertsWinId , opySyncAlertsWinId;
	private Radio opySyncAlertsWinId$automaticRgId, opySyncAlertsWinId$manualRgId;
	private A resetOrgListAnchId,applySettingsAnchId ; 
	private UpdateOptSyncData updateOptSyncDataView;
	private Label userNameStarLbId,userNameLbId,optSyncAuthStarLbId,optSyncAuthLbId,enableOptFlagLbId;
	//private Div userNameDivId;
	//,resetUsersListAnchId ;
	/**
	 * Default Constructor
	 */
	public CreateOptSyncUserController() {
		logger.debug(">>>>>>>>>>>>> entered in CreateOptSyncUserController Constructor");
		String style = "font-weight:bold;font-size:15px;color:#313031;font-family:Arial,Helvetica,sans-serif;align:left";
		PageUtil.setHeader("OptSync Settings", Constants.STRING_NILL, style, true);
		logger.debug("<<<<<<<<<<<<< completed CreateOptSyncUserController Constructor ");
	}//CreateOptSyncUserController

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		logger.debug(">>>>>>>>>>>>> entered in doAfterCompose");
		super.doAfterCompose(comp);
		type = (String)session.removeAttribute("UpdateOptSyncDataType");
		sessionUpdateOptSyncData =(UpdateOptSyncData)session.removeAttribute("UpdateOptSyncData");
		resetOrgListAnchId.setVisible(false);
		setDefaultSettings();
		if(orgListBxId != null)
			setUserOrganization(orgListBxId);
		logger.debug("<<<<<<<<<<<<< completed doAfterCompose ");
	}//doAfterCompose

	/**
	 * This method populates all the optSync setting's 
	 * @param userId
	 */

	private void setDefaultSettings(){
		logger.debug(">>>>>>>>>>>>> entered in setDefaultSettings");
		String filterQuery =Constants.STRING_NILL;
		String filterCountQuery=Constants.STRING_NILL; 
		addOptSyncPluginTBId.setVisible(true);

		try {
			Map<Integer, Field> objMap = new HashMap<Integer, Field>();
			objMap.put(0, UpdateOptSyncData.class.getDeclaredField("optSyncName"));
			objMap.put(1, UpdateOptSyncData.class.getDeclaredField("optSyncId"));
			objMap.put(2, UpdateOptSyncData.class.getDeclaredField("emailId"));
			objMap.put(3, UpdateOptSyncData.class.getDeclaredField("status"));
			objMap.put(4, UpdateOptSyncData.class.getDeclaredField("optSyncHitTime"));
			objMap.put(5, UpdateOptSyncData.class.getDeclaredField("enabledOptSyncFlag"));

			if(optSyncLBId != null)	optSyncLBId.setItemRenderer(this);

			optSyncPagingId.setPageSize(Integer.parseInt(itemsPerPageLBId.getSelectedItem().getLabel()));
			optSyncPagingId.setAttribute(LBFilterEventListener.HANDLE_ON_PAGING, "false");
			filterQuery = " FROM UpdateOptSyncData where 1=1";
			filterCountQuery = "SELECT COUNT(*) FROM UpdateOptSyncData where 1=1";
			viewUDateOptSynObj = LBFilterEventListener.lbFilterSetup(optSyncLBId, optSyncPagingId, filterQuery, filterCountQuery, Constants.STRING_NILL, objMap);

			Utility.refreshModel(viewUDateOptSynObj, 0, true);
		} catch (WrongValueException e) {

			logger.error("Exception ::",e);
		} catch (NoSuchFieldException e) {
			logger.error("Exception ::",e);
		} catch (SecurityException e) {

			logger.error("Exception ::",e);
		}
		logger.debug("<<<<<<<<<<<<< completed setDefaultSettings ");
	}//setDefaultSettings


	/**
	 * This method handles the paging event.
	 * @param event
	 */
	public void onPaging$optSyncPagingId(ForwardEvent event) {
		logger.debug(">>>>>>>>>>>>> entered in onPaging$optSyncPagingId");
		final PagingEvent pe = (PagingEvent) event.getOrigin();
		int _startPageNumber = pe.getActivePage();
		Utility.refreshModel(viewUDateOptSynObj,  _startPageNumber, false);
		logger.debug("<<<<<<<<<<<<< completed onPaging$optSyncPagingId ");
	}//onPaging$optSyncPagingId

	/**
	 * This method handles the items per page even
	 */
	public void onSelect$itemsPerPageLBId(){
		logger.debug(">>>>>>>>>>>>> entered in onSelect$itemsPerPageLBId");
		setDefaultSettings();
		logger.debug("<<<<<<<<<<<<< completed onSelect$itemsPerPageLBId ");
	}//onSelect$itemsPerPageLBId

	
		public void onSelect$itemsPerPageLBId1(){
			logger.debug(">>>>>>>>>>>>> entered in onSelect$itemsPerPageLBId1");
			//setDefaultSettings();
			UserOrganization userOrganization = (UserOrganization)orgListBxId.getSelectedItem().getValue();
			Users users = (Users)usersListBxId.getSelectedItem().getValue();
			if(userOrganization == null && users == null){
				return;
			}
			enableOptFlagLbId.setVisible(true);
			enableOptSyncChkbxId.setVisible(true);
			applySettingsAnchId.setVisible(true);
			enableOptSyncChkbxId.setChecked(false);
			if(userOrganization.getOptSyncKey() != null && !(Constants.STRING_NILL.equals(userOrganization.getOptSyncKey())) ){
				optSynAuthKeyTbId.setReadonly(true);
				generateKeyAnchId.setVisible(false);
				itemsPerPageLBId.setVisible(false);
				itemsPerPageLBId1.setVisible(true);
				if(users == null){
					doSettings(userOrganization.getOptSyncKey().toString(), null);
				}
				else
					doSettings(userOrganization.getOptSyncKey().toString(), users.getUserId());
			}
			logger.debug("<<<<<<<<<<<<< completed onSelect$itemsPerPageLBId 1");
		}//onSelect$itemsPerPageLBId


	/**
	 * This method helps to populate the listBox.
	 */
	@Override
	public void render(Listitem li, UpdateOptSyncData updateOptSyncData, int arg2) throws Exception {
		logger.debug(">>>>>>>>>>>>> entered in render");
		//Setting Row
		li.setValue(updateOptSyncData);
		//setting optSync name
		li.appendChild(new Listcell(updateOptSyncData.getOptSyncName()));
		//setting optSync id
		li.appendChild(new Listcell(updateOptSyncData.getOptSyncId() != null ? updateOptSyncData.getOptSyncId().toString() : Constants.STRING_NILL) );
		//logger.info("OptSync plugin Id :: "+updateOptSyncData.getOptSyncId());
		//setting email id
		li.appendChild(new Listcell(updateOptSyncData.getEmailId() != null ? updateOptSyncData.getEmailId() : Constants.STRING_NILL));
		//setting status
		String status = getStatusOptSyncStatus(updateOptSyncData);
		li.appendChild(new Listcell(status));
		//setting downTime
		String downTime = getDownTime(updateOptSyncData);
		li.appendChild(new Listcell(downTime));
		//Actions
		Listcell lc = new Listcell();
		Hbox hbox = displayActions(updateOptSyncData, new Hbox());
		hbox.setParent(lc);
		lc.setParent(li);
		li.setParent( optSyncLBId);
		//setting Sytle
		li.setStyle("cursor:pointer;");
		logger.debug("<<<<<<<<<<<<< completed render ");
	}//render


	/**
	 * This method calcualte's OptSync Status
	 * @param updateOptSyncData
	 * @return status
	 */
	private String getStatusOptSyncStatus(UpdateOptSyncData updateOptSyncData) {
		logger.debug(">>>>>>>>>>>>> entered in getStatusOptSyncStatus");
		String status = Constants.STRING_NILL;
		if(OCConstants.OPT_SYNC_STATUS_NEW.equalsIgnoreCase(updateOptSyncData.getStatus())){
			status = "--";
		}
		else if(OCConstants.OPT_SYNC_STATUS_ACTIVE.equalsIgnoreCase(updateOptSyncData.getStatus())){
			status = "UP";
		}
		else if(OCConstants.OPT_SYNC_STATUS_DEACTIVE.equalsIgnoreCase(updateOptSyncData.getStatus())){
			status = "Down";
		}
		logger.debug("<<<<<<<<<<<<< completed getStatusOptSyncStatus ");
		return status;
	}//getStatusOptSyncStatus

	/**
	 * This method calculates the downTime
	 * @param updateOptSyncData
	 * @return downTime
	 */

	private String getDownTime(UpdateOptSyncData updateOptSyncData) {
		logger.debug(">>>>>>>>>>>>> entered in getDownTime");
		String downTime =Constants.STRING_NILL;
		if(updateOptSyncData != null && OCConstants.OPT_SYNC_STATUS_DEACTIVE.equalsIgnoreCase(updateOptSyncData.getStatus())){
			//down time
			Calendar currentTime=Calendar.getInstance();
			Calendar hitTime=(Calendar) (updateOptSyncData.getOptSyncHitTime() == null ? Calendar.getInstance() : updateOptSyncData.getOptSyncHitTime());
			long ctime=currentTime.getTimeInMillis();
			long htime=hitTime.getTimeInMillis();
			long diff=ctime-htime;

			//long diffSeconds = diff / 1000 % 60;
			long diffMinutes = diff / (60 * 1000) % 60;
			long diffHours = diff / (60 * 60 * 1000) % 24;
			long diffDays = diff / (24 * 60 * 60 * 1000);

			if(diffDays == 0 && diffHours == 0 ){
				downTime = diffMinutes + " minutes";
			}
			else if(diffDays == 0 && diffHours != 0 ){
				downTime = diffHours + " hours and "+ diffMinutes + " minutes";
			}else if(diffDays != 0  ){
				downTime = diffDays + " days, " + diffHours + " hours and "+ diffMinutes + " minutes";
			}
		}//down
		else{
			downTime = "--";
		}
		logger.debug("<<<<<<<<<<<<< completed getDownTime ");
		return downTime;
	}//getDownTime()

	/**
	 * This method draw actionAttributes like Edit,Vies,Delete,Activate
	 * @param updateOptSyncData
	 * @param hbox
	 * @return hBox
	 */
	private Hbox displayActions(UpdateOptSyncData updateOptSyncData, Hbox hbox) {
		logger.debug(">>>>>>>>>>>>> entered in displayActions");
		Image img ;
		//setting view image
		img= new Image("/img/theme/preview_icon.png");
		img.setStyle("margin-right:5px;cursor:pointer;");
		img.setTooltiptext("View Details");
		img.setAttribute("imageEventName", "view");
		img.addEventListener("onClick",this);
		img.setParent(hbox);
		//Adding Space between Images.
		Space space = new Space();
		space.setParent(hbox);
		//setting edit image
		img = new Image("/img/email_edit.gif");
		img.setTooltiptext("Edit");
		img.setStyle("margin-right:5px;cursor:pointer;");
		img.setAttribute("imageEventName", "editList");
		img.addEventListener("onClick", this);
		img.setParent(hbox);
		//Adding Space between Images.
		space = new Space();
		space.setParent(hbox);
		//setting Status
		if(OCConstants.OPT_SYNC_PLUGIN_STATUS_ACTIVE.equalsIgnoreCase(updateOptSyncData.getPluginStatus())){
			img= new Image("/img/pause_icn.png");
			img.setStyle("margin-right:5px;cursor:pointer;");
			img.setTooltiptext("Pause Alerts");
			img.setAttribute("imageEventName", "inactive");
			img.addEventListener("onClick",this);
			img.setParent(hbox);
		}
		else if(OCConstants.OPT_SYNC_PLUGIN_STATUS_INACTIVE.equalsIgnoreCase(updateOptSyncData.getPluginStatus())){
			img= new Image("/img/play_icn.png");
			img.setStyle("margin-right:5px;cursor:pointer;");
			img.setTooltiptext("Activate Alerts");
			img.setAttribute("imageEventName", "active");
			img.addEventListener("onClick",this);
			img.setParent(hbox);
		}
		//Adding Space between Images.
		space = new Space();
		space.setParent(hbox);
		//setting delete image
		img = new Image("/img/action_delete.gif");
		img.setTooltiptext("Delete");
		img.setStyle("margin-right:3px;cursor:pointer;");
		img.setAttribute("imageEventName", "userDelete");
		img.addEventListener("onClick", this);
		img.setParent(hbox);
		logger.debug("<<<<<<<<<<<<< completed displayActions ");
		return hbox;
	}//displayActions

	

	private void clearGroupBoxes(boolean flag) {
		optSyncPluginTbId.setValue(Constants.STRING_NILL);
		optSyncPluginTbId.setValue(Constants.STRING_NILL);
		emailIdTbId.setValue(Constants.STRING_NILL);
		optSyncPluginTbIdView.setValue(Constants.STRING_NILL);
		optSyncPluginTbIdView.setValue(Constants.STRING_NILL);
		emailIdTbIdView.setValue(Constants.STRING_NILL);
		addOneMoreEmailTBId1.setVisible(flag);
		addOneMoreEmailTBId2.setVisible(flag);
		addOneMoreEmailTBId3.setVisible(flag);
		addOneMoreEmailTBId4.setVisible(flag);
		deleteEmailTBId1.setVisible(flag);
		deleteEmailTBId2.setVisible(flag);
		deleteEmailTBId3.setVisible(flag);
		deleteEmailTBId4.setVisible(flag);
		deleteEmailTBId5.setVisible(flag);
		
	}

	/**
	 * This method handle's on click event of 
	 */
	public void onClick$addOptSyncPluginTBId() {
		logger.debug(">>>>>>>>>>>>> entered in onClick$addOptSyncPluginTBId");
	
		newOptSyncPluginGbIdView.setVisible(false);

		if(isViewDetails){
			logger.info("view details is true");
			if(updateOptSyncDataView != null){
				setOrganisationAndUserListbox(updateOptSyncDataView);
				setOptSynckey(updateOptSyncDataView);
				
				OptSyncService optSyncService = new OptSyncService();
				UserOrganization userOrganization1 = optSyncService.findByOrgId(updateOptSyncDataView.getOrgId());
				doSettings(userOrganization1.getOptSyncKey(), updateOptSyncDataView.getUserId());
				updateOptSyncDataView = null;
			}
			else{
				Components.removeAllChildren(orgListBxId);
				Components.removeAllChildren(usersListBxId);
				
				optSynAuthKeyTbId.setValue(Constants.STRING_NILL);
				setUserOrganization(orgListBxId);
			}
			isViewDetails =  false;
			generateKeyAnchId.setVisible(true);
		}

		if (!validateAccountId(orgListBxId)) {
			logger.error("Account Id was not selected");
			return ;
		}

		if(!validateSelectedUser(usersListBxId)){
			return;
		}

		String optSyncAuth =  optSynAuthKeyTbId.getValue().trim();
		if (Constants.STRING_NILL.equals(optSyncAuth.trim()) ||  optSyncAuth.length() < 16) {
			MessageUtil.setMessage(	"Please provide OptSync Authentication Key.","color:red", "TOP");
			return ;
		}
		logger.info("Setting focus");
		emailAddress1Label.setFocus(true);
		cancelBtnId.setFocus(true);
		saveBtnId.setFocus(true);
		addOptSyncPluginTBId.setVisible(false);
		clearTextBoxFeilds();
		//Disable delete toolBarButton
		deleteToolBarButton(false);
		optSyncPluginTbId.setFocus(true);
		newOptSyncPluginGbId.setVisible(true);
		pluginTbId.setReadonly(false);
		addOneMoreEmailTBId1.setVisible(true);
		session.removeAttribute("UpdateOptSyncDataType"); 
		session.removeAttribute("UpdateOptSyncData");
		logger.debug("<<<<<<<<<<<<< completed onClick$addOptSyncPluginTBId ");
	} // onClick$addOptSyncPluginTBId

	
	// on click of save button
	public void onClick$saveBtnId() {
		logger.debug(">>>>>>>>>>>>> entered in onClick$saveBtnId");
		sessionUpdateOptSyncData = (UpdateOptSyncData) session.getAttribute("UpdateOptSyncData");
		type = (String) session.getAttribute("UpdateOptSyncDataType");
		
		UpdateOptSyncData updateOptSyncData = null;
		//Create
		if(sessionUpdateOptSyncData == null && type  == null){

			if (!validateAccountId(orgListBxId) || !validateFeilds() ) {
				logger.error("Please select the Account ................. ");
				return ;
			}

			//as we are creating new plugin so new obj to be created
			sessionUpdateOptSyncData =new UpdateOptSyncData();
			long userOrgId = (((UserOrganization)orgListBxId.getSelectedItem().getValue()).getUserOrgId().longValue());
			Long usersId1 = ((Users)usersListBxId.getSelectedItem().getValue()).getUserId().longValue();
			logger.info("optsync plugin added by user with userId ::"+usersId1);
			//Setting UserID
			sessionUpdateOptSyncData.setUserId(usersId1);
			//Setting OptSyncPlugin id
			sessionUpdateOptSyncData.setOptSyncId(Long.parseLong(pluginTbId.getValue().trim()));
			//Setting OptSyncName
			sessionUpdateOptSyncData.setOptSyncName(optSyncPluginTbId.getValue().trim());
			//Setting OptSyncStatus As New
			sessionUpdateOptSyncData.setStatus(OCConstants.OPT_SYNC_STATUS_NEW);
			//Setting OptSyncPlugin is Active
			sessionUpdateOptSyncData.setPluginStatus(OCConstants.OPT_SYNC_PLUGIN_STATUS_ACTIVE);
			//Setting OptSync EmailId
			sessionUpdateOptSyncData.setEmailId(getEmails(emailIdTbId.getValue().trim()));
			//Setting OptSync Enabled 
			if(enableOptSyncChkbxId.isChecked()){
				sessionUpdateOptSyncData.setEnabledOptSyncFlag(OCConstants.Opt_SYNC_ENABLE_MOINTORING);
			}
			else{
				sessionUpdateOptSyncData.setEnabledOptSyncFlag(OCConstants.Opt_SYNC_DISABLE_MOINTORING);
			}
			//Setting User Organization
			sessionUpdateOptSyncData.setOrgId(userOrgId);
			updateOptSyncData = sessionUpdateOptSyncData ;
		}
		else if(sessionUpdateOptSyncData != null && type  != null){
			
			updateOptSyncData = sessionUpdateOptSyncData;
			
			if (!validateFeilds() )
			{
				logger.error("error in validating feilds");
				return;
			}
			//Updating OptSyncName
			sessionUpdateOptSyncData.setOptSyncName(optSyncPluginTbId.getValue().trim());
			//	sessionUpdateOptSyncData.setOptSyncId(Long.parseLong(pluginTbId.getValue().trim()));
			//Updating OptSyncEmail's
			sessionUpdateOptSyncData.setEmailId(getEmails(emailIdTbId.getValue().trim()));
			////Updating OptSyncEnabled
			if(enableOptSyncChkbxId.isChecked()){
				sessionUpdateOptSyncData.setEnabledOptSyncFlag(OCConstants.Opt_SYNC_ENABLE_MOINTORING);
			}
			else{
				sessionUpdateOptSyncData.setEnabledOptSyncFlag(OCConstants.Opt_SYNC_DISABLE_MOINTORING);
			}
		}

		logger.info("Confirmation Box ....................");
		try {
			String saveMsg = Constants.STRING_NILL;
			int confirm = Messagebox.show("Are you sure you want to save the OptSync Plug-in?", "Prompt", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
			optSynAuthKeyTbId.setFocus(true);
			optSyncLBId.setFocus(true);
			if(confirm != Messagebox.OK) return ;

			if(confirm == Messagebox.OK) {
				try {
					if(type == null) {
						OptSyncService optSyncService = new OptSyncService();
						//setting user organization authentication key
						String optSyncAuthkey = optSynAuthKeyTbId.getValue().trim();
						if(!validateOptSyncKey(optSyncAuthkey)){
							logger.error("Optsync Auth key already exists");
							return;
						}
						//UserOrganization userOrgObj = GetUser.getUserObj().getUserOrganization();//.setOptSyncKey(optSyncAuthkey);
						UserOrganization userOrganization= (UserOrganization)orgListBxId.getSelectedItem().getValue();
						/*if(userOrganization == null ) {
							MessageUtil.setMessage("Please select  User Organisation Account Id.","color:red;");
							return ;
						}
						String str = userOrganization.getOptSyncKey();
						if(!str.equalsIgnoreCase(optSyncAuthkey))
						{
							logger.info("Setting optsyncAuthkey to user org"+userOrganization.getUserOrgId().longValue());
							optSyncService.updateOptSynKeyByOrgId(userOrganization.getUserOrgId().longValue(), optSyncAuthkey);
						}
						else{
							logger.info("OptSynAuth key is already exist");
						}*/
						if(userOrganization != null) {
							String str = userOrganization.getOptSyncKey();
							if(str == null){
								str = "";
							}
							if(!str.equalsIgnoreCase(optSyncAuthkey))
							{
								
									logger.info("Setting optsyncAuthkey to user org"+userOrganization.getUserOrgId().longValue());
									
									optSyncService.updateOptSynKeyByOrgId(userOrganization.getUserOrgId().longValue(), optSyncAuthkey);
							}
							else{
								logger.info("OptSynAuth key is already exist");
							}
						}
						saveMsg = "created"; 
						optSyncService.saveOrUpdate(sessionUpdateOptSyncData);
						/*Redirect.goTo(PageListEnum.EMPTY);
						Redirect.goTo(PageListEnum.ADMIN_OPT_SYNC_USER);*/
						
						logger.info("-------"+saveMsg);
					}else if(type != null){
						saveMsg = "updated";
						OptSyncService optSyncService = new OptSyncService();
						optSyncService.saveOrUpdate(sessionUpdateOptSyncData);
						newOptSyncPluginGbId.setVisible(false);
						addOptSyncPluginTBId.setVisible(true);
						generateKeyAnchId.setVisible(false);
						saveBtnId.setVisible(false);

						/*Listitem tempList = new Listitem(allStr);
						tempList.setParent(usersListBxId);
						setUserOrganization(null);
						onSelect$orgListBxId();
						enableOptSyncChkbxId.setChecked(false);*/
					}
					
					MessageUtil.setMessage("OptSync Plug-in "+saveMsg +"  successfully.","color:green;");
				} catch (Exception e) {
					logger.error("Problem in "+saveMsg +" OptSync Plug-in Exception ::", e);
				}
			}
		} catch (Exception e) {
			logger.error("Exception ::", e);
		}
		
		if(updateOptSyncData != null){
		//Setting List Box
		setOrganisationAndUserListbox(updateOptSyncData);
		//Setting OptSync Key
		OptSyncService optSyncService = new OptSyncService();
		UserOrganization userOrganization1 = optSyncService.findByOrgId(updateOptSyncData.getOrgId());
		doSettings(userOrganization1.getOptSyncKey(), updateOptSyncData.getUserId());
		addOptSyncPluginTBId.setVisible(true);
		newOptSyncPluginGbId.setVisible(false);
		newOptSyncPluginGbIdView.setVisible(false);
		}
		else{
			logger.error("problem in optSync reload");
		}
		session.removeAttribute("UpdateOptSyncDataType"); 
		session.removeAttribute("UpdateOptSyncData");
		logger.debug("<<<<<<<<<<<<< completed onClick$saveBtnId ");
	}// save button

	
	/**
	 * This method helps to Enable n disable optSync monitoring
	 */
	public void onClick$applySettingsAnchId(){
		logger.debug(">>>>>>> Started  onClick$applyBtnId :: ");
		//1.Check Org
		if (!validateAccountId(orgListBxId)) {
			logger.error("Account Id was not selected");
			return ;
		}
		//2.Check users
		if(!validateSelectedUser(usersListBxId)){
			return;
		}
		//3.Check OptSyncKey
		String optSyncAuth =  optSynAuthKeyTbId.getValue().trim();
		if (Constants.STRING_NILL.equals(optSyncAuth.trim()) ||  optSyncAuth.length() < 16) {
			MessageUtil.setMessage(	"Please provide OptSync Authentication Key.","color:red", "TOP");
			return ;
		}
		
		Users users = (Users)usersListBxId.getSelectedItem().getValue();
		OptSyncService optSyncService =  new OptSyncService();
		List<UpdateOptSyncData> updateOptSyncDataList = optSyncService.findOptSyncByUserId(users.getUserId());
		
		if(updateOptSyncDataList != null && updateOptSyncDataList.size() > 0){
			
			if(enableOptSyncChkbxId.isChecked()){
				int confirm = Messagebox.show("Confirm to enable OptSync Monitoring.", "Confirm",Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
				if(confirm ==  Messagebox.CANCEL) return;

				if(confirm == Messagebox.OK) {

					//OptSyncService optSyncService = new OptSyncService();
					int rowsEffected =0;
					rowsEffected = optSyncService.updateOptSyncMonitoring(updateOptSyncDataList.get(0).getUserId(),OCConstants.Opt_SYNC_ENABLE_MOINTORING);
					if(rowsEffected >= 1){	
						MessageUtil.setMessage("OptSync monitoring enabled successfully.","color:blue","TOP");
						setMonitoringFlag(users.getUserId());
					}
				}

			}
			else{
				int confirm = Messagebox.show("Confirm to disable OptSync Monitoring.", "Confirm",Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
				if(confirm ==  Messagebox.CANCEL) return;

				if(confirm == Messagebox.OK) {

					//OptSyncService optSyncService = new OptSyncService();
					int rowsEffected =0;
					rowsEffected = optSyncService.updateOptSyncMonitoring(updateOptSyncDataList.get(0).getUserId(),OCConstants.Opt_SYNC_DISABLE_MOINTORING);
					if(rowsEffected >= 1){	
						MessageUtil.setMessage("OptSync monitoring disabled successfully.","color:blue","TOP");
						setMonitoringFlag(users.getUserId());
					}
				}
			}
			
			
			
			
			/*
			addOptSyncPluginTBId.setVisible(false);
			
			setMonitoringFlag(users.getUserId());
			
			
			boolean flag = enableOptSyncChkbxId.isChecked();
			
			if(!flag){
				
				
				int confirm = Messagebox.show("Confirm to disable OptSync Monitoring.", "Confirm",Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
				if(confirm ==  Messagebox.CANCEL) return;

				if(confirm == Messagebox.OK) {

					//OptSyncService optSyncService = new OptSyncService();
					int rowsEffected =0;
					rowsEffected = optSyncService.updateOptSyncMonitoring(updateOptSyncDataList.get(0).getUserId(),OCConstants.Opt_SYNC_DISABLE_MOINTORING);
					if(rowsEffected >= 1){	
						MessageUtil.setMessage("OptSync monitoring disabled successfully.","color:blue","TOP");
						setMonitoringFlag(users.getUserId());
					}
				}
			}
			else{

				int confirm = Messagebox.show("Confirm to enable OptSync Monitoring.", "Confirm",Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
				if(confirm ==  Messagebox.CANCEL) return;

				if(confirm == Messagebox.OK) {

					//OptSyncService optSyncService = new OptSyncService();
					int rowsEffected =0;
					rowsEffected = optSyncService.updateOptSyncMonitoring(updateOptSyncDataList.get(0).getUserId(),OCConstants.Opt_SYNC_ENABLE_MOINTORING);
					if(rowsEffected >= 1){	
						MessageUtil.setMessage("OptSync monitoring enabled successfully.","color:blue","TOP");
						setMonitoringFlag(users.getUserId());
					}
				}
			
			//}
		
			
			
		*/}
		else{
			MessageUtil.setMessage("No OptSync Plugin's for User. Please add OptSync Plugin.","color:red","TOP");
			addOptSyncPluginTBId.setVisible(true);
		}
		logger.debug("<<<<< Completed onClick$applyBtnId .");
	}//applyBtnId

	/**
	 * This method update's OptSync Monitoring
	 * @param updateOptSyncData
	 * @param status
	 * @param users
	 */
	private void updateOptSyncMonitoring(UpdateOptSyncData updateOptSyncData,String status) {
		logger.debug(">>>>>>> Started  updateOptSyncMonitoring :: ");
		logger.info("........."+status);
		if(OCConstants.DISABLE.equalsIgnoreCase(status)){
			enableOptSyncChkbxId.setChecked(false);
		}
		else{
			enableOptSyncChkbxId.setChecked(true);
		}
		int confirm = Messagebox.show("Confirm to "+status+" OptSync Monitoring.", "Confirm",Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
		if(confirm ==  Messagebox.CANCEL) return;

		if(confirm == Messagebox.OK) {
			OptSyncService optSyncService = new OptSyncService();
			int rowsEffected =0;
			if(OCConstants.ENABLE.equalsIgnoreCase(status)){
				logger.info("Enabling");
				rowsEffected = optSyncService.updateOptSyncMonitoring(updateOptSyncData.getUserId(),OCConstants.Opt_SYNC_ENABLE_MOINTORING);
				enableOptSyncChkbxId.setChecked(true);
			}
			else{
				logger.info("Disabling");
				rowsEffected = optSyncService.updateOptSyncMonitoring(updateOptSyncData.getUserId(),OCConstants.Opt_SYNC_DISABLE_MOINTORING);
				enableOptSyncChkbxId.setChecked(false);
			}
			if(rowsEffected >= 1)	
				MessageUtil.setMessage("OptSync monitoring "+status+"d successfully.","color:blue","TOP");
		}
		logger.debug("<<<<< Completed updateOptSyncMonitoring .");
	}//updateOptSyncMonitoring

	/**
	 * This method populate the table with optSyncSettings.
	 * @param authKey
	 * @param userId
	 */
	private void doSettings(String authKey ,Long userId){
		logger.debug(">>>>>>>>>>>>> entered in doSettings");
		logger.info("Authkey :::"+authKey);
		String filterQuery =Constants.STRING_NILL;
		String filterCountQuery=Constants.STRING_NILL; 
		String qryPrefix=Constants.STRING_NILL;
		optSynAuthKeyTbId.setValue(authKey);
		optSynAuthKeyTbId.setReadonly(true);

		generateKeyAnchId.setVisible(false);
		
		
		itemsPerPageLBId.setVisible(false);
		itemsPerPageLBId1.setVisible(true);
		String appendQuery = Constants.STRING_NILL;
		if(userId != null  ){
			appendQuery = "AND od.userId="+userId;
		}
		if(authKey == null){
		/*	itemsPerPageLBId.setVisible(false);
			itemsPerPageLBId1.setVisible(true); */

			optSynAuthKeyTbId.setReadonly(false);
			optSynAuthKeyTbId.setValue(Constants.STRING_NILL);
			generateKeyAnchId.setVisible(true);
			filterQuery = "SELECT DISTINCT od FROM UpdateOptSyncData od, UserOrganization o "
					+ "WHERE od.orgId=o.userOrgId AND o.optSyncKey IS NULL "+appendQuery;
			filterCountQuery = "SELECT COUNT(DISTINCT od.id) FROM UpdateOptSyncData od, UserOrganization o "
					+ "WHERE od.orgId=o.userOrgId AND o.optSyncKey IS NULL "+appendQuery;
		}
		else{
			filterQuery = "SELECT DISTINCT od FROM UpdateOptSyncData od, UserOrganization o "
					+ "WHERE od.orgId=o.userOrgId AND o.optSyncKey='"+authKey+"'"+appendQuery;
			filterCountQuery = "SELECT COUNT(DISTINCT od.id) FROM UpdateOptSyncData od, UserOrganization o "
					+ "WHERE od.orgId=o.userOrgId AND o.optSyncKey='"+authKey+"'"+appendQuery;
			qryPrefix="od";
		}
		Map<Integer, Field> objMap = new HashMap<Integer, Field>();

		try {
			objMap.put(0, UpdateOptSyncData.class.getDeclaredField("optSyncName"));
			objMap.put(1, UpdateOptSyncData.class.getDeclaredField("optSyncId"));
			objMap.put(2, UpdateOptSyncData.class.getDeclaredField("emailId"));
			objMap.put(3, UpdateOptSyncData.class.getDeclaredField("status"));
			objMap.put(4, UpdateOptSyncData.class.getDeclaredField("optSyncHitTime"));
			objMap.put(5, UpdateOptSyncData.class.getDeclaredField("enabledOptSyncFlag"));
		} catch (NoSuchFieldException e) {
			logger.error("Exception",e);
		} catch (SecurityException e) {
			logger.error("Exception",e);
		}

		optSyncPagingId.setPageSize(Integer.parseInt(itemsPerPageLBId1.getSelectedItem().getLabel()));
		if(filterQuery != null && filterCountQuery != null) {
			viewUDateOptSynObj = LBFilterEventListener.lbFilterSetup(optSyncLBId, optSyncPagingId, filterQuery, filterCountQuery, qryPrefix, objMap);
			Utility.refreshModel(viewUDateOptSynObj, 0, true);

		}
	}//doSettings
	private String getEmails(String emailId) {
		logger.debug(">>>>>>>>>>>>> entered in getEmails");
		//second email id
		if(isemailAddress2textbox){
			emailId = emailId + "," + emailAddress2TbId.getValue().trim();
		}
		//third email id
		if(isemailAddress3textbox){
			emailId = emailId + "," + emailAddress3TbId.getValue().trim();
		}
		//fourth email id
		if(isemailAddress4textbox){
			emailId = emailId + "," + emailAddress4TbId.getValue().trim();
		}
		//fifth email id
		if(isemailAddress5textbox){
			emailId = emailId + "," + emailAddress5TbId.getValue().trim();
		}
		logger.info("setting email id as ::::::::" +emailId);
		logger.debug("<<<<<<<<<<<<< completed getEmails ");
		return emailId;
	}//getEmails

	/**
	 * This method handle's onClick$resetBtnId  event.
	 */
	public void onClick$resetBtnId() {
		logger.debug(">>>>>>>>>>>>> entered in onClick$resetBtnId");
		optSyncPluginTbId.setValue(Constants.STRING_NILL);
		pluginTbId.setValue(Constants.STRING_NILL);
		pluginTbId.setReadonly(false);
		generateAnchId.setVisible(true);
		enableOptSyncChkbxId.setChecked(false);
		//userNameDivId.setVisible(true);
		
		clearEmailTextBoxes();
		logger.debug("<<<<<<<<<<<<< completed onClick$resetBtnId ");
		return;
	}//onClick$resetBtnId 


	/**
	 * This method generate's optSyncAuthenicationKey
	 */
	public void onClick$generateKeyAnchId() {

		logger.debug(">>>>>>>>>>>>> entered in onClick$generateKeyAnchId");
		if (!validateAccountId(orgListBxId)) {
			return ;
		}

		if(!validateSelectedUser(usersListBxId)){
			return;
		}

		//Get generated OptSync Authentication Key  
		String 	generateString = getOptSyncAuthenticationKey();
		optSynAuthKeyTbId.setValue(generateString);
		optSynAuthKeyTbId.setReadonly(true);
		generateKeyAnchId.setVisible(false);
		logger.debug("<<<<<<<<<<<<< completed onClick$generateKeyAnchId ");
	}//on click of key

	private String getOptSyncAuthenticationKey() {
		logger.debug(">>>>>>>>>>>>> entered in getOptSyncAuthenticationKey");
		String 	generateString = Constants.STRING_NILL;
		boolean genOptSynKeyFlag = false;
		OptSyncService optSyncService = new OptSyncService();

		while(!genOptSynKeyFlag) {
			generateString = randomString(16);
			UserOrganization userOrganization =  optSyncService.getUserOrgByOptSyncAuthKey(generateString);
			if(userOrganization == null ){
				genOptSynKeyFlag = true;
				logger.info("Generated OptSync Authentication Key is unquie " );
				break;
			}
			else{
				logger.error("As generated OptSync Authentication Key is not unique "+generateString +"\n Trying Again.......");
				continue;
			}
		}//while loop
		logger.debug("<<<<<<<<<<<<< completed getOptSyncAuthenticationKey ");
		return generateString;
	}//getOptSyncAuthenicationKey

	//generating 16 Characters String
	private String randomString( int len ) 
	{
		final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		Random rnd = new Random();
		StringBuilder sb = new StringBuilder( len );
		for( int i = 0; i < len; i++ ) 
			sb.append( AB.charAt( rnd.nextInt(AB.length()) ) );
			return sb.toString();
	}//randomString
	
	/**
	 * This method is use to generate plugIn Id
	 */
	public void onClick$generateAnchId() {
		logger.debug(">>>>>>>>>>>>> entered in onClick$generateAnchId");
		sessionUpdateOptSyncData = (UpdateOptSyncData) session.getAttribute("UpdateOptSyncData");
		type	= (String) session.getAttribute("UpdateOptSyncDataType");
		String pluginId = optSyncPluginTbId.getValue().trim();
		if(pluginId == null || pluginId.equals(Constants.STRING_NILL))
		{
			logger.info("plugIn id does not exist so creating");
			generateAnchId.setVisible(true);
			long generateNumber= generateSixteenDigit();
			pluginTbId.setValue(generateNumber +Constants.STRING_NILL);
			pluginTbId.setReadonly(true);
			generateAnchId.setVisible(false);
		}//if
		else{
			logger.info("plugIn Id of user already exist");
			if(sessionUpdateOptSyncData != null && type !=  null){
				generateAnchId.setVisible(false);
				pluginTbId.setReadonly(true);
			}//if
			else{
				generateAnchId.setVisible(true);
				generateAnchId.setVisible(false);
				pluginTbId.setValue(generateSixteenDigit() +Constants.STRING_NILL);
				pluginTbId.setReadonly(true);
			}//else
		}//else
		logger.debug("<<<<<<<<<<<<< completed onClick$generateAnchId ");
	} //generate id


	/**
	 * This method to generate 16 digit number.
	 * @return generateOptSyncId
	 */
	public long generateSixteenDigit(){
		logger.debug(">>>>>>>>>>>>> entered in generateSixteenDigit");
		long generateOptSyncId =0L; 
		OptSyncService optSyncService = new OptSyncService();
		while(true){
			Random rand = new Random();
			generateOptSyncId = 1 + rand.nextInt(9);  
			for(int i = 0; i < 15; i++) {
				generateOptSyncId *= 10L;
				generateOptSyncId += rand.nextInt(10);
			}
			List<UpdateOptSyncData>	optSyncList =optSyncService.getAllOptSyncPluginByPluginId(generateOptSyncId);
			if(optSyncList == null ){
				logger.info("Generated plugIn id is unquie ");
				break;
			}
			else{
				continue;
			}
		}//while
		logger.debug("<<<<<<<<<<<<< completed generateSixteenDigit ");
		return generateOptSyncId;
	}//generateSixteenDigit

	/**
	 * This method handle's onEvent.
	 */
	@Override
	public void onEvent(Event event) throws Exception {
		logger.debug(">>>>>>>>>>>>> entered in onEvent");
		super.onEvent(event);

		if(event.getTarget() instanceof Image) {
			Image img = (Image)event.getTarget();
			String imageEventName = img.getAttribute("imageEventName").toString();
			Listitem li = (Listitem)img.getParent().getParent().getParent();

			UpdateOptSyncData updateOptSyncData=(UpdateOptSyncData)li.getValue();
			UserOrganization userOrganization = null;
			Users users = null;
			if(orgListBxId != null && orgListBxId.getSelectedItem() != null){
				userOrganization = (UserOrganization)orgListBxId.getSelectedItem().getValue();
			}

			if(usersListBxId != null && usersListBxId.getSelectedItem() != null ){
				users =(Users)usersListBxId.getSelectedItem().getValue();
			}

			if(updateOptSyncData == null){
				MessageUtil.setMessage("Please Select the Feild.", "red");
				return;
			}

			if(imageEventName.equals("editList")) {
				editList(updateOptSyncData);
			} 
			else if(imageEventName.equals("userDelete")) {
				logger.info("Deleting the OptSync plugIn with plugIn id ::"+updateOptSyncData.getOptSyncId());
				deleteOptSyncSetting(updateOptSyncData,userOrganization,users);
			}//user delete
			else if(imageEventName.equalsIgnoreCase("view") && updateOptSyncData  != null){
				viewOptSyncDetails(updateOptSyncData);
			}
			else if(imageEventName.equalsIgnoreCase("active") && updateOptSyncData  != null) {
				updateEmailAlerts(updateOptSyncData,"active",userOrganization,users);
			}
			else if(imageEventName.equalsIgnoreCase("inactive") && updateOptSyncData  != null) {

				newOptSyncPluginGbId.setVisible(false);
				newOptSyncPluginGbIdView.setVisible(false);

				createWinId$opySyncAlertsWinId.setVisible(true);
				createWinId$opySyncAlertsWinId.doHighlighted();

				createWinId$opySyncAlertsWinId.setAttribute(Constants.OPTSYNC_OBJ, updateOptSyncData);

				//updateEmailAlerts(updateOptSyncData,"pause",userOrganization,users);
			} 
		}
		
		logger.debug("<<<<<<<<<<<<< completed onEvent ");
	}//on Event

	/**
	 * This method is use to delete the OptSyncSetting.
	 * @param updateOptSyncData
	 */
	private void deleteOptSyncSetting(UpdateOptSyncData updateOptSyncData,UserOrganization userOrganization,Users users) {
		logger.debug(">>>>>>>>>>>>> entered in deleteOptSyncSetting");
		//	logger.info("Deleting the OptSync plugIn with plugIn id ::"+updateOptSyncData.getOptSyncId()+" From userOrganizaiton "+userOrganization.getUserOrgId()+" with userId :"+users.getUserId());
		try {
			int confirm = Messagebox.show("Confirm to delete the selected OptSync Plug-in.", "Confirm",Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
			if(confirm ==  Messagebox.CANCEL) return;

			if(confirm == Messagebox.OK) {
				newOptSyncPluginGbId.setVisible(false);
				newOptSyncPluginGbIdView.setVisible(false);
				OptSyncService optSyncService = new OptSyncService();
				optSyncService.deleteOptSyncPlugin(updateOptSyncData);

				MessageUtil.setMessage("OptSync Plug-in  deleted successfully.","color:blue","TOP");
				if(userOrganization ==  null && users ==  null){
					setDefaultSettings();
				}
				else if(userOrganization !=  null && users ==  null){
					doSettings(userOrganization.getOptSyncKey(), null);
				}
				else if(userOrganization ==  null && users !=  null){
					doSettings(null,users.getUserId());
				}
				else if(userOrganization !=  null && users !=  null){
					doSettings(userOrganization.getOptSyncKey(),users.getUserId());
				}
				//setDefaultSettings();
			}//if
		}//try
		catch (Exception e) {
			logger.error("Exception ::", e);
		}
		logger.debug("<<<<<<<<<<<<< completed deleteOptSyncSetting ");
		return;
	}//deleteOptSyncSetting

	/**
	 * This method update the Email Alerts 
	 * @param updateOptSyncData
	 * @param status
	 */
	private void updateEmailAlerts(UpdateOptSyncData updateOptSyncData,	String status,UserOrganization userOrganization,Users users) {
		logger.debug(">>>>>>>>>>>>> entered in updateEmailAlerts");
		newOptSyncPluginGbId.setVisible(false);
		newOptSyncPluginGbIdView.setVisible(false);

		String pluginStatus = OCConstants.OPT_SYNC_PLUGIN_STATUS_INACTIVE;
		if("active".equalsIgnoreCase(status)){
			pluginStatus =  OCConstants.OPT_SYNC_PLUGIN_STATUS_ACTIVE;
			status= "activate";
		}

		try {
			int confirm = Messagebox.show("Confirm to "+status+" OptSync alerts.", "Confirm",Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
			if(confirm ==  Messagebox.CANCEL) return;

			if(confirm == Messagebox.OK) {
				
				
				
				OptSyncService optSyncService = new OptSyncService();
				int rowseffected = optSyncService.updatePluginStatus(pluginStatus, updateOptSyncData.getOptSyncId());
				if(rowseffected == 1){
					if(userOrganization ==  null && users ==  null){
						setDefaultSettings();
					}
					else if(userOrganization !=  null && users ==  null){
						doSettings(userOrganization.getOptSyncKey(), null);
					}
					else if(userOrganization ==  null && users !=  null){
						doSettings(null,users.getUserId());
					}
					else if(userOrganization !=  null && users !=  null){
						doSettings(userOrganization.getOptSyncKey(),users.getUserId());
					}
					MessageUtil.setMessage("OptSync alerts "+status+"d successfully.","color:blue","TOP");
				}
				else{
					MessageUtil.setMessage("OptSync alerts "+status+" failed.","red");
					return;
				}
			}//if
		}//try
		catch (Exception e) {
			logger.error("Exception ::", e);
		}
		logger.debug("<<<<<<<<<<<<< completed updateEmailAlerts ");
		return;
	}//updateEmailAlerts
	/**
	 * This method help in Editing EamilId, plugIn Name or adding email id
	 * @param updateOptSyncData
	 */
	Label emailAddress1Label ;

	private void editList(UpdateOptSyncData updateOptSyncData ){
		logger.debug(">>>>>>>>>>>>> entered in editList");
		try{
			clearGroupBoxes(false);
			session.setAttribute("UpdateOptSyncDataType", "edit");
			session.setAttribute("UpdateOptSyncData", updateOptSyncData);
			//View is disabled
			newOptSyncPluginGbIdView.setVisible(false);
			//disabled Add new plugIn 
			addOptSyncPluginTBId.setVisible(false);
			// Disabled generate plugIn id link
			generateAnchId.setVisible(false);
			//enable apply settings
			applySettingsAnchId.setVisible(true);
			newOptSyncPluginGbId.setVisible(true);
			generateKeyAnchId.setVisible(true);
			saveBtnId.setLabel("Update");
			saveBtnId.setVisible(true);
			resetBtnId.setVisible(false);
			
			//Setting List Box
			setOrganisationAndUserListbox(updateOptSyncData);
			//Setting OptSync Key
			setOptSynckey(updateOptSyncData);
			//Setting OptSync Monitoring 
			setMonitoringFlag(updateOptSyncData.getUserId());
			/*if( OCConstants.Opt_SYNC_ENABLE_MOINTORING.equalsIgnoreCase(updateOptSyncData.getEnabledOptSyncFlag())  && updateOptSyncData.getEnabledOptSyncFlag() != null)
			{
				enableOptSyncChkbxId.setChecked(true);
			}
			else{
				enableOptSyncChkbxId.setChecked(false);
			}*/
			//Setting OptSync plugIn Name
			optSyncPluginTbId.setValue(updateOptSyncData.getOptSyncName());
			//Setting plugIn Id
			pluginTbId.setValue(updateOptSyncData.getOptSyncId().toString());
			pluginTbId.setReadonly(true);
			//Setting Emails
			String emailIds=updateOptSyncData.getEmailId();

			if(emailIds != null){
				displayEmailId(emailIds);
			}
			//Added for 2.3.11
			emailAddress1Label.setFocus(true);
			cancelBtnId.setFocus(true);
			saveBtnId.setFocus(true);
			resetOrgListAnchId.setVisible(true);
		}catch (Exception e) {
			logger.error(" - ** Exception while trying to edit the list settings - " + e + " **");
		}
		logger.debug("<<<<<<<<<<<<< completed editList ");
	}//editList

	/**
	 * This method display's Email id
	 * @param emailIds
	 */

	private void displayEmailId(String emailIds) {
		logger.debug(">>>>>>>>>>>>> entered in displayEmailId");
		List<String> items = Arrays.asList(emailIds.split("\\s*,\\s*"));
		int itemsSize =  items.size();

		switch(itemsSize){

		case 1:
			//Set First email id
			emailIdTbId.setValue(items.get(0));
			deleteEmailTBId1.setVisible(false);
			addOneMoreEmailTBId1.setVisible(true);
			//Hide 2,3,4,5 email
			secondEmailId(false);
			thirdEmailId(false);
			fourthEmailId(false);
			fifthEmailId(false);
			break;

		case 2://second email id
			deleteEmailTBId1.setVisible(true);
			addOneMoreEmailTBId1.setVisible(false);
			addOneMoreEmailTBId2.setVisible(true);

			//Setting First and Second Email id
			emailIdTbId.setValue(items.get(0));
			emailAddress2TbId.setValue(items.get(1));
			//Show Second Email id
			secondEmailId(true);

			//3,4 & 5 th as false
			thirdEmailId(false);
			fourthEmailId(false);
			fifthEmailId(false);

			break;			
		case 3://third email id
			deleteEmailTBId1.setVisible(true);
			addOneMoreEmailTBId1.setVisible(false);
			addOneMoreEmailTBId2.setVisible(false);
			addOneMoreEmailTBId3.setVisible(true);
			//Setting First,Second,third Email id
			emailIdTbId.setValue(items.get(0));	
			emailAddress2TbId.setValue(items.get(1));
			emailAddress3TbId.setValue(items.get(2));

			//Show 2 & 3 Email
			secondEmailId(true);
			thirdEmailId(true);

			//Hide 4,5 Email 
			fourthEmailId(false);
			fifthEmailId(false);
			break;

		case 4://fourth email id

			deleteEmailTBId1.setVisible(true);
			addOneMoreEmailTBId1.setVisible(false);
			addOneMoreEmailTBId2.setVisible(false);
			addOneMoreEmailTBId3.setVisible(false);
			addOneMoreEmailTBId4.setVisible(true);

			//Setting First,Second,Third,Fourth Email id
			emailIdTbId.setValue(items.get(0));	
			emailAddress2TbId.setValue(items.get(1));
			emailAddress3TbId.setValue(items.get(2));
			emailAddress4TbId.setValue(items.get(3));

			//Show 2,3,4 email id
			secondEmailId(true);
			thirdEmailId(true);
			fourthEmailId(true);

			//Hide Fifth Email id
			fifthEmailId(false);

			break;

		case 5://fifth email id
			deleteEmailTBId1.setVisible(true);
			addOneMoreEmailTBId1.setVisible(false);
			addOneMoreEmailTBId2.setVisible(false);
			addOneMoreEmailTBId3.setVisible(false);
			addOneMoreEmailTBId4.setVisible(false);

			//Setting First and Second Email id
			emailIdTbId.setValue(items.get(0));	
			emailAddress2TbId.setValue(items.get(1));
			emailAddress3TbId.setValue(items.get(2));
			emailAddress4TbId.setValue(items.get(3));
			emailAddress5TbId.setValue(items.get(4));

			//Show all email id
			secondEmailId(true);
			thirdEmailId(true);
			fourthEmailId(true);
			fifthEmailId(true);
			break;
		}//switch
		logger.debug("<<<<<<<<<<<<< completed displayEmailId ");
	}//displayEmailId

	/**
	 * This method show's or hide's Email Address Label,Text box, add email btn, delete email button
	 * @param flag
	 */
	private void secondEmailId(boolean flag) {
		logger.debug(">>>>>>>>>>>>> entered in secondEmailId");
		emailAddress2Label.setVisible(flag);
		emailAddress2TbId.setVisible(flag);
		deleteEmailTBId2.setVisible(flag);
		isemailAddress2textbox = flag;
		logger.debug("<<<<<<<<<<<<< completed secondEmailId ");
	}//secondEmailId

	/**
	 * This method show's or hide's Email Address Label,Text box, add email btn, delete email button
	 * @param flag
	 */
	private void thirdEmailId(boolean flag) {
		logger.debug(">>>>>>>>>>>>> entered in thirdEmailId");
		emailAddress3Label.setVisible(flag);
		emailAddress3TbId.setVisible(flag);
		deleteEmailTBId3.setVisible(flag);
		isemailAddress3textbox = flag;
		logger.debug("<<<<<<<<<<<<< completed thirdEmailId ");
	}//thirdEmailId

	/**
	 * This method show's or hide's Email Address Label,Text box, add email btn, delete email button
	 * @param flag
	 */
	private void fourthEmailId(boolean flag) {
		logger.debug(">>>>>>>>>>>>> entered in fourthEmailId");
		emailAddress4Label.setVisible(flag);
		emailAddress4TbId.setVisible(flag);
		deleteEmailTBId4.setVisible(flag);
		isemailAddress4textbox = flag;
		logger.debug("<<<<<<<<<<<<< completed fourthEmailId ");
	}//fourthEmailId


	/**
	 * This method show's or hide's Email Address Label,Text box, add email btn, delete email button
	 * @param flag
	 */
	private void fifthEmailId(boolean flag) {
		logger.debug(">>>>>>>>>>>>> entered in fifthEmailId");
		emailAddress5Label.setVisible(flag);
		emailAddress5TbId.setVisible(flag);
		deleteEmailTBId5.setVisible(flag);
		isemailAddress5textbox = flag;
		logger.debug("<<<<<<<<<<<<< completed fifthEmailId ");
	}//fifthEmailId

	/**
	 * This method helps to add's maxLength,value and focus to secondEmail
	 * @param flag
	 */
	private void secondEmailIdConstraints(boolean flag) {
		logger.debug(">>>>>>>>>>>>> entered in secondEmailIdConstraints");
		emailAddress2TbId.setValue(Constants.STRING_NILL);
		emailAddress2TbId.setMaxlength(OCConstants.MAX_EMAIL_ID_LENGTH);
		emailAddress2TbId.setFocus(flag);
		logger.debug("<<<<<<<<<<<<< completed secondEmailIdConstraints ");
	}//secondEmailIdConstraints

	/**
	 * This method helps to add's maxLength,value and focus to thirdEmail
	 * @param flag
	 */
	private void thirdEmailIdConstraints(boolean flag) {
		logger.debug(">>>>>>>>>>>>> entered in thirdEmailIdConstraints");
		emailAddress3TbId.setValue(Constants.STRING_NILL);
		emailAddress3TbId.setMaxlength(OCConstants.MAX_EMAIL_ID_LENGTH);
		emailAddress3TbId.setFocus(flag);
		logger.debug("<<<<<<<<<<<<< completed thirdEmailIdConstraints ");
	}//thirdEmailIdConstraints

	/**
	 * This method helps to add's maxLength,value and focus to fourthEmail
	 * @param flag
	 */
	private void fourthEmailIdConstraints(boolean flag) {
		logger.debug(">>>>>>>>>>>>> entered in fourthEmailIdConstraints");
		emailAddress4TbId.setValue(Constants.STRING_NILL);
		emailAddress4TbId.setMaxlength(OCConstants.MAX_EMAIL_ID_LENGTH);
		emailAddress4TbId.setFocus(flag);
		logger.debug("<<<<<<<<<<<<< completed fourthEmailIdConstraints ");
	}//fourthEmailIdConstraints
	/**
	 * This method helps to add's maxLength,value and focus to fifthEmail
	 * @param flag
	 */
	private void fifthEmailIdConstraints(boolean flag) {
		logger.debug(">>>>>>>>>>>>> entered in fifthEmailIdConstraints");
		emailAddress5TbId.setValue(Constants.STRING_NILL);
		emailAddress5TbId.setMaxlength(OCConstants.MAX_EMAIL_ID_LENGTH);
		emailAddress5TbId.setFocus(flag);
		logger.debug("<<<<<<<<<<<<< completed fifthEmailIdConstraints ");
	}//fifthEmailIdConstraints

	/**
	 * This method helps to display email id
	 * @param emailIds
	 */
	private void displayEmailIdView(String emailIds) {
		logger.debug(">>>>>>>>>>>>> entered in displayEmailIdView");
		List<String> items = Arrays.asList(emailIds.split("\\s*,\\s*"));
		int itemsSize = items.size();

		switch(itemsSize){
		case 1:
			emailIdTbIdView.setValue(items.get(0));
			emailIdTbIdView.setReadonly(true);
			//Hide 2,3,4,5 Email
			secondEmailView(false);
			thirdEmailView(false);
			fourthEmailView(false);
			fifthEmailView(false);
			break;
		case 2:
			//Show 1 and 2
			emailIdTbIdView.setValue(items.get(0));	
			emailAddress2TbIdView.setValue(items.get(1));
			secondEmailView(true);

			//Hide 3,4,5 Email
			thirdEmailView(false);
			fourthEmailView(false);
			fifthEmailView(false);
			break;
		case 3:
			//Show 1,2 and 3
			emailIdTbIdView.setValue(items.get(0));	
			emailAddress2TbIdView.setValue(items.get(1));
			emailAddress3TbIdView.setValue(items.get(2));
			secondEmailView(true);
			thirdEmailView(true);
			//Hide 4,5 Email
			fourthEmailView(false);
			fifthEmailView(false);
			break;
		case 4:
			//Show 1,2,3 and 4
			emailIdTbIdView.setValue(items.get(0));	
			emailAddress2TbIdView.setValue(items.get(1));
			emailAddress3TbIdView.setValue(items.get(2));
			emailAddress4TbIdView.setValue(items.get(3));
			secondEmailView(true);
			thirdEmailView(true);
			fourthEmailView(true);
			//Hide 4,5 Email
			fifthEmailView(false);
			break;
		case 5:
			//show all
			emailIdTbIdView.setValue(items.get(0));	
			emailAddress2TbIdView.setValue(items.get(1));
			emailAddress3TbIdView.setValue(items.get(2));
			emailAddress4TbIdView.setValue(items.get(3));
			emailAddress5TbIdView.setValue(items.get(4));
			secondEmailView(true);
			thirdEmailView(true);
			fourthEmailView(true);
			fifthEmailView(true);
			break;
		}//switch
		logger.debug("<<<<<<<<<<<<< completed displayEmailIdView ");
	}//displayEmailIdView

	/**
	 * This method hide's or show's Email id label and textBox
	 * @param flag
	 */
	private void secondEmailView(boolean flag) {
		logger.debug(">>>>>>>>>>>>> entered in secondEmailView");
		emailAddress2LabelView.setVisible(flag);
		emailAddress2TbIdView.setVisible(flag);
		logger.debug("<<<<<<<<<<<<< completed secondEmailView ");
	}//secondEmailView
	/**
	 * This method hide's or show's Email id label and textBox
	 * @param flag
	 */
	private void thirdEmailView(boolean flag) {
		logger.debug(">>>>>>>>>>>>> entered in thirdEmailView");
		emailAddress3LabelView.setVisible(flag);
		emailAddress3TbIdView.setVisible(flag);
		logger.debug("<<<<<<<<<<<<< completed thirdEmailView ");
	}//thirdEmailView

	/**
	 * This method hide's or show's Email id label and textBox
	 * @param flag
	 */
	private void fourthEmailView(boolean flag) {
		logger.debug(">>>>>>>>>>>>> entered in fourthEmailView");
		emailAddress4LabelView.setVisible(flag);
		emailAddress4TbIdView.setVisible(flag);
		logger.debug("<<<<<<<<<<<<< completed fourthEmailView ");
	}//fourthEmailView
	/**
	 * This method hide's or show's Email id label and textBox
	 * @param flag
	 */
	private void fifthEmailView(boolean flag) {
		logger.debug(">>>>>>>>>>>>> entered in fifthEmailView");
		emailAddress5LabelView.setVisible(flag);
		emailAddress5TbIdView.setVisible(flag);
		logger.debug("<<<<<<<<<<<<< completed fifthEmailView ");
	}//fifthEmailView

	/**
	 * This method clear email textBox
	 */
	private void clearEmailIdTextBoxView() {
		logger.debug(">>>>>>>>>>>>> entered in clearEmailIdTextBoxView");
		emailIdTbIdView.setValue(Constants.STRING_NILL);	
		emailAddress2TbIdView.setValue(Constants.STRING_NILL);
		emailAddress3TbIdView.setValue(Constants.STRING_NILL);
		emailAddress4TbIdView.setValue(Constants.STRING_NILL);
		emailAddress5TbIdView.setValue(Constants.STRING_NILL);
		logger.debug("<<<<<<<<<<<<< completed clearEmailIdTextBoxView ");
	}//clearEmailIdTextBoxView

	/**
	 * This method set's the userOrganisation name and which user.
	 * @param updateOptSyncData
	 */
	private void setOrganisationAndUserListbox(UpdateOptSyncData updateOptSyncData) {
		logger.debug(">>>>>>>>>>>>> entered in setOrganisationAndUserListbox");
		Long orgId = updateOptSyncData.getOrgId();
		OptSyncService optSyncService = new OptSyncService();
		UserOrganization tempOrg = optSyncService.findByOrgId(orgId);
		Users users = optSyncService.findByUserId(updateOptSyncData.getUserId());

		//Setting User Organization Details
		Components.removeAllChildren(orgListBxId);
		Listitem listItem = null;
		listItem = new Listitem(tempOrg.getOrgExternalId().trim(),tempOrg);
		listItem.setParent(orgListBxId);
		orgListBxId.setSelectedItem(listItem);
		orgListBxId.setSelectedIndex(0);

		//Setting User Details
		Components.removeAllChildren(usersListBxId);
		listItem = new Listitem(Utility.getOnlyUserName(users.getUserName()),users);
		listItem.setParent(usersListBxId);
		usersListBxId.setSelectedItem(listItem);
		usersListBxId.setSelectedIndex(0);
		
		visibleHtmlElements(true);
		enableOptFlagLbId.setVisible(true);
		enableOptSyncChkbxId.setVisible(true);
		applySettingsAnchId.setVisible(true);
		
		logger.debug("<<<<<<<<<<<<< completed setOrganisationAndUserListbox ");
	}//setOrganisationAndUserListbox

	private void visibleHtmlElements(boolean flag) {
		logger.debug("Starting  visibleHtmlElements");
		userNameStarLbId.setVisible(flag);
		userNameLbId.setVisible(flag);
		usersListBxId.setVisible(flag);
		optSyncAuthStarLbId.setVisible(flag);
		optSyncAuthLbId.setVisible(flag);
		optSynAuthKeyTbId.setVisible(flag);
		generateKeyAnchId.setVisible(flag);
		
		
		logger.debug("Completed  visibleHtmlElements");
	}

	/**
	 * This method set the OptSync Key and also add's read only attribute to it.
	 * @param updateOptSyncData
	 */
	private void setOptSynckey(UpdateOptSyncData updateOptSyncData) {
		logger.debug(">>>>>>>>>>>>> entered in setOptSynckey");
		Long orgId = updateOptSyncData.getOrgId();
		OptSyncService optSyncService = new OptSyncService();
		UserOrganization userOrganization = optSyncService.findByOrgId(orgId);

		if(userOrganization !=null ){
			optSynAuthKeyTbId.setValue(userOrganization.getOptSyncKey());
			optSynAuthKeyTbId.setReadonly(true);
			generateKeyAnchId.setVisible(false);
		}
		logger.debug("<<<<<<<<<<<<< completed setOptSynckey ");
	}//setOptSynckey

	/**
	 * This method populates the optSync details.
	 * @param updateOptSyncData
	 */
	private void viewOptSyncDetails(UpdateOptSyncData updateOptSyncData) {
		logger.debug(">>>>>>>>>>>>> entered in viewOptSyncDetails");
		clearEmailIdTextBoxView();
		//Edit plugIn Group box disabled
		newOptSyncPluginGbId.setVisible(false);
		applySettingsAnchId.setVisible(false);
		newOptSyncPluginGbIdView.setVisible(true);
		addOptSyncPluginTBId.setVisible(true);
		updateOptSyncDataView  = updateOptSyncData;
		isViewDetails = true;

		try{
			//Setting List Box
			setOrganisationAndUserListbox(updateOptSyncData);
			//Setting OptSync Key
			setOptSynckey(updateOptSyncData);
			//Setting OptSync Monitoring 
			setMonitoringFlag(updateOptSyncData.getUserId());
			/*if( OCConstants.Opt_SYNC_ENABLE_MOINTORING.equalsIgnoreCase(updateOptSyncData.getEnabledOptSyncFlag())  && updateOptSyncData.getEnabledOptSyncFlag() != null)
			{
				enableOptSyncChkbxId.setChecked(true);
			}
			else{
				enableOptSyncChkbxId.setChecked(false);
			}*/
			//Setting OptSync plugIn Name
			optSyncPluginTbIdView.setValue(updateOptSyncData.getOptSyncName());
			//Setting plugIn Id
			pluginTbIdView.setValue(updateOptSyncData.getOptSyncId().toString());
			//setting email id
			String emailIds=updateOptSyncData.getEmailId();
			if(emailIds != null){
				displayEmailIdView(emailIds);
			}
			//readOnly 
			setReadOnlyView(true);
			resetOrgListAnchId.setVisible(true);
		}catch (Exception e) {
			logger.error(" - ** Exception while trying to Display the list settings - " + e + " **");
		}
		logger.debug("<<<<<<<<<<<<< completed viewOptSyncDetails ");
	}//viewOptSyncDetails


	/**
	 * This method sets monitoring flag
	 * @param updateOptSyncData
	 */
	private void setMonitoringFlag(Long userId) {
		logger.debug(">>>>>>> Started  setMonitoringFlag :: ");
		OptSyncService optSyncService =  new OptSyncService();
		enableOptSyncChkbxId.setChecked(false);
		List<UpdateOptSyncData> updateOptSyncDataList = optSyncService.findOptSyncByUserId(userId);
		if(updateOptSyncDataList != null && updateOptSyncDataList.size() > 0){
			for (UpdateOptSyncData updateOptSyncData : updateOptSyncDataList) {
				if( OCConstants.Opt_SYNC_ENABLE_MOINTORING.equalsIgnoreCase(updateOptSyncData.getEnabledOptSyncFlag())  && updateOptSyncData.getEnabledOptSyncFlag() != null){
					enableOptSyncChkbxId.setChecked(true);
					break;
				}
				
			}//for
		}
		logger.debug("<<<<< Completed setMonitoringFlag .");
	}//setMonitoringFlag

	/**
	 * This method set or reset read only view.
	 * @param flag
	 */
	private void setReadOnlyView(boolean flag) {
		logger.debug(">>>>>>>>>>>>> entered in setReadOnlyView");
		optSynAuthKeyTbId.setReadonly(flag);
		optSyncPluginTbIdView.setReadonly(flag);
		pluginTbIdView.setReadonly(flag);

		emailIdTbIdView.setReadonly(flag);	
		emailAddress2TbIdView.setReadonly(flag);
		emailAddress3TbIdView.setReadonly(flag);
		emailAddress4TbIdView.setReadonly(flag);
		emailAddress5TbIdView.setReadonly(flag);
		logger.debug("<<<<<<<<<<<<< completed setReadOnlyView ");
	}//setReadOnlyView

	public void clearTextBoxFeilds() {
		logger.debug(">>>>>>>>>>>>> entered in clearTextBoxFeilds");
		optSyncPluginTbId.setValue(Constants.STRING_NILL);
		pluginTbId.setValue(Constants.STRING_NILL);
		generateAnchId.setVisible(true);
		//Clear Email TextBoxes.
		clearEmailTextBoxes();
		//Clear All Secondary Email's
		clearAllEmailConstraints(false);
		//Clear All selected textBoxes
		clearSelectedTextBoxes();
		saveBtnId.setLabel("Add");
		saveBtnId.setVisible(true);
		resetBtnId.setVisible(true);
		logger.debug("<<<<<<<<<<<<< completed clearTextBoxFeilds ");
		return;
	}// clear text boxes

	private void clearSelectedTextBoxes() {
		logger.debug(">>>>>>>>>>>>> entered in clearSelectedTextBoxes");
		isemailAddress2textbox = false;
		isemailAddress3textbox = false;
		isemailAddress4textbox = false;
		isemailAddress5textbox = false;
		logger.debug("<<<<<<<<<<<<< completed clearSelectedTextBoxes");
	}//clearSelectedTextBoxex

	/**
	 * This method clears all the Email TextBoxes.
	 */
	private void clearEmailTextBoxes() {
		logger.debug(">>>>>>>>>>>>> entered in clearEmailTextBoxes");
		emailIdTbId.setValue(Constants.STRING_NILL);
		emailAddress2TbId.setValue(Constants.STRING_NILL);
		emailAddress3TbId.setValue(Constants.STRING_NILL);
		emailAddress4TbId.setValue(Constants.STRING_NILL);
		emailAddress5TbId.setValue(Constants.STRING_NILL);
		logger.debug("<<<<<<<<<<<<< completed clearEmailTextBoxes ");
	}//clearEmailTextBoxes

	/**
	 * This method clear's all the Email TextBoxes and Constraints.
	 * @param flag
	 */
	private void clearAllEmailConstraints(boolean flag) {
		logger.debug(">>>>>>>>>>>>> entered in clearAllEmailConstraints");
		secondEmailId(flag);
		secondEmailIdConstraints(flag);
		thirdEmailId(flag);
		thirdEmailIdConstraints(flag);
		fourthEmailId(flag);
		fourthEmailIdConstraints(flag);
		fifthEmailId(flag);
		fifthEmailIdConstraints(flag);
		logger.debug("<<<<<<<<<<<<< completed clearAllEmailConstraints ");
	}//clearAllEmailConstraints
	
	/**
	 * This method validates all the fields.
	 * @return
	 */
	public boolean validateFeilds() {
		logger.debug(">>>>>>>>>>>>> entered in validateFeilds");
		try {
			// Validation of list box
			if (!validateAccountId(orgListBxId)) {
				return false;
			}
			//Validation of User's ListBox
			if(!validateSelectedUser(usersListBxId)){
				return false;
			}
			//Validation of optSync Key
			if(!validateOptSyncKey(optSynAuthKeyTbId.getValue().trim())){
				return false;
			}
			//Validation of optSync name
			if(!validateOptSyncPluginName(optSyncPluginTbId.getValue().trim())){
				return false;
			}
			
			//Validation of OptSync plugIn id.
			if (!validatePluginId(pluginTbId)) {
				return false;
			}

			//Validation of email id
			if(!validateEmailIds()){
				return false;
			}

		} catch (Exception exception) {
			logger.error("Exception ::", exception);
			return false;
		}
		logger.debug("<<<<<<<<<<<<< completed validateFeilds ");
		return true;
	}//validateFeilds


	/**
	 * This method validate's the EmailId
	 * @return boolean
	 */
	private boolean validateEmailIds() {
		logger.debug(">>>>>>>>>>>>> entered in validateEmailIds");
		logger.info("\nSecond Email :: "+isemailAddress2textbox+"\nThird Email :: "+isemailAddress3textbox+"\nFourth Email :: "+isemailAddress4textbox+"\nFifth Email :: "+isemailAddress4textbox);
		//First Email Validation
		logger.info("Validating first Email"+emailIdTbId.getValue());
		if(! validateEmail(emailIdTbId.getValue(),"first")){
			return false;
		}
		//Second Email Validation
		if(isemailAddress2textbox){
			logger.info("Validating Second Email"+emailAddress2TbId.getValue());
			if(!(validateEmail(emailAddress2TbId.getValue(),"second"))){
				return false;
			}
		}
		//Third Email Validation
		if(isemailAddress3textbox){
			logger.info("Validating third Email"+emailAddress2TbId.getValue());
			if(!( validateEmail(emailAddress3TbId.getValue(),"third"))){
				return false;
			}
		}
		//Fourth Email Validation
		if(isemailAddress4textbox){
			logger.info("Validating fourth Email"+emailAddress2TbId.getValue());
			if(!(validateEmail(emailAddress4TbId.getValue(),"fourth"))){
				return false;
			}
		}
		//fifth Email Validation
		if(isemailAddress5textbox){
			logger.info("Validating fifth Email"+emailAddress2TbId.getValue());
			if(!( validateEmail(emailAddress5TbId.getValue(),"fifth"))){
				return false;
			}
		}
		return true;
	}//validateEmailIds

	private boolean validateEmail(String emailStr, String whichEmail) {
		logger.debug(">>>>>>>>>>>>> entered in validateEmail");
		emailStr = emailStr.trim();
		boolean flag = true;
		logger.info(emailStr+"whichEmailwhichEmail ::"+whichEmail);
		if (Constants.STRING_NILL.equals(emailStr)) {
			MessageUtil.setMessage("Please provide "+whichEmail+" email address.", "color:red",	"TOP");
			flag = false;
		}

		if (emailStr.length() > 0 && !Utility.validateEmail(emailStr)) {
			// submitBtnId.setDisabled(true);
			MessageUtil.setMessage("Please enter valid "+whichEmail+" email address","color:red", "TOP");
			flag = false;
		}
		logger.debug("<<<<<<<<<<<<< completed validateEmail ");
		return flag;
	}//validateEmail

	/**
	 * This method validates optSyncPluginName
	 * @param optSyncPluginName
	 * @return true/false
	 */
	private boolean validateOptSyncPluginName(String optSyncPluginName) {
		logger.debug(">>>>>>>>>>>>> entered in validateOptSyncPluginName");
		if (Constants.STRING_NILL.equals(optSyncPluginName)) {
			MessageUtil.setMessage("Please provide optsync plug-in name.","color:red", "TOP");
			logger.debug("<<<<<<<<<<<<< completed validateOptSyncPluginName ");
			return false;
		}

		if (optSyncPluginName.length() > 0 && !Utility.validateName(optSyncPluginName)) {
			MessageUtil.setMessage("Please enter Valid OptSync Plug-in Name.",
					"color:red", "TOP");
			logger.debug("<<<<<<<<<<<<< completed validateOptSyncPluginName ");
			return false;
		}
		logger.debug("<<<<<<<<<<<<< completed validateOptSyncPluginName ");
		return true;
	}//validateOptSyncPluginName

	/**
	 * This method validates the optSyncAuthentication Key
	 * @param optSyncAuth
	 * @return flag
	 */
	/*public boolean validateOptSyncKey(String optSyncAuth ){
		logger.debug(">>>>>>>>>>>>> entered in validateOptSyncKey");
		boolean isTrue =false;
		try{
			if (optSyncAuth.trim().equals("") ||  optSyncAuth.length() < 16 ||  optSyncAuth.length() > 16) {
				MessageUtil.setMessage(	"Please provide optsync authentication key.","color:red", "TOP");
				isTrue =  false;
				return isTrue;
			}
			else if (optSyncAuth.length() == 0 && !Utility.validateName(optSyncAuth)) {
				MessageUtil.setMessage("Please enter valid 16 character optsync authentication key.","color:red", "TOP");
				isTrue=  false;
				return isTrue;
			}
			else if(optSyncAuth.length() == 16) {
				
				if (!validateAccountId(orgListBxId)) {
					return false;
				}

				UserOrganization organization =(UserOrganization)orgListBxId.getSelectedItem().getValue();
				if(organization == null) {
					logger.error("User org not existed ... and returning");
					isTrue =  false;
					return isTrue;
				}

				OptSyncService optSyncService =  new OptSyncService();
				List<UserOrganization> userOrgList = optSyncService.getAllUserOrganizationByOptSyncKey(optSyncAuth);
				boolean existOptSynKey = false;
				
				if(userOrgList == null || userOrgList.isEmpty()){
					logger.info("usr org list is::"+userOrgList);
					isTrue =  true;
					return isTrue;
				}

				//Comparing for Selected UserOrganization with found OrganizationList.
				for (UserOrganization eachOrg : userOrgList) {
					logger.info("Each Org ::"+eachOrg.getUserOrgId()+"\t selected ::"+organization.getUserOrgId());
					if(eachOrg.getUserOrgId() ==  organization.getUserOrgId()) {
						existOptSynKey = true;
					}
				}	

				if(!existOptSynKey) {
					logger.error("OptSyncKey already exists, please provide another optsync key.");
					MessageUtil.setMessage("OptSync authentication key already exist. Please enter another optsynckey.","color:red", "TOP");
					generateKeyAnchId.setVisible(true);
					optSynAuthKeyTbId.setReadonly(false);
					isTrue=  false;
					return isTrue;
				}
			}
			isTrue=  true;
		}
		catch(Exception e){
			logger.error("Exception ::::::::::::" ,e);
			isTrue =  false;
		}
		logger.debug("<<<<<<<<<<<<< completed validateOptSyncKey ");
		return isTrue;
	}//validateOptSyncKey
*/
public boolean validateOptSyncKey(String optSyncAuth ){
		
		logger.info("validating optsync key");
		boolean isTrue =false;
		UpdateOptSyncDataDao updateOptSyncDataDao = null;
		try{
			if (optSyncAuth.trim().equals("") ||  optSyncAuth.length() < 16) {

				MessageUtil.setMessage(	"Please provide OptSync Authentication Key.","color:red", "TOP");
				isTrue =  false;
				return isTrue;
			}

			if (optSyncAuth.length() == 0 && !Utility.validateName(optSyncAuth)) {
				MessageUtil.setMessage("Please enter Valid 16 Character OptSync Authentication Key.","color:red", "TOP");
				isTrue=  false;
				return isTrue;
			}
			
			
			if(optSyncAuth.length() == 16) {
				if (!validateAccountId(orgListBxId)) {
					return false;
				}
				
				updateOptSyncDataDao =  (UpdateOptSyncDataDao) ServiceLocator.getInstance().getDAOByName(OCConstants.UPDATE_OPTSYNC_DATA_DAO);
				
				String orgExternalId=orgListBxId.getSelectedItem().getLabel();
				long userOrgId=(((UserOrganization)orgListBxId.getSelectedItem().getValue()).getUserOrgId().longValue());;
				
				List<UserOrganization> orgList = (List<UserOrganization>)updateOptSyncDataDao.findOptSynKey(userOrgId);
				if(orgList == null) {
					logger.error("User org not existed ... and returning");
					isTrue =  false;
					return isTrue;
				}
				
				List<UserOrganization> userOrgList = updateOptSyncDataDao.findAllByOptSyncName(optSyncAuth);
				//int size =0;
				if(userOrgList == null || userOrgList.isEmpty()){
					logger.info("usr org list is::"+userOrgList);
					isTrue =  true;
					return isTrue;
				}
				
				boolean existOptSynKey = false;
				
				for (UserOrganization eachOrg : userOrgList) {
					if(eachOrg.getUserOrgId() ==  orgList.get(0).getUserOrgId().longValue()) {
						existOptSynKey = true;
					}
				}	
				
				if(!existOptSynKey) {
					logger.error("OptSyncKey already exists, please provide another optsync key");
					MessageUtil.setMessage("OptSync Authentication Key already exist. Please enter another OptSyncKey.","color:red", "TOP");
					generateKeyAnchId.setVisible(true);
					optSynAuthKeyTbId.setReadonly(false);
					isTrue=  false;
					return isTrue;
				}
			}
			isTrue=  true;
		 }
		 catch(Exception e){
			 
			 logger.error("Exception ::::::::::::" ,e);
			 isTrue =  false;
		 }
			return isTrue;

		
	}

	/**
	 * This method validate the plugIn id.
	 * @param txtbox
	 * @return true/false
	 */
	public boolean validatePluginId(Textbox textBox) {
		logger.debug(">>>>>>>>>>>>> entered in validatePluginId");
		long optSyncPluginId = 0L;
		try {
			if(textBox.getValue().trim() == null){
				MessageUtil.setMessage("Please Enter Plug-in ID.","color:red", "TOP");
				return false;
			}

			if (textBox.isValid()) {
				optSyncPluginId = Long.parseLong(textBox.getValue().trim());
				if (optSyncPluginId <= 0) {
					MessageUtil.setMessage("Please enter plug-in ID.","color:red", "TOP");
					return false;
				}// if
				String checkSize = optSyncPluginId +Constants.STRING_NILL;
				if(checkSize.length() != 16 ){
					MessageUtil.setMessage("Please provide valid plug-in ID with 16 digit.","color:red", "TOP");
					logger.error("Not a 16 digit number"+ optSyncPluginId);
					return false;
				}
			}//is valid textBox
			
			OptSyncService optSyncService = new OptSyncService();
			List<UpdateOptSyncData>	optSyncList =optSyncService.getAllOptSyncPluginByPluginId(optSyncPluginId);

			if(optSyncList != null  ){
				if(sessionUpdateOptSyncData != null){
					if(sessionUpdateOptSyncData.getId().equals(optSyncList.get(0).getId())){
						logger.info("Editing plugIn id ::"+ sessionUpdateOptSyncData.getOptSyncId()+"In DB Existing plugIn id"+optSyncList.get(0).getOptSyncId());
						return true;
					}
				}
				MessageUtil.setMessage(	"Entered plug-in ID already exists. Please enter another plug-in ID. ","color:red;");
				generateAnchId.setVisible(true);
				pluginTbId.setReadonly(false);
				logger.error("DataIntegrityVoilation 16 digit number non unique number "+ optSyncPluginId);
				return false;
			}
			return true;
		} catch(NumberFormatException nfe){
			MessageUtil.setMessage(	"Please enter valid 16 digit plug-in ID. ","color:red;");
			return false;
		}
		catch (Exception e) {
			logger.error("Exception  ::", e);
			return false;
		}

	}// validateNum(-)


	/**
	 * This method validates Organization listBox is selected
	 * @param orgListBxId1
	 * @return true/false
	 */
	public boolean validateAccountId(Listbox orgListBoxId ) {
		logger.debug(">>>>>>>>>>>>> entered in validateAccountId");

		if (orgListBoxId.getSelectedIndex() == 0 && orgListBoxId.getSelectedItem().getValue() == null) {
			MessageUtil.setMessage("Please select user organization id.","color:red;");
			return false;
		}	
		if (orgListBoxId.getSelectedIndex() == 0 && (orgListBoxId.getSelectedItem().getValue().equals(allStr))) {
			MessageUtil.setMessage("Please select user organization id.","color:red;");
			return false;
		}
		long orgId =(((UserOrganization)orgListBoxId.getSelectedItem().getValue()).getUserOrgId().longValue());

		try {
			OptSyncService optSyncService = new OptSyncService();
			UserOrganization userOrganization = optSyncService.findByOrgId(orgId);
			logger.info("selected account id unique across the organization  ");
			if(userOrganization == null){
				MessageUtil.setMessage("Selected  user organization id does not exist. Please select another user organization id.","color:red;");
				return false;
			}
			return true;

		} catch (Exception e) {
			logger.info("Exception : ", e);
			return false;
		}

	}// /validate account id

	/**
	 * This method help's to validate a user is selected or not.
	 * @param userListBxId1
	 * @return  true/false
	 */
	public boolean validateSelectedUser(Listbox userListBxId1 ) {
		logger.debug(">>>>>>>>>>>>> entered in validateSelectedUser");
		if(userListBxId1 == null){
			logger.error("Organization with No User...........");
			return false;
		}

		if(userListBxId1 == null || userListBxId1.getSelectedItem() == null ){
			MessageUtil.setMessage("For the selected user organization no users existed.","color:red;");
			return false;
		}
		
		if (userListBxId1.getSelectedIndex() == 0 && userListBxId1.getSelectedItem().getValue() == null) {
			MessageUtil.setMessage("Please select user name.","color:red;");
			return false;
		}

		if (userListBxId1.getSelectedIndex() == 0 && (userListBxId1.getSelectedItem().getValue().equals(selectStr))) {
			MessageUtil.setMessage("Please select user name.","color:red;");
			return false;
		}
		Users users = ((Users)userListBxId1.getSelectedItem().getValue());
		if(users == null){
			//Special Case :: Found and resolved problem in CreateUserController
			MessageUtil.setMessage("For the selected user organization no users existed.","color:red;");
			return false;
		}
		return true;
	}// /validate account id
	
	private void deleteToolBarButton(boolean flag ) {
		logger.debug(">>>>>>>>>>>>> entered in deleteToolBarButton");
		deleteEmailTBId1.setVisible(flag);
		deleteEmailTBId2.setVisible(flag);
		deleteEmailTBId3.setVisible(flag);
		deleteEmailTBId4.setVisible(flag);
		deleteEmailTBId5.setVisible(flag);
		logger.debug("<<<<<<<<<<<<< completed deleteToolBarButton ");
	}



	//Second
	public void onClick$addOneMoreEmailTBId1() {
		logger.debug(">>>>>>>>>>>>> entered in onClick$addOneMoreEmailTBId1");
		//disable option to add second email again & enable option to delete it
		addOneMoreEmailTBId1.setVisible(false);
		deleteEmailTBId1.setVisible(true);

		//Adding second email address
		secondEmailId(true);
		//Constraints for second email address
		secondEmailIdConstraints(true);
		//Set Label's for Email Address
		setEmailAddressLabels();
		//Give option to add 3rd email id
		addOneMoreEmailTBId2.setVisible(true);
		logger.debug("<<<<<<<<<<<<< completed onClick$addOneMoreEmailTBId1 ");
	} //onClick$addOneMoreEmailTBId1


	//Third
	public void onClick$addOneMoreEmailTBId2(){
		logger.debug(">>>>>>>>>>>>> entered in onClick$addOneMoreEmailTBId2");
		//disable option to add third email again & enable option to delete it
		addOneMoreEmailTBId2.setVisible(false);
		deleteEmailTBId3.setVisible(true);
		//Adding third email
		thirdEmailId(true);
		//Constraints for third email address
		thirdEmailIdConstraints(true);
		//Enable option to add fourth email address
		addOneMoreEmailTBId3.setVisible(true);
		logger.debug("<<<<<<<<<<<<< completed onClick$addOneMoreEmailTBId2 ");
	}//onClick$addOneMoreEmailTBId2 

	//Fourth
	public void onClick$addOneMoreEmailTBId3(){
		logger.debug(">>>>>>>>>>>>> entered in onClick$addOneMoreEmailTBId3");
		setEmailAddressLabels();
		addOneMoreEmailTBId3.setVisible(false);
		deleteEmailTBId4.setVisible(true);
		//Adding Fourth Email
		fourthEmailId(true);
		//Constraints for fourth email address
		fourthEmailIdConstraints(true);
		//Enable option to add fifth email address.
		addOneMoreEmailTBId4.setVisible(true);
		logger.debug("<<<<<<<<<<<<< completed onClick$addOneMoreEmailTBId3 ");
	}//onClick$addOneMoreEmailTBId3

	//Fifth
	public void onClick$addOneMoreEmailTBId4(){
		addOneMoreEmailTBId4.setVisible(false);
		deleteEmailTBId5.setVisible(false);
		//Adding fifth Email
		fifthEmailId(true);
		//Constraints for fifth Email
		fifthEmailIdConstraints(true);
	}//added fifth email

	/**
	 * This method set's the email address label's
	 */
	private void setEmailAddressLabels() {
		logger.debug(">>>>>>>>>>>>> entered in setEmailAddressLabel");
		emailAddress2Label.setValue("Email Address 2:");
		emailAddress3Label.setValue("Email Address 3:");
		emailAddress4Label.setValue("Email Address 4:");
		emailAddress5Label.setValue("Email Address 5:");
		logger.debug("<<<<<<<<<<<<< completed setEmailAddressLabel ");
	}//setEmailAddressLabel

	/**
	 * This method handle onClick event on  first email id
	 */
	public void onClick$deleteEmailTBId1(){
		logger.debug(">>>>>>>>>>>>> entered in onClick$deleteEmailTBId1");
		//Set Labels
		setEmailAddressLabels();
		if(!isemailAddress5textbox && !isemailAddress4textbox && !isemailAddress3textbox && isemailAddress2textbox){
			logger.info("Setting Second Eamil to First");
			emailIdTbId.setValue(emailAddress2TbId.getValue().trim());
			deleteEmailTBId1.setVisible(false);
			addOneMoreEmailTBId2.setVisible(false);
			secondEmailId(false);
			thirdEmailId(false);
			fourthEmailId(false);
			fifthEmailId(false);
			//Add 2nd email id
			addOneMoreEmailTBId1.setVisible(true);
		}
		else if(!isemailAddress5textbox && !isemailAddress4textbox && isemailAddress3textbox && isemailAddress2textbox){
			logger.info("Setting Second Email TextBox value to First Email TextBox and Third Email TextBox value  to Second Email TextBox ");
			emailIdTbId.setValue(emailAddress2TbId.getValue().trim());
			emailAddress2TbId.setValue(emailAddress3TbId.getValue().trim());
			emailAddress2Label.setValue("Email Address 2:");
			secondEmailId(true);
			thirdEmailId(false);
			fourthEmailId(false);
			fifthEmailId(false);
			//Add third email id
			addOneMoreEmailTBId2.setVisible(true);
		}
		else if(!isemailAddress5textbox && isemailAddress4textbox && isemailAddress3textbox && isemailAddress2textbox){	
			logger.info("Setting Second Email TextBox value to First Email TextBox , Third Email TextBox value  to Second Email TextBox and  fourth Email TextBox value  to third Email TextBox  ");
			emailIdTbId.setValue(emailAddress2TbId.getValue().trim());
			emailAddress2TbId.setValue(emailAddress3TbId.getValue().trim());
			emailAddress3TbId.setValue(emailAddress4TbId.getValue().trim());
			secondEmailId(true);
			thirdEmailId(true);
			fourthEmailId(false);
			fifthEmailId(false);
			setEmailAddressLabels();
			//Add fourth Email id
			addOneMoreEmailTBId3.setVisible(true);
		}
		else if(isemailAddress5textbox && isemailAddress4textbox && isemailAddress3textbox && isemailAddress2textbox){
			emailIdTbId.setValue(emailAddress2TbId.getValue().trim());
			emailAddress2TbId.setValue(emailAddress3TbId.getValue().trim());
			emailAddress3TbId.setValue(emailAddress4TbId.getValue().trim());
			emailAddress4TbId.setValue(emailAddress5TbId.getValue().trim());
			fifthEmailId(false);
			setEmailAddressLabels();
			//Add fifth email id
			addOneMoreEmailTBId4.setVisible(true);
		}	
		logger.debug("<<<<<<<<<<<<< completed onClick$deleteEmailTBId1 ");
	}// onClick$deleteEmailTBId1

	/**
	 * This method handle's onClick Event on Second Email id.
	 */
	public void onClick$deleteEmailTBId2(){
		logger.debug(">>>>>>>>>>>>> entered in onClick$deleteEmailTBId2");
		secondEmailId(false);
		//Set Labels
		setEmailAddressLabels();
		//deleteEmailTBId1.setVisible(true);
		logger.info("\nSecond Email :: "+isemailAddress2textbox+"\nThird Email :: "+isemailAddress3textbox+"\nFourth Email :: "+isemailAddress4textbox+"\nFifth Email :: "+isemailAddress4textbox);
		if(!isemailAddress5textbox && !isemailAddress4textbox && !isemailAddress3textbox){
			addOneMoreEmailTBId2.setVisible(false);
			addOneMoreEmailTBId1.setVisible(true);
			deleteEmailTBId1.setVisible(false);
		}
		else if(!isemailAddress5textbox && !isemailAddress4textbox && isemailAddress3textbox){
			logger.info("Setting Third Email TextBox Value to second email.");
			emailAddress2TbId.setValue(emailAddress3TbId.getValue().trim());
			addOneMoreEmailTBId3.setVisible(false);
			secondEmailId(true);
			thirdEmailId(false);
			fourthEmailId(false);
			fifthEmailId(false);
			//Add Third Email id
			addOneMoreEmailTBId2.setVisible(true);
		}
		else if(!isemailAddress5textbox && isemailAddress4textbox && isemailAddress3textbox){
			logger.info("Setting fourth email textBox value to third email,Third Email TextBox Value to second email.");
			emailAddress2TbId.setValue(emailAddress3TbId.getValue().trim());
			emailAddress3TbId.setValue(emailAddress4TbId.getValue().trim());
			addOneMoreEmailTBId4.setVisible(false);

			secondEmailId(true);
			thirdEmailId(true);
			fourthEmailId(false);
			fifthEmailId(false);
			//Set label
			setEmailAddressLabels();
			addOneMoreEmailTBId3.setVisible(true);
		}
		else if(isemailAddress5textbox && isemailAddress4textbox && isemailAddress3textbox){
			logger.info("Setting fifth Eamil textbox value to fourth,fourth email textBox value to third and third email textbox value to second");
			emailAddress2TbId.setValue(emailAddress3TbId.getValue().trim());
			emailAddress3TbId.setValue(emailAddress4TbId.getValue().trim());
			emailAddress4TbId.setValue(emailAddress5TbId.getValue().trim());
			secondEmailId(true);
			thirdEmailId(true);
			fourthEmailId(true);
			fifthEmailId(false);
			setEmailAddressLabels();
			addOneMoreEmailTBId4.setVisible(true);
		}	
		logger.debug("<<<<<<<<<<<<< completed onClick$deleteEmailTBId2 ");
	}//deleting second email id

	//Deleting Third email id	
	public void onClick$deleteEmailTBId3(){
		logger.debug(">>>>>>>>>>>>> entered in onClick$deleteEmailTBId3");
		thirdEmailId(false);
		setEmailAddressLabels();

		if(!isemailAddress5textbox && !isemailAddress4textbox ){
			addOneMoreEmailTBId3.setVisible(false);
			addOneMoreEmailTBId2.setVisible(true);
		}
		else if(!isemailAddress5textbox && isemailAddress4textbox ){
			logger.info("deleting fourth email id");
			addOneMoreEmailTBId1.setVisible(false);
			addOneMoreEmailTBId2.setVisible(false);
			emailAddress3TbId.setValue(emailAddress4TbId.getValue().trim());
			thirdEmailId(true);
			fourthEmailId(false);
			fifthEmailId(false);
			addOneMoreEmailTBId3.setVisible(true);
		}
		else if(isemailAddress5textbox && isemailAddress4textbox ){
			logger.info("deleting fourth email id");
			emailAddress3TbId.setValue(emailAddress4TbId.getValue().trim());
			emailAddress4TbId.setValue(emailAddress5TbId.getValue().trim());
			thirdEmailId(true);
			fourthEmailId(true);
			fifthEmailId(false);
			addOneMoreEmailTBId4.setVisible(false);
		}
		logger.debug("<<<<<<<<<<<<< completed onClick$deleteEmailTBId3 ");
	}//deleting third0 email id

	//Deleting fourth email id	
	public void onClick$deleteEmailTBId4(){
		logger.debug(">>>>>>>>>>>>> entered in onClick$deleteEmailTBId4");
		setEmailAddressLabels();
		if(isemailAddress5textbox && isemailAddress4textbox ){
			logger.info("deleting fifth email id");
			emailAddress4TbId.setValue(emailAddress5TbId.getValue().trim());
			fourthEmailId(true);
			fifthEmailId(false);
			addOneMoreEmailTBId4.setVisible(true); 
		}
		else if(!isemailAddress5textbox && isemailAddress4textbox ){
			logger.info("deleting fourth email id");
			emailAddress4TbId.setValue(emailAddress5TbId.getValue().trim());
			fourthEmailId(false);
			addOneMoreEmailTBId3.setVisible(true);
			addOneMoreEmailTBId4.setVisible(false); 
		}
		logger.debug("<<<<<<<<<<<<< completed onClick$deleteEmailTBId4 ");
	}//deleting third0 email id

	//Deleting fifth email id	
	public void onClick$deleteEmailTBId5(){
		logger.debug(">>>>>>>>>>>>> entered in onClick$deleteEmailTBId5");
		fifthEmailId(false);
		addOneMoreEmailTBId4.setVisible(true);
		logger.debug("<<<<<<<<<<<<< completed onClick$deleteEmailTBId5 ");
	}//onClick$deleteEmailTBId5


	//Added for 2.3.9
	public void onClick$submitBtnId$opySyncAlertsWinId(){

		try {
			String onAlertsBy=Constants.ALERTS_SENDING_MANUALLY;
			if(opySyncAlertsWinId$automaticRgId.isChecked()){
				onAlertsBy =Constants.ALERTS_SENDING_AUTOMATICALLY;
			}else if(opySyncAlertsWinId$manualRgId.isChecked()){
				onAlertsBy = Constants.ALERTS_SENDING_MANUALLY;
			}
			OptSyncService optSyncService =  new OptSyncService();
			UpdateOptSyncData updateOptSyncData =(UpdateOptSyncData) createWinId$opySyncAlertsWinId.getAttribute(Constants.OPTSYNC_OBJ);

			int alertsCount =optSyncService.updateAlertSendingsStatus(onAlertsBy, updateOptSyncData.getOptSyncId());
			logger.info("alerts count"+alertsCount);

			int rowseffected = optSyncService.updatePluginStatus(OCConstants.OPT_SYNC_PLUGIN_STATUS_INACTIVE, updateOptSyncData.getOptSyncId());
			if(rowseffected == 1){
				UserOrganization userOrganization = null;
				Users users = null;
				if(orgListBxId != null && orgListBxId.getSelectedItem() != null){
					userOrganization = (UserOrganization)orgListBxId.getSelectedItem().getValue();
				}

				if(usersListBxId != null && usersListBxId.getSelectedItem() != null ){
					users =(Users)usersListBxId.getSelectedItem().getValue();
				}
				if(userOrganization ==  null && users ==  null){
					setDefaultSettings();
				}
				else if(userOrganization !=  null && users ==  null){
					doSettings(userOrganization.getOptSyncKey(), null);
				}
				else if(userOrganization ==  null && users !=  null){
					doSettings(null,users.getUserId());
				}
				else if(userOrganization !=  null && users !=  null){
					doSettings(userOrganization.getOptSyncKey(),users.getUserId());
				}
				MessageUtil.setMessage("OptSync alerts paused successfully.","color:blue","TOP");
			
			}
		//	Redirect.goTo(PageListEnum.EMPTY);
		//	Redirect.goTo(PageListEnum.ADMIN_OPT_SYNC_USER);
			opySyncAlertsWinId.setVisible(false);
		} catch (Exception e) {
			logger.error("Exception ::",e);
		}
	}

	public void onClick$cancelBtnId$opySyncAlertsWinId(){
		opySyncAlertsWinId$automaticRgId.setChecked(true);
		opySyncAlertsWinId.setVisible(false);
	}


	//Added for 2.3.11

	

	/**
	 * This method helps to reset Organization list
	 */
	public void onClick$resetOrgListAnchId(){
		logger.debug(">>>>>>> Started  onClick$resetOrgListAnchId :: ");
		newOptSyncPluginGbId.setVisible(false);
		newOptSyncPluginGbIdView.setVisible(false);
		addOptSyncPluginTBId.setVisible(true);
		applySettingsAnchId.setVisible(true);
		resetOrgListAnchId.setVisible(false);
		updateOptSyncDataView =null; 
		setUserOrganization(null);
		
		onSelect$orgListBxId();
		logger.debug("<<<<< Completed onClick$resetOrgListAnchId .");
	}//resetOrgListAnchId

	



	/**
	 * This method populates the UserOrganizationList in the ListBox.
	 * @param orgLbId
	 */
	private void setUserOrganization(Listbox orgLbId) {
		logger.debug(">>>>>>>>>>>>> entered in setUserOrganization");
		Listitem tempItem = null;
		Components.removeAllChildren(orgListBxId);
		OptSyncService optSyncService = new OptSyncService();
		List<UserOrganization> orgList	=optSyncService.findAllOrganizations();
		if(orgList == null) {
			logger.error("no organization list exist from the DB...");
			return ;
		}
		Listitem tempList = new Listitem(allStr);
		tempList.setParent(orgListBxId);
		for (UserOrganization userOrganization : orgList) {
			//set Organization Name
			if(userOrganization.getOrganizationName() == null || userOrganization.getOrganizationName().trim().equals(Constants.STRING_NILL)) continue;
			tempItem = new Listitem(userOrganization.getOrgExternalId().trim(),userOrganization);
			tempItem.setParent(orgListBxId);
		} // for
		orgListBxId.setSelectedIndex(0);
		logger.debug("<<<<<<<<<<<<< completed setUserOrganization ");
	}//setUserOrganization

	/**
	 * This method helps to reset Users list
	 *//*
	public void onClick$resetUsersListAnchId(){
		logger.debug(">>>>>>> Started  onClick$resetUsersListAnchId :: ");
		newOptSyncPluginGbId.setVisible(false);
		newOptSyncPluginGbIdView.setVisible(false);
		addOptSyncPluginTBId.setVisible(true);
		setUsers(orgListBxId);
		onSelect$usersListBxId();
		logger.debug("<<<<< Completed onClick$resetUsersListAnchId .");
	}//resetUsersListAnchId
	*//**
	 * This method helps to reset the user's list
	 * @param object
	 *//*
	private void setUsers(Listbox orgListBxId) {
		logger.debug(">>>>>>> Started  setUsers :: ");

		UserOrganization userOrganization = (UserOrganization)orgListBxId.getSelectedItem().getValue();
		Long userOrgId = userOrganization.getUserOrgId().longValue();
		//Display OptSync PlugIn based on selected Organization.
		//doSettings(userOrganization.getOptSyncKey(), null);

		Components.removeAllChildren(usersListBxId); 
		Listitem tempList = new Listitem(selectStr);
		tempList.setParent(usersListBxId);

		OptSyncService optSyncService = new OptSyncService();
		List<Users> usersList = optSyncService.getAllUsersByOrganizationId(userOrgId);

		if(usersList == null || usersList.size() == 0) {
			logger.debug("No users exists for the Selected Organization..");
			return;
		}
		Listitem listitem = null;
		for (Users users : usersList) {
			String userNameStr = Utility.getOnlyUserName(users.getUserName());
			listitem = new Listitem(userNameStr,users);
			listitem.setParent(usersListBxId);
		} // for

		if(usersListBxId.getItemCount() > 0) {
			usersListBxId.setSelectedIndex(0);
		}
		onSelect$usersListBxId();
		logger.debug("<<<<< Completed setUsers .");
	}//setUsers
*/
	/**
	 * This method help to capture on select event on orgListBox
	 */
	public void onSelect$orgListBxId() {
		logger.debug(">>>>>>>>>>>>> entered in onSelect$orgListBxId");
		logger.info("in on select of orgListBxId" +orgListBxId.getSelectedItem().getValue());
		if(orgListBxId.getSelectedItem().getLabel().equals(allStr) ) {
			itemsPerPageLBId.setVisible(true);
			itemsPerPageLBId1.setVisible(false);
			resetOrgListAnchId.setVisible(false);
			Components.removeAllChildren(usersListBxId); 
			//As No Organization is selected need to display all the OptSync PlugIn's from all the Organization's.
			setDefaultSettings();
			visibleHtmlElements(false);
			enableOptFlagLbId.setVisible(false);
			enableOptSyncChkbxId.setVisible(false);
			applySettingsAnchId.setVisible(false);
			return;
		}
		else{
			resetOrgListAnchId.setVisible(true);
		}

		UserOrganization userOrganization = (UserOrganization)orgListBxId.getSelectedItem().getValue();
		Long userOrgId = userOrganization.getUserOrgId().longValue();
		//Display OptSync PlugIn based on selected Organization.
		doSettings(userOrganization.getOptSyncKey(), null);

		Components.removeAllChildren(usersListBxId); 
		Listitem tempList = new Listitem(selectStr);
		tempList.setParent(usersListBxId);

		OptSyncService optSyncService = new OptSyncService();
		List<Users> usersList = optSyncService.getAllUsersByOrganizationId(userOrgId);

		if(usersList == null || usersList.size() == 0) {
			userNameStarLbId.setVisible(true);
			userNameLbId.setVisible(true);
			usersListBxId.setVisible(true);
			optSyncAuthStarLbId.setVisible(true);
			optSyncAuthLbId.setVisible(true);
			optSynAuthKeyTbId.setVisible(true);
			if(userOrganization.getOptSyncKey() != null && !(Constants.STRING_NILL.equals(userOrganization.getOptSyncKey())) ){
				optSynAuthKeyTbId.setReadonly(true);
				generateKeyAnchId.setVisible(false);
			}else{
				generateKeyAnchId.setVisible(true);
			}
			enableOptFlagLbId.setVisible(false);
			enableOptSyncChkbxId.setVisible(false);
			applySettingsAnchId.setVisible(false);
			logger.debug("No users exists for the Selected Organization..");
			return;
		}
		Listitem listitem = null;
		for (Users users : usersList) {
			String userNameStr = Utility.getOnlyUserName(users.getUserName());
			listitem = new Listitem(userNameStr,users);
			listitem.setParent(usersListBxId);
		} // for

		if(usersListBxId.getItemCount() > 0) {
			usersListBxId.setSelectedIndex(0);
		}

		userNameStarLbId.setVisible(true);
		userNameLbId.setVisible(true);
		usersListBxId.setVisible(true);
		optSyncAuthStarLbId.setVisible(true);
		optSyncAuthLbId.setVisible(true);
		optSynAuthKeyTbId.setVisible(true);
		if(userOrganization.getOptSyncKey() != null && !(Constants.STRING_NILL.equals(userOrganization.getOptSyncKey())) ){
			optSynAuthKeyTbId.setReadonly(true);
			generateKeyAnchId.setVisible(false);
		}else{
			generateKeyAnchId.setVisible(true);
		}
		enableOptFlagLbId.setVisible(false);
		enableOptSyncChkbxId.setVisible(false);
		applySettingsAnchId.setVisible(false);
		logger.debug("<<<<<<<<<<<<< completed onSelect$orgListBxId ");
	}//onSelect$orgListBxId

	/**
	 * This method help's to capture the on select event 
	 */
	public void onSelect$usersListBxId() {
		logger.debug(">>>>>>>>>>>>> entered in onSelect$usersListBxId");
		enableOptFlagLbId.setVisible(false);
		enableOptSyncChkbxId.setVisible(false);
		applySettingsAnchId.setVisible(false);
		UserOrganization userOrganization = (UserOrganization)orgListBxId.getSelectedItem().getValue();
		Users users = (Users)usersListBxId.getSelectedItem().getValue();
		if(userOrganization == null && users == null){
			return;
		}
		enableOptFlagLbId.setVisible(true);
		enableOptSyncChkbxId.setVisible(true);
		applySettingsAnchId.setVisible(true);
		enableOptSyncChkbxId.setChecked(false);
		if(userOrganization.getOptSyncKey() != null && !(Constants.STRING_NILL.equals(userOrganization.getOptSyncKey())) ){
			optSynAuthKeyTbId.setReadonly(true);
			generateKeyAnchId.setVisible(false);
			itemsPerPageLBId.setVisible(false);
			itemsPerPageLBId1.setVisible(true);
			if(users == null){
				doSettings(userOrganization.getOptSyncKey().toString(), null);
			}
			else
				doSettings(userOrganization.getOptSyncKey().toString(), users.getUserId());
		}
		else{
			optSynAuthKeyTbId.setValue(Constants.STRING_NILL);
			optSynAuthKeyTbId.setReadonly(false);
			generateKeyAnchId.setVisible(true);
		}
		
		if(users != null){
		setMonitoringFlag(users.getUserId());
		}
		else{
			return;
		}
		
	}//onSelect$usersListBxId


	/**
	 * This method handle's on click event of cancel button.
	 */
	public void onClick$cancelBtnId(){
		logger.debug(">>>>>>>>>>>>> entered in onClick$cancelBtnId");
		/*Redirect.goTo(PageListEnum.EMPTY);
		Redirect.goTo(PageListEnum.ADMIN_OPT_SYNC_USER);*/
		//Add 2.3.11
		newOptSyncPluginGbId.setVisible(false);
		clearGroupBoxes(false);
		addOptSyncPluginTBId.setVisible(true);
		newOptSyncPluginGbIdView.setVisible(false);
		logger.debug("<<<<<<<<<<<<< completed onClick$cancelBtnId ");
	}//cancelBtnId
}//Eof
