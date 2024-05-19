package org.mq.marketer.campaign.controller.report;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.CampReportLists;
import org.mq.marketer.campaign.beans.CampaignReport;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.CampReportListsDao;
import org.mq.marketer.campaign.dao.CampaignReportDao;
import org.mq.marketer.campaign.dao.CampaignsDao;
import org.mq.marketer.campaign.dao.SuppressedContactsDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.MessageUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.event.PagingEvent;




public class SuppressedContactsReportController extends GenericForwardComposer {

	private CampaignReport campaignReport;
	private Long crId;
	private Listbox suppContRepLbId;
	private CampaignsDao campaignsDao;
	private CampaignReportDao campaignReportDao;
	//private CampReportListsDao campReportListsDao;
	private SuppressedContactsDao suppressedContactsDao;
	private Paging suppcontactsPagingId;
	private CampReportListsDao campReportListsDao;
	
	
	
	public SuppressedContactsReportController() {
		
		campaignReport = (CampaignReport)Sessions.getCurrent().getAttribute("campaignReport");
		this.crId = campaignReport.getCrId();
		campaignReportDao = (CampaignReportDao)SpringUtil.getBean("campaignReportDao");
		campReportListsDao = (CampReportListsDao)SpringUtil.getBean("campReportListsDao");
		suppressedContactsDao = (SuppressedContactsDao)SpringUtil.getBean("suppressedContactsDao");
		//campReportListsDao = (CampReportListsDao)SpringUtil.getBean("CampReportListsDao");
	}
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		if(campaignReport == null) {
			//need to have logger
			return;
		}
		
		exportCbId.setSelectedIndex(0);
		
		String suppressedTime = MyCalendar.calendarToString(campaignReport.getSentDate(), MyCalendar.FORMAT_DATETIME_STYEAR);
		suppcontactsPagingId.setTotalSize(suppressedContactsDao.getCountByReport(campaignReport.getUser().getUserId(),
				getReportListNames(campaignReport.getCrId()), suppressedTime ) );
		
		suppcontactsPagingId.addEventListener("onPaging", new EventListener() {
			public void onEvent(Event e) {
				
				Paging openPaging = (Paging) e.getTarget();
				int desiredPage = openPaging.getActivePage();
				PagingEvent pagingEvent = (PagingEvent) e;
				int pSize = pagingEvent.getPageable().getPageSize();
				int ofs = desiredPage * pSize;
				String type = (String)openPaging.getAttribute("type");
				redraw(ofs,(byte) pagingEvent.getPageable().getPageSize());
				
			}
		});
		
		redraw(0,(byte)10);	
		
		
	}//doAfterCompose()
	
	private Listbox pageSizeLbId;
	public void onSelect$pageSizeLbId() {
			 
		 try {
				logger.debug("Just enter here...");
				
				if(suppContRepLbId.getItemCount() == 0 ) {
					
					logger.debug("No reports found for this user...");
					return;
				}
				suppcontactsPagingId.setActivePage(0);
				changeRows(pageSizeLbId.getSelectedItem().getLabel(),suppcontactsPagingId);
			} catch (Exception e) {
				logger.error("Exception :: errorr while getting from the changeRows method",e);
			}
		 
	}
		
		
	public void changeRows(String selStr, Paging campListPaging) throws Exception {
			try {
				if(campListPaging!=null){
					int pNo = Integer.parseInt(selStr);
					campListPaging.setPageSize(pNo);
					//campListPaging1.setPageSize(pNo);
					redraw(campListPaging.getActivePage()*pNo, (byte)pNo);
					//campListPaging.setActivePage(0);
				}
			} catch (WrongValueException e) {
				logger.error("Exception while getting the contacts...",e);
				
			} catch (NumberFormatException e) {
				logger.error("Exception while gettinf the contacts...",e);
			}
		}

	
	
public String getReportListNames(Long campRepId) {
		
		String campRepListNames = "";
		
		CampReportLists campRepLists = campReportListsDao.findByCampReportId(campRepId);
		String listNames = campRepLists.getListsName();
		String[] listNameArr = listNames.split(",");
		for (String listName : listNameArr) {
			
			
			if(campRepListNames.length() > 0) campRepListNames += ",";
			campRepListNames += "'"+listName.trim()+"'";
			
			
			
		}//for
		
		
		return campRepListNames;
		
		
	}
	
	public void redraw(int startFrom, int count) {
		
		int itemCount = suppContRepLbId.getItemCount();
		for (; itemCount > 0; itemCount--) {
			suppContRepLbId.removeItemAt(itemCount - 1);
		}
		//System.gc();
		
		String suppressedTime = MyCalendar.calendarToString(campaignReport.getSentDate(), MyCalendar.FORMAT_DATETIME_STYEAR);
		List<Object[]> suppContList = suppressedContactsDao.getSuppressedContactsByReport(campaignReport.getUser().
										getUserId(), getReportListNames(campaignReport.getCrId()), startFrom, count, suppressedTime);
		
		for (Object[] obj : suppContList) {
			Listitem li = new Listitem();
			li.setValue(obj[0]);
			li.appendChild(new Listcell(obj[0].toString()));
			Listcell lc = new Listcell(obj[1].toString());
			lc.setParent(li);
			suppContRepLbId.appendChild(li);
		}
		
	}
	
	private Combobox exportCbId;
	public void onClick$exportBtnId() {
		try {
			fileDownload(exportCbId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
		}
	} // onClick$exportBtnId

	public void fileDownload(Combobox fileType) throws Exception{
		//logger.debug("-- just entered --");
		int index = fileType.getSelectedIndex()==-1?0:fileType.getSelectedIndex();
		String type = (String)fileType.getItemAtIndex(index).getValue();
		if(type.equalsIgnoreCase("csv")){
		  	String s = ",";
	        StringBuffer sb = new StringBuffer();

	        for (Object head : suppContRepLbId.getHeads()) {
	          String h = "";
	          for (Object header : ((Listhead) head).getChildren()) {
	        	  
	        	  if(h.trim().length() > 0) h+= s;
	            h += "\""+((Listheader) header).getLabel() +"\"";
	          }
	          sb.append(h + "\r\n");
	        }
	        if(suppContRepLbId.getItemCount()==0){
	        	try {
					MessageUtil.setMessage("No suppressed contacts found for this campaign to export.","color:blue");
				} catch (Exception e) {
				}
	        	return;
	        }
	        
	        int size = 1000;
			String listNames = getReportListNames(campaignReport.getCrId());
			
			String suppressedTime = MyCalendar.calendarToString(campaignReport.getSentDate(), MyCalendar.FORMAT_DATETIME_STYEAR);
			long totalContacts  = suppressedContactsDao.getCountByReport(campaignReport.getUser().getUserId(),
									listNames, suppressedTime);
			
			for(int i=0;i < totalContacts; i+=size) {
				
				
				
				List<Object[]> suppContList = suppressedContactsDao.getSuppressedContactsByReport(campaignReport.getUser().
						getUserId(), listNames, i, size, suppressedTime );
				
				
				if(suppContList.size()>0){
					
					for (Object[] objects : suppContList) {
						 String j = "";
						
						 j +=  "\""+objects[0]+"\""+ s+"\""+objects[1]+"\"";
						 sb.append(j + "\r\n");
					}
					
					
				}
				
						
						
			}
			 Filedownload.save(sb.toString().getBytes(), "text/plain", "SuppressedContactsReports.csv");
	        
	        
	       /* for (Object item : suppContRepLbId.getItems()) {
	          String i = "";
	          for (Object cell : ((Listitem) item).getChildren()) {
	            i += ((Listcell) cell).getLabel() + s;
	          }
	          sb.append(i + "\r\n");
	        }
	        Filedownload.save(sb.toString().getBytes(), "text/plain", "SuppressedContactsReports.csv");*/
		}
	} // fileDownload
	
	
	
	
}//SuppressedContactsReportController
