package org.mq.marketer.campaign.controller.report;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.SMSCampReportLists;
import org.mq.marketer.campaign.beans.SMSCampaignReport;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.SMSCampReportListsDao;
import org.mq.marketer.campaign.dao.SMSCampaignReportDao;
import org.mq.marketer.campaign.dao.SMSSuppressedContactsDao;
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

public class SMSSuppressedContactsReportController extends GenericForwardComposer{

	private Combobox exportCbId;
	
	private SMSCampaignReport smsCampaignReport;
	private Long crId;
	
	
	private SMSCampaignReportDao smsCampaignReportDao;
	private SMSSuppressedContactsDao smsSuppressedContactsDao;
	
	private Paging suppcontactsPagingId;
	private SMSCampReportListsDao smsCampReportListsDao;
	public SMSSuppressedContactsReportController() {
		
		smsCampaignReport = (SMSCampaignReport)Sessions.getCurrent().getAttribute("smsCampaignReport");
		crId = smsCampaignReport.getSmsCrId();
		smsCampReportListsDao = (SMSCampReportListsDao)SpringUtil.getBean("smsCampReportListsDao");
		smsSuppressedContactsDao = (SMSSuppressedContactsDao)SpringUtil.getBean("smsSuppressedContactsDao");
		
		
		
	}
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	String reportList;
	private Listbox suppContRepLbId;
	Long userId;
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		// TODO Auto-generated method stub
		
		super.doAfterCompose(comp);
		exportCbId.setSelectedIndex(0);
		if(smsCampaignReport == null) {
			//need to have logger
			return;
		}
		
		userId = smsCampaignReport.getUser().getUserId();
		reportList = getReportListNames(smsCampaignReport.getSmsCrId());
		//int total  = smsSuppressedContactsDao.getCountByReport(userId, reportList, smsCampaignReport.getSmsCrId() );
		long total  = smsCampaignReport.getSuppressedCount();
		
		suppcontactsPagingId.setTotalSize((int)total);
		suppcontactsPagingId.setDetailed(true);
		logger.info("size of suppressed contact list ::"+total);
		
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
		
	}
	
	
public void redraw(int startFrom, int count) {
		
		int itemCount = suppContRepLbId.getItemCount();
		for (; itemCount > 0; itemCount--) {
			suppContRepLbId.removeItemAt(itemCount - 1);
		}
		//System.gc();
		
		String suppressedTime = MyCalendar.calendarToString(smsCampaignReport.getSentDate(), MyCalendar.FORMAT_DATETIME_STYEAR);
		List<Object[]> suppContList = smsSuppressedContactsDao.getSuppressedContactsByReport(userId, reportList, smsCampaignReport.getSmsCrId(), suppressedTime, startFrom, count);
		
		for (Object[] obj : suppContList) {
			Listitem li = new Listitem();
			li.setValue(obj[0]);
			li.appendChild(new Listcell(obj[0].toString()));
			Listcell lc = new Listcell(obj[1].toString());
			lc.setParent(li);
			suppContRepLbId.appendChild(li);
		}
		
	}
	
	
	
	
public String getReportListNames(Long campRepId) {
		
		String campRepListNames = "";
		
		SMSCampReportLists campRepLists = smsCampReportListsDao.findByCampReportId(campRepId);
		String listNames = campRepLists.getListsName();
		String[] listNameArr = listNames.split(",");
		for (String listName : listNameArr) {
			
			
			if(campRepListNames.length() > 0) campRepListNames += ",";
			campRepListNames += "'"+listName.trim()+"'";
			
			
			
		}//for
		
		
		return campRepListNames;
		
		
	}
	

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

        for (Object head : suppContRepLbId.getHeads()) {
          String h = "";
          for (Object header : ((Listhead) head).getChildren()) {
        	  if(h.trim().length() > 0 ) h += s; 
            h += ((Listheader) header).getLabel() ;
          }
          sb.append(h + "\r\n");
        }
        if(suppContRepLbId.getItemCount()==0){
        	try {
        		MessageUtil.setMessage("No records found for this SMS campaign to export.","color:green");
			} catch (Exception e) {
			}
        	return;
        }
        
        int size = 1000;
		
		//long totalContacts  = smsSuppressedContactsDao.getCountByReport(userId,	reportList, smsCampaignReport.getSmsCrId());
        long totalContacts  = smsCampaignReport.getSuppressedCount();
        
		for(int i=0;i < totalContacts; i+=size) {
			
			
			
			//List<Object[]> suppContList = smsSuppressedContactsDao.getSuppressedContactsByReport(userId, reportList, smsCampaignReport.getSmsCrId(),  i, size);
			
			String suppressedTime = MyCalendar.calendarToString(smsCampaignReport.getSentDate(), MyCalendar.FORMAT_DATETIME_STYEAR);
			List<Object[]> suppContList = smsSuppressedContactsDao.getSuppressedContactsByReport(userId, reportList, smsCampaignReport.getSmsCrId(), suppressedTime,  i, size);
			if(suppContList.size()>0){
				
				for (Object[] objects : suppContList) {
					 String j = "";
					
					 j +=  "\""+objects[0]+"\""+ s+"\""+objects[1]+"\"";
					 sb.append(j + "\r\n");
				}
				
				
			}
			
					
					
		}
		 Filedownload.save(sb.toString().getBytes(), "text/plain", "SMSSuppressedContactsReports.csv");
        
        
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
	
}
