package org.mq.loyality;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.loyality.common.dao.ContactsLoyaltyDao;
import org.mq.loyality.common.dao.IUserDao;
import org.mq.loyality.common.hbmbean.Contacts;
import org.mq.loyality.common.hbmbean.ContactsLoyalty;
import org.mq.loyality.common.hbmbean.EmailQueue;
import org.mq.loyality.common.hbmbean.LoyaltySettings;
import org.mq.loyality.common.hbmbean.OCSMSGateway;
import org.mq.loyality.common.hbmbean.UserOrganization;
import org.mq.loyality.common.hbmbean.Users;
import org.mq.loyality.common.service.UserService;
import org.mq.loyality.utils.CaptiwayToSMSApiGateway;
import org.mq.loyality.utils.Configuration;
import org.mq.loyality.utils.Constants;
import org.mq.loyality.utils.EncryptDecryptLtyMembshpPwd;
import org.mq.loyality.utils.GatewayRequestProcessHelper;
import org.mq.loyality.utils.MembershipDetails;
import org.mq.loyality.utils.OCConstants;
import org.mq.loyality.utils.PropertyUtil;
import org.mq.loyality.utils.SMSStatusCodes;
import org.mq.loyality.utils.Utility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;


@Controller
public class HomeController {
	@Autowired
	private UserService userService;
	@Autowired
	private CaptiwayToSMSApiGateway captiwayToSMSApiGateway;
	@Autowired
	private GatewayRequestProcessHelper gatewayRequestProcessHelper;
	@Autowired
	private IUserDao userDAO;
	@Autowired
	private ContactsLoyaltyDao contactsLoyalityDao;
	private static final Logger logger = LogManager.getLogger(Constants.LOYALTY_LOGGER);
	// private static Logger logger = Logger.getLogger(HomeController.class);

	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public ModelAndView loginForm(HttpServletRequest request,@ModelAttribute("loyalitySettings") LoyaltySettings loyalitySettings)
			throws IOException {
		//String name = request.getServerName();
		
		
		
		 String name1 = "http://".concat(request.getServerName());
		String orgname = "";
		loyalitySettings = userService.getSettingDetails(name1);
		
		
		Configuration config = Configuration.getInstance();
		List<UserOrganization> userList = userService
				.getOrgDetails(loyalitySettings.getUserOrgId());
		
		if (userList.size() != 0) {
			UserOrganization user = userList.get(0);
			orgname = user.getOrganizationName();
		} else {
			orgname = "default";
		}
		String filePath = config.getProperty("path") + orgname + "/"
				+ orgname + ".png";
		logger.info("file path is============dsddsdsdsd===ssasasasas===>"+filePath);
		// HttpSession session=request.getSession(true);
		// session.setAttribute("image",filePath);
		// session.setAttribute("loyalitySettings", loyalitySettings);
		ModelAndView model=null;
		if(loyalitySettings.getLoyaltyType().equals("Mobile Number"))
		 model = new ModelAndView("common/login-mobile");
		else
			 model = new ModelAndView("common/login-form");
		model.addObject("loyalitySettings", loyalitySettings);
		model.addObject("colorCode",loyalitySettings.getColorCode());
		model.addObject("image", filePath);
		model.addObject("userId",loyalitySettings.getUserId());
		return model;
	}

	@RequestMapping(value = "/error-login", method = RequestMethod.GET)
	public ModelAndView invalidLogin() throws AuthenticationException {
		ModelAndView modelAndView = new ModelAndView("common/login-form");
		modelAndView.addObject("error", true);
		return modelAndView;
	}
	
	
	@RequestMapping(value = "/loginMobile", method = RequestMethod.GET)
	public ModelAndView loginForm1(HttpServletRequest request,@ModelAttribute("loyalitySettings") LoyaltySettings loyalitySettings)
			throws IOException {
		//String name = request.getServerName();
		
		 String name1 = "http://".concat(request.getServerName());
		String orgname = "";
		loyalitySettings = userService.getSettingDetails(name1);
		Configuration config = Configuration.getInstance();
		List<UserOrganization> userList = userService
				.getOrgDetails(loyalitySettings.getUserOrgId());
		
		if (userList.size() != 0) {
			UserOrganization user = userList.get(0);
			orgname = user.getOrganizationName();
		} else {
			orgname = "default";
		}
		/*String filePath = config.getProperty("path") + orgname + "/"
				+ orgname + ".png";*/
		String filepath=loyalitySettings.getPath();
		

		// HttpSession session=request.getSession(true);
		// session.setAttribute("image",filePath);
		// session.setAttribute("loyalitySettings", loyalitySettings);

		ModelAndView model = new ModelAndView("common/login-mobile");
		model.addObject("loyalitySettings", loyalitySettings);
		model.addObject("colorCode",loyalitySettings.getColorCode());
		model.addObject("image", filepath);
		model.addObject("userId",loyalitySettings.getUserId());
		return model;
	}

	@RequestMapping(value = "/denied", method = RequestMethod.GET)
	public ModelAndView devied() throws AuthenticationException {
		ModelAndView modelAndView = new ModelAndView("denied");
		modelAndView.addObject("error", true);
		return modelAndView;
	}
	
	@RequestMapping(value = "/changePassword", method = RequestMethod.GET)
	public String successLogin(HttpServletRequest request,Model model) {
		
		HttpSession session=request.getSession();
		ContactsLoyalty contactLoyality = (ContactsLoyalty) session.getAttribute("loyalityConfig");
		//String pass = "";
		try {
			//pass = EncryptDecryptLtyMembshpPwd.decrypt(contactLoyality.getMembershipPwd());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.info(" Exception :: ",e);
		}
		//request.setAttribute("password",pass);
		return "common/changePassword";
	}
	@RequestMapping(value = "/deleteCookies", method = RequestMethod.GET)
	public @ResponseBody String deleteCookies(HttpServletRequest request,HttpServletResponse response) {
		logger.info(" came to delete cookies");
		/*try {
			Thread.sleep(150000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		Cookie cookies[] = request.getCookies();
		logger.info(" length "+cookies.toString()+" "+cookies.length);
		for(Cookie cookie: cookies){
			String cName = cookie.getName();
			logger.info(" cookie "+cName);
			if(cName.equals("lt") || cName.equals("lu") || cName.equals("lp")){
				cookie.setMaxAge(0);
				logger.info("logout  ---> "+cookie.getName());
				response.addCookie(cookie);
			}
		}
		logger.info(" bshdhgv f "+request.getContextPath());
		try {
			response.sendRedirect("logout");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.info(" Exception :: ",e);
		}
		return "done";
	}
	
	@RequestMapping(value = "/changePass", method = RequestMethod.POST)
	public String changePass(HttpServletRequest request,Model model,@RequestParam(value = "cpass") String cpass) {
		HttpSession session=request.getSession();
		ContactsLoyalty cl = (ContactsLoyalty) session.getAttribute("loyalityConfig");
		try {
			cl.setMembershipPwd(EncryptDecryptLtyMembshpPwd.encryptMemPassword(cpass));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.info(" Exception :: ",e);
		}
		userService.savePassword(cl);
		MembershipDetails memDetails=(MembershipDetails) session.getAttribute("memDetails");
		model.addAttribute("membership", memDetails);
		
		String clientTime = request.getParameter("clientTime");
		logger.info(" time :: "+clientTime);
		try{
			String st1[] = clientTime.split(" ");
		String st[]=st1[0].split("-");
		if(st[0].length()!=2){st[0]="0"+st[0];}st[0] = st[0]+"-";
		if(st[1].length()!=2){st[1]="0"+st[1];}st[1] = st[1]+"-";
		
		clientTime = Constants.STRING_NILL;
		for(String s:st){
			clientTime += s;
		}
		clientTime = clientTime+" ";
		String st2[] = st1[1].split(":");
		if(st2[0].length() !=2){st2[0] = "0"+st2[0];}st2[0] = st2[0]+":";
		if(st2[1].length() !=2){st2[1] = "0"+st2[1];}st2[1] = st2[1]+":";
		if(st2[2].length() !=2){st2[2] = "0"+st2[2];}st2[2] = st2[2];
		for(String s:st2){
			clientTime += s;
		}
		}catch(Exception e){
			logger.info(" Time from client is wrong ::: "+clientTime,e);
		}
		/*long sysTime = Calendar.getInstance().getTimeInMillis();
		long longTime = (Long.parseLong(clientTime))*60*1000;*/
		/*Date d = new Date(1990, 1, 1, 0, 0, 0);
		//Calendar cal = Calendar.getInstance();
		//cal.set(1989, 12, 31, 23, 57, 0);
		TimeZone timeZone = Calendar.getInstance().getTimeZone();
		long offSet = timeZone.getOffset(Calendar.ZONE_OFFSET);
		TimeZone tz = Calendar.getInstance().setTimeZone(offset);
		sysTime = sysTime-offSet;
		sysTime = sysTime+longTime;
		System.out.println(" longTime ::: "+longTime);
		TimeZone timeZone =  TimeZone.getTimeZone("GMT" + strTimeZone);
		String time = MyCalendar.calendarToString(Calendar.getInstance(), MyCalendar.FORMAT_DATE_FORMAT, timeZone);*/
		//long serverTime = cal.getTimeInMillis();
		//System.out.println("current time in millis "+serverTime);
		//long diff = serverTime - longTime ;
		//System.out.println("difference ::: "+diff);
		//longTime += diff;
		//System.out.println("setting time "+longTime);
		/*Date d = new Date(longTime);
		System.out.println(d.toString());
		Calendar cal1 = Calendar.getInstance();
		cal1.setTimeInMillis(sysTime);
		String time = MyCalendar.calendarToString(cal1, MyCalendar.FORMAT_DATE_FORMAT);*/
		String time = clientTime;
		LoyaltySettings loyalitySettings= (LoyaltySettings)session.getAttribute("loyaltySettings");
		String orgName="";
		List<UserOrganization> userList = userService
				.getOrgDetails(loyalitySettings.getUserOrgId());
		
		if (userList.size() != 0) {
			UserOrganization user = userList.get(0);
			orgName = user.getOrganizationName();
		} 
   	 Contacts contact = cl.getContact();
   	 String fName = contact.getFirstName();
   	 if(fName == null || fName.isEmpty()){
   		 fName = "Valued Customer";
   	 }
   	String sb= Constants.STRING_NILL;
   sb =	PropertyUtil.getPropertyValueFromDB("WebPortalPswdChange");
   Users user = userService.findByUserId(cl.getUserId());
  if(sb != null){
	  fName = Matcher.quoteReplacement(fName);
   sb = sb.replaceFirst("<firstName>", fName);
fName = cl.getCardNumber().toString();
if(fName == null || fName.isEmpty()){
fName = "Not Available";
}
sb = sb.replaceFirst("<updatedTime>", time);
sb = sb.replaceFirst("<userName>", fName);
cpass = Matcher.quoteReplacement(cpass);
sb = sb.replaceFirst("<password>", cpass);
cpass = user.getFirstName() != null ? user.getFirstName()+" " : ""+user.getLastName() != null ? user.getLastName() : "";
cpass = Matcher.quoteReplacement(cpass);
sb = sb.replaceFirst("<senderName>", cpass);
orgName = Matcher.quoteReplacement(orgName);
sb = sb.replaceFirst("<OrganizationName>", orgName);
   	EmailQueue queue=new EmailQueue(orgName+": "+(Constants.SUBJECT_CHANGE_PASSWORD), sb.toString(), Constants.EQ_TYPE_LOYALTY_EMAIL_ALERTS, "Active", cl.getContact().getEmailId(), org.mq.loyality.utils.MyCalendar.getNewCalendar());
   	userService.saveOrUpdate(queue);
   }
   	if(cl.getMobilePhone() != null && !cl.getMobilePhone().isEmpty()){
   	Map<String, Object> resultMap  = Utility.validateMobile(user, cl.getMobilePhone());
   	String cardId = (String) resultMap.get("phone");
   	boolean conMobileIsValid = false;
   	if( resultMap.get("isValid")!=null){
			conMobileIsValid = (Boolean) resultMap.get("isValid");
   	}
   	if (conMobileIsValid) {
   	StringBuffer sb1=new StringBuffer(); 
       sb1.append("Password of your loyalty membership# ");
       sb1.append(fName+" was successfully updated on ");
       sb1.append(time+" .");
       OCSMSGateway ocGateway;
	try {
		ocGateway = gatewayRequestProcessHelper.getOcSMSGateway(user, 
					SMSStatusCodes.defaultSMSOptinGatewayTypeMap.get(user.getCountryType()));
		if(ocGateway != null){
		String senderId = ocGateway.getSenderId();
		captiwayToSMSApiGateway.sendSingleSms(ocGateway,sb1.toString(),cardId,senderId);
		userDAO.updateUsedSMSCount(user.getUserId(), 1);
		}
	} catch (Exception e) {
		logger.info("Exception :::: ",e);
	}
   	}
    }
    
		
   	
		return "redirect:/membership";
	}
	
@RequestMapping(value = "/cantLogin", method = RequestMethod.POST)
public @ResponseBody String cantLogin(HttpServletRequest request) throws Exception {
		 String cardId=request.getParameter("cardid");
		 String name1 = "http://".concat(request.getServerName());
		 LoyaltySettings loyalitySettings = userService.getSettingDetails(name1);
	     List<ContactsLoyalty>cList=userService.getUser(cardId.toString().trim(),loyalitySettings);
	     if(cList!=null && cList.size()==0)
	     return "Invalid EmailId /Mobile# /Card# ";
		
			
			String orgName="";
			List<UserOrganization> userList = userService
					.getOrgDetails(loyalitySettings.getUserOrgId());
			
			if (userList.size() != 0) {
				UserOrganization user = userList.get(0);
				orgName = user.getOrganizationName();
			} 
	     if(cList!=null && cList.size()>0){
	     for(ContactsLoyalty cl:cList)
	     {
	    	 if(cl.getMembershipStatus().equalsIgnoreCase("Active")) {
	    	 Contacts contact = cl.getContact();
	    	 String fName = contact.getFirstName();
	    	 if(fName == null || fName.isEmpty()){
	    		 fName = "Valued Customer";
	    	 }
	    	StringBuffer sb=new StringBuffer(); 
	        sb.append("<html><head><body>");
			sb.append(" Hi "+fName+",<BR><BR>"+"You are receiving this email because you requested to retrieve your log-in credentials from the loyalty web-portal.  Here is your loyalty membership details:\r\n ");
			sb.append("<BR>");
sb.append("<BR>");
fName = cl.getCardNumber() != null ? cl.getCardNumber()+"":null ;
if(fName == null || fName.isEmpty()){
	fName = "Not Available";
}
sb.append("Username : "+fName);
//fName = cl.getMembershipPwd();
/*if(fName == null || fName.isEmpty()){
	fName = "Not Available";
}else{*/
	//fName = EncryptDecryptLtyMembshpPwd.decrypt(fName);
//}
logger.info("Customer Email:::"+cl.getContact().getEmailId());
sb.append("<BR>");
//sb.append("Password : "+ "Can not share");
sb.append("<BR><BR>");
sb.append(" You can sign in to your account at "+"<a href="+loyalitySettings.getUrlStr()+request.getContextPath()+">"+loyalitySettings.getUrlStr()+request.getContextPath()+"</a>"+" anytime to view your current loyalty balance, recent purchases and even update your profile.");
sb.append("<BR><BR>");
sb.append(" Feel free to contact us should you require any assistance.");
sb.append(" Note: Please keep this email in your mailbox to avoid losing your log-in details.");
sb.append("<BR><BR>");
sb.append("Happy Shopping!\r\n ");
sb.append("<BR><BR>");
Users user = userService.findByUserId(cl.getUserId());
fName = user.getFirstName() != null ? user.getFirstName()+" " : ""+user.getLastName() != null ? user.getLastName() : "";
sb.append(fName+"<BR>");
sb.append(orgName);
sb.append("</body></head></html>");
			if(cl.getContact().getEmailId() !=null && !cl.getContact().getEmailId().isEmpty()){
				logger.info("Inserting in email queue:::");
	    	EmailQueue queue=new EmailQueue(orgName+": "+Constants.SUBJECT_CANT_LOGIN, sb.toString(), Constants.EQ_TYPE_LOYALTY_EMAIL_ALERTS, "Active", cl.getContact().getEmailId(), org.mq.loyality.utils.MyCalendar.getNewCalendar());
	    	userService.saveOrUpdate(queue);
			}
	    	if(cl.getMobilePhone() != null && !cl.getMobilePhone().isEmpty()){
	    	Map<String, Object> resultMap  = Utility.validateMobile(user, cl.getMobilePhone());
	    	cardId = (String) resultMap.get("phone");
	    	boolean conMobileIsValid = false;
	    	if( resultMap.get("isValid")!=null){
				conMobileIsValid = (Boolean) resultMap.get("isValid");
	    	}
	    	if (conMobileIsValid) {
	    	StringBuffer sb1=new StringBuffer(); 
	        /*sb1.append(orgName+":");*/
	    	fName = cl.getCardNumber().toString();
	    	if(fName == null || fName.isEmpty()){
	    		fName = "Not Available";
	    	}
	        sb1.append("Please enter your membership# as ");
	        sb1.append(fName);//+" & password as ");
	       // fName = cl.getMembershipPwd();
	        //if(fName == null || fName.isEmpty()){
	        	//fName = "Can not share";
	        //}else{
	        	//fName = EncryptDecryptLtyMembshpPwd.decrypt(fName);
	       // }
	        sb1.append(fName+" to log-in to your loyalty account.");
	        OCSMSGateway ocGateway = gatewayRequestProcessHelper.getOcSMSGateway(user, 
					SMSStatusCodes.defaultSMSOptinGatewayTypeMap.get(user.getCountryType()));
	        if(ocGateway != null){
	        String senderId = ocGateway.getSenderId();
	        captiwayToSMSApiGateway.sendSingleSms(ocGateway,sb1.toString(),cardId,senderId);
	        userDAO.updateUsedSMSCount(user.getUserId(), 1);
	        }
	    	}
	     }
	     }else if (cl.getMembershipStatus().equalsIgnoreCase("Closed")){
	    	 Users user = userService.findByUserId(cl.getUserId());
	    	 ContactsLoyalty newCl =  contactsLoyalityDao.findByLoyaltyId(cl.getUserId(), cl.getTransferedTo());
	    	 if(newCl != null){
	    	 if(cl.getContact().getEmailId() !=null && !cl.getContact().getEmailId().isEmpty()){
	    	 String emailTemplate = PropertyUtil.getPropertyValueFromDB("transferCantLoginMsgTemplate");
	    	 Contacts contact=cl.getContact();
	    	 String fName = contact.getFirstName();
	    	 if(fName == null || fName.isEmpty()){
	    		 fName = "Valued Customer";
	    	 }
	    	 emailTemplate = emailTemplate.replace("[firstName]", fName);
	    	 fName = newCl.getCardNumber() != null ?newCl.getCardNumber().toString():null;
	    	 if(fName == null || fName.isEmpty()){
	    	 	fName = "Not Available";
	    	 }
	    	 emailTemplate = emailTemplate.replace("[newMembership]",  fName);
	    	// fName = newCl.getMembershipPwd();
	    	 //if(fName == null || fName.isEmpty()){
	    	 	fName = "Can not share";
	    	 //}else{
	    	 	//fName = EncryptDecryptLtyMembshpPwd.decrypt(fName);
	    	 //}
	    	 emailTemplate = emailTemplate.replace("[password]", fName);
	    	 fName = cl.getCardNumber() != null ? cl.getCardNumber()+"":null ;
	    	 if(fName == null || fName.isEmpty()){
	    	 	fName = "Not Available";
	    	 }
	    	 emailTemplate = emailTemplate.replace("[oldMembership]", fName);
	    	 fName = user.getFirstName() != null ? user.getFirstName()+" " : ""+user.getLastName() != null ? user.getLastName() : "";
	    	 emailTemplate = emailTemplate.replace("[senderName]", fName);
	    	 
	    	 emailTemplate = emailTemplate.replace("[organizationName]", orgName);
	    	 EmailQueue queue=new EmailQueue(orgName+": "+Constants.SUBJECT_CANT_LOGIN, emailTemplate.toString(), Constants.EQ_TYPE_LOYALTY_EMAIL_ALERTS, "Active", cl.getContact().getEmailId(), org.mq.loyality.utils.MyCalendar.getNewCalendar());
		    	userService.saveOrUpdate(queue);
	    	 }
	    	 if(cl.getMobilePhone() != null && !cl.getMobilePhone().isEmpty()){
	    		 String smsTemplate = PropertyUtil.getPropertyValueFromDB("transferCantLoginSMSMsgTemplate");
	 	    	Map<String, Object> resultMap  = Utility.validateMobile(user, cl.getMobilePhone());
	 	    	cardId = (String) resultMap.get("phone");
	 	    	boolean conMobileIsValid = false;
	 	    	if( resultMap.get("isValid")!=null){
	 				conMobileIsValid = (Boolean) resultMap.get("isValid");
	 	    	}
	 	    	smsTemplate = smsTemplate.replace("[Organization Name]", orgName);
	 	    	if (conMobileIsValid) {
	 	        /*sb1.append(orgName+":");*/
	 	    	String fName = cl.getCardNumber().toString();
	 	    	if(fName == null || fName.isEmpty()){
	 	    		fName = "Not Available";
	 	    	}
	 	    	smsTemplate = smsTemplate.replace("[oldMembership]", fName);
	 	       
	 	    	fName = newCl.getCardNumber() != null ?newCl.getCardNumber().toString():null;
		    	 if(fName == null || fName.isEmpty()){
		    	 	fName = "Not Available";
		    	 }
		    	 smsTemplate = smsTemplate.replace("[newMembership]",  fName);
		    	// fName = newCl.getMembershipPwd();
		    	 //if(fName == null || fName.isEmpty()){
		    	 	fName = "Can not share";
		    	 //}else{
		    	 	//fName = EncryptDecryptLtyMembshpPwd.decrypt(fName);
		    	 //}
		    	 smsTemplate = smsTemplate.replace("[password]",  fName);
	 	        OCSMSGateway ocGateway = gatewayRequestProcessHelper.getOcSMSGateway(user, 
	 					SMSStatusCodes.defaultSMSOptinGatewayTypeMap.get(user.getCountryType()));
	 	        if(ocGateway != null){
	 	        String senderId = ocGateway.getSenderId();
	 	        captiwayToSMSApiGateway.sendSingleSms(ocGateway,smsTemplate,cardId,senderId);
	 	        userDAO.updateUsedSMSCount(user.getUserId(), 1);
	 	        }
	 	    	}
	 	     }
	    	 }
	     }
	    	 
	     }
	     }
		return "You'll be receiving your log-in credentials at your registered email address/mobile # shortly.";
	}
}
