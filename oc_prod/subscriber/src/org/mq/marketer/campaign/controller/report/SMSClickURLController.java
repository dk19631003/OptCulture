package org.mq.marketer.campaign.controller.report;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.SMSCampaignReport;
import org.mq.marketer.campaign.beans.SMSCampaignUrls;
import org.mq.marketer.campaign.custom.MyDatebox;
import org.mq.marketer.campaign.dao.SMSCampaignSentDao;
import org.mq.marketer.campaign.dao.SMSCampaignUrlsDao;
import org.mq.marketer.campaign.dao.SMSClicksDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.utils.OCConstants;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkex.zul.impl.JFreeChartEngine;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.CategoryModel;
import org.zkoss.zul.Chart;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Footer;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.SimpleCategoryModel;
import org.zkoss.zul.event.PagingEvent;


public class SMSClickURLController extends GenericForwardComposer implements EventListener {
	 
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	Desktop desktop = null;
	
	private Listbox timeOptionLbId,clicksRateLbId;
	private Div startDivId , endDivId ;
	private MyDatebox startDateboxId, endDateboxId;
	private Label date1LblId ;
	private Chart clicksChartId;
	private Footer totClicksLBFId;
	
	//private Paging clicksPagingId;
	
	private Rows viewClicksRowsId;
	
	private Combobox exportCbId;
	private int tempHourShiftInt =0;
	
	boolean isFistAndSingleDay=false;
	
	public static String clickUrlColName = ""; 
	
	
	
	private Session sessionScope;
	private SMSClicksDao smsClicksDao;
	private SMSCampaignReport smsCampaignReport;
	private SMSCampaignUrlsDao smsCampaignUrlsDao;
	
	private SMSCampaignSentDao smsCampaignSentDao;
	
	
	
	
	public SMSClickURLController(){
		this.sessionScope = Sessions.getCurrent();
		smsCampaignReport = (SMSCampaignReport)Sessions.getCurrent().getAttribute("smsCampaignReport");
		
		smsCampaignUrlsDao =(SMSCampaignUrlsDao)SpringUtil.getBean("smsCampaignUrlsDao");
		
		smsClicksDao = (SMSClicksDao)SpringUtil.getBean("smsClicksDao");
		smsCampaignSentDao=(SMSCampaignSentDao)SpringUtil.getBean("smsCampaignSentDao");
		
		
	}
	
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		// TODO Auto-generated method stub
		super.doAfterCompose(comp);
		clicksChartId.setEngine(new JFreeChartEngine());
		defaultSettings();
	
    	drawChart(null, null);
		
		
		exportCbId.setSelectedIndex(0);
		
	}//doAfterCompose
	
	
	public void defaultSettings(){
		
		try {
			// default settings. 
			clicksChartId.setEngine(new JFreeChartEngine());
			TimeZone clientTimeZone = (TimeZone)sessionScope.getAttribute("clientTimeZone");
			Calendar sentDate = smsCampaignReport.getSentDate();
			
			
			tempHourShiftInt = TimeZone.getDefault().getOffset(sentDate.getTimeInMillis());
			tempHourShiftInt = tempHourShiftInt - clientTimeZone.getOffset(sentDate.getTimeInMillis());
			tempHourShiftInt = tempHourShiftInt/(1000*60*60);
			
			
			SMSCampaignReport smsCr = (SMSCampaignReport)sessionScope.getAttribute("smsCampaignReport");
			if(smsCr != null){
				try {
					Calendar tempCal1 = (Calendar)Utility.makeCopy(smsCr.getSentDate());
					Calendar tempCal2 = (Calendar)Utility.makeCopy(smsCr.getSentDate());
					
					tempCal1.set(Calendar.SECOND,tempCal1.get(Calendar.SECOND)+1);
					startDateboxId.setValue(tempCal1);
					
					tempCal2.set(Calendar.SECOND,tempCal2.get(Calendar.SECOND)+1);
					endDateboxId.setValue(tempCal2);
				}catch(Exception e) {
					logger.error("Exception ::" , e);
				}
			}
			
			if(smsCampaignReport == null){
				
				smsCampaignReport = (SMSCampaignReport)desktop.getAttribute("smsCampaignReport");
			}
			
			Long smsCrid= smsCampaignReport.getSmsCrId();
			List<SMSCampaignUrls> smsCampUrlsList = smsCampaignUrlsDao.getClickList(smsCrid);
			 
		/*	clicksPagingId.setActivePage(0);
			clicksPagingId.setTotalSize(smsCampUrlsList.size());
			clicksPagingId.addEventListener("onPaging", this);*/
			 
			getSMSClickReport(smsCampUrlsList);
			setFooter(totClicksLBFId);
			 
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception :::",e);
		}
		
	}//defaultSettings
	
	public void  getSMSClickReport( List<SMSCampaignUrls> smsCampUrlsList){
		
		try {
			/*if(smsCampaignReport == null){
				
				smsCampaignReport = (SMSCampaignReport)desktop.getAttribute("smsCampaignReport");
			}
			
			Long smsCrid= smsCampaignReport.getSmsCrId();
			
			 List<SMSCampaignUrls> smsCampUrlsList = smsCampaignUrlsDao.getClickList(smsCrid);*/
			 
			 
			 List<Object[]> smsClicksList = smsClicksDao.getClicksByCampUrlId();
			 
			 
			 
			 
			for (SMSCampaignUrls smsCampUrls : smsCampUrlsList) {
				
				
					Row clikcRow = new Row();
					
					
					/*String finalClickUrl=Constants.STRING_NILL;
					
					finalClickUrl = smsCampUrls.getOriginalUrl() != null ? smsCampUrls.getOriginalUrl() : Constants.STRING_NILL;
					
					if(smsCampUrls.getShortUrl() != null && !smsCampUrls.getShortUrl().isEmpty()){
						
						finalClickUrl +="("+smsCampUrls.getShortUrl()+")" ;
					
					}
					clikcRow.appendChild(new Label(finalClickUrl));*/
				//	String originalUrl=smsCampUrls.getOriginalUrl().trim() != null ? smsCampUrls.getOriginalUrl().trim() :Constants.STRING_NILL;
					
					String originalUrl=Constants.STRING_NILL;
					
					//String shortUrl=Constants.STRING_NILL;
					if(smsCampUrls.getTypeOfUrl().equals(OCConstants.SHORTURL_TYPE_BARCODE_TYPE_MULTIPLE) || 
							smsCampUrls.getTypeOfUrl().equals(OCConstants.SHORTURL_TYPE_BARCODE_TYPE_SINGLE)){
								
								//shortUrl +=OCConstants.SHORTURL_TYPE_BARCODE_STRING;
								
								originalUrl+=OCConstants.SHORTURL_TYPE_BARCODE_STRING;
					}else{
						
						if(smsCampUrls.getShortUrl().trim() != null && !smsCampUrls.getShortUrl().trim() .isEmpty() ){
							
							
							originalUrl+=smsCampUrls.getOriginalUrl().trim();
							//shortUrl+=smsCampUrls.getShortUrl().trim();
						}
						
					}
					
					clikcRow.appendChild(new Label(originalUrl));
					
					Label shortUrlLbl= new Label(smsCampUrls.getShortUrl());
					
					
					
					clikcRow.appendChild(shortUrlLbl);
					
					
					
					
					
					
					String uniqCountLbl="0";
					String totCountLbl="0";
					
					for (Object[] objects : smsClicksList) {
						
						if(((Long)objects[0]).longValue() == smsCampUrls.getId().longValue()){
							
							uniqCountLbl =""+objects[1].toString().trim();
							totCountLbl=""+objects[2].toString().trim();
							break;
						}
						
					}
					
					//if(uniqCountLbl.isEmpty() && totCountLbl.isEmpty())continue;
					clikcRow.appendChild(new Label(uniqCountLbl));
					
					clikcRow.appendChild(new Label(totCountLbl));
					
					clikcRow.setParent(viewClicksRowsId);
			}
			
			
		} catch (Exception e) {
			//logger.error("Exception :::",e);
			logger.error("Excepton",e);
			
		}	
	}//getSMSClickReport
	
	public void setFooter(Footer totClicksLBFId) {
		
		
			try {
				if(smsCampaignReport == null)
					smsCampaignReport = (SMSCampaignReport)desktop.getAttribute("smsCampaignReport");
				
				long crId = smsCampaignReport.getSmsCrId();
				//long count = smsCampaignUrlsDao.getClickCountByCrId(crId).longValue();
				long count=smsCampaignSentDao.findTotClicksByCrid(crId);
				logger.info("crid is "+crId+ " count is "+count);
				totClicksLBFId.setLabel(""+count);
			} catch (Exception e) {
				logger.error("Exception" ,e);
				//logger.error("Exception :::",e);
			}
		
	}
		
	public void onClick$refreshBtnId() {
		refresh((String)timeOptionLbId.getSelectedItem().getValue(),startDateboxId, endDateboxId);
	} //onClick$refreshBtnId()
	public void refresh(String option, MyDatebox fromDatebox, MyDatebox toDatebox) {
		
		try {
			if(logger.isDebugEnabled()) logger.debug("--just entered--");
			Calendar clientFromDtCal = fromDatebox.getClientValue();
			Calendar clientToDtCal = toDatebox.getClientValue();
			Calendar sentDate = smsCampaignReport.getSentDate();
			
			isFistAndSingleDay=false;
			if(option.equals("0")) {
				
				drawChart(null, null);
				
			} else if(option.equals("1") || option.equals("2")) {
				
				logger.debug("Client From Date :"+clientFromDtCal);
				logger.debug("Client To   Date :"+clientToDtCal);
				Calendar serverFromDateCal = (Calendar)Utility.makeCopy(fromDatebox.getServerValue());
				logger.debug("Server From Date :"+serverFromDateCal);
				
				if(clientFromDtCal==null ) {
					/*MessageUtil.setMessage("Selected date should be later than email sent date.",
							"color:red", "top");*/
					MessageUtil.setMessage("Selected date should be later than SMS sent date.",
							"color:red", "top");
					return;
				} 
				
				if(sentDate.get(Calendar.DATE) == serverFromDateCal.get(Calendar.DATE) && 
						sentDate.get(Calendar.MONTH) == serverFromDateCal.get(Calendar.MONTH) && 
						sentDate.get(Calendar.YEAR) == serverFromDateCal.get(Calendar.YEAR)) {
					
				}
				else if(sentDate.after(serverFromDateCal)) {
					
					/*MessageUtil.setMessage("Selected date should be later than email sent date.",
							"color:red", "top");*/
					MessageUtil.setMessage("Selected date should be later than SMS sent date.",
							"color:red", "top");
					return;
				}
				serverFromDateCal.set(Calendar.HOUR_OF_DAY, 
						serverFromDateCal.get(Calendar.HOUR_OF_DAY)-clientFromDtCal.get(Calendar.HOUR_OF_DAY));
				serverFromDateCal.set(Calendar.MINUTE, 
						serverFromDateCal.get(Calendar.MINUTE)-clientFromDtCal.get(Calendar.MINUTE));
				serverFromDateCal.set(Calendar.SECOND, 0);
				
				Calendar serverToDateCal = null;
				
				long sentDateInt = (sentDate.getTimeInMillis()/(1000*60*60*24));
				long fromDateInt = (fromDatebox.getServerValue().getTimeInMillis()/(1000*60*60*24));
//					logger.debug("==== "+sentDateInt+"  "+fromDateInt);
//					logger.debug("====serverFromDateCal= "+fromDatebox.getServerValue());
				
				if(option.equals("1")) {
					serverToDateCal = (Calendar)Utility.makeCopy(serverFromDateCal);
					serverToDateCal.set(Calendar.DATE, serverToDateCal.get(Calendar.DATE)+1);
					serverToDateCal.set(Calendar.SECOND, serverToDateCal.get(Calendar.SECOND) - 1);
					
					if(sentDateInt==fromDateInt)
						isFistAndSingleDay=true;
				}
				if(option.equals("2")) {
					serverToDateCal = (Calendar)Utility.makeCopy(toDatebox.getServerValue());
					logger.debug("Server To Date :"+serverToDateCal);
					
					if(clientFromDtCal.after(clientToDtCal)) {
						MessageUtil.setMessage("'End' date should be later than 'Start' date.", "color:red", "top");
						return;
					}

					long toDateInt = (serverToDateCal.getTimeInMillis()/(1000*60*60*24));
					
					if(sentDateInt==fromDateInt && sentDateInt==toDateInt)
						isFistAndSingleDay=true;
					
					serverToDateCal.set(Calendar.HOUR_OF_DAY, 
							23+serverToDateCal.get(Calendar.HOUR_OF_DAY)-clientToDtCal.get(Calendar.HOUR_OF_DAY));
					serverToDateCal.set(Calendar.MINUTE, 
							59+serverToDateCal.get(Calendar.MINUTE)-clientToDtCal.get(Calendar.MINUTE));
					serverToDateCal.set(Calendar.SECOND, 59);
					
				}
				drawChart(serverFromDateCal, serverToDateCal);
				
			} 
		} catch (Exception e) {
			logger.error("Exception ::" , e);
		}
	}
	public Chart drawChart(Calendar startDate, Calendar endDate) {
		try {
			MessageUtil.clearMessage();
			if(logger.isDebugEnabled())logger.debug("-- just entered --");
			
			String startDateStr = (startDate == null)?null:startDate.toString();
			String endDateStr = (endDate == null)?null:endDate.toString(); 
			
			int stHour = 0;
			if(startDate!=null) stHour = startDate.get(Calendar.HOUR_OF_DAY);
			
			if(smsClicksDao == null)
				smsClicksDao = (SMSClicksDao)SpringUtil.getBean("smsClicksDao");
			
			List<Object[]> clickRates = 
					smsClicksDao.getClickRateByCrId(smsCampaignReport.getSmsCrId(),startDateStr, endDateStr) ;
			
			CategoryModel model = new SimpleCategoryModel();
			
			Integer[] xScale = new Integer[24];
			Integer[] totalClickData = new Integer[24];
			Integer[] uniqueClickData = new Integer[24];
			
			int clickRateFirstCol=-1;
			
			
			for(int i=0;i<xScale.length;i++) {
				xScale[i] = i;
				
				totalClickData[i] = 0;
				uniqueClickData[i] = 0;
				int temp;
				
				for(Object[] obj:clickRates) {
					temp = Integer.parseInt(obj[0].toString());
					temp = (24+temp-tempHourShiftInt)%24;
					
					if(clickRateFirstCol==-1 && isFistAndSingleDay) clickRateFirstCol=temp;
					
					if(temp==xScale[i] && temp >=clickRateFirstCol) {
						totalClickData[i] = Integer.parseInt(obj[1].toString());
						uniqueClickData[i] = Integer.parseInt(obj[2].toString());
						break;
					}
				}// for obj
				
				model.setValue("Clicks", ""+xScale[i], totalClickData[i]);
			}
			
			
				Listitem li;
				Listcell lc;
				
				int count = clicksRateLbId.getItemCount();
				for(;count>0;count--){
					clicksRateLbId.removeItemAt(count-1);
				}
				//System.gc();
				
				for (int i = 0; i < xScale.length; i++) {
					
					if(totalClickData[i]==0) continue;
					
					li = new Listitem();
					li.setParent(clicksRateLbId);
					
					lc = new Listcell(xScale[i]+":00 - "+((xScale[i]+1)==24?0:(xScale[i]+1)) +":00");
					lc.setParent(li);
					
					lc = new Listcell(""+uniqueClickData[i]);
					lc.setParent(li);
					
					lc = new Listcell(""+totalClickData[i]);
					lc.setParent(li);
				}
				
				clicksChartId.setModel(model);
				
			} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception :::",e);
			return null;
		}
		return clicksChartId;
	}  

	public void onClick$exportBtnId() {
		try {
			fileDownload(exportCbId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
		}
	} //onClick$exportLblId
	
	public void fileDownload(Combobox fileType) throws Exception{
		logger.debug("-- just entered --");
		int index = fileType.getSelectedIndex()==-1?0:fileType.getSelectedIndex();
		String type = (String)fileType.getItemAtIndex(index).getValue();
		if(type.equalsIgnoreCase("csv")){
		  	String s = ",";
	        StringBuffer sb = new StringBuffer();

	        for (Object head : clicksRateLbId.getHeads()) {
	          String h = "";
	          for (Object header : ((Listhead) head).getChildren()) {
	        	  if(h.trim().length() > 0 ) h += s; 
	            h += "\""+((Listheader) header).getLabel()+"\"" ;
	          }
	          sb.append(h + "\r\n");
	        }
	        if(clicksRateLbId.getItemCount()==0){
	        	try {
					//Messagebox.show("No records for opens & clicks over time found for this campaign to export.");
					MessageUtil.setMessage("No records for clicks over time found for this SMS campaign to export.", "color:blue");
				} catch (Exception e) {
				}
	        	return;
	        }
	        for (Object item : clicksRateLbId.getItems()) {
	          String i = "";
	          for (Object cell : ((Listitem) item).getChildren()) {
	        	  if(i.trim().length() > 0 ) i += s; 
	        	  
	            i += "\""+((Listcell) cell).getLabel()+"\"" ;
	          }
	          sb.append(i + "\r\n");
	        }
	        Filedownload.save(sb.toString().getBytes(), "text/plain", "SMSClicksReports.csv");
		}
	} // fileDownload
	
	public void onSelect$timeOptionLbId() {
		
		try {
			selectOption((String)timeOptionLbId.getSelectedItem().getValue());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
		}
		
	} //onSelect$timeOptionLbId
	
	private void selectOption(String option) throws Exception{
		
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
	} //selectOption
	
	
	@Override
	public void onEvent(Event event) throws Exception {
		super.onEvent(event);
		
		if(event.getTarget() instanceof Paging) {
			
			
			Paging paging = (Paging)event.getTarget();
			
			int desiredPage = paging.getActivePage();
			
			
			
			
			PagingEvent pagingEvent = (PagingEvent) event;
			int pSize = pagingEvent.getPageable().getPageSize();
			int ofs = desiredPage * pSize;
			
			if(smsCampaignReport == null){
				
				smsCampaignReport = (SMSCampaignReport)desktop.getAttribute("smsCampaignReport");
			}
			
			Long smsCrid= smsCampaignReport.getSmsCrId();
			 List<SMSCampaignUrls> smsCampUrlsList = smsCampaignUrlsDao.getClickList(smsCrid);
			 
			getSMSClickReport(smsCampUrlsList);
			
			
			
			
		}
	}
}
