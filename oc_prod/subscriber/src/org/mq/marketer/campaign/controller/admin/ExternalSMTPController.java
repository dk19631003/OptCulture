package org.mq.marketer.campaign.controller.admin;

import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.UserExternalSMTPSettings;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.controller.service.RestWebService;
import org.mq.marketer.campaign.dao.ExternalSMTPSettingsDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.Redirect;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Textbox;

public class ExternalSMTPController extends GenericForwardComposer {

	    private Grid fieldsGridId;    
	    private Textbox userNameTbId,passwordTbId,confPassTbId,emlIdTbId,firstNameTbId,lastNameTbId;
	    private Textbox addTbId,cityTbId,stateTbId,zipCodeTbId,countryTbId,phoneTbId,websiteTbId;
	    private Textbox companyTbId,packageTbId,whiteLabelTbId,ipGrpTbId,ipTbId;
	    private Radiogroup freeUserRdId;
	    private Datebox billingDateCalId;
	    
	    private UserExternalSMTPSettings userExternalSMTPSettings;
	    private Users user;
	    
	    private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);	 
	    
	    public ExternalSMTPController() {
	    	
		}
	
		@Override
		public void doAfterCompose(Component comp) throws Exception {
			super.doAfterCompose(comp);
			
			ExternalSMTPSettingsDao externalSMTPDao = (ExternalSMTPSettingsDao)SpringUtil.getBean("externalSMTPSettingsDao");
			user = GetUser.getUserObj();
			userExternalSMTPSettings = externalSMTPDao.findByUserId(user.getUserId());
		}
		
		
		public void onClick$saveOrEditBtnId() throws Exception {
			
			if(userExternalSMTPSettings == null) {
				
				if(userNameTbId.getValue().trim().equals("")) {
					
					userNameTbId.setFocus(true);
					MessageUtil.setMessage("Username field cannot be left empty.", "red");
					return;
				} 
				if(passwordTbId.getValue().trim().equals("")) {
					
					passwordTbId.setFocus(true);
					MessageUtil.setMessage("Password field cannot be left empty.", "red");
					return;
				}
				if(confPassTbId.getValue().trim().equals("")) {
					
					confPassTbId.setFocus(true);
					MessageUtil.setMessage("Confirm Password field cannot be left empty.", "red");
					return;
				}
				if(emlIdTbId.getValue().trim().equals("")) {
					
					emlIdTbId.setFocus(true);
					MessageUtil.setMessage("Email ID field cannot be left empty.", "red");
					return;
				}
				if(firstNameTbId.getValue().trim().equals("")) {
					
					firstNameTbId.setFocus(true);
					MessageUtil.setMessage("First Name field cannot be left empty.", "red");
					return;
				}
				if(lastNameTbId.getValue().trim().equals("")) {
					
					lastNameTbId.setFocus(true);
					MessageUtil.setMessage("Last Name field cannot be left empty.", "red");
					return;
				}
				if(addTbId.getValue().trim().equals("")) {
					
					addTbId.setFocus(true);
					MessageUtil.setMessage("Address field cannot be left empty.", "red");
					return;
				}
				if(cityTbId.getValue().trim().equals("")) {
					cityTbId.setFocus(true);
					MessageUtil.setMessage("City field cannot be left empty.", "red");
					return;
				}
				if(stateTbId.getValue().trim().equals("")) {
					stateTbId.setFocus(true);
					MessageUtil.setMessage("State field cannot be left empty.", "red");
					return;
				}
				if(zipCodeTbId.getValue().trim().equals("")) {
					zipCodeTbId.setFocus(true);
					MessageUtil.setMessage("ZIP field cannot be left empty.", "red");
					return;
				}
				if(countryTbId.getValue().trim().equals("")) {
					countryTbId.setFocus(true);
					MessageUtil.setMessage("Country field cannot be left empty.", "red");
					return;
				}
				if(phoneTbId.getValue().trim().equals("")) {
					phoneTbId.setFocus(true);
					MessageUtil.setMessage("Phone number field cannot be left empty.", "red");
					return;
				}
				if(websiteTbId.getValue().trim().equals("")) {
					websiteTbId.setFocus(true);
					MessageUtil.setMessage("Website field cannot be left empty.", "red");
					return;
				}
				if(whiteLabelTbId.getValue().trim().equals("")) {
					whiteLabelTbId.setFocus(true);
					MessageUtil.setMessage("White Label field cannot be left empty.", "red");
					return;
				}
				if(companyTbId.getValue().trim().equals("")) {
					MessageUtil.setMessage("Company field cannot be left empty.", "red");
					companyTbId.setFocus(true);
					return;
				}

				
				CustomerManagement customerMgnt = new CustomerManagement();

				String response = customerMgnt.addNewUser(emlIdTbId.getValue(),websiteTbId.getValue(),userNameTbId.getValue(),passwordTbId.getValue(),
						 confPassTbId.getValue(),firstNameTbId.getValue(),lastNameTbId.getValue(),addTbId.getValue(),cityTbId.getValue()
						,stateTbId.getValue(),zipCodeTbId.getValue(),countryTbId.getValue(),phoneTbId.getValue()
						,companyTbId.getValue(),packageTbId.getValue(),whiteLabelTbId.getValue()
						,ipGrpTbId.getValue(),ipTbId.getValue(),freeUserRdId.getSelectedItem().getValue().toString(),billingDateCalId.getValue());
				
				if(response == null) {
					MessageUtil.setMessage("Error occured while creating a Sendgrid user.","red","top");
				} else {
					MessageUtil.setMessage("Sendgrid user created successfully.","green","top");
				}
				
			} else  {
				// if edit mode;
				userNameTbId.setValue(userExternalSMTPSettings.getUserName());
				passwordTbId.setValue(userExternalSMTPSettings.getPassword());
			}
			
		}
		
		
		public void onClick$cancelBtnId() throws Exception {
			Redirect.goToPreviousPage();
		}
		
		public void onClick$backBtnId() throws Exception {
			Redirect.goToPreviousPage();
		}
}


class CustomerManagement {
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);	 
	
	public CustomerManagement() {
		// TODO Auto-generated constructor stub
	}
	
	public String addNewUser(String userEmail,String website,String username,String password,
			String confPassStr, String firstNameStr, String lastNameStr, String addStr, String 
			cityStr, String stateStr, String zipCodeStr, String countryStr,String phoneStr, 
			String companyStr, String packageStr, String whiteLabelStr,
			 String ipGrpStr, String ipStr, String freeUserRdId, Date billingDateCalId) throws Exception {
		
			StringBuilder queryStringSB =  new StringBuilder();
		
			queryStringSB.append("?api_user="+ "captiway");
			queryStringSB.append("&api_key="+ "captiway123");
		
			queryStringSB.append("&username="+ username);
			queryStringSB.append("&website="+ website);
			queryStringSB.append("&password="+ password);
			queryStringSB.append("&confirm_password="+ confPassStr);
			queryStringSB.append("&city="+ cityStr);
			queryStringSB.append("&state="+ stateStr);
			queryStringSB.append("&zip="+ zipCodeStr);
			queryStringSB.append("&country="+ companyStr);
			queryStringSB.append("&email="+ userEmail);
			queryStringSB.append("&phone="+ phoneStr);
			queryStringSB.append("&first_name="+ firstNameStr);
			queryStringSB.append("&last_name="+ lastNameStr);
			queryStringSB.append("&address="+ addStr);
			queryStringSB.append("&free_user="+ freeUserRdId);
			
		String responseJSON = RestWebService.requestService(Constants.SG_ADD_NEW_USER, queryStringSB.toString());
		logger.info("respone JSON is "+ responseJSON);
		
		return responseJSON;
	}
	
	public int delUser() throws Exception {
		return 0;
	}
	
	public int modifyUser() throws Exception {
		return 0;
	}
	
	public int enableAppAccess() throws Exception {
		
		return 0;
	} 
	
	public List retrieveAllCustomers() throws Exception {
		
		return null;
	}
	
	
	
}