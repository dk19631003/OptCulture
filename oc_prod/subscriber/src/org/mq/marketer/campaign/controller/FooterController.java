package org.mq.marketer.campaign.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.PageListEnum;
import org.mq.marketer.campaign.general.Redirect;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Label;

public class FooterController extends GenericForwardComposer {

	private Label versionLblId;
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);

		/*String versionStr = PropertyUtil.getPropertyValueFromDB("version");
		
		String branding = GetUser.getUserObj().getUserOrganization().getBranding();

		if(branding!=null && branding.equalsIgnoreCase("CAP")) {
			versionLblId.setValue("Captiway Version " + versionStr+" ");
		}
		else {
			versionLblId.setValue("OptCulture Version " + versionStr+" ");
		}*/
		
	} //
	
	
	public void onClick$supportAId() {
		session.setAttribute("feedbackHeaderLbl", "Support");
		Redirect.goTo(PageListEnum.EMPTY);
		Redirect.goTo(PageListEnum.GENERAL_FEEDBACK);
	} //
	
}
