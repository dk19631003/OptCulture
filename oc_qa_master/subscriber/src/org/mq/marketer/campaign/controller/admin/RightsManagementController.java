package org.mq.marketer.campaign.controller.admin;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.mq.marketer.campaign.beans.SecGroups;
import org.mq.marketer.campaign.beans.SecRights;
import org.mq.marketer.campaign.beans.SecRoles;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.UserController;
import org.mq.marketer.campaign.controller.useradmin.BillingDetailsController;
import org.mq.marketer.campaign.dao.SecGroupsDao;
import org.mq.marketer.campaign.dao.SecGroupsDaoForDML;
import org.mq.marketer.campaign.dao.SecRightsDao;
import org.mq.marketer.campaign.dao.SecRightsDaoForDML;
import org.mq.marketer.campaign.dao.SecRolesDao;
import org.mq.marketer.campaign.dao.SecRolesDaoForDML;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.dao.UsersDaoForDML;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.LBFilterEventListener;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PageUtil;
import org.mq.marketer.campaign.general.RightsEnum;
import org.mq.marketer.campaign.general.Utility;
import org.springframework.dao.DataIntegrityViolationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Window;

public class RightsManagementController extends GenericForwardComposer<Window> {

	private static final long serialVersionUID = 1L;
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	Tab usersRolesTabId,rolesGroupsTabId, groupsRightsTabId;

	Checkbox rolesGrantedChkId, groupsGrantedChkId, rightsGrantedChkId;
	
	Button addRoleBtnId, deleteRoleBtnId;
	
	Listbox usersURLBId, rolesURLBId, rolesRGLBId, groupsRGLBId, groupsGRLBId, rightsGRLBId;


	Toolbarbutton rolesRGClearFilterTbbId;
	
	Window rightsManagementWinId , subWinId;
	
	private Div subWinId$addRoleDivId, subWinId$addGroupDivId;
	
	private Textbox subWinId$roleNameTBId, subWinId$roleDescTBId,
	subWinId$groupNameTBId, subWinId$groupDescTBId;
	private Label subWinId$rolesErrormsgLblId, subWinId$groupErrormsgLblId;
	
	UsersDao usersDao = null;
	UsersDaoForDML usersDaoForDML = null;
	SecRolesDao secRolesDao = null;
	SecRolesDaoForDML secRolesDaoForDML = null;
	SecGroupsDao secGroupsDao = null;
	SecGroupsDaoForDML secGroupsDaoForDML = null;
	SecRightsDao secRightsDao = null;
	SecRightsDaoForDML secRightsDaoForDML = null;
	
	boolean rolesRGFetched=false;
	boolean groupsRGFetched=false;
	boolean groupsGRFetched=false;
	boolean rightsGRFetched=false;
	
	public RightsManagementController() {
		usersDao = (UsersDao)SpringUtil.getBean("usersDao");
		usersDaoForDML = (UsersDaoForDML)SpringUtil.getBean("usersDaoForDML");
		secRolesDao = (SecRolesDao)SpringUtil.getBean("secRolesDao");
		secRolesDaoForDML = (SecRolesDaoForDML)SpringUtil.getBean("secRolesDaoForDML");
		secGroupsDao = (SecGroupsDao)SpringUtil.getBean("secGroupsDao");
		secGroupsDaoForDML = (SecGroupsDaoForDML)SpringUtil.getBean("secGroupsDaoForDML");
		secRightsDao = (SecRightsDao)SpringUtil.getBean("secRightsDao");
		secRightsDaoForDML = (SecRightsDaoForDML)SpringUtil.getBean("secRightsDaoForDML");
	}
	
	@Override
	public void doAfterCompose(Window comp) throws Exception {
		super.doAfterCompose(comp);
		logger.debug("-- Just Entered -- ");
		
		String style = "font-weight:bold;font-size:15px;color:#313031;" ;
		PageUtil.setHeader("User Rights Management", "", style, true);

		populateURUsers(); // Populate User only one time
		populateRoles(rolesURLBId); // Fetch Roles First time & on Adding the role 
		
		onSelect$usersRolesTabId();

		LBFilterEventListener.lbFilterSetup(usersURLBId);
		LBFilterEventListener.lbFilterSetup(rolesURLBId);
		
		LBFilterEventListener.lbFilterSetup(rolesRGLBId);
		LBFilterEventListener.lbFilterSetup(groupsRGLBId);
		
		LBFilterEventListener.lbFilterSetup(groupsGRLBId);
		LBFilterEventListener.lbFilterSetup(rightsGRLBId);
	}
	

	public void onSelect$usersRolesTabId() {
	}
	
	public void onSelect$rolesGroupsTabId() {
		if(rolesRGFetched==false) {
			populateRoles(rolesRGLBId);
			rolesRGFetched=true;
			groupsRGLBId.clearSelection();
		}
		
		if(groupsRGFetched==false) {
			populateGroups(groupsRGLBId);
			groupsRGFetched=true;
			rolesRGLBId.clearSelection();
		}
	}
	
	public void onSelect$groupsRightsTabId() {
		if(groupsGRFetched==false) {
			populateGroups(groupsGRLBId);
			groupsGRFetched=true;
			rightsGRLBId.clearSelection();
		}
		
		if(rightsGRFetched==false) {
			populateRights();
			rightsGRFetched=true;
			groupsGRLBId.clearSelection();
		}
	}
	
	public void onClick$addRoleBtnId() {
		subWinId$rolesErrormsgLblId.setVisible(false);
		subWinId$addGroupDivId.setVisible(false);
		subWinId$addRoleDivId.setVisible(true);
		subWinId.doHighlighted();
	}
	
	public void onClick$addGroupBtnId() {
		subWinId$groupErrormsgLblId.setVisible(false);
		subWinId$addRoleDivId.setVisible(false);
		subWinId$addGroupDivId.setVisible(true);
		subWinId.doHighlighted();
	}
	
	public void onClick$rolesSaveBtnId$subWinId() {
		try {
			if(subWinId$roleNameTBId.getValue().trim().isEmpty()) {
				subWinId$rolesErrormsgLblId.setValue("Please provide all required the Values");
				subWinId$rolesErrormsgLblId.setVisible(true);
				return;
			}
			SecRoles secRole = new SecRoles();
			secRole.setName(subWinId$roleNameTBId.getValue().trim());
			secRole.setDescription(subWinId$roleDescTBId.getValue().trim().isEmpty()?null:subWinId$roleDescTBId.getValue().trim());
			secRole.setType(Constants.SECROLE_TYPE_CUSTOM);
			secRolesDaoForDML.saveOrUpdate(secRole);
			
			subWinId.setVisible(false);
			
			populateRoles(rolesURLBId);
			rolesRGFetched=false;
		
		} catch (DataIntegrityViolationException e) {
			logger.error("Exception", e);
			subWinId$rolesErrormsgLblId.setValue("Error : Given Role is already exist.");
			subWinId$rolesErrormsgLblId.setVisible(true);
			
		} catch (Exception e) {
			logger.error("Exception", e);
			subWinId$rolesErrormsgLblId.setValue("Error :"+e.getMessage());
			subWinId$rolesErrormsgLblId.setVisible(true);
		}
	}
	
	
	public void onClick$groupSaveBtnId$subWinId() {
		try {
			if(subWinId$groupNameTBId.getValue().trim().isEmpty()) {
				subWinId$groupErrormsgLblId.setValue("Please provide all required the Values");
				subWinId$groupErrormsgLblId.setVisible(true);
				return;
			}
			SecGroups secGroup = new SecGroups();
			secGroup.setName(subWinId$groupNameTBId.getValue().trim());
			secGroup.setDescription(subWinId$groupDescTBId.getValue().trim().isEmpty()?null:subWinId$groupDescTBId.getValue().trim());
			secGroup.setType(Constants.SECGROUP_TYPE_CUSTOM);
			//secGroupsDao.saveOrUpdate(secGroup);
			secGroupsDaoForDML.saveOrUpdate(secGroup);
			
			subWinId.setVisible(false);
			
			populateGroups(groupsRGLBId);
			groupsGRFetched=false;

		
		} catch (DataIntegrityViolationException e) {
			logger.error("Exception", e);
			subWinId$groupErrormsgLblId.setValue("Error : Given Group is already exist.");
			subWinId$groupErrormsgLblId.setVisible(true);
			
		} catch (Exception e) {
			logger.error("Exception", e);
			subWinId$groupErrormsgLblId.setValue("Error :"+e.getMessage());
			subWinId$groupErrormsgLblId.setVisible(true);
		}
	}
		

	public void onCheck$rolesGrantedChkId() {
		rolesURLBId.setAttribute(Constants.FILTER_SHOW_GRANTED, rolesGrantedChkId.isChecked() ? "true" : "false");
		onSelect$usersURLBId();
	}
	
	public void onCheck$groupsGrantedChkId() {
		groupsRGLBId.setAttribute(Constants.FILTER_SHOW_GRANTED, groupsGrantedChkId.isChecked() ? "true" : "false");
		onSelect$rolesRGLBId();
	}
	
	public void onCheck$rightsGrantedChkId() {
		rightsGRLBId.setAttribute(Constants.FILTER_SHOW_GRANTED, rightsGrantedChkId.isChecked() ? "true" : "false");
		onSelect$groupsGRLBId();
	}
	
//******************  USERS & ROLES  ******************************	
	
	public void onSelect$usersURLBId() {
		
		logger.info("usersURLBId selected");
		Set<Listitem> selLiSet = usersURLBId.getSelectedItems();

		Users tempUser=null;
		SecRoles tempRole=null;
		
		Set<SecRoles> selUserRoles= new HashSet<SecRoles>();
		
		for (Listitem eachLi : selLiSet) {
			tempUser = (Users)eachLi.getValue();
			if(tempUser==null) {
				logger.info("User is null ==="+eachLi.getChildren().size());
				continue;
			}
			
			List<SecRoles> userRoles = secRolesDao.findByUserId(tempUser.getUserId());
			logger.info("userRoles="+userRoles);
			
			if(userRoles==null || userRoles.isEmpty()) continue;
			
			Iterator<SecRoles> rolesIt = userRoles.iterator();
			while(rolesIt.hasNext()) {
				tempRole = rolesIt.next();
				//logger.info(tempUser.getUserName()+"="+tempRole.getName());
				selUserRoles.add(tempRole);
				
			} // while
		} // for

		Set<String> selUsersRoleNameSet = new HashSet<String>();

		for (SecRoles secRoles : selUserRoles) {
			selUsersRoleNameSet.add(secRoles.getName());
		} // for
		
		List<Listitem> rolesList = rolesURLBId.getItems();
		for (Listitem eachLi : rolesList) {
			tempRole = (SecRoles)eachLi.getValue();
			if(selUsersRoleNameSet.contains(tempRole.getName())) {
				eachLi.setSelected(true);
				//eachLi.setVisible(true);
			}
			else {
				eachLi.setSelected(false);
			}
		} // for
		
		Utility.filterListboxByListitems(rolesURLBId, false);

	} // onSelect$usersLBId

	
	public void onClick$saveRoleChangesBtnId() {
		Set<Listitem> selUsersSet = usersURLBId.getSelectedItems();
		Set<Listitem> selRolesSet = rolesURLBId.getSelectedItems();
		
		if(selUsersSet==null || selUsersSet.isEmpty()) {
			MessageUtil.setMessage("Please select User & Roles to be associated.", "red");
			return;
		}
		
		String msgStr = "Confirm to save the changes for the selected user ?";
		if(selRolesSet==null || selRolesSet.isEmpty()) {
			msgStr = "Confirm to delete all the roles for the selected user ?";
		}
		if(Messagebox.show(msgStr, "Confirm", 
				Messagebox.YES|Messagebox.NO, Messagebox.QUESTION) != Messagebox.YES) {
			return;
		}
		
		Set<SecRoles> setRolesSet = new HashSet<SecRoles>();
		SecRoles secRole=null;
		Iterator<Listitem> rolesIt = selRolesSet.iterator();
		
		boolean isBillingAdmin = false;
		while(rolesIt.hasNext()) {
			secRole = (SecRoles)rolesIt.next().getValue();
			setRolesSet.add(secRole);
			if(secRole.getName().equals(Constants.ROLE_USER_BILLING_ADMIN)) isBillingAdmin = true;
			
		}
		
		Users tempUser=null;
		Iterator<Listitem> usersIt = selUsersSet.iterator();
		while(usersIt.hasNext()) {
			tempUser = (Users)usersIt.next().getValue();
			tempUser.setRoles(setRolesSet);
			
			
			//usersDao.saveOrUpdate(tempUser);
			usersDaoForDML.saveOrUpdate(tempUser);
			
			if(isBillingAdmin && tempUser.getCIMProfileId() == null) {
				
				
				createBillingProfile(tempUser);
				
				
			}//if
			
			
		}
		
		
		
		
		
		
		
	}
	
	public void createBillingProfile (Users user) {
		

		
		if(user.getCIMProfileId() != null) {
			
			MessageUtil.setMessage("User already has CIM account.", "color:blue;");
			return ;
			
		}//
		
		
		String createCutomerProfile = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
									"<createCustomerProfileRequest xmlns=\"AnetApi/xml/v1/schema/AnetApiSchema.xsd\">" +
									"<merchantAuthentication>" +
									"<name>"+BillingDetailsController.CIMMerchantLoginName+"</name>" +
									"<transactionKey>"+BillingDetailsController.CIMMerchantTransactionKey+"</transactionKey>" +
									"</merchantAuthentication>" +
									"<profile>" +
									"<merchantCustomerId>"+user.getUserId().longValue()+"</merchantCustomerId>" +
									"<description>"+user.getUserName()+"</description>" +
									"<email>"+user.getEmailId()+"</email>" +
									/*"<paymentProfiles>"+
									"<billTo>" +
									"<firstName>" +user.getFirstName() +"</firstName>"+
									"<lastName>" +user.getLastName() +"</lastName>"+
									"<company>" +user.getCompanyName()+"</company>"+
									"<address>" +user.getAddressOne()+"</address>" +
									"<city>" +user.getCity()+"</city>" +
									"<state>" +user.getState()+"</state>" +
									"<zip>" +user.getPinCode()+"</zip>" +
									"<country>" +user.getCountry()+"</country>" +
									"<phoneNumber>" +user.getPhone()+"</phoneNumber>" +
									"</billTo>"+
									"</paymentProfiles>"+*/
									"</profile>" +
									"</createCustomerProfileRequest>";

		
		
		
		try {
			URL url = new URL("https://api.authorize.net/xml/v1/request.api");
			
			HttpURLConnection urlconnection = (HttpURLConnection) url.openConnection();
			
			urlconnection.setRequestMethod("POST");
			urlconnection.setRequestProperty("Content-Type","application/xml");
			urlconnection.setDoOutput(true);

			OutputStreamWriter out = new OutputStreamWriter(urlconnection.getOutputStream());
			out.write(createCutomerProfile);
			out.flush();
			out.close();
			
			BufferedReader in = new BufferedReader(	new InputStreamReader(urlconnection.getInputStream()));
			
			String decodedString;
			
			String response = "";
			while ((decodedString = in.readLine()) != null) {
				response += decodedString;
			}
			
			logger.debug("response =============>"+response);
		String value = parseForSuccessResponse(response);
		
		if(!value.contains(Constants.ADDR_COL_DELIMETER) ) {
			
			MessageUtil.setMessage("CIM profile for user : '"+user.getUserName()+"' created successfully.", "color:blue;");
			user.setCIMProfileId(value);
			//usersDao.saveOrUpdate(user);
			usersDaoForDML.saveOrUpdate(user);
			
		}else {
			
			MessageUtil.setMessage("Poblem while creating CIM profile for user : '"+user.getUserName()+"' " +
					"\n errorcode :"+value.split(Constants.ADDR_COL_DELIMETER)[0] +" \n message :"+value.split(Constants.ADDR_COL_DELIMETER)[1] , "color:red;");
			
			
		}
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ",e);
		} catch (ProtocolException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ",e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ",e);
		}catch (Exception e) {
			// TODO: handle exception
			MessageUtil.setMessage("Poblem while creating CIM profile for user : '"+user.getUserName()+"' " , "color:red;");
			
		}
		
		
	
		
	}
	
	
public String parseForSuccessResponse(String response) {
		
		/**
		 * ﻿<?xml version="1.0" encoding="utf-8"?>
		 * <createCustomerProfileResponse xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema"
		 *  xmlns="AnetApi/xml/v1/schema/AnetApiSchema.xsd">
		 *  <messages>
		 *  <resultCode>Ok</resultCode>
		 *  <message><code>I00001</code>
		 *  <text>Successful.</text></message></messages>
		 *  <customerProfileId>80244827</customerProfileId>
		 *  <customerPaymentProfileIdList /><customerShippingAddressIdList /><validationDirectResponseList /></createCustomerProfileResponse>
		 */

		
		Node node = null;
		Node childNode = null;
		Element element = null;
		
		String code = "";
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(new ByteArrayInputStream(response.getBytes()));
			doc.getDocumentElement().normalize();
			
			Element docElement = doc.getDocumentElement();
			//<resultCode>Ok</resultCode>

			NodeList nodeLst = doc.getElementsByTagName("messages");//given to each <MESSAGE> tag
			for(int i=0; i<nodeLst.getLength(); i++) {
				
				
				node = nodeLst.item(i);
				element = (Element)node;
				//guid = element.getAttribute("GUID");
				//sentId = element.getAttribute("ID");//sent id in our SMSCampaignSent
				
				//logger.info("sent id is===>"+sentId+"node is====>"+node.toString());
				
				if(element.hasChildNodes()) {
					
					
					NodeList childNodeList = element.getChildNodes();
					for(int j=0; j<childNodeList.getLength(); j++) {
						
						
						childNode = childNodeList.item(j);
						//logger.debug("my childNode is=====>"+childNode.getNodeName());
						
						if(childNode.getNodeName().equals("resultCode")) {
							
							if( childNode.getTextContent().trim().equalsIgnoreCase("OK")){
								NodeList profileNodes = doc.getElementsByTagName("customerProfileId");
								if(profileNodes != null && profileNodes.getLength() > 0) {
									for(int p=0; p<profileNodes.getLength(); p++) {
										
										
										node = profileNodes.item(p);
										element = (Element)node;
										
										return element.getTextContent();
										
									}
								}
							}
						}
				
						if(childNode.getNodeName().equals("message")) { //it is error
							
							return (childNode.getFirstChild().getTextContent() +Constants.ADDR_COL_DELIMETER+childNode.getLastChild().getTextContent());
							
						}
							
							
					}
				}
					
			}		
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ",e);
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ",e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ",e);
		}catch (Exception e) {
			// TODO: handle exception
			logger.error("Exception ",e);
		}
		
		return code;
		
		
	}//parseForSuccessResponse
	
	
//******************  ROLES & GROUPS  ******************************

	public void onSelect$rolesRGLBId() {
		
		logger.info("rolesRGLBId selected");
		Set<Listitem> selLiSet = rolesRGLBId.getSelectedItems();

		SecRoles tempRole=null;
		SecGroups tempGroup=null;
		
		Set<SecGroups> selRoleGroups = new HashSet<SecGroups>();
		
		for (Listitem eachLi : selLiSet) {
			tempRole = (SecRoles)eachLi.getValue();
			if(tempRole==null) {
				logger.info("SecRole is null ==="+eachLi.getChildren().size());
				continue;
			}
			
			List<SecGroups> groupsList = secGroupsDao.findByRoleId(tempRole.getRole_id());
			
			if(groupsList==null || groupsList.isEmpty()) continue;
			
			Iterator<SecGroups> groupsIt = groupsList.iterator();
			while(groupsIt.hasNext()) {
				tempGroup = groupsIt.next();
				//logger.info(tempRole.getName()+"="+tempGroup.getName());
				selRoleGroups.add(tempGroup);
				
			} // while
		} // for

		Set<String> selGroupNameSet = new HashSet<String>();

		for (SecGroups secGroup : selRoleGroups) {
			selGroupNameSet.add(secGroup.getName());
		} // for
		
		List<Listitem> groupsList = groupsRGLBId.getItems();
		for (Listitem eachLi : groupsList) {
			tempGroup = (SecGroups)eachLi.getValue();
			if(selGroupNameSet.contains(tempGroup.getName())) {
				eachLi.setSelected(true);
				//eachLi.setVisible(true);
			}
			else {
				eachLi.setSelected(false);
			}
		} // for

		Utility.filterListboxByListitems(groupsRGLBId, false);
	} // onSelect$usersLBId

	
	public void onClick$saveGroupChangesBtnId() {
		Set<Listitem> selRolesSet = rolesRGLBId.getSelectedItems();
		Set<Listitem> selGroupsSet = groupsRGLBId.getSelectedItems();
		
		if(selRolesSet==null || selRolesSet.isEmpty()) {
			MessageUtil.setMessage("Please select Roles & Groups to be associated.", "red");
			return;
		}
		
		String msgStr = "Confirm to save the changes for the selected Role ?";
		if(selGroupsSet==null || selGroupsSet.isEmpty()) {
			msgStr = "Confirm to delete all the Groups for the selected Role ?";
		}
		if(Messagebox.show(msgStr, "Confirm", 
				Messagebox.YES|Messagebox.NO, Messagebox.QUESTION) != Messagebox.YES) {
			return;
		}
		
		Set<SecGroups> secGroupsSet = new HashSet<SecGroups>();
		SecGroups secGroup=null;
		Iterator<Listitem> groupsIt = selGroupsSet.iterator();
		while(groupsIt.hasNext()) {
			secGroup = (SecGroups)groupsIt.next().getValue();
			secGroupsSet.add(secGroup);
		}
		
		SecRoles tempRole=null;
		Iterator<Listitem> rolesIt = selRolesSet.iterator();
		while(rolesIt.hasNext()) {
			tempRole = (SecRoles)rolesIt.next().getValue();
			tempRole.setGroupsSet(secGroupsSet);
			secRolesDaoForDML.saveOrUpdate(tempRole);
		}
	}
	

//******************  GROUPS & RIGHTS  ******************************

	public void onSelect$groupsGRLBId() {
		
		logger.info("groupsGRLBId selected");
		Set<Listitem> selLiSet = groupsGRLBId.getSelectedItems();

		SecGroups tempGroup=null;
		SecRights tempRight=null;
		
		Set<SecRights> selGroupRights = new HashSet<SecRights>();
		
		for (Listitem eachLi : selLiSet) {
			tempGroup = (SecGroups)eachLi.getValue();
			if(tempGroup==null) {
				logger.info("SecGroup is null ==="+eachLi.getChildren().size());
				continue;
			}
			
			List<SecRights> rightsList = secRightsDao.findByGroupId(tempGroup.getGroup_id());
			
			if(rightsList==null || rightsList.isEmpty()) continue;
			
			Iterator<SecRights> rightsIt = rightsList.iterator();
			while(rightsIt.hasNext()) {
				tempRight = rightsIt.next();
				selGroupRights.add(tempRight);
			} // while
		} // for

		Set<String> selRightNameSet = new HashSet<String>();

		for (SecRights secRight : selGroupRights) {
			selRightNameSet.add(secRight.getName());
		} // for
		
		List<Listitem> rightsList = rightsGRLBId.getItems();
		for (Listitem eachLi : rightsList) {
			tempRight = (SecRights)eachLi.getValue();
			if(selRightNameSet.contains(tempRight.getName())) {
				eachLi.setSelected(true);
				//eachLi.setVisible(true);
			}
			else {
				eachLi.setSelected(false);
			}
		} // for
		
		Utility.filterListboxByListitems(rightsGRLBId, false);

	} // onSelect$usersLBId

	
	public void onClick$saveRightChangesBtnId() {
		Set<Listitem> selGroupsSet = groupsGRLBId.getSelectedItems();
		Set<Listitem> selRightsSet = rightsGRLBId.getSelectedItems();
		
		if(selGroupsSet==null || selGroupsSet.isEmpty()) {
			MessageUtil.setMessage("Please select Groups & Rights to be associated.", "red");
			return;
		}
		
		String msgStr = "Confirm to save the changes for the selected group ?";
		if(selRightsSet==null || selRightsSet.isEmpty()) {
			msgStr = "Confirm to delete all the rights for the selected group ?";
		}
		if(Messagebox.show(msgStr, "Confirm", 
				Messagebox.YES|Messagebox.NO, Messagebox.QUESTION) != Messagebox.YES) {
			return;
		}
		
		Set<SecRights> secRightsSet = new HashSet<SecRights>();
		SecRights secRight=null;
		Iterator<Listitem> rightsIt = selRightsSet.iterator();
		while(rightsIt.hasNext()) {
			secRight = (SecRights)rightsIt.next().getValue();
			secRightsSet.add(secRight);
		}
		
		SecGroups tempGroup=null;
		Iterator<Listitem> groupsIt = selGroupsSet.iterator();
		while(groupsIt.hasNext()) {
			tempGroup = (SecGroups)groupsIt.next().getValue();
			tempGroup.setRightsSet(secRightsSet);
			//secGroupsDao.saveOrUpdate(tempGroup);
			secGroupsDaoForDML.saveOrUpdate(tempGroup);
		}
	}
		
//***************  DELETE Roles  ********************
		
	public void onClick$deleteRoleBtnId() {
		
		Set<Listitem> selRolesSet = rolesURLBId.getSelectedItems();
		
		if(selRolesSet==null || selRolesSet.isEmpty()) {
			MessageUtil.setMessage("Please select Roles to be deleted.", "red");
			return;
		}
		
		if(Messagebox.show("Confirm to delete the selected roles?", "Confirm", 
				Messagebox.YES|Messagebox.NO, Messagebox.QUESTION) != Messagebox.YES) {
			return;
		}
		
		boolean deletedFlag=false;
		SecRoles tempRole=null;
		List<Users> tempUsersSet=null;
		List<SecGroups> tempGroupsSet=null;
		String igGroupsStr="";
		
		Iterator<Listitem> rolesIt = selRolesSet.iterator();
		while(rolesIt.hasNext()) {
			tempRole = (SecRoles)rolesIt.next().getValue();
			long  rolesId= tempRole.getRole_id();
			SecRoles secRoles = secRolesDao.findByRolesId(rolesId);
			logger.info("role type is "+secRoles.getType());
			
			if(secRoles.getType()!= null && ! secRoles.getType().equals(Constants.SECROLE_TYPE_CUSTOM)){
				/*MessageUtil.setMessage("Role: "+tempRole.getName()+" Cannot be deleted," +
						"\r\n"+tempRole.getName()+"  is type of Admin .", "red");
				return;*/

				igGroupsStr += "\n"+tempRole.getName()+" : Reason : "+tempRole.getName()+" is  type of All";
				continue;
				
			
		}
			
			tempGroupsSet = secGroupsDao.findByRoleId(tempRole.getRole_id());
			if(tempGroupsSet!=null && tempGroupsSet.size() > 0) {
				/*MessageUtil.setMessage("Role: "+tempRole.getName()+" is associated to Group," +
						"\r\n try after removing the association.", "red");
				return;*/
				igGroupsStr += "\n"+tempRole.getName()+" : Reason : is associated to Group";
				continue;
			}
			
			tempUsersSet = usersDao.findByRoleId(tempRole.getRole_id());
			if(tempUsersSet!=null && !tempUsersSet.isEmpty()) {
				/*MessageUtil.setMessage("Role: "+tempRole.getName()+" is associated to Users," +
						"\r\n try after removing the association.", "red");
				return;*/
				igGroupsStr += "\n"+tempRole.getName()+" : Reason : is associated to Users";
				continue;
			}
			
			secRolesDaoForDML.delete(tempRole.getRole_id());
			deletedFlag=true;
		}
		
		if(deletedFlag) {
			populateRoles(rolesURLBId);
			rolesRGFetched=false;
		}
		if(!igGroupsStr.isEmpty()) {
			MessageUtil.setMessage("Some Roles deletion is ignored : "+igGroupsStr, "red");
			
		}
		
	} //
	
	//***************  DELETE Groups  ********************
	
		public void onClick$deleteGroupBtnId() {
			Set<Listitem> selGroupsSet = groupsRGLBId.getSelectedItems();
			if(selGroupsSet==null || selGroupsSet.isEmpty()) {
				MessageUtil.setMessage("Please select Groups to be deleted.", "red");
				return;
			}
			
			
			if(Messagebox.show("Confirm to delete the selected groups?", "Confirm", 
					Messagebox.YES|Messagebox.NO, Messagebox.QUESTION) != Messagebox.YES) {
				return;
			}
			
			boolean deletedFlag=false;
			SecGroups tempGroup=null;
			List<SecRoles> tempRolesSet=null;
			List<SecRights> tempRightsSet=null;
		
			String igGroupsStr="";
			
			Iterator<Listitem> groupsIt = selGroupsSet.iterator();
			while(groupsIt.hasNext()) {
				tempGroup = (SecGroups)groupsIt.next().getValue();
				long group_id= tempGroup.getGroup_id();
				SecGroups secGroups = secGroupsDao.findByGroupId(group_id);
				logger.info("role type is "+secGroups.getType());
				
				if(secGroups.getType() != null &&secGroups.getType().equals(Constants.SECGROUP_TYPE_ADMIN)){
					/*MessageUtil.setMessage("Group: "+tempGroup.getName()+" Cannot be deleted," +
							"\r\n"+tempGroup.getName()+"  is type of Admin .", "red");*/

					igGroupsStr += "\n"+tempGroup.getName()+" : Reason : Admin type";
					continue;
				}
				
				
				tempRightsSet = secRightsDao.findByGroupId(tempGroup.getGroup_id());
				
				if(tempRightsSet!=null && tempRightsSet.size()>0) {
					/*MessageUtil.setMessage("Group: "+tempGroup.getName()+" is associated to rights," +
							"\r\n try after removing the association.", "red");*/
					igGroupsStr += "\n"+tempGroup.getName()+" : Reason : associated to rights";
					continue;
				}
				
				tempRolesSet = secRolesDao.findByGroupId(tempGroup.getGroup_id());
				if(tempRolesSet!=null && !tempRolesSet.isEmpty()) {
					/*MessageUtil.setMessage("Group: "+tempGroup.getName()+" is associated to Roles," +
							"\r\n try after removing the association.", "red");*/
					igGroupsStr += "\n"+tempGroup.getName()+" : Reason : associated to Roles";
					continue;
				}
				
				//secGroupsDao.delete(tempGroup.getGroup_id());
				secGroupsDaoForDML.delete(tempGroup.getGroup_id());
				deletedFlag=true;
			}

			if(deletedFlag) {
				populateGroups(groupsRGLBId);
				groupsGRFetched=false;
			}
			
			if(!igGroupsStr.isEmpty()) {
				MessageUtil.setMessage("Some Groups deletion is ignored : "+igGroupsStr, "red");
				
			}
			
		} //
		
	
	public List<Users> getAllUsers() {
		List<Users> usersList = usersDao.findOrderbyAllUsers();
		logger.info("usersList size is :"+usersList.size());
		return usersList;
	}
	
	public List<SecRoles> getAllRoles() {
		List<SecRoles> rolesList = secRolesDao.findAllOrderByName();
		logger.info("RolesList size is :"+rolesList.size());
		return rolesList;
	}
	
	public List<SecGroups> getAllGroups() {
		List<SecGroups> list = secGroupsDao.findAllOrderByName();
		logger.info("Groups size is :"+list.size());
		return list;
	}
	
	public List<SecRights> getAllRights() {
		List<SecRights> list = secRightsDao.findAllOrderByName();
		logger.info("Rights size is :"+list.size());
		
		Set<String> rightNames = new HashSet<String>();
		for (SecRights secRights : list) {
			rightNames.add(secRights.getName());
		}
		
		List<SecRights> newRightsList = new ArrayList<SecRights>();
		
		SecRights newRight=null;
		
		RightsEnum allRights[] = RightsEnum.values();
		for (RightsEnum eachRight : allRights) {
			
			if(!rightNames.contains(eachRight.name())) {
				newRight = new SecRights();
				
				newRight.setRight_id(eachRight.getRight_id());
				newRight.setName(eachRight.name());
				newRight.setDescription(eachRight.getDescription());
				newRight.setType(eachRight.getType());
				newRightsList.add(newRight);
			} // if
		} // for
		
		if(!newRightsList.isEmpty()) {
			try {
				secRightsDaoForDML.saveByCollection(newRightsList);
				list = secRightsDao.findAllOrderByName();
				logger.info("Rights size is After inserting new :"+list.size());
			} catch (Exception e) {
				logger.error("Exception  ::", e);
			}
		}
		
		return list;
	}
	
	
	private void populateURUsers() {
		List<Users> usersList = getAllUsers();
		
		int count=usersURLBId.getItemCount();
		for (int i = count-1; i >=0 ; i--) {
			usersURLBId.removeItemAt(i);
		}
		
		Listitem li=null;
		for (Users eachUser : usersList) {
			li = new Listitem();
			li.setValue(eachUser);
			li.appendChild(new Listcell());
			new Listcell(Utility.getOnlyUserName(eachUser.getUserName())).setParent(li);
			new Listcell(Utility.getOnlyOrgId(eachUser.getUserName())).setParent(li);
			li.setParent(usersURLBId);
		}
	}
	
	/**
	 * 
	 */
	private void populateRoles(Listbox lbox) {
		List<SecRoles> rolesList = getAllRoles();
		
		int count=lbox.getItemCount();
		for (int i = count-1; i >=0 ; i--) {
			lbox.removeItemAt(i);
		}
		
		Listitem li=null;
		for (SecRoles eachRoles : rolesList) {
			li = new Listitem();
			li.setValue(eachRoles);
			li.appendChild(new Listcell());
			new Listcell(eachRoles.getName()).setParent(li);
			new Listcell(eachRoles.getDescription()).setParent(li);
			li.setParent(lbox);
		}
	}

	
	/**
	 * 
	 */
	private void populateGroups(Listbox lbox) {
		List<SecGroups> list = getAllGroups();
		
		int count=lbox.getItemCount();
		for (int i = count-1; i >=0 ; i--) {
			lbox.removeItemAt(i);
		}
		
		Listitem li=null;
		for (SecGroups eachGroup : list) {
			li = new Listitem();
			li.setValue(eachGroup);
			li.appendChild(new Listcell());
			new Listcell(eachGroup.getName()).setParent(li);
			new Listcell(eachGroup.getDescription()).setParent(li);
			li.setParent(lbox);
		}
	}	
	
	/**
	 * 
	 */
	private void populateRights() {
		List<SecRights> list = getAllRights();
		
		int count=rightsGRLBId.getItemCount();
		for (int i = count-1; i >=0 ; i--) {
			rightsGRLBId.removeItemAt(i);
		}
		
		Listitem li=null;
		for (SecRights eachRight : list) {
			li = new Listitem();
			li.setValue(eachRight);
			li.appendChild(new Listcell());
			new Listcell(eachRight.getName()).setParent(li);
			new Listcell(eachRight.getType()).setParent(li);
			new Listcell(eachRight.getDescription()).setParent(li);
			li.setParent(rightsGRLBId);
		}
		
	}
}

