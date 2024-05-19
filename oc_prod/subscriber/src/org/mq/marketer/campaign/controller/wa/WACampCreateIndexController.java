package org.mq.marketer.campaign.controller.wa;

import org.mq.marketer.campaign.general.PageListEnum;
import org.mq.marketer.campaign.general.Redirect;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.util.GenericForwardComposer;

public class WACampCreateIndexController extends GenericForwardComposer {
	
	private Session session;
	public WACampCreateIndexController() {
		session = Sessions.getCurrent();
		
	}
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		session.removeAttribute("waCampaign");
		session.removeAttribute("editWACampaign");
		session.removeAttribute("messageContent");
		session.removeAttribute("waDraftStatus");
		
		Redirect.goTo(PageListEnum.WA_CAMP_SETTINGS);
	}//doAfterCompose()
	
}
