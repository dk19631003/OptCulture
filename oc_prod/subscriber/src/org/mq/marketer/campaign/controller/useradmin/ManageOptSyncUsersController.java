
package org.mq.marketer.campaign.controller.useradmin;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.UpdateOptSyncData;
import org.mq.marketer.campaign.beans.UserOrganization;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.dao.UpdateOptSyncDataDao;
import org.mq.marketer.campaign.dao.UpdateOptSyncDataDaoForDML;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.dao.UsersDaoForDML;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.LBFilterEventListener;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PageListEnum;
import org.mq.marketer.campaign.general.PageUtil;
import org.mq.marketer.campaign.general.Redirect;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.utils.OCConstants;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.A;
import org.zkoss.zul.Button;
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

public class ManageOptSyncUsersController extends GenericForwardComposer
		implements EventListener, ListitemRenderer<UpdateOptSyncData> {

	//class level variable declaration
	private UpdateOptSyncDataDao updateOptSyncDataDao;
	private UpdateOptSyncDataDaoForDML updateOptSyncDataDaoForDML;
	private UsersDao usersDao=null;
	private UsersDaoForDML usersDaoForDML=null;
	private Label emailAddress2LabelView,emailAddress3LabelView,emailAddress4LabelView,emailAddress5LabelView;
	
	private Textbox optSynAuthKeyTbId, optSyncPluginTbId, pluginTbId,
			emailIdTbId,emailAddress2TbId,emailAddress3TbId,emailAddress4TbId,emailAddress5TbId,optSyncPluginTbIdView,pluginTbIdView,emailIdTbIdView,emailAddress2TbIdView ,emailAddress3TbIdView,emailAddress4TbIdView,emailAddress5TbIdView;;
	private Toolbarbutton addOptSyncPluginTBId,addOneMoreEmailTBId1,addOneMoreEmailTBId2,addOneMoreEmailTBId3,addOneMoreEmailTBId4,deleteEmailTBId1,deleteEmailTBId2,deleteEmailTBId3,deleteEmailTBId4,deleteEmailTBId5;
	private Groupbox newOptSyncPluginGbId,newOptSyncPluginGbIdView;
	private Label emailAddress2Label,emailAddress3Label,emailAddress4Label,emailAddress5Label;//,emailAddress2star,emailAddress3star,emailAddress4star,emailAddress5star;
	private Button saveBtnId,resetBtnId;  
	private boolean isemailAddress2textbox=false,isemailAddress3textbox=false,isemailAddress4textbox=false,isemailAddress5textbox=false;
	private Listbox userOrgLbId,optSyncLBId,itemsPerPageLBId;
	private Paging optSyncPagingId;
	private LBFilterEventListener  viewUDateOptSynObj;
	private String query =null;
	private String countQuery=null; 
	private Window createWinId;
	private String type;
	private UpdateOptSyncData sessionUpdateOptSyncData;
	private static final Logger logger = LogManager	.getLogger(Constants.SUBSCRIBER_LOGGER);
	private static String allStr = "--All--";
	private Users currentUser;
	

	// Added after 2.3.9
	private Window createWinId$opySyncAlertsWinId , opySyncAlertsWinId;
	private Radio opySyncAlertsWinId$automaticRgId, opySyncAlertsWinId$manualRgId;


	public ManageOptSyncUsersController() {

		String style = "font-weight:bold;font-size:15px;color:#313031;font-family:Arial,Helvetica,sans-serif;align:left";
		
		PageUtil.setHeader("Manage OptSync ", "", style, true);

		updateOptSyncDataDao = (UpdateOptSyncDataDao) SpringUtil.getBean("updateOptSyncDataDao");
		updateOptSyncDataDaoForDML = (UpdateOptSyncDataDaoForDML) SpringUtil.getBean("updateOptSyncDataDaoForDML");

		usersDao = (UsersDao) SpringUtil.getBean("usersDao");
		usersDaoForDML = (UsersDaoForDML) SpringUtil.getBean("usersDaoForDML");
	}//constructor

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		// TODO Auto-generated method stub
		super.doAfterCompose(comp);
		logger.info("Current User UserId :: "+GetUser.getUserId());
		type = (String) session.removeAttribute("UpdateOptSyncDataType");
		sessionUpdateOptSyncData = (UpdateOptSyncData) session.removeAttribute("UpdateOptSyncData");
		currentUser = GetUser.getUserObj();

		setOptSyncAuthKey();

	}// doAfterCompose
	
	
	List<UserOrganization> currentUserOrgList=null;
	List<UpdateOptSyncData> optSyncDataList = null;
	
	private void setOptSyncAuthKey() {

		UserOrganization userOrgObj = GetUser.getUserObj().getUserOrganization();// .setOptSyncKey(optSyncAuthkey);
		
		
		if (userOrgObj != null) {
			currentUserOrgList= (List<UserOrganization>)updateOptSyncDataDao.findOptSynKey(userOrgObj.getUserOrgId());
			if(currentUserOrgList != null){
				logger.info("Setting optsync authetication key ::"+currentUserOrgList.get(0).getOptSyncKey() +" size of LIst"+currentUserOrgList.size());
				if(currentUserOrgList.get(0).getOptSyncKey() != null && !currentUserOrgList.get(0).getOptSyncKey().isEmpty()){ 
				
					optSyncDataList = updateOptSyncDataDao.findAllByUserId(currentUser.getUserId().longValue());
					
					optSynAuthKeyTbId.setReadonly(true);
					optSynAuthKeyTbId.setDisabled(true);
					generateKeyAnchId.setVisible(false);
				}
				else{
					optSynAuthKeyTbId.setReadonly(false);
					optSynAuthKeyTbId.setDisabled(false);
					generateKeyAnchId.setVisible(true);
				}
			
				setDefaultSettings(GetUser.getUserId().longValue());
				optSynAuthKeyTbId.setValue(currentUserOrgList.get(0).getOptSyncKey());
				
			}
			
		}
		else
			logger.error("Unable to set optsync key..................");

	}//setting optsync key
	
	
	public boolean isEnabledMointoring(List<UpdateOptSyncData> list){
		boolean flag =  false;
		
		if(list == null || list.size() == 0)
			return flag;
		
		for (UpdateOptSyncData updateOptSyncData : list) {
			if(OCConstants.Opt_SYNC_ENABLE_MOINTORING.equalsIgnoreCase(updateOptSyncData.getEnabledOptSyncFlag()))
				{
					logger.info("OptSync monitoring is enabled");
					flag=true;
				}
		}
		
		return flag;
		
	}
	
	
	
	//setDefaultSettings
	private void setDefaultSettings(Long userId){
		
		logger.info("in default settings...........");
		
		addOptSyncPluginTBId.setVisible(true);
		try {
			Map<Integer, Field> objMap = new HashMap<Integer, Field>();
			
			objMap.put(0, UpdateOptSyncData.class.getDeclaredField("optSyncName"));
			objMap.put(1, UpdateOptSyncData.class.getDeclaredField("optSyncId"));
			objMap.put(2, UpdateOptSyncData.class.getDeclaredField("emailId"));
			objMap.put(3, UpdateOptSyncData.class.getDeclaredField("status"));
			objMap.put(4, UpdateOptSyncData.class.getDeclaredField("optSyncHitTime"));
			objMap.put(5, UpdateOptSyncData.class.getDeclaredField("enabledOptSyncFlag"));
			if(optSyncLBId != null)
			optSyncLBId.setItemRenderer(this);
			optSyncPagingId.setPageSize(Integer.parseInt(itemsPerPageLBId.getSelectedItem().getLabel()));
			
			optSyncPagingId.setAttribute(LBFilterEventListener.HANDLE_ON_PAGING, "false");
			
			//displaying Optsync plugings based on current userId & org id
			query = " FROM UpdateOptSyncData  where userId =  "+userId+" AND orgId ="+currentUserOrgList.get(0).getUserOrgId();
			countQuery = "SELECT COUNT(*) FROM UpdateOptSyncData  WHERE userId = "+userId +" AND orgId ="+currentUserOrgList.get(0).getUserOrgId();
			viewUDateOptSynObj = LBFilterEventListener.lbFilterSetup(optSyncLBId, optSyncPagingId, query, countQuery, "", objMap);
/*
			//displaying Optsync plugings based on current userId 
			query = " FROM UpdateOptSyncData  where userId = "+userId ;
			countQuery = "SELECT COUNT(*) FROM UpdateOptSyncData  WHERE userId = "+userId ;
			viewUDateOptSynObj = LBFilterEventListener.lbFilterSetup(optSyncLBId, optSyncPagingId, query, countQuery, null, objMap);
*/
			
			
			
			/*//displaying Optsync plugings based on current OrgID 
			if(currentUserOrgList.get(0).getOptSyncKey() == null)return;
			
			query = "SELECT DISTINCT od FROM UpdateOptSyncData od, UserOrganization o "
					+ "WHERE od.orgId=o.userOrgId AND o.optSyncKey='"+currentUserOrgList.get(0).getOptSyncKey()+"'";
			countQuery = "SELECT COUNT(DISTINCT od.id) FROM UpdateOptSyncData od, UserOrganization o "
					+ "WHERE od.orgId=o.userOrgId AND o.optSyncKey='"+currentUserOrgList.get(0).getOptSyncKey()+"'";
			String qryPrefix="od";
			
			viewUDateOptSynObj = LBFilterEventListener.lbFilterSetup(optSyncLBId, optSyncPagingId, query, countQuery, qryPrefix, objMap);
			*/
			Utility.refreshModel(viewUDateOptSynObj, 0, true);
		} catch (WrongValueException e) {
			logger.error("Exception ::",e);
		} catch (NoSuchFieldException e) {
			logger.error("Exception ::",e);
		} catch (SecurityException e) {
			logger.error("Exception ::",e);
		}
	}//setDefaultSettings
	
	
	
	public void onPaging$optSyncPagingId(ForwardEvent event) {
		logger.debug("Paging Event called here ");
		
		final PagingEvent pe = (PagingEvent) event.getOrigin();
		int _startPageNumber = pe.getActivePage();
		
		Utility.refreshModel(viewUDateOptSynObj,  _startPageNumber, false);
	}
	
	public void onSelect$itemsPerPageLBId(){
		setDefaultSettings(GetUser.getUserId());
	}
	
	@Override
public void render(Listitem li, UpdateOptSyncData updateOptSyncData, int arg2) throws Exception {
		
		
		li.setValue(updateOptSyncData);
		//setting optsync name
		li.appendChild(new Listcell(updateOptSyncData.getOptSyncName()));
		//optsync plugin id	
		li.appendChild(new Listcell(updateOptSyncData.getOptSyncId() != null ? updateOptSyncData.getOptSyncId().toString() : "") );
		//setting email id
		li.appendChild(new Listcell(updateOptSyncData.getEmailId() != null ? updateOptSyncData.getEmailId() : ""));
		//setting status
		String status ="";
		
		if(OCConstants.OPT_SYNC_STATUS_NEW.equalsIgnoreCase(updateOptSyncData.getStatus())){
			status = "--";
		}
		else
		if(OCConstants.OPT_SYNC_STATUS_ACTIVE.equalsIgnoreCase(updateOptSyncData.getStatus())){
			status = "UP";
			}
		else
		if(OCConstants.OPT_SYNC_STATUS_DEACTIVE.equalsIgnoreCase(updateOptSyncData.getStatus())){
			status = "Down";
				
			}
		li.appendChild(new Listcell(status));
		String downTime = "";
		if(OCConstants.OPT_SYNC_STATUS_DEACTIVE.equalsIgnoreCase(updateOptSyncData.getStatus())){
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
			}else		
			if(diffDays == 0 && diffHours != 0 ){
				downTime = diffHours + " hours and "+ diffMinutes + " minutes";
			}else			
			if(diffDays != 0  ){
				downTime = diffDays + " days, " + diffHours + " hours and "+ diffMinutes + " minutes";
			}
		
		}//down
		else{
			downTime = "--";
		}
		//setting downtime
		li.appendChild(new Listcell(downTime));
		
		//Actions
		
		Listcell lc = new Listcell();
		Hbox hbox = new Hbox();
		Image img ;
		
			//setting view image
			img= new Image("/img/theme/preview_icon.png");
			img.setStyle("margin-right:5px;cursor:pointer;");
			img.setTooltiptext("View Details");
			img.setAttribute("imageEventName", "view");
			img.addEventListener("onClick",this);
			img.setParent(hbox);
				
			Space space = new Space();
			space.setParent(hbox);
			img = new Image("/img/email_edit.gif");
			//setting edit image
			img.setTooltiptext("Edit");
			img.setStyle("margin-right:5px;cursor:pointer;");
			img.setAttribute("imageEventName", "editList");
			img.addEventListener("onClick", this);
			img.setParent(hbox);
			
			space = new Space();
			space.setParent(hbox);
			
			if(OCConstants.OPT_SYNC_PLUGIN_STATUS_ACTIVE.equalsIgnoreCase(updateOptSyncData.getPluginStatus())){
				img= new Image("/img/pause_icn.png");
				img.setStyle("margin-right:5px;cursor:pointer;");
				img.setTooltiptext("Pause Alerts");
				img.setAttribute("imageEventName", "inactive");
				img.addEventListener("onClick",this);
				img.setParent(hbox);
			}
			else
			if(OCConstants.OPT_SYNC_PLUGIN_STATUS_INACTIVE.equalsIgnoreCase(updateOptSyncData.getPluginStatus())){
				img= new Image("/img/play_icn.png");
				img.setStyle("margin-right:5px;cursor:pointer;");
				img.setTooltiptext("Activate Alerts");
				img.setAttribute("imageEventName", "active");
				img.addEventListener("onClick",this);
				img.setParent(hbox);
			}
			space = new Space();
			space.setParent(hbox);
			
			//setting delete image
			img = new Image("/img/action_delete.gif");
			img.setTooltiptext("Delete");
			img.setStyle("margin-right:3px;cursor:pointer;");
			img.setAttribute("imageEventName", "userDelete");
			img.addEventListener("onClick", this);
			
			img.setParent(hbox);
		
			hbox.setParent(lc);
			lc.setParent(li);
			
			li.setParent( optSyncLBId);
		
	}//render
		

/*	//setting Select Accout Listbox
	private void setUserOrg(Listbox orgLbId) {
	
		List<UserOrganization> orgList	=(List<UserOrganization>) updateOptSyncDataDao.findAllOrganizations();
		
		if(orgList == null) {
			logger.debug("no organization list exist from the DB...");
			return ;
		}
		Listitem tempList = new Listitem(allStr);
		tempList.setParent(orgLbId);
		Listitem tempItem = null;
		
		for (UserOrganization userOrganization : orgList) {
			if(userOrganization.getOrganizationName() == null || userOrganization.getOrganizationName().trim().equals("")) continue;
			
			tempItem = new Listitem(userOrganization.getOrgExternalId().trim(),userOrganization.getUserOrgId());
			tempItem.setParent(orgLbId);
		} // for
		orgLbId.setSelectedIndex(0);
	}//setUserOrg

	
	
	public void onSelect$userOrgLbId() {
		
		logger.info("in on select of userOrgLbId");
		
	}//onSelect$userOrgLbId
*/

	private final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private Random rnd = new Random();
	
	
	// Generate key
		public void onClick$generateKeyAnchId() {
			logger.info("on click of generate Anch key");
			/*if (!validateAccountId(userOrgLbId)) {
						return ;
			}*/

			if(! isEnabledMointoring(optSyncDataList)){
				MessageUtil.setMessage("OptSync monitoring is not enabled for "+Utility.getOnlyUserName(currentUser.getUserName())+" \n Please contact Administrator.",	"color:red", "TOP");
				return;
			}
			optSynAuthKeyTbId.getValue().trim();
					
			// generate the 16 digit number
			boolean genOptSynKeyFlag = false;
			String 	generateString ="";
				
			while(!genOptSynKeyFlag) {
						
				generateString = randomString(16);
				//change the name 
				List<UserOrganization> userOgList =  updateOptSyncDataDao.findAllByOptSyncName(generateString);
						
				if(userOgList == null || userOgList.size() == 0){
						genOptSynKeyFlag = true;
						logger.info("Generated Plugin id is unquie "+generateString );
						break;
					}
				else{
						generateKeyAnchId.setVisible(true);
						continue;
					}
			}//while loop
			logger.debug("User ::::::::::::"+ generateString +"<<<<<<<< Length >>>>>.:"+generateString.length());
				optSynAuthKeyTbId.setValue(generateString);
				optSynAuthKeyTbId.setReadonly(true);
				generateKeyAnchId.setVisible(false);
					
					
		}//on click of key

		//generating 16 Characters String
		private String randomString( int len ) 
		{
			StringBuilder sb = new StringBuilder( len );
			for( int i = 0; i < len; i++ ) 
				sb.append( AB.charAt( rnd.nextInt(AB.length()) ) );
				return sb.toString();
		}//randomString

	public void onClick$addOptSyncPluginTBId() {
		newOptSyncPluginGbIdView.setVisible(false);
		session.removeAttribute("UpdateOptSyncDataType"); 
		session.removeAttribute("UpdateOptSyncData");
		deleteEmailTBId1.setVisible(false);
		
		if(! isEnabledMointoring(optSyncDataList)){
			MessageUtil.setMessage("OptSync monitoring is not enabled for "+Utility.getOnlyUserName(currentUser.getUserName())+" \n Please contact Administrator.",	"color:red", "TOP");
			return;
		}
		clearTextBoxFeilds();
		addOptSyncPluginTBId.setVisible(false);
		newOptSyncPluginGbId.setVisible(true);
	} // onClick$addOptSyncPluginTBId

	// Specifying maxEmailtext length
	private int maxEmailLength = 50;

	// Second
	public void onClick$addOneMoreEmailTBId1() {

		deleteEmailTBId1.setVisible(true);
		logger.info("Adding Second email text box");
		addOneMoreEmailTBId1.setVisible(false);
		deleteEmailTBId2.setVisible(true);
		emailAddress2Label.setVisible(true);
		// emailAddress2star.setVisible(true);
		emailAddress2TbId.setVisible(true);
		emailAddress2TbId.setValue(Constants.STRING_NILL);
		emailAddress2TbId.setFocus(true);

		emailAddress2TbId.setMaxlength(maxEmailLength);
		addOneMoreEmailTBId2.setVisible(true);
		// logger.debug("Boolean value of second email textbox ::"+isemailAddress2textbox);
		isemailAddress2textbox = true;
		// logger.debug("Boolean value of second email textbox ::"+isemailAddress2textbox);
		addOptSyncPluginTBId.setVisible(false);
		emailAddress2Label.setValue("Email Address 2:");
		emailAddress3Label.setValue("Email Address 3:");
		emailAddress4Label.setValue("Email Address 4:");
		emailAddress5Label.setValue("Email Address 5:");

	} // added second email

	// Third
	public void onClick$addOneMoreEmailTBId2() {
		logger.info("Adding Third email text box");
		addOneMoreEmailTBId2.setVisible(false);
		deleteEmailTBId3.setVisible(true);
		emailAddress3Label.setVisible(true);
		// emailAddress3star.setVisible(true);
		emailAddress3TbId.setVisible(true);
		emailAddress3TbId.setValue(Constants.STRING_NILL);
		emailAddress3TbId.setFocus(true);
		emailAddress3TbId.setMaxlength(maxEmailLength);
		addOneMoreEmailTBId3.setVisible(true);
		// logger.debug("Boolean value of Third email textbox ::"+isemailAddress3textbox);
		isemailAddress3textbox = true;
		// logger.debug("Boolean value of Third email textbox ::"+isemailAddress3textbox);
		addOptSyncPluginTBId.setVisible(false);
	}// //added third email

	// Fourth
	public void onClick$addOneMoreEmailTBId3() {
		String label = emailAddress4Label.getValue().trim();
		logger.info("Adding fourth email text box::::::::::::::::::" + label);
		if (label.equalsIgnoreCase("Email address 3:"))
			label = "Email address 4:";
		logger.info("Adding Fourth email text box");
		addOneMoreEmailTBId3.setVisible(false);
		deleteEmailTBId4.setVisible(true);
		emailAddress4Label.setVisible(true);
		emailAddress4Label.setValue(label);
		// emailAddress4star.setVisible(true);
		emailAddress4TbId.setVisible(true);
		emailAddress4TbId.setValue(Constants.STRING_NILL);
		emailAddress4TbId.setFocus(true);
		emailAddress4TbId.setMaxlength(maxEmailLength);
		addOneMoreEmailTBId4.setVisible(true);
		// logger.debug("Boolean value of Fourth email textbox ::"+isemailAddress4textbox);
		isemailAddress4textbox = true;
		// logger.debug("Boolean value of Fourth email textbox ::"+isemailAddress4textbox);
		addOptSyncPluginTBId.setVisible(false);

	}// added fourth email

	// Fifth
	public void onClick$addOneMoreEmailTBId4() {
		String label = emailAddress5Label.getValue().trim();
		logger.info("Adding Fifth email text box::::::::::::::::::" + label);
		if (label.equalsIgnoreCase("Email address 4:"))
			label = "Email address 5:";
		addOneMoreEmailTBId4.setVisible(false);
		deleteEmailTBId5.setVisible(true);
		emailAddress5Label.setVisible(true);
		emailAddress5Label.setValue(label);
		// emailAddress5star.setVisible(true);
		emailAddress5TbId.setVisible(true);
		emailAddress5TbId.setValue(Constants.STRING_NILL);
		emailAddress5TbId.setFocus(true);
		emailAddress5TbId.setMaxlength(maxEmailLength);
		logger.debug("Boolean value of Fifth email textbox ::"
				+ isemailAddress5textbox);
		isemailAddress5textbox = true;
		logger.debug("Boolean value of Fifth email textbox ::"
				+ isemailAddress5textbox);
		addOptSyncPluginTBId.setVisible(false);
	}// added fifth email

	// Deleting first email id
	public void onClick$deleteEmailTBId1() {

		// Set Labels
		emailAddress2Label.setValue("Email Address 2:");
		emailAddress3Label.setValue("Email Address 3:");
		emailAddress4Label.setValue("Email Address 4:");
		emailAddress5Label.setValue("Email Address 5:");

		/*
		 * if(!isemailAddress5textbox && !isemailAddress4textbox &&
		 * !isemailAddress3textbox){ addOneMoreEmailTBId1.setVisible(true);
		 * logger.info("In if 0"); }
		 */

		if (!isemailAddress5textbox && !isemailAddress4textbox
				&& !isemailAddress3textbox && isemailAddress2textbox) {

			emailIdTbId.setValue(emailAddress2TbId.getValue().trim());
			addOneMoreEmailTBId1.setVisible(true);
			deleteEmailTBId1.setVisible(false);
			logger.info("In if ........");

			emailAddress2Label.setVisible(false);
			emailAddress2TbId.setVisible(false);
			deleteEmailTBId2.setVisible(false);
			addOneMoreEmailTBId2.setVisible(false);
			isemailAddress2textbox = false;
			logger.info("Deleting 3th email");

			emailAddress3Label.setVisible(false);
			emailAddress3TbId.setVisible(false);
			// emailAddress3star.setVisible(false);
			deleteEmailTBId3.setVisible(false);
			isemailAddress3textbox = false;
			addOneMoreEmailTBId2.setVisible(false);
			logger.info("Deleting 3th email");

			emailAddress4Label.setVisible(false);
			// emailAddress4star.setVisible(false);
			emailAddress4TbId.setVisible(false);
			isemailAddress4textbox = false;
			deleteEmailTBId4.setVisible(false);
			addOneMoreEmailTBId3.setVisible(false);
			logger.info("Deleting 4th email");

			emailAddress5Label.setVisible(false);
			// emailAddress5star.setVisible(false);
			emailAddress5TbId.setVisible(false);
			deleteEmailTBId5.setVisible(false);
			isemailAddress5textbox = false;
			logger.info("Deleting 5th email");
		}

		if (!isemailAddress5textbox && !isemailAddress4textbox
				&& isemailAddress3textbox && isemailAddress2textbox) {
			addOneMoreEmailTBId2.setVisible(true);
			emailIdTbId.setValue(emailAddress2TbId.getValue().trim());
			logger.info("In if 3");

			emailAddress2Label.setValue("Email Address 2:");
			emailAddress2Label.setVisible(true);
			emailAddress2TbId.setValue(emailAddress3TbId.getValue().trim());
			emailAddress2TbId.setVisible(true);
			deleteEmailTBId2.setVisible(true);
			isemailAddress2textbox = true;

			emailAddress3Label.setVisible(false);
			emailAddress3TbId.setVisible(false);
			// emailAddress3star.setVisible(false);
			deleteEmailTBId3.setVisible(false);
			isemailAddress3textbox = false;
			addOneMoreEmailTBId2.setVisible(true);
			logger.info("Deleting 3th email");

			emailAddress4Label.setVisible(false);
			// emailAddress4star.setVisible(false);
			emailAddress4TbId.setVisible(false);
			isemailAddress4textbox = false;
			deleteEmailTBId4.setVisible(false);
			addOneMoreEmailTBId3.setVisible(false);
			logger.info("Deleting 4th email");

			emailAddress5Label.setVisible(false);
			// emailAddress5star.setVisible(false);
			emailAddress5TbId.setVisible(false);
			deleteEmailTBId5.setVisible(false);
			isemailAddress5textbox = false;
			logger.info("Deleting 5th email");
		}

		if (!isemailAddress5textbox && isemailAddress4textbox
				&& isemailAddress3textbox && isemailAddress2textbox) {
			addOneMoreEmailTBId1.setVisible(false);
			addOneMoreEmailTBId2.setVisible(false);
			logger.info("In if 2");

			emailIdTbId.setValue(emailAddress2TbId.getValue().trim());

			emailAddress2Label.setValue("Email Address 2:");
			emailAddress2Label.setVisible(true);
			emailAddress2TbId.setValue(emailAddress3TbId.getValue().trim());
			emailAddress2TbId.setVisible(true);
			deleteEmailTBId2.setVisible(true);
			isemailAddress2textbox = true;

			emailAddress3Label.setValue("Email Address 3:");
			emailAddress3Label.setVisible(true);
			emailAddress3TbId.setValue(emailAddress4TbId.getValue().trim());
			emailAddress3TbId.setVisible(true);
			deleteEmailTBId3.setVisible(true);
			isemailAddress3textbox = true;

			addOneMoreEmailTBId3.setVisible(true);

			emailAddress4Label.setVisible(false);
			// emailAddress4star.setVisible(false);
			emailAddress4TbId.setVisible(false);
			isemailAddress4textbox = false;
			deleteEmailTBId4.setVisible(false);
			logger.info("Deleting 4th email");
			// addOneMoreEmailTBId3.setVisible(false);

			emailAddress5Label.setVisible(false);
			// emailAddress5star.setVisible(false);
			emailAddress5TbId.setVisible(false);
			deleteEmailTBId5.setVisible(false);
			isemailAddress5textbox = false;
			addOneMoreEmailTBId4.setVisible(false);
			logger.info("Deleting 5th email");
		}

		if (isemailAddress5textbox && isemailAddress4textbox
				&& isemailAddress3textbox && isemailAddress2textbox) {
			addOneMoreEmailTBId2.setVisible(false);
			logger.info("In if 1");

			emailIdTbId.setValue(emailAddress2TbId.getValue().trim());

			emailAddress2Label.setValue("Email Address 2:");
			emailAddress2Label.setVisible(true);
			emailAddress2TbId.setValue(emailAddress3TbId.getValue().trim());
			emailAddress2TbId.setVisible(true);
			deleteEmailTBId2.setVisible(true);
			isemailAddress2textbox = true;

			emailAddress3Label.setValue("Email Address 3:");
			emailAddress3Label.setVisible(true);
			emailAddress3TbId.setValue(emailAddress4TbId.getValue().trim());
			emailAddress3TbId.setVisible(true);
			deleteEmailTBId3.setVisible(true);
			isemailAddress3textbox = true;

			emailAddress4Label.setValue("Email Address 4:");
			emailAddress4Label.setVisible(true);
			emailAddress4TbId.setValue(emailAddress5TbId.getValue().trim());
			emailAddress4TbId.setVisible(true);
			deleteEmailTBId4.setVisible(true);
			addOneMoreEmailTBId4.setVisible(true);
			isemailAddress4textbox = true;

			emailAddress5Label.setVisible(false);
			// emailAddress5star.setVisible(false);
			emailAddress5TbId.setVisible(false);
			deleteEmailTBId5.setVisible(false);
			isemailAddress5textbox = false;
			logger.info("Deleting 5th email");

		}

	}

	// Deleting second email id
	public void onClick$deleteEmailTBId2() {
		logger.info("Deleting Second Email Id");
		deleteEmailTBId2.setVisible(false);

		deleteEmailTBId1.setVisible(false);
		// addOneMoreEmailTBId1.setVisible(true);
		addOneMoreEmailTBId2.setVisible(false);
		emailAddress2Label.setVisible(false);
		// emailAddress2star.setVisible(false);
		emailAddress2TbId.setVisible(false);

		isemailAddress2textbox = false;

		// Set Labels
		emailAddress3Label.setValue("Email Address 3:");
		emailAddress4Label.setValue("Email Address 4:");
		emailAddress5Label.setValue("Email Address 5:");

		if (!isemailAddress5textbox && !isemailAddress4textbox
				&& !isemailAddress3textbox) {
			addOneMoreEmailTBId1.setVisible(true);
			logger.info("In if 0");
		} else {
			deleteEmailTBId1.setVisible(true);
		}

		if (!isemailAddress5textbox && !isemailAddress4textbox
				&& isemailAddress3textbox) {
			addOneMoreEmailTBId2.setVisible(true);
			logger.info("In if 3");

			emailAddress2Label.setValue("Email Address 2:");
			emailAddress2Label.setVisible(true);
			emailAddress2TbId.setValue(emailAddress3TbId.getValue().trim());
			emailAddress2TbId.setVisible(true);
			deleteEmailTBId2.setVisible(true);
			isemailAddress2textbox = true;

			emailAddress3Label.setVisible(false);
			emailAddress3TbId.setVisible(false);
			// emailAddress3star.setVisible(false);
			deleteEmailTBId3.setVisible(false);
			isemailAddress3textbox = false;
			addOneMoreEmailTBId2.setVisible(true);
			logger.info("Deleting 3th email");

			emailAddress4Label.setVisible(false);
			// emailAddress4star.setVisible(false);
			emailAddress4TbId.setVisible(false);
			isemailAddress4textbox = false;
			deleteEmailTBId4.setVisible(false);
			addOneMoreEmailTBId3.setVisible(false);
			logger.info("Deleting 4th email");

			emailAddress5Label.setVisible(false);
			// emailAddress5star.setVisible(false);
			emailAddress5TbId.setVisible(false);
			deleteEmailTBId5.setVisible(false);
			isemailAddress5textbox = false;
			logger.info("Deleting 5th email");
		}

		if (!isemailAddress5textbox && isemailAddress4textbox
				&& isemailAddress3textbox) {
			addOneMoreEmailTBId1.setVisible(false);
			addOneMoreEmailTBId2.setVisible(false);
			logger.info("In if 2");

			emailAddress2Label.setValue("Email Address 2:");
			emailAddress2Label.setVisible(true);
			emailAddress2TbId.setValue(emailAddress3TbId.getValue().trim());
			emailAddress2TbId.setVisible(true);
			deleteEmailTBId2.setVisible(true);
			isemailAddress2textbox = true;

			emailAddress3Label.setValue("Email Address 3:");
			emailAddress3Label.setVisible(true);
			emailAddress3TbId.setValue(emailAddress4TbId.getValue().trim());
			emailAddress3TbId.setVisible(true);
			deleteEmailTBId3.setVisible(true);
			isemailAddress3textbox = true;

			addOneMoreEmailTBId3.setVisible(true);

			emailAddress4Label.setVisible(false);
			// emailAddress4star.setVisible(false);
			emailAddress4TbId.setVisible(false);
			isemailAddress4textbox = false;
			deleteEmailTBId4.setVisible(false);
			logger.info("Deleting 4th email");
			// addOneMoreEmailTBId3.setVisible(false);

			emailAddress5Label.setVisible(false);
			// emailAddress5star.setVisible(false);
			emailAddress5TbId.setVisible(false);
			deleteEmailTBId5.setVisible(false);
			isemailAddress5textbox = false;
			addOneMoreEmailTBId4.setVisible(false);
			logger.info("Deleting 5th email");
		}

		if (isemailAddress5textbox && isemailAddress4textbox
				&& isemailAddress3textbox) {
			addOneMoreEmailTBId2.setVisible(false);
			logger.info("In if 1");

			emailAddress2Label.setValue("Email Address 2:");
			emailAddress2Label.setVisible(true);
			emailAddress2TbId.setValue(emailAddress3TbId.getValue().trim());
			emailAddress2TbId.setVisible(true);
			deleteEmailTBId2.setVisible(true);
			isemailAddress2textbox = true;

			emailAddress3Label.setValue("Email Address 3:");
			emailAddress3Label.setVisible(true);
			emailAddress3TbId.setValue(emailAddress4TbId.getValue().trim());
			emailAddress3TbId.setVisible(true);
			deleteEmailTBId3.setVisible(true);
			isemailAddress3textbox = true;

			emailAddress4Label.setValue("Email Address 4:");
			emailAddress4Label.setVisible(true);
			emailAddress4TbId.setValue(emailAddress5TbId.getValue().trim());
			emailAddress4TbId.setVisible(true);
			deleteEmailTBId4.setVisible(true);
			addOneMoreEmailTBId4.setVisible(true);
			isemailAddress4textbox = true;

			emailAddress5Label.setVisible(false);
			// emailAddress5star.setVisible(false);
			emailAddress5TbId.setVisible(false);
			deleteEmailTBId5.setVisible(false);
			isemailAddress5textbox = false;
			logger.info("Deleting 5th email");

		}
	}// deleting second email id

	// Deleting Third email id
	int count = 0;

	public void onClick$deleteEmailTBId3() {
		logger.info("Deleting Third Email Id");
		deleteEmailTBId3.setVisible(false);
		// addOneMoreEmailTBId2.setVisible(true);
		addOneMoreEmailTBId3.setVisible(false);
		emailAddress3Label.setVisible(false);
		// emailAddress3star.setVisible(false);
		emailAddress3TbId.setVisible(false);

		isemailAddress3textbox = false;
		emailAddress4Label.setValue("Email Address 3:");
		emailAddress5Label.setValue("Email Address 4:");

		if (!isemailAddress5textbox && !isemailAddress4textbox) {
			addOneMoreEmailTBId2.setVisible(true);

		}

		if (isemailAddress5textbox && isemailAddress4textbox) {
			addOneMoreEmailTBId3.setVisible(true);
		}

		if (!isemailAddress5textbox && isemailAddress4textbox) {
			addOneMoreEmailTBId1.setVisible(false);
			addOneMoreEmailTBId2.setVisible(false);
			logger.info("In if 2");

			emailAddress3Label.setValue("Email Address 3:");
			emailAddress3Label.setVisible(true);
			emailAddress3TbId.setValue(emailAddress4TbId.getValue().trim());
			emailAddress3TbId.setVisible(true);
			deleteEmailTBId3.setVisible(true);
			isemailAddress3textbox = true;

			addOneMoreEmailTBId3.setVisible(true);

			emailAddress4Label.setVisible(false);
			// emailAddress4star.setVisible(false);
			emailAddress4TbId.setVisible(false);
			isemailAddress4textbox = false;
			deleteEmailTBId4.setVisible(false);
			logger.info("Deleting 4th email");
			// addOneMoreEmailTBId3.setVisible(false);

			emailAddress5Label.setVisible(false);
			// emailAddress5star.setVisible(false);
			emailAddress5TbId.setVisible(false);
			deleteEmailTBId5.setVisible(false);
			isemailAddress5textbox = false;
			addOneMoreEmailTBId4.setVisible(false);
			logger.info("Deleting 5th email");
		}

		if (isemailAddress5textbox && isemailAddress4textbox) {
			logger.info("In if 1");

			emailAddress3Label.setValue("Email Address 3:");
			emailAddress3Label.setVisible(true);
			emailAddress3TbId.setValue(emailAddress4TbId.getValue().trim());
			emailAddress3TbId.setVisible(true);
			deleteEmailTBId3.setVisible(true);
			isemailAddress3textbox = true;
			addOneMoreEmailTBId3.setVisible(false);

			emailAddress4Label.setValue("Email Address 4:");
			emailAddress4Label.setVisible(true);
			emailAddress4TbId.setValue(emailAddress5TbId.getValue().trim());
			emailAddress4TbId.setVisible(true);
			deleteEmailTBId4.setVisible(true);
			addOneMoreEmailTBId4.setVisible(true);
			isemailAddress4textbox = true;

			emailAddress5Label.setVisible(false);
			// emailAddress5star.setVisible(false);
			emailAddress5TbId.setVisible(false);
			deleteEmailTBId5.setVisible(false);
			isemailAddress5textbox = false;
			logger.info("Deleting 5th email");

		}

	}// deleting third0 email id

	// Deleting fourth email id
	public void onClick$deleteEmailTBId4() {
		logger.info("Deleting fourth Email Id");
		deleteEmailTBId4.setVisible(false);
		// addOneMoreEmailTBId3.setVisible(true);
		addOneMoreEmailTBId4.setVisible(false);
		emailAddress4Label.setVisible(false);
		// emailAddress4star.setVisible(false);
		emailAddress4TbId.setVisible(false);

		isemailAddress4textbox = false;
		logger.info("isemailAddress5textboxisemailAddress5textbox ::::"
				+ isemailAddress5textbox);
		if (!isemailAddress5textbox) {
			logger.info("1");
			addOneMoreEmailTBId3.setVisible(true);
			return;
		}

		String label = "Email Address 5:";
		if (isemailAddress5textbox) {
			logger.info("2");
			addOneMoreEmailTBId4.setVisible(true);
			label = "Email Address 4:";
		}

		logger.info("Label ............: " + label);
		emailAddress5Label.setValue(label);

		if (emailAddress5Label.getValue().equalsIgnoreCase(label)) {
			logger.info("3");
			emailAddress4Label.setValue(label);
			emailAddress4Label.setVisible(true);
			emailAddress4TbId.setVisible(true);
			emailAddress4TbId.setValue(emailAddress5TbId.getValue().trim());
			deleteEmailTBId4.setVisible(true);
			isemailAddress4textbox = true;

			logger.info("Deleting fifth Email Id");
			deleteEmailTBId5.setVisible(false);
			addOneMoreEmailTBId4.setVisible(true);
			emailAddress5Label.setVisible(false);
			// emailAddress5star.setVisible(false);
			emailAddress5TbId.setVisible(false);

			isemailAddress5textbox = false;
		}

	}// deleting third0 email id

	// Deleting fifth email id
	public void onClick$deleteEmailTBId5() {
		logger.info("Deleting fifth Email Id");
		deleteEmailTBId5.setVisible(false);
		addOneMoreEmailTBId4.setVisible(true);
		emailAddress5Label.setVisible(false);
		// emailAddress5star.setVisible(false);
		emailAddress5TbId.setVisible(false);

		isemailAddress5textbox = false;
	}// deleting third0 email id	
		
	
	
		
		
		// on click of save button
	public void onClick$saveBtnId() {
		logger.debug("Session Type ::"+  session.getAttribute("UpdateOptSyncDataType")); 
		logger.debug("Session Data ::"+  session.getAttribute("UpdateOptSyncData"));
	
		sessionUpdateOptSyncData = (UpdateOptSyncData) session.getAttribute("UpdateOptSyncData");
		type	= (String) session.getAttribute("UpdateOptSyncDataType");
	
		//Create
		if(sessionUpdateOptSyncData == null && type  == null){
			//on save
			logger.info("Save button is clicked");
			logger.info("session updateoptsyncdata & type are  null");
			if (!validateFeilds() )
			{
				logger.error("error in validating feilds");
				return;
			}
			sessionUpdateOptSyncData =new UpdateOptSyncData();
			//logger.info("Get optsyncAuthkey to user org ::"+GetUser.getUserObj().getUserOrganization().getOptSyncKey());
			sessionUpdateOptSyncData.setUserId(GetUser.getUserId());
			sessionUpdateOptSyncData.setOptSyncId(Long.parseLong(pluginTbId.getValue().trim()));
			sessionUpdateOptSyncData.setOptSyncName(optSyncPluginTbId.getValue().trim());
			//while creating status  NEW
			sessionUpdateOptSyncData.setStatus(OCConstants.OPT_SYNC_STATUS_NEW);
			//while creating new plugin status should be active 
			sessionUpdateOptSyncData.setPluginStatus(OCConstants.OPT_SYNC_PLUGIN_STATUS_ACTIVE);
			String emailId=emailIdTbId.getValue().trim();
			//second email id
			if(isemailAddress2textbox){
				 emailId = emailId + "," + emailAddress2TbId.getValue().trim();
				 isemailAddress2textbox =false;
			}
			//third email id
			if(isemailAddress3textbox){
				 emailId = emailId + "," + emailAddress3TbId.getValue().trim();
				 isemailAddress3textbox =false;
			}
			//fourth email id
			if(isemailAddress4textbox){
				 emailId = emailId + "," + emailAddress4TbId.getValue().trim();
				 isemailAddress4textbox =false;
			}
			//fifth email id
			if(isemailAddress5textbox){
				 emailId = emailId + "," + emailAddress5TbId.getValue().trim();
				 isemailAddress5textbox =false;
			}
			//setting email id
			sessionUpdateOptSyncData.setEmailId(emailId);
			sessionUpdateOptSyncData.setEnabledOptSyncFlag(OCConstants.Opt_SYNC_DISABLE_MOINTORING);
			if(currentUserOrgList != null){
			sessionUpdateOptSyncData.setOrgId(currentUserOrgList.get(0).getUserOrgId());	
			}
		}
		else if(sessionUpdateOptSyncData != null && type  != null){
			logger.info("Update button is clicked");
			if (!validateFeilds() )
			{
				logger.error("error in validating feilds");
				return;
			}
			//sessionUpdateOptSyncData.setUserId(GetUser.getUserId());
			sessionUpdateOptSyncData.setOptSyncId(Long.parseLong(pluginTbId.getValue().trim()));
			sessionUpdateOptSyncData.setOptSyncName(optSyncPluginTbId.getValue().trim());
			String emailId=emailIdTbId.getValue().trim();
			//second email id
			if(isemailAddress2textbox ){
				 emailId = emailId + "," + emailAddress2TbId.getValue().trim();
			}
			//third email id
			if(isemailAddress3textbox ){
				 emailId = emailId + "," + emailAddress3TbId.getValue().trim();
			}
			//fourth email id
			if(isemailAddress4textbox ){
				 emailId = emailId + "," + emailAddress4TbId.getValue().trim();
			}
			//fifth email id
			if(isemailAddress5textbox ){
				 emailId = emailId + "," + emailAddress5TbId.getValue().trim();
			}
			sessionUpdateOptSyncData.setEmailId(emailId);
		}
		
		logger.info("Confirmation Box ....................");
		try {
			 String saveMsg = "";
			int confirm = Messagebox.show("Are you sure you want to save the OptSync Plug-in?", "Prompt", 
					 Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
			if(confirm != Messagebox.OK) return ;
			
				if(confirm == Messagebox.OK) {
					logger.info("Saving OptSync Plug-in Setting");
					
					try {
						if(type == null) {
							
							String optSyncAuthkey = optSynAuthKeyTbId.getValue().trim();
							if(!validateOptSyncKey(optSyncAuthkey)){
								logger.error("Optsync Auth key already exists");
								return;
							}
							UserOrganization userOrgObj = GetUser.getUserObj().getUserOrganization();//.setOptSyncKey(optSyncAuthkey);
							if(userOrgObj != null) {
								String str = userOrgObj.getOptSyncKey();
								if(str == null){
									str = "";
								}
								if(!str.equalsIgnoreCase(optSyncAuthkey))
								{
									
										logger.info("Setting optsyncAuthkey to user org");
										 //usersDao.updateOptSynKey(GetUser.getUserObj().getUserOrganization().getUserOrgId() , optSyncAuthkey);
										usersDaoForDML.updateOptSynKey(GetUser.getUserObj().getUserOrganization().getUserOrgId() , optSyncAuthkey);
									
								}
								else{
									logger.info("OptSynAuth key is already exist");
								}
							}
							logger.info("Storing OptSyncAuthkey in UserOrg table");
							saveMsg = "created"; 
							updateOptSyncDataDaoForDML.saveOrUpdate(sessionUpdateOptSyncData);
							logger.info("-------"+saveMsg);
						}else if(type != null){
							saveMsg = "updated";
							updateOptSyncDataDaoForDML.saveOrUpdate(sessionUpdateOptSyncData);
							logger.info("-------"+saveMsg);
						}
						MessageUtil.setMessage("OptSync Plug-in "+saveMsg +"  successfully.","color:green;");
					} catch (Exception e) {
						logger.error("Problem in "+saveMsg +" OptSync Plug-in Exception ::", e);
					}
				}

		} catch (Exception e) {
			logger.error("Exception ::", e);
		}
		
	  session.removeAttribute("UpdateOptSyncDataType"); 
	  session.removeAttribute("UpdateOptSyncData");
	  Redirect.goTo(PageListEnum.EMPTY);
	  Redirect.goTo(PageListEnum.USERADMIN_OPT_SYNC);
	     
	}// save button
	
	// on click of reset button
	public void onClick$resetBtnId() {
		optSyncPluginTbId.setValue(Constants.STRING_NILL);
		pluginTbId.setValue(Constants.STRING_NILL);
		pluginTbId.setReadonly(false);
		generateAnchId.setVisible(true);
		emailIdTbId.setValue(Constants.STRING_NILL);
		emailAddress2TbId.setValue(Constants.STRING_NILL);
		emailAddress3TbId.setValue(Constants.STRING_NILL);
		emailAddress4TbId.setValue(Constants.STRING_NILL);
		emailAddress5TbId.setValue(Constants.STRING_NILL);
		return;
	}// reset button

	
	private A generateKeyAnchId,generateAnchId ;
	
	// Generate Id
	public void onClick$generateAnchId() {
		logger.info(" on click of Generate id");
		sessionUpdateOptSyncData = (UpdateOptSyncData) session.getAttribute("UpdateOptSyncData");
		type	= (String) session.getAttribute("UpdateOptSyncDataType");
	
		String pluginId = optSyncPluginTbId.getValue().trim();
		if(pluginId == null || pluginId.equals(""))
		{
			logger.info("plugin id does not exist so creating");
			generateAnchId.setVisible(true);
		
			long generateNumber= generateSixteenDigit();
		
			pluginTbId.setValue(generateNumber +"");
			pluginTbId.setReadonly(true);
			generateAnchId.setVisible(false);
		}//if
		else{
			logger.info("Plugin Id of user already exist");
			if(sessionUpdateOptSyncData != null && type !=  null){
				generateAnchId.setVisible(false);
				pluginTbId.setReadonly(true);
			}
			else{
				generateAnchId.setVisible(true);
				generateAnchId.setVisible(false);
				pluginTbId.setValue(generateSixteenDigit() +"");
				pluginTbId.setReadonly(true);
			}
			
		}

	} //generate id
	
	
	//method to generate 16 digit number
	public long generateSixteenDigit(){
		long generateOptSyncId =0L;
		while(true){
		
			Random rand = new Random();
			generateOptSyncId = 1 + rand.nextInt(9);  
				for(int i = 0; i < 15; i++) {
					generateOptSyncId *= 10L;
					generateOptSyncId += rand.nextInt(10);
			}
			List<UpdateOptSyncData>	optSyncList =( List<UpdateOptSyncData>) updateOptSyncDataDao.findAllByOptSynId(generateOptSyncId);
			if(optSyncList == null ){
				logger.info("Generated Plugin id is unquie "+generateOptSyncId);
				break;
			}
			else{
				
				continue;
			}
				
		}//while
		return generateOptSyncId;
	}

	
	
	//******************** Event Listener ****************************
	
		@Override
		public void onEvent(Event event) throws Exception {
			super.onEvent(event);
		
			if(! isEnabledMointoring(optSyncDataList)){
				MessageUtil.setMessage("OptSync monitoring is not enabled for "+Utility.getOnlyUserName(currentUser.getUserName())+" \n Please contact Administrator.",	"color:red", "TOP");
				return;
			}
			
			
			if(event.getTarget() instanceof Image) {
				
				Image img = (Image)event.getTarget();
				String imageEventName = img.getAttribute("imageEventName").toString();
				logger.debug("Event Type is ::"+imageEventName);
				Listitem li = (Listitem)img.getParent().getParent().getParent();
				
				
				UpdateOptSyncData updateOptSyncData=(UpdateOptSyncData)li.getValue();
				 if(imageEventName.equals("editList")) {
					
					 
					// session.setAttribute("UpdateOptSyncData", "edit");
					editList(updateOptSyncData );
				} 
				 else if(imageEventName.equals("userDelete")) {
						
					 logger.info("Deleting the OptSync Plugin with plugin id");
						try {
							
							int confirm = Messagebox.show("Confirm to delete the selected OptSync Plug-in.", "Confirm",Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
							
							if(confirm ==  Messagebox.CANCEL) return;
							
							if(confirm == Messagebox.OK) {
								newOptSyncPluginGbId.setVisible(false);
								newOptSyncPluginGbIdView.setVisible(false);
								//updateOptSyncDataDao.delete(updateOptSyncData);
								updateOptSyncDataDaoForDML.delete(updateOptSyncData);
								MessageUtil.setMessage("OptSync Plug-in  deleted successfully.","color:blue","TOP");
//								setDefaultSettings(GetUser.getUserId());
								Redirect.goTo(PageListEnum.EMPTY);
								Redirect.goTo(PageListEnum.USERADMIN_OPT_SYNC);
								
							}//if
							}//try
						 catch (Exception e) {
							// TODO Auto-generated catch block
							logger.error("Exception ::", e);
						}
						return;
						
					}
				 else if(imageEventName.equalsIgnoreCase("view") && updateOptSyncData  != null){
					 logger.info("On click of view details image");
					 
					 viewOptSyncDetails(updateOptSyncData);
				 }
				 else if(imageEventName.equalsIgnoreCase("active") && updateOptSyncData  != null) {
						logger.info("Current Status is Inactive so changing to active");
						
						try {
							int confirm = Messagebox.show("Confirm to Activate OptSync alerts.", "Confirm",Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
							if(confirm ==  Messagebox.CANCEL) return;
							
							if(confirm == Messagebox.OK) {
								newOptSyncPluginGbId.setVisible(false);
								newOptSyncPluginGbIdView.setVisible(false);
								
								//int rowseffected = updateOptSyncDataDao.updatePluginStatus(OCConstants.OPT_SYNC_PLUGIN_STATUS_ACTIVE, updateOptSyncData.getOptSyncId());
								int rowseffected = updateOptSyncDataDaoForDML.updatePluginStatus(OCConstants.OPT_SYNC_PLUGIN_STATUS_ACTIVE, updateOptSyncData.getOptSyncId());
								if(rowseffected == 1)
								MessageUtil.setMessage("OptSync alerts Activated successfully.","color:blue","TOP");
								Redirect.goTo(PageListEnum.EMPTY);
								Redirect.goTo(PageListEnum.USERADMIN_OPT_SYNC);
							}//if
						}//try
					 catch (Exception e) {
							logger.error("Exception ::", e);
						}
							return;
						
					}
					else if(imageEventName.equalsIgnoreCase("inactive") && updateOptSyncData  != null) {
						logger.info("Current Status is Active so changing to In active");
						
						try {
							/*int confirm = Messagebox.show("Confirm to Pause OptSync alerts.", "Confirm",Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
							if(confirm ==  Messagebox.CANCEL) return;
							
							if(confirm == Messagebox.OK) {*/
								newOptSyncPluginGbId.setVisible(false);
								newOptSyncPluginGbIdView.setVisible(false);
								
							
								
								createWinId$opySyncAlertsWinId.setVisible(true);
								createWinId$opySyncAlertsWinId.doHighlighted();
								
								createWinId$opySyncAlertsWinId.setAttribute(Constants.OPTSYNC_OBJ, updateOptSyncData);
								
								/*int rowseffected = updateOptSyncDataDao.updatePluginStatus(OCConstants.OPT_SYNC_PLUGIN_STATUS_INACTIVE, updateOptSyncData.getOptSyncId());
								if(rowseffected == 1)
								MessageUtil.setMessage("OptSync alerts Paused successfully.","color:blue","TOP");
								Redirect.goTo(PageListEnum.EMPTY);
								Redirect.goTo(PageListEnum.USERADMIN_OPT_SYNC);*/
							//}
						}//try
					 catch (Exception e) {
							logger.error("Exception ::", e);
						}
							return;
						
					} 
				}
			}//on Event
					
		
				

	//editListMethod	
	private void editList(UpdateOptSyncData updateOptSyncData ){
		try{
			
			logger.debug("In edit List");
			
			logger.debug("In updateOptSync Data :: "+updateOptSyncData);
			session.setAttribute("UpdateOptSyncDataType", "edit");
			session.setAttribute("UpdateOptSyncData", updateOptSyncData);
			newOptSyncPluginGbIdView.setVisible(false);
			newOptSyncPluginGbId.setVisible(true);

			addOptSyncPluginTBId.setVisible(false);
			generateAnchId.setVisible(false);
			
			saveBtnId.setLabel("Update");
			saveBtnId.setVisible(true);
			resetBtnId.setVisible(false);
			
			optSynAuthKeyTbId.setReadonly(true);
			optSyncPluginTbId.setValue(updateOptSyncData.getOptSyncName());
			
			pluginTbId.setValue(updateOptSyncData.getOptSyncId().toString());
			pluginTbId.setReadonly(true);
			
			String emailIds=updateOptSyncData.getEmailId();
			
			List<String> items = Arrays.asList(emailIds.split("\\s*,\\s*"));
			int itemsSize = items.size();
			

			switch(itemsSize){
			
			case 1: //first email id 
			
				logger.info("Editing first email id text box");
				addOneMoreEmailTBId1.setVisible(true);
				deleteEmailTBId1.setVisible(false);
				emailAddress2Label.setVisible(false);
			//	emailAddress2star.setVisible(false);
				emailAddress2TbId.setVisible(false);
				emailIdTbId.setValue(items.get(0));
				//2,3,4 & 5 th as false
				emailAddress2Label.setVisible(false);
				//emailAddress2star.setVisible(false);
				emailAddress2TbId.setVisible(false);
				addOneMoreEmailTBId2.setVisible(false);
				deleteEmailTBId2.setVisible(false);
				emailAddress3Label.setVisible(false);
				//emailAddress3star.setVisible(false);
				emailAddress3TbId.setVisible(false);
				addOneMoreEmailTBId3.setVisible(false);
				deleteEmailTBId3.setVisible(false);
				emailAddress4Label.setVisible(false);
				//emailAddress4star.setVisible(false);
				emailAddress4TbId.setVisible(false);
				addOneMoreEmailTBId4.setVisible(false);
				deleteEmailTBId4.setVisible(false);
				emailAddress5Label.setVisible(false);
				//emailAddress5star.setVisible(false);
				emailAddress5TbId.setVisible(false);
				deleteEmailTBId5.setVisible(false);
			
			break;
			
			case 2://second email id
			
				logger.info("Editing second email id text box");
				addOneMoreEmailTBId1.setVisible(false);
				addOneMoreEmailTBId2.setVisible(true);
				deleteEmailTBId1.setVisible(true);
				emailAddress2Label.setVisible(true);
				//emailAddress2star.setVisible(true);
				emailAddress2TbId.setVisible(true);
				deleteEmailTBId2.setVisible(true);
				isemailAddress2textbox = true;
				addOptSyncPluginTBId.setVisible(false);
				emailIdTbId.setValue(items.get(0));	
				emailAddress2TbId.setValue(items.get(1));
				//3,4 & 5 th as false
				emailAddress3Label.setVisible(false);
				//emailAddress3star.setVisible(false);
				emailAddress3TbId.setVisible(false);
				deleteEmailTBId3.setVisible(false);
				addOneMoreEmailTBId3.setVisible(false);
				emailAddress4Label.setVisible(false);
				//emailAddress4star.setVisible(false);
				emailAddress4TbId.setVisible(false);
				deleteEmailTBId4.setVisible(false);
				addOneMoreEmailTBId4.setVisible(false);
				emailAddress5Label.setVisible(false);
				//emailAddress5star.setVisible(false);
				emailAddress5TbId.setVisible(false);
				deleteEmailTBId5.setVisible(false);
				
			break;
			
			case 3://third email id
			
				
				logger.info("Editing third email id text box");
				addOneMoreEmailTBId1.setVisible(false);
				addOneMoreEmailTBId2.setVisible(true);
				deleteEmailTBId1.setVisible(true);
				emailAddress2Label.setVisible(true);
				//emailAddress2star.setVisible(true);
				emailAddress2TbId.setVisible(true);
				deleteEmailTBId2.setVisible(true);
				isemailAddress2textbox = true;
				addOneMoreEmailTBId2.setVisible(false);
				
				addOneMoreEmailTBId3.setVisible(true);
				emailAddress3Label.setVisible(true);
			//	emailAddress3star.setVisible(true);
				emailAddress3TbId.setVisible(true);
				deleteEmailTBId3.setVisible(true);
				isemailAddress3textbox = true;
				addOptSyncPluginTBId.setVisible(false);
				emailIdTbId.setValue(items.get(0));	
				emailAddress2TbId.setValue(items.get(1));
				emailAddress3TbId.setValue(items.get(2));
				//4 & 5 th as false
				emailAddress4Label.setVisible(false);
			//	emailAddress4star.setVisible(false);
				emailAddress4TbId.setVisible(false);
				addOneMoreEmailTBId4.setVisible(false);
				deleteEmailTBId4.setVisible(false);
				
				emailAddress5Label.setVisible(false);
			//	emailAddress5star.setVisible(false);
				emailAddress5TbId.setVisible(false);
				deleteEmailTBId5.setVisible(false);
			break;
			
			
			case 4://fourth email id

				logger.info("Editing fourth email id text box");
				addOneMoreEmailTBId1.setVisible(false);
				addOneMoreEmailTBId2.setVisible(true);
				deleteEmailTBId1.setVisible(true);
				emailAddress2Label.setVisible(true);
				//emailAddress2star.setVisible(true);
				emailAddress2TbId.setVisible(true);
				deleteEmailTBId2.setVisible(true);
				isemailAddress2textbox = true;
				addOneMoreEmailTBId2.setVisible(false);
				addOneMoreEmailTBId3.setVisible(true);
				emailAddress3Label.setVisible(true);
				//emailAddress3star.setVisible(true);
				emailAddress3TbId.setVisible(true);
				deleteEmailTBId3.setVisible(true);
				isemailAddress3textbox = true;
				addOneMoreEmailTBId3.setVisible(false);
				addOneMoreEmailTBId4.setVisible(true);
				emailAddress4Label.setVisible(true);
				//emailAddress4star.setVisible(true);
				emailAddress4TbId.setVisible(true);
				deleteEmailTBId4.setVisible(true);
				isemailAddress4textbox = true;
				addOptSyncPluginTBId.setVisible(false);
				emailIdTbId.setValue(items.get(0));	
				emailAddress2TbId.setValue(items.get(1));
				emailAddress3TbId.setValue(items.get(2));
				emailAddress4TbId.setValue(items.get(3));
			
				//5 th as false
				emailAddress5Label.setVisible(false);
				//emailAddress5star.setVisible(false);
				emailAddress5TbId.setVisible(false);
				deleteEmailTBId5.setVisible(false);
			break;
			
			
			case 5://fifth email id
			
				logger.info("Editing fifth email id text box");
				addOneMoreEmailTBId1.setVisible(false);
				deleteEmailTBId1.setVisible(true);
				
				addOneMoreEmailTBId2.setVisible(true);
				emailAddress2Label.setVisible(true);
			//	emailAddress2star.setVisible(true);
				emailAddress2TbId.setVisible(true);
				deleteEmailTBId2.setVisible(true);
				isemailAddress2textbox = true;
				addOneMoreEmailTBId2.setVisible(false);
				
				addOneMoreEmailTBId3.setVisible(true);
				emailAddress3Label.setVisible(true);
			//	emailAddress3star.setVisible(true);
				emailAddress3TbId.setVisible(true);
				deleteEmailTBId3.setVisible(true);
				isemailAddress3textbox = true;
				addOneMoreEmailTBId3.setVisible(false);
				
				addOneMoreEmailTBId4.setVisible(true);
				emailAddress4Label.setVisible(true);
			//	emailAddress4star.setVisible(true);
				emailAddress4TbId.setVisible(true);
				deleteEmailTBId4.setVisible(true);
				isemailAddress4textbox = true;
				addOptSyncPluginTBId.setVisible(false);
				addOneMoreEmailTBId4.setVisible(false);
				
				emailAddress5Label.setVisible(true);
			//	emailAddress5star.setVisible(true);
				emailAddress5TbId.setVisible(true);
				isemailAddress5textbox = true;
				addOptSyncPluginTBId.setVisible(false);
				deleteEmailTBId5.setVisible(true);
				emailIdTbId.setValue(items.get(0));	
				
				emailAddress2TbId.setValue(items.get(1));
				emailAddress3TbId.setValue(items.get(2));
				emailAddress4TbId.setValue(items.get(3));
				emailAddress5TbId.setValue(items.get(4));
	
			break;
			}//switch
			
			emailAddressLabel1.setFocus(true);
			saveBtnId.setFocus(true);
			cancelBtnId.setFocus(true);
		}catch (Exception e) {
			logger.error(" - ** Exception while trying to edit the list settings - " + e + " **");
		}
	}//editListmethod
	Label emailAddressLabel1;
	Button cancelBtnId;
	//view optsync details
	private void viewOptSyncDetails(UpdateOptSyncData updateOptSyncData) {
			
		emailIdTbIdView.setValue("");	
		emailAddress2TbIdView.setValue("");
		emailAddress3TbIdView.setValue("");
		emailAddress4TbIdView.setValue("");
		emailAddress5TbIdView.setValue("");
		
		logger.info("Displaying optsync details");
		newOptSyncPluginGbId.setVisible(false);
		newOptSyncPluginGbIdView.setVisible(true);
		addOptSyncPluginTBId.setVisible(true);
			
			try{
				
				optSynAuthKeyTbId.setReadonly(true);
				optSyncPluginTbIdView.setValue(updateOptSyncData.getOptSyncName());
				optSyncPluginTbIdView.setReadonly(true);
				pluginTbIdView.setValue(updateOptSyncData.getOptSyncId().toString());
				pluginTbIdView.setReadonly(true);
				
				
				
				String emailIds=updateOptSyncData.getEmailId();
				
				List<String> items = Arrays.asList(emailIds.split("\\s*,\\s*"));
				int itemsSize = items.size();
				

				switch(itemsSize){
				
				case 1:
					logger.info("Items Size is "+items.size());		
					emailAddress2LabelView.setVisible(false);
					emailAddress2TbIdView.setVisible(false);
					emailAddress3LabelView.setVisible(false);
					emailAddress3TbIdView.setVisible(false);
					emailAddress4LabelView.setVisible(false);
					emailAddress4TbIdView.setVisible(false);
					emailAddress5LabelView.setVisible(false);
					emailAddress5TbIdView.setVisible(false);
					emailIdTbIdView.setValue(items.get(0));
				
				break;
			
				//second email id
				case 2:
					logger.info("Items Size is "+items.size());				
					emailAddress2LabelView.setVisible(true);
					emailAddress2TbIdView.setVisible(true);
					emailAddress3LabelView.setVisible(false);
					emailAddress3TbIdView.setVisible(false);
					emailAddress4LabelView.setVisible(false);
					emailAddress4TbIdView.setVisible(false);
					emailAddress5LabelView.setVisible(false);
					emailAddress5TbIdView.setVisible(false);
					emailIdTbIdView.setValue(items.get(0));	
					emailAddress2TbIdView.setValue(items.get(1));
				break;
				
				//third email id
				
				case 3:
					logger.info("Items Size is "+items.size());					
					emailAddress2LabelView.setVisible(true);
					emailAddress2TbIdView.setVisible(true);
					emailAddress3LabelView.setVisible(true);
					emailAddress3TbIdView.setVisible(true);
					emailAddress4LabelView.setVisible(false);
					emailAddress4TbIdView.setVisible(false);
					emailAddress5LabelView.setVisible(false);
					emailAddress5TbIdView.setVisible(false);
					emailIdTbIdView.setValue(items.get(0));	
					emailAddress2TbIdView.setValue(items.get(1));
					emailAddress3TbIdView.setValue(items.get(2));
				break;
				
				//fourth email id
				case 4:
					logger.info("Items Size is "+items.size());					
					emailAddress2LabelView.setVisible(true);
					emailAddress2TbIdView.setVisible(true);
					emailAddress3LabelView.setVisible(true);
					emailAddress3TbIdView.setVisible(true);
					emailAddress4LabelView.setVisible(true);
					emailAddress4TbIdView.setVisible(true);
					emailAddress5LabelView.setVisible(false);
					emailAddress5TbIdView.setVisible(false);
					emailIdTbIdView.setValue(items.get(0));	
					emailAddress2TbIdView.setValue(items.get(1));
					emailAddress3TbIdView.setValue(items.get(2));
					emailAddress4TbIdView.setValue(items.get(3));
				break;
				
				
				//fifth email id
				case 5:
					logger.info("Items Size is "+items.size());
					emailAddress2LabelView.setVisible(true);
					emailAddress2TbIdView.setVisible(true);
					emailAddress3LabelView.setVisible(true);
					emailAddress3TbIdView.setVisible(true);
					emailAddress4LabelView.setVisible(true);
					emailAddress4TbIdView.setVisible(true);
					emailAddress5LabelView.setVisible(true);
					emailAddress5TbIdView.setVisible(true);
					emailIdTbIdView.setValue(items.get(0));	
					emailAddress2TbIdView.setValue(items.get(1));
					emailAddress3TbIdView.setValue(items.get(2));
					emailAddress4TbIdView.setValue(items.get(3));
					emailAddress5TbIdView.setValue(items.get(4));
				
				break;
				}//switch
				
				//readonly 
				optSynAuthKeyTbId.setReadonly(true);
				optSyncPluginTbIdView.setReadonly(true);
				pluginTbIdView.setReadonly(true);
				
				emailIdTbIdView.setReadonly(true);	
				emailAddress2TbIdView.setReadonly(true);
				emailAddress3TbIdView.setReadonly(true);
				emailAddress4TbIdView.setReadonly(true);
				emailAddress5TbIdView.setReadonly(true);
				
			}catch (Exception e) {
				logger.error(" - ** Exception while trying to Display the list settings - " + e + " **");
			}
		
		}
	
	
	
	// clear feild values

	public void clearTextBoxFeilds() {

		optSyncPluginTbId.setValue("");
		pluginTbId.setValue("");
		generateAnchId.setVisible(true);
		emailIdTbId.setValue("");
		emailAddress2TbId.setValue("");
		emailAddress3TbId.setValue("");
		emailAddress4TbId.setValue("");
		emailAddress5TbId.setValue("");
		
		
		addOneMoreEmailTBId1.setVisible(true);
		emailAddress2Label.setVisible(false);
		//emailAddress2star.setVisible(false);
		emailAddress2TbId.setVisible(false);
		addOneMoreEmailTBId2.setVisible(false);
		
		emailAddress3Label.setVisible(false);
		//emailAddress3star.setVisible(false);
		emailAddress3TbId.setVisible(false);
		addOneMoreEmailTBId3.setVisible(false);
		
		emailAddress4Label.setVisible(false);
		//emailAddress4star.setVisible(false);
		emailAddress4TbId.setVisible(false);
		addOneMoreEmailTBId4.setVisible(false);
		
		emailAddress5Label.setVisible(false);
		//emailAddress5star.setVisible(false);
		emailAddress5TbId.setVisible(false);
		
		saveBtnId.setLabel("Add");
		saveBtnId.setVisible(true);
		resetBtnId.setVisible(true);
		
		return;
	}// clear text boxes

	// validation of textbox feilds
	public boolean validateFeilds() {
		logger.debug("in validate feilds");

		try {
			
			String optSyncAuth = optSynAuthKeyTbId.getValue().trim();
			if(!validateOptSyncKey(optSyncAuth)){
				return false;
			}
			
			// validation of optsync name

			String optSyncNm = optSyncPluginTbId.getValue().trim();
			
			

			if (optSyncNm.trim().equals("")) {
				MessageUtil.setMessage("Please provide OptSync Plug-in Name.","color:red", "TOP");
				return false;
			}

			if (optSyncNm.length() > 0 && !Utility.validateName(optSyncNm)) {
				MessageUtil.setMessage("Please enter Valid OptSync Plug-in Name.","color:red", "TOP");
				return false;
			}

			// validation of optculture plugin id.
			if (!validateNum(pluginTbId)) {
				return false;
			}

			
			// validation of email id
			String emailStr = emailIdTbId.getValue().trim();
			if (emailStr.trim().equals("")) {
				MessageUtil.setMessage("Please provide  email address.", "color:red",	"TOP");
				return false;
			}

			if (emailStr.length() > 0 && !Utility.validateEmail(emailStr)) {
				// submitBtnId.setDisabled(true);
				MessageUtil.setMessage("Please enter  valid  email address",
						"color:red", "TOP");
				return false;
			}
			//validation of email id 2
			logger.debug(" Validate second mehtod Boolean value of textbox ::"+isemailAddress2textbox);
			if(isemailAddress2textbox){
				logger.debug("Validating second email id");
			String emailStr2=emailAddress2TbId.getValue().trim();
			
			if (emailStr2.trim().equals("")) {
				MessageUtil.setMessage("Please provide second email address.", "color:red","TOP");
				return false;
			}

			if (emailStr2.length() > 0 && !Utility.validateEmail(emailStr2)) {
				MessageUtil.setMessage("Please enter  valid second email address",
						"color:red", "TOP");
				return false;
			}

			}//if
			
			//validation of email id 3
			logger.debug(" Validate third mehtod Boolean value of textbox ::"+isemailAddress3textbox);
			if(isemailAddress3textbox){
				logger.debug("Validating third email id");
			String emailStr3=emailAddress3TbId.getValue().trim();
			if (emailStr3.trim().equals("")) {
				MessageUtil.setMessage("Please provide third email address.", "color:red","TOP");
				return false;
			}

			if (emailStr3.length() > 0 && !Utility.validateEmail(emailStr3)) {
				MessageUtil.setMessage("Please enter valid third  email address",
						"color:red", "TOP");
				return false;
			}

			}//if
			
			
			//validation of email id 4
			logger.debug(" Validate Fourth mehtod Boolean value of textbox ::"+isemailAddress4textbox);
			if(isemailAddress4textbox){
				logger.debug("Validating foruth email id");
			String emailStr4=emailAddress4TbId.getValue().trim();
			
			if (emailStr4.trim().equals("")) {
				MessageUtil.setMessage("Please provide fourth email address.", "color:red","TOP");
				logger.info("Not setting Fourth email id");
				return false;
			}

			if (emailStr4.length() > 0 && !Utility.validateEmail(emailStr4)) {
				MessageUtil.setMessage("Please enter valid fourth email address",
						"color:red", "TOP");
				return false;
			}

			}//if
			
			//validation of email id 5
			logger.debug(" Validate fifth mehtod Boolean value of textbox ::"+isemailAddress5textbox);
			if(isemailAddress5textbox){
				logger.debug("Validating fifth email id");
			String emailStr5=emailAddress5TbId.getValue().trim();
			
			if (emailStr5.trim().equals("")) {
				MessageUtil.setMessage("Please provide fifth email address.", "color:red","TOP");
				return false;
			}

			if (emailStr5.length() > 0 && !Utility.validateEmail(emailStr5)) {
				// submitBtnId.setDisabled(true);
				MessageUtil.setMessage("Please enter valid fifth email address",
						"color:red", "TOP");
				return false;
			}

			}//if
			
		} catch (Exception exception) {
			logger.error("Exception ::", exception);
		}

		return true;
	}

	
	
	// validate Plugin ID
	public boolean validateNum(Textbox txtbox) {
		logger.info("Validating 16 digit number");
		long optSyncPluginId = 0L;
		try {
			if(txtbox.getValue().trim() == null){
				
				MessageUtil.setMessage("Please Enter Plug-in ID.","color:red", "TOP");
				return false;
			}
			
			if (txtbox.isValid()) {
				optSyncPluginId = Long.parseLong(txtbox.getValue().trim());
				if (optSyncPluginId <= 0) {

					MessageUtil.setMessage("Please Enter Plug-in ID. ","color:red", "TOP");
					return false;

				}// if
				
				String checkSize = optSyncPluginId +"";
				if(checkSize.length() == 16 ){
					logger.info("Its 16 digit number will check  its unquie ness");
					
				}
				else{
					MessageUtil.setMessage("Please provide Valid Plug-in ID  with 16 digit.","color:red", "TOP");
					logger.error("Not a 16 digit number"+ optSyncPluginId);
					return false;
				}
			}
			
			List<UpdateOptSyncData>	optSyncList =( List<UpdateOptSyncData>) updateOptSyncDataDao.findAllByOptSynId(optSyncPluginId);
			
			if(optSyncList == null  ){
				logger.info("Generated Plugin id is unquie "+optSyncPluginId);
				
			}
			else{
				if(sessionUpdateOptSyncData != null){
					
					if(sessionUpdateOptSyncData.getId().equals( optSyncList.get(0).getId())){
						logger.info("Editing Plugin id In DB ");
						return true;
					}
				}
				
				MessageUtil.setMessage(	"Entered Plug-in ID  already exists. Please enter Unique Plug-in ID . ","color:red;");
				generateAnchId.setVisible(true);
				pluginTbId.setReadonly(false);
				
				logger.error("DataIntegrityVoilation 16 digit number non unique number "+ optSyncPluginId);
				return false;
			}

			return true;
		} catch(NumberFormatException nfe){
			MessageUtil.setMessage(	"Please Enter Valid 16 digit Plug-in ID. ",	"color:red;");
			return false;
		}
		catch (Exception e) {

			logger.error("Exception  ::", e);
			return false;
		}

	}// validateNum(-)

	
public boolean validateOptSyncKey(String optSyncAuth ){
		
		logger.info("validating optsync key");
		boolean isTrue =false;
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
			/*	if (!validateAccountId(userOrgLbId)) {
					return false;
				}*/
				 
				String orgExternalId=GetUser.getUserObj().getUserOrganization().getOrgExternalId();
				long userOrgId=GetUser.getUserObj().getUserOrganization().getUserOrgId();
				
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


public void onClick$submitBtnId$opySyncAlertsWinId(){
	
	try {
		String onAlertsBy=Constants.ALERTS_SENDING_MANUALLY;
		if(opySyncAlertsWinId$automaticRgId.isChecked()){
			onAlertsBy =Constants.ALERTS_SENDING_AUTOMATICALLY;
		}else if(opySyncAlertsWinId$manualRgId.isChecked()){
			onAlertsBy = Constants.ALERTS_SENDING_MANUALLY;
		}
		
		UpdateOptSyncData updateOptSyncData =(UpdateOptSyncData) createWinId$opySyncAlertsWinId.getAttribute(Constants.OPTSYNC_OBJ);
		//int alertsCount =updateOptSyncDataDao.updateAlertSendingStatus(onAlertsBy, updateOptSyncData.getOptSyncId());
		int alertsCount =updateOptSyncDataDaoForDML.updateAlertSendingStatus(onAlertsBy, updateOptSyncData.getOptSyncId());
		logger.info("alerts count"+alertsCount);
		
		//int rowseffected = updateOptSyncDataDao.updatePluginStatus(OCConstants.OPT_SYNC_PLUGIN_STATUS_INACTIVE, updateOptSyncData.getOptSyncId());
		int rowseffected = updateOptSyncDataDaoForDML.updatePluginStatus(OCConstants.OPT_SYNC_PLUGIN_STATUS_INACTIVE, updateOptSyncData.getOptSyncId());
		if(rowseffected == 1)
		MessageUtil.setMessage("OptSync alerts paused successfully.","color:blue","TOP");
		Redirect.goTo(PageListEnum.EMPTY);
		Redirect.goTo(PageListEnum.USERADMIN_OPT_SYNC);
		opySyncAlertsWinId.setVisible(false);
		
		
		
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}

public void onClick$cancelBtnId$opySyncAlertsWinId(){
	opySyncAlertsWinId$automaticRgId.setChecked(true);
	opySyncAlertsWinId.setVisible(false);
}





/*//method to split the number in group of Four
private static String[] splitByNumber(String text, int number) {

    int inLength = text.length();
    int arLength = inLength / number;
    int left=inLength%number;
    if(left>0){++arLength;}
    String ar[] = new String[arLength];
        String tempText=text;
        for (int x = 0; x < arLength; ++x) {

            if(tempText.length()>number){
            ar[x]=tempText.substring(0, number);
            tempText=tempText.substring(number);
            }else{
                ar[x]=tempText;
            }

        }


    return ar;
}//String splitting
*/	


	}//Eof
	
	
	
	


