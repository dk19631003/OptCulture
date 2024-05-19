package org.mq.marketer.campaign.controller.useradmin;

import java.sql.Date;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeSet;

import javax.sound.midi.MidiDevice.Info;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.DigitalReceiptUserSettings;
import org.mq.marketer.campaign.beans.OrganizationStores;
import org.mq.marketer.campaign.beans.OrganizationZone;
import org.mq.marketer.campaign.beans.SecRoles;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.beans.UsersDomains;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.DigitalReceiptUserSettingsDao;
import org.mq.marketer.campaign.dao.DigitalReceiptUserSettingsDaoForDML;
import org.mq.marketer.campaign.dao.OrganizationStoresDao;
import org.mq.marketer.campaign.dao.OrganizationStoresDaoForDML;
import org.mq.marketer.campaign.dao.SecRolesDao;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.dao.UsersDomainsDao;
import org.mq.marketer.campaign.dao.ZoneDao;
import org.mq.marketer.campaign.dao.ZoneDaoForDML;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PageListEnum;
import org.mq.marketer.campaign.general.PageUtil;
import org.mq.marketer.campaign.general.Utility;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.SelectEvent;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Bandbox;
import org.zkoss.zul.Bandpopup;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Column;
import org.zkoss.zul.Columns;
import org.zkoss.zul.Detail;
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Menupopup;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Treecell;
import org.zkoss.zul.Treechildren;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.Treerow;
import org.zkoss.zul.Window;
import org.zkoss.zul.event.PagingEvent;

public class OrganizationZoneController extends GenericForwardComposer implements EventListener {
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);

	private Tabbox zoneTabBoxId, zoneTabId, addStoreLocTabId;
	// private Label zoneTabId;
	private Div zoneDivId, divSubAndStore;
	private Grid zoneGridId;
	private Rows zoneRowsId;
	private Textbox zoneNameTb, descriptionTb;
	private Listbox storeListHead, orgunitzoneTb, itemListboxId, sudsidiaryListBox, sudsidiaryId, SelSubAndStoreId,
			orgunitTb, zoneLbId,reportsPerPageLBId;
	private Paging zoneReportPagingId;
	private Button createZoneBtnId,saveBtnId;
	private Window custWin;
	private TimeZone clientTimeZone;
	private Users currentUser;
	private Bandbox zoneBandBoxId;
	private Bandpopup zoneBandpopupId;
	private OrganizationZone zone;
	private ZoneDaoForDML ZoneDaoForDML;
	private ZoneDao zoneDao;
	private Session sessionScope;
	private OrganizationStoresDao organizationStoresDao;
	private OrganizationStoresDaoForDML organizationStoresDaoforDml;
	private Listheader subheaderId, zoneName, createdDate, groupOf, edit;
	private UsersDomainsDao userDomainDao;
	private Map<String, String> SBSMap = new HashMap<String, String>();
	private Map<String, String> storeNameMap = new HashMap<String, String>();
	private Map<String, String> subAndStoreMap = new HashMap<String, String>();
	private Map<String, String> selSubMap = new HashMap<String, String>();
	private Map<String, String> selectedSubMap =new HashMap<String, String>();
	private Map<String, String> editedMap =new HashMap<String, String>();
	
	private Treechildren subchild, storechild;
	private Treeitem subitem, storeitem;
	private Treerow storerow, subrow;
	private Treecell storecell, subcell;
	private Rows subsandStoreRowId;
	private Label zoneNamelabel;
	//private Listitem selecteditem;

	private final String SNO = "SNO";
	private final String SBS = "SBS";
	static final String drAdmin = "Admin";
	static final String drPower = "All";
	//private String currentUserRole;
	private UsersDao usersDao;
	//Set<OrganizationStores> subSet = new HashSet();
	//Set<OrganizationStores> storeSet = new HashSet();
	private Map<String, Rows> rowsMap = new HashMap<String, Rows>();
	private List<Checkbox> cbListSbs;
	private List<Checkbox> cbListStore;
	private Map<String ,List> sbsMap=new HashMap<>();
	private DigitalReceiptUserSettingsDao digitalReceiptUserSettingsDao;
	private DigitalReceiptUserSettingsDaoForDML digitalReceiptUserSettingsDaoForDML;
	private UsersDomainsDao domainDao;
	//private MyListener listener = new MyListener();
	public OrganizationZoneController() {
		session = Sessions.getCurrent();
		sessionScope = Sessions.getCurrent();
		currentUser = GetUser.getUserObj();
		String style = "font-weight:bold;font-size:15px;color:#313031;font-family:Arial,Helvetica,sans-serif;align:left";
		PageUtil.setHeader("Manage Zones ", "", style, true);
		clientTimeZone = (TimeZone) session.getAttribute("clientTimeZone");
		zoneDao = (ZoneDao) SpringUtil.getBean("zoneDao");
		ZoneDaoForDML = (ZoneDaoForDML) SpringUtil.getBean("zoneDaoForDML");
		this.organizationStoresDao = (OrganizationStoresDao) SpringUtil.getBean("organizationStoresDao");
		userDomainDao = (UsersDomainsDao) SpringUtil.getBean("usersDomainsDao");
		sessionScope.removeAttribute("editType");
		usersDao = (UsersDao) SpringUtil.getBean("usersDao");
		digitalReceiptUserSettingsDaoForDML= (DigitalReceiptUserSettingsDaoForDML) SpringUtil.getBean("digitalReceiptUserSettingsDaoForDML");
		digitalReceiptUserSettingsDao= (DigitalReceiptUserSettingsDao) SpringUtil.getBean("digitalReceiptUserSettingsDao");
		this.domainDao = (UsersDomainsDao) SpringUtil.getBean("usersDomainsDao");
	}

	public void doAfterCompose(Component comp) throws Exception {

		try {
			super.doAfterCompose(comp);
			zoneNamelabel.setValue("");
			/*if(drAdmin.endsWith(getEditorRoleSet())){
				currentUserRole=drAdmin;
			}else if(drPower.endsWith(getEditorRoleSet())){
				currentUserRole=drPower;
			}*/
			saveBtnId.setLabel("Create Zone");
		  session.removeAttribute("OrganizationZone");
			setorgunitzoneTb();
			setorgunitTb();
		} catch (Exception exception) {
			// TODO Auto-generated catch block
			exception.printStackTrace();
		}
	}
	private void onLoad() {
	  Components.removeAllChildren(subsandStoreRowId);
		cbListSbs = new ArrayList<Checkbox>();
		cbListStore=new ArrayList<Checkbox>();
		Long domainId = null;
		if(orgunitzoneTb.getSelectedIndex()>0){
		 domainId =  ((Long)orgunitzoneTb.getSelectedItem().getValue()).longValue();
		}
		  logger.info(" domain Id"+domainId);
		  if( domainId ==null)
			  return;
			List<OrganizationStores> subIdList = organizationStoresDao.findSubsidaryByOrgUnitId(domainId);
			if (subIdList == null) {
				return;
			}
			for (OrganizationStores  subs : subIdList) {
				List<OrganizationStores> storeList=organizationStoresDao.findStoreBySubsidaryId(domainId, subs.getSubsidiaryId());
				logger.info("subsidiary  "+subs.getSubsidiaryId()+"   Name  "+subs.getSubsidiaryName());
				if(storeList==null)continue;
				Row row = new Row();
				//row.setStyle("background-color:#cccccc;hover-color:#cccccc");
				row.setValue(subs.getSubsidiaryId());
				Checkbox chkbox = new Checkbox();
				chkbox.setLabel(subs.getSubsidiaryName()+"    ("+subs.getSubsidiaryId()+")");
				chkbox.setValue(subs.getSubsidiaryId());
				chkbox.addEventListener("onCheck", this);
				Label label=new Label(subs.getSubsidiaryName());
				chkbox.setParent(row);
				//label.setParent(row);
				Detail detail=new Detail();
				Grid grid = new Grid();
				grid.setWidth("300px");
				Columns cols = new Columns();
				Column column = new Column("Store(s) under "+(subs.getSubsidiaryName()));
				column.setStyle("background-color:#505050");
				column.setParent(cols);
			   // column = new Column("Store Name");
				//column.setParent(cols);
				cols.setParent(grid);
				grid.setParent(detail);
				detail.setParent(row);
				Rows tempRows = new Rows();
				for(OrganizationStores stores:storeList){
					Row tempRow=new Row();
					Checkbox tempchkbox = new Checkbox();
					tempchkbox.setLabel(""+(stores.getStoreName()!=null ? stores.getStoreName(): "")+"   ("+stores.getHomeStoreId()+")");
					tempchkbox.setValue(stores.getStoreId());
					Label templabel=new Label(""+(stores.getStoreName()!=null ?stores.getStoreName(): ""));
					tempchkbox.setParent(tempRow);
					//templabel.setParent(tempRow);
					tempRow.setParent(tempRows);
					cbListStore.add(tempchkbox);
				}
				sbsMap.put(subs.getSubsidiaryId(), cbListStore);
				cbListSbs.add(chkbox);
				rowsMap.put(subs.getSubsidiaryId(), tempRows);
				tempRows.setParent(grid);	
				row.setParent(subsandStoreRowId);
			}
							
	}
	public void onClick$saveBtnId(){
		try {
			zoneNamelabel.setValue("");
		zone =(OrganizationZone) session.getAttribute("OrganizationZone");
		if(zone == null && !validateZoneName())
		{
			
			return;
		}
		if(!listboxisEmpty())
		{
			return;
		}
		if(!validateSubAndStore())
		{
			return;
		}
		
		/*for (Checkbox chkbox : cbListSbs) {		
			if(chkbox.isChecked()){
			logger.info("Subsidiary id "+chkbox.getValue());
			if(sbsMap.containsKey(chkbox.getValue())){
				List<Checkbox> chkListStore=sbsMap.get(chkbox.getValue());
				logger.info("   Subsidiary Id  "+chkbox.getValue());
				for(Checkbox ck:chkListStore){
					logger.info("====StoreId "+ck.getValue());
				}
			 }
			}
			else{
				
			}*/
		Set<OrganizationStores> storeSet = new HashSet<>();
			for(Checkbox chk:cbListStore){
				if(chk.isChecked()){
					logger.info("Store   "+chk.getValue());			
					OrganizationStores store=	organizationStoresDao.findByStoreId(chk.getValue());
                    storeSet.add(store);
				}
				else continue;
			}
			int confirm ;
			if(saveBtnId.getLabel().equals("Create Zone"))
				confirm= Messagebox.show("Are you sure you want to  create a new zone?", "Prompt",
					Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
			
			else
				confirm= Messagebox.show("Are you sure you want to  change the settings?", "Prompt",
						Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
			if (confirm == Messagebox.OK) {
					if(zone == null) {
						logger.info("==============new zone creation===================");
							zone = new OrganizationZone();
							zone.setZoneName(zoneNameTb.getValue().trim());
							zone.setCreatedDate(Calendar.getInstance(clientTimeZone));
							zone.setCreatedBy(""+currentUser.getUserId());
					}
					zone.setDescription(descriptionTb.getValue().trim());
					zone.setDomainId((Long)orgunitzoneTb.getSelectedItem().getValue());
					zone.setModifiedDate(Calendar.getInstance(clientTimeZone));
					zone.setCreatedBy(""+currentUser.getUserId());
					if(storeSet != null)zone.setStores(storeSet);
						ZoneDaoForDML.saveOrUpdate(zone);

			if(saveBtnId.getLabel().equals("Create Zone")) 
					MessageUtil.setMessage("Zone created successfully.", "color:green;");
			else
				MessageUtil.setMessage("Zone setting changed successfully.", "color:green;");	 
				session.removeAttribute("OrganizationZone");
					zoneTabBoxId.setSelectedIndex(0);  
					onSelect$zoneTabBoxId();
			}
	      	}
			catch (Exception exception) {
					// TODO Auto-generated catch block
					logger.error("Exception ::", exception);
	             }
} 
	/*public void onClick$createZoneBtnId() {

		//1.check the selection itemcount>0 in lb
		zone =(OrganizationZone) session.getAttribute("OrganizationZone");
		if(zone == null && !validateZoneName())
		{
			
			return;
		}
		if(!listboxisEmpty())
		{
			return;
		}
		if(!validateSubAndStore())
		{
			return;
		}
		//logger.info("storeSet==========" + storeSet.toString());
		
		 
		try {
			String saveMsg = createZoneBtnId.getLabel().equals("Create") ? "create " : "updated";
			String askMsg = createZoneBtnId.getLabel().equals("Create") ? "create " : "update";
			int confirm = Messagebox.show("Are you sure you want to "+askMsg+" the zone?", "Prompt",
					Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
			if (confirm == Messagebox.OK) {

				try {
					if(zone == null) {
							zone = new OrganizationZone();
							zone.setZoneName(zoneNameTb.getValue().trim());
							zone.setCreatedDate(Calendar.getInstance(clientTimeZone));
							
					}
					zone.setDescription(descriptionTb.getValue().trim());
					// UsersDomains userDomains = (UsersDomains)
					// orgunitzoneTb.getSelectedItem().getValue();
					// UserDomain = ;
					// zone.setDomainId((userDomains.getDomainId()));
					zone.setDomainId((Long)orgunitzoneTb.getSelectedItem().getValue());
					zone.setModifiedDate(Calendar.getInstance(clientTimeZone));
					Set<OrganizationStores> stores = (Set<OrganizationStores>)SelSubAndStoreId.getAttribute("Store");
					
					
					if(stores != null)zone.setStores(stores);
					
					//logger.info("subset==========" + subSet.toString());
					//zone.setStores(storeSet);
					
					ZoneDaoForDML.saveOrUpdate(zone);
					
					
					 * if (zone == null) { saveMsg = "created";
					 * 
					 * ZoneDaoForDML.saveOrUpdate(zone); logger.info("-------" +
					 * saveMsg); } else if (zone != null) {
					 * 
					 * saveMsg = "updated";
					 * 
					 * ZoneDaoForDML.saveOrUpdate(zone); logger.info("-------" +
					 * saveMsg);
					 * 
					 * }
					 
					MessageUtil.setMessage("zone " + saveMsg + "  successfully.", "color:green;");
					zoneTabBoxId.setSelectedIndex(0);
					session.removeAttribute("OrganizationZone");
				} catch (Exception exception) {
					// TODO Auto-generated catch block
					logger.error("Exception ::", exception);
				}
			}
			// }
		} catch (Exception exception) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", exception);
		}

	}*/

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
	

	private Map<Long, String> orgUnit = new HashMap<Long, String>();

	private void setorgunitzoneTb() {

		int count = orgunitzoneTb.getItemCount();

		for (; count > 1; count--) {

			orgunitzoneTb.removeItemAt(count - 1);

		}
		// Components.removeAllChildren(orgunitzoneTb);
		try {
			List<UsersDomains> domainsList = userDomainDao.FindByOrgId(currentUser.getUserOrganization().getUserOrgId());
			//List<UsersDomains> domainsList = usersDao.getAllDomainsByUser(currentUser.getUserId());
			
			if (domainsList == null) {
				logger.debug("no organization list exist in the DB...");
				return;
			}
			// Listitem item = null;
			if (domainsList != null && domainsList.size() > 0) {

				for (UsersDomains orgunit : domainsList) {
					Listitem templist = new Listitem(orgunit.getDomainName(), orgunit.getDomainId());
					// templist.setValue(orgunit);
					templist.setParent(orgunitzoneTb);

				}
				orgunitzoneTb.setSelectedIndex(0);

			}
			
		} catch (Exception exception) {
			// TODO Auto-generated catch block
			logger.error("exception in set OrgUnit", exception);
		}

	}

	
/*	venkata 12_03_2019
 * private void setorgunitzoneTb() {

		int count = orgunitzoneTb.getItemCount();

		for (; count > 1; count--) {

			orgunitzoneTb.removeItemAt(count - 1);

		}
		// Components.removeAllChildren(orgunitzoneTb);
		try {
		//	List<UsersDomains> orgList = userDomaonDao.FindByOrgId(currentUser.getUserOrganization().getUserOrgId());
			List<UsersDomains> domainsList = usersDao.getAllDomainsByUser(currentUser.getUserId());
			
			if (domainsList == null) {
				logger.debug("no organization list exist in the DB...");
				return;
			}
			// Listitem item = null;
			if (domainsList != null && domainsList.size() > 0) {

				for (UsersDomains orgunit : domainsList) {
					Listitem templist = new Listitem(orgunit.getDomainName(), orgunit.getDomainId());
					// templist.setValue(orgunit);
					templist.setParent(orgunitzoneTb);

				}
				orgunitzoneTb.setSelectedIndex(0);

			}
			 if(currentUserRole.equalsIgnoreCase(drPower)){
				logger.info("*********************inside power user ");
				for (UsersDomains usersDomains : domainsList) {
					
					for(int i=0; i< orgunitzoneTb.getItemCount(); i++) {
						
						if(usersDomains.getDomainName().equals(orgunitzoneTb.getItemAtIndex(i).getLabel())) {
							orgunitzoneTb.setSelectedIndex(i);
							orgunitzoneTb.setDisabled(true);
							break;
						}
							
					}
				}
				onSelect$orgunitzoneTb();
			}
		} catch (Exception exception) {
			// TODO Auto-generated catch block
			logger.error("exception in set OrgUnit", exception);
		}

	}
*/
	
	private void setorgunitTb() {
        logger.info("======inside set Org unit==========");
		try {
			int count = orgunitTb.getItemCount();

			for (; count > 1; count--) {

				orgunitTb.removeItemAt(count - 1);

			}
			List<UsersDomains> orgList = userDomainDao.FindByOrgId(currentUser.getUserOrganization().getUserOrgId());
			//List<UsersDomains> orgList = usersDao.getAllDomainsByUser(currentUser.getUserId());
			
			if (orgList == null) {
				logger.debug("no organization list exist in the DB...");
				return;
			}
			// Listitem item = null;
			if (orgList != null && orgList.size() > 0) {

				for (UsersDomains orgunit : orgList) {
					Listitem templist = new Listitem(orgunit.getDomainName(), orgunit);
					// templist.setValue(orgunit);

					templist.setParent(orgunitTb);
				}

				orgunitTb.setSelectedIndex(0);
			   }
			  onSelect$orgunitTb();
		} catch (Exception exception) {
			// TODO Auto-generated catch block
			logger.error("exception in set OrgUnit", exception);
		}

	}

	
/*
 * Venkata 12_03_2019
 * 	private void setorgunitTb() {
           logger.info("======inside set Org unit==========");
		try {
			int count = orgunitTb.getItemCount();

			for (; count > 1; count--) {

				orgunitTb.removeItemAt(count - 1);

			}
			//List<UsersDomains> orgList = userDomaonDao.FindByOrgId(currentUser.getUserOrganization().getUserOrgId());
			List<UsersDomains> orgList = usersDao.getAllDomainsByUser(currentUser.getUserId());
			
			if (orgList == null) {
				logger.debug("no organization list exist in the DB...");
				return;
			}
			// Listitem item = null;
			if (orgList != null && orgList.size() > 0) {

				for (UsersDomains orgunit : orgList) {
					Listitem templist = new Listitem(orgunit.getDomainName(), orgunit);
					// templist.setValue(orgunit);

					templist.setParent(orgunitTb);
				}

				orgunitTb.setSelectedIndex(0);
			   }
			  if(currentUserRole.equalsIgnoreCase(drPower)){
				logger.info("                      inside power user        ");
				for (UsersDomains usersDomains : orgList) {
					
					for(int i=0; i< orgunitTb.getItemCount(); i++) {
						
						if(usersDomains.getDomainName().equals(orgunitTb.getItemAtIndex(i).getLabel())) {
							orgunitTb.setSelectedIndex(i);
							orgunitTb.setDisabled(true);
							break;
						}
							
					}
				}
			}
			  onSelect$orgunitTb();
		} catch (Exception exception) {
			// TODO Auto-generated catch block
			logger.error("exception in set OrgUnit", exception);
		}

	}
*/
	//Long UserDomain;

	public void onSelect$orgunitzoneTb() {
		onLoad();
	//	setsudsidiaryId();
	
	}
	public void onSelect$orgunitTb() {
		zoneReportPagingId.setTotalSize(0);
		if(orgunitTb.getSelectedIndex() == 0) {
			int itemCount = zoneLbId.getItemCount();

			for (; itemCount > 0; itemCount--) {
				zoneLbId.removeItemAt(itemCount-1);

			}
		}

		//getZoneReports();
		onSelect$reportsPerPageLBId();
	}

	/*
	 * public void setsudsidiaryId() {
	 * 
	 * Components.removeAllChildren(divSubAndStore);
	 * //Components.removeAllChildren(sudsidiaryId); Long domainId=UserDomain;
	 * logger.info("domainId     "+domainId); SBSMap = new HashMap<String,
	 * String>(); storeNameMap = new HashMap<String, String>();
	 * 
	 * try { List<OrganizationStores> subIdList =
	 * organizationStoresDao.findSubsidaryByOrgUnitId(domainId); if (subIdList
	 * != null && subIdList.isEmpty()){ return; } Listitem homeStoreItem = null;
	 * Listheader sudsidiaryListHeader=null; Listcell listcellId=null;
	 * 
	 * List<OrganizationStores> storeIdList = organizationStoresDao
	 * .findSubsidaryByOrgUnitId(currentUser.getUserOrganization().getUserOrgId(
	 * ), domainId.longValue()); int headerInt = 0; if (storeIdList != null &&
	 * subIdList!=null){ for (OrganizationStores subsidiary : subIdList) {
	 * logger.info("=============subs=============="+subIdList); Listbox lb =
	 * new Listbox(); lb.setCheckmark(true); lb.setMultiple(true); Listhead head
	 * = new Listhead(); sudsidiaryListHeader = new
	 * Listheader(subsidiary.getSubsidiaryId());
	 * sudsidiaryListHeader.setParent(head);
	 * SBSMap.put(domainId.longValue()+Constants.DELIMETER_DOUBLECOLON+
	 * subsidiary.getSubsidiaryId(), subsidiary.getSubsidiaryName()); for
	 * (OrganizationStores org : storeIdList) {
	 * if(subsidiary.getSubsidiaryId().equals(org.getSubsidiaryId())){
	 * 
	 * Listitem item = new Listitem();
	 * logger.info("===========storename=========== " + storeIdList);
	 * homeStoreItem = new Listitem(org.getStoreName(), org.getHomeStoreId());
	 * homeStoreItem.setParent(lb); //homeStoreItem.setParent(sudsidiaryId); //
	 * storeNameMap.put(domainId.longValue()+Constants.DELIMETER_DOUBLECOLON+org
	 * .getSubsidiaryId()+Constants.DELIMETER_DOUBLECOLON+org.getHomeStoreId(),
	 * org.getStoreName()); } else continue; } head.setParent(lb);
	 * lb.setParent(divSubAndStore); }
	 * 
	 * } logger.info("Received Subsidiaries based on orgunitId");
	 * 
	 * } catch (Exception exception) {
	 * logger.error("Exception raised while setting the sudsidiary and store",
	 * exception); }
	 * 
	 * }
	 */
	
	
	/*public void onSelect$treeId() {

		logger.debug("raised an event====");
		
		if(treeId.getSelectedCount() == 0 ) return;
		
		//Treeitem treeitem = treeId.getSelectedItem();
		
		
        Collection<Treeitem> itemSet=treeId.getItems();
        for (Treeitem treeitem : itemSet) {
        	
        	treeitem.
        	
        	
        }

		for (Treeitem treeitem : itemSet) {
			String type = (String)treeitem.getAttribute("type");
			
			if(type.equalsIgnoreCase("SBS")) {
					
					logger.info("treeitem.isSelected()"+treeitem.isSelected());
					List<Component> listChild= treeitem.getChildren();
		       	  //((Treeitem) listChild).setSelected(true);
		       	  logger.info("comp== "+treeitem);
		       	  logger.info("listChild=== "+listChild);
		       	  
		       	 // Treechildren treeChld = null;
		       	  Treerow treeRow = null;
		       	  
		       	  for(Component chld:listChild){
		       		  if(chld instanceof Treerow) continue;
		       		  if(chld instanceof Treechildren) {
		       			  
		       			  Treechildren treechld = (Treechildren)chld;
		       			  List<Component> storeItems = treechld.getChildren();
		       			  for (Component component : storeItems) {
		       				  if(component instanceof Treeitem) {
		       					Treeitem storeItem = (Treeitem)component; 
		       					  storeItem.setSelected(treeitem.isSelected());
		       				  }
		       				  
		       			  }//for
		       			 
		       		  }
		       	  }
			}
				
       		  
       	 }//for
		}*/
			    	  
	
	
	/*	Treeitem tree = treeId.getSelectedItem();
		if (tree != null) {
			String selItem = tree.getLabel();
			if (selectedSubMap.containsKey(selItem)) {
				String StoreValues = selectedSubMap.get(selItem);
				logger.info("=======StoreValues :-" + StoreValues);
				String[] itemStoreValue = StoreValues.split(Constants.DELIMETER_DOUBLECOLON);
				
				for (String store : itemStoreValue) {
					logger.info("===========store :-" + store);
					
				}

			}
		}
	
		//treeId.setOpen(true);

		logger.info("inside onseleced");
		List<Component> list = treeId.getSelectedItem().getChildren();
		logger.info("list" + list);
		for (Component comp : list) {
			logger.info("comp" + comp);
			Treeitem selectedItem = treeId.getSelectedItem();
			logger.info("selectedItem" + selectedItem);
			Treechildren children = selectedItem.getTreechildren();
			logger.info("children" + children);
			for (Treeitem i : children.getItems()) {

				logger.info("inside loop");
				i.setSelected(true);

			}
		}
    }
*/

	/*public void setsudsidiaryId() {
		Components.removeAllChildren(divSubAndStore);
		Components.removeAllChildren(treeId);
		
		// Components.removeAllChildren(sudsidiaryId);
		UsersDomains userDomains = (UsersDomains) orgunitzoneTb.getSelectedItem().getValue();
		Long domainId = userDomains.getDomainId();
		Long domainId = null;
		if(orgunitzoneTb.getSelectedIndex()>0){
		 domainId =  ((Long)orgunitzoneTb.getSelectedItem().getValue()).longValue();
		
//		logger.info("domainId     " + domainId);
		SBSMap = new HashMap<String, String>();
		storeNameMap = new HashMap<String, String>();
		subAndStoreMap = new HashMap<String, String>();
		selectedSubMap = new HashMap<String,String>();

		try {
			List<OrganizationStores> subIdList = organizationStoresDao.findSubsidaryByOrgUnitId(domainId);
			if (subIdList != null && subIdList.isEmpty()) {
				return;
			}
			int count = 0;
			Tree lb = new Tree();
			// treeId.setCheckmark(true);
			// treeId.setMultiple(true);
			// treeId.addEventListener(Events.ON_SELECT , this);
			 treeId.addEventListener(Events.ON_SELECT , this);
			Treechildren child = new Treechildren();
			Treecell homeStorecell = null;

			Treecell subsidiarycell = null;
			List<OrganizationStores> storeIdList = organizationStoresDao
					.findSubsidaryByOrgUnitId(currentUser.getUserOrganization().getUserOrgId(), domainId.longValue());
			if (storeIdList != null && subIdList != null) {
				for (OrganizationStores subsidiary : subIdList) {
					logger.info("=============subs==============" + subIdList);
					StringBuilder selSubsidiary = new StringBuilder();
					StringBuilder selectedSub =new StringBuilder();
					Treeitem item = new Treeitem();
					item.setValue(subsidiary);
					item.setAttribute("type", "SBS");
					Treerow rowsubsidiary = new Treerow();
					Treechildren childStore = new Treechildren();
					subsidiarycell = new Treecell(subsidiary.getSubsidiaryId());
					subsidiarycell.addEventListener(Events.ON_SELECT, this);
					
					// subsidiarycell.addEventListener(Events.ON_SELECT, this);
					subsidiarycell.setId("subId_" + (count++) + "_TrchId");
					subsidiarycell.setParent(rowsubsidiary);
					SBSMap.put(domainId.longValue() + Constants.DELIMETER_DOUBLECOLON + subsidiary.getSubsidiaryId(),
							subsidiary.getSubsidiaryName());
					int storeSize = 0;
					for (OrganizationStores orgStore : storeIdList) {
						Treeitem itemStore = new Treeitem();
						itemStore.setValue(orgStore);
						itemStore.setAttribute("type","Store");
						Treerow rowStore = new Treerow();
						if (subsidiary.getSubsidiaryId().equals(orgStore.getSubsidiaryId())) {
							logger.info("===========storename=========== " + storeIdList);
							storeSize = storeSize+1;
							homeStorecell = new Treecell(orgStore.getStoreName());
							
							// homeStorecell.addEventListener("onClick", this);
							homeStorecell.setParent(rowStore);
							rowStore.setParent(itemStore);
							itemStore.setParent(childStore);
							String storeId = String.valueOf(orgStore.getStoreId());
							 storeNameMap.put(domainId.longValue()+Constants.DELIMETER_DOUBLECOLON+orgStore.getSubsidiaryId()+Constants.DELIMETER_DOUBLECOLON+orgStore.getHomeStoreId(),orgStore.getStoreName());
							// org.getStoreName());
							// subAndStoreMap.put(org.getSubsidiaryId()+"_"+org.getStoreName(),
							// org.getSubsidiaryName().concat("_").concat(storeId));
							subAndStoreMap.put(
									orgStore.getSubsidiaryId() + Constants.DELIMETER_DOUBLECOLON + orgStore.getStoreName(),
									orgStore.getSubsidiaryName().concat(Constants.DELIMETER_DOUBLECOLON).concat(storeId));

							
							  selSubsidiary.append(orgStore.getSubsidiaryName());
							  selSubsidiary.append(Constants. DELIMETER_DOUBLECOLON);
							  selSubsidiary.append(orgStore.getStoreId());
							  selSubsidiary.append(Constants.DELIMETER_DOUBLECOLON);
							  selSubsidiary.append(orgStore.getStoreName());
							  selSubsidiary.append(Constants.DELIMETER_DOUBLECOLON);
							 
							  selectedSub.append(orgStore.getStoreName());
							  selectedSub.append(Constants.DELIMETER_DOUBLECOLON);
						} else
							continue;

					}//for
					item.setAttribute("size", storeSize);
					selSubMap.put(subsidiary.getSubsidiaryId(), selSubsidiary.toString());
					selectedSubMap.put(subsidiary.getSubsidiaryId(), selectedSub.toString());
					childStore.setParent(item);
					rowsubsidiary.setParent(item);
					item.setParent(child);
					child.setParent(treeId);
					treeId.setParent(divSubAndStore);

				}

			}

			logger.info("Received Subsidiaries based on orgunitId And Store based on Subsidiary");

		} catch (Exception exception) {
			logger.error("Exception raised while setting the sudsidiary and store", exception);
		}
		}

	}*/

	/*public void onClick$abbBtnId() {
		
		logger.info("i'm in onClick$abbBtnId()");
		//Components.removeAllChildren(selecteditem);
		
	//	Set<OrganizationStores> subSet = new HashSet();
		Set<OrganizationStores> storeSet = new HashSet();


		int itemCount = SelSubAndStoreId.getItemCount();

		for (; itemCount > 0; itemCount--) {
			SelSubAndStoreId.removeItemAt(itemCount-1);

		}

		Set<Treeitem> selectedItem = treeId.getSelectedItems();
		
		OrganizationStores sub = null;
		OrganizationStores store = null;
		for (Treeitem item : selectedItem) {
			//selecteditem = new Listitem();
			 sub = item.getValue();
			String type = (String)item.getAttribute("type");
			//if (!storeNameMap.containsKey(UserDomain.longValue() + Constants.DELIMETER_DOUBLECOLON + sub.getSubsidiaryId()+sub.getHomeStoreId())) {	
			if(type.equalsIgnoreCase("SBS")){
				String subsidiaryNo = sub.getSubsidiaryId();
				logger.info("===subsidiaryNo"+subsidiaryNo);
				String storeName = sub.getStoreName();
				String subsidiaryName = sub.getSubsidiaryName();
				String storeId = sub.getHomeStoreId();
				logger.info("===subsidiaryName"+subsidiaryName);
				logger.info("===storeName"+storeName);
				logger.info("===storeId"+storeId);
			
				Listcell subsidiaryNoCell = new Listcell(subsidiaryName);
				subsidiaryNoCell.setParent(selecteditem);
				Listcell storeNameCell = new Listcell(subsidiaryNo);
				storeNameCell.setParent(selecteditem);
				Listcell subsidiaryNameCell = new Listcell(storeName);
				subsidiaryNameCell.setParent(selecteditem);
				Listcell storeIdCell = new Listcell(storeId);
				storeIdCell.setParent(selecteditem);
				Listcell imageDelCell = new Listcell();

				Image delImg = new Image("/img/action_delete.gif");
				delImg.setTooltiptext("Delete");
				delImg.setStyle("cursor:pointer;");
				delImg.addEventListener("onClick", this);
				delImg.setParent(imageDelCell);
				delImg.setAttribute("", subsidiaryNo);
				imageDelCell.setParent(selecteditem);

				selecteditem.setParent(SelSubAndStoreId);
		
				subSet.add(sub);
				SelSubAndStoreId.setAttribute("SBS", subSet);
			}
			else if(type.equalsIgnoreCase("Store"))
			{
				store=item.getValue();
				String subsidiaryNo = store.getSubsidiaryId();
				String storeName = store.getStoreName();
				String subsidiaryName = store.getSubsidiaryName();
				String storeId = store.getHomeStoreId();
				logger.info("store ===subsidiaryName"+subsidiaryName);
				logger.info("store ===storeName"+storeName);
				logger.info("store ===storeId"+storeId);
				Listitem selecteditem = new Listitem();
				Listcell subsidiaryNoCell = new Listcell(subsidiaryName);
				subsidiaryNoCell.setParent(selecteditem);
				Listcell storeNameCell = new Listcell(subsidiaryNo);
				storeNameCell.setParent(selecteditem);
				Listcell subsidiaryNameCell = new Listcell(storeName);
				subsidiaryNameCell.setParent(selecteditem);
				Listcell storeIdCell = new Listcell(storeId);
				storeIdCell.setParent(selecteditem);
				Listcell imageDelCell = new Listcell();

				Image delImg = new Image("/img/action_delete.gif");
				
				delImg.setTooltiptext("Delete");
				delImg.setStyle("cursor:pointer;");
				delImg.addEventListener("onClick", this);
				delImg.setParent(imageDelCell);
				imageDelCell.setValue(storeName);
				delImg.setAttribute("editType", "delete");
				delImg.setAttribute("", subsidiaryNo);
				
				imageDelCell.setParent(selecteditem);
				

				selecteditem.setParent(SelSubAndStoreId);
				//subSet.add(store);
				storeSet.add(store);
				SelSubAndStoreId.setAttribute("Store", storeSet);
				
			}
		}
	}*/
		
			/* else {
				Treeitem itemstore = treeId.getSelectedItem();
				OrganizationStores storeobj=itemstore.getValue();
				
				logger.info("storeobj"+storeobj);
				logger.info("itemstore"+itemstore);
				String allstore = null;
				String subsidiaryNo = storeobj.getSubsidiaryId();
				logger.info("subsidiaryNo"+subsidiaryNo);
				if (selSubMap.containsKey(subsidiaryNo)) {
					allstore = selSubMap.get(subsidiaryNo);
					logger.info("All Store " + allstore);
					String subsidiaryName = storeobj.getStoreName();
					String storeId = storeobj.getHomeStoreId();
					String storeName = storeobj.getStoreName();
					String[] subsidiaryNameStore = allstore.split(Constants.DELIMETER_DOUBLECOLON);
					for (int index = 0; index < subsidiaryNameStore.length; index++) {
						subsidiaryName = subsidiaryNameStore[index++];
						storeId = subsidiaryNameStore[index++];
					storeName = subsidiaryNameStore[index];
						boolean flag = true;
						if (!storeSet.isEmpty()) {
							for (OrganizationStores st : storeSet) {
								if (st.getHomeStoreId().equals(storeId)) {
									flag = false;
									break;
								}
							}
						}
						if (flag) {
							//sub = new OrganizationStores();
							//sub.setSubsidiaryId(subsidiaryNo);
							//store = new OrganizationStores();
							//store.setStoreName(storeName);
							//store.setHomeStoreId(storeId);
							//logger.info("Store name" + storeName);
							Listcell subsidiaryNoCell = new Listcell(subsidiaryName);
							subsidiaryNoCell.setParent(selecteditem);
							Listcell storeNameCell = new Listcell(subsidiaryNo);
							storeNameCell.setParent(selecteditem);

							Listcell subsidiaryNameCell = new Listcell(storeName);
							subsidiaryNameCell.setParent(selecteditem);
							Listcell storeIdCell = new Listcell(storeId);
							storeIdCell.setParent(selecteditem);
							Listcell imageDelCell = new Listcell();

							Image delImg = new Image("/img/action_delete.gif");
							delImg.setTooltiptext("Delete");
							delImg.setStyle("cursor:pointer;");
							delImg.addEventListener("onClick", this);
							delImg.setParent(imageDelCell);
							delImg.setAttribute("", subsidiaryNo);
							imageDelCell.setParent(selecteditem);

							selecteditem.setParent(SelSubAndStoreId);
				//			subSet.add(sub);
							storeSet.add(storeobj);
							selecteditem = new Listitem();
						//}
					}
				}*/
			 
	


			
		
	



	public void SelSubAndStoreId(String selectedcheckbox) {
		UsersDomains userDomains = (UsersDomains) orgunitTb.getSelectedItem().getValue();
		Long domainId = userDomains.getDomainId();
		List<OrganizationStores> subIdList = organizationStoresDao.findSubsidaryByOrgUnitId(domainId);

	}

	public void onEvent(Event event) throws Exception {
		// TODO Auto-generated method stub
		super.onEvent(event);
		
    	if (event.getTarget() instanceof Image) {

			Image img = (Image) event.getTarget();
			// String type = (String) sessionScope.getAttribute("editType");
			String type = (String) img.getAttribute("editType");
			if(type == null ) return;

			if ( type.equals("delete")) {
				
				try {
					Listitem imgedit = (Listitem) (img.getParent()).getParent().getParent();
					OrganizationZone zone = imgedit.getValue();
					int confirm = Messagebox.show("Confirm to delete the selected zone.", "Confirm",
							Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
					if (confirm == Messagebox.OK) {
					logger.info("deletion  zone id "+zone.getZoneId()); 
            if( digitalReceiptUserSettingsDao.isZoneConfigured(zone.getZoneId())){
					  confirm = Messagebox.show("This zone has template mapping .Are you sure want to  delete the associate mapping also.", "Confirm",
							Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
					if (confirm == Messagebox.OK) {
					  zone.setDeleteStatus(true);
					ZoneDaoForDML.saveOrUpdate(zone);
					//long	userId = findPowerUser(zone.getDomainId());
					long	userId = currentUser.getUserId();
					DigitalReceiptUserSettings setting=	digitalReceiptUserSettingsDao.findUserSelectedTemplateBy(userId, zone.getZoneId());
       	   //TODO venkata 12_03_2019
					setting.setSettingEnable(false);
					digitalReceiptUserSettingsDaoForDML.saveOrUpdate(setting);
       	   zoneLbId.removeChild(imgedit);
            zoneReportPagingId.setTotalSize(zoneLbId.getItemCount());
					 MessageUtil.setMessage("Zone deleted successfully.", "color:green;");
					}
          
            }
            else{
					 //  ZoneDaoForDML.deleteZoneStoreBy(zone.getZoneId());
					  // ZoneDaoForDML.deleteZone(zone);
					   zone.setDeleteStatus(true);
						ZoneDaoForDML.saveOrUpdate(zone);
					    zoneLbId.removeChild(imgedit);
					  zoneReportPagingId.setTotalSize(zoneLbId.getItemCount());
					   MessageUtil.setMessage("Zone deleted successfully.", "color:green;");
					          }
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					logger.info("Exception : ",e);
				}
			
			}
				
			if (type.equals("edit")) {
				logger.info("i'm into onevent edit");
				Listitem imgedit = (Listitem) (img.getParent()).getParent().getParent();
				OrganizationZone zone = imgedit.getValue();
				session.setAttribute("OrganizationZone", zone);
				logger.info("zone.getStores()"+zone.getStores());
				logger.info("zone.getSubsidiaries()"+zone.getSubsidiaries());
				logger.info("Zone Id " + zone);
					try {
						zoneTabBoxId.setSelectedIndex(1);
						saveBtnId.setLabel("Update Zone Settings");
						newZoneSetting(zone);
					} catch (Exception exception) {

						logger.error("Exception  in onEvent ::", exception);
					}
			}
		}

		
		if(event.getTarget() instanceof Checkbox){
			Checkbox sbschkbox=(Checkbox) event.getTarget();
			if(sbschkbox.isChecked()){
				Row row=(Row) sbschkbox.getParent();
			     String subId=row.getValue();
			     Detail dt=row.getDetailChild();
			     dt.setOpen(true);
				if(rowsMap.containsKey(subId)){
				   Rows rows=rowsMap.get(subId);
				  List<Component> temprow=rows.getChildren();
				   for(Component comp:temprow){
					Checkbox ch=  (Checkbox) comp.getFirstChild();
					ch.setChecked(true);
				   }
				   
				}
			}
			else{
				Row row=(Row) sbschkbox.getParent();
			     String subId=row.getValue();
				if(rowsMap.containsKey(subId)){
				   Rows rows=rowsMap.get(subId);
				  List<Component> temprow=rows.getChildren();
				   for(Component comp:temprow){
					Checkbox ch=  (Checkbox) comp.getFirstChild();
					ch.setChecked(false);
				   }
				   
				}
			}
			
		}
		if(event.getTarget() instanceof Paging) {
			Paging paging = (Paging)event.getTarget();
			int desiredPage = paging.getActivePage();
			PagingEvent pagingEvent = (PagingEvent) event;
			int pSize = pagingEvent.getPageable().getPageSize();
			int ofs = desiredPage * pSize;
			getZoneReports(ofs,  pagingEvent.getPageable().getPageSize());
		}
	}
		  
	public void onSelect$zoneTabBoxId(){
		zoneNamelabel.setValue("");
		if(zoneTabBoxId.getSelectedIndex() == 1){
			
			  Components.removeAllChildren(subsandStoreRowId);
			orgunitzoneTb.setSelectedIndex(0);
			orgunitzoneTb.setDisabled(false);
			setorgunitzoneTb();
			zoneNameTb.setText(Constants.STRING_NILL);
			zoneNameTb.setDisabled(false);
			descriptionTb.setText(Constants.STRING_NILL);
			//onClick$abbBtnId();
		//	createZoneBtnId.setLabel("Create");
			
		}else if(zoneTabBoxId.getSelectedIndex() == 0){
			saveBtnId.setLabel("Create Zone");
			orgunitTb.setSelectedIndex(0);
			setorgunitTb();
			onSelect$orgunitTb();
		}
	}
	public void getZoneReports(int startIndex,int endIndex ) {
		int itemCount = zoneLbId.getItemCount();
		for (; itemCount > 0; itemCount--) {
			zoneLbId.removeItemAt(itemCount-1);

		}
		String domainId=null;
		if(orgunitTb.getSelectedIndex()==0){
		List<Listitem> list=	orgunitTb.getItems();
		StringBuilder sb=new StringBuilder("");
		for(Listitem item:list){
			if(sb.length()>0)sb.append(",");
			UsersDomains domian=	 item.getValue();
			if(domian!=null)
			sb.append(""+domian.getDomainId());
			else continue;
		}
		domainId=sb.toString();
		}
		else
			domainId=""+((UsersDomains) orgunitTb.getSelectedItem().getValue()).getDomainId();
		logger.info("  domains "+domainId);
		if (domainId != null) {
			List<OrganizationZone> zonelist = zoneDao.getZoneDetailsBydomainId(domainId,startIndex, endIndex);
			List<OrganizationStores> storeList = null;
			logger.info("=========zonelist" + zonelist);
			if (zonelist != null) {
				Listcell lc;
				Listcell lcEdit;
				Listitem li;
				//StringBuilder group=null;
					for (OrganizationZone zone : zonelist) {
						Map<String, String> subAndStoreMap = new HashMap<String, String>();
						Map<String ,String> subsNameMap=new HashMap<>();
					StringBuilder sbStore = new StringBuilder();
					StringBuilder group=new StringBuilder();
					li = new Listitem();
					lc = new Listcell(zone.getZoneName());
					lc.setParent(li);
					//lc = new Listcell("" + zone.getCreatedDate());
					lc = new Listcell("" + MyCalendar.calendarToString(zone.getCreatedDate(), MyCalendar.FORMAT_FULLMONTHDATEONLY,clientTimeZone ));
					lc.setParent(li);
					li.setValue(zone);
					storeList = zoneDao.getGroupOfDetailByZone(zone.getZoneId());
					if (storeList != null)
						for (OrganizationStores stores : storeList) {
							logger.info("store Name  " + stores.getStoreName());
							String subName=stores.getSubsidiaryName() != null ? stores.getSubsidiaryName() : "";
							String subId=stores.getSubsidiaryId() != null ? stores.getSubsidiaryId() : "";
							subsNameMap.put(subId, subName);
							String store=stores.getStoreName() != null ?  stores.getStoreName()  : "";
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
					//group.append(subAndStoreMap);	
					lc = new Listcell(newGroup.toString());
					lc.setParent(li);

					lc = new Listcell();
					Hbox hbox = new Hbox();

					Image editImg = new Image("/images/Edit_icn.png");
					editImg.setTooltiptext("Edit");
					editImg.setStyle("cursor:pointer;");
					editImg.setStyle("padding-left: 10px;");
        			editImg.setStyle("padding-up: 25px;");
					editImg.addEventListener("onClick", this);
					sessionScope.setAttribute("editType","edit");
					editImg.setAttribute("editType", "edit");
					editImg.setParent(hbox);
					Image delImg = new Image("/img/action_delete.gif");
					delImg.setTooltiptext("Delete");
					delImg.setStyle("cursor:pointer;");
					delImg.setStyle("padding-left: 25px;");
					delImg.addEventListener("onClick", this);
				    sessionScope.setAttribute("editType","delete");
					delImg.setAttribute("editType", "delete");
					delImg.setParent(hbox);
				
					
				//	delImg.setParent(lc);
				//	editImg.setParent(lc);

					hbox.setParent(lc);
					lc.setParent(li);
					li.setValue(zone);
					li.setParent(zoneLbId);

				}

			}
		}

	}
public void newZoneSetting(OrganizationZone zone){
	this.zone = zone;
	zoneNameTb.setValue(zone.getZoneName());
	zoneNameTb.setDisabled(true);
	orgunitzoneTb.setDisabled(true);
	descriptionTb.setValue(zone.getDescription());
	Long domainId = zone.getDomainId();
	logger.info("domainId" + domainId);
	
	setorgunitzoneTb();
	// List<UsersDomains> orgList =
	// userDomaonDao.FindByOrgId(currentUser.getUserOrganization().getUserOrgId());
	List<Listitem> items = orgunitzoneTb.getItems();

	for (Listitem item : items) {
		for (int i = 0; i < orgunitzoneTb.getItemCount(); i++) {
			logger.info("domainId at index======= " + orgunitzoneTb.getItemAtIndex(i).getLabel());
			if (domainId.equals(orgunitzoneTb.getItemAtIndex(i).getValue())) {
				logger.info("domainId at index " + orgunitzoneTb.getItemAtIndex(i).getValue());
				orgunitzoneTb.setSelectedIndex(i);
				onSelect$orgunitzoneTb();
				break;
			}
		}

	}
	  
	Set<OrganizationStores> setStore= zone.getStores();
     for(Checkbox chkst:cbListStore){
    	for(OrganizationStores store:setStore){
    		if(store.getStoreId().equals(chkst.getValue())){
    			chkst.setChecked(true);
    			for (Checkbox chk:cbListSbs) {
    				Row row=(Row) chk.getParent();
				     Detail dt=row.getDetailChild();
				     dt.setOpen(true);
    			}
    		}
    		else continue;
    		
        }
     }
  /*   boolean isallChecked=false;
    	for (Checkbox chk:cbListSbs) {
    		logger.info("***************************");
	   		for(Checkbox chkst:cbListStore){
	   			if(!chkst.isChecked()){
	   				logger.info("========not checked=========");
	   				isallChecked=false;
	   				break;
	   			}		
	   			else
	   				isallChecked=true;
		}
	   		logger.info("isAll checked  "+isallChecked);
	   		if(isallChecked)
	   			chk.setChecked(true);
 		
		}*/
	
}
/*	public void zoneSetting(OrganizationZone zone) {
		logger.info("i'm inside zonesetting method");
		this.zone = zone;
		zoneNameTb.setValue(zone.getZoneName());
		zoneNameTb.setDisabled(true);
		orgunitzoneTb.setDisabled(true);
		
		descriptionTb.setValue(zone.getDescription());
		Long domainId = zone.getDomainId();
		logger.info("domainId" + domainId);
		
		setorgunitzoneTb();
		// List<UsersDomains> orgList =
		// userDomaonDao.FindByOrgId(currentUser.getUserOrganization().getUserOrgId());
		List<Listitem> items = orgunitzoneTb.getItems();

		for (Listitem item : items) {
			for (int i = 0; i < orgunitzoneTb.getItemCount(); i++) {
				logger.info("domainId at index======= " + orgunitzoneTb.getItemAtIndex(i).getLabel());
				if (domainId.equals(orgunitzoneTb.getItemAtIndex(i).getValue())) {
					logger.info("domainId at index " + orgunitzoneTb.getItemAtIndex(i).getValue());
					orgunitzoneTb.setSelectedIndex(i);
					onSelect$orgunitzoneTb();
					break;
				}
			}

		}
		logger.info("after org.unit");
		Collection<Treeitem> treeitems =  treeId.getItems();
		  
		Set<OrganizationStores> list= zone.getStores();
		Treeitem currSBSitem =  null;
		Treeitem sbsitem =  null;
		String sbsId = null;
		int size = 0;
		List<Treeitem> sbsStoresList = null;
			for (Treeitem treeitem : treeitems) {
				if(treeitem.getAttribute("type").equals("SBS")){
					currSBSitem = treeitem;
					if(sbsitem == null) sbsitem =  treeitem;
					sbsId = sbsitem.getLabel();
					size = (Integer)sbsitem.getAttribute("size");
					
					if(sbsStoresList == null) sbsStoresList = new ArrayList<Treeitem>();
					//continue;
					logger.debug("====1=====");
				}
				if(currSBSitem.getAttribute("type").equals("SBS")&& !currSBSitem.getLabel().equals(sbsitem.getLabel())){
					logger.debug("====4=====");
					int selectedCount = 0;
					for (Treeitem storetreeitem : sbsStoresList) {
						storetreeitem.setSelected(true);
						selectedCount = selectedCount+1;
					}
					logger.info("selectedCount"+selectedCount);
					logger.info("size"+size);
					if(size == selectedCount) sbsitem.setSelected(true);
					sbsitem = treeitem;
					sbsStoresList = new ArrayList<Treeitem>();
					sbsId = sbsitem.getLabel();
					size = (Integer)sbsitem.getAttribute("size");
					
				}
				for (OrganizationStores organizationStores : list) {
					logger.debug("====2====="+treeitem.getAttribute("type").toString()+ 
							" "+organizationStores.getSubsidiaryId() + " " + sbsId + " "+ organizationStores.getStoreName()+" "+treeitem.getLabel());
					if(organizationStores.getSubsidiaryId().equals(sbsId) &&
							organizationStores.getStoreName().equals(treeitem.getLabel())){
						logger.debug("====3=====");
						sbsStoresList.add(treeitem);
					}
				}
			}
			onClick$abbBtnId();
			
			
			
		for (Treeitem treeitem : treeitems) {
			logger.info("treeitem"+treeitem.getLabel());
			logger.info("zone.getSubsidiaries()"+zone.getSubsidiaries());
			logger.info("zone.getStores()"+zone.getStores());
			//Set<OrganizationStores>sublist= zone.getSubsidiaries();
			for (OrganizationStores organizationStores : sublist) {
				logger.info("zone.getSubsidiaries()"+organizationStores.getSubsidiaryId());
				if((treeitem.getLabel()).equals(organizationStores.getSubsidiaryId()))
				{
			logger.info("sub"+treeitem.getLabel());
			treeitem.setSelected(true);
				}		
			}
		
		for (OrganizationStores organizationStores : list) {
			logger.info("organizationStores"+organizationStores.getHomeStoreId());
		
		if((treeitem.getLabel()).equals(organizationStores.getStoreName()))
		{
			logger.info("store"+treeitem.getLabel());
			treeitem.setSelected(true);
			
		}
		}	
		editedMap=(Map<String, String>) ((HashMap) selectedSubMap).clone();;
		Collection<String> values = editedMap.values();
		Collection<String> keysetvalues = editedMap.keySet();
		for (String string : keysetvalues) {
			String storesvalues =editedMap.get(string);
			
			logger.info("===============++++++++++++++++++++++storesvalues"+storesvalues);
		}
		logger.info("values"+values);
		logger.info("values"+keysetvalues);
		onClick$abbBtnId();
		
		}
			//logger.info("=====treeitemvalue   "+treeitem.getValue());
			logger.info("=====treeitemlabel   "+treeitem.getLabel());
			
		   Iterator<OrganizationStores> storeitem = stores.iterator();
		   // logger.info("storeset"+storeSet);
		    while (storeitem.hasNext()) {
		    	logger.info("treeitem.getLabel())"+treeitem.getLabel());
		    	logger.info("storeitem.next().getStoreName())"+storeitem.next().getStoreName());
		    	if((treeitem.getLabel()).equals(storeitem.next().getStoreName()));
				{
					treeitem.setSelected(true);
				}
		      
		    }
			
		}
		// orgunitzoneTb.setSelectedIndex(zone.getDomainId());

		// List<OrganizationZone> Selectedzonelist =
		// zoneDao.getZoneDetailsByOrgUnitId(zoneId);
	}*/
	
	public boolean validateZoneName() {
		logger.info("----just entered---");
		
		String zoneName = zoneNameTb.getValue().trim();
		logger.info("zoneName in validatezonename"+zoneName);
		//String homeStoreId = posLocationTbId.getValue().trim();
		
		
		if(zoneName.length() == 0) {
			
			MessageUtil.setMessage("Please provide Zone Name. Zone Name cannot be left empty.", "color:red;");
			return false;
			
		}
	
			if(!Utility.validateNames(zoneName)){
				zoneNamelabel.setValue("Please provide Valid Zone Name.special characters not allowed.");
				return false;
				
			}
		
		try {
			/*if(orgunitTb.getSelectedIndex()==0){
				MessageUtil.setMessage("Please select Org. Unit.", "color:red;");
				return false;
			}*/
			UsersDomains userDomains = (UsersDomains) orgunitTb.getSelectedItem().getValue();
			
			Long domainId=(Long)orgunitzoneTb.getSelectedItem().getValue();
			if(domainId==null){
				MessageUtil.setMessage("Please select Org. Unit.", "color:red;");
				return false;
			}
			OrganizationZone zoneNames = (OrganizationZone) zoneDao.getZoneNameByOrgUnitId(domainId,zoneName);
			
			if(zoneNames == null) {
				logger.info("======zoneNames======="+zoneNames);
				return true;
			}else {
		
			MessageUtil.setMessage("Zone Name already exists. Please provide another Zone Name.",  "color:red;");
			return false;
		}
			
		} catch (Exception e) {
			logger.info("Exception : ",e);
		}
		
		return true;
		
		
	}
	public boolean listboxisEmpty()
	{
		
		   for(Checkbox ch:cbListStore){
			   if(ch.isChecked())
				   return true;
		   }
	   MessageUtil.setMessage(" Please select Subsidiary and Store for Zone creation.",  "color:red;");
		return false;
	}
	public boolean  validateSubAndStore(){
		 for (Checkbox chk:cbListStore) {
			if(!chk.isChecked())continue;			
			 List<OrganizationZone> storelist = zoneDao.getZoneStoresBydomainId(""+orgunitzoneTb.getSelectedItem().getValue());
			 if(storelist!=null){ 
			 for (OrganizationZone organizationZone : storelist) {
				 logger.info("organizationZone.getStores()=========+++=before"+organizationZone.getStores()); 
				 Set<OrganizationStores> orgZone = organizationZone.getStores();
				 for (OrganizationStores organizationStores : orgZone) {
					 if((chk.getValue()).equals(organizationStores.getStoreId())){
						   String zoneName =organizationZone.getZoneName();
						 logger.info("zoneNameTb.getValue()"+zoneNameTb.getValue());
						 if(zoneName.equals(zoneNameTb.getValue()))
						 {
							 
						 }
						 else
						 {
							 MessageUtil.setMessage("Selected  Store already exist in "+zoneName+". Please select another stores",  "color:red;");
							 return false;
						 }
					 }
					
				 	}
			 	}
			 }
		 }
		  
		return true;
	}
	public void onSelect$reportsPerPageLBId() {
		try {
			int n =Integer.parseInt(reportsPerPageLBId.getSelectedItem().getLabel().trim());
			logger.info("  page size count "+n);
			zoneReportPagingId.setActivePage(0);
			zoneReportPagingId.addEventListener("onPaging", this);
			zoneReportPagingId.setPageSize(n);
			setpageTotCount();
			getZoneReports(0, zoneReportPagingId.getPageSize());
			
			
		} catch (Exception e) {
			logger.error("Exception ::", e);
		}
	}
	public void setpageTotCount() {
		String domainId=null;
		if(orgunitTb.getSelectedIndex()==0){
			List<Listitem> list=	orgunitTb.getItems();
			StringBuilder sb=new StringBuilder("");
			for(Listitem item:list){
				if(sb.length()>0)sb.append(",");
				UsersDomains domian=	 item.getValue();
				if(domian!=null)
				sb.append(""+domian.getDomainId());
				else continue;
			}
			domainId=sb.toString();
			}
			else
				domainId=""+((UsersDomains) orgunitTb.getSelectedItem().getValue()).getDomainId();
		     if(domainId!=null){
			int count = zoneDao.getZoneCount(domainId);
			logger.info("count  "+count);
		zoneReportPagingId.setTotalSize(count);
		
	}
	}
	public Long findPowerUser(Long domainId) {
		List<Long> userIdsList = domainDao.getUsersByDomainId(domainId);
		SecRolesDao secRolesDao = (SecRolesDao)SpringUtil.getBean("secRolesDao");
		for (Long userId : userIdsList) {

			List<SecRoles> rolesList = secRolesDao.findByUserId(userId);

			String role = "";

			//List<Authorities> roleLst = usersDao.findRole(userName);
			for (SecRoles secRole : rolesList) {

				//if(secRole.getType().equalsIgnoreCase("Super User")) return userId;
				if(secRole.getType().equalsIgnoreCase("Super User")) return userId;

			}

		}

		return null;
	}
}