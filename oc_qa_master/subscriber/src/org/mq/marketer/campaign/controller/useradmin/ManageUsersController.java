package org.mq.marketer.campaign.controller.useradmin;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.EmailQueue;
import org.mq.marketer.campaign.beans.SecRoles;
import org.mq.marketer.campaign.beans.UserSMSGateway;
import org.mq.marketer.campaign.beans.UserSMSSenderId;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.beans.UsersDomains;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.EmailQueueDao;
import org.mq.marketer.campaign.dao.EmailQueueDaoForDML;
import org.mq.marketer.campaign.dao.SecRolesDao;
import org.mq.marketer.campaign.dao.UserSMSGatewayDao;
import org.mq.marketer.campaign.dao.UserSMSGatewayDaoForDML;
import org.mq.marketer.campaign.dao.UserSMSSenderIdDao;
import org.mq.marketer.campaign.dao.UserSMSSenderIdDaoForDML;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.dao.UsersDaoForDML;
import org.mq.marketer.campaign.dao.UsersDomainsDao;
import org.mq.marketer.campaign.dao.UsersDomainsDaoForDML;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.GridFilterEventListener;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PageListEnum;
import org.mq.marketer.campaign.general.PageUtil;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Redirect;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.utils.OCConstants;
import org.zkoss.zk.ui.Component;
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
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Image;
import org.zkoss.zul.Include;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Popup;
import org.zkoss.zul.Row;
import org.zkoss.zul.RowRenderer;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

public class ManageUsersController extends GenericForwardComposer {

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	private UsersDao usersDao;
	private UsersDaoForDML usersDaoForDML;
	private UsersDomains usersDomains;
	private UsersDomainsDao usersDomainsDao;
	private UsersDomainsDaoForDML usersDomainsDaoForDML;
	private Users currentUser;
	private Grid customersGridId;
	private Session session;
	private SecRoles secRoles;
	private SecRolesDao secRolesDao;
	private EmailQueueDao emailQueueDao;
	private EmailQueueDaoForDML emailQueueDaoForDML;
	private Popup changePwdPopupId;
	private Textbox changePwdTbId;
	private final String ROLE_BASIC = "Basic User";
	private final String ROLE_POWER = "Power User";
	private Div creditsDivId;

	private Textbox searchBoxId, domainTbId;
	private Listbox userByLbId, filterOptionLbId;
	List<UsersDomains> domainsList;
//	List<Users> usersList;
	List<Map<String, Object>> usersList;
	List<SecRoles> userRolesList;
	List<SecRoles> userRolesStandardList;
	List<SecRoles> userRolesListStandard;
	List<SecRoles> userRolesNameList;
	// String domainIds = "";
	String roleIds = "";
	Map<Long, String> mapObj;
	private boolean isAdmin;

	// added after sms settings

	private Textbox userNameTbId, passwordTbId, rePasswordTbId, emailIdTbId, firstNameTbId, lastNameTbId;
	private Intbox emailLimitIntBoxId, SMSimitIntBoxId;
	private Checkbox activateUserChkBoxId, enableSMSChkId;
	private Users user;
	private String type;
	private Window createDomainWinId;
	private Textbox createDomainWinId$domainNameTbId;
	private Label createDomainWinId$msgLblId;
	private Listbox domainLbId, RoleLbId;
	private Label isPwrUserExistsLblId;
	private Textbox addressOneTbId, addressTwoTbId, cityTbId, stateTbId, countryTbId, pinTbId, phoneTbId;
	private Tabbox manageUsersTabBoxId;
	private Button createUserBtnId, saveUserBtnId, editUserBtnId, backBtnId;

	private boolean isValidUserName;
	private Label nameStatusLblId;

	private Div smsDivId, landingPageDivId, radioDivId;

	private UserSMSGatewayDao userSMSGatewayDao;
	private UserSMSGatewayDaoForDML userSMSGatewayDaoForDML;
	private UserSMSSenderIdDao userSMSSenderIdDao;
	private UserSMSSenderIdDaoForDML userSMSSenderIdDaoForDML;

	public ManageUsersController() {

		usersDao = (UsersDao) SpringUtil.getBean("usersDao");
		usersDaoForDML = (UsersDaoForDML) SpringUtil.getBean("usersDaoForDML");
		currentUser = GetUser.getUserObj();
		usersDomainsDao = (UsersDomainsDao) SpringUtil.getBean("usersDomainsDao");
		usersDomainsDaoForDML = (UsersDomainsDaoForDML) SpringUtil.getBean("usersDomainsDaoForDML");
		session = Sessions.getCurrent();
		emailQueueDao = (EmailQueueDao) SpringUtil.getBean("emailQueueDao");
		emailQueueDaoForDML = (EmailQueueDaoForDML) SpringUtil.getBean("emailQueueDaoForDML");
		secRolesDao = (SecRolesDao) SpringUtil.getBean("secRolesDao");

		userSMSGatewayDao = (UserSMSGatewayDao) SpringUtil.getBean("userSMSGatewayDao");
		userSMSGatewayDaoForDML = (UserSMSGatewayDaoForDML) SpringUtil.getBean("userSMSGatewayDaoForDML");

		userSMSSenderIdDao = (UserSMSSenderIdDao) SpringUtil.getBean("userSMSSenderIdDao");
		userSMSSenderIdDaoForDML = (UserSMSSenderIdDaoForDML) SpringUtil.getBean("userSMSSenderIdDaoForDML");

		String style = "font-weight:bold;font-size:15px;color:#313031;font-family:Arial,Helvetica,sans-serif;align:left";
		PageUtil.setHeader("Manage Users", "", style, true);

	}

	private RowRenderer renderer = new MyRowRenderer();

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		// TODO Auto-generated method stub
		super.doAfterCompose(comp);
		type = (String) session.removeAttribute("manageUserType");
		user = (Users) session.removeAttribute("manageUser");

		// domainsList =
		// usersDomainsDao.FindByOrgId(currentUser.getUserOrganization().getUserOrgId());
		
		/*
		 * String company = currentUser.getCompanyName();
		 * 
		 * if (company != null) { domainTbId.setValue(company);
		 * domainTbId.setDisabled(true);
		 * 
		 * }
		 */

		List<UsersDomains> domainsList = usersDao.getAllDomainsByUser(currentUser.getUserId());

		/*
		 * logger.info("domainList ::"+((UsersDomains)domainList.get(0)).getDomainId());
		 * if(true) return;
		 */

		if (domainsList == null || domainsList.size() == 0) {
			logger.debug("No Domains found for user :" + currentUser.getUserId());
			return;
		}

		Listitem domainItem;
		String domainIds = "";

		for (UsersDomains usersDomains : domainsList) {

			domainItem = new Listitem(usersDomains.getDomainName(), usersDomains);
			domainItem.setParent(domainLbId);

			if (domainIds.length() > 0)
				domainIds += ",";
			domainIds += usersDomains.getDomainId();

		}

		if (domainLbId.getItemCount() > 0) {
			domainLbId.setSelectedIndex(0);
		}
		

		// usersList =
		// usersDao.getUsersByOrg(currentUser.getUserOrganization().getUserOrgId());

		setUserInfo();

		List<Map<String, Object>> usersRolesMapsList = usersDao.getUsersByDomain(domainIds);
		List<Map<String, Object>> userRoleStoreOpList = usersDao.getUsersByDomainByRole(domainIds);
		List<Map<String, Object>> userRoleStandardOpList = usersDao.getUsersByStandardRole(domainIds);

		usersRolesMapsList.addAll(userRoleStoreOpList);
		usersRolesMapsList.addAll(userRoleStandardOpList);
		
		
		 
		 

		if (usersRolesMapsList != null && usersRolesMapsList.size() > 0) {

			String userIdStr = "";
			mapObj = new HashMap<Long, String>();
			for (Map<String, Object> map : usersRolesMapsList) {

				Long userid = (Long) map.get("user_id");
				String roleName = (String) map.get("name");
				// logger.info("rolename is "+roleName);
				mapObj.put(userid, roleName);

				// logger.info(" map obj is "+mapObj);

				if (userIdStr.length() > 0)
					userIdStr += ",";
				userIdStr += userid.longValue();
				// logger.info("userIdStr is "+userIdStr);
			}

			List<Users> userList = usersDao.findByuserId(userIdStr);
			customersGridId.setRowRenderer(renderer);
			customersGridId.setModel(new ListModelList(userList));

			GridFilterEventListener.gridFilterSetup(customersGridId);

		}
		/*
		 * Long orgOwnerId =
		 * usersDao.getOwnerofOrg(currentUser.getUserOrganization().getUserOrgId());
		 * Users orgOwner = usersDao.findByUserId(orgOwnerId); List<UsersDomains>
		 * domainsLists = usersDao.getAllDomainsByUser(orgOwner.getUserId());
		 */
		
		String userDomainStr = "";
		 Set<UsersDomains> domainSet = new HashSet<UsersDomains>();
			
			domainSet.addAll(domainsList);//user.getUserDomains();
		 
		 
		 for (UsersDomains usersDomains : domainSet) {
			
			if (userDomainStr.length() > 0)
				userDomainStr += ",";
			 userDomainStr += usersDomains.getDomainName();
			 domainTbId.setValue(userDomainStr);
				domainTbId.setDisabled(true);

		}
		 


		// if (userObj == null) {
		String types = "'Standard'";
		String RoleStandard = "Standard_User";
		String type = "'Custom'";
		String RoleName = "StoreOperator";
		// userRolesList = secRolesDao.findRoleByType(types);
		// boolean
		// manageStoreOperator=currentUser.getUserOrganization().isManageStoreOperator();
		// if(manageStoreOperator==true) {
		userRolesList = secRolesDao.findRoleStoreByType(types, RoleStandard);
		userRolesNameList = secRolesDao.findRoleStoreByType(type, RoleName);

		/*
		 * if (userRolesNameList != null && !userRolesNameList.isEmpty()) {
		 * userRolesNameList.get(0).setName("Store Operator"); //
		 * userRolesList.addAll(userRolesNameList); }
		 */
		userRolesList.addAll(userRolesNameList);
		// }

		if (userRolesList == null || userRolesList.size() == 0) {
			logger.debug("No Roles found for user :" + currentUser.getUserId());
			// return;
		}
		Listitem roleItem;
		for (SecRoles secRoles : userRolesList) {

			roleItem = new Listitem(secRoles.getName(), secRoles);
			roleItem.setParent(RoleLbId);

			/*
			 * if(roleIds.length() > 0) roleIds += ","; roleIds += secRoles.getRole_id();
			 */

		}

		
	}

	public void onClick$addNewDomainAnchId() {

		createDomainWinId.doModal();
		createDomainWinId$domainNameTbId.setValue("");
		createDomainWinId$msgLblId.setValue("");

	}

	public void onSelect$RoleLbId() {

		long domainId = ((UsersDomains) domainLbId.getSelectedItem().getValue()).getDomainId().longValue();

		SecRoles secRoles = (SecRoles) RoleLbId.getSelectedItem().getValue();
		String role = secRoles.getName();
		// logger.info(" role name is "+role);

		if (role.equalsIgnoreCase("Power User")) {

			List<Map<String, Object>> tempList = usersDao.findPowerUsersOfSelDomain(domainId);

			if (tempList.size() == 0) {

				isPwrUserExistsLblId.setValue("");
				return;
			}

			for (Map<String, Object> tempRec : tempList) {

				if (type != null && user != null
						&& ((Long) tempRec.get("user_id")).longValue() != user.getUserId().longValue()) {

					RoleLbId.setSelectedIndex(0);
					// Messagebox.show("Power user already exists in the selected organization
					// unit.");
					isPwrUserExistsLblId.setValue("Power user already exists in the selected organization unit.");
					break;
				} else if (type == null && user == null) {
					// isPwrUserExistsLblId.setValue("");

					RoleLbId.setSelectedIndex(0);
					// Messagebox.show("Power user already exists in the selected organization
					// unit.");
					isPwrUserExistsLblId.setValue("Power user already exists in the selected organization unit.");
					break;

				} else {
					isPwrUserExistsLblId.setValue("");

				}
			} // for

		} else if (role.equalsIgnoreCase("Basic User")) {

			isPwrUserExistsLblId.setValue("");
		}else if (role.equalsIgnoreCase("Standard_User")) {

			isPwrUserExistsLblId.setValue("");
		} else if (role.equalsIgnoreCase("StoreOperator")) {

			isPwrUserExistsLblId.setValue("");
		}

	}

	public void onSelect$domainLbId() {
		logger.debug("---just entered ----");
		long domainId = ((UsersDomains) domainLbId.getSelectedItem().getValue()).getDomainId().longValue();
		SecRoles secRoles = (SecRoles) RoleLbId.getSelectedItem().getValue();
		String role = secRoles.getName();
		// String role = (String)RoleLbId.getSelectedItem().getValue();

		if (role.equalsIgnoreCase("Power User")) {

			List<Map<String, Object>> tempList = usersDao.findPowerUsersOfSelDomain(domainId);

			if (tempList.size() == 0) {

				isPwrUserExistsLblId.setValue("");
				return;
			}

			for (Map<String, Object> tempRec : tempList) {

				if (type != null && user != null
						&& ((Long) tempRec.get("user_id")).longValue() != user.getUserId().longValue()) {

					RoleLbId.setSelectedIndex(0);
					// Messagebox.show("Power user already exists in the selected organization
					// unit.");
					isPwrUserExistsLblId.setValue("Power user already exists in the selected organization unit.");
					break;
				} else if (type == null && user == null) {
					// isPwrUserExistsLblId.setValue("");

					RoleLbId.setSelectedIndex(0);
					// Messagebox.show("Power user already exists in the selected organization
					// unit.");
					isPwrUserExistsLblId.setValue("Power user already exists in the selected organization unit.");
					break;

				}
			} // for

		} // if
		else if (role.equalsIgnoreCase("Basic User")) {

			isPwrUserExistsLblId.setValue("");
		} else if (role.equalsIgnoreCase("Standard_User")) {

			// landingPageDivId.setVisible(false);
			// radioDivId.setVisible(false);

			isPwrUserExistsLblId.setValue("");
		} else if (role.equalsIgnoreCase("StoreOperator")) {

			isPwrUserExistsLblId.setValue("");
		}

	}// onSelect$domainLbId()

	public void onClick$saveDomainBtnId$createDomainWinId() {

		String domainName = createDomainWinId$domainNameTbId.getValue();
		// UsersDomains usersDomains = null;
		// String userName =
		// userNameTbId.getValue().trim()+"__org__"+currentUser.getUserOrganization().getOrgExternalId();

		domainName = Utility.condense(domainName);

		if (domainName == null || domainName.trim().length() == 0) {

			createDomainWinId$msgLblId.setValue("Please provide Organization Unit Name.");
			return;
		}

		if (!Utility.validateNames(domainName)) {
			createDomainWinId$msgLblId
					.setValue("Please provide Valid Organization Unit Name.special characters not allowed.");
			return;

		}

		String examineDomName = domainName;
		if (domainName.contains("'")) {

			examineDomName = domainName.replace("'", "\\'");

		}

		try {
			UsersDomainsDao usersDomainsDao = (UsersDomainsDao) SpringUtil.getBean("usersDomainsDao");
			int usersDomains = usersDomainsDao.findBydomainName(examineDomName,
					GetUser.getUserObj().getUserOrganization().getUserOrgId());// user name should be unique accross the
																				// organization
			if (usersDomains != 0) {
				createDomainWinId$msgLblId
						.setValue("Organization Unit Name is already exist,please provide another name.");
				return;
			}

		} catch (Exception e) {
			logger.error("Exception : " + e);
		}

		try {
			if (Messagebox.show("Are you sure you want to create the organization unit?", "Prompt",
					Messagebox.YES | Messagebox.NO, Messagebox.QUESTION) == Messagebox.NO) {
				return;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
		}

		usersDomains = new UsersDomains();
		usersDomains.setCreatedDate(Calendar.getInstance());
		usersDomains.setDomainManagerId(currentUser);
		usersDomains.setDomainName(domainName);
		usersDomains.setUserOrganization(currentUser.getUserOrganization());

		usersDomainsDaoForDML.saveOrUpdate(usersDomains);

		createDomainWinId.setVisible(false);

		Listitem newDomainItem = new Listitem(domainName, usersDomains);
		domainLbId.appendChild(newDomainItem);

		if (domainLbId.getItemCount() != 0)
			domainLbId.setSelectedIndex(0);

		// associating super user to created domains

		Set<UsersDomains> currDomainSet = new HashSet<UsersDomains>();
		// currentUser.getUserDomains();
		// can be declared globally
		List<UsersDomains> domainsList = usersDao.getAllDomainsByUser(currentUser.getUserId());

		currDomainSet.addAll(domainsList);

		currDomainSet.add(usersDomains);
		currentUser.setUserDomains(currDomainSet);
		// usersDao.saveOrUpdate(currentUser);
		usersDaoForDML.saveOrUpdate(currentUser);

	}

	public void onClick$cancelDomainBtnId$createDomainWinId() {

		createDomainWinId.setVisible(false);

	}

	/*
	 * public boolean validatePassword(){
	 * 
	 * //String pwd = passwordTbId.getValue().trim(); //String repwd =
	 * rePasswordTbId.getValue().trim();
	 * //logger.debug("the values of passwords are=======>"+pwd+"====="+repwd);
	 * 
	 * if( pwd.length() == 0 ) {
	 * 
	 * passwordTbId.setFocus(true); MessageUtil.setMessage("Please enter password.",
	 * "color:red;"); return false;
	 * 
	 * }
	 * 
	 * if( repwd.length() == 0 ) {
	 * 
	 * rePasswordTbId.setFocus(true);
	 * MessageUtil.setMessage("Please enter re-enter password.", "color:red;");
	 * return false;
	 * 
	 * }
	 * 
	 * if(pwd ==null || pwd.length() <= 3) { MessageUtil.setMessage(
	 * "Password must be greater than 3 characters.", "color:red;", "TOP");
	 * changePwdTbId.setStyle("border:1px solid #DD7870;"); return false; } if(repwd
	 * ==null || repwd.length() <= 3) { MessageUtil.setMessage(
	 * " Re-enter password must be greater than 3 characters.", "color:red;",
	 * "TOP"); changePwdTbId.setStyle("border:1px solid #DD7870;"); return false; }
	 * else if(!pwd.equals(repwd)) {
	 * 
	 * MessageUtil.setMessage("Both passwords must match.", "color:red;");
	 * passwordTbId.setFocus(true); rePasswordTbId.setFocus(true);
	 * 
	 * return false; }
	 * 
	 * return true;
	 * 
	 * }
	 */

	public boolean validateEmail() {
		logger.debug("----just entered2---" + user);

		String email = emailIdTbId.getValue();
		if (email.trim().length() == 0) {
			MessageUtil.setMessage("Please provide Email address.", "color:red;");
			return false;

		} // if

		if (!Utility.validateEmail(email.trim())) {

			MessageUtil.setMessage("Please provide valid Email address.", "color:red;");

			return false;

		} // if

		return true;

	}

	public boolean validateUser() {
		logger.debug("----just entered3---" + user);
		String firstName = firstNameTbId.getValue();

		if (firstName == null || firstName.trim().length() == 0) {

			MessageUtil.setMessage("Please provide First Name. First Name cannot be left empty.", "color:red;");
			return false;

		}

		/*
		 * if(domainLbId.getSelectedIndex() == -1) {
		 * MessageUtil.setMessage("Please select the Organization Unit.", "color:red;");
		 * return false;
		 * 
		 * }
		 */
		if (RoleLbId.getSelectedIndex() == -1) {

			MessageUtil.setMessage("Please select a role for the user. Role cannot be left empty.", "color:red;");
			return false;

		}
		if (addressOneTbId.getValue() == null || addressOneTbId.getValue().trim().length() == 0) {

			MessageUtil.setMessage("Please provide address one. Address one cannot be left empty.", "color:red;");
			return false;

		}
		if (cityTbId.getValue() == null || cityTbId.getValue().trim().length() == 0) {

			MessageUtil.setMessage("Please provide city. City cannot be left empty.", "color:red;");
			return false;

		}

		if (stateTbId.getValue() == null || stateTbId.getValue().trim().length() == 0) {

			MessageUtil.setMessage("Please provide state . State Name cannot be left empty.", "color:red;");
			return false;

		}
		if (countryTbId.getValue() == null || countryTbId.getValue().trim().length() == 0) {

			MessageUtil.setMessage("Please provide country . Country Name cannot be left empty.", "color:red;");
			return false;

		}

		if (!validateNum(pinTbId) || pinTbId.getValue().trim().length()==0) {
			MessageUtil.setMessage("Enter valid Zip Code.", "color:red;");
			return false;
		} // if

		/*
		 * String pin = pinTbId.getValue().trim(); if( (pin.length() > 6) ||
		 * (pin.length() < 5 ) ) {
		 * MessageUtil.setMessage("Enter only 5 / 6 digit value for the Zip Code."
		 * ,"color:red;"); return false;
		 * 
		 * }
		 */

		String value = phoneTbId.getValue().trim();

		// if(value != null && value.length() > 0 &&
		// (Utility.phoneParse(value,user.getUserOrganization())==null)) {
		if (value != null && value.length() > 0
				&& (Utility.phoneParse(value, currentUser.getUserOrganization()) == null)) {
			MessageUtil.setMessage(" Please provide valid Phone Number.", "color:red;");
			return false;
		}

		return true;

	}

	public void onCheck$enableSMSChkId() {

		smsDivId.setVisible(enableSMSChkId.isChecked());

	}

	public void onClick$createUserBtnId() {
		logger.debug("----just entered---" + user);

		// onBlur$userNameTbId();
		if (user == null && type == null && !isValidUserName) {
			MessageUtil.setMessage(
					"User Name field left empty or already exists. \n  Special characters are not allowed.",
					"color:red", "TOP");
			return;
		}

		/*
		 * if(user == null && type == null && !validatePassword()){ return; }
		 */

		if (!validateEmail()) {

			return;
		}

		if (!validateUser()) {
			return;
		}

		String userName = userNameTbId.getValue();
		String tempPwd = "";

		// logger.debug("----just entered4---"+userName);

		/*
		 * try { if (Messagebox.show("Are you sure you want to save the user?",
		 * "Prompt", Messagebox.YES|Messagebox.NO, Messagebox.QUESTION) ==
		 * Messagebox.NO) { return; } } catch (Exception e) { // TODO Auto-generated
		 * catch block logger.error("Exception ::" , e); }
		 */
		try {
			String saveMsg = "";
			int confirm = Messagebox.show("Are you sure you want to " + createUserBtnId.getLabel() + " the user?",
					"Prompt", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
			if (confirm == Messagebox.OK) {

				try {
					if (user == null && type == null) {
						// logger.debug("----just entered5---"+user);

						user = new Users();

						userName = userName + "__org__" + currentUser.getUserOrganization().getOrgExternalId();

						tempPwd = Long.toHexString(Double.doubleToLongBits(Math.random()));
						logger.debug("temppwd =====" + tempPwd);
						// String passwordHash = Utility.encryptPassword(userName, tempPwd);
						String passwordHash = Utility.encryptPassword(userName, tempPwd);
						// String passwordHash = Utility.encryptPassword(userName,
						// passwordTbId.getValue().trim());
						/*
						 * Md5PasswordEncoder md5 = new Md5PasswordEncoder(); String passwordHash =
						 * md5.encodePassword(passwordTbId.getValue().trim(),user.getUserName());
						 */

						user.setUserName(userName);
						user.setPassword(passwordHash);
						// user.setMandatoryUpdatePwdOn(Calendar.getInstance());
						user.setCreatedDate(Calendar.getInstance());
						user.setUsedEmailCount(0);
						user.setUsedSmsCount(0);
						user.setEmailCount(0);
						user.setloyaltyServicetype(currentUser.getloyaltyServicetype());

					}

					int availableCredits = currentUser.getEmailCount() - currentUser.getUsedEmailCount();

					Integer givenCredits = emailLimitIntBoxId.getValue();
					if (givenCredits == null) {

						givenCredits = 0;
					}

					if (givenCredits != null) {
						if (type == null) {
							availableCredits = availableCredits - givenCredits;

						} else if (type != null) {

							logger.debug("availableCredits::" + availableCredits + " user.getEmailCount()::"
									+ user.getEmailCount() + " user.getUsedEmailCount()::" + user.getUsedEmailCount()
									+ " givenCredits::" + givenCredits);

							if (user.getEmailCount().intValue() != givenCredits.intValue()) {

								availableCredits = availableCredits
										+ ((user.getEmailCount().intValue() - user.getUsedEmailCount().intValue())
												- givenCredits.intValue());

							}

						}

						if (availableCredits < 0) {
							MessageUtil.setMessage("Sufficient credits not found for the Super user.", "color:red;");
							return;
						}

						currentUser.setUsedEmailCount(currentUser.getEmailCount() - availableCredits);
						// usersDao.saveOrUpdate(currentUser);
						usersDaoForDML.saveOrUpdate(currentUser);
					}

					// added for sms settings for user if his parent user enabled sms setiings

					if (enableSMSChkId.isChecked()) {

						if (currentUser.isEnableSMS()) {
							user.setEnableSMS(enableSMSChkId.isChecked());
							smsDivId.setVisible(enableSMSChkId.isChecked());
						} else {

							MessageUtil.setMessage("Parent user does not have SMS settings", "color:red;");
							return;
						}
					}
					Integer givenSMSCredits = null;
					if (smsDivId.isVisible()) {

						int smsAvailableCount = currentUser.getSmsCount() - currentUser.getUsedSmsCount();

						givenSMSCredits = SMSimitIntBoxId.getValue();
						if (givenSMSCredits == null) {

							givenSMSCredits = 0;
						}

						if (givenSMSCredits != null) {
							if (type == null) {
								smsAvailableCount = smsAvailableCount - givenSMSCredits;

							} else if (type != null) {

								logger.debug("SMSavailableCredits::" + smsAvailableCount + " user.getSmsCount()::"
										+ user.getSmsCount() + " user.getUsedSmsCount()::" + user.getUsedSmsCount()
										+ " givenSMSCredits::" + givenSMSCredits);

								if (user.getSmsCount() != null) {

									if (user.getSmsCount().intValue() != givenSMSCredits.intValue()) {

										smsAvailableCount = smsAvailableCount
												+ ((user.getSmsCount().intValue() - user.getUsedSmsCount().intValue())
														- givenSMSCredits.intValue());

									}
								}

							}

							if (smsAvailableCount < 0) {
								MessageUtil.setMessage("Sufficient credits not found for the Super user.",
										"color:red;");
								return;
							}

							currentUser.setUsedSmsCount(currentUser.getSmsCount() - smsAvailableCount);
							// usersDao.saveOrUpdate(currentUser);
							usersDaoForDML.saveOrUpdate(currentUser);
						}

					}
					
					/*
					 * Long orgId = currentUser.getUserOrganization().getUserOrgId(); //if
					 * (session.getAttribute("selectedUserOrgId") != null)
					 * 
					 * orgId = (Long) session.getAttribute("selectedUserOrgId");
					 */

					Set<UsersDomains> userDomainset = new HashSet<UsersDomains>();
					userDomainset.add((UsersDomains) domainLbId.getSelectedItem().getValue());

					// logger.debug("----just entered6---"+user);

					// default details of the power user and basic user

					user.setEmailCount(givenCredits);

					// added for sms settings
					user.setSmsCount(givenSMSCredits == null ? 0 : givenSMSCredits);

					// ******************************
					user = setUserData(user, currentUser);

					Set<SecRoles> secRolesSet = new HashSet<SecRoles>();
					secRolesSet.add((SecRoles) RoleLbId.getSelectedItem().getValue());
					// SecRoles secRoles=(SecRoles)RoleLbId.getSelectedItem().getValue();
					// String userRole = secRoles.getName();
					SecRoles secRole = (SecRoles) RoleLbId.getSelectedItem().getValue();

					String userRole = secRole.getName();
					logger.info("user role is" + userRole);

//	String saveMsg = "";

					if (type == null) {
						// logger.debug("----just entered7---"+user);
						saveMsg = "Created";

						// SecRoles secRoles = new SecRoles();
						/*
						 * secRolesSet.add(secRoles);
						 * 
						 * userRole =secRoles.getName();
						 */
						// usersDao.saveRolesByCollection(secRolesSet);
						// usersDao.saveOrUpdate(user);
						usersDaoForDML.saveOrUpdate(user);

						user.setRoles(secRolesSet);
						user.setUserDomains(userDomainset);// domains will be set only after user's save.

						if (userRole.equalsIgnoreCase("Basic User")) {
							user.setAccountType(Constants.USER_ACCOUNT_TYPE_SHARED);
							// usersDao.saveOrUpdate(user);
							usersDaoForDML.saveOrUpdate(user);
							logger.info("account type b  is " + user.getAccountType());
						} else if (userRole.equalsIgnoreCase("Power User")) {
							user.setAccountType(Constants.USER_ACCOUNT_TYPE_PRIMARY);
							// usersDao.saveOrUpdate(user);
							usersDaoForDML.saveOrUpdate(user);
							logger.info("account type p  is " + user.getAccountType());

							

						}
						if (userRole.equalsIgnoreCase("Standard_User")) {
							user.setAccountType(Constants.USER_ACCOUNT_TYPE_STANDARD);
							// usersDao.saveOrUpdate(user);
							usersDaoForDML.saveOrUpdate(user);
							logger.info("account type s  is " + user.getAccountType());
						} // else

						if (userRole.equalsIgnoreCase("StoreOperator")) {
							user.setAccountType(Constants.USER_ACCOUNT_TYPE_CUSTOM);
							// usersDao.saveOrUpdate(user);
							usersDaoForDML.saveOrUpdate(user);
							logger.info("account type s  is " + user.getAccountType());
						} // else

						GetUser.checkUserFolders(userName);
						// if(user.getUserId()!=null)
						Utility.setUserDefaultMapping(user.getUserId());

					} else if (type != null) {

						// logger.debug("----just entered8---"+userName);
						// usersDao.saveOrUpdate(user);
						user.setloyaltyServicetype(currentUser.getloyaltyServicetype());
						usersDaoForDML.saveOrUpdate(user);

						user.setUserDomains(userDomainset);// domains will be set only after user's save.
						user.setRoles(secRolesSet);

						saveMsg = "Updated";
						String roleNameStr = "";
						List<SecRoles> userRoles = secRolesDao.findByUserId(user.getUserId());
						for (SecRoles secRoles : userRoles) {
							roleNameStr = secRoles.getName();
						}

						String existedUserRole = getUserByRole(roleNameStr);
						logger.debug("existingrole ::" + existedUserRole);
						if (existedUserRole != null) {

							if (!userRole.equals(existedUserRole)) {
								user.setAccountType(Constants.USER_ACCOUNT_TYPE_SHARED);
								// usersDao.saveOrUpdate(user);
								usersDaoForDML.saveOrUpdate(user);
								// usersDao.saveByCollection(sharedUsersList);

							} else if (existedUserRole.equalsIgnoreCase("Basic User")
									&& userRole.equalsIgnoreCase("Power User")) {

								user.setAccountType(Constants.USER_ACCOUNT_TYPE_PRIMARY);
								usersDaoForDML.saveOrUpdate(user);
								
							} else if (userRole.equalsIgnoreCase("Basic User")) {
								user.setAccountType(Constants.USER_ACCOUNT_TYPE_SHARED);
								// usersDao.saveOrUpdate(user);
								usersDaoForDML.saveOrUpdate(user);
								logger.info("account type b  is " + user.getAccountType());
							} else if (userRole.equalsIgnoreCase("Power User")) {
								user.setAccountType(Constants.USER_ACCOUNT_TYPE_PRIMARY);
								// usersDao.saveOrUpdate(user);
								usersDaoForDML.saveOrUpdate(user);
								logger.info("account type p  is " + user.getAccountType());

							}
							if (userRole.equalsIgnoreCase("Standard_User")) {
								user.setAccountType(Constants.USER_ACCOUNT_TYPE_STANDARD);
								// usersDao.saveOrUpdate(user);
								usersDaoForDML.saveOrUpdate(user);
								logger.info("account type s  is " + user.getAccountType());
							} // else

							if (userRole.equalsIgnoreCase("StoreOperator")) {
								user.setAccountType(Constants.USER_ACCOUNT_TYPE_CUSTOM);
								// usersDao.saveOrUpdate(user);
								usersDaoForDML.saveOrUpdate(user);
								logger.info("account type s  is " + user.getAccountType());
							} // else
							

							// usersDao.setAuthorities(user.getUserName(), existedUserRole,userRole);

						}

					}

					/*
					 * user.setCompanyName(currentUser.getUserOrganization().getOrganizationName());
					 * user.setAddressOne(currentUser.getAddressOne());
					 * 
					 * if(currentUser.getAddressTwo() != null) {
					 * 
					 * user.setAddressTwo(currentUser.getAddressTwo()); }
					 * user.setCity(currentUser.getCity()); user.setState(currentUser.getState());
					 * user.setCountry(currentUser.getCountry());
					 * user.setPinCode(currentUser.getPinCode());
					 * 
					 * if(currentUser.getPhone() != null) { user.setPhone(currentUser.getPhone()); }
					 * 
					 * 
					 * 
					 * user.setPackageStartDate(Calendar.getInstance());
					 * user.setPackageExpiryDate(currentUser.getPackageExpiryDate());
					 * user.setFooterEditor(currentUser.getFooterEditor());
					 * user.setVmta(currentUser.getVmta());
					 */

					// logger.debug("----just entered9---"+user);
					// usersDao.saveOrUpdate(user);
					usersDaoForDML.saveOrUpdate(user);

					// added sms settings after user creation

					List<UserSMSGateway> currUserSMSGatewaysList = userSMSGatewayDao.findAllByUserId(user.getUserId());

					List<UserSMSSenderId> currUserMSSenderIdList = userSMSSenderIdDao.findByUserId(user.getUserId());

					if (enableSMSChkId.isChecked()) {

						try {
							if (currentUser.isEnableSMS()) {

								List<UserSMSGateway> listTobeSaved = new ArrayList<UserSMSGateway>();

								if (currUserSMSGatewaysList == null) {

									List<UserSMSGateway> parentUserSMSGatewaysList = userSMSGatewayDao
											.findAllByUserId(currentUser.getUserId());

									UserSMSGateway mUserGateway = null;

									if (parentUserSMSGatewaysList.size() > 0) {

										for (UserSMSGateway userSMSGateway : parentUserSMSGatewaysList) {

											mUserGateway = new UserSMSGateway();
											mUserGateway.setUserId(user.getUserId());
											mUserGateway.setAccountType(userSMSGateway.getAccountType());
											mUserGateway.setOrgId(currentUser.getUserOrganization().getUserOrgId());
											mUserGateway.setGatewayId(userSMSGateway.getGatewayId());
											mUserGateway.setCreatedDate(Calendar.getInstance());
											mUserGateway.setCreatedBy(currentUser.getUserId());
											mUserGateway.setModifiedDate(Calendar.getInstance());
											mUserGateway.setModifiedBy(currentUser.getUserId());

											listTobeSaved.add(mUserGateway);

										}

									}

									if (listTobeSaved.size() > 0) {

										userSMSGatewayDaoForDML.saveByCollection(listTobeSaved);
									}
								}

								List<UserSMSSenderId> smsSenderIdlist = new ArrayList<UserSMSSenderId>();

								if (currUserMSSenderIdList == null) {

									List<UserSMSSenderId> parentUserSMSSenderIdList = userSMSSenderIdDao
											.findByUserId(currentUser.getUserId());

									UserSMSSenderId mUserSMSSenderId = null;

									if (parentUserSMSSenderIdList != null && parentUserSMSSenderIdList.size() > 0) {

										for (UserSMSSenderId userSMSSenderId : parentUserSMSSenderIdList) {

											mUserSMSSenderId = new UserSMSSenderId();

											mUserSMSSenderId.setUserId(user.getUserId());

											mUserSMSSenderId.setUserName(user.getUserName());

											mUserSMSSenderId.setSenderId(userSMSSenderId.getSenderId());
											mUserSMSSenderId.setSmsType(userSMSSenderId.getSmsType());

											smsSenderIdlist.add(mUserSMSSenderId);

										}

									}

									if (smsSenderIdlist.size() > 0) {

										userSMSSenderIdDaoForDML.saveByCollection(smsSenderIdlist);
									}

								}

							} else {

								MessageUtil.setMessage("Parent user does not have SMS settings", "color:red;");
								return;
							}
						} catch (Exception e) {
							logger.error("Exception :::", e);
							// logger.error("Exception",e);
						}

					} else {

						user.setEnableSMS(enableSMSChkId.isChecked());

						// usersDao.saveOrUpdate(user);
						usersDaoForDML.saveOrUpdate(user);

						try {
							// delete if user has sms gateways and sms sender ids

							if (currUserSMSGatewaysList != null) {

								for (UserSMSGateway userSMSGateway : currUserSMSGatewaysList) {

									// userSMSGatewayDao.delete(userSMSGateway);

									// userSMSGatewayDao.deleteBy(userSMSGateway.getId());
									userSMSGatewayDaoForDML.deleteBy(userSMSGateway.getId());

								}

							}

							if (currUserMSSenderIdList != null) {

								for (UserSMSSenderId userSMSSenderId : currUserMSSenderIdList) {

									// userSMSSenderIdDao.delete(userSMSSenderId);

									// userSMSSenderIdDao.deleteBy(userSMSSenderId.getId());
									userSMSSenderIdDaoForDML.deleteBy(userSMSSenderId.getId());
								}

							}
						} catch (Exception e) {
							// logger.error("Exception",e);
							logger.error("Exception :::", e);
						}

					}

					// logger.info(">>> type ::"+type);
					// check And create UserDirecories...
					// logger.info(">>>>>>>"+RoleLbId.getSelectedItem().getLabel());

					// create folder structure , if User created newly

					MessageUtil.setMessage("User " + saveMsg + " successfully.", "color:green;");

					if (type == null) {
						String messageToUser = PropertyUtil.getPropertyValueFromDB("newUserDetailsTemplate");
						messageToUser = messageToUser
								.replace(Constants.NEW_USER_DETAILS_PLACEHOLDERS_FNAME, user.getFirstName())
								.replace(Constants.NEW_USER_DETAILS_PLACEHOLDERS_USERNAME,
										Utility.getOnlyUserName(user.getUserName()))
								.replace(Constants.NEW_USER_DETAILS_PLACEHOLDERS_PASSWORD, tempPwd)
								.replace(Constants.NEW_USER_DETAILS_PLACEHOLDERS_ORGID,
										Utility.getOnlyOrgId(user.getUserName()));

						EmailQueue emailQueue = new EmailQueue("Welcome to OptCulture!", messageToUser,
								Constants.EQ_TYPE_NEW_USER_DETAILS, "Active", user.getEmailId(),
								MyCalendar.getNewCalendar(), user);
						// emailQueueDao.saveOrUpdate(emailQueue);
						emailQueueDaoForDML.saveOrUpdate(emailQueue);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					logger.error("Exception ::", e);
				}

			}
		} catch (Exception e) {

		}
		Redirect.goTo(PageListEnum.EMPTY);
		Redirect.goTo(PageListEnum.USERADMIN_MANAGE_USER);

	}

	public void onSelect$userByLbId() {
		logger.debug("----------just entered--------------");
		prepareFilterOptions(userByLbId.getSelectedIndex());

	}

	public void prepareFilterOptions(int index) {

		for (int i = filterOptionLbId.getItemCount(); i > 1; i--) {

			filterOptionLbId.removeItemAt(i - 1);

		}

		Listitem item = null;
		if (index == 1) {

			for (UsersDomains userDomain : domainsList) {
				item = new Listitem(userDomain.getDomainName(), userDomain);
				item.setParent(filterOptionLbId);

			}
			filterOptionLbId.setSelectedIndex(0);

		} else if (index == 2) {
			for (int i = 0; i < RoleLbId.getItemCount(); i++) {

				item = new Listitem(RoleLbId.getItemAtIndex(i).getLabel(), RoleLbId.getItemAtIndex(i).getValue());
				item.setParent(filterOptionLbId);

			}

			filterOptionLbId.setSelectedIndex(0);

		} else if (index == 3) {

			item = new Listitem("Active", true);
			item.setParent(filterOptionLbId);

			item = new Listitem("In Active", false);
			item.setParent(filterOptionLbId);
			filterOptionLbId.setSelectedIndex(0);

		}

	}

	public void onClick$userFilterBtnId() {

		int index = userByLbId.getSelectedIndex();
		if (index != 0) {

			logger.debug("filterOptionLbId.getSelectedItem()" + filterOptionLbId.getSelectedItem());

			if (filterOptionLbId.getSelectedItem() == null || filterOptionLbId.getSelectedIndex() == 0) {

				MessageUtil.setMessage("Please select at least one filter option.", "color:red;");
				filterOptionLbId.setFocus(true);
				return;
			}

			List<Users> userList = null;

			// String orgUnitName = null;
			String role = null;
			String status = null;

			if (index == 1) {// getUsers by organization unit
				String userIds = "";
				Set<Long> userIdsSet = new HashSet<Long>();
				List<Map<String, Object>> retList = usersDao.getUsersByDomain(
						"" + ((UsersDomains) filterOptionLbId.getSelectedItem().getValue()).getDomainId());
				List<Map<String, Object>> retStoreList = usersDao.getUsersByDomainByRole(
						"" + ((UsersDomains) filterOptionLbId.getSelectedItem().getValue()).getDomainId());
				List<Map<String, Object>> retStandardist = usersDao.getUsersByStandardRole(
						"" + ((UsersDomains) filterOptionLbId.getSelectedItem().getValue()).getDomainId());
			//List<Map<String, Object>> retList = usersDao.getUsersByDomain();
				//List<Map<String, Object>> retStoreList = usersDao.getUsersByDomainByRole();
				retList.addAll(retStoreList);
				retList.addAll(retStandardist);

				for (Map<String, Object> map : retList) {

					/*
					 * if(userIds.length() > 0) userIds += ","; userIds +=
					 * (String)map.get("user_id");
					 */
					userIdsSet.add((Long) map.get("user_id"));

				}

				if (userIdsSet.size() <= 0) {

					MessageUtil.setMessage("No users found in chosen organization unit.", "color:red;");
					return;

				}

				userList = usersDao.findAllByIds(userIdsSet);

			} else if (index == 2) {// getusers by role

				role = ((SecRoles) filterOptionLbId.getSelectedItem().getValue()).getName();
				// userList = usersDao.findByAuthority(role,
				// currentUser.getUserOrganization().getUserOrgId());
				List<UsersDomains> domainsList = usersDao.getAllDomainsByUser(currentUser.getUserId());

				userList = usersDao.findByRoleName(role);

			} else if (index == 3) {// getUsers by status

				userList = usersDao.findByStatus((Boolean) filterOptionLbId.getSelectedItem().getValue(),
						currentUser.getUserOrganization().getUserOrgId());

			}

			if (userList == null || userList.size() <= 0) {

				MessageUtil.setMessage("No users found with chosen criteria.", "color:red;");
				return;

			}
			customersGridId.setModel(new ListModelList(userList));

			customersGridId.setRowRenderer(renderer);

		} // if
		else if (index == 0) {

			if (usersList != null && usersList.size() > 0) {

				customersGridId.setModel(new ListModelList(usersList));

				customersGridId.setRowRenderer(renderer);
			}

		}

	}

	public void onBlur$userNameTbId() {

		String userName = userNameTbId.getValue().trim();

		if (userName.length() == 0) {
			nameStatusLblId.setStyle("color:red;font-weight:bold;");
			nameStatusLblId.setValue("Please Provide User Name");
			isValidUserName = false;
			return;
		}

		userName = userName + "__org__" + currentUser.getUserOrganization().getOrgExternalId();
		if (userName == null)
			return;
		userName = Utility.condense(userName);

		if (!Utility.validateUserName(userName)) {
			nameStatusLblId.setStyle("color:red;font-weight:bold;");
			nameStatusLblId.setValue("Provide valid User Name, Special characters and spaces will not be allowed");
			isValidUserName = false;
			return;
		}

		try {
			UsersDao usersDao = (UsersDao) SpringUtil.getBean("usersDao");
			UsersDaoForDML usersDaoForDML = (UsersDaoForDML) SpringUtil.getBean("usersDaoForDML");
			Users user = usersDao.findByUsername(userName, GetUser.getUserObj().getUserOrganization().getUserOrgId());// user
																														// name
																														// should
																														// be
																														// unique
																														// accross
																														// the
																														// organization
			if (user == null) {
				nameStatusLblId.setStyle("color:#08658F;font-weight:bold;");
				nameStatusLblId.setValue("Available");
				isValidUserName = true;
			} else {
				nameStatusLblId.setStyle("color:red;font-weight:bold;");
				nameStatusLblId.setValue("Not Available");
				// userNameTbId.setValue(userName.substring(0, userName.substring(beginIndex)));
				isValidUserName = false;
			}
		} catch (Exception e) {
			logger.error("Exception : ");
		}
	}

	public void onClick$resetSearchCriteriaAnchId() {

		try {
			searchBoxId.setValue("Search User...");
			userByLbId.setSelectedIndex(0);
			filterOptionLbId.setSelectedIndex(0);

			// usersList =
			// usersDao.getUsersByOrg(currentUser.getUserOrganization().getUserOrgId());
			customersGridId.setRowRenderer(renderer);
			customersGridId.setModel(new ListModelList(usersList));

		} catch (WrongValueException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
		}
	}// onClick$resetSearchCriteriaAnchId()

	public void onChanging$searchBoxId(InputEvent event) {
		String key = event.getValue();
		LinkedList<Users> item = new LinkedList<Users>();

		if (usersList == null && usersList.size() < 1)
			return;

		// logger.debug("Total Users Count :" + usersList.size());
		Users user;
		if (key.trim().length() != 0) {
			for (int i = 0; i < usersList.size(); i++) {
				user = (Users) usersList.get(i);
				if (user.getUserName().toLowerCase().indexOf(key.toLowerCase()) != -1
						&& user.getUserName().toLowerCase().indexOf(key.toLowerCase()) == 0)
					item.add(user);
			}
			customersGridId.setModel(new ListModelList(item));
		} else
			customersGridId.setModel(new ListModelList(usersList));
	}

	public void userSettings() {

		if (manageUsersTabBoxId.getSelectedIndex() == 1) {

			type = (String) session.getAttribute("manageUserType");
			user = (Users) session.getAttribute("manageUser");
			// userNameTbId,passwordTbId,rePasswordTbId,emailIdTbId,firstNameTbId,lastNameTbId;

			if (type != null && user != null) {

				RoleLbId.getItems().clear();

				String typeAll = "'All'";
				String typestandard = "'Standard'";
				String RoleStandard = "Standard_User";
				String typeCustom = "'Custom'";
				String RoleStore = "StoreOperator";

				userRolesList = secRolesDao.findRoleByType(typeAll);
				// boolean
				// manageStoreOperator=currentUser.getUserOrganization().isManageStoreOperator();
				// if(manageStoreOperator==true) {
				userRolesStandardList = secRolesDao.findRoleStoreByType(typestandard, RoleStandard);
				userRolesNameList = secRolesDao.findRoleStoreByType(typeCustom, RoleStore);

				if (userRolesNameList != null && !userRolesNameList.isEmpty()) {
					userRolesNameList.get(0).setName("StoreOperator");
					// userRolesList.addAll(userRolesNameList);
				}
				// if (isAdmin) {
				userRolesList.addAll(userRolesStandardList);
				userRolesList.addAll(userRolesNameList);


				if (userRolesList == null || userRolesList.size() == 0) {
					logger.debug("No Roles found for user :" + currentUser.getUserId());
					// return;
				}
				Listitem roleItem;
				for (SecRoles secRoles : userRolesList) {

					roleItem = new Listitem(secRoles.getName(), secRoles);
					roleItem.setParent(RoleLbId);

				}
					/*
				 * String types = "'All'"; String typestandard = "'Standard'"; String
				 * RoleStandard = "Standard_User"; String typeCustom = "'Custom'"; String
				 * RoleName = "StoreOperator"; userRolesList =
				 * secRolesDao.findRoleByType(types); // boolean //
				 * manageStoreOperator=currentUser.getUserOrganization().isManageStoreOperator()
				 * ; // if(manageStoreOperator==true) { userRolesListStandard =
				 * secRolesDao.findRoleStoreByType(typestandard, RoleStandard);
				 * userRolesList.addAll(userRolesListStandard); userRolesNameList =
				 * secRolesDao.findRoleStoreByType(typeCustom, RoleName);
				 * 
				 * if (userRolesNameList != null && !userRolesNameList.isEmpty()) {
				 * userRolesNameList.get(0).setName("Store Operator"); //
				 * userRolesList.addAll(userRolesNameList); }
				 * userRolesList.addAll(userRolesNameList);
				 * 
				 * // }
				 * 
				 * if (userRolesList == null || userRolesList.size() == 0) {
				 * logger.debug("No Roles found for user :" + currentUser.getUserId()); //
				 * return; } Listitem roleItem; for (SecRoles secRoles : userRolesList) {
				 * 
				 * roleItem = new Listitem(secRoles.getName(), secRoles);
				 * roleItem.setParent(RoleLbId);
				 * 
				 * 
					 * if(roleIds.length() > 0) roleIds += ","; roleIds += secRoles.getRole_id();
				 * 
				 * 
				 * }
					 */


				userNameTbId.setValue(Utility.getOnlyUserName(user.getUserName()));

//				passwordTbId.setValue("********");
//				rePasswordTbId.setValue("********");
				emailIdTbId.setValue(user.getEmailId());
				firstNameTbId.setValue(user.getFirstName());
				lastNameTbId.setValue(user.getLastName());
				emailLimitIntBoxId.setValue(user.getEmailCount());

				List<UsersDomains> domainsList = usersDao.getAllDomainsByUser(user.getUserId());
				// Set<UsersDomains> domainSet = new HashSet<UsersDomains>();

				for (UsersDomains usersDomains : domainsList) {

					for (int i = 0; i < domainLbId.getItemCount(); i++) {

						if (usersDomains.getDomainName().equals(domainLbId.getItemAtIndex(i).getLabel())) {
							domainLbId.setSelectedIndex(i);
							break;
						}

					} // for

				}

				// Set<SecRoles> roleSet = user.getRoles();
				List<SecRoles> userRoles = secRolesDao.findByUserId(user.getUserId());
				for (SecRoles secRoles : userRoles) {

					for (int i = 0; i < RoleLbId.getItemCount(); i++) {

						if (RoleLbId.getItemAtIndex(i).getLabel().equals(secRoles.getName())) {

							RoleLbId.setSelectedIndex(i);
//							RoleLbId.setDisabled(true);
							break;
						}

					} // for

				} // foreach
				/*
				 * String userRole = getUserByRole();
				 * 
				 * for(int i=0; i<RoleLbId.getItemCount();i++) {
				 * 
				 * if(RoleLbId.getItemAtIndex(i).getValue().equals(userRole)){
				 * 
				 * RoleLbId.setSelectedIndex(i); break; }
				 * 
				 * }
				 */
				activateUserChkBoxId.setChecked(user.isEnabled());

				// added for user adrress

				addressOneTbId.setValue(user.getAddressOne());
				addressTwoTbId.setValue(user.getAddressTwo());
				cityTbId.setValue(user.getCity());
				stateTbId.setValue(user.getState());
				countryTbId.setValue(user.getCountry());
				pinTbId.setValue(user.getPinCode());
				phoneTbId.setValue(user.getPhone());

				// Added for sms settings

				enableSMSChkId.setChecked(user.isEnableSMS());

				smsDivId.setVisible(user != null && user.isEnableSMS());
				if (smsDivId.isVisible()) {
					SMSimitIntBoxId.setValue(user.getSmsCount());
					SMSimitIntBoxId.setReadonly(false);
				}

				if (type != null && type.equals("view")) {

					// userNameTbId.setReadonly(true);
					userNameTbId.setDisabled(true);
//					passwordTbId.setReadonly(true);
//					rePasswordTbId.setReadonly(true);
					emailIdTbId.setReadonly(true);
					firstNameTbId.setReadonly(true);
					lastNameTbId.setReadonly(true);
					domainLbId.setDisabled(true);
					RoleLbId.setDisabled(true);
					activateUserChkBoxId.setDisabled(true);
					createUserBtnId.setVisible(false);
					editUserBtnId.setVisible(true);
					backBtnId.setVisible(true);
					isValidUserName = true;

					// added for address fields
					addressOneTbId.setReadonly(true);
					addressTwoTbId.setReadonly(true);
					cityTbId.setReadonly(true);
					stateTbId.setReadonly(true);
					countryTbId.setReadonly(true);
					pinTbId.setReadonly(true);
					phoneTbId.setReadonly(true);

					if (user.isEnableSMS()) {

						enableSMSChkId.setChecked(true);
						smsDivId.setVisible(true);
						SMSimitIntBoxId.setReadonly(true);
					}

				} else if (type != null && type.equals("edit")) {

					// userNameTbId.setReadonly(true);
					userNameTbId.setDisabled(true);
//					passwordTbId.setReadonly(true);
//					rePasswordTbId.setReadonly(true);
					emailIdTbId.setReadonly(false);
					firstNameTbId.setReadonly(false);
					lastNameTbId.setReadonly(false);
					domainLbId.setDisabled(false);
					RoleLbId.setDisabled(false);
					activateUserChkBoxId.setDisabled(false);
					createUserBtnId.setLabel("Update");
					createUserBtnId.setVisible(true);
					editUserBtnId.setVisible(false);
					backBtnId.setVisible(false);
					isValidUserName = true;

					// added for address fields
					addressOneTbId.setReadonly(false);
					addressTwoTbId.setReadonly(false);
					cityTbId.setReadonly(false);
					stateTbId.setReadonly(false);
					countryTbId.setReadonly(false);
					pinTbId.setReadonly(false);
					phoneTbId.setReadonly(false);

					if (user.isEnableSMS()) {

						enableSMSChkId.setChecked(true);
						smsDivId.setVisible(true);
						SMSimitIntBoxId.setReadonly(false);
					}

				} // else if

				/*
				 * else if (type != null && type.equals("Suspend")) {
				 * 
				 * try { logger.debug("--Just Entered--"); MessageUtil.clearMessage(); String
				 * msg=user.isEnabled()?"disable":"enable"; String
				 * msgHeader=user.isEnabled()?"Disable User":"Enable User";
				 * 
				 * try {
				 * 
				 * int confirm = Messagebox.show("Are you sure you want to "+msg+" the user : "
				 * + user.getUserName() + "?", msgHeader, Messagebox.OK | Messagebox.CANCEL,
				 * Messagebox.QUESTION); if(confirm != Messagebox.OK) { return; }
				 * 
				 * UsersDao usersDao = (UsersDao) SpringUtil.getBean("usersDao"); UsersDaoForDML
				 * usersDaoForDML = (UsersDaoForDML) SpringUtil.getBean("usersDaoForDML");
				 * user.setEnabled(!user.isEnabled()); //usersDao.saveOrUpdate(user);
				 * usersDaoForDML.saveOrUpdate(user); } catch (Exception e) { // TODO
				 * Auto-generated catch block logger.error("Exception  ::", e); }
				 * 
				 * try { Messagebox.show("User "+msg +"d successfully.", "Information",
				 * Messagebox.OK, Messagebox.INFORMATION); } catch (Exception e) { // TODO
				 * Auto-generated catch block logger.error("Exception  ::", e); }
				 * Redirect.goTo(PageListEnum.EMPTY);
				 * Redirect.goTo(PageListEnum.ADMIN_LIST_USERS);
				 * //Executions.getCurrent().getDesktop().invalidate(); } catch (Exception e) {
				 * logger.error("Exception  ::", e); } }
				 */

			}

		}

	}

	public void onClick$editUserBtnId() {

		Users userObj = (Users) session.getAttribute("manageUser");
		/*
		 * String types = "'All'"; String typestandard = "'Standard'"; String
		 * RoleStandard="Standard_User"; String type="'Custom'"; String
		 * RoleName="StoreOperatorRole"; userRolesList =
		 * secRolesDao.findRoleByType(types); //boolean
		 * manageStoreOperator=currentUser.getUserOrganization().isManageStoreOperator()
		 * ; //if(manageStoreOperator==true) {
		 * userRolesListStandard=secRolesDao.findRoleStoreByType(typestandard,
		 * RoleStandard); userRolesList.addAll(userRolesListStandard);
		 * userRolesNameList=secRolesDao.findRoleStoreByType(type, RoleName);
		 * 
		 * if(userRolesNameList != null && ! userRolesNameList.isEmpty()){
		 * userRolesNameList.get(0).setName("Store Operator");
		 * //userRolesList.addAll(userRolesNameList); }
		 * userRolesList.addAll(userRolesNameList);
		 * 
		 * //}
		 * 
		 * if(userRolesList==null || userRolesList.size()==0) {
		 * logger.debug("No Roles found for user :"+currentUser.getUserId()); //return;
		 * } Listitem roleItem; for (SecRoles secRoles : userRolesList) {
		 * 
		 * roleItem = new Listitem(secRoles.getName(), secRoles);
		 * roleItem.setParent(RoleLbId);
		 * 
		 * if(roleIds.length() > 0) roleIds += ","; roleIds += secRoles.getRole_id();
		 * 
		 * 
		 * }
		 */

		// userNameTbId.setReadonly(true);
		userNameTbId.setDisabled(true);
//		passwordTbId.setReadonly(true);
//		rePasswordTbId.setReadonly(true);
		emailIdTbId.setReadonly(false);
		firstNameTbId.setReadonly(false);
		lastNameTbId.setReadonly(false);
		domainLbId.setDisabled(false);
		RoleLbId.setDisabled(false);
		activateUserChkBoxId.setDisabled(false);
		createUserBtnId.setLabel("Update");
		createUserBtnId.setVisible(true);
		editUserBtnId.setVisible(false);
		backBtnId.setVisible(false);
		isValidUserName = true;

		// added for address fields
		addressOneTbId.setReadonly(false);
		addressTwoTbId.setReadonly(false);
		cityTbId.setReadonly(false);
		stateTbId.setReadonly(false);
		countryTbId.setReadonly(false);
		pinTbId.setReadonly(false);
		phoneTbId.setReadonly(false);

		/*
		 * if(userObj != null){
		 * 
		 * enableSMSChkId.setChecked(userObj.isEnableSMS());
		 * 
		 * smsDivId.setVisible(userObj != null && userObj.isEnableSMS());
		 * if(smsDivId.isVisible()){ SMSimitIntBoxId.setValue(userObj.getSmsCount());
		 * SMSimitIntBoxId.setReadonly(false); }
		 * 
		 * }
		 */

	}

	public void onClick$backBtnId() {

		manageUsersTabBoxId.setSelectedIndex(0);
		onSelect$manageUsersTabBoxId();
	}

	public void onSelect$manageUsersTabBoxId() {

		logger.debug("----just entyered ----onSelect$manageUsersTabBoxId" + user + "   " + type);
		type = (String) session.removeAttribute("manageUserType");
		user = (Users) session.removeAttribute("manageUser");

		userNameTbId.setValue("");
		nameStatusLblId.setValue("");
//		passwordTbId.setValue("");
//		rePasswordTbId.setValue("");
		firstNameTbId.setValue("");
		lastNameTbId.setValue("");
		emailIdTbId.setValue("");
		RoleLbId.getChildren().clear();
		
		String types = "'Standard'";
		String RoleStandard = "Standard_User";
		String type = "'Custom'";
		String RoleName = "StoreOperator";
		// userRolesList = secRolesDao.findRoleByType(types);
		// boolean
		// manageStoreOperator=currentUser.getUserOrganization().isManageStoreOperator();
		// if(manageStoreOperator==true) {
		userRolesList = secRolesDao.findRoleStoreByType(types, RoleStandard);
		userRolesNameList = secRolesDao.findRoleStoreByType(type, RoleName);

		/*
		 * if (userRolesNameList != null && !userRolesNameList.isEmpty()) {
		 * userRolesNameList.get(0).setName("Store Operator"); //
		 * userRolesList.addAll(userRolesNameList); }
		 */
		userRolesList.addAll(userRolesNameList);
		// }

		if (userRolesList == null || userRolesList.size() == 0) {
			logger.debug("No Roles found for user :" + currentUser.getUserId());
			// return;
		}
		Listitem roleItem;
		for (SecRoles secRoles : userRolesList) {

			roleItem = new Listitem(secRoles.getName(), secRoles);
			roleItem.setParent(RoleLbId);

			/*
			 * if(roleIds.length() > 0) roleIds += ","; roleIds += secRoles.getRole_id();
			 */

		}

		if (domainLbId.getItemCount() > 0)
			domainLbId.setSelectedIndex(0);
		if (RoleLbId.getItemCount() > 0)
			RoleLbId.setSelectedIndex(0);

		activateUserChkBoxId.setChecked(true);
		emailLimitIntBoxId.setValue(0);

		//userNameTbId.setReadonly(false);
		userNameTbId.setDisabled(false);
		isPwrUserExistsLblId.setValue("");
//		passwordTbId.setReadonly(false);
//		rePasswordTbId.setReadonly(false);
		firstNameTbId.setReadonly(false);
		lastNameTbId.setReadonly(false);
		emailIdTbId.setReadonly(false);
		domainLbId.setDisabled(false);
		RoleLbId.setDisabled(false);
		activateUserChkBoxId.setDisabled(false);

		// added for address fields

		addressOneTbId.setValue("");
		addressTwoTbId.setValue("");
		cityTbId.setValue("");
		stateTbId.setValue("");
		countryTbId.setValue("");
		pinTbId.setValue("");
		phoneTbId.setValue("");

		addressOneTbId.setReadonly(false);
		addressTwoTbId.setReadonly(false);
		cityTbId.setReadonly(false);
		stateTbId.setReadonly(false);
		countryTbId.setReadonly(false);
		pinTbId.setReadonly(false);
		phoneTbId.setReadonly(false);

		// sms settings

		enableSMSChkId.setChecked(false);
		smsDivId.setVisible(false);
		SMSimitIntBoxId.setValue(0);

		createUserBtnId.setLabel("Create");
		createUserBtnId.setVisible(true);
		editUserBtnId.setVisible(false);
		backBtnId.setVisible(false);

		setUserInfo();
	}

	/*
	 * public String getUserRole(String userName) {
	 * 
	 * 
	 * List<Authorities> roleLst = usersDao.findRole(userName); for (Authorities
	 * authorities : roleLst) {
	 * 
	 * if(authorities.getAuthority().equals(Constants.ROLE_USER) ||
	 * authorities.getAuthority().equals(Constants.ROLE_ADMIN) ) {
	 * 
	 * continue; }
	 * 
	 * return authorities.getAuthority(); }
	 * 
	 * return null; }
	 */
	public String getUserByRole(String roleName) {

		try {
			List<SecRoles> roleLst = usersDao.findByRole(roleName);
			logger.info("role list is" + roleLst);
			for (SecRoles secRoles : roleLst) {
				if (currentUser.getUserOrganization().getClientType().equals(Constants.CLIENT_TYPE_POS)) {
					if (secRoles.getType().equals(Constants.SECROLE_TYPE_ADMIN)
							|| secRoles.getType().equals(Constants.SECROLE_TYPE_OPT_ADMIN))
						continue;

				} else {
					if (secRoles.getType().equals(Constants.SECROLE_TYPE_ADMIN)
							|| secRoles.getType().equals(Constants.SECROLE_TYPE_BCRM_ADMIN))
						continue;

				}
				return secRoles.getName();

			}
			return null;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
			return null;
		}
	}

	/**** duplicate entry of listName *****/
	public void onClick$changePwdBtnId() {
		try {

			String newPwdStr = changePwdTbId.getValue().trim();

			if (newPwdStr == null || newPwdStr.trim().length() == 0) {
				MessageUtil.setMessage("Please provide password.", "color:red;", "TOP");
				changePwdTbId.setStyle("border:1px solid #DD7870;");
				return;
			} else if (newPwdStr == null || newPwdStr.trim().length() <= 3) {
				MessageUtil.setMessage("Password must be greater than 3 characters.", "color:red;", "TOP");
				changePwdTbId.setStyle("border:1px solid #DD7870;");
				return;
			}
			
			String pattern = "^(?=.{8,50}$)(?=(.*[A-Z]))(?=(.*[a-z]))(?=(.*[0-9]))(?=.*[-@!#$%^&-+=()]).*$";
			

			Pattern pwdPattern = Pattern.compile(pattern);

			if (newPwdStr == null || (!pwdPattern.matcher(newPwdStr.trim()).matches())) {
				MessageUtil.setMessage(
						"Password must contain at least 8 characters,1 uppercase,1 lowercase,"
						+ "\n 1 special character (@!#$%^&+-=*'()) and 1 number .", "color:red",
						"TOP");
				changePwdTbId.setStyle("border:1px solid #DD7870;");
				return;
			}

			Users editUser = (Users) changePwdPopupId.getAttribute("CHANGE_PWD");
			String newPwdHash = Utility.encryptPassword(editUser.getUserName(), newPwdStr);
			/*
			 * Md5PasswordEncoder md5 = new Md5PasswordEncoder(); String newPwdHash =
			 * md5.encodePassword(newPwdStr,editUser.getUserName());
			 */

			if (editUser.getPassword().equals(newPwdHash)) {
				MessageUtil.setMessage("Same password is given, please give another password.", "color:red;", "TOP");
				changePwdTbId.setStyle("border:1px solid #DD7870;");
				return;
			}
			editUser.setPassword(newPwdHash);
			if (editUser.getLastLoggedInTime() == null) {
				editUser.setLastLoggedInTime(Calendar.getInstance());
			}
			// usersDao.saveOrUpdate(editUser);
			usersDaoForDML.saveOrUpdate(editUser);

			MessageUtil.setMessage("Password changed successfully..", "color:blue;", "TOP");
			Include xcontents = Utility.getXcontents();
			xcontents.invalidate();
		} catch (Exception e) {
			logger.error("Exception :: errorr getting from the copyList method >> ::", e);
		}
	}

	public class MyRowRenderer implements RowRenderer, EventListener {

		Calendar tempCal;
		Users user;
		Label lbl;
		Hbox hbox;

		@Override
		public void render(Row row, Object crObj, int arg2) {
			user = (Users) crObj;

			row.setValue(user);
			lbl = new Label(Utility.getOnlyUserName(user.getUserName()));
			lbl.setParent(row);

			lbl = new Label(user.getEmailId());
			lbl.setParent(row);

			String lstName = user.getLastName() != null ? " " + user.getLastName() : "";
			lbl = new Label(user.getFirstName() + lstName);
			lbl.setParent(row);

			List<UsersDomains> userDomains = usersDao.getAllDomainsByUser(user.getUserId());

			String domainNames = "";
			for (UsersDomains eachDomain : userDomains) {
				if (domainNames.length() > 0)
					domainNames += ", ";
				domainNames += eachDomain.getDomainName();
			}

			lbl = new Label(domainNames);
			lbl.setParent(row);

			String role = mapObj.get(user.getUserId());
			// logger.debug(" role is "+role);

			/*
			 * if(role.equalsIgnoreCase("Basic User")){ role = ROLE_BASIC; } else
			 * if(role.equalsIgnoreCase("Power User")) {
			 * 
			 * role = ROLE_POWER; }
			 */

			lbl = new Label(role);
			lbl.setParent(row);

			String status = user.isEnabled() ? "Active" : "In Active";
			lbl = new Label(status);
			lbl.setParent(row);

			Hbox hbox = new Hbox();

			Image img = new Image("/img/theme/preview_icon.png");
			img.setStyle("margin-right:10px;cursor:pointer;");
			img.setTooltiptext("View Details");
			img.setAttribute("type", "userView");
			img.addEventListener("onClick", this);

			img.setParent(hbox);

			Image editImg = new Image("/img/email_edit.gif");
			editImg.setTooltiptext("Edit");
			editImg.setStyle("cursor:pointer;margin-right:10px;");
			editImg.addEventListener("onClick", this);
			editImg.setAttribute("type", "userEdit");
			editImg.setParent(hbox);

			img = new Image("/img/icons/Change-password.png");
			img.setTooltiptext("Change Password");
			img.setStyle("margin-right:5px;cursor:pointer;");
			img.setAttribute("type", "changePwd");
			img.addEventListener("onClick", this);
			img.setParent(hbox);

			String src = "";
			String toolTipTxtStr = "";
			String type = "";
			if (user.isEnabled()) {
				src = "/images/loyalty/suspend.png";
				toolTipTxtStr = "Suspend";
				type = "Suspend";
			} else if (!user.isEnabled()) {
				src = "/img/play_icn.png";
				toolTipTxtStr = "Activate";
				type = "Activate";
			}

			Image statusImg = new Image(src);
			statusImg.setTooltiptext(toolTipTxtStr);
			statusImg.setStyle("cursor:pointer;margin-right:5px;");
			statusImg.addEventListener("onClick", this);
			statusImg.setAttribute("type", type);
			statusImg.setParent(hbox);

			/*
			 * Image delImg = new Image("/img/action_delete.gif");
			 * delImg.setTooltiptext("Delete"); delImg.setStyle("cursor:pointer;");
			 * delImg.addEventListener("onClick", this); delImg.setAttribute("type",
			 * "userDelete"); delImg.setParent(hbox);
			 */

			hbox.setParent(row);

		}

		@Override
		public void onEvent(Event evt) throws Exception {
			// TODO Auto-generated method stub
			Object obj = evt.getTarget();

			if (obj instanceof Image) {

				Image img = (Image) obj;
				user = ((Row) img.getParent().getParent()).getValue();

				String evtType = (String) img.getAttribute("type");

				if (evtType.equalsIgnoreCase("userView")) {

					session.setAttribute("manageUserType", "view");
					session.setAttribute("manageUser", user);
					manageUsersTabBoxId.setSelectedIndex(1);
					userSettings();

				} else if (evtType.equalsIgnoreCase("userEdit")) {

					session.setAttribute("manageUserType", "edit");
					session.setAttribute("manageUser", user);
					manageUsersTabBoxId.setSelectedIndex(1);
					userSettings();

				} else if (evtType.equalsIgnoreCase("changePwd")) {

					changePwdTbId.setValue("");
					logger.debug("user is  is ::" + user);
					logger.debug(">>>>>>>>> changePwdPopupId is  ::" + changePwdPopupId);
					changePwdPopupId.setAttribute("CHANGE_PWD", user);
					changePwdPopupId.open(img, "middle_right");
					changePwdTbId.setStyle("border:1px solid #7F9DB9;");

				} else if (evtType.equalsIgnoreCase("Suspend")) {

					// session.setAttribute("manageUserType", "Suspend");
					// session.setAttribute("manageUser", user);
					// manageUsersTabBoxId.setSelectedIndex(1);
					// userSettings();
					try {
						logger.debug("--Just Entered--");
						MessageUtil.clearMessage();
						String msg = user.isEnabled() ? "disable" : "enable";
						String msgHeader = user.isEnabled() ? "Disable User" : "Enable User";

						try {

							int confirm = Messagebox.show(
									"Are you sure you want to " + msg + " the user : " + user.getUserName() + "?",
									msgHeader, Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
							if (confirm != Messagebox.OK) {
								return;
							}

							UsersDao usersDao = (UsersDao) SpringUtil.getBean("usersDao");
							UsersDaoForDML usersDaoForDML = (UsersDaoForDML) SpringUtil.getBean("usersDaoForDML");
							user.setEnabled(!user.isEnabled());
							// usersDao.saveOrUpdate(user);
							usersDaoForDML.saveOrUpdate(user);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							logger.error("Exception  ::", e);
						}

						try {
							Messagebox.show("User " + msg + "d successfully.", "Information", Messagebox.OK,
									Messagebox.INFORMATION);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							logger.error("Exception  ::", e);
						}
						Redirect.goTo(PageListEnum.EMPTY);
						Redirect.goTo(PageListEnum.USERADMIN_MANAGE_USER);
						// Executions.getCurrent().getDesktop().invalidate();
					} catch (Exception e) {
						logger.error("Exception  ::", e);
					}

				} else if (evtType.equalsIgnoreCase("Activate")) {

					// session.setAttribute("manageUserType", "Activate");
					// session.setAttribute("manageUser", user);
					// manageUsersTabBoxId.setSelectedIndex(1);
					// userSettings();
					try {
						logger.debug("--Just Entered--");
						MessageUtil.clearMessage();
						String msg = user.isEnabled() ? "disable" : "enable";
						String msgHeader = user.isEnabled() ? "Disable User" : "Enable User";
						boolean isEnabled = false;

						try {

							int confirm = Messagebox.show(
									"Are you sure you want to " + msg + " the user : " + user.getUserName() + "?",
									msgHeader, Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
							if (confirm != Messagebox.OK) {
								return;
							}

							UsersDao usersDao = (UsersDao) SpringUtil.getBean("usersDao");
							UsersDaoForDML usersDaoForDML = (UsersDaoForDML) SpringUtil.getBean("usersDaoForDML");
							user.setEnabled(!isEnabled);
							// usersDao.saveOrUpdate(user);
							usersDaoForDML.saveOrUpdate(user);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							logger.error("Exception  ::", e);
						}

						try {
							Messagebox.show("User " + msg + "d successfully.", "Information", Messagebox.OK,
									Messagebox.INFORMATION);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							logger.error("Exception  ::", e);
						}
						Redirect.goTo(PageListEnum.EMPTY);
						Redirect.goTo(PageListEnum.USERADMIN_MANAGE_USER);
						// Executions.getCurrent().getDesktop().invalidate();
					} catch (Exception e) {
						logger.error("Exception  ::", e);

					}

				}
				/*
				 * else if(evtType.equalsIgnoreCase("userDelete")) {
				 * 
				 * //TODO need to decide user Deletion
				 * 
				 * 
				 * 
				 * 
				 * 
				 * }
				 */

			}

		}

	}

	public Users setUserData(Users newUser, Users parentUser) {

		newUser.setCompanyName(parentUser.getUserOrganization().getOrganizationName());
		newUser.setAddressOne(addressOneTbId.getValue());

		newUser.setAddressTwo(addressTwoTbId.getValue());
		newUser.setCity(cityTbId.getValue());
		newUser.setState(stateTbId.getValue());
		newUser.setCountry(countryTbId.getValue());
		newUser.setPinCode(pinTbId.getValue());

		newUser.setPhone(phoneTbId.getValue());

		newUser.setPackageStartDate(Calendar.getInstance());
		newUser.setPackageExpiryDate(parentUser.getPackageExpiryDate());
		newUser.setFooterEditor(parentUser.getFooterEditor());
		newUser.setVmta(parentUser.getVmta());

		// setSMSGate way and Country Code
		newUser.setUserSMSTool(parentUser.getUserSMSTool());
		newUser.setCountryCarrier(parentUser.getCountryCarrier());
		newUser.setMsgChkType(parentUser.getMsgChkType());

		newUser.setCountryType(parentUser.getCountryType());

		// ************editable properties***************
		newUser.setEmailId(emailIdTbId.getValue());
		newUser.setFirstName(firstNameTbId.getValue());
		newUser.setLastName(lastNameTbId.getValue());

		newUser.setEnabled(activateUserChkBoxId.isChecked());
		newUser.setUserOrganization(parentUser.getUserOrganization());

		return newUser;
	}

	// added for parent user address if user does not have address
	public void setUserInfo() {
		logger.debug("user obj is" + user);
		logger.debug("prent user obj is" + currentUser);

		if (user == null) {
			logger.debug("currentUser.getAddressOne()" + currentUser.getAddressOne());
			if (currentUser.getAddressOne() != null || !currentUser.getAddressOne().isEmpty()) {
				addressOneTbId.setValue(currentUser.getAddressOne());
			}
			if (currentUser.getAddressTwo() != null || !currentUser.getAddressTwo().isEmpty()) {
				addressTwoTbId.setValue(currentUser.getAddressTwo());
			}

			if (currentUser.getCity() != null || !currentUser.getCity().isEmpty()) {
				cityTbId.setValue(currentUser.getCity());
			}

			if (currentUser.getState() != null || !currentUser.getState().isEmpty()) {
				stateTbId.setValue(currentUser.getState());
			}

			if (currentUser.getCountry() != null || !currentUser.getCountry().isEmpty()) {
				countryTbId.setValue(currentUser.getCountry());
			}

			if (currentUser.getPinCode() != null || !currentUser.getPinCode().isEmpty()) {
				pinTbId.setValue(currentUser.getPinCode());
			}

		}

	}

	public boolean validateNum(Textbox txtbox) {

		// logger.debug("----just entered with the intbox======>"+txtbox);

		String tbErrorCss = "border:1px solid #F37373; background:#FFCFCF";
		String tbNormalCss = "border:1px solid #B2B0B1; background:url('/subscriber/zkau/web/1d1ebab6/zul/img/misc/text-bg.gif') repeat-x scroll 0 0 #FFFFFF";

		String countryTypeStr = countryTbId.getValue().trim();

		try {
			if (txtbox.isValid()) {

				if (Utility.zipValidateMap.containsKey(countryTypeStr)) {
					logger.info("country type is ----->>>" + countryTypeStr);
					String zip = txtbox.getValue().trim();
					boolean zipCode = Utility.validateZipCode(zip, countryTypeStr);
					if (!zipCode) {
						txtbox.setStyle(tbErrorCss);
						return false;
					}
				} else {
					logger.info("country type is ----->>>" + countryTypeStr);
					String zip = txtbox.getValue().trim();

					if (zip != null && zip.length() > 0) {

						try {

							Long pinLong = Long.parseLong(zip);

						} catch (NumberFormatException e) {
							// MessageUtil.setMessage("Please provide 5 / 6 digits Zip Code.","color:red;");
							txtbox.setStyle(tbErrorCss);
							return false;
						}

						if (zip.length() > 6 || zip.length() < 5) {

							// Messagebox.show("Please provide 5 / 6 digits Zip code / pin.");
							// MessageUtil.setMessage("Please provide 5 / 6 digits Zip code / Pin.",
							// "Color:red", "Top");
							txtbox.setStyle(tbErrorCss);
							return false;

						}
					}
				}
				txtbox.setStyle(tbNormalCss);
			}
			return true;
		} catch (Exception e) {

			txtbox.setStyle(tbErrorCss);
			logger.error("Exception  ::", e);
			return false;
		}

		/*
		 * try { if(txtbox.isValid()){ int str =
		 * Integer.parseInt(txtbox.getValue().trim()); if( str <= 0) {
		 * 
		 * //txtbox.setStyle(tbErrorCss);
		 * 
		 * 
		 * return false;
		 * 
		 * }//if txtbox.setStyle(tbNormalCss); }
		 * 
		 * return true; } catch (Exception e) { // TODO Auto-generated catch block
		 * txtbox.setStyle(tbErrorCss); logger.error("Exception ::" , e); return false;
		 * }
		 */

	}// validateNum(-)

}// class
