package org.mq.marketer.campaign.controller.program;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.general.Constants;
import org.zkforge.canvas.Canvas;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Window;

/**
 * @author Krishna
 *
 */
public class ActivityComponentController extends GenericForwardComposer {
	
	private static final long serialVersionUID = 1L;
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	private ProgramDesignerController programController = null;
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {

		super.doAfterCompose(comp);
		if ( arg != null && arg.containsKey( "programController" )) {
			programController = (ProgramDesignerController)arg.get("programController") ;
		}
		
		Clients.evalJavaScript("disableCompTextSelection();");
	} // doAfterCompose
	
	
	public void onClick$menuitemPropertiesId(Event event) {

		if(programController != null) {
			programController.handleMenuitemPropertiesClick(event);
		}
		else {
			logger.info("Unable to get the Parent Controller reference...");
			return;
		}
	} // onClick$menuitemPropertiesId
	
	
	
	public void onClick$menuitemRemoveId(Event event) {
		
		if(programController != null) {
			programController.handleMenuitemRemoveClick(event);
		}
		else {
			logger.info("Unable to get the Parent Controller reference...");
			return;
		}
	} // menuEventHandler
	
	private Window activityComponentWinId;
	
	public void onClick$activityComponentWinId() {
		
		if ( arg != null && !arg.containsKey( "deSelect" )) {
			
			Canvas canvas = (Canvas)activityComponentWinId.getParent();
			List children = canvas.getChildren();
			for (int i=0; i<children.size(); i++) {
				Window obj = (Window)children.get(i);
				obj.setBorder("none");
				obj.setStyle("position:absolute;padding:1px;");
			}
			activityComponentWinId.setBorder("normal");
			activityComponentWinId.setStyle("position:absolute;padding:0px;");
			
		}//if
		
		
		
	}//onClick$activityComponentWinId()
	
}


