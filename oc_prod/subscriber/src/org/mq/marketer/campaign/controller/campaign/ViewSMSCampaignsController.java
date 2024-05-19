package org.mq.marketer.campaign.controller.campaign;

import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PageUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Include;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.SimpleListModel;

public class ViewSMSCampaignsController extends GenericForwardComposer{
	
	private Listbox smsCampStatusLb;
	private Include viewCampIncId;
	private String[] data = {"All","Active","Running","Sent","Draft"};
	
	public ViewSMSCampaignsController() {
		String style = "font-weight:bold;font-size:15px;color:#313031;font-family:Arial,Helvetica,sans-serif;align:left";
		PageUtil.setHeader("My SMS Campaigns","",style,true);
		
	}
	 @Override
	public void doAfterCompose(Component comp) throws Exception {
		// TODO Auto-generated method stub
		super.doAfterCompose(comp);
		
		String[] data = {"All","Active","Running","Sent","Draft"};
		ListModel strset = new SimpleListModel(data);        
		
		smsCampStatusLb.setModel(strset);
		
		smsCampStatusLb.setSelectedIndex(0);
	}
	
	 
	 public void onClick$submintBtnId() {
			
			reloadCampaigns();
		}
	 
	/* public void onSelect$smsCampStatusLb() {
		 
		 reloadCampaigns();
		 
		 
	 }//onSelect$smsCampStatusLb()
	 */
	 
	 
	 public void reloadCampaigns(){
 		MessageUtil.clearMessage();
 		viewCampIncId.setSrc("/zul/Empty.zul");
 		viewCampIncId.setSrc("/zul/campaign/SMSCampaignList.zul");
 	}
	 
	
	 public void onClick$resetAnchId() {
			
		 smsCampStatusLb.setSelectedIndex(0);
		viewCampIncId.setSrc("/zul/Empty.zul");
		viewCampIncId.setSrc("/zul/campaign/SMSCampaignList.zul");
		
	}
		
	 
	 
	 
}
