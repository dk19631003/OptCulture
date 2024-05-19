package org.mq.loyality.common.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.loyality.common.dao.IUserDao;
import org.mq.loyality.common.hbmbean.ContactsLoyalty;
import org.mq.loyality.common.hbmbean.LoginDetails;
import org.mq.loyality.common.hbmbean.LoyaltySettings;
import org.mq.loyality.common.hbmbean.UserOrganization;
import org.mq.loyality.utils.Configuration;
import org.mq.loyality.utils.Constants;
import org.mq.loyality.utils.EncryptDecryptLtyMembshpPwd;
import org.mq.loyality.utils.MyAuthenticationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements AuthenticationProvider {

	@Autowired
	IUserDao userDao;
	@Autowired
	EncryptDecryptLtyMembshpPwd encryptPwd;
	@Autowired
	private Environment env;
	@Autowired
	private UserService userService;
	private static final Logger logger = LogManager.getLogger(Constants.LOYALTY_LOGGER);

	private @Autowired
	HttpServletRequest request;

	@SuppressWarnings("deprecation")
	@Override
	@Transactional
	public Authentication authenticate(Authentication authentication)
			throws AuthenticationException{
		
		
		ContactsLoyalty contact = null;
		// List<GrantedAuthority> list = new ArrayList<GrantedAuthority>();
		Object[] obj = (Object[]) authentication.getPrincipal();
		String login = (String) obj[0];
		Short countryCarrier = (Short) obj[1];
		String mobileRangeJava = (String) obj[2];
		String password = (String) authentication.getCredentials();
		// String name = request.getServerName();
		String name1 = "http://".concat(request.getServerName());
		// String name1 = "http://".concat(name);
		List<LoyaltySettings> loyalitySettingsList = userDao
				.getSettingDetails(name1);
		
	/*Pattern pattern = Pattern.compile(".*[^0-9].*");
	logger.ino("in login=====sdsdsdd=======>"+login);
		if(!(pattern.matcher(login).matches()))
		{
			logger.info("in login=====sdsdsdd=======>"+login);
			throw new BadCredentialsException("Please enter valid mobile number/card number","101");
		}*/
		
		List<LoginDetails> details= userDao.checkLoginDetails(login.trim(), loyalitySettingsList.get(0).getUserOrgId());
		
		if (loyalitySettingsList.size() > 0) {
			LoyaltySettings ls = loyalitySettingsList.get(0);
			//String encPwd = null;
			try {
				//encPwd = EncryptDecryptLtyMembshpPwd.encrypt(password.trim());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.info(" Exception :: ",e);
			}
				
	List<ContactsLoyalty> userList1 =userDao.getUser(login, mobileRangeJava, ls.getUserOrgId(), countryCarrier);
			
			if(userList1 != null )
			{
				List<UserOrganization> userList = userService
						.getOrgDetails(ls.getUserOrgId());
				UserOrganization userOrg = new UserOrganization();
				if (userList.size() != 0) {
					userOrg = userList.get(0);
				}
				if (login.length() >= userOrg.getMinNumberOfDigits() && login.length() <= userOrg.getMinNumberOfDigits()){
					if(userList1.size() != 0)
					{
						contact=userList1.get(0);
						int count=0;
				/*if (login.length() == 10 &&  ls.getLoyaltyType().equalsIgnoreCase("Card Number") && userList1.size() == 0) 
				 {
					 throw new BadCredentialsException("Unable to login.Please enter card #","101");
				 }
				
				if (login.length() == 10  && ls.getLoyaltyType().equalsIgnoreCase("Mobile Number") && userList1.size() == 0) 
					{
					 //add condition
						throw new BadCredentialsException("Please verify the mobile #  you have entered","102");
					}
				 
				 if (login.length() != 10  && details != null && details.size() == 0&& userList1.size() == 0) 
		    	  {
						 throw new BadCredentialsException("Please verify the card #  you have entered","103");
		    	  }
				
				 if (login.length() !=10  && details != null && details.size() != 0 && userList1.size() == 0 ) 
			    	  {
						throw new BadCredentialsException("Please verify the card #  you have entered","104"); 
			    	  }*/
				
				if (login.length() >= userOrg.getMinNumberOfDigits() && login.length() <= userOrg.getMinNumberOfDigits() && (userList1.size() > 1)) {
			          for (ContactsLoyalty c : userList1) {
			           if(c.getMobilePhone().equals(login) && BCrypt.checkpw(password, c.getMembershipPwd()))
			            		{
			             count++;
			             contact =c;
			            } 
			          }
			      	if(count>1)
					{
			      		throw new BadCredentialsException("Please verify the mobile number and password you have entered.","107"); 
					}
			        }
				 
			}else{
				/*userList1 =userDao.getUserCard(login, ls.getUserOrgId());*/
				if(userList1.size() !=0){
					contact = userList1.get(0);
				}else{
					if(details.size() ==1){
						throw new BadCredentialsException("Please verify the number you have entered.","108"); 
					}else if(ls.getLoyaltyType().equalsIgnoreCase("Card Number")){
						throw new BadCredentialsException("Unable to log-in! Please try logging-in with your card # or click on 'Can't Log-in?' link for more help.","105"); 
					}else if(ls.getLoyaltyType().equalsIgnoreCase("Mobile Number")){
						throw new BadCredentialsException("Please verify the number you have entered.","110");
					}
				}
				
			}
					
				
			}else{
				if(userList1.size()!=0){
					contact = userList1.get(0);
				}else{
					if(!ls.getLoyaltyType().equalsIgnoreCase("Card Number")){
						throw new BadCredentialsException("Please verify the number you have entered.","111");
					}else if(details.size() ==1){
						throw new BadCredentialsException("Please verify the card# you have entered.","112"); 
					}else if(details.size() >1){
						String dig = userOrg.getMinNumberOfDigits()==userOrg.getMaxNumberOfDigits() ? userOrg.getMinNumberOfDigits()+"":userOrg.getMinNumberOfDigits()+"-"+userOrg.getMaxNumberOfDigits();
						throw new BadCredentialsException("Unable to log-in! Please try logging-in with your registered "+dig+" digits mobile # without the country carrier or click on 'Can't Log-in?' link for more help.","105");
					}
				}
			}
				try {
					if(!BCrypt.checkpw(password,contact.getMembershipPwd()))
					 {
						 throw new BadCredentialsException("Wrong credentials! Please try again.","113");
					 }
				} catch (Exception e) {
					// TODO Auto-generated catch block
					throw new BadCredentialsException(
							"Wrong credentials! Please try again.", "113");
				}
				 if (!(contact.getMembershipStatus().equalsIgnoreCase("Active"))) {
					 if(contact.getMembershipStatus().equalsIgnoreCase("Suspended")){
						throw new BadCredentialsException(
								"Your membership has suspended. Please contact store.","106");
					}else if(contact.getMembershipStatus().equalsIgnoreCase("Closed")){
						throw new BadCredentialsException(
								"This card is closed. Please log-in with your new card details.","106");
					}
					else{
						throw new BadCredentialsException(
								"Your membership has expired. Please contact store.","106");
					}
				 }
					contact.setPath(name1);	
				}
			
			}
		else
		{
			throw new BadCredentialsException(
					"Invalid card number");
		}
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		authorities.add(new GrantedAuthorityImpl("ROLE_USER"));
		//Authentication newAuth = new UsernamePasswordAuthenticationToken(contact.getPrincipal(),auth.getCredentials(),authorities);
	return new MyAuthenticationToken(contact, authorities);
	}
	
	

	@Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }


	
}
