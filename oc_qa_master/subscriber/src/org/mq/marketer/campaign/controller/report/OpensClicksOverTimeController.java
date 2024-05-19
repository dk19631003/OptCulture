package org.mq.marketer.campaign.controller.report;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.CampaignReport;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.custom.MyDatebox;
import org.mq.marketer.campaign.dao.ClicksDao;
import org.mq.marketer.campaign.dao.OpensDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.Utility;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkex.zul.impl.JFreeChartEngine;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.CategoryModel;
import org.zkoss.zul.Chart;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.SimpleCategoryModel;

public class OpensClicksOverTimeController extends GenericForwardComposer {
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	private OpensDao opensDao;
	private ClicksDao clicksDao;
	private CampaignReport campaignReport;
	
	private Chart opensClicksChartId;
	private Listbox opensClicksRateLbId;
	private Session sessionScope;
	private Combobox exportCbId;
	private int tempHourShiftInt =0;
	
	boolean isFistAndSingleDay=false;
	
	public OpensClicksOverTimeController() {
		this.sessionScope = Sessions.getCurrent();
		campaignReport = 
			(CampaignReport)Sessions.getCurrent().getAttribute("campaignReport");
	}
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		// TODO Auto-generated method stub
		super.doAfterCompose(comp);
		opensClicksChartId.setEngine(new JFreeChartEngine());
		TimeZone clientTimeZone = (TimeZone)sessionScope.getAttribute("clientTimeZone");
		Calendar sentDate = campaignReport.getSentDate();
		
		tempHourShiftInt = TimeZone.getDefault().getOffset(sentDate.getTimeInMillis());
		tempHourShiftInt = tempHourShiftInt - clientTimeZone.getOffset(sentDate.getTimeInMillis());
		tempHourShiftInt = tempHourShiftInt/(1000*60*60);
		
		exportCbId.setSelectedIndex(0);
		
		CampaignReport cr = (CampaignReport)sessionScope.getAttribute("campaignReport");
    	if(cr != null){
    		try {
	    		Calendar tempCal1 = (Calendar)Utility.makeCopy(cr.getSentDate());
	    		Calendar tempCal2 = (Calendar)Utility.makeCopy(cr.getSentDate());
	    		
	    		tempCal1.set(Calendar.SECOND,tempCal1.get(Calendar.SECOND)+1);
	    		datebox1Id.setValue(tempCal1);
	    		
	    		tempCal2.set(Calendar.SECOND,tempCal2.get(Calendar.SECOND)+1);
	    		datebox2Id.setValue(tempCal2);
    		}catch(Exception e) {
    			logger.error("Exception ::" , e);
    		}
    	}
		// default settings. 
    	//drawChart(null, null);
    	consolidatedChart(null, null);
    //	prevoiusChart(null,null);
	}
	
	/*public void init(Chart opensClicksChartId,Listbox opensClicksRateLbId) {
		this.opensClicksChartId = opensClicksChartId;
		opensClicksChartId.setEngine(new JFreeChartEngine());
		this.opensClicksRateLbId = opensClicksRateLbId;
		
		TimeZone clientTimeZone = (TimeZone)sessionScope.getAttribute("clientTimeZone");
		Calendar sentDate = campaignReport.getSentDate();
		
		tempHourShiftInt = TimeZone.getDefault().getOffset(sentDate.getTimeInMillis());
		tempHourShiftInt = tempHourShiftInt - clientTimeZone.getOffset(sentDate.getTimeInMillis());
		tempHourShiftInt = tempHourShiftInt/(1000*60*60);

		drawChart(null, null);
	}*/

	public void refresh(String option, MyDatebox fromDatebox, MyDatebox toDatebox) {
		
		try {
			if(logger.isDebugEnabled()) logger.debug("--just entered--");
			Calendar clientFromDtCal = fromDatebox.getClientValue();
			Calendar clientToDtCal = toDatebox.getClientValue();
			Calendar sentDate = campaignReport.getSentDate();
			
			isFistAndSingleDay=false;
			if(option.equals("0")) {
				
				consolidatedChart(null, null);
				
			} else if(option.equals("1") || option.equals("2")) {
				
				logger.debug("Client From Date :"+clientFromDtCal);
				logger.debug("Client To   Date :"+clientToDtCal);
				Calendar serverFromDateCal = (Calendar)Utility.makeCopy(fromDatebox.getServerValue());
				logger.debug("Server From Date :"+serverFromDateCal);
				
				if(clientFromDtCal==null ) {
					MessageUtil.setMessage("Selected date should be later than Email sent date.",
							"color:red", "top");
					return;
				} 
				
				if(sentDate.get(Calendar.DATE) == serverFromDateCal.get(Calendar.DATE) && 
						sentDate.get(Calendar.MONTH) == serverFromDateCal.get(Calendar.MONTH) && 
						sentDate.get(Calendar.YEAR) == serverFromDateCal.get(Calendar.YEAR)) {
					
				}
				else if(sentDate.after(serverFromDateCal)) {
					
					MessageUtil.setMessage("Selected date should be later than Email sent date.",
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
//				logger.debug("==== "+sentDateInt+"  "+fromDateInt);
//				logger.debug("====serverFromDateCal= "+fromDatebox.getServerValue());
				
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
		MessageUtil.clearMessage();
		if(logger.isDebugEnabled())logger.debug("-- just entered --");

		try {
			
			if(opensDao == null)
				opensDao = (OpensDao)SpringUtil.getBean("opensDao");
			String startDateStr = (startDate == null)?null:startDate.toString();
			String endDateStr = (endDate == null)?null:endDate.toString(); 
			
			int stHour = 0;
			if(startDate!=null) stHour = startDate.get(Calendar.HOUR_OF_DAY);
			
			logger.debug(" Server startDateStr :"+startDateStr);
			logger.debug(" Server   endDateStr :"+endDateStr);
			logger.debug(" DATA to shift :"+stHour);

			List<Object[]> openRates = 
				opensDao.getOpenRateByCrId(campaignReport.getCrId(),startDateStr, endDateStr) ;
			
			if(logger.isDebugEnabled()) {
				logger.debug(" Open rate List size :"+openRates.size());
				
/*				for (Object[] objects : openRates) {
					for (Object obj : objects) {
						logger.debug("  "+obj);
					}
					logger.info("\n-----------");
				}
*/			} 
			
			if(clicksDao == null)
				clicksDao = (ClicksDao)SpringUtil.getBean("clicksDao");
			List<Object[]> clickRates = 
				clicksDao.getClickRateByCrId(campaignReport.getCrId(),startDateStr, endDateStr) ;
			if(logger.isDebugEnabled())logger.debug(" Click rate List size :"+clickRates.size());

			//SimpleXYModel model = new SimpleXYModel();
			CategoryModel model = new SimpleCategoryModel();

			// Do initialisation
			Integer[] xScale = new Integer[24];
			Integer[] totalOpenData = new Integer[24];
			Integer[] uniqueOpenData = new Integer[24];
			
			Integer[] totalClickData = new Integer[24];
			Integer[] uniqueClickData = new Integer[24];
			
			int openRateFirstCol=-1;
			int clickRateFirstCol=-1;
			
			for(int i=0;i<xScale.length;i++) {
				xScale[i] = i;
				
				totalOpenData[i] = 0;
				totalClickData[i] = 0;
				uniqueOpenData[i] = 0;
				uniqueClickData[i] = 0;
				
				int temp;
				
				for(Object[] obj:openRates) {
					temp = Integer.parseInt(obj[0].toString());
					// temp = (24+temp-tempHourShiftInt)%24;
					
					if(openRateFirstCol==-1 && isFistAndSingleDay) openRateFirstCol=temp;
					
					if(temp==xScale[i] && temp >=openRateFirstCol) {
						totalOpenData[i] = Integer.parseInt(obj[1].toString());
						uniqueOpenData[i] = Integer.parseInt(obj[2].toString());
						break;
					}
				}// for obj	
				
				for(Object[] obj:clickRates) {
					temp = Integer.parseInt(obj[0].toString());
				//	temp = (24+temp-tempHourShiftInt)%24;
					
					if(clickRateFirstCol==-1 && isFistAndSingleDay) clickRateFirstCol=temp;
					
					if(temp==xScale[i] && temp >=clickRateFirstCol) {
						totalClickData[i] = Integer.parseInt(obj[1].toString());
						uniqueClickData[i] = Integer.parseInt(obj[2].toString());
						break;
					}
				}// for obj
				
				model.setValue("Opens", ""+xScale[i], totalOpenData[i]);
				model.setValue("Clicks", ""+xScale[i], totalClickData[i]);

			}// for i
			
			Listitem li;
			Listcell lc;
			
			int count = opensClicksRateLbId.getItemCount();
			for(;count>0;count--){
				opensClicksRateLbId.removeItemAt(count-1);
			}
			//System.gc();
			
			for (int i = 0; i < xScale.length; i++) {
				
				if(totalOpenData[i]==0 && totalClickData[i]==0) continue;
				
				li = new Listitem();
				li.setParent(opensClicksRateLbId);
				lc = new Listcell(xScale[i]+":00 - "+((xScale[i]+1)==24?0:(xScale[i]+1)) +":00");
				lc.setParent(li);
				lc = new Listcell(""+uniqueOpenData[i]);
				lc.setParent(li);
				lc = new Listcell(""+totalOpenData[i]);
				lc.setParent(li);
				lc = new Listcell(""+uniqueClickData[i]);
				lc.setParent(li);
				lc = new Listcell(""+totalClickData[i]);
				lc.setParent(li);
			}
			
			opensClicksChartId.setModel(model);
		} catch(Exception e) {
			logger.error("** Exception while drawing the line chart ", (Throwable)e);
		}
		return opensClicksChartId;
	}
	public Chart consolidatedChart(Calendar startDate, Calendar endDate) {
		MessageUtil.clearMessage();
		if(logger.isDebugEnabled())logger.debug("-- just entered --");

		try {
			
			if(opensDao == null)
				opensDao = (OpensDao)SpringUtil.getBean("opensDao");
			String startDateStr = (startDate == null)?null:startDate.toString();
			String endDateStr = (endDate == null)?null:endDate.toString(); 
			
			int stHour = 0;
			if(startDate!=null) stHour = startDate.get(Calendar.HOUR_OF_DAY);
			
			logger.debug(" Server startDateStr :"+startDateStr);
			logger.debug(" Server   endDateStr :"+endDateStr);
			logger.debug(" DATA to shift :"+stHour);

			List<Object[]> openRates = 
				opensDao.getOpenRateByCrId(campaignReport.getCrId(),startDateStr, endDateStr) ;
			
			if(logger.isDebugEnabled()) {
				logger.debug(" Open rate List size :"+openRates.size());
				
/*				for (Object[] objects : openRates) {
					for (Object obj : objects) {
						logger.debug("  "+obj);
					}
					logger.info("\n-----------");
				}
*/			} 
			
			if(clicksDao == null)
				clicksDao = (ClicksDao)SpringUtil.getBean("clicksDao");
			List<Object[]> clickRates = 
				clicksDao.getClickRateByCrId(campaignReport.getCrId(),startDateStr, endDateStr) ;
			if(logger.isDebugEnabled())logger.debug(" Click rate List size :"+clickRates.size());
			
			Calendar sentDate = campaignReport.getSentDate();
			logger.debug(" sentDate :"+sentDate);
			TimeZone clientTimeZone = (TimeZone)sessionScope.getAttribute("clientTimeZone");
			
			Calendar tempSentDate =MyCalendar.string2Calendar(MyCalendar.calendarToString(sentDate, MyCalendar.FORMAT_DATETIME_STYEAR,clientTimeZone));
			
			int tempHourShiftInt = tempSentDate.get(Calendar.HOUR_OF_DAY);
			
			
			int strtHour = sentDate.get(Calendar.HOUR_OF_DAY);
			tempHourShiftInt = tempSentDate.get(Calendar.HOUR_OF_DAY)-sentDate.get(Calendar.HOUR_OF_DAY);
			
			logger.info("tempHourShiftInt is ==========="+tempHourShiftInt);
			logger.info("Hour according to tempSentDate calendar is ==========="+tempSentDate.get(Calendar.HOUR_OF_DAY));
			logger.info("Hour according to tempSentDate calendar is ==========="+tempSentDate.get(MyCalendar.HOUR_OF_DAY));
			logger.info("Hour according to sentDate calendar is ==========="+sentDate.get(Calendar.HOUR_OF_DAY));
			logger.info("Hour according to sentDate calendar is ==========="+sentDate.get(MyCalendar.HOUR_OF_DAY));
			

			//SimpleXYModel model = new SimpleXYModel();
			CategoryModel model = new SimpleCategoryModel();

			// Do initialisation
			Integer[] xScale = new Integer[24];
			Integer[] totalOpenData = new Integer[24];
			Integer[] uniqueOpenData = new Integer[24];
			
			Integer[] totalClickData = new Integer[24];
			Integer[] uniqueClickData = new Integer[24];
			
			int openRateFirstCol=-1;
			int clickRateFirstCol=-1;
			
			for(int i=0;i<xScale.length;i++) {
				xScale[i] = (strtHour+i)%24;
				
				totalOpenData[i] = 0;
				totalClickData[i] = 0;
				uniqueOpenData[i] = 0;
				uniqueClickData[i] = 0;
				
				int temp;
				
				for(Object[] obj:openRates) {
					temp = Integer.parseInt(obj[0].toString());
					logger.info("temp before shift===="+temp);
					//temp = (24+temp-tempHourShiftInt)%24;
					logger.info("temp after shift===="+temp);
					
					if(openRateFirstCol==-1 && isFistAndSingleDay) openRateFirstCol=temp;
					
					if(temp==xScale[i] && temp >=openRateFirstCol) {
						totalOpenData[i] = Integer.parseInt(obj[1].toString());
						uniqueOpenData[i] = Integer.parseInt(obj[2].toString());
						break;
					}
				}// for obj	
				
				for(Object[] obj:clickRates) {
					temp = Integer.parseInt(obj[0].toString());
					logger.info("temp before shift===="+temp);
					//temp = (24+temp-tempHourShiftInt)%24;
					logger.info("temp after shift===="+temp);
					
					if(clickRateFirstCol==-1 && isFistAndSingleDay) clickRateFirstCol=temp;
					
					if(temp==xScale[i] && temp >=clickRateFirstCol) {
						totalClickData[i] = Integer.parseInt(obj[1].toString());
						uniqueClickData[i] = Integer.parseInt(obj[2].toString());
						break;
					}
				}// for obj
				
				model.setValue("Opens", ""+(24+xScale[i]+tempHourShiftInt)%24, totalOpenData[i]);
				model.setValue("Clicks", ""+(24+xScale[i]+tempHourShiftInt)%24, totalClickData[i]);
				
				logger.info("x axis after adding temphourshift ==="+(24+xScale[i]+tempHourShiftInt)%24,  totalOpenData[i] );
				logger.info("y axis after adding temphourshift  ==="+(24+xScale[i]+tempHourShiftInt)%24,  totalClickData[i] );
				
				
				logger.info("x axis ==="+xScale[i],  totalOpenData[i] );
				logger.info("y axis ==="+xScale[i],  totalClickData[i] );
				

			}// for i
			
			Listitem li;
			Listcell lc;
			
			int count = opensClicksRateLbId.getItemCount();
			for(;count>0;count--){
				opensClicksRateLbId.removeItemAt(count-1);
			}
			//System.gc();
			
			for (int i = 0; i < xScale.length; i++) {
				
				if(totalOpenData[i]==0 && totalClickData[i]==0) continue;
				
				li = new Listitem();
				li.setParent(opensClicksRateLbId);
				//lc = new Listcell(xScale[i]+":00 - "+((xScale[i]+1)==24?0:(xScale[i]+1)) +":00");
				/*lc = new Listcell(xScale[i]+tempHourShiftInt
						+":00 - "+
			         ((xScale[i]+tempHourShiftInt+1)>=24?
						          (xScale[i]+tempHourShiftInt+1)-24:
						        	  (xScale[i]+tempHourShiftInt+1)) +":00");*/
				lc = new Listcell(((xScale[i]+tempHourShiftInt)>=24?(xScale[i]+tempHourShiftInt-24):(xScale[i]+tempHourShiftInt))+":00 - "+
			         ((xScale[i]+tempHourShiftInt+1)>=24?(xScale[i]+tempHourShiftInt+1)-24:(xScale[i]+tempHourShiftInt+1)) +":00");
				lc.setParent(li);
				lc = new Listcell(""+uniqueOpenData[i]);
				lc.setParent(li);
				lc = new Listcell(""+totalOpenData[i]);
				lc.setParent(li);
				lc = new Listcell(""+uniqueClickData[i]);
				lc.setParent(li);
				lc = new Listcell(""+totalClickData[i]);
				lc.setParent(li);
			}
			
			opensClicksChartId.setModel(model);
		} catch(Exception e) {
			logger.error("** Exception while drawing the line chart ", (Throwable)e);
		}
		return opensClicksChartId;
	}
	
	
	public void fileDownload(Combobox fileType) throws Exception{
		logger.debug("-- just entered --");
		int index = fileType.getSelectedIndex()==-1?0:fileType.getSelectedIndex();
		String type = (String)fileType.getItemAtIndex(index).getValue();
		if(type.equalsIgnoreCase("csv")){
		  	String s = ",";
	        StringBuffer sb = new StringBuffer();

	        for (Object head : opensClicksRateLbId.getHeads()) {
	          String h = "";
	          for (Object header : ((Listhead) head).getChildren()) {
	        	  if(h.trim().length() > 0 ) h += s; 
	            h += "\""+((Listheader) header).getLabel()+"\"" ;
	          }
	          sb.append(h + "\r\n");
	        }
	        if(opensClicksRateLbId.getItemCount()==0){
	        	try {
					//Messagebox.show("No records for opens & clicks over time found for this campaign to export.");
					MessageUtil.setMessage("No records for opens & clicks over time found for this campaign to export.", "color:blue");
				} catch (Exception e) {
				}
	        	return;
	        }
	        for (Object item : opensClicksRateLbId.getItems()) {
	          String i = "";
	          for (Object cell : ((Listitem) item).getChildren()) {
	        	  if(i.trim().length() > 0 ) i += s; 
	        	  
	            i += "\""+((Listcell) cell).getLabel()+"\"" ;
	          }
	          sb.append(i + "\r\n");
	        }
	        Filedownload.save(sb.toString().getBytes(), "text/plain", "OpensClicksReports.csv");
		}
	} // fileDownload
	
	public void onClick$exportLblId() {
		try {
			fileDownload(exportCbId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
		}
	} //onClick$exportLblId
	private Listbox timeOptionLbId;
	private MyDatebox datebox1Id, datebox2Id;
	
	public void onClick$refreshBtnId() {
		refresh((String)timeOptionLbId.getSelectedItem().getValue(),datebox1Id, datebox2Id);
	} //onClick$refreshBtnId()
	
	public void onSelect$timeOptionLbId() {
	
		try {
			selectOption((String)timeOptionLbId.getSelectedItem().getValue());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
		}
		
	} //onSelect$timeOptionLbId
	
	private Div startDivId,endDivId;
	private Label date1LblId;
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
}
