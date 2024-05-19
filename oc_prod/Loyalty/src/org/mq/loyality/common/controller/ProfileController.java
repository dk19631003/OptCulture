package org.mq.loyality.common.controller;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.loyality.common.dao.IUserDao;
import org.mq.loyality.common.dao.OTPGeneratedCodesDao;
import org.mq.loyality.common.dao.SMSSettingsDao;
import org.mq.loyality.common.dao.UserDaoImpl;
import org.mq.loyality.common.hbmbean.Contacts;
import org.mq.loyality.common.hbmbean.ContactsLoyalty;
import org.mq.loyality.common.hbmbean.EmailQueue;
import org.mq.loyality.common.hbmbean.LoyaltyProgram;
import org.mq.loyality.common.hbmbean.LoyaltySettings;
import org.mq.loyality.common.hbmbean.OCSMSGateway;
import org.mq.loyality.common.hbmbean.OTPGeneratedCodes;
import org.mq.loyality.common.hbmbean.SMSSettings;
import org.mq.loyality.common.hbmbean.UserOrganization;
import org.mq.loyality.common.hbmbean.Users;
import org.mq.loyality.common.service.GeoService;
import org.mq.loyality.common.service.MembershipService;
import org.mq.loyality.common.service.UserService;
import org.mq.loyality.utils.CaptiwayToSMSApiGateway;
import org.mq.loyality.utils.City;
import org.mq.loyality.utils.Constants;
import org.mq.loyality.utils.GatewayRequestProcessHelper;
import org.mq.loyality.utils.MyCalendar;
import org.mq.loyality.utils.OCConstants;
import org.mq.loyality.utils.PropertyUtil;
import org.mq.loyality.utils.SMSStatusCodes;
import org.mq.loyality.utils.State;
import org.mq.loyality.utils.Utility;
import org.mq.loyality.utils.Zip;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ProfileController {
	@Autowired
	private UserService userService;
	@Autowired
	private GeoService geoService;
	@Autowired
	private OTPGeneratedCodesDao otpDao;
	@Autowired
	private SMSSettingsDao smsSettingsDao;
	@Autowired
	private CaptiwayToSMSApiGateway captiwayToSMSApiGateway;
	@Autowired
	private GatewayRequestProcessHelper gatewayRequestProcessHelper;
	@Autowired
	private MembershipService membershipService;
	@Autowired
	private IUserDao userDAO;
	private static final Logger logger = LogManager.getLogger(Constants.LOYALTY_LOGGER);

	@RequestMapping(value = "/profile", method = RequestMethod.GET)
	public String profile(HttpServletRequest request, ModelMap model) {
		HttpSession session=request.getSession();
		ContactsLoyalty cl = null;
		ContactsLoyalty contactLoyalty;
	if(session.getAttribute("loyalityConfig")!=null)
	{
		 cl = (ContactsLoyalty) session
				.getAttribute("loyalityConfig");
	}

	
	if(cl!=null){
	//contactLoyalty=membershipService.getContactsLoyaltyByCardId(cl.getCardNumber());
		contactLoyalty = cl;
	Contacts contacts = contactLoyalty.getContact();
	Contacts contacts1 =  new Contacts();
	contacts1.setStringBirthDay(MyCalendar.calendarToString(
				contacts.getBirthDay(),
				org.mq.loyality.utils.MyCalendar.FORMAT_DATETIME_STDATE, null));
	contacts1.setStringAnniversary(MyCalendar.calendarToString(
				contacts.getAnniversary(),
				org.mq.loyality.utils.MyCalendar.FORMAT_DATETIME_STDATE, null));
		if((contacts.getFirstName() == null || contacts.getFirstName().trim().length() == 0) && (contacts.getLastName() == null || contacts.getLastName().trim().length() == 0) )
		{
			contacts1.setFirstName("--");
		}else{
			contacts1.setFirstName(contacts.getFirstName());
		}
		contacts1.setLastName(contacts.getLastName());
		if(contacts.getEmailId() == null || contacts.getEmailId().trim().length() == 0){
			contacts1.setEmailId("--");
		}else{
			contacts1.setEmailId(contacts.getEmailId());
		}
		logger.info(" mobile phoneeeeeee"+contacts.getMobilePhone());
		if(contacts.getMobilePhone() == null || contacts.getMobilePhone().trim().length() == 0){
			contacts1.setMobilePhone("--");
		}else{
			contacts1.setMobilePhone(contacts.getMobilePhone());
		}
		StringBuffer sb = new StringBuffer();

		if (contacts.getAddressOne() != null) {
			sb.append(contacts.getAddressOne());
		}
		/*if (contacts.getAddressTwo() != null && !contacts.getAddressTwo().equals("")) {
			if(sb.length() != 0)sb.append(", ");
			sb.append(contacts.getAddressTwo());
		}*/
		if (contacts.getCity() != null && !contacts.getCity().equals("")) {
			if(sb.length() != 0)sb.append(", ");
			sb.append(contacts.getCity());
		}
		if (contacts.getState() != null && !contacts.getState().equals("")) {
			if(sb.length() != 0)sb.append(", ");
			sb.append(contacts.getState());
		}
		if (contacts.getCountry() != null && !contacts.getCountry().equals("")) {
			if(sb.length() != 0)sb.append(", ");
			sb.append(contacts.getCountry());
		}
		if (contacts.getZip() != null && !contacts.getZip().equals("") ) {
			if(sb.length() != 0)sb.append(", ");
			sb.append(contacts.getZip());
		}
		contacts1.setUsState(sb.toString().length()!=0 ? sb.toString() : "--");
		/*if(contactLoyalty.getMobilePhone() == null || contactLoyalty.getMobilePhone().trim().length() == 0){
			contacts.setMobilePhone("--");
		}else{
		contacts.setMobilePhone(contactLoyalty.getMobilePhone());
		}*/
		model.addAttribute("contacts", contacts1);
}
		return "common/profile";
	}

	@RequestMapping(value = "/updateProfile", method = RequestMethod.GET)
	public String updateProfile(HttpServletRequest request, ModelMap model) {
		HttpSession session=request.getSession();
		
		ContactsLoyalty cl = null;
		ContactsLoyalty contactLoyalty;
		Contacts contacts=null;
	if(session.getAttribute("loyalityConfig")!=null)
	{
		 cl = (ContactsLoyalty) session
				.getAttribute("loyalityConfig");
	}
	if(cl!=null){
		//contactLoyalty=membershipService.getContactsLoyaltyByCardId(cl.getCardNumber());
		contactLoyalty = cl;
		contacts = contactLoyalty.getContact();
		contacts.setStringBirthDay(MyCalendar.calendarToString(
				contacts.getBirthDay(),
				org.mq.loyality.utils.MyCalendar.FORMAT_MDATEONLY, null));
		contacts.setStringAnniversary(MyCalendar.calendarToString(
				contacts.getAnniversary(),
				org.mq.loyality.utils.MyCalendar.FORMAT_MDATEONLY, null));
		if (!(MyCalendar.calendarToString(contacts.getBirthDay(),
				org.mq.loyality.utils.MyCalendar.FORMAT_MDATEONLY, null))
				.equals("--")) {
			String split[] = StringUtils.split(MyCalendar.calendarToString(
					contacts.getBirthDay(),
					org.mq.loyality.utils.MyCalendar.FORMAT_MDATEONLY, null),
					"-");
			contacts.setBirthDay1(split[1]);
			contacts.setBirthMonth(split[0]);
			contacts.setBirthYear(split[2]);
		}

		if (!(MyCalendar.calendarToString(contacts.getAnniversary(),
				org.mq.loyality.utils.MyCalendar.FORMAT_MDATEONLY, null))
				.equals("--")) {
			String split1[] = StringUtils.split(MyCalendar.calendarToString(
					contacts.getAnniversary(),
					org.mq.loyality.utils.MyCalendar.FORMAT_MDATEONLY, null),
					"-");

			contacts.setAnnDay(split1[1]);
			contacts.setAnnMonth(split1[0]);
			contacts.setAnnYear(split1[2]);
		}}
		model.addAttribute("contacts", contacts);
		return "common/updateProfile";
	}

	@RequestMapping(value = "/saveProfile", method = RequestMethod.POST)
	public String saveProfile(HttpServletRequest request,
			@ModelAttribute("contacts") Contacts data,
			final RedirectAttributes redirectAttributes,ModelMap model) {
		HttpSession session=request.getSession();
		ContactsLoyalty cl = null;
		ContactsLoyalty contactLoyalty = null;
		Contacts contacts=null;
	if(session.getAttribute("loyalityConfig")!=null)
	{
		 cl = (ContactsLoyalty) session
				.getAttribute("loyalityConfig");
	}
	if(cl!=null){
		//contactLoyalty=membershipService.getContactsLoyaltyByCardId(cl.getCardNumber());
		contactLoyalty = cl;
	 contacts = contactLoyalty.getContact();
		if (data.getBirthDay1() != null && data.getBirthMonth() != null
				&& data.getBirthYear() != null
				&& !(data.getBirthDay1()).equals("0")
				&& !(data.getBirthMonth()).equals("0")
				&& !(data.getBirthYear()).equals("0")) {
			Calendar cal = Calendar.getInstance();
			SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
			Date d1 = null;
			try {
				d1 = df.parse((data.getBirthDay1() + "-" + data.getBirthMonth()
						+ "-" + data.getBirthYear()));
				cal.setTime(d1);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.info(" Exception :: ",e);
			}
			contacts.setBirthDay(cal);
		}

		if (data.getAnnDay() != null && data.getAnnMonth() != null
				&& data.getAnnYear() != null&& !(data.getAnnDay()).equals("0")
						&& !(data.getAnnMonth()).equals("0")
						&& !(data.getAnnYear()).equals("0")) {
			Calendar cal1 = Calendar.getInstance();
			SimpleDateFormat df1 = new SimpleDateFormat("dd-MM-yyyy");
			Date d2 = null;
			try {
				d2 = df1.parse((data.getAnnDay() + "-" + data.getAnnMonth()
						+ "-" + data.getAnnYear()));
				cal1.setTime(d2);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.info(" Exception :: ",e);
			}
			contacts.setAnniversary(cal1);
		}
		contacts.setFirstName(data.getFirstName());
		contacts.setAddressOne(data.getAddressOne());
		contacts.setAddressTwo(data.getAddressTwo());
		//contacts.setMobilePhone(data.getMobilePhone());
		contacts.setLastName(data.getLastName());
		contacts.setGender(data.getGender());
		contacts.setEmailId(data.getEmailId().trim());
		contacts.setState(data.getState());
		contacts.setCountry(data.getCountry());
		contacts.setCity(data.getCity());
		contacts.setZip(data.getZip());
		contactLoyalty.setContact(contacts);
	}
		userService.saveProfile(contacts);
		session.setAttribute("loyalityConfig",contactLoyalty);
		
		model.addAttribute("contacts", contacts);
		return "redirect:/profile";
	}

	@RequestMapping(value = "/saveMobile", method = RequestMethod.POST)

	public String saveMobile(HttpServletRequest request,
			@ModelAttribute("contacts") Contacts data,
			final RedirectAttributes redirectAttributes, ModelMap model) {
		try {
			
			HttpSession session=request.getSession();
			ContactsLoyalty contactLoyality = null;
			Contacts contacts=null;
		if(session.getAttribute("loyalityConfig")!=null)
		{
			 contactLoyality = (ContactsLoyalty) session
					.getAttribute("loyalityConfig");
		
		 contacts = contactLoyality.getContact();
		 LoyaltySettings settings = (LoyaltySettings) session.getAttribute("loyaltySettings");
		 Long orgId = settings.getUserOrgId();
			List<UserOrganization> userList = userService
					.getOrgDetails(orgId);
			UserOrganization user = null;
			if (userList.size() != 0) {
			user = userList.get(0);
			}
			
			String newMobile = data.getMobilePhone();
			String mobileRange[] = null;
			int minDigits = user.getMinNumberOfDigits();
			int maxDigits = user.getMaxNumberOfDigits();
			String countryCarrier = settings.getOrgOwner().getCountryCarrier()+"";
			if(minDigits == maxDigits){
				mobileRange = new String[] { minDigits+""};
			}else{
				mobileRange = new String[] {minDigits+"",maxDigits+""};
			}	
			/*
			boolean isCarrier = false;
			boolean isMobile = false;
			if(mobileRange.length==1){
				isCarrier = (newMobile.length() == Integer.valueOf(mobileRange[0].trim()) ? false : true );
				isMobile = isCarrier ? newMobile.startsWith(countryCarrier) && newMobile.substring((""+countryCarrier+"").length()).length() == Integer.valueOf(mobileRange[0].trim()) : true;
			}else{
				isCarrier = ((newMobile.length() >= Integer.valueOf(mobileRange[0].trim())) && (newMobile.length() <= Integer.valueOf(mobileRange[1].trim()))  ? false : true );
				isMobile = isCarrier ? newMobile.startsWith(String.valueOf(countryCarrier)) && newMobile.substring((""+countryCarrier+"").length()).length() <= Integer.valueOf(mobileRange[0].trim()) && newMobile.substring((""+countryCarrier+"").length()).length() >= Integer.valueOf(mobileRange[1].trim()):true;
			}
			
			newMobile = isMobile && newMobile.startsWith(String.valueOf(countryCarrier)) && isCarrier  ? newMobile.substring((""+countryCarrier+"").length()) : newMobile;*/
		 
			contacts.setMobilePhone(newMobile);
			contacts.setMobileStatus(Constants.CON_MOBILE_STATUS_ACTIVE);
			contactLoyality.setContact(contacts);
			userService.saveProfile(contacts);
			model.addAttribute("contacts", contacts);
			
			/*updateLoyaltyMembrshpPhone(contactLoyality.getContact(),
					data.getMobilePhone());*/
			ContactsLoyalty returnLoyalty = updateLoyaltyMembrshpPhone(contactLoyality.getContact(),contactLoyality.getLoyaltyId(),
					newMobile, settings.getOrgOwner().getCountryCarrier().toString());
			if(returnLoyalty != null){
				logger.info(" shdvghvf "+returnLoyalty.getCardNumber());
				//session=request.getSession();
				session.setAttribute("loyalityConfig", returnLoyalty);
			}
		}
		} catch (Exception e) {
			logger.info(" Exception :: ",e);
		}
		
		return "redirect:/profile";
	}
	
	
	@RequestMapping(value = "/checkMobile", method = RequestMethod.GET)
	public @ResponseBody String checkMobile(HttpServletRequest request,
			
			@RequestParam(value="phone", required=false) String id) {
			logger.info("phone number"+id);
			HttpSession session = request.getSession();
			LoyaltySettings settings = (LoyaltySettings) session.getAttribute("loyaltySettings");
			ContactsLoyalty contactsLoyalty = (ContactsLoyalty) session.getAttribute("loyalityConfig");
			Users user = userService.findByUserId(contactsLoyalty.getUserId());
			Map<String, Object> resultMap  = Utility.validateMobile(user, id);
			if( resultMap.get("isValid")!=null){
				if(! (Boolean) resultMap.get("isValid"))return "103";
	    	}else{return "103";}
			/*if(settings.getLoyaltyType().equalsIgnoreCase("card number"))
			{*/
				ContactsLoyalty tempLtyContact = membershipService
						.getLoyaltyByPrgmAndPhone(
								contactsLoyalty.getProgramId(), id, settings.getOrgOwner().getCountryCarrier().toString());
				LoyaltyProgram loyaltyProgram = membershipService.findById(contactsLoyalty.getProgramId());
				if(loyaltyProgram.getUniqueMobileFlag()=='Y' && tempLtyContact != null)
				{
					return "101";
				}
			//}
			return "102";
			}
	
			public String checkMobile(String id, ContactsLoyalty contactsLoyalty, LoyaltySettings settings) {
			logger.info("phone number"+id);
			Users user = userService.findByUserId(contactsLoyalty.getUserId());
			Map<String, Object> resultMap  = Utility.validateMobile(user, id);
			logger.info(" came from validating mobile... ");
			if( resultMap.get("isValid")!=null){
				if(! (Boolean) resultMap.get("isValid"))return "103";
	    	}else{return "103";}
			/*if(settings.getLoyaltyType().equalsIgnoreCase("card number"))
			{*/
				ContactsLoyalty tempLtyContact = membershipService
						.getLoyaltyByPrgmAndPhone(
								contactsLoyalty.getProgramId(), id, settings.getOrgOwner().getCountryCarrier().toString());
				LoyaltyProgram loyaltyProgram = membershipService.findById(contactsLoyalty.getProgramId());
				if(loyaltyProgram.getUniqueMobileFlag()=='Y' && tempLtyContact != null)
				{
					return "101";
				}
			/*}*/
			return (String) resultMap.get("phone");
			}

	private ContactsLoyalty updateLoyaltyMembrshpPhone(Contacts contact, Long loyaltyId, String newPhone, String countryCarrier) {
		try {
			List<ContactsLoyalty> contactLoyaltyList = userService
					.findLoyaltyListByContactId(contact.getContactId());
			if (contactLoyaltyList == null || contactLoyaltyList.size() == 0)
				return null;
			ContactsLoyalty returnLoyalty = null;
			for (ContactsLoyalty loyalty : contactLoyaltyList) {
				boolean isDifferentMobile = false;
				boolean sendUpdateAlert = false;
				String conMobile = loyalty.getMobilePhone();
				if(loyalty.getLoyaltyId().equals(loyaltyId))returnLoyalty=loyalty;
				// to identify whether entered one is same as previous mobile
				if (newPhone != null && !newPhone.isEmpty()) {
					if (conMobile != null && !conMobile.isEmpty()
							&& !conMobile.equals(newPhone)) {
						if ((conMobile.length() < newPhone.length() && !newPhone
								.endsWith(conMobile))
								|| (newPhone.length() < conMobile.length() && !conMobile
										.endsWith(newPhone))
								|| conMobile.length() == newPhone.length()) {
							isDifferentMobile = true;
							sendUpdateAlert = true;
						}
					} else if (conMobile == null || conMobile.isEmpty()) {
						isDifferentMobile = true;
					}
				}
				if (isDifferentMobile) {
					if (OCConstants.LOYALTY_MEMBERSHIP_TYPE_CARD
							.equalsIgnoreCase(loyalty.getMembershipType())) {
						LoyaltyProgram prgmObj = membershipService
								.findById(loyalty.getProgramId());
						if (prgmObj.getUniqueMobileFlag() == OCConstants.FLAG_YES) {
							ContactsLoyalty tempLtyContact = membershipService
									.getLoyaltyByPrgmAndPhone(
											loyalty.getProgramId(), newPhone, countryCarrier);
							if (tempLtyContact != null)
								continue; 
							
							loyalty.setMobilePhone(newPhone);
							userService.saveOrUpdate(loyalty);
							if (sendUpdateAlert) {
								sendUpdateAlert(loyalty, conMobile, newPhone,
										contact.getEmailId(), contact.getContactId());
							} else {
								sendAddAlert(loyalty, newPhone,
										contact.getEmailId(),
										contact.getContactId());
							}
						} else {
							loyalty.setMobilePhone(newPhone);
							userService.saveOrUpdate(loyalty);
							if (sendUpdateAlert) {
								sendUpdateAlert(loyalty, conMobile, newPhone,
										contact.getEmailId(), contact.getContactId());
							} else {
								sendAddAlert(loyalty, newPhone,
										contact.getEmailId(),
										contact.getContactId());
							}
						}
					} else if (OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE
							.equalsIgnoreCase(loyalty.getMembershipType())) {
						ContactsLoyalty tempContact = membershipService
								.getLoyaltyByPrgmAndMembrshp(
										loyalty.getProgramId(), newPhone);
						if (tempContact != null)
							continue; 
						loyalty.setMobilePhone(newPhone);
						loyalty.setCardNumber(newPhone);
						userService.saveOrUpdate(loyalty);
						if (sendUpdateAlert) {
							sendUpdateAlert(loyalty, conMobile, newPhone,
									contact.getEmailId(), contact.getContactId());
						} else {
							sendAddAlert(loyalty, newPhone,
									contact.getEmailId(),
									contact.getContactId());
						}
					}
				} else
					continue; 
			}
			return returnLoyalty;
		} catch (Exception e) {
			logger.info("Exception is :: ",e);
		}
			return null;
	}
				
	

	

	private void sendAddAlert(ContactsLoyalty loyalty, String newPhone,
			String emailId, Long contactId) {
		try {
			Users user = userService.findByUserId(loyalty.getUserId());
			String phoneSubStr = StringUtils.right(newPhone, 4);
			String orgName = "";
			if (user != null && user.getUserOrganization() != null && user.getUserOrganization().getOrganizationName() != null) {
				orgName = user.getUserOrganization().getOrganizationName();
			}
			if (emailId != null && !emailId.isEmpty()) {
				String messageStr = PropertyUtil
						.getPropertyValueFromDB("ltyPhoneAddTemplate");
				messageStr = messageStr.replace("[toStr]", phoneSubStr);

				EmailQueue email = new EmailQueue(null,
						Constants.EQ_TYPE_LOYALTY_OC_ALERTS, messageStr,
						Constants.EQ_STATUS_ACTIVE, emailId, user,
						MyCalendar.getNewCalendar(), orgName
								+ ": Your Phone Number Updated Successfully!",
						contactId, null);
				userService.saveOrUpdate(email);
			}

			if (!user.isEnableSMS())
				return;

			String message = PropertyUtil
					.getPropertyValueFromDB("ltyPhoneAddSmsMsgTemplate");
			message = message.replace("[orgName]", orgName);
			message = message.replace("[toStr]", phoneSubStr);

			// validate loyalty phone no and new phone no
			Map<String, Object> resultMap = null;

			resultMap = Utility.validateMobile(user, newPhone);
			newPhone = (String) resultMap.get("phone");
			boolean phoneIsValid = false;
			if(resultMap.get("isValid") !=null) phoneIsValid =(Boolean) resultMap.get("isValid");

			if (phoneIsValid) {
				sendSmsAlert(user, newPhone, message);
			}
		} catch (Exception e) {
			logger.info("Exception ::: ",e);
		}
	}

	

	private void sendSmsAlert(Users user, String phone, String message) {

		try {
			OCSMSGateway ocGateway = gatewayRequestProcessHelper
					.getOcSMSGateway(user,
							SMSStatusCodes.defaultSMSOptinGatewayTypeMap
									.get(user.getCountryType()));
			
			if(ocGateway == null){
				return;
			}
			if (!ocGateway.isPostPaid()
					&& !captiwayToSMSApiGateway.getBalance(ocGateway, 1)) {
				return;
			}

			if (!(((user.getSmsCount() == null ? 0 : user.getSmsCount()) - (user
					.getUsedSmsCount() == null ? 0 : user.getUsedSmsCount())) >= 1)) {
				return;
			}

			String msgContent = Constants.STRING_NILL;
			String messageHeader = Constants.STRING_NILL;
			if (SMSStatusCodes.optOutFooterMap.get(user.getCountryType())) {
			messageHeader = findMessageHeader(user);
			}
			if (messageHeader.equals("")) {
				msgContent = message;
			} else {
				msgContent = messageHeader + message;
			}
			String senderId = ocGateway.getSenderId();
			captiwayToSMSApiGateway.sendSingleSms(ocGateway, msgContent, phone, senderId);
			userDAO.updateUsedSMSCount(user.getUserId(), 1);
			return;
		} catch (Exception e) {
			logger.info(" Exception :: ",e);
		}
	}

	private void sendUpdateAlert(ContactsLoyalty loyalty, String conMobile,
			String newPhone, String emailId, Long contactId) {
		try {
			Users user = userService.findByUserId(loyalty.getUserId());
			String conMobileSubStr = StringUtils.right(conMobile, 4);
			String phoneSubStr = StringUtils.right(newPhone, 4);
			String firstName=loyalty.getContact().getFirstName();
			if(firstName == null){
				firstName = "Valued Customer";
			}
			String orgName = "";
			if (user != null && user.getUserOrganization() != null) {
				if (!user.isEnableSMS())
					return;
				orgName = user.getUserOrganization().getOrganizationName();
			}
			if (emailId != null && !emailId.isEmpty()) {
				
				StringBuffer sb=new StringBuffer(); 
		        sb.append("<html><head><body>");
				sb.append(" Hi "+firstName+",");
				sb.append("<BR>");
	
	sb.append("You have successfully updated your phone number from XXXXXX"+ conMobileSubStr+" to  XXXXXX"+ phoneSubStr+".");
	sb.append(" Now you can make use of the new number for earning and redeeming loyalty in all future purchases. In case you have not initiated this change, we request you to contact the store immediately. ");
	sb.append("<BR>");
	firstName = loyalty.getCardNumber().toString();
	if(firstName == null || firstName.isEmpty()){
		firstName = "Not Available";
	}
	sb.append("Your Membership #:"+firstName);
	sb.append("<BR>");
	sb.append("Happy Shopping!");
	sb.append("<BR>");
	firstName = user.getFirstName() != null ? user.getFirstName()+" " : ""+user.getLastName() != null ? user.getLastName() : "";
	sb.append(firstName);
	sb.append("<BR>");
	sb.append(orgName);
	
	sb.append("</body></head></html>");
	
		EmailQueue email = new EmailQueue(null,Constants.EQ_TYPE_LOYALTY_EMAIL_ALERTS, sb.toString(),
				Constants.EQ_STATUS_ACTIVE,emailId, user,MyCalendar.getNewCalendar(),"Membership mobile number updated", 
			  contactId, null);
				
				userService.saveOrUpdate(email);
			}

			String message = orgName
					+ ": You have changed your mobile# from "
					+ "XXXXXX"
					+ conMobileSubStr
					+ " to XXXXXX"
					+ phoneSubStr
					+ ". "
					+ "Please make use of updated no. for earning/redeeming reward in future. "
					+ "If you have not initiated this change, please contact store.";
			// validate loyalty phone no and new phone no
			Map<String, Object> resultMap = null;

			resultMap = Utility.validateMobile(user, conMobile);
			conMobile = (String) resultMap.get("phone");
			
			boolean conMobileIsValid = false;
			boolean phoneIsValid =false;
	if( resultMap.get("isValid")!=null){
			conMobileIsValid = (Boolean) resultMap.get("isValid");
	}
			resultMap = Utility.validateMobile(user, newPhone);
			newPhone = (String) resultMap.get("phone");
			if( resultMap.get("isValid")!=null){
			phoneIsValid = (Boolean) resultMap.get("isValid");
			}
			if (conMobileIsValid) {
				sendSmsAlert(user, conMobile, message);
			}
			if (phoneIsValid) {
				sendSmsAlert(user, newPhone, message);
			}
		} catch (Exception e) {
			logger.info(" Exception :: ",e);
		}
	}

	@RequestMapping(value = "/states", method = RequestMethod.GET)
	public @ResponseBody
	Set<State> findAllStates() {
		return this.geoService.findAllStates();
	}

	@RequestMapping(value = "/cities", method = RequestMethod.GET)
	public @ResponseBody
	Set<City> citiesForState(
			@RequestParam(value = "stateName", required = true) String state) {
		return this.geoService.findCitiesForState(state);
	}

	@RequestMapping(value = "/zip", method = RequestMethod.GET)
	public @ResponseBody
	Set<Zip> zipForCIty(
			@RequestParam(value = "cityName", required = true) String city) {
		return this.geoService.findzipForCIty(city);
	}

	@RequestMapping(value = "/sendVerificationCode", method = RequestMethod.POST)
	public @ResponseBody
	OTPGeneratedCodes getOTPVerificationCode(HttpServletRequest request) {
		HttpSession session=request.getSession();
		ContactsLoyalty contactLoyality = null;
	if(session.getAttribute("loyalityConfig")!=null)
	{
		 contactLoyality = (ContactsLoyalty) session
				.getAttribute("loyalityConfig");
	}
	
	String phone = request.getParameter("phone");
	LoyaltySettings settings = (LoyaltySettings) session.getAttribute("loyaltySettings");
	String status = checkMobile(phone, contactLoyality,settings);
	OTPGeneratedCodes otpgeneratedcode = null;
	if(status.equalsIgnoreCase("103")||(status.equalsIgnoreCase("101"))){
		otpgeneratedcode = new OTPGeneratedCodes();
		otpgeneratedcode.setStatus(status);
		return otpgeneratedcode;
	}
	
	/*if(contactLoyality.getContact().getMobilePhone()!=null && ((contactLoyality.getContact().getMobilePhone().startsWith(settings.getOrgOwner().getCountryCarrier()+"") && contactLoyality.getContact().getMobilePhone().equalsIgnoreCase(settings.getOrgOwner().getCountryCarrier()+""+phone))
			|| !(contactLoyality.getContact().getMobilePhone().startsWith(settings.getOrgOwner().getCountryCarrier()+"")) && contactLoyality.getContact().getMobilePhone().equalsIgnoreCase(phone.substring((""+settings.getOrgOwner().getCountryCarrier()).length()) ))){*/
	if(contactLoyality.getContact().getMobilePhone()!=null && ((contactLoyality.getContact().getMobilePhone().contains(phone+"") && phone.contains(contactLoyality.getContact().getMobilePhone())  ))){
		otpgeneratedcode = new OTPGeneratedCodes();
		otpgeneratedcode.setStatus("105");
		return otpgeneratedcode;
	}
	phone = status;
		if(contactLoyality!=null){
		Users user = userService.findByUserId(contactLoyality.getUserId());
		
		try {
			otpgeneratedcode = findOTPCodeByPhone(phone, user.getUserId(),
					OCConstants.OTP_GENERATED_CODE_STATUS_ACTIVE);
			if (otpgeneratedcode != null) {
				Calendar currCal = Calendar.getInstance();
				Calendar createdDate = otpgeneratedcode.getCreatedDate();
				long currtime = currCal.getTimeInMillis() / 1000;
				long createdtime = createdDate.getTimeInMillis() / 1000;
				long timeDiff = currtime - createdtime;
				long duration = 3 * 60 * 60;
				if (timeDiff < duration) {
					sendOTPCode(user, phone, otpgeneratedcode.getOtpCode());
					otpgeneratedcode.setSentCount(otpgeneratedcode
							.getSentCount() + 1);
					saveOTPgeneratedcode(otpgeneratedcode);
					return otpgeneratedcode;
				} else {
					otpgeneratedcode
							.setStatus(OCConstants.OTP_GENERATED_CODE_STATUS_EXPIRED);
					saveOTPgeneratedcode(otpgeneratedcode);
				}
			}
			String activeOTPCode = generateOtpCode();
			otpgeneratedcode = new OTPGeneratedCodes();
			otpgeneratedcode.setCreatedDate(Calendar.getInstance());
			otpgeneratedcode.setOtpCode(activeOTPCode);
			otpgeneratedcode.setPhoneNumber(phone);
			otpgeneratedcode
					.setStatus(OCConstants.OTP_GENERATED_CODE_STATUS_ACTIVE);
			otpgeneratedcode.setUserId(user.getUserId());
			otpgeneratedcode.setSentCount(1L);
			sendOTPCode(user, phone, otpgeneratedcode.getOtpCode());
			saveOTPgeneratedcode(otpgeneratedcode);
		
		} catch (Exception e) {
			logger.info(" Exception :: ",e);
		}
		}
		return otpgeneratedcode;
	}

	private OTPGeneratedCodes findOTPCodeByPhone(String phone, Long userId,
			String status) throws Exception {
		return otpDao.findOTPCodeByPhone(phone, userId, status);
	}

	private void saveOTPgeneratedcode(OTPGeneratedCodes otpgenCode)
			throws Exception {
		otpDao.saveOrUpdate(otpgenCode);
	}

	private String generateOtpCode() {
		final long MAX_NUMBER = 999999L;
		final long MIN_NUMBER = 100000L;
		String otpCode = "";
		String randNoStr = "";
		Long randNo = Long.valueOf(new Random().nextLong());
		if (randNo.toString().startsWith("-")) {
			randNoStr = randNo.toString().replace("-", "");
		} else {
			randNoStr = randNo.toString();
		}
		randNo = Long
				.valueOf((Long.parseLong(randNoStr) % (MAX_NUMBER - MIN_NUMBER))
						+ MIN_NUMBER);
		otpCode = randNo.toString();
		return otpCode;
	}

	private void sendOTPCode(Users user, String phone, String otpCode) {

		logger.info("OTP code is=================>"+otpCode);
		try {
			OCSMSGateway ocGateway = gatewayRequestProcessHelper
					.getOcSMSGateway(user,
							SMSStatusCodes.defaultSMSOptinGatewayTypeMap
									.get(user.getCountryType()));
			if (!(((user.getSmsCount() == null ? 0 : user.getSmsCount()) - (user
					.getUsedSmsCount() == null ? 0 : user.getUsedSmsCount())) >= 1) ||  ocGateway == null) {
				
				return;
			}
			String msgContent="";
			if(user.getCountryType().equals(Constants.SMS_COUNTRY_INDIA)) {
				msgContent = PropertyUtil.getPropertyValueFromDB("UpdatePhoneOtpMessage");
				msgContent = msgContent.replace("[OTP]",otpCode);
				msgContent = msgContent.replace("[BrandName]",ocGateway.getSenderId());
			}else {
				msgContent = PropertyUtil.getPropertyValueFromDB(Constants.OTP_MESSAGE);
			}
			if(msgContent == null) return;
			msgContent = msgContent.replaceFirst("<otpCode>", otpCode);
			if (SMSStatusCodes.optOutFooterMap.get(user.getCountryType())) {
				msgContent = findMessageHeader(user)+msgContent;
			}
			
			/*if (messageHeader.equals("")) {
				msgContent = 
			} else {
				msgContent = messageHeader + PropertyUtil.getPropertyValueFromDB(Constants.OTP_MESSAGE).replaceFirst("[otpCode]", otpCode);
			}*/
			String senderId = ocGateway.getSenderId();
			try {
				captiwayToSMSApiGateway.sendSingleSms(ocGateway, msgContent, phone,
						senderId);
			    	userDAO.updateUsedSMSCount(user.getUserId(), 1);
			  
			   } catch (Exception e) {
			    logger.error("Exception While sending OTP SMS ",e);
			   }
				
			
			
			
		} catch (Exception e) {
			logger.info(" Exception :: ",e);
		}
	}

	private String findMessageHeader(Users user) {
		String messageHeader = Constants.STRING_NILL;
		try {
			List<SMSSettings> smsSettings = null;
		
				smsSettings = smsSettingsDao.findByUser(user.getUserId());
				if (smsSettings != null) {
					SMSSettings optinSettings = null;
					SMSSettings optOutSettings = null;
					SMSSettings helpSettings = null;
					for (SMSSettings eachSMSSetting : smsSettings) {
						if (eachSMSSetting.getType().equalsIgnoreCase(
								OCConstants.SMS_PROGRAM_KEYWORD_TYPE_OPTIN))
							optinSettings = eachSMSSetting;
						else if (eachSMSSetting.getType().equalsIgnoreCase(
								OCConstants.SMS_PROGRAM_KEYWORD_TYPE_OPTOUT))
							optOutSettings = eachSMSSetting;
						else if (eachSMSSetting.getType().equalsIgnoreCase(
								OCConstants.SMS_PROGRAM_KEYWORD_TYPE_HELP))
							helpSettings = eachSMSSetting;
					}
					if (optinSettings != null && messageHeader.isEmpty())
						messageHeader = optinSettings.getMessageHeader();
					else if (optOutSettings != null && messageHeader.isEmpty())
						messageHeader = optOutSettings.getMessageHeader();
					else if (helpSettings != null && messageHeader.isEmpty())
						messageHeader = helpSettings.getMessageHeader();
				}
		} catch (Exception e) {
			logger.info(" Exception :: ",e);
			return messageHeader;

		}
		return messageHeader;

	}

}
