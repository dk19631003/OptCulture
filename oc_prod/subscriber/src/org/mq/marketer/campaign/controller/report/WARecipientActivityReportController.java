package org.mq.marketer.campaign.controller.report;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.WACampaignReport;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.WACampaignSentDao;
import org.mq.marketer.campaign.dao.WAClicksDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.MessageUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.ComponentNotFoundException;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Window;
import org.zkoss.zul.event.PagingEvent;

public class WARecipientActivityReportController extends GenericForwardComposer{
	
	private Paging repPagingId;
	private Listhead genericLbHeaderId;
	private Listbox genericLbId;
	private WACampaignReport waCampaignReport;
	private WACampaignSentDao waCampaignSentDao;
	private WAClicksDao waClicksDao;
	
	private Hbox toolbarButtonHboxId;
	private Combobox exportCbId;
	private TimeZone clientTimeZone = null;
	private String others = "" ;
	private String pending="";
	private String statusPending="";
	private long otherCount = 0;
	
	private Window popupWindow;
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	
	private final String STATUS_DELIVERED = "Delivered";
	private final String TYPE_CLICKED = "Clicked";
	private final String TYPE_OTHERS = "Others";
	private final String TYPE_PENDING ="Pending";
	private final String TYPE_STATUS_PENDING= "Status Pending";
	public WARecipientActivityReportController(){
		waCampaignReport = (WACampaignReport)Sessions.getCurrent().getAttribute("waCampaignReport");
		this.waCampaignSentDao = (WACampaignSentDao)SpringUtil.getBean("waCampaignSentDao");
		clientTimeZone = (TimeZone)(Sessions.getCurrent().getAttribute("clientTimeZone"));
		
		waClicksDao = (WAClicksDao)SpringUtil.getBean("waClicksDao");
	}
	
	@Override
	public void doAfterCompose(Component comp){
		try{
			
			super.doAfterCompose(comp);
			logger.debug("---just entered----in WARecipientReport");
			
			Toolbarbutton tBarBtn ;
			tBarBtn = new Toolbarbutton();
			tBarBtn.setLabel(STATUS_DELIVERED);
			tBarBtn.setStyle("font-weight:bold;color:#43A0BA");
			tBarBtn.setAttribute("addEvent", STATUS_DELIVERED);
			tBarBtn.addEventListener("onClick", this);
			tBarBtn.setParent(toolbarButtonHboxId);
			
			tBarBtn = new Toolbarbutton();
			tBarBtn.setLabel(TYPE_CLICKED);
			tBarBtn.setStyle("font-weight:bold;color:#43A0BA;");
			tBarBtn.setAttribute("addEvent", TYPE_CLICKED);
			tBarBtn.addEventListener("onClick", this);
			tBarBtn.setParent(toolbarButtonHboxId);
			
			int pendingCount=checkForPendingAndStatusPending("'Pending'");
			if(pendingCount>0){
				tBarBtn = new Toolbarbutton();
				tBarBtn.setLabel(TYPE_PENDING);
				tBarBtn.setStyle("font-weight:bold;color:#43A0BA;");
				tBarBtn.setAttribute("addEvent", TYPE_PENDING);
				tBarBtn.addEventListener("onClick", this);
				tBarBtn.setParent(toolbarButtonHboxId);
			}
			int statusPendingCount=checkForPendingAndStatusPending("'Status Pending'");
			if(statusPendingCount>0){
				tBarBtn = new Toolbarbutton();
				tBarBtn.setLabel(TYPE_STATUS_PENDING);
				tBarBtn.setStyle("font-weight:bold;color:#43A0BA;");
				tBarBtn.setAttribute("addEvent", TYPE_STATUS_PENDING);
				tBarBtn.addEventListener("onClick", this);
				tBarBtn.setParent(toolbarButtonHboxId);
			}
			
			
			repPagingId.setAttribute(TYPE_OTHERS,false);
			repPagingId.setAttribute(TYPE_PENDING,false);
			repPagingId.setAttribute(TYPE_STATUS_PENDING,false);
			
			getRecieved();
			
		
		}catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	
	private Listbox pageSizeLbId;
	 public void onSelect$pageSizeLbId() {
			 
		 try {
				logger.debug("Just enter here...");
				
				if(genericLbId.getItemCount() == 0 ) {
					
					logger.debug("No reports found for this user...");
					return;
				}
				repPagingId.setActivePage(0);
				changeRows();
			} catch (Exception e) {
				logger.error("Exception :: errorr while getting from the changeRows method",e);
			}
		 
	 }
		
		
	 public void changeRows(){
			try {
				String selStr = pageSizeLbId.getSelectedItem().getLabel();
				if(repPagingId!=null){
					int pNo = Integer.parseInt(selStr);
					repPagingId.setPageSize(pNo);
					
					redraw((String)repPagingId.getAttribute("type"), repPagingId.getActivePage()*pNo, (byte)pNo);
					
				}
			} catch (WrongValueException e) {
				logger.error("Exception while getting the contacts...",e);
				
			} catch (NumberFormatException e) {
				logger.error("Exception while gettinf the contacts...",e);
			}
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
			//logger.debug("-- just entered --");
			int index = fileType.getSelectedIndex()==-1?0:fileType.getSelectedIndex();
			String type = (String)fileType.getItemAtIndex(index).getValue();
			if(type.equalsIgnoreCase("csv")){
			  	String s = ",";
		        StringBuffer sb = new StringBuffer();

		        for (Object head : genericLbId.getHeads()) {
		          String h = "";
		          for (Object header : ((Listhead) head).getChildren()) {
		        	  if(((Listheader) header).getLabel().isEmpty()) continue;
		        	  if(h.trim().length() > 0 ) h += s; 
		            h += "\""+((Listheader) header).getLabel()+ "\"";
		          }
		          sb.append(h + "\r\n");
		        }
		        if(genericLbId.getItemCount()==0){
		        	try {
		        		MessageUtil.setMessage("No records found for this WA campaign to export.","color:green");
					} catch (Exception e) {
					}
		        	return;
		        }
		        String statusType = (String)repPagingId.getAttribute("type");
		        
		        boolean isOpenedClicked = statusType.equalsIgnoreCase("'"+TYPE_CLICKED+"'");
		        boolean isOpen = false;
		        long totalContacts  = waCampaignReport.getClicks();
		        int size = 1000;
				
		        String status = null;
		        
		        
		        logger.info("status :: "+statusType);
		        
		        
        
		        if(!isOpenedClicked) {
		        	
		        	totalContacts  = waCampaignSentDao.getCount(waCampaignReport.getWaCrId(), statusType);
		        }
		        
				List<Object[]> rcptRepList = null;
				for(int i=0;i < totalContacts; i+=size) {
					
					if(!isOpenedClicked) {
						rcptRepList = waCampaignSentDao.getRepByStatus(waCampaignReport.getWaCrId(), statusType, i, size);
						if(rcptRepList.size()>0){
							
							for (Object[] object : rcptRepList) {
								
								 String j = "";
								 
								 if(statusType.equalsIgnoreCase("'"+STATUS_DELIVERED+"'")){
									 
									 j += "\""+object[0]+"\""+s+"\""+object[2]+"\"";
									 
								 } else {
									 
									 j +=  "\""+object[0]+"\""+s;
								 }
								
								 
								 if(object.length>3 && (Boolean) repPagingId.getAttribute(TYPE_OTHERS)) {
									 
									 j+="\""+object[3]+"\"";
									 
								 }
								 sb.append(j + "\r\n");
							}
							
							
						}//if
						
					}else{
						
						if(isOpen) {
							rcptRepList = waCampaignSentDao.getOpenedMobilesByCrId(waCampaignReport.getWaCrId(), i, size);
						}else{
							
							rcptRepList = waCampaignSentDao.getClickedMobilesByCrId(waCampaignReport.getWaCrId(), i, size);
						}
						
						if(rcptRepList.size()>0){
							
							for (Object[] object : rcptRepList) {
								
								 String j = "";
								
								 j += "\""+object[1]+"\""+s+"\""+object[2]+"\""+s;
								 if(object.length>3) {
									 j+="\""+object[3]+"\"";
								 }
								 sb.append(j + "\r\n");
							}
							
							
						}//if
						
					}
	
				}
				 Filedownload.save(sb.toString().getBytes(), "text/plain", "WARecipientActivityReport.csv");
		        
		      
			}
		} // fileDownload
	
	
		private void getPending() {
			
			repPagingId.setAttribute("type","'"+TYPE_PENDING+"'");
			int totCount = waCampaignSentDao.getCount(waCampaignReport.getWaCrId(), (String)repPagingId.getAttribute("type"));
			
			repPagingId.setTotalSize(totCount);
			repPagingId.addEventListener("onPaging",this);
			Components.removeAllChildren(genericLbHeaderId);
			exportCbId.setSelectedIndex(0);
			
			Listheader lh = new Listheader(" Mobile numbers");
			//lh.setWidth("50%");
			lh.setParent(genericLbHeaderId);
			
			lh = new Listheader("Reason");
			lh.setParent(genericLbHeaderId);
			
			changeRows();
			
		}
		
		private void getStatusPending() {
			
			repPagingId.setAttribute("type","'"+TYPE_STATUS_PENDING+"'");
			int totCount = waCampaignSentDao.getCount(waCampaignReport.getWaCrId(), (String)repPagingId.getAttribute("type"));
			
			repPagingId.setTotalSize(totCount);
			repPagingId.addEventListener("onPaging",this);
			Components.removeAllChildren(genericLbHeaderId);
			exportCbId.setSelectedIndex(0);
			
			Listheader lh = new Listheader(" Mobile numbers");
			//lh.setWidth("50%");
			lh.setParent(genericLbHeaderId);
			
			/*lh = new Listheader("Reason");
			//lh.setWidth("15%");
			lh.setParent(genericLbHeaderId);*/
			
			changeRows();
			
		}


	private void getRecieved() {
		
		repPagingId.setAttribute("type","'"+STATUS_DELIVERED+"'");
		int totCount = waCampaignSentDao.getCount(waCampaignReport.getWaCrId(), (String)repPagingId.getAttribute("type"));
		
		repPagingId.setTotalSize(totCount);
		repPagingId.addEventListener("onPaging",this);
		Components.removeAllChildren(genericLbHeaderId);
		exportCbId.setSelectedIndex(0);
		
		Listheader lh = new Listheader(" Mobile numbers");
		//lh.setWidth("50%");
		lh.setParent(genericLbHeaderId);
		
		/*lh = new Listheader("Status");
		lh.setParent(genericLbHeaderId);
	*/
		
		lh = new Listheader("No. of Clicks");
		//lh.setWidth("15%");
		lh.setParent(genericLbHeaderId);
		
		changeRows();
		
	}
	
	public void getClicks(){
		
		repPagingId.setAttribute("type","'"+TYPE_CLICKED+"'");
		int clickCount = waCampaignReport.getClicks();
		
		repPagingId.setTotalSize(clickCount);
		repPagingId.addEventListener("onPaging",this);
		Components.removeAllChildren(genericLbHeaderId);
		exportCbId.setSelectedIndex(0);
		
		Listheader lh = new Listheader("Mobile Numbers");
		lh.setWidth("30%");
		lh.setMaxlength(38);
		lh.setParent(genericLbHeaderId);
		
		lh = new Listheader("No. of Clicks");
		lh.setWidth("15%");
		lh.setParent(genericLbHeaderId);
		
		lh = new Listheader("Last Clicked URL");
		lh.setMaxlength(30);
		lh.setWidth("30%");
		lh.setParent(genericLbHeaderId);
		
		lh = new Listheader("");
		lh.setWidth("25%");
		lh.setParent(genericLbHeaderId);
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
				if(toolBarBtnAttr.equalsIgnoreCase(STATUS_DELIVERED)){
					getRecieved();
				}
				else if(toolBarBtnAttr.equalsIgnoreCase(TYPE_CLICKED)){
					
					getClicks();
				}
				else if(toolBarBtnAttr.equalsIgnoreCase(TYPE_OTHERS)){
					repPagingId.setTotalSize((int)otherCount);
					repPagingId.setAttribute("type",others);
					repPagingId.setAttribute(TYPE_OTHERS,true);
					Components.removeAllChildren(genericLbHeaderId);
					
					Listheader lh;
					lh = new Listheader(" Mobile numbers ");
					lh.setParent(genericLbHeaderId);
					
					lh = new Listheader(" Status ");
					lh.setParent(genericLbHeaderId);
				
					changeRows();
				}
				else if(toolBarBtnAttr.equalsIgnoreCase(TYPE_PENDING)){
					repPagingId.setTotalSize((int)otherCount);
					repPagingId.setAttribute("type",pending);
					repPagingId.setAttribute(TYPE_PENDING,true);
					Components.removeAllChildren(genericLbHeaderId);
					
					Listheader lh;
					lh = new Listheader(" Mobile numbers ");
					lh.setParent(genericLbHeaderId);
					
					lh = new Listheader(" Status ");
					lh.setParent(genericLbHeaderId);
					
					getPending();
					changeRows();
				}else if(toolBarBtnAttr.equalsIgnoreCase(TYPE_STATUS_PENDING)){
					repPagingId.setTotalSize((int)otherCount);
					repPagingId.setAttribute("type",statusPending);
					repPagingId.setAttribute(TYPE_STATUS_PENDING,true);
					Components.removeAllChildren(genericLbHeaderId);
					
					Listheader lh;
					lh = new Listheader(" Mobile numbers ");
					lh.setParent(genericLbHeaderId);
					
					lh = new Listheader(" Status ");
					lh.setParent(genericLbHeaderId);
					
					getStatusPending();
					changeRows();
				}
								
				else {
					
					repPagingId.setAttribute(TYPE_OTHERS,false);
					
					String type = "'" + toolBarBtnAttr + "'";

					int totCount = waCampaignSentDao.getCount(waCampaignReport.getWaCrId(), type);
					repPagingId.setTotalSize(totCount);
					
					repPagingId.setAttribute("type", type);
					
					Components.removeAllChildren(genericLbHeaderId);
					
					Listheader lh = new Listheader(" Mobile numbers ");
					
					lh.setParent(genericLbHeaderId);
					
					changeRows();
				}
				
		 }
		
		
	}
	
	private void redraw(String type,int start_index, byte _size) {
		try {
			logger.debug("type is--------->"+type+ " "+_size );
			int count = genericLbId.getItemCount();
			
			for(;count>0;count--){
				genericLbId.removeItemAt(count-1);
			}
			 if(type.equalsIgnoreCase("opened")) {
				
				Calendar tempCal = null;
				List<Object[]> list = waCampaignSentDao.getOpenedMobilesByCrId(waCampaignReport.getWaCrId(),start_index,_size);
				logger.debug("the list size is====>"+list.size());
				
				for (final Object[] obj : list) {
					
					final Listitem li = new Listitem();
					li.setValue(obj[0]);
					li.appendChild(new Listcell(obj[1].toString()));
					li.appendChild(new Listcell(obj[2].toString()));
					tempCal = (Calendar)obj[3];
					li.appendChild(new Listcell(MyCalendar.calendarToString(tempCal, null, clientTimeZone)));
					
					Listcell lc = new Listcell();
					lc.setParent(li);
					
					genericLbId.appendChild(li);
				}
				
			}//else if
			
			
			else if(type.equalsIgnoreCase("'"+TYPE_CLICKED+"'")) {
				
				logger.info("is entered clicks");
				
				List<Object[]> list = waCampaignSentDao.getClickedMobilesByCrId(waCampaignReport.getWaCrId(),start_index,_size);
				logger.debug("the list size is in clicked ====>"+list.size());
				Label lbl;
				Listcell lc;
				for (final Object[] obj : list) {
					final Listitem li = new Listitem();
					
					li.setValue(obj[0]);
					
					li.appendChild(new Listcell(obj[1].toString()));
					
					li.appendChild(new Listcell("" + obj[2]));
					lc = new Listcell();
					
					lbl = new Label("" + obj[3]);
					lbl.setMaxlength(25);
					lbl.setTooltiptext("" + obj[3]);
					lbl.setParent(lc);
					li.appendChild(lc);
					
					lc = new Listcell();
					lc.setParent(li);
					Toolbarbutton tb = new Toolbarbutton("More Details");
					tb.setParent(lc);
					tb.setStyle("color:blue;");
					tb.addEventListener("onClick",  new EventListener() {
						public void onEvent(Event e) {
							long sentId = (Long)li.getValue();
							doPopUp(sentId,obj[1].toString(), TYPE_CLICKED);
						}
					});
					
					genericLbId.appendChild(li);
				}
				
			}//else if			
			
			
			else if((Boolean) repPagingId.getAttribute(TYPE_OTHERS)) {

				List<Object[]> list = waCampaignSentDao.getRepByStatus(waCampaignReport.getWaCrId(),type, start_index,_size);
				logger.debug("the list size is====>"+list.size());
				Listitem li;
				for (Object[] obj : list) {
					
					li = new Listitem();
					li.appendChild(new Listcell((String)obj[0]));
					
					li.appendChild(new Listcell("" + obj[3]));
					genericLbId.appendChild(li);
				}//for
			}else if((Boolean) repPagingId.getAttribute(TYPE_PENDING)) {

				List<Object[]> list = waCampaignSentDao.getRepByStatus(waCampaignReport.getWaCrId(),type, start_index,_size);
				logger.debug("the list size is====>"+list.size());
				Listitem li;
				for (Object[] obj : list) {
					
					li = new Listitem();
					li.appendChild(new Listcell((String)obj[0]));
					
					//li.appendChild(new Listcell("" + obj[3]));
					li.appendChild(new Listcell("Delivered to recepient"));
					genericLbId.appendChild(li);
				}//for
			}
			
			else {
				
				List<Object[]> list = waCampaignSentDao.getRepByStatus(waCampaignReport.getWaCrId(), type, start_index,_size);
				logger.debug("the list size is====>"+list.size());
				Listitem li;
				for (Object[] obj : list) {
					li = new Listitem();
					
					logger.info("mobile no is"+obj[0]);
					
					logger.info("clikcs no is"+obj[2]);
					
					
					
					
					li.appendChild(new Listcell(""+obj[0]));
					
					//li.appendChild(new Listcell(""+obj[3]));
					
					
					
					li.appendChild(new Listcell(""+obj[2]));
					
					
					genericLbId.appendChild(li);
				}
				
			}//else if
			
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception :::",e);
		}
		
		
	}//redraw
	
	private void doPopUp(long sentId,String mobileNumber,String type){
		try {
			if(logger.isDebugEnabled())
				logger.debug("--just entered --");
			Div div = (Div)popupWindow.getFellow("popDivId");
			Components.removeAllChildren(div);
			Listbox lb = new Listbox();
			lb.setSclass("contactsView");
			lb.setHeight("350px");
			lb.setParent(div);
			Listhead lh = new Listhead();
			lh.setParent(lb);
			Listheader lHeader;
			
			if(type.equals(TYPE_CLICKED)){
				lHeader = new Listheader("Urls clicked by "+ mobileNumber);
				lHeader.setParent(lh);
				lHeader = new Listheader("Date ");
				lHeader.setWidth("150px");
				lHeader.setAlign("center");
				lHeader.setParent(lh);
				
				if(waClicksDao == null)
					waClicksDao = (WAClicksDao)SpringUtil.getBean("waClicksDao");
				if(logger.isDebugEnabled())
					logger.debug("No. of clicks by sent Id : "+sentId);
				List<Object[]> clickList = waClicksDao.getClickInfoBySentId(sentId);
				Listitem li ;
				Listcell lc ;
				Label lbl ;
				for(Object[] obj : clickList) {
					li = new Listitem();
					li.setParent(lb);
					lc = new Listcell();
					lbl = new Label(obj[0].toString());
					lbl.setMaxlength(40);
					lbl.setTooltiptext(obj[0].toString());
					lc.appendChild(lbl);
					li.appendChild(lc);
					li.appendChild(new Listcell(
							MyCalendar.calendarToString((Calendar)obj[1], null, clientTimeZone)));
				}
			}
			popupWindow.setVisible(true);
			popupWindow.setPosition("center");
			popupWindow.doHighlighted();
		} catch (ComponentNotFoundException e) {
			// TODO Auto-generated catch block
			logger.error("Exception :::",e);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception :::",e);
		}
	}
	public int checkForPendingAndStatusPending(String status){
		int count = waCampaignSentDao.getCount(waCampaignReport.getWaCrId(),status);
		if(count!=0) return count;
		else return 0;
	}
		
	}//class



