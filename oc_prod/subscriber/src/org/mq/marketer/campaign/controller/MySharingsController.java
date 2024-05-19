package org.mq.marketer.campaign.controller;


import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.MailingList;
import org.mq.marketer.campaign.beans.SegmentRules;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.beans.UsersDomains;
import org.mq.marketer.campaign.dao.MailingListDao;
import org.mq.marketer.campaign.dao.MailingListDaoForDML;
import org.mq.marketer.campaign.dao.SegmentRulesDao;
import org.mq.marketer.campaign.dao.SegmentRulesDaoForDML;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PageUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Tabbox;


public class MySharingsController extends GenericForwardComposer {
	
	UsersDao usersDao=null; 
	
	private Button createDefaultShareBtnId;

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);	
	Users currentUser;
	List<Users> sharedUserList;
	private Div selectListDivId,selectSegmentDivId;
	private Label selectedListsLbId,selectedSegmentsLbId;
	
	private Listbox shareMlLbId,shareSegmentsLbId;
	private MailingListDao mailingListDao;
	private MailingListDaoForDML mailingListDaoForDML;
	private SegmentRulesDao segmentRulesDao;
	private SegmentRulesDaoForDML segmentRulesDaoForDML;

	private MailingList mailingList;
	 
	public MySharingsController() {
		
		usersDao = (UsersDao)SpringUtil.getBean("usersDao");
		mailingListDao = (MailingListDao)SpringUtil.getBean("mailingListDao");
		mailingListDaoForDML = (MailingListDaoForDML)SpringUtil.getBean("mailingListDaoForDML");
		segmentRulesDaoForDML = (SegmentRulesDaoForDML)SpringUtil.getBean("segmentRulesDaoForDML");
		segmentRulesDao = (SegmentRulesDao)SpringUtil.getBean("segmentRulesDao");
		String style = "font-weight:bold;font-size:15px;color:#313031;font-family:Arial,Helvetica,sans-serif;align:left";
     	PageUtil.setHeader("Share","",style,true);
	}
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
        
		currentUser = GetUser.getUserObj();
		//sharedUserList = usersDao.getSharedUsersByParentUserId(currentUser.getUserId());
		Long currUserId = currentUser.getUserId(); 
		List<MailingList> userMlList = mailingListDao.findAllByCurrentUser(currUserId);
		
		
		if(userMlList == null || userMlList.size()==0) {
			
			logger.debug("no mailingLists for this user "+currentUser.getUserId()+" or under his shared user too ");
			return;
			
		}
		
		String listName = "";
		Listitem li = null;
		for (MailingList mailingList : userMlList) {
		
			li = new Listitem(mailingList.getListName());
			li.setValue(mailingList);
			
			li.setParent(shareMlLbId);
			
			/*if(mailingList.getUsers().getUserId().longValue() != currentUser.getUserId().longValue()) {
				
				shareMlLbId.addItemToSelection(li);
				
				if(listName.length()> 0) listName += ",";
				listName += mailingList.getListName();
				
			}
*/			List<UsersDomains> sharedToDomains = mailingListDao.findSharedToDomainsByListID(mailingList.getListId());
			if(sharedToDomains != null && sharedToDomains.size() > 0) {
				
				shareMlLbId.addItemToSelection(li);
				
				if(listName.length()> 0) listName += ",";
				listName += mailingList.getListName();
				
				
			}
			
			
			
			
			
		}//for
		selectedListsLbId.setValue(listName);
		if(listName.trim().length() > 0) {
			
			selectListDivId.setVisible(true);
		}else {
			
			selectListDivId.setVisible(false);
		}
		
		
		
	} // doAfterCompose
	
	private Tabbox shareTabBoxId;
	public void onSelect$shareTabBoxId() {
		
		if(shareTabBoxId.getSelectedIndex() == 1) {
			
			List<SegmentRules> segRuleList = segmentRulesDao.findAllByCurrentUser(currentUser.getUserId());
			
			if(segRuleList == null || segRuleList.size() <= 0) {
				
				logger.debug("no Segments for this user "+currentUser.getUserId()+" or under his shared user too ");
				return;
				
			}
			
			Components.removeAllChildren(shareSegmentsLbId);
			
			String listName = "";
			Listitem li = null;
			for (SegmentRules segRule: segRuleList) {
			
				li = new Listitem(segRule.getSegRuleName());
				li.setValue(segRule);
				
				li.setParent(shareSegmentsLbId);
				
				/*if(segRule.getUserId().longValue() != currentUser.getUserId().longValue()) {
					
					shareSegmentsLbId.addItemToSelection(li);
					
					if(listName.length()> 0) listName += ",";
					listName += segRule.getSegRuleName();
					
				}*/
				
				
				List<UsersDomains> sharedToDomains = segmentRulesDao.findSharedToDomainsByListID(segRule.getSegRuleId());
				if(sharedToDomains != null &&  sharedToDomains.size() > 0) {
					
					shareSegmentsLbId.addItemToSelection(li);
					
					if(listName.length()> 0) listName += ",";
					listName += segRule.getSegRuleName();
					
				}
				
				
			}//for
			selectedSegmentsLbId.setValue(listName);
			if(listName.trim().length() > 0) {
				
				selectSegmentDivId.setVisible(true);
			}else {
				
				selectSegmentDivId.setVisible(false);
			}
			
			
			
		}
		
		
		
	}
	
	public void onClick$mlShareBtnId() {
		
		if(shareMlLbId.getSelectedCount() == 0) {
			
			
			MessageUtil.setMessage("Please select list(s) to be shared.", "color:red;");
			return;
			
		}
		
		/*if(Messagebox.show("Confirm to create default share for User "+GetUser.getOnlyUserName(),
				"Confirm", Messagebox.YES | Messagebox.NO, Messagebox.EXCLAMATION)==Messagebox.NO) {
			return;
		}*/
		try {
			int confirm = Messagebox.show("Are you sure you want to create default share for user?" +GetUser.getOnlyUserName(), "Confirm",Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
			if(confirm == Messagebox.OK){		
		try {
			MailingList ml = null;
			Set<Listitem> itemList = shareMlLbId.getSelectedItems();
			
			/*Users shUser = sharedUserList.get(0);
			
			Set<UsersDomains> domainSet = currentUser.getUserDomains();
			logger.info("size ::"+domainSet.size());
			Iterator< UsersDomains> it = domainSet.iterator();
			String domainIds = "";
			while (it.hasNext()) {
				UsersDomains usersDomains = (UsersDomains) it.next();
				domainIds = usersDomains.getDomainId().longValue()+"";
			}*/
			
			
		/*	List<UsersDomains> domainList = usersDao.findDomainsByIds(domainIds);
			
			Set<UsersDomains> domSet = new HashSet<UsersDomains>();
			domSet.addAll(domainList);*/
			 List<UsersDomains> domainsList = usersDao.getAllDomainsByUser(currentUser.getUserId());
			
			for (Listitem li : itemList) {
				
				try {
					ml = (MailingList)li.getValue();
					
					/*Set<UsersDomains> sharedToDomain = new HashSet<UsersDomains>();
					sharedToDomain.addAll(currentUser.getUserDomains());
					*/
					
					//logger.info("share ::"+sharedToDomain.iterator().next().getDomainId().longValue()+" set "+sharedToDomain+"ml "+ml.getListId().longValue());
					Set<UsersDomains> domainSet = new HashSet<UsersDomains>();
					
					domainSet.addAll(domainsList);
					ml.setSharedToDomain(domainSet);
					mailingListDaoForDML.saveOrUpdate(ml);
					logger.info("ml domains ::"+ml.getSharedToDomain().size());
					/*if(li.isSelected() && ml.getUsers().getUserId().longValue() == currentUser.getUserId().longValue()) {
						ml.setUsers(shUser);
						mailingListDao.saveOrUpdate(ml);

					}
					else if(!li.isSelected() && ml.getUsers().getUserId().longValue() != currentUser.getUserId().longValue()) {
						ml.setUsers(currentUser);
						mailingListDao.saveOrUpdate(ml);
						
						
					}*/
				} catch (Exception e) {
					// TODO Auto-generated catch block
					logger.error("Exception ::", e);
				}
				
			}//for
			
		MessageUtil.setMessage("Shared mailing list settings updated successfully.", "color:green;");
		//MessageUtil.setMessage("Selected mailing list updated successfully.", "color:green;");
		
				return;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
		}
			}
		}catch(Exception e){
			
		}
		
	}//onClick$mlShareBtnId()
	
	public void onClick$segmentShareBtnId() {
		
		
		if(shareSegmentsLbId.getSelectedCount() == 0) {
			
			
			MessageUtil.setMessage("Please select segment(s) to be shared.", "color:red;");
			return;
			
		}
		
		
		/*if(Messagebox.show("Confirm to create default Share for User "+GetUser.getOnlyUserName(),
				"Confirm", Messagebox.YES | Messagebox.NO, Messagebox.EXCLAMATION)==Messagebox.NO) {
			return;
		}*/
		try {
			int confirm = Messagebox.show("Are you sure you want to create default share for user?" +GetUser.getOnlyUserName(), "Confirm",Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
			if(confirm == Messagebox.OK){		
		
		try {
			SegmentRules segRule = null;
			Set<Listitem> itemList = shareSegmentsLbId.getSelectedItems();
			
			
			List<UsersDomains> domainsList = usersDao.getAllDomainsByUser(currentUser.getUserId());
			for (Listitem li : itemList) {
				
				segRule = (SegmentRules)li.getValue();
/*				if(li.isSelected() && segRule.getUserId().longValue() == currentUser.getUserId().longValue()) {
					segRule.setUserId(shUser.getUserId());
					segmentRulesDao.saveOrUpdate(segRule);

				}
				else if(!li.isSelected() && segRule.getUserId().longValue() != currentUser.getUserId().longValue()) {
					segRule.setUserId(currentUser.getUserId());
					segmentRulesDao.saveOrUpdate(segRule);
					
					
				}*/
				Set<UsersDomains> domainSet = new HashSet<UsersDomains>();
				
				domainSet.addAll(domainsList);
				segRule.setSharedToDomain(domainSet);
				segmentRulesDaoForDML.saveOrUpdate(segRule);
				
			}//for
			
				/*Messagebox.show("Shared segment settings updated successfully.");*/
			MessageUtil.setMessage("Shared segment settings updated successfully.", "color:blue;");
			return;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
		}
			}
			}catch(Exception e){
				
				
			}
		
	}//onClick$segmentShareBtnId() 
	
	
	
	public void onSelect$shareMlLbId() {
		
		
		Set<Listitem> selectedItems = shareMlLbId.getSelectedItems();
		String listNamesStr="";
		for (Listitem li : selectedItems) {
			
			MailingList mailingList = (MailingList) li.getValue();			
			if(listNamesStr.length() != 0) listNamesStr+=",";
			listNamesStr += mailingList.getListName() ;
		}
		
		selectedListsLbId.setValue(listNamesStr);
		
		//visibility div of Selected list
		if(selectedListsLbId.getValue().length() != 0) {
			selectListDivId.setVisible(true);
		}else{
			selectListDivId.setVisible(false);
		}
		
		
	}
	
	public void onSelect$shareSegmentsLbId() {
		
		
		Set<Listitem> selectedItems = shareSegmentsLbId.getSelectedItems();
		String listNamesStr="";
		for (Listitem li : selectedItems) {
			
			SegmentRules segRule = (SegmentRules) li.getValue();			
			if(listNamesStr.length() != 0) listNamesStr+=",";
			listNamesStr += segRule.getSegRuleName();
		}
		
		selectedSegmentsLbId.setValue(listNamesStr);
		
		//visibility div of Selected list
		if(selectedSegmentsLbId.getValue().length() != 0) {
			selectSegmentDivId.setVisible(true);
		}else{
			selectSegmentDivId.setVisible(false);
		}
		
		
	}
	
}
