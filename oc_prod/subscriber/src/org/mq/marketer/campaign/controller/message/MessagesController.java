package org.mq.marketer.campaign.controller.message;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.PageUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Include;
import org.zkoss.zul.Label;

public class MessagesController extends GenericForwardComposer  {

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	private Label inLblId,trashLblId,msgsLblId;
	Include msgsIncId;
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		// TODO Auto-generated method stub
		super.doAfterCompose(comp);
		String style = "font-weight:bold;font-size:15px;color:#313031;font-family:Arial,Helvetica,sans-serif;align:left";
		PageUtil.setHeader("Messages","",style,true);
	}
	
	public void onClick$inLblId() {
		try {
			showMessages();
		} catch (Exception e) {
			logger.error("Exception >>:error getting from the showmessages method :: ",e);
		}
	} //onClick$inLblId
	
	private void showMessages() throws Exception{
		inLblId.setStyle("cursor:pointer;font-weight:bold;");
		trashLblId.setStyle("cursor:pointer;font-weight:normal;");
		msgsIncId.setSrc("zul/message/inbox.zul");
		reset();
	}
	
	
	void reset(){
		msgsLblId.setValue("");
	}
	
	
	public void onClick$unreadLblId() {
		try {
			showMessages();
		} catch (Exception e) {
			logger.error("Exception >>:error getting from the showmessages method :: ",e);
		}
	}
	
	
	public void onClick$trashLblId() {
		try {
			showTrashMessages();
		} catch (Exception e) {
			logger.error("Exception >>:error getting from the showTrashMessages method :: ",e);
		}
	}
	
	private void showTrashMessages() throws Exception{
		inLblId.setStyle("cursor:pointer;font-weight:normal;");
		trashLblId.setStyle("cursor:pointer;font-weight:bold;");
		msgsIncId.setSrc("zul/message/trash.zul");
		reset();
	} // showTrashMessages
} // class
