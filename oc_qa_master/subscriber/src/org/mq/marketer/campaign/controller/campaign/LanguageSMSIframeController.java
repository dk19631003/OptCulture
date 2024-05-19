package org.mq.marketer.campaign.controller.campaign;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.general.Constants;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;

public class LanguageSMSIframeController extends GenericForwardComposer {

	
	//private Session session;
	private String msgContent;
	private Textbox SMSMsgLangTbId;
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	private Session session;
	
	public LanguageSMSIframeController() {
		// TODO Auto-generated constructor stub
		this.session = Sessions.getCurrent();
	}
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		// TODO Auto-generated method stub
		try {
			super.doAfterCompose(comp);
				
			logger.debug("---just entered---in Controller");
			msgContent = (String)sessionScope.get("messageContent");
			logger.info("message content in iframe is===>"+msgContent);
			if(msgContent != null) {
				
				SMSMsgLangTbId.setValue(msgContent);
				SMSMsgLangTbId.focus();
				//SMSMsgLangTbId.invalidate();
			}
			getCharCount( SMSMsgLangTbId.getValue());
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
		}
		
	}//doAfterCompose
	
	/**
	 * executes onchanging of the SMS msg textbox
 	 * @param event
	 */
	public void onChanging$SMSMsgLangTbId(InputEvent event){
		
		try {
			logger.info(" event.getValue()"+ event.getValue());
			session.setAttribute("messageContent", event.getValue());
			getCharCount( event.getValue());
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
		}
		
		
	}//onChanging$SMSMsgLangTbId(-)

	private Textbox charCountTbId;
	private Label warnLblId;
	/**
	 * caluculates the actual character count of 
	 * the SMS campaign and sets this value to the SMS msg related  textbox
	 * @param msgContent specifies the actual msg content.
	 */
	public void getCharCount(String msgContent) {
		try {	//logger.info("msgContent lenght is==========>"+msgContent.length());
				int charCount = (msgContent.length())*2;
				//logger.info("the length is====>"+charCount);
				if(charCount>140) {
					warnLblId.setVisible(true);
					int msgcount = charCount/140;
					charCountTbId.setValue(""+charCount+"/"+(msgcount+1));
					/*charCountTbId.setValue(""+(smsCampaign.getMessageContent().
							substring(msgcount*160, charCount)).length()+"/"+(msgcount+1));*/
				}//if
				else {
					warnLblId.setVisible(false);
					charCountTbId.setValue(""+charCount+" / "+1);
				}//else
			
			
		} catch (Exception e) {
				//logger.debug("Exception while getting the character count",e);
			logger.error("Exception ::", e);
		}//catch
		
	}//getCharCount
	
	
	/**
	 * updates the current cursor position
	 */
	
	public void onChange$caretPosTBId(){
		logger.debug("---just entered----");
	}
	
	
	
}
