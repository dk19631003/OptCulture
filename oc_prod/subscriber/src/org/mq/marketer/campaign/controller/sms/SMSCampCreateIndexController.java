package org.mq.marketer.campaign.controller.sms;

import org.mq.marketer.campaign.general.PageListEnum;
import org.mq.marketer.campaign.general.Redirect;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.util.GenericForwardComposer;

public class SMSCampCreateIndexController extends GenericForwardComposer {
	
	private Session session;
	public SMSCampCreateIndexController() {
		session = Sessions.getCurrent();
		
	}
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		session.removeAttribute("smsCampaign");
		session.removeAttribute("editSmsCampaign");
		session.removeAttribute("messageContent");
		session.removeAttribute("smsDraftStatus");
		
		Redirect.goTo(PageListEnum.SMS_CAMP_SETTINGS);
	}//doAfterCompose()
	
}
