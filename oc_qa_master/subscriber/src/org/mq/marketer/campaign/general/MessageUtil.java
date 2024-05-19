package org.mq.marketer.campaign.general;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.zhtml.Messagebox;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;

/**
 This class can be used to set the message for generic label 
 created date 28-04-2009
 author RM Team
*/
public class MessageUtil{
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	
	
	public static boolean setMessage(String msg, String style){
		
		try {
			
			if(style.contains("red")) {
				Messagebox.show(msg, "Error", Messagebox.OK, Messagebox.ERROR);
			}
			else {
				Messagebox.show(msg, "Information", Messagebox.OK, Messagebox.INFORMATION);
			}

		} catch (Exception e) {
			logger.error("Setting Message is failed :" + msg);
			logger.error("Exception:",e);
			return false;
		}

		return true;
	}
	
	public static boolean setMessage(String msg,String style,String pos){
		try{
			logger.debug("Setting the Message : "+msg+" at "+pos);
			return setMessage(msg, style);
			
/*			Label msgLabel;
			Div msgDiv;
			if(pos.equalsIgnoreCase("TOP")){
				msgDiv = (Div)Utility.getComponentById("msgsDivId");
				if(style.contains("red")){
					msgDiv.setClass("wayerror");
				}else{
					msgDiv.setClass("waymsg");
				}
				msgLabel = (Label)Utility.getComponentById("topMsgLbId");
				msgLabel.setValue(msg);
				msgLabel.setStyle("font-weight:bold");
				msgDiv.setVisible(true);
			}Clients.evalJavaScript("parent.window.scrollTo(0,0)");
			
			Timer timer = (Timer)Utility.getComponentById("clearMsgDivTimerId");
			timer.start();
			logger.debug("Message setting is successfull");
			return true;
*/		
		} catch(Exception e){
			logger.error("Setting Message is failed :" + msg);
			logger.error("Exception:"+e);
			return false;
		}
	}
	public static boolean clearMessage(){
		try{
			logger.debug("Clearing Messages");
			
		/*((Div)Utility.getComponentById("msgsDivId")).setVisible(false);
			Label msgLabel = new Label();
			msgLabel = (Label)Utility.getComponentById("topMsgLbId");
			if(msgLabel!=null)
				msgLabel.setValue("");
			logger.debug("Message cleared successfully");
*/			
			return true;
		}catch(Exception e){
			logger.error("Message clearing is failed");
			logger.error("Exception:"+e);
			return false;
		}
	}
	
	public static String getOnlyMessage(String ErrorMsg){
		
		if(ErrorMsg.contains(":")){
			
			return ErrorMsg.substring(ErrorMsg.indexOf(":")+1);
		}
		return ErrorMsg;
		
	}
}
