package org.mq.marketer.campaign.controller.campaign;

import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PageUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Include;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.SimpleListModel;

public class ViewCampaignsController extends GenericForwardComposer {

	private Include viewCampIncId;
	private Listbox campStatusLb;
	
	public ViewCampaignsController() {
		String style = "font-weight:bold;font-size:15px;color:#313031;font-family:Arial,Helvetica,sans-serif;align:left";
		PageUtil.setHeader("My Email Campaigns","",style,true);
		
		
	}//ViewCampaignsController()
	
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		// TODO Auto-generated method stub
		super.doAfterCompose(comp);
		String[] data = {"All","Active","Running","Sent","Draft"};
		ListModel strset = new SimpleListModel(data);        
		
		campStatusLb.setModel(strset);
		
		campStatusLb.setSelectedIndex(0);
		
	}//doAfterCompose()
	
	
	
	/*public void onSelect$campStatusLb(){
		
		reloadCampaigns();
		
	}//onSelect$campStatusLb()
*/	
	
	public void onClick$submintBtnId() {
		
		reloadCampaigns();
	}
	
	public void onClick$resetAnchId() {
		
		campStatusLb.setSelectedIndex(0);
		viewCampIncId.setSrc("/zul/Empty.zul");
		viewCampIncId.setSrc("/zul/campaign/CampaignList.zul");
		
	}
	
	
	public void reloadCampaigns(){
		MessageUtil.clearMessage();
		viewCampIncId.setSrc("/zul/Empty.zul");
		viewCampIncId.setSrc("/zul/campaign/CampaignList.zul");
	}
	
}
