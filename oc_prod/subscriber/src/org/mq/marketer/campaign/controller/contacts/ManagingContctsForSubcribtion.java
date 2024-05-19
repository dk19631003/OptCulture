package org.mq.marketer.campaign.controller.contacts;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.PageUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Include;

public class ManagingContctsForSubcribtion extends GenericForwardComposer {

	private Include manageContactsIncId,supressContctIncId,supressPhnNumberIncId;
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	public ManagingContctsForSubcribtion(){}
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		// TODO Auto-generated method stub
		super.doAfterCompose(comp);
		
		 String style = "font-weight:bold;font-size:15px;color:#313031;" +
						"font-family:Arial,Helvetica,sans-serif;align:left";
		 PageUtil.setHeader("Suppressed Contacts","",style,true);
		
		//org.mq.marketer.campaign.general.PageUtil.setHeader("Manage Contacts","","",true);
	}
	
	
	public void onClick$segmentTbId(){
		try {
			manageContactsIncId.setSrc("/zul/contact/ListSegmentation.zul");
		} catch (Exception e) {
			logger.error("Exception :: errorr while redirecting the page",e);
		}
		
	}
	
	public void onClick$suppContTbId() {
		try {
			supressContctIncId.setSrc("/zul/contact/suppressContacts.zul");
		} catch (Exception e) {
			logger.error("Exception :: errorr while redirecting the page >>>",e);
		}
	}
	
	public void onClick$suppPhnTbId() {
		try {
			supressContctIncId.setSrc("/zul/contact/suppressPhoneNumbers.zul");
		} catch (Exception e) {
			logger.error("Exception :: errorr while redirecting the page >>>",e);
		}
	}
	
}
