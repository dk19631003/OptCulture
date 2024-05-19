package org.mq.marketer.campaign.controller.report;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.TimeZone;


import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.CampaignReport;
import org.mq.marketer.campaign.beans.CampaignSent;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.BounceDao;
import org.mq.marketer.campaign.dao.CampaignSentDao;
import org.mq.marketer.campaign.general.BounceCategories;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.optculture.data.dao.JdbcResultsetHandler;
import org.mq.optculture.utils.OCCSVWriter;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.event.PagingEvent;

@SuppressWarnings("serial")
public class BouncesController extends GenericForwardComposer {

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	CampaignReport campaignReport;
	private Listbox bounceGenericLbId;
	private Listbox notSentLbId;
	private Paging bouncePagingId;
	private Paging notSentPagingId;
	private Groupbox notSentGbBox;
	//Desktop desktop = null;
	Long crId;
	Paging notOpenPaging;
	BounceDao bounceDao;
	CampaignSentDao campaignSentDao;
	//private String subContentName = null;
	
	public BouncesController() {
		bounceDao = (BounceDao)SpringUtil.getBean("bounceDao");
		campaignReport = (CampaignReport)Sessions.getCurrent().getAttribute("campaignReport");
		this.crId = campaignReport.getCrId();
		campaignSentDao = (CampaignSentDao)SpringUtil.getBean("campaignSentDao");
	}
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		// TODO Auto-generated method stub
		super.doAfterCompose(comp);
		
		exportCbId.setSelectedIndex(0);
		exportPendingReportCbId.setSelectedIndex(0);
		//subContentName = "Undeliverable";
		//subContentName = BounceCategories.BOUNCED;
		bouncePagingId.setAttribute("type", BounceCategories.BOUNCED);
		bouncePagingId.setTotalSize(bounceDao.getBounceSizeByType(crId, BounceCategories.BOUNCED));
		bouncePagingId.addEventListener("onPaging", new EventListener() {
			public void onEvent(Event e) {
				
				Paging openPaging = (Paging) e.getTarget();
				int desiredPage = openPaging.getActivePage();
				PagingEvent pagingEvent = (PagingEvent) e;
				int pSize = pagingEvent.getPageable().getPageSize();
				int ofs = desiredPage * pSize;
				String type = (String)openPaging.getAttribute("type");
				redraw(type,ofs,(byte) pagingEvent.getPageable().getPageSize());
				
			}
		});
		//redraw("Undeliverable",0,(byte)10);	
		int notSentCount = campaignSentDao.getNotSentCount(crId);
		if(notSentCount>0){
		//notSentLbId.setVisible(true);
		notSentGbBox.setVisible(true);
		notSentPagingId.setAttribute("type", "Submitted");
		notSentPagingId.setTotalSize(notSentCount);
		notSentPagingId.addEventListener("onPaging", new EventListener() {
			public void onEvent(Event e) {
				
				Paging openPaging = (Paging) e.getTarget();
				int desiredPage = openPaging.getActivePage();
				PagingEvent pagingEvent = (PagingEvent) e;
				int pSize = pagingEvent.getPageable().getPageSize();
				int ofs = desiredPage * pSize;
				String type = (String)openPaging.getAttribute("type");
				redraw(type,ofs,(byte) pagingEvent.getPageable().getPageSize());
				
			}
		});
		}
		changeRows();
	} // doAfterCompose
	
/*
	public void init(Listbox bounceGenericLbId,Paging bouncePagingId) throws Exception {
		this.bounceGenericLbId = bounceGenericLbId;
		this.bouncePagingId = bouncePagingId;
		bouncePagingId.setAttribute("type","Undeliverable");
		
		subContentName = "Undeliverable";
		bouncePagingId.setTotalSize(bounceDao.getBounceSizeByType(crId, subContentName));
		bouncePagingId.addEventListener("onPaging", new EventListener() {
			public void onEvent(Event e) {
				
				Paging openPaging = (Paging) e.getTarget();
				int desiredPage = openPaging.getActivePage();
				PagingEvent pagingEvent = (PagingEvent) e;
				int pSize = pagingEvent.getPageable().getPageSize();
				int ofs = desiredPage * pSize;
				String type = (String)openPaging.getAttribute("type");
				redraw(type,ofs,(byte) pagingEvent.getPageable().getPageSize());
				
			}
		});
		redraw("Undeliverable",0,(byte)10);	
	}
	*/
	private Listbox pageSizeLbId;
	private Listbox notSentpageSizeLbId;
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
	 public void onSelect$notSentpageSizeLbId() {
		 
		 try {
				logger.debug("Just entered here...");
				
				if(notSentLbId.getItemCount() == 0 ) {
					
					logger.debug("No Campaigns found for this user...");
					return;
				}
				notSentpageSizeLbId.setActivePage(0);
				changeRows();
			} catch (Exception e) {
				logger.error("Exception :: errorr while getting from the changeRows method",e);
			}
		 
	 }
		
		
		 public void changeRows()  {
				try {
					String selStr=pageSizeLbId.getSelectedItem().getLabel();
					String selectedStr=notSentpageSizeLbId.getSelectedItem().getLabel();
					if(bouncePagingId!=null){
						int pNo = Integer.parseInt(selStr);
						bouncePagingId.setPageSize(pNo);
						//campListPaging1.setPageSize(pNo);
						redraw((String)bouncePagingId.getAttribute("type"), bouncePagingId.getActivePage()*pNo, (byte)pNo);
						//bouncePagingId.setActivePage(0);
					}
					if(notSentPagingId!=null){
						int pNo = Integer.parseInt(selectedStr);
						notSentPagingId.setPageSize(pNo);
						redraw((String)notSentPagingId.getAttribute("type"), notSentPagingId.getActivePage()*pNo, (byte)pNo);
					}
				} catch (WrongValueException e) {
					logger.error("Exception while getting the contacts...",e);
					
				} catch (NumberFormatException e) {
					logger.error("Exception while gettinf the contacts...",e);
				}
			}
		
	
	
	public void setBouncePageSubContent(String subContentName) {
		//this.subContentName = subContentName;
		int repCount = bounceDao.getBounceSizeByType(crId,subContentName);
		
		logger.debug("Bounce Count for Cat :"+subContentName +"  = "+repCount);
		
		bouncePagingId.setTotalSize(repCount);
		bouncePagingId.setAttribute("type",subContentName);
		//bouncePagingId.setActivePage(0);
		//pageSizeLbId.setSelectedIndex(1);
		//redraw(subContentName,0,(byte)10);
		changeRows();
	}

	private void redraw(String type,int start_index, byte _size) {
		logger.info("type : "+type);
		if(type.equalsIgnoreCase("Submitted")){
			int notSentCount = notSentLbId.getItemCount();
			for (; notSentCount > 0; notSentCount--) {
				notSentLbId.removeItemAt(notSentCount - 1);
			}
			//System.gc();
			List<CampaignSent> notSentList = campaignSentDao.findSentByStatusAndCrId("Submitted",crId);
			if(notSentList!=null){
			for (CampaignSent obj : notSentList) {
				logger.info("------object"+obj.getEmailId());
				Listitem li = new Listitem();
				li.setValue(obj.getEmailId());
				li.setStyle("cursor: default;");
				Listcell tempCell = new Listcell(obj.getEmailId());
				tempCell.setStyle("cursor: default;");
				li.appendChild(tempCell);
				notSentLbId.appendChild(li);
				}
			}
		}
		else{
			int count = bounceGenericLbId.getItemCount();
			for (; count > 0; count--) {
				bounceGenericLbId.removeItemAt(count - 1);
			}
			//System.gc();
	//		logger.info("calling method");
			List<Object[]> list = bounceDao.getBouncesByCategory(crId,type,start_index,_size); //(start_index, _size);
	//		logger.info("called method");
			for (Object[] obj : list) {
				logger.info("------object 0"+obj[0]);
				Listitem li = new Listitem();
				li.setValue(obj[0]);
				li.setStyle("cursor: default;");
				Listcell tempCell = new Listcell(obj[1].toString());
				tempCell.setStyle("cursor: default;");
				li.appendChild(tempCell);
				Label reason = new Label(obj[2] != null ? obj[2].toString() : "");
				tempCell = new Listcell(reason.getValue());
				tempCell.setStyle("cursor: default;");
				tempCell.setTooltiptext(reason.getValue());
				li.appendChild(tempCell);
				bounceGenericLbId.appendChild(li);
			}
		}
	}

	public void export(Combobox exportCbId,String reportType){
		logger.debug("-- just entered --");
		String fileType = exportCbId.getSelectedItem().getLabel();
		String category = "";
		if(reportType.equalsIgnoreCase("PendingReport")){
			category ="Delivery Status Pending";
		}else{
		category = (String)bouncePagingId.getAttribute("type");
		}
		String userName = GetUser.getUserName();
		String usersParentDirectory = (String)PropertyUtil.getPropertyValue("usersParentDirectory");
		String exportDir = usersParentDirectory + "/" + userName + "/Export/" ;
		File downloadDir = new File(exportDir);
		String query = Constants.STRING_NILL;
		File file = null;
		BufferedWriter bw = null;
		String filePath = Constants.STRING_NILL;
		JdbcResultsetHandler jdbcResultsetHandler = null;
		if(downloadDir.exists()){
			try {
				FileUtils.deleteDirectory(downloadDir);
				logger.debug(downloadDir.getName() + " is deleted");
			} catch (Exception e) {
				logger.error("Exception");
				logger.warn(downloadDir.getName() + " is not deleted");
			}
		}
		if(!downloadDir.exists()){
			downloadDir.mkdirs();
		}
		
		if(fileType.contains("csv")){
			try {
				TimeZone clientTimeZone = (TimeZone)(Sessions.getCurrent().getAttribute("clientTimeZone"));
				
				String name = campaignReport.getCampaignName();
				if(name.contains("/")) {
					
					name = name.replace("/", "_") ;
					
				}
				
				filePath = 
					exportDir +  "Report_" + name + "_" + 
					MyCalendar.calendarToString(campaignReport.getSentDate(), MyCalendar.FORMAT_YEARTOSEC, clientTimeZone) +
					"_" + category + ".csv";
				
				logger.debug("Download File path : " + filePath);
				file = new File(filePath);
				bw = new BufferedWriter(new FileWriter(filePath));
				int count=0;
				if(reportType.equalsIgnoreCase("PendingReport")){
					count=notSentPagingId.getTotalSize();
					bw.write("\"Email Address\" \r\n");
					query = "select cs.email_id from campaign_sent cs WHERE  cs.cr_id = "+crId+" AND cs.status='Submitted' ";
				}
				else {
					count = bouncePagingId.getTotalSize();
		
				logger.debug("Total count : " + count);
				bw.write("\"Email Address\",\"Reason\" \r\n");
				query = "select cs.email_id,b.message from bounces b, campaign_sent cs WHERE  cs.cr_id = "+crId+" AND cs.status='Bounced' AND b.sent_id = cs.sent_id and b.category like '"
						  + category+"%'";
				}
				jdbcResultsetHandler = new JdbcResultsetHandler();
				jdbcResultsetHandler.executeStmt(query);
				if(jdbcResultsetHandler.totalRecordsSize() == 0)
				{
					MessageUtil.setMessage("There are no records to export.", "style:red;");
					return ;
				}
				OCCSVWriter csvWriter = new OCCSVWriter(bw);
				try {
					csvWriter = new OCCSVWriter(bw);
					csvWriter.writeAll(jdbcResultsetHandler.getResultSet(), false);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					logger.error("exception while initiating writer ",e);
					
				}finally{
					bw.flush();
					csvWriter.flush();
					bw.close();
					csvWriter.close();
					csvWriter = null;
					
				}
				Filedownload.save(file, "text/plain");
			} catch (IOException e) {
				logger.info("Exception while writing to file ",e);
			} finally {
				if(jdbcResultsetHandler != null)jdbcResultsetHandler.destroy(); jdbcResultsetHandler = null; fileType = null;
				category = null; userName = null; usersParentDirectory = null; exportDir = null; downloadDir = null;
				query = null; file = null; bw = null; filePath = null; //System.gc();
				
			}
			logger.debug("-- exit --");
		}
	} // export
	private Toolbarbutton hardBounceTbarBtnId,softBounceTbarBtnId,droppedTbarBtnId,blockedTbarBtnId,othersTbarBtnId;//nonExtentionAddrTbarBtnId
	public void onClick$hardBounceTbarBtnId() {
		try {
			setBouncePageSubContent(BounceCategories.BOUNCED);
		} catch (Exception e) {
			logger.error("Exception :: error occured from  setBouncePageSubContent method ",e);
		}
	} // hardBounceTbarBtnId ,

	public void onClick$softBounceTbarBtnId() {
		try {
			setBouncePageSubContent(BounceCategories.SOFT_BOUNCED);
		} catch (Exception e) {
			logger.error("Exception :: error occured from  setBouncePageSubContent method ",e);
		}
	} // softBounceTbarBtnId

	public void onClick$droppedTbarBtnId() {
		try {
			setBouncePageSubContent(BounceCategories.DROPPED);
		} catch (Exception e) {
			logger.error("Exception :: error occured from  setBouncePageSubContent method ",e);
		}
	} // droppedTbarBtnId
	
	public void onClick$blockedTbarBtnId() {
		try {
			setBouncePageSubContent(BounceCategories.BLOCKED);
		} catch (Exception e) {
			logger.error("Exception :: error occured from  setBouncePageSubContent method ",e);
		}
	} // blockedTbarBtnId
	
	public void onClick$othersTbarBtnId() {
		try {
			setBouncePageSubContent(BounceCategories.OTHERS);
		} catch (Exception e) {
			logger.error("Exception :: error occured from  setBouncePageSubContent method ",e);
		}
	} // othersTbarBtnId
	
	private Combobox exportCbId;
	private Combobox exportPendingReportCbId;
	public void onClick$exportBtnId() {
		export(exportCbId,"bounceReport");
	} // onClick$exportBtnId
	public void onClick$exportPendingReportBtnId() {
		export(exportPendingReportCbId,"PendingReport");
	}

}
