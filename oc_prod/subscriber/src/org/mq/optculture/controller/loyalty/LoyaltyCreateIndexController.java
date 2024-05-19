package org.mq.optculture.controller.loyalty;

import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.general.PageListEnum;
import org.mq.marketer.campaign.general.Redirect;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.utils.OCConstants;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.util.GenericForwardComposer;

public class LoyaltyCreateIndexController extends GenericForwardComposer {
	private Session session;
	public LoyaltyCreateIndexController() {
		
		session = Sessions.getCurrent();
		
	}
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);

		session.removeAttribute("programId");
		session.removeAttribute("loyaltyProgramTier");
//		session.removeAttribute("editCampaign");
//		session.removeAttribute("editorType");
//		session.removeAttribute("selectedTemplate");
//		session.removeAttribute("isBack");
		
		Utility.ltyBreadCrumbFrom(1, OCConstants.LOYALTY_SERVICE_TYPE_SBTOOC.equalsIgnoreCase(GetUser.getUserObj().getloyaltyServicetype()));
		Redirect.goTo(PageListEnum.LOYALTY_CREATE_PROGRAM);
		
	}//doAfterCompose
	
}
