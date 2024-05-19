package org.mq.marketer.campaign.controller.useradmin;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.dialect.FirebirdDialect;
import org.mq.marketer.campaign.beans.Campaigns;
import org.mq.marketer.campaign.beans.DefaultCategories;
import org.mq.marketer.campaign.beans.EmailQueue;
import org.mq.marketer.campaign.beans.EventTrigger;
import org.mq.marketer.campaign.beans.SMSCampaigns;
import org.mq.marketer.campaign.beans.UserCampaignCategories;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.ActivityEnum;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.CampaignsDao;
import org.mq.marketer.campaign.dao.DefaultCategoriesDao;
import org.mq.marketer.campaign.dao.EmailQueueDao;
import org.mq.marketer.campaign.dao.EmailQueueDaoForDML;
import org.mq.marketer.campaign.dao.EventTriggerDao;
import org.mq.marketer.campaign.dao.SMSCampaignsDao;
import org.mq.marketer.campaign.dao.UserCampaignCategoriesDao;
import org.mq.marketer.campaign.dao.UserCampaignCategoriesDaoForDML;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.dao.UsersDaoForDML;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PageListEnum;
import org.mq.marketer.campaign.general.PageUtil;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Redirect;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.ForEach;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Column;
import org.zkoss.zul.Columns;
import org.zkoss.zul.Detail;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.event.PagingEvent;

public class SubscriberSettingsController extends GenericForwardComposer {
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);

	private Listbox itemsPerPageLBId;
	private Paging prferencesPerPagingId;
	private Grid prferenceGId;
	private Rows preferenceRowsId;
	private DefaultCategoriesDao defaultCategoriesDao;
	private UserCampaignCategoriesDao userCampaignCategoriesDao;
	private UserCampaignCategoriesDaoForDML userCampaignCategoriesDaoForDML;
	private Users currentUser;
	private UsersDao usersDao;
	private UsersDaoForDML usersDaoForDML;
	
	
	private CampaignsDao campaignsDao;
	private EventTriggerDao eventTriggerDao;
	private SMSCampaignsDao smsCampaignsDao;
	
	
	private EmailQueueDaoForDML emailQueueDaoForDML;
	 
	public SubscriberSettingsController(){
		
		userCampaignCategoriesDao = (UserCampaignCategoriesDao)SpringUtil.getBean("userCampaignCategoriesDao");
		userCampaignCategoriesDaoForDML = (UserCampaignCategoriesDaoForDML)SpringUtil.getBean("userCampaignCategoriesDaoForDML");
		usersDao = (UsersDao)SpringUtil.getBean("usersDao");
		usersDaoForDML = (UsersDaoForDML)SpringUtil.getBean("usersDaoForDML");
		defaultCategoriesDao =(DefaultCategoriesDao)SpringUtil.getBean("defaultCategoriesDao");
		
		campaignsDao = (CampaignsDao) SpringUtil.getBean("campaignsDao");
		eventTriggerDao = (EventTriggerDao) SpringUtil.getBean("eventTriggerDao");
		smsCampaignsDao = (SMSCampaignsDao)SpringUtil.getBean("smsCampaignsDao");
		
		currentUser = GetUser.getUserObj();
		
		String style = "font-weight:bold;font-size:15px;color:#313031;font-family:Arial,Helvetica,sans-serif;align:left";
		PageUtil.setHeader("Subscriber Preference Center","",style,true);
	}
	private boolean isEdit=false;
	 int totSize=0;
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		// TODO Auto-generated method stub
		super.doAfterCompose(comp);
		
		
		 if(currentUser.getSubscriptionEnable()){
				enableChkBoxId.setChecked(true);
			 }else{
				 enableChkBoxId.setChecked(false);
			 }
		// usersDao.saveOrUpdate(currentUser);
		List<UserCampaignCategories> userCategoriesList= userCampaignCategoriesDao.findByUserId(currentUser.getUserId());
		
		
		
		if(userCategoriesList == null || userCategoriesList.size() < 0){
			logger.debug("in default");
			List<DefaultCategories> defaultList = defaultCategoriesDao.findAll();
			totSize =defaultList.size();
			for (DefaultCategories defaultCategories : defaultList) {
				
				if(defaultCategories.getCategoryName().equals(Constants.CAMP_CATEGORY_TRANSACTIONAL))continue;
				
				Row row = new Row();
				
				//Set Category
				row.appendChild(new Label(defaultCategories.getCategoryName()));
				
				
				//set Description
				row.appendChild(new Label(defaultCategories.getDescription()));
				Checkbox chkbox= new Checkbox();
				chkbox.setChecked(true);
				row.appendChild(chkbox);
				
				
				Hbox hbox = new Hbox();
				
				Image img = new Image("/images/action_add.gif");
				img.setStyle("margin-right:10px;cursor:pointer;");
				
				img.setId(defaultCategories.getCategoryName()+"ImgId");
				
				img.setAttribute("imgEdit", "EDIT");
				
				img.addEventListener("onClick",this);
				
				img.setParent(hbox);
				hbox.setParent(row);
				
				
				UserCampaignCategories userCampaignCategories = new UserCampaignCategories(currentUser.getUserId());
				userCampaignCategories.setCreatedDate(Calendar.getInstance());
				userCampaignCategories.setCategoryName(defaultCategories.getCategoryName());
				userCampaignCategories.setDescription(defaultCategories.getDescription());
				userCampaignCategories.setType("Default");
				
				row.setValue(userCampaignCategories);
				
				row.setParent(preferenceRowsId);
			}
			
	}else{
		totSize = userCampaignCategoriesDao.findTotCount(currentUser.getUserId());
		logger.debug("in user settings"+totSize);
		redraw(userCategoriesList);
		isEdit = true;
	}
	
		prferencesPerPagingId.setActivePage(0);
		prferencesPerPagingId.setTotalSize(totSize);
		prferencesPerPagingId.addEventListener("onPaging", this);
		
	}
	
	public void redraw(List<UserCampaignCategories> userCatList){
		
		Components.removeAllChildren(preferenceRowsId);
		
		
		//List<UserCampaignCategories> userCamCterList = null;
		/*for (UserCampaignCategories userCampCatObj : userCatList) {
			
			if(!userCampCatObj.getType().equals("Custom")){
				Row tempRow  = createRow(userCampCatObj);
				tempRow.setValue(userCampCatObj);
				tempRow.setParent(preferenceRowsId);
			}
		}
		
		for (UserCampaignCategories userCampCatObj1 : userCatList) {
			
			if(userCampCatObj1.getType().equals("Custom")){
				boolean isRowcreated = false;
				Row tempRow  = createRow(userCampCatObj1);
				tempRow.setValue(userCampCatObj1);
				List chalidrowList = preferenceRowsId.getChildren();
				
				if(chalidrowList != null && chalidrowList.size() > 0) {
					for (Object object : chalidrowList) {
						
						Row chaildRow = (Row)object;
						UserCampaignCategories tempUserCampCatObj = chaildRow.getValue();
						
						if(!tempUserCampCatObj.getType().equals("Custom") 
								&& tempUserCampCatObj.getId() == userCampCatObj1.getParentPositionId()){
								preferenceRowsId.insertBefore(tempRow, chaildRow);
								isRowcreated = true;
						}else if(tempUserCampCatObj.getParentPositionId() == userCampCatObj1.getParentPositionId()){
							preferenceRowsId.insertBefore(tempRow, chaildRow);
							isRowcreated = true;
						}
					}
				}
				else {
					
					tempRow.setParent(preferenceRowsId);
					isRowcreated = true;
				}
					
				if(!isRowcreated) tempRow.setParent(preferenceRowsId);
				//tempRow.setParent(preferenceRowsId);
			}
		}*/
		
		Map<Long, List<UserCampaignCategories>> postioningMap = new HashMap<Long, List<UserCampaignCategories>>();
		List<UserCampaignCategories> userCampCatList = null;
		
		for (UserCampaignCategories userCampCatObj : userCatList) {
			if(userCampCatObj.getType().equals("Default") || userCampCatObj.getType().equals("CP")){
				if(postioningMap.containsKey(userCampCatObj.getId())){
					userCampCatList = postioningMap.get(userCampCatObj.getId());
					if(userCampCatList == null ) userCampCatList = new ArrayList<UserCampaignCategories>();
				}else {
					userCampCatList = new ArrayList<UserCampaignCategories>();
				}
				userCampCatList.add(userCampCatObj);
				postioningMap.put(userCampCatObj.getId(), userCampCatList);
			}else {
				if(postioningMap.containsKey(userCampCatObj.getParentPositionId())) {
					userCampCatList = postioningMap.get(userCampCatObj.getParentPositionId());
					if(userCampCatList == null ) userCampCatList = new ArrayList<UserCampaignCategories>();
				}else {
					userCampCatList = new ArrayList<UserCampaignCategories>();
				}
				userCampCatList.add(userCampCatObj);
				postioningMap.put(userCampCatObj.getParentPositionId(), userCampCatList);
			}
		} // for
		
		Set<Long> campCatSet = postioningMap.keySet();
		if(campCatSet == null && campCatSet.size() <= 0) return;
		
		for (Long eachId : campCatSet) {
			 userCampCatList =  postioningMap.get(eachId);
			
			 for (UserCampaignCategories eachObj : userCampCatList) {
				  Row rowObj = createRow(eachObj);
				  rowObj.setParent(preferenceRowsId);
				
			}
			
		}
		
		
		
		/*for (UserCampaignCategories userCampCatObj : userCatList) {
			
			
			if(!userCampCatObj.getType().equals("Custom")){
				if(postioningMap.containsKey(userCampCatObj.getId())) {
					userCamCterList = postioningMap.get(userCampCatObj.getParentPositionId());
					if(userCamCterList == null) userCamCterList = new ArrayList<UserCampaignCategories>();
					userCamCterList.add(userCampCatObj);
					postioningMap.put(userCampCatObj.getId(), userCamCterList);
					
				}else {
					userCamCterList= new ArrayList<UserCampaignCategories>();
					userCamCterList.add(userCampCatObj);
					postioningMap.put(userCampCatObj.getId(), userCamCterList);
				}
			}else{
				
				logger.info("userCampCatObj.getType() is  ::"+userCampCatObj.getType());
				logger.info("key existed is ::"+postioningMap.containsKey(userCampCatObj.getParentPositionId()));
				if(postioningMap.containsKey(userCampCatObj.getParentPositionId())) {
					userCamCterList = postioningMap.get(userCampCatObj.getParentPositionId());
					
					if(userCamCterList == null) userCamCterList = new ArrayList<UserCampaignCategories>();
					userCamCterList.add(userCampCatObj);
					postioningMap.put(userCampCatObj.getParentPositionId(), userCamCterList);
				}else {
					userCamCterList= new ArrayList<UserCampaignCategories>();
					userCamCterList.add(userCampCatObj);
					postioningMap.put(userCampCatObj.getParentPositionId(), userCamCterList);
				}
			}
			
				
			
		}// for
		
		logger.info("postioningMap is  :: "+postioningMap);
		
		 for (UserCampaignCategories userCampaignCategories : userCatList) {
			 
			 //if(userCampaignCategories.getType().equals("Custom")) continue;
			 
			 
			 //createRow(userCampaignCategories);
			 
			 if(postioningMap.containsKey(userCampaignCategories.getId())) {
				 userCamCterList = postioningMap.get(userCampaignCategories.getId());
				 for (UserCampaignCategories eachObj : userCamCterList) {
					 createRow(eachObj);
				}
			 }
			 	
			 
			 
			
		}*/
		
			
	 }//redraw
	
	
	private Row createRow(UserCampaignCategories userCampaignCategories){

		
		Row row = new Row();
	 	
		if(userCampaignCategories.getType().equals("Default")){
			row.appendChild(new Label(userCampaignCategories.getCategoryName()));
			row.appendChild(new Label(userCampaignCategories.getDescription()));
			
		}else{
			Textbox tempBox= new Textbox(userCampaignCategories.getCategoryName());
			tempBox.setDisabled(true);
			row.appendChild(tempBox);
			row.appendChild(new Textbox(userCampaignCategories.getDescription()));
		}
			
			Checkbox chkCheckbox = new Checkbox();
			if(userCampaignCategories.getIsVisible() ){
				chkCheckbox.setChecked(true);
			}else{
				chkCheckbox.setChecked(false);
			}
			
			row.appendChild(chkCheckbox);
			
			if(userCampaignCategories.getType().equals("Default") 
					|| userCampaignCategories.getType().equals("CP")){
				
				Hbox hbox = new Hbox();
				
				Image img = new Image("/images/action_add.gif");
				img.setId(userCampaignCategories.getCategoryName()+"ImgId");
				img.setStyle("margin-right:10px;cursor:pointer;");
				img.addEventListener("onClick",this);
				img.setAttribute("imgEdit", "EDIT");
				
				
				img.setParent(hbox);
				hbox.setParent(row);
			}
			
			row.setValue(userCampaignCategories);
			return row;
			//row.setParent(preferenceRowsId);
		
	}
	
	
	
	/*public void onSelect$itemsPerPageLBId() {
		try {
			
			
			int count = totSize;
			prferencesPerPagingId.setTotalSize(count);
			int n =Integer.parseInt(itemsPerPageLBId.getSelectedItem().getLabel().trim());
			prferencesPerPagingId.setPageSize(n);
			
			List<UserCampaignCategories> userCategoriesList= userCampaignCategoriesDao.findByUserId(currentUser.getUserId(),0,n);
					 
			redraw(userCategoriesList);
			
			
		} catch (Exception e) {
			logger.error("Exception ::", e);
		}
	}//onSelect$storesPerPageLBId()
*/ 
 public void onClick$subsAncId(){
	 /*we are not going to check the Subscription is enable or not
	  **/
	 /*if(currentUser.getSubscriptionEnable()){
		 
		 Redirect.goTo(PageListEnum.USERADMIN_VIEW_PREFERENCE_SETTINGS);
	 }else{
		 
		 MessageUtil.setMessage("Please enable subscriber preference center to preview the page.", "color:red");
	 }*/
	 Redirect.goTo(PageListEnum.USERADMIN_VIEW_PREFERENCE_SETTINGS);
 }
 
 
 
 public void onClick$addCategoryTBarBtnId(){
	
	Row  tempRow =createRow(true);
	
	UserCampaignCategories userCampaignCategories = new UserCampaignCategories(currentUser.getUserId());
	userCampaignCategories.setCreatedDate(Calendar.getInstance());
	userCampaignCategories.setType("CP");
	tempRow.setValue(userCampaignCategories);
	
	tempRow.setParent(preferenceRowsId);
	
 }
 
 private Checkbox enableChkBoxId;
 private List<Campaigns> campaignList;
 
 public void onClick$saveBtnId(){
	 UserCampaignCategories userCampaignCategories = null;
	 List<UserCampaignCategories> userSubsList = new LinkedList<UserCampaignCategories>();
	 String emailId= currentUser.getEmailId();
	 StringBuffer sb = null;
	 String message = PropertyUtil.getPropertyValueFromDB("Subscription_Preference_Alert");

	
	/* String message = "<html><head></head><body><font style=\" font-family: Arial ; font-size: 15; line-height: 150%;word-spacing: normal ;\">"
				+ "Hi [fname],<br /><br /><P>We noticed that you recently activated Subscriber Preference Center feature in your OptCulture account. "
				+ "In order for this feature to function properly for currently running email/SMS campaigns and/or Event triggers,please add "
				+ "&#39;Campaign Category&#39; setting to them. </p><br />"
				+ "<p>Following is the list of your actively running email/SMS communication that need this setting:<p/>"
				+ "[Active_CampaignsList_WithOut_Category]"
				+ "<br /><p>Account details:"
				+ "<p/>Log-in URL: <a href=\"https://app.optculture.com\">https://app.optculture.com</a><br/>Username : [username]<br />Organization : [organization]<br /><br />"
				+ "<p>In case of any queries on this feature or anything else on the application, please reach out to us at <a href=\"mailto:teamCS@optculture.com\">teamCS@optculture.com.</a><p/>"
				+ "<br/><br/>Regards,<br />Team OptCulture</font></body></html>";
	*/ 
	 
	 //<html><head></head><body><font style="font-family: Arial ; font-size: 15; line-height: 150%;word-spacing: normal;">Hi [fname],<br /><br /><P>We noticed that you recently activated Subscriber Preference Center feature in your OptCulture account. In order for this feature to function properly for currently running email/SMS campaigns and/or Event triggers,please add &#39;Campaign Category&#39; setting to them. </p><br /><p>Following is the list of your actively running email/SMS communication that need this setting:<p/>[Active_CampaignsList_WithOut_Category]<br /><p>Account details:<p/>Log-in URL: <a href="https://app.optculture.com">https://app.optculture.com</a><br/>Username : [username]<br/>Organization : [organization]<br/><br /><p>In case of any queries on this feature or anything else on the application, please reach out to us at <a href="mailto:teamCS@optculture.com">teamCS@optculture.com.</a><p/><br/><br/>Regards,<br />Team OptCulture</font></body></html>
	   
	 currentUser.setSubscriptionEnable(enableChkBoxId.isChecked());
	 //usersDao.saveOrUpdate(currentUser);
	 usersDaoForDML.saveOrUpdate(currentUser);
	 
	 try{
		 //message = message.replace("[username]", Utility.getOnlyUserName(user.getUserName()));
			//message = message.replace("[organization]", Utility.getOnlyOrgId(user.getUserName()));
		 
		 if(enableChkBoxId.isChecked()){	

			 String statusStr = "Active";
			 
			 List<Campaigns> campList = campaignsDao.findCampaignByUserIdAndStatusActiveRunning(currentUser.getUserId());
			 List<EventTrigger> eventTriggerList = eventTriggerDao.findByUserIdActiveStatus(currentUser.getUserId(),statusStr);
			 List<SMSCampaigns> smsCampaign = smsCampaignsDao.getSMSCampaignsByUserIDStatus(currentUser.getUserId(), statusStr);
				
			 if((campList!=null && campList.size()>0) || (eventTriggerList!=null && eventTriggerList.size()>0) || (smsCampaign!=null && smsCampaign.size()>0))
			 {
				 sb = prepareActiveCampaignListHtml(campList,eventTriggerList,smsCampaign);
				 
				 message = message.replace("[Active_CampaignsList_WithOut_Category]", (sb == null ? "" : sb.toString()));
				 
				 message = message.replace("[ApplicationUrl]", PropertyUtil.getPropertyValueFromDB("ApplicationUrl"));

				 
				 message = message.replace("[fname]", currentUser.getFirstName());
				 message = message.replace("[username]", Utility.getOnlyUserName(currentUser.getUserName()));
				 message = message.replace("[organization]", Utility.getOnlyOrgId(currentUser.getUserName()));
				 emailQueueDaoForDML = (EmailQueueDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.EMAILQUEUE_DAO_ForDML);
				 EmailQueue eqObj = new EmailQueue("OptCulture Alert: Attention needed on newly added feature settings",message, Constants.EQ_TYPE_SUPPORT_ALERT,"Active",emailId,MyCalendar.getNewCalendar(), currentUser);
				 emailQueueDaoForDML.saveOrUpdate(eqObj);
				 
			 }
				 
				 
			 /*StringBuffer campaignNamesbuffer = new StringBuffer();
			 StringBuffer eventTriggerNamesbuffer = new StringBuffer();
			 StringBuffer smsCampaignNamesbuffer = new StringBuffer();
			 
			 for(Campaigns campaign: campList){
				 if(campaignNamesbuffer.length() > 0) campaignNamesbuffer.append(",");
				 campaignNamesbuffer.append(campaign.getCampaignName());
			 }
			 
			 if(campaignNamesbuffer.length() > 0){
				 message = message.replace("[campaignName]", campaignNamesbuffer.toString());
			}
			 
			 List<EventTrigger> eventTriggerList = eventTriggerDao.findByUserIdActiveStatus(currentUser.getUserId(),statusStr);
				
			 for(EventTrigger eTList: eventTriggerList){
				 if(eventTriggerNamesbuffer.length() > 0) eventTriggerNamesbuffer.append(",");
				 eventTriggerNamesbuffer.append(eTList.getTriggerName());
			 }
			 if(eventTriggerNamesbuffer.length() > 0){
				 message = message.replace("[eventTrigger]", eventTriggerNamesbuffer);
				 
			 }
			 
			 
			List<SMSCampaigns> smsCampaign = smsCampaignsDao.getSMSCampaignsByUserIDStatus(currentUser.getUserId(), "Active");
				
			 for(SMSCampaigns smsCampList: smsCampaign){
				 if(smsCampaignNamesbuffer.length() > 0) smsCampaignNamesbuffer.append(",");
				 smsCampaignNamesbuffer.append(smsCampList.getSmsCampaignName());
			 }
			 if(smsCampaignNamesbuffer.length()>0){
				 message = message.replace("[smsCampaignName]", smsCampaignNamesbuffer);
			 }*/
			  
			 /*emailQueueDaoForDML = (EmailQueueDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.EMAILQUEUE_DAO_ForDML);
			 EmailQueue eqObj = new EmailQueue("Subscribtion Preferrence Alerts",message, Constants.SPC_ALERT,"Active",emailId,MyCalendar.getNewCalendar(), currentUser);
			 emailQueueDaoForDML.saveOrUpdate(eqObj);*/
		 }

				
				
			
			
		}
		catch(Exception e){
			logger.error("** Exception : ", e);
		}
	 
	 
	 List chaildRowList = preferenceRowsId.getChildren();
		
	 if(chaildRowList == null || chaildRowList.size() == 0) {
			MessageUtil.setMessage("No data available for saving.","color:red","TOP");
			return;
	 }
	 
	//Validation of subscription  setting
	for (Object object : chaildRowList) {
		Row tempRow  =(Row)object;
		
		if(tempRow.getFirstChild() instanceof Label) continue;
		
		if(subscrptionSettingsValidation(tempRow) == false) {
			return;
		}
	}
	 
	
	//Saving Object into DB
	//Textbox tempTextbox = null;
	Checkbox visibleChkBox = null;
	Long parentId = 0l;
	for (Object object : chaildRowList) {
		Row tempRow  =(Row)object;
		
		userCampaignCategories = (UserCampaignCategories)tempRow.getValue();
		
		
		
		
		
		boolean isNew = false;
		if(userCampaignCategories == null) {
			userCampaignCategories = new UserCampaignCategories(currentUser.getUserId());
			userCampaignCategories.setCreatedDate(Calendar.getInstance());
			userCampaignCategories.setType("Custom");
			isNew = true;
		}
		List chaildLblList = tempRow.getChildren();
		
		visibleChkBox = (Checkbox)chaildLblList.get(2);
		
		if(tempRow.getFirstChild() instanceof Textbox ) {
			
			/*//Set Category
			userCampaignCategories.setCategoryName(((Label)chaildLblList.get(0)).getValue());
			
			//set Description
			userCampaignCategories.setDescription(((Label)chaildLblList.get(1)).getValue());
			userCampaignCategories.setType("Default");
		}else{*/
			
			//Set Category
			userCampaignCategories.setCategoryName(((Textbox)chaildLblList.get(0)).getValue());
			((Textbox)chaildLblList.get(0)).setDisabled(true);
			//set Description
			userCampaignCategories.setDescription(((Textbox)chaildLblList.get(1)).getValue());
		}
		//set Visiblity 
		userCampaignCategories.setVisible(visibleChkBox.isChecked());
		
		if(userCampaignCategories.getId() != null && 
				(userCampaignCategories.getType().equals("Default") || userCampaignCategories.getType().equals("CP")) ) {
			parentId = userCampaignCategories.getId();
		}
		
		if(isNew && parentId != 0l && userCampaignCategories.getType().equals("Custom")){
			
			userCampaignCategories.setParentPositionId(parentId);
		}
		
		/*if(parentId != 0l && !userCampaignCategories.getType().equals("Default"))
								userCampaignCategories.setParentPositionId(parentId);*/
		
		
		
		userCampaignCategoriesDaoForDML.saveOrUpdate(userCampaignCategories);
		
		if(userCampaignCategories.getType().equals("Default") ||  userCampaignCategories.getType().equals("CP")) {
			parentId = userCampaignCategories.getId();
		}
		
		if(tempRow.getValue() == null)tempRow.setValue(userCampaignCategories);
		
//		parentId = 0l;
		
		
	} //for
	
	MessageUtil.setMessage("Settings saved successfully.", "color:blue");
	
	
	
	
	
	
	
/*	int confirm = Messagebox.show("Are you sure you want to save the  settings? ", "Prompt", 
			 Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
	if(confirm == Messagebox.OK) {
		
		userCampaignCategoriesDao.saveByCollection(userSubsList);
		
		
		MessageUtil.setMessage("Settings saved successfully", "color:blue");
		
		
	}*/
	
	 /*
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 //Old Scenario
	 
		if(chaildRowList == null || chaildRowList.size() == 0) {
			MessageUtil.setMessage("No data available for saving.","color:red","TOP");
			return;
			
		}
		
		//Validation of subscription  setting
		for (Object object : chaildRowList) {
			Row tempRow  =(Row)object;
			
			if(tempRow.getFirstChild() instanceof Label) continue;
			
			if(subscrptionSettingsValidation(tempRow) == false) {
				return;
			}
			
		}
		
		Textbox tempTextbox = null;
		Checkbox visibleChkBox = null;
		for (Object object : chaildRowList) {
			Row tempRow  =(Row)object;
			
			userCampaignCategories = (UserCampaignCategories)tempRow.getValue();
			List chaildLblList = tempRow.getChildren();
			// visible
			visibleChkBox = (Checkbox)chaildLblList.get(2);
			
			if(tempRow.getFirstChild() instanceof Label ) {
				
				 if(userCampaignCategories == null) {
					
					userCampaignCategories = new UserCampaignCategories();
					userCampaignCategories.setCreatedDate(Calendar.getInstance());
					userCampaignCategories.setType("Default");
				}else if(userCampaignCategories != null && 
						userCampaignCategories.getIsVisible() == visibleChkBox.isChecked()){
					continue;
				}			
				
			}else if(tempRow.getFirstChild() instanceof Textbox) {
				
				if(userCampaignCategories == null) {
					userCampaignCategories = new UserCampaignCategories();
					userCampaignCategories.setCreatedDate(Calendar.getInstance());
					userCampaignCategories.setType("Custom");
					
				}
				else {
					userCampaignCategories = (UserCampaignCategories)tempRow.getValue();
				}
				
			}
			
			
			
			
			//category
			String tempStr = "";
			if(chaildLblList.get(0) instanceof Label) {
				tempStr = ((Label)chaildLblList.get(0)).getValue();
			}else {
				tempStr =((Textbox)chaildLblList.get(0)).getValue();
			}
//			tempTextbox = (Textbox)chaildLblList.get(0);
			
			userCampaignCategories.setCategoryName(tempStr.trim());
			
			//Decscription
			if(chaildLblList.get(1) instanceof Label) {
				tempStr = ((Label)chaildLblList.get(1)).getValue();
			}else {
				tempStr =((Textbox)chaildLblList.get(1)).getValue();
			}
//			tempTextbox = (Textbox)chaildLblList.get(1);
			userCampaignCategories.setDescription(tempStr.trim());
			
			
			if(!visibleChkBox.isChecked()){
				userCampaignCategories.setIsVisible(false);	
			}else{
				userCampaignCategories.setIsVisible(true);
			}
			logger.debug("userCampaignCategories ::"+userCampaignCategories.getCategoryName()+
					" is visible"+userCampaignCategories.getIsVisible());
			
			
			
			
			
			
			
			userCampaignCategories.setUserId(currentUser.getUserId());
			
			
			
			if(tempRow.getValue() == null)tempRow.setValue(userCampaignCategories);
			
			userSubsList.add(userCampaignCategories);
			
		}
		
		int confirm = Messagebox.show("Are you sure you want to save the  settings? ", "Prompt", 
				 Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
		if(confirm == Messagebox.OK) {
			
			userCampaignCategoriesDao.saveByCollection(userSubsList);
			
			
			MessageUtil.setMessage("Settings saved successfully", "color:blue");
			
			
		}*/
	 
	 
 }
 
 private StringBuffer prepareActiveCampaignListHtml(List<Campaigns> campList,List<EventTrigger> eventTriggerList,List<SMSCampaigns> smsCampaign){
		
	 StringBuffer sb = new StringBuffer();
	 
	 StringBuffer campaignNamesbuffer = new StringBuffer();
	 StringBuffer eventTriggerNamesbuffer = new StringBuffer();
	 StringBuffer smsCampaignNamesbuffer = new StringBuffer();
	 
	 sb.append("<table style=\"width:50%;margin-left:20px;border-collapse:collapse;\"><tr style=\"background:#CA0000;color:#ffffff;\">"
	 		+ "<th style=\"border:1px solid  #D1D6DA;padding:5px;font-size:13;text-align:center;\">Communication Type</th>"
	 		+ "<th style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Communication Name</th></tr>");
	 
	 sb.append("<tr><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Email Campaign</td>");
	 
	 if(campList==null || campList.size()<=0){
			sb.append("<td colspan=\"6\" style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">--</td>");
		}
		else {
			sb.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">");
			for(Campaigns campaign: campList){
				sb.append(campaign.getCampaignName()+"<br/>");
			}
			sb.append("</td>");
		}
	 sb.append("<tr><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">SMS Campaign</td>");
	 if(smsCampaign==null || smsCampaign.size()<=0){
			sb.append("<td colspan=\"6\" style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">--</td>");
		}
		else {
			sb.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">");
			for(SMSCampaigns smsCampList: smsCampaign){
				sb.append(smsCampList.getSmsCampaignName()+"<br/>");
			}
			sb.append("</td>");
		}
	
	 /*sb.append("<tr><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">SMS Campaign</td>");
	 if(smsCampaign==null || smsCampaign.size()<=0){
			sb.append("<td colspan=\"6\" style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">--</td>");
		}
		else {
			sb.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">");
			for(SMSCampaigns smsCampList: smsCampaign){
				sb.append(smsCampList.getSmsCampaignName()+"<br/>");
			}
			sb.append("</td>");
		}*/
	 
	 sb.append("<tr><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Event Trigger</td>");
	 if(eventTriggerList==null || eventTriggerList.size()<=0){
			sb.append("<td colspan=\"6\" style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">--</td>");
		}
		else {
			sb.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">");
			for(EventTrigger eTList: eventTriggerList){
				sb.append(eTList.getTriggerName()+"<br/>");
			}
			sb.append("</td>");
		}
	 
	if(sb.length() > 0){
			sb.append("</table>");
		}
		return sb;
 }
 
 
 
	private boolean subscrptionSettingsValidation(Row tempRow) {

		
		 try {
			List rowList = tempRow.getChildren();
			
			 
			//Category name
			 Textbox tempTextbox = null;
			 tempTextbox = (Textbox)rowList.get(0);
			 tempTextbox.setStyle("border:1px solid #7F9DB9;");
			 
			 if(tempTextbox.getValue().trim().equals("")) {
				 tempTextbox.setStyle("border:1px solid #DD7870;");
				 MessageUtil.setMessage("Please provide category name.","color:red","TOP");
				 return false;
			 }
			
			//Description
			 tempTextbox = (Textbox)rowList.get(1);
			 tempTextbox.setStyle("border:1px solid #7F9DB9;");
			 
			 if(tempTextbox.getValue().trim().equals("")) {
				 tempTextbox.setStyle("border:1px solid #DD7870;");
				 MessageUtil.setMessage("Please provide category description.","color:red","TOP");
				 return false;
			 }
			 
			 
				return true;
			} catch (Exception e) {
				logger.error("Exception ::", e);
				return false;
			}
	}
 
 public Row createRow(boolean flag){
	 Row tempRow = new Row();
		
		// category
		Textbox categoryTextBox;
		categoryTextBox = new Textbox();
		categoryTextBox.setParent(tempRow);
		
		//Decscription
		
		Textbox descTextBox;
		descTextBox = new Textbox();
		descTextBox.setParent(tempRow);
		descTextBox.setCols(30);
		
		// visible
		Checkbox visibleChkBox;
		visibleChkBox = new Checkbox();
		visibleChkBox.setChecked(true);
		visibleChkBox.setParent(tempRow);
		
		
		//Image
		if(flag){
			Hbox hbox = new Hbox();
			Image img = new Image("/images/action_add.gif");
			img.setStyle("margin-right:10px;cursor:pointer;");
			img.addEventListener("onClick",this);
			img.setAttribute("imgEdit", "EDIT");
			img.setParent(hbox);
			hbox.setParent(tempRow);
		}
		
		tempRow.setValue(null);
		
		return tempRow;
	 
 }
 
  	//private Map<Object, Rows> rowsMap = new HashMap<Object, Rows>();
 
	@Override
	public void onEvent(Event event) throws Exception {

		// TODO Auto-generated method stub
		super.onEvent(event);
		
		
		if(event.getTarget() instanceof Image) {
			
			Image img =(Image)event.getTarget();
			Row	parentRow = (Row)img.getParent().getParent();
			logger.info("is Row :: "+preferenceRowsId.getLastChild());
			logger.info("last Row index :: "+ ((Row)preferenceRowsId.getLastChild()).getIndex());
			logger.info("is parentRow index  :: "+parentRow.getIndex());
			logger.info("is true  "+(((Row)preferenceRowsId.getLastChild()).getIndex() == parentRow.getIndex()));
			
//			if(true) return;
			if(((Row)preferenceRowsId.getLastChild()).getIndex() == parentRow.getIndex()) {
				Row	tempRow =createRow(false);
				tempRow.setParent(preferenceRowsId);
				return;
			}
			
			
			List chaildRowList =  preferenceRowsId.getChildren();
			int index = 0;
			for (Object object : chaildRowList) {
				Row temprow = (Row)object;
				if(parentRow.getIndex() == temprow.getIndex()) break;
				index ++;
			}
			logger.debug("is index"+index);
			
			String imgAction = (String)img.getAttribute("imgEdit");
			if(imgAction.equals("EDIT")){
				
				//tempRow.setParent(preferenceRowsId);
				int insertIndex = 0;
				//Components.removeAllChildren(preferenceRowsId);
				logger.info("chaildRowList size ::: "+chaildRowList.size());
				
				for (Object object : chaildRowList) {
					
					
					Row temprow = (Row)object;
					logger.info("insertIndex is :: "+insertIndex);
					if(insertIndex > index){
						
						Row	tempRow =createRow(false);
						logger.debug("is macthed"+insertIndex);
						
						
						preferenceRowsId.insertBefore(tempRow, temprow);
						
						//temprow.beforeChildAdded(tempRow, temprow);
						//temprow.setParent(preferenceRowsId);
						break;
					}
					
					insertIndex++;
				}
				
				
				
			}
		}
		/*else if(event.getTarget() instanceof Paging) {
			
			
			Paging paging = (Paging)event.getTarget();
			
			int desiredPage = paging.getActivePage();
			
			
			
		
			PagingEvent pagingEvent = (PagingEvent) event;
			int pSize = pagingEvent.getPageable().getPageSize();
			int ofs = desiredPage * pSize;
			List<UserCampaignCategories> userCategoriesList= userCampaignCategoriesDao.findByUserId(currentUser.getUserId(),ofs,(byte) pagingEvent.getPageable().getPageSize());
			redraw(userCategoriesList);
			
			
			
			
		}*/
	}
 
	public void onClick$cancelBtnId(){
		Redirect.goTo(PageListEnum.EMPTY);
		Redirect.goTo(PageListEnum.USERADMIN_SUBSCRIBER_SETTINGS);
		
		
	}
 
}
