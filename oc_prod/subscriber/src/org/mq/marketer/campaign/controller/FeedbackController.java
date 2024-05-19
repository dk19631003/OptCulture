package org.mq.marketer.campaign.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.dao.EmailQueueDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PageListEnum;
import org.mq.marketer.campaign.general.PageUtil;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Redirect;
import org.mq.marketer.campaign.general.Utility;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;

@SuppressWarnings("serial")
public class FeedbackController  extends GenericForwardComposer {
private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	private EmailQueueDao emailQueueDao;
	private Textbox subTbId,messageTbId;
	private Session session;
	private Label EmailTeam;
	private String headerLabl;
	
	
	
	public FeedbackController(){
		emailQueueDao = (EmailQueueDao)SpringUtil.getBean("emailQueueDao");
		session = Sessions.getCurrent();
		
		
		
		headerLabl = (String)session.getAttribute("feedbackHeaderLbl");
		
		headerLabl = headerLabl != null ? headerLabl : "Feedback";
		
		
		String style = "font-weight:bold;font-size:15px;color:#313031;font-family:Arial,Helvetica,sans-serif;align:left";
    	PageUtil.setHeader(headerLabl,"",style,true);
		
	}
    
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		// TODO Auto-generated method stub
		
		super.doAfterCompose(comp);
		if(headerLabl=="Feedback") {
			
			EmailTeam.setValue("Email the feedback team");
		}
		else {
			EmailTeam.setValue("Email the support team");
			
		}
		
	}
    
	public void sendFeedback(String subject, String message){
		try{
			if(subject.trim().length()==0){
				MessageUtil.setMessage("Please enter subject.","color:red","TOP");
				return;
			}
			if(message.trim().length()==0){
				MessageUtil.setMessage("Please enter message.","color:red","TOP");
				return;
			}

			Utility.sendInstantMail(null,subject,message,Constants.EQ_TYPE_FEEDBACK,
					PropertyUtil.getPropertyValue("feedbackMailId"), null);
			MessageUtil.setMessage("Email sent successfully.","color:green;","TOP");
		
		}catch(Exception e){
			logger.error("** Exception  :"+ e +" **");
			MessageUtil.setMessage("Mail could not be sent. Please try again later.","color:red","TOP");
		}
    }
    
    public void cancel(){
    	MessageUtil.clearMessage();
    	Redirect.goTo(PageListEnum.RM_HOME);
    }
    
    
	public void onClick$cancelBtnId() {
		try {
			cancel();
		} catch (Exception e) {
			logger.error("** Exception ********* :"+ e +" **");
		}
	}
	
	public void onClick$sendBtnId() {
		try {
			sendFeedback(subTbId.getValue(),messageTbId.getValue());
		} catch (WrongValueException e) {
			// TODO Auto-generated catch block
			logger.error("** Exception >>>>>>>>>> :"+ e +" **");
		}
	}

}
