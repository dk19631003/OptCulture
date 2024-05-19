package org.mq.marketer.campaign.controller.useradmin;

import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.MailingList;
import org.mq.marketer.campaign.beans.SegmentRules;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.beans.UsersDomains;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.controller.MySharingsController;
import org.mq.marketer.campaign.dao.MailingListDao;
import org.mq.marketer.campaign.dao.MailingListDaoForDML;
import org.mq.marketer.campaign.dao.SegmentRulesDao;
import org.mq.marketer.campaign.dao.SegmentRulesDaoForDML;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.dao.UsersDomainsDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PageUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Div;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Tabbox;

public class MysharingsController extends GenericForwardComposer{

	
	Users currentUser;
	List<Users> sharedUserList;
	private Div selectListDivId,selectSegmentDivId;
	private Label selectedListsLbId,selectedSegmentsLbId;
	
	private Listbox shareMlLbId,shareSegmentsLbId,shareMlWithDomainLbId,shareSegmentsWithDomainLbId,shareListlbId,shareSegmentListlbId;
	private MailingListDao mailingListDao;
	private MailingListDaoForDML mailingListDaoForDML;
	private SegmentRulesDao segmentRulesDao;
	private UsersDomainsDao usersDomainsDao;
	private SegmentRulesDaoForDML segmentRulesDaoForDML;
	
	private UsersDao usersDao=null; 
	//private List<UsersDomains> domainsList;
	
	private Set<UsersDomains> domainsSet;
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	public MysharingsController() {
		
		usersDao = (UsersDao)SpringUtil.getBean("usersDao");
		mailingListDao = (MailingListDao)SpringUtil.getBean("mailingListDao");
		mailingListDaoForDML = (MailingListDaoForDML)SpringUtil.getBean("mailingListDaoForDML");
		segmentRulesDao = (SegmentRulesDao)SpringUtil.getBean("segmentRulesDao");
		usersDomainsDao = (UsersDomainsDao)SpringUtil.getBean("usersDomainsDao");
		segmentRulesDaoForDML = (SegmentRulesDaoForDML)SpringUtil.getBean("segmentRulesDaoForDML");
		session = Sessions.getCurrent();
		
		String style = "font-weight:bold;font-size:15px;color:#313031;font-family:Arial,Helvetica,sans-serif;align:left";
     	PageUtil.setHeader("Share","",style,true);
	}
	
	
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		// TODO Auto-generated method stub
		super.doAfterCompose(comp);
		
		currentUser = GetUser.getUserObj();
		//sharedUserList = usersDao.getSharedUsersByParentUserId(currentUser.getUserId());
		Long currUserId = currentUser.getUserId(); 
		
		
		//domainsList =  usersDomainsDao.FindByOrgId(currentUser.getUserOrganization().getUserOrgId());
		List<UsersDomains> domainsList = usersDao.getAllDomainsByUser(currentUser.getUserId());
		
		
		domainsSet = new HashSet<UsersDomains>();
		
		domainsSet.addAll(domainsList);
		
		List<MailingList> userMlList = mailingListDao.findAllByCurrentUser(currUserId);
		
		
		if(userMlList == null || userMlList.size()==0) {
			
			logger.debug("No mailingLists for this user "+currentUser.getUserId()+" or under his shared user too ");
			return;
			
		}
		
		if(domainsSet == null || domainsSet.size() == 0) {
			
			logger.debug("No Domainss for this user's organization "+currentUser.getUserId()+"");
			return;
			
			
		}
		
		Listitem li = null;
		for(UsersDomains userDomains : domainsSet) {
			
			li = new Listitem(userDomains.getDomainName());
			li.setValue(userDomains);
			li.setParent(shareMlWithDomainLbId);
			
			
			
			
		}
		
		
		for (MailingList mailingList : userMlList) {
		
			li = new Listitem(mailingList.getListName());
			li.setValue(mailingList);
			
			li.setParent(shareMlLbId);
			
			/*if(mailingList.getUsers().getUserId().longValue() != currentUser.getUserId().longValue()) {
				
				shareMlLbId.addItemToSelection(li);
				
			}*/
			
			
		}//for
		
		prepareExistingMlShare();
		
		
		
		
		
	}
	
	public void prepareExistingMlShare() {
		
		//TODO need to prepare the existing share
		
		/*List<Users> sharedUsersList = usersDao.getSharedUsersByParentUserId(currentUser.getUserId());
		List<MailingList> shMlList = null;
		Set<UsersDomains> shUserDomains = null;*/
		String domainNameStr = null;
		
		/*for (Users shUsers : sharedUsersList) {
			
			shMlList = mailingListDao.findAllBySharedUser(shUsers.getUserId());
			shUserDomains = shUsers.getUserDomains();
			
			domainNameStr = prepareDomianNameStr(shUserDomains); 
			
			for (MailingList mailingList : shMlList) {
				
				prepareListItems(mailingList, domainNameStr);
				
			}
			
		}*/
		
		
		List<Listitem> itemList = shareMlLbId.getItems();
		for (Listitem listitem : itemList) {
			
			
			MailingList ml = listitem.getValue();
			
			List<UsersDomains> sharedToDomains = mailingListDao.findSharedToDomainsByListID(ml.getListId());
 			if(sharedToDomains != null &&  sharedToDomains.size() > 0) {
				
				domainNameStr = prepareDomianNameStr(sharedToDomains); 
				prepareListItems(ml, domainNameStr);
				
			}
			
			
			
		}//for
		
		
		
		
		
		
		
		
	}//prepareExistingShare()
	
	public void prepareExistingSegmentShare() {
		
		//TODO need to prepare the existing share
		
		/*List<Users> sharedUsersList = usersDao.getSharedUsersByParentUserId(currentUser.getUserId());
		List<SegmentRules> shSegList = null;
		Set<UsersDomains> shUserDomains = null;*/
		String domainNameStr = null;
		
		/*for (Users shUsers : sharedUsersList) {
			
			shSegList = segmentRulesDao.findAllBySharedUser(shUsers.getUserId());
			shUserDomains = shUsers.getUserDomains();
			
			domainNameStr = prepareDomianNameStr(shUserDomains); 
			
			for (SegmentRules segmentRule : shSegList) {
				
				prepareShareSegmentList(segmentRule, domainNameStr);
				
			}
			
		}*/
		
		List<Listitem> itemList = shareSegmentsLbId.getItems();
		for (Listitem listitem : itemList) {
			
			
			SegmentRules segRule = listitem.getValue();
			
			List<UsersDomains> sharedToDomains = segmentRulesDao.findSharedToDomainsByListID(segRule.getSegRuleId());
 			if(sharedToDomains != null &&  sharedToDomains.size() > 0) {
				
				domainNameStr = prepareDomianNameStr(sharedToDomains); 
				prepareShareSegmentList(segRule, domainNameStr);
				
			}
			
			
			
		}//for
		
		
		
		
	}//prepareExistingShare()
	
	
	public String prepareDomianNameStr(List<UsersDomains> shDomainSet) {
		
		String retStr = "";
		//List<UsersDomains> domainList = usersDomainsDao.findByIds(shDomainSet); 
		
		if(shDomainSet == null) {
			
			logger.debug("prolem in getting domains");
			return null;
			
		}
		for (UsersDomains usersDomains : shDomainSet) {
			
			if(retStr.length() > 0) retStr += ",";
			retStr += usersDomains.getDomainName();
			
			
			
		}
		
		/*for (UsersDomains usersDomains : shUserDomainSet) {
			
			if(retStr.length() > 0) retStr += ",";
			retStr += usersDomains.getDomainName();
			
			
			
		}*/
		
		return retStr;
		
	}
	
	
	
	
	
	
	private Tabbox shareTabBoxId;
	public void onSelect$shareTabBoxId() {
		
		if(shareTabBoxId.getSelectedIndex() == 1) {
			
			List<SegmentRules> segRuleList = segmentRulesDao.findAllByCurrentUser(currentUser.getUserId());
			
			if(segRuleList == null || segRuleList.size() <= 0) {
				
				logger.debug("No Segments for this user "+currentUser.getUserId()+" or under his shared user too ");
				return;
				
			}
			
			
			
			for(int i=shareSegmentsLbId.getItemCount(); i>0;i--) {
				
				shareSegmentsLbId.removeItemAt(i-1);
				
			}
			
			//String listName = "";
			Listitem li = null;
			for (SegmentRules segRule: segRuleList) {
			
				li = new Listitem(segRule.getSegRuleName());
				li.setValue(segRule);
				
				li.setParent(shareSegmentsLbId);
				
				/*if(segRule.getUserId().longValue() != currentUser.getUserId().longValue()) {
					
					shareSegmentsLbId.addItemToSelection(li);*/
					
				/*	if(listName.length()> 0) listName += ",";
					listName += segRule.getSegRuleName();*/
					
				//}
				
				
			}//for
			
			for(int i=shareSegmentsWithDomainLbId.getItemCount(); i>0;i--) {
				
				shareSegmentsWithDomainLbId.removeItemAt(i-1);
				
			}
			
			for(UsersDomains userDomains : domainsSet) {
				
				li = new Listitem(userDomains.getDomainName());
				li.setValue(userDomains);
				li.setParent(shareSegmentsWithDomainLbId);
				
				
				
				
			}
			
			for(int i=shareSegmentListlbId.getItemCount(); i>0;i--) {
							
				shareSegmentListlbId.removeItemAt(i-1);
							
			}
			
			
			/*selectedSegmentsLbId.setValue(listName);
			if(listName.trim().length() > 0) {
				
				selectSegmentDivId.setVisible(true);
			}else {
				
				selectSegmentDivId.setVisible(false);
			}
			*/
			prepareExistingSegmentShare();
			
		}
		
		
		
	}
	private void prepareShareSegmentList(SegmentRules segRule, String domainNamesStr) {
		
		Listitem li = null;
		Listcell lc = null;
		
		

		li = new Listitem();
		li.setParent(shareSegmentListlbId);
		li.setValue(segRule);
		
		lc = new Listcell();
		lc.setParent(li);
		
		
		lc = new Listcell(segRule.getSegRuleName());
		lc.setParent(li);
		
		lc = new Listcell(domainNamesStr);
		lc.setParent(li);
		
		lc = new Listcell();
		lc.setParent(li);
		Image delIconImg = new Image("/img/action_delete.gif");
		delIconImg.setParent(lc);
		delIconImg.addEventListener("onClick", new EventListener() {
			
			@Override
			public void onEvent(Event event) throws Exception {
				// TODO Auto-generated method stub
				
				try {
					int confirm = Messagebox.show("Are you sure you want to delete the selected segment(s)?", "Confirm",Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
					if(confirm == Messagebox.OK){
							
				
					try {
						Image img = (Image)event.getTarget();
						Listitem li  = ((Listitem)img.getParent().getParent());
						int index = li.getIndex();
						SegmentRules rule = (SegmentRules)li.getValue();
						
						
						
						rule.setSharedToDomain(null);
						segmentRulesDaoForDML.saveOrUpdate(rule);
						
						shareSegmentListlbId.removeItemAt(index);
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						logger.error("Exception ::", e1);
					}
				
					
				
				try {
					MessageUtil.setMessage("Selected segment deleted successfully.", "color:green;");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					logger.error("Exception ::" , e);
				}
				
				
				return;
				
			}
			}catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Exception ::" , e);
			}
			}	
			
		});
		
	
	
	
		
		
		
		
		
		
	}
	
	private void prepareListItems(MailingList mailingList, String domainNamesStr) {
		
		Listitem li = null;
		Listcell lc = null;
		

		li = new Listitem();
		li.setParent(shareListlbId);
		li.setValue(mailingList);
		
		lc = new Listcell();
		//lc.setWidth("30px");
		lc.setParent(li);
		
		
		lc = new Listcell(mailingList.getListName());
		lc.setParent(li);
		
		lc = new Listcell(domainNamesStr != null ? domainNamesStr : "");
		lc.setParent(li);
	
		
		lc = new Listcell();
		Image delIconImg = new Image("/img/action_delete.gif");
		delIconImg.setParent(lc);
		delIconImg.addEventListener("onClick", new EventListener() {
			
			@Override
			public void onEvent(Event event) throws Exception {
				// TODO Auto-generated method stub
				
				try {
					int confirm = Messagebox.show("Are you sure you want to delete the selected mailing list(s)?", "Confirm",Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
					if(confirm == Messagebox.OK){
						try {
							Image img = (Image)event.getTarget();
							Listitem li  = ((Listitem)img.getParent().getParent());
							int index = li.getIndex();
							MailingList ml = (MailingList)li.getValue();
							
							
							
							//ml.setUsers(currentUser);
							ml.setSharedToDomain(null);
							mailingListDaoForDML.saveOrUpdate(ml);
							
							shareListlbId.removeItemAt(index);
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							logger.error("Exception ::", e1);
						}
					try {
					MessageUtil.setMessage("Selected mailing list deleted successfully.", "color:green;");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					logger.error("Exception ::" , e);
				} 
					
				return;
					}	
					
					
				}
				catch (Exception e) {
					// TODO Auto-generated catch block
					logger.error("Exception ::" , e);
				} 
				
				
				
				
					}
			
		});
		
		lc.setParent(li);
		
		
	}
	
	
	
	public void onClick$mlShareBtnId() {
		
		
		if(shareMlLbId.getSelectedCount() == 0) {

			MessageUtil.setMessage("Please select at least one mailing list to share.", "color:red;");
			return;
			
			
			
		}
		
		
		if(shareMlWithDomainLbId.getSelectedCount()==0) {
			
			MessageUtil.setMessage("Please select at least one domian to share the selected mailing list(s).", "color:red;");
			return;
			
		}
		
		
		String listNamesStr = "";
		String domainNamesStr = "";
		
		
		Set<Listitem> domainSet = shareMlWithDomainLbId.getSelectedItems();
		for (Listitem listitem : domainSet) {
			
			if(domainNamesStr.trim().length() > 0) domainNamesStr += ",";
			domainNamesStr += ((UsersDomains)listitem.getValue()).getDomainName();
			
			
		}
		
		
		List chldren = null;
		Listitem li = null;
		Listcell lc = null;
		
		Set<Listitem> shreSet = shareMlLbId.getSelectedItems();
		List<Listitem> itemList = shareListlbId.getItems();
		for (Listitem mlitem : shreSet) {
			boolean itemExist = false;
			
			MailingList ml = (MailingList)mlitem.getValue();
			if(itemList.size() > 0) {
				
				for (Listitem listitem : itemList) {
					
					chldren = listitem.getChildren();
					if(((Listcell)chldren.get(1)).getLabel().equals(mlitem.getLabel()) ){
						
						itemExist = true;
						((Listcell)chldren.get(2)).setLabel(domainNamesStr);
						break;
						
					}
					
					
				}//for
			}
			
			if(!itemExist || itemList.size() == 0) {
				prepareListItems(ml,domainNamesStr);
				
				
			}

			
		}//for
		
		
		/*shareMlLbId.clearSelection();
		shareMlWithDomainLbId.clearSelection();*/
		
		
	}
	
	
	public void onClick$segmentShareBtnId() {
		
		
		if(shareSegmentsLbId.getSelectedCount() == 0) {

			MessageUtil.setMessage("Please select at least one segment to share.", "color:red;");
			return;
			
			
			
		}
		
		
		if(shareSegmentsWithDomainLbId.getSelectedCount()==0) {
			
			MessageUtil.setMessage("Please select at least one domian to share the selected segment(s).", "color:red;");
			return;
			
		}
		
		
		
		
		
		
		String segmentNamesStr = "";
		String domainNamesStr = "";
		
		Set<Listitem> domainSet = shareSegmentsWithDomainLbId.getSelectedItems();
		
		for (Listitem listitem : domainSet) {
			
			if(domainNamesStr.trim().length() > 0) domainNamesStr += ",";
			domainNamesStr += ((UsersDomains)listitem.getValue()).getDomainName();
			
			
		}
		
		
		List chldren = null;
		Listitem li = null;
		Listcell lc = null;
		
		Set<Listitem> shrSet = shareSegmentsLbId.getSelectedItems();
		List<Listitem> itemList = shareSegmentListlbId.getItems();
		
		
		for (Listitem segmentitem : shrSet) {
			
			/*if(segmentNamesStr.trim().length() > 0) segmentNamesStr += ",";
			segmentNamesStr += ((SegmentRules)listitem.getValue()).getSegRuleName();*/
			
			boolean itemExist = false;
			
			
			if(itemList.size() > 0) {
				
				for (Listitem listitem : itemList) {
					
					chldren = listitem.getChildren();
					chldren = listitem.getChildren();
					if(((Listcell)chldren.get(1)).getLabel().equals(segmentitem.getLabel()) ){
						
						itemExist = true;
						((Listcell)chldren.get(2)).setLabel(domainNamesStr);
						break;
						
					}
					
				}//for
				
			}
			
			if(!itemExist || itemList.size() == 0) {
				
				
				prepareShareSegmentList((SegmentRules)segmentitem.getValue(), domainNamesStr);
				
			}
			
		}//for
		
		/*shareSegmentsLbId.clearSelection();
		shareSegmentsWithDomainLbId.clearSelection();*/
		
		
	}
	
	public void onSelect$shareSegmentListlbId() {
		
		shareSegmentsLbId.clearSelection();
		shareSegmentsWithDomainLbId.clearSelection();
		
		Listitem selItem = shareSegmentListlbId.getSelectedItem();
		
		List chldren = selItem.getChildren();
		String segmentName = ((Listcell)chldren.get(1)).getLabel();
		
		for (Listitem segItem : shareSegmentsLbId.getItems()) {
			
			if(segItem.getLabel().equals(segmentName)) {
				shareSegmentsLbId.setSelectedItem(segItem);
				break;
			}
			
		}
		String domainNamesStr = ((Listcell)chldren.get(2)).getLabel();
		
			
			String[] tempArr = domainNamesStr.split(",");
			for (String domianName : tempArr) {
				
				for (Listitem domainItem : shareSegmentsWithDomainLbId.getItems()) {
					
					
					if(domainItem.getLabel().equals(domianName)) {
						
						shareSegmentsWithDomainLbId.addItemToSelection(domainItem);
					}
					
					
				}//for
				
				
				
			}//for
			
		
		/*MailingList mlObj = getMlObjFromListName(listName);
		
		Set<UsersDomains> domainSet = getDomainSet(((Listcell)chldren.get(2)).getLabel()); */
		
	}
	
	
	public MailingList getMlObjFromListName(String listName) {
		
		MailingList ml = null;
		
		for(Listitem item : shareMlLbId.getItems()) {
			
			if(item.getLabel().equals(listName.trim())) {
				
				ml = (MailingList)item.getValue();
			
				break;
			}
		}
		
		return ml;
		
		
	}
	
	public SegmentRules getSegmentObjFromSegName(String segRuleName) {
		
		SegmentRules segment = null;
		
		for(Listitem item : shareSegmentsLbId.getItems()) {
			
			if(item.getLabel().equals(segRuleName.trim())) {
				
				segment = (SegmentRules)item.getValue();
			
				break;
			}
		}
		
		return segment;
		
		
	}
	
	
	
	
	
	
	public Set<UsersDomains> getDomainSet(String domainsStr) {
		
		Set<UsersDomains> retSet = new HashSet<UsersDomains>();
		UsersDomains ud = null;
		
			
			String[] tempArr = domainsStr.split(",");
			for (String domain : tempArr) {
				
				for (Listitem item : shareMlWithDomainLbId.getItems()) {
					
					if(item.getLabel().endsWith(domain.trim())) {
						
						ud = (UsersDomains)item.getValue();
						retSet.add(ud);
						
					}//if
					
					
					
					
				}
				
			}
			
			
		
		return retSet;
		
	}
	
	
	
	public void onSelect$shareListlbId() {
		
		shareMlLbId.clearSelection();
		shareMlWithDomainLbId.clearSelection();
		
		Listitem selItem = shareListlbId.getSelectedItem();
		
		List chldren = selItem.getChildren();
		String listName = ((Listcell)chldren.get(1)).getLabel();
		for (Listitem mlItem : shareMlLbId.getItems()) {
			
			if(mlItem.getLabel().equals(listName)) {
				shareMlLbId.setSelectedItem(mlItem);
				break;
			}
			
		}
		String domainNamesStr = ((Listcell)chldren.get(2)).getLabel();
		
			
			String[] tempArr = domainNamesStr.split(",");
			for (String domianName : tempArr) {
				
				for (Listitem domainItem : shareMlWithDomainLbId.getItems()) {
					
					
					if(domainItem.getLabel().equals(domianName)) {
						
						shareMlWithDomainLbId.addItemToSelection(domainItem);
					}
					
					
				}//for
				
				
				
			}//for
		
		
		
	}
	
	
	
	
	/*private Users getCorrespondingSharedUser(String domainNamesStr) {
		
		try {
			List<Users> sharedUsers = usersDao.getSharedUsersByParentUserId(currentUser.getUserId());
			
			String currDomainStr = currentUser.getUserDomains().iterator().next().getDomainName();
			
			if(!domainNamesStr.contains(currDomainStr)) {
				
				domainNamesStr += ","+currDomainStr;
			}
			
			
			String[] tempArr = domainNamesStr.split(",");
			
			int domianSize = tempArr.length;
			
			for (Users shUser : sharedUsers) {
							
				Set<UsersDomains> shUserDomains = shUser.getUserDomains();
				
				if(shUserDomains.size() != (domianSize)) continue;

				
				boolean found = false;
				
				for (String eachDomain : tempArr) {
					eachDomain = eachDomain.trim();

					found = false;
					
					for (UsersDomains usersDomains : shUserDomains) {
						if(usersDomains.getDomainName().equals(eachDomain)) {
							found=true;
							break;
						}
					} // for ShUser Domain
					
					if(found==false) break;
					
				} // for eachDomain

				if(found==true) return shUser;
				
			} // for each ShUser
			
			
			//TODO create the SH User
			
			Users shUser = new Users();
			shUser.setAccountType(Constants.USER_ACCOUNT_TYPE_SHARED);
			shUser.setParentUser(currentUser);
			shUser.setUserOrganization(currentUser.getUserOrganization());
			shUser.setUserName("Shared"+(sharedUsers.size()+1)+"_by_"+currentUser.getUserName());
			shUser.setEnabled(false);
			shUser.setEmailId(currentUser.getEmailId());
			
			shUser = setUserData(shUser, currentUser);
			
			
			shUser.setCreatedDate(Calendar.getInstance());
			
			
			usersDao.saveOrUpdate(shUser);
			
			Set<UsersDomains> shUserDomainSet = new HashSet<UsersDomains>();
			//shUserDomainSet.addAll(currentUser.getUserDomains());//updation bug 
			shUserDomainSet.addAll(getDomainSet(domainNamesStr));//updation bug
			
			shUser.setUserDomains(shUserDomainSet);
			
			usersDao.saveOrUpdate(shUser);
			return shUser;
			
			
			
			
			
		} catch (Exception e) {
			logger.error("Exception ::" , e);
		}
		return null;
	}*/
	
	
public void onClick$submitSegmentSharesBtnId() {
		
		if(shareSegmentListlbId.getItemCount() == 0) {
			
			MessageUtil.setMessage("No shared items found. Please click on 'Share Segment(s)' to add for sharing.", "color:red;");
			return;
			
		}
		try {
			int confirm = Messagebox.show("Are you sure you want to share the selected segment(s)?", "Confirm",Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
			if(confirm == Messagebox.OK){
			
			
			List chlList = null;
			Users shUser = null;
			
			List<Listitem> items = shareSegmentListlbId.getItems();
			SegmentRules segruleObj = null;
			
			for (Listitem item : items) {
				
				chlList = item.getChildren();
				
				String domainNamesStr = ((Listcell)chlList.get(2)).getLabel();
				
				/*shUser = getCorrespondingSharedUser(domainNamesStr);
				if(shUser != null) {
					
					segruleObj = getSegmentObjFromSegName(((Listcell)chlList.get(1)).getLabel());
					
					segruleObj.setUserId(shUser.getUserId());
					segmentRulesDao.saveOrUpdate(segruleObj);
					
					
					
				}//if shUser
*/				Set<UsersDomains> sharedToDomains = getSharedDomainsByDomainStr(domainNamesStr);
				segruleObj = getSegmentObjFromSegName(((Listcell)chlList.get(1)).getLabel());
				segruleObj.setSharedToDomain(sharedToDomains);
				segmentRulesDaoForDML.saveOrUpdate(segruleObj);
				
								
				
				
			}//for
			MessageUtil.setMessage("Segment(s) shared successfully.", "color:blue","TOP");
			
//	Messagebox.show("Given Segments shared successfully");
			
			
			
}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	public void onClick$submitMlSharesBtnId() {
		
		if(shareListlbId.getItemCount() == 0) {
			
			MessageUtil.setMessage("No shared items found. Please click on 'Share List(s)' to add for sharing.", "color:red;");
			return;
			
		}
		
		try {
			int confirm = Messagebox.show("Are you sure you want to share the selected mailing list(s)?", "Confirm",Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
			if(confirm == Messagebox.OK){
			
			
			
			List chlList = null;
			
			List<Listitem> items = shareListlbId.getItems();
			MailingList mlObj = null;
			
			for (Listitem item : items) {
				
				chlList = item.getChildren();
				
				String domainNamesStr = ((Listcell)chlList.get(2)).getLabel();
				
				
				Set<UsersDomains> sharedToDomains = getSharedDomainsByDomainStr(domainNamesStr);
				mlObj = getMlObjFromListName(((Listcell)chlList.get(1)).getLabel());
				mlObj.setSharedToDomain(sharedToDomains);
				mailingListDaoForDML.saveOrUpdate(mlObj);
				/*shUser = getCorrespondingSharedUser(domainNamesStr);
				if(shUser != null) {
					
					mlObj = getMlObjFromListName(((Listcell)chlList.get(1)).getLabel());
					
					mlObj.setUsers(shUser);
					mailingListDao.saveOrUpdate(mlObj);
					
					
					
				}//if shUser
*/				
			}//for
			
			MessageUtil.setMessage("Mailing list(s) shared successfully.", "color:blue","TOP");
			
			
}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
		}
	}
	
	
	
	public Set<UsersDomains> getSharedDomainsByDomainStr(String domainNamesStr) {
		
		String domainArr[] = null;
		Set<UsersDomains> domSet = new HashSet<UsersDomains>();
		List<Listitem> domList = shareMlWithDomainLbId.getItems();
		
		domainArr = domainNamesStr.split(",");
		
		
		for (String domName : domainArr) {
			
			for (Listitem listitem : domList) {
				
				if(domName.equals( ((UsersDomains)listitem.getValue()).getDomainName()) )
					domSet.add((UsersDomains)listitem.getValue());
				
			}//inner
			
			
			
		}//outer
		
		return domSet;
		
	}
	
	
	
	
	
public Users setUserData(Users newUser, Users parentUser) {
		
		newUser.setCompanyName(parentUser.getUserOrganization().getOrganizationName());
		newUser.setAddressOne(parentUser.getAddressOne());
		
		if(parentUser.getAddressTwo() != null) {
		
			newUser.setAddressTwo(parentUser.getAddressTwo());
		}
		newUser.setCity(parentUser.getCity());
		newUser.setState(parentUser.getState());
		newUser.setCountry(parentUser.getCountry());
		newUser.setPinCode(parentUser.getPinCode());
		
		if(parentUser.getPhone() != null) {
			newUser.setPhone(parentUser.getPhone());
		}
		
		
		
		newUser.setPackageStartDate(Calendar.getInstance());
		newUser.setPackageExpiryDate(parentUser.getPackageExpiryDate());
		newUser.setFooterEditor(parentUser.getFooterEditor());
		newUser.setVmta(parentUser.getVmta());
		
		//************editable properties***************
		newUser.setEmailId(parentUser.getEmailId());
		newUser.setFirstName(parentUser.getFirstName());
		newUser.setLastName(parentUser.getLastName());
		
		newUser.setEnabled(parentUser.isEnabled());
		newUser.setUserOrganization(parentUser.getUserOrganization());
		
		
		
		
		
		return newUser;
	}
	
	
	
	
	
}
