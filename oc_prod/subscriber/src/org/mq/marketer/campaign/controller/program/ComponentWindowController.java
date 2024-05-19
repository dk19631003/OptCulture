package org.mq.marketer.campaign.controller.program;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.general.Constants;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Window;


public class ComponentWindowController extends GenericForwardComposer {
	private static final long serialVersionUID = 1L;
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	private Window[] prevWin;
	private Window[] nextWin;
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		// TODO Auto-generated method stub
		super.doAfterCompose(comp);
		logger.info("Component created "+comp);
	}
		
}


