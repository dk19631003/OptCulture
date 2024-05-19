package org.mq.loyality.common.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.loyality.common.hbmbean.ContactsLoyalty;
import org.mq.loyality.common.hbmbean.LoginDetails;
import org.mq.loyality.common.hbmbean.LoyaltySettings;
import org.mq.loyality.common.hbmbean.UserOrganization;
import org.mq.loyality.common.service.UserService;
import org.mq.loyality.utils.Constants;
import org.mq.loyality.utils.EncryptDecryptLtyMembshpPwd;
import org.mq.loyality.utils.MyAuthenticationToken;
import org.mq.loyality.utils.OCConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;


/**
 * @author swapna.ayyalaraju
 *
 */

@Controller
public class LoginController {
	@Autowired
	private UserService userService;
	@Autowired
	AuthenticationManager authenticationManager;
	private static final Logger logger = LogManager.getLogger(Constants.LOYALTY_LOGGER);
	private static Map<String, String> zipValidateMap = new HashMap<String, String>();
	
	
	static{
		
		zipValidateMap.put(Constants.SMS_COUNTRY_US,"/^[0-9]{5}(?:-[0-9]{4})?$/");
		  zipValidateMap.put(Constants.SMS_COUNTRY_INDIA, "/^\\d{6}$/");
		  zipValidateMap.put(Constants.SMS_COUNTRY_PAKISTAN, "/^\\d{5}$/");
		 // zipValidateMap.put("UAE", "");
		  zipValidateMap.put(Constants.SMS_COUNTRY_CANADA, "/^(?!.*[DFIOQU])[A-VXY][0-9][A-Z] ?[0-9][A-Z][0-9]$/");
		  zipValidateMap.put(Constants.SMS_COUNTRY_KUWAIT, "/^\\d{5}$/");
		 
	}
	
	
	@RequestMapping(value = "/login.html", method = RequestMethod.GET)
	public ModelAndView loginPage(ModelAndView mav, HttpServletRequest request) {
		ModelAndView model = null;
		if(request.getSession(true).getAttribute("loyaltySettings") !=null)
		{
		
			model = new ModelAndView("redirect:/membership");
			return model;
		}
		String name1 = "http://".concat(request.getServerName());
		String orgname = Constants.STRING_NILL;
		String mobileRange = Constants.STRING_NILL;
		String mobileRangeJava = Constants.STRING_NILL;
		LoyaltySettings loyalitySettings = userService.getSettingDetails(name1);
		List<UserOrganization> userList = userService
				.getOrgDetails(loyalitySettings.getUserOrgId());
		if (userList.size() != 0) {
			UserOrganization user = userList.get(0);
			orgname = user.getOrganizationName();
			if(user.getMaxNumberOfDigits() != user.getMinNumberOfDigits()){
			mobileRange = "\""+user.getMinNumberOfDigits()+" to "+user.getMaxNumberOfDigits()+"\"";
			mobileRangeJava = user.getMinNumberOfDigits()+" to "+user.getMaxNumberOfDigits();
			}else{
			mobileRange = "\""+user.getMaxNumberOfDigits()+"\"";
			mobileRangeJava = ""+user.getMaxNumberOfDigits();
			}
		} else {
			orgname = "default";
			mobileRange = "10";
			mobileRangeJava = "10";
		}
		if (loyalitySettings.getLoyaltyType().equals("Mobile Number"))
			model = new ModelAndView("common/login-mobile");
		else
			model = new ModelAndView("common/login");
		model.addObject("loyalitySettings", loyalitySettings);
		model.addObject("colorCode", loyalitySettings.getColorCode());
		
		String path=loyalitySettings.getPath();
		
		String name="";
		String folderpath = null;
		String folder = null;
		if(path!=null)
		{
		String split[]=path.split("/");
		name=split[8];
		folderpath=split[7];
		folder=split[6];
		}
		model.addObject("orgname",orgname);
		logger.info(" mobile range :: "+mobileRange);
		model.addObject("mobileRange",mobileRange);
		model.addObject("mobileRangeJava",mobileRangeJava);
		model.addObject("image",request.getContextPath()+"/"+folder+"/"+folderpath+"/"+name);
		model.addObject("userId", loyalitySettings.getUserId());
		return model;
	}

	@SuppressWarnings("deprecation")
	@RequestMapping(value = "/login.ajax", method = RequestMethod.POST)
	@ResponseBody
	public ModelMap login(HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(value = "id") String username,
			@RequestParam(value = "password") String password) {
		ModelMap map = new ModelMap();
		if(request.getSession(true).getAttribute("loyaltySettings") !=null)
		{
		
			map.put("success", true);
			map.put("returnUrl", getReturnUrl(request, response));
			return map;
		}
		Pattern pattern = Pattern.compile(".*[^0-9].*");
		
		if((pattern.matcher(username.trim()).matches()))
		{
			logger.info("if condition==========>"+username);
			map.put("success", false);
			map.put("message", "Please enter valid card or mobile  number.");
			return map;
		}
		String name1 = "http://".concat(request.getServerName());
		LoyaltySettings loyality = userService.getSettingDetails(name1);
		if(loyality!=null){
			
			
			List<UserOrganization> userList = userService.getOrgDetails(loyality.getUserOrgId());
			String mobileRange = Constants.STRING_NILL;
			String mobileRangeJava = Constants.STRING_NILL;
			//boolean isMobile = false;
			if (userList.size() != 0) {
				UserOrganization user = userList.get(0);
				if(user.getMaxNumberOfDigits() != user.getMinNumberOfDigits()){
					mobileRange = "\""+user.getMinNumberOfDigits()+" to "+user.getMaxNumberOfDigits()+"\"";
					mobileRangeJava = user.getMinNumberOfDigits()+" to "+user.getMaxNumberOfDigits();
					//isMobile = !(username.trim().length() >= user.getMinNumberOfDigits() && username.trim().length() <= user.getMinNumberOfDigits());
				}else{
					mobileRange = "\""+user.getMaxNumberOfDigits()+"\"";
					mobileRangeJava = ""+user.getMaxNumberOfDigits();
					//isMobile = (username.trim().length()!=user.getMaxNumberOfDigits());
				}
			} else {
				mobileRange = "10";
				mobileRangeJava = "10";
				//isMobile = username.trim().length()!=10;
			}
			
			
			
			/*if((loyality.getLoyaltyType().equals("Mobile Number"))&& isMobile)
			{
				map.put("success", false);
				map.put("message", "Please enter "+mobileRangeJava+" digits mobile number.");
				return map;
			}*/
		LoginDetails loginDet=new LoginDetails();
		loginDet.setUserId(username);
		loginDet.setOrgId(loyality.getUserOrgId());
		loginDet.setLoyaltyId(loyality.getUserOrgId());
		loginDet.setLoginDate(Calendar.getInstance() );
		userService.saveOrUpdate(loginDet);
		UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
				new Object[]{username.trim(),loyality.getOrgOwner().getCountryCarrier(), mobileRangeJava}, password.trim());
		try {
			MyAuthenticationToken auth = (MyAuthenticationToken) authenticationManager
					.authenticate(token);
			List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>(auth.getAuthorities());
			authorities.add(new GrantedAuthorityImpl("ROLE_USER"));
			Authentication newAuth = new UsernamePasswordAuthenticationToken(auth.getPrincipal(),auth.getCredentials(),authorities);
			SecurityContextHolder.getContext().setAuthentication(newAuth);
			HttpSession session=request.getSession(true);
			session.setAttribute("loyalityConfig",(ContactsLoyalty)newAuth.getPrincipal());
			session.setAttribute("userName",(String)username.trim());
			session.setAttribute("loyaltySettings", loyality);
			session.setAttribute("mobileRange", mobileRange);
			session.setAttribute("mobileRangeJava",mobileRangeJava);
			session.setAttribute("zipRegx", zipValidateMap.get(loyality.getOrgOwner().getCountryType())!=null?zipValidateMap.get(loyality.getOrgOwner().getCountryType()):"/^\\d{5,6}$/");
			String rememberMe = request.getParameter("rememberMeID");
			logger.info(" remeber me ::: "+rememberMe);
			try{
			if(rememberMe != null && ( rememberMe.equals("on") || rememberMe.equals("true"))){
				int age = 60 * 60*24*7;
				Cookie cookie = new Cookie("lu", EncryptDecryptLtyMembshpPwd.encryptCookie(username));
				cookie.setMaxAge(age);
				response.addCookie(cookie);
				cookie =new Cookie("lp", EncryptDecryptLtyMembshpPwd.encryptCookie(password));
				cookie.setMaxAge(age);
				response.addCookie(cookie);
				cookie =new Cookie("lt", EncryptDecryptLtyMembshpPwd.encryptCookie(loyality.getLoyaltyType().equals("Mobile Number")?"mt":"ct"));
				cookie.setMaxAge(age);
				response.addCookie(cookie);
				}
			}catch(Exception e){
				logger.info("Exception while saving cookies data ",e);
			}
	        map.put("success", true);
			map.put("returnUrl", getReturnUrl(request, response));
		}
		catch (BadCredentialsException e) {
			map.put("success", false);
			map.put("message", e.getMessage());
			map.put("info", e.getExtraInformation());
		}
		}
		else
		{
			map.put("success", false);
		}
		return map;
	}

	/**
	 * @param request
	 * @param response
	 * @return
	 */
	private String getReturnUrl(HttpServletRequest request,
			HttpServletResponse response) {
		return "membership";
	}

}
