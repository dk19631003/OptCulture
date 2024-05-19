package org.mq.marketer.campaign.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.clapper.util.io.FileUtil;
import org.mq.marketer.campaign.beans.SupportTicket;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.PageListEnum;
import org.mq.marketer.campaign.general.PageUtil;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Redirect;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zul.Div;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Textbox;

@SuppressWarnings("serial")
public class SupportViewControlle extends GenericForwardComposer implements EventListener {
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER); 
	
	private final String supportFileDir = PropertyUtil.getPropertyValue("usersSupportDirectory").trim();
	

	
	 private Textbox clientNametbId,contactNametbId,contactPhonetbId,contactEmailtbId,descriptiontbId,fileDownloadtbId;
	 private Listbox websiteLbId,posIntegrationLbId;
	 private Radio websiteRgId,posIntegrationRgId,bugRgId,featureReqRgId,serviceReqRgId,technicalReqRgId;
	 private Div websiteDivId,posIntegrationDivId;
	
	
	 Desktop desktopScope = null;
	 
	public  SupportViewControlle(){
		 String style = "font-weight:bold;font-size:15px;color:#313031;font-family:Arial,Helvetica,sans-serif;align:left";
	     PageUtil.setHeader("Support","",style,true);
	     desktopScope = Executions.getCurrent().getDesktop();
	 }
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		// TODO Auto-generated method stub
		super.doAfterCompose(comp);
		
		SupportTicket supportTicket = (SupportTicket)Sessions.getCurrent().getAttribute("TICKET_VIEW");
		
		
		defaultSettings(supportTicket);
		
	}
	
	public void defaultSettings(SupportTicket supportTicket){
		if(supportTicket == null) return;
		
		// type 
		
		if(supportTicket.getType() == 1){
			bugRgId.setChecked(true);
		}else if(supportTicket.getType() == 2){
			featureReqRgId.setChecked(true);
		} else if(supportTicket.getType() == 3){
			serviceReqRgId.setChecked(true);
		} else if(supportTicket.getType() == 4){
			technicalReqRgId.setChecked(true);
		}
		
		// client Name
		
		clientNametbId.setValue(supportTicket.getClientName() !=null ?supportTicket.getClientName() :null );
		
		// contactName
		
		contactNametbId.setValue(supportTicket.getContactName() != null ? supportTicket.getContactName() : null);
		
		// contact email
		contactEmailtbId.setValue(supportTicket.getContactEmail() != null ? supportTicket.getContactEmail() : null);
		
		//contact phone
		
		contactPhonetbId.setValue(supportTicket.getContactPhone() != null ? supportTicket.getContactPhone() : null);
		
		// product area type
		
		if(supportTicket.getProductAreaType() == 1){
			websiteRgId.setChecked(true);
			
			websiteDivId.setVisible(true);
			websiteLbId.setVisible(true);
			
			posIntegrationDivId.setVisible(false);
			posIntegrationLbId.setVisible(false);
			
			
			
			
			
		}else if(supportTicket.getProductAreaType() == 2){
			posIntegrationRgId.setChecked(true);
			posIntegrationDivId.setVisible(true);
			posIntegrationLbId.setVisible(true);
			
			
			websiteDivId.setVisible(false);
			websiteLbId.setVisible(false);
		}

		// product are type
		if(websiteRgId.isChecked()){
			
			
			for (Listitem item : websiteLbId.getItems()) {
				
				if(supportTicket.getProductArea() != null) {
					if(item.getLabel() != null && 
							( item.getLabel().equals(supportTicket.getProductArea())) )  {
						
						item.setSelected(true);
						break;
					}
					
					
					
				}//if
				websiteLbId.setDisabled(true);
				
			}// for
			
		}// if 
	 if(posIntegrationRgId.isChecked()){
		
		for (Listitem item : posIntegrationLbId.getItems()) {
			
			if(supportTicket.getProductArea() != null) {
				if(item.getLabel() != null && 
						( item.getLabel().equals(supportTicket.getProductArea()) ))  {
					item.setSelected(true);
					break;
				}
				
				
				
			}//if
			posIntegrationLbId.setDisabled(true);
		}// for
		
	}// if
		
		// description
		
		
		descriptiontbId.setValue(supportTicket.getDescription() != null ? supportTicket.getDescription() : null);
		
		//  file name 
		
		fileDownloadtbId.setValue(supportTicket.getFileName() != null ? supportTicket.getFileName() : null);
		
		
		//onClick$downloadBtnId(supportTicket);
	}
	
	@Override
	public void onEvent(Event arg0) throws Exception {
		// TODO Auto-generated method stub
		super.onEvent(arg0);
	}
	
	public void onClick$downloadBtnId(){
		
		try {
			SupportTicket supportTicket = (SupportTicket)Sessions.getCurrent().getAttribute("TICKET_VIEW");
			
			String fileName = supportTicket.getFileName().trim();
				String filePath = supportFileDir+File.separator+fileName;
				logger.info("file path is"+filePath);
				
				
				File supportFile = new File(filePath.trim());
				//FileInputStream fs = new FileInputStream(supportFile); 
				
				String ext = FileUtil.getFileNameExtension(filePath.trim());
				
				String mimeType=Constants.STRING_NILL;
				
				if(ext.equalsIgnoreCase("png") || ext.equalsIgnoreCase("gif") ){
					
					mimeType ="image/"+ext;
				}else if(ext.equalsIgnoreCase("jpg") || ext.equalsIgnoreCase("jpeg") ) {
					
					mimeType ="image/jpeg";
				}
					else if(ext.equalsIgnoreCase("doc")){
						
						mimeType ="application/msword";
				}
				
				
				//InputStream is = desktopScope.getWebApp().ge.getResourceAsStream(filePath);
				logger.info(" supportFile is ::"+supportFile.getAbsolutePath());
				Filedownload.save(supportFile, mimeType);
				 
				
		} catch (Exception e) {
			// TODO Auto-generated catch block
			
			logger.error("Exception" , e);
			//logger.error("Exception :::",e);
		}
		
	        
	    }//onClick$downloadBtnId
		
	public void onClick$backBtnId(){
		
		Redirect.goTo(PageListEnum.ADMIN_VIEW_TICKETS);
	}

}
