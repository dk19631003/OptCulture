package org.mq.marketer.campaign.controller.contacts;

import org.mq.marketer.campaign.general.PageListEnum;
import org.mq.marketer.campaign.general.Redirect;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.util.GenericForwardComposer;

@SuppressWarnings("rawtypes")
public class NotificationWebIndex extends GenericForwardComposer{
	private static final long serialVersionUID = 1L;
	
	private Session session;
	public NotificationWebIndex() {
		session = Sessions.getCurrent();
		
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		session.removeAttribute("editNotification");
		session.removeAttribute("notificationCampaign");
		session.removeAttribute("notificationDraftStatus");
		
		Redirect.goTo(PageListEnum.NOTIFICATION_WEB);
	}
}
