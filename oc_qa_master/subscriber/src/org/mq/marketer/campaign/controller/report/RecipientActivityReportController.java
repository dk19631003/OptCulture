package org.mq.marketer.campaign.controller.report;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.CampaignReport;
import org.mq.marketer.campaign.beans.CampaignSent;
import org.mq.marketer.campaign.beans.ShareSocialNetworkLinks;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.CampaignSentDao;
import org.mq.marketer.campaign.dao.ClicksDao;
import org.mq.marketer.campaign.dao.FarwardToFriendDao;
import org.mq.marketer.campaign.dao.OpensDao;
import org.mq.marketer.campaign.dao.ShareSocialNetworkLinksDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.optculture.data.dao.JdbcResultsetHandler;
import org.mq.optculture.utils.OCCSVWriter;
import org.zkoss.zk.ui.Component;
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

@SuppressWarnings("serial")
public class RecipientActivityReportController extends GenericForwardComposer {
	//Include cmptId;
	private Listbox genericLbId,pageSizeLbId;
	private Paging repPagingId;
	private Listhead genericLbHeaderId;
	private Window popupWindow;
	
	private TimeZone clientTimeZone = null;
	private int currentPage = 1;
	CampaignSentDao campaignSentDao;
	CampaignReport campaignReport;
	OpensDao opensDao;
	ClicksDao clicksDao;
	private FarwardToFriendDao farwardToFriendDao;
	private ShareSocialNetworkLinksDao shareSocialNetworkLinksDao;
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	public RecipientActivityReportController() {
		logger.debug("Date : "+new Date());
		campaignSentDao = (CampaignSentDao)SpringUtil.getBean("campaignSentDao");
		campaignReport = (CampaignReport)Sessions.getCurrent().getAttribute("campaignReport");
		opensDao =(OpensDao)SpringUtil.getBean("opensDao");
		clientTimeZone = (TimeZone)(Sessions.getCurrent().getAttribute("clientTimeZone"));
		farwardToFriendDao = (FarwardToFriendDao)SpringUtil.getBean("farwardToFriendDao");
		shareSocialNetworkLinksDao = (ShareSocialNetworkLinksDao)SpringUtil.getBean("shareSocialNetworkLinksDao");
	}
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		
			// TODO Auto-generated method stub
			super.doAfterCompose(comp);
			
			defaultSetting();
		
	}
	
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
	 public void changeRows()  {
			try {
				
				String selStr=pageSizeLbId.getSelectedItem().getLabel();
				
				if(repPagingId!=null){
					int pNo = Integer.parseInt(selStr);
					repPagingId.setPageSize(pNo);
					//campListPaging1.setPageSize(pNo);
					redraw((String)repPagingId.getAttribute("type"), repPagingId.getActivePage()*pNo, (byte)pNo);
					//repPagingId.setActivePage(0);
				}
			} catch (WrongValueException e) {
				logger.error("Exception while getting the contacts...",e);
				
			} catch (NumberFormatException e) {
				logger.error("Exception while gettinf the contacts...",e);
			}
		}
		
	
	private void defaultSetting() {
		try {
			
			
			repPagingId.setAttribute("type","All");
			//repPagingId.setDetailed(true);
			repPagingId.setTotalSize((int)(campaignReport.getSent()-campaignReport.getBounces()));
			repPagingId.addEventListener("onPaging", new EventListener() {
				public void onEvent(Event e) {
					Paging openPaging = (Paging) e.getTarget();
					int desiredPage = openPaging.getActivePage();
					PagingEvent pagingEvent = (PagingEvent) e;
					int pSize = pagingEvent.getPageable().getPageSize();
					int ofs = desiredPage * pSize;
					String type = (String)openPaging.getAttribute("type");
					redraw(type,ofs, (byte) pagingEvent.getPageable().getPageSize());
				}
			});
			getAllRecipients();
			
			exportCbId.setSelectedIndex(0);
		} catch (Exception e) {
			logger.error("Exception****::",e);
		}
	}
	/*
	public void init(Listbox genericLbId,Listhead genericLbHeaderId,Paging repPagingId,Window popupWindow) {
		this.genericLbId = genericLbId;
		this.genericLbHeaderId = genericLbHeaderId;
		this.repPagingId = repPagingId;
		this.popupWindow = popupWindow;
		
		repPagingId.setAttribute("type","All");		
		repPagingId.setTotalSize((int)campaignReport.getSent());
		repPagingId.addEventListener("onPaging", new EventListener() {
			public void onEvent(Event e) {
				Paging openPaging = (Paging) e.getTarget();
				int desiredPage = openPaging.getActivePage();
				PagingEvent pagingEvent = (PagingEvent) e;
				int pSize = pagingEvent.getPageable().getPageSize();
				int ofs = desiredPage * pSize;
				String type = (String)openPaging.getAttribute("type");
				redraw(type,ofs, (byte) pagingEvent.getPageable().getPageSize());
			}
		});
		getAllRecipients();
	}
	*/
	public void setReptActRepSubContent(String subContentName) {
		
		if(subContentName.equals("allrecipients")) {
			repPagingId.setAttribute("type","All");
			Components.removeAllChildren(genericLbHeaderId);
			repPagingId.setTotalSize((int)campaignReport.getSent()-campaignReport.getBounces());
			getAllRecipients();
			currentPage = 1;
			
		} else if (subContentName.equals("open")) {
			repPagingId.setAttribute("type","open");
			Components.removeAllChildren(genericLbHeaderId);
			repPagingId.setTotalSize(campaignReport.getOpens());
			getOpens();
			//repPagingId.setAttribute("type", subContentName);
			currentPage = 2;
			
		} else if (subContentName.equals("click")) {
			repPagingId.setAttribute("type","click");
			Components.removeAllChildren(genericLbHeaderId);
			repPagingId.setTotalSize(campaignReport.getClicks());
			getClicks();
			currentPage = 3;
			
		} else if (subContentName.equals("unsubscribed")) {
			repPagingId.setAttribute("type","unsubscribed");
			Components.removeAllChildren(genericLbHeaderId);
			repPagingId.setTotalSize(campaignReport.getUnsubscribes());
			getUnsubscribes();
			currentPage = 4;
			
		} else if (subContentName.equals("spam")) {
			repPagingId.setAttribute("type","spammed");
			Components.removeAllChildren(genericLbHeaderId);
			repPagingId.setTotalSize(campaignSentDao.getSpamEmailCount(campaignReport.getCrId()));
			getSpammedEmails();
			currentPage = 5;
		}
		else if (subContentName.equals("forward")) {
			repPagingId.setAttribute("type","forward");
			Components.removeAllChildren(genericLbHeaderId);
			int forwardCount =farwardToFriendDao.findByUniqEmailCount(campaignReport.getCrId().longValue());
			repPagingId.setTotalSize(forwardCount);
			getForwardEmails();
			currentPage = 6;
		}
		else if (subContentName.equals("share")) {
			repPagingId.setAttribute("type","share");
			Components.removeAllChildren(genericLbHeaderId);
			Map<String, String> emailIdwithCountMap = setShareReports(0,Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel()));
			
			if(emailIdwithCountMap != null && emailIdwithCountMap.size() > 0) {
				Set<String> KeySet = emailIdwithCountMap.keySet();
				Listitem li;
				for (String string : KeySet) {
					li = new Listitem();
					String countValStr = emailIdwithCountMap.get(string);
					logger.info("countValStr val is ::"+countValStr);
					
					li.appendChild(new Listcell(string));
					li.appendChild(new Listcell(countValStr));
					genericLbId.appendChild(li);
				}
			
				repPagingId.setTotalSize(emailIdwithCountMap.size());
			}
			
			//int shareCount = shareSocialNetworkLinksDao.findShareCount(campaignReport.getCrId().longValue());
			getShareEmails();
			currentPage = 7;
		}
	}
	
	private void getAllRecipients(){
		Listheader lh = new Listheader("Email Address");
//		lh.setWidth("50%");
		lh.setParent(genericLbHeaderId);
		lh = new Listheader("Opens");
//		lh.setWidth("25%");
		lh.setParent(genericLbHeaderId);
		lh = new Listheader("Clicks");
//		lh.setWidth("25%");
		lh.setParent(genericLbHeaderId);
		changeRows();
		//redraw("All",0,(byte)10);
	}
	
	private void getOpens(){
		Listheader lh = new Listheader("Email Address");
		lh.setWidth("35%");
		lh.setMaxlength(38);
		lh.setParent(genericLbHeaderId);
		lh = new Listheader("No. of Opens");
		lh.setWidth("15%");
		lh.setParent(genericLbHeaderId);
		lh = new Listheader("Last Time Opened");
		lh.setWidth("30%");
		lh.setParent(genericLbHeaderId);
		lh = new Listheader("");
		lh.setWidth("20%");
		lh.setParent(genericLbHeaderId);
		//redraw("open",0,(byte)10);
		changeRows();
	}
	
	private void getClicks(){
		Listheader lh = new Listheader("Email Address");
		lh.setWidth("40%");
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
		lh.setWidth("20%");
		lh.setParent(genericLbHeaderId);
		//pageSizeLbId.setSelectedIndex(1);
		//redraw("click",0,(byte)10);
		changeRows();
	}
	
	private void getUnsubscribes(){
		Listheader lh = new Listheader("Email Address");
		lh.setParent(genericLbHeaderId);
		lh.setWidth("100%");
		
		/*lh = new Listheader("No. of Sents");
		lh.setWidth("30%");*/
		lh.setParent(genericLbHeaderId);
		//pageSizeLbId.setSelectedIndex(1);
		//redraw("unsubscribed",0,(byte)10);
		changeRows();
	}
	
	private void getSpammedEmails(){
		Listheader lh = new Listheader("Email Address");
		lh.setParent(genericLbHeaderId);
		lh.setWidth("100%");
		/*pageSizeLbId.setSelectedIndex(1);
		redraw("spammed",0,(byte)10);*/
		changeRows();
	}
	private void getForwardEmails(){
		Listheader lh = new Listheader("Email Address");
		lh.setParent(genericLbHeaderId);
		lh.setWidth("70%");
		
		lh = new Listheader("No. of forwards");
		lh.setWidth("30%");
		lh.setParent(genericLbHeaderId);
		changeRows();
	}
	
	private void getShareEmails(){
		Listheader lh = new Listheader("Email Address");
		lh.setWidth("60%");
		lh.setParent(genericLbHeaderId);
		lh = new Listheader("Shared on Social Network");
		lh.setWidth("40%");
		lh.setParent(genericLbHeaderId);
		
		changeRows();
		//redraw("All",0,(byte)10);
	}
	
	private void redraw(String type,int start_index, byte _size) {
		
		int count = genericLbId.getItemCount();
		for(;count>0;count--){
			genericLbId.removeItemAt(count-1);
		}
		//System.gc();
		if(type.equalsIgnoreCase("All")) {
			//List<Object[]> list = getRecipientsBySize(start_index, _size);
			List<Object[]> list = campaignSentDao.getAllDelEmailsByCrId(campaignReport.getCrId(),start_index,_size); //(start_index, _size);
			Listitem li;
			for (Object[] obj : list) {
				li = new Listitem();
				li.setValue(obj[0]);
				li.appendChild(new Listcell(""+obj[1]));
				li.appendChild(new Listcell("" + obj[2]));
				li.appendChild(new Listcell("" + obj[3]));
				genericLbId.appendChild(li);
			}
		}else if(type.equals("open")){
			Calendar tempCal = null;
			
			List<Object[]> list = campaignSentDao.getOpenedEmailsByCrId(campaignReport.getCrId(),start_index,_size); //(start_index, _size);
			for (final Object[] obj : list) {
				
				final Listitem li = new Listitem();
				li.setValue(obj[0]);
				li.appendChild(new Listcell(obj[1].toString()));
				li.appendChild(new Listcell(obj[2].toString()));
				tempCal = (Calendar)obj[3];
				li.appendChild(new Listcell(MyCalendar.calendarToString(tempCal, null, clientTimeZone)));
				
				Listcell lc = new Listcell();
				lc.setParent(li);
				Toolbarbutton tb = new Toolbarbutton("More Details");
				tb.setParent(lc);
				tb.setStyle("color:blue;");
				tb.addEventListener("onClick",  new EventListener() {
					public void onEvent(Event e) {
						long sentId = ((Long)li.getValue());
						doPopUp(sentId,obj[1].toString(), "opens");
					}
				});
				genericLbId.appendChild(li);
			}
		}else if(type.equals("click")){
			
			List<Object[]> list = campaignSentDao.getClickedEmailsByCrId(campaignReport.getCrId(),start_index,_size); //(start_index, _size);
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
						doPopUp(sentId,obj[1].toString(), "clicks");
					}
				});
				genericLbId.appendChild(li);
			}
			
		}else if(type.equals("unsubscribed")){
			logger.debug("Just started ...");
			List<Object[]> list = campaignSentDao.getUnsubscribesByCrId(campaignReport.getCrId(),start_index,_size); //(start_index, _size);
			logger.debug("List of Unsubscribed Users :"+list);
			Listitem li;
			for (Object[] obj : list) {
				li = new Listitem();
				li.setValue(obj[0]);
				li.appendChild(new Listcell(obj[1].toString()));
				genericLbId.appendChild(li);
			}
		} else if(type.equals("spammed")){
			logger.debug("Just started ...");
			List<Object[]> list = campaignSentDao.getSpammedEmailsByCrId(campaignReport.getCrId(),start_index,_size); //(start_index, _size);
			logger.debug("List of Spammed Users :"+list);
			Listitem li;
			for (Object[] obj : list) {
				li = new Listitem();
				li.setValue(obj[0]);
				li.appendChild(new Listcell(obj[1].toString()));
				genericLbId.appendChild(li);
			}
		}
		
		else if(type.equals("forward")){
			logger.debug("Just started ...");
			List<Object[]> list = farwardToFriendDao.getForwardEmailsByCrId(campaignReport.getCrId(),start_index,_size); //(start_index, _size);
			logger.debug("List of forward Users :"+list);
			Listitem li;
			for (Object[] obj : list) {
				li = new Listitem();
				li.appendChild(new Listcell(""+obj[0]));
				li.appendChild(new Listcell("" + obj[1]));
				genericLbId.appendChild(li);
			}
		}else if(type.equals("share")){
			
			Map<String, String> emaiIdwithCountMap = setShareReports(start_index,_size);
			
			if(emaiIdwithCountMap != null && emaiIdwithCountMap.size() > 0) {
				Set<String> KeySet = emaiIdwithCountMap.keySet();
				Listitem li;
				for (String string : KeySet) {
					li = new Listitem();
					String countValStr = emaiIdwithCountMap.get(string);
					logger.info("countValStr val is ::"+countValStr);
					
					li.appendChild(new Listcell(string));
					li.appendChild(new Listcell(countValStr));
					genericLbId.appendChild(li);
				}
				
				repPagingId.setTotalSize(emaiIdwithCountMap.size());
			}
			
			
		}
		
		//System.gc();
	}
	
	
	private Map<String, String> setShareReports(int startIdx, int _size) {
		
		logger.debug("Just started ...");
		List<Long> sentIdLst = shareSocialNetworkLinksDao.findDistinctSentIdsByCrId(campaignReport.getCrId(), startIdx, _size);
		if(sentIdLst == null  || sentIdLst.size() == 0) return null;
		
		String sentIdStr = "";
		for (Long eachSentId : sentIdLst) {
				sentIdStr += sentIdStr.trim().length() == 0 ? eachSentId : ","+eachSentId;
			
		}
		
		logger.info("sentIdStr is  ::"+sentIdStr);
		//List<ShareSocialNetworkLinks> list = shareSocialNetworkLinksDao.getSharedByCrId(campaignReport.getCrId(), startIdx, _size); //(start_index, _size);
		List<ShareSocialNetworkLinks> list = shareSocialNetworkLinksDao.getSharedByCrId(campaignReport.getCrId(), sentIdStr);
		
//		if(list == null || list.size() == 0) return null;
		
		/*String sentIdStr ="";
		for (ShareSocialNetworkLinks shareSocialNetworkLinks : list) {
			if(!sentIdStr.contains(""+shareSocialNetworkLinks.getSentId())) {
				if(sentIdStr.trim().length()== 0) {
					sentIdStr = ""+shareSocialNetworkLinks.getSentId();
				}else {
					sentIdStr += ","+shareSocialNetworkLinks.getSentId();
				}
			}
		}
		logger.info(" sentIdStr is ::"+sentIdStr);*/
		List<Map<String, Object>> list1 =  campaignSentDao.findEmaiIBySentIds(sentIdStr);
		Map<String, String> emaiIdwithCountMap = new HashMap<String, String>();
		
		for (Map<String, Object> eachMap : list1) {
			String keyStr = eachMap.get("sent_id").toString();
			
			for (ShareSocialNetworkLinks eachSahreLinkObj : list) {
				
				
				if(keyStr.equals(""+eachSahreLinkObj.getSentId())) {
					
					
					String shraLinkStr = "";
					if(emaiIdwithCountMap.containsKey(eachMap.get("email_id"))) {
						shraLinkStr = emaiIdwithCountMap.get(eachMap.get("email_id"));
						
						
						
						//String[] sahrCountStrArr  = countValStr.split(":_:");
						
						if(eachSahreLinkObj.getSourceType().equals("Facebook")) {
							if(shraLinkStr.contains("Facebook")) continue;
							
							shraLinkStr = shraLinkStr.trim().length() == 0 ? "Facebook" :shraLinkStr + ", Facebook";
						}else if(eachSahreLinkObj.getSourceType().equals("Twitter")) {
							
							if(shraLinkStr.contains("Twitter")) continue;
							
							shraLinkStr = shraLinkStr.trim().length() == 0 ? "Twitter" :shraLinkStr + ", Twitter";
						}
						
					}else {
						if(eachSahreLinkObj.getSourceType().equals("Facebook")) shraLinkStr = "Facebook";
						else if(eachSahreLinkObj.getSourceType().equals("Twitter")) shraLinkStr = "Twitter";
					}
					emaiIdwithCountMap.put(eachMap.get("email_id").toString(), shraLinkStr);
					
				}
			}
			
		}
		logger.info(" >>>>>>>>>> emaiIdwithCountMap is ::"+emaiIdwithCountMap);
		return emaiIdwithCountMap;
		
		
	} // setShareReports
	
	private void doPopUp(long sentId,String emailId,String type){
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
		if(logger.isDebugEnabled())
			logger.debug("Getting the details for "+type);
		if(type.equals("opens")){
			lHeader = new Listheader("Opened timings of "+ emailId);
			lHeader.setParent(lh);
			List<Calendar> dateList = opensDao.getOpenTimeInfo(sentId);
			if(logger.isDebugEnabled())
				logger.debug("No. of opens by sent Id : "+sentId);
			Listitem li ;
			
			for (Calendar calendar : dateList) {
				li = new Listitem(
						MyCalendar.calendarToString(calendar, null, clientTimeZone));
				li.setParent(lb);
			}
			
		}else if(type.equals("clicks")){
			lHeader = new Listheader("Urls clicked by "+ emailId);
			lHeader.setParent(lh);
			lHeader = new Listheader("Date ");
			lHeader.setWidth("150px");
			lHeader.setAlign("center");
			lHeader.setParent(lh);
			
			if(clicksDao == null)
				clicksDao = (ClicksDao)SpringUtil.getBean("clicksDao");
			if(logger.isDebugEnabled())
				logger.debug("No. of clicks by sent Id : "+sentId);
			List<Object[]> clickList = clicksDao.getClickInfoBySentId(sentId);
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
	}
	
	public void export(Combobox exportCbId){
		logger.debug("-- just entered --");
		String type = exportCbId.getSelectedItem().getLabel();
		StringBuffer sb = null;
		String userName = GetUser.getUserName();
		String usersParentDirectory = (String)PropertyUtil.getPropertyValue("usersParentDirectory");
		String exportDir = usersParentDirectory + "/" + userName + "/Export/" ;
		File downloadDir = new File(exportDir);
		JdbcResultsetHandler jdbcResultsetHandler = null;
		Map<String, String> emaiIdwithCountMap = null;
		if(downloadDir.exists()){
			try {
				FileUtils.deleteDirectory(downloadDir);
				logger.debug(downloadDir.getName() + " is deleted");
			} catch (Exception e) {
				logger.error("Exception ::" , e);
				
				logger.debug(downloadDir.getName() + " is not deleted");
			}
		}
		if(!downloadDir.exists()){
			downloadDir.mkdirs();
		}
		
		if(type.contains("csv")){
			
			String name = campaignReport.getCampaignName();
			if(name.contains("/")) {
				
				name = name.replace("/", "_") ;
				
			}
			String filePath = exportDir +  "Report_" + name + "_" +
				MyCalendar.calendarToString(campaignReport.getSentDate(), MyCalendar.FORMAT_YEARTOSEC, clientTimeZone);
			File file = null;
			BufferedWriter bw = null;
			String query = Constants.STRING_NILL;
			try{
				
				if(currentPage != 7){
			switch (currentPage) {
			case 1:
				
						filePath = filePath + "_AllRecipients.csv";
						logger.debug("Download File path : " + filePath);
						bw = new BufferedWriter(new FileWriter(filePath));
						bw.write("\"Email Address\",\"Opens\",\"Clicks\"\r\n");
						query = "select email_id,opens,clicks from campaign_sent as cs where  cs.cr_id =" + campaignReport.getCrId()+" AND status in ('"+Constants.CS_STATUS_SUCCESS+"','"+Constants.CS_STATUS_UNSUBSCRIBED+"')";
						
					
				break;
			case 2:
				
					filePath = filePath + "_Opened.csv";
					logger.debug("Download File path : " + filePath);
					bw = new BufferedWriter(new FileWriter(filePath));
					bw.write("\"Email Address\",\"No. of Opens\",\"Last Time Opened\"\r\n");
					
					query = "SELECT (SELECT c.email_id FROM campaign_sent c WHERE c.cr_id=" + campaignReport.getCrId() +
							  " AND o.sent_id=c.sent_id), COUNT(o.sent_id), MAX(o.open_date) FROM opens o WHERE o.sent_id IN (SELECT cs.sent_id FROM campaign_sent cs WHERE cs.cr_id=" + campaignReport.getCrId() +
							  " AND cs.opens>0) GROUP BY o.sent_id  ORDER BY MAX(o.open_date) DESC";
					
					break;
			case 3:
				
					filePath = filePath + "_Clicked.csv";
					logger.debug("Download File path : " + filePath);
					
					bw = new BufferedWriter(new FileWriter(filePath));
					bw.write("\"Email Address\",\"No. of Clicks\",\"Last Clicked URL\"\r\n");
					query = "select cs.email_id,cs.clicks,c.click_Url from campaign_sent as cs, clicks  c where  cs.cr_id =" + campaignReport.getCrId() +" and cs.clicks>0 and cs.sent_id=c.sent_id and c.click_date=(SELECT max(ck.click_date) FROM clicks ck where ck.sent_id=cs.sent_id) group by cs.email_id  order by cs.clicks desc";
					
				break;
			case 4:
				
					filePath = filePath + "_Unsubscribed.csv";
					logger.debug("Download File path : " + filePath);
					bw = new BufferedWriter(new FileWriter(filePath));
					bw.write("\"Email Address\"\r\n");
					query = "select email_id from campaign_sent where cr_id =" + campaignReport.getCrId() +" and status='Unsubscribed'";
					
				break;
			case 5:
				
					filePath = filePath + "_Spammed.csv";
					logger.debug("Download File path : " + filePath);
					bw = new BufferedWriter(new FileWriter(filePath));
					bw.write("\"Email Address\"\r\n");
					query = "select email_id from campaign_sent where cr_id =" + campaignReport.getCrId() +" and status='spammed'";
					
				break;
			case 6:
			
					filePath = filePath + "_Forward.csv";
					logger.debug("Download File path : " + filePath);
					bw = new BufferedWriter(new FileWriter(filePath));
					bw.write("\"Email Address\",\"No. of forwards\"\r\n");
					query = "select to_email_id,COUNT(to_email_id) from farward_to_friend where cr_id =" + campaignReport.getCrId() +" GROUP BY to_email_id ";
				
				break;
						
				
			default:
				break;
				
				
				
			}
			file = new File(filePath);
			jdbcResultsetHandler = new JdbcResultsetHandler();
			jdbcResultsetHandler.executeStmt(query);
			if(jdbcResultsetHandler.totalRecordsSize()==0)
			{
				MessageUtil.setMessage("There are no records to export.", "style:red;");
				return;
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
			}//if
			else if(currentPage == 7)
			{
				filePath = filePath + "_Share.csv";
				logger.debug("Download File path : " + filePath);
				file = new File(filePath);
				bw = new BufferedWriter(new FileWriter(filePath));
				
				int count = shareSocialNetworkLinksDao.findShareCount(campaignReport.getCrId());
				if(count == 0) {
					MessageUtil.setMessage("There are no records to export.", "style:red;");
					return ;
				}
				
				bw.write("\"Email Address\",\"Shared on Social Network\"\r\n");
				int size = 1000;
				
				for (int i = 0; i < count; i+=size) {
					sb = new StringBuffer();
					emaiIdwithCountMap =  setShareReports(i,size);
					 
					if(emaiIdwithCountMap != null && emaiIdwithCountMap.size() > 0 ) {
						logger.debug("Got Map of size : " + emaiIdwithCountMap.size() + " | start index : " + i);
						Set<String> keySet = emaiIdwithCountMap.keySet();
						for (String eachKey : keySet) {
							sb.append("\"");sb.append(eachKey);sb.append("\""); sb.append(",");
							sb.append("\""); sb.append(emaiIdwithCountMap.get(eachKey)); sb.append("\"\r\n");
						}
					}
					
					bw.write(sb.toString());
										
				}
				bw.flush();
				bw.close();
				Filedownload.save(file, "text/plain");
			}
			}catch(Exception e)	{
				logger.info("Exception while writing to file, ",e);
			}finally {
				if(jdbcResultsetHandler!=null)jdbcResultsetHandler.destroy(); jdbcResultsetHandler = null;
				type = null; sb = null; userName = null; usersParentDirectory = null; exportDir = null ;
				downloadDir = null; emaiIdwithCountMap = null; filePath = null; file = null; bw = null; query = null; //System.gc();
			}
			
			
			
		}
	}
	
	public void onSelect$timeOptionLbId() {
		try {
			selectOption((String)timeOptionLbId.getSelectedItem().getValue());
		} catch (Exception e) {
			logger.error("Exception :error getting from the selectOption method ::",e);
		}
	}
	private Listbox timeOptionLbId;
	private Div startDivId,endDivId;
	private Label date1LblId;
	private Combobox exportCbId;
	
	private void selectOption(String option) throws Exception {
		
		if(option.equals("0")) {
			startDivId.setVisible(false);
			endDivId.setVisible(false);
		} else if(option.equals("1")) {
		date1LblId.setValue("Select Date");
			startDivId.setVisible(true);
			endDivId.setVisible(false);
		} else {
			date1LblId.setValue("Start Date");
			startDivId.setVisible(true);
			endDivId.setVisible(true);
		}
	}
	
	public void onClick$allRecipientstoolbarBtnId() {
		try {
			setReptActRepSubContent("allrecipients");
		} catch (Exception e) {
			logger.error("Exception :error getting from the setReptActRepSubContent method ::",e);
		}
	} // onClick$allRecipientstoolbarBtnId
	
	
	public void onClick$openedToolbarBtnId() {
		try {
			setReptActRepSubContent("open");
		} catch (Exception e) {
			logger.error("Exception :error getting from the setReptActRepSubContent method ::",e);
		}
	} // onClick$openedToolbarBtnId
	
	public void onClick$clickedToolbarBtnId() {
		try {
			setReptActRepSubContent("click");
		} catch (Exception e) {
			logger.error("Exception :error getting from the setReptActRepSubContent method ::",e);
		}
	} // onClick$clickedToolbarBtnId
	
	public void onClick$unSubscribeedToolbarBtnId() {
		try {
			setReptActRepSubContent("unsubscribed");
		} catch (Exception e) {
			logger.error("Exception :error getting from the setReptActRepSubContent method ::",e);
		}
	} // onClick$unSubscribeedToolbarBtnId
	
	public void onClick$markedAsSpamToolbarBtnId() {
		try {
			setReptActRepSubContent("spam");
		} catch (Exception e) {
			logger.error("Exception :error getting from the setReptActRepSubContent method ::",e);
		}
	} // onClick$markedAsSpamToolbarBtnId
	
	// for forward
	
	public void onClick$forwardToolbarBtnId() {
		try {
			setReptActRepSubContent("forward");
		} catch (Exception e) {
			logger.error("Exception :error getting from the setReptActRepSubContent method ::",e);
		}
	} // onClick$forwardToolbarBtnId
	
	public void onClick$shareToolbarBtnId() {
		try {
			setReptActRepSubContent("share");
		} catch (Exception e) {
			logger.error("Exception :error getting from the setReptActRepSubContent method ::",e);
		}
	} // onClick$shareToolbarBtnId
	
	public void onClick$exportBtnId() {
		try {
			export(exportCbId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
		}
	}
	
	
	
} // class
