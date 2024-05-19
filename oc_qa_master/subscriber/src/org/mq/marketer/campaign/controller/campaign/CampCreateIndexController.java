package org.mq.marketer.campaign.controller.campaign;

import org.mq.marketer.campaign.general.PageListEnum;
import org.mq.marketer.campaign.general.Redirect;
import org.mq.marketer.campaign.general.Utility;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.util.GenericForwardComposer;

public class CampCreateIndexController extends GenericForwardComposer {

	private Session session;
	public CampCreateIndexController() {
		
		session = Sessions.getCurrent();
		
	}
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		// TODO Auto-generated method stub
		super.doAfterCompose(comp);
		session.removeAttribute("campaign");
		session.removeAttribute("editCampaign");
		session.removeAttribute("editorType");
		//session.removeAttribute("selectedTemplate");
		session.removeAttribute("isBack");
		
		Utility.breadCrumbFrom(1);
		Redirect.goTo(PageListEnum.CAMPAIGN_SETTINGS);
		
	}//doAfterCompose
	
	
	
}
