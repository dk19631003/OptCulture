package org.mq.marketer.campaign.controller.contacts;

import org.mq.marketer.campaign.general.PageListEnum;
import org.mq.marketer.campaign.general.Redirect;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.util.GenericForwardComposer;

public class CreateSegmentsController extends GenericForwardComposer{

	private Session session;
	public CreateSegmentsController() {
		session = Sessions.getCurrent();
		
	}
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		// TODO Auto-generated method stub
		super.doAfterCompose(comp);
		
		session.removeAttribute("editSegment");
		

		Redirect.goTo(PageListEnum.CONTACT_MANAGE_SEGMENTS);
	}
	
	
	
	
}
