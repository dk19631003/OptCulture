package org.mq.optculture.controller.loyalty;

import java.util.Calendar;
import java.util.TimeZone;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.controller.ActivityEnum;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.dao.UserActivitiesDao;
import org.mq.marketer.campaign.dao.UserActivitiesDaoForDML;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.EncryptDecryptLtyMembshpPwd;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PageListEnum;
import org.mq.marketer.campaign.general.PageUtil;
import org.mq.marketer.campaign.general.Redirect;
import org.mq.marketer.campaign.general.Utility;
import org.mq.marketer.campaign.beans.LoyaltyProgram;
import org.mq.marketer.campaign.beans.LoyaltyThresholdAlerts;
import org.mq.marketer.campaign.beans.UserActivities;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.PageUtil;
import org.mq.optculture.business.loyalty.LoyaltyProgramService;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Include;
import org.zkoss.zul.Label;
import org.zkoss.zul.Popup;
import org.zkoss.zul.Textbox;


@SuppressWarnings("serial")
public class LoyaltyDetailedReportController extends GenericForwardComposer {
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	private LoyaltyProgramService ltyPrgmSevice;
	private Label dateLblId,prgmNamelblId;
	private Popup ltyPwdPopupId;
	private Textbox ltyPwdTbId;
	private Long userId;
	
	private Include rightId;
	private UserActivitiesDaoForDML userActivitiesDaoForDML;
	private UsersDao usersDao;
	private Long currentOCAdminUserId;
	
	public LoyaltyDetailedReportController() {
		userId = GetUser.getUserObj().getUserId();
		PageUtil.setHeader("Loyalty Program Reports", null, "", true);
		ltyPrgmSevice= new LoyaltyProgramService();
		userActivitiesDaoForDML = (UserActivitiesDaoForDML)SpringUtil.getBean("userActivitiesDaoForDML");
		usersDao = (UsersDao) SpringUtil.getBean("usersDao");
	}
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		Long progmId=(Long) session.getAttribute("PROGRAM_REPORT_DETAILS");
		LoyaltyProgram loyaltyProgram = ltyPrgmSevice.getProgmObj(progmId);
		prgmNamelblId.setValue(loyaltyProgram.getProgramName());
		String dateStr = MyCalendar.calendarToString(loyaltyProgram.getCreatedDate(),null,(TimeZone)sessionScope.get("clientTimeZone"));
		dateLblId.setValue(dateStr);
		currentOCAdminUserId = (Long)session.getAttribute("currentOCAdmin");
		
	}
	
	public void changeRightContent(String page, String title, Include rightId) throws Exception { 
		
		PageUtil.setHeader(title, null, "", true);
		rightId.setSrc("zul/report/"+page+".zul");
	}
	
	public void onClick$prgmOvrwToolbarBtnId() {
		try {
			if(userActivitiesDaoForDML!=null) {
			UserActivities userActivity = new UserActivities("Visited loyalty program overview report page", "Visited pages", Calendar.getInstance(),userId );
			userActivitiesDaoForDML.saveOrUpdate(userActivity);
			}
			changeRightContent("ltyProgramOverviewReport","Loyalty Program Reports",rightId);
		} catch (Exception e) {
			logger.error("Exception ::" , e);
		}
	} // onClick$prgmOvrwToolbarBtnId
	
	public void onClick$kpiReporttoolbarBtnId() {
		try {
			if(userActivitiesDaoForDML!=null) {
			UserActivities userActivity = new UserActivities("Visited loyalty program KPIs report page", "Visited pages", Calendar.getInstance(),userId );
			userActivitiesDaoForDML.saveOrUpdate(userActivity);
			}
			changeRightContent("ltyKpiReport","Loyalty Program Reports",rightId);//TODO zul to be added
		} catch (Exception e) {
			logger.error("Exception ::" , e);
		}
	} // onClick$kpiReporttoolbarBtnId
	
	public void onClick$liabilityReportToolbarBtnId() {
		try {
			if(userActivitiesDaoForDML!=null) {
			UserActivities userActivity = new UserActivities("Visited loyalty program liability report page", "Visited pages", Calendar.getInstance(),userId );
			userActivitiesDaoForDML.saveOrUpdate(userActivity);
			}
			changeRightContent("ltyLiabilityReport","Loyalty Program Reports",rightId);
		} catch (Exception e) {
			logger.error("Exception ::" , e);
		}
	}//onClick$liabilityReportToolbarBtnId
	
	public void onClick$transactionToolbarBtnId() {
		try {
			if(userActivitiesDaoForDML!=null) {
			UserActivities userActivity = new UserActivities("Visited loyalty program store report page", "Visited pages", Calendar.getInstance(),userId );
			userActivitiesDaoForDML.saveOrUpdate(userActivity);
			}
			changeRightContent("ltyTransactionReport","Loyalty Program Reports",rightId);
		} catch (Exception e) {
			logger.error("Exception ::" , e);
		}
	} // onClick$transactionToolbarBtnId
	
	public void onClick$regCustomersReportToolbarBtnId() {
		try {
			if(userActivitiesDaoForDML!=null) {
			UserActivities userActivity = new UserActivities("Visited loyalty program customers report page", "Visited pages", Calendar.getInstance(),userId );
			userActivitiesDaoForDML.saveOrUpdate(userActivity);
			}
			changeRightContent("ltyRegistrationReport","Loyalty Program Reports",rightId);
		} catch (Exception e) {
			logger.error("Exception ::" , e);
		}
	} // onClick$regCustomersReportToolbarBtnId
	
	public void onClick$storeToolbarBtnId() {
		try {
			if(userActivitiesDaoForDML!=null) {
			UserActivities userActivity = new UserActivities("Visited loyalty program  transactions report page", "Visited pages", Calendar.getInstance(),userId );
			userActivitiesDaoForDML.saveOrUpdate(userActivity);
			}
			changeRightContent("ltyStoreReport","Loyalty Program Reports",rightId);
		} catch (Exception e) {
			logger.error("Exception ::" , e);
		}
	} // onClick$storeToolbarBtnId
	
	public void onClick$viewPrgmBtnId() {
		if(session.getAttribute("isPasswordRequired")!=null){
			Boolean isPasswordRequired = (boolean)session.getAttribute("isPasswordRequired");
			if(isPasswordRequired!=null && isPasswordRequired) {
				try {
					LoyaltyThresholdAlerts loyaltyThresholdAlerts = ltyPrgmSevice.findPwdByUserID(userId);
					String encryptedPwd = EncryptDecryptLtyMembshpPwd.decryptProgramPwd(loyaltyThresholdAlerts.getLtySecurityPwd());
					ltyPwdTbId.setValue("");
					ltyPwdTbId.setValue(encryptedPwd.trim());
					onClick$submitPwdBtnId();
					Long progmId=(Long) session.getAttribute("PROGRAM_REPORT_DETAILS");
					LoyaltyProgram loyaltyProgram = ltyPrgmSevice.getProgmObj(progmId);
					userActivitylogAdminUser("viewed/edited", loyaltyProgram.getProgramName());
				}catch (Exception e) {
					logger.error("lolalty reports ::"+e);
				}
			}
		}else {
			ltyPwdPopupId.open(ltyPwdPopupId);
		}
		//Long progmId=(Long) session.getAttribute("PROGRAM_REPORT_DETAILS");
		//session.setAttribute("programId", progmId);
		//Redirect.goTo(PageListEnum.LOYALTY_PROGRAM_OVERVIEW);
	}
	
	public void onClick$submitPwdBtnId() {
		try {
			String pwdStr = ltyPwdTbId.getValue().trim();

			if(pwdStr == null || pwdStr.trim().length() ==0) {
				MessageUtil.setMessage( "Please provide password.", "color:red;", "TOP");
				ltyPwdTbId.setText("");
				return;
			}
			/*else if(pwdStr ==null || pwdStr.trim().length() <= 3) {
				MessageUtil.setMessage( "Password must be greater than 3 characters.", "color:red;", "TOP");
				ltyPwdTbId.setStyle("border:1px solid #DD7870;");
				return;
			}
*/
			LoyaltyProgramService ltyPrgmSevice = new LoyaltyProgramService();
			LoyaltyThresholdAlerts loyaltyThresholdAlerts = ltyPrgmSevice.findPwdByUserID(userId);
			if(loyaltyThresholdAlerts != null && EncryptDecryptLtyMembshpPwd.decryptProgramPwd(loyaltyThresholdAlerts.getLtySecurityPwd()).equals(pwdStr)) {
				Long progmId=(Long) session.getAttribute("PROGRAM_REPORT_DETAILS");
				session.setAttribute("programId", progmId);
				Redirect.goTo(PageListEnum.LOYALTY_PROGRAM_OVERVIEW);
				}
			 else{
				MessageUtil.setMessage("Please enter the correct password", "color:red", "TOP");
				ltyPwdTbId.setText("");
				return;
			}
			}
		catch(Exception e) {
			logger.error("Exception ::",e);
		}
}
	
	public void onClick$backBtnId() {
		Redirect.goTo(PageListEnum.LOYALTY_PROGRAM_REPORTS);
	}
	
	private void userActivitylogAdminUser(String functionalityType,String lolyaltyProgramName) {
		try {
		if(userActivitiesDaoForDML != null) {
			Users currentUser = GetUser.getUserObj();
			String name = null;
			if(currentUser.getLastName()!=null && !currentUser.getLastName().isEmpty()) {
				 name = Utility.getOnlyUserName(currentUser.getFirstName()+" "+currentUser.getLastName());	
			}else {
				 name = Utility.getOnlyUserName(currentUser.getFirstName());
			}
			Users adminUser = usersDao.find(currentOCAdminUserId);
			String adminUsername = Utility.getOnlyUserName(adminUser.getUserName());
	      	userActivitiesDaoForDML.addToActivityList(ActivityEnum.LOYALTY_ADMIN_PASSWORD_BYPASS,currentUser,true,adminUsername,functionalityType,name,lolyaltyProgramName);
		}
		}catch (Exception e) {
			logger.error("LoyaltyDetailedReportController :: userActivitylogAdminUser ::"+e);
		}
	}
}
