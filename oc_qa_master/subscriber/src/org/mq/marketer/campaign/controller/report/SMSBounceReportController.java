package org.mq.marketer.campaign.controller.report;

import java.util.List;
import java.util.TimeZone;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.SMSBounces;
import org.mq.marketer.campaign.beans.SMSCampaignReport;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.dao.SMSBouncesDao;
import org.mq.marketer.campaign.dao.SMSCampaignSentDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.SMSStatusCodes;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.event.PagingEvent;

public class SMSBounceReportController extends GenericForwardComposer{

	private Paging bouncePagingId;
	private Listhead bounceLbHeaderId;
	private Listbox bounceGenericLbId;
	private SMSCampaignReport smsCampaignReport;
	private SMSCampaignSentDao smsCampaignSentDao;
	
	private Hbox toolbarButtonHboxId;
	
	
	private TimeZone clientTimeZone = null;
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	private int currentPage = 1;
	
	private String others = "" ;
	private long otherCount = 0;
	private SMSBouncesDao smsBouncesDao;
	
	private Combobox exportCbId;
	public SMSBounceReportController() {
		
		smsBouncesDao = (SMSBouncesDao)SpringUtil.getBean("smsBouncesDao");

	}
	
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		// TODO Auto-generated method stub
		super.doAfterCompose(comp);
		
		List<Object[]> catList = smsBouncesDao.getAllBounceCategories(smsCampaignReport.getSmsCrId());
		logger.info("catListcatList"+catList.size());
		if(catList == null || catList.size() == 0) return;
		
		//logger.debug("size of list i got is "+catList.size());
		exportCbId.setSelectedIndex(0);
		
		String defaultType = "";
		Toolbarbutton tBarBtn ;
		int count = 0; 
		for(Object[] category : catList) {
			logger.debug("category[0]"+category[0]);
			if(category[0] != null) {
				if(count<5) {
						tBarBtn = new Toolbarbutton();
						tBarBtn.setLabel((String) category[0]);
						tBarBtn.setStyle("font-weight:bold;color:#43A0BA");
						tBarBtn.setAttribute("addEvent", (String) category[0]);
						tBarBtn.addEventListener("onClick", this);
						tBarBtn.setParent(toolbarButtonHboxId);
						logger.debug("in if  "+(String) category[0]);
						if(count==0) defaultType = "'" + (String) category[0] + "'";
				}
				else {
					logger.debug("in else  "+(String) category[0]);
					if(others.length() > 0) others += ",";
					others += "'" + (String) category[0] + "'";
					otherCount += (Long)category[1];
				}
			}
			count++;
		}
	  if(catList.size()>5) {
		  	tBarBtn = new Toolbarbutton();
		  	tBarBtn.setLabel("Other Categories");
			tBarBtn.setStyle("font-weight:bold;color:#43A0BA");
			tBarBtn.setAttribute("addEvent", "Other Categories");
			tBarBtn.addEventListener("onClick", this);
			tBarBtn.setParent(toolbarButtonHboxId);
			
	  }
	  
	  bouncePagingId.setAttribute("type",defaultType);
	  logger.info("smsCampaignReport.getSmsCrId(), defaultType"+smsCampaignReport.getSmsCrId()+"....."+ defaultType);
	  int totCount = smsBouncesDao.getCount(smsCampaignReport.getSmsCrId(), defaultType);
	  
	  
	  //todo ned to calculate
	  bouncePagingId.setTotalSize(totCount);
	  
	  bouncePagingId.addEventListener("onPaging",this);
	  
	  Components.removeAllChildren(bounceLbHeaderId);
	  bouncePagingId.setAttribute("OtherCat",false);
	  
	  getDefault();
	  
	  
	}
	
	
	
 private Listbox pageSizeLbId;
 public void onSelect$pageSizeLbId() {
		 
		 try {
				logger.debug("Just enter here...");
				
				if(bounceGenericLbId.getItemCount() == 0 ) {
					
					logger.debug("No SMS Campaigns found for this user...");
					return;
				}
				bouncePagingId.setActivePage(0);
				changeRows();
			} catch (Exception e) {
				logger.error("Exception :: errorr while getting from the changeRows method",e);
			}
		 
	 }
	
	
	 public void changeRows(){
		 
			try {
				String selStr = pageSizeLbId.getSelectedItem().getLabel();
				if(bouncePagingId!=null){
					int pNo = Integer.parseInt(selStr);
					bouncePagingId.setPageSize(pNo);
					
					redraw((String)bouncePagingId.getAttribute("type"),bouncePagingId.getActivePage()*pNo, (byte)pNo);
					
				}
			} catch (WrongValueException e) {
				logger.error("Exception while getting the contacts...",e);
				
			} catch (NumberFormatException e) {
				logger.error("Exception while gettinf the contacts...",e);
			}
		}
	
	
public void onClick$undeliveredTbarBtnId(){
		
		setReptActRepSubContent("unDelivered");
	}
	
	public void onClick$invalidNumTbarBtnId() {
		
		setReptActRepSubContent("invalidNumber");
	}
	
	public void onClick$othersTbarBtnId(){
		
		setReptActRepSubContent("others");
	}
	
	
	public void onClick$blockedTbarBtnId(){
		
		setReptActRepSubContent("blocked");
	}
	
	
	public void onClick$exportBtnId() {
		try {
			export(exportCbId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
		}
	} // onClick$exportBtnId

	
	public void export(Combobox fileType) throws Exception{
		 if(bounceGenericLbId.getItemCount()==0){
        	try {
				MessageUtil.setMessage("No records found for this SMS campaign to export.","color:green");
			} catch (Exception e) {
			}
        	return;
        }
		
		int index = fileType.getSelectedIndex()==-1?0:fileType.getSelectedIndex();
		String type = (String)fileType.getItemAtIndex(index).getValue();
		if(type.equalsIgnoreCase("csv")){

	        StringBuffer sb = new StringBuffer();

	        for (Object head : bounceGenericLbId.getHeads()) {
	          String h = "";
	          for (Object header : ((Listhead) head).getChildren()) {
	            h += "\"" + ((Listheader) header).getLabel() + "\"" + ",";
	          }
	          sb.append(h + "\r\n");
	        }
	       
	        
	        int size = 1000;
	        
	        String statusType = (String)bouncePagingId.getAttribute("type");
	        
	       
			long totalContacts  = smsBouncesDao.getCount(smsCampaignReport.getSmsCrId(), statusType);
			
			
			
			
			for(int i=0;i < totalContacts; i+=size) {
				
				
				if((Boolean) bouncePagingId.getAttribute("OtherCat")) {
						List<SMSBounces> bounceContList = smsBouncesDao.getBouncesCatOthers(smsCampaignReport.getSmsCrId(), statusType, i, size);
						
						
						
						if(bounceContList.size()>0){
							
							for (SMSBounces bounceCont : bounceContList) {
								 String j = "";
								 j += "\"" + bounceCont.getMobile() + "\"" + ",";
								 j +=  "\"" + bounceCont.getCategory() + "\"" + ",";
								 sb.append(j + "\r\n");
							}
							
							
						}//if
				}
				else {
					
					//List<String> bounceContList = smsBouncesDao.getBouncesByCat(smsCampaignReport.getSmsCrId(), statusType, i, size);
					List<SMSBounces> bounceContList = smsBouncesDao.getBouncesAndReasonByCat(smsCampaignReport.getSmsCrId(), statusType, i, size);
					
					
					
					if(bounceContList.size()>0){
						
						for (SMSBounces smsBounces : bounceContList) {
							 String j = "";
							
							 j +=  "\""+smsBounces.getMobile()+"\"" + ",";
							 j +=  "\""+smsBounces.getMessage()+"\"" + ",";
							 sb.append(j + "\r\n");
						}
						
						
					}//if
				}
				
						
						
			}
			 Filedownload.save(sb.toString().getBytes(), "text/plain", "SMSBouncedMobiles.csv");
		}
	} // fileDownload
	
	
	
	
	
	
	
	
	
	
public void setReptActRepSubContent(String subContentName){
		
		//logger.debug("in setReptActRepSubContent" );	
		if(subContentName.equalsIgnoreCase("UnDelivered")) {
			bouncePagingId.setAttribute("type","'undelivered'");
			
			Components.removeAllChildren(bounceLbHeaderId);
			int totCount = smsBouncesDao.getCount(smsCampaignReport.getSmsCrId(), SMSStatusCodes.CLICKATELL_STATUS_DELIVERY_ERROR);
			bouncePagingId.setTotalSize(totCount);
			getDefault();
			currentPage = 1;
			
		} 
}
	
private void getDefault() {
	
	Listheader lh; //= new Listheader(" Mobile numbers ");
	lh = new Listheader(" Mobile numbers ");
	lh.setParent(bounceLbHeaderId);
	lh = new Listheader(" Reason ");
	lh.setParent(bounceLbHeaderId);
	

	changeRows();
	
	
	
}


public void onEvent(Event event) throws Exception {
	// TODO Auto-generated method stub
	super.onEvent(event);
	
		if(event.getTarget() instanceof Paging) {
			Paging openPaging = (Paging) event.getTarget();
			int desiredPage = openPaging.getActivePage();
			PagingEvent pagingEvent = (PagingEvent) event;
			int pSize = pagingEvent.getPageable().getPageSize();
			int ofs = desiredPage * pSize;
			String type = (String)openPaging.getAttribute("type");
			redraw(type,ofs, (byte) pagingEvent.getPageable().getPageSize());

			
			
		}
		
		if(event.getTarget() instanceof Toolbarbutton){
			
			Toolbarbutton toolBarBtn = (Toolbarbutton) event.getTarget();
			String toolBarBtnAttr = (String)toolBarBtn.getAttribute("addEvent");
			
			if(toolBarBtnAttr.equalsIgnoreCase("Other Categories") ){
				bouncePagingId.setTotalSize((int) otherCount);
				bouncePagingId.setAttribute("type",others);
				bouncePagingId.setAttribute("OtherCat",true);
				Components.removeAllChildren(bounceLbHeaderId);
				
				Listheader lh;
				lh = new Listheader(" Mobile numbers ");
				lh.setParent(bounceLbHeaderId);
				lh = new Listheader(" Reason ");
				lh.setParent(bounceLbHeaderId);
			
				changeRows();
			}
			
			else {
				
				bouncePagingId.setAttribute("OtherCat",false);
				String type = "'" + toolBarBtnAttr + "'";
				int totCount = smsBouncesDao.getCount(smsCampaignReport.getSmsCrId(), type);
				bouncePagingId.setTotalSize(totCount);
				
				bouncePagingId.setAttribute("type", type);
				
				Components.removeAllChildren(bounceLbHeaderId);
				
				Listheader lh; //= new Listheader(" Mobile numbers ");
				lh = new Listheader(" Mobile numbers ");
				lh.setParent(bounceLbHeaderId);
				lh = new Listheader(" Reason ");
				lh.setParent(bounceLbHeaderId);
				
				changeRows();
			}
			
	 }
	
	
}

private void redraw(String type,int start_index, byte _size) {
	try {
		
		//logger.debug("type is--------->"+type);
		int count = bounceGenericLbId.getItemCount();
		
		for(;count>0;count--){
			bounceGenericLbId.removeItemAt(count-1);
		}
		
		if((Boolean) bouncePagingId.getAttribute("OtherCat")) {

			List<SMSBounces> list = smsBouncesDao.getBouncesCatOthers(smsCampaignReport.getSmsCrId(),type, start_index,_size);
			//logger.debug("the list size is====>"+list.size());
			Listitem li;
			for (SMSBounces obj : list) {
				
				li = new Listitem();
				li.appendChild(new Listcell(obj.getMobile()));
				//li.appendChild(new Listcell(obj.getCategory()));
				li.appendChild(new Listcell(obj.getMessage()));
				bounceGenericLbId.appendChild(li);
			}//for
		}
		else {
				//List<String> list = smsBouncesDao.getBouncesByCat(smsCampaignReport.getSmsCrId(),type, start_index,_size);
				List<SMSBounces> list = smsBouncesDao.getBouncesAndReasonByCat(smsCampaignReport.getSmsCrId(),type, start_index,_size);
			//	logger.debug("the list size is====>"+list.size());
				Listitem li;
				for (SMSBounces obj : list) {
					
					li = new Listitem();
					//li.appendChild(new Listcell(obj));
					//added by harshi
					li.appendChild(new Listcell(obj.getMobile()));
					li.appendChild(new Listcell(obj.getMessage()));
					bounceGenericLbId.appendChild(li);
				}//for
		}
		
	}catch (Exception e) {
		// TODO: handle exception
	}
}


}
