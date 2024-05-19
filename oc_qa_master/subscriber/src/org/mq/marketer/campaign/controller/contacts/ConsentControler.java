package org.mq.marketer.campaign.controller.contacts;

import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.MailingList;
import org.mq.marketer.campaign.controller.ActivityEnum;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.dao.MailingListDao;
import org.mq.marketer.campaign.dao.UserActivitiesDao;
import org.mq.marketer.campaign.dao.UserActivitiesDaoForDML;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PageListEnum;
import org.mq.marketer.campaign.general.PageUtil;
import org.mq.marketer.campaign.general.Redirect;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Checkbox;

public class ConsentControler extends GenericForwardComposer {

	private Checkbox consentCBId;
//	Include inc = null;
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	UserActivitiesDao userActivitiesDao = null;
	UserActivitiesDaoForDML userActivitiesDaoForDML = null;
	MailingListDao mailingListDao = null;
	
	
	public ConsentControler() {
		
		String style = "font-weight:bold;font-size:15px;color:#313031;font-family:Arial,Helvetica,sans-serif;align:left";
     	PageUtil.setHeader("Add / Import Contacts","",style,true);
	}
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		// TODO Auto-generated method stub
		super.doAfterCompose(comp);
//		inc = (Include)Utility.getComponentById("xcontents"); 
        mailingListDao = (MailingListDao)SpringUtil.getBean("mailingListDao");
        userActivitiesDao = (UserActivitiesDao)SpringUtil.getBean("userActivitiesDao");
        userActivitiesDaoForDML = (UserActivitiesDaoForDML)SpringUtil.getBean("userActivitiesDaoForDML");
  	  		/*if(userActivitiesDao != null) {
        		userActivitiesDao.addToActivityList(ActivityEnum.VISIT_CONTACT_UPLOAD_CONSENT, GetUser.getUserObj(), null);
  	  		}*/
  	  	if(userActivitiesDaoForDML != null) {
    		userActivitiesDaoForDML.addToActivityList(ActivityEnum.VISIT_CONTACT_UPLOAD_CONSENT, GetUser.getLoginUserObj(), null);
	  		}
	}
	
	
	public void onClick$proceedBtnId() {
		
		try {
			if(consentCBId.isChecked()){
				MessageUtil.clearMessage();
			 	Set upFile = (Set)sessionScope.get("uploadFile_Ml");
			 	Set addSingle = (Set)sessionScope.get("AddSingle_Ml");
				if(upFile != null){
					setConsent(upFile);
					Redirect.goTo(PageListEnum.CONTACT_UPLOAD_CSV_SETTINGS);
//					inc.setSrc("/zul/contact/````.zul");
					//inc.setSrc("/zul/contact/FieldSelection.zul");
				}
				if(addSingle != null){
					setConsent(addSingle);
//					inc.setSrc("/zul/contact/AddSingle.zul");//contact/contacts
					Redirect.goTo(PageListEnum.CONTACT_ADDSINGLE_NEW);
//					inc.setSrc("/zul/contact/AddSingleNew.zul");
				} 
			 }else{
				 MessageUtil.setMessage("You must confirm that you follow these guidelines to continue.","color:red","TOP");
			 }
		} catch (Exception e) {
			logger.error("Exception >>error occured while redirecting the page",e);
		}
	} // onClick$proceedBtnId

	void setConsent(Set s) throws Exception{
		for(Object obj:s){
			MailingList m = (MailingList)obj;
			m.setConsent(true);
		}
	} // setConsent
	
	
	public void onClick$cancelBtnId() {
		 
		try {
			MessageUtil.clearMessage();
			sessionScope.remove("isNewML");
			sessionScope.remove("AddSingle_Ml");
			sessionScope.remove("uploadFile_Ml");
			sessionScope.remove("newListName");
			Redirect.goTo(PageListEnum.CONTACT_UPLOAD);
			//inc.setSrc("/zul/contact/upload.zul");
		} catch (Exception e) {
			logger.error("Exception >>error occured while removing attributes from the sessionscope and redirecting the page",e);
		}
	}
}
