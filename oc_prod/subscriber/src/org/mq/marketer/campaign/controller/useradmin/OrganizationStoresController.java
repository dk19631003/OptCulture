package org.mq.marketer.campaign.controller.useradmin;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
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
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.clapper.util.io.FileUtil;
import org.mq.marketer.campaign.beans.DigitalReceiptUserSettings;
import org.mq.marketer.campaign.beans.Messages;
import org.mq.marketer.campaign.beans.Address;
import org.mq.marketer.campaign.beans.Coupons;
import org.mq.marketer.campaign.beans.Messages;
import org.mq.marketer.campaign.beans.MailingList;
import org.mq.marketer.campaign.beans.OrganizationStores;
import org.mq.marketer.campaign.beans.POSMapping;
import org.mq.marketer.campaign.beans.UserFromEmailId;
import org.mq.marketer.campaign.beans.UserOrganization;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.beans.UsersDomains;
import org.mq.marketer.campaign.controller.HomeController;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.controller.MessageHandler;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.ContactsDao;
import org.mq.marketer.campaign.dao.EmailQueueDao;
import org.mq.marketer.campaign.dao.MessagesDao;
import org.mq.marketer.campaign.dao.MessagesDaoForDML;
import org.mq.marketer.campaign.dao.OrganizationStoresDao;
import org.mq.marketer.campaign.dao.OrganizationStoresDaoForDML;
import org.mq.marketer.campaign.dao.UserFromEmailIdDao;
import org.mq.marketer.campaign.dao.UserFromEmailIdDaoForDML;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.dao.UsersDomainsDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.GridFilterEventListener;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PageListEnum;
import org.mq.marketer.campaign.general.PageUtil;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Redirect;
import org.mq.marketer.campaign.general.Utility;
import org.zkoss.util.media.Media;
import org.zkoss.zhtml.Fileupload;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Div;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Popup;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Window;
import org.zkoss.zul.event.PagingEvent;

public class OrganizationStoresController extends GenericForwardComposer{
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	 private OrganizationStoresDao organizationStoresDao;
	 private OrganizationStoresDaoForDML organizationStoresDaoForDML;
	 private UserFromEmailIdDao userFromEmailIdDao;
	 private UserFromEmailIdDaoForDML userFromEmailIdDaoForDML;
	 private Grid storesGridId;
	 private Rows orgStoreRowsId;
	 private Session session;
	 private OrganizationStores stores;
	 private UsersDao usersDao;
	 private UsersDomainsDao userDomaonDao;
	 private OrganizationStoresDao orgStoreDao;
	 private UserOrganization userOrganization;
	 private Users currentUser;
	 private Tab addStoreLocTabId,storeLocTabId;
	 private Paging storeLocationsPagingId,storeLocPagingId; 
	 private Listbox storesPerPageLBId,orgUnitId,orgUnitbulkLbId,subsLBId;
	 List<OrganizationStores> storeList ,tempSbList,tempPosList;
	 private TimeZone clientTimeZone;
	 private ContactsDao contactsDao;
	 private Textbox uploadCSVTbId,cEmailTb,frmNameTbId,storeImgUrlTbId,brandTbId,brandImgUrlTbId,descriptionTbId,offersTbId,latTbId,LngTbId;
	 private Radiogroup uploadRgId;
	 private Radio singleuploadRbId,bulkuploadRbId;
	 Div singleAddStoreDiv, bulkAddStoreDiv, mappingCSVId;
	 private Rows contactRowsId;
	 private MessagesDao messagesDao;
	 private MessagesDaoForDML messagesDaoForDML;
	 private Popup regEmailPopupId;
	 private final String EMPTY_STRING = "";
	 
	 private  static String NORMAL_STYLE = "border:1px solid #7F9DB9;";
	 private  static String ERROR_STYLE = "border:1px solid #DD7870;";
	 private static String SELECT_STRING = "--select--";
	 private List<UserFromEmailId> userFromEmailIdList = null;
	 
	 private Window custExport;
	 private Div custExport$chkDivId;
	 private Combobox exportCbId;
	 private Toolbarbutton addNewDomainAnchId;
	 //start
	 private Window createSubsWinId;
	 private Textbox createSubsWinId$subsID,createSubsWinId$subName;
	 private Label createSubsWinId$msgLblId;
	 private Combobox sbsNumbCmboBxId;
	//end
	  private Button addStoreBtnId,cancelStoreBtnId,saveStoreBtnId,editStoreBtnId,backBtnId;
      private Tabbox organizationStoresTabBoxId;
	
		private boolean isValidStoreName;
		String filePath = "";
		private Textbox storeNameTbId,storeManagerNameTbId,emailIdTbId,websiteTbId,phoneTbId,streetTbId,cityTbId,stateTbId,zipTbId,subsID,countryTbId;
		private Label posLocationStatusLblId;
		private Combobox cFromEmailCb,cReplyToEmailCb;	
	 private Textbox posLocCb,sbLocName;
	 private static String searchStr;
	 private static String allStr = "--All--";
	 
	 private static String[] storeMapStr ={"--select--","Subsidiary Id","Subsidiary Name", "POS Store ID","Store Name","Store Manager Name","Email Id","Website","Phone","Street","City","State","Country","Zip Code","From Name","From Email Address","Reply-to Email Address",
			"Brand", "Store Image URL", "Brand Image URL","Latitude","Longitude","Offers","Description"};
	// int storeImgPathIndx = -1, brandImgPathIndx=-1, latLngIndx=-1, offersIndx=-1, descriptionIndx=-1,brandIndx=-1;	
	 public OrganizationStoresController(){
			
		 session = Sessions.getCurrent();
		 usersDao = (UsersDao)SpringUtil.getBean("usersDao");
		 contactsDao =(ContactsDao)SpringUtil.getBean("contactsDao");
		 messagesDao = (MessagesDao)SpringUtil.getBean("messagesDao");
		 messagesDaoForDML = (MessagesDaoForDML)SpringUtil.getBean("messagesDaoForDML");
		 userDomaonDao=(UsersDomainsDao) SpringUtil.getBean("usersDomainsDao");
		 orgStoreDao=(OrganizationStoresDao) SpringUtil.getBean("organizationStoresDao");
		 
	   	 currentUser = GetUser.getUserObj();
		 String style = "font-weight:bold;font-size:15px;color:#313031;font-family:Arial,Helvetica,sans-serif;align:left";
		 PageUtil.setHeader("Manage Stores ","",style,true);
		 clientTimeZone = (TimeZone)session.getAttribute("clientTimeZone");
			
	 }
	 private Textbox searchBoxId;
	 private Listbox searchByStoreID,searchByOrgID,searchBySbsID;
	 private Users user;
	 private String type;
	 private Map<String, String> SBSMap = new HashMap<String, String>();
	 private Object[] selectedStoreItem = new Object[2];
	 private Label orgId,sbsId,storeID;
		
		@SuppressWarnings("rawtypes")
		
	
	public void doAfterCompose(Component comp) throws Exception {
		
		
		try {
			// TODO Auto-generated method stub
			super.doAfterCompose(comp);
			exportCbId.setSelectedIndex(0);
			type = (String)session.removeAttribute("OrganizationStoresType");
			session.removeAttribute("OrganizationStores");
			organizationStoresDao =	(OrganizationStoresDao)SpringUtil.getBean("organizationStoresDao");
			organizationStoresDaoForDML =	(OrganizationStoresDaoForDML)SpringUtil.getBean("organizationStoresDaoForDML");
			userFromEmailIdDao = (UserFromEmailIdDao)SpringUtil.getBean("userFromEmailIdDao");
			userFromEmailIdDaoForDML = (UserFromEmailIdDaoForDML)SpringUtil.getBean("userFromEmailIdDaoForDML");

			logger.info("do afeter compose  userFromEmailIdDao============"+userFromEmailIdDao);
			userFromEmailIdList  = userFromEmailIdDao.getEmailsWithoutConsideringStatusByUserId(GetUser.getUserObj().getUserId());
			
			getOrgIds();
			
		//	getPosLocIds();
			setUserOrg();
			//storeList = 	organizationStoresDao.findByOrgByOrgId(GetUser.getUserObj().getUserOrganization().getUserOrgId());
							
				setpageTotCount(null);
				
				
				storeLocationsPagingId.setActivePage(0);
				storeLocationsPagingId.addEventListener("onPaging", this);
				redrawStores(0,storeLocationsPagingId.getPageSize(), null);
				
				/*setPosLocCount();
				storeLocPagingId.setActivePage(0);
				storeLocPagingId.addEventListener("onPaging", this);*/
				
				
			

			/*	Rows rows = new Rows();
			rows.setParent(storesGridId);*/
			//redrawStores( null,0,storeLocationsPagingId.getPageSize());
				//Removing all children's of row
				Components.removeAllChildren(contactRowsId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
		}
		

	} //  doAfterCompose()
   //set all org unit
		private Map<Long, String> orgUnit = new HashMap<Long, String>();
		private void setUserOrg() {
			sbsNumbCmboBxId.setDisabled(false);
			try {
			List<UsersDomains> orgList	= userDomaonDao.FindByOrgId(currentUser.getUserOrganization().getUserOrgId());
			
			if(orgList == null) {
				logger.debug("no organization list exist in the DB...");
				return ;
			}
		     Listitem item = null;
		     Listitem item11=null;
			if(orgList != null && orgList.size() >0) {
				
				for (UsersDomains eachObj : orgList) {
					item=new Listitem(eachObj.getDomainName(),eachObj);
					item.setValue(eachObj);
					item.setParent(orgUnitId);
					item11=new Listitem(eachObj.getDomainName(),eachObj);
					item11.setValue(eachObj);
					item11.setParent(orgUnitbulkLbId);
					
					orgUnit.put(eachObj.getDomainId(), eachObj.getDomainName());
				}
			}
			}
			catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("exception",e);
			}

		}  
		 
		public void onSelect$orgUnitId(){
			if(orgUnitId.getSelectedIndex()==0){
				subsLBId.setSelectedIndex(0);
				resetSudsidiaryLbId();
				return;
			}
			UsersDomains domain=orgUnitId.getSelectedItem().getValue();
			if(domain!=null)
			setSubsidaryId(domain.getDomainId());
			
		}
		//Subsidary id
		public void setSubsidaryId(Long domainId){
			logger.info("Enter into setSubsidaryId() ");
			int count=subsLBId.getItemCount();
			for (; count > 1; count--) {
				subsLBId.removeItemAt(count-1);
			}
			SBSMap=new HashMap<>();
			try {
				Components.removeAllChildren(sbsNumbCmboBxId);
				sbsNumbCmboBxId.setValue("");
				sbLocName.setText("");
				List<OrganizationStores> subList=orgStoreDao.findSubsidaryByOrgUnitId(currentUser.getUserOrganization().getUserOrgId(),domainId );
				if(subList == null) 
					return ;
				/*for (OrganizationStores sudsidiary : subList) {
					if(SBSMap.containsKey(domainId+Constants.DELIMETER_DOUBLECOLON+sudsidiary.getSubsidiaryId()))
						continue;
					Listitem sudsidiaryList = new Listitem(sudsidiary.getSubsidiaryId(), sudsidiary);
					sudsidiaryList.setParent(subsLBId);
					SBSMap.put(domainId+Constants.DELIMETER_DOUBLECOLON+sudsidiary.getSubsidiaryId(), sudsidiary.getSubsidiaryName());
					
				}*/
				 Comboitem comboItem = null;
				 for (OrganizationStores sudsidiary : subList){
					 if(SBSMap.containsKey(domainId+Constants.DELIMETER_DOUBLECOLON+sudsidiary.getSubsidiaryId()))
							continue;
					 comboItem = new Comboitem();
					 comboItem.setLabel(sudsidiary.getSubsidiaryId());
					 comboItem.setValue(sudsidiary);
					 comboItem.setParent(sbsNumbCmboBxId);
					 SBSMap.put(domainId+Constants.DELIMETER_DOUBLECOLON+sudsidiary.getSubsidiaryId(), sudsidiary.getSubsidiaryName());
						
				}
				}
				catch (Exception e) {
					// TODO Auto-generated catch block
					logger.error("exception",e);
				}
		}//onSelect$sbLocId
		public void  onSelect$sbsNumbCmboBxId(){
			logger.info(" inside onselect susidiary");
			if(sbsNumbCmboBxId.getValue()!=null){
			String subs	=sbsNumbCmboBxId.getValue();
			if(subs!=null){
			UsersDomains domain=orgUnitId.getSelectedItem().getValue();
			sbLocName.setText(SBSMap.get(domain.getDomainId()+Constants.DELIMETER_DOUBLECOLON+subs));
			sbLocName.setValue(SBSMap.get(domain.getDomainId()+Constants.DELIMETER_DOUBLECOLON+subs));
			}
			}
			
				
		}
	  
		//onClick$addNewSBId 
		public void onClick$addNewDomainAnchId() {
			UsersDomains domain=orgUnitId.getSelectedItem().getValue();
			if(domain==null){
				MessageUtil.setMessage("Please select Org.Unit ID.","color:red","TOP");
				return;
			}
			createSubsWinId.doModal();
			createSubsWinId$subsID.setValue("");
			createSubsWinId$subName.setValue("");
			createSubsWinId$msgLblId.setValue("");	
		}
		
		
		//end
		public void setpageTotCount(String key) {
			
			String orgId = "All";
			String sbsId = "All";
			String storeId = "All";
			/*String searchStr = (searchBoxId.getValue().trim().equals("Search store...") || 
								searchBoxId.getValue().trim().equals("")) ? null : searchBoxId.getValue().trim() ;
			*/
			if(searchByOrgID.getSelectedIndex() != 0) {
				
				//orgId = ((OrganizationStores)searchByOrgID.getSelectedItem().getValue()).getUserOrganization().toString();
				UsersDomains userDomains = (UsersDomains) searchByOrgID.getSelectedItem().getValue();
				orgId = userDomains.getDomainId().toString();
				
			}
			if(searchBySbsID.getSelectedIndex() != 0) {
				
				sbsId = ((OrganizationStores)searchBySbsID.getSelectedItem().getValue()).getSubsidiaryId();
				
			}
			if(searchByStoreID.getSelectedIndex() != 0) {
				
				storeId = searchByStoreID.getSelectedItem().getValue();
				
			}
			int totalSize = organizationStoresDao.findByOrganizationstoresCount(GetUser.getUserObj().getUserOrganization().getUserOrgId(),storeId, key); 
			
			storeLocationsPagingId.setTotalSize(totalSize);
			
		}
		
	/*	public void setPosLocCount() {
		
		int totCount = contactsDao.findTotPosLoc(GetUser.getUserObj().getUserId());
		logger.debug("total count is"+totCount);
		storeLocPagingId.setTotalSize(totCount);
			
		}*/
	
	public void getOrgIds() {
		
		for(int i = searchByOrgID.getItemCount() ; i>1 ; i--) {
			
			searchByOrgID.removeItemAt(i);
		}
		
		/*List<OrganizationStores> orgIdList = organizationStoresDao.findAllOrgs();
		Listitem orgItem = null;
		for (OrganizationStores org : orgIdList){
			orgItem = new Listitem(org.getUserOrganization().toString(),org);
			
			orgItem.setParent(searchByOrgID);
			
			
			
			
		}
		*/
		List<UsersDomains> orgList	= userDomaonDao.FindByOrgId(currentUser.getUserOrganization().getUserOrgId());
		Listitem item11=null;
		if(orgList != null && orgList.size() >0) {
			
			for (UsersDomains eachObj : orgList) {
				item11=new Listitem(eachObj.getDomainName(),eachObj);
				item11.setValue(eachObj);
				item11.setParent(searchByOrgID);
			}
		}
		
	}

	public void getSbsIds(Long domainId) {
		
		/*for(int i = searchBySbsID.getItemCount() ; i>1 ; i--) {
			
			searchBySbsID.removeItemAt(i);
		}
		String searchStrSbs="";
		if(searchByOrgID.getSelectedIndex() != 0) {
			
			searchStrSbs = ((OrganizationStores)searchByOrgID.getSelectedItem().getValue()).getHomeStoreId();
			
		}
		List<OrganizationStores> sbsIdList = organizationStoresDao.findAllSbs(searchStrSbs);
		Listitem sbsItem = null;
		for (OrganizationStores sbs : sbsIdList){
			sbsItem = new Listitem(sbs.getSubsidiaryId(),sbs);
			
			sbsItem.setParent(searchBySbsID);
			
			
		}*/
		

		
		int count = searchBySbsID.getItemCount();

		for (; count > 1; count--) {

			searchBySbsID.removeItemAt(count - 1);

		}
		SBSMap = new HashMap<String, String>();
		//Components.removeAllChildren(sudsidiaryLbId);
		try {
			List<OrganizationStores> subIdList = organizationStoresDao.findSubsidaryByOrgUnitId(domainId);
			if (subIdList != null && subIdList.isEmpty())
				return;
              if(subIdList!=null){
				for (OrganizationStores sudsidiary : subIdList) {
					Listitem sudsidiaryList = new Listitem(sudsidiary.getSubsidiaryId(), sudsidiary);
					sudsidiaryList.setParent(searchBySbsID);
					SBSMap.put(domainId.longValue()+Constants.DELIMETER_DOUBLECOLON+sudsidiary.getSubsidiaryId(), sudsidiary.getSubsidiaryName());
				}
              }
			searchBySbsID.setSelectedIndex(0);
			searchByStoreID.setSelectedIndex(0);
			//searchByStoreID.setDisabled(true);
			
		} catch (Exception exception) {
			logger.error("Exception raised while setting the sudsidiary", exception);
		}
		//sudsidiaryLbId.setSelectedIndex(0);
	
		
		
	}
	
	/*public void getStoreIds() {
		
		for(int i = searchByStoreID.getItemCount() ; i>1 ; i--) {
			
			searchByStoreID.removeItemAt(i);
		}
		String searchStr1="";
		if(searchByOrgID.getSelectedIndex() != 0) {
			
			searchStr1 = ((OrganizationStores)searchByOrgID.getSelectedItem().getValue()).getHomeStoreId();
			
		}
		String searchStr2=null;
		if(searchBySbsID.getSelectedIndex() != 0) {
			
			searchStr2 = ((OrganizationStores)searchBySbsID.getSelectedItem().getValue()).getSubsidiaryId();
			
		}

		List<OrganizationStores> storeIdList = organizationStoresDao.findStores(searchStr1,searchStr2);
		Listitem homeStoreItem = null;
		for (OrganizationStores org : storeIdList){
			homeStoreItem = new Listitem(org.getHomeStoreId(),org);
			
			homeStoreItem.setParent(searchByStoreID);
			
			
		}
		
		
	}*/

		public void redrawStores(int startIndex, int size, String  key) {
			
			String orgId="All";
			String sbsId="All";
			String storeId = "All";
			
			/*String searchStr = (searchBoxId.getValue().trim().equals("Search store...") || 
								searchBoxId.getValue().trim().equals("")) ? null : searchBoxId.getValue().trim() ;
			*/
			if(searchByOrgID.getSelectedIndex() != 0) {
				
				UsersDomains userDomains = (UsersDomains) searchByOrgID.getSelectedItem().getValue();
				orgId = userDomains.getDomainId().toString();
				
			}
			if(searchBySbsID.getSelectedIndex() != 0) {
				
				sbsId = ((OrganizationStores)searchBySbsID.getSelectedItem().getValue()).getSubsidiaryId();
				
			}
			if(searchByStoreID.getSelectedIndex() != 0) {
				
				//OrganizationStores org = (OrganizationStores)searchByStoreID.getSelectedItem().getValue();
				//storeId=org.getHomeStoreId().toString();
				logger.info("a "+searchByStoreID.getSelectedItem());
				logger.info("b "+searchByStoreID.getSelectedIndex());
				logger.info("c "+searchByStoreID.getSelectedItem().getValue());
				storeId=searchByStoreID.getSelectedItem().getValue();
			}
			List<OrganizationStores> storeList = organizationStoresDao.findByOrganization(GetUser.getUserObj().getUserOrganization().getUserOrgId(), orgId,sbsId,storeId, key, startIndex, size);
				
			
			Components.removeAllChildren(orgStoreRowsId);
			
			if(storeList == null || storeList.size() == 0) return;
			
			logger.info("Got  OrganizationStores List of size :" + storeList.size());
			
			
			
			for (OrganizationStores org : storeList) {
				Row row = new Row();
				
				row.setParent(orgStoreRowsId);
				
			    row.appendChild(new Label(org.getDomainId() != null ? ""+orgUnit.get(org.getDomainId()) : "--"));
				row.appendChild(new Label(org.getSubsidiaryId()));
				row.appendChild(new Label(org.getSubsidiaryName()));
				row.appendChild(new Label(org.getHomeStoreId()));
				row.appendChild(new Label(org.getStoreName()));
				row.appendChild(new Label(org.getStoreManagerName()));
				row.appendChild(new Label(org.getEmailId()));
				row.appendChild(new Label(org.getWebsite()));
				row.appendChild(new Label(org.getAddress().getPhone()));
				row.appendChild(new Label(org.getAddress().getAddressOne()));
				row.appendChild(new Label(org.getAddress().getCity()));
				row.appendChild(new Label(org.getAddress().getState()));
				row.appendChild(new Label(""+org.getAddress().getPin()));
				
				Hbox hbox = new Hbox();
				
				Image img = new Image("/img/theme/preview_icon.png");
				img.setStyle("margin-right:10px;cursor:pointer;");
				img.setTooltiptext("View");
				img.setAttribute("type", "userView");
				img.addEventListener("onClick",this);
				
				img.setParent(hbox);
				
				
				Image editImg = new Image("/img/email_edit.gif");
				editImg.setTooltiptext("Edit");
				editImg.setStyle("cursor:pointer;margin-right:5px;");
				editImg.addEventListener("onClick", this);
				editImg.setAttribute("type", "userEdit");
				editImg.setParent(hbox);
				
				Image delImg = new Image("/img/action_delete.gif");
				delImg.setTooltiptext("Delete");
				delImg.setStyle("cursor:pointer;");
				delImg.addEventListener("onClick", this);
				delImg.setAttribute("type", "userDelete");
				delImg.setParent(hbox);
				
				row.setValue(org);
				
				hbox.setParent(row);
				
				
			}
			
			
		}
		
		
		
		
		
	public void onSelect$storesPerPageLBId() {
		try {
			int n =Integer.parseInt(storesPerPageLBId.getSelectedItem().getLabel().trim());
			storeLocationsPagingId.setPageSize(n);
			searchBoxId.setText("");
			setpageTotCount(null);
			redrawStores(0, n, null);
			
			
		} catch (Exception e) {
			logger.error("Exception ::", e);
		}
	}//onSelect$storesPerPageLBId()
	
	public void onSelect$searchByOrgID(){

		UsersDomains userDomains = (UsersDomains) searchByOrgID.getSelectedItem().getValue();
		if(searchByOrgID.getSelectedIndex() == 0)
		{
			searchBySbsID.setSelectedIndex(0);
			searchByStoreID.setSelectedIndex(0);
			resetSudsidiaryLbId();
			resetStoreLbId();
			return;
		}
		getSbsIds(userDomains.getDomainId());
		/*//setStoreIds(userDomains.getS);
		List<Map<String, Object>> tempList = usersDao.findPowerUsersOfSelDomain(userDomains.getDomainId());
		Long powuserIdoftheDomain = null; 
		for (Map<String, Object> map : tempList) {
			powuserIdoftheDomain = Long.parseLong(map.get("user_id").toString());
		}
		if(powuserIdoftheDomain == null) return;
		currentUser = usersDao.findByUserId(powuserIdoftheDomain);
*/	}//onSelect$searchByOrgId()
	
	public void onSelect$searchBySbsID(){

		int count = searchByStoreID.getItemCount();
		logger.info("prev store count" + count);
		for (; count > 1; count--) {
			searchByStoreID.removeItemAt(count - 1);
		}
		if (searchBySbsID.getSelectedIndex() == 0) {
			searchByStoreID.setSelectedIndex(0);
			//searchByStoreID.setDisabled(true);
			return;
		}
		//searchByStoreID.setDisabled(false);
		OrganizationStores organizationStores = (OrganizationStores) searchBySbsID.getSelectedItem().getValue();
		
		
			int index = searchBySbsID.getSelectedItem().getIndex();
			logger.info("---------------------------------------------");
			
		try {
			List<OrganizationStores> storeList = organizationStoresDao.findStoreBySubsidaryId(
					organizationStores.getDomainId(), organizationStores.getSubsidiaryId());
			if (storeList == null || storeList.isEmpty())
				return;
			logger.info("storeList count from db "+ storeList);
			
			Listitem tempStoreList = null;
			for (OrganizationStores store : storeList) {
				tempStoreList = new Listitem(store.getHomeStoreId(), store.getHomeStoreId());
				tempStoreList.setParent(searchByStoreID);
			}
			searchByStoreID.setSelectedIndex(0);
			logger.info("drStoreLbId total count :- "+ searchByStoreID.getItemCount());
			


		} catch (Exception exception) {
			logger.error("Exception raised while setting the stores", exception);
		}
		
		logger.info("=======userDomains" + organizationStores.getSubsidiaryId());
	
		
	}//onSelect$searchBySbsId
	
	public void onSelect$searchByStoreID(){
		selectedStoreItem[0] = searchByStoreID.getSelectedItem();
		selectedStoreItem[1] = searchByStoreID.getSelectedIndex();
		 logger.info("on evnet selectedStoreItem[1]" +selectedStoreItem[1]);
	      logger.info("on event selectedStoreItem[0]" +selectedStoreItem[0]);
		
	}

	public void resetStoreLbId(){

		
		int sizeStoreID = searchByStoreID.getItemCount();
		sizeStoreID = sizeStoreID-1;
				while(sizeStoreID>0){
			
					searchByStoreID.removeItemAt(1);
					sizeStoreID--;
		}
		return;
	
		
	}
	
	public void onEvent(Event event) throws Exception {
			// TODO Auto-generated method stub
			super.onEvent(event);
			
			if(event.getTarget() instanceof Image){
				
				Image img = (Image)event.getTarget();
				
			
				String evtType = (String)img.getAttribute("type");
			
               if(evtType.equalsIgnoreCase("userView")){
            	   
            	   Row tempRow = ((Row)((Hbox)img.getParent()).getParent());
   				   OrganizationStores stores = (OrganizationStores)tempRow.getValue();
					
					session.setAttribute("organizationStoresType", "view");
					session.setAttribute("organizationStores", stores);
					organizationStoresTabBoxId.setSelectedIndex(1);
					singleuploadRbId.setChecked(true);
					singleAddStoreDiv.setVisible(true);
					bulkAddStoreDiv.setVisible(false);
					storeSettings();
					
				}
				else if(evtType.equalsIgnoreCase("userEdit")) {
					
					Row tempRow = ((Row)((Hbox)img.getParent()).getParent());
					OrganizationStores stores = (OrganizationStores)tempRow.getValue();
					
					session.setAttribute("organizationStoresType", "edit");
					session.setAttribute("organizationStores", stores);
					organizationStoresTabBoxId.setSelectedIndex(1);
					singleuploadRbId.setChecked(true);
					singleAddStoreDiv.setVisible(true);
					bulkAddStoreDiv.setVisible(false);
					storeSettings();
				}else if(img.getAttribute("type").equals("userDelete")) {
					
					Row tempRow = ((Row)((Hbox)img.getParent()).getParent());
					OrganizationStores stores = (OrganizationStores)tempRow.getValue();
					//TODO
					/*Row tempRow = (Row)((Hbox)img.getParent()).getParent();
					OrganizationStores org = (OrganizationStores)tempRow.getAttribute("ORG");
					*/
					try {
						
						int confirm = Messagebox.show("Confirm to delete the selected store.", "Confirm",Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
						if(confirm == Messagebox.OK) {
							
							
							try {
								//organizationStoresDao.delete(stores);//TODO enable this comment
								organizationStoresDaoForDML.delete(stores);//TODO enable this comment
								MessageUtil.setMessage("Store deleted successfully.","color:blue","TOP");
								orgStoreRowsId.removeChild(tempRow);
								int totalSize = organizationStoresDao.findByOrganizationstoresCount(GetUser.getUserObj().getUserOrganization().getUserOrgId(), "All", null); 
								storeLocationsPagingId.setTotalSize(totalSize);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								logger.error("Exception ::", e);
								 MessageUtil.setMessage("Please disable the store under particular zone and try again.", "color:red;");
							}
						}
						}
						
						
					 catch (Exception e) {
						// TODO Auto-generated catch block
						logger.error("Exception ::", e);
					}
					return;
					
				}else if(evtType != null && "CONTACT_MAPPING_DELETE".equalsIgnoreCase(evtType)) {
					
					Row tempRow1 = (Row)(img.getParent()).getParent();
					contactRowsId.removeChild(tempRow1);
				}
				
			}
			else if(event.getTarget() instanceof Paging) {
				
				
				Paging paging = (Paging)event.getTarget();
				
				int desiredPage = paging.getActivePage();
				
				
				
				
				PagingEvent pagingEvent = (PagingEvent) event;
				int pSize = pagingEvent.getPageable().getPageSize();
				int ofs = desiredPage * pSize;
			//	redrawStores(ofs, (byte) pagingEvent.getPageable().getPageSize(), null);
				
			
				String searchStr = (searchBoxId.getValue().trim().equals("Search store...") || 
						searchBoxId.getValue().trim().equals("")) ? null : searchBoxId.getValue().trim() ;
				redrawStores(ofs, (byte) pagingEvent.getPageable().getPageSize(), searchStr);
				
				
			}
			
		} //onEvent
	
	
	
	
	
	
	
	

		public boolean validateHomeStoreId() {
			logger.info("----just entered---");
			
			String homeStoreId = posLocCb.getValue().trim();
			//String homeStoreId = posLocationTbId.getValue().trim();
			
			
			if(homeStoreId.length() == 0) {
				
				MessageUtil.setMessage("Please provide POS Store ID. POS Store ID cannot be left empty.", "color:red;");
				return false;
				
			}
			try {
				
				OrganizationStores organizationStores = organizationStoresDao.findByStoresId(homeStoreId,GetUser.getUserObj().getUserOrganization().getUserOrgId());//Home Store Id should be unique across the organization
				
				if(organizationStores == null) {
					
					return true;
				}else {
				
				MessageUtil.setMessage("POS Store ID already exists. Please provide another POS Store ID.",  "color:red;");
				return false;
			}
				
			} catch (Exception e) {
				logger.info("Exception : ");
			}
			
			return true;
			
			
		}//validateStores()
		
		
		public boolean validateSubsidaryID(){
			String sub =sbsNumbCmboBxId.getValue().trim();
			logger.info(" combo sub "+sub);
			  UsersDomains domain=orgUnitId.getSelectedItem().getValue();
				if(domain==null){
					return false;
				}  
			if(!SBSMap.containsKey(domain.getDomainId()+Constants.DELIMETER_DOUBLECOLON+sbsNumbCmboBxId.getValue())){
				List<OrganizationStores> subList=orgStoreDao.findStoreBySubsidaryId(domain.getDomainId(), sub);
				if(subList!=null && subList.size()>0) {
					MessageUtil.setMessage("Subsidiary Id is already exists ,please provide another Id.","color:red;");
					return false;
				}
				return true;
	           }
	          
				
			/*String subId=sudsidiary.getSubsidiaryId();
			 UsersDomains domain=orgUnitId.getSelectedItem().getValue();	
			 
		  Long domainId=domain.getDomainId();
		  
           try {	
        	 //  logger.info("====================subid verification ");
				OrganizationStores organizationStores = organizationStoresDao.getSubIdByOrgUnitId(currentUser.getUserOrganization().getUserOrgId(), domainId, subId);	
				if(organizationStores == null) {		
					return true;
				}else {
				
				MessageUtil.setMessage("Subsidiary ID already exists. Please provide another Subsidiary ID.",  "color:red;");
				return false;
			}
				
			} catch (Exception e) {
				logger.info("Exception : ");
			}
			*/
			return true;
			
		}
		public boolean validateSubsidaryName(){
			String subName=sbLocName.getValue().trim();	
           if(subName.length() == 0) {	
				MessageUtil.setMessage("Please provide Subsidiary Name.", "color:red;");
				return false;
				
           }
				 UsersDomains domain=orgUnitId.getSelectedItem().getValue();
					if(domain==null){
						return false;
					}  
				if(!SBSMap.containsKey(domain.getDomainId()+Constants.DELIMETER_DOUBLECOLON+sbsNumbCmboBxId.getValue())){
				List<OrganizationStores> subList11=orgStoreDao.findStoreBySubsidaryName(domain.getDomainId(), subName);
				if(subList11!=null && subList11.size()>0 && !subList11.get(0).getSubsidiaryId().equalsIgnoreCase(sbsNumbCmboBxId.getValue())){
					MessageUtil.setMessage("Subsidiary Name is already exists ,please provide another Name.","color:red;");
					return false;	
				}
				return true;
			}
           /*try {
        	//   logger.info("====================subname verification ");
				OrganizationStores organizationStores = organizationStoresDao.getSubNameByOrgUnitId(currentUser.getUserOrganization().getUserOrgId(), domainId, subId,subName);
				
				if(organizationStores == null) {		
					return true;
				}else {
				
				MessageUtil.setMessage("Subsidiary Name already exists. Please provide another Subsidiary Name.",  "color:red;");
				return false;
			}
				
			} catch (Exception e) {
				logger.info("Exception : ");
			}*/
			
			return true;
			
		}
		public boolean validateStoreImgPath(){
			String storeImgUrl=storeImgUrlTbId.getValue().trim();
			if(storeImgUrl!=null && !storeImgUrl.isEmpty() && !Utility.isImageURL(storeImgUrl)){
					MessageUtil.setMessage("Please provide valid store image url", "color:red;");
				return false;
			}
			return true;
		}
		public boolean validateBrandImgPath(){
			String brandImgUrl=brandImgUrlTbId.getValue().trim();
			if(brandImgUrl!=null && !brandImgUrl.isEmpty() && !Utility.isImageURL(brandImgUrl)){
				MessageUtil.setMessage("Please provide valid brand image url", "color:red;");
			return false;
		}
			return true;
		}
		public boolean validateLatitude(){
			String lat=latTbId.getValue().trim();
			if(lat!=null && !lat.isEmpty() && !Utility.isLatitude(lat)){
				MessageUtil.setMessage("Please provide valid latitude", "color:red;");
			return false;
			}
			return true;
		}
		public boolean validateLongitude()
		{
			String lon=LngTbId.getValue().trim();
			if(lon!=null && !lon.isEmpty() && !Utility.isLongitude(lon)){
				MessageUtil.setMessage("Please provide valid longitude", "color:red;");
					return false;
				}
				return true;
			}
		public boolean validateBrand(){
			String brand=brandTbId.getValue().trim();
			if(Utility.validateName(brand)){
				MessageUtil.setMessage("Please provide valid brand", "color:red;");
				return false;
			}
			
			return true;
		}
		
		public boolean validateOffers(){
			String offers=brandTbId.getValue().trim();
			if(offers!=null && !offers.isEmpty()){
				if(Utility.validateName(offers)){
					MessageUtil.setMessage("Please provide valid offers", "color:red;");
					return false;
				}
			}
			
			return true;
		}
		public boolean validateDescription(){
			String description=descriptionTbId.getValue().trim();
			if(description!=null && !description.isEmpty()){
			if(Utility.validateName(description)){
				MessageUtil.setMessage("Please provide valid description", "color:red;");
				return false;
			}
		}
			return true;
		}
		
		public boolean validatePosStoreId(){
			String subId=sbsNumbCmboBxId.getValue().trim();
			 UsersDomains domain=orgUnitId.getSelectedItem().getValue();	
		    Long domainId=domain.getDomainId();
		    String posId=  posLocCb.getValue().trim();
           if( posId.length()==0) {
				MessageUtil.setMessage("Please provide POS Store ID.", "color:red;");
				return false;
				
			}
           try {
        	 //  logger.info("====================posid verification ");
				OrganizationStores organizationStores = organizationStoresDao.getStoreIdBySubsidary(currentUser.getUserOrganization().getUserOrgId(), domainId, subId,posId);
				
				if(organizationStores == null) {		
					return true;
				}else {
				
				MessageUtil.setMessage("POS Store ID already exists. Please provide another POS Store ID.",  "color:red;");
				return false;
			}
				
			} catch (Exception e) {
				logger.info("Exception : ");
			}
			
			return true;
			
		}
		
		
		public boolean validateStores() {
			logger.info("----just entered---");
			String storeName = storeNameTbId.getValue().trim();
			
			if(storeName.trim().length() == 0) {
				
				MessageUtil.setMessage("Please provide store name. Store name cannot be left empty.", "color:red;");
				return false;
				
			}
			
			return true;
			
			
		}//validateStores()
		
		
		public boolean  validateStoreAddress() {
			
			
			try {
				String street = streetTbId.getValue().trim();
				String city = cityTbId.getValue().trim();
				String state = stateTbId.getValue().trim();
				String pin = zipTbId.getValue().trim();
				String phone = phoneTbId.getValue().trim();
				String country =countryTbId.getValue().trim();
				
       if( street.length() == 0){
					
					MessageUtil.setMessage("Please provide Street. Street cannot be left empty.", "color:red;");
					return false;
					
				}
       
       			if(city.length() == 0){
					
					MessageUtil.setMessage("Please provide City. City cannot be left empty.", "color:red;");
					return false;
					
				}
      
      			if(state.length() == 0){
					
					MessageUtil.setMessage("Please provide State. State cannot be left empty.", "color:red;");
					return false;
					
				} 
      			if(country.length() == 0){
					
					MessageUtil.setMessage("Please provide Country. Country cannot be left empty.", "color:red;");
					return false;
					
				} 
      
      			String countryType = currentUser.getCountryType();
      			
      		  if(Utility.zipValidateMap.containsKey(currentUser.getCountryType())){
      			if(Utility.zipValidateMap.containsKey(countryType)){
      
			    if(pin.length() == 0) {
						
						MessageUtil.setMessage(" Zip code cannot be left empty.","color:red;");
						return false;
			    }
			    
			  //  String zip = zipTbId.getValue().trim();
				 boolean zipCode = Utility.validateZipCode(pin, countryType);
				 if(!zipCode){
					 MessageUtil.setMessage("Please enter valid zip code.","color:red;");
						return false;
				 }
				 
      			}else{
    				
    				if(pin != null && pin.length() > 0){
    					
    					try{
    						
    						Long pinLong = Long.parseLong(pin);
    						
    		      } catch (NumberFormatException e) {
    						MessageUtil.setMessage("Please provide 5 / 6 digits Zip Code.","color:red;");
    						return false;
    		      }
    					
    				if(pin.length() > 6 || pin.length() < 5) {
    					
    					//	Messagebox.show("Please provide 5 / 6 digits Zip code / pin.");
    						MessageUtil.setMessage("Please provide 5 / 6 digits Zip code / Pin.", "Color:red", "Top");
    						return false;
    						
    					}
    				}
    			}
      			
      		  }
			    /*if(pin.length() > 6 || pin.length() < 5 ) {
					MessageUtil.setMessage("Please provide 5 / 6 digits Zip code.","color:red;");
					return false;
						
			    }*/
			    
      
	      /*try{
					
					Long pinLong = Long.parseLong(pin);
					
	      } catch (NumberFormatException e) {
					MessageUtil.setMessage("Please provide valid Zip Code.","color:red;");
					return false;
	      }*/
      

      		if( phone.length() > 0 && (Utility.phoneParse(phone, GetUser.getUserObj().getUserOrganization())==null)) {
	      //if( phone.length() > 0 && !Utility.validateUserPhoneNum(phone)){
							
							//MessageUtil.setMessage("Please provide valid Phone number.", "Color:Red", "Top");
							MessageUtil.setMessage("Invalid Phone number.", "color:red", "TOP");
			//long phone = Long.parseLong(value);
							return false;
			}

			return true;
			} catch (WrongValueException e) {
				// TODO Auto-generated catch block
				logger.error("Exception ::", e);
				return false;
			}
			
			
		}//validateStoreAddress()
		
		public boolean validateEmail(String email) {
			//String email = emailIdTbId.getValue();
			logger.info("email========="+email);
		if(email!= null && 
				email.trim().length() >0 && 
				!Utility.validateEmail(email)) {
			
			//MessageUtil.setMessage("Please provide valid Email address.", "Color:Red", "Top");
			MessageUtil.setMessage("Please provide valid Email address.", "color:red", "TOP");
			return false;
		}
		return true;
		}//validatEmail()
		public boolean validateUrl(){
			String website = websiteTbId.getValue();
			if(website!= null && website.trim().length() > 0 && !Utility.isURL(website)){
				//MessageUtil.setMessage("Please provide valid Website URL.", "Color:Red", "Top");
				MessageUtil.setMessage("Please provide valid Website URL.", "color:red", "TOP");
				
				return false;
			}
			return true;
		}
		
	
		public void onClick$cancelStoreBtnId() {
			 Redirect.goTo(PageListEnum.EMPTY);
		     Redirect.goTo(PageListEnum.USERADMIN_ORG_STORES);
		}
		
		public void onClick$addStoreBtnId() {
			logger.info("----just entered---");
			stores = (OrganizationStores)session.getAttribute("organizationStores");
		
		/*	if(stores == null && type  == null && !validateHomeStoreId()){
				
				return;
			}*/			
			/*if(!validateStores())
				return;*/
			if(singleuploadRbId.isSelected()){
				
				if(orgUnitId.getSelectedIndex() == 0) {
					MessageUtil.setMessage("Please select Org.Unit ID.","color:red","TOP");
				return;
				}
			if(stores == null && type  == null)	
			if(!validateSubsidaryID()){
				return;
			}
			if(!validateSubsidaryName()){
				return;
			}
			if(sbsNumbCmboBxId.getValue()!=null){
				String subs	=sbsNumbCmboBxId.getValue();
				if(subs.isEmpty()){
					MessageUtil.setMessage("Please select Subsidiary ID.","color:red","TOP");
					return;
				}
			}
			
			if(!validateStoreImgPath()){
				return;
			}
			
			if(!validateBrandImgPath()){
				return;
			}
			
			if(!validateLongitude()){
				return;
			}
			if(!validateLatitude()){
				return;
			}
			/*if(!validateDescription()){
				return;
			}
			
			if(!validateOffers()){
				return;
			}*/
			
			if(stores == null && type  == null && !validatePosStoreId()){
				return;
			}
			if (stores == null && type  == null && !validateStores() )
			{
				return;
			}			
			
			if(!validateStoreAddress())
			{
				return ;
			}//if
			
			if(!validateEmail(emailIdTbId.getValue())){
				
				return;
			}
			
			

			if(cFromEmailCb.getValue().equalsIgnoreCase(EMPTY_STRING))
			{
				/*MessageUtil.setMessage("Please provide From-Email address.", "color:red", "TOP");
				return;*/
			}else if(!validateEmail(cFromEmailCb.getValue())) {
				return;

			}

			if(cReplyToEmailCb.getValue().equalsIgnoreCase(EMPTY_STRING))
			{
				/*MessageUtil.setMessage("Please provide To-Email address.", "color:red", "TOP");
				return;*/
			}else if(!validateEmail(cReplyToEmailCb.getValue())) {
				return;

			}

			
			
			if(!validateUrl()){
				return;
			}
			
			//onSelect$posLocCb();
			 Address address = null;
			if(stores == null && type  == null) {	
				
				 stores = new OrganizationStores(); 
				//add orgUnitId
				 UsersDomains domain=orgUnitId.getSelectedItem().getValue();	
				 stores.setDomainId(domain.getDomainId());
				
				 //add subsidiaryId
				 stores.setSubsidiaryId(sbsNumbCmboBxId.getValue().trim());
				 stores.setSubsidiaryName(sbLocName.getValue().trim());
				 
				 stores.setHomeStoreId(posLocCb.getValue().trim());
				 stores.setStoreName(storeNameTbId.getValue().trim());
				 stores.setUserOrganization(currentUser.getUserOrganization());
				 stores.setCreatedDate(MyCalendar.getInstance(clientTimeZone));
				 stores.setModifiedDate(MyCalendar.getInstance(clientTimeZone));
				 ////2.4.3
				 
				 if(cFromEmailCb.getValue().equalsIgnoreCase(EMPTY_STRING)){
					 stores.setFromEmailId(null);
				 }else{
					 stores.setFromEmailId(cFromEmailCb.getSelectedItem().getLabel());
				 }
				 
				 
				 if(cReplyToEmailCb.getValue().equalsIgnoreCase(EMPTY_STRING)){
					 stores.setReplyToEmailId(null);
				 }else{
					 stores.setReplyToEmailId(cReplyToEmailCb.getSelectedItem().getLabel());
				 }
				 ///
				address = new Address();
				
			}
			else if(stores != null && type != null) {
				
				/*posLocCb.setValue(stores.getHomeStoreId());
				storeNameTbId.setValue(stores.getStoreName());*/
				address = stores.isAddressFlag() ? stores.getAddress() : new Address();
				stores.setModifiedDate(MyCalendar.getInstance(clientTimeZone));
				
			}
			 UsersDomains domain=orgUnitId.getSelectedItem().getValue();	
			 stores.setDomainId(domain.getDomainId());
			 //add subsidiaryId
			 stores.setSubsidiaryId(sbsNumbCmboBxId.getValue().trim());
			 stores.setSubsidiaryName(sbLocName.getValue().trim());
			 
			 
			 stores.setHomeStoreId(posLocCb.getValue().trim());
			 stores.setHomeStoreId(posLocCb.getValue());
			 stores.setStoreName(storeNameTbId.getValue());
			 
			 if(frmNameTbId.getValue() != null){
				 
				 if(frmNameTbId.getValue().trim().length() > 0)
					 	stores.setFromName(frmNameTbId.getValue().trim());
				 else
					 	stores.setFromName(null);
			 } 
			 ////2.4.3
			 
			 /*stores.setFromEmailId(cFromEmailCb.getSelectedItem().getLabel());
			 stores.setReplyToEmailId(cReplyToEmailCb.getSelectedItem().getLabel());*/
			 if(EMPTY_STRING.equals(cFromEmailCb.getText())){
				 stores.setFromEmailId(null);
			 }else{
				 stores.setFromEmailId(cFromEmailCb.getSelectedItem().getLabel());
			 }
			 
			 if(EMPTY_STRING.equals(cReplyToEmailCb.getText())){
				 stores.setReplyToEmailId(null);
			 }else{
				 stores.setReplyToEmailId(cReplyToEmailCb.getSelectedItem().getLabel());
			 }
			 ////storeImgUrlTbId,brandTbId,brandImgUrlTbId,descriptionTbId,offersTbId,latLngTbId;
			 stores.setStoreManagerName(storeManagerNameTbId.getValue());
			 stores.setEmailId(emailIdTbId.getValue());
			 stores.setWebsite(websiteTbId.getValue());
		
			 stores.setStoreImagePath(storeImgUrlTbId.getValue());
			 stores.setBrandImagePath(brandImgUrlTbId.getValue());
			 stores.setLatitude(latTbId.getValue());		 
			 stores.setLongitude(LngTbId.getValue());
			 
			 stores.setDescription(descriptionTbId.getValue());
			 stores.setStoreBrand(brandTbId.getValue());
			
			
			 stores.setAddressFlag(true);
					
			 address.setAddressOne(streetTbId.getValue()!= null?streetTbId.getValue().trim():"");
			 address.setCity(cityTbId.getValue()!= null? cityTbId.getValue().trim():"");
			 address.setState(stateTbId.getValue()!= null? stateTbId.getValue().trim():"");
			 address.setCountry(countryTbId.getValue()!= null? countryTbId.getValue().trim():"");
			 logger.info("zipTbId.getValue() is  :"+zipTbId.getValue().trim());
			 address.setPin(zipTbId.getValue().trim());
			 address.setPhone(phoneTbId.getValue()!= null? phoneTbId.getValue().trim():"");
			 stores.setAddress(address);
			 
			 stores.setCountry(countryTbId.getValue().trim());
			 stores.setState(stateTbId.getValue().trim());
			 stores.setCity(cityTbId.getValue().trim());
			 stores.setLocality(streetTbId.getValue().trim());
			 stores.setMobileNo(phoneTbId.getValue().trim());
			 stores.setZipCode(zipTbId.getValue().trim());
			 stores.setStoreBrand(brandTbId.getValue().trim());
			 
			 try {
				 String saveMsg = "";
				int confirm = Messagebox.show("Are you sure you want to save the store?", "Prompt", 
						 Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
					if(confirm == Messagebox.OK) {
						
						try {
							//organizationStoresDao.saveOrUpdate(stores);
							organizationStoresDaoForDML.saveOrUpdate(stores);
							
							if(type == null) {
								saveMsg = "created"; 
								logger.info("-------"+saveMsg);
							}else if(type != null){
								logger.info(" store updated");
								saveMsg = "updated";
								
							}
							organizationStoresDaoForDML.saveOrUpdate(stores);
							MessageUtil.setMessage("Store "+saveMsg +"  successfully.","color:green;");
							organizationStoresDaoForDML.upadteSubName(currentUser.getUserOrganization().getUserOrgId(),domain.getDomainId() , sbsNumbCmboBxId.getValue().trim(), sbLocName.getValue().trim());
						} catch (Exception e) {
							// TODO Auto-generated catch block
							logger.error("Exception ::", e);
						}
					}
					//organizationStoresDaoForDML.upadteSubName(currentUser.getUserOrganization().getUserOrgId(),stores.getDomainId() , stores.getSubsidiaryId(), stores.getSubsidiaryId());
					session.removeAttribute("organizationStores");
					//				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Exception ::", e);
			}
		} else if(bulkuploadRbId.isSelected()){
			if(orgUnitbulkLbId.getSelectedIndex() == 0) {
				MessageUtil.setMessage("Please select Org.Unit ID.","color:red","TOP");
			return;
			}
			List<String> scopeCustfieldList = new ArrayList<String>();
			List<String> scopePosAttrList = new ArrayList<String>();
			
			List childRowLst = contactRowsId.getChildren();
			boolean fieldSel = false;
			
			for (Object object : childRowLst) {
				Row temRow = (Row)object;
				List chaildLis = temRow.getChildren();
				
				//csv File  Attribute
				
				Div csvDiv =(Div)chaildLis.get(0);
				Textbox csvColTxtBx = (Textbox)csvDiv.getChildren().get(0);
				csvColTxtBx.setStyle(NORMAL_STYLE);
				
				if(csvColTxtBx.getValue().trim().equals("")) {
					logger.debug("Custom fieldData is eampty");
					csvColTxtBx.setStyle("border:1px solid #DD7870;");
					
					MessageUtil.setMessage("Provide .csv column label.", "color:red", "TOP");
					return ;
				}
				
				//optculture field
				
				Div optFieldDiv = (Div)chaildLis.get(1);
				Listbox optFieldLstBx = (Listbox)optFieldDiv.getChildren().get(0);
				
				if(optFieldLstBx.getSelectedItem() == null){
					MessageUtil.setMessage("Please select the field from List box.","color:red","TOP");
					optFieldLstBx.setStyle(ERROR_STYLE);
					return ;
				}
				
				String scopeCustFieldStr = optFieldLstBx.getSelectedItem().getLabel();
				optFieldLstBx.setStyle(NORMAL_STYLE);
				
				if(scopeCustFieldStr.equals(SELECT_STRING)) {
					MessageUtil.setMessage("Please select the OptCulture Attribute field.","color:red","TOP");
					optFieldLstBx.setStyle(ERROR_STYLE);
					return ;
				}
				
				if(scopeCustFieldStr.equals("")) {
					MessageUtil.setMessage("Please select the OptCulture Attribute field.","color:red","TOP");
					optFieldLstBx.setStyle(ERROR_STYLE);
					return ;
				}
				
				String scopePosStr = csvColTxtBx.getValue();
				
				if(scopeCustfieldList.contains(scopeCustFieldStr) ) {
					optFieldLstBx.setStyle(ERROR_STYLE);
					MessageUtil.setMessage("Optculture field  "+ optFieldLstBx.getSelectedItem().getLabel() +" is already mapped.","color:red","TOP");
					return ;
				}else if(scopePosAttrList.contains(scopePosStr)) {
					csvColTxtBx.setStyle(ERROR_STYLE);
					MessageUtil.setMessage("CSV column label  "+ csvColTxtBx.getValue() +" is already mapped.","color:red","TOP");
					return ;
				}
				
				scopeCustfieldList.add(scopeCustFieldStr);
				scopePosAttrList.add(scopePosStr);
			
				if(scopeCustfieldList.contains("Subsidiary Id") && scopeCustfieldList.contains("Subsidiary Name") && scopeCustfieldList.contains("POS Store ID") && scopeCustfieldList.contains("Store Name") && scopeCustfieldList.contains("Street")
						&& scopeCustfieldList.contains("City") && scopeCustfieldList.contains("State") && scopeCustfieldList.contains("Country") && 
						(( ( !Utility.zipValidateMap.containsKey(currentUser.getCountryType())) && !scopeCustfieldList.contains("Zip Code")) || scopeCustfieldList.contains("Zip Code"))  )
					fieldSel = true;
			}	
				/*if(scopeCustfieldList.contains("POS Store ID") && scopeCustfieldList.contains("Store Name") && scopeCustfieldList.contains("Street")
						&& scopeCustfieldList.contains("City") && scopeCustfieldList.contains("State") &&
						( ( !Utility.zipValidateMap.containsKey(currentUser.getCountryType())) && !scopeCustfieldList.contains("Zip Code")) || scopeCustfieldList.contains("Zip Code")  )
					fieldSel = true;*/
			
			
			if(!fieldSel){
				MessageUtil.setMessage("Please map following mandatory fields: Subsidiary Id, Subsidiary Name, POS Store ID, " +
			                           "\n Store Name, Street, City, State, Country.","color:red","TOP");
				return;
			}

			try {
				int confirm = Messagebox.show("Are you sure you want to save the new mapping fields?", 
						"Confirm", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
				if(confirm == Messagebox.OK) {
					filePath = PropertyUtil.getPropertyValue("usersParentDirectory").trim() + "/" + sessionScope.get("userName") + "/List/" + uploadCSVTbId.getValue();
					uploadStores(filePath);
					
				}
				
			}catch (Exception e) {
				logger.error("Exception::",e);
			}
			
		}
			 
		     
			 session.removeAttribute("organizationStoresType"); 
			 session.removeAttribute("organizationStores");
		     Redirect.goTo(PageListEnum.EMPTY);
		     Redirect.goTo(PageListEnum.USERADMIN_ORG_STORES);
		     
		  
		}//onClick$addStoreBtnId()
		
		
		private void uploadStores(String path){
			try{
			logger.info("===path is ===="+path.toString());
			List rowList = contactRowsId.getChildren();
			int sbsIdIndx = -1,  sbsNameIdIndx = -1,  posLocationIdIndx = -1, storeNameIndx = -1, storeManagerNameIndx = -1, emailIdIndx = -1;
			int websiteIndx = -1, phoneIndx = -1, streetIndx = -1, cityIndx = -1, stateIndx = -1, zipIndx = -1;
			int fromNameIndx = -1, fromEmailIdIndx = -1, toEmailIdIndx = -1 ; // 2.4.4 release.
			int storeImgPathIndx = -1, brandImgPathIndx=-1,offersIndx=-1, descriptionIndx=-1,brandIndx=-1,countryIndx=-1, latIndx=-1,LngIndx=-1;	//2.7.4 
			
			List<String> contactHeadersList = new ArrayList<String>();
			FileReader fileReader = new FileReader(path);
			BufferedReader br = new BufferedReader(fileReader);
			String csvColumnStr = "";
			String lineStr=null;
			String[] lineStrTokens ;
			
		//	logger.info("===========outside while==========");
			while((lineStr = br.readLine())!= null) {
				
				if(lineStr.trim().length()==0) continue;
				lineStrTokens = parse(lineStr);
				if(lineStrTokens.length==0) { continue; }
		//		logger.info("===========inside while==========");
				
				for (Object object : rowList) {
					
		//			logger.info("===========inside for==========");
					Row tempRow = (Row)object;
					List mapRowLst = tempRow.getChildren();
					
					Div csvDiv =(Div)mapRowLst.get(0);
					String csvColLabel = ((Textbox)csvDiv.getChildren().get(0)).getValue();   // csv file label 2.4.3 rajeev
					
					Div optFieldDiv = (Div)mapRowLst.get(1);
					Listbox optCulFiledListbx = (Listbox)optFieldDiv.getChildren().get(0);
					String optCulFiled = optCulFiledListbx.getSelectedItem().getLabel();   // optculture filled label 2.4.3 rajeev
					
					for(int j=0; j< lineStrTokens.length ;j++) {
						
		//				logger.info("===========inside for j==========");
						csvColumnStr = lineStrTokens[j].trim();
						
						if(contactHeadersList.contains(csvColumnStr)) {		continue; 	}
						
						
						if(!(contactHeadersList.contains(csvColumnStr)) && optCulFiled.equals("Subsidiary Id") 
								&& csvColLabel.trim().equalsIgnoreCase(csvColumnStr)) {
							sbsIdIndx =  j;
							contactHeadersList.add(csvColumnStr);
							continue;
							
						}
						if(!(contactHeadersList.contains(csvColumnStr)) && optCulFiled.equals("Subsidiary Name") 
								&& csvColLabel.trim().equalsIgnoreCase(csvColumnStr)) {
							sbsNameIdIndx =  j;
							contactHeadersList.add(csvColumnStr);
							continue;
							
						}
						
						if(!(contactHeadersList.contains(csvColumnStr)) && optCulFiled.equals("POS Store ID") 
								&& csvColLabel.trim().equalsIgnoreCase(csvColumnStr)) {
							posLocationIdIndx =  j;
							contactHeadersList.add(csvColumnStr);
							continue;
							
						}
						
						if(!(contactHeadersList.contains(csvColumnStr)) && optCulFiled.equals("Store Name") 
								&& csvColLabel.trim().equalsIgnoreCase(csvColumnStr)) {
							storeNameIndx =  j;
							contactHeadersList.add(csvColumnStr);
							continue;
							
						}
						
						if(!(contactHeadersList.contains(csvColumnStr)) && optCulFiled.equals("Store Manager Name") 
								&& csvColLabel.trim().equalsIgnoreCase(csvColumnStr)) {
							storeManagerNameIndx =  j;
							contactHeadersList.add(csvColumnStr);
							continue;
							
						}
						
						if(!(contactHeadersList.contains(csvColumnStr)) && optCulFiled.equals("Email Id") 
								&& csvColLabel.trim().equalsIgnoreCase(csvColumnStr)) {
							emailIdIndx =  j;
							contactHeadersList.add(csvColumnStr);
							continue;
							
						}
						
						if(!(contactHeadersList.contains(csvColumnStr)) && optCulFiled.equals("Website") 
								&& csvColLabel.trim().equalsIgnoreCase(csvColumnStr)) {
							websiteIndx =  j;
							contactHeadersList.add(csvColumnStr);
							continue;
							
						}
						
						if(!(contactHeadersList.contains(csvColumnStr)) && optCulFiled.equals("Phone") 
								&& csvColLabel.trim().equalsIgnoreCase(csvColumnStr)) {
							phoneIndx =  j;
							contactHeadersList.add(csvColumnStr);
							continue;
							
						}
						
						if(!(contactHeadersList.contains(csvColumnStr)) && optCulFiled.equals("Street") 
								&& csvColLabel.trim().equalsIgnoreCase(csvColumnStr)) {
							streetIndx =  j;
							contactHeadersList.add(csvColumnStr);
							continue;
							
						}
						
						if(!(contactHeadersList.contains(csvColumnStr)) && optCulFiled.equals("City") 
								&& csvColLabel.trim().equalsIgnoreCase(csvColumnStr)) {
							cityIndx =  j;
							contactHeadersList.add(csvColumnStr);
							continue;
							
						}
						
						if(!(contactHeadersList.contains(csvColumnStr)) && optCulFiled.equals("State") 
								&& csvColLabel.trim().equalsIgnoreCase(csvColumnStr)) {
							stateIndx =  j;
							contactHeadersList.add(csvColumnStr);
							continue;
							
						}
						if(!(contactHeadersList.contains(csvColumnStr)) && optCulFiled.equals("Country") 
								&& csvColLabel.trim().equalsIgnoreCase(csvColumnStr)) {
							countryIndx =  j;
							contactHeadersList.add(csvColumnStr);
							continue;
							
				}
						
						if(!(contactHeadersList.contains(csvColumnStr)) && optCulFiled.equals("Zip Code") 
								&& csvColLabel.trim().equalsIgnoreCase(csvColumnStr)) {
							zipIndx =  j;
							contactHeadersList.add(csvColumnStr);
							continue;
							
						}
						
						if(!(contactHeadersList.contains(csvColumnStr)) && optCulFiled.equals("From Name") 
								&& csvColLabel.trim().equalsIgnoreCase(csvColumnStr)) {
							fromNameIndx =  j;
							contactHeadersList.add(csvColumnStr);
							continue;
							
						}
						
						if(!(contactHeadersList.contains(csvColumnStr)) && optCulFiled.equals("From Email Address") 
								&& csvColLabel.trim().equalsIgnoreCase(csvColumnStr)) {
							fromEmailIdIndx =  j;
							contactHeadersList.add(csvColumnStr);
							continue;
							
						}
						
						if(!(contactHeadersList.contains(csvColumnStr)) && optCulFiled.equals("Reply-to Email Address") 
								&& csvColLabel.trim().equalsIgnoreCase(csvColumnStr)) {
							toEmailIdIndx =  j;
							contactHeadersList.add(csvColumnStr);
							continue;
							
						}
						
						if(!(contactHeadersList.contains(csvColumnStr)) && optCulFiled.equals("Brand") 
								&& csvColLabel.trim().equalsIgnoreCase(csvColumnStr)) {
							brandIndx =  j;
							contactHeadersList.add(csvColumnStr);
							continue;
							
						}
						if(!(contactHeadersList.contains(csvColumnStr)) && optCulFiled.equals("Store Image URL") 
								&& csvColLabel.trim().equalsIgnoreCase(csvColumnStr)) {
							storeImgPathIndx =  j;
							contactHeadersList.add(csvColumnStr);
							continue;
							
						}
						if(!(contactHeadersList.contains(csvColumnStr)) && optCulFiled.equals("Brand Image URL") 
								&& csvColLabel.trim().equalsIgnoreCase(csvColumnStr)) {
							brandImgPathIndx =  j;
							contactHeadersList.add(csvColumnStr);
							continue;
							
						}
						if(!(contactHeadersList.contains(csvColumnStr)) && optCulFiled.equals("Latitude") 
								&& csvColLabel.trim().equalsIgnoreCase(csvColumnStr)) {
							latIndx =  j;
							contactHeadersList.add(csvColumnStr);
							continue;
							
						}
						if(!(contactHeadersList.contains(csvColumnStr)) && optCulFiled.equals("Longitude") 
								&& csvColLabel.trim().equalsIgnoreCase(csvColumnStr)) {
							LngIndx =  j;
							contactHeadersList.add(csvColumnStr);
							continue;
							
						}
						if(!(contactHeadersList.contains(csvColumnStr)) && optCulFiled.equals("Offers") 
								&& csvColLabel.trim().equalsIgnoreCase(csvColumnStr)) {
							offersIndx =  j;
							contactHeadersList.add(csvColumnStr);
							continue;
							
						}
						if(!(contactHeadersList.contains(csvColumnStr)) && optCulFiled.equals("Description") 
								&& csvColLabel.trim().equalsIgnoreCase(csvColumnStr)) {
							descriptionIndx =  j;
							contactHeadersList.add(csvColumnStr);
							continue;
							
						}
						
						
						
						
					} //end for j
					
				}  //end for 
				
				break;
				
			}  //end while
			
			
			//Read Data
			
			int totalStoresCount = 0,inValidStoreCount = 0,updatedStoreCount = 0,newStoreCount= 0;
	//		List<OrganizationStores> storeBulkList = new ArrayList<OrganizationStores>();
		
			while((lineStr = br.readLine())!= null) {
				try {
					logger.info("---while loop---");
//					stores = new OrganizationStores(); 
					if(lineStr.trim().length()==0) continue;
					lineStrTokens = parse(lineStr);
					
					if( lineStrTokens.length == 0) {
						continue;
					}
					
					totalStoresCount++;
					
					boolean isValidCont = false;
					String tempStrPin = null;
					
					
					String countryType = currentUser.getCountryType();
					
					/*if(posLocationIdIndx == -1 && storeNameIndx == -1 && streetIndx == -1 && cityIndx == -1 && stateIndx == -1 && zipIndx == -1){
						inValidStoreCount++;
						continue;
					}
					if(posLocationIdIndx != -1 && lineStrTokens[posLocationIdIndx].trim().length()  > 0) {
						
						isValidCont = true;
					}
					else if(storeNameIndx != -1 && lineStrTokens[storeNameIndx].trim().length()  > 0){
						isValidCont = true;
					}
					else if(streetIndx != -1 && lineStrTokens[streetIndx].trim().length()  > 0){
						isValidCont = true;
						logger.info("--value for street is----"+isValidCont);
					}
					else if(cityIndx != -1 && lineStrTokens[cityIndx].trim().length()  > 0){
						isValidCont = true;
					}
					else if(stateIndx != -1 && lineStrTokens[stateIndx].trim().length()  > 0){
						isValidCont = true;
					}
					else if(zipIndx != -1 && lineStrTokens[zipIndx].trim().length()  > 0){
						if(lineStrTokens[zipIndx].trim().length() == 6 || lineStrTokens[zipIndx].trim().length() == 5) {
							isValidCont = true;
						}
					}
					
					if(!isValidCont) {
						inValidStoreCount++;
						logger.info("---total invalid count is----"+inValidStoreCount);
		//				continue;
					}*/
					if(sbsIdIndx != -1 && lineStrTokens[sbsIdIndx].trim().length() == 0) {
						inValidStoreCount++;
						continue;
					}else if(sbsNameIdIndx != -1 && lineStrTokens[sbsNameIdIndx].trim().length() == 0) {
						inValidStoreCount++;
						continue;
					}
					else if(posLocationIdIndx != -1 && lineStrTokens[posLocationIdIndx].trim().length() == 0) {
						inValidStoreCount++;
						continue;
					}
					else if(storeNameIndx != -1 && lineStrTokens[storeNameIndx].trim().length() == 0){
						inValidStoreCount++;
						continue;
					}
					else if(streetIndx != -1 && lineStrTokens[streetIndx].trim().length() == 0){
						inValidStoreCount++;
						continue;
					}
					else if(cityIndx != -1 && lineStrTokens[cityIndx].trim().length() == 0){
						inValidStoreCount++;
						continue;
					}
					else if(stateIndx != -1 && lineStrTokens[stateIndx].trim().length() == 0){
						inValidStoreCount++;
						continue;
					}
					else if(countryIndx != -1 && lineStrTokens[countryIndx].trim().length() == 0){
						inValidStoreCount++;
						continue;
					}
					
					else if(Utility.zipValidateMap.containsKey(countryType)){
						if(zipIndx != -1 && lineStrTokens[zipIndx].trim().length() == 0 ){
							//(lineStrTokens[zipIndx].trim().length() != 6 || lineStrTokens[zipIndx].trim().length() != 5)){
							inValidStoreCount++;
							continue;
						}else if((tempStrPin = lineStrTokens[zipIndx].trim()).length() != 0){
					
							boolean validZip = Utility.validateZipCode(tempStrPin, countryType);
							if(!validZip){
								inValidStoreCount++;
								continue;
							}
						}
						
					}
					else if(brandIndx != -1 && lineStrTokens[brandIndx].trim().length() == 0){
						inValidStoreCount++;
						continue;
					}
					
					
					/*else if(zipIndx != -1 && lineStrTokens[zipIndx].trim().length() == 0 ){
		//					(lineStrTokens[zipIndx].trim().length() != 6 || lineStrTokens[zipIndx].trim().length() != 5)){
							inValidStoreCount++;
							continue;
					}
					else if((tempStrPin = lineStrTokens[zipIndx].trim()).length() != 0){
						String countryType = currentUser.getCountryType();
						boolean validZip = Utility.validateZipCode(tempStrPin, countryType);
						if(!validZip){
							inValidStoreCount++;
							continue;
						}
					}*/

					
					
					String homeStoreId = lineStrTokens[posLocationIdIndx].trim();
					String sbsId = lineStrTokens[sbsIdIndx].trim();
					String subName=lineStrTokens[sbsNameIdIndx].trim();
					String posId=lineStrTokens[posLocationIdIndx].trim();
				//	String storeName=lineStrTokens[storeNameIndx].trim();
		
					//OrganizationStores organizationStores = organizationStoresDao.findByStoresId(homeStoreId,GetUser.getUserObj().getUserOrganization().getUserOrgId());
					Long domainId = ((UsersDomains)orgUnitbulkLbId.getSelectedItem().getValue()).getDomainId();
					if(orgUnitbulkLbId.getSelectedIndex() == 0) {
						MessageUtil.setMessage("Please select Org.Unit ID.","color:red","TOP");
					return;
					}
					
					OrganizationStores organizationStores = organizationStoresDao.getStoreIdBySubsidary(currentUser.getUserOrganization().getUserOrgId(), domainId, sbsId,posId);
					//validate subsidiary
					
					if(organizationStores != null){
						/*try{
						organizationStores = setStoreFields(lineStrTokens, organizationStores, posLocationIdIndx, storeNameIndx, storeManagerNameIndx, emailIdIndx,
													  websiteIndx, phoneIndx, streetIndx, cityIndx, stateIndx, zipIndx, currentUser);
						  organizationStores = setStoreFields(lineStrTokens, organizationStores, sbsIdIndx, sbsNameIdIndx, posLocationIdIndx, storeNameIndx, storeManagerNameIndx, emailIdIndx,
									  websiteIndx, phoneIndx, streetIndx, cityIndx, stateIndx, zipIndx, currentUser, fromNameIndx,fromEmailIdIndx,toEmailIdIndx);
						//store is null
				//		if(organizationStores == null) continue;
				//		storeBulkList.add(organizationStores);
						updatedStoreCount++;
						
						//organizationStoresDao.saveOrUpdate(organizationStores);
						organizationStoresDaoForDML.saveOrUpdate(organizationStores);
						
						
						} catch (Exception e) {
							logger.debug("Exception while updaing the store..",e);
							inValidStoreCount++;
							continue;
						}*/
						continue;
					}
					else {
						try{
							
							organizationStores = new OrganizationStores();
							organizationStores.setUserOrganization(userOrganization);
							organizationStores.setDomainId(domainId);
							//brandImgPathIndx=-1, latLngIndx=-1, offersIndx=-1, descriptionIndx=-1,brandIndx=-1;
							organizationStores = setStoreFields(lineStrTokens, organizationStores,sbsIdIndx, sbsNameIdIndx,
									posLocationIdIndx, storeNameIndx, storeManagerNameIndx, emailIdIndx,
									  websiteIndx, phoneIndx, streetIndx, cityIndx, stateIndx, zipIndx, 
									  currentUser,fromNameIndx,fromEmailIdIndx,toEmailIdIndx,storeImgPathIndx,brandImgPathIndx, latIndx,LngIndx, offersIndx, descriptionIndx,brandIndx,countryIndx);
							
							if(organizationStores == null) continue;
							
							newStoreCount++;
							
							//organizationStoresDao.saveOrUpdate(organizationStores);
					
							organizationStoresDaoForDML.saveOrUpdate(organizationStores);
							
						}catch (Exception e) {
							logger.debug("Exception while adding the new store..",e);
							inValidStoreCount++;
						}
					}
					
					organizationStoresDaoForDML.upadteSubName(currentUser.getUserOrganization().getUserOrgId(),domainId , sbsId, subName);
					
					UserFromEmailId alreadyPresentUserFromEmailId = null;
					String confirmationURL;
					
					//logger.info("from email id added>>>>>>>>>>>>>"+lineStrTokens[fromEmailIdIndx].trim());
					//logger.info("to email id added>>>>>>>>>>>>>"+lineStrTokens[toEmailIdIndx].trim());
					
					if(fromEmailIdIndx != -1){
						if(Utility.validateEmail(lineStrTokens[fromEmailIdIndx].trim())){
							logger.info("from email id added>>>>>>>>>>>>>"+lineStrTokens[fromEmailIdIndx].trim());
							alreadyPresentUserFromEmailId = isRegisterNewFromEmail(lineStrTokens[fromEmailIdIndx].trim());
							if(alreadyPresentUserFromEmailId == null){
								alreadyPresentUserFromEmailId = setFromEmailID(lineStrTokens[fromEmailIdIndx].trim());
								logger.info("from email id added>>>>>>>>>>>>>"+lineStrTokens[fromEmailIdIndx].trim());
								//userFromEmailIdDao.saveOrUpdate(alreadyPresentUserFromEmailId);
								userFromEmailIdDaoForDML.saveOrUpdate(alreadyPresentUserFromEmailId);




								confirmationURL = PropertyUtil.getPropertyValue("confirmationURL") + "?requestedAction=userStoreFromEmailId&userId="+currentUser.getUserId()+
										"&homeStoreId=" + 
										homeStoreId + "&email=" + lineStrTokens[fromEmailIdIndx].trim();

								addVerificationMailToQueue("From-Email", confirmationURL, lineStrTokens[fromEmailIdIndx].trim());
							}else if(alreadyPresentUserFromEmailId != null){
								// do nothing
							}
						}
					}
					alreadyPresentUserFromEmailId = null;
					
					
					if(toEmailIdIndx != -1){
						logger.info("to email id added>>>>>>>>>>>>>"+lineStrTokens[toEmailIdIndx].trim());
						if(Utility.validateEmail(lineStrTokens[toEmailIdIndx].trim())){

							alreadyPresentUserFromEmailId = isRegisterNewFromEmail(lineStrTokens[toEmailIdIndx].trim());
							if(alreadyPresentUserFromEmailId == null){
								alreadyPresentUserFromEmailId = setFromEmailID(lineStrTokens[toEmailIdIndx].trim());
								logger.info("to email id added>>>>>>>>>>>>>"+lineStrTokens[toEmailIdIndx].trim());
								userFromEmailIdDaoForDML.saveOrUpdate(alreadyPresentUserFromEmailId);


/*								confirmationURL = PropertyUtil.getPropertyValue("confirmationURL") + "?requestedAction=userStoreFromEmailId&userId="+currentUser.getUserId()+
										"&homeStoreId=" + 
										homeStoreId + "&email=" + lineStrTokens[fromEmailIdIndx].trim();
*/
								confirmationURL = PropertyUtil.getPropertyValue("confirmationURL") + "?requestedAction=userStoreReplyToEmailId&userId="+currentUser.getUserId()+
										"&homeStoreId=" + 
										homeStoreId + "&email=" + lineStrTokens[toEmailIdIndx].trim();

								addVerificationMailToQueue("To-Email", confirmationURL, lineStrTokens[toEmailIdIndx].trim());
							}else if(alreadyPresentUserFromEmailId != null){
								// do nothing
							}

						}
						
					}
					
					
					
				} catch (Exception e) {
					logger.error("Exception ::", e);
				}
				
			} // while
			
			
			Messagebox.show("We have sent you a confirmation email to new emails added.\n " +
					"Follow the instructions in the email and this new To-Email/From-Email will be verified.", "Register email", Messagebox.OK, Messagebox.INFORMATION);
			//save By collection the list
		//	organizationStoresDao.saveByCollection(storeBulkList);
			fileReader.close();
			br.close();
			
			StringBuffer msgStuff = new StringBuffer(" Uploaded file name : ");
			msgStuff.append(uploadCSVTbId.getValue());
			msgStuff.append("\n Total stores : ");
			msgStuff.append(""+ totalStoresCount);
			msgStuff.append("\n Invalid stores : ");
			msgStuff.append(inValidStoreCount);
			msgStuff.append("\n Updated stores : ");
			msgStuff.append(updatedStoreCount);
			msgStuff.append("\n Newly added stores : ");
			msgStuff.append(newStoreCount);
			
			Messages messages = new Messages("Stores" ,"Uploaded Successfully" ,msgStuff.toString() ,
					Calendar.getInstance(),"Inbox",false ,"Info", currentUser); 
			//messagesDao.saveOrUpdate(messages);
			messagesDaoForDML.saveOrUpdate(messages);
			}catch (FileNotFoundException e) {
				logger.error("Exception ::", e);
			}catch (Exception e) {
				logger.error("Exception ::", e);
			}
			
			
		}
		
		private OrganizationStores setStoreFields(String[] nextLineStr, OrganizationStores store, int sbsIdIndex, int sbsNameIndex, int posLocationIdIndx, int storeNameIndx, int storeManagerNameIndx,
				 int emailIdIndx, int websiteIndx, int phoneIndx, int streetIndx, int cityIndx, int stateIndx, int zipIndx, Users currentUser, int fromNameIndx,int fromEmailIdIndx,int toEmailIdIndx,
				 int storeImgPathIndx,int brandImgPathIndx,int latIndx,int LngIndx,int offersIndx,int descriptionIndx,int brandIndx,int countryIndx){
			
			logger.info("---inside setStoreFieldData---");
			String tempStr=null;
		//	OrganizationStores store = new OrganizationStores();
			if(sbsIdIndex > -1 && sbsIdIndex < nextLineStr.length && (tempStr = nextLineStr[sbsIdIndex].trim()).length() !=0) {
				logger.info("---inside posLocationIdIndx---" + tempStr);
				store.setSubsidiaryId(tempStr);
				
			}
			if(sbsNameIndex > -1 && sbsNameIndex < nextLineStr.length && (tempStr = nextLineStr[sbsNameIndex].trim()).length() !=0) {
				logger.info("---inside posLocationIdIndx---" + tempStr);
				store.setSubsidiaryName(tempStr);
				
			}
			if(posLocationIdIndx > -1 && posLocationIdIndx < nextLineStr.length && (tempStr = nextLineStr[posLocationIdIndx].trim()).length() !=0) {
				logger.info("---inside posLocationIdIndx---" + tempStr);
				store.setHomeStoreId(tempStr);
				
			}
			
			if(storeNameIndx > -1 && storeNameIndx < nextLineStr.length && (tempStr = nextLineStr[storeNameIndx].trim()).length() !=0){
				logger.info("---inside storeNameIndx---" + tempStr);
				store.setStoreName(tempStr);
			}
			
			if(storeManagerNameIndx > -1 && storeManagerNameIndx < nextLineStr.length && (tempStr = nextLineStr[storeManagerNameIndx].trim()).length() !=0  ){
				
				store.setStoreManagerName(tempStr);
			}
			
			if(fromNameIndx > -1 && fromNameIndx < nextLineStr.length && (tempStr = nextLineStr[fromNameIndx].trim()).length() !=0  ){
				store.setFromName(tempStr);
			}
			
			if( emailIdIndx > -1 && emailIdIndx < nextLineStr.length && (Utility.validateEmail(tempStr = nextLineStr[emailIdIndx].trim()))) {
				logger.info("---inside emailIdIndx---" + tempStr);
				store.setEmailId(tempStr);
			}
			
			if(websiteIndx > -1 && websiteIndx < nextLineStr.length && (Utility.isURL(tempStr = nextLineStr[websiteIndx].trim()))){
				logger.info("---inside websiteIndx---" + tempStr);
				store.setWebsite(tempStr);
			}
			if(storeImgPathIndx > -1 && storeImgPathIndx < nextLineStr.length && (Utility.isImageURL(tempStr = nextLineStr[storeImgPathIndx].trim()))){
				logger.info("---inside storeImgPathIndx---" + tempStr);
				store.setStoreImagePath(tempStr);
			}
			if(brandImgPathIndx > -1 && brandImgPathIndx < nextLineStr.length && (Utility.isImageURL(tempStr = nextLineStr[brandImgPathIndx].trim()))){
				logger.info("---inside brandImgPathIndx---" + tempStr);
				store.setBrandImagePath(tempStr);
			}
			if(latIndx > -1 && latIndx < nextLineStr.length && ((tempStr = nextLineStr[latIndx].trim()).length()!=0)){
				logger.info("---inside latLngIndx---" + tempStr);
				store.setLatitude(tempStr);
			}
			if(LngIndx > -1 && LngIndx < nextLineStr.length && ((tempStr = nextLineStr[LngIndx].trim()).length()!=0)){
				logger.info("---inside latLngIndx---" + tempStr);
				store.setLongitude(tempStr);
			}
			if(descriptionIndx > -1 && descriptionIndx < nextLineStr.length && ((tempStr = nextLineStr[descriptionIndx].trim()).length()!=0)){
				logger.info("---inside descriptionIndx---" + tempStr);
				store.setDescription(tempStr);
			}
			if(brandIndx > -1 && brandIndx < nextLineStr.length && ((tempStr = nextLineStr[brandIndx].trim()).length()!=0)){
				logger.info("---inside brandIndx---" + tempStr);
				store.setStoreBrand(tempStr);
			}
			if(streetIndx > -1 && streetIndx < nextLineStr.length && (tempStr = nextLineStr[streetIndx].trim()).length() !=0){
				logger.info("---inside streetIndx---" + tempStr);
				store.setLocality(tempStr);
			}
			
			
			if(cityIndx > -1 && cityIndx < nextLineStr.length && (tempStr = nextLineStr[cityIndx].trim()).length() !=0){
				logger.info("---inside cityIndx---" + tempStr);
				store.setCity(tempStr);
			}
			
			if(stateIndx > -1 && stateIndx < nextLineStr.length && (tempStr = nextLineStr[stateIndx].trim()).length() !=0){
				logger.info("---inside stateIndx---" + tempStr);
				store.setState(tempStr);
			}
			if(countryIndx > -1 && countryIndx < nextLineStr.length && (tempStr = nextLineStr[countryIndx].trim()).length() !=0){
				logger.info("---inside countryIndx---" + tempStr);
				store.setCountry(tempStr);
			}
			
			if(zipIndx > -1 && zipIndx < nextLineStr.length && (tempStr = nextLineStr[zipIndx].trim()).length() !=0  ){
				logger.info("---inside zipIndx---" + tempStr);
				store.setZipCode(tempStr);
				/*if(tempStr.trim().length() == 6 || tempStr.trim().length() == 5) {
					try {
					int pinNum  = Integer.parseInt(tempStr);
					address.setPin(""+pinNum);
					} catch (Exception e) {
						logger.error("Exception ::", e);
					}
				}*/
			}
			
			 //brandImgPathIndx=-1, latLngIndx=-1, offersIndx=-1, descriptionIndx=-1,brandIndx=-1;
			/* stores.setStoreImagePath(storeImgUrlTbId.getValue());
			 stores.setBrandImagePath(brandImgUrlTbId.getValue());
			 stores.setLatLng(Double.parseDouble(latLngTbId.getValue()));
			 stores.setOffers(offersTbId.getValue());
			 stores.setDescription(descriptionTbId.getValue());
			*/
			Address address = null;
			address = new Address();
			
			store.setAddressFlag(true);
			
			if(streetIndx > -1 && streetIndx < nextLineStr.length && (tempStr = nextLineStr[streetIndx].trim()).length() !=0){
				logger.info("---inside streetIndx---" + tempStr);
				address.setAddressOne(tempStr);
			}
			
			if(cityIndx > -1 && cityIndx < nextLineStr.length && (tempStr = nextLineStr[cityIndx].trim()).length() !=0){
				logger.info("---inside cityIndx---" + tempStr);
				address.setCity(tempStr);
			}
			
			if(stateIndx > -1 && stateIndx < nextLineStr.length && (tempStr = nextLineStr[stateIndx].trim()).length() !=0){
				logger.info("---inside stateIndx---" + tempStr);
				address.setState(tempStr);
			}
			if(countryIndx > -1 && countryIndx < nextLineStr.length && (tempStr = nextLineStr[countryIndx].trim()).length() !=0){
				logger.info("---inside countryIndx---" + tempStr);
				address.setCountry(tempStr);
			}
			
			if(zipIndx > -1 && zipIndx < nextLineStr.length && (tempStr = nextLineStr[zipIndx].trim()).length() !=0  ){
				logger.info("---inside zipIndx---" + tempStr);
				address.setPin(tempStr);
				/*if(tempStr.trim().length() == 6 || tempStr.trim().length() == 5) {
					try {
					int pinNum  = Integer.parseInt(tempStr);
					address.setPin(""+pinNum);
					} catch (Exception e) {
						logger.error("Exception ::", e);
					}
				}*/
			}
			
			if(phoneIndx > -1 && phoneIndx < nextLineStr.length && (tempStr = nextLineStr[phoneIndx].trim()).length() !=0){
				logger.info("---inside stateIndx---" + tempStr);
				store.setMobileNo(tempStr);
			}
			
			if( fromEmailIdIndx > -1 && fromEmailIdIndx < nextLineStr.length && (Utility.validateEmail(tempStr = nextLineStr[fromEmailIdIndx].trim()))) {
				logger.info("---inside emailIdIndx---" + tempStr);
				store.setFromEmailId(tempStr);
			}
			
			if(toEmailIdIndx > -1 && toEmailIdIndx < nextLineStr.length && (Utility.validateEmail(tempStr = nextLineStr[toEmailIdIndx].trim()))){
				logger.info("---inside websiteIndx---" + tempStr);
				store.setReplyToEmailId(tempStr);
			}
			
			if(phoneIndx > -1 && phoneIndx < nextLineStr.length && (tempStr = nextLineStr[phoneIndx].trim()).length() !=0  ){
				logger.info("---inside phoneIndx---" + tempStr);
				try {
					String mobileStr = Utility.phoneParse(tempStr, currentUser.getUserOrganization());
					if(mobileStr != null && mobileStr.trim().length() >0){
						address.setPhone(mobileStr);
					}
					
				}catch (NumberFormatException e) {
				}
			}
			
			store.setAddress(address);
			
			 store.setUserOrganization(currentUser.getUserOrganization());
			 store.setCreatedDate(MyCalendar.getInstance(clientTimeZone));
			 store.setModifiedDate(MyCalendar.getInstance(clientTimeZone));
			
			return store;
		}
		
		
		public void onClick$editStoreBtnId() {
			
			
			posLocCb.setReadonly(false);
			storeNameTbId.setReadonly(false);
			storeManagerNameTbId.setReadonly(false);
			emailIdTbId.setReadonly(false);
			websiteTbId.setReadonly(false);
			phoneTbId.setReadonly(false);
			streetTbId.setReadonly(true);
			cityTbId.setReadonly(false);
			stateTbId.setReadonly(false);
			zipTbId.setReadonly(false);
			brandTbId.setReadonly(false);
			
			addStoreBtnId.setLabel("Update");
			addStoreBtnId.setVisible(true);
			cancelStoreBtnId.setVisible(true);
			
			editStoreBtnId.setVisible(false);
			backBtnId.setVisible(false);
					
			
		}//onClick$editStoreBtnId()
		
		
		
		public void onClick$backBtnId() {
			
			organizationStoresTabBoxId.setSelectedIndex(0);
			onSelect$organizationStoresTabBoxId();
		}
		// onClick$backBtnId()
		
		
		
		
		
		
		public void onClick$storeFilterBtnId() {
		
			//int index = searchByStoreID.getSelectedIndex();
			/*if(index == 0) {
				
				logger.info("searchByStoreID.getSelectedItem()"+searchByStoreID.getSelectedItem());
				
				if(searchByStoreID.getSelectedItem() == null || searchByStoreID.getSelectedIndex() == 0) {
					
					
					MessageUtil.setMessage("Please select at least one filter option.", "color:red;");
					searchByStoreID.setFocus(true);
					return;
				}
			}
				*/
				
			//storeList = organizationStoresDao.findByStoreId(searchByStoreID.getSelectedItem().getLabel().trim(), currentUser.getUserOrganization().getUserOrgId(), storeNameTbId.getValue());
			Components.removeAllChildren(orgStoreRowsId);
			
			//setpageTotCount(null);
			//storeLocationsPagingId.setActivePage(0);
			//redrawStores( 0,storeLocationsPagingId.getPageSize(), null);
			String orgId="All";
			if(searchByOrgID.getSelectedIndex() != 0) {
				
				//orgId = ((OrganizationStores)searchByOrgID.getSelectedItem().getValue()).getUserOrganization().toString();
				UsersDomains userDomains = (UsersDomains) searchByOrgID.getSelectedItem().getValue();
				orgId = userDomains.getDomainId().toString();
				
			}
			String sbsId="All";
			String storeId="All";
			if(searchBySbsID.getSelectedIndex() != 0) {
				
				 sbsId = ((OrganizationStores)searchBySbsID.getSelectedItem().getValue()).getSubsidiaryId();
				
			}
			if(searchByStoreID.getSelectedIndex() != 0) {
				
				 storeId = searchByStoreID.getSelectedItem().getValue();
				
			}
			
			String searchStr = (searchBoxId.getValue().trim().equals("Search store...") || 
					searchBoxId.getValue().trim().equals("")) ? null : searchBoxId.getValue().trim() ;
			int totalSize = organizationStoresDao.findByOrgStoresCount(GetUser.getUserObj().getUserOrganization().getUserOrgId(),orgId,sbsId,storeId,searchStr); 		
			storeLocationsPagingId.setTotalSize(totalSize);
			storeLocationsPagingId.setActivePage(0);
			storeLocationsPagingId.addEventListener("onPaging", this);
			logger.debug("filter  :: "+searchBoxId.getValue());
			redrawStores( 0,storeLocationsPagingId.getPageSize(),searchStr);
				
				
	}//onClick$storeFilterBtnId()
		
		
		public void onChanging$searchBoxId(InputEvent event) {

		/*	String key = event.getValue();
			int totalSize=0;
			logger.debug("got the key ::"+key);
			
			String storeId = "All";
			if (key.trim().length() != 0) {
				
				 totalSize = organizationStoresDao.findByOrgStoresCount(GetUser.getUserObj().getUserOrganization().getUserOrgId(),  key); 
				
				
				storeLocationsPagingId.setTotalSize(totalSize);
				
			//setpageTotCount(key);
			logger.debug("seearch :: "+searchBoxId.getValue());
			//storeLocationsPagingId.setActivePage(0);
			redrawStores( 0,storeLocationsPagingId.getPageSize(),key);
				
			}
			
			
			else {
				totalSize = organizationStoresDao.findByOrgStoresCount(GetUser.getUserObj().getUserOrganization().getUserOrgId(), null); 
				
				storeLocationsPagingId.setTotalSize(totalSize);
				//setpageTotCount(null);
				// totalSize = organizationStoresDao.findByOrganizationstoresCount(GetUser.getUserObj().getUserOrganization().getUserOrgId(), storeId, null); 
				redrawStores( 0,storeLocationsPagingId.getPageSize(), null);
				
				
			}*/

		}
						

			
		
		public void onClick$resetSearchCriteriaAnchId() {
			
			try {
				int count = searchByStoreID.getItemCount();
				logger.info("prev store count" + count);
				for (; count > 1; count--) {
					searchByStoreID.removeItemAt(count - 1);
				}
				
				int cnt = searchBySbsID.getItemCount();
				logger.info("prev store count" + cnt);
				for (; cnt > 1; cnt--) {
					searchBySbsID.removeItemAt(cnt - 1);
				}
				
				searchBoxId.setValue("");
				searchByOrgID.setSelectedIndex(0);
				searchBySbsID.setSelectedIndex(0);
				searchByStoreID.setSelectedIndex(0);
				setpageTotCount(null);
				String orgId="All";
				String sbsId="All";
				String storeId = "All";
				
				/*if(searchByOrgID.getSelectedIndex() != 0) {
					UsersDomains userDomains = (UsersDomains) searchByOrgID.getSelectedItem().getValue();
					orgId = userDomains.getDomainId().toString();
					//orgId = ((OrganizationStores)searchByOrgID.getSelectedItem().getValue()).getUserOrganization().toString();
					
				}
				if(searchBySbsID.getSelectedIndex() != 0) {
					
					sbsId = ((OrganizationStores)searchBySbsID.getSelectedItem().getValue()).getSubsidiaryId();
					
				}*/
				if(searchByStoreID.getSelectedIndex() != 0) {
					
					storeId = searchByStoreID.getSelectedItem().getValue();
					
				}
				int totalSize = organizationStoresDao.findByOrganizationstoresCount(GetUser.getUserObj().getUserOrganization().getUserOrgId(), storeId, null); 
				storeLocationsPagingId.setActivePage(0);
				storeLocationsPagingId.addEventListener("onPaging", this);
				storeLocationsPagingId.setTotalSize(totalSize);
				redrawStores( 0,storeLocationsPagingId.getPageSize(), null);
								
			} catch (WrongValueException e) {
				// TODO Auto-generated catch block
				logger.error("Exception ::", e);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Exception ::", e);
			}
		}//onClick$resetSearchCriteriaAnchId()
		
		
		public void onSelect$organizationStoresTabBoxId() {
			sbsNumbCmboBxId.setValue("");
			sbsNumbCmboBxId.setDisabled(false);
			logger.debug("----just entered ----");
		//	singleuploadRbId.isChecked();
			userFromEmailIdDao = (UserFromEmailIdDao)SpringUtil.getBean("userFromEmailIdDao");
			userFromEmailIdDaoForDML = (UserFromEmailIdDaoForDML)SpringUtil.getBean("userFromEmailIdDaoForDML");
			logger.info("userFromEmailIdList>>>>>>>>>>>>>>>>"+userFromEmailIdList);
			bulkuploadRbId.setChecked(true);
			bulkAddStoreDiv.setVisible(true);
			singleAddStoreDiv.setVisible(false);
			
			type = (String)session.removeAttribute("organizationStoresType"); 
			stores = (OrganizationStores)session.removeAttribute("organizationStores");
			//posLocationTbId.setValue("");
			sbLocName.setValue("");
			sbLocName.setValue("");
			
			orgUnitId.setSelectedIndex(0);
			orgUnitId.setDisabled(false);

			posLocCb.setValue("");
			posLocCb.setDisabled(false);
			sbLocName.setDisabled(false);
			sbLocName.setDisabled(false);
			
			storeNameTbId.setValue("");
			storeNameTbId.setDisabled(false);
			storeManagerNameTbId.setValue("");
			emailIdTbId.setValue("");
			websiteTbId.setValue("");
			phoneTbId.setValue("");
			streetTbId.setValue("");
			cityTbId.setValue("");
			stateTbId.setValue("");
			zipTbId.setValue("");
			countryTbId.setValue("");
			brandTbId.setValue("");
			
			posLocCb.setReadonly(false);
			storeNameTbId.setReadonly(false);
			storeManagerNameTbId.setReadonly(false);
			emailIdTbId.setReadonly(false);
			websiteTbId.setReadonly(false);
			emailIdTbId.setReadonly(false);
			phoneTbId.setReadonly(false);
			streetTbId.setReadonly(false);
			cityTbId.setReadonly(false);
			stateTbId.setReadonly(false);
			zipTbId.setReadonly(false);
			countryTbId.setReadonly(false);
			brandTbId.setReadonly(false);
			
			addStoreBtnId.setLabel("Add Store");
			addStoreBtnId.setVisible(true);
			cancelStoreBtnId.setVisible(false);
			editStoreBtnId.setVisible(false);
			backBtnId.setVisible(false);
			
		}//onSelect$organizationStoresTabBoxId()
		
		public void storeSettings() {
			
			if(organizationStoresTabBoxId.getSelectedIndex() == 1 && singleuploadRbId.isChecked()) {
				
				type = (String)session.getAttribute("organizationStoresType");
				stores = (OrganizationStores)session.getAttribute("organizationStores");
				//userNameTbId,passwordTbId,rePasswordTbId,emailIdTbId,firstNameTbId,lastNameTbId;
				
				if(type != null && stores != null) {
					
					logger.info("in View===");
					logger.info("======================SubId "+stores.getSubsidiaryId());
					sbsNumbCmboBxId.setValue(stores.getSubsidiaryId());
					sbLocName.setValue(stores.getSubsidiaryName());
					sbsNumbCmboBxId.setDisabled(true);
					List<Listitem> retList = orgUnitId.getItems();
					for (Listitem listitem : retList) {
						logger.debug(stores.getDomainId());
						
						if(listitem.getIndex() == 0 )continue;
						
						logger.debug(((UsersDomains)listitem.getValue()));
						if(((UsersDomains)listitem.getValue()).getDomainId().longValue() == stores.getDomainId().longValue()) {
							listitem.setSelected(true);
							break;
						}
						
						
					}
					
					orgUnitId.setDisabled(true);
					
					
					posLocCb.setValue(stores.getHomeStoreId());
					posLocCb.setDisabled(true);
					storeNameTbId.setValue(stores.getStoreName().trim());
					storeNameTbId.setDisabled(false);
					storeManagerNameTbId.setValue(stores.getStoreManagerName());
					emailIdTbId.setValue(stores.getEmailId());
					websiteTbId.setValue(stores.getWebsite());
					frmNameTbId.setValue(stores.getFromName());
					cFromEmailCb.setValue(stores.getFromEmailId()); 
					cReplyToEmailCb.setValue(stores.getReplyToEmailId());
					storeImgUrlTbId.setValue((stores.getStoreImagePath()!=null)?stores.getStoreImagePath():"");
					brandTbId.setValue((stores.getStoreBrand()!=null)?stores.getStoreBrand():"");
					brandImgUrlTbId.setValue((stores.getBrandImagePath()!=null)?stores.getBrandImagePath():"");
					descriptionTbId.setValue((stores.getDescription()!=null)?stores.getDescription():"");
					latTbId.setValue((stores.getLatitude()!=null)?(stores.getLatitude().toString()):"");
					LngTbId.setValue((stores.getLongitude()!=null)?(stores.getLongitude().toString()):"");
					if(stores.isAddressFlag()){
						try {
				   Address address = stores.getAddress();
				
					streetTbId.setValue(address.getAddressOne());
					streetTbId.setAttribute("value", address.getAddressOne());
					
					cityTbId.setValue(address.getCity());
					cityTbId.setAttribute("value", address.getCity());
				
                    stateTbId.setValue(address.getState());
                    stateTbId.setAttribute("value", address.getState());
                    
                    countryTbId.setValue(address.getCountry());
                    countryTbId.setAttribute("value", address.getCountry());

                 
			    	zipTbId.setValue(address.getPin()+ "");
					zipTbId.setAttribute("value", address.getPin());
					
					phoneTbId.setValue(address.getPhone());
					phoneTbId.setAttribute("value", address.getPhone());
					}catch(Exception e){
					  logger.error("Exception ::", e);
					}
					
					/*phoneTbId.setValue(stores.getPhone());
					streetTbId.setValue(stores.getStreet());
					cityTbId.setValue(stores.getCity());
					stateTbId.setValue(stores.getState());
					zipTbId.setValue(stores.getZip());*/
				    
					
						
					}
					
					
					
					if(type != null && type.equals("view")) {
						sbLocName.setDisabled(true);
						posLocCb.setReadonly(true);
						storeNameTbId.setReadonly(true);
						storeManagerNameTbId.setReadonly(true);
						emailIdTbId.setReadonly(true);
						websiteTbId.setReadonly(true);
					    phoneTbId.setReadonly(true);
						streetTbId.setReadonly(true);
						cityTbId.setReadonly(true);
						stateTbId.setReadonly(true);
						zipTbId.setReadonly(true);
						countryTbId.setReadonly(true);
						brandTbId.setReadonly(true);
						
						addStoreBtnId.setLabel("Edit");
						addStoreBtnId.setVisible(false);
						cancelStoreBtnId.setVisible(false);
						editStoreBtnId.setVisible(true);
						backBtnId.setVisible(true);
						
					    defaultSettings4FromAndReply2Emails(stores.getStoreId());
						
					}else if(type != null && type.equals("edit")) {
						sbLocName.setDisabled(false);
						posLocCb.setReadonly(false);
						storeNameTbId.setReadonly(false);
						storeManagerNameTbId.setReadonly(false);
						emailIdTbId.setReadonly(false);
						websiteTbId.setReadonly(false);
						emailIdTbId.setReadonly(false);
						phoneTbId.setReadonly(false);
						streetTbId.setReadonly(false);
						cityTbId.setReadonly(false);
						stateTbId.setReadonly(false);
						zipTbId.setReadonly(false);
						countryTbId.setReadonly(false);
						brandTbId.setReadonly(false);
						
						
						
						
						addStoreBtnId.setLabel("Update");
						addStoreBtnId.setVisible(true);
						cancelStoreBtnId.setVisible(true);
						editStoreBtnId.setVisible(false);
						backBtnId.setVisible(false);
						
						defaultSettings4FromAndReply2Emails(stores.getStoreId());
						
					}//else if
				}//if
				
			}
			}//storeSettings()
		
		
		
		
		
		// add store location 
		
		/*
		public void getPosLocIds() {
			
			for(int i = posLocCb.getItemCount() ; i>1 ; i--) {
				
				posLocCb.removeItemAt(i);
			}
			
			List<Object> posLocIdList = contactsDao.findPosLocId(currentUser.getUserId());
			
			//List<Object> posLocIdList = userFromEmailIdDao.findPosLocId(currentUser.getUserId());
			Comboitem comboitem = null;
			for (Object object : posLocIdList) {
				String posLoc = (String)object;
			
				comboitem = new Comboitem(posLoc);
				comboitem.setParent(posLocCb);
				posLocCb.setSelectedIndex(0);
		}
		
			
		}
		*/
	
		public void onCheck$uploadRgId(){
			/*singleAddStoreDiv.setVisible(uploadRgId.getSelectedItem().getId().equals(singleuploadRbId) ? true : false);
			bulkAddStoreDiv.setVisible(uploadRgId.getSelectedItem().getId().equals(bulkuploadRbId) ? true : false);*/
			
			
			logger.info("userFromEmailIdList>>>>>>>>>>>>>>>>"+userFromEmailIdList);
			if(uploadRgId.getSelectedIndex() == 0){
				logger.info("inside bulk add store---------");
				singleAddStoreDiv.setVisible(false);
				bulkAddStoreDiv.setVisible(true);
			}else if(uploadRgId.getSelectedIndex() == 1){
				logger.info("inside single add store---------");
				singleAddStoreDiv.setVisible(true);
				bulkAddStoreDiv.setVisible(false);
				if(stores == null){
					frmNameTbId.setValue("");
					//clearAllFromToEmailAddresses4NewStore();
					defaultSettings4FromAndReply2Emails(null);
					
				}
			}
		}
		
		
		public void onClick$uploadBtnId(){
			Media media = Fileupload.get();	
			MessageUtil.clearMessage();
			if(media == null) {
				MessageUtil.setMessage("Please select a file.", "color:red", "TOP");
				return;
			}
			String path = PropertyUtil.getPropertyValue("usersParentDirectory").trim() + "/" + sessionScope.get("userName") + "/List/" +((Media)media).getName();
			logger.info("path---->"+path);
			String ext = FileUtil.getFileNameExtension(path);
			logger.info("ext---->"+ext);
			if(ext == null){
				MessageUtil.setMessage("Upload .csv file only.","color:red","TOP");
				return;
			}
			if(!ext.equalsIgnoreCase("csv")){
				MessageUtil.setMessage("Upload .csv file only.","color:red","TOP");
				return;
			}
			
			
			String pathString = PropertyUtil.getPropertyValue("usersParentDirectory").trim() + "/" + GetUser.getUserName() + "/List/" + media.getName();
			boolean isSuccess = copyDataFromMediaToFile(pathString,media);
			uploadCSVTbId.setValue(media.getName());
			uploadCSVTbId.setDisabled(true);
			media = null;
			if(!isSuccess){
				return;
			}
			
			logger.info("===inside uploadBtnId===");
			defaultMapSettings(pathString);
			mappingCSVId.setVisible(true);
		}
		
		
		private void defaultMapSettings(String filePath){
			
			Components.removeAllChildren(contactRowsId);
			logger.info("===inside defaultMapSetting===");
			BufferedReader br;
			String lineStr;
			List<String> fileHeaderList = new ArrayList<String>();
			try {
				FileReader fileReader = new FileReader(filePath);
				br = new BufferedReader(fileReader);
				lineStr = null;
				String[] lineStrTokens ;
				
				//Getting the Headers 
				while((lineStr = br.readLine())!= null) {
//				lineStr += ",\"0\"";
					if(lineStr.trim().length()==0) continue;
					
					lineStrTokens = parse(lineStr);// lineStr.split(csvDelemiterStr);
					logger.debug("lineStrTokens>>>>"+lineStrTokens);
					
					if(lineStrTokens.length==0) { continue; }	
				
					for(int j=0; j< lineStrTokens.length ;j++) {
						fileHeaderList.add(lineStrTokens[j].trim());
//						csvColumnStr = lineStrTokens[j].trim();
						
					}
					break;
				} // while
				
			} catch (Exception e) {
				logger.error("Exception ::", e);
			}
			
			if(fileHeaderList == null || fileHeaderList.size() == 0) {
				MessageUtil.setMessage("File does not have the header field set.", "Color:red", "Top");
				return;
			}
			
			for (String eachStr : fileHeaderList) {
				/*logger.info("===fileHeaderList are==="+ eachStr);*/
				defaultGenFieldMapp(eachStr);
				
			}
			
			
		}
		
		
		private void defaultGenFieldMapp(String fileHeader){
			logger.info("===inside defaultGenFieldMapp===");
			logger.info("===fileHeaderList are==="+ fileHeader);
			Row row = null;
			row = new Row();
			
//			CSV Colomn Label
			Div csvDiv=new Div();
			csvDiv.setParent(row);
			
			Textbox posAttrTextBx = new Textbox();
			posAttrTextBx.setValue(fileHeader);
			posAttrTextBx.setDisabled(true);
			posAttrTextBx.setParent(csvDiv);
			
			Image csvDelValImg = new Image();
			csvDelValImg.setSrc("/img/action_delete.gif");
			csvDelValImg.setStyle("cursor:pointer;");
			csvDelValImg.setStyle("cursor:pointer;margin:0 10px 0 15px;");
			csvDelValImg.addEventListener("onClick", this);
		//	csvDelValImg.setAttribute("TYPE", "POS_DEL_OPTIONAL_VALUE");
			csvDelValImg.setParent(csvDiv);
			
			csvDelValImg.setAttribute("type", "CONTACT_MAPPING_DELETE");
			//row.setParent(contactRowsId);
			
//			Custom Field
			Div ocAttrDiv=new Div();
			Listbox optFiledLstBx = null;
			
			optFiledLstBx =  createMappingListbox();
			optFiledLstBx.setParent(ocAttrDiv);
			ocAttrDiv.setParent(row);
			row.setParent(contactRowsId);
			
		
		}
		
		
		private Listbox createMappingListbox(){
			Listbox formatListBx = new Listbox();
			Listitem tempItem = null;
			
			for(int i=0; i<storeMapStr.length; i++) { 
				tempItem = new Listitem(storeMapStr[i]);
				tempItem.setParent(formatListBx);
			}
			
			formatListBx.setMold("select");
			formatListBx.setSelectedIndex(0);
			return formatListBx;
		}

		
		private final Pattern csvPattern = Pattern.compile("\"([^\"]*)\"|(?<=,|^)([^,]*)(?:,|$)");  
	    private ArrayList<String> allMatches = new ArrayList<String>();        
	    private Matcher matcher = null;
		public String[] parse(String csvLine) {
	        matcher = csvPattern.matcher(csvLine);
	        allMatches.clear();
	        String match;
	        while (matcher.find()) {
	                match = matcher.group(1);
	                if (match!=null) {
	                        allMatches.add(match);
	                }
	                else {
	                        allMatches.add(matcher.group(2));
	                }
	        }

	        if (allMatches.size() > 0) {
	                return allMatches.toArray(new String[allMatches.size()]);
	        }
	        else {
	                return new String[0];
	        }                       
	    }
		
		public boolean copyDataFromMediaToFile(String path,Media m) {
			MessagesDao messagesDao = (MessagesDao) SpringUtil.getBean("messagesDao");
			MessagesDaoForDML messagesDaoForDML = (MessagesDaoForDML) SpringUtil.getBean("messagesDaoForDML");
			String ext = FileUtil.getFileNameExtension(path);
			File file = new File(path);
			BufferedReader br = null;
			BufferedWriter bw = null ;
			if(!ext.equalsIgnoreCase("csv")){
				MessageUtil.setMessage("Upload .csv file only.","color:red","BOTTOM");
				return false;
			}
			try{
//				if(logger.isDebugEnabled()) logger.debug("reading data from media using getReaderData()");
				br = new BufferedReader((InputStreamReader)m.getReaderData());
				bw = new BufferedWriter(new FileWriter(path));
				String line = "";
				while((line=br.readLine())!=null){
					bw.write(line);
					bw.newLine();
				}
				bw.flush();
				bw.close();
				br.close();
				return true;
			}catch(Exception e1){
//				logger.error("** Exception is " + e1.getMessage()+" :trying to read with Media.getStringData() **");
				try{
//					if(logger.isDebugEnabled()) logger.debug("Reading file with Media.getStringData()");
					String data = m.getStringData(); 
					FileUtils.writeStringToFile(file, data);
					return true;
				}catch(Exception e2){
//					logger.error("** Exception is " + e2 +" :trying to read as Streams **");
					try {
						FileOutputStream out = new FileOutputStream (file);
						BufferedInputStream in = new BufferedInputStream((FileInputStream)m.getStreamData());
						byte[] buf = new byte[1024];
						int count = 0;
						while ((count = in.read(buf)) >= 0) {
							out.write(buf, 0, count);
						}
						out.flush();
						in.close();
						out.close();
						return true;
					} catch (FileNotFoundException e) {
//						logger.error("** Exception is : File not found **");
					} catch (Exception e3) {
//						logger.error("** Exception is " + e3 +"  so trying to read as bytes **");
						try {
							byte[] data = m.getByteData();
							FileOutputStream fos = new FileOutputStream(file);
							fos.write(data);
							fos.flush();
							fos.close(); 
							return true;
						} catch (Exception e) {
//							logger.error("** Exception is " + e +" **");
						}
					}
					String message = "CSV file upload failed,"+m.getName()+"\n could not copied reason may be due to network problem or may be very large file";
					Users user = GetUser.getUserObj();
					(new MessageHandler(messagesDao,user.getUserName())).sendMessage("Contact","uploaded failed",message,"Inbox",false,"INFO", user);
					return false;
				}
				
			}
		} // copyDataFromMediaToFile
		
		
		public void onClick$cancelBtnId() {

			regEmailPopupId.close();

		}
		
		public void onClick$submitBt1nId() {
			try {
				
				
				String flagStr = (String)regEmailPopupId.getAttribute("flagStr");
				String confirmationURL = null;
				String homeStoreId = posLocCb.getValue().trim();
				String newFromEmail= cEmailTb.getValue().trim();
				UserFromEmailId userFromEmailId = null;
				
				
				if(newFromEmail.trim().equals("")){
					regEmailPopupId.close();
					MessageUtil.setMessage("Email field cannot be left empty.", "color:red", "TOP");
					return;
				}
				
				if(!Utility.validateEmail(newFromEmail.trim())) {
					regEmailPopupId.close();
					MessageUtil.setMessage("Please enter a valid email address.", "color:red", "TOP");
					cEmailTb.setValue("");
					return;
			 	}
				
				if(homeStoreId.length() == 0) {
					MessageUtil.setMessage("Please provide POS Store ID. POS Store ID cannot be left empty.", "color:red;");
					return;
					
				}
				
				UserFromEmailId alreadyPresentUserFromEmailId = isRegisterNewFromEmail(cEmailTb.getValue().trim());
				
				confirmationURL = PropertyUtil.getPropertyValue("confirmationURL") + "?requestedAction=userStoreFromEmailId&userId="+currentUser.getUserId()+
						"&homeStoreId=" + 
						homeStoreId + "&email=" + newFromEmail;
				
				if(alreadyPresentUserFromEmailId == null){
					userFromEmailId = new UserFromEmailId();
					userFromEmailId.setUsers(currentUser);
					userFromEmailId.setEmailId(newFromEmail);
					userFromEmailId.setStatus(0);
					//userFromEmailIdDao.saveOrUpdate(userFromEmailId);
					userFromEmailIdDaoForDML.saveOrUpdate(userFromEmailId);

					
					addVerificationMailToQueue(flagStr, confirmationURL, cEmailTb.getValue().trim());
					
				}else if(alreadyPresentUserFromEmailId != null){
					   
					   if(alreadyPresentUserFromEmailId.getStatus() == 0){
						   doSomeProcessIfEmailIdIsThere(cEmailTb,flagStr,confirmationURL);
					   }
					
				}
				
				
			} catch (Exception e) {
				
				logger.error("Exception ::", e);
			}

		}

		public void onClick$frmEmailAnchId() {
			
			regEmailPopupId.removeAttribute("flagStr");
			regEmailPopupId.setAttribute("flagStr", "From-Email");
		}
		
		public void onClick$replyToEmailAnchId() {
			
			regEmailPopupId.removeAttribute("flagStr");
			regEmailPopupId.setAttribute("flagStr", "To-Email");
		}
		
		public UserFromEmailId isRegisterNewFromEmail(String newFromEmail) throws Exception {
			
			try {
				
				Users user  = GetUser.getUserObj();	
				
				boolean newEmailIdThere = false;
				UserFromEmailId alreadyPresentUserFromEmailId = null;
					
					if(userFromEmailIdList.size() > 0){
						for(UserFromEmailId aUserFromEmailId : userFromEmailIdList){
							if(aUserFromEmailId.getEmailId() !=null ){
								if(aUserFromEmailId.getEmailId().equalsIgnoreCase(newFromEmail)){
									newEmailIdThere = true;
									alreadyPresentUserFromEmailId = aUserFromEmailId;
									break;
								}
							}
						}
						
					}
					
					
					if(newEmailIdThere == false){
						return alreadyPresentUserFromEmailId;
					}else{
						return null;
					}
					
					
			} catch (Exception e) {
				regEmailPopupId.close();
				e.printStackTrace();
			}
			
			return null;
		}// registerNewFromEmail()
		
		private void doSomeProcessIfEmailIdIsThere(Textbox cFromEmailTb,String flagStr, String confirmationURL){
			 try {
					int confirm = Messagebox.show("The given email address is pending for approval ." +
							" Do you want to resend the verification?","Send Verification ?",
						 		Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
					if(confirm != 1) {
						regEmailPopupId.close();
						cFromEmailTb.setValue("");
						return ;
					}
				} catch (Exception e) {
					logger.error("Exception ::", e);
				}
			 
			 addVerificationMailToQueue(flagStr, confirmationURL, cFromEmailTb.getValue());
		}
		
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
				//regEmailPopupId.close();
				MessageUtil.clearMessage();
				if(uploadRgId.getSelectedIndex() == 1){
					Messagebox.show("We have sent you a confirmation email to "+newFromEmail+".\n " +
							"Follow the instructions in the email and this new "+flagStr+" will be verified.", "Register email", Messagebox.OK, Messagebox.INFORMATION);
				}
				/*Messagebox.show("We have sent you a confirmation email to "+newFromEmail+".\n " +
						"Follow the instructions in the email and this new "+flagStr+" will be verified.", "Register email", Messagebox.OK, Messagebox.INFORMATION);*/
				cEmailTb.setValue("");
			} catch (WrongValueException e) {
				logger.error("Exception ::", e);
			}catch (Exception e) {

		//	logger.error("** Exception while sending verification",e);
			
			
			}
			
			
		}
		
		/*public void onSelect$posLocCb(){
			logger.info("insde select of POS combo box");
			if(posLocCb.getValue().trim().length() == 0){
				return;
			}
			//defaultSettings4FromAndReply2Emails(posLocCb.getValue().trim());
			
		}*/
		private void defaultSettings4FromAndReply2Emails(Long storeId){
			
			//String homeStoreId = posLocCb.getValue().trim();
			logger.info("StoreId==========="+storeId);
			List<UserFromEmailId> userFromAndReplyToEmailIdList = userFromEmailIdDao.getFromAndReplyToEmailsOfStoreByUserId(currentUser.getUserId());
			logger.info("userFromAndReplyToEmailIdList List size : " + userFromAndReplyToEmailIdList.size());
			
			
			List<String> listOfEmailIds = new ArrayList<String>();
			
			for(UserFromEmailId anUserFromEmailId: userFromAndReplyToEmailIdList){
				if(anUserFromEmailId.getEmailId() != null){
					
					if( !listOfEmailIds.contains(anUserFromEmailId.getEmailId())){
						listOfEmailIds.add(anUserFromEmailId.getEmailId());
					}
				}
			}
			Components.removeAllChildren(cFromEmailCb);
			Components.removeAllChildren(cReplyToEmailCb);

			
			if (listOfEmailIds.size() > 0) {
				for (Object obj : listOfEmailIds) {
					String anEmailId = (String) obj;
					cFromEmailCb.appendItem(anEmailId);
					cReplyToEmailCb.appendItem(anEmailId);

				}
			}
		
			if (!(cFromEmailCb.getItemCount() > 0)) {
				cFromEmailCb.appendItem(EMPTY_STRING);

			}
			
			if (!(cReplyToEmailCb.getItemCount() > 0)) {
				cReplyToEmailCb.appendItem(EMPTY_STRING);

			}
			cFromEmailCb.setSelectedIndex(0);
			cReplyToEmailCb.setSelectedIndex(0);
            
			
			if(storeId == null) {
				cFromEmailCb.appendItem(EMPTY_STRING);
				cReplyToEmailCb.appendItem(EMPTY_STRING);
				cFromEmailCb.setSelectedIndex(cFromEmailCb.getItemCount() - 1); 
				cReplyToEmailCb.setSelectedIndex(cReplyToEmailCb.getItemCount() - 1); 
				return;
			}
			
            OrganizationStores organizationStores = organizationStoresDao.getStoreByStoreId(storeId);
			if(organizationStores != null){
				
				
				if(organizationStores.getFromEmailId() != null){
					
					for (int index = 0; index < cFromEmailCb.getItemCount(); index++) {
						
						// logger.debug(cFromEmailCb.getItemAtIndex(index).getLabel() +
						// " == " + fromEmailId);
						if (cFromEmailCb.getItemAtIndex(index).getLabel().equals(organizationStores.getFromEmailId())) {
							cFromEmailCb.setSelectedIndex(index); 
							break; // break added on date 13th aug 2015
						}
					}
				}else{
					if(cFromEmailCb.getItemCount() > 0) {
						cFromEmailCb.appendItem(EMPTY_STRING);
						cFromEmailCb.setSelectedIndex(cFromEmailCb.getItemCount() - 1); 
					}
				}
				
				
				if(organizationStores.getReplyToEmailId() != null){
					
					for (int index = 0; index < cReplyToEmailCb.getItemCount(); index++) {
						
						// logger.debug(cFromEmailCb.getItemAtIndex(index).getLabel() +
						// " == " + fromEmailId);
						if (cReplyToEmailCb.getItemAtIndex(index).getLabel().equals(organizationStores.getReplyToEmailId())) {
							cReplyToEmailCb.setSelectedIndex(index);
							break; // break added on date 13th aug 2015
						}
					}
				}else{
					if(cReplyToEmailCb.getItemCount() > 0) {
						cReplyToEmailCb.appendItem(EMPTY_STRING);
						cReplyToEmailCb.setSelectedIndex(cReplyToEmailCb.getItemCount() - 1); 
					}
				
					
				}
				
				
				
			}
		}
		
		private void clearAllFromToEmailAddresses4NewStore(){
			Components.removeAllChildren(cFromEmailCb);
			Components.removeAllChildren(cReplyToEmailCb);
			cFromEmailCb.setText(EMPTY_STRING);
			cReplyToEmailCb.setText(EMPTY_STRING);
		}
		
		private UserFromEmailId setFromEmailID(String newEmailId){
			UserFromEmailId userFromEmailId = new UserFromEmailId();
			userFromEmailId.setUsers(currentUser);
			userFromEmailId.setEmailId(newEmailId);
			userFromEmailId.setStatus(0);
			
			return userFromEmailId;
		}
		
		//for Export button
		 public void onClick$exportBtnId() {
				createWindow();
				custExport.setVisible(true);
				custExport.doHighlighted();
				
			
			}
			 public void onClick$selectAllAnchr$custExport() {
				 anchorEvent(true);
			 }

			 public void onClick$clearAllAnchr$custExport() {
				anchorEvent(false);
			 }

			 public void anchorEvent(boolean flag) {
				List<Component> chkList = custExport$chkDivId.getChildren();
				 Checkbox tempChk = null;
				 for (int i = 0; i < chkList.size(); i++) {
					 if(!(chkList.get(i) instanceof Checkbox)) continue;

					 tempChk = (Checkbox)chkList.get(i);
					 tempChk.setChecked(flag);

				 } // for
			 }

			 public void createWindow()	{

					try {
						Components.removeAllChildren(custExport$chkDivId);
						Checkbox tempChk2 = new Checkbox("Org Unit");
						tempChk2.setSclass("custCheck");
						tempChk2.setParent(custExport$chkDivId);
						tempChk2.setChecked(true);
						
						
						 tempChk2 = new Checkbox("Subsidiary Id");
						tempChk2.setSclass("custCheck");
						tempChk2.setParent(custExport$chkDivId);
						tempChk2.setChecked(true);
						
						tempChk2 = new Checkbox("Subsidiary Name");
						tempChk2.setSclass("custCheck");
						tempChk2.setParent(custExport$chkDivId);
						tempChk2.setChecked(true);
	                    
						tempChk2 = new Checkbox("Store Name");
						tempChk2.setSclass("custCheck");
						tempChk2.setParent(custExport$chkDivId);
						tempChk2.setChecked(true);
	                    
					    tempChk2 = new Checkbox("POS Store ID");
						tempChk2.setSclass("custCheck");
						tempChk2.setParent(custExport$chkDivId);
						tempChk2.setChecked(true);


						tempChk2 = new Checkbox("Manager");
						tempChk2.setSclass("custCheck");
						tempChk2.setParent(custExport$chkDivId);
						tempChk2.setChecked(true);

						tempChk2 = new Checkbox("Email Id");
						tempChk2.setSclass("custCheck");
						tempChk2.setParent(custExport$chkDivId);
						tempChk2.setChecked(true);

						tempChk2 = new Checkbox("Website");
						tempChk2.setSclass("custCheck");
						tempChk2.setParent(custExport$chkDivId);
						tempChk2.setChecked(true);

						tempChk2 = new Checkbox("Phone");
						tempChk2.setSclass("custCheck");
						tempChk2.setParent(custExport$chkDivId);
						tempChk2.setChecked(true);
						
						tempChk2 = new Checkbox("Street");
						tempChk2.setSclass("custCheck");
						tempChk2.setParent(custExport$chkDivId);
						tempChk2.setChecked(true);
						
						tempChk2 = new Checkbox("City");
						tempChk2.setSclass("custCheck");
						tempChk2.setParent(custExport$chkDivId);
						tempChk2.setChecked(true);

						tempChk2 = new Checkbox("State");
						tempChk2.setSclass("custCheck");
						tempChk2.setParent(custExport$chkDivId);
						tempChk2.setChecked(true);
						
						tempChk2 = new Checkbox("Country");
						tempChk2.setSclass("custCheck");
						tempChk2.setParent(custExport$chkDivId);
						tempChk2.setChecked(true);

						tempChk2 = new Checkbox("Zip Code");
						tempChk2.setSclass("custCheck");
						tempChk2.setParent(custExport$chkDivId);
						tempChk2.setChecked(true);
						
						tempChk2 = new Checkbox("Brand");
						tempChk2.setSclass("custCheck");
						tempChk2.setParent(custExport$chkDivId);
						tempChk2.setChecked(true);
						
						tempChk2 = new Checkbox("From Email Address");
						tempChk2.setSclass("custCheck");
						tempChk2.setParent(custExport$chkDivId);
						tempChk2.setChecked(true);

					} catch (Exception e) {
						logger.error("Exception ::", e);
					}
				}
			 //To export data to file
			 public void onClick$selectFieldBtnId$custExport() {

				 custExport.setVisible(false);
				 List<Component> chkList = custExport$chkDivId.getChildren();

				 int indexes[]=new int[chkList.size()];
				 
				 boolean checked=false;

				 for(int i=0;i<chkList.size();i++) {
					 indexes[i]=-1;
				 } // for

				 Checkbox tempChk = null;

				 for (int i = 0; i < chkList.size(); i++) {
					 if(!(chkList.get(i) instanceof Checkbox)) continue;

					 tempChk = (Checkbox)chkList.get(i);

					 if(tempChk.isChecked()) {
						 indexes[i]=0;
						 checked=true;
					 }else{
							indexes[i]=-1;
						}

				 } // for


				 if( ((Checkbox)custExport$chkDivId.getLastChild()).isChecked()) {

					 checked=true;
				 }

				 if(checked) {

					 int confirm=Messagebox.show("Do you want to export with selected fields ?", "Confirm",Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
					 if(confirm==1){
						 try{
							 String type = exportCbId.getSelectedItem().getValue();
							 logger.info("FileType---- >"+type);
							 if(type.contains("csv"))
								 exportCSV((String)exportCbId.getSelectedItem().getValue(),indexes);
							 exportCSV(type.toString(),indexes);
							 if(type.contains("xls"))
									 exportExcel((String)exportCbId.getSelectedItem().getValue(),indexes);

						 }catch(Exception e){
							 logger.error("Exception caught :: ",e);
						 }
					 }
					 else{
						 custExport.setVisible(true);
					 }

				 }
				 else {

					 MessageUtil.setMessage("Please select atleast one field", "red");
					 custExport.setVisible(false);
				 }

			 }
			 
			 private void exportExcel(String value, int[] indexes){

				 logger.debug("-- just entered into exportEXCEL --");
				 String type = exportCbId.getSelectedItem().getValue();
				 logger.info("FileType---- >"+type);
				 StringBuffer sb = null;
				 String userName = GetUser.getUserName();
				 String usersParentDirectory = (String)PropertyUtil.getPropertyValue("usersParentDirectory");
				 String exportDir = usersParentDirectory + "/" + userName + "/Export/" ;
				 File downloadDir = new File(exportDir);
				 if(downloadDir.exists()){
					 try {
						 FileUtils.deleteDirectory(downloadDir);
						 logger.debug(downloadDir.getName() + " is deleted");
					 } catch (Exception e) {
						 logger.error("Exception ::" , e);

						 logger.debug(downloadDir.getName() + " is not deleted");
					 }
				 }
				 if(!downloadDir.exists()){
					 downloadDir.mkdirs();
				 }

					 String filePath = exportDir +  "Store_Reports_" +
							 MyCalendar.calendarToString(Calendar.getInstance(), MyCalendar.FORMAT_YEARTOSEC, clientTimeZone);
					 try {
						 filePath = filePath + "_StoreReports"+"."+type;
						 String orgId="All";
						 String sbsId="All";
						 String storeId = "All";
						 logger.debug("Download File path : " + filePath);
						 File file = new File(filePath);
						 logger.info("file---->"+file);
						 if(searchByOrgID.getSelectedIndex() != 0) {	
							 //orgId = ((OrganizationStores)searchByOrgID.getSelectedItem().getValue()).getUserOrganization().toString();
							UsersDomains userDomains = (UsersDomains) searchByOrgID.getSelectedItem().getValue();
							orgId = userDomains.getDomainId().toString();
						 }
						 if(searchBySbsID.getSelectedIndex() != 0) {	
							 sbsId = ((OrganizationStores)searchBySbsID.getSelectedItem().getValue()).getSubsidiaryId();	
						 }
						 if(searchByStoreID.getSelectedIndex() != 0) {	
							 storeId = searchByStoreID.getSelectedItem().getValue();	
						 }
						 String searchStr = (searchBoxId.getValue().trim().equals("Search store...") || 
								 searchBoxId.getValue().trim().equals("")) ? null : searchBoxId.getValue().trim() ;
						 int count = organizationStoresDao.findByOrganizationstoresCount(GetUser.getUserObj().getUserOrganization().getUserOrgId(),storeId,searchStr); 
						 if(count == 0) {
							 Messagebox.show("No store reports found.","Info", Messagebox.OK,Messagebox.INFORMATION);
							 return;
						 }
						 HSSFWorkbook hwb = new HSSFWorkbook();
						 HSSFSheet sheet = hwb.createSheet("Store Reports");
						 HSSFRow row = sheet.createRow((short) 0);
						 int columnId=0;
						 HSSFCell cell = null;
						 if(indexes[0]==0) {
						 cell = row.createCell(columnId++);
						 cell.setCellValue("Org Unit");
						 }
						 if(indexes[1]==0) {
						 cell = row.createCell(columnId++);
						 cell.setCellValue("Subsidiary Id");
						 }
						 if(indexes[2]==0) {
						 cell = row.createCell( columnId++);
						 cell.setCellValue("Subsidiary Name");
						 }
						 if(indexes[3]==0) {
						 cell = row.createCell( columnId++);
						 cell.setCellValue("Store Name");
						 }
						 if(indexes[4]==0) {
						 cell = row.createCell( columnId++);
						 cell.setCellValue("POS Store ID");
						 }
						 if(indexes[5]==0) {
						 cell = row.createCell( columnId++);
						 cell.setCellValue("Manager");
						 }
						 if(indexes[6]==0) {
						 cell = row.createCell( columnId++);
						 cell.setCellValue("Email Id");
						 }
						 if(indexes[7]==0) {
						 cell = row.createCell( columnId++);
						 cell.setCellValue("Website");
						 }
						 if(indexes[8]==0) {
						 cell = row.createCell( columnId++);
						 cell.setCellValue("Phone");
						 }
						 if(indexes[9]==0) {
						 cell = row.createCell( columnId++);
						 cell.setCellValue("Street");
						 }
						 if(indexes[10]==0) {
						 cell = row.createCell( columnId++);
						 cell.setCellValue("City");
						 }
						 if(indexes[11]==0) {
						 cell = row.createCell( columnId++);
						 cell.setCellValue("State");
						 }
						 if(indexes[12]==0) {
						 cell = row.createCell( columnId++);
						 cell.setCellValue("Country");
						 }
						 if(indexes[13]==0) {
						 cell = row.createCell(columnId++);
						 cell.setCellValue("Zip code");
						 }
						 if(indexes[14]==0) {
						 cell = row.createCell( columnId++);
						 cell.setCellValue("Brand");
						 }
						 if(indexes[15]==0) {
							 cell = row.createCell( columnId++);
							 cell.setCellValue("From Email Address");
							 }

						 int size = 1000;
						 List<OrganizationStores> storeList = null;
						 for (int i = 0; i < count; i+=size) {
							 sb = new StringBuffer();
							 storeList = organizationStoresDao.findByOrganization(GetUser.getUserObj().getUserOrganization().getUserOrgId(),orgId,sbsId,storeId,searchStr,0,count);
							 if(storeList != null) {
								 if(storeList.size()>0){
									 int rowId=1;
									 for (OrganizationStores store : storeList) {
										 row = sheet.createRow((short) rowId++);
										 columnId=0;
										 cell=null;
										 if(indexes[0]==0) {
										 cell = row.createCell( columnId++);
										 cell.setCellValue(store.getDomainId() != null ? orgUnit.get(store.getDomainId()) : "");
										 logger.info(""+cell.getColumnIndex());
										 }
										 if(indexes[1]==0) {
										 cell = row.createCell( columnId++);
										 cell.setCellValue(store.getSubsidiaryId()) ;
										 logger.info(""+cell.getColumnIndex());
										 }
										 if(indexes[2]==0) {
										 cell = row.createCell( columnId++);
										 cell.setCellValue(store.getSubsidiaryName()) ;
										 logger.info(""+cell.getColumnIndex());
										 }
										 if(indexes[3]==0) {
										 cell = row.createCell(columnId++);
										 cell.setCellValue(store.getStoreName()) ;
										 logger.info(""+cell.getColumnIndex());
										 }
										 if(indexes[4]==0) {
										 cell = row.createCell( columnId++);
										 cell.setCellValue(store.getHomeStoreId()) ;
										 logger.info(""+cell.getColumnIndex());
										 }
										 if(indexes[5]==0) {
										 cell = row.createCell( columnId++);
										 cell.setCellValue(store.getStoreManagerName()) ;
										 logger.info(""+cell.getColumnIndex());
										 }
										 if(indexes[6]==0) {
										 cell = row.createCell( columnId++);
										 cell.setCellValue(store.getEmailId()) ;
										 logger.info(""+cell.getColumnIndex());
										 }
										 if(indexes[7]==0) {
										 cell = row.createCell( columnId++);
										 cell.setCellValue(store.getWebsite()) ;
										 logger.info(""+cell.getColumnIndex());
										 }
										 if(indexes[8]==0) {
										 cell = row.createCell( columnId++);
										 cell.setCellValue(store.getAddress().getPhone()) ;
										 logger.info(""+cell.getColumnIndex());
										 }
										 if(indexes[9]==0) {
										 cell = row.createCell( columnId++);
										 cell.setCellValue(store.getAddress().getAddressOne()) ;
										 logger.info(""+cell.getColumnIndex());
										 }
										 if(indexes[10]==0) {
										 cell = row.createCell( columnId++);
										 cell.setCellValue(store.getAddress().getCity()) ;
										 logger.info(""+cell.getColumnIndex());
										 }
										 if(indexes[11]==0) {
										 cell = row.createCell( columnId++);
										 cell.setCellValue(store.getAddress().getState()) ;
										 logger.info(""+cell.getColumnIndex());
										 }
										 if(indexes[12]==0) {
										 cell = row.createCell( columnId++);
										 cell.setCellValue(store.getAddress().getCountry()) ;
										 logger.info(""+cell.getColumnIndex());
										 }
										 if(indexes[13]==0) {
										 cell = row.createCell( columnId++);
										 cell.setCellValue(store.getAddress().getPin()) ;
										 logger.info(""+cell.getColumnIndex());
										 }
										 if(indexes[14]==0) {
										 cell = row.createCell( columnId++);
										 cell.setCellValue(store.getStoreBrand()) ;
										 logger.info(""+cell.getColumnIndex());
										 }
										 if(indexes[15]==0) {
											 cell = row.createCell( columnId++);
											 cell.setCellValue(store.getFromEmailId()) ;
											 logger.info(""+cell.getColumnIndex());
											 }
									 }

								 }
							 }


						 }

						 FileOutputStream fileOut = new FileOutputStream(filePath);
						 hwb.write(fileOut);
						 fileOut.close();
						 storeList = null;
						 sb = null;
						 Filedownload.save(file, "application/vnd.ms-excel");						 
						 //System.gc();
					 } catch (IOException e) {
						 logger.error("Exception ::",e);

					 }
					 logger.debug("-- exit --");
				 
			 }
			 
			 private void exportCSV(String value, int[] indexes) {
				 	logger.debug("-- just entered into exportCSV --");
					String type = exportCbId.getSelectedItem().getValue();
					logger.info("FileType---- >"+type);
					StringBuffer sb = null;
					String userName = GetUser.getUserName();
					String usersParentDirectory = (String)PropertyUtil.getPropertyValue("usersParentDirectory");
					String exportDir = usersParentDirectory + "/" + userName + "/Export/" ;
					File downloadDir = new File(exportDir);
					if(downloadDir.exists()){
						try {
							FileUtils.deleteDirectory(downloadDir);
							logger.debug(downloadDir.getName() + " is deleted");
						} catch (Exception e) {
							logger.error("Exception ::" , e);
							
							logger.debug(downloadDir.getName() + " is not deleted");
						}
					}
					if(!downloadDir.exists()){
						downloadDir.mkdirs();
					}
					
						
						String filePath = exportDir +  "Store_Reports_" +
							MyCalendar.calendarToString(Calendar.getInstance(), MyCalendar.FORMAT_YEARTOSEC, clientTimeZone);
							try {
									//filePath = filePath + "_StoreReports.csv";
									filePath = filePath + "_StoreReports"+"."+type;
									String orgId = "All";
									String sbsId = "All";
									String storeId = "All";
									logger.debug("Download File path : " + filePath);
									File file = new File(filePath);
									logger.info("file---->"+file);
									BufferedWriter bw = new BufferedWriter(new FileWriter(filePath));
									if(searchByOrgID.getSelectedIndex() != 0) {	
										//orgId = ((OrganizationStores)searchByOrgID.getSelectedItem().getValue()).getUserOrganization().toString();
										UsersDomains userDomains = (UsersDomains) searchByOrgID.getSelectedItem().getValue();
										orgId = userDomains.getDomainId().toString();
									}
									if(searchBySbsID.getSelectedIndex() != 0) {	
										sbsId = ((OrganizationStores)searchBySbsID.getSelectedItem().getValue()).getSubsidiaryId();	
									}
									if(searchByStoreID.getSelectedIndex() != 0) {	
										storeId = searchByStoreID.getSelectedItem().getValue();	
									}
									String searchStr = (searchBoxId.getValue().trim().equals("Search store...") || 
											searchBoxId.getValue().trim().equals("")) ? null : searchBoxId.getValue().trim() ;
									int count = organizationStoresDao.findByOrganizationstoresCount(GetUser.getUserObj().getUserOrganization().getUserOrgId(),storeId,searchStr); 
									if(count == 0) {
										Messagebox.show("No store reports found.","Info", Messagebox.OK,Messagebox.INFORMATION);
										return;
									}
									 String udfFldsLabel= "";
									 if(indexes[0]==0) {
										 udfFldsLabel += "\""+"Org Unit"+"\""+",";
									 }
									 if(indexes[1]==0) {
										 udfFldsLabel += "\""+"Subsidiary Id"+"\""+",";
									 }
									 if(indexes[2]==0) {
										 udfFldsLabel += "\""+"Subsidiary Name"+"\""+",";
									 }
									
									 if(indexes[3]==0) {
										 udfFldsLabel += "\""+"Store Name"+"\""+",";
									 }
									 if(indexes[4]==0) {
										 udfFldsLabel += "\""+"POS Store ID"+"\""+",";
									 }
									 if(indexes[5]==0) {
										 udfFldsLabel += "\""+"Manager"+"\""+",";
									 }
									 if(indexes[6]==0) {
										 udfFldsLabel += "\""+"Email Id"+"\""+",";
									 }	
									 if(indexes[7]==0) {
										 udfFldsLabel += "\""+"Website"+"\""+",";
									 }	
									 if(indexes[8]==0) {

										 udfFldsLabel += "\""+"Phone"+"\""+",";
									 }
									 if(indexes[9]==0) {

										 udfFldsLabel += "\""+"Street"+"\""+",";
									 }
									 if(indexes[10]==0) {

										 udfFldsLabel += "\""+"City"+"\""+",";
									 }
									 if(indexes[11]==0) {

										 udfFldsLabel += "\""+"State"+"\""+",";
									 }
									 if(indexes[12]==0) {

										 udfFldsLabel += "\""+"Country"+"\""+",";
									 }
									 if(indexes[13]==0) {

										 udfFldsLabel += "\""+"Zip code"+"\""+",";
									 }
									 if(indexes[14]==0) {

										 udfFldsLabel += "\""+"Brand"+"\""+",";
									 }
									 if(indexes[15]==0) {

										 udfFldsLabel += "\""+"From Email Address"+"\""+",";
									 }
									 sb = new StringBuffer();
									 sb.append(udfFldsLabel);
									 sb.append("\r\n");
									 bw.write(sb.toString());
									 //System.gc();
									
									int size = 1000;
									List<OrganizationStores> storeList = null;
									for (int i = 0; i < count; i+=size) {
										sb = new StringBuffer();
										storeList = organizationStoresDao.findByOrganization(GetUser.getUserObj().getUserOrganization().getUserOrgId(),orgId,sbsId,storeId,searchStr,0,count);
										if(storeList != null) {
										if(storeList.size()>0){
											for (OrganizationStores store : storeList) {
												if(indexes[0]==0) {
													sb.append("\"");sb.append(store.getDomainId() != null ? orgUnit.get(store.getDomainId()) : ""); sb.append("\",");
												}
												if(indexes[1]==0) {
													sb.append("\"");sb.append(store.getSubsidiaryId()); sb.append("\",");
												}
												if(indexes[2]==0) {
													sb.append("\"");sb.append(store.getSubsidiaryName()); sb.append("\",");
												}
												if(indexes[3]==0) {
													sb.append("\"");sb.append(store.getStoreName()); sb.append("\",");
												}
												if(indexes[4]==0) {
													sb.append("\"");sb.append(store.getHomeStoreId()); sb.append("\",");
												}
												if(indexes[5]==0) {
													sb.append("\"");sb.append(store.getStoreManagerName()); sb.append("\",");
												}
												if(indexes[6]==0) {
													sb.append("\"");sb.append(store.getEmailId()); sb.append("\",");
												}
												if(indexes[7]==0) {
													sb.append("\"");sb.append(store.getWebsite()); sb.append("\",");
												}
												if(indexes[8]==0) {
												sb.append("\"");sb.append(store.getAddress().getPhone()); sb.append("\",");
												}
												if(indexes[9]==0) {
												sb.append("\"");sb.append(store.getAddress().getAddressOne()); sb.append("\",");
												}
												if(indexes[10]==0) {
												sb.append("\"");sb.append(store.getAddress().getCity()); sb.append("\",");
												}
												if(indexes[11]==0) {
													sb.append("\"");sb.append(store.getAddress().getState()); sb.append("\",");
												}
												if(indexes[12]==0) {
													sb.append("\"");sb.append(store.getAddress().getCountry()); sb.append("\",");
												}
												if(indexes[13]==0) {
													sb.append("\"");sb.append(store.getAddress().getPin()); sb.append("\",");
												}
												if(indexes[14]==0) {
													sb.append("\"");sb.append(store.getStoreBrand()); sb.append("\",");
												}
												if(indexes[15]==0) {
													sb.append("\"");sb.append(store.getFromEmailId()); sb.append("\",");
												}
												sb.append("\r\n");
											}
											
										}
										}
										
										bw.write(sb.toString());
										storeList = null;
										sb = null;
										//System.gc();
									}
									bw.flush();
									bw.close();
									Filedownload.save(file, "text/plain");
									} catch (IOException e) {
									logger.error("Exception ::",e);
									
								}
								logger.debug("-- exit --");
				
				}
		
			 public void resetSudsidiaryLbId(){
					
					int sizeSubSiID = subsLBId.getItemCount();
							sizeSubSiID = sizeSubSiID-1;
							while(sizeSubSiID>0){
								subsLBId.removeItemAt(1);
						sizeSubSiID--;
					}
					return;
				}
				
			 public void onClick$saveSubsBtnId$createSubsWinId() {
				 logger.info("  ==========inside onclick window save button========");
					String subId = createSubsWinId$subsID.getValue().trim();
						if(subId == null || subId.trim().length() == 0) {
							createSubsWinId$msgLblId.setValue("Please provide subsudiary Id.");
						return;
					}
						String subName= createSubsWinId$subName.getValue().trim();
						if(subName == null || subName.trim().length() == 0) {
							createSubsWinId$msgLblId.setValue("Please provide subsudiary Name.");
						return;
						}
						UsersDomains domain=orgUnitId.getSelectedItem().getValue();
						if(domain==null){
							return;
						}
						List<OrganizationStores> subList=orgStoreDao.findStoreBySubsidaryId(domain.getDomainId(), subId);
						if(subList.size()>0) {
							createSubsWinId$msgLblId.setValue("Subsidiary Id is already exists ,please provide another Id.");
							return;
						}
						
						List<OrganizationStores> subList11=orgStoreDao.findStoreBySubsidaryName(domain.getDomainId(), subName);
						if(subList11.size()>0){
							createSubsWinId$msgLblId.setValue("Subsidiary Name is already exists ,please provide another Name.");
							return;	
						}
					
					try {
						
						if (Messagebox.show("Are you sure you want to create new subsidiary ?", "Prompt", 
						  		  Messagebox.YES|Messagebox.NO, Messagebox.QUESTION) == Messagebox.NO) {
							
		                  OrganizationStores orgNewStore=new OrganizationStores();
		                  orgNewStore.setDomainId(domain.getDomainId());
		                  orgNewStore.setSubsidiaryId(subId);
		                  orgNewStore.setSubsidiaryName(subName);
		                  sbLocName.setText(subName);
		                  sbLocName.setReadonly(true);
		                  session.setAttribute("NewSubStore", orgNewStore);	
						}
							createSubsWinId.setVisible(false);
						      return;
						
					} catch (Exception e) {
						// TODO Auto-generated catch block
						logger.error("Exception ::" , e);
					}
					
					
				}
				
				
				public void onClick$cancelSubsBtnId$createSubsWinId() {
					
					createSubsWinId.setVisible(false);
					
				}
		} //OrganizationStoresController
		
		
		
		
		
		
		

