package org.mq.marketer.campaign.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.Campaigns;
import org.mq.marketer.campaign.beans.CountryReceivingNumbers;
import org.mq.marketer.campaign.beans.LoyaltyProgram;
import org.mq.marketer.campaign.beans.MailingList;
import org.mq.marketer.campaign.beans.SegmentRules;
import org.mq.marketer.campaign.beans.UpdateOptSyncData;
import org.mq.marketer.campaign.beans.UserOrganization;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.beans.UsersDomains;
import org.mq.marketer.campaign.controller.admin.EditUserController;
import org.mq.marketer.campaign.dao.CountryReceivingNumbersDao;
import org.mq.marketer.campaign.dao.MailingListDao;
import org.mq.marketer.campaign.dao.SegmentRulesDao;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.dao.UsersDomainsDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PageListEnum;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Redirect;
import org.mq.marketer.campaign.general.RightsEnum;
import org.mq.marketer.campaign.general.SMSStatusCodes;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.business.loyalty.LoyaltyProgramService;
import org.mq.optculture.model.opySync.OptSyncService;
import org.mq.optculture.utils.OCConstants;
import org.zkoss.spring.security.SecurityUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.BookmarkEvent;
import org.zkoss.zk.ui.event.ClientInfoEvent;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Bandbox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Menu;
import org.zkoss.zul.Menubar;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Menupopup;
import org.zkoss.zul.Style;
import org.zkoss.zul.Window;

public class HomeController extends GenericForwardComposer {

	private Session session;
	private Campaigns campaign;
	Style style1Id, style2Id;
	Menu coupMenuId,userAdminMenuId;
	private Window browserWinId;
	private Label homeHeaderUserLableRightId;
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	private   final String SMS_MSG_TEXT = "You have not opted for SMS, Please contact admin.";
	private   final String WA_MSG_TEXT = "You have not opted for WhatsApp, Please contact admin.";
	private final String SMS_MSG_KEYWORD_TEXT = "You have not opted for SMS Keywords, Please contact admin.";
	
	private MailingListDao mailingListDao ;
	private SegmentRulesDao segmentRulesDao ;
	private UsersDao usersDao ;
	private Users currentUser;
	
	 	
	public HomeController() {
		//logger.info(".....just entered in Home controller....");
		session = Sessions.getCurrent();
		campaign = (Campaigns)session.getAttribute("campaign");
		mailingListDao = (MailingListDao)SpringUtil.getBean("mailingListDao");
		segmentRulesDao = (SegmentRulesDao)SpringUtil.getBean("segmentRulesDao");
		usersDao = (UsersDao)SpringUtil.getBean("usersDao");
	}
	
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		// TODO Auto-generated method stub
		super.doAfterCompose(comp);

		logger.debug("style1Id="+style1Id.getSrc());
		logger.debug("style2Id="+style2Id.getSrc());
		
		style1Id.setSrc(null);
		style2Id.setSrc(null);

		//String versionStr = PropertyUtil.getPropertyValueFromDB("version");
		UserOrganization userOrg = GetUser.getUserObj().getUserOrganization(); 
		String branding = userOrg.getBranding();

		if(branding!=null && branding.equalsIgnoreCase("CAP")) {
			style1Id.setSrc("/css/style.css");
			style2Id.setSrc("/css/zk_overrides.css");
		}
		else {
			style1Id.setSrc("/css/style.css");
			style2Id.setSrc("/css/opt_zk_overrides.css");
		}
		
		
		logger.debug("getCampaign>>>>>>>>>>>>"+campaign);
		GetUser.getInfoFromRequest();
		String sesId = ((HttpSession)session.getNativeSession()).getId();
		if( ActiveUsers.sessions.contains(sesId) ) {
			logger.info("the Session id is====>"+sesId+" "+GetUser.getUserObj());
			ActiveUsers.activeUsersMap.put(sesId, GetUser.getUserObj());
			logger.info("the size of active users are===>"+ActiveUsers.activeUsersMap.size()+GetUser.getUserName());
		}
		
		
		checkMQS();
		sessionScope.put("isAdmin",SecurityUtil.isAllGranted(RightsEnum.Menu_Adminstrator_VIEW.name()));
		


		if(session.getAttribute("clientTimeZone")!=null) {
			setupIndexPAge();
		}
		
		logger.info("browser : "+ ((HttpServletRequest)Executions.getCurrent().getNativeRequest()).getHeader("User-Agent") );
		
		String browser  = ((HttpServletRequest)Executions.getCurrent().getNativeRequest()).getHeader("User-Agent");
		
		logger.info("Browser name : "+ browser);
		logger.info("Server Name : "+ Executions.getCurrent().getServerName());
		logger.info("Server Port: "+ Executions.getCurrent().getServerPort());
		
				
		if((browser != null && browser.indexOf( "MSIE" ) == -1 ) && (browser != null && browser.indexOf("Firefox") == -1) && (browser != null && browser.indexOf( "Chrome" ) == -1 ) && (browser !=null && browser.indexOf("") == -1)) {
			
			logger.info("Browser name : "+ browser);
			browserWinId.setVisible(true);
			browserWinId.setPosition("center");
			browserWinId.doHighlighted();
		}
		
		
		
		session.setAttribute("userFilter", usersBandBoxId);
		session.setAttribute("userlabelDiv", userLblDivId);
		homeHeaderUserLableRightId.setValue("My Items & Shared Items");
		
		if(GetUser.getUserObj().getAccountType().equals(Constants.USER_ACCOUNT_TYPE_SHARED)) {
			
			homeHeaderUserLableRightId.setValue("My Items");
		}
		
     // manage menu hiding under My account	
		if(manageusersMenuItemId == null && organizationStoresMenuItemId == null){
			if(manageMenuId != null )manageMenuId.setVisible(false);
		}
		
		//settings menu hiding under My account
		if((posSettingsMenuItemId == null && listSettingsMenuItemId == null) ||
				(bcrmSettingsMenuItemId == null && listSettingsMenuItemId == null)) {
			if(settingsMenuId != null )settingsMenuId.setVisible(false);
		}
		
		//TODO need to make visible depend on the client type
		// To make Transfer Card visible to store operators only
		Users user = GetUser.getUserObj();
		if(user!=null && user.getUserId()!=null) {
			Long isOCAdmin = PropertyUtil.getisOCAdminCache(user.getUserId());
			 if(isOCAdmin!=null) {
				 session.setAttribute("isPasswordRequired", true);
				 session.setAttribute("currentOCAdmin", isOCAdmin);
				 PropertyUtil.isOCAdminCache.remove(user.getUserId());
			 }
		}
		
	} //
	
	
	private Menu manageMenuId,settingsMenuId;
	public void onCreate$indexWinId() {
		
		UserOrganization userOrg = GetUser.getUserObj().getUserOrganization(); 
		String orgType = userOrg.getClientType();
		
		if(orgType == null) {
			logger.debug("got client type is null....");
			return;
		}//if
		
		makeDispMenuItems(orgType);
		
	}
	
	private Menu assetsMenuId;
	public void makeDispMenuItems(String clientType) {
		
		try {
			if(clientType.equals(Constants.CLIENT_TYPE_BCRM) ) {
				
				if(homesPassedSegmentsMenuItemId != null)homesPassedSegmentsMenuItemId.setVisible(true);
				if(manageBcrmSegmentsMenuItemId != null) {
					
					manageBcrmSegmentsMenuItemId.setVisible(true);
					
				}
				
				if(parentalConsentMenuItemId != null ) {
					
					//parentalConsentMenuItemId.setVisible(false);
				}
				if(bcrmSettingsMenuItemId != null)bcrmSettingsMenuItemId.setVisible(true);
				
				//if(assetsMenuId != null)assetsMenuId.setVisible(false);
				//if(smsCampaignReportsMenuItemId != null)smsCampaignReportsMenuItemId.setVisible(false);
				//if(smsKeywordUsageReportsMenuItemId != null)smsKeywordUsageReportsMenuItemId.setVisible(false);
				if(optIntelReportsMenuItemId != null)optIntelReportsMenuItemId.setVisible(false);
				if(viewLoyaltyCardsMenuItemId != null)viewLoyaltyCardsMenuItemId.setVisible(false);
				//if(couponReportsMenuItemId != null)couponReportsMenuItemId.setVisible(false);
				//if(couponMenuItemId != null)couponMenuItemId.setVisible(false);
				/*
				if(digitalRecieptsMenuItemId != null)digitalRecieptsMenuItemId.setVisible(false);
			//	bcrmSettingsMenuItemId.setVisible(true);
				if(smsKeywordUsageReportsMenuItemId != null)smsKeywordUsageReportsMenuItemId.setVisible(false);
				if(optIntelReportsMenuItemId != null)optIntelReportsMenuItemId.setVisible(false);
				
				if(userAdminMenuId !=null) userAdminMenuId.setVisible(false);
				if(coupMenuId !=null) coupMenuId.setVisible(false);
				if(eventTriggersMenuItemId !=null) eventTriggersMenuItemId.setVisible(false);
				if(myImagesMenuItemId != null) myImagesMenuItemId.setVisible(false);
				if(couponReportsMenuItemId != null) couponReportsMenuItemId.setVisible(false);*/
				
			}else if(clientType.equals(Constants.CLIENT_TYPE_POS)) {
				
				if( manageSegmentsMenuItemId != null) manageSegmentsMenuItemId.setVisible(true);
				
				if(parentalConsentMenuItemId != null ) 	parentalConsentMenuItemId.setVisible(true);
				
				if(digitalRecieptsMenuItemId!=null) digitalRecieptsMenuItemId.setVisible(true);
				//APP-947
				if(templatesMenuItemId!=null) templatesMenuItemId.setVisible(true);
				
				
				if(mySegmentsMenuItemId!=null) mySegmentsMenuItemId.setVisible(true);
				if(posSettingsMenuItemId!=null) posSettingsMenuItemId.setVisible(true);
				
			}
			
			
			
			
			
			//Users currUser= GetUser.getUserObj();
			//if(currentUser.getCountryType().equals(Constants.SMS_COUNTRY_INDIA)){
			
			if(currentUser == null)	currentUser = GetUser.getUserObj();
			if(currentUser.isEnableSMS()) {
				Boolean retVal = SMSStatusCodes.optInMap.get(currentUser.getCountryType());
				if(retVal != null){
						
					if( retVal == true ){
					
						if(optinSmsSettingsMenuItemId != null)optinSmsSettingsMenuItemId.setVisible(true);
						if(SmsSettingsMenuItemId != null)SmsSettingsMenuItemId.setVisible(false);
						
					}else{
						
						if(optinSmsSettingsMenuItemId != null)optinSmsSettingsMenuItemId.setVisible(false);
						if(SmsSettingsMenuItemId != null)SmsSettingsMenuItemId.setVisible(true);
						
					}
				}
			}
			//}else if(currentUser.getCountryType().equals(Constants.SMS_COUNTRY_US)){
				
			/*else if(!SMSStatusCodes.optInMap.get(Constants.SMS_COUNTRY_US)){
				
				if(optinSmsSettingsMenuItemId != null)optinSmsSettingsMenuItemId.setVisible(false);
				if(SmsSettingsMenuItemId != null)SmsSettingsMenuItemId.setVisible(true);
				
			}*/
				
			//}
			
			/**
			 * For zone user digital receipt reports with zone filter
			 * For non-zone user digital receipt reports with zone filter
			 */
			  
			if(currentUser!=null && currentUser.isZoneWise() && eReceiptMenuItemId!=null && zoneAdmingMenuItemId!=null ) {
				eReceiptMenuItemId.setVisible(true);
				zoneAdmingMenuItemId.setVisible(true);
			}else if(digitalReceiptMenuItemId!=null ){
				digitalReceiptMenuItemId.setVisible(true);
			}
			
			
			
		} catch (Exception e) {
			logger.error("Exception ::",e);
		}
		
	}
	
	
	
	
	public void onBookmarkChange$indexWinId(BookmarkEvent event) {
		try {
			goToPage(event.getBookmark());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("** Exception bookmark change");
		}
	}
	
	
	
	public void setupIndexPAge() {
		
		/**********Zscript code start**********/
		String currentPage = (String)sessionScope.get("currentPage"); // (String)desktop.getBookmark();
	   	if(currentPage!=null && currentPage.trim().length()!= 0) {
	  		//indexWinId.goToPage(currentPage);
	  		Redirect.goTo(currentPage);
  		} else {
  			//indexWinId.goToPage("RMHome");
  			Redirect.goTo(PageListEnum.RM_HOME);
  		}
		/*******Zscript code end*******/
        /**
         * DashBoard Menu will be disabled if the role is StoreOpWebRedemption 
         */

       try {
           if(session.getAttribute("userRoleSet") instanceof Set) {
               Set<String> userRoleSet = (Set<String>)session.getAttribute("userRoleSet");
               
               logger.info("userRoleSet.contains(RightsEnum.MenuItem_Loyalty_Menu_Customer_LookUp_Fbb_VIEW) :"+userRoleSet.contains(Constants.MENUITEM_LOYALTY_MENU_CUSTOMER_LOOKUP_FBB_VIEW));
               
               if(userRoleSet != null && userRoleSet.contains(Constants.MENUITEM_LOYALTY_MENU_CUSTOMER_LOOKUP_FBB_VIEW)
            		   &&!userRoleSet.contains(Constants.MENUITEM_DASHBOARD_VIEW)) { 
                   homeMenuItemId.setVisible(false);
                   Redirect.goTo(PageListEnum.EMPTY);
                   Redirect.goTo(PageListEnum.CUSTOMER_LOOKUP_FBB);
               }
               if(userRoleSet != null && userRoleSet.contains(Constants.MENUITEM_LOYALTY_MENU_CUSTOMER_LOOKUP_AND_REDEEM_VIEW)
            		   &&!userRoleSet.contains(Constants.MENUITEM_DASHBOARD_VIEW)) { 
                   homeMenuItemId.setVisible(false);
                   Redirect.goTo(PageListEnum.EMPTY);
                   Redirect.goTo(PageListEnum.LOYALTY_CUSTOMER_LOOKUP_AND_REDEEM);
               }
               if(userRoleSet != null && userRoleSet.contains(Constants.MENUITEM_LOYALTY_MENU_CUSTOMER_LOOKUP_VIEW)&&!(userRoleSet.contains(Constants.MENUITEM_DASHBOARD_VIEW))) { 
                   homeMenuItemId.setVisible(false);
                   Redirect.goTo(PageListEnum.EMPTY);
                   Redirect.goTo(PageListEnum.CUSTOMER_LOOKUP);
               }
               
           }
       } catch (Exception e) {
           // TODO Auto-generated catch block
           logger.error("Exception ::", e);
       }
	} //

	private Listbox userFilterListBoxId, sharedUserFilterListBoxId;
	
	
	
	public void onOpen$usersBandBoxId() {
		try {
			
			int count = userFilterListBoxId.getItemCount();
			for(int i= count; i > 0; i--) {
				
				
				userFilterListBoxId.removeItemAt(i-1);
				
				
			}
			
			
			count = sharedUserFilterListBoxId.getItemCount();
			for(int i= count; i > 0; i--) {
				
				
				sharedUserFilterListBoxId.removeItemAt(i-1);
				
				
			}
			
			//Components.removeAllChildren(userFilterListBoxId);
			
			Users user = GetUser.getUserObj(); // usersDao.findByUsername(userName);
			
			if(user == null) {
				logger.error("** User is null ");
				return;
			}
			List<UsersDomains> domainsList = usersDao.getAllDomainsByUser(user.getUserId());
			//Set<UsersDomains> domainSet = new HashSet<UsersDomains>();
			// logger.debug("userDomainsSet = "+userDomainsSet);
			
			String userDomainIds = "";
			for (UsersDomains eachDomain : domainsList) {
				
				if(userDomainIds.length()>0) userDomainIds +=",";
				userDomainIds += eachDomain.getDomainId().longValue();
			}
			
			//TODO need to get the list,segments which are shared.
			UsersDomainsDao usersDomainsDao = (UsersDomainsDao)SpringUtil.getBean("usersDomainsDao");
			List<Long> sharedLists = usersDomainsDao.getSharedLists(userDomainIds);
			//logger.info("sharedLists ::"+sharedLists.get(0).longValue());
			
			
			
			
			List<Long> sharedSegments = usersDomainsDao.getSharedSegments(userDomainIds);
			//logger.info("sharedLists ::"+sharedSegments.get(0).longValue());
			
			
			//List<Map<String, Object>> domainUserList = usersDao.getUserIdsByDomain(userDomainIds);
				
			prepareUserListitems(sharedLists, sharedSegments,  user); // fills the list box with the users under one domain
				
		} catch (Exception e) {
			logger.error("** Exception ", e);
		}
		
	}
	/**
	 * fills the list box with the users under one domain
	 * @param userList
	 * @param user
	 */
	public void prepareUserListitems(List<Map<String, Object>> domainUserList , Users user) {
		Listitem useritem = null;
		String mainUserName = Utility.getOnlyUserName(user.getUserName());
		List<Long> sharedUserIds = new ArrayList<Long>();
		if(user.getAccountType().equalsIgnoreCase(Constants.USER_ACCOUNT_TYPE_PRIMARY)) {
			
			mainUserName = Utility.getOnlyUserName(user.getUserName());
			useritem = new Listitem("Non Shared Item(s)", user.getUserId());
			useritem.setParent(userFilterListBoxId);
			userFilterListBoxId.setSelectedIndex(0);
		}
		
		for (Map<String, Object> eachUsers : domainUserList) {
			
			if( ((String)eachUsers.get("username")).contains(mainUserName)) {
				logger.debug("item to non share list");
				sharedUserIds.add((Long)eachUsers.get("user_id"));
				
				/*useritem = new Listitem(Utility.getOnlyUserName((String)eachUsers.get("username")), eachUsers.get("user_id"));
				useritem.setParent(userFilterListBoxId);
				*/
			}
			else {
				logger.debug("item to share list");
				useritem = new Listitem(Utility.getOnlyUserName((String)eachUsers.get("username")), eachUsers.get("user_id"));
				useritem.setParent(sharedUserFilterListBoxId);
			}
		} // for
		
		useritem = new Listitem("Shared Item(s)", sharedUserIds);
		useritem.setParent(userFilterListBoxId);
		
		prepareUserSelection();//makes the selection of items
	}
	
	
	
	
	/**added for sharing
	 * fills the list box with the users under one domain
	 * @param userList
	 * @param user
	 */
	public void prepareUserListitems(List<Long> mlLists , List<Long> segmentsList, Users user) {
		try {
			Listitem useritem = null;
			String mainUserName = Utility.getOnlyUserName(user.getUserName());
			List<Long> sharedUserIds = new ArrayList<Long>();
			
			String ListidStr = Utility.getIdsStrFromList(mlLists);
			String SegmentidStr = Utility.getIdsStrFromList(segmentsList);
			
			
			//if(user.getAccountType().equalsIgnoreCase(Constants.USER_ACCOUNT_TYPE_PRIMARY)) {
				
				Map<String, List<Long>> nonShareditemsMap = new HashMap<String, List<Long>>();
				
				//to find mLits(non shared)
				List<Long> nonSharedMlLists = mailingListDao.findNonSharedLists(user.getUserId(), ListidStr );
				
				//to find segments(non shared)
				List<Long> nonSharedSegments = segmentRulesDao.findNonSharedSegments(user.getUserId(), SegmentidStr );
					
				
				nonShareditemsMap.put(Constants.SHARED_ITEM_LISTS, nonSharedMlLists);
				nonShareditemsMap.put(Constants.SHARED_ITEM_SEGMENTS, nonSharedSegments);
				
				
				mainUserName = Utility.getOnlyUserName(user.getUserName());
				useritem = new Listitem("Non Shared Item(s)", nonShareditemsMap);
				useritem.setParent(userFilterListBoxId);
				userFilterListBoxId.setSelectedIndex(0);
				
				
			//}
			
			
			
			
			/*for (Map<String, Object> eachUsers : domainUserList) {
				
				if( ((String)eachUsers.get("username")).contains(mainUserName)) {
					logger.debug("item to non share list");
					sharedUserIds.add((Long)eachUsers.get("user_id"));
					
					useritem = new Listitem(Utility.getOnlyUserName((String)eachUsers.get("username")), eachUsers.get("user_id"));
					useritem.setParent(userFilterListBoxId);
					
				}
				else {
					logger.debug("item to share list");
					useritem = new Listitem(Utility.getOnlyUserName((String)eachUsers.get("username")), eachUsers.get("user_id"));
					useritem.setParent(sharedUserFilterListBoxId);
				}
			} // for
*/		
			
			
			
			
			//to prepare myShared iTems
			Map<String, List<Long>> MyShareditemsMap = new HashMap<String, List<Long>>();
			
			Map<String , Object> sharedListMap = getOnlyMySharedLists(ListidStr, user);
			Map<String , Object> sharedSegmentMap = getOnlyMySharedSegments(SegmentidStr, user);
			
			MyShareditemsMap.put(Constants.SHARED_ITEM_LISTS, sharedListMap.get(Constants.MY_SHARED_ITEM_LISTS) != null ? (List<Long>)sharedListMap.get(Constants.MY_SHARED_ITEM_LISTS) : null );
			MyShareditemsMap.put(Constants.SHARED_ITEM_SEGMENTS, sharedSegmentMap.get(Constants.MY_SHARED_ITEM_SEGMENTS) != null ? (List<Long>)sharedSegmentMap.get(Constants.MY_SHARED_ITEM_SEGMENTS) : null);
			
			useritem = new Listitem("Shared Item(s)", MyShareditemsMap);
			useritem.setParent(userFilterListBoxId);
			
			
			
			//To Prepare Other's Shared items
			//Map<String, List<Long>> OthersShareditemsMap = new HashMap<String, List<Long>>();
			
			Map<String, List<Long>> otherSharedListMap = sharedListMap.get(Constants.OTHERS_SHARED_ITEM_LISTS) != null ? (Map<String, List<Long>>)sharedListMap.get(Constants.OTHERS_SHARED_ITEM_LISTS) : null;
			Map<String, List<Long>> otherSharedSegmentMap = sharedSegmentMap.get(Constants.OTHERS_SHARED_ITEM_SEGMENTS) !=  null ? (Map<String, List<Long>>)sharedSegmentMap.get(Constants.OTHERS_SHARED_ITEM_SEGMENTS) : null;
			
			
			Set<String> ListUserNameSet = otherSharedListMap != null ? otherSharedListMap.keySet() : null;
			Set<String> SeguserNameSet = (otherSharedSegmentMap != null ? otherSharedSegmentMap.keySet() : null);
			
			if(ListUserNameSet == null && SeguserNameSet == null) {
				
				//TODO need to decide
				
				
			}
			
			
			
			if(ListUserNameSet != null) {
				Iterator<String> it = ListUserNameSet.iterator();
				while (it.hasNext()) {
					
					Map<String, List<Long>> OthersShareditemsMap = new HashMap<String, List<Long>>();
					String userName = (String) it.next();
					//logger.debug("in list usernames:"+SeguserNameSet);
					if(userName == null) continue; 
					Listitem existedItem = null;
					if(sharedUserFilterListBoxId.getItemCount() > 0) {
						
						 existedItem = findUserExistedItem("Shared_By_"+userName);
						
						if(existedItem != null) {
							
							OthersShareditemsMap = existedItem.getValue();
							
						}//if
					
					}//if
					
					OthersShareditemsMap.put(Constants.SHARED_ITEM_LISTS, otherSharedListMap.get(userName));
					
					if(SeguserNameSet != null && SeguserNameSet.contains(userName)) {
						
						OthersShareditemsMap.put(Constants.SHARED_ITEM_SEGMENTS, otherSharedSegmentMap.get(userName));
					}
					
					if(existedItem == null) {
						useritem = new Listitem("Shared_By_"+userName, OthersShareditemsMap );
						useritem.setParent(sharedUserFilterListBoxId);
					}
					
				}
			}
			
			if(SeguserNameSet != null) {
				Iterator<String> it = SeguserNameSet.iterator();
				
				while (it.hasNext()) {
					
					Map<String, List<Long>> OthersShareditemsMap = new HashMap<String, List<Long>>();
					String userName = (String) it.next();
					if(userName == null) continue; 
					Listitem existedItem = null;
					if(sharedUserFilterListBoxId.getItemCount() > 0) {
						
						 existedItem = findUserExistedItem("Shared_By_"+userName);
						
						if(existedItem != null) {
							
							OthersShareditemsMap = existedItem.getValue();
							
						}//if
					
					}//if
					//logger.debug("in segment usernames::"+userName+"  "+SeguserNameSet != null ? SeguserNameSet.contains(userName): SeguserNameSet);
					OthersShareditemsMap.put(Constants.SHARED_ITEM_SEGMENTS, otherSharedSegmentMap.get(userName));
					
					if(ListUserNameSet != null && ListUserNameSet.contains(userName)) {
						
						OthersShareditemsMap.put(Constants.SHARED_ITEM_LISTS, otherSharedListMap.get(userName));
					}
					
					if(existedItem == null) {
						useritem = new Listitem("Shared_By_"+userName, OthersShareditemsMap );
						useritem.setParent(sharedUserFilterListBoxId);
					}
					
				}
				
			}
			
			//prepareUserSelection();//makes the selection of items
			
			prepareItemsSelection();//makes the selection of items
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
		}
		
		
	}
	
	
	public Listitem findUserExistedItem(String userName){
		
		for (Listitem eachItem : sharedUserFilterListBoxId.getItems()) {
			if(eachItem.getLabel().equals(userName)) return eachItem;
			
			
			
		}//for
		
		
		return null;
		
		
		
		
		
	}
	
	public Map<String ,Object> getOnlyMySharedLists(String ListidStr, Users u ) {
		
		
		Map<String , Object> sharedItemsMap = new HashMap<String, Object>();
		
		Map<String , List<Long>> othersSharedItemsMap = new HashMap<String, List<Long>>();
		
		List<Long> mySharedistIds = new ArrayList<Long>();
		//List<Long> othersSharedistIds = new ArrayList<Long>();
		
		List<MailingList> allSharedLists = mailingListDao.findByIds(ListidStr);
		
		if(allSharedLists != null ) {
			for (MailingList mailingList : allSharedLists) {
				
				Users mlUsers = mailingList.getUsers();
				
				String mlUserName = Utility.getOnlyUserName(mlUsers.getUserName());
				
				if(mlUsers.getUserId().longValue() == u.getUserId().longValue()) {
					
					mySharedistIds.add(mailingList.getListId());
					
				}else {
					
					
					List<Long> eachUserSharedList = othersSharedItemsMap.get(mlUserName);
					
					if(eachUserSharedList == null) eachUserSharedList = new ArrayList<Long>();
					
					eachUserSharedList.add(mailingList.getListId());
					othersSharedItemsMap.put(mlUserName, eachUserSharedList);
					
				}
				
				
			}//for
		}
		
		sharedItemsMap.put(Constants.MY_SHARED_ITEM_LISTS, mySharedistIds.size() > 0 ? mySharedistIds : null );
		sharedItemsMap.put(Constants.OTHERS_SHARED_ITEM_LISTS, othersSharedItemsMap.size() > 0 ? othersSharedItemsMap : null );
		
		return sharedItemsMap;
		
		
	}
	
	
public Map<String, Object> getOnlyMySharedSegments(String SegmentidStr, Users u ) {
		
		
	Map<String , Object> sharedItemsMap = new HashMap<String, Object>();
	
	Map<String , List<Long>> othersSharedItemsMap = new HashMap<String, List<Long>>();
	
	List<Long> mySharedistIds = new ArrayList<Long>();
	//List<Long> othersSharedistIds = new ArrayList<Long>();
	//String segmentUserIds = findAllSegmentUserIds(allSharedSegments);
	//Set<String> userIdsSet = new HashSet<String>();
		
		try {
			//if(allSharedSegments == null) return null;
			
			List<SegmentRules> allSharedSegments = segmentRulesDao.findById(SegmentidStr);
			if(allSharedSegments != null) {
				for (SegmentRules segmentRule: allSharedSegments) {
					
					//Users segUser = segmentRule.getUsers();
					if(segmentRule.getUserId().longValue() == u.getUserId().longValue()) {
						
						mySharedistIds.add(segmentRule.getSegRuleId());
						
					}else{
						String userIdstr = segmentRule.getUserId().longValue() +"";
						/*String userName = null;
						if(!userIdsSet.contains(userIdstr) ){
							
							usersDao.findNameByUserId();
							userIdsSet.add(userIdstr)
							
						}
						*/
						String userName = usersDao.findNameById(userIdstr);
						String segUserName = Utility.getOnlyUserName(userName);
						List<Long> eachUserSharedList = othersSharedItemsMap.get(segUserName);
						
						if(eachUserSharedList == null) eachUserSharedList = new ArrayList<Long>();
						
						eachUserSharedList.add(segmentRule.getSegRuleId());
						othersSharedItemsMap.put(segUserName, eachUserSharedList);
						//othersSharedistIds.add(segmentRule.getListId());
						
					}
					
					
				}//for
			}
			
			sharedItemsMap.put(Constants.MY_SHARED_ITEM_SEGMENTS, mySharedistIds.size() > 0 ? mySharedistIds : null );
			sharedItemsMap.put(Constants.OTHERS_SHARED_ITEM_SEGMENTS, othersSharedItemsMap.size() > 0 ? othersSharedItemsMap : null );
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
		}
		
		return sharedItemsMap;
		
	}
	
	
	public void prepareItemsSelection() {
		
		try {
			Set<Long> listIdsSet = (Set<Long>)session.getAttribute(Constants.LISTIDS_SET);
			Set<Long> segmentIdsSet = (Set<Long>)session.getAttribute(Constants.SEGMENTIDS_SET);
			
		//	logger.debug("lists ::"+listIdsSet.size());
		//	logger.debug("segments ::"+segmentIdsSet.size());
			
			userFilterListBoxId.clearSelection();
			sharedUserFilterListBoxId.clearSelection();
			
			if(listIdsSet != null && listIdsSet.size() > 0) {
				
				for (Listitem item : userFilterListBoxId.getItems()) {
					
					Map<String, List<Long>>  itemMap = item.getValue();
					
					if(itemMap.get(Constants.SHARED_ITEM_LISTS) == null) continue;
						
					List<Long> mlList = (itemMap.get(Constants.SHARED_ITEM_LISTS));
					
					if(listIdsSet.containsAll(mlList) ) {
						userFilterListBoxId.addItemToSelection(item);
					}
					/*if(itemMap.get(Constants.SHARED_ITEM_SEGMENTS) != null) {
						
						segmentList.addAll(itemMap.get(Constants.SHARED_ITEM_SEGMENTS));
					}*/
					
					
				}//for
				for (Listitem item : sharedUserFilterListBoxId.getItems()) {
					
					Map<String, List<Long>>  itemMap = item.getValue();
					
					if(itemMap.get(Constants.SHARED_ITEM_LISTS) == null) continue;
						
					List<Long> mlList = (itemMap.get(Constants.SHARED_ITEM_LISTS));
					boolean foundAll = false;
					
					if(listIdsSet.containsAll(mlList) ) {
						sharedUserFilterListBoxId.addItemToSelection(item);
					}
					/*if(itemMap.get(Constants.SHARED_ITEM_SEGMENTS) != null) {
						
						segmentList.addAll(itemMap.get(Constants.SHARED_ITEM_SEGMENTS));
					}*/
					
					
				}//for
				
			}//IF
			if(segmentIdsSet != null && segmentIdsSet.size() > 0) {
				
				for (Listitem item : userFilterListBoxId.getItems()) {
					
					Map<String, List<Long>>  itemMap = item.getValue();
					
					if(itemMap.get(Constants.SHARED_ITEM_SEGMENTS) == null) continue;
						
					List<Long> segList = (itemMap.get(Constants.SHARED_ITEM_SEGMENTS));
					
					if(segmentIdsSet.containsAll(segList) ) {
						userFilterListBoxId.addItemToSelection(item);
					}
					/*if(itemMap.get(Constants.SHARED_ITEM_SEGMENTS) != null) {
						
						segmentList.addAll(itemMap.get(Constants.SHARED_ITEM_SEGMENTS));
					}*/
					
					
				}//for
				for (Listitem item : sharedUserFilterListBoxId.getItems()) {
					
					Map<String, List<Long>>  itemMap = item.getValue();
					
					if(itemMap.get(Constants.SHARED_ITEM_SEGMENTS) == null) continue;
						
					List<Long> segList = (itemMap.get(Constants.SHARED_ITEM_SEGMENTS));
					
					if(segmentIdsSet.containsAll(segList) ) {
						sharedUserFilterListBoxId.addItemToSelection(item);
					}
					/*if(itemMap.get(Constants.SHARED_ITEM_SEGMENTS) != null) {
						
						segmentList.addAll(itemMap.get(Constants.SHARED_ITEM_SEGMENTS));
					}*/
					
					
				}//for
				
				
			}//IF
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
		}
		
	}
	
	
	public void prepareUserSelection() {
		
		try {
			Set<Long> userIdsSet = (Set<Long>)session.getAttribute(Constants.USERIDS_SET);
			
			if(userIdsSet != null) {
				logger.debug("---just entered---"+userIdsSet.size());
				userFilterListBoxId.clearSelection();
				sharedUserFilterListBoxId.clearSelection();
				boolean chkIsShredExists = false;
				
				for (Long userId : userIdsSet) {
					
					
					if(userFilterListBoxId.getItemCount() > 1) {
						
						if(userId.longValue() == 
							((Long)userFilterListBoxId.getItemAtIndex(0).getValue()).longValue()) {
							
							logger.debug("---added item for selection---");
							
							userFilterListBoxId.addItemToSelection(userFilterListBoxId.getItemAtIndex(0));
							//break;
						}
						
						
						List<Long> sharedUsers = userFilterListBoxId.getItemAtIndex(1).getValue();
						if(!chkIsShredExists) {
							for (Long shUserId : sharedUsers) {
								
								if(userId.longValue() == shUserId.longValue()) {
									
									logger.debug("---added item for selection---");
									
									userFilterListBoxId.addItemToSelection(userFilterListBoxId.getItemAtIndex(1));
									chkIsShredExists = true;
									break;
								}
								
								
							}
						
						}
						
						
					}//if
					else if(userFilterListBoxId.getItemCount() == 1) {
						
						/*if(userId.longValue() == 
							((Long)userFilterListBoxId.getItemAtIndex(0).getValue()).longValue()) {
							
							logger.debug("---added item for selection---");
							*/
							//userFilterListBoxId.addItemToSelection(userFilterListBoxId.getItemAtIndex(0));
						List<Long> sharedUsers = userFilterListBoxId.getItemAtIndex(0).getValue();
						if(!chkIsShredExists) {
							for (Long shUserId : sharedUsers) {
								
								if(userId.longValue() == shUserId.longValue()) {
									
									logger.debug("---added item for selection---");
									
									userFilterListBoxId.addItemToSelection(userFilterListBoxId.getItemAtIndex(0));
									chkIsShredExists = true;
									break;
								}
								
								
							}
						
						}
						//}
						
						
					}
					
					
						
							
					
					/*List<Long> sharedUsers = userFilterListBoxId.getItemAtIndex(1).getValue();
					if(!chkIsShredExists) {
						for (Long shUserId : sharedUsers) {
							
							if(userId.longValue() == shUserId.longValue()) {
								
								logger.debug("---added item for selection---");
								
								userFilterListBoxId.addItemToSelection(userFilterListBoxId.getItemAtIndex(1));
								chkIsShredExists = true;
								break;
							}
							
							
						}
					
					}
					*/
					/*for(int i=0; i<userFilterListBoxId.getItemCount(); i++) {
						
						if(userId.longValue() == 
							((Long)userFilterListBoxId.getItemAtIndex(i).getValue()).longValue()) {
							
							logger.debug("---added item for selection---");
							
							userFilterListBoxId.addItemToSelection(userFilterListBoxId.getItemAtIndex(i));
							break;
						}
							
					}//inner for
*/					
					for(int i=0; i<sharedUserFilterListBoxId.getItemCount(); i++) {
						if(userId.longValue() == 
							((Long)sharedUserFilterListBoxId.getItemAtIndex(i).getValue()).longValue()) {
							logger.debug("---added item for selection---");
							sharedUserFilterListBoxId.addItemToSelection(sharedUserFilterListBoxId.getItemAtIndex(i));
							break;
						}
							
					}//inner for
					
					
					
				}//outer for
				
				
			}//if
		} catch (Exception e) {
			logger.error("** Exception",e);
		}
		
	}
	
	public void onClick$cancelFilterBtnId() {
		usersBandBoxId.close();
		prepareItemsSelection();
		
		
		
		
		
	}
	
	private Bandbox usersBandBoxId;
	private Div userLblDivId;
	public void prepareShareItemsSet() {try {
		String itemsViewLabel = "My Item(s) & Shared Item(s)";
		boolean mainUserFound = false;
		boolean userSharedFound = false;
		boolean sharedFound = false;
		
		List<Long> mlList = new ArrayList<Long>();
		List<Long> segmentList = new ArrayList<Long>();
		
		if(userFilterListBoxId.getSelectedCount() == 0 && sharedUserFilterListBoxId.getSelectedCount() == 0) {
			MessageUtil.setMessage("Please select at least one user.","color:red;");
			return;
		}
		
		
		
//		Set<Listitem> itemSet = new HashSet<Listitem>();
		
		if(userFilterListBoxId.getSelectedCount() > 0) {
			
		
			for (Listitem item : userFilterListBoxId.getSelectedItems()) {
				
				if(!mainUserFound && item.getLabel().startsWith("Non Shared")) {
					
					mainUserFound = true;
				}else if( !userSharedFound && item.getLabel().startsWith("Shared Item")) {
					
					userSharedFound = true;
					
				}
				
				Map<String, List<Long>>  itemMap = item.getValue();
				
				if(itemMap.get(Constants.SHARED_ITEM_LISTS) != null) {
					
					mlList.addAll(itemMap.get(Constants.SHARED_ITEM_LISTS));
				}
				if(itemMap.get(Constants.SHARED_ITEM_SEGMENTS) != null) {
					
					segmentList.addAll(itemMap.get(Constants.SHARED_ITEM_SEGMENTS));
				}
			
			}//for
		}
		
		if(sharedUserFilterListBoxId.getSelectedCount() > 0) {
			
			if(!sharedFound) sharedFound = true;
			
			
			for (Listitem item : sharedUserFilterListBoxId.getSelectedItems()) {
				
				Map<String, List<Long>>  itemMap = item.getValue();
				if(itemMap.get(Constants.SHARED_ITEM_LISTS) != null) {
					
					mlList.addAll(itemMap.get(Constants.SHARED_ITEM_LISTS));
				}
				if(itemMap.get(Constants.SHARED_ITEM_SEGMENTS) != null) {
					
					segmentList.addAll(itemMap.get(Constants.SHARED_ITEM_SEGMENTS));
				}
				
				
			}//for
			
		}//if
		Set<Long> listIdsSet = (Set<Long>)session.getAttribute(Constants.LISTIDS_SET);
		Set<Long> segmentIdsSet = (Set<Long>)session.getAttribute(Constants.SEGMENTIDS_SET);
		
		if(listIdsSet == null) {
			
			listIdsSet = new HashSet<Long>();
		}
		if(segmentIdsSet ==null) {
			segmentIdsSet = new HashSet<Long>();
		}
		listIdsSet.clear();
		segmentIdsSet.clear();
		
		listIdsSet.addAll(mlList);
		segmentIdsSet.addAll(segmentList);
		
		logger.debug("listIdsSet :: "+listIdsSet.size());
		logger.debug("segmentSet :: "+segmentIdsSet.size());
		session.setAttribute(Constants.LISTIDS_SET, listIdsSet);
		session.setAttribute(Constants.SEGMENTIDS_SET, segmentIdsSet);
		
		
		if(sharedFound && !mainUserFound && !userSharedFound) {
			itemsViewLabel = "Shared Item(s)";
			
		}
		else if(mainUserFound && !userSharedFound && !sharedFound) {
			
			itemsViewLabel = "My Item(s)";
			
			
		}
		else if(!mainUserFound && userSharedFound && !sharedFound) {
			
			itemsViewLabel = "My Shared Item(s)";
			
			
		}
		
		homeHeaderUserLableRightId.setValue(itemsViewLabel);
	} catch (Exception e) {
		// TODO Auto-generated catch block
		logger.error("Exception ::", e);
	}
	
	
	

}//prepareShareItemsSet
	
	
	
	public void onClick$showFilterBtnId() {
		
		prepareShareItemsSet();
		
		
		//homeHeaderUserLableRightId.setValue(itemsViewLabel);
		usersBandBoxId.close();
		String currPage = (String)session.getAttribute("currentPage");
		Redirect.goTo(PageListEnum.EMPTY);
		Redirect.goTo(currPage);

		
		//if(true) return;
		
		/*try {
			List<Long> userList = new ArrayList<Long>();
			
			if(userFilterListBoxId.getSelectedCount() == 0 && sharedUserFilterListBoxId.getSelectedCount() == 0) {
				MessageUtil.setMessage("Please select at least one user.","color:red;");
				return;
			}
			
			
			
			Set<Listitem> itemSet = new HashSet<Listitem>();
			
			if(userFilterListBoxId.getSelectedCount() > 0) {
				int index = userFilterListBoxId.getItemCount();
				
				
				
				
				
				
				
				
				List<Long> sharedUserIds = null;
				Long userId= null;
				Object  obj = (userFilterListBoxId.getItemAtIndex(index-1).getValue());
				if(obj instanceof List) {
					
					sharedUserIds = (List<Long>)obj;
					
				}else if(obj instanceof Long) {
					
					userId = (Long)obj; 
					
					
				}
				
				if(userFilterListBoxId.getSelectedCount() > 1) {
					
					userList.add((Long)userFilterListBoxId.getItemAtIndex(0).getValue());
					userList.addAll(sharedUserIds);
					
				}
				else {
					
					if(sharedUserIds != null) {
						userList.addAll(sharedUserIds);
					}else{
						
						userList.add(userId);
					}
					
					Listitem item = userFilterListBoxId.getSelectedItem();
					if( (item.getIndex() == 0 && userFilterListBoxId.getItemCount() == 1) || 
							(item.getIndex() == 1 && userFilterListBoxId.getItemCount() > 1)	) {
	
						userList.addAll((List<Long>) item.getValue());
						
					}else{
						
						userList.add((Long)item.getValue());
						
					}
					
					
				}//else
			
			}
			
			//itemSet.addAll( userFilterListBoxId.getSelectedItems() );
			//itemSet.addAll(sharedUserFilterListBoxId.getSelectedItems());
			
			for (Listitem item : sharedUserFilterListBoxId.getSelectedItems()) {
				
				userList.add((Long)item.getValue());
			}
			
			
			
			GetUser.setUsersSet(userList);
			
			Set<Long> userIdsSet = (Set<Long>)session.getAttribute(Constants.USERIDS_SET);*/
		/*	
			String itemsViewLabel = "My Item(s) & Shared Item(s)";
			boolean mainUserFound = false;
			boolean userSharedFound = false;
			boolean sharedFound = false;
			//List<Users> uList = usersDao.findAllByIds(userIdsSet);
			//Long userItemId = null;
			Long userId = null;
			Users currentUser = GetUser.getUserObj();
			*/
			
			/*for(Long userItemId : userList) {
				for (Users user : uList) {
					
					//userItemId = (Long)item.getValue();
					userId = user.getUserId();
					
					if(!mainUserFound  && user.getAccountType().equals(Constants.USER_ACCOUNT_TYPE_PRIMARY) && 
							userItemId.longValue() == user.getUserId()) {//means it is the main user
						
						mainUserFound = true;
						
						
						
					}
					
					else if(!userSharedFound && user.getAccountType().equals(Constants.USER_ACCOUNT_TYPE_SHARED) ){
						
						if(user.getParentUser() != null && (currentUser.getUserId().longValue() == user.getParentUser().getUserId() )) {
						
						
							userSharedFound = true;
							//logger.debug("items5===>"+itemsViewLabel);
						}
						else if(user.getUserId().longValue() == currentUser.getUserId().longValue()){
							
							mainUserFound = true;
							//logger.debug("items6===>"+itemsViewLabel);
						}
						
						else {
							
							sharedFound = true;
							//logger.debug("items7===>"+itemsViewLabel);
						}
					}
					
						
				}//inner for
			}//outer for
*/			/*if(sharedFound && !mainUserFound && !userSharedFound) {
				itemsViewLabel = "Shared Item(s)";
				
			}
			else if(mainUserFound && !userSharedFound && !sharedFound) {
				
				itemsViewLabel = "My Item(s)";
				
				
			}
			else if(!mainUserFound && userSharedFound && !sharedFound) {
				
				itemsViewLabel = "Shared Item(s)";
				
				
			}
			
			homeHeaderUserLableRightId.setValue(itemsViewLabel);
			usersBandBoxId.close();
			currPage = (String)session.getAttribute("currentPage");
			Redirect.goTo(PageListEnum.EMPTY);
			Redirect.goTo(currPage);*/
		/*} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
		}*/
		
	} // onClick$showFilterBtnId

	private void goToPage(PageListEnum srcEnum) {
		goToPage(srcEnum.getPagePath());
	}

	private void goToPage(String src) {
		if(!src.trim().equals(""))
			Redirect.goTo(src);
	}
		
	private Menuitem homeMenuItemId,myEmailsMenuItemId,createSpecialRewardsId,mySpecialRewardsId,createEmailMenuItemId,eventTriggerMenuItemId,digitalRecieptsMenuItemId,digitalRecieptsSettingsMenuItemId,templatesMenuItemId,templatesSettingsMenuItemId,RewardReportsMenuItemId,LtyROIReportsMenuItemId,
					valueCodesMenuItemId,liabilityReportsMenuItemId,createEventMenuItemId,manageEventMenuItemId;
	/***********Home*************/
	public void onClick$homeMenuItemId(){
		//goToPage(homeMenuItemId.getValue());
		goToPage(PageListEnum.RM_HOME);
		
	} // onClick$homeMenuItemId
	/***********Emails*************/
	//changes 2.5.3.0 start
	public void onClick$createEventMenuItemId() {
		goToPage(createEventMenuItemId.getValue());
	}
	public void onClick$manageEventMenuItemId() {
		goToPage(manageEventMenuItemId.getValue());
	}
	public void onClick$createSpecialRewardsId(){
		goToPage(createSpecialRewardsId.getValue());
	} 
	public void onClick$mySpecialRewardsId(){
		goToPage(mySpecialRewardsId.getValue());
	} 
	public void onClick$RewardReportsMenuItemId() {
		goToPage(RewardReportsMenuItemId.getValue());
	}
	public void onClick$LtyROIReportsMenuItemId() {
		goToPage(LtyROIReportsMenuItemId.getValue());
	}
	public void onClick$valueCodesMenuItemId() {
		goToPage(valueCodesMenuItemId.getValue());
	}
	public void onClick$liabilityReportsMenuItemId() {
		goToPage(liabilityReportsMenuItemId.getValue());
	}
	//changes 2.5.3.0 end
	public void onClick$myEmailsMenuItemId(){
		goToPage(myEmailsMenuItemId.getValue());
	} 
	public void onClick$createEmailMenuItemId(){
		goToPage(createEmailMenuItemId.getValue());
	}
	
	public void onClick$digitalRecieptsMenuItemId(){
		goToPage(digitalRecieptsMenuItemId.getValue());
	}
	
	public void onClick$eventTriggerMenuItemId(){
		goToPage(eventTriggerMenuItemId.getValue());
	}
	
	public void onClick$digitalRecieptsSettingsMenuItemId() {
		goToPage(digitalRecieptsSettingsMenuItemId.getValue());
	}
	
	public void onClick$templatesMenuItemId(){
		goToPage(templatesMenuItemId.getValue());
	}
	public void onClick$templatesSettingsMenuItemId() {
		goToPage(templatesSettingsMenuItemId.getValue());
	}
	
	/***********SMS*************/
	//app-4288
	private Menuitem MySMSCampaignsMenuItemId,MyWACampaignsMenuItemId,createSmsCampaignMenuItemId, SmsCampSetupMenuItemId, SmsSettingsMenuItemId,optinSmsSettingsMenuItemId;
	private Menuitem createWACampaignMenuItemId;
	private Menu smsMenuId;
	
	public boolean isOptedForSMS() {
		//logger.debug("current USer ::"+currentUser);
		
		if(currentUser == null) currentUser = GetUser.getUserObj();
		
		if(!currentUser.isEnableSMS()) {
			
			MessageUtil.setMessage(SMS_MSG_TEXT, "color:red;");
			return false;
		}
	
		return true;
	}
	public boolean isOptedForSMSKeywords() {
		//logger.debug("current USer ::"+currentUser);
		
		if(currentUser == null) currentUser = GetUser.getUserObj();
	
		CountryReceivingNumbersDao countryReceivingNumbersDao =(CountryReceivingNumbersDao)SpringUtil.getBean("countryReceivingNumbersDao");
		List<CountryReceivingNumbers> recevingNumList = countryReceivingNumbersDao.findBy(currentUser.getCountryType());//getReceivingNumByCountry(countryMap.get(country), typeMap.get(type));
		if(recevingNumList == null) {
			MessageUtil.setMessage(SMS_MSG_KEYWORD_TEXT, "color:red;");
			return false;
		}
		return true;
	}
	

	public void onClick$MySMSCampaignsMenuItemId(){
		
		if(isOptedForSMS())goToPage(MySMSCampaignsMenuItemId.getValue());
	}
	//APP-4288
    public void onClick$MyWACampaignsMenuItemId(){
		
		if(isOptedForWA())goToPage(MyWACampaignsMenuItemId.getValue());
	}
	public void onClick$createSmsCampaignMenuItemId(){
		
		if(isOptedForSMS())goToPage(createSmsCampaignMenuItemId.getValue());
	}
	
	public void onClick$SmsCampSetupMenuItemId() {
		
		if(isOptedForSMS() && isOptedForSMSKeywords()) goToPage(SmsCampSetupMenuItemId.getValue());
		
	}
	public void onClick$SmsSettingsMenuItemId() {
		
		if(isOptedForSMS() && isOptedForSMSKeywords() )goToPage(SmsSettingsMenuItemId.getValue());
		
	}
	
	public void onClick$optinSmsSettingsMenuItemId() {
		
		if(isOptedForSMS() && isOptedForSMSKeywords())goToPage(optinSmsSettingsMenuItemId.getValue());
		
	}
	
	public void onClick$createWACampaignMenuItemId(){
		
		if(isOptedForWA())goToPage(createWACampaignMenuItemId.getValue());
	}
	public boolean isOptedForWA() {
		
		if(currentUser == null) currentUser = GetUser.getUserObj();
		
		if(!currentUser.isEnableWA()) { //APP-4288
			
			MessageUtil.setMessage(WA_MSG_TEXT, "color:red;");
			return false;
		}
	
		return true;
	}
	
	/*********** SOCIAL *************/
	private Menuitem createSocialMenuItemId,socialSettingsMenuItemId,customCodesMenuItemId,mySocialMenuItemId;
	public void onClick$createSocialMenuItemId(){
		goToPage(PageListEnum.EMPTY);
		goToPage(createSocialMenuItemId.getValue());
	}
	public void onClick$socialSettingsMenuItemId(){
		goToPage(socialSettingsMenuItemId.getValue());
	}
	
	public void onClick$customCodesMenuItemId() {
		
		goToPage(customCodesMenuItemId.getValue());
	}
	public void onClick$mySocialMenuItemId() {
		goToPage(mySocialMenuItemId.getValue());
	}

	
	/***********Contacts*************/
	private Menuitem myContactsMenuItemId,uploadContactsMenuItemId,promoteCenterMenuItemId,autoEmailMenuItemId,webformsMenuItemId,
	manageContactsMenuItemId,manageSegmentsMenuItemId, manageBcrmSegmentsMenuItemId, mySegmentsMenuItemId,parentalConsentMenuItemId, doubleOptInMenuItemId,homesPassedSegmentsMenuItemId,autoSmsMenuItemId,singleViewOfContactsMenuItemId
	,autoEmailMenuItemIdBeeId,autoSmsReportsMenuItemId,offersNotificationMenuItemId,notificationMenuItemId,notificationMenuItemListId,notificationReportsMenuItemId;
	
	public void onClick$notificationReportsMenuItemId() {
		goToPage(notificationReportsMenuItemId.getValue());
	}
	public void onClick$notificationMenuItemListId() {
		goToPage(notificationMenuItemListId.getValue());
	}
	public void onClick$notificationMenuItemId() {
		goToPage(notificationMenuItemId.getValue());
	}
	
	public void onClick$offersNotificationMenuItemId() {
		goToPage(offersNotificationMenuItemId.getValue());
	}
	
	public void onClick$autoSmsReportsMenuItemId() {
		goToPage(autoSmsReportsMenuItemId.getValue());
	}
	
	public void onClick$autoEmailMenuItemIdBeeId(){
		session.removeAttribute("editCustomTemplate");
		session.removeAttribute("fromAddNewBtn");
		session.removeAttribute("typeOfEmail");
			goToPage(autoEmailMenuItemIdBeeId.getValue());
		}
	
	public void onClick$myContactsMenuItemId(){
		goToPage(myContactsMenuItemId.getValue());
	}
	public void onClick$uploadContactsMenuItemId(){
		goToPage(uploadContactsMenuItemId.getValue());
	}
	public void onClick$singleViewOfContactsMenuItemId(){
		goToPage(singleViewOfContactsMenuItemId.getValue());
	}
	public void onClick$promoteCenterMenuItemId(){
		goToPage(promoteCenterMenuItemId.getValue());
	}
	
	public void onClick$doubleOptInMenuItemId(){
		goToPage(doubleOptInMenuItemId.getValue());
	}
	
	public void onClick$manageContactsMenuItemId(){
		goToPage(manageContactsMenuItemId.getValue());
	}
	public void onClick$manageSegmentsMenuItemId(){
		session.setAttribute("editSegment", null);
		goToPage(manageSegmentsMenuItemId.getValue());
	}
	public void onClick$mySegmentsMenuItemId() {
		
		goToPage(mySegmentsMenuItemId.getValue());
	}
	public void onClick$parentalConsentMenuItemId() {
		
		goToPage(parentalConsentMenuItemId.getValue());
		
	}
	public void onClick$webformsMenuItemId(){
		goToPage(webformsMenuItemId.getValue());
	}

	public void onClick$autoEmailMenuItemId(){
	session.removeAttribute("editCustomTemplate");
	session.removeAttribute("fromAddNewBtn");
	session.removeAttribute("typeOfEmail");
		goToPage(autoEmailMenuItemId.getValue());
	}

	public void onClick$autoSmsMenuItemId(){
	session.removeAttribute("editSmsTemplate");
	session.removeAttribute("fromAddNewBtn");
	session.removeAttribute("typeOfSms");
	if(isOptedForSMS())	goToPage(autoSmsMenuItemId.getValue());
	}

	public void onClick$manageBcrmSegmentsMenuItemId() {
		
		goToPage(manageBcrmSegmentsMenuItemId.getValue());
		
		
	}
	public void onClick$homesPassedSegmentsMenuItemId() {
		
		goToPage(homesPassedSegmentsMenuItemId.getValue());
	}
	
	/***********Loyalty*************/
	private Menuitem createLtyPrgmMenuItemId,ltyMenuTransferCardMenuItemId,myLtyPrgmsMenuItemId ,ltyCustomerLookUpMenuItemId,ltyCustomerLookUpAndRedeemMenuItemId,ltyDashboardMenuItemId, ltyTransferCardMenuItemId, fraudLtyPrgmMenuItemId, customerLookUpMenuItemId,ltyCustomerLookUpFBBMenuItemId,storeFilesUploadMenuItemId,addMemberMenuItemId;
	
	public void onClick$storeFilesUploadMenuItemId(){
		goToPage(storeFilesUploadMenuItemId.getValue());
	}
	public void onClick$createLtyPrgmMenuItemId(){
		goToPage(createLtyPrgmMenuItemId.getValue());
	}
	public void onClick$ltyDashboardMenuItemId(){
		goToPage(ltyDashboardMenuItemId.getValue());
	}
	public void onClick$myLtyPrgmsMenuItemId(){
		goToPage(myLtyPrgmsMenuItemId.getValue());
	}
	public void onClick$ltyCustomerLookUpMenuItemId(){
		goToPage(ltyCustomerLookUpMenuItemId.getValue());
	}
	public void onClick$ltyCustomerLookUpAndRedeemMenuItemId() {
		goToPage(ltyCustomerLookUpAndRedeemMenuItemId.getValue());
	}
	public void onClick$ltyCustomerLookUpFBBMenuItemId(){
		goToPage(ltyCustomerLookUpFBBMenuItemId.getValue());
	}
	public void onClick$ltyTransferCardMenuItemId(){
		goToPage(ltyTransferCardMenuItemId.getValue());
	}
	
	public void onClick$ltyMenuTransferCardMenuItemId(){
		goToPage(ltyMenuTransferCardMenuItemId.getValue());
	}
	 public void onClick$fraudLtyPrgmMenuItemId(){
	   goToPage(fraudLtyPrgmMenuItemId.getValue());
  	}
	
	public void onClick$customerLookUpMenuItemId(){
		goToPage(customerLookUpMenuItemId.getValue());
	}
	public void onClick$addMemberMenuItemId(){
		goToPage(addMemberMenuItemId.getValue());
	}
	/***********Assets*************/
	private Menuitem myImagesMenuItemId,myTemplatesMenuItemId;
	public void onClick$myImagesMenuItemId(){
		goToPage(myImagesMenuItemId.getValue());
	}
	public void onClick$myTemplatesMenuItemId(){
		goToPage(myTemplatesMenuItemId.getValue());
	}
	/***********Reports*************/
	private Menuitem reportsMenuItemId,autoEmailReportsMenuItemId,smsCampaignReportsMenuItemId,WACampaignReportsMenuItemId,optIntelReportsMenuItemId,
				couponReportsMenuItemId,smsKeywordUsageReportsMenuItemId,
				viewLoyaltyCardsMenuItemId,loyaltyReportMenuItemId,digitalReceiptMenuItemId,eventTriggerNewReportMenuItemId,eReceiptMenuItemId;
	
	public void onClick$reportsMenuItemId(){
		goToPage(reportsMenuItemId.getValue());
	}
	public void onClick$autoEmailReportsMenuItemId(){
		goToPage(autoEmailReportsMenuItemId.getValue());
	}
	
	public void onClick$smsCampaignReportsMenuItemId(){
		if(isOptedForSMS())goToPage(smsCampaignReportsMenuItemId.getValue());
	}
	public void onClick$WACampaignReportsMenuItemId(){
		if(isOptedForWA())goToPage(WACampaignReportsMenuItemId.getValue());
	}
	
	public void onClick$smsKeywordUsageReportsMenuItemId() {
		
		if(isOptedForSMS() && isOptedForSMSKeywords())goToPage(smsKeywordUsageReportsMenuItemId.getValue());
	}
	
	public void onClick$optIntelReportsMenuItemId() {
		goToPage(optIntelReportsMenuItemId.getValue());
	}
	public void onClick$eventTriggerNewReportMenuItemId() {
		goToPage(eventTriggerNewReportMenuItemId.getValue());
	}
	public void onClick$couponReportsMenuItemId() {
		goToPage(couponReportsMenuItemId.getValue());
	}
	public void onClick$viewLoyaltyCardsMenuItemId() {
		goToPage(viewLoyaltyCardsMenuItemId.getValue());
	}
	
	public void onClick$loyaltyReportMenuItemId() {
		goToPage(loyaltyReportMenuItemId.getValue());
	}
	
	public void onClick$digitalReceiptMenuItemId(){
		goToPage(digitalReceiptMenuItemId.getValue());
	}
	
	
	public void onClick$eReceiptMenuItemId(){
		goToPage(eReceiptMenuItemId.getValue());
	}
	
	
	/***********Marketing Tools *************/
	private Menuitem eventTriggersMenuItemId;
	public void onClick$eventTriggersMenuItemId(){
		goToPage(eventTriggersMenuItemId.getValue());
	}
	
	
	private Menuitem ReferalSettingsMenuItemId;
	public void onClick$ReferalSettingsMenuItemId(){
		goToPage(ReferalSettingsMenuItemId.getValue());
	}
	
	
	private Menuitem ReferalReportsMenuItemId;
	public void onClick$ReferalReportsMenuItemId(){
		goToPage(ReferalReportsMenuItemId.getValue());
	}
	
	
	
	/***********My Account*************/
	private Menuitem myProfileMenuItemId,recentActivityMenuItemId, 
	mySharingsMenuItemId, manageusersMenuItemId, faqMenuItemId, termsMenuItemId, billingAdmingMenuItemId, adminMySharingsMenuItemId,posSettingsMenuItemId, 
	organizationStoresMenuItemId, bcrmSettingsMenuItemId,listSettingsMenuItemId,updateSubscriptionMenuItemId,manageOptSyncUsersMenuItemId,
	fileDownloadMenuItemId, loyaltySettingsMenuItemId, ftpSettingsMenuItemId,zoneAdmingMenuItemId,manageItemsMenuItemId;
	
	/*public void onClick$myProfileMqsMenuItemId(){
		//goToPage(smsCampaignReportsMenuItemId.getValue());
		loadMQSpage("My Profile",myProfileMqsMenuItemId.getValue());
	}*/
	public void onClick$fileDownloadMenuItemId(){
		goToPage(fileDownloadMenuItemId.getValue());
	}
	
	public void onClick$myProfileMenuItemId(){
		goToPage(myProfileMenuItemId.getValue());
	}
	
	public void onClick$recentActivityMenuItemId(){
		goToPage(recentActivityMenuItemId.getValue());
	}
	
	public void onClick$mySharingsMenuItemId(){
		goToPage(mySharingsMenuItemId.getValue());
	}
	
	public void onClick$manageusersMenuItemId() {
		goToPage(manageusersMenuItemId.getValue());
		
	}
	public void onClick$faqMenuItemId() {
		Sessions.getCurrent().setAttribute("menuFaqSrc", "FAQ");
		goToPage(faqMenuItemId.getValue());
		
	}
	
	public void onClick$termsMenuItemId() {
		Sessions.getCurrent().setAttribute("menuTermsSrc", "Terms and Conditions");
		goToPage(termsMenuItemId.getValue());
		
	}
	
	
	public void onClick$billingAdmingMenuItemId() {
		goToPage(billingAdmingMenuItemId.getValue());
		
	}
	
	public void onClick$zoneAdmingMenuItemId() {
		goToPage(zoneAdmingMenuItemId.getValue());
		
	}
		
	
	public void onClick$adminMySharingsMenuItemId() {
		
		goToPage(adminMySharingsMenuItemId.getValue());
		
		
	}
	public void onClick$organizationStoresMenuItemId() {
		
		goToPage(organizationStoresMenuItemId.getValue());
		
		
	}
	public void onClick$manageItemsMenuItemId() {
		
		goToPage(manageItemsMenuItemId.getValue());
		
		
	}
	
	
	
	
	public void onClick$posSettingsMenuItemId() {
		goToPage(posSettingsMenuItemId.getValue());
	}
	
	public void onClick$listSettingsMenuItemId() {
		goToPage(listSettingsMenuItemId.getValue());
	}
	
	public void onClick$bcrmSettingsMenuItemId() {
		goToPage(bcrmSettingsMenuItemId.getValue());
	}
	
	public void onClick$updateSubscriptionMenuItemId() {
		goToPage(updateSubscriptionMenuItemId.getValue());
	}
	
	public void onClick$ftpSettingsMenuItemId() {
		goToPage(ftpSettingsMenuItemId.getValue());
	}
	
	public void onClick$manageOptSyncUsersMenuItemId() {
		
		
		if(isEnabledMointoring()){
			goToPage(manageOptSyncUsersMenuItemId.getValue());
		}
		else{
			MessageUtil.setMessage("You don't have access to this page.\n Please contact support at support@optculture.com.", "color:red;");
		}
	}
	
	public void onClick$loyaltySettingsMenuItemId(){
		Sessions.getCurrent().setAttribute("menuSrc", "MyAccount");
		goToPage(loyaltySettingsMenuItemId.getValue());
	}
	
	
	public boolean isEnabledMointoring(){
		boolean flag =  false;
		if(currentUser == null) currentUser = GetUser.getUserObj();
		OptSyncService optSyncService =  new OptSyncService();
		List<UpdateOptSyncData> updateOptSyncDataList = optSyncService.findOptSyncByUserId(currentUser.getUserId());
		if(updateOptSyncDataList != null && updateOptSyncDataList.size() > 0){
			for (UpdateOptSyncData updateOptSyncData : updateOptSyncDataList) {
				if( OCConstants.Opt_SYNC_ENABLE_MOINTORING.equalsIgnoreCase(updateOptSyncData.getEnabledOptSyncFlag())  && updateOptSyncData.getEnabledOptSyncFlag() != null){
					flag = true;
					break;
				}
			}//for
		}
		return flag;
	}
	
	
	/***********Billing*************/
	
	private Menuitem myBillsMqsMenuItemId,myPaymentsMqsMenuItemId,AccSummaryMqsMenuItemId,usageInfoMqsMenuItemId,topupMqsMenuItemId;
	
	public void onClick$myBillsMqsMenuItemId(){
		//goToPage(smsCampaignReportsMenuItemId.getValue());
		loadMQSpage("My Bills",myBillsMqsMenuItemId.getValue());
	}
	public void onClick$myPaymentsMqsMenuItemId(){
		//goToPage(smsCampaignReportsMenuItemId.getValue());
		loadMQSpage("My Payments",myPaymentsMqsMenuItemId.getValue());
	}
	public void onClick$AccSummaryMqsMenuItemId(){
		//goToPage(smsCampaignReportsMenuItemId.getValue());
		loadMQSpage("Account Summary",AccSummaryMqsMenuItemId.getValue());
	}
	public void onClick$usageInfoMqsMenuItemId(){
		//goToPage(smsCampaignReportsMenuItemId.getValue());
		loadMQSpage("Usage Info",usageInfoMqsMenuItemId.getValue());
	}
	public void onClick$topupMqsMenuItemId(){
		//goToPage(smsCampaignReportsMenuItemId.getValue());
		loadMQSpage("Topup",topupMqsMenuItemId.getValue());
	}
	
	/***********Tickets*************/
	private Menuitem regTicketMqsMenuItemId,myTicketsMqsMenuItemId;
	public void onClick$regTicketMqsMenuItemId(){
		//goToPage(smsCampaignReportsMenuItemId.getValue());
		loadMQSpage("Register Ticket",regTicketMqsMenuItemId.getValue());
	}
	public void onClick$myTicketsMqsMenuItemId(){
		//goToPage(smsCampaignReportsMenuItemId.getValue());
		loadMQSpage("My Tickets",myTicketsMqsMenuItemId.getValue());
	}
	
	/***********Administrator*************/
	private Menuitem createuserMenuItemId,listusersMenuItemId,rightsManagementsMenuItemId,createOptSyncUserMenuItemId , latestSalesDetailsMenuItemId, editVmtaMenuItemId,
		dashBoardMenuItemId,programMenuItemId,scoreActivityMenuItemId,posSettingMenuItemId,  sparkBaseSettingMenuItemId,
		missingSalesReceiptsMenuItemId, bcrmSettingMenuItemId,couponMenuItemId,viewTicketsMenuItemId,viewSmsGatewaysMenuItemId,loyaltySettingMenuItemId,
		createCouponMenuItemId,manageusersAdminMenuItemId;
	public void onClick$createuserMenuItemId(){
		//goToPage(smsCampaignReportsMenuItemId.getValue());
		Executions.getCurrent().removeAttribute("type");
		Sessions.getCurrent().removeAttribute("editUserObj");
		goToPage(createuserMenuItemId.getValue());
	}
	public void onClick$listusersMenuItemId(){
		//goToPage(smsCampaignReportsMenuItemId.getValue());
		goToPage(listusersMenuItemId.getValue());
	}
	
	public void onClick$manageusersAdminMenuItemId(){
		//goToPage(smsCampaignReportsMenuItemId.getValue());
		goToPage(manageusersAdminMenuItemId.getValue());
	}
	public void onClick$rightsManagementsMenuItemId(){
		goToPage(rightsManagementsMenuItemId.getValue());
	}
	
	public void onClick$createOptSyncUserMenuItemId(){
		goToPage(createOptSyncUserMenuItemId.getValue());
	}
	public void onClick$loyaltySettingMenuItemId(){
		Sessions.getCurrent().setAttribute("menuSrc", "Administrator");
		goToPage(loyaltySettingMenuItemId.getValue());
	}
	
	public void onClick$latestSalesDetailsMenuItemId(){
		//goToPage(smsCampaignReportsMenuItemId.getValue());
		goToPage(latestSalesDetailsMenuItemId.getValue());
	}
	
	public void onClick$editVmtaMenuItemId(){
		//goToPage(smsCampaignReportsMenuItemId.getValue());
		goToPage(editVmtaMenuItemId.getValue());
	}
	public void onClick$dashBoardMenuItemId(){
		//goToPage(smsCampaignReportsMenuItemId.getValue());
		goToPage(dashBoardMenuItemId.getValue());
	}
	public void onClick$programMenuItemId(){
		//goToPage(smsCampaignReportsMenuItemId.getValue());
		goToPage(programMenuItemId.getValue());
	}
	
	public void onClick$scoreActivityMenuItemId(){
		//goToPage(smsCampaignReportsMenuItemId.getValue());
		goToPage(scoreActivityMenuItemId.getValue());
	}
	
	public void onClick$missingSalesReceiptsMenuItemId(){
		//goToPage(smsCampaignReportsMenuItemId.getValue());
		goToPage(missingSalesReceiptsMenuItemId.getValue());
	}
	
	private Menuitem digiRcptMenuItemId;
	public void onClick$digiRcptMenuItemId(){
		//goToPage(smsCampaignReportsMenuItemId.getValue());
		goToPage(digiRcptMenuItemId.getValue());
	}
	
	public void onClick$posSettingMenuItemId() {
		goToPage(posSettingMenuItemId.getValue());
	}
	 public void onClick$sparkBaseSettingMenuItemId() {
			goToPage(sparkBaseSettingMenuItemId.getValue());
	 }
	 
	 public void onClick$bcrmSettingMenuItemId() {
			goToPage(bcrmSettingMenuItemId.getValue());
	 }
	 
	 public void onClick$couponMenuItemId() {
		 goToPage(couponMenuItemId.getValue()); 
	 }
	 
	 public void onClick$createCouponMenuItemId() {
		 Sessions.getCurrent().removeAttribute("EDIT_COUPON_OBJECT");
		 goToPage(createCouponMenuItemId.getValue()); 
	 }
	 
	 public void onClick$viewTicketsMenuItemId() {
		 goToPage(viewTicketsMenuItemId.getValue()); 
	 }
	 
	 public void onClick$viewSmsGatewaysMenuItemId() {
		 goToPage(viewSmsGatewaysMenuItemId.getValue()); 
	 }
	 
	 
	/***********Navigation Divs*************/
	private Div campCreationFirstStepId,campCreationSecondStepId,campCreationThirdStepId,campCreationFourthStepId,
				campCreationFifthStepId,campCreationSixthStepId;
	
	
	public void onClick$campCreationFirstStepId(){
		goToCampaignPage(campCreationFirstStepId);
	}
	public void onClick$campCreationSecondStepId(){
		goToCampaignPage(campCreationSecondStepId);
	}
	public void onClick$campCreationThirdStepId(){
		goToCampaignPage(campCreationThirdStepId);
	}
	public void onClick$campCreationFourthStepId(){
		goToCampaignPage(campCreationFourthStepId);
	}
	public void onClick$campCreationFifthStepId(){
		goToCampaignPage(campCreationFifthStepId);
	}
	public void onClick$campCreationSixthStepId(){
		goToCampaignPage(campCreationSixthStepId);
	}
	
	/*private Menuitem galleryMenuItemId;
	public void onClick$galleryMenuItemId() {
		goToPage(galleryMenuItemId.getValue());
	}*/
	
	
	public void onClientInfo$indexWinId(ClientInfoEvent evt) {
  		try {

  			 // alert(evt.getDesktopWidth()+"x"+evt.getDesktopHeight()+"x"+evt.getColorDepth());
	   		if(session.getAttribute("clientTimeZone")==null) {
	   			
	   			session.setAttribute("screenWidth", evt.getScreenWidth());
	   			session.setAttribute("screenHeight", evt.getScreenHeight());
	   			
	   			session.setAttribute("desktopWidth", evt.getDesktopWidth());
	   			session.setAttribute("desktopHeight", evt.getDesktopHeight());
	   			
	  			session.setAttribute("clientTimeZone", evt.getTimeZone());
	  			/*Window win = (Window)org.mq.marketer.campaign.general.Utility.getComponentById("RMHomeWinId");
	  			logger.info("Window obect is==========>"+win);
	  			if(win!=null) {
	  				prepareData();
	  			}*/
	  			
	  			//logger.info("onClientInfo Client Time zone ============="+session.getAttribute("clientTimeZone"));
	  			setupIndexPAge();
	   		}
  		}catch(Exception e) { }
   	} // onClientInfo
	
	

	
	public void goToCampaignPage(Div tempDiv) {
		
		logger.info("Img="+tempDiv.getId()+" :: "+tempDiv.getStyle());
		logger.info("Img="+tempDiv.getId()+" :: "+tempDiv.getSclass());
		
		if(tempDiv.getSclass().contains("create_email_step_completed")) {
			
			if(tempDiv.getId().equals("campCreationFirstStepId")) { 
				Redirect.goTo(PageListEnum.CAMPAIGN_SETTINGS);
			} 
			else if(tempDiv.getId().equals("campCreationSecondStepId")) { 
				Redirect.goTo(PageListEnum.CAMPAIGN_MLIST);
			}
			else if(tempDiv.getId().equals("campCreationThirdStepId")) { 
				Redirect.goTo(PageListEnum.CAMPAIGN_LAYOUT);
			}
			else if(tempDiv.getId().equals("campCreationFourthStepId")) {
				Campaigns campaign=(Campaigns)Sessions.getCurrent().getAttribute("campaign");
				if(campaign==null || campaign.getEditorType()==null) return;

				if(campaign.getEditorType().equalsIgnoreCase("blockEditor")) {
					
					Redirect.goTo(PageListEnum.CAMPAIGN_BLOCK_EDITOR);
				}
				else if(campaign.getEditorType().equalsIgnoreCase("plainTextEditor") ||
						campaign.getEditorType().equalsIgnoreCase(Constants.CAMP_EDTYPE_EXTERNAL_EMAIL)) {
					
					Redirect.goTo(PageListEnum.CAMPAIGN_PLAIN_EDITOR);
				}
				else if(campaign.getEditorType().equalsIgnoreCase("beeEditor") ||
						campaign.getEditorType().equalsIgnoreCase(Constants.CAMP_EDTYPE_EXTERNAL_EMAIL)) {
					
					Redirect.goTo(PageListEnum.CAMPAIGN_HTML_BEE_EDITOR);
				}
				else if(campaign.getEditorType().equalsIgnoreCase("plainHtmlEditor")) {
					
					Redirect.goTo(PageListEnum.CAMPAIGN_HTML_EDITOR);
				}
			}
			else if(tempDiv.getId().equals("campCreationFifthStepId")) { 
				Redirect.goTo(PageListEnum.CAMPAIGN_TEXT_MESSAGE);
			}
			else if(tempDiv.getId().equals("campCreationSixthStepId")) { 
				Redirect.goTo(PageListEnum.CAMPAIGN_FINAL);
			}
		} // outer if
		
	} // goToCampaignPage
	
	public void checkMQS(){
		String useMQS = PropertyUtil.getPropertyValueFromDB("useMQS");
		if(useMQS!=null){
			if(!useMQS.equalsIgnoreCase("true")){
				Menubar menubar = (Menubar)Utility.getComponentById("indexMenubarId");
				List menus = menubar.getChildren();
				for(Object obj:menus){
					if(obj instanceof Menu){
						doMenuCheck((Menu)obj,false);
					}
				}//for
			}//if
		}//if
	}//checkMQS
	
	public void doMenuCheck(Menu menu,boolean visible) {
		
		String id = menu.getId();
		if(!id.contains("mqs")){
			Menupopup menuPopup= menu.getMenupopup();
			List<Component> mis = menuPopup.getChildren();
			String miValue = "";
			for(Component eachComp : mis){
				if(!(eachComp instanceof Menuitem)) {
					continue;
				}
				Menuitem mi=(Menuitem)eachComp;
				miValue = mi.getId();
				if(miValue.contains("Mqs")){
					mi.setVisible(visible);
				}
			}
		}else
			menu.setVisible(visible);
	}
	
	public void loadMQSpage(String mqsPageHeader,String mqsPageToLoad){
		session.setAttribute("mqsPageHeader", mqsPageHeader);
		session.setAttribute("mqsPageToLoad", mqsPageToLoad);
		Redirect.goTo(PageListEnum.EMPTY);
		Redirect.goTo(PageListEnum.MQS);
	}
	
	/***********Loyalty Navigation Divs*************/
	private Div ltyPrgmFirstStepId,ltyPrgmSecondStepId,ltyPrgmThirdStepId,ltyPrgmFourthStepId,
				ltyPrgmFifthStepId,ltyPrgmSixthStepId;
	
	
	public void onClick$ltyPrgmFirstStepId(){
		goToLtyPage(ltyPrgmFirstStepId);
	}
	public void onClick$ltyPrgmSecondStepId(){
		goToLtyPage(ltyPrgmSecondStepId);
	}
	public void onClick$ltyPrgmThirdStepId(){
		goToLtyPage(ltyPrgmThirdStepId);
	}
	public void onClick$ltyPrgmFourthStepId(){
		goToLtyPage(ltyPrgmFourthStepId);
	}
	public void onClick$ltyPrgmFifthStepId(){
		goToLtyPage(ltyPrgmFifthStepId);
	}
	public void onClick$ltyPrgmSixthStepId(){
		goToLtyPage(ltyPrgmSixthStepId);
	}
	
	private void goToLtyPage(Div tempDiv) {

		
		logger.info("Img="+tempDiv.getId()+" :: "+tempDiv.getStyle());
		logger.info("Img="+tempDiv.getId()+" :: "+tempDiv.getSclass());
		
		if(tempDiv.getSclass().contains("create_email_step_completed")) {
			
			if(tempDiv.getId().equals("ltyPrgmFirstStepId")) { 
				Redirect.goTo(PageListEnum.LOYALTY_CREATE_PROGRAM);
			} 
			else if(tempDiv.getId().equals("ltyPrgmSecondStepId")) {
				
				Long prgmId = (Long)session.getAttribute("programId");
				LoyaltyProgramService ltyPrgmSevice = new LoyaltyProgramService();
				LoyaltyProgram progmObj = ltyPrgmSevice.getProgmObj(prgmId);
				
				if(progmObj.getRewardType()!=null && progmObj.getRewardType().equalsIgnoreCase(OCConstants.REWARD_TYPE_PERK))
					Redirect.goTo(PageListEnum.LOYALTY_PERK_RULES);
				else 
				    Redirect.goTo(PageListEnum.LOYALTY_RULES);
					
			}
			else if(tempDiv.getId().equals("ltyPrgmThirdStepId")) { 
				Redirect.goTo(PageListEnum.LOYALTY_ADD_CARDS);
			}
			else if(tempDiv.getId().equals("ltyPrgmFourthStepId")) {
				Redirect.goTo(PageListEnum.LOYALTY_ADDITIONAL_SETTINGS);
			}
			else if(tempDiv.getId().equals("ltyPrgmFifthStepId")) { 
				Redirect.goTo(PageListEnum.LOYALTY_AUTO_COMMUNICATION);
			}
			else if(tempDiv.getId().equals("ltyPrgmSixthStepId")) { 
				Redirect.goTo(PageListEnum.LOYALTY_PROGRAM_OVERVIEW);
			}
		} // outer if
		
	
		
	}
}
